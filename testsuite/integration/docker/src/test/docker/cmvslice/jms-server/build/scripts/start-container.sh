#!/bin/bash

finish() {
	echo "### Service ${HOSTNAME} terminating ###"
	echo "Stopping JBoss"
	/etc/init.d/jboss stop
	echo "### Service ${HOSTNAME} terminated ###"
    exit
}
trap finish INT TERM

# record start time
mkdir -p ${ENM_SHARED_DIR}/vmlockfile
date '+%s' > ${ENM_SHARED_DIR}/vmlockfile/.${HOSTNAME}.starting

# ensure global.properties exists
[ -f ${ENM_SHARED_DIR}/global.properties ] || touch ${ENM_SHARED_DIR}/global.properties

# start jboss
[ -f /tmp/standalone-jms.conf ] && mv /tmp/standalone-jms.conf /ericsson/3pp/jboss/bin/standalone-jms.conf
/etc/init.d/jboss start

echo "### Service ${HOSTNAME} started ###"
while :; do
	# sleep in background to be able to process signals
	# see https://superuser.com/questions/1299461/how-can-i-stop-a-docker-container-running-sleep
    sleep 15 &
    wait
done
