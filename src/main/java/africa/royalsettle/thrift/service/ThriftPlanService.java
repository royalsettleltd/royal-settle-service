package africa.royalsettle.thrift.service;

import africa.royalsettle.common.enums.ThriftContributionStatus;
import africa.royalsettle.common.enums.TransactionStatus;
import africa.royalsettle.common.enums.TransactionType;
import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.security.service.CurrentUserService;
import africa.royalsettle.thrift.dto.*;
import africa.royalsettle.thrift.models.BankPaymentNotification;
import africa.royalsettle.thrift.models.ThriftContribution;
import africa.royalsettle.thrift.models.ThriftPlan;
import africa.royalsettle.thrift.repository.ThriftContributionRepository;
import africa.royalsettle.thrift.repository.ThriftPlanRepository;
import africa.royalsettle.transaction.model.Transaction;
import africa.royalsettle.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static africa.royalsettle.common.util.PageableUtil.buildPageableObject;
import static africa.royalsettle.common.util.TextUtils.generateCode;
import static africa.royalsettle.thrift.dto.ThriftPlanResponse.mapToResponse;

@Service
@RequiredArgsConstructor
public class ThriftPlanService {
    private final ThriftPlanRepository thriftPlanRepository;
    private final ThriftContributionRepository thriftContributionRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;
    private final UsersRepository userRepository;
    private static final String BANK_NAME = "Royalsettle";
    private static final String ACCOUNT_NUMBER = "1234567890";

    public ThriftPlanResponse createThriftPlan(ThriftPlanRequest payload) {
        Users currentUser = currentUserService.getCurrentUser();

        ThriftPlan plan = new ThriftPlan();
        plan.setPlanName(payload.getPlanName());
        plan.setPeriodicContribution(payload.getPeriodicAmount());
        plan.setTargetAmount(payload.getTargetAmount());
        plan.setStartDate(LocalDateTime.now());

        if (payload.getEndDate() != null) {
            plan.setEndDate(payload.getEndDate());
        }
        plan.setDescription(payload.getDescription());
        plan.setUser(currentUser);
        plan.setIsCompleted(false);

        ThriftPlan savedPlan = thriftPlanRepository.save(plan);

        return mapToResponse(savedPlan);
    }


    public ThriftPlanResponse getThriftPlanById(Long planId) {
        ThriftPlan plan = thriftPlanRepository.findById(planId)
                .orElseThrow(() -> new BadRequestException("Thrift plan not found"));
        return mapToResponse(plan);
    }


    public Page<ThriftPlanResponse> getAllThriftPlans(int page, int size) {
        Pageable request = buildPageableObject(page, size, Sort.by("startDate").descending());

        Page<ThriftPlan> plans = thriftPlanRepository.findAll(request);
        return plans.map(ThriftPlanResponse::mapToResponse);
    }

    public SendThriftResponse sendThrift(SendThriftRequest request) {

        Users user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new BadRequestException("User not found"));

        ThriftPlan plan = thriftPlanRepository.findByCode(request.getThriftCode())
                .orElseThrow(() -> new BadRequestException("Thrift plan not found"));

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
        return "TXN|".concat(generateCode());
    }

    public ReconcilePaymentResponse reconcileBankPayment(BankPaymentNotification notification) {

        Transaction transaction = transactionRepository
                .findByRsReference(notification.getBankReference())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        if (transaction.getStatus() == TransactionStatus.SUCCESS) {
            ThriftContribution contribution = thriftContributionRepository
                    .findByTransaction(transaction)
                    .orElseThrow(() -> new BadRequestException("Contribution not found"));

            ReconcilePaymentResponse response = new ReconcilePaymentResponse();
            response.setMessage("Payment already reconciled");
            response.setTransactionStatus(transaction.getStatus().name());
            response.setContributionStatus(contribution.getStatus().name());

            return response;
        }


        // Verify amount
        if (transaction.getAmount().compareTo(notification.getAmount()) != 0) {
            throw new BadRequestException("Payment amount mismatch");
        }

        // Mark transaction SUCCESS
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        // Mark contribution SUCCESS
        ThriftContribution contribution = thriftContributionRepository
                .findByTransaction(transaction)
                .orElseThrow(() -> new BadRequestException("Contribution not found"));
        contribution.setStatus(ThriftContributionStatus.SUCCESS);
        thriftContributionRepository.save(contribution);

        // Prepare response
        ReconcilePaymentResponse response = new ReconcilePaymentResponse();
        response.setMessage("Payment reconciled successfully");
        response.setTransactionStatus(transaction.getStatus().name());
        response.setContributionStatus(contribution.getStatus().name());

        return response;
    }
}