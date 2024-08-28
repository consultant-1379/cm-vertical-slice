/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.utility.constants;

/**
 * General constants.
 */
public class MiscellaneousConstants {

    // Timeouts and Intervals
    public static final int SYNC_TIMEOUT = 90 * 1000;
    public static final int DELTA_SYNC_TIMEOUT = 15 * 1000;
    public static final int SUPERVISION_OFF_TIMEOUT = 20 * 1000;
    public static final int NOTIFICATION_PROCESS_TIME = 2 * 1000;
    public static final int SYNC_FAILURE_WAIT_TIME = 15 * 1000; // 5s for connection to be refused; 2 connection attempts
    public static final int SUPERVISION_FAILURE_WAIT_TIME = 10 * 1000; // 1 connection attempt

    // SoftwareSync
    public static final String SWSYNC_SHARED_DIR = "target/shared_dir";

    // JMS QUEUE
    public static final String CONNECTION_FACTORY = "/JmsXA";
    public static final String QUEUE_NAME = "queue/NetworkElementNotifications";
    public static final int QUEUE_PRIORITY = 6;

    // System Property
    public static final String JBOSS_SERVER_NAME_SYSTEM_PROPERTY = "jboss.server.name";

    private MiscellaneousConstants() {}

}
