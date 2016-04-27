<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>Votree</title>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/error.css">
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <div id="body">
    <div class="container text-center">
    <a href="/">
      <img src="/resources/image/tree.png" class="tree-img"/></a>
        <p class="error-font">올바르지 않은 접근입니다!</p>
        <p class="error-font-message">${message}</p>
    </div>
  </div>
  <!-- body end -->
  <jsp:include page="votree.regist.jsp" />
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/home.js"></script>
</body>
</html>