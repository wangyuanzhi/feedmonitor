<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Buffalo's Feeds Management</title>
<script type="text/javascript">
	function deleteFeed(feedId) {
		document.forms["feedForm"].elements["id"].value = feedId;
		document.forms["feedForm"].elements["action"].value = "delete";
		document.forms["feedForm"].submit();
	}
	function clearLastUpdate(feedId) {
		document.forms["feedForm"].elements["id"].value = feedId;
		document.forms["feedForm"].elements["action"].value = "clear";
		document.forms["feedForm"].submit();
	}
	function refresh() {
		document.forms["feedForm"].elements["id"].value = "";
		document.forms["feedForm"].elements["action"].value = "";
		document.forms["feedForm"].submit();
	}
</script>
</head>
<body>
	<hr>
	<form id="feedForm" method="POST" action="feed.html">
		<table border="1">
			<!-- tr>
				<td align="left">Name (Optional)</td>
				<td align="left"><input type="text" name="name" size="120" />
				</td>
			</tr -->
			<tr>
				<td align="left">RSS URL</td>
				<td align="left"><input type="text" name="url" size="120"
					value="http://oabt.org/rss.php?cid=1" /></td>
			</tr>
			<tr>
				<td align="left">Keywords</td>
				<td align="left"><input type="text" name="keywords" size="120"
					value="720 rmvb " />
				</td>
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
		<button onclick="refresh()">Refresh</button>
		<br><hr>
		<table border="1">
			<tr bordercolor="#C0C0C0" bgcolor="#C0C0C0">
				<!-- th align="center">Name</th -->
				<th align="center">RSS URL</th>
				<th align="center">Keywords</th>
				<th align="center">Last Update</th>
				<th align="center">Comments</th>
				<th align="center">Actions</th>
			</tr>
			<c:forEach var="fd" items="${feeds}">
				<tr>
					<!-- td align="left"><c:out value='${fd.name}' /></td -->
					<td align="left"><a href="${fd.url}">${fd.url}</a>
					</td>
					<td align="left">${fd.keywords}</td>
					<td align="left"><fmt:formatDate value="${fd.lastUpdated}"
							pattern="yyyy.MM.dd HH:mm:ss" timeZone="GMT+8" />
					</td>
					<td align="left">${fd.comment}</td>
					<td align="center"><button onclick="deleteFeed(${fd.id})">Delete</button>
						<button onclick="clearLastUpdate(${fd.id})">Clear Last
							Update</button>
					</td>
				</tr>
			</c:forEach>
		</table>
	</form>
	<hr>
	<button onclick="refresh()">Refresh</button>
</body>
</html>