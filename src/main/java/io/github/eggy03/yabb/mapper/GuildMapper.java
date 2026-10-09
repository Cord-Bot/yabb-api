package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface GuildMapper {
    
    Guild toEntity(GuildDTO dto);
    List<Guild> toEntityList(List<GuildDTO> dtoList);
    
    GuildDTO toDTO(Guild entity);
    List<GuildDTO> toDTOList(List<Guild> entityList);
}
