<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie Seeker</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="${pageContext.request.contextPath}/js/theme.js" defer></script>
    <script src="${pageContext.request.contextPath}/js/suggestions.js" defer></script>
</head>
<body>

<header class="app-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">
            🎬 Movie<span>Seeker</span>
        </a>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/watchlist.jsp" class="nav-link">My Watchlist</a>
            <div class="theme-toggle">
                <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
                <label for="theme-toggle" class="theme-label" title="Toggle Dark Mode">🌙</label>
            </div>
        </nav>
    </div>
</header>

<main class="container">
    <section class="search-section">
        <form action="search" method="get" class="search-form">
            <div class="search-group">
                <input type="text" id="query" name="query" placeholder="Search for movies..." onkeyup="showSuggestions(this.value)" value="${query}" autocomplete="off">
                <div id="suggestions"></div>
            </div>

            <div class="filters-group">
                <select id="genre" name="genre">
                    <option value="">All Genres</option>
                    <c:forEach var="entry" items="${genreMap}">
                        <option value="${entry.key}" <c:if test="${entry.key == selectedGenre}">selected</c:if>>${entry.value}</option>
                    </c:forEach>
                </select>

                <select id="sort-by" name="sortBy">
                    <option value="popularity" <c:if test="${selectedSortBy == 'popularity'}">selected</c:if>>Popularity</option>
                    <option value="release_date" <c:if test="${selectedSortBy == 'release_date'}">selected</c:if>>Release Date</option>
                    <option value="vote_average" <c:if test="${selectedSortBy == 'vote_average'}">selected</c:if>>Rating</option>
                    <option value="vote_count" <c:if test="${selectedSortBy == 'vote_count'}">selected</c:if>>Vote Count</option>
                </select>

                <input type="hidden" id="sort-order" name="sortOrder" value="${not empty selectedSortOrder ? selectedSortOrder : 'desc'}">

                <button type="button" id="sort-order-btn" class="sort-toggle" onclick="toggleSortOrder()" title="Toggle Sort Order">
                    <c:choose>
                        <c:when test="${selectedSortOrder == 'asc'}">⬆️</c:when>
                        <c:otherwise>⬇️</c:otherwise>
                    </c:choose>
                </button>
            </div>

            <button type="submit" class="btn btn-primary">Search</button>
        </form>
    </section>

    <section id="results" class="movies-grid">
        <c:if test="${not empty movies}">
            <c:forEach var="movie" items="${movies}">
                <article class="movie-card" onclick="window.location.href='movieDetails?id=${movie.id}'">
                    <div class="poster-wrapper">
                        <c:choose>
                            <c:when test="${not empty movie.poster_path}">
                                <img src="https://image.tmdb.org/t/p/w500${movie.poster_path}" alt="${movie.title}" loading="lazy">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/no-poster.png" alt="No Image" loading="lazy">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="movie-info">
                        <h3>${movie.title}</h3>
                        <div class="meta">
                            <span class="year">
                                <c:choose>
                                    <c:when test="${not empty movie.release_date}">
                                        ${movie.release_date.length() >= 4 ? movie.release_date.substring(0, 4) : movie.release_date}
                                    </c:when>
                                    <c:otherwise>N/A</c:otherwise>
                                </c:choose>
                            </span>
                            <span class="rating">⭐ ${movie.vote_average}</span>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </c:if>
        <c:if test="${empty movies and not empty query}">
            <p class="no-results">No movies found matching your criteria.</p>
        </c:if>
    </section>

    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="search?query=${query}&genre=${selectedGenre}&sortBy=${selectedSortBy}&sortOrder=${selectedSortOrder}&page=${currentPage - 1}" class="btn btn-secondary">Previous</a>
            </c:if>
            <span class="page-info">Page ${currentPage} of ${totalPages}</span>
            <c:if test="${currentPage < totalPages}">
                <a href="search?query=${query}&genre=${selectedGenre}&sortBy=${selectedSortBy}&sortOrder=${selectedSortOrder}&page=${currentPage + 1}" class="btn btn-secondary">Next</a>
            </c:if>
        </div>
    </c:if>
</main>

<footer class="app-footer">
    <div class="container">
        <p>&copy; 2026 Movie Seeker by Konrad Czardybon</p>
        <p class="api-credit">This product uses the TMDb API but is not endorsed or certified by TMDb.</p>
    </div>
</footer>

<script>
    function toggleSortOrder() {
        const input = document.getElementById('sort-order');
        const btn = document.getElementById('sort-order-btn');

        if (input.value === 'asc') {
            input.value = 'desc';
            btn.innerHTML = '⬇️';
        } else {
            input.value = 'asc';
            btn.innerHTML = '⬆️';
        }
    }
</script>

</body>
</html>