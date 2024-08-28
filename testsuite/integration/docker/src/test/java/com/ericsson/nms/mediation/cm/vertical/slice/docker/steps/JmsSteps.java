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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CLUSTEREDMEDIATIONSERVICECONSUMERCM0;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CLUSTEREDNETWORKELEMENTSUBSCRIPTIONS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.DEFAULT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.HORNETQSERVER;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.JMSQUEUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.JMSSERVER;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.JMSTOPIC;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.MESSAGING;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.NETWORKELEMENTNOTIFICATIONS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.RESUME;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.SUBSYSTEM;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.SYNCSTATUSCHANGETOPIC;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.JmsServerCallBackHandler;

import cucumber.api.java.After;
import cucumber.api.java.en.And;

public class JmsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsSteps.class);

    @And("^A (.*) operation is invoked on (.*) name (.*)$")
    public void executeOperationOnJms(final String jmsOperation, final String jmsMessgeService, final String jmsMessageServiceName) {
        performOperationOnJms(jmsOperation, jmsMessgeService, jmsMessageServiceName);
    }

    @After("@ResumeJms")
    public void resumeJmsQueuesAndTopics() {
        final String resumeOperation = RESUME;
        final Map<String, String> queueAndOperation = new HashMap<>();
        queueAndOperation.put(CLUSTEREDMEDIATIONSERVICECONSUMERCM0, JMSQUEUE);
        queueAndOperation.put(NETWORKELEMENTNOTIFICATIONS, JMSQUEUE);
        queueAndOperation.put(SYNCSTATUSCHANGETOPIC, JMSTOPIC);
        queueAndOperation.put(CLUSTEREDNETWORKELEMENTSUBSCRIPTIONS, JMSQUEUE);

        for (final Map.Entry<String, String> entry : queueAndOperation.entrySet()) {
            performOperationOnJms(resumeOperation, entry.getValue(), entry.getKey());
        }
    }

    private void performOperationOnJms(final String jmsOperation, final String jmsMessageService, final String jmsMessageServiceName) {
        final ModelNode address = new ModelNode().setEmptyList();
        address.add(SUBSYSTEM, MESSAGING);
        address.add(HORNETQSERVER, DEFAULT);
        address.add(jmsMessageService, jmsMessageServiceName);
        final ModelNode op = Operations.createOperation(jmsOperation, address);
        try {
            final InetAddress jbMAddr = InetAddress.getByName(JMSSERVER);
            final ModelControllerClient mCC = ModelControllerClient.Factory.create(jbMAddr, 9999, new JmsServerCallBackHandler());
            LOGGER.info("Attempting to {} {} {} ", jmsOperation, jmsMessageService, jmsMessageServiceName);
            mCC.execute(op);
        } catch (final IOException ex) {
            LOGGER.error("IOexception raised while tring to {} {} {}. Exception = {}", jmsOperation, jmsMessageService, jmsMessageServiceName, ex);
        }
    }
}
