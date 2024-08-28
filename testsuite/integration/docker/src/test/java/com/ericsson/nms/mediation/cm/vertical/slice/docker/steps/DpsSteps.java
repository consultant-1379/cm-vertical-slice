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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EMPTY_STRING;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.WAITINTERVALMILLISECONDS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.WAITTIMEMINUTES;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.ACTIVE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.DEFAULT_NETYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.DEFAULT_OSS_MODEL_IDENTITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.DEFAULT_OSS_PREFIX;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.DEFAULT_PLATFORM_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.DELETE_NRM_DATA_FROM_ENM;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.FALSE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT_ID;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NE_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.OSS_MODEL_IDENTITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.OSS_PREFIX;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.PLATFORM_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.SYNCHRONIZED;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.SYNC_STATUS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.TRUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.UNSYNCHRONIZED;
import static com.jayway.awaitility.Awaitility.await;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.AttributeVerifier;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.CommonUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.DpsHelper;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DpsSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(DpsSteps.class);
    @Inject
    private DpsHelper dpsHelper;

    @When("^I create a node called (.*):$")
    public void createNodeInDps(final String name, final Map<String, Object> attributes)
            throws UnknownHostException {
        LOGGER.info("Create NetworkElement MO with non default attributes for [{}]", name);
        final Map<String, Object> realAttributesMap = buildNeAttributes(name, attributes);
        dpsHelper.createNetworkElement(name, realAttributesMap);
        dpsHelper.createCppConnectivityInformation(name);
        dpsHelper.createNetworkElementSecurity(name);
    }

    // TODO improve this method in how default attributes are built
    private Map<String, Object> buildNeAttributes(final String name, final Map<String, Object> attributes) {
        final Map<String, Object> realAttributesMap = new HashMap<>();
        realAttributesMap.put(NETWORK_ELEMENT_ID, attributes.containsKey(NETWORK_ELEMENT_ID) ? attributes.get(NETWORK_ELEMENT_ID) : name);
        realAttributesMap.put(NE_TYPE, attributes.containsKey(NE_TYPE) ? attributes.get(NE_TYPE) : DEFAULT_NETYPE);
        realAttributesMap.put(PLATFORM_TYPE, attributes.containsKey(PLATFORM_TYPE) ? attributes.get(PLATFORM_TYPE) : DEFAULT_PLATFORM_TYPE);
        realAttributesMap.put(OSS_MODEL_IDENTITY, attributes
                .containsKey(OSS_MODEL_IDENTITY) ? attributes.get(OSS_MODEL_IDENTITY) : DEFAULT_OSS_MODEL_IDENTITY);
        realAttributesMap.put(OSS_PREFIX, attributes.containsKey(OSS_PREFIX) ? attributes.get(OSS_PREFIX) : DEFAULT_OSS_PREFIX);

        return realAttributesMap;
    }

    private void modifyManagedObjectsAttributeInDps(final String fdn, final String attribute, final String value) throws InterruptedException {
        dpsHelper.modify(fdn, attribute, value);
    }

    @When("^I enable cm supervision on the node (.*)$")
    public void enableCmSupervision(final String nodeName) throws InterruptedException {
        final String fdn = CommonUtil.getCmNodeHeartbeatSupervisionFdn(nodeName);
        modifyManagedObjectsAttributeInDps(fdn, ACTIVE, TRUE);
    }

    @When("^I disable cm supervision on the node (.*)$")
    public void disableCmSupervision(final String nodeName) throws InterruptedException {
        final String fdn = CommonUtil.getCmNodeHeartbeatSupervisionFdn(nodeName);
        modifyManagedObjectsAttributeInDps(fdn, ACTIVE, FALSE);
    }

    public void executeActionOnManagedObject(final String action, final String nodeName) throws InterruptedException {
        final String fdn = CommonUtil.getCmFunctionFdn(nodeName);
        LOGGER.info("Executing action: [{}], for fdn: [{}]", action, fdn);
        dpsHelper.execute(fdn, action, null);
    }

    @Then("^Node (.*) will not exist in DPS$")
    public void verifyNodeDoesNotExistInDps(final String nodeName) {
        final String managedObject = CommonUtil.getNetworkElementFdn(nodeName);
        final Map<String, Object> attributes = dpsHelper.find(managedObject);
        assertNull(attributes);
    }

    @Then("^ManagedObject (.*) will not exist in DPS$")
    public void verifyManagedObjectFdnDoesNotExist(final String managedObject) {
        final Map<String, Object> attributes = dpsHelper.find(managedObject);
        assertNull(attributes);
    }

    // TODO improve this method around the default polling times logic as its duplicated and any hard-coding
    @Then("^The syncstatus for node (.*) will be (.*):$")
    public void verifySyncStatusInDps(final String nodeName, final String expectedSyncState, final Map<String, Long> waitUnits) {
        final String fdn = CommonUtil.getCmFunctionFdn(nodeName);
        final long pollingTimeInMilli;
        final long watingTimeInMinutes;

        if (waitUnits.containsKey(EMPTY_STRING)) {
            pollingTimeInMilli = Constants.DPS_POLLING_TIME_IN_MILLI_SECONDS;
            watingTimeInMinutes = Constants.DPS_WAIT_TIME_IN_MINUTES;
        } else {
            pollingTimeInMilli = waitUnits.get(WAITINTERVALMILLISECONDS);
            watingTimeInMinutes = waitUnits.get(WAITTIMEMINUTES);
        }

        LOGGER.info("Total wait time for the attribute {} to change to {} is {} minutes",
                SYNC_STATUS, expectedSyncState, Constants.DPS_WAIT_TIME_IN_MINUTES);
        await().atMost(watingTimeInMinutes, TimeUnit.MINUTES).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(pollingTimeInMilli);
                final Map<String, Object> attributes = dpsHelper.find(fdn);
                return attributes.get(SYNC_STATUS).equals(expectedSyncState);
            }
        });
    }

    @Given("^The node (.*) is (.*)$")
    public void ensureSyncStatusForNode(final String nodeName, final String syncStatus) throws InterruptedException {
        final String fdn = CommonUtil.getCmFunctionFdn(nodeName);
        final Map<String, Object> attributes = dpsHelper.find(fdn);
        assertNotNull(attributes);
        final String actualSyncStatus = (String) attributes.get(SYNC_STATUS);
        if (syncStatus.equals(actualSyncStatus)) {
            return;
        }
        if (SYNCHRONIZED.equals(syncStatus)) {
            enableCmSupervision(nodeName);
        } else if (UNSYNCHRONIZED.equals(syncStatus)) {
            disableCmSupervision(nodeName);
        } else {
            LOGGER.error("Node [{}] is in [{}] state", nodeName, actualSyncStatus);
            Assert.fail("Node is neither in SYNCHRONIZED or UNSYNCHRONIZED state");
        }
        final Map<String, Long> waitUnits = new HashMap<>();
        waitUnits.put(EMPTY_STRING, 0L);
        verifySyncStatusInDps(nodeName, syncStatus, waitUnits);
    }

    // TODO improve this method around the default polling times logic as its duplicated and any hard-coding
    @Then("^DPS will contain the MO (.*):$")
    public void verifyManagedObjectExistsInDps(final String fdn, final Map<String, Long> waitUnits) {
        final long pollingTimeInMilli;
        final long watingTimeInMinutes;

        if (waitUnits.containsKey(EMPTY_STRING)) {
            pollingTimeInMilli = Constants.DPS_POLLING_TIME_IN_MILLI_SECONDS;
            watingTimeInMinutes = Constants.DPS_WAIT_TIME_IN_MINUTES;
        } else {
            pollingTimeInMilli = waitUnits.get(WAITINTERVALMILLISECONDS);
            watingTimeInMinutes = waitUnits.get(WAITTIMEMINUTES);
        }

        LOGGER.info("Total wait time for the MO [{}] to be persisted in DPS is [{}]", fdn, pollingTimeInMilli);
        await().atMost(watingTimeInMinutes, TimeUnit.MINUTES).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(pollingTimeInMilli);
                final ManagedObject dpsMo = dpsHelper.findMoByFdn(fdn);
                if (null == dpsMo) {
                    return false;
                }
                return fdn.equals(dpsMo.getFdn());
            }
        });
    }

    @Then("^DPS will contain (.*) with attribute name (.*) and object value (.*)")
    public void verifyAttributeValueExistsInDps(final String fdn, final String attributeName, final String expectedValue)
            throws InterruptedException {
        final AttributeVerifier attributeVerifier = new AttributeVerifier();
        LOGGER.info("Verify that fdn [{}] has an attribute [{}] with value [{}]", fdn, attributeName, expectedValue);
        final Object dpsAttributeObject = dpsHelper.getAttribute(fdn, attributeName);
        assertNotNull("The attribute for the fdn does not exist in dps", dpsAttributeObject);
        LOGGER.info("The attribute [{}] is persisted in dps as [{}]", attributeName, dpsAttributeObject.toString());
        assertTrue("The expected attribute value does not exist in dps",
                attributeVerifier.verifyAttributeInDps(fdn, dpsAttributeObject, expectedValue));
    }

    @Then("^DPS will not contain (.*) with attribute name (.*) and object value (.*)")
    public void verifyAttributeValueDoesNotExistsInDps(final String fdn, final String attributeName, final String expectedValue)
            throws InterruptedException {
        final AttributeVerifier attributeVerifier = new AttributeVerifier();
        LOGGER.info("Verify that fdn [{}] has an attribute [{}] with value [{}]", fdn, attributeName, expectedValue);
        final Object dpsAttributeObject = dpsHelper.getAttribute(fdn, attributeName);
        if (dpsAttributeObject != null) {
            assertFalse("The expected attribute value exists in dps", attributeVerifier.verifyAttributeInDps(fdn, dpsAttributeObject, expectedValue));
        }
    }

    public void executeRemoveNode(final String nodeName) throws InterruptedException {
        final String removefdn = CommonUtil.getNetworkElementFdn(nodeName);
        LOGGER.info("Executing remove action for fdn: [{}]", removefdn);
        dpsHelper.remove(removefdn);
    }

    @Given("^ManagedObject (.*) is deleted$")
    public void executeRemoveManagedObject(final String managedObject) throws InterruptedException {
        LOGGER.info("Executing remove action for managedObject: [{}]", managedObject);
        dpsHelper.remove(managedObject);
    }

    @Given("^Node (.*) is deleted$")
    public void executeDeleteNode(final String nodeName) throws InterruptedException {
        disableCmSupervision(nodeName);
        final String neFdn = CommonUtil.getNetworkElementFdn(nodeName);
        if (dpsHelper.managedElementExists(neFdn)) {
            executeActionOnManagedObject(DELETE_NRM_DATA_FROM_ENM, nodeName);
        }
        executeRemoveNode(nodeName);
    }

}
