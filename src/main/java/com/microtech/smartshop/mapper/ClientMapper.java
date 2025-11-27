package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.request.ClientCreateRequestDTO;
import com.microtech.smartshop.dto.response.ClientResponseDTO;
import com.microtech.smartshop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientResponseDTO toResponse(Client client);

    // Alias for consistency
    default ClientResponseDTO toResponseDTO(Client client) {
        return toResponse(client);
    }

    Client toEntity(ClientCreateRequestDTO dto);
}
