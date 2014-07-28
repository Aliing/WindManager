/**
 * for chart "Inbound Bandwidth Over Time"
 */
var chart_in_bandwidth_over_time;

chart_in_bandwidth_over_time = new AhReportChart.Chart({
	 container: "in_bandwidth_over_time",
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

chart_in_bandwidth_over_time.additionalArgs.add("subType", "in");

$('#in_bandwidth_over_time').show();
chart_in_bandwidth_over_time.render();