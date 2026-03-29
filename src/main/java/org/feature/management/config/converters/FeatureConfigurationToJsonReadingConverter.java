package org.feature.management.config.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.models.FeatureConfiguration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
@Slf4j
@RequiredArgsConstructor
public class FeatureConfigurationToJsonReadingConverter implements Converter<Json, FeatureConfiguration> {

    private final ObjectMapper objectMapper;

    @Override
    public FeatureConfiguration convert(Json source) {
        try {
            return objectMapper.readValue(source.asString(), FeatureConfiguration.class);
        } catch (JsonProcessingException e) {
            log.error("Error reading FeatureConfiguration from JSON", e);
            throw new RuntimeException("Error reading FeatureConfiguration from JSON", e);
        }
    }
}
