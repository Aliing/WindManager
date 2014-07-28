(function($, _){
	var win = window,
		ahDashSpace = win.AhDashboard,
		ahDash = ahDashSpace.Dashboard,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		$el = ARC.$el,
		isObject = ARC.isObject,
		isString = ARC.isString,
		isUndefined = ARC.isUndefined;
	
	var DA_PERIOD_TYPE = {
		HOUR: 1,
		DAY: 2,
		WEEK: 3,
		COSTOM: 4,
		CALENDAR_DAY: 5,
		CALENDAR_WEEK: 6,
		LAST8HOUR: 7
	};
	
	Chart.prototype.isBlnCtlsInBottom = function() {
		if (this.isInEditStatus()) {
			return false;
		} else {
			return false;
		}
	};
	Chart.prototype.exportChartLabelFormatter = function(value) {
		var val = value;
		if (isString(val)) {
			var vals = this.currentDashboard.daHelper.getValueAndMarkFromCategory(val);
			if (vals && vals.length > 1) {
				val = vals[0];
			}
		}
		return val;
	};
	
	var iterateChartCtlButtonGroups = function(chart, func) {
		chart.ctlButtonGrps.iterate(function(ctlContainer, groupby){
			if (ctlContainer == chart.AREA_SECTIONS.MAIN_TITLE_CTL
					&& groupby == 'show') {
				func.call(this);	
			}
		});
	};
	ahDash.prototype.__callItWhenRenderDashboard = function() {
		var self = this;
		//this function will be called when a chart is rendered with data
		if (!Chart.prototype.dataRenderMethods['dash']) {
			Chart.prototype.dataRenderMethods['dash'] = {};
		}
		Chart.prototype.dataRenderMethods['dash']['afterChartDataIsRenderedFunc'] = function() {
			if (!this) {
				return;
			}
			if (this.resetChartContainerWidth) {
				this.resetChartContainerWidth();
			}
			var reqAllowTids = self.getReqAllowTids();
			delete reqAllowTids[this.container];
			if (_.size(reqAllowTids) > 0) {
				return;
			}
			//must be last one
			self.resizeDa();
		};
		
		var old_onstatuschanged = Chart.prototype.__onStatusChanged;
		Chart.prototype.__onStatusChanged = function(oldStatus, status) {
			if (old_onstatuschanged) {
				old_onstatuschanged.apply(this, [oldStatus, status]);
			}
			if (status === this.STATUS_MODE.EDIT) {
				this.mainContentTip.show(this.my_addi_data_config_info || "N/A");
				iterateChartCtlButtonGroups(this, function() {this.$container.hide();});
			} else {
				iterateChartCtlButtonGroups(this, function() {this.$container.show();});
			}
		}
	};
	
	var getHourStrForCustomTime = function(val) {
		if (!val) {
			return "";
		}
		var valTmp = val%12;
		return (valTmp<10?'0'+valTmp:valTmp) + ':00' + (val>12?'PM':'AM');
	};
	ahDash.prototype.__getRefreshPeriodStr = function(chart) {
		if (!chart) {
			return;
		}
		var configs = this.getCurReportWidgetConfig(chart);
		if (configs) {
			if (configs.checked) {
				if (configs.hasOwnProperty('timeType')) {
					if (configs.timeType == 1) {
						return "Last Hour";
					} else if (configs.timeType == 2) {
						return "Last Day";
					} else if (configs.timeType == 3) {
						return "Last Week";
					} else if (configs.timeType == 4) {
						return configs.startDate + " " + getHourStrForCustomTime(configs.startHour)
								+ " - " + configs.endDate + " " + getHourStrForCustomTime(configs.endHour);
					}
				}
			} else {
				return "According to dashboard setting";
			}
		}
	};
	
	ahDash.prototype.__getCurrentEditWidgetOptions = function(chartContainer) {
		var result = {};
		daColumnConfigs = this.getDaColumnConfigs();
		if (chartContainer) {
			var chartTmp = daColumnConfigs.getChart(chartContainer);
			if (chartTmp) {
				result.title = chartTmp.getTitleText();
				result.container = chartTmp.container;
			}
		} else {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart.isChartShown() === false) {
					result.title = chart.getTitleText();
					result.container = chart.container;
				}
			});
		}
		return result;
	};
	
	ahDash.prototype.__refreshDaConfigsAfterSuccSaving = function(data) {
		if (!data) {
			return;
		}
		var self = this;
		var daColumnConfigs = self.getDaColumnConfigs();
		daColumnConfigs.iterateCharts(function(chart){
			if (!chart) {
				return;
			}
			var configsTmp = data[chart.reportId];
			if (configsTmp) {
				self.addMarkHelperToChart(chart.container, {
						'oWidgetId': configsTmp.id,
						'xkey': configsTmp.xk
					});
				self.initChartValues(chart.container, {
					'title': chart.getTitleText(),
					'widgetConfId': configsTmp.id,
					'reportId': chart.reportId
				});
				
				if (configsTmp.config) {
					daColumnConfigs.setOriReportWidgetConfig(chart, configsTmp.config, true);
				}
			}
		});
		if (data.additionalInfo) {
			if (data.additionalInfo.appInterval) {
				self.customInfo["appInterval"] = data.additionalInfo.appInterval;
			}
		}
		self.setChanged(false);
	};
	
	ahDash.prototype.__prepareCertainChartConfigs = function(chart, doConfigType) {
		var self = this;
		var daColumnConfigs = self.getDaColumnConfigs();
		chart.ctlButtons.addCustomAttribute('__Close', 'closeCallback', function(divName){
			var chartTmp = daColumnConfigs.getChart(divName);
			var dcId;
			if (chartTmp) {
				dcId = chartTmp.reportId;
			}
			if (self.getMarkHelperFromChart(divName, "oWidgetId")) {
				daColumnConfigs.moveChartToRecyle(divName);
			} else {
				daColumnConfigs.removeChart(divName);
			}
			
			daColumnConfigs.refresh();
			if (self.afterCloseFunc) {
				self.afterCloseFunc(divName, dcId);
			}
		});
		chart.ctlButtons.addCustomAttribute('__Config', 'configFunc', function(divName){
			var chartTmp = daColumnConfigs.getChart(divName);
			var markTmp = daColumnConfigs.getMarkHelperFromChart(divName);
			var oriClone = false;
			if (markTmp && markTmp.cloned === 1) {
				oriClone = true;
			}
			if (chartTmp && self.configFunc) {
				self.configFunc(divName, {
					'cloned': oriClone
				}, chartTmp.reportId);
			}
		});
		chart.ctlButtons.addCustomAttribute('__TopologySelect', 'onMyStatusChange', function(chart, oldStatus, status){
			var checkedTmp = self.getCurReportWidgetConfig(chart, 'checked');
			if (isUndefined(checkedTmp)) {
				checkedTmp = false;
			}
			if (status === chart.STATUS_MODE.VIEW && checkedTmp) {
				$('#'+this.MAIN_MENU_ID).show();
			} else {
				$('#'+this.MAIN_MENU_ID).hide();
			}
		});
		chart.ctlButtons.addCustomAttribute('__TopologySelect', 'configFunc', function(divName){
			var chartTmp = daColumnConfigs.getChart(divName);
			if (!chartTmp) {
				return;
			}
			var chartHelperArgs = daColumnConfigs.getMarkHelperFromChart(divName);
			if (!chartHelperArgs) {
				chartHelperArgs = {};
			}
			self.setCurCtlBarSelectContainer(divName);
			if (self.topologyConfigFunc) {
				self.topologyConfigFunc(chartTmp, divName, chartTmp.reportId, chartHelperArgs.oWidgetId, self.getConfigType('topo'));
			}
		});
		chart.ctlButtons.addCustomAttribute('__TimePeroid', 'onMyStatusChange', function(chart, oldStatus, status){
			var checkedTmp = self.getCurReportWidgetConfig(chart, 'checked');
			if (isUndefined(checkedTmp)) {
				checkedTmp = false;
			}
			if (status === chart.STATUS_MODE.VIEW && checkedTmp) {
				$('#'+this.MAIN_MENU_ID).show();
			} else {
				$('#'+this.MAIN_MENU_ID).hide();
			}
		});
		chart.ctlButtons.addCustomAttribute('__TimePeroid', 'configFunc', function(divName){
			var chartTmp = daColumnConfigs.getChart(divName);
			if (!chartTmp) {
				return;
			}
			var chartHelperArgs = daColumnConfigs.getMarkHelperFromChart(divName);
			if (!chartHelperArgs) {
				chartHelperArgs = {};
			}
			self.setCurCtlBarSelectContainer(divName);
			if (self.timeConfigFunc) {
				self.timeConfigFunc(chartTmp, divName, chartTmp.reportId, chartHelperArgs.oWidgetId, self.getConfigType('period'));
			}
		});
		chart.ctlButtons.addCustomAttribute('__ChartCheck', 'configFunc', function(divName){
			var chartTmp = daColumnConfigs.getChart(divName);
			if (!chartTmp) {
				return;
			}
			var chartHelperArgs = daColumnConfigs.getMarkHelperFromChart(divName);
			if (!chartHelperArgs) {
				chartHelperArgs = {};
			}
			self.setCurCtlBarSelectContainer(divName);
			var ckBtn = chart.ctlButtons.get('__ChartCheck');
			if (self.checkConfigFunc) {
				self.checkConfigFunc(chartTmp, divName, chartTmp.reportId, chartHelperArgs.oWidgetId, ckBtn.isChecked());
			}
			if (ckBtn.isChecked()) {
				chartTmp.ctlButtons.get('__TopologySelect').doShow(true);
				chartTmp.ctlButtons.get('__TimePeroid').doShow(true);
			} else {
				chartTmp.ctlButtons.get('__TopologySelect').doShow(false);
				chartTmp.ctlButtons.get('__TimePeroid').doShow(false);
			}
		});
		chart.ctlButtons.addCustomAttribute('__ChartCheck', 'onMyStatusChange', function(chart, oldStatus, status){
			if (status === chart.STATUS_MODE.VIEW && (chart.__editPermissionCheck == null || chart.__editPermissionCheck())) {
				this.setCanCheck();
				if (this.onEnterEditMode) {
					this.onEnterEditMode.apply(this, [chart]);
				}
				$('#'+this.MAIN_MENU_ID).show();
			} else {
				$('#'+this.MAIN_MENU_ID).hide();
			}
		});
		chart.ctlButtons.addCustomAttribute('__ChartCheck', 'onEnterEditMode', function(chart){
			var divName = chart;
			if (isObject(chart)) {
				divName = chart.container;
			}
			var chartTmp = daColumnConfigs.getChart(divName);
			if (!chartTmp) {
				if (isObject(chart)) {
					chartTmp = chart;
				}
			}
			if (!chartTmp) {
				return;
			}
			this.enableCheck();
			var checkedTmp = self.getCurReportWidgetConfig(chart, 'checked');
			if (isUndefined(checkedTmp)) {
				checkedTmp = false;
			}
			this.setCheckStatus(checkedTmp, true);
			self.setTmpReportConfigNoChg(divName, {
				'checked': checkedTmp
			}, true);
			this.showNote(checkedTmp);
		});
		/*chart.ctlButtons.get('__ChartCheck').customizeNoteBehavior(function($chkBtn, $chkNote, $warningNote) {
			var self = this;
			$chkBtn.hover(function(e){
				if (!self.isChecked()) {
					return;
				}
				$chkNote.hide();
				$warningNote.show();
			}, function(e){
				$warningNote.hide();
				$chkNote.show();
			})
		});*/
		ARC.ctlCons["__Email"].prototype.emailMark = "da";
		ARC.ctlCons["__Email"].prototype.checkBefore = function(){
			if (!checkWhetherChartHasData(this)) {
				return false;
			}
			return true;
		};
		ARC.ctlCons["__Print"].prototype.checkBefore = function(){
			if (!checkWhetherChartHasData(this)) {
				return false;
			}
			return true;
		};
		ARC.ctlCons["__Export"].prototype.checkBefore = function(){
			if (!checkWhetherChartHasData(this)) {
				return false;
			}
			return true;
		};
	};
	
	var checkWhetherChartHasData = function(chart) {
		if (chart) {
			var el = document.getElementById(chart.AREA_SECTIONS.NODATA_CONTAINER);
			if (el && el.style.display === "none") {
				return true;
			}
		}
		return false;
	};
	
	ahDash.prototype.__daSettingBackChartRefreshCallback = function(chart, mainTitleTmp, options) {
		var self = this;
		if (!mainTitleTmp) {
			mainTitleTmp = "";
		}
		$el(chart.AREA_SECTIONS.MAIN_TITLE).text(mainTitleTmp);
		$el(chart.AREA_SECTIONS.MAIN_TITLE).attr({"title": mainTitleTmp});
		self.initChartValues(chart, {
			'title': mainTitleTmp,
			'widgetConfId': options.oWidgetId,
			'reportId': options.reportId
		});
	};
	
	ahDash.prototype.__refreshAChartCallback = function(chart) {
		var self = this;
		var curWidgetOptions = self.getCurWidgetOptions();
		if (curWidgetOptions) {
			if (curWidgetOptions.title) {
				$el(chart.AREA_SECTIONS.MAIN_TITLE).text(curWidgetOptions.title);
				$el(chart.AREA_SECTIONS.MAIN_TITLE).attr({"title": curWidgetOptions.title});
				if (self.getCertainChartInitValue.getTitle(chart) != curWidgetOptions.title) {
					self.setChanged(true, chart);
				}
			}
		}
		
		self.changeChartInterval(chart);
	};
	
	ahDash.prototype.__refreshRequestData = function(chart) {
		var self = this;
		var daColumnConfigs = self.getDaColumnConfigs();
		chart.additionalArgs.add(false, "daId", self.boardId);
		chart.additionalArgs.add(false, "daType", "da");
		chart.additionalArgs.add(false, "grpTimestamp", self._grp_request_timestamp);
		var chartHelperArgs = daColumnConfigs.getMarkHelperFromChart(chart.container);
		if (chartHelperArgs && chartHelperArgs.oWidgetId) {
			chart.additionalArgs.add(false, "widgetId", chartHelperArgs.oWidgetId);
		}
		if (self.getConfigType("topo") === "tmp") {
			var tmpTopoConfig = self.getCurReportWidgetConfig(chart);
			if (tmpTopoConfig) {
				var tmpArgs = {};
				if (tmpTopoConfig.hasOwnProperty('timeType')) {
					tmpArgs.timeType = tmpTopoConfig.timeType;
				}
				if (tmpTopoConfig.hasOwnProperty('enableTimeLocal')) {
					tmpArgs.enableTimeLocal = tmpTopoConfig.enableTimeLocal;
				}
				if (tmpTopoConfig.hasOwnProperty('startDate')) {
					tmpArgs.startTime = tmpTopoConfig.startDate;
				}
				if (tmpTopoConfig.hasOwnProperty('startHour')) {
					tmpArgs.startHour = tmpTopoConfig.startHour;
				}
				if (tmpTopoConfig.hasOwnProperty('endDate')) {
					tmpArgs.endTime = tmpTopoConfig.endDate;
				}
				if (tmpTopoConfig.hasOwnProperty('endHour')) {
					tmpArgs.endHour = tmpTopoConfig.endHour;
				}
				if (tmpTopoConfig.hasOwnProperty('obType')) {
					tmpArgs.obType = tmpTopoConfig.obType;
				}
				if (tmpTopoConfig.hasOwnProperty('obId')) {
					tmpArgs.obId = tmpTopoConfig.obId;
				}
				if (tmpTopoConfig.hasOwnProperty('lid')) {
					tmpArgs.locationId = tmpTopoConfig.lid;
				}
				if (tmpTopoConfig.hasOwnProperty('fobType')) {
					tmpArgs.fobType = tmpTopoConfig.obType;
				}
				if (tmpTopoConfig.hasOwnProperty('fobId')) {
					tmpArgs.fobId = tmpTopoConfig.fobId;
				}
				if (tmpTopoConfig.hasOwnProperty('checked')) {
					tmpArgs.widgetChecked = tmpTopoConfig.checked;
				}
				chart.additionalArgs.add(false, tmpArgs);
			}
		}
	};
	
	var DEFAULT_CHART_INTERVAL = 600000;
	ahDash.prototype.__changeChartInterval = function(chart, option) {
		if (!chart) {
			return;
		}
		option = option || {};
		var interval = -1;
		var self = this;
		var blnAppWidget = false;
		
		if (self.timeType != DA_PERIOD_TYPE.HOUR) {
			chart.clearRequestInterval();
			chart.callNextData = false;
			return;
		}
		
		if (self.timeType == DA_PERIOD_TYPE.HOUR && self.customInfo.hasOwnProperty("appInterval")) {
			var curConfig = self.getReportConfig(chart.reportId);
			if (curConfig && curConfig.blnApp) {
				if (self.customInfo.appInterval < DEFAULT_CHART_INTERVAL) {
					interval = self.customInfo.appInterval;
				}
				blnAppWidget = true;
			}
		}
		if (interval < 0) {
			interval = DEFAULT_CHART_INTERVAL;
		}
		chart.interval = interval;
		chart.callNextData = true;
		if (self.timeType == DA_PERIOD_TYPE.HOUR && blnAppWidget) {
			chart.intervalStrategy.setStrategy({type: "iterate", steadyInterval: interval, intervals: [60000, 60000, 60000, 60000, 60000, 60000]}, option.blnForceIntervalInit);
		}
		chart.resetSendRequestTimer();
	};
	
	var getSeriesBaseName = function(series) {
		if (series && series.options && series.options.myDataNode 
				&& series.options.myDataNode.seriesBaseName
				&& series.options.myDataNode.seriesBaseName != series.name) {
			return "<br/>" + series.options.myDataNode.seriesBaseName;
		}
		return "";
	};
	ahDash.prototype.__prepareChartOptionsBeforeRender = function(reportTmp, oriChart, container) {
		if (!reportTmp) {
			return;
		}
		var self = this;
		var daColumnConfigs = self.getDaColumnConfigs();
		var xAxisTmp;
		var chartTypeTmp;
		if (reportTmp.config) {
			xAxisTmp = reportTmp.config.axis;
			chartTypeTmp = reportTmp.config.chartType;
		}
		if (!chartTypeTmp) {
			chartTypeTmp = self.daHelper.getDefaultChartType(xAxisTmp);
		}
		
		reportTmp.url = 'reportData.action';
		reportTmp.operation = "data";
		reportTmp.legend = "right middle";
		reportTmp.repeat = false;
		reportTmp.interval = 60000;
		reportTmp.periodType = ARC.periodTypeEnum.LASTONEHOUR;
		reportTmp.reqDescInfoAtFirst = false;
		reportTmp.objPrefix = "rpdata";
		reportTmp.printType = 2;
		reportTmp.xAxisType = 'linear';
		if (self.daHelper.isOvertime(xAxisTmp, oriChart)) {
			reportTmp.xAxisType = 'datetime';
		};
		
		if (chartTypeTmp === 'table') {
			reportTmp.renderType = 'table';
		} else if (chartTypeTmp === 'list') {
			reportTmp.renderType = 'list';
		}
		
		if (!reportTmp.events) {
			reportTmp.events = {};
		}
		
		if (reportTmp.xAxisType == 'datetime') {
			reportTmp.events.tipFormatter = function(obj, ahChart) {
					var xName = ahChart.getDateStringWithTimeZone(obj.x);
					var blnInt = self.daHelper.isMetricDataIntType(xAxisTmp, obj.series.options.keyN);
					var tipTmp = ARC.escapeHtmlTag(xName) +'<br/>'+
						ARC.escapeHtmlTag(obj.series.name + getSeriesBaseName(obj.series)) +': <b>'+ self.formatValue(obj.y, blnInt)+'</b> ';
					tipTmp += obj.series.options.myfUnit || "";
					var curConfig = ahChart.currentDashboard.getReportConfig(ahChart.reportId),
						curMetric = obj.series.options.keyN;
					var actionTip;
					if (ahChart.currentDashboard.daHelper.isChartMetricDrillDownable(curConfig, curMetric)) {
						actionTip = Aerohive.lang.chart.tip.monitor.viewDrillDown;
					} else if (ahChart.currentDashboard.daHelper.isChartMetricValueLinkable(curConfig, curMetric)) {
						actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
					} else if (ahChart.currentDashboard.daHelper.isChartMonitorable(curConfig, curMetric)) {
						if (ahChart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
							actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
						} else {
							actionTip = Aerohive.lang.chart.tip.monitor.timelimited;
						}
					}
					if (actionTip) {
						tipTmp += "<br/><span>" + actionTip + "</span>";
					}
					return tipTmp;
				}
		} else if (chartTypeTmp === 'pie') {
			reportTmp.events.tipFormatter = function(obj, ahChart) {
				var blnInt = self.daHelper.isMetricDataIntType(xAxisTmp, obj.series.options.keyN);
				var tipTmp = ARC.escapeHtmlTag(obj.point.name) + "<br>" 
						+ ARC.escapeHtmlTag(obj.series.name + getSeriesBaseName(obj.series)) +': <b>' + self.formatValue(obj.y, blnInt) + '</b>';
				tipTmp += obj.series.options.myfUnit || "";
				var curConfig = ahChart.currentDashboard.getReportConfig(ahChart.reportId),
					curMetric = obj.series.options.keyN;
				var actionTip;
				if (ahChart.currentDashboard.daHelper.isChartMetricDrillDownable(curConfig, curMetric)) {
					actionTip = Aerohive.lang.chart.tip.monitor.viewDrillDown;
				} else if (ahChart.currentDashboard.daHelper.isChartMetricValueLinkable(curConfig, curMetric)) {
					actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
				} else if (ahChart.currentDashboard.daHelper.isChartMonitorable(curConfig, curMetric)) {
					if (ahChart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
						actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
					} else {
						actionTip = Aerohive.lang.chart.tip.monitor.timelimited;
					}
				}
				if (actionTip) {
					tipTmp += "<br/><span>" + actionTip + "</span>";
				}
				return tipTmp;
			}
		} else {
			reportTmp.events.tipFormatter = function(obj, ahChart) {
				var xName = obj.x;
				var blnInt = self.daHelper.isMetricDataIntType(xAxisTmp, obj.series.options.keyN);
				var vals = self.daHelper.getValueAndMarkFromCategory(xName);
				if (vals && vals.length > 1) {
					xName = vals[0];
				}
				var tipTmp = '<b>'+ ARC.escapeHtmlTag(xName) +'</b><br/>'+
					ARC.escapeHtmlTag(obj.series.name + getSeriesBaseName(obj.series)) +': <b>'+ self.formatValue(obj.y, blnInt) + '</b>';
				tipTmp += obj.series.options.myfUnit || "";
				var curConfig = ahChart.currentDashboard.getReportConfig(ahChart.reportId),
					curMetric = obj.series.options.keyN;
				var actionTip;
				if (ahChart.currentDashboard.daHelper.isChartMetricDrillDownable(curConfig, curMetric)) {
					actionTip = Aerohive.lang.chart.tip.monitor.viewDrillDown;
				} else if (ahChart.currentDashboard.daHelper.isChartMetricValueLinkable(curConfig, curMetric)) {
					actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
				} else if (ahChart.currentDashboard.daHelper.isChartMonitorable(curConfig, curMetric)) {
					if (ahChart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
						actionTip = Aerohive.lang.chart.tip.monitor.viewDetail;
					} else {
						actionTip = Aerohive.lang.chart.tip.monitor.timelimited;
					}
				}
				if (actionTip) {
					tipTmp += "<br/><span>" + actionTip + "</span>";
				}
				return tipTmp;
			}
		}
		
		if (reportTmp.xk) {
			self.addMarkHelperToChart(container, {'xkey': reportTmp.xk});
			delete reportTmp.xk;
		}
		if (reportTmp.config) {
			daColumnConfigs.setReportConfig(reportTmp.reportId, reportTmp.config);
			delete reportTmp.config;
		}
		
		var oriWChecked = reportTmp.checked;
		delete reportTmp.checked;
		
		return {
			oriWChecked: oriWChecked
		};
	};
	
	ahDash.prototype.__prepareChartOptionsAfterRender = function(chartTmp, reportTmp, beforeResult) {
		if (!chartTmp) {
			return;
		}
		if (!beforeResult) {
			beforeResult = {};
		}
		if (reportTmp.oWidgetId) {
			chartTmp.ctlButtons.addAfterRenderedCallback('__ChartCheck', function(ctlBtn, chartArg) {
				if (ctlBtn) {
					ctlBtn.setCheckStatus(beforeResult.oriWChecked, true);
				}
			});
		}
	};
	
	ahDash.prototype.__restorChartFromRecyleNoDelete = function(chartTmp) {
		var self = this;
		if(!self.getMarkHelperFromChart(chartTmp.container, "oWidgetId")) {
			self.removeChart(chartTmp.container);
			chartTmp.destroy(true);
		} else {
			if (chartTmp.isInChangeMode()) {
				var titleTmp = self.daInstance.getCertainChartInitValue.getTitle(chartTmp);
				$el(chartTmp.AREA_SECTIONS.MAIN_TITLE).text(titleTmp);
				$el(chartTmp.AREA_SECTIONS.MAIN_TITLE).attr({"title": titleTmp});
				self.daInstance.setChanged(false, chartTmp);
			}
		}
	};
	
	ahDash.prototype.__clearRecyleItemsNoDelete = function(chartTmp) {
		var self = this;
		var ckBtn = chartTmp.ctlButtons.get('__ChartCheck');
		if (ckBtn) {
			ckBtn.setCheckStatus(self.getOriReportWidgetConfig(chartTmp, "checked"), true);
		}
	};
	
	ahDash.prototype.__refreshConfigCheckEver = function() {
		var self = this;
		self.iterateCharts(function(chart) {
			if (chart && chart.isBlnCtlsInBottom()) {
				$('#'+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM).show();
			} else {
				$('#'+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM).hide();
			}
		});
	};
	
	ahDash.prototype.__prepareChartConfigData = function(result, chartsTmp) {
		if (!chartsTmp) {
			return;
		}
		var self = this;
		var columnCharts = self.getColumnCharts();
		_.each(chartsTmp, function(obj){
			var cName = obj.container;
			if (!cName) return;
			var chartTmp = columnCharts[cName];
			var blnOvertime = false;
			var oriClone = 0;
			if (chartTmp && !chartTmp.delMark && !chartTmp.noChartMark && chartTmp.chart) {
				var oriWId = -1;
				var markTmp = self.getMarkHelperFromChart(cName);
				if (markTmp) {
					if (markTmp.cloned === 1) {
						oriClone = 1;
					}
					if (markTmp.oWidgetId) {
						oriWId = markTmp.oWidgetId;
					}
				}
				var curConfig = self.getReportConfig(chartTmp.reportId);
				var sample = 60;
				if (curConfig.desc) {
					sample = curConfig.desc.sample || sample;
				}
				result.charts.push({
					'id': chartTmp.reportId,
					'title': chartTmp.chart.getTitleText(),
					'overTime': blnOvertime,
					'cloned': oriClone,
					'wId': oriWId,
					'axis': curConfig.axis,
					'sample': sample,
					'wConfig': self.getCurReportWidgetConfig(chartTmp.chart),
					'lastData': chartTmp.chart?chartTmp.chart.getLastData():null
				});
			}
		});
	};
})(jQuery, _);