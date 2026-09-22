package com.cth.pay.provider.stripe.router;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.LoadBalancingPolicy;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.handler.PaymentHandler;
import com.cth.pay.core.router.PaymentRouter;
import com.cth.pay.provider.stripe.handler.StripeGatewayHandler;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StripeGatewayRouter implements PaymentRouter {

    private final List<PaymentHandler> handlers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean enabled = new AtomicBoolean(true);
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private volatile LoadBalancingPolicy loadBalancingPolicy = LoadBalancingPolicy.ROUND_ROBIN;

    public StripeGatewayRouter() {
        handlers.add(new StripeGatewayHandler("stripe-worker-1"));
        handlers.add(new StripeGatewayHandler("stripe-worker-2"));
    }

    @Override
    public PaymentProvider getProvider() { return PaymentProvider.STRIPE; }

    @Override
    public boolean isEnabled() { return enabled.get(); }

    @Override
    public void setEnabled(boolean enabled) { this.enabled.set(enabled); }

    @Override
    public List<PaymentHandler> getHandlers() { return List.copyOf(handlers); }

    @Override
    public void addHandler(PaymentHandler handler) { if (handler != null) handlers.add(handler); }

    @Override
    public boolean removeHandler(String handlerId) { return handlers.removeIf(h -> h.getHandlerId().equalsIgnoreCase(handlerId)); }

    @Override
    public int getHandlerCount() { return handlers.size(); }

    @Override
    public LoadBalancingPolicy getLoadBalancingPolicy() { return loadBalancingPolicy; }

    @Override
    public void setLoadBalancingPolicy(LoadBalancingPolicy policy) { if (policy != null) this.loadBalancingPolicy = policy; }

    @Override
    public PaymentHandler selectHandler() {
        List<PaymentHandler> availableHandlers = handlers.stream().filter(PaymentHandler::isAvailable).toList();
        if (availableHandlers.isEmpty()) throw new IllegalStateException("No available Stripe worker handlers found.");
        if (loadBalancingPolicy == LoadBalancingPolicy.LEAST_CONNECTIONS) {
            return availableHandlers.stream().min(Comparator.comparingInt(PaymentHandler::getActiveConnections)).orElse(availableHandlers.getFirst());
        }
        int index = Math.abs(roundRobinIndex.getAndIncrement() % availableHandlers.size());
        return availableHandlers.get(index);
    }

    @Override
    public PaymentResponse executePayment(PaymentRequest request) {
        if (!isEnabled()) {
            return PaymentResponse.failed(request.transactionId(), PaymentProvider.STRIPE, request.amount(), request.currency(), "NONE", "Stripe Gateway disabled.");
        }
        return selectHandler().processPayment(request);
    }

    @Override
    public RefundResponse executeRefund(RefundRequest request) {
        if (!isEnabled()) throw new IllegalStateException("Stripe Gateway disabled.");
        return selectHandler().processRefund(request);
    }
}
