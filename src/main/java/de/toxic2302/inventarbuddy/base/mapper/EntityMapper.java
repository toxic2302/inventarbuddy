package de.toxic2302.inventarbuddy.base.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EntityMapper<D, E> {
    E toEntity(D dto);
    D toDto(E entity);

    Collection<E> toEntityCollection(Collection<D> dto);
    Collection<D> toDtoCollection(Collection<E> entity);

    List<E> toEntityList(List<D> dto);
    List<D> toDtoList(List<E> entity);

    // workarounds
    default Page<D> toDtoPage(Page<E> entity) {
        return entity.map(this::toDto);
    }

    default Optional<D> toDtoOptional(Optional<E> entity) {
        return entity.map(this::toDto);
    }

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D dto);
}
