package com.example.recommendation_service.controller;

import com.example.recommendation_service.client.CatalogClient;
import com.example.recommendation_service.client.UserClient;
import com.example.recommendation_service.config.CacheTestConfig;
import com.example.recommendation_service.config.RedisConfig;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import com.example.recommendation_service.config.MongoTestConfig;
import com.example.recommendation_service.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@AutoConfigureMockMvc
@Testcontainers
public class RecommendationControllerIntegTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @MockBean
    private CatalogClient catalogClient;

    private Recommendation recommendation1;
    private Recommendation recommendation2;
    private Recommendation recommendation3;
    private List<Recommendation> recommendationList;

    //DTOs
    private CatalogDTO catalogDTO1;
    private CatalogDTO catalogDTO2;
    private CatalogDTO catalogDTO3;
    private CatalogDTO catalogDTO4;
    List<CatalogDTO> catalogDTOS1;
    List<CatalogDTO> catalogDTOS2;

    private UserDTO userDTO1;
    private UserDTO userDTO2;
    private UserDTO userDTO3;
    List<UserDTO> userDTOS;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @BeforeAll
    static void setUpAll() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @BeforeEach
    void setUp() {
        recommendationRepository.deleteAll();

        //DTOs
        catalogDTO1 = new CatalogDTO();
        catalogDTO1.setMovieId("1L");
        catalogDTO1.setTitle("Title1");
        catalogDTO1.setDescription("----");
        catalogDTO1.setRatingAverage(3.5);
        catalogDTO1.setGenre("ACTION");

        catalogDTO2 = new CatalogDTO();
        catalogDTO2.setMovieId("2L");
        catalogDTO2.setTitle("Title2");
        catalogDTO2.setDescription("----");
        catalogDTO2.setRatingAverage(4.5);
        catalogDTO2.setGenre("ANIMATION");

        catalogDTOS1 = List.of(catalogDTO1, catalogDTO2);

        catalogDTO3 = new CatalogDTO();
        catalogDTO3.setMovieId("3L");
        catalogDTO3.setTitle("Title3");
        catalogDTO3.setDescription("----");
        catalogDTO3.setRatingAverage(4.8);
        catalogDTO3.setGenre("SCI_FIC");

        catalogDTO4 = new CatalogDTO();
        catalogDTO4.setMovieId("4L");
        catalogDTO4.setTitle("Title4");
        catalogDTO4.setDescription("----");
        catalogDTO4.setRatingAverage(3.9);
        catalogDTO4.setGenre("DRAMA");

        catalogDTOS2 = List.of(catalogDTO3, catalogDTO4);

        // Recomendaciones
        recommendation1 = new Recommendation();
        recommendation1.setRecommendedMovies(catalogDTOS1);
        recommendation1.setGeneratedAt(LocalDate.now());
        recommendation1.setUserId("U1");

        recommendation2 = new Recommendation();
        recommendation2.setRecommendedMovies(catalogDTOS2);
        recommendation2.setGeneratedAt(LocalDate.now());
        recommendation2.setUserId("U2");

        recommendation3 = new Recommendation();
        recommendation3.setRecommendedMovies(catalogDTOS1);
        recommendation3.setGeneratedAt(LocalDate.now());
        recommendation3.setUserId("U2");

        recommendationList = List.of(recommendation1, recommendation2, recommendation3);

        // DTOs de usuarios
        userDTO1 = new UserDTO();
        userDTO1.setUserId("U1");
        userDTO1.setEmail("user1@gmail.com");
        userDTO1.setPreferences(List.of("ACTION", "ANIMATION", "DRAMA"));

        userDTO2 = new UserDTO();
        userDTO2.setUserId("U2");
        userDTO2.setEmail("user2@gmail.com");
        userDTO2.setPreferences(List.of("SCI_FIC", "ANIMATION", "DRAMA"));

        userDTO3 = new UserDTO();
        userDTO3.setUserId("U3");
        userDTO3.setEmail("user3@gmail.com");
        userDTO3.setPreferences(List.of("SCI_FIC", "ANIMATION"));

        userDTOS = List.of(userDTO1, userDTO2, userDTO3);
    }

    @Test
    @DisplayName("Should get all recommendations by GET endpoint.")
    void getAll_shouldReturnList() throws Exception{
        recommendationRepository.saveAll(recommendationList);

        mockMvc.perform(get("/recommendation/getAll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(recommendationList.size()));


        List<Recommendation> result= recommendationRepository.findAll();
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should get all recommendations by userId by GET endpoint")
    void getByUserId_shouldReturnARecommendation() throws Exception{
        recommendationRepository.saveAll(recommendationList);

        mockMvc.perform(get("/recommendation/userId/{userId}", recommendation1.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verifica que recommendedMovies exista y sea un arreglo
                .andExpect(jsonPath("$.recommendedMovies").isArray())
                // Verifica que al menos un elemento tenga genre "ACTION"
                //$ → Representa el objeto raíz del JSON que devuelve tu endpoint.
                //recommendedMovies → Es el atributo del objeto raíz que queremos inspeccionar (en tu caso, la lista de películas recomendadas).
                //[*] → Es un comodín que indica "todos los elementos del array".
                //.genre → Selecciona el atributo genre de cada elemento del array.
                .andExpect(jsonPath("$.recommendedMovies[*].genre", hasItem("ACTION")))
                // Verifica que al menos un elemento tenga genre "ANIMATION"
                .andExpect(jsonPath("$.recommendedMovies[*].genre", hasItem("ANIMATION")))
                // Opcional: verifica que userId de la respuesta sea el correcto
                .andExpect(jsonPath("$.userId").value("U1"));

    }

    @Test
    @DisplayName("Should create recommendations by POST endpoint")
    void createRecommendations_shouldReturnList() throws Exception{
        when(userClient.getAllUsers()).thenReturn(userDTOS);
        when(catalogClient.getAll()).thenReturn(Stream.of(catalogDTOS1, catalogDTOS2)
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        mockMvc.perform(post("/recommendation/createRecommendation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3)) // Esperamos 3 recomendaciones

                // Primera recomendación (U1)
                .andExpect(jsonPath("$[0].userId").value("U1"))
                .andExpect(jsonPath("$[0].recommendedMovies", hasSize(3)))
                .andExpect(jsonPath("$[0].recommendedMovies[*].genre",
                        containsInAnyOrder("ACTION", "ANIMATION", "DRAMA")))

                // Segunda recomendación (U2)
                .andExpect(jsonPath("$[1].userId").value("U2"))
                .andExpect(jsonPath("$[1].recommendedMovies", hasSize(3)))
                .andExpect(jsonPath("$[1].recommendedMovies[*].genre",
                        containsInAnyOrder("SCI_FIC", "ANIMATION", "DRAMA")))

                // Tercera recomendación (U3)
                .andExpect(jsonPath("$[2].userId").value("U3"))
                .andExpect(jsonPath("$[2].recommendedMovies", hasSize(2)))
                .andExpect(jsonPath("$[2].recommendedMovies[*].genre",
                        containsInAnyOrder("SCI_FIC", "ANIMATION")));
    }

    @Test
    @DisplayName("Should return a list of users by recommended movies")
    void getUsersByRecommendedMovie_shouldReturnAListUserDTO() throws Exception{
        recommendationRepository.saveAll(recommendationList);

        when(userClient.getAllUsers()).thenReturn(userDTOS);

        mockMvc.perform(get("/recommendation/userByMovie/{movieId}", "1L")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Debe haber 1 usuario con esa película recomendada
                .andExpect(jsonPath("$[0].userId").value("U1"))
                .andExpect(jsonPath("$[0].email").value("user1@gmail.com"));

    }

    @Test
    @DisplayName("Should return a list of recommendations by score")
    void getByMinScore_shouldReturnAList() throws Exception{
        recommendationRepository.saveAll(recommendationList);

        when(catalogClient.getAll()).thenReturn(catalogDTOS1);

        mockMvc.perform(get("/recommendation/getByMinScore/{minScore}", 4.0)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].movieId").value("2L"))
                .andExpect(jsonPath("$[0].ratingAverage").value(4.5));
    }

}
