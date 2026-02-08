/**
 * Manages the watchlist functionality using LocalStorage.
 */

const STORAGE_KEY = 'movie_seeker_watchlist';

/**
 * Retrieves the watchlist IDs from LocalStorage.
 * @returns {Array} Array of movie IDs.
 */
function getWatchlist() {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
}

/**
 * Toggles a movie in the watchlist.
 * If present, removes it. If absent, adds it.
 * @param {number} movieId - The ID of the movie.
 * @param {HTMLElement} btnElement - The button element triggered.
 */
function toggleWatchlist(movieId, btnElement) {
    let watchlist = getWatchlist();
    const index = watchlist.indexOf(movieId);

    if (index > -1) {
        watchlist.splice(index, 1);
    } else {
        watchlist.push(movieId);
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(watchlist));
    updateButtonState(movieId, btnElement);
}

/**
 * Updates the appearance of the watchlist button based on the movie's presence in the list.
 * @param {number} movieId - The ID of the movie.
 * @param {HTMLElement} btnElement - The button element to update.
 */
function updateButtonState(movieId, btnElement) {
    const watchlist = getWatchlist();
    const isInList = watchlist.includes(movieId);

    if (isInList) {
        btnElement.textContent = "Remove from Watchlist";
        btnElement.classList.add("remove-btn");
        btnElement.classList.remove("add-btn");
    } else {
        btnElement.textContent = "Add to Watchlist";
        btnElement.classList.add("add-btn");
        btnElement.classList.remove("remove-btn");
    }
}

/**
 * Checks if a movie is in the watchlist.
 * @param {number} movieId
 * @returns {boolean}
 */
function isInWatchlist(movieId) {
    const watchlist = getWatchlist();
    return watchlist.includes(movieId);
}