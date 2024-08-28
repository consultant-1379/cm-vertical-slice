#!/bin/bash

source ${ENM_SHARED_DIR}/scripts/container-functions.sh

source ${ENM_SHARED_DIR}/scripts/retrieve-global-properties

KEEPALIVED_CONF="$1"

fill_template() {
	sed -e "s|#CM_VIP#|${CM_VIP}|g" \
		-e "s|#MSCM_IP#|${MSCM_IP}|g" \
		-e "s|#CM_PORT#|${CM_PORT}|g" \
		-e "s|#TCP_CHECK_PORT#|8081|g" \
		"$1"
}

print "Reading global properties"
CM_VIP=${GLOBAL_PROPERTIES_ARRAY['svc_CM_vip_ipaddress']}
IFS=',' read -r -a MSCM <<< "${GLOBAL_PROPERTIES_ARRAY['mscm']}"

print "CM_VIP: ${CM_VIP}"
print "MSCM: ${MSCM[@]}"

print "Generating config file: ${KEEPALIVED_CONF}"
rm -f "${KEEPALIVED_CONF}"
fill_template /etc/keepalived/enm_keepalived_EXTERNAL_CM.template >> "${KEEPALIVED_CONF}"

for CM_PORT in ${CM_UNSECURE_PORT} ${CM_SECURE_PORT}; do
	fill_template /etc/keepalived/enm_keepalived_MSCM_BEGIN.template >> "${KEEPALIVED_CONF}"
	for MSCM_IP in ${MSCM[@]}; do
		fill_template /etc/keepalived/enm_keepalived_MSCM_RealServer.template >> "${KEEPALIVED_CONF}"
	done
	fill_template /etc/keepalived/enm_keepalived_MSCM_END.template >> "${KEEPALIVED_CONF}"
done
