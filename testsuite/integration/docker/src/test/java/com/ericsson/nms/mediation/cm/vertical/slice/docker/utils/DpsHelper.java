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

import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.CHAR_ENCODING;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.COMMA;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EMPTY_STRING;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.EQUALS_ONE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.Http_PORT_80;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.PORT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.Constants.SPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CPP_CONNECTIVITY_INFORMATION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.CPP_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.GENERATION_COUNTER;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.IP_ADDRESS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT_SECURITY;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT_SECURITY_NAMESPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NETWORK_ELEMENT_SECURITY_VERSION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NE_NAMESSPACE;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NORMAL_USERNAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.NORMAL_USER_PASSWORD;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.OSS_PREFIX;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.ROOT_USERNAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.ROOT_USER_PASSWORD;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.SECURE_USERNAME;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.*;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.SECURITY_FUNCTION;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.TARGET_GROUPS;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.VERSION_1;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.MoConstants.VERSION_2;
import static com.ericsson.nms.mediation.cm.vertical.slice.docker.utils.AttributeTransformer.transform;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

/**
 * This is a Data Persistence Service helper class for any DPS related operations. Will need refactoring once adequate structure is in place
 *
 * @author ebrione
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class DpsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DpsHelper.class);

    @EJB(lookup = "java:/datalayer/DataPersistenceService")
    private DataPersistenceService dps;

    @Inject
    private CryptographyService cryptographyService;

    @Inject
    private NetsimHelper netsimHelper;

    public String create(final String parentFdn, final String type, final String name, final String namespace, final String version,
            final Map<String, Object> attributes) {
        return create(dps.getLiveBucket().findMoByFdn(parentFdn), type, name, namespace, version, attributes);
    }

    public String create(final String type, final String name, final String namespace, final String version, final Map<String, Object> attributes) {
        return create((ManagedObject) null, type, name, namespace, version, attributes);
    }

    private String create(final ManagedObject parent, final String type, final String name, final String namespace, final String version,
            final Map<String, Object> attributes) {
        return dps.getLiveBucket().getMibRootBuilder().addAttributes(transform(type, attributes)).namespace(namespace).version(version).parent(parent)
                .type(type).name(name).create().getFdn();
    }

    public void modify(final String fdn, final String attribute, final Object value) {
        final ManagedObject managedObject = dps.getLiveBucket().findMoByFdn(fdn);
        if (managedObject != null) {
            LOGGER.info("Modifyng MO, fdn: [{}], attribute: [{}], value: [{}]", fdn, attribute, value);
            managedObject.setAttribute(attribute, transform(managedObject.getType(), attribute, value));
        }
    }

    public Object getAttribute(final String fdn, final String attribute) {
        LOGGER.info("Getting the attribute [{}] for fdn [{}]", attribute, fdn);
        return dps.getLiveBucket().findMoByFdn(fdn).getAttribute(attribute);
    }

    public void remove(final String fdn) {
        final ManagedObject managedObject = dps.getLiveBucket().findMoByFdn(fdn);
        if (managedObject != null) {
            LOGGER.info(" Deleting MO: fdn [{}]", fdn);
            dps.getLiveBucket().deletePo(managedObject);
        }
    }

    public Map<String, Object> find(final String fdn) {
        final ManagedObject managedObject = dps.getLiveBucket().findMoByFdn(fdn);
        return managedObject == null ? null : managedObject.getAllAttributes();
    }

    public ManagedObject findMoByFdn(final String fdn) {
        return dps.getLiveBucket().findMoByFdn(fdn);
    }

    public void execute(final String fdn, final String action, final Map<String, Object> arguments) {
        final ManagedObject managedObject = dps.getLiveBucket().findMoByFdn(fdn);
        if (managedObject != null) {
            LOGGER.info("Performing action: [{}] on fdn[{}] with args [{}]", action, fdn, arguments);
            managedObject.performAction(action, arguments);
        }
    }

    /**
     * Returns a list of children fdns for a node defined in dps
     *
     * @param nodeName
     *            the name of the node in dps whose children will be listed
     * @return list of children fdns
     */
    public List<String> retrieveDpsTopologyTree(final String nodeName) {
        final String ossPrefix = (String) getAttribute(NETWORK_ELEMENT + EQUALS + nodeName, OSS_PREFIX);
        final List<String> dpsFdns = getTopologyTree(ossPrefix);
        final ListIterator<String> it = dpsFdns.listIterator();
        while (it.hasNext()) {
            it.set(it.next().replace(ossPrefix + COMMA, EMPTY_STRING));
        }
        LOGGER.info("The size of dps topology is [{}]", dpsFdns.size());
        return dpsFdns;
    }

    private List<String> getTopologyTree(final String fdn) {
        LOGGER.info("Getting the topology tree for [{}]", fdn);
        final List<String> emptyFdnList = new ArrayList<>();
        return traverseAllChildren(dps.getLiveBucket().findMoByFdn(fdn), emptyFdnList);
    }

    private List<String> traverseAllChildren(final ManagedObject parentObject, final List<String> fdns) {
        if (parentObject != null) {
            for (final ManagedObject childObject : parentObject.getChildren()) {
                fdns.add(childObject.getFdn());
                traverseAllChildren(childObject, fdns);
            }
        }
        return fdns;
    }

    public Map<String, Object> getNetworkElementSecurityMOAttributes() {
        final Map<String, Object> mandatoryNetworkElementSecurityAttributes = new HashMap<>();

        final List<String> targetGroups = new ArrayList<>();
        targetGroups.add("Group1");

        LOGGER.info("Password is now encrypted");

        mandatoryNetworkElementSecurityAttributes.put(ROOT_USERNAME, "rootUserName");
        mandatoryNetworkElementSecurityAttributes.put(ROOT_USER_PASSWORD, "rootUserPassword");
        mandatoryNetworkElementSecurityAttributes.put(SECURE_USERNAME, "netsim");
        final String encryptedPassword = encryptPassword(cryptographyService, "netsim");
        mandatoryNetworkElementSecurityAttributes.put(SECURE_USER_PASSWORD, encryptedPassword);
        mandatoryNetworkElementSecurityAttributes.put(NORMAL_USERNAME, "normalUserName");
        mandatoryNetworkElementSecurityAttributes.put(NORMAL_USER_PASSWORD, "normalUserPassword");

        mandatoryNetworkElementSecurityAttributes.put(TARGET_GROUPS, targetGroups);

        return mandatoryNetworkElementSecurityAttributes;
    }

    private String encryptPassword(final CryptographyService cryptographyService, final String password) {
        byte[] encryptedNormalUserPassword = null;
        try {
            encryptedNormalUserPassword = cryptographyService.encrypt(password.getBytes(CHAR_ENCODING));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String enCryptedPassword = encryptedNormalUserPassword == null ? "shroot" : encode(encryptedNormalUserPassword);

        return enCryptedPassword;
    }

    private String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public void createNetworkElement(final String name, final Map<String, Object> attributes) {
        LOGGER.info("Create MO with type: [{}], name: [{}], namespace: [{}], version: [{}], attributes: [{}]", NETWORK_ELEMENT, name, attributes);
        create(NETWORK_ELEMENT, name, NE_NAMESSPACE, VERSION_2, attributes);
    }

    public void createCppConnectivityInformation(final String name) throws UnknownHostException {
        LOGGER.info("Create CppConnectivityInformation MO for [{}]", name);
        final String parent = String.format("NetworkElement=%s", name);

        final Map<String, Object> attr = new HashMap<>();
        attr.put(PORT, Http_PORT_80);
        final String netsimIp = netsimHelper.getIpAddressForNode(name);
        LOGGER.info("Netsim ip = {} ", netsimIp);
        attr.put(IP_ADDRESS, netsimIp);

        create(parent, CPP_CONNECTIVITY_INFORMATION, name, CPP_NAMESPACE, VERSION_1, attr);
    }

    public void createNetworkElementSecurity(final String name) throws UnknownHostException {
        LOGGER.info("Create NetworkElementSecurity MO for [{}]", name);
        final String parent = NETWORK_ELEMENT + EQUALS + name + COMMA + SECURITY_FUNCTION + EQUALS_ONE;

        Map<String, Object> attr = new HashMap<>();
        attr = getNetworkElementSecurityMOAttributes();

        create(parent, NETWORK_ELEMENT_SECURITY, "1", NETWORK_ELEMENT_SECURITY_NAMESPACE, NETWORK_ELEMENT_SECURITY_VERSION, attr);
    }

    public long getGenerationCounterFromDps(final String nodeName) {
        final String generationCounterString =
                getAttribute(NETWORK_ELEMENT + EQUALS + nodeName + COMMA + CPP_CONNECTIVITY_INFORMATION + EQUALS_ONE, GENERATION_COUNTER).toString();
        final String[] arrayGenerationCounter = generationCounterString.split(SPACE);
        return Long.parseLong(arrayGenerationCounter[arrayGenerationCounter.length - 1]);
    }

    public boolean managedElementExists(final String neFdn) {
        final String ossPrefix = (String) getAttribute(neFdn, OSS_PREFIX);
        final ManagedObject meContextMo = findMoByFdn(ossPrefix);
        return meContextMo == null ? false : meContextMo.getChild(MANAGED_ELEMENT + EQUALS_ONE) != null;
    }
}
