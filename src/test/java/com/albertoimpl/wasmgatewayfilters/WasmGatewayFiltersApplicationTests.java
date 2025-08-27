package com.albertoimpl.wasmgatewayfilters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
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
    void wasmFilterResponds() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(
                Map.of(
                        "X-CustomSum-x",
                        Collections.singletonList("10"),
                        "X-CustomSum-y",
                        Collections.singletonList("16")));
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(
                        "http://localhost:" + port + "/sum",
                        HttpMethod.GET,
                        new HttpEntity<>("", headers),
                        String.class);

        assertEquals(responseEntity.getHeaders().get("X-CustomSum").get(0), "26");
    }

}
