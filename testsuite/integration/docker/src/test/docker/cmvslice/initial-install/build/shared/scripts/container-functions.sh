#! /bin/bash

NEXUS_URL=https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus
MCAST_ROUTE='224.0.0.0/4'

print() {
	echo "$(date) $@"
}

uninstall_rpm() {
	package=$(rpm -qa | grep "$1")
	if [ -n "${package}" ]; then
		rpm -ev --nodeps "${package}"
	fi
}

get_global_property() {
	grep "^$1 *=" "${ENM_SHARED_DIR}/global.properties" | cut -d'=' -f2
}

add_route_jgroups() {
	routes=$(ip route show ${MCAST_ROUTE} | wc -l)
	if [ $routes -eq 0 ]; then
		jgroups_nic=$(get_global_property 'jgroups_bind_nic')
		if [ -n "${jgroups_nic}" ]; then
			print "Adding route ${MCAST_ROUTE} to ${jgroups_nic}"
			ip route add ${MCAST_ROUTE} dev ${jgroups_nic}
		else
			print "Property jgroups_bind_nic is not defined"
		fi
	else
		print "Multicast route for jgroups already defined"
	fi
}

deploy_neo4j() {
	local JBOSS_DEPLOYMENTS_DIR="${JBOSS_HOME}/standalone/deployments"
	# Find neo4j ear in installation directory and copy to deployments folder
	NEO_EAR_STAGING_FILE=$(find '/opt/ericsson/ERICdpsneo4j_CXP9032728' -type f -regex ".*dps-neo4j-ear-.*\.staging")
	NEO_EAR_FILE=$(basename $(echo ${NEO_EAR_STAGING_FILE} | sed 's/staging/ear/g'))
	print "Deploying ${NEO_EAR_FILE}"
	cp -f "${NEO_EAR_STAGING_FILE}" "${JBOSS_DEPLOYMENTS_DIR}/${NEO_EAR_FILE}"
	# Find neo4j jca rar in installation directory and copy to deployments folder
	NEO_RAR_STAGING_FILE=$(find '/opt/ericsson/ERICneo4jjca_CXP9032726' -type f -regex ".*neo4j-jca-rar-.*\.staging")
	NEO_RAR_FILE=$(basename $(echo ${NEO_RAR_STAGING_FILE} | sed 's/staging/rar/g'))
	print "Deploying ${NEO_RAR_FILE}"
	cp -f "${NEO_RAR_STAGING_FILE}" "${JBOSS_DEPLOYMENTS_DIR}/${NEO_RAR_FILE}"
}

patch_ears() {
	if [ ! -d "$1" ]; then
		print "No EARs to patch"
		return
	fi
	for dir in $(find "$1" -mindepth 1 -maxdepth 1 -type d); do
		dirname=$(basename "${dir}")
		for ear in $(find {$JBOSS_HOME/standalone/deployments,/opt/ericsson} -type f -name "${dirname}-*.ear"); do
			print "Patching ${ear}"
			jar uf ${ear} -C "${dir}" .
		done
	done
}

install_byteman() {
	local BYTEMAN_HOME="$1"
	BYTEMAN_VERSION=$(echo ${BYTEMAN_HOME} | sed s'/.*\-//'g)
	BYTEMAN_FILE=byteman-download-${BYTEMAN_VERSION}-bin.zip
	NEXUS_LINK=${NEXUS_URL}/service/local/repositories/sonatype/content/org/jboss/byteman/byteman-download/${BYTEMAN_VERSION}/${BYTEMAN_FILE}

	mkdir -p /opt/ && cd /opt && curl -O ${NEXUS_LINK} && jar xvf ${BYTEMAN_FILE}

	find ${BYTEMAN_HOME} -type f -exec chmod +x {} \;

	# write byteman home to shared dir host file
	echo -n "${BYTEMAN_HOME}" > "${ENM_SHARED_DIR}/byteman/${HOSTNAME}.host"
}

dump_start_environment() {
	print "  HOSTNAME=${HOSTNAME}"
	print "  IP_INTERNAL=${IP_INTERNAL}"
	print "  IP_MESSAGING=${IP_MESSAGING}"
	print "  HOST_IP_ADDRESS=${HOST_IP_ADDRESS}"
}
