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

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import com.ericsson.oss.mediation.cm.router.policy.StickyDistributionResult;

@ApplicationScoped
public class CmRouterPolicyHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmRouterPolicyHelper.class);

    /**
     * Cache holding the distribution of nodes per CM Mediation Service.
     */
    @Inject
    @NamedCache("CmRouterPolicyDistributionCacheV2")
    private Cache<String, StickyDistributionResult> distributionCache;

    public String getSupervisingMediationInstance(final String nodeName) {
        for (final Entry<String, StickyDistributionResult> entry : distributionCache) {
            LOGGER.debug("cache key:{} value:{}", entry.getKey(), entry.getValue());
            if (entry.getKey().contains(nodeName)) {
                return entry.getValue().getStickyMSinstance();
            }
        }
        return null;
    }

}
