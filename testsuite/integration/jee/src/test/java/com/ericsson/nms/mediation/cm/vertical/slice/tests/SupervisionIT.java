/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.VS_TEST_DEPLOYMENT_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.CM_ROUTER_POLICY_INSTRUMENTATION_BEAN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.CM_ROUTER_POLICY_NUMBER_OF_SUPERVISED_NODES_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.DELTA_SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ACTIVE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ATTRIBUTE_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DELTA_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.LOST_SYNCHRONIZATION_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.PENDING_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNC_STATUS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TOPOLOGY_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_OSS_MODEL_IDENTITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.SGSN_15B_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.SGSN_15B_OSS_MODEL_IDENTITY;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.mediation.cm.vertical.slice.common.SyncTriggerType;
import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.PibConfigurationUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;

/**
 * Supervision test suite.
 */
@RunWith(Arquillian.class)
public class SupervisionIT extends DeploymentTest {

    private static final int HEARTBEAT_ORIGINAL_INTERVAL = 60000 * 7;
    private static final int HEARTBEAT_SHORT_INTERVAL = 15000;
    private static final int HEARTBEAT_VERY_SHORT_INTERVAL = 3000;
    private static final int HEARTBEAT_SHORT_RETRY_COUNT = 1;
    private static final int HEARTBEAT_ORIGINAL_RETRY_COUNT = 10;

    private static final String[] INTERMEDIATE_SYNC_STATUSES = new String[] { PENDING_SYNC_STATUS_ATTR_VALUE, TOPOLOGY_SYNC_STATUS_ATTR_VALUE,
        ATTRIBUTE_SYNC_STATUS_ATTR_VALUE, DELTA_SYNC_STATUS_ATTR_VALUE };

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private AssertAssistant assertAssistant;
    @Inject
    private NetsimOperator netsimOperator;

    @EJB
    private NodeDpsController nodeDpsController;
    @EJB
    private MoDpsController moDpsController;

    private List<TestNode> nodes;

    private TestNode nodeToRestart;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(SupervisionIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                final String simname = netsimOperator.getSimulationForNode(ERBS_17A_NODE_NAME);
                nodes = netsimOperator.getNodesFromSimulation(5, simname, ERBS_17A_OSS_MODEL_IDENTITY);
                nodeDpsController.deleteNodes(nodes);
                nodeDpsController.addNodes(nodes);
                nodeToRestart = null;
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(SupervisionIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (nodes != null) {
                    nodeDpsController.deleteNodes(nodes);
                }
                PibConfigurationUtil.updateHeartbeatRetryCount(HEARTBEAT_ORIGINAL_RETRY_COUNT);
                PibConfigurationUtil.updateHearbeatIntervalTime(HEARTBEAT_ORIGINAL_INTERVAL);
                if (nodeToRestart != null) {
                    netsimOperator.startNode();
                }
            }

        }.start();
    }

    /**
     * Sync Status Validator test.
     * <p>
     * Simulates a scenario where there is a mixture of nodes, some of which are hanging in an intermediate sync status.
     * After a period of 2 heartbeats the hanging nodes are verified if they have been resynchronized.
     */
    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void validatesSyncStatuses() {
        new SyncTestCase("Sync Status Validator") {

            @Override
            public void execute() throws Exception {
                syncAssistant.syncNodesParallelyAndAssertSynchronized(nodes);
                setIntermediateSyncStatusForTestNodes();

                PibConfigurationUtil.updateHeartbeatRetryCount(HEARTBEAT_SHORT_RETRY_COUNT);
                PibConfigurationUtil.updateHearbeatIntervalTime(HEARTBEAT_SHORT_INTERVAL);

                TimeUtil.sleep(HEARTBEAT_SHORT_INTERVAL + DELTA_SYNC_TIMEOUT);
                assertAssistant.assertSyncStatus(nodes, SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE);
            }

        }.start();
    }

    /**
     * Simulates a scenario where a node has lost connectivity for a few seconds when subscription validation occurs.<br>
     * Expected behavior is to retry subscription validation once, after the initial failure. Also, {@code CmFunction.lostSyncrhonization} is expected
     * to be updated when retrying to validate subscription, as well as after the connection to the node is re-established.
     * <p>
     * <b>Note:</b> Exceptions are expected to thrown as part of this test case.
     */
    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void retriesSubscriptionAfterFailure() {
        new SyncTestCase("Subscription Retry After Failure") {

            @Override
            public void execute() throws Exception {
                final TestNode node = nodes.get(0);
                final String nodeName = node.getName();
                final String cmFunctionFdn = FdnUtil.getCmFunctionFdn(nodeName);
                netsimOperator.setTestNode(node);
                nodeToRestart = node;

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                assertNull(moDpsController.getAttribute(cmFunctionFdn, LOST_SYNCHRONIZATION_ATTR_NAME));

                netsimOperator.stopNode();

                PibConfigurationUtil.updateHeartbeatRetryCount(HEARTBEAT_SHORT_RETRY_COUNT);
                PibConfigurationUtil.updateHearbeatIntervalTime(HEARTBEAT_SHORT_INTERVAL);
                TimeUtil.sleep(HEARTBEAT_SHORT_INTERVAL);

                assertNotNull(moDpsController.getAttribute(cmFunctionFdn, LOST_SYNCHRONIZATION_ATTR_NAME));

                netsimOperator.startNode();
                TimeUtil.sleep(HEARTBEAT_SHORT_INTERVAL * 2);
                assertNull(moDpsController.getAttribute(cmFunctionFdn, LOST_SYNCHRONIZATION_ATTR_NAME));
            }

        }.start();
    }

    /**
     * Verifies that non-CPP nodes are not supervised by CM Router Policy.
     * <p>
     * Adds a non-CPP node and a bunch of CPP nodes. Waits for a couple of CM Heartbeats and checks if the number of supervised nodes is equal to the
     * number of CPP nodes, and also if the non-CPP node's active attribute is still true.
     */
    @Test
    @InSequence(3)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void supervisesOnlyCppNodes() {
        new SyncTestCase("Supervise Only CPP Nodes") {

            @Override
            public void execute() throws Exception {
                final TestNode nonCppNode = netsimOperator.createTestNode(SGSN_15B_NODE_NAME, SGSN_15B_OSS_MODEL_IDENTITY);
                deleteAddAndActivateNonCppNode(nonCppNode);

                syncAssistant.syncNodesParallelyAndAssertSynchronized(nodes);

                PibConfigurationUtil.updateHearbeatIntervalTime(HEARTBEAT_VERY_SHORT_INTERVAL);
                TimeUtil.sleep(2 * HEARTBEAT_VERY_SHORT_INTERVAL);

                assertAssistant.assertInstrumentationMetric(CM_ROUTER_POLICY_NUMBER_OF_SUPERVISED_NODES_METRIC, nodes.size(),
                        CM_ROUTER_POLICY_INSTRUMENTATION_BEAN);
                final boolean activeAttrValue =
                        moDpsController.getAttribute(FdnUtil.getCmNodeHeartbeatSupervisionFdn(nonCppNode.getName()), ACTIVE_ATTR_NAME);
                assertTrue(activeAttrValue);
            }

        }.start();
    }

    private void setIntermediateSyncStatusForTestNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            final String intermediateSyncStatus = INTERMEDIATE_SYNC_STATUSES[i % INTERMEDIATE_SYNC_STATUSES.length];
            final String nodeName = nodes.get(i).getName();
            moDpsController.setAttribute(FdnUtil.getCmFunctionFdn(nodeName), SYNC_STATUS_ATTR_NAME, intermediateSyncStatus);
        }
    }

    private void deleteAddAndActivateNonCppNode(final TestNode node) {
        node.setPlatformType(null);
        nodeDpsController.deleteNode(node);
        nodeDpsController.addNode(node);
        syncAssistant.syncNode(node, SyncTriggerType.SUPERVISION, 2000); // Not expected to sync...
    }

}
