package com.example.catalog_service.mapper;
import com.example.catalog_service.dtos.CatalogUpdateDto;
import com.example.catalog_service.enums.Genre;
import com.example.catalog_service.models.Catalog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

//Esto le dice a MapStruct que esta interfaz es un mapper, es decir, un convertidor entre objetos.
//Si un campo del objeto de origen (source) es null, no sobrescribas el valor existente en el objeto destino (target)
//Source es el objeto que enviamos en la request
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CatalogMapper {
    //Esta línea crea una instancia del mapper para usarlo en tu código
    CatalogMapper INSTANCE = Mappers.getMapper(CatalogMapper.class);

    //source → el objeto con los datos que quieres actualizar (el que viene de la request).
    //@MappingTarget Catalog target → el objeto existente que quieres modificar.
    //MapStruct se encarga de copiar solo los campos no nulos de source a target.
    void updateCatalogFromDto(CatalogUpdateDto source, @MappingTarget Catalog target);

    //Esto convierte automáticamente el String del DTO al Enum de la entidad.
    default Genre map(String genre) {
        if (genre == null) return null;
        return Genre.valueOf(genre.toUpperCase());
    }
}
