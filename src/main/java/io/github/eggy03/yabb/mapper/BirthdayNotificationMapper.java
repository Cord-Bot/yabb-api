package io.github.eggy03.yabb.mapper;

import io.github.eggy03.yabb.dto.BirthdayNotificationDTO;
import io.github.eggy03.yabb.entity.BirthdayNotification;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface BirthdayNotificationMapper {

    BirthdayNotificationDTO toDTO(@NonNull BirthdayNotification entity);
    List<BirthdayNotificationDTO> toDTOList(@NonNull List<BirthdayNotification> entityList);

}
