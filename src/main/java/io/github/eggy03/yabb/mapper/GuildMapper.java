package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.entity.Guild;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface GuildMapper {
    
    Guild toEntity(@NonNull GuildDTO dto);
    List<Guild> toEntityList(@NonNull List<GuildDTO> dtoList);
    
    GuildDTO toDTO(@NonNull Guild entity);
    List<GuildDTO> toDTOList(@NonNull List<Guild> entityList);
}
