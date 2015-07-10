$(document).ready(function() {

$('#chart_div').highcharts({
	chart: {
		zoomType: 'x'
	},
	title: {
		text: 'Analysis Throughput'
	},
	subtitle: {
		text: document.ontouchstart === undefined ?
				'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
	},
	xAxis: {
		type: 'datetime',
		tickInterval: 10000
	},
	yAxis: {
		title: {
			text: '#Tweets Analysed/10secs'
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

$('#tpm_chart_div').highcharts({
	chart: {
		zoomType: 'x'
	},
	title: {
		text: 'Tweet Streaming Rate'
	},
	subtitle: {
		text: document.ontouchstart === undefined ?
				'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
	},
	xAxis: {
		type: 'datetime',
		tickInterval: 60000 //1 min
	},
	yAxis: {
		title: {
			text: '#Tweets/min'
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



chart = $('#chart_div').highcharts();
tpm_chart = $('#tpm_chart_div').highcharts();

});