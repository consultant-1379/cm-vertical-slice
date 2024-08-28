/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.utility.deployment;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

/**
 * Resolves artifacts, returning the relevant resolved package files.
 */
public class ArtifactUtil {

    private ArtifactUtil() {}

    /**
     * Resolves artifact for given coordinates, without including any dependencies. This method should be used to resolve just the artifact with given
     * name, and it can be used for adding artifacts as modules into EAR.
     * <p>
     * If artifact cannot be resolved, or the artifact was resolved into more then one file then the IllegalStateException will be thrown.
     *
     * @param artifactCoordinates
     *            Maven artifact coordinates (in the usual Maven format)
     *
     *            <pre>
     * {@code<groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
     *            </pre>
     *
     * @return package file representing the resolved artifact
     * @throws IllegalStateException
     */
    public static File resolveArtifactWithoutDependencies(final String artifactCoordinates) {
        final File artifact = getMavenResolver().resolve(artifactCoordinates).withoutTransitivity().asSingleFile();
        if (artifact == null) {
            throw new IllegalStateException("Artifact with coordinates '" + artifactCoordinates + "' was not resolved.");
        }
        return artifact;
    }

    /**
     * Returns Maven dependency resolver, allowing for the dependency resolving with transitive dependencies.
     *
     * @return MavenDependencyResolver built using the local POM file
     */
    private static PomEquippedResolveStage getMavenResolver() {
        return Maven.resolver().loadPomFromFile("pom.xml");
    }

}
