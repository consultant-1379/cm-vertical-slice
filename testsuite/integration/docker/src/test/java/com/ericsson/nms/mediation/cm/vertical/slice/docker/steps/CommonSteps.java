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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.ACTIVE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CM_NODE_HEARTBEAT_SUPERVISION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CPP_CONNECTIVITY_INFORMATION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.PORT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.AttributeTransformer.registerBooleanConverter;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.AttributeTransformer.registerIntegerConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;

public class CommonSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    @Before("@SetupDpsObjectTypesConversion")
    public void setupDpsObjectTypesConversion() {
        LOGGER.info("SetupDpsObjectTypesConversion");
        registerIntegerConverter(CPP_CONNECTIVITY_INFORMATION, PORT);
        registerBooleanConverter(CM_NODE_HEARTBEAT_SUPERVISION, ACTIVE);
    }

    @And("^I wait (.*) seconds$")
    public void waitForSeconds(final String seconds) throws InterruptedException {
        final long convertedMilliSeconds = Long.valueOf(seconds)*1000;
        LOGGER.info("Waiting for {} seconds", seconds);
        Thread.sleep(convertedMilliSeconds);
    }
}
