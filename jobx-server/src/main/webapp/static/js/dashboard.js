function JobXChart() {
    this.path = arguments[0] || "/";
    this.intervalId = null;
    this.intervalTime = 2000;
    this.gauge = null;
    this.gaugeOption = null;
    this.data = null;
    this.socket = null;
    this.resizeChartData = {};
    this.chartItem = {};
    this.diskLoad = false;
    this.cpuLoad = false;
    this.configLoad = false;
    this.cpuChartObj = {};
    this.chartWidth = 0;
    this.cpuX = [];
    this.cpuY = [];
    this.config = [];
    this.overviewDataArr = [
        {"key": "us", "title": "用户占用", "color": "rgba(221,68,255,0.90)"},
        {"key": "sy", "title": "系统占用", "color": "rgba(255,255,255,0.90)"},
        {"key": "memUsage", "title": "内存使用率", "color": ""},
        {"key": "id", "title": "Cpu空闲", "color": "rgba(92,184,92,0.90)"},
        {"key": "swap", "title": "Swap空闲", "color": "rgba(240,173,78,0.90)"}
    ];

    var width = window.screen.width;
    //iphone6以下屏幕大小
    if (width < 375) {
        this.screen = 1;
    } else if (width >= 375 && width < 1280) {
        this.screen = 2;
    } else {
        this.screen = 3;
    }

    var _this = this;
    $(window).resize(function () {
        window.setTimeout(function () {
            if ( _this.chartWidth == 0 || $(window).width()!= _this.chartWidth ) {
                _this.chartWidth = $(window).width();
                _this.resize();
                //$("#cpu-chart").find("div").first().css("width","100%").find("canvas").first().css("width","100%");
            }
        },500);

    });

};

;JobXChart.prototype.query = function () {
    var self = this;
    $.ajax({
        url: self.path + "/record.do",
        type: "POST",
        dataType: "json",
        data: {
            "startTime": $("#startTime").val(),
            "endTime": $("#endTime").val()
        }
    }).done(function (data) {
        if (data.length>0) {
            $("#overview_loader").hide();
            $("#record-report-havedata").show();
            $("#record-report-nodata").hide();
            //折线图
            var lineArray = [];
            var donutMap = new Map();
            var successSum = 0;
            var failedSum = 0;
            var killedSum = 0;
            var timeoutSum = 0;
            var lostSum = 0;
            var singleton = 0;
            var flow = 0;
            var rerun = 0;
            var auto = 0;
            var operator = 0;

            for (var i=0;i<data.length;i++) {
                lineArray.push({
                    date: data[i].date,
                    success: data[i].success,
                    failed: data[i].failed,
                    killed: data[i].killed,
                    timeout: data[i].timeout,
                    lost: data[i].lost
                });
                successSum += parseInt(data[i].success);
                failedSum += parseInt(data[i].failed);
                killedSum += parseInt(data[i].killed);
                timeoutSum += parseInt(data[i].timeout);
                lostSum += parseInt(data[i].lost);
                singleton += parseInt(data[i].singleton);
                flow += parseInt(data[i].flow);
                rerun += parseInt(data[i].rerun);
                auto += parseInt(data[i].auto);
                operator += parseInt(data[i].operator);
            }

            donutMap.put("success",successSum);
            donutMap.put("failed",failedSum);
            donutMap.put("killed",killedSum);
            donutMap.put("timeout",timeoutSum);
            donutMap.put("lost",lostSum);

            self.resizeChartData = {
                "lineArray": lineArray,
                "donutMap": donutMap,
                "singleton": singleton,
                "flow": flow,
                "rerun": rerun,
                "auto": auto,
                "operator": operator
            };
            self.changeChar(null);
            self.createChart();
        } else {
            window.setTimeout(function () {
                $("#overview_loader").hide();
                $("#record-report-havedata").hide();
                $("#record-report-nodata").show();
            }, 750);
        }
    });
}

;JobXChart.prototype.monitor = function () {

    var self = this;

    if ((arguments[0] || 0) == 1 && self.intervalId == null) {
        return;
    }

    /**
     * 没有执行器
     */
    if (!$("#agentId").val()) {
        window.setTimeout(function () {
            $(".loader").remove();
            $(".div-havedata").hide();
            $(".div-nodata").show();
        }, 1000);
        return;
    }
    $(".div-nodata").hide();
    $(".div-havedata").show();
    /**
     * 关闭上一个websocket
     */
    if (self.socket) {
        self.socket.close();
        self.socket = null;
    }

    $.ajax({
        type: "POST",
        url: self.path + "/monitor.do",
        dataType: "json",
        data: {
            "agentId":$("#agentId").val()
        }
    }).done(function (dataResult) {
        if (dataResult.toString().indexOf("login") > -1) {
            window.location.href = self.path;
        }
        //remobe loader...
        $(".loader").remove();
        var connType = dataResult.connType;
        var data = dataResult.data;
        //代理
        if (connType == 1) {
            self.data = $.parseJSON(data);
            if (self.intervalId == null) {
                /**
                 * 第一个轮询不显示,等下一个轮询开始渲染...
                 * @type {number}
                 */
                self.intervalId = window.setInterval(function () {
                    self.monitor(connType);
                }, self.intervalTime);
            } else {
                self.render();
            }
        } else {//直联-->发送websocket...
            if (self.intervalId != null) {
                window.clearInterval(self.intervalId);
                self.intervalId = null;
                self.clear();
            }

            self.socket = new io.connect(data, {
                extraHeaders: {
                    'Access-Control-Allow-Origin':this.path
                }
            });

            self.socket.on("monitor", function (data) {
                self.data = data;
                self.render();
            });
            //when close then clear data...
            self.socket.on("disconnect", function () {
                console.log('close');
                self.clear();
            });
        }
    });
}

;JobXChart.prototype.clear = function () {
    if (this.socket != null) {
        this.socket.close();
        this.socket = null;
    }
    this.data = [];
    this.diskLoad = false;
    this.cpuLoad = false;
    this.configLoad = false;
    this.config = [];
    this.cpuChartObj = null;
    this.cpuX = [];
    this.cpuY = [];
}

;JobXChart.prototype.render = function () {
    var self = this;
    $(".loader").remove();

    //解决子页面登录失联,不能跳到登录页面的bug
    if (!self.diskLoad) {
        self.diskLoad = true;
        $("#overview-chart").html("").css("height", "auto");
        $("#disk-view").html("");
        $("#disk-item").html("");

        var diskArr = $.parseJSON(self.data.diskUsage);
        var freeTotal, usedTotal;

        for (var i in diskArr) {
            var disk = diskArr[i].disk;
            var used = parseFloat(diskArr[i].used);
            var free = parseFloat(diskArr[i].free);
            var val = parseInt((used / (used + free)) * 100);

            if (disk == "usage") {
                freeTotal = free;
                usedTotal = used;
                continue;
            }

            var colorCss = "";
            if (val < 60) {
                colorCss = "progress-bar-success";
            } else if (val < 80) {
                colorCss = "progress-bar-warning";
            } else {
                colorCss = "progress-bar-danger";
            }

            var html =
                '<div class="side-border">' +
                '<h6>' +
                '   <small style="font-weight: lighter">' +
                '       <i class="glyphicon glyphicon-hdd"></i>&nbsp;&nbsp;' + disk + '&nbsp;&nbsp;(已用:' + used + 'G/空闲:' + free + 'G)' +
                '   </small>' +
                '   <div class="progress progress-small">' +
                '       <a href="#" data-toggle="tooltip" title="" class="progress-bar tooltips ' + colorCss + '" style="width: ' + val + '%;" data-original-title="' + val + '%">' +
                '           <span class="sr-only">' + val + '%</span>' +
                '       </a>' +
                '   </div>' +
                '</h6>' +
                '</div>';
            $("#disk-item").append(html);
        }

        $('#disk-view').highcharts({
            chart: {
                type: 'pie',
                backgroundColor: 'rgba(0,0,0,0)',
                options3d: {
                    enabled: true,
                    alpha: 45,
                    beta: 0
                }
            },
            colors: ['rgba(76,224,105,0.45)', 'rgba(237,56,46,0.45)'],
            title: {
                text: ''
            },
            tooltip: {
                pointFormat: '{series.name}:{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    depth: 30,
                    dataLabels: {
                        enabled: false,
                        format: '{point.name}'
                    }
                }
            },
            series: [{
                type: 'pie',
                name: '占比',
                data: [
                    ['空闲磁盘', parseFloat(freeTotal)],
                    ['已用磁盘', parseFloat(usedTotal)]
                ]
            }]
        });
    }

    if (!self.configLoad) {
        self.config = $.parseJSON(self.data.config);
        self.configLoad = true;

        $.each(self.config, function (name, value) {
            var css = {
                "font-size": "15px",
                "font-weight": "900",
                "color": "rgba(255,255,255,0.8)",
                "margin-top": "3px"
            };

            if (typeof(value) == 'string') {
                if (!value) {
                    $("#view-" + name).remove();
                } else {
                    $("#config-" + name).css(css).html(value);
                }
            } else {
                $.each(value, function (k, v) {
                    if (!v) {
                        $("#view-" + name + "-" + k).remove();
                    } else {
                        $("#config-" + name + "-" + k).css(css).html(v);
                    }
                });
            }
        });
        $("#config-view").fadeIn(1000);

    }

    if (!self.cpuLoad) {
        self.cpuLoad = true;
        var overData = [];
        $.each(self.overviewDataArr, function (i, elem) {
            $.each(self.data, function (name, obj) {
                if (elem.key == name) {
                    if (name == "swap") {
                        overData.push([elem.key, 100.00 - parseFloat(obj)]);
                    } else {
                        overData.push([elem.key, parseFloat(obj)]);
                    }

                }
            });
            var _data = $.parseJSON(self.data.cpuData);
            for (var k in _data) {
                if (elem.key == k) {
                    overData.push([k, parseFloat(_data[k])]);
                }
            }
        });

        $.each(overData, function (i, obj) {
            var key = obj[0];
            var val = obj[1];
            var title = "";

            $.each(self.overviewDataArr, function (k, v) {
                if (i == k) {
                    title = v.title;
                }
            });

            if (i == 2) {
                if (self.screen == 2) {
                    var html =
                        '<div class="gauge-cpu-warp">' +
                        '   <div id="gauge-cpu" class="gaugeChart_m"></div>' +
                        '   <span class="gaugeChartTitle" >' +
                        '       <i class="icon" >&#61881;</i>&nbsp;' + title + '' +
                        '   </span>' +
                        '</div>';
                    $("#overview-chart").append(html);
                } else {
                    var html =
                        '<div class="gauge-cpu-warp">' +
                        '   <div id="gauge-cpu" class="gaugeChart"></div>' +
                        '   <span class="gaugeChartTitle" >' +
                        '       <i class="icon" >&#61881;</i>&nbsp;' + title + '' +
                        '   </span>' +
                        '</div>';
                    $("#overview-chart").append(html);
                }
                self.gauge = echarts.init(document.getElementById('gauge-cpu'));
                self.gaugeOption = {
                    series: [
                        {
                            name: '内存使用率',
                            type: 'gauge',
                            startAngle: 180,
                            endAngle: 0,
                            center: ['50%', '90%'],
                            radius: 115,
                            axisLine: {
                                lineStyle: {
                                    color: [[0.2, 'rgb(92,184,92)'], [0.8, 'rgb(240,173,78)'], [1, 'rgb(237,26,26)']],
                                    width: 6
                                }
                            },
                            axisTick: {
                                show: true,
                                splitNumber: 4,
                                length: 8,
                                lineStyle: {
                                    color: 'auto'
                                }
                            },
                            splitLine: {
                                show: true,
                                length: 10,
                                lineStyle: {
                                    color: 'auto'
                                }
                            },
                            pointer: {
                                width: 4,
                                length: '90%',
                                color: 'rgba(255, 255, 255, 0.8)'
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                show: true,
                                backgroundColor: 'rgba(0,0,0,0)',
                                borderWidth: 0,
                                borderColor: '#ccc',
                                offsetCenter: [0, -30],
                                formatter: '{value}%',
                                textStyle: {
                                    fontSize: 20
                                }
                            },
                            data: [{value: 50}]
                        }
                    ]
                };
                self.gaugeOption.series[0].data[0].value = parseFloat(val);
                self.gauge.setOption(self.gaugeOption, true);

            } else {
                var html = $(
                    '<div class="pie-chart-tiny" id="cpu_' + key + '">' +
                    '   <span class="percent"></span>' +
                    '       <span style="font-weight: lighter" class="pie-title" title="' + title + '(' + key + ')">' +
                    '       <i class="icon" >&#61881;</i>&nbsp;' + title + '' +
                    '   </span>' +
                    '</div>&nbsp;&nbsp;');

                html.easyPieChart({
                    easing: 'easeOutBounce',
                    barColor: self.overviewDataArr[i].color,
                    trackColor: 'rgba(0,0,0,0.3)',
                    scaleColor: 'rgba(255,255,255,0.85)',
                    lineCap: 'square',
                    lineWidth: 4,
                    animate: 3000,
                    size: self.screen == 3 ? ((i == 1 || i == 3) ? 120 : 90) : (self.screen == 1 ? 120 : 105),
                    onStep: function (from, to, percent) {
                        $(this.el).find('.percent').text(Math.round(percent));
                    }
                });
                html.data('easyPieChart').update(val);
                $("#overview-chart").append(html);
                if ($.isMobile()) {
                    $(".percent").css("margin-top", "35px");
                }
            }
        });
        self.cpuChartObj = echarts.init(document.getElementById('cpu-chart'));
        var option = {};
        self.cpuChartObj.setOption(option);
    } else {
        $.each(self.overviewDataArr, function (i, elem) {
            var overElem = $("#cpu_" + elem.key);
            if (overElem.length > 0) {
                $.each(self.data, function (name, obj) {
                    if (elem.key == name) {
                        if (name == "swap") {
                            overElem.data('easyPieChart').update(100.00 - parseFloat(obj));
                        } else {
                            overElem.data('easyPieChart').update(parseFloat(obj));
                        }
                    }
                });
                var _data = $.parseJSON(self.data.cpuData);
                for (var k in _data) {
                    if (elem.key == k) {
                        overElem.data('easyPieChart').update(parseFloat(_data[k]));
                    }
                }
            }
        });

        //添加新的
        self.cpuX.push(self.data.time);
        self.cpuY.push(parseFloat($.parseJSON(self.data.cpuData)["usage"]));

        if (self.cpuY.length == 60 * 10) {
            self.cpuX.shift();
            self.cpuY.shift();
        }

        var max = parseInt(Math.max.apply({}, self.cpuY)) + 1;
        if (max > 100) {
            max = 100;
        }

        var opt = {
            tooltip: {
                trigger: 'axis',
                formatter: "监&nbsp;控&nbsp;时&nbsp;间&nbsp;&nbsp;: {b} <br />CPU使用率 : {c}%",
                textStyle: {
                    fontSize: 12
                }
            },
            title: {
                left: 'center',
                text: ''
            },
            grid: {
                top: '9%',
                left: '0%',
                right: '2%',
                bottom: '8%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                show: false,
                boundaryGap: false,
                splitLine: {show: false},
                data: self.cpuX
            },
            yAxis: {
                type: 'value',
                boundaryGap: [0, '100%'],
                splitLine: {show: false},
                max: max,
                axisLabel: {
                    show: true,
                    textStyle: {color: 'rgba(255,255,255,0.80)'}
                },
                axisLine: {
                    lineStyle: {color: 'rgba(220,220,255,0.6)'}
                }
            },
            dataZoom: [{
                type: 'inside',
                start: 0,
            }, {
                start: 0,
                backgroundColor: 'rgba(0,0,0,0.05)',
                dataBackgroundColor: 'rgba(0,0,0,0.2)',
                fillerColor: 'rgba(0,0,0,0.3)',
                handleColor: 'rgba(0,0,0,0.9)',
                textStyle: {color: '#aaa'}
            }],
            series: [
                {
                    type: 'line',
                    smooth: false,
                    symbol: 'none',
                    sampling: 'average',
                    itemStyle: {
                        normal: {
                            color: 'rgba(255, 255, 255,0.3)'
                        }
                    },
                    areaStyle: {
                        normal: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                offset: 0,
                                color: 'rgba(255, 255, 255,0.3)'
                            }, {
                                offset: 1,

                                color: 'rgba(20, 70, 155,0.2)'
                            }])
                        }
                    },
                    data: self.cpuY
                }
            ]
        };

        self.cpuChartObj.setOption(opt);
        self.gaugeOption.series[0].data[0].value = parseFloat(self.data.memUsage);
        self.gauge.setOption(self.gaugeOption, true);
    }

    //topInfo...
    //@JSONType(orders={"pid","user","virt","res","cpu","mem","time","command"})
    var text = "<tr>";
    $.each($.parseJSON(self.data.top), function (i, data) {
        var obj = $.parseJSON(data);
        switch (self.screen) {
            case 1:
                //小屏去掉pid,user,virt,res...
                for (var k in obj) {
                    if ('pid' === k || 'user' === k || 'virt' === k || 'res' === k) {
                        continue;
                    }
                    if ('cpu' === k || 'mem' === k) {
                        text += ("<td>" + obj[k] + "%</td>");
                    } else {
                        text += ("<td>" + obj[k] + "</td>");
                    }
                }
                break
            case 2:
                //中屏去掉virt,res
                for (var k in obj) {
                    if ('virt' === k || 'res' === k) {
                        continue;
                    }
                    if ('cpu' === k || 'mem' === k) {
                        text += ("<td>" + obj[k] + "%</td>");
                    } else {
                        text += ("<td>" + obj[k] + "</td>");
                    }
                }
                break
            case 3:
                //大屏,全部字段显示。。。
                for (var k in obj) {
                    if ('cpu' === k || 'mem' === k) {
                        var val = obj[k];
                        var colorCss = "";
                        if (val < 60) {
                            colorCss = "progress-bar-success";
                        } else if (val < 80) {
                            colorCss = "progress-bar-warning";
                        } else {
                            colorCss = "progress-bar-danger";
                        }
                        var cpu =
                            '<td>' +
                            '   <div class="status pull-right bg-transparent-black-1" style="margin-left: 5px;font-size: 10px;">' +
                            '       <span id="agent_number" class="animate-number" data-value="100.00" data-animation-duration="1500">' + val + '</span>%' +
                            '   </div>' +
                            '   <div class="progress progress-small progress-white">' +
                            '       <div class="progress-bar ' + colorCss + '" role="progressbar" data-percentage="' + val + '%" style="width:' + val + '%" aria-valuemin="0" aria-valuemax="100"></div>' +
                            '   </div>' +
                            '</td>';
                        text += cpu;
                    } else {
                        text += ("<td>" + obj[k] + "</td>");
                    }
                }
                break;
        }
        text += '</tr>';
    });

    switch (self.screen) {
        case 1:
            var shtml = '<tr>' +
                '<td class="noborder" style="width: 25%" title="CPU使用占比">CPU</td>' +
                '<td class="noborder" style="width: 25%" title="内存使用占比">MEM</td>' +
                '<td class="noborder" style="width: 25%" title="持续时长">TIME</td>' +
                '<td class="noborder" style="width: 25%" title="所执行的命令">COMMAND</td>' +
                '</tr>';
            $("#topbody").html(shtml+text);
            break;
        case 2:
            var mhtml = '<tr>' +
                '<td class="noborder" style="width: 10%" title="进程ID">PID</td>' +
                '<td class="noborder" style="width: 20%" title="进程所属的用户">USER</td>' +
                '<td class="noborder" style="width: 20%" title="CPU使用占比">CPU</td>' +
                '<td class="noborder" style="width: 15%" title="内存使用占比">MEM</td>' +
                '<td class="noborder" style="width: 15%" title="持续时长">TIME</td>' +
                '<td class="noborder" style="width: 20%" title="所执行的命令">COMMAND</td>' +
                '</tr>';
            $("#topbody").html(mhtml+text);
            break
        case 3:
            var lhtml = '<tr>' +
                '<td class="noborder" style="width: 10%" title="进程ID">PID</td>' +
                '<td class="noborder" style="width: 10%" title="进程所属的用户">USER</td>' +
                '<td class="noborder" style="width: 10%" title="虚拟内存">VIRI</td>' +
                '<td class="noborder" style="width: 10%" title="常驻内存">RES</td>' +
                '<td class="noborder" style="width: 15%" title="CPU使用占比">CPU</td>' +
                '<td class="noborder" style="width: 15%" title="内存使用占比">MEM</td>' +
                '<td class="noborder" style="width: 13%" title="持续时长">TIME</td>' +
                '<td class="noborder" style="width: 17%" title="所执行的命令">COMMAND</td>' +
                '</tr>';
            $("#topbody").html(lhtml+text);
            break
    }
}

;JobXChart.prototype.resize = function () {
    if ( this.resizeChartData.lineArray == undefined || this.resizeChartData.lineArray.length == 0) {
        $("#overview_report").html("");
        $("#overview_pie").html('');
        $("#overview_loader").hide();
        $("#record-report-havedata").hide();
        $("#record-report-nodata").show();
    }else {
        if ($.isMobile()) {
            $("#overview_pie_div").remove();
            $("#overview_report_div").removeClass("col-xs-9").addClass("col-xs-12")
        } else {
            if ($(window).width() < 1280) {
                $("#overview_pie_div").hide();
                $("#overview_report_div").removeClass("col-xs-9").addClass("col-xs-12")
            } else {
                $("#overview_report_div").removeClass("col-xs-12").addClass("col-xs-9");
                $("#overview_pie_div").show();
            }
        }
        this.createChart();
    }
}

;JobXChart.prototype.changeChar = function (map) {
    if ( map == null ) {
        if ( this.chartItem.item == null ) {
            map = new Map();
            map.put("success","success");
            map.put("failed","failed");
            map.put("killed","killed");
            this.chartItem.item = map;
        } else {
            map = this.chartItem.item;
        }
    }else {
        this.chartItem.item = map;
    }

    var json = [
        {
            key:"success",
            color:"rgba(32,192,92,0.7)"
        },
        {
            key:"failed",
            color:"rgba(237,73,73,0.7)"
        },
        {
            key:"timeout",
            color:"rgba(254,212,42,0.7)"
        },
        {
            key:"killed",
            color:"rgba(11,98,164,0.7)"
        },
        {
            key:"lost",
            color:"rgba(222,220,219,0.7)"
        }
    ];

    var titleColor = '#afd7ff';
    var labels = [];
    var colors = [];
    var line = [];
    var donut = [];
    var donutSum = 0;
    var entry = map.entrys();

    for (var i=0;i<entry.length;i++) {
        var key = entry[i].key;
        for (var j=0;j<json.length;j++) {
            if (json[j].key == key) {
                labels.push(key);
                colors.push(json[j].color);
            }
        }
        donut.push({
            label:key,
            value:this.resizeChartData.donutMap.get(key)
        });
        donutSum += this.resizeChartData.donutMap.get(key);
    }
    colors.push(titleColor);

    for ( var d = 0;d<this.resizeChartData.lineArray.length;d++ ) {
        var obj = this.resizeChartData.lineArray[d];
        var objData = {
            "date":obj["date"]
        }
        for (var k in obj) {
            if( map.get(k)!=null ) {
                objData[k] = obj[k];
            }
        }
        line.push(objData);
    }
    this.chartItem.line = line;
    this.chartItem.donut = donut;
    this.chartItem.labels = labels;
    this.chartItem.colors = colors;
    this.chartItem.titleColor = titleColor;
    this.chartItem.donutSum = donutSum;
}

;JobXChart.prototype.createChart = function () {
    var _this = this;
    //线型报表
    $("#overview_report_div").show();
    $("#overview_report_bar").show();
    $("#overview_report").html("");
    Morris.Line({
        element: $('#overview_report'),
        data:this.chartItem.line,
        xkey: 'date',
        ykeys: this.chartItem.labels,
        labels: this.chartItem.labels,
        lineColors: this.chartItem.colors,
        pointSize: 0,
        pointStrokeColors: this.chartItem.colors,
        gridTextColor:'rgba(225,225,225,0.8)',
        lineWidth: 3,
        resize: false
    });

    //屏幕大于1024显示饼状图
    if ($(window).width() >= 1280) {
        $("#overview_pie").html('');
        $("#overview_pie_div").show();
        Morris.Donut({
            element: $('#overview_pie'),
            data: _this.chartItem.donut,
            colors:_this.chartItem.colors,
            formatter:function(y) {
                if (y ==0) {
                    return "0 (0%)";
                }
                var val = (y/_this.chartItem.donutSum) * 100;
                return  y + " (" + val.toFixed(0) + "%)"
            },
            backgroundColor:'none',
            labelColor: _this.chartItem.titleColor,
            resize: false
        });
    }
}

