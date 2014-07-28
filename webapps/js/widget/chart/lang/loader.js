;(function() {
	var root = this;
	Aerohive = root.Aerohive || {};
	root.Aerohive = Aerohive;
	Aerohive.lang = Aerohive.lang || {};
	Aerohive.lang.chartLoader = (function() {
		var def_lang = 'en_US';
		var getFilePath = function(filename) {
			return 'js/widget/chart/lang/' + filename + '.js?t=W2TXD';
		}
		var load = function(filename, callback) {
			filename = filename || def_lang;
			head.js(getFilePath(filename), function() {
				if (callback) {
					callback();
				}
			});
		};
		
		return load;
	})();
	Aerohive.lang.encapPresentationText = function(textObj) {
		var result = {};
		for (var key in textObj) {
			result[key] = {
				text: textObj[key]
			};
		}
		return result;
	};
	
	Aerohive.presentation = Aerohive.presentation || {};
	Aerohive.presentation.chart = Aerohive.presentation.chart || {};
	Aerohive.presentation.chart.color = Aerohive.presentation.chart.color || {};
	var COLOR_SET_RED = ['#C84B00', '#FFC20E', '#F7A520', '#CC0066', '#CC6633', '#FF6699', '#FF66FF', '#FF0000', '#FF3399'];
	var COLOR_SET_BLUE = ['#0066FF', '#00CCCC', '#0093D1', '#330099', '#339999', '#3399CC'
	                      ,'#66CCFF', '#CCCCFF', '#00FF99', '#CCFF00'];
	Aerohive.presentation.chart.color.GeneratorHelper = function() {
		var self = this;
		var COLORS_TYPE = {
			'red': {
				'set': COLOR_SET_RED
			},
			'blue': {
				'set': COLOR_SET_BLUE
			}
		};
		var getADefinedRedSetColor = function(type) {
			var colorSet = COLORS_TYPE[type];
			return {
				type: type,
				func: function(index) {
					return colorSet.set[(index||0)%colorSet.set.length];
				}
			}
		};
		self.getADefinedRedSetColor = getADefinedRedSetColor('red');
		self.getADefinedBlueSetColor = getADefinedRedSetColor('blue'); 
	};
	Aerohive.presentation.chart.color.GENERATOR_HELPER = new Aerohive.presentation.chart.color.GeneratorHelper();
}).call(this);