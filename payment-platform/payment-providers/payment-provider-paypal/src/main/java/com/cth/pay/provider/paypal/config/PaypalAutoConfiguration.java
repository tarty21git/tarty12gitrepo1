package com.cth.pay.provider.paypal.config;

import com.cth.pay.provider.paypal.router.PaypalGatewayRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class PaypalAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaypalGatewayRouter paypalGatewayRouter() {
        return new PaypalGatewayRouter();
    }
}
