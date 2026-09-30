package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import io.github.eggy03.yabb.entity.Birthday;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface BirthdayRequestMapper {

    Birthday toEntity(BirthdayRequestDTO dto);
    List<Birthday> toEntityList(List<BirthdayRequestDTO> dtoList);

    BirthdayRequestDTO toDTO(@NonNull Birthday entity);
    List<BirthdayRequestDTO> toDTOList(@NonNull List<Birthday> entityList);
}
