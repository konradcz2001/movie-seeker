package com.konrad.movieseeker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieService {
    private static final String API_KEY = "bb654568604492b2afe0260d26333c44";
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=";
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/";

    public List<Movie> searchMovies(String query) {
        List<Movie> movies = new ArrayList<>();
        try {
            URL url = new URL(SEARCH_URL + query.replace(" ", "%20"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray results = jsonResponse.getAsJsonArray("results");

            for (int i = 0; i < results.size(); i++) {
                JsonObject movieJson = results.get(i).getAsJsonObject();
                Movie movie = new Movie();

                movie.setId(movieJson.get("id").getAsInt());
                movie.setTitle(getAsString(movieJson, "title"));
                movie.setOverview(getAsString(movieJson, "overview"));
                movie.setRelease_date(getAsString(movieJson, "release_date"));
                movie.setPoster_path(getAsString(movieJson, "poster_path"));
                movie.setOriginal_language(getAsString(movieJson, "original_language"));
                movie.setPopularity(getAsDouble(movieJson, "popularity"));
                movie.setVote_average(getAsDouble(movieJson, "vote_average"));
                movie.setVote_count(getAsInt(movieJson, "vote_count"));

                movies.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Movie getMovieDetails(int movieId) {
        try {
            URL url = new URL(MOVIE_DETAILS_URL + movieId + "?api_key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject movieJson = JsonParser.parseReader(reader).getAsJsonObject();
            Movie movie = new Movie();

            movie.setId(movieJson.get("id").getAsInt());
            movie.setTitle(getAsString(movieJson, "title"));
            movie.setOverview(getAsString(movieJson, "overview"));
            movie.setRelease_date(getAsString(movieJson, "release_date"));
            movie.setPoster_path(getAsString(movieJson, "poster_path"));
            movie.setOriginal_language(getAsString(movieJson, "original_language"));
            movie.setPopularity(getAsDouble(movieJson, "popularity"));
            movie.setVote_average(getAsDouble(movieJson, "vote_average"));
            movie.setVote_count(getAsInt(movieJson, "vote_count"));

            JsonArray genresArray = movieJson.getAsJsonArray("genres");
            List<Movie.Genre> genres = new ArrayList<>();
            for (JsonElement genreElement : genresArray) {
                JsonObject genreJson = genreElement.getAsJsonObject();
                Movie.Genre genre = new Movie.Genre();
                genre.setId(genreJson.get("id").getAsInt());
                genre.setName(getAsString(genreJson, "name"));
                genres.add(genre);
            }
            movie.setGenres(genres);

            List<Review> reviews = getMovieReviews(movieId);
            movie.setReviews(reviews);

            return movie;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Review> getMovieReviews(int movieId) {
        List<Review> reviews = new ArrayList<>();
        try {
            URL url = new URL(MOVIE_REVIEWS_URL + movieId + "/reviews?api_key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray results = jsonResponse.getAsJsonArray("results");

            for (int i = 0; i < results.size(); i++) {
                JsonObject reviewJson = results.get(i).getAsJsonObject();
                Review review = new Review();

                review.setAuthor(getAsString(reviewJson, "author"));
                review.setContent(getAsString(reviewJson, "content"));
                review.setRating(getAsDouble(reviewJson.get("author_details").getAsJsonObject(), "rating"));
                review.setUpdatedAt(getAsString(reviewJson, "updated_at"));

                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private String getAsString(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsString() : "";
    }

    private int getAsInt(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsInt() : 0;
    }

    private double getAsDouble(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsDouble() : 0.0;
    }
}
