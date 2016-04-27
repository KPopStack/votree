
(function (){
  $('.btn-vote').click(function(e) {
    var id, mode, $this;

    $this = $(this);
    id = $this.attr('votree-id');
    mode = $this.attr('vote-mode');

    voteUserChoice(id, mode);
  });
//  var $voteItem = $('.vote-item');
//  var $optVoteItem = $('.opt-vote-item');
//  $optVoteItem.click(function(){
//    fillColorToVote($(this));
//  });
//  function fillColorToVote($this){
//    console.log($voteItem);
//    if($this.is(":checked")){
//      $this.parent().parent().addClass("selected-vote");
//    }
//    if(!$voteItem.find('.opt-vote-item').is(":checked")){
//      $voteItem.removeClass("selected-vote");
//    }
//  };
  
  var $optVoteItem = $('.opt-vote-item');
  var $voteItem = $('.vote-item');
  $optVoteItem.click(function() {
    var $this = $(this);
    if($this.is(":checked")){
      if($this.attr('type')==='radio'){
        $this.parents(".panel-body").children().removeClass("selected-vote");
      }
      $this.parents('.vote-item').addClass("selected-vote"); 
    }
    else{
      $this.parents('.vote-item').removeClass("selected-vote");
      
    }
  });
  
  //투표항목 체크시마다 모든 투표에 다 완료했는지 확인후 투표버튼의 활성화 여부를 결정
  $('.opt-vote-item').change(function(e) {
    var isEnableBtn, $voteBtn;

    isEnableBtn = validator.validateVotingForm();
    $voteBtn = $('.btn-vote'); 

    if (isEnableBtn) {
      $voteBtn.prop('disabled', false);
    } else {
      $voteBtn.prop('disabled', true);
    }
  });

  $('.btnNext').click(function(){
    $('.nav-tabs > .active').next('li').find('a').trigger('click');
  });

  $('.btnPrevious').click(function(){
    $('.nav-tabs > .active').prev('li').find('a').trigger('click');
  });

  function voteUserChoice(votreeId, voteMode){

    var validateResult, requestBodyData, votes, votesLength,
    voteInfo,userChoices, $this, responseObj;

    validateResult = validator.validateVotingForm();

    if(validateResult){
      requestBodyData = {};
      votes = [];
      votesLength = $('.vote-tab').size();

      requestBodyData.votreeId = votreeId;
      requestBodyData.userId = $('#userId').val();
      for (var i = 1 ; i <= votesLength; i++){

        voteInfo = {};
        userChoices = [];

        voteInfo.voteId = $('#vote'+i).attr("voteId");

        $("#opt-vote-"+i+":checked").each(function() {
          $this = $(this);
          userChoices.push($this.val());
        });

        voteInfo.userChoices = userChoices;
        votes.push(voteInfo);
      }
      requestBodyData.votes = votes;
      requestBodyData.userId = $('#userId').val();
      $.ajax({
        url : "/api/v0.1/votrees/"+votreeId+"/"+voteMode,
        type: "POST",
        data: JSON.stringify(requestBodyData),
        contentType: "application/json; charset=utf-8",
        dataType   : "json",
        complete    : function(data){
          responseObj = jQuery.parseJSON(data.responseText);
          if(responseObj.header.isSuccessful){
            window.location = "/result/" + votreeId;
          }
          else{
            alert(responseObj.header.resultMessage);
            window.location = "/detail/" + votreeId;
          }
        }
      });
    }
    else{
      alert("모든 항목에 투표를 하셔야 합니다.");
    }
  }

})();
