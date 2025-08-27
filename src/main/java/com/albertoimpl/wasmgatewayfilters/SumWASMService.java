package com.albertoimpl.wasmgatewayfilters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Collections.emptyList;

@Service
class SumWASMService {

    @Value("classpath:sum.wasm")
    private Resource resourceFile;

    public Integer customSum(Integer x, Integer y) {
        return x + y;
    }
}
