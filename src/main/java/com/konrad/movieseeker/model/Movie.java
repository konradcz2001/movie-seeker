package com.konrad.movieseeker.model;

import java.util.List;

/**
 * The Movie class represents a movie entity with various attributes such as id, poster path, title, overview, release date,
 * original language, popularity, vote average, vote count, genres, reviews, YouTube trailer key, cast details,
 * and a list of recommended movies.
 * <p>
 * It serves as the main data model mapped from the TMDb API responses.
 * </p>
 */
public class Movie {
    private int id;
    private String poster_path;
    private String title;
    private String overview;
    private String release_date;
    private String original_language;
    private double popularity;
    private double vote_average;
    private int vote_count;
    private String youtubeKey;
    private List<Genre> genres;

    /**
     * A list of genre IDs associated with the movie.
     * This field is primarily populated during search results where full Genre objects are not provided by the API.
     */
    private List<Integer> genre_ids;

    private List<Review> reviews;
    private List<Actor> cast;
    private List<Movie> recommendations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public String getYoutubeKey() {
        return youtubeKey;
    }

    public void setYoutubeKey(String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Actor> getCast() {
        return cast;
    }

    public void setCast(List<Actor> cast) {
        this.cast = cast;
    }

    public List<Movie> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Movie> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Nested static class representing a Genre with an ID and a name.
     */
    public static class Genre {
        private int id;
        private String name;

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
    }
}