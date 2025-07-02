package com.upao.infraestructura.ms_movie_service.repositories;
import com.upao.infraestructura.ms_movie_service.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Object> findByTitle(String title);
}
