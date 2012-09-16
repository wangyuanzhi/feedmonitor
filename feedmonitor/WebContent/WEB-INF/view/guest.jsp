<%@page contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@page import="com.hotinno.feedmonitor.web.guest.*"%>
<jsp:useBean id="guestDao" type="com.hotinno.feedmonitor.dao.guest.GuestDao" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>JPA Guestbook Web Application Tutorial - Powered by the
	PostgreSQL service in Cloud Foundry</title>
</head>
<body>
	<form method="POST" action="guest.html">
		Name: <input type="text" name="name" /> <input type="submit"
			value="Add" />
	</form>
	<hr>
	<ol>
		<%
			for (com.hotinno.feedmonitor.dao.guest.Guest guest : guestDao.getAllGuests()) {
		%>
		<li><%=guest%></li>
		<%
   }
%>
	</ol>
	<hr>
</body>
</html>