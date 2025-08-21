package com.example.rating_service.mapper;

import com.example.rating_service.dtos.RatingDTO;
import com.example.rating_service.models.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface RatingMapper {

    // Para actualizar un Rating existente con datos de un DTO de actualización
    //No es necesario generar una instancia como en catalog
    void updateRatingToDto(RatingDTO source, @MappingTarget Rating target);
    // Para convertir Rating a DTO al actualizar
    RatingDTO toDTO(Rating rating);
}
