package com.app.investment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.tinkoff.piapi.core.InvestApi;

@EnableAsync
@Configuration
public class ApplicationConfig {

    @Bean
    public InvestApi investApi() {
        String token = System.getenv("token");
        return InvestApi.create(token);
    }
}
