package io.roastedroot.spring.gateway.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WasmGatewayFiltersApplicationTests {

    @LocalServerPort private int port;

    @Test
    void sum() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(
                Map.of(
                        "X-Sum-x",
                        Collections.singletonList("10"),
                        "X-Sum-y",
                        Collections.singletonList("16")));
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(
                        "http://localhost:" + port + "/sum",
                        HttpMethod.GET,
                        new HttpEntity<>("", headers),
                        String.class);

        assertEquals(responseEntity.getHeaders().get("X-Sum").get(0), "26");
    }

    @Test
    void helloWorld() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(Map.of("X-HelloWorld-name", List.of("foobar")));
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(
                        "http://localhost:" + port + "/hello",
                        HttpMethod.GET,
                        new HttpEntity<>("", headers),
                        String.class);

        assertEquals("Hello foobar", responseEntity.getHeaders().get("X-HelloWorld").get(0));
    }

    @Test
    void jq() {
        String body =
                "{\n"
                        + "  \"name\": \"Alice\",\n"
                        + "  \"age\": 30,\n"
                        + "  \"skills\": [\"Java\", \"Go\", \"Wasm\"]\n"
                        + "}";
        String query = "{ user: .name, firstSkill: .skills[0] }";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(Map.of("X-Jq-query", List.of(query)));
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(
                        "http://localhost:" + port + "/jq",
                        HttpMethod.PUT,
                        new HttpEntity<>(body, headers),
                        String.class);

        assertEquals("{\"user\":\"Alice\",\"firstSkill\":\"Java\"}\n", responseEntity.getBody());
    }
}
