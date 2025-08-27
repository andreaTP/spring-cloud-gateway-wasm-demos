#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_ROOT=$( cd -- "${SCRIPT_DIR}/.." &> /dev/null && pwd )

WASI_SDK_VERSION=25
WASI_SDK_MINOR_VERSION=0
WASI_SDK_DIR="wasi-sdk-${WASI_SDK_VERSION}.${WASI_SDK_MINOR_VERSION}-x86_64-linux"
WASI_SDK_PATH="${SCRIPT_DIR}/tools/${WASI_SDK_DIR}"

# Check if wasi-sdk is already available
if [ ! -d "$WASI_SDK_PATH" ]; then
    echo "wasi-sdk not found at $WASI_SDK_PATH"
    echo "Downloading wasi-sdk using get_wasi_sdk.sh..."
    bash "${SCRIPT_DIR}/get_wasi_sdk.sh"
else
    echo "wasi-sdk found at $WASI_SDK_PATH"
fi

# Verify wasi-sdk is now available
if [ ! -d "$WASI_SDK_PATH" ]; then
    echo "Error: Failed to obtain wasi-sdk"
    exit 1
fi

# Set up environment variables for wasi-sdk
export WASI_SDK_PATH="$WASI_SDK_PATH"
export CC="${WASI_SDK_PATH}/bin/clang"
export CXX="${WASI_SDK_PATH}/bin/clang++"

C_SOURCE_DIR="${PROJECT_ROOT}/src/main/resources/c"
WASM_TARGET_DIR="${PROJECT_ROOT}/src/main/resources"

# Check if C source directory exists
if [ ! -d "$C_SOURCE_DIR" ]; then
    echo "Error: C source directory not found at $C_SOURCE_DIR"
    exit 1
fi

echo "Compiling C files from $C_SOURCE_DIR to $WASM_TARGET_DIR"

"$CC" \
    --target=wasm32-unknown-unknown -nostdlib -Wl,--no-entry -Wl,--export-all \
    -O3 \
    -o "${WASM_TARGET_DIR}/sum.c.wasm" \
    "${C_SOURCE_DIR}/sum.c"

"$CC" \
    --target=wasm32-wasip1 \
    --sysroot="$WASI_SDK_PATH/share/wasi-sysroot" \
    -O3 \
    -o "${WASM_TARGET_DIR}/hello-world.c.wasm" \
    "${C_SOURCE_DIR}/hello-world.c"

echo "Build completed successfully!"
echo "Generated WASM files:"
ls -la "$WASM_TARGET_DIR"/*.wasm 2>/dev/null || echo "No WASM files found"

