<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Buffalo's Seeds Management</title>
<script type="text/javascript">
	function deleteSeed(seedId) {
		document.forms["seedForm"].elements["id"].value = seedId;
		document.forms["seedForm"].elements["action"].value = "delete";
		document.forms["seedForm"].submit();
	}
	function clearProcessed(seedId) {
		document.forms["seedForm"].elements["id"].value = seedId;
		document.forms["seedForm"].elements["action"].value = "clear";
		document.forms["seedForm"].submit();
	}
	function timedRefresh(timeoutPeriod) {
		setTimeout("location.reload(true);",timeoutPeriod);
	}
</script>
</head>
<body>
	<hr>
	<form id="seedForm" method="POST" action="seed.html">
		<table border="1">
			<tr>
				<td align="left">Name (Optional)</td>
				<td align="left"><input type="text" name="name" size="120" />
				</td>
			</tr>
			<tr>
				<td align="left">Magnet URL</td>
				<td align="left"><input type="text" name="url" size="120" /></td>
			</tr>
			<tr>
				<td align="left">Comment</td>
				<td align="left"><input type="text" name="comment" size="120" />
				</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><input type="submit"
					value="Submit" /></td>
			</tr>
		</table>
		<hr>
		<input type="hidden" name="id"> <input type="hidden"
			name="action">
		<table border="1">
			<tr bordercolor="#C0C0C0" bgcolor="#C0C0C0">
				<th align="center">Name</th>
				<!--<th align="center">Magnet URL</th>-->
				<th align="center">Adding Time</th>
				<th align="center">Processed</th>
				<th align="center">Processed Time</th>
				<th align="center">Comment</th>
				<th align="center">Actions</th>
			</tr>
			<c:forEach var="sd" items="${seeds}">
				<tr>
					<td align="left"><c:out value='${sd.name}' /></td>
					<td align="left"><fmt:formatDate value="${sd.date}"
							pattern="yyyy.MM.dd HH:mm:ss" timeZone="GMT+8" />
					</td>
					<td align="left">${sd.processed}</td>
					<td align="left"><fmt:formatDate value="${sd.processedTime}"
							pattern="yyyy.MM.dd HH:mm:ss" timeZone="GMT+8" />
					</td>
					<td align="left">${sd.comment}</td>
					<td align="center"><button onclick="deleteSeed(${sd.id})">Delete</button>
					<button onclick="clearProcessed(${sd.id})">Clear Processed</button>
				</tr>
			</c:forEach>
		</table>
	</form>
	<hr>
</body>
</html>