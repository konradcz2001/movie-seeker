<!DOCTYPE html>
<html>
<head>
    <title>Movie Search</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <script>
        document.addEventListener('DOMContentLoaded', (event) => {
            document.addEventListener('click', function(e) {
                const suggestions = document.getElementById("suggestions");
                if (e.target.closest("#suggestions") === null && e.target.closest("#query") === null) {
                    suggestions.style.display = 'none';
                }
            });
        });

        const genreMap = {
            28: "Action",
            12: "Adventure",
            16: "Animation",
            35: "Comedy",
            80: "Crime",
            99: "Documentary",
            18: "Drama",
            10751: "Family",
            14: "Fantasy",
            36: "History",
            27: "Horror",
            10402: "Music",
            9648: "Mystery",
            10749: "Romance",
            878: "Science Fiction",
            10770: "TV Movie",
            53: "Thriller",
            10752: "War",
            37: "Western"
        };

        function searchMovies(event) {
            event.preventDefault();
            const query = document.getElementById("query").value;
            const genre = document.getElementById("genre").value;
            const sortBy = document.getElementById("sort-by").value;
            const sortOrder = document.getElementById("sort-order").checked ? "asc" : "desc";
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        const response = JSON.parse(xhr.responseText);
                        let movies = response.results;

                        if (genre !== "") {
                            movies = movies.filter(movie => movie.genre_ids.includes(parseInt(genre)));
                        }

                        if (sortBy) {
                            movies.sort((a, b) => {
                                let aValue, bValue;

                                if (sortBy === "popularity") {
                                    aValue = a.popularity;
                                    bValue = b.popularity;
                                } else if (sortBy === "release_date") {
                                    aValue = new Date(a.release_date);
                                    bValue = new Date(b.release_date);
                                } else if (sortBy === "vote_average") {
                                    aValue = a.vote_average;
                                    bValue = b.vote_average;
                                } else if (sortBy === "vote_count") {
                                    aValue = a.vote_count;
                                    bValue = b.vote_count;
                                }

                                if (sortOrder === "asc") {
                                    return aValue > bValue ? 1 : -1;
                                } else {
                                    return aValue < bValue ? 1 : -1;
                                }
                            });
                        }

                        let moviesHtml = "";
                        for (let i = 0; i < movies.length; i++) {
                            let genres = movies[i].genre_ids.map(id => genreMap[id]).join(', ');
                            moviesHtml += "<div class='movie' onclick='window.location.href=\"movieDetails.jsp?id=" + movies[i].id + "\"'>" +
                                (movies[i].poster_path ? "<img src='https://image.tmdb.org/t/p/w500" + movies[i].poster_path + "' alt='" + movies[i].title + "'>" : "") +
                                "<div class='movie-details'>" +
                                "<h2>" + movies[i].title + "</h2>" +
                                "<p>" + movies[i].overview + "</p>" +
                                "<p><strong>Genres:</strong> " + genres + "</p>" +
                                "<p><strong>Release Date:</strong> " + movies[i].release_date + "</p>" +
                                "<p><strong>Original Language:</strong> " + movies[i].original_language + "</p>" +
                                "<p><strong>Popularity:</strong> " + movies[i].popularity + "</p>" +
                                "<p><strong>Vote Average:</strong> " + movies[i].vote_average + "</p>" +
                                "<p><strong>Vote Count:</strong> " + movies[i].vote_count + "</p>" +
                                "</div>" +
                                "</div>";
                        }
                        document.getElementById("results").innerHTML = moviesHtml;
                    } catch (e) {
                        console.error("Parsing error:", e);
                    }
                } else if (xhr.readyState == 4) {
                    console.error("Server error:", xhr.statusText);
                }
            };
            xhr.open("GET", "search?query=" + encodeURIComponent(query), true);
            xhr.send();
        }

        function showSuggestions(str) {
            const suggestionsBox = document.getElementById("suggestions");
            if (str.length == 0) {
                suggestionsBox.innerHTML = "";
                suggestionsBox.style.display = 'none';
                return;
            }
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    const suggestions = JSON.parse(xhr.responseText);
                    let uniqueTitles = new Set();
                    let suggestionsHtml = "";
                    for (let i = 0; i < suggestions.length; i++) {
                        if (!uniqueTitles.has(suggestions[i].title)) {
                            uniqueTitles.add(suggestions[i].title);
                            suggestionsHtml += "<p onclick='selectSuggestion(\"" + suggestions[i].title.replace(/"/g, '&quot;') + "\")'>" + suggestions[i].title + "</p>";
                        }
                    }
                    suggestionsBox.innerHTML = suggestionsHtml;
                    suggestionsBox.style.display = suggestionsHtml ? 'block' : 'none';
                    suggestionsBox.style.width = document.querySelector('form').offsetWidth + 'px'; // Adjust width
                }
            };
            xhr.open("GET", "suggestions?query=" + encodeURIComponent(str), true);
            xhr.send();
        }

        function selectSuggestion(title) {
            document.getElementById("query").value = title;
            document.getElementById("suggestions").style.display = 'none';
        }
    </script>
</head>
<body>
<h1>Movie Search</h1>
<form onsubmit="searchMovies(event)">
    <input type="text" id="query" name="query" placeholder="Enter movie title" onkeyup="showSuggestions(this.value)">
    <select id="genre" name="genre">
        <option value="">All Genres</option>
        <option value="28">Action</option>
        <option value="12">Adventure</option>
        <option value="16">Animation</option>
        <option value="35">Comedy</option>
        <option value="80">Crime</option>
        <option value="99">Documentary</option>
        <option value="18">Drama</option>
        <option value="10751">Family</option>
        <option value="14">Fantasy</option>
        <option value="36">History</option>
        <option value="27">Horror</option>
        <option value="10402">Music</option>
        <option value="9648">Mystery</option>
        <option value="10749">Romance</option>
        <option value="878">Science Fiction</option>
        <option value="10770">TV Movie</option>
        <option value="53">Thriller</option>
        <option value="10752">War</option>
        <option value="37">Western</option>
    </select>
    <select id="sort-by" name="sort-by">
        <option value="">Sort By</option>
        <option value="popularity">Popularity</option>
        <option value="release_date">Release Date</option>
        <option value="vote_average">Vote Average</option>
        <option value="vote_count">Vote Count</option>
    </select>
    <label for="sort-order">Ascending</label>
    <input type="checkbox" id="sort-order" name="sort-order">
    <input type="submit" value="Search">
    <div id="suggestions"></div>
</form>
<div id="results"></div>
</body>
</html>
