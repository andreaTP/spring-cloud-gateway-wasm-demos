package com.albertoimpl.wasmgatewayfilters;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class SumService {

    @Value("classpath:sum.c.wasm")
    private Resource resourceFile;

    private Instance instance;
    private ExportFunction sumFn;

    @PostConstruct
    void init() throws IOException {
        WasmModule module = Parser.parse(resourceFile.getInputStream());
        instance = Instance.builder(module).build();
        sumFn = instance.exports().function("sum");
    }

    public Integer customSum(Integer x, Integer y) {
        return (int) sumFn.apply(x, y)[0];
    }
}
