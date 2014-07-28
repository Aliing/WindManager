/**
 * used to add controls for report chart, for example, buttons
 * @author wx
 */

(function($, yDom, yEvent, yMenu){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		UNDEFINED,
		isUndefined = ARC.isUndefined,
		pick = ARC.pick,
		each = ARC.each,
		$el = ARC.$el,
		stopEventBubble = ARC.stopEventBubble,
		getPageWH = ARC.getPageWH,
		consoleLog = ARC.consoleLog;
	
	$.extend(Chart.prototype, {
		emailChart: function (options, chartOptions, callback) {
			var self = this,
				chart = self.hcChart,
				svg = chart.getSVG($.extend(true, {}, chart.options.exporting.chartOptions, chartOptions));
			
			options = $.extend(true, {}, chart.options.exporting, options);
			
			var requestArgs = {
				filename: options.filename,
				type: options.type,
				width: options.width,
				emailAddress: options.emailAddress,
				svg: svg,
				ignore: new Date().getTime()
			};
			if (options.emailMark) {
				requestArgs.emailMark = options.emailMark;
			}
			
			var dataRequestCallBack = function(data, textStatus) {
				if (data) {
					var retStatus = true;
					if (data.resultStatus === false) {
						retStatus = false;
					}
					var message = "";
					if (retStatus === true) {
						if (data.info) {
							message = data.info;
						} else {
							message = "Successfully send E-mail.";
						}
						message = "<span class='rpSuccInfo'>" + message + "</span>";
						self.tips.addInfo(message);
					} else {
						if (data.errInfo) {
							message = data.errInfo;
							message = "<span class='rpErrInfo'>" + message + "</span>";
							self.tips.addError(message);
						} else if (data.warnInfo) {
							message = data.warnInfo;
							message = "<span class='rpWarnInfo'>" + message + "</span>";
							self.tips.addWarning(message);
						} else {
							message = "Failed to send E-mail.";
							message = "<span class='rpErrInfo'>" + message + "</span>";
							self.tips.addError(message);
						}
					}
				}
				if (callback) {
					callback();
				}
			}
			
			$.post(options.url,
					requestArgs,
					dataRequestCallBack,
					"json");
		}
	});
	
	var $appendIfPresent = function(parentEl, childEl, option) {
		if (childEl) {
			if (option === 'hide') {
				childEl.hide();
			} else if (option === 'show') {
				childEl.show();
			}
			parentEl.append(childEl);
		}
	};
	
	var _EVENT_COMMON_TYPE = 'event.ctl.common';
	Chart.prototype._EVENT_COMMON_TYPE = _EVENT_COMMON_TYPE;
	var _dealCommonEventTypeFromChart = function(chart, type, operation, notifier, options, callback) {
		if (!notifier.uniqueID
				|| notifier.uniqueID !== chart.uniqueID) {
			return;
		}
		var menuId = this.MAIN_MENU_ID;
		if (_EVENT_COMMON_TYPE === type) {
			if ('nodata' === operation) {
				$el(menuId).addClass('disabled');
				this.disabled = true;
			} else if ('hasdata' === operation) {
				$el(menuId).removeClass('disabled');
				this.disabled = false;
			}
			if (callback) {
				callback.call(chart, type, operation);
			}
		}
	};
	
	var exportButton = function(chart, menuSpanId, options) {
		var self = this;
		var subMenuType = options.subMenuType || 'YUI';
		self.render = function() {
			registerEvents();
			if (subMenuType === 'YUI') {
				oMenu.render();
				yEvent.addListener(self.MAIN_MENU_ID, "click", onDownMenuClick, null, oMenu);
			} else if (subMenuType === 'subli') {
				prepareSubliKindOfSubMenu();
			}
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.destroy = function(blnRmContainer) {
			if (!blnRmContainer && oMenu) {
				oMenu.destroy();
			}
			unRegisterEvents();
			$el(self.MAIN_MENU_ID).remove();
			$el(self.SUB_MENU_DIV_ID).remove();
		};
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		
		var registerEvents = function() {
			ARC.Events.register(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.register(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		var unRegisterEvents = function() {
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		self.dealNotify = function(type, operation, notifier) {
			_dealCommonEventTypeFromChart.call(self, chart, type, operation, notifier, null, function(type, operation) {
				if ('nodata' === operation) {
					$el(self.MAIN_MENU_ID).find("ul.dropdown-menu > li").addClass('disabled');
				} else if ('hasdata' === operation) {
					$el(self.MAIN_MENU_ID).find("ul.dropdown-menu > li").removeClass('disabled');
				}
			});
		};
		
		var subLiMenus = [];
		subLiMenus.push(['downloadPNG', 'Download a PNG file']);
		subLiMenus.push(['downloadJPEG', 'Download a JPEG file']);
		subLiMenus.push(['downloadPDF', 'Download a PDF file']);
		subLiMenus.push(['downloadSVG', 'Download an SVG file']);
		var prepareSubliKindOfSubMenu = function() {
			var $aTmp, $liTmp;
			var $curParentLi = $('#' + self.MAIN_MENU_ID);
			$curParentLi.addClass("dropdown-submenu");
			var $subUl = $("<ul></ul>").addClass("dropdown-menu");
			for (var i = 0; i < subLiMenus.length; i++) {
				var singleMenu = subLiMenus[i];
				$aTmp = $("<a></a>").attr({
						tabindex: -1,
						href: "javascript: void(0);"
					}).html(singleMenu[1]);
				$liTmp = $("<li></li>").attr({exType: singleMenu[0]}).click(function(exportType){
					return function(e) {
						onCertainExportClick(exportType, true);
						if (chart.menuItemClickAfter) {
							chart.menuItemClickAfter(self.MAIN_MENU_ID);
						}
					};
				}(singleMenu[0]));
				$liTmp.append($aTmp);
				$subUl.append($liTmp);
			}
			$curParentLi.append($subUl);
		};
		
		var cHeight = chart.hcChart.options.chart.height;
		if (cHeight !== null && cHeight > 0) {
			cHeight += 30;
		}
		
		var onDownMenuClick = function(e) {
			var blnGo = true;
			if (self.checkBefore) {
				blnGo = self.checkBefore.apply(chart);
			}
			if (!blnGo) {
				return;
			}
			oMenu.show();
			stopEventBubble(e);
			if (chart.menuItemClickAfter) {
				chart.menuItemClickAfter(self.MAIN_MENU_ID);
			}
		};
		
		var menuId = self.SUB_MENU_DIV_ID;
		var oMenu = new yMenu(menuId, { fixedcenter: false, zIndex: 999 });
		
		var onMenuItemClick = function(p_sType, p_aArgs, p_oValue) {
			onCertainExportClick(p_oValue);
		};
		var onCertainExportClick = function(exportType, blnNeedCheck) {
			if (blnNeedCheck) {
				var blnGo = true;
				if (self.checkBefore) {
					blnGo = self.checkBefore.apply(chart);
				}
				if (!blnGo) {
					return;
				}
			}
			var additionalOptions = {
				title: {
					align: 'left',
					text: chart.getTitleText()
				},
				subtitle: {
					align: 'left',
					text: chart.getSubTitleText()
				},
				yAxis: {
					title: {
						text: chart.axisTitles.yAxis
					}
				},
				xAxis: {
					title: {
						text: chart.axisTitles.xAxis
					}
				},
				chart: {
					height: 550,
					width: 900
				},
				legend: {
					x: -20
				}
			};
			if (!chart.isDatetimeType) {
				additionalOptions = $.extend(true, additionalOptions, {
					xAxis: {
						labels: {
							formatter: function() {
								return chart.exportChartLabelFormatter?chart.exportChartLabelFormatter.apply(chart, [this.value]):this.value;
							},
							style: {
								color: '#6D869F'
							},
							'width': 400
						}
					}
				});
			}
			
			var oriAllowPromptTmp;
			if (win.allowPrompt && win.prevantPrompt && $.isFunction(win.prevantPrompt)) {
				oriAllowPromptTmp = win.allowPrompt;
				win.prevantPrompt();
			}
			var fileNameTmp = chart.getTitleText();
			if (exportType === 'downloadPNG') {
				chart.hcChart.exportChart({
					type: 'image/png',
					filename: fileNameTmp
				}, additionalOptions);
			} else if (exportType === 'downloadJPEG') {
				chart.hcChart.exportChart({
					type: 'image/jpeg',
					filename: fileNameTmp
				}, additionalOptions);
			} else if (exportType === 'downloadPDF') {
				chart.hcChart.exportChart({
					type: 'application/pdf',
					filename: fileNameTmp
				}, additionalOptions);
			} else if (exportType === 'downloadSVG') {
				chart.hcChart.exportChart({
					type: 'image/svg+xml',
					filename: fileNameTmp
				}, additionalOptions);
			}
			if (win.allowPrompt && oriAllowPromptTmp) {
				win.allowPrompt = oriAllowPromptTmp;
			}
		};
		
		oMenu.addItems([
		    [{ text: 'Download a PNG file', onclick: { fn: onMenuItemClick, obj: "downloadPNG" } }],
			[{ text: 'Download a JPEG file', onclick: { fn: onMenuItemClick, obj: "downloadJPEG"} }],
			[{ text: 'Download a PDF file', onclick: { fn: onMenuItemClick, obj: "downloadPDF" } }],
		    [{ text: 'Download an SVG file', onclick: { fn: onMenuItemClick, obj: "downloadSVG" } }]
		]);

		oMenu.subscribe("beforeShow", function(){
			var x = yDom.getX(self.MAIN_MENU_ID);
			var y = yDom.getY(self.MAIN_MENU_ID);
			var pageSec = getPageWH();
			var menuLeft = x;
			if (pageSec && pageSec.width && pageSec.width < (x+110)) {
				menuLeft -= 110;
			}
			yDom.setX(menuId, menuLeft);
			yDom.setY(menuId, y+20);
		});
		
	};
	
	var printButton = function(myChart, menuSpanId, options) {
		var self = this;
		self.MAIN_MENU_ID = myChart.getMainMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.render = function() {
			$('#'+self.MAIN_MENU_ID).click(function(e){
				if (myChart.menuItemClickAfter) {
					myChart.menuItemClickAfter(self.MAIN_MENU_ID);
				}
				onPrintClick();
				stopEventBubble(e);
			});
			registerEvents();
		};
		self.destroy = function(blnRmContainer) {
			$el(self.MAIN_MENU_ID).remove();
			unRegisterEvents();
		};
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === myChart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		
		var onPrintClick = function() {
			var blnGo = true;
			if (self.checkBefore) {
				blnGo = self.checkBefore.apply(myChart);
			}
			if (!blnGo) {
				return;
			}
			print();
		};
		
		var registerEvents = function() {
			ARC.Events.register(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.register(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		var unRegisterEvents = function() {
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		self.dealNotify = function(type, operation, notifier) {
			_dealCommonEventTypeFromChart.call(self, myChart, type, operation, notifier, null);
		};
		
		var tmp_AREA_SECTIONS = myChart.AREA_SECTIONS;
		var print = function () {
			var chart = myChart.hcChart,
				container = chart.container,
				origDisplay = [],
				origParent = container.parentNode,
				body = document.body,
				childNodes = body.childNodes;

			if (chart.isPrinting) { // block the button while in printing mode
				return;
			}

			chart.isPrinting = true;

			// hide all body content
			each(childNodes, function (node, i) {
				if (node.nodeType === 1) {
					origDisplay[i] = node.style.display;
					node.style.display = 'none';
				}
			});

			$body = $('body');
			$titleSection = $('#'+tmp_AREA_SECTIONS.TITLE_CONTAINER);
			$ctlSection = $('#'+tmp_AREA_SECTIONS.CHART_CTL);
			$summarySection = $('#'+tmp_AREA_SECTIONS.SUMMARY);
			$subTitleSection = $('#'+tmp_AREA_SECTIONS.SUB_TITLE_CONTAINER);
			$subTitle = $('#'+tmp_AREA_SECTIONS.SUB_TITLE);
			
			var preSubTitleFloat = $subTitle.css('float');
			$subTitle.css('float', 'none');
			// pull out the chart
			$appendIfPresent($body, $titleSection);
			$appendIfPresent($body, $subTitleSection);
			if (myChart.printType == 1) {
				$appendIfPresent($body, $ctlSection, 'hide');
			} else if (myChart.printType == 2) {
				$ctlSection.hide();
			}
			body.appendChild(container);
			$appendIfPresent($body, $summarySection);
			
			// print
			win.print();
			
			// allow the browser to prepare before reverting
			setTimeout(function () {
				// put the chart back in
				$appendIfPresent($(origParent), $titleSection);
				$subTitle.css('float', preSubTitleFloat);
				$appendIfPresent($(origParent), $subTitleSection);
				if (myChart.printType == 1) {
					$appendIfPresent($(origParent), $ctlSection, 'show');
				} else if (myChart.printType == 2) {
					$ctlSection.show();
				}
				origParent.appendChild(container);
				$appendIfPresent($(origParent), $summarySection);

				// restore all body content
				each(childNodes, function (node, i) {
					if (node.nodeType === 1) {
						node.style.display = origDisplay[i];
					}
				});
				chart.isPrinting = false;
				
				if (ARC.RenderedCharts) {
					var curCharts = ARC.RenderedCharts.getAll();
					if (curCharts) {
						for (var key in curCharts) {
							if (curCharts[key]) {
								curCharts[key].resizeChart();
							}
						}
					}
				}
			}, 500);
		};
	};
	
	var emailButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			configDivName = chart.popups.add('email', 'Email Report');
			initConfigWin();
			registerEvents();
			$('#'+self.MAIN_MENU_ID).click(function(e){
				onEmailClick();
				stopEventBubble(e);
				if (chart.menuItemClickAfter) {
					chart.menuItemClickAfter(self.MAIN_MENU_ID);
				}
			});
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.destroy = function(blnRmContainer) {
			if (!blnRmContainer && emailConfigPanel) {
				emailConfigPanel.destroy();
			}
			unRegisterEvents();
			$el(self.MAIN_MENU_ID).remove();
			$el(self.SUB_MENU_DIV_ID).remove();
		};
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		
		var disabled = false;
		var configDivName;
		var emailConfigPanel;
		var _eventNameSpace = 'event.ctl.email';
		var cHeight = chart.hcChart.options.chart.height;
		if (cHeight !== null && cHeight > 0) {
			cHeight += 30;
		}
		
		var registerEvents = function() {
			ARC.Events.register(_eventNameSpace, 'newone', self);
			ARC.Events.register(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.register(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		var unRegisterEvents = function() {
			ARC.Events.remove(_eventNameSpace, 'newone', self);
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		
		self.dealNotify = function(type, operation, notifier) {
			if (!notifier.uniqueID
					|| notifier.uniqueID === self.uniqueID) {
				return;
			}
			if (_eventNameSpace === type
					&& 'newone' === operation) {
				hideConfigWin();
				var contentDiv = chart.popups.getContentContainer(configDivName);
				set_innerHTML(contentDiv, '');
			}
			_dealCommonEventTypeFromChart.call(self, chart, type, operation, notifier, null);
		};
		
		var onEmailClick = function() {
			var blnGo = true;
			if (self.checkBefore) {
				blnGo = self.checkBefore.apply(chart);
			}
			if (!blnGo) {
				return;
			}
			showConfigWin();
		};
		
		var initConfigWin = function() {
			var div = document.getElementById(configDivName);
			emailConfigPanel = new YAHOO.widget.Panel(div, {
				width:"530px",
				underlay: "none",
				visible:false,
				draggable:true,
				close:true,
				modal:false,
				fixedcenter:false,
				constraintoviewport:true,
				zIndex:999
				});
			emailConfigPanel.render(document.body);
			div.style.display = "";
		};
		
		var showConfigWin = function() {
			if (emailConfigPanel !== null) {
				ARC.Events.notifyAll(_eventNameSpace, 'newone', self, fetchConfigInfo);
			}
		};
		
		var fetchConfigInfo = function() {
			var url = 'reportExport.action?operation=emailConfig&ignore='+new Date().getTime();
			var args = {chartId: chart.container};
			if (self.emailMark) {
				args.emailMark = self.emailMark;
			}
			$.post(url, args, callbackSuc);
		};
		
		var callbackSuc = function(data, textStatus) {
			var contentDiv = chart.popups.getContentContainer(configDivName);
			set_innerHTML(contentDiv, data);
			chart.popups.moveYUIPanelTo(true, emailConfigPanel, 10);
			emailConfigPanel.cfg.setProperty('visible', true);
		};
		
		var hideConfigWin = function() {
			if (emailConfigPanel !== null) {
				emailConfigPanel.cfg.setProperty('visible', false);
			}
		};
		
		var doSendEmail = function(emailAddressArg) {
			if (disabled) {
				return;
			}
			emailAddressTmp = '';
			if (emailAddressArg) {
				emailAddressTmp = emailAddressArg;
			}
			chart.tips.addProcessing("Sending E-mail...");
			disabled = true;
			var additionalOptions = {
				title: {
					align: 'left',
					text: chart.getTitleText()
				},
				subtitle: {
					align: 'left',
					text: chart.getSubTitleText()
				},
				yAxis: {
					title: {
						text: chart.axisTitles.yAxis
					}
				},
				xAxis: {
					title: {
						text: chart.axisTitles.xAxis
					}
				},
				chart: {
					height: 550,
					width: 900
				},
				legend: {
					x: -20
				}
			};
			if (!chart.isDatetimeType) {
				additionalOptions = $.extend(true, additionalOptions, {
					xAxis: {
						labels: {
							formatter: function() {
								return chart.exportChartLabelFormatter?chart.exportChartLabelFormatter.apply(chart, [this.value]):this.value;
							},
							style: {
								color: '#6D869F'
							},
							'width': 400
						}
					}
				});
			}
			var args = {
					type: 'email/pdf',
					filename: chart.getTitleText(),
					emailAddress: emailAddressTmp
				};
			if (self.emailMark) {
				args.emailMark = self.emailMark;
			}
			chart.emailChart(args, additionalOptions, function() {disabled = false;});
		};
		
		self.sendEmailToAddress = doSendEmail;
		self.hideConfigPanel = hideConfigWin;
	};
	
	var closeButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			configDivName = chart.popups.add('close', dlgTitle);
			initConfigWin();
			registerEvents();
			$('#'+self.MAIN_MENU_ID).click(function(e){
				showConfigWin();
				stopEventBubble(e);
			});
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.destroy = function(blnRmContainer) {
			if (!blnRmContainer && closeConfigPanel) {
				closeConfigPanel.destroy();
			}
			unRegisterEvents();
			$el(self.MAIN_MENU_ID).remove();
			$el(self.SUB_MENU_DIV_ID).remove();
		};
		var dlgTitle = 'Close Chart';
		var dlgText = 'Do you want to close this chart?';
		var disabled = false;
		var configDivName;
		var closeConfigPanel;
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT && (chart.__editPermissionCheck == null || chart.__editPermissionCheck())) {
				$('#'+self.MAIN_MENU_ID).show();
			} else {
				$('#'+self.MAIN_MENU_ID).hide();
			}
		};
		
		var _eventNameSpace = 'event.ctl.close.widget';
		
		var registerEvents = function() {
			ARC.Events.register(_eventNameSpace, 'newone', self);
		};
		var unRegisterEvents = function() {
			ARC.Events.remove(_eventNameSpace, 'newone', self);
		};
		
		self.dealNotify = function(type, operation, notifier) {
			if (!notifier.uniqueID
					|| notifier.uniqueID === self.uniqueID) {
				return;
			}
			if (_eventNameSpace === type
					&& 'newone' === operation) {
				hideConfigWin();
				var contentDiv = chart.popups.getContentContainer(configDivName);
				set_innerHTML(contentDiv, '');
			}
		};
		
		var initConfigWin = function() {
			var div = document.getElementById(configDivName);
			closeConfigPanel = new YAHOO.widget.SimpleDialog(div, {
				width:"250px",
				underlay: "none",
				visible:false,
				draggable:true,
				close:true,
				modal:false,
				fixedcenter:false,
				constraintoviewport:true,
				icon: YAHOO.widget.SimpleDialog.ICON_WARN,
				zIndex:999
				});
			closeConfigPanel.setHeader(dlgTitle);
			closeConfigPanel.cfg.setProperty("text", dlgText);
			closeConfigPanel.cfg.queueProperty("buttons", mybuttons);
			closeConfigPanel.render(document.body);
			div.style.display = "";
		};
		
		var showConfigWin = function() {
			if (closeConfigPanel !== null) {
				chart.popups.moveYUIPanelTo(true, closeConfigPanel, 10);
				closeConfigPanel.cfg.setProperty('visible', true);
				ARC.Events.notifyAll(_eventNameSpace, 'newone', self);
			}
		};
		
		var hideConfigWin = function() {
			if (closeConfigPanel !== null) {
				closeConfigPanel.cfg.setProperty('visible', false);
			}
		};
		
		var cancelClose = function() {
			this.hide();
		};
		var confirmToCloseChart = function() {
			this.hide();
			self.closeCallback(chart.container);
		};
		var mybuttons = [ { text:"Yes", handler: confirmToCloseChart}, 
                          { text:"Cancel", handler: cancelClose, isDefault:true} ];
	};
	
	var configButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			$('#'+self.MAIN_MENU_ID).click(function(e){
				prepareToConfig();
				stopEventBubble(e);
			});
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.destroy = function(blnRmContainer) {
			$el(self.MAIN_MENU_ID).remove();
			$el(self.SUB_MENU_DIV_ID).remove();
		};
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).show();
			} else {
				$('#'+self.MAIN_MENU_ID).hide();
			}
		};
		
		var prepareToConfig = function() {
			if (self.configFunc) {
				self.configFunc(chart.container);
			}
		};
	};
	
	var popupZoomInButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			configDivName = chart.popups.add('popup_zoomin', '');
			initConfigWin();
			registerEvents();
			$('#'+self.MAIN_MENU_ID).click(function(e){
				if (chart.menuItemClickAfter) {
					chart.menuItemClickAfter(self.MAIN_MENU_ID);
				}
				if (self.disabled) {
					return;
				}
				showConfigWin();
			});
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		self.uniqueID = self.MAIN_MENU_ID;
		self.destroy = function(blnRmContainer) {
			unRegisterEvents();
			if (!blnRmContainer && configPanel) {
				configPanel.destroy();
			}
			$el(self.MAIN_MENU_ID).remove();
			$el(self.SUB_MENU_DIV_ID).remove();
		};
		
		self.onStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		
		var registerEvents = function() {
			ARC.Events.register(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.register(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		var unRegisterEvents = function() {
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'nodata', self);
			ARC.Events.remove(_EVENT_COMMON_TYPE, 'hasdata', self);
		};
		self.dealNotify = function(type, operation, notifier) {
			_dealCommonEventTypeFromChart.call(self, chart, type, operation, notifier, null);
		};
		
		var configDivName;
		var configPanel;
		var chartDesc;
		
		var initConfigWin = function() {
			var div = document.getElementById(configDivName);
			configPanel = new YAHOO.widget.Panel(div, {
				width:"100px",
				underlay: "none",
				visible:false,
				draggable:true,
				close:true,
				modal:true,
				fixedcenter:false,
				constraintoviewport:true,
				zIndex:999
				});
			configPanel.render(document.body);
			div.style.display = "";
			configPanel.hideEvent.subscribe(function(){hideConfigWin()});
		};
		
		var showConfigWin = function() {
			if (configPanel != null) {
				var contentDiv = chart.popups.getContentContainer(configDivName);
				if (chart.getTitleText()) {
					chart.popups.setTitle(configDivName, chart.getTitleText());
				} else {
					chart.popups.setTitle(configDivName, 'Zoom in for chart');
				}
				var pageSize = getPageWH();
				configPanel.cfg.setProperty('width', pageSize.width-30+'px');
				configPanel.cfg.setProperty('height', pageSize.height-25+'px');
				chartDesc = chart.cloneTo({
					container: contentDiv,
					chartHeight: pageSize.height-75	,
					width: 1,
					displayCtl: {
						fullXaxisName: true,
						fullTableItems: true,
						blnNoControlBtn: true
					}
				});
				configPanel.cfg.setProperty('visible', true);
				if (chartDesc) {
					chartDesc.currentDashboard = chart.currentDashboard;
					chartDesc.render();
					$el(chartDesc.AREA_SECTIONS.TITLE_CONTAINER).hide();
					if (chartDesc.AREA_SECTIONS.TIP_UPDATE_AREA) {
						$el(chartDesc.AREA_SECTIONS.TIP_UPDATE_AREA.CONTAINER).hide();
					}
					$el(chartDesc.AREA_SECTIONS.SUB_TITLE_CONTAINER).hide();
				}
			}
		};
		
		var hideConfigWin = function() {
			if (configPanel !== null) {
				if (chartDesc) {
					chartDesc.destroy(false);
				}
				$el(chart.popups.getContentContainer(configDivName)).empty();
				configPanel.cfg.setProperty('visible', false);
			}
		};
	};
	
	var DEFAULT_WAITING_TIME = 10000;
	
	var CTL_BUTTON_NAMES = {
		EXPORT_MENU_: '__Export',
		PRINT_MENU_: '__Print',
		EMAIL_MENU_: '__Email',
		CLOSE_MENU_: '__Close',
		CONFIG_MENU_: '__Config',
		POPUP_ZOOMIN_MENU_: '__PopupZoomIn'
	};
	var DEF_CTL_BUTTON_STYLES = {
		EXPORT_MENU_: 'chart_exportBtn',
		PRINT_MENU_: 'chart_printBtn',
		EMAIL_MENU_: 'chart_emailBtn',
		CLOSE_MENU_: 'chart_closeBtn',
		CONFIG_MENU_: 'chart_configBtn',
		POPUP_ZOOMIN_MENU_: 'chart_zoomInBtn'
	};
	
	
	ARC.callbacks.addCandidate('__Email', function(chart, options) {
		chart.ctlButtons.add({
			name: '__Email',
			menuId: CTL_BUTTON_NAMES.EMAIL_MENU_,
			constructFunc: emailButton,
			style1: DEF_CTL_BUTTON_STYLES.EMAIL_MENU_,
			supportType: '+chart',
			tip: 'Email'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__Print', function(chart, options) {
		chart.ctlButtons.add({
			name: '__Print',
			menuId: CTL_BUTTON_NAMES.PRINT_MENU_,
			constructFunc: printButton,
			style1: DEF_CTL_BUTTON_STYLES.PRINT_MENU_,
			supportType: '+chart',
			tip: 'Print'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__Export', function(chart, options) {
		chart.ctlButtons.add({
			name: '__Export',
			menuId: CTL_BUTTON_NAMES.EXPORT_MENU_,
			constructFunc: exportButton,
			style1: DEF_CTL_BUTTON_STYLES.EXPORT_MENU_,
			supportType: '+chart',
			subMenu: true,
			tip: 'Export'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__Close', function(chart, options) {
		chart.ctlButtons.add({
			name: '__Close',
			menuId: CTL_BUTTON_NAMES.CLOSE_MENU_,
			constructFunc: closeButton,
			ctlContainer: 'MAIN_TITLE_CTL',
			style1: DEF_CTL_BUTTON_STYLES.CLOSE_MENU_,
			supportType: '+all',
			alwaysRender: true,
			tip: 'Close Chart'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__Config', function(chart, options) {
		chart.ctlButtons.add({
			name: '__Config',
			menuId: CTL_BUTTON_NAMES.CONFIG_MENU_,
			constructFunc: configButton,
			style1: DEF_CTL_BUTTON_STYLES.CONFIG_MENU_,
			supportType: '+all',
			alwaysRender: true,
			tip: 'Config Chart'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__PopupZoomIn', function(chart, options) {
		chart.ctlButtons.add({
			name: '__PopupZoomIn',
			menuId: CTL_BUTTON_NAMES.POPUP_ZOOMIN_MENU_,
			constructFunc: popupZoomInButton,
			style1: DEF_CTL_BUTTON_STYLES.POPUP_ZOOMIN_MENU_,
			supportType: '+all',
			alwaysRender: false,
			tip: 'Zoom in the chart'
		}, true);
	});
	
	$.extend(ARC, {
		chartStyles: DEF_CTL_BUTTON_STYLES,
		ctlCons: {
			"__Email": emailButton,
			"__Print": printButton,
			"__Export": exportButton
		}
	});
})(jQuery, YAHOO.util.Dom, YAHOO.util.Event, YAHOO.widget.Menu);