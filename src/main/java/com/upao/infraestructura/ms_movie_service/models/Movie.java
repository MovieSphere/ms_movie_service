package com.upao.infraestructura.ms_movie_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "movies")
public class Movie {

    @Id
    private String id;
    private String title;
    private String synopsis;
    private List<String> genres;
    private double rating;
    private int releaseYear;
    private String s3ImageKey;
}
