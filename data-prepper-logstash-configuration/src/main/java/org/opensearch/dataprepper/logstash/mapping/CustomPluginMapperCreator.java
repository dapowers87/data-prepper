package org.opensearch.dataprepper.logstash.mapping;

import org.opensearch.dataprepper.logstash.exception.LogstashMappingException;

import java.lang.reflect.Constructor;

public class CustomPluginMapperCreator {
    CustomPluginMapper createMapperClass(final String attributesMapperClassName) {
        final Class<?> attributesMapperClass;
        try {
            attributesMapperClass = Class.forName(attributesMapperClassName);
        } catch (final ClassNotFoundException ex) {
            throw new LogstashMappingException("Unable to find Mapper class with name of " + attributesMapperClassName, ex);
        }

        if(!CustomPluginMapper.class.isAssignableFrom(attributesMapperClass)) {
            throw new LogstashMappingException("The provided mapping class does not implement " + CustomPluginMapper.class);
        }

        try {
            final Constructor<?> defaultConstructor = attributesMapperClass.getDeclaredConstructor();
            final Object instance = defaultConstructor.newInstance();
            return  (CustomPluginMapper) instance;
        } catch (final Exception ex) {
            throw new LogstashMappingException("Unable to create Mapper class with name of " + attributesMapperClassName, ex);
        }
    }
}
