package org.feature.management.config.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.feature.management.models.BooleanFeatureStrategy;
import org.feature.management.models.FeatureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConverterTests {

    private ObjectMapper objectMapper;
    private SetToJsonWritingConverter setToJsonWritingConverter;
    private SetToJsonReadingConverter setToJsonReadingConverter;
    private FeatureConfigurationToJsonWritingConverter featureConfigWritingConverter;
    private FeatureConfigurationToJsonReadingConverter featureConfigReadingConverter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        setToJsonWritingConverter = new SetToJsonWritingConverter(objectMapper);
        setToJsonReadingConverter = new SetToJsonReadingConverter(objectMapper);
        featureConfigWritingConverter = new FeatureConfigurationToJsonWritingConverter(objectMapper);
        featureConfigReadingConverter = new FeatureConfigurationToJsonReadingConverter(objectMapper);
    }

    @Test
    void setToJsonWritingConverter_Success() {
        Set<String> source = Set.of("owner1", "owner2");
        Json result = setToJsonWritingConverter.convert(source);
        assertThat(result).isNotNull();
        assertThat(result.asString()).contains("owner1", "owner2");
    }

    @Test
    void setToJsonWritingConverter_Error() throws JsonProcessingException {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        SetToJsonWritingConverter converter = new SetToJsonWritingConverter(mockMapper);
        when(mockMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error") {
        });

        assertThatThrownBy(() -> converter.convert(Set.of("test")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error converting set to JSON");
    }

    @Test
    void setToJsonReadingConverter_Success() {
        Json source = Json.of("[\"owner1\", \"owner2\"]");
        Set<String> result = setToJsonReadingConverter.convert(source);
        assertThat(result).containsExactlyInAnyOrder("owner1", "owner2");
    }

    @Test
    void setToJsonReadingConverter_Error() {
        Json source = Json.of("invalid-json");
        assertThatThrownBy(() -> setToJsonReadingConverter.convert(source))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error reading set from JSON");
    }

    @Test
    void featureConfigWritingConverter_Success() {
        FeatureConfiguration source = new BooleanFeatureStrategy();
        Json result = featureConfigWritingConverter.convert(source);
        assertThat(result).isNotNull();
    }

    @Test
    void featureConfigWritingConverter_Error() throws JsonProcessingException {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        FeatureConfigurationToJsonWritingConverter converter = new FeatureConfigurationToJsonWritingConverter(
                mockMapper);
        when(mockMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error") {
        });

        assertThatThrownBy(() -> converter.convert(new BooleanFeatureStrategy()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error converting FeatureConfiguration to JSON");
    }

    @Test
    void featureConfigReadingConverter_Success() {
        Json source = Json.of("{\"strategy\":\"BooleanFeatureStrategy\"}");
        FeatureConfiguration result = featureConfigReadingConverter.convert(source);
        assertThat(result).isInstanceOf(BooleanFeatureStrategy.class);
    }

    @Test
    void featureConfigReadingConverter_Error() {
        Json source = Json.of("invalid-json");
        assertThatThrownBy(() -> featureConfigReadingConverter.convert(source))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error reading FeatureConfiguration from JSON");
    }
}
