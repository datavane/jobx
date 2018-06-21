<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
	String path = request.getContextPath().replaceAll("/$","");
	String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
	pageContext.setAttribute("contextPath",contextPath);
%>

	<!-- Sidebar -->
	<aside id="sidebar">

		<!-- Sidbar Widgets -->
		<div class="side-widgets overflow">
			<!-- Profile Menu -->
			<div class="text-center s-widget m-b-25 dropdown" id="profile-menu">
				<a href="" id="header-img" data-toggle="dropdown" class="animated a-hover">
					<img class="profile-pic" id="profile-pic" width="140px;" height="140px;"
					<c:if test="${jobx_user.headerPath != null}">
						 src="${jobx_user.headerPath}?<%=System.currentTimeMillis()%>">
					</c:if>
					<c:if test="${jobx_user.headerPath == null}">
						src="${contextPath}/static/img/profile-pic.jpg">
					</c:if>
					<div class="change-text" id="change-img" href="javascript:void(0);">更换头像</div>
				</a>
				<h4 class="m-0">${jobx_user.userName}</h4>
				<ul class="dropdown-menu profile-menu">
					<li><a href="${contextPath}/user/detail/${jobx_user.userId}.htm">个人信息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
					<li><a href="${contextPath}/notice/view.htm">通知&nbsp;&&nbsp;消息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
					<li><a href="${contextPath}/logout.htm">退出登录</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
				</ul>
			</div>

			<!-- Calendar -->
			<div class="s-widget m-b-25">
				<div id="sidebar-calendar"></div>
			</div>

			<div class="s-widget m-b-25 jobx-progress" style="display: none;" >
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

		<!-- Side Menu -->
		<ul class="list-unstyled side-menu">

			<li class="<c:if test="${fn:contains(uri,'/dashboard')}">active</c:if>">
				<a href="${contextPath}/dashboard.htm">
					<i aria-hidden="true" class="fa fa-tachometer"></i><span class="menu-item">作业报告</span>
				</a>
			</li>

			<li class="dropdown <c:if test="${fn:contains(uri,'/group')}">active</c:if><c:if test="${fn:contains(uri,'/agent')}">active</c:if>">
				<a href="#">
					<i aria-hidden="true" class="fa fa-desktop" aria-hidden="true"></i><span class="menu-item">执行器管理</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/group')}">class="active"</c:if>>
						<a href="${contextPath}/group/view.htm" class="<c:if test="${fn:contains(uri,'/group')}">active</c:if>">分组管理</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/view')}">class="active"</c:if>>
						<a href="${contextPath}/agent/view.htm" class="<c:if test="${fn:contains(uri,'/agent')}">active</c:if>">执行器管理</a>
					</li>
				</ul>
			</li>

			<li class="dropdown <c:if test="${fn:contains(uri,'/job')}">active</c:if>">
				<a href="#">
					<i aria-hidden="true" class="fa fa-tasks" aria-hidden="true"></i><span class="menu-item">作业管理</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/job/view')}">class="active"</c:if>>
						<a href="${contextPath}/job/view.htm" class="<c:if test="${fn:contains(uri,'/job/view')}">active</c:if>">作业列表</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/goexec')}">class="active"</c:if>>
						<a href="${contextPath}/job/goexec.htm" class="<c:if test="${fn:contains(uri,'/goexec')}">active</c:if>">现场执行</a>
					</li>
				</ul>
			</li>

			<li class="dropdown <c:if test="${fn:contains(uri,'/record')}">active</c:if>">
				<a href="#">
					<i class="fa fa-print" aria-hidden="true"></i><span class="menu-item">调度记录</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/running')}">class="active"</c:if>>
						<a href="${contextPath}/record/running.htm" class="<c:if test="${fn:contains(uri,'running')}">active</c:if>">正在运行</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/done')}">class="active"</c:if>>
						<a href="${contextPath}/record/done.htm" class="<c:if test="${fn:contains(uri,'done')}">active</c:if>">已完成</a>
					</li>
				</ul>
			</li>

			<li class="<c:if test="${fn:contains(uri,'/terminal')}">active</c:if>">
				<a href="${contextPath}/terminal/view.htm">
					<i aria-hidden="true" class="fa fa-terminal"></i><span class="menu-item">WEB终端</span>
				</a>
			</li>


			<li class="dropdown <c:if test="${fn:contains(uri,'/monitor')}">active</c:if>">
				<a href="#">
					<i class="fa fa-bar-chart" aria-hidden="true"></i><span class="menu-item">监控中心</span>
				</a>
				<ul class="list-unstyled menu-item">
					<li <c:if test="${fn:contains(uri,'/druid')}">class="active"</c:if>>
						<a href="${contextPath}/monitor/druid.htm" class="<c:if test="${fn:contains(uri,'druid')}">active</c:if>">Druid监控</a>
					</li>
					<li <c:if test="${fn:contains(uri,'/report')}">class="active"</c:if>>
						<a href="${contextPath}/monitor/report.htm" class="<c:if test="${fn:contains(uri,'report')}">active</c:if>">机器监控</a>
					</li>
				</ul>
			</li>

			<c:if test="${permission eq true}">
				<li <c:if test="${fn:contains(uri,'/user')}">class="active"</c:if>>
					<a href="${contextPath}/user/view.htm">
						<i class="fa fa-user" aria-hidden="true"></i></i><span class="menu-item">用户管理</span>
					</a>
				</li>
				<li <c:if test="${fn:contains(uri,'/config')}">class="active"</c:if>>
					<a href="${contextPath}/config/view.htm">
						<i aria-hidden="true" class="fa fa-cog"></i><span class="menu-item">系统设置</span>
					</a>
				</li>
			</c:if>

		</ul>
	</aside>