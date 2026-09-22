package com.cth.pay.core;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCoreTest {

    @Test
    void testPaymentRequestValid() {
        PaymentRequest request = new PaymentRequest(
                "TX-100", new BigDecimal("150.00"), PaymentCurrency.USD,
                PaymentProvider.STRIPE, "CUST-001", "test@example.com", "Test", null
        );
        assertEquals("TX-100", request.transactionId());
        assertEquals(new BigDecimal("150.00"), request.amount());
    }

    @Test
    void testPaymentRequestInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> new PaymentRequest(
                "TX-101", BigDecimal.ZERO, PaymentCurrency.USD,
                PaymentProvider.STRIPE, "CUST-001", "test@example.com", "Invalid", null
        ));
    }
}
