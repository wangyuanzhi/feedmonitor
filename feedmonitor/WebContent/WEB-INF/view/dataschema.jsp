<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>JPA Guestbook Web Application Tutorial - Powered by the
	PostgreSQL service in Cloud Foundry</title>
</head>
<body>
	<form method="POST" action="dataschema.html">
		SQL:
		<textarea rows="10" cols="120" name="sql">${sql}</textarea>
		<!--
		Name: <input type="textarea" name="sql" size="120" value="${sql}" />  -->
		<br>
		<input type="submit" value="Add" />
	</form>
	<hr>
	<ol>
	<c:forEach var="line" items="${result}">
		<li><c:out value="${line}"></c:out>
		</li>
	</c:forEach>
	</ol>
	<hr>
</body>
</html>