package org.feature.management.config.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Set;

@ReadingConverter
@Slf4j
@RequiredArgsConstructor
public class SetToJsonReadingConverter implements Converter<Json, Set<String>> {

    private final ObjectMapper objectMapper;

    @Override
    public Set<String> convert(Json source) {
        try {
            return objectMapper.readValue(source.asString(), new TypeReference<Set<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error reading set from JSON", e);
            throw new RuntimeException("Error reading set from JSON", e);
        }
    }
}
