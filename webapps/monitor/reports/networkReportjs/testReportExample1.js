/**
 * for chart "Bandwidth Usage by AP"
 */
var chart_chartContainerId_test;

chart_chartContainerId_test = new AhReportChart.Chart({
	 container: "chartContainerId_test",
	 width: 600,
	 chartHeight: 300,
	 reportId: -1,
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

$('#chartContainerId_test').show();
chart_chartContainerId_test.render();