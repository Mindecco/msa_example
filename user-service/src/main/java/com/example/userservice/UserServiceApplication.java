package com.example.userservice;

import com.example.userservice.service.Testinf;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceimpl2;
import com.netflix.discovery.converters.Auto;
import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);

    }
    // xml파일에 Bean 태그로 등록하던 방식을 Spring-boot에서는 @Bean 을 사용하여 쉽게 구현 가능
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    /*
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration auth ) throws  Exception{
        return auth.getAuthenticationManager();
    }*/

}
