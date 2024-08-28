#! /bin/bash

source docker-env-functions.sh

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

install_visibroker() {
	yum install -y 'shadow-utils'
	groupadd -f -g 205 microfocus
	id -u visibroker >/dev/null 2>&1 && userdel -f -r visibroker
	enm ++ 'ERICvisibroker_CXP9031180'
	usermod -a -G jboss visibroker
}

unpack_visibroker_license() {
	# license is also available in Nexus artifact:
	# com.microfocus.visibroker:vb85-license-pack-dist:1.0.0:tar.gz:pack
	tar -xf ${DOCKER_INIT_DIR}/vblic.tar.gz -C /opt
}

print "Removing RPMs"
yum shell -y << EOF
remove ERICdpsmediationclient_CXP9031421 ERICeventmediationclient_CXP9031422 ERICsupervisionmediationclient_CXP9031281
remove ERICmediationrouter_CXP9031423
EOF

#print "Installing Visibroker"
#install_visibroker
print "Unpacking VB license"
unpack_visibroker_license

print "Installing byteman: ${BYTEMAN_HOME}"
install_byteman ${BYTEMAN_HOME}

print "Updating NECONN certificates"
cp -f ${DOCKER_INIT_DIR}/certs/* /ericsson/neconn/data/certs/

print "Adding route towards public network via LVS-ROUTER"
ip route add ${EXTERNAL_SUBNET} via ${LVSROUTER_IP_INTERNAL}

print "Adding multicast route for jgroups"
add_route_jgroups

print "Starting SSH daemon"
/usr/sbin/sshd

print "Waiting for Model Deployment Service"
wait_container "model-deployment"

print "Waiting for NEO4J"
wait_container "neo4j"
deploy_neo4j

print "Jboss may not be listening on port 9990, so breaking out of the jboss_monitor script"
sed -i '/curl/a break' /usr/sbin/jboss_monitor.sh

# patch ears
patch_ears "${DOCKER_JBOSS_DIR}/patch"

# pre-start completed
print "Starting with environment:"
dump_start_environment

if [ "${START_TCPDUMP}" == "true" ]; then
	print "Start dump network traffic"
	yum -y install tcpdump
	tcpdump -n -v port ${CM_UNSECURE_PORT} or port ${CM_SECURE_PORT} -w /output/${HOSTNAME}-dump.pcap &
fi
