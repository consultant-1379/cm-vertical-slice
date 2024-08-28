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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.NetsimHelper;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;

public class NetsimSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetsimSteps.class);

    @Inject
    private NetsimHelper netsimHelper;

    @Before("@SetupNetsim")
    public void setupNetsim() {
        LOGGER.info("Generating Network Map");
        netsimHelper.generateNetworkMap();
    }

    @Then("^The node will not contain the managed object (.*):$")
    public void verifyManagedObjectIsDeletedOnNetsim(final String fdn){
        assertFalse(netsimHelper.isMoCreated(fdn));
    }

    @Then("^Validate that the node (.*) has no subscription$")
    public void verifyNodeHasNoSubscription(final String nodeName){
        assertTrue(getsubscriptionStatusFromNetsim(nodeName).contains("NoCSsubscriptions"));
    }

    @Then("^Validate that the node (.*) has a valid subscription$")
    public void verifyNodeHasAValidSubscription(final String nodeName){
        assertTrue(getsubscriptionStatusFromNetsim(nodeName).contains("Subscriptions:"));
    }

    public String getsubscriptionStatusFromNetsim(final String nodeName){
        return netsimHelper.getSubscriptionStatusFromNetsim(nodeName);
    }
}
