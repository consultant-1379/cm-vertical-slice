#! /bin/bash

source docker-env-functions.sh

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

print "Uninstalling iptables"
uninstall_rpm 'iptables'

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

# pre-start completed
print "Starting with environment:"
dump_start_environment
