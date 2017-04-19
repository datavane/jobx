<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <style type="text/css">
        .error_msg {
            color: red;
            margin-top: 5px;
            font-size: 12px;
        }
    </style>

    <script type="text/javascript">

        $(document).ready(function () {
            $("#size").change(function () {
                var pageSize = $("#size").val();
                window.location.href="${contextPath}/terminal/view?pageNo=${pageBean.pageNo}&pageSize="+pageSize+"&orderBy=${pageBean.orderBy}&order=${pageBean.order}&csrf=${csrf}";
            });
        })

        function ssh(id, type) {
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/terminal/ssh",
                data: "id=" + id,
                dataType: "html",
                success: function (data) {
                    if(data.indexOf("login")>-1){
                        window.location.href="${contextPath}";
                    }
                    var json = eval("(" + data + ")");
                    if (json.status == "authfail" || json.status == "keyauthfail") {
                        if (type == 2) {
                            alert("用户名密码错误,登录失败");
                        } else {
                            editSsh(id,0);
                        }
                    } else if (json.status == "hostfail") {
                        alert("DNS解析失败");
                    } else if (json.status == "genericfail") {
                        alert("连接失败请重试");
                    } else if (json.status == "success") {
                        var url = '${contextPath}' + json.url;
                        swal({
                            title: "",
                            text: "登陆成功,您确定要打开终端吗？",
                            type: "warning",
                            showCancelButton: true,
                            closeOnConfirm: false,
                            confirmButtonText: "打开"
                        });

                        /**
                         *
                         * 默认打开新的弹窗浏览器会阻止,有的浏览器如Safair连询问用户是否打开新窗口的对话框都没有.
                         * 这里页面自己弹出询问框,当用户点击"打开"产生了真正的点击行为,然后利用事件冒泡就触发了包裹它的a标签,使得可以在新窗口打开a标签的连接
                         *
                         */
                        if ($("#openLink").length == 0) {
                            $(".sweet-alert").find(".confirm").wrap("<a id='openLink' href='" + url + "'  target='_blank'/></a>");
                        } else {
                            $("#openLink").attr("href", url);
                        }

                        $("#openLink").click(function () {

                            $("div[class^='sweet-']").remove();

                            //更改最后登录日期
                            window.setTimeout(function(){
                                $.ajax({
                                    headers:{"csrf":"${csrf}"},
                                    type: "POST",
                                    url: "${contextPath}/terminal/detail",
                                    data: "id="+id,
                                    dataType: "JSON",
                                    success: function (json) {
                                        $("#time_"+id).text(json.logintime);
                                    }
                                })
                            },5000);

                        });

                        $(".sweet-alert").find(".cancel").click(function () {
                            window.setTimeout(function () {
                                $("div[class^='sweet-']").remove();
                            }, 500);
                        });
                    }
                }
            });
        }

        function editSsh(id,type) {
            $(".error_msg").empty();
            if (type == 1) {
                $("#sshform").attr("action","edit");
                $("#sshTitle").text("编辑终端");
                $("#sshbtn").text("保存");
            }else {
                $("#sshform").attr("action","login");
                $("#sshTitle").text("登陆终端");
                $("#sshbtn").text("登陆");
            }
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type: "POST",
                url: "${contextPath}/terminal/detail",
                data: "id="+id,
                dataType: "json",
                success: function (json) {
                    $("#sshid").val(id);
                    $("#sshuser").val(json.user);
                    $("#sshname").val(unEscapeHtml(json.name));
                    $("#sshport").val(json.port);
                    $("#sshhost").val(json.host).attr("readonly","readonly");
                    $("#sshuser")[0].focus();
                    $("#sshModal").modal("show");
                }
            });
        }


        function del(id) {
            swal({
                title: "",
                text: "您确定要删除该终端吗?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "删除"
            },function () {
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/terminal/del",
                    data: "id="+id,
                    dataType: "html",
                    success: function (message) {
                        if (message == "success") {
                            alertMsg("删除成功!")
                            $("#tr_" + id).remove();
                        }else {
                            alert("删除失败!")
                        }
                    }
                });
            });
        }

        function saveSsh() {

            $(".error_msg").empty();

            var user = $("#sshuser").val();
            var name = $("#sshname").val();
            var pwd = $("#sshpwd").val();
            var port = $("#sshport").val();
            var host = $("#sshhost").val();
            var falg = true;

            if(!name){
                $("#sshname_lab").text("终端实例名称不能为空");
                falg = false;
            }else {
                if (name.length>20){
                    $("#sshname_lab").text("终端实例名称输入太长不合法");
                    falg = false;
                }
            }

            if(!host) {
                $("#sshhost_lab").text("机器地址不能为空");
                falg = false;
            }else {
                var reg = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
                //验证是否为网址
                var objExp=new RegExp(reg);
                if(!objExp.test(host)){
                    //验证是否为IP
                    reg = /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/;
                    if (!reg.test(host)) {
                        $("#sshhost_lab").text("机器地址不合法,必须你网址或者IP");
                        falg = false;
                    }
                }
            }

            if(!port){
                $("#sshport_lab").text("连接端口不能为空");
                falg = false;
            }else {
                if(isNaN(port)){
                    $("#sshport_lab").text("连接端口输入不合法,必须为数字");
                    falg = false;
                }else {
                    port = parseInt(port);
                    if (port<0){
                        $("#sshport_lab").text("连接端口输入不合法,不能为负数");
                        falg = false;
                    }else if(port>65535){
                        $("#sshport_lab").text("连接端口输入不合法,不能超过65535");
                        falg = false;
                    }
                }
            }

            if(!user){
                $("#sshuser_lab").text("登陆账号不能为空");
                falg = false;
            }else {
                if(user.length>255){
                    $("#sshuser_lab").text("登陆账号太长,不合法");
                    falg = false;
                }
            }

            if(!pwd){
                $("#sshpwd_lab").text("登陆密码不能为空");
                falg = false;
            }

            if (!falg) return;

            var host = $("#sshhost").val();

            var action = $("#sshform").attr("action");

            if (action == "add") {
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/terminal/exists",
                    data: {
                        "host":host
                    },
                    dataType: "html",
                    success: function (status) {
                        if(status=="false"){
                            $.ajax({
                                headers:{"csrf":"${csrf}"},
                                type: "POST",
                                url: "${contextPath}/terminal/save",
                                data: {
                                    "name":name,
                                    "userName": user,
                                    "password": pwd,
                                    "port": port,
                                    "host": host
                                },
                                dataType: "html",
                                success: function (status) {
                                    $("#sshModal").modal("hide");
                                    $("#sshform")[0].reset();
                                    if (status == "success") {
                                        alertMsg("恭喜你添加终端成功!");
                                        location.reload();
                                    } else {
                                        alert("用户名密码错误,添加终端失败");
                                    }
                                }
                            });
                        }else {
                            alert("添加终端失败,该机器终端实例已存在!");
                        }
                    }
                });
            }else {
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type: "POST",
                    url: "${contextPath}/terminal/save",
                    data: {
                        "id":$("#sshid").val(),
                        "name":name,
                        "userName": user,
                        "password": pwd,
                        "port": port,
                        "host": host
                    },
                    dataType: "html",
                    success: function (status) {
                        $("#sshModal").modal("hide");
                        $("#sshform")[0].reset();
                        if(action == "login") {
                            if (status == "success") {
                                ssh($("#sshid").val(), 0);
                            }else {
                                alert("用户名密码错误,登陆终端失败!");
                            }
                        }else {
                            if (status == "success") {
                                alertMsg("恭喜你修改终端成功!");
                                location.reload();
                            } else {
                                alert("用户名密码错误,修改终端失败");
                            }
                        }
                    }
                });
            }

        }

        function addSSH() {
            $(".error_msg").empty();
            $("#sshform")[0].reset();
            $("#sshform").attr("action","add");
            $("#sshhost").removeAttr("readonly");
            $("#sshTitle").text("添加终端");
            $("#sshbtn").text("保存");
            $("#sshid").empty();
            $("#sshModal").modal("show");

        }

        function sortPage(field) {
            location.href="${contextPath}/terminal/view?pageNo=${pageBean.pageNo}&pageSize=${pageBean.pageSize}&orderBy="+field+"&order="+("${pageBean.order}"=="asc"?"desc":"asc")+"&csrf=${csrf}";
        }

    </script>

    <style type="text/css">
        .visible-md i {
            font-size: 15px;
        }
    </style>

</head>
<jsp:include page="/WEB-INF/common/top.jsp"/>

<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/common/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">opencron</a></li>
        <li><a href="">WEB终端</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-terminal" aria-hidden="true"></i>&nbsp;WEB终端&nbsp;&nbsp;</h4>
    <div class="block-area" id="defaultStyle">
        <div>
            <div style="float: left">
                <label>
                    每页 <select size="1" class="select-self" id="size" style="width: 50px;margin-bottom: 8px">
                    <option value="15">15</option>
                    <option value="30" ${pageBean.pageSize eq 30 ? 'selected' : ''}>30</option>
                    <option value="50" ${pageBean.pageSize eq 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${pageBean.pageSize eq 100 ? 'selected' : ''}>100</option>
                </select> 条记录
                </label>
            </div>
            <c:if test="${permission eq true}">
                <div style="float: right;margin-top: -10px">
                    <a href="javascript:addSSH();" class="btn btn-sm m-t-10"
                       style="margin-left: 50px;margin-bottom: 8px"><i class="icon">&#61943;</i>添加</a>
                </div>
            </c:if>
        </div>

        <table class="table tile textured table-custom table-sortable">
            <thead>
            <tr>
                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'name'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-alpha sort-asc" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">实例名称</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-alpha sort-desc" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">实例名称</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'name'}">
                        <th  class="sortable sort-alpha" style="cursor: pointer" onclick="sortPage('name')" title="点击排序">实例名称</th>
                    </c:when>
                </c:choose>

               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'host'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机地址</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机地址</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'host'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('host')" title="点击排序">主机地址</th>
                   </c:when>
               </c:choose>

               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'port'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'port'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('port')" title="点击排序">SSH端口</th>
                   </c:when>
               </c:choose>
               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'logintime'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="sortPage('logintime')" title="点击排序">最后登陆</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="sortPage('logintime')" title="点击排序">最后登陆</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'logintime'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="sortPage('logintime')" title="点击排序">最后登陆</th>
                   </c:when>
               </c:choose>
                <th>
                    <center>操作</center>
                </th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="t" items="${pageBean.result}" varStatus="index">
                <tr id="tr_${t.id}">
                    <td id="name_${t.id}">${t.name}</td>
                    <td>${t.host}</td>
                    <td>${t.port}</td>
                    <td id="time_${t.id}"><fmt:formatDate value="${t.logintime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                        <center>
                            <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                <a href="javascript:ssh('${t.id}')" title="登录">
                                    <i aria-hidden="true" class="fa fa-tv"></i>
                                </a>&nbsp;&nbsp;
                                <a href="javascript:editSsh('${t.id}',1)" title="编辑">
                                    <i aria-hidden="true" class="fa fa-edit"></i>
                                </a>&nbsp;&nbsp;
                                <a href="javascript:del('${t.id}')" title="删除">
                                    <i aria-hidden="true" class="fa fa-remove"></i>
                                </a>&nbsp;&nbsp;
                            </div>
                        </center>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <cron:pager href="${contextPath}/terminal/view?csrf=${csrf}" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>

    </div>

    <!-- 修改密码弹窗 -->
    <div class="modal fade" id="sshModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 id="sshTitle">SSH登录</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="sshform">
                        <input type="hidden" id="sshid">
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="sshname" class="col-lab control-label"><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;名&nbsp;&nbsp;称&nbsp;&nbsp;：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshname"
                                       placeholder="请输入终端的实例名称">&nbsp;&nbsp;<label class="error_msg" id="sshname_lab"></label>
                            </div>
                        </div>

                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="sshhost" class="col-lab control-label"><i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;地&nbsp;&nbsp;址&nbsp;&nbsp;：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshhost"
                                       placeholder="请输入主机地址(IP)">&nbsp;&nbsp;<label class="error_msg" id="sshhost_lab"></label>
                            </div>
                        </div>


                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="sshport" class="col-lab control-label"><i class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;端&nbsp;&nbsp;口&nbsp;&nbsp;：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshport"
                                       placeholder="请输入端口">&nbsp;&nbsp;<label class="error_msg" id="sshport_lab"></label>
                            </div>
                        </div>

                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="sshuser" class="col-lab control-label"><i class="glyphicon glyphicon-user"></i>&nbsp;&nbsp;帐&nbsp;&nbsp;号&nbsp;&nbsp;：</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshuser"
                                       placeholder="请输入账户">&nbsp;&nbsp;<label class="error_msg" id="sshuser_lab"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="sshpwd" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;密&nbsp;&nbsp;码&nbsp;&nbsp;：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="sshpwd" placeholder="请输入密码"/>&nbsp;&nbsp;<label class="error_msg" id="sshpwd_lab"></label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" id="sshbtn" onclick="saveSsh()">保存</button>
                        &nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
