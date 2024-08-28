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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to send REST requests to manipulate configuration parameters using Platform Integration Bridge.
 */
public class PibConfigurationUtil {

    private static final String HEARTBEAT_INTERVAL_PIB_PARAM_NAME = "cm_policy_heartbeat_interval_timer_value_millisecs";
    private static final String HEARTBEAT_RETRY_COUNT_PIB_PARAM_NAME = "cm_policy_heartbeat_count_to_retry_sync";
    private static final String NOTIF_BUFFER_SIZE_PIB_CONFIG_PARAM_NAME = "cm_notification_buffer_size_per_node";
    private static final String UPGRADE_SHARED_DIR_PARAM_NAME = "upgSharedDir";
    private static final String ENABLED_TLS_PROTOCOLS_ECIM_PARAM_NAME = "enabledTLSProtocolsECIM";

    private static final String STRING_PIB_CONFIG_PARAM_TYPE_NAME = "String";
    private static final String LONG_PIB_CONFIG_PARAM_TYPE_NAME = "long";
    private static final String INT_PIB_CONFIG_PARAM_TYPE_NAME = "int";

    private static final String PARAM_SCOPE_GLOBAL = "GLOBAL";
    private static final String PARAM_SCOPE_JVM_AND_SERVICE = "JVM_AND_SERVICE";

    private static final String ADD_CONFIG_PARAMETER_REST_SERVICE_NAME = "addConfigParameter";
    private static final String UPDATE_CONFIG_PARAMETER_REST_SERVICE_NAME = "updateConfigParameterValue";
    private static final String GET_CONFIG_PARAMETER_REST_SERVICE_NAME = "getConfigParameter";

    private static final String DEFAULT_JVM_INSTANCE_ID = "CM-VS-JEE-TEST";
    private static final String DEFAULT_JBOSS_IP = "localhost";
    private static final int DEFAULT_PORT_OFFSET = 629;
    private static final String GET_HTTP_METHOD = "GET";

    private static final String USERNAME = "pibUser";
    private static final String PASSWORD = "3ric550N*";
    private static final String AUTHORIZATION_CHARSET = "UTF-8";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_ENCODING_PREFIX = "Basic ";

    private static final Logger LOGGER = LoggerFactory.getLogger(PibConfigurationUtil.class);

    private PibConfigurationUtil() {
    }

    public static void updateHeartbeatRetryCount(final int count) throws IOException {
        updateConfigParameter(HEARTBEAT_RETRY_COUNT_PIB_PARAM_NAME, String.valueOf(count), INT_PIB_CONFIG_PARAM_TYPE_NAME,
                PARAM_SCOPE_JVM_AND_SERVICE);
    }

    public static void updateHearbeatIntervalTime(final long milliSeconds) throws IOException {
        updateConfigParameter(HEARTBEAT_INTERVAL_PIB_PARAM_NAME, String.valueOf(milliSeconds), LONG_PIB_CONFIG_PARAM_TYPE_NAME,
                PARAM_SCOPE_JVM_AND_SERVICE);
    }

    public static void updateNotifBufferSizePerNode(final int bufferSize) throws IOException {
        updateConfigParameter(NOTIF_BUFFER_SIZE_PIB_CONFIG_PARAM_NAME, String.valueOf(bufferSize), INT_PIB_CONFIG_PARAM_TYPE_NAME,
                PARAM_SCOPE_JVM_AND_SERVICE);
    }

    public static int getNotifBufferSizePerNode() throws IOException {
        return (int) getConfigParameter(NOTIF_BUFFER_SIZE_PIB_CONFIG_PARAM_NAME, INT_PIB_CONFIG_PARAM_TYPE_NAME);
    }

    public static void updateUpgradeSharedDir(final String newDir) throws IOException {
        updateConfigParameter(UPGRADE_SHARED_DIR_PARAM_NAME, newDir, STRING_PIB_CONFIG_PARAM_TYPE_NAME, PARAM_SCOPE_JVM_AND_SERVICE);
    }

    public static void addEnabledTLSProtocolsECIM(final String value) throws IOException {
        addConfigParameter(ENABLED_TLS_PROTOCOLS_ECIM_PARAM_NAME, value, STRING_PIB_CONFIG_PARAM_TYPE_NAME, PARAM_SCOPE_GLOBAL);
    }

    public static void addConfigParameter(final String paramName, final String paramValue, final String paramType, final String paramScope)
            throws IOException {
        LOGGER.debug("Adding '{}' parameter with value: '{}'...", paramName, paramValue);
        issueRestCall(generateConfigParamRestUrl(paramName, paramValue, paramType, paramScope, ADD_CONFIG_PARAMETER_REST_SERVICE_NAME));
    }

    private static void updateConfigParameter(final String paramName, final String paramValue, final String paramType, final String paramScope)
            throws IOException {
        LOGGER.debug("Updating '{}' parameter with value: '{}'...", paramName, paramValue);
        issueRestCall(generateConfigParamRestUrl(paramName, paramValue, paramType, paramScope, UPDATE_CONFIG_PARAMETER_REST_SERVICE_NAME));
    }

    private static Object getConfigParameter(final String paramName, final String paramType) throws IOException {
        LOGGER.debug("Getting '{}' parameter...", paramName);
        final Object getResponse = issueGetRestCall(generateGetConfigParamRestUrl(paramName), paramType);
        LOGGER.debug("...obtained the value '{}'", getResponse);
        return getResponse;
    }

    private static String generateConfigParamRestUrl(final String paramName, final String paramValue, final String paramType,
            final String paramScope, final String action) {
        final String jvmInstanceId = System.getProperty("com.ericsson.oss.sdk.node.identifier", DEFAULT_JVM_INSTANCE_ID);
        final String jbossIp = System.getProperty("jboss.bind.address.unsecure", DEFAULT_JBOSS_IP);
        final Integer portOffset = Integer.getInteger("jboss.socket.binding.port-offset", DEFAULT_PORT_OFFSET);
        final int port = 8080 + portOffset;

        final StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(jbossIp);
        url.append(":");
        url.append(port);
        url.append("/pib/configurationService/");
        url.append(action);
        url.append("?paramName=");
        url.append(paramName);
        url.append("&paramValue=");
        url.append(paramValue);
        url.append("&paramType=");
        url.append(paramType);
        url.append("&paramScopeType=");
        url.append(paramScope);
        if (PARAM_SCOPE_JVM_AND_SERVICE.equals(paramScope)) {
            url.append("&serviceIdentifier=mediationservice");
            url.append("&jvmIdentifier=");
            url.append(jvmInstanceId);
        }
        LOGGER.trace("Constructed URL: '{}'", url.toString());

        return url.toString();
    }

    private static void issueRestCall(final String restUrl) throws IOException {
        final String autorizationEncoding = DatatypeConverter.printBase64Binary((USERNAME + ":" + PASSWORD).getBytes(AUTHORIZATION_CHARSET));

        final URL url = new URL(restUrl);
        final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_ENCODING_PREFIX + autorizationEncoding);
        httpConnection.setRequestMethod(GET_HTTP_METHOD);

        LOGGER.debug("HTTP request: '{}'.", restUrl);
        LOGGER.debug("HTTP response code: '{}'.", httpConnection.getResponseCode());
    }

    private static String generateGetConfigParamRestUrl(final String paramName) {
        final String jvmInstanceId = System.getProperty("com.ericsson.oss.sdk.node.identifier", DEFAULT_JVM_INSTANCE_ID);
        final String jbossIp = System.getProperty("jboss.bind.address.unsecure", DEFAULT_JBOSS_IP);
        final Integer portOffset = Integer.getInteger("jboss.socket.binding.port-offset", DEFAULT_PORT_OFFSET);
        final int port = 8080 + portOffset;
        final StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(jbossIp);
        url.append(":");
        url.append(port);
        url.append("/pib/configurationService/");
        url.append(GET_CONFIG_PARAMETER_REST_SERVICE_NAME);
        url.append("?paramName=");
        url.append(paramName);
        url.append("&paramScopeType=JVM_AND_SERVICE");
        url.append("&serviceIdentifier=mediationservice");
        url.append("&jvmIdentifier=");
        url.append(jvmInstanceId);
        LOGGER.trace("Constructed URL: '{}'", url.toString());

        return url.toString();
    }

    private static Object issueGetRestCall(final String restUrl, final String resultType) throws IOException {
        Object resultObject = null;
        final String autorizationEncoding = DatatypeConverter.printBase64Binary((USERNAME + ":" + PASSWORD).getBytes(AUTHORIZATION_CHARSET));
        final URL obj = new URL(restUrl);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_ENCODING_PREFIX + autorizationEncoding);
        con.setRequestMethod(GET_HTTP_METHOD);

        final int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            final InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
            final StringBuffer response = new StringBuffer();

            try (final BufferedReader in = new BufferedReader(inputStreamReader)) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            resultObject = parseGetRestCallResponse(response.toString(), resultType);
        }

        return resultObject;
    }

    private static Object parseGetRestCallResponse(final String result, final String resultType) {
        Object parsedResult = null;
        final String[] parts = result.split("value=");
        if (parts != null && parts[1] != null) {
            final String[] val = parts[1].split(",");
            if (val != null && val[0] != null) {
                if (INT_PIB_CONFIG_PARAM_TYPE_NAME.equals(resultType)) {
                    parsedResult = Integer.parseInt(val[0]);
                } else if (LONG_PIB_CONFIG_PARAM_TYPE_NAME.equals(resultType)) {
                    parsedResult = Long.parseLong(val[0]);
                } else if (STRING_PIB_CONFIG_PARAM_TYPE_NAME.equals(resultType)) {
                    parsedResult = val[0];
                }
            }
        }
        return parsedResult;
    }

}
