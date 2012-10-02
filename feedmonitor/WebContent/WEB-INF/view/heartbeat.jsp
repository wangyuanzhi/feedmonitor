<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Buffalo's Heart Beats</title>
<script type="text/JavaScript">
<!--
function timedRefresh(timeoutPeriod) {
	setTimeout("location.reload(true);",timeoutPeriod);
}
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-28787291-1']);
_gaq.push(['_trackPageview']);

(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
//   -->
</script>
</head>
<body onload="JavaScript:timedRefresh(60000);">
	<hr>
	<div><a href='feed.html'>Feed Management</a>, <a href='seed.html'>Seed Management</a></div>
	<hr>
	<table border="1">
		<tr>
			<td align="left">Connect to <b>Transmission(BT)</b> with:</td>
			<td align="left"><a href="https://kingchy.dyndns.org/transmission/">FQDN
					(Domain Name)</a></td>
			<td align="left"><a
				href="https://<c:out value='${latestIp}' />/transmission/">IP Address</a></td>
			<td align="left"><a href="https://192.168.0.223/transmission/">LAN</a></td>
		</tr>
		<tr>
			<td align="left">Connect to <b>mlnet(eMule)</b> with:</td>
			<td align="left"><a href="http://kingchy.dyndns.org:4080">FQDN
					(Domain Name)</a></td>
			<td align="left"><a
				href="http://<c:out value='${latestIp}' />:4080">IP Address</a></td>
			<td align="left"><a href="http://192.168.0.223:4080">LAN</a></td>
		</tr>
		<tr>
			<td align="left">Connect to <b>Web Access</b> with:</td>
			<td align="left"><a href="http://kingchy.dyndns.org:9000">FQDN
					(Domain Name)</a></td>
			<td align="left"><a
				href="http://<c:out value='${latestIp}' />:9000">IP Address</a></td>
			<td align="left"><a href="http://192.168.0.223:9000">LAN</a></td>
		</tr>
		<tr>
			<td align="left">Connect to <b>Web Shell</b> with:</td>
			<td align="left"><a href="https://kingchy.dyndns.org/webshell/">FQDN
					(Domain Name)</a></td>
			<td align="left"><a
				href="https://<c:out value='${latestIp}' />/webshell/">IP Address</a></td>
			<td align="left"><a href="https://192.168.0.223/webshell/">LAN</a></td>
		</tr>
	</table>
	<hr>
	<table border="1">
		<tr bordercolor="#C0C0C0" bgcolor="#C0C0C0">
			<th align="center">IP Address</th>
			<th align="center">First Strike</th>
			<th align="center">Last Beat</th>
			<th align="center">Life Time</th>
		</tr>
		<c:forEach var="hb" items="${heatbeats}">
			<tr>
				<td align="left"><c:out value='${hb.ip}' /></td>
				<td align="left"><fmt:formatDate value="${hb.time}"
						pattern="yyyy.MM.dd 'at' HH:mm:ss" timeZone="GMT+8" />
				</td>
				<td align="left"><fmt:formatDate value="${hb.lastBeat}"
						pattern="yyyy.MM.dd 'at' HH:mm:ss" timeZone="GMT+8" />
				</td>
				<td align="left"><fmt:formatNumber maxFractionDigits="2"
						value="${ (hb.lastBeat.time - hb.time.time) / (60* 60 * 1000) % 60 }" />
					Hour(s)</td>
			</tr>
		</c:forEach>
	</table>
	<hr>
	<font color="#C0C0C0"> Total Heart Beat is: <c:out
			value="${total}"></c:out>, Last Update is: <fmt:formatDate value="${lastUpdate}"
							pattern="yyyy.MM.dd HH:mm:ss" timeZone="GMT+8" /></font>
</body>
</html>