package com.example.recommendation_service.service;

import com.example.recommendation_service.client.CatalogClient;
import com.example.recommendation_service.client.UserClient;
import com.example.recommendation_service.dtos.CatalogDTO;
import com.example.recommendation_service.dtos.UserDTO;
import com.example.recommendation_service.exception.UserNotFoundException;
import com.example.recommendation_service.models.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    //Assign movies to a user based on preferences:

    public List<Recommendation> createRecommendation(){
        //Get all user's preferences
        List<UserDTO> users= userClient.getAllUsers();

        //Get all movies from catalog:
        List<CatalogDTO> movies= catalogClient.getAll();

        //Fillter for movies which match the user's preferences
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



}
