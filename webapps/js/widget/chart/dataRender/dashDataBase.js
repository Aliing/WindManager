/**
 * need underscore, do not include jQuery here
 */
var ah_dashDataRender_base_data = {};
ah_dashDataRender_base_data.daBackendHelper = {
	getCertainExpFromExpConfig: function(exp, options, additionalFunc) {
		exp = AHDaSimpleHelper._getCertainExpFromExpConfig(exp, options);
		if (additionalFunc) {
			exp = additionalFunc(exp);
		}
		return exp;
	},
	getExpressionFuncs: AHDaSimpleHelper._getExpressionFuncs,
	getFuncOnlyExpression: AHDaSimpleHelper._getFuncOnlyExpression,
	getExpressionMetrics: AHDaSimpleHelper._getExpressionMetrics,
	enhanceExpToSafer: AHDaSimpleHelper._enhanceExpToSafer,
	getMetricUnit: AHDaSimpleHelper._getMetricUnit,
	selectProperUnitForSeries: AHDaSimpleHelper._selectProperUnitForSeries,
	replaceAllOnce: AHDaSimpleHelper._replaceAllOnce
};
ah_dashDataRender_base_data.dataRender = function(renderOptions) {
	var daHelper;
	var xExp,
		axis,
		overtime = false,
		chartType = 'column',
		blnPairData = false,
		data,
		blnDds = false,
		curConfig;
	if (renderOptions) {
		if (renderOptions.daHelper) {
			daHelper = renderOptions.daHelper;
		}
		xExp = renderOptions.xExp;
		axis = renderOptions.axis;
		overtime = renderOptions.overtime;
		chartType = renderOptions.chartType;
		blnPairData = renderOptions.blnPairData;
		data = renderOptions.data;
		blnDds = renderOptions.blnDds;
		curConfig = renderOptions.curConfig;
	}
	if (!daHelper) {
		daHelper = ah_dashDataRender_base_data.daBackendHelper;
	}
	
	if (isString(curConfig)) {
		curConfig = eval(curConfig);
	}
	
	var curData = eval(data);
	
	var curConfigTmpSubKey = 0;
	var curSeries = {};
	var curCategories = [];
	var curCategoriesWithMarkData = [];
	var curSerieConfigs = {};
	var curSortedSeries;
	var totalValue = 0, totalOriValue = 0;
	
	var collectedAxisValues = [];
	
	var expParsedValues = {};
	expParsedValues[BK_DEFAULT] = {};
	expParsedValues[BK_DOWN_AGG] = {};
	expParsedValues[BK_DOWN_NO_AGG] = {};
	expParsedValues[BK_DOWN_AGG_MY_DEAL] = {};
	
	function isFunction(func) {
		return typeof func == "function";
	}
	
	function isString(s) {
		return typeof s === 'string';
	}
	
	function isUndefined(obj) {
		return typeof obj == "undefined";
	}
	
	function isDefined(obj) {
		return typeof obj != "undefined";
	}
	
	function isArray(object){
		return	object && typeof object==='object' &&
	            typeof object.length==='number' &&
	            typeof object.splice==='function' &&
	            !(object.propertyIsEnumerable('length'));
	}
	
	/**
	 * common helper functions start
	 */
	var BK_DEFAULT = 1,
	BK_DOWN_AGG = 2,
	BK_DOWN_NO_AGG = 3,
	BK_DOWN_AGG_MY_DEAL = 4;

	var NO_AGG_GRP_PREFIX = "noagg";
	var DEF_NULL_VALUE = 0;
	
	var isSeriesConfigsContainsKey = function(configArg, metric) {
		return [configArg[metric]];
	};
	
	var getPaddingOrder = function(order, maxOrder) {
		var difLen = ('0'+maxOrder).length - ('0'+order).length;
		var result = order;
		if (difLen > 0) {
			for (var i = 0; i < difLen; i++) {
				result = '0' + result;
			}
		}
		return result;
	};
	var sortCurseries = function(seriesArg) {
		var curSeriesGrp = {};
		_.each(_.values(seriesArg), function(obj) {
			var keyTmp = obj.mygidx + '0' + obj.myidx;
			if (!curSeriesGrp[keyTmp]) {
				curSeriesGrp[keyTmp] = [];
			}
			curSeriesGrp[keyTmp].push(obj);
		});
		_.each(_.keys(curSeriesGrp), function(key) {
			var obj = curSeriesGrp[key];
			if (obj && obj.length > 1) {
				curSeriesGrp[key] = _.sortBy(obj, function(obj) {
					return obj.name;
				});
				var curIdx = 0;
				_.each(curSeriesGrp[key], function(obj) {
					obj.myidx2 = curIdx++;
				});
			}
		});
		delete curSeriesGrp;
		var maxgidx = 0,
			maxidx = 0,
			maxidx2 = 0;
		_.each(_.values(seriesArg), function(obj) {
			if (obj.mygidx > maxgidx) {
				maxgidx = obj.mygidx;
			}
			if (obj.myidx > maxidx) {
				maxidx = obj.myidx;
			}
			if (obj.myidx2 && obj.myidx2 > maxidx2) {
				maxidx2 = obj.myidx2;
			}
		});
		return _.sortBy(_.values(seriesArg), function(obj) {
			return (obj.type === 'line'?'1':'0') + '0' + getPaddingOrder(obj.mygidx, maxgidx) + '0' + getPaddingOrder(obj.myidx, maxidx) 
						+ '0' + (obj.myidx2?getPaddingOrder(obj.myidx2, maxidx2):getPaddingOrder('0', maxidx2));
		});
	};
	
	var averageAH = function(rows,row, bkDown, bkKey, metric) {
		var length = rows.length;
		var result = 0;
		  
		var dataTmp;
		/*var metricsTmp = getExpMetrics(exp);
		if (!metricsTmp || metricsTmp.length < 1) {
			return 0;
		}
		if (bkDown == 1) {
			for (var i = 0; i < metricsTmp.length; i++) {
				exp = daHelper.replaceAllOnce(exp, '['+metricsTmp[i]+']', 'row[i]["'+metricsTmp[i]+'"].BreakDown["'+bkKey+'"]["'+metricsTmp[i]+'"]');
			}
		} else {
			exp = daHelper.replaceAllOnce(exp, '['+metricsTmp[i]+']', 'rows[i]["'+metricsTmp[i]+'"]');
		}*/
		for (var i = 0; i < length; i++) {
			if (bkDown == 1) {
				if (rows[i][metric].BreakDown[bkKey]) {
					dataTmp = rows[i][metric].BreakDown[bkKey][metric];
				}
			} else {
				dataTmp = rows[i][metric];
			}
			if (!dataTmp) {
				dataTmp = 0;
			}
			result += dataTmp;
		}
		return result/length;
	};
	
	var blnOnlyOneBreakDown = true;
	var prepareSeriesConfigs = function(configArg) {
		if (configArg && configArg.m) {
			var seriesConfigTmp = {};
			var iBdCount = 0;
			_.each(_.keys(configArg.m), function(key) {
				var cTmp = configArg.m[key];
				if (cTmp && cTmp.length > 0) {
					_.each(cTmp, function(cTmpTmp) {
						var seriesObj = {
							name: cTmpTmp.name,
							exp: cTmpTmp.exp,
							bkOption: cTmpTmp.bkOption,
							gKey: key,
							gidx: cTmpTmp.gidx,
							idx: cTmpTmp.idx,
							myUnit: daHelper.getMetricUnit(axis, cTmpTmp.metric)
						};
						seriesConfigTmp[cTmpTmp.metric] = seriesObj;
						if (cTmpTmp.bkOption == BK_DOWN_AGG || cTmpTmp.bkOption == BK_DOWN_NO_AGG) {
							iBdCount++;
						}
					});
				}
			});
			
			blnOnlyOneBreakDown = (iBdCount < 2);
			return seriesConfigTmp;
		}
	};
	
	var generateId = function() {
		var args = arguments,
			length = args.length;
		if (length < 1) {
			return;
		}
		var str = '';
		for (var i = 0; i < length; i++) {
			str += '_' + args[i];
		}
		return str.replace(/[^0-9^a-z^A-Z]/g, '_');
	};
	/**
	 * common helper functions end
	 */
	
	var expExchange = function(exp, options) {
		var args = arguments,
			argLen = args.length;
		var func, bkType;
		if (argLen > 3) {
			func = args[2];
			bkType = args[3];
		} else if (argLen > 2) {
			if (isFunction(args[2])) {
				func = args[2];
			} else {
				bkType = args[2];
			}
		}
		if (!bkType || !expParsedValues[bkType]) {
			return daHelper.getCertainExpFromExpConfig(exp, options, func);
		}
		if (!expParsedValues[bkType][exp]) {
			var blnFirstime = true;
			if (options.callIfFirstTime) {
				exp = options.callIfFirstTime(exp, options.rowsObj, options);
				delete options.rowsObj;
				blnFirstime = false;
			}
			
			if (!blnFirstime) {
				options.noFunc = true;
			}
			expParsedValues[bkType][exp] = {
					firstime: blnFirstime,
					exp: daHelper.getCertainExpFromExpConfig(exp, options, func)
			};
		}
		
		return expParsedValues[bkType][exp].exp;
	};
	
	var dealExpFuncsAtFirstTime = function(exp, rows, options) {
		var funcs = daHelper.getExpressionFuncs(exp);
		if (funcs && funcs.length > 0) {
			var expTmp;
			_.each(funcs, function(funcl) {
				expTmp = daHelper.getFuncOnlyExpression(funcl[0], options);
				exp = exp.replace(funcl[0], eval(expTmp));
			});
		}
		return exp;
	};
	
	
	var expMetricsMap = {};
	var getExpMetrics = function(exp) {
		if (!exp) {
			return;
		}
		if (!expMetricsMap[exp]) {
			expMetricsMap[exp] = daHelper.getExpressionMetrics(exp);
		}
		return expMetricsMap[exp];
	};
	
	
	var prepareProperData = function(rowVal, value) {
		return value;
	}
	if (blnPairData) {
		prepareProperData = function(rowVal, value) {
			if (overtime) {
				return [rowVal, value];
			} else {
				return [""+rowVal, value];
			}
		};
	}
	
	if (xExp) {
		xExp = expExchange(xExp, {rowName: "rowData"});
	}
	var addCategories = function(rowData) {
		var value = eval(xExp);
		curCategories.push(value+"");
		if (typeof rowData["@bkValue"] === 'undefined') {
			curCategoriesWithMarkData.push(value + "[!<||" + value);
		} else {
			curCategoriesWithMarkData.push(value + "[!<||" + rowData["@bkValue"]);
		}
	};
	if (!xExp || blnPairData) {
		addCategories = function(rowData) {
		}
	}
	
	if (curConfig.m) {
		curSerieConfigs = prepareSeriesConfigs(curConfig);
	}
	
	var getCurSortedSeries = function() {
		if (!curSortedSeries) {
			curSortedSeries = sortCurseries(curSeries);
		}
		return curSortedSeries;
	};
	
	var prepareSpecialDataForChart = function(curSortedSeries) {
		if (curSortedSeries && chartType == 'pie') {
			curSortedSeries = prepareForPieData(curSortedSeries);
		}
		return curSortedSeries;
	};
	
	var prepareForPieData = function(curSortedSeries) {
		if (chartType != 'pie' || !curSortedSeries || curSortedSeries.length < 1) {
			return;
		}
		var result = _.extend({}, curSortedSeries[0]);
		result.myDataNode = result.myDataNode || {};
		result.myDataNode.dataBkValMap = pieDataBkValMap;
		var dataArrTmp = [];
		var oriDataTmp = [];
		if (!result.name) {
			result.name = "";
		} else {
			result.name = result.name.replace(/[\s\S]*<br\/>/g, '');
		}
		_.each(curSortedSeries, function(series) {
			if (series && series.data && series.data.length > 0) {
				for (var i = 0; i < series.data.length; i++) {
					dataArrTmp.push(series.data[i]);
					oriDataTmp.push(series.myDataNode.oriData[i]);
					totalValue += series.data[i][1];
					totalOriValue += series.myDataNode.oriData[i][1];
				}
			}
		});
		if (dataArrTmp && dataArrTmp.length > 0) {
			var dataMapTmp = _.map(dataArrTmp, 
									function(data, i) {
										return {
											data: data,
											oriData: oriDataTmp[i]
										};
									});
			var sortedDataMapTmp = _.sortBy(dataMapTmp, function(data) { return data.oriData[1]*-1;});
			result.data = [];
			result.myDataNode.oriData = [];
			_.each(sortedDataMapTmp, function(data) {
				result.data.push(data.data);
				result.myDataNode.oriData.push(data.oriData);
			});
		}
		return [result];
	};
	
	var pieDataBkValMap = {};
	var paddingDataToCertainSeries = function(key, bkType, configKey, seriesName, optionsArg) {
		if (!configKey) {
			configKey = key;
		}
		if (!curSeries[key]) {
			getCertainCurSeries(key, bkType, configKey, seriesName);
		}
		if (!curSeries[key]) {
			return;
		}
		if (!curSeries[key].data) {
			curSeries[key].data = [];
		}
		if (!curSeries[key].topoArr) {
			curSeries[key].topoArr = [];
		}
		var dataLen = curSeries[key].data.length;
		if (dataLen < curRowDataPos - 1) {
			var axisValLen = collectedAxisValues.length;
			for (var i = dataLen; i < curRowDataPos - 1; i++) {
				if (i >= axisValLen) {
					break;
				}
				curSeries[key].data.push(prepareProperData(collectedAxisValues[i], DEF_NULL_VALUE));
			}
		}
		
		if (optionsArg && optionsArg.rowData) {
			var bkOptions = getCertainBkConditions(key, bkType, configKey, optionsArg);
			optionsArg["bkType"] = bkOptions["@bkType"];
			optionsArg["bkValue"] = bkOptions["@bkValue"];
			optionsArg["isSwitch"] = bkOptions["@isSwitch"];
			if (!optionsArg.node) {
				optionsArg.node = bkOptions["@topoId"];
			}
			if (chartType == 'table') {
				curSeries[key].topoArr.push(bkOptions["@topoId"]);
			}
			if (chartType == 'pie') {
				pieDataBkValMap[optionsArg.rowData[axis]] = bkOptions;
			}
		}
		prepareSeriesDataNode(key, optionsArg);
	};
	var getCertainBkConditions = function(key, bkType, configKey, optionsArg) {
		optionsArg = optionsArg || optionsArg;
		if (bkType == BK_DOWN_AGG || bkType == BK_DOWN_NO_AGG) {
			if (optionsArg.node && optionsArg.rowData && optionsArg.rowData[configKey] && optionsArg.rowData[configKey].BreakDown) {
				return optionsArg.rowData[configKey].BreakDown[optionsArg.node];
			}
		}
		return optionsArg.rowData;
	};
	var getValueOrDefaultIfUnDefined = function(value, defValue) {
		if (isUndefined(value)) {
			return defValue;
		}
		return value;
	};
	var prepareSeriesDataNode = function(key, optionsArg) {
		if (curSeries[key].hasOwnProperty('myDataNode')) {
			if (optionsArg) {
				curSeries[key].myDataNode["lid"] = getValueOrDefaultIfUnDefined(optionsArg.node, -1);
				curSeries[key].myDataNode["bkType"] = getValueOrDefaultIfUnDefined(optionsArg.bkType, 1);
				curSeries[key].myDataNode["bkValue"] = getValueOrDefaultIfUnDefined(optionsArg.bkValue, null);
				curSeries[key].myDataNode["isSwitch"] = getValueOrDefaultIfUnDefined(optionsArg.isSwitch, false);
			}
		}
	};
	
	var getCertainCurSeriesDef = function(key, configArg) {
		var result = {
				name: configArg.name,
				id: generateId(key, configArg.gKey),
				type: chartType,
				stack: configArg.gKey,
				stacking: 'normal',
				keyN: key,
				mygidx: configArg.gidx,
				myidx: configArg.idx,
				myUnit: configArg.myUnit,
				myDataNode: {}
			};
		return result;
	};
	var getCertainCurSeriesBkWithAgg = function(key, configKey, configArg, seriesName) {
		var chartTypeTmp = chartType;
		/*if (chartType == 'line') {
			chartTypeTmp = 'area';
		}*/
		if (isUndefined(seriesName)) {
			seriesName = "Undefined";
		}
		var result = {
				name: blnOnlyOneBreakDown?seriesName: seriesName + "<br/>" + configArg.name,
				id: generateId(configKey, configArg.gKey, curConfigTmpSubKey),
				type: chartTypeTmp,
				stack: configArg.gKey,
				stacking: 'normal',
				keyN: configKey,
				mygidx: configArg.gidx,
				myidx: configArg.idx,
				myUnit: configArg.myUnit,
				myDataNode: blnOnlyOneBreakDown?{seriesBaseName: configArg.name}:{}
			};
		curConfigTmpSubKey++;
		return result;
	};
	var getCertainCurSeriesBkWithoutAgg = function(key, configKey, configArg, seriesName) {
		var chartTypeTmp = chartType;
		/*if (chartType == 'line') {
			chartTypeTmp = 'area';
		}*/
		if (isUndefined(seriesName)) {
			seriesName = "Undefined";
		}
		var result = {
				name: blnOnlyOneBreakDown?seriesName: seriesName + "<br/>" + configArg.name,
				id: generateId(configKey, configArg.gKey, curConfigTmpSubKey),
				type: chartTypeTmp,
				stack: generateId(configKey, NO_AGG_GRP_PREFIX, curConfigTmpSubKey),
				stacking: 'normal',
				keyN: configKey,
				mygidx: configArg.gidx,
				myidx: configArg.idx,
				myUnit: configArg.myUnit,
				myDataNode: blnOnlyOneBreakDown?{seriesBaseName: configArg.name}:{}
			};
		curConfigTmpSubKey++;
		return result;
	};
	var getCertainCurSeriesBkWithAggMyDeal = function(key, configArg) {
		var result = {
				name: configArg.name,
				id: generateId(key, configArg.gKey),
				type: chartType,
				stack: configArg.gKey,
				stacking: 'normal',
				keyN: key,
				mygidx: configArg.gidx,
				myidx: configArg.idx,
				myUnit: configArg.myUnit
			};
		return result;
	};
	var getCertainCurSeries = function(key, bkType, configKey, seriesName) {
		if (!configKey) {
			configKey = key;
		}
		if (!curSeries[key]) {
			var configTmp = curSerieConfigs[configKey];
			if (!configTmp) {
				return;
			}
			if (bkType == BK_DEFAULT) {
				curSeries[key] = getCertainCurSeriesDef(key, configTmp);
			} else if (bkType == BK_DOWN_AGG) {
				curSeries[key] = getCertainCurSeriesBkWithAgg(key, configKey, configTmp, seriesName);
			} else if (bkType == BK_DOWN_NO_AGG) {
				curSeries[key] = getCertainCurSeriesBkWithoutAgg(key, configKey, configTmp, seriesName);
			} else if (bkType == BK_DOWN_AGG_MY_DEAL) {
				curSeries[key] = getCertainCurSeriesBkWithAggMyDeal(key, configTmp);
			}
		}
		
		return curSeries[key];
	};
	
	var calExpForBreakDownItem = function(dataArg, rows, metrics, exp, idArg) {
		exp = daHelper.enhanceExpToSafer(
				expExchange(exp, {rowName: "dataArg", bkDown: true, bkKey: idArg, rowsObj: rows, callIfFirstTime: dealExpFuncsAtFirstTime}),
				"dataArg",
				{
					metrics: metrics,
					bkKey: idArg
				}
			);
		return {
			name: getBreakDownName(dataArg, metrics, idArg),
			value: eval(exp)
		};
	};
	
	var bkNamesMap = {};
	var getBreakDownName = function(dataArg, metrics, idArg) {
		if (!bkNamesMap[idArg]) {
			bkNamesMap[idArg] = idArg;
			for (var i = 0; i < metrics.length; i++) {
				if (dataArg[metrics[i]] && dataArg[metrics[i]].BreakDown[idArg]) {
					bkNamesMap[idArg] = dataArg[metrics[i]].BreakDown[idArg].name;
					break;
				}
			}
		}
		return bkNamesMap[idArg];
	};
	var dataPrepareForARow = function(rowData, rows, oriExp) {
		var metricsTmp = getExpMetrics(oriExp);
		var blnBreakDown = false;
		if (metricsTmp 
				&& metricsTmp.length > 0
				&& rowData[metricsTmp[0]]
				&& rowData[metricsTmp[0]].BreakDown) {
			blnBreakDown = true;
		}
		var resultData;
		var resultExp = oriExp;
		if (!blnBreakDown) {
			resultData = rowData;
		} else {
			resultData = null;
			var dataObjs = {};
			var dataObjKeys = [];
			_.each(metricsTmp, function(metric) {
				if (rowData[metric] && rowData[metric].BreakDown) {
					dataObjs[metric] = rowData[metric];
					dataObjKeys.push(_.keys(dataObjs[metric].BreakDown));
				}
			});
			var ids = _.uniq(_.flatten(dataObjKeys));
			if (ids && ids.length > 0) {
				resultData = {};
				_.each(ids, function(idTmp) {
					resultData[idTmp] = calExpForBreakDownItem(dataObjs, rows, metricsTmp, oriExp, idTmp);
				});
				resultExp = '[value]';
			}
		}
		return {
			data: resultData,
			newExp: resultExp
		}
	};
	
	var checkValueToSeriesData = function(value) {
		if (!value || isNaN(value) || !isFinite(value)) {
			return DEF_NULL_VALUE;
		}
		return value;
	};
	if (chartType === 'table' || chartType === 'list') {
		checkValueToSeriesData = function(value) {
			return value;
		}
	}
	var addToSeriesDataArray = function(axisVal, key, value) {
		if (isUndefined(axisVal) || axisVal === "") {
			axisVal = " ";
		}
		value = checkValueToSeriesData(value);
		curSeries[key].data.push(prepareProperData(axisVal, value));
	};
	
	var curRowDataPos = 1;
	var addPointDataToSeriesDef = function(rowData, rows, key, axisVal, configArg) {
		paddingDataToCertainSeries(key, configArg.bkOption, key, configArg.name, {rowData: rowData});
		var exp = expExchange(configArg.exp, {rowName: "rowData", rowsObj: rows, callIfFirstTime: dealExpFuncsAtFirstTime}, configArg.bkOption);
		addToSeriesDataArray(axisVal, key, eval(exp));
	};
	var addPointDataToSeriesBkWithAgg = function(rowData, rows, key, axisVal, configArg) {
		var metrics = getExpMetrics(configArg.exp);
		var newData = dataPrepareForARow(rowData, rows, configArg.exp);
		var dataTmp = newData.data;
		var expTmp = newData.newExp;
		expTmp = expExchange(expTmp, {rowName: "data", noFunc: true}, configArg.bkOption);
		var axisValTmp;
		var seriesNameTmp = configArg.name;
		var data;
		_.each(_.keys(dataTmp), function(keyId) {
			seriesNameTmp = getBreakDownName(rowData, metrics, keyId);
			if (!overtime) {
				axisValTmp = seriesNameTmp;
			} else {
				axisValTmp = axisVal;
			}
			paddingDataToCertainSeries(keyId+key, configArg.bkOption, key, seriesNameTmp, {
				node: keyId,
				rowData: rowData
			});
			data = dataTmp[keyId];
			addToSeriesDataArray(axisValTmp, keyId+key, eval(expTmp));
		});
	};
	var addPointDataToSeriesBkWithoutAgg = function(rowData, rows, key, axisVal, configArg) {
		addPointDataToSeriesBkWithAgg(rowData, rows, key, axisVal, configArg);
	};
	var addPointDataToSeriesBkWithAggMyDeal = function(rowData, rows, key, axisVal, configArg) {
		paddingDataToCertainSeries(key, configArg.bkOption, key, configArg.name, {rowData: rowData});
		var exp = expExchange(configArg.exp, {rowName: "rowData", rowsObj: rows, callIfFirstTime: dealExpFuncsAtFirstTime}, function(expArg){
			return expArg + ".original";
		}, configArg.bkOption);
		addToSeriesDataArray(axisVal, key, eval(exp));
	};
	var addPointDataToSeries = function(rowData, rows) {
		var axisVal = getRowAxisValue(rowData);
		var addPointDataFunc;
		
		for (var key in rowData) {
			var configTmp = curSerieConfigs[key];
			if (!configTmp) {
				continue;
			}
			var blnRealBk = true;
			if (rowData[key] && _.isObject(rowData[key]) && _.isEmpty(rowData[key].BreakDown)) {
				blnRealBk = false;
			}
			
			var bkType = configTmp.bkOption;
			if (bkType == BK_DEFAULT) {
				addPointDataFunc = addPointDataToSeriesDef;
			} else if (bkType == BK_DOWN_AGG && blnRealBk) {
				addPointDataFunc = addPointDataToSeriesBkWithAgg;
			} else if (bkType == BK_DOWN_NO_AGG && blnRealBk) {
				addPointDataFunc = addPointDataToSeriesBkWithoutAgg;
			} else if (bkType == BK_DOWN_AGG_MY_DEAL || (!blnRealBk && curConfig.blnBkNoDataTotal)) {
				addPointDataFunc = addPointDataToSeriesBkWithAggMyDeal;
			}
			
			addPointDataFunc.apply(this, [rowData, rows, key, axisVal, configTmp]);
			//if (curConfig.blnBkNoDataTotal && blnRealBk) {
			//	addPointDataToSeriesBkWithAggMyDeal.apply(this, [rowData, rows, key, axisVal, configTmp]);
			//}
		}
		curRowDataPos++;
		collectedAxisValues.push(axisVal);
	};
	var getRowAxisValue = function(rowData) {
		var axisVal = rowData[axis];
		if (overtime && isString(axisVal)) {
			if (axisVal.indexOf('T') > 0) {
				axisVal = Date.parse(axisVal);
			}
		}
		return axisVal;
	};
	
	var appendingDataToAllSeries = function() {
		_.each(_.keys(curSeries), function(key) {
			paddingDataToCertainSeries(key);
		});
	};
	
	
	var prepareChartValueRange = function() {
		var gGroupValues = {};
		var sortedSeries = getCurSortedSeries();
		var seriesLen = sortedSeries.length;
		
		var gIdTmp;
		var valueTmp;
		var seriesObj;
		for (var curPointPos = 1; curPointPos < curRowDataPos; curPointPos++) {
			for (var i = seriesLen-1; i >= 0; i--) {
				seriesObj = sortedSeries[i];
				gIdTmp = seriesObj.stack;
				if (!gGroupValues[gIdTmp]) {
					gGroupValues[gIdTmp] = {};
				}
				if (!gGroupValues[gIdTmp][curPointPos]) {
					gGroupValues[gIdTmp][curPointPos] = {
						value: 0
					};
				}
				valueTmp = seriesObj.data[curPointPos-1];
				if (isArray(valueTmp)) {
					valueTmp = valueTmp[1];
				}
				if (isString(valueTmp)) {
					continue;
				}
				if (isUndefined(gGroupValues[gIdTmp][curPointPos].min)) {
					gGroupValues[gIdTmp][curPointPos].min = valueTmp;
				} else if (valueTmp < 0) {
					gGroupValues[gIdTmp][curPointPos].min += valueTmp;
				}
				gGroupValues[gIdTmp][curPointPos].value += valueTmp;
			}
		}
		
		var min;
		var max;
		_.each(_.values(gGroupValues), function(obj) {
			if (obj) {
				_.each(_.values(obj), function(valObj) {
					if (isUndefined(min) || valObj.min < min) {
						min = valObj.min;
					}
					if (isUndefined(max) || valObj.value > max) {
						max = valObj.value;
					}
				});
			}
		});
		
		if (isDefined(min) && isDefined(max)) {
			if (min > 0 && min < 10) {
				min = 0;
			}
			if (max < 0 && max > -10) {
				max = 0;
			}
			return {
				min: min,
				max: max
			}
		}
	};
	
	var doSeriesDataScale = function(unitHelper) {
		_.each(_.keys(curSeries), function(key) {
			doSingleSeriesDataScale(curSeries[key], unitHelper);
		});
	};
	var doSingleSeriesDataScale = function(curSeriesTmp, unitHelper) {
		if ("unit" in unitHelper) {
			curSeriesTmp.myUnit = unitHelper.unit; 
		}
		if ("formatUnit" in unitHelper) {
			curSeriesTmp.myfUnit = unitHelper.formatUnit;
		}
		if (unitHelper.func && curSeriesTmp.data) {
			var dataTmp = [];
			if (chartType == 'pie' || overtime) {
				_.each(curSeriesTmp.data, function(data) {
					dataTmp.push([data[0], parseFloat(unitHelper.func(data[1]))]);
				});
			} else {
				if (unitHelper.forceStr) {
					_.each(curSeriesTmp.data, function(data) {
						dataTmp.push(unitHelper.func(data));
					});
				} else {
					_.each(curSeriesTmp.data, function(data) {
						dataTmp.push(parseFloat(unitHelper.func(data)));
					});
				}
			}
			curSeriesTmp.data = dataTmp;
		}
	};
	
	var addPointsToSeries = function(rows) {
		if (!rows || rows.length < 1) {
			return;
		}
		
		_.each(rows, function(rowData) {
			addPointDataToSeries(rowData, rows);
			addCategories(rowData);
		});
		
		appendingDataToAllSeries();
		
		var valRange = prepareChartValueRange();
		
		// currently, only work for pie chart
		if (chartType === "pie") {
			_.each(getCurSortedSeries(), function(curSeriesTmp) {
				curSeriesTmp.myDataNode = curSeriesTmp.myDataNode || {};
				curSeriesTmp.myDataNode.oriData = curSeriesTmp.data;
			});
		}
		if (chartType == "table" || chartType == "list") {
			/*var getMaxValueOfSeries = function(seriesTmp) {
				if (seriesTmp && seriesTmp.data) {
					if (!isString(seriesTmp.data[0])) {
						return _.max(seriesTmp.data);
					}
				}
			};
			_.each(_.values(curSeries), function(curSeriesTmp) {
				var unitHelper = daHelper.selectProperUnitForSeries(getMaxValueOfSeries(curSeriesTmp), curSeriesTmp.myUnit);
				if (!unitHelper) {
					return;
				}
				unitHelper.forceStr = true;
				doSingleSeriesDataScale(curSeriesTmp, unitHelper);
			});*/
		} else if (valRange && ('max' in valRange)) {
			var seriesTmp = getCurSortedSeries();
			if (!seriesTmp || seriesTmp.length <= 0) {
				return;
			}
			var baseUnit = seriesTmp[0].myUnit;
			var unitHelper = daHelper.selectProperUnitForSeries(valRange.max, baseUnit);
			if (!unitHelper) {
				return;
			}
			doSeriesDataScale(unitHelper);
		}
		
		return {
			curSortedSeries: prepareSpecialDataForChart(getCurSortedSeries()),
			valRange: null,
			curCategories: curCategories,
			categoriesWithMark: curCategoriesWithMarkData,
			totalValue: totalValue,
			totalOriValue: totalOriValue
		}
	};
	
	return addPointsToSeries(curData);
};