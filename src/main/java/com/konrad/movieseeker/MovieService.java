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


/**
 * The MovieService class provides methods to interact with the Movie Database API.
 * It allows searching for movies based on a query string, retrieving details of a specific movie including trailers,
 * fetching movie reviews, and creating Movie objects from JSON data.
 * The class handles HTTP connections to the API endpoints and parses JSON responses to populate Movie and Review objects.
 */
public class MovieService {
    private static final String API_KEY = ApiKey.apiKey;
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=";
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/";


    /**
     * Searches for movies based on the provided query string.
     *
     * @param query The search query string to look for movies
     * @return A list of Movie objects representing the search results
     */
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
                Movie movie = createMovie(movieJson);
                movies.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Retrieves details of a movie based on the provided movieId.
     * This method fetches information about the movie, including its genres, YouTube trailer key, and reviews.
     *
     * @param movieId The unique identifier of the movie for which to retrieve details
     * @return A Movie object representing the details of the movie, including genres, trailer key, and reviews
     * or null if an error occurs during the retrieval process
     */
    public Movie getMovieDetails(int movieId) {
        try {
            // Using append_to_response to get videos in the same request
            URL url = new URL(MOVIE_DETAILS_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=videos");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject movieJson = JsonParser.parseReader(reader).getAsJsonObject();
            Movie movie = createMovie(movieJson);

            // Parse Genres
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

            // Parse Videos to find YouTube Trailer
            if (movieJson.has("videos") && !movieJson.get("videos").isJsonNull()) {
                JsonObject videosObj = movieJson.getAsJsonObject("videos");
                JsonArray results = videosObj.getAsJsonArray("results");
                for (JsonElement element : results) {
                    JsonObject video = element.getAsJsonObject();
                    String site = getAsString(video, "site");
                    String type = getAsString(video, "type");

                    if ("YouTube".equals(site) && "Trailer".equals(type)) {
                        movie.setYoutubeKey(getAsString(video, "key"));
                        break; // Stop after finding the first trailer
                    }
                }
            }

            // Fetch Reviews (kept separate as per original logic structure, though could be appended too)
            List<Review> reviews = getMovieReviews(movieId);
            movie.setReviews(reviews);

            return movie;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a Movie object based on the provided JsonObject representing a movie.
     *
     * @param movieJson The JsonObject containing the movie information
     * @return A Movie object with attributes set based on the values in the JsonObject
     */
    private Movie createMovie(JsonObject movieJson) {
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
        return movie;
    }

    /**
     * Retrieves the reviews for a specific movie identified by the given movieId.
     *
     * @param movieId The unique identifier of the movie for which to retrieve reviews
     * @return A list of Review objects containing information about each review, including author, content, rating, and last update date
     */
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


    /**
     * Retrieves the value associated with the specified member name from the given JsonObject as a string.
     * If the member does not exist or is null, returns an empty string.
     *
     * @param obj The JsonObject from which to retrieve the value
     * @param memberName The name of the member whose value is to be retrieved
     * @return The string value associated with the member name, or an empty string if the member is null or does not exist
     */
    private String getAsString(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsString() : "";
    }


    /**
     * Retrieves the value associated with the specified member name from the given JsonObject as an integer.
     * If the member does not exist or is null, returns 0.
     *
     * @param obj The JsonObject from which to retrieve the value
     * @param memberName The name of the member whose value is to be retrieved
     * @return The integer value associated with the member name, or 0 if the member is null or does not exist
     */
    private int getAsInt(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsInt() : 0;
    }

    /**
     * Retrieves the value associated with the specified member name from the given JsonObject as a double.
     * If the member does not exist or is null, returns 0.0.
     *
     * @param obj The JsonObject from which to retrieve the value
     * @param memberName The name of the member whose value is to be retrieved
     * @return The double value associated with the member name, or 0.0 if the member is null or does not exist
     */
    private double getAsDouble(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsDouble() : 0.0;
    }
}