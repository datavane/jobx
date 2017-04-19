<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!--base-->
    <link rel="shortcut icon" href="${contextPath}/img/terminal.png" />
    <script type="text/javascript" src="${contextPath}/js/jquery.min.js?resId=${resourceId}"></script> <!-- jQuery Library -->
    <link rel="stylesheet" href="${contextPath}/css/font-awesome.css?resId=${resourceId}" >
    <link rel="stylesheet" href="${contextPath}/css/font-awesome-ie7.min.css?resId=${resourceId}" >
    <link rel="stylesheet" href='${contextPath}/css/sweetalert.css?resId=${resourceId}' >
    <script type="text/javascript" src="${contextPath}/js/sweetalert.min.js?resId=${resourceId}"></script>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="${contextPath}/css/bootstrap.css?resId=${resourceId}" >
    <script type="text/javascript" src="${contextPath}/js/bootstrap.js?resId=${resourceId}"></script>

    <!--fileinput-->
    <link href="${contextPath}/js/fileinput/css/fileinput.css" media="all" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${contextPath}/js/fileinput/js/fileinput.js?resId=${resourceId}" ></script>
    <script type="text/javascript" src="${contextPath}/js/fileinput/js/locales/zh.js?resId=${resourceId}"></script>
    <link rel="stylesheet" href="${contextPath}/css/opencron.term.css?resId=${resourceId}" >

    <!--term-->
    <link type="text/css" rel="stylesheet" href="${contextPath}/js/xterm/xterm.css?resId=${resourceId}">
    <link type="text/css" rel="stylesheet" href="${contextPath}/js/xterm/addons/fullscreen/fullscreen.css?resId=${resourceId}" />

    <script src="${contextPath}/js/xterm/xterm.js?resId=${resourceId}" type="text/javascript"></script>
    <script src="${contextPath}/js/xterm/addons/attach/attach.js?resId=${resourceId}" type="text/javascript"></script>
    <script src="${contextPath}/js/xterm/addons/fit/fit.js?resId=${resourceId}" type="text/javascript"></script>
    <script src="${contextPath}/js/xterm/addons/fullscreen/fullscreen.js?resId=${resourceId}" type="text/javascript"></script>
    <script src="${contextPath}/js/opencron.term.js?resId=${resourceId}" type="text/javascript" ></script>
    <script type="text/javascript" src="${contextPath}/js/opencron.js?resId=${resourceId}"></script>

    <title>opencron Terminal</title>
</head>

<body>

<div id="appbar" class="navbar navbar-default" role="navigation">
    <div class="container">
        <div>
            <ul class="nav navbar-nav">
                <li><a class="term-logo" href="${contextPath}" target="_blank" title="">opencron</a></li>

                <li class="dropdown">
                    <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" title="常用操作"><i aria-hidden="true" class="fa fa-server"></i>&nbsp;操作<b class="caret"></b></a>
                    <ul class="dropdown-menu" >
                        <li><a href="${contextPath}/terminal/reopen?token=${token}&csrf=${csrf}" target="_blank" title="克隆会话">&nbsp;克隆会话</a></li>
                        <li><a href="javascript:upload()" title="上传文件">&nbsp;上传文件</a></li>
                    </ul>
                </li>

                <li class="dropdown">
                    <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" title="打开终端"><i aria-hidden="true" class="fa fa-folder-open-o"></i>&nbsp;打开<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <c:forEach var="t" items="${terms}">
                            <li><a href="${contextPath}/terminal/ssh2?id=${t.id}&csrf=${csrf}" target="_blank">${t.name}(${t.host})</a></li>
                        </c:forEach>
                    </ul>
                </li>

                <li class="dropdown">
                    <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" title="主题"><i aria-hidden="true" class="fa fa-gear"></i>&nbsp;主题<b class="caret"></b></a>
                    <ul class="dropdown-menu theme" >
                        <li><a theme="default" href="javascript:void(0)"><span class="circle" style="background-color:#dddddd"></span>&nbsp;默认</a></li>
                        <li><a theme="gray" href="javascript:void(0)"><span class="circle" style="background-color:gray"></span>&nbsp;灰色</a></li>
                        <li><a theme="green" href="javascript:void(0)"><span class="circle" style="background-color:green"></span>&nbsp;绿色</a></li>
                        <li><a theme="black" href="javascript:void(0)"><span class="circle" style="background-color:black"></span>&nbsp;黑色</a></li>
                    </ul>
                </li>

                <li><a href="javascript:closeTerminal();" title="退出终端" data-toggle="tooltip"><i aria-hidden="true" class="fa fa-power-off"></i>&nbsp;退出</a></li>

                <li id="sendNode" style="padding-top: 11px;margin-left: 18px;">
                    <label style="color:#777;font-weight: normal; "><i aria-hidden="true" class="fa fa-send"></i>&nbsp;<span id="sendLabel">命令</span></label>&nbsp;&nbsp;<input id="sendInput" class="send-input" size="30" placeholder="发送到所有终端会话请在这里输入" type="text">
                    &nbsp;<div class="btn btn-success btn-sm" id="sendBtn">发送</div>
                </li>
                <li style="float: right;margin-right: 15px;"><a href="https://github.com/wolfboys/opencron" target="_blank"><i aria-hidden="true" class="fa fa-github" style="font-size:35px;position:absolute;top:8px"></i></a></li>
            </ul>
        </div>
    </div>
</div>

<div id="terminal-container"></div>

<div id="upload_push" class="modal fade" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">文件上传</h4>
            </div>
            <div class="modal-body">
                <form enctype="multipart/form-data">
                    <div class="input-group" style="padding-bottom:12px">
                        <span class="input-group-addon">上传路径</span>
                        <input type="text" id="path" class="form-control col-lg-13" placeholder="请输入文件上传路径,默认在当前终端所在的路径下">
                    </div>
                    <input id="file" name="file" type="file">
                    <div id="errorBlock" class="help-block"></div>
                </form>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    $(document).ready(function () {
       document.title = unEscapeHtml('${name}');
        opencronTerm =  new OpencronTerm('${token}','${csrf}','${theme}');
        //去掉a点击时的虚线框
        $(".container").find("a").focus(function () {
            this.blur();
        })

        $(".theme").find("a").click(function () {
            opencronTerm.theme($(this).attr("theme"));
        });

        $("#terminal-container").css({
            "padding-top":$("#appbar").outerHeight()+5+"px"
        });

        $('#file').fileinput({
            language: 'zh',
            showPreview : true,
            browseOnZoneClick:false,
            uploadUrl : '${contextPath}/terminal/upload',
            removeLabel : "删除",
            showCaption: true, //是否显示标题,
            dropZoneEnabled:true,
            dropZoneTitle:"拖拽文件到这里来上传...",
            resizeImage: false,
            previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
            initialCaption: "请选择要上传的文件",
            maxFileSize:104857600,//文件最大100M
            allowedFileExtensions : null,
            elErrorContainer: '#errorBlock',
            uploadExtraData: function() {
                var obj = {};
                obj.token = '${token}';
                obj.path = $("#path").val();
                return obj;
            }
        }).on("fileuploaded", function(event, data) {
            if (!data.response) {
                alert('文件格式类型不正确');
            }else if (data.response.success == "false") {
               window.setTimeout(function () {
                   $("#upload_push").find(".progress-bar-success").addClass("progress-bar-danger").removeClass("progress-bar-success");
                   $(".file-actions").find(".glyphicon-ok-sign").addClass("text-danger glyphicon-remove-sign").removeClass("glyphicon-ok-sign text-success");
                   var uperrorhtml = '<span class="close kv-error-close" onclick="colseUpError()">×</span><ul><li><b>上传失败: (可能有以下原因)</b></li>' +
                       '<li><b>1): </b>文件大小太大,上传失败</li>' +
                       '<li><b>2): </b>上传路径是当前路径,且控制台正在实时输出日志(无法获取当前路径)</li>' +
                       '</ul>';
                   $(".file-error-message").css({"margin":"8px 0px"});
                    $("#errorBlock").html(uperrorhtml).show();
               },300);
            }
        });

    });

    function colseUpError() {
        $("#errorBlock").empty().hide();
    }

    function upload() {
        $("#upload_push").modal('show');
        $(".fileinput-remove-button").click();
        $("#path").val('');
    }

    function closeTerminal() {
        swal({
            title: "",
            text: "您确定要退出终端吗？",
            type: "warning",
            showCancelButton: true,
            closeOnConfirm: false,
            confirmButtonText: "退出"
        }, function() {
            window.close();
        });
    }

</script>

</body>
</html>