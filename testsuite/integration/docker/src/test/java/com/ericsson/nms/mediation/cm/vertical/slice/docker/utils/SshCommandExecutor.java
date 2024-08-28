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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshCommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshCommandExecutor.class);

    public static String exec(final String hostname, final String command) throws IOException {
        final List<String> cmdarray = new ArrayList<>();
        cmdarray.add("/usr/bin/ssh");
        cmdarray.add("root@" + hostname);
        cmdarray.add(command);

        LOGGER.info("About to execute: {}", cmdarray);
        final Process process = Runtime.getRuntime().exec(cmdarray.toArray(new String[0]));
        final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output;
        final StringBuilder sb = new StringBuilder();
        while ((output = br.readLine()) != null) {
            LOGGER.info("Output: {}", output);
            sb.append(output).append('\n');
        }
        try {
            process.waitFor();
        } catch (final InterruptedException e) {
            throw new IOException("Interrupted while waiting for ssh command completion", e);
        }
        LOGGER.info("Exit code: {}", process.exitValue());
        process.destroy();
        return sb.toString();
    }
}
