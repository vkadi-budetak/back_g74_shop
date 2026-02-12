package de.ait.g_74_shop.dto.mapping;

import de.ait.g_74_shop.domain.Product;
import de.ait.g_74_shop.dto.product.ProductDto;
import de.ait.g_74_shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    // пишемо метод який конвертує із продукта і закидуємо в ProductDto
    ProductDto mapEntityToDto(Product entity);
    Product mapDtoToEntity(ProductSaveDto dto);
}
