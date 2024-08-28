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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.VS_TEST_DEPLOYMENT_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.DELTA_SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ATTRIBUTE_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.EUTRAN_CELL_POLYGON_ATTR_MODIFY_COMMAND_POSTFIX;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.EUTRAN_CELL_POLYGON_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.EUTRAN_CELL_POLYGON_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.GENERATION_COUNTER_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SECTOR_CARRIER_REF_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SECTOR_CARRIER_REF_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNC_STATUS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ARWEN_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.RIVENDELL_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_OSS_MODEL_IDENTITY;

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
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.DpsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.NetsimAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.DpsNotificationListener;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

// TODO Figure out why, when these tests run after other tests, the Delta Syncs that are occurring are not reading changes on the node and thus
// failing core Delta Sync test cases.

/**
 * Delta Sync end-to-end test suite.
 */
@RunWith(Arquillian.class)
public class DeltaSyncIT extends DeploymentTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeltaSyncIT.class);

    @Inject
    private NetsimOperator netsimOperator;

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private NetsimAssistant netsimAssistant;
    @Inject
    private DpsAssistant dpsAssistant;
    @Inject
    private AssertAssistant assertAssistant;

    @EJB
    private NodeDpsController nodeDpsController;
    @EJB
    private MoDpsController moDpsController;

    @Inject
    private DpsNotificationListener dpsNotificationListener;

    private TestNode node;
    private String nodeName;
    private String nodeMoToBeCleanedUp;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(DeltaSyncIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                node = netsimOperator.createTestNode(ERBS_17A_NODE_NAME, ERBS_17A_OSS_MODEL_IDENTITY);
                netsimOperator.setTestNode(node);
                nodeName = node.getName();

                setupNodeForTest();
                nodeMoToBeCleanedUp = null;
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(DeltaSyncIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (nodeMoToBeCleanedUp != null) {
                    netsimOperator.deleteMo(nodeMoToBeCleanedUp);
                }
                if (node != null) {
                    nodeDpsController.deleteNode(node);
                }
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
    public void deltaSync() {
        new SyncTestCase("Basic Delta Sync") {

            @Override
            public void execute() throws Exception {
                final String sctpMoFdn = FdnUtil.getSctpNodeFdn(ARWEN_MO_NAME);
                nodeMoToBeCleanedUp = sctpMoFdn;

                netsimOperator.deleteMo(sctpMoFdn);

                netsimAssistant.manipulateSctpMoOnNode(RIVENDELL_MO_NAME);
                netsimOperator.deleteMo(FdnUtil.getSctpNodeFdn(RIVENDELL_MO_NAME));
                netsimAssistant.manipulateSctpMoOnNode(ARWEN_MO_NAME);

                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                assertTrue(dpsAssistant.moExists(nodeName, sctpMoFdn));
                assertAssistant.assertGenerationCounterInSync(node);
            }

        }.start();
    }

    /**
     * Creates an SCTP MO on the node, updates it and deletes it. Afterwards the node is being delta-synced. Test then asserts if no SCTP MO has been
     * persisted to DPS.
     */
    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void deltaSyncCreateUpdateDeleteSingleMo() {
        new SyncTestCase("Delta Sync with 'Create-Update-Delete' updates for single MO") {

            @Override
            public void execute() throws Exception {
                final String auPortFdn = FdnUtil.getAuPortNodeFdn(RIVENDELL_MO_NAME);

                netsimOperator.deleteMo(auPortFdn);

                netsimAssistant.manipulateAuMoOnNode();
                netsimOperator.deleteMo(auPortFdn);
                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                assertFalse(dpsAssistant.moExists(nodeName, auPortFdn));
                assertAssistant.assertGenerationCounterInSync(node);
            }

        }.start();
    }

    /**
     * Creates an EUtranCellFdd MO on the node and updates a sequence-of-MO-Refs type of attribute. Node is delta-synced and asserted as usually.
     */
    @Test
    @InSequence(3)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void deltaSyncUpdateSeqMoRefs() {
        new SyncTestCase("Delta Sync with Sequence MO Refs") {

            @Override
            public void execute() throws Exception {
                final String eUtranCellTddFdn = FdnUtil.getEUtranCellTddNodeFdn(RIVENDELL_MO_NAME);
                nodeMoToBeCleanedUp = eUtranCellTddFdn;

                netsimOperator.deleteMo(eUtranCellTddFdn);

                netsimAssistant.addEUtranCellTddMoAndUpdateAttributesOnNode(SECTOR_CARRIER_REF_ATTR_NAME, SECTOR_CARRIER_REF_ATTR_VALUE);
                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                assertTrue(dpsAssistant.moExists(nodeName, eUtranCellTddFdn));
                assertAssistant.assertGenerationCounterInSync(node);

                final int EXPECTED_ELEMENTS_COUNT = 4;
                final int actualElementsCount = dpsAssistant.getElementsCountInAttribute(nodeName, eUtranCellTddFdn, SECTOR_CARRIER_REF_ATTR_NAME);
                assertAssistant.assertElementsCount(EXPECTED_ELEMENTS_COUNT, actualElementsCount);
            }

        }.start();
    }

    /**
     * Creates an EUtranCellFdd MO on the node and updates a sequence-of-structs type of attribute. Node is then delta-synced and asserted as usually.
     */
    @Test
    @InSequence(4)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void deltaSyncUpdateSeqStructs() {
        new SyncTestCase("Delta Sync with Sequence Structs Updates") {

            @Override
            public void execute() throws Exception {
                final String eUtranCellTddFdn = FdnUtil.getEUtranCellTddNodeFdn(RIVENDELL_MO_NAME);
                nodeMoToBeCleanedUp = eUtranCellTddFdn;

                netsimOperator.deleteMo(eUtranCellTddFdn);

                netsimAssistant.addEUtranCellTddMoAndUpdateAttributesOnNode(EUTRAN_CELL_POLYGON_ATTR_NAME + EUTRAN_CELL_POLYGON_ATTR_MODIFY_COMMAND_POSTFIX,
                        EUTRAN_CELL_POLYGON_ATTR_VALUE);
                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                assertTrue(dpsAssistant.moExists(nodeName, eUtranCellTddFdn));
                assertAssistant.assertGenerationCounterInSync(node);

                final int EXPECTED_ELEMENTS_COUNT = 2;
                final int actualElementsCount = dpsAssistant.getElementsCountInAttribute(nodeName, eUtranCellTddFdn, EUTRAN_CELL_POLYGON_ATTR_NAME);
                assertAssistant.assertElementsCount(EXPECTED_ELEMENTS_COUNT, actualElementsCount);
            }

        }.start();
    }

    /**
     * Delta Sync calls full sync when the difference between the Generation Counters on the Node and DPS is greater than a pre-defined threshold.
     */
    @Test
    @InSequence(5)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void deltaSyncCallsFullSync() {
        new SyncTestCase("Delta Sync calls Full Sync") {

            @Override
            public void execute() throws Exception {
                moDpsController.setAttribute(FdnUtil.getCppCiFdn(nodeName), GENERATION_COUNTER_ATTR_NAME, -1l);
                dpsNotificationListener.startListening();

                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT * 2, SyncTriggerType.SUPERVISION);

                final boolean isAttributeModified = dpsNotificationListener.isAttributeModifiedWithValue(SYNC_STATUS_ATTR_NAME,
                        ATTRIBUTE_SYNC_STATUS_ATTR_VALUE);
                dpsNotificationListener.stopListening();

                assertTrue("Sync Status was never modified to 'ATTRIBUTE' -> Full Sync was not invoked!", isAttributeModified);
            }

        }.start();
    }

    private void setupNodeForTest() {
        syncAssistant.deleteAddAndSyncNode(node);
        syncAssistant.turnSupervisionOffAndAssertUnsynchronized(nodeName);
    }

}
