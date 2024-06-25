package com.konrad.movieseeker;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;

@WebServlet("/search")
public class MovieSearchServlet extends HttpServlet {
    private static final String API_KEY = "bb654568604492b2afe0260d26333c44";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String genre = req.getParameter("genre");
        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder") != null ? "asc" : "desc";

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String apiUrl = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + encodedQuery;

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Server returned HTTP response code: " + conn.getResponseCode() + " for URL: " + apiUrl);
        }

        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray results = response.getAsJsonArray("results");

        List<Map<String, Object>> movies = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            JsonObject movieJson = results.get(i).getAsJsonObject();
            Map<String, Object> movie = new HashMap<>();
            movie.put("id", movieJson.get("id").getAsInt());
            movie.put("title", getJsonElementAsString(movieJson.get("title")));
            movie.put("overview", getJsonElementAsString(movieJson.get("overview")));
            movie.put("release_date", getJsonElementAsString(movieJson.get("release_date")));
            movie.put("original_language", getJsonElementAsString(movieJson.get("original_language")));
            movie.put("popularity", movieJson.get("popularity").getAsDouble());
            movie.put("vote_average", movieJson.get("vote_average").getAsDouble());
            movie.put("vote_count", movieJson.get("vote_count").getAsInt());
            movie.put("poster_path", movieJson.has("poster_path") && !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : null);
            JsonArray genreIds = movieJson.getAsJsonArray("genre_ids");
            List<Integer> genres = new ArrayList<>();
            for (int j = 0; j < genreIds.size(); j++) {
                genres.add(genreIds.get(j).getAsInt());
            }
            movie.put("genre_ids", genres);
            movies.add(movie);
        }

        if (genre != null && !genre.isEmpty()) {
            int genreId = Integer.parseInt(genre);
            movies.removeIf(movie -> !((List<Integer>) movie.get("genre_ids")).contains(genreId));
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Map<String, Object>> comparator;
            switch (sortBy) {
                case "popularity":
                    comparator = Comparator.comparingDouble(movie -> (Double) movie.get("popularity"));
                    break;
                case "release_date":
                    comparator = Comparator.comparing(movie -> (String) movie.get("release_date"));
                    break;
                case "vote_average":
                    comparator = Comparator.comparingDouble(movie -> (Double) movie.get("vote_average"));
                    break;
                case "vote_count":
                    comparator = Comparator.comparingInt(movie -> (Integer) movie.get("vote_count"));
                    break;
                default:
                    comparator = Comparator.comparing(movie -> (String) movie.get("title"));
            }
            if (sortOrder.equals("desc")) {
                comparator = comparator.reversed();
            }
            movies.sort(comparator);
        }

        req.setAttribute("movies", movies);
        req.setAttribute("genreMap", getGenreMap());
        req.setAttribute("query", query);
        req.setAttribute("selectedGenre", genre);
        req.setAttribute("selectedSortBy", sortBy);
        req.setAttribute("selectedSortOrder", sortOrder);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private String getJsonElementAsString(JsonElement element) {
        return element != null && !element.isJsonNull() ? element.getAsString() : "";
    }

    private Map<Integer, String> getGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();
        genreMap.put(28, "Action");
        genreMap.put(12, "Adventure");
        genreMap.put(16, "Animation");
        genreMap.put(35, "Comedy");
        genreMap.put(80, "Crime");
        genreMap.put(99, "Documentary");
        genreMap.put(18, "Drama");
        genreMap.put(10751, "Family");
        genreMap.put(14, "Fantasy");
        genreMap.put(36, "History");
        genreMap.put(27, "Horror");
        genreMap.put(10402, "Music");
        genreMap.put(9648, "Mystery");
        genreMap.put(10749, "Romance");
        genreMap.put(878, "Science Fiction");
        genreMap.put(10770, "TV Movie");
        genreMap.put(53, "Thriller");
        genreMap.put(10752, "War");
        genreMap.put(37, "Western");
        return genreMap;
    }
}
