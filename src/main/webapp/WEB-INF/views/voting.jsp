<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>투표하기</title>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="stylesheet" href="/resources/css/voting.css">
  <link rel="short icon" href="/resources/image/favicon.ico" type="image/x-icon">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <div id="body">
    <div class="container">
      <div role="tabpanel">

        <!-- Nav tabs -->
        <ul class="nav nav-tabs" role="tablist" id="voting-tab">
          <c:forEach var="vote" items="${subVoteList}" varStatus="loop">
            <li role="presentation" class="<c:if test="${loop.count == 1}"><c:out value="active" /></c:if>"><a href="#vote${loop.count}" aria-controls="vote${loop.count}" role="tab"
              data-toggle="tab"><c:out value="투표 ${loop.count}" /></a></li>
            <c:set var="totalTabCount" value="${loop.count}" />
          </c:forEach>
        </ul>
        <!-- Tab panes -->
        <div class="tab-content" id="voting-tab-content">
          <c:forEach var="vote" items="${subVoteList}" varStatus="loop">
            <div role="tabpanel" class="vote-tab tab-pane fade <c:if test="${loop.count == 1}"><c:out value="in active" /></c:if>" id="vote${loop.count}" voteId="${vote.voteId}">
              <!-- 투표옵션에 따른 상태변수 설정 -->
              <c:choose>
                <c:when test="${vote.duplicateYN == 'Y'}">
                  <c:set var="itemType" value="checkbox" />
                  <c:set var="duplicateState" value="Y" />
                </c:when>
                <c:otherwise>
                  <c:set var="itemType" value="radio" />
                  <c:set var="duplicateState" value="N" />
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${vote.previewYN == 'Y'}">
                  <c:set var="preViewState" value="bg-success" />
                </c:when>
                <c:otherwise>
                  <c:set var="preViewState" value="bg-danger" />
                </c:otherwise>
              </c:choose>
              <!-- 상단 좌측 보트리 이름 / 우측:각 투표별 설정(중복,중간집계 여부) -->
              <div class="row">
                <div class="col-md-12">
                  <h2>&nbsp; ${fn:escapeXml(title)}</h2>
                </div>
              </div>
              <hr>
              <!-- 투표 제목(주제) 과 해당 투표의 항목들  -->
              <div class="panel panel-default" id="voting-vote-panel">
                <div class="row panel-heading">
                  <div class="col-md-9">
                    <p class="votree-sub-title">${fn:escapeXml(vote.topic)}</p>
                  </div>
                  <div class="col-md-3 votree-info-box">
                    <c:if test="${duplicateState eq 'Y'}">
                      <p class="statusVote"><span class="votree-font-color-red">여러개의 항목</span>에 투표가 가능합니다!</p>
                    </c:if>
                    <c:if test="${duplicateState eq 'N'}">
                      <p class="statusVote"><span class="votree-font-color-red">하나의 항목</span>에만 투표가 가능합니다!</p>
                    </c:if>
                  </div>
                </div>
                <div class="panel-body">
                  <c:forEach var="item" items="${vote.voteItemList}" varStatus="inLoop">
                    <c:choose>
                    <c:when test="${item.isVoted == 1}">
                      <c:set var="selected" value="selected-vote" />
                    </c:when>
                    <c:otherwise>
                      <c:set var="selected" value="" />
                    </c:otherwise>
                    </c:choose>
                    <div class='${itemType} vote-item ${selected }'>
                      <label class="votree-label-width"> <input class="opt-vote-item" type="${itemType}" name="opt-vote-${loop.count}" id="opt-vote-${loop.count}" value="${item.itemId}"
                        <c:if test="${item.isVoted == 1}" >checked</c:if>> 
                        <c:choose>
                          <c:when test="${item.categoryId == 2}">
                            <img class="vote_item_img" alt="alpha-votree" src="${fileStorageControllerUrl}${fn:escapeXml(item.value)}">
                          </c:when>
                          <c:when test="${item.categoryId == 3}">
                            <iframe width="600" height="350" src="https://www.youtube.com/embed/${fn:escapeXml(item.value)}" frameborder="0" allowfullscreen></iframe>
                          </c:when>
                          <c:otherwise>
                            <span class="vote-item-text"><c:out value="${item.value}" /></span>
                          </c:otherwise>
                        </c:choose>
                      </label>
                    </div>
                  </c:forEach>
                  <div class="votree-button-align-right">
                    <c:choose>
                      <c:when test="${loop.count == 1 }">
                        <c:set var="firstTab" value="disabled" />
                      </c:when>
                      <c:otherwise>
                        <c:set var="firstTab" value="" />
                      </c:otherwise>
                    </c:choose>
                    <c:if test="${loop.count == totalTabCount}">
                      <c:set var="lastTab" value="disabled" />
                    </c:if>
                    <a class="btn btn-default btnPrevious ${firstTab}">이전</a> <a class="btn btn-default btnNext ${lastTab }">다음</a>
                    <button type="button" class="btn btn-default btn-md btn-vote" votree-id="${id}" vote-mode="${voteMode}" disabled>
                      <c:choose>
                        <c:when test="${'vote'==voteMode}">투표</c:when>
                        <c:otherwise>재투표</c:otherwise>
                      </c:choose>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
      </div>
    </div>
  </div>
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/js/validation.js"></script>
  <script src="/resources/js/voting.js"></script>
</body>
</html>

