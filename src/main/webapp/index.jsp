<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie Search</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script src="js/theme.js" defer></script>
    <script src="js/suggestions.js" defer></script>
</head>
<body>
<h1>Movie Seeker</h1>
<div class="header-controls">
    <a href="watchlist.jsp" class="watchlist-btn">My Watchlist</a>
</div>
<div class="theme-toggle">
    <label for="theme">Dark Theme:</label>
    <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
</div>
<form action="search" method="get">
    <input type="text" id="query" name="query" placeholder="Enter movie title" onkeyup="showSuggestions(this.value)" value="${query}">
    <select id="genre" name="genre">
        <option value="">All Genres</option>
        <c:forEach var="entry" items="${genreMap}">
            <option value="${entry.key}" <c:if test="${entry.key == selectedGenre}">selected</c:if>>${entry.value}</option>
        </c:forEach>
    </select>
    <select id="sort-by" name="sortBy">
        <option value="">Sort By</option>
        <option value="popularity" <c:if test="${selectedSortBy == 'popularity'}">selected</c:if>>Popularity</option>
        <option value="release_date" <c:if test="${selectedSortBy == 'release_date'}">selected</c:if>>Release Date</option>
        <option value="vote_average" <c:if test="${selectedSortBy == 'vote_average'}">selected</c:if>>Vote Average</option>
        <option value="vote_count" <c:if test="${selectedSortBy == 'vote_count'}">selected</c:if>>Vote Count</option>
    </select>
    <label for="sort-order">Ascending</label>
    <input type="checkbox" id="sort-order" name="sortOrder" <c:if test="${selectedSortOrder == 'asc'}">checked</c:if>>
    <input type="submit" value="Search">
    <div id="suggestions"></div>
</form>
<div id="results">
    <c:if test="${not empty movies}">
        <c:forEach var="movie" items="${movies}">
            <div class="movie" onclick="window.location.href='movieDetails?id=${movie.id}'">
                <c:if test="${not empty movie.poster_path}">
                    <img src="https://image.tmdb.org/t/p/w500${movie.poster_path}" alt="${movie.title}">
                </c:if>
                <div class="movie-details">
                    <h2>${movie.title}</h2>
                    <p>${movie.overview}</p>
                    <p><strong>Genres:</strong>
                        <c:forEach var="genreId" items="${movie.genre_ids}" varStatus="status">
                            <c:out value="${genreMap[genreId]}"/>
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
        </c:forEach>
    </c:if>
</div>

<c:if test="${totalPages > 1}">
    <div class="pagination-container">
        <c:if test="${currentPage > 1}">
            <a href="search?query=${query}&genre=${selectedGenre}&sortBy=${selectedSortBy}&sortOrder=${selectedSortOrder}&page=${currentPage - 1}" class="pagination-btn">Previous</a>
        </c:if>

        <span class="page-info">Page ${currentPage} of ${totalPages}</span>

        <c:if test="${currentPage < totalPages}">
            <a href="search?query=${query}&genre=${selectedGenre}&sortBy=${selectedSortBy}&sortOrder=${selectedSortOrder}&page=${currentPage + 1}" class="pagination-btn">Next</a>
        </c:if>
    </div>
</c:if>

</body>
</html>