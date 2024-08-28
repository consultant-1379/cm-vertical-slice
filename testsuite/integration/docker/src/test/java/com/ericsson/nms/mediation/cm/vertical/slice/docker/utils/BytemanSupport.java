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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.SHARED_DIR_ENV_VAR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BytemanSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(BytemanSupport.class);

    private static final Path SHARED_BYTEMAN_DIR = Paths.get(System.getenv(SHARED_DIR_ENV_VAR), "byteman");
    private static final String SUBMIT_BYTEMAN_RULE = "/bin/bmsubmit.sh";
    private static final String HOST_EXT = ".host";

    /**
     * Creates a script with the specified rule and loads it on all hosts matching the supplied pattern.
     *
     * @param scriptName
     *            name of the byteman script
     * @param hostGlob
     *            the hostname glob pattern
     * @param rule
     *            the byteman rule
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void loadBytemanRules(final String scriptName, final String hostGlob, final String rule) throws IOException {
        final Path scriptFile = getScriptPath(scriptName);

        final File file = scriptFile.toFile();
        file.createNewFile();
        file.setExecutable(true);
        file.setWritable(true);

        final FileWriter writer = new FileWriter(file);
        try {
            writer.write(rule);
        } finally {
            writer.flush();
            writer.close();
        }

        for (final Entry<String, String> bytemanHost : getBytemanHosts(hostGlob).entrySet()) {
            final String hostname = bytemanHost.getKey();
            final String bytemanHome = bytemanHost.getValue();
            executeSshCommand(hostname, bytemanHome + SUBMIT_BYTEMAN_RULE + " -l " + scriptFile);
        }
    }

    /**
     * Unloads the rules in the specified script from all hosts matching the supplied pattern.
     *
     * @param scriptName
     *            name of the byteman script
     * @param hostGlob
     *            the hostname glob pattern
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void unloadBytemanRules(final String scriptName, final String hostGlob) throws IOException {
        final Path scriptFile = getScriptPath(scriptName);
        if (Files.exists(scriptFile)) {
            try {
                for (final Entry<String, String> bytemanHost : getBytemanHosts(hostGlob).entrySet()) {
                    final String hostname = bytemanHost.getKey();
                    final String bytemanHome = bytemanHost.getValue();
                    executeSshCommand(hostname, bytemanHome + SUBMIT_BYTEMAN_RULE + " -u " + scriptFile);
                }
            } finally {
                Files.delete(scriptFile);
            }
        }
    }

    private static Path getScriptPath(final String scriptName) {
        return SHARED_BYTEMAN_DIR.resolve(scriptName);
    }

    private static Map<String, String> getBytemanHosts(final String glob) throws IOException {
        final Map<String, String> bytemanHosts = new TreeMap<>();
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(SHARED_BYTEMAN_DIR, glob + HOST_EXT)) {
            for (final Path hostFile : dir) {
                final String filename = hostFile.getFileName().toString();
                final String hostname = filename.substring(0, filename.indexOf(HOST_EXT));
                final String bytemanHome = new String(Files.readAllBytes(hostFile));
                bytemanHosts.put(hostname, bytemanHome);
            }
        }
        return bytemanHosts;
    }

    private static void executeSshCommand(final String hostname, final String command) throws IOException {
        try {
            SshCommandExecutor.exec(hostname, command);
        } catch (final IOException ex) {
            LOGGER.error("There was an error creating or unloading the byteman rule stack trace = {}", ex);
            throw ex;
        }
    }


}
