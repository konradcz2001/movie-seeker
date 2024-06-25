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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;

@WebServlet("/suggestions")
public class MovieSuggestionsServlet extends HttpServlet {
    private static final String API_KEY = "bb654568604492b2afe0260d26333c44";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
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

        resp.setContentType("application/json");
        resp.getWriter().write(results.toString());
    }
}
