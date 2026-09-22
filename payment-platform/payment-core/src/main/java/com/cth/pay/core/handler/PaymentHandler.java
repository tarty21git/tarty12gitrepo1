package com.cth.pay.core.handler;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.PaymentProvider;

public interface PaymentHandler {
    String getHandlerId();
    PaymentProvider getProvider();
    boolean isAvailable();
    int getActiveConnections();
    PaymentResponse processPayment(PaymentRequest request);
    RefundResponse processRefund(RefundRequest request);
}
