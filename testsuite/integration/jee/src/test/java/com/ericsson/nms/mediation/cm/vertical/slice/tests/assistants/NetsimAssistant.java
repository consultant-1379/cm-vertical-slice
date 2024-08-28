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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.GANDALF_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.USER_LABEL_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ANTENNA_SUBUNIT_MO_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.AU_PORT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CABINET_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ENODE_B_FUNCTION_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EQUIPMENT_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EUTRAN_CELL_TDD_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.RIVENDELL_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.SCTP_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TRANSPORT_NETWORK_FDN;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TREAT_AS_MO_TYPE;

import javax.inject.Inject;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

/**
 * Test helper class.<br>
 * Executes operations/queries relevant to NetSim nodes.
 */
public class NetsimAssistant {

    @Inject
    private NetsimOperator netsimOperator;

    public void setTestNode(final TestNode testNode) {
        netsimOperator.setTestNode(testNode);
    }

    public void updateAttributeOnNode(final String moFdn, final String attributeName, final String attributeValue) {
        netsimOperator.updateAttribute(moFdn, attributeName, attributeValue);
    }

    public void deleteMoOnNode(final String fdn) {
        netsimOperator.deleteMo(fdn);
    }

    public void manipulateSctpMoOnNode(final String moName) {
        final String sctpFdn = FdnUtil.getSctpNodeFdn(moName);
        netsimOperator.addMo(TRANSPORT_NETWORK_FDN, SCTP_MO_TYPE, moName);
        netsimOperator.updateAttribute(sctpFdn, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE);
    }

    // public void addEUtranCellFddMoAndUpdateAttributesOnNode(final String attributeName, final String attributeValue) {
    // final String eUtranCellFddFdn = FdnUtil.getEUtranCellTddNodeFdn(RIVENDELL_MO_NAME);
    // netsimOperator.addMo(ENODE_B_FUNCTION_FDN, EUTRAN_CELL_FDD_MO_TYPE, RIVENDELL_MO_NAME);
    //
    // /*
    // * The below attribute set is required because of EUtranCellFDD MO validation on creation
    // * The ticket has the solution to get arround this problem
    // * https://jira-nam.lmera.ericsson.se/browse/NSS-1778
    // */
    //
    // netsimOperator.updateAttribute(eUtranCellFddFdn, EARFCNUL_ATTR_NAME, EARFCNUL_ATTR_VALUE);
    // netsimOperator.updateAttribute(eUtranCellFddFdn, attributeName, attributeValue);
    // }

    public void addEUtranCellTddMoAndUpdateAttributesOnNode(final String attributeName, final String attributeValue) {
        final String eUtranCellTddFdn = FdnUtil.getEUtranCellTddNodeFdn(RIVENDELL_MO_NAME);
        netsimOperator.addMo(ENODE_B_FUNCTION_FDN, EUTRAN_CELL_TDD_MO_TYPE, RIVENDELL_MO_NAME);
        netsimOperator.updateAttribute(eUtranCellTddFdn, attributeName, attributeValue);
    }

    public void manipulateAuMoOnNode() {
        final String auPortFdn = FdnUtil.getAuPortNodeFdn(RIVENDELL_MO_NAME);
        netsimOperator.addMo(ANTENNA_SUBUNIT_MO_FDN, AU_PORT_MO_TYPE, RIVENDELL_MO_NAME);
        netsimOperator.updateAttribute(auPortFdn, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE);
    }

    public void manipulateTreatAsMoOnNode() {
        final String treatAsFdn = FdnUtil.getTreatAsMoFdn(RIVENDELL_MO_NAME);
        netsimOperator.addMo(ENODE_B_FUNCTION_FDN, TREAT_AS_MO_TYPE, RIVENDELL_MO_NAME);
        netsimOperator.updateAttribute(treatAsFdn, USER_LABEL_ATTR_NAME, GANDALF_ATTR_VALUE);
    }

    public void createCabinetMoOnNode(final String moName) {
        netsimOperator.addMo(EQUIPMENT_FDN, CABINET_MO_TYPE, moName);
    }

    public void createMultipleCabinetMosOnNode(final int numberOfMos, final String moName) {
        for (int moNumber = 1; moNumber <= numberOfMos; moNumber++) {
            createCabinetMoOnNode(moName + "_" + moNumber);
        }
    }

    public void deleteCabinetMoOnMode(final String moName) {
        netsimOperator.deleteMo(FdnUtil.getCabinetMoFdn(moName));
    }

    public void deleteMultipleCabinetMosOnMode(final int numberOfMos, final String moName) {
        for (int moNumber = 1; moNumber <= numberOfMos; moNumber++) {
            deleteCabinetMoOnMode(moName + "_" + moNumber);
        }
    }

}
