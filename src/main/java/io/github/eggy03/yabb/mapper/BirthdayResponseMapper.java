package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayResponseDTO;
import io.github.eggy03.yabb.entity.Birthday;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface BirthdayResponseMapper {

    Birthday toEntity(BirthdayResponseDTO dto);
    List<Birthday> toEntityList(List<BirthdayResponseDTO> dtoList);

    BirthdayResponseDTO toDTO(@NonNull Birthday entity);
    List<BirthdayResponseDTO> toDTOList(@NonNull List<Birthday> entityList);

}
