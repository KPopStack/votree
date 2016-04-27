<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!--Votree Edit Modal -->
<div class="modal fade" id="vote-edit-modal" tabindex="-1" role="dialog" aria-labelledby="vote-edit-modal-label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header regist-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="vote-regist-modal-Label">투표 수정</h4>
      </div>
      <!-- Regist Vote Body  -->
      <div class="votree-regist-container">
        <div class="modal-body votree-regist-body col-md-9">
          <div class="tabpanel" role="tabpanel" id="navTab">
            <!-- Nav tabs -->
            <ul class="nav nav-tabs" id="tabs" role="tablist">
            </ul>
            <div id="voteTitle" class="vote-regist-tab-content tab-content"></div>
          </div>
        </div>



        <div class="votree-regist-sidebar col-md-3 " id="config">
          <div class="vote-default-config" id="sidebar-option">
            <div class="form-group" id="sidebar-title-time">
              <label for="votree-name">투표 이름 </label> <input type="text" class="form-control" id="votree-name"> 
              <label for="start-datetime">시작 시간</label> <input id="start-datetime" readonly
                type="text" class="form-control" style="background-color:white">
              <div id="layer" class="layer" style="display: none;">
                <div class="calendar-header">
                  <a href="#" class="rollover calendar-btn-prev-month">이전달</a>' <strong class="calendar-title"></strong>' <a href="#" class="rollover calendar-btn-next-month">다음달</a>'
                </div>
                <div class="calendar-body">
                  <table cellspacing="0" cellpadding="0">
                    <thead>
                      <tr>
                        <th class="sun">S</th>
                        <th>M</th>
                        <th>T</th>
                        <th>W</th>
                        <th>T</th>
                        <th>F</th>
                        <th class="sat">S</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr class="calendar-week">
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                        <td class="calendar-date"></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <!-- calendar-body end -->
                <div class="calendar-footer">
                  <p>
                    오늘 <em class="calendar-today"></em>
                  </p>
                </div>
                <!-- calendar-footer end -->
              </div>
              <label for="start-datetime">종료 시간</label> <input id="due-datetime" readonly type="text" class="form-control" style="background-color:white">

            </div>
            <!-- sidebar-title-time end -->
            <div class="sidebar-item list-group-item" id="options">
              비밀 투표
              <div class="material-switch pull-right" id="sidevar-private">
                <c:if test="${votree.type eq 2 }">
                  <input id="is-private" name="isPrivate" type="checkbox" checked />
                  <c:set var="privateVotree" value="visible" />
                </c:if>
                <c:if test="${votree.type ne 2 }">
                  <input id="is-private" name="isPrivate" type="checkbox" />
                  <c:set var="privateVotree" value="hidden" />
                </c:if>
                <label for="is-private" class="label-primary"></label>
              </div>
              <input type="text" class="form-control ${privateVotree}" id="private-password" value="${votree.plainPassword }"> <span id="password-err-msg"></span>
            </div>
          </div>
          <!-- sidebar-option end -->
          <div class="vote-flexible-config"></div>
        </div>
        <!-- config end -->
      </div>
      <div class="modal-footer vote-regist-footer">
        <button type="button" id="vote-edit-cancle" class="btn btn-danger" data-dismiss="modal">취소</button>
        <button id="vote-edit-finish" type="button" class="btn btn-primary">수정 완료</button>
      </div>
    </div>
  </div>
</div>



<!-- Votree가 Private으로 변경될 경우 -->
<form id="votreeHidden" method="post" action="/detail/${votreeId }">
  <input id="votreeHiddenInput" type="hidden" name="votreeHiddenInput" />
  <button type="submit"></button>
</form>

<script src="/resources/js/votree.edit.js"></script>
