package africa.royalsettle.thrift.service;

import africa.royalsettle.common.enums.ThriftContributionStatus;
import africa.royalsettle.common.enums.TransactionStatus;
import africa.royalsettle.common.enums.TransactionType;
import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.security.service.CurrentUserService;
import africa.royalsettle.thrift.dto.ReconcilePaymentResponse;
import africa.royalsettle.thrift.dto.SendThriftRequest;
import africa.royalsettle.thrift.dto.SendThriftResponse;
import africa.royalsettle.thrift.dto.ThriftPlanRequest;
import africa.royalsettle.thrift.dto.ThriftPlanResponse;
import africa.royalsettle.thrift.models.BankPaymentNotification;
import africa.royalsettle.thrift.models.ThriftContribution;
import africa.royalsettle.thrift.models.ThriftPlan;
import africa.royalsettle.thrift.repository.ThriftContributionRepository;
import africa.royalsettle.thrift.repository.ThriftPlanRepository;
import africa.royalsettle.transaction.model.Transaction;
import africa.royalsettle.transaction.repository.TransactionRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThriftPlanServiceTest {

    @Mock
    private ThriftPlanRepository thriftPlanRepository;

    @Mock
    private ThriftContributionRepository thriftContributionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private ThriftPlanService thriftPlanService;

    @Test
    void createsThriftPlanForCurrentUser() {
        Users user = user();
        ThriftPlanRequest request = planRequest();
        LocalDate beforeCreation = LocalDate.now();
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(thriftPlanRepository.save(any(ThriftPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ThriftPlanResponse response = thriftPlanService.createThriftPlan(request);

        ArgumentCaptor<ThriftPlan> planCaptor = ArgumentCaptor.forClass(ThriftPlan.class);
        verify(thriftPlanRepository).save(planCaptor.capture());
        ThriftPlan savedPlan = planCaptor.getValue();
        assertEquals("School Fees", savedPlan.getPlanName());
        assertEquals(new BigDecimal("25000.00"), savedPlan.getPeriodicContribution());
        assertEquals(new BigDecimal("300000.00"), savedPlan.getTargetAmount());
        assertFalse(savedPlan.getStartDate().isBefore(beforeCreation));
        assertEquals(LocalDate.of(2027, 6, 30), savedPlan.getEndDate());
        assertEquals("Monthly savings for school fees", savedPlan.getDescription());
        assertSame(user, savedPlan.getUser());
        assertFalse(savedPlan.getIsCompleted());

        assertEquals("School Fees", response.getPlanName());
        assertEquals("John Doe", response.getUserFullName());
        assertEquals(savedPlan.getStartDate(), response.getStartDate());
    }

    @Test
    void getsThriftPlanById() {
        ThriftPlan plan = plan();
        when(thriftPlanRepository.findById(42L)).thenReturn(Optional.of(plan));

        ThriftPlanResponse response = thriftPlanService.getThriftPlanById(42L);

        assertEquals(plan.getCode(), response.getPlanCode());
        assertEquals("School Fees", response.getPlanName());
        assertEquals("John Doe", response.getUserFullName());
    }

    @Test
    void rejectsUnknownThriftPlanId() {
        when(thriftPlanRepository.findById(42L)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.getThriftPlanById(42L)
        );

        assertEquals("Thrift plan not found", exception.getMessage());
    }

    @Test
    void returnsMappedPageWithRequestedSorting() {
        ThriftPlan plan = plan();
        PageRequest pageable = PageRequest.of(
                1,
                2,
                Sort.by("startDate").descending()
        );
        Page<ThriftPlan> plans = new PageImpl<>(List.of(plan), pageable, 5);
        when(thriftPlanRepository.findAll(pageable)).thenReturn(plans);

        Page<ThriftPlanResponse> response = thriftPlanService.getAllThriftPlans(2, 2);

        assertEquals(5, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertEquals(1, response.getNumber());
        assertEquals("School Fees", response.getContent().get(0).getPlanName());
    }

    @Test
    void rejectsInvalidPaginationBeforeRepositoryLookup() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.getAllThriftPlans(0, 10)
        );

        assertEquals("pageNumber must be at least 1", exception.getMessage());
        verify(thriftPlanRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void initiatesThriftContributionAndReturnsBankInstructions() {
        Users user = user();
        ThriftPlan plan = plan();
        SendThriftRequest request = sendRequest();
        when(userRepository.findByCode("USER-CODE")).thenReturn(Optional.of(user));
        when(thriftPlanRepository.findByCode("THRIFT-CODE")).thenReturn(Optional.of(plan));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeSend = LocalDateTime.now();
        SendThriftResponse response = thriftPlanService.sendThrift(request);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction transaction = transactionCaptor.getValue();
        assertSame(user, transaction.getUser());
        assertSame(plan, transaction.getThriftPlan());
        assertEquals(TransactionType.THRIFT_CONTRIBUTION, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(new BigDecimal("25000.00"), transaction.getAmount());
        assertTrue(transaction.getRsReference().matches("TXN\\|\\d{17}[A-F0-9]{8}"));

        ArgumentCaptor<ThriftContribution> contributionCaptor =
                ArgumentCaptor.forClass(ThriftContribution.class);
        verify(thriftContributionRepository).save(contributionCaptor.capture());
        ThriftContribution contribution = contributionCaptor.getValue();
        assertSame(user, contribution.getUser());
        assertSame(plan, contribution.getThriftPlan());
        assertSame(transaction, contribution.getTransaction());
        assertEquals(ThriftContributionStatus.PENDING, contribution.getStatus());
        assertFalse(contribution.getContributionDate().isBefore(beforeSend));

        assertEquals(transaction.getRsReference(), response.getRsReference());
        assertEquals("Royalsettle", response.getBankName());
        assertEquals("1234567890", response.getAccountNumber());
        assertEquals("School Fees", response.getPlanName());
    }

    @Test
    void rejectsContributionForUnknownUser() {
        SendThriftRequest request = sendRequest();
        when(userRepository.findByCode("USER-CODE")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.sendThrift(request)
        );

        assertEquals("User not found", exception.getMessage());
        verify(thriftPlanRepository, never()).findByCode(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void rejectsContributionForUnknownPlan() {
        SendThriftRequest request = sendRequest();
        when(userRepository.findByCode("USER-CODE")).thenReturn(Optional.of(user()));
        when(thriftPlanRepository.findByCode("THRIFT-CODE")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.sendThrift(request)
        );

        assertEquals("Thrift plan not found", exception.getMessage());
        verify(transactionRepository, never()).save(any());
        verify(thriftContributionRepository, never()).save(any());
    }

    @Test
    void reconcilesMatchingPayment() {
        Transaction transaction = transaction(TransactionStatus.PENDING);
        ThriftContribution contribution = contribution(transaction, ThriftContributionStatus.PENDING);
        BankPaymentNotification notification = notification("TXN|REFERENCE", "25000.00");
        when(transactionRepository.findByRsReference("TXN|REFERENCE"))
                .thenReturn(Optional.of(transaction));
        when(thriftContributionRepository.findByTransaction(transaction))
                .thenReturn(Optional.of(contribution));

        ReconcilePaymentResponse response =
                thriftPlanService.reconcileBankPayment(notification);

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(ThriftContributionStatus.SUCCESS, contribution.getStatus());
        verify(transactionRepository).save(transaction);
        verify(thriftContributionRepository).save(contribution);
        assertEquals("Payment reconciled successfully", response.getMessage());
        assertEquals("SUCCESS", response.getTransactionStatus());
        assertEquals("SUCCESS", response.getContributionStatus());
    }

    @Test
    void returnsExistingStatusesForAlreadyReconciledPayment() {
        Transaction transaction = transaction(TransactionStatus.SUCCESS);
        ThriftContribution contribution = contribution(transaction, ThriftContributionStatus.SUCCESS);
        BankPaymentNotification notification = notification("TXN|REFERENCE", "25000.00");
        when(transactionRepository.findByRsReference("TXN|REFERENCE"))
                .thenReturn(Optional.of(transaction));
        when(thriftContributionRepository.findByTransaction(transaction))
                .thenReturn(Optional.of(contribution));

        ReconcilePaymentResponse response =
                thriftPlanService.reconcileBankPayment(notification);

        assertEquals("Payment already reconciled", response.getMessage());
        assertEquals("SUCCESS", response.getTransactionStatus());
        assertEquals("SUCCESS", response.getContributionStatus());
        verify(transactionRepository, never()).save(any());
        verify(thriftContributionRepository, never()).save(any());
    }

    @Test
    void rejectsPaymentAmountMismatch() {
        Transaction transaction = transaction(TransactionStatus.PENDING);
        BankPaymentNotification notification = notification("TXN|REFERENCE", "24000.00");
        when(transactionRepository.findByRsReference("TXN|REFERENCE"))
                .thenReturn(Optional.of(transaction));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.reconcileBankPayment(notification)
        );

        assertEquals("Payment amount mismatch", exception.getMessage());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        verify(transactionRepository, never()).save(any());
        verify(thriftContributionRepository, never()).findByTransaction(any());
    }

    @Test
    void rejectsUnknownTransactionDuringReconciliation() {
        BankPaymentNotification notification = notification("UNKNOWN", "25000.00");
        when(transactionRepository.findByRsReference("UNKNOWN")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.reconcileBankPayment(notification)
        );

        assertEquals("Transaction not found", exception.getMessage());
        verify(thriftContributionRepository, never()).findByTransaction(any());
    }

    @Test
    void rejectsReconciliationWhenContributionIsMissing() {
        Transaction transaction = transaction(TransactionStatus.SUCCESS);
        BankPaymentNotification notification = notification("TXN|REFERENCE", "25000.00");
        when(transactionRepository.findByRsReference("TXN|REFERENCE"))
                .thenReturn(Optional.of(transaction));
        when(thriftContributionRepository.findByTransaction(transaction))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> thriftPlanService.reconcileBankPayment(notification)
        );

        assertEquals("Contribution not found", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    private Users user() {
        Users user = Users.builder()
                .username("john@example.com")
                .fullName("John Doe")
                .build();
        user.setCode("USER-CODE");
        return user;
    }

    private ThriftPlanRequest planRequest() {
        ThriftPlanRequest request = new ThriftPlanRequest();
        request.setPlanName("School Fees");
        request.setPeriodicAmount(new BigDecimal("25000.00"));
        request.setTargetAmount(new BigDecimal("300000.00"));
        request.setEndDate(LocalDate.of(2027, 6, 30));
        request.setDescription("Monthly savings for school fees");
        return request;
    }

    private ThriftPlan plan() {
        return ThriftPlan.builder()
                .planName("School Fees")
                .periodicContribution(new BigDecimal("25000.00"))
                .targetAmount(new BigDecimal("300000.00"))
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2027, 6, 30))
                .description("Monthly savings for school fees")
                .user(user())
                .isCompleted(false)
                .build();
    }

    private SendThriftRequest sendRequest() {
        return new SendThriftRequest(
                "USER-CODE",
                "THRIFT-CODE",
                new BigDecimal("25000.00"),
                LocalDateTime.now()
        );
    }

    private Transaction transaction(TransactionStatus status) {
        return Transaction.builder()
                .user(user())
                .thriftPlan(plan())
                .type(TransactionType.THRIFT_CONTRIBUTION)
                .amount(new BigDecimal("25000.00"))
                .status(status)
                .rsReference("TXN|REFERENCE")
                .build();
    }

    private ThriftContribution contribution(
            Transaction transaction,
            ThriftContributionStatus status
    ) {
        return ThriftContribution.builder()
                .amount(transaction.getAmount())
                .contributionDate(LocalDateTime.now())
                .status(status)
                .user(transaction.getUser())
                .thriftPlan(transaction.getThriftPlan())
                .transaction(transaction)
                .build();
    }

    private BankPaymentNotification notification(String reference, String amount) {
        return BankPaymentNotification.builder()
                .bankReference(reference)
                .amount(new BigDecimal(amount))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
