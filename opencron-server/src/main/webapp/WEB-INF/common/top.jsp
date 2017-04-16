<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
	String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
	String path = request.getContextPath().replaceAll("/$","");
	String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
	pageContext.setAttribute("contextPath",contextPath);
	request.setAttribute("uri",request.getRequestURI());
%>

<body id="skin-blur-ocean">
<div id="mask" class="mask"></div>
<header id="header">
	<a href="" id="menu-toggle" style="background-image: none"><i class="icon">&#61773;</i></a>
	<a id="log1" href="${contextPath}/home?csrf=${csrf}" class="logo pull-left"><div style="float: left; width: 165px; margin-top: 5px; margin-left: 14px">
		<img src="${contextPath}/img/opencron.png">
	</div>
	</a>
	<div class="media-body">
		<div class="media" id="top-menu" style="float:right;margin-right:15px;">
			<div class="pull-left tm-icon" id="msg-icon" style="display: none;">
				<a  class="drawer-toggle" data-drawer="messages" id="toggle_message" href="#">
					<i class="sa-top-message icon" style="background-image:none;font-size: 30px; background-size: 25px;">&#61710;</i>
					<i class="n-count">5</i>
				</a>
			</div>
			<div id="time" style="float:right;">
				<span id="hours"></span>:<span id="min"></span>:<span id="sec"></span>
			</div>
		</div>
	</div>
</header>

<div class="clearfix"></div>

<div class="container" id="crop-avatar">

	<!-- Cropping modal -->
	<div class="modal fade" id="avatar-modal" aria-hidden="true" aria-labelledby="avatar-modal-label" role="dialog" tabindex="-1">
		<div class="modal-dialog modal-md">
			<div class="modal-content">
				<form class="avatar-form" name="picform" action="${contextPath}/headpic/upload" enctype="multipart/form-data" method="post">
                    <input type="hidden" name="csrf" value="${csrf}">
					<input name="userId" type="hidden" value="${opencron_user.userId}">
					<div class="modal-header">
						<button class="close" data-dismiss="modal" type="button">&times;</button>
						<h4 class="modal-title" id="avatar-modal-label">更改图像</h4>
					</div>
					<div class="modal-body">
						<div class="avatar-body">

							<!-- Upload image and data -->
							<div class="avatar-upload">
								<input class="avatar-src" name="src" type="hidden">
								<input class="avatar-data" name="data" type="hidden">

								<input type="button" value="请选择本地照片" class="btn btn-default" onclick="document.picform.file.click()">
								<input class="avatar-input" id="avatarInput" name="file" type="file" style="display:none;">
							</div>

							<!-- Crop and preview -->
							<div class="row">
								<div class="col-md-8">
									<div class="avatar-wrapper">
										<span class="upload-txt"><span class="upload-add"></span>点击上传图片并选择需要裁剪的区域</span>
									</div>
								</div>

								<div class="col-md-4">
									<div class="avatar-preview preview-lg"></div>
								</div>

							</div>

							<div class="row avatar-btns">
								<div class="col-md-8">
									<div class="btn-group">
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="-90" title="逆时针旋转90度">向左转</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="-15">-15°</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="-30">-30°</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="-45">-45°</button>
									</div>
									<div class="btn-group" style="float:right">
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="90" title="顺时针旋转90度">向右转</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="15">15°</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="30">30°</button>
										<button type="button" class="btn btn-primary" data-method="rotate" data-option="45">45°</button>
									</div>
								</div>
								<div class="col-md-4">
									<button class="btn btn-primary btn-block avatar-save" type="submit">上传</button>
								</div>
							</div>


						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- Loading state -->
	<div class="loading" aria-label="Loading" role="img" tabindex="-1"></div>
</div>

<section id="main" class="p-relative" role="main">

	<!-- Sidebar -->
	<aside id="sidebar">

		<!-- Sidbar Widgets -->
		<div class="side-widgets overflow">
			<!-- Profile Menu -->
			<div class="text-center s-widget m-b-25 dropdown" id="profile-menu">
				<a href="" id="header-img" data-toggle="dropdown" class="animated a-hover">
					<img class="profile-pic" id="profile-pic" width="140px;" height="140px;"  onerror="javascript:this.src='${contextPath}/img/profile-pic.jpg'" src="${contextPath}/upload/${opencron_user.userId}${opencron_user.picExtName}?<%=System.currentTimeMillis()%>">
					<div class="change-text" id="change-img" href="javascript:void(0);">更换头像</div>
				</a>
				<h4 class="m-0">${opencron_user.userName}</h4>
				<ul class="dropdown-menu profile-menu">
					<li><a href="${contextPath}/user/detail?userId=${opencron_user.userId}&csrf=${csrf}">个人信息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
					<li><a href="${contextPath}/notice/view?csrf=${csrf}">通知&nbsp;&&nbsp;消息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
					<li><a href="${contextPath}/logout?csrf=${csrf}">退出登录</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
				</ul>
			</div>

			<!-- Calendar -->
			<div class="s-widget m-b-25">
				<div id="sidebar-calendar"></div>
			</div>

			<div class="s-widget m-b-25 opencron-progress" style="display: none;" >
				<h2 class="tile-title side-tile-title tile-title-color">
					报告明细
				</h2>

				<div class="s-widget-body">
					<div class="side-border">
						<small>运行模式(自动/手动)</small>
						<div class="progress progress-small">
							<a href="#" data-toggle="tooltip" id="progress_type" class="progress-bar tooltips progress-bar-danger">
								<span class="sr-only" id="progress_type_tip"></span>
							</a>
						</div>
					</div>
					<div class="side-border">
						<small>作业类型(单一/流程)</small>
						<div class="progress progress-small">
							<a href="#" data-toggle="tooltip" id="progress_category" class="tooltips progress-bar progress-bar-info">
								<span class="sr-only"></span>
							</a>
						</div>
					</div>
					<div class="side-border">
						<small>规则类型(crontab/quartz)</small>
						<div class="progress progress-small">
							<a href="#" data-toggle="tooltip" id="progress_model" class="tooltips progress-bar progress-bar-warning">
								<span class="sr-only"></span>
							</a>
						</div>
					</div>
					<div class="side-border">
						<small>重跑状态(非重跑/重跑)</small>
						<div class="progress progress-small">
							<a href="#" data-toggle="tooltip" id="progress_rerun" class="tooltips progress-bar progress-bar-success">
								<span class="sr-only"></span>
							</a>
						</div>
					</div>
					<div class="side-border">
						<small>执行状态(成功/失败)</small>
						<div class="progress progress-small">
							<a href="#" data-toggle="tooltip" id="progress_status" class="tooltips progress-bar progress-bar-success">
								<span class="sr-only"></span>
							</a>
						</div>
					</div>
				</div>
			</div>

			</div>

		</div>

		<!-- Side Menu -->
		<ul class="list-unstyled side-menu">
			<li class="<c:if test="${fn:contains(uri,'/home')}">active</c:if>">
				<a href="${contextPath}/home?csrf=${csrf}">
					<i aria-hidden="true" class="fa fa-tachometer"></i><span class="menu-item">作业报告</span>
				</a>
			</li>
			<li class="<c:if test="${fn:contains(uri,'/agent')}">active</c:if>">
				<a  href="${contextPath}/agent/view?csrf=${csrf}">
					<i aria-hidden="true" class="fa fa-desktop"></i><span class="menu-item">执行器管理</span>
				</a>
			</li>
			<li class="dropdown <c:if test="${fn:contains(uri,'/job')}">active</c:if>">
				<a href="#">
					<i aria-hidden="true" class="fa fa-tasks" aria-hidden="true"></i><span class="menu-item">作业管理</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/job/view')}">class="active"</c:if>>
						<a href="${contextPath}/job/view?csrf=${csrf}" class="<c:if test="${fn:contains(uri,'/job/view')}">active</c:if>">作业列表</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/exec')}">class="active"</c:if>>
						<a href="${contextPath}/job/goexec?csrf=${csrf}" class="<c:if test="${fn:contains(uri,'/exec')}">active</c:if>">现场执行</a>
					</li>
				</ul>
			</li>

			<li class="dropdown <c:if test="${fn:contains(uri,'/record')}">active</c:if>">
				<a href="#">
					<i class="fa fa-print" aria-hidden="true"></i><span class="menu-item">调度记录</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/running')}">class="active"</c:if>>
						<a href="${contextPath}/record/running?csrf=${csrf}" class="<c:if test="${fn:contains(uri,'running')}">active</c:if>">正在运行</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/done')}">class="active"</c:if>>
						<a href="${contextPath}/record/done?csrf=${csrf}" class="<c:if test="${fn:contains(uri,'done')}">active</c:if>">已完成</a>
					</li>
				</ul>
			</li>

			<li class="<c:if test="${fn:contains(uri,'/terminal')}">active</c:if>">
				<a href="${contextPath}/terminal/view?csrf=${csrf}">
					<i aria-hidden="true" class="fa fa-terminal"></i><span class="menu-item">WEB终端</span>
				</a>
			</li>

			<c:if test="${permission eq true}">
				<li <c:if test="${fn:contains(uri,'/user')}">class="active"</c:if>>
					<a href="${contextPath}/user/view?csrf=${csrf}">
						<i class="fa fa-user" aria-hidden="true"></i></i><span class="menu-item">用户管理</span>
					</a>
				</li>
				<li <c:if test="${fn:contains(uri,'/config')}">class="active"</c:if>>
					<a href="${contextPath}/config/view?csrf=${csrf}">
						<i aria-hidden="true" class="fa fa-cog"></i><span class="menu-item">系统设置</span>
					</a>
				</li>
			</c:if>
		</ul>
	</aside>