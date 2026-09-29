package io.github.eggy03.yabb.mapper;

import java.util.List;

public interface CommonMappingInterface<E, D> {

    E toEntity(D dto);
    List<E> toEntityList(List<D> dtoList);

    D toDTO(E entity);
    List<D> toDTOList(List<E> entityList);
}
