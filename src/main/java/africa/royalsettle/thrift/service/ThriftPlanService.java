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

import java.time.LocalDate;
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

        ThriftPlan plan = ThriftPlan.builder()
                .planName(payload.getPlanName())
                .periodicContribution(payload.getPeriodicAmount())
                .targetAmount(payload.getTargetAmount())
                .startDate(LocalDate.now())
                .endDate(payload.getEndDate() != null ? payload.getEndDate() : null)
                .description(payload.getDescription())
                .user(currentUser)
                .isCompleted(false)
                .build();

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

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.THRIFT_CONTRIBUTION)
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING)
                .rsReference(generateUniqueReference())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        ThriftContribution contribution = ThriftContribution.builder()
                .amount(request.getAmount())
                .contributionDate(LocalDateTime.now())
                .status(ThriftContributionStatus.PENDING)
                .user(user)
                .thriftPlan(plan)
                .transaction(savedTransaction)
                .build();

        thriftContributionRepository.save(contribution);

        return SendThriftResponse.builder()
                .message("Please send the exact amount to the bank account below and include the reference provided.")
                .rsReference(savedTransaction.getRsReference())
                .bankName(BANK_NAME)
                .accountNumber(ACCOUNT_NUMBER)
                .planName(plan.getPlanName())
                .build();
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

        if (transaction.getAmount().compareTo(notification.getAmount()) != 0) {
            throw new BadRequestException("Payment amount mismatch");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        ThriftContribution contribution = thriftContributionRepository
                .findByTransaction(transaction)
                .orElseThrow(() -> new BadRequestException("Contribution not found"));
        contribution.setStatus(ThriftContributionStatus.SUCCESS);
        thriftContributionRepository.save(contribution);

        ReconcilePaymentResponse response = new ReconcilePaymentResponse();
        response.setMessage("Payment reconciled successfully");
        response.setTransactionStatus(transaction.getStatus().name());
        response.setContributionStatus(contribution.getStatus().name());

        return response;
    }
}