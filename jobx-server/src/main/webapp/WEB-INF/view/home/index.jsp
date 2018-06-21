<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.jobx.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <script src="${contextPath}/static/js/echarts.min.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/highcharts/js/highcharts.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/highcharts/js/highcharts-more.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/highcharts/js/highcharts-3d.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/highcharts/js/modules/exporting.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/socket.io.js?resId=${resourceId}"></script>
    <script src="${contextPath}/static/js/dashboard.js?resId=${resourceId}"></script>

    <script type="text/javascript">

        $(document).ready(function () {
            var jobxChart = new JobXChart('${contextPath}');
            //跨时段查询任务运行比例
            jobxChart.query();

            $('body').on('click touchstart', '#menu-toggle', function(e){
                jobxChart.resize();
            });

            <c:if test="${agents ne null and !empty agents}">
                //系统实时监控
                //jobxChart.monitor();
            </c:if>

            $("#queryChart").click(function () {
                jobxChart.query();
            });

            $("#agentId").change(function () {
                //清理上一个轮询...
                if (jobxChart.intervalId != null) {
                    window.clearInterval(jobxChart.intervalId);
                    jobxChart.intervalId = null;
                    jobxChart.clear();
                }
                jobxChart.monitor();
            });

            var agent_number = (parseFloat("${success}")/parseFloat("${fn:length(agents)}")*100).toFixed(2);
            if( isNaN(agent_number) ){
                $("#agent_number").text(0).attr("data-value",0);
                $("#agent_number_prop").attr("data-percentage","0%").css("width","0%");
            }else {
                $("#agent_number").text(agent_number).attr("data-value",agent_number);
                $("#agent_number_prop").attr("data-percentage",agent_number+"%").css("width",agent_number+"%");
            }

        /*    var job_number = (parseFloat("${singleton}")/parseFloat("${job}")*100).toFixed(2);
            if(isNaN(job_number)){
                $("#job_number").text(0).attr("data-value",0);
                $("#job_number_prop").attr("data-percentage","0%").css("width","0%");
            }else {
                $("#job_number").text(job_number).attr("data-value",job_number);
                $("#job_number_prop").attr("data-percentage",job_number+"%").css("width",job_number+"%");
            }*/

            var ok_number = (parseFloat("${successAutoRecord}")/parseFloat("${successRecord}")*100).toFixed(2);
            if(isNaN(ok_number)){
                $("#ok_number").text(0).attr("data-value",0);
                $("#ok_number_prop").attr("data-percentage","0%").css("width","0%");
            }else {
                $("#ok_number").text(ok_number).attr("data-value",ok_number);
                $("#ok_number_prop").attr("data-percentage",ok_number+"%").css("width",ok_number+"%");
            }

            var no_number = (parseFloat("${failedAutoRecord}")/parseFloat("${failedRecord}")*100).toFixed(2);
            if(isNaN(no_number)){
                $("#no_number").text(0).attr("data-value",0);
                $("#no_number_prop").attr("data-percentage","0%").css("width"+"0%");
            }else {
                $("#no_number").text(no_number).attr("data-value",no_number);
                $("#no_number_prop").attr("data-percentage",no_number+"%").css("width",no_number+"%");
            }

            if ($.isMobile()) {
                $("#startTime").css("width","80px").removeClass("Wdate").addClass("mWdate");
                $("#endTime").css("width","80px").removeClass("Wdate").addClass("mWdate");
            }

            $(".count").mouseover(function () {
                $(this).css({"background-color":"rgba(0,0,0,0.55)"});
                $(this).parent().prev().find("i:first").removeClass("eye-grey");
            }).mouseout(function () {
                $(this).css({"background-color":""});
                $(this).parent().prev().find("i:first").addClass("eye-grey");
            });

            $(".card-link").mouseover(function (){
                $(this).find("i:first").removeClass("eye-grey");
                $(this).next("div").find("div:first").css({"background-color":"rgba(0,0,0,0.55)"});
            });

            $("#overview_report_bar").find("td").click(function () {
                var group = $(this).attr("group");
                $("#overview_report_bar").find("."+group).each(function (i,n) {
                    var _this = $(n);
                    if (_this.hasClass("on")) {
                        _this.removeClass(group+"-color");
                        _this.find("div").removeClass(group+"-bar");
                        _this.removeClass("on").addClass("off");
                    } else {
                        if (_this.hasClass("legendLabel")) {
                            _this.addClass(group+"-color");
                        } else {
                            _this.find(".legendColorBox").addClass(group+"-bar");
                        }
                        _this.removeClass("off").addClass("on");
                    }
                });
                var onNode = $("#overview_report_bar").find(".on");
                var map = new Map();
                if (onNode.length>0) {
                    for (var i = 0; i < onNode.length; i++) {
                        var group = $(onNode[i]).attr("group");
                        map.put(group,group);
                    }
                }
                jobxChart.changeChar(map);
                jobxChart.createChart();
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
        <li><a href="">首页</a></li>
    </ol>

    <h4 class="page-title" ><i class="fa fa-tachometer" aria-hidden="true" style="font-size: 30px;"></i>&nbsp;作业报告</h4>
    <!-- Quick Stats -->
    <div class="block-area" id="overview" style="margin-top: 0px">
        <!-- cards -->
        <div class="row cards">
            <div class="card-container col-lg-3 col-sm-6 col-sm-12">
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/agent/view.htm'">
                    <div class="front count">
                        <div class="media">
                            <span class="pull-left"><i style="font-size: 60px;margin-top: 0px;" aria-hidden="true" class="fa fa-desktop"></i></span>
                            <div class="media-body">
                                <small>执行器</small>
                                <div class="clearfix"></div>
                                <h2 data-animation-duration="1500" data-value="0" class="media-heading animate-number">${fn:length(agents)}</h2>
                            </div>
                        </div>

                        <div class="progress-list">
                            <div class="details">
                                <div class="title">通信状态(正常机器/失联机器)</div>
                            </div>
                            <div class="status pull-right bg-transparent-black-1">
                                <span data-animation-duration="1500" data-value="" class="animate-number" id="agent_number" ></span>%
                            </div>
                            <div class="progress progress-sm progress-transparent-black">
                                <div data-percentage="0%" class="progress-bar animate-progress-bar" id="agent_number_prop"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-container col-lg-3 col-sm-6 col-sm-12">
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/job/view.htm'">
                    <div class="front count">
                        <div class="media">
                            <span class="pull-left"><i style="font-size: 60px;margin-top: 1px;" aria-hidden="true" class="fa fa-tasks"></i></span>
                            <div class="media-body">
                                <small>作业数</small>
                                <h2 data-animation-duration="1500" data-value="0" class="media-heading animate-number">${job}</h2>
                            </div>
                        </div>
                        <div class="progress-list">
                            <div class="details">
                                <div class="title">作业类型(单一任务/流程任务)</div>
                            </div>
                            <div class="status pull-right bg-transparent-black-1">
                                <span data-animation-duration="1500" data-value="" class="animate-number" id="job_number"></span>%
                            </div>
                            <div class="progress progress-sm progress-transparent-black">
                                <div data-percentage="0%" class="progress-bar animate-progress-bar" id="job_number_prop"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-container col-lg-3 col-sm-6 col-sm-12">
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/record/done.htm?success=1'">
                    <div class="front count">
                        <div class="media">
                            <span class="pull-left"><i style="font-size: 60px;margin-top: 0px;" class="fa fa-thumbs-o-up" aria-hidden="true"></i></span>
                            <div class="media-body">
                                <small>成功作业</small>
                                <h2 data-animation-duration="1500" data-value="0" class="media-heading animate-number">${successRecord}</h2>
                            </div>
                        </div>

                        <div class="progress-list">
                            <div class="details">
                                <div class="title">执行类型(自动执行/手动执行)</div>
                            </div>
                            <div class="status pull-right bg-transparent-black-1">
                                <span data-animation-duration="1500" data-value="" class="animate-number" id="ok_number"></span>%
                            </div>
                            <div class="progress progress-sm progress-transparent-black">
                                <div data-percentage="0%" class="progress-bar animate-progress-bar" id="ok_number_prop"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-container col-lg-3 col-sm-6 col-sm-12">
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/record/done.htm?success=0'">
                    <div class="front count">
                        <div class="media">
                            <span class="pull-left"><i style="font-size: 60px;margin-top: -3px;" class="fa fa-thumbs-o-down" aria-hidden="true"></i></span>
                            <div class="media-body">
                                <small>失败作业</small>
                                <h2 data-animation-duration="1500" data-value="0" class="media-heading animate-number">${failedRecord}</h2>
                            </div>
                        </div>

                        <div class="progress-list">
                            <div class="details">
                                <div class="title">执行类型(自动执行/手动执行)</div>
                            </div>
                            <div class="status pull-right bg-transparent-black-1">
                                <span data-animation-duration="1500" data-value="" class="animate-number" id="no_number"></span>%
                            </div>
                            <div class="progress progress-sm progress-transparent-black">
                                <div data-percentage="0%" class="progress-bar animate-progress-bar" id="no_number_prop"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <!-- /cards -->

    </div>

    <div class="block-area col-xs-12" id="record-report" style="margin-bottom: 15px;">
        <div class="textured2 col-xs-12" style="padding: 0;">
            <div class="block-color col-xs-12" style="position:relative;border-radius: 1px;border-bottom-left-radius:0px;border-bottom-right-radius: 0px;margin-bottom:0px;">
                <div class="tile-title" >
                    <i aria-hidden="true" class="fa fa-bar-chart"></i>&nbsp;执行报告
                </div>
                <div id="timeopter">
                    <div style="float: right;margin-bottom: 0px;margin-top: -10px;margin-right:10px;">
                        <label for="startTime" class="label-self">时间&nbsp;: </label>
                        <input type="text" style="border-radius: 1px;width: 90px" id="startTime" name="startTime" value="${startTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate"/>
                        <label for="endTime" class="label-self">&nbsp;至&nbsp;</label>
                        <input type="text" style="border-radius: 1px;width: 90px" id="endTime" name="endTime" value="${endTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate"/>&nbsp;
                        <button id="queryChart" class="btn btn-default btn-sm" style="vertical-align:top;height: 25px;" type="button"><i class="glyphicon glyphicon-search"></i>查询</button>
                    </div>
                </div>
            </div>

            <div id="record-report-havedata">
                <div class="col-xs-9 block-color" id="overview_report_div" style="display: none">
                    <div id="overview_report" style="height: 300px;" class="main-chart" ></div>
               </div>
                <div class="col-xs-3 block-color" id="overview_pie_div" style="display: none">
                     <div id="overview_pie" class="main-chart" style="height: 280px;margin-top: 20px;" ></div>
                </div>
                <div class="col-xs-12 block-color" id="overview_report_bar">
                    <table>
                        <tbody>
                            <tr>
                                <td class="on success" group="success"><div class="legendColorBox success-bar"></div></td>
                                <td class="on success legendLabel success-color" group="success">Successful</td>

                                <td class="on failed" group="failed"><div class="legendColorBox failed-bar"></div></td>
                                <td class="on failed legendLabel failed-color" group="failed">Failed</td>

                                <td class="on killed" group="killed"><div class="legendColorBox killed-bar"></div></td>
                                <td class="on killed legendLabel killed-color" group="killed">Killed</td>

                                <td class="off timeout" group="timeout"><div class="legendColorBox"></div></td>
                                <td class="off timeout legendLabel" group="timeout">Timeout</td>

                                <td class="off lost" group="lost"><div class="legendColorBox"></div></td>
                                <td class="off lost legendLabel" group="lost">Lost</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="col-xs-12 block-color" id="overview_loader" style="height:340px;">
                    <figure>
                        <div class='dot white'></div>
                        <div class='dot'></div>
                        <div class='dot'></div>
                        <div class='dot'></div>
                        <div class='dot'></div>
                    </figure>
                </div>
            </div>

            <div id="record-report-nodata" class="text-center record-div-nodata col-xs-12 block-color" style="height: 340px;margin-bottom: 0px">
                <div  style="font-size: 110px;margin-top: 60px" class="eye-record-nodata">
                    <i  class="glyphicon glyphicon-eye-close"></i>
                    <span class="nodata">无记录</span>
                </div>
            </div>
        </div>
    </div>

   <%-- <c:if test="${agents ne null and !empty agents}">--%>

     <c:if test="${ 1==2 } ">

        <h4 class="page-title" ><i class="icon">&#61881;</i> &nbsp;监控概况</h4>
        <!-- Main Widgets -->
        <div class="block-area" id="monitor" style="margin-top: 0px">

            <div class="row">
                <div class="col-md-12">
                    <!-- overview -->
                    <div class="tile " style="background: none">
                        <h2 class="tile-title" style="width: 100%;background:rgba(0,0,0,0.40);border-top-left-radius:2px;border-top-right-radius:2px;"><i aria-hidden="true" class="fa fa-area-chart"></i>&nbsp;系统概况</h2>
                        <div class="tile-config dropdown" style="float: right;">
                            <select class="form-control input-sm m-b-10" style="width: 120px;border-radius: 1px;" id="agentId">
                                <c:forEach var="w" items="${agents}">
                                    <c:if test="${w.status eq true}">
                                        <option value="${w.agentId}" ${w.agentId eq agentId ? 'selected' : ''}>${w.name}</option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>

                        <div id="overview-chart" class="p-10 text-center div-havedata" style="background:rgba(0,0,0,0.40);border-bottom-left-radius:2px;border-bottom-right-radius:2px;height: 192px;">
                            <figure>
                                <div class='dot white'></div>
                                <div class='dot'></div>
                                <div class='dot'></div>
                                <div class='dot'></div>
                                <div class='dot'></div>
                            </figure>
                        </div>

                        <div class="p-10 text-center div-nodata"  style="background:rgba(0,0,0,0.40);border-bottom-left-radius:2px;border-bottom-right-radius:2px;height: 192px;">
                            <div  style="font-size: 110px;" class="eye-grey">
                                <i  class="glyphicon glyphicon-eye-close"></i>
                                <span class="nodata">无记录</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-8">
                    <div class="tile textured" id="top" style="min-height: 250px">
                        <h2 class="tile-title"><i aria-hidden="true" class="fa fa-server"></i>&nbsp;进程监控</h2>
                        <div class="div-havedata" style="margin-left: 15px;margin-right: 15px;">
                            <table class="table tile table-custom table-sortable " style="font-size: 13px;background-color: rgba(0,0,0,0);">
                                <tbody id="topbody" style="color: #fafafa;font-size:12px;">
                                <figure>
                                    <div class='dot white'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                </figure>
                                </tbody>
                            </table>
                        </div>
                        <div class="text-center div-nodata" style="margin-top: 20px">
                            <div  style="font-size: 110px;" class="eye-grey">
                                <i  class="glyphicon glyphicon-eye-close"></i>
                                <span class="nodata">无记录</span>
                            </div>
                        </div>
                    </div>
                    <!-- CPU -->
                    <div class="tile" id="cpu">
                        <h2 class="tile-title"><i aria-hidden="true" class="fa fa-line-chart"></i>&nbsp;CPU使用率</h2>
                        <div class="p-t-10 p-r-5 p-b-5">
                            <div class="div-havedata" style="height: 200px; padding: 0px; position: relative;" id="cpu-chart">
                                <figure>
                                    <div class='dot white'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                    <div class='dot'></div>
                                </figure>
                            </div>
                            <div class="text-center div-nodata" style="height: 200px;margin-top: 20px; position: relative;">
                                <div  style="font-size: 110px;" class="eye-grey">
                                    <i  class="glyphicon glyphicon-eye-close"></i>
                                    <span class="nodata">无记录</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!--config-->
                <div class="col-md-4" id="info">

                    <div class="tile textured">
                        <h2 class="tile-title"><i aria-hidden="true" class="fa fa-pie-chart"></i>&nbsp;机器信息</h2>
                        <figure>
                            <div class='dot white'></div>
                            <div class='dot'></div>
                            <div class='dot'></div>
                            <div class='dot'></div>
                            <div class='dot'></div>
                        </figure>

                        <div class="p-t-10 p-r-5 p-b-5 div-havedata">
                            <div id="disk-view" class="main-chart" style="height: 250px;margin-top: 10px;"></div>
                        </div>
                        <div class="s-widget-body div-havedata" id="disk-item"></div>
                        <div class="listview narrow" id="config-view" style="margin-top: -17px;display: none;">

                            <div class="media" id="view-hostname">
                                <div class="pull-right">
                                    <div class="counts" id="config-hostname"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-leaf"></i>&nbsp;&nbsp;主&nbsp;机&nbsp;名</h6>
                                </div>
                            </div>

                            <div class="media" id="view-os">
                                <div class="pull-right">
                                    <div class="counts" id="config-os"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-globe"></i>&nbsp;&nbsp;系统名称</h6>
                                </div>
                            </div>

                            <div class="media" id="view-kernel">
                                <div class="pull-right">
                                    <div class="counts" id="config-kernel"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-info-sign"></i>&nbsp;&nbsp;内核版本</h6>
                                </div>
                            </div>

                            <div class="media" id="view-name">
                                <div class="pull-right">
                                    <div class="counts" id="config-cpuinfo-name"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-star-empty"></i>&nbsp;&nbsp;CPU名称</h6>
                                </div>
                            </div>

                            <div class="media" id="view-machine">
                                <div class="pull-right">
                                    <div class="counts" id="config-machine"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-list-alt"></i>&nbsp;&nbsp;CPU架构</h6>
                                </div>
                            </div>

                            <div class="media" id="view-cpuinfo-count">
                                <div class="pull-right">
                                    <div class="counts" id="config-cpuinfo-count"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-certificate"></i>&nbsp;&nbsp;CPU核数</h6>
                                </div>
                            </div>

                            <div class="media" id="view-cpuinfo-info">
                                <div class="pull-right">
                                    <div class="counts" id="config-cpuinfo-info"></div>
                                </div>
                                <div class="media-body">
                                    <h6><i class="glyphicon glyphicon-fire"></i>&nbsp;&nbsp;CPU频率</h6>
                                </div>
                            </div>
                        </div>

                        <div class="listview narrow text-center div-nodata" id="config-view-nodata" style="height: 290px;padding-top: 55px">
                            <div  style="font-size: 110px;" class="eye-grey">
                                <i  class="glyphicon glyphicon-eye-close"></i>
                                <span class="nodata" >无记录</span>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>

    </c:if>

</section>

</body>
</html>

