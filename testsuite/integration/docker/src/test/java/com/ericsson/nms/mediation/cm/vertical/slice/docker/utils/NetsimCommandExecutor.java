/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EMPTY_STRING;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * JSCH Secure Shell command executor.
 */
@Startup
@Singleton
public class NetsimCommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetsimCommandExecutor.class);

    private static final String NETSIM_HOST_USER_NAME = "netsim";
    private static final String NETSIM_HOST_PASSWORD = "netsim";

    private static final String CHANNEL_TYPE = "exec";
    private static final String STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_NAME = "StrictHostKeyChecking";
    private static final String STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_VALUE = "no";

    private static final int CONNECTION_TIMEOUT_MILLISECS = 5000;

    private Session session;
    private ChannelExec channel;

    public String executeCommand(final String command) {
        String commandOutput = EMPTY_STRING;

        try {
            establishNetsimSession();
            establishChannelAndSetCommand(command);
            commandOutput = getCommandOutput();

        } catch (final Exception exception) {
            LOGGER.error("Error occured while executing SSH command: [{}].", command, exception);

        } finally {
            closeChannel();
            closeSession();
        }
        commandOutput = commandOutput.replaceAll("\\s+", "");
        LOGGER.info("Command output: [{}]", commandOutput);
        return commandOutput;
    }

    private void establishNetsimSession() throws JSchException, UnknownHostException {
        LOGGER.info("Establishing a NetSim SSH session...");

        final String netsimHostIp = InetAddress.getByName(NETSIM_HOST_USER_NAME).getHostAddress();
        LOGGER.info("Netsim ip = {}", netsimHostIp);

        session = getSession(netsimHostIp);
        session.connect(CONNECTION_TIMEOUT_MILLISECS);
        LOGGER.debug("Established a NetSim SSH session ([{}]).", netsimHostIp);
    }

    private Session getSession(final String ip) throws JSchException {
        final JSch jsch = new JSch();
        final Session session = jsch.getSession(NETSIM_HOST_USER_NAME, ip, 22);
        session.setPassword(NETSIM_HOST_PASSWORD);
        session.setConfig(getSessionConfig());

        return session;
    }

    private Properties getSessionConfig() {
        final Properties sessionConfig = new Properties();
        sessionConfig.put(STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_NAME, STRICT_HOST_KEY_CHECKING_CONFIG_PARAM_VALUE);

        return sessionConfig;
    }

    private void closeSession() {
        LOGGER.info("Closing SSH session...");
        session.disconnect();
    }

    private void establishChannelAndSetCommand(final String command) throws JSchException {
        LOGGER.info("Executing command {} on netsim", command);
        channel = getChannel(session);
        channel.setCommand(command);

        LOGGER.info("Connecting to an SSH channel...");
        channel.connect();
    }

    private ChannelExec getChannel(final Session session) throws JSchException {
        final ChannelExec channel = (ChannelExec) session.openChannel(CHANNEL_TYPE);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        return channel;
    }

    private String getCommandOutput() throws IOException {
        String commandOutput;
        try (InputStream inputStream = channel.getInputStream()) {
            commandOutput = IOUtils.toString(inputStream);
        }

        return commandOutput;
    }

    private void closeChannel() {
        LOGGER.info("Closing SSH channel...");
        channel.disconnect();
    }

}
