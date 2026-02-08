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

<%@ include file="fragments/header.jsp" %>

<main class="container" style="text-align: center; padding-top: 100px;">
    <div class="error-content">
        <h1 style="font-size: 4rem; margin: 0; color: #dc3545;">Something went wrong</h1>
        <p style="font-size: 1.2rem; color: var(--text-muted); margin: 20px 0;">
            We encountered an unexpected error properly processing your request.
        </p>

        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Try Again</a>
    </div>
</main>

<%@ include file="fragments/footer.jsp" %>

</body>
</html>