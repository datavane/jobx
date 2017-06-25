<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String port = request.getServerPort() == 80 ? "" : (":"+request.getServerPort());
    String path = request.getContextPath().replaceAll("/$","");
    String contextPath = request.getScheme()+"://"+request.getServerName()+port+path;
    pageContext.setAttribute("contextPath",contextPath);
%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>

    <jsp:include page="/WEB-INF/layouts/resource.jsp"/>

    <sitemesh:write property='head' />

    <script type="text/javascript">

        $(document).ready(function() {

            <c:if test="${fn:contains(uri,'/notice/')}">
            $("#msg-icon").remove();
            </c:if>

            if($.isMobile()){
                $("#time").remove();
                $("#change-img").remove();
                $(".opencron-progress").remove();
            }else {
                $("#profile-pic").mouseover(function () {
                    $("#change-img").show();
                }).mouseout(function () {
                    $("#change-img").hide();
                });

                $("#change-img").mouseover(function () {
                    $(this).show();
                }).mouseout(function () {
                    $(this).hide();
                });

                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/progress.do",
                    dataType: "JSON",
                    success: function (data) {
                        if (data) {
                            $(".opencron-progress").show();
                            var job_type = parseInt(parseFloat(data.auto / (data.auto + data.operator)) * 100);
                            if (isNaN(job_type)) {
                                $("#progress_type").css("width", "0%");
                            } else {
                                $("#progress_type").attr("data-original-title", job_type+"%").css("width", job_type + "%").next();

                                $("#progress_type").attr("data-original-title", job_type+"%").css("width", job_type + "%");
                            }

                            var job_category = parseInt(parseFloat(data.singleton / (data.singleton + data.flow)) * 100);
                            if (isNaN(job_category)) {
                                $("#progress_category").attr("data-original-title", 0).css("width", "0%");
                            } else {
                                $("#progress_category").attr("data-original-title", job_category+"%").css("width", job_category + "%");
                            }

                            var job_model = parseInt(parseFloat(data.crontab / (data.crontab + data.quartz)) * 100);
                            if (isNaN(job_model)) {
                                $("#progress_model").attr("data-original-title", 0).css("width", "0%");
                            } else {
                                $("#progress_model").attr("data-original-title", job_model+"%").css("width", job_model + "%");
                            }

                            var job_rerun = parseInt(parseFloat((data.success + data.failure + data.killed - data.rerun) / (data.success + data.failure + data.killed)) * 100);
                            if (isNaN(job_rerun)) {
                                $("#progress_rerun").attr("data-original-title", 0).css("width", "0%");
                            } else {
                                $("#progress_rerun").attr("data-original-title", job_rerun+"%").css("width", job_rerun + "%");
                            }

                            var job_status = parseInt(parseFloat(data.success / (data.success + data.failure + data.killed)) * 100);
                            if (isNaN(job_status)) {
                                $("#progress_status").attr("data-original-title", 0).css("width", "0%");
                            } else {
                                $("#progress_status").attr("data-original-title", job_status+"%").css("width", job_status + "%");
                            }

                        }else {
                            $(".opencron-progress").remove();
                        }
                    }
                });
            }

            var skin = $.cookie("opencron_skin");
            if(skin) {
                $('body').attr('id', skin);
            }

            $('body').on('click', '.template-skins > a', function(e){
                e.preventDefault();
                var skin = $(this).data('skin');
                $('body').attr('id', skin);
                $('#changeSkin').modal('hide');
                $.cookie("opencron_skin", skin, {
                    expires : 30,
                    domain:document.domain,
                    path:"/"
                });
            });


            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url: "${contextPath}/notice/uncount.do",
                dataType: "JSON",
                success: function (data) {
                    if (data){
                        $(".n-count").text(data);
                        $("#msg-icon").show();
                        $.ajax({
                            headers:{"csrf":"${csrf}"},
                            type:"POST",
                            url: "${contextPath}/notice/unread.htm",
                            dataType: "html",
                            success: function (data) {
                                $("#msgList").html(data);
                            }
                        });
                    }else {
                        $("#messages").remove();
                        $(".n-count").remove();
                        $("#toggle_message").css({"padding":"10px 0px 0"});
                        $("#msg-icon").click(function () {
                            window.location.href="${contextPath}/notice/view.htm?csrf=${csrf}";
                        })
                        $("#msg-icon").show();
                    }
                }
            });
        });
    </script>

</head>

<body id="skin-blur-ocean">

    <div id="mask" class="mask"></div>

    <header id="header">
        <a href="" id="menu-toggle" style="background-image: none"><i class="icon">&#61773;</i></a>
        <a id="log1" href="${contextPath}/dashboard.htm?csrf=${csrf}" class="logo pull-left"><div style="float: left; width: 165px; margin-top: 5px; margin-left: 14px">
            <img src="${contextPath}/static/img/opencron.png">
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
                    <form class="avatar-form" name="picform" action="${contextPath}/headpic/upload.do" enctype="multipart/form-data" method="post">
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input name="userId" type="hidden" value="${opencron_user.userId}">
                        <div class="modal-header">
                            <button class="close btn-float" style="margin-top:-7px!important;margin-right:-11px;" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                            <h4 class="modal-title" id="avatar-modal-label">更改图像</h4>
                        </div>
                        <div class="modal-body">
                            <div class="avatar-body">

                                <!-- Upload image and data -->
                                <div class="avatar-upload">
                                    <input class="avatar-src" name="src" type="hidden">
                                    <input class="avatar-data" name="data" type="hidden">

                                    <button class="btn btn-default" onclick="document.picform.file.click()">请选择本地照片</button>
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

        <jsp:include page="/WEB-INF/layouts/menu.jsp"/>

        <sitemesh:write property='body' />

        <!-- Older IE Message -->
        <!--[if lt IE 9]>
        <div class="ie-block">
            <h1 class="Ops">Ooops!</h1>
            <p> 您使用的浏览器太老啦，升级到下列任何一个最新浏览器，以便您愉快的访问该网站。 </p>
            <ul class="browsers">
                <li>
                    <a href="https://www.google.com/intl/en/chrome/browser/">
                        <img src="${contextPath}/static/img/browsers/chrome.png" alt="">
                        <div>Google Chrome</div>
                    </a>
                </li>
                <li>
                    <a href="http://www.mozilla.org/en-US/firefox/new/">
                        <img src="${contextPath}/static/img/browsers/firefox.png" alt="">
                        <div>Mozilla Firefox</div>
                    </a>
                </li>
                <li>
                    <a href="http://www.opera.com/computer/windows">
                        <img src="${contextPath}/static/img/browsers/opera.png" alt="">
                        <div>Opera</div>
                    </a>
                </li>
                <li>
                    <a href="http://safari.en.softonic.com/">
                        <img src="${contextPath}/static/img/browsers/safari.png" alt="">
                        <div>Safari</div>
                    </a>
                </li>
                <li>
                    <a href="http://windows.microsoft.com/en-us/internet-explorer/downloads/ie-10/worldwide-languages">
                        <img src="${contextPath}/static/img/browsers/ie.png" alt="">
                        <div>Internet Explorer(New)</div>
                    </a>
                </li>
            </ul>
            <p>请升级您的浏览器以便带来更好的用户体验 <br/>谢谢...</p>
        </div>
        <![endif]-->

    </section>

</body>

</html>


