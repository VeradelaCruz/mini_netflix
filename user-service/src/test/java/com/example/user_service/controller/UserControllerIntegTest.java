package com.example.user_service.controller;

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
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "eureka.instance.enabled=false"
})
@ActiveProfiles("test")
@Import({MongoTestConfig.class, CacheTestConfig.class})
@ImportAutoConfiguration(exclude = {
        RedisConfig.class,
        org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.class,
        org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration.class
})
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private RatingClient ratingClient;

    @MockBean
    private CatalogClient catalogClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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
    @DisplayName("Should add a User in repository")
    void addUser_shouldReturnAList() throws Exception{
        //Estás serializando userList en vez del UserDTO que representa la actualización.
        String requestedBody= objectMapper.writeValueAsString(userList);

        mockMvc.perform(post("/user/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestedBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(3))
                // ✅ Primer usuario
                .andExpect(jsonPath("$[0].userId").value("1L"))
                .andExpect(jsonPath("$[0].email").value("email1@gmail.com"))
                .andExpect(jsonPath("$[0].preferences[*]",
                        containsInAnyOrder("ACTION", "SCI_FIC")))
                .andExpect(jsonPath("$[0].role").value("USER"))

                // ✅ Segundo usuario
                .andExpect(jsonPath("$[1].userId").value("2L"))
                .andExpect(jsonPath("$[1].email").value("email2@gmail.com"))
                .andExpect(jsonPath("$[1].preferences[*]",
                        containsInAnyOrder("DRAMA", "SCI_FIC")))
                .andExpect(jsonPath("$[1].role").value("USER"))

                // ✅ Tercer usuario
                .andExpect(jsonPath("$[2].userId").value("3L"))
                .andExpect(jsonPath("$[2].email").value("email3@gmail.com"))
                .andExpect(jsonPath("$[2].preferences[*]",
                        containsInAnyOrder("ACTION", "DRAMA")))
                .andExpect(jsonPath("$[2].role").value("ADMIN"));

    }

    @Test
    @DisplayName("Should get all users")
    void getAllUsers_shouldReturnList() throws Exception {
        userRepository.saveAll(userList);

        // Ejecutamos la petición
        ResultActions result = mockMvc.perform(get("/user/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Recorremos la lista con forEach y validamos cada usuario
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);

            result.andExpect(jsonPath("$[" + i + "].userId").value(user.getUserId()))
                    .andExpect(jsonPath("$[" + i + "].email").value(user.getEmail()))
                    .andExpect(jsonPath("$[" + i + "].preferences[*]",
                            containsInAnyOrder(user.getPreferences().toArray())))
                    .andExpect(jsonPath("$[" + i + "].role").value(user.getRole().name()));
        }

        result.andExpect(jsonPath("$.length()").value(userList.size()));
    }

    @Test
    @DisplayName("Should return a user by id")
    void  getUserById_shouldReturnUser() throws Exception{
        userRepository.saveAll(userList);

        mockMvc.perform(get("/user/{userId}", user1.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("1L"))
                .andExpect(jsonPath("$.email").value("email1@gmail.com"))
                .andExpect(jsonPath("$.preferences[*]",
                        containsInAnyOrder("ACTION", "SCI_FIC")))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Should update a user")
    void updateUser_shouldReturnUser() throws Exception {
        userRepository.saveAll(userList);

        // Solo usamos el DTO que representa la actualización
        //Aqui va el body que iria en el endpoint en caso de post o put
        String requestedBody = objectMapper.writeValueAsString(userDTO);

        // Llamamos al endpoint PUT
        mockMvc.perform(put("/user/update/{userId}", user1.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("1L"))
                .andExpect(jsonPath("$.email").value("email11@gmail.com"))
                .andExpect(jsonPath("$.preferences[*]",
                        containsInAnyOrder("ANIMATION", "SCI_FIC")))
                .andExpect(jsonPath("$.role").value("USER"));

        // Verificamos que la base de datos se haya actualizado
        User updatedUser = userRepository.findById(user1.getUserId()).orElseThrow();
        assertEquals("email11@gmail.com", updatedUser.getEmail());
        assertEquals(List.of("ANIMATION", "SCI_FIC"), updatedUser.getPreferences());

    }

    @Test
    @DisplayName("Should get users by role")
    void getUsersByRole_shouldReturnList() throws Exception {
        userRepository.saveAll(userList);

        mockMvc.perform(get("/user/getByRole/{role}", Role.USER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // solo USER
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].role").value("USER"));
    }

    @Test
    @DisplayName("Should send rating to rating service and verify DTO")
    void sendRating_shouldReturnMessage() throws Exception {
        // Guardamos usuarios (aunque no es estrictamente necesario aquí)
        userRepository.saveAll(userList);

        // Serializamos el DTO que vamos a enviar
        String requestedBody = objectMapper.writeValueAsString(ratingUserDTO);

        // Llamamos al endpoint
        mockMvc.perform(post("/user/sendRating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestedBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Rating sent and average updated successfully."));

        // Capturamos el DTO que el service envió al client
        ArgumentCaptor<RatingUserDTO> captor = ArgumentCaptor.forClass(RatingUserDTO.class);
        verify(ratingClient, times(1)).addAndCalculateAverage(captor.capture());

        RatingUserDTO sentDTO = captor.getValue();

        // Verificamos que los campos sean correctos
        assertEquals("1U", sentDTO.getUserId());
        assertEquals("1M", sentDTO.getMovieId());
        assertEquals("1R", sentDTO.getId());
        assertEquals("THREE_STARS", sentDTO.getScore());
        assertEquals("----", sentDTO.getComment());
    }

    @Test
    @DisplayName("Should return a list of users by preferences")
    void getByPreferences_shouldReturnList() throws Exception{
        userRepository.saveAll(userList);

        mockMvc.perform(get("/user/get-by-preferences")
                        .param("preferences", "SCI_FIC", "ACTION")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].userId").value("1L"))
                .andExpect(jsonPath("$[1].userId").value("2L"))
                .andExpect(jsonPath("$[2].userId").value("3L"));

    }

    @Test
    @DisplayName("Should count users by roles")
    void countUsersByRoles_shouldReturnList() throws Exception{
        userRepository.saveAll(userList);

        mockMvc.perform(get("/user/count-by-roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder("ADMIN", "USER")))
                .andExpect(jsonPath("$[*].amount", containsInAnyOrder(1, 2)));

    }




}
