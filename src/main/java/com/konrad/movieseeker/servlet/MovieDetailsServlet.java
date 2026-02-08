package com.konrad.movieseeker.servlet;

import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/movieDetails")
public class MovieDetailsServlet extends HttpServlet {

    private final MovieService movieService = new MovieService();

    /**
     * Handles the HTTP GET request to retrieve details of a specific movie based on the provided movie ID.
     * Parses the movie ID from the request parameters, fetches the movie details using the MovieService,
     * sets the retrieved movie object as an attribute in the request, and forwards the request to the 'movieDetails.jsp' page
     * to display the movie details.
     *
     * @param request  the HttpServletRequest object containing the request parameters
     * @param response the HttpServletResponse object for sending the response
     * @throws ServletException if there is a servlet-related issue
     * @throws IOException      if there is an I/O issue
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int movieId = Integer.parseInt(request.getParameter("id"));
        Movie movie = movieService.getMovieDetails(movieId);
        request.setAttribute("movie", movie);
        request.getRequestDispatcher("/movieDetails.jsp").forward(request, response);
    }
}