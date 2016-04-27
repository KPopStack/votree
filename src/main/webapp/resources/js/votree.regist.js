/*
 * Created by Sang Woo Chon on 2016. 1.21
 */

(function (){
  
  setVoteTabEvent();
  setSidebarEvent();
  
  $('#vote-regist-finish').click(function(e){
    registVote();
  });
  
  $('#vote-edit-finish').click(function(e){
    editVote();
  });
  
  $('#vote-confirm-modal').on('hidden.bs.modal', function(){
    location.href="/"; // 페이지 이동 모달창이 사라지면 메인으로 이동
  });
  
  function setVoteTabEvent(){
    var isDefaultTab = true;
    addTab(isDefaultTab);
    
    $('.vote-regist-tab-content').slimScroll({
      height: '550px'
    });
    
    $('#add-tab').click(function(e){
      if($('#tabs li').length<5){
        addTab();
      }
      else{
        alert("투표탭은 5개이상 만들 수 없습니다.");
      }
    });
    
  }

  function setSidebarEvent(){
    setDatetimePicker();
    $('#is-private').click(function(e){
       if ($(this).is(':checked')) {
        $(e.target).parent().parent().append('<input type="text" class="form-control" id="private-password">');
        setPasswordValidator();
      } 
      else {
        $('#private-password').remove();
      }
    });
  }

  function setPasswordValidator(){
    var $passwordErrMsg = $("#password-err-msg");
    
    $("#private-password").keyup(function (e) {
      var keyCode = e.keyCode;
      if (keyCode != 8 && keyCode != 0 && (keyCode < 48 || keyCode > 57)) {
        $passwordErrMsg.html("숫자만 가능합니다.").show().fadeOut(3000);
        $(this).val('');
        return false;
     }
      if($(e.target).val().length > 3){
        $passwordErrMsg.html(" 4자리만 가능합니다.").show().fadeOut(3000);
        $(this).val($(this).val().substr(0,4)); 
        return false;
      }
    });
  }

  function addTab(isDefaultTab){
    var tabsNum, addBtn, addedTab;
    
    tabsNum = $('.tabs-list').length;
    addBtn = $('#add-tab');
    addedTab = '<li role="presentation">'
                 +  '<a id="vote-tab-'+ tabsNum +'" href="#vote-' + tabsNum + '" class="tabs-list vote-tabs" role="tab" data-toggle="tab">'
                 +    '<h8>투표' + tabsNum + '</h8>'
                 +  '<i id="vote-del-' + tabsNum + '" class="pull-right fa fa-minus fa-2x" style="position:absoulte; display:none"></i>'
                 +  '</a>'
                 + '</li>';
    
    $(addedTab).insertBefore(addBtn);
    
    if(isDefaultTab){
      $('#vote-del-1').remove();
    }

    
    addTabContent(tabsNum);
    addSidebarConfig(tabsNum);
    setAddedTabEvent(tabsNum);

  }

  function setAddedTabEvent(tabsNum){
    /*
     * 탭 삭제 버튼 show/hide 이벤트
     */
    $('.tabs-list').each(function(index){
      var $this =$(this);
      if(!$this.hasClass('default-tab')){
        $this.mouseenter(function(e){
          var voteId = $(this).attr('id').substring(9);
          $('#vote-del-'+voteId).show();
        })
        $this.mouseleave(function(e){
          var voteId = $(this).attr('id').substring(9);
          $('#vote-del-'+voteId).hide();
        })
      }
    });
    
    
    /*
     * 탭 삭제 버튼 이벤트
     */
    $('#vote-del-'+tabsNum).each(function(index){
      var $this =$(this);
      $this.click(function(e){
        e.stopPropagation();
        var voteId = $this.attr('id').substring(9);
        $('#vote-tab-'+(voteId-1)).tab('show');
        deleteTab(voteId);
      })
    })
    
    /*
     * 탭 추가/삭제시 이벤트
     */  
    $('.tabs-list').on('shown.bs.tab', function (e) {
      var $this = $(this);
      var previousTab = e.relatedTarget;
      if($this.text() === "+"){
        $(previousTab).tab('show');
        $('.vote-flexible-config .sidebar-item').hide();
        $('#is-duplicate-' + previousTab.id.substring(9)).parent().parent().show();
      }
      else{
        $('.vote-flexible-config .sidebar-item').hide();
        $('#is-duplicate-' + $this.attr('id').substring(9)).parent().parent().show();
      }
    });
    
    /*
     * 질문 추가 버튼 클릭 이벤트
     */
    $('#add-question-'+tabsNum).click(function(e){
      var currentTabId = e.target.id.substring(13);
      if($('#vote-' + currentTabId +' .question-list').length<10){
        addQuestion(currentTabId);
      }      
      else{
        alert("질문을 11개이상 만들 수 없습니다!");
      }
    });
    
    /*
     * 질문 추가 이벤트
     */
    $('.add-question').each(function(index){
      $('#vote-tab-'+index).click(function(e){
        $('.vote-flexible-config .sidebar-item').hide();
        $('#is-duplicate-1').parent().parent().show();
      })
    })
    
    /*
     * 질문 카테고리 이벤트
     */
    $('#vote-' + tabsNum + '-question-type-1').change(function(e){
      var voteId = $(this).attr('id').charAt(5);
      var questionId = $(this).attr('id').charAt(21);
      var currentType = $(this).val();
      chooseQuestion(currentType, voteId, questionId);
    });
  }

  function addTabContent(tabsNum){
    var addedTabContent = 
      '<div role="tabpanel" class="vote-tab tab-pane active" id="vote-' + tabsNum + '">'
    +   '<div class="vote-attribute vote-attribute-topic col-md-12">'
    +     '<label for="vote-name-'+tabsNum+'">투표 주제</label>'
    +     '<input type="text" class="vote-topic form-control" id="vote-name-'+ tabsNum +'">'
    +     '<label for="vote-name-'+tabsNum+'">투표 항목</label>'
    +   '</div>'
    +   '<div class="vote-attribute vote-attribute-add-btn col-md-5 pull-right">'
    +      '<i id="add-question-' + tabsNum +'" class="add-question pull-right fa fa-plus-square-o fa-3x"></i>'
    +   '</div>'
    +   '<div class="question-list" id="vote-' + tabsNum + '-attribute-1">'
    +     '<div class="pull-left question-number">1. </div>'
    +     '<div class="vote-attribute col-md-5">'
    +       '<select id="vote-' + tabsNum + '-question-type-1" class="form-control">'
    +         '<option>텍스트</option>'
    +         '<option>이미지</option>'
    +         '<option>비디오</option>'
    +       '</select>'
    +     '</div>'
    +     '<div class="vote-item-value col-md-12">'
    +       '<input type="text" class="vote-item-input text-input form-control" id="vote-' + tabsNum + '-question-1">'
    +       '<span id="vote-' + tabsNum + '-value-err-msg-1"></span>'
    +      '</div>'
    +     '</div>'
    +   '</div>';
   
    $('.vote-regist-tab-content').append(addedTabContent);
    $('#tabs a[href="#vote-'+tabsNum+'"]').tab('show');
  }
  function addSidebarConfig(tabsNum){
    var addedSidebarConfig = 
      '<li class="sidebar-item list-group-item">중복 투표'
     +   '<div class="material-switch pull-right">'
     +     '<input id="is-duplicate-' + tabsNum + '" name="isDuplicate" type="checkbox"/>'
     +     '<label for="is-duplicate-' + tabsNum + '" class="label-primary"></label>'
     +   '</div>'
     + '</li>';
    $('.vote-flexible-config').append(addedSidebarConfig);
  }

  function addQuestion(currentTabNum){
    
    var questionNum, addedQuestion;
       

    questionNum = $('#vote-'+currentTabNum+ ' .question-list').length+1;
    addedQuestion =   
                          '<div class="question-list" id="vote-' + currentTabNum + '-attribute-' + questionNum + '">'
                        +   '<div class="pull-left question-number">' + questionNum + '.</div> '
                        +   '<div class="vote-attribute vote-attribute-del-btn col-md-5 pull-right">'
                        +      '<i id="vote-' + currentTabNum + '-delete-question-' + questionNum + '" class="pull-right fa fa-minus-square-o fa-3x vote-attribute"></i>'
                        +   '</div>'
                        +   '<div class="vote-attribute col-md-5">'
                        +     '<select id="vote-' + currentTabNum + '-question-type-' + questionNum + '" class="vote-item form-control">'
                        +       '<option>텍스트</option>'
                        +       '<option>이미지</option>'
                        +       '<option>비디오</option>'
                        +     '</select>'
                        +  '</div>'
                        +  '<div class="vote-item-value col-md-12">'
                        +    '<input type="text" class="vote-item-input text-input form-control" id="vote-' + currentTabNum + '-question-' + questionNum + '">'
                        +    '<span id="vote-' + currentTabNum + '-value-err-msg-' + questionNum + '"></span>'
                        +   '</div>'
                        + '</div>';
    $('#vote-'+ currentTabNum).append(addedQuestion);
    
    $('#vote-' + currentTabNum + '-question-type-'+ questionNum).change(function(e){
      var $el = $(e.target);
      var voteId = $el.attr('id').substr(5,2).replace('-','');
      var questionId = $el.attr('id').substr(21,2).replace(' ','');
      var currentType = $(this).val();
      chooseQuestion(currentType, voteId, questionId);
    });
    
    $('#vote-'+ currentTabNum + '-delete-question-'+questionNum).click(function(e){
      var $el = $(e.target);
      var voteId = $el.attr('id').substr(5,2).replace('-','');
      var questionId = $el.attr('id').substr(23,2).replace(' ','');
      deleteQuestion(voteId, questionId);
    });
  }

  function deleteTab(voteId){
    
    $('#vote-tab-'+voteId).parent().remove();
    $('#vote-'+voteId).remove();
    $('.vote-tabs').each(function(index){
      var $this, currentIndex = index+1;
      $this = $(this);
      $this.attr('href','#vote-' + currentIndex);
      $this.children('i').attr('id','vote-del-' + currentIndex);
      $this.attr('id','vote-tab-' + currentIndex);
      $this.children('h8').text('투표'+ currentIndex);
    });
    
    $('.vote-tab').each(function(index){
      var $this, currentIndex = index+1;
      $this = $(this);
      $this.children('.vote-attribute-topic').children('input').attr('id', 'vote-name-'+ currentIndex);
      $this.children('.vote-attribute-add-btn').children('i').attr('id', 'add-question-'+ currentIndex);
      $this.attr('id','vote-'+ currentIndex);
    });
    $('#vote-'+ voteId +' .question-list').each(function(index){
      var $this = $(this), currentIndex = index+1, $voteItemValue = $this.children('.vote-item-value'),
      $voteItemValueChildren = $voteItemValue.children();
      
      $this.attr('id','vote-'+voteId+'-attribute-'+ currentIndex);
      $this.children('.question-number').text(currentIndex+". ");
      $this.children('.vote-attribute-del-btn').children('i').attr('id','vote-' + voteId + '-delete-question-' + currentIndex);
      $this.children('.vote-attribute').children('select').attr('id','vote-' + voteId + '-question-type-' + currentIndex);
      $voteItemValue.children('span').attr('id','vote-' + voteId + '-err-msg-' + currentIndex);
      $voteItemValue.children('input').attr('id','vote-' + voteId + '-question-' + currentIndex);        
      if($voteItemValueChildren.hasClass('file-input')){
        $voteItemValueChildren.children('div').find('.file-preview-image').attr('id','vote-' + voteId + '-question-img-' + currentIndex);
        $voteItemValue.children('div').find('.file').attr('id','vote-' + voteId + '-question-img-' + currentIndex);
        $voteItemValue.children('label').attr('id','vote-' + voteId + '-question-' + currentIndex);
      }
    });
  }

  function chooseQuestion(type, voteId, questionId) {
    var voteQuestionId, voteImageInputId,
    
    voteQuestionId = 'vote-' + voteId + '-question-' + questionId; 
    voteImageInputId = 'vote-' + voteId + '-image-input-' + questionId;
    
    
    if(type === "텍스트") {
      if($('#vote-'+voteId+'-attribute-'+questionId).children('.vote-item-value').children('.file-input').length) {
        $('#vote-'+voteId+'-attribute-'+questionId).children('.vote-item-value').children('.file-input').remove();      
      }
      if($('#vote-'+voteId+'-youtube-thumnail-'+questionId)) {
        $('#vote-'+voteId+'-youtube-thumnail-'+questionId).remove();
      }
        
      $('#'+voteQuestionId).replaceWith('<input type="text" class="vote-item-input text-input form-control" id="' + voteQuestionId + '">');
    }
    else if(type === "이미지") {
      if($('#vote-'+voteId+'-youtube-thumnail-'+questionId)) {
        $('#vote-'+voteId+'-youtube-thumnail-'+questionId).remove();
      }
      
      $('#'+voteQuestionId).replaceWith(
          '<label id="' + voteQuestionId + '" class="control-label"></label>'
        + '<input id="vote-' + voteId + '-image-input-' + questionId + '" type="file" class="file vote-item-input text-input">'
      );
      
      $('#'+voteImageInputId).fileinput({
        'allowedFileTypes' : ['image'],
        'allowedFileExtensions' : ['jpg', 'gif', 'png'],
        'showUpload':false,
        'showRemove':false,
        'showCaption':false,
        'showUpload' :false,
        'showPreview' : true,
        'showClose' : false,
        'browseIcon' : '<i class="fa fa-cloud-upload"></i>',
        'previewFileType':'any',
        'resizeImage': true,
        'maxFileCount' : 1,
        'resizePreference' : 'height',
        'maxFileSize' : 5200
      });
      $('#'+voteImageInputId).on('fileimagesloaded', function(event) {
        $(this).parent().remove();
      }).on('fileloaded', function(event, file, previewId, index, reader) {
        $('#'+previewId+ ' img').attr('id', 'vote-' + voteId + '-question-img-'+questionId);
        resizeAndShowFile(file, voteId,questionId);
      });
    }
    
    else if(type === "비디오"){
      if($('#vote-'+voteId+'-attribute-'+questionId).children('.vote-item-value').children('.file-input').length){
        $('#vote-'+voteId+'-attribute-'+questionId).children('.vote-item-value').children('.file-input').remove();
      }
      
      $('#'+voteQuestionId).replaceWith('<input type="text" value="https://"class="youtube-input vote-item-input form-control" id="' + voteQuestionId + '">');
      $('#'+voteQuestionId).keyup(function(e){
        var youtubeValidateResult = validator.validateYoutubeUrl($(e.target).val()); 
        if(youtubeValidateResult){
          if(!$('#vote-' + voteId + '-youtube-thumnail-' + questionId + '').length){
            $('#'+voteQuestionId).parent().append(
                '<img src="http://img.youtube.com/vi/' + youtubeValidateResult + '/mqdefault.jpg" id="vote-' + voteId + '-youtube-thumnail-' + questionId + '" class="img-fluid center-block">'
            )
          }
          $('#vote-' + voteId + '-value-err-msg-' + questionId).html("잘못된 URL 입니다.").fadeOut("slow");
        }
        else{
          $('#vote-' + voteId + '-youtube-thumnail-' + questionId + '').remove();
          $('#vote-' + voteId + '-value-err-msg-' + questionId).html("잘못된 URL 입니다.").show();
        }
      })
    }
  }
  
  function resizeAndShowFile(file, voteId, questionId){
    var fileLoader = new FileReader(),
    canvas = document.createElement('canvas'),
    context = null,
    imageObj = new Image(),
    blob = null,
    resizedImg = null;

    canvas.id     = "hiddenCanvas";
    canvas.style.visibility   = "hidden";   
    document.body.appendChild(canvas);  

    context = canvas.getContext('2d');  
    fileLoader.readAsDataURL(file);

    fileLoader.onload = function() {
      var data = this.result; 
      imageObj.src = data;
    };

    fileLoader.onabort = function() {
      alert("The upload was aborted.");
    };

    fileLoader.onerror = function() {
      alert("An error occured while reading the file.");
    };  

    imageObj.onload = function() {  
      var ratio;
      if(this.width == 0 || this.height == 0){
        alert('Image is empty');
      } else {                
        context.clearRect(0,0,this.width,this.width);
        ratio = Math.max((this.width / 1280), (this.height / 700));
        canvas.width = this.width/ratio;
        canvas.height = this.height/ratio;
        context.drawImage(imageObj, 0, 0, this.width, this.height, 0, 0, this.width/ratio, this.height/ratio);
        resizedImg = canvas.toDataURL("image/jpeg");
        $('#vote-' + voteId + '-question-img-'+questionId).attr('src',resizedImg);
      }       
    };
  }

  function deleteQuestion(voteId, questionId){
    $('#vote-'+voteId+'-attribute-'+questionId).empty().remove();
    $('#vote-'+ voteId +' .question-list').each(function(index){
      var $this = $(this), currentIndex = index+1;
      $this.attr('id','vote-'+voteId+'-attribute-'+ currentIndex);
      $this.children('.question-number').text(currentIndex +". ");
      $this.children('.vote-attribute').children('select').attr('id','vote-' + voteId + '-question-type-' + currentIndex);
      $this.children('.vote-attribute-del-btn').children('i').attr('id','vote-' + voteId + '-delete-question-' + currentIndex);
      $this.children('.vote-item-value').children('input').attr('id','vote-' + voteId + '-question-' + currentIndex);
      
      
      $this.children('.vote-item-value').children('span').attr('id','vote-' + voteId + '-err-msg-' + currentIndex);
      $this.children('.vote-item-value').children('input').attr('id','vote-' + voteId + '-question-' + currentIndex); 
      if($this.children('.vote-item-value').children().hasClass('file-input')){
        $this.children('.vote-item-value').children().children('div').find('.file-preview-image').attr('id','vote-' + voteId + '-question-img-' + currentIndex);
        $this.children('.vote-item-value').children('div').find('.file').attr('id','vote-' + voteId + '-question-img-' + currentIndex);
        $this.children('.vote-item-value').children('label').attr('id','vote-' + voteId + '-question-' + currentIndex);
      }
    });
  }

  function setDatetimePicker(){
    var calendar, startDateTimePicker, dueDateTimePicker, currentYear, currentMonth, currentDate;
    currentYear = new Date().getFullYear();
    currentMonth = new Date().getMonth()+1;
    currentDate = new Date().getDate();
    
    
    calendar = new tui.component.Calendar({
      element: '#layer',
      titleFormat: 'yyyy년 m월',
      todayFormat: 'yyyy년 mm월 dd일 (D)'
    });

    startDateTimePicker = new tui.component.DatePicker({
      element: '#start-datetime',
      dateForm: 'yyyy-mm-dd ',
      selectableRanges: [
                         [{year: currentYear, month: currentMonth, date: currentDate}, {year: 2500, month: 12, date: 31}],
                     ],
      timePicker: new tui.component.TimePicker({})
    }, calendar);

    dueDateTimePicker = new tui.component.DatePicker({
      element: '#due-datetime',
      dateForm: 'yyyy-mm-dd ',
      date: {year: currentYear, month: currentMonth, date: currentDate+1},
      selectableRanges: [
                         [{year: currentYear, month: currentMonth, date: currentDate}, {year: 2500, month: 12, date: 31}],
                     ],
      timePicker: new tui.component.TimePicker({})
    }, calendar);
    $('#layer').on('click', function(event) {
      var $el = $(event.target);

      if ($el.hasClass('selectable')) {
        startDateTimePicker.close();
        dueDateTimePicker.close();
      }
    });
  }

  function registVote(){
    var validateResult,
        requestBodyData, votes, votesLength, 
        voteInfo, questionList, questionLength,
        questionInfo;
    
    validateResult = validator.validateVotreeRegistForm();
    if(validateResult.state){
      
      requestBodyData = {};
      votes = [];
      votesLength = $('.vote-tab').length;

      for (var i = 1 ; i <= votesLength; i++){
        voteInfo = {};
        questionList = [];
        questionLength = $('#vote-' + i + ' .question-list').length;

        for(var j = 1 ; j  <= questionLength ; j++){
          questionInfo = {};
          questionInfo.category = $('#vote-' + i + '-question-type-' + j).val();
          questionInfo.value = $('#vote-' + i + '-question-' + j).val();
          
          if(questionInfo.category === "텍스트"){
            questionInfo.categoryId = 1;
          }
          else if(questionInfo.category === "이미지"){
            questionInfo.categoryId = 2;
            questionInfo.value = $('#vote-' + i + '-question-img-'+j).attr('src')
          }
          else if(questionInfo.category === "비디오"){
            questionInfo.categoryId = 3;
            questionInfo.value = questionInfo.value.substring(questionInfo.value.length-11, questionInfo.value.length);
          }

          
          questionList.push(questionInfo);
        }
        voteInfo.previewYN = "N";
        voteInfo.topic = $('#vote-name-'+i).val();
        voteInfo.voteItems = questionList;
        voteInfo.duplicateYN = $('#is-duplicate-'+i).is(':checked') ? 'Y' : 'N';

        votes.push(voteInfo);
      }
      requestBodyData.type = $('#is-private').is(':checked') ? 2 : 1;
      requestBodyData.title = $('#votree-name').val();
      requestBodyData.startDatetime=$('#start-datetime').val();
      requestBodyData.dueDatetime=$('#due-datetime').val();
      requestBodyData.isPrivate=$('#is-private').is(':checked') ? 'Y':'N';
      requestBodyData.plainPassword=$('#private-password').val();
      requestBodyData.turnOut=0;
      requestBodyData.votes = votes;
      
      $.ajax({
        url : "api/v0.1/votrees",
        type: "POST",
        beforeSend: function (request) {
          request.setRequestHeader("userId", $('#userId').val());
        },
        data: JSON.stringify(requestBodyData),
        contentType: "application/json; charset=utf-8",
        dataType   : "json",
        timeout : 10000,
        complete    : function(data){
          var responseObj = jQuery.parseJSON(data.responseText);
          if(responseObj.header.resultCode === 200){
            var action = "/detail/"+responseObj.body;
            $('#goDetailPage').attr("action",action);
            $('#plainPasswordForPost').attr("value",requestBodyData.plainPassword);
            $('#vote-regist-modal').modal('toggle');
            $('#vote-confirm-modal').modal();//투표 생성이 완료되면 페이지 이동 모달창 등장
          }
          else{
            alert("투표생성이 실패했습니다." + responseObj.header.resultMessage);
          }
        }
      }); 
    }
    else{
      alert(validateResult.msg);
    } 
  }

  function editVote(){
    var validateResult, 
        requestBodyData, votes, votesLength,
        voteInfo, hostnameWithPort, votreeId;
    
    validateResult = validator.validateVotreeRegistForm();
      if(validateResult.state){
        requestBodyData = {};
        votes = [];
        votesLength = $('.vote-tab').length;

        for (var i = 1 ; i <= votesLength; i++){
          voteInfo = {};
          voteInfo.previewYN = "N";
          voteInfo.isDuplicate = $('#is-duplicate-'+i).is(':checked') ? 'Y' : 'N';
          votes.push(voteInfo);
        }
        requestBodyData.type = $('#is-private').is(':checked') ? 2 : 1;
        requestBodyData.previewYN = "N";
        requestBodyData.title = $('#votree-name').val();
        requestBodyData.startDatetime=$('#start-datetime').val();
        requestBodyData.dueDatetime=$('#due-datetime').val();
        requestBodyData.isPrivate=$('#is-private').is(':checked') ? 'Y':'N';
        requestBodyData.plainPassword=$('#private-password').val();
        requestBodyData.votes = votes;
        
        hostnameWithPort = window.location.origin;
        votreeId = window.location.pathname.substring(6);
        $.ajax({
          url : hostnameWithPort +"/api/v0.1/votrees/"+ votreeId,
          type: "PUT",
          data: JSON.stringify(requestBodyData),
          contentType: "application/json; charset=utf-8",
          dataType   : "json",
          complete    : function(data){
            var responseObj = jQuery.parseJSON(data.responseText);
            if(responseObj.header.resultCode === 200){
              alert("success!");
              window.location.replace("/profiles/" + $('#proposerId').val());
            }
            else{
              alert("Fail! " + responseObj.header.resultMessage);
            }
          }
        }); 
    }
    else{
      alert(validateResult.msg);
    }
  }
})();

