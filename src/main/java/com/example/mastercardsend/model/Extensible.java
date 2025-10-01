package com.example.mastercardsend.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public abstract class Extensible {

    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
}

