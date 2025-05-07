package com.upao.infraestructura.ms_movie_service.controllers;

import com.upao.infraestructura.ms_movie_service.models.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    @GetMapping
    public List<Movie> getAllMovies() {
        Movie movie = new Movie(1,"Titanic", "https://example.com/titanic.jpg");
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        return movies;
    }
}
