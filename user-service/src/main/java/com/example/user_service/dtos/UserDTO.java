package com.example.user_service.dtos;

import com.example.user_service.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String userName;
    private Email email;
    private List<String> preferences;
    private Role role;
}
