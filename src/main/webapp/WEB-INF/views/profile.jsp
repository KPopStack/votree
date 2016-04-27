<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/libs/calendar.css">
  <link rel="stylesheet" href="/resources/css/libs/timepicker.css">
  <link rel="stylesheet" href="/resources/css/profile.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
  <title>프로필</title>
</head>
<body>
<div id="body">
  <jsp:include page="/WEB-INF/views/votree.header.jsp" />
  <jsp:useBean id="dateObject" class="java.util.Date" />
  <jsp:useBean id="sDateValue" class="java.util.Date" />
  <jsp:useBean id="dDateValue" class="java.util.Date" />
  <!-- user information-->
    <div class="container profile-container">
      <div class="row">
        <div class="col-md-3"></div>
        <div class="col-md-3 votree-fixed-height">
          <img src="${fn:escapeXml(user.profileUrl)}" id="userImage" class="img-circle" alt="facebook" width="200" height="200">
          <c:if test="${userId eq user.id }">
          <button id="modifiedButton" data-toggle="modal" data-target="#profileModal" class="btn btn-success btn-lg">프로필편집</button>
          </c:if>
        </div>
        <div class="col-md-5">
          <div id="votree-font">
            ${fn:escapeXml(user.name) } <br> ${fn:escapeXml(user.email)}
          </div>
        </div>
      </div>
      <!-- row end -->
    </div>
    <!-- container end -->
    <!-- votreeList -->
      <ul class="nav nav-tabs container">
        <li class="active"><a data-toggle="tab" href="#home"><span data-placement="top" data-toggle="tooltip" title="내가 등록한">진행중인 투표</span></a></li>
        <li><a data-toggle="tab" href="#menu1"><span data-placement="top" data-toggle="tooltip" title="내가 등록한">완료된 투표</span></a></li>
        <li><a data-toggle="tab" href="#menu2"><span data-placement="top" data-toggle="tooltip" title="내가 참여한 투표">내가 참여한 투표</span></a></li>
      </ul>
      <div class="tab-content">
        <div id="home" class="tab-pane fade in active">
          <div class="container">
            <c:set var="votrees" value="${runningVotrees}" scope="request"/>
            <c:set var="fromProfilePage" value="1" scope="request" />
            <jsp:include page="votree.grid.show.jsp"/>          
          </div>
          <!-- container end -->
        </div>
        <!-- home end -->
        <div id="menu1" class="tab-pane fade">
          <div class="container">
            <c:set var="votrees" value="${expiredVotrees}" scope="request"/>
            <jsp:include page="votree.grid.show.jsp"/>  
            <!-- row end -->
          </div>
          <!-- container end -->
        </div>
        <!-- menu1 end -->
        <div id="menu2" class="tab-pane fade">
          <div class="container">
            <c:set var="votrees" value="${joinedVotrees}" scope="request"/>
            <jsp:include page="votree.grid.show.jsp"/>  
            <!-- row end -->
          </div>
          <!-- container end -->
        </div>
      </div>
      <!-- tab-content end -->

    <div id="profileModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header ">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
            <h3 class="modal-title login-modal-title" id="myModalLabel">프로필편집</h3>
          </div>
          <div class="modal-body">
            <form>
              <div class="form-group">
                <label for="name">Name:</label> <input type="text" class="form-control" id="name" value="${fn:escapeXml(user.name)}">
              </div>
              <div class="form-group">
                <label for="email">Email:</label> <input type="email" class="form-control" id="email" value="${fn:escapeXml(user.email) }">
              </div>
              <input type="hidden" id="userId" value="${user.id}">
              <input type="submit" id="modifiedSubmit" class="btn btn-success btn-lg" value ="변경">
              <button data-dismiss="modal" id="test" class="btn btn-success btn-lg">취 소</button>
              <button id="imageModifiedButton" class="btn btn-success btn-lg pull-right">이미지변경</button>
            </form>
          </div>
          <!-- modal-body end -->
        </div>
        <!-- modal-content end -->
      </div>
      <!-- modal-dialog end -->
    </div>
    <!-- profileModal end -->
    
    
  </div>
  <!-- body end -->
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/js/libs/jquery.slimscroll.min.js"></script>
  <script src="/resources/js/libs/code-snippet.min.js"></script>
  <script src="/resources/js/libs/calendar.min.js"></script>
  <script src="/resources/js/libs/date-picker.min.js"></script>
  <script src="/resources/js/home.js"></script>
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/profile.js"></script>
</body>
</html>

