
package com.ericsson.nms.mediation.cm.vertical.slice.utility.constants;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;

/**
 * Test-Node specific details.
 * <p>
 * Currently vApp NetSim nodes are divided into different simulations depending on their version (i.e. 13A, 13B, 14A...).<br>
 * Each simulation has a dedicated set of nodes contained in a range of specific local IP addresses. These details are aimed to be abstracted and
 * captured here.
 * <p>
 * <b>Note</b>: Every time NetSim template changes, these details have to be carefully updated! Tests depend heavily on these properties to be able to
 * connect to the relevant NetSim nodes.
 *
 * @see TestNode
 */
public class TestNodeConstants {

    //
    // LTEG1301-limx5-5K-FDD-LTE01 (used by: FullSyncIT, InstrumentationIT, NotificationIT)
    //
    public static final String ERBS_16B_NODE_NAME = "LTE01ERBS00001";
    public static final String ERBS_16B_OSS_MODEL_IDENTITY = "16B-G.1.301"; // Corresponds to G.1.301

    //
    // LTEH140-limx10-FT-FDD-LTE10 (used by: DeltaSyncIT, FullSyncIT, InstrumentationIT, SupervisionIT)
    //
    public static final String ERBS_17A_NODE_NAME = "LTE10ERBS00001";
    public static final String ERBS_17A_OSS_MODEL_IDENTITY = "17A-H.1.40"; // Corresponds to H.1.40

    //
    // LTEG1132-limx3-5K-FDD-LTE101 (used by: TreatAsIT)
    //
    public static final String ERBS_16A_TREAT_AS_NODE_NAME = "LTE101ERBS00001"; // This node on netsim has actual version as G.1.132
    public static final String ERBS_TREAT_AS_OSS_MODEL_IDENTITY = "4613-704-163"; // Corresponds to G.1.124

    //
    // RNCV71543x1-FT-RBSU4330x46-RNC01 (used by: LargeRncSyncIT)
    //
    public static final String RNC_16B_NODE_NAME = "RNC01";
    public static final String RNC_16B_OSS_MODEL_IDENTITY = "16B-V.7.1543"; // Corresponding to V.7.1543

    //
    // CORE-ST-4.5K-SGSN-15B-CP01-V3x2 (used by: SupervisionIT)
    //
    public static final String SGSN_15B_NODE_NAME = "SGSN-15B-CP01-V301";
    public static final String SGSN_15B_OSS_MODEL_IDENTITY = "4137-250-717";

    private TestNodeConstants() {}

}
