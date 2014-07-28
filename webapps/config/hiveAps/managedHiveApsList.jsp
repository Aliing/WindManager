<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<%@page import="com.ah.be.parameter.constant.util.AhWebUtil"%>
<%@page import="com.ah.util.devices.impl.Device"%>
<%@page import="com.ah.bo.hiveap.DeviceInfo"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link type="text/css" rel="stylesheet" href="<s:url value="/css/hm_tab.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>

<style type="text/css">
pre {
        display: block;
        overflow: auto;
        padding: 5px 10px;
        wwhite-space: pre-wrap; /* css-3 */
        white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
        white-space: -pre-wrap; /* Opera 4-6 */
        white-space: -o-pre-wrap; /* Opera 7 */
        word-wrap: break-word; /* Internet Explorer 5.5+ */
        white-space : normal ; /* Internet Explorer 5.5+ */
}

p {
	margin-top:5px;
	margin-bottom:5px;
}
<!--
#cliInfoPanel .bd {
	padding: 0;
}

#cliInfoPanel .bd_top {
	background-color: #eee;
}

#cliInfoPanel .cli_viewer {
	padding: 10px;
	overflow: auto;
	height: 25em;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #fff;
}

#cliInfoPanel .ft {
	height: 15px;
	padding: 0;
}

#cliInfoPanel .yui-resize-handle-br {
	right: 0;
	bottom: 0;
	height: 8px;
	width: 8px;
	position: absolute;
}

#lldpCdpPanel .bd {
	overflow: auto;
}

img.serverIcon {
	border: 1px solid #f8f8f8;
}

img.serverIcon:hover {
	border: 1px solid #6088aa;
}

table.view tr.newHiveApRow {

}

.managedStatusLabel {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-style: normal;
	font-weight: bold;
	color: #fff;
	background-color: #333333;
	padding: 2px 4px;
	margin-top: 1px;
}

#newHiveApTool {
	cursor: pointer;
}

#newHiveApTool:hover {
	color: #FFB73F;
}

#syncSGEPanel .tableHd {
	border-bottom: 1px solid #808080;
	padding: 2px 5px 2px 4px;
}

#syncSGEPanel .tableBd {
	padding: 2px 5px 2px 4px;
}

#syncSGEPanel .syncSGE_message {
	margin-top: 5px;
	padding: 2px 5px 2px 4px;
	background-color: #FFFFCC;
	font-weight: normal;
	color: #003366;
	border-right-style: inset;
	border-bottom-style: inset;
	border-right-color: #333333;
	border-bottom-color: #333333;
	border-right-width: 1px;
	border-bottom-width: 1px;
}

#syncSGEPanel span {
	display: block;
	padding: 2px;
}

#syncSGEPanel span.finished {
	background-color: #339900;
}

#syncSGEPanel span.running {
	background-color: #33FF00;
}

#syncSGEPanel div.normal {
	padding: 2px 0;
}

#syncSGEPanel div.error {
	padding: 2px 0;
	color: #FF0000;
}
-->
</style>

<script>
var Device_TYPE_SWITCH = <%=HiveAp.Device_TYPE_SWITCH%>;
var Device_TYPE_AP = <%=HiveAp.Device_TYPE_HIVEAP%>;
var Device_TYPE_VPN_GATEWAY = <%=HiveAp.Device_TYPE_VPN_GATEWAY%>;
var Device_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;
var Device_TYPE_VPN_BR = <%=HiveAp.Device_TYPE_VPN_BR%>;

var MODEL_AG20 = <%=HiveAp.HIVEAP_MODEL_20%>;
var MODEL_AG28 = <%=HiveAp.HIVEAP_MODEL_28%>;

var MODEL_VPN_GATEWAY = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA%>;
var MODEL_CVG_APPLIANCE = <%=HiveAp.HIVEAP_MODEL_VPN_GATEWAY%>;

var MODEL_BR200_WP = <%=HiveAp.HIVEAP_MODEL_BR200_WP%>;
var MODEL_BR200 = <%=HiveAp.HIVEAP_MODEL_BR200%>;
var MODEL_BR100 = <%=HiveAp.HIVEAP_MODEL_BR100%>;
var MODEL_BR200_LTE_VZ = <%=HiveAp.HIVEAP_MODEL_BR200_LTE_VZ%>;

var MODEL_380 = <%=HiveAp.HIVEAP_MODEL_380%>;
var MODEL_390 = <%=HiveAp.HIVEAP_MODEL_390%>;
var MODEL_370 = <%=HiveAp.HIVEAP_MODEL_370%>;
var MODEL_230 = <%=HiveAp.HIVEAP_MODEL_230%>;
var MODEL_350 = <%=HiveAp.HIVEAP_MODEL_350%>;
var MODEL_340 = <%=HiveAp.HIVEAP_MODEL_340%>;
var MODEL_330 = <%=HiveAp.HIVEAP_MODEL_330%>;
var MODEL_320 = <%=HiveAp.HIVEAP_MODEL_320%>;

var MODEL_170 = <%=HiveAp.HIVEAP_MODEL_170%>;
var MODEL_141 = <%=HiveAp.HIVEAP_MODEL_141%>;
var MODEL_121 = <%=HiveAp.HIVEAP_MODEL_121%>;
var MODEL_120 = <%=HiveAp.HIVEAP_MODEL_120%>;
var MODEL_110 = <%=HiveAp.HIVEAP_MODEL_110%>;

var MODEL_SR_24 = <%=HiveAp.HIVEAP_MODEL_SR24%>;
var MODEL_SR_2024P = <%=HiveAp.HIVEAP_MODEL_SR2024P%>;
var MODEL_SR_2124P = <%=HiveAp.HIVEAP_MODEL_SR2124P%>;
var MODEL_SR_2148P = <%=HiveAp.HIVEAP_MODEL_SR2148P%>;
var MODEL_SR_48 = <%=HiveAp.HIVEAP_MODEL_SR48%>;

var SPT_DEVICE_IMAGE_COUNTS = '<%=DeviceInfo.SPT_DEVICE_IMAGE_COUNTS%>';

<%=AhWebUtil.getBoolArray(Device.SUPPORTED_LOCATE)%>;

var deviceTypes = <s:property value="deviceTypesJsonString" escapeHtml="false" />;
var deviceInfos = <s:property value="deviceInfoJSONStr" escapeHtml="false" />;

function isAnySwitchSelected(selectedId){
	if(selectedId == null){
		return false;
	}
	for(var i=0; i<selectedId.length; i++){
		var id = selectedId[i];
		var type = deviceTypes["_" + id];
		if(type == Device_TYPE_SWITCH){
			return true;
		}
	}
	return false;
}

YAHOO.util.Event.onDOMReady(function () {
	var isCollapse = <s:property value="collapsed" />;
	collapseNewHiveAPs.isCollapse = isCollapse || false;
	collapseNewHiveAPs();
});
function collapseNewHiveAPs(){
	var isCollapse = collapseNewHiveAPs.isCollapse;
	var toolSpan = document.getElementById("newHiveApTool");
	var hiveApTable = document.getElementById("hiveApListTable");
	if(hiveApTable && hiveApTable.rows){
		var rows = hiveApTable.rows;
		for(var i=0; i<rows.length; i++){
			var row = rows[i];
			if (/\bnewHiveApRow\b/.test(row.className)) {
				row.style.display = isCollapse?"none":"";
			}
		}
	}
	if(toolSpan){
		toolSpan.innerHTML = isCollapse?"[ + ]":"[ - ]";
		toolSpan.title = isCollapse? "Click to expend":"Click to collapse";
		toolSpan.onclick = function(){
			collapseNewHiveAPs.isCollapse = !isCollapse;
			var url = "<s:url action='hiveAp' includeParams='none'/>" + "?operation=collapseNewHiveAps&collapsed=" + !isCollapse + "&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {}, null);
			collapseNewHiveAPs();
		}
	}
}
<s:if test="%{showReassignMenu}">
YAHOO.util.Event.onDOMReady(function () {
	var sMenu = new YAHOO.widget.Menu('reassign_menu', { zindex: 1000,maxheight:100000,minscrollheight:100000 });
	var sItems = [
					[<s:iterator value="reassignDomains" status="status">
						{text: "<s:property value="domainNameESC" escape="false" />"},
					</s:iterator>]
				  ];
	sMenu.addItems(sItems);
	sMenu.subscribe('click', sMenuItemClick);
	sMenu.subscribe("beforeShow", function(){
		var x1 = YAHOO.util.Dom.getX('s_menutoggle');
		var y1 = YAHOO.util.Dom.getY('s_menutoggle');
		YAHOO.util.Dom.setX('reassign_menu', x1 + 5);
		YAHOO.util.Dom.setY('reassign_menu', y1+20);
	});

	sMenu.render();

	YAHOO.util.Event.addListener("s_menutoggle", "click", sMenu.show, null, sMenu);

	// insert a header for filtering
	var filterInputId = YAHOO.util.Dom.generateId();
	var defaultMessage = '<s:property value="%{filterVHMText}"/>';
	sMenu.setHeader("<input autocomplete='off' type='text' value='"+defaultMessage+"' id='"+filterInputId+"'>");
	// set input as a fixed length
	var hlength = sMenu.header.offsetWidth*0.9;
	YAHOO.util.Dom.setStyle(filterInputId, "width", (hlength<100?100:hlength)+"px");
	YAHOO.util.Dom.setStyle(sMenu.body, "zoom", "normal");

	YAHOO.util.Event.on(filterInputId, "keyup", function(){
		var inputElement = document.getElementById(filterInputId);
		YAHOO.util.Dom.setStyle(inputElement, "color", "#000");
		filterVhm(inputElement.value, sMenu);
	}, sMenu, true);
	YAHOO.util.Event.on(filterInputId, "focus", function(){
		var inputElement = document.getElementById(filterInputId);
		if(inputElement.value == defaultMessage){
			inputElement.value = "";
			YAHOO.util.Dom.setStyle(inputElement, "color", "#000");
		}
	}, null, true);
	YAHOO.util.Event.on(filterInputId, "blur", function(){
		var inputElement = document.getElementById(filterInputId);
		if(inputElement.value.length == 0){
			inputElement.value = defaultMessage;
			YAHOO.util.Dom.setStyle(inputElement, "color", "#999");
		}
	}, null, true);
	function filterVhm(t, sMenu){
		var items = sMenu.getItems();
		for(var i=0; i<items.length; i++){
			var text = items[i].cfg.getProperty("text");
			if(t.length == 0 || text.toLowerCase().indexOf(t.toLowerCase()) == 0){
				YAHOO.util.Dom.setStyle(items[i].element, "display", "");
			}else{
				YAHOO.util.Dom.setStyle(items[i].element, "display", "none");
			}
		}
		if(YAHOO.env.ua.ie){//fix shadow issue
			sMenu.configShadow(null, [true], null);
		}
	}

	function sMenuItemClick(p_sType, p_aArguments){
		var event = p_aArguments[0];
		var menuItem = p_aArguments[1];
//		alert(menuItem.cfg.getProperty("text"));
		var reassignDomain = menuItem.cfg.getProperty("text");
		document.forms[formName].reassignDomainName.value = reassignDomain;
		submitAction("reassignDomain");
	}
});
</s:if>
</script>
<script>
var writePermission = <s:property value="writePermission" />;
// QUIRKS FLAG, FOR BOX MODEL
var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
	// UNDERLAY/IFRAME SYNC REQUIRED
var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

YAHOO.util.Event.onDOMReady(function () {
	/*
	     Instantiate the menu.  The first argument passed to the
	     constructor is the id of the DOM element to be created for the
	     menu; the second is an object literal representing a set of
	     configuration properties for the menu.
	*/
	var oMenu = new YAHOO.widget.Menu("basicmenu", { fixedcenter: false, zIndex: 999 });

	/*
	    Add items to the menu by passing an array of object literals
	    (each of which represents a set of YAHOO.widget.MenuItem
	    configuration properties) to the "addItems" method.
	*/
	var tItems=[];
	var subItem=[];
	tItems.push([{ text: '<s:text name="geneva_06.update.menu.update"/>', onclick: { fn: onMenuItemClick, obj: "simpllyUpdateModel" } }]);
	
	subItem.push([{ text: '<s:text name="hiveAp.update.configuration"/>', onclick: { fn: onMenuItemClick, obj: "advancedUpdateModel" } }]);
	subItem.push([{ text: '<s:text name="hiveAp.update.image"/>', onclick: { fn: onMenuItemClick, obj: "upgradeImage" } }]);
	<s:if test="%{hmListType != 'managedSwitches'}">
		subItem.push([{ text: '<s:text name="hiveAp.update.l7.signature"/>', onclick: { fn: onMenuItemClick, obj: "upgradeSignature" } }]);
    </s:if>
	subItem.push([{ text: '<s:text name="hiveAp.update.cwp.remove"/>', onclick: { fn: openCwpRemovePanel} }]);
	subItem.push([{ text: '<s:text name="hiveAp.update.bootstrap"/>', onclick: { fn: onMenuItemClick, obj: "upgradeBootstrap" } }]);
	<s:if test="%{hmListType != 'managedVPNGateways' && hmListType != 'managedSwitches'}">
		subItem.push([{ text: '<s:text name="hiveAp.update.countryCode"/>', onclick: { fn: onMenuItemClick, obj: "upgradeCountryCode" } }]);
    </s:if>
    <s:if test="%{hmListType != 'managedSwitches'}">
		subItem.push([{ text: '<s:text name="hiveAp.update.poe"/>', onclick: { fn: onMenuItemClick, obj: "upgradePoe" } }]);
    </s:if>
	subItem.push([{ text: '<s:text name="hiveAp.update.netdump"/>', onclick: { fn: onMenuItemClick, obj: "upgradeNetdump" } }]);
	<s:if test="%{(hmListType == 'managedHiveAps' || hmListType == 'managedVPNGateways') && vaImageURL != '' }">
		subItem.push([{ text: '<s:text name="hiveAp.update.cvg.url.menu"/>', url: "<s:property value='vaImageURL'/>"}]);
    </s:if>
	
    tItems.push({text: "<s:text name="geneva_06.update.menu.advanced"/>", submenu: {id: "updateAdvanced",itemdata:subItem}});
	
	oMenu.addItems(tItems);

	oMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('menutoggle');
		var y = YAHOO.util.Dom.getY('menutoggle');
		YAHOO.util.Dom.setX('basicmenu', x);
		YAHOO.util.Dom.setY('basicmenu', y+20);
	});

/*
     Since this menu is built completely from script, call the "render"
     method passing in the id of the DOM element that the menu's
     root element should be appended to.
*/
	oMenu.render();

	YAHOO.util.Event.addListener("menutoggle", "click", oMenu.show, null, oMenu);

//  move topology leafNode menu to here
	var t_oMenu = new YAHOO.widget.Menu("t_menu", { fixedcenter: false, zIndex: 999 });
	t_oMenu.subscribe('click',otherMenuItemClick);
	t_oMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('t_menutoggle');
		var y = YAHOO.util.Dom.getY('t_menutoggle');
		YAHOO.util.Dom.setX('t_menu', x);
		YAHOO.util.Dom.setY('t_menu', y+20);
	});

	YAHOO.util.Event.addListener("t_menutoggle", "click", function() {opent_oMenu(t_oMenu);}, null, t_oMenu);

	// Overlay for client information.
	//createClientInfoPanel();//revise horizontal scroll bar issue.
	// Overlay for neighbor information.
	//createNeighborInfoPanel();//revise horizontal scroll bar issue.
	// Overlay for CLI information.
	//createCliInfoPanel();//revise position issue.

	// createLldpCdpPanel();
	// Overlay for remove Captive Web Page directory
	//createCwpRemovePanel();//revise horizontal scroll bar issue.
	// Overlay for image to boot
	//createImageBootPanel();//revise horizontal scroll bar issue.
	//create tooltip for description
	createTooltip();
	<s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive && writeDisabled!='disabled') || (!hMOnline && writeDisabled!='disabled')}">
		createDeviceInventoryMenu();
	</s:if>
 });
 
function createDeviceInventoryMenu() {
	var di_Menu = new YAHOO.widget.Menu("s_menu_inventory", { fixedcenter: false, zIndex: 999 });

	var di_Items=[];
	//var subItem=[];
	<s:if test="%{hMOnline}">
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.addImport"/>'}]);
	</s:if>
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.remove"/>'}]);
	//di_Items.push(subItem);
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.export"/>'}]);
	di_Menu.addItems(di_Items);

	di_Menu.subscribe('click', function (p_sType, p_aArguments){
		var event = p_aArguments[0];
		var menuItem = p_aArguments[1];
		var menuText = menuItem.cfg.getProperty("text");
		if (menuText=='<s:text name="geneva_08.hm.menu.addImport"/>') {
			openScanOverlay();
		} else if (menuText=='<s:text name="geneva_08.hm.menu.remove"/>') {
			submitAction('remove');
		} else if (menuText=='<s:text name="geneva_08.hm.menu.export"/>') {
			submitAction('exportDeviceInventory');
		}
	});
	
	di_Menu.subscribe("beforeShow", function(){
		var x1 = YAHOO.util.Dom.getX('menu_inventory');
		var y1 = YAHOO.util.Dom.getY('menu_inventory');
		YAHOO.util.Dom.setX('s_menu_inventory', x1 + 1);
		YAHOO.util.Dom.setY('s_menu_inventory', y1+25);
	});

	di_Menu.render();

	YAHOO.util.Event.addListener("menu_inventory", "click", di_Menu.show, null, di_Menu);
}

 function opent_oMenu(t_oMenu){
		var tItems= [];
		var deDtype = -1;
		var deDmodel = -1;
		var selectItem=true;
		var moreSelect=false;


		var selectedIds = hm.util.getSelectedIds();
		if(selectedIds.length < 1){
			selectItem=false;
		} else if (selectedIds.length>1) {
			moreSelect = true;
		}

		if (selectItem){
			var selectedId = selectedIds[0];
			if (typeof deviceTypes["_" + selectedId] != 'undefined') {
				deDtype = deviceTypes["_" + selectedId];
			}
			if (typeof deviceTypes["__" + selectedId] != 'undefined') {
				deDmodel = deviceTypes["__" + selectedId];
			}
		}

		tItems = resetToolMenu(deDtype, deDmodel,writePermission,selectItem, moreSelect ,<s:property value="%{showSyncSGEMenu}"/> ,<s:property value="%{visibleCliWindow}" />);

		t_oMenu.clearContent();
		t_oMenu.addItems(tItems);
		t_oMenu.render();
		t_oMenu.show();
 }

function resetToolMenu(deviceType, deviceModel, permission, selectItem, moreSelect, showSegMenu,visibleCliWindow){
	var tItems=[];
	var subItem2=[];
	var subArray=[];
	if (moreSelect) {
		var displayResetPse=true;
		var displayResetUsbModem=true;
		var displaySetImageToBoot=true;
		var displayClearIdManager=true;
		var displayTurboModeToggle = true;
		var selectedIds = hm.util.getSelectedIds();
		var deDtype=-1;
		var deDmodel=-1;
		for(var i=0; i<selectedIds.length; i++){
			var id = selectedIds[i];
			if (typeof deviceTypes["_" + id] != 'undefined') {
				deDtype = deviceTypes["_" + id];
			}
			if (typeof deviceTypes["__" + id] != 'undefined') {
				deDmodel = deviceTypes["__" + id];
			}
			if (deDmodel!=MODEL_BR200_LTE_VZ && deDmodel!=MODEL_BR200_WP && deDmodel!=MODEL_SR_24 && deDmodel!=MODEL_SR_2124P && deDmodel!=MODEL_SR_48 && deDmodel!=MODEL_SR_2024P && deDmodel!=MODEL_SR_2148P) {
				displayResetPse=false;
			}
			if (deDmodel!=MODEL_BR200_LTE_VZ){
				displayResetUsbModem=false;
			}
			if((deviceInfos[deDmodel])[SPT_DEVICE_IMAGE_COUNTS] < 2){
				displaySetImageToBoot=false;
			}
			if(deDmodel != MODEL_230){
				displayTurboModeToggle=false;
			}
			if (deDmodel==MODEL_BR100 || deDmodel==MODEL_VPN_GATEWAY || deDmodel == MODEL_CVG_APPLIANCE
					|| deDmodel==MODEL_SR_24 || deDmodel==MODEL_SR_2124P || deDmodel==MODEL_SR_2024P || deDmodel==MODEL_SR_2148P || deDmodel==MODEL_SR_48) {
				displayClearIdManager=false;
			}
		}

		tItems.push([{text: "<s:text name="topology.menu.client.information"/>"}]);

		if (displayResetPse){
			tItems.push([{text: "<s:text name="topology.menu.hiveAp.pse.reset"/>",disabled: !permission}]);
		}
		if (displayResetUsbModem){
			tItems.push([{text: "<s:text name="geneva_03.topology.menu.hiveAp.usbmodem.reset"/>",disabled: !permission}]);
		}
		tItems.push([{text: "<s:text name="topology.menu.hiveAp.reboot"/>",disabled: !permission}]);
		tItems.push([{text: "<s:text name="topology.menu.hiveAp.reset.default"/>",disabled: !permission}]);
		if (displaySetImageToBoot){
			tItems.push([{text: "<s:text name="topology.menu.hiveAp.invokeBackup"/>",disabled: !permission}]);
		}
		if (displayTurboModeToggle){
			tItems.push([{text: "<s:text name="topology.menu.hiveAp.turboModeToggle"/>",disabled: !permission}]);
		}
		if (displayClearIdManager){
			tItems.push([{text: "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>",disabled: !permission}]);
		}
		tItems.push([{text: "<s:text name="topology.menu.get.tech.file"/>",disabled: !permission}]);
		if(deviceType== Device_TYPE_BRANCH_ROUTER || deviceType== Device_TYPE_VPN_BR){
			tItems.push([{text: "<s:text name="topology.menu.hiveAp.disable.hiveui.cfg"/>",disabled: !permission}]);
		}

		 if(visibleCliWindow){
			 tItems.push([{text: "<s:text name="hiveap.tools.cliWindow.menu"/>",disabled: !permission}]);
		}
	} else {
		var readMonitorPermission = !<s:property value="configView" /> || <s:property value="displayMonitorView" />;
		tItems.push([{text: "<s:text name="topology.menu.device.monitor"/>", disabled: !selectItem || !readMonitorPermission}]);
		if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE) {
			subItem2.push({text: "<s:text name="topology.menu.client.information"/>" , disabled: !selectItem});
		}
		if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48 && deviceModel!=MODEL_BR200) {
			subItem2.push({text: "<s:text name="topology.menu.neighbor.information"/>" , disabled: !selectItem});
			//subItem2.push({text: "<s:text name="topology.menu.acsp.neighbor.information"/>" , disabled: !selectItem});
		}
		if (subItem2.length>0){
			tItems.push(subItem2);
		}

		var subItem=[];
		subArray=[];
		subItem2=[];
		subArray.push("<s:text name="topology.menu.diagnostics.ping"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showlog"/>");
		if (deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P || deviceModel==MODEL_SR_48) {
			subArray.push("<s:text name="topology.menu.diagnostics.showfdb"/>");
		}

		subArray.push("<s:text name="topology.menu.diagnostics.showversion"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showrunningconfig"/>");
		subArray.push("<s:text name="topology.menu.diagnostics.showiproutes"/>");
		if (deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			subArray.push("<s:text name="topology.menu.diagnostics.showmacroutes"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showarpcache"/>");
		}

		if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			subArray.push("<s:text name="topology.menu.diagnostics.showroamingcache"/>");
		}
		if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceType== Device_TYPE_AP) {
			subArray.push("<s:text name="topology.menu.diagnostics.showdnxpneighbor"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showdnxpcache"/>");
		}
		if (deviceType!=Device_TYPE_VPN_GATEWAY && deviceType!= Device_TYPE_SWITCH) {
			subArray.push("<s:text name="topology.menu.diagnostics.showdhcpclientallocation"/>");
		}
		if (subArray.length>0){
			subItem.push(subArray);
		}

		subArray=[];
		if (deviceType!=Device_TYPE_AP && deviceType!=Device_TYPE_SWITCH) {
			subArray.push("<s:text name="topology.menu.diagnostics.showpathmtudiscovery"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showtcpmss"/>");
		}
		if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE) {
			 subArray.push("<s:text name="topology.menu.diagnostics.showamrptunnel"/>");
		}
		if (deviceType==Device_TYPE_AP) {
			 subArray.push("<s:text name="topology.menu.diagnostics.showvpngretunnel"/>");
		}
		if (deviceType!=Device_TYPE_SWITCH) {
			subArray.push("<s:text name="topology.menu.diagnostics.showvpnikeevent"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showvpnikesa"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showvpnipsecsa"/>");
			subArray.push("<s:text name="topology.menu.diagnostics.showvpnipsectunnel"/>");
		}
		if (subArray.length>0){
			subItem.push(subArray);
		}

		 subArray=[];
		 subArray.push("<s:text name="topology.menu.diagnostics.showcpu"/>");
		 subArray.push("<s:text name="topology.menu.diagnostics.showmemory"/>");
		if (deviceModel==MODEL_320 || deviceModel==MODEL_340) {
			subArray.push("<s:text name="topology.menu.diagnostics.showsystempower"/>");
		}
		if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE) {
			 subArray.push("<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>");
		}

		if (deviceModel==MODEL_BR200_LTE_VZ || deviceModel==MODEL_BR200_WP || deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_48 || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P) {
			 subArray.push("<s:text name="topology.menu.diagnostics.showpse"/>");
		 }
		 subItem.push(subArray);

		 subArray=[];
		 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subArray.push("<s:text name="topology.menu.troubleshoot.clientTrace"/>");
		 }
		 if (deviceType!=Device_TYPE_VPN_GATEWAY){
			 subArray.push("<s:text name="topology.menu.troubleshoot.vlan.probe"/>");
		 }
		 if (deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P  && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subArray.push("<s:text name="topology.menu.remoteSniffer"/>");
		 }

		 if (deviceType==Device_TYPE_BRANCH_ROUTER || deviceType==Device_TYPE_VPN_BR || deviceType==Device_TYPE_VPN_GATEWAY) {
			 subArray.push({text: "<s:text name="topology.menu.firewall.policy"/>", disabled: !permission || !selectItem});
		 }

		 if (deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P || deviceModel==MODEL_SR_48) {
			 //subArray.push("<s:text name="topology.menu.diagnostics.virtualCableTester"/>");
		 }
		 if (subArray.length>0){
			 subItem.push(subArray);
		 }

		 subItem2.push({text: "<s:text name="topology.menu.diagnostics"/>",submenu: {id: "diagnostics",itemdata:subItem}, disabled: !selectItem});

		 subItem=[];
		 subArray=[];
		 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subArray.push("<s:text name="topology.menu.statistics.acsp"/>");
		 }
		 subArray.push("<s:text name="topology.menu.statistics.interface"/>");
		 if (deviceModel!=MODEL_BR200 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subArray.push("<s:text name="topology.menu.statistics.summary"/>");
		 }
		 subItem2.push({text: "<s:text name="topology.menu.statistics"/>",submenu: {id: "statistics",itemdata:subArray}, disabled: !selectItem});

		 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE) {
			 subArray=[];
			 subArray.push("<s:text name="topology.menu.lldpcdp.clear"/>");
			 subArray.push("<s:text name="topology.menu.lldpcdp.showLldpPara"/>");
			 subArray.push("<s:text name="topology.menu.lldpcdp.showLldpNeighbor"/>");
			 subArray.push("<s:text name="topology.menu.lldpcdp.showCdpPara"/>");
			 subArray.push("<s:text name="topology.menu.lldpcdp.showCdpNeighbor"/>");
			 subItem2.push({text: "<s:text name="topology.menu.lldpcdp"/>",submenu: {id: "lldpcdp",itemdata:subArray}, disabled: !selectItem});
		 }

		 if (deviceType==Device_TYPE_AP && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE) {
			 subItem2.push({text: "<s:text name="topology.menu.alg.sip.name"/>", disabled: !selectItem});
		 }
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.configuration.audit"/>", disabled: !selectItem});
		 tItems.push(subItem2);

		 subItem2=[];
		 if (deviceModel==MODEL_BR200_LTE_VZ || deviceModel==MODEL_BR200_WP || deviceModel==MODEL_SR_24 || deviceModel==MODEL_SR_2124P || deviceModel==MODEL_SR_48 || deviceModel==MODEL_SR_2024P || deviceModel==MODEL_SR_2148P) {
		 	subItem2.push({text: "<s:text name="topology.menu.hiveAp.pse.reset"/>",disabled: !permission || !selectItem});
		 }
		 if (deviceModel==MODEL_BR200_LTE_VZ) {
			 	subItem2.push({text: "<s:text name="geneva_03.topology.menu.hiveAp.usbmodem.reset"/>",disabled: !permission || !selectItem});
		 }
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.reboot"/>",disabled: !permission || !selectItem});
	 	if((deviceInfos[deviceModel])[SPT_DEVICE_IMAGE_COUNTS] == 2){
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.invokeBackup"/>",disabled: !permission || !selectItem});
		}
		 if(deviceModel == MODEL_230){
			 subItem2.push({text: "<s:text name="topology.menu.hiveAp.turboModeToggle"/>",disabled: !permission || !selectItem});
		 }
		 if (deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subItem2.push({text: "<s:text name="topology.menu.hiveAp.locateAP"/>",disabled: !permission || !selectItem});
		 }
		 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 subItem2.push({text: "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>",disabled: !permission || !selectItem});
		 }
		 subItem2.push({text: "<s:text name="topology.menu.hiveAp.reset.default"/>",disabled: !permission || !selectItem});
		 if(deviceType== Device_TYPE_BRANCH_ROUTER || deviceType== Device_TYPE_VPN_BR){
			 subItem2.push({text: "<s:text name="topology.menu.hiveAp.disable.hiveui.cfg"/>",disabled: !permission || !selectItem});
		 }

		 tItems.push(subItem2);


		 tItems.push([{text: "<s:text name="topology.menu.hiveAp.alarm"/>",disabled: !selectItem}]);

		 subItem2=[];
		 subItem2.push({text: "<s:text name="topology.menu.ssh.web.client"/>",disabled: !permission || !selectItem});
		 subItem2.push({text: "<s:text name="topology.menu.ssh.proxy.client"/>",disabled: !permission || !selectItem});
		 tItems.push(subItem2);

		 //tItems.push([{text: "<s:text name="topology.menu.hiveAp.sshTunnel"/>",disabled: !permission || !selectItem}]);

		 if (showSegMenu) {
			 tItems.push([{text: "<s:text name="topology.menu.syncWithSGE"/>",disabled: !permission || !selectItem}]);
		 }

		 tItems.push([{text: "<s:text name="topology.menu.get.tech.file"/>",disabled: !permission}]);
		 if (deviceModel!=MODEL_BR100 && deviceModel!=MODEL_BR200 && deviceModel!=MODEL_AG20 && deviceModel!=MODEL_AG28
				 && deviceModel!=MODEL_320 && deviceModel!=MODEL_340 && deviceModel!=MODEL_VPN_GATEWAY && deviceModel!=MODEL_CVG_APPLIANCE && deviceModel!=MODEL_SR_24 && deviceModel!=MODEL_SR_2124P && deviceModel!=MODEL_SR_2024P && deviceModel!=MODEL_SR_2148P && deviceModel!=MODEL_SR_48) {
			 tItems.push([{text: "<s:text name="topology.menu.spectralAnalysis"/>",disabled: !permission || !selectItem}]);
		 }

		 if(visibleCliWindow){
			 tItems.push([{text: "<s:text name="hiveap.tools.cliWindow.menu"/>",disabled: !permission || !selectItem}]);
		}

	}

	 return tItems;
}


var tooltip = null;
function createTooltip(){
	var elements = document.getElementsByName("indication");
	if(elements.length < 1){
		return;
	}
	tooltip = new YAHOO.widget.Tooltip("ttDesc", {
		context:elements,
		width: "250px",
		autodismissdelay: 10000,
		showdelay: 100
	});
}

var lldpCdpPanel = null;
function createLldpCdpPanel() {
	var div = window.document.getElementById('lldpCdpPanel');
	lldpCdpPanel = new YAHOO.widget.Panel(div, { width:"380px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	lldpCdpPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(lldpCdpPanel);
	lldpCdpPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

var clientInfoPanel = null;
function createClientInfoPanel() {
	var div = window.document.getElementById('clientInfoPanel');
	clientInfoPanel = new YAHOO.widget.Panel(div, { width:"620px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	clientInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(clientInfoPanel);
	clientInfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

var neighborInfoPanel = null;
function createNeighborInfoPanel() {
	var div = window.document.getElementById('neighborInfoPanel');
	neighborInfoPanel = new YAHOO.widget.Panel(div, { width:"620px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	neighborInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(neighborInfoPanel);
	neighborInfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
var waitingPanel = null;
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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
	overlayManager.register(waitingPanel);
	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
var cliInfoPanel = null;
function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, { width:"800px", visible:false, fixedcenter:"contained", draggable:true, constraintoviewport:true } );
	cliInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(cliInfoPanel);
	cliInfoPanel.hideEvent.subscribe(resetParams);
	cliInfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

var cwpRemovePanel = null;
function createCwpRemovePanel() {
	var div = window.document.getElementById('cwpRemovePanel');
	cwpRemovePanel = new YAHOO.widget.Panel(div, { width:"600px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	cwpRemovePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(cwpRemovePanel);
	cwpRemovePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}


var imageBootPanel = null;
function createImageBootPanel(){
	var div = window.document.getElementById('imageBootPanel');
	imageBootPanel = new YAHOO.widget.Panel(div, { width:"310px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	imageBootPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(imageBootPanel);
	imageBootPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
var turboModeTogglePanel = null;
function createTurboModeTogglePanel(){
	var div = window.document.getElementById('turboModeTogglePanel');
	turboModeTogglePanel = new YAHOO.widget.Panel(div, { width:"340px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	turboModeTogglePanel.render(document.body);
	div.style.display = "";
	overlayManager.register(turboModeTogglePanel);
	turboModeTogglePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
<s:if test="%{showSyncSGEMenu}">
var syncSGEPanel = null;
function createSyncSGEPanel(){
	var div = window.document.getElementById('syncSGEPanel');
	syncSGEPanel = new YAHOO.widget.Panel(div, { width:"380px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	syncSGEPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(syncSGEPanel);
	syncSGEPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
</s:if>
// "click" event handler for update menu item
function onMenuItemClick(p_sType, p_aArgs, p_oValue) {
	// alert("menu item:" + this.cfg.getProperty("text") + ", operation:" + p_oValue);
	submitAction(p_oValue);
}
// "click" event handler for other menu item
function otherMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	//menu item is disabled, do nothing.
	if(menuItem.cfg.getProperty("disabled") == true){
	//	alert("This menu item is disabled.");
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	if (text == "<s:text name="topology.menu.diagnostics"/>"
		|| text == "<s:text name="topology.menu.statistics"/>"
		|| text == "<s:text name="topology.menu.hiveAp.updates"/>"
		|| text == "<s:text name="topology.menu.lldpcdp"/>"
		|| text == "<s:text name="topology.menu.hiveAp.deviceInventory"/>") {
		return;
	} else if (text == "<s:text name="topology.menu.hiveAp.reboot"/>") {
		openConfirmBootDialog(text);
	} else if (text == "<s:text name="topology.menu.hiveAp.reset.default"/>") {
		openConfirmBootDialog(text);
	} else if (text == "<s:text name="topology.menu.hiveAp.invokeBackup"/>") {
		openImageBootPanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.turboModeToggle"/>") {
		openTurboModeTogglePanel();
	} else if (text == "<s:text name="topology.menu.lldpcdp.clear"/>") {
		openLldpCdpPanel(text);
	} else if (text == "<s:text name="topology.menu.device.monitor"/>"){
		goDeviceMonitoring();
	} else if (text == "<s:text name="topology.menu.client.information"/>"){
		clearClientInfoTable();
		retrieveClientInfo();
	} else if (text == "<s:text name="topology.menu.neighbor.information"/>"){
		clearNeighborInfoTable();
		retrieveNeighborInfo();
	} else if (text == "<s:text name="topology.menu.hiveAp.alarm"/>"){
		retrieveAlarmInfo();
	} else if (text=="<s:text name="topology.menu.ssh.web.client"/>"){
		retrieveSshWebClient();
	} else if (text=="<s:text name="topology.menu.ssh.proxy.client"/>"){
		retrieveSshProxyClient();
	} else if (text == "<s:text name="topology.menu.hiveAp.configuration.audit"/>"){
		requestConfigurationAudit();
	} else if (text == "<s:text name="topology.menu.get.tech.file"/>"){
		requestTechFiles();
	} else if (text == "<s:text name="topology.menu.troubleshoot.clientTrace"/>"){
		openClientTracePanel();
	} else if (text == "<s:text name="topology.menu.troubleshoot.vlan.probe"/>"){
		openVlanProbePanel();
	} else if (text == "<s:text name="topology.menu.troubleshoot.path.probe"/>"){
		openPathProbePanel();
	} else if (text == "<s:text name="topology.menu.packetCapture"/>") {
		openPacketCapturePanel();
	} else if (text == "<s:text name="topology.menu.remoteSniffer"/>") {
		openRemoteSnifferPanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.sshTunnel"/>") {
		openSshTunnelPanel();
	} else if(text == "<s:text name="hiveap.tools.cliWindow.menu"/>") {
		openCLIWindow();
	} else if(text == "<s:text name="topology.menu.hiveAp.locateAP"/>") {
		openLocateAPPanel();
	} else if(text == "<s:text name="topology.menu.syncWithSGE"/>") {
		openSyncSGEPanel();
	} else if (text == "<s:text name="topology.menu.spectralAnalysis"/>"){
		checkSpectralAnalysis();
	} else if (text == "<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>") {
		openMulticastMonitorPanel();
	} else if (text == "<s:text name="topology.menu.firewall.policy"/>") {
		openFirewallPolicyPanel();
	} else if (text == "<s:text name="topology.menu.hiveAp.pse.reset"/>") {
		openConfirmResetPSEDialog(text);
	} else if (text == "<s:text name="geneva_03.topology.menu.hiveAp.usbmodem.reset"/>") {
		requestMultipleItemCli(text);
	} else if (text == "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>"){
		clearRadsecCredentials(text);
	//} else if (text == "<s:text name="geneva_08.hm.menu.add"/>"){
	//	openScanOverlay();
	//} else if (text == "<s:text name="button.remove"/>"){
	//	submitAction('remove');
	//} else if (text == "<s:text name="geneva_08.hm.menu.export"/>"){
	//	submitAction('exportDeviceInventory');
//	} else if (text == "<s:text name="topology.menu.hiveAp.deviceInventory"/>") {
//		var redirectorUrl = '<s:property value="redirectorServiceURL" escape="false"/>';
//		var newWin = window.open(redirectorUrl);
//		newWin.focus();
	} else if (text == "<s:text name="topology.menu.hiveAp.disable.hiveui.cfg"/>"){
		disableHiveUIConfig(text);
	} else{
		requestSingleItemCli(text);
	}
}

function resizeCliPanel(obj){
    var panelHeight = obj.height;
    resetPanelSize(panelHeight, this);
}

function checkIsSelectedOneItem()
{
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length != 1){
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return false;
	} else {
		document.getElementById(formName + "_hiveApId").value = selectedIds[0];
	}
	return true;
}

function checkMultiSelection() {
	var inputElements = document.getElementsByName('selectedIds');

	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no destination <s:text name='hiveAp.tag' />.");
		warnDialog.show();
		return false;
	}

	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one <s:text name='hiveAp.tag' />.");
		warnDialog.show();
		return false;
	}

	return true;
}

// fnr add

function openLldpCdpPanel(text)
{
	if (!checkIsSelectedOneItem())
	{
		return;
	}
	if(null == lldpCdpPanel){
		createLldpCdpPanel();
	}
	var div = window.document.getElementById('lldpCdpTitle');
	div.innerHTML = text;

	document.getElementById('chkLldp').checked=true;
	document.getElementById('chkCdp').checked=false;

	hm.util.hideFieldError();

    if (IE_SYNC) {
        // Keep the underlay and iframe size in sync.

        // You could also set the width property, to achieve the
        // same results, if you wanted to keep the panel's internal
        // width property in sync with the DOM width.
        lldpCdpPanel.sizeUnderlay();
        // Syncing the iframe can be expensive. Disable iframe if you
        // don't need it.
        lldpCdpPanel.syncIframe();
    }
	lldpCdpPanel.cfg.setProperty('visible', true);
}

function hideLldpCdpPanel()
{
	if(null != lldpCdpPanel){
		lldpCdpPanel.cfg.setProperty('visible', false);
	}
}

function requestLldpCdpAction()
{
	var text = window.document.getElementById('lldpCdpTitle').innerHTML;
	if (text=="<s:text name="topology.menu.lldpcdp.clear"/>") {
		document.forms['lldpCdpForm'].operation.value = "lldpcdpclear";
	}
	var selectedIds = hm.util.getSelectedIds();
	document.forms['lldpCdpForm'].hiveApId.value = selectedIds[0];
	var formObject = document.getElementById('mapNodes');
	YAHOO.util.Connect.setForm(formObject);

	var url = "<s:url action='mapNodes' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:lldpCdpCliInfoResult,failure:lldpCdpConnectedFailed,timeout: 100000}, null);
	if(waitingPanel != null)
	{
		waitingPanel.show();
	}

}

var lldpCdpCliInfoResult = function(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	// hm.util.clearSelection();
	hideLldpCdpPanel();

	eval("var response = " + o.responseText);
	if(response.result)
	{
		showInfoDialog(response.rspMessage.valueOf());
	}else {
		if(warnDialog != null) {
			warnDialog.cfg.setProperty('text', response.rspMessage.valueOf());
			warnDialog.show();
		}
	}
}

var lldpCdpConnectedFailed = function(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	// hm.util.clearSelection();
	hideLldpCdpPanel();
}


function clickLldpCdp(text,value) {
	var chkLldp = document.getElementById('chkLldp');
	var chkCdp = document.getElementById('chkCdp');
	if (text=="lldp") {
		if (value==false) {
			if (!chkCdp.checked) {
				chkCdp.checked=true;
			}
		}
	} else if (text=="cdp") {
		if (value==false) {
			if (!chkLldp.checked) {
				chkLldp.checked=true;
			}
		}
	}else {
		if (value==true) {
			document.getElementById(text).readOnly=false;
		}else {
			document.getElementById(text).readOnly=true;
		}
	}
}

//CWP directory removal feature
var cwpRemovalFormName = "cwpRemoval";
function hideCwpRemovePanel() {
	if(cwpRemovePanel != null){
		cwpRemovePanel.cfg.setProperty('visible', false);
	}
}

function openCwpRemovePanel() {
	if(null == cwpRemovePanel){
		createCwpRemovePanel();
	}
	if(cwpRemovePanel != null){
		hm.util.hideFieldError();
		var selectedIds = hm.util.getSelectedIds();
		if(selectedIds.length < 1){
			warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
			return;
		}
		var previousSelectedEl = getElementsByName_iefix("input", "allSelectedIds");
		if(selectedIds.length == 1){
			document.getElementById(cwpRemovalFormName + '_cwpDirectoryremoveAll').checked = true;
			document.getElementById(cwpRemovalFormName + '_cwpDirectoryremove').disabled = false;
			document.getElementById("cwpRemoveTitle").innerHTML = '<s:text name="hiveAp.update.cwp.remove"/>';
			disableDirs();
		}else{
			//multiple items
			document.getElementById(cwpRemovalFormName + '_cwpDirectoryremove').disabled = true;
			document.getElementById(cwpRemovalFormName + '_cwpDirectoryremoveAll').checked = true;
			document.getElementById("cwpRemoveTitle").innerHTML = '<s:text name="hiveAp.update.cwp.remove"/>';
			hideCwpDirectories();
		}
		createSelectedIds(selectedIds, previousSelectedEl);
		cwpRemovePanel.cfg.setProperty('visible', true);
	}
}

function getElementsByName_iefix(tag, name) {
     var elem = document.forms[cwpRemovalFormName].getElementsByTagName(tag);
     var arr = new Array();
     for(i = 0,iarr = 0; i < elem.length; i++) {
          att = elem[i].getAttribute("name");
          if(att == name) {
                arr[iarr] = elem[i];
               iarr++;
          }
     }
     return arr;
}

function createSelectedIds(selectedIds, previousEl){
	var parent = document.getElementById('cwpRemoval');
	for(var i=0; i<previousEl.length; i++){
		parent.removeChild(previousEl[i]);
	}
	for(var i =0; i< selectedIds.length; i++){
		var hiddenEl = document.createElement('input');
		hiddenEl.type = 'hidden';
		hiddenEl.name = 'allSelectedIds';
		hiddenEl.value = selectedIds[i];
		parent.appendChild(hiddenEl);
	}
}

function hideCwpDirectories(){
	var cwpDirectories = document.getElementById('cwpDirectories');
//	while (cwpDirectories.firstChild != null) {
//		cwpDirectories.removeChild(cwpDirectories.firstChild);
//	}
	cwpDirectories.style.display = 'none';
}

function changeFirstLetterToUpper(letter){
	try{
		var firstLetter = letter.substring(0,1).toUpperCase();
		return firstLetter + letter.substring(1);
	} catch(e) {
		return letter;
	}
}

function createCwpDirectories(dirs){
	var cwpDirectories = document.getElementById('cwpDirectories');
	cwpDirectories.style.display = '';
	var head = "<table border='0' cellspacing='0' cellpadding='2px'>";
	var foot = "</table>";
	var body = "";
	var dirExist = false;
	if(dirs != undefined){
		body += "<tr><td></td><td class='panelLabel'>Web Page Directories</td><td class='panelLabel'>SSIDs / Interfaces</td></tr>";
		body += "<tr><td>- - -</td><td>- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -</td><td>- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -</td></tr>";
		for(var dir=0; dir < dirs.length; dir++){
			if(dirs[dir].ssids == undefined){
				body += "<tr><td><input type='checkbox' name='selectedDirs' value='" + dirs[dir].name + "'/> </td><td>" + changeFirstLetterToUpper(dirs[dir].name) + " </td></tr>";
			}else{
				body += "<tr><td><input type='checkbox' name='selectedDirs' value='" + dirs[dir].name + "'/> </td><td>" + changeFirstLetterToUpper(dirs[dir].name) + " </td><td>" + dirs[dir].ssids[0] + "</td></tr>";
				for(var i = 1; i< dirs[dir].ssids.length; i++){
					body += "<tr><td></td><td></td><td>" + dirs[dir].ssids[i] + "</td></tr>";
				}
			}
		}
		if(dirs.length > 0){
			dirExist = true;
		}
	}
	cwpDirectories.innerHTML = dirExist ? ("<fieldset>" + head + body + foot + "</fieldset>") : (head + body + foot);
}

function disableDirs(){
	document.getElementById("cwpDirectories").style.display="none";
}

function retrieveDirs(){
	document.forms[cwpRemovalFormName].operation.value = 'retrieveCwpDirectory';
	var formObject = document.forms[cwpRemovalFormName];
	YAHOO.util.Connect.setForm(formObject);

	var url = "<s:url action='mapNodes' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:cwpDirectoryResult,failure:connectedFailed,timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function submitCwpRemovePanel(){
	document.forms[cwpRemovalFormName].operation.value = 'removeCwpDirectory';
	var formObject = document.forms[cwpRemovalFormName];
	YAHOO.util.Connect.setForm(formObject);

	var url = "<s:url action='mapNodes' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:removeCwpDirectoryResult,failure:connectedFailed,timeout: 60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var removeCwpDirectoryResult = function(o) {
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.suc){
		showInfoDialog('<s:text name="info.cwp.direcotry.remove.success" />');

		//set removeAll button selected;
		//document.getElementById(cwpRemovalFormName + '_cwpDirectoryremove').checked = true;
		if(!result.notRetrieve) {
			retrieveDirs();
		}
	}
	if (result.msg){
		var reportEl = document.getElementById("cwpRemoveLabel");
		hm.util.reportFieldError(reportEl, result.msg);
	}
}

var cwpDirectoryResult = function(o) {
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.h){
		var dialogTitleDiv = document.getElementById("cwpRemoveTitle");
		dialogTitleDiv.innerHTML = '<s:text name="hiveAp.update.cwp.remove"/>' + ' - ' + result.h;
	}
	if (result.msg){
		var reportEl = document.getElementById("cwpRemoveLabel");
		hm.util.reportFieldError(reportEl, result.msg);
	}
	createCwpDirectories(result.dirs);
}

//CWP directory removal feature end
<s:if test="%{showSyncSGEMenu}">
function openSyncSGEPanel(){
	if(null == syncSGEPanel){
		createSyncSGEPanel();
	}
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	syncSGEPanel.pollingCount = 0;
	syncSGEPanel.totalCount = duration / 10;
	var table = document.getElementById("syncSGETable");
	for(var i=1, length = table.rows.length; i<length; i++){
		table.rows[i].cells[1].innerHTML = "<span>&nbsp;</span>";
	}
	hm.util.hide('syncSGE_message');
	var url = "<s:url action='hiveAp' includeParams='none' />?operation=syncSGE&ignore=" + new Date().getTime();
	ajaxRequest(null, url, processSyncSGEResult, "post");
	syncSGEPanel.show();
}

function processSyncSGEResult(o){
	eval("var result = " + o.responseText);
	if(result.error){
		// show error message;
		document.getElementById("syncSGE_message").innerHTML = result.error;
		hm.util.show('syncSGE_message');
		syncSGEPanel.messageTimeoutId = setTimeout("hm.util.hide('syncSGE_message');", 15000);
	}else{
		// fetch progress interval...
		var url = "<s:url action='hiveAp' includeParams='none' />?operation=fetchSGEProgress&ignore=" + new Date().getTime();
		setTimeout("ajaxRequest(null, '"+url+"', processSyncSGEProgressResult, 'post')", 1000);
	}
}

function processSyncSGEProgressResult(o){
	eval("var result = " + o.responseText);
	if(result.data){
		var table = document.getElementById("syncSGETable");
		for(var i=1, length = table.rows.length; i<length; i++){
			table.rows[i].cells[1].innerHTML = result.data.length>i-1?result.data[i-1]:"<span>&nbsp;</span>";
		}
	}
	if(result.msg){
		document.getElementById("syncSGE_message").innerHTML = result.msg;
		hm.util.show('syncSGE_message');
		if(syncSGEPanel.messageTimeoutId){
			clearTimeout(syncSGEPanel.messageTimeoutId);
		}
		syncSGEPanel.messageTimeoutId = setTimeout("hm.util.hide('syncSGE_message');", 15000);
	}
	// clear progress timer
	if(syncSGEPanel.progressTimeoutId){
		clearTimeout(syncSGEPanel.progressTimeoutId);
	}
	// request progress only if the panel show up
	if(syncSGEPanel.cfg.getProperty("visible") && (syncSGEPanel.pollingCount++ < syncSGEPanel.totalCount)){
		var url = "<s:url action='hiveAp' includeParams='none' />?operation=fetchSGEProgress&ignore=" + new Date().getTime();
		syncSGEPanel.progressTimeoutId = setTimeout("ajaxRequest(null, '"+url+"', processSyncSGEProgressResult, 'post')", 10000);
	}
}
</s:if>
// Turbo Mode Toggle feature
function openTurboModeTogglePanel(){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	} else {
		if(null == turboModeTogglePanel){
			createTurboModeTogglePanel();
		}
		if(null != turboModeTogglePanel){
			document.getElementById('turboModeToggleoff').checked = true;
			turboModeTogglePanel.show();
		}
	} 
}

function hideTurboModeTogglePanel(){
	if(null != turboModeTogglePanel){
		turboModeTogglePanel.hide();
	}
}

function submitTurboModeToggle() {
	var value = document.getElementById('turboModeToggleon').checked? "on" : "off";
	doAjaxRequest("requestMultipleItemCli", "<s:text name="topology.menu.hiveAp.turboModeToggle"/>", cliInfoResult,"value1="+value);
	hideTurboModeTogglePanel();
}

//set image to boot feature
function openImageBootPanel(){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	} else{
		document.getElementById(formName + "_hiveApId").value = selectedIds[0];
		doAjaxRequest("fetchImageVer", "<s:text name="topology.menu.hiveAp.fetchImageVer"/>", fetchImageVerResult);
	}
	/* } else {
		if(null == imageBootPanel){
			createImageBootPanel();
		}
		if(null != imageBootPanel){
			document.getElementById('imageBootcurrent').checked = true;
			$("#imageBootPanelMsg").html("");
			$("#imageBootcurrent").next().html('<s:text name="topology.menu.hiveAp.invokeBackup.currentText" />');
			$("#imageBootbackup").next().html('<s:text name="topology.menu.hiveAp.invokeBackup.backupText" />');
			imageBootPanel.show();
		}
	} */
}

function fetchImageVerResult(o){
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(null == imageBootPanel){
		createImageBootPanel();
	}
	if(null != imageBootPanel){
		var imageBootPanelMsg = $("#imageBootPanelMsg");
		var imageBootcurrent_for = $("#imageBootcurrent").next();
		var imageBootbackup_for = $("#imageBootbackup").next();
		var imageBootPanelSubmit = $("#imageBootPanelSubmit");
		if(result.isConnected){
			imageBootPanelSubmit.attr("disabled",false);
			if(result.v){
				imageBootPanelMsg.html(result.v);
				imageBootcurrent_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.currentText" />');
				imageBootbackup_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.backupText" />');
			} else {
				imageBootPanelMsg.html("");
				if(result.currentVer && ""!=result.currentVer){
					imageBootcurrent_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.currentTextWithVer"><s:param>'+result.currentVer+'</s:param></s:text>');
				} else {
					imageBootcurrent_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.currentText" />');
				}
				if(result.backupVer && ""!=result.backupVer){
					imageBootbackup_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.backupTextWithVer"><s:param>'+result.backupVer+'</s:param></s:text>');
				} else {
					imageBootbackup_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.backupText" />');
				}
			}
		} else {
			imageBootPanelSubmit.attr("disabled",true);
			if(result.v){
				imageBootPanelMsg.html(result.v);
				imageBootcurrent_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.currentText" />');
				imageBootbackup_for.html('<s:text name="topology.menu.hiveAp.invokeBackup.backupText" />');
			}
		}
		
		document.getElementById('imageBootcurrent').checked = true;
		imageBootPanel.show();
	}
}

function hideImageBootPanel(){
	if(null != imageBootPanel){
		imageBootPanel.hide();
	}
}

//end

//locate ap feature
var locateAPPanel = null;
function createLocateAPPanel(){
	var div = window.document.getElementById('locateAPPanel');
	locateAPPanel = new YAHOO.widget.Panel(div, { width:"400px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	locateAPPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(locateAPPanel);
	locateAPPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openLocateAPPanel()
{
	if (!checkIsSelectedOneItem())
	{
		return;
	}

	doAjaxRequest("requestHiveAPModel", "", requestHiveAPModelResult);
}

function requestHiveAPModelResult(o)
{
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	if (result.model)
	{
		if(null == locateAPPanel){
			createLocateAPPanel();
		}

		// hide some color
		//var model = result.model;
		//var modelStr = result.modelStr;
		if (SUPPORTED_LOCATE.contains(result.model))
		{
			var selectList = document.getElementById("locateAPColorMore");
			var green = document.createElement("option");
			green.text = "Green";
			green.value = "green";
			green.style.backgroundColor = "#00FF00"

			var red = document.createElement("option");
			red.text = "Red";
			red.value = "red";
			red.style.backgroundColor = "#FF0000"

			var yellow = document.createElement("option");
			yellow.text = "Yellow";
			yellow.value = "yellow";
			yellow.style.backgroundColor = "#FFFF00";

			var blue = document.createElement("option");
			blue.text = "Blue";
			blue.value = "blue";
			blue.style.backgroundColor = "#0000FF";

			var white = document.createElement("option");
			white.text = "White";
			white.value = "white";
			white.style.backgroundColor = "#FFFFFF";

			var orange = document.createElement("option");
			orange.text = "Orange";
			orange.value = "orange";
			orange.style.backgroundColor = "#FF7F00";

			var purple = document.createElement("option");
			purple.text = "Purple";
			purple.value = "purple";
			purple.style.backgroundColor = "#9F00C5";

			var amber = document.createElement("option");
			amber.text = "Amber";
			amber.value = "amber";
			amber.style.backgroundColor = "#FFBF00"

			var off = document.createElement("option");
			off.text = "Off";
			off.value = "off";
			off.style.backgroundColor = "#808080";

			switch(result.model){

				case MODEL_121:
				case MODEL_141:
				case MODEL_230:
				case MODEL_370:
				case MODEL_390:
				case MODEL_BR100:
				case MODEL_BR200:
				case MODEL_BR200_WP:
				case MODEL_BR200_LTE_VZ:{
					removeAllOptions(selectList);
					try{
						selectList.add(amber,null);
						selectList.add(white,null);
						selectList.add(off,null);
					}catch(e){
						selectList.add(amber);
						selectList.add(white);
						selectList.add(off);
					}
					break;
				}

				case MODEL_320:
				case MODEL_340:{

					removeAllOptions(selectList);
					try{
						selectList.add(green,null);
						selectList.add(red,null);
						selectList.add(orange,null);
						selectList.add(off,null);
					}catch(e){
						selectList.add(green);
						selectList.add(red);
						selectList.add(orange);
						selectList.add(off);
					}
					break;
				}

				case MODEL_110:
				case MODEL_120:
				case MODEL_330:
				case MODEL_350:{
					removeAllOptions(selectList);
					try{
						selectList.add(green,null);
						selectList.add(orange,null);
						selectList.add(yellow,null);
						selectList.add(blue,null);
						selectList.add(purple,null);
						selectList.add(white,null);
						selectList.add(off,null);
					}catch(e){
						selectList.add(green);
						selectList.add(orange);
						selectList.add(yellow);
						selectList.add(blue);
						selectList.add(purple);
						selectList.add(white);
						selectList.add(off);
					}
					break;
				}

				default:
					removeAllOptions(selectList);
					try{
						selectList.add(green,null);
						selectList.add(red,null);
						selectList.add(yellow,null);
						selectList.add(blue,null);
						selectList.add(purple,null);
						selectList.add(white,null);
						selectList.add(off,null);
					}catch(e){
						selectList.add(green);
						selectList.add(red);
						selectList.add(yellow);
						selectList.add(blue);
						selectList.add(purple);
						selectList.add(white);
						selectList.add(off);
					}
					break;
			}


			// show or hide
			document.getElementById('noLedSection').style.display="";
			document.getElementById('locateAPColorMore').style.display="";
			document.getElementById('locateAPColorLess').style.display="none";

			// init value
			document.getElementById('noLed').checked = false;
			selectNoLed(false);
			//document.getElementById('locateAPColorMore').options[0].selected=true;
			document.getElementById('locateAPBlink').options[0].selected=true;

			locateAPColorMoreChange();
			locateAPColorLessChange();
		} else {
			// in 3.4r3, locate hiveap only avaliable in ap 100 series.
			warnDialog.cfg.setProperty('text', "Locate Device is unavailable on the "+result.modelStr);
			warnDialog.show();
			return;

			/**
			// show or hide
			document.getElementById('noLedSection').style.display="none";
			document.getElementById('locateAPColorMore').style.display="none";
			document.getElementById('locateAPColorLess').style.display="";

			// init value
			document.getElementById('locateAPColorLess').options[0].selected=true;
			document.getElementById('locateAPBlink').options[0].selected=true;
			**/
		}

		if(null != locateAPPanel){
			locateAPPanel.show();
		}
	}
	else if(result.e){
		warnDialog.cfg.setProperty('text', result.e);
		warnDialog.show();
	}
	else {
		warnDialog.cfg.setProperty('text', "Unable to determine the device model.");
		warnDialog.show();
	}
}
function removeAllOptions(data){
	for(var i = 0 ; i < data.options.length;){
		data.remove(data.options[i]);
	}
}
function hideLocateAPPanel(){
	if(null != locateAPPanel){
		locateAPPanel.hide();
	}
}

function selectNoLed(checked)
{
	if (checked) {
		document.getElementById('locateAPColorMore').disabled = true;
		document.getElementById('locateAPBlink').disabled = true;
	} else {
		document.getElementById('locateAPColorMore').disabled = false;
		if(document.getElementById('locateAPColorMore').value != "off"){
			document.getElementById('locateAPBlink').disabled = false;
		}
	}
}

function submitLocateAP() {

	var color;
	var blink;
	var noled = document.getElementById('noLed').checked;
	if (!noled) {
		if (document.getElementById('locateAPColorMore').style.display != "none")
		{
			color = document.getElementById('locateAPColorMore').value;
		} else {
			color = document.getElementById('locateAPColorLess').value;
		}
		blink = document.getElementById('locateAPBlink').value;
	}

	doAjaxRequest("controlLedOfAP", "", controlLedResult, "noLed="+noled+"&ledColor="+color+"&ledBlink="+blink);
}

function controlLedResult(o)
{
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	if (result.msg) {
		warnDialog.cfg.setProperty('text', result.msg);
		warnDialog.show();
	} else {
		showInfoDialog("The LED status was updated successfully.");
	}
}

//locate ap feature---end

function resetPanelSize(panelHeight, panel){
    var headerHeight = panel.header.offsetHeight; // Content + Padding + Border
    var footerHeight = panel.footer.offsetHeight; // Content + Padding + Border
    var headerWidth = panel.header.offsetWidth; // Content + Padding + Border;
    var bodyTopHeight = YAHOO.util.Dom.get('bd_top').offsetHeight;

    var bodyHeight = (panelHeight - headerHeight - footerHeight);
    var bodyContentHeight = (IE_QUIRKS) ? bodyHeight-bodyTopHeight : bodyHeight - 20 - bodyTopHeight;

    YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('cli_viewer'), 'height', bodyContentHeight + 'px');

    if (IE_SYNC) {
	    YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get('cli_viewer'), 'width', (headerWidth - 24)  + 'px');

        // Keep the underlay and iframe size in sync.

        // You could also set the width property, to achieve the
        // same results, if you wanted to keep the panel's internal
        // width property in sync with the DOM width.

        panel.sizeUnderlay();

        // Syncing the iframe can be expensive. Disable iframe if you
        // don't need it.

        panel.syncIframe();
    }
}

function resetParams(){
	//alert("reset params...");
	var interfaceEl = document.getElementById("interfaceType");
	var callIdEl = document.getElementById("callId");
	interfaceEl.selectedIndex = 0;
	callIdEl.value = "";
}

function refreshHiveAPListPage(){
	submitAction('refreshAfterResetConfig');
}

function retrieveClientInfo(){
	//if (!checkIsSelectedOneItem()){
	//	return;
	//}
	//doAjaxRequest("retrieveClientInfo", "", clientInfoResult);
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	thisOperation = 'syncClientsFromAPs';
	if (selectedIds.length > 5)
	{
		confirmDialog.cfg.setProperty('text', "<html><body>The operation might take up to several minutes.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	} else {
		doContinueOper();
	}
}

function clientFirstPage() {
	clearClientInfoTable();
	url = "<s:url action='mapNodes' includeParams='none' />?operation=clientFirstPage&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 10000});
}

function clientPreviousPage() {
	clearClientInfoTable();
	url = "<s:url action='mapNodes' includeParams='none' />?operation=clientPreviousPage&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 10000});
}

function clientNextPage() {
	clearClientInfoTable();
	url = "<s:url action='mapNodes' includeParams='none' />?operation=clientNextPage&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 10000});
}

function clientLastPage() {
	clearClientInfoTable();
	url = "<s:url action='mapNodes' includeParams='none' />?operation=clientLastPage&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 10000});
}

function clientGotoPage() {
	clearClientInfoTable();
	var pageIndex = document.getElementById("clientGotoPage").value;
	url = "<s:url action='mapNodes' includeParams='none' />?operation=clientGotoPage&pageIndex="+pageIndex+"&ignore=" + new Date().getTime();
	YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 10000});
}

function retrieveNeighborInfo(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	doAjaxRequest("retrieveNeighborInfo", "", neighborInfoResult);
}

function requestSingleItemCli(menuText){
	if (!checkIsSelectedOneItem()){
		return;
	}
	doAjaxRequest("requestSingleItemCli", menuText, cliInfoResult);
}

function requestMultipleItemCli(menuText){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	doAjaxRequest("requestMultipleItemCli", menuText, cliInfoResult);
}

function clearRadsecCredentials(menuText){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	thisOperation = menuText;
	var message;
	if(selectedIds.length == 1){
		message = '<s:text name="topology.menu.hiveAp.clear.radsec.credentials.message"><s:param>device</s:param></s:text>';
	} else {
		message = '<s:text name="topology.menu.hiveAp.clear.radsec.credentials.message"><s:param>devices</s:param></s:text>';
	}
	confirmDialog.cfg.setProperty('text', message);
	confirmDialog.show();
}

function disableHiveUIConfig(menuText){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	doAjaxRequest("disableHiveUIConfig", menuText, cliInfoResult);
}

/**
var clearRadsecResult = function(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	if(result.v == null || result.v == ""){
		showInfoDialog("<s:text name="info.hiveAp.clear.cloudauthca.result"/>");
	}else{
		showInfoDialog(result.v);
	}
}
*/

function requestConfigurationAudit(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	doAjaxRequest("configurationAudit", "", cliInfoResult);
}

function requestMismatchAudit(hiveApId){
	if(waitingPanel != null){
		waitingPanel.setHeader("Retrieving Information...");
	}
	document.getElementById(formName + "_hiveApId").value = hiveApId;
	doAjaxRequest("configurationAudit", "", cliInfoResult);
}

function requestTechFiles(){
	document.forms[formName].operation.value = "requestTech";
	var listType='<s:property value="%{listType}"/>';
	var url = "<s:url action='mapNodes' includeParams='none' />?parentOperation="+listType+"&ignore="+new Date().getTime();
	document.forms[formName].action = url;
	document.forms[formName].submit();
	url = "<s:url action='hiveAp' includeParams='none' />";
	document.forms[formName].action = url;
}

function checkSpectralAnalysis(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	submitAction("startSpectrumAnalysis");
}

function startSpectralAnalysis() {
    var hiveApId = document.getElementById(formName + "_hiveApId").value;
	var url = '<s:url action="mapNodes" includeParams="none"></s:url>' + "?operation=fetchHiveApInterfaceInfo&hiveApId="+hiveApId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessApIf, failure : resultDoNothing, timeout: 60000 }, null);
}

var resultDoNothing = function(o) {
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
}

function spectralAnalysisSupCheck(){
    var hiveApId = document.getElementById(formName + "_hiveApId").value;
	var url = '<s:url action="mapNodes" includeParams="none"></s:url>' + "?operation=spectralAnalysisSupCheck&hiveApId="+hiveApId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : spectralAnalysisSupResult, failure : resultDoNothing, timeout: 60000 }, null);
}

function spectralAnalysisSupResult(o){
	eval("var details = " + o.responseText);
	var v = details.v;
	if (v==0) {
		if(waitingPanel != null) {
			waitingPanel.hide();
		}
		warnDialog.cfg.setProperty('text', details.m);
		warnDialog.show();
	} else if (v==2){
        confirmDialog.cfg.setProperty('text', "<html><body>Performing spectrum analysis on the selected device will affect performance, and if WIPS is enabled no spectrum analysis will be performed.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	} else{
        confirmDialog.cfg.setProperty('text', "<html><body>Performing spectrum analysis on the selected device will affect performance.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

var detailsSuccessApIf = function (o) {
	eval("var details = " + o.responseText);
	var v = details.v;
	if (v==0) {
		if(waitingPanel != null) {
			waitingPanel.hide();
		}
		warnDialog.cfg.setProperty('text', details.m);
		warnDialog.show();
	} else {
		var runAP=document.getElementById(formName + "_hiveApId").value;;
		var runInterface=v;
		var runChannelWifi0="";
		var runChannelWifi1="";
		if (v==1) {
			runChannelWifi0="1-13";
			if (details.w0){
				runChannelWifi0="1-11";
			}
		} else {
			runChannelWifi1="36-165";
		}
		var runInterval=1;
		var runTime=5;
		var url = '<s:url action="spectralAnalysis" includeParams="none"></s:url>' + "?operation=updateSettingsParamsFromAp&runAP="+runAP + "&runInterface="+runInterface +"&runChannelWifi0="+runChannelWifi0 + "&runChannelWifi1="+runChannelWifi1 +"&runInterval="+runInterval +"&runTime="+runTime + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : detailsSuccessIf, failure:resultDoNothing, timeout: 120000 }, null);
		if (waitingPanel!=null) {
			waitingPanel.show();
		}
	}
}

var detailsSuccessIf = function(o) {
	eval("var details = " + o.responseText);
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
	var s = details.s;
	var err = details.e;
	if (s) {
	 	var hiveApId = document.getElementById(formName + "_hiveApId").value;
		window.location.href = 'spectralAnalysis.action?operation=view&id='+hiveApId;
	} else {
		if (err) {
			warnDialog.cfg.setProperty('text', err);
			warnDialog.show();
		}
	}

};

function launchSpectrumAnalysis(vid) {
	window.location.href = 'spectralAnalysis.action?operation=view&id='+vid;
}
function sipCalls(){
	var callId = document.getElementById("callId");
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, "value1="+callId.value);
	document.getElementById("cli_viewer").innerHTML = '';
}

function retrieveIfDetail() {
	var interfaceElement = document.getElementById("interfaceType");
	var item = interfaceElement.options[interfaceElement.selectedIndex].text;
	var interfaceList = new Array();
	for(var i=0;i<interfaceElement.options.length;i++){
		interfaceList[interfaceList.length]=interfaceElement.options[i].value;
	}
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, "value1="+item+"&value2="+interfaceList);
	document.getElementById("cli_viewer").innerHTML = '';
}

function ping() {
	var targetIpElement = document.getElementById("targetIp");
	if (targetIpElement.value.length == 0) {
        hm.util.reportFieldError(targetIpElement, '<s:text name="error.requiredField"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
        targetIpElement.focus();
        return;
  	}
   	if (! hm.util.validateIpAddress(targetIpElement.value)) {
		hm.util.reportFieldError(targetIpElement, '<s:text name="error.formatInvalid"><s:param><s:text name="topology.menu.diagnostics.ping.name" /></s:param></s:text>');
		targetIpElement.focus();
		return;
	}
	doAjaxRequest("requestSingleItemCli", "", cliInfoResult, "value1="+targetIpElement.value);
	document.getElementById("cli_viewer").innerHTML = '';
}

function retrieveSshWebClient(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	var hiveApId = document.getElementById(formName + "_hiveApId").value;
	var url="<s:url action='sshWebClient' includeParams='none'/>" + "?operation=webSsh&hiveApId=" + hiveApId;
	window.open(url,"","scrollbars=yes,width=750px,height=600px,resizable=yes,top=150,left=250");
}

function retrieveSshProxyClient(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	var hiveApId = document.getElementById(formName + "_hiveApId").value;
	var url="<s:url action='sshClient' includeParams='none'/>" + "?operation=sshConfig&hiveApId=" + hiveApId;
	window.open(url,"","scrollbars=yes,width=600px,height=360px,resizable=yes,top=150,left=250");
}

function goDeviceMonitoring(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	var deviceMonitoringUrl = "<s:url action="hiveAp" includeParams='none'/>?operation=showHiveApDetails&id="+document.getElementById(formName + "_hiveApId").value + "&ignore=" + new Date().getTime();
	document.location.href = deviceMonitoringUrl;
}

function retrieveAlarmInfo(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	doAjaxRequest("alarm", "", alarmInfo);
}

var alarmInfo = function(o) {
	eval("var result = " + o.responseText);
	var redirect_url = "<s:url action='alarms' includeParams='none' />" + "?operation=search&apId=" + result.v + "&ignore=" + new Date().getTime();
	document.location.href = redirect_url;
}

function doAjaxRequest(operation, menuText, callback, postData ){
	document.forms[formName].operation.value = operation;
	if(menuText != undefined && menuText != ""){
		document.forms[formName].menuText.value = menuText;
	}
	YAHOO.util.Connect.setForm(document.forms[formName]);
	url = "<s:url action='mapNodes' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 300000}, postData);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function doAjaxRequestHiveApAction(operation, callback, postData ){
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.forms[formName]);
	url = "<s:url action='hiveAp' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 300000}, postData);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

/***
function rebootDevices(){
	document.forms[formName].operation.value = "rebootHiveAPs";
	YAHOO.util.Connect.setForm(document.forms[formName]);
	url = "<s:url action='hiveAp' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:connectedFailed,failure:connectedFailed,timeout: 60000});
	if(waitingPanel != null){
		waitingPanel.show();
	}
}
**/

var _abgModelItems = ['all','wifi0','wifi1','eth0','mgt0','wifi0.1','wifi0.2',
					 'wifi0.3','wifi0.4','wifi0.5','wifi0.6','wifi0.7','wifi1.1',
					 'wifi1.2','wifi1.3','wifi1.4','wifi1.5','wifi1.6','wifi1.7'];
var _11nModelItems = ['all','wifi0','wifi1','eth0','eth1','mgt0','agg0','red0',
					 'wifi0.1','wifi0.2','wifi0.3','wifi0.4','wifi0.5','wifi0.6',
					 'wifi0.7','wifi0.8','wifi0.9','wifi0.10','wifi0.11','wifi0.12',
					 'wifi0.13','wifi0.14','wifi0.15','wifi0.16',
					 'wifi1.1','wifi1.2','wifi1.3','wifi1.4','wifi1.5','wifi1.6',
					 'wifi1.7','wifi1.8','wifi1.9','wifi1.10','wifi1.11','wifi1.12',
					 'wifi1.13','wifi1.14','wifi1.15','wifi1.16'];
var _11nModelOneRaidoSimpleItems = ['all','wifi0','eth0','eth1','mgt0','agg0','red0',
					 'wifi0.1','wifi0.2','wifi0.3','wifi0.4','wifi0.5','wifi0.6',
					 'wifi0.7','wifi0.8'];
var _11nModelOneRadioItems = ['all','wifi0','eth0','eth1','mgt0','agg0','red0',
					 'wifi0.1','wifi0.2','wifi0.3','wifi0.4','wifi0.5','wifi0.6',
					 'wifi0.7','wifi0.8','wifi0.9','wifi0.10','wifi0.11','wifi0.12',
					 'wifi0.13','wifi0.14','wifi0.15','wifi0.16'];
var _11nNoRadioItems = ['all','eth0','eth1','mgt0','agg0','red0'];

var _11nCvgItems = ['all'];

var updateInterfaceItems = function(is11n, radioDsType,interfacelist){
	var interfaceEl = document.getElementById("interfaceType");
	if(interfacelist == "undefined" || interfacelist==null){
		interfacelist = ["all"];
	}
	interfaceEl.length = interfacelist.length;
	for(var i=0; i<interfacelist.length; i++){
		interfaceEl.options[i].value = interfacelist[i];
		interfaceEl.options[i].text = interfacelist[i];
	}
	/* if(is11n){// the ap is 11n model
		//radioDsType 1: 8 one radio wifi, 2: 16 one radio wifi, 3: no wifi 4:cvg
		if (radioDsType==1) {
			interfaceEl.length = _11nModelOneRaidoSimpleItems.length;
			for(var i=0; i<_11nModelOneRaidoSimpleItems.length; i++){
				interfaceEl.options[i].value = _11nModelOneRaidoSimpleItems[i];
				interfaceEl.options[i].text = _11nModelOneRaidoSimpleItems[i];
			}
		} else if (radioDsType==2) {
			interfaceEl.length = _11nModelOneRadioItems.length;
			for(var i=0; i<_11nModelOneRadioItems.length; i++){
				interfaceEl.options[i].value = _11nModelOneRadioItems[i];
				interfaceEl.options[i].text = _11nModelOneRadioItems[i];
			}
		} else if (radioDsType==3) {
			interfaceEl.length = _11nNoRadioItems.length;
			for(var i=0; i<_11nNoRadioItems.length; i++){
				interfaceEl.options[i].value = _11nNoRadioItems[i];
				interfaceEl.options[i].text = _11nNoRadioItems[i];
			}
		} else {
			interfaceEl.length = _11nModelItems.length;
			for(var i=0; i<_11nModelItems.length; i++){
				interfaceEl.options[i].value = _11nModelItems[i];
				interfaceEl.options[i].text = _11nModelItems[i];
			}
		}
	}else{
		if (radioDsType==4) {
			interfaceEl.length = _11nCvgItems.length;
			for(var i=0; i<_11nCvgItems.length; i++){
				interfaceEl.options[i].value = _11nCvgItems[i];
				interfaceEl.options[i].text = _11nCvgItems[i];
			}
		} else {
			interfaceEl.length = _abgModelItems.length;
			for(var i=0; i<_abgModelItems.length; i++){
				interfaceEl.options[i].value = _abgModelItems[i];
				interfaceEl.options[i].text = _abgModelItems[i];
			}
		}
	} */
}

var cliInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	var pingTr = document.getElementById("pingTr");
	var interfaceTr = document.getElementById("interfaceTr");
	var sipTr = document.getElementById("sipTr");
	if("<s:text name="topology.menu.statistics.interface"/>" == result.t){
		pingTr.style.display = "none";
		sipTr.style.display = "none";
		interfaceTr.style.display = "";
		updateInterfaceItems(result.is11n, result.radioDsType,result.interfacelist);
	}else if ("<s:text name="topology.menu.diagnostics.ping"/>" == result.t) {
		pingTr.style.display = "";
		interfaceTr.style.display = "none";
		sipTr.style.display = "none";
		if(result.ip){
			document.getElementById("targetIp").value = result.ip;
		}
	}else if ("<s:text name="topology.menu.diagnostics.traceroute"/>" == result.t) {
		pingTr.style.display = "none";
		interfaceTr.style.display = "none";
		sipTr.style.display = "none";
		if(result.ip){
			document.getElementById("tracerouteIp").value = result.ip;
		}
	}else if ("<s:text name="topology.menu.alg.sip.name"/>" == result.t) {
		pingTr.style.display = "none";
		interfaceTr.style.display = "none";
		sipTr.style.display = "";
		if(result.callId){
			document.getElementById("callId").value = result.callId;
		}
	}else{
		pingTr.style.display = "none";
		interfaceTr.style.display = "none";
		sipTr.style.display = "none";
	}

	if(result.t){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = result.t;
	}
	if(result.h){
		var dialogTitleDiv = document.getElementById("cliInfoTitle");
		dialogTitleDiv.innerHTML = dialogTitleDiv.innerHTML + ' - ' + result.h;
	}

	var cliDiv = document.getElementById("cli_viewer");
	if ("<s:text name="topology.menu.diagnostics.showdhcpclientallocation"/>" == result.t) {
		if(result.dhcpMsgs){
			var dhcpMsgs = result.dhcpMsgs;
			var subnets = result.subnets;
			var reult = "<table><tr><th>Sub-Network</th><th>DHCP Client</th></tr>";
			for(var i = 0;i<dhcpMsgs.length;i++){
				reult = reult + "<tr><td>"+ subnets[i] + "</td><td>";
				reult = reult + "<pre>" + dhcpMsgs[i].replace(/\n/g,"<br>") + "</pre>";
				reult = reult + "</td></tr>";
			}
			reult = reult + "</table>";
			cliDiv.innerHTML=reult
		} else {
			cliDiv.innerHTML="<s:text name="error.dhcp.client.allocation.notexist"/>";
		}

	} else {
		cliDiv.innerHTML = "<pre>" + result.v.replace(/\n/g,"<br>") + "</pre>";

		//fix bug 22830
		if(result.v == "<s:text name="info.cli.pse.reset.success"/>"){
			setTimeout(function(){
				cliDiv.innerHTML = "<pre>" + "<s:text name="info.cli.pse.reset.complete"/>" + "</pre>";
			},3000);
		}else if(result.v == "<s:text name="geneva_03.info.cli.usbmodem.reset.success"/>"){
			setTimeout(function(){
				cliDiv.innerHTML = "<pre>" + "<s:text name="geneva_03.info.cli.usbmodem.reset.complete"/>" + "</pre>";
			},3000);
		} else if(result.v == "<s:text name="info.cli.reboot.current.success"/>"){
			setTimeout(function(){
				cliDiv.innerHTML = "<pre>" + "<s:text name="info.cli.reboot.current.complete"/>" + "</pre>";
			},5000);
		} else if(result.v == "<s:text name="info.cli.reboot.backup.success"/>"){
			setTimeout(function(){
				cliDiv.innerHTML = "<pre>" + "<s:text name="info.cli.reboot.backup.complete"/>" + "</pre>";
			},5000);
		} else if(result.v == "<s:text name="info.cli.turbo.success"/>"){
			setTimeout(function(){
				cliDiv.innerHTML = "<pre>" + "<s:text name="info.cli.turbo.complete"/>" + "</pre>";
			},3000);
		}
	}

    if(null == cliInfoPanel){
    	createCliInfoPanel();
    }
	if (result.t && "<s:text name="topology.menu.hiveAp.reset.default"/>" == result.t) {
		cliInfoPanel.hideEvent.subscribe(refreshHiveAPListPage);
	} else {
		cliInfoPanel.hideEvent.subscribe(resetParams);
	}
    var panelHeight = YAHOO.util.Dom.get('cliInfoPanel').offsetHeight;
    resetPanelSize(panelHeight, cliInfoPanel);
	cliInfoPanel.cfg.setProperty('visible', true);
}

var clientInfoResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	var clientInfoElement = document.getElementById("clientInfoLabel");
	var clientInfoTable = document.getElementById("clientInfoTable");

	eval("var result = " + o.responseText);
	hm.util.hideFieldError();

	if(result.e){
		hm.util.hide('paginatorTR');
		hm.util.reportFieldError(clientInfoElement, result.e);
	}

	if(result.data){
		var data = result.data;
		for(var i=0; i<data.length; i++){
			var rowData = data[i].rowData;
			if(i+1 < clientInfoTable.rows.length){
				clientInfoTable.deleteRow(i+1);
			}
			var row = clientInfoTable.insertRow(i+1);
			for(var j=0; j<rowData.length; j++){
				var cell = row.insertCell(j);
				cell.className = 'list';
				cell.noWrap=true;
				cell.innerHTML = rowData[j].v + "&nbsp;";
			}
		}

		// paginator
		hm.util.show('paginatorTR');
		if (result.pageIndex > 1)
		{
			hm.util.show('clientFirstPageTD');
			hm.util.show('clientPreviousPageTD');
			hm.util.hide('clientFirstDarkTD');
			hm.util.hide('clientPreviousDarkTD');
		} else {
			hm.util.hide('clientFirstPageTD');
			hm.util.hide('clientPreviousPageTD');
			hm.util.show('clientFirstDarkTD');
			hm.util.show('clientPreviousDarkTD');
		}
		if (result.pageIndex < result.pageCount)
		{
			hm.util.show('clientNextPageTD');
			hm.util.show('clientLastPageTD');
			hm.util.hide('clientNextDarkTD');
			hm.util.hide('clientLastDarkTD');
		} else {
			hm.util.hide('clientNextPageTD');
			hm.util.hide('clientLastPageTD');
			hm.util.show('clientNextDarkTD');
			hm.util.show('clientLastDarkTD');
		}
		document.getElementById('clientPageCenterTD').innerHTML="<td nowrap='nowrap' style='padding: 3px 4px 0px 4px;' id='clientPageCenterTD'> "
			+ result.pageIndex + " / " + result.pageCount + " </td>";
	}

	if(null == clientInfoPanel){
		createClientInfoPanel();
	}
	clientInfoPanel.cfg.setProperty('visible', true);
}

var neighborInfoResult = function(o) {
	eval("var result = " + o.responseText);
	hm.util.hideFieldError();
	var neighborInfoElement = document.getElementById("neighborInfoLabel");
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	var titleDiv = document.getElementById("neighborTitle");
	retrieveResult(result,neighborInfoTable,neighborInfoElement,titleDiv);
	if(null == neighborInfoPanel){
		createNeighborInfoPanel();
	}
	neighborInfoPanel.cfg.setProperty('visible', true);
}

function retrieveResult(result, table, tableTrElement, dialogTitleDiv) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.h){
		var currentStr = dialogTitleDiv.innerHTML;
		if(currentStr.indexOf(' - ')>0){
			dialogTitleDiv.innerHTML = currentStr.slice(0,currentStr.indexOf(' - '))+ ' - ' + result.h;
		}else{
			dialogTitleDiv.innerHTML = currentStr + ' - ' + result.h;
		}
	}
	if(result.e){
		hm.util.reportFieldError(tableTrElement, result.e);
		return;
	}
	if(result.data){
		var data = result.data;
		for(var i=0; i<data.length; i++){
			var rowData = data[i].rowData;
			if(i+1 < table.rows.length){
				table.deleteRow(i+1);
			}
			var row = table.insertRow(i+1);
			for(var j=0; j<rowData.length; j++){
				var cell = row.insertCell(j);
				cell.className = 'list';
				cell.innerHTML = rowData[j].v + "&nbsp;";
			}
		}
	}
}

var connectedFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function clearClientInfoTable(){
	var clientInfoTable = document.getElementById("clientInfoTable");
	clearTableData(clientInfoTable);
}
function clearNeighborInfoTable(){
	var neighborInfoTable = document.getElementById("neighborInfoTable");
	clearTableData(neighborInfoTable);
}

function clearTableData(table){
	var rowCount = table.rows.length;
	for(var i=1; i< rowCount; i++){
		table.deleteRow(1);
	}
	for(var j=0; j<12; j++){
		var row = table.insertRow(1);
		for(var k=0; k<table.rows[0].cells.length; k++){
			var cell = row.insertCell(k);
			cell.className = 'list';
			cell.innerHTML = "&nbsp;";
		}
	}
}

function openConfirmBootDialog(text){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	thisOperation = text;
	if(text == "<s:text name="topology.menu.hiveAp.reboot"/>"){
		hm.util.confirmRebooting(isAnySwitchSelected(selectedIds));
	}else if (text == "<s:text name="topology.menu.hiveAp.invokeBackup"/>"){
		hm.util.confirmBootImage(isAnySwitchSelected(selectedIds));
	}else if (text == "<s:text name="topology.menu.hiveAp.reset.default"/>"){
		hm.util.confirmResetConfig(isAnySwitchSelected(selectedIds));
	}

}

function openConfirmResetPSEDialog(text){
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	thisOperation = text;
	hm.util.confirmRestPSE();
}

var formName = 'hiveAp';
var thisOperation;
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'multiEdit') {
		hm.util.checkAndConfirmModify();
	}else if (operation == 'remove') {
		//hm.util.checkAndConfirmDelete();
		<s:if test="%{hMOnline}">
			hm.util.checkAndConfirmDeleteRuleFromRedirect('selectedIds','<s:text name ="hiveAp.remove.dlg.confirm" />', true);
		</s:if>
		<s:else>
			hm.util.checkAndConfirmDeleteRuleFromRedirect('selectedIds','<s:text name ="hiveAp.remove.dlg.confirm.single" />', false);
		</s:else>
	//}else if (operation == 'RemoveRedirectDevice') {
	}else if (operation == 'clone2') {
		hm.util.checkAndConfirmClone();
	}else if (operation == 'reassignDomain') {
		hm.util.checkAndConfirmReassign();
	}else if (operation == 'toFriendly') {
		hm.util.checkAndConfirmFriendly();
	}else if (operation == 'toRogue') {
		hm.util.checkAndConfirmRogue();
	}else if (operation == 'removeCwpDirectory') {
		hm.util.confirmRemoveCwpDirectory();
	}else if (operation == 'exportDeviceInventory') {
		hm.util.checkAndConfirmMultiple("export");
	}else if (operation == 'startSpectrumAnalysis') {
		spectralAnalysisSupCheck();
	}else if (validate(operation)) {
		doContinueOper();
	}
}

function validate(operation)
{
	return true;
}

function doContinueOper() {
	if (thisOperation == 'startSpectrumAnalysis') {
		startSpectralAnalysis();
		return;
	}

	if (thisOperation == 'syncClientsFromAPs') {
		url = "<s:url action='mapNodes' includeParams='none' />?operation=syncClientsFromAPs&allItemsSelected="+document.getElementById("checkAll").checked+"&selectedAPIdStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:clientInfoResult,failure:connectedFailed,timeout: 600000});
		if(waitingPanel != null){
			waitingPanel.show();
		}
		return;
	}
	
	if (thisOperation == 'simpllyUpdateModel') {
		//this function from hiveApUpdateSimpleModel.jsp
		configSimplifiedUpdate();
		return;
	}
	if (thisOperation == 'advancedUpdateModel') {
		//this function from hiveApUpdateSimpleModel.jsp
		configAdvancedUpdate();
		return;
	}
	
	if (thisOperation == 'checkConnectStatus' || 
			thisOperation == 'checkNetworkPolicy') {
		//this function from hiveApUpdateSimpleModel.jsp
		configUpdateOperation();
		return;
	}

	if (thisOperation != 'upgradeConfiguration'
		&& thisOperation != 'upgradeImage'
		&& thisOperation != 'upgradeBootstrap'
		&& thisOperation != 'upgradeCountryCode'
		&& thisOperation != 'removeCwpDirectory'
		&& thisOperation != "<s:text name="topology.menu.hiveAp.reboot"/>"
		&& thisOperation != "<s:text name="topology.menu.hiveAp.reset.default"/>"
		&& thisOperation != "<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>"
		&& thisOperation != "<s:text name="topology.menu.hiveAp.invokeBackup"/>"
		&& thisOperation != 'upgradeOutdoor'
		&& thisOperation != "<s:text name="topology.menu.hiveAp.pse.reset"/>"
		&& thisOperation != 'exportDeviceInventory') {
		showProcessing();
	}
	//change operation name while multiEdit single one.
	if(thisOperation == 'multiEdit'){
		var selectedIds = hm.util.getSelectedIds();
		if (selectedIds.length == 1) {
			thisOperation = "edit2";
			document.forms[formName].id.value = selectedIds[0];
		}
	}else if (thisOperation == 'removeCwpDirectory') {
		submitCwpRemovePanel();
		return;
	}else if(thisOperation == "<s:text name="topology.menu.hiveAp.reboot"/>"){
		//rebootDevices();
		thisOperation = "rebootHiveAPs";
	}else if(thisOperation == "<s:text name="topology.menu.hiveAp.reset.default"/>"){
		doAjaxRequest("requestMultipleItemCli", "<s:text name="topology.menu.hiveAp.reset.default"/>", cliInfoResult);
		return;
	} else if (thisOperation == "<s:text name="topology.menu.hiveAp.invokeBackup"/>"){
		var value = document.getElementById('imageBootcurrent').checked? "current" : "backup";
		doAjaxRequestHiveApAction("invokeImage", cliInfoResult, "imageType="+value);
		hideImageBootPanel();
		return;
	} else if(thisOperation == '<s:text name="topology.menu.hiveAp.pse.reset"/>'){
		requestMultipleItemCli(thisOperation);
		return;
	} else if(thisOperation == '<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>'){
		doAjaxRequest("clearRadsecCerts", '<s:text name="topology.menu.hiveAp.clear.radsec.credentials"/>', cliInfoResult);
		return;
	}
    if (thisOperation=='remove') {
    	document.forms[formName].resetDeviceFlag.value = Get('ck_resetDeviceFlag').checked;
    }
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function onLoadPage() {
	createWaitingPanel();
	<s:if test="%{pageAutoRefresh}">
	startHiveApPagingTimer();
	</s:if>
}
function onUnloadPage() {
	clearTimeout(hiveApPagingTimeoutId);
}

var doCustomAutoRefreshSettingSubmit = function(postfix) {
	var baseUrl = "<s:url action="hiveAp" includeParams="none" />?ignore=" + new Date().getTime();
	var url = baseUrl + "&" + postfix;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateAutoRefreshSetting }, null);
}
var updateAutoRefreshSetting = function(o) {
	eval("var result = " + o.responseText);
	if (hm.util.isAFunction(updateAutoRefreshStatus)) {
		updateAutoRefreshStatus(result.autoOn);
	}
	if (result.autoOn == false) {
		clearTimeout(hiveApPagingTimeoutId);
	} else {
		if (result.refreshOnce) {
			submitAction('refreshFromCache');
		} else {
			startHiveApPagingTimer();
		}
	}
}

function configSimplifiedUpdate(){
	arrayOperations = [
		"checkConnectStatus",
		"getDeviceCounts",
		"getRebootDevices",
		"checkNetworkPolicy",
		"uploadWizard"
	];
	configUpdateOperation();
}

function configAdvancedUpdate(){
	arrayOperations = [
		"checkConnectStatus",
		"upgradeConfiguration"
	];
	configUpdateOperation();
}

var hiveApPagingLiveCount = 0;
var hiveApPagingTimeoutId;
function startHiveApPagingTimer() {
	var interval = <s:property value="%{pageRefInterval}"/>; // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (hiveApPagingLiveCount++ < total) {
		hiveApPagingTimeoutId = setTimeout("pollHiveApPagingCache()", interval * 1000);  // seconds
	}
}
function pollHiveApPagingCache() {
	var url = "<s:url action="hiveAp" includeParams="none" />?operation=updates&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateHiveAp }, null);
}
//cached value for refresh which is updated when overlay is showing or item be selected
var cachedRefresh = false;
function updateHiveAp(o) {
	eval("var updates = " + o.responseText);
	var unallowRefresh = isAnyOverlayShowing() || isAnyItemSelected();

	for (var i = 0; i < updates.length; i++) {
		if (updates[i].id < 0) {
			if(unallowRefresh){
				cachedRefresh = true;
			}else{
				submitAction('refreshFromCache');
				return;
			}
		}
	}
	if(!unallowRefresh && cachedRefresh){
		submitAction('refreshFromCache');
		return;
	}
	startHiveApPagingTimer();
}

function isAnyOverlayShowing(){
	if(null != overlayManager){
		var overlays = overlayManager.overlays;
		for(var i=0; i<overlays.length; i++){
			if(overlays[i].cfg.getProperty("visible")){
				return true;
			}
		}
	}
	return false;
}

function isAnyItemSelected(){
	var selectedIds = hm.util.getSelectedIds();
	return selectedIds.length > 0 ? true : false;
}

function switchViewType(viewType){
	document.forms[formName].operation.value = 'switch';
	document.forms[formName].viewType.value = viewType;
	document.forms[formName].hmListType.value = '<s:property value="listType"/>';
    document.forms[formName].submit();
}

function insertPageContext() {
	<%--
	//var monitor_rb = '<input type="radio" name="viewType" id="viewType_m" value="monitor" onclick="switchViewType(\'monitor\')"/><label for="viewType_m"><s:text name="hiveAp.view.label.monitor"/></label>';
	//var monitor_rb_ck = '<input type="radio" name="viewType" id="viewType_m" value="monitor" checked onclick="switchViewType(\'monitor\')"/><label for="viewType_m"><s:text name="hiveAp.view.label.monitor"/></label>';
	//var config_rb = '<input type="radio" name="viewType" id="viewType_c" value="config" onclick="switchViewType(\'config\')"/><label for="viewType_c"><s:text name="hiveAp.view.label.config"/></label>';
	//var config_rb_ck = '<input type="radio" name="viewType" id="viewType_c" value="config" checked onclick="switchViewType(\'config\')"/><label for="viewType_c"><s:text name="hiveAp.view.label.config"/></label>';
	//'<s:url action="hiveAp"><s:param name="operation" value="%{'switch'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'
	//var config_a ="<a href='<s:url action="hiveAp"><s:param name="operation" value="%{'switch'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'><s:text name="hiveAp.view.label.config"/></a>";
	--%>
	<s:if test="%{displayMonitorView}">
	<%-- var monitor_a ='<a href="javascript:void(0);" onclick="switchViewType(\'monitor\')"><s:text name="hiveAp.view.label.monitor"/></a>';--%>
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>'
		+ '</td>');
	</s:if>
	<s:elseif test="%{displayConfigView}">
	<%-- var config_a ='<a href="javascript:void(0);" onclick="switchViewType(\'config\')"><s:text name="hiveAp.view.label.config"/></a>';--%>
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td><td nowrap>'
		+ '</td>');
	</s:elseif>
	<s:else>
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />&nbsp;&nbsp;</td>');

	</s:else>

}

function locateAPColorMoreChange() {
	var color = document.getElementById("locateAPColorMore");
	var colorValue = color.value;
	if(colorValue == "green"){
		color.style.background = "#00FF00";
	}else if(colorValue == "red"){
		color.style.background = "#FF0000";
	}else if(colorValue == "yellow"){
		color.style.background = "#FFFF00";
	}else if(colorValue == "blue"){
		color.style.background = "#0000FF";
	}else if(colorValue == "purple"){
		color.style.background = "#9F00C5";
	}else if(colorValue == "white"){
		color.style.background = "#FFFFFF";
	}else if(colorValue == "amber"){
		color.style.background = "#FFBF00";
	}else if(colorValue == "orange"){
		color.style.background = "#FF7F00";
	}else{
		color.style.background = "#808080";
	}

	if(colorValue == "off"){
		document.getElementById("locateAPBlink").disabled = true;
	}else{
		document.getElementById("locateAPBlink").disabled = false;
	}
}

function locateAPColorLessChange(){
	var color = document.getElementById("locateAPColorLess");
	if(color.value == "green"){
		color.style.background = "#00FF00";
	}else if(color.value == "red"){
		color.style.background = "#FF0000";
	}else if(color.value == "orange"){
		color.style.background = "#FF6000";
	}else{
		color.style.background = "#FFFFFF";
	}
}
</script>

<div id="content">
	<s:form action="hiveAp">
		<s:hidden name="cacheId" />
		<s:hidden name="menuText" />
		<s:hidden name="hiveApId" />
		<s:hidden name="reassignDomainName" />
		<s:hidden name="hiveApModel"/>
		<s:hidden name="viewType"/>
		<s:hidden name="simpleUpdate" id="simpleUpdate"/>
		<s:hidden name="completeCfgUpdate" id="completeCfgUpdate"/>
		<s:hidden name="imageUpgrade" id="imageUpgrade"/>
		<s:hidden name="forceImageUpgrade" id="forceImageUpgrade"/>
		<s:hidden name="simplifiedRebootType" id="simplifiedRebootType"/>
		<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
		<!-- fix bug 14675 -->
		<s:hidden name="hmListType"/>
		
		<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
		<s:hidden name="resetDeviceFlag"/>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive) || !hMOnline}">
			<tr>
				<td class="menu_bg" style="padding-top:10px">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="15px" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
							<td class="tab_selectTabStyle" nowrap><a href="javascript:void(0);"><s:text name="geneva_08.tab.title.alldevice"/></a></td>
							<td class="tab_unSelectTabStyle" nowrap>
							<a href='<s:url action="deviceInventory"><s:param name="diMenuTypeKey" value="%{selectedL2Feature.key}"/></s:url>'>
							<s:text name="geneva_08.tab.title.newdevice"/></a>
							</td>
							<td width="100%" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			</s:if>
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
						<!--  s:if test="%{configView}">-->
							<%--
					<td><input type="button" name="ignore" value="Update"
						class="button" onClick="submitAction('update_todo');"
						<s:property value="writeDisabled" />></td>

					<td><input type="button" name="ignore" value="Edit"
						class="button" onClick="submitAction('editChecked');"></td>
  --%>
							<td style="display:none;"><input type="button" name="ignore" value="New"
								class="button" id="n_menutoggle"
								<s:property value="mGListWriteDisabled" />>
							</td>
							<td><div id="newDeviceMenu" class="yuimenu"></div></td>
							<td><input type="button" name="ignore" value="Modify"
								class="button" onClick="submitAction('multiEdit');"
								<s:property value="mGListWriteDisabled" />></td>
							<s:if test="%{!hMOnline}">
							<td style="display:none;"><input type="button" name="ignore" value="Remove"
								class="button" onClick="submitAction('remove');"
								<s:property value="writeDisabled" />></td>
							</s:if>
						<!--/s:if>	-->
								<!--
							<td><input type="button" name="ignore" value="Clone"
								class="button" onClick="submitAction('clone2');"
								<s:property value="mGListWriteDisabled" />>
							</td>
							 -->
							<td style="display:none;"><input type="button" name="ignore" value="Remove"
								class="button" onClick="submitAction('remove');"
								<s:property value="writeDisabled" />></td>
							<s:if test="%{!oEMSystem}">
								<s:if test="%{hmListType != 'managedSwitches' && hmListType != 'managedVPNGateways'}">
									<td style="display:none;"><input type="button" name="ignore" value="Import"
										class="button" onClick="submitAction('importNew');"
										<s:property value="mGListWriteDisabled" />>
									</td>
								</s:if>
							</s:if>
							<td><input type="button" name="ignore" value="Update..."
								class="button" id="menutoggle"
								<s:property value="mGListWriteDisabled" />></td>
							<td>
								<div id="basicmenu" class="yuimenu"></div></td>
							<td><input type="button" name="ignore" value="Utilities..."
								class="button" id="t_menutoggle"
								<s:property value="mGListWriteDisabled" /> ></td>
							<td><div id="t_menu" class="yuimenu"></div>
							</td>
							<s:if test="%{showReassignMenu}">
								<td><input type="button" name="ignore" value="Reassign..."
									class="button" id="s_menutoggle">
								</td>
								<td><div id="reassign_menu" class="yuimenu"></div>
								</td>
							</s:if>
							
							 <s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive && writeDisabled!='disabled') || (!hMOnline && writeDisabled!='disabled')}">
							 	<td><input type="button" name="ignore" value="<s:text name="geneva_08.button.deviceInventory"/>"
									class="button long" id="menu_inventory">
								</td>
								<td><div id="s_menu_inventory" class="yuimenu" style="width:140px;"></div>
								</td>
							 </s:if>

							<%-- <td class="labelT1"></td>
					<td class="labelT1"><a
							href='<s:url action="hiveAp"><s:param name="operation" value="%{'switch'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'><s:property
							value="hostName" /><s:text name="hiveAp.view.label.monitor"/></a></td>
					<td class="labelT1"><a
							href='<s:url action="hiveAp"><s:param name="operation" value="%{'switch'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'><s:property
							value="hostName" /><s:text name="hiveAp.view.label.config"/></a></td--%>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
					<s:if test="%{easyMode && listType == 'managedHiveAps'}">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="noteInfo" style="vertical-align:top">
									<p>
										<s:text name="hivaAp.easyMode.Note" />
									</p>
								</td>
								<td class="noteInfo">
									<p style="padding-left:5px">
										<s:text name="hivaAp.easyMode.Note.content" />
									</p>
								</td>
							</tr>

						</table>
					</s:if>
				</td>
			</tr>
			<tr>
				<td>
					<table id="hiveApListTable" cellspacing="0" cellpadding="0" border="0" class="view" width="100%">
							<tr>
								<th class="check"><input type="checkbox" id="checkAll"
									onClick="hm.util.toggleCheckAll(this);"></th>
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 28}">
										<th><ah:sort name="pendingIndex"
												key="hiveAp.configuration.audit" /></th>
									</s:if>
									<s:elseif test="%{columnId == 1}">
									<th style="padding-left: <s:property value="%{hostnamIndent}" />px">
											<ah:sort name="hostName" key="hiveAp.hostName" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 2}">
										<th style="text-align: center;"><ah:sort name="severity"
												key="monitor.hiveAp.severity.status" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 3}">
									<th><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" orderByIp="true" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 38}">
									<th><ah:sort name="capwapClientIp" key="hiveAp.capwapIpAddress" orderByIp="true" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 4}">
										<th><ah:sort name="macAddress" key="hiveAp.macaddress" />
										</th>
									</s:elseif>
									<s:elseif test="%{columnId == 5}">
										<th width="60px"><ah:sort name="connected"
												key="monitor.hiveAp.capwap.status" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 6}">
										<th><ah:sort name="hiveApType" key="hiveAp.apType" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 7}">
										<th style="text-align: center;"><s:text
												name="monitor.hiveAp.numberOfClient" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 8}">
										<th><ah:sort name="upTime"
												key="monitor.hiveAp.connectionTime" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 9}">
										<th><ah:sort name="hiveApModel"
												key="monitor.hiveAp.model" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 10}">
									<th><ah:sort name="displayVer" key="monitor.hiveAp.sw" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 11}">
									<th><ah:sort name="discoveryTime" key="hiveAp.discoveryTime" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 23}">
										<th><s:text name="hiveAp.wifi0.channel" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 24}">
										<th><s:text name="hiveAp.wifi0.power" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 25}">
										<th><s:text name="hiveAp.wifi1.channel" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 26}">
										<th><s:text name="hiveAp.wifi1.power" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 27}">
									<th><ah:sort name="countryCode" key="hiveAp.countryCode" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 30}">
										<th><ah:sort name="eth0DeviceId"
												key="hiveAp.lldpCdp.eth0.deviceId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 31}">
										<th><ah:sort name="eth0PortId"
												key="hiveAp.lldpCdp.eth0.portId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 32}">
										<th><ah:sort name="eth1DeviceId"
												key="hiveAp.lldpCdp.eth1.deviceId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 33}">
										<th><ah:sort name="eth1PortId"
												key="hiveAp.lldpCdp.eth1.portId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 35}">
										<th><ah:sort name="eth0SystemId"
												key="hiveAp.lldpCdp.eth0.systemId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 36}">
										<th><ah:sort name="eth1SystemId"
												key="hiveAp.lldpCdp.eth1.systemId" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 21}">
										<th><ah:sort name="serialNumber"
												key="hiveAp.serialNumber" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 40}">
										<th><s:text name="hiveAp.brRouter.usb.3G" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 41}">
									<th><ah:sort name="isOutdoor" key="hiveAp.isOutdoor" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 42}">
										<th><s:text name="hiveAp.deviceCategory" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 45}">
										<th><ah:sort name="signatureVer" key="hiveAp.head.l7.signature.ver" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 14}">
									<th><ah:sort name="mapContainer" key="hiveAp.topology" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 34}">
										<th><s:text name="config.configTemplate.vlan" /></th>
									</s:elseif>
									<s:elseif test="%{columnId == 37}">
										<th><s:text name="config.configTemplate.vlanNative" /></th>
									</s:elseif>
								<s:elseif test="%{columnId == 13}">
									<th><ah:sort name="LOWER(configTemplate.configName)" key="hiveAp.template" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 29}">
									<th><s:text name="hiveAp.hiveProfile" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 15}">
									<th><ah:sort name="dhcp" key="hiveAp.head.dhcp" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 16}">
									<th><ah:sort name="netmask" key="hiveAp.netmask" orderByIp="true" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 17}">
									<th><ah:sort name="gateway" key="hiveAp.gateway" orderByIp="true" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 18}">
									<th><ah:sort name="location" key="hiveAp.location" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 43}">
									<th><ah:sort name="wifi0RadioProfile.radioName" key="hiveAp.head.wifi0.radioProfile" /></th>
								</s:elseif>
								<s:elseif test="%{columnId == 44}">
									<th><ah:sort name="wifi1RadioProfile.radioName" key="hiveAp.head.wifi1.radioProfile" /></th>
								</s:elseif>
								<s:if test="%{columnId == 46}">
									<th><ah:sort name="supplementalCLI" key="hollywood_02.supp_cli_apList.title" /></th>
								</s:if>
								</s:iterator>
								<s:if test="%{showDomain}">
									<th><ah:sort name="owner.domainName" key="config.domain" />
									</th>
								</s:if>
							</tr>
							<s:if test="%{page.size() == 0}">
								<ah:emptyList />
							</s:if>
							<tiles:insertDefinition name="selectAll" />
							<s:if test="%{hasNewHiveAP}">
								<!-- fix bug 14675 -->
								<tr>
									<td class="managedStatusLabel" colspan="100">
										<s:if test="%{listType == 'managedVPNGateways'}">
											<s:text name="geneva_08.device.list.newGateways.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:if>
										<s:elseif test="%{listType == 'managedRouters'}">
											<s:text name="geneva_08.device.list.newBRs.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:elseif>
										<s:elseif test="%{listType == 'managedSwitches'}">
											<s:text name="geneva_08.device.list.newSRs.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:elseif>
										<s:elseif test="%{listType == 'managedDeviceAPs'}">
											<s:text name="geneva_08.device.list.newHiveAps.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:elseif>
										<s:elseif test="%{listType == 'managedHiveAps'}">
											<s:text name="geneva_08.device.list.newdevice.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:elseif>
										<s:else>
											<s:text name="geneva_08.device.list.newdevice.title" />&nbsp;(<s:property value="newHiveApCount"/>)
										</s:else>
										<span id="newHiveApTool"></span>
									</td>
								</tr>
							</s:if>
							<s:iterator value="page" status="status" id="pageRow">
								<tiles:insertDefinition name="rowClass" />
								<s:if test="%{newHiveAP}">
									<s:set name="statusClass" value="%{'newHiveApRow'}" />
								</s:if>
								<s:else>
									<s:if test="%{hasNewHiveAP && #statusClass != ''}">
										<!-- fix bug 14675 -->
										<tr>
											<td class="managedStatusLabel" colspan="100">
												<s:if test="%{listType == 'managedVPNGateways'}">
													<s:text name="geneva_08.device.list.managedGateways.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:if>
												<s:elseif test="%{listType == 'managedRouters'}">
													<s:text name="geneva_08.device.list.managedBRs.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:elseif>
												<s:elseif test="%{listType == 'managedSwitches'}">
													<s:text name="geneva_08.device.list.managedSRs.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:elseif>
												<s:elseif test="%{listType == 'managedDeviceAPs'}">
													<s:text name="geneva_08.device.list.managedHiveAps.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:elseif>
												<s:elseif test="%{listType == 'managedHiveAps'}">
													<s:text name="geneva_08.device.list.managedDevices.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:elseif>
												<s:else>
													<s:text name="geneva_08.device.list.managedDevices.title" />&nbsp;(<s:property value="managementHiveApCount"/>)
												</s:else>
											</td>
										</tr>
									</s:if>
									<s:set name="statusClass" value="%{''}" />
								</s:else>
								<tr
									class="<s:property value="%{#rowClass}"/> <s:property value="%{#statusClass}"/>">
									<s:if
										test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
										<td class="listCheck"><input type="checkbox"
											disabled="disabled" /></td>
									</s:if>
									<s:else>
										<td class="listCheck"><ah:checkItem /></td>
									</s:else>
									<s:iterator value="%{selectedColumns}">
										<s:if test="%{columnId == 1}">
											<td class="list">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<s:if test="%{iconMaxCount > 0}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem1" escape="false" /></td>
														</s:if>
														<s:if test="%{iconMaxCount > 1}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem2" escape="false" /></td>
														</s:if>
														<s:if test="%{iconMaxCount > 2}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem3" escape="false" /></td>
														</s:if>
														<s:if test="%{iconMaxCount > 3}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem4" escape="false" /></td>
														</s:if>
														<s:if test="%{iconMaxCount > 4}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem5" escape="false" /></td>
														</s:if>
														<s:if test="%{iconMaxCount > 5}">
															<td style="padding-right: 3px;"><s:property
																	value="iconItem6" escape="false" /></td>
														</s:if>

														<s:if test="%{easyMode && disableExpress}">
															<td style="padding: 2px 5px 2px 4px;"><s:property value="hostName" /></td>
														</s:if>
														<s:elseif test="%{showDomain}">
															<td nowrap="nowrap">
																<table>
																	<tr>
																<s:if test="%{configView}">
																	<td>
																		<a href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit2'}"/><s:param name="id" value="%{#pageRow.id}"/>
																	<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
																				value="hostName" /> </a><input type="hidden" id='devType<s:property value="#pageRow.id"/>_' value='<s:property value="deviceType"/>'/></td>
																</s:if>
																<s:else>
																	<td>
																	<a href='<s:url action="hiveAp"><s:param name="operation" value="%{'showHiveApDetails'}"/><s:param name="id" value="%{#pageRow.id}"/>
																		<s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property value="hostName" /> </a></td>
																</s:else>
																		<td><s:if test="spnSupportBln">
																				<s:if test="%{writeDisabled==''}">
																					<a href="#snp"
																						onclick="launchSpectrumAnalysis('<s:property value="#pageRow.id" />');"><img
																						src="<s:url value="/images/sa/snp.png" />"
																						title="Spectrum Analysis Enabled" width="16"
																						height="16" border="0px" class="dblk"> </a>
																				</s:if>
																				<s:else>
																					<img src="<s:url value="/images/sa/snp.png" />"
																						title="Spectrum Analysis Enabled" width="16"
																						height="16" border="0px" class="dblk">
																				</s:else>
																			</s:if></td>
																	</tr>
																</table></td>
														</s:elseif>
														<s:else>
															<td nowrap="nowrap">
																<table>
																<tr>
																	<s:if test="%{configView}">
																		<td><a
																			href='<s:url action="hiveAp"><s:param name="operation" value="%{'edit2'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
																					value="hostName" /> </a><input type="hidden" id='devType<s:property value="#pageRow.id"/>_' value='<s:property value="deviceType"/>'/></td>
																	</s:if>
																	<s:else>
																		<td>
																			<a href='<s:url action="hiveAp"><s:param name="operation" value="%{'showHiveApDetails'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
																					value="hostName" /> </a></td>
																	</s:else>

																		<td><s:if test="spnSupportBln">
																				<s:if test="%{writeDisabled==''}">
																					<a href="#snp"
																						onclick="launchSpectrumAnalysis('<s:property value="#pageRow.id" />');"><img
																						src="<s:url value="/images/sa/snp.png" />"
																						title="Spectrum Analysis Enabled" width="16"
																						height="16" border="0px" class="dblk"> </a>
																				</s:if>
																				<s:else>
																					<img src="<s:url value="/images/sa/snp.png" />"
																						title="Spectrum Analysis Enabled" width="16"
																						height="16" border="0px" class="dblk">
																				</s:else>
																			</s:if></td>
																	</tr>
																</table></td>
														</s:else>
													</tr>
											</table><input type="hidden" id='devType<s:property value="#pageRow.id"/>_' value='<s:property value="deviceType"/>'/>
										</td>
										</s:if>
										<s:elseif test="%{columnId == 28}">
											<td width="22px" class="list"
												style="text-align: center; padding-left: 10px;"><s:property
													value="configIndicationIcon" escape="false" /></td>
										</s:elseif>
										<s:elseif test="%{columnId == 2}">
											<td class="list"
												style="text-align: center; vertical-align: center; padding-top: 4px;">
												<a
												href='<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}"/><s:param name="apId" value="%{macAddress}"/><s:param name="filterVHM" value="%{#pageRow.owner.domainName}"/></s:url>'><s:property
														value="severityIcon" escape="false" /> </a></td>
										</s:elseif>
										<s:elseif test="%{columnId == 3}">
											<td class="list" nowrap="nowrap"><s:property
													value="ipAddress" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 38}">
											<td class="list" nowrap="nowrap"><s:property
													value="capwapClientIp" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 4}">
											<td class="list" nowrap="nowrap"><s:property
													value="macAddress" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 5}">
											<td nowrap="nowrap" class="list"
												style="<s:if test="%{allDisconnected}">text-align: center; </s:if>vertical-align: center; padding-top: 3px; padding-bottom: 0px;">
												<s:property value="connectionIcon" escape="false" />&nbsp;<s:property
													value="dtlsIcon" escape="false" /></td>
										</s:elseif>
										<s:elseif test="%{columnId == 6}">
											<td class="list" nowrap="nowrap">
											<!-- fix bug 14694 -->
											<s:if test="%{easyMode && disableExpress}">
												<s:text name="hiveAp.manage.list.unknow" />
											</s:if>
											<s:else>
												<s:if test="%{deviceType == 2}">
													&nbsp;
												</s:if>
												<s:else>
													<s:property value="hiveApTypeString" /> &nbsp;
												</s:else>
											</s:else>
											</td>
										</s:elseif>
										<s:elseif test="%{columnId == 7}">
											<td class="list" style="text-align: center;">
												<!-- fix bug 14694 -->
												<s:if test="%{deviceType == 2}">
													&nbsp;
												</s:if>
												<s:else>
													<s:if test="%{connected}">
														<s:if test="%{activeClientCount > 0}">
															<a href='<s:url action="clientMonitor" includeParams="none">
															<s:param name="operation" value="%{'search'}"/>
															<s:param name="filterApName" value="%{hostName}"/>
															<s:param name="filterApMac" value="%{macAddress}"/>
															</s:url>'>
																<s:property value="activeClientCount" /> </a>
														</s:if>
														<s:else>
															<s:property value="activeClientCount" />
														</s:else>
													</s:if> <s:else>-</s:else>
												</s:else>
											</td>
										</s:elseif>
										<s:elseif test="%{columnId == 8}">
											<td class="list" nowrap="nowrap"><s:property
													value="upTimeString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 9}">
											<td class="list" nowrap="nowrap"><s:property
													value="deviceModelName" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 10}">
											<td class="list" nowrap="nowrap"><s:property
													value="displayVerNoBuild" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 11}">
											<td class="list" nowrap="nowrap"><s:property
													value="discoveryTimeString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 23}">
											<td class="list" nowrap="nowrap"><s:property
													value="wifi0.runningChannel" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 24}">
											<td class="list" nowrap="nowrap"><s:property
													value="wifi0.runningPower" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 25}">
											<td class="list" nowrap="nowrap"><s:property
													value="wifi1.runningChannel" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 26}">
											<td class="list" nowrap="nowrap"><s:property
													value="wifi1.runningPower" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 27}">
											<td class="list" nowrap="nowrap"><s:property
													value="countryName" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 30}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth0DeviceIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 31}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth0PortIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 32}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth1DeviceIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 33}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth1PortIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 35}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth0SystemIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 36}">
											<td class="list" nowrap="nowrap"><s:property
													value="eth1SystemIdString" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 21}">
											<td class="list" nowrap="nowrap"><s:property
													value="serialNumber" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 40}">
											<td class="list" nowrap="nowrap"><s:property
													value="usbConnectionStatus" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 41}">
											<s:if test="%{isOutdoor==null}" >
												<td class="list">&nbsp;</td>
											</s:if>
											<s:elseif test="%{isOutdoor}">
												<td class="list"><s:text name="hiveAp.isOutdoor.dsp.true"/>&nbsp;</td>
											</s:elseif>
											<s:elseif test="%{!isOutdoor}">
												<td class="list"><s:text name="hiveAp.isOutdoor.dsp.false"/>&nbsp;</td>
											</s:elseif>
										</s:elseif>
										<s:elseif test="%{columnId == 42}">
											<td class="list" nowrap="nowrap"><s:property
													value="deviceCategory" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 45}">
											<td class="list" nowrap="nowrap"><s:property
													value="signatureVerStringWithPrefix" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 14}">
											<td class="list" nowrap="nowrap"><s:property
													value="topologyName" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 34}">
											<td class="list" nowrap="nowrap"><s:property
													value="vlanName" /> &nbsp;</td>
										</s:elseif>
										<s:elseif test="%{columnId == 37}">
											<td class="list" nowrap="nowrap"><s:property
													value="nativeVlanName" /> &nbsp;</td>
										</s:elseif>
									<s:elseif test="%{columnId == 13}">
										<td class="list" nowrap="nowrap">
											<s:if test="%{deviceType == 2}">
												N/A &nbsp;
											</s:if>
											<s:else>
												<s:if test="%{showDomain}">
													<a	href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{configTemplateId}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
															value="configTemplateName" /></a>
												</s:if>
												<s:else>
													<a	href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{configTemplateId}"/></s:url>'>
													<s:property value="configTemplateName" /></a>
												</s:else>
											</s:else>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 29}">
										<td class="list" nowrap="nowrap">
											<s:if test="%{deviceType == 2}">
												N/A &nbsp;
											</s:if>
											<s:else>
												<s:property value="hiveName" /> &nbsp;
											</s:else>
										</td>
									</s:elseif>
									<s:elseif test="%{columnId == 15}">
										<td class="list" nowrap="nowrap"><s:property
												value="dhcpString" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 16}">
										<td class="list" nowrap="nowrap"><s:property
												value="netmask" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 17}">
										<td class="list" nowrap="nowrap"><s:property
												value="gateway" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 18}">
										<td class="list" nowrap="nowrap"><s:property
												value="location" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 43}">
										<td class="list" nowrap="nowrap"><s:property
												value="wifi0RadioProfileName" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 44}">
										<td class="list" nowrap="nowrap"><s:property
												value="wifi1RadioProfileName" /> &nbsp;</td>
									</s:elseif>
									<s:elseif test="%{columnId == 46}">
										<td class="list" nowrap="nowrap"><s:property
												value="supplementalCLIName" /> &nbsp;</td>
									</s:elseif>
									</s:iterator>
									<s:if test="%{showDomain}">
										<td class="list"><s:property value="%{owner.domainName}" />
										</td>
									</s:if>
								</tr>
							</s:iterator>
						</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<div id="clientInfoPanel" style="display: none;">
	<div class="hd" id="clientTitle">
		<s:text name="topology.menu.client.information" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr id="paginatorTR" style="display: none">
				<td colspan="2">
					<table border="0" cellspacing="0" cellpadding="0" width="120px">
						<tr>
							<td id="clientFirstPageTD"><a
								href="javascript: clientFirstPage();"> <img
									src="<s:url value="/images/paging/first.png" />"
									onMouseOver="this.src='<s:url value="/images/paging/first_over.png" />'"
									onMouseOut="this.src='<s:url value="/images/paging/first.png" />'"
									title="First" width="24" height="24" border="0px" class="dblk">
							</a>
							</td>
							<td style="padding-left: 0px" id="clientPreviousPageTD"><a
								href="javascript: clientPreviousPage();"> <img
									src="<s:url value="/images/paging/prev.png" />"
									onMouseOver="this.src='<s:url value="/images/paging/prev_over.png" />'"
									onMouseOut="this.src='<s:url value="/images/paging/prev.png" />'"
									title="Previous" width="24" height="24" border="0px"
									class="dblk"> </a>
							</td>
							<td id="clientFirstDarkTD"><img
								src="<s:url value="/images/paging/firstd.png" />" width="24"
								height="24" class="dblk">
							</td>
							<td style="padding-left: 0px" id="clientPreviousDarkTD"><img
								src="<s:url value="/images/paging/prevd.png" />" width="24"
								height="24" class="dblk">
							</td>
							<td nowrap="nowrap" style="padding: 3px 4px 0px 4px;"
								id="clientPageCenterTD">1 / 1</td>
							<td style="padding-right: 0px" id="clientNextPageTD"><a
								href="javascript: clientNextPage();"> <img
									src="<s:url value="/images/paging/next.png" />"
									onMouseOver="this.src='<s:url value="/images/paging/next_over.png" />'"
									onMouseOut="this.src='<s:url value="/images/paging/next.png" />'"
									title="Next" width="24" height="24" border="0px" class="dblk">
							</a>
							</td>
							<td id="clientLastPageTD"><a
								href="javascript: clientLastPage();"> <img
									src="<s:url value="/images/paging/last.png" />"
									onMouseOver="this.src='<s:url value="/images/paging/last_over.png" />'"
									onMouseOut="this.src='<s:url value="/images/paging/last.png" />'"
									title="Last" width="24" height="24" border="0px" class="dblk">
							</a>
							</td>
							<td style="padding-right: 0px" id="clientNextDarkTD"><img
								src="<s:url value="/images/paging/nextd.png" />" width="24"
								height="24" class="dblk">
							</td>
							<td style="padding-left: 2px" id="clientLastDarkTD"><img
								src="<s:url value="/images/paging/lastd.png" />" width="24"
								height="24" class="dblk">
							</td>
							<td valign="bottom" style="padding: 0px 2px 0px 12px;"><s:textfield
									name="clientGotoPage" id="clientGotoPage"
									cssStyle="width: 35px;" />
							</td>
							<td><a href="javascript: clientGotoPage();"> <img
									src="<s:url value="/images/paging/gotopage.png" />"
									onMouseOver="this.src='<s:url value="/images/paging/gotopage_over.png" />'"
									onMouseOut="this.src='<s:url value="/images/paging/gotopage.png" />'"
									title="Go to page" width="24" height="24" border="0px"
									class="dblk"> </a>
							</td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td><label id="clientInfoLabel"></label></td>
				<td>
					<div
						style="height: 300px; width: 600px; overflow-x: scroll; overflow-y: scroll;">
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%" id="clientInfoTable">
							<tr>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.macAddress" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.ipAddress" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.hostname" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.apName" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.connectionTime" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.rssi" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.authenticationMethod" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.encryptionMethod" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.cwpUsed" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.radioMode" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.ssid" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.vlan" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.userProfile" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.channel" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.client.title.transmissionRate" /></th>
							</tr>
							<s:generator separator="," val="%{' '}" count="12">
								<s:iterator>
									<tr>
										<td class="list" colspan="16">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</table>
					</div></td>
			</tr>
		</table>
	</div>
</div>
<div id="neighborInfoPanel" style="display: none;">
	<div class="hd" id="neighborTitle">
		<s:text name="topology.menu.neighbor.information" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td><label id="neighborInfoLabel"></label></td>
				<td>
					<div
						style="height: 300px; width: 600px; overflow-x: scroll; overflow-y: scroll;">
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%" id="neighborInfoTable">
							<tr>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.neighborHostname" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.neighborNodeId" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.connectionTime" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.linkCost" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.rssi" /></th>
								<th align="left" nowrap="nowrap"><s:text
										name="topology.neighbor.title.linkType" /></th>
							</tr>
							<s:generator separator="," val="%{' '}" count="12">
								<s:iterator>
									<tr>
										<td class="list" colspan="6">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</table>
					</div></td>
			</tr>
		</table>
	</div>
</div>
<div id="cliInfoPanel" style="display: none;">
	<div class="hd" id="cliInfoTitle">Dialog</div>
	<div class="bd">
		<div id="bd_top" class="bd_top">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr id="pingTr" style="display: none;">
					<td class="labelT1" width="100px" style="padding-right: 10px;">
						<s:text name="topology.menu.diagnostics.ping.name" /></td>
					<td><s:textfield id="targetIp" maxlength="15" size="15px" />
					</td>
					<td><input type="button" id="ping" name="ignore" value="Ping"
						class="button" onClick="ping();"></td>
				</tr>
				<tr id="interfaceTr" style="display: none;">
					<td class="labelT1" width="100px"><s:text
							name="topology.map.statistics.interface.name" /></td>
					<td><s:select id="interfaceType" list="%{enumInterfaceType}"
							listKey="key" listValue="value" onchange="retrieveIfDetail();" />
					</td>
				</tr>
				<tr id="sipTr" style="display: none;">
					<td class="labelT1" width="100px"><s:text
							name="topology.menu.alg.sip.label" /></td>
					<td><s:textfield id="callId" maxlength="128" size="64"
							onkeypress="return hm.util.keyPressPermit(event,'name');" /></td>
					<td><input type="button" id="sipCall" name="ignore" value="OK"
						class="button" onClick="sipCalls();"></td>
				</tr>
			</table>
		</div>
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft"></div>
</div>
<s:if test="%{showSyncSGEMenu}">
	<div id="syncSGEPanel" style="display: none;">
		<div class="hd">
			<s:text name="topology.menu.syncWithSGE" />
		</div>
		<div class="bd">
			<div style="background-color: #FFF; padding: 2px;">
				<div>
					<table id="syncSGETable" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td width="260px" class="tableHd"><s:text
									name="hiveap.tools.sync.sge.stage" />
							</td>
							<td class="tableHd"><s:text
									name="hiveap.tools.sync.sge.state" />
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.authAp" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.uncgAp" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.client" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.assoc" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.signal" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.rssiCfg" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td class="tableBd"><s:text
									name="hiveap.tools.sync.sge.stage.rssiuncg" />
							</td>
							<td class="tableBd"><span>&nbsp;</span>
							</td>
						</tr>
					</table>
				</div>
				<div id="syncSGE_message" class="syncSGE_message"
					style="display: none;"></div>
			</div>
		</div>
	</div>
</s:if>
<div id="lldpCdpPanel" style="display: none;">
	<div class="hd" id="lldpCdpTitle">
		<s:text name="topology.menu.lldpcdp.on" />
	</div>
	<div class="bd">
		<s:form action="mapNodes" name="lldpCdpForm">
			<s:hidden name="operation" />
			<s:hidden name="hiveApId" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td height="10"></td>
							</tr>
							<tr>
								<td width="90px" class="labelT1"><s:checkbox name="chkLldp"
										id="chkLldp" onclick="clickLldpCdp('lldp',this.checked);" />
									<s:text name="topology.menu.lldpcdp.lldp" /></td>
								<td class="labelT1"><s:checkbox name="chkCdp" id="chkCdp"
										onclick="clickLldpCdp('cdp',this.checked);" /> <s:text
										name="topology.menu.lldpcdp.cdp" /></td>
							</tr>

							<tr>
								<td height="10"></td>
							</tr>
						</table></td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="OK"
									class="button" onClick="requestLldpCdpAction();" /></td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideLldpCdpPanel();" /></td>
							</tr>
						</table></td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div id="cwpRemovePanel" style="display: none;">
	<div class="hd" id="cwpRemoveTitle">
		<s:text name="hiveAp.update.cwp.remove" />
	</div>
	<div class="bd">
		<s:form action="mapNodes" id="cwpRemoval" name="cwpRemoval">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td><label id="cwpRemoveLabel"></label></td>
							</tr>
						</table></td>
				</tr>
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td>
									<div id="cwpSelection" style="margin: 5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td width="250px"><s:radio
														list="#{'removeAll':'Remove all web page directories'}"
														label="Gender" name="cwpDirectory"
														onclick="disableDirs();" /></td>
												<td><s:radio
														list="#{'remove':'Remove specific web page directory'}"
														label="Gender" name="cwpDirectory"
														onclick="retrieveDirs();" /></td>
											</tr>
										</table>
									</div>
									<div id="cwpDirectories" style="margin: 5px;"></div></td>
							</tr>
						</table></td>
				</tr>
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="Submit"
									class="button" onClick="submitAction('removeCwpDirectory');">
								</td>
								<td><input type="button" name="ignore" value="Cancel"
									class="button" onClick="hideCwpRemovePanel();"></td>
							</tr>
						</table></td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div id="imageBootPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.invokeBackup" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="noteError">
												<s:label id="imageBootPanelMsg"></s:label>
											</td>
										</tr>
										<tr>
											<td><s:radio list="#{'current':'Current HiveOS Image'}"
													label="Gender" name="imageBoot" />
											</td>
										</tr>
										<tr>
											<td height="5px"></td>
										</tr>
										<tr>
											<td><s:radio list="#{'backup':'Backup HiveOS Image'}"
													label="Gender" name="imageBoot" />
											</td>
										</tr>
									</table>
								</div></td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Submit" id="imageBootPanelSubmit"
								class="button"
								onClick="openConfirmBootDialog('<s:text name="topology.menu.hiveAp.invokeBackup"/>');">
							</td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideImageBootPanel();">
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
	</div>
</div>
<div id="turboModeTogglePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.turboModeToggle" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td colspan="3" class="noteInfo">
												<s:text name="topology.menu.hiveAp.turboModeToggle.note"/>
											</td>
										</tr>
										<tr><td style="height: 10px;"></td></tr>
										<tr>
											<td width="80px">
												<s:text name="topology.menu.hiveAp.turboModeToggle.text" />
											</td>
											<td width="50px"><s:radio list="#{'on':'On'}"
													label="Gender" name="turboModeToggle" />
											</td>
											<td><s:radio list="#{'off':'Off'}"
													label="Gender" name="turboModeToggle" />
											</td>
										</tr>
									</table>
								</div></td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Submit" id="turboModeTogglePanelSubmit"
								class="button"
								onClick="submitTurboModeToggle();">
							</td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideTurboModeTogglePanel();">
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
	</div>
</div>
<div id="locateAPPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.locateAP" />
	</div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
						width="100%">
						<tr>
							<td>
								<div style="margin: 5px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1" id="noLedSection" colspan="10"><s:checkbox
													name="noLed" id="noLed"
													onclick="selectNoLed(this.checked);" /> <s:text
													name="topology.menu.hiveAp.locateAP.noLed" /></td>
										</tr>
										<tr>
											<td class="labelT1" width="80px"><s:text
													name="topology.menu.hiveAp.locateAP.color" /></td>
											<td width="130px">
											<select name="locateAPColorMore" id="locateAPColorMore" style="width: 100px;"
												onchange="locateAPColorMoreChange();">
											</select>
											<select name="locateAPColorLess" id="locateAPColorLess"
												style="width: 100px;" onchange="locateAPColorLessChange();">
													<option value="green" style="background-color: #00FF00"
														selected="selected">Green</option>
													<option value="red" style="background-color: #FF0000">Red</option>
													<option value="orange" style="background-color: #FF6000">Orange</option>
													<option value="off" style="background-color: #FFFFFF">Off</option>
											</select>
											</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
													name="topology.menu.hiveAp.locateAP.blinkMode" /></td>
											<td><select name="locateAPBlink" id="locateAPBlink"
												style="width: 100px;">
													<option value="fast-blink" selected="selected">Fast</option>
													<option value="slow-blink">Slow</option>
													<option value="no-blink">Steady</option>
											</select></td>
										</tr>
									</table>
								</div></td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Submit"
								class="button" onClick="submitLocateAP();">
							</td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideLocateAPPanel();">
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
	</div>
</div>
<div id="debugClientPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.clientTrace" />
	</div>
	<div class="bd">
		<iframe id="debug_client" name="debug_client" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var debugClientPanel = null;
function createDebugClientPanel(width, height){
	var div = document.getElementById("debugClientPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("debug_client");
	iframe.width = width;
	iframe.height = height;
	debugClientPanel = new YAHOO.widget.Panel(div, { fixedcenter:"contained", width:(width+20)+"px", visible:false, constraintoviewport:true } );
	debugClientPanel.render();
	div.style.display="";
	overlayManager.register(debugClientPanel);
	debugClientPanel.beforeHideEvent.subscribe(clearDebugClientData);
	debugClientPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
	var resize = createResizer("debugClientPanel");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, debugClientPanel, true);
	resize.on("endResize", function(args){
		debugClientPanelResizeCallback();
	}, debugClientPanel, true);
}
// Create Resize instance, binding it to the 'resizablepanel' DIV
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 780,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
function debugClientPanelResizeCallback(){/**/}
function clearDebugClientData(){
	clientTraceIframeWindow.onUnloadPage();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("debug_client").style.display = "none";
	}
}
function openClientTracePanel(){
	if(isSelectedItemSwitch()){
		warnDialog.cfg.setProperty('text', '<s:text name="topology.menu.troubleshoot.clientTrace"/> is not supported in the platform.');
		warnDialog.show();
		return;
	}

	if(null == debugClientPanel){
		var frameWidth = YAHOO.util.Dom.getViewportWidth();
		var viewportWidth = YAHOO.util.Dom.getViewportWidth()*0.8;
		var viewportHeight = YAHOO.util.Dom.getViewportHeight()*0.8;
		if(viewportWidth >= 835 || (viewportWidth < 835 && 835 < frameWidth)){
			viewportWidth = 835;
		}
		if(viewportHeight >= 560){
			viewportHeight = 560;
		}
		createDebugClientPanel(viewportWidth, viewportHeight);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("debug_client").style.display = "";
	}
	debugClientPanel.show();
	document.forms[formName].operation.value = "initDebugClient";
	document.forms[formName].target="debug_client";
	document.forms[formName].action = "<s:url action='hiveApToolkit' includeParams='none' />";
	document.forms[formName].submit();
	document.forms[formName].target="_self";
	document.forms[formName].action = "<s:url action='hiveAp' includeParams='none' />";
//	var iframe = document.getElementById("debug_client");
//	iframe.src ="<s:url value='mapNodes.action' includeParams='none' />?operation=initDebugClient&hiveApId=1";
}
function updateClientDebugPanelTitle(str){
	if(null != debugClientPanel){
		debugClientPanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.clientTrace"/>"+" - "+str;
	}
}
var clientTraceIframeWindow;
</script>
<div id="packetCapturePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.packetCapture" />
	</div>
	<div class="bd">
		<iframe id="packetCaptureFrame" name="packetCaptureFrame" width="0"
			height="0" frameborder="0" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var packetCapturePanel = null;
function createPacketCapturePanel(width, height){
	var div = document.getElementById("packetCapturePanel");
	width = width || 600;
	height = height || 600;
	var iframe = document.getElementById("packetCaptureFrame");
	iframe.width = width;
	iframe.height = height;
	packetCapturePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	packetCapturePanel.render();
	div.style.display="";
	overlayManager.register(packetCapturePanel);
	packetCapturePanel.beforeHideEvent.subscribe(clearPacketCaptureData);
	packetCapturePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
function clearPacketCaptureData(){
	packetCaptureIframeWindow.clearTimeout(packetCaptureIframeWindow.pollWifi0TimeoutId);
	packetCaptureIframeWindow.clearTimeout(packetCaptureIframeWindow.pollWifi1TimeoutId);
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("packetCaptureFrame").style.display = "none";
	}
}
function openPacketCapturePanel()
{
	if (!checkIsSelectedOneItem())
	{
		return;
	}

	if(null == packetCapturePanel){
		createPacketCapturePanel(600,600);
	}

	var selectedIds = hm.util.getSelectedIds();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("packetCaptureFrame").style.display = "";
	}
	packetCapturePanel.show();
	var iframe = document.getElementById("packetCaptureFrame");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initPacketCapture&hiveApId="+selectedIds[0];
}
var packetCaptureIframeWindow;
</script>
<div id="remoteSnifferPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.remoteSniffer" />
	</div>
	<div class="bd">
		<iframe id="remoteSnifferFrame" name="remoteSnifferFrame" width="0"
			height="0" frameborder="0" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var remoteSnifferPanel = null;

function createRemoteSnifferPanel(width, height){
	var div = document.getElementById("remoteSnifferPanel");
	var iframe = document.getElementById("remoteSnifferFrame");
	iframe.width = width;
	iframe.height = height;
	remoteSnifferPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	remoteSnifferPanel.render();
	div.style.display="";
	overlayManager.register(remoteSnifferPanel);
	remoteSnifferPanel.beforeHideEvent.subscribe(closeRemoteSnifferPanel);
	remoteSnifferPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openRemoteSnifferPanel()
{
	if (!checkIsSelectedOneItem())
	{
		return;
	}

	if (isSelectedItemCVG()) {
		warnDialog.cfg.setProperty('text', '<s:text name="topology.menu.remoteSniffer"/> is not proper for VPN Gateway.');
		warnDialog.show();
		return;
	}

	if(isSelectedItemSwitch()){
		warnDialog.cfg.setProperty('text', '<s:text name="topology.menu.remoteSniffer"/> is not supported in the platform.');
		warnDialog.show();
		return;
	}

	if(null == remoteSnifferPanel){
		createRemoteSnifferPanel(540,260);
	}

	var selectedIds = hm.util.getSelectedIds();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("remoteSnifferFrame").style.display = "";
	}

	remoteSnifferPanel.show();
	var iframe = document.getElementById("remoteSnifferFrame");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initRemoteSniffer&hiveApId="+selectedIds[0];
	//iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initRemoteSniffer";

}

function updateRemoteSnifferTitle(str) {
	if(null != remoteSnifferPanel){
		remoteSnifferPanel.header.innerHTML = "<s:text name="topology.menu.remoteSniffer"/>" + " - " + str;
	}
}

function closeRemoteSnifferPanel() {
	//remoteSnifferIFrameWindow.onHidePage();
	//remoteSnifferPanel.hide();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("remoteSnifferFrame").style.display = "none";
	}
}

function clearRemoteSnifferPanel(){
	remoteSnifferPanel.hide();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("remoteSnifferFrame").style.display = "none";
	}
}

var remoteSnifferIFrameWindow;
</script>
<div id="sshTunnelPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.hiveAp.sshTunnel" />
	</div>
	<div class="bd">
		<iframe id="ssh_tunnel" name="ssh_tunnel" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var sshTunnelPanel = null;
function createSshTunnelPanel(width, height){
	var div = document.getElementById("sshTunnelPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("ssh_tunnel");
	iframe.width = width;
	iframe.height = height;
	sshTunnelPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	sshTunnelPanel.render();
	div.style.display="";
	overlayManager.register(sshTunnelPanel);
	sshTunnelPanel.beforeHideEvent.subscribe(clearSshTunnelData);
	sshTunnelPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
function clearSshTunnelData(){
	sshTunnelIframeWindow.onHidePage();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("ssh_tunnel").style.display = "none";
	}
}
function openSshTunnelPanel(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	var hiveApId = document.getElementById(formName + "_hiveApId").value;
	if(null == sshTunnelPanel){
		createSshTunnelPanel(600,500);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("ssh_tunnel").style.display = "";
	}
	sshTunnelPanel.show();
	var iframe = document.getElementById("ssh_tunnel");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initSshTunnelPanel&hiveApId="+hiveApId;
}
function updateSshTunnelPanelTitle(str){
	if(null != sshTunnelPanel){
		sshTunnelPanel.header.innerHTML = "<s:text name="topology.menu.hiveAp.sshTunnel"/>"+" - "+str;
	}
}
var sshTunnelIframeWindow;
</script>

<s:if test="%{visibleCliWindow}">
	<%-- CLI Window begin --%>
	<div id="cliWindowPanel" style="display: none;">
		<div class="hd">
			<s:text name="hiveap.tools.cliWindow.menu" />
		</div>
		<div class="bd">
			<iframe id="cliWindow" name="cliWindow" width="0" height="0"
				frameborder="0" style="background-color: #999;" src=""> </iframe>
		</div>
	</div>

	<script type="text/javascript">
var cliWindowPanel=null;

function createCLIWindowPanel(width, height){
	var div = document.getElementById("cliWindowPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("cliWindow");
	iframe.width = width;
	iframe.height = height;
	cliWindowPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	cliWindowPanel.render();
	div.style.display="";
	overlayManager.register(cliWindowPanel);
	cliWindowPanel.beforeHideEvent.subscribe(clearCLIWindow);
	cliWindowPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function clearCLIWindow(){
	cliWindowIFrame.onHidePage();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("cliWindow").style.display = "none";
	}
}

function openCLIWindow(){
	if(!checkMultiSelection()){
		return;
	}

	if(null == cliWindowPanel){
		createCLIWindowPanel(700, 550);
	}

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("cliWindow").style.display = "";
	}

	cliWindowPanel.show();

	document.forms[formName].operation.value = "initCLIWindow";
	document.forms[formName].target="cliWindow";
	document.forms[formName].action = "<s:url action='hiveApToolkit' includeParams='none' />";
	document.forms[formName].submit();
	document.forms[formName].action = "<s:url action='hiveAp' includeParams='none' />";
	document.forms[formName].target="_self";

	//var iframe = document.getElementById("cliWindow");
	//iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initCLIWindow";
}

function updateCLIWindowTitle(str) {
	if(null != cliWindowPanel){
		cliWindowPanel.header.innerHTML = "<s:text name="hiveap.tools.cliWindow.menu"/>" + " - " + str;
	}
}

var cliWindowIFrame = null;
</script>
</s:if>
<%-- CLI Window end --%>

<div id="vlanProbePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.vlan.probe" />
	</div>
	<div class="bd">
		<iframe id="vlan_probe" name="vlan_probe" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var vlanProbePanel = null;
function createVlanProbePanel(width, height){
	var div = document.getElementById("vlanProbePanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vlan_probe");
	iframe.width = width;
	iframe.height = height;
	vlanProbePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	vlanProbePanel.render();
	div.style.display="";
	overlayManager.register(vlanProbePanel);
	vlanProbePanel.beforeHideEvent.subscribe(clearVlanProbeData);
	vlanProbePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
function clearVlanProbeData(){
	vlanProbeIframeWindow.onHidePage();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vlan_probe").style.display = "none";
	}
}
function openVlanProbePanel(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	/**
	if(isSelectedItemSwitch()){
		warnDialog.cfg.setProperty('text', '<s:text name="topology.menu.troubleshoot.vlan.probe"/> is not supported in the platform.');
		warnDialog.show();
		return;
	}**/

	var hiveApId = document.getElementById(formName + "_hiveApId").value;
	if(null == vlanProbePanel){
		createVlanProbePanel(500,400);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vlan_probe").style.display = "";
	}
	vlanProbePanel.show();
	var iframe = document.getElementById("vlan_probe");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initVlanProbePanel&hiveApId="+hiveApId;
}
function updateVlanProbePanelTitle(str){
	if(null != vlanProbePanel){
		vlanProbePanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.vlan.probe"/>"+" - "+str;
	}
}
var vlanProbeIframeWindow;
</script>
<div id="pathProbePanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.path.probe" />
	</div>
	<div class="bd">
		<iframe id="path_probe" name="path_probe" width="0" height="0"
			frameborder="0" style="background-color: #999;" src=""> </iframe>
	</div>
</div>
<script type="text/javascript">
var pathProbePanel = null;
function createPathProbePanel(width, height){
	var div = document.getElementById("pathProbePanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("path_probe");
	iframe.width = width;
	iframe.height = height;
	pathProbePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	pathProbePanel.render();
	div.style.display="";
	overlayManager.register(pathProbePanel);
	pathProbePanel.beforeHideEvent.subscribe(clearPathProbeData);
	pathProbePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}
function clearPathProbeData(){
	pathProbeIframeWindow.onHidePage();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("path_probe").style.display = "none";
	}
}
function openPathProbePanel(){
	if (!checkIsSelectedOneItem()){
		return;
	}
	var hiveApId = document.getElementById(formName + "_hiveApId").value;
	if(null == pathProbePanel){
		createPathProbePanel(500,480);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("path_probe").style.display = "";
	}
	pathProbePanel.show();
	var iframe = document.getElementById("path_probe");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initPathProbePanel&hiveApId="+hiveApId;
}
function updatePathProbePanelTitle(str){
	if(null != pathProbePanel){
		pathProbePanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.path.probe"/>"+" - "+str;
	}
}
var pathProbeIframeWindow;
</script>

<div id="multicastMonitorPanel" style="display: none;">
	<div class="hd"><s:text name="topology.menu.diagnostics.showmulticastmonitor"/></div>
	<div class="bd">
		<iframe id="multicastMonitorFrame" name="multicastMonitorFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var multicastMonitorPanel = null;

function createMulticastMonitorPanel(width, height){
	var div = document.getElementById("multicastMonitorPanel");
	var iframe = document.getElementById("multicastMonitorFrame");
	iframe.width = width;
	iframe.height = height;
	multicastMonitorPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	multicastMonitorPanel.render();
	div.style.display="";
	overlayManager.register(multicastMonitorPanel);
	multicastMonitorPanel.beforeHideEvent.subscribe(closeMulticastMonitorPanel);
	multicastMonitorPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function openMulticastMonitorPanel()
{
	if (!checkIsSelectedOneItem())
	{
		return;
	}

	if(null == multicastMonitorPanel){
		createMulticastMonitorPanel(540,260);
	}

	var selectedIds = hm.util.getSelectedIds();
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("multicastMonitorFrame").style.display = "";
	}

	multicastMonitorPanel.show();
	var iframe = document.getElementById("multicastMonitorFrame");

	//iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initmulticastMonitor&hiveApId="+selectedIds[0];
	iframe.src ="<s:url value='mapNodes.action' includeParams='none' />?operation=initmulticastMonitor&hiveApId="+selectedIds[0];

}

function updateMulticastMonitorTitle(str) {
	if(null != multicastMonitorPanel){
		multicastMonitorPanel.header.innerHTML = "<s:text name="topology.menu.diagnostics.showmulticastmonitor"/>" + " - " + str;
	}
}

function closeMulticastMonitorPanel() {
	multicastMonitorIFrameWindow.onHidePage();

	multicastMonitorPanel.hide();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("multicastMonitorFrame").style.display = "none";
	}
}

var multicastMonitorIFrameWindow;
</script>

<div id="fwPolicyRulePanel" style="display: none;">
	<div class="hd" id="fwPolicyRuleTitle">
		<s:text name="topology.menu.firewall.policy" />
	</div>
	<div class="bd">
		<iframe id="fwPolicyRuleFrame" name="fwPolicyRuleFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
function openFirewallPolicyPanel() {
	if (!checkIsSelectedOneItem()) {
		return;
	}

	if (isSelectedItemHiveAp()) {
		warnDialog.cfg.setProperty('text', "<s:text name="topology.menu.firewall.policy"/>" + " only support " + "<s:text name="config.guid.hiveAp.list.branchRouters.simple"/>" + " and HiveOS Virtual Appliance.");
		warnDialog.show();
		return;
	}

	if(isSelectedItemSwitch()){
		warnDialog.cfg.setProperty('text', "<s:text name="topology.menu.firewall.policy"/>" + " only support " + "<s:text name="config.guid.hiveAp.list.branchRouters.simple"/>" + " and HiveOS Virtual Appliance.");
		warnDialog.show();
		return;
	}

	if (fwPolicyRulePanel == null) {
		createFwPolicyRulePanel();
	}
	showFwPolicyRulePanel();
}

var fwPolicyRulePanel = null;
function createFwPolicyRulePanel(width, height){
	var div = document.getElementById("fwPolicyRulePanel");
	width = width || 400;
	height = height || 150;
	var iframe = document.getElementById("fwPolicyRuleFrame");
	iframe.width = width;
	iframe.height = height;
	fwPolicyRulePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	fwPolicyRulePanel.render();
	div.style.display="";
	overlayManager.register(fwPolicyRulePanel);
	fwPolicyRulePanel.beforeHideEvent.subscribe(clearFwPolicyRuleData);
	fwPolicyRulePanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function showFwPolicyRulePanel(){
	if(null != fwPolicyRulePanel){
		if(YAHOO.env.ua.ie){
			document.getElementById("fwPolicyRuleFrame").style.display = "";
		}
		fwPolicyRulePanel.show();
	}
	var selectedIds = hm.util.getSelectedIds();
	var iframe = document.getElementById("fwPolicyRuleFrame");
	iframe.src ="<s:url value='hiveApToolkit.action' includeParams='none' />?operation=initFwPolicyRulePanel&hiveApId="+selectedIds[0];
}

function hideFwPolicyRulePanel(){
	if(null != fwPolicyRulePanel){
		fwPolicyRulePanel.hide();
	}
}

function clearFwPolicyRuleData(){
	//fix bug 16599
	//fwPolicyRulePanel.onHidePage();

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("fwPolicyRuleFrame").style.display = "none";
	}
}
</script>
<script>
	function getDevTypeEl() {
		var selectedIds = hm.util.getSelectedIds();
		var toCheckId = selectedIds[0];
		return Get("devType"+toCheckId+"_");
	}
	function isSelectedItemCertainType(itemType) {
		var typeHoldEl = getDevTypeEl();
		if (typeHoldEl) {
			if (typeHoldEl.value == itemType) {
				return true;
			}
		}
		return false;
	}
	function isSelectedItemHiveAp() {
		return isSelectedItemCertainType(0);
	}
	function isSelectedItemBr() {
		return isSelectedItemCertainType(1);
	}
	function isSelectedItemCVG() {
		return isSelectedItemCertainType(2);
	}
	function isSelectedItemSwitch() {
		return isSelectedItemCertainType(Device_TYPE_SWITCH);
	}
</script>

<script>
	YAHOO.util.Event.onDOMReady(function(){
		var newMenu = new YAHOO.widget.Menu("newDeviceMenu", { fixedcenter: false, zIndex: 999 });

		newMenu.addItems([
						<s:iterator value="%{apModel}" status="status">
						[
							{ text: '<s:property value="value" />', onclick: { fn: newDeviceClick, obj: <s:property value="key"/> } }
						],
						</s:iterator>
		]);

		newMenu.subscribe("beforeShow", function(){
			var x = YAHOO.util.Dom.getX('n_menutoggle');
			var y = YAHOO.util.Dom.getY('n_menutoggle');
			YAHOO.util.Dom.setX('newDeviceMenu', x);
			YAHOO.util.Dom.setY('newDeviceMenu', y+20);
		});

		newMenu.render();

		YAHOO.util.Event.addListener("n_menutoggle", "click", newMenu.show, null, newMenu);
	});


	function newDeviceClick(p_sType, p_aArgs, p_oValue) {
		document.getElementById(formName + "_hiveApModel").value = p_oValue;
		submitAction('new2');
	}
</script>
<tiles:insertDefinition name="deviceMappingRedirector" />
<tiles:insertDefinition name="updateSimpleModel" />
<script>
	if (scanOverlayResult==null) {
		var div = document.getElementById('scanPanelResult');
		scanOverlayResult = new YAHOO.widget.Panel(div, {
			width:"610px",
			visible:false,
			fixedcenter:"contained",
			draggable:true,
			close: false, 
			modal:true,
			constraintoviewport:true,
			zIndex:1
			});
		scanOverlayResult.render(document.body);
		scanOverlayResult.moveTo(1,1);//fix scroll bar issue
		div.style.display = "";
	}
	scanOverlayResult.hideEvent.subscribe(refreshDeviceInventoryListPage);
	
	function refreshDeviceInventoryListPage() {
		if (importDialogResultOpen==1) {
			var lHref = 'deviceInventory.action?operation=nothing';
			<s:if test="%{selectedL2Feature.key=='configHiveAps' || selectedL2Feature.key=='configVpnGateways' || selectedL2Feature.key=='configBranchRouters' || selectedL2Feature.key=='configSwitches' || selectedL2Feature.key=='configDeviceHiveAps'}">
				lHref = lHref + "&diMenuTypeKey=configHiveAps";
			</s:if>
			<s:else>
				lHref = lHref + "&diMenuTypeKey=managedHiveAps";
			</s:else>
			window.location.href = lHref;
		}
	}
</script>

