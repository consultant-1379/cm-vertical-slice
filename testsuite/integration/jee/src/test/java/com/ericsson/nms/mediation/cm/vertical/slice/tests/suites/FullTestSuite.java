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

package com.ericsson.nms.mediation.cm.vertical.slice.tests.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ericsson.nms.mediation.cm.vertical.slice.tests.*;

@RunWith(Suite.class)
@SuiteClasses({ DeltaSyncIT.class, FullSyncIT.class, InstrumentationIT.class, SupervisionIT.class,
    NotificationIT.class, TreatAsIT.class, LargeRncSyncIT.class })
public class FullTestSuite {

    public FullTestSuite() {}

}
