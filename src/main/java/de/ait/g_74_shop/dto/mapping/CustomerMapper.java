package de.ait.g_74_shop.dto.mapping;

import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CartMapper.class)
public interface CustomerMapper {

    CustomerDto mapEntityToDto(Customer entity);
    Customer mapDtoToEntity(CustomerSaveDto saveDto);
}
