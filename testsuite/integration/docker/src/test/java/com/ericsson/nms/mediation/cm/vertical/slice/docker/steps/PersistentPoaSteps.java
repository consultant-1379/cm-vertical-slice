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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.CmRouterPolicyHelper;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.CommonUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.SshCommandExecutor;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.cm.events.StartSubscriptionValidationEvent;
import com.ericsson.oss.mediation.core.events.MediationClientType;
import com.ericsson.oss.mediation.core.events.OperationType;
import com.ericsson.oss.mediation.sdk.event.MediationTaskRequest;
import com.ericsson.oss.mediation.sdk.event.SupervisionMediationTaskRequest;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PersistentPoaSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentPoaSteps.class);

    private static final List<String> MSCMS = Arrays.asList("mscm-1", "mscm-2");

    private static final String CMD_BLOCK = "iptables -A INPUT -p tcp --match multiport --dports 8081,59001,59010 -j DROP";
    private static final String CMD_UNBLOCK = "iptables -D INPUT -p tcp --match multiport --dports 8081,59001,59010 -j DROP";
    private static final String CMD_QUERY = "ipvsadm -L | grep %s";

    private String supervisingMscm;
    private final Set<String> blockedHosts = new LinkedHashSet<>();

    @Inject
    @Modeled
    private EventSender<MediationTaskRequest> eventSender;

    @Inject
    private CmRouterPolicyHelper cmRouterPolicy;

    @Then("I detect the supervising mscm for (.*)$")
    public void detectSupervisingMscm(final String nodeName) {
        supervisingMscm = cmRouterPolicy.getSupervisingMediationInstance(nodeName);
        LOGGER.info("Supervision of node {} is on {}", nodeName, supervisingMscm);
        if (!MSCMS.contains(supervisingMscm)) {
            Assert.fail("Invalid supervising mscm detected!");
        }
    }

    @When("^I (block|unblock) notifications on the (supervising|other) mscm")
    public void blockUnblockNotifications(final String blockUnblock, final String mscmType) throws InterruptedException, IOException {
        if (!MSCMS.contains(supervisingMscm)) {
            Assert.fail("Supervising mscm not detected!");
        }

        final String mscm = "supervising".equals(mscmType) ? supervisingMscm : getOtherMscm();

        if ("block".equals(blockUnblock)) {
            SshCommandExecutor.exec(mscm, CMD_BLOCK);
            waitForVirtualServer(mscm, false);
            blockedHosts.add(mscm);
        } else {
            SshCommandExecutor.exec(mscm, CMD_UNBLOCK);
            waitForVirtualServer(mscm, true);
            blockedHosts.remove(mscm);
        }
        LOGGER.info("Command {} execution completed!", blockUnblock);
    }

    private String getOtherMscm() {
        return MSCMS.stream().filter(Predicate.isEqual(supervisingMscm).negate()).findFirst().get();
    }

    @When("^I send validation for (.*)$")
    public void sendSubscriptionValidation(final String nodeName) {
        final String cmNodeHeartbeatSupervisionFdn = CommonUtil.getCmNodeHeartbeatSupervisionFdn(nodeName);
        final StartSubscriptionValidationEvent event = new StartSubscriptionValidationEvent(cmNodeHeartbeatSupervisionFdn);
        LOGGER.info("######## Send StartSubscriptionValidationEvent event {} - {}", cmNodeHeartbeatSupervisionFdn, event);
        eventSender.send(event);
    }

    @When("^I send request to resubscribe (.*)$")
    public void resubscribe(final String nodeName) throws InterruptedException {
        final String cmNodeHeartbeatSupervisionFdn = CommonUtil.getCmNodeHeartbeatSupervisionFdn(nodeName);
        final SupervisionMediationTaskRequest request = new SupervisionMediationTaskRequest();
        request.setNodeAddress(cmNodeHeartbeatSupervisionFdn);
        request.setProtocolInfo(OperationType.CM.name());
        request.setClientType(MediationClientType.EVENT_BASED.name());
        request.setJobId(UUID.randomUUID().toString());
        request.setSupervisionAttributes(new HashMap<String, Object>());
        request.getSupervisionAttributes().put(MoConstants.ACTIVE, false);
        LOGGER.info("######## Send supervision MTR to resubscribe {} - {}", cmNodeHeartbeatSupervisionFdn, request);
        eventSender.send(request);
    }

    @After("@TestPersistentPoa")
    public void restoreAllMscms() throws InterruptedException, IOException {
        if (!blockedHosts.isEmpty()) {
            LOGGER.info("Unblocking all blocked hosts: {}", blockedHosts);
            for (final String hostName : blockedHosts) {
                SshCommandExecutor.exec(hostName, CMD_UNBLOCK);
                waitForVirtualServer(hostName, true);
            }
            blockedHosts.clear();
        }
    }

    private void waitForVirtualServer(final String hostName, final boolean active) throws InterruptedException, IOException {
        final String sshCmdQuery = String.format(CMD_QUERY, hostName);
        for (int attempt = 1; attempt <= 10; ++attempt) {
            if (attempt > 1) {
                Thread.sleep(5000L);
            }
            final String rsp = SshCommandExecutor.exec("lvsrouter", sshCmdQuery);
            LOGGER.debug("attempt: {}, rsp: {}", attempt, rsp);
            if (active ? !rsp.isEmpty() : rsp.isEmpty()) {
                return;
            }
        }
        Assert.fail("Timed out waiting for LVS Router to update the VirtualServer table");
    }
}
