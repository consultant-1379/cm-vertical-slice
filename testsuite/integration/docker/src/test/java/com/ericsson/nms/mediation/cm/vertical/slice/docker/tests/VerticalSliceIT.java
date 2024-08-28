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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.tests;

import java.io.File;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.runtime.arquillian.ArquillianCucumber;

@RunWith(ArquillianCucumber.class)
@CucumberOptions(features = "src/test/resources/features",
        glue = { "com.ericsson.nms.mediation.cm.vertical.slice.docker",
            "com.ericsson.nms.mediation.cm.vertical.slice.docker.steps",
            "com.ericsson.nms.mediation.cm.vertical.slice.docker.tests",
            "com.ericsson.nms.mediation.cm.vertical.slice.docker.utils",
            "com.ericsson.oss.services.test.netsim.step" },
        plugin = { "pretty", "json:/opt/cucumber-report/cucumber.json" },
        tags = { "@AddAndSyncNode,@RunAllTests,@Teardown" })
@ArquillianSuiteDeployment
public class VerticalSliceIT {

    @Deployment
    public static EnterpriseArchive getTestEar() {
        final File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "vertical-slice-tests.war")
                .addPackage("com.ericsson.nms.mediation.cm.vertical.slice.docker")
                .addPackage("com.ericsson.nms.mediation.cm.vertical.slice.docker.steps")
                .addPackage("com.ericsson.nms.mediation.cm.vertical.slice.docker.tests")
                .addPackage("com.ericsson.nms.mediation.cm.vertical.slice.docker.utils")
                .addClass(VerticalSliceIT.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("MANIFEST.MF");

        return ShrinkWrap.create(EnterpriseArchive.class, "vertical-slice-tests.ear")
                .addAsManifestResource("jboss-deployment-structure.xml")
                .addAsModule(testWar)
                .addAsLibraries(files)
                .addAsResource("ServiceFrameworkConfiguration.properties");
    }
}
