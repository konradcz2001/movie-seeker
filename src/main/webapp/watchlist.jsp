<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Watchlist</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script src="js/theme.js" defer></script>
    <script src="js/watchlist.js" defer></script>
</head>
<body>
<div class="container">
    <div class="header-container">
        <h1>My Watchlist</h1>
        <div class="header-controls">
            <button onclick="window.location.href='index.jsp'">Back to Search</button>
        </div>
        <div class="theme-toggle">
            <label for="theme">Dark Theme:</label>
            <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
        </div>
    </div>

    <%-- Logic to redirect if no IDs are present but JS might have them --%>
    <div id="loading-msg" style="text-align: center; margin-top: 50px;">
        <c:if test="${empty param.ids and empty movies}">
            <p>Loading your watchlist...</p>
        </c:if>
        <c:if test="${not empty param.ids and empty movies}">
            <p>Your watchlist is empty or movies could not be loaded.</p>
        </c:if>
    </div>

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
                        <p><strong>Release Date:</strong> ${movie.release_date}</p>
                        <p><strong>Vote Average:</strong> ${movie.vote_average}</p>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        // If the 'ids' parameter is missing from the URL, read from LocalStorage and redirect.
        const urlParams = new URLSearchParams(window.location.search);
        if (!urlParams.has('ids')) {
            const watchlist = getWatchlist();
            if (watchlist.length > 0) {
                const idsParam = watchlist.join(',');
                window.location.href = 'watchlist?ids=' + idsParam;
            } else {
                document.getElementById('loading-msg').innerHTML = "<p>Your watchlist is currently empty.</p>";
            }
        }
    });
</script>
</body>
</html>