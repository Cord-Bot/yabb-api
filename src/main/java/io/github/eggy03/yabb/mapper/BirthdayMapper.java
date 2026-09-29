package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.entity.Birthday;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface BirthdayMapper {

    Birthday toEntity(BirthdayDTO dto);
    List<Birthday> toEntityList(List<BirthdayDTO> dtoList);

    BirthdayDTO toDTO(@NonNull Birthday entity);
    List<BirthdayDTO> toDTOList(@NonNull List<Birthday> entityList);
}
