package com.cth.pay.provider.stripe;

import com.cth.pay.core.dto.PaymentRequest;
import com.cth.pay.core.dto.PaymentResponse;
import com.cth.pay.core.enums.PaymentCurrency;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.enums.TransactionStatus;
import com.cth.pay.provider.stripe.router.StripeGatewayRouter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StripeProviderTest {

    @Test
    void testStripeRouterExecution() {
        StripeGatewayRouter router = new StripeGatewayRouter();
        assertTrue(router.isEnabled());
        assertEquals(2, router.getHandlerCount());

        PaymentRequest request = new PaymentRequest(
                "TX-STRIPE-1", new BigDecimal("99.99"), PaymentCurrency.USD,
                PaymentProvider.STRIPE, "CUST-1", "stripe@example.com", "Stripe Checkout", null
        );

        PaymentResponse response = router.executePayment(request);
        assertEquals(TransactionStatus.SUCCESS, response.status());
    }
}
