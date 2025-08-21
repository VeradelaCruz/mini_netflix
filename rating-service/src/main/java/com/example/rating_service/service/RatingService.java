package com.example.rating_service.service;

import com.example.rating_service.client.CatalogClient;
import com.example.rating_service.dtos.CatalogDTO;
import com.example.rating_service.dtos.RatingAverageDTO;
import com.example.rating_service.dtos.RatingDTO;
import com.example.rating_service.dtos.RatingUserDTO;
import com.example.rating_service.enums.Score;
import com.example.rating_service.exception.MovieNotFoundById;
import com.example.rating_service.exception.RatingNotFoundException;
import com.example.rating_service.mapper.RatingMapper;
import com.example.rating_service.models.Rating;
import com.example.rating_service.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingMapper ratingMapper;
    @Autowired
    private  RatingRepository ratingRepository;
    @Autowired
    private  CatalogClient catalogClient;
 //Operation CRUD:
    //Create Ratings
    public List<Rating>  createRating(List<Rating> ratingList){
        if(ratingList==null || ratingList.isEmpty()){
            throw new IllegalArgumentException("The rating list cannot be empty");
        }
        return ratingRepository.saveAll(ratingList);
    }

    //Create only one rating:
    public Rating createOneRating(RatingUserDTO dto) {
        Rating rating = new Rating();
        rating.setMovieId(dto.getMovieId());
        rating.setUserId(dto.getUserId());
        rating.setComment(dto.getComment());

        if (dto.getScore() != null) {
            rating.setScore(Score.from(dto.getScore())); // Conversión de String a Enum
        }

        return ratingRepository.save(rating);
    }


    //Get all ratings:
    public List<Rating> findAllRating(){
        return ratingRepository.findAll();
    }

    //Get by rating id:
    public Rating findById(String id){
        return ratingRepository.findById(id)
                .orElseThrow(()->new RatingNotFoundException(id));
    }

    public List<Rating> findAllByMovieId(String movieId){
        //Call feign client to get movie
        CatalogDTO movie= catalogClient.getById(movieId);
        //Throw exception if it doesn't exist
        if (movie== null ){
            throw new MovieNotFoundById(movieId);
        }else {
            List<Rating> ratings = ratingRepository.findByMovieId(movieId);
            return ratings;
        }
    }


    //Update a rating
    public RatingDTO changeRating(String id, RatingDTO ratingDTO){
        Rating updatedRating = findById(id);
        ratingMapper.updateRatingToDto(ratingDTO, updatedRating);
        Rating saved = ratingRepository.save(updatedRating);
        return ratingMapper.toDTO(saved);
    }

    //Delete rating
    public void removeRating(String id){
        findAllByMovieId(id);
        ratingRepository.deleteById(id);
    }


    //Get averageScore from movies:
    public RatingAverageDTO calculateAverageScore(String movieId){
        //We call catalog-service to get the movie's name:
        CatalogDTO catalogDTO= catalogClient.getById(movieId);
        //Get all ratings:
        List<Rating> ratings = ratingRepository.findByMovieId(movieId);

        //Calculation of average scores made by all users:
        double average = ratings.stream()
                .map(Rating::getScore)           // extrae el Score de cada Rating
                .mapToInt(Score::getValue)  // ahora score es Score, llamamos al getValue()
                .average()
                .orElse(0.0);                    // devuelve 0 si no hay ratings

        //Assigning each value to dto
        RatingAverageDTO averageDTO= new RatingAverageDTO();
        averageDTO.setMovieId(movieId);
        averageDTO.setAverageScore(average);
        averageDTO.setTitle(catalogDTO.getTitle());
        averageDTO.setDescription(catalogDTO.getDescription());

        return averageDTO;
    }
}
