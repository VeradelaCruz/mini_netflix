package com.example.user_service.repository;

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
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.models.User;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
@Testcontainers
@DataMongoTest
public class UserRepositoryIntegTest {

    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0")
            .withEnv("MONGO_INITDB_DATABASE", "catalog_test_db"); // esto crea la DB al iniciar

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }


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
    @DisplayName("Should save all users")
    void saveUsers(){
        List<User> list= userRepository.saveAll(userList);

        assertEquals(3, list.size());
        assertEquals("1L", list.get(0).getUserId());
        assertEquals("2L", list.get(1).getUserId());
        assertEquals("3L", list.get(2).getUserId());
    }

    @Test
    @DisplayName("Should find all users")
    void findAll(){
        userRepository.saveAll(userList);

        List<User> list= userRepository.findAll();
        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("Should find a user by id")
    void findById(){
        userRepository.saveAll(userList);

        User user= userRepository.findById(user1.getUserId()).get();
        assertEquals("1L", user.getUserId());
        assertEquals("email1@gmail.com", user.getEmail());
        assertTrue(user.getPreferences()
                .stream()
                .anyMatch(pref -> List.of("ACTION", "SCI_FIC").contains(pref)));
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    @DisplayName("Shoul remove a user fromm repository")
    void removeUser(){
        userRepository.saveAll(userList);

        userRepository.deleteById(user1.getUserId());
        assertFalse(userRepository.existsById(user1.getUserId()));
    }
    @Test
    @DisplayName("Should find a user by role")
    void findUserByRole(){
        userRepository.saveAll(userList);

        List<User> users= userRepository.findUsersByRole(Role.USER);
        assertEquals(2,  users.size());
        assertEquals(Role.USER, users.get(0).getRole());
        assertEquals(Role.USER, users.get(0).getRole());
    }

}
