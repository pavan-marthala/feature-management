package org.feature.management.config.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Set;

@WritingConverter
@Slf4j
@RequiredArgsConstructor
public class SetToJsonWritingConverter implements Converter<Set<String>, Json> {

    private final ObjectMapper objectMapper;

    @Override
    public Json convert(Set<String> source) {
        try {
            return Json.of(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            log.error("Error converting set to JSON", e);
            throw new RuntimeException("Error converting set to JSON", e);
        }
    }
}
