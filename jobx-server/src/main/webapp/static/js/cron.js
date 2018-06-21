
function CronInput(uri,fn) {

    $(".recent-rd").each(function (i,e) {
        $(e).text("");
    });

    $('#cronSelector').modal("show");

    $("#year,#month,#day,#week,#hour,#minutes,#seconds").click(function () {
        var cronExp = "";
        var year = $("#year").val();
        var month = $("#month").val();
        var day = $('#day') .val()||"*";
        var week = $('#week') .val()||"*";
        var hour = $("#hour").val();
        var minutes = $("#minutes").val();
        var seconds = $("#seconds").val();

        if(seconds === null){
            return;
        }

        if(day.length > 1 && day.indexOf("*") == 0){
            day = day.toString().substr(2);
        }
        if(!week){week="*";}
        if(week.length > 1 && week.indexOf("*") == 0){
            week = week.toString().substr(2);
        }
        if(week>0){
            week = parseInt(week)+1;
            if(week==8) week=1;
        }

        if(day != "*" && week != "*") {
            var nodeId = $(this).attr("id");
            if (nodeId == "day") {
                $("#week").find("option").removeAttr("selected");
                week = "*";
            }else if(nodeId == "week") {
                $("#day").find("option").removeAttr("selected");
                day = "*";
            }
        }

        if(year.length > 1 && year.indexOf("*") == 0){
            year = year.toString().substr(2);
        }
        if(month.length > 1 && month.indexOf("*") == 0){
            month = month.toString().substr(2);
        }

        if(hour.length > 1 && hour.indexOf("*") == 0){
            hour = hour.toString().substr(2);
        }
        if(minutes.length > 1 && minutes.indexOf("*") == 0){
            minutes = minutes.toString().substr(2);
        }
        if(seconds.length > 1 && seconds.indexOf("*") == 0){
            seconds = seconds.toString().substr(2);
        }
        cronExp = seconds + " " + minutes + " " + hour + " " ;
        if(week == "*"){
            cronExp += day + " " + month + " ? ";
        }else if(day == "*" && week != "*"){
            cronExp += "? " + month + " " + week + " ";
        }

        cronExp += year;
        $("#expTip").css("visibility","visible").html('请采用quartz框架的时间格式表达式,如 0 0 10 L * ? *');
        $("#cronExpInput").text(cronExp);
        ajax({
            type: "POST",
            url: uri+"/verify/recenttime.do",
            data: {
                "cronExp": cronExp
            }
        },function (data) {
            $(".recent-rd").each(function (i,e) {
                var index = $(e).attr("index");
                $(e).text(data[parseInt(index)]);
            });
        },function () {
        });
        
        $("#okexp").click(function () {
            $("#cronSelector").modal("hide");
            fn(cronExp);
        });
        
    });
}


