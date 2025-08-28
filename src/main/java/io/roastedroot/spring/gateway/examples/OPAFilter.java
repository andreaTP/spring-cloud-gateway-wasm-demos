package io.roastedroot.spring.gateway.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OPAFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(OPAFilter.class);

    private final OPAService opaService;

    public OPAFilter(OPAService opaService) {
        this.opaService = opaService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String username = exchange.getRequest().getHeaders().get("X-User").get(0);
            boolean allowed = authz(username);

            if (!allowed) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean authz(String user) {
        return opaService.authz(user);
    }
}
