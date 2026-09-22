package com.cth.pay.service.registry;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.LoadBalancingPolicy;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.handler.PaymentHandler;
import com.cth.pay.core.router.PaymentRouter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GatewayRegistryService {

    private final Map<PaymentProvider, PaymentRouter> routerRegistry = new ConcurrentHashMap<>();

    public GatewayRegistryService(ObjectProvider<List<PaymentRouter>> routersProvider) {
        List<PaymentRouter> routers = routersProvider.getIfAvailable(Collections::emptyList);
        for (PaymentRouter router : routers) {
            registerRouter(router);
        }
    }

    public void registerRouter(PaymentRouter router) {
        if (router != null && router.getProvider() != null) {
            routerRegistry.put(router.getProvider(), router);
        }
    }

    public Optional<PaymentRouter> getRouter(PaymentProvider provider) {
        return Optional.ofNullable(routerRegistry.get(provider));
    }

    public List<PaymentRouter> getAllRouters() {
        return new ArrayList<>(routerRegistry.values());
    }

    public boolean isGatewayActive(PaymentProvider provider) {
        PaymentRouter router = routerRegistry.get(provider);
        return router != null && router.isEnabled();
    }

    public void setGatewayActive(PaymentProvider provider, boolean active) {
        PaymentRouter router = routerRegistry.get(provider);
        if (router == null) {
            throw new IllegalArgumentException("Payment Provider [" + provider + "] is not registered.");
        }
        router.setEnabled(active);
    }

    public void setLoadBalancingPolicy(PaymentProvider provider, LoadBalancingPolicy policy) {
        PaymentRouter router = routerRegistry.get(provider);
        if (router == null) {
            throw new IllegalArgumentException("Payment Provider [" + provider + "] is not registered.");
        }
        router.setLoadBalancingPolicy(policy);
    }

    public void addWorkerHandler(PaymentProvider provider, PaymentHandler handler) {
        PaymentRouter router = routerRegistry.get(provider);
        if (router == null) {
            throw new IllegalArgumentException("Payment Provider [" + provider + "] is not registered.");
        }
        router.addHandler(handler);
    }

    public boolean removeWorkerHandler(PaymentProvider provider, String handlerId) {
        PaymentRouter router = routerRegistry.get(provider);
        if (router == null) {
            throw new IllegalArgumentException("Payment Provider [" + provider + "] is not registered.");
        }
        return router.removeHandler(handlerId);
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentRouter router = routerRegistry.get(request.provider());
        if (router == null) {
            return PaymentResponse.failed(
                    request.transactionId(), request.provider(), request.amount(), request.currency(),
                    "NONE", "No payment router registered for provider: " + request.provider()
            );
        }
        return router.executePayment(request);
    }

    public RefundResponse processRefund(RefundRequest request) {
        PaymentRouter router = routerRegistry.get(request.provider());
        if (router == null) {
            throw new IllegalArgumentException("No payment router registered for provider: " + request.provider());
        }
        return router.executeRefund(request);
    }
}
