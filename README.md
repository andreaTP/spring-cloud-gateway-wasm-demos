# Spring Cloud Gateway and WebAssembly showcases

A comprehensive collection of custom Spring Cloud Gateway filters that demonstrate various WebAssembly integration patterns using different runtime environments and languages.

This project extends the work started in [wasm-gateway-filters](https://github.com/Albertoimpl/wasm-gateway-filters) by Albertoimpl, providing multiple examples of how to integrate WebAssembly with Spring Cloud Gateway for different use cases.

## Features

- **WASM Filters**: Custom filters that execute WebAssembly modules
- **Multiple Runtimes**: Support for Chicory WASM runtime and OPA WASM
- **Language Support**: C, Go, JavaScript, and Rego policy examples
- **Real-time Processing**: Request/response modification using WASM functions
- **Performance**: Lightweight WASM execution for high-throughput scenarios

## Examples

### 1. Sum Filter (WASM C Function)

A simple mathematical operation demonstrating basic WASM integration.

**Route**: `/sum`
**Method**: GET
**Headers**:
- `X-Sum-x`: First number
- `X-Sum-y`: Second number

**Response Header**: `X-Sum`: Result of addition

**Example**:
```bash
curl localhost:8081/sum -H"X-Sum-x:10" -H"X-Sum-y:16" -sI | grep X-Sum
# Response: X-Sum: 26
```

**Source Code**: `src/main/resources/c/sum.c`
```c
int sum(int x, int y) {
  return x + y;
}
```

### 2. Hello World Filter (WASM C Function)

Demonstrates string processing and input handling with WASM.

**Route**: `/hello`
**Method**: GET
**Headers**:
- `X-HelloWorld-name`: Name to greet

**Response Header**: `X-HelloWorld`: Greeting message

**Example**:
```bash
curl localhost:8081/hello -H"X-HelloWorld-name:foobar" -sI | grep X-HelloWorld
# Response: X-HelloWorld: Hello foobar
```

**Source Code**: `src/main/resources/c/hello-world.c`
```c
#include <stdio.h>
#include <string.h>

int main() {
    char input[1024];
    if (fgets(input, sizeof(input), stdin) != NULL) {
        input[strcspn(input, "\n")] = 0;
        printf("Hello %s", input);
    } else {
        printf("Hello World");
    }
    return 0;
}
```

### 3. JQ Filter (JSON Processing)

Advanced JSON querying and transformation using jq4j library.

**Route**: `/jq`
**Method**: PUT
**Headers**:
- `X-Jq-query`: JQ query expression
**Body**: JSON payload to process

**Response**: Transformed JSON based on query

**Example**:
```bash
curl -X PUT localhost:8081/jq \
  -H"X-Jq-query:{ user: .name, firstSkill: .skills[0] }" \
  -d '{"name":"Alice","age":30,"skills":["Java","Go","Wasm"]}'
# Response: {"user":"Alice","firstSkill":"Java"}
```

### 4. JavaScript Filter (Dynamic Validation)

Runtime JavaScript execution for dynamic request validation and processing.

**Route**: `/js`
**Method**: PUT
**Headers**:
- `X-JavaScript-validate`: JavaScript validation function
**Body**: JSON payload to validate

**Response**: Validation result or error message

**Example**:
```bash
curl -X PUT localhost:8081/js \
  -H"X-JavaScript-validate:function validate(userStr) { const user = JSON.parse(userStr); return user.age >= 18 ? 'Valid' : 'User must be 18 or older.'; }" \
  -d '{"name":"Alice","age":17,"skills":["Java"]}'
# Response: User must be 18 or older.
```

**Default Validation Function**: `src/test/resources/validate.js`
```javascript
function validate(userStr) {
    const user = JSON.parse(userStr);
    const errors = [];
    if (!user.name || user.name.length < 3) {
        errors.push("Name must be at least 3 characters.");
    }
    if (user.age < 18) {
        errors.push("User must be 18 or older.");
    }
    return errors.length > 0 ? errors.join(" ") : "Valid";
}
```

### 5. Go Filter (WASM Go Function)

Runtime Go WebAssembly execution for request validation and processing.

**Route**: `/go`
**Method**: PUT
**Headers**: None required
**Body**: JSON payload to validate

**Response**: Validation result or error message

**Example**:
```bash
curl -X PUT localhost:8081/go \
  -d '{"name":"Alice","age":17,"skills":["Java","Go","Wasm"]}'
# Response: User must be 18 or older.

curl -X PUT localhost:8081/go \
  -d '{"name":"Bob","age":25,"skills":["Python","Rust"]}'
# Response: Valid
```

**Source Code**: `src/main/resources/go/validate.go`
```go
type User struct {
    Name   string   `json:"name"`
    Age    int      `json:"age"`
    Skills []string `json:"skills"`
}

func validate(userStr string) string {
    var user User
    err := json.Unmarshal([]byte(userStr), &user)
    if err != nil {
        return "Invalid JSON format"
    }

    var errors []string

    if user.Name == "" || len(user.Name) < 3 {
        errors = append(errors, "Name must be at least 3 characters.")
    }

    if user.Age < 18 {
        errors = append(errors, "User must be 18 or older.")
    }

    if len(errors) > 0 {
        return strings.Join(errors, " ")
    }

    return "Valid"
}
```

### 6. OPA Filter (Policy Enforcement)

Open Policy Agent (OPA) integration for policy-based access control.

**Route**: `/opa`
**Method**: GET
**Headers**:
- `X-User`: Username for authorization

**Response**: 200 OK if allowed, 401 Unauthorized if denied

**Example**:
```bash
# Allowed user
curl localhost:8081/opa -H"X-User:Bob"
# Response: 200 OK

# Denied user
curl localhost:8081/opa -H"X-User:Alice"
# Response: 401 Unauthorized
```

**Policy**: `src/main/resources/opa/policy.rego`
```rego
package test

import rego.v1

default allow := false

allow if {
    input == "Bob"
}
```

## Configuration

The gateway routes are configured in `src/main/resources/application.yaml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - uri: https://chicory.dev
          predicates:
            - Path=/sum
          filters:
            - StripPrefix=1
            - SumFilter
        - uri: https://chicory.dev
          predicates:
            - Path=/hello
          filters:
            - StripPrefix=1
            - HelloWorldFilter
        - uri: https://chicory.dev
          predicates:
            - Path=/jq
          filters:
            - StripPrefix=1
            - JqFilter
        - uri: https://chicory.dev
          predicates:
            - Path=/js
          filters:
            - StripPrefix=1
            - JavaScriptFilter
        - uri: https://chicory.dev
          predicates:
            - Path=/go
          filters:
            - StripPrefix=1
            - GoFilter
        - uri: https://chicory.dev
          predicates:
            - Path=/opa
          filters:
            - StripPrefix=1
            - OPAFilter
server:
  port: 8081
```

## Building and Running

### Prerequisites

- Java 11+
- Maven 3.6+
- WASI SDK (for compiling C to WASM)

### Running the Application

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on port 8081.

### Compiling WASM Files

Use the provided build scripts in the `scripts/` directory to compile source files to WebAssembly:

```bash
# Make scripts executable
chmod +x scripts/*.sh

# Build all WASM files
./scripts/build-wasm.sh

# Build specific language WASM files
./scripts/build-go.sh      # For Go files
./scripts/build-opa.sh     # For OPA policies
```

The scripts will compile the appropriate source files to WebAssembly modules in the resources directory.

## Testing

Run the comprehensive test suite to verify all examples:

```bash
mvn test
```

The tests cover:
- Sum filter functionality
- Hello World filter with name input
- JQ JSON processing
- JavaScript validation
- Go validation
- OPA policy enforcement

## Dependencies

### Core Dependencies

- **Spring Boot 3.5.5**: Application framework
- **Spring Cloud Gateway**: API gateway functionality
- **Chicory**: WebAssembly runtime for Java
- **OPA Java WASM**: Open Policy Agent integration
- **jq4j**: JSON query processing
- **QuickJS4J**: JavaScript runtime

### Build Tools

- **Maven**: Build and dependency management
- **Spotless**: Code formatting and quality
- **Spring Boot Maven Plugin**: Application packaging

## Architecture

The project follows a modular architecture where each filter type is implemented as a separate Spring Cloud Gateway filter:

```
Request → Gateway → Filter Chain → WASM Execution → Response Modification → Backend
```

Each filter:
1. Intercepts the request
2. Extracts parameters from headers or body
3. Executes the appropriate WASM function or script
4. Modifies the response accordingly
5. Forwards to the backend service

## Performance Considerations

- **WASM Execution**: Lightweight and fast execution environment
- **Memory Management**: Efficient memory usage with Chicory runtime
- **Caching**: Consider implementing caching for frequently used WASM modules
- **Async Processing**: Non-blocking execution for high-throughput scenarios

## Security Considerations

- **Input Validation**: All inputs are validated before WASM execution
- **Sandboxing**: WASM modules run in isolated environments
- **Policy Enforcement**: OPA integration provides policy-based access control
- **JavaScript Isolation**: QuickJS runtime provides secure JavaScript execution

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.

## Acknowledgments

This project builds upon the foundational work in [wasm-gateway-filters](https://github.com/Albertoimpl/wasm-gateway-filters) by Albertoimpl, extending it with multiple examples and runtime environments.

## Related Projects

- [Chicory](https://github.com/dylibso/chicory): WebAssembly runtime for Java
- [Open Policy Agent](https://www.openpolicyagent.org/): Policy engine
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway): API gateway framework
