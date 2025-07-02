package com.upao.infraestructura.ms_movie_service.controllers;

import com.upao.infraestructura.ms_movie_service.models.Movie;
import com.upao.infraestructura.ms_movie_service.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    @Autowired
    private MovieRepository repository;

    @GetMapping
    public ResponseEntity<?> getFirstMovie() {
        List<Movie> movies = repository.findAll();
        return movies.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(movies.get(0));
    }
}

