var validator = {};

validator.validateProfileEditFormName = function (name){
  var MINIMUM_LENGTH = 1;
  if($.trim(name).length < MINIMUM_LENGTH){
    return false;
  }
  return true;
}
validator.validateProfileEditFormEmail = function (email){
  var MINIMUM_LENGTH = 1;
  if($.trim(email).length < MINIMUM_LENGTH){
    return false;
  }
  var emailRegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  return emailRegExp.test($.trim(email))
}
validator.validateYoutubeUrl = function (inputUrl){
  var videoId;
  if((inputUrl.substring(0,17) === "https://youtu.be/" && inputUrl.length == 28)
      || (inputUrl.substring(0,30) === "https://www.youtube.com/watch?" && inputUrl.length == 43)){
    videoId = inputUrl.substring(inputUrl.length-11, inputUrl.length);
    return videoId;
  }
  return false;
}

validator.setValidateResult = function (validateResult, state, msg){
  validateResult.state = state;
  validateResult.msg = msg;
  return validateResult;
}


validator.validateVotreeRegistForm = function (){
  var validateResult, startDateTimeInput, dueDateTimeInput, startDateTime, dueDateTime, $this, 
      password, votreeName, $topics = $('.vote-topic'), $voteItems = $('.vote-item-input'), $votes = $('.vote-tab');
  
  validateResult = {};
  startDateTimeInput = $.trim($('#start-datetime').val());
  dueDateTimeInput = $.trim($('#due-datetime').val());
  votreeName = $.trim($('#votree-name').val());
  if($('#private-password').val()){
    password = $.trim($('#private-password').val());
  }
  startDateTime = new Date(startDateTimeInput);
  dueDateTime = new Date(dueDateTimeInput);
  
  validateResult.state = true;
  validateResult.msg = "성공";

  if(votreeName === ""){
    validateResult = validator.setValidateResult(validateResult,false,"투표 이름을 입력하세요");
    return validateResult;
  }
  if(votreeName.length > 40){
    validateResult = validator.setValidateResult(validateResult,false,"투표 이름이 너무 깁니다. (40자 이상)");
    return validateResult;
  }

  if(startDateTimeInput.length != 16){
    validateResult = validator.setValidateResult(validateResult,false,"시작 시간이 올바르지 않습니다");
    return validateResult;
  }
  if(dueDateTimeInput.length != 16){
    validateResult = validator.setValidateResult(validateResult,false,"마감 시간이 올바르지 않습니다");
    return validateResult;
  }
  if(startDateTime >= dueDateTime){
    validateResult = validator.setValidateResult(validateResult,false,"투표 시작시간이 마감시간보다 늦을 수 없습니다.");
    return validateResult;
  }

  if(password === ""){
    validateResult = validator.setValidateResult(validateResult,false,"비밀 투표 비밀번호를 입력하세요.");
    return validateResult;
  }
  $votes.each(function(index){
    var $this = $(this);
    if($this.children('.question-list').size() < 2){
      validateResult = validator.setValidateResult(validateResult,false,"투표 항목은 2개 이상이어야 합니다.");
      return validateResult;
    }
    if($("#vote-name-"+(index+1)).val().length > 40){
      validateResult = validator.setValidateResult(validateResult,false,"투표 주제가 너무 깁니다. (40자 이상)");
      return validateResult;
    }
  });
  

  $topics.each(function(index){
    var currentValue = $.trim($(this).val());
    if(currentValue === ""){
      validateResult = validator.setValidateResult(validateResult,false,"투표 주제 "+(index+1)+"을 입력하세요.");
      return validateResult;
    }
    if(currentValue.length > 100){
      validateResult = validator.setValidateResult(validateResult,false,"투표 주제 "+(index+1)+"의 길이가 너무 깁니다.(100자 이상)");
      return validateResult;
    }
  })

  $voteItems.each(function(index){
    var $this = $(this);
    var currentValue = $.trim($this.val());
    
    if($this.hasClass('input-youtube')){
      if(!validator.validateYoutubeUrl(currentValue)){
        validateResult = validator.setValidateResult(validateResult,false,"유튜브 URL이 올바르지 않습니다.");
        return validateResult; 
      }
    }
    
    if(currentValue === ""){
      validateResult = validator.setValidateResult(validateResult,false,"투표 항목을 입력하세요.");
      return validateResult;
    }
    if(currentValue.length > 100){
      validateResult = validator.setValidateResult(validateResult,false,"투표항목이 너무 깁니다.(100자 이상)");
      return validateResult;
    }
  });
  
  return validateResult;
}
  

validator.validateVotingForm = function (){
    var votesLength, isAllVoted;

    votesLength = $('.vote-tab').size();
    isAllVoted = true;

    for (var i = 1 ; i <= votesLength; i++) {
      isAllVoted = isAllVoted && !!($("#opt-vote-"+i+":checked").size());
    }
    return isAllVoted;
}