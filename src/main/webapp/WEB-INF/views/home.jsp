<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/votree.regist.css">
  <link rel="stylesheet" href="/resources/css/libs/calendar.css">
  <link rel="stylesheet" href="/resources/css/libs/timepicker.css">
  <title>Votree</title>
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <jsp:useBean id="dateObject" class="java.util.Date" />
  <div id="body">
    <div class="marketing-box">
      <div class="votree-goal">
        <p class="votree-goal-comment">열심히 일한 당신, 약속을 잡으려면?</p>
      </div>
      <div id="middleBox">
        <c:choose>
          <c:when test="${not empty userId}">
            <button type="button" class="btn btn-primary btn-lg col-md-4 col-md-offset-4" data-toggle="modal" data-target="#vote-regist-modal">투표 등록하기</button>
          </c:when>
          <c:when test="${empty userId}">
            <button type="button" class="btn btn-primary btn-lg col-md-4 col-md-offset-4" data-toggle="modal" data-target="#Login">투표 등록하기</button>
          </c:when>
        </c:choose>
      </div>
    </div> <!-- marketing-box end -->
    <div id="exTab2">
      <ul class="nav nav-tabs container">
        <!-- 
              voteListType : 0  default
              voteListType : 1  runningVotree
              voteListType : 2  expiredVotree
              -->
        <c:if test="${voteListType == 0 || voteListType == 1}">
          <c:set value="active" var="runningType"></c:set>
        </c:if>
        <c:if test="${voteListType == 2}">
          <c:set value="active" var="expiredType"></c:set>
        </c:if>
        <li class="${runningType}"><a href="#tab_a" data-toggle="tab">진행중인 투표</a></li>
        <li class="${expiredType}"><a href="#tab_b" data-toggle="tab">완료된 투표</a></li>
      </ul>
      <div class="tab-content">
        <div id="tab_a" class="tab-pane ${runningType}" >
          <div class="container">
          <c:set var="votrees" scope="request" value="${runningVotrees}"/>
          <jsp:include page="votree.grid.show.jsp"/>
            <div id="middleButton">
              <!-- 
                    Prev button disabled on First Page
                    Next button disabled on Last Page
                     -->
              <c:if test="${runningPageNum == 0}">
                <c:set value="disabled" var="runningFirstPageDisabled"></c:set>
              </c:if>
              <a class="btn btn-primary btn-arrow-left ${runningFirstPageDisabled}" href="/?voteListType=1&runningPageNum=${runningPageNum - 1}">이전</a>
              <c:if test="${loopCount != contentsPerPage}"><!-- TODO -->
                <c:set value="disabled" var="runningLastPageDisabled"></c:set>
              </c:if>
              <a class="btn btn-primary btn-arrow-right ${runningLastPageDisabled}" href="/?voteListType=1&runningPageNum=${runningPageNum + 1}">다음</a>
            </div> <!-- middleButton end -->
          </div> <!-- container end -->
        </div> <!--  tab_a end -->
        <div id="tab_b" class="tab-pane ${expiredType}">
          <div class="container">
            <c:set var="votrees" scope="request" value="${expiredVotrees }"/>
            <jsp:include page="votree.grid.show.jsp"/>
            <div id="middleButton">
              <c:if test="${expiredPageNum == 0}">
                <c:set value="disabled" var="expiredFirstPageDisabled"></c:set>
              </c:if>
              <a class="btn btn-primary btn-arrow-left ${expiredFirstPageDisabled}" href="/?voteListType=2&expiredPageNum=${expiredPageNum - 1}">이전</a>
              <c:if test="${loopCount != contentsPerPage}">
                <c:set value="disabled" var="expiredLastPageDisabled"></c:set>
              </c:if>
              <a class="btn btn-primary btn-arrow-right ${expiredLastPageDisabled}" href="/?voteListType=2&expiredPageNum=${expiredPageNum + 1}">다음</a>
            </div><!-- middleButton end -->
          </div><!-- container end -->
        </div><!-- tab_b end -->
      </div><!-- tab-content end -->
    </div> <!-- exTab2 end -->
  <jsp:include page="votree.login.modal.jsp" />
  </div><!-- body end -->
  <jsp:include page="votree.regist.jsp" />
  <div class="modal fade" id="vote-confirm-modal" tabindex="-1" role="dialog" aria-labelledby="vote-confirm-modal-label" aria-hidden="true">
    <div class="modal-dialog vote-confirm-modal-dialog">
      <div class="modal-content vote-confirm-modal-content">
        <div class="modal-header"><h3>투표를 성공적으로 생성했습니다.</h3></div>
        <div class="modal-body">방금 등록한 <span class="highlight-blue">투표의 상세 화면</span>으로 이동하시겠습니까?</div>
        <div class="modal-footer">
          <form method="post" id="goDetailPage" style="display:inline">
            <input type="hidden" name="votreeHiddenInput" id="plainPasswordForPost"/>
            <input type="submit" class="btn btn-primary btn-md votree-go-detail-page" value="상세화면으로 이동하기" />
          </form>
          <a class="btn btn-default btn-md" href="/">닫기</a>
        </div>
      </div>
    </div>
  </div>
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  
  <script src="/resources/js/libs/canvas-to-blob.min.js"></script>
  <script src="/resources/js/libs/fileinput.min.js"></script>  
  <script src="/resources/js/libs/jquery.slimscroll.min.js"></script>
  <script src="/resources/js/libs/code-snippet.min.js"></script>
  <script src="/resources/js/libs/calendar.min.js"></script>
  <script src="/resources/js/libs/date-picker.min.js"></script>
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/home.js"></script>
  <script src="/resources/js/votree.regist.js"></script>
</body>
</html>
