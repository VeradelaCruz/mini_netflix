package com.example.recommendation_service.client;

import com.example.recommendation_service.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8084/user")
public interface UserClient {
    @GetMapping("/getAll")
    public List<UserDTO> getAllUsers();
}
