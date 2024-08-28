#!/bin/bash

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

KEEPALIVED_CONF=/etc/keepalived/enm_keepalived.conf

print "Configuring SNAT"
iptables -A FORWARD -s ${INTERNAL_SUBNET} -d ${EXTERNAL_SUBNET} -j ACCEPT
iptables -t nat -A POSTROUTING -d ${EXTERNAL_SUBNET} -j MASQUERADE
iptables -A FORWARD -s ${EXTERNAL_SUBNET} -d ${INTERNAL_SUBNET} -m state --state RELATED,ESTABLISHED -j ACCEPT

print "Starting SSH daemon"
/usr/sbin/sshd

print "Creating Keepalived config file"
${DOCKER_INIT_DIR}/scripts/create-keepalived-conf.sh ${KEEPALIVED_CONF}

print "Starting Keepalived"
# Usage: keepalived [OPTION...]
#  -f, --use-file=FILE          Use the specified configuration file
#  -P, --vrrp                   Only run with VRRP subsystem
#  -C, --check                  Only run with Health-checker subsystem
#  -l, --log-console            Log messages to local console
#  -D, --log-detail             Detailed log messages
#  -S, --log-facility=[0-7]     Set syslog facility to LOG_LOCAL[0-7]
#  -X, --release-vips           Drop VIP on transition from signal.
#  -V, --dont-release-vrrp      Don't remove VRRP VIPs and VROUTEs on daemon stop
#  -I, --dont-release-ipvs      Don't remove IPVS topology on daemon stop
#  -R, --dont-respawn           Don't respawn child processes
#  -n, --dont-fork              Don't fork the daemon process
#  -d, --dump-conf              Dump the configuration data
#  -p, --pid=FILE               Use specified pidfile for parent process
#  -r, --vrrp_pid=FILE          Use specified pidfile for VRRP child process
#  -c, --checkers_pid=FILE      Use specified pidfile for checkers child process
#  -a, --address-monitoring     Report all address additions/deletions notified via netlink
#  -x, --snmp                   Enable SNMP subsystem
#  -A, --snmp-agent-socket=FILE Use the specified socket for master agent
#  -s, --namespace=NAME         Run in network namespace NAME (overrides config)
#  -m, --core-dump              Produce core dump if terminate abnormally
#  -M, --core-dump-pattern=PATN Also set /proc/sys/kernel/core_pattern to PATN (default 'core')
#  -i, --config_id id           Skip any configuration lines beginning '@' that don't match id
#  -v, --version                Display the version number
#  -h, --help                   Display this help message
nohup /usr/sbin/keepalived -n -l -d -D -f ${KEEPALIVED_CONF} -p /var/run/keepalived.pid |& tee /var/log/keepalived.log &

while ! lsmod | grep -Eq "^ip_vs " &>/dev/null; do
	print "Waiting for ip_vs to be loaded"
	sleep 1
done
print "Configuring ip_vs"
sysctl -w net.ipv4.vs.expire_nodest_conn=1

if [ "${START_TCPDUMP}" == "true" ]; then
	print "Start dump network traffic"
	yum -y install tcpdump
	tcpdump -i any -n -v -w /output/${HOSTNAME}-dump.pcap &
fi
