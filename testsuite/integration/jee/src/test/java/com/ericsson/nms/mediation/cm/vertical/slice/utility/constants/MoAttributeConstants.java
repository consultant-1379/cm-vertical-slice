/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.utility.constants;

/**
 * Constants related to the attributes of Managed Objects.
 */
public class MoAttributeConstants {

    // Attribute Names
    public static final String ACTIVE_ATTR_NAME = "active";
    public static final String ANU_TYPE_ATTR_NAME = "anuType";
    public static final String CATEGORY_ATTR_NAME = "category";
    public static final String CM_FUNCTION_ID_ATTR_NAME = "CmFunctionId";
    public static final String CM_NODE_HEARTBEAT_SUPERVISION_ID_ATTR_NAME = "CmNodeHeartbeatSupervisionId";
    public static final String CPP_CI_ID_ATTR_NAME = "CppConnectivityInformationId";
    public static final String DL_MAX_WAIT_ATTR_NAME = "dlMaxWaitingTimeGlobal";
    public static final String DHCP_CLIENT_IDENTIFIER_ATTR_NAME = "dhcpClientIdentifier";
    public static final String DNS_LOOKUP_TIMER_ATTR_NAME = "dnsLookupTimer";
    public static final String EARFCNUL_ATTR_NAME = "earfcnul";
    public static final String EUTRAN_CELL_POLYGON_ATTR_NAME = "eutranCellPolygon";
    public static final String FAILED_SYNCS_COUNT_ATTR_NAME = "failedSyncsCount";
    public static final String GENERATION_COUNTER_ATTR_NAME = "generationCounter";
    public static final String IP_ADDRESS_ATTR_NAME = "ipAddress";
    public static final String LOST_SYNCHRONIZATION_ATTR_NAME = "lostSynchronization";
    public static final String ME_CONTEXT_ID_ATTR_NAME = "MeContextId";
    public static final String MODEL_IDENTITY_ATTR_NAME = "modelIdentity";
    public static final String NAME_ATTR_NAME = "name";
    public static final String NETWORK_ELEMENT_ID_ATTR_NAME = "networkElementId";
    public static final String NETWORK_ELEMENT_TYPE_ATTR_NAME = "neType";
    public static final String NE_UTC_OFFSET_ATTR_NAME = "utcOffset";
    public static final String NE_TECHNOLOGY_DOMAIN_ATTR_NAME = "technologyDomain";
    public static final String NODE_MODEL_IDENTITY_ATTR_NAME = "nodeModelIdentity";
    public static final String OSS_MODEL_IDENTITY_ATTR_NAME = "ossModelIdentity";
    public static final String OSS_PREFIX_ATTR_NAME = "ossPrefix";
    public static final String PLATFORM_TYPE_ATTR_NAME = "platformType";
    public static final String PORT_ATTR_NAME = "port";
    public static final String RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_ATTR_NAME = "releaseInactiveUesMpLoadLevel";
    public static final String SECTOR_CARRIER_REF_ATTR_NAME = "sectorCarrierRef";
    public static final String STATIC_ROUTES_ATTR_NAME = "staticRoutes";
    public static final String SYNC_STATUS_ATTR_NAME = "syncStatus";
    public static final String TARGET_NAMESPACE_KEYS_ATTR_NAME = "targetNamespaceKeys";
    public static final String TREAT_AS_ATTR_NAME = "treatAsTestNewAttribute";
    public static final String TYPE_ATTR_NAME = "type";
    public static final String USER_LABEL_ATTR_NAME = "userLabel";

    // Attribute Values
    public static final String ANU_TYPE_ATTR_NETSIM_INITIAL_VALUE = "0";
    public static final String ANU_TYPE_ATTR_NETSIM_FINAL_VALUE = "1";
    public static final String ANU_TYPE_ATTR_DPS_FINAL_VALUE = "ASC";
    public static final String ARAGORN_ATTR_VALUE = "Aragorn";
    public static final String CPP_PLATFORM_TYPE_ATTR_VALUE = "CPP";
    public static final String DHCP_CLIENT_INTERFACE_INITIAL_ATTR_VALUE = "[,0,8]";
    public static final String DHCP_CLIENT_INTERFACE_FINAL_ATTR_VALUE = "[1,0,1]";
    public static final String DL_MAX_WAIT_INITIAL_ATTR_VALUE = "0";
    public static final String DL_MAX_WAIT_FINAL_ATTR_VALUE = "1";
    public static final String DNS_LOOKUP_TIMER_INITIAL_ATTR_VALUE = "1500";
    public static final String DNS_LOOKUP_TIMER_FINAL_ATTR_VALUE = "1600";
    public static final String EARFCNUL_ATTR_VALUE = "18000";
    public static final String ERBS_NETWORK_ELEMENT_TYPE_ATTR_VALUE = "ERBS";
    public static final String RNC_NETWORK_ELEMENT_TYPE_ATTR_VALUE = "RNC";
    public static final String EUTRAN_CELL_POLYGON_ATTR_VALUE = "[[1,2],[3,4]]";
    public static final String GANDALF_ATTR_VALUE = "Gandalf";
    public static final boolean INITIAL_ACTIVE_ATTR_VALUE = false;
    public static final String LEGOLAS_ATTR_VALUE = "Legolas";
    public static final String RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_INITIAL_VALUE = "2";
    public static final String RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_FINAL_VALUE = "1";
    public static final String RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_DPS_FINAL_VALUE = "HIGH_LOAD";
    public static final String SECTOR_CARRIER_REF_ATTR_VALUE = "[379,380,381,382]";
    public static final int SSH_PORT_ATTR_VALUE = 22;
    public static final String STATIC_ROUTES_INITIAL_ATTR_VALUE = "";
    public static final String STATIC_ROUTES_FINAL_ATTR_VALUE = "[[0,192.168.0.1,255.255.255.168,192.168.0.2,true,100,0],"
            + "[1,192.168.0.1,255.255.255.168,192.168.0.2,true,100,1],[2,192.168.0.1,255.255.255.168,192.168.0.2,true,100,2]]";
    public static final String TREAT_AS_ATTR_INITIAL_VALUE = "0";
    public static final String TREAT_AS_ATTR_FINAL_VALUE = "22";

    // Sync Status Attribute values
    public static final String ATTRIBUTE_SYNC_STATUS_ATTR_VALUE = "ATTRIBUTE";
    public static final String DELTA_SYNC_STATUS_ATTR_VALUE = "DELTA";
    public static final String PENDING_SYNC_STATUS_ATTR_VALUE = "PENDING";
    public static final String SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE = "SYNCHRONIZED";
    public static final String TOPOLOGY_SYNC_STATUS_ATTR_VALUE = "TOPOLOGY";
    public static final String UNSYNCHRONIZED_SYNC_STATUS_ATTR_VALUE = "UNSYNCHRONIZED";

    // Network Element Security Attribute values
    public static final String CHAR_ENCODING_ATTR_VALUE = "UTF-8";
    public static final String CPP_NODE_PASSWORD_ATTR_VALUE = "netsim";
    public static final String CPP_NODE_USERNAME_ATTR_VALUE = "netsim";
    public static final String NETWORK_ELEMENT_SECURITY_ID_ATTR_NAME = "NetworkElementSecurityId";
    public static final String NORMAL_USERNAME_ATTR_NAME = "normalUserName";
    public static final String NORMAL_USER_PASSWORD_ATTR_NAME = "normalUserPassword";
    public static final String ROOT_USERNAME_ATTR_NAME = "rootUserName";
    public static final String ROOT_USER_PASSWORD_ATTR_NAME = "rootUserPassword";
    public static final String SECURE_USERNAME_ATTR_NAME = "secureUserName";
    public static final String SECURE_USER_PASSWORD_ATTR_NAME = "secureUserPassword";
    public static final String TARGET_GROUPS_ATTR_NAME = "targetGroups";
    public static final String VERSION_1_0_0_ATTR_VALUE = "1.0.0";
    public static final String VERSION_4_1_1_ATTR_VALUE = "4.1.1";

    // Attribute Supporting Constants
    public static final String EUTRAN_CELL_POLYGON_ATTR_MODIFY_COMMAND_POSTFIX = " ([struct], EutranCellCorner)";

    private MoAttributeConstants() {}

}
