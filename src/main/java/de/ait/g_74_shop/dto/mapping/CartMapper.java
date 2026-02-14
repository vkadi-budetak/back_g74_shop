package de.ait.g_74_shop.dto.mapping;

import de.ait.g_74_shop.domain.Cart;
import de.ait.g_74_shop.dto.cart.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PositionMapper.class)
public interface CartMapper {

    CartDto mapEntityToDto(Cart entity);
}
