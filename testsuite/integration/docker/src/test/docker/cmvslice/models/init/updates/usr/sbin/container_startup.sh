#!/bin/bash
set -e

source docker-env-functions.sh

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

trap cleanup INT TERM EXIT

function cleanup() {
	print "Stopping the Model Deployment Service"
	/etc/init.d/modeldeployservice stop
}

# Ensure cleanup
rm -rf ${SHARED_DIR}/${READY_FILE_NAME}

# Deploy additional RPMs
print "Deploying additional models..."
sed -i '/create_ready_file/d' /deploy-models.sh
/deploy-models.sh || exit 1

# Restart the model deployment service with mdt.send.events=true
print "Restarting the Model Deployment Service"
/etc/init.d/modeldeployservice stop

start-model-deployment-service.sh

create_ready_file

echo "### Service ${HOSTNAME} started ###"
while :; do
    sleep 15 &
    wait
done
