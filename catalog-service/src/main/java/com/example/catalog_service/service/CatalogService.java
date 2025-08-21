package com.example.catalog_service.service;

import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.exception.MovieNotFound;
import com.example.catalog_service.exception.MovieNotFoundByName;
import com.example.catalog_service.mapper.CatalogMapper;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    @Autowired
 private CatalogRepository catalogRepository;

 //CRUD operations:
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
    public Catalog findMovieById(String movieId){
        return catalogRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFound(movieId));
    }

    //Find all movies:
    public List<Catalog> findAllMovies(){
     return catalogRepository.findAll();
    }

    //Update a movie using mapper:
    public Catalog changeCatalog(String movieId, CatalogUpdateDto catalogUpdateDto){
        Catalog updatedCatalog = findMovieById(movieId);

        CatalogMapper.INSTANCE.updateCatalogFromDto(catalogUpdateDto, updatedCatalog);

        return catalogRepository.save(updatedCatalog);
    }


    //Delete a movie
    public void removeMovieById(String movieId){
     //Throws exception
        findMovieById(movieId);
        //Deletes movie
        catalogRepository.deleteById(movieId);
    }

    //Other CRUD operations:
    public Catalog findByTitle(String title){
     return catalogRepository.findByTitle(title)
             .orElseThrow(()-> new MovieNotFoundByName(title));
    }



}
