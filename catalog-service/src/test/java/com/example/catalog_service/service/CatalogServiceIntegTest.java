package com.example.catalog_service.service;

import com.example.catalog_service.config.CacheTestConfig;
import com.example.catalog_service.config.MongoTestConfig;
import com.example.catalog_service.config.RedisConfig;
import com.example.catalog_service.dtos.CatalogDTO;
import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.dtos.RatingScoreDTO;
import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.exception.MovieNotFound;
import com.example.catalog_service.exception.MovieNotFoundByName;
import com.example.catalog_service.mapper.CatalogMapper;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.repository.CatalogRepository;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

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
//Esto limpia la base de datos entre tests:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // limpia BD entre tests
@Testcontainers
public class CatalogServiceIntegTest {

    // 1️⃣ Declaramos el contenedor de Mongo
    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0");

    // 2️⃣ Configuramos Spring Boot para usar la URI del contenedor
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Autowired
    private CatalogService catalogService;

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
    @DisplayName("It should create a catalog with two movies")
    void createCatalogTest_ShouldReturnList(){
        List<Catalog> result = catalogService.createMovies(list);

        assertNotNull(result);
        assertEquals(3, result.size());

        List<Catalog> saved = catalogRepository.findAll();
        assertEquals(3, saved.size());
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
        assertEquals(3, catalogsResult.size());

        List<Catalog> catalogs= catalogRepository.findAll();
        assertEquals(3, catalogs.size());
    }

    @Test
    @DisplayName("Should update a movie from catalog using mapper")
    void updateCatalog_ShouldReturnDTO(){
        catalogRepository.save(catalog1);

        Catalog catalogUpdate= catalogService.changeCatalog(catalog1.getMovieId(), catalogUpdateDto);

        catalogRepository.save(catalogUpdate);

        assertNotNull(catalogUpdate);
        assertEquals(catalogUpdateDto.getTitle(), catalogUpdate.getTitle());
        assertEquals(Genre.valueOf(catalogUpdateDto.getGenre()), catalogUpdate.getGenre());
        assertEquals(catalogUpdateDto.getDescription(), catalogUpdate.getDescription());
        assertEquals(catalogUpdateDto.getReleaseYear(), catalogUpdate.getReleaseYear());
        assertEquals(catalogUpdateDto.getRatingAverage(), catalogUpdate.getRatingAverage());

        Catalog savedCatalog = catalogRepository.findById(catalog1.getMovieId())
                .orElseThrow(()->new MovieNotFound(catalog1.getMovieId()));
        assertEquals(catalogUpdateDto.getTitle(), savedCatalog.getTitle());
        assertEquals(Genre.valueOf(catalogUpdateDto.getGenre()), savedCatalog.getGenre());
        assertEquals(catalogUpdateDto.getDescription(), catalogUpdate.getDescription());
        assertEquals(catalogUpdateDto.getReleaseYear(), catalogUpdate.getReleaseYear());
        assertEquals(catalogUpdateDto.getRatingAverage(), catalogUpdate.getRatingAverage());

    }
    @Test
    @DisplayName("Should remove movie by ID")
    void removeCatalog_ShouldRemoveMovie() {
        catalogRepository.save(catalog1);

        catalogService.removeMovieById(catalog1.getMovieId());

        assertFalse(catalogRepository.existsById(catalog1.getMovieId()));
    }

    @Test
    @DisplayName("Should return exception")
    void removeCatalog_ShouldReturnAnException(){
        MovieNotFound exception =
                assertThrows(MovieNotFound.class,
        () -> catalogService.findMovieById("999L"));

        assertEquals("Movie with id: 999L not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return a movie by title, if exists")
    void findByTitle_ShouldReturnAMovie(){
        catalogRepository.save(catalog1);

        Catalog catalog = catalogService.findByTitle(catalog1.getTitle());

        assertNotNull(catalog);
        assertEquals("Title1", catalog.getTitle());

        Catalog result= catalogRepository.findByTitle(catalog1.getTitle())
                        .get();
        assertEquals(catalog.getTitle(),result.getTitle());

    }

    @Test
    @DisplayName("Should return an exception")
    void findTitle_ShouldReturnException(){
        MovieNotFoundByName exception = assertThrows(MovieNotFoundByName.class,
                ()->catalogService.findByTitle("Title99"));

                assertEquals("Movie with title: Title99 could not be found.", exception.getMessage());
    }


    @Test
    @DisplayName("Should update ratingScore and persist the change in DB")
    void changeScore_ShouldUpdateAndPersistRatingScore() {
        catalogRepository.save(catalog1);

        catalogService.changeScore(ratingScoreDTO);

        // Recuperar de DB para verificar persistencia
        Catalog result = catalogRepository.findById("1L")
                .orElseThrow(() -> new RuntimeException("Catalog not found"));

        assertEquals(4.5, result.getRatingAverage());
        assertEquals("1L", result.getMovieId());
    }



    @Test
    @DisplayName("Should find a list of movies by genre")
    void findByGenre_ShouldReturnList(){
        catalogRepository.saveAll(list);

        List<Catalog> catalogList= catalogService.findByGenre(genres);

        assertNotNull(catalogList);
        assertEquals(3, catalogList.size());

        //Cuando guardas con saveAll(list) en MongoDB,
        // el orden de recuperación no está garantizado a menos que hagas un sort
        //Asi no dependes del orden:
        assertTrue(catalogList.stream().anyMatch(c -> c.getGenre() == Genre.ACTION));
        assertTrue(catalogList.stream().anyMatch(c -> c.getGenre() == Genre.ANIMATION));
    }


    @Test
    @DisplayName("Should return a list with the top 3 from catalog")
    void  findTop3_ShouldReturnList(){
        catalogRepository.saveAll(list);

        List<Catalog> catalogs= catalogService.findTop3();


        assertNotNull(catalogs);
        assertEquals(3, catalogs.size());

        // Verifica que estén en orden descendente por ratingAverage
        assertEquals(4.4, catalogs.get(0).getRatingAverage());
        assertEquals(4.3, catalogs.get(1).getRatingAverage());
        assertEquals(3.9, catalogs.get(2).getRatingAverage());
    }


    @Test
    @DisplayName("Should group movies by genre")
    void groupByGenre_ShouldReturnAList(){
        catalogRepository.saveAll(list);

        Map<Genre, List<CatalogDTO>> result = catalogService.groupByGenre(Genre.ACTION);

        assertNotNull(result);
        assertTrue(result.containsKey(Genre.ACTION));

        List<CatalogDTO> actionMovies = result.get(Genre.ACTION);
        assertEquals(1, actionMovies.size());
        //Verifica que la primera (y en este caso única)
        // película de la lista tenga efectivamente el género ACTION.
        assertEquals(Genre.ACTION, actionMovies.get(0).getGenre());
        assertEquals(catalog2.getMovieId(), actionMovies.get(0).getMovieId());
    }

}


