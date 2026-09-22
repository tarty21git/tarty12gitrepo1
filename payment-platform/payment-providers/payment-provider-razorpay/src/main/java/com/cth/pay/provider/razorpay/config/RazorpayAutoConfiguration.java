package com.cth.pay.provider.razorpay.config;

import com.cth.pay.provider.razorpay.router.RazorpayGatewayRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class RazorpayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RazorpayGatewayRouter razorpayGatewayRouter() {
        return new RazorpayGatewayRouter();
    }
}
