/**
 * for chart "Ssid client count"
 */
(function() {
	var chart_ssid_client_count;
	
	var chart_width = 600;
	var chart_height = 300;
	
	chart_ssid_client_count = new AhReportChart.Chart({
		 container: "ssid_client_count",
		 width: chart_width,
		 chartHeight: chart_height,
		 reportId: 1301,
		 legend: 'right middle',
		 xAxisType: 'datetime',
		 events: {
			tipFormatter: function(obj, ahChart) {
				var xName = ahChart.getDateStringWithTimeZone(obj.x);
				return xName +'<br/>'+
					obj.series.name +': <b>'+ obj.y+'</b>';
			},
			initChartHWJudge: function(arg, ahChart) {
				if (arg.seriesCount) {
					if (arg.seriesCount > 5) {
						var chart_height_tmp = chart_height + 50*(arg.seriesCount - 5);
						return {
							height: chart_height_tmp
						}
					}
				}
			}
		 },
		 expert: {
			 chart: {
				zoomType: 'x' 
			 },
			 plotOptions: {
				 series: {
					marker: {
						enabled: false
					} 
				 }
			 },
			 yAxis: {
				 allowDecimals: false,
				 min: 0
			 }
		 }
	});
	
	$('#ssid_client_count').show();
	chart_ssid_client_count.render();
})();