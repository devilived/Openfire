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
<title><fmt:message key="item.statistics" /></title>
<meta name="pageID" content="statistics" />
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
<script src="assets/js/statistics.js"></script>
<style type="text/css">
</style>
</head>
<body>
	<div class="admin-content">
		<div class="am-g">
			<div class="am-u-sm-12">
				<script id="ver-tpl" type="text/x-handlebars-template">
				<table class="am-table am-table-bd am-table-striped admin-content-table">
					<caption>客户端版本统计  在线量：[安卓:{{ifnullZero android}} / 苹果:{{ifnullZero ios}}]</caption>
					<thead><tr><th>客户端</th><th>最后访问日期</th></tr></thead>
					
					<tbody>
					
					{{#each lastlogin}}
  						<tr class="row">
							<td>{{client}}</td>
							<td>{{lasttime}}</td>
						</tr>
					{{/each}}
					</tbody>
				</table>
				</script>
			</div>
			
			<div class="am-u-sm-12">
				<table class="am-table am-table-bd am-table-striped admin-content-table">
					<caption>收入统计</caption>
					<thead><tr>
							<th>&nbsp;</th>
							<th>支付宝/天</th>
							<th>微信/天</th>
							<th>合计/天</th>
							<th>年会员/个</th>
						</tr>
					</thead>
					<tbody>
					<script id="money-tpl" type="text/x-handlebars-template">
					{{#each KEY_WEEK}}
  						<tr class="row">
							<td>{{showday @index}}</td>
							<td>{{alicnt}}笔&nbsp;/&nbsp;{{cal alimoney '/' 100}}元</td>
							<td>{{wxcnt}}笔&nbsp;/&nbsp;{{cal wxmoney '/' 100 }}元</td>
							<td>{{cnt}}笔&nbsp;/&nbsp;{{cal money '/' 100 }}元</td>
							<td>{{yearcnt}}个</td>
						</tr>
					{{/each}}
					<tr class="row">
						<td>一周平均</td>
						<td>{{calint KEY_WEEK_ALI_CNT '/' 7}}笔&nbsp;/&nbsp;{{cal KEY_WEEK_ALI_AVG '/' 100}}元</td>
						<td>{{calint KEY_WEEK_WX_CNT '/' 7}}笔&nbsp;/&nbsp;{{cal KEY_WEEK_WX_AVG '/' 100}}元</td>
						<td>{{calint KEY_WEEK_CNT '/' 7}}笔&nbsp;/&nbsp;{{cal KEY_WEEK_AVG '/' 100}}元</td>
						<td>{{calint KEY_WEEK_YEAR_CNT '/' 7}}个</td>
					</tr>
					<tr class="row">
						<td>本月总共</td>
						<td colspan='3'>{{cal KEY_SUM_MONTH '/' 100}}元</td>
					</tr>
					</script>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<div class="am-modal am-modal-prompt" tabindex="-1" id="userEditLayer">
	  <div class="am-modal-dialog">
	    <div class="am-modal-hd">用户{{uid}}/{{phone}}/{{nick}}</div>
	    <div class="am-modal-bd">
	    <input type="text" class="am-modal-prompt-input" value="{{lvl}}" placeholder="级别">
<!-- 	       	级别:<select name="lvl" data-am-selected="{btnSize: 'sm'}"> -->
<!-- 						<option value="">取消会员</option> -->
<!-- 						<option value="TRY">TRY</option> -->
<!-- 						<option value="YEAR">YEAR</option> -->
<!-- 				</select> -->
<!-- 				<input type="hidden" name="uid" value="{{}}"/> -->
	    </div>
	    <div class="am-modal-footer">
	      <span class="am-modal-btn" data-am-modal-cancel>取消</span>
	      <span class="am-modal-btn" data-am-modal-confirm>保存</span>
	    </div>
	  </div>
	</div>
</body>
</html>