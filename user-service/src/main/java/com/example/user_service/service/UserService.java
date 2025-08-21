package com.example.user_service.service;

import com.example.user_service.client.RatingClient;
import com.example.user_service.dtos.RatingUserDTO;
import com.example.user_service.dtos.UserDTO;
import com.example.user_service.enums.Role;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private RatingClient ratingClient;

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
    public String sendRating(RatingUserDTO ratingDTO){
        ratingClient.addOneRating(ratingDTO);
        return "Rating sent successfully.";
    }

}
