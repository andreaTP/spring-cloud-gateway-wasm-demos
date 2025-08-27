package io.roastedroot.spring.gateway.examples;

import io.roastedroot.quickjs4j.annotations.GuestFunction;
import io.roastedroot.quickjs4j.annotations.Invokables;
import io.roastedroot.quickjs4j.core.Engine;
import io.roastedroot.quickjs4j.core.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class JavaScriptService {

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptService.class);

    @Invokables("from_js")
    interface JsApi {
        @GuestFunction
        String validate(String body);
    }

    private final Engine engine =
            Engine.builder().addInvokables(JsApi_Invokables.toInvokables()).build();

    public String run(String query, String body) {
        try (Runner runner = Runner.builder().withEngine(engine).build()) {
            JsApi jsApi = JsApi_Invokables.create(query, runner);
            String result = jsApi.validate(body);

            if (!runner.stdout().isEmpty()) {
                LOG.info("js stdout: {}", runner.stdout());
            }
            if (!runner.stderr().isEmpty()) {
                LOG.info("js stderr: {}", runner.stderr());
            }

            return result;
        }
    }
}
