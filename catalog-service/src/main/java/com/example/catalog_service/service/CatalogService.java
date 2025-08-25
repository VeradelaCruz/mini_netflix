package com.example.catalog_service.service;

import com.example.catalog_service.dtos.CatalogDTO;
import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.dtos.RatingScoreDTO;
import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.exception.GenreNotFoundException;
import com.example.catalog_service.exception.MovieNotFound;
import com.example.catalog_service.exception.MovieNotFoundByName;
import com.example.catalog_service.mapper.CatalogMapper;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    @Autowired
 private CatalogRepository catalogRepository;

 //CRUD operations:
    //value = "movies" → nombre del cache donde se guardará
    //"allMovies" → el cache que guarda la lista completa de películas.
    //allEntries → Significa que se borran todas las
 // entradas de esos caches, no solo una clave específica.
 @CacheEvict(value = {"movies", "allMovies"}, allEntries = true)
 //Create a movie for the catalog
 public List<Catalog> createMovies(List<Catalog> catalogs){
     if(catalogs == null || catalogs.isEmpty()){
         throw new IllegalArgumentException("The catalog list cannot be empty");
     }
     //Make ratingAverage(0.0) when a movie is created
     catalogs.forEach(c -> c.setRatingAverage(0.0));

     return catalogRepository.saveAll(catalogs);
    }

    //Find a movie by movieId:
    @Cacheable(value = "movies", key = "#movieId")
    public Catalog findMovieById(String movieId){
        return catalogRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFound(movieId));
    }

    //Find all movies:
    @Cacheable(value = "allMovies")
    public List<Catalog> findAllMovies(){
     return catalogRepository.findAll();
    }

    //Update a movie using mapper:
    @CachePut(value = "movies", key = "#movieId")
    public Catalog changeCatalog(String movieId, CatalogUpdateDto catalogUpdateDto){
        Catalog updatedCatalog = findMovieById(movieId);

        CatalogMapper.INSTANCE.updateCatalogFromDto(catalogUpdateDto, updatedCatalog);

        return catalogRepository.save(updatedCatalog);
    }


    //Delete a movie
    @CacheEvict(value = "movies", key = "#movieId")
    public void removeMovieById(String movieId){
     //Throws exception
        findMovieById(movieId);
        //Deletes movie
        catalogRepository.deleteById(movieId);
    }

    //Other CRUD operations:
    @Cacheable(value = "moviesByTitle", key = "#title")
    public Catalog findByTitle(String title){
     return catalogRepository.findByTitle(title)
             .orElseThrow(()-> new MovieNotFoundByName(title));
    }

    //Update score:
    @CachePut(value = "movies", key = "#dto.movieId")
    @CacheEvict(value = "top3Movies", allEntries = true)
    public Catalog changeScore(RatingScoreDTO dto){
        Catalog catalog= findMovieById(dto.getMovieId());
        catalog.setRatingAverage(dto.getRatingAverage());
        return catalogRepository.save(catalog);
    }
//ARREGLAR PARA VERIFICAR SI LOS GENEROS INTRODUCIDOS EXISTEN
    //MANEJO DE ERRORES:
    //Filter by genre
    public List<Catalog> findByGenre(List<Genre> genres) {
        Set<Genre> genreSet = new HashSet<>(genres); // Para optimizar contains}
        return findAllMovies().stream()
                //Solo mantenemos las películas cuyo género está en la lista genres
                .filter(catalog -> genreSet.contains(catalog.getGenre()))
                .collect(Collectors.toList());
    }

    //Find Top 3 movies with better rating:
    @Cacheable(value = "top3Movies")
    public List<Catalog> findTop3(){
     return findAllMovies().stream()
             //Ordena las películas por ratingAverage de mayor a menor.
             //.reversed() invierte el orden natural (que sería de menor a mayor).
             .sorted(Comparator.comparingDouble(Catalog::getRatingAverage).reversed())
             // Take first 3
             .limit(3)
             .collect(Collectors.toList());
    }

    //Group movies by genre
    public Map<Genre, List<CatalogDTO>> groupByGenre(Genre genre){
        List<CatalogDTO> filtered = findAllMovies().stream()
                .filter(movie -> movie.getGenre().equals(genre))
                .map(CatalogDTO::new)
                .toList();

        if (filtered.isEmpty()) {
            throw new GenreNotFoundException(genre);
        }

        return filtered.stream()
                .collect(Collectors.groupingBy(CatalogDTO::getGenre));
    }
}
