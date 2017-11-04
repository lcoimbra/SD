<%--
  Created by IntelliJ IDEA.
  User: kifel
  Date: 05/12/2016
  Time: 12:23
  To change this template use File | Settings | File Templates.
--%>
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

    <script>
        function validate() {
            var val = document.getElementById("Id").value;
            if (val == "") {
                alert("Must Fill All Inputs!!");
                return false;
            }
        }
    </script>
</head>

<body>

    <div class="container">
        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Results of search auction by code</h1>
            <c:if test="${session.searchAuction.getAucBeanArray() != null}">
                <c:forEach items="${session.searchAuction.getAucBeanArray()}" var="aux">
                    <p><b>Id:</b><c:out value="${aux.getId()}"/></p>
                    <p><b>Code:</b><c:out value="${aux.getCode()}"/></p>
                    <p><b>Title:</b><c:out value="${aux.getTitle()}"/></p>
                    <hr>
                </c:forEach>
            </c:if>
        </div>

        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Search details of an auction</h1>
            <form class="form-group" method="GET" action="DetailAuction" onsubmit="return validate()">
                <div class="form-group">
                    <label for="Id">ID</label>
                    <input type="text" class="form-control" name="Id" id="Id">
                </div>
                <button type="submit" class="btn btn-default">Search</button>
            </form>
        </div>
    </div> <!-- /container -->
    <!-- Fixed navbar -->
    <c:if test="${session.userID != -2 }">
        <jsp:include page="header.jsp" />
    </c:if>
    <c:if test="${session.userID == -2 }">
        <jsp:include page="headerAdmin.jsp" />
    </c:if>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="JS/bootstrap.min.js"></script>
</body>
</html>

