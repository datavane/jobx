<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="cron"  uri="http://www.jobx.org"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<meta name="author" content="author:benjobs,wechat:wolfboys,Created by 2016" />
<head>

    <style type="text/css">
        .nav-tabs > li > a {
            padding: 13px 16px 13px;
        }

        .tab-content {
            padding: 29px 0px;
        }
        .td-title {
            background: rgba(0,0,0,0.2);
            width: 135px;
        }
    </style>

    <script type="text/javascript">
        var format = {
            "Version":"版本",
            "Drivers":"驱动",
            "ResetEnable":"是否允许重置",
            "ResetCount":"重置次数",
            "JavaVMName":"JAVA版本",
            "JavaVersion":"JVM名称",
            "JavaClassPath":"<br>classpath路径",
            "StartTime":"启动时间"
        };

        var page = {
            toHome : function () {
                ajax({
                    type: "get",
                    url: "${contextPath}/druid/basic.json"
                },function (data) {
                    var html = "";
                    data = data["Content"];
                    for(var key in data) {
                        var value = data[key];
                        if (key != "StartTime")
                            value = value.toString().replace(/,|:/g, "<br>&nbsp;&nbsp;");
                        html += $("#basic-each").html()
                            .replace(/#key#/,format[key])
                            .replace(/#value#/,value);
                    }
                    html = $("#basic-template").html().replace(/#basic#/,html);
                    $("#basic").html(html);
                });
            },
            toDS:function () {
                ajax({
                    type: "get",
                    url: "${contextPath}/druid/datasource.json"
                },function (data) {
                    var html = "";
                    data = data["Content"][0];
                    for(var key in data) {
                        var value = data[key];
                        html += $("#basic-each").html()
                            .replace(/#key#/,key)
                            .replace(/#value#/,value?value:"");
                    }
                    html = $("#basic-template").html().replace(/#basic#/,html);
                    $("#datasource").html(html);
                });
            }
        }

    </script>

    <script type="text/html" id="basic-each">
        <tr><td class="td-title">#key#</td><td>&nbsp;&nbsp;#value#</td></tr>
    </script>

    <script type="text/html" id="basic-template">
        <table class="table tile">
            <tbody>
            #basic#
            </tbody>
        </table>
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
        <li><a href="">监控中心</a></li>
        <li><a href="">Druid监控</a></li>
    </ol>
    <h4 class="page-title"><i class="fa fa-bar-chart" aria-hidden="true" style="font-style: 30px;"></i>&nbsp;Druid监控</h4>

    <div class="block-area">
        <div class="tab-container">
            <ul class="nav tab nav-tabs">
                <li class=""><a href="javascript:void(0)">jobx Druid</a></li>
                <li class=""><a href="#basic" onclick="page.toHome()">首页</a></li>
                <li class=""><a href="#datasource" onclick="page.toDS()" >数据源</a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane" id="basic"></div>
                <div class="tab-pane" id="datasource"></div>
            </div>
        </div>
    </div>
</section>

</body>

</html>
