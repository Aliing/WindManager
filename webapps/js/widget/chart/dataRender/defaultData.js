(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		$el = ARC.$el,
		isUndefined = ARC.isUndefined;
	
	var defaultDataRender = function(chart, options, data) {
		if (data.categories
				&& data.categories.data) {
			chart.hcChart.xAxis[0].setCategories(data.categories.data);
		}
		if (data.series
				&& data.series.length > 0) {
			var preSeriesState = {};
			if (chart.oriSeriesData != null) {
				for (var key in chart.oriSeriesData) {
					preSeriesState[chart.oriSeriesData[key].id] = true;
				}
			}
			chart.oriSeriesData = {};
			var stackGroup = pick(options.stackGroup, 'no');
			var seriesLength = data.series.length;
			for (var i = 0; i < seriesLength; i++) {
				var seriesData = data.series[i];
				var seriesName = seriesData.name;
				var seriesPoints = seriesData.data;
				if (options
						&& options.xAxisType === 'datetime') {
					var seriesPointsTmp = [];
					for (var j = 0; j < seriesPoints.length; j++) {
						seriesPointsTmp.push([chart.getDateWithTimeZone(seriesPoints[j][0]).getTime(), seriesPoints[j][1]]);
					}
					seriesPoints = seriesPointsTmp;
				}
				chart.oriSeriesData[seriesName] = {
						name: seriesName,
						id: seriesName+"_"+seriesData.id,
						data: seriesPoints,
						type: seriesData.type,
						color: seriesData.color
				};
				if (seriesData.stack
						&& stackGroup !== 'no') {
					chart.oriSeriesData[seriesName].stack = seriesData.stack;
					chart.oriSeriesData[seriesName].stacking = stackGroup;
				}
				if (seriesLength === 1 && 
						(isUndefined(seriesData.data)
								|| seriesData.data === null
								|| seriesData.data.length < 1)) {
					chart.tips.addError('There is no data for this report.', 'all', -1);
					$el(chart.AREA_SECTIONS.CHART_CONTENT).hide();
					$el(chart.AREA_SECTIONS.CHART_CTL).hide();
					self.preNoData = true;
				} else if (self.preNoData === true) {
					chart.tips.addError('', 'all', 10);
					$el(chart.AREA_SECTIONS.CHART_CONTENT).show();
					$el(chart.AREA_SECTIONS.CHART_CTL).show();
					self.preNoData = false;
				}
				if (chart.hcChart.get(chart.oriSeriesData[seriesName].id)) {
					if (preSeriesState[chart.oriSeriesData[seriesName].id]) {
						preSeriesState[chart.oriSeriesData[seriesName].id] = false;
					}
					chart.hcChart.get(chart.oriSeriesData[seriesName].id).setData(chart.oriSeriesData[seriesName].data, false);
				} else {
					chart.hcChart.addSeries(chart.oriSeriesData[seriesName], false);
				}
			}
			for (var key in preSeriesState) {
				if (preSeriesState[key] && preSeriesState[key] == true) {
					var serTmp = chart.hcChart.get(key);
					if (serTmp) {
						serTmp.remove(false);
					}
				}
			}
		}
	};
	
	var defaultLegendRender = function(chart, options, data) {
		chart.legendData = {};
		if (data.series
				&& data.series.length > 0) {
			var seriesLength = data.series.length;
			for (var i = 0; i < seriesLength; i++) {
				var seriesData = data.series[i];
				var seriesName = seriesData.name;
				chart.legendData[seriesName] = seriesData.summary;
			}
		}
	};
	
	Chart.prototype.dataRenderMethods['default'] = {
			dataFunc: defaultDataRender,
			legendFunc: defaultLegendRender
	};
})(jQuery);