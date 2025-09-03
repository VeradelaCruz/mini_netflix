package com.example.user_service.service;

import com.example.user_service.client.CatalogClient;
import com.example.user_service.client.RatingClient;
import com.example.user_service.config.CacheTestConfig;
import com.example.user_service.config.MongoTestConfig;
import com.example.user_service.config.RedisConfig;
import com.example.user_service.dtos.RatingScoreDTO;
import com.example.user_service.dtos.RatingUserDTO;
import com.example.user_service.dtos.UserDTO;
import com.example.user_service.dtos.UserRoleDTO;
import com.example.user_service.enums.Role;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"

})
@ActiveProfiles("test")
// Indicamos a Spring que importe estas configuraciones adicionales solo para este test
// Esto permite usar beans definidos en MongoTestConfig y CacheTestConfig
@Import({MongoTestConfig.class, CacheTestConfig.class})

// Excluimos la configuración automática de Redis para este test
// Esto evita que Spring intente cargar Redis y su CacheManager real, que no necesitamos en tests
@ImportAutoConfiguration(exclude = RedisConfig.class)
public class UserSeriviceIntegTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private RatingClient ratingClient;

    @MockBean
    private CatalogClient catalogClient;

    @Autowired
    private UserMapper userMapper;

    private User user1;
    private User user2;
    private User user3;
    List<User> userList;

    //DTOs
    private UserDTO userDTO;
    private RatingScoreDTO ratingScoreDTO;
    private RatingUserDTO ratingUserDTO;
    private UserRoleDTO roleDTO;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();

        user1= new User();
        user1.setUserId("1L");
        user1.setEmail("email1@gmail.com");
        user1.setPreferences(List.of("ACTION", "SCI_FIC"));
        user1.setRole(Role.USER);

        user2= new User();
        user2.setUserId("2L");
        user2.setEmail("email2@gmail.com");
        user2.setPreferences(List.of("DRAMA", "SCI_FIC"));
        user2.setRole(Role.USER);

        user3= new User();
        user3.setUserId("3L");
        user3.setEmail("email3@gmail.com");
        user3.setPreferences(List.of("ACTION", "DRAMA"));
        user3.setRole(Role.ADMIN);

        userList = List.of(user1, user2, user3);

        //DTOs
        userDTO= new UserDTO();
        userDTO.setUserId("1L");
        userDTO.setEmail("email11@gmail.com");
        userDTO.setPreferences(List.of("ANIMATION", "SCI_FIC"));
        userDTO.setRole(Role.USER);

        ratingUserDTO= new RatingUserDTO();
        ratingUserDTO.setMovieId("1M");
        ratingUserDTO.setId("1R");
        ratingUserDTO.setUserId("1U");
        ratingUserDTO.setComment("----");
        ratingUserDTO.setScore("THREE_STARS");


        ratingScoreDTO= new RatingScoreDTO();
        ratingScoreDTO.setMovieId("1M");
        ratingScoreDTO.setRatingAverage(3.0);

    }

    @Test
    @DisplayName("Should create an user if it's not empty")
    void createUser_shouldReturnAUserList(){
        List<User> list= userService.createUser(userList);

        assertNotNull(list);
        assertEquals(3, list.size());

        List<User> result= userRepository.saveAll(userList);
        assertNotNull(result);
        assertEquals(3, result.size());

    }

    @Test
    @DisplayName("Should return an exception in case userList is empty")
    void createUser_shouldReturnException() {
        List<User> emptyList= new ArrayList<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(emptyList));

        assertNotNull(exception);
        assertEquals("The user list cannot be empty", exception.getMessage());

    }

    @Test
    @DisplayName("Should find all users")
    void findAllUsers_shouldReturnAList(){
        userRepository.saveAll(userList);

        List<User> list= userService.findAll();

        assertNotNull(list);
        assertEquals(3, list.size());

        List<User> result= userRepository.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should return a user by id")
    void findById_shouldReturnUser(){
        userRepository.saveAll(userList);

        User user= userService.findById(user1.getUserId());
        assertNotNull(user);
        assertEquals("1L", user.getUserId());
        assertEquals("email1@gmail.com", user.getEmail());
        assertEquals(List.of("ACTION", "SCI_FIC"), user.getPreferences());
        assertEquals(Role.USER, user.getRole());

        User result= userRepository.findById(user.getUserId()).get();
        assertEquals("1L", result.getUserId());
        assertEquals("email1@gmail.com", result.getEmail());
        assertEquals(List.of("ACTION", "SCI_FIC"), result.getPreferences());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("Should return an exception if id is not found")
    void findById_shouldReturnException(){
        UserNotFoundException exception= assertThrows(UserNotFoundException.class,
                ()-> userService.findById(user1.getUserId()));

        assertEquals("User with name: 1L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should update a user")
    void  updateUser_shouldReturnUserUpdated(){
        userRepository.saveAll(userList);

        User user= userService.updateUser(user1.getUserId(), userDTO);
        assertEquals("1L", user.getUserId());
        assertEquals("email11@gmail.com", user.getEmail());
        assertEquals(List.of("ANIMATION", "SCI_FIC"), user.getPreferences());
        assertEquals(Role.USER, user.getRole());

        User result= userRepository.save(user);
        assertEquals("1L", result.getUserId());
        assertEquals("email11@gmail.com", result.getEmail());
        assertEquals(List.of("ANIMATION", "SCI_FIC"), result.getPreferences());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    @DisplayName("Should remove a user if present")
    void removeUser_shouldVoid(){
        userRepository.saveAll(userList);

        userService.removeUser(user1.getUserId());

        assertFalse(userRepository.existsById(user1.getUserId()));
    }

    @Test
    @DisplayName("Should return an exception if is not present")
    void removeUser_shouldException(){
        UserNotFoundException exception= assertThrows(UserNotFoundException.class,
                ()-> userService.findById("99L"));

        assertEquals("User with name: 99L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return a list of users by role if present")
    void findByRoles_shouldReturnAList(){
        userRepository.saveAll(userList);

        List<User> users= userService.findByRoles(Role.USER);

        assertEquals(2, users.size());
        assertEquals(Role.USER, users.get(0).getRole());
        assertEquals(Role.USER, users.get(1).getRole());

        List<User> result= userRepository.findUsersByRole(Role.USER);
        assertEquals(2, result.size());
        assertEquals(Role.USER, result.get(0).getRole());
        assertEquals(Role.USER, result.get(1).getRole());
    }

    @Test
    @DisplayName("Should return an exception if is not present")
    void findByRoles_shouldReturnException() {
        userRepository.saveAll(List.of(user1,user2));

        RuntimeException exception= assertThrows(RuntimeException.class,
                ()-> userService.findByRoles(Role.ADMIN));

        assertEquals("No users found with role: ADMIN", exception.getMessage() );
    }

    @Test
    @DisplayName("Should send a rating from user and update ratingaAverage")
    void sendRatingAndUpdateCatalog_shouldReturnMessage(){
        userRepository.saveAll(userList);

        String result = userService.sendRatingAndUpdateCatalog(ratingUserDTO);

        // Assert: verificamos que el RatingClient fue llamado una vez con el DTO correcto
        verify(ratingClient, times(1)).addAndCalculateAverage(ratingUserDTO);

        // Assert: verificamos que el mensaje retornado es el esperado
        assertEquals("Rating sent and average updated successfully.", result);
    }

    @Test
    @DisplayName("Should return a list of user by preferences")
    void findUserByPreferences_shouldReturnAList(){
        userRepository.saveAll(userList);

        List<User> list= userService.findUserByPreferences(List.of("ACTION", "SCI_FIC"));
        assertEquals(3, list.size());
        assertThat(list.stream()
                .anyMatch(pref -> List.of("ACTION", "SCI_FIC").contains(pref)));
    }
}
