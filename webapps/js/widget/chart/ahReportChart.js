/**
 * Encapsulate Highcharts to form a customized widget
 * @author wx
 */

(function($) {
	var win = window,
		HC = win.Highcharts,
		UNDEFINED;
	
	win.AhReportChart = {};
	// set global or lang settings for Highcharts here
	HC.setOptions({
		global : {
			useUTC: true
		}
	});
	
	function pick() {
		var args = arguments,
			i,
			arg,
			length = args.length;
		for (i = 0; i < length; i++) {
			arg = args[i];
			if (typeof arg !== 'undefined' && arg !== null) {
				return arg;
			}
		}
	}
	function pFloat(value) {
		if (value) {
			return parseInt(value);
		}
		return 0;
	}
	function isUndefined(obj, defValue) {
		if (obj !== UNDEFINED) return false;
		if (defValue) {
			return defValue;
		} else {
			return true;
		}
	}
	function isString(s) {
		return typeof s === 'string';
	}
	function isObject(obj) {
		return typeof obj === 'object';
	}
	function isArray(obj) {
		return Object.prototype.toString.call(obj) === '[object Array]';
	}
	function $el() {
		if (arguments.length < 1) {
			return null;
		}
		var elId = '#';
		for (var i = 0; i < arguments.length; i++) {
			elId += arguments[i];
		}
		return $(elId);
	}
	function each(arrays, func) {
		if (arrays && func && arrays.length > 0) {
			for (var i = 0; i < arrays.length; i++) {
				func(arrays[i], i);
			}
		}
	}
	function removeArrayItem(arrays, item) {
		if (isUndefined(arrays)
				|| arrays === null
				|| arrays.length < 1) {
			return false;
		}
		for(var i = 0; i < arrays.length; i++) {
	        if (arrays[i] == item) {
	        	arrays.splice(i, 1);
	            break;
	        }
	    }
		
		return true;
	}
	function containsArrayItem(arrays, item) {
		if (isUndefined(arrays)
				|| arrays === null
				|| arrays.length < 1) {
			return false;
		}
		for(var i = 0; i < arrays.length; i++) {
	        if (arrays[i] == item) {
	        	return true;
	        }
	    }
		
		return false;
	}
	function destroyObjectProperties(obj) {
		if (obj) {
			var key;
			for (key in obj) {
				if (obj[key] && obj[key].destroy) {
					obj[key].destroy();
				}
				delete obj[key];
			}
		}
	}
	function consoleLog(message) {
		if (win.console && message) {
			console.log(message);
		}
	}
	function stopEventBubble(e) {
		if (e && e.stopPropagation) {
			// for not IE
			e.stopPropagation();
		} else {
			// for IE
			if (win.event){
				win.event.cancelBubble = true;
			}
		}
	}
	function replaceSpecialSymbol(value) {
		if (value && isString(value)) {
			return value.replace(/\\/g, "\\\\");
		}
		return value;
	}
	function escapeHtmlTag(htmlStr) {
		if (!htmlStr || !isString(htmlStr)) {
			return htmlStr;
		}
		var tagsToReplace = {
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;'
		};
		var result = htmlStr.replace(/[&<>]/g, function(tag) {
			return tagsToReplace[tag] || [tag];
		});
		if (result) {
			result = result.replace(/&lt;br&gt;|&lt;br\/&gt;/g, "<br/>");
		}
		return result;
	}
	
	var getPageWH = function() {
		var pageWidth = window.innerWidth,
			pageHeight = window.innerHeight;
		
		if (typeof pageWidth != 'number') {
			if (document.compatMode == 'CSS1Compat') {
				pageWidth = document.documentElement.clientWidth;
				pageHeight = document.documentElement.clientHeight;
			} else {
				pageWidth = document.body.clientWidth;
				pageHeight = document.body.clientHeight;
			}
		}
		
		return {
			width: pageWidth,
			height: pageHeight
		}
	};
	
	var Events = function() {
		var regEvents = {};
		
		return {
			register: function(type, operation, instance) {
				if (!type
						||!operation
						||!instance
						||!instance.uniqueID
						||!$.isFunction(instance.dealNotify)) {
					return;
				}
				if (regEvents[type]) {
					if (regEvents[type][operation]) {
						var blnContained = false;
						var arrayTmp = regEvents[type][operation];
						for (var i = 0; i < arrayTmp.length; i++) {
							if (arrayTmp[i].uniqueID === instance.uniqueID) {
								blnContained = true;
								break;
							}
						}
						if (blnContained === false) {
							regEvents[type][operation].push(instance);
						}
					} else {
						regEvents[type][operation] = [];
						regEvents[type][operation].push(instance);
					}					
				} else {
					regEvents[type] = {};
					regEvents[type][operation] = [];
					regEvents[type][operation].push(instance);
				}
			},
			remove: function(type, operation, instance) {
				if (regEvents[type]
						&& regEvents[type][operation]) {
					var foundPos = -1;
					var arrayTmp = regEvents[type][operation];
					var lengthTmp = arrayTmp.length;
					for (var i = 0; i < lengthTmp; i++) {
						if (arrayTmp[i].uniqueID === instance.uniqueID) {
							foundPos = i;
							break;
						}
					}
					if (foundPos >= 0) {
						arrayTmp.splice(foundPos, 1);
					}
				}
			},
			notifyAll: function(type, operation, notifier, callback) {
				if (!type
						||!operation) {
					return;
				}
				if (regEvents[type]
						&& regEvents[type][operation]) {
					var arrayTmp = regEvents[type][operation];
					for (var i = 0; i < arrayTmp.length; i++) {
						arrayTmp[i].dealNotify(type, operation, notifier);
					}
				}
				if (callback) {
					callback();
				}
			},
			init: function() {
				regEvents = {};
			},
			clear: function() {
				regEvents = {};
			}
		}
	};
	Events = new Events();
	
	var renderedCharts = function() {
		var rCharts = {};
		
		return {
			add: function(id, chart) {
				if (!id
						|| !chart) {
					return;
				}
				rCharts[id] = chart;
			},
			get: function(id) {
				if (rCharts[id]) {
					return rCharts[id];
				}
				return null;
			},
			remove: function(id) {
				if (rCharts[id]) {
					delete rCharts[id];
				}
			},
			getAll: function() {
				return rCharts;
			},
			init: function() {
				rCharts = {};
			},
			clear: function() {
				rCharts = {};
			}
		}
	};
	var RenderedCharts = new renderedCharts();
	var _curChartIdMarkEl = 'current_chart_identifier';
	var getCurrentPopUpWinChart = function() {
		if (currentPopUpChartId !== '') {
			return AhReportChart.RenderedCharts.get(currentPopUpChartId);
		}
		return null;
	};
	var currentPopUpChartId = '';
	
	var getCertainTypeOfAttributeFunc = function(objArg, attrArg, preferMarkArg) {
		var tempFunc;
		if (preferMarkArg && objArg[preferMarkArg]) {
			tempFunc = objArg[preferMarkArg][attrArg];
		}
		if (isUndefined(tempFunc)){
			for (var key in objArg) {
				if (objArg[key]) {
					tempFunc = objArg[key][attrArg];
					break;
				}
			}
		}
		return tempFunc;
	};
	var localTzOffsetValue = new Date().getTimezoneOffset()*60000;
	
	var Chart = function(options, callback) {
		var self = this;
		self.hcChart = null;
		self.callNextData = pick(options.repeat, false);
		self.operation = pick(options.operation, 'getChartData');
		if(options.expert && options.expert.chart) {
			self.container = pick(options.expert.chart.container, null);
		}
		self.container = pick(options.container, self.container, null);
		self.uniqueID = self.container;
		self.$container = $el(self.container);
		self.reportUrl = pick(options.url, 'networkUsageReport.action');
		self.reportId = options.reportId;
		self.periodType = pick(options.periodType, periodTypeEnum.NOT_DEFINED);
		self.interval = pick(options.interval, 600000);
		self.isDatetimeType = false;
		if (options.xAxisType === 'datetime') {
			self.isDatetimeType = true;
		}
		self.rendered = false;
		self.oriSeriesData = {};
		self.legendData = {};
		self.preNoData = false;
		self.printType = pick(options.printType, 1);
		self.hasRenderedWithData = false;
		self.AREA_SECTIONS = {
				ANCHOR_NAME: self.container + '_anchor',
				TITLE_CONTAINER: self.container + CONTAINER_TOP_TITLE_OP,
				MAIN_TITLE: self.container + CONTAINER_TOP_TITLE_OP + 'Title',
				SUB_TITLE: self.container + CONTAINER_TOP_TITLE_OP + 'SubTitle',
				CHART_CTL: self.container + CONTAINER_CHART_CONTENT + 'Ctls',
				TIP_CONTENT: self.container + 'Tip',
				CUSTOM_CONTENT: self.container + 'Custom',
				CHART_CONTENT: self.container + CONTAINER_CHART_CONTENT,
				SUMMARY: self.container + CONTAINER_BOTTOM_SUMMARY,
				WORK_CONTENT: self.container + 'WorkC',
				HELPER_CONTENT: self.container + 'HelperC',
				NODATA_CONTAINER: self.container + 'NoDataC',
				TABLE_CONTENT_CONTAINER: self.container + 'TableCC',
				MAIN_CONTENT_TIP_INFO: self.container + 'WorkC_content_tip'
		};
		var $noDataContainerEl;
		self.noDataContainer = {
			show: function(text) {
				preDataShownContainer = getPreDataShownContainer();
				$el(self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).hide();
				$el(self.AREA_SECTIONS.CHART_CONTENT).hide();
				
				if (!text) {
					text = "No Data";
				}
				$el(self.AREA_SECTIONS.NODATA_CONTAINER + " > div").html(text);
				
				if (!$noDataContainerEl) {
					$noDataContainerEl = $el(self.AREA_SECTIONS.NODATA_CONTAINER);
				}
				$noDataContainerEl.show();
			},
			hide: function() {
				if (!$noDataContainerEl) {
					$noDataContainerEl = $el(self.AREA_SECTIONS.NODATA_CONTAINER);
				}
				$noDataContainerEl.hide();
				if (preDataShownContainer) {
					$el(preDataShownContainer).show();
				}
			}
		};
		self.getCurrentShownContainer = function() {
			var container = getBeforeMainContentTipDiv();
			if (!container) {
				container = self.AREA_SECTIONS.CHART_CONTENT;
			}
			return container;
		};
		var $mainContentTipEl;
		var beforeMainContentTipDiv;
		var mainContentContainers = [self.AREA_SECTIONS.NODATA_CONTAINER,self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER,self.AREA_SECTIONS.CHART_CONTENT];
		var getBeforeMainContentTipDiv = function() {
			beforeMainContentTipDiv = "";
			for (var i = 0; i < mainContentContainers.length; i++) {
				if ($el(mainContentContainers[i] + " :visible").length > 0) {
					beforeMainContentTipDiv = mainContentContainers[i];
					break;
				}
			}
			return beforeMainContentTipDiv;
		};
		self.mainContentTip = {
			show: function(text) {
				if (!self.mainContentTip.isShown()) {
					beforeMainContentTipDiv = getBeforeMainContentTipDiv();
				}
				$el(self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).hide();
				$el(self.AREA_SECTIONS.CHART_CONTENT).hide();
				$el(self.AREA_SECTIONS.NODATA_CONTAINER).hide();
				
				if (!text) {
					text = "";
				}
				$el(self.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO).html("<div style='font-size:14px;font-weight:bold;'>" + text + "</div>");
				
				if (!$mainContentTipEl) {
					$mainContentTipEl = $el(self.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO);
				}
				$mainContentTipEl.show();
			},
			hide: function(callback) {
				if (!$mainContentTipEl) {
					$mainContentTipEl = $el(self.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO);
				}
				$mainContentTipEl.hide();
				if (beforeMainContentTipDiv) {
					$el(beforeMainContentTipDiv).show();
				}
				if (callback) {
					callback.apply(self);
				}
			},
			isShown: function() {
				return $el(self.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO + " :visible").length > 0;
			}
		};
		self.showTableContent = function() {
			$el(self.AREA_SECTIONS.CHART_CONTENT).hide();
			$el(self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).show();
		};
		self.showChartContent = function() {
			$el(self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).hide();
			$el(self.AREA_SECTIONS.CHART_CONTENT).show();
		};
		self.STATUS_MODE = {
			VIEW: 'view',
			EDIT: 'edit'
		};
		self.statusChanged = function(status) {
			if (controlButtons) {
				for (var key in controlButtons) {
					var ctlBtnTmp = controlButtons[key];
					if (ctlBtnTmp && ctlBtnTmp.onStatusChange) {
						ctlBtnTmp.onStatusChange(oldStatus, status);
					}
				}
			}
			if (self.__onStatusChanged) {
				self.__onStatusChanged.apply(self, [oldStatus, status]);
			}
			oldStatus = status;
			if (self.isInViewStatus()) {
				self.mainContentTip.hide();
				if (prePaused === false) {
					self.doStart();
				}
			} else if (self.isInEditStatus()) {
				prePaused = isPaused();
				if (prePaused === false) {
					self.doPause();
				}
			}
		};
		self.isInEditStatus = function() {
			return oldStatus == self.STATUS_MODE.EDIT;
		};
		self.isInViewStatus = function() {
			return oldStatus == self.STATUS_MODE.VIEW;
		};
		self.getCurrentStatus = function() {
			return oldStatus;
		};
		var prePaused = false;
		self.toggleContents = function(cType) {
			if (cType === 'main') {
				chartShownOrHide = true;
				$el(self.AREA_SECTIONS.WORK_CONTENT).show();
				$el(self.AREA_SECTIONS.HELPER_CONTENT).hide();
				/*if (prePaused === false) {
					self.doStart();
				}*/
			} else if (cType === 'helper') {
				chartShownOrHide = false;
				$el(self.AREA_SECTIONS.WORK_CONTENT).hide();
				$el(self.AREA_SECTIONS.HELPER_CONTENT).height($el(self.AREA_SECTIONS.WORK_CONTENT).height()).show();
				/*prePaused = isPaused();
				if (prePaused === false) {
					self.doPause();
				}*/
			}
		};
		var chartShownOrHide = true;
		self.isChartShown = function() {
			return chartShownOrHide;
		};
		self.getOptions = function() {
			return options;
		};
		self.genReportAttrs = function(attrs, tmpPrefix) {
			if(isUndefined(tmpPrefix)) {
				tmpPrefix = objPrefix;
			}
			if (attrs) {
				var myArgs = {};
				for (var key in attrs) {
					myArgs[objAttributePath(key, tmpPrefix)] = attrs[key];
				}
				return myArgs;
			}
			return {};
		};
		self.getMainMenuId = function(menuId) {
			return self.container+'_'+menuId;
		};
		self.getSubMenuId = function(menuId) {
			return self.container+'_'+menuId + SUB_MENU_DIV_POSTFIX;
		};
		self.additionalArgs = {
			add: function() {
				var args = arguments,
					length = args.length;
				if (length < 1) {
					return;
				}
				var arg0 = args[0],
					arg1,
					arg2;
				if (length > 1) { arg1 = args[1]; }
				if (length > 2) { arg2 = args[2]; }
				var tmpPrefix='';
				if (arg2) {
					if (isString(arg0)) {
						tmpPrefix = arg0;
					} else if (arg0 === true) {
						tmpPrefix = objPrefix;
					}
					var tmpArgs = {};
					tmpArgs[arg1] = arg2;
					$.extend(additionalArgsObj, self.genReportAttrs(tmpArgs, tmpPrefix));
				} else if (arg1) {
					if (isString(arg0)) {
						if (isString(arg1)) {
							var tmpArgs = {};
							tmpArgs[arg0] = arg1;
							$.extend(additionalArgsObj, self.genReportAttrs(tmpArgs));
						} else if (isObject(arg1)) {
							tmpPrefix = arg0;
						}
					} else if (arg0 === true) {
						tmpPrefix = objPrefix;
					}
					if (isObject(arg1)) {
						$.extend(additionalArgsObj, self.genReportAttrs(arg1, tmpPrefix));
					}
				} else if (arg0) {
					tmpPrefix = objPrefix;
					if (isObject(arg0)) {
						$.extend(additionalArgsObj, self.genReportAttrs(arg0, tmpPrefix));
					}
				}
			},
			remove: function() {
				var args = arguments,
					length = args.length;
				if (length < 1) {
					return;
				}
				var arg0 = args[0],
					arg1;
				if (length >1) {
					arg1 = args[1];
				}
				var tmpPrefix = '';
				if (arg1) {
					if (isString(arg0)) {
						tmpPrefix = arg0;
					} else if (arg0 === true) {
						tmpPrefix = objPrefix;
					}
					if (isString(arg1)) {
						if (additionalArgsObj[objAttributePath(arg1, tmpPrefix)]) {
							delete additionalArgsObj[objAttributePath(arg1, tmpPrefix)]; 
						}
					} else if (isArray(arg1)) {
						for (var i = 0; i < arg1.length; i++) {
							if (additionalArgsObj[objAttributePath(arg1[i], tmpPrefix)]) {
								delete additionalArgsObj[objAttributePath(arg1[i], tmpPrefix)]; 
							}
						}
					}
				} else if (arg0) {
					tmpPrefix = objPrefix;
					if (isString(arg0)) {
						if (additionalArgsObj[objAttributePath(arg0, tmpPrefix)]) {
							delete additionalArgsObj[objAttributePath(arg0, tmpPrefix)]; 
						}
					} else if (isArray(arg0)) {
						for (var i = 0; i < arg0.length; i++) {
							if (additionalArgsObj[objAttributePath(arg0[i], tmpPrefix)]) {
								delete additionalArgsObj[objAttributePath(arg0[i], tmpPrefix)]; 
							}
						}
					}
				}
			},
			get: function() {
				var args = arguments,
				length = args.length;
				if (length < 1) {
					return UNDEFINED;
				}
				var arg0 = args[0],
					arg1;
				if (length >1) {
					arg1 = args[1];
				}
				var tmpPrefix = '';
				if (arg1) {
					if (isString(arg0)) {
						tmpPrefix = arg0;
					} else if (arg0 === true) {
						tmpPrefix = objPrefix;
					}
					if (isString(arg1)) {
						if (additionalArgsObj[objAttributePath(arg1, tmpPrefix)]) {
							return additionalArgsObj[objAttributePath(arg1, tmpPrefix)]; 
						}
					}
				} else if (arg0) {
					tmpPrefix = objPrefix;
					if (isString(arg0)) {
						if (additionalArgsObj[objAttributePath(arg0, tmpPrefix)]) {
							return additionalArgsObj[objAttributePath(arg0, tmpPrefix)]; 
						}
					}
				}
				return UNDEFINED;
			},
			clear: function() {
				additionalArgsObj = {};
			},
			init: function() {
				additionalArgsObj = {};
			},
			clone: function() {
				return additionalArgsObj;
			}
		};
		var isCtlBtnSupportRenderType = function(ctlRenderTypes, curRenderType) {
			if (ctlRenderTypes.indexOf("+all") >= 0) {
				return true;
			}
			if (ctlRenderTypes.indexOf("+"+curRenderType) >= 0) {
				return true;
			}
			if (ctlRenderTypes.indexOf("-"+curRenderType) >= 0) {
				return false;
			}
			return false;
		};
		self.ctlButtons = {
			add: function(options, blnHead) {
				if (self.getOptions().displayCtl && self.getOptions().displayCtl.blnNoControlBtn) {
					return;
				}
				if (!options.menuId 
						|| $.trim(options.menuId) === ''
						|| !options.constructFunc
						|| !$.isFunction(options.constructFunc)) {
					return;
				}
				
				var ctlRenderTypes = pick(options.supportType, "+all");
				if (!isCtlBtnSupportRenderType(ctlRenderTypes, renderType)) {
					return;
				}
				
				if(isUndefined(blnHead)) {
					blnHead = false;
				}
				
				var menuId = options.menuId;
				controlButtons[menuId] = new ctlButtonBuilder(self, options);
				if (blnHead === true) {
					controlButtonsIndex.unshift(menuId);
				} else {
					controlButtonsIndex.push(menuId);
				}
			},
			addCustomAttribute: function(menuId, name, attr) {
				if (!menuId || !name || !attr) return;
				var btnTmp = controlButtons[menuId];
				if (btnTmp && !btnTmp.isCtlBuilder) {
					btnTmp[name] = attr;
				}
			},
			addAfterRenderedCallback: function(menuId, func) {
				if (!menuId || !func) return;
				if (!controlBtnAfterRenderedFunc[menuId]) {
					controlBtnAfterRenderedFunc[menuId] = [];
				}
				controlBtnAfterRenderedFunc[menuId].push(func);
			},
			get: function(menuId) {
				if (!menuId
						|| $.trim(menuId) === '') {
					return null;
				}
				return controlButtons[menuId];
			},
			remove: function() {
				var args = arguments,
					length = args.length;
				if (length < 1) return;
				if (length > 0) {
					var arg0 = args[0];
					if (isString(arg0)) {
						for (var i = 0; i < length; i++) {
							if (controlButtons[args[i]]) {
								delete controlButtons[args[i]];
								removeArrayItem(controlButtonsIndex, args[i]);
							}
						}
					} else if (arg0 === true) {
						for (var i = 1; i < length; i++) {
							var tmpBtn = controlButtons[args[i]];
							if (!tmpBtn) continue;
							if (tmpBtn.remove && $.isFunction(tmpBtn.remove)) {
								tmpBtn.remove();
							}
							delete controlButtons[args[i]];
							removeArrayItem(controlButtonsIndex, args[i]);
						}
					}
				}
			},
			init: function() {
				controlButtons = {};
				controlButtonsIndex = [];
				controlBtnAfterRenderedFunc = {};
			}
		};
		self.getADefinedColor = function(idx) {
			var len = defColors.length;
			if (idx >= len) {
				idx = idx + 1;
			} else if (idx < 0) {
				idx = 0;
			}
			return defColors[idx%len];
		};
		self.getTitleText = function() {
			return $el(self.AREA_SECTIONS.MAIN_TITLE).text();
		};
		self.getSubTitleText = function() {
			return $el(self.AREA_SECTIONS.SUB_TITLE).text();
		};
		self.axisTitles = {
			xAxis: '',
			yAxis: ''
		};
		self.timeOffset = 0;
		self.getDateWithTimeZone = function(time) {
			return new Date(time + self.timeOffset);
		};
		self.getDateStringWithTimeZone = function(time) {
			//var date = self.getDateWithTimeZone(time);
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
		self.tips = {
			clear: function() {
				$el(self.AREA_SECTIONS.TIP_CONTENT).html("");
			},
			addInfo: function(text, tipId, hidetime) {
				addTipMessages('Info', text, tipId, hidetime);
				showTipMessages(tipId);
			},
			addWarning: function(text, tipId, hidetime) {
				addTipMessages('Warn', text, tipId, hidetime);
				showTipMessages(tipId);
			},
			addError: function(text, tipId, hidetime) {
				addTipMessages('Error', text, tipId, hidetime);
				showTipMessages(tipId);
			},
			addProcessing: function(text, tipId) {
				addTipMessages('Processing', text, tipId, -1);
				showTipMessages(tipId);
			}
		};
		self.popups = {
			add: function(divName, titleText) {
				var divNameTmp = getCertainPopDiv(divName);
				if (containsArrayItem(popupDivs, divNameTmp) === false) {
					popupDivs.push(divNameTmp);
					var $divHdTmp = $("<div></div>")
										.addClass("hd")
										.text(titleText);
					var $divBdTmp = $("<div></div>")
										.addClass("bd");
					var $divBdContentTmp = $("<div></div>")
										.attr("id", divNameTmp+"_divc");
					var $divTmp = $("<div></div>")
									.attr("id", divNameTmp)
									.append($divHdTmp)
									.append($divBdTmp.append($divBdContentTmp));
					self.$container.append($divTmp);
				}
				return divNameTmp;
			},
			getContentContainer: function(divName) {
				return divName + "_divc";
			},
			setTitle: function(divName, title) {
				$el(divName + " div.hd").text(title);
			},
			contains: function(divName) {
				return containsArrayItem(popupDivs, divName);
			},
			remove: function(divName) {
				removeArrayItem(popupDivs, divName);
			},
			moveYUIPanelTo: function(blnInChart, popWin, top, left) {
				currentPopUpChartId = self.container;
				if (blnInChart === false) return;
				var $chartElContainer = $el(self.AREA_SECTIONS.WORK_CONTENT);
				var iWidthTmp = $chartElContainer.width();
				var iHeightTmp = $chartElContainer.height();
				var iPopWinWidth = popWin.cfg.getProperty("width");
				if (!iPopWinWidth) {
					iPopWinWidth = 0;
				} else {
					iPopWinWidth = iPopWinWidth.replace(/px/, "");
				}
				var iPopWinHeight = popWin.cfg.getProperty("height");
				if (!iPopWinHeight) {
					iPopWinHeight = 0;
				} else {
					iPopWinHeight = iPopWinHeight.replace(/px/, "");
				}
				var leftTmp;
				var topTmp;
				if (!left || left === 'auto') {
					leftTmp = (iWidthTmp - iPopWinWidth)/2;
				} else {
					leftTmp = left;
				}
				if (!top || top === 'auto') {
					topTmp = (iHeightTmp - iPopWinHeight)/2;
				} else {
					topTmp = top;
				}
				popWin.cfg.setProperty("context", [self.AREA_SECTIONS.WORK_CONTENT, "tl", "tl", null, [leftTmp, topTmp]]);
			}
		};
		self.destroyChart = function() {
			if (self.hcChart && self.hcChart.destroy) {
				self.hcChart.destroy();
			}
			$el(self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).empty();
		};
		self.destroy = function(blnRmContainer, callback) {
			var containerTmp = self.container;
			if (controlButtons) {
				for (var menuId in controlButtons) {
					if (controlButtons[menuId] && controlButtons[menuId].destroy) {
						controlButtons[menuId].destroy(blnRmContainer);
					}
				}
			}
			self.destroyChart();
			if (arguments.length > 0 && blnRmContainer === false) {
				self.$container.empty();
			} else {
				self.$container.remove();
			}
			if (callback) {
				callback(containerTmp);
			}
			AhReportChart.RenderedCharts.remove(containerTmp);
			delete self;
		};
		self.cloneTo = function(descOptionsArg, callback) {
			if (!descOptionsArg || !descOptionsArg.container) {
				return;
			}
			var descOptions = $.extend(true, {}, options, descOptionsArg);
			var chartResult = new AhReportChart.Chart(descOptions, callback);
			chartResult.requestDataFunc = function() {
				chartResult.dataReceivedCallBack(lastData);
			};
			chartResult.additionalArgs.add(self.additionalArgs.clone());
			return chartResult;
		};

		var oldStatus;
		var popupDivs = [];
		var getCertainPopDiv = function(divName) {
			return self.container + '_pop_' + divName;
		};
		var tipTimerIds = {};
		var defTipTimerLastTime = 8000;
		var clearTipMessage = function(tipId) {
			var tipIdTmp = 'all';
			if (tipId
					&& tipId !== '') {
				tipIdTmp = tipId;
			}
			if (allTipMessages[tipIdTmp]) {
				delete allTipMessages[tipIdTmp];
			}
			if (tipTimerIds[tipIdTmp]) {
				clearTimeout(tipTimerIds[tipIdTmp]);
				delete tipTimerIds[tipIdTmp];
			}
			showTipMessages(tipIdTmp);
		};
		var showTipMessages = function(tipId) {
			var htmls = "";
			var blnFirst = true;
			var tipIdTmp;
			if (tipId) {
				if (tipId === '') {
					tipIdTmp = 'all';
				} else {
					tipIdTmp = tipId;
				}
			}
			for (var key in allTipMessages) {
				if (tipIdTmp
						&& key !== tipIdTmp) {
					continue;
				}
				if (tipTimerIds[key]) {
					clearTimeout(tipTimerIds[key]);
				}
				if (allTipMessages[key].texts.length === 0
						&& allTipMessages[key].texts[0] === '') {
					break;
				}
				var clsType = 'rpSuccInfo';
				var messageType = allTipMessages[key].type;
				if (messageType) {
					if (messageType === 'Warn') {
						clsType = 'rpWarnInfo';
					} else if (messageType === 'Error') {
						clsType = 'rpErrInfo';
					}
				}
				for (var i = 0; i < allTipMessages[key].texts.length; i++) {
					if (blnFirst) {
						if ('Processing' === messageType) {
							htmls += '<div class="reportProcessingImage"></div>';
						}
						htmls += '<span class="'+clsType+'">'+allTipMessages[key].texts[i]+'</span>';
						blnFirst = false;
					} else {
						htmls += '<br>';
						if ('Processing' === messageType) {
							htmls += '<div class="reportProcessingImage"></div>';
						}
						htmls += '<span class="'+clsType+'">'+allTipMessages[key].texts[i]+'</span>';
					}
				}
				if (allTipMessages[key].hidetime
						&& allTipMessages[key].hidetime > 0) {
					tipTimerIds[key] = setTimeout(function(){clearTipMessage(key);}, allTipMessages[key].hidetime);
				}
			}
			showMessageType1(htmls, self.AREA_SECTIONS.CHART_CONTENT);
		};
		var showMessageType1 = function(htmls) {
			$el(self.AREA_SECTIONS.TIP_CONTENT).html(htmls);
		};
		var showMessageType2 = function(htmls, divContainer) {
			var pos = $el(divContainer).position();
			var $tipContainerTmp = $el(self.AREA_SECTIONS.TIP_CONTENT);
			$tipContainerTmp.width($el(divContainer).width());
			$tipContainerTmp.css({"top": pos.top, "left": 0});
			$tipContainerTmp.html(htmls);
		};
		var allTipMessages = {};
		var addTipMessages = function(type, text, tipId, hidetime) {
			var tipIdTmp = 'all';
			var typeTmp = 'Info';
			var hidetimeTmp = defTipTimerLastTime;
			if (tipId
					&& tipId !== '') {
				tipIdTmp = tipId;
			}
			if (type) {
				typeTmp = type;
			}
			if (hidetime) {
				hidetimeTmp = hidetime;
			}
			if (!allTipMessages[tipIdTmp]) {
				allTipMessages[tipIdTmp] = {};
			}
			allTipMessages[tipIdTmp].type = typeTmp;
			if (isArray(text)) {
				allTipMessages[tipIdTmp].texts = text;
			} else {
				allTipMessages[tipIdTmp].texts = [];
				allTipMessages[tipIdTmp].texts.push(text);
			}
			if (hidetimeTmp > 0) {
				allTipMessages[tipIdTmp].hidetime = hidetimeTmp;
			}
			if (tipTimerIds[tipIdTmp]) {
				clearTimeout(tipTimerIds[tipIdTmp]);
			}
		}
		
		var lastRefreshTime;
		self.setLastRefreshTime = function(time) {
			lastRefreshTime = time;
		};
		self.getLastRefreshTimeWithTimeZone = function() {
			if (!lastRefreshTime) {
				return "";
			}
			return self.getDateStringWithTimeZone(lastRefreshTime, localTzOffsetValue);
		};
		self.getLastRefreshTime = function() {
			return lastRefreshTime;
		};
		
		self.resizeChart = function() {
			if (self.hcChart) {
				self.hcChart.setSize(self.$container.width(), options.chartHeight);
			}
		};
		
		var renderType = pick(options.renderType, 'chart');
		self.isRenderedAsChart = function() {
			return renderType === 'chart';
		};
		self.isRenderedAsHTML = function() {
			return renderType === 'html';
		};
		self.isRenderedAsTable = function() {
			return renderType === 'table' || renderType === 'list';
		};
		var getPreDataShownContainer = function() {
			if (self.isRenderedAsTable()) {
				return self.AREA_SECTIONS.TABLE_CONTENT_CONTAINER;
			} else {
				return self.AREA_SECTIONS.CHART_CONTENT;
			}
		};
		var preDataShownContainer = getPreDataShownContainer();
		self.ctlButtonGrps = {
			get: function(ctlSectionDiv, myChart, options) {
				var grpName = ctlSectionDiv + '_grp' + options.groupby;
				if (!(grpName in this._curGroups)) {
					var $grpElContainer = $("<div></div>").addClass("dropdown clearfix");
					var $grpElBtn = $('<button></button>')
									.attr({
										title: pick(options.groupTip, '')
									})
									.addClass('btn my-ahchart-menu-button')
									.html("<i class='bticon-settings'></i><span class='caret'></span>");
					var $menuContainer = $("<ul></ul>").addClass("dropdown-menu");
					$grpElContainer.append($grpElBtn);
					$grpElContainer.append($menuContainer);
					$el(ctlSectionDiv).append($grpElContainer);
					this._curGroups[grpName] = {
						'$container': $grpElContainer,
						'$menu': $menuContainer
					};
				}
				return this._curGroups[grpName];
			},
			genSingleMenu: function(myChart, options) {
				var $menuLink = $("<a></a>")
								.attr({
									tabindex: -1,
									href: 'javascript: void(0);'
								})
								.addClass(pick(options.style, ''))
								.html(options.menuName || options.title || 'Menu Name');
				var $menuItem = $("<li></li>")
								.attr({
									id: myChart.getMainMenuId(options.menuId),
									title: pick(options.tip, '')	
									});
				$menuItem.append($menuLink);
				return $menuItem;
			},
			iterate: function(func) {
				for (var key in this._curGroups) {
					var grpNames = key.split('_grp');
					func.call(this._curGroups[key], grpNames[0], grpNames[1]);
				}
			},
			_curGroups: {}
		};
		var menuAsUl = false;
		var ctlButtonBuilder = function(myChart, options) {
			if (self.__additionalOptions.ctlButtons && options.menuId in self.__additionalOptions.ctlButtons) {
				options = $.extend(true, {}, options, self.__additionalOptions.ctlButtons[options.menuId]);
			}
			collectCtlButtonsInfo(options);
			var builder = this;
			builder.options = options;
			builder.alwaysRender = pick(options.alwaysRender, false);
			builder.isCtlBuilder = true;
			builder.render = function() {
				var menuId = options.menuId;
				var hasSubMenu = pick(options.subMenu, false);
				var ctlSectionDiv = myChart.AREA_SECTIONS.CHART_CTL;
				if (options.ctlContainer) {
					if (options.ctlContainer.indexOf('#') >= 0) {
						ctlSectionDiv = options.ctlContainer.substr(1);
					} else {
						ctlSectionDiv = myChart.AREA_SECTIONS[options.ctlContainer];
					}
				}
				var $ctlSectionDiv;
				if (ctlButtonsInfo.grpBtnCount > 1 && 'groupby' in options) {
					menuAsUl = true;
				}
				
				if (menuAsUl) {
					$ctlSectionDiv = self.ctlButtonGrps.get(ctlSectionDiv, myChart, options).$menu;
					$ctlSectionDiv.append(self.ctlButtonGrps.genSingleMenu(myChart, options));
				} else {
					var cssStyle = pick(options.style1, options.style, '');
					$ctlSectionDiv = $('#'+ctlSectionDiv);
					$ctlSectionDiv.append($("<div></div>")
						.attr({
							id: myChart.getMainMenuId(menuId),
							title: pick(options.tip, '')	
							})
						.addClass(cssStyle));
					if (hasSubMenu === true) {
						$('body').append($("<div></div>").attr({id: myChart.getSubMenuId(menuId)}).addClass("yuimenu"));
					}
				}
				
				return new options.constructFunc(myChart, menuId, pick(options.menuOptions, {}));
			};
		};
		
		var startTime = pick(options.startTime, 0),
			endTime = pick(options.endTime, 0),
			fullWidget = pick(options.fullWidget, true),
			stackGroup = pick(options.stackGroup, 'no'),
			events = pick(options.events, {}),
			objPrefix = pick(options.objPrefix, 'rp'),
			iwidth = pick(options.width, 1), // width of div container
			chartWidth = pick(options.chartWidth, null), // width of chart
			contentHeight = pick(options.chartHeight, '100%'); // height of div which contains chart
		
		var blnOriZoomType = true;
		var optZoomType = '';
		if (options.zoomType) {
			blnOriZoomType = false;
			if (options.zoomType !== 'no') {
				optZoomType = options.zoomType;
			} else {
				optZoomType = '';
			}
		}
		
		var additionalArgsObj = {};
		var controlButtons = {};
		var controlButtonsIndex = [];
		var controlBtnAfterRenderedFunc = {};
		
		var ctlButtonsInfo = {};
		var collectCtlButtonsInfo = function(option) {
			if ('groupby' in option) {
				if (!('grpBtnCount' in ctlButtonsInfo)) {
					ctlButtonsInfo.grpBtnCount = 0;
				}
				ctlButtonsInfo.grpBtnCount += 1;
			}
		};
		var cwidth = iwidth + 'px';
		if (iwidth <= 1 && iwidth >= 0) {
			cwidth = iwidth*100 + '%';
		} else if (iwidth < 0) {
			cwidth = 'auto';
		}
		options.chartWidth = chartWidth;
		
		var timerId = null;
		var firstTimeCall = true;
		self.isFirstTimeDataCall = function() {
			return firstTimeCall;
		};
		self.showLoadingTip = function(container, blnShown) {
			showOrHideLoadingTip(container, blnShown);
		};
		
		var chartInited = false;
		var reqDescInfoAtFirst  = pick(options.reqDescInfoAtFirst, true);
		var paused = false;
		self.doPause = function() {
			paused = true;
		};
		self.doStart = function() {
			paused = false;
		};
		var isPaused = function() {
			return paused;
		};
		var previouStatus = {};
		self.doPauseWithStatus = function() {
			previouStatus.paused = paused;
			self.doPause();
		};
		self.doStartWithStatus = function() {
			if (previouStatus.paused) {
				self.doPause();
			} else {
				self.doStart();
			}
			previouStatus.paused = paused;
		};
		
		var objAttributePath = function(attr, tmpPrefix) {
			if (tmpPrefix && $.trim(tmpPrefix) != '') {
				return tmpPrefix+'.'+attr;
			}
			return attr;
		}
		
		var doShowLegend = function(point) {
			var name = point.name;
			var legendShown = splitNameToLines(name);
			if (self.legendData[name]) {
				for (var i = 0; i < self.legendData[name].length; i++) {
					legendShown += '<br><span style="font-size:11px; color:#BDBAAB; line-height: 1.1em;">' + self.legendData[name][i] + '</span>';
				}
			}
			return legendShown;
		};
		var doShowLegendPie = function(point) {
			var name = point.name;
			var dataTmp = point.series.options.myDataNode.oriData[point.x][1];
			var percentage = (dataTmp / (self.totalOriValue || 1) * 100).toFixed(2);
			if (percentage == 0) {
				percentage = 0.01;
			}
			var legendShown = percentage + '% ' + (percentage<10?'   ':'') + splitNameToLines(name);
			if (self.legendData[name]) {
				for (var i = 0; i < self.legendData[name].length; i++) {
					legendShown += '<br><span style="font-size:11px; color:#BDBAAB; line-height: 1.1em;">' + self.legendData[name][i] + '</span>';
				}
			}
			return legendShown;
		};
		var splitNameToLines = function(name) {
			if (name) {
				var count1 = 22, count2 = 28;
				var result = name;
				var len = name.length;
				if (len > count1) {
					result = name.substr(0, count1);
					len -= count1;
					if (len > 0 && name[count1] != " ") {
						result += "-";
					}
					var count = len/count2;
					if (count > 0) {
						for (var i = 0; i < count; i++) {
							result += "<br/>" + name.substr(i*count2+count1, count2);
							if ((i+1)*count2 < len && name[(i+1)*count2+count1] != " ") {
								result += "-";
							}
						}
					} else {
						result += "<br/>" + name.substr(count1, len);
					}
				}
				return result;
			}
			return "";
		};
		self.splitLegendName = splitNameToLines;
		self.legendTypes = {
			common: doShowLegend,
			pie: doShowLegendPie
		};
		
		var defTipFormatter = function(obj, ahChart) {
			var xName = obj.x;
			if (options
					&& options.xAxisType === 'datetime') {
				xName = new Date(obj.x);
			}
			return '<b>'+ escapeHtmlTag(xName) +'</b><br/>'+
				escapeHtmlTag(obj.series.name) +': '+ obj.y;
		};
		var tipFormatterFunc = pick(events.tipFormatter, defTipFormatter);
		
		var onXAxisSelection = function(event) {
			if (event.xAxis) {
				event.preventDefault();
				showOrHideLoadingMainTip(false);
				// then, call asyn request to get data
				doTempXAxisSelectionRequest(event.xAxis[0].min, event.xAxis[0].max);
			}
		};
		
		var overlayPositionHandlerId;
		var overlayAdjustPosition = function(tipContainer) {
			var $tipC = $el(tipContainer);
			if (!$tipC) {
				if (overlayPositionHandlerId) {
					clearTimeout(overlayPositionHandlerId);
				}
				return;
			}
			
			if ($tipC.hasClass("overlay-trigger")) {
				var $overlayTmp = $el(tipContainer, ">div.overlay");
				var offset1 = $tipC.offset();
				var offset2 = $overlayTmp.offset();
				if (offset1 && offset2) {
					if (Math.abs(offset1.left - offset2.left) >= 5
							|| Math.abs(offset1.top - offset2.top) >= 5) {
						$overlayTmp.offset(offset1);
					}
				}
				overlayPositionHandlerId = setTimeout(function(){
					overlayAdjustPosition(tipContainer);
				}, 100);
			} else {
				if (overlayPositionHandlerId) {
					clearTimeout(overlayPositionHandlerId);
				}
			}
		};
		var showOrHideLoadingTip = function(tipContainer, show) {
			var $tipC = $el(tipContainer);
			if (!$tipC) return;
			if (show) {
				// the overlay is shown now
				if ($tipC.hasClass("overlay-trigger")) {
					return;
				}
				if ($tipC) {
					$tipC.overlay({
						container: "#"+tipContainer,
						opacity: 0.25
					});
					var $overlayTmp = $el(tipContainer, ">div.overlay");
					var marginTop = $overlayTmp.height()/2;
					if (marginTop > 20) {
						marginTop = marginTop -20;
					}
					$overlayTmp.html("<div style='margin-top:"+marginTop+"px;"
										+ "margin-left: " + $overlayTmp.width()/5+"px;"
										+ "margin-right: " + $overlayTmp.width()/5+"px;"
										+ "'>"
										+ "<span class='reportProcessingImage'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>"
										+ "<span class='reportLoadingTipText'>Loading...</span></div>");
					overlayPositionHandlerId = setTimeout(function(){
						overlayAdjustPosition(tipContainer);
					}, 100);
					
					// avoid blank area in chrome, should call this time, not sure why
					if (!self.hasRenderedWithData) {
						$overlayTmp.css("position", "static");//.css("position", "absolute");
					} else {
						$overlayTmp.css("position", "absolute");
					}
				}
			} else {
				if ($tipC.hasClass("overlay-trigger")) {
					$tipC.removeClass("overlay-trigger");
					if ($el(tipContainer, ">div.overlay")) {
						$el(tipContainer, ">div.overlay").remove();
					}
				}
			}
		};
		
		var showOrHideLoadingMainTip = function(show) {
			showOrHideLoadingTip(self.AREA_SECTIONS.CHART_CONTENT, show);
		};
		
		var doTempXAxisSelectionRequest = function(min, max) {
			var myArgs = self.genReportAttrs({
				useScaleArea: true,
				scaleAreaStart: pFloat(min),
				scaleAreaEnd: pFloat(max)
			});
			options.customArgs = $.extend(true, {}, options.customArgs, myArgs);
			doDataRequest();
		};
		
		/**
		 * used to send data request with certain interval
		 * @returns
		 */
		var doSendRequestTimer = function() {
			self.clearRequestInterval();
			prepareChartInterval();
			timerId = setTimeout(function(){doDataRequestCalledByInterval()}, self.interval);
		};
		self.clearRequestInterval = function() {
			if(timerId) {
				clearTimeout(timerId);
			}
		};
		self.resetSendRequestTimer = function(intervalArg) {
			if (!intervalArg) {
				intervalArg = self.interval;
			}
			self.clearRequestInterval();
			prepareChartInterval();
			timerId = setTimeout(function(){doDataRequestCalledByInterval()}, intervalArg);
		};
		var prepareChartInterval = function() {
			self.interval = self.intervalStrategy.run();
		};
		var intervalStrategyObj = {type: "fixed", __my_logs: {curPos: 0, blnEnd: true}};
		self.intervalStrategy = {
			run: function() {
				var result = intervalStrategyObj.steadyInterval || self.interval;
				if (intervalStrategyObj.type === "iterate") {
					var myLogs = intervalStrategyObj.__my_logs;
					if (!myLogs.blnEnd && myLogs.curPos < myLogs.intervals.length) {
						if (myLogs.curPos == myLogs.intervals.length - 1) {
							myLogs.blnEnd = true;
						}
						result = myLogs.intervals[myLogs.curPos];
						myLogs.curPos = myLogs.curPos + 1;
					} else if (myLogs.curPos >= myLogs.intervals.length) {
						myLogs.blnEnd = true;
					}
				}
				return result;
			},
			setStrategy: function(strategy, blnForce) {
				if (!blnForce && intervalStrategyObj.__my_logs.curPos > 0) {
					return;
				}
				intervalStrategyObj = $.extend(true, {}, intervalStrategyObj, strategy||{});
				var intervals = intervalStrategyObj.intervals;
				if (intervals && intervals.length > 0) {
					intervalStrategyObj.__my_logs.intervals = intervals;
					intervalStrategyObj.__my_logs.blnEnd = false;
					intervalStrategyObj.__my_logs.curPos = 0;
				}
			},
			reset: function() {
				intervalStrategyObj.__my_logs.blnEnd = false;
				intervalStrategyObj.__my_logs.curPos = 0;
			},
			stop: function() {
				intervalStrategyObj.__my_logs.blnEnd = true;
				if (intervalStrategyObj.__my_logs.intervals) {
					intervalStrategyObj.__my_logs.curPos = intervalStrategyObj.__my_logs.intervals.length;
				} else {
					intervalStrategyObj.__my_logs.curPos = 0;
				}
			},
			backAStep: function() {
				if (!intervalStrategyObj.__my_logs.blnEnd && intervalStrategyObj.__my_logs.curPos > 0) {
					intervalStrategyObj.__my_logs.curPos = intervalStrategyObj.__my_logs.curPos - 1;
				}
			}
		};
		/**
		 * used to send request data to server to fetch report data
		 */
		var _doAtomDataRequest = function() {
			if (self.requestDataFunc == null) {
				return;
			}
			self.requestDataFunc();
		};
		var doDataRequest = function() {
			self.data_request_by_interval = false;
			_doAtomDataRequest();
		};
		self.data_request_by_interval = false;
		var doDataRequestCalledByInterval = function() {
			self.data_request_by_interval = true;
			_doAtomDataRequest();
		};
		var doSingleDataRequest = function(tmpRequestArgs) {
			if (!self.reportUrl) {
				return;
			}
			if (isPaused()) {
				self.intervalStrategy.backAStep();
				doSendRequestTimer();
				return;
			}
			if (events.beforeSendingRequest) {
				var blnGoThrough = events.beforeSendingRequest.apply(self, [self]);
				blnGoThrough = isUndefined(blnGoThrough, true);
				if (!blnGoThrough) {
					if (self.callNextData) {
						self.intervalStrategy.backAStep();
						doSendRequestTimer();
					}
					return;
				}
			}
			var requestArgs = {
					firstCall: firstTimeCall,
					operation: self.operation
			};
			var myArgs = self.genReportAttrs({
				id: self.reportId,
				startTime: startTime,
				endTime: endTime,
				periodType: self.periodType,
				blnReqDesc: reqDescInfoAtFirst,
				ignore: new Date().getTime()
			});
			$.extend(true, requestArgs, myArgs);
			if (options.customArgs) {
				$.extend(true, requestArgs, options.customArgs);
			}
			$.extend(true, requestArgs, additionalArgsObj);
			if (tmpRequestArgs) {
				$.extend(true, requestArgs, tmpRequestArgs);
			}
			$.post(self.reportUrl,
					requestArgs,
					dataRequestCallBack,
					"json");
		};
		self.requestDataFunc = doSingleDataRequest;
		
		self.hasRequestedData = function() {
			if (lastData) {
				return true;
			}
			return false;
		};
		self.getLastData = function() {
			return lastData;
		};
		var lastData;
		var dataRequestCallBack = function(data, textStatus) {
			self.data_request_by_interval = false;
			self.hasRenderedWithData = true;
			if (isPaused()) {
				return;
			}
			lastData = data;
			var descFunc = getCertainTypeOfAttributeFunc(self.themes, 'descFunc');
			if (reqDescInfoAtFirst === true) {
				if (data && data.length > 0) {
					var dataDetail = data[0];
					doInitChartContainers();
					if ($.isFunction(descFunc)) {
						descFunc.apply(self, [self, dataDetail]);
					}
					reqDescInfoAtFirst = false;
					if (self.requestDataFunc != null) {
						self.requestDataFunc();
					}
					showOrHideLoadingMainTip(true);
					return;
				}
			}
			
			var blnGoThrough = true;
			if (events.beforeRenderingReceivedData) {
				blnGoThrough = events.beforeRenderingReceivedData.call(self,data);
				blnGoThrough = isUndefined(blnGoThrough, true);
			}
			// wait for next request
			if (blnGoThrough && self.callNextData) {
				doSendRequestTimer();
			}
			
			if (!blnGoThrough && !firstTimeCall) {
				showOrHideLoadingMainTip(false);
				return;
			}
			
			if (data && data.length > 0) {
				var dataDetail = data[0];
				var succ = true;
				
				if (dataDetail.resultStatus) {
					var rs = dataDetail.resultStatus;
					if (rs.status
							&& rs.status === false) {
						succ = false;
					}
					var errTmp = "";
					if (rs.errInfo) {
						errTmp = rs.errInfo;
					}
					if (rs.errCode) {
						// add operations here
					}
					self.tips.addError(errTmp);
				}
				
				if (succ === true) {
					initializeChartIfNeeded(dataDetail);
					
					var beforeChartDataFunc = getCertainTypeOfAttributeFunc(self.themes, 'beforeChartDataFunc');
					var afterChartDataFunc = getCertainTypeOfAttributeFunc(self.themes, 'afterChartDataFunc');
					var afterChartDataIsRenderedFunc = getCertainTypeOfAttributeFunc(self.dataRenderMethods, 'afterChartDataIsRenderedFunc');
					
					if (beforeChartDataFunc) {
						beforeChartDataFunc.apply(self, [self, dataDetail]);
					}
					
					if (self.isRenderedAsHTML()) {
						var htmlDataFunc = getCertainTypeOfAttributeFunc(self.dataRenderMethods, 'htmlDataFunc');
						if (!htmlDataFunc) {
							htmlDataFunc = function() {
								$el(self.AREA_SECTIONS.CHART_CONTENT).html("<div>"+dataDetail.htmlText+"</div>");
							}
						}
						if (htmlDataFunc) {
							htmlDataFunc.apply(self, [self, options, dataDetail]);
						}
					} else if (self.isRenderedAsChart() || self.isRenderedAsTable()) {
						var chartDataRenderFunc = getCertainTypeOfAttributeFunc(self.dataRenderMethods, 'dataFunc');
						var chartLegendRenderFunc = getCertainTypeOfAttributeFunc(self.dataRenderMethods, 'legendFunc');
						
						if (chartLegendRenderFunc) {
							chartLegendRenderFunc.apply(self, [self, options, dataDetail]);
						}
						
						if (chartDataRenderFunc) {
							chartDataRenderFunc.apply(self, [self, options, dataDetail]);
						}
					}
					
					if (afterChartDataFunc) {
						afterChartDataFunc.apply(self, [self, dataDetail]);
					}
					
					if (events.doCustomDataOperation) {
						events.doCustomDataOperation.apply(self, [self, dataDetail]);
					}
					
					if (self.isRenderedAsChart()) {
						self.redrawHighChart(dataDetail);
					}
					
					if (afterChartDataIsRenderedFunc) {
						afterChartDataIsRenderedFunc.apply(self);
					}
				}
			}
			
			// hide loading icon
			showOrHideLoadingMainTip(false);
			if (firstTimeCall) {
				firstTimeCall = false;
			}
		};
		self.dataReceivedCallBack = dataRequestCallBack;
		self.redrawHighChart = function(dataDetail) {
			self.hcChart.redraw();
			var additionalRenderAfterDrawFunc = getCertainTypeOfAttributeFunc(self.dataRenderMethods, 'afterDrawFunc');
			if (additionalRenderAfterDrawFunc) {
				additionalRenderAfterDrawFunc.apply(self, [self, options, dataDetail]);
			}
		};
		
		var changed = false;
		if (!isUndefined(options.priChanged)) {
			changed = options.priChanged;
		}
		self.setChanged = function(chged) {
			if (arguments.length < 1) {
				chged = true;
			}
			/*if (changed === chged) {
				return;
			}*/
			changed = chged;
			if (self.afterContentChangedFunc) {
				self.afterContentChangedFunc.apply(self, [changed]);
			}
		};
		self.isInChangeMode = function() {
			return changed;
		};
		
		self.render = function() {
			if (!self.container) {
				return;
			}
			
			each(self.callbacks, function(fn) {
				fn.apply(self, [self, options]);
			});
			
			if (options.buttons
					&& $.isArray(options.buttons)
					&& options.buttons.length > 0) {
				each(options.buttons, function(fn) {
					fn.apply(self, [self, options]);
				});
			}
			
			doInitChartContainers();
			
			doDataRequest();
			self.rendered = true;
			
			RenderedCharts.add(self.container, self);
		};
		
		var initializeChartIfNeeded = function(data) {
			if (chartInited === false) {
				chartInited = true;
				doInitHighChart(self.AREA_SECTIONS.CHART_CONTENT, options, data);
				renderCtlButtons(false);
				self.addActionForButtonMenu('.my-ahchart-menu-button');
			}
		};
		
		var renderCtlButtons = function(always) {
			var length = controlButtonsIndex.length;
			for (var i = 0; i < length; i++) {
				var tmpBtn = controlButtons[controlButtonsIndex[i]];
				if (tmpBtn && tmpBtn.isCtlBuilder && always === tmpBtn.alwaysRender && $.isFunction(tmpBtn.render)) {
					tmpBtn = tmpBtn.render();
					if ($.isFunction(tmpBtn.render)) {
						tmpBtn.render();
					}
					controlButtons[controlButtonsIndex[i]] = tmpBtn;
					
					var tmpBtnCallbacks = controlBtnAfterRenderedFunc[controlButtonsIndex[i]];
					if (tmpBtnCallbacks && tmpBtnCallbacks.length > 0) {
						for (var k = 0; k < tmpBtnCallbacks.length; k++) {
							if ($.isFunction(tmpBtnCallbacks[k])) {
								tmpBtnCallbacks[k].apply(tmpBtn, [tmpBtn, self]);
							}
						}
						
						delete controlBtnAfterRenderedFunc[controlButtonsIndex[i]];
					}
				}
			}
		};
		
		var chartContainerInited = false;
		var doInitChartContainers = function() {
			if (chartContainerInited === true) {
				return;
			}
			if (!self.container) {
				return;
			}
			
			if (cwidth != '100%') {
				self.$container.width(cwidth);
			}
			var $anchorTmp = $("<div><a name='" + self.AREA_SECTIONS.ANCHOR_NAME + "' id='" + self.AREA_SECTIONS.ANCHOR_NAME + "'/></div>");
			self.$container.append($anchorTmp);
			var anchorTextNode = self.AREA_SECTIONS.ANCHOR_NAME + "_text";
			if (options.anchor) {
				anchorTextNode = options.anchor;
			}
			$el(anchorTextNode).attr("href", "#" + self.AREA_SECTIONS.ANCHOR_NAME).show();
			
			var themeFunc = getCertainTypeOfAttributeFunc(self.themes, 'themeFunc', options.theme);
			if ($.isFunction(themeFunc)) {
				var containerOptions = {
					cwidth: cwidth,
					contentHeight: contentHeight
				}
				themeFunc.apply(self, [self, containerOptions]);
			}
			
			renderCtlButtons(true);
			
			self.statusChanged(self.STATUS_MODE.VIEW);
			
			chartContainerInited = true;
			
			if (callback) {
				callback.apply(self, [self]);
			}
		};
		self.syncAnchorTextWithTitle = function() {
			$("a[href=#"+self.AREA_SECTIONS.ANCHOR_NAME+"]").text($el(self.AREA_SECTIONS.MAIN_TITLE).text());
		};
		
		var adjustChartContentHeight = function(valueTmp) {
			$el(self.AREA_SECTIONS.CHART_CONTENT).height(valueTmp);
		};
		
		var doInitHighChart = function(divId, options, data) {
			if (!self.isRenderedAsChart()) {
				return;
			}
			adjustChartContentHeight("100%");
			var chartOption = $.extend(true, {}, 
						defChartOptions, 
						{
							renderTo: divId, 
							width: pick(options.chartWidth, null), 
							height: pick(options.chartHeight, 0)
						}
			);
			if (blnOriZoomType === false) {
				if (options.zoomType && options.zoomType !== 'no') {
					$.extend(true, chartOption, {
						zoomType: optZoomType,
						events: {
							selection: onXAxisSelection
						}
					});
				} else {
					$.extend(true, chartOption, {
						zoomType: ''
					});
				}
			}
			var legendOptions = $.extend(true, {},
						deflegendOptions,
						{
							labelFormatter: function () {
								if (this.name) {
									return doShowLegend(this);
								}
								return '';
							}
						}
			);
			if (options.legend) {
				var legendTmp = $.trim(options.legend);
				if (legendTmp != null && legendTmp != '') {
					if (legendTmp === 'no') {
						$.extend(legendOptions, {enabled: false});
					} else {
						var aligns = legendTmp.split(' ');
						if (aligns.length === 2) {
							$.extend(legendOptions, {align: aligns[0], verticalAlign: aligns[1]});
							if ((aligns[0] === 'left' || aligns[0] === 'right')
									&& aligns[1] === 'middle') {
								$.extend(legendOptions, {layout: 'vertical'});
							} else if ((aligns[1] === 'top' || aligns[1] === 'bottom')
									&& aligns[0] === 'center') {
								$.extend(legendOptions, {layout: 'horizontal'});
							}
						}
					}
				}
			}
			var rotationTmp = pick(options.xrotation, 0);
			if (options
					&& options.xAxisType === 'datetime'
					&& !options.xrotation) {
				rotationTmp = 335;
			}
			var xAxisOptions = $.extend(true, {}, defXAxisOptions,
						{
							type: pick(options.xAxisType, 'linear')
						},
						{
							labels:
							{
								rotation: rotationTmp
							},
							dateTimeLabelFormats: 
							{
								hour: '%l:%M%p',
								minute: '%l:%M%p',
								second: '%l:%M:%S%p',
								millisecond: '%l:%M:%S%p'
							}
						}
			);
			
			var yAxisOptions = $.extend(true, {}, defYAxisOptions);
			
			var typeTmp = defChartOptions.defaultSeriesType;
			if (data.series
					&& data.series.length > 0) {
				var seriesTmp = data.series[0];
				typeTmp = seriesTmp.type;
				if (typeTmp === 'pie') {
					$.extend(true, yAxisOptions, {
						lineWidth: 0
					});
					$.extend(true, xAxisOptions, {
						lineWidth: 0
					});
				} else if (typeTmp === 'column' || typeTmp === 'bar') {
					var invertedTmp = false;
					if (options && options.expert && options.expert.chart) {
						invertedTmp = options.expert.chart.inverted;
					}
					if (invertedTmp) {
						$.extend(true, yAxisOptions, {
							tickLength: 100,
							tickPixelInterval: 150
						});
						if (!self.isDatetimeType) {
							$.extend(true, xAxisOptions, {
								labels: {
									 formatter: function() {
										 var val = this.value;
										 if (val.length > 16) {
											 val = val.substring(0, 13) + '...';
										 }
										 return val;
									 }
								 }
							});
						}
					}
				}
			}
			var highChartRenderOptions = $.extend(true, {}, {
				colors: defColors,
				chart: chartOption,
				title: defTitleOptions,
				legend: legendOptions,
				credits: defCreditsOptions,
				exporting: defExportingOptions,
				plotOptions: defPlotOptions,
				
				xAxis: xAxisOptions,
				yAxis: yAxisOptions,
				tooltip: {
					borderWidth: 1,
					style: {
						fontSize: '9px'
					},
					formatter: function() {
						return tipFormatterFunc(this, self);
					}
				},
				
				series: []
			}, options.expert);
			
			var chartRenderFunc = getCertainTypeOfAttributeFunc(self.chartRenderMethods, 'func', options.renderMethod);
			if ($.isFunction(chartRenderFunc)) {
				highChartRenderOptions = chartRenderFunc.apply(self, [self, options, data, highChartRenderOptions]);
			}
			
			self.hcChart = new Highcharts.Chart(highChartRenderOptions);
		};		
	};
	
	Chart.prototype.addActionForButtonMenu = function(elPath) {
		var self = this;
		$el(this.uniqueID + " " + elPath).unbind('click').click(function(e) {
			var $this = $(this);
			var $parent = $this.parents("div.dropdown");
			$parent.toggleClass("open");
			var $mainMenu = $parent.find("> .dropdown-menu");
			var btnPos = $this.position();
			var offPos = $this.offset();
			var pageSize = getPageWH();
			var menuPos = {
				top: btnPos.top + $this.height() + 8,
				left: btnPos.left
			};
			$parent.removeClass("my-out-right-page");
			if (offPos.left + $mainMenu.width() > pageSize.width) {
				$parent.addClass("my-out-right-page");
				// plus 10 for padding+margin
				menuPos.left = menuPos.left + $this.width() - $mainMenu.width() + 10;
			}
			$mainMenu.css({
				top: menuPos.top,
				left: menuPos.left
			});
			$this.focus();
			self.bln_click_in_menu_area = false;
			e.preventDefault();
			e.stopPropagation();
		}).blur(function(e) {
			if (!self.bln_click_in_menu_area) {
				$(this).parents("div.dropdown").removeClass("open");
			}
		});
		
		$el(this.uniqueID + " .dropdown-menu > li").unbind('hover').hover(function(e) {
			var $this = $(this);
			var $parent = $this.parents("div.dropdown");
			if ($this.hasClass("dropdown-submenu")) {
				var curPos = $this.position();
				var $subMenu = $this.find("> .dropdown-menu");
				var leftPos = curPos.left + $this.width() + 1;
				if ($parent.hasClass("my-out-right-page")) {
					leftPos = curPos.left - $subMenu.width() -1;
				}
				$subMenu.css({
					top: 0,
					left: leftPos
				})
				.show();
			}
		}, function(e) {
			var $this = $(this);
			if ($this.hasClass("dropdown-submenu")) {
				$this.find("> .dropdown-menu").hide();
			}
		});
		
		$el(this.uniqueID + " ul.dropdown-menu").unbind("mouseover")
			.unbind("mouseout")
			.mouseover(function(e) {
				self.bln_click_in_menu_area = true;
			}).mouseout(function(e) {
				self.bln_click_in_menu_area = false;
			});
	};
	Chart.prototype.menuItemClickAfter = function(menuId) {
		$el(this.uniqueID + " #" + menuId).parents("div.dropdown").removeClass("open");
	};
	
	var CandidateCallbacks = function() {
		var candidates = {};
		return {
			addCandidate: function(name, func) {
				if (name && func) {
					candidates[name] = func;
				}
			},
			rmCandidate: function(names) {
				if (names && names.length > 0){
					for (var i = 0; i < names.length; i++) {
						var name = names[i];
						if (candidates[name]) {
							delete candidates[name];
						}
					}
				}
			},
			rmUsedCallbacks: function(names) {
				if (names && names.length > 0 && Chart.prototype.callbacks) {
					for (var i = 0; i < names.length; i++) {
						var name = names[i];
						if (name) {
							var lenTmp = Chart.prototype.callbacks.length;
							for (var j = 0; j < lenTmp; j++) {
								var elTmp = Chart.prototype.callbacks[j];
								if (elTmp && name === elTmp.name) {
									Chart.prototype.callbacks.splice(j, 1);
									break;
								}
							}
						}
					}
				}
			},
			addUsedCallbacks: function(names) {
				if (names && names.length > 0) {
					for (var i = 0; i < names.length; i++) {
						var nameObj = names[i],
							name = nameObj;
						if ($.isArray(nameObj)) {
							name = nameObj[0];
							if (nameObj.length > 1) {
								Chart.prototype.__additionalOptions.ctlButtons = 
												Chart.prototype.__additionalOptions.ctlButtons || {};
								Chart.prototype.__additionalOptions.ctlButtons[name] = nameObj[1];
							}
						}
						if (name && candidates[name]) {
							Chart.prototype.callbacks.push(candidates[name]);
						}
					}
				}
			},
			get: function(name) {
				return candidates[name];
			},
			clearUsedCallbacks: function() {
				Chart.prototype.callbacks = [];
			},
			clearCandidates: function() {
				candidates = {};
			}
		}
	};
	CandidateCallbacks = new CandidateCallbacks();
	
	// define some default options for this widget
	// default options for outer widget
	var CONTAINER_TOP_TITLE_OP = '_top_title_op',
		CONTAINER_CHART_CONTENT = '_chart_content',
		CONTAINER_BOTTOM_SUMMARY = '_bottom_summary';
	// default options for Highcharts
	var defColors = ["#0093D1", "#002848", "#C84B00", "#F7A520", "#FFC20E", "#BDBAAB", "#6F6F6F", "#0066FF", "#00CCCC"
	         		, "#330099", "#339999", "#3399CC", "#663300", "#66CCFF", "#993399", "#999900", "#CC0066", "#CC6633"
	        		,"#CCCCFF", "#FF6699", "#FF66FF", "#FFCC99", "#FFFF66"
	        		,"#00FF99", "#336600", "#FF0000", "#FF3399", "#CCFF00", "#CCCCCC", "#666699"
	        		,"#000000"];
	var defChartOptions = {
		defaultSeriesType: 'column',
		backgroundColor: '#FFFFFF',
		animation: false,
		events: {},
		showAxes: true,
		ignoreHiddenSeries: true,
		spacingRight: 1,
		spacingLeft: 1
	};
	var defTitleOptions = {
		text: ''
	};
	var deflegendOptions = {
		align: 'right',
		layout: 'vertical',
		verticalAlign: 'middle',
		itemStyle: {
			fontSize: '11px'
		},
		x: -2
	};
	var defXAxisOptions = {
	};
	var defYAxisOptions = {
		gridLineColor: '#FFFFFF',
		lineWidth: 1
	};
	var defCreditsOptions = {
		enabled: false
	};
	var defExportingOptions = {
		enabled: false,
		filename: 'currentChart',
		url: 'reportExport.action?operation=export&ignore='+new Date().getTime()
	};
	var defPlotOptions = {
		pie: {
			allowPointSelect: false,
			cursor: 'pointer',
			dataLabels: {
				enabled: false,
				formatter: function() {
					return this.percentage.toFixed(2) + '%';
				}
			},
			showInLegend: true
		},
		column: {
			borderWidth: 0,
			shadow: false
		},
		bar: {
			borderWidth: 0,
			shadow: false
		},
		area: {
			borderWidth: 0,
			shadow: false
		}
	};
	
	// other helper tools
	var SUB_MENU_DIV_POSTFIX = '_sub';
	
	Chart.prototype.callbacks = [];
	Chart.prototype.themes = {};
	Chart.prototype.chartRenderMethods = {};
	Chart.prototype.dataRenderMethods = {};
	Chart.prototype.seriesPointClick = null;
	Chart.prototype.xCategoryClick = null;
	Chart.prototype.__onStatusChanged = null;
	Chart.prototype.__editPermissionCheck = null;
	Chart.prototype.__additionalOptions = {};
	
	var periodTypeEnum = {
			NOT_DEFINED       : -1,
			LASTONEHOUR       : 1,
			LASTCLOCKHOUR     : 2,
			LASTONEDAY        : 3,
			LASTCALENDARDAY   : 4,
			LASTWEEK          : 5,
			LASTCALENDARWEEK  : 6,
			LASTONEMONTH      : 7,
			LASTCALENDARMONTH : 8,
			CUSTOM            : 9
	};
	
	$.extend(AhReportChart, {
		Chart: Chart,
		Events: Events,
		RenderedCharts: RenderedCharts,
		getCurrentPopUpWinChart: getCurrentPopUpWinChart,
		periodTypeEnum: periodTypeEnum,
		isUndefined: isUndefined,
		isObject: isObject,
		isString: isString,
		callbacks: CandidateCallbacks,
		pick: pick,
		each: each,
		stopEventBubble: stopEventBubble,
		$el: $el,
		pFloat: pFloat,
		getPageWH: getPageWH,
		consoleLog: consoleLog,
		replaceSpecialSymbol: replaceSpecialSymbol,
		escapeHtmlTag: escapeHtmlTag,
		product: 'AhReportChart',
		version: '0.9.0'
	});
}
)(jQuery);