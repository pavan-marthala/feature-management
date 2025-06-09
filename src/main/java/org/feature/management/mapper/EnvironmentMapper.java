package org.feature.management.mapper;

import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.models.Environment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {

    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    @Mapping(target = "etag", source = "etag", qualifiedByName = "convertEtagToInteger")
    @Mapping(target = "owners", source = "owners")
    Environment toDto(EnvironmentEntity entity);

    @org.mapstruct.Named("convertEtagToInteger")
    default Integer convertEtagToInteger(Long etag) {
        return etag != null ? etag.intValue() : null;
    }
}
