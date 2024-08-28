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

package com.ericsson.nms.mediation.cm.vertical.slice.tests;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.VS_TEST_DEPLOYMENT_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SUPERVISION_FAILURE_WAIT_TIME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_FAILURE_WAIT_TIME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_OSS_MODEL_IDENTITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_OSS_MODEL_IDENTITY;

import java.util.ArrayList;
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
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;

/**
 * Standard Node Sync end-to-end test suite.
 */
@RunWith(Arquillian.class)
public class FullSyncIT extends DeploymentTest {

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private NetsimOperator netsimOperator;
    @Inject
    private AssertAssistant assertAssistant;

    @EJB
    private NodeDpsController nodeDpsController;

    private List<TestNode> nodes;
    private TestNode nodeToBeClean;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(FullSyncIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                nodes = getTestNodes();
                nodeDpsController.deleteNodes(nodes);
                nodeDpsController.addNodes(nodes);
                nodeToBeClean = null;
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(FullSyncIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (nodes != null) {
                    nodeDpsController.deleteNodes(nodes);
                }
                if (nodeToBeClean != null) {
                    netsimOperator.setTestNode(nodeToBeClean);
                    netsimOperator.startNode();
                }
            }

        }.start();
    }

    /**
     * Issues a sync of multiple nodes (of different versions) almost immediately, so that they are all being synchronized simultaneously.
     */
    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void syncNodesParallely() {
        new SyncTestCase("Parallel Full Sync") {

            @Override
            public void execute() throws Exception {
                syncAssistant.syncNodesParallelyAndAssertSynchronized(nodes);
            }

        }.start();
    }

    /**
     * Syncs multiple nodes (of different versions) sequentially, one by one.
     */
    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void syncNodes() {
        new SyncTestCase("Full Sync") {

            @Override
            public void execute() throws Exception {
                for (final TestNode node : nodes) {
                    syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                }
            }

        }.start();
    }

    /**
     * Syncs a single node and then re-syncs via action.
     */
    @Test
    @InSequence(3)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void syncNodeViaAction() {
        new SyncTestCase("Re-Sync using Action") {

            @Override
            public void execute() throws Exception {
                final TestNode node = nodes.get(0);

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.ACTION);
            }

        }.start();
    }

    /**
     * Syncs a single node and then attempts to re-sync it by starting supervision again. Because it's on already, nothing is expected to happen, and
     * the node should stay synchronized.
     */
    @Test
    @InSequence(4)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void reSyncNode() {
        new SyncTestCase("Re-Sync using Supervision On") {

            @Override
            public void execute() throws Exception {
                final TestNode node = nodes.get(1);

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
            }

        }.start();
    }

    @Test
    @InSequence(5)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void testSyncRetryAfterFailure() {
        new SyncTestCase("Sync Retry After Failure") {

            @Override
            public void execute() throws Exception {
                final TestNode node = nodes.get(0);
                nodeToBeClean = node;
                netsimOperator.setTestNode(node);
                netsimOperator.stopNode();

                syncAssistant.syncNode(node, SyncTriggerType.SUPERVISION, SUPERVISION_FAILURE_WAIT_TIME);
                syncAssistant.syncNode(node, SyncTriggerType.ACTION, 4 * SYNC_FAILURE_WAIT_TIME);
                assertAssistant.assertFailedSyncsCount(node.getName(), 2);

                netsimOperator.startNode();
                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.ACTION);
                assertAssistant.assertFailedSyncsCount(node.getName(), 0);
            }

        }.start();
    }

    private List<TestNode> getTestNodes() {
        final List<TestNode> nodes = new ArrayList<>();
        nodes.add(netsimOperator.createTestNode(ERBS_16B_NODE_NAME, ERBS_16B_OSS_MODEL_IDENTITY));
        nodes.add(netsimOperator.createTestNode(ERBS_17A_NODE_NAME, ERBS_17A_OSS_MODEL_IDENTITY));
        return nodes;
    }

}
