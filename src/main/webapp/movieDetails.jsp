<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie Details</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script src="js/theme.js" defer></script>
</head>
<body>
<div class="container">
    <div class="header-container">
        <h1>Movie Details</h1>
        <button onclick="window.location.href='index.jsp'">Back to Search</button>
        <div class="theme-toggle">
            <label for="theme">Dark Theme:</label>
            <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
        </div>
    </div>
    <div class="movie-details-container">
        <c:if test="${movie.poster_path ne null and movie.poster_path ne ''}">
            <img class="movie-poster" src="https://image.tmdb.org/t/p/w500${movie.poster_path}" alt="${movie.title}">
        </c:if>
        <div class="movie-info">
            <h2>${movie.title}</h2>
            <p><strong>Overview:</strong> ${movie.overview}</p>
            <p><strong>Genres:</strong>
                <c:forEach var="genre" items="${movie.genres}" varStatus="status">
                    ${genre.name}
                    <c:if test="${not status.last}">, </c:if>
                </c:forEach>
            </p>
            <p><strong>Release Date:</strong> ${movie.release_date}</p>
            <p><strong>Original Language:</strong> ${movie.original_language}</p>
            <p><strong>Popularity:</strong> ${movie.popularity}</p>
            <p><strong>Vote Average:</strong> ${movie.vote_average}</p>
            <p><strong>Vote Count:</strong> ${movie.vote_count}</p>
        </div>
    </div>

    <c:if test="${not empty movie.youtubeKey}">
        <div style="text-align: center; margin: 20px 0;">
            <iframe width="100%" height="450" style="max-width: 800px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);"
                    src="https://www.youtube.com/embed/${movie.youtubeKey}"
                    title="YouTube video player" frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen>
            </iframe>
        </div>
    </c:if>

    <c:if test="${not empty movie.cast}">
        <div class="cast-container">
            <h2>Top Cast</h2>
            <div class="cast-list">
                <c:forEach var="actor" items="${movie.cast}">
                    <div class="actor-card">
                        <c:choose>
                            <c:when test="${not empty actor.profile_path}">
                                <img src="https://image.tmdb.org/t/p/w200${actor.profile_path}" alt="${actor.name}">
                            </c:when>
                            <c:otherwise>
                                <div class="no-photo">No Image</div>
                            </c:otherwise>
                        </c:choose>
                        <p><strong>${actor.name}</strong></p>
                        <p class="character">${actor.character}</p>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <div class="reviews-container">
        <h2>Reviews</h2>
        <c:if test="${empty movie.reviews}">
            <p class="no-reviews">No reviews available.</p>
        </c:if>
        <c:forEach var="review" items="${movie.reviews}">
            <div class="review">
                <p><strong>Author:</strong> ${review.author}</p>
                <p><strong>Rating:</strong> ${review.rating}</p>
                <fmt:parseDate value="${review.updatedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate"/>
                <p><strong>Updated At:</strong>
                    <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd"/>
                </p>
                <p>${review.content}</p>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>