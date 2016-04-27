
(function(){
  var votreeData, responseObj;
  
  $('#votreeHidden').hide();
  
  $('#vote-edit-modal').on('hide.bs.modal', function (e) {
    $('.delete').remove();
  })

  $('.votree-setting').click(function(e) {  

    votreeId = $(this).attr("id");
    userId = $('#userId').val();
    $.ajax({
      type : "GET",
      url : "/api/v0.1/votrees/"+ votreeId + "/votes" ,
      headers : {"userId" : userId},
      dataType : "json",
      complete : function(data){
        responseObj = jQuery.parseJSON(data.responseText);
        votreeData = responseObj;
        createModal(responseObj);
      }
    })
  });//votree-setting click event end

  function createModal(data){
    var $votreeName = $('#votree-name');
    var $startDatetime = $('#start-datetime');
    var $dueDatetime = $('#due-datetime');
    var formattedStartDatetime = dateTimeFormatter(new Date(data.startDatetime));
    var formattedDueDatetime = dateTimeFormatter(new Date(data.dueDatetime));
    var $privatePassword = $('#private-password');

    $votreeName.val(data.title);
    $startDatetime.val(formattedStartDatetime);
    $dueDatetime.val(formattedDueDatetime);
    $privatePassword.val(data.plainPassword);

    var tabNum = data.subVoteList.length;
    addTab(data.subVoteList);
    $('#is-private').click(function(e){
      if ($(this).is(':checked')) {
        $('#private-password').removeClass("hidden");
        $('#private-password').show();
      } 
      else {
        $('#private-password').hide();
      }

    });
    $("#private-password").keyup(function (e) {
      if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
        $("#password-err-msg").html("숫자만 가능합니다.").show().fadeOut(3000);
        $(this).val('');
        return false;
      }
      if($(e.target).val().length > 3){
        $("#password-err-msg").html(" 4자리만 가능합니다.").show().fadeOut(3000);
        $(this).val($(this).val().substr(0,4));
        return false;
      }
    });

    function addTab(subTabs){
      var tabsNum, $tabs, addedTab, $activeTab;

      tabsNum = subTabs.length;
      $tabs = $('#tabs');
      for(var i = 1 ; i <= tabsNum ; i++ ){
        addedTab = '<li class="delete" role="presentation" id="tab'+i+'">'
        +  '<a id="vote-tab-'+i+'" href="#vote-'+i+'" aria-controls="vote-'+i+'"class="delete tabs-list" role="tab" data-toggle="tab">'
        +  "투표"+i
        +  '</a>'
        + '</li>';
        $tabs.append(addedTab);
      }
      $activeTab = $tabs.find('#tab1');
      $activeTab.addClass("active");
      
      addTabContent(subTabs);
      
    }

    function addTabContent(subTabs) {
      var tabsNum, addedTabContent, title, category, singleAnswer, duplicateYN, inputItem, tempUrl,
          $voteTabContent, $activeContent;
      
      tabsNum = subTabs.length;
      
      for(var i = 1; i<=tabsNum;i++){
        title = 
          '<div role="tabpanel" class="delete vote-tab tab-pane" id="vote-'+i+'">'
          + '<div id ="tabC'+i+'" class="delete vote-attribute col-md-12">'
          + '<label class="delete" for="vote-topic">투표 주제</label>'  
          + '<input type="text" class="delete vote-topic form-control" id="vote-name-' +i+'"value=' + subTabs[i-1].topic +' disabled>'
          + '<label class="delete" for="vote-question">투표 항목</label>'
          + '</div>'
          + '<div class="delete vote-attribute col-md-5 pull-right"></div>'
          + '</div>';
        $("#voteTitle").append(title);

        for(var j = 1; j <= data.subVoteList[i-1].voteItemList.length; j++){
          switch(subTabs[i-1].voteItemList[j-1].categoryId) {
          case 1:
            category = '<option>텍스트</option>';
            inputItem = '<input type="text" class="delete vote-item-input form-control" id="vote-'+i+'-question-'+j+'" value='+ data.subVoteList[i-1].voteItemList[j-1].value +' disabled>'
            break;
          case 2:
            category = '<option>이미지</option>'; 
            inputItem = '<img src="/files/'+ data.subVoteList[i-1].voteItemList[j-1].value +'" style="height:160px"/>';
            break;
          case 3:
            category = '<option>동영상</option>';
            tempUrl = data.subVoteList[i-1].voteItemList[j-1].value;
            inputItem = '<iframe width="300" height="160" src="https://www.youtube.com/v/' + tempUrl + '" frameborder="0" allowfullscreen></iframe>';
            break;
          default:
            category = '<option>타입오류</option>';
          }
          singleAnswer = 
            '<div class="delete question-list" id="vote-'+i+'-attribute-'+j+'">'
            + '<div class="delete pull-left question-number">'+j+'</div>'
            + '<div id="category'+i+'-'+j+'" class="delete vote-attribute col-md-5">'
            + '<select id="vote-'+i+'-question-type-'+j+'" class="delete vote-item-type form-control" disabled>'
            + category
            + '</select>'
            + '</div>'
            + '<div id="inputdata'+i+'-'+j+'" class="delete vote-item-value col-md-12">'
            + inputItem
            + '</div>'
            + '<span class="delete" id="vote-'+i+'-value-err-msg-'+j+'"></span>';
          + '</div>'
          $('#vote-'+i).append(singleAnswer);
        }// j-loop end
        
        switch(subTabs[i-1].duplicateYN) {
        case 'Y':
          category = '<input class="delete label-primary" id="is-duplicate-'+i+'" name="isDuplicate" type="checkbox" checked />'; break;
        case 'N':
          category = '<input class="delete label-primary" id="is-duplicate-'+i+'" name="isDuplicate" type="checkbox" />'; break;
        }
       
        duplicateYN =
                 '<li class="sidebar-item list-group-item delete" style="display:none">중복 투표'
            +      '<div id = "checkbox'+i+'" class="material-switch pull-right">'  
            +         category
            +         '<label for="is-duplicate-'+i+'" class="label-primary"></label>'
            +       '</div>'
            +    '</li>'
        
        $('.vote-flexible-config').append(duplicateYN);
        
        $('.vote-regist-tab-content').slimScroll({
          height: '550px'
        });
        
      }//i-loop end
      
      $activeContent = $("#voteTitle").find("#vote-1");
      $activeContent.addClass("in active");
      
      $('.tabs-list').on('shown.bs.tab', function (e) {
        $this = $(this);
        previousTab = e.relatedTarget;
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

      $('#is-duplicate-1').parent().parent().show();
      
      
    }//function addTabContent end
    
    setDatetimePicker();


  }//function create modal end

   


  $('#vote-edit-finish').click(function(e){
    var votreeId = votreeData.id;
    var requestBodyData = {};
    var votes = [];
    var votesLength = $('.vote-tab').size();
    var userId =$('#userId').val();
    var voteInfo, questionList, questionLength, questionInfo;
    var validateResult;
    
    
    validateResult = validator.validateVotreeRegistForm();
    if(validateResult.state){
      for (var i = 1 ; i <= votesLength; i++){
        voteInfo = {};
        questionList = [];
        questionLength = $('#vote-' + i + ' .question-list').size();
  
        for(var j = 1 ; j  <= questionLength ; j++){
          questionInfo = {};
          questionInfo.category = $('#vote-' + i + '-question-type-' + j).val();
          questionInfo.value = $('#vote-' + i + '-question-' + j).val();
          if(questionInfo.category === "비디오"){
            questionInfo.value = questionInfo.value.substring(questionInfo.value.length-11, questionInfo.value.length);
          }
          else if(questionInfo.category === "이미지"){
            questionInfo.value = $('#vote-' + i + '-question-img-'+j).attr('src')
          }
  
          questionList.push(questionInfo);
        }
        voteInfo.id = votreeData.subVoteList[i-1].voteId;
        voteInfo.voteName = $('#vote-name-'+i).val();
        voteInfo.itemList = questionList;
        voteInfo.isDuplicate = $('#is-duplicate-'+i).is(':checked') ? "Y" : "N";
        votes.push(voteInfo);
      }
      requestBodyData.type = $('#is-private').is(':checked') ? 2 : 1;
      requestBodyData.votreeTitle = $('#votree-name').val();
      requestBodyData.startDatetime=$('#start-datetime').val();
      requestBodyData.dueDatetime=$('#due-datetime').val();
      requestBodyData.isPrivate=$('#is-private').is(':checked');
      requestBodyData.plainPassword=$('#private-password').val();
      requestBodyData.votes = votes;
      document.getElementById('votreeHiddenInput').value = requestBodyData.plainPassword;
  
      $.ajax({
        url : "/api/v0.1/votrees/"+votreeId,
        type : "PUT",
        data: JSON.stringify(requestBodyData),
        dataType : "json",
        headers : {"userId" : userId},
        contentType: "application/json; charset=utf-8",
        complete    : function(data){
          var responseObj = jQuery.parseJSON(data.responseText);
          if(responseObj.header.resultCode === 200){
            alert("수정이 성공하였습니다.")
            $('.delete').remove();
            $('#votreeHidden').submit();
          }
          else {
            alert("투표수정이 실패했습니다." + responseObj.header.resultMessage);
          }
        }
      });//ajax end
    }
    else{
      alert(validateResult.msg);
    }
  }); //vote-edit-finish end

  function setDatetimePicker(){
    var calendar, startDateTimePicker, dueDateTimePicker, 
    currentYear, currentMonth, currentDate,
    startDate, endDate;
    currentYear = new Date().getFullYear();
    currentMonth = new Date().getMonth()+1;
    currentDate = new Date().getDate();

    startDate = new Date(votreeData.startDatetime);
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
                         date: {
                           year: startDate.getFullYear(),
                           month: startDate.getMonth()+1,
                           date: startDate.getDate()
                         },
                         timePicker: new tui.component.TimePicker({
                           defaultHour: startDate.getHours(),
                           defaultMinute: startDate.getMinutes()
                         })
    }, calendar);
    endDate = new Date(votreeData.dueDatetime);
    dueDateTimePicker = new tui.component.DatePicker({
      element: '#due-datetime',
      dateForm: 'yyyy-mm-dd ',
      selectableRanges: [
                         [{year: currentYear, month: currentMonth, date: currentDate}, {year: 2500, month: 12, date: 31}],
                         ],
                         date: {
                           year: endDate.getFullYear(),
                           month: endDate.getMonth()+1,
                           date: endDate.getDate()
                         },
                         timePicker: new tui.component.TimePicker({
                           defaultHour: endDate.getHours(),
                           defaultMinute: endDate.getMinutes()
                         })
    }, calendar);

    $('#layer').on('click', function(event) {
      var $el = $(event.target);
      if ($el.hasClass('selectable')) {
        startDateTimePicker.close();
        dueDateTimePicker.close();
      }
    });
  }//setDateTimePicker function end

  function dateTimeFormatter(inputDateTime){
    var inputYear = inputDateTime.getFullYear();
    var inputMonth = addZeroValue(inputDateTime.getMonth() + 1);
    var inputDay = addZeroValue(inputDateTime.getDate());
    var inputHour = addZeroValue(inputDateTime.getHours());
    var inputMinutes = addZeroValue(inputDateTime.getMinutes());

    return inputYear+'-'+inputMonth+"-"+inputDay+" "+inputHour+":"+inputMinutes;
  }

  function addZeroValue(input){
    if(input < 10) {
      input = "0" + input;
    }
    return input;
  }
})();