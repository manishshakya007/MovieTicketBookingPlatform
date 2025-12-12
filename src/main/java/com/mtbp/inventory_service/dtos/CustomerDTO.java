package com.mtbp.inventory_service.dtos;

import java.util.Set;

public record CustomerDTO(
        String id,
        String name,
        String email,
        String phone,
        Set<String>roles
) { }
