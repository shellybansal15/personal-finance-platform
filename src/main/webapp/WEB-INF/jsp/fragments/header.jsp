<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="${sessionScope.csrfToken}">
    <title>${pageTitle != null ? pageTitle : 'FinanceFlow'} | Personal Finance Platform</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom Fintech Styles (White & Green in Light / Black & Green in Dark) -->
    <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">

    <!-- Pre-render Theme Initialization (prevents flash of unstyled theme) -->
    <script>
        (function() {
            const savedTheme = localStorage.getItem('finance_app_theme') || 'light';
            document.documentElement.setAttribute('data-bs-theme', savedTheme);
        })();
    </script>
</head>
<body>
