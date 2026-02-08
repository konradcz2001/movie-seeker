package com.konrad.movieseeker.service;

import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        // We use a spy to partially mock the class. We want to test the real parsing logic,
        // but we want to mock the 'makeApiCall' method to avoid real network requests.
        movieService = Mockito.spy(new MovieService());
    }

    @Test
    void testSearchMoviesReturnsCorrectData() throws IOException {
        // Arrange
        String mockJsonResponse = "{"
                + "\"page\": 1,"
                + "\"total_pages\": 5,"
                + "\"results\": ["
                + "  {"
                + "    \"id\": 101,"
                + "    \"title\": \"Test Movie\","
                + "    \"overview\": \"Description\","
                + "    \"release_date\": \"2023-01-01\","
                + "    \"vote_average\": 8.5,"
                + "    \"genre_ids\": [28, 35]"
                + "  }"
                + "]"
                + "}";

        // Mock the protected method makeApiCall to return our JSON string
        doReturn(mockJsonResponse).when(movieService).makeApiCall(anyString());

        // Act
        SearchResult result = movieService.searchMovies("Test", 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCurrentPage());
        assertEquals(5, result.getTotalPages());

        List<Movie> movies = result.getMovies();
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
        assertEquals(101, movies.get(0).getId());
        assertTrue(movies.get(0).getGenre_ids().contains(28));
    }

    @Test
    void testGetMovieDetailsParsesCorrectly() throws IOException {
        // Arrange
        String mockDetailsJson = "{"
                + "\"id\": 555,"
                + "\"title\": \"Epic Movie\","
                + "\"genres\": [{\"id\": 12, \"name\": \"Adventure\"}],"
                + "\"videos\": { \"results\": [] },"
                + "\"credits\": { \"cast\": [] },"
                + "\"recommendations\": { \"results\": [] }"
                + "}";

        // We also need to mock the reviews call which is made separately
        String mockReviewsJson = "{ \"results\": [] }";

        // Mock calls. The service calls makeApiCall twice (once for details, once for reviews)
        // We can differentiate based on arguments or just return specific Jsons for sequential calls if using exact mocks,
        // but since we use 'anyString()', we need to be careful.
        // For simplicity in this unit test, let's assume makeApiCall returns the details JSON first.
        // However, a better approach with Spy is to match the URL.

        doReturn(mockDetailsJson).when(movieService).makeApiCall(org.mockito.ArgumentMatchers.contains("/movie/555?"));
        doReturn(mockReviewsJson).when(movieService).makeApiCall(org.mockito.ArgumentMatchers.contains("/reviews"));

        // Act
        Movie movie = movieService.getMovieDetails(555);

        // Assert
        assertNotNull(movie);
        assertEquals(555, movie.getId());
        assertEquals("Epic Movie", movie.getTitle());
        assertEquals(1, movie.getGenres().size());
        assertEquals("Adventure", movie.getGenres().get(0).getName());
    }

    @Test
    void testDiscoverMoviesBuildsCorrectUrl() throws IOException {
        // Arrange
        String genreId = "28"; // Action
        String sortBy = "vote_average";
        String sortOrder = "asc";
        int page = 2;

        // We mock makeApiCall to return empty valid JSON,
        // because we are primarily interested in the URL passed to it.
        doReturn("{ \"results\": [] }").when(movieService).makeApiCall(anyString());

        // Act
        movieService.discoverMovies(genreId, sortBy, sortOrder, page);

        // Assert
        // Capture the argument passed to makeApiCall
        org.mockito.ArgumentCaptor<String> urlCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(movieService).makeApiCall(urlCaptor.capture());

        String calledUrl = urlCaptor.getValue();

        // Check if URL contains all expected parameters
        assertTrue(calledUrl.contains("with_genres=28"), "URL should contain genre filter");
        assertTrue(calledUrl.contains("sort_by=vote_average.asc"), "URL should contain correct sort parameter");
        assertTrue(calledUrl.contains("page=2"), "URL should contain page number");
    }
}