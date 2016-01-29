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
	int userCount = admin.getUserManager().getUserCount();
%>

<html>
<head>
<title><fmt:message key="item.user" /></title>
<meta name="pageID" content="user" />
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
<script src="assets/plugins/am-pagination/amazeui-pagination.min.js"></script>
<%-- ============================================================================= --%>
<script>
jQuery.noConflict();
window.USR_CNT=<%=userCount%>;
</script>
<script src="assets/js/user.js"></script>
<style type="text/css">
.avatar {
	width: 20px;
	height: 20px;
}

.presence {
	width: 16px;
	height: 16px;
	border: 0
}
.am-pagination {
	margin: 0
}
</style>
</head>
<body>
	<div class="admin-content">
		<div class="am-g">
			<div class="am-u-sm-12 am-u-md-6">
				<ul class="am-pagination"></ul>
			</div>
			<div class="am-u-sm-12 am-u-md-3">
				<div class="am-form-group">
					<select name="acctype" data-am-selected="{btnSize: 'sm'}">
						<option value="none">请选择......</option>
						<option value="uid">uid</option>
						<option value="phone">手机号</option>
					</select>
				</div>
			</div>
			<div class="am-u-sm-12 am-u-md-3">
				<div class="am-input-group am-input-group-sm">
					<input name="accvalue" type="text" class="am-form-field" /> <span
						class="am-input-group-btn">
						<button class="am-btn am-btn-default" type="button" id="search">搜索</button>
					</span>
				</div>
			</div>
		</div>
		<div class="am-g">
			<div class="am-u-sm-12">
				<table
					class="am-table am-table-bd am-table-striped admin-content-table">
					<thead>
						<tr>
							<th>头像</th>
							<th>UID</th>
							<th>手机号</th>
							<th>昵称</th>
							<th>在线状态</th>
							<th>会员等级</th>
							<th>到期日期</th>
							<th>管理</th>
						</tr>
					</thead>
					<tbody>
					<script id="user-tpl" type="text/x-handlebars-template">
					{{#each this}}
  						<tr class="row" data-uid="{{uid}}">
							<td>{{#if avatar}}<img class="avatar" src="/TelServer{{avatar}}">{{else}}&nbsp;{{/if}}</td>
							<td>{{ifnull uid}}</td>
							<td>{{ifnull phone}}</td>
							<td>{{ifnull nick}}</td>
							{{#unless presence}}<td><img class="presence" src="/images/user-clear-16x16.gif"/></td>{{/unless}}
							{{#compare presence '==' 'available'}}<td><img class="presence" src="/images/user-green-16x16.gif"/></td>{{/compare}}
							{{#compare presence '==' 'chat'}}<td><img class="presence" src="/images/user-green-16x16.gif"/></td>{{/compare}}
							{{#compare presence '==' 'away'}}<td><img class="presence" src="/images/user-yellow-16x16.gif"/></td>{{/compare}}
							{{#compare presence '==' 'xa'}}<td><img class="presence" src="/images/user-yellow-16x16.gif"/></td>{{/compare}}
							{{#compare presence '==' 'dnd'}}<td><img class="presence" src="/images/user-red-16x16.gif"/></td>{{/compare}}

							<td>{{ifnull lvl}}</td>
							<td>{{ifnull endTime}}</td>
							<td>
								<div class="am-btn-toolbar">
								  <div class="am-btn-group am-btn-group-xs">
									<button class="am-btn am-btn-default am-btn-xs am-text-secondary"><span class="am-icon-pencil-square-o"></span> 编辑</button>
									<button class="am-btn am-btn-default am-btn-xs am-text-danger"><span class="am-icon-trash-o"></span> 删除</button>
								  </div>
								</div>
							</td>
						</tr>
					{{/each}}
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