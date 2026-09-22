package com.cth.pay.core.dto;

import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;

import java.math.BigDecimal;

public record RefundRequest(
        String originalTransactionId,
        String refundTransactionId,
        BigDecimal amount,
        PaymentCurrency currency,
        PaymentProvider provider,
        String reason
) {
    public RefundRequest {
        if (originalTransactionId == null || originalTransactionId.isBlank()) {
            throw new IllegalArgumentException("Original transaction ID is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be greater than zero");
        }
    }
}
