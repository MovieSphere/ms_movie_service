package com.upao.infraestructura.ms_movie_service.services;

import com.upao.infraestructura.ms_movie_service.models.Movie;
import com.upao.infraestructura.ms_movie_service.repositories.MovieRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieServiceTests {

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void clearDB() {
        movieRepository.deleteAll();
    }

    @Test
    void testInsertMovie() {
        // Arrange – Crear película de prueba
        Movie movie = new Movie();
        movie.setTitle("Dune");
        movie.setSynopsis("Sci-fi epic");
        movie.setGenres(Arrays.asList("Ciencia Ficción"));
        movie.setRating(8.5);
        movie.setReleaseYear(2024);
        movie.setS3ImageKey("dune.jpg");

        // Act – Guardar en MongoDB
        movieRepository.save(movie);

        // Assert – Verificar que hay una película guardada
        assertEquals(1, movieRepository.findAll().size());
    }

    @Test
    void testRetrieveInsertedMovie() {
        // Arrange – Insertar una película con título conocido
        Movie movie = new Movie();
        movie.setTitle("Blade Runner");
        movieRepository.save(movie);

        // Act – Recuperar la película
        Movie retrieved = movieRepository.findAll().get(0);

        // Assert – Validar que el título coincide
        assertEquals("Blade Runner", retrieved.getTitle());
    }

    @Test
    void testUpdateMovieRating() {
        // Arrange – Crear y guardar película con rating inicial
        Movie movie = new Movie();
        movie.setTitle("Arrival");
        movie.setRating(7.5);
        movie = movieRepository.save(movie);

        // Act – Modificar rating y guardar de nuevo
        movie.setRating(9.0);
        movieRepository.save(movie);
        Optional<Movie> updated = movieRepository.findById(movie.getId());

        // Assert – Verificar que el nuevo rating es el esperado
        assertTrue(updated.isPresent());
        assertEquals(9.0, updated.get().getRating());
    }

    @Test
    void testDeleteMovie() {
        // Arrange – Insertar una película
        Movie movie = new Movie();
        movie.setTitle("Enemy");
        movie = movieRepository.save(movie);

        // Act – Eliminar la película por ID
        movieRepository.deleteById(movie.getId());

        // Assert – Asegurar que la base de datos está vacía
        assertTrue(movieRepository.findAll().isEmpty());
    }

    @Test
    void testMultipleMovieInsertions() {
        // Arrange – Crear dos películas distintas
        Movie m1 = new Movie(); m1.setTitle("Dune");
        Movie m2 = new Movie(); m2.setTitle("Prisoners");

        // Act – Guardarlas en MongoDB
        movieRepository.saveAll(Arrays.asList(m1, m2));

        // Assert – Verificar que se guardaron ambas
        assertEquals(2, movieRepository.findAll().size());
    }
}
