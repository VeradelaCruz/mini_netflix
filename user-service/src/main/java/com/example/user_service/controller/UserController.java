package com.example.user_service.controller;

import com.example.user_service.dtos.RatingUserDTO;
import com.example.user_service.dtos.UserDTO;
import com.example.user_service.dtos.UserRoleDTO;
import com.example.user_service.enums.Role;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private  UserService userService;
    @Autowired
    private  UserMapper userMapper;

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@Valid @RequestBody List<User> userList){
        userService.createUser(userList);
        return ResponseEntity.status(HttpStatus.CREATED).body(userList);
    }

    @GetMapping("/getAll")
    public List<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId){
        return userService.findById(userId);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId,
                                        @Valid @RequestBody UserDTO userDTO){
        User updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @GetMapping("/getByRole/{role}")
    public ResponseEntity<?> getUserByRole(@PathVariable Role role){
        List<User> list= userService.findByRoles(role);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/sendRating")
    public ResponseEntity<String> sendRating(@RequestBody RatingUserDTO ratingUserDTO){
        return ResponseEntity.ok(userService.sendRatingAndUpdateCatalog(ratingUserDTO));
    }

    @GetMapping("/get-by-preferences")
    public ResponseEntity<List<User>> getUsersByPreferences(@RequestBody List<String> preferences) {
        List<User> users = userService.findUserByPreferences(preferences);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count-by-roles")
    public ResponseEntity<List<UserRoleDTO>> countUsersByRoles() {
        List<UserRoleDTO> result = userService.countUsersByRoles();
        return ResponseEntity.ok(result);
    }


}
