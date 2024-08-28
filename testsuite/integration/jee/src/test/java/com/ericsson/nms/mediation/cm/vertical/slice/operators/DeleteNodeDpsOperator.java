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

import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.common.exception.MoNotFoundException;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

/**
 * Delete Node DPS service.
 * <p>
 * Deletes all node MOs in DPS, effectively erasing all information relevant to this node from DPS.
 */
@ApplicationScoped
public class DeleteNodeDpsOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteNodeDpsOperator.class);

    @Inject
    private SyncAssistant syncAssistant;
    @Inject
    private SyncNodeDpsOperator syncNodeDpsOperator;
    @EJB
    private MoDpsController moDpsController;

    public void deleteNodes(final List<TestNode> nodes) {
        LOGGER.info("--> Deleting nodes in DPS ({})...", nodes);
        for (final TestNode node : nodes) {
            deleteNode(node);
        }
        LOGGER.info("--> DELETED nodes in DPS ({}).", nodes);
    }

    public void deleteNode(final TestNode node) {
        final String nodeName = node.getName();

        LOGGER.debug("Deleting node: '{}'...", nodeName);

        try {
            if (syncNodeDpsOperator.checkSupervisionActive(nodeName)) {
                syncAssistant.turnSupervisionOffAndAssertUnsynchronized(nodeName);
            }
            moDpsController.deleteMo(FdnUtil.getMeContextFdn(nodeName));

            final String neFdn = FdnUtil.getNetworkElementFdn(nodeName);

            final OptionalLong targetPoId = moDpsController.getTargetPoId(neFdn);
            moDpsController.deleteMo(neFdn);
            targetPoId.ifPresent(this::waitForTargetDeletion);

        } catch (final MoNotFoundException exception) {
            LOGGER.debug("Node '{}' does not exist in DPS.", nodeName);
        }

        LOGGER.info("--> DELETED node: '{}'. <--", nodeName);
    }

    private void waitForTargetDeletion(final long targetPoId) {
        for (int n = 0; n < 30; ++n) {
            if (!moDpsController.poExists(targetPoId)) {
                LOGGER.debug("Target with poId={} has been deleted", targetPoId);
                return;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (final InterruptedException e) {
                // no-op
            }
        }
        throw new IllegalStateException("Target with poId=" + targetPoId + " has not been deleted");
    }
}
