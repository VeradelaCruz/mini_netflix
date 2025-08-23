package com.example.user_service.dtos;

import com.example.user_service.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleDTO {
    private List<String> userIds;
    private Role role;
    private long amount;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
