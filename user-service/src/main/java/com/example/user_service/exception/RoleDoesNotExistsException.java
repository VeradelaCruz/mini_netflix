package com.example.user_service.exception;

import com.example.user_service.enums.Role;

public class RoleDoesNotExistsException extends RuntimeException {
    public RoleDoesNotExistsException(Role role) {
        super("The role you are searching does not exists.");
    }
}
