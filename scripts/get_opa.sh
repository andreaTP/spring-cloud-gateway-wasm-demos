#! /bin/bash
set -euxo pipefail

OPA_VERSION="1.7.1"

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

OPA_URL="https://github.com/open-policy-agent/opa/releases/download/v${OPA_VERSION}/opa_linux_amd64"

mkdir -p ${SCRIPT_DIR}/tools

(
    cd ${SCRIPT_DIR}/tools
    echo "Downloading opa..."
    wget "$OPA_URL"
    mv opa_linux_amd64 opa
    chmod a+x opa
)
