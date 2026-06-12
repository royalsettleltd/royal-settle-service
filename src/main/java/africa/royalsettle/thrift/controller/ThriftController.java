package africa.royalsettle.thrift.controller;

import africa.royalsettle.thrift.dto.*;
import africa.royalsettle.thrift.models.BankPaymentNotification;
import africa.royalsettle.thrift.service.ThriftPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thrift")
@Tag(name = "Thrift", description = "Thrift plan, contribution, and reconciliation operations")
@SecurityRequirement(name = "bearerAuth")
public class ThriftController {
    private final ThriftPlanService thriftPlanService;

    public ThriftController(ThriftPlanService thriftPlanService) {
        this.thriftPlanService = thriftPlanService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create thrift plan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thrift plan created"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient authority")
    })
    public ResponseEntity<ThriftPlanResponse> createThriftPlan(@RequestBody ThriftPlanRequest request) {
        ThriftPlanResponse response = thriftPlanService.createThriftPlan(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getPlan/{planId}")
    @Operation(summary = "Get thrift plan")
    public ResponseEntity<ThriftPlanResponse> getThriftPlanById(
            @Parameter(description = "Database identifier of the thrift plan", example = "42")
            @PathVariable Long planId
    ) {
        ThriftPlanResponse response = thriftPlanService.getThriftPlanById(planId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllPlan")
    @Operation(summary = "List thrift plans")
    public ResponseEntity<Page<ThriftPlanResponse>> getAllThriftPlans(
            @Parameter(example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ThriftPlanResponse> response = thriftPlanService.getAllThriftPlans(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    @Operation(summary = "Initiate thrift contribution")
    public ResponseEntity<SendThriftResponse> sendThrift(@RequestBody SendThriftRequest request) {
        SendThriftResponse response = thriftPlanService.sendThrift(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reconcile")
    @Operation(summary = "Reconcile bank payment")
    public ResponseEntity<ReconcilePaymentResponse> reconcileBankPayment(@RequestBody BankPaymentNotification notification) {

        return ResponseEntity.ok(thriftPlanService.reconcileBankPayment(notification));
    }
}
