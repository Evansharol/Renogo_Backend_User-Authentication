package com.example.renogo.mapper;

import com.example.renogo.dto.UserRequestDTO;
import com.example.renogo.dto.UserResponseDTO;
import com.example.renogo.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        return dto;
    }

    public static User toEntity(UserRequestDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());
        user.setDepartment(dto.getDepartment());
        user.setStatus(dto.getStatus());

        return user;
    }
}
