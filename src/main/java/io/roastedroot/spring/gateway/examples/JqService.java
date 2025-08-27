package io.roastedroot.spring.gateway.examples;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.roastedroot.jq4j.Jq;
import io.roastedroot.jq4j.JqResult;
import org.springframework.stereotype.Service;

@Service
class JqService {

    public String run(String query, String body) {
        JqResult result =
                Jq.builder()
                        .withStdin(body.getBytes(UTF_8))
                        .withArgs("-M", "--compact-output", query)
                        .run();

        if (result.success()) {
            return new String(result.stdout(), UTF_8);
        } else {
            throw new RuntimeException("failed to run jq on query: " + query + ", body: " + body);
        }
    }
}
