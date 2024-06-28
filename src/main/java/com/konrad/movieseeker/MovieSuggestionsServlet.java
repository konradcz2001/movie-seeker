package com.konrad.movieseeker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@WebServlet("/suggestions")
public class MovieSuggestionsServlet extends HttpServlet {
    private static final String API_KEY = ApiKey.apiKey;


    /**
     * Receives a HTTP GET request with a query parameter, encodes the query using UTF-8 charset,
     * constructs an API URL with the encoded query and API key, makes a GET request to the API URL,
     * reads the response as a JSON object, extracts the 'results' array from the response,
     * sets the response content type to 'application/json', and writes the 'results' array as a string to the response.
     *
     * @param req the HttpServletRequest object representing the request
     * @param resp the HttpServletResponse object representing the response
     * @throws ServletException if the servlet encounters difficulty
     * @throws IOException if an input or output error occurs while the servlet is handling the GET request
     */
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
