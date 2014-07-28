/**
 * for chart "Device SLA Compliance"
 */

(function(){
	var optionSelectionButton = function(chart, menuSpanId, options) {
		var self = this;
		self.render = function() {
			createOptions();
			$('#'+self.MAIN_MENU_ID).click(function(){
				toggleOptionSelection();
			});
		};
		self.MAIN_MENU_ID = chart.getMainMenuId(menuSpanId);
		self.SUB_MENU_DIV_ID = chart.getSubMenuId(menuSpanId);
		var menuId = self.SUB_MENU_DIV_ID;
		var $optionsGroup;
		
		var toggleOptionSelection = function() {
			$optionsGroup.toggle();
		};
		
		var runChartWithCertainType = function() {
			$options = $('#'+chart.AREA_SECTIONS.CUSTOM_CONTENT+'>div>input[name=typeOption]:checked');
			var value = $options.attr('value');
			if (!value) {
				value = 'all';
			}
			chart.additionalArgs.add("subType", value);
			if (chart.requestDataFunc) {
				chart.requestDataFunc();
			}
			$optionsGroup.hide();
		};
		
		var createOptions = function() {
			$optionsGroup = $("<div style='float: right; padding-right: 10px;'></div>").hide();
			$('#'+chart.AREA_SECTIONS.CUSTOM_CONTENT).append($optionsGroup);
			$optionsGroup.append($("<input type='radio' name='typeOption' value='all' checked id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_all'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_all'+"'>All</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='throughput' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_throughput'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_throughput'+"'>Throughput</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='crcError' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_crcError'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_crcError'+"'>CRC Error</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='airtime' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_airtime'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_airtime'+"'>Airtime</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='txDrop' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_txdrop'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_txdrop'+"'>Tx Drop</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='rxDrop' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_rxdrop'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_rxdrop'+"'>Rx Drop</label></input>"));
			
			$optionsGroup.append($("<input type='radio' name='typeOption' value='txRetry' id='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_txretry'+"'><label for='"
					+chart.AREA_SECTIONS.CUSTOM_CONTENT+'_txretry'+"'>Tx Retry</label></input>"));
			
			var $btnSave = $("<input type='button' value='Save'></input>").css("margin-left", '5px');
			var $btnCancel = $("<input type='button' value='Cancel'></input>");
			$btnCancel.click(function(){
				$optionsGroup.hide();
			});
			$btnSave.click(function(){
				runChartWithCertainType();
			});
			
			$optionsGroup.append($btnSave);
			$optionsGroup.append($btnCancel);
		};
	};

	var sla_device_chart;
	sla_device_chart = new AhReportChart.Chart({
		 container: "sla_device",
		 width: 600,
		 chartHeight: 300,
		 reportId: 1041,
		 legend: 'center bottom',
		 stackGroup: 'area',
		 xAxisType: 'datetime',
		 interval: 180000,
		 events: {
			tipFormatter: function(obj, ahChart) {
				var xName = ahChart.getDateStringWithTimeZone(obj.x);
				return xName +'<br/>'+
					obj.series.name +': <b>'+ obj.y+'</b>';
			}
		 },
		 buttons: [
			function(chart, options) {
				chart.ctlButtons.add({
					menuId: 'optionSelection',
					constructFunc: optionSelectionButton,
					style: 'chart_optionsBtn',
					tip: 'Select an option'
				});
			}
		 ],
		 expert: {
			 plotOptions: {
				series: {
					marker: {
						enabled: false
					}
				} 
			 },
			 yAxis: {
				 min: 0,
				 max: 100
			 }
		 }
	});
	
	$('#sla_device').show();
	sla_device_chart.render();
})();