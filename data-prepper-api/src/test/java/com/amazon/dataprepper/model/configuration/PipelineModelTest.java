/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.dataprepper.model.configuration;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PipelineModelTest {

    public static Random random = new Random();
    public static final Integer TEST_WORKERS = random.nextInt(30);
    public static final Integer TEST_READ_BATCH_DELAY = random.nextInt(40);
    public static PluginModel TEST_VALID_SOURCE_PLUGIN_MODEL = new PluginModel("source-plugin", validPluginSettings());
    public static PluginModel TEST_VALID_PREPPERS_PLUGIN_MODEL = new PluginModel("prepper", validPluginSettings());
    public static PluginModel TEST_VALID_SINKS_PLUGIN_MODEL = new PluginModel("sink", validPluginSettings());

    @Test
    public void testPipelineModelCreation() {
        final PipelineModel pipelineModel = new PipelineModel(
                validSourcePluginModel(),
                validPreppersPluginModel(),
                validSinksPluginModel(),
                TEST_WORKERS,
                TEST_READ_BATCH_DELAY
        );
        final PluginModel originalSource = pipelineModel.getSource();
        final List<PluginModel> originalPreppers = pipelineModel.getProcessors();
        final List<PluginModel> originalSinks = pipelineModel.getSinks();

        assertThat(originalSource, notNullValue());
        assertThat(originalPreppers, notNullValue());
        assertThat(originalSinks, notNullValue());
        assertThat(originalSource.getPluginName(), is(equalTo(TEST_VALID_SOURCE_PLUGIN_MODEL.getPluginName())));
        assertThat(originalSource.getPluginSettings(), is(equalTo(TEST_VALID_SOURCE_PLUGIN_MODEL.getPluginSettings())));
        assertThat(originalPreppers.get(0).getPluginName(), is(equalTo(TEST_VALID_PREPPERS_PLUGIN_MODEL.getPluginName())));
        assertThat(originalPreppers.get(0).getPluginSettings(), is(equalTo(TEST_VALID_PREPPERS_PLUGIN_MODEL.getPluginSettings())));
        assertThat(originalSinks.get(0).getPluginName(), is(equalTo(TEST_VALID_SINKS_PLUGIN_MODEL.getPluginName())));
        assertThat(originalSinks.get(0).getPluginSettings(), is(equalTo(TEST_VALID_SINKS_PLUGIN_MODEL.getPluginSettings())));
        assertThat(pipelineModel.getWorkers(), is(TEST_WORKERS));
        assertThat(pipelineModel.getReadBatchDelay(), is(TEST_READ_BATCH_DELAY));
    }

    public static Map<String, Object> validPluginSettings() {
        final Map<String, Object> settings = new HashMap<>();
        settings.put("property", "value");
        return settings;
    }

    public static PluginModel validSourcePluginModel() {
        return new PluginModel("source-plugin", validPluginSettings());
    }

    public static List<PluginModel> validPreppersPluginModel() {
        return Collections.singletonList(new PluginModel("prepper", validPluginSettings()));
    }

    public static List<PluginModel> validSinksPluginModel() {
        return Collections.singletonList(new PluginModel("sink", validPluginSettings()));
    }

    @Test
    public void testPipelineModelWithPrepperAndProcessorConfigThrowsException() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PipelineModel(
                validSourcePluginModel(),
                validPreppersPluginModel(),
                validPreppersPluginModel(),
                validSinksPluginModel(),
                TEST_WORKERS,
                TEST_READ_BATCH_DELAY
        ));

        final String expected = "Pipeline model cannot specify a prepper and processor configuration. It is " +
                "recommended to move prepper configurations to the processor section to maintain compatibility with " +
                "DataPrepper version 1.2 and above.";

        assertTrue(exception.getMessage().contains(expected));
    }

    @Test
    public void testPipelineModelWithValidPrepperConfig() {
        List<PluginModel> expectedPreppersPluginModel = validPreppersPluginModel();
        PipelineModel pipelineModel = new PipelineModel(
                validSourcePluginModel(),
                expectedPreppersPluginModel,
                null,
                validSinksPluginModel(),
                TEST_WORKERS,
                TEST_READ_BATCH_DELAY
        );

        assertEquals(expectedPreppersPluginModel, pipelineModel.getPreppers());
        assertEquals(expectedPreppersPluginModel, pipelineModel.getProcessors());
    }

    @Test
    public void testPipelineModelWithValidProcessorConfig() {
        List<PluginModel> expectedPreppersPluginModel = validPreppersPluginModel();
        PipelineModel pipelineModel = new PipelineModel(
                validSourcePluginModel(),
                null,
                expectedPreppersPluginModel,
                validSinksPluginModel(),
                TEST_WORKERS,
                TEST_READ_BATCH_DELAY
        );

        assertEquals(expectedPreppersPluginModel, pipelineModel.getPreppers());
        assertEquals(expectedPreppersPluginModel, pipelineModel.getProcessors());
    }
}
