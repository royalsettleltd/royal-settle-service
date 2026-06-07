package africa.royalsettle.thrift.controller;
import africa.royalsettle.thrift.dto.*;
import africa.royalsettle.thrift.models.BankPaymentNotification;
import africa.royalsettle.thrift.service.ThriftPlanService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thrift")
public class ThriftController {
    private final ThriftPlanService thriftPlanService;

    public ThriftController(ThriftPlanService thriftPlanService) {
        this.thriftPlanService = thriftPlanService;
    }

    @PostMapping("/create")
    public ResponseEntity<ThriftPlanResponse> createThriftPlan(@RequestBody ThriftPlanRequest request) {
        ThriftPlanResponse response = thriftPlanService.createThriftPlan(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getPlan/{planId}")
    public ResponseEntity<ThriftPlanResponse> getThriftPlanById(@PathVariable Long planId) {
        ThriftPlanResponse response = thriftPlanService.getThriftPlanById(planId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllPlan")
    public ResponseEntity<Page<ThriftPlanResponse>> getAllThriftPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ThriftPlanResponse> response = thriftPlanService.getAllThriftPlans(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<SendThriftResponse> sendThrift(@RequestBody SendThriftRequest request) {
        SendThriftResponse response = thriftPlanService.sendThrift(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("reconcile")
    public ResponseEntity<ReconcilePaymentResponse> reconcileBankPayment(@RequestBody BankPaymentNotification notification) {

        return ResponseEntity.ok( thriftPlanService.reconcileBankPayment(notification));
    }
}
