package de.toxic2302.inventarbuddy.base.mapper;

import java.util.List;

public interface BaseMapper<E, D> {

    E mapToEntity(D dto);
    D mapToDto(E entity);

    List<D> mapToDtoList(List<E> entities);

    default E partialUpdate(E entity, D dto) {return entity;}
}
