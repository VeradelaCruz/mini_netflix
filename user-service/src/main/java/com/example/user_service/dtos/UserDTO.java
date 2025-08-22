package com.example.user_service.dtos;

import com.example.user_service.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String userId;
    private String email;
    private List<String> preferences;
    private Role role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
