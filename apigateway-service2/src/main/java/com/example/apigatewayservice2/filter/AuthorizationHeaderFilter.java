package com.example.apigatewayservice2.filter;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env){
        super(Config.class);
        this.env= env;
    }

    //login -> token -> user (with token ) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange,chain)->{
            ServerHttpRequest request = exchange.getRequest();
           // ServerHttpResponse response = exchange.getResponse();

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange,"No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer","");

            if(!isJwtValid(jwt))
                return onError(exchange,"JWT token is not valid", HttpStatus.UNAUTHORIZED);

            return chain.filter(exchange);
            });
        }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch(Exception ex){
            returnValue = false;
        }

        if(subject == null || subject.isEmpty())
            returnValue = false;


        return  returnValue;
    }

    // Spring Cloud Gateway는 기존 MVC 방식이 아니라 WebFlux 비동기방식으로 처리되기 된다..!
    // Mono: webflux 에서 처리하는 반환값,단일이면 Mono 여러개면 Flux  , Mono,Flux -> Spring 5.0 WebFlux 에 추가된
    // 기존 MVC는 SevletHttpResponse 를 사용하는데 Webflux는 ServerHttpResponse,Request를 사용한다..!
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    ;

    public static class Config{

    }
}
