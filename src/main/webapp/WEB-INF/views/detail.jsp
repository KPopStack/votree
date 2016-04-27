<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<title>투표상세보기</title>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/calendar.css">
  <link rel="stylesheet" href="/resources/css/libs/timepicker.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/detail.css">
  <link rel="stylesheet" href="/resources/css/profile.css">
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <div id="body">
    <div class="container">
      <div class="row">
        <div class="col-md-8 votree-box-border">
          <div class="row">
            <div class="col-md-8 votree-button-box">
              <p class="span-length-limit votree-title">${fn:escapeXml(votree.title) }</p>
            </div>
            <div class="col-md-4 votree-button-box">
              <span class="votree-detail-size pull-right"> <br> 
              <i class="fa fa-clock-o margin-bottom"> </i>
                <fmt:formatDate value="${votree.startDatetime}" pattern="yyyy/MM/dd HH:mm" var="startDatetime" />
                <fmt:formatDate value="${votree.dueDatetime}" pattern="~ yyyy/MM/dd HH:mm" var="dueDatetime" />
                &nbsp;${startDatetime}<br>
                &nbsp;&nbsp;${dueDatetime}<br>
              
              <i class="fa fa-users margin-bottom"> </i> &nbsp; ${votree.turnout} 명 참여 <br>
              <i class="fa fa-eye margin-bottom"> </i> &nbsp; ${votree.hit } 명 조회  <br>
              <c:if test="${votree.type eq 2}">
        &nbsp;<i class="fa fa-lock margin-bottom"> </i>&nbsp; 비밀투표<br> 
                </c:if> <!--  ***TEST***<br> L:${userId }<br>p:${proposer.id },${proposer.name } -->
              </span>
            </div>
          </div>
          <div class="row">

            <div class="col-md-12 votree-info-box">
              <p class="votes-name">투표 목록</p>
              <c:forEach var="singleVote" items="${voteList }" varStatus="loop">
                <p class="span-length-limit votree-sub-size">투표 ${loop.count }.${fn:escapeXml(singleVote.topic) }</p>
              </c:forEach>
              <!--  ${voteTopicList } -->
            </div>

          </div>
          <div class="row">
            <div class="col-md-12 votree-button-box">
              <c:set var="now" value="<%=new java.util.Date()%>" />
              <c:if test="${votree.dueDatetime > now}">
                <c:choose>
                  <c:when test="${votree.votingCount gt 0}">
                    <button type="button" id="revote-join" class="btn btn-primary btn-lg votree-margin-left" value="${votree.id}">투표 수정하기</button>
                  </c:when>
                  <c:otherwise>
                    <c:if test="${userId ne null}">
                      <button type="button" id="vote-join" class="btn btn-primary btn-lg votree-margin-left" value="${votree.id}">투표 참여하기</button>
                    </c:if>
                    <c:if test="${userId eq null}">
                      <button type="button" class="btn btn-primary btn-lg votree-margin-left" data-toggle="modal" data-target="#Login">투표 참여하기</button>
                    </c:if>
                  </c:otherwise>
                </c:choose>
              </c:if>
              <input type="hidden" id="userId" value="${userId }">
              <button type="button" id="${votree.id}" class="btn btn-success btn-lg votree-margin-left votree-result">투표 결과보기</button>
              <c:if test="${userId == proposer.id && votree.dueDatetime > now}">
                <button type="button" id="${votree.id}" class="btn btn-lg votree-setting-button votree-setting" data-toggle="modal" data-target="#vote-edit-modal">
                  <i class="fa fa-cog fa-2x"></i>
                </button>
              </c:if>
            </div>
          </div>
        </div>
        <div class="col-md-4 votree-box-border">
          <div class="row votree-proposer-info">
            <p class="proposer-info">발의자 정보</p>
            <img src="${fn:escapeXml(proposer.profileUrl)}" class="proposer-img img-circle"><br> <span class="span-length-limit" style="font-size: 20px">${fn:escapeXml(proposer.name)}</span>
          </div>

        </div>

      </div>

      <!-- div row -->
    </div>
    <!-- div container -->
    <jsp:include page="/WEB-INF/views/votree.login.modal.jsp" />
    <c:set var="votree" value="${votree }" />
    <jsp:include page="/WEB-INF/views/votree.edit.jsp" />
  </div>
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/js/home.js"></script>
  <script src="/resources/js/libs/jquery.slimscroll.min.js"></script>
  <script src="/resources/js/libs/code-snippet.min.js"></script>
  <script src="/resources/js/libs/calendar.min.js"></script>
  <script src="/resources/js/libs/date-picker.min.js"></script>
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/detail.js"></script>
</body>
</html>