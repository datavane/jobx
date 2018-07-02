
window.alert = function(msg) {
    toastr.success(msg);
}

function alertMsg(msg) {
    swal({
        title: "",
        text: msg,
        type: "success",
        timer: 1200,
        showCancelButton: false,
        closeOnConfirm: false,
        showConfirmButton: false
    });
}

function ajax(params,successCallback,errorCallBack) {
    jQuery.ajax({
        headers: params.headers||{},
        type:  params.type||"GET",
        url:params.url,
        data:params.data||{},
        dataType:params.dataType||"json",
        success:function (data) {
            if (successCallback) {
                successCallback(data);
            }
        },
        error: function () {
            if (errorCallBack) {
                errorCallBack();
            }else {
                $(".jobx_loading").remove();
                alert("网络繁忙请刷新页面重试!");
            }
        }
    });
}

function goback() {
    swal({
        title: "",
        text: "您确定要离开这个页面吗?如果离开将丢失页面内容？",
        type: "warning",
        showCancelButton: true,
        closeOnConfirm: false,
        confirmButtonText: "返回"
    }, function() {
        history.back();
    });
}

var jobx = {
    testIp:function(ip){
        var reg = /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/;
        return reg.test(ip);
    },
    testPort:function(port){
        var reg = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
        return reg.test(port);
    },
    testEmail:function(mail){
        var reg = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
        return reg.test(mail);
    },
    testMobile:function(mobile){
        var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
        return reg.test(mobile);
    },
    testNumber:function(number){
        var reg = /^[0-9]*[0-9][0-9]*$/;
        return reg.test(number);
    },
    testQq:function(qq){
        var reg = /^[1-9]\d{5,11}$/;
        return reg.test(qq);
    },

    tipOk:function (el) {
        var pel;
        if (typeof(el) == "string") {
            pel = $(el).parent()
            el = $(el).next();
        }else {
            pel = el.parent()
        }
        var okStyle = pel.hasClass("col-md-9")?'right:-30px':'';
        if (pel.find(".ok").length == 0){
            var okHtml = "<span class='ok'><font class='big-green' color='green' style='"+okStyle+"'><i class='glyphicon glyphicon-ok-sign'></i></font></span>";
            el.before($(okHtml));
        }
        pel.find(".tips").css("visibility","hidden");
    },

    tipError:function (el,message) {
        if (typeof(el) == "string"){
            el = $(el).next();
        }
        var pel = el.parent();
        pel.find(".ok").remove();
        pel.find(".tips").css("visibility","visible").html("<font color='red'><i class='glyphicon glyphicon-remove-sign'></i>&nbsp;"+message+"</font>");
    },
    tipDefault:function (el) {
        if (typeof(el) == "string"){
            el = $(el).next();
        }
        if (el.hasClass("ok")){
            el = el.next();
        }
        el.html(el.attr("tip")).css("visibility","visible");
    }
};

function Loading() {
    this.loadingId = "loading_"+(new Date().getTime());
    $('body').append(
        "<div class='modal fade in jobx_loading' id='"+this.loadingId+"' tabindex='-1' role='dialog' aria-hidden='false' style='display: block;'>" +
        "<figure>" +
        "    <div class='dot white'></div>" +
        "    <div class='dot'></div>" +
        "    <div class='dot'></div>" +
        "    <div class='dot'></div>" +
        "    <div class='dot'></div>" +
        "</figure>" +
        "</div>"
    );
    this.exit = function (fn) {
        var _this = this;
        window.setTimeout(function () {
            $("#"+_this.loadingId).fadeIn(500,function () {
                $(this).remove();
                fn();
            });
        },500);
    }
}

function toBase64(text){
    return  Base64.encode(text);
}

function passBase64(text){
    return  Base64.decode(text);
}

function escapeHtml(text) {
    if(text){
        return text.replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#39;") ;
    }else {
        return "";
    }
}

function unEscapeHtml(text) {
    if(text){
        return text.replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&amp;/g,"").replace(/&quot;/g,"").replace(/&#39;/,"'");
    }
}