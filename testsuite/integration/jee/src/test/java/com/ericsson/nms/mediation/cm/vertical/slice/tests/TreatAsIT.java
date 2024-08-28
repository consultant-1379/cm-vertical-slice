/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.tests;

import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.VS_TEST_DEPLOYMENT_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.DELTA_SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CABINET_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16A_TREAT_AS_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_TREAT_AS_OSS_MODEL_IDENTITY;

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
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.DpsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.NetsimAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.TreatAsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

// TODO Look at the performance and clean up issues (mostly around creating/cleaning Cabinet MOs)
/**
 * Treat-As test suite. Syncs and triggers notifications for an unsupported node, treating it as a supported node version.
 */
@RunWith(Arquillian.class)
public class TreatAsIT extends DeploymentTest {

    @Inject
    private TreatAsAssistant treatAsAssistant;
    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private NetsimOperator netsimOperator;
    @Inject
    private AssertAssistant assertAssistant;
    @Inject
    private NetsimAssistant netsimAssistant;
    @Inject
    private DpsAssistant dpsAssistant;

    @EJB
    private NodeDpsController nodeDpsController;

    private TestNode node;
    private String nodeName;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(TreatAsIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                node = netsimOperator.createTestNode(ERBS_16A_TREAT_AS_NODE_NAME, ERBS_TREAT_AS_OSS_MODEL_IDENTITY);
                nodeName = node.getName();

                netsimOperator.setTestNode(node);
                nodeDpsController.deleteNode(node);
                nodeDpsController.addNode(node);
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(TreatAsIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (node != null) {
                    nodeDpsController.deleteNode(node);
                }
                treatAsAssistant.resetNetsimValues();
            }

        }.start();
    }

    /**
     * Syncs an unsupported G.1.132 node version as a G.1.124 Mirror version.
     */
    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void treatAsFullSyncForUnsupportedNodeVersionCompletesSuccessfully() {
        new SyncTestCase("Full Treat-As Sync") {

            @Override
            public void execute() throws Exception {
                treatAsAssistant.performBackwardCompatibleChangesOnNode(nodeName);
                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                treatAsAssistant.assertTreatAsBehaviour(nodeName);
            }

        }.start();
    }

    /**
     * Delta sync an unsupported G.1.132 node version as a G.1.124 Mirror version. Test then asserts that only supported MOs / attributes are
     * persisted to DPS.
     */
    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void treatAsDeltaSyncForUnsupportedNodeVersionCompletesSuccessfully() {
        new SyncTestCase("Delta Sync Treat-As with updates to unsupported MOs / attributes") {

            @Override
            public void execute() throws Exception {
                syncAssistant.deleteAddAndSyncNode(node);
                syncAssistant.turnSupervisionOffAndAssertUnsynchronized(nodeName);

                treatAsAssistant.performBackwardCompatibleChangesOnNode(nodeName);
                syncAssistant.syncNodeAndAssertSynchronized(node, DELTA_SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                treatAsAssistant.assertTreatAsBehaviour(nodeName);
            }

        }.start();
    }

    /**
     * Syncs an unsupported G.1.132 node version as a G.1.124 Mirror version and then trigger notifications to assert the correct behaviour.
     */
    @Test
    @InSequence(3)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void notificatonsForUnsupportedNodeVersionReceivedSuccessfully() {
        new SyncTestCase("Treat-As notifications with updates to unsupported MOs / attributes") {

            @Override
            public void execute() throws Exception {
                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                final String notificationCabinetMoNameSuffix = "_Notification";
                netsimAssistant.createCabinetMoOnNode(CABINET_MO_NAME + notificationCabinetMoNameSuffix);
                treatAsAssistant.performBackwardCompatibleChangesOnNode(nodeName);

                treatAsAssistant.assertTreatAsBehaviour(nodeName);
                assertTrue(dpsAssistant.moExists(nodeName, FdnUtil.getCabinetMoFdn(CABINET_MO_NAME) + notificationCabinetMoNameSuffix));
                assertAssistant.assertGenerationCounterInSync(node);

                netsimAssistant.deleteCabinetMoOnMode(CABINET_MO_NAME + notificationCabinetMoNameSuffix);
            }

        }.start();
    }

}
