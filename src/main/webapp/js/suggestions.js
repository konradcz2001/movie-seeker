
/**
 * Function: showSuggestions
 *
 * Description: Retrieves suggestions based on the input string and displays them in a dropdown list.
 *
 * @param {string} str - The input string used to fetch suggestions.
 * @returns {void}
 */
function showSuggestions(str) {
    if (str.length === 0) {
        document.getElementById("suggestions").innerHTML = "";
        document.getElementById("suggestions").style.display = "none";
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const suggestions = JSON.parse(xhr.responseText);
            let suggestionsHtml = "";
            const uniqueTitles = new Set();
            for (let i = 0; i < suggestions.length; i++) {
                if (!uniqueTitles.has(suggestions[i].title)) {
                    uniqueTitles.add(suggestions[i].title);
                    suggestionsHtml += "<p onclick='selectSuggestion(\"" + suggestions[i].title.replace(/"/g, '&quot;') + "\")'>" + suggestions[i].title + "</p>";
                }
            }
            document.getElementById("suggestions").innerHTML = suggestionsHtml;
            document.getElementById("suggestions").style.display = suggestionsHtml ? "block" : "none";
            document.getElementById("suggestions").style.width = document.getElementById("query").offsetWidth + "px";
            document.getElementById("suggestions").className = document.body.classList.contains('dark-mode') ? 'dark-mode' : '';
        }
    };
    xhr.open("GET", "suggestions?query=" + encodeURIComponent(str), true);
    xhr.send();
}

/**
 * Updates the value of the input field with the selected suggestion title and hides the suggestions container.
 *
 * @param {string} title - The title of the selected suggestion.
 */
function selectSuggestion(title) {
    document.getElementById("query").value = title;
    document.getElementById("suggestions").style.display = "none";
}

/**
 * Event listener that hides the suggestions element when a click event occurs outside of the suggestions or query elements.
 */
document.addEventListener('click', function(e) {
    const suggestions = document.getElementById("suggestions");
    if (e.target.closest("#suggestions") === null && e.target.closest("#query") === null) {
        suggestions.style.display = "none";
    }
});
