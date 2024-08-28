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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CM_HEARTBEAT_INTERVAL;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CM_HEARTBEAT_INTERVAL_DEFAULT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CM_NOTIFICATION_EVICTION_INTERVAL;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CM_NOTIFICATION_EVICTION_INTERVAL_DEFAULT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.ENABLED_TLS_PROTOCOLS_ECIM;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.PIB_HOST;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.PIB_PORT;

import java.io.IOException;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.PibConfigurationUtil;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;

public class PibSteps {

    @Before("@SetupPib")
    public void setupPib() throws IOException {
        PibConfigurationUtil.addPibParameter(PIB_HOST, PIB_PORT, ENABLED_TLS_PROTOCOLS_ECIM, "TLSv1.2", "String", "GLOBAL");
    }

    @When("^The (.*) PIB parameter is updated to (.*)$")
    public void updatePibParameter(final String paramName, final String paramValue) throws IOException {
        PibConfigurationUtil.setPibParameter(PIB_HOST, PIB_PORT, paramName, paramValue);
    }

    /**
     * Only invoke this teardown method for restoring PIB configurations in medcore.
     */
    @After("@ResetPib")
    public void resetPib() throws IOException {
        PibConfigurationUtil.setPibParameter(PIB_HOST, PIB_PORT, CM_HEARTBEAT_INTERVAL, CM_HEARTBEAT_INTERVAL_DEFAULT);
    }

    /**
     * Only invoke this teardown method for restoring PIB configurations in mediation.
     */
    @After("@ResetPibForEviction")
    public void resetPibForEviction() throws IOException {
        PibConfigurationUtil.setPibParameter(PIB_HOST, PIB_PORT, CM_NOTIFICATION_EVICTION_INTERVAL,
                CM_NOTIFICATION_EVICTION_INTERVAL_DEFAULT);
    }

}
