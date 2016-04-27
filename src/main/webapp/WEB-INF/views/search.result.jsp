<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>검색결과</title>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/search.result.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <div id="body">
    <div class="container search-result-container">
      <div class="row">
        <div class="col-md-12 votree-search-title">사용자</div>
      </div>
      <div class="row">
        <c:set value="0" var="searchedUserCount"></c:set>
        <c:forEach var="searchedUser" items="${users}" varStatus="loop">
          <button id="${searchedUser.id }" class="col-md-4 v-btn-left user-box">
            <div class="row">
              <div class="col-md-4">
                <img src="${fn:escapeXml(searchedUser.profileUrl)}" class="votree-user-img img-circle">
              </div>
              <div class="col-md-8">
                <p id="span-title" class="span-length-limit">${fn:escapeXml(searchedUser.name) }</p>${fn:escapeXml(searchedUser.email) }<br> from ${searchedUser.oauthProvider }
              </div>
            </div>
          </button>
          <c:set value="${loop.count}" var="searchedUserCount"></c:set>
        </c:forEach>
      </div>
      <c:if test="${searchedUserCount < 1}">
          <span class="votree-no-result">사용자 검색 결과가 없습니다!</span>
      </c:if>
      <!-- row end -->


      <div class="row">
        <div class="col-md-12 votree-search-title">Votree</div>
      </div>
      <c:set var="votrees" value="${votrees }" scope="request"/>
      <jsp:include page="votree.grid.show.jsp"/>
      <c:if test="${empty loopCount}">
          <span class="votree-no-result">Votree 검색 결과가 없습니다!</span>
      </c:if>
      <!-- row end -->
    </div>
    <!-- search-result-container end -->
  </div>
  <!-- body end -->
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/home.js"></script>
  
</body>
</html>