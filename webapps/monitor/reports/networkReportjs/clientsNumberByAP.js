/**
 * for chart "Clients Number by AP"
 */
var chart_clients_number_by_ap;

chart_clients_number_by_ap = new AhReportChart.Chart({
	 container: "clients_number_by_ap",
	 width: 600,
	 chartHeight: 300,
	 reportId: 1021,
	 legend: 'right middle',
	 stackGroup: 'normal',
	 events: {
		tipFormatter: function(obj, ahChart) {
			return obj.x + "<br>" + obj.series.name +': <b>'+ obj.y+'</b>';
		}
	 },
	 expert: {
		 chart: {
			inverted: true
		 },
		 plotOptions: {
			line: {
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

$('#clients_number_by_ap').show();
chart_clients_number_by_ap.render();