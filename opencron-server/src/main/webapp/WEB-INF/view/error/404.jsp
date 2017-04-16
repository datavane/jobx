<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="cron" uri="http://www.opencron.org" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="${contextPath}/img/favicon.ico?resId=${resourceId}" />
    <title>Opencron 404</title>
    <style style="text/css">
        * {
            margin: 0;
            padding: 0;
            outline: none;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            -khtml-user-select: none;
            user-select: none;
            cursor: default;
            font-weight: lighter;
        }

        .whole {
            width: 100%;
            height: 100%;
            line-height: 100%;
            position: fixed;
            bottom: 0;
            left: 0;
            z-index: -1000;
            overflow: hidden;
        }

        .whole img {
            width: 100%;
            height: 100%;
        }

        .mask {
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
            background: #000;
            opacity: 0.3;
            filter: alpha(opacity=30);
        }

        .b {
            width: 100%;
            text-align: center;
            height: 400px;
            position: absolute;
            top: 25%;
        }

        .a a {
            display: block;
            float: left;
            width: 150px;
            height: 50px;
            background: #fff;
            text-align: center;
            line-height: 50px;
            font-size: 18px;
            border-radius: 25px;
            color: #333
        }

        .a a:hover {
            color: #000;
            box-shadow: #fff 0 0 20px
        }

        p {
            color: #fff;
            margin-top: 60px;
            font-size: 30px;
            font-weight: lighter;
        }

        #num {
            margin: 0 5px;
            font-weight: 400;
        }

        .notfound{width:600px;margin:0 auto;}
        .notfound p:first-child {
            text-align: center;
            font-family: cursive;
            font-size: 150px;
            font-weight: bold;
            line-height: 100px;
            letter-spacing: 5px;
            color: #fff;
        }

        .notfound p:first-child span {
            cursor: pointer;
            text-shadow: 0px 0px 2px #686868,
            0px 1px 1px #ddd,
            0px 2px 1px #d6d6d6,
            0px 3px 1px #ccc,
            0px 4px 1px #c5c5c5,
            0px 5px 1px #c1c1c1,
            0px 6px 1px #bbb,
            0px 7px 1px #777,
            0px 8px 3px rgba(100, 100, 100, 0.4),
            0px 9px 5px rgba(100, 100, 100, 0.1),
            0px 10px 7px rgba(100, 100, 100, 0.15),
            0px 11px 9px rgba(100, 100, 100, 0.2),
            0px 12px 11px rgba(100, 100, 100, 0.25),
            0px 13px 15px rgba(100, 100, 100, 0.3);
            -webkit-transition: all .1s linear;
            transition: all .1s linear;
        }

        .notfound p:first-child span:hover {
            text-shadow: 0px 0px 2px #686868,
            0px 1px 1px #fff,
            0px 2px 1px #fff,
            0px 3px 1px #fff,
            0px 4px 1px #fff,
            0px 5px 1px #fff,
            0px 6px 1px #fff,
            0px 7px 1px #777,
            0px 8px 3px #fff,
            0px 9px 5px #fff,
            0px 10px 7px #fff,
            0px 11px 9px #fff,
            0px 12px 11px #fff,
            0px 13px 15px #fff;
            -webkit-transition: all .1s linear;
            transition: all .1s linear;
        }

        .notfound p:not(:first-child) {
            text-align: center;
            color: #666;
            font-family: cursive;
            font-size: 20px;
            text-shadow: 0 1px 0 #fff;
            letter-spacing: 1px;
            line-height: 2em;
            margin-top: -50px;
        }
    </style>
    <script type="text/javascript">
        var num = 5;
        window.setInterval(function () {
            --num;
            document.getElementById("num").innerHTML=num;
            if(num==0){
                document.getElementById("num").innerHTML=0;
                location.href="${contextPath}";
            }

        }, 1000);
    </script>
</head>

<body>
<div class="whole">
    <img src="${contextPath}/img/back.jpg" />
    <div class="mask"></div>
</div>
<div class="b">
    <div class="notfound">
        <p><span>4</span><span>0</span><span>4</span></p>
    </div>
    <p>
        亲爱哒,您要查看的页面未找到(´･ω･`)<br>
        <span id="num">5</span>秒后自动跳转到主页...
    </p>
</div>

</body>
</html>
