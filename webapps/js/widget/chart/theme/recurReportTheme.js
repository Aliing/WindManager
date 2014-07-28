(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		$el = ARC.$el,
		pFloat = ARC.pFloat;
	
	var dashTheme = function(chart, options) {
		var cwidth = '100%';
		var contentHeight = pick(options.contentHeight, 275);
		chart.AREA_SECTIONS.CHANGE_STATUS_MARK = chart.AREA_SECTIONS.TITLE_CONTAINER + '_chgstatus';
		chart.AREA_SECTIONS.MAIN_TITLE_CTL = chart.AREA_SECTIONS.TITLE_CONTAINER + '_title_ctl';
		chart.AREA_SECTIONS.CONTROLBAR_BOTTOM = chart.container + '_ctl_bar_bottom';
		chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_LEFT = chart.AREA_SECTIONS.CONTROLBAR_BOTTOM + 'left';
		chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_CENTER = chart.AREA_SECTIONS.CONTROLBAR_BOTTOM + 'center';
		chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_RIGHT = chart.AREA_SECTIONS.CONTROLBAR_BOTTOM + 'right';
		chart.AREA_SECTIONS.SUB_TITLE_CONTAINER = chart.AREA_SECTIONS.SUB_TITLE + '_container';
		
		var $workDiv = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.WORK_CONTENT
						})
						.addClass("workerArea_dash")
						.width(cwidth);
		var $helpDiv = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.HELPER_CONTENT
						})
						.addClass("chelperContent")
						.width(cwidth);
		chart.$container.append($workDiv);
		chart.$container.append($helpDiv);
		var $div1 = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.TITLE_CONTAINER
						})
						.addClass('rpTopDescStyleDash')
						.width(cwidth);
		$workDiv.append($div1);
		var $div1Left = $("<div></div>")
							.addClass('rpTitleStyleDashOutter');
		var $div1ChgMark = $("<div></div>")
							.attr({
								"id": chart.AREA_SECTIONS.CHANGE_STATUS_MARK
							})
							.addClass('rpTitleMarkStyleDash');
		var $div1Title = $("<div></div>")
							.attr({
								"id": chart.AREA_SECTIONS.MAIN_TITLE
							})
							.addClass('rpTitleStyleDash');
		var $div1TitleCtl = $("<div></div>")
							.attr({
								"id": chart.AREA_SECTIONS.MAIN_TITLE_CTL
							})
							.addClass('rpTitleCtlStyleDash');
		$div1Left.append($div1ChgMark);
		$div1Left.append($div1Title);
		$div1.append($div1Left);
		$div1.append($div1TitleCtl);
		
		var $div_separator = $("<div></div>").addClass("my-chart-separator");
		$workDiv.append($div_separator);
		
		var $div_sub = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.SUB_TITLE_CONTAINER
						})
						.width(cwidth)
						.height("20px");
		var $divSubTitle = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.SUB_TITLE
						})
						.addClass('reportSubTitleStyleDash');
		var $div1Ctl = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.CHART_CTL
						})
						.addClass('rpTitleCtlStyleDash');
		$div_sub.append($divSubTitle);
		$div_sub.append($div1Ctl);
		$workDiv.append($div_sub);
		
		var $divTip = $("<div></div>")
					.attr({
						"id": chart.AREA_SECTIONS.TIP_CONTENT
					})
					.addClass('reportTipStyle_dash')
					.width(cwidth);
		$workDiv.append($divTip);
		
		var $divCustom = $("<div></div>")
					.attr({
						"id": chart.AREA_SECTIONS.CUSTOM_CONTENT
					})
					.addClass('reportCustomContentStyle')
					.width(cwidth);
		$workDiv.append($divCustom);
		
		var $div2 = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.CHART_CONTENT,
							"height": contentHeight
						})
						.width($workDiv.width())
						.height(contentHeight+"px");
		$workDiv.append($div2);
		
		var $div2tbl = $("<div></div>")
				.attr({
					"id": chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER,
					"height": contentHeight
				})
				.width(cwidth)
				.height(contentHeight+"px")
				.addClass('daTblContainer')
				.hide();
		$workDiv.append($div2tbl);
		
		var $nodataDiv = $('<div><div class="daNoDataContainerText">No Data</div></div>')
				.attr({
					"id": chart.AREA_SECTIONS.NODATA_CONTAINER
				})
				.addClass('daNoDataContainer')
				.width(cwidth)
				.height(contentHeight+"px")
				.hide();
		$workDiv.append($nodataDiv);
		
		var $mainContentTipDiv = $('<div></div>')
				.attr({
					"id": chart.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO
				})
				.addClass('daMainContentTip')
				.width(cwidth)
				.height(contentHeight+"px")
				.hide();
		$workDiv.append($mainContentTipDiv);
		
		var $div3 = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.SUMMARY
						})
						.addClass('reportSummaryStyle')
						.width(cwidth);
		$workDiv.append($div3);
		
		//if (chart.isRenderedAsChart() || chart.isRenderedAsTable()) {
			var $divCtlbar = $("<div id='"+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM+"'>"
								+ "<table><tr><td align='left'><div id='"+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_LEFT+"'></div></td>"
								+ "<td align='center'><div id='"+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_CENTER+"'></div></td>"
								+ "<td align='right'><div id='"+chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_RIGHT+"'></div></td>"
								+ "</tr></table></div>");
			//$divCtlbar.hide();
			$divCtlbar.addClass('rpCustomCtlBtnDash');
			$workDiv.append($divCtlbar);
			$el(chart.AREA_SECTIONS.CONTROLBAR_BOTTOM + '>table').css('width', '100%');
			$el(chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_LEFT).css('float', 'left');
			//$el(chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_CENTER).html('center');
			$el(chart.AREA_SECTIONS.CONTROLBAR_BOTTOM_RIGHT).css('float', 'right');
		//}
		//if (chart.isRenderedAsHTML()) {
		//	var $divPadding = $("<div></div>").height('25px');
		//	$workDiv.append($divPadding);
		//}
			
		chart.resetChartContainerWidth = function() {
			$el(chart.AREA_SECTIONS.CHART_CONTENT).width(cwidth);
		};
	};
	
	var refreshDescriptions = function(chart, data) {
		if (data.desc) {
			var desc = data.desc;
			if (desc.title) {
				//$el(chart.AREA_SECTIONS.MAIN_TITLE).text(desc.title);
				chart.syncAnchorTextWithTitle();
			}
			if (desc.subTitle) {
				$el(chart.AREA_SECTIONS.SUB_TITLE).text(desc.subTitle);
			}
			if (desc.timeOffset) {
				chart.timeOffset = pFloat(desc.timeOffset);
			}
		}
		
		chart.url = "reportData.action";
		chart.operation = "data";
	};
	
	var refreshChartControlers = function(chart, data) {
		if (data.config) {
		}
	};
	
	var refreshOtherContent = function(chart, data) {
		if (data.desc) {
			var desc = data.desc;
			if (desc.yTitle) {
				chart.hcChart.yAxis[0].setTitle({text: desc.yTitle}, false);
				chart.axisTitles.yAxis = desc.yTitle;
			}
		}
	};
	
	var refreshBeforeChartData = function(chart, data) {
		if (chart.whenRefreshData) {
			chart.whenRefreshData.apply(chart, [chart, data]);
		}
		refreshDescriptions(chart, data);
		refreshChartControlers(chart, data);
		
		chart.setLastRefreshTime(new Date().getTime());
		hideContentForCertainChartMode(chart);
	};
	
	var hideContentForCertainChartMode = function(chart) {
		if (chart.isInViewStatus()) {
			$el(chart.AREA_SECTIONS.MAIN_CONTENT_TIP_INFO).hide();
			$el(chart.AREA_SECTIONS.HELPER_CONTENT).hide();
			$el(chart.AREA_SECTIONS.SUB_TITLE_CONTAINER).show();
		} else if (chart.isInEditStatus()) {
			$el(chart.AREA_SECTIONS.CHART_CONTENT).hide();
			$el(chart.AREA_SECTIONS.NODATA_CONTAINER).hide();
			$el(chart.AREA_SECTIONS.TABLE_CONTENT_CONTAINER).hide();
			$el(chart.AREA_SECTIONS.SUB_TITLE_CONTAINER).hide();
		}
	};
	
	Chart.prototype.themes['dash'] = {
		themeFunc: dashTheme,
		descFunc: refreshDescriptions,
		beforeChartDataFunc: refreshBeforeChartData,
		afterChartDataFunc: refreshOtherContent
	};
	
	Chart.prototype.afterContentChangedFunc = function(changed) {
		if (this.isInEditStatus() && changed) {
			$el(this.AREA_SECTIONS.CHANGE_STATUS_MARK).text("* ");
		} else {
			$el(this.AREA_SECTIONS.CHANGE_STATUS_MARK).text("");
		}
	};
	
	var old_onstatuschanged = Chart.prototype.__onStatusChanged;
	Chart.prototype.__onStatusChanged = function(oldStatus, status) {
		if (old_onstatuschanged) {
			old_onstatuschanged.apply(this, [oldStatus, status]);
		}
	};
	
	Chart.prototype.whenRefreshData = null;
	var DEF_ADDITIONAL_MENU_BTN_OPTION = {
		ctlContainer: 'MAIN_TITLE_CTL', 
		groupby: 'show'
	};
	var prepareAdditionalOptionsForMenuBtn = function(menuId, options) {
		if (options) {
			options = $.extend(true, {}, DEF_ADDITIONAL_MENU_BTN_OPTION, options);
		} else {
			options = DEF_ADDITIONAL_MENU_BTN_OPTION;
		}
		return [menuId, options];
	};
	ARC.callbacks.addUsedCallbacks([prepareAdditionalOptionsForMenuBtn('__Email', {menuName: 'Email', style: 'chart_emailBtn_li'})
									, prepareAdditionalOptionsForMenuBtn('__Print', {menuName: 'Print', style: 'chart_printBtn_li'})
									, prepareAdditionalOptionsForMenuBtn('__Export', {menuName: 'Export', style: 'chart_exportBtn_li'
																			, tip: ''
																			, menuOptions: {subMenuType: 'subli'}
																		})
									, prepareAdditionalOptionsForMenuBtn('__PopupZoomIn', {menuName: 'Zoom In', style: 'chart_zoomInBtn_li'})
									, '__Close']);
})(jQuery);