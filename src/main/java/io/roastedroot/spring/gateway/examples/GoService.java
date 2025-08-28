package io.roastedroot.spring.gateway.examples;

import com.dylibso.chicory.runtime.ImportValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class GoService {

    @Value("classpath:validate.go.wasm")
    private Resource resourceFile;

    private WasmModule module;

    @PostConstruct
    void init() throws IOException {
        module = Parser.parse(resourceFile.getInputStream());
    }

    public String run(String body) {
        WasiOptions wasiOpts = WasiOptions.builder().inheritSystem().build();
        try (WasiPreview1 wasi = WasiPreview1.builder().withOptions(wasiOpts).build()) {

            Instance instance =
                    Instance.builder(module)
                            .withImportValues(
                                    ImportValues.builder()
                                            .addFunction(wasi.toHostFunctions())
                                            .build())
                            .withStart(false)
                            .build();
            var validateFn = instance.exports().function("validate");

            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            var inputPtr = (int) instance.exports().function("malloc").apply(bytes.length + 1)[0];
            instance.memory().writeCString(inputPtr, body);
            var resultPtr = (int) validateFn.apply(inputPtr)[0];

            return instance.memory().readCString(resultPtr);
        }
    }
}
