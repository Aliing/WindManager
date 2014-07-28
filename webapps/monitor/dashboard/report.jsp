<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<link type="text/css" rel="stylesheet" href="<s:url value="/css/jquery.fixedheadertable.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<link type="text/css" rel="stylesheet" href="<s:url value="/css/widget/ahReportChart.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/assets/skins/sam/skin.css" includeParams="none" />?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/treeview/assets/treeview-menu.css"  includeParams="none"/>" />
<link type="text/css" rel="stylesheet" href="<s:url value="/css/dashboard.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>

<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<script src="<s:url value="/yui/treeview/treeview-min.js" includeParams="none" />"></script>
<script src="<s:url value="/js/hm.da.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery.fixedheadertable.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery/plugin/jquery.tablesorter.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/underscore-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/innerhtml.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/chart/lang/loader.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/widget/chart/lang/en_US.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/report/en_US.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/report/data.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/report/widgetAdditional.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/report/simpleHelper.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/report/daHelper.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<style type="text/css">
#calendarpicker button {
    background: url(<s:url value="./images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarpicker1 button {
    background: url(<s:url value="./images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarpicker2 button {
    background: url(<s:url value="./images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarpicker3 button {
    background: url(<s:url value="./images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}
</style>
<style type="text/css">
<!--
#report_bar{
	padding: 2px 4px;
}

#report_title{
    color: grey;
    font-family: Helvetica,Arial,sans-serif;
    font-size: 14px;
    font-weight: bold;
}

#report_content_info{
	padding: 8px 4px 0;
}

#report_content_info span{
	display: none;
}

td.report_content{
	border-top: 2px solid #FFE43F;
	min-width: 1000px;
}
td.report_content div{
	display: block;
}
td.report_period{
	color: #666;
}
td.report_label, td.report_button{
	padding: 0 2px 2px 2px;
}
td.report_button a{
	margin: 0 8px;
}
td.report_button .wrap{
	float: right;
}
td.report_button .wrap .a{
	display: none;
}
-->
</style>

<script type='text/javascript'>
var formName = 'recurReport';
var customMode = false;
var da_widget_group_panel = null;
var waitingPanel = null;
var blnPageLoaded = false;

var isiPad = navigator.userAgent.match(/iPad/i) != null;
function onLoadPage() {
	var width = YAHOO.util.Dom.getViewportWidth()/(isiPad?4:6);
	YAHOO.util.Dom.setStyle("reportSettingView", "width", width+"px");
	registerSplit();
	var data = reportSettingPane.initTree();
	//hm.da.createTreeWidget("-1", "da_div_group_topy_tree1", data[0]);
	hm.da.createTreeWidget("-2", "da_div_group_filter_tree1", data[0]);
	hm.da.createTreeWidget("-2", "da_div_group_filter_userpro_tree1", data[1]);
	reportSettingPane.initWidget();
	reportSettingPane.resizeWidth(width);
	reportPane.init();
	reportPane.loadConfig();
	blnPageLoaded = true;
}

function isDash(){
	return true;
}

function registerSplit(){
	var minWidth = 160, maxWidth = 600;
	var splitter = Get("splitter");
	splitter.onmousedown = function(e){
		var d = document, doc = document.documentElement, body = document.body, e = e||window.event;
		var movingBar = document.createElement("div");
		with(movingBar.style){
			position = "absolute";
			width = "4px";
			cursor = "e-resize";
			background = "#999";
			zIndex = "9999";
		}
		body.appendChild(movingBar);
		var currentWidth = parseInt(YAHOO.util.Dom.getStyle("reportSettingView", "width")), newWidth;
		debug("left pane width: " + currentWidth);
		debug("mouse down");
		var x = e.pageX || (e.clientX + (doc && doc.scrollLeft || body && body.scrollLeft || 0));
		var y = e.pageY || (e.clientY + (doc && doc.scrollTop || body && body.scrollTop || 0));
		movingBar.style.height = Get("splitter").offsetHeight + "px";
		movingBar.style.top = YAHOO.util.Dom.getY(splitter) + "px";
		movingBar.style.left = x + "px";
		if(splitter.setCapture){
			splitter.setCapture();
		}else if(window.captureEvents){
			window.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP);
		}
		if(e.preventDefault){
			e.preventDefault();
		}else{
			e.returnValue = false;
		}
		d.onmousemove = function(e){
			debug("mouse move");
			e = e || window.event;
			if(!e.pageX){
				e.pageX = e.clientX + (doc && doc.scrollLeft || body && body.scrollLeft || 0);
			}
			if(!e.pageY){
				e.pageY = e.clientY + (doc && doc.scrollTop || body && body.scrollTop || 0);
			}
			debug("mouse pageX: " + e.pageX);
			var dx = e.pageX - x;
			var dy = e.pageY - y;
			newWidth = currentWidth + dx;
			if(newWidth >= minWidth && newWidth <= maxWidth){
				movingBar.style.left = e.pageX + "px";
			}
			newWidth = newWidth < minWidth ? minWidth : (newWidth > maxWidth ? maxWidth : newWidth);
		}
		d.onmouseup = function(){
			debug("mouse up");
			if(splitter.releaseCapture){
				splitter.releaseCapture();
			}else if(window.releaseEvents){
				window.releaseEvents(Event.MOUSEMOVE | Event.MOUSEUP);
			}
			d.onmousemove = null;
			d.onmouseup = null;
			body.removeChild(movingBar);
			debug("left pane new width: " + newWidth);
			YAHOO.util.Dom.setStyle("reportSettingView", "width", newWidth+"px");
			reportSettingPane.resizeWidth(newWidth);
		}
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	return true;
}

function sizeLeftDaGroup() {

}

var reportPane = (function(_$){
	var REPORT_PANE_STAGE_BASIC = 0;
	var REPORT_PANE_STAGE_WIDGET = 1;
	var REPORT_PANE_STAGE_FILTER = 2;
	var REPORT_PANE_STAGE_SCHEDULE = 3;
	var REPORT_PANE_STAGE_PREVIEW = 4;

	<s:if test="%{dataSource.id == null}">
	var currentStage = REPORT_PANE_STAGE_BASIC;//by default
	</s:if>
	<s:else>
	var currentStage = REPORT_PANE_STAGE_WIDGET;//by default
	</s:else>
	var reportBasicPanel;
	var reportSchedulePanel;
	var report_bar;
	var report_title;
	var report_btn_edit;
	var report_btn_back;
	var report_btn_continue;
	var report_btn_save;
	var report_btn_cancel;
	var report_content_info_1;
	var report_content_info_2;
	var report_content_blank;
	var report_content_filled;
	var report_content_period;
	var report_left_pane_td;
	var splitter;
	var initialize = function(){
		report_bar = _$("#report_bar");
		report_title = _$("#report_title");
		report_btn_edit = _$("#report_btn_edit");
		report_btn_back = _$("#report_btn_back");
		report_btn_continue = _$("#report_btn_continue");
		report_btn_save = _$("#report_btn_save");
		report_btn_cancel = _$("#report_btn_cancel");
		report_content_info_1 = _$("#report_content_info_1");
		report_content_info_2 = _$("#report_content_info_2");
		report_content_blank = _$("#report_content_blank");
		report_content_filled = _$("#report_content_filled");
		report_content_period = _$(".report_period div");
		report_left_pane_td = _$("#report_left_pane_td");
		splitter = _$("#splitter");
		refreshLayout();
	};

	var refreshLayout = function(){
		switch(currentStage){
		case REPORT_PANE_STAGE_BASIC:
			report_bar.css({"visibility": "hidden"});
			report_content_blank.css({"display": "block"});
			report_content_filled.css({"display": "none"});
			report_left_pane_td.css({"display": "block"});
			splitter.css({"display": "block"});
			showReportBasicPanel();
			adjustContentHeight(false);
			break;
		case REPORT_PANE_STAGE_WIDGET:
			report_bar.css({"visibility": "visible"});
			report_btn_edit.css({"display": "none"});
			report_btn_back.css({"display": "none"});
			report_btn_continue.css({"display": "block"});
			report_btn_save.css({"display": "block"});
			report_btn_cancel.css({"display": "block"});
			report_content_info_1.css({"display": "block"});
			report_content_info_2.css({"display": "none"});
			report_content_blank.css({"display": "none"});
			report_content_filled.css({"display": "block"});
			report_content_period.css({"display": "none"});
			report_left_pane_td.css({"display": ""});
			splitter.css({"display": ""});
			reportSettingPane.expandWidgetBox();
			adjustContentHeight(false);
			break;
		case REPORT_PANE_STAGE_FILTER:
			report_bar.css({"visibility": "visible"});
			report_btn_edit.css({"display": "none"});
			report_btn_back.css({"display": "block"});
			report_btn_continue.css({"display": "block"});
			report_btn_save.css({"display": "block"});
			report_btn_cancel.css({"display": "block"});
			report_content_info_1.css({"display": "none"});
			report_content_info_2.css({"display": "block"});
			report_content_blank.css({"display": "none"});
			report_content_filled.css({"display": "block"});
			report_content_period.css({"display": "none"});
			report_left_pane_td.css({"display": ""});
			splitter.css({"display": ""});
			reportSettingPane.expandFilterBox();
			adjustContentHeight(false);
			break;
		case REPORT_PANE_STAGE_SCHEDULE:
			report_bar.css({"visibility": "visible"});
			report_btn_edit.css({"display": "none"});
			report_btn_back.css({"display": "block"});
			report_btn_continue.css({"display": "block"});
			report_btn_save.css({"display": "block"});
			report_btn_cancel.css({"display": "block"});
			report_content_info_1.css({"display": "none"});
			report_content_info_2.css({"display": "none"});
			report_content_blank.css({"display": "none"});
			report_content_filled.css({"display": "block"});
			report_content_period.css({"display": "none"});
			report_left_pane_td.css({"display": ""});
			splitter.css({"display": ""});
			showReportSchedulePanel();
			adjustContentHeight(false);
			break;
		case REPORT_PANE_STAGE_PREVIEW:
			report_bar.css({"visibility": "visible"});
			report_btn_edit.css({"display": "block"});
			report_btn_back.css({"display": "block"});
			report_btn_continue.css({"display": "none"});
			report_btn_save.css({"display": "block"});
			report_btn_cancel.css({"display": "block"});
			report_content_info_1.css({"display": "none"});
			report_content_info_2.css({"display": "none"});
			report_content_blank.css({"display": "none"});
			report_content_filled.css({"display": "block"});
			report_content_period.css({"display": "block"});
			report_left_pane_td.css({"display": "none"});
			splitter.css({"display": "none"});
			//do preview request for current dashboard
			daPanel.preview();
			adjustContentHeight(true);
			break;
		default:
			report_bar.css({"visibility": "block"});
			report_btn_edit.css({"display": "none"});
			report_btn_back.css({"display": "none"});
			report_btn_continue.css({"display": "none"});
			report_btn_save.css({"display": "none"});
			report_btn_cancel.css({"display": "block"});
			report_content_info_1.css({"display": "none"});
			report_content_info_2.css({"display": "none"});
			report_content_blank.css({"display": "block"});
			report_content_filled.css({"display": "none"});
			report_content_period.css({"display": "none"});
			report_left_pane_td.css({"display": ""});
			splitter.css({"display": ""});
			adjustContentHeight(false);
			break;
		}
	};

	var adjustContentHeight = function(set){
		var content = _$("td.report_content");
		if(set){
			var vp = _$(window).height();
			var dm = _$(document).height();
			var ot = content.offset().top;
			if(dm <= vp){
				content.css({"height": (vp-ot-2)+"px"});
			}
		}else{
			content.css({"height": "100%"});
		}
	}

	var continueClick = function(){
		if(!validateContinue()){
			return;
		}
		currentStage++;
		refreshLayout();
	};

	var validateContinue = function(){
		if(currentStage < REPORT_PANE_STAGE_WIDGET){
			return true;
		}
		var dashboard = getCurrentDashboard();
		var selected = dashboard.getAllReportIds();
		if(null == selected || selected.length == 0){
			showWarnDialog("<s:text name="error.pleaseAddItems" />");
			return false;
		}
		return true;
	};

	var backClick = function(){
		currentStage--;
		refreshLayout();
		//enter edit for current dashboard
		var dashboard = getCurrentDashboard();
		if (!dashboard.isInEditMode()) {
			dashboard.toggleMode(true);
		}
	};

	var saveClick = function(){
		if(!validateSave()){
			return;
		}
		var configs = daPanel.getLayouts(getCurrentDashboard());
		if (configs) {
			var hiddenElContainer = _$("div.hiddenArraySection")[0];
			if (configs.cols) {
				hm.util.createFormArrayEls(hiddenElContainer, "columnsConfig", configs.cols, true);
				hm.util.createFormArrayEls(hiddenElContainer, "columnCharts", configs.charts);
			}
		}
		<s:if test="%{dataSource.id == null}">
		submitAction("create");
		</s:if>
		<s:else>
		submitAction("update");
		</s:else>
	};

	var validateSave = function(){
		return validateContinue();
	}

	var cancelClick = function(){
		submitAction("cancel");
	};

	var editClick = function(){
		currentStage = REPORT_PANE_STAGE_WIDGET;
		refreshLayout();
		daPanel.edit();
	};

	var loadReportConfig = function(){
		var url = "<s:url action='recurReport' includeParams='none'/>?operation=loadReportConfig&ignore="+new Date().getTime();;
		ajaxRequest(null, url, succLoadReportConfig);
	};

	var succLoadReportConfig = function(o){
		eval("var data = " + o.responseText);
		fillReportBasicSettings(data.id, data.rn, data.rd, data.rh, data.rf, data.rs);
		fillReportScheduleSetting(data.fr);
	};

	var createReportBasicPanel = function(){
		var div = document.getElementById("new_report_basic_panel");
		reportBasicPanel = new YAHOO.widget.Panel(div, {
			width:"600px",
			visible:false,
			draggable:true,
			modal:true,
			constraintoviewport:false
		});
		reportBasicPanel.render(document.body);
		div.style.display = ""
	}

	var showReportBasicPanel = function(){
		if(null == reportBasicPanel){
			createReportBasicPanel();
		}
		var offset = _$(".report_content").offset();
		reportBasicPanel.cfg.setProperty("xy", [offset.left, offset.top + 5]);
		if(currentStage == REPORT_PANE_STAGE_BASIC){
			reportBasicPanel.cfg.setProperty("close", false);
		}else{
			reportBasicPanel.cfg.setProperty("close", true);
		}
		reportBasicPanel.show();
		focusReportBasicSettings();
	};

	var saveReportBasic = function(){
		if(!validateReportBasicSettings()){
			return;
		}
		var url = "<s:url action='recurReport' includeParams='none'/>?operation=saveReportBasic";
		ajaxRequest("report_basic_setting", url, succSaveReportBasic, "post");
		reportBasicPanel.hide();
	};

	var succSaveReportBasic = function(o){
		try {
			eval("var data = " + o.responseText);
			if(data.suc){
				report_title.text(data.n);
				if(currentStage == REPORT_PANE_STAGE_BASIC){
					continueClick();
				}
			}
		}catch(e){
			showWarnDialog("Save Report Info Error.", "Error");
			return;
		}
	};

	var cancelReportBasic = function(){
		if(null != reportBasicPanel){
			reportBasicPanel.hide();
		}
		if(currentStage == REPORT_PANE_STAGE_BASIC){
			cancelClick();
		}
	};

	var createReportSchedulePanel = function(){
		var div = document.getElementById("new_report_schedule_panel");
		reportSchedulePanel = new YAHOO.widget.Panel(div, {
			width:"600px",
			visible:false,
			close:false,
			draggable:true,
			modal:true,
			constraintoviewport:false
		});
		reportSchedulePanel.render(document.body);
		div.style.display = ""
	};

	var showReportSchedulePanel = function(){
		if(null == reportSchedulePanel){
			createReportSchedulePanel();
		}
		var offset = _$(".report_content").offset();
		reportSchedulePanel.cfg.setProperty("xy", [offset.left, offset.top + 5]);
		init_report_customtime_settings();
		reportSchedulePanel.show();
	};

	var saveReportSchedule = function(){
		if(!validateReportScheduleSettings()){
			return;
		}
		collectReportScheduleSettings("report_schedule_setting");
		var url = "<s:url action='recurReport' includeParams='none'/>?operation=saveReportSchedule";
		ajaxRequest("report_schedule_setting", url, succSaveReportSchedule, "post");
		reportSchedulePanel.hide();
	};

	var succSaveReportSchedule = function(o){
		try {
			eval("var data = " + o.responseText);
			if(data.pd){
				_$(".report_period span").html(data.pd);
			}
			if(data.suc){
				continueClick();
			}
		}catch(e){
			showWarnDialog("Save Report Info Error.", "Error");
			return;
		}
	};

	var cancelReportSchedule = function(){
		if(null != reportSchedulePanel){
			reportSchedulePanel.hide();
		}
		backClick();
	};

	return {
		init: initialize,
		go: continueClick,
		back: backClick,
		edit: editClick,
		save: saveClick,
		cancel: cancelClick,
		loadConfig: loadReportConfig,
		showBasic: showReportBasicPanel,
		saveBasic: saveReportBasic,
		cancelBasic: cancelReportBasic,
		showSchedule: showReportSchedulePanel,
		saveSchedule: saveReportSchedule,
		cancelSchedule: cancelReportSchedule
	};
})(jQuery);

// widget panel
YAHOO.util.Event.onDOMReady(init);

function init(){
	var div2 = document.getElementById('da_widget_group_panel');
	da_widget_group_panel = new YAHOO.widget.Panel(div2, {
		width:"550px",
		visible:false,
		fixedcenter:false,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	da_widget_group_panel.render(document.body);
	div2.style.display = "";
}


function openWidgetGroupPanel(){
	if(null != da_widget_group_panel){
		da_widget_group_panel.center();
		da_widget_group_panel.cfg.setProperty('visible', true);
	}
}

function hideWidgetGroupPanel(){
	if(null != da_widget_group_panel){
		da_widget_group_panel.cfg.setProperty('visible', false);
	}
}
// end
</script>

<div id="content"><s:form action="recurReport">
	<s:hidden name="operation" /><s:hidden name="id" />
	<div class="hiddenArraySection" style="display:none;"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="body_bg"><img
								src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
								width="9" height="9" alt="" class="dblk" /></td>
						<td class="menu_bg" width="100%">
						<td class="body_bg"><img
								src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
								width="9" height="9" alt="" class="dblk" /></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="menu_bg" style="padding: 2px 2px 0 2px;">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<div id="report_bar" style="visibility: hidden;">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td nowrap="nowrap" class="report_label">
											<span id="report_title"><s:property value="dataSource.dashName" /></span>
											<a class="marginBtn" href="javascript:void(0);" onclick="reportPane.showBasic();"><img class="dinl"
												src="<s:url value="/images/modify-one.png" />"
												width="16" height="16" alt="" style="vertical-align: middle;" title="<s:text name="button.edit"></s:text>" /></a>
										</td>
										<td></td>
										<td width="400px" align="right" nowrap="nowrap" class="report_period"><div><s:text name="hm.recurreport.config.period.label" /> <span><s:property value="%{dataSource.displayReportCurrentTimeString}" /></span></div></td>
										<td width="350px" align="right" nowrap="nowrap" class="report_button npcButton">
											<div class="wrap">
											<a href="javascript:void(0);" onclick="reportPane.back();" id="report_btn_back" class="btCurrent"  title="<s:text name="button.back"></s:text>"><span><s:text name="button.back"/></span></a>
											<a href="javascript:void(0);" onclick="reportPane.go();" id="report_btn_continue" class="btCurrent"  title="<s:text name="button.continue"></s:text>"><span><s:text name="button.continue"/></span></a>
											<a href="javascript:void(0);" onclick="reportPane.edit();" id="report_btn_edit" class="btCurrent"  title="<s:text name="button.edit"></s:text>"><span><s:text name="button.edit"/></span></a>
											<s:if test="%{!updateDisabled}">
											<a href="javascript:void(0);" onclick="reportPane.save();" id="report_btn_save" class="btCurrent"  title="<s:text name="common.button.save"></s:text>"><span><s:text name="common.button.save"/></span></a>
											</s:if>
											<a href="javascript:void(0);" onclick="reportPane.cancel();" id="report_btn_cancel" class="btCurrent"  title="<s:text name="common.button.cancel"></s:text>"><span><s:text name="common.button.cancel"/></span></a>
											</div>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td><tiles:insertDefinition name="notes" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td valign="top" style="padding-right: 20px" id="report_left_pane_td">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr><td><tiles:insertDefinition name="reportSettingView" /></td></tr>
										</table>
									</td>
									<td id="splitter" style="border-left: 2px solid #ddd; border-right: 2px solid #ddd; cursor: e-resize; width: 0;"></td>
									<td valign="top" width="100%" style="padding-left: 2px;">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td valign="top" class="report_content">
													<div id="report_content_info">
														<span id="report_content_info_1"><s:text name="hm.recurreport.config.new.info.step1" /></span>
														<span id="report_content_info_2"><s:text name="hm.recurreport.config.new.info.step2" /></span>
													</div>
													<div id="report_content_blank">
													</div>
													<div id="report_content_filled">
													</div>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>

<div id="da_widget_group_panel" style="display: none;">
	<div class="hd">
		<s:text name="hm.dashboard.tree.select.topology.title"/>
	</div>
	<div class="bd" style="overflow: auto;">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="body_bg"><img
						src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				<td class="menu_bg" width="100%">
				<td class="body_bg"><img
						src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
			</tr>
			<tr>
				<td class="menu_bg"/>
				<td class="menu_bg" width="100%">

					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<!--
							<td width="33%" valign="top">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="dac_group_title"><s:text name="hm.dashboard.tree.group.title"/></td>
									</tr>
									<tr>
										<td>
											<div id="da_div_group_topy_tree1"></div>
										</td>
									</tr>
								</table>
							</td>
 -->
							<td width="49%" valign="top">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="dac_group_title"> <s:text name="hm.dashboard.tree.filter.scrop"/> </td>
									</tr>
									<tr>
										<td>
											<div id="da_div_group_filter_tree1"></div>
										</td>
									</tr>
								</table>
							</td>

							<td width="49%" valign="top">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="dac_group_title"> <s:text name="hm.dashboard.tree.filter.title"/> </td>
									</tr>
									<tr>
										<td>
											<div id="da_div_group_filter_userpro_tree1"></div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table  cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr><td height="20px"/></tr>
									<tr>
										<td class="sepLine">
											<img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
										</td>
									</tr>
									<tr><td height="20px"/></tr>
								</table>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td>
											<input type="button" name="ignore" value="OK"
												class="button" onClick="hm.da.saveWidgetGroupPanel(daPanel.saveTopo);">
										</td>
										<td width="20px"/>
										<td>
											<input type="button" name="ignore" value="Cancel"
												class="button" onClick="hideWidgetGroupPanel();">
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
				<td class="menu_bg"></td>
			</tr>
		</table>
	</div>
</div>

<div id="new_report_basic_panel" style="display: none;">
    <div class="hd"><s:if test="%{dataSource.id == null}">
		<s:text name="hm.recurreport.config.new.title" />
		</s:if>
		<s:else>
		<s:text name="hm.recurreport.config.edit.title" />
		</s:else></div>
    <div class="bd"><s:form action="recurReport" name="report_basic_setting" id="report_basic_setting">
    	<table cellspacing="0" cellpadding="0" border="0" width="100%">
    		<tr>
    			<td>
    				<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
    					<tr><td><tiles:insertDefinition name="reportBasicView" /></td></tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding-top: 8px;">
    				<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><input type="button" name="ignore"
								value="<s:text name="common.button.ok"></s:text>" class="button" onclick="reportPane.saveBasic();"></td>
							<td><input type="button" name="ignore" value="<s:text name="common.button.cancel"></s:text>"
								class="button" onclick="reportPane.cancelBasic();"></td>
						</tr>
    				</table>
    			</td>
    		</tr>
    	</table></s:form>
    </div>
</div>
<div id="new_report_schedule_panel" style="display: none;">
    <div class="hd"><s:text name="hm.recurreport.config.new.schedule.title" /></div>
    <div class="bd"><s:form action="recurReport" name="report_schedule_setting" id="report_schedule_setting">
    	<table cellspacing="0" cellpadding="0" border="0" width="100%">
    		<tr>
    			<td>
    				<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
    					<tr><td><tiles:insertDefinition name="reportScheduleView" /></td></tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding-top: 8px;">
    				<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><input type="button" name="ignore"
								value="<s:text name="common.button.ok"></s:text>" class="button" onclick="reportPane.saveSchedule();"></td>
							<td><input type="button" name="ignore" value="<s:text name="common.button.cancel"></s:text>"
								class="button" onclick="reportPane.cancelSchedule();"></td>
						</tr>
    				</table>
    			</td>
    		</tr>
    	</table></s:form>
    </div>
</div>
<div id="widgetCopyDiv" style="display: none;">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td align="right" colspan="2" ><a class="marginBtn" id="da_bt_closeWidgetEditLink">
			<img src="<s:url value="/images/cancel.png" includeParams="none"/>"
				width="16" height="16" title="Close" class="dinl"/></a></td>
		</tr>
		<tr><td colspan="2"><div id="widgetCopyErrorDiv"></div></td></tr>

		<tr>
			<td class="labelT1"><s:text name="hm.dashboard.widget.type"/></td>
			<td><s:select id="da_select_wd_groupType" list="lstComponentGroupType" listKey="key" listValue="value" style="width:350px;" onchange="hm.da.changeComponentGroup(this.value);"></s:select></td>
		</tr>
		<tr>
			<td class="labelT1"><s:text name="hm.dashboard.widget.dataSet"/></td>
			<td> <select id="da_select_wd_metric" style="width:350px;"
					onchange="hm.da.changeWidgetMetric(this.options[this.selectedIndex].text);"></select>
			</td>
		</tr>
		<tr>
			<td class="labelT1"><s:text name="hm.dashboard.widget.name"/></td>
			<td><s:textfield name="da_tx_wd_title" id="da_tx_wd_title" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" maxlength="64" size="45" ></s:textfield>&nbsp;<s:text name="hm.dashboard.widget.name.range"/></td>
		</tr>

		<tr>
			<td colspan="2" align="center"><table>
				<tr>
				<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="da_bt_editWidgetDone" title="Done"><span><s:text name="hm.dashboard.widget.done"/></span></a></td>
				</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<script>
	var currentDashBoardId="<s:property value="dataSource.id"/>";
	var cloneFlg=<s:property value="dataSource.cloneDash"/>;
	var daPanel = (function($) {
		head.js("<s:url value="/js/jquery.overlay.min.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/highcharts.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/exporting.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/json2-min.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/ahReportChart.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/chartControls.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/dashControls.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/theme/recurReportTheme.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/render/dashRender.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/dataRender/dashDataBase.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/dataRender/dashData.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/dashboard.js" includeParams="none" />?v=<s:property value="verParam" />",
				"<s:url value="/js/widget/chart/daCustom/recurReportSpecial.js" includeParams="none" />?v=<s:property value="verParam" />",
				function() {
					Aerohive.lang.chart.device.status.group.title.name = '<s:text name="hm.dashboard.widget.clientdevice.config.group.name.device" />';
					REPORT_ADDITIONAL["login administrator"] = null;
					AhReportChart.Chart.prototype.whenRefreshData = function(chart, data) {
						if (!chart || !chart.currentDashboard) {
							return;
						}
						chart.currentDashboard.setLastRefreshTime(new Date().getTime());
					};
					prepareDashboardSettings();
					checkToResetMyDashboard();
				}
		);
		
		var waitedCounts = 0;
		var waitToResetTimerId;
		var checkToResetMyDashboard = function() {
			if (waitToResetTimerId) {
				clearTimeout(waitToResetTimerId);
			}
			if (!blnPageLoaded && waitedCounts < 5) {
				waitedCounts = waitedCounts + 1;
				waitToResetTimerId = setTimeout(checkToResetMyDashboard, 100);
				return;
			}
			resetMyDashboard();
		}

		var daGroupContainers = {
			getCommon: function() {
				return "report_content_filled";
			},
			addCommon: function() {
				return dashboardGroup.containers.generate({
					parent: 'report_content_filled'
				});
			}
		};

		var dashboardGroup = null;
		function prepareDashboardSettings() {
			dashboardGroup = new AhDashboard.DashboardGroup({});
			AhDashboard.Dashboard.prototype.configFunc = doDashboardChartConfig;
			AhDashboard.Dashboard.prototype.topologyConfigFunc = doDaChartTopologyConfig;
			AhDashboard.Dashboard.prototype.afterCloseFunc = afterChartIsClosed;
			AhDashboard.Dashboard.prototype.checkConfigFunc = doDaChartCheckConfig;
			AhDashboard.Dashboard.prototype.daHelper = AhDashboardHelper.helper;
			AhDashboard.Dashboard.prototype.afterRenderedCommonFunc = function(daTmp) {
				sizeLeftDaGroup();
				if (widgetPanel) {
					widgetPanel.syncWidgetSelection();
				}
			};
		};

		var curEditContent;
		function doDashboardChartConfig(chartContainer, options, chartId) {
			sizeLeftDaGroup();
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				var editContent = dashboardTmp.toggleEditArea(chartContainer, true);
				if(!editContent) {
					return;
				}
				var blnCloned = false;
				if (typeof options.cloned !== 'undefined') {
					blnCloned = options.cloned;
				}
				curEditContent = editContent;
				$('#'+editContent).empty();
				var divHtml =$('#widgetCopyDiv').html();
				$('#'+editContent).html(divHtml);
				$('#widgetCopyDiv').html('');

				$('#da_bt_closeWidgetEditLink').click(function(e){
					dashboardTmp.toggleEditArea(chartContainer, false, false);
					$('#widgetCopyDiv').html(divHtml);
					$('#'+editContent).html('');
					sizeLeftDaGroup();
				});
				$('#da_bt_editWidgetDone').click(function(e){
					var msg = hm.da.checkWidgetDone(chartId);
					if(msg==null){
						hm.da.saveWidgetCopyDiv(chartId,dashboardTmp,chartContainer,editContent, divHtml,blnCloned);
					} else {
						hm.util.displayJsonErrorNoteWithID(msg, "widgetCopyErrorDiv");
					}
				});

				hm.da.initWidgetCopyDivValue(chartId);
				var optionsTmp = dashboardTmp.getCurrentEditWidgetOptions(chartContainer);
				var titleTmp = '';
				if (optionsTmp && optionsTmp.title) {
					$('#da_tx_wd_title').val(optionsTmp.title);
				}
			}
		}

		function doDaChartTopologyConfig(chart, chartContainer, chartId, widgetIdArg) {
			if (!chartContainer || !chartId) {
				return;
			}
			var dashboardTmp = getCurrentDashboard();
			if (!dashboardTmp) {
				return;
			}

			startTopologySelectForChart(chart, widgetIdArg);
		}

		function doDaChartCheckConfig(chart, chartContainer, chartId, widgetIdArg, checked) {
			if (!chart) {
				return;
			}
			var dashboardTmp = getCurrentDashboard();
			if (!dashboardTmp) {
				return;
			}

			startCheckForWidget(chart, chartContainer, widgetIdArg, checked);
		}

		function startCheckForWidget(chart, chartContainer, widgetIdArg, checked) {
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				dashboardTmp.setTmpReportConfig(dashboardTmp.getCurCtlBarSelectContainer(),
						{
							'checked': checked
						}, true);
			}
		}
		var succDoCheckForWidget = function(data, textStatus) {
		};

		function afterChartIsClosed(chartContainer, dcId) {
			sizeLeftDaGroup();
			if (dcId && widgetPanel) {
				widgetPanel.uncheckWidget(dcId);
			}
		}

		function createNewDashboardForRecurReport(options) {
			if (!options) {
				options = {};
			}
			options.action = "recurReport.action";
			options.configType = "tmp";

			return new AhDashboard.Dashboard(options);
		}

		var myDashboard = null;
		function resetMyDashboard() {
			var dashOptions = {};
			dashOptions.container = daGroupContainers.addCommon();
			if (currentDashBoardId) {
				dashOptions.boardId = parseInt(currentDashBoardId);
			} else {
				dashOptions.boardId = -1;
				if (cloneFlg==true) {
					dashOptions.helper = 'clone';
				} else {
					dashOptions.helper = 'new';
					dashOptions.requestSetting4New = true;
				}

			}
			dashOptions.p = {r: true};
			<s:if test="%{writeDisabled != 'disabled'}">
			dashOptions.p.w = true;
			</s:if>
			dashOptions.opAllowed = "none";
			myDashboard = createNewDashboardForRecurReport(dashOptions);
			myDashboard.render();
			myDashboard.setNotRequestData(true);
			setCustomMode(true);
		}

		function setCustomMode(mode) {
			//customMode = mode;
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				dashboardTmp.toggleMode(mode);
			}
		}

		function succSaveMyDashboardDone() {
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				dashboardTmp.setChanged(false);
				dashboardTmp.clearRecyleItems();
			}
		}

		function getCurrentDashboard() {
			return myDashboard;
		}

		var saveWidgetGroupForTemporary = function() {
			this.setTmpReportConfig(this.getCurCtlBarSelectContainer(),
					{
						'lid': widgetSelectedMapNode.data.id,
						'obId': widgetSelectedFilterNode.data.id,
						'obType': widgetSelectedFilterNode.data.tp,
						'fobId': widgetSelectedFilterUpNode.data.id,
						'fobType': widgetSelectedFilterUpNode.data.tp
					}, true);
		};
		function startTopologySelectForChart(chart, widgetIdArg) {
			if(!chart) {
				return false;
			}

			var dashboardTmp = getCurrentDashboard();
			if (!dashboardTmp) {
				return false;
			}
			var tmpTopoConfig = dashboardTmp.getCurReportWidgetConfig(chart);
			var tmpObType=null;
			var tmpObId=null;
			var tmpMap=null;

			if (tmpTopoConfig.hasOwnProperty('obType')) {
				tmpObType = tmpTopoConfig.obType;
			}
			if (tmpTopoConfig.hasOwnProperty('obId')) {
				tmpObId = tmpTopoConfig.obId;
			}
			//if (tmpTopoConfig.hasOwnProperty('lid')) {
			//	tmpMap = tmpTopoConfig.lid;
			//}

			$.post("recurReport.action",{
				'operation': 'topoSelect',
				'widgetId': widgetIdArg,
				//'widgetLocationId': tmpMap,
				'treeId': tmpObId,
				'treeType': tmpObType
			}, function(data, textStatus) {
				succDoTopologySelect(chart, data, textStatus);
			});
		}
		var succDoTopologySelect = function(chart, data, textStatus) {
			try {
				eval("var data = " + data);
				if (data.t) {
					var dashboardTmp = getCurrentDashboard();
					if (!dashboardTmp) {
						return false;
					}
					var tmpTopoConfig = dashboardTmp.getCurReportWidgetConfig(chart);
					if (tmpTopoConfig) {
						if (tmpTopoConfig.hasOwnProperty('obType')) {
							data.obType = tmpTopoConfig.obType;
						}
						if (tmpTopoConfig.hasOwnProperty('obId')) {
							data.obId = tmpTopoConfig.obId;
						}
						//if (tmpTopoConfig.hasOwnProperty('lid')) {
						//	data.map = tmpTopoConfig.lid;
						//}
						if (tmpTopoConfig.hasOwnProperty('fobType')) {
							data.fobType = tmpTopoConfig.fobType;
						}
						if (tmpTopoConfig.hasOwnProperty('fobId')) {
							data.fobId = tmpTopoConfig.fobId;
						}
					}
					if (data.v) {
						eval("var data2 = " + data.v);
						eval("var data3 = " + data.v3);
						Get("da_div_group_filter_tree1").innerHTML='';
						Get("da_div_group_filter_userpro_tree1").innerHTML='';
						hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree1");
						hm.da.createTreeWidget(data.obId, "da_div_group_filter_tree1", data2);
						hm.da.createTreeWidget(data.fobId, "da_div_group_filter_userpro_tree1", data3);
					}
					openWidgetGroupPanel();
				} else {
					showWarnDialog(data.m,"Error");
				}
			} catch(e){
				showWarnDialog("init Widget group Error.", "Error");
				return false;
			}
		};

		function prepareDaLayoutsConfig(dashboardTmp) {
			if (!dashboardTmp) {
				return;
			}
			var layouts = dashboardTmp.getColumnLayout();
			var columnsLayout = new Array();
			var chartsLayout = [];
			var prepareElType = function(el) {
				if (typeof el == 'undefined') {
					return ';';
				}
				return ';' + el;
			};
			_.each(
				_.sortBy(
						_.values(layouts), function(obj){
							return obj.order;
						}
				),
				function(obj) {
					columnsLayout.push(obj.size);
					chartsLayout.push(_.reduce(obj.charts, function(memo, objC){
						if (!objC) {
							return;
						}
						var strTmp = objC.id + prepareElType(objC.title)
												+ prepareElType(objC.overTime)
												+ prepareElType(objC.cloned) + prepareElType(objC.wId);
						if (objC.wConfig) {
							//strTmp += prepareElType(objC.wConfig.lid)
							strTmp += prepareElType(objC.wConfig.obType)
										+ prepareElType(objC.wConfig.obId)
										+ prepareElType(objC.wConfig.fobType)
										+ prepareElType(objC.wConfig.fobId)
										+ prepareElType(objC.wConfig.checked);
						}
						if (memo !== '') return memo + ',' + strTmp;
						return strTmp;
					}, ''));
				}
			);

			return {
				"cols": columnsLayout,
				"charts": chartsLayout
			};
		}

		var curEditContent;
		function doDashboardChartConfig(chartContainer, options, chartId) {
			sizeLeftDaGroup();
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				var editContent = dashboardTmp.toggleEditArea(chartContainer, true);
				if(!editContent) {
					return;
				}
				var blnCloned = false;
				if (typeof options.cloned !== 'undefined') {
					blnCloned = options.cloned;
				}
				curEditContent = editContent;
				$('#'+editContent).empty();
				var divHtml =$('#widgetCopyDiv').html();
				$('#'+editContent).html(divHtml);
				$('#widgetCopyDiv').html('');
				//hm.da.resetWidgetCopyDiv();

				$('#da_bt_closeWidgetEditLink').click(function(e){
					dashboardTmp.toggleEditArea(chartContainer, false, false);
					$('#widgetCopyDiv').html(divHtml);
					$('#'+editContent).html('');
					sizeLeftDaGroup();
				});
				$('#da_bt_editWidgetDone').click(function(e){
					var msg = hm.da.checkWidgetDone(chartId);
					if(msg==null){
						hm.da.saveWidgetCopyDiv(chartId,dashboardTmp,chartContainer,editContent, divHtml);
					} else {
						hm.util.displayJsonErrorNoteWithID(msg, "widgetCopyErrorDiv");
					}
				});
				hm.da.initWidgetCopyDivValue(chartId);
				var optionsTmp = dashboardTmp.getCurrentEditWidgetOptions(chartContainer);
				var titleTmp = '';
				if (optionsTmp && optionsTmp.title) {
					$('#da_tx_wd_title').val(optionsTmp.title);
				}
			}
		}

		var doReportPreview = function() {
			if (myDashboard) {
				myDashboard.toggleMode(false, true);
			}
		};

		var doReportEdit = function() {
			if (myDashboard) {
				myDashboard.toggleMode(true);
			}
		};

		return {
			getCurrentDashboard: getCurrentDashboard,
			preview: doReportPreview,
			edit: doReportEdit,
			getLayouts: prepareDaLayoutsConfig,
			saveTopo: saveWidgetGroupForTemporary
		};
	})(jQuery);

	getCurrentDashboard = daPanel.getCurrentDashboard;
</script>