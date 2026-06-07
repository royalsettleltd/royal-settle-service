package africa.royalsettle.thrift.service;

import africa.royalsettle.common.enums.ThriftContributionStatus;
import africa.royalsettle.common.enums.TransactionStatus;
import africa.royalsettle.common.enums.TransactionType;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.thrift.dto.*;
import africa.royalsettle.thrift.models.BankPaymentNotification;
import africa.royalsettle.thrift.models.ThriftContribution;
import africa.royalsettle.thrift.models.ThriftPlan;
import africa.royalsettle.thrift.repository.ThriftContributionRepository;
import africa.royalsettle.thrift.repository.ThriftPlanRepository;
import africa.royalsettle.transaction.model.Transaction;
import africa.royalsettle.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ThriftPlanService {
    @Autowired

    private final ThriftPlanRepository thriftPlanRepository;
    @Autowired
    private ThriftContributionRepository thriftContributionRepository;
    @Autowired

    private TransactionRepository transactionRepository;
    private final UsersRepository userRepository;
    private static final String BANK_NAME = "Royalsettle";
    private static final String ACCOUNT_NUMBER = "1234567890";

    public ThriftPlanResponse createThriftPlan(ThriftPlanRequest payload) {
//The user should be the currently logged in user when auth is ready
//        User user = userRepository.findById()
//                .orElseThrow(() -> new RuntimeException("User not found"));

        ThriftPlan plan = new ThriftPlan();
        plan.setPlanName(payload.getPlanName());
        plan.setPeriodicContribution(payload.getPeriodicAmount());
        plan.setTargetAmount(payload.getTargetAmount());
        plan.setStartDate(LocalDateTime.now());

        if (payload.getEndDate() != null) {
            plan.setEndDate(payload.getEndDate());
        }
        plan.setDescription(payload.getDescription());
        //   plan.setUser(user);
        plan.setIsCompleted(false);

        ThriftPlan savedPlan = thriftPlanRepository.save(plan);

        return mapToResponse(savedPlan);
    }


    public ThriftPlanResponse getThriftPlanById(Long planId) {

        ThriftPlan plan = thriftPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Thrift plan not found"));
        return mapToResponse(plan);
    }


    public Page<ThriftPlanResponse> getAllThriftPlans(int page, int size) {
        PageRequest request = PageRequest.of(page, size, Sort.by("startDate").descending());

        Page<ThriftPlan> plans = thriftPlanRepository.findAll(request);
        return plans.map(this::mapToResponse);
    }

    public SendThriftResponse sendThrift(SendThriftRequest request) {

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ThriftPlan plan = thriftPlanRepository.findById(request.getThriftPlanId())
                .orElseThrow(() -> new RuntimeException("Thrift plan not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(TransactionType.THRIFT_CONTRIBUTION);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setRsReference(generateUniqueReference());

        Transaction savedTransaction = transactionRepository.save(transaction);

        ThriftContribution contribution = new ThriftContribution();
        contribution.setAmount(request.getAmount());
        contribution.setContributionDate(LocalDateTime.now());
        contribution.setStatus(ThriftContributionStatus.PENDING);
        contribution.setUser(user);
        contribution.setThriftPlan(plan);
        contribution.setTransaction(savedTransaction);

        thriftContributionRepository.save(contribution);

        SendThriftResponse response = new SendThriftResponse();
        response.setMessage("Please send the exact amount to the bank account below and include the reference provided.");
        response.setRsReference(savedTransaction.getRsReference());
        response.setBankName(BANK_NAME);
        response.setAccountNumber(ACCOUNT_NUMBER);
        response.setPlanName(plan.getPlanName());

        return response;
    }

    private String generateUniqueReference() {
        return "TTXN-" + System.currentTimeMillis();
    }

    public ReconcilePaymentResponse reconcileBankPayment(BankPaymentNotification notification) {

        Transaction transaction = transactionRepository
                .findByRsReference(notification.getBankReference())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (transaction.getStatus() == TransactionStatus.SUCCESS) {
            ThriftContribution contribution = thriftContributionRepository
                    .findByTransaction(transaction)
                    .orElseThrow(() -> new RuntimeException("Contribution not found"));

            ReconcilePaymentResponse response = new ReconcilePaymentResponse();
            response.setMessage("Payment already reconciled");
            response.setTransactionStatus(transaction.getStatus().name());
            response.setContributionStatus(contribution.getStatus().name());

            return response;
        }


        // Verify amount
        if (transaction.getAmount().compareTo(notification.getAmount()) != 0) {
            throw new RuntimeException("Payment amount mismatch");
        }

        // Mark transaction SUCCESS
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        // Mark contribution SUCCESS
        ThriftContribution contribution = thriftContributionRepository
                .findByTransaction(transaction)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));
        contribution.setStatus(ThriftContributionStatus.SUCCESS);
        thriftContributionRepository.save(contribution);

        // Prepare response
        ReconcilePaymentResponse response = new ReconcilePaymentResponse();
        response.setMessage("Payment reconciled successfully");
        response.setTransactionStatus(transaction.getStatus().name());
        response.setContributionStatus(contribution.getStatus().name());

        return response;
    }


    private ThriftPlanResponse mapToResponse(ThriftPlan plan) {
        ThriftPlanResponse response = new ThriftPlanResponse();
        response.setPlanId(plan.getId());
        response.setPlanName(plan.getPlanName());
        response.setDescription(plan.getDescription());

        //response.setUserFullName(plan.getUser().getFullName());
        response.setPeriodicAmount(plan.getPeriodicContribution());
        response.setTargetAmount(plan.getTargetAmount());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setCompleted(plan.getIsCompleted());

        return response;
    }

}

//        "planId": "5aae696d-adae-4a23-88e9-2b260dbfd156",
//        "planName": "1m saving goal",
//        "periodicContribution": null,
//        "targetAmount": 1000000.00,
//        "userFullName": null,
//        "startDate": "2026-01-20T12:23:22.9002644",
//        "endDate": null,
//        "description": null,
//        "totalContributed": null,
//        "completed": false