#!/bin/bash
KEEPALIVED_PID_FILE=/var/run/keepalived.pid

finish() {
	if [ -f ${KEEPALIVED_PID_FILE} ]; then
		local pid=$(cat ${KEEPALIVED_PID_FILE})
		echo "$(date) Stopping Keepalived (PID ${pid})"
		kill ${pid}
	fi
	echo "$(date) Exiting"
    exit
}
trap finish INT TERM

rm -f /var/run/*.pid

${DOCKER_INIT_DIR}/scripts/startup.sh

echo "### Service ${HOSTNAME} started ###"
while :; do
	# sleep in background to be able to process signals
	# see https://superuser.com/questions/1299461/how-can-i-stop-a-docker-container-running-sleep
    sleep 15 &
    wait
done
