
/**
 * Sets a cookie with the specified name, value, and expiration days.
 *
 * @param {string} name - The name of the cookie.
 * @param {string} value - The value to be stored in the cookie.
 * @param {number} days - The number of days until the cookie expires.
 */
function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

/**
 * Retrieves the value of a cookie by its name.
 *
 * @param {string} name - The name of the cookie to retrieve.
 * @returns {string|null} The value of the cookie if found, or null if the cookie does not exist.
 */
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

/**
 * Function to erase a cookie by setting its Max-Age to a negative value, effectively deleting it.
 *
 * @param {string} name - The name of the cookie to be erased.
 */
function eraseCookie(name) {
    document.cookie = name + '=; Max-Age=-99999999;';
}

/**
 * Toggles the theme between dark mode and light mode.
 * Updates the body class to reflect the theme change, sets a cookie with the theme value,
 * and updates the theme toggle switch accordingly.
 */
function toggleTheme() {
    const isDarkMode = document.body.classList.toggle('dark-mode');
    setCookie('theme', isDarkMode ? 'dark' : 'light', 365);
    document.getElementById('theme-toggle').checked = isDarkMode;
}


/**
 * Arrow function that listens for the DOMContentLoaded event and checks for a cookie named 'theme'.
 * If the 'theme' cookie is set to 'dark', it adds the 'dark-mode' class to the body element
 * and checks the theme-toggle checkbox.
 */
document.addEventListener('DOMContentLoaded', () => {
    const theme = getCookie('theme');
    if (theme === 'dark') {
        document.body.classList.add('dark-mode');
        document.getElementById('theme-toggle').checked = true;
    }
});
