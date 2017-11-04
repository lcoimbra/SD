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
        function validateBid() {
            var val = document.getElementById("amount").value;
            if (val == "") {
                alert("Must Fill All Inputs!!");
                return false;
            }
        }

        function validateMessage() {
            var val = document.getElementById("message").value;
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
        <c:if test="${session.auction.getDeadline() != null}">
        <div class="jumbotron">
            <h1>Detail Auction</h1>
                    <c:if test="${session.auction.getId() != null }"><p><b>Id:</b> ${session.auction.getId()}</p></c:if>
                    <c:if test="${session.auction.getTitle() != null}"><p><b>Title:</b> ${session.auction.getTitle()}</p></c:if>
                    <c:if test="${session.auction.getCode() != null}"><p><b>Code:</b> ${session.auction.getCode()}</p></c:if>
                    <c:if test="${session.auction.getDescription() != null}"><p><b>Description:</b> ${session.auction.getDescription()}</p></c:if>
                    <c:if test="${session.auction.getDeadline() != null}"><p><b>Deadline:</b> ${session.auction.getDeadline()}</p></c:if>
                    <c:if test="${session.auction.getAmount() != null}"><p><b>Amount:</b> ${session.auction.getAmount()}</p></c:if>
                    <c:if test="${session.auction.getStatus() != null}"><p><b>Status:</b> ${session.auction.getStatus()}</p></c:if>
                    <c:if test="${session.eBayPrice != null}"><p><b>Lowest eBay price:</b> ${session.eBayPrice} €</p></c:if>
                    <c:if test="${session.auction.getMessage() != null}"><p><b>Messages:</b></p>
                          <c:forEach items="${session.auction.getMessage()}" var="msg">
                             <p> <c:out value="${msg}"/></p>
                          </c:forEach>
                    </c:if>
                    <c:if test="${session.auction.getBids() != null}"><p><b>Bids:</b></p>
                          <c:forEach items="${session.auction.getBids()}" var="bid">
                            <p> <c:out value="${bid}"/></p>
                          </c:forEach>
                    </c:if>
        </div>

        <!-- Main component for a primary marketing message or call to action -->
        <c:if test="${session.auction.getId() != null && session.userID!=-2}">
            <div class="jumbotron">
                <h1>Bid on this auction</h1>
                <form class="form-group" method="POST" action="bid" onsubmit="return validateBid()">
                    <div class="form-group">
                        <label for="amount">Amount</label>
                        <input type="text" class="form-control" name="amount" id="amount">
                    </div>
                    <button type="submit" class="btn btn-default">Bid</button>
                </form>
            </div>

            <!-- Main component for a primary marketing message or call to action -->
            <div class="jumbotron">
                <h1>Send a message</h1>
                <form class="form-group" method="POST" action="sendMsg" onsubmit="return validateMessage()">
                    <div class="form-group">
                        <label for="message">Message</label>
                        <input type="text" class="form-control" name="message" id="message">
                    </div>
                    <button type="submit" class="btn btn-default">Send</button>
                </form>
            </div>

            <!-- Main component for a primary marketing message or call to action -->
            <div class="jumbotron">
                <h1>Users seeing this auction</h1>
                <form class="form-group" method="POST" action="sendMsg">
                    <div>
                        <ul id="myListAuction"/>
                    </div>
                </form>
            </div>


        </c:if>

        <c:if test="${session.userID == -2}">
            <div class="jumbotron">
                <h1>Cancel Auction</h1>
                <form class="form-group" method="POST" action="cancel">
                    <button type="submit" class="btn btn-default">Cancel</button>
                </form>
            </div>
        </c:if>
        </c:if><c:if test="${session.auction.getDeadline() == null}">
        <div class="jumbotron">
            <h1>Detail Auction</h1>
            <p> Auction not found </p>
        </div>
        </c:if>
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

