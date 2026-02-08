package com.konrad.movieseeker.servlet;

import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.model.SearchResult;
import com.konrad.movieseeker.service.MovieService;
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
 * This servlet acts as the main controller for the application.
 * It maps to both the root context ("") and "/search", ensuring that initialization logic
 * (like loading genres) runs immediately upon application start.
 * </p>
 */
@WebServlet(urlPatterns = {"", "/search"})
public class MovieSearchServlet extends HttpServlet {
    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve parameters
        String query = req.getParameter("query");
        String genre = req.getParameter("genre");
        String sortBy = req.getParameter("sortBy");
        String sortOrderParam = req.getParameter("sortOrder");

        // 1. Resolve Sort Order (Strict check)
        // Default is "desc" unless explicit "asc" or "on" (checkbox) is received.
        String sortOrder = "desc";
        if ("asc".equals(sortOrderParam) || "on".equals(sortOrderParam)) {
            sortOrder = "asc";
        }

        // 2. Resolve Page
        int page = 1;
        if (req.getParameter("page") != null && !req.getParameter("page").isEmpty()) {
            try {
                page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        SearchResult searchResult;
        List<Movie> movies;

        // 3. DECISION LOGIC: Search Mode vs. Discover Mode
        // If 'query' contains actual text, we MUST use Search API (which has pagination limitations regarding sorting).
        // If 'query' is null or empty/whitespace, we use Discover API (which supports perfect global sorting).
        boolean hasQuery = (query != null && !query.trim().isEmpty());

        if (hasQuery) {
            // MODE 1: Text Search (e.g., User typed "Batman")
            // API Limitation: TMDb Search API sorts by "relevance" by default and doesn't support global custom sorting.
            // We fetch the raw page and apply filtering/sorting in-memory ONLY FOR THE CURRENT PAGE.
            searchResult = movieService.searchMovies(query, page);
            movies = searchResult.getMovies();

            // In-memory Filtering
            if (genre != null && !genre.isEmpty()) {
                int genreId = Integer.parseInt(genre);
                movies.removeIf(movie -> movie.getGenre_ids() == null || !movie.getGenre_ids().contains(genreId));
            }

            // In-memory Sorting
            if (sortBy != null && !sortBy.isEmpty()) {
                sortMoviesInMemory(movies, sortBy, sortOrder);
            }

        } else {
            // MODE 2: Discover / Browse (e.g., User selected "Comedy" + "Popularity")
            // We use the Discover API. This delegates sorting/filtering to the TMDb server.
            // This ensures that Page 2 is correctly sorted relative to Page 1.
            searchResult = movieService.discoverMovies(genre, sortBy, sortOrder, page);
            movies = searchResult.getMovies();
        }

        // 4. Set Attributes for JSP
        req.setAttribute("movies", movies);
        req.setAttribute("genreMap", getGenreMap());
        req.setAttribute("query", query); // Pass back query so input field works
        req.setAttribute("selectedGenre", genre);
        req.setAttribute("selectedSortBy", sortBy);
        req.setAttribute("selectedSortOrder", sortOrder);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", searchResult.getTotalPages());

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void sortMoviesInMemory(List<Movie> movies, String sortBy, String sortOrder) {
        Comparator<Movie> comparator;
        switch (sortBy) {
            case "popularity":
                comparator = Comparator.comparingDouble(Movie::getPopularity);
                break;
            case "release_date":
                comparator = Comparator.comparing(Movie::getRelease_date, Comparator.nullsLast(String::compareTo));
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
        if ("desc".equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        movies.sort(comparator);
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