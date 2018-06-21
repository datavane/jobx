<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.jobx.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<!DOCTYPE html>
<html lang="en">
<head>
    <style type="text/css">
        .error_msg {
            color: red;
            margin-top: 5px;
            font-size: 12px;
        }
        .visible-md i {
            font-size: 15px;
        }
    </style>

    <script type="text/javascript">

        var page = {
            ssh:function (id,msg,failCallback) {
                ajax({
                    url: "${contextPath}/terminal/ssh.do",
                    type: "post",
                    data: {"id":id}
                },function (json) {
                    if(json&&json.toString().indexOf("login")>-1){
                        window.location.href="${contextPath}";
                    }
                    if ( json.status != "success" ) {
                        if (!failCallback) {
                            if (json.status == "authfail" || json.status == "keyauthfail") {
                                alert("用户名密码错误,登录失败");
                            } else if (json.status == "hostfail") {
                                alert("DNS解析失败");
                            } else if (json.status == "genericfail") {
                                alert("连接失败请重试");
                            }
                        } else {
                            failCallback();
                        }
                    } else {
                        var url = '${contextPath}' + json.url;
                        swal({
                            title: "",
                            text: msg?msg:"登陆成功,您确定要打开终端吗？",
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
                                    url: "${contextPath}/terminal/detail.do",
                                    type: "post",
                                    data: {"id":id},
                                    dataType: "json"
                                }).done(function (json) {
                                    $("#time_"+id).text(json.loginTime);
                                })
                            },5000);

                        });

                        $(".sweet-alert").find(".cancel").click(function () {
                            window.setTimeout(function () {
                                $("div[class^='sweet-']").remove();
                            }, 50);
                        });
                    }
                });

            },
            add:function () {
                $(".error_msg").empty();
                $("#sshform")[0].reset();
                $("#sshform").attr("action","add");
                $("#sshhost").removeAttr("readonly");
                $("#sshTitle").text("添加终端");
                $("#sshbtn").text("保存");
                $("#sshid").empty();
                $("#sshModal").modal("show");
            },
            edit:function (id) {
                $(".error_msg").empty();
                $("#sshform").attr("action","edit");
                $("#sshTitle").text("编辑终端");
                $("#sshbtn").text("保存");

                ajax({
                    url: "${contextPath}/terminal/detail.do",
                    type: "post",
                    data: {"id":id}
                },function (json) {
                    $("#sshid").val(id);
                    $("#sshuser").val(json.user);
                    $("#sshname").val(unEscapeHtml(json.name));
                    $("#sshport").val(json.port);
                    $("#sshhost").val(json.host).attr("readonly","readonly");
                    $("#sshtype").val(json.sshType);
                    $(".nav-tabs [type="+json.sshType+"]").tab("show");
                    $("#sshuser")[0].focus();
                    $("#sshModal").modal("show");
                });
            },
            remove:function (id) {
                swal({
                    title: "",
                    text: "您确定要删除该终端吗?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "删除"
                },function () {
                    ajax({
                        url: "${contextPath}/terminal/delete.do",
                        type: "post",
                        data: {"id":id}
                    },function (data) {
                        if (data) {
                            alertMsg("删除成功!");
                            $("#tr_" + id).remove();
                        }else {
                            alert("删除失败!")
                        }
                    })
                });
            },
            save:function () {
                $(".error_msg").empty();

                var falg = true;

                var name = $("#sshname").val();
                var user = $("#sshuser").val();
                var pwd =  $("#sshpwd").val();
                var port = $("#sshport").val();
                var host = $("#sshhost").val();
                var sshtype = $("#sshtype").val();

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

                if (sshtype == 1 ) {
                    if($.trim($("#keyfile").val())==''){
                        $("#keyfile_lab").text("请上传SSH KEY文件");
                        return false;
                    }
                }else {
                    if (!pwd) {
                        $("#sshpwd_lab").text("登陆密码不能为空");
                        falg = false;
                    }
                }

                if (!falg) return;

                var host = $("#sshhost").val();

                var action = $("#sshform").attr("action");
                var formData = new FormData();
                formData.append("name",name);
                formData.append("userName",user);
                formData.append("sshType",1);
                formData.append("port",port);
                formData.append("host",host);
                formData.append("phrase",$("#passphrase").val());
                formData.append("sshkey",$('input[name=sshkey]')[0].files[0]);

                var callback = function (data,opt) {
                    $("#sshModal").modal("hide");
                    $("#sshform")[0].reset();
                    if (data == "success") {
                        alertMsg("恭喜你"+opt+"终端成功!");
                        setTimeout(function () {
                            location.reload();
                        },1000);
                    } else {
                        alert("用户名密码错误,"+opt+"终端失败");
                    }
                };

                if (action == "add") {
                    ajax({
                        url: "${contextPath}/terminal/exists.do",
                        type: "post",
                        data: {
                            "userName":user,
                            "host":host
                        }
                    },function (status) {
                        if(!status) {
                            if (sshtype == 0){
                                ajax({
                                    url: "${contextPath}/terminal/save.do",
                                    type: "post",
                                    data:{
                                        "name":name,
                                        "userName": user,
                                        "sshType":sshtype,
                                        "password": toBase64(pwd),
                                        "port": port,
                                        "host": host
                                    }
                                },function (data) {
                                    callback(data,"添加")
                                })
                            }else {
                                $.ajax({
                                    url: "${contextPath}/terminal/save.do",
                                    type: "post",
                                    data: formData,
                                    processData: false,
                                    contentType:false,
                                    success:function (data) {
                                        callback(data,"添加");
                                    }
                                })
                            }
                        }else {
                            alert("添加终端失败,该机器终端实例已存在!");
                        }
                    });
                }else {
                    if (sshtype == 0){
                        ajax({
                            url: "${contextPath}/terminal/save.do",
                            type: "post",
                            data:{
                                "id":$("#sshid").val(),
                                "name":name,
                                "sshType":0,
                                "userName": user,
                                "password": toBase64(pwd),
                                "port": port,
                                "host": host
                            }
                        },function (data) {
                            callback(data,"修改");
                        });
                    }else {
                        formData.append("id",$("#sshid").val());
                        $.ajax({
                            url: "${contextPath}/terminal/save.do",
                            type: "post",
                            data: formData,
                            processData: false,
                            contentType:false,
                            success:function (data) {
                                callback(data,"修改");
                            }
                        })
                    }
                }
            },
            sort:function (field) {
                location.href="${contextPath}/terminal/view.htm?pageNo=${pageBean.pageNo}&pageSize=${pageBean.pageSize}&orderBy="+field+"&order="+("${pageBean.order}"=="asc"?"desc":"asc");
            }
        }

        $(document).ready(function () {
            $("#size").change(function () {
                var pageSize = $("#size").val()||${pageBean.pageSize};
                window.location.href="${contextPath}/terminal/view.htm?pageNo=${pageBean.pageNo}&pageSize="+pageSize+"&orderBy=${pageBean.orderBy}&order=${pageBean.order}";
            });

            $(".sshtype").find("a").click(function () {
                $("#sshtype").val($(this).attr("type"));
                $(".error_msg").empty();
            });

            $(".sshlink").click(function () {
                var id = $(this).attr("ssh");
                page.ssh(id,null,function () {
                    swal({
                        title: "",
                        text: "登录失败,请重新设置用户名密码",
                        type: "warning",
                        showCancelButton: true,
                        closeOnConfirm: true,
                        confirmButtonText:"设置"
                    }, function () {
                        page.edit(id);
                    });
                })

            });
        });

    </script>
</head>

<body>

<!-- Content -->
<section id="content" class="container">

    <!-- Messages Drawer -->
    <jsp:include page="/WEB-INF/layouts/message.jsp"/>

    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
        <li class="icon">&#61753;</li>
        当前位置：
        <li><a href="">JobX</a></li>
        <li><a href="">WEB终端</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-terminal" aria-hidden="true"></i>&nbsp;WEB终端&nbsp;&nbsp;</h4>
    <div class="block-area" id="defaultStyle">
        <div>
            <c:if test="${permission eq true}">
                <div style="float: right;margin-top: -10px">
                    <a href="javascript:page.add();" class="btn btn-sm m-t-10"
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
                            <th  class="sortable sort-alpha sort-asc" style="cursor: pointer" onclick="page.sort('name')" title="点击排序">实例名称</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-alpha sort-desc" style="cursor: pointer" onclick="page.sort('name')" title="点击排序">实例名称</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'name'}">
                        <th  class="sortable sort-alpha" style="cursor: pointer" onclick="page.sort('name')" title="点击排序">实例名称</th>
                    </c:when>
                </c:choose>

               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'host'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="page.sort('host')" title="点击排序">主机地址</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="page.sort('host')" title="点击排序">主机地址</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'host'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="page.sort('host')" title="点击排序">主机地址</th>
                   </c:when>
               </c:choose>

               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'port'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="page.sort('port')" title="点击排序">SSH端口</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="page.sort('port')" title="点击排序">SSH端口</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'port'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="page.sort('port')" title="点击排序">SSH端口</th>
                   </c:when>
               </c:choose>
                <c:choose>
                    <c:when test="${pageBean.orderBy eq 'ssh_type'}">
                        <c:if test="${pageBean.order eq 'asc'}">
                            <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="page.sort('ssh_type')" title="点击排序">登录方式</th>
                        </c:if>
                        <c:if test="${pageBean.order eq 'desc'}">
                            <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="page.sort('ssh_type')" title="点击排序">登录方式</th>
                        </c:if>
                    </c:when>
                    <c:when test="${pageBean.orderBy ne 'ssh_type'}">
                        <th  class="sortable sort-numeric" style="cursor: pointer" onclick="page.sort('ssh_type')" title="点击排序">登录方式</th>
                    </c:when>
                </c:choose>
               <c:choose>
                   <c:when test="${pageBean.orderBy eq 'login_time'}">
                       <c:if test="${pageBean.order eq 'asc'}">
                           <th  class="sortable sort-numeric sort-asc" style="cursor: pointer" onclick="page.sort('login_time')" title="点击排序">最后登陆</th>
                       </c:if>
                       <c:if test="${pageBean.order eq 'desc'}">
                           <th  class="sortable sort-numeric sort-desc" style="cursor: pointer" onclick="page.sort('login_time')" title="点击排序">最后登陆</th>
                       </c:if>
                   </c:when>
                   <c:when test="${pageBean.orderBy ne 'login_time'}">
                       <th  class="sortable sort-numeric" style="cursor: pointer" onclick="page.sort('login_time')" title="点击排序">最后登陆</th>
                   </c:when>
               </c:choose>
                <th class="text-center">操作</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="t" items="${pageBean.result}" varStatus="index">
                <tr id="tr_${t.id}">
                    <td id="name_${t.id}">${t.name}</td>
                    <td>${t.host}</td>
                    <td>${t.port}</td>
                    <td>
                        <c:if test="${t.sshType eq 0}">
                            <span class="label label-success">&nbsp;&nbsp;账&nbsp;号&nbsp;密&nbsp;码&nbsp;&nbsp;</span>
                        </c:if>
                        <c:if test="${t.sshType eq 1}">
                            <span class="label label-warning">&nbsp;&nbsp;&nbsp;Private&nbsp;&nbsp;Key&nbsp;&nbsp;&nbsp;</span>
                        </c:if>
                    </td>
                    <td id="time_${t.id}"><fmt:formatDate value="${t.loginTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td class="text-center">
                            <div class="visible-md visible-lg hidden-sm hidden-xs action-buttons">
                                <a class="sshlink" ssh="${t.id}" href="javascript:void(0);" title="登录">
                                    <i aria-hidden="true" class="fa fa-tv"></i>
                                </a>&nbsp;&nbsp;
                                <a href="javascript:page.edit('${t.id}')" title="编辑">
                                    <i aria-hidden="true" class="fa fa-edit"></i>
                                </a>&nbsp;&nbsp;
                                <a href="javascript:page.remove('${t.id}')" title="删除">
                                    <i aria-hidden="true" class="fa fa-remove"></i>
                                </a>&nbsp;&nbsp;
                            </div>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <cron:pager href="${contextPath}/terminal/view.htm" id="${pageBean.pageNo}" size="${pageBean.pageSize}" total="${pageBean.totalCount}"/>

    </div>

    <!-- 修改密码弹窗 -->
    <div class="modal fade" id="sshModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                    <h4 id="sshTitle">SSH登录</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="sshform">
                        <input type="hidden" id="sshid">
                        <div class="form-group">
                            <label for="sshname" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;&nbsp;名&nbsp;&nbsp;&nbsp;称&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshname" name="name"
                                       placeholder="请输入终端的实例名称">&nbsp;&nbsp;<label class="error_msg" id="sshname_lab"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="sshhost" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-tag"></i>&nbsp;&nbsp;&nbsp;地&nbsp;&nbsp;&nbsp;址&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshhost"
                                       placeholder="请输入主机地址(IP)">&nbsp;&nbsp;<label class="error_msg" id="sshhost_lab"></label>
                            </div>
                        </div>

                        <div class="form-group" >
                            <label for="sshport" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-question-sign"></i>&nbsp;&nbsp;&nbsp;端&nbsp;&nbsp;&nbsp;口&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshport"
                                       placeholder="请输入端口">&nbsp;&nbsp;<label class="error_msg" id="sshport_lab"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="sshuser" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-user"></i>&nbsp;&nbsp;&nbsp;帐&nbsp;&nbsp;&nbsp;号&nbsp;&nbsp;</label>
                            <div class="col-md-9">
                                <input type="text" class="form-control " id="sshuser"
                                       placeholder="请输入账户">&nbsp;&nbsp;<label class="error_msg" id="sshuser_lab"></label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="sshhost" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-transfer"></i>&nbsp;&nbsp;&nbsp;方&nbsp;&nbsp;&nbsp;式&nbsp;&nbsp;</label>
                            <div class="col-md-9 sshtype">
                                <ul class="nav nav-tabs">
                                    <input type="hidden" id="sshtype" name="sshType" value="0"/>
                                    <li class="active">
                                        <a href="#home" type="0" data-toggle="tab">账号登录</a>
                                    </li>
                                    <li>
                                        <a href="#sshkey" type="1" data-toggle="tab">SSH KEY登录</a>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div class="form-group tab-content">
                            <div class="tab-pane fade in active" id="home">
                                <div class="form-group">
                                    <label for="sshpwd" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;&nbsp;密&nbsp;&nbsp;&nbsp;码&nbsp;&nbsp;</label>
                                    <div class="col-md-9">
                                        <input type="password" class="form-control " id="sshpwd" placeholder="请输入密码"/>&nbsp;&nbsp;<label class="error_msg" id="sshpwd_lab"></label>
                                    </div>
                                </div>
                            </div>

                            <div class="tab-pane fade" id="sshkey">
                                <div class="form-group">
                                    <label for="keyfile" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-file"></i>&nbsp;SSH KEY</label>
                                    <div class="col-md-9">
                                        <input type="file" class="file form-control" data-show-preview="false" id="keyfile" value="请点击上传私钥文件" name="sshkey"/>&nbsp;&nbsp;&nbsp;<label class="error_msg" id="keyfile_lab"></label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="passphrase" class="col-lab control-label">&nbsp;&nbsp;<i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;&nbsp;密&nbsp;&nbsp;&nbsp;钥&nbsp;&nbsp;</label>
                                    <div class="col-md-9">
                                        <input type="password" class="form-control " id="passphrase" placeholder="请输入SSH密钥,如未设置不用输入"/>&nbsp;&nbsp;<label class="error_msg" id="passphrase_lab"></label>
                                    </div>
                                </div>

                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm" id="sshbtn" onclick="page.save()">保存</button>
                        &nbsp;&nbsp;
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>

</body>

</html>


