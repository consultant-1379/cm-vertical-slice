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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.VS_TEST_DEPLOYMENT_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.NOTIFICATION_PROCESS_TIME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SYNC_TIMEOUT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.GANDALF_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNC_STATUS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.UNSYNCHRONIZED_SYNC_STATUS_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.USER_LABEL_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.DEFAULT_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.SCTPPROFILE_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_OSS_MODEL_IDENTITY;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.assertj.core.api.SoftAssertions;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.mediation.cm.vertical.slice.common.SyncTriggerType;
import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.JmsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.DpsNotificationListener;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.PibConfigurationUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;
import com.ericsson.oss.mediation.network.api.notifications.NotificationType;

@RunWith(Arquillian.class)
public class NotificationIT extends DeploymentTest {

    private static int notifBufferSizeOriginalValue;
    private static final String CM_DATA_CHANGE_Q = "queue/CmDataChangeQueue-amq";
    private boolean resetBufferSize;

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private NetsimOperator netsimOperator;
    @Inject
    private AssertAssistant assertAssistant;
    @Inject
    private DpsNotificationListener dpsNotificationListener;
    @Inject
    private JmsAssistant jmsAssistant;

    @EJB
    private NodeDpsController nodeDpsController;
    @EJB
    private MoDpsController moDpsController;

    @BeforeClass
    public static void classSetUp() throws IOException {
        notifBufferSizeOriginalValue = PibConfigurationUtil.getNotifBufferSizePerNode();
    }

    private TestNode node;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(NotificationIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                node = netsimOperator.createTestNode(ERBS_16B_NODE_NAME, ERBS_16B_OSS_MODEL_IDENTITY);
                nodeDpsController.deleteNode(node);
                nodeDpsController.addNode(node);
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(NotificationIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (node != null) {
                    nodeDpsController.deleteNode(node);
                }
                if (resetBufferSize) {
                    PibConfigurationUtil.updateNotifBufferSizePerNode(notifBufferSizeOriginalValue);
                    resetBufferSize = false;
                }
            }

        }.start();
    }

    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void singleUpdateNotification() {
        new SyncTestCase("Single Update Notification") {

            @Override
            public void execute() throws Exception {
                final String sctpFdn = FdnUtil.getSctpNodeFdn(DEFAULT_MO_NAME);
                final String sctpFdnWithOssPrefix = FdnUtil.prependMeContextToFdn(node.getName(), sctpFdn);

                final SoftAssertions assertionGroup = new SoftAssertions();

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                netsimOperator.setTestNode(node);

                netsimOperator.updateAttribute(sctpFdn, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE);
                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertDpsMoAttribute(sctpFdnWithOssPrefix, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE, assertionGroup);

                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertNotificationSentToCmDataChangeQueue(CM_DATA_CHANGE_Q, NotificationType.UPDATE);

                netsimOperator.updateAttribute(sctpFdn, USER_LABEL_ATTR_NAME, "");
                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertDpsMoAttribute(sctpFdnWithOssPrefix, USER_LABEL_ATTR_NAME, "", assertionGroup);

                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertNotificationSentToCmDataChangeQueue(CM_DATA_CHANGE_Q, NotificationType.UPDATE);

                assertionGroup.assertAll();
            }

        }.start();
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void singleCreateDeleteNotification() {
        new SyncTestCase("Single Create Delete Notification") {

            @Override
            public void execute() throws Exception {
                final String sctpProfileFdn = FdnUtil.getSctpProfileNodeFdn(DEFAULT_MO_NAME);
                final String sctpProfileFdnWithOssPrefix = FdnUtil.prependMeContextToFdn(node.getName(), sctpProfileFdn);
                final String sctpProfileParentFdn = FdnUtil.getParentFdn(sctpProfileFdn);

                netsimOperator.deleteMo(sctpProfileFdn);

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                netsimOperator.setTestNode(node);

                // preliminary checks
                assertFalse("Expeted that mo does not exist on the node: " + sctpProfileFdnWithOssPrefix,
                        moDpsController.moExists(sctpProfileFdnWithOssPrefix));

                netsimOperator.addMo(sctpProfileParentFdn, SCTPPROFILE_MO_TYPE, DEFAULT_MO_NAME);
                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertTrue("Expected that mo exists on the node " + sctpProfileFdnWithOssPrefix,
                        moDpsController.moExists(sctpProfileFdnWithOssPrefix));

                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertNotificationSentToCmDataChangeQueue(CM_DATA_CHANGE_Q, NotificationType.CREATE);

                netsimOperator.deleteMo(sctpProfileFdn);
                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertFalse("Expeted that mo does not exist on the node: " + sctpProfileFdnWithOssPrefix,
                        moDpsController.moExists(sctpProfileFdnWithOssPrefix));

                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertNotificationSentToCmDataChangeQueue(CM_DATA_CHANGE_Q, NotificationType.DELETE);

                assertAssistant.assertGenerationCounterInSync(node);
            }

        }.start();
    }

    @Test
    @InSequence(3)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void overflowNotification() {
        new SyncTestCase("Overflow Notification") {

            @Override
            public void execute() throws Exception {

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);

                dpsNotificationListener.startListening();
                netsimOperator.setTestNode(node);
                netsimOperator.sendOverflowNotification();
                TimeUtil.sleep(SYNC_TIMEOUT);

                final boolean isAttributeModified = dpsNotificationListener.isAttributeModifiedWithValue(SYNC_STATUS_ATTR_NAME,
                        UNSYNCHRONIZED_SYNC_STATUS_ATTR_VALUE);
                dpsNotificationListener.stopListening();
                assertTrue("Expected that sync status changed temporarily to UNSYNCHRONIZED after receiving Overflow notification.",
                        isAttributeModified);

                assertAssistant.assertSyncStatus(node, SYNCHRONIZED_SYNC_STATUS_ATTR_VALUE);
            }

        }.start();
    }


    @Test
    @InSequence(4)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void singleUpdateNotificationWhenSendEventFails() {
        new SyncTestCase("Single Update Notification when Send Event to CmDataChange Queue fails") {

            @Override
            public void execute() throws Exception {
                final String sctpFdn = FdnUtil.getSctpNodeFdn(DEFAULT_MO_NAME);
                final String sctpFdnWithOssPrefix = FdnUtil.prependMeContextToFdn(node.getName(), sctpFdn);

                final SoftAssertions assertionGroup = new SoftAssertions();

                syncAssistant.syncNodeAndAssertSynchronized(node, SYNC_TIMEOUT, SyncTriggerType.SUPERVISION);
                netsimOperator.setTestNode(node);

                // Fill the CmDataChange JMS Queue.
                final boolean isQueueFull = jmsAssistant.fillJmsQueue(CM_DATA_CHANGE_Q);

                assertTrue("CmDataChangeQue is not full", isQueueFull);

                //AVC notification should get persist to DPS even when the sendEvent method will fail due to ADDRESS_FULL Jms Exception
                netsimOperator.updateAttribute(sctpFdn, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE);
                TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
                assertAssistant.assertDpsMoAttribute(sctpFdnWithOssPrefix, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE, assertionGroup);

                // Clean up, empty the CmDataChange JMS Queue.
                final boolean isQueueEmpty = jmsAssistant.emptyJmsQueue(CM_DATA_CHANGE_Q);

                assertTrue("CmDataChangeQue is not empty", isQueueEmpty);

                assertionGroup.assertAll();
            }

        }.start();
    }

}
