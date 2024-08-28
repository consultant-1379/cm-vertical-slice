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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.FAILED_SYNCS_COUNT_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.GENERATION_COUNTER_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NE_UTC_OFFSET_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NODE_MODEL_IDENTITY_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.OSS_MODEL_IDENTITY_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.MANAGED_ELEMENT_MO_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.oss.mediation.network.api.MociCMConnectionProvider;
import com.ericsson.oss.mediation.network.api.exception.MociConnectionProviderException;
import com.ericsson.oss.mediation.network.api.notifications.NodeNotification;
import com.ericsson.oss.mediation.network.api.notifications.NotificationType;
import com.ericsson.oss.mediation.network.api.util.ConnectionConfig;

/**
 * Test helper class.<br>
 * Executes more complex assertions.
 */
public class AssertAssistant {

    private static final String INSTRUMENTATION_METRIC_ASSERTION_FAILURE_MESSAGE = "Assertion failed for: ";

    private static final Logger LOGGER = LoggerFactory.getLogger(AssertAssistant.class);

    @Inject
    private MoDpsController moDpsController;
    @Inject
    private InstrumentationAssistant instrumentationAssistant;
    @Inject
    private JmsAssistant jmsAssistant;

    @EJB
    private NodeDpsController nodeDpsController;
    @EJB(lookup = MociCMConnectionProvider.VERSION_INDEPENDENT_JNDI_NAME)
    private MociCMConnectionProvider mociCmConnectionProvider;

    public void assertSyncStatus(final TestNode node, final String expectedSyncStatus) {
        final List<TestNode> nodes = new ArrayList<>();
        nodes.add(node);
        assertSyncStatus(nodes, expectedSyncStatus);
    }

    public void assertSyncStatus(final List<TestNode> nodes, final String expectedSyncStatus) {
        for (final TestNode node : nodes) {
            final String nodeName = node.getName();
            final String cmSupervisionFdn = FdnUtil.getCmNodeHeartbeatSupervisionFdn(nodeName);

            final String syncStatus = nodeDpsController.getSyncStatus(nodeName);
            LOGGER.debug("Sync status of '{}' is '{}'.", cmSupervisionFdn, syncStatus);
            assertEquals("Node '" + cmSupervisionFdn + "' is not " + expectedSyncStatus + ".", expectedSyncStatus, syncStatus);
        }
    }

    public void assertGenerationCounterInSync(final TestNode node) throws MociConnectionProviderException {
        final String nodeName = node.getName();

        final ConnectionConfig connectionConfig = new ConnectionConfig(node.getIpAddress());
        final long nodeGenerationCounder = mociCmConnectionProvider.getGenerationCounter(connectionConfig);

        final long currentDpsGenerationCounter = moDpsController.getAttribute(FdnUtil.getCppCiFdn(nodeName), GENERATION_COUNTER_ATTR_NAME);

        LOGGER.debug("Node/DPS Generation Counters: [{}] / [{}]", nodeGenerationCounder, currentDpsGenerationCounter);
        assertEquals(nodeGenerationCounder, currentDpsGenerationCounter);
    }

    public void assertElementsCount(final int expectedElementsCount, final int actualElementsCount) {
        LOGGER.debug("Expected/Actual elements count: [{}] / [{}]", expectedElementsCount, actualElementsCount);
        assertEquals("Attribute's elemnt count is diffrent than expected.", expectedElementsCount, actualElementsCount);
    }

    public void assertNodeModelVersion(final String nodeName, final String nodeModelVersionPrefix) {
        final String managedElementMoDpsVersion = moDpsController.getVersion(FdnUtil.getManagedElementFdn(nodeName));
        LOGGER.debug("Version of {} in DPS: '{}'", MANAGED_ELEMENT_MO_TYPE, managedElementMoDpsVersion);
        assertTrue(managedElementMoDpsVersion.startsWith(nodeModelVersionPrefix));
    }

    public void assertOssModelIdentity(final String nodeName, final String expectedOssModelIdentity) {
        assertOssModelIdentity(nodeName, Arrays.asList(expectedOssModelIdentity));
    }

    public void assertOssModelIdentity(final String nodeName, final Collection<String> expectedOssModelIdentities) {
        assertModelIdentity(OSS_MODEL_IDENTITY_ATTR_NAME, nodeName, expectedOssModelIdentities);
    }

    public void assertNodeModelIdentity(final String nodeName, final String expectedNodeModelIdentity) {
        assertNodeModelIdentity(nodeName, Arrays.asList(expectedNodeModelIdentity));
    }

    public void assertNodeModelIdentity(final String nodeName, final Collection<String> expectedNodeModelIdentities) {
        assertModelIdentity(NODE_MODEL_IDENTITY_ATTR_NAME, nodeName, expectedNodeModelIdentities);
    }

    public void assertUtcOffset(final String nodeName, final String utcOffset) {
        final String networkElementFdn = FdnUtil.getNetworkElementFdn(nodeName);
        final String actualUtcOffset = moDpsController.getAttribute(networkElementFdn, NE_UTC_OFFSET_ATTR_NAME);

        LOGGER.debug("Expected/Actual values of {} in DPS: '{}' / '{}'", NE_UTC_OFFSET_ATTR_NAME, utcOffset,
                actualUtcOffset);

        assertEquals(utcOffset, actualUtcOffset);
    }

    public void assertFailedSyncsCount(final String nodeName, final int expectedFailedSyncsCount) {
        final String cmFunctionFdn = FdnUtil.getCmFunctionFdn(nodeName);
        final int actualFailedSyncsCount = moDpsController.getAttribute(cmFunctionFdn, FAILED_SYNCS_COUNT_ATTR_NAME);
        LOGGER.debug("Expected/Actual values of {} in DPS: '{}' / '{}'", FAILED_SYNCS_COUNT_ATTR_NAME, expectedFailedSyncsCount,
                actualFailedSyncsCount);
        assertEquals(expectedFailedSyncsCount, actualFailedSyncsCount);
    }

    public void assertInstrumentationMetric(final String metricName, final long expectedValue, final String mBeanName) {
        final long actualValue = instrumentationAssistant.getInstrumentationAttributeValue(mBeanName, metricName);
        LOGGER.debug("Expected/Actual metric '{}' value: [{}] / [{}]", metricName, expectedValue, actualValue);
        assertEquals(INSTRUMENTATION_METRIC_ASSERTION_FAILURE_MESSAGE + metricName, expectedValue, actualValue);
    }

    public void assertInstrumentationMetricNotZero(final String metricName, final String mBeanName) {
        final long actualValue = instrumentationAssistant.getInstrumentationAttributeValue(mBeanName, metricName);
        final boolean metricNotZero = actualValue == 0 ? false : true;
        LOGGER.debug("Is metric '{}' not zero? [{}]", metricName, metricNotZero);
        assertTrue(INSTRUMENTATION_METRIC_ASSERTION_FAILURE_MESSAGE + metricName, metricNotZero);
    }

    public void assertDpsMoAttribute(final String fdn, final String attributeName, final Object expectedValue) {
        final Object actualValue = moDpsController.getAttribute(fdn, attributeName);
        LOGGER.debug("Expected/Actual MO attribute '{}' value: [{}] / [{}]", attributeName, expectedValue, actualValue);
        assertEquals(expectedValue, actualValue);
    }

    public void assertDpsMoAttribute(final String fdn, final String attributeName, final Object expectedValue, final SoftAssertions assertionGroup) {
        final Object actualValue = moDpsController.getAttribute(fdn, attributeName);
        LOGGER.debug("Expected/Actual MO attribute '{}' value: [{}] / [{}]", attributeName, expectedValue, actualValue);

        assertionGroup.assertThat(actualValue).as("MO attribute is what we expect").isEqualTo(expectedValue);
    }

    public void assertNotificationSentToCmDataChangeQueue(final String queue, final NotificationType notificationType) {
        final SoftAssertions assertionGroup = new SoftAssertions();
        final NodeNotification actualValue = jmsAssistant.getLastMessageFromQueue(queue);
        final NotificationType action = actualValue.getAction();
        LOGGER.debug("Expected/Actual Notification Type value: [{}] / [{}]", notificationType, action);

        assertionGroup.assertThat(action).as("Notification found in CmDataChange Queue ").isEqualTo(notificationType);
    }

    public void assertNotification(final TestNode node, final String moName, final String attrName, final String attrValue)
            throws MociConnectionProviderException {
        assertGenerationCounterInSync(node);
        assertDpsMoAttribute(moName, attrName, attrValue);
    }

    private void assertModelIdentity(final String modelIdentityAttrName, final String nodeName, final Collection<String> expectedModelIdentities) {
        final String networkElementFdn = FdnUtil.getNetworkElementFdn(nodeName);
        final String actualModelIdentity = moDpsController.getAttribute(networkElementFdn, modelIdentityAttrName);
        boolean doesMatch = false;
        LOGGER.debug("Actual value of {} in DPS: '{}'", modelIdentityAttrName, actualModelIdentity);

        for (final String expectedModelIdentity : expectedModelIdentities) {
            if (expectedModelIdentity.equals(actualModelIdentity)) {
                LOGGER.debug("Expected value: '{}' matches actual", expectedModelIdentity);
                doesMatch = true;
                break;
            } else {
                LOGGER.debug("Expected value: '{}' does not match actual", expectedModelIdentity);
            }
        }
        assertTrue(doesMatch);

    }

}
