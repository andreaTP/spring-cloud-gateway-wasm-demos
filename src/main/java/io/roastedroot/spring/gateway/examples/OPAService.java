package io.roastedroot.spring.gateway.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.styra.opa.wasm.OpaPolicy;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class OPAService {

    // TODO: FIXME is getting removed from target/classes
    @Value("file:src/main/resources/policy.wasm")
    private Resource resourceFile;

    private final ObjectMapper mapper;

    public OPAService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public boolean authz(String user) {
        OpaPolicy policy = null;
        try {
            policy = OpaPolicy.builder().withPolicy(resourceFile.getInputStream()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var result = policy.evaluate("\"" + user + "\"");
        try {
            var json = mapper.readTree(result);
            return json.get(0).get("result").asBoolean();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
