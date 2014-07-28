var hm = hm || {};
hm.sa = hm.sa || {};

hm.sa.initFFTChart = function(dash, pos, band, channels, center, span, rlevel, vscale, sample) {
	var chart = {
			dash: dash, pos: pos,
			name: "Real-time FFT",
			margins: {left: 35, bottom: 20, top: 22, right: 10, legend_small: 0, legend_large: 50},
			tbar: {create: hm.sa.fftToolbar, band: band, channels: channels,
				   center: center, span: span, rlevel: rlevel, vscale: vscale, sample: sample},
			axis_x: {unit: "GHz", lbl: "GHz"}, 	// Most attributes are filled
			axis_y: {unit: "dBm", lbl: "dBm"},  // in by functions below.
			hover_xy: hm.sa.fftHoverXY,
			series_colors: ["#B2B2B2", "#800000", "#BF4000", "#FF8000", "#FFA300", "#FFC500", "#FFE800", "#D4FF0B", "#55FF2B", "#00FF58", "#00FF9F", "#00FFE7", "#01E4FC", "#02BBF8", "#0292F4", "#066DE3", "#0F50B8", "#18338D", "#211662", "#270346", "#2D002A"],
			type: "line", smooth: false
		};
	hm.sa.setFFTChannelTicks(chart);
	hm.sa.setFFTAxisXboundaries(chart);
	hm.sa.setFFTAxisYticks(chart);
	hm.cs.initDashChart(dash, chart, pos);
}

hm.sa.initDutyChart = function(dash, pos, band, channels, center, span, min, max, sample) {
	var chart = {
			dash: dash, pos: pos,
			name: "FFT Duty Cycle",
			margins: {left: 39, bottom: 20, top: 22, right: 10, legend_small: 0, legend_large: 50},
			tbar: {create: hm.sa.dutyToolbar, band: band, channels: channels,
				   center: center, span: span, min: min, max: max, sample: sample},
			axis_x: {unit: "GHz", lbl: "GHz"}, 	 // Most attributes are filled
			axis_y: {ticks_start: 0, unit: "%"}, // in by functions below.
			hover_xy: hm.sa.fftHoverXY,
			series_colors: ["#B2B2B2", "#800000", "#BF4000", "#FF8000", "#FFA300", "#FFC500", "#FFE800", "#D4FF0B", "#55FF2B", "#00FF58", "#00FF9F", "#00FFE7", "#01E4FC", "#02BBF8", "#0292F4", "#066DE3", "#0F50B8", "#18338D", "#211662", "#270346", "#2D002A"],
			type: "line", smooth: false
		};
	hm.sa.setFFTChannelTicks(chart);
	hm.sa.setFFTAxisXboundaries(chart);
	hm.sa.setDutyAxisYticks(chart);
	hm.cs.initDashChart(dash, chart, pos);
}

hm.sa.initSweptFFTChart = function(dash, pos, band, channels, center, span, url) {
	var chart = {
			dash: dash, pos: pos,
			name: "Swept Spectrogram", url: url,
			margins: {left: 45, bottom: 20, top: 22, right: 10, legend_small: 0, legend_large: 50},
			tbar: {create: hm.sa.sweptFftToolbar, band: band, channels: channels,
				   center: center, span: span},
			axis_x: {unit: "GHz", lbl: "GHz"}, 	 // Most attributes are filled
			axis_y: {start: 0, end: 200,
					 ticks_start: 0, ticks_interval: 100, ticks: ["0", "-100", "-200"],
					 lbl: "Sweep"}, // in by functions below.
			series_colors: ["#b00e22"]
		};
	hm.sa.setFFTChannelTicks(chart);
	hm.sa.setFFTAxisXboundaries(chart);
	hm.cs.initDashChart(dash, chart, pos);
}

hm.sa.initSweptDutyChart = function(dash, pos, band, channels, center, span, url) {
	var chart = {
			dash: dash, pos: pos,
			name: "Swept Spectrogram - FFT Duty Cycle", url: url,
			margins: {left: 45, bottom: 20, top: 22, right: 10, legend_small: 0, legend_large: 50},
			tbar: {create: hm.sa.sweptDutyToolbar, band: band, channels: channels,
				   center: center, span: span},
			axis_x: {unit: "GHz", lbl: "GHz"}, 	 // Most attributes are filled
			axis_y: {start: 0, end: 200,
					 ticks_start: 0, ticks_interval: 100, ticks: ["0", "-100", "-200"],
					 lbl: "Sweep"}, // in by functions below.
			series_colors: ["#b00e22"],
			smooth: false
		};
	hm.sa.setFFTChannelTicks(chart);
	hm.sa.setFFTAxisXboundaries(chart);
	hm.cs.initDashChart(dash, chart, pos);
}

hm.sa.fftToolbar = function(chart, canvas, div, width, height) {
	hm.sa.markChannels(chart, canvas, div, width, height);
	if (height == 0) {
		return;
	}
	var x = 8, y = chart.margins.top+5;
	var changeBand = function(cb, chart) {
		hm.sa.changeBand(cb, chart, "updateFFTBand&fftBand=");
	}
	var changeChannels = function(cb, chart) {
		hm.sa.changeChannels(cb, chart, "updateFFTChannels&fftChannels=");
	}
	var changeCenter = function(cb, chart) {
		hm.sa.changeCenter(cb, chart, "updateFFTCenter&fftCenter=");
	}
	var changeSpan = function(cb, chart) {
		hm.sa.changeSpan(cb, chart, "updateFFTSpan&fftSpan=");
	}
	hm.sa.channelControls(chart, div, x, y, changeBand, changeChannels, changeCenter, changeSpan);
	var l1 = hm.cs.createLabel(y, x+360, "Reference Level", "titleLbl");
	div.appendChild(l1);
	var levelItems = ["0 dBm", "-10 dBm", "-20 dBm", "-30 dBm", "-40 dBm", "-50 dBm", "-60 dBm", "-70 dBm", "-80 dBm", "-90 dBm"];
	var levelValues = [0, -10, -20, -30, -40, -50, -60, -70, -80, -90];
	select = hm.cs.createSelect(y-2, x+456, hm.sa.changeFFTRLevel, chart, levelItems, levelValues, chart.tbar.rlevel);
	div.appendChild(select);
	l2 = hm.cs.createLabel(y+22, x+360, "Vertical Scale", "titleLbl");
	div.appendChild(l2);
	hm.cs.alignRight(l1, l2);
	var vscaleItems = ["10 dBm", "5 dBm", "1 dBm"];
	var vscaleValues = [10, 5, 1];
	select = hm.cs.createSelect(y+21, x+456, hm.sa.changeFFTVScale, chart, vscaleItems, vscaleValues, chart.tbar.vscale);
	div.appendChild(select);

	var check = hm.cs.createCheckBox(y-2, x+555, hm.sa.changeFFTSample,chart,chart.tbar.sample);
	//select = hm.cs.createSelect(y-2, x+602, hm.sa.changeFFTSample, chart, ["Average", "Max Hold"],[0,1], chart.tbar.sample);
	div.appendChild(check);
	if (chart.tbar.sample){
		check.setAttribute("checked","checked");
	}
	l1 = hm.cs.createLabel(y, x+580, "Max Hold", "titleLbl");
	div.appendChild(l1);
}

hm.sa.dutyToolbar = function(chart, canvas, div, width, height) {
	hm.sa.markChannels(chart, canvas, div, width, height);
	if (height == 0) {
		return;
	}
	var x = 8, y = chart.margins.top+5;
	var changeBand = function(cb, chart) {
		hm.sa.changeBand(cb, chart, "updateDutyBand&dutyBand=");
	}
	var changeChannels = function(cb, chart) {
		hm.sa.changeChannels(cb, chart, "updateDutyChannels&dutyChannels=");
	}
	var changeCenter = function(cb, chart) {
		hm.sa.changeCenter(cb, chart, "updateDutyCenter&dutyCenter=");
	}
	var changeSpan = function(cb, chart) {
		hm.sa.changeSpan(cb, chart, "updateDutySpan&dutySpan=");
	}
	hm.sa.channelControls(chart, div, x, y, changeBand, changeChannels, changeCenter, changeSpan);
	var l1 = hm.cs.createLabel(y, x+360, "Maximum", "titleLbl");
	div.appendChild(l1);
	var maxItems = ["100 %", "80 %", "60 %"];
	var maxValues = [100, 80, 60];
	select = hm.cs.createSelect(y-2, x+420, hm.sa.changeDutyMax, chart, maxItems, maxValues, chart.tbar.max);
	div.appendChild(select);
	l2 = hm.cs.createLabel(y+22, x+360, "Minimum", "titleLbl");
	div.appendChild(l2);
	hm.cs.alignRight(l1, l2);
	var minItems = ["40 %", "20 %", "0 %"];
	var minValues = [40, 20, 0];
	select = hm.cs.createSelect(y+21, x+420, hm.sa.changeDutyMin, chart, minItems, minValues, chart.tbar.min);
	div.appendChild(select);
	
	var check = hm.cs.createCheckBox(y-2, x+500, hm.sa.changeDutySample,chart,chart.tbar.sample);
	//select = hm.cs.createSelect(y-2, x+602, hm.sa.changeFFTSample, chart, ["Average", "Max Hold"],[0,1], chart.tbar.sample);
	div.appendChild(check);
	if (chart.tbar.sample){
		check.setAttribute("checked","checked");
	}
	l1 = hm.cs.createLabel(y, x+525, "Max Hold", "titleLbl");
	div.appendChild(l1);
}

hm.sa.sweptFftToolbar = function(chart, canvas, div, width, height) {
	hm.sa.markChannels(chart, canvas, div, width, height);
	if (height == 0) {
		return;
	}
	var x = 8, y = chart.margins.top+5;
	var changeBand = function(cb, chart) {
		hm.sa.changeBand(cb, chart, "updateSweptFFTBand&sweptFftBand=");
	}
	var changeChannels = function(cb, chart) {
		hm.sa.changeChannels(cb, chart, "updateSweptFFTChannels&sweptFftChannels=");
	}

	hm.sa.sweptChannelControls(chart, div, x, y, changeBand, changeChannels);

	//var l1 = hm.cs.createLabel(y, x+358, "Sample", "titleLbl");
	//div.appendChild(l1);
	//select = hm.cs.createSelect(y-2, x+408, hm.sa.changeSweptFFTSample, chart, ["Average", "Max Hold"],[0,1], chart.tbar.sample);
	//div.appendChild(select);
	var l2 = hm.cs.createLabel(y+22, x, "RSSI: -35", "titleLbl");
	div.appendChild(l2);
	var tbs = hm.sa.createColorLegend(y+22, x+56);
	div.appendChild(tbs);
	var l2 = hm.cs.createLabel(y+22, x+220, "-100 dBm", "titleLbl");
	div.appendChild(l2);
}

hm.sa.sweptDutyToolbar = function(chart, canvas, div, width, height) {
	hm.sa.markChannels(chart, canvas, div, width, height);
	if (height == 0) {
		return;
	}
	var x = 8, y = chart.margins.top+5;
	var changeBand = function(cb, chart) {
		hm.sa.changeBand(cb, chart, "updateSweptDutyBand&sweptDutyBand=");
	}
	var changeChannels = function(cb, chart) {
		hm.sa.changeChannels(cb, chart, "updateSweptDutyChannels&sweptDutyChannels=");
	}
	hm.sa.sweptChannelControls(chart, div, x, y, changeBand, changeChannels);
	var l2 = hm.cs.createLabel(y+22, x, "Duty:  0%", "titleLbl");
	div.appendChild(l2);
	var tbs = hm.sa.createColorDutyLegend(y+22, x+56);
	div.appendChild(tbs);
	var l2 = hm.cs.createLabel(y+22, x+220, "100%", "titleLbl");
	div.appendChild(l2);
}

hm.sa.channelControls = function(chart, div, x, y, changeBand, changeChannels, changeCenter, changeSpan) {
	var l1 = hm.cs.createLabel(y, x, "Band", "titleLbl");
	div.appendChild(l1);
	if (chart.tbar.band == 2400) {
		var bandItems = ["2.400 - 2.500 GHz"];
		var bandValues = [2400];
	} else {
		var bandItems = ["5.150 - 5.350 GHz", "5.470 - 5.725 GHz", "5.725 - 5.850 GHz"];
		var bandValues = [5150, 5470, 5725];	
	}

	var select = hm.cs.createSelect(y-2, x+57, changeBand, chart, bandItems, bandValues, chart.tbar.band);
	div.appendChild(select);
	var l2 = hm.cs.createLabel(y+22, x, "Channels", "titleLbl");
	div.appendChild(l2);
	if (chart.tbar.band == 2400) {
		var channelItems = ["1, 6, 11", "1, 8, 13", "1, 5, 9, 13"];
		var channelValues = [1611, 1813, 15913];
	} else if (chart.tbar.band == 5150) {
		var channelItems = ["36-64"];
		var channelValues = [3664];
	} else if (chart.tbar.band == 5470) {
		var channelItems = ["100-140"];
		var channelValues = [100140];
	} else if (chart.tbar.band == 5725) {
		var channelItems = ["149-165"];
		var channelValues = [149165];
	}
	select = hm.cs.createSelect(y+21, x+57, changeChannels, chart, channelItems, channelValues, chart.tbar.channels);
	div.appendChild(select);
	hm.cs.alignRight(l1, l2);
	l1 = hm.cs.createLabel(y, x+208, "Center", "titleLbl");
	div.appendChild(l1);
	if (chart.tbar.band == 2400) {
		var centerItems = ["2.412 GHz", "2.417 GHz", "2.422 GHz", "2.427 GHz", "2.432 GHz", "2.437 GHz", "2.442 GHz", "2.447 GHz", "2.452 GHz", "2.457 GHz", "2.462 GHz", "2.467 GHz", "2.472 GHz"];
		var centerValues = [2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472];
	} else if (chart.tbar.band == 5150) {
		var centerItems = ["5.180 GHz", "5.200 GHz", "5.220 GHz", "5.240 GHz", "5.250 GHz", "5.260 GHz", "5.280 GHz", "5.300 GHz", "5.320 GHz"];
		var centerValues = [5180, 5200, 5220, 5240, 5250, 5260, 5280, 5300, 5320];
	} else if (chart.tbar.band == 5470) {
		var centerItems = ["5.500 GHz", "5.520 GHz", "5.540 GHz", "5.560 GHz", "5.580 GHz", "5.600 GHz", "5.620 GHz", "5.640 GHz", "5.660 GHz", "5.680 GHz", "5.700 GHz"];
		var centerValues = [5500, 5520, 5540, 5560, 5580, 5600, 5620, 5640, 5660, 5680, 5700];
	} else if (chart.tbar.band == 5725) {
		var centerItems = ["5.745 GHz", "5.765 GHz", "5.785 GHz", "5.805 GHz", "5.825 GHz"];
		var centerValues = [5745, 5765, 5785, 5805, 5825];
	}
	select = hm.cs.createSelect(y-2, x+250, changeCenter, chart, centerItems, centerValues, chart.tbar.center);
	div.appendChild(select);
	l2 = hm.cs.createLabel(y+22, x+208, "Span", "titleLbl");
	div.appendChild(l2);
	hm.cs.alignRight(l1, l2);
	if (chart.tbar.band == 2400) {
		var spanItems = ["80 MHz", "60 MHz", "40 MHz", "20 MHz", "10 MHz"];
		var spanValues = [80, 60, 40, 20, 10];
	} else if (chart.tbar.band == 5150 || chart.tbar.band == 5725) {
		var spanItems = ["200 MHz", "100 MHz", "80 MHz", "60 MHz", "40 MHz", "20 MHz"];
		var spanValues = [200, 100, 80, 60, 40, 20];
	} else if (chart.tbar.band == 5470) {
		var spanItems = ["240 MHz", "200 MHz", "100 MHz", "80 MHz", "60 MHz", "40 MHz", "20 MHz"];
		var spanValues = [240, 200, 100, 80, 60, 40, 20];
	}
	select = hm.cs.createSelect(y+21, x+250, changeSpan, chart, spanItems, spanValues, chart.tbar.span);
	div.appendChild(select);
}

hm.sa.sweptChannelControls = function(chart, div, x, y, changeBand, changeChannels) {
	var l1 = hm.cs.createLabel(y, x, "Band", "titleLbl");
	div.appendChild(l1);
	if (chart.tbar.band == 2400) {
		var bandItems = ["2.400 - 2.500 GHz"];
		var bandValues = [2400];
	} else {
		var bandItems = ["5.150 - 5.350 GHz", "5.470 - 5.725 GHz", "5.725 - 5.850 GHz"];
		var bandValues = [5150, 5470, 5725];	
	}
	
	var select = hm.cs.createSelect(y-2, x+57, changeBand, chart, bandItems, bandValues, chart.tbar.band);
	div.appendChild(select);
	var l2 = hm.cs.createLabel(y, x+200 , "Channels", "titleLbl");
	div.appendChild(l2);
	if (chart.tbar.band == 2400) {
		var channelItems = ["1, 6, 11", "1, 8, 13", "1, 5, 9, 13"];
		var channelValues = [1611, 1813, 15913];
	} else if (chart.tbar.band == 5150) {
		var channelItems = ["36-64"];
		var channelValues = [3664];
	} else if (chart.tbar.band == 5470) {
		var channelItems = ["100-140"];
		var channelValues = [100140];
	} else if (chart.tbar.band == 5725) {
		var channelItems = ["149-165"];
		var channelValues = [149165];
	}
	select = hm.cs.createSelect(y-2, x+265, changeChannels, chart, channelItems, channelValues, chart.tbar.channels);
	div.appendChild(select);
}

hm.sa.createColorLegend  = function(top, left) {
var rssiColors = ['#800000', '#951500', '#aa2b00', '#bf4000', '#d45500', '#ea6b00', '#ff8000', '#ff8c00',
				  '#ff9700', '#ffa300', '#ffae00', '#ffba00', '#ffc500', '#ffd100', '#ffdc00', '#ffe800',
				  '#fff300', '#ffff00', '#d4ff0b', '#aaff15', '#80ff20', '#55ff2b', '#2bff35', '#00ff40',
				  '#00ff58', '#00ff70', '#00ff88', '#00ff9f', '#00ffb7', '#00ffcf', '#00ffe7', '#00ffff',
				  '#00f1fe', '#01e4fc', '#01d6fb', '#01c8f9', '#02bbf8', '#02adf6', '#029ff5', '#0292f4',
				  '#0384f2', '#0376f1', '#066de3', '#0963d4', '#0c5ac6', '#0f50b8', '#1246aa', '#153d9c',
				  '#18338d', '#1b2a7f'];
	var tab = document.createElement("table");
	var tbody=document.createElement("tbody");
	tab.style.position = "absolute";
	if (YAHOO.env.ua.ie > 0) {top--;} else
	if (YAHOO.env.ua.webkit > 0) {top -= 2;}
	tab.style.top = top + "px";
	tab.style.left = left + "px";
	tab.setAttribute("cellSpacing",0);
	tab.setAttribute("cellPadding",0);
	var tr = document.createElement("tr");
	for (var i = 0; i < rssiColors.length; i++) {
		var td=document.createElement("td");
		td.style.backgroundColor=rssiColors[i];
		//td.setAttribute("bgcolor",rssiColors[i]);
		td.width="3px";
		td.height="18px";
		//td.innerText= '&nbsp;'; 
		tr.appendChild(td); 
	}
	tbody.appendChild(tr); 
	tab.appendChild(tbody); 
	return tab;
}

hm.sa.createColorDutyLegend  = function(top, left) {
var rssiColors = ['#800000', '#951500', '#aa2b00', '#bf4000', '#d45500', '#ea6b00', '#ff8000', '#ff8c00',
				  '#ff9700', '#ffa300', '#ffae00', '#ffba00', '#ffc500', '#ffd100', '#ffdc00', '#ffe800',
				  '#fff300', '#ffff00', '#d4ff0b', '#aaff15', '#80ff20', '#55ff2b', '#2bff35', '#00ff40',
				  '#00ff58', '#00ff70', '#00ff88', '#00ff9f', '#00ffb7', '#00ffcf', '#00ffe7', '#00ffff',
				  '#00f1fe', '#01e4fc', '#01d6fb', '#01c8f9', '#02bbf8', '#02adf6', '#029ff5', '#0292f4',
				  '#0384f2', '#0376f1', '#066de3', '#0963d4', '#0c5ac6', '#0f50b8', '#1246aa', '#153d9c',
				  '#18338d', '#1b2a7f'];
	var tab = document.createElement("table");
	var tbody=document.createElement("tbody");
	tab.style.position = "absolute";
	if (YAHOO.env.ua.ie > 0) {top--;} else
	if (YAHOO.env.ua.webkit > 0) {top -= 2;}
	tab.style.top = top + "px";
	tab.style.left = left + "px";
	tab.setAttribute("cellSpacing",0);
	tab.setAttribute("cellPadding",0);
	var tr = document.createElement("tr");
	for (var i = rssiColors.length; i >= 0; i--) {
		var td=document.createElement("td");
		td.style.backgroundColor=rssiColors[i];
		//td.setAttribute("bgcolor",rssiColors[i]);
		td.width="3px";
		td.height="18px";
		//td.innerText= '&nbsp;'; 
		tr.appendChild(td); 
	}
	tbody.appendChild(tr); 
	tab.appendChild(tbody); 
	return tab;
}



hm.sa.markChannels = function(chart, canvas, div, width, height) {
	var fs = new Array();
	var chs = new Array();
	if (chart.tbar.channels == 1611) {
		fs = [2412, 2437, 2462];
		chs = [1, 6, 11];
	} else if (chart.tbar.channels == 1813) {
		fs = [2412, 2447, 2472];
		chs = [1, 8, 13];
	} else if (chart.tbar.channels == 15913) {
		fs = [2412, 2432, 2452, 2472];
		chs = [1, 5, 9, 13];
	} else if (chart.tbar.channels == 3664) {
		fs = [5180, 5200, 5220, 5240, 5260, 5280, 5300, 5320];
		chs = [36, 40, 44, 48, 52, 56, 60, 64];
	} else if (chart.tbar.channels == 100140) {
		fs = [5500, 5520, 5540, 5560, 5580, 5600, 5620, 5640, 5660, 5680, 5700];
		chs = [100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140];
	} else if (chart.tbar.channels == 149165) {
		fs = [5745, 5765, 5785, 5805, 5825];
		chs = [149, 153, 157, 161, 165];
	}
	hm.sa.channelMarkers(chart, canvas, div, width, height, fs, chs);
}

hm.sa.channelMarkers = function(chart, canvas, div, width, height, fs, chs) {
	var x1 = chart.margins.left, x2 = width-chart.margins.right;
	var y1 = canvas.height-chart.margins.bottom, y2 = chart.margins.top+height;
	for (var i = 0; i < fs.length; i++) {
		hm.sa.channelMarker(chart, canvas, div, x1, x2, y1, y2, fs[i], chs[i]);
	}
}

hm.sa.channelMarker = function(chart, canvas, div, x1, x2, y1, y2, f, ch) {
	var margin = 15, edge = 5;
	var top = y2+margin, bottom = y1-y2-2*margin;
	var x = hm.cs.worldToCanvasX(chart, canvas, f);
	var lbl_top = chart.url ? y2-5:y2;
	if (x > x1 && x < x2) {
		var txt = hm.cs.createLabel(lbl_top, x, ch, "channelLbl");
		div.appendChild(txt);
		txt.style.left = (x-Math.round(txt.offsetWidth/2)+1)+"px";
	}
	x = hm.cs.worldToCanvasX(chart, canvas, f-9);
	if (x > x1 && x < x2) {
		hm.sa.dashedMarker(div, x, top, bottom);
		div.appendChild(hm.cs.createDiv(top, x, edge, 1, "#003366"));
		div.appendChild(hm.cs.createDiv(top+bottom, x, edge, 1, "#003366"));
	}
	x = hm.cs.worldToCanvasX(chart, canvas, f+9);
	if (x > x1 && x < x2) {
		hm.sa.dashedMarker(div, x, top, bottom);
		div.appendChild(hm.cs.createDiv(top, x-edge+1, edge, 1, "#003366"));
		div.appendChild(hm.cs.createDiv(top+bottom, x-edge+1, edge, 1, "#003366"));
	}
}

hm.sa.dashedMarker = function(div, x, top, bottom) {
	var dash = 10, gap = 4, lastDash = dash;
	for (var dy = 0; dy < bottom; dy+=dash+gap) {
		if (dy+dash+gap > bottom) { lastDash = bottom-dy; }
	}
	var edash = Math.floor((dash+lastDash)/2)+1;
	div.appendChild(hm.cs.createDiv(top, x, 1, edash, "#003366"));
	for (dy = edash+gap; dy < bottom; dy+=dash+gap) {
		if (dy+dash+gap > bottom) { dash = bottom-dy; }
		div.appendChild(hm.cs.createDiv(top+dy, x, 1, dash, "#003366"));
	}
}

hm.sa.fftHoverXY = function(chart, div, width, x, y) {
	if (width < 400) {
		return null; // Need more space
	}
	var tdiv = hm.cs.createDiv(3, 0, 0, 0, "#f00");
	div.appendChild(tdiv);
	var sx = 125;
	tdiv.appendChild(hm.cs.createLabel(0, sx, "X =", "titleLbl"));
	if (Math.floor(x) != x) { x *= 1000; }
	x = new String(x).replace(/\./, '');
	var txt = hm.cs.createLabel(0, sx+21, x.charAt(0)+'.'+x.substring(1)+" "+chart.axis_x.unit);
	tdiv.appendChild(txt);
	sx += txt.offsetWidth+35;
	tdiv.appendChild(hm.cs.createLabel(0, sx, "Y =", "titleLbl"));
	tdiv.appendChild(hm.cs.createLabel(0, sx+21, y+" "+chart.axis_y.unit));
	return tdiv;
}

hm.sa.changeBand = function(cb, chart, operation) {
	var update = function(data) {
		chart.tbar.band = data.value;
		chart.tbar.channels = data.channels;
		chart.tbar.center = data.center;
		chart.tbar.span = data.span;
		hm.sa.setFFTChannelTicks(chart);
		hm.sa.setFFTAxisXboundaries(chart);
	}
	hm.sa.changeChartParam(chart, update, operation+cb.value);
}

hm.sa.changeChannels = function(cb, chart, operation) {
	var update = function(data) {
		chart.tbar.channels = data.value;
		hm.sa.setFFTChannelTicks(chart);
	}
	hm.sa.changeChartParam(chart, update, operation+cb.value);
}

hm.sa.changeCenter = function(cb, chart, operation) {
	var update = function(data) {
		chart.tbar.center = data.value;
		hm.sa.setFFTAxisXboundaries(chart);
	}
	hm.sa.changeChartParam(chart, update, operation+cb.value);
}

hm.sa.changeSpan = function(cb, chart, operation) {
	var update = function(data) {
		chart.tbar.span = data.value;
		hm.sa.setFFTAxisXboundaries(chart);
	}
	hm.sa.changeChartParam(chart, update, operation+cb.value);
}

hm.sa.changeFFTRLevel = function(cb, chart) {
	var update = function(data) {
		chart.tbar.rlevel = data.value;
		hm.sa.setFFTAxisYticks(chart);
	}
	hm.sa.changeChartParam(chart, update, "updateFFTRefLevel&fftRefLevel="+cb.value);
}

hm.sa.changeFFTVScale = function(cb, chart) {
	var update = function(data) {
		chart.tbar.vscale = data.value;
		hm.sa.setFFTAxisYticks(chart);
	}
	hm.sa.changeChartParam(chart, update, "updateFFTVertScale&fftVertScale="+cb.value);
}

hm.sa.changeFFTSample = function(cb, chart) {
	var update = function(data) {
		chart.tbar.sample = data.value;
	}
	hm.sa.changeChartParam(chart, update, "updateFFTSample&fftSample="+cb.checked);
}

hm.sa.changeDutySample = function(cb, chart) {
	var update = function(data) {
		chart.tbar.sample = data.value;
	}
	hm.sa.changeChartParam(chart, update, "updateDutySample&dutySample="+cb.checked);
}

hm.sa.changeDutyMax = function(cb, chart) {
	var update = function(data) {
		chart.tbar.max = data.value;
		hm.sa.setDutyAxisYticks(chart);
	}
	hm.sa.changeChartParam(chart, update, "updateDutyMax&dutyMax="+cb.value);
}

hm.sa.changeDutyMin = function(cb, chart) {
	var update = function(data) {
		chart.tbar.min = data.value;
		hm.sa.setDutyAxisYticks(chart);
	}
	hm.sa.changeChartParam(chart, update, "updateDutyMin&dutyMin="+cb.value);
}

hm.sa.changeChartParam = function(chart, update, operation) {
	var updateDone = function(o) {
		eval("var data = " + o.responseText);
		if (data) {
			if (data.aid < 0) { // This chart is dead
				return;
			}
			hm.cs.removeDashChart(chart.dash, chart.pos); // Blocks redraws
			update(data);
			hm.cs.redrawDashChart(chart.dash, chart.pos);
		}
	}
	var tx = YAHOO.util.Connect.asyncRequest('GET', settingsUrl(chart)+operation, {success : updateDone});
}

hm.sa.setFFTChannelTicks = function(chart) {
	if (chart.tbar.channels == 1611) {
		chart.axis_x.ticks_start = 2402;
		chart.axis_x.ticks_interval = 5;
		chart.axis_x.ticks = ["", "", "2.412", "", "", "", "", "2.437", "", "", "", "", "2.462", "", "", "", ""];
	} else if (chart.tbar.channels == 1813) {
		chart.axis_x.ticks_start = 2402;
		chart.axis_x.ticks_interval = 5;
		chart.axis_x.ticks = ["", "", "2.412", "", "", "", "", "", "", "2.447", "", "", "", "", "2.472", "", ""];
	} else if (chart.tbar.channels == 15913) {
		chart.axis_x.ticks_start = 2402;
		chart.axis_x.ticks_interval = 5;
		chart.axis_x.ticks = ["", "", "2.412", "", "", "", "2.432", "", "", "", "2.452", "", "", "", "2.472", "", ""];
	} else if (chart.tbar.channels == 3664) {
		chart.axis_x.ticks_start = 5180;
		chart.axis_x.ticks_interval = 20;
		chart.axis_x.ticks = ["5.18", "5.20", "5.22", "5.24", "5.26", "5.28", "5.30", "5.32"]; 
	} else if (chart.tbar.channels == 100140) {
		chart.axis_x.ticks_start = 5500;
		chart.axis_x.ticks_interval = 20;
		chart.axis_x.ticks = ["5.50", "5.52", "5.54", "5.56", "5.58", "5.60", "5.62", "5.64", "5.66", "5.68", "5.70"]; 
	} else if (chart.tbar.channels == 149165) {
		chart.axis_x.ticks_start = 5745;
		chart.axis_x.ticks_interval = 20;
		chart.axis_x.ticks = ["5.745", "5.765", "5.785", "5.805", "5.825"]; 
	}
}

hm.sa.setFFTAxisXboundaries = function(chart) {
	var dx = Math.floor(chart.tbar.span/2);
	var start = chart.tbar.center - dx;
	var end = chart.tbar.center + dx;
	if (chart.tbar.channels == 1611 || chart.tbar.channels == 1813 || chart.tbar.channels == 15913) {
		chart.axis_x.start = start<2402?2402:start;
		chart.axis_x.end = end>2482?2482:end;
	} else if (chart.tbar.channels == 3664) {
		chart.axis_x.start = start<5160?5160:start;
		chart.axis_x.end = end>5340?5340:end;
	} else if (chart.tbar.channels == 100140) {
		chart.axis_x.start = start<5480?5480:start;
		chart.axis_x.end = end>5720?5720:end;
	} else if (chart.tbar.channels == 149165) {
		chart.axis_x.start = start<5725?5725:start;
		chart.axis_x.end = end>5845?5845:end;
	}
}

hm.sa.setFFTAxisYticks = function(chart) {
	chart.axis_y.end = chart.tbar.rlevel;
	chart.axis_y.start = chart.axis_y.end - 10 * chart.tbar.vscale;
	chart.axis_y.ticks_start = chart.axis_y.start;
	chart.axis_y.ticks_interval = 2 * chart.tbar.vscale;
	var ticks = new Array();
	var tyw = chart.axis_y.ticks_start;
	for (var i = 0; i <= 5; i++) {
		tyw = chart.axis_y.ticks_start + chart.axis_y.ticks_interval * i;
		ticks[i] = tyw+'';
	}
	chart.axis_y.ticks = ticks;
}

hm.sa.setDutyAxisYticks = function(chart) {
	chart.axis_y.start = chart.tbar.min;
	chart.axis_y.end = chart.tbar.max;
	if (chart.axis_y.end-chart.axis_y.start<70) {
		chart.axis_y.ticks_interval = 10;
		chart.axis_y.ticks = ["0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"];
	} else {
		chart.axis_y.ticks_interval = 20;
		chart.axis_y.ticks = ["0%", "20%", "40%", "60%", "80%", "100%"];
	}
}
