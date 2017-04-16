<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron"  uri="http://www.opencron.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
 <jsp:include page="/WEB-INF/common/resource.jsp"/>
    <script type="text/javascript">
        function clearRecord() {

            var startTime = $("#startTime").val();
            if (!startTime){
                alert("请填写起始时间！");
                return false;
            }
            var endTime = $("#endTime").val();
            if (!endTime){
                alert("请填写结束时间！");
                return false;
            }
            swal({
                title: "",
                text: "您确定要清理此时间段的任务记录？",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "执行"
            }, function() {
                $.ajax({
                    headers:{"csrf":"${csrf}"},
                    type:"POST",
                    url: "${contextPath}/config/clear",
                    data: {
                        "startTime": startTime,
                        "endTime": endTime
                    }
                });
                alertMsg( "清理操作已执行.");
            });
        }
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
        <li><a href="">系统设置</a></li>
    </ol>
    <h4 class="page-title"><i class="glyphicon glyphicon-cog"></i>&nbsp;设置详情</h4>
    <div class="block-area" id="defaultStyle">

        <table class="table tile">
            <tbody id="tableContent">
            <tr>
                <td class="item"><i class="glyphicon glyphicon-envelope"></i>&nbsp;发件邮箱：</td>
                <td>${config.senderEmail}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a class="green" href="${contextPath}/config/editpage?csrf=${csrf}" title="编辑"><i class="glyphicon glyphicon-pencil"></i></a>
                </td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-map-marker"></i>&nbsp;SMTP地址：</td>
                <td>${config.smtpHost}
                </td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-filter"></i>&nbsp;SMTP端口：</td>
                <td>${config.smtpPort} &nbsp;&nbsp;<span class="tips">（SSL协议）</span>
                </td>
            </tr>

            <tr>
                <td class="item"><i class="glyphicon glyphicon-lock"></i>&nbsp;邮箱密码：</td>
                <td>******</td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-font"></i>&nbsp;发信URL：</td>
                <td>${config.sendUrl}</td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-time"></i>&nbsp;发送间隔：</td>
                <td>
                    ${config.spaceTime} 分钟<span class="tips">（同一执行器失联后告警邮件和短信发送后到下一次发送的时间间隔）</span>
                </td>
            </tr>
            <tr>
                <td class="item"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;短信模板：</td>
                <td>
                    ${config.template}
                </td>
            </tr>
            <tr>
                <td class="item"><i class='glyphicon glyphicon-trash'></i>&nbsp;清理记录：</td>
                <td>
                    <label for="startTime" class="label-self">时间&nbsp;: </label>
                    <input type="text" style="border-radius: 2px;width: 90px" id="startTime" name="startTime" value="${startTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate select-self"/>
                    <label for="endTime" class="label-self">&nbsp;至&nbsp;</label>
                    <input type="text" style="border-radius: 2px;width: 90px" id="endTime" name="endTime" value="${endTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate select-self"/>&nbsp;
                    <button onclick="clearRecord()" class="btn btn-default btn-sm" style="vertical-align:top;height: 25px;" type="button"><i class="glyphicon glyphicon-trash"></i>&nbsp;清理</button><span class="tips">&nbsp;&nbsp;&nbsp;（<b>*&nbsp;</b>此操作会删除选定时间段内的任务记录，请谨慎执行）</span>
                </td>
            </tr>
            </tbody>

        </table>
    </div>

</section>
<br/><br/>

<jsp:include page="/WEB-INF/common/footer.jsp"/>
