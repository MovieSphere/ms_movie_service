package com.upao.infraestructura.ms_movie_service;

import com.upao.infraestructura.ms_movie_service.services.MovieService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MsMovieServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MsMovieServiceApplication.class, args);
        MovieService scraperService = context.getBean(MovieService.class);
        scraperService.fetchAndStoreMovie();
    }
}
