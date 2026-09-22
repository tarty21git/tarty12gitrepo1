package com.cth.pay.core.router;

import com.cth.pay.core.enums.LoadBalancingPolicy;
import com.cth.pay.core.handler.PaymentHandler;
import com.cth.pay.core.strategy.PaymentStrategy;

import java.util.List;

public interface PaymentRouter extends PaymentStrategy {
    List<PaymentHandler> getHandlers();
    void addHandler(PaymentHandler handler);
    boolean removeHandler(String handlerId);
    int getHandlerCount();
    LoadBalancingPolicy getLoadBalancingPolicy();
    void setLoadBalancingPolicy(LoadBalancingPolicy policy);
    PaymentHandler selectHandler();
}
