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
package com.ericsson.nms.mediation.cm.vertical.slice.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.nms.mediation.cm.vertical.slice.operators.common.DpsFacade;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;

/**
 * Query DPS service.
 * <p>
 * Provides a way to executes DPS operations related to querying and projecting, for batch reads from the database.
 */
@ApplicationScoped
public class QueryDpsOperator {

    @Inject
    private DpsFacade dpsFacade;

    /**
     * Executes a DPS query on a particular attribute and returns the values for this attribute mapped to respective FDNs.<br>
     * All MOs of the passed type are being queried, and the resulting size of the map should correspond to the number of these MO instances in DPS.
     *
     * @param moNamespace
     *            model namespace of the MO type being queried
     * @param moType
     *            type of MO instances being queried
     * @param attrName
     *            name of the attribute for which the values are being read from DPS
     * @return map of FDNs and the corresponding queried attribute values
     */
    public <T> Map<String, T> getAttrValues(final String moNamespace, final String moType, final String attrName) {
        final List<Object[]> queryResult = getQueryResult(moNamespace, moType, attrName);
        return convertToResultMap(queryResult);
    }

    private List<Object[]> getQueryResult(final String moNamespace, final String moType, final String attrName) {
        final Query<TypeRestrictionBuilder> typeQuery = createTypeQuery(moNamespace, moType);
        final Projection fdnProjection = ProjectionBuilder.field(ObjectField.MO_FDN);
        final Projection attrProjection = ProjectionBuilder.attribute(attrName);

        return dpsFacade.getLiveBucket().getQueryExecutor().executeProjection(typeQuery, fdnProjection, attrProjection);
    }

    private Query<TypeRestrictionBuilder> createTypeQuery(final String moNamespace, final String moType) {
        return dpsFacade.getQueryBuilder().createTypeQuery(moNamespace, moType);
    }

    private <T> Map<String, T> convertToResultMap(final List<Object[]> queryResult) {
        final Map<String, T> resultMap = new HashMap<>();
        for (final Object[] projectedMoAttrs : queryResult) {
            final String fdn = (String) projectedMoAttrs[0];
            @SuppressWarnings("unchecked")
            final T attrValue = (T) projectedMoAttrs[1];
            resultMap.put(fdn, attrValue);
        }
        return resultMap;
    }

}
