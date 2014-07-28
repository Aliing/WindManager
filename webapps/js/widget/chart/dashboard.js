(function($, _){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		$el = ARC.$el,
		isObject = ARC.isObject,
		pFloat = ARC.pFloat,
		consoleLog = ARC.consoleLog,
		isUndefined = ARC.isUndefined;
	
	win.AhDashboard = {};
	
	daRnd.today=new Date();
	daRnd.seed=daRnd.today.getTime();
	function daRnd() {
		daRnd.seed = (daRnd.seed*9301+49297) % 233280;
		return daRnd.seed/(233280.0);
	};
	function daRand(number) {
		return Math.ceil(daRnd()*number);
	};
	function daDefRand() {
		return daRand(10000);
	};
	
	var tzOffsetValue = new Date().getTimezoneOffset()*60000;
	var getDateStringWithTimeZone = function(time, diffTime) {
		if (diffTime) {
			time = time - diffTime;
		}
		var date = new Date(time);
		var year = date.getUTCFullYear();
		var month = date.getUTCMonth()+1;
		var dayOfMonth = date.getUTCDate();
		var hour = date.getUTCHours();
		var minute = date.getUTCMinutes();
		var append = 'AM';
		if (hour >= 12) {
			hour = hour -12;
			append = 'PM';
		}
		if (hour === 0) hour = 12;
		var fillNumber = function(value) {
			if (value < 10) {
				return '0'+value;
			}
			return value;
		};
		
		return fillNumber(year) + '-' + fillNumber(month) + '-' + fillNumber(dayOfMonth) + ' ' + fillNumber(hour) + ':' + fillNumber(minute) + append;
	};
	
	var DashboardGroup = function(options, callback) {
		var self = this;
		var dashboardObjs = {};
		var tabPrefix = pick(options.prefix, 'da_group_tab_of_');
		var toggleContainer = function(container, show) {
			if (show) {
				$el(container).show();
			} else {
				$el(container).hide();
			}
		};
		var hideDaContainer = function(container) {
			toggleContainer(container, false);
		};
		var showDaContainer = function(container) {
			toggleContainer(container, true);
		};
		var getUniqueGtabId = function() {
			var chart_container_idTmp = tabPrefix + daDefRand();
			while(document.getElementById(chart_container_idTmp)) {
				chart_container_idTmp = tabPrefix + daDefRand();
			}
			return chart_container_idTmp;
		};
		
		self.containers = {
			generate: function(option) {
				if (!option || !option.parent) {
					return;
				}
				var show = pick(option.show, true);
				var idTmp = getUniqueGtabId();
				var $divTmp = $("<div></div>")
								.attr({
									"id": idTmp
								})
								.addClass("daColumnsContainer");
				$el(option.parent).append($divTmp);
				if (show) {
					$divTmp.show();
				} else {
					$divTmp.hide();
				}
				return idTmp;
			},
			remove: function(tabContainer) {
				$el(tabContainer).remove();
			}
		};
		
		self.dashboards = {
			add: function(daId, dashboard, option) {
				if (!daId || !dashboard) {
					return;
				}
				dashboardObjs[daId] = dashboard;
			},
			addAndActive: function() {
				var argLen = arguments.length;
				if (argLen < 2) {
					return;
				}
				var daId = arguments[0];
				var dashboard = arguments[1];
				var option;
				var aCallback;
				if (argLen > 2) {
					if ($.isFunction(arguments[2])) {
						aCallback = arguments[2];
					} else {
						option = arguments[2];
					}
				}
				if (argLen > 3) {
					if ($.isFunction(arguments[3])) {
						aCallback = arguments[3];
					}
				}
				if (!aCallback) {
					aCallback = showDaContainer;
				}
				
				self.dashboards.add(daId, dashboard, option);
				self.dashboards.activeDa(daId, aCallback);
			},
			remove: function(daId, aCallback) {
				if (!daId) {
					return;
				}
				if (dashboardObjs[daId]) {
					dashboardObjs[daId].removeDashboardTimeIds(true);
					var containerTmp = dashboardObjs[daId].container;
					delete dashboardObjs[daId];
					self.containers.remove(containerTmp);
					if (aCallback) {
						aCallback(containerTmp);
					}
				}
			},
			get: function(daId) {
				if (!daId) {
					return dashboardObjs;
				}
				return dashboardObjs[daId];
			},
			activeDa: function(daId, aCallback) {
				if (!daId) {
					return;
				}
				if (!aCallback) {
					aCallback = showDaContainer;
				}
				_.each(_.keys(dashboardObjs), function(key) {
					if (key != daId) {
						if (dashboardObjs[key] && dashboardObjs[key].actived === true) {
							dashboardObjs[key].deactive(hideDaContainer);
						}
					}
				});
				if (dashboardObjs[daId] && dashboardObjs[daId].actived === false) {
					dashboardObjs[daId].active(aCallback);
				} else {
					dashboardObjs[daId].resizeDa();
				}
			},
			deactiveDa: function(daId, aCallback) {
				if (!daId) {
					return;
				}
				if (!aCallback) {
					aCallback = hideDaContainer;
				}
				if (dashboardObjs[daId] && dashboardObjs[daId].actived === true) {
					dashboardObjs[daId].deactive(aCallback);
				}
			},
			withoutUnusedDas: function() {
				var idTmps = [];
				_.each(_.keys(dashboardObjs), function(key) {
					var obj = dashboardObjs[key];
					if (obj && obj.container) {
						if ($el(obj.container).length < 1) {}
						idTmps.push(key);
					}
				});
				if (idTmps.length > 0) {
					_.each(idTmps, function(idTmp) {
						delete dashboardObjs[idTmp];
					});
				}
			},
			changeDaId: function(oriId, idNew) {
				if (!oriId || !idNew) {
					return;
				}
				if (dashboardObjs[oriId]) {
					dashboardObjs[idNew] = dashboardObjs[oriId];
					dashboardObjs[idNew].boardId = pFloat(idNew);
					dashboardObjs[oriId] = null;
				}
			},
			init: function() {
				dashboardObjs = {};
			},
			clear: function() {
				dashboardObjs = {};
			}
		};
	};
	
	var sortItemsAsLRTB = function(items, subArrayFunc, optionFunc) {
		var result = [];
		var orderedSubArrays = [];
		var maxItemsLen = 0;
		if (items && items.length > 0) {
			var subItems;
			for (var i = 0; i < items.length; i++) {
				subItems = {items: subArrayFunc(items[i])};
				if (optionFunc) {
					subItems.option = optionFunc(items[i]);
				}
				orderedSubArrays.push(subItems);
				if (subItems.items && subItems.items.length > maxItemsLen) {
					maxItemsLen = subItems.items.length; 
				}
			}
			if (maxItemsLen > 0) {
				for (var i = 0; i < maxItemsLen; i++) {
					for (var j = 0; j < orderedSubArrays.length; j++) {
						subItems = orderedSubArrays[j];
						if (subItems.items && subItems.items.length > i) {
							result.push({
								item: subItems.items[i],
								option: subItems.option
							});
						}
					}
				}
			}
		}
		
		return result;
	};
	
	var sortItemsAsTBLR = function(items, subArrayFunc, optionFunc) {
		var result = [];
		if (items && items.length > 0) {
			var subItems;
			for (var i = 0; i < items.length; i++) {
				subItems = {items: subArrayFunc(items[i])};
				if (optionFunc) {
					subItems.option = optionFunc(items[i]);
				}
				if (subItems.items && subItems.items.length > 0) {
					_.each(subItems.items, function(item) {
						result.push({
							item: item,
							option: subItems.option
						});
					});
				}
			}
		}
		
		return result;
	};
	
	var Dashboard = function(options, callback) {
		var self = this;
		self.container = options.container;
		self.$container = $el(self.container);
		self.boardId = pick(options.boardId, -1);
		self.blnNewDashboard = false;
		self.blnCloneDashboard = false;
		var helperMode = pick(options.helper, 'common');
		if (helperMode === 'new') {
			self.blnNewDashboard = true;
		}
		if (helperMode === 'clone') {
			self.blnCloneDashboard = true;
		}
		self.rendered = false;
		self.action = pick(options.action, 'dashboard.action');
		self.column_container_prefix = self.container + "_";
		self.chart_container_prefix = self.container + "_chart_";
		self.timeType = null;
		self.customInfo = {};
		var daColumnConfigs = new DashColumnConfigs(self);
		self.getDaColumnConfigs = function() {
			return daColumnConfigs;
		};
		var multiEdit = pick(options.multiEdit, false);
		var da_note_container = self.container + "_notes";
		var da_outter_note_container = self.container + "_outternotes";
		self.isExistedDa = function() {
			return !self.blnNewDashboard && !self.blnCloneDashboard;
		};
		self.notes = {
			addProcessing: function(text, ltime) {
				showTipMessages('Processing', text, -1);
			},
			addWarning: function(text, ltime) {
				showTipMessages('Warn', text, ltime);
			},
			addError: function(text, ltime) {
				showTipMessages('Error', text, ltime);
			},
			addInfo: function(text, ltime) {
				showTipMessages('Info', text, ltime);
			},
			clear: function() {
				if (note_deal_timeid) {
					clearTimeout(note_deal_timeid);
				}
				clearTipMessage();
			}
		};
		var showTipMessages = function(mtype, text, ltime) {
			if (note_deal_timeid) {
				clearTimeout(note_deal_timeid);
			}
			if (!ltime) {
				ltime = defNoteLeaveTime;
			}
			
			var clsType = 'rpSuccInfo';
			if (mtype === 'Warn') {
				clsType = 'rpWarnInfo';
			} else if (mtype === 'Error') {
				clsType = 'rpErrInfo';
			}
			
			var htmls = "";
			if ('Processing' === mtype) {
				htmls += '<div class="reportProcessingImage"></div>';
			}
			htmls += '<span class="'+clsType+'">'+text+'</span>';
			
			if (ltime > 0) {
				note_deal_timeid = setTimeout(function(){clearTipMessage();}, ltime);
			}
			
			$el(da_note_container).html(htmls);
			$el(da_outter_note_container).show();
		};
		var clearTipMessage = function() {
			$el(da_note_container).html('');
			$el(da_outter_note_container).hide();
		};
		var defNoteLeaveTime = 8000;
		var note_deal_timeid;

		var lastRefreshTime;
		self.setLastRefreshTime = function(time) {
			lastRefreshTime = time;
		};
		self.getLastRefreshTime = function(refreshTime, tzOffset) {
			refreshTime = refreshTime || lastRefreshTime;
			if (!refreshTime) {
				return "";
			}
			tzOffset = tzOffset || tzOffsetValue;
			return getDateStringWithTimeZone(refreshTime, tzOffset);
		};
		var lastChartReportPeriod = {
			common: '',
			special: ''
		};
		self.setLastChartReportPeriod = function(text, blnSpecialPeriod) {
			if (blnSpecialPeriod) {
				lastChartReportPeriod.special = text;
			} else {
				lastChartReportPeriod.common = text;
			}
		};
		self.getLastChartReportPeriod = function() {
			return lastChartReportPeriod.common || lastChartReportPeriod.special || ' ';
		};
		
		self.getRefreshPeriodStr = function(chart) {
			if (self.__getRefreshPeriodStr) {
				return self.__getRefreshPeriodStr.apply(self, [chart]);
			}
		};
		
		var notRequestDataMark = false;
		self.setNotRequestData = function(blnNoRequest) {
			notRequestDataMark = blnNoRequest;
		};
		
		self.opAllowed = {
			"monitor": true,
			"deviceLink": true,
			"drilldown": true
		};
		if (options.opAllowed) {
			if ("all" !== options.opAllowed) {
				if ("none" === options.opAllowed) {
					self.opAllowed.monitor = false;
					self.opAllowed.deviceLink = false;
					self.opAllowed.drilldown = false;
				} else {
					if (options.opAllowed.indexOf("+monitor") < 0 || options.opAllowed.indexOf("-monitor") >= 0) {
						self.opAllowed.monitor = false;
					}
					if (options.opAllowed.indexOf("+deviceLink") < 0 || options.opAllowed.indexOf("-deviceLink") >= 0) {
						self.opAllowed.deviceLink = false;
					}
					if (options.opAllowed.indexOf("+drilldown") < 0 || options.opAllowed.indexOf("-drilldown") >= 0) {
						self.opAllowed.drilldown = false;
					}
				}
			}
		}
		
		self.actived = true;
		self.active = function(aCallback) {
			self.actived = true;
			restoreFromBg();
			if (aCallback) {
				aCallback(self.container);
			}
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					chart.resizeChart();
					//chart.hackLabelClickable(chart);
				}
			});
			if (self.whenActiveFunc) {
				self.whenActiveFunc.apply(self);
			}
		};
		self.deactive = function(aCallback) {
			if (self.actived === false) {
				return;
			}
			self.actived = false;
			pauseToBg();
			if (aCallback) {
				aCallback(self.container);
			}
		};
		var blnMoved = false;
		self.moveTo = function(anotherContainer) {
			if (!anotherContainer) {
				return;
			}
			self.$container.appendTo(anotherContainer);
			blnMoved = true;
		};
		var pauseToBg = function() {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					chart.doPauseWithStatus();
				}
			});
		};
		var restoreFromBg = function() {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					chart.doStartWithStatus();
				}
			});
		};
		
		self.formatValue = function(value, blnIntValue) {
			if (blnIntValue) {
				return value;
			}
			if (typeof value !== 'undefined') {
				return value.toFixed(2);
			}
		};
		
		self.resizeDa = function() {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart && chart.isRenderedAsChart()) {
					chart.resizeChart();
					if (!isUndefined(chart.yAxisValueRangeMin) && !isUndefined(chart.yAxisValueRangeMax)) {
						chart.hcChart.yAxis[0].setExtremes(chart.yAxisValueRangeMin, chart.yAxisValueRangeMax, false);
					}
				}
			});
		};
		
		self.setChanged = function() {
			var len = arguments.length;
			var chged = true;
			if (len > 0) {
				chged = arguments[0];
			}
			if (len === 1) {
				daColumnConfigs.iterateCharts(function(chart){
					if (chart) {
						chart.setChanged(chged);
					}
				});
			} else if (len > 1) {
				var chartTmp = arguments[1];
				if (chartTmp) {
					chartTmp.setChanged(chged);
				}
			}
		};
		self.isInChangeMode = function() {
			var changed = false;
			daColumnConfigs.iterateCharts(function(chart){
				if (changed === false && chart) {
					changed = chart.isInChangeMode();
				}
			});
			return changed;
		};
		
		self.render = function() {
			renderDashboardSettings();
		}
		
		var initReportIdObjs;
		self.initReportIds = {
			init: function() {
				initReportIdObjs = daColumnConfigs.getAllReportIds();
			},
			isIn: function(reportId) {
				return $.inArray(reportId, initReportIdObjs) > -1;
			},
			reportIdCount: function() {
				var countIds=new Array();
				if (initReportIdObjs!=null) {
					for (var i = 0; i < initReportIdObjs.length; i++) {
						if (countIds.indexOf(initReportIdObjs[i])==-1) {
							countIds.push(initReportIdObjs[i]);
						}
					}
				}
				return countIds.length;
			},
			clear: function() {
				initReportIdObjs = null;
			}
		};
		self.initColumnChartsOrder = null;
		self._grp_request_timestamp = null;
		var resetGrpRequestTimestamp = function() {
			self._grp_request_timestamp = new Date().getTime();
		};
		resetGrpRequestTimestamp();
		
		var showLoadingTipForNewlyAddedChart = function(chart) {
			if (!chart.hasRenderedWithData) {
				chart.noDataContainer.show(" ");
				chart.showLoadingTip(chart.getCurrentShownContainer(), true);
			}
		};
		self.toggleMode = function(customMode, blnForceRefresh) {
			var mode = 'view';
			if (customMode) {
				mode = 'edit';
				self.clearRecyleItems();
				self.initReportIds.init();
				daColumnConfigs.setChartsCount(self.initReportIds.reportIdCount());
				self.initColumnChartsOrder = daColumnConfigs.getCurrentSortedColumnCharts();
			} else {
				self.removeDashboardTimeIds(false);
				self.initReportIds.clear();
				self.initColumnChartsOrder = null;
			}
			resetGrpRequestTimestamp();
			daColumnConfigs.iterateCharts(function(chart){
				if (chart && chart.statusChanged) {
					chart.statusChanged(mode);
					var hasRequested = false;
					if (chart.isInViewStatus() && chart._my_do_not_request_data) {
						delete chart._my_do_not_request_data;
						if (!chart.hasRequestedData() || blnForceRefresh) {
							showLoadingTipForNewlyAddedChart(chart);
							chart.requestDataFunc();
							hasRequested = true;
							chart.__isConfigChanged__ = false;
						} else {
							toggleEditTipInfoForChart(chart, false);
						}
					}
					if (!hasRequested && chart.isInViewStatus() && (chart.__isConfigChanged__ || blnForceRefresh)) {
						chart.__isConfigChanged__ = false;
						chart.requestDataFunc();
					}
				}
			});
			daColumnConfigs.statusChanged(mode);
			self.resizeDa();
		};
		self.clearRecyleItems = function() {
			daColumnConfigs.clearRecyleItems();
		};
		self.reset = function() {
			if (!blnMoved) {
				self.$container.empty();
			}
		};
		self.destroy = function() {
			
		};
		self.getColumnLayout = function() {
			return daColumnConfigs.getColumnLayout();
		};
		var curContainerInEdit;
		self.addNewChart = function(ids) {
			if (!ids) {
				return;
			}
			if (!self.canAddEditSection()) {
				return "Please wait for another adding operation to be finished.";
			}
			var addCount = 1;
			if ($.isArray(ids)) {
				addCount = ids.length;
			}
			if (!self.checkCanAddMoreChart(addCount)) {
				return "This perspective now contains the maximum number of widgets allowed";
			}
			var existedIds = [];
			if ($.isArray(ids)) {
				_.each(ids, function(id) {
					if (self.checkReportIdExist(id, self.getAllReportIds())) {
						existedIds.push(id);
						return;
					}
					addANewChart(id);
				});
			} else {
				if (self.checkReportIdExist(ids, self.getAllReportIds())) {
					existedIds.push(ids);
				} else {
					addANewChart(ids);
				}
			}
			if (existedIds.length > 0) {
				return existedIds;
			}
			return true;
		};
		var addANewChart = function(newId, containerArg) {
			if (!self.checkCanAddMoreChart()) {
				return;
			}
			if (!containerArg) {
				containerArg = $el(self.container + " div.daNewWidgetContent").parent().attr("id");
				$el(containerArg).unbind();
			}
			daColumnConfigs.setCurBlankWidgetEditMode(true);
			self.addNewEditChart(containerArg, $("div.daNewWidgetContent").parents("div.daColumn").attr("id"));
			$el(containerArg).empty();
			self.toggleEditArea(containerArg, false, true, newId);
		};
		self.checkCanAddMoreChart = function(count) {
			if (!self.canAddEditSection() || !self.checkEditPermission()) {
				return false;
			}
			if (!count) {
				count = 1;
			}
			if ((daColumnConfigs.getChartsCount() + count) <= self.__chartsCountLimitation) {
				return true;
			}
			return false;
		}
		self.removeExistChart = function(ids) {
			if (!ids) {
				return;
			}
			if ($.isArray(ids)) {
				_.each(ids, function(id) {
					removeAExistChart(id);
				});
			} else {
				removeAExistChart(ids);
			}
		};
		var removeAExistChart = function(id) {
			var chartTmp = daColumnConfigs.getChartByReportId(id);
			if (!chartTmp) {
				return;
			}
			daColumnConfigs.moveChartToRecyle(chartTmp.container, {reportId: id});
			daColumnConfigs.refresh();
		};
		self.toggleEditArea = function(chartContainer, blnShow) {
			if (blnShow === true && self.canAddEditSection() === false) return;
			var argLen = arguments.length;
			var argRefresh = false;
			if (argLen > 2) {
				argRefresh = arguments[2];
			}
			var newId;
			if (argLen > 3) {
				newId = arguments[3];
			}
			var additionalOption = {};
			if (argLen > 4) {
				newId = arguments[4];
			}
			if (blnShow === false && chartContainer === '-1') {
				chartContainer = curContainerInEdit;
			}
			var chart = daColumnConfigs.getChart(chartContainer);
			if (chart) {
				if (blnShow === false) {
					countOfEditingPanel > 0 ? countOfEditingPanel-- : null;
					chart.toggleContents('main');
					if (argRefresh) {
						if (newId) {
							self.refreshForCertainReportId(chart.reportId, newId);
						} else {
							self.refreshForCertainReportId(chart.reportId, chart.reportId);
						}
					}
				} else if (blnShow === true) {
					countOfEditingPanel++;
					curContainerInEdit = chart.container;
					chart.toggleContents('helper');
					return chart.AREA_SECTIONS.HELPER_CONTENT;
				}
			} else {
				chart = daColumnConfigs.getMarkHolderChart(chartContainer);
				if (chart) {
					if (blnShow === false) {
						countOfEditingPanel > 0 ? countOfEditingPanel-- : null;
						self.removeNewEditChart(chart.container, daColumnConfigs.getColumnContainer(chart.container), false);
						if (argRefresh && newId) {
							self.refreshForCertainReportId(-1, newId, chartContainer);
						} else {
							$el(chart.container).remove();
							daColumnConfigs.refresh();
						}
					} else {
						countOfEditingPanel++;
						curContainerInEdit = chart.container;
					}
					return chart.container;
				}
			}
		};
		var countOfEditingPanel = 0;
		self.canAddEditSection = function() {
			if (countOfEditingPanel > 0) {
				return false;
			}
			return true;
		};
		
		self.cloneTo = function(descOptionsArg, callback) {
			if (!descOptionsArg || !descOptionsArg.container) {
				return;
			}
			if (!lastData || !lastData.daSettings) {
				return;
			}
			var descOptions = $.extend(true, {}, options, {
				container: descOptionsArg.container,
				boardId: -1
			});
			var dashboardResult = new AhDashboard.Dashboard(descOptions, callback);
			dashboardResult.renderDashboardColumns(lastData.daSettings);
			dashboardResult.setLastData(lastData);
			var columnsTmp = daColumnConfigs.getSortedColumnNames();
			if (!columnsTmp || columnsTmp.length < 1) {
				return;
			}
			var colIdx = 0;
			var configsMap = {};
			var daResultConfig = dashboardResult.getDaColumnConfigs();
			_.each(columnsTmp, function(columnNameTmp){
				var chartsTmp = daColumnConfigs.getOrderedChartsInColumn(columnNameTmp);
				var descColumnNameTmp = descOptionsArg.container + "_" + colIdx++;
				if (!chartsTmp || chartsTmp.length < 1) {
					return;
				}
				_.each(chartsTmp, function(objTmp) {
					var chartTmp = daColumnConfigs.getChart(objTmp.container);
					if (chartTmp) {
						var descDivIdTmp = dashboardResult.getRandomDivId();
						var $descDivIdTmp = $("<div></div>").attr({
							id: descDivIdTmp
						}).addClass('daColumnChart');
						$el(descColumnNameTmp).append($descDivIdTmp);
						var chartDesc = chartTmp.cloneTo({
							container: descDivIdTmp
						}, function(chart){
							$el(chart.AREA_SECTIONS.MAIN_TITLE).text(chartTmp.getTitleText());
							$el(chart.AREA_SECTIONS.MAIN_TITLE).attr({"title": chartTmp.getTitleText()});
						});
						if (chartDesc) {
							chartDesc.currentDashboard = dashboardResult;
							var configTmp = {};
							configTmp[objTmp.container] = chartDesc.container;
							daColumnConfigs.cloneTo(daResultConfig, configTmp);
							chartDesc.render();
							chartTmp.statusChanged(chartTmp.STATUS_MODE.EDIT);
							dashboardResult.addAChartToConfig(descDivIdTmp, chartDesc, descColumnNameTmp);
							dashboardResult.addMarkHelperToChart(descDivIdTmp, daColumnConfigs.getMarkHelperFromChart(objTmp.container));
							dashboardResult.addMarkHelperToChart(descDivIdTmp, {'cloned': 1});
							dashboardResult.setChanged(true, chartDesc);
							configsMap[objTmp.container] = chartDesc.container;
						}
					}
				});
			});
			
			dashboardResult.setDaRenderDone();
			dashboardResult.setChanged(true);
			return dashboardResult;
		};
		
		self.getCurrentEditWidgetOptions = function(chartContainer) {
			if (self.__getCurrentEditWidgetOptions) {
				return self.__getCurrentEditWidgetOptions.apply(self, [chartContainer]);
			}
		};
		var curWidgetOptions;
		self.getCurWidgetOptions = function() {
			return curWidgetOptions;
		};
		self.setCurrentEditWidgetOptions = function(optionsArg, blnAppend) {
			if (blnAppend) {
				$.extend(true, curWidgetOptions, optionsArg);
			} else {
				curWidgetOptions = optionsArg;
			}
		};
		
		self.refreshSelectedCharts = function(tmpRequestArgs, callback, option) {
			option = option || {};
			resetGrpRequestTimestamp();
			daColumnConfigs.iterateCharts(function(chart){
				if (chart.isChartShown() === true) {
					chart.requestDataFunc(chart.genReportAttrs(tmpRequestArgs));
				}
				if (chart && callback) {
					callback.apply(self, [chart]);
				}
				self.changeChartInterval(chart, option);
			});
		};
		self.refreshCertainChart = function(chart, tmpRequestArgs) {
			if (!isObject(chart)) {
				chart = daColumnConfigs.getChart(chart);
			}
			resetGrpRequestTimestamp();
			if (chart.isChartShown() === true) {
				chart.requestDataFunc(chart.genReportAttrs(tmpRequestArgs));
			}
			self.changeChartInterval(chart);
		};
		
		
		var requestInterval = 50;
		var lastStartRefreshTime;
		var reqAllowTids = {};
		self.getReqAllowTids = function() {
			return reqAllowTids;
		};
		var checkWhetherToRequestData = function() {
			if (!lastStartRefreshTime) {
				lastStartRefreshTime = new Date().getTime();
			} else {
				var curTime = new Date().getTime();
				var timeDiff = curTime - lastStartRefreshTime;
				if (timeDiff >= requestInterval) {
					lastStartRefreshTime = curTime;
				} else {
					lastStartRefreshTime = curTime + requestInterval - timeDiff;
					return requestInterval - timeDiff;
				}
			}
		};
		
		self.refreshDaConfigsAfterSuccSaving = function(data) {
			if (self.__refreshDaConfigsAfterSuccSaving) {
				self.__refreshDaConfigsAfterSuccSaving.apply(self, [data]);
			}
		};
		
		self.restoreUnsavedModification = function() {
			daColumnConfigs.restorChartFromRecyle();
		};
		
		var initValuesObj = {};
		var initValues = {
			add: function() {
				var len = arguments.length;
				if (len < 2) {
					return;
				}
				var arg0 = arguments[0];
				if (!initValuesObj[arg0]) {
					initValuesObj[arg0] = {};
				}
				if (len === 3) {
					initValuesObj[arg0][arguments[1]] = arguments[2];
				} else if (len === 2) {
					$.extend(true, initValuesObj[arg0], arguments[1]);
				}
			},
			get: function(cContainer, attrName) {
				if (!cContainer) {
					return;
				}
				if (!attrName) {
					return initValuesObj[cContainer];
				}
				if (initValuesObj[cContainer]) {
					return initValuesObj[cContainer][attrName];
				}
			},
			remove: function(cContainer, attrName) {
				if (!cContainer) {
					return;
				}
				if (!attrName) {
					delete initValuesObj[cContainer];
				} else if (initValuesObj[cContainer]) {
					delete initValuesObj[cContainer][attrName];
				}
			},
			init: function() {
				initValuesObj = {};
			},
			clear: function() {
				initValuesObj = {};
			}
		};
		self.initChartValues = function(chart, option) {
			if (chart && option) {
				initValues.add(chart.container, option);
			}
		};
		var getChartInitValue = {
			get: function(chart, attrName) {
				if (chart && attrName) {
					return initValues.get(chart.container, attrName);
				} else if (chart) {
					return initValues.get(chart.container);
				}
			},
			getTitle: function(chart) {
				return getChartInitValue.get(chart, "title");
			},
			getWidgetConfId: function(chart) {
				return getChartInitValue.get(chart, "widgetConfId");
			},
			getReportId: function(chart) {
				return getChartInitValue.get(chart, "reportId");
			}
		};
		self.getCertainChartInitValue = getChartInitValue;
		self.renderChartTitle = function(chart, mainTitleTmp) {
			if (!mainTitleTmp) {
				mainTitleTmp = "";
			}
			$el(chart.AREA_SECTIONS.MAIN_TITLE).text(mainTitleTmp);
			$el(chart.AREA_SECTIONS.MAIN_TITLE).attr({"title": mainTitleTmp});
			if (!getChartInitValue.get(chart)
					|| !getChartInitValue.get(chart).hasOwnProperty("title")) {
				self.initChartValues(chart, {
					'title': mainTitleTmp
				});
			}
		};
		
		var setRenderStatus = function(status) {
			if (self.rendered === status) {
				return;
			}
			self.rendered = status;
			if (self.rendered === true) {
				prepareChartConfigs();
				daColumnConfigs.refresh();
			}
			if (self.afterRenderedCommonFunc) {
				self.afterRenderedCommonFunc.apply(self, [self]);
			}
		};
		self.setDaRenderDone = function() {
			setRenderStatus(true);
		};
		
		/*self.hackLabelClickable = function() {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					chart.hackLabelClickable(chart);
				}
			});
		};*/
		
		var prepareChartConfigs = function() {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					prepareCertainChartConfigs(chart);
				}
			});
		};
		var curCtlBarSelectContainer;
		self.setCurCtlBarSelectContainer = function(container) {
			curCtlBarSelectContainer = container;
		};
		self.getCurCtlBarSelectContainer = function() {
			return curCtlBarSelectContainer;
		};
		self.getCurCtlBarSelectChart = function() {
			return daColumnConfigs.getChart(curCtlBarSelectContainer);
		};
		//var doConfigType = 'tmp';
		var defDoConfigType = 'persist';
		var doConfigType = options.configType||defDoConfigType;
		var prepareCertainChartConfigs = function(chart) {
			if (self.__prepareCertainChartConfigs) {
				self.__prepareCertainChartConfigs.apply(self, [chart, doConfigType]);
			}
		};
		self.getConfigType = function(attr) {
			if (!attr) {
				return doConfigType;
			} else {
				if (isObject(doConfigType)) {
					return doConfigType[attr]||defDoConfigType;
				} else {
					return doConfigType;
				}
			}
		};
		var renderDashboardSettings = function() {
			if (!self.action) {
				return;
			}
			if (self.blnNewDashboard === true && !options.requestSetting4New) {
				newDashboardSettings();
			} else {
				requestDashboardSettings();
			}
		};
		var newDashboardSettings = function() {
			renderDaColumns(defDashboardSetting);
			setRenderStatus(true);
		};
		var requestDashboardSettings = function() {
			if (self.boardId < 0 && self.board > -100) {
				return;
			}
			var requestArgs = {
				operation: 'dashboardSetting',
				boardId: self.boardId,
				ignore: new Date().getTime()
			};
			if (options.monitorEl) {
				requestArgs.monitorEl = options.monitorEl;
			}
			$.post(self.action,
				requestArgs,
				dashboardSettingsCallBack,
				"json");
		};
		
		self.setLastData = function(data) {
			lastData = data;
		};
		var lastData = {};
		var dashboardSettingsCallBack = function(data, textStatus) {
			var daSettingTmp;
			if (data && data.length > 0) {
				var dataDetail = data[0];
				daSettingTmp = dataDetail.daSetting;
				self.customInfo = dataDetail.customInfo || {};
				self.timeType = self.customInfo.timeType;
			}
			if (!daSettingTmp.columns
					|| !$.isArray(daSettingTmp.columns)
					|| daSettingTmp.columns.length < 1) {
				daSettingTmp = defDashboardSetting;
			}
			
			lastData.data = data;
			
			var daColumnOrders = renderDaColumns(daSettingTmp);
			
			if (data && data.length > 0) {
				var dataDetail = data[0];
				var daReportsTmp = dataDetail.daReports;
				if (daReportsTmp && daColumnOrders.length > 0) {
					var daReportsOrderedItems = [];
					for (var i = 0; i < daColumnOrders.length; i++) {
						daReportsOrderedItems.push({
							order: daColumnOrders[i],
							items: daReportsTmp['order'+daColumnOrders[i]]
						});
					}
					daReportsOrderedItems = sortItemsAsLRTB(daReportsOrderedItems, 
							function(item) {return item.items;},
							function(item) {return {order: item.order}});
					if (daReportsOrderedItems && daReportsOrderedItems.length > 0) {
						_.each(daReportsOrderedItems, function(reportItem) {
							var reportOptions = reportItem.item;
							var orderTmp = reportItem.option.order;
							var mainTitleTmp = reportOptions.title;
							refreshAChart({
								'report': reportOptions,
								'columnName': self.column_container_prefix + orderTmp
							}, function(chart) {
								if (self.__daSettingBackChartRefreshCallback) {
									self.__daSettingBackChartRefreshCallback.apply(self, [chart, mainTitleTmp, reportOptions]);
								}
								self.changeChartInterval(chart);
							});
						});
					}
				}
			}
			
			setRenderStatus(true);
		};
		var colRendered = false;
		var preStoredDaColumnsOrder;
		var renderDaColumns = function(daSettingTmp) {
			if (colRendered === true) {
				return preStoredDaColumnsOrder;
			}
			var $noteDiv = $('<div id="'+da_outter_note_container+'" style="display:none;"><table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">' +
					'<tr> <td height="2"></td></tr>' +
					'<tr><td class="noteError"><div class="daNoteContainer" id="' + da_note_container + '"></div></td></tr>' +
					'<tr><td height="3"></td></tr></table></div>');
			self.$container.append($noteDiv);
			
			var daColumnOrders = new Array();
			if (daSettingTmp && daSettingTmp.columns && daSettingTmp.columns.length > 0) {
				var columnsTmp = daSettingTmp.columns;
				for (var i = 0; i < columnsTmp.length; i++) {
					var columnTmp = columnsTmp[i];
					var colIdTmp = self.column_container_prefix + columnTmp.order;
					var colWidthTmp = columnTmp.width;
					var $columnDivTmp = $('<div></div>').attr({
											id: colIdTmp
										})
										.addClass('daColumn')
										.addClass('daColumnNum'+(i+1))
										.width(colWidthTmp);
					self.$container.append($columnDivTmp);
					daColumnOrders.push(columnTmp.order);
					daColumnConfigs.addColumnConfig({
						columnName: colIdTmp,
						size: pick(columnTmp.size, 'large')
					});
				}
			}
			
			lastData.daSettings = daSettingTmp;
			colRendered = true;
			preStoredDaColumnsOrder = daColumnOrders; 
			return daColumnOrders;
		};
		self.renderDashboardColumns = renderDaColumns;
		
		var syncNewChartConfigs = function(chart) {
			if (chart) {
				prepareCertainChartConfigs(chart);
				chart.statusChanged(daColumnConfigs.getStringMode());
			}
		};
		var refreshAChartCallback = function(chart) {
			if (self.__refreshAChartCallback) {
				self.__refreshAChartCallback.apply(self, [chart]);
			}
		};
		self.addAChartToConfig = function(chartContainerArg, chartArg, columnNameArg) {
			syncNewChartConfigs(chartArg);
			daColumnConfigs.addChart(chartContainerArg, chartArg, columnNameArg);
		};
		self.addMarkHelperToChart = function(chartContainer, optionsArg) {
			daColumnConfigs.addMarkHelperToChart(chartContainer, optionsArg);
		};
		self.getMarkHelperFromChart = function(chartContainer, attrName) {
			return daColumnConfigs.getMarkHelperFromChart(chartContainer, attrName);
		};
		var refreshAChart = function(chartOptions, callback) {
			if (notRequestDataMark) {
				chartOptions._my_do_not_request_data = true;
			}
			var columnNameTmp = chartOptions.columnName;
			var chart_container_idTmp = chartOptions.container;
			if (!chart_container_idTmp && !columnNameTmp) return;
			if (!columnNameTmp) {
				columnNameTmp = daColumnConfigs.getColumnContainer(chart_container_idTmp);
			}
			if (!chart_container_idTmp && !columnNameTmp) return;
			$el(chart_container_idTmp).css('height', '100%');
			var oriChart = daColumnConfigs.getChart(chart_container_idTmp);
			var oriReportId;
			if (oriChart) {
				oriReportId = oriChart.reportId;
				if (!curWidgetOptions) {
					curWidgetOptions = self.getCurrentEditWidgetOptions(oriChart.container);
				}
			}
			if (!chart_container_idTmp) {
				chart_container_idTmp = self.getRandomDivId();
				var $chart_container_idTmp = $("<div></div>")
							.attr({
								id: chart_container_idTmp
							})
							.addClass('daColumnChart');
				$el(columnNameTmp).append($chart_container_idTmp);
			} else {
				if (!$el(chart_container_idTmp).hasClass('daColumnChart')) {
					$el(chart_container_idTmp).addClass('daColumnChart');
				}
				if ($el(chart_container_idTmp).hasClass('daNewWidgetContainer')) {
					$el(chart_container_idTmp).removeClass('daNewWidgetContainer');
				}
				daColumnConfigs.removeChartWithoutContainer(chart_container_idTmp);
			}
			
			var beforeRenderResult;
			var reportTmp = chartOptions.report;
			var additionalOptionTmp = reportTmp.config.opt;
			
			reportTmp.container = chart_container_idTmp;
			if (oriChart) {
				reportTmp.priChanged = oriChart.isInChangeMode();
			}
			
			if (self.__prepareChartOptionsBeforeRender) {
				beforeRenderResult = self.__prepareChartOptionsBeforeRender.apply(self, [reportTmp, oriChart, chart_container_idTmp]);
			}
			
			if (!reportTmp.events) {
				reportTmp.events = {};
			}
			if (reportTmp.events.beforeSendingRequest) {
				reportTmp.events['beforeSendingRequest'] = function(chart) {
					reportTmp.events.beforeSendingRequest.apply(this, [chart]);
					beforeChartSendingRequest.apply(this, [chart]);
				}
			} else {
				reportTmp.events['beforeSendingRequest'] = beforeChartSendingRequest;
			}
			
			var chartTmp;
			if (callback) {
				chartTmp = new ARC.Chart(reportTmp, callback);
			} else {
				chartTmp = new ARC.Chart(reportTmp, refreshAChartCallback);
			}
			
			if (self.__prepareChartOptionsAfterRender) {
				self.__prepareChartOptionsAfterRender.apply(self, [chartTmp, reportTmp, beforeRenderResult]);
			}
			
			if (chartOptions._my_do_not_request_data) {
				chartTmp._my_do_not_request_data = true;
			}
			
			//test code
			var idTmp = -1;
			if (reportTmp.reportId > 0) {
				idTmp = reportTmp.reportId % 2 -2;
			}
			if (reportTmp.oWidgetId) {
				self.addMarkHelperToChart(chart_container_idTmp, {'oWidgetId': reportTmp.oWidgetId});
			}
			
			chartTmp.additionalArgs.add(false, "reportId", reportTmp.reportId);
			var configRpTmp = daColumnConfigs.getReportConfig(reportTmp.reportId);
			if (configRpTmp) {
				//for test code
				if (reportTmp.xAxisType == 'datetime') {
					chartTmp.additionalArgs.add(false, "testCode", "tb");
				}
			}
			chartTmp.additionalArgs.add(false, "forTest", false);
			chartTmp.currentDashboard = self;
			chartTmp.render();
			syncNewChartConfigs(chartTmp);
			daColumnConfigs.addChart(chart_container_idTmp, chartTmp, columnNameTmp);
			if (oriReportId && oriReportId !== chartTmp.reportId) {
				self.addMarkHelperToChart(chart_container_idTmp, {'cloned': 0});
			}
			if (getChartInitValue.getReportId(chartTmp) != chartTmp.reportId) {
				self.setChanged(true, chartTmp);
			} else if (!getChartInitValue.getWidgetConfId(chartTmp)) {
				self.setChanged(true, chartTmp);
			}
			
			if (additionalOptionTmp && additionalOptionTmp.title) {
				self.renderChartTitle(chartTmp, additionalOptionTmp.title);
			}
			
			if (additionalOptionTmp) {
				var selectionTip = "<div class='daMainContentTipText'>";
				selectionTip += "<div><div class='title'>Type:</div><div class='content'>" + (additionalOptionTmp.group || "N/A") + "</div></div>";
				selectionTip += "<div><div class='title'>Data Set:</div><div class='content'>" + (additionalOptionTmp.metric || "N/A") + "</div></div>";
				//selectionTip += "<div><div class='title'>Name:</div><div class='content'>" + (additionalOptionTmp.title || "N/A") + "</div></div>";
				selectionTip += "</div>";
				chartTmp.my_addi_data_config_info = selectionTip;
			}
			
			if (chartOptions._my_do_not_request_data && additionalOptionTmp) {
				toggleEditTipInfoForChart(chartTmp, true, chartTmp.my_addi_data_config_info);
			}
		};
		
		var requestWaitingQueue = {};
		var beforeChartSendingRequest = function(chart) {
			if (!chart || chart._my_do_not_request_data) {
				return false;
			}
			if (!requestWaitingQueue[chart.container] && chart.isChartShown() === true) {
				var reqAllow = checkWhetherToRequestData();
				if (reqAllow && reqAllow > 0) {
					var containerTmp = chart.container;
					requestWaitingQueue[containerTmp] = true;
					if (!reqAllowTids[containerTmp]) {
						reqAllowTids[containerTmp] = {};
					}
					if ("tid" in reqAllowTids[containerTmp]) {
						clearTimeout(reqAllowTids[containerTmp].tid);
						delete reqAllowTids[containerTmp];
					}
					if (!reqAllowTids[containerTmp]) {
						reqAllowTids[containerTmp] = {};
					}
					reqAllowTids[containerTmp].tid = setTimeout(function(){
						chart.requestDataFunc(chart.genReportAttrs());
						requestWaitingQueue[containerTmp] = false;
					}, reqAllow);
					return false;
				}
			}
			refreshRequestData(chart);
		};
		var refreshRequestData = function(chart) {
			if (chart.isFirstTimeDataCall() || !chart.data_request_by_interval) {
				chart.showLoadingTip(chart.getCurrentShownContainer(), true);
			}
			if (self.__refreshRequestData) {
				self.__refreshRequestData.apply(self, [chart]);
			}
		};
		var removeCertainTimeId = function(chart) {
			var container = chart.container || "";
			chart.clearRequestInterval();
			if (container in reqAllowTids) {
				if ("tid" in reqAllowTids[container]) {
					clearTimeout(reqAllowTids[container].tid);
				}
				delete reqAllowTids[container];
			}
			if (container in requestWaitingQueue) {
				delete requestWaitingQueue[container];
			}
		};
		self.removeDashboardTimeIds = function(blnRemoveAll, containers) {
			if (blnRemoveAll) {
				daColumnConfigs.iterateCharts(function(chart){
					if (chart) {
						removeCertainTimeId(chart);
					}
				});
			} else {
				if (containers) {
					if (!$.isArray(containers)) {
						containers = [containers];
					}
					var chartTmp;
					_.each(containers, function(container) {
						chartTmp = self.getChartByContainer(container);
						if (chartTmp) {
							removeCertainTimeId(chartTmp);
						}
					});
				} else {
					var curChartContainers = [];
					daColumnConfigs.iterateCharts(function(chart){
						if (chart) {
							curChartContainers.push(chart.container);
						}
					});
					if (curChartContainers && curChartContainers.length > 0) {
						if (reqAllowTids) {
							var chartTmp;
							_.each(reqAllowTids, function(container) {
								if (!_.find(curChartContainers, function(containerArg){return containerArg == container;})) {
									chartTmp = self.getChartByContainer(container);
									if (chartTmp) {
										chartTmp.clearRequestInterval();
									}
									if ((container in reqAllowTids) && ("tid" in reqAllowTids[container])) {
										clearTimeout(reqAllowTids[container].tid);
									}
									delete reqAllowTids[container];
								}
							});
						}
						if (requestWaitingQueue) {
							_.each(requestWaitingQueue, function(container) {
								if (!_.find(curChartContainers, function(containerArg){return containerArg == container;})) {
									if (container in requestWaitingQueue) {
										delete requestWaitingQueue[container];
									}
								}
							});
						}
					}
				}
			}
		};
		
		self.refreshForCertainReportId = function(oldId, newId, containerArg) {
			var chartsToDeal = [];
			var blnExistedId = false;
			if (containerArg) {
				daColumnConfigs.iterateCharts(function(chart){
					if (chart && chart.isChartShown()) {
						if (chart.reportId === oldId && chart.container === containerArg) {
							var objTmp = $.extend(true, {}, daColumnConfigs.getCommonConfigFromChart(chart.container, chart));
							objTmp.reportId = newId;
							chartsToDeal.push({
								'report': objTmp,
								'container': chart.container
							});
							blnExistedId = true;
						}
					}
				});
			} else {
				daColumnConfigs.iterateCharts(function(chart){
					if (chart && chart.isChartShown()) {
						if (chart.reportId === oldId) {
							var objTmp = $.extend(true, {}, daColumnConfigs.getCommonConfigFromChart(chart.container, chart));
							objTmp.reportId = newId;
							chartsToDeal.push({
								'report': objTmp,
								'container': chart.container
							});
							blnExistedId = true;
						}
					}
				});
			}
			if (!blnExistedId) {
				var objTmp = $.extend(true, {}, daColumnConfigs.getCommonConfigFromChart(containerArg));
				objTmp.reportId = newId;
				chartsToDeal.push({
					'report': objTmp,
					'container': containerArg
				});
			}
			
			var requestArgs = {
				'operation': 'getConfig4Widget',
				'widgetId': newId,
				'ignore': new Date().getTime()
			};
			$.post(self.action,
				requestArgs,
				function(data, textStatus) {
					refreshForCertainReportIdCallback(chartsToDeal, data, textStatus);
				},
				"json");
		};
		var refreshForCertainReportIdCallback = function(chartsToDeal, data, textStatus) {
			if (chartsToDeal.length > 0) {
				_.each(chartsToDeal, function(chartOptions){
					$.extend(true, chartOptions.report, data);
					if (daColumnConfigs.isInEditMode()) {
						chartOptions._my_do_not_request_data = true;
					}
					refreshAChart(chartOptions, refreshAChartCallback);
				});
			}
			
			curWidgetOptions = null;
			daColumnConfigs.refresh();
		};
		var toggleEditTipInfoForChart = function(chart, blnShown, tips) {
			if (chart) {
				if (blnShown) {
					chart.mainContentTip.show(tips);
				} else {
					chart.mainContentTip.hide();
				}
			}
		};
		
		self.getRandomDivId = function() {
			var chart_container_idTmp = self.chart_container_prefix + daDefRand();
			while(document.getElementById(chart_container_idTmp)) {
				chart_container_idTmp = self.chart_container_prefix + daDefRand();
			}
			return chart_container_idTmp;
		};
		
		self.addNewEditChart = function(containerArg, columnArg) {
			if (columnArg && containerArg) {
				daColumnConfigs.addChart(containerArg, {noChartMark: true}, columnArg);
				daColumnConfigs.refresh();
			}
		};
		self.removeNewEditChart = function(containerArg, columnArg, blnRefresh) {
			if (columnArg && containerArg) {
				daColumnConfigs.removeChart(containerArg, columnArg);
				if (blnRefresh) {
					daColumnConfigs.refresh();
				}
			}
		};
		self.getReportConfig = function(id) {
			return daColumnConfigs.getReportConfig(id);
		};
		self.setTmpReportConfig = function(container, tmpConfigs, blnAppend) {
			daColumnConfigs.setTmpReportConfig(container, tmpConfigs, blnAppend);
			self.setChanged(true, daColumnConfigs.getChart(container));
		};
		self.setTmpReportConfigNoChg = function(container, tmpConfigs, blnAppend) {
			daColumnConfigs.setTmpReportConfig(container, tmpConfigs, blnAppend);
		};
		self.getCurReportWidgetConfig = function(chart, attr) {
			return daColumnConfigs.getCurReportWidgetConfig(chart, attr);
		};
		self.getOriReportWidgetConfig = function(chart, attr) {
			return daColumnConfigs.getOriReportWidgetConfig(chart, attr);
		};
		self.getAllReportIds = function() {
			return daColumnConfigs.getAllReportIds();
		};
		self.isBlank = function() {
			return daColumnConfigs.getAllReportIds().length < 1;
		};
		self.checkReportIdExist = function(id, allIds) {
			if (!allIds) {
				allIds = self.getAllReportIds();
			}
			return _.contains(allIds, id);
		};
		self.isInEditMode = function() {
			return daColumnConfigs.isInEditMode();
		}
		self.getChartByContainer = function(container) {
			if (container) {
				return daColumnConfigs.getChart(container);
			}
		};
		
		self.refreshDaConfigForCharts = function(func) {
			if (!func) {
				return;
			}
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					func.apply(chart, [chart]);
				}
			});
		};
		
		self.changeChartInterval = function(chart, option) {
			if (self.__changeChartInterval) {
				if (chart) {
					self.__changeChartInterval.apply(self, [chart, option]);
				} else {
					daColumnConfigs.iterateCharts(function(chart){
						if (chart) {
							self.__changeChartInterval.apply(chart, [chart, option]);
						}
					});
				}
			}
		};
		
		self.applyToCharts = function(callback) {
			daColumnConfigs.iterateCharts(function(chart){
				if (chart) {
					callback.apply(self, [chart]);
				}
			});
		};
		
		options.p = options.p || {};
		var _accessPermission = {
			READ: options.p.r?true:false,
			WRITE: options.p.w?true:false
		};
		self.checkEditPermission = function() {
			return _accessPermission.WRITE;
		};
		
		var defDashboardSetting = {
			columns: [{
				order: 0,
				size: 'large',
				width: '50%'
			},{
				order: 1,
				size: 'large',
				width: '50%'
			}]
		};
		
		if (self.__callItWhenRenderDashboard) {
			self.__callItWhenRenderDashboard.apply(self);
		}
	};
	
	Chart.prototype.__editPermissionCheck = function() {
		if (this.currentDashboard != null) {
			return this.currentDashboard.checkEditPermission();
		}
	};
	
	Dashboard.prototype.configFunc = null;
	Dashboard.prototype.topologyConfigFunc = null;
	Dashboard.prototype.timeConfigFunc = null;
	Dashboard.prototype.afterCloseFunc = null;
	Dashboard.prototype.checkConfigFunc = null;
	Dashboard.prototype.daHelper = null;
	Dashboard.prototype.whenActiveFunc = null;
	
	Dashboard.prototype.__getRefreshPeriodStr = null;
	Dashboard.prototype.__getCurrentEditWidgetOptions = null;
	Dashboard.prototype.__refreshDaConfigsAfterSuccSaving = null;
	Dashboard.prototype.__prepareCertainChartConfigs = null;
	Dashboard.prototype.__daSettingBackChartRefreshCallback = null;
	Dashboard.prototype.__refreshAChartCallback = null;
	Dashboard.prototype.__callItWhenRenderDashboard = null;
	Dashboard.prototype.__refreshRequestData = null;
	Dashboard.prototype.__prepareChartOptionsBeforeRender = null;
	Dashboard.prototype.__restorChartFromRecyleNoDelete = null;
	Dashboard.prototype.__clearRecyleItemsNoDelete = null;
	Dashboard.prototype.__refreshConfigCheckEver = null;
	Dashboard.prototype.__prepareChartConfigData = null;
	Dashboard.prototype.__changeChartInterval = null;
	Dashboard.prototype.__chartsCountLimitation = 10;
	Dashboard.prototype.__unitConvertStrategy = null;
	Dashboard.prototype.__blankWidgetHeight = 305;
	
	var DashColumnConfigs = function(daInstance) {
		var self = this;
		self.daInstance = daInstance;
		var daMultiColumns = {};
		var columnCharts = {};
		var columnChartHelpers = {};
		var totalChartsCount = 0;
		var curBlankWidget;
		var daBlankWidgetConfigs;
		var sortedColumns = [];
		var editMode = false;
		var oldStatus;
		var reportConfigs = {};
		var tmpReportConfigs = {};
		
		self.getColumnCharts = function() {
			return columnCharts;
		};
		self.cloneTo = function(dcDesc, cConfigMaps) {
			if (!dcDesc) {
				return;
			}
			if (!cConfigMaps) {
				cConfigMaps = {};
			}
			
			var rcTmp = dcDesc.getReportConfig();
			if (!rcTmp || _.size(rcTmp) < 1) {
				dcDesc.overrideReportConfig(reportConfigs);
			}
			
			var configsTmp = {};
			_.each(_.keys(cConfigMaps), function(key) {
				if (isUndefined(tmpReportConfigs[key])) {
					return;
				}
				configsTmp[cConfigMaps[key]] = $.extend(true, {}, tmpReportConfigs[key]);
			});
			dcDesc.overrideTmpReportConfig(configsTmp, true);
		};
		var clearRelatedConfigs = function() {
			var args = arguments,
				len = args.length;
			if (len < 1) {
				return;
			}
			var container = args[0],
				reportId;
			if (isObject(container)) {
				container = container.container;
			} else if (len > 1){
				reportId = args[1];
			}
			if (columnCharts[container]) {
				delete columnCharts[container];
			}
			if (columnChartHelpers[container]) {
				delete columnChartHelpers[container];
			}
			if (reportConfigs[reportId]) {
				delete reportConfigs[reportId];
			}
			if (tmpReportConfigs[container]) {
				delete tmpReportConfigs[container];
			}
			self.removeChart(container);
			sortColumnNames();
		};
		var sortColumnNames = function() {
			var columnsTmp = _.keys(daMultiColumns);
			if (columnsTmp && columnsTmp.length > 0) {
				sortedColumns = _.sortBy(columnsTmp, function(name){return name;});
			}
		};
		self.getSortedColumnNames = function() {
			return sortedColumns;
		};
		self.addColumnConfig = function(options) {
			if (options.columnName) {
				daMultiColumns[options.columnName] = {
						chartCount: 0,
						charts: [],
						size: options.size,
						containerConfig: defContainerConfig
				};
				sortColumnNames();
			}
		};
		self.getColumnConfig = function(columnContainer) {
			if (columnContainer) {
				return daMultiColumns[columnContainer];
			}
		};
		self.getChartsCount = function() {
			return totalChartsCount;
		};
		
		self.setChartsCount = function(ccount) {
			totalChartsCount=ccount;
		};
		
		self.statusChanged = function(status) {
			if (status === 'edit') {
				editMode = true;
			} else {
				editMode = false;
			}
			oldStatus = status;
			self.refresh();
		};
		self.getStringMode = function() {
			if (editMode) {
				return 'edit';
			}
			return 'view';
		};
		self.isInEditMode = function() {
			return editMode;
		};
		
		self.iterateCharts = function(func, columnContainer) {
			var charts = self.getAllCharts(columnContainer);
			if (charts && charts.length > 0) {
				for (var i = 0; i < charts.length; i++) {
					func(charts[i]);
				}
			}
		};
		self.getAllCharts = function(columnContainer) {
			var colContainers = [];
			var result = [];
			if (columnContainer) {
				if ($.isArray(columnContainer)) {
					colContainers = columnContainer;
				} else {
					colContainers.push(columnContainer);
				}
			} else {
				colContainers = self.getSortedColumnNames();
			}
			
			result = _.pluck(sortItemsAsLRTB(colContainers, function(containerName) {
							var items = [];
							var colTmp = daMultiColumns[containerName];
							if (colTmp && colTmp.charts && colTmp.charts.length > 0) {
								for (var i = 0; i < colTmp.charts.length; i++) {
									var objTmp = columnCharts[colTmp.charts[i]];
									if (objTmp && !objTmp.delMark && !objTmp.noChartMark) {
										items.push(objTmp.chart);
									}
								}
							}
							return items;
						}), 'item');
			
			return result;
		};
		self.getChart = function(chartContainer) {
			if (chartContainer) {				
				var objTmp = columnCharts[chartContainer];
				if (objTmp && !objTmp.delMark && !objTmp.noChartMark) {
					return objTmp.chart;
				}
			}
		};
		var isChartNoChartMark = function(chartContainer) {
			if (chartContainer) {				
				var objTmp = columnCharts[chartContainer];
				if (objTmp && objTmp.noChartMark) {
					return true;
				}
			}
		};
		self.getChartByReportId = function(reportId) {
			if (reportId) {				
				for (var key in columnCharts) {
					var objTmp = columnCharts[key];
					if (objTmp && !objTmp.delMark && !objTmp.noChartMark) {
						if (objTmp.chart
								&& objTmp.chart.reportId === reportId) {
							return objTmp.chart;
						}
					}
				}
			}
		};
		self.getAllReportIds = function() {
			var result = [];
			var charts = self.getAllCharts();
			if (charts && charts.length > 0) {
				for (var i = 0; i < charts.length; i++) {
					result.push(charts[i].reportId);
				}
			}
			return result;
		};
		self.getMarkHolderChart = function(chartContainer) {
			if (chartContainer) {
				var objTmp = columnCharts[chartContainer];
				if (objTmp && !objTmp.delMark && objTmp.noChartMark) {
					return objTmp.chart;
				}
			}
		};
		self.getChartConfig = function(chartContainer) {
			if (chartContainer) {				
				var objTmp = columnCharts[chartContainer];
				if (objTmp && !objTmp.delMark && !objTmp.noChartMark) {
					return objTmp;
				}
			}
		};
		self.getCommonConfigFromChart = function(chartContainer, chart) {
			var result = {};
			if (chart) {
				var optionsTmp = chart.getOptions();
				result.width = optionsTmp.width;
				result.chartHeight = optionsTmp.chartHeight;
				result.xAxisType = optionsTmp.xAxisType;
			} else {
				var columnNameTmp = self.getColumnContainer(chartContainer);
				if (daMultiColumns[columnNameTmp]) {
					result = $.extend(true, result, daMultiColumns[columnNameTmp].containerConfig);
				}
			}
			return result;
		};
		self.getColumnContainer = function(chartContainer) {
			var result = $el(chartContainer).parent().attr("id");
			
			if (!result) {
				var objTmp = columnCharts[chartContainer];
				if (objTmp) {
					result = objTmp.columnName;
				}
			}
			
			return result;
		};
		var addChartToColumn = function(columnContainer, chartContainer, chart) {
			var objTmp = daMultiColumns[columnContainer];
			if (objTmp) {
				objTmp.chartCount += 1;
				objTmp.charts.push(chartContainer);
				objTmp.containerConfig = self.getCommonConfigFromChart(chartContainer);
			}
			
			columnCharts[chartContainer] = {
				chart: chart,
				reportId: chart.reportId,
				columnName: columnContainer
			};
			if (chart.noChartMark) {
				columnCharts[chartContainer].noChartMark = chart.noChartMark;
				columnCharts[chartContainer].chart.container = chartContainer;
			}
				
			totalChartsCount++;
		};
		var removeChartFromColumn = function(columnContainer, chartContainer, blnRmContainer) {
			var objTmp = daMultiColumns[columnContainer];
			var preTotalChartCount = totalChartsCount;
			if (objTmp) {
				objTmp.charts = _.without(objTmp.charts, chartContainer);
				totalChartsCount -= (objTmp.chartCount - objTmp.charts.length);
				objTmp.chartCount = objTmp.charts.length;
			}
			if (totalChartsCount < preTotalChartCount && !isChartNoChartMark(chartContainer)) {
				self.resortColumnChartsOrder("row", {
					ignore: [chartContainer]
				});
			}
			if (columnCharts[chartContainer]) {
				if (columnCharts[chartContainer].chart
						&& columnCharts[chartContainer].chart.destroy) {
					columnCharts[chartContainer].chart.destroy(blnRmContainer);
				}
				delete columnCharts[chartContainer];
			}
		};
		var checkIfThereIsRemovedItems = function(optionArg) {
			optionArg = optionArg || {};
			var reportId = optionArg.reportId || -1;
			for (var key in columnCharts) {
				if (columnCharts[key].delMark && columnCharts[key].chart && columnCharts[key].chart.reportId == reportId) {
					return key;
				}
			}
		};
		var removeRecyleExistItems = function(columnContainer, optionArg) {
			var existedRemovedItem = checkIfThereIsRemovedItems(optionArg);
			if (existedRemovedItem) {
				removeChartFromColumn(columnContainer, existedRemovedItem, true);
				return true;
			}
			return false;
		};
		var moveChartToRecyleFromColumn = function(columnContainer, chartContainer, optionArg) {
			if (columnCharts[chartContainer]
					&& optionArg
					&& optionArg.hasOwnProperty("reportId")
					&& columnCharts[chartContainer].reportId === optionArg.reportId) {
				if (checkIfThereIsRemovedItems({reportId: optionArg.reportId})) {
					removeChartFromColumn(columnContainer, chartContainer, true);
				};
			}
			var objTmp = daMultiColumns[columnContainer];
			var preTotalChartCount = totalChartsCount;
			if (objTmp) {
				objTmp.charts = _.without(objTmp.charts, chartContainer);
				totalChartsCount -= (objTmp.chartCount - objTmp.charts.length);
				objTmp.chartCount = objTmp.charts.length;
			}
			if (columnCharts[chartContainer]) {
				if (totalChartsCount < preTotalChartCount) {
					self.resortColumnChartsOrder("row", {
						ignore: [chartContainer]
					});
				}
				columnCharts[chartContainer].delMark = true;
				columnCharts[chartContainer].chart.$container.hide();
			}
		};
		self.restorChartFromRecyle = function() {
			self.resortColumnChartsToCertainOrder(self.daInstance.initColumnChartsOrder);
			for (var key in columnCharts) {
				if (columnCharts[key].delMark && columnCharts[key].chart && self.daInstance.initReportIds.isIn(columnCharts[key].chart.reportId)) {
					var chartTmp = columnCharts[key].chart;
					if (chartTmp) {
						var objTmp = daMultiColumns[columnCharts[key].columnName];
						if (!objTmp) {
							self.addColumnConfig({
								'columnName': columnCharts[key].columnName,
								'size': 'large'
							});
						}
						
						objTmp = daMultiColumns[columnCharts[key].columnName];
						if (!objTmp) {
							continue;
						}
						objTmp.charts.push(chartTmp.container);
						totalChartsCount += 1;
						objTmp.chartCount = objTmp.charts.length;
						
						delete columnCharts[key].delMark;
						chartTmp.$container.show();
					}
				} else {
					var chartTmp = columnCharts[key].chart;
					if (chartTmp) {
						if (self.daInstance.__restorChartFromRecyleNoDelete) {
							self.daInstance.__restorChartFromRecyleNoDelete.apply(self, [chartTmp]);
						}
					}
				}
			}
			
			self.clearRecyleItems();
		};
		self.clearRecyleItems = function() {
			var chartTmp;
			for (var key in columnCharts) {
				if (columnCharts[key].delMark) {
					if (columnCharts[key].chart && columnCharts[key].chart.destroy) {
						columnCharts[key].chart.destroy(true);
						clearRelatedConfigs(columnCharts[key].chart);
					}
					delete columnCharts[key];
				} else if (columnCharts[key].chart) {
					chartTmp = columnCharts[key].chart;
					if (chartTmp) {
						if (self.daInstance.__clearRecyleItemsNoDelete) {
							self.daInstance.__clearRecyleItemsNoDelete.apply(self, [chartTmp]);
						}
					}
				}
			}
			tmpReportConfigs = {};
			self.daInstance.initColumnChartsOrder = null;
		};
		var resetBlankWidgetConfigs = function(chart, options) {
			if (!daBlankWidgetConfigs && chart && chart.$container && chart.$container.length > 0) {
				daBlankWidgetConfigs = {
						width: chart.$container.attr('width'),
						height: self.daInstance.__blankWidgetHeight,
						style: chart.$container.attr('style')
				};
			}
		};
		self.addChart = function(chartContainer, chart, columnContainer) {
			if (chartContainer && chart) {
				if (!columnContainer) {
					columnContainer = self.getColumnContainer(chartContainer);
				}
				if (!columnContainer) return;
				resetBlankWidgetConfigs(chart);
				addChartToColumn(columnContainer, chartContainer, chart);
			}
		};
		self.removeChart = function(chartContainer, columnContainer) {
			if (chartContainer) {
				if (!columnContainer) {
					columnContainer = self.getColumnContainer(chartContainer);
				}
				if (!columnContainer) return;
				removeChartFromColumn(columnContainer, chartContainer, true);
			}
		};
		self.removeChartWithoutContainer = function(chartContainer, columnContainer) {
			if (chartContainer) {
				if (!columnContainer) {
					columnContainer = self.getColumnContainer(chartContainer);
				}
				if (!columnContainer) return;
				removeChartFromColumn(columnContainer, chartContainer, false);
			}
		};
		self.moveChartToRecyle = function(chartContainer, optionArg) {
			if (chartContainer) {
				var columnContainer;
				if (!optionArg || !optionArg.columnContainer) {
					columnContainer = self.getColumnContainer(chartContainer);
				} else {
					columnContainer = optionArg.columnContainer;
				}
				if (!columnContainer) return;
				moveChartToRecyleFromColumn(columnContainer, chartContainer, optionArg);
			}
		};
		self.setCurBlankWidgetEditMode = function(editMode) {
			if (curBlankWidget) {
				curBlankWidget.editMode = editMode;
			}
		};
		self.refresh = function(options, callback) {
			if (self.daInstance.__refreshConfigCheckEver) {
				self.daInstance.__refreshConfigCheckEver.apply(self);
			}
			
			if (!editMode) {
				hideBlankWidgetIfThereIs();
				return;
			}
			
			if (!self.daInstance.checkCanAddMoreChart()) {
				return;
			}
			
			if (curBlankWidget) {
				curBlankWidget.toggleShow(editMode);
			}
			
			var minCountDiv = getMinChartsColumn();
			var blankInSameColumn = false;
			if (curBlankWidget 
					&& curBlankWidget.editMode === false){
				if(curBlankWidget.columnName !== minCountDiv) {
					curBlankWidget.$container.remove();
				} else {
					blankInSameColumn = true;
				}
			}
			if (blankInSameColumn) return;
			
			var blankDiv = self.daInstance.getRandomDivId();
			var $blankDiv = $('<div></div>').attr({
								id: blankDiv
							})
							.width('auto')
							.addClass('daColumnChart')
							.addClass('daNewWidgetContainer');
			$el(minCountDiv).append($blankDiv);
			curBlankWidget = new BlankWidget(self.daInstance, {
				columnName: minCountDiv,
				container: blankDiv,
				size: self.getBlankWidgetConfig()
			});
			curBlankWidget.render();
		};
		var hideBlankWidgetIfThereIs = function() {
			if (editMode) {
				return;
			}
			if (curBlankWidget) {
				curBlankWidget.toggleShow(false);
			}
			$("div.daNewWidgetContent").parent("div.daNewWidgetContainer").hide();
		};
		
		var getMinChartsColumn = function() {
			var minCount = -1;
			var minCountDiv;
			if (sortedColumns && sortedColumns.length > 0) {
				var len = sortedColumns.length;
				for (var i = 0; i < len; i++) {
					var key = sortedColumns[i];
					var daColumn = daMultiColumns[key];
					if (daColumn) {
						if (minCount === -1 || daColumn.chartCount < minCount) {
							minCount = daColumn.chartCount;
							minCountDiv = key;
						}
					}
				}
				if (!minCountDiv) {
						var daColumn = daMultiColumns[sortedColumns[0]];
						if (daColumn) {
							minCountDiv = key;
						}
				}
			}
			
			return minCountDiv;
		};
		
		self.addMarkHelperToChart = function(chartContainer, optionsArg) {
			if (!columnChartHelpers[chartContainer]) {
				columnChartHelpers[chartContainer] = {};
			}
			$.extend(true, columnChartHelpers[chartContainer], optionsArg);
		};
		self.getMarkHelperFromChart = function(chartContainer, attrName) {
			if (!attrName) {
				return columnChartHelpers[chartContainer];
			} else {
				var objTmp = columnChartHelpers[chartContainer];
				if (objTmp) {
					return objTmp[attrName];
				}
			}
		};
		self.getAllMarkHelpers = function() {
			return columnChartHelpers;
		};
		
		self.overrideReportConfig = function(configs) {
			reportConfigs = $.extend(true, {}, configs);
		};
		self.setReportConfig = function(id, config) {
			reportConfigs[id] = config;
		};
		self.getReportConfig = function(id) {
			if (!isUndefined(id)) {
				return reportConfigs[id];
			}
			return reportConfigs;
		};
		
		self.overrideTmpReportConfig = function(configs, blnAppend) {
			if (blnAppend) {
				tmpReportConfigs = $.extend(true, tmpReportConfigs, configs);
			} else {
				tmpReportConfigs = $.extend(true, {}, configs);
			}
		};
		self.setTmpReportConfig = function(container, tmpConfigs, blnAppend) {
			if (!container) {
				return;
			}
			if (blnAppend && tmpReportConfigs[container]) {
				$.extend(true, tmpReportConfigs[container], tmpConfigs);
				return;
			}
			tmpReportConfigs[container] = tmpConfigs;
		};
		self.getTmpReportConfig = function(container) {
			return tmpReportConfigs[container];
		};
		
		self.setOriReportWidgetConfig = function(chart, tmpConfigs, blnAppend) {
			if (!chart) {
				return;
			}
			if (blnAppend && reportConfigs[chart.reportId]) {
				$.extend(true, reportConfigs[chart.reportId], tmpConfigs);
				return;
			}
			reportConfigs[chart.reportId] = tmpConfigs;
		};
		self.getOriReportWidgetConfig = function(chart, attr) {
			var oriWconfig = self.getReportConfig(chart.reportId);
			if (oriWconfig) {
				oriWconfig = oriWconfig.widgetConfig;
			}
			if (oriWconfig && attr) {
				return oriWconfig[attr];
			}
			return oriWconfig;
		};
		self.getCurReportWidgetConfig = function(chart, attr) {
			var oriWconfig = self.getReportConfig(chart.reportId);
			if (oriWconfig) {
				oriWconfig = oriWconfig.widgetConfig;
			}
			var result = {};
			if (oriWconfig) {
				result = $.extend(true, result, oriWconfig);
			}
			oriWconfig = self.getTmpReportConfig(chart.container);
			if (oriWconfig) {
				result = $.extend(true, result, oriWconfig);
			}
			if (attr) {
				return result[attr];
			}
			return result;
		};
		
		self.getOrderedChartsInColumn = function(name) {
			var chartsTmp = [];
			$('#' + name + ' > div.daColumnChart').each(function(){
				if ($(this).hasClass("daNewWidgetContainer")) {
					return;
				}
				chartsTmp.push({
					container: this.id,
					top: $(this).offset().top
				});
			});
			
			return chartsTmp;
		};
		self.getColumnLayout = function() {
			if (!sortedColumns || sortedColumns.length < 1) return;
			var blnRealChart = _.any(columnCharts, function(obj){
				if (obj && !obj.noChartMark) return true;
				return false;
			});
			if (!blnRealChart) return;
			var result = {};
			
			var columnIdx = 0;
			_.each(sortedColumns, function(name) {
				if (!name) return;
				var columnTmp = daMultiColumns[name]; 
				if (columnTmp) {
					result[name] = {
						order: columnIdx++,
						size: columnTmp.size,
						charts: []
					};
					var chartsTmp = self.getOrderedChartsInColumn(name);
					if (!chartsTmp || chartsTmp.length < 1) return;
					chartsTmp = _.sortBy(chartsTmp, function(obj){return obj.top;});
					
					if (self.daInstance.__prepareChartConfigData) {
						self.daInstance.__prepareChartConfigData.apply(self, [result[name], chartsTmp]);
					}
				}
			});
			
			return result;
		};
		
		self.getCurrentSortedColumnCharts = function(columnName) {
			var columns = [];
			if (columnName) {
				if ($.isArray(columnName)) {
					columns = columnName;
				} else {
					columns.push(columnName);
				}
			} else {
				columns = sortedColumns;
			}
			if (columns && columns.length > 0) {
				var result = {};
				_.each(columns, function(cname){
					var columnTmp = daMultiColumns[cname];
					if (columnTmp) {
						var chartsTmp = self.getOrderedChartsInColumn(cname);
						if (!chartsTmp || chartsTmp.length < 1) return;
						result[cname] = _.map(_.sortBy(chartsTmp, function(obj){return obj.top;}), function(obj){return obj.container;});
					}
				});
				return result;
			}
		};
		self.resortColumnChartsToCertainOrder = function(oriOrder) {
			if (!oriOrder) {
				return;
			}
			_.each(_.keys(oriOrder), function(cname){
				var columnTmp = daMultiColumns[cname];
				if (columnTmp) {
					columnTmp.charts = oriOrder[cname];
					columnTmp.chartCount = columnTmp.charts.length;
					_.each(columnTmp.charts, function(chartTmp) {
						$el(chartTmp).appendTo("#"+cname);
					});
					adjustPositionOfNewWidget(cname);
				}
			});
		};
		var adjustPositionOfNewWidget = function(columnName) {
			$el(columnName + " div.daNewWidgetContent").parent().appendTo("#" + columnName);
		};
		var isChartRemoved = function(chartContainer) {
			return !self.getChart(chartContainer);
		};
		
		self.resortColumnChartsOrder = function(strategy, option) {
			strategy = strategy || "column";
			option = option || {};
			var ignoreCharts = option.ignore || [];
			if (strategy === "row") {
				var columnChartsTmp = {};
				var chartsCount = 0;
				_.each(sortedColumns, function(name) {
					if (!name) return;
					var columnTmp = daMultiColumns[name];
					if (columnTmp) {
						var chartsTmp = self.getOrderedChartsInColumn(name);
						if (!chartsTmp || chartsTmp.length < 1) return;
						chartsTmp = _.filter(_.sortBy(chartsTmp, function(obj){return obj.top;}), function(obj){return !isChartRemoved(obj.container);});
						columnChartsTmp[name] = {
							count: chartsTmp.length,
							charts: chartsTmp
						};
						if (chartsTmp.length > chartsCount) {
							chartsCount = chartsTmp.length; 
						}
						columnTmp.charts = [];
						columnTmp.chartCount = 0;
					}
				});
				if (chartsCount > 0) {
					var chartsTmp = [];
					var chartContainer;
					for (var i = 0; i < chartsCount; i++) {
						_.each(sortedColumns, function(name) {
							if (columnChartsTmp[name] && columnChartsTmp[name].count > i) {
								chartContainer = columnChartsTmp[name].charts[i].container; 
								if ($.inArray(chartContainer, ignoreCharts) == -1 && !isChartRemoved(chartContainer)) {
									chartsTmp.push(chartContainer);
								}
							}
						});
					}
					var curPos = 0;
					var chartsLen = chartsTmp.length;
					while (curPos < chartsLen) {
						_.each(sortedColumns, function(name) {
							if (curPos >= chartsLen) {
								return;
							}
							var columnTmp = daMultiColumns[name];
							if (columnTmp) {
								var chartTmp = chartsTmp[curPos];
								columnTmp.charts.push(chartTmp);
								columnTmp.chartCount++;
								$el(chartTmp).appendTo("#"+name);
								curPos++;
							}
						});
					}
					_.each(sortedColumns, function(cname) {
						adjustPositionOfNewWidget(cname);
					});
				}
			}
		};
		
		self.getBlankWidgetConfig = function() {
			return daBlankWidgetConfigs;
		};
		
		var defContainerConfig = {
			'width': -1,
			'chartHeight': 275
		};
	};
	
	var BlankWidget = function(daInstance, options, callback) {
		var self = this;
		self.daInstance = daInstance;
		self.columnName = options.columnName;
		self.editMode = false;
		var container = options.container;
		self.$container = $el(container);
		var size = options.size;
		
		self.render = function() {
			newBlankWidgetCallBack();
		};
		self.toggleShow = function(blnShow) {
			if (self.$container.find("div.daNewWidgetContent").length === 0) {
				return;
			}
			if (blnShow) {
				self.$container.show();
			} else {
				self.$container.hide();
			}
		};
		
		var newBlankWidgetCallBack = function(data, textStatus) {
			if (!size) {
				size = defBlankWidgetSize;
			}
			self.$container.attr({
							style: size.style
						})
						.height(size.height);
			var $div1 = $('<div>Select a premade widget from<br/>the left panel to display here</div>')
							.addClass('daNewWidgetContent');
			self.$container.append($div1);
			/*self.$container.click(function(e){
				if (self.daInstance.canAddEditSection() === false) {
					return;
				}
				self.$container.unbind('click');
				self.editMode = true;
				self.daInstance.addNewEditChart(container, self.columnName);
				
				if (Dashboard.prototype.configFunc) {
					Dashboard.prototype.configFunc(container, {});
				}
			});*/
		};
		
		var closeNewEditWidget = function() {
			self.$container.remove();
			self.daInstance.removeNewEditChart(container, self.columnName);
		};
		
		var defBlankWidgetSize = {
			style: "width: auto;",
			height: self.daInstance.__blankWidgetHeight + 'px'
		}
	};
	
	$.extend(ARC.chartStyles, {
		/*EXPORT_MENU_: 'chart_exportBtn_dash',
		PRINT_MENU_: 'chart_printBtn_dash',
		EMAIL_MENU_: 'chart_emailBtn_dash',*/
		CLOSE_MENU_: 'chart_closeBtn_dash',
		CONFIG_MENU_: 'chart_configBtn_dash'
	});
	
	$.extend(AhDashboard, {
		Dashboard: Dashboard,
		DashboardGroup: DashboardGroup,
		product: 'AhDashboard',
		version: '0.0.1'
	});
	
})(jQuery, _);