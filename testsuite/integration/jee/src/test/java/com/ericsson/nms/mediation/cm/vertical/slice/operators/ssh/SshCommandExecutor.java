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

package com.ericsson.nms.mediation.cm.vertical.slice.operators.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

/**
 * JSCH Secure Shell Command Executor.
 */
@ApplicationScoped
public class SshCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshCommandExecutor.class);

    private static final String VAPP_HOST_IP_ADDRESS = System.getProperty("netsim.ip.address", "192.168.0.2");

    private static final String HOST_USER_NAME = "netsim";
    private static final String HOST_PASSWORD = "netsim";

    private static final String CHANNEL_TYPE = "exec";
    private static final String STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_NAME = "StrictHostKeyChecking";
    private static final String STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_VALUE = "no";

    private Session session;
    private ChannelExec channel;

    @PostConstruct
    private void init() throws JSchException {
        establishSession();
    }

    @PreDestroy
    private void shutDown() {
        closeSession();
    }

    public String executeCommand(final String command) {
        String commandOutput = "";
        try {
            establishChannel(command);
            commandOutput = processCommand();
            closeChannel();

        } catch (final Exception exception) {
            LOGGER.error("Error occured while executing SSH command: [{}].", command, exception);
        }
        return commandOutput;
    }

    private void establishSession() throws JSchException {
        LOGGER.trace("Establishing an SSH session...");
        session = getSession(VAPP_HOST_IP_ADDRESS);
        session.connect();
    }

    private Session getSession(final String ip) throws JSchException {
        final JSch jsch = new JSch();
        final Session session = jsch.getSession(HOST_USER_NAME, ip, 22);
        session.setPassword(HOST_PASSWORD);
        session.setConfig(getSessionConfig());

        return session;
    }

    private Properties getSessionConfig() {
        final Properties sessionConfig = new Properties();
        sessionConfig.put(STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_NAME, STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_VALUE);

        return sessionConfig;
    }

    private void closeSession() {
        LOGGER.trace("Closing SSH session...");
        session.disconnect();
    }

    private void establishChannel(final String command) throws JSchException {
        channel = getChannel(session);
        channel.setCommand(command);

        LOGGER.trace("Connecting to an SSH channel...");
        channel.connect();
    }

    private ChannelExec getChannel(final Session session) throws JSchException {
        final ChannelExec channel = (ChannelExec) session.openChannel(CHANNEL_TYPE);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        return channel;
    }

    private void closeChannel() {
        LOGGER.trace("Closing SSH channel...");
        channel.disconnect();
    }

    private String processCommand() throws IOException {
        try (Scanner sc = new Scanner(channel.getInputStream(), StandardCharsets.UTF_8.name())) {
            return sc.useDelimiter("\\A").next();
        }
    }

}
