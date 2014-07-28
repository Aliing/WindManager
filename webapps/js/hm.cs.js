var hm = hm || {};
hm.cs = hm.cs || {};

hm.cs.createDash = function(aid, did, top, left, bottom, toolbar, footer, status) {
	// aid & did should always have correct value
	var udiv = document.getElementById(did);
	var vpw = YAHOO.util.Dom.getViewportWidth();
	var vph = YAHOO.util.Dom.getViewportHeight();
	var dash = {aid: aid, udiv: udiv, top: top, left: left, width: vpw-left, height: vph-top-bottom, bottom: bottom, tbh: 33, tbx: 0, toolbar: toolbar, footer: footer};
	dash.tiles = new Array();
	for (var i = 0; i < 4; i++) { dash.tiles[i] = {chart: null, zcanvas: null, paused: false}; }
	var zoom = -1;
	if (status.length > 0) {
		zoom = hm.cs.applyTilesStatus(dash, status);
		dash.width = vpw-dash.left;
		dash.height = vph-dash.top-dash.bottom;
	}
	hm.cs.createDashCanvas(dash);
	hm.cs.drawDash(dash, status.length==0, zoom<0);
	if (zoom >= 0) {
		hm.cs.zoomInChart(dash, zoom);
	}

	var resizeHandler = function() {
		if (YAHOO.util.Dom.getViewportWidth()-dash.left != dash.width ||
			YAHOO.util.Dom.getViewportHeight()-dash.top-dash.bottom != dash.height) {
			hm.cs.resizeDash(dash);
			hm.cs.adjustCvr(dash);
		}
		dash.resizeTid = window.setTimeout(resizeHandler, 2000);
	}
	if (YAHOO.env.ua.ie == 0 && false) { // Not even if not IE
		window.onresize = function() { hm.cs.resizeDash(dash); };
	} else {
		dash.resizeTid = window.setTimeout(resizeHandler, 3000);
	}
	return dash;
}

/*
 * Page unload
 */
hm.cs.closeDash = function(dash) {
	if (dash.resizeTid) { clearTimeout(dash.resizeTid); }
	if (dash.pollTid) { clearTimeout(dash.pollTid); }
}

/*
 * Plug in one tile.
 */
hm.cs.initDashChart = function(dash, chart, i) {
	var tile = dash.tiles[i];
	if (tile.tid >= 0 && chart != null) {
		var canvas = tile.canvas;
		if (canvas != null) {
			hm.cs.drawChartAxes(tile, chart, canvas);
		}
	}
	tile.chart = chart;
}

hm.cs.startDash = function(dash, dataUrl) {
	dash.dataUrl = dataUrl;
	dash.pollCount = 0;
	hm.cs.fetchDashData(dash);
}

/*
 * Move dash origin and redraw
 */
hm.cs.moveDash = function(dash, top, left, bottom, tbx) {
	dash.top = top; dash.left = left; dash.bottom = bottom; dash.tbx = tbx;
	hm.cs.resizeDash(dash);
}

/*
 * Resize dash and redraw
 */
hm.cs.resizeDash = function(dash) {
	if (dash.top>10) {
		displayTriggerIconDiv();
		resizeLeftMenu();
	} else {
		hideTriggerIconDiv();
	}
	var hidden = 0, ti;
	for (var i = 0; i < 4; i++) {
		var tile = dash.tiles[i];
		var canvas = tile.canvas;
		if (canvas != null) {
			if (canvas.div == null) { hidden++; } else { ti = i; }
			canvas.div = null; // Make invisible, just to disable redraws
		}
	}
	dash.width = YAHOO.util.Dom.getViewportWidth()-dash.left;
	dash.height = YAHOO.util.Dom.getViewportHeight()-dash.top-dash.bottom;
	var pdiv = dash.pdiv; dash.pdiv = null;
	if (pdiv != null) { dash.udiv.removeChild(pdiv); }
	hm.cs.createDashCanvas(dash);
	hm.cs.drawDash(dash, false, hidden == 0);
	if (hidden > 0) {
		hm.cs.zoomInChart(dash, ti);
	}
}

hm.cs.drawDash = function(dash, reset, all) {
	// Never in zoomed in mode
	for (var i = 0; i < 4; i++) {
		var tile = dash.tiles[i];
		if (reset) { tile.tid = i; }
		if (tile.tid >= 0) {
			tile.canvas = hm.cs.getDashTile(dash, tile.tid);
			// 4 only if last one, not if zoomed in.
			var ti = tile.tid==4?-i-1:i;
			hm.cs.drawDashChart(dash, tile, ti, all);
		}
	}
	if (all) { // will be redrawn in hm.cs.zoomInChart
		hm.cs.drawDashBottom(dash);
	}
}

hm.cs.drawDashBottom = function(dash) {	
	if (dash.bottom > 0) {
		var canvas = hm.cs.getDashTile(dash, 9);
		var cdiv = dash.cdiv;
		var bdiv = hm.cs.backDiv(cdiv, canvas.top, canvas.left, canvas.width, canvas.height);
		dash.footer(bdiv, canvas.width, canvas.height);
	}
}

hm.cs.drawDashChart = function(dash, tile, ti, all) {
	var canvas = tile.canvas, cdiv = dash.cdiv;
	if (canvas == null || cdiv == null) {
		return;
	}
	var chart = tile.chart;
	hm.cs.createChartCanvas(dash, tile, ti, canvas, cdiv);
	if (all && chart != null) {
		hm.cs.drawChartAxes(tile, chart, canvas);
		hm.cs.drawChartSeries(tile, chart, canvas);
	}
}

hm.cs.redrawDashChart = function(dash, ti) {
	var tile = dash.tiles[ti];
	hm.cs.drawDashChart(dash, tile, tile.zcanvas?-ti-1:ti, true);
}

hm.cs.drawChartSeries = function(tile, chart, canvas) {
	if (tile.series == undefined) {
		return;
	}
	// Incoming chart & canvas will never be null
	for (var i = 0; i < tile.series.length; i++) {
		if (chart.type == "scatter") {
			hm.cs.drawScatterSeries(tile, chart, canvas, tile.series[i]);
		} else {
			if (tile.series.length>1){
				hm.cs.drawLineSeries(tile, chart, canvas, tile.series[i], chart.series_colors[i]);
            } else {
				hm.cs.drawLineSeries(tile, chart, canvas, tile.series[i], chart.series_colors[1]);
            }
			//if (i == 0) { hm.cs.drawLineSeries(tile, chart, canvas, tile.series[i], chart.series_colors[i]); }
			//hm.cs.drawLineSeries(tile, chart, canvas, tile.series[i], chart.series_colors[i]);
		}
	}
}

hm.cs.fetchDashData = function(dash) {
	var fetchDataDone = function(o) {
		eval("var data = " + o.responseText);
		if (data) { hm.cs.processDashData(dash, data); }
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', dash.dataUrl+"&ignore="+new Date().getTime(), {success : fetchDataDone});
}

hm.cs.processDashData = function(dash, data) {
	if (data.aid < 0) { // Stop polling, this chart is dead.
		return;
	}
	// remain times
	eval("var rtm = data.m");
	resetRemainTimes(rtm);
	for (var i = 0; i < 4; i++) {
		var tile = dash.tiles[i];
		var chart = tile.chart;
		if (!chart) {
			continue;
		}
		var canvas = tile.canvas;
		eval("var series = data.t"+i);
		if (chart.url) {
			if (series) {
				// New samples available
				if (hm.cs.tileActive(dash, canvas) && !tile.paused) {
					var img = canvas.idiv.firstChild;
					img.src = chart.url+"&width="+img.width+"&height="+img.height+"&ignore="+new Date().getTime();
				}
			}
		} else if (series && series.length > 0) {
			tile.series = series;
			if (!hm.cs.tileActive(dash, canvas)) {
				continue;
			}
			if (!tile.paused && chart != null) {
				hm.cs.clearDashChart(dash, canvas);
				hm.cs.drawChartSeries(tile, chart, canvas);
			}
		}
	}
	var pollHandler = function() {
		var maxPoll=60;
		//var maxPoll = YAHOO.env.ua.ie == 0 ? 200 : 100;
		//if (YAHOO.env.ua.webkit) { maxPoll = 60; }
		if (++dash.pollCount == maxPoll) {
			var href = window.location.href;
			var dsi = href.indexOf("tilesStatus");
			if (dsi > 0) {
				href = href.replace(/tilesStatus=......./, "tilesStatus="+hm.cs.getTilesStatus(dash));
			} else {
				href+="&tilesStatus="+hm.cs.getTilesStatus(dash);
			}
			top.autoRefresh = true; // To avoid user prompt.
			window.location.replace(href);
			return;
		}
		hm.cs.fetchDashData(dash);
	}
	dash.pollTid = window.setTimeout(pollHandler, 1000);
}

hm.cs.tileActive = function(dash, canvas) {
	if (dash.cdiv == null || dash.pdiv == null) {
		// cdiv has charts, pdiv has cdiv and dash toolbar
		return false;
	}
	if (canvas == null || canvas.div == null) {
		// closed or not visible
		return false;
	}
	return true;
}

hm.cs.getTilesStatus = function(dash) {
	var s = 10-dash.top>0?3:10-dash.left>0?2:1;
	var open, hidden = 0;
	for (var i = 0; i < 4; i++) {
		var tile = dash.tiles[i];
		var canvas = tile.canvas;
		if (canvas == null) {
			s+='C';
		} else {
			s+=''+tile.tid;
			if (canvas.div == null) { hidden++; } else { open = i; }
		}
	}
	s+=hidden>0?'Z'+open:'NZ';
	return s;
}

hm.cs.applyTilesStatus = function(dash, status) {
	if (status.charAt(0) == 3) { // Maximize
		hm.cs.createCvr(dash);
		dash.top = 2; dash.left = 2; dash.bottom = 0;
	} else if (status.charAt(0) == 2) { // Left slider
		triggerSlider();
		top.autoRefresh = false; // No false onBeforeUnloadEvent trigger 
		dash.left = 2; dash.tbx = 13;
	}
	for (var i = 0; i < 4; i++) {
		dash.tiles[i].tid = status.charAt(i+1)=='C'?-1:Math.round(status.charAt(i+1));
	}
	return status.charAt(5)=='N'?-1:Math.round(status.charAt(6));
}

hm.cs.clearDashChart = function(dash, canvas) {
	var div = canvas.div; canvas.div = null;
	if (div != null) { dash.cdiv.removeChild(div); } // only data div
	var sdiv = canvas.sdiv; canvas.sdiv = null;
	if (sdiv != null) { dash.cdiv.removeChild(sdiv); } // only data div
	sdiv = hm.cs.createDiv(canvas.top, canvas.left, canvas.width, canvas.height, false);
	dash.cdiv.appendChild(sdiv);
	canvas.sdiv = sdiv;
	div = hm.cs.createDiv(canvas.top, canvas.left, canvas.width, canvas.height, false);
	div.style.zIndex = 1; // below tbar
	dash.cdiv.appendChild(div);
	canvas.r = Raphael(div, canvas.width, canvas.height);
	canvas.div = div;
}

hm.cs.removeDash = function(dash) {
	for (var i = 0; i < dash.tiles.length; i++) {
		var tile = dash.tiles[i];
		var canvas = tile.canvas;
		if (canvas) { canvas.div = null; }  // Make invisible, just to disable redraws
	}
	var pdiv = dash.pdiv;
	var cdiv = dash.cdiv; dash.cdiv = null;
	if (pdiv != null && cdiv != null) { pdiv.removeChild(cdiv); }
	cdiv = hm.cs.createDiv(0, 0, 0, 0, false);
	pdiv.appendChild(cdiv);
	dash.cdiv = cdiv;
}

hm.cs.removeDashChart = function(dash, i) {
	var tile = dash.tiles[i];
	var canvas = tile.canvas;
	var div = canvas.div; canvas.div = null; canvas.r = null;
	if (div != null) { dash.cdiv.removeChild(div); }
	div = canvas.sdiv; canvas.sdiv = null;
	if (div != null) { dash.cdiv.removeChild(div); }
	div = canvas.tdiv; canvas.tdiv = null;
	if (div != null) { dash.cdiv.removeChild(div); }
	div = canvas.axesDiv; canvas.axesDiv = null;
	if (div != null) { dash.cdiv.removeChild(div); }
	div = canvas.backDiv; canvas.backDiv = null;
	if (div != null) { dash.cdiv.removeChild(div); }
}

hm.cs.helpChart = function(dash, ti) {
}

hm.cs.pauseChart = function(dash, ti, img) {
	var pi = ti<0?-ti-1:ti;
	var tile = dash.tiles[pi];
	tile.paused = !tile.paused;
	img.src = tile.paused?hm.cs.resumeHvr.src:hm.cs.pauseHvr.src;
	img.title = tile.paused?"Resume":"Pause";
}

hm.cs.zoomOutChart = function(dash, ti) {
	hm.cs.removeDash(dash);
	for (var i = 0; i < dash.tiles.length; i++) {
		if (i != -ti-1 && dash.tiles[i].canvas != null) {
			hm.cs.drawDashBottom(dash);
			var tile = dash.tiles[-ti-1];
			tile.canvas = tile.zcanvas;
			tile.zcanvas = null;
			hm.cs.drawTiles(dash);
			return;
		}
	}
	// Last one
	hm.cs.drawDash(dash, true, true);
}

hm.cs.zoomInChart = function(dash, ti) {
	hm.cs.removeDash(dash);
	var tile = dash.tiles[ti];
	tile.zcanvas = tile.canvas;
	tile.canvas = hm.cs.getDashTile(dash, 4);
	hm.cs.drawTile(dash, -ti-1);
	hm.cs.drawDashBottom(dash);
	return tile;
}

hm.cs.closeChart = function(dash, ti) {
	var ci = ti<0?-ti-1:ti;
	var sx = ci<2?2:0;
	var tbl = dash.tiles[sx], tbr = dash.tiles[sx+1];
	var nti = 5-sx*2-ci;
	var nbr = dash.tiles[nti];
	if (nbr.canvas) {
		if (tbl.canvas || tbr.canvas) { // TT or BB
			if (ti < 0) { // Close in zoomed in mode
				hm.cs.removeDash(dash);
				hm.cs.drawDashBottom(dash);
				// Redraw bottom half
				if (tbl.canvas) { hm.cs.drawTile(dash, sx); }
				if (tbr.canvas) { hm.cs.drawTile(dash, sx+1); }
			} else { // close ci and nbr charts
				hm.cs.removeDashChart(dash, 2-sx);
				hm.cs.removeDashChart(dash, 3-sx);
			}
			nbr.canvas = hm.cs.getDashTile(dash, 7-sx); nbr.tid = 7-sx;
		} else { // FS
			nbr.tid = 4; nbr = null;
		}
	} else if (tbl.canvas) {
		if (tbr.canvas) { // LL & RR
			hm.cs.removeDash(dash);
			hm.cs.drawDashBottom(dash);
			tbl.canvas = hm.cs.getDashTile(dash, 6); tbl.tid = 6;
			tbr.canvas = hm.cs.getDashTile(dash, 8); tbr.tid = 8;
			nti = sx;
			hm.cs.drawTile(dash, sx+1);
		} else { // FS
			nti = sx; tbl.tid = 4; nbr = null;
		}
	} else if (tbr.canvas) { // FS
		nti = sx+1; tbr.tid = 4; nbr = null;
	} else { // Last one
		hm.cs.removeDash(dash);
		hm.cs.drawDash(dash, true, true);
		return;
	}
	dash.tiles[ci].zcanvas = null;
	dash.tiles[ci].canvas = null;
	dash.tiles[ci].tid = -1;
	if (nbr) {
		hm.cs.drawTile(dash, nti);  // nti never < 0
	} else {
		hm.cs.zoomInChart(dash, nti).zcanvas = null; // Don't save it
	}
}

hm.cs.drawTiles = function(dash) {
	for (var i = 0; i < dash.tiles.length; i++) {
		if (dash.tiles[i].canvas != null) {
			hm.cs.drawTile(dash, i);
		}
	}
}

hm.cs.drawTile = function(dash, ti) {
	var tile = dash.tiles[ti<0?-ti-1:ti];
	hm.cs.drawDashChart(dash, tile, ti, true);
}

hm.cs.getDashTile = function(dash, ti) {
	var top = dash.tbh, left = 0, width = dash.width, height = dash.height;
	var margin = 4;
	width = Math.floor((width-left-margin-2)/2);
	height = Math.floor((height-top-margin-1)/2);
	switch (ti) {
	case 0: return {top: top, left: left, width: width, height: height}; // TL
	case 1: return {top: top, left: left+width+margin, width: width, height: height}; // TR
	case 2: return {top: top+height+margin, left: left, width: width, height: height}; // BL
	case 3: return {top: top+height+margin, left: left+width+margin, width: width, height: height}; // BR
	case 4: return {top: top, left: left, width: width*2+margin, height: height*2+margin}; // FS
	case 5: return {top: top, left: left, width: width*2+margin, height: height}; // TT
	case 6: return {top: top, left: left, width: width, height: height*2+margin}; // LL
	case 7: return {top: top+height+margin, left: left, width: width*2+margin, height: height}; // BB
	case 8: return {top: top, left: left+width+margin, width: width, height: height*2+margin}; // RR
	case 9: return {top: top+height*2+margin*2, left: left, width: width*2+margin, height: dash.bottom-margin}; // Bottom Tile
	}
}

hm.cs.createToolbar = function(dash, pdiv) {
	var tbm = 4;
	var width = hm.cs.getDashTile(dash, 4).width - dash.tbx;
	var tdiv = hm.cs.backDiv(pdiv, 0, dash.tbx, width, dash.tbh-tbm);
	var ti = 10-dash.top;
	var zoom = ti < 0 ? hm.cs.maximize : hm.cs.reduce;
	var zoomHvr = ti < 0 ? hm.cs.maximizeHvr : hm.cs.reduceHvr;
	var title = ti < 0 ? "Maximize" : "Restore Down";
	var x = width-23, y = Math.floor((dash.tbh-tbm-15)/2);
	tdiv.appendChild(hm.cs.createOpIcon(dash, zoom, zoomHvr, y, x, hm.cs.zoomDash, ti, title));
	dash.toolbar(tdiv, width, dash.tbh-tbm);
	dash.tdiv = tdiv;
}

hm.cs.zoomDash = function(dash, ti) {
	if (ti < 0) {
		hm.cs.createCvr(dash);
		hm.cs.moveDash(dash, 2, 2, 0, 0);
	} else {
		dash.udiv.removeChild(dash.tcvr); dash.tcvr = null;
		dash.udiv.removeChild(dash.lcvr); dash.lcvr = null;
		hm.cs.moveDash(dash, dash.otop, dash.oleft, dash.obottom, dash.otbx);
	}
}

hm.cs.createCvr = function(dash) {
	dash.otop = dash.top; dash.oleft = dash.left; dash.obottom = dash.bottom, dash.otbx = dash.tbx;
	dash.tcvr = hm.cs.createDiv(0, 0, dash.left+dash.width, dash.top, "#e1e1e1");
	dash.udiv.appendChild(dash.tcvr);
	dash.lcvr = hm.cs.createDiv(0, 0, dash.left, dash.top+dash.height+dash.bottom, "#e1e1e1");
	dash.udiv.appendChild(dash.lcvr);
}

hm.cs.adjustCvr = function(dash) {
	if (10-dash.top > 0 && dash.tcvr && dash.lcvr) {
		dash.tcvr.style.width = (dash.left+dash.width) + "px";
		dash.lcvr.style.height = (dash.top+dash.height) + "px";
	}
}

hm.cs.createDashCanvas = function(dash) {
	var pdiv = hm.cs.createDiv(dash.top, dash.left, 0, 0, false);
	dash.udiv.appendChild(pdiv);
	var cdiv = hm.cs.createDiv(0, 0, 0, 0, false);
	pdiv.appendChild(cdiv);
	hm.cs.createToolbar(dash, pdiv);
	dash.pdiv = pdiv;
	dash.cdiv = cdiv;
}

hm.cs.createChartCanvas = function(dash, tile, ti, canvas, cdiv) {
	// Incoming canvas will never be null
	var top = canvas.top, left = canvas.left, width = canvas.width, height = canvas.height;
	canvas.backDiv = hm.cs.backDiv(cdiv, top, left, width, height);
	canvas.axesDiv = hm.cs.createDiv(top, left, width, height, false);
	cdiv.appendChild(canvas.axesDiv);
	canvas.ar = Raphael(canvas.axesDiv, width, height);
	var sdiv = hm.cs.createDiv(top, left, width, height, false);
	cdiv.appendChild(sdiv);
	var div = hm.cs.createDiv(top, left, width, height, false);
	cdiv.appendChild(div);
	canvas.r = Raphael(div, width, height);
	var zoom = ti < 0 ? hm.cs.zoomOut : hm.cs.zoomIn;
	var zoomHvr = ti < 0 ? hm.cs.zoomOutHvr : hm.cs.zoomInHvr;
	var callback = ti < 0 ? hm.cs.zoomOutChart : hm.cs.zoomInChart;
	var title = ti < 0 ? "Zoom Out" : " Zoom In";
	var x = width - 21, dx = 18;
	var tdiv = hm.cs.createDiv(top, left, width, 0, false);
	tdiv.style.zIndex = 2; // above canvas.div
	cdiv.appendChild(tdiv);
	tdiv.appendChild(hm.cs.createOpIcon(dash, hm.cs.close, hm.cs.closeHvr, 3, x, hm.cs.closeChart, ti, "Close"));
	tdiv.appendChild(hm.cs.createOpIcon(dash, zoom, zoomHvr, 3, x-=dx, callback, ti, title));
	//tdiv.appendChild(hm.cs.createOpIcon(dash, hm.cs.help, hm.cs.helpHvr, 3, x-=dx, hm.cs.helpChart, ti, "Help"));
	var pause = tile.paused?hm.cs.resume:hm.cs.pause;
	title = tile.paused?"Resume":"Pause";
	var icon = hm.cs.createOpIcon(dash, pause, null, 3, x-=dx, hm.cs.pauseChart, ti, title);
	tdiv.appendChild(icon);
	var img = icon.firstChild;
	var over = function(e) { img.src = tile.paused?hm.cs.resumeHvr.src:hm.cs.pauseHvr.src; };
	var out = function(e) { img.src = tile.paused?hm.cs.resume.src:hm.cs.pause.src; };
	YAHOO.util.Event.addListener(img, "mouseover", over);
	YAHOO.util.Event.addListener(img, "mouseout", out);
	canvas.tdiv = tdiv;
	canvas.sdiv = sdiv;
	canvas.div = div;
}

hm.cs.drawChartAxes = function(tile, chart, canvas) {
	// Incoming chart & canvas will never be null
	var div = canvas.axesDiv;
	if (div == null) {
		return;
	}
	
	var legend = tile.zcanvas?chart.margins.legend_large:chart.margins.legend_small;
	var lbl = hm.cs.createLabel(legend>0?3:2, 8, chart.name, "titleLbl");
	div.appendChild(lbl);
	var gridColor = '#ddd';
	if (legend > 0) { div.appendChild(hm.cs.createDiv(chart.margins.top-2, 7, canvas.width-14, 1, gridColor)); }
	var x1 = chart.margins.left, x2 = canvas.width-chart.margins.right;
	var y1 = canvas.height-chart.margins.bottom, y2 = chart.margins.top+legend;
	if (chart.url) { y2+=10; }
	canvas.scale_x = (x2-x1) / (chart.axis_x.end-chart.axis_x.start);
	canvas.scale_y = (y1-y2) / (chart.axis_y.end-chart.axis_y.start);
	if (chart.tbar && chart.tbar.create) { chart.tbar.create(chart, canvas, canvas.tdiv, canvas.width, legend); }
	div.appendChild(hm.cs.createDiv(y1, x1, x2-x1, 1, "#000")); // X axis
	div.appendChild(hm.cs.createDiv(y2, x1, 1, y1-y2, "#000")); // Y axis
	div.appendChild(hm.cs.createDiv(y1, x1, 1, 4, "#000"));     // axis tick
	div.appendChild(hm.cs.createDiv(y1, x1 - 4, 4, 1, "#000")); // axis tick
	for (var i = 0; i < chart.axis_x.ticks.length; i++) {
		var txw = chart.axis_x.ticks_start + chart.axis_x.ticks_interval * i;
		if (txw < chart.axis_x.start || txw > chart.axis_x.end) {
			// beyond boundaries
			continue;
		}
		var tx = hm.cs.worldToCanvasX(chart, canvas, txw);
		if (tx != x1) {
			div.appendChild(hm.cs.createDiv(y2, tx, 1, y1 - y2, gridColor));
		}
		div.appendChild(hm.cs.createDiv(y1, tx, 1, 4, "#000"));
		if (chart.axis_x.ticks[i].length > 0 && txw != chart.axis_x.end) {
			var txt = hm.cs.createLabel(y1 + 4, tx, chart.axis_x.ticks[i]);
			div.appendChild(txt);
			txt.style.left = (tx-Math.round(txt.offsetWidth/2)+1)+"px";
		}
	}
	for (var i = 0; i < chart.axis_y.ticks.length; i++) {
		var tyw = chart.axis_y.ticks_start + chart.axis_y.ticks_interval * i;
		if (tyw < chart.axis_y.start || tyw > chart.axis_y.end) {
			// beyond boundaries
			continue;
		}
		var ty = hm.cs.worldToCanvasY(chart, canvas, tyw);
		if (ty != y1) {
			div.appendChild(hm.cs.createDiv(ty, x1 + 1, x2 - x1, 1, gridColor));
		}
		div.appendChild(hm.cs.createDiv(ty, x1 - 4, 4, 1, "#000"));
		if (chart.axis_y.ticks[i].length > 0) {
			if (tyw == chart.axis_y.end) {
				ty += 1; // 1 extra pixel, just below chart name
			}
			var lbl = hm.cs.createLabel(ty - 7, x1, chart.axis_y.ticks[i]);
			div.appendChild(lbl);
			lbl.style.left = (x1 - lbl.offsetWidth - 5) + "px";
		}
	}
	if (chart.axis_x.lbl) {
		var lbl = hm.cs.createLabel(y1+4, canvas.width, chart.axis_x.lbl);
		div.appendChild(lbl);
		lbl.style.left = (canvas.width-lbl.offsetWidth-6) + "px";
	}
	var labelAdjX = YAHOO.env.ua.ie == 0 ? 0 : -2;
	if (chart.axis_y.lbl) {
		var txt = canvas.ar.text(8+labelAdjX*2,(y1-y2)*.5+y2, chart.axis_y.lbl);
		txt.attr( {font : "12px 'Arial'"}).rotate(90);
	}
	div = canvas.div;
	if (chart.url && div != null) {
		var idiv = hm.cs.createDiv(y2, x1+1, 0, 0, false);
		var img = document.createElement("img");
		img.src = hm.cs.spacer.src;
		img.width = x2-x1-1;
		img.height = y1-y2;
		idiv.appendChild(img);
		div.appendChild(idiv);
		canvas.idiv = idiv;
	}
}

hm.cs.drawLineSeries = function(tile, chart, canvas, data, color) {
	// Incoming chart & canvas will never be null
	var r = canvas.r;
	// var color = Raphael.hsb2rgb(Math.random(), 0.85, 0.75).hex;
	// color = "#b00e22"; color = "#0e35b0"; color = "#15b00e"; color =
	// "#4b80b6"; color = "#f60"; color="#5f3"
	var legend = tile.zcanvas?chart.margins.legend_large:chart.margins.legend_small;
	var ystart = canvas.height-chart.margins.bottom, yend = chart.margins.top+legend;
	if (data.x.length == 0) { return; }
	for (var i = 0; data.x[i] < chart.axis_x.start; i++) {
		if (i+1 == data.x.length) { return; }
	}
	while (hm.cs.outsideY(chart, data.y[i])) {
		if (++i == data.x.length) { return; }
	}
	var x1 = hm.cs.worldToCanvasX(chart, canvas, data.x[i]);
	var y1 = hm.cs.worldToCanvasY(chart, canvas, data.y[i]);
//	if (data.y[i] < chart.axis_y.start) { y1 = ystart; } else
//	if (data.y[i] > chart.axis_y.end) { y1 = yend; }
	var dy = 50;
	hm.cs.createMarker(tile, chart, canvas, r, x1, y1, color, legend, data.x[i], data.y[i], dy);
	var path = "M " + x1 + " " + y1;
	var count = 1;
	var maxDispalyGap=0;
	for (;i < data.x.length; i++) {
		if (data.x[i] > chart.axis_x.end) {
			break;
		}
		if (hm.cs.outsideY(chart, data.y[i])) {
			if (++maxDispalyGap<=2) {
				continue;
			}
			hm.cs.drawCurve(r, path, color, count);
			while (hm.cs.outsideY(chart, data.y[i])) {
				if (++i == data.x.length) { break; }
			}
			if (i < data.x.length) {
				count = 1;
				x1 = hm.cs.worldToCanvasX(chart, canvas, data.x[i]);
				y1 = hm.cs.worldToCanvasY(chart, canvas, data.y[i]);
				hm.cs.createMarker(tile, chart, canvas, r, x1, y1, color, legend, data.x[i], data.y[i], dy);
				var path = "M " + x1 + " " + y1;
			}				
		} else {
			maxDispalyGap=0;
			count++;
			var x2 = hm.cs.worldToCanvasX(chart, canvas, data.x[i]);
			var y2 = hm.cs.worldToCanvasY(chart, canvas, data.y[i]);
//			if (data.y[i] < chart.axis_y.start) { y2 = ystart; } else
//			if (data.y[i] > chart.axis_y.end) { y2 = yend; }
			hm.cs.createMarker(tile, chart, canvas, r, x2, y2, color, legend, data.x[i], data.y[i], dy);
			if (chart.smooth) {
				var w = 2;
				path += [ "C ", x1 + w, y1, x2 - w, y2, x2, y2 ];
			} else {
				path += "L " + x2 + " " + y2;
			}
			x1 = x2;
			y1 = y2;
		}
	}
	hm.cs.drawCurve(r, path, color, count);
}

hm.cs.drawCurve = function(r, path, color, count) {
	if (count > 1) {
		var curve = r.path(path);
		curve.attr( {stroke: color, 'stroke-width': 2, 'stroke-linecap': 'round'});
	}
}

hm.cs.drawScatterSeries = function(tile, chart, canvas, data) {
	// Incoming chart & canvas will never be null
	var rssiColors = ['#800000', '#951500', '#aa2b00', '#bf4000', '#d45500', '#ea6b00', '#ff8000', '#ff8c00',
					  '#ff9700', '#ffa300', '#ffae00', '#ffba00', '#ffc500', '#ffd100', '#ffdc00', '#ffe800',
					  '#fff300', '#ffff00', '#d4ff0b', '#aaff15', '#80ff20', '#55ff2b', '#2bff35', '#00ff40',
					  '#00ff58', '#00ff70', '#00ff88', '#00ff9f', '#00ffb7', '#00ffcf', '#00ffe7', '#00ffff',
					  '#00f1fe', '#01e4fc', '#01d6fb', '#01c8f9', '#02bbf8', '#02adf6', '#029ff5', '#0292f4',
					  '#0384f2', '#0376f1', '#066de3', '#0963d4', '#0c5ac6', '#0f50b8', '#1246aa', '#153d9c',
					  '#18338d', '#1b2a7f', '#1e2071', '#211662', '#240c54', '#270346', '#2a0038', '#2d002a'];
	var r = canvas.r;
	var legend = tile.zcanvas?chart.margins.legend_large:chart.margins.legend_small;
	var ystart = canvas.height-chart.margins.bottom, yend = chart.margins.top+legend;
	if (data.x.length == 0) { return; }
	for (var i = 0; data.x[i] < chart.axis_x.start; i++) {
		if (i+1 == data.x.length) { return; }
	}
	for (;i < data.x.length; i++) {
		if (data.x[i] > chart.axis_x.end) {
			break;
		}
		if (hm.cs.outsideY(chart, data.y[i])) {
		} else {
			var x1 = hm.cs.worldToCanvasX(chart, canvas, data.x[i]);
			var y1 = hm.cs.worldToCanvasY(chart, canvas, data.y[i]);
			var rc = - data.y[i] - 35;
			if (rc < 3) { rc = 3; } else if (rc > 50) { rc = 50; }
			rc = rssiColors[rc];
			var div = hm.cs.createDiv(y1-1, x1-1, 3, 3, rc);
			canvas.sdiv.appendChild(div);
			hm.cs.createMarker(tile, chart, canvas, r, x1, y1, rc, legend, data.x[i], data.y[i], 3);
		}
	}
}

hm.cs.outsideY = function(chart, y) {
	return y < chart.axis_y.start || y > chart.axis_y.end;
}

hm.cs.worldToCanvasX = function(chart, canvas, x) {
	return chart.margins.left
			+ Math.round((x - chart.axis_x.start) * canvas.scale_x);
}

hm.cs.worldToCanvasY = function(chart, canvas, y) {
	return canvas.height - chart.margins.bottom
			- Math.round((y - chart.axis_y.start) * canvas.scale_y);
}

hm.cs.createMarker = function(tile, chart, canvas, r, x, y, color, legend, xw, yw, dy) {
	var bdiv = null, bubble = r.set(), xydiv = null;
	var cdiv = canvas.div;
	if (cdiv == null) { return; }
	var over = function(e) {
		cdiv.style.zIndex = 3; // Above toolbar
		var ry_min = 0;
		if (chart.hover_xy) {
			xydiv = chart.hover_xy(chart, cdiv, canvas.width, xw, yw);
			ry_min = chart.margins.top;
		}
		bdiv = hm.cs.hoverBubble(chart, canvas, r, x, y, color, xw, yw, bubble, ry_min);
	};
	var out = function(e) {
		if (bdiv) { cdiv.removeChild(bdiv); bdiv = null; }
		if (xydiv) { cdiv.removeChild(xydiv); xydiv = null; }
		bubble.remove();
		bubble = r.set();
		cdiv.style.zIndex = 1;
	};
	var top = y - dy;
	var height = dy * 2;
	var margin = chart.margins.top + legend;
	if (top < margin) {
		height -= margin - top;
		top = margin;
	} else if (top+height > canvas.height) {
		height = canvas.height-top;
	}
	var left = Math.round(x-canvas.scale_x/2);
	var width = Math.round(canvas.scale_x);
	var div = hm.cs.createDiv(top, left, width, height, false);
	div.style.cursor = "default";
	cdiv.appendChild(div);
	YAHOO.util.Event.addListener(div, "mouseover", over);
	YAHOO.util.Event.addListener(div, "mouseout", out);
}

hm.cs.hoverBubble = function(chart, canvas, r, x, y, color, xw, yw, bubble, ry_min) {
	var circle = r.circle(x, y, 5);
	circle.attr({stroke : '#fff', 'stroke-width' : 1, fill : color});
	var padding = 4;
	var ry = y-30-padding;
	var ny = y-12, ndy = padding+1;
	if (ry < ry_min) {
		ry += 45;
		ny = y+12;
		ndy = -ndy;
	}
	var label = yw + " " + chart.axis_y.unit;
	var txt = hm.cs.createLabel(ry+padding, x, label, "markerLbl");
	canvas.div.appendChild(txt);
	var dx = Math.round(txt.offsetWidth / 2);
	var tx = x-dx;
	if (tx < 0) {
		tx = padding;
	} else if (x+dx > canvas.width) {
		tx = canvas.width-dx*2-padding;
	}
	txt.style.left = tx + "px";
	var rounded = r.rect(tx-padding, ry, txt.offsetWidth+padding*2+1, 15+padding*2, padding*1.5);
	rounded.attr({'stroke-width' : 0, fill : '#444', stroke : '#444'});
	var nib = r.path("M " + [ x - padding, ny ] + "L " + [ x, ny + ndy ]
			+ "L " + [ x + padding, ny ] + "Z");
	nib.attr({'stroke-width' : 0, fill : '#444', stroke : '#444'});
	bubble.push(circle, rounded, nib).attr({opacity : 0});
	bubble.attr({opacity : 1});
	// bubble.animate({opacity:1},100);
	return txt;
}

hm.cs.backDiv = function(pdiv, top, left, width, height) {
	var div = hm.cs.createDiv(top, left, 0, 0, false);
	pdiv.appendChild(div);
	top = 0;
	div.appendChild(hm.cs.cornerDiv(hm.cs.tlc, 0, 0));
	div.appendChild(hm.cs.createDiv(0, 9, width-18, 9, "#fff"));
	div.appendChild(hm.cs.cornerDiv(hm.cs.trc, 0, width-9));

	div.appendChild(hm.cs.createDiv(9, 0, width, height-18, "#fff"));

	div.appendChild(hm.cs.cornerDiv(hm.cs.blc, height-9, 0));
	div.appendChild(hm.cs.createDiv(height-9, 9, width-18, 9, "#fff"));
	div.appendChild(hm.cs.cornerDiv(hm.cs.brc, height-9, width-9));
	return div;
}

hm.cs.createSelect = function(top, left, onchange, context, items, values, selected) {
	var select = document.createElement("select");
	select.style.position = "absolute";
	if (YAHOO.env.ua.ie > 0) {top--;} else
	if (YAHOO.env.ua.webkit > 0) {top -= 2;}
	select.style.top = top + "px";
	select.style.left = left + "px";
	select.onchange = function() { onchange(select, context); };
	for (var i = 0; i < items.length; i++) {
		var value = values == null ? items[i] : values[i];
		var option = new Option(items[i], value);
		if (selected != undefined && selected == value) {
			option.selected = true;
		}
		if (YAHOO.env.ua.ie == 0) {
			select.add(option, null);
		} else {
			select.add(option);
		}
	}
	return select;
}

hm.cs.createCheckBox = function(top, left, onclick, context, selected) {
	var echkbox=document.createElement("input");
	echkbox.setAttribute("type","checkbox");
	echkbox.style.position = "absolute";
	if (YAHOO.env.ua.ie > 0) {top--;} else
	if (YAHOO.env.ua.webkit > 0) {top -= 2;}
	echkbox.style.top = top + "px";
	echkbox.style.left = left + "px";
	echkbox.onclick = function() { onclick(echkbox, context); };
	return echkbox;
}

hm.cs.createButton = function(top, left, txt, btid, className,onclick) {
	var button=document.createElement("input");
	button.setAttribute("type","button");
	button.setAttribute("value",txt);
	button.setAttribute("id",btid);
	if (className) {
		button.setAttribute("class",className);
        button.setAttribute("className",className);
	}
	button.style.position = "absolute";
	if (YAHOO.env.ua.ie > 0) {top--;} else
	if (YAHOO.env.ua.webkit > 0) {top -= 2;}
	button.style.top = top + "px";
	button.style.left = left + "px";
	button.onclick = function() { onclick(); };
	return button;
}

hm.cs.alignRight = function(div1, div2) {
	var w1 = div1.offsetWidth, w2 = div2.offsetWidth, left = div1.style.left;
	var x = Math.floor(left.substring(0, left.length-2));
	if (w1 > w2) {
		div2.style.left = x+w1-w2+"px";
	} else {
		div1.style.left = x+w2-w1+"px";
	}
}

hm.cs.createLabel = function(top, left, txt, className, ids) {
	var div = document.createElement("div");
	div.className = className ? className : "defaultLbl";
	div.style.position = "absolute";
	div.style.top = top + "px";
	div.style.left = left + "px";
	if (ids) {
		div.id=ids;
	}
	div.appendChild(document.createTextNode(txt));
	return div;
}

hm.cs.createDiv = function(top, left, width, height, bg) {
	var div = document.createElement("div");
	div.style.position = "absolute";
	if (bg) { div.style.backgroundColor = bg; }
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = width + "px";
	div.style.height = height + "px";
	return div;
}

hm.cs.createOpIcon = function(dash, icon, hvr, top, left, callback, ti, title) {
	var img = document.createElement("img");
	img.src = icon.src;
	// img.style.border = 0;
	img.style.cursor = "pointer";
	img.width = 16;
	img.height = 16;
	img.title = title;
	if (hvr) {
		var over = function(e) {
			img.src = hvr.src;
		};
		var out = function(e) {
			img.src = icon.src;
		};
		YAHOO.util.Event.addListener(img, "mouseover", over);
		YAHOO.util.Event.addListener(img, "mouseout", out);
	}
	var zoom = function(e) {
		callback(dash, ti, img);
	};
	img.onclick = zoom;
	var div = document.createElement("div");
	div.style.position = "absolute";
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = "16px";
	div.style.height = "16px";
	div.appendChild(img);
	return div;
}

hm.cs.cornerDiv = function(corner, top, left) {
	var img = document.createElement("img");
	img.src = corner.src;
	img.style.display = "block";
	img.width = 9;
	img.height = 9;

	var div = document.createElement("div");
	div.style.position = "absolute";
	div.style.top = top + "px";
	div.style.left = left + "px";
	div.style.width = "9px";
	div.style.height = "9px";
	div.appendChild(img);
	return div;
}

hm.cs.loadIcons = function(baseUrl) {
	hm.cs.spacer = hm.util.loadImage(baseUrl + "/spacer.gif");
	hm.cs.close = hm.util.loadImage(baseUrl + "/cancel.png");
	hm.cs.closeHvr = hm.util.loadImage(baseUrl + "/cancel_disable.png");
	// hm.cs.close = hm.util.loadImage(baseUrl + "/sa/cancel-off.png");
	// hm.cs.closeHvr = hm.util.loadImage(baseUrl + "/sa/cancel-on.png");
	hm.cs.maximize = hm.util.loadImage(baseUrl + "/sa/maximize-off.png");
	hm.cs.maximizeHvr = hm.util.loadImage(baseUrl + "/sa/maximize-on.png");
	hm.cs.reduce = hm.util.loadImage(baseUrl + "/sa/reduce-off.png");
	hm.cs.reduceHvr = hm.util.loadImage(baseUrl + "/sa/reduce-on.png");
	hm.cs.zoomIn = hm.util.loadImage(baseUrl + "/sa/zoom-out-off.png");
	hm.cs.zoomInHvr = hm.util.loadImage(baseUrl + "/sa/zoom-out-on.png");
	hm.cs.zoomOut = hm.util.loadImage(baseUrl + "/sa/zoom-in-off.png");
	hm.cs.zoomOutHvr = hm.util.loadImage(baseUrl + "/sa/zoom-in-on.png");
	hm.cs.help = hm.util.loadImage(baseUrl + "/sa/help-off.png");
	hm.cs.helpHvr = hm.util.loadImage(baseUrl + "/sa/help-on.png");
	hm.cs.pause = hm.util.loadImage(baseUrl + "/sa/pause-off.png");
	hm.cs.pauseHvr = hm.util.loadImage(baseUrl + "/sa/pause-on.png");
	hm.cs.resume = hm.util.loadImage(baseUrl + "/sa/resume-off.png");
	hm.cs.resumeHvr = hm.util.loadImage(baseUrl + "/sa/resume-on.png");
	hm.cs.tlc = hm.util.loadImage(baseUrl + "/rounded/top_left_white.gif");
	hm.cs.trc = hm.util.loadImage(baseUrl + "/rounded/top_right_white.gif");
	hm.cs.blc = hm.util.loadImage(baseUrl + "/rounded/bottom_left_white.gif");
	hm.cs.brc = hm.util.loadImage(baseUrl + "/rounded/bottom_right_white.gif");
	hm.cs.returnOff = hm.util.loadImage(baseUrl + "/sa/return.png");
	hm.cs.returnOn = hm.util.loadImage(baseUrl + "/sa/return-on.png");
	hm.cs.stopOff = hm.util.loadImage(baseUrl + "/sa/stop.png");
	hm.cs.stopOn = hm.util.loadImage(baseUrl + "/sa/stop-on.png");
	hm.cs.settingsOff = hm.util.loadImage(baseUrl + "/search/Settings_off.png");
	hm.cs.settingsOn = hm.util.loadImage(baseUrl + "/search/Settings_on.png");
}
