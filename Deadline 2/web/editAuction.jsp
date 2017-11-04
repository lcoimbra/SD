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

    <title>iBei</title>

    <!-- Bootstrap core CSS -->
    <link href="CSS/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="CSS/navbar-fixed-top.css" rel="stylesheet">
</head>

<body>

    <div class="container">
        <!-- Main component for a primary marketing message or call to action -->
        <div class="jumbotron">
            <h1>Edit Auction</h1>
            <form class="form-group" method="POST" action="EditAuction2">
                <div class="form-group">
                    <label for="Code">Code</label>
                    <input type="text" class="form-control" name="Code" id="Code">
                </div>
                <div class="form-group">
                    <label for="Title">Title</label>
                    <input type="text" class="form-control" name="Title" id="Title">
                </div>
                <div class="form-group">
                    <label for="Description">Description</label>
                    <input type="text" class="form-control" name="Description" id="Description">
                </div>
                <div class="form-group">
                    <label for="Deadline">Deadline</label>
                    <input type="datetime-local" class="form-control" name="Deadline" id="Deadline">
                </div>
                <div class="form-group">
                    <label for="Amount">Amount</label>
                    <input type="text" class="form-control" name="Amount" id="Amount">
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

