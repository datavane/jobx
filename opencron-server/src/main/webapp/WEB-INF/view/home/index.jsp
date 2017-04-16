<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/common/resource.jsp"/>

    <script src="${contextPath}/js/echarts.min.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/highcharts/js/highcharts.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/highcharts/js/highcharts-more.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/highcharts/js/highcharts-3d.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/highcharts/js/modules/exporting.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/socket/socket.io.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/socket/websocket.js?resId=${resourceId}"></script>
    <script src="${contextPath}/js/home.js?resId=${resourceId}"></script>

    <style type="text/css">

        #config-view h6 {
            margin-bottom: 10px;
            margin-top: 4px;
            color: rgba(255, 255, 255, 0.85);
            font-weight: bold;
        }

        .pie-title {
            color: rgba(235, 235, 235, 0.85);
            font-size: 12px;
            font-weight: bold;
        }

        .labact{
            color:rgb(255,255,255);
            font-weight: lighter;
        }

        .block-area{
            margin-top: -15px;
        }

        .disk-item{
            font-weight: lighter;
        }

        #config-view .counts{
            font-weight: lighter;
        }

        #config-view h6{
            font-weight: lighter;
        }

        .main-chart {
            font-weight: lighter;
        }

        .report_detail {
            margin-top: 5px;
            margin-bottom: 15px;
        }


        .noborder{
            font-family: "Roboto","Arial",sans-serif;
            color: rgba(192, 192, 192,0.9);
            font-weight: lighter;
        }

        .pull-left i {
            color: rgba(225,225,225,0.9);
        }

        .eye-grey{
            filter:alpha(opacity=20);
            -moz-opacity:0.2;
            opacity:0.2;
        }
        .record-div-nodata{
            display: none;
            height: 235px;
            position: relative;
            z-index: -1;
            margin-bottom: -15px;
        }
        .div-nodata{
            display: none;
        }
        .eye-record-nodata{
            filter:alpha(opacity=40);
            -moz-opacity:0.4;
            opacity:0.4;
        }

        .nodata{
            font-size: 40px;
            font-weight: 400;
        }

        a:visited {
            color: rgba(225,225,225,1);
        }

    </style>

    <script type="text/javascript">
        $(document).ready(function () {

            var opencronChart = new OpencronChart('${contextPath}','${csrf}');

            //跨时段查询任务运行比例
            opencronChart.query();

            //系统实时监控
            opencronChart.monitor();

            $("#queryChart").click(function () {
                opencronChart.query();
            });

            $("#agentId").change(
                function () {
                    //清理上一个轮询...
                    if (opencronChart.intervalId != null) {
                        window.clearInterval(opencronChart.intervalId);
                        opencronChart.intervalId = null;
                        opencronChart.clear();
                    }
                    opencronChart.monitor();
                }
            );

            var agent_number = (parseFloat("${success}")/parseFloat("${fn:length(agents)}")*100).toFixed(2);
            if( isNaN(agent_number) ){
                $("#agent_number").text(0).attr("data-value",0);
                $("#agent_number_prop").attr("data-percentage","0%").css("width","0%");
            }else {
                $("#agent_number").text(agent_number).attr("data-value",agent_number);
                $("#agent_number_prop").attr("data-percentage",agent_number+"%").css("width",agent_number+"%");
            }

            var job_number = (parseFloat("${singleton}")/parseFloat("${job}")*100).toFixed(2);
            if(isNaN(job_number)){
                $("#job_number").text(0).attr("data-value",0);
                $("#job_number_prop").attr("data-percentage","0%").css("width","0%");
            }else {
                $("#job_number").text(job_number).attr("data-value",job_number);
                $("#job_number_prop").attr("data-percentage",job_number+"%").css("width",job_number+"%");
            }

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

            $(window).resize(function () {
                window.setTimeout(function () {
                    if (typeof(windowSize) =="undefined" ) {
                        windowSize = {
                            width:$(window).width(),
                            height:$(window).height()
                        }
                        opencronChart.resize();
                        $("#cpu-chart").find("div").first().css("width","100%").find("canvas").first().css("width","100%");
                    }
                    if($(window).width()!=windowSize.width||$(window).height()!=windowSize.height) {
                         windowSize = {
                             width:$(window).width(),
                             height:$(window).height()
                         }
                         opencronChart.resize();
                        $("#cpu-chart").find("div").first().css("width","100%").find("canvas").first().css("width","100%");
                    }
                },500)
            });

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
        <li><a href="">首页</a></li>
    </ol>

    <h4 class="page-title" ><i class="fa fa-tachometer" aria-hidden="true" style="font-size: 30px;"></i>&nbsp;作业报告</h4>
    <!-- Quick Stats -->
    <div class="block-area" id="overview" style="margin-top: 0px">
        <!-- cards -->
        <div class="row cards">
            <div class="card-container col-lg-3 col-sm-6 col-sm-12">
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/agent/view?csrf=${csrf}'">
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
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/job/view?csrf=${csrf}'">
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
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/record/done?success=1&csrf=${csrf}'">
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
                <div class="card hover" onclick="javascript:window.location.href='${contextPath}/record/done?success=0&csrf=${csrf}'">
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
            <div class="block-color col-xs-12" style="position:relative;border-radius: 2px;border-bottom-left-radius:0px;border-bottom-right-radius: 0px;margin-bottom:0px;">
                <div class="tile-title" >
                    <i aria-hidden="true" class="fa fa-bar-chart"></i>&nbsp;执行报告
                </div>
                <div id="timeopter">
                    <div style="float: right;margin-bottom: 0px;margin-top: -10px;margin-right:10px;">
                        <label for="startTime" class="label-self">时间&nbsp;: </label>
                        <input type="text" style="border-radius: 2px;width: 90px" id="startTime" name="startTime" value="${startTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate select-self"/>
                        <label for="endTime" class="label-self">&nbsp;至&nbsp;</label>
                        <input type="text" style="border-radius: 2px;width: 90px" id="endTime" name="endTime" value="${endTime}" onfocus="WdatePicker({onpicked:function(){},dateFmt:'yyyy-MM-dd'})" class="Wdate select-self"/>&nbsp;
                        <button id="queryChart" class="btn btn-default btn-sm" style="vertical-align:top;height: 25px;" type="button"><i class="glyphicon glyphicon-search"></i>查询</button>
                    </div>
                </div>
            </div>

            <div id="record-report-havedata">
                <div class="col-xs-7 block-color" id="overview_report_div" style="display: none">
                   <div id="overview_report" style="height: 300px;" class="main-chart" ></div>
               </div>
                <div id="report_detail" class="col-xs-2 block-color" style="height: 300px;padding-top:15px;display: none">
                   <h5 class="subtitle mb5" style="font-size: 20px;">报告明细</h5>
                   <div class="clearfix"></div>

                   <span class="sublabel">运行模式(自动/手动)</span>
                   <div class="progress progress-sm report_detail">
                       <a href="#" data-toggle="tooltip" class="tooltips progress-bar progress-bar-primary" role="progressbar" id="job_type">
                           <span class="sr-only"></span>
                       </a>
                   </div><!-- progress -->

                   <span class="sublabel">作业类型(单一/流程）</span>
                   <div class="progress progress-sm report_detail">
                       <a href="#" data-toggle="tooltip" class="tooltips progress-bar progress-bar-success" role="progressbar" id="job_category" >
                           <span class="sr-only"></span>
                       </a>
                   </div><!-- progress -->

                   <span class="sublabel">规则类型(crontab/quartz)</span>
                   <div class="progress progress-sm report_detail">
                       <a href="#" data-toggle="tooltip" class="tooltips progress-bar progress-bar-danger" role="progressbar"  id="job_model" >
                           <span class="sr-only"></span>
                       </a>
                   </div><!-- progress -->

                   <span class="sublabel">重跑状态 (非重跑/重跑)</span>
                   <div class="progress progress-sm report_detail">
                       <a href="#" data-toggle="tooltip" class="tooltips progress-bar progress-bar-warning" role="progressbar"  id="job_rerun"  >
                           <span class="sr-only"></span>
                       </a>
                   </div><!-- progress -->

                   <span class="sublabel">执行状态(成功/失败)</span>
                   <div class="progress progress-sm report_detail">
                       <a href="#" data-toggle="tooltip" class="tooltips progress-bar progress-bar-success" role="progressbar" id="job_status" >
                           <span class="sr-only"></span>
                       </a>
                   </div><!-- progress -->
               </div>
                <div class="col-xs-3 block-color" id="overview_pie_div" style="display: none">
                     <div id="overview_pie" class="main-chart" style="height: 300px;" ></div>
                </div>
                <div class="col-xs-12 block-color" id="overview_loader" style="height: 300px;">
                     <div class="loader">
                         <div class="loader-inner">
                             <div class="loader-line-wrap">
                                 <div class="loader-line"></div>
                             </div>
                             <div class="loader-line-wrap">
                                 <div class="loader-line"></div>
                             </div>
                             <div class="loader-line-wrap">
                                 <div class="loader-line"></div>
                             </div>
                             <div class="loader-line-wrap">
                                 <div class="loader-line"></div>
                             </div>
                             <div class="loader-line-wrap">
                                 <div class="loader-line"></div>
                             </div>
                         </div>
                     </div>
                </div>
            </div>

            <div id="record-report-nodata" class="text-center record-div-nodata col-xs-12 block-color" style="height: 300px;margin-bottom: 0px">
                <div  style="font-size: 110px;margin-top: 60px" class="eye-record-nodata">
                    <i  class="glyphicon glyphicon-eye-close"></i>
                    <span class="nodata">无记录</span>
                </div>
            </div>
        </div>
    </div>

    <h4 class="page-title" ><i class="icon">&#61881;</i> &nbsp;监控概况</h4>
    <!-- Main Widgets -->
    <div class="block-area" id="monitor" style="margin-top: 0px">

        <div class="row">
            <div class="col-md-12">
                <!-- overview -->
                <div class="tile " style="background: none">
                    <h2 class="tile-title" style="width: 100%;background:rgba(0,0,0,0.40);border-top-left-radius:2px;border-top-right-radius:2px;"><i aria-hidden="true" class="fa fa-area-chart"></i>&nbsp;系统概况</h2>
                    <div class="tile-config dropdown" style="float: right;">
                        <select class="form-control input-sm m-b-10" style="width: 120px;border-radius: 2px;" id="agentId">
                            <c:forEach var="w" items="${agents}">
                                <option value="${w.agentId}" ${w.agentId eq agentId ? 'selected' : ''}>${w.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div id="overview-chart" class="p-10 text-center div-havedata" style="background:rgba(0,0,0,0.40);border-bottom-left-radius:2px;border-bottom-right-radius:2px;height: 192px;">

                        <div class="loader">
                            <div class="loader-inner">
                                <div class="loader-line-wrap">
                                    <div class="loader-line"></div>
                                </div>
                                <div class="loader-line-wrap">
                                    <div class="loader-line"></div>
                                </div>
                                <div class="loader-line-wrap">
                                    <div class="loader-line"></div>
                                </div>
                                <div class="loader-line-wrap">
                                    <div class="loader-line"></div>
                                </div>
                                <div class="loader-line-wrap">
                                    <div class="loader-line"></div>
                                </div>
                            </div>
                        </div>

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
                            <div class="loader" >
                                <div class="loader-inner">
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                </div>
                            </div>
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
                            <div class="loader">
                                <div class="loader-inner">
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                    <div class="loader-line-wrap">
                                        <div class="loader-line"></div>
                                    </div>
                                </div>
                            </div>
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
                    <div class="loader">
                        <div class="loader-inner">
                            <div class="loader-line-wrap">
                                <div class="loader-line"></div>
                            </div>
                            <div class="loader-line-wrap">
                                <div class="loader-line"></div>
                            </div>
                            <div class="loader-line-wrap">
                                <div class="loader-line"></div>
                            </div>
                            <div class="loader-line-wrap">
                                <div class="loader-line"></div>
                            </div>
                            <div class="loader-line-wrap">
                                <div class="loader-line"></div>
                            </div>
                        </div>
                    </div>

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

</section>

<jsp:include page="/WEB-INF/common/footer.jsp"/>

