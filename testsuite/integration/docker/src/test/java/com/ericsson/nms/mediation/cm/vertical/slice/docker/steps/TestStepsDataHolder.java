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

package com.ericsson.nms.mediation.cm.vertical.slice.docker.steps;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * This is a data holder class. This allows data to be passed between test steps.
 * Will need refactoring once adequate structure is in place
 * @author ebrione
 */
@Startup
@Singleton
public class TestStepsDataHolder {

    List<String> dpsFdnList = new ArrayList<>();
    List<String> netsimFdnList = new ArrayList<>();

    public void setDpsFdnList(final List<String> dpsFdnList) {
        this.dpsFdnList = dpsFdnList;
    }

    public void setNetsimFdnList(final List<String> netsimFdnList) {
        this.netsimFdnList = netsimFdnList;
    }

    public List<String> getNetsimFdnList() {
        return netsimFdnList;
    }

    public List<String> getDpsFdnList() {
        return dpsFdnList;
    }
}
