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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CHAR_A;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.WILDCARD;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NE_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.RELEASE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;

/**
 * This is a Model Service helper class for any Model Service related operations.
 * Will need refactoring once adequate structure is in place
 * @author ebrione
 */
@Startup
@Singleton
public class ModelServiceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceHelper.class);
    private static final String FDN_NAME_URN_TEMPLATE = "/%s/%s/%s/%s";
    private static final String DPS_PRIMARYTYPE_SCHEMA = "dps_primarytype";
    private static final String NODE_MODEL = "_NODE_MODEL";;

    @EJB
    private DpsHelper dpsBean;

    @Inject
    private ModelService service;

    private ModelMetaInformation metaInfoAccess;

    @PostConstruct
    public void initialize() {
        metaInfoAccess = service.getModelMetaInformation();
    }

    private Set<String> removeUnsupportedMoTypes(final Set<String> moTypes, final String nodeType, final String modelVersion) {
        LOGGER.info("The number of moTypes before model validation is {} and they are {} ", moTypes.size(), moTypes);
        final String modelUrn = String.format(FDN_NAME_URN_TEMPLATE, DPS_PRIMARYTYPE_SCHEMA, nodeType + NODE_MODEL, WILDCARD, modelVersion);
        LOGGER.info("Checking the supported models for the modelUrn [{}]", modelUrn);
        final Set<String> supportedMoTypes = new HashSet<>();
        try {
            final Collection<ModelInfo> modelInfos = metaInfoAccess.getModelsFromUrn(modelUrn);
            for (final ModelInfo mi : modelInfos) {
                if (moTypes.contains(mi.getName())) {
                    supportedMoTypes.add(mi.getName());
                }
            }
        } catch (final Exception e) {
            LOGGER.error("ERROR getting getModelsFromUrn for {} ", modelUrn, e);
        }
        LOGGER.info("The number of moTypes after model validation is [{}] and they are [{}] ", supportedMoTypes.size(), supportedMoTypes  );
        return supportedMoTypes;
    }

    /**
     * Returns a set of the Managed Objects (MO) Types supported in Model Service for a node
     *
     * @param nodeName
     *            the name of the node whose type and release will be used in the model service query
     * @param moTypes
     *            a set of existing MO Types of which may be supported or not supported MO Types
     * @return set of supported MO Types
     */
    public Set<String> getSupportedMOTypesFromModelService(final String nodeName, final Set<String> moTypes) {
        final String nodeType = (String) dpsBean.getAttribute(NETWORK_ELEMENT + EQUALS + nodeName, NE_TYPE);
        final String dpsReleaseVersion = (String) dpsBean.getAttribute(NETWORK_ELEMENT + EQUALS  + nodeName, RELEASE);
        final String modelReleaseVersion = translateDpsReleaseVersionToModelVersion(dpsReleaseVersion);
        LOGGER.info("Converted the dpsReleaseVersion from [{}] to [{}]", dpsReleaseVersion, modelReleaseVersion);
        final Set<String> supportedMoTypes =  removeUnsupportedMoTypes(moTypes, nodeType, modelReleaseVersion);
        LOGGER.info("The number of unique supported MO types defined on the node is [{}]", supportedMoTypes.size());
        return supportedMoTypes;
    }

    /*
     * This method translates the NetworkElement release attribute to a corresponding model service release version
     * e.g. B.1.234 to 2.1.234. This model version is derived by translating the the letter to a number using the following
     * scheme A -> 1 , B -> 2
     */
    private String translateDpsReleaseVersionToModelVersion(final String dpsReleaseVersion) {
        final String modelVersionSuffix = dpsReleaseVersion.substring(1);
        final char modelVersionMajorLetter = dpsReleaseVersion.substring(0,1).charAt(0);
        final int modelVersionMajorDigit = modelVersionMajorLetter - CHAR_A + 1;
        return modelVersionMajorDigit + modelVersionSuffix;

    }

}
