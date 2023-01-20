package com.example.userservice.security;

import com.example.userservice.service.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.server.authorization.IpAddressReactiveAuthorizationManager.hasIpAddress;


@Configuration
@EnableWebSecurity
public class WebSecurity {

    private String IP_ADDRESS = "127.0.0.1";
    private UserService userService;
    private Environment env;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    /*
    @Autowired
    public void setBCryptPasswordEncoder(@Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }*/

    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setEnv(@Lazy Environment env) {
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = getAuthenticationManager(http);

        http.csrf().disable();
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/**"))
                .access(hasIpAddress(IP_ADDRESS))
                .and()
                .authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/actuator/**"))
                .permitAll()
                .and()
                .authenticationManager(authenticationManager)
                .addFilter(getAuthenticationFilter(authenticationManager))

        ;
        http.headers().frameOptions().disable();
        return http.build();
    }

    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String ipAddress) {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress);
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(ipAddressMatcher.matches(request));
        };
    }

    private AuthenticationManager getAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        return builder.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, userService, env);
    }

/*
    String IP_ADDRESS = "10.1.31.109";
    BCryptPasswordEncoder bCryptPasswordEncoder;
    AuthenticationConfiguration authenticationConfiguration;
    UserService userService;

    @Autowired
    public WebSecurity( UserService userService,BCryptPasswordEncoder bCryptPasswordEncoder,AuthenticationConfiguration authenticationConfiguration ) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {  // 권한(Authentication)

        http
                .authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/**"))
                .access(hasIpAddress(IP_ADDRESS))
                .and()
                .authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/user-service/h2-console/**"))
                .permitAll()
                .and()
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .and()
                .csrf().disable()

        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return this.authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;
    }


    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String ipAddress) {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress);
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(ipAddressMatcher.matches(request));
        };
    }


    @Bean
    public AuthenticationFilter getAuthenticationFiler() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }

  */
}
