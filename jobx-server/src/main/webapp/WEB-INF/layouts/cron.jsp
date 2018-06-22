<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="modal fade cronSelector" id="cronSelector" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-search">
        <div class="modal-content">
            <div class="modal-header">
                <button class="close btn-float" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
                <h4>时间表达式</h4>
            </div>
            <div class="modal-body" style="padding: 0px;">
                <table class="table tile textured" style="font-size: 13px;">
                    <thead>
                    <tr>
                        <th>年</th>
                        <th>月</th>
                        <th>日</th>
                        <th>周</th>
                        <th>时</th>
                        <th>分</th>
                        <th>秒</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <select id="year" size="8" multiple="multiple" >
                                <option value="*" selected="selected">每年</option>
                                <c:forEach var="i" begin="2018" end="2050" step="1">
                                    <option value="${i}">${i}年</option>
                                </c:forEach>
                            </select>
                          <%--  <span class="btn btn-exprepeat">从<input type="text" id="year_start">重复</span>--%>
                        </td>
                        <td>
                            <select id="month" size="8" multiple="multiple" >
                                <option value="*" selected="selected">每月</option>
                                <c:forEach var="i" begin="1" end="12" step="1">
                                    <option value="${i}">${i}月</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <select id="day" size="8" multiple="multiple" >
                                <option value="*" selected="selected" id="everyday">每日</option>
                                <c:forEach var="i" begin="1" end="31" step="1">
                                    <option value="${i}">${i}日</option>
                                </c:forEach>
                                <option value="L">末日</option>
                            </select>
                        </td>
                        <td>
                            <select id="week" size="8" multiple="multiple" >
                                <c:forEach var="i" begin="1" end="7" step="1">
                                    <option value="${i}">星期${i}</option>
                                </c:forEach>
                                <option value="*" selected="selected" id="everyweek">每星期</option>
                            </select>
                        </td>
                        <td>
                            <select id="hour" size="8" multiple="multiple" >
                                <c:forEach var="i" begin="0" end="23" step="1">
                                    <option value="${i}">${i}时</option>
                                </c:forEach>
                                <option value="*" selected="selected">每时</option>
                            </select>
                        </td>
                        <td>
                            <select id="minutes" size="8" multiple="multiple" >
                                <c:forEach var="i" begin="0" end="59" step="1">
                                    <option value="${i}">${i}分</option>
                                </c:forEach>
                                <option value="*" selected="selected">每分</option>
                            </select>
                        </td>
                        <td>
                            <select id="seconds" size="8" multiple="multiple" >
                                <option value="0" selected="selected">0秒</option>
                                <c:forEach var="i" begin="1" end="59" step="1">
                                    <option value="${i}">${i}秒</option>
                                </c:forEach>
                                <option value="*" >每秒</option>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <table class="recenttime" style="width: 100%;">
                    <tr>
                        <td style="width: 25%">最近5次执行</td>
                        <td rowspan="7" style="width: 50%;text-align: center;font-size: 35px;">
                            <span id="cronExpInput">
                                <span class="select-defexp">-- -- -- -- -- -- --</span>
                            </span>
                        </td>
                        <td rowspan="7" style="width: 25%;text-align: center">
                            <button class="btn btn-defexp" type="button" id="okexp">确定</button>
                        </td>
                    </tr>
                    <tr><td class="recent-rd" index="0"></td></tr>
                    <tr><td class="recent-rd" index="1"></td></tr>
                    <tr><td class="recent-rd" index="2"></td></tr>
                    <tr><td class="recent-rd" index="3"></td></tr>
                    <tr><td class="recent-rd" index="4"></td></tr>
                    <tr><td style="height: 20px;"></td></tr>
                </table>
            </div>
        </div>
    </div>
</div>