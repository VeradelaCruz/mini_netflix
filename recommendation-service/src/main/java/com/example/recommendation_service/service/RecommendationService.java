package com.example.recommendation_service.service;

import com.example.recommendation_service.client.CatalogClient;
import com.example.recommendation_service.client.UserClient;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.exception.MovieNotFoundException;
import com.example.recommendation_service.exception.UserNotFoundException;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecommendationService {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private CatalogClient catalogClient;
    @Autowired
    private UserClient userClient;

    //Get all recommendations:
    public List<Recommendation> findAllRecommendations(){
        return recommendationRepository.findAll();
    }

    //Get by id
    public  Recommendation findByUserId(String userId){
        return recommendationRepository.findByUserId(userId)
                .orElseThrow(()-> new UserNotFoundException(userId));
    }

    //Get users by recommended movie:
    public List<UserDTO> findUsersByRecommendedMovie(String movieId) {
        // Primero obtenemos los IDs de usuarios que tienen esta película recomendada
        List<String> usersId = findAllRecommendations().stream()
                .filter(recommendation -> recommendation.getRecommendedMovies() != null)
                .filter(recommendation -> recommendation.getRecommendedMovies()
                        .stream()
                        .anyMatch(movie -> movieId.equals(movie.getMovieId()))) // Compara con el movieId de cada CatalogDTO
                .map(Recommendation::getUserId)
                .collect(Collectors.toList());

        // Si no hay usuarios con esa película, lanzamos excepción
        if (usersId.isEmpty()) {
            throw new MovieNotFoundException(movieId);
        }

        // Filtramos todos los usuarios obtenidos del microservicio UserClient
        return userClient.getAllUsers().stream()
                .filter(user -> usersId.contains(user.getUserId()))
                .collect(Collectors.toList());
    }

    //Assign movies to a user based on preferences:

    public List<Recommendation> createRecommendation(){
        //Get all user's preferences
        List<UserDTO> users= userClient.getAllUsers();

        //Get all movies from catalog:
        List<CatalogDTO> movies= catalogClient.getAll();

        //Filter for movies which match the user's preferences
        return users.stream().map(user -> {
            List<CatalogDTO> recommendedMovies = movies.stream()
                    .filter(movie -> user.getPreferences().contains(movie.getGenre())) // coincidencia por género
                    .sorted(Comparator.comparing(CatalogDTO::getRatingAverage, Comparator.nullsLast(Comparator.reverseOrder()))) // ordenar por rating
                    .map(movie -> {
                        CatalogDTO rec = new CatalogDTO();
                        rec.setMovieId(movie.getMovieId());
                        rec.setTitle(movie.getTitle());
                        rec.setDescription(movie.getDescription());
                        rec.setRatingAverage(movie.getRatingAverage());
                        rec.setGenre(movie.getGenre());
                        return rec;
                    })
                    .collect(Collectors.toList());

            Recommendation recommendation = new Recommendation();
            recommendation.setUserId(user.getUserId());
            recommendation.setRecommendedMovies(recommendedMovies);
            recommendation.setGeneratedAt(LocalDate.now());

            return recommendationRepository.save(recommendation);
        }).collect(Collectors.toList());
    }

    // Filter recommended movies by minimum score
    public List<CatalogDTO> findRecommendationByScore(double minScore) {
        // Get all movies from catalog-service with ratingAverage greater than or equal to minScore
        List<CatalogDTO> catalogDTO = catalogClient.getAll()
                .stream()
                .filter(catalog -> catalog.getRatingAverage() >= minScore)
                .collect(Collectors.toList());

        // Extract all recommended movie IDs from all recommendations
        Set<String> recommendedMovieIds = findAllRecommendations().stream()
                .flatMap(rec -> {
            if (rec.getRecommendedMovies() != null) {
                return rec.getRecommendedMovies().stream(); // Stream<CatalogDTO>
            } else {
                return Stream.empty(); // In case recommendedMovies is null
            }})
                .map(CatalogDTO::getMovieId)                          // Convert Stream<CatalogDTO> -> Stream<String>
                .collect(Collectors.toSet());                        // Collect as Set<String>

        // Filter the catalog movies to include only those that are in the recommended movies
        return catalogDTO.stream()
                .filter(movie -> recommendedMovieIds.contains(movie.getMovieId()))
                .toList();
    }

}
