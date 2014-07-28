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
		consoleLog = ARC.consoleLog;
	
	var startOrPauseButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			$mainBtn.after($("<span></span>").attr({
				id: btnDesc
			}).addClass('rpBtnLineTip'));
			$el(btnDesc).addClass('startOrpauseDesc');
			//$el(btnDesc).text('Pause');
			$('#'+self.MAIN_MENU_ID).click(function(e){
				startOrPauseTheChart();
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
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		
		var startStyle = 'chart_startBtn_dash';
		var pauseStyle = 'chart_pauseBtn_dash';
		var $mainBtn = $el(self.MAIN_MENU_ID);
		var btnDesc = self.MAIN_MENU_ID + '_tip';
		
		var startOrPauseTheChart = function() {
			if ($mainBtn.hasClass(startStyle)) {
				$mainBtn.removeClass(startStyle);
				$mainBtn.addClass(pauseStyle);
				//$el(btnDesc).text('Pause');
				$mainBtn.attr({
					title: 'pause this chart'
				});
				chart.doStart();
			} else {
				$mainBtn.removeClass(pauseStyle);
				$mainBtn.addClass(startStyle);
				//$el(btnDesc).text('Start');
				$mainBtn.attr({
					title: 'start this chart'
				});
				chart.doPause();
			}
		};
	};
	
	var topologySelectButton = function(chart, menuSpanId, options) {
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
		
		self.doShow = function(blnShown) {
			if (blnShown) {
				$('#'+self.MAIN_MENU_ID).show();
			} else {
				$('#'+self.MAIN_MENU_ID).hide();
			}
		};
		
		var defStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		self.onStatusChange = function(oldStatus, status) {
			if (self.onMyStatusChange) {
				self.onMyStatusChange.apply(self, [chart, oldStatus, status]);
			} else {
				defStatusChange(oldStatus, status);
			}
		};
		
		var prepareToConfig = function() {
			if (self.configFunc) {
				self.configFunc(chart.container);
			}
		};
	};
	
	var periodSelectButton = function(chart, menuSpanId, options) {
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
		
		self.doShow = function(blnShown) {
			if (blnShown) {
				$('#'+self.MAIN_MENU_ID).show();
			} else {
				$('#'+self.MAIN_MENU_ID).hide();
			}
		};
		
		var defStatusChange = function(oldStatus, status) {
			if (status === chart.STATUS_MODE.EDIT) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		self.onStatusChange = function(oldStatus, status) {
			if (self.onMyStatusChange) {
				self.onMyStatusChange.apply(self, [chart, oldStatus, status]);
			} else {
				defStatusChange(oldStatus, status);
			}
		};
		
		var prepareToConfig = function() {
			if (self.configFunc) {
				self.configFunc(chart.container);
			}
		};
	};
	
	var chartCheckButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			var $ckBtn = $("<input></input>")
							.attr({
								'type': 'checkbox'
							}).click(function(e){
								prepareToConfig();
							});
			var $tipNote = $("<span class='chart_check_note_tip'></span>");
			var $tipWarningNote = $("<span class='chart_warning_note_tip'>Warning: uncheck it will clear configurations set for this chart.</span>");
			$('#'+self.MAIN_MENU_ID).append($tipNote);
			$('#'+self.MAIN_MENU_ID).append($tipWarningNote);
			$('#'+self.MAIN_MENU_ID).append($ckBtn);
			$chkEl = $('#'+self.MAIN_MENU_ID + " >input");
			$chkEl.click(function(e) {
				if (!blnCanEdit) {
					return false;
				}
				if (this.checked) {
					self.showNote(true);
				} else {
					self.showNote(false);
				}
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
		var blnCanEdit = true;
		var $chkEl;
		
		self.showNote = function(blnChecked) {
			if (blnChecked) {
				$el(self.MAIN_MENU_ID + " span.chart_check_note_tip")
					.removeClass("checked")	
					.addClass("checked")
					.html("Notice: Configurations of this chart is used now!")
					.show();
			} else {
				$el(self.MAIN_MENU_ID + " span.chart_check_note_tip")
					.removeClass("checked")
					.html("Check it to use configurations of this chart.")
					.show();
			}
		};
		self.disableCheck = function() {
			$chkEl.attr({'disabled': true});
		};
		self.enableCheck = function() {
			$chkEl.attr({'disabled': false});
		};
		self.isChecked = function() {
			if($chkEl.attr("checked")) {
				return true;
			}
			return false;
		};
		self.setCanCheck = function() {
			blnCanEdit = true;
		};
		self.setCheckStatus = function(checked, blnIngoreChk) {
			if (!blnIngoreChk && !blnCanEdit) {
				return;
			}
			$chkEl.attr({"checked": checked});
		};
		
		var defStatusChange = function(oldStatus, status) {
			if (chart.__editPermissionCheck == null || chart.__editPermissionCheck()) {
				$('#'+self.MAIN_MENU_ID).hide();
			} else {
				$('#'+self.MAIN_MENU_ID).show();
			}
		};
		self.onStatusChange = function(oldStatus, status) {
			if (self.onMyStatusChange) {
				self.onMyStatusChange.apply(self, [chart, oldStatus, status]);
			} else {
				defStatusChange(oldStatus, status);
			}
		};
		
		self.customizeNoteBehavior = function(func) {
			if (func) {
				func.apply(self, [$('#'+self.MAIN_MENU_ID + " >input"), $el(self.MAIN_MENU_ID + " span.chart_check_note_tip"), $el(self.MAIN_MENU_ID + " span.chart_warning_note_tip")]);
			}
		};
		
		var prepareToConfig = function() {
			$el(self.MAIN_MENU_ID + " span.chart_warning_note_tip").hide();
			if (self.configFunc) {
				self.configFunc(chart.container);
			}
		};
	};
	
	var CTL_BUTTON_NAMES = {
		START_OR_PAUSE_MENU_: '__StartOrPause',
		TOPOLOGY_MENU_: '__TopologySelect',
		PERIOD_MENU_: '__TimePeroid',
		CHART_CHECK_MENU_: '__ChartCheck'
	};
	var DEF_CTL_BUTTON_STYLES = {
		START_OR_PAUSE_MENU_: 'chart_pauseBtn_dash',
		TOPOLOGY_MENU_: 'chart_topologyBtn_dash',
		PERIOD_MENU_: 'chart_clockBtn_dash',
		CHART_CHECK_MENU_: 'checkChartSec'
	};
	
	ARC.callbacks.addCandidate('__StartOrPause', function(chart, options) {
		chart.ctlButtons.add({
			name: '__StartOrPause',
			menuId: CTL_BUTTON_NAMES.START_OR_PAUSE_MENU_,
			constructFunc: startOrPauseButton,
			style: DEF_CTL_BUTTON_STYLES.START_OR_PAUSE_MENU_,
			//ctlContainer: 'CONTROLBAR_BOTTOM_CENTER',
			supportType: '+chart+table+list+html',
			tip: 'pause this chart'
		});
	});
	
	ARC.callbacks.addCandidate('__TopologySelect', function(chart, options) {
		chart.ctlButtons.add({
			name: '__TopologySelect',
			menuId: CTL_BUTTON_NAMES.TOPOLOGY_MENU_,
			constructFunc: topologySelectButton,
			style: DEF_CTL_BUTTON_STYLES.TOPOLOGY_MENU_,
			ctlContainer: 'CONTROLBAR_BOTTOM_RIGHT',
			supportType: '+chart+table+list+html',
			alwaysRender: true,
			tip: 'select topology for this chart'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__TimePeroid', function(chart, options) {
		chart.ctlButtons.add({
			name: '__TimePeroid',
			menuId: CTL_BUTTON_NAMES.PERIOD_MENU_,
			constructFunc: periodSelectButton,
			style: DEF_CTL_BUTTON_STYLES.PERIOD_MENU_,
			ctlContainer: 'CONTROLBAR_BOTTOM_RIGHT',
			supportType: '+chart+table+list+html',
			alwaysRender: true,
			tip: 'select time period for this chart'
		}, true);
	});
	
	ARC.callbacks.addCandidate('__ChartCheck', function(chart, options) {
		chart.ctlButtons.add({
			name: '__ChartCheck',
			menuId: CTL_BUTTON_NAMES.CHART_CHECK_MENU_,
			constructFunc: chartCheckButton,
			style: DEF_CTL_BUTTON_STYLES.CHART_CHECK_MENU_,
			ctlContainer: 'CONTROLBAR_BOTTOM_RIGHT',
			supportType: '+chart+table+list+html',
			alwaysRender: true,
			tip: 'check to use chart special configurations'
		}, true);
	});
	
})(jQuery, YAHOO.util.Dom, YAHOO.util.Event, YAHOO.widget.Menu);