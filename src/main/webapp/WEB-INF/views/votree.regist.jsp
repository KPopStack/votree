<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!-- Modal -->
<div class="modal fade" id="vote-regist-modal" tabindex="-1" role="dialog" aria-labelledby="vote-regist-modal-label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header regist-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="vote-regist-modal-Label">투표 생성</h4>
      </div>
      <!-- Regist Vote Body  -->
      <div class="votree-regist-container">
        <div class="modal-body votree-regist-body col-md-9">
          <div class="tabpanel" role="tabpanel">
            <!-- Nav tabs -->
            <ul class="nav nav-tabs" id="tabs" role="tablist">
              <li role="presentation" id="add-tab"><a href="#" class="tabs-list default-tab" role="tab" data-toggle="tab">+</a></li>
            </ul>
            <!-- Tab Contents -->
            <div class="vote-regist-tab-content tab-content">
            </div>
          </div>
        </div>
        <div class="votree-regist-sidebar col-md-3">
          <div class="vote-default-config">
            <div class="form-group">
              <label for="votree-name">투표 이름 </label> <input type="text" class="form-control" id="votree-name" > <label for="start-datetime">시작 시간</label> <input id="start-datetime" type="text"
                size="20" readonly class="form-control" style="background-color:white">
              <div id="layer" class="layer">
                <!-- TUI Calendar/Datetime Picker -->
                <div class="calendar-header">
                  <a href="#" class="rollover calendar-btn-prev-month">이전달</a> <strong class="calendar-title"></strong> <a href="#" class="rollover calendar-btn-next-month">다음달</a>
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
                <div class="calendar-footer">
                  <p>
                    오늘 <em class="calendar-today"></em>
                  </p>
                </div>
              </div>
              <label for="due-datetime">마감 시간</label> <input id="due-datetime" type="text" size="20" readonly class="form-control" style="background-color:white">

            </div>

            <li class="sidebar-item list-group-item">비밀 투표
              <div class="material-switch pull-right">
                <input id="is-private" name="isPrivate" type="checkbox" /> <label for="is-private" class="label-primary"></label>
              </div>
            </li> 
            <span id="password-err-msg"></span>
          </div>

          <div class="vote-flexible-config">
          </div>
        </div>
      </div>
      <div class="modal-footer vote-regist-footer">
        <button type="button" class="btn btn-danger" data-dismiss="modal">취소</button>
        <button id="vote-regist-finish" type="button" class="btn btn-primary">생성 완료</button>
        
      </div>
    </div>
  </div>
</div>
