package com.upao.infraestructura.ms_movie_service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upao.infraestructura.ms_movie_service.models.Movie;
import com.upao.infraestructura.ms_movie_service.repositories.MovieRepository;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger("MovieSphereLogger");

    @Value("${tmdb.token}")
    private String tmdbToken;

    @Value("${aws.accessKeyId}")
    private String awsAccessKey;

    @Value("${aws.secretAccessKey}")
    private String awsSecretKey;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    private static final Map<Integer, String> GENRE_MAP = Map.ofEntries(
            Map.entry(28, "Acción"), Map.entry(12, "Aventura"), Map.entry(16, "Animación"),
            Map.entry(35, "Comedia"), Map.entry(80, "Crimen"), Map.entry(99, "Documental"),
            Map.entry(18, "Drama"), Map.entry(10751, "Familiar"), Map.entry(14, "Fantasía"),
            Map.entry(36, "Historia"), Map.entry(27, "Terror"), Map.entry(10402, "Música"),
            Map.entry(9648, "Misterio"), Map.entry(10749, "Romance"), Map.entry(878, "Ciencia Ficción"),
            Map.entry(10770, "Película de TV"), Map.entry(53, "Suspenso"), Map.entry(10752, "Guerra"),
            Map.entry(37, "Western")
    );

    @PostConstruct
    public void fetchAndStoreMovie() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?language=es-ES&page=1")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + tmdbToken)
                    .build();

            Response response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode movieJson = root.get("results").get(0);

            String title = movieJson.get("title").asText();
            String synopsis = movieJson.get("overview").asText();
            double rating = movieJson.get("vote_average").asDouble();
            String releaseDate = movieJson.get("release_date").asText();
            List<String> genres = new ArrayList<>();
            for (JsonNode genreId : movieJson.get("genre_ids")) {
                String genreName = GENRE_MAP.getOrDefault(genreId.asInt(), "Desconocido");
                genres.add(genreName);
            }

            String posterPath = movieJson.get("poster_path").asText();
            String fullImageUrl = "https://image.tmdb.org/t/p/original" + posterPath;
            String imageKey = UUID.randomUUID() + "_poster.jpg";

            // Download image
            File tempFile = new File("/tmp/" + imageKey);
            try (InputStream in = new URL(fullImageUrl).openStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            // Upload to S3
            S3Client s3 = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                    .build();
            s3.putObject(PutObjectRequest.builder().bucket(bucketName).key(imageKey).build(),
                    Paths.get(tempFile.getAbsolutePath()));
            logger.info("Imagen subida a S3: " + imageKey);

            // Save to Mongo
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setSynopsis(synopsis);
            movie.setGenres(genres);
            movie.setRating(rating);
            movie.setReleaseYear(LocalDate.parse(releaseDate).getYear());
            movie.setS3ImageKey(imageKey);

            movieRepository.save(movie);
            logger.info("Película guardada en MongoDB: " + title);

        } catch (Exception e) {
            logger.error("Error al obtener o guardar la película: ", e);
        }
    }
}
