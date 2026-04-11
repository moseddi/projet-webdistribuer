package tn.esprit.apigateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class AuthorizationHeaderFilter {

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build())
                        .build();
            }
            return chain.filter(exchange);
        };
    }
}