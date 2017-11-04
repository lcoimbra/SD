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
    <link href="CSS/signin.css" rel="stylesheet">
</head>

<body>
    <c:if test="${session.username != null}">
        <c:if test="${session.userID != -2}">
            <jsp:forward page = "home.jsp" />
        </c:if>
        <jsp:forward page = "homeAdmin.jsp" />
    </c:if>
    <div class="container">
        <form class="form-signin" method="POST" action="login">
            <h2 class="form-signin-heading" align="center">Welcome</h2>
            <label for="inputUser" class="sr-only">Email address</label>
            <input type="text" name="username" id="inputUser" class="form-control" placeholder="Username" required autofocus>
            <label for="inputPassword" class="sr-only">Password</label>
            <input type="password"  name="password" id="inputPassword" class="form-control" placeholder="Password" required>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            <button class="btn btn-lg btn-primary btn-block" type="submit" name="register">Register</button>
        </form>
        <form class="form-signin" method="POST" action="login">
            <button class="btn btn-lg btn-primary btn-block" type="submit" name="fb">Log In FB</button>
        </form>
    </div> <!-- /container -->
</body>
</html>