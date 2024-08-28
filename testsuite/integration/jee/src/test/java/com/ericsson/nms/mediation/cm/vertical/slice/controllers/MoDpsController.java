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

import java.util.Map;
import java.util.OptionalLong;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.exception.MoNotFoundException;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.common.DpsFacade;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;

/**
 * Facade allowing to access all Managed Object DPS operations from one central point.
 * <p>
 * Provides MO manipulation methods allowing to read/query MOs and read/update their attributes in DPS.
 */
@Stateless
@LocalBean
public class MoDpsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoDpsController.class);

    @Inject
    private DpsFacade dpsFacade;

    public void deleteMo(final String fdn) {
        final DataBucket liveBucket = dpsFacade.getLiveBucket();

        final ManagedObject moToDelete = liveBucket.findMoByFdn(fdn);
        if (moToDelete != null) {
            liveBucket.deletePo(moToDelete);
            LOGGER.trace("Deleted '{}' MO.", moToDelete);
        }
    }

    public OptionalLong getTargetPoId(final String fdn) {
        final ManagedObject mo = dpsFacade.getLiveBucket().findMoByFdn(fdn);
        if (mo != null) {
            final PersistenceObject targetPo = mo.getTarget();
            if (targetPo != null) {
                return OptionalLong.of(targetPo.getPoId());
            }
        }
        return OptionalLong.empty();
    }

    public boolean moExists(final String fdn) {
        return dpsFacade.getLiveBucket().findMoByFdn(fdn) != null;
    }

    public boolean poExists(final long poId) {
        return dpsFacade.getLiveBucket().findPoById(poId) != null;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public ManagedObject getMo(final String fdn) {
        final ManagedObject mo = dpsFacade.getLiveBucket().findMoByFdn(fdn);
        if (mo == null) {
            throw new MoNotFoundException(fdn);
        }
        return mo;
    }

    public String getVersion(final String fdn) {
        final ManagedObject mo = getMo(fdn);
        return mo.getVersion();
    }

    public <T> T getAttribute(final String fdn, final String attributeName) {
        final ManagedObject mo = getMo(fdn);
        final T attrValue = mo.getAttribute(attributeName);
        LOGGER.debug("[{}] getAttribute: {}='{}'", fdn, attributeName, attrValue);
        return attrValue;
    }

    public Map<String, Object> getAllAttributes(final String fdn) {
        final ManagedObject mo = getMo(fdn);
        final Map<String, Object> attrMap = mo.getAllAttributes();
        LOGGER.debug("[{}] getAllAttributes: {}", fdn, attrMap);
        return attrMap;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setAttribute(final String fdn, final String attributeName, final Object attributeValue) {
        final ManagedObject mo = getMo(fdn);
        mo.setAttribute(attributeName, attributeValue);
        LOGGER.debug("[{}] setAttribute: {}='{}'", fdn, attributeName, attributeValue);
    }
}
