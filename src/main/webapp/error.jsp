<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Movie Seeker</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="${pageContext.request.contextPath}/js/theme.js" defer></script>
</head>
<body>

<header class="app-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">
            🎬 Movie<span>Seeker</span>
        </a>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/" class="nav-link">Back to Home</a>
            <div class="theme-toggle">
                <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
                <label for="theme-toggle" class="theme-label" title="Toggle Dark Mode"></label>
            </div>
        </nav>
    </div>
</header>

<main class="container" style="text-align: center; padding-top: 100px;">
    <div class="error-content">
        <h1 style="font-size: 4rem; margin: 0; color: #dc3545;">Something went wrong</h1>
        <p style="font-size: 1.2rem; color: var(--text-muted); margin: 20px 0;">
            We encountered an unexpected error properly processing your request.
        </p>

        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Try Again</a>
    </div>
</main>

<footer class="app-footer">
    <div class="container">
        <p>&copy; 2026 Movie Seeker by Konrad Czardybon</p>
        <p class="api-credit">This product uses the TMDb API but is not endorsed or certified by TMDb.</p>
    </div>
</footer>

</body>
</html>