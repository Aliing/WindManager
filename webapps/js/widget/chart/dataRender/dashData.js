(function($, _){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		$el = ARC.$el,
		isObject = ARC.isObject,
		isString = ARC.isString,
		pFloat = ARC.pFloat,
		replaceSpecialSymbol = ARC.replaceSpecialSymbol,
		isUndefined = ARC.isUndefined;
	
	function isString(s) {
		return typeof s === 'string';
	}
	
	var TABLE_ROWS_SLICE = 20;
	var FIXED_TABLE_HEIGHT = 275;
	var TABLE_BOTTOM_TOOLBAR_HEIGHT = 20;
	var FIX_TABLE_HEAD_JUDGE_HEIGHT = 240;
	
	var _sendNoDataEvent = function(chart, msg) {
		chart.noDataContainer.show(msg);
		ARC.Events.notifyAll(chart._EVENT_COMMON_TYPE, 'nodata', chart);
	};
	var _sendHasDataEvent = function(chart) {
		ARC.Events.notifyAll(chart._EVENT_COMMON_TYPE, 'hasdata', chart);
	};
	var dashDataRender = function(chart, options, data) {
		//if (!chart.isFirstTimeDataCall()) {
		chart.showLoadingTip(chart.getCurrentShownContainer(), false);
		//}
		
		var curDa = chart.currentDashboard;
		if (curDa == null) {
			_sendNoDataEvent(chart, "Sorry, Failed to render this chart.");
			return;
		}
		if (data.rs && !data.rs.resultStatus) {
			_sendNoDataEvent(chart, data.rs.errMsg);
			return;
		}
		if (data.exception) {
			_sendNoDataEvent(chart, data.exception);
			return;
		}
		var curData = eval(data.data);
		if (!curData || curData.length < 1) {
			var errMsg = Aerohive.lang.chart.nodata.nodata;
			if (data.exception1) {
				errMsg = data.exception1;
			} 
			if (data.desc && 'dc' in data.desc && data.desc.dc < 1) {
				errMsg = Aerohive.lang.chart.nodata.nodevice;
			}
			_sendNoDataEvent(chart, errMsg);
			return;
		}
		chart.noDataContainer.hide();
		_sendHasDataEvent(chart);
		chart.tips.addError('', 'all', -1);
		
		chart.tmpOptions = chart.tmpOptions || {};
		chart.tmpOptions.whenRender = chart.tmpOptions.whenRender || {};
		chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE = TABLE_ROWS_SLICE;
		if (options.displayCtl && options.displayCtl.fullTableItems) {
			chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE = 10000;
			chart.tmpOptions.whenRender.fullDisplayed = true;
		}
		chart.tmpOptions.whenRender.cur_FIXED_TABLE_HEIGHT = options.chartHeight;
		var blnTblBottomOpOut = true;
		
		var curConfig = curDa.getReportConfig(chart.reportId);
		curConfig.desc = data.desc;
		var axis = curConfig.axis;
		var xExp = curConfig.xExp;
		var axisDisName = curConfig.xname;
		var curCategories = [];
		var overtime = curDa.daHelper.isOvertime(axis, chart);
		var chartType = curDa.daHelper.getWidgetChartType(axis, chart);
		var axisObj = curDa.daHelper.getAxis(axis);
		var chartInverted = curDa.daHelper.isWidgetChartInverted(axisObj, chart);
		var blnPairData = false;
		if (overtime || chartType === 'pie') {
			blnPairData = true;
		}
		var blnDds = false;
		if (curConfig && curConfig.w) {
			blnDds = curConfig.w.blnDds;
		}
		var daHelper = curDa.daHelper;
		var blnCanMonitor = daHelper.checkWhetherMonitorItemInTimeRange(curConfig);
		
		var curSortedSeries;
		
		var dataCalOptions = {
			daHelper: curDa.daHelper,
			axis: axis,
			xExp: xExp,
			overtime: overtime,
			chartType: chartType,
			blnPairData: blnPairData,
			data: data.data,
			blnDds: blnDds,
			curConfig: curConfig
		};
		
		var setChartCategories = function() {
			chart.hcChart.xAxis[0].setCategories(curCategories);
		};
		if (blnPairData) {
			setChartCategories = function(){};
		}
		
		var adjustChartValueRange = function() {
			if (valRange) {
				if (!isUndefined(valRange.min) && !isUndefined(valRange.max)) {
					chart.yAxisValueRangeMin = valRange.min;
					chart.yAxisValueRangeMax = valRange.max;
					chart.hcChart.yAxis[0].setExtremes(valRange.min, valRange.max, false);
				}
			}
		};
		if (!chart.isRenderedAsChart()) {
			adjustChartValueRange = function(){
			};
		}
		
		var __getCategoryInfoDict = function(category) {
			var values = getValueAndMarkFromCategory(category);
			var textTmp = category;
			var valueTmp;
			if (values) {
				textTmp = values[0];
				if (values.length > 1) {
					valueTmp = values[1];
				} else {
					valueTmp = textTmp;
				}
			}
			return {
				text: textTmp,
				value: valueTmp
			}
		};
		var getCategoryShowStr = function(category) {
			return category;
		};
		var tzOffsetValue = chart.timeOffset || 0;
		if (overtime) {
			//var tzOffsetValue = new Date().getTimezoneOffset()*60000;
			var dateValueTmp;
			if (chartInverted) {
				getCategoryShowStr = function(category) {
					dateValueTmp = chart.getDateStringWithTimeZone(category + tzOffsetValue);
					if (dateValueTmp) {
						dateValueTmp = dateValueTmp.replace(' ', '<br/>');
					}
					return dateValueTmp;
				};
			} else {
				getCategoryShowStr = function(category) {
					return chart.getDateStringWithTimeZone(category + tzOffsetValue);
				};
			}
		}
		var getRealDataStrFromMetricObj = function(data, metricObj) {
			if (metricObj && metricObj.presentation 
					&& metricObj.presentation.databased
					&& data in metricObj.presentation.databased
					&& 'text' in metricObj.presentation.databased[data]) {
				return metricObj.presentation.databased[data].text;
			}
			return data;
		};
		var getDataValueShown = function(dataPoint, metricObj) {
			if (metricObj && metricObj.dataType == "time" && !isString(dataPoint)) {
				return chart.getDateStringWithTimeZone(dataPoint + tzOffsetValue);
			} else {
				return getRealDataStrFromMetricObj(dataPoint, metricObj);
			}
		};
		if (blnPairData) {
			getDataValueShown = function(dataPoint, metricObj) {
				if (dataPoint) {
					if (metricObj && metricObj.dataType == "time" && !isString(dataPoint)) {
						return chart.getDateStringWithTimeZone(dataPoint[1] + tzOffsetValue);
					} else {
						return getRealDataStrFromMetricObj(dataPoint[1], metricObj);
					}
				}
				return "";
			};
		}
		
		var getCurSortedSeries = function() {
			return curSortedSeries;
		};
		
		var getCategories = function() {
			var sortedSeries = getCurSortedSeries();
			if (sortedSeries
					&& sortedSeries.length > 0
					&& sortedSeries[0]
					&& sortedSeries[0].data
					&& sortedSeries[0].data.length > 0
					&& $.isArray(sortedSeries[0].data[0])) {
				var categoriesTmp = [];
				_.each(sortedSeries[0].data, function(obj) {
					if (obj) {
						categoriesTmp.push(obj[0]);
					} else {
						categoriesTmp.push(null);
					}
				});
				return categoriesTmp;
			} else {
				return curCategories;
			}
		};
		
		var getValueAndMarkFromCategory = function(value) {
			if (value) {
				return value.split("[!<||");
			}
		};
		var getCertainDataWithUnit = function(data, unit) {
			var unitHelper = daHelper.selectProperUnitForSeries(data, unit);
			var result = data;
			if (unitHelper) {
				if (unitHelper.func) {
					result = unitHelper.func(data);
				}
				if (unitHelper.formatUnit) {
					result += unitHelper.formatUnit;
				}
			}
			return result;
		};
		var isHasReportAdditionalSettings = function() {
			return "REPORT_ADDITIONAL" in win;
		};
		var isWithTablePostColumns = function() {
			return isHasReportAdditionalSettings()
					&& REPORT_ADDITIONAL[axis]
					&& REPORT_ADDITIONAL[axis].table
					&& REPORT_ADDITIONAL[axis].table.postColumn
					&& REPORT_ADDITIONAL[axis].table.postColumn.length > 0;
		};
		var blnPostColumns = isWithTablePostColumns();
		var tblAdditionalColumnEventMap = {};
		var tblAdditionalRowEventMap = {
			events: {},
			options: {}
		};
		var tblAdditionalColumnEvent = {
			addTitle: function(titleObj, idx) {
				var result = titleObj.title.html(titleObj.title.text);
				var addi_click = _.find(result.split(" "), function(subText) {
					if (subText) {
						return subText.indexOf("data-addi-click") > -1;
					}
				});
				if (addi_click) {
					var addi_clicks = addi_click.split("=");
					if (addi_clicks.length > 0) {
						var strTmp = addi_clicks[1].trim();
						if (strTmp && strTmp.length > 2) {
							strTmp = strTmp.substr(1, strTmp.length - 2);
						}
						var addi_click_mark = 'data-addi-click="' + idx + ' ' + strTmp + '"';
						result = result.replace(addi_click, addi_click_mark);
						tblAdditionalColumnEventMap[idx + ' ' + strTmp] = titleObj.title[strTmp];
					}
				}
				return result;
			},
			addRowItem: function(itemObj, idx, rowIdx, options) {
				var result = itemObj.data.html(itemObj.data.text);
				var addi_click = _.find(result.split(" "), function(subText) {
					if (subText) {
						return subText.indexOf("data-addi-click") > -1;
					}
				});
				if (addi_click) {
					var addi_clicks = addi_click.split("=");
					if (addi_clicks.length > 0) {
						var strTmp = addi_clicks[1].trim();
						if (strTmp && strTmp.length > 2) {
							strTmp = strTmp.substr(1, strTmp.length - 2);
						}
						var addi_click_mark = 'data-addi-click="' + idx + ' ' + rowIdx + ' ' + strTmp + '"';
						result = result.replace(addi_click, addi_click_mark);
						tblAdditionalRowEventMap.events[idx + ' ' + strTmp] = itemObj.data[strTmp];
						tblAdditionalRowEventMap.options[idx + ' ' + rowIdx] = options;
					}
				}
				return result;
			}
		};
		var addAdditionalColumnTitle = function() {
			if (!blnPostColumns) {
				return "";
			}
			var result = "";
			_.each(REPORT_ADDITIONAL[axis].table.postColumn, function(obj, idx) {
				result += "<th class='daTbString daTitleAddi'><span>" + tblAdditionalColumnEvent.addTitle(obj, idx) + "</span><div class='sort_mark'></div></th>";
			});
			return result;
		};
		var addAdditionalColumnRowItem = function(rowIdx, options) {
			if (!blnPostColumns) {
				return "";
			}
			var result = "";
			_.each(REPORT_ADDITIONAL[axis].table.postColumn, function(obj, idx) {
				result += "<td class='daTblCellData'>" + tblAdditionalColumnEvent.addRowItem(obj, idx, rowIdx, options) + "</td>";
			});
			return result;
		};
		var renderContentInTableCommon = function() {
			var sortedSeries = getCurSortedSeries();
			chart.oriSeriesData.series = sortedSeries;
			var datas = _.map(sortedSeries, function(obj){
				return {
					data: obj.data,
					unit: obj.myUnit,
					funit: obj.myfUnit,
					topoArr: obj.topoArr || []
				};
			});
			var categoriesTmp = getCategories();
			var html = "";
			html += "<div class='daTblContentContainer'><table class='view data_table' width='100%' border='0' cellspacing='0' cellpadding='0'>";
			html += "<thead><tr>";
			html += "<th class='daTbString'><span>" + axisDisName + "</span><div class='sort_mark'></div></th>";
			var styleStr = "daTbNumber";
			_.each(sortedSeries, function(obj) {
				var curSeriesTmp = curDa.daHelper.getMetric(axis, obj.keyN);
				if (isString(curSeriesTmp.validate)) {
					styleStr = "daTbString";
				} else {
					styleStr = "daTbNumber";
				}
				html += "<th class='"+styleStr+"'><span>" + obj.name + "</span><div class='sort_mark'></div></th>";
			});
			html += addAdditionalColumnTitle();
			html += "</tr></thead>";
			html += "<tbody>";
			var curIdx = 0;
			var maxLen = datas[0].data.length;
			var tdCount = datas.length;
			for (var i = 0; i < maxLen; i++) {
				html += "<tr class='" + (i%2==0?"odd":"even") + "'>";
				
				var titleStyle = "daAxisTitle";
				var titleText = getCategoryShowStr(categoriesTmp[i]);
				var categoryInfo = __getCategoryInfoDict(titleText);
				var textTmp = categoryInfo.text;
				var valueTmp = categoryInfo.value;
				if (curDa.daHelper.isChartDeviceLinkable(curConfig) && curDa.opAllowed.deviceLink) {
					titleStyle = "daAxisTitleLinkable1";
					titleText = "<a href='javascript:void(0);' onclick='javascript: AhReportChart.Chart.xAxisLabelClickEvent(event,"
									+ "\""+ chart.container +"\", "
									+ "\""+ replaceSpecialSymbol(textTmp) +"\", "
									+ "\""+ replaceSpecialSymbol(valueTmp) +"\");'>" + ARC.escapeHtmlTag(textTmp) + "</a>";
				} else if (curDa.daHelper.isChartMonitorable(curConfig) && curDa.opAllowed.monitor) {
					titleStyle = "daAxisTitleLinkable2";
					if (blnCanMonitor) {
						titleText = "<a href='javascript:void(0);' onclick='javascript: AhReportChart.Chart.xAxisLabelClickEvent(event,"
										+ "\""+ chart.container +"\", "
										+ "\""+ replaceSpecialSymbol(textTmp) +"\", "
										+ "\""+ replaceSpecialSymbol(valueTmp) +"\");'>" + ARC.escapeHtmlTag(textTmp) + "</a>";
					} else {
						titleText = "<a href='javascript:void(0);' onmouseover='javascript: AhReportChart.Chart.xAxisLabelHoverNoClickEvent(event,"
										+ "\""+ chart.container +"\");' onmouseout='javascript: AhReportChart.Chart.xAxisLabelHoverNoClickEvent(event,"
										+ "\""+ chart.container +"\", true);'>" + ARC.escapeHtmlTag(textTmp) + "</a>";
					}
				} else {
					titleText = ARC.escapeHtmlTag(textTmp);
				}
				html += "<td class='" + titleStyle + "'>" + titleText + "</td>";
				var dataTmp;
				var dataStrTmp;
				for (var j = 0; j < datas.length; j++) {
					dataTmp = datas[j].data;
					var metricObjTmp = curDa.daHelper.getMetric(axis, sortedSeries[j].keyN);
					dataStrTmp = getDataValueShown(dataTmp[i], metricObjTmp);
					var oriDataTmp = "<input class='data_ori' type='hidden' value='" + dataStrTmp + "' data_kind_str=" + isString(metricObjTmp.validate) + "/>";
					if (datas[j].unit) {
						dataStrTmp = getCertainDataWithUnit(dataStrTmp, datas[j].unit);
					}
					
					dataStrTmp = '<span class="data_shown">' + dataStrTmp + '</span>';
					var linkable = curDa.daHelper.isChartMetricValueLinkable(curConfig, sortedSeries[j].keyN);
					if (datas[j].topoArr.length > 0 && linkable) {
						var topoIdTmp = (i > datas[j].topoArr.length - 1 ? -1 : datas[j].topoArr[i]);
						if (topoIdTmp > 0) {
							var link = curDa.daHelper.getFormattedLinkUrl(linkable, {
								'topo_id': topoIdTmp
								});
							dataStrTmp = '<a href="' + link + '">' + dataStrTmp + '</a>';
						}
					}
					html += "<td class='daTblCellData'>" + dataStrTmp + oriDataTmp + "</td>";
				}
				html += addAdditionalColumnRowItem(i, {
						bkValue: replaceSpecialSymbol(valueTmp),
						name: replaceSpecialSymbol(textTmp)
					});
				html += "</tr>";
			}
			if (blnTblBottomOpOut) {
				html += "</tbody></table>";
				html += addPageOperationForTable(chart, tdCount, maxLen);
				html += "</div>";
			} else {
				html += addPageOperationForTable(chart, tdCount, maxLen);
				html += "</tbody>";
				html += "</table></div>";
			}
			
			chart.oriSeriesData.len = maxLen;
			return html;
		};
		
		var renderContentInTableInverted = function() {
			var sortedSeries = getCurSortedSeries();
			chart.oriSeriesData.series = sortedSeries;
			var html = "";
			html += "<div class='daTblContentContainer'><table class='view data_table' border='0' cellspacing='0' cellpadding='0'>";
			html += "<thead><tr>";
			html += "<th class='daTbString'>" + axisDisName + "</th>";
			var styleStr = "daTbString";
			var categoriesTmp = getCategories();
			_.each(categoriesTmp, function(category) {
				html += "<th class='"+styleStr+"'>" + __getCategoryInfoDict(getCategoryShowStr(category)).text + "</th>";
			});
			
			html += "</tr></thead>";
			html += "<tbody>";
			var blnDeviceLink = curDa.daHelper.isChartDeviceLinkable(curConfig) && curDa.opAllowed.deviceLink,
				blnMonitorLink = curDa.daHelper.isChartMonitorable(curConfig) && curDa.opAllowed.monitor && blnCanMonitor;
			var curPos = 0;
			var tdCount = sortedSeries[0].data.length;
			var maxLen = sortedSeries.length;
			_.each(sortedSeries, function(obj) {
				html += "<tr class='" + (curPos++%2==0?"odd":"even") + "'>";
				var curSeriesTmp = curDa.daHelper.getMetric(axis, obj.keyN);
				var titleStyle = "daAxisTitle";
				var titleText = obj.name;
				var categoryInfo = __getCategoryInfoDict(titleText);
				var textTmp = categoryInfo.text;
				var valueTmp = categoryInfo.value;
				if (obj.myDataNode) {
					valueTmp = obj.myDataNode.bkValue;
				}
				if (blnDeviceLink) {
					titleStyle = "daAxisTitleLinkable1";
					titleText = "<a href='javascript:void(0);' onclick='javascript: AhReportChart.Chart.xAxisLabelClickEvent(event,"
						+ "\""+ chart.container +"\", "
						+ "\""+ replaceSpecialSymbol(textTmp) +"\", "
						+ "\""+ replaceSpecialSymbol(valueTmp) +"\");'>" + ARC.escapeHtmlTag(textTmp) + "</a>";
				} else if (blnMonitorLink) {
					titleStyle = "daAxisTitleLinkable2";
					titleText = "<a href='javascript:void(0);' onclick='javascript: AhReportChart.Chart.xAxisLabelClickEvent(event,"
						+ "\""+ chart.container +"\", "
						+ "\""+ replaceSpecialSymbol(textTmp) +"\", "
						+ "\""+ replaceSpecialSymbol(valueTmp) +"\");'>" + ARC.escapeHtmlTag(textTmp) + "</a>";
				}
				html += "<td class='" + titleStyle + "'>" + ARC.escapeHtmlTag(textTmp) + "</td>";
				var blnDealTopoLink = "topoArr" in obj;
				var curPosTmp = 0;
				_.each(obj.data, function(dataArg) {
					var dataStrTmp = getDataValueShown(dataArg, curSeriesTmp);
					if (obj.myUnit) {
						dataStrTmp = getCertainDataWithUnit(dataStrTmp, obj.myUnit);
					}
					var linkable = curDa.daHelper.isChartMetricValueLinkable(curConfig, obj.keyN);
					if (blnDealTopoLink && obj.topoArr.length > 0 && linkable) {
						var topoIdTmp = (curPosTmp > obj.topoArr.length - 1 ? -1 : obj.topoArr[curPosTmp]);
						if (topoIdTmp > 0) {
							var link = curDa.daHelper.getFormattedLinkUrl(linkable, {
								'topo_id': topoIdTmp
								});
							dataStrTmp = '<a href="' + link + '">' + dataStrTmp + '</a>';
						}
					}
					html += "<td class='daTblCellData'>" + dataStrTmp + "</td>";
					curPosTmp++;
				});
				html += "</tr>";
			});
			if (blnTblBottomOpOut) {
				html += "</tbody></table>";
				html += addPageOperationForTable(chart, tdCount, maxLen);
				html += "</div>";
			} else {
				html += addPageOperationForTable(chart, tdCount, maxLen);
				html += "</tbody>";
				html += "</table></div>";
			}
			
			chart.oriSeriesData.len = maxLen;
			return html;
		};
		
		var prepareListContentGroup = function() {
			if (isHasReportAdditionalSettings()) {
				var listGrpOption = daHelper.getWidgetAdditionalProperties(REPORT_ADDITIONAL, [axis, "list", "group", "forkey"]);
				if (!listGrpOption || !(curConfig.xKey in listGrpOption)) {
					return;
				}
				return listGrpOption[curConfig.xKey];
			}
		};
		var _getStrToShowInList = function(obj, curSeriesConfigTmp) {
			var valTmp = "";
			var strShown = "";
			if (obj.data && obj.data.length > 0) {
				valTmp = obj.data[0];
			} else {
				valTmp = "";
			}
			strShown = getDataValueShown(valTmp, curSeriesConfigTmp);
			if (obj.myUnit) {
				strShown = getCertainDataWithUnit(strShown, obj.myUnit);
			}
			var linkable = daHelper.isChartMetricValueLinkable(curConfig, obj.keyN);
			if (linkable) {
				strShown = "<a href='" + daHelper.getFormattedLinkUrl(linkable, {'data_key': valTmp}) + "'>" + strShown + "</a>"
			}
			return strShown;
		};
		var _getSeriesObjByKey = function(key) {
			return _.find(getCurSortedSeries(), function(obj) { return obj.keyN === key; });
		};
		var renderContentInList = function() {
			var sortedSeries = getCurSortedSeries();
			if (!sortedSeries || sortedSeries.length < 1) {
				return;
			}
			var listGrpOption = prepareListContentGroup();
			var html = "";
			html += "<div class='daListContentContainer'>";
			var curPos = 0;
			var styleStr = "daTdListString";
			if (listGrpOption) {
				html += "<table class='view' width='100%' border='0' cellspacing='0' cellpadding='0'>";
				html += "<tr><td colspan='10'><div class='chart_list_header_blank'></div></td></tr>";
				var grpLen = listGrpOption.length;
				var dealtKeys = {};
				_.each(listGrpOption, function(singleGrp, iPos) {
					if (!('metrics' in singleGrp) || singleGrp.metrics.length < 1) {
						return;
					}
					if (iPos > 0) {
						html += "<tr><td colspan='10'><div class='chart_list_separator'></div></td></tr>";
					}
					var curItemLen = 0;
					var tmpHtml = "",
						firstTdHtml = "";
					_.each(singleGrp.metrics, function(curMetric, curPos) {
						var seriesTmp = _getSeriesObjByKey(curMetric);
						if (!seriesTmp) {
							return;
						}
						dealtKeys[curMetric] = true;
						var curSeriesConfigTmp = curDa.daHelper.getMetric(axis, curMetric);
						curItemLen++;
						if (curItemLen == 1) {
							firstTdHtml = "<td class='daTdListValue'>" + _getStrToShowInList(seriesTmp, curSeriesConfigTmp) 
											+ "</td>"
											+ "<td class='daTdListString' nowrap='nowrap'>" + seriesTmp.name + "</td>";
						} else {
							//tmpHtml += "<tr class='" + (curPos++%2==0?"odd":"even") + "'>";
							tmpHtml += "<tr class='chart_list_with_group'>";
							tmpHtml += "<td class='daTdListValue'>" + _getStrToShowInList(seriesTmp, curSeriesConfigTmp) 
											+ "</td>"
											+ "<td class='daTdListString' nowrap='nowrap'>" + seriesTmp.name + "</td>";
							tmpHtml += "</tr>";
						}
					});
					if (curItemLen > 0) {
						html += "<tr class='chart_list_with_group'>";
						html += "<td class='chart_list_group_title' rowspan='" + curItemLen + "'>" + daHelper.getCertainName(singleGrp.name) + "</td>" + firstTdHtml;
						html += "</tr>";
						html += tmpHtml;
					}
				});
				
				var missedSeries = _.filter(sortedSeries, function(obj) {return !dealtKeys[obj.keyN]; });
				if (missedSeries && missedSeries.length > 0) {
					html += "<tr><td colspan='10'><table class='chart_list_with_group_not_in_group'"
							+ " width='100%' border='0' cellspacing='0' cellpadding='0'>";
					_.each(missedSeries, function(obj) {
						var curSeriesConfigTmp = curDa.daHelper.getMetric(axis, obj.keyN);
						//html += "<tr class='" + (curPos++%2==0?"odd":"even") + "'>";
						html += "<tr>";
						html += "<td class='daTdListString' nowrap='nowrap'>" + obj.name + "</td>";
						var strShown = _getStrToShowInList(obj, curSeriesConfigTmp);
						html += "<td class='daTdListValue'>" + strShown + "</td>";
						html += "</tr>";
					});
					html += "</table></td></tr>";
				}
				
				html += "</table>";
			} else {
				html += "<table class='view' width='100%' border='0' cellspacing='0' cellpadding='0'>";
				_.each(sortedSeries, function(obj) {
					var curSeriesConfigTmp = curDa.daHelper.getMetric(axis, obj.keyN);
					html += "<tr class='" + (curPos++%2==0?"odd":"even") + "'>";
					html += "<td class='daTdListString' nowrap='nowrap'>" + obj.name + "</td>";
					var strShown = _getStrToShowInList(obj, curSeriesConfigTmp);
					html += "<td class='daTdListValue'>" + strShown + "</td>";
					html += "</tr>";
				});
				html += "</table>";
			}
			
			html += "</div>";
			$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).html(html);
			chart.showTableContent();
		};
		
		var blnTableOperationExist = false;
		var addPageOperationForTable = function(chart, tdCount, rowsCount) {
			if (tdCount < 1 || rowsCount <= chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE) {
				chart.oriSeriesData.curIdx = rowsCount;
				return "";
			}
			blnTableOperationExist = true;
			if (blnTblBottomOpOut) {
				chart.tmpOptions.whenRender.cur_FIXED_TABLE_HEIGHT = chart.tmpOptions.whenRender.cur_FIXED_TABLE_HEIGHT - TABLE_BOTTOM_TOOLBAR_HEIGHT;
				$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " .daTblContentContainer table.data_table").height(chart.tmpOptions.whenRender.cur_FIXED_TABLE_HEIGHT);
			}
			var ltdc;
			var ctdc;
			var rtdc;
			var result = '';
			var table_container_class = 'table_op_container';
			if (blnTblBottomOpOut) {
				result += '<table>';
				table_container_class += ' table_op_container_out';
			}
			result += '<tr name="table_data_op_row">';
			
			var addPageOperations = function() {
				var opstr = '<div class="' + table_container_class + '"><a href="javascript:void(0);" name="'+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER
								+'_opa_all" class="table_op_all">All</a>'
								+'<a href="javascript:void(0);" name="'+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER
								+'_opa_more" class="table_op_more">' + chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE + ' More</a>'
								+'<a href="javascript:void(0);" name="'+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER
								+'_opa_topn" class="table_op_topn">Top ' + chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE + '</a>'
								+'</div>';
				return opstr;
			};
			
			if (tdCount > 4) {
				ltdc = 2;
				rtdc = 2;
				ctdc = tdCount - 3;
			}
			
			if (ltdc == -1) {
				result += '<td colspan="' + ltdc + '">' + addPageOperations() + '</td>'
							+ '<td colspan="' + ctdc + '"></td>'
							+ '<td align="right" colspan="' + rtdc + '">' + addPageOperations() + '</td>';
			} else {
				result += '<td colspan="' + tdCount +'">' + addPageOperations() + '</td>';
			}
			
			result += '</tr>';
			if (blnTblBottomOpOut) {
				result += '</table>';
			}
			chart.oriSeriesData.curIdx = chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE;
			return result;
		};
		
		var renderContentInTable = function() {
			var html = '';
			if (chartInverted) {
				html = renderContentInTableInverted();
			} else {
				html = renderContentInTableCommon();
			}
			
			$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).html(html);
			//addAdditionalEventsToTableItems(chart, axisObj);
			makeTableTitleFixed(chart, 100, function() {
				addAdditionalTableColumnEvents(chart, axisObj, tblAdditionalColumnEventMap);
				addAdditionalTableRowItemEvents(chart, axisObj, tblAdditionalRowEventMap);
				copyTableSortEventToFixedTitle(chart);
			}, function() {
				makeTableSortable(chart);
			});
			if (blnTableOperationExist) {
				hideCertainTableRows(chart, chart.oriSeriesData.len, chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE);
				addShowRowsActionToTableData(chart, {blnOpOutTbl: blnTblBottomOpOut});
			}
			chart.showTableContent();
		};
		
		var renderContentInChart = function() {
			var preSeriesState = {};
			if (chart.oriSeriesData != null) {
				for (var key in chart.oriSeriesData) {
					preSeriesState[chart.oriSeriesData[key].id] = true;
				}
			}
			
			_.each(_.keys(preSeriesState), function(id) {
				var serTmp = chart.hcChart.get(id);
				if (serTmp) {
					serTmp.remove(false);
				}
			});
			
			chart.oriSeriesData = getCurSortedSeries();
			var firstSeries;
			if (chart.oriSeriesData && chart.oriSeriesData.length > 0) {
				firstSeries = chart.oriSeriesData[0];
			}
			setChartCategories();
			curColorIdx = 0;
			var blnPieChart = false;
			var sortedSeriesTmp = getCurSortedSeries();
			for (var i = sortedSeriesTmp.length - 1; i >= 0; i--) {
				var obj = sortedSeriesTmp[i];
				if (obj.type === 'pie') {
					blnPieChart = true;
					addPresentationForPieChart(obj);
				} else if (overtime) {
					addPresentationForOvertimeChart(obj);
				} else {
					addPresentationForCommonChart(obj);
				}
			}
			_.each(getCurSortedSeries(), function(obj) {
				chart.hcChart.addSeries(obj, false);
			});
			
			var yaxisTitle = axisDisName;
			if (firstSeries && firstSeries.myUnit) {
				yaxisTitle = axisDisName + " (" + firstSeries.myUnit + ")";
			}
			
			if (blnPieChart) {
				$("div#"+chart.container+" text[style*=da-chart-xaxis]").remove();
				chart.hcChart.xAxis[0].setTitle({ text: yaxisTitle, align: "middle"});
				chart.axisTitles.xAxis = yaxisTitle;
			} else {
				$("div#"+chart.container+" text[style*=da-chart-yaxis]").remove();
				chart.hcChart.yAxis[0].setTitle({ text: yaxisTitle, align: "middle"});
				chart.axisTitles.yAxis = yaxisTitle;
			}
		};
		
		var colorTypes = {};
		var getAProperColor = function(axis, metric) {
			var presentTmp = daHelper.getPreferPresentationWithPresentObj(daHelper.getPreferPresentation(axis, metric), {colorTypes: colorTypes});
			if (presentTmp && presentTmp.color_type) {
				if (presentTmp.color_type in colorTypes) {
					colorTypes[presentTmp.color_type] = colorTypes[presentTmp.color_type] + 1;
				} else {
					colorTypes[presentTmp.color_type] = 1;
				}
			}
			return presentTmp? presentTmp.color||chart.getADefinedColor(curColorIdx++) : chart.getADefinedColor(curColorIdx++);
		};
		var curColorIdx = 0;
		var addPresentationForCommonChart = function(seriesObj) {
			seriesObj.color = getAProperColor(axis, seriesObj.keyN);
		};
		
		var addPresentationForOvertimeChart = function(seriesObj) {
			if (!seriesObj || !overtime) {
				return;
			}
			seriesObj.color = getAProperColor(axis, seriesObj.keyN);
		};
		
		var addPresentationForPieChart = function(seriesObj) {
			if (!seriesObj || !seriesObj.data || seriesObj.data.length < 1 || seriesObj.type != 'pie') {
				return;
			}
			var descData = [];
			var presentObj = daHelper.getPreferPresentation(axis, seriesObj.keyN);
			var singleData;
			var presentTmp;
			curColorIdx = 0;
			var nameMap = {};
			_.each(seriesObj.data, function(data){
				var presentTmp = daHelper.getPreferPresentationWithPresentObj(presentObj, data[0]);
				if (data) {
					singleData = {
						name: presentTmp? presentTmp.text||data[0] : data[0],
						color: presentTmp? presentTmp.color||chart.getADefinedColor(curColorIdx++) : chart.getADefinedColor(curColorIdx++),
						y: data[1]
					}
					descData.push(singleData);
					nameMap[singleData.name] = data[0];
				} else {
					descData.push(null);
				}
			});
			seriesObj.data = descData;
			seriesObj.myDataNode = seriesObj.myDataNode || {};
			seriesObj.myDataNode.nameMap = nameMap;
		};
		
		var checkSeriesInStackGroup = function(seriesArgs) {
			if (seriesArgs) {
				_.each(_.values(_.groupBy(seriesArgs, function(singleSeries) {
					return singleSeries.stack;
				})), function(seriesArray) {
					if (seriesArray && seriesArray.length > 1) {
						_.each(seriesArray, function(singleSeries) {
							singleSeries.myInGroup = true;
						});
					}
				});
			}
		};
		var adjustSeriesTimeDataWithTimeZone = function(seriesArgs) {
			if (!overtime) {
				return;
			}
			var timeOffset = chart.timeOffset;
			var dataTmp;
			if (seriesArgs) {
				_.each(_.values(seriesArgs), function(series) {
					if (series && series.data) {
						dataTmp = [];
						_.each(series.data, function(data) {
							dataTmp.push([data[0] + timeOffset, data[1]]);
						});
						series.data = dataTmp;
					}
				});
			}
		};
		
		var valRange;
		var result = ah_dashDataRender_base_data.dataRender(dataCalOptions);
		if (result) {
			var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);

			if (result.curSortedSeries && result.curSortedSeries.length > 0) {
				var _sortOption = daHelper.getWidgetAdditionalProperties(REPORT_ADDITIONAL, [axis, "column", "sortBy"], {
					blnForKey: true,
					keyValue: curConfig.xKey
				});
				if (_sortOption) {
					if (!isString(_sortOption.data)) {
						var idx = _sortOption.data;
						var blnAsc = (_sortOption.direction === "desc"?1:-1);
						result.curSortedSeries = _.sortBy(result.curSortedSeries, function(aData) {
							return aData.data[idx]*blnAsc;
						});
					} else {
						// to extend it when needed
					}
				}
			}
		
			curSortedSeries = result.curSortedSeries;
			checkSeriesInStackGroup(curSortedSeries);
			adjustSeriesTimeDataWithTimeZone(curSortedSeries);
			valRange = result.valRange;
			chart.totalValue = result.totalValue;
			chart.totalOriValue = result.totalOriValue;
			
			if (chart.currentDashboard.daHelper.isChartMonitorable(curConfig)
					|| chart.currentDashboard.daHelper.isChartDeviceLinkable(curConfig)
					|| blnPostColumns) {
				curCategories = result.categoriesWithMark;
			} else {
				curCategories = result.curCategories;
			}
		}
		if (chartType === 'table') {
			renderContentInTable();
		} else if (chartType === 'list') {
			renderContentInList();
		} else {
			renderContentInChart();
		}
		adjustChartValueRange();
	};
	
	var additionalRenderAfterDraw = function(chart, options, data) {
		if (!chart.currentDashboard) {
			return;
		}
		var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
		setTimeout(function() {
			if (chart.currentDashboard.daHelper.isChartMonitorable(curConfig)) {
				if (!chart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
					$el(chart.container).find("span.helloxaxis").each(function(index, xTitleObj) {
						var $xTitleObj = $(xTitleObj);
						var $xTitleObjP = $xTitleObj.parent("span");
						$xTitleObjP.css("old-color", $xTitleObjP.css("color")).css("color", "#666666");
						$xTitleObj.attr("old-title", $xTitleObj.attr("title")).attr("title", Aerohive.lang.chart.tip.monitor.timelimited);
					});
				}
			}
		}, 1000);
	};
	
	var addAdditionalTableColumnEvents = function(chart, axis, eventMap) {
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' th a[data-addi-click]').each(function() {
			var $this = $(this);
			var click_event_str = $this.attr("data-addi-click");
			if (click_event_str) {
				var click_event_strs = click_event_str.split(" ");
				if (click_event_strs.length > 1) {
					$this.unbind('click')
							.removeAttr("data-addi-click")
							.click(function(e) {
								eventMap[click_event_str](e, chart);
							});
				}
			}
		});
	};
	
	var addAdditionalTableRowItemEvents = function(chart, axis, eventMap) {
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' tbody td a[data-addi-click]').each(function() {
			var $this = $(this);
			var click_event_str = $this.attr("data-addi-click");
			if (click_event_str) {
				var click_event_strs = click_event_str.split(" ");
				if (click_event_strs.length > 2) {
					$this.unbind('click')
							.removeAttr("data-addi-click")
							.click(function(e) {
								eventMap.events[click_event_strs[0] + ' ' + click_event_strs[2]](e, 
									chart,
									eventMap.options[click_event_strs[0] + ' ' + click_event_strs[1]]);
							});
				}
			}
		});
	};
	
	var makeTableSortable = function(chart) {
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " table thead tr th").addClass("header");
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " table.data_table").tablesorter({
			cssAsc: "headerSortUp",
			cssDesc: "headerSortDown",
			cssHeader: "header",
			widthFixed: true,
			widgets: ['ah_alter_table_row_bg'],
			curAhWidgetChart: chart,
			textExtraction: function(node) {
				var $node = $(node);
				var result = $node.find("input.data_ori").val();
				if ((isUndefined(result) || result === null) && $node.find("span.data_shown").length > 0) {
					result = $node.find("span.data_shown").text();
				}
				if (isUndefined(result) || result === null) {
					result = $node.text();
				}
				return result.replace(":", " ").replace("."," ").replace("-"," ");
			}
		});
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " table.data_table thead tr th").each(function() {
			if ($(this).hasClass("daTitleAddi")) {
				$(this).removeClass("header");
				return;
			}
		});
	};
	var SORT_TOGGLE_CLASS = ["headerSortUp", "headerSortDown"];
	var copyTableSortEventToFixedTitle = function(chart) {
		var oriHeadThs = $el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " div.fht-tbody table thead tr th");
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " div.fht-thead table thead tr th").each(function(idx) {
			$(this).unbind('click').click(function(e) {
				var $oriHeaderTmp = $(oriHeadThs[idx]);
				var $this = $(this);
				$oriHeaderTmp.click();
				setTimeout(function(){
					$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " div.fht-thead table thead tr th").removeClass("headerSortUp headerSortDown");
					var clsName = _.find(SORT_TOGGLE_CLASS, function(clsName) {
						if ($oriHeaderTmp.hasClass(clsName)) {
							return true;
						}
					});
					if (clsName) {
						$this.addClass(clsName);
					}
				}, 50);
			});
		});
	};
	var resetDefaultDataTableRowBackground = function(chart) {
		resetTableRowBackground($el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " table.data_table"));
		if (!chart.tmpOptions.whenRender.fullDisplayed) {
			reShowCurrentRowItemsForSortReason(chart);
		}
	};
	var resetTableRowBackground = function($tblEl) {
		$tblEl.find("tbody tr").each(function(idx) {
			var clsName = "odd";
			if (idx%2 == 1) {
				clsName = "even";
			}
			$(this).removeClass("even odd").addClass(clsName);
		});
	};
	
	var addAdditionalEventsToTableItems = function(chart, axis) {
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' td.daAxisTitleLinkable1 > a').unbind('click');
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' td.daAxisTitleLinkable1 > a').click(function(e){
			xAxisLabelClickEvent(e, chart, $(this).text(), axis);
		});
		
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' td.daAxisTitleLinkable2 > a').unbind('click');
		$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + ' td.daAxisTitleLinkable2 > a').click(function(e){
			xAxisLabelClickEvent(e, chart, $(this).text(), axis);
		});
	};
	
	var addShowRowsActionToTableData = function(chart, options) {
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER+" div.table_op_container>a.table_op_all").click(function(e) {
			showTableDataRows(chart, 'all', options);
		});
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER+" div.table_op_container>a.table_op_more").click(function(e) {
			showTableDataRows(chart, 'more', options);
		});
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER+" div.table_op_container>a.table_op_topn").click(function(e) {
			showTableDataRows(chart, 'topn', options);
		});
	};
	var __makeTableTitleFixed = function(chart, options) {
		options = options || {};
		if (options.precall) {
			options.precall();
		}
		var $tableTmp = $("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER+" .daTblContentContainer table.data_table");
		var oriTblHeight = options.oriTblHeight;
		if (!oriTblHeight) {
			oriTblHeight = $tableTmp.height(); 
		}
		if (oriTblHeight > FIX_TABLE_HEAD_JUDGE_HEIGHT) {
			$tableTmp.fixedHeaderTable({height: chart.tmpOptions.whenRender.cur_FIXED_TABLE_HEIGHT});
		}
		if (options.callback) {
			options.callback();
		}
	};
	var makeTableTitleFixed = function(chart, delay, callback, precall) {
		var oriTblHeight = $("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER+" .daTblContentContainer table.data_table").height();
		if (delay) {
			setTimeout(function(){__makeTableTitleFixed(chart, {oriTblHeight: oriTblHeight, callback: callback, precall: precall});}, delay);
		} else {
			__makeTableTitleFixed(chart, {oriTblHeight: oriTblHeight, callback: callback, precall: precall});
		}
	};
	var getTableDataLen = function(chart, options) {
		return chart.oriSeriesData.len;
	};
	var getTableMaxEndRowPos = function(chart, endRow, options) {
		var result = endRow,
			dataLen = getTableDataLen(chart, options);
		if (!endRow || endRow > dataLen) {
			result = dataLen;
		}
		return result;
	};
	var showTableDataRows = function(chart, type, options) {
		var dataLen = getTableDataLen(chart, options);
		if (type == 'all') {
			showCertainTableRows(chart, dataLen, chart.oriSeriesData.curIdx, options);
		} else if (type == 'more') {
			showCertainTableRows(chart, chart.oriSeriesData.curIdx + chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE, chart.oriSeriesData.curIdx, options);
		} else if (type == 'topn') {
			hideCertainTableRows(chart, chart.oriSeriesData.len, chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE, options);
		}
	};
	var hideCertainTableRows = function(chart, endRow, startRow, options) {
		endRow = getTableMaxEndRowPos(chart, endRow, options);
		if (!startRow) {
			startRow = 0;
		}
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " .data_table tbody tr").each(function(i) {
			if (i >= startRow && i < endRow) {
				$(this).hide();
			}
		});
		chart.oriSeriesData.curIdx = startRow;
		adjustTableDataOpItems(chart, startRow, options);
	};
	var showCertainTableRows = function(chart, endRow, startRow, options) {
		endRow = getTableMaxEndRowPos(chart, endRow, options);
		if (!startRow) {
			startRow = 0;
		}
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " .data_table tbody tr").each(function(i) {
			if (i >= startRow && i < endRow) {
				$(this).show();
			}
		});
		chart.oriSeriesData.curIdx = endRow;
		adjustTableDataOpItems(chart, endRow, options);
	};
	var reShowCurrentRowItemsForSortReason = function(chart) {
		$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " .data_table tbody tr").each(function(i) {
			if (i < chart.oriSeriesData.curIdx) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	};
	var adjustTableDataOpItems = function(chart, endRow, options) {
		options = options || {};
		var maxLen = chart.oriSeriesData.len;
		if (endRow >= maxLen) {
			showOrHideTableDataOpItems(chart, true, 'a.table_op_topn');
		} else if (endRow <= chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE && maxLen > chart.tmpOptions.whenRender.cur_TABLE_ROWS_SLICE) {
			showOrHideTableDataOpItems(chart, false, 'a.table_op_topn');
		} else {
			showOrHideTableDataOpItems(chart, false);
		}
		if (!options.blnOpOutTbl) {
			$("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " .data_table tbody tr[name=table_data_op_row]").show();
		}
	};
	var tblOpItems = ['a.table_op_all', 'a.table_op_more', 'a.table_op_topn'];
	var showOrHideTableDataOpItems = function(chart, blnShow, items) {
		if (isString(items)) {
			items = [items];
		}
		var $tblOpRow = $("#"+chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER + " tr[name=table_data_op_row]");
		var opFunc = blnShow?'show':'hide';
		var opFunc2 = blnShow?'hide':'show';
		_.each(tblOpItems, function(item) {
			$tblOpRow.find(item)[opFunc2]();
		});
		_.each(items, function(item) {
			$tblOpRow.find(item)[opFunc]();
		});
	};
	
	var xAxisLabelClickEvent = function(e, chart, text, value, axis) {
		/*if (e) {
			e.preventDefault();
			e.stopPropagation();
		}*/
	    if (!isObject(chart)) {
	    	chart = ARC.RenderedCharts.get(chart);
	    }
	    if (!chart.currentDashboard.opAllowed.monitor && !chart.currentDashboard.opAllowed.deviceLink) {
			return;
		}
	    var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
	    if (chart.currentDashboard.daHelper.isChartMonitorable(curConfig) 
	    		&& !chart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
	    	return;
	    }
	    if (!axis) {
	    	axis = curConfig.axis;
	    }
		axisObj = chart.currentDashboard.daHelper.getAxis(axis);
		if (!axisObj) {
			return;
		}
	    if (chart.xCategoryClick) {
	    	chart.xCategoryClick.apply(chart, [chart, text, curConfig.xmt, value]);
	    }
	};
	
	var _prepareFadeInMsgContainer = function(chart) {
		var $container = chart.$container,
			$msgContainer = $container.find(".hover-msg-container");
		if ($msgContainer.length <= 0) {
			$msgContainer = $("<div class='hover-msg-container'><div class='hover-msg-mark-info'></div><div class='hover-msg-content'></div></div>");
			$container.append($msgContainer);
		}
		return $msgContainer;
	};
	var _showFadeInMsgContainer = function(blnShow, $msgContainer, msg) {
		if (!blnShow) {
			$msgContainer.css("display", "none");
			return;
		}
		$msgContainer.find(".hover-msg-content").html(msg);
		$msgContainer.css("display", "table-cell");
	};
	var _xAxisLabelHoverNoClickEvent = function(e, chart, blnHide) {
		if (!isObject(chart)) {
	    	chart = ARC.RenderedCharts.get(chart);
	    }
	    var $msgContainer = _prepareFadeInMsgContainer(chart);
	    if (blnHide) {
	    	_showFadeInMsgContainer(false, $msgContainer);
	    	return;
	    }
	    var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
	    if (chart.currentDashboard.daHelper.isChartMonitorable(curConfig) 
	    		&& !chart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
	    	_showFadeInMsgContainer(true, $msgContainer, Aerohive.lang.chart.tip.monitor.timelimitedTbl);
	    }
	};
	
	var dashDataHTMLRender = function(chart, options, data) {
		if (!chart) {
			return;
		}
		if (!data) {
			chart.tips.addError('There is no data this chart.', 'all');
			return;
		}
		chart.tips.addError('', 'all', -1);
		var html = data.htmlText;
		var html1;
		delete data.htmlText;
		delete data.rs;
		delete data.exception;
		delete data.result;
		var dataOri = data.data;
		data.data = eval(data.data);
		if (html && $.trim(html) != "") {
			html1 = html.replace(/<AeroHive\/>/g, JSON.stringify(data));
		}
		data.htmlText = html;
		data.data = dataOri;
		set_innerHTML(chart.AREA_SECTIONS.CHART_CONTENT, html1);
	};
	
	Chart.prototype.dataRenderMethods['dash'] = {
			dataFunc: dashDataRender,
			afterDrawFunc: additionalRenderAfterDraw,
			htmlDataFunc: dashDataHTMLRender
	};
	
	$.extend(Chart, {
		xAxisLabelClickEvent: xAxisLabelClickEvent,
		xAxisLabelHoverNoClickEvent: _xAxisLabelHoverNoClickEvent
	});
	//Chart.prototype.hackLabelClickable = additionalRenderAfterDraw;
	
	$.tablesorter.addWidget({
        id: "ah_alter_table_row_bg",
        format: function (table) {
        	if (table.config.curAhWidgetChart) {
        		resetDefaultDataTableRowBackground(table.config.curAhWidgetChart);
        	}
        }
     });
})(jQuery, _);