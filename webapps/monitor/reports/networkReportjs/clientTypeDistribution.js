/**
 * for chart "Client Type Distribution"
 */
var chart_client_type_distribution;

chart_client_type_distribution = new AhReportChart.Chart({
	 container: "client_type_distribution",
	 width: 600,
	 chartHeight: 450,
	 reportId: 1024,
	 legend: 'center bottom',
	 events: {
		tipFormatter: function(obj, ahChart) {
			return obj.point.name + "<br>" 
					+ obj.series.name +': <b>' + obj.y + '</b>';
		}
	 },
	 expert: {
		 yAxis: {
			 min: 0,
			 title: ''
		 }
	 }
});

$('#client_type_distribution').show();
chart_client_type_distribution.render();