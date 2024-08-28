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

package com.ericsson.nms.mediation.cm.vertical.slice.utility;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ANTENNA_SUBUNIT_MO_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.AU_PORT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CABINET_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_FUNCTION_MO_RDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_NODE_HEARTBEAT_SUPERVISION_MO_RDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CPP_CI_MO_RDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ENODE_B_FUNCTION_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EQUIPMENT_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EUTRAN_CELL_FDD_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EUTRAN_CELL_TDD_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.MANAGED_ELEMENT_RDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ME_CONTEXT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NETWORK_ELEMENT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NODE_MANAGED_FUNCTION_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.RBS_COFIGURATION_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.SCTPPROFILE_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.SCTP_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TRANSPORT_NETWORK_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TREAT_AS_MO_TYPE;

/**
 * Helps to construct commonly used FDNs.
 */
public class FdnUtil {

    private FdnUtil() {
    }

    // --- OSS FDN resolving methods --- //

    public static String getNetworkElementFdn(final String nodeName) {
        return NETWORK_ELEMENT_MO_TYPE + "=" + nodeName;
    }

    public static String getCmFunctionFdn(final String nodeName) {
        return getNetworkElementFdn(nodeName) + "," + CM_FUNCTION_MO_RDN;
    }

    public static String getCppCiFdn(final String nodeName) {
        return getNetworkElementFdn(nodeName) + "," + CPP_CI_MO_RDN;
    }

    public static String getCmNodeHeartbeatSupervisionFdn(final String nodeName) {
        return getNetworkElementFdn(nodeName) + "," + CM_NODE_HEARTBEAT_SUPERVISION_MO_RDN;
    }

    public static String getManagedElementFdn(final String nodeName) {
        return getMeContextFdn(nodeName) + "," + MANAGED_ELEMENT_RDN;
    }

    // --- Node FDN resolving methods (w/o MeContext prepended) --- //

    public static String getSctpNodeFdn(final String moName) {
        return TRANSPORT_NETWORK_FDN + "," + SCTP_MO_TYPE + "=" + moName;
    }

    public static String getEUtranCellFddNodeFdn(final String moName) {
        return ENODE_B_FUNCTION_FDN + "," + EUTRAN_CELL_FDD_MO_TYPE + "=" + moName;
    }

    public static String getEUtranCellTddNodeFdn(final String moName) {
        return ENODE_B_FUNCTION_FDN + "," + EUTRAN_CELL_TDD_MO_TYPE + "=" + moName;
    }

    public static String getAuPortNodeFdn(final String moName) {
        return ANTENNA_SUBUNIT_MO_FDN + "," + AU_PORT_MO_TYPE + "=" + moName;
    }

    public static String getTreatAsMoFdn(final String moName) {
        return ENODE_B_FUNCTION_FDN + "," + TREAT_AS_MO_TYPE + "=" + moName;
    }

    public static String getCabinetMoFdn(final String moName) {
        return EQUIPMENT_FDN + "," + CABINET_MO_TYPE + "=" + moName;
    }

    public static String getSctpProfileNodeFdn(final String moName) {
        return TRANSPORT_NETWORK_FDN + "," + SCTPPROFILE_MO_TYPE + "=" + moName;
    }

    public static String getRbsConfigurationFdn(final String moName) {
        return NODE_MANAGED_FUNCTION_FDN + "," + RBS_COFIGURATION_MO_TYPE + "=" + moName;
    }

    public static String getWrongRbsConfigurationFdn(final String moName) {
        return ENODE_B_FUNCTION_FDN + "," + RBS_COFIGURATION_MO_TYPE + "=" + moName;
    }

    // --- MeContext FDN methods --- //

    public static String getMeContextFdn(final String nodeName) {
        return ME_CONTEXT_MO_TYPE + "=" + nodeName;
    }

    public static String prependMeContextToFdn(final String nodeName, final String fdn) {
        return getMeContextFdn(nodeName) + "," + fdn;
    }

    // --- Other methods --- //
    public static String getNodeName(final String fdn) {
        return fdn.substring(fdn.indexOf("=") + 1, fdn.indexOf(","));
    }

    public static String getParentFdn(final String fdn) {
        final int lastIndexOfComma = fdn.lastIndexOf(',');
        if (lastIndexOfComma < 0) {
            return "";
        }
        final String parentFdn = fdn.substring(0, lastIndexOfComma).trim();
        return parentFdn;
    }

}
