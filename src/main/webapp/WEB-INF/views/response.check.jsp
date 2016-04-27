<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="/resources/css/libs/notosanskr.css">
  <title>response check</title>
  <link rel="stylesheet" href="/resources/css/libs/bootstrap.css">
  <link rel="stylesheet" href="/resources/css/home.css">
  <link rel="stylesheet" href="/resources/css/common.css">
  <link rel="stylesheet" href="/resources/css/response.check.css">
  <link rel="stylesheet" href="/resources/bower_components/tui-chart/dist/chart.min.css" />
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
  <jsp:include page="votree.header.jsp" />
  <div id="body">
    <div class="container">
      <h2>주요 페이지 현재 응답 속도</h2>
  <table class="table table-striped">
    <thead>
      <tr>
        <th>접속 페이지 URI</th>
        <th>가장 최근 응답 속도(ms)</th>
        <th>오늘 평균 응답 속도(ms)</th>
        <th>오늘 페이지 조회수(번)</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>/</td>
        <td>${currentResult.result0 }</td>
        <td>${currentResult.todayResult0 }</td>
        <td>${currentResult.todayCount0 }</td>
      </tr>
      <tr>
        <td>/detail</td>
        <td>${currentResult.result1 }</td>
        <td>${currentResult.todayResult1 }</td>
        <td>${currentResult.todayCount1 }</td>
      </tr>
      <tr>
        <td>/voting</td>
        <td>${currentResult.result2 }</td>
        <td>${currentResult.todayResult2 }</td>
        <td>${currentResult.todayCount2 }</td>
      </tr>
      <tr>
        <td>/result</td>
        <td>${currentResult.result3 }</td>
        <td>${currentResult.todayResult3 }</td>
        <td>${currentResult.todayCount3 }</td>
      </tr>
      <tr>
        <td>/search</td>
        <td>${currentResult.result4 }</td>
        <td>${currentResult.todayResult4 }</td>
        <td>${currentResult.todayCount4 }</td>
      </tr>
      <tr>
        <td>/profiles</td>
        <td>${currentResult.result5 }</td>
        <td>${currentResult.todayResult5 }</td>
        <td>${currentResult.todayCount5 }</td>
      </tr>
            
    </tbody>
  </table>
      <h2>주요 페이지 통계 그래프</h2>
      <div class="votree-input-box-middle">
        <input type="text" class="form-control votree-input-width" id="input-year" placeholder="연도(yyyy)" value="2016"/>
        <input type="text" class="form-control votree-input-width" id="input-month" placeholder="월(MM)"/>
        <input type="button" class="btn btn-default" id="input-summit" value="검색"/>
      </div>
      <div id="averageGraph" class=""></div>
      <div class="votree-graph-boundary"></div>
      <div id="sdGraph" class=""></div>
    </div>
  
  </div>
  <jsp:useBean id="toDay" class="java.util.Date" />
  <fmt:formatDate value="${toDay}" pattern="yyyy" var="now" />
  <jsp:include page="votree.footer.jsp" />
  <script src="/resources/bower_components/tui-code-snippet/code-snippet.min.js"></script>
  <script src="/resources/bower_components/tui-component-effects/effects.min.js"></script>
  <script src="/resources/bower_components/raphael/raphael-min.js"></script>
  <script src="/resources/bower_components/tui-chart/dist/chart.min.js"></script>
  <script src="/resources/js/response.check.js"></script>
</body>



</html>
