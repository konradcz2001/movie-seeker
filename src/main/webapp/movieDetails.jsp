<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${movie.title} - Movie Seeker</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="${pageContext.request.contextPath}/js/theme.js" defer></script>
    <script src="${pageContext.request.contextPath}/js/watchlist.js" defer></script>
</head>
<body>

<header class="app-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">
            🎬 Movie<span>Seeker</span>
        </a>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/" class="nav-link">Back to Search</a>
            <a href="${pageContext.request.contextPath}/watchlist.jsp" class="nav-link">My Watchlist</a>
            <div class="theme-toggle">
                <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
                <label for="theme-toggle" class="theme-label" title="Toggle Dark Mode">🌙</label>
            </div>
        </nav>
    </div>
</header>

<main class="container details-page">
    <section class="movie-hero">
        <div class="hero-poster">
            <c:choose>
                <c:when test="${not empty movie.poster_path}">
                    <img src="https://image.tmdb.org/t/p/w500${movie.poster_path}" alt="${movie.title}">
                </c:when>
                <c:otherwise>
                    <div class="no-poster">No Image</div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="hero-content">
            <h1>${movie.title}</h1>
            <div class="hero-meta">
                <span class="release-date">${movie.release_date}</span>
                <span class="runtime">Language: ${movie.original_language.toUpperCase()}</span>
                <span class="score">⭐ ${movie.vote_average} (${movie.vote_count} votes)</span>
            </div>

            <div class="genres">
                <c:forEach var="genre" items="${movie.genres}">
                    <span class="genre-tag">${genre.name}</span>
                </c:forEach>
            </div>

            <div class="actions">
                <button id="watchlist-toggle" class="btn btn-outline" onclick="toggleWatchlist(${movie.id}, this)">
                    Add to Watchlist
                </button>
            </div>

            <div class="overview">
                <h3>Overview</h3>
                <p>${movie.overview}</p>
            </div>
        </div>
    </section>

    <c:if test="${not empty movie.youtubeKey}">
        <section class="video-section">
            <h3>Trailer</h3>
            <div class="video-container">
                <iframe src="https://www.youtube.com/embed/${movie.youtubeKey}"
                        title="YouTube video player" frameborder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen>
                </iframe>
            </div>
        </section>
    </c:if>

    <c:if test="${not empty movie.cast}">
        <section class="cast-section">
            <h3>Top Cast</h3>
            <div class="scrolling-wrapper">
                <c:forEach var="actor" items="${movie.cast}">
                    <div class="card actor-card">
                        <c:choose>
                            <c:when test="${not empty actor.profile_path}">
                                <img src="https://image.tmdb.org/t/p/w200${actor.profile_path}" alt="${actor.name}" loading="lazy">
                            </c:when>
                            <c:otherwise>
                                <div class="no-photo">No Image</div>
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <p class="name">${actor.name}</p>
                            <p class="character">${actor.character}</p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <c:if test="${not empty movie.recommendations}">
        <section class="recommendations-section">
            <h3>You Might Also Like</h3>
            <div class="scrolling-wrapper">
                <c:forEach var="rec" items="${movie.recommendations}">
                    <div class="card rec-card" onclick="window.location.href='movieDetails?id=${rec.id}'">
                        <c:choose>
                            <c:when test="${not empty rec.poster_path}">
                                <img src="https://image.tmdb.org/t/p/w200${rec.poster_path}" alt="${rec.title}" loading="lazy">
                            </c:when>
                            <c:otherwise>
                                <div class="no-photo">No Image</div>
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <p class="title">${rec.title}</p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <section class="reviews-section">
        <h3>Reviews</h3>
        <c:if test="${empty movie.reviews}">
            <p>No reviews available for this movie.</p>
        </c:if>
        <div class="reviews-list">
            <c:forEach var="review" items="${movie.reviews}">
                <div class="review-card">
                    <div class="review-header">
                        <span class="author">A review by <strong>${review.author}</strong></span>
                        <c:if test="${review.rating > 0}">
                            <span class="rating-badge">★ ${review.rating}</span>
                        </c:if>
                    </div>
                    <div class="review-body">
                        <p>${review.content}</p>
                    </div>
                    <div class="review-footer">
                        <fmt:parseDate value="${review.updatedAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate"/>
                        <small>Updated: <fmt:formatDate value="${parsedDate}" pattern="MMM dd, yyyy"/></small>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</main>

<footer class="app-footer">
    <div class="container">
        <p>&copy; 2026 Movie Seeker by Konrad Czardybon</p>
        <p class="api-credit">This product uses the TMDb API but is not endorsed or certified by TMDb.</p>
    </div>
</footer>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const btn = document.getElementById('watchlist-toggle');
        if(btn) {
            updateButtonState(${movie.id}, btn);
        }
    });
</script>
</body>
</html>