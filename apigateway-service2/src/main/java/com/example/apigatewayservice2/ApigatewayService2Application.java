package com.example.apigatewayservice2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class ApigatewayService2Application {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayService2Application.class, args);
    }


    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();

    }
/*

    @Primary
    @Bean
    public HttpClientProperties httpClientProperties2() {
        return new CustomHttpClientProperties();
    }

    @ConfigurationProperties(value = "spring.cloud.gateway.httpclient", ignoreInvalidFields = true)
    private static class CustomHttpClientProperties extends HttpClientProperties {

    }
*/

}
