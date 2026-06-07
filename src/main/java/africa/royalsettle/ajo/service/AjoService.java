package africa.royalsettle.ajo.service;

import africa.royalsettle.ajo.dto.CreateAjoRequest;
import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.ajo.models.AjoMember;
import africa.royalsettle.ajo.repository.AjoMemberRepository;
import africa.royalsettle.ajo.repository.AjoRepository;
import africa.royalsettle.common.enums.AjoStatus;
import africa.royalsettle.onboarding.models.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AjoService {
    private final AjoRepository ajoRepository;
    private final AjoMemberRepository ajoMemberRepository;
    private static final int MAX_AJO_SLOT = 10;

    public Ajo createAjo(CreateAjoRequest request) {
        //KYC First before creating ajo
        Ajo ajo = new Ajo();
        ajo.setName(request.getName());
        ajo.setAmount(request.getAmount());
        ajo.setFrequency(request.getFrequency());
        ajo.setDuration(request.getDuration());
        //ajo.setStartDate(request.getStartDate()); === start date is when the number of people are
        //completed. Min of 3, max of 10.
        ajo.setStatus(AjoStatus.PENDING);
        ajo.setCreatedBy(request.getCreatedBy());

        return ajoRepository.save(ajo);
    }

    public String inviteToAjo(Long ajoId) {
        Ajo ajo = ajoRepository.findById(ajoId).orElseThrow(() -> new RuntimeException("Ajo not found!"));
        if (ajo.getCode() == null) {
            generateAjoCode();
            ajoRepository.save(ajo);
        }
        return "https://royalsettle.app/join-ajo?code=" + ajo.getCode();
    }

    public String joinAjo(String code, Users activeUser) {
        if (activeUser == null) {
            throw new RuntimeException("Please,You need to Sign Up and Log in");
        }
        if (!activeUser.isKycVerified()) {
            throw new RuntimeException("User must complete Kyc before Joining ajo");
        }

        Ajo ajo = ajoRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Code not valid"));
        boolean alreadyAMember = ajoMemberRepository.existsByAjoAndUsers(ajo, activeUser);

        if (alreadyAMember) {
            throw new RuntimeException("User Already Joined this Ajo");
        }
        long memberCount = ajoMemberRepository.countAjoMember(ajo);
        if (memberCount < MAX_AJO_SLOT) {
            AjoMember member = new AjoMember();
            member.setAjo(ajo);
            member.setUsers(activeUser);
            ajoMemberRepository.save(member);
        }
        return activeUser.getLastName() + activeUser.getLastName() + "Successfully joined Ajo" + ajo.getCode();
    }

    private String generateAjoCode() {
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RSAJ-" + random;
    }
}
