package com.cth.pay.service;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.LoadBalancingPolicy;
import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.handler.PaymentHandler;
import com.cth.pay.core.router.PaymentRouter;
import com.cth.pay.service.registry.GatewayRegistryService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GatewayRegistryServiceTest {

    static class MockPaymentRouter implements PaymentRouter {
        private boolean enabled = true;
        private LoadBalancingPolicy policy = LoadBalancingPolicy.ROUND_ROBIN;

        @Override
        public PaymentProvider getProvider() { return PaymentProvider.STRIPE; }
        @Override
        public boolean isEnabled() { return enabled; }
        @Override
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        @Override
        public List<PaymentHandler> getHandlers() { return List.of(); }
        @Override
        public void addHandler(PaymentHandler handler) {}
        @Override
        public boolean removeHandler(String handlerId) { return false; }
        @Override
        public int getHandlerCount() { return 1; }
        @Override
        public LoadBalancingPolicy getLoadBalancingPolicy() { return policy; }
        @Override
        public void setLoadBalancingPolicy(LoadBalancingPolicy policy) { this.policy = policy; }
        @Override
        public PaymentHandler selectHandler() { return null; }
        @Override
        public PaymentResponse executePayment(PaymentRequest request) {
            return PaymentResponse.success(request.transactionId(), "MOCK-123", request.provider(), request.amount(), request.currency(), "handler-1", "OK");
        }
        @Override
        public RefundResponse executeRefund(RefundRequest request) {
            return RefundResponse.success(request.refundTransactionId(), request.originalTransactionId(), "RFND-123", request.provider(), request.amount(), request.currency(), "OK");
        }
    }

    @Test
    void testRegistryService() {
        MockPaymentRouter router = new MockPaymentRouter();
        GatewayRegistryService registry = new GatewayRegistryService(
                new org.springframework.beans.factory.ObjectProvider<>() {
                    @Override
                    public List<PaymentRouter> getObject(Object... args) { return List.of(router); }
                    @Override
                    public List<PaymentRouter> getIfAvailable() { return List.of(router); }
                    @Override
                    public List<PaymentRouter> getIfUnique() { return List.of(router); }
                    @Override
                    public List<PaymentRouter> getObject() { return List.of(router); }
                }
        );

        assertTrue(registry.isGatewayActive(PaymentProvider.STRIPE));
    }
}
