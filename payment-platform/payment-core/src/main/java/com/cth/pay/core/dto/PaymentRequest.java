package com.cth.pay.core.dto;

import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;

import java.math.BigDecimal;
import java.util.Map;

public record PaymentRequest(
        String transactionId,
        BigDecimal amount,
        PaymentCurrency currency,
        PaymentProvider provider,
        String customerId,
        String customerEmail,
        String description,
        Map<String, String> metadata
) {
    public PaymentRequest {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
    }
}
