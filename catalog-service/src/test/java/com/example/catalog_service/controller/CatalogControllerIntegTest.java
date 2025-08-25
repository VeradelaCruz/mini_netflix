package com.example.catalog_service.controller;

import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.dtos.RatingScoreDTO;
import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.repository.CatalogRepository;
import com.example.catalog_service.service.CatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CatalogControllerIntegTest {
    @Autowired
    private CatalogService catalogService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Catalog catalog1;
    private Catalog catalog2;
    private Catalog catalog3;
    private List<Catalog> list;
    private CatalogUpdateDto catalogUpdateDto;
    private RatingScoreDTO ratingScoreDTO;
    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        catalogRepository.deleteAll();

        catalog1 = new Catalog();
        catalog1.setMovieId("1L");
        catalog1.setTitle("Title1");
        catalog1.setGenre(Genre.ANIMATION);
        catalog1.setReleaseYear(1990);
        catalog1.setDescription("-----");
        catalog1.setRatingAverage(4.4);

        catalog2 = new Catalog();
        catalog2.setMovieId("2L");
        catalog2.setTitle("Title2");
        catalog2.setGenre(Genre.ACTION);
        catalog2.setReleaseYear(1991);
        catalog2.setDescription("-----");
        catalog2.setRatingAverage(4.3);

        catalog3 = new Catalog();
        catalog3.setMovieId("3L");
        catalog3.setTitle("Title3");
        catalog3.setGenre(Genre.COMEDY);
        catalog3.setReleaseYear(1990);
        catalog3.setDescription("-----");
        catalog3.setRatingAverage(3.9);

        list = List.of(catalog1, catalog2, catalog3);

        catalogUpdateDto= new CatalogUpdateDto();
        catalogUpdateDto.setTitle("Title4");
        catalogUpdateDto.setGenre("ACTION");
        catalogUpdateDto.setReleaseYear(1992);
        catalogUpdateDto.setDescription("-----");
        catalogUpdateDto.setRatingAverage(4.5);


        ratingScoreDTO = new RatingScoreDTO();
        ratingScoreDTO.setMovieId("1L");
        ratingScoreDTO.setRatingAverage(4.5);

        genres= List.of(Genre.ACTION, Genre.ANIMATION,Genre.COMEDY);

    }


    @Test
    @DisplayName("Should create movies via POST endpoint")
    void addMovie_ShouldReturnCreatedList() throws Exception {
        String requestBody = objectMapper.writeValueAsString(list);

        mockMvc.perform(post("/catalog/addMovie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value("1L"))
                .andExpect(jsonPath("$[1].movieId").value("2L"))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));

    }
    @Test
    @DisplayName("Should get all movies via GET endpoint")
    void getAll_ShouldReturnAList() throws Exception {
        catalogRepository.saveAll(list);

        mockMvc.perform(get("/catalog/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(list.size()))
                .andExpect(jsonPath("$[0].movieId").value("1L"))
                .andExpect(jsonPath("$[1].movieId").value("2L"))
                .andExpect(jsonPath("$[2].movieId").value("3L"));
    }

    @Test
    @DisplayName("Should return catalog by ID via GET endpoint")
    void getById_ShouldReturnCatalog() throws Exception {
        // Preparar datos en la base de datos en memoria
        catalogRepository.saveAll(list);

        // Crear el DTO que se enviará como RequestBody
        String requestBody = objectMapper.writeValueAsString(ratingScoreDTO);

        mockMvc.perform(get("/catalog/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value("1L"))
                .andExpect(jsonPath("$.title").value("Title1"));
    }

    @Test
    @DisplayName("Should update a movie")
    void updateCatalog_ShouldReturnAMovieUpdated() throws Exception {
        catalogRepository.saveAll(list);
        String requestBody= objectMapper.writeValueAsString(catalogUpdateDto);

        mockMvc.perform(put("/catalog/update/{movieId}", catalog1.getMovieId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value("1L"))
                .andExpect(jsonPath("$.title").value("Title4"))
                .andExpect(jsonPath("$.genre").value("ACTION"))
                .andExpect(jsonPath("$.description").value("-----"))
                .andExpect(jsonPath("$.ratingAverage"). value(4.5))
                .andExpect(jsonPath("$.releaseYear").value(1992));

    }

    @Test
    @DisplayName("Should remove a movie")
    void deleteMovie_ShouldReturnOk() throws Exception{
        catalogRepository.saveAll(list);

        mockMvc.perform(delete("/catalog/delete/{movieId}", catalog1.getMovieId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        // Verificamos que realmente se eliminó de la base de datos
        assertFalse(catalogRepository.existsById(catalog1.getMovieId()));
    }
    @Test
    @DisplayName("Should return 404 when deleting a non-existing movie")
    void deleteMovie_NonExisting_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/catalog/delete/{movieId}", "999L")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
