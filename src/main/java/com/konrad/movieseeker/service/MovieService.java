package com.konrad.movieseeker.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konrad.movieseeker.model.Actor;
import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.model.Review;
import com.konrad.movieseeker.model.SearchResult;
import com.konrad.movieseeker.util.AppConfig;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The MovieService class provides methods to interact with the Movie Database API (TMDb).
 * It encapsulates the logic for fetching movie data, searching, discovering, retrieving details, reviews,
 * and parsing the JSON responses into domain objects.
 */
public class MovieService {

    private static final String API_KEY = AppConfig.getApiKey();
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=";
    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY;
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/";

    // Simple in-memory cache to store movie details
    private final Map<Integer, Movie> movieCache = new ConcurrentHashMap<>();
    private static final int CACHE_LIMIT = 100;

    /**
     * Searches for movies based on the provided query string and page number.
     * This method uses the /search/movie endpoint which sorts by relevance.
     *
     * @param query The search query string.
     * @param page  The page number.
     * @return A {@link SearchResult} object.
     */
    public SearchResult searchMovies(String query, int page) {
        String urlString = SEARCH_URL + query.replace(" ", "%20") + "&page=" + page;
        return fetchMoviesFromUrl(urlString, page);
    }

    /**
     * Discovers movies based on filters and sort options using the Discover API.
     * This guarantees consistent sorting across pages.
     *
     * @param genreId   The genre ID to filter by.
     * @param sortBy    The field to sort by.
     * @param sortOrder The sort order ("asc" or "desc").
     * @param page      The page number.
     * @return A {@link SearchResult} object.
     */
    public SearchResult discoverMovies(String genreId, String sortBy, String sortOrder, int page) {
        StringBuilder urlBuilder = new StringBuilder(DISCOVER_URL);
        urlBuilder.append("&page=").append(page);

        // Apply Genre Filter
        if (genreId != null && !genreId.isEmpty()) {
            urlBuilder.append("&with_genres=").append(genreId);
        }

        // Apply Sorting
        // Default to popularity if no sortBy is provided
        String apiSortBy = "popularity";
        if (sortBy != null && !sortBy.isEmpty()) {
            apiSortBy = mapSortParam(sortBy);
        }

        // Ensure sortOrder is respected even for default popularity
        String order = (sortOrder != null && !sortOrder.isEmpty()) ? sortOrder : "desc";
        urlBuilder.append("&sort_by=").append(apiSortBy).append(".").append(order);

        return fetchMoviesFromUrl(urlBuilder.toString(), page);
    }

    private String mapSortParam(String sortBy) {
        switch (sortBy) {
            case "release_date": return "primary_release_date";
            case "vote_average": return "vote_average";
            case "vote_count": return "vote_count";
            default: return "popularity";
        }
    }

    private SearchResult fetchMoviesFromUrl(String urlString, int page) {
        List<Movie> movies = new ArrayList<>();
        int totalPages = 0;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return new SearchResult(new ArrayList<>(), 0, 1);
            }

            // Enforced UTF-8 encoding
            InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray results = jsonResponse.getAsJsonArray("results");

            if (jsonResponse.has("total_pages")) {
                totalPages = jsonResponse.get("total_pages").getAsInt();
            }

            for (int i = 0; i < results.size(); i++) {
                JsonObject movieJson = results.get(i).getAsJsonObject();
                Movie movie = createMovie(movieJson);

                // Parse genre_ids for search results to allow filtering
                if (movieJson.has("genre_ids")) {
                    JsonArray ids = movieJson.getAsJsonArray("genre_ids");
                    List<Integer> genreIds = new ArrayList<>();
                    for (JsonElement id : ids) {
                        genreIds.add(id.getAsInt());
                    }
                    movie.setGenre_ids(genreIds);
                }

                movies.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SearchResult(movies, totalPages, page);
    }

    /**
     * Retrieves details of a specific movie identified by its ID.
     * Checks the cache first; if not found, fetches from the API and caches the result.
     *
     * @param movieId The unique identifier of the movie.
     * @return A {@link Movie} object populated with all details, or null if an error occurs.
     */
    public Movie getMovieDetails(int movieId) {
        // Check cache first
        if (movieCache.containsKey(movieId)) {
            System.out.println("Fetching movie details from CACHE for ID: " + movieId);
            return movieCache.get(movieId);
        }

        try {
            System.out.println("Fetching movie details from API for ID: " + movieId);
            // Using append_to_response to get videos, credits, and recommendations in a single HTTP request
            URL url = new URL(MOVIE_DETAILS_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=videos,credits,recommendations");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Enforced UTF-8 encoding
            InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
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

            // Parse Videos to find a YouTube Trailer
            if (movieJson.has("videos") && !movieJson.get("videos").isJsonNull()) {
                JsonObject videosObj = movieJson.getAsJsonObject("videos");
                JsonArray results = videosObj.getAsJsonArray("results");
                for (JsonElement element : results) {
                    JsonObject video = element.getAsJsonObject();
                    String site = getAsString(video, "site");
                    String type = getAsString(video, "type");
                    if ("YouTube".equals(site) && "Trailer".equals(type)) {
                        movie.setYoutubeKey(getAsString(video, "key"));
                        break;
                    }
                }
            }

            // Parse Cast (Credits) - Limit to top 6
            if (movieJson.has("credits") && !movieJson.get("credits").isJsonNull()) {
                JsonObject creditsObj = movieJson.getAsJsonObject("credits");
                JsonArray castArray = creditsObj.getAsJsonArray("cast");
                List<Actor> castList = new ArrayList<>();
                int limit = Math.min(castArray.size(), 6);
                for (int i = 0; i < limit; i++) {
                    JsonObject castJson = castArray.get(i).getAsJsonObject();
                    Actor actor = new Actor();
                    actor.setName(getAsString(castJson, "name"));
                    actor.setCharacter(getAsString(castJson, "character"));
                    actor.setProfile_path(getAsString(castJson, "profile_path"));
                    castList.add(actor);
                }
                movie.setCast(castList);
            }

            // Parse Recommendations - Limit to top 6
            if (movieJson.has("recommendations") && !movieJson.get("recommendations").isJsonNull()) {
                JsonObject recsObj = movieJson.getAsJsonObject("recommendations");
                JsonArray results = recsObj.getAsJsonArray("results");
                List<Movie> recList = new ArrayList<>();
                int limit = Math.min(results.size(), 6);
                for (int i = 0; i < limit; i++) {
                    JsonObject recJson = results.get(i).getAsJsonObject();
                    Movie rec = new Movie();
                    rec.setId(recJson.get("id").getAsInt());
                    rec.setTitle(getAsString(recJson, "title"));
                    rec.setPoster_path(getAsString(recJson, "poster_path"));
                    recList.add(rec);
                }
                movie.setRecommendations(recList);
            }

            // Fetch Reviews separately
            List<Review> reviews = getMovieReviews(movieId);
            movie.setReviews(reviews);

            // Store in cache with size limit check
            if (movie != null) {
                if (movieCache.size() >= CACHE_LIMIT) {
                    System.out.println("Cache limit reached. Clearing cache.");
                    movieCache.clear();
                }
                movieCache.put(movieId, movie);
            }

            return movie;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to create a basic Movie object from a JSON object.
     * Maps common fields like ID, title, overview, release date, etc.
     *
     * @param movieJson The JsonObject containing movie data.
     * @return A populated {@link Movie} object.
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
     * Fetches reviews for a specific movie.
     *
     * @param movieId The ID of the movie.
     * @return A list of {@link Review} objects.
     */
    public List<Review> getMovieReviews(int movieId) {
        List<Review> reviews = new ArrayList<>();
        try {
            URL url = new URL(MOVIE_REVIEWS_URL + movieId + "/reviews?api_key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Enforced UTF-8 encoding
            InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
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
     * Safely retrieves a string member from a JsonObject.
     *
     * @param obj        The parent JsonObject.
     * @param memberName The key to look up.
     * @return The string value or an empty string if null/missing.
     */
    private String getAsString(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsString() : "";
    }

    /**
     * Safely retrieves an int member from a JsonObject.
     *
     * @param obj        The parent JsonObject.
     * @param memberName The key to look up.
     * @return The int value or 0 if null/missing.
     */
    private int getAsInt(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsInt() : 0;
    }

    /**
     * Safely retrieves a double member from a JsonObject.
     *
     * @param obj        The parent JsonObject.
     * @param memberName The key to look up.
     * @return The double value or 0.0 if null/missing.
     */
    private double getAsDouble(JsonObject obj, String memberName) {
        JsonElement elem = obj.get(memberName);
        return elem != null && !elem.isJsonNull() ? elem.getAsDouble() : 0.0;
    }
}