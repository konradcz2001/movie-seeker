<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPath" value="${pageContext.request.servletPath}" />

<header class="app-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">
            🎬 Movie<span>Seeker</span>
        </a>

        <nav class="main-nav">

            <c:if test="${currentPath.contains('/watchlist.jsp') || currentPath.contains('/movieDetails.jsp')}">
                <a href="${pageContext.request.contextPath}/" class="nav-link">Back to Search</a>
            </c:if>

            <c:if test="${!currentPath.contains('/watchlist.jsp') && !currentPath.contains('/error')}">
                <a href="${pageContext.request.contextPath}/watchlist.jsp" class="nav-link">My Watchlist</a>
            </c:if>

            <c:if test="${currentPath.contains('/error')}">
                <a href="${pageContext.request.contextPath}/" class="nav-link">Back to Home</a>
            </c:if>

            <div class="theme-toggle">
                <input type="checkbox" id="theme-toggle" onclick="toggleTheme()">
                <label for="theme-toggle" class="theme-label" title="Toggle Theme"></label>
            </div>
        </nav>
    </div>
</header>