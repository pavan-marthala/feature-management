package org.feature.management.mapper;

import org.feature.management.entity.FeatureEntity;
import org.feature.management.models.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeatureMapper {

    FeatureMapper INSTANCE = Mappers.getMapper(FeatureMapper.class);

    @Mapping(target = "configuration", source = "configuration")
    FeatureEntity toEntity(Feature feature);

    @Mapping(target = "_configuration", source = "configuration")
    Feature toModel(FeatureEntity entity);


}
