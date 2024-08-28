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

import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.modeling.common.event.ModelDeploymentEventChannel;
import com.ericsson.oss.itpf.modeling.mdt.remote.ModelDeploymentService;
import com.ericsson.oss.itpf.modeling.mdt.remote.ModelDeploymentServiceImpl;

public class ModelDeploymentServiceUtil {

    private static final String MODEL_ROOT_PROP = "MODEL_ROOT";
    private static final String MODEL_ROOT = "target/model_repo";
    private static final String XML_REPO_PATH = "XML_REPO_PATH";
    private static final String MODEL_REPO_CHECKSUM_FILE_PROP = "repoChecksumFilePath";
    private static final String MODEL_REPO_CHECKSUM_FILE = MODEL_ROOT + "/mdtrepo.checksum";
    private static final String SEND_EVENTS_PROP = "mdt.send.events";
    private static final String SEND_EVENTS = "true";
    private static final String RMI_STUB_LOCATION = MODEL_ROOT + "/ModelDeploymentService";
    private static final String TRANSPORT_CONFIG_LOCATION = "target";
    private static final int RMI_PORT = 2053;
    private static final String MDT_REPORT_DIRECTORY_PROP = "mdt.report.directory";
    private static final String MDT_REPORT_DIRECTORY = MODEL_ROOT + "/report";
    private static final String JMS_HOST_PORT_PROP = "jms.host.port";
    /*
     * The default value is 4447: we need to adjust with port offset (see
     * property jboss.socket.binding.port-offset in arquillian.xml)
     */
    private static final int JMS_HOST_PORT = 4447 + 629;

    /**
     * The Model Deployment Service is defined static to avoid being garbage
     * collected between tests (in arquillian, test class is instantiated for
     * each single test). If the Model Deployment Service is garbage collected,
     * then clients would no longer be able to make remote invocations.
     */
    private static ModelDeploymentService modelDeploymentService = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDeploymentServiceUtil.class);

    private ModelDeploymentServiceUtil() {

    }

    public static void setupMdtSystemProperties() {
        System.setProperty(MODEL_ROOT_PROP, MODEL_ROOT);
        System.setProperty(XML_REPO_PATH, MODEL_ROOT + "/mdtrepo.xml");
        System.setProperty(MODEL_REPO_CHECKSUM_FILE_PROP, MODEL_REPO_CHECKSUM_FILE);
        System.setProperty(SEND_EVENTS_PROP, SEND_EVENTS);
        System.setProperty(ModelDeploymentService.RMI_STUB_LOCATION_JVM_PROP, RMI_STUB_LOCATION);
        System.setProperty(ModelDeploymentService.RMI_PORT_NUMBER_JVM_PROP, Integer.toString(RMI_PORT));
        System.setProperty(ModelDeploymentEventChannel.TRANSPORT_CONFIG_LOCATION_JVM_PROP, TRANSPORT_CONFIG_LOCATION);
        System.setProperty(JMS_HOST_PORT_PROP, Integer.toString(JMS_HOST_PORT));
        System.setProperty(MDT_REPORT_DIRECTORY_PROP, MDT_REPORT_DIRECTORY);
    }

    public static void resetMdtSystemProperties() {
        System.clearProperty(MODEL_ROOT_PROP);
        System.clearProperty(MODEL_REPO_CHECKSUM_FILE_PROP);
        System.clearProperty(XML_REPO_PATH);
        System.clearProperty(SEND_EVENTS_PROP);
        System.clearProperty(ModelDeploymentService.RMI_STUB_LOCATION_JVM_PROP);
        System.clearProperty(ModelDeploymentService.RMI_PORT_NUMBER_JVM_PROP);
        System.clearProperty(ModelDeploymentEventChannel.TRANSPORT_CONFIG_LOCATION_JVM_PROP);
        System.clearProperty(JMS_HOST_PORT_PROP);
        System.clearProperty(MDT_REPORT_DIRECTORY_PROP);
    }

    public static void setupModelDeploymentService() throws IOException {
        if (modelDeploymentService == null) {
            setupMdtSystemProperties();
            LOGGER.info("Setting up Model Deployment Service RMI stub: {}, port {}", RMI_STUB_LOCATION, RMI_PORT);
            modelDeploymentService = new ModelDeploymentServiceImpl();
            final Remote modelDeployServiceRemoteStub = UnicastRemoteObject.exportObject(modelDeploymentService, RMI_PORT);
            try (final ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(RMI_STUB_LOCATION))) {
                outputStream.writeObject(modelDeployServiceRemoteStub);
            }
            addCleanupHook();
        }
    }

    public static void cleanupModelDeploymentService() throws IOException {
        LOGGER.info("Cleaning up Model Deployment Service RMI stub: {}", modelDeploymentService);
        assertTrue(UnicastRemoteObject.unexportObject(modelDeploymentService, true));
        Files.delete(Paths.get(RMI_STUB_LOCATION));
        modelDeploymentService = null;
        resetMdtSystemProperties();
    }

    private static void addCleanupHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ModelDeploymentServiceUtil.cleanupModelDeploymentService();
                } catch (final IOException e) {
                    LOGGER.warn("I/O error while cleaning up Model Deployment Service RMI Stub", e);
                }
            }
        });
    }

}
