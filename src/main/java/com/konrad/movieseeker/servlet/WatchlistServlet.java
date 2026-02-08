package com.konrad.movieseeker.servlet;

import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet handling the display of the Watchlist.
 * It accepts a comma-separated list of movie IDs, fetches their details using MovieService,
 * and forwards the list of Movie objects to the JSP.
 */
@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {

    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idsParam = req.getParameter("ids");
        List<Movie> movies = new ArrayList<>();

        if (idsParam != null && !idsParam.isEmpty()) {
            String[] ids = idsParam.split(",");
            for (String idStr : ids) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    // Reuse getMovieDetails which uses caching to fetch movie data
                    Movie movie = movieService.getMovieDetails(id);
                    if (movie != null) {
                        movies.add(movie);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }

        req.setAttribute("movies", movies);
        req.getRequestDispatcher("/watchlist.jsp").forward(req, resp);
    }
}