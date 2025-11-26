package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.request.UserCreateRequestDTO;
import com.microtech.smartshop.dto.request.UserUpdateRequestDTO;
import com.microtech.smartshop.dto.response.UserResponseDTO;
import com.microtech.smartshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    User toEntity(UserCreateRequestDTO dto);

    void updateEntityFromDTO(UserUpdateRequestDTO dto, @MappingTarget User entity);
}


