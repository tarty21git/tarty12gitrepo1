package com.cth.pay.core.strategy;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.PaymentProvider;

public interface PaymentStrategy {
    PaymentProvider getProvider();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    PaymentResponse executePayment(PaymentRequest request);
    RefundResponse executeRefund(RefundRequest request);
}
