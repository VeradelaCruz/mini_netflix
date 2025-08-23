package com.example.user_service.dtos;

import com.example.user_service.enums.Role;
import lombok.Data;

@Data
public class UserRoleDTO {
    private String userId;
    private Role role;
    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
