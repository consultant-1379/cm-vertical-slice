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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.steps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.COMMA;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.DPS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.NETSIM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.DpsHelper;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.ModelServiceHelper;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.NetsimHelper;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SyncSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSteps.class);

    @Inject
    private DpsHelper dpsHelper;

    @Inject
    private ModelServiceHelper modelServiceHelper;

    @Inject
    private NetsimHelper netsimHelper;

    @Inject
    private TestStepsDataHolder testStepsDataHolder;

    @When("^I read the (.*) topology for node (.*)$")
    public void readTopology(final String target, final String nodeName) {
        if (DPS.equals(target)){
            testStepsDataHolder.setDpsFdnList(dpsHelper.retrieveDpsTopologyTree(nodeName));
        }
        else if (NETSIM.equals(target)){
            final List<String> netsimFdns = netsimHelper.retrieveNetsimTopologyTree(nodeName);
            final Set<String> netsimMoTypes = getUniqueManagedObjectTypesFromFdnList(netsimFdns);
            final Set<String> supportedNetsimMoTypes = modelServiceHelper.getSupportedMOTypesFromModelService(nodeName, netsimMoTypes);
            testStepsDataHolder.setNetsimFdnList(getFdnListOfSupportedNetsimManagedObjects(netsimFdns, supportedNetsimMoTypes));
        }
        else {
            fail("Invalid target name");
        }
    }

    @Then("^the topologies for node (.*) should match$")
    public void compareTopologies(final String nodeName) {
        compareTopologyTrees(nodeName, testStepsDataHolder.getNetsimFdnList(), testStepsDataHolder.getDpsFdnList());
    }

    private List<String> getFdnListOfSupportedNetsimManagedObjects(final List<String> netsimFdns, final Set<String> supportedNetsimMOTypes) {
        String moType = "";
        final List<String> fdnListForSupportedMOs = new ArrayList<>();
        for (final String netsimFdn : netsimFdns) {
            moType = getManagedObjectTypeFromFdn(netsimFdn);
            if (supportedNetsimMOTypes.contains(moType)) {
                fdnListForSupportedMOs.add(netsimFdn);
            }
        }
        LOGGER.info("The number of supported children MO fdns for this node is [{}]", fdnListForSupportedMOs.size());
        return fdnListForSupportedMOs;
    }

    private Set<String> getUniqueManagedObjectTypesFromFdnList(final List<String> netsimFdns) {
        final Set<String> netsimMoTypes = new HashSet<>();
        for (final String netsimFdn : netsimFdns) {
            netsimMoTypes.add(getManagedObjectTypeFromFdn(netsimFdn));
        }
        LOGGER.info("The number of unique MO types defined on the netsim node are [{}]", netsimMoTypes.size());
        return netsimMoTypes;
    }

    private void compareTopologyTrees(final String nodeName, final List<String> supportedNetsimFdns, final List<String> dpsFdns) {
        @SuppressWarnings("unchecked")
        final List<String> unsynchedFdns = (List<String>) CollectionUtils.disjunction(supportedNetsimFdns, dpsFdns);
        LOGGER.info("There are [{}] differences between the DPS topology and the node topology for the node [{}]", unsynchedFdns.size(), nodeName);
        if (unsynchedFdns.size() > 0) {
            LOGGER.info("The differences between DPS and the node are as follows");
            for (final String difference : unsynchedFdns) {
                LOGGER.info(difference);
            }
            LOGGER.info("There are [{}] differences between the DPS topology and the node topology for the node [{}]", unsynchedFdns.size(),
                    nodeName);
            fail("The Topology Tree check has failed for  " + nodeName);
        }
    }

    private String getManagedObjectTypeFromFdn(final String fdn) {
        return fdn.substring(fdn.lastIndexOf(COMMA) + 1, fdn.lastIndexOf(EQUALS));
    }

    @Then("^The Generation Counter in dps and netsim for node (.*) should not be equal$")
    public void verifyGenerationCountersAreNotEqual(final String nodeName){
        assertFalse("The Generation Counter on DPS and Netsim are equal",affirmGenerationCountersAreEqual(nodeName));
    }

    @Then("^The Generation Counter in dps and netsim for node (.*) should be equal$")
    public void verifyGenerationCountersAreEqual(final String nodeName){
        assertTrue("The Generation Counter on DPS and Netsim are not equal",affirmGenerationCountersAreEqual(nodeName));
    }

    public boolean affirmGenerationCountersAreEqual(final String nodeName){
        final long netsimGenerationCounter = netsimHelper.getGenerationCounterFromNetsim(nodeName);
        final long dpsGenerationCounter = dpsHelper.getGenerationCounterFromDps(nodeName);
        LOGGER.info("Netsim Generation Counter was: [{}] Dps generation counter was: [{}]", netsimGenerationCounter, dpsGenerationCounter);
        if(netsimGenerationCounter == dpsGenerationCounter ){
            return true;
        } else {
            return false;
        }
    }
}
