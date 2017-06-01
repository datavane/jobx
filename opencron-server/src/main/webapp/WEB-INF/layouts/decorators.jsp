<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

    <jsp:include page="/WEB-INF/layouts/resource.jsp"/>

    <sitemesh:write property='head' />

</head>

<body id="skin-blur-ocean">

    <jsp:include page="/WEB-INF/layouts/top.jsp"/>

    <sitemesh:write property='body' />

    <jsp:include page="/WEB-INF/layouts/footer.jsp"/>

</body>
</html>


