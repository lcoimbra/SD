<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="ICO/favicon.ico">

    <title>iBei</title>

    <!-- Bootstrap core CSS -->
    <link href="CSS/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="CSS/navbar-fixed-top.css" rel="stylesheet">
</head>

<body>
    <!-- Fixed navbar -->
    <jsp:include page="headerAdmin.jsp" />

    <div class="container">

        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Statistics</h1>
            <c:if test="${session.stats != null}">
                <p><b>Top 10 Users - Auction Created:</b></p>
                <c:forEach items="${session.stats.getCreation()}" var="i">
                    <p><c:out value="${i}"/></p>
                </c:forEach>
                <hr>
                <p><b>Top 10 Users - Auction Won:</b></p>
                <c:forEach items="${session.stats.getWon()}" var="i">
                    <p><c:out value="${i}"/></p>
                </c:forEach>
                <hr>
                <p><b>Number of auctions closed last 10 days: </b>${session.stats.getnClose()}</p>
            </c:if>
        </div>

    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="JS/bootstrap.min.js"></script>
</body>
</html>
