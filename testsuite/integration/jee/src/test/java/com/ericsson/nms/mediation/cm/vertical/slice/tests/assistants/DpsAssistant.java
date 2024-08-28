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
package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import java.util.List;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;

/**
 * Test helper class.<br>
 * Executes operations/queries relevant to DPS.
 */
public class DpsAssistant {

    private static final Logger LOGGER = LoggerFactory.getLogger(DpsAssistant.class);

    @EJB
    private MoDpsController moDpsController;

    public boolean moExists(final String nodeName, final String fdn) {
        final String ossFdn = FdnUtil.prependMeContextToFdn(nodeName, fdn);
        final boolean moExists = moDpsController.moExists(ossFdn);

        LOGGER.debug("Does '{}' MO exist in DPS: {}", ossFdn, moExists);
        return moExists;
    }

    public int getElementsCountInAttribute(final String nodeName, final String fdn, final String attributeName) {
        final String ossFdn = FdnUtil.prependMeContextToFdn(nodeName, fdn);
        final List<?> elementsInAttribute = moDpsController.getAttribute(ossFdn, attributeName);

        return elementsInAttribute.size();
    }

}
