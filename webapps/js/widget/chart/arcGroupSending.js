/**
 * used to make grouped chart request sending
 * @author wx
 */

(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		UNDEFINED,
		isUndefined = ARC.isUndefined,
		pick = ARC.pick,
		each = ARC.each,
		consoleLog = ARC.consoleLog;
	
	var NO_GROUP_ENABLED = 'no_group_enabled';
	
	var ChartGroupAttr = function(chart, options) {
		var self = this;
		self.url = chart.reportUrl;
		self.operation = chart.operation;
		self.reportId = chart.reportId;
		self.periodType = chart.periodType;
		self.interval = chart.interval;
		self.chart = chart;
		self.subType = pick(chart.additionalArgs.get('subType'), 'default');
		
		var grpEnabled = true;
		self.grpMark = 'default';
		if (options.groupRequest) {
			grpEnabled = pick(options.groupRequest.enabled, true);
			if (options.groupRequest.grpMark) {
				self.grpMark = options.groupRequest.grpMark;
			}
		}
		if (self.grpMark === 'default') {
			self.grpMark = self.subType;
		}
		
		self.getMarkId = function() {
			if (grpEnabled === false) {
				return NO_GROUP_ENABLED;
			}
			return self.url+self.operation+self.periodType;
		};
		self.getMarkIdWithReportId = function() {
			if (grpEnabled === false) {
				return NO_GROUP_ENABLED;
			}
			return self.url+self.operation+self.periodType+self.reportId;
		};
		self.getMarkIdWithInterval = function(intervalTmp) {
			if (grpEnabled === false) {
				return NO_GROUP_ENABLED;
			}
			var intervalTmp1 = self.interval;
			if (intervalTmp) {
				intervalTmp1 = intervalTmp;
			}
			return self.url+self.operation+self.periodType+intervalTmp1;
		};
		
		self.getReportInfo = function() {
			return {
				reportId: self.reportId,
				interval: self.interval
			};
		};
	};
	
	var GroupRequestsCls = function() {
		var self = this;
		
		self.groupCharts = {
			add: function(chart, options) {
				var aGroupAttr = new ChartGroupAttr(chart, options);
				if (aGroupAttr.getMarkId() !== NO_GROUP_ENABLED) {
					chartCollections.push(aGroupAttr);
					if (!idWithSubTypes[chart.reportId]) {
						idWithSubTypes[chart.reportId] = {};
						idWithSubTypes[chart.reportId].items = [];
					}
					idWithSubTypes[chart.reportId].items.push(aGroupAttr.subType);
				}
			}	
		};
		
		self.rePrepare = function() {
			requestGroups = {};
			requestCallBackWithMarks = {};
			for (var key in timerIds) {
				if(timerIds[key]) {
					clearTimeout(timerIds[key]);
				}
			}
			timerIds = {};
			if (chartCollections !== null
					&& chartCollections.length > 0) {
				var tmpRequestGroups = {};
				for (var i = 0; i < chartCollections.length; i++) {
					var tmpAttr = chartCollections[i];
					requestCallBackWithMarks[tmpAttr.reportId+tmpAttr.grpMark] = tmpAttr.chart.dataReceivedCallBack;
					if (tmpRequestGroups[tmpAttr.getMarkId()]) {
						var tmpRequestGroupItem = tmpRequestGroups[tmpAttr.getMarkId()][tmpAttr.reportId];
						if (tmpRequestGroupItem) {
							tmpRequestGroupItem.count = tmpRequestGroupItem.count + 1;
							if (tmpRequestGroupItem.interval > tmpAttr.interval) {
								tmpRequestGroupItem.interval = tmpAttr.interval;
								tmpRequestGroupItem.attr = tmpAttr;
							}
						} else {
							tmpRequestGroups[tmpAttr.getMarkId()][tmpAttr.reportId] = {
									count: 1,
									interval: tmpAttr.interval,
									attr: tmpAttr
							}
						}
					} else {
						tmpRequestGroups[tmpAttr.getMarkId()] = {};
						tmpRequestGroups[tmpAttr.getMarkId()][tmpAttr.reportId] = {
								count: 1,
								interval: tmpAttr.interval,
								attr: tmpAttr
						}
					}
				}
				for (var key in tmpRequestGroups) {
					var tmpGroupItem = tmpRequestGroups[key];
					for (var key1 in tmpGroupItem) {
						var tmpGroupItem1 = tmpGroupItem[key1];
						var groupKey = tmpGroupItem1.attr.getMarkIdWithInterval(tmpGroupItem1.interval);
						if (requestGroups[groupKey]) {
							for (var j = 0; j < tmpGroupItem1.count; j++) {
								requestGroups[groupKey].push({
									reportId: key1,
									interval: tmpGroupItem1.interval,
									attr: tmpGroupItem1.attr
								});
							}
						} else {
							requestGroups[groupKey] = [];
							for (var j = 0; j < tmpGroupItem1.count; j++) {
								requestGroups[groupKey].push({
									reportId: key1,
									interval: tmpGroupItem1.interval,
									attr: tmpGroupItem1.attr
								});
							}
						}
						
						if (requestCalledLog[groupKey]
							&& requestCalledLog[groupKey].reqDescInfoAtFirst) {
						} else {
							requestCalledLog[groupKey] = {};
							requestCalledLog[groupKey].reqDescInfoAtFirst = true;
						}
					}
				}
				
			}
		}
		
		self.prepareAndDoRequest = function() {
			if (chartCollections !== null
					&& chartCollections.length > 0) {
				self.rePrepare();
				self.doRequest();
			}
		};
		self.doRequest = function() {
			if (requestGroups) {
				for (var key in requestGroups) {
					doRequestSingleGroup(key);
				}
			}
		};
		var doRequestSingleGroup = function(key) {
			if (requestGroups
					&& requestGroups[key]) {
				var ids = requestGroups[key];
				if (!ids || ids == null || ids.length < 1) return;
				var reportIds = [];
				var interval;
				var attrTmp;
				for (var i = 0; i < ids.length; i++) {
					if (ids[i].repeat === false) continue;
					interval = ids[i].interval;
					reportIds.push(ids[i].reportId);
					if (!attrTmp) {
						attrTmp = ids[i].attr;
					}
				}
				if (reportIds.length < 1) return;
				var reportIdsWithSubTypes = [];
				for (var i = 0; i < reportIds.length; i++) {
					idWithSubTypes[reportIds[i]].curIdx = 0;
				}
				for (var i = 0; i < reportIds.length; i++) {
					var curSubType = idWithSubTypes[reportIds[i]].items[idWithSubTypes[reportIds[i]].curIdx];
					idWithSubTypes[reportIds[i]].curIdx = idWithSubTypes[reportIds[i]].curIdx + 1;
					reportIdsWithSubTypes.push(reportIds[i] + '_' + pick(curSubType, ''));
				}
				var reqDescInfoAtFirst = false;
				if (requestCalledLog[key] && requestCalledLog[key].reqDescInfoAtFirst) {
					reqDescInfoAtFirst = requestCalledLog[key].reqDescInfoAtFirst;
				}
				var myArgs = attrTmp.chart.genReportAttrs({
					"idsWithSubTypes": reportIdsWithSubTypes,
					"blnReqDesc": reqDescInfoAtFirst
				});
				
				var requestArgs = {
						operation: attrTmp.operation,
						ignore: new Date().getTime()
				};
				$.extend(true, requestArgs, myArgs);
				$.post(attrTmp.url,
						$.param(requestArgs, true),
						function(data, textStatus) {
							dataRequestCallBack(data, textStatus, key);
						},
						"json");
				removeNoRepeatCharts(key);
				doSendRequestTimer(key, function() {doRequestSingleGroup(key);}, interval);
			}
		};
		var removeNoRepeatCharts = function(key) {
			if (requestCalledLog[key] && requestCalledLog[key].reqDescInfoAtFirst === true) {
				return;
			}
			if (requestGroups
					&& requestGroups[key]) {
				var ids = requestGroups[key];
				if (!ids || ids == null || ids.length < 1) return;
				for (var i = 0; i < ids.length; i++) {
					requestGroups[key][i].repeat = true;
					var attrTmp = ids[i].attr;
					if (attrTmp) {
						var blnRepeat = attrTmp.chart.repeat;
						var blnCallNext = attrTmp.chart.callNextData;
						if (blnRepeat === false
								|| blnCallNext === false) {
							requestGroups[key][i].repeat = false;
						}
					}
				}
			}
		};
		
		var dataRequestCallBack = function(data, textStatus, key) {
			if (data
					&& data.length > 0) {
				for (var i = 0; i < data.length; i++) {
					var dataDetailTmp = data[i];
					var dataDetail;
					if (dataDetailTmp
							&& dataDetailTmp.length > 0) {
						dataDetail = dataDetailTmp[0];
					}
					if (dataDetail.groupInfo) {
						var reportId = dataDetail.groupInfo.reportId;
						var grpMark = pick(dataDetail.groupInfo.grpMark, 'default');
						var markId = reportId + grpMark;
						if (requestCallBackWithMarks[markId]) {
							requestCallBackWithMarks[markId](dataDetailTmp, textStatus);
						}
					}
				}
				if (requestCalledLog[key] && requestCalledLog[key].reqDescInfoAtFirst === true) {
					requestCalledLog[key].reqDescInfoAtFirst = false;
					doRequestSingleGroup(key);
				}
			}
		};
		var doSendRequestTimer = function(keyName, funcName, interval) {
			if(timerIds[keyName]) {
				clearTimeout(timerIds[keyName]);
			}
			if (requestGroups
					&& requestGroups[keyName]
					&& requestGroups[keyName].length < 1) {
				return;
			}
			timerIds[keyName] = setTimeout(function(){funcName()}, interval);
		};
		
		var chartCollections = [];
		var requestGroups = {};
		var requestCallBackWithMarks = {};
		var timerIds = {};
		var idWithSubTypes = {};
		var requestCalledLog = {};
	};
	
	var GroupRequests = new GroupRequestsCls();
	
	Chart.prototype.callbacks.push(function(chart, options) {
		var grpEnabled = true;
		if (options.groupRequest) {
			grpEnabled = pick(options.groupRequest.enabled, true);
		}
		if (grpEnabled) {
			chart.requestDataFunc = null;
			GroupRequests.groupCharts.add(chart, options);
		}
	});
	
	$.extend(ARC, {
		GroupRequests: GroupRequests
	});

})(jQuery);