package com.example.recommendation_service.controller;

import com.example.recommendation_service.client.CatalogClient;
import com.example.recommendation_service.client.UserClient;
import com.example.recommendation_service.config.CacheTestConfig;
import com.example.recommendation_service.config.RedisConfig;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import com.example.recommendation_service.service.MongoTestConfig;
import com.example.recommendation_service.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

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
    @DisplayName("Should get all recommendations by GET enpoint.")
    void getAll_shouldReturnList() throws Exception{
        recommendationRepository.saveAll(recommendationList);

        String requestedBody= objectMapper.writeValueAsString(recommendationList);

        mockMvc.perform(get("/recommendation/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(recommendationList.size()));


        List<Recommendation> result= recommendationRepository.findAll();
        assertEquals(3, result.size());
    }
}
