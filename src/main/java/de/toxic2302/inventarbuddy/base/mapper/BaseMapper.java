package de.toxic2302.inventarbuddy.base.mapper;

public interface BaseMapper<E, D> {

    E mapToEntity(D dto);
    D mapToDto(E entity);

    default E partialUpdate(E entity, D dto) {return entity;}
}
