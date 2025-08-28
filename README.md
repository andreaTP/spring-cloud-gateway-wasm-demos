# Spring Cloud Gateway and WebAssembly showcases

A collection of custom Spring Cloud Gateway filters that demonstrate various WebAssembly integration patterns using different runtime environments and languages.

This project extends the work started in [wasm-gateway-filters](https://github.com/Albertoimpl/wasm-gateway-filters) by Albertoimpl, providing multiple examples of how to integrate WebAssembly with Spring Cloud Gateway for different use cases and switch to [Chicory](https://github.com/dylibso/chicory) as the underlying Wasm engine.

## Running the Application

### Prerequisites

- Java 11+
- Maven 3.6+

### Quick Start

```bash
mvn spring-boot:run
```

The application will start on port 8081.

## Examples

### 1. Sum Filter

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

### 2. Hello World Filter

Demonstrates string processing and standard input/output handling with WASM.

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

### 3. JQ Filter

Advanced JSON querying and transformation using [jq](https://github.com/jqlang/jq) the command line JSON processor.

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

**Source Code**: `src/main/java/io/roastedroot/spring/gateway/examples/JqService.java`

### 4. JavaScript Filter

Runtime JavaScript execution for dynamic request validation and processing based on [QuickJs](https://github.com/bellard/quickjs).

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

curl -X PUT localhost:8081/js \
  -H"X-JavaScript-validate:function validate(userStr) { const user = JSON.parse(userStr); return user.age >= 18 ? 'Valid' : 'User must be 18 or older.'; }" \
  -d '{"name":"Alice","age":20,"skills":["Java"]}'
# Valid
```

**Source Code**: `src/main/java/io/roastedroot/spring/gateway/examples/JavaScriptService.java`  
**Default Validation Function**: `src/test/resources/validate.js`

### 5. Go Filter

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
curl -s -o /dev/null -w "%{http_code}" localhost:8081/opa -H"X-User:Bob"
# Response: 200

# Denied user
curl -s -o /dev/null -w "%{http_code}" localhost:8081/opa -H"X-User:Alice"
# Response: 401
```

**Policy**: `src/main/resources/opa/policy.rego`

## Configuration

The gateway routes are configured in `src/main/resources/application.yaml`.

## Building and Running

### Compiling WASM Files

Use the provided build scripts in the `scripts/` directory to compile source files to WebAssembly:

```bash
./scripts/build-wasm.sh    # Build all C wasm examples
./scripts/build-go.sh      # For Go files
./scripts/build-opa.sh     # For OPA policies
```

## Testing

Run the comprehensive test suite to verify all examples:

```bash
mvn test
```

## Architecture

In this project all the examples are separate Spring Cloud Gateway filter:

```
Request → Gateway → Filter Chain → WASM Execution → Response Modification → Backend
```

Each filter:
1. Intercepts the request
2. Extracts parameters from headers or body
3. Executes the appropriate WASM function or script
4. Modifies the response accordingly
5. Forwards to the backend service

## Acknowledgments

This project builds upon the foundational work in [wasm-gateway-filters](https://github.com/Albertoimpl/wasm-gateway-filters) by Albertoimpl, extending it with multiple examples and runtime environments.
