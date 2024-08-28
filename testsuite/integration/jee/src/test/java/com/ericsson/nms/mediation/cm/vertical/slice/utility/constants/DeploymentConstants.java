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

package com.ericsson.nms.mediation.cm.vertical.slice.utility.constants;

/**
 * Deployment identifiers, Artifact names and other deployment-related constants.
 */
public class DeploymentConstants {

    // Artifacts
    // Note: Below artifacts should never contain a dependency with a version. Versions should be managed in the relevant POM file.
    public static final String SOFTWARE_SYNC_EVENT_ARTIFACT = "com.ericsson.oss.mediation.events:software-sync-event-model-jar:jar:?";
    // ---
    public static final String SYNC_NODE_EVENT_ARTIFACT = "com.ericsson.oss.mediation.cm.events:sync-node-event-jar:jar:?";
    public static final String JSCH_ARTIFACT = "com.jcraft:jsch:jar:?";
    public static final String ASSERT_J_ARTIFACT = "org.assertj:assertj-core:jar:?";
    // Deployments
    public static final String VS_TEST_DEPLOYMENT_NAME = "vertical-slice-test";

    private DeploymentConstants() {}

}
