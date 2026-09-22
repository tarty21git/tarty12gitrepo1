package com.cth.pay.provider.klarna.config;

import com.cth.pay.provider.klarna.router.KlarnaGatewayRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class KlarnaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KlarnaGatewayRouter klarnaGatewayRouter() {
        return new KlarnaGatewayRouter();
    }
}
