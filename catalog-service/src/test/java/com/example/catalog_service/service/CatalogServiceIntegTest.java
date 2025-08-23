package com.example.catalog_service.service;

import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.exception.MovieNotFound;
import com.example.catalog_service.exception.MovieNotFoundByName;
import com.example.catalog_service.mapper.CatalogMapper;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CatalogServiceIntegTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogMapper catalogMapper;

    private Catalog catalog1;
    private Catalog catalog2;
    private List<Catalog> list;

    @BeforeEach
    void setUp() {
        catalogRepository.deleteAll();

        catalog1 = new Catalog();
        catalog1.setMovieId("1L");
        catalog1.setTitle("Title1");
        catalog1.setGenre(Genre.ANIMATION);
        catalog1.setReleaseYear(1990);
        catalog1.setDescription("-----");

        catalog2 = new Catalog();
        catalog2.setMovieId("2L");
        catalog2.setTitle("Title2");
        catalog2.setGenre(Genre.ACTION);
        catalog2.setReleaseYear(1991);
        catalog2.setDescription("-----");

        list = List.of(catalog1, catalog2);
    }

    @Test
    @DisplayName("It should create a catalog with two movies")
    void createCatalogTest_ShouldReturnList(){
        List<Catalog> result = catalogService.createMovies(list);

        assertNotNull(result);
        assertEquals(2, result.size());

        List<Catalog> saved = catalogRepository.findAll();
        assertEquals(2, saved.size());
        assertTrue(saved.stream().allMatch(c -> c.getRatingAverage() == 0.0));
    }

    @Test
    @DisplayName("It should find a movie by id, if founded")
    void findMovieById_ShouldReturnAMovie() {
        // Arrange - persistir datos en la BD de prueba
        catalogRepository.save(catalog1);

        // Act - llamar al metodo del service
        Catalog catalogResult = catalogService.findMovieById(catalog1.getMovieId());

        // Assert - validar el resultado
        assertNotNull(catalogResult);
        assertEquals("1L", catalogResult.getMovieId());

        // Verificar también desde el repositorio
        Catalog catalog = catalogRepository.findById(catalog1.getMovieId())
                .orElseThrow();
        assertEquals("1L", catalog.getMovieId());
    }

    @Test
    @DisplayName("It should throw MovieNotFound when movie id does not exist")
    void findMovieById_ShouldReturnAnException() {

        MovieNotFound exception = assertThrows(
                MovieNotFound.class,
                () -> catalogService.findMovieById("999L") // ID que no existe
        );

        assertEquals("Movie with id: 999L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("It should return all movies from catalog")
    void findAll_ShouldReturnList(){
        catalogRepository.saveAll(list);

        List<Catalog> catalogsResult= catalogService.findAllMovies();

        assertNotNull(catalogsResult);
        assertEquals(2, catalogsResult.size());

        List<Catalog> catalogs= catalogRepository.findAll();
        assertEquals(2, catalogs.size());
    }

    

}


