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
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.RNC_NETWORK_ELEMENT_TYPE_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.RNC_16B_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.RNC_16B_OSS_MODEL_IDENTITY;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.SyncTriggerType;
import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;

/**
 * Large RNC Sync end-to-end test suite.
 */
@RunWith(Arquillian.class)
public class LargeRncSyncIT extends DeploymentTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(LargeRncSyncIT.class);

    @Inject
    private NetsimOperator netsimOperator;

    @Inject
    private SyncAssistant syncAssistant;

    @EJB
    private NodeDpsController nodeDpsController;

    private TestNode node;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(LargeRncSyncIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                node = netsimOperator.createTestNode(RNC_16B_NODE_NAME, RNC_16B_OSS_MODEL_IDENTITY);
                node.setNeType(RNC_NETWORK_ELEMENT_TYPE_ATTR_VALUE);
                netsimOperator.setTestNode(node);

                setupNodeForTest();

                System.setProperty("com.ericsson.oss.cpp.largenodes", "RNC,MGW");

            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(LargeRncSyncIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (node != null) {
                    nodeDpsController.deleteNode(node);
                }

                System.setProperty("com.ericsson.oss.cpp.largenodes", "");
            }

        }.start();
    }

    /**
     * Standard Delta Sync test. Creates an SCTP MO on the node, updates it and deletes it. Afterwards creates another SCTP MO and updates it, without
     * deleting it. In the end the node is being delta-synced.
     */
    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void largeRncSync() {
        new SyncTestCase("Large RNC Sync") {

            @Override
            public void execute() throws Exception {
                syncAssistant.syncNodeAndAssertSynchronized(node, 5 * SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
            }

        }.start();
    }

    private void setupNodeForTest() {
        syncAssistant.deleteAndAddNode(node);
    }

}
