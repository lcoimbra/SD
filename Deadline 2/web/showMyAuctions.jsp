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

        function validate2() {
            var val = document.getElementById("Id2").value;
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
            <h1>MyAuctions</h1>
            <c:if test="${session.myAuctions.getAucBeanArray() != null}">
                <c:forEach items="${session.myAuctions.getAucBeanArray()}" var="aux">
                    <p><b>Id: </b><c:out value="${aux.getId()}"/></p>
                    <p><b>Code: </b><c:out value="${aux.getCode()}"/></p>
                    <p><b>Title: </b><c:out value="${aux.getTitle()}"/></p>
                    <hr>
                </c:forEach>
            </c:if>
        </div>

        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Search details of an auction</h1>
            <form class="form-group" method="POST" action="DetailAuction" onsubmit="return validate()">
                <div class="form-group">
                    <label for="Id">ID</label>
                    <input type="text" class="form-control" name="Id" id="Id">
                </div>
                <button type="submit" class="btn btn-default">Search</button>
            </form>
        </div>

        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Edit auction</h1>
            <form class="form-group" method="get" action="EditAuction" onsubmit="return validate2()">
                <div class="form-group">
                    <label for="Id">ID</label>
                    <input type="text" class="form-control" name="Id" id="Id2">
                </div>
                <button type="submit" class="btn btn-default">Edit</button>
            </form>
        </div>
    </div> <!-- /container -->
    <!-- Fixed navbar -->
    <jsp:include page="header.jsp" />

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="JS/bootstrap.min.js"></script>
</body>
</html>


