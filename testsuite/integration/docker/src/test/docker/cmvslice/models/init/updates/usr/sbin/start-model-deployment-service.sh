#!/bin/bash -e

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

LOG=/var/log/mdt.log

if [ -f ${LOG} ]; then
	print "Removing: ${LOG}"
	cat ${LOG} >> ${LOG}.save
	rm -f ${LOG}
fi

/etc/init.d/modeldeployservice start

while [ ! -f ${LOG} ]; do
	sleep 1
done
while ! grep "Service registered successfully" ${LOG}; do
	sleep 1
done
