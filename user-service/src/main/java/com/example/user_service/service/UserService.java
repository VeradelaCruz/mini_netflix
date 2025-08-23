package com.example.user_service.service;

import com.example.user_service.client.CatalogClient;
import com.example.user_service.client.RatingClient;
import com.example.user_service.dtos.RatingScoreDTO;
import com.example.user_service.dtos.RatingUserDTO;
import com.example.user_service.dtos.UserDTO;
import com.example.user_service.dtos.UserRoleDTO;
import com.example.user_service.enums.Role;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<User> createUser(List<User> userList){
        if(userList.isEmpty()|| userList==null){
            throw new IllegalArgumentException("The user list cannot be empty");
        }
        return userRepository.saveAll(userList);
    }

    //Get all users:
    public List<User> findAll(){
    return userRepository.findAll();
    }

    //Get user by name:
    public User findById(String userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(userId));
    }

    //Update a user:
    public User updateUser(String userId, UserDTO userDTO){
        User updatedUser= findById(userId);
        userMapper.updateUserToDTO(userDTO, updatedUser);
        return userRepository.save(updatedUser);
    }

    //Delete user:
    public void removeUser(String userName){
        User user = findById(userName);
        userRepository.delete(user);
    }

    //Find users by role:
    public List<User> findByRoles(Role role){
        List<User> users = userRepository.findUsersByRole(role);
        if (users.isEmpty()) {
            throw new RuntimeException("No users found with role: " + role);
        }
        return users;
    }

    //Create a rating:
    public String sendRatingAndUpdateCatalog(RatingUserDTO ratingDTO) {
        ratingClient.addAndCalculateAverage(ratingDTO);
        return "Rating sent and average updated successfully.";
    }


    //Filter users by preferences:
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
    public List<UserRoleDTO> countUsersByRoles() {
        return findAll().stream()
                //Agrupa los usuarios por rol y cuenta cuántos hay en cada grupo
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()))
                //Recorremos cada par (rol, cantidad)
                .entrySet().stream()
                //Convertimos a UserRoleDTO
                .map(entry -> {
                    UserRoleDTO dto = new UserRoleDTO();
                    dto.setRole(entry.getKey());
                    dto.setAmount(entry.getValue().intValue());
                    return dto;
                })
                //Convertimos los dtos a lista
                .collect(Collectors.toList());
    }

}
