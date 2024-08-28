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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class AttributeTransformer {

    @SuppressWarnings("rawtypes")
    private static final HashMap<String, Converter> CONVERTERS = new HashMap<>();

    public static void register(final String type, final String attribute, final Converter<?, ?> converter) {
        CONVERTERS.put(key(type, attribute), converter);
    }

    public static void registerIntegerConverter(final String type, final String attribute) {
        CONVERTERS.put(key(type, attribute), Ints.stringConverter());
    }

    public static void registerBooleanConverter(final String type, final String attribute) {
        CONVERTERS.put(key(type, attribute), BooleanConverter.INSTANCE);
    }

    public static class BooleanConverter extends Converter<String, Boolean> {

        private static final BooleanConverter INSTANCE = new BooleanConverter();

        @Override
        protected Boolean doForward(final String s) {
            return Boolean.valueOf(s);
        }

        @Override
        protected String doBackward(final Boolean aBoolean) {
            return aBoolean.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public static Object transform(final String type, final String attribute, final Object value) {
        @SuppressWarnings("rawtypes")
        final Converter converter = CONVERTERS.get(key(type, attribute));
        return converter == null ? value : converter.convert(value);
    }

    public static Map<String, Object> transform(final String type, final Map<String, Object> attributes) {
        return Maps.transformEntries(attributes, new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(final String attribute, final Object value) {
                return transform(type, attribute, value);
            }
        });
    }

    private static String key(final String type, final String attribute) {
        return Joiner.on(".").join(type, attribute);
    }
}
