/* --------------------------------------------------------
 Flot Charts
 -----------------------------------------------------------*/

//Line Chart
/*
$(function () {
    if ($('#line-chart')[0]) {
        var d1 = [[1,14], [2,15], [3,18], [4,16], [5,19], [6,17], [7,15], [8,16], [9,20], [10,16], [11,18]];

        $.plot('#line-chart', [ {
            data: d1,
            label: "Data",

        },],

            {
                series: {
                    lines: {
                        show: true,
                        lineWidth: 1,
                        fill: 0.25,
                    },

                    color: 'rgba(255,255,255,0.7)',
                    shadowSize: 0,
                    points: {
                        show: true,
                    }
                },

                yaxis: {
                    min: 10,
                    max: 22,
                    tickColor: 'rgba(255,255,255,0.15)',
                    tickDecimals: 0,
                    font :{
                        lineHeight: 13,
                        style: "normal",
                        color: "rgba(255,255,255,0.8)",
                    },
                    shadowSize: 0,
                },
                xaxis: {
                    tickColor: 'rgba(255,255,255,0)',
                    tickDecimals: 0,
                    font :{
                        lineHeight: 13,
                        style: "normal",
                        color: "rgba(255,255,255,0.8)",
                    }
                },
                grid: {
                    borderWidth: 1,
                    borderColor: 'rgba(255,255,255,0.25)',
                    labelMargin:10,
                    hoverable: true,
                    clickable: true,
                    mouseActiveRadius:6,
                },
                legend: {
                    show: false
                }
            });

        $("#line-chart").bind("plothover", function (event, pos, item) {
            if (item) {
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                $("#linechart-tooltip").html(item.series.label + " of " + x + " = " + y).css({top: item.pageY+5, left: item.pageX+5}).fadeIn(200);
            }
            else {
                $("#linechart-tooltip").hide();
            }
        });

        $("<div id='linechart-tooltip' class='chart-tooltip'></div>").appendTo("body");
    }

});
*/

//Bar Chart
$(function () {
    if ($("#bar-chart")[0]) {
        var data1 = [[1,60], [2,30], [3,50], [4,100], [5,10], [6,90], [7,85], [8,40], [9,5]];
        var data2 = [[1,20], [2,90], [3,60], [4,40], [5,100], [6,25], [7,65], [8,5], [9,70]];
        var data3 = [[1,100], [2,20], [3,60], [4,90], [5,80], [6,10], [7,5], [8,15], [9,50]];
    
        var barData = new Array();

        barData.push({
                data : data1,
                label: 'Product 1',
                bars : {
                        show : true,
                        barWidth : 0.1,
                        order : 1,
                        fill:1,
                        lineWidth: 0,
                        fillColor: 'rgba(255,255,255,0.6)'
                }
        });
        barData.push({
                data : data2,
                label: 'Product 2',
                bars : {
                        show : true,
                        barWidth : 0.1,
                        order : 2,
                        fill:1,
                        lineWidth: 0,
                        fillColor: 'rgba(255,255,255,0.8)'
                }
        });
        barData.push({
                data : data3,
                label: 'Product 3',
                bars : {
                        show : true,
                        barWidth : 0.1,
                        order : 3,
                        fill:1,
                        lineWidth: 0,
                        fillColor: 'rgba(255,255,255,0.3)'
                },
        });

        //Display graph
        $.plot($("#bar-chart"), barData, {
                
                grid : {
                        borderWidth: 1,
                        borderColor: 'rgba(255,255,255,0.25)',
                        show : true,
                        hoverable : true,
                        clickable : true,       
                },
                
                yaxis: {
                    tickColor: 'rgba(255,255,255,0.15)',
                    tickDecimals: 0,
                    font :{
                        lineHeight: 13,
                        style: "normal",
                        color: "rgba(255,255,255,0.8)",
                    },
                    shadowSize: 0,
                },
                
                xaxis: {
                    tickColor: 'rgba(255,255,255,0)',
                    tickDecimals: 0,
                    font :{
                        lineHeight: 13,
                        style: "normal",
                        color: "rgba(255,255,255,0.8)",
                    },
                    shadowSize: 0,
                },
                
                legend : true,
                tooltip : true,
                tooltipOpts : {
                        content : "<b>%x</b> = <span>%y</span>",
                        defaultTheme : false
                }

        });
        
        $("#bar-chart").bind("plothover", function (event, pos, item) {
            if (item) {
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                $("#barchart-tooltip").html(item.series.label + " of " + x + " = " + y).css({top: item.pageY+5, left: item.pageX+5}).fadeIn(200);
            }
            else {
                $("#barchart-tooltip").hide();
            }
        });

        $("<div id='barchart-tooltip' class='chart-tooltip'></div>").appendTo("body");

    }

});

//Pie Chart
$(function(){
    var pieData = [
                    {data: 1, color: 'rgba(255,255,255,0.2)'},
                    {data: 2, color: 'rgba(255,255,255,0.8)'},
                    {data: 3, color: 'rgba(255,255,255,0.5)'},
                    {data: 4, color: 'rgba(255,255,255,0.1)'},
                    {data: 4, color: 'rgba(255,255,255,0.9)'},
                ];
    if($('#donut-chart')[0]){
        $.plot('#donut-chart', pieData, {
            series: {
                
                
                pie: {
                    
                    innerRadius: 0.5,
                    show: true,
                    stroke: { 
                        width: 0,
                        
                    },
                    fill: 1,
                    fillColor: {
                        colors : ['rgba(255, 255, 255, 0.5)', 'rgba(0, 215, 76, 0.8)', 'rgba(255,255,255,0.8)']
                    } 
                }
            }
        });
    }
    
    if($('#pie-chart')[0]){
        $.plot('#pie-chart', pieData, {
            series: {
                pie: {
                    show: true,
                    stroke: { 
                        width: 0,
                        
                    },
                    /*label: {
                        show: true,
                        radius: 3/4,
                        formatter: function(label, series){
                            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                        },
                        background: { 
                            opacity: 0.5,
                            color: '#000'
                        }
                    }*/
                }
            }
        });
    }
});

/*


//Dynamic Chart
$(function() {
    if ($('#dynamic-chart')[0]) {
        var data = [],
            totalPoints = 300;

        function getRandomData() {
            if (data.length > 0)
                data = data.slice(1);

            while (data.length < totalPoints) {
                var prev = data.length > 0 ? data[data.length - 1] : 50,
                    y = prev + Math.random() * 10 - 5;
                if (y < 0) {
                    y = 0;
                } else if (y > 90) {
                    y = 90;
                }

                data.push(y);
            }

            var res = [];
            for (var i = 0; i < data.length; ++i) {
                res.push([i, data[i]])
            }

            return res;
        }


        var updateInterval = 30;
        var plot = $.plot("#dynamic-chart", [ getRandomData() ], {
            series: {
                label: "Data",
                lines: {
                    show: true,
                    lineWidth: 1,
                    fill: 0.25,
                },

                color: 'rgba(255,255,255,0.2)',
                shadowSize: 0,
            },
            yaxis: {
                min: 0,
                max: 100,
                tickColor: 'rgba(255,255,255,0.15)',
                font :{
                    lineHeight: 13,
                    style: "normal",
                    color: "rgba(255,255,255,0.8)",
                },
                shadowSize: 0,

            },
            xaxis: {
                tickColor: 'rgba(255,255,255,0.15)',
                show: true,
                font :{
                    lineHeight: 13,
                    style: "normal",
                    color: "rgba(255,255,255,0.8)",
                },
                shadowSize: 0,
                min: 0,
                max: 250
            },
            grid: {
                borderWidth: 1,
                borderColor: 'rgba(255,255,255,0.25)',
                labelMargin:10,
                hoverable: true,
                clickable: true,
                mouseActiveRadius:6,
            },
            legend: {
                show: false
            }
        });

        function update() {
            plot.setData([getRandomData()]);
            // Since the axes don't change, we don't need to call plot.setupGrid()

            plot.draw();
            setTimeout(update, updateInterval);
        }

        update();

      /!*  $("#dynamic-chart").bind("plothover", function (event, pos, item) {
            if (item) {
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                $("#dynamic-chart-tooltip").html(item.series.label + " of " + x + " = " + y).css({top: item.pageY+5, left: item.pageX+5}).fadeIn(200);
            }
            else {
                $("#dynamic-chart-tooltip").hide();
            }
        });

        $("<div id='dynamic-chart-tooltip' class='chart-tooltip'></div>").appendTo("body");*!/
    }
});

*/



/* --------------------------------------------------------
 Sparkline
 -----------------------------------------------------------*/
(function(){
    //Bar
    $("#stats-bar-1").sparkline([6,4,8,6,5,6,7,8,3,5,9,5,8,3], {
        type: 'bar',
        height: '50px',
        barWidth: 4,
        barColor: '#fff',
        barSpacing: 3,
    });

    $("#stats-bar-2").sparkline([4,7,6,2,5,3,8,6], {
        type: 'bar',
        height: '50px',
        barWidth: 4,
        barColor: '#fff',
        barSpacing: 3
    });

    $("#stats-bar-3").sparkline([4,7,6,2,5,3,8,6], {
        type: 'bar',
        height: '50px',
        barWidth: 4,
        barColor: '#fff',
        barSpacing: 3
    });

    //Line
    $("#stats-line").sparkline([9,4,6,5,6,4,5,7,9,3,6,5], {
        type: 'line',
        width: '100%',
        height: '65',
        lineColor: 'rgba(255,255,255,0.4)',
        fillColor: 'rgba(0,0,0,0.2)',
        lineWidth: 1.25,
    });

    $("#stats-line-2").sparkline([5,6,3,9,7,5,4,6,5,6,4,9], {
        type: 'line',
        width: '100%',
        height: '65',
        lineColor: 'rgba(255,255,255,0.4)',
        fillColor: 'rgba(0,0,0,0.2)',
        lineWidth: 1.25,
    });

    $("#stats-line-3").sparkline([8,11,9,12,10,9,13,11,12,11,10], {
        type: 'line',
        height: '65',
        width: '100%',
        lineColor: 'rgba(255,255,255,0.4)',
        lineWidth: 1.25,
        fillColor: 'rgba(0,0,0,0.2)',
        barWidth: 5,
        barColor: '#C5CED6',

    });

    $("#stats-line-4").sparkline([4,6,7,8,9,5,3,6,5,6,7,5,7,2,7], {
        type: 'line',
        height: '65',
        width: '100%',
        lineColor: 'rgba(255,255,255,0.4)',
        lineWidth: 1.25,
        fillColor: 'rgba(0,0,0,0.2)',
        barWidth: 5,
        barColor: '#C5CED6',

    });

    $("#stats-line-5").sparkline([10,9,12,10,9,13,11,12,11,10,12], {
        type: 'line',
        height: '65',
        width: '80',
        lineColor: 'rgba(255,255,255,0.4)',
        fillColor: 'rgba(0,0,0,0.2)',
        lineWidth: 1.25,
        barWidth: 5,
        barColor: '#C5CED6',

    });

    $("#stats-line-6").sparkline([7,2,7,5,7,6,5,6,3,5,9,8,6,7,4], {
        type: 'line',
        height: '65',
        width: '80',
        lineColor: 'rgba(255,255,255,0.85)',
        fillColor: 'rgba(0,0,0,0.2)',
        barWidth: 5,
        barColor: '#C5CED6',
        lineWidth: 1.25,
    });

    //Tristate
    $("#stats-tristate").sparkline([2,2,-2,2,-2,-2,2,2,2,2,2], {
        type: 'tristate',
        width: '100%',
        height: '52',
        barWidth: 4,
        barSpacing: 3,
        zeroAxis: false,
        posBarColor: '#fff',
        negBarColor: '#fff',
        zeroBarColor: '#fff',
        lineWidth: 0,
        lineColor: 'rgba(0,0,0,0)',
    });
})();


/* --------------------------------------------------------
 Map
 -----------------------------------------------------------*/
$(function(){
    //USA Map
    if($('#usa-map')[0]) {
	$('#usa-map').vectorMap({
            map: 'us_aea_en',
            backgroundColor: 'rgba(0,0,0,0.25)',
            regionStyle: {
                initial: {
                    fill: 'rgba(255,2552,255,0.7)'
                },
                hover: {
                    fill: '#fff'
                },
            },
    
            zoomMin:0.88,
            focusOn:{
                x: 5,
                y: 1,
                scale: 1.8
            },
            markerStyle: {
                initial: {
                    fill: '#e80000',
                    stroke: 'rgba(0,0,0,0.4)',
                    "fill-opacity": 2,
                    "stroke-width": 7,
                    "stroke-opacity": 0.5,
                    r: 4
                },
                hover: {
                    stroke: 'black',
                    "stroke-width": 2,
                },
                selected: {
                    fill: 'blue'
                },
                selectedHover: {
                }
            },
            zoomOnScroll: false,
    
            markers :[
                {latLng: [33, -86], name: 'Sample Name 1'},
                {latLng: [33.7, -93], name: 'Sample Name 2'},
                {latLng: [36, -79], name: 'Sample Name 3'},
                {latLng: [29, -99], name: 'Sample Name 4'},
                {latLng: [33, -95], name: 'Sample Name 4'},
                {latLng: [31, -92], name: 'Liechtenstein'},
            ],
        });
    }
    
    //World Map
    if($('#world-map')[0]) {
	$('#world-map').vectorMap({
            map: 'world_mill_en',
            backgroundColor: 'rgba(0,0,0,0)',
            series: {
                regions: [{
                    scale: ['#C8EEFF', '#0071A4'],
                    normalizeFunction: 'polynomial'
                }]
            },
            regionStyle: {
                initial: {
                    fill: 'rgba(255,2552,255,0.7)'
                },
                hover: {
                    fill: '#fff'
                },
            },
        });
    }
});

/* --------------------------------------------------------
 Easy Pie Charts
 -----------------------------------------------------------*/
/*
$(function() {
    $('.pie-chart-tiny').easyPieChart({
        easing: 'easeOutBounce',
        barColor: 'rgba(255,255,255,0.75)',
        trackColor: 'rgba(0,0,0,0.3)',
        scaleColor: 'rgba(255,255,255,0.3)',
        lineCap: 'square',
        lineWidth: 4,
        size: 100,
        animate: 3000,
        onStep: function(from, to, percent) {
            $(this.el).find('.percent').text(Math.round(percent));
        }
    });

    var chart = window.chart = $('.pie-chart-tiny').data('easyPieChart');
    $('.pie-chart-tiny .pie-title > i').on('click', function() {
        $(this).closest('.pie-chart-tiny').data('easyPieChart').update(Math.random()*200-100);
    });
});*/
