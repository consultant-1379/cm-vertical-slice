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

package com.ericsson.nms.mediation.cm.vertical.slice.utility.deployment;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.ASSERT_J_ARTIFACT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.JSCH_ARTIFACT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.SOFTWARE_SYNC_EVENT_ARTIFACT;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants.SYNC_NODE_EVENT_ARTIFACT;

import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import com.ericsson.nms.mediation.cm.vertical.slice.common.SyncTriggerType;
import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.common.exception.MoNotFoundException;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.MoDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.ModelDeploymentServiceController;
import com.ericsson.nms.mediation.cm.vertical.slice.controllers.NodeDpsController;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.AddNodeDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.DeleteNodeDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.NetsimOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.QueryDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.SyncNodeDpsOperator;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.common.DpsFacade;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.ssh.SshCommandExecutor;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.DeltaSyncIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.FullSyncIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.InstrumentationIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.LargeRncSyncIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.NotificationIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.SupervisionIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.TreatAsIT;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.AssertAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.DpsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.InstrumentationAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.JmsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.NetsimAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.SyncAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants.TreatAsAssistant;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.deployment.DeploymentTest;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCase;
import com.ericsson.nms.mediation.cm.vertical.slice.tests.templates.SyncTestCaseSetup;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.DpsNotificationListener;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.ModelDeploymentServiceUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.PibConfigurationUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.DeploymentConstants;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.InstrumentationMetricsConstants;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MiscellaneousConstants;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.TestNodeConstants;

/**
 * Provides deployment archive files.
 * <p>
 * <b>Note:</b> To be used with arquillian-suite-extension only.
 */
public class DeploymentUtil {

    private DeploymentUtil() {}

    /**
     * Creates a Test Web Archive deployment.
     * <p>
     * <b>Note:</b> All classes used in this project need to be added to this archive (except the classes in utility.deployment package).
     *
     * @return web archive containing all the required classes within this project as well as external resources
     */
    public static Archive<?> createTestWar(final String name) {
        final WebArchive testWar = ShrinkWrap.create(WebArchive.class, name + ".war");

        // Driving Test classes
        testWar.addClasses(DeploymentTest.class, FullSyncIT.class, InstrumentationIT.class, DeltaSyncIT.class, SupervisionIT.class,
                NotificationIT.class, TreatAsIT.class, LargeRncSyncIT.class);
        // Supplementing Test Assistant classes
        testWar.addClasses(SyncTestCase.class, SyncTestCaseSetup.class, SyncAssistant.class, NetsimAssistant.class, DpsAssistant.class,
                AssertAssistant.class, TreatAsAssistant.class, InstrumentationAssistant.class, JmsAssistant.class);
        // Common classes
        testWar.addClasses(TestNode.class, SyncTriggerType.class, MoNotFoundException.class);
        // Operator classes
        testWar.addClasses(DpsFacade.class, QueryDpsOperator.class, AddNodeDpsOperator.class, DeleteNodeDpsOperator.class,
                SyncNodeDpsOperator.class, NetsimOperator.class);
        // EJB Controllers
        testWar.addClasses(NodeDpsController.class, MoDpsController.class, ModelDeploymentServiceController.class);
        // Utility classes
        testWar.addClasses(FdnUtil.class, PibConfigurationUtil.class, DpsNotificationListener.class, TimeUtil.class,
                SshCommandExecutor.class, ModelDeploymentServiceUtil.class);
        // Constants
        testWar.addClasses(MiscellaneousConstants.class, MoConstants.class, DeploymentConstants.class, TestNodeConstants.class,
                InstrumentationMetricsConstants.class);
        // External Dependencies required
        testWar.addAsLibraries(createJar(SYNC_NODE_EVENT_ARTIFACT));
        testWar.addAsLibraries(createJar(JSCH_ARTIFACT));
        testWar.addAsLibraries(createJar(SOFTWARE_SYNC_EVENT_ARTIFACT));
        testWar.addAsLibraries(createJar(ASSERT_J_ARTIFACT));
        // Resources
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsManifestResource(new File("src/test/resources/META-INF/MANIFEST.MF"));

        return testWar;
    }

    private static JavaArchive createJar(final String artifactName) {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, ArtifactUtil.resolveArtifactWithoutDependencies(artifactName));
    }

}
