package com.konrad.movieseeker.servlet;

import com.konrad.movieseeker.model.Movie;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WatchlistServletTest {

    private WatchlistServlet servlet;

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
        servlet = new WatchlistServlet();
        servlet.setMovieService(movieService);
        when(request.getRequestDispatcher("/watchlist.jsp")).thenReturn(dispatcher);
    }

    @Test
    void testDoGetWithValidIds() throws ServletException, IOException {
        // Arrange
        String idsParam = "101, 102";
        when(request.getParameter("ids")).thenReturn(idsParam);

        Movie movie1 = new Movie();
        movie1.setId(101);
        Movie movie2 = new Movie();
        movie2.setId(102);

        when(movieService.getMovieDetails(101)).thenReturn(movie1);
        when(movieService.getMovieDetails(102)).thenReturn(movie2);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(movieService, times(1)).getMovieDetails(101);
        verify(movieService, times(1)).getMovieDetails(102);

        // Capture the argument passed to setAttribute to verify list content
        org.mockito.ArgumentCaptor<List<Movie>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("movies"), captor.capture());

        List<Movie> capturedList = captor.getValue();
        assertEquals(2, capturedList.size());
    }

    @Test
    void testDoGetIgnoresInvalidIds() throws ServletException, IOException {
        // Arrange
        // "101" is valid, "abc" is invalid format, "999" is valid int but returns null movie
        String idsParam = "101, abc, 999";
        when(request.getParameter("ids")).thenReturn(idsParam);

        Movie movie1 = new Movie();
        movie1.setId(101);

        when(movieService.getMovieDetails(101)).thenReturn(movie1);
        when(movieService.getMovieDetails(999)).thenReturn(null); // Movie not found

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(movieService).getMovieDetails(101);
        verify(movieService).getMovieDetails(999);

        // "abc" should cause exception which is caught, so verify logic continues
        org.mockito.ArgumentCaptor<List<Movie>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("movies"), captor.capture());

        List<Movie> capturedList = captor.getValue();
        assertEquals(1, capturedList.size()); // Only movie 101 should be in the list
        assertEquals(101, capturedList.get(0).getId());
    }
}