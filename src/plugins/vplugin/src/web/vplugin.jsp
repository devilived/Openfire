<%@page import="com.vidmt.of.plugin.utils.VerStatUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.*
                 "
	errorPage="error.jsp"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>

<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager" />
<c:set var="admin" value="${admin.manager}" />
<%
	admin.init(request, response, session, application, out);
%>

<html>
<head>
<title>vplugin插件首页</title>
<meta name="pageID" content="vplugin" />
<%-- ========================================================= --%>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="description" content="">
<meta name="keywords" content="">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<!-- Set render engine for 360 browser -->
<meta name="renderer" content="webkit">

<!-- No Baidu Siteapp-->
<meta http-equiv="Cache-Control" content="no-siteapp" />

<!-- Add to homescreen for Chrome on Android -->
<meta name="mobile-web-app-capable" content="yes">
<link rel="icon" sizes="192x192" href="assets/i/app-icon72x72@2x.png">

<!-- Add to homescreen for Safari on iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="Amaze UI" />
<link rel="apple-touch-icon-precomposed"
	href="assets/i/app-icon72x72@2x.png">

<!-- Tile icon for Win8 (144x144 + tile color) -->
<meta name="msapplication-TileColor" content="#0e90d2">
<link rel="stylesheet" href="assets/plugins/amazeui/css/amazeui.min.css">
<script src="assets/js/jquery-2.1.4.min.js"></script>
<script src="assets/plugins/amazeui/js/amazeui.min.js"></script>
<%-- ============================================================================= --%>
<script src="assets/js/vplugin.js"></script>
<style type="text/css">

</style>
</head>
<body>
	<fmt:message key="vplugin.body" />
	<table class="am-table am-table-bordered am-table-striped am-table-compact">
		<caption>统计信息</caption>
		<tr>
			<th>版本信息</th>
			<td id="VerInfo"></td>
		</tr>
	</table>
	<table class="am-table am-table-bordered am-table-striped am-table-compact"id="UrlTestTb" >
		<caption>测试信息</caption>
		<tr>
			<td>api/test/cache/clear.json</td>
			<td class='btn'><button type='button' style="color:red;font-weight:bold">清除</button></td>
		</tr>
		<tr>
			<td>api/test/user/delete.json</td>
			<td class='btn'><button type='button' style="color:red;font-weight:bold">清除</button></td>
		</tr>
		<tr>
			<td data-url='api/test/user/delete.json?uid={uid}&phone={phone}'>api/test/user/delete.json?uid=<input type="text" name="uid"/>&amp;phone=<input type="text" name="phone"/>.json</td>
			<td class='btn'><button type='button'>显示</button></td>
		</tr>
		<tr>
			<td>api/test/cache/trace.json</td>
			<td class='btn'><button type='button'>显示</button></td>
		</tr>
		<tr>
			<td data-url='api/1/location/getNearby.json?SRDSCFGA89asdnomasdfpawerlk={uid}&pageSize=24&currentPage={currentPage}'>api/1/location/getNearby.json?uid=<input type="text" name="uid"/>&amp;pageSize=24&amp;currentPage=<input type="text" name="currentPage"/>.json</td>
			<td class='btn'><button type='button'>显示</button></td>
		</tr>
		<tr>
			<td data-url='api/1/user/getByAcc.json?SRDSCFGA89asdnomasdfpawerlk=111&account={acc}'>api/1/user/getByAcc.json?account=<input type="text" name="acc"/></td>
			<td class='btn'><button type='button'>显示</button></td>
		</tr>
		<tr>
			<td data-url='api/test/cache/clear/user/{uid}.json'>api/test/cache/clear/user/<input type="text" name="uid"/>.json</td>
			<td class='btn'><button type='button' style="color:red;font-weight:bold">清除</button></td>
		</tr>
	</table>
</body>
</html>