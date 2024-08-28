#! /bin/bash

source docker-env-functions.sh

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

print "Installing additional packages"
yum install -y iproute 2>/dev/null

print "Updating filesystem"
cp -Rf ${DOCKER_INIT_DIR}/updates/* /

print "Creating model deployment directories"
mkdir -p /var/opt/ericsson/ERICmodeldeployment/data/{install,post_install}

print "Installing RPMs"
install_rpms_from_nexus
install_rpms_from_iso

print "Configuring non CPP node models for removal"
function create_empty_model {
	local rpm=$1
	rpm_install_dir="/etc/opt/ericsson/ERICmodeldeployment/data/execution/toBeInstalled/${rpm}/99.9.9"
	mkdir -p "${rpm_install_dir}"
	cp -f ${DOCKER_INIT_DIR}/nodemodels/empty-node-model-jar.jar "${rpm_install_dir}/empty-${rpm}-jar.jar"
}

for node_model_rpm in $(enm list-models | grep 'nodemodel_CXP' | grep -v -f ${DOCKER_INIT_DIR}/nodemodels/needed-rpms.txt); do
	create_empty_model ${node_model_rpm}
done
for model_rpm in $(enm list-models | grep -f ${DOCKER_INIT_DIR}/nodemodels/removable-model-rpms.txt); do
	create_empty_model ${model_rpm}
done
