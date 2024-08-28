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
package com.ericsson.nms.mediation.cm.vertical.slice.operators.common;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;

/**
 * General DPS facade service.
 * <p>
 * Provides common methods called directly on DPS.
 */
@ApplicationScoped
public class DpsFacade {

    @EJB(lookup = DataPersistenceService.JNDI_LOOKUP_NAME)
    private DataPersistenceService dps;

    public DataBucket getLiveBucket() {
        return dps.getDataBucket(dps.getLiveBucket().getName(), BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    public QueryBuilder getQueryBuilder() {
        return dps.getQueryBuilder();
    }

}
