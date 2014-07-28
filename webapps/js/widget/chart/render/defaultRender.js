(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick;
	
	var defaultRender = function(chart, options, data, highChartOption) {
		var chart_final_width = pick(options.chartWidth, 600);
		var chart_final_height = pick(options.chartHeight, 0);
		if (options.events && options.events.initChartHWJudge && data.series) {
			var argTmp = {seriesCount: data.series.length};
			var resultTmp = options.events.initChartHWJudge(argTmp, self);
			if (resultTmp) {
				chart_final_height = resultTmp.height;
			}
		}
		
		$.extend(true, highChartOption, {
			chart: {
				height: chart_final_height
			}
		});
		
		return highChartOption;
	};
	
	Chart.prototype.chartRenderMethods['default'] = {func: defaultRender};
})(jQuery);