'use strict';
(function() {
    var $voteJoin = $('#vote-join');
    $voteJoin.click(function() {
        var votreeId = $voteJoin.val();
        window.location = '/voting/' + votreeId + '/vote';
    });

    var $revoteJoin = $('#revote-join');
    $revoteJoin.click(function() {
        var votreeId = $revoteJoin.val();
        window.location = '/voting/' + votreeId + '/revote';
    });

    var $votreeResult = $(".votree-result");
    $votreeResult.click(function() {
        var votreeId = $(this).attr('id');
        window.location = '/result/' + votreeId;
    });
})();
