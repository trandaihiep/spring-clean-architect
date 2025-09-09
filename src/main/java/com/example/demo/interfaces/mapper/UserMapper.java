package com.example.demo.interfaces.mapper;

import com.example.demo.domain.model.User;
import com.example.demo.interfaces.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}

