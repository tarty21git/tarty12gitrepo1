package com.cth.pay.core.dto;

import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(
        String refundTransactionId,
        String originalTransactionId,
        String providerRefundId,
        PaymentProvider provider,
        BigDecimal amount,
        PaymentCurrency currency,
        TransactionStatus status,
        String message,
        Instant timestamp
) {
    public static RefundResponse success(
            String refundTransactionId,
            String originalTransactionId,
            String providerRefundId,
            PaymentProvider provider,
            BigDecimal amount,
            PaymentCurrency currency,
            String message
    ) {
        return new RefundResponse(
                refundTransactionId, originalTransactionId, providerRefundId, provider,
                amount, currency, TransactionStatus.REFUNDED, message, Instant.now()
        );
    }
}
