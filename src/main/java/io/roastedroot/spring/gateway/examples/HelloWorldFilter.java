package io.roastedroot.spring.gateway.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class HelloWorldFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldFilter.class);

    private final HelloWorldService helloWorldService;

    public HelloWorldFilter(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String name = exchange.getRequest().getHeaders().get("X-HelloWorld-name").get(0);
            LOG.info("Hello World: {}", name);
            String result = hello(name);
            LOG.info("Hello World result: {}", result);
            return chain.filter(exchange)
                    .then(
                            Mono.fromRunnable(
                                    () -> {
                                        exchange.getResponse()
                                                .getHeaders()
                                                .add("X-HelloWorld", result);
                                    }));
        };
    }

    private String hello(String name) {
        return helloWorldService.helloWorld(name);
    }
}
