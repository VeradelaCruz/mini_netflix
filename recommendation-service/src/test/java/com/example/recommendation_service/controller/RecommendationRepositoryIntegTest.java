package com.example.recommendation_service.controller;

import com.example.recommendation_service.config.CacheTestConfig;
import com.example.recommendation_service.config.RedisConfig;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.exception.UserNotFoundException;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import com.example.recommendation_service.service.MongoTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
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
public class RecommendationRepositoryIntegTest {

    @Autowired
    private RecommendationRepository recommendationRepository;

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
    @DisplayName("Should save recommendation in repository")
    void saveAll_shouldReturnList(){
        List<Recommendation> savedList =recommendationRepository.saveAll(recommendationList);

        assertNotNull(savedList);
        assertEquals(3, savedList.size());

        assertEquals("U1", savedList.get(0).getUserId());
        assertEquals("U2", savedList.get(1).getUserId());
        assertEquals("U2", savedList.get(2).getUserId());
    }

    @Test
    @DisplayName("Should find all recommendations")
    void findAll_shouldReturnList(){
        recommendationRepository.saveAll(recommendationList);

        List<Recommendation> recommendations= recommendationRepository.findAll();
        assertNotNull(recommendations);
        assertEquals(3, recommendations.size());

        // Verificamos que los IDs de usuario coinciden con los esperados
        assertTrue(recommendations.stream().anyMatch(r -> "U1".equals(r.getUserId())));
        assertTrue(recommendations.stream().anyMatch(r -> "U2".equals(r.getUserId())));
    }

    @Test
    @DisplayName("Should find recommendations by userId")
    void findByUserId_ShouldReturnList(){
        recommendationRepository.saveAll(recommendationList);

        Recommendation recommendation= recommendationRepository.findByUserId("U1").get();

        assertNotNull(recommendation);
        assertEquals("U1", recommendation.getUserId());
    }

    @Test
    @DisplayName("Should save a recommendation")
    void saveRecommendation_ShouldReturnRecommendation(){
        Recommendation savedRecommendation= recommendationRepository.save(recommendation1);

        // Validamos que los datos importantes se hayan persistido correctamente
        assertEquals(recommendation1.getUserId(), savedRecommendation.getUserId());
        assertEquals(recommendation1.getRecommendedMovies(), savedRecommendation.getRecommendedMovies());
    }
}
