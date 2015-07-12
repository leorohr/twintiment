$(document).ready(function() {

$('#chart_div').highcharts({
	chart: {
		zoomType: 'x'
	},
	title: {
		text: 'Sentiment'
	},
// 	subtitle: {
// 		text: document.ontouchstart === undefined ?
// 				'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
// 	},
	xAxis: {
		type: 'datetime',
 		tickInterval: 1000
	},
	yAxis: {
		title: {
			text: 'Sentiment Value'
		}
	},
	legend: {
		layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle',
        borderWidth: 0
	},
	plotOptions: {
			series: {
				marker: {
					enabled: false
				}
			},
			lineWidth: 1,
			states: {
				hover: {
					lineWidth: 1
				}
			},
			threshold: null
	},
	series: [{
		type: 'line',
		name: 'Sentiment'
	}, {
		type: 'line',
		name: 'Avg. Sentiment'
	}]
});

$('#tpm_chart_div').highcharts({
	chart: {
		zoomType: 'x'
	},
	title: {
		text: 'Tweet Streaming Rate'
	},
// 	subtitle: {
// 		text: document.ontouchstart === undefined ?
// 				'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
// 	},
	xAxis: {
		type: 'datetime',
 		tickInterval: 5000, //5 sec
	},
	yAxis: {
		title: {
			text: '#Tweets/5sec'
		}
	},
	legend: {
		enabled: false
	},
	plotOptions: {
			series: {
				marker: {
					enabled: false
				}
			},
			lineWidth: 1,
			states: {
				hover: {
					lineWidth: 1
				}
			},
			threshold: null
	},
	series: [{
		type: 'line',
		name: 'Tweets'
	}]
});



sentiment_chart = $('#chart_div').highcharts();
tpm_chart = $('#tpm_chart_div').highcharts();

});