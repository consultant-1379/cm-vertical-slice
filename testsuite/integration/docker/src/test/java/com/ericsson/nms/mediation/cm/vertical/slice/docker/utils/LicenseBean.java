/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.cm.vertical.slice.docker.utils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.lcm.api.LicenseControlMonitorService;

@Startup
@Singleton
public class LicenseBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseBean.class);

    @EServiceRef
    private LicenseControlMonitorService licenseControlMonitorService;

    private static final String LICENSE_NAME = "FAT1023070";

    /*
     * Get from Team Voyager's page "Adding Network Capacity License Guidelines":
     * https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/TV/Adding+Network+Capacity+License+Guidelines
     */
    private static final String LICENSE =
            "14 FAT1023070 Ni LONG NORMAL NETWORK EXCL 1000000 INFINITE_KEYS 8 DEC 2020 1 JUN 2021 NO_SHR SLM_CODE 1 NON_COMMUTER NO_GRACE NO_OVERDRAFT DEMO NON_REDUNDANT Ni NO_HLD 20 Radio_Network_Base_Package_numberOf_5MHzSC twZ7QDTmm5KbzZfWrsK8frvRBWP6voZDkPqNmW1vlSLO:XYT3OGN4z2vboTBJUkq3,yUYNPE7IqkUMlj09DEpRLiXHKbjAIW2EZ1I1:BIdTB39639G7:,DVspV3R,UgIeUVs";

    @PostConstruct
    public void addLicense() {
        try {
            LOGGER.info("In the try block on the postconstruct");
            if (licenseControlMonitorService.getLicense(LICENSE_NAME) == null) {
                licenseControlMonitorService.addLicense(LICENSE);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
