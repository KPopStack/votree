/*
 * Created by SeongWon Kong 01.28.2016
 */
 'use strict';
(function() {
    const ENTER_KEY = 13;
    var $searchButton = $('#search-button'),
        $searchInput = $('#search-input');
    $searchInput.keyup(function(e) {
        if (e.keyCode === ENTER_KEY) {
            $searchButton.click();
        }
    });

    var $searchButton = $("#search-button");
    $searchButton.click(function(){
        var keyword = $.trim($searchInput.val());
        if (keyword !== '') {
            window.location = '/search/?query=' + keyword;
        } else {
            alert('검색어를 입력해주세요!');
        }
    });
})();
