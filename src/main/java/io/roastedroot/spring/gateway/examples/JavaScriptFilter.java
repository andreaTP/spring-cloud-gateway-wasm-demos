package io.roastedroot.spring.gateway.examples;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class JavaScriptFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptFilter.class);

    private final JavaScriptService jsService;

    public JavaScriptFilter(JavaScriptService jsService) {
        this.jsService = jsService;
    }

    private Mono<String> extractBody(ServerWebExchange exchange) {
        Flux<DataBuffer> body = exchange.getRequest().getBody();

        return DataBufferUtils.join(body)
                .map(
                        dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer); // important!
                            return new String(bytes, UTF_8);
                        });
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String validateFn =
                    exchange.getRequest().getHeaders().get("X-JavaScript-validate").get(0);
            return extractBody(exchange)
                    .flatMap(
                            body -> {
                                LOG.info("JavaScript: {} + {}", validateFn, body);
                                String result = validate(validateFn, body);

                                exchange.getResponse()
                                        .getHeaders()
                                        .add("Content-Type", "text/plain");

                                DataBufferFactory bufferFactory =
                                        exchange.getResponse().bufferFactory();
                                DataBuffer buffer = bufferFactory.wrap(result.getBytes(UTF_8));

                                return exchange.getResponse().writeWith(Mono.just(buffer));
                            });
        };
    }

    private String validate(String validate, String body) {
        return jsService.run(validate, body);
    }
}
