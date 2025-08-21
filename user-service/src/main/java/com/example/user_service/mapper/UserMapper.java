package com.example.user_service.mapper;

import com.example.user_service.dtos.UserDTO;
import com.example.user_service.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface UserMapper {
    void updateUserToDTO(UserDTO source,@MappingTarget User target);
    UserDTO toDto(User user);
}
