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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.COMMA;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS_ONE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.FORMAT_STRING;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CM_FUNCTION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CM_NODE_HEARTBEAT_SUPERVISION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    public static String getFdn(final String fdnFormat, final String nodeName) {
        final String fdn = String.format(fdnFormat, nodeName);
        LOGGER.trace("Formatted fdn: [{}]", fdn);
        return fdn;
    }

    public static String getCmNodeHeartbeatSupervisionFdn(final String nodeName) {
        return getFdn(NETWORK_ELEMENT + EQUALS + FORMAT_STRING + COMMA + CM_NODE_HEARTBEAT_SUPERVISION + EQUALS_ONE, nodeName);
    }

    public static String getCmFunctionFdn(final String nodeName) {
        return getFdn(NETWORK_ELEMENT + EQUALS + FORMAT_STRING + COMMA + CM_FUNCTION + EQUALS_ONE, nodeName);
    }

    public static String getNetworkElementFdn(final String nodeName) {
        return getFdn(NETWORK_ELEMENT + EQUALS + FORMAT_STRING, nodeName);
    }

}
