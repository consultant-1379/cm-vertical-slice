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

package com.ericsson.nms.mediation.cm.vertical.slice.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.ssh.SshCommandExecutor;

/**
 * Managed Object NetSim service.
 * <p>
 * Provides MO manipulation methods allowing to add/delete MOs and update their attributes on the node.
 */
@ApplicationScoped
public class NetsimOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetsimOperator.class);

    private static final String SHELL_COMMAND_POSTFIX = " | ./inst/netsim_pipe -sim %s -ne %s";
    private static final String CREATE_MO_SHELL_COMMAND = "echo 'createmo:parentid=\"" + "%s\"" + ",type=\"" + "%s\"" + ",name=\"" + "%s\";'"
            + SHELL_COMMAND_POSTFIX;
    private static final String SET_MO_ATTRIBUTE_SHELL_COMMAND = "echo 'setmoattribute:mo=\"%s\", attributes=\"%s=%s\";'" + SHELL_COMMAND_POSTFIX;
    private static final String DELETE_MO_SHELL_COMMAND = "echo 'deletemo:moid=\"%s\";'" + SHELL_COMMAND_POSTFIX;
    private static final String START_MO_SHELL_COMMAND = "echo '.start'" + SHELL_COMMAND_POSTFIX;
    private static final String STOP_MO_SHELL_COMMAND = "echo '.stop'" + SHELL_COMMAND_POSTFIX;
    private static final String OVERFLOW_NOTIFICATION_SHELL_COMMAND = "echo 'sendcsnotif:type=OverflowType;'" + SHELL_COMMAND_POSTFIX;
    private static final String SHOW_STARTED_NODES_SHELL_COMMAND = "echo '.show started'| inst/netsim_pipe";

    private class NetsimNodeInfo {
        private final String ip;
        private final String simulation;

        NetsimNodeInfo(final String ip, final String simulation) {
            this.ip = ip;
            this.simulation = simulation;
        }

        String getIp() {
            return ip;
        }

        String getSimulation() {
            return simulation;
        }

        @Override
        public String toString() {
            return new StringBuilder("NetsimNodeInfo{")
                    .append("ip=").append(ip)
                    .append(",simulation=").append(simulation)
                    .append("}").toString();
        }
    }

    @Inject
    private SshCommandExecutor commandExecutor;

    private Map<String, NetsimNodeInfo> nodes;
    private TestNode nodeToOperateOn;

    @PostConstruct
    private void setupNodes() {
        try {
            final Map<String, NetsimNodeInfo> parsedNodes = new HashMap<>();
            final String shellOutput = executeShellCommand(SHOW_STARTED_NODES_SHELL_COMMAND);
            LOGGER.debug("Shell command output is : [{}]...", shellOutput);
            final String shellOutputLines[] = shellOutput.split("\\n");
            for (final String shellOutputLine : shellOutputLines) {
                parseNodeInfo(parsedNodes, shellOutputLine.trim());
            }

            nodes = Collections.unmodifiableMap(parsedNodes);
        } catch (final Exception e) {
            LOGGER.error("Error while executing the command", e);
        }
    }

    public void setTestNode(final TestNode testNode) {
        nodeToOperateOn = testNode;
    }

    public void addMo(final String parentFdn, final String moType, final String moName) {
        final String addMoCommand = String.format(CREATE_MO_SHELL_COMMAND, parentFdn, moType, moName, nodeToOperateOn.getNetsimNodeSimulation(),
                nodeToOperateOn.getName());
        executeShellCommand(addMoCommand);
    }

    public void deleteMo(final String fdn) {
        final String deleteMoCommand =
                String.format(DELETE_MO_SHELL_COMMAND, fdn, nodeToOperateOn.getNetsimNodeSimulation(), nodeToOperateOn.getName());
        executeShellCommand(deleteMoCommand);
    }

    public void updateAttribute(final String fdn, final String attributeName, final String attributeValue) {
        final String updateAttributeCommand = String.format(SET_MO_ATTRIBUTE_SHELL_COMMAND, fdn, attributeName, attributeValue,
                nodeToOperateOn.getNetsimNodeSimulation(), nodeToOperateOn.getName());
        executeShellCommand(updateAttributeCommand);
    }

    public void startNode() {
        final String startNodeCommand = String.format(START_MO_SHELL_COMMAND, nodeToOperateOn.getNetsimNodeSimulation(), nodeToOperateOn.getName());
        executeShellCommand(startNodeCommand);
    }

    public void stopNode() {
        final String stopNodeCommand = String.format(STOP_MO_SHELL_COMMAND, nodeToOperateOn.getNetsimNodeSimulation(), nodeToOperateOn.getName());
        executeShellCommand(stopNodeCommand);
    }

    public void sendOverflowNotification() {
        final String overflowNotificationCommand =
                String.format(OVERFLOW_NOTIFICATION_SHELL_COMMAND, nodeToOperateOn.getNetsimNodeSimulation(), nodeToOperateOn.getName());
        executeShellCommand(overflowNotificationCommand);
    }

    public TestNode createTestNode(final String name, final String ossModelIdentity) {
        final NetsimNodeInfo nodeInfo = nodes.get(name);
        LOGGER.debug("nodes returned from netsim : {}", nodes);
        if (nodeInfo == null) {
            throw new IllegalStateException("Unable to locate node with name: " + name + ". Check name is valid and node is started.");
        }
        return new TestNode(nodeInfo.getIp(), name, nodeInfo.getSimulation(), ossModelIdentity);
    }

    public List<TestNode> getNodesFromSimulation(final int numberOfNodes, final String simulation, final String ossModelIdentity) {
        final List<TestNode> nodesFromSim = new ArrayList<>();
        for (final Entry<String, NetsimNodeInfo> node : nodes.entrySet()) {
            final NetsimNodeInfo nodeInfo = node.getValue();
            if (nodeInfo.getSimulation().equals(simulation)) {
                nodesFromSim.add(new TestNode(nodeInfo.getIp(), node.getKey(), nodeInfo.getSimulation(), ossModelIdentity));
                if (nodesFromSim.size() == numberOfNodes) {
                    return nodesFromSim;
                }
            }
        }
        throw new IllegalStateException(
                "Unable to populate TestNode list with " + numberOfNodes + " entries. Check simulation is valid and nodes are turned on");
    }

    public String getSimulationForNode(final String name) {
        final NetsimNodeInfo nodeInfo = nodes.get(name);
        LOGGER.debug("node returned from netsim : {}", nodeInfo);
        if (nodeInfo == null) {
            throw new IllegalStateException("Unable to locate node with name: " + name + ". Check name is valid and node is started.");
        }
        return nodeInfo.getSimulation();
    }

    private String executeShellCommand(final String command) {
        LOGGER.debug("Executing shell command: [{}]...", command);
        return commandExecutor.executeCommand(command);
    }

    /**
     * Parses a node's IP, Simulation Name and Node name.
     *
     * @param nodes
     *            map to store the parsed netsim info. No entry is added if nothing is parsed from the input.
     * @param lineToParse
     *            expected format as below:<br>
     *            LTE05ERBS00005 192.168.100.159 /netsim/netsimdir/LTEE1239x160-5K-FDD-LTE05
     */
    private void parseNodeInfo(final Map<String, NetsimNodeInfo> nodes, final String lineToParse) {
        final int NODE_NAME_POSITION = 0;
        final int NODE_IP_POSITION = 1;
        final int NODE_SIMULATION_POSITION = 2;
        if (lineToParse.startsWith("LTE") || lineToParse.startsWith("SGSN") || lineToParse.startsWith("RNC")) {
            final String words[] = lineToParse.split("\\s+");
            final String[] simDirectory = words[NODE_SIMULATION_POSITION].split("/");
            final NetsimNodeInfo nodeInfo = new NetsimNodeInfo(words[NODE_IP_POSITION], simDirectory[simDirectory.length - 1]);
            nodes.put(words[NODE_NAME_POSITION], nodeInfo);
        }
    }

}
