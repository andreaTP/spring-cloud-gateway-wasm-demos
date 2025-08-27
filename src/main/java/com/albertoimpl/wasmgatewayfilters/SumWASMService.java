package com.albertoimpl.wasmgatewayfilters;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.Parser;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
class SumWASMService {

    @Value("classpath:sum.c.wasm")
    private Resource resourceFile;

    // private Instance instance;
    // private ExportFunction sumFunction;

    // @PostConstruct
    // void init() throws IOException {
    //     instance = Instance.builder(Parser.parse(resourceFile.getContentAsByteArray()))
    //             .build();
    //     sumFunction = instance.exports().function("sum");
    // }

    public Integer customSum(Integer x, Integer y) {
        return x + y + 1;
        // return (int) sumFunction.apply(x, y)[0];
    }
}
