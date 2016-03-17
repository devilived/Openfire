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
	int userCount = admin.getUserManager().getUserCount();
%>

<html>
<head>
<title><fmt:message key="item.jvm" /></title>
<meta name="pageID" content="jvm" />
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
<script src="assets/js/handlebars-v4.0.5.js"></script>
<script src="assets/js/ext-hbs.js"></script>

<script src="assets/plugins/amazeui/js/amazeui.min.js"></script>
<%-- ============================================================================= --%>
<script>
jQuery.noConflict();
</script>
<script src="assets/js/jvm.js"></script>
<style type="text/css">
th small{font-weight:normal;}
#cacheTable thead th{padding-left:.2rem;padding-right:.2rem}
#clearCache{float:right}
</style>
</head>
<body>
	<div class="admin-content">
		<div class="am-g">
			<div class="am-u-sm-12">
			<table id='cacheTable' class="am-table am-table-bd am-table-striped admin-content-table">
					<caption>缓存信息<button id="clearCache" class="am-btn am-btn-danger" type="button">清空缓存</button></caption>
					<thead><tr>
						<th>&nbsp;</th>
						<th>avg getTime</th>
						<th>avg searchTime</th>
						<th>size</th>
						<th>mem size</th>
						<th>disk size</th>
						<th>offheap size</th>
						<th>calMem size</th>
						<th>calDisk size</th>
						<th>calOffheap size</th>
						<th>cache hits</th>
						<th>cache mis</th>
						<th>obj cnt</th>
						<th>disk hits</th>
						<th>disk mis</th>
						<th>diskObj cnt</th>
					</tr></thead>
					
					<tbody>
					<script id="cache-tpl" type="text/x-handlebars-template">
					{{#each this}}
					<tr class="cache-row">
						<td>{{name}}</td>
						<td>{{avgGetTime}}</td>
						<td>{{avgSearchTime}}</td>
						<td>{{size}}</td>
						<td>{{memSize}}</td>
						<td>{{diskSize}}</td>
						<td>{{offheapSize}}</td>
						<td>{{calMemSize}}</td>
						<td>{{calDiskSize}}</td>
						<td>{{calOffheapSize}}</td>
						<td>{{cacheHits}}</td>
						<td>{{cacheMis}}</td>
						<td>{{objCnt}}</td>
						<td>{{diskHits}}</td>
						<td>{{diskMis}}</td>
						<td>{{diskObjCnt}}</td>
					</tr>
					{{/each}}
					</script>
				</tbody>
				</table>
			</div>
			<div class="am-u-sm-12">
				<script id="mem-tpl" type="text/x-handlebars-template">
				<table class="am-table am-table-bd am-table-striped admin-content-table">
					<caption>内存信息</caption>
					<thead><tr>
						<th>&nbsp;</th>
						<th>初始内存(M)</th>
						<th>已用内存(M)</th>
						<th style="width:30%"><small>保证可以由 Java使用的内存量,committed 可以小于 init。但将始终大于或等于 used</small><br/>提交内存(M)</th>
						<th style="width:30%"><small>已使用的和已提交的将始终小于或等于 max（如果定义了 max）,当used <= max事，即使used > committed内存分配也会失败</small><br/>最大内存(M)</th>
					</tr></thead>
					
					<tbody>
					<tr>
						<td>堆内存</td>
						<td>{{showM heap.init}}</td>
						<td>{{showM heap.used}}</td>
						<td>{{showM heap.committed}}</td>
						<td>{{showM heap.max}}</td>
					</tr>
					<tr>
						<td>非堆内存</td>
						<td>{{showM nonheap.init}}</td>
						<td>{{showM nonheap.used}}</td>
						<td>{{showM nonheap.committed}}</td>
						<td>{{showM nonheap.max}}</td>
					</tr>
					{{#each pools}}
  						<tr>
						<td>内存池:<br/>{{type}}<br/>{{name}}</td>
						<td>{{showM peakusg.init}}</td>
						<td>{{showM peakusg.used}}</td>
						<td>{{showM peakusg.committed}}</td>
						<td>{{showM peakusg.max}}</td>						
						</tr>
					{{/each}}
					</tbody>
				</table>

				<table class="am-table am-table-bd am-table-striped admin-content-table">
					<caption>系统信息</caption>
					<tbody>
					<tr><td>rt名字</td><td>{{runtime.name}}</td></tr>
					<tr><td>rt规范名字</td><td>{{runtime.specname}}</td></tr>
					<tr><td>jvm名字</td><td>{{runtime.vmname}}</td></tr>
					<tr><td>启动时间</td><td>{{runtime.starttime}}</td></tr>
					<tr><td>cpu数量</td><td>{{runtime.cpus}}</td></tr>
					<tr><td>线程总数</td><td>{{thread.totalcnt}}</td></tr>
					<tr><td>线程峰值总数</td><td>{{thread.peakcnt}}</td></tr>
					<tr><td>守护线程总数</td><td>{{thread.daemoncnt}}</td></tr>
					<tr><td>已加载类总数</td><td>{{cll.loadedcnt}}</td></tr>
					<tr><td>已卸载类总数</td><td>{{cll.unloadedcnt}}</td></tr>
					<tr><td>历史类总数</td><td>{{cll.totalcnt}}</td></tr>
					</tbody>
				</table>
				</script>
			</div>
		</div>
	</div>
</body>
</html>