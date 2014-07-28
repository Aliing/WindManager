/**
 * for chart "Bandwidth Usage by AP"
 */
var chart_device_error;

chart_device_error = new AhReportChart.Chart({
	 container: "device_error",
	 width: 600,
	 chartHeight: 300,
	 reportId: 1501,
	 legend: 'right middle',
	 stackGroup: 'normal',
	 events: {
		tipFormatter: function(obj, ahChart) {
			return obj.x + "<br>" + obj.series.name +': <b>'+ obj.y+'</b>';
		}
	 },
	 expert: {
		 chart: {
			inverted: true,
			type: 'column'
		 },
		 yAxis: {
			 min: 0
		 }
	 }
});

$('#device_error').show();
chart_device_error.render();