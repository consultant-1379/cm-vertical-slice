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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.BOOLEAN;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.INTEGER;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.LONG;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.STRING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeVerifier.class);

    /**
     * This methods verifies a attribute value in dps for an fdn is as expected. Currently, this
     * only verifies some attributes of primitive types. The verification for the more complex
     * attribute types will be need to be extended here further
     * @param fdn
     *            the fdn of the Managed Object whose attribute is to be verified
     @ param dpsObject
     *            the manage attribute object to be verified
     @ param expectedValue
     *            the expect value of the attribute to be verified
     */
    public Boolean verifyAttributeInDps(final String fdn, final Object dpsObject, final String expectedValue) {
        final String simpleClassName = dpsObject.getClass().getSimpleName();
        LOGGER.info("The simpleClassName is {} for {}", simpleClassName, dpsObject);

        boolean result = false;
        switch (simpleClassName){
            case BOOLEAN:
                result = ((Boolean) dpsObject).booleanValue() == Boolean.valueOf(expectedValue).booleanValue();
                break;
            case INTEGER:
                result = ((Integer) dpsObject).intValue() == Integer.valueOf(expectedValue).intValue();
                break;
            case LONG:
                result = ((Long) dpsObject).intValue() == Integer.valueOf(expectedValue).longValue();
                break;
            case STRING:
                result = dpsObject.toString().equals(expectedValue);
                break;
            default:
                LOGGER.error("The attribute verification for this object type [{}] is not supported", simpleClassName);
        }

        LOGGER.info("Returning result is {} ", result);
        return result;

    }

}
