package com.example.user_service.service;

import com.example.user_service.client.CatalogClient;
import com.example.user_service.client.RatingClient;
import com.example.user_service.dtos.RatingUserDTO;
import com.example.user_service.dtos.UserDTO;
import com.example.user_service.dtos.UserRoleDTO;
import com.example.user_service.enums.Role;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private RatingClient ratingClient;
    @Autowired
    private CatalogClient catalogClient;

    //Create user:
    @CacheEvict(value = {"allUsers", "userList"}, allEntries = true)
    public List<User> createUser(List<User> userList){
        if(userList.isEmpty()|| userList==null){
            throw new IllegalArgumentException("The user list cannot be empty");
        }
        return userRepository.saveAll(userList);
    }

    //Get all users:
    @Cacheable(value = "allUsers")
    public List<User> findAll(){
    return userRepository.findAll();
    }

    //Get user by name:
    @Cacheable(value = {"allUsers", "userId"}, key = "#userId")
    public User findById(String userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(userId));
    }

    //Update a user:
    @CacheEvict(value = {"userId", "userDTO", "allUsers"}, allEntries = true)
    public User updateUser(String userId, UserDTO userDTO){
        User updatedUser= findById(userId);
        userMapper.updateUserToDTO(userDTO, updatedUser);
        return userRepository.save(updatedUser);
    }

    //Delete user:
    @CacheEvict(value = {"allUsers", "userName"}, allEntries = true)
    public void removeUser(String userId){
        User user = findById(userId);
        userRepository.delete(user);
    }

    //Find users by role:
    @Cacheable(value = {"allUsers", "role"}, key = "#role")
    public List<User> findByRoles(Role role){
        List<User> users = userRepository.findUsersByRole(role);
        if (users.isEmpty()) {
            throw new RuntimeException("No users found with role: " + role);
        }
        return users;
    }

    //Create a rating:
    @CircuitBreaker(name = "ratingServiceCB",
            fallbackMethod = "fallbackUpdateScore")
    @CacheEvict(value = {"allUsers, ratingDTO"}, allEntries = true)
    public String sendRatingAndUpdateCatalog(RatingUserDTO ratingDTO) {
            ratingClient.addAndCalculateAverage(ratingDTO);
            return "Rating sent and average updated successfully.";
    }

    public void fallbackRating(RatingUserDTO ratingUserDTO, Throwable t) {
        System.out.println("CircuitBreaker activado. No se pudo enviar rating: " + t.getMessage());
    }


    //Filter users by preferences:
    @Cacheable(value = {"preferences", "allUsers"}, key = "#preferences")
    public List<User> findUserByPreferences(List<String> preferences){
        Set<String> prefsSet = new HashSet<>(preferences);
        return findAll().stream()
                .filter(user -> Optional.ofNullable(user.getPreferences())
                        .orElse(Collections.emptyList())
                        .stream()
                        .anyMatch(prefsSet::contains))
                .collect(Collectors.toList());
    }

    //Count users by role:
    @Cacheable(value = "usersByRole")
    public List<UserRoleDTO> countUsersByRoles() {
        return findAll().stream()
                // Agrupa los usuarios por rol
                .collect(Collectors.groupingBy(User::getRole))
                // Recorremos cada par (rol, lista de usuarios)
                .entrySet().stream()
                // Convertimos a UserRoleDTO
                .map(entry -> {
                    UserRoleDTO dto = new UserRoleDTO();
                    dto.setRole(entry.getKey());
                    // cantidad de usuarios
                    dto.setAmount(entry.getValue().size());
                    // lista de IDs
                    dto.setUserIds(entry.getValue().stream()
                            .map(User::getUserId)
                            .collect(Collectors.toList()));
                    return dto;
                })
                // Convertimos los DTOs a lista
                .collect(Collectors.toList());
    }


}
