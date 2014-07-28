/**
 * for chart "Client Distribution by SSID"
 */
var chart_client_distribution_by_ssid;

chart_client_distribution_by_ssid = new AhReportChart.Chart({
	 container: "client_distribution_by_ssid",
	 width: 600,
	 chartHeight: 450,
	 reportId: 1023,
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

$('#client_distribution_by_ssid').show();
chart_client_distribution_by_ssid.render();