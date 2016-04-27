<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<title>결과보기화면</title>
<link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
<link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
<link rel="stylesheet" href="/resources/css/common.css">
<link rel="stylesheet" href="/resources/css/home.css">
<link rel="stylesheet" href="/resources/css/result.css">
<link rel="stylesheet" href="/resources/bower_components/tui-chart/dist/chart.min.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <input type="hidden" id="totalResultList" value='${fn:escapeXml(jsonString_total)}'>
  <input type="hidden" id="topResultList" value='${fn:escapeXml(jsonString_top)}'>
  <div id="body">
    <div class="container result-container">
      <div class="row votree-margin">
        <div class="col-md-9">
          <p class="votree-title span-length-limit">${fn:escapeXml(votree.title)}<span class="votree-title-result"> 투표 결과</span>
          </p>
        </div>
        <c:set var="now" value="<%=new java.util.Date()%>" />
        <input type="hidden" id="votree-id-for-ajax" value="${votree.id }" />
        <div class="col-md-3 votree-img-align-right">
          <c:choose>
            <c:when test="${votree.dueDatetime > now}">
              <img class="votree-finish-img" src="/resources/image/notfinished.png">
            </c:when>
            <c:otherwise>
              <img class="votree-finish-img" src="/resources/image/finished.png">
            </c:otherwise>
          </c:choose>
        </div>
      </div>
      <div class="row votree-top-choice-border">
        <div class="col-md-2">
          <div class="votree-result-tab votree-top-choice votree-top-choice-active" data-toggle="tooltip" title="Top Choice!" data-placement="top"></div>
          <c:if test="${textItemOnly == true}">
            <div class="votree-result-tab votree-best-choice" data-toggle="tooltip" title="Votree Choice!" data-placement="top"></div>
            <div class="votree-result-tab votree-personal-choice" data-toggle="tooltip" title="Same Choice!" data-placement="top"></div>
          </c:if>
        </div>
        <!-- TOP CHOICE  -->
        <div class="col-md-10 votree-padding-left votree-top-choice-content">
          <div class="row">
            <c:choose>
              <c:when test="${!empty topResults}">
                <span class="votree-choice-highlight">TOP CHOICE!</span>
                <br>
                <span class="votree-result-info">각 투표 주제당 <span class="votree-result-highlight">가장 많이</span> 득표한 항목들 입니다.</span>
              </c:when>
              <c:otherwise>
                <span class="votree-result-info">투표 결과가 존재하지 않습니다.</span>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="row">
            <c:forEach var="topResult" items="${topResults}">
              <a id='${topResult.voteId}' class="btn btn-default btn-lg btn-huge result-item span-length-limit" data-toggle="tooltip" title="${fn:escapeXml(topResult.topic)}">
                <c:choose>
                  <c:when test="${topResult.voteItemCategory eq 1}">
                ${fn:escapeXml(topResult.value)}
              </c:when>
                  <c:when test="${topResult.voteItemCategory eq 2}">
                Image
              </c:when>
                  <c:when test="${topResult.voteItemCategory eq 3}">
                Video
              </c:when>
                </c:choose><input type="hidden" value="${fn:escapeXml(topResult.value)}" id="vote-item-hidden-value" /> <input type="hidden" value="${fn:escapeXml(topResult.voteItemCategory)}"
                  id="vote-item-hidden-category" />
              </a>
            </c:forEach>
          </div>
          <div class="row votree-result-img-box">
            <input type="hidden" value="${fileStorageControllerUrl}" id="votree-fileStorage-url" />
            <div id="graph" class="votree-tui-chart-display"></div>
            <img id="vote-item-image" src="" class="vote-item-image" />
            <iframe id="vote-item-video" width="400" height="300" src="" frameborder="0" allowfullscreen></iframe>
          </div>
        </div>
        <!-- TOP CHOICE DIV END -->

        <!-- BEST CHOICE 수정필요!!!!!-->
        <div class="col-md-10 votree-padding-left votree-best-choice-content">
          <div class="row">
            <c:choose>
              <c:when test="${!empty bestResults}">
                <span class="votree-choice-highlight">VOTREE CHOICE!</span>
                <br>
                <span class="votree-result-info"><span class="votree-result-highlight">VOTREE가 추천&nbsp;</span>하는 투표 결과입니다. 참여자들이 주로 어떤 생각을 하는지 살펴보세요!<br/>
               <b> 나뭇가지</b>를 클릭해주세요!</span>
              </c:when>
              <c:otherwise>
                <span class="votree-result-info">Votree Choice 결과가 존재하지 않습니다.</span>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="row" style="margin-bottom:25px; margin-top: 25px;">
            <c:forEach var="bestResult" items="${bestResults}" varStatus="loop" begin="0" end="3">
              <c:if test="${fn:length(bestResult.branch.user) > 1}">
              <div id='best-choice-${loop.count}' class="btn-huge best-choice-button votree-branch-default votree-branch"></div>
              </c:if>
            </c:forEach>
          </div>
          <c:forEach var="bestResult" items="${bestResults }" varStatus="inLoop" begin="0" end="3">
            <div class="row best-additional-info-box best-choice-${inLoop.count}">
              <p class="votree-result-info" style="margin-bottom:20px;">
              <span class="votree-result-highlight">총 ${fn:length(bestResult.branch.user)}명</span>이 아래와 같은 응답을 했습니다.</p>
              <c:forEach var="vote" items="${bestResult.branch.vote}">
                <a class="btn btn-default btn-huge-best" data-toggle="tooltip" title="${fn:escapeXml(vote.topic)}">${fn:escapeXml(vote.vote_item_value) }</a>
              </c:forEach>
              <c:forEach var="user" items="${bestResult.branch.user}">
              <div class="row" style="margin-top: 25px">
                <div class="col-md-3">
                  <img src="${user.profileUrl}" class="col-md-12" />
                </div>
                <div class="col-md-9 votree-name-email-info">
                  <span class="votree-result-info"> 이름 : ${fn:escapeXml(user.name) }<br> <c:if test="${user.email != '' }">이메일 : ${fn:escapeXml(user.email) }</c:if></span>
                </div>
              </div>
              </c:forEach>      
            </div>
          </c:forEach>
        </div>
        <!-- BEST CHOICE END -->


        <!-- PERSONAL CHOICE -->
        <div class="col-md-10 votree-padding-left votree-personal-choice-content">
          <div class="row">
            <c:choose>
              <c:when test="${!empty sameChoiceUsers}">
                <span class="votree-choice-highlight">SAME CHOICE!</span>
                <br>
                <span class="votree-result-info"><span class="votree-result-highlight">${fn:escapeXml(userName)}</span>님과 같은 투표를 한 회원들 입니다.</span>
              </c:when>
              <c:otherwise>
                <span class="votree-result-info">Same Choice 결과가 존재하지 않습니다.</span>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="row" style="margin-bottom: 25px;">
            <c:forEach var="sameResult" items="${sameChoiceUsers}">
              <div id='personal-choice-${sameResult.id}' class="btn btn-default btn-lg btn-huge span-length-limit votree-personal-choice-box" data-toggle="tooltip"
                title="${fn:escapeXml(sameResult.email)}">${fn:escapeXml(sameResult.name)}</div>
            </c:forEach>
          </div>
          <c:forEach var="sameResult" items="${sameChoiceUsers}">
            <div class="row personal-additional-info-box personal-choice-${sameResult.id}">
              <div class="col-md-3">
                <img src="${sameResult.profileUrl}" class="col-md-12" />
              </div>
              <div class="col-md-9 votree-name-email-info">
                <span class="votree-result-info"> 이름 : ${fn:escapeXml(sameResult.name) }<br> <c:if test="${sameResult.email != '' }">이메일 : ${fn:escapeXml(sameResult.email) }</c:if></span>
              </div>
            </div>
          </c:forEach>
          <!-- PERSONAL CHOICE END -->
        </div>
      </div>
      <!-- Bottom Information -->
      <div class="row">
        <div class="col-md-2">
          <img src="${proposer.profileUrl}" class="col-md-12" />
        </div>
        <div class="col-md-10">
          <fmt:formatDate pattern="yyyy년 MM월 dd일 HH시 mm분" value="${votree.startDatetime }" var="start" />
          <fmt:formatDate pattern="yyyy년 MM월 dd일 HH시 mm분" value="${votree.dueDatetime }" var="due" />
          <span class="votree-result-info"> <span class="votree-result-highlight">${fn:escapeXml(votree.title) }</span> 투표는 <span class="votree-result-highlight">${fn:escapeXml(proposer.name) }</span>님이
            등록했습니다.<br> <span class="votree-result-highlight">${start}</span> 부터 <span class="votree-result-highlight"> ${due}</span>까지<br> <span class="votree-result-highlight"> <c:choose>
                <c:when test="${votree.type eq 2}">비공개</c:when>
                <c:otherwise>공개</c:otherwise>
              </c:choose> 투표
          </span>로 진행됩니다.<br> 현재까지 <span class="votree-result-highlight"> 총 ${votree.turnout }명</span>이 참여했고, <span class="votree-result-highlight">${votree.hit }명</span>이 투표를 조회했습니다.
          </span>
        </div>
      </div>
    </div>
  </div>
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/bower_components/tui-code-snippet/code-snippet.min.js"></script>
  <script src="/resources/bower_components/tui-component-effects/effects.min.js"></script>
  <script src="/resources/bower_components/raphael/raphael-min.js"></script>
  <script src="/resources/bower_components/tui-chart/dist/chart.min.js"></script>
  <script src="/resources/js/result.js"></script>
</body>



</html>
