package com.cth.pay.api.controller;

import com.cth.pay.api.entity.TransactionEntity;
import com.cth.pay.api.repository.TransactionRepository;
import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.LoadBalancingPolicy;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.router.PaymentRouter;
import com.cth.pay.service.registry.GatewayRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Gateway Operations", description = "Endpoints for payment processing, refunds, and gateway management")
public class PaymentController {

    private final GatewayRegistryService registryService;
    private final TransactionRepository transactionRepository;

    public PaymentController(GatewayRegistryService registryService, TransactionRepository transactionRepository) {
        this.registryService = registryService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/process")
    @Operation(summary = "Process international payment request")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = registryService.processPayment(request);

        TransactionEntity entity = new TransactionEntity(
                request.transactionId(), response.providerTransactionId(),
                request.provider(), request.amount(), request.currency(),
                response.status(), request.customerId(), request.customerEmail(),
                response.handlerId(), response.message()
        );
        transactionRepository.save(entity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    @Operation(summary = "Process payment refund")
    public ResponseEntity<RefundResponse> processRefund(@RequestBody RefundRequest request) {
        RefundResponse response = registryService.processRefund(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/gateways")
    @Operation(summary = "Get list of all payment gateways and their status/worker pools")
    public ResponseEntity<?> getGateways() {
        List<Map<String, Object>> gateways = new ArrayList<>();
        for (PaymentRouter router : registryService.getAllRouters()) {
            Map<String, Object> map = new HashMap<>();
            map.put("provider", router.getProvider());
            map.put("enabled", router.isEnabled());
            map.put("loadBalancingPolicy", router.getLoadBalancingPolicy());
            map.put("handlerCount", router.getHandlerCount());

            List<Map<String, Object>> handlers = router.getHandlers().stream().map(h -> {
                Map<String, Object> hm = new HashMap<>();
                hm.put("handlerId", h.getHandlerId());
                hm.put("available", h.isAvailable());
                hm.put("activeConnections", h.getActiveConnections());
                return hm;
            }).toList();

            map.put("handlers", handlers);
            gateways.add(map);
        }
        return ResponseEntity.ok(gateways);
    }

    @PostMapping("/gateways/{provider}/status")
    @Operation(summary = "Start or Stop payment gateway")
    public ResponseEntity<?> setGatewayStatus(@PathVariable PaymentProvider provider, @RequestParam boolean active) {
        try {
            registryService.setGatewayActive(provider, active);
            return ResponseEntity.ok(Map.of("message", "Gateway " + provider + " active status set to " + active));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/gateways/{provider}/policy")
    @Operation(summary = "Configure load balancing policy for a gateway")
    public ResponseEntity<?> setGatewayPolicy(@PathVariable PaymentProvider provider, @RequestParam LoadBalancingPolicy policy) {
        try {
            registryService.setLoadBalancingPolicy(provider, policy);
            return ResponseEntity.ok(Map.of("message", "Gateway " + provider + " policy updated to " + policy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
