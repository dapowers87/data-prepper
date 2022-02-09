/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.dataprepper.plugins.processor.mutateevent;

import com.amazon.dataprepper.metrics.PluginMetrics;
import com.amazon.dataprepper.model.annotations.DataPrepperPlugin;
import com.amazon.dataprepper.model.annotations.DataPrepperPluginConstructor;
import com.amazon.dataprepper.model.event.Event;
import com.amazon.dataprepper.model.processor.AbstractProcessor;
import com.amazon.dataprepper.model.processor.Processor;
import com.amazon.dataprepper.model.record.Record;

import java.util.Collection;

@DataPrepperPlugin(name = "add_entry", pluginType = Processor.class, pluginConfigurationType = AddEntryProcessorConfig.class)
public class AddEntryProcessor extends AbstractProcessor<Record<Event>, Record<Event>> {
    private final String key;
    private final Object value;
    private final boolean overwriteIfKeyExists;

    @DataPrepperPluginConstructor
    public AddEntryProcessor(final PluginMetrics pluginMetrics, final AddEntryProcessorConfig config) {
        super(pluginMetrics);
        this.key = config.getKey();
        this.value = config.getValue();
        this.overwriteIfKeyExists = config.getOverwriteIfKeyExists();
    }

    @Override
    public Collection<Record<Event>> doExecute(final Collection<Record<Event>> records) {
        for(final Record<Event> record : records) {
            final Event recordEvent = record.getData();

            if(!recordEvent.containsKey(key) || overwriteIfKeyExists) {
                recordEvent.put(key, value);
            }
        }

        return records;
    }

    @Override
    public void prepareForShutdown() {
    }

    @Override
    public boolean isReadyForShutdown() {
        return true;
    }

    @Override
    public void shutdown() {
    }
}
