(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		pFloat = ARC.pFloat,
		isUndefined = ARC.isUndefined,
		replaceSpecialSymbol = ARC.replaceSpecialSymbol,
		isString = ARC.isString;
	
	var _MONITOR_TYPE_DISPLAY_NAME = {
		device: "device",
		client: "client",
		user: "user",
		app: "application",
		appclient: "client"
	};
	
	var _doShowLegendPieNotLegend = function(point) {
		var self = this;
		var name = point.name;
		var data = point.y;
		if (self.currentDashboard) {
			var curConfig = self.currentDashboard.getReportConfig(self.reportId);
			if (self.currentDashboard.daHelper
					&& !self.currentDashboard.daHelper.isMetricDataIntType(curConfig.axis, curConfig.m[0][0].metric)) {
				data = data.toFixed(2);
				if (data == 0) {
					data = 0.01;
				}
			}
		}
		if (point.series.options.myUnit) {
			data += point.series.options.myUnit;
		}
		var legendShown = data + ' ' + self.splitLegendName(name);
		if (self.legendData[name]) {
			for (var i = 0; i < self.legendData[name].length; i++) {
				legendShown += '<br><span style="font-size:11px; color:#BDBAAB; line-height: 1.1em;">' + self.legendData[name][i] + '</span>';
			}
		}
		return legendShown;
	};
	var _getLegendShowFunc = function(chart, chartType, curConfig) {
		if (chartType == "pie") {
			if ("REPORT_ADDITIONAL" in win
					&& chart.currentDashboard
					&& chart.currentDashboard.daHelper) {
				var pieOption = chart.currentDashboard.daHelper.getWidgetAdditionalProperties(REPORT_ADDITIONAL, [curConfig.axis, "pie"], {
					blnForKey: true,
					keyValue: curConfig.xKey
				});
				// default to show percentage
				if (pieOption && !pieOption.legendPercentage) {
					return _doShowLegendPieNotLegend;
				}
			}
			return chart.legendTypes.pie;
		}
		return chart.legendTypes.common;
	};
	
	var dashRender = function(chart, options, data, highChartOption) {
		var chart_final_width = pick(options.chartWidth, 600);
		var chart_final_height = pick(options.chartHeight, 0);
		if (options.events && options.events.initChartHWJudge && data.series) {
			var argTmp = {seriesCount: data.series.length};
			var resultTmp = options.events.initChartHWJudge(argTmp, self);
			if (resultTmp) {
				chart_final_height = resultTmp.height;
			}
		}
		
		var curChartConfig;
		if (chart.currentDashboard) {
			curChartConfig = chart.currentDashboard.getReportConfig(chart.reportId);
		}
		if (!curChartConfig) {
			curChartConfig = {};
		}
		
		var chartOptionTmp = {
				height: chart_final_height
		};
		var getValueAndMarkFromCategory = function(value) {
			return chart.currentDashboard.daHelper.getValueAndMarkFromCategory(value);
		};
		var _escapeHtmlTag = function(txt) {
			return txt;
		};
		if (!chart.isDatetimeType) {
			_escapeHtmlTag = function(txt) {
				return ARC.escapeHtmlTag(txt);
			};
		}
		var addLinkToLabelFormatter = function(text, value) {
			if (isUndefined(value)) {
				value = this.value;
			}
			if (isUndefined(value)) {
				value = "";
			}
			if (!isString(value)) {
				return isUndefined(text)?text:value;
			}
			var values = getValueAndMarkFromCategory(value);
			if (!text) {
				if (values) {
					text = values[0];
				} else {
					text = this.value;
				}
			}
			if (values && values.length > 1 && (chart.currentDashboard.opAllowed.monitor || chart.currentDashboard.opAllowed.deviceLink)) {
				var titleText = "Click it to see ";
				if (_MONITOR_TYPE_DISPLAY_NAME[curChartConfig.xmt]) {
					titleText += _MONITOR_TYPE_DISPLAY_NAME[curChartConfig.xmt] + " ";
				}
				titleText += "details of " + values[0] + "(" + values[1] + ").";
				return '<span class="helloxaxis" onclick="javascript: AhReportChart.Chart.xAxisLabelClickEvent(event, \''
							+ chart.container +'\', \''
							+ replaceSpecialSymbol(values[0]) +'\', \''
							+ replaceSpecialSymbol(values[1]) +'\');" title="' + titleText + '">' + _escapeHtmlTag(text) + '</span>';
			} else {
				return _escapeHtmlTag(text);
			}
		};
		var yAxisTmp = {
			labels: {
				formatter: function() {
					return this.value;
				}
			}
		};
		var legendTmp = {};
		var isAllMetricInt = function(curDa, curConfig) {
			if (curConfig && curConfig.m) {
				for (var key in curConfig.m) {
					var metrics = curConfig.m[key];
					for (var i = 0; i < metrics.length; i++) {
						if (!curDa.daHelper.isMetricDataIntType(curConfig.axis, metrics[i].metric)) {
							return false;
						}
					}
				}
				return true;
			}
		};
		if (chart.currentDashboard) {
			var curDa = chart.currentDashboard;
			var curConfig = curDa.getReportConfig(chart.reportId);
			if (curConfig) {
				var axisObj = curDa.daHelper.getAxis(curConfig.axis);
				chartOptionTmp.inverted = curDa.daHelper.isWidgetChartInverted(axisObj, chart);
				var typeTmp = curDa.daHelper.getWidgetChartType(curConfig.axis, chart);
				var xAxisTmp = {
						labels: {
							style: {
								'classcc': 'hellodashchart',
								'width': 220
							}
						},
						tickmarkPlacement: 'on',
						useHTML: true
					};
				if (typeTmp === 'column' && !chart.isDatetimeType) {
					$.extend(true, xAxisTmp, {
						labels: {
							formatter: function() {
								return addLinkToLabelFormatter.apply(this);
							}
						}
					});
				}
				if (isAllMetricInt(curDa, curConfig)) {
					yAxisTmp.allowDecimals = false;
				}
				if (typeTmp === 'pie') {
					xAxisTmp.lineWidth = 0;
					yAxisTmp.lineWidth = 0;
					yAxisTmp.title = {
						text: ''
					};
					$.extend(true, xAxisTmp, {
						title: {
							style: {
								'classcc': 'da-chart-xaxis'
							}
						}
					});
					var legendFunc = _getLegendShowFunc(chart, typeTmp, curConfig);
					$.extend(true, legendTmp, {
						labelFormatter: function () {
							if (this.name) {
								return ARC.escapeHtmlTag(legendFunc.call(chart, this));
							}
							return '';
						}
					});
				} else {
					yAxisTmp.title = {
						//text: curConfig.xname
						text: '',
						style: {
							'classcc': 'da-chart-yaxis'
						}
					};
					var legendFunc = _getLegendShowFunc(chart, typeTmp, curConfig);
					$.extend(true, legendTmp, {
						labelFormatter: function () {
							if (this.name) {
								return ARC.escapeHtmlTag(legendFunc.call(chart, this));
							}
							return '';
						}
					});
				}
				
				if (typeTmp !== 'pie') {
					var valRange = curDa.daHelper.getValueRange(chart, curConfig);
					if (valRange) {
						if (valRange.length > 0) {
							yAxisTmp.min = pFloat(valRange[0]);
						}
						if (valRange.length > 1) {
							yAxisTmp.max = pFloat(valRange[1]);
						}
					}
				}
				
				if (curDa.daHelper.isChartMonitorable(curConfig) && chart.currentDashboard.opAllowed.monitor) {
					$.extend(true, xAxisTmp, {
						labels: {
							style: {
								color: '#0093D1',
								//fontStyle: 'italic',
								cursor: 'pointer',
								width: 220
							},
							useHTML: true
						}
					});
				}
				
				if (curDa.daHelper.isChartDeviceLinkable(curConfig) && chart.currentDashboard.opAllowed.deviceLink) {
					$.extend(true, xAxisTmp, {
						labels: {
							style: {
								color: '#FFCC00',
								//fontStyle: 'italic',
								cursor: 'pointer',
								width: 220
							},
							useHTML: true
						}
					});
				}
				
				if (typeTmp === 'column' || typeTmp === 'bar') {
					if (chartOptionTmp.inverted) {
						$.extend(true, yAxisTmp, {
							tickLength: 100,
							tickPixelInterval: 150
						});
						if (!chart.isDatetimeType) {
							if (options.displayCtl && options.displayCtl.fullXaxisName) {
								$.extend(true, xAxisTmp, {
									labels: {
										 formatter: function() {
											 var val = this.value;
											 if (isString(val)) {
												 var vals = getValueAndMarkFromCategory(val);
												 if (vals && vals.length > 1) {
													 val = vals[0];
												 }
											 }
											 return addLinkToLabelFormatter.apply(this, [val]);
										 }
									 }
								});
							} else {
								$.extend(true, xAxisTmp, {
									labels: {
										 formatter: function() {
											 var val = this.value;
											 if (isString(val)) {
												 var vals = getValueAndMarkFromCategory(val);
												 if (vals && vals.length > 1) {
													 val = vals[0];
												 }
											 }
											 if (val.length > 16) {
												 val = val.substring(0, 13) + '...';
											 }
											 return addLinkToLabelFormatter.apply(this, [val]);
										 }
									 }
								});
							}
						}
					}
				}
			}
		}
		
		$.extend(true, highChartOption, {
			chart: chartOptionTmp,
			legend: legendTmp,
			plotOptions: {
				series: {
					cursor: 'pointer',
					point: {
						events: {
							click: function() {
								if (chart.currentDashboard.opAllowed.drilldown && chart.seriesPointClick) {
									var blnContinueClick = true;
									if (this.series.options.type === "pie") {
										var curTime = new Date().getTime();
										this.my_click_option = this.my_click_option || {time: curTime};
										var diffTime = curTime - this.my_click_option.time;
										if (diffTime <= 0 || diffTime > 250) {
											blnContinueClick = false;
										}
										this.my_click_option.time = curTime;
									}
									if (!blnContinueClick) {
										//return true;
									}
									chart.seriesPointClick.apply(this, [chart, this]);
								}
							}
						}
					},
					marker: {
						enabled: false
					} 
				}
			},
			xAxis: xAxisTmp,
			yAxis: yAxisTmp
		});
		
		return highChartOption;
	};
	
	Chart.prototype.chartRenderMethods['dash'] = {func: dashRender};
})(jQuery);