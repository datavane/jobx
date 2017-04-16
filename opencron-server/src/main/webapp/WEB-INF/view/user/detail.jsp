<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <script type="text/javascript">

        function editPwd(id){
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url:"${contextPath}/user/pwdpage",
                data:{"id":id},
                success : function(obj) {
                    $("#pwdform")[0].reset();
                    if(obj!=null){
                        $("#oldpwd").html("");
                        $("#checkpwd").html("");
                        $("#Result").html("");
                        $("#id").val(obj.userId);
                        $("#pwdModal").modal("show");
                        return;
                    }
                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                }
            });

        }

        function savePwd(){
            var id = $("#id").val();
            if (!id){
                alert("页面异常，请刷新重试!");
                return false;
            }
            var pwd0 = $("#pwd0").val();
            if (!pwd0){
                alert("请填原密码!");
                return false;
            }
            var pwd1 = $("#pwd1").val();
            if (!pwd1){
                alert("请填新密码!");
                return false;
            }
            if (pwd1.length < 6 || pwd1.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            var pwd2 = $("#pwd2").val();
            if (!pwd2){
                alert("请填写确认密码!");
                return false;
            }
            if (pwd2.length < 6 || pwd2.length > 15){
                alert("密码长度请在6-15位之间!");
                return false;
            }
            if (pwd1 != pwd2){
                alert("两密码不一致!");
                return false;
            }
            $.ajax({
                headers:{"csrf":"${csrf}"},
                type:"POST",
                url:"${contextPath}/user/editpwd",
                data:{
                    "id":id,
                    "pwd0":calcMD5(pwd0),
                    "pwd1":calcMD5(pwd1),
                    "pwd2":calcMD5(pwd2)
                },
                success:function(data){
                    if (data == "success"){
                        $('#pwdModal').modal('hide');
                        alertMsg("修改成功");
                        return false;
                    }
                    if(data == "one"){
                        $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不正确' + "</font>");
                        return false;
                    }
                    if(data == "two"){
                        $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                        return false;
                    }

                },
                error : function() {
                    alert("网络繁忙请刷新页面重试!");
                    return false;
                }
            });
        }

        $(document).ready(function(){
            $("#size").change(function(){
                var pageSize = $("#size").val();
                window.location.href = "${contextPath}/user/view?pageSize="+pageSize+"&csrf=${csrf}";
            });

            $("#pwd1").change(function(){
                if ($("#pwd1").val().length < 6 || $("#pwd1").val().length > 15){
                    alert("密码长度请在6-15位之间!");
                    return false;
                }
            });

            $("#pwd0").focus(function(){
                $("#oldpwd").html("");
            });

            $("#pwd2").focus(function(){
                $("#checkpwd").html("");
            });

            $("#pwd0").blur(function(){
                if (!$("#pwd0").val()){
                    $("#oldpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;请输入原密码' + "</font>");
                }
            });

            $("#pwd2").change(function(){
                if ($("#pwd2").val()==$("#pwd1").val()){
                    $("#checkpwd").html("<font color='green'>" + '<i class="glyphicon glyphicon-ok-sign"></i>&nbsp;两密码一致' + "</font>");
                }else {
                    $("#checkpwd").html("<font color='red'>" + '<i class="glyphicon glyphicon-remove-sign"></i>&nbsp;密码不一致' + "</font>");
                }
            });
        });

    </script>
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
        <li><a href="">用户管理</a></li>
        <li><a href="">用户详情</a></li>
    </ol>
    <h4 class="page-title"><i aria-hidden="true" class="fa fa-eye"></i>&nbsp;用户详情</h4>

    <div style="float: right;margin-top: 5px">
        <a onclick="history.back();" class="btn btn-sm m-t-10" style="margin-right: 16px;margin-bottom: -4px"><i class="fa fa-mail-reply" aria-hidden="true"></i>&nbsp;返回</a>
    </div>

    <div class="block-area" id="defaultStyle">
        <table class="table tile textured">
            <tbody id="tableContent">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-user"></i>&nbsp;用&nbsp;&nbsp;户&nbsp;&nbsp;名：</td>
                <td>${u.userName}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="#" onclick="editPwd('${u.userId}')" title="修改密码"><i class="glyphicon glyphicon-lock"></i></a>&nbsp;&nbsp;
                    <a href="${contextPath}/user/editpage?id=${u.userId}&csrf=${csrf}" title="编辑资料"><i class="glyphicon glyphicon-pencil"></i></a></td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-tag"></i>&nbsp;真实姓名：</td>
                <td>${u.realName}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-comment"></i>&nbsp;联系方式：</td>
                <td>${u.contact}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-envelope"></i>&nbsp;电子邮箱：</td>
                <td>${u.email}</td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-magnet"></i>&nbsp;QQ&nbsp;号&nbsp;码：</td>
                <td>${u.qq}</td>
            </tr>


            </tbody>

        </table>
    </div>

    <!-- 修改密码弹窗 -->
    <div class="modal fade" id="pwdModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4>修改密码</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form" id="pwdform">
                        <input type="hidden" id="id">
                        <div class="form-group" style="margin-bottom: 4px;">
                            <label for="pwd0" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;原&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd0" placeholder="请输入原密码">&nbsp;&nbsp;<label id="oldpwd"></label>
                            </div>
                        </div>
                        <div class="form-group" style="margin-bottom: 20px;">
                            <label for="pwd1" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;新&nbsp;&nbsp;密&nbsp;&nbsp;码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd1" placeholder="请输入新密码">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pwd2" class="col-lab control-label"><i class="glyphicon glyphicon-lock"></i>&nbsp;&nbsp;确认密码：</label>
                            <div class="col-md-9">
                                <input type="password" class="form-control " id="pwd2" placeholder="请输入确认密码"/>&nbsp;&nbsp;<label id="checkpwd"></label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-sm"  onclick="savePwd()">保存</button>&nbsp;&nbsp;
                        <button type="button" class="btn btn-sm"  data-dismiss="modal">关闭</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
