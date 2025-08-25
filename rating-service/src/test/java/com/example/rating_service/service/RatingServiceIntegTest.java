package com.example.rating_service.service;

import com.example.rating_service.enums.Score;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.models.Rating;
import com.example.rating_service.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    private Rating rating1;
    private Rating rating2;
    private Rating rating3;
    private List<Rating> ratingList;


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

}
