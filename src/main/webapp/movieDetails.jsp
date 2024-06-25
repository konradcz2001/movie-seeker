<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="com.google.gson.JsonParser" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie Details</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
<h1>Movie Details</h1>
<button onclick="window.location.href='index.jsp'">Back to Search</button>
<%
    String movieId = request.getParameter("id");
    if (movieId != null && !movieId.isEmpty()) {
        String apiKey = "bb654568604492b2afe0260d26333c44"; // Replace with your actual TMDB API key
        String movieDetailsUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        String movieReviewsUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=" + apiKey;
        System.out.println(movieReviewsUrl);

        try {
            // Fetch movie details
            URL url = new URL(movieDetailsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject movie = JsonParser.parseReader(reader).getAsJsonObject();

            String title = movie.get("title").getAsString();
            String overview = movie.get("overview").getAsString();
            String releaseDate = movie.get("release_date").getAsString();
            String originalLanguage = movie.get("original_language").getAsString();
            double popularity = movie.get("popularity").getAsDouble();
            double voteAverage = movie.get("vote_average").getAsDouble();
            int voteCount = movie.get("vote_count").getAsInt();
            String posterPath = movie.has("poster_path") && !movie.get("poster_path").isJsonNull() ? movie.get("poster_path").getAsString() : null;

%>
<div class="movie-details-container">
    <% if (posterPath != null) { %>
    <img class="movie-poster" src="https://image.tmdb.org/t/p/w500<%= posterPath %>" alt="<%= title %>">
    <% } %>
    <div class="movie-info">
        <h2><%= title %></h2>
        <p><strong>Overview:</strong> <%= overview %></p>
        <p><strong>Release Date:</strong> <%= releaseDate %></p>
        <p><strong>Original Language:</strong> <%= originalLanguage %></p>
        <p><strong>Popularity:</strong> <%= popularity %></p>
        <p><strong>Vote Average:</strong> <%= voteAverage %></p>
        <p><strong>Vote Count:</strong> <%= voteCount %></p>
    </div>
</div>
<%
    // Fetch movie reviews
    URL reviewUrl = new URL(movieReviewsUrl);
    HttpURLConnection reviewConn = (HttpURLConnection) reviewUrl.openConnection();
    reviewConn.setRequestMethod("GET");

    InputStreamReader reviewReader = new InputStreamReader(reviewConn.getInputStream());
    JsonObject reviewsObject = JsonParser.parseReader(reviewReader).getAsJsonObject();
    JsonArray reviews = reviewsObject.getAsJsonArray("results");

    if (reviews.size() > 0) {
%>
<h3>Reviews:</h3>
<div class="reviews-container">
    <%
        for (int i = 0; i < reviews.size(); i++) {
            JsonObject review = reviews.get(i).getAsJsonObject();
            String author = review.get("author").getAsString();
            String content = review.get("content").getAsString();
            String updatedAt = review.get("updated_at").getAsString();
            int rating = review.has("author_details") && review.get("author_details").getAsJsonObject().has("rating") && !review.get("author_details").getAsJsonObject().get("rating").isJsonNull() ? review.get("author_details").getAsJsonObject().get("rating").getAsInt() : -1;
    %>
    <div class="review">
        <p><strong>Author:</strong> <%= author %></p>
        <p><strong>Rating:</strong> <%= rating != -1 ? rating : "No rating" %></p>
        <p><strong>Updated At:</strong> <%= updatedAt.split("T")[0] %></p>
        <p><%= content %></p>
    </div>
    <%
        }
    %>
</div>
<%
} else {
%>
<p class="no-reviews">No reviews available.</p>
<%
            }

        } catch (Exception e) {
            out.println("<p>Error retrieving movie details: " + e.getMessage() + "</p>");
        }
    } else {
        out.println("<p>Invalid movie ID.</p>");
    }
%>
</body>
</html>
