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

package com.ericsson.nms.mediation.cm.vertical.slice.utility.constants;

// TODO Names of the metrics are too confusing. We should re-consider giving them meaningful labels.
/**
 * Constants required for the Instrumentation tests.
 */
public class InstrumentationMetricsConstants {

    // Full Sync instrumentation metrics (DPS Handlers)
    public static final String DPS_CONTROLLER_HANDLER_INVOCATIONS_METRIC = "dpsInvocationController";
    public static final String DPS_TOPOLOGY_HANDLER_INVOCATIONS_METRIC = "dpsInvocationTopologySync";
    public static final String DPS_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC = "dpsInvocationAttributeSync";
    // ---
    public static final String DPS_AVERAGE_NUMBER_OF_MOS_SYNCED_METRIC = "averageNumberOfMosBeingSynced";
    public static final String DPS_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC = "averageNumberOfAttrBeingSynced";
    public static final String DPS_SUCCESSFUL_SYNCS_COUNT_METRIC = "dpsCounterForSuccessfulSync";
    public static final String DPS_FAILED_SYNCS_COUNT_METRIC = "dpsNumberOfFailedSyncs";
    // ---
    public static final String DPS_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "averageDpsTopologyDataTimeTaken";
    public static final String DPS_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "averageDpsAttributeDataTimeTaken";
    public static final String DPS_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC = "averageOverallSyncTimeTaken";
    public static final String DPS_MAX_OVERALL_SYNC_EXECUTION_TIME_METRIC = "maxOverallSyncTimeTaken";

    // Full Sync instrumentation metrics (MOCI Handlers)
    public static final String MOCI_TOPOLOGY_HANDLER_INVOCATIONS_METRIC = "topologySyncInvocations";
    public static final String MOCI_ATTRIBUTE_HANDLER_INVOCATIONS_METRIC = "attributeSyncInvocations";
    // ---
    public static final String MOCI_FAILED_SYNCS_COUNT_METRIC = "numberOfFailedSyncs";
    // ---
    public static final String MOCI_TOPOLOGY_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "averageTopologySyncTimeTaken";
    public static final String MOCI_ATTRIBUTE_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "averageAttributeSyncTimeTaken";

    // Delta Sync instrumentation metrics (DPS Handlers)
    public static final String DPS_DELTA_DPS_HANDLER_INVOCATIONS_METRIC = "dpsDeltaInvocationAttributeSync";
    // ---
    public static final String DPS_DELTA_AVERAGE_NUMBER_OF_ATTRIBUTES_SYNCED_METRIC = "averageNumberOfAttrBeingSynced";
    public static final String DPS_DELTA_SUCCESSFUL_SYNCS_COUNT_METRIC = "dpsSuccessfulDeltaSync";
    public static final String DPS_DELTA_FAILED_SYNCS_COUNT_METRIC = "dpsFailedDeltaSync";
    // ---
    public static final String DPS_DELTA_NODE_INFO_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "averageOverallNodeInfoDeltaSyncTimeTaken";
    public static final String DPS_DELTA_DPS_HANDLER_AVERAGE_EXECUTION_TIME_METRIC = "dpsPersistAttributesAvgTime";
    public static final String DPS_DELTA_AVERAGE_OVERALL_SYNC_EXECUTION_TIME_METRIC = "averageOverallDeltaSyncTimeTaken";

    // CM Router Policy instrumentation metrics
    public static final String CM_ROUTER_POLICY_NUMBER_OF_SUPERVISED_NODES_METRIC = "numberOfSupervisedNodes";

    // MBean co-ordinates
    public static final String DPS_HANDLERS_INSTRUMENTATION_MBEAN =
            "com.ericsson.nms.mediation.component.dps.instrumentation.inbound-dps-handler-code:type=DpsHandlerInstrumentation";
    public static final String MOCI_HANDLERS_INSTRUMENTATION_MBEAN =
            "com.ericsson.nms.mediation.component.moci.instrumentation.sync-node-moci-handler-code:type=SyncNodeInstrumentation";
    public static final String DPS_DELTA_HANDLERS_INSTRUMENTATION_MBEAN =
            "com.ericsson.nms.mediation.component.dps.instrumentation.inbound-dps-handler-code:type=DeltaDpsHandlerInstrumentation";
    public static final String CM_ROUTER_POLICY_INSTRUMENTATION_BEAN =
            "com.ericsson.oss.mediation.cm.router.policy.instrumentation.cm-router-policy:type=SupervisionInstrumentation";

}
