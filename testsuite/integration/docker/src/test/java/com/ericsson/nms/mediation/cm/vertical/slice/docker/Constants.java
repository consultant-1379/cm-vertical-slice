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

package com.ericsson.nms.mediation.cm.vertical.slice.docker;

public final class Constants {

    public static final String SHARED_DIR_ENV_VAR = "ENM_SHARED_DIR";

    public static final String WAITINTERVALMILLISECONDS = "waitIntervalMilliseconds";
    public static final String WAITTIMEMINUTES = "waitTimeMinutes";

    public static final String CLUSTEREDMEDIATIONSERVICECONSUMERCM0 = "ClusteredMediationServiceConsumerCM0";
    public static final String NETWORKELEMENTNOTIFICATIONS = "NetworkElementNotifications";
    public static final String SYNCSTATUSCHANGETOPIC = "SyncStatusChangeTopic";
    public static final String CLUSTEREDNETWORKELEMENTSUBSCRIPTIONS = "ClusteredNetworkElementSubscriptions";

    public static final String JMSQUEUE = "jms-queue";
    public static final String JMSTOPIC = "jms-topic";
    public static final String JMSSERVER = "jms-server";
    public static final String HORNETQSERVER = "hornetq-server";
    public static final String PIB_HOST = System.getProperty("jboss.bind.address.internal");
    public static final String PIB_PORT = "8080";
    public static final String CM_HEARTBEAT_INTERVAL = "cm_policy_heartbeat_interval_timer_value_millisecs";
    public static final String CM_HEARTBEAT_INTERVAL_DEFAULT = "420000";
    public static final String CM_NOTIFICATION_EVICTION_INTERVAL = "cm_notification_buffer_eviction_time_value_milliseconds";
    public static final String CM_NOTIFICATION_EVICTION_INTERVAL_DEFAULT = "420000";
    public static final String ENABLED_TLS_PROTOCOLS_ECIM = "enabledTLSProtocolsECIM";
    public static final String SUBSYSTEM = "subsystem";
    public static final String MESSAGING = "messaging";
    public static final String DEFAULT = "default";
    public static final String RESUME = "resume";

    public static final String DPS = "dps";
    public static final String NETSIM = "netsim";

    public static final String EMPTY_STRING = "";
    public static final String EQUALS = "=";
    public static final String EQUALS_ONE = "=1";
    public static final String COMMA = ",";
    public static final String WILDCARD = "*";
    public static final String COLON = ":";
    public static final String SPACE = " ";

    public static final char CHAR_A = 'A';

    public static final long DPS_POLLING_TIME_IN_MILLI_SECONDS = 1000;
    public static final long DPS_WAIT_TIME_IN_MINUTES = 4;

    public static final String PORT = "port";
    public static final String Http_PORT_80 = "80";
    public static final String FORMAT_STRING = "%s";

    public static final String BOOLEAN = "Boolean";
    public static final String STRING = "String";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";

    public static final String CHAR_ENCODING = "UTF-8";
}
