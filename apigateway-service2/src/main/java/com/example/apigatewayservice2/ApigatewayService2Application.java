package com.example.apigatewayservice2;

import org.apache.http.client.methods.HttpTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApigatewayService2Application {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayService2Application.class, args);
    }


    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();

    }
}
