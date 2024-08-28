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
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_AVERAGE_NUMBER_OF_MOS_SYNCED_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_CONTROLLER_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_DPS_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_DPS_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_FAILED_SYNCS_COUNT_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_NODE_INFO_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_DELTA_SUCCESSFUL_SYNCS_COUNT_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_HANDLERS_INSTRUMENTATION_MBEAN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_MAX_OVERALL_SYNC_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_SUCCESSFUL_SYNCS_COUNT_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.DPS_TOPOLOGY_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_FAILED_SYNCS_COUNT_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_HANDLERS_INSTRUMENTATION_MBEAN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants.MOCI_TOPOLOGY_HANDLER_INVOCATIONS_METRIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.DUMMY_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_16B_OSS_MODEL_IDENTITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_NODE_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants.ERBS_17A_OSS_MODEL_IDENTITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.InstrumentationAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

/**
 * Instrumentation end-to-end test suite, testing MOCI and DPS Handlers Instrumentation.
 */
@RunWith(Arquillian.class)
public class InstrumentationIT extends DeploymentTest {

    @Inject
    private NetsimOperator netsimOperator;

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private InstrumentationAssistant instrumentationAssistant;
    @Inject
    private AssertAssistant assertAssistant;

    @EJB
    private NodeDpsController nodeDpsController;

    private int numberOfSyncsDuringSetup;
    private List<TestNode> nodes;

    @Before
    public void setUp() {
        new SyncTestCaseSetup(InstrumentationIT.class.getSimpleName(), SyncTestCaseSetup.SETUP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                nodes = new ArrayList<>();
                nodes.add(netsimOperator.createTestNode(ERBS_17A_NODE_NAME, ERBS_17A_OSS_MODEL_IDENTITY));
                nodes.add(netsimOperator.createTestNode(ERBS_16B_NODE_NAME, ERBS_16B_OSS_MODEL_IDENTITY));
                nodeDpsController.deleteNodes(nodes);

                numberOfSyncsDuringSetup = nodes.size();
            }

        }.start();
    }

    @After
    public void cleanUp() {
        new SyncTestCaseSetup(InstrumentationIT.class.getSimpleName(), SyncTestCaseSetup.CLEAN_UP_PHASE_NAME) {

            @Override
            public void execute() throws Exception {
                if (nodes != null) {
                    nodeDpsController.deleteNodes(nodes);
                }
            }

        }.start();
    }

    @Test
    @InSequence(1)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void testFullSyncInstrumentation() {
        new SyncTestCase("Full Sync Instrumentation metrics") {

            @Override
            public void execute() throws Exception {
                final String[] MAIN_SYNC_MOCI_INSTRUMENTATION_METRICS = { MOCI_TOPOLOGY_HANDLER_INVOCATIONS_METRIC,
                    MOCI_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC };
                final String[] MAIN_SYNC_DPS_INSTRUMENTATION_METRICS = { DPS_CONTROLLER_HANDLER_INVOCATIONS_METRIC,
                    DPS_TOPOLOGY_HANDLER_INVOCATIONS_METRIC, DPS_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC, DPS_SUCCESSFUL_SYNCS_COUNT_METRIC };
                final Map<String, Long> expectedInstrumentationMetrics = new HashMap<>();
                final long expectedFailedSyncs =
                        instrumentationAssistant
                                .getInstrumentationAttributeValue(MOCI_HANDLERS_INSTRUMENTATION_MBEAN, MOCI_FAILED_SYNCS_COUNT_METRIC);

                for (final String instumentationMetric : MAIN_SYNC_MOCI_INSTRUMENTATION_METRICS) {
                    expectedInstrumentationMetrics.put(instumentationMetric,
                            instrumentationAssistant.getInstrumentationAttributeValue(MOCI_HANDLERS_INSTRUMENTATION_MBEAN, instumentationMetric)
                                    + numberOfSyncsDuringSetup);
                }
                for (final String instumentationMetric : MAIN_SYNC_DPS_INSTRUMENTATION_METRICS) {
                    expectedInstrumentationMetrics.put(instumentationMetric,
                            instrumentationAssistant.getInstrumentationAttributeValue(DPS_HANDLERS_INSTRUMENTATION_MBEAN, instumentationMetric)
                                    + numberOfSyncsDuringSetup);
                }

                syncAssistant.deleteAddAndSyncNodes(nodes);

                // MOCI Handlers instrumentation asserts
                for (final String instumentationMetric : MAIN_SYNC_MOCI_INSTRUMENTATION_METRICS) {
                    assertAssistant.assertInstrumentationMetric(instumentationMetric, expectedInstrumentationMetrics.get(instumentationMetric),
                            MOCI_HANDLERS_INSTRUMENTATION_MBEAN);
                }
                assertAssistant.assertInstrumentationMetric(MOCI_FAILED_SYNCS_COUNT_METRIC, expectedFailedSyncs, MOCI_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(MOCI_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        MOCI_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(MOCI_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        MOCI_HANDLERS_INSTRUMENTATION_MBEAN);

                // DPS Handlers instrumentation asserts
                for (final String instumentationMetric : MAIN_SYNC_DPS_INSTRUMENTATION_METRICS) {
                    assertAssistant.assertInstrumentationMetric(instumentationMetric, expectedInstrumentationMetrics.get(instumentationMetric),
                            DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                }
                assertAssistant.assertInstrumentationMetricNotZero(DPS_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_AVERAGE_NUMBER_OF_MOS_SYNCED_METRIC, DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant
                        .assertInstrumentationMetricNotZero(DPS_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC, DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant
                        .assertInstrumentationMetricNotZero(DPS_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC, DPS_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_MAX_OVERALL_SYNC_EXECUTION_TIME_METRIC, DPS_HANDLERS_INSTRUMENTATION_MBEAN);
            }

        }.start();
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment(VS_TEST_DEPLOYMENT_NAME)
    public void testDeltaSyncInstrumentation() {
        new SyncTestCase("Delta Sync Instrumentation metrics") {

            @Override
            public void execute() throws Exception {
                final String[] MAIN_DELTA_SYNC_INSTRUMENTATION_METRICS = { DPS_DELTA_SUCCESSFUL_SYNCS_COUNT_METRIC,
                    DPS_DELTA_DPS_HANDLER_INVOCATIONS_METRIC };
                final Map<String, Long> expectedInstrumentationMetrics = new HashMap<>();

                for (final String instrumentationMetric : MAIN_DELTA_SYNC_INSTRUMENTATION_METRICS) {
                    expectedInstrumentationMetrics.put(
                            instrumentationMetric,
                            instrumentationAssistant
                                    .getInstrumentationAttributeValue(DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN, instrumentationMetric)
                                    + numberOfSyncsDuringSetup);
                }

                syncAssistant.deleteAddAndSyncNodes(nodes);
                syncAssistant.deltasyncNodes(nodes);

                // Delta-Sync Handlers instrumentation asserts
                for (final String instumentationMetric : MAIN_DELTA_SYNC_INSTRUMENTATION_METRICS) {
                    assertAssistant.assertInstrumentationMetric(instumentationMetric, expectedInstrumentationMetrics.get(instumentationMetric),
                            DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);
                }
                assertAssistant.assertInstrumentationMetric(DPS_DELTA_FAILED_SYNCS_COUNT_METRIC, 0, DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_DELTA_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC,
                        DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_DELTA_DPS_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_DELTA_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC,
                        DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);
                assertAssistant.assertInstrumentationMetricNotZero(DPS_DELTA_NODE_INFO_HANDLER_AVERAGE_EXECUTION_TIME_METRIC,
                        DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN);

                for (final TestNode node : nodes) {
                    netsimOperator.setTestNode(node);
                    netsimOperator.deleteMo(FdnUtil.getSctpNodeFdn(DUMMY_MO_NAME));
                }
            }

        }.start();
    }

}
