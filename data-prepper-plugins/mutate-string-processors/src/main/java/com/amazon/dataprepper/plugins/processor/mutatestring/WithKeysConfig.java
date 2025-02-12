/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.dataprepper.plugins.processor.mutatestring;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class WithKeysConfig implements StringProcessorConfig<String> {

    @NotNull
    @NotEmpty
    @JsonProperty("with_keys")
    private List<String> withKeys;

    @Override
    public List<String> getIterativeConfig() {
        return withKeys;
    }

    public List<String> getWithKeys() {
        return withKeys;
    }
}
