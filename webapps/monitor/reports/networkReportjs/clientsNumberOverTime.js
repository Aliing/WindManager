/**
 * for chart "Clients Number Over Time"
 */
var chart_clients_number_over_time;

chart_clients_number_over_time = new AhReportChart.Chart({
	 container: "clients_number_over_time",
	 width: 600,
	 chartHeight: 300,
	 reportId: 1022,
	 legend: 'right middle',
	 xAxisType: 'datetime',
	 events: {
		tipFormatter: function(obj, ahChart) {
			var xName = ahChart.getDateStringWithTimeZone(obj.x);
			return xName +'<br/>'+
				obj.series.name +': <b>'+ obj.y+'</b>';
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

$('#clients_number_over_time').show();
chart_clients_number_over_time.render();