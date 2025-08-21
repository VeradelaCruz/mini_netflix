package com.example.catalog_service.controller;

import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.models.Catalog;
import com.example.catalog_service.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
 @Autowired
  private CatalogService catalogService;
    @PostMapping("/addMovie")
    public ResponseEntity<List<Catalog>> addMovie(@Valid @RequestBody List<Catalog> catalogs){
        List<Catalog> created = catalogService.createMovies(catalogs);
        return ResponseEntity.ok(created);
    }


     @GetMapping("/getAll")
        public ResponseEntity<List<Catalog>> getAll(){
         return ResponseEntity.ok(catalogService.findAllMovies());
     }

    @GetMapping("/{movieId}")
    public ResponseEntity<Catalog> getById(@PathVariable String movieId){
        Catalog catalog = catalogService.findMovieById(movieId);
        return ResponseEntity.ok(catalog);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<Catalog> updateMovie(
            @PathVariable String movieId,
            @Valid @RequestBody CatalogUpdateDto catalogUpdateDto) {

        Catalog updated = catalogService.changeCatalog(movieId, catalogUpdateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable String movieId){
        catalogService.removeMovieById(movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byTitle/{title}")
    public ResponseEntity<Catalog> getByTitle(@PathVariable String title){
        Catalog catalog= catalogService.findByTitle(title);
        return ResponseEntity.ok(catalog);
    }



}
