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
package com.ericsson.nms.mediation.cm.vertical.slice.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facilitates time-related utility methods.
 */
public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {
    }

    public static String convertToMinSecMs(final long milliseconds) {
        final long min = milliseconds / 1000 / 60;
        final long sec = milliseconds / 1000 % 60;
        final long ms = milliseconds % 1000;

        final StringBuilder time = new StringBuilder();
        time.append(min);
        time.append(" min ");
        time.append(sec);
        time.append(" sec ");
        time.append(ms);
        time.append(" ms");

        return time.toString();
    }

    public static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException exception) {
            LOGGER.error("Error while putting the thread to sleep.", exception);
        }
    }

}
