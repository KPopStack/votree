<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<jsp:useBean id="dateObject" class="java.util.Date" />
<div class="row">
  <c:forEach var="votree" items="${votrees}" varStatus="loop">
    <c:choose>
    <c:when test="${userId == votree.proposerId }"><c:set var="myVotree" value="myVotree"/></c:when>
    <c:otherwise><c:set var="myVotree" value=""/></c:otherwise>
    </c:choose>
    <div id="${votree.id}" value='${votree.type}' class='col-md-3 v-btn-left votree-box' >
      <p id="span-title" class="span-length-limit">
        <c:if test="${votree.type eq 2}">
          <i class="fa fa-lock"></i>
        </c:if><span class="span-my-votree ${myVotree}">${fn:escapeXml(votree.title)}</span>&nbsp; 
      </p>
      <jsp:setProperty name="dateObject" property="time" value="${votree.dueDatetime }" />
      <i class="fa fa-user"> </i><span class="span-my-votree ${myVotree}">${fn:escapeXml(votree.proposerName)}</span> &nbsp; &nbsp; 
      <i class="fa fa-users"> </i><span class="span-my-votree ${myVotree}"> ${votree.turnout} <br></span> 
      <i class="fa fa-clock-o"> </i>
      <span class="span-my-votree ${myVotree}"><fmt:formatDate value="${dateObject}" pattern="yyyy년 MM월 dd일까지" /></span>
      <c:set value="${loop.count}" scope="request" var="loopCount"></c:set>
      <form action="/detail/${votree.id}" method="post" class="votree-hidden-form fade in active" style="display: none">
        <input type="password" class="votree-pwd votree-input-box" placeholder="비밀번호" name="votreeHiddenInput" style="display: inline" />
        <button type="submit" class="btn btn-default votree-pwd-button" style="display: inline">확인</button>
      </form>
    </div>
  </c:forEach>
</div>
