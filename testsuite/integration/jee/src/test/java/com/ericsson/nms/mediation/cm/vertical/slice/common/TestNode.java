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

package com.ericsson.nms.mediation.cm.vertical.slice.common;

import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants;

/**
 * Holds information about the tested node.
 */
public class TestNode {

    private final String ipAddress;
    private final String name;
    private final String netsimNodeSimulation;
    private final String ossModelIdentity;
    private String platformType = MoAttributeConstants.CPP_PLATFORM_TYPE_ATTR_VALUE;
    private String neType = MoAttributeConstants.ERBS_NETWORK_ELEMENT_TYPE_ATTR_VALUE;

    public TestNode(final String ipAddress, final String name, final String netsimNodeSimulation, final String ossModelIdentity) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.netsimNodeSimulation = netsimNodeSimulation;
        this.ossModelIdentity = ossModelIdentity;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getNetsimNodeSimulation() {
        return netsimNodeSimulation;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(final String platformType) {
        this.platformType = platformType;
    }

    public String getOssModelIdentity() {
        return ossModelIdentity;
    }

    public String getNeType() {
        return neType;
    }

    public void setNeType(final String neType) {
        this.neType = neType;
    }

    @Override
    public String toString() {
        final StringBuilder testNode = new StringBuilder("TestNode [IP Address: ");
        testNode.append(ipAddress);
        testNode.append(", Name: ");
        testNode.append(name);

        if (netsimNodeSimulation != null) {
            testNode.append(", Netsim Node Simulation: ");
            testNode.append(netsimNodeSimulation);
        }
        if (ossModelIdentity != null) {
            testNode.append(", OSS Model Identity: ");
            testNode.append(ossModelIdentity);
        }

        testNode.append(", Platform Type: ");
        testNode.append(platformType);

        testNode.append(", Network Element Type: ");
        testNode.append(neType);

        testNode.append("]");

        return testNode.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!TestNode.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final TestNode testNode = (TestNode) obj;
        return name.equals(testNode.name);
    }

}
