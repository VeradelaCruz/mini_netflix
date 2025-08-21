package com.example.user_service.repository;

import com.example.user_service.enums.Role;
import com.example.user_service.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface UserRepository extends MongoRepository<User,String> {
 List<User> findUsersByRole(Role role);
}
