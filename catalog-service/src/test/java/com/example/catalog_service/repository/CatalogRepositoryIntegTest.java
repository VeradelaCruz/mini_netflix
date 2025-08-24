package com.example.catalog_service.repository;

import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.dtos.RatingScoreDTO;
import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.exception.MovieNotFound;
import com.example.catalog_service.mapper.CatalogMapper;
import com.example.catalog_service.models.Catalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
public class CatalogRepositoryIntegTest {
    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogMapper catalogMapper;

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
    @DisplayName("Should save all movies and set ratingAverage to 0.0")
    void createMovies_ShouldSaveAllAndResetRating() {
        // Simulamos lo que hace createMovies
        list.forEach(c -> c.setRatingAverage(0.0));
        List<Catalog> saved = catalogRepository.saveAll(list);

        // Verificar que se guardaron todas
        List<Catalog> allFromDb = catalogRepository.findAll();
        assertEquals(3, allFromDb.size());

        // Verificar que ratingAverage se haya puesto en 0.0
        assertTrue(allFromDb.stream().allMatch(c -> c.getRatingAverage() == 0.0));

        // Verificar que los IDs y títulos se mantuvieron
        assertEquals("1L", allFromDb.get(0).getMovieId());
        assertEquals("Title1", allFromDb.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find a movie by id")
    void findMovieById_ShouldReturnAMovie(){
        //Simulamos el servicio
       catalogRepository.save(catalog1);

       //Verificar que funciona en el repositorio:
        Catalog catalog= catalogRepository.findById(catalog1.getMovieId()).get();

        //Verifico valores:
        assertEquals("1L", catalog.getMovieId());
    }
    @Test
    @DisplayName("Should return an exception")
    void findMovieById_ShouldReturnAnException(){
        MovieNotFound exception= assertThrows(MovieNotFound.class,
                //El repositorio devuelve un Optional.
                // La excepción solo se lanza si vos usás .orElseThrow
                ()-> catalogRepository.findById("99L")
                        .orElseThrow(()->new MovieNotFound("99L")));

        assertEquals("Movie with id: 99L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should find all movies")
    void findAllMovies_ShouldReturnAList(){
        //Simulamos el service:
        catalogRepository.saveAll(list);
        //Verificamos que lo guardara y que funciona:
        List<Catalog> catalogList= catalogRepository.findAll();

        //Comparar:
        assertNotNull(catalogList);
        assertEquals(3, catalogList.size());
        
    }





}
