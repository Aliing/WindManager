/**
 * for chart "Top N Clients by Bandwidth Usage"
 */

(function() {
	var el = document.getElementById("top_n_clients_bandwidth");
	if (!el || el === null) return;
	
	var chart_top_n_clients_bandwidth;
	
	var objTips = {};
	var unitName = "B";
	chart_top_n_clients_bandwidth = new AhReportChart.Chart({
		 container: "top_n_clients_bandwidth",
		 width: 600,
		 chartHeight: 300,
		 reportId: 1025,
		 legend: 'right middle',
		 events: {
			tipFormatter: function(obj, ahChart) {
				return obj.x + "<br>" + obj.series.name +': <b>'+ obj.y+'</b> ' + unitName;
			},
			doCustomDataOperation: function(chart, data) {
				if (data && data.userMessage) {
					var dataUser = data.userMessage;
					if (dataUser.u) {
						unitName = dataUser.u;
					}
				}
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
	
	$('#top_n_clients_bandwidth').show();
	chart_top_n_clients_bandwidth.render();
})();