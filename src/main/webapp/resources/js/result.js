'use strict';
(function() {
  var theme, width, options;
  theme = {
      chart: {
        background: 'none',
        fontFamily: 'Sans'
      },
      series: {
        colors: ['#E74C3C', '#8E44AD',
                 '#3498DB', '#1ABC9C',
                 '#F1C40F', '#D35400',
                 '#95A5A6', '#F5B7B1',
                 '#D2B4DE', '#D4EFDF',
                 '#D2B4DE', '#FDEBD0',
                 '#EBF5FB', '#FEF9E7',
                 '#E9F7EF', '#EBF5FB',
                 '#F9EBEA', '#F5EEF8']
      }
  };

  width = Number($('#vote-item-image').css('width'));
  options = {
      theme: 'votree-chart',
      chart: {
        width: width,
        height: 300,
        title: '<span style="font-weight: bold; font-size:22px;">투표 결과</span>'
      },
      legend: {
        hidden: true
      }
  };

  tui.chart.registerTheme('votree-chart', theme);
  $('[data-toggle="tooltip"]').tooltip();

  $('.result-item').click(function(e){
    var $this = $(this),
    votreeId = $('#votree-id-for-ajax').val(),
    voteId = e.target.id,
    fileStorageUrl = $('#votree-fileStorage-url').val(),
    fileName = $this.find('#vote-item-hidden-value').val(),
    hiddenCategoryValue = Number($this.find('#vote-item-hidden-category').val()),
    $graph = $('#graph'),
    $voteItemImage = $('#vote-item-image'),
    $voteItemVideo = $('#vote-item-video');
    $('.result-item').css("border", "1px solid #ccc");
    $this.css("border","2px solid #464");


    const IMAGE_ITEM = 2,
    VIDEO_ITEM = 3;

    $.ajax({
      url:"/api/v0.1/votes/"+ voteId +"/result?votree_id="+votreeId,
      type : "GET",
      dataType : "json", 
      success: function(data){
        var container = document.getElementById('graph');
        tui.chart.pieChart(container, data, options);
      }
    });
    $graph.empty();
    $voteItemImage.attr('src','').hide();
    $voteItemVideo.attr('src','').hide();
    $this = $(this);
    $graph.show();
    if(hiddenCategoryValue === IMAGE_ITEM) {
      $voteItemImage.css({
        "visibility" : "visible",
      });
      $voteItemImage.attr("src",fileStorageUrl+fileName).show();
    }
    if(hiddenCategoryValue === VIDEO_ITEM) {
      $voteItemVideo.attr("src","https://www.youtube.com/v/"+fileName).show();
    }
  });

  var $topChoiceTab = $('.votree-top-choice'),
  $topChoiceContent = $('.votree-top-choice-content'),
  $bestChoiceTab = $('.votree-best-choice'),
  $bestChoiceContent = $('.votree-best-choice-content'),
  $personalChoiceTab = $('.votree-personal-choice'),
  $personalChoiceContent = $('.votree-personal-choice-content'),
  $personalAdditionalInfoBox = $('.personal-additional-info-box'),
  $votreePersonalChoiceBox = $('.votree-personal-choice-box'),
  $bestAdditionalInfoBox = $('.best-additional-info-box');
  
  $personalChoiceContent.hide();
  $bestChoiceContent.hide();
  $personalAdditionalInfoBox.hide();
  $bestAdditionalInfoBox.hide();
  
  $topChoiceTab.click(function(e) {
    $personalChoiceContent.hide();
    $bestChoiceContent.hide();
    $topChoiceContent.show();
    $personalChoiceTab.removeClass('votree-personal-choice-active');
    $bestChoiceTab.removeClass('votree-best-choice-active');
    $topChoiceTab.addClass('votree-top-choice-active');
  });

  $personalChoiceTab.click(function(e) {
    $topChoiceContent.hide();
    $bestChoiceContent.hide();
    $personalChoiceContent.show();
    $topChoiceTab.removeClass('votree-top-choice-active');
    $bestChoiceTab.removeClass('votree-best-choice-active');
    $personalChoiceTab.addClass('votree-personal-choice-active');

  });

  $bestChoiceTab.click(function(e) {
    $topChoiceContent.hide();
    $personalChoiceContent.hide();
    $bestChoiceContent.show();
    $topChoiceTab.removeClass('votree-top-choice-active');
    $personalChoiceTab.removeClass('votree-personal-choice-active');
    $bestChoiceTab.addClass('votree-best-choice-active');
    
  }); 

  $votreePersonalChoiceBox.click(function(e) {
    var $this = $(this);
    $personalAdditionalInfoBox.hide();
    $votreePersonalChoiceBox.css("border", "1px solid #ccc");
    var selectedId = $this.attr('id');
    $this.css("border","2px solid #464");
    $('.'+selectedId).show();
  });
  
  var $bestChoiceButton = $('.best-choice-button');
  $bestChoiceButton.click(function(e) {
    var $this = $(this);
    $bestAdditionalInfoBox.hide();
    $bestChoiceButton.removeClass('votree-branch-active');
    $this.addClass('votree-branch-active');
    var selectedId = $this.attr('id');
    $('.'+selectedId).show();
  });
  
})();
