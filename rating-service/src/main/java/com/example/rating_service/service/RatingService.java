package com.example.rating_service.service;

import com.example.rating_service.client.CatalogClient;
import com.example.rating_service.dtos.*;
import com.example.rating_service.enums.Score;
import com.example.rating_service.exception.MovieNotFoundById;
import com.example.rating_service.exception.RatingNotFoundException;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.models.Rating;
import com.example.rating_service.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingMapper ratingMapper;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CatalogClient catalogClient;

    //Operation CRUD:
    //Create Ratings
    @CacheEvict(value = {"ratings", "allRatings"}, allEntries = true)
    //"ratings" → Es el caché que guarda resultados individuales de findById().
    //"allRatings" → Es el caché que guarda la lista completa de findAllRating()
    public List<Rating> createRating(List<Rating> ratingList) {
        if (ratingList == null || ratingList.isEmpty()) {
            throw new IllegalArgumentException("The rating list cannot be empty");
        }
        return ratingRepository.saveAll(ratingList);
    }

    //We create a new rating from user and update score in catalog service
    @CacheEvict(value = {"ratings", "allRatings", "ratingsByMovie"}, allEntries = true)
    public RatingAverageDTO saveRatingAndUpdateCatalog(RatingUserDTO ratingDTO) {
        // Create rating
        Rating rating = new Rating();
        rating.setMovieId(ratingDTO.getMovieId());
        rating.setUserId(ratingDTO.getUserId());
        rating.setScore(Score.from(ratingDTO.getScore()));
        rating.setComment(ratingDTO.getComment());
        ratingRepository.save(rating);

        // 2. Get all ratings from rating:
        List<Rating> ratings = ratingRepository.findByMovieId(ratingDTO.getMovieId());

        // 3. Calculation for average score
        double average = ratings.stream()
                .map(Rating::getScore)
                .mapToInt(Score::getValue)
                .average()
                .orElse(0.0);

        // 4. Update ratingAverage in catalog service
        RatingScoreDTO dto = new RatingScoreDTO();
        dto.setMovieId(ratingDTO.getMovieId());
        dto.setRatingAverage(average);
        catalogClient.updateScore(dto);

        // 5. Return dto with average and movie id:
        RatingAverageDTO averageDTO = new RatingAverageDTO();
        averageDTO.setMovieId(ratingDTO.getMovieId());
        averageDTO.setAverageScore(average);
        return averageDTO;
    }


    //Get all ratings:
    @Cacheable(value = "allRatings")
    public List<Rating> findAllRating() {
        return ratingRepository.findAll();
    }

    //Get by rating id:
    //value = "ratings" → Nombre del "espacio de caché" en Redis.
    //key = "#id" → Cada rating se guarda en Redis con la clave de su ID.
    @Cacheable(value = "ratings", key = "#id")
    public Rating findById(String id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(id));
    }

    @Cacheable(value = "ratingsByMovie", key = "#movieId")
    public List<Rating> findAllByMovieId(String movieId) {
        //Call feign client to get movie
        CatalogDTO movie = catalogClient.getById(movieId);
        //Throw exception if it doesn't exist
        if (movie == null) {
            throw new MovieNotFoundById(movieId);
        } else {
            List<Rating> ratings = ratingRepository.findByMovieId(movieId);
            return ratings;
        }
    }


    //Update a rating
    @CacheEvict(value = {"ratings", "allRatings", "ratingsByMovie"}, allEntries = true)
    //value = {"ratings", "allRatings", "ratingsByMovie"} → Limpia todos los espacios de caché relacionados.
    //allEntries = true → Elimina todas las entradas dentro de cada espacio de caché listado.
    public RatingDTO changeRating(String id, RatingDTO ratingDTO) {
        Rating updatedRating = findById(id);
        ratingMapper.updateRatingToDto(ratingDTO, updatedRating);
        Rating saved = ratingRepository.save(updatedRating);
        return ratingMapper.toDTO(saved);
    }

    //Delete rating
    @CacheEvict(value = {"ratings", "allRatings", "ratingsByMovie"}, allEntries = true)
    public void removeRating(String id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(id));
        ratingRepository.deleteById(id);
    }

}
