package io.roastedroot.spring.gateway.examples;

import com.dylibso.chicory.compiler.MachineFactoryCompiler;
import com.dylibso.chicory.runtime.ImportValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public String helloWorld(String name) {
        try (ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                InputStream stdin =
                        new ByteArrayInputStream(name.getBytes(StandardCharsets.UTF_8));
                WasiPreview1 wasi =
                        WasiPreview1.builder()
                                .withOptions(
                                        WasiOptions.builder()
                                                .withStdin(stdin)
                                                .withStdout(stdout)
                                                .build())
                                .build(); ) {
            Instance.builder(module)
                    // using the runtime compiler
                    .withMachineFactory(MachineFactoryCompiler::compile)
                    .withImportValues(
                            ImportValues.builder().addFunction(wasi.toHostFunctions()).build())
                    // directly leveraging the "main" function
                    .build();

            return stdout.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
    }
}
