package africa.royalsettle.ajo.service;

import africa.royalsettle.ajo.dto.AjoResponse;
import africa.royalsettle.ajo.dto.CreateAjoRequest;
import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.ajo.models.AjoMember;
import africa.royalsettle.ajo.repository.AjoMemberRepository;
import africa.royalsettle.ajo.repository.AjoRepository;
import africa.royalsettle.common.enums.AjoStatus;
import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.security.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjoServiceTest {

    @Mock
    private AjoRepository ajoRepository;

    @Mock
    private AjoMemberRepository ajoMemberRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AjoService ajoService;

    @Test
    void createsAjoAndAddsInitiatorAsFirstMember() {
        Users initiator = verifiedUser();
        CreateAjoRequest request = new CreateAjoRequest();
        request.setName(" December Contribution ");
        request.setAmount(new BigDecimal("50000.00"));
        request.setFrequency(" MONTHLY ");
        request.setDuration(" 10 months ");

        when(currentUserService.getCurrentUser()).thenReturn(initiator);
        when(ajoRepository.save(any(Ajo.class))).thenAnswer(invocation -> {
            Ajo ajo = invocation.getArgument(0);
            ajo.setId(42L);
            ajo.setCreatedBy("customer@example.com");
            return ajo;
        });

        AjoResponse response = ajoService.createAjoWithInitiator(request);

        ArgumentCaptor<Ajo> ajoCaptor = ArgumentCaptor.forClass(Ajo.class);
        verify(ajoRepository).save(ajoCaptor.capture());
        Ajo savedAjo = ajoCaptor.getValue();
        assertEquals("December Contribution", savedAjo.getName());
        assertEquals("MONTHLY", savedAjo.getFrequency());
        assertEquals("10 months", savedAjo.getDuration());
        assertEquals(AjoStatus.PENDING, savedAjo.getStatus());
        assertEquals(initiator, savedAjo.getUsers());
        assertTrue(savedAjo.getAjoCode().matches("RSAJ-[A-F0-9]{6}"));

        ArgumentCaptor<AjoMember> memberCaptor = ArgumentCaptor.forClass(AjoMember.class);
        verify(ajoMemberRepository).save(memberCaptor.capture());
        assertEquals(savedAjo, memberCaptor.getValue().getAjo());
        assertEquals(initiator, memberCaptor.getValue().getUsers());

        assertEquals(savedAjo.getAjoCode(), response.getAjoCode());
        assertEquals(new BigDecimal("50000.00"), response.getAmount());
    }

    @Test
    void rejectsAjoCreationWhenKycIsIncomplete() {
        Users initiator = Users.builder().kycVerified(false).build();
        when(currentUserService.getCurrentUser()).thenReturn(initiator);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ajoService.createAjoWithInitiator(new CreateAjoRequest())
        );

        assertEquals("User must complete KYC before using Ajo", exception.getMessage());
        verify(ajoRepository, never()).save(any());
        verify(ajoMemberRepository, never()).save(any());
    }

    @Test
    void joinsAjoUsingNormalizedInvitationCode() {
        Users user = verifiedUser();
        Ajo ajo = ajo();
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(ajoRepository.findByAjoCodeForUpdate("RSAJ-A1B2C3")).thenReturn(Optional.of(ajo));
        when(ajoMemberRepository.existsByAjoAndUsers(ajo, user)).thenReturn(false);
        when(ajoMemberRepository.countAjoMember(ajo)).thenReturn(4L);

        String response = ajoService.joinAjo(" rsaj-a1b2c3 ");

        assertEquals("Successfully joined Ajo December Contribution", response);
        verify(ajoMemberRepository).save(any(AjoMember.class));
    }

    @Test
    void rejectsJoinWhenAjoIsFull() {
        Users user = verifiedUser();
        Ajo ajo = ajo();
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(ajoRepository.findByAjoCodeForUpdate("RSAJ-A1B2C3")).thenReturn(Optional.of(ajo));
        when(ajoMemberRepository.existsByAjoAndUsers(ajo, user)).thenReturn(false);
        when(ajoMemberRepository.countAjoMember(ajo)).thenReturn(10L);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ajoService.joinAjo("RSAJ-A1B2C3")
        );

        assertEquals("Ajo has reached its maximum number of members", exception.getMessage());
        verify(ajoMemberRepository, never()).save(any());
    }

    @Test
    void returnsMappedPageWithoutDiscardingPaginationMetadata() {
        Users user = verifiedUser();
        Ajo ajo = ajo();
        ajo.setId(42L);
        PageRequest pageRequest = PageRequest.of(
                1,
                2,
                Sort.by(Sort.Direction.DESC, "id")
        );
        Page<Ajo> ajoPage = new PageImpl<>(List.of(ajo), pageRequest, 5);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(ajoMemberRepository.findAjosByUser(user, pageRequest)).thenReturn(ajoPage);

        Page<AjoResponse> response = ajoService.getAllMyAjos(2, 2);

        assertEquals(5, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertEquals(1, response.getNumber());
    }

    @Test
    void rejectsInvalidPagination() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ajoService.getAllMyAjos(0, 10)
        );

        assertEquals("pageNumber must be at least 1", exception.getMessage());
        verify(currentUserService, never()).getCurrentUser();
    }

    private Users verifiedUser() {
        return Users.builder()
                .username("customer@example.com")
                .kycVerified(true)
                .build();
    }

    private Ajo ajo() {
        Ajo ajo = new Ajo();
        ajo.setAjoCode("RSAJ-A1B2C3");
        ajo.setName("December Contribution");
        ajo.setAmount(new BigDecimal("50000.00"));
        ajo.setFrequency("MONTHLY");
        ajo.setDuration("10 months");
        ajo.setStatus(AjoStatus.PENDING);
        return ajo;
    }
}
