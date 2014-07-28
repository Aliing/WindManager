/**
 * for chart "Bandwidth Usage by SSID"
 */
var chart_bandwidth_usage_by_ssid;

chart_bandwidth_usage_by_ssid = new AhReportChart.Chart({
	 container: "bandwidth_usage_by_ssid",
	 width: 600,
	 chartHeight: 450,
	 reportId: 1004,
	 legend: 'center bottom',
	 events: {
		tipFormatter: function(obj, ahChart) {
			var value = obj.y;
			var label = 'MB';
			if (value >= 1024) {
				value = value/1024;
				label = 'GB';
			}
			if (value >= 1024) {
				value = value/1024;
				label = 'TB';
			}
			return obj.point.name + "<br>" 
					+ obj.series.name +': <b>' + value.toFixed(2) + " " + label + '</b>';
		}
	 },
	 expert: {
		 yAxis: {
			 min: 0,
			 title: ''
		 }
	 }
});

$('#bandwidth_usage_by_ssid').show();
chart_bandwidth_usage_by_ssid.render();