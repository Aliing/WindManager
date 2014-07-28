/**
 * for chart "Outbound Bandwidth Over Time"
 */
var chart_out_bandwidth_over_time;

chart_out_bandwidth_over_time = new AhReportChart.Chart({
	 container: "out_bandwidth_over_time",
	 width: 600,
	 chartHeight: 300,
	 reportId: 1003,
	 legend: 'right middle',
	 xAxisType: 'datetime',
	 events: {
		tipFormatter: function(obj, ahChart) {
			var xName = ahChart.getDateStringWithTimeZone(obj.x);
			return xName +'<br/>'+
				obj.series.name +': <b>'+ obj.y+'</b> Mbps';
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
			 min: 0
		 }
	 }
});

chart_out_bandwidth_over_time.additionalArgs.add("subType", "out");

$('#out_bandwidth_over_time').show();
chart_out_bandwidth_over_time.render();