package io.roastedroot.spring.gateway.examples;

import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class HelloWorldService {

    @Value("classpath:hello-world.c.wasm")
    private Resource resourceFile;

    private WasmModule module;

    @PostConstruct
    void init() throws IOException {
        module = Parser.parse(resourceFile.getInputStream());
    }

    public String customHelloWorld(String name) {
        return "todo";
    }
}
