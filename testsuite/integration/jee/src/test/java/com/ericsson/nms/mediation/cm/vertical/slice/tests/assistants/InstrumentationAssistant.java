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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.assistants;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.util.JmxUtil;

/**
 * Test helper class.<br>
 * Accommodates instrumentation-related procedures.
 */
public class InstrumentationAssistant {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationAssistant.class);

    /**
     * Gets the value of an instrumented attribute.<br>
     * Locates the MBean server, looks up the relevant MBean and the attribute provided.
     * <p>
     * <b>Note:</b> The returned value is always assumed to be convertible to Number!
     *
     * @param mBeanName
     *            name of the MBean from which the attribute is going to be read
     * @param attributeName
     *            name of the attribute for which the value is to be retrieved
     * @return long value of the attribute required
     * @throws IntrospectionException
     */
    public long getInstrumentationAttributeValue(final String mBeanName, final String attributeName) {
        final MBeanServer mBeanServer = JmxUtil.locateMBeanServer();
        LOGGER.trace("MBeanServer located: '{}'.", mBeanServer);

        Number attributeValue = 0;
        try {
            final ObjectName mBeanObjectName = new ObjectName(mBeanName);
            LOGGER.debug("Looking up value of '{}' metric in the following MBean: {}...", attributeName, mBeanServer.getMBeanInfo(mBeanObjectName));

            final Object rawAttributeValue = mBeanServer.getAttribute(mBeanObjectName, attributeName);
            LOGGER.warn("Raw value of '{}' metric is: '{}'.", attributeName, rawAttributeValue.toString());
            attributeValue = (Number) rawAttributeValue;
            LOGGER.debug("Value of '{}' metric is: [{}].", attributeName, attributeValue);

        } catch (final InstanceNotFoundException exception) {
            LOGGER.warn("Instumentation metric: '{}' not found.", attributeName);
        } catch (MalformedObjectNameException | AttributeNotFoundException | MBeanException | ReflectionException | IntrospectionException exception) {
            LOGGER.error("Error while obtaining instrumentation metric: '{}' from DPS:", attributeName, exception);
        }

        return attributeValue.longValue();
    }

}
