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
package com.ericsson.nms.mediation.cm.vertical.slice.utility;

import static com.ericsson.oss.itpf.datalayer.dps.notification.DpsNotificationConfiguration.DPS_EVENT_NOTIFICATION_CHANNEL_URI;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Consumes;

@ApplicationScoped
public class DpsNotificationListener {

    private Set<AttributeChangeData> changedAttributes;
    private boolean isListening;

    @PostConstruct
    private void init() {
        changedAttributes = new HashSet<>();
        stopListening();
    }

    public void processDpsNotification(
            @Observes @Consumes(endpoint = DPS_EVENT_NOTIFICATION_CHANNEL_URI) final DpsAttributeChangedEvent dpsAttributeChangeEvent) {
        if (isListening) {
            changedAttributes.addAll(dpsAttributeChangeEvent.getChangedAttributes());
        }
    }

    public void startListening() {
        isListening = true;
    }

    public void stopListening() {
        isListening = false;
    }

    public boolean isAttributeModifiedWithValue(final String attributeName, final Object attributeValue) {
        for (final AttributeChangeData changedAttribute : changedAttributes) {
            if (isAttributeChangeDataExisting(changedAttribute, attributeName, attributeValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAttributeChangeDataExisting(final AttributeChangeData changedAttribute, final String attributeName, final Object attributeValue) {
        final String changedAttributeName = changedAttribute.getName();
        final Object changedAttributeValue = changedAttribute.getNewValue();

        return changedAttributeName.equals(attributeName) && changedAttributeValue.equals(attributeValue);
    }

}
