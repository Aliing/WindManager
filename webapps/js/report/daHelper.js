(function($, _){
	var win = window,
		UNDEFINED;
	
	win.AhDashboardHelper = {};
	
	function isUndefined(obj) {
		if (obj !== UNDEFINED) {
			return false;
		}
		return true;
	}
	function isNotUndefined(obj) {
		return !isUndefined(obj);
	}
	function isObject(obj) {
		return typeof obj === 'object';
	}
	
	
	var Helper = function() {
		var self = this;
		// published methods
		self.isOvertime = function(axis, chart) {
			if (chart) {
				var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
				if (curConfig) {
					return curConfig.overtime;
				}
			} 
			return AHDaSimpleHelper._isOvertime(axis);
		};
		
		self.getDefaultChartType = function(axis) {
			return AHDaSimpleHelper._getDefaultChartType(axis);
		};
		
		self.getWidgetChartType = function(axis, chart) {
			if (chart) {
				var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
				if (curConfig && curConfig.chartType) {
					return curConfig.chartType;
				}
			} 
			
			return self.getDefaultChartType(axis);
		};
		
		self.isChartDefaultInverted = function(axis) {
			return AHDaSimpleHelper._isChartDefaultInverted(axis);
		};
		
		self.isWidgetChartInverted = function(axis, chart) {
			if (chart) {
				var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
				if (curConfig && curConfig.hasOwnProperty('inverted')) {
					return curConfig.inverted;
				}
			} 
			
			return self.isChartDefaultInverted(axis);
		};
		
		self.getDefaultChartDirection = function(axis) {
			return AHDaSimpleHelper._getDefaultChartDirection(axis);
		};
		
		self.getMetricUnit = function(axis, metric) {
			return AHDaSimpleHelper._getMetricUnit(axis, metric);
		};
		
		self.selectProperUnitForSeries = function(data, baseUnit) {
			return AHDaSimpleHelper._selectProperUnitForSeries(data, baseUnit);
		}
		
		self.getPreferPresentation = function(axis, metric, data) {
			return AHDaSimpleHelper._getPreferPresentation(axis, metric, data);
		};
		self.getPreferPresentationWithPresentObj = function(presentObj, data) {
			return AHDaSimpleHelper._getPreferPresentationWithPresentObj(presentObj, data);
		};
		self.getValueAndMarkFromCategory = function(value) {
			if (value && (typeof value === "string")) {
				return value.split("[!<||");
			}
		};
		
		var getMetricPropertiesFromConfig = function(curConfig, metric, prop) {
			if (curConfig && curConfig.m) {
				for (var key in curConfig.m) {
					var mObjArray = curConfig.m[key];
					var result = _.find(mObjArray, function(mObj) {
						return mObj.metric == metric;
					});
					if (result) {
						break;
					}
				}
				if (prop) {
					return result[prop];
				}
				return result;
			}
		};
		self.getMetricProperties = getMetricPropertiesFromConfig;
		var MILLISECONDS_30_DAYS = 30*24*3600000;
		self.checkWhetherMonitorItemInTimeRange = function(curConfig) {
			if (curConfig && curConfig.desc
					&& curConfig.desc.rqtime - curConfig.desc.starttime <= MILLISECONDS_30_DAYS) {
				return true;
			}
		};
		
		var monitorSupportedType = {
			"app": true,
			"user": true,
			"device": true,
			"client": true,
			"port": true,
			"appclient": true
		};
		self.isChartMonitorable = function(curConfig, metric) {
			if (!curConfig) {
				return false;
			}
			if (!metric) {
				return monitorSupportedType[curConfig['xmt']]?true:false;
			} else {
				return monitorSupportedType[getMetricPropertiesFromConfig(curConfig, metric, 'mt')]?true:false;
			}
		};
		
		var deviceLinkSupportedType = {
			"deviceDetail": true,
			"client": false
		};
		self.isChartDeviceLinkable = function(curConfig, metric) {
			if (!curConfig) {
				return false;
			}
			if (!metric) {
				return deviceLinkSupportedType[curConfig['xmt']]?true:false;
			} else {
				return deviceLinkSupportedType[getMetricPropertiesFromConfig(curConfig, metric, 'mt')]?true:false;
			}
		};
		
		var drilldownSupportedType = {
			"dd": true
		};
		self.isChartMetricDrillDownable = function(curConfig, metric) {
			if (!curConfig || !metric) {
				return false;
			}
			return drilldownSupportedType[getMetricPropertiesFromConfig(curConfig, metric, 'mt')]?true:false;
		};
		
		var valueLinkSupportedType = {
			"link": true
		};
		self.isChartMetricValueLinkable = function(curConfig, metric) {
			if (!curConfig || !metric) {
				return false;
			}
			return valueLinkSupportedType[getMetricPropertiesFromConfig(curConfig, metric, 'mt')]?getMetricPropertiesFromConfig(curConfig, metric, 'mv'):false;
		};
		
		var supportedURLArgs = ['data_key', 'name_key', 'topo_id'];
		self.getFormattedLinkUrl = function(url, option) {
			if (url && url.indexOf("{{") >= 0) {
				option = option || {};
				for (var i = 0; i < supportedURLArgs.length; i++) {
					url = AHDaSimpleHelper._replaceAllOnce(url, "{{"+supportedURLArgs[i]+"}}", option[supportedURLArgs[i]] || "");
				}
			}
			return url + "&ignore=" + new Date().getTime();
		};
		
		self.getOriNameValue = function(pointInfo, axis, metric, name) {
			var result = pointInfo.name || name || "";
			var axisObj = self.getAxis(axis);
			if (axisObj && axisObj.yaxis[metric] && axisObj.yaxis[metric].data_key_convert) {
				result = axisObj.yaxis[metric].data_key_convert(result) || "";
			} else {
				if (pointInfo && pointInfo.series 
						&& pointInfo.series.options 
						&& pointInfo.series.options.myDataNode
						&& pointInfo.series.options.myDataNode.nameMap) {
					result = pointInfo.series.options.myDataNode.nameMap[result] || "";
				}
			}
			
			return result;
		};
		
		self.getNameBeforeConvert = function(pointInfo, axis, metric, name) {
			var result = pointInfo.name || name || "";
			var axisObj = self.getAxis(axis);
			if (pointInfo && pointInfo.series 
					&& pointInfo.series.options 
					&& pointInfo.series.options.myDataNode
					&& pointInfo.series.options.myDataNode.nameMap) {
				result = pointInfo.series.options.myDataNode.nameMap[result] || "";
			}
			
			return result;
		};
		
		var getChartMetricDrillDownValue = function(curConfig, metric) {
			return getMetricPropertiesFromConfig(curConfig, metric, 'mv');
		};
		
		self.getPromptKeyMap = function(axis) {
			var axisObj = self.getAxis(axis);
			if (isNotUndefined(axisObj)) {
				var result = {};
				result[axisObj.prompt] = axis;
				if (axisObj.yaxis) {
					var yaxisObj;
					_.each(_.keys(axisObj.yaxis), function(key) {
						yaxisObj = axisObj.yaxis[key];
						result[yaxisObj.prompt] = key;
					});
				}
				return result;
			}
		};
		
		self.getExpressionMetrics = function(exp) {
			return AHDaSimpleHelper._getExpressionMetrics(exp);
		};
		
		self.getExpressionFuncs = function(exp) {
			return AHDaSimpleHelper._getExpressionFuncs(exp);
		};
		
		self.replaceAllOnce = function(exp, findStr, repStr) {
			return AHDaSimpleHelper._replaceAllOnce(exp, findStr, repStr);
		};
		self.replaceAll = function(expression,global,replacement) {
			return AHDaSimpleHelper._replaceAll(expression,global,replacement);
		};
		self.isMetricDataIntType = function(axis, metric) {
			return AHDaSimpleHelper._isMetricDataIntType(axis, metric);
		};
		
		self.getFuncOnlyExpression = function(exp, options) {
			return AHDaSimpleHelper._getFuncOnlyExpression(exp, options);
		};
		self.getCertainExpFromExpConfig = function(exp, options, additionalFunc) {
			exp = AHDaSimpleHelper._getCertainExpFromExpConfig(exp, options);
			if (additionalFunc) {
				exp = additionalFunc(exp);
			}
			return exp;
		};
		
		self.enhanceExpToSafer = function(exp, rowName, options) {
			return AHDaSimpleHelper._enhanceExpToSafer(exp, rowName, options);
		};
		
		self.getAxis = function(axis) {
			if (!axis) {
				return;
			}
			if (isObject(axis)) {
				return axis;
			}
			
			if (AeroHive.localizationData[axis]) {
				return AeroHive.localizationData[axis];
			}
		};
		
		self.getMetric = function(axis, metric) {
			var axisObj = self.getAxis(axis);
			if (axisObj && axisObj.yaxis) {
				return axisObj.yaxis[metric];
			}
		};
		
		self.getKeyOfPromptAxis = function(prompt) {
			return _.find(_.keys(AeroHive.localizationData), function(key){
				if (isNotUndefined(AeroHive.localizationData[key])
					&& AeroHive.localizationData[key].prompt == prompt) {
					return true;
				}
				return false;
			});
		};
		
		self.getKeyOfPromptMetric = function(axis, prompt) {
			var axisObj = self.getAxis(axis);
			if (isNotUndefined(axisObj) && axisObj.yaxis) {
				return _.find(_.keys(axisObj.yaxis), function(key){
					if (isNotUndefined(axisObj.yaxis[key])
						&& axisObj.yaxis[key].prompt == prompt) {
					return true;
				}
				return false;
				});
			}
		};
		
		self.getAxisObjFromChart = function(chart) {
			if (!chart || !chart.currentDashboard) {
				return;
			}
			var curDa = chart.currentDashboard;
			var curConfig = curDa.getReportConfig(chart.reportId);
			if (!curConfig) {
				return;
			}
			return self.getAxis(curConfig.axis);
		};
		
		self.getValueRange = function(chart, curConfig) {
			if (!curConfig && chart) {
				curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
			} 
			if (curConfig && curConfig.valRange) {
				return curConfig.valRange.split('to');
			}
		};
		
		self.isValueRangeMaxSet = function(chart, curConfig) {
			if (!curConfig && chart) {
				curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
			} 
			if (curConfig && curConfig.valRange) {
				var val = curConfig.valRange.split('to');
				if (val && val.length == 2) {
					return true;
				}
			}
			return false;
		};
		
		self.getDrillDownInfoOfChart = function(chart, point) {
			if (!chart || !chart.currentDashboard) {
				return;
			}
			var curDa = chart.currentDashboard;
			var curConfig = curDa.getReportConfig(chart.reportId);
			var axis = curConfig.axis;
			var metric = point.series.options.keyN;
			if (!self.isChartMetricDrillDownable(curConfig, metric)) {
				return;
			}
			
			var blnOvertime = self.isOvertime(axis);
			var xName = point.series.name;
			var ctime = -1;
			if (!blnOvertime) {
				var markPos = xName.indexOf('<br/>');
				if (markPos > 0) {
					xName = point.category + '/' + xName.substring(0, markPos);
				} else {
					xName = point.category;
				}
			} else {
				ctime = point.x;
			}
			
			return {
				drilldownType: getChartMetricDrillDownValue(curConfig, metric) || "0",
				node: point.series.options.myDataNode.lid,
				bkType: point.series.options.myDataNode.bkType,
				bkValue: point.series.options.myDataNode.bkValue,
				isSwitch: point.series.options.myDataNode.isSwitch,
				overtime: blnOvertime,
				xName: xName,
				metric: metric,
				ctime: ctime,
				widgetId: curDa.getMarkHelperFromChart(chart.container, 'oWidgetId')
			};
		};
		
		self.isPropertyExist = function(obj, properties) {
			if (!obj) {
				return false;
			}
			var result = true;
			if (!_.isArray(properties)) {
				properties = [properties];
			}
			var curObj = obj,
				lstProperty;
			for (var i = 0; i < properties.length; i++) {
				if (properties[i] in curObj) {
					lstProperty = properties[i];
					curObj = curObj[lstProperty];
				} else {
					result = false;
					break;
				}
			}
			if (result) {
				var resultObj;
				if (!_.isObject(curObj)) {
					resultObj = {};
					resultObj[lstProperty] = curObj;
				} else {
					resultObj = curObj;
				}
				return resultObj;
			}
			return false;
		};
		self.getWidgetAdditionalProperties = function(obj, properties, options) {
			if (!_.isArray(properties)) {
				properties = [properties];
			}
			if (properties.length == 0) {
				return null;
			}
			options = options || {};
			var propertiesWithKey;
			if (options.blnForKey && options.keyValue) {
				propertiesWithKey = [];
				_.each(properties, function(pro) {
					propertiesWithKey.push(pro);
				});
				propertiesWithKey.push("forkey");
				propertiesWithKey.push(options.keyValue);
			}
			var result = null;
			var obj1 = self.isPropertyExist(obj, propertiesWithKey);
			if (!obj1) {
				obj1 = self.isPropertyExist(obj, properties);
			}
			var tmpProperties = [];
			_.each(properties, function(pro) {
				tmpProperties.push(pro);
			});
			tmpProperties[0] = "COMMON_CONFIG";
			var objC = self.isPropertyExist(obj, tmpProperties);
			if (obj1 && objC) {
				result = $.extend(true, {}, objC, obj1);
			} else {
				result = obj1 || objC;
			}
			return result;
		};
		self.getCertainName = function(obj) {
			return AHDaSimpleHelper._getCertainName(obj);
		};
		
		// private methods
		var getAxisChartConfigs = function(axisObj) {
			return AHDaSimpleHelper._getAxisChartConfigs(axisObj);
		};
	};
	
	var helper = new Helper();
	
	$.extend(AhDashboardHelper, {
		helper: helper,
		product: 'AhDashboardHelper',
		version: '0.0.1'
	});
})(jQuery, _);