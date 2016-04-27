/*
 * Created by SeongWon Kong 01.23.2016
 */
'use strict';
(function() {
    const PRIVATE_VOTREE_TYPE = 2;
    var $votreeBoxes = $('.votree-box');
    $votreeBoxes.on({
        click: function() {
            var votreeId, $this = $(this),
                votreeType = Number($this.attr('value'));
            if (votreeType !== PRIVATE_VOTREE_TYPE) {
                votreeId = $this.attr('id');
                window.location = '/detail/' + votreeId;
            }
        },
        mouseenter: function() {
            var $this = $(this),
                votreeType = Number($this.attr('value'));
            if (votreeType === PRIVATE_VOTREE_TYPE) {
                $this.children().hide();
                $this.find('#span-title').show();
                $this.find('.votree-hidden-form').css('display', 'inline');
            }
        },
        mouseleave: function() {
            var $this = $(this),
                votreeType = Number($this.attr('value'));
            if (votreeType === PRIVATE_VOTREE_TYPE) {
                $this.removeClass('showPasswordInput');
                $this.find('.votree-hidden-form').hide();
                $this.find('.fa').show();
                $this.find('.span-my-votree').show();
            }
        }
    });

    var $hiddenForm = $('.votree-hidden-form');
    $hiddenForm.submit(function() {
        var $this = $(this),
            userId = $('#userId').val(),
            input = $this.find('.votree-pwd').val();
        if (userId.length === 0) {
            alert('로그인을 해주세요.');
            return false;
        } else if (input.length === 0) {
            alert('비밀번호를 입력해주세요.');
            return false;
        } else if (input.length > 0) {
            return true;
        }
        return true;
    });

    var $userBox = $(".user-box");
    $userBox.click(function() {
        var $this = $(this),
            votreeId = $this.attr('id');
        window.location = '/profiles/' + votreeId;
    });
})();
