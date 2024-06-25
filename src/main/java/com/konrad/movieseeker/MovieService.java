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
                movie.setOriginalTitle(getAsString(movieJson, "original_title"));
                movie.setOverview(getAsString(movieJson, "overview"));
                movie.setReleaseDate(getAsString(movieJson, "release_date"));
                movie.setPosterPath(getAsString(movieJson, "poster_path"));
                movie.setBackdropPath(getAsString(movieJson, "backdrop_path"));
                movie.setOriginalLanguage(getAsString(movieJson, "original_language"));
                movie.setPopularity(getAsDouble(movieJson, "popularity"));
                movie.setVoteAverage(getAsDouble(movieJson, "vote_average"));
                movie.setVoteCount(getAsInt(movieJson, "vote_count"));

                movies.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
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