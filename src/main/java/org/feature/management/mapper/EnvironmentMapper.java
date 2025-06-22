package org.feature.management.mapper;

import org.feature.management.entity.EnvironmentEntity;
import org.feature.management.models.Environment;
import org.feature.management.models.EnvironmentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {

    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    Environment toModel(EnvironmentEntity entity);

    EnvironmentEntity toEntity(EnvironmentRequest model);
}
