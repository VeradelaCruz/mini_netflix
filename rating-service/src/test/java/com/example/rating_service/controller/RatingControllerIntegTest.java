package com.example.rating_service.controller;

import com.example.rating_service.client.CatalogClient;
import com.example.rating_service.dtos.CatalogDTO;
import com.example.rating_service.dtos.RatingAverageDTO;
import com.example.rating_service.dtos.RatingDTO;
import com.example.rating_service.dtos.RatingUserDTO;
import com.example.rating_service.enums.Score;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.models.Rating;
import com.example.rating_service.repository.RatingRepository;
import com.example.rating_service.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RatingControllerIntegTest {
    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private RatingRepository ratingRepository;

    @MockBean
    private CatalogClient catalogClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

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

        ratingDTO= new RatingDTO();
        ratingDTO.setId("1L");
        ratingDTO.setMovieId("C1");
        ratingDTO.setUserId("U1");
        ratingDTO.setComment("----");
        ratingDTO.setScore(Score.FOUR_STARS);

    }

    @Test
    @DisplayName("Should create ratings via POST enpoint")
    void addRating_ShouldReturnOk() throws Exception {
        String requestedBody= objectMapper.writeValueAsString(ratingList);

        mockMvc.perform(post("/rating/addRating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1L"))
                .andExpect(jsonPath("$[1].id").value("2L"))
                .andExpect(jsonPath("$[2].id").value("3L"));

        List<Rating> savedRatings = ratingRepository.findAll();
        assertEquals(3, savedRatings.size());

    }

    @Test
    @DisplayName("Should add a rating, calculate average, and return correct response")
    void addAndCalculateAverage_ShouldReturnCreated() throws Exception {
        // Guardamos ratings previos en la base para simular datos existentes
        ratingRepository.saveAll(ratingList);
        // Cuando el service llame al catalogClient.updateScore, simplemente hacemos nada
        when(catalogClient.updateScore(any())).thenReturn(null); // o un objeto válido


        // Llamamos al endpoint usando MockMvc
        mockMvc.perform(post("/rating/addAndCalculateAverage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movieId").value("C3"))
                .andExpect(jsonPath("$.averageScore").value(4.5))
                .andExpect(jsonPath("$.message").value("Rating added and catalog updated successfully"));

        // Verificamos que catalogClient.updateScore haya sido llamado exactamente una vez
        verify(catalogClient, times(1)).updateScore(any());
    }

}
