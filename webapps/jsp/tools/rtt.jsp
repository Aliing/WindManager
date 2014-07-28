<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script
	src="<s:url value="/js/raphael152-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/hm.cs.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/hm.sa.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
function onLoadPage() {
	createCharts();
}
var dash = null;
function onUnloadPage() {
	hm.cs.closeDash(dash);
}
var autoRefresh = false;
function onBeforeUnloadEvent(e){
	if (autoRefresh) {
		autoRefresh = false; // Clear no user prompt
		return;
	}
	var trackTitle = '<s:property value="trackChangeTitle" />';
	var tips = "You have unsaved changes. They will be lost if you continue."
				+"\n\n(If you don't like this popup window, change the option under"
				+"\n\"Home\">\""+trackTitle+"\")";
	hm.util.hide("processing");
	// for IE and FF
	if(e){
		e.returnValue = tips;
	}
	// for Safari
	return tips;
}
var navTop = 80+4, navLeft = ${leftWidth+20+5};
function createCharts() {
	<s:if test="%{easyMode}">
	navTop++;
	</s:if>
	hm.cs.loadIcons("<s:url includeParams="none" value="/images" />");
	dash = hm.cs.createDash(<s:property value="aid" />, "content", navTop, navLeft, 130, dashToolbar, dashBottom, "<s:property value="tilesStatus" />");
	hm.sa.initFFTChart(dash, 0, <s:property value="fftBand" />, <s:property value="fftChannels" />,
								<s:property value="fftCenter" />, <s:property value="fftSpan" />,
								<s:property value="fftRefLevel" />, <s:property value="fftVertScale" />,
								<s:property value="fftSample" />);
	hm.sa.initDutyChart(dash, 1, <s:property value="dutyBand" />, <s:property value="dutyChannels" />,
								 <s:property value="dutyCenter" />, <s:property value="dutySpan" />,
								 <s:property value="dutyMin" />, <s:property value="dutyMax" />);
	hm.sa.initSweptFFTChart(dash, 2, <s:property value="sweptFftBand" />, <s:property value="sweptFftChannels" />,
			<s:property value="sweptFftCenter" />, <s:property value="sweptFftSpan" />,
			dashDataUrl()+"sweptFft");
	hm.cs.startDash(dash, dashDataUrl()+"fft");
}

<%-- Global toolbar --%>
function dashToolbar(div, width, height) {
	var lbl = hm.cs.createLabel(7, 8, "Current AP: " + '<s:property value="currentAPHostName"/>', "titleLbl");
	div.appendChild(lbl);
}
function dashBottom(div, width, height) {
	var lbl = hm.cs.createLabel(7, 8, "Custom content ...", "titleLbl");
	div.appendChild(lbl);
}
function dashDataUrl() {
	return "rtt.action?aid="+dash.aid+"&operation=";
}
function settingsUrl(chart) {
	return "rtt.action?ignore="+new Date().getTime()+"&aid="+chart.dash.aid+"&operation=";
}
function resetRemainTimes(vals) {
}
function hideLeftMenu() {
	if (dash) {
		hm.cs.moveDash(dash, dash.top, 2, dash.bottom, 13);
	}
	if (YAHOO.env.ua.ie > 0) {
		autoRefresh = true; // To avoid user prompt, false onBeforeUnloadEvent trigger
	}
}
function showLeftMenu() {
	hm.cs.moveDash(dash, dash.top, navLeft, dash.bottom, 0);
	if (YAHOO.env.ua.ie > 0) {
		autoRefresh = true; // To avoid user prompt, false onBeforeUnloadEvent trigger
	}
}
</script>
<script type="text/javascript">
document.writeln('<style>html, body { overflow: hidden; }</style>');
</script>
<style>
.defaultLbl {
	font-size: 12px;
	white-space: nowrap;
}

.titleLbl {
	font-size: 12px;
	color: #003366;
	font-weight: bold;
	white-space: nowrap;
}

.markerLbl {
	font-size: 12px;
	color: #fff;
	white-space: nowrap;
}
</style>
<div id="content"></div>
