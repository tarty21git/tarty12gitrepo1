package com.cth.pay.core.dto;

import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String transactionId,
        String providerTransactionId,
        PaymentProvider provider,
        BigDecimal amount,
        PaymentCurrency currency,
        TransactionStatus status,
        String responseCode,
        String message,
        String handlerId,
        Instant timestamp
) {
    public static PaymentResponse success(
            String transactionId,
            String providerTransactionId,
            PaymentProvider provider,
            BigDecimal amount,
            PaymentCurrency currency,
            String handlerId,
            String message
    ) {
        return new PaymentResponse(
                transactionId, providerTransactionId, provider, amount, currency,
                TransactionStatus.SUCCESS, "200", message, handlerId, Instant.now()
        );
    }

    public static PaymentResponse failed(
            String transactionId,
            PaymentProvider provider,
            BigDecimal amount,
            PaymentCurrency currency,
            String handlerId,
            String message
    ) {
        return new PaymentResponse(
                transactionId, null, provider, amount, currency,
                TransactionStatus.FAILED, "500", message, handlerId, Instant.now()
        );
    }
}
