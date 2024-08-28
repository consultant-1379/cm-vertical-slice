#! /bin/bash

replace_properties() {
	sed -e "s|#MCAST_ADDR#|${MCAST_ADDR}|g" \
		-e "s|#MCAST_MESSAGING_ADDR#|${MCAST_MESSAGING_ADDR}|g" \
		-e "s|#ENM_SHARED_DIR#|${ENM_SHARED_DIR}|g" \
		-e "s|#CM_VIP#|${CM_VIP}|g" \
		-e "s|#MSCM#|${MSCM}|g" \
		-i "$1"
}

replace_properties "${ENM_SHARED_DIR}/global.properties"
replace_properties "${ENM_SHARED_DIR}/jboss/jvm/default.jvm.properties"
