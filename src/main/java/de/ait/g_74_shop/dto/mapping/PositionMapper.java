package de.ait.g_74_shop.dto.mapping;

import de.ait.g_74_shop.domain.Position;
import de.ait.g_74_shop.dto.position.PositionDto;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {

    PositionDto mapEntityToDto(Position entity);
    Set<PositionDto> mapEntitySetToDtoSet(Set<Position> entitySet);
}
