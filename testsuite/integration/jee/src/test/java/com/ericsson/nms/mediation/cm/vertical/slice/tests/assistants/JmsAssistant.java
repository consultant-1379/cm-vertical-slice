/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *------------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.InitialContext;

import java.util.Enumeration;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.eventbus.jms.util.JBossCLICommandExecutor;
import com.ericsson.oss.mediation.network.api.notifications.NodeNotification;


public class JmsAssistant {
    private static final String CONNECTION_FACTORY = "/JmsXA";
    private static final String TEST_FDN ="MeContext=LTE02ERBS00010,ManagedElement=1,ENodeBFunction=1,Cdma2000Network=1,Cdma2000FreqBand=1,Cdma2000Freq=1,ExternalCdma2000Cell=99";

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsAssistant.class);

    public NodeNotification getLastMessageFromQueue(final String queueName) {
        NodeNotification response = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        try {
            LOGGER.debug(" Trying to consume the last message from the queue");
            final InitialContext initialContext = new InitialContext();
            connectionFactory = (ConnectionFactory) initialContext.lookup(CONNECTION_FACTORY);
            final Queue queue = (Queue) initialContext.lookup(queueName);
            connection = connectionFactory.createConnection();
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();

            LOGGER.debug("Browse through the elements in queue");
            final QueueBrowser browser = session.createBrowser(queue);
            final Enumeration<?> msgs = browser.getEnumeration();
            if (!msgs.hasMoreElements()) {
                LOGGER.debug("No messages in queue");
            } else {
                while (msgs.hasMoreElements()) {
                    final ObjectMessage message = (ObjectMessage) msgs.nextElement();
                    if (message.getObject() instanceof NodeNotification) {
                        response = (NodeNotification) message.getObject();
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception while getting message from queue!", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final JMSException e) {
                    LOGGER.error("JMSException while getting message from queue: " + e);
                }
            }
        }
        return response;
    }

    /**
     * The CmDataChangeQueue is configured in standalone-full.xml to have a max-size-bytes=500 KB
     * It takes approximatly 60 JMS messages to fill this Queue
     *
     * @param queueName
     *            The queue Name
     * @return true if the Queue is full
     */
    public boolean fillJmsQueue(final String queueName) {
        boolean queueIsFull = false;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        try {
            LOGGER.debug(" Trying to fill the queue {} ", queueName);
            final InitialContext initialContext = new InitialContext();
            connectionFactory = (ConnectionFactory) initialContext.lookup(CONNECTION_FACTORY);
            final Queue queue = (Queue) initialContext.lookup(queueName);
            connection = connectionFactory.createConnection();
            connection.start();
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer publisher = session.createProducer(queue);
            final NodeNotification nodeNotification = new NodeNotification();
            nodeNotification.setFdn(TEST_FDN);

            while (!queueIsFull) {
                try {
                    final Message message = session.createObjectMessage(nodeNotification);
                    publisher.send(message);
                } catch (final JMSException e) {
                    LOGGER.error("JMSException Caught while adding message {}", e);
                    queueIsFull = true;
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception while adding message's to queue", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final JMSException e) {
                    LOGGER.error("JMSException while adding message's to queue " + e);
                }
            }
        }
        return queueIsFull;
    }

    /**
     * Empty a JMS Queue using the JBOSS Management command
     * /subsystem=messaging-activemq/server=default/jms-queue=CmDataChangeQueue:remove-messages
     * <p>
     * See JBoss EAP 7.3.0.GA Model Reference:
     * https://access.redhat.com/webassets/avalon/d/red_hat_jboss_enterprise_application_platform/7.3/mgmt_model/
     *
     * @param queueName
     *            The queue Name
     * @return true if the Management command to empty the Queue was successful
     */
    public boolean emptyJmsQueue (final String queueName) {
        boolean queueIsEmpty = false;
        try {
            LOGGER.debug(" Trying to empty the queue {} ", queueName);
            LOGGER.debug(" Sending JBOSS Management Command {} ",
                    "/subsystem=messaging-activemq/server=default/jms-queue=CmDataChangeQueue:remove-messages");
            final ModelNode modelNode = new ModelNode();
            modelNode.get("operation").set("remove-messages");
            final ModelNode address = modelNode.get("address");
            address.add("subsystem", "messaging-activemq");
            address.add("server", "default");
            address.add("jms-queue", "CmDataChangeQueue");
            JBossCLICommandExecutor.executeCommand(modelNode);
            queueIsEmpty = true;
        }catch (final Exception e) {
            LOGGER.error("Exception while trying to empty the queue " + e);
        }
        return queueIsEmpty;
    }
}
