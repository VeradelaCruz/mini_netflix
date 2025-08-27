package com.example.rating_service.service;

import com.example.rating_service.client.CatalogClient;
import com.example.rating_service.dtos.CatalogDTO;
import com.example.rating_service.dtos.RatingAverageDTO;
import com.example.rating_service.dtos.RatingDTO;
import com.example.rating_service.dtos.RatingUserDTO;
import com.example.rating_service.enums.Score;
import com.example.rating_service.exception.MovieNotFoundById;
import com.example.rating_service.exception.RatingNotFoundException;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.models.Rating;
import com.example.rating_service.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
public class RatingServiceIntegTest {
    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @MockBean
    private CatalogClient catalogClient;

    private Rating rating1;
    private Rating rating2;
    private Rating rating3;
    private List<Rating> ratingList;
    private RatingUserDTO ratingUserDTO;
    private RatingAverageDTO ratingAverageDTO;
    private CatalogDTO catalogDTO;
    private RatingDTO ratingDTO;



    @BeforeEach
    void  setUp(){
        ratingRepository.deleteAll();

        rating1= new Rating();
        rating1.setId("1L");
        rating1.setUserId("U1");
        rating1.setMovieId("C1");
        rating1.setScore(Score.THREE_STARS);
        rating1.setComment("----");
        rating1.setCreatedAt(LocalDate.now());

        rating2= new Rating();
        rating2.setId("2L");
        rating2.setUserId("U2");
        rating2.setMovieId("C2");
        rating2.setScore(Score.FOUR_STARS);
        rating2.setComment("----");
        rating2.setCreatedAt(LocalDate.now());

        rating3= new Rating();
        rating3.setId("3L");
        rating3.setUserId("U3");
        rating3.setMovieId("C3");
        rating3.setScore(Score.FIVE_STARS);
        rating3.setComment("----");
        rating3.setCreatedAt(LocalDate.now());

       ratingList= List.of(rating1, rating2, rating3);

        //--- DTOS----
       ratingUserDTO= new RatingUserDTO();
       ratingUserDTO.setUserId("U4");
       ratingUserDTO.setMovieId("C3");
       ratingUserDTO.setComment("----");
       ratingUserDTO.setScore(String.valueOf(Score.FOUR_STARS));

       ratingAverageDTO= new RatingAverageDTO();
       ratingAverageDTO.setMovieId("C3");
       ratingAverageDTO.setTitle("Title1");
       ratingAverageDTO.setDescription("----");
       ratingAverageDTO.setAverageScore(3.5);

       catalogDTO= new CatalogDTO();
       catalogDTO.setMovieId("C1");
       catalogDTO.setTitle("Title");
       catalogDTO.setDescription("----");
       catalogDTO.setRatingAverage(3.5);
//Mockear la respuesta del catalogClient para "C1", devolviendo un CatalogDTO válido.
        Mockito.when(catalogClient.getById("C1")).thenReturn(catalogDTO);
//Mockear la excepcion del catalogClient para "C1", devolviendo un CatalogDTO válido.
        Mockito.when(catalogClient.getById("C1")).thenReturn(null);

        ratingDTO= new RatingDTO();
        ratingDTO.setId("1L");
        ratingDTO.setMovieId("C1");
        ratingDTO.setUserId("U1");
        ratingDTO.setComment("----");
        ratingDTO.setScore(Score.FOUR_STARS);

    }

    @Test
    @DisplayName("Should create a rating list and save it")
    void createRating_ShouldReturnList(){
        List<Rating> list= ratingService.createRating(ratingList);

        assertNotNull(list);
        assertEquals(3, list.size());

        List<Rating> fromDb = ratingRepository.findAll();
        assertEquals(3, fromDb.size());
    }

    @Test
    @DisplayName("Should return an exception if the list is empty")
    void createRating_ShouldReturnException(){
        List<Rating> emptyList = Collections.emptyList();

        // Verificar que se lanza la excepción
        assertThrows(IllegalArgumentException.class,()->
                ratingService.createRating(emptyList));

        //Verifica con el valor null
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.createRating(null);
        });
    }

    @Test
    @DisplayName("Should create a new rating from user and update score in catalog service")
    void saveRatingAndUpdateCatalog_shouldReturnDTO(){
        //Setup: Guardamos ratings previos en la base para simular datos existentes
        ratingRepository.saveAll(List.of(rating1, rating2, rating3));

        //Acción: Creamos un nuevo rating y actualizamos promedio
        RatingAverageDTO ratingAverage= ratingService.saveRatingAndUpdateCatalog(ratingUserDTO);

        // Verificar que se guardó el nuevo rating
        List<Rating> ratings = ratingRepository.findByMovieId("C3");
        assertThat(ratings).hasSize(2);

        // Verificar cálculo del promedio
        double expectedAverage = (5 + 4) / 2.0;
        assertThat(ratingAverage.getAverageScore()).isEqualTo(expectedAverage);

        // Verificamos que el mock del microservicio CatalogClient
        // haya sido llamado exactamente una vez
        verify(catalogClient, times(1))
                // Verificamos que el metodo updateScore haya
                // sido llamado con un argumento (DTO) que cumpla ciertas condiciones
                .updateScore(
                        argThat(dto ->
                                // Condición 1: el movieId del DTO debe ser "C3"
                                dto.getMovieId().equals("C3") &&
                                        // Condición 2: el ratingAverage del DTO debe ser igual al promedio esperado calculado en la prueba
                                        dto.getRatingAverage() == expectedAverage
                        )
                );

    }

    @Test
    @DisplayName("Should return a list of ratings")
    void findAllRatings_ShouldReturnAList(){
        ratingRepository.saveAll(ratingList);

        List<Rating> list= ratingService.findAllRating();
        assertNotNull(list);
        assertEquals(3, list.size());

        List<Rating> result= ratingRepository.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should return a rating by id, if present")
    void  findById_ShouldReturnRating(){
        ratingRepository.saveAll(ratingList);

        Rating rating= ratingService.findById(rating1.getId());
        assertNotNull(rating);
        assertEquals("1L", rating.getId());
        assertEquals("U1", rating.getUserId());

        Rating result= ratingRepository.findById(rating1.getId()).get();
        assertNotNull(result);
        assertEquals("1L", result.getId());
        assertEquals("U1", result.getUserId());
    }

    @Test
    @DisplayName("Should return an exception if rating Id does not exist")
    void  findById_ShouldReturnAnException(){
        RatingNotFoundException exception= assertThrows(RatingNotFoundException.class,
                ()-> ratingService.findById(rating1.getId()));

        assertEquals("Rating with id: 1L not found.", exception.getMessage());
    }


    @Test
    @DisplayName("Should return a list by movie id")
    void findByMovieId_ShouldReturnAList(){
        //Guardamos ratings previos en la base para simular datos existentes
        ratingRepository.saveAll(ratingList);

        // Llamamos al metodo a probar, buscando por movieId "C1"
        List<Rating> list = ratingService.findAllByMovieId("C1");
        assertNotNull(list);
        assertEquals(1, list.size());

        // Comprobamos que todos los elementos tengan movieId igual a "C1"
        assertTrue(list.stream().allMatch(r -> "C1".equals(r.getMovieId())));

        // Verificamos que el mock del microservicio CatalogClient
        // haya sido llamado exactamente una vez con un argumento
        // cuyo movieId sea "C1"
        verify(catalogClient, times(1))
                .getById("C1");
    }

    @Test
    @DisplayName("Should throw MovieNotFoundById when movie does not exist (Integration Test)")
    void findByMovieId_ShouldReturnException_WhenMovieNotFound(){
        ratingRepository.saveAll(ratingList);

        MovieNotFoundById exception= assertThrows(MovieNotFoundById.class,
                ()->  ratingService.findAllByMovieId("C1"));

        assertEquals("Movie with id: C1 not found.", exception.getMessage());
    }


    @Test
    @DisplayName("Should update ratingScore in rating")
    void changeRating_ShouldReturnDTO(){
        ratingRepository.saveAll(ratingList);

        RatingDTO rating= ratingService.changeRating(rating1.getId(), ratingDTO);
        assertNotNull(rating);
        assertEquals("1L", rating.getId());
        assertEquals(Score.FOUR_STARS,rating.getScore());

        // Verificar cambios en DB
        Rating ratingFromDb = ratingRepository.findById(rating1.getId()).orElseThrow();
        assertEquals(Score.FOUR_STARS, ratingFromDb.getScore());
        assertEquals(Score.FOUR_STARS, rating.getScore());

    }

    @Test
    @DisplayName("Should remove a rating")
    void removeRating_ShouldVoid(){
        ratingRepository.saveAll(ratingList);
        ratingRepository.deleteById(rating1.getId());

        assertFalse(ratingRepository.existsById(rating1.getMovieId()));
    }

    @Test
    @DisplayName("Should return RatingNotFoundException ")
    void removeRating_ShouldReturnException(){
        RatingNotFoundException exception= assertThrows(RatingNotFoundException.class,
                ()->ratingService.removeRating(rating1.getId()));

        assertEquals("Rating with id: 1L not found.", exception.getMessage());
    }


}
