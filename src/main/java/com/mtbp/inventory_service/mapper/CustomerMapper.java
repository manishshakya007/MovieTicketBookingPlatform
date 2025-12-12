package com.mtbp.inventory_service.mapper;

import com.mtbp.inventory_service.dtos.CustomerDTO;
import com.mtbp.inventory_service.entities.Customer;
import com.mtbp.inventory_service.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "roles", source = "roles")
    com.mtbp.inventory_service.dtos.CustomerDTO toDTO(Customer customer);

    @Mapping(target = "roles", ignore = true)
    Customer toEntity(CustomerDTO dto);

    List<CustomerDTO> toDTOList(List<Customer> customers);

    List<Customer> toEntityList(List<CustomerDTO> dtos);

    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
