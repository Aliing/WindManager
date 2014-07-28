<%@page import="com.ah.bo.dashboard.DashboardComponent"%>
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
<script src="<s:url value="/js/widget/ulMenu.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<style>
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
<script type='text/javascript'>
var formName = 'dashboard';
var imagesBaseUrl = "<s:url value="/images/" includeParams="none"/>";
var appInvalidMsg = "<s:text name="info.da.application.remove.deny" />";
var writePermission = <s:property value="writePermission"/>;
var customMode = false;
var defaultTabId='<s:property value="dafaultTabId"/>';
var applicationPerspectiveId='<s:property value="applicationPerspectiveId"/>';
var currentDashBoardId="<s:property value="dataSource.id"/>";
var currentDashBoardType="<s:property value="dataSource.daType"/>";
var cloneTabFlg=false;
//var currentWidgetId=null;
var thisOperation;
var appWarningDailogTimeout=600000;
var homedomain=<s:property value="isInHomeDomain"/>;
var COMPONENT_GROUP_APPLICATION = <%=DashboardComponent.COMPONENT_GROUP_APPLICATION%>;
var COMPONENT_GROUP_CLIENTS = <%=DashboardComponent.COMPONENT_GROUP_CLIENTS%>;
var COMPONENT_GROUP_USERS = <%=DashboardComponent.COMPONENT_GROUP_USERS%>;
var COMPONENT_GROUP_AEROHIVEDEVICE = <%=DashboardComponent.COMPONENT_GROUP_AEROHIVEDEVICE%>;
var COMPONENT_GROUP_NETWORK = <%=DashboardComponent.COMPONENT_GROUP_NETWORK%>;

var newDashParamSettings = {m_location:-1, m_objectType:-2, m_objectId:"-2",m_fobjectType:-2, m_fobjectId:"-2", m_selectTimeType:1, m_customStartTime:0, m_customEndTime:0,m_enableTimeLocal:false};

var waitingPanel = null;

var highApTimeOutArray={};

function getAvailableComponents(pageMode){
	if(applicationPerspectiveId == currentDashBoardId && pageMode != hm.da.MODE_NEW){
		return [COMPONENT_GROUP_APPLICATION];
	}else{
		return [COMPONENT_GROUP_CLIENTS, COMPONENT_GROUP_AEROHIVEDEVICE, COMPONENT_GROUP_NETWORK];
	}
}

function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

function isMonitorUserDash(){
	//if(Get("da_td_tab_new_temp")) return false;
	if(currentDashBoardType==4) {
		return true;
	}
	return false;
}

function isMonitorApplicationDash(){
	//if(Get("da_td_tab_new_temp")) return false;
	if(currentDashBoardType==3) {
		return true;
	}
	return false;
}

function isDash(){
	//if(Get("da_td_tab_new_temp")) return true;
	if(currentDashBoardType==1) {
		return true;
	}
	return false;
}

function isDrilldownDash() {
	//if(Get("da_td_tab_new_temp")) return false;
	if(currentDashBoardType==5) {
		return true;
	}
	return false;
}

function setCustomMode(mode) {
	customMode = mode;
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		dashboardTmp.toggleMode(customMode);
	}
}

var isiPad = navigator.userAgent.match(/iPad/i) != null;
function onLoadPage() {
	var width = YAHOO.util.Dom.getViewportWidth()/(isiPad?4:6);
	YAHOO.util.Dom.setStyle("left_view_pane_wrap", "width", width+"px");
	registerSplit();
	createTabTrWhenOnLoad();
	changeCustomTime("<s:property value="dataSource.selectTimeType"/>", false);
	var data = dashboardSettingPane.initTree();
	//hm.da.createTreeWidget("-1", "da_div_group_topy_tree1", data[0]);
	hm.da.createTreeWidget("-2", "da_div_group_filter_tree1", data[0]);
	hm.da.createTreeWidget("-2", "da_div_group_filter_userpro_tree1", data[1]);
	dashboardSettingPane.initWidget();
	dashboardSettingPane.resizeWidth(width);
	YAHOO.util.Event.addListener(window, 'resize', function(){
		dashboardSettingPane.resizeLayout();
	});
	var wmsg='<s:property value="vhmDeviceCount"/>';
	if (wmsg!=null && wmsg!='') {
		setTimeout(function () {showNoDeviceWarnDialog(wmsg);}, 2000);
	} else {
		wmsg='<s:property value="onloadErrorMessage"/>';
		if (wmsg!=null && wmsg!='') {
			setTimeout(function () {showWarnDialog(wmsg, "Warning");}, 2000);
		}
	}
	createButtonMenuItem();
	createApplicationCustomMenu();
	
	var $lastUpdateContent1 = $("#lastUpdateTime1a .last-update-content");
	var $lastUpdateContent2 = $("#lastUpdateTime2a .last-update-content");
	$("#lastUpdateTime1aImg").hover(function(e) {
		$lastUpdateContent1.css("display", "inline-block");
	}, function(e) {
		$lastUpdateContent1.css("display", "none");
	});
	$("#lastUpdateTime2aImg").hover(function(e) {
		$lastUpdateContent2.css("display", "inline-block");
	}, function(e) {
		$lastUpdateContent2.css("display", "none");
	});
	
	resetTableMouseAction();
}

function resetTableMouseAction(){
	$(".dac_table_tab_class").each(function(idx, item) {
		if ($(item).parent().hasClass('dac_td_tab_link_sel') && customMode==false && writePermission) {
			$(item).find(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/aclose.png");
			$(item).find(".dac_img_tab_close").attr("title", "Remove");
		} else {
			$(item).find(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/uclose.png");
			$(item).find(".dac_img_tab_close").attr("title", "");
		}
		$(item).hover(
			function(e){
				if (!$(item).parent().hasClass('dac_td_tab_link_sel') && customMode==false && writePermission){
					this.style.backgroundColor="#F5F5F5";
					if ($(item).find(".dac_img_tab_close").length>0) {
						$(item).find(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/aclose.png");
						$(item).find(".dac_img_tab_close").attr("title", "Remove");
					}
					//if ($(item).find(".dac_td_tab_center_remove").length>0) {
					//	$(item).find(".dac_td_tab_center_remove").show();
					//}
				}
			},
			function(e){
				if (!$(item).parent().hasClass('dac_td_tab_link_sel') && customMode==false && writePermission){
					this.style.backgroundColor="#FFFFFF";
					if ($(item).find(".dac_img_tab_close").length>0) {
						$(item).find(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/uclose.png");
						$(item).find(".dac_img_tab_close").attr("title", "");
					}
					//if ($(item).find(".dac_td_tab_center_remove").length>0) {
					//	$(item).find(".dac_td_tab_center_remove").hide();
					//}
				}
			}
		);
	});
	
	if (customMode || !writePermission) {
		$(".dac_img_tab_new").attr("title", "");
		$(".dac_img_tab_new").attr("src", imagesBaseUrl + "/dashboard/unew.png");
	}
}

function showNoDeviceWarnDialog(wmsg){
	var mybuttons = [ { text:"OK", handler: function(){
		this.hide();
		if (Get("dontShowAgain").checked) {
			var url = "dashboard.action?operation=dontShowMessageAgain&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url);
		}
	}}];
    var warningMsg = '<html><body>' + wmsg + '<br><br> <input type="checkbox" id="dontShowAgain" value="false"/>Do not show me this message again.'  + '</body></html>';
    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
    dlg.show();
}

var dashboardSettingPane = (function(){
	var initWidget = function(){
		widgetPanel.init();
	};

	var initTree = function(){
		return monitorPane.init();
	};

	var resizeLayout = function(){
		monitorPane.resizeLayout();
		widgetPanel.resizeLayout();
	};

	var resizeWidth = function(width){
		monitorPane.resizeWidth(width);
		widgetPanel.resizeWidth(width);
	};

	return {
		initWidget: initWidget,
		initTree: initTree,
		resizeLayout: resizeLayout,
		resizeWidth: resizeWidth
	};
})();

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
		var currentWidth = parseInt(YAHOO.util.Dom.getStyle("left_view_pane_wrap", "width")), newWidth;
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
			YAHOO.util.Dom.setStyle("left_view_pane_wrap", "width", newWidth+"px");
			dashboardSettingPane.resizeWidth(newWidth);
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp) {
				dashboardTmp.resizeDa();
			}
		}
	}
}

function createTabTrWhenOnLoad(){
	var arrays = new Array();
	var index = 0;
	<s:iterator value="tabItemArray">
		arrays[index] = new Array();
		arrays[index][0] = "<s:property value="%{key}"/>";
		arrays[index][1] = "<s:property value="%{value}" escapeHtml="false"/>";
		<s:if test="%{toopTip!=null}">
			arrays[index][2] = '1';
		</s:if>
		<s:else>
			arrays[index][2] = null;
		</s:else>
		index++;
	</s:iterator>
	hm.da.createTabTR(arrays);

	<s:iterator value="tabItemHighInterval">
		if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + '<s:property value="%{key}"/>']){
			clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + '<s:property value="%{key}"/>']);
		}
		
		var tmFction = "hm.da.dispalyHighIntervalWarn(\"" + "<s:property value="%{toopTip}"/>" + "\",\"" + '<s:property value="%{key}"/>' +"\")";
		//debug(tmFction);
		highApTimeOutArray["warningHighIntervalAPTimeoutId" + '<s:property value="%{key}"/>'] = setTimeout(tmFction, <s:property value="%{value}"/>);
		//eval("var warningHighIntervalAPTimeoutId" + '<s:property value="%{key}"/>' +"=" + setTimeout(tmFction, <s:property value="%{value}"/>));
		//debug(tmFction);
	</s:iterator>

}

function traceDiffResult(e){
	if (customMode && !ignoreCheck) {
		if(e){
			e.returnValue = tips;
		}
		return tips;
	}
	return;
}

function sizeLeftDaGroup() {
	var displayTree =false;
	var tdd
	if(Get("da_tr_viewMode").style.display=='none') {
		tdd = document.getElementById('da_td_blank_detail3');
	} else {
		tdd = document.getElementById('da_td_blank_detail');
		displayTree=true;
	}
	if (tdd == null) {
		return;
	}
	var vpHeight = YAHOO.util.Dom.getViewportHeight();

	var extra2 = vpHeight - YAHOO.util.Dom.getY(tdd) + YAHOO.util.Dom.getDocumentScrollTop()-5;
	if (extra2 > 0) {
		tdd.height = extra2;
	} else {
		tdd.height = 1;
	}
	if(displayTree) {
		Get("da_td_blank_detail3").height=1;
		Get("da_td_blank_detail2").height=tdd.height;
	} else {
		Get("da_td_blank_detail").height=1;
		Get("da_td_blank_detail2").height=1;
	}
}

function createApplicationCustomMenu()
{
	new Aerohive.menuObj.ULMenu({
		btnPath: '#da_btlastcustom_app',
		menuRendered: true
	});
}
function applicationCustomMenuItemClick(text) {
	if (text == '<s:text name="hm.dashboard.config.customize.last.caleweek"/>') {
		changeCustomTime(6, true);
	} else if (text == '<s:text name="hm.dashboard.config.customize.last.24hours"/>') {
		changeCustomTime(2, true);
	} else if (text == '<s:text name="hm.dashboard.config.customize.last.7days"/>') {
		changeCustomTime(3, true);
	} else if (text == '<s:text name="hm.dashboard.config.customize.customer"/>') {
		openCustomTimePanel();
	}
}

function createButtonMenuItem()
{
	var btnPath = '#reportButtonItemMenu';
	var ulMenuDaObj = new Aerohive.menuObj.ULMenu({
		btnPath: btnPath
	});
	var menuItems = [];
	<s:if test="%{writePermission}">
		menuItems.push({
			icon: 'bticon-saveasreport',
			text: '<s:text name="hm.dashboard.edit.saveasreport"/>',
			click: showNewReportPanel
		});
	</s:if>
	menuItems.push({
		icon: 'bticon-export',
		text: '<s:text name="config.usb.modem.button.export"/>',
		click: exportCurrentDashboard
	});
	menuItems.push({
		icon: 'bticon-email',
		text: '<s:text name="admin.interface.ha.email"/>',
		click: openEmailSendOverlayOverlay
	});
	var $gContainer = $(btnPath).parents(".dropdown");
	var $menuContainer = $("<ul></ul>").addClass("dropdown-menu");
	_.each(menuItems, function(menuItem) {
		var $aTmp = $("<a></a>").attr({
			href: 'javascript: void(0);'
		}).html('<i class="' + menuItem.icon + '"></i>' + menuItem.text);
		var $liTmp = $("<li></li>").click(function(e){
			menuItem.click();
		});
		$liTmp.append($aTmp);
		$menuContainer.append($liTmp);
	});
	$gContainer.append($menuContainer);
	ulMenuDaObj.addAdditionalEventsToMenu();
}

function buttonMenuItemClick(p_sType, p_aArguments, objectkey) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	console.log(objectkey);
	if (objectkey==null) {
		return;
	}

	if (objectkey == 'saveasreport') {
		showNewReportPanel();
	} else if (objectkey == 'exportreport') {
		exportCurrentDashboard();
	} else if (objectkey == 'emailreport') {
		openEmailSendOverlayOverlay();
	}
}

function customLabelClick() {
	//Get("da_tr_custom_label").style.display="none";
	Get("da_tr_custom_time").style.display="none";
	Get("da_tr_custom_save").style.display="";
	setCustomMode(true);
	hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_WRITE );
	hm.da.disabledRemoveNewTabButton();
}

function customCancelClick() {
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		if (dashboardTmp.isInChangeMode()) {
			showCancelOrSaveWin();
			return;
		}
	}
	customCancelClickCancel();
}
function customCancelClickCancel() {
	//Get("da_tr_custom_label").style.display="";
	Get("da_tr_custom_time").style.display="";
	Get("da_tr_custom_save").style.display="none";
	hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_READ );
	hm.da.enabledRemoveNewTabButton();
	cleanThingsAfterCancelOnPage();
	setCustomMode(false);
	resetTableMouseAction();
	sizeLeftDaGroup();
}

function newModeCancelClick() {
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		if (dashboardTmp.isInChangeMode()) {
			showCancelOrSaveWin();
			return;
		}
	}
	newModeCancelClickCancel();
}
function newModeCancelClickCancel() {
	if(isDash()) {
		hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_READ );
	} else {
		hm.da.switchMode(hm.da.MODE_DRILL, hm.da.C_NONE );
	}
	hm.da.enabledRemoveNewTabButton();
	Get("da_tr_tab").removeChild(Get("da_td_tab_new_temp"));
	$("#da_tr_tab_td" + currentDashBoardId).removeClass("dac_td_tab_link");
	$("#da_tr_tab_td" + currentDashBoardId).addClass("dac_td_tab_link_sel");
	cloneTabFlg=false;
	cleanThingsAfterCancelOnPage();
	customMode = false;
	resetTableMouseAction();
	sizeLeftDaGroup();
}

function newModeSaveClick(operation) {
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp == null) {
		return false;
	}
	var selected = dashboardTmp.getAllReportIds();
	if(null == selected || selected.length == 0){
		showWarnDialog("<s:text name="error.pleaseAddItems" />");
		return false;
	}
	
	hm.da.showWaitingPanel();
	var layouts = prepareDaLayoutsConfig(dashboardTmp);
	var bgRollupTmp = Get(formName + '_bgRollup2').checked;
	var reqArgs = {
			'operation': 'newTab',
			'columnsConfig': layouts.cols,
			'columnCharts': layouts.charts,
			'tabName': 'My Perspective',
			'bgRollup2': bgRollupTmp,
			'ignore': new Date().getTime()
	};
	if(cloneTabFlg){
		reqArgs.cloneTabId = currentDashBoardId;
	}
	$.post('dashboard.action',
			$.param(reqArgs, true),
			function(data, textStatus) {
				hm.da.succSaveNewTab(data);
			},
			'json');
}

function changeCustomTime(index, refresh){
	if (!refresh) {
		YAHOO.util.Dom.removeClass(Get("da_btlasthour"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastday"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastweek"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcustom"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlasthour_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcaleday_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlast8hour_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcustom_app"), 'btTDSel');
		
		Get("da_tr_custom_time_detail").style.display="none";
		if (applicationPerspectiveId==currentDashBoardId) {
			if (index ==1) {
				YAHOO.util.Dom.addClass(Get("da_btlasthour_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else if (index ==5) {
				YAHOO.util.Dom.addClass(Get("da_btlastcaleday_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else if (index ==7) {
				YAHOO.util.Dom.addClass(Get("da_btlast8hour_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else {
				YAHOO.util.Dom.addClass(Get("da_btlastcustom_app"), 'btTDSel');
				if (index==2) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.24hours"/>';
				} else if (index==3) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.7days"/>';
				} else if (index==6) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.caleweek"/>';
				} else {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
				}
				//Get("da_tr_custom_time_detail").style.display="";
			}
		} else {
			if (index ==1) {
				YAHOO.util.Dom.addClass(Get("da_btlasthour"), 'btTDSel');
			} else if (index ==2) {
				YAHOO.util.Dom.addClass(Get("da_btlastday"), 'btTDSel');
			} else if (index ==3) {
				YAHOO.util.Dom.addClass(Get("da_btlastweek"), 'btTDSel');
			} else if (index ==4) {
				YAHOO.util.Dom.addClass(Get("da_btlastcustom"), 'btTDSel');
				//Get("da_tr_custom_time_detail").style.display="";
			}
		}
		
	}
	if (refresh) {
		if(applicationPerspectiveId == currentDashBoardId) {
			var checkApNumberForAppSucc=function(o){
				try {
					eval("var data = " + o.responseText);
				}catch(e){
					showWarnDialog("Change Time Session timeout.", "Error");
					return false;
				}

				if (data.t) {
					if (data.w) {
						var cancelBtn = function(){
					        this.hide();
					    };
					    var mybuttons = [ { text:"Continue", handler: function(){this.hide();changeCustomTimeTypeReal(index);} },
					                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
					    var warningMsg = "<html><body>" + data.w+ "</body></html>";
					    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
					    dlg.show();
					} else {
						changeCustomTimeTypeReal(index);
					}
				} else {
					showWarnDialog(data.m, "Error");
				}
			}

			var url = "dashboard.action?operation=checkApNumberForApp&timeType=" + index +"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : checkApNumberForAppSucc, timeout: 60000});
		} else {
			changeCustomTimeTypeReal(index);
		}
	}
}

function changeCustomTimeTypeReal(index) {
	var startDateTmp = "1970-01-01",
	startHourTmp = 0,
	endDateTmp = "1970-01-01",
	endHourTmp = 0,
	enableTimeLocalTmp = false;
	if (index == 4) {
		startDateTmp = Get("da_cu_startTime").value;
		startHourTmp = Get("da_cu_startHour").value;
		endDateTmp = Get("da_cu_endTime").value;
		endHourTmp = Get("da_cu_endHour").value;
		enableTimeLocalTmp = Get("enableTimeLocal").checked;
	}
	
	var url = "dashboard.action?operation=changeCustomTimeType&timeType=" + index +"&ignore="+new Date().getTime();
	if (index==4) {
		url = url + "&startTime=" + Get("da_cu_startTime").value + "&startHour=" + Get("da_cu_startHour").value
		+ "&endTime=" + Get("da_cu_endTime").value + "&endHour=" + Get("da_cu_endHour").value + "&enableTimeLocal=" + Get("enableTimeLocal").checked;
	}
	hm.da.showWaitingPanel();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,
			{success : function(o) {
				succChangeCustomTime(o, index, {
									'startDate': startDateTmp,
									'startHour': startHourTmp,
									'endDate': endDateTmp,
									'endHour': endHourTmp,
									'enableTimeLocal': enableTimeLocalTmp
								});
			}, timeout: 60000});
}

var succChangeCustomTime= function(o, typeArg, options) {
	try {
		eval("var data = " + o.responseText);
	}catch(e){
		hm.da.hideWaitingPanel();
		showWarnDialog("Change custom time Error. Session timeout.", "Error");
		return;
	}
	if(data.t) {
		YAHOO.util.Dom.removeClass(Get("da_btlasthour"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastday"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastweek"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcustom"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlasthour_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcaleday_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlast8hour_app"), 'btTDSel');
		YAHOO.util.Dom.removeClass(Get("da_btlastcustom_app"), 'btTDSel');
		
		Get("da_tr_custom_time_detail").style.display="none";
		if (applicationPerspectiveId==currentDashBoardId) {
			if (typeArg ==1) {
				YAHOO.util.Dom.addClass(Get("da_btlasthour_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else if (typeArg ==5) {
				YAHOO.util.Dom.addClass(Get("da_btlastcaleday_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else if (typeArg ==7) {
				YAHOO.util.Dom.addClass(Get("da_btlast8hour_app"), 'btTDSel');
				Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
			} else {
				YAHOO.util.Dom.addClass(Get("da_btlastcustom_app"), 'btTDSel');
				if (typeArg==2) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.24hours"/>';
				} else if (typeArg==3) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.7days"/>';
				} else if (typeArg==6) {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.last.caleweek"/>';
				} else {
					Get("span_da_btlastcustom_app").innerHTML='<s:text name="hm.dashboard.config.customize.customer"/>';
				}
				//Get("da_tr_custom_time_detail").style.display="";
			}
		} else {
			if (typeArg ==1) {
				YAHOO.util.Dom.addClass(Get("da_btlasthour"), 'btTDSel');
			} else if (typeArg ==2) {
				YAHOO.util.Dom.addClass(Get("da_btlastday"), 'btTDSel');
			} else if (typeArg ==3) {
				YAHOO.util.Dom.addClass(Get("da_btlastweek"), 'btTDSel');
			} else if (typeArg ==4) {
				YAHOO.util.Dom.addClass(Get("da_btlastcustom"), 'btTDSel');
				//Get("da_tr_custom_time_detail").style.display="";
			}
		}

		if(data.v) {
			Get("da_span_custom_time_detail").innerHTML=data.v;
		}
		hm.da.resetHighIntervalTimeOut(data);
	} else {
		hm.util.displayJsonErrorNote(data.m);
	}
	hm.da.hideWaitingPanel();
	refreshSelectedWidgets(typeArg, function(chart) {
		if (!chart) {
			return;
		}
		var tmpTopoConfig = this.getCurReportWidgetConfig(chart);
		if (tmpTopoConfig) {
			if (tmpTopoConfig.hasOwnProperty('checked') && !tmpTopoConfig.checked) {
				this.setTmpReportConfig(chart.container,
						{
							'timeType': typeArg,
							'startDate': options.startDateTmp,
							'startHour': options.startHourTmp,
							'endDate': options.endDateTmp,
							'endHour': options.endHourTmp,
							'enableTimeLocal': options.enableTimeLocalTmp
						}, true);
			}
		}
		if (typeArg == 1) {
			chart.intervalStrategy.reset();
		} else {
			chart.intervalStrategy.stop();
		}
	});
}

var refreshSelectedWidgets = function(typeArg, callback) {
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		dashboardTmp.timeType = typeArg;
		dashboardTmp.refreshSelectedCharts({
			periodType: typeArg
		}, callback);
	}
};

function submitAction(operation) {
	showProcessing();
	document.forms[formName].operation.value = operation;
 	beforeSubmitAction(document.forms[formName]);
	document.forms[formName].submit();
}

function doContinueOper() {
     submitAction(thisOperation);
}

function exportCurrentDashboard() {
	 Today = new Date();
	    var NowHour = Today.getHours();
	    var NowMinute = Today.getMinutes();
	    var NowSecond = Today.getSeconds();
	    var waittime = (NowHour*3600)+(NowMinute*60)+NowSecond;
	    if((waittime-document.forms[formName].waittime.value)>3){
	    	document.forms[formName].waittime.value=waittime;
	    }
	    else{
	        return false;
	    }
	var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		if (dashboardTmp.isBlank()) {
			showWarnDialog('<s:text name="hm.dashboard.blank.not.support"><s:param><s:text name="config.usb.modem.button.export"/></s:param></s:text>', "Warning");
			return;
		}
		var layouts = dashboardTmp.getColumnLayout();
		var oFormContainer = $("div#otherFormContainer")[0];
		oFormContainer.innerHTML = "";

		var formTmp = document.createElement('form');
		formTmp.method = 'post';
		formTmp.action = 'dashboard.action';
		formTmp.enctype = 'multipart/form-data';
		formTmp.style.display = 'none';
		oFormContainer.appendChild(formTmp);
		var elOperation = document.createElement('input');
		elOperation.type = 'hidden';
		elOperation.name = 'operation';
		elOperation.value = 'export';
		formTmp.appendChild(elOperation);

		var datas = [],
			results = [],
			exceptions = [],
			columns = [],
			orders = [],
			wIds = [],
			samples = [],
			xaxises = [];
		var icOrder = 0;
		_.each(
				_.sortBy(
						_.values(layouts), function(obj){
							return obj.order;
						}
				),
				function(obj) {
					icOrder = 0;
					_.each(obj.charts, function(chartTmp) {
						var lstData = chartTmp.lastData;
						lstData = lstData?lstData[0]:{};
						datas.push(lstData.data?lstData.data:"");
						results.push(lstData.result);
						exceptions.push(lstData.exception?lstData.exception:null);
						columns.push(obj.order);
						orders.push(icOrder++);
						wIds.push(chartTmp.wId);
						xaxises.push(chartTmp.axis);
						samples.push(chartTmp.sample);
					});
				}
		);
		hm.util.createFormArrayEls(formTmp, "rd_data", datas);
		hm.util.createFormArrayEls(formTmp, "rd_result", results);
		hm.util.createFormArrayEls(formTmp, "rd_exception", exceptions);
		hm.util.createFormArrayEls(formTmp, "rd_column", columns);
		hm.util.createFormArrayEls(formTmp, "rd_order", orders);
		hm.util.createFormArrayEls(formTmp, "rd_wIds", wIds);
		hm.util.createFormArrayEls(formTmp, "rd_xaxis", xaxises);
		hm.util.createFormArrayEls(formTmp, "rd_samples", samples);

		formTmp.submit();
	}
}
</script>


<div id="content"><s:form action="dashboard">
	<s:hidden name="operation" />
	<s:hidden name="deviceMonitorType" />
	<s:hidden name="monitorEl" />
	<input type="hidden" name="waittime" value="0"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="dacAllPanel">
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
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
					<tr>
						<td class="menu_bg"></td>
						<td class="menu_bg" width="100%">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td >
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr id="da_tr_tab">
											</tr>
										</table>
									</td>
									<td align="right" style="border-bottom:1px solid #DDDDDD">
										<div class="btn-group" id="menuButtonForNormalDiv">
										<s:if test="%{writePermission}">
											<button class="btn" type="button" title="<s:text name="hm.dashboard.config.customize.link"/>" onclick="customLabelClick();"><i class="bticon-edit"></i></button>
										</s:if>
											<div class="dropdown clearfix ulMenuList">
												<button class="btn" type="button" id="reportButtonItemMenu" title="<s:text name="config.ssid.advanceOption"/>"><i class="bticon-settings"></i><span class="caret"></span></button>
											</div>
										</div>
										
										<div class="btn-group" id="menuButtonForDrillDownDiv" style="display:none;">
											<button class="btn" type="button" title="<s:text name="topology.menu.operation.export2pdf"/>" onclick="exportCurrentDashboard();"><i class="bticon-export"></i></button>
											<button class="btn" type="button" title="<s:text name="admin.interface.ha.email"/>" onclick="openEmailSendOverlayOverlay();"><i class="bticon-email"></i></button>
										</div>
									</td>
								</tr>
								<tr>
									<td height="15px"/>
								</tr>
								<tr>
									<td colspan="2">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td valign="top">
													<div id="left_view_pane_wrap" style="width: 200px; padding-right: 20px; overflow: hidden;">
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td id="monitor_view_pane_wrap" valign="top">
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr><td><tiles:insertDefinition name="reportMonitorView" /></td></tr>
																	<tr><td><tiles:insertDefinition name="statusView" /></td></tr>
																</table>
															</td>
															<td id="config_view_pane_wrap" valign="top" style="display:none;">
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr><td><tiles:insertDefinition name="reportConfigView" /></td></tr>
																</table>
															</td>
														</tr>
													</table>
													</div>
												</td>
												<td id="splitter" style="border-left: 2px solid #ddd; border-right: 2px solid #ddd; cursor: e-resize; width: 0;"></td>
												<td valign="top" width="100%">
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr id="da_tr_viewMode">
															<td>
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td></td>
																		<td valign="top">
																			<table width="100%" border="0" cellspacing="0" cellpadding="0">
																				<tr id="da_tr_custom_time">
																					<td align="left" style="display:<s:property value="displayCommonTimeStyle"/>; padding-left: 14px;" id="common_tab_time_td">
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td nowrap="nowrap">
																									<s:text name="hm.dashboard.config.customize.timeduration"/>
																								</td>
																								<td>
																									<div class="btn-group">
																										<button class="btn" type="button" id="da_btlasthour" title="<s:text name="hm.dashboard.config.customize.last.hour"/>" onclick="changeCustomTime(1, true);"><s:text name="hm.dashboard.config.customize.last.1hour"/></button>
																										<button class="btn" type="button" id="da_btlastday" title="<s:text name="hm.dashboard.config.customize.last.c24hour"/>" onclick="changeCustomTime(2, true);"><s:text name="hm.dashboard.config.customize.last.24hours"/></button>
																										<button class="btn" type="button" id="da_btlastweek" title="<s:text name="hm.dashboard.config.customize.last.c7day"/>" onclick="changeCustomTime(3, true);"><s:text name="hm.dashboard.config.customize.last.7days"/></button>
																										<button class="btn" type="button" id="da_btlastcustom" title="<s:text name="hm.dashboard.config.customize.customer"/>" onclick="openCustomTimePanel();"><s:text name="hm.dashboard.config.customize.customer"/></button>
																									</div>
																								</td>
																								<%-- 
																								<td class="npcButton" id="da_btlasthour" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="changeCustomTime(1, true);" title='<s:text name="hm.dashboard.config.customize.last.hour"/>'><span><s:text name="hm.dashboard.config.customize.last.hour"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlastday" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent"  onclick="changeCustomTime(2, true);" title='<s:text name="hm.dashboard.config.customize.last.day"/>'><span><s:text name="hm.dashboard.config.customize.last.day"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlastweek" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="changeCustomTime(3, true);" title='<s:text name="hm.dashboard.config.customize.last.week"/>'><span><s:text name="hm.dashboard.config.customize.last.week"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlastcustom" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="openCustomTimePanel();" title='<s:text name="hm.dashboard.config.customize.customer"/>'><span><s:text name="hm.dashboard.config.customize.customer"/></span></a></td>
																								--%>
																							</tr>
																						</table>
																					</td>
																					<td align="left" nowrap="nowrap" style="display:<s:property value="displayAppTimeStyle"/>; padding-left: 14px;" id="application_tab_time_td" >
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td nowrap="nowrap">
																									<s:text name="hm.dashboard.config.customize.timeduration"/>
																								</td>
																								<td>
																									<div class="btn-group">
																										<button class="btn" type="button" id="da_btlasthour_app" title="<s:text name="hm.dashboard.config.customize.last.hour"/>" onclick="changeCustomTime(1, true);"><s:text name="hm.dashboard.config.customize.last.1hour"/></button>
																										<button class="btn" type="button" id="da_btlast8hour_app" title="<s:text name="hm.dashboard.config.customize.last.c8hour"/>" onclick="changeCustomTime(7, true);"><s:text name="hm.dashboard.config.customize.last.8hours"/></button>
																										<button class="btn" type="button" id="da_btlastcaleday_app" title="<s:text name="hm.dashboard.config.customize.last.caleday"/>" onclick="changeCustomTime(5, true);"><s:text name="hm.dashboard.config.customize.last.1day"/></button>
																										<div class="dropdown clearfix ulMenuList">
																											<button class="btn" type="button" id="da_btlastcustom_app" title="<s:text name="hm.dashboard.config.customize.customer"/>"><span id="span_da_btlastcustom_app"><s:text name="hm.dashboard.config.customize.customer"/></span><span class="caret"></span></button>
																											<ul class="dropdown-menu">
																												<li><a href="javascript: void(0);" 
																														onclick="javascript: applicationCustomMenuItemClick('<s:text name="hm.dashboard.config.customize.last.24hours"/>');"><s:text name="hm.dashboard.config.customize.last.24hours"/></a></li>
																												<li><a href="javascript: void(0);" 
																														onclick="javascript: applicationCustomMenuItemClick('<s:text name="hm.dashboard.config.customize.last.7days"/>');"><s:text name="hm.dashboard.config.customize.last.7days"/></a></li>
																												<li><a href="javascript: void(0);"
																														onclick="javascript: applicationCustomMenuItemClick('<s:text name="hm.dashboard.config.customize.customer"/>');"><s:text name="hm.dashboard.config.customize.customer"/></a></li>
																											</ul>
																										</div>
																									</div>
																								</td>
																							<%-- 
																								<td class="npcButton" id="da_btlasthour_app" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="changeCustomTime(1, true);" title='<s:text name="hm.dashboard.config.customize.last.hour"/>'><span><s:text name="hm.dashboard.config.customize.last.hour"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlast8hour_app" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="changeCustomTime(7, true);" title='<s:text name="hm.dashboard.config.customize.last.c8hour"/>'><span><s:text name="hm.dashboard.config.customize.last.c8hour"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlastcaleday_app" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent"  onclick="changeCustomTime(5, true);" title='<s:text name="hm.dashboard.config.customize.last.caleday"/>'><span><s:text name="hm.dashboard.config.customize.last.caleday"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" id="da_btlastcustom_app" nowrap="nowrap"><a href="javascript:void(0);" class="btCurrent" onclick="showApplicationCustomMenu();" title='<s:text name="hm.dashboard.config.customize.customer"/>'><span id="span_da_btlastcustom_app"><s:text name="hm.dashboard.config.customize.customer"/></span>
																								
																									<img src="<s:url value="/images/dashboard/topology-down.png" includeParams="none"/>" width="12" height="12" alt="" class="dblk" />
																								</a></td>
																							--%>
																							</tr>
																						</table>
																					</td>

																					<td>
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td nowrap="nowrap">
																									<div class="last-update-container">
																									<a id="lastUpdateTime1aImg" style="display:<s:property value="displayCommonTimeStyle"/>;" class="marginBtn" href="javascript:void(0);" onclick="javascript:refreshDashboard();"><img class="dinl"
																										src="<s:url value="/images/dashboard/refresh.png" />"
																										width="16" height="16" alt="Refresh" style="vertical-align: middle;" title="Refresh" /></a>
																									<div id="lastUpdateTime1a" style="color: #888888;display:inline-block;">&nbsp;<div class="last-update-content">Last Update: <span id="lastUpdateTime1"></span></div></div>
																									</div>
																								</td>
																							</tr>
																						</table>
																					</td>
																					<td width="99999px">&nbsp;</td>
																					<td style="padding-right: 20px" align="right" >
																						<div id="time_period_common" class="global_report_period_str">&nbsp;</div>
																					<%-- 
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr id="da_tr_custom_label">
																								<s:if test="%{writePermission}">
																									<td class="npcButton" ><a href="javascript:void(0);" onclick="customLabelClick();"  class="btCurrent"  title='<s:text name="hm.dashboard.config.customize.link"/>'><span><s:text name="config.networkpolicy.button.edit"/></span></a></td>
																									<td width="16px"/>
																								</s:if>
																								<s:if test="%{writePermission}">
																									<td class="npcButton" nowrap="nowrap"><a href="javascript:void(0);" onclick="showNewReportPanel();" class="btCurrent"  title='<s:text name="hm.dashboard.edit.saveasreport"/>'><span><s:text name="hm.dashboard.edit.saveasreport"/></span></a></td>
																									<td width="16px"/>
																								</s:if>
																								<input type="hidden" name="waittime" value="0"/>
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="exportCurrentDashboard();" class="btCurrent"  title='<s:text name="topology.menu.operation.export2pdf"/>'><span><s:text name="config.usb.modem.button.export"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="openEmailSendOverlayOverlay();" class="btCurrent"  title='<s:text name="admin.interface.ha.email"/>'><span><s:text name="admin.interface.ha.email"/></span></a></td>
																							</tr>
																						</table>
																						--%>
																					</td>
																				</tr>
																				<tr id="da_tr_custom_time_detail" style="display: none;">
																					<td align="left" colspan="2" style="padding-left: 20px"><font color="#888888">
																						<span id="da_span_custom_time_detail"><s:property value="dataSource.currentDashCustomTimeString"/></span></font>
																					</td>
																				</tr>
																				<tr id="da_tr_custom_save" style="display:none;">
																					<td colspan="3" class="customHtmlAItemDisable" style="padding-left:10px; font-size: 16px;"><s:text name="hm.dashboard.edit.note"/> </td>
																					<td align="right" width="150px">
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td style="padding-right: 30px; display:none;"><s:checkbox name="bgRollup"></s:checkbox><s:text name="hm.dashboard.background.roolup"/></td>
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="customCancelClick();" id="da_bt_custom_cancel" class="btCurrent"  title="Cancel"><span><s:text name="common.button.cancel"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" ><a href="javascript:void(0);" id="da_bt_custom_save" class="btCurrent"  title="Save"><span><s:text name="common.button.save"/></span></a></td>
																							</tr>
																						</table>
																					</td>
																				</tr>
																				<tr>
																					<td colspan="4">
																						<div id="da_div_container" class="chartsContainer">
																						</div>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td id="da_td_blank_detail" height="1"></td>
																		<td id="da_td_blank_detail2" height="1"></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr id="da_tr_newMode" style="display: none;">
															<td>
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td></td>
																		<td valign="top">
																			<table width="100%" border="0" cellspacing="0" cellpadding="0">

																				<tr>
																					<td class="customHtmlAItemDisable" style="padding-left:10px; font-size: 16px;"><s:text name="hm.dashboard.edit.note2"/> </td>
																					<td align="right" width="150px">
																						<table  border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td style="padding-right: 30px; display:none;"><s:checkbox name="bgRollup2"></s:checkbox><s:text name="hm.dashboard.background.roolup"/></td>
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="newModeCancelClick();" id="da_bt_newMode_cancel" class="btCurrent"  title="Cancel"><span><s:text name="common.button.cancel"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" ><a href="javascript:void(0);" id="da_bt_newMode_save" onclick="newModeSaveClick('newTab');"  class="btCurrent"   title="Save"><span><s:text name="common.button.save"/></span></a></td>
																								</tr>
																						</table>
																					</td>

																				</tr>
																				<tr>
																					<td colspan="2">
																						<div id="da_newda_div_container" class="chartsContainer"></div>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr style="display:none;" id="da_tr_drillMode">
															<td valign="top">
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td>
																			<table width="100%" border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td valign="top" style="padding-left: 6px;">
																						<table width="100%" border="0" cellspacing="0" cellpadding="0">
																							<tr>
																								<td>
																									<div class="last-update-container">
																									<a id="lastUpdateTime2aImg" class="marginBtn" href="javascript:void(0);" onclick="javascript:refreshDashboard();"><img class="dinl"
																													src="<s:url value="/images/dashboard/refresh.png" />"
																													width="16" height="16" alt="Refresh" title="Refresh" style="vertical-align: middle;"/></a>
																									<div id="lastUpdateTime2a" style="color: #888888;display:inline-block;">&nbsp;<div class="last-update-content">Last Update: <span id="lastUpdateTime2"></span></div></div>
																									</div>
																								</td>
																							</tr>
																						</table>
																					</td>

																					<td style="padding-right: 20px" align="right">
																					<div id="time_period_drillmode" class="global_report_period_str">&nbsp;</div>
																					<%-- 
																						<table border="0" cellspacing="0" cellpadding="0">
																							<tr id="da_tr_drill_custom_label">
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="exportCurrentDashboard();" class="btCurrent"  title='<s:text name="topology.menu.operation.export2pdf"/>'><span><s:text name="config.usb.modem.button.export"/></span></a></td>
																								<td width="16px"/>
																								<td class="npcButton" ><a href="javascript:void(0);" onclick="openEmailSendOverlayOverlay();" class="btCurrent"  title='<s:text name="admin.interface.ha.email"/>'><span><s:text name="admin.interface.ha.email"/></span></a></td>
																							</tr>
																						</table>
																						--%>
																					</td>
																				</tr>
																				<tr>
																					<td colspan="2">
																						<div id="da_div_drill_container" class="chartsContainer">
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
									</td>
								</tr>
							</table>
						</td>
						<td class="menu_bg"></td>
					</tr>
					<tr style="display:none;">
						<td>
							<div id="widgetCopyDiv">
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
						</td>
					</tr>
					<tr>
						<td id="da_td_blank_detail3" height="1"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</s:form>
</div>
<div id="otherFormContainer" style="display:none;"></div>

<script>
var da_customtime_panel_detail = 'da_customtime_panel_detail';

YAHOO.util.Event.onDOMReady(init);
var da_customtime_panel = null;
var da_widget_group_panel = null;
var emailSendOverlay = null;
var customHtmlPanel=null;
function init() {
// create filter overlay
	var div = document.getElementById('da_customtime_panel');
	da_customtime_panel = new YAHOO.widget.Panel(div, {
		width:"350px",
		visible:false,
		fixedcenter:false,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	da_customtime_panel.render(document.body);
	div.style.display = "";

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

	var div3 = document.getElementById('emailPanel');
	emailSendOverlay = new YAHOO.widget.Panel(div3, {
		width:"530px",
		visible:false,
		fixedcenter:true,
		draggable:false,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	emailSendOverlay.render(document.body);
	div3.style.display = "";

	var div4 = document.getElementById('customHtmlPanel');
	customHtmlPanel = new YAHOO.widget.Panel(div4, {
		width:"730px",
		visible:false,
		fixedcenter:false,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	customHtmlPanel.render(document.body);
	div4.style.display = "";


}

function openEmailSendOverlayOverlay(){
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp || dashboardTmp.isBlank()) {
		showWarnDialog('<s:text name="hm.dashboard.blank.not.support"><s:param><s:text name="admin.interface.ha.email"/></s:param></s:text>', "Warning");
		return;
	}
	if(null != emailSendOverlay){
		emailSendOverlay.cfg.setProperty('visible', true);
	}
}

function hideEmailSendOverlayOverlay(){
	if(null != emailSendOverlay){
		emailSendOverlay.cfg.setProperty('visible', false);
	}
}

function validateEmailPanelInput(){
	if (document.getElementById("onceMail").value.trim()=="") {
		  hm.util.reportFieldError(document.getElementById("emailErrorDiv"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
		  return false;
	}

	var emails = document.getElementById("onceMail").value.split(";");
	for (var i=0;i<emails.length;i++) {
		if (i==emails.length-1 && emails[i].trim()=="") {
			break;
		}
		if (!hm.util.validateEmail(emails[i].trim())) {
			hm.util.reportFieldError(document.getElementById("emailErrorDiv"), '<s:text name="error.formatInvalid"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
			document.getElementById("onceMail").focus();
			return false;
		}
	}
	return true;
}
//send email event
function sendEmail()
{
	if(null != emailSendOverlay){
		emailSendOverlay.cfg.setProperty('visible', false);
	}
	var emailAddrs=$('#onceMail').val();
	var dashboardTmp = getCurrentDashboard();
    if (!dashboardTmp) return;
	dashboardTmp.notes.addProcessing( '<s:text name="info.da.email.send.note"/>');
	$.post('dashboard.action',
            {
               'operation': 'sendEmail',
               'emailAddrs': emailAddrs
            },
            succSendEmailCallback,
             'json');
}
var succSendEmailCallback = function(data, textStatus) {
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp) return;
	dashboardTmp.setChanged(false);
	if (data) {
		if (data.resultStatus === true) {
			dashboardTmp.notes.addInfo('<s:text name="info.da.email.send.successfully"/>');

		}
		else{
			if(data.returnMsg!=null)
				{
				dashboardTmp.notes.addError(data.returnMsg);
				}

		}
		return;
	}

	dashboardTmp.notes.addError('<s:text name="info.da.email.send.failed"/>');
};

var _widget_config_callback;
var saveWidgetGroupForTemporary = function() {
	this.setTmpReportConfig(this.getCurCtlBarSelectContainer(),
			{
				'obId': widgetSelectedFilterNode.data.id,
				'obType': widgetSelectedFilterNode.data.tp,
				'fobId': widgetSelectedFilterUpNode.data.id,
				'fobType': widgetSelectedFilterUpNode.data.tp
			}, true);
};
var saveWidgetGroupPersist = function() {
	var chart = this.getChartByContainer(this.getCurCtlBarSelectContainer());
	var tmpTopoConfig = this.getCurReportWidgetConfig(chart);
	var tmpObType=null;
	var tmpObId=null;
	var tmpFobType=null;
	var tmpFobId=null;

	if (tmpTopoConfig.hasOwnProperty('obType')) {
		tmpObType = tmpTopoConfig.obType;
	}
	if (tmpTopoConfig.hasOwnProperty('obId')) {
		tmpObId = tmpTopoConfig.obId;
	}
	if (tmpTopoConfig.hasOwnProperty('fobType')) {
		tmpFobType = tmpTopoConfig.fobType;
	}
	if (tmpTopoConfig.hasOwnProperty('fobId')) {
		tmpFobId = tmpTopoConfig.fobId;
	}
	$.post("dashboard.action",{
		'operation': 'saveTopoSelect',
		'widgetId': widgetIdArg,
		'treeId': tmpObId,
		'treeType': tmpObType,
		'filterObjectType': tmpFobType,
		'filterObjectId': tmpFobId
	}, function(data, textStatus) {
		succSaveTopologySelect(chart, data, textStatus);
	}, "json");
	return false;
};
var succSaveTopologySelect = function(dashboardTmp, chart, data, textStatus) {
	if (data && data.rs) {
		chart.currentDashboard.refreshCertainChart(chart);
		hideWidgetGroupPanel();
	} else {
		if (data.m) {
			showWarnDialog(data.m,"Error");
		}
	}
};
function openWidgetGroupPanel(configType){
	if(null != da_widget_group_panel){
		da_widget_group_panel.center();
		da_widget_group_panel.cfg.setProperty('visible', true);
		if (configType === "tmp") {
			_widget_config_callback = saveWidgetGroupForTemporary;
		} else {
			_widget_config_callback = saveWidgetGroupPersist;
		}
	}
}

function hideWidgetGroupPanel(){
	if(null != da_widget_group_panel){
		da_widget_group_panel.cfg.setProperty('visible', false);
	}
	_widget_config_callback = null;
}

function openCustomTimePanel(){
	if(null != da_customtime_panel){
		init_da_customtime_panel()
	}
}

function hideCustomtimePanel(){
	if(null != da_customtime_panel){
		da_customtime_panel.cfg.setProperty('visible', false);
	}
}

function getCurrentDateForCustomTime(deleteOneDay) {
	var date = new Date();
	if (deleteOneDay) {
		date.setTime(date.getTime()-3600000 * 24);
	}
	var fillNumber = function(value) {
		if (value < 10) {
			return '0'+value;
		}
		return value;
	};
	return fillNumber(date.getUTCFullYear()) + "-" + fillNumber(date.getUTCMonth()+1) + "-" + fillNumber(date.getUTCDate());
};
function init_da_customtime_panel(){
	var succfetchCustomTime=function(o){
		eval("var data = " + o.responseText);
		if (data.t) {
			Get("da_cu_startTime").value=data.s;
			Get("da_cu_startHour").value=data.sh;
			Get("da_cu_endTime").value=data.e;
			Get("da_cu_endHour").value=data.eh;
			Get("enableTimeLocal").checked=data.c;
		} else {
			var dateStr = getCurrentDateForCustomTime(true);
			Get("da_cu_startTime").value=dateStr;
			dateStr = getCurrentDateForCustomTime();
			Get("da_cu_endTime").value=dateStr;
			Get("da_cu_endHour").value=0;
			Get("da_cu_startHour").value=0;
			Get("enableTimeLocal").checked=false;

		}
		da_customtime_panel.center();
		da_customtime_panel.cfg.setProperty('visible', true);
	}
	var url = "dashboard.action?operation=fetchCustomTime&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succfetchCustomTime, timeout: 60000});

}

function checkCustomTimeInvalid(){
	if (document.getElementById("da_cu_startTime").value=="") {
	  hm.util.reportFieldError(document.getElementById("da_cu_startTime"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.config.custom.start.time"/></s:param></s:text>');
	  return false;
	}
	if (document.getElementById("da_cu_endTime").value=="") {
	  hm.util.reportFieldError(document.getElementById("da_cu_endTime"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.config.custom.end.time"/></s:param></s:text>');
	  return false;
	}

	if (Get("da_cu_startTime").value >Get("da_cu_endTime").value
		 || (Get("da_cu_startTime").value ==Get("da_cu_endTime").value
		 &&  parseInt(Get("da_cu_startHour").value) >= parseInt(Get("da_cu_endHour").value))) {
	     hm.util.reportFieldError(Get("da_cu_startTime"), '<s:text name="error.shourldLargerThan"><s:param><s:text name="hm.dashboard.config.custom.end.time"/></s:param><s:param><s:text name="hm.dashboard.config.custom.start.time"/></s:param></s:text>');
	     return false;
	}

	var succCheckConfigCustomTime=function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Check custom time session time out.", "Error");
			return false;
		}

		if (data.t) {
			if (data.e) {
			    hm.util.reportFieldError(Get("da_cu_startTime"), data.e);
			    return false;
			} else if (data.w) {
			    var mybuttons = [ { text:"View Incomplete Data", handler: function(){this.hide();hideCustomtimePanel(); changeCustomTime(4, true);} },
			                      { text:"Cancel", handler: function(){this.hide();}, isDefault:true} ];
			    var warningMsg = "<html><body>" + data.w +  "</body></html>";
			    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
			    dlg.show();
			} else {
	hideCustomtimePanel();
	changeCustomTime(4, true);
}
		}
	}

	var url = "dashboard.action?operation=checkCustomTimeRange&ignore="+new Date().getTime();
	url = url + "&startTime=" + Get("da_cu_startTime").value + "&startHour=" + Get("da_cu_startHour").value
	+ "&endTime=" + Get("da_cu_endTime").value + "&endHour=" + Get("da_cu_endHour").value + "&enableTimeLocal=" + Get("enableTimeLocal").checked;
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succCheckConfigCustomTime, timeout: 60000});

	//TODOfnr
}

function formatValue(value){
    var v=value;
    if(value.length==0)
       return v;
    if(parseInt(value)<=9)
       v="0"+value;
    return v;
}

YAHOO.util.Event.onDOMReady(function () {
    function onButtonClick() {

        /*
             Create an empty body element for the Overlay instance in order
             to reserve space to render the Calendar instance into.
        */
        oCalendarMenu.setBody("&#32;");
        oCalendarMenu.body.id = "calendarcontainer";
        // Render the Overlay instance into the Button's parent element
        oCalendarMenu.render(this.get("container"));
        // Align the Overlay to the Button instance
        oCalendarMenu.align();
        /*
             Create a Calendar instance and render it into the body
             element of the Overlay.
        */
        var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id);
        oCalendar.render();
        /*
            Subscribe to the Calendar instance's "changePage" event to
            keep the Overlay visible when either the previous or next page
            controls are clicked.
        */
        oCalendar.changePageEvent.subscribe(function () {
            window.setTimeout(function () {
                oCalendarMenu.show();
            }, 0);
        });

        /*
            Subscribe to the Calendar instance's "select" event to
            update the month, day, year form fields when the user
            selects a date.
        */
        oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
            var aDate;
            if (p_aArgs) {
                aDate = p_aArgs[0][0];
                var beginDate_doc = document.getElementById("da_cu_startTime");
                beginDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
            }
            oCalendarMenu.hide();

        });
        /*
             Unsubscribe from the "click" event so that this code is
             only executed once
        */
        this.unsubscribe("click", onButtonClick);

    };
    function onButtonClick1() {

        /*
             Create an empty body element for the Overlay instance in order
             to reserve space to render the Calendar instance into.
        */
        oCalendarMenu1.setBody("&#32;");
        oCalendarMenu1.body.id = "calendarcontainer1";
        // Render the Overlay instance into the Button's parent element
        oCalendarMenu1.render(this.get("container"));
        // Align the Overlay to the Button instance
        oCalendarMenu1.align();
        /*
             Create a Calendar instance and render it into the body
             element of the Overlay.
        */
        var oCalendar = new YAHOO.widget.Calendar("buttoncalendar1", oCalendarMenu1.body.id);
        oCalendar.render();
        /*
            Subscribe to the Calendar instance's "changePage" event to
            keep the Overlay visible when either the previous or next page
            controls are clicked.
        */
        oCalendar.changePageEvent.subscribe(function () {
            window.setTimeout(function () {
                oCalendarMenu1.show();
            }, 0);
        });

        /*
            Subscribe to the Calendar instance's "select" event to
            update the month, day, year form fields when the user
            selects a date.
        */
        oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
            var aDate;
            if (p_aArgs) {
                aDate = p_aArgs[0][0];
                var endDate_doc = document.getElementById("da_cu_endTime");
                endDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
            }
            oCalendarMenu1.hide();

        });
        /*
             Unsubscribe from the "click" event so that this code is
             only executed once
        */
        this.unsubscribe("click", onButtonClick1);

    };

    // Create an Overlay instance to house the Calendar instance
    var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

    // Create an Overlay instance to house the Calendar instance
    var oCalendarMenu1 = new YAHOO.widget.Overlay("calendarmenu");

    // Create a Button instance of type "menu"
    var startTimeButton = new YAHOO.widget.Button({
                                        type: "menu",
                                        id: "calendarpicker",
                                        label: "",
                                        menu: oCalendarMenu,
                                        container: "startdatefields" });

    // Create a Button instance of type "menu"
    var endTimeButton = new YAHOO.widget.Button({
                                        type: "menu",
                                        id: "calendarpicker1",
                                        label: "",
                                        menu: oCalendarMenu1,
                                        container: "enddatefields" });

    /*
        Add a "click" event listener that will render the Overlay, and
        instantiate the Calendar the first time the Button instance is
        clicked.
    */
    startTimeButton.on("click", onButtonClick);
    endTimeButton.on("click", onButtonClick1);


    hm.util.addYUICalendarButton({
    	bodyId: 'calendarcontainer2',
    	id: 'calendarpicker2',
    	container: 'startdatefields_for_chart',
    	afterSelect: function(aDate) {
    		var date_doc = document.getElementById("da_cu_startTime_for_chart");
    		date_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
    	}
    });

    hm.util.addYUICalendarButton({
    	bodyId: 'calendarcontainer3',
    	id: 'calendarpicker3',
    	container: 'enddatefields_for_chart',
    	afterSelect: function(aDate) {
    		var date_doc = document.getElementById("da_cu_endTime_for_chart");
    		date_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
    	}
    });
});

</script>
<div id="da_customtime_panel" style="display: none;">
	<div class="hd">
		<s:text name="hm.dashboard.config.custom.time"/>
	</div>
	<div class="bd">
		<s:form action="dashboard" id="da_customtime_panel_detail"
			name="da_customtime_panel_detail">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr style="display:none;">
					<td style="padding: 2px 2px 2px 6px;"> <s:checkbox name="enableTimeLocal" id="enableTimeLocal"></s:checkbox><s:text name="hm.dashboard.component.config.time.local"/></td>
				</tr>
				<tr>
					<td>
						<table  cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" width="80px"><s:text name="hm.dashboard.config.custom.start.time"/></td>
								<td width="60px"><s:textfield name="startTime"
									id="da_cu_startTime" value="%{startTime}" readonly="true"
									size="10" maxlength="10" /></td>
								<td width="15px"> <div id="startdatefields"></div> </td>
								<td width="80px"><s:select name="startHour"
									id="da_cu_startHour" value="%{startHour}" list="%{lstHours}"
									listKey="id" listValue="value" /></td>
							</tr>
							<tr><td height="6px"/></tr>
							<tr>
								<td class="labelT1"><s:text name="hm.dashboard.config.custom.end.time"/></td>
								<td width="60px"><s:textfield name="endTime"
									id="da_cu_endTime" value="%{endTime}" readonly="true"
									size="10" maxlength="10" /></td>
								<td width="15px"> <div id="enddatefields"></div> </td>
								<td width="80px"><s:select name="endHour"
									id="da_cu_endHour" value="%{endHour}" list="%{lstHours}"
									listKey="id" listValue="value" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
				<tr>
					<td style="padding-top: 8px;" align="center">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="OK"
										class="button" onClick="checkCustomTimeInvalid();">
								</td>
								<td width="20px"/>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button" onClick="hideCustomtimePanel();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>

<script type="text/javascript">
	function openHelpLinkPage(url) {
		window.open(url, 'newHelpWindows', 'height=600, width=800, resizable=yes');
	}
</script>

<div id="cancelWarnDialogDiv" style="display: none;">
</div>
<div id="customizeForChartPanel" style="display: none;">
	<div class="hd">
		<s:text name="hm.dashboard.tree.select.time.title"/>
	</div>
	<div class="bd">
		<div id="customizeForChartContent">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="dacAllPanel">
		<tr>
			<td align="left">
				<table border="0" cellspacing="0" cellpadding="0" style="margin-left: 45px; margin-top:10px; margin-bottom: 20px;">
					<tr>
						<td colspan="7">
							<div id="errDivForChartTimeSelect"></div>
						</td>
					</tr>
					<tr>

						<td class="npcButton"><a id="da_btlasthour_for_chart" href="javascript:void(0);" onclick="javascript: changeChartCustomTime(1);" class="btCurrent" title='<s:text name="hm.dashboard.config.customize.last.hour"/>'><span><s:text name="hm.dashboard.config.customize.last.hour"/></span></a></td>
						<td width="16px"/>
						<td class="npcButton"><a id="da_btlastday_for_chart" href="javascript:void(0);" onclick="javascript: changeChartCustomTime(2);" class="btCurrent" title='<s:text name="hm.dashboard.config.customize.last.day"/>'><span><s:text name="hm.dashboard.config.customize.last.day"/></span></a></td>
						<td width="16px"/>
						<td class="npcButton"><a id="da_btlastweek_for_chart" href="javascript:void(0);" onclick="javascript: changeChartCustomTime(3);" class="btCurrent" title='<s:text name="hm.dashboard.config.customize.last.week"/>'><span><s:text name="hm.dashboard.config.customize.last.week"/></span></a></td>
						<td width="16px"/>
						<td class="npcButton"><a id="da_btlastcustom_for_chart" href="javascript:void(0);" onclick="javascript: changeChartCustomTime(4);" class="btCurrent" title='<s:text name="hm.dashboard.config.customize.customer"/>'><span><s:text name="hm.dashboard.config.customize.customer"/></span></a></td>
					</tr>
					<tr>
						<td colspan="7">
							<span id="da_span_custom_time_detail_for_chart" style="display: none; margin-top: 7px; color: #BDBAAB;"></span>
						</td>
					</tr>
					<tr>
						<td colspan="7">
							<div id="da_customtime_panel_for_chart" style="display: none; margin-top: 7px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr><td height="8px"></td></tr>
									<tr style="display:none;">
										<td style="padding: 2px 2px 2px 6px;"> <s:checkbox name="enableTimeLocal" id="da_cu_enableTimeLocal_for_chart"></s:checkbox><s:text name="hm.dashboard.component.config.time.local"/></td>
									</tr>
									<tr><td height="8px"></td></tr>
									<tr>
										<td>
											<table  cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1" width="80px"><s:text name="hm.dashboard.config.custom.start.time"/></td>
													<td width="60px"><s:textfield name="startTime"
														id="da_cu_startTime_for_chart" value="%{startTime}" readonly="true"
														size="10" maxlength="10" /></td>
													<td width="15px"> <div id="startdatefields_for_chart"></div> </td>
													<td width="80px"><s:select name="startHour"
														id="da_cu_startHour_for_chart" value="%{startHour}" list="%{lstHours}"
														listKey="id" listValue="value" /></td>
												</tr>
												<tr><td height="6px"/></tr>
												<tr>
													<td class="labelT1"><s:text name="hm.dashboard.config.custom.end.time"/></td>
													<td width="60px"><s:textfield name="endTime"
														id="da_cu_endTime_for_chart" value="%{endTime}" readonly="true"
														size="10" maxlength="10" /></td>
													<td width="15px"> <div id="enddatefields_for_chart"></div> </td>
													<td width="80px"><s:select name="endHour"
														id="da_cu_endHour_for_chart" value="%{endHour}" list="%{lstHours}"
														listKey="id" listValue="value" /></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="5px"></td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
				</table>
				<table border="0" cellspacing="0" cellpadding="0" align="center" style="margin-bottom: 5px;">
					<tr>
						<td align="center">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="button" name="ignore" value="OK" <s:property value="writeDisabled" />
											class="button" onClick="javascript:saveChartTimePeriodSelection(_widget_config_callback);">
									</td>
									<td width="20px"/>
									<td>
										<input type="button" name="ignore" value="Cancel"
											class="button" onClick="javascript:hideDaCustomizePanel();">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</table>
		</div>
	</div>
</div>
<div id="da_widget_group_panel" style="display: none;">
	<div class="hd">
		<s:text name="hm.dashboard.tree.select.topology.title"/>
	</div>
	<div class="bd" style="overflow: auto; max-height: 550px;">
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
							<td width="49%" valign="top">
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
											<input type="button" name="ignore" value="OK" <s:property value="writeDisabled" />
												class="button" onClick="hm.da.saveWidgetGroupPanel(_widget_config_callback);">
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

<script type="text/javascript">
var daCustomizePanel = null;
function createDaCustomizePanel(){
	var div = document.getElementById("customizeForChartPanel");
	daCustomizePanel = new YAHOO.widget.Panel(div, {
		width:"450px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:999
		});
	daCustomizePanel.render(document.body);
	div.style.display="";
}

function hideDaCustomizePanel(){
	if(null != daCustomizePanel){
		daCustomizePanel.hide();
	}
}

var saveCustomizePeriodForTemporary = function(options) {
	this.setTmpReportConfig(this.getCurCtlBarSelectContainer(),
			{
				'timeType': options.curIndex,
				'startDate': options.startDateTmp,
				'startHour': options.startHourTmp,
				'endDate': options.endDateTmp,
				'endHour': options.endHourTmp,
				'enableTimeLocal': options.enableTimeLocalTmp
			}, true);
};
var saveCustomizePeriodPersist = function(options) {
	var dashboardTmp = getCurrentDashboard();
	var curChartContainer = dashboardTmp.getCurCtlBarSelectContainer();
	$.post("dashboard.action",
			{
				operation: "saveTimePeriodChart",
				timeType: options.curIndex,
				startHour: options.startHourTmp,
				startTime: options.startDateTmp,
				endHour: options.endHourTmp,
				endTime: options.endDateTmp,
				enableTimeLocal: options.enableTimeLocalTmp,
				widgetId: dashboardTmp.getMarkHelperFromChart(curChartContainer, "oWidgetId")
			}, 
			function(data, textStatus) {
				dashboardTmp.refreshCertainChart(dashboardTmp.getChartByContainer(curChartContainer));
			}, "json");
};
function openDaCustomizePanel(configType){
	if(null == daCustomizePanel){
		createDaCustomizePanel();
	}
	if (configType === "tmp") {
		_widget_config_callback = saveCustomizePeriodForTemporary;
	} else {
		_widget_config_callback = saveCustomizePeriodPersist;
	}
	daCustomizePanel.show();
}

function startTopologySelectForChart(chart, widgetIdArg, configType) {
	if(!chart) {
		return false;
	}
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp) {
		return false;
	}
	$.post("dashboard.action",{
		'operation': 'topoSelect',
		'widgetId': widgetIdArg
	}, function(data, textStatus) {
		succDoTopologySelect(chart, configType, data, textStatus);
	}, "json");
}
var succDoTopologySelect = function(chart, configType, data, textStatus) {
	try {
		if (data.t) {
			if (configType === "tmp") {
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
					if (tmpTopoConfig.hasOwnProperty('fobType')) {
						data.fobType = tmpTopoConfig.fobType;
					}
					if (tmpTopoConfig.hasOwnProperty('fobId')) {
						data.fobId = tmpTopoConfig.fobId;
					}
				}
			}
			if (data.v) {
				eval("var data2 = " + data.v);
				eval("var data3 = " + data.v3);
				Get("da_div_group_filter_tree1").innerHTML='';
				Get("da_div_group_filter_userpro_tree1").innerHTML='';
				//hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree1");
				hm.da.createTreeWidget(data.obId, "da_div_group_filter_tree1", data2);
				hm.da.createTreeWidget(data.fobId, "da_div_group_filter_userpro_tree1", data3);
				//currentWidgetId=data.wId;
			}
			openWidgetGroupPanel(configType);
		} else {
			showWarnDialog(data.m,"Error");
		}
	} catch(e){
		showWarnDialog("init Widget group Error. Session timeout.", "Error");
		return false;
	}
};

function startPeriodSelectForChart(chart, configType, widgetIdArg) {
	if (!chart) {
		return;
	}

	if (configType === "tmp") {
		var data = {};
		data.resultStatus = true;
		data.wId = widgetIdArg;
		data.tt = 2;
		data.ct = {};
		data.ct.s = '';
		data.ct.e = '';

		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp) {
			var tmpTopoConfig = dashboardTmp.getCurReportWidgetConfig(chart);
			if (tmpTopoConfig) {
				if (tmpTopoConfig.hasOwnProperty('timeType')) {
					data.tt = tmpTopoConfig.timeType;
				}
				if (tmpTopoConfig.hasOwnProperty('enableTimeLocal')) {
					data.ct.c = tmpTopoConfig.enableTimeLocal;
				}
				if (tmpTopoConfig.hasOwnProperty('startDate')) {
					data.ct.s = tmpTopoConfig.startDate;
				}
				if (tmpTopoConfig.hasOwnProperty('startHour')) {
					data.ct.sh = tmpTopoConfig.startHour;
				}
				if (tmpTopoConfig.hasOwnProperty('endDate')) {
					data.ct.e = tmpTopoConfig.endDate;
				}
				if (tmpTopoConfig.hasOwnProperty('endHour')) {
					data.ct.eh = tmpTopoConfig.endHour;
				}
			}
		}

		succDoTimePeriodSelect(data);
	} else {
		$.post("dashboard.action",{
			'operation': 'periodSelect',
			'widgetId': widgetIdArg
		}, function(data, textStatus) {
			succDoTimePeriodSelect(configType, data, textStatus)	
		}, "json"); 
	}
}
var succDoTimePeriodSelect = function(configType, data, textStatus) {
	try {
		if (data && data.resultStatus) {
			//currentWidgetId = data.wId;
			initChartTimePeriodSelection(data);
		}
	} catch (e) {
		showWarnDialog("init Time Period selection panel Error. Session timeout.", "Error");
		return false;
	}
	openDaCustomizePanel(configType);
};

var timePeriodTypesForChart = {
	1: 'da_btlasthour_for_chart',
	2: 'da_btlastday_for_chart',
	3: 'da_btlastweek_for_chart',
	4: 'da_btlastcustom_for_chart'
};
function changeChartCustomTime(index) {
	_.each(_.keys(timePeriodTypesForChart), function(idx) {
		$('#'+timePeriodTypesForChart[idx]).parent().removeClass('btTDSel');
		if (idx == index) {
			$('#'+timePeriodTypesForChart[idx]).parent().addClass('btTDSel');
		}
	});

	if (index == 4) {
		$('#da_customtime_panel_for_chart').show();
		$('#da_span_custom_time_detail_for_chart').show();
	} else {
		$('#da_customtime_panel_for_chart').hide();
		$('#da_span_custom_time_detail_for_chart').hide();
	}
}
function initChartTimePeriodSelection(data) {
	$('#da_cu_startTime_for_chart').val('');
	$('#da_cu_startHour_for_chart').get(0).selectedIndex = 0;
	$('#da_cu_endTime_for_chart').val('');
	$('#da_cu_endHour_for_chart').get(0).selectedIndex = 0;
	$('#da_span_custom_time_detail_for_chart').text('');
	if (!data) {
		$('#da_customtime_panel_for_chart').hide();
		$('#da_span_custom_time_detail_for_chart').hide();
		_.each(_.values(timePeriodTypesForChart), function(id) {
			$("#"+id).parent().removeClass('btTDSel');
		});
	} else {
		if (data.tt) {
			changeChartCustomTime(data.tt);
		}
		if (data.custstr) {
			$('#da_span_custom_time_detail_for_chart').text(data.custstr);
		}
		if (data.ct) {
			Get("da_cu_startTime_for_chart").value=data.ct.s;
			Get("da_cu_startHour_for_chart").value=data.ct.sh;
			Get("da_cu_endTime_for_chart").value=data.ct.e;
			Get("da_cu_endHour_for_chart").value=data.ct.eh;
			Get("da_cu_enableTimeLocal_for_chart").checked=data.ct.c;
		} else {
			Get("da_cu_startTime_for_chart").value='';
			Get("da_cu_endTime_for_chart").value='';
			Get("da_cu_endHour_for_chart").value=0;
			Get("da_cu_startHour_for_chart").value=0;
			Get("da_cu_enableTimeLocal_for_chart").checked=false;

		}
	}
}
function getSelectedTimeType() {
	return _.find(_.keys(timePeriodTypesForChart), function(idx) {
		return $('#'+timePeriodTypesForChart[idx]).parent().hasClass('btTDSel');
	});
}

function checkChartCustomTimeInvalid(){
	if (document.getElementById("da_cu_startTime_for_chart").value=="") {
	  hm.util.reportFieldError(document.getElementById("da_cu_startTime_for_chart"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.config.custom.start.time"/></s:param></s:text>');
	  return false;
	}
	if (document.getElementById("da_cu_endTime_for_chart").value=="") {
	  hm.util.reportFieldError(document.getElementById("da_cu_endTime_for_chart"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.config.custom.end.time"/></s:param></s:text>');
	  return false;
	}

	if (Get("da_cu_startTime_for_chart").value >Get("da_cu_endTime_for_chart").value
		 || (Get("da_cu_startTime_for_chart").value ==Get("da_cu_endTime_for_chart").value
		 &&  parseInt(Get("da_cu_startHour_for_chart").value) >= parseInt(Get("da_cu_endHour_for_chart").value))) {
	     hm.util.reportFieldError(Get("da_cu_startTime_for_chart"), '<s:text name="error.shourldLargerThan"><s:param><s:text name="hm.dashboard.config.custom.end.time"/></s:param><s:param><s:text name="hm.dashboard.config.custom.start.time"/></s:param></s:text>');
	     return false;
	}
	return true;
}

function saveChartTimePeriodSelection(callback) {
	var curIndex = getSelectedTimeType();
	if (!curIndex) {
		hm.util.displayJsonErrorNoteWithID('Please select time type first.', 'errDivForChartTimeSelect');
		return;
	}
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp) {
		showWarnDialog("No dashboard is defined.", "Error");
		return false;
	}

	if(curIndex == 4 && !checkChartCustomTimeInvalid()) {
		return false;
	}

	var startDateTmp = "1970-01-01",
		startHourTmp = 0,
		endDateTmp = "1970-01-01",
		endHourTmp = 0,
		enableTimeLocalTmp = false;
	if (curIndex == 4) {
		startDateTmp = Get("da_cu_startTime_for_chart").value;
		startHourTmp = Get("da_cu_startHour_for_chart").value;
		endDateTmp = Get("da_cu_endTime_for_chart").value;
		endHourTmp = Get("da_cu_endHour_for_chart").value;
		enableTimeLocalTmp = Get("da_cu_enableTimeLocal_for_chart").checked;
	}
	var result;
	if (callback) {
		result = callback.apply(dashboardTmp, [{
			curIndex: curIndex,
			startDateTmp: startDateTmp,
			startHourTmp: startHourTmp,
			endDateTmp: endDateTmp,
			endHourTmp: endHourTmp,
			enableTimeLocalTmp: enableTimeLocalTmp
		}]);
	}
	if (typeof result === 'undefined' || result) {
		succSaveTimePeriodSelect();
	}
}
var succSaveTimePeriodSelect = function(data, textStatus) {
	hideDaCustomizePanel();
	initChartTimePeriodSelection();

	/* var dashboardTmp = getCurrentDashboard();
	if (dashboardTmp) {
		dashboardTmp.refreshCertainChart(dashboardTmp.getCurCtlBarSelectChart());
	} */
};
</script>

<script type="text/javascript">
	head.js("<s:url value="/js/jquery.overlay.min.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/highcharts.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/exporting.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/json2-min.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/ahReportChart.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/chartControls.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/dashControls.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/theme/dashTheme.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/render/dashRender.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/dataRender/dashDataBase.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/dataRender/dashData.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/dashboard.js" includeParams="none" />?v=<s:property value="verParam" />",
			"<s:url value="/js/widget/chart/daCustom/dashSpecial.js" includeParams="none" />?v=<s:property value="verParam" />",
			function() {
				Aerohive.lang.chart.device.status.group.title.name = '<s:text name="hm.dashboard.widget.clientdevice.config.group.name.device" />';
				
				AhReportChart.Chart.prototype.seriesPointClick = function(chart, pointInfo) {
					if (!chart || !pointInfo) {
						return;
					}
					var hasDealt = false;
					// deal with link in chart
					var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
					var linkable = AhDashboardHelper.helper.isChartMetricValueLinkable(
							curConfig, pointInfo.series.options.keyN);
					if (linkable) {
						window.location.href = AhDashboardHelper.helper.getFormattedLinkUrl(linkable, {
							'data_key': pointInfo.y,
							'name_key': AhDashboardHelper.helper.getOriNameValue(pointInfo, curConfig.axis, pointInfo.series.options.keyN)
							});
						hasDealt = true;
					} 
					if (!hasDealt){
						var option = pointInfo.series.options || {};
						var optionName = option.name;
						if (pointInfo.series.type=="pie") {
							optionName = pointInfo.name;
						}
						var vResult = hm.da.validateForMonitorTab(chart, optionName, {
							isOnPoint: true,
							metric: option.keyN
						});
						var monitorElBase = option.myDataNode.bkValue;
						var nameTmp = AhDashboardHelper.helper.getNameBeforeConvert(pointInfo, curConfig.axis, pointInfo.series.options.keyN);
						if (pointInfo.series.options
								&& pointInfo.series.options.myDataNode
								&& pointInfo.series.options.myDataNode.dataBkValMap
								&& nameTmp in pointInfo.series.options.myDataNode.dataBkValMap) {
							monitorElBase = pointInfo.series.options.myDataNode.dataBkValMap[nameTmp]['@bkValue'];
						}
						if (vResult) {
							if (chart.currentDashboard.daHelper.checkWhetherMonitorItemInTimeRange(curConfig)) {
								hm.da.newMonitorTab(vResult.type, {
									tabName: vResult.name,
									monitorEl: monitorElBase,
									reportId: chart.reportId,
									metric: option.keyN,
									widgetId: chart.currentDashboard.getMarkHelperFromChart(chart.container, "oWidgetId")
								});
							}
							hasDealt = true;
						}
					}
					if (!hasDealt) {
						var ddInfo = AhDashboardHelper.helper.getDrillDownInfoOfChart(chart, pointInfo);
						if (!ddInfo) {
							return;
						}
						var vResult = hm.da.validateForMonitorTab(chart, ddInfo.xName, { isdrilldown: true });
						if (vResult) {
							hm.da.newDrillTab(vResult.type, vResult.name, ddInfo);
							hasDealt = true;
						}
					}
				};
				AhReportChart.Chart.prototype.xCategoryClick = function(chart, xLabelText, mType, macOrId) {
					if (!chart || !xLabelText || !macOrId) {
						return;
					}
					var hasDealt = false;
					var vResult = hm.da.validateForMonitorTab(chart, xLabelText);
					if (vResult) {
						hm.da.newMonitorTab(vResult.type, {
							tabName: vResult.name,
							monitorEl: macOrId,
							reportId: chart.reportId,
							widgetId: chart.currentDashboard.getMarkHelperFromChart(chart.container, "oWidgetId")
						});
						hasDealt = true;
					}
					
					if (!hasDealt) {
						if (hm.da.validateForDeviceDetailLink(chart)) {
							checkToMonitorDevice(xLabelText, mType, macOrId);
							hasDealt = true;
						}
					}
				};

				AhReportChart.Chart.prototype.whenRefreshData = function(chart, data) {
					if (!chart || !chart.currentDashboard) {
						return;
					}
					var curDa = chart.currentDashboard;
					curDa.setLastRefreshTime(new Date().getTime());
					/* var lastPeriodStr = '';
					var blnSpecialPeriod = false;
					if (data && data.desc) {
						lastPeriodStr = data.desc.subTitle;
						blnSpecialPeriod = data.desc.specialPeriod;
					}
					curDa.setLastChartReportPeriod(lastPeriodStr, blnSpecialPeriod); */
					refreshLastUpdateTimeForDa(curDa);
					refreshGlobalReportPeriodForDa(curDa);
				};
				prepareDashboardSettings();
				doRenderDashboard();
			}
	);

	var refreshLastUpdateTimeForDa = function(da) {
		if (!da) {
			return;
		}
		var curTimeStr = da.getLastRefreshTime();
		if ($('#lastUpdateTime1a:visible').length > 0) {
			$('#lastUpdateTime1').text(curTimeStr);
		} else if ($('#lastUpdateTime2a:visible').length > 0) {
			$('#lastUpdateTime2').text(curTimeStr);
		}
	};
	var refreshGlobalReportPeriodForDa = function(da) {
		// do not use it now
		/* if (!da) {
			return;
		}
		var periodStr = da.getLastChartReportPeriod();
		if ($('#time_period_drillmode:visible').length > 0) {
			$('#time_period_drillmode').text(periodStr);
		} else if ($('#time_period_common:visible').length > 0) {
			$('#time_period_common').text(periodStr);
		} */
	};

	var tzOffsetValue = new Date().getTimezoneOffset()*60000;

	function refreshDashboard(option) {
		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp) {
			dashboardTmp.refreshSelectedCharts(null, null, option);
		}
	}

	var daGroupContainers = {
		getCommon: function() {
			return "da_div_container";
		},
		getNew: function() {
			return "da_newda_div_container";
		},
		getMon: function() {
			return "da_div_drill_container";
		},
		getDrill: function() {
			return "da_div_drill_container";
		},
		addCommon: function() {
			return dashboardGroup.containers.generate({
				parent: 'da_div_container'
			});
		},
		addNew: function() {
			return dashboardGroup.containers.generate({
				parent: 'da_newda_div_container'
			});
		},
		addMon: function() {
			return dashboardGroup.containers.generate({
				parent: 'da_div_drill_container'
			});
		},
		addDrill: function() {
			return dashboardGroup.containers.generate({
				parent: 'da_div_drill_container'
			});
		}
	};

	var dashboardGroup = null;
	function prepareDashboardSettings() {
		dashboardGroup = new AhDashboard.DashboardGroup({});
		AhDashboard.Dashboard.prototype.configFunc = doDashboardChartConfig;
		AhDashboard.Dashboard.prototype.topologyConfigFunc = doDaChartTopologyConfig;
		AhDashboard.Dashboard.prototype.timeConfigFunc = doDaChartTimeConfig;
		AhDashboard.Dashboard.prototype.afterCloseFunc = afterChartIsClosed;
		AhDashboard.Dashboard.prototype.checkConfigFunc = doDaChartCheckConfig;
		AhDashboard.Dashboard.prototype.daHelper = AhDashboardHelper.helper;
		AhDashboard.Dashboard.prototype.afterRenderedCommonFunc = function(daTmp) {
			sizeLeftDaGroup();
		};
		AhDashboard.Dashboard.prototype.whenActiveFunc = function() {
			refreshLastUpdateTimeForDa(this);
			refreshGlobalReportPeriodForDa(this);
		};
	};

	function addCheckP(obj) {
		obj.p = {r: true};
		<s:if test="%{writeDisabled != 'disabled'}">
		obj.p.w = true;
		</s:if>
	}

	function doRenderDashboard() {
		var dashOptions = {};
		dashOptions.container = daGroupContainers.addCommon();
		dashOptions.boardId = currentDashBoardId;
		addCheckP(dashOptions);
		var dashboardTmp = new AhDashboard.Dashboard(dashOptions);
		dashboardGroup.dashboards.addAndActive(currentDashBoardId, dashboardTmp);
		dashboardTmp.render();
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
				Get("da_bt_custom_save").style.display="";
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
			/**
			$('#da_bt_widgetViewDetail').click(function(e){
				newEditComponent(chartId,blnCloned);
			});
			**/
			hm.da.initWidgetCopyDivValue(chartId);
			var optionsTmp = dashboardTmp.getCurrentEditWidgetOptions(chartContainer);
			var titleTmp = '';
			if (optionsTmp && optionsTmp.title) {
				$('#da_tx_wd_title').val(optionsTmp.title);
			}
			Get("da_bt_custom_save").style.display="none";
		}
	}

	function doDaChartTopologyConfig(chart, chartContainer, chartId, widgetIdArg, configType) {
		if (!chartContainer || !chartId) {
			return;
		}
		var dashboardTmp = getCurrentDashboard();
		if (!dashboardTmp) {
			return;
		}

		startTopologySelectForChart(chart, widgetIdArg, configType);
	}

	function doDaChartTimeConfig(chart, chartContainer, chartId, widgetIdArg, configType) {
		if (!chart) {
			return;
		}
		var dashboardTmp = getCurrentDashboard();
		if (!dashboardTmp) {
			return;
		}

		startPeriodSelectForChart(chart, configType, widgetIdArg);
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

	var saveChartCheckForTemporary = function(widgetIdArg, checked) {
		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp) {
			dashboardTmp.setTmpReportConfig(dashboardTmp.getCurCtlBarSelectContainer(),
					{
						'checked': checked
					}, true);
		}
	};
	var saveChartCheckPersist = function(widgetIdArg, checked) {
		$.post("dashboard.action",{
			'operation': 'checkWidget',
			'widgetId': widgetIdArg,
			'widgetChecked': checked
		}, succDoCheckForWidget);
	};
	function startCheckForWidget(chart, chartContainer, widgetIdArg, checked) {
		if (chart.currentDashboard.getConfigType("check") === "tmp") {
			saveChartCheckForTemporary(widgetIdArg, checked);
		} else {
			saveChartCheckPersist(widgetIdArg, checked);
		}
	}
	var succDoCheckForWidget = function(data, textStatus) {
		//do nothing for now
	};

	function afterChartIsClosed(chartContainer, dcId) {
		sizeLeftDaGroup();
		if (dcId && widgetPanel) {
			widgetPanel.uncheckWidget(dcId);
		}
	}

	function removePreviousNewDashboard() {
		if (myNewDashboard != null) {
			myNewDashboard.reset();
			myNewDashboard = null;
		}
	};
	var myNewDashboard = null;
	function resetNewDashboard() {
		//Get(formName+'_bgRollup2').checked = false;
		removePreviousNewDashboard();
		var dashOptions = {};
		dashOptions.container = daGroupContainers.addNew();
		dashOptions.boardId = -1;
		dashOptions.helper = 'new';
		addCheckP(dashOptions);
		myNewDashboard = new AhDashboard.Dashboard(dashOptions);
		dashboardGroup.dashboards.addAndActive('-1', myNewDashboard);
		myNewDashboard.render();
		setCustomMode(true);
	}
	hm.da.callback.createNewTabDone = resetNewDashboard;

	function resetCloneDashboard() {
		var dashboardTmp = dashboardGroup.dashboards.get(currentDashBoardId);
		if (!dashboardTmp) {
			return;
		}
		Get(formName+'_bgRollup2').checked = Get(formName+'_bgRollup').checked;
		if (myNewDashboard != null) {
			myNewDashboard.reset();
			myNewDashboard = null;
		}
		var dashOptions = {};
		dashOptions.container = daGroupContainers.addNew();
		dashOptions.boardId = -1;
		dashOptions.helper = 'clone';
		myNewDashboard = dashboardTmp.cloneTo(dashOptions);
		dashboardGroup.dashboards.addAndActive('-1', myNewDashboard);
		//myNewDashboard.render();
		setCustomMode(true);
	}
	hm.da.callback.createCloneTabDone = resetCloneDashboard;

	function succSaveNewTabDone(curDaId) {
		if (!curDaId || !dashboardGroup.dashboards.get('-1')) {
			return;
		}
		dashboardGroup.dashboards.changeDaId('-1', curDaId);
		var dashboardTmp = dashboardGroup.dashboards.get(curDaId);
		if (dashboardTmp) {
			dashboardTmp.setChanged(false);
			dashboardTmp.moveTo('#'+daGroupContainers.getCommon());
			//dashboardTmp.resizeDa();
			$('#da_bt_closeWidgetEditLink').click();
			dashboardTmp.clearRecyleItems();
		}
		dashboardGroup.dashboards.activeDa(curDaId);
		removePreviousNewDashboard();
	}
	hm.da.callback.succSaveNewTabDone = succSaveNewTabDone;

	function changeTabDone(curDaId) {
		if (!curDaId) {
			return;
		}
		if (!dashboardGroup.dashboards.get(curDaId)) {
			var dashOptions = {};
			dashOptions.container = daGroupContainers.addCommon();
			dashOptions.boardId = curDaId;
			addCheckP(dashOptions);
			var dashboardTmp = new AhDashboard.Dashboard(dashOptions);
			dashboardGroup.dashboards.addAndActive(curDaId, dashboardTmp);
			dashboardTmp.render();
		} else {
			dashboardGroup.dashboards.activeDa(curDaId);
		}
		sizeLeftDaGroup();
	}
	hm.da.callback.changeTabDone = changeTabDone;

	function newMonitorTabDone(curDaId, elName) {
		if (!curDaId || !elName) {
			return;
		}
		if (!dashboardGroup.dashboards.get(curDaId)) {
			var dashOptions = {};
			dashOptions.container = daGroupContainers.addMon();
			dashOptions.boardId = curDaId;
			dashOptions.monitorEl = elName;
			addCheckP(dashOptions);
			var dashboardTmp = new AhDashboard.Dashboard(dashOptions);
			dashboardGroup.dashboards.addAndActive(curDaId, dashboardTmp);
			dashboardTmp.render();
		} else {
			dashboardGroup.dashboards.activeDa(curDaId);
		}
		sizeLeftDaGroup();
	}
	hm.da.callback.succNewMonitorTabDone = newMonitorTabDone;

	function succStartDrilldownTabDone(curDaId) {
		if (!curDaId) {
			return;
		}
		var dashboardTmp = dashboardGroup.dashboards.get(curDaId);
		if (!dashboardTmp) {
			var dashOptions = {};
			dashOptions.container = daGroupContainers.addDrill();
			dashOptions.boardId = curDaId;
			addCheckP(dashOptions);
			var dashboardTmp = new AhDashboard.Dashboard(dashOptions);
			dashboardGroup.dashboards.addAndActive(curDaId, dashboardTmp);
			dashboardTmp.render();
		} else {
			dashboardGroup.dashboards.activeDa(curDaId);
		}
	}
	hm.da.callback.succStartDrilldownTabDone = succStartDrilldownTabDone;

	function removeTabDone(id, curDaId) {
		dashboardGroup.dashboards.remove(id);
		changeTabDone(curDaId);
	}
	hm.da.callback.removeTabDone = removeTabDone;

	function getCurrentDashboard() {
		if ($('#da_tr_newMode :visible').length > 0) {
			return myNewDashboard;
		}
		return dashboardGroup.dashboards.get(currentDashBoardId);
	}
</script>
<script type="text/javascript">
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
									+ prepareElType(objC.wConfig.timeType)
									+ prepareElType(objC.wConfig.startDate)
									+ prepareElType(objC.wConfig.startHour)
									+ prepareElType(objC.wConfig.endDate)
									+ prepareElType(objC.wConfig.endHour)
									+ prepareElType(objC.wConfig.enableTimeLocal)
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

	$('a#da_bt_custom_save').click(function(event) {
		var dashboardTmp = getCurrentDashboard();
		if (!dashboardTmp) return;

		var selected = dashboardTmp.getAllReportIds();
		if(null == selected || selected.length == 0){
			showWarnDialog("<s:text name="error.pleaseAddItems" />");
			return;
		}
		
		dashboardTmp.notes.addProcessing('<s:text name="info.custom.save.note"/>');
		var layouts = prepareDaLayoutsConfig(dashboardTmp);

		var bgRollupTmp = Get(formName + '_bgRollup').checked;
		$.post('dashboard.action',
				$.param({
					'operation': 'saveLayout',
					'columnsConfig': layouts.cols,
					'columnCharts': layouts.charts,
					'curDealTabId': currentDashBoardId,
					'bgRollup': bgRollupTmp
				}, true),
				succSaveDaLayoutsCallback,
				'json');
	});

	var succSaveDaLayoutsCallback = function(data, textStatus) {
		var dashboardTmp = getCurrentDashboard();
		if (!dashboardTmp) return;
		dashboardTmp.setChanged(false);
		if (data) {
			if (data.resultStatus === true) {
				dashboardTmp.notes.addInfo('<s:text name="info.da.save.successfully"/>');
				$('#da_bt_closeWidgetEditLink').click();
				dashboardTmp.clearRecyleItems();
				if (data.d) {
					dashboardTmp.refreshDaConfigsAfterSuccSaving(data.d);
				}
				customCancelClickCancel();

				hm.da.resetHighIntervalTimeOut(data);

				return;
			}
		}
		dashboardTmp.notes.addError('<s:text name="info.da.save.failed"/>');
	};

	function cleanThingsAfterCancelOnPage() {
		$('#da_bt_closeWidgetEditLink').click();
		var dashboardTmp = dashboardGroup.dashboards.get(currentDashBoardId);
		if (dashboardTmp) {
			if (dashboardTmp.isExistedDa()) {
				dashboardTmp.restoreUnsavedModification();
			} else {
				dashboardGroup.dashboards.remove('-1');
			}
			sizeLeftDaGroup();
		}
		removePreviousNewDashboard();
		dashboardGroup.dashboards.activeDa(currentDashBoardId);
	}

	var cancelButDiscardChanges = function() {
		this.hide();

		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp && !dashboardTmp.isExistedDa()) {
			removePreviousNewDashboard();
		}
		if ($('#da_bt_custom_cancel :visible').length > 0) {
			customCancelClickCancel();
		} else if ($('#da_bt_newMode_cancel :visible').length > 0) {
			newModeCancelClickCancel();
		}

		if (dashboardTmp && dashboardTmp.isExistedDa()) {
			dashboardTmp.setChanged(false);
		}
	};
	var cancelButSaveDashboard = function() {
		this.hide();
		if ($('#da_bt_custom_save :visible').length > 0) {
			$('a#da_bt_custom_save').click();
		} else if ($('#da_bt_newMode_save :visible').length > 0) {
			$('a#da_bt_newMode_save').click();
		}
	};
	var mybuttons = [ { text:"Save", handler: cancelButSaveDashboard, isDefault:true},
                      { text:"Cancel", handler: cancelButDiscardChanges} ];
	var cancelOrSavePanel;
	var initCancelOrSaveWin = function() {
		var div = document.getElementById("cancelWarnDialogDiv");
		cancelOrSavePanel = new YAHOO.widget.SimpleDialog(div, {
			width:"350px",
			underlay: "none",
			visible:false,
			draggable:true,
			close:true,
			modal:true,
			fixedcenter:true,
			constraintoviewport:true,
			icon: YAHOO.widget.SimpleDialog.ICON_WARN,
			zIndex:999
			});
		cancelOrSavePanel.setHeader("Warning");
		cancelOrSavePanel.cfg.setProperty("text", '<s:text name="info.da.save.dialog.prompt"/>');
		cancelOrSavePanel.cfg.queueProperty("buttons", mybuttons);
		cancelOrSavePanel.render(document.body);
		div.style.display = "";
	};
	var showCancelOrSaveWin = function() {
		if (cancelOrSavePanel == null) {
			initCancelOrSaveWin();
		}
		cancelOrSavePanel.cfg.setProperty('visible', true);
	};

	var checkToMonitorDevice = function(mText, mType, macOrId) {
		mType = mType.replace("Detail", "");
		$.post('dashboard.action',
				{
					operation: "checkDeviceMonitor",
					monitorEl: macOrId,
					deviceMonitorType: mType
				},
				function(data, textStatus) {
					succCheckMonitorDeviceCallback(mText, mType, data, textStatus);
				},
				'json');
	};
	var succCheckMonitorDeviceCallback = function(mText, mType, data, textStatus) {
		if (data) {
			if (data.rs) {
				confirmLeavePageForMonitor(mText, mType, data.id);
				return;
			}
		}
		warnNoDeviceInMonitorList(mText, mType);
	};

	var confirmLeavePageForMonitor = function(mText, mType, id) {
		thisOperation = "deviceMonitor";
		var tipstr1 = "Devices";
		if (mType == 'client') {
			tipstr1 = 'Clients';
		}
		document.forms[formName].monitorEl.value = id;
		document.forms[formName].deviceMonitorType.value = mType;
		
		var mybuttons = [ { text:"Continue", handler: function(){this.hide(); doContinueOper();}, isDefault:true },
	                      { text:"Cancel", handler: function(){this.hide();}} ];
	    var warningMsg = "<html><body>This operation takes you to the Monitor > " + tipstr1 
	    					+ " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;page. To stay on this page, click Cancel.</body></html>";
	    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Confirm");
	    dlg.show();
	};

	var warnNoDeviceInMonitorList = function(mText, mType) {
		var tipstr1 = "device";
		if (mType == 'client') {
			tipstr1 = 'client';
		}
		warnDialog.cfg.setProperty('text', "No " + tipstr1 + " (" + mText + ") exists!");
		warnDialog.show();
	};
</script>

<div id="emailPanel" style="display: none;">
	<div class="hd">
		Email
	</div>
	<div class="bd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="labelT1">
						<s:text name="report.networkusage.onceMail.text" />
					</td>
				</tr>
				<tr><td><table><tr><td><div id="emailErrorDiv"/></td></table></td></tr>
				<tr>
					<td style="padding-left: 10px">
						<s:textfield name="onceMail" id="onceMail" maxlength="128"
							size="50" />&nbsp;<s:text name="report.reportList.email.emailNoteRange" />
					</td>
				</tr>
				<tr><td height="8px"/></tr>
				<tr>
					<td nowrap="nowrap"  class="noteInfo" style="padding-left: 10px"><s:text
							name="report.reportList.email.note" /></td>
				</tr>
				<tr>
					<td nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
							name="report.reportList.email.emailNote" /></td>
				</tr>
				<tr>
					<td height="8px"></td>
				</tr>
				<tr>
					<td  align="right">
						<input type="button" name="ignore" value="Send""
							class="button" onClick="if(validateEmailPanelInput()) sendEmail();">
					</td>
				</tr>
			</table>
	</div>
</div>

<script type="text/javascript">
<!--
var newReportPanel = null;

function createNewReportPanel(){
	var div = document.getElementById("new_report_panel");
	newReportPanel = new YAHOO.widget.Panel(div, {
		width:"600px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:true,
		constraintoviewport:false
		});
	newReportPanel.render(document.body);
	div.style.display = ""
}

function showNewReportPanel(){
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp || dashboardTmp.isBlank()) {
		showWarnDialog('<s:text name="hm.dashboard.blank.not.support"><s:param><s:text name="hm.dashboard.edit.saveasreport"/></s:param></s:text>', "Warning");
		return;
	}
	if(null == newReportPanel){
		createNewReportPanel();
	}
	//cleanReportBasicSettings();
	init_report_customtime_settings();
	newReportPanel.show();
	focusReportBasicSettings();
}

function saveReportAsPdf(){
	if(!validateReportSettings()){
		return;
	}
	var url = "<s:url action='dashboard' includeParams='none'/>?operation=saveAsReport";
	collectReportScheduleSettings("dashboard_report_setting");
	ajaxRequest("dashboard_report_setting", url, succSaveReportAsPdf, "post");
}

function succSaveReportAsPdf(o){
	try {
		eval("var data = " + o.responseText);
		if(data.m){
			showWarnDialog(data.m, "Error");
			return;
		}else if(data.id){
			cancelReportAsPdf();
			cleanReportBasicSettings();
			var msg = '<s:text name="info.da.save.report.successfully"></s:text>';
			var link = "<a href='<s:url action='recurReport' includeParams='none'/>?operation=edit&id="+data.id+"'>" + "here</a>";
			msg = msg.replace("{0}", data.t).replace("{1}", link).replace("{0}", data.t);
			showInfoDialog(msg);
		}
	}catch(e){
		showWarnDialog("Save Report Error. Session timeout.", "Error");
		return;
	}
}

function cancelReportAsPdf(){
	if(null != newReportPanel){
		newReportPanel.hide();
	}
}

function validateReportSettings(){
	if(!validateReportBasicSettings()){
		return false;
	}
	if(!validateReportScheduleSettings()){
		return false;
	}
	return true;
}

//-->
</script>
<div id="new_report_panel" style="display: none;">
    <div class="hd"><s:text name="hm.dashboard.edit.new.report" /></div>
    <div class="bd"><s:form action="dashboard" name="dashboard_report_setting" id="dashboard_report_setting">
    	<table cellspacing="0" cellpadding="0" border="0" width="100%">
    		<tr>
    			<td>
    				<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
    					<tr><td><tiles:insertDefinition name="reportBasicView" /></td></tr>
    					<tr><td><tiles:insertDefinition name="reportScheduleView" /></td></tr>
    				</table>
    			</td>
    		</tr>
    		<tr>
    			<td style="padding-top: 8px;">
    				<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td><input type="button" name="ignore" <s:property value="writeDisabled" />
								value="<s:text name="common.button.save"></s:text>" class="button" onclick="saveReportAsPdf();"></td>
							<td><input type="button" name="ignore" value="<s:text name="common.button.cancel"></s:text>"
								class="button" onclick="cancelReportAsPdf();"></td>
						</tr>
    				</table>
    			</td>
    		</tr>
    	</table></s:form>
    </div>
</div>
