/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
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
 * Constants related to Managed Objects.
 */
public class MoConstants {

    // MO Types
    public static final String AU_PORT_MO_TYPE = "AuPort";
    public static final String CABINET_MO_TYPE = "Cabinet";
    public static final String CM_FUNCTION_MO_TYPE = "CmFunction";
    public static final String CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE = "CmNodeHeartbeatSupervision";
    public static final String CPP_CI_MO_TYPE = "CppConnectivityInformation";
    public static final String EAI_MO_TYPE = "EntityAddressingInformation";
    public static final String EUTRAN_CELL_FDD_MO_TYPE = "EUtranCellFDD";
    public static final String EUTRAN_CELL_TDD_MO_TYPE = "EUtranCellTDD";
    public static final String MANAGED_ELEMENT_MO_TYPE = "ManagedElement";
    public static final String ME_CONTEXT_MO_TYPE = "MeContext";
    public static final String NETWORK_ELEMENT_MO_TYPE = "NetworkElement";
    public static final String NETWORK_ELEMENT_SECURITY_MO_TYPE = "NetworkElementSecurity";
    public static final String SCTP_MO_TYPE = "Sctp";
    public static final String SCTPPROFILE_MO_TYPE = "SctpProfile";
    public static final String RBS_COFIGURATION_MO_TYPE = "RbsConfiguration";
    public static final String SECURITY_FUNCTION_MO_TYPE = "SecurityFunction";
    public static final String TARGET_MO_TYPE = "Target";
    public static final String TREAT_AS_MO_TYPE = "NewMoToTestTreatAs";

    // MO Reference Types
    public static final String CI_REF_TYPE = "ciRef";
    public static final String NETWORK_ELEMENT_REF_TYPE = "networkElementRef";

    // MO Names (Values)
    public static final String ARWEN_MO_NAME = "Arwen";
    public static final String CABINET_MO_NAME = "TreatAsCabinet";
    public static final String DEFAULT_MO_NAME = "1";
    public static final String DUMMY_MO_NAME = "Dummy";
    public static final String RIVENDELL_MO_NAME = "Rivendell";

    // MO RDNs (Relatively Distinguished Names)
    public static final String CM_FUNCTION_MO_RDN = CM_FUNCTION_MO_TYPE + "=" + DEFAULT_MO_NAME;
    public static final String CM_NODE_HEARTBEAT_SUPERVISION_MO_RDN = CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE + "=" + DEFAULT_MO_NAME;
    public static final String CPP_CI_MO_RDN = CPP_CI_MO_TYPE + "=" + DEFAULT_MO_NAME;
    public static final String MANAGED_ELEMENT_RDN = MANAGED_ELEMENT_MO_TYPE + "=" + DEFAULT_MO_NAME;

    // MO FDNs (Fully Distinguished Names)
    public static final String ANTENNA_UNIT_GROUP_FDN = MANAGED_ELEMENT_RDN + ",Equipment=1,AntennaUnitGroup=1";
    public static final String ANTENNA_SUBUNIT_MO_FDN = ANTENNA_UNIT_GROUP_FDN + ",AntennaUnit=1,AntennaSubunit=1";
    public static final String ANTENNA_NEAR_UNIT_MO_FDN = ANTENNA_UNIT_GROUP_FDN + ",AntennaNearUnit=1";
    public static final String ENODE_B_FUNCTION_FDN = MANAGED_ELEMENT_RDN + ",ENodeBFunction=1";
    public static final String NODE_MANAGED_FUNCTION_FDN = MANAGED_ELEMENT_RDN + ",NodeManagementFunction=1";
    public static final String EQUIPMENT_FDN = MANAGED_ELEMENT_RDN + ",Equipment=1";
    public static final String IP_ROUTING_TABLE_MO_FDN = MANAGED_ELEMENT_RDN + ",IpOam=1,Ip=1,IpRoutingTable=1";
    public static final String IP_INTERFACE_MO_FDN = EQUIPMENT_FDN
            + ",Subrack=1,Slot=1,PlugInUnit=1,ExchangeTerminalIp=1,GigaBitEthernet=1,IpInterface=1";
    public static final String TRANSPORT_NETWORK_FDN = MANAGED_ELEMENT_RDN + ",TransportNetwork=1";

    // MO Namespaces
    public static final String CPP_MED_NAMESPACE = "CPP_MED";
    public static final String DPS_NAMESPACE = "DPS";
    public static final String MEDIATION_NAMESPACE = "MEDIATION";
    public static final String OSS_NE_CM_DEF_NAMESPACE = "OSS_NE_CM_DEF";
    public static final String OSS_NE_DEF_NAMESPACE = "OSS_NE_DEF";
    public static final String OSS_NE_SEC_DEF_NAMESPACE = "OSS_NE_SEC_DEF";
    public static final String OSS_TOP_NAMESPACE = "OSS_TOP";

    // MO Versions
    public static final String CM_FUNCTION_MO_VERSION = "1.0.1";
    public static final String CM_NODE_HEARTBEAT_SUPERVISION_MO_VERSION = "1.0.1";
    public static final String CPP_CI_MO_VERSION = "1.0.0";
    public static final String EAI_MO_VERSION = "1.0.0";
    public static final String ME_CONTEXT_MO_VERSION = "3.0.0";
    public static final String NETWORK_ELEMENT_MO_VERSION = "2.0.0";
    public static final String TARGET_MO_VERSION = "1.0.0";

    private MoConstants() {}

}
