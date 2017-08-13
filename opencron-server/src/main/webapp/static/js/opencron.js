
window.alert = function(msg){
    swal("", msg , "error");
}

function alertMsg(msg){
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

var opencron = {
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
        var reg = /^(13|15|18|14|17)[0-9]{9}$/;
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
        var okStyle = "";
        if (typeof(el) == "string"){
            okStyle = $(el).parent().hasClass("col-md-9")?'right:-30px':'';
            el = $(el).next();
        }
        el.html($("<font class='big-green' color='green' style='"+okStyle+"'><i class='glyphicon glyphicon-ok-sign'></i></font>"))
    },
    tipError:function (el,message) {
        if (typeof(el) == "string"){
            el = $(el).next();
        }
        el.html("<font color='red'><i class='glyphicon glyphicon-remove-sign'></i>&nbsp;"+message+"</font>");
    },
    tipDefault:function (el) {
        if (typeof(el) == "string"){
            el = $(el).next();
        }
        el.html(el.attr("tip"));
    }
};

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