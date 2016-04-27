'use strict';
(function() {
    var $userImage = $('#userImage');
    $userImage.on({
        mouseover: function() {
            $('#modifiedButton').css('display', 'inline');
        },
        mouseout: function() {
            $('#modifiedButton').css('display', 'none');
        }
    });

    $('#modifiedButton').mouseover(function() {
        $('#modifiedButton').css('display', 'inline');
    });

    $('#cancleModal').click(function() {
        $('#modifiedButton').css('display', 'none');
    });

    $('#imageModifiedButton').click(function() {
        window.open('https://ko.gravatar.com/emails', 'win',
        'width=600,height=600,toolbar=no,scrollbars=no,resizable=no,status=no');
    });

    $('[data-toggle="tooltip"]').tooltip();

    $('#modifiedSubmit').click(function(event) {
        event.preventDefault();
        var requestBody = {},
            userId = $('#userId').val();
        requestBody.name = $('#name').val();
        requestBody.email = $('#email').val();
        if (!validator.validateProfileEditFormName(requestBody.name)) {
            alert('이름이 너무 짧습니다.');
            return false;
        }
        if (!validator.validateProfileEditFormEmail(requestBody.email)) {
            alert('EMAIL형식이 맞지 않습니다.');
            return false;
        }
        $.ajax({
            url: '/api/v0.1/users/' + userId,
            type: 'PUT',
            data: JSON.stringify(requestBody),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            complete: function(data) {
                var responseObj = jQuery.parseJSON(data.responseText);
                if (responseObj.header.resultCode === 200) {
                    window.location.href = '/profiles/' + userId + '?modifiedName=' + responseObj.body;
                } else {
                    alert('프로필변경이 실패했습니다.' + responseObj.header.resultMessage);
                }
            }
        });
        return true;
    });
})();
