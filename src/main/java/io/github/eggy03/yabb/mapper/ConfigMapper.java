package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.ConfigDTO;
import io.github.eggy03.yabb.entity.Config;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ConfigMapper {
    
    Config toEntity(ConfigDTO dto);
    List<Config> toEntityList(List<ConfigDTO> dtoList);
    
    ConfigDTO toDTO(Config entity);
    List<ConfigDTO> toDTOList(List<Config> entityList);
}
