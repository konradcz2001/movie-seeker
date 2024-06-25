package com.konrad.movieseeker;


import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/suggestions")
public class MovieSuggestionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MovieService movieService;

    public MovieSuggestionsServlet() {
        this.movieService = new MovieService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        List<Movie> movies = movieService.searchMovies(query);
        Gson gson = new Gson();
        String json = gson.toJson(movies);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
