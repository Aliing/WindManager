/**
 * for chart "Bandwidth Usage by AP"
 */
var chart_device_bandwidth_usage;

chart_device_bandwidth_usage = new AhReportChart.Chart({
	 container: "chart_device_bandwidth",
	 width: 600,
	 chartHeight: 300,
	 reportId: 1002,
	 legend: 'right middle',
	 stackGroup: 'normal',
	 events: {
		tipFormatter: function(obj, ahChart) {
			return obj.x + "<br>" + obj.series.name +': <b>'+ obj.y+'</b> Mbps';
		}
	 },
	 expert: {
		 chart: {
			inverted: true
		 },
		 yAxis: {
			 min: 0
		 }
	 }
});

$('#chart_device_bandwidth').show();
chart_device_bandwidth_usage.render();