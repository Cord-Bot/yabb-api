package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayScheduleDTO;
import io.github.eggy03.yabb.entity.BirthdaySchedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface BirthdayScheduleMapper {

    BirthdaySchedule toEntity(BirthdayScheduleDTO dto);
    List<BirthdaySchedule> toEntityList(List<BirthdayScheduleDTO> dtoList);

    BirthdayScheduleDTO toDTO(BirthdaySchedule entity);
    List<BirthdayScheduleDTO> toDTOList(List<BirthdaySchedule> entityList);
}
