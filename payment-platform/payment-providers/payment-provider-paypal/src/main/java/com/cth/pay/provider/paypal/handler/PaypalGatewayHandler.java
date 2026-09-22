package com.cth.pay.provider.paypal.handler;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.dto.RefundRequest;
import com.cth.pay.core.dto.RefundResponse;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.handler.PaymentHandler;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PaypalGatewayHandler implements PaymentHandler {

    private final String handlerId;
    private final AtomicBoolean available = new AtomicBoolean(true);
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public PaypalGatewayHandler(String handlerId) {
        this.handlerId = handlerId;
    }

    @Override
    public String getHandlerId() { return handlerId; }

    @Override
    public PaymentProvider getProvider() { return PaymentProvider.PAYPAL; }

    @Override
    public boolean isAvailable() { return available.get(); }

    public void setAvailable(boolean available) { this.available.set(available); }

    @Override
    public int getActiveConnections() { return activeConnections.get(); }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        activeConnections.incrementAndGet();
        try {
            if (!isAvailable()) {
                return PaymentResponse.failed(
                        request.transactionId(), PaymentProvider.PAYPAL,
                        request.amount(), request.currency(), handlerId,
                        "PayPal Worker [" + handlerId + "] is unavailable"
                );
            }
            String providerTxId = "PAYID-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
            return PaymentResponse.success(
                    request.transactionId(), providerTxId, PaymentProvider.PAYPAL,
                    request.amount(), request.currency(), handlerId,
                    "PayPal payment processed successfully by worker [" + handlerId + "]"
            );
        } finally {
            activeConnections.decrementAndGet();
        }
    }

    @Override
    public RefundResponse processRefund(RefundRequest request) {
        activeConnections.incrementAndGet();
        try {
            String providerRefundId = "REFUND-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
            return RefundResponse.success(
                    request.refundTransactionId(), request.originalTransactionId(),
                    providerRefundId, PaymentProvider.PAYPAL, request.amount(), request.currency(),
                    "PayPal refund processed successfully by worker [" + handlerId + "]"
            );
        } finally {
            activeConnections.decrementAndGet();
        }
    }
}
