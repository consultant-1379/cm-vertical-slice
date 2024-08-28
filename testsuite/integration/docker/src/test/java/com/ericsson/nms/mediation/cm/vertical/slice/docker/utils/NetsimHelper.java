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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.test.servicehelper.remote.NetsimOperator;

/**
 * This is a Netsim helper class for any netsim related operations.
 * Will need refactoring once adequate structure is in place
 * @author ebrione
 */
@Stateless
public class NetsimHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetsimHelper.class);

    @EJB(lookup = NetsimOperator.JNDI_NAME)
    private NetsimOperator netsimOperator;

    @Inject
    private NetsimCommandExecutor netsimCommandExecutor;

    public List<String> retrieveNetsimTopologyTree(final String nodeName) {
        final List<String> netsimFdns = netsimOperator.getTopologyFromNode(nodeName);
        LOGGER.info("The size of netsim topology (may include unsupported MOs) is [{}]", netsimFdns.size());
        return netsimFdns;
    }

    private String executeCommand(final String command){
        return netsimCommandExecutor.executeCommand(command);
    }

    public void generateNetworkMap(){
        executeCommand("echo \".generateNetworkMap\" | /netsim/inst/netsim_shell");
    }

    public String getIpAddressForNode(final String nodeName) {
        return executeCommand("echo \".show started\" | /netsim/inst/netsim_pipe | grep " + nodeName + " | awk '{print $2}'");
    }

    public boolean isMoCreated(final String fdn){
        return netsimOperator.isMoCreated(fdn);
    }

    public long getGenerationCounterFromNetsim(final String nodeName){
        final long netsimGenerationCounter = Long.parseLong(executeCommand("echo 'e cs_notification_db:get_generation_count().' | /netsim/inst/netsim_pipe -sim LTEH1160-V2limx2-5K-FDD-LTE02 -ne " + nodeName + " | grep -v notif"));
        LOGGER.info("The Generation counter for Node [{}] from Netsim is [{}]", nodeName, netsimGenerationCounter);
        return netsimGenerationCounter;
    }

    public String getSubscriptionStatusFromNetsim(final String nodeName){
        final String netsimSubscriptionStatus = executeCommand("echo 'status;' | /netsim/inst/netsim_pipe -sim LTEH1160-V2limx2-5K-FDD-LTE02 -ne " + nodeName + " | grep '[s/S]ubscriptions';");
        LOGGER.info("The subscription status for Node [{}] from Netsim is [{}]", nodeName, netsimSubscriptionStatus);
        return netsimSubscriptionStatus;
    }
}
