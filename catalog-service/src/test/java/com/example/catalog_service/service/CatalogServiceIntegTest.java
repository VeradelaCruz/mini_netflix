package com.example.catalog_service.service;

import com.example.catalog_service.enums.Genre;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static sun.jvm.hotspot.runtime.BasicObjectLock.size;

@SpringBootTest
@ActiveProfiles("test")
public class CatalogServiceIntegTest {
    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CatalogRepository catalogRepository;

    private List<Catalog> list;

    @BeforeEach
    void setUp() {
        catalogRepository.deleteAll(); // limpiar BD antes de cada prueba

        Catalog catalog1 = new Catalog();
        catalog1.setMovieId("1L");
        catalog1.setTitle("Title1");
        catalog1.setGenre(Genre.ANIMATION);
        catalog1.setReleaseYear(1990);
        catalog1.setDescription("-----");

        Catalog catalog2 = new Catalog();
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
        List<Catalog> result= catalogService.createMovies(list);

        assertNotNull(result);
        assertEquals(2, result.size());

        List<Catalog> saved = catalogRepository.findAll();
        assertEquals(2, saved.size());
        assertTrue(saved.stream().allMatch(c -> c.getRatingAverage() == 0.0));
    }

    @Test
    @DisplayName("")


}
