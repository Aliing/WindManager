/**
 * for chart "Client Distribution by SSID"
 */
var chart_client_distribution_by_ssid_ghz24;

chart_client_distribution_by_ssid_ghz24 = new AhReportChart.Chart({
	 container: "client_distribution_by_ssid_ghz24",
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

chart_client_distribution_by_ssid_ghz24.additionalArgs.add("subType", "GHz24");

$('#client_distribution_by_ssid_ghz24').show();
chart_client_distribution_by_ssid_ghz24.render();