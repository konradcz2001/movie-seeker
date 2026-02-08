<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Watchlist - Movie Seeker</title>
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
            <div class="theme-toggle">
                <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
                <label for="theme-toggle" class="theme-label" title="Toggle Dark Mode"></label>
            </div>
        </nav>
    </div>
</header>

<main class="container watchlist-page">
    <h1>My Watchlist</h1>

    <div id="loading-msg" class="alert-box">
        <c:if test="${empty param.ids and empty movies}">
            <p>Loading your watchlist...</p>
        </c:if>
        <c:if test="${not empty param.ids and empty movies}">
            <p>Your watchlist is empty.</p>
        </c:if>
    </div>

    <section class="movies-list-vertical">
        <c:if test="${not empty movies}">
            <c:forEach var="movie" items="${movies}">
                <article class="movie-row" onclick="window.location.href='movieDetails?id=${movie.id}'">
                    <div class="poster-thumb">
                        <c:choose>
                            <c:when test="${not empty movie.poster_path}">
                                <img src="https://image.tmdb.org/t/p/w200${movie.poster_path}" alt="${movie.title}">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/no-poster.png" alt="No Image">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="info-col">
                        <h2>${movie.title}</h2>
                        <div class="meta-row">
                            <span class="date">${movie.release_date}</span>
                            <span class="lang">${movie.original_language.toUpperCase()}</span>
                        </div>
                        <div class="genres-row">
                            <c:forEach var="genre" items="${movie.genres}" varStatus="status">
                                <span class="genre-text">${genre.name}</span><c:if test="${not status.last}">, </c:if>
                            </c:forEach>
                        </div>
                        <p class="overview-text">${movie.overview}</p>
                    </div>
                    <div class="stats-col">
                        <div class="stat-item">
                            <span class="label">Score</span>
                            <span class="value">⭐ ${movie.vote_average}</span>
                        </div>
                        <div class="stat-item">
                            <span class="label">Popularity</span>
                            <span class="value">${movie.popularity}</span>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </c:if>
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
        const urlParams = new URLSearchParams(window.location.search);
        if (!urlParams.has('ids')) {
            const watchlist = getWatchlist();
            if (watchlist.length > 0) {
                const idsParam = watchlist.join(',');
                window.location.href = 'watchlist?ids=' + idsParam;
            } else {
                document.getElementById('loading-msg').innerHTML = "<p>Your watchlist is currently empty. Go add some movies!</p>";
            }
        }
    });
</script>
</body>
</html>