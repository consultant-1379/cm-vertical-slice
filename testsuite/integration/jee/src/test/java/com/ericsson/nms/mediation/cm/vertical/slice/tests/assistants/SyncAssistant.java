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

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.DELTA_SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SUPERVISION_OFF_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ACTIVE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.UNSYNCHRONIZED_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.DUMMY_MO_NAME;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.SyncTriggerType;
import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;
import com.ericsson.oss.mediation.network.api.MociCMConnectionProvider;

/**
 * Test helper class.<br>
 * Provides ways to sync and (un)supervise a node and query for sync-related states.
 */
public class SyncAssistant {

    private static final int MONITOR_SYNC_STATUS_INTERVAL = 1 * 500;

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncAssistant.class);

    @Inject
    private NetsimAssistant netsimAssistant;

    @EJB
    private NodeDpsController nodeDpsController;
    @EJB
    private MoDpsController moDpsController;
    @EJB(lookup = MociCMConnectionProvider.VERSION_INDEPENDENT_JNDI_NAME)
    private MociCMConnectionProvider mociCmConnectionProvider;

    public void syncNode(final TestNode node, final SyncTriggerType syncType, final int waitTime) {
        initiateSync(node.getName(), syncType);
        TimeUtil.sleep(waitTime);
    }

    public void syncNodeAndAssertSynchronized(final TestNode node, final long timeout, final SyncTriggerType syncType) {
        final long startTime = System.currentTimeMillis();
        final String nodeName = node.getName();
        LOGGER.info("============== => Starting to Sync node: '{}'... <= ==============", nodeName);

        initiateSync(nodeName, syncType);

        final boolean isSyncStatusSynchronized = monitorUntilDesiredSyncStatus(nodeName, SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE, timeout);
        assertTrue(constructSyncFailedLogLine(nodeName), isSyncStatusSynchronized);

        final long timeTaken = System.currentTimeMillis() - startTime;
        LOGGER.info("============== => Finished Syncing node: '{}' (took [{}] ms). <= ==============", nodeName, timeTaken);
    }

    public void syncNodesParallelyAndAssertSynchronized(final List<TestNode> nodes) {
        final List<String> nodeNames = new ArrayList<>();
        for (final TestNode node : nodes) {
            final String nodeName = node.getName();
            nodeDpsController.sync(nodeName);
            nodeNames.add(nodeName);
        }

        final boolean areAllSyncStatusesSynchronized = monitorUntilDesiredSyncStatus(nodeNames, SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE, SYNC_TIMEOUT);
        assertTrue(constructSyncFailedLogLine(nodeNames.toString()), areAllSyncStatusesSynchronized);
    }

    public void turnSupervisionOffAndAssertUnsynchronized(final String nodeName) {
        LOGGER.debug("Turning Supervision Off for '{}'...", nodeName);

        moDpsController.setAttribute(FdnUtil.getCmNodeHeartbeatSupervisionFdn(nodeName), ACTIVE_ATTR_NAME, false);
        final boolean isSyncStatusUnsynchronized = monitorUntilDesiredSyncStatus(nodeName, UNSYNCHRONIZED_SYNC_STATUS_ATTR_VALUE,
                SUPERVISION_OFF_TIMEOUT);
        assertTrue("Turning Supervision Off for '" + nodeName + "' has failed.", isSyncStatusUnsynchronized);

        LOGGER.debug("Turned SUPERVISION OFF for '{}'.", nodeName);
    }

    /**
     * Delta-syncs a node by unsubscribing it, creating a new MO and an attribute on the associated NetSim and triggering a sync.
     *
     * @param node
     *            <code>TestNode</code> to be delta-synced
     */
    public void deltasyncNode(final TestNode node) {
        turnSupervisionOffAndAssertUnsynchronized(node.getName());
        netsimAssistant.setTestNode(node);
        netsimAssistant.deleteMoOnNode(FdnUtil.getSctpNodeFdn(DUMMY_MO_NAME));
        netsimAssistant.manipulateSctpMoOnNode(DUMMY_MO_NAME);

        syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
    }

    /**
     * Delta-syncs nodes by unsubscribing them, creating a new MO and an attribute on the associated NetSims and triggering a sync.
     *
     * @param nodes
     *            collection of <code>TestNode</code> instances to be delta-synced
     */
    public void deltasyncNodes(final List<TestNode> nodes) {
        for (final TestNode node : nodes) {
            deltasyncNode(node);
        }
    }

    /**
     * Prepares node for test. Deletes, adds and syncs the <code>node</codes> passed.
     *
     * @param node
     *            <code>TestNode</code> to be setup
     */
    public void deleteAddAndSyncNode(final TestNode node) {
        deleteAndAddNode(node);
        syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
    }

    /**
     * Prepares node for test. Deletes and adds the <code>node</codes> passed.
     *
     * @param node
     *            <code>TestNode</code> to be setup
     */
    public void deleteAndAddNode(final TestNode node) {
        nodeDpsController.deleteNode(node);
        nodeDpsController.addNode(node);
    }

    /**
     * Prepares nodes for test. Deletes, adds and syncs the <code>nodes</codes> passed.
     *
     * @param nodes
     *            collection of <code>TestNode</code> instances to be setup
     */
    public void deleteAddAndSyncNodes(final List<TestNode> nodes) {
        for (final TestNode node : nodes) {
            deleteAddAndSyncNode(node);
        }
    }

    public boolean monitorUntilDesiredSyncStatus(final String nodeName, final String desiredSyncStatus, final long timeout) {
        final List<String> nodeNames = new ArrayList<>();
        nodeNames.add(nodeName);
        return monitorUntilDesiredSyncStatus(nodeNames, desiredSyncStatus, timeout);
    }

    public boolean monitorUntilDesiredSyncStatus(final List<String> nodeNames, final String desiredSyncStatus, final long timeout) {
        final long startTime = System.currentTimeMillis();
        boolean isDesiredStatus = false;

        while (!isDesiredStatus && isTimeoutNotReached(startTime, timeout)) {
            final Map<String, String> currentSyncStatuses = nodeDpsController.getSyncStatuses();

            isDesiredStatus = true;
            for (final String nodeName : nodeNames) {
                final String cmFunctionFdn = FdnUtil.getCmFunctionFdn(nodeName);
                if (!desiredSyncStatus.equals(currentSyncStatuses.get(cmFunctionFdn))) {
                    isDesiredStatus = false;
                }
            }

            TimeUtil.sleep(MONITOR_SYNC_STATUS_INTERVAL);
        }

        return isDesiredStatus;
    }

    private void initiateSync(final String nodeName, final SyncTriggerType syncType) {
        switch (syncType) {
            case SUPERVISION:
                nodeDpsController.sync(nodeName);
                break;

            case ACTION:
                nodeDpsController.syncViaAction(nodeName);
                break;

            default:
                LOGGER.error("'{}' sync type is not supported! No sync initiated!", syncType);
                fail();
        }
    }

    private boolean isTimeoutNotReached(final long startTime, final long timeout) {
        final long deltaTime = System.currentTimeMillis() - startTime;
        return deltaTime < timeout;
    }

    private String constructSyncFailedLogLine(final String nodeNames) {
        final StringBuilder logLine = new StringBuilder("Sync of '");
        logLine.append(nodeNames);
        logLine.append("' failed.");

        return logLine.toString();
    }

}
