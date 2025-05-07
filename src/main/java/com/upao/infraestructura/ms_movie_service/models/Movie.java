package com.upao.infraestructura.ms_movie_service.models;

public class Movie {
    private int id;
    private String name;
    private String image;

    public Movie() {
    }

    public Movie(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return image;
    }

    public void setEmail(String email) {
        this.image = email;
    }
}

