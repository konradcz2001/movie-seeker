package com.konrad.movieseeker.servlet;

import com.konrad.movieseeker.model.Movie;
import com.konrad.movieseeker.model.SearchResult;
import com.konrad.movieseeker.service.MovieService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieSearchServletTest {

    private MovieSearchServlet servlet;

    @Mock
    private MovieService movieService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new MovieSearchServlet();
        // Inject the mock service
        servlet.setMovieService(movieService);
    }

    @Test
    void testDoGetWithSearchQuery() throws ServletException, IOException {
        // Arrange
        String query = "Batman";
        when(request.getParameter("query")).thenReturn(query);
        when(request.getRequestURI()).thenReturn("/MovieSeeker/search");
        when(request.getContextPath()).thenReturn("/MovieSeeker");

        // Mock service response
        List<Movie> mockMovies = new ArrayList<>();
        mockMovies.add(new Movie());
        SearchResult mockResult = new SearchResult(mockMovies, 2, 1);

        when(movieService.searchMovies(eq(query), anyInt())).thenReturn(mockResult);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        // Verify that the service was called with correct parameters
        verify(movieService).searchMovies(eq(query), eq(1));

        // Verify attributes were set
        verify(request).setAttribute(eq("movies"), eq(mockMovies));
        verify(request).setAttribute(eq("query"), eq(query));
        verify(request).setAttribute(eq("totalPages"), eq(2));

        // Verify forward to JSP
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGetWithoutQueryTriggersDiscover() throws ServletException, IOException {
        // Arrange - No query param (null)
        when(request.getParameter("query")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/MovieSeeker/");
        when(request.getContextPath()).thenReturn("/MovieSeeker");

        // Mock discover response
        SearchResult mockResult = new SearchResult(new ArrayList<>(), 10, 1);
        when(movieService.discoverMovies(any(), any(), anyString(), anyInt())).thenReturn(mockResult);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        // Verify discover was called instead of search
        verify(movieService).discoverMovies(any(), any(), anyString(), eq(1));
        verify(dispatcher).forward(request, response);
    }
}