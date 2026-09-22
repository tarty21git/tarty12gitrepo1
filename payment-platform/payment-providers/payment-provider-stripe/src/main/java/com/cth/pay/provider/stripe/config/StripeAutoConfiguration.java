package com.cth.pay.provider.stripe.config;

import com.cth.pay.provider.stripe.router.StripeGatewayRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StripeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StripeGatewayRouter stripeGatewayRouter() {
        return new StripeGatewayRouter();
    }
}
