/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.docker;

public class MoConstants {

    // Managed Object attributes values
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String DEFAULT_NETYPE = "ERBS";
    public static final String DEFAULT_PLATFORM_TYPE = "CPP";
    public static final String DEFAULT_OSS_MODEL_IDENTITY = "17A-H.1.160";
    public static final String DEFAULT_OSS_PREFIX = "";
    public static final String SYNCHRONIZED = "SYNCHRONIZED";
    public static final String UNSYNCHRONIZED = "UNSYNCHRONIZED";

    // Managed Object
    public static final String CM_NODE_HEARTBEAT_SUPERVISION = "CmNodeHeartbeatSupervision";
    public static final String CPP_CONNECTIVITY_INFORMATION = "CppConnectivityInformation";
    public static final String NETWORK_ELEMENT = "NetworkElement";
    public static final String CM_FUNCTION = "CmFunction";
    public static final String SECURITY_FUNCTION = "SecurityFunction";
    public static final String NETWORK_ELEMENT_SECURITY = "NetworkElementSecurity";
    public static final String MANAGED_ELEMENT = "ManagedElement";

    // Managed Object attribute names
    public static final String IP_ADDRESS = "ipAddress";
    public static final String OSS_MODEL_IDENTITY = "ossModelIdentity";
    public static final String PORT = "port";
    public static final String ACTIVE = "active";
    public static final String PLATFORM_TYPE = "platformType";
    public static final String OSS_PREFIX = "ossPrefix";
    public static final String NE_TYPE = "neType";
    public static final String NETWORK_ELEMENT_ID = "networkElementId";
    public static final String SYNC_STATUS = "syncStatus";
    public static final String RELEASE = "release";
    public static final String GENERATION_COUNTER = "generationCounter";

    // NetworkElementSecurity
    public static final String NETWORK_ELEMENT_SECURITY_NAMESPACE = "OSS_NE_SEC_DEF";
    public static final String NETWORK_ELEMENT_SECURITY_VERSION = "4.1.1";
    public static final String ROOT_USERNAME = "rootUserName";
    public static final String ROOT_USER_PASSWORD = "rootUserPassword";
    public static final String SECURE_USERNAME = "secureUserName";
    public static final String SECURE_USER_PASSWORD = "secureUserPassword";
    public static final String NORMAL_USERNAME = "normalUserName";
    public static final String NORMAL_USER_PASSWORD = "normalUserPassword";
    public static final String NETWORK_ELEMENT_SECURITY_ID = "NetworkElementSecurityId";
    public static final String TARGET_GROUPS = "targetGroups";
    public static final String SNMP_AUTH_KEY = "snmpAuthKey";
    public static final String SNMP_AUTH_PROTOCOL = "snmpAuthProtocol";
    public static final String SNMP_PRIV_KEY = "snmpPrivKey";
    public static final String SNMP_PRIV_PROTOCOL = "snmpPrivProtocol";

    // Action
    public static final String DELETE_NRM_DATA_FROM_ENM = "deleteNrmDataFromEnm";

    // Miscellaneous
    public static final String CPP_NAMESPACE = "CPP_MED";
    public static final String VERSION_1 = "1.0.0";
    public static final String NE_NAMESSPACE = "OSS_NE_DEF";
    public static final String VERSION_2 = "2.0.0";

}
