package com.example.recommendation_service.service;

import com.example.recommendation_service.client.CatalogClient;
import com.example.recommendation_service.client.UserClient;
import com.example.recommendation_service.config.CacheTestConfig;
import com.example.recommendation_service.config.RedisConfig;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.exception.MovieNotFoundException;
import com.example.recommendation_service.exception.UserNotFoundException;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

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
public class RecommendationServiceIntegTest {
    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void logDbName() {
        System.out.println("DB usada: " + mongoTemplate.getDb().getName());
    }


    @Test
    @DisplayName("Should find all recommendations")
    void findAll_shouldReturnAList(){
        recommendationRepository.saveAll(recommendationList);

        List<Recommendation> recommendations= recommendationService.findAllRecommendations();

        assertNotNull(recommendations);
        assertEquals(3, recommendations.size());

        List<Recommendation> result= recommendationRepository.findAll();
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should return recommendations by userId")
    void findByUserId_ShouldReturnRecommendation(){
        recommendationRepository.saveAll(recommendationList);

        Recommendation recommendation= recommendationService.findByUserId(recommendation1.getUserId());

        assertNotNull(recommendation);
        assertEquals("U1", recommendation.getUserId());

        Recommendation result= recommendationRepository.findByUserId(recommendation1.getUserId()).get();
        assertNotNull(result);
        assertEquals("U1", result.getUserId());
    }

    @Test
    @DisplayName("Should return exception when userId does not exist")
    void  findByUserId_ShouldReturnException(){
        UserNotFoundException exception= assertThrows(UserNotFoundException.class,
                ()-> recommendationService.findByUserId(recommendation1.getUserId()));

        assertNotNull(exception);
        assertEquals("User with name: U1 not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return list with users and recommended movies")
    void findUsersByRecommendedMovie_ShouldReturnUserList(){
        // Guardamos recomendaciones en la DB de test
        recommendationRepository.saveAll(recommendationList);

        // Mockeamos la respuesta del microservicio de usuarios
        when(userClient.getAllUsers()).thenReturn(userDTOS);

        // Llamamos al metodo que queremos testear
        List<UserDTO> result = recommendationService.findUsersByRecommendedMovie("1L");

        // Validaciones
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("U1", result.get(0).getUserId());

        // Verificaciones opcionales
        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should return an exception when findUsersByRecommendedMovie is called")
    void findUsersByRecommendedMovie_ShouldReturnException(){
        recommendationRepository.saveAll(recommendationList);

        when(userClient.getAllUsers()).thenReturn(userDTOS);

        MovieNotFoundException exception = assertThrows(
                MovieNotFoundException.class,
                () -> recommendationService.findUsersByRecommendedMovie("999L")
        );

        assertNotNull(exception);
        assertEquals("Movie with id: 999L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should create a recommendation with userId and preferences")
    void  createRecommendation_shouldReturnRecommendation(){
        // Mock responses
        when(userClient.getAllUsers()).thenReturn(userDTOS);
        when(catalogClient.getAll()).thenReturn(List.of(catalogDTO1, catalogDTO2, catalogDTO3, catalogDTO4));

        List<Recommendation> recommendations = recommendationService.createRecommendation();

        // Validamos que los userId coincidan
        List<String> expectedUserIds = List.of("U1", "U2", "U3");
        assertTrue(recommendations.stream()
                .map(Recommendation::getUserId)
                .allMatch(expectedUserIds::contains)
        );

        // Validamos que todas las películas recomendadas correspondan a las preferencias del usuario
        assertTrue(
                recommendations.stream() // recorremos cada Recommendation
                        .allMatch(rec ->
                                rec.getRecommendedMovies().stream() // recorremos cada película recomendada
                                        .allMatch(movie ->
                                                userDTOS.stream() // buscamos al usuario correspondiente por userId
                                                        .filter(u -> u.getUserId().equals(rec.getUserId())) // filtramos por userId
                                                        .findFirst() // tomamos el primer usuario que coincide (debería haber solo uno)
                                                        .get() // obtenemos el objeto UserDTO
                                                        .getPreferences() // obtenemos las preferencias de ese usuario
                                                        .contains(movie.getGenre()) // verificamos que el género de la película esté en sus preferencias
                                        )
                        )
        );
    }

    @Test
    @DisplayName("Should return all recommendations by score")
    void findRecommendationByScore_shouldReturnList(){
        recommendationRepository.saveAll(recommendationList);

        when(catalogClient.getAll()).thenReturn(List.of(catalogDTO1, catalogDTO2, catalogDTO3, catalogDTO4));
        when(userClient.getAllUsers()).thenReturn(userDTOS);
        
        List<CatalogDTO> list= recommendationService.findRecommendationByScore(4.0);
        assertNotNull(list);
        assertEquals(2, list.size());

        assertTrue(list.stream().allMatch(movie -> movie.getRatingAverage() >= 4.0));

    }

}
