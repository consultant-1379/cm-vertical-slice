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
package com.ericsson.nms.mediation.cm.vertical.slice.controllers;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.AddNodeDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.DeleteNodeDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.SyncNodeDpsOperator;

/**
 * Facade allowing to access all node DPS operations from one central point.
 * <p>
 * Delegates the calls to the respective node DPS operators, providing automatic transaction handling.<br>
 * <b>NOTE</b>: Overarching transactions are not desired when delegating 'delete-node' methods.
 */
@Stateless
@LocalBean
public class NodeDpsController {

    @Inject
    private AddNodeDpsOperator addNodeDpsOperator;
    @Inject
    private DeleteNodeDpsOperator deleteNodeOperator;
    @Inject
    private SyncNodeDpsOperator syncNodeOperator;

    public void addNode(final TestNode node) {
        addNodeDpsOperator.addNode(node);
    }

    public void addNodes(final List<TestNode> nodes) {
        addNodeDpsOperator.addNodes(nodes);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteNode(final TestNode node) {
        deleteNodeOperator.deleteNode(node);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteNodes(final List<TestNode> nodes) {
        deleteNodeOperator.deleteNodes(nodes);
    }

    public void sync(final String nodeName) {
        syncNodeOperator.sync(nodeName);
    }

    public void syncViaAction(final String nodeName) {
        syncNodeOperator.syncViaAction(nodeName);
    }

    public String getSyncStatus(final String nodeName) {
        return syncNodeOperator.getSyncStatus(nodeName);
    }

    public Map<String, String> getSyncStatuses() {
        return syncNodeOperator.getSyncStatuses();
    }

}
