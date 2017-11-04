<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<nav class="navbar navbar-default navbar-fixed-top">
    <c:if test="${session.username == null}">
        <jsp:forward page = "index.jsp" />
    </c:if>
    <c:if test="${session.userID == -2}">
        <jsp:forward page = "index.jsp" />
    </c:if>
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="./home.jsp">iBei</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="./createAuction.jsp">Create Auction</a></li>
                <li><a href="<s:url action="myAuctions"></s:url> ">My Auctions</a></li>
                <!--<li><a href="<s:url action="myAuctions"></s:url>My Actions</a></li>-->
                <li><a href="./search.jsp">Search</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <c:if test="${session.authenticationBean.fbIsAssociated( session.userID ) == false}">
                    <li><a href="<s:url action="fbAssociate"></s:url>">Link Facebook Account</a></li>
                </c:if>
                <li class="active"><a href="<s:url action="logout"></s:url> ">Logout<span class="sr-only">(current)</span></a></li>
            </ul>

        </div><!--/.nav-collapse -->
    </div>
</nav>
    <p hidden id="hidUserId"><c:out value="${session.userID}"/> </p>
    <p hidden id="hidUser"><c:out value="${session.username}"/> </p>
    <c:if test="${session.auction.getId() != null}">
        <p hidden id="hidId"><c:out value="${session.auction.getId()}"/></p>
    </c:if>

    <script src="JS/wsScript.js" type="text/javascript"/>


</body>
</html>
