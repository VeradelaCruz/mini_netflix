package com.example.rating_service.controller;

import com.example.rating_service.client.CatalogClient;
import com.example.rating_service.config.CacheTestConfig;
import com.example.rating_service.config.MongoTestConfig;
import com.example.rating_service.config.RedisConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        //Cuando pruebas un endpoint con MockMvc que espera datos en formato JSON
        // (por ejemplo, un POST o un PUT),
        // debes enviar el contenido en el cuerpo de la solicitud
        // (request body) como una cadena JSON.
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
        when(catalogClient.updateScore(any())).thenReturn(null);


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

    @Test
    @DisplayName("Should show all ratings")
    void  getAll_ShouldReturnAList() throws Exception{
        ratingRepository.saveAll(ratingList);

        mockMvc.perform(get("/rating/getAll")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(ratingList.size()))
                .andExpect(jsonPath("$[0].id").value("1L"))
                .andExpect(jsonPath("$[1].id").value("2L"))
                .andExpect(jsonPath("$[2].id").value("3L"));
        List<Rating> result= ratingRepository.findAll();
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should return a rating via GET endpoint")
    void getById_ShouldReturnRating() throws Exception{
        ratingRepository.saveAll(ratingList);

        mockMvc.perform(get("/rating/{id}", rating1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1L"));

        Rating result = ratingRepository.findById(rating1.getId()).orElseThrow();
        assertEquals("1L", result.getId());
    }

    @Test
    @DisplayName("Should return a rating by movie id")
    void getByMovieId_shouldReturnRating() throws Exception{
        ratingRepository.saveAll(ratingList);

        when(catalogClient.getById("C1")).thenReturn(catalogDTO);

        mockMvc.perform(get("/rating/byMovie/{movieId}", rating1.getMovieId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value("C1"));

        verify(catalogClient, times(1)).getById("C1");

        List<Rating> result = ratingRepository.findByMovieId(rating1.getMovieId());
        assertEquals("C1", result.get(0).getMovieId());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return a rating updated")
    void updateRating_shouldReturnRating() throws  Exception{
        ratingRepository.saveAll(ratingList);

        mockMvc.perform(put("/rating/update/{id}", rating1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ratingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value("FOUR_STARS"))
                .andExpect(jsonPath("$.comment").value("----"));
    }

    @Test
    @DisplayName("Should remove a rating")
    void  removeRating_shouldReturnVoid() throws  Exception{
        ratingRepository.saveAll(ratingList);

        mockMvc.perform(delete("/rating/delete/{id}", rating1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


}
