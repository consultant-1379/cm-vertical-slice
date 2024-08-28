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

package com.ericsson.nms.mediation.cm.vertical.slice.operators;

import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CATEGORY_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CHAR_ENCODING_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CM_FUNCTION_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CM_NODE_HEARTBEAT_SUPERVISION_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CPP_CI_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CPP_NODE_PASSWORD_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.CPP_NODE_USERNAME_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ERBS_NETWORK_ELEMENT_TYPE_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.IP_ADDRESS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ME_CONTEXT_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.MODEL_IDENTITY_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NAME_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NETWORK_ELEMENT_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NETWORK_ELEMENT_SECURITY_ID_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NETWORK_ELEMENT_TYPE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NORMAL_USERNAME_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.NORMAL_USER_PASSWORD_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.OSS_MODEL_IDENTITY_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.OSS_PREFIX_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.PLATFORM_TYPE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.PORT_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ROOT_USERNAME_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.ROOT_USER_PASSWORD_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SECURE_USERNAME_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SECURE_USER_PASSWORD_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.SSH_PORT_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TARGET_GROUPS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TARGET_NAMESPACE_KEYS_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.TYPE_ATTR_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.VERSION_1_0_0_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoAttributeConstants.VERSION_4_1_1_ATTR_VALUE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CI_REF_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_FUNCTION_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_FUNCTION_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CM_NODE_HEARTBEAT_SUPERVISION_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CPP_CI_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CPP_CI_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.CPP_MED_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.DEFAULT_MO_NAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.DPS_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EAI_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.EAI_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.MEDIATION_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ME_CONTEXT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.ME_CONTEXT_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NETWORK_ELEMENT_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NETWORK_ELEMENT_MO_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NETWORK_ELEMENT_REF_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.NETWORK_ELEMENT_SECURITY_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.OSS_NE_CM_DEF_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.OSS_NE_DEF_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.OSS_NE_SEC_DEF_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.OSS_TOP_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.SECURITY_FUNCTION_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TARGET_MO_TYPE;
import static com.ericsson.nms.mediation.cm.vertical.slice.utility.constants.MoConstants.TARGET_MO_VERSION;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.common.TestNode;
import com.ericsson.nms.mediation.cm.vertical.slice.operators.common.DpsFacade;
import com.ericsson.nms.mediation.cm.vertical.slice.utility.FdnUtil;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

/**
 * Add Node DPS service.
 * <p>
 * Creates a set of basic MO/POs in DPS for the node to be sync-able.
 */
@ApplicationScoped
public class AddNodeDpsOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddNodeDpsOperator.class);

    @Inject
    private DpsFacade dpsFacade;

    @Inject
    private CryptographyService cryptographyService;

    public void addNodes(final List<TestNode> nodes) {
        LOGGER.info("--> Adding nodes to DPS ({})...", nodes);
        for (final TestNode node : nodes) {
            addNode(node);
        }
        LOGGER.info("--> ADDED nodes to DPS ({}).", nodes);
    }

    public void addNode(final TestNode node) {
        final String nodeName = node.getName();
        LOGGER.debug("Adding node: '{}'...", nodeName);

        try {
            final DataBucket liveBucket = dpsFacade.getLiveBucket();
            final String platformType = node.getPlatformType();
            final String ossModelIdentity = node.getOssModelIdentity();
            final String neType = node.getNeType() != null ? node.getNeType() : ERBS_NETWORK_ELEMENT_TYPE_ATTR_VALUE;

            final PersistenceObject targetPo = createTargetPo(liveBucket, node);
            final PersistenceObject entityAddressInfoPo = createEaiPo(liveBucket, platformType, neType);

            final ManagedObject networkElementMo = createNetworkElementMo(liveBucket, nodeName, platformType, ossModelIdentity, neType);
            networkElementMo.setTarget(targetPo);

            final ManagedObject meContextMo = createMeContextMo(liveBucket, nodeName);
            meContextMo.addAssociation(NETWORK_ELEMENT_REF_TYPE, networkElementMo);
            networkElementMo.setEntityAddressInfo(entityAddressInfoPo);

            final ManagedObject cppConnectivityInformationMo = createCppConnectivityInformationMo(liveBucket, networkElementMo, node.getIpAddress());

            createCmFunctionMo(liveBucket, networkElementMo);
            createCmNodeHeartbeatSupervision(liveBucket, networkElementMo);

            targetPo.addAssociation(CI_REF_TYPE, cppConnectivityInformationMo);
            LOGGER.trace("Created ciAssociation in Target PO.");
            entityAddressInfoPo.addAssociation(CI_REF_TYPE, cppConnectivityInformationMo);
            LOGGER.trace("Created ciAssociation in entityAddressInfo.");

            final Map<String, Object> securityAttributes = createCppSecurityFunctionAttributes();
            final ManagedObject securityMo = createSecurityMo(liveBucket, networkElementMo, securityAttributes);
            final Map<String, Object> neSecAtts =
                    createCppNetworkelementSecurityAttributes(CPP_NODE_USERNAME_ATTR_VALUE, CPP_NODE_PASSWORD_ATTR_VALUE);
            createNetworkElementSecurityMo(liveBucket, securityMo, neSecAtts);
            LOGGER.trace("Added SecurityFunction");

        } catch (final Exception exception) {
            LOGGER.error("Exception thrown when adding node to DPS!", exception);
            Assert.fail();
        }

        LOGGER.info("--> ADDED node: '{}'. <--", nodeName);
    }

    private PersistenceObject createTargetPo(final DataBucket dataBucket, final TestNode node) {
        LOGGER.trace("Creating '{}' PO...", TARGET_MO_TYPE);

        final Map<String, Object> moAttributes = new HashMap<>();
        moAttributes.put(CATEGORY_ATTR_NAME, TargetTypeInformation.CATEGORY_NODE);
        moAttributes.put(TYPE_ATTR_NAME, node.getNeType());
        moAttributes.put(NAME_ATTR_NAME, node.getName());
        if (node.getOssModelIdentity() != null) {
            moAttributes.put(MODEL_IDENTITY_ATTR_NAME, node.getOssModelIdentity());
        }

        final PersistenceObject targetPo = dataBucket.getPersistenceObjectBuilder()
                .type(TARGET_MO_TYPE)
                .namespace(DPS_NAMESPACE)
                .version(TARGET_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' PO.", TARGET_MO_TYPE);

        return targetPo;
    }

    private PersistenceObject createEaiPo(final DataBucket dataBucket, final String platformType, final String neType) {
        LOGGER.trace("Creating '{}' PO...", EAI_MO_TYPE);

        final Map<String, Object> moAttributes = new HashMap<>();
        final List<String> targetNamespaceKeys = new ArrayList<>();
        if (platformType != null) {
            targetNamespaceKeys.add(platformType);
        }
        targetNamespaceKeys.add(neType);
        moAttributes.put(TARGET_NAMESPACE_KEYS_ATTR_NAME, targetNamespaceKeys);

        final PersistenceObject entityAddressInfoPo = dataBucket.getPersistenceObjectBuilder()
                .type(EAI_MO_TYPE)
                .namespace(MEDIATION_NAMESPACE)
                .version(EAI_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' PO.", EAI_MO_TYPE);

        return entityAddressInfoPo;
    }

    private ManagedObject createMeContextMo(final DataBucket dataBucket, final String nodeName) {
        LOGGER.trace("Creating '{}' MO ('{}')...", ME_CONTEXT_MO_TYPE, nodeName);

        final Map<String, Object> moAttributes = new HashMap<>();
        moAttributes.put(ME_CONTEXT_ID_ATTR_NAME, nodeName);

        final ManagedObject meContextMo = dataBucket.getMibRootBuilder()
                .type(ME_CONTEXT_MO_TYPE)
                .name(nodeName)
                .namespace(OSS_TOP_NAMESPACE)
                .version(ME_CONTEXT_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' MO.", ME_CONTEXT_MO_TYPE);

        return meContextMo;
    }

    private ManagedObject createNetworkElementMo(final DataBucket dataBucket, final String nodeName, final String platformType,
            final String ossModelIdentity, final String neType) {
        LOGGER.trace("Creating '{}' MO ('{}')...", NETWORK_ELEMENT_MO_TYPE, nodeName);

        final Map<String, Object> moAttributes = new HashMap<>();
        moAttributes.put(NETWORK_ELEMENT_ID_ATTR_NAME, nodeName);
        moAttributes.put(NETWORK_ELEMENT_TYPE_ATTR_NAME, neType);
        if (platformType != null) {
            moAttributes.put(PLATFORM_TYPE_ATTR_NAME, platformType);
        }
        moAttributes.put(OSS_PREFIX_ATTR_NAME, FdnUtil.getMeContextFdn(nodeName));
        moAttributes.put(OSS_MODEL_IDENTITY_ATTR_NAME, ossModelIdentity);

        final ManagedObject networkElementMo = dataBucket.getMibRootBuilder()
                .type(NETWORK_ELEMENT_MO_TYPE)
                .name(nodeName)
                .namespace(OSS_NE_DEF_NAMESPACE)
                .version(NETWORK_ELEMENT_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' MO.", NETWORK_ELEMENT_MO_TYPE);

        return networkElementMo;
    }

    private ManagedObject createCppConnectivityInformationMo(final DataBucket dataBucket, final ManagedObject parentMo,
            final String netsimIpAddress) {
        LOGGER.trace("Creating '{}' MO (IP: '{}')...", CPP_CI_MO_TYPE, netsimIpAddress);

        final Map<String, Object> moAttributes = new HashMap<>();
        moAttributes.put(IP_ADDRESS_ATTR_NAME, netsimIpAddress);
        moAttributes.put(CPP_CI_ID_ATTR_NAME, DEFAULT_MO_NAME);
        moAttributes.put(PORT_ATTR_NAME, SSH_PORT_ATTR_VALUE);

        final ManagedObject cppConnectivityInformationMo = dataBucket.getMibRootBuilder()
                .parent(parentMo)
                .type(CPP_CI_MO_TYPE)
                .name(DEFAULT_MO_NAME)
                .namespace(CPP_MED_NAMESPACE)
                .version(CPP_CI_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' MO.", CPP_CI_MO_TYPE);

        return cppConnectivityInformationMo;
    }

    private ManagedObject createCmFunctionMo(final DataBucket dataBucket, final ManagedObject parentMo) {
        LOGGER.trace("Creating '{}' MO...", CM_FUNCTION_MO_TYPE);

        final ManagedObject cmFunctionMo = dataBucket.getMibRootBuilder()
                .parent(parentMo)
                .type(CM_FUNCTION_MO_TYPE)
                .name(DEFAULT_MO_NAME)
                .namespace(OSS_NE_CM_DEF_NAMESPACE)
                .version(CM_FUNCTION_MO_VERSION)
                .addAttribute(CM_FUNCTION_ID_ATTR_NAME, DEFAULT_MO_NAME)
                .create();
        LOGGER.trace("Created '{}' MO.", CM_FUNCTION_MO_TYPE);

        return cmFunctionMo;
    }

    private ManagedObject createCmNodeHeartbeatSupervision(final DataBucket dataBucket, final ManagedObject parentMo) {
        LOGGER.trace("Creating '{}' MO...", CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE);

        final Map<String, Object> moAttributes = new HashMap<>();
        moAttributes.put(CM_NODE_HEARTBEAT_SUPERVISION_ID_ATTR_NAME, DEFAULT_MO_NAME);

        final ManagedObject cmNodeHeartbeatSupervisionMo = dataBucket.getMibRootBuilder()
                .parent(parentMo)
                .type(CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE)
                .name(DEFAULT_MO_NAME)
                .namespace(OSS_NE_CM_DEF_NAMESPACE)
                .version(CM_NODE_HEARTBEAT_SUPERVISION_MO_VERSION)
                .addAttributes(moAttributes)
                .create();
        LOGGER.trace("Created '{}' MO.", CM_NODE_HEARTBEAT_SUPERVISION_MO_TYPE);

        return cmNodeHeartbeatSupervisionMo;
    }

    // [VULCANIANS] TODO cleanup the following section...
    private Map<String, Object> createCppSecurityFunctionAttributes() {
        final Map<String, Object> secFtcAtts = new HashMap<>();
        secFtcAtts.put("securityFunctionId", "SecurityFunction");
        return secFtcAtts;
    }

    private Map<String, Object> createCppNetworkelementSecurityAttributes(final String username, final String password) {
        final Map<String, Object> cppCiAtts = new HashMap<>();

        final List<String> targetGroups = new ArrayList<>();
        targetGroups.add("Group1");

        String encryptedPassword = password;
        try {
            encryptedPassword = encryptPassword(encryptedPassword);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        cppCiAtts.put(ROOT_USERNAME_ATTR_NAME, "rootUserName");
        cppCiAtts.put(ROOT_USER_PASSWORD_ATTR_NAME, "rootUserPassword");
        cppCiAtts.put(SECURE_USERNAME_ATTR_NAME, username);
        cppCiAtts.put(SECURE_USER_PASSWORD_ATTR_NAME, encryptedPassword);
        cppCiAtts.put(NORMAL_USERNAME_ATTR_NAME, "normalUserName");
        cppCiAtts.put(NORMAL_USER_PASSWORD_ATTR_NAME, "normalUserPassword");
        cppCiAtts.put(NETWORK_ELEMENT_SECURITY_ID_ATTR_NAME, "NetworkElementSecurityId");
        cppCiAtts.put(TARGET_GROUPS_ATTR_NAME, targetGroups);

        return cppCiAtts;
    }

    private String encryptPassword(final String password) throws UnsupportedEncodingException {
        final byte[] encryptedNormalUserPassword = cryptographyService.encrypt(password.getBytes(CHAR_ENCODING_ATTR_VALUE));
        final String enCryptedPassword = encode(encryptedNormalUserPassword);

        return enCryptedPassword;
    }

    private String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private ManagedObject createSecurityMo(final DataBucket dataBucket, final ManagedObject parentMo, final Map<String, Object> attributes) {
        final ManagedObject managedObj = dataBucket.getMibRootBuilder()
                .parent(parentMo)
                .type(SECURITY_FUNCTION_MO_TYPE)
                .name(DEFAULT_MO_NAME)
                .namespace(OSS_NE_SEC_DEF_NAMESPACE)
                .version(VERSION_1_0_0_ATTR_VALUE)
                .addAttributes(attributes)
                .create();
        LOGGER.info("Created {} MO with FDN {}", SECURITY_FUNCTION_MO_TYPE, managedObj.getFdn());
        return managedObj;
    }

    public void createNetworkElementSecurityMo(final DataBucket dataBucket, final ManagedObject parentMo, final Map<String, Object> attributes) {
        final ManagedObject managedObj = dataBucket.getMibRootBuilder()
                .parent(parentMo)
                .type(NETWORK_ELEMENT_SECURITY_MO_TYPE)
                .name(DEFAULT_MO_NAME)
                .namespace(OSS_NE_SEC_DEF_NAMESPACE)
                .version(VERSION_4_1_1_ATTR_VALUE)
                .addAttributes(attributes)
                .create();
        LOGGER.info("Created {} MO with FDN {}", NETWORK_ELEMENT_SECURITY_MO_TYPE, managedObj.getFdn());
    }

}
