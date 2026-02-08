package com.konrad.movieseeker;

import java.util.List;

/**
 * Represents the result of a movie search operation, encapsulating a list of movies
 * and pagination information.
 * <p>
 * This class is used to transfer data from the service layer to the presentation layer,
 * allowing the view to render both the search results and the pagination controls.
 * </p>
 */
public class SearchResult {

    private List<Movie> movies;
    private int totalPages;
    private int currentPage;

    public SearchResult(List<Movie> movies, int totalPages, int currentPage) {
        this.movies = movies;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}