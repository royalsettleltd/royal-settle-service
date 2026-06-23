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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static africa.royalsettle.ajo.dto.AjoResponse.toResponse;
import static africa.royalsettle.common.util.PageableUtil.buildPageableObject;

@Service
@RequiredArgsConstructor
public class AjoService {

    private static final int MAX_AJO_SLOTS = 10;
    private static final String INVITE_URL = "https://royalsettle.app/join-ajo?code=";

    private final AjoRepository ajoRepository;
    private final AjoMemberRepository ajoMemberRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public AjoResponse createAjoWithInitiator(CreateAjoRequest request) {
        Users initiator = currentUserService.getCurrentUser();
        requireKyc(initiator);

        Ajo ajo = Ajo.builder()
                .name(request.getName().trim())
                .amount(request.getAmount())
                .frequency(request.getFrequency().trim())
                .duration(request.getDuration().trim())
                .status(AjoStatus.PENDING)
                .ajoCode(generateAjoCode())
                .users(initiator)
                .build();
        Ajo savedAjo = ajoRepository.save(ajo);

        AjoMember member = AjoMember.builder()
                .ajo(savedAjo)
                .users(initiator)
                .build();
        ajoMemberRepository.save(member);

        return toResponse(savedAjo);
    }

    @Transactional
    public String getAjoInvitationLink(Long ajoId) {
        Ajo ajo = ajoRepository.findById(ajoId)
                .orElseThrow(() -> new EntityNotFoundException("Ajo not found"));

        if (ajo.getAjoCode() == null) {
            ajo.setAjoCode(generateAjoCode());
        }

        return INVITE_URL.concat(ajo.getAjoCode());
    }

    @Transactional
    public String joinAjo(String code) {
        Users currentUser = currentUserService.getCurrentUser();
        requireKyc(currentUser);

        String normalizedCode = code.trim().toUpperCase();
        Ajo ajo = ajoRepository.findByAjoCodeForUpdate(normalizedCode)
                .orElseThrow(() -> new BadRequestException("Invalid Ajo code"));
        boolean alreadyAMember = ajoMemberRepository.existsByAjoAndUsers(ajo, currentUser);

        if (alreadyAMember) {
            throw new BadRequestException("User has already joined this Ajo");
        }

        long memberCount = ajoMemberRepository.countAjoMember(ajo);
        if (memberCount >= MAX_AJO_SLOTS) {
            throw new BadRequestException("Ajo has reached its maximum number of members");
        }

        AjoMember member = new AjoMember();
        member.setAjo(ajo);
        member.setUsers(currentUser);
        ajoMemberRepository.save(member);

        return "Successfully joined Ajo " + ajo.getName();
    }
//Making a change.
    @Transactional(readOnly = true)
    public Page<AjoResponse> getAllMyAjos(int pageNumber, int pageSize) {
        Pageable pageable = buildPageableObject(pageNumber, pageSize);
        Users currentUser = currentUserService.getCurrentUser();

        return ajoMemberRepository.findAjosByUser(currentUser, pageable)
                .map(AjoResponse::toResponse);
    }

    private void requireKyc(Users user) {
        if (!user.isKycVerified()) {
            throw new BadRequestException("User must complete KYC before using Ajo");
        }
    }

    private String generateAjoCode() {
        String randomCode = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "RSAJ-".concat(randomCode);
    }
}
