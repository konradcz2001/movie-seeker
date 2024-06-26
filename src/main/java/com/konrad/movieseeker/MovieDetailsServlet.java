package com.konrad.movieseeker;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/movieDetails")
public class MovieDetailsServlet extends HttpServlet {

    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int movieId = Integer.parseInt(request.getParameter("id"));
        Movie movie = movieService.getMovieDetails(movieId);
        request.setAttribute("movie", movie);
        request.getRequestDispatcher("/movieDetails.jsp").forward(request, response);
    }
}
