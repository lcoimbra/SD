<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="ICO/favicon.ico">

    <title>Ban User</title>

    <!-- Bootstrap core CSS -->
    <link href="CSS/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="CSS/navbar-fixed-top.css" rel="stylesheet">

    <script>
        function validate() {
            var val = document.getElementById("user").value;
            if (val == "") {
                alert("Must Fill All Inputs!!");
                return false;
            }
        }
    </script>
</head>

<body>
<!-- Fixed navbar -->
<jsp:include page="headerAdmin.jsp" />

<div class="container">

    <!-- Main component for a primary marketing message or call to action -->
    <div class="jumbotron">
        <h1>Unlink User from their Facebook Account</h1>
        <form class="form-group" method="POST" action="unlinkUser" onsubmit="return validate()">
            <div class="form-group">
                <label for="user">Username</label>
                <input type="text" class="form-control" name="user" id="user">
            </div>
            <button type="Submit" class="btn btn-default">Unlink</button>
        </form>
        </p>
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
