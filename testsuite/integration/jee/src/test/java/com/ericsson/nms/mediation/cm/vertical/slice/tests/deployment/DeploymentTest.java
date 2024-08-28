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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.*;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants.SWSYNC_SHARED_DIR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.utility.ModelDeploymentServiceUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.PibConfigurationUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.deployment.DeploymentUtil;

/**
 * Base for the Vertical Slice tests.<br>
 * Provides the deployment definitions and deployment execution methods, relieving the concrete tests from worrying about the deployment.
 * <p>
 * Test suites should extend this class and implement only the relevant test cases specific to the suite.
 */
@ArquillianSuiteDeployment
public abstract class DeploymentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentTest.class);
    private static final AtomicBoolean SUITE_SETUP = new AtomicBoolean();

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void starting(final Description d) {
            if (isRunningInContainer()) {
                LOGGER.info("### Class [" + d.getTestClass().getSimpleName() + "] Test " + d.getMethodName() + " starting");
            }
        }

        @Override
        protected void succeeded(final Description d) {
            if (isRunningInContainer()) {
                LOGGER.info("### Class [" + d.getTestClass().getSimpleName() + "] Test " + d.getMethodName() + " succeeded");
            }
        }

        @Override
        protected void failed(final Throwable e, final Description d) {
            if (isRunningInContainer()) {
                LOGGER.info("### Class [" + d.getTestClass().getSimpleName() + "] Test " + d.getMethodName() + " failed: " + e);
            }
        }
    };

    private static boolean isRunningInContainer() {
        try {
            new InitialContext().lookup("java:comp/env");
            return true;
        } catch (final NamingException e) {
            return false;
        }
    }

    /**
     * Arquillian Driving EAR deployment (tests should be run using this deployment).
     */
    @Deployment(name = VS_TEST_DEPLOYMENT_NAME, testable = true, order = Integer.MAX_VALUE)
    public static Archive<?> createVsTestDeployment() {
        return DeploymentUtil.createTestWar(VS_TEST_DEPLOYMENT_NAME);
    }

    @BeforeClass
    @RunAsClient
    public static void classSetup() throws IOException {
        // execute setup once per suite
        if (SUITE_SETUP.compareAndSet(false, true)) {
            LOGGER.info("Setting up PIB parameters");
            PibConfigurationUtil.addEnabledTLSProtocolsECIM("TLSv1.2");
            LOGGER.info("Setting up shared directory for Model Deployment");
            Files.createDirectories(Paths.get(SWSYNC_SHARED_DIR));
            PibConfigurationUtil.updateUpgradeSharedDir(SWSYNC_SHARED_DIR);
            ModelDeploymentServiceUtil.setupModelDeploymentService();
        }
    }

}
