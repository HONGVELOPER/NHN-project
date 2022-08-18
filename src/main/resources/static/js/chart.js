function input(){
    const sDate = document.querySelector("#sDate").value
    const eDate = document.querySelector("#eDate").value
        $.ajax({
            url: 'admin/dailyChartData?' + $.param({"startDate": sDate}) + '&' + $.param({"endDate": eDate}),
            success: function (result) {
                var date = JSON.parse(result).date;
                var quantity = JSON.parse(result).quantity;
                var totalAmount = JSON.parse(result).totalAmount;
                drawLineChart(date, quantity, totalAmount)

            }
        })

        function drawLineChart(date, quantity, totalAmount) {
            Highcharts.chart('container', {
                chart: {
                    zoomType: 'xy'
                },
                title: {
                    text: '매출 통계 현황'
                },
                subtitle: {
                    text: '일별 매출 그래프'
                },
                xAxis: {
                    categories: date,
                    crosshair: true
                },
                yAxis: [{
                    labels: {
                        format: '{value}개',
                        style: {
                            color: Highcharts.getOptions().colors[0]
                        }
                    },
                    title: {
                        text: '판매 수량',
                        style: {
                            color: Highcharts.getOptions().colors[0]
                        }
                    }
                }, {
                    title: {
                        text: '판매 금액',
                        style: {
                            color: Highcharts.getOptions().colors[1]
                        }
                    },
                    labels: {
                        format: '{value}원',
                        style: {
                            color: Highcharts.getOptions().colors[1]
                        }
                    },
                    opposite: true
                }],
                tooltip: {
                    shared: true
                },
                legend: {
                    layout: 'vertical',
                    align: 'left',
                    x: 120,
                    verticalAlign: 'top',
                    y: 100,
                    floating: true,
                    backgroundColor:
                        Highcharts.defaultOptions.legend.backgroundColor ||
                        'rgba(255,255,255,0.25)'
                },
                series: [{
                    name: '당일 판매 수량',
                    type: 'column',
                    data: quantity,
                    tooltip: {
                        valueSuffix: ' 개'
                    }

                }, {
                    name: '당일 판매 금액',
                    type: 'spline',
                    yAxis: 1,
                    data: totalAmount,
                    tooltip: {
                        valueSuffix: '원'
                    }
                }]
            });
        }
}