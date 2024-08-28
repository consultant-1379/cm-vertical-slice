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

import java.io.IOException;
import java.util.Map;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.BytemanSupport;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class RulesSteps {
    private static final String RULE_SCRIPT_NAME = "bytemanRule.btm";
    private static final String HOST_MSCM_GLOB = "mscm*";

    /**
     * @param exceptionType
     *            - Can be something like RuntimeException etc. Please be aware that the exception must be available for the test. Otherwise an
     *            earlyReturnException may be thrown by byteman
     * @param exceptionRules
     *            In general, must contain RULE, CLASS, METHOD, IF, DO and ENDRULE Please see https://developer.jboss.org/wiki/ABytemanTutorial#top
     *            for reference
     * @throws IOException
     */
    @Given("A (.*) is thrown for:$")
    public void createAndInjectException(final String exceptionType, final Map<String, Object> ruleText) throws IOException {
        final StringBuilder rule = new StringBuilder();
        ruleText.entrySet().stream().forEachOrdered(line -> rule.append(line.getKey() + " " + line.getValue()));
        // ByteMan injection of Byteman Rule Script containing Exception Logic
        BytemanSupport.loadBytemanRules(RULE_SCRIPT_NAME, HOST_MSCM_GLOB, rule.toString());
    }

    @When("^I unload all Byteman Rules$")
    public void unloadAllBytemanRulesStep() throws IOException {
        // ByteMan unloading of Byteman Rule Script containing Exception Logic
        BytemanSupport.unloadBytemanRules(RULE_SCRIPT_NAME, HOST_MSCM_GLOB);
    }

    /**
     * Only invoke this teardown method for unloading and removing byteman rules.
     *
     * @throws IOException
     */
    @After("@BytemanTearDown")
    public void unloadAllBytemanRules() throws IOException {
        // ByteMan unloading of Byteman Rule Script containing Exception Logic
        BytemanSupport.unloadBytemanRules(RULE_SCRIPT_NAME, HOST_MSCM_GLOB);
    }

}
