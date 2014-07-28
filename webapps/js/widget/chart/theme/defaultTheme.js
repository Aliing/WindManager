(function($){
	var win = window,
		ARC = win.AhReportChart,
		Chart = ARC.Chart,
		pick = ARC.pick,
		$el = ARC.$el,
		pFloat = ARC.pFloat;
	
	var defTheme = function(chart, options) {
		var cwidth = '100%';
		var contentHeight = pick(options.contentHeight, 300);
		var $div1 = $("<div class='reportTopDescStyle'>"
							+"<div class='reportTitleStyle' id='" + chart.AREA_SECTIONS.MAIN_TITLE + "'></div>"
							+"<div class='reportSubTitleStyle' id='" + chart.AREA_SECTIONS.SUB_TITLE + "'></div>"
							+"</div>")
						.attr({
							"id": chart.AREA_SECTIONS.TITLE_CONTAINER
						})
						.width(cwidth);
		chart.$container.append($div1);
		
		var $divTip = $("<div class='reportTipStyle'></div>")
					.attr({
						"id": chart.AREA_SECTIONS.TIP_CONTENT
					})
					.width(cwidth);
		chart.$container.append($divTip);

		var $div4 = $("<div class='reportTitleCtlStyle' id='" + chart.AREA_SECTIONS.CHART_CTL + "'></div>")
						.width(cwidth).height('20px');
		chart.$container.append($div4);
		
		
		var $divCustom = $("<div class='reportCustomContentStyle'></div>")
					.attr({
						"id": chart.AREA_SECTIONS.CUSTOM_CONTENT
					})
					.width(cwidth);
		chart.$container.append($divCustom);
		
		var $div2 = $("<div></div>")
						.attr({
							"id": chart.AREA_SECTIONS.CHART_CONTENT,
							"height": contentHeight
						})
						.width(cwidth)
						.height(contentHeight+"px");
		chart.$container.append($div2);
		
		var $div3 = $("<div class='reportSummaryStyle'></div>")
						.attr({
							"id": chart.AREA_SECTIONS.SUMMARY
						})
						.width(cwidth);
		chart.$container.append($div3);
	};
	
	var refreshDescriptions = function(chart, data) {
		if (data.desc) {
			var desc = data.desc;
			if (desc.title) {
				$el(chart.AREA_SECTIONS.MAIN_TITLE).text(desc.title);
				$("a[href=#"+chart.AREA_SECTIONS.ANCHOR_NAME+"]").text(desc.title);
			}
			if (desc.subTitle) {
				$el(chart.AREA_SECTIONS.SUB_TITLE).text(desc.subTitle);
			}
			if (desc.summary) {
				$el(chart.AREA_SECTIONS.SUMMARY).html(desc.summary);
			}
			if (desc.timeOffset) {
				chart.timeOffset = pFloat(desc.timeOffset);
			}
		}
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
		refreshDescriptions(chart, data);
		refreshChartControlers(chart, data);
	};
	
	Chart.prototype.themes['default'] = {
		themeFunc: defTheme,
		descFunc: refreshDescriptions,
		beforeChartDataFunc: refreshBeforeChartData,
		afterChartDataFunc: refreshOtherContent
	};
	
	ARC.callbacks.addUsedCallbacks(['__Email','__Print','__Export']);
})(jQuery);