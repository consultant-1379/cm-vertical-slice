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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.NOTIFICATION_PROCESS_TIME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ANU_TYPE_ATTR_DPS_FINAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ANU_TYPE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ANU_TYPE_ATTR_NETSIM_FINAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ANU_TYPE_ATTR_NETSIM_INITIAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DHCP_CLIENT_IDENTIFIER_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DHCP_CLIENT_INTERFACE_FINAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DHCP_CLIENT_INTERFACE_INITIAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DL_MAX_WAIT_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DL_MAX_WAIT_FINAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DL_MAX_WAIT_INITIAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DNS_LOOKUP_TIMER_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DNS_LOOKUP_TIMER_FINAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.DNS_LOOKUP_TIMER_INITIAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_DPS_FINAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_FINAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_INITIAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.STATIC_ROUTES_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.STATIC_ROUTES_FINAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.STATIC_ROUTES_INITIAL_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TREAT_AS_ATTR_FINAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TREAT_AS_ATTR_INITIAL_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TREAT_AS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ANTENNA_NEAR_UNIT_MO_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CABINET_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ENODE_B_FUNCTION_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.IP_INTERFACE_MO_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.IP_ROUTING_TABLE_MO_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.RIVENDELL_MO_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.assertj.core.api.SoftAssertions;

import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;
import com.ericsson.oss.mediation.network.api.exception.MociConnectionProviderException;

/**
 * Test helper class.
 * <p>
 * Accommodates all Treat-As-related operations.
 */
public class TreatAsAssistant {

    private static final int NUMBER_OF_CABINET_MOS = 10;

    private final String treatAsFdn = FdnUtil.getTreatAsMoFdn(RIVENDELL_MO_NAME);

    @Inject
    private NetsimAssistant netsimAssistant;
    @Inject
    private DpsAssistant dpsAssistant;
    @Inject
    private AssertAssistant assertAssistant;
    @Inject
    private NetsimOperator netsimOperator;

    /**
     * Manipulate MOs and attributes in the node to exercise Treat-As features.
     *
     * @see <a href="http://confluence-nam.lmera.ericsson.se/x/N1pKC">Treat-as Node Info</a>
     */
    public void performBackwardCompatibleChangesOnNode(final String nodeName) {

        // Manipulate unsupported MO
        netsimAssistant.manipulateTreatAsMoOnNode();

        // Update unsupported attribute
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, TREAT_AS_ATTR_NAME, TREAT_AS_ATTR_FINAL_VALUE);

        final String firstCabinetMoFdn = FdnUtil.getCabinetMoFdn(CABINET_MO_NAME + "_1");

        if (!dpsAssistant.moExists(nodeName, firstCabinetMoFdn)) {
            // Create more MOs than the previously supported cardinality
            netsimAssistant.createMultipleCabinetMosOnNode(NUMBER_OF_CABINET_MOS, CABINET_MO_NAME);
        }

        // Set restricted attribute
        netsimAssistant.updateAttributeOnNode(ANTENNA_NEAR_UNIT_MO_FDN, ANU_TYPE_ATTR_NAME, ANU_TYPE_ATTR_NETSIM_FINAL_VALUE);

        // Change unsupported Enum Literal
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_ATTR_NAME,
                RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_FINAL_VALUE);

        // Change struct with unsupported member
        netsimAssistant.updateAttributeOnNode(IP_INTERFACE_MO_FDN, DHCP_CLIENT_IDENTIFIER_ATTR_NAME,
                DHCP_CLIENT_INTERFACE_FINAL_ATTR_VALUE);

        // Change seq of struct with unsupported member
        netsimAssistant.updateAttributeOnNode(IP_ROUTING_TABLE_MO_FDN, STATIC_ROUTES_ATTR_NAME, STATIC_ROUTES_FINAL_ATTR_VALUE);

        // Set a value outside the range in DPS
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, DNS_LOOKUP_TIMER_ATTR_NAME, DNS_LOOKUP_TIMER_FINAL_ATTR_VALUE);

        // Long changed to ENUM
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, DL_MAX_WAIT_ATTR_NAME, DL_MAX_WAIT_FINAL_ATTR_VALUE);

        TimeUtil.sleep(NOTIFICATION_PROCESS_TIME);
    }

    public void assertTreatAsBehaviour(final String nodeName) throws MociConnectionProviderException {
        final SoftAssertions groupAssertions = new SoftAssertions();
        final String eNodeBFunctionFdn = FdnUtil.prependMeContextToFdn(nodeName, ENODE_B_FUNCTION_FDN);

        assertUnsupportedMo(nodeName, groupAssertions);
        assertCardinalityChange(nodeName, groupAssertions);
        assertRestrictedAttribute(nodeName, groupAssertions);
        assertUsupportedEnumLiteral(eNodeBFunctionFdn, groupAssertions);
        assertUnsupportedStructMember(nodeName, groupAssertions);
        assertSequenceOfStructWithUnsupportedMember(nodeName, groupAssertions);
        assertRange(eNodeBFunctionFdn, groupAssertions);
        assertLongChangedToEnum(eNodeBFunctionFdn, groupAssertions);

        groupAssertions.assertAll();
    }

    public void resetNetsimValues() {
        netsimOperator.deleteMo(treatAsFdn);
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, TREAT_AS_ATTR_NAME, TREAT_AS_ATTR_INITIAL_VALUE);
        netsimAssistant.updateAttributeOnNode(ANTENNA_NEAR_UNIT_MO_FDN, ANU_TYPE_ATTR_NAME, ANU_TYPE_ATTR_NETSIM_INITIAL_VALUE);
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_ATTR_NAME,
                RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_NETSIM_INITIAL_VALUE);
        netsimAssistant.updateAttributeOnNode(IP_INTERFACE_MO_FDN, DHCP_CLIENT_IDENTIFIER_ATTR_NAME, DHCP_CLIENT_INTERFACE_INITIAL_ATTR_VALUE);
        netsimAssistant.updateAttributeOnNode(IP_ROUTING_TABLE_MO_FDN, STATIC_ROUTES_ATTR_NAME, STATIC_ROUTES_INITIAL_ATTR_VALUE);
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, DNS_LOOKUP_TIMER_ATTR_NAME, DNS_LOOKUP_TIMER_INITIAL_ATTR_VALUE);
        netsimAssistant.updateAttributeOnNode(ENODE_B_FUNCTION_FDN, DL_MAX_WAIT_ATTR_NAME, DL_MAX_WAIT_INITIAL_ATTR_VALUE);
    }

    /**
     * Asserts that unsupported MOs are ignored during sync / notifications.
     */
    private void assertUnsupportedMo(final String nodeName, final SoftAssertions groupAssertions) {
        groupAssertions.assertThat(dpsAssistant.moExists(nodeName, treatAsFdn)).as("Unsupported MO is not persisted in DPS").isFalse();
    }

    /**
     * Asserts that after increasing container cardinality of an MO, more MOs could be persisted (above the previous limit).
     */
    private void assertCardinalityChange(final String nodeName, final SoftAssertions groupAssertions) {
        final String cabinetMoFdnBase = FdnUtil.getCabinetMoFdn(CABINET_MO_NAME);
        for (int moNumber = 1; moNumber <= NUMBER_OF_CABINET_MOS; moNumber++) {
            final String cabinetMoFdn = cabinetMoFdnBase + "_" + moNumber;
            groupAssertions.assertThat(dpsAssistant.moExists(nodeName, cabinetMoFdn)).as("Cabinet MO %s exists", cabinetMoFdn).isTrue();
        }
    }

    /**
     * Asserts that attribute is persisted after removing the {@code restricted} tag.
     */
    private void assertRestrictedAttribute(final String nodeName, final SoftAssertions groupAssertions) {
        final String antennaNearUnitNodeFdn = FdnUtil.prependMeContextToFdn(nodeName, ANTENNA_NEAR_UNIT_MO_FDN);
        assertAssistant.assertDpsMoAttribute(antennaNearUnitNodeFdn, ANU_TYPE_ATTR_NAME, ANU_TYPE_ATTR_DPS_FINAL_VALUE, groupAssertions);
    }

    /**
     * Asserts that a enum literal can still be persisted after changing its name.
     */
    private void assertUsupportedEnumLiteral(final String eNodeBFunctionFdn, final SoftAssertions groupAssertions) {
        assertAssistant.assertDpsMoAttribute(eNodeBFunctionFdn, RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_ATTR_NAME,
                RELEASE_INACTIVE_UES_MP_LOAD_LEVEL_DPS_FINAL_VALUE, groupAssertions);
    }

    /**
     * Asserts that we can sync / process notifications of a struct with a new unsupported member. Everything but the new member should be persisted.
     */
    private void assertUnsupportedStructMember(final String nodeName, final SoftAssertions groupAssertions) {
        final Map<String, Object> expectedDhcpClientIdentifier = new HashMap<>();
        expectedDhcpClientIdentifier.put("clientIdentifier", "1");
        expectedDhcpClientIdentifier.put("clientIdentifierType", "AUTOMATIC");
        final String ipInterfaceFdnWithOssPrefix = FdnUtil.prependMeContextToFdn(nodeName, IP_INTERFACE_MO_FDN);
        assertAssistant.assertDpsMoAttribute(ipInterfaceFdnWithOssPrefix, DHCP_CLIENT_IDENTIFIER_ATTR_NAME, expectedDhcpClientIdentifier,
                groupAssertions);
    }

    /**
     * Asserts that we can sync / process notifications out of a sequence of structs with a new unsupported member.
     * Everything but the new member should be persisted.
     */
    private void assertSequenceOfStructWithUnsupportedMember(final String nodeName, final SoftAssertions groupAssertions) {
        final List<Map<String, Object>> expectedStaticRoute = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final Map<String, Object> route = new HashMap<>();
            route.put("nextHopIpAddr", "192.168.0.2");
            route.put("networkMask", "255.255.255.168");
            route.put("routeMetric", 100);
            route.put("indexOfStaticRoute", i);
            route.put("redistribute", true);
            route.put("ipAddress", "192.168.0.1");
            expectedStaticRoute.add(route);
        }
        final String ipRoutingTable = FdnUtil.prependMeContextToFdn(nodeName, IP_ROUTING_TABLE_MO_FDN);
        assertAssistant.assertDpsMoAttribute(ipRoutingTable, STATIC_ROUTES_ATTR_NAME, expectedStaticRoute, groupAssertions);
    }

    /**
     * Asserts that we can sync values outside of the previously defined range for an attribute.
     */
    private void assertRange(final String eNodeBFunctionFdn, final SoftAssertions groupAssertions) {
        assertAssistant.assertDpsMoAttribute(eNodeBFunctionFdn, DNS_LOOKUP_TIMER_ATTR_NAME, Integer.valueOf(
                DNS_LOOKUP_TIMER_FINAL_ATTR_VALUE), groupAssertions);
    }

    /**
     * Asserts that we can sync / process notifications after changing a long to a enum type.
     */
    private void assertLongChangedToEnum(final String eNodeBFunctionFdn, final SoftAssertions groupAssertions) {
        assertAssistant.assertDpsMoAttribute(eNodeBFunctionFdn, DL_MAX_WAIT_ATTR_NAME, Integer.valueOf(DL_MAX_WAIT_FINAL_ATTR_VALUE),
                groupAssertions);
    }

}
