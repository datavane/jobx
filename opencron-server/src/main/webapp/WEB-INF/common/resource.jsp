<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
    String path = request.getContextPath().replaceAll("/$","");
    String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
    pageContext.setAttribute("contextPath",contextPath);
%>
<title>opencron</title>
    <meta name="format-detection" content="telephone=no">
    <meta name="description" content="opencron">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="opencron,crontab,a better crontab,Let's crontab easy">
    <meta name="author" content="author:benjobs,wechat:wolfboys,Created by 2016" />

    <!-- CSS -->
    <link rel="stylesheet" href="${contextPath}/css/bootstrap.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/animate.min.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/font-awesome.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/font-awesome-ie7.min.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/form.css?resId=${resourceId}" />

    <link rel="stylesheet" href="${contextPath}/css/calendar.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/style.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/icons.css?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/generics.css?resId=${resourceId}" />
    <link rel="stylesheet" href='${contextPath}/css/sweetalert.css?resId=${resourceId}' />
    <link rel="stylesheet" href='${contextPath}/css/opencron.css?resId=${resourceId}' />
    <link rel="stylesheet" href='${contextPath}/css/loading.css?resId=${resourceId}' />
    <link rel="stylesheet" href='${contextPath}/css/morris.css?resId=${resourceId}' />
    <link rel="stylesheet" href='${contextPath}/css/prettify.min.css?resId=${resourceId}' />
    <link rel="shortcut icon" href="${contextPath}/img/favicon.ico?resId=${resourceId}" />
    <link rel="stylesheet" href="${contextPath}/css/glyphicons.css?resId=${resourceId}" />

    <!-- Javascript Libraries -->
    <!-- jQuery -->
    <script type="text/javascript" src="${contextPath}/js/jquery.min.js?resId=${resourceId}"></script> <!-- jQuery Library -->
    <script type="text/javascript" src="${contextPath}/js/jquery-ui.min.js?resId=${resourceId}"></script> <!-- jQuery UI -->
    <script type="text/javascript" src="${contextPath}/js/jquery.easing.1.3.js?resId=${resourceId}"></script> <!-- jQuery Easing - Requirred for Lightbox + Pie Charts-->

    <!-- Bootstrap -->
    <script type="text/javascript" src="${contextPath}/js/bootstrap.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/easypiechart.js?resId=${resourceId}"></script> <!-- EasyPieChart - Animated Pie Charts -->

    <!--  Form Related -->
    <script type="text/javascript" src="${contextPath}/js/icheck.js?resId=${resourceId}"></script> <!-- Custom Checkbox + Radio -->
    <script type="text/javascript" src="${contextPath}/js/select.min.js?resId=${resourceId}"></script> <!-- Custom Select -->

    <!-- UX -->
    <script type="text/javascript" src="${contextPath}/js/scroll.min.js?resId=${resourceId}"></script> <!-- Custom Scrollbar -->

    <!-- Other -->
    <script type="text/javascript" src="${contextPath}/js/calendar.min.js?resId=${resourceId}"></script> <!-- Calendar -->
    <script type="text/javascript" src="${contextPath}/js/raphael.2.1.2-min.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/prettify.min.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/morris.min.js?resId=${resourceId}"></script>
    <!-- All JS functions -->
    <script id="themeFunctions" src="${contextPath}/js/functions.js?${contextPath}&resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/testdevice.js?resId=${resourceId}"></script>

    <!-- MD5 -->
    <script type="text/javascript" src="${contextPath}/js/md5.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.base64.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/html5/html5shiv/html5shiv.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/gauge.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.cookie.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/My97DatePicker/WdatePicker.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/sweetalert.min.js?resId=${resourceId}"></script>
    <script type="text/javascript" src="${contextPath}/js/opencron.js?resId=${resourceId}"></script>

    <!--upfile-->
    <link rel="stylesheet" href="${contextPath}/js/cropper/cropper.main.css?resId=${resourceId}" type="text/css" />
    <link rel="stylesheet" href="${contextPath}/js/cropper/cropper.css?resId=${resourceId}" type="text/css" />
    <script type="text/javascript" src="${contextPath}/js/cropper/cropper.js?resId=${resourceId}" ></script>
    <script type="text/javascript" src="${contextPath}/js/opencron.cropper.js?resId=${resourceId}" ></script>



