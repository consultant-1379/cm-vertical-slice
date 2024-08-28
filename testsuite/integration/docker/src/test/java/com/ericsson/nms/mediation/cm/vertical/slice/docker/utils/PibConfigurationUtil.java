/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.COLON;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PibConfigurationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PibConfigurationUtil.class);

    private static final String USERNAME = "pibUser";
    private static final String PASSWORD = "3ric550N*";

    public static void addPibParameter(final String hostName, final String port, final String paramName, final String paramValue,
            final String paramType, final String paramScope) throws IOException {
        LOGGER.info("Attempting to add the PIB parameter {} with value {} on host {}:{}", paramName, paramValue, hostName, port);
        final String pibRestUrl = buildAddPibRestUrl(hostName, port, paramName, paramValue, paramType, paramScope);
        final URL url = new URL(pibRestUrl);

        final String userPass = DatatypeConverter.printBase64Binary((USERNAME + COLON + PASSWORD).getBytes("UTF-8"));
        final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestProperty("Authorization", "Basic " + userPass);
        httpConnection.setRequestMethod("GET");
        LOGGER.info("The rest call, {}, returned with response code {}", pibRestUrl, httpConnection.getResponseCode());
    }

    private static String buildAddPibRestUrl(final String hostName, final String port, final String paramName, final String paramValue,
            final String paramType, final String paramScope) {
        final StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(hostName);
        url.append(COLON);
        url.append(port);
        url.append("/pib/configurationService/addConfigParameter");
        url.append("?paramName=");
        url.append(paramName);
        url.append("&paramValue=");
        url.append(paramValue);
        url.append("&paramType=");
        url.append(paramType);
        url.append("&paramScopeType=");
        url.append(paramScope);
        return url.toString();
    }

    public static void setPibParameter(final String hostName, final String port, final String paramName, final String paramValue) throws IOException {
        LOGGER.info("Attempting to set the PIB parameter {} to value {} on host {}:{}", paramName, paramValue, hostName, port);
        final String pibRestUrl = buildSetPibRestUrl(hostName, port, paramName, paramValue);
        final URL url = new URL(pibRestUrl);

        final String userPass = DatatypeConverter.printBase64Binary((USERNAME + COLON + PASSWORD).getBytes("UTF-8"));
        final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestProperty("Authorization", "Basic " + userPass);
        httpConnection.setRequestMethod("GET");
        LOGGER.info("The rest call, {}, returned with response code {}", pibRestUrl, httpConnection.getResponseCode());
    }

    private static String buildSetPibRestUrl(final String hostName, final String port, final String paramName, final String paramValue) {
        final StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(hostName);
        url.append(COLON);
        url.append(port);
        url.append("/pib/configurationService/updateConfigParameterValue");
        url.append("?paramName=");
        url.append(paramName);
        url.append("&paramValue=");
        url.append(paramValue);
        return url.toString();
    }

}
