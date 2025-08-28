#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_ROOT=$( cd -- "${SCRIPT_DIR}/.." &> /dev/null && pwd )

OPA_PATH="${SCRIPT_DIR}/tools/opa"

# Check if opa is already available
if [ ! -f "$OPA_PATH" ]; then
    echo "opa not found at $OPA_PATH"
    echo "Downloading opa using get_opa.sh..."
    bash "${SCRIPT_DIR}/get_opa.sh"
else
    echo "opa found at $OPA_PATH"
fi

# Verify opa is now available
if [ ! -f "$OPA_PATH" ]; then
    echo "Error: Failed to obtain opa"
    exit 1
fi

RESOURCES_DIR="${PROJECT_ROOT}/src/main/resources"
OPA_SOURCE_DIR="${RESOURCES_DIR}/opa"

opa build -t wasm -e test/allow ${OPA_SOURCE_DIR}/policy.rego
tar -xzf bundle.tar.gz -C ${RESOURCES_DIR} /policy.wasm
chmod 755 ${RESOURCES_DIR}/policy.wasm
rm -rf bundle.tar.gz
