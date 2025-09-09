package com.example.rating_service.repository;

import com.example.rating_service.config.CacheTestConfig;
import com.example.rating_service.config.MongoTestConfig;
import com.example.rating_service.config.RedisConfig;
import com.example.rating_service.enums.Score;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.mapper.RatingMapperImpl;
import com.example.rating_service.models.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
// Indicamos a Spring que importe estas configuraciones adicionales solo para este test
// Esto permite usar beans definidos en MongoTestConfig y CacheTestConfig
@Import({MongoTestConfig.class, CacheTestConfig.class, RatingMapperImpl.class})

// Excluimos la configuración automática de Redis para este test
// Esto evita que Spring intente cargar Redis y su CacheManager real, que no necesitamos en tests
@ImportAutoConfiguration(exclude = RedisConfig.class)
@Testcontainers
@DataMongoTest
public class RatingRepositoryIntegTest {

    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        mongoContainer.start(); // asegúrate de iniciar el contenedor
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

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
    @DisplayName("Should save a list with ratings")
    void saveAll_ShouldReturnAList(){
        ratingRepository.saveAll(ratingList);

        List<Rating> ratings= ratingRepository.findAll();
        assertNotNull(ratings);

        assertEquals(3, ratings.size());
    }

    @Test
    @DisplayName("Should find all ratings")
    void findAll_ShouldReturnAList(){
        ratingRepository.saveAll(ratingList);

        List<Rating> list=ratingRepository.findAll();
        assertEquals(3, list.size());

    }

    @Test
    @DisplayName("Should find a rating by id")
    void findById_ShouldReturnRating(){
        ratingRepository.saveAll(ratingList);

        Rating rating= ratingRepository.findById(rating1.getId()).get();

        assertEquals("1L", rating.getId());
    }

    @Test
    @DisplayName("Should return a list of rating by movieId")
    void  findByMovieId_ShouldReturnAList(){
        ratingRepository.saveAll(ratingList);

        List<Rating> ratings= ratingRepository.findByMovieId(rating1.getMovieId());
        assertNotNull(ratings);
        assertEquals(1, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getMovieId().equals("C1")));

    }

    @Test
    @DisplayName("Should remove a rating")
    void deleteRating_ShouldReturnVoid(){
        ratingRepository.saveAll(ratingList);

        ratingRepository.deleteById(rating1.getId());

        assertFalse(ratingRepository.existsById(rating1.getId()));
    }
}
