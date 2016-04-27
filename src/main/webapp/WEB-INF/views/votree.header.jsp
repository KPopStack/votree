<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/resources/css/login.css">
<link rel="stylesheet" href="/resources/css/votree.header.css">
<style>
  body {
    font-family: 'Noto Sans KR', sans-serif;
  }
</style>
<nav class="navbar navbar-default navbar-fixed-top votree-border-none">
  <div class="container">
    <div class="navbar-header votree-navbar">
      <a class="" href="/"><img src="/resources/image/votree_logo.png" class="votree-logo-size"></a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav navbar-right">
        <li><div class="navbar-form navbar-right">
            <div class="form-group">
              <input class="form-control typeahead" id="search-input" type="text" placeholder="Search">
            </div>
            <button id="search-button" type="button" class="btn btn-default btn-sm">
              <i class="fa fa-search"></i>
            </button>
          </div></li>
        <c:choose>
          <c:when test="${empty userId}">
            <li><button type="button" class="btn btn-sm btn-default" data-toggle="modal" style="margin-top: 8px; margin-left: 5px;" data-target="#Login">Login</button></li>
          </c:when>
          <c:otherwise>
            <li><div class="dropdown">
                <a id="dLabel" data-target="#" href="http://alpha-votree.nhnent.com/" data-toggle="dropdown" aria-haspopup="true" role="button" class="btn btn-default btn-sm" aria-expanded="false">${fn:escapeXml(userName)}<span
                  class="caret"></span>
                </a>
                <ul class="dropdown-menu dropdown-Font votree-dropdown-width" role="menu" aria-labelledby="dLabel">
                  <li><a href="/profiles/${userId}">프로필보기</a></li>
                  <li><a href="/logout">Logout</a></li>
                </ul>
              </div></li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
  <input type="hidden" id="userId" value="${userId}">
  <jsp:include page="votree.login.modal.jsp" />
  <!-- 
  <c:if test='${sessionScope.name eq "PAYCO USER" }'>
    <div class="modal fade" id="paycoUserLogin" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header ">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
            <h3 class="modal-title login-modal-title">PAYCO에서는 이름을 지원하지 않습니다. 변경해주세요</h3>
          </div>
          <div class="modal-body">
            <form>
              <div class="form-group">
                <label for="name">Name:</label> <input type="text" class="form-control" id="name" value="${sessionScope.name}">
              </div>
              <div class="form-group">
                <label for="email">Email:</label> <input type="text" class="form-control" id="email" value="">
              </div>
              <input type="hidden" id="userId" value="${sessionScope.userId}">
              <button id="modifiedSubmit" class="btn btn-success btn-lg">변 경</button>
              <button data-dismiss="modal" id="test" class="btn btn-success btn-lg">취 소</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </c:if>
   -->
</nav>
<script src="/resources/js/libs/jquery-1.12.0.js"></script>
<script src="/resources/js/libs/bootstrap.js"></script>
<script src="/resources/js/libs/typeahead.js"></script>
<script src="/resources/js/header.js"></script>
<script src="/resources/js/profile.js"></script>
