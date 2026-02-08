package com.konrad.movieseeker;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/**
 * Servlet implementation class MovieSearchServlet.
 * <p>
 * This servlet handles the search functionality for movies. It processes GET requests
 * containing search queries, pagination parameters, and filter options (genre, sort order).
 * It delegates the actual data fetching to {@link MovieService} and forwards the results
 * to the {@code index.jsp} view.
 * </p>
 */
@WebServlet("/search")
public class MovieSearchServlet extends HttpServlet {
    private final MovieService movieService = new MovieService();

    /**
     * Handles the HTTP GET method.
     * Retrieves search parameters, fetches paginated movie results, applies in-memory filtering/sorting
     * (for the current page), and sets attributes for the view.
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String genre = req.getParameter("genre");
        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder") != null ? "asc" : "desc";

        int page = 1;
        if (req.getParameter("page") != null && !req.getParameter("page").isEmpty()) {
            try {
                page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Use Service to fetch data with pagination
        SearchResult searchResult = movieService.searchMovies(query, page);
        List<Movie> movies = searchResult.getMovies();

        // In-memory filtering (Note: filtering happens on the current page of results returned by API)
        if (genre != null && !genre.isEmpty()) {
            int genreId = Integer.parseInt(genre);
            movies.removeIf(movie -> movie.getGenre_ids() == null || !movie.getGenre_ids().contains(genreId));
        }

        // In-memory sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Movie> comparator;
            switch (sortBy) {
                case "popularity":
                    comparator = Comparator.comparingDouble(Movie::getPopularity);
                    break;
                case "release_date":
                    comparator = Comparator.comparing(Movie::getRelease_date);
                    break;
                case "vote_average":
                    comparator = Comparator.comparingDouble(Movie::getVote_average);
                    break;
                case "vote_count":
                    comparator = Comparator.comparingInt(Movie::getVote_count);
                    break;
                default:
                    comparator = Comparator.comparing(Movie::getTitle);
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
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", searchResult.getTotalPages());

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    /**
     * Helper method to generate a map of genre IDs to genre names.
     * This is used to populate the genre dropdown in the UI and map IDs in results.
     *
     * @return A map containing Genre ID as Key and Genre Name as Value.
     */
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