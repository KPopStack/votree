'use strict';
(function() {
    var width, container,
        $inputSummit = $('#input-summit'),
        $inputMonth = $('#input-month'),
        $inputYear = $('#input-year');

    $inputSummit.click(function() {
        var year = $inputYear.val();
        var month = $inputMonth.val();
        drawLineChart(year, month);
    });

    $inputMonth.keyup(function(e) {
        if (e.keyCode === 13) { // ENTER key
            $inputSummit.click();
        }
    });

    function drawLineChart(year, month) {
        width = parseInt($('.container').css('width'),10);
        var optionsAvg = {
            chart: {
                width: width,
                height: 400,
                title: '주요 페이지 평균 응답 속도'
            },
            yAxis: {
                title: '응답시간'
            },
            xAxis: {
                title: 'Date'
            },
            series: {
                hasDot: true
            },
            tooltip: {
                suffix: 'ms'
            }
        };

        $('[data-toggle="tooltip"]').tooltip();

        $.ajax({
            url: '/api/v0.1/check/average?year=' + year + '&month=' + month,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                container = document.getElementById('averageGraph');
                $(container).empty();
                tui.chart.lineChart(container, data, optionsAvg);
            }
        });

    var optionsSd = {
        chart: {
            width: width,
            height: 400,
            title: '주요 페이지 표준편차'
        },
        yAxis: {
            title: '표준편차'
        },
        xAxis: {
            title: 'Date'
        },
        series: {
            hasDot: true
        },
        tooltip: {
            suffix: 'ms'
        }
    };
        $.ajax({
            url: '/api/v0.1/check/sd?year=' + year + '&month=' + month,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                container = document.getElementById('sdGraph');
                $(container).empty();
                tui.chart.lineChart(container, data, optionsSd);
            }
        });
    }
})();
