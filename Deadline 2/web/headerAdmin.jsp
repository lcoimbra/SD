<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
</head>
<body>
<nav class="navbar navbar-default navbar-fixed-top">
    <c:if test="${session.username == null}">
        <jsp:forward page = "index.jsp" />
    </c:if>
    <c:if test="${session.userID != -2}">
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
            <a class="navbar-brand" href="./homeAdmin.jsp">iBei</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="<s:url action="Stats"></s:url> ">Statistics</a></li>
                <li><a href="./banUser.jsp">Ban User</a></li>
                <li><a href="./unlinkUser.jsp">Unlink User</a></li>
                <li><a href="./search.jsp">Search Auction</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <%--
                <li><a href="../navbar/">Default</a></li>
                <li><a href="">Notification</a></li>  <%-- alterar para aparecer o nº????????????--%>
                <li class="active"><a href="<s:url action="logout"></s:url> ">Logout<span class="sr-only">(current)</span></a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>
</body>
</html>
