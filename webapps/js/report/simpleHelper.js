var AHDaSimpleHelper = {};
AHDaSimpleHelper._getAxisObj = function(axis) {
	if (AHDaSimpleHelper._isObject(axis)){
		return axis;
	}
	var axisObj;
	if (AeroHive.localizationData[axis]) {
		axisObj = AeroHive.localizationData[axis];
	}
	return axisObj;
};
AHDaSimpleHelper._isUndefined = function(obj) {
	if (typeof obj == 'undefined') {
		return true;
	}
	return false;
};
AHDaSimpleHelper._isDefined = function(obj) {
	if (typeof obj != 'undefined') {
		return true;
	}
	return false;
};
AHDaSimpleHelper._isObject = function(obj) {
	if (typeof obj == 'object') {
		return true;
	}
	return false;
};
AHDaSimpleHelper._getCertainName = function(obj) {
	if (AHDaSimpleHelper._isObject(obj)) {
		return obj.name;
	}
	return obj;
};
AHDaSimpleHelper._getAxisChartConfigs = function(axisObj) {
	if (axisObj && axisObj.defaultChart) {
		var configs = axisObj.defaultChart.split(' ');
		if (!configs[1] && (configs[0] == 'vertical' || configs[0] == 'horizontal')) {
			configs = ['column', configs[0]];
		}
		return configs;
	}
};
AHDaSimpleHelper._isOvertime = function(axis) {
	var axisObj = AHDaSimpleHelper._getAxisObj(axis);
	if (AHDaSimpleHelper._isDefined(axisObj) && AHDaSimpleHelper._isDefined(axisObj.overtime)) {
		return axisObj.overtime;
	}
	return false;
};
AHDaSimpleHelper._getMetricUnit = function(axis, metric) {
	axis = AHDaSimpleHelper._getAxisObj(axis);
	if (AHDaSimpleHelper._isDefined(axis) && axis.yaxis[metric]) {
		return axis.yaxis[metric].unit;
	}
};
AHDaSimpleHelper._getDefaultChartType = function(axis) {
	var result = "column";
	var configs = AHDaSimpleHelper._getAxisChartConfigs(AHDaSimpleHelper._getAxisObj(axis));
	if (AHDaSimpleHelper._isDefined(configs) && configs[0]) {
		result = configs[0];
	}
	
	return result;
};
AHDaSimpleHelper._isChartDefaultInverted = function(axis) {
	var directionTmp = AHDaSimpleHelper._getDefaultChartDirection(axis);
	if (directionTmp == 'horizontal') {
		return true;
	}
	return false;
};
AHDaSimpleHelper._getDefaultChartDirection = function(axis) {
	var result = "vertical";
	var configs = AHDaSimpleHelper._getAxisChartConfigs(AHDaSimpleHelper._getAxisObj(axis));
	if (AHDaSimpleHelper._isDefined(configs) && configs[1]) {
		result = configs[1];
	}
	return result;
};
AHDaSimpleHelper._getPreferPresentation = function(axis, metric, data) {
	axis = AHDaSimpleHelper._getAxisObj(axis);
	if (AHDaSimpleHelper._isDefined(axis) && axis.yaxis[metric] && axis.yaxis[metric].presentation) {
		if (!data) {
			return axis.yaxis[metric].presentation;
		} else {
			return AHDaSimpleHelper._getPreferPresentationWithPresentObj(axis.yaxis[metric].presentation, data);
		}
	}
};
AHDaSimpleHelper._getMetricDataType = function(axis, metric) {
	axis = AHDaSimpleHelper._getAxisObj(axis);
	if (AHDaSimpleHelper._isDefined(axis) && axis.yaxis[metric] && axis.yaxis[metric].dataType) {
		return axis.yaxis[metric].dataType;
	}
};
AHDaSimpleHelper._isMetricDataIntType = function(axis, metric) {
	axis = AHDaSimpleHelper._getAxisObj(axis);
	if (AHDaSimpleHelper._isDefined(axis) && axis.yaxis[metric] && axis.yaxis[metric].valueInt) {
		return true;
	}
	return false;
};
AHDaSimpleHelper._getPreferPresentationWithPresentObj = function(presentObj, data) {
	if (presentObj) {
		var options = {};
		if (AHDaSimpleHelper._isObject(data)) {
			options = data;
			data = data.data;
		}
		if (AHDaSimpleHelper._isDefined(data)) {
			if (presentObj.databased && presentObj.databased[data]) {
				return presentObj.databased[data];
			}
		} else {
			if (presentObj.color) {
				return {
					color: presentObj.color
				}
			} else if (presentObj.colorSelector) {
				var curIdx = 0;
				if (options.colorTypes && presentObj.colorSelector.type in options.colorTypes) {
					curIdx = options.colorTypes[presentObj.colorSelector.type];
				}
				return {
					color: presentObj.colorSelector.func(curIdx),
					color_type: presentObj.colorSelector.type
				}
			}
		}
	}
};

AHDaSimpleHelper.__unitTypeDefinition = {
	_UNIT_TYPE1: [['B', 'KB',   'MB',   'GB',   'TB'], 
                   [1,   1000,   1000,   1000,   1000]],
    _UNIT_TYPE2: [['BPS', 'KBPS', 'MBPS', 'GBPS', 'TBPS'],
                  [1,     1000,   1000,   1000,   1000]],
    _UNIT_TYPE3: [['SECOND', 'MINUTE', 'HOUR', 'DAY'],
                   [1,        60,       60,     24]],
    _UNIT_TYPE1_DISPLAY: {
		B: "Bytes",
		KB: "KB",
		MB: "MB",
		GB: "GB",
		TB: "TB"
	},
	_UNIT_TYPE2_DISPLAY: {
		BPS: "bps",
		KBPS: "Kbps",
		MBPS: "Mbps",
		GBPS: "Gbps",
		TBPS: "Tbps"
	},
	_UNIT_TYPE3_DISPLAY: {
		SECOND: "sec",
		MINUTE: "min",
		HOUR: "hour",
		DAY: "day"
	}
};
AHDaSimpleHelper._selectProperUnitForSeriesExpress = function(data, baseUnit) {
	var result = AHDaSimpleHelper._selectProperUnitForSeries(data, baseUnit) || {};
	result.data = data;
	if (!result.unit) {
		result.unit = baseUnit;
	}
	if (result.func) {
		result['data'] = result.func(data);
		result.func = null;
	}
	return result;
};
AHDaSimpleHelper._selectProperUnitForSeries = function(data, baseUnit) {
	var _UNIT_TYPE1 = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE1;
	var _UNIT_TYPE2 = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE2;
	var _UNIT_TYPE3 = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE3;
	var _UNIT_TYPE1_DISPLAY = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE1_DISPLAY;
	var _UNIT_TYPE2_DISPLAY = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE2_DISPLAY;
	var _UNIT_TYPE3_DISPLAY = AHDaSimpleHelper.__unitTypeDefinition._UNIT_TYPE3_DISPLAY;
	var doFormatUnit = function(unit) {
		if (!unit) {
			return "";
		}
		if (unit === "%") {
			return unit;
		}
		return " " + unit;
	};
	var pos = _.indexOf(_UNIT_TYPE1[0], baseUnit);
	var unitLen = 0;
	var unit = baseUnit,
		func = null;
	if (pos >= 0) {
		unitLen = _UNIT_TYPE1[0].length;
		var valThreshold = 2000;
		var distance = 1;
		if (data <= valThreshold || pos == unitLen - 1) {
			return {
				unit: _UNIT_TYPE1_DISPLAY[unit],
				formatUnit: doFormatUnit(_UNIT_TYPE1_DISPLAY[unit])
			};
		}
		for (var i = pos + 1; i < unitLen; i++) {
			data = data/_UNIT_TYPE1[1][i];
			distance *= _UNIT_TYPE1[1][i];
			if (data > valThreshold) {
				continue;
			}
			i == unitLen? i = unitLen - 1 : null;
			unit = _UNIT_TYPE1[0][i];
			break;
		}
		if (unit == baseUnit && i == unitLen) {
			unit = _UNIT_TYPE1[0][unitLen - 1];
		}
		if (unit == baseUnit) {
			return {
				unit: _UNIT_TYPE1_DISPLAY[unit],
				formatUnit: doFormatUnit(_UNIT_TYPE1_DISPLAY[unit])
			};
		}
		return {
			unit: _UNIT_TYPE1_DISPLAY[unit],
			formatUnit: doFormatUnit(_UNIT_TYPE1_DISPLAY[unit]),
			func: function(value) {
				return (value/distance).toFixed(2);
			}
		};
	}
	
	pos = _.indexOf(_UNIT_TYPE2[0], baseUnit);
	if (pos >= 0) {
		unitLen = _UNIT_TYPE2[0].length;
		var valThreshold = 2000;
		var distance = 1;
		data = data * 8;
		if (data <= valThreshold || pos == unitLen - 1) {
			return {
				unit: _UNIT_TYPE2_DISPLAY[unit],
				formatUnit: doFormatUnit(_UNIT_TYPE2_DISPLAY[unit]),
				func: function(value) {
					return value*8;
				}
			};
		}
		for (var i = pos + 1; i < unitLen; i++) {
			data = data/_UNIT_TYPE2[1][i];
			distance *= _UNIT_TYPE2[1][i];
			if (data > valThreshold) {
				continue;
			}
			i == unitLen? i = unitLen - 1 : null;
			unit = _UNIT_TYPE2[0][i];
			break;
		}
		if (unit == baseUnit && i == unitLen) {
			unit = _UNIT_TYPE2[0][unitLen - 1];
		}
		if (unit == baseUnit) {
			return {
				unit: _UNIT_TYPE2_DISPLAY[unit],
				formatUnit: doFormatUnit(_UNIT_TYPE2_DISPLAY[unit]),
				func: function(value) {
					return value*8;
				}
			};
		}
		return {
			unit: _UNIT_TYPE2_DISPLAY[unit],
			formatUnit: doFormatUnit(_UNIT_TYPE2_DISPLAY[unit]),
			func: function(value) {
				return (value*8/distance).toFixed(2);
			}
		};
	}
	
	pos = _.indexOf(_UNIT_TYPE3[0], baseUnit);
	if (pos >= 0) {
		unitLen = _UNIT_TYPE3[0].length;
		if (pos == unitLen - 1) {
			return {
				forceStr: true,
				unit: "",
				func: function(value) {
					return value + " " + _UNIT_TYPE3_DISPLAY[baseUnit];
				}
			};
		}

		return {
			forceStr: true,
			unit: "",
			func: function(value) {
				var num = 0;
				var result = "";
				var i;
				var blnFirstZero = true;
				for (i = pos + 1; i < unitLen; i++) {
					if (value > 0 && value < _UNIT_TYPE3[1][i]) {
						result = value + " " + _UNIT_TYPE3_DISPLAY[_UNIT_TYPE3[0][i-1]] + (result?" ":"") + result;
						break;
					}
					num = value % _UNIT_TYPE3[1][i];
					if (num > 0 && blnFirstZero) {
						blnFirstZero = false;
					} 
					if (!blnFirstZero) {
						result = num + " "  + _UNIT_TYPE3_DISPLAY[_UNIT_TYPE3[0][i-1]] + (result?" ":"") + result;
					}
					value = (value-num)/_UNIT_TYPE3[1][i];
				}
				if (i == unitLen && value > 0) {
					result = value + " "  + _UNIT_TYPE3_DISPLAY[_UNIT_TYPE3[0][i-1]] + (result?" ":"") + result;
				}
				return result?result:("0 " + _UNIT_TYPE3_DISPLAY[baseUnit]);
			}
		};
	}
	
	return {
		unit: unit,
		formatUnit: doFormatUnit(unit),
		func: func
	}
};

AHDaSimpleHelper._getExpressionMetrics = function(exp) {
	if (!exp) {
		return [];
	}
	var result = [];
	var str = exp;
	var leftPos = str.indexOf('[');
	var rightPos = str.indexOf(']');
	while (str && leftPos >= 0) {
		if (rightPos - leftPos > 1) {
			result.push(str.substring(leftPos+1, rightPos));
			str = str.substring(rightPos+1);
		}
		if (str) {
			leftPos = str.indexOf('[');
			rightPos = str.indexOf(']');
		}
	}
	
	return result;
};
AHDaSimpleHelper._getExpressionFuncs = function(exp) {
	var result = [];
	var lPos = 0;
	var rPos = 0;
	var cutCounts = 0;
	_.each(AHDaSimpleHelper._supportedFuncs, function(funcName) {
		lPos = exp.indexOf(funcName + '(');
		rPos = exp.indexOf(')');
		while (exp && lPos >= 0 && rPos - lPos > 1) {
			result.push([exp.substring(lPos, rPos+1), lPos+cutCounts, rPos+cutCounts]);
			cutCounts = rPos + 1;
			exp = exp.substring(rPos + 1, exp.length);
			if (exp) {
				lPos = exp.indexOf(funcName + '(');
				rPos = exp.indexOf(')');
			}
		}
	});
	return result;
};


AHDaSimpleHelper._replaceAll = function (expression,global,replacement) {
	if (!expression || !global) {
		return "";
	}
	for(var replaced = expression.replace( global, replacement) ; replaced != expression ;) {
		replaced = (expression = replaced).replace( global, replacement);
	}
	return expression;
};
AHDaSimpleHelper._getFuncOnlyExpression = function(exp, options) {
	var markFunctionsInExp = function(exp) {
		var sLen = AHDaSimpleHelper._supportedFuncs.length;
		var isContainFunc = function(exp) {
			if (!exp) {
				return false;
			}
			var result = false;
			for (var i = 0; i < sLen; i++) {
				if (exp.indexOf(AHDaSimpleHelper._supportedFuncs[i]) >= 0) {
					result = true;
					break;
				}
			}
			return result;
		};
		if (!isContainFunc(exp)) {
			return exp;
		}
		var expNewTmp = "";
		var funcName;
		for (var i = 0; i < sLen; i++) {
			funcName = AHDaSimpleHelper._supportedFuncs[i];
			expNewTmp = AHDaSimpleHelper._replaceAllOnce(exp, funcName + "(", funcName + "!(");
		}
		var expNew = "";
		if (expNewTmp) {
			var blnInMatch = false,
				lefBracketCount = 0;
			for (var i = 0; i < expNewTmp.length;) {
				if (expNewTmp[i] === "!") {
					expNew += expNewTmp[i];
					if ((i < expNewTmp.length - 1) && expNewTmp[i+1] === "(") {
						blnInMatch = true;
						lefBracketCount = 1;
						expNew += expNewTmp[i+1];
						i += 2;
						continue;
					}
				}
				if (blnInMatch) {
					if (expNewTmp[i] === "(") {
						lefBracketCount++;
					} else if (expNewTmp[i] === ")") {
						lefBracketCount--;
						if (lefBracketCount == 0) {
							expNew += "!";
							blnInMatch = false;
							lefBracketCount = 0;
						}
					}
				}
				expNew += expNewTmp[i];
				i++;
			}
		}
		return expNew;
	};
	var expNew = markFunctionsInExp(exp);
	expNew = AHDaSimpleHelper._replaceAll(expNew, 
			"AHAVG!([", (options.avgFunc?options.avgFunc:"averageAH") 
			+ "(" 
			+ (options.rowsName?options.rowsName:"rows") 
			+ ", 0, " 
			+ (options.bkDown?1:0) 
			+ ", '" 
			+ (options.bkKey?options.bkKey:null) 
			+ "', '");
	expNew = AHDaSimpleHelper._replaceAll( expNew, "]!)" , "')");
	return expNew;
};
AHDaSimpleHelper._getCertainExpFromExpConfig = function(exp, options) {
	var aRowName = options.rowName?options.rowName:"rowData";
	
	if (!options.noFunc) {
		var rowsName = options.rowsName?options.rowsName:"rows",
			avgFunc = "averageAH",
			bkDown = options.bkDown?1:0,
			bkKey = options.bkKey?options.bkKey:null;
		
		exp = AHDaSimpleHelper._getFuncOnlyExpression(exp, {
			avgFunc: avgFunc,
			bkDown: bkDown,
			bkKey: bkKey,
			rowsName: rowsName
		});
	}
	
	exp = exp.replace(/\[/g, aRowName + "['");
	exp = exp.replace(/]/g, "']");
	
	return exp;
};
AHDaSimpleHelper._enhanceExpToSafer = function(exp, rowName, options) {
	var metric = options.metric,
		metrics = options.metrics,
		bkKey = options.bkKey;
	exp = AHDaSimpleHelper._replaceAllOnce(exp, rowName+'[', '(' + rowName + '[');
	var enhanceFunc;
	if (bkKey) {
		enhanceFunc = AHDaSimpleHelper._enhanceExpressionBreakDown;
	} else {
		enhanceFunc = AHDaSimpleHelper._enhanceExpressionNoBreakDown;
	}
	
	if (metric) {
		exp = enhanceFunc(exp, rowName, metric, bkKey);
	} else if (metrics && metrics.length > 0) {
		_.each(metrics, function(metric) {
			exp = enhanceFunc(exp, rowName, metric, bkKey);
		});
	}
	
	return exp;
};


AHDaSimpleHelper._replaceAllOnce = function(exp, findStr, repStr) {
	if (!exp || !findStr) {
		return;
	}
	findStr = findStr.replace(/\[/g, '\\[');
	findStr = findStr.replace(/\(/g, '\\(');
	return exp.replace(new RegExp(findStr, 'g'), repStr);
};
AHDaSimpleHelper._enhanceExpressionBreakDown = function(exp, rowName, metric, bkKey) {
	return AHDaSimpleHelper._replaceAllOnce(exp,
			'[\''+metric+'\']', 
			'[\''+metric+'\'].BreakDown[\''+bkKey+'\']?' +
			rowName + '[\''+metric+'\'].BreakDown[\''+bkKey+'\'][\''+metric+'\']:0)');
};
AHDaSimpleHelper._enhanceExpressionNoBreakDown = function(exp, rowName, metric) {
	return AHDaSimpleHelper._replaceAllOnce(exp,
			'[\''+metric+'\']',
			'[\''+metric+'\']?' +
			rowName + '[\''+metric+'\']:0)');
};

AHDaSimpleHelper._supportedFuncs = ['AHAVG'];
