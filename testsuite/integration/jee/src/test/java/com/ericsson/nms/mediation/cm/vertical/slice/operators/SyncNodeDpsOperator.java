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

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ACTIVE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SYNC_STATUS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_FUNCTION_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.OSS_NE_CM_DEF_NAMESPACE;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.oss.itpf.datalayer.dps.delegate.DataAccessDelegate;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.mediation.sdk.event.MediationTaskRequest;

/**
 * Node Sync DPS service.
 * <p>
 * Provides ways to trigger sync and perform sync-related DPS operations (i.e read sync status).
 */
@ApplicationScoped
public class SyncNodeDpsOperator {

    private static final String SYNC_ACTION = "sync";

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncNodeDpsOperator.class);

    @EJB(lookup = DataAccessDelegate.JNDI_LOOKUP_NAME)
    private DataAccessDelegate dataAccessDelegate;
    @Inject
    @Modeled
    private EventSender<MediationTaskRequest> eventSender;

    @Inject
    private QueryDpsOperator queryDpsOperator;

    @EJB
    private MoDpsController moDpsController;

    public void sync(final String nodeName) {
        LOGGER.debug("Initiating sync by turning SUPERVISION ON...");
        moDpsController.setAttribute(FdnUtil.getCmNodeHeartbeatSupervisionFdn(nodeName), ACTIVE_ATTR_NAME, Boolean.TRUE);
    }

    public void syncViaAction(final String nodeName) {
        LOGGER.debug("Initiating sync by triggering SYNC ACTION...");
        final ManagedObject cmFunctionMo = moDpsController.getMo(FdnUtil.getCmFunctionFdn(nodeName));
        dataAccessDelegate.performAction(cmFunctionMo, SYNC_ACTION, new HashMap<String, Object>());
    }

    public String getSyncStatus(final String nodeName) {
        final ManagedObject cmFunctionMo = moDpsController.getMo(FdnUtil.getCmFunctionFdn(nodeName));
        return cmFunctionMo.getAttribute(SYNC_STATUS_ATTR_NAME);
    }

    public Map<String, String> getSyncStatuses() {
        return queryDpsOperator.getAttrValues(OSS_NE_CM_DEF_NAMESPACE, CM_FUNCTION_MO_TYPE, SYNC_STATUS_ATTR_NAME);
    }

    public boolean checkSupervisionActive(final String nodeName) {
        final boolean activeAttr = moDpsController.getAttribute(FdnUtil.getCmNodeHeartbeatSupervisionFdn(nodeName), ACTIVE_ATTR_NAME);
        if (!activeAttr) {
            LOGGER.debug("Supervision for '{}' is already turned off.", nodeName);
            return false;
        }

        return true;
    }

}
