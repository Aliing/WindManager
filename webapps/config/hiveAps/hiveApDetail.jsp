<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tiles:insertDefinition name="flashHeader" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css"/>" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-beta-min.js" />"></script>

<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ports.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/portMonitorPage2.css" includeParams="none"/>?v=<s:property value="verParam" />" />
    
<style type="text/css">
span.summaryLabel {
	padding-top: 5px;
	padding-right: 5px;
	font-weight: bold;
}
span.summaryValue {
	padding-top: 5px;
	padding-right: 60px;
}

table.view tr td.list {padding: 2px 5px 2px 4px;height:26px;}

#poeBounceInfoPanel .bd {
	padding: 0;
}

#poeBounceInfoPanel .bd_top {
	background-color: #eee;
}

#poeBounceInfoPanel .cli_viewer {
	padding: 10px;
	overflow: auto;
	height: 25em;
	font-family: sans-serif, Arial, Helvetica, Verdana;
	background-color: #fff;
}

#poeBounceInfoPanel .ft {
	height: 35px;
	padding: 0;
}

#poeBounceInfoPanel .yui-resize-handle-br {
	right: 0;
	bottom: 0;
	height: 8px;
	width: 8px;
	position: absolute;
}

</style>
<tiles:insertDefinition name="portInterfaceScript" />
<script>
var formName = 'hiveApMonitor';
function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
	fetchHiveAPData();

	// for switch
	<s:if test="%{blnSwitch || blnSwitchAsBr}">
	// init the port group section
	//var portCount = '<s:property value="portGroup.portNum"/>';
	//if (portCount.length > 0) {
		$('#portGroupSection').portsConfig(
				{//portNum: <s:property value="portGroup.portNum"/>,
					deviceModels: [<s:property value="dataSource.hiveApModel"/>], //SR24, option for group, array of device modes
					deviceType: <s:property value="dataSource.deviceType"/>, // switch, option for group
					mode: 4,
					mouseEvent: {
						enter: requestPortInfo,
						leave: hidePortInfoPanel
					}
				});

		// update port group status with hm port config
		var dataText = '<s:property value="portGroup.portsBasicData"/>';
		if(dataText.length > 0) {
			var statusData = eval("("+dataText+")");
			if(statusData) {
				// init the port status
				$('#portGroupSection').portsConfig('update', statusData);
			}
		}

		// update port group status with port info from device
		var devicePortData = '<s:property value="portStatusData"/>';
		devicePortData = devicePortData.replace(/\&quot;/g, "\"");
		if(devicePortData.length > 0) {
			var deviceStatusData = eval("("+devicePortData+")");
			if(deviceStatusData) {
				// init the port status
				$('#portGroupSection').portsConfig('updatePortClaxx', deviceStatusData);
			}
		}
	//}
	</s:if>
	
	// fix bug 24060, revmove link anchor from title which added by flash on IE.
	var winTitle = document.title;
	var titleSplit = winTitle.split("#");
	if (titleSplit != null && titleSplit.length > 1) {
		document.title = titleSplit[0];
	}
}
function onUnloadPage() {
}

function fetchHiveAPData(){
	fetchPower();
}

function fetchPower(){
	var powerEl = document.getElementById("poePower");
	if(powerEl){
		url = "<s:url action='hiveApMonitor' includeParams='none' />" + "?operation=fetchPoePowerStatus&hiveApId=" + <s:property value="%{id}"/> + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: updatePowerInfo, failure:fetchPowerFailed,timeout: 30000}, null);
	}
}

function updatePowerInfo(o){
	eval("var result = " + o.responseText);
	if(result.msg){
		document.getElementById("poePower").innerHTML = result.msg;
	}
}

function fetchPowerFailed(){
	// dummy
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
}
var ssidListMenu = null;
function createSsidListMenu(){
	ssidListMenu = new YAHOO.widget.Menu("ssidMenu", { fixedcenter: false });
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveAp" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln(' \'<s:property value="%{changedHiveApName}" />\'</td>');
}

function synchronizeApInfo(targetId){
	if(targetId == undefined){
		return;
	}
	url = "<s:url action='mapNodes' includeParams='none' />" + "?operation=syncApInfo&hiveApId=" + targetId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: synchronizeResult, failure:synchronizeFail,timeout: 30000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function showNeighborDetails(apId, domainId){
	url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=showNeighborDetails&macAddress=" + apId + "&domainId=" + domainId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: synchronizeResult, failure:abortResult,timeout: 30000}, null);
}

var abortResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

var synchronizeResult = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	debug("clear result=" + o.responseText);
	eval("var result = " + o.responseText);
	if(result.success){
		var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=showHiveApDetails&id=" + result.id + "&ignore=" + new Date().getTime();
//		window.location.replace(redirect_url);
		window.location.href = redirect_url;
	}else{
		if(warnDialog != null){
			if(result.msg != null){
				warnDialog.cfg.setProperty('text', result.msg);
				warnDialog.show();
			}
		}
	}
}

var synchronizeFail=function(o){
	var id=document.getElementById("hiveAp_id").value;
	var redirect_url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=showHiveApDetails&id=" + id + "&ignore=" + new Date().getTime();
	window.location.href = redirect_url;
}

function clearErrorCounters(targetId){
	if(targetId == undefined){
		return;
	}
	url = "<s:url action='hiveApMonitor' includeParams='none' />?operation=clearErrorCounters&hiveApId=" + targetId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: clearErrorCountersResult, failure: clearErrorCountersFail,timeout: 30000}, null);
/* 	if(waitingPanel != null){
		waitingPanel.show();
	} */
}

var clearErrorCountersResult = function(o) {
/* 	if(waitingPanel != null){
		waitingPanel.hide();
	} */
	debug("clearErrorCountersResult" + o.responseText);
	eval("var result = " + o.responseText);
	if(result.success){
		// show success msg
		showInfoDialog(result.msg);
		
		// refresh monitor page
		var id=document.getElementById("hiveAp_id").value;
		synchronizeApInfo(id);
	}else{
		if(result.msg != null){
			showWarnDialog(result.msg);
		}
	} 
}

var clearErrorCountersFail=function(o){
	debug("clearErrorCountersFail");
	showWarnDialog('<s:text name="info.clear.error.counters.failed" />');
}

function showSsidDetails(ssid, domainId){
	url = "<s:url action='hiveAp' includeParams='none' />" + "?operation=showSsidDetails&ssidName=" + ssid + "&domainId=" + domainId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkSsidResult, failure:abortResult,timeout: 30000}, null);
}

var x, y;
function locateXY(event){
	x = (document.body.scrollLeft == 0 ? document.documentElement.scrollLeft : document.body.scrollLeft) + event.clientX;
	y = (document.body.scrollTop == 0 ? document.documentElement.scrollTop : document.body.scrollTop) + event.clientY;
}

var checkSsidResult = function(o){
	eval("var result = " + o.responseText);
	if(result.msg){
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', result.msg);
			warnDialog.show();
		}
	}else if(result.ssids){
		var ssids = result.ssids;
		if(ssids.length == 1){
			var ssid = ssids[0];
			var redirect_url;
			<s:if test="%{fullMode}">
				redirect_url = "<s:url action='ssidProfilesFull' includeParams='none' />" + "?operation=edit&id=" + ssid.id 
							+ "&domainId=" + ssid.domainId 
							+ "&manualLstForward=deviceDetailForward"
							+ "&fromObjId=" + <s:property value='dataSource.id'/>
							+ "&ignore=" + new Date().getTime();
			</s:if>
			<s:else>
			redirect_url = "<s:url action='ssidProfiles' includeParams='none' />" + "?operation=edit&id=" + ssid.id 
							+ "&domainId=" + ssid.domainId 
							+ "&manualLstForward=deviceDetailForward"
							+ "&fromObjId=" + <s:property value='dataSource.id'/>
							+ "&ignore=" + new Date().getTime();
			</s:else>

			window.location.href = redirect_url;
		}else{
			if(null == ssidListMenu){
				createSsidListMenu();
			}else{
				var items = ssidListMenu.getItems();
				for(var i=items.length; i>0; i--){
					ssidListMenu.removeItem(i-1);
				}
			}
			for(var i=0; i<ssids.length; i++){
				var ssid = ssids[i];
				var redirect_url;
				<s:if test="%{fullMode}">
					redirect_url = "<s:url action='ssidProfilesFull' includeParams='none' />" + "?operation=edit&id=" + ssid.id 
							+ "&domainId=" + ssid.domainId 
							+ "&manualLstForward=deviceDetailForward"
							+ "&fromObjId=" + <s:property value='dataSource.id'/>
							+ "&ignore=" + new Date().getTime();
				</s:if>
				<s:else>
				redirect_url = "<s:url action='ssidProfiles' includeParams='none' />" + "?operation=edit&id=" + ssid.id 
							+ "&domainId=" + ssid.domainId 
							+ "&manualLstForward=deviceDetailForward"
							+ "&fromObjId=" + <s:property value='dataSource.id'/>
							+ "&ignore=" + new Date().getTime();
				</s:else>
				var item = {text: ssid.name, url: redirect_url};
				ssidListMenu.addItem(item)
			}
			ssidListMenu.render(document.body);
			//YAHOO.util.Dom.setX('ssidMenu', x + 5);
			//YAHOO.util.Dom.setY('ssidMenu', y + 10);
			ssidListMenu.cfg.setProperty("xy",[x+5,y+5]);
			ssidListMenu.show();
		}
	}
}

var clientId;
function showLocation() {
	var selectedIds = hm.util.getSelectedIds();
	if(selectedIds.length != 1){
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}
	clientId = selectedIds[0];
	requestLocation("locateRogue", clientId, locationSucceeded);
}
function showClientLocation(id) {
	clientId = id;
	requestLocation("locateRogue", clientId, locationSucceeded);
}
function requestLocation(operation, clientId, callback) {
	url = "<s:url action='maps' includeParams='none' />?operation=" + operation + "&clientId=" + clientId;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:callback,failure:requestFailed,timeout: 10000}, null);
}
var locationInfoDialog = null;
var locationSucceeded = function(o){
	eval("var result = " + o.responseText);
	if (locationInfoDialog == null) {
		locationInfoDialog = new YAHOO.widget.SimpleDialog("locationInfoDialog",
              { width: "550px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal: true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
              } );
     	locationInfoDialog.setHeader("Operation failed.");
     	locationInfoDialog.render(document.body);
	}
	if (result.msg) {
		locationInfoDialog.cfg.setProperty('text', result.msg);
		locationInfoDialog.show();
	}
	if (result.mapId) {
		var url = "<s:url action='maps' includeParams='none' />?operation=mapRogue&selectedMapId=" + result.mapId + "&clientId=" + clientId;
		window.location.href = url;
	}
}
var requestFailed = function(o) {
	warnDialog.cfg.setProperty('text', "Operation failed.  Please try again later.");
	warnDialog.show();
}

function hidePortInfoPanel() {
	/*if(window.console) {
		console.debug('hide==' + new Date().getTime());
	}*/
	mouseOutFlag = true;
	if(null != portInfoPanel){
		portInfoPanel.cfg.setProperty('visible', false);
	}
}

function showPortInfoPanel() {
	/*if(window.console) {
		console.debug('show==' + new Date().getTime());
	}*/
	if (mouseOutFlag) {
		// if mouse already leave port, don't show portInfoPanel
		return;
	}
	if(null == portInfoPanel){
		createPortInfoPanel();
	}
	var lm_x = YAHOO.util.Dom.getX('tdPortInfo');
	var lm_y = YAHOO.util.Dom.getY('tdPortInfo');
	portInfoPanel.cfg.setProperty("xy", [lm_x, lm_y]);
	portInfoPanel.cfg.setProperty('visible', true);
}

var portInfoPanel = null;
/*
 * flag for fix issue:
	 mouse move out fast enough, but port info panel still show.
 */
var mouseOutFlag = false;
function createPortInfoPanel() {
	var div = window.document.getElementById('portInfoPanel');
	portInfoPanel = new YAHOO.widget.Panel(div, { width:"500px", close:false, visible:false, draggable:true } );
	portInfoPanel.render(document.body);
	div.style.display = "";
}

function getPortTypeString(portType) {
	var portTypeString = '';
	var portTypeInt = parseInt(portType);
	switch (portTypeInt) {
		case 1: //phone & data
			portTypeString = '<s:property value="%{portTypePhoneDataString}"/>';
			break;
		case 2: //
			portTypeString = '<s:property value="%{portTypeAerohiveApString}"/>';
			break;
		case 3: // monitor/mirror
			portTypeString = '<s:property value="%{portTypeMonitorString}"/>';
			break;
		case 4: // access
			portTypeString = '<s:property value="%{portTypeAccessString}"/>';
			break;
		case 5: // 8021.q
			portTypeString = '<s:property value="%{portType8021QString}"/>';
			break;
		case 6: // wan
			portTypeString = '<s:property value="%{portTypeWanString}"/>';
			break;
		default: // default
			portTypeString = '<s:property value="%{portTypeAccessString}"/>';
	}
	return portTypeString;

}

function vlanNumSort(a, b) {
	return a - b;
}

function addVlansToResult(result, tmpStart, tmpEnd, tmpVlanCount) {
    if (result && result.length > 0) {
        result += ', '
    }
    if (tmpVlanCount >= 3) {
        // count > 3, need join Vlan IDs with '-', e.g. '1,2,3' -> '1-3'
        result +=  tmpStart + '-' + tmpEnd;
    } else if (tmpVlanCount == 2) {
        // e.g. '1,2' -> '1,2'
        result += tmpStart + ',' + tmpEnd;
    } else if (tmpVlanCount == 1) {
        // e.g. '1' -> '1'
        result += tmpStart;
    }
    
    return result;
}

/**
 * display vlans more simple, easy for view
 * example: origin vlans = 1,2,3,4,5,6,7,8,10,11,100,102,103,104
 *          simlified vlans = 1-11, 100, 102-104
 */
function formatVlans (vlans) {
    var result = '';
    //var vlans = '200,1,2,3,4,5,6,7,8,10,11,100,102,103,104'
    if (vlans != null && vlans.length > 0) {
        var vlanArr = vlans.split(',');
        if (vlanArr != null && vlanArr.length > 0) {
            vlanArr.sort(vlanNumSort);
            //alert(vlanArr.join());
            
            if (vlanArr.length == 1) {
                result = vlanArr[0];
            } else {
                var tmpStart = vlanArr[0];
                var tmpEnd = vlanArr[0];
                var tmpVlanCount = 1;
                for (var i = 1; i < vlanArr.length; i ++) {
                    if ((vlanArr[i] - tmpEnd) > 1) {
                        // Vlan ID is not incremented by 1, add vlans from tmpStart to tmpEnd to result
                        result = addVlansToResult(result, tmpStart, tmpEnd, tmpVlanCount);
                        
                        // reset value of tmpStart & tmpEnd & tmpVlanCount
                        tmpStart = vlanArr[i];
                        tmpEnd = vlanArr[i];
                        tmpVlanCount = 1;
                    } else {
                        // Vlan ID is incremented by 1
                        tmpEnd = vlanArr[i];
                        tmpVlanCount ++;
                    }
                    
                    // the last item
                    if (i == vlanArr.length -1) {
                        result = addVlansToResult(result, tmpStart, tmpEnd, tmpVlanCount);
                    }
                }
            }
        }
    }
    //alert("result=" + result);
    return result;
}

/**
 * 
 * hmportType     :1:phone & data, 2:hiveap(now is not used), 3:monitor/mirror, 4:access, 5:8021.q, 6:WAN, -1 default(Access)
 * deviceporttype :0:Access, 1:Trunk, 2:WAN
 * deviceMirrorPortDest : 0:not dest mirror port, 1:is dest mirror port
 * match case:   hm              device  porttype
 *               4/-1/3          0       access
 *               6               2       wan
 *               others          1       trunk
 *
 */
function checkPortTypeMatch(hmPortType, devicePortType, deviceMirrorPortDest) {
	if (hmPortType == 4 || hmPortType == -1) {
		// port type in hm config is Access
		if (devicePortType == 0) {
			// on device port is Acces too, return match
			return true;
		}
	} else if (hmPortType == 6) {
		// port type in hm config is Wan
		if (devicePortType == 2) {
			// on device port is Wan too, return match
			return true;
		}
	} else if (hmPortType == 3) {
		// port type in hm config is mirror
		if (deviceMirrorPortDest == 1) {
			// on device port is dest mirror port too, return match
			return true;
		}
	} else {
		// port type in hm config is not -1,3,4,6; can only be 1,5(should be trunk)
		if (devicePortType == 1) {
			// on device port is trunk too, return match
			return true;
		}
	}
	
	// other wise, return not match
	return false;
}

var PORT_CLASS = {
/* 	UP_PORT: 'up-port',
	DOWN_PORT: 'down-port',
	ERROR_PORT: 'error-port',
	UP_PORT_POE: 'up-port-poe',
	DOWN_PORT_POE: 'down-port-poe',
	ERROR_PORT_POE: 'error-port-poe',
	NOT_CONNECTED_USB_PORT: 'disabled-port' */
	UP: 'up',
	DOWN: 'down',
	ERROR: 'error',
	DISABLED: 'disabled',
	POE: 'poe'
};

var PORT_MODE = {
		PORT_ETH:'ETH',
		PORT_SFP:'SFP',
		PORT_USB:'USB'
};

/**
 * portElId : USB_0, ETH_1, SFP_1
   portFalg : USB, ETH, SFP
   linkState : 0: admin enable & down, 1: admin enable & up, 2: admin enable & down by stpd, 
   			   3: admin disable (must be down), 99: usb disabled
   portPoe : true/false
   portIsError : true/false
   
   add this method for bug 23221 fix
 */
function refreshPortsColorAndIcon2(portElId, portMode, linkState, portPoe, portIsError) {
	//debug('refreshPortsColorAndIcon2, portElId=' +portElId+', portMode=' + portMode + ', linkState=' + linkState + ', portPoe=' + portPoe + ', portIsError=' + portIsError);
	   
	// remove all color css if exist
	$('#'+portElId).removeClass(PORT_CLASS.UP + ' ' + PORT_CLASS.DOWN + ' ' + PORT_CLASS.ERROR +  ' ' + PORT_CLASS.POE + ' ' + PORT_CLASS.DISABLED);
	
	if (PORT_MODE.PORT_USB == portMode) {
		// USB (has 3 status: up/down/not connected)
		if (linkState == 1) {
			// connected and up
			$('#'+portElId).addClass(PORT_CLASS.UP);
		} else if (linkState == 0) {
			// connected but down
			$('#'+portElId).addClass(PORT_CLASS.DOWN);
		} else if (linkState == 99 || linkState == 3) {
			// not connected (99) or admin disable (3)
			$('#'+portElId).addClass(PORT_CLASS.DISABLED);
		}
	} else {
		// ETH/SFP (has 3 status: up/down/error, with/without POE)
		if (portIsError) {
			// ERROR
			$('#'+portElId).addClass(PORT_CLASS.ERROR);
		} else {
			if (linkState == 1) {
				// UP
				$('#'+portElId).addClass(PORT_CLASS.UP);
			} else if (linkState == 3) {
				// admin disable
				$('#'+portElId).addClass(PORT_CLASS.DISABLED);
			}  else {
				// DOWN (0/2)
				$('#'+portElId).addClass(PORT_CLASS.DOWN);
			}
		}
		// POE (only ETH has POE)
		if (PORT_MODE.PORT_ETH == portMode && portPoe) {
			$('#'+portElId).addClass(PORT_CLASS.POE);
		}
	}
}

/**
 * portArg:
	 {
	 	ETH:true/false
	 	SFP:true/false
	 	USB:true/false
	 	accessProfileId:-1
	 	groupNum:-1
	 	port:usb start from 0, others start from 1 (ETH:1-24/1-48, SFP:1-4)
	 	portType:1:phone & data, 2:hiveap, 3:monitor/mirror, 4:access, 5:8021.q, 6:WAN
	 }
 *
 */
function requestPortInfo(portArg) {

	//debug(portArg);
	
	// mouse in
	mouseOutFlag = false;

	var macEl = document.getElementById("macAddress");
	var swEth48El = document.getElementById("swEth48");
	var portMode = portArg.ETH ? PORT_MODE.PORT_ETH : (portArg.SFP ? PORT_MODE.PORT_SFP : PORT_MODE.PORT_USB); // ETH/SFP/USB
	var portElId = portMode + "_" + portArg.port; // e.g. USB_0, ETH_1, SFP_1 (usb start from 0, others start from 1)
	/*if (portMode == 'USB') {
		// now use port color indicate the only one info: connected/disconnected
		return; // if wanna show more info, need modify here.
	
		// usb
		var usbinfo = '<s:property value="%{usbStatusString}"/>';
		var contentDiv = document.getElementById("bd_content");
		if (contentDiv != null) {
			contentDiv.innerHTML = usbinfo;

			// set title
			var portTitle = document.getElementById("portInfoTitle");
			portTitle.innerHTML = portMode + ' <s:text name="monitor.hiveAp.switch.port.overview" />';

			showPortInfoPanel();
		}
	} else {*/
		// ETH or SFP or USB
		url = "<s:url action='hiveApMonitor' includeParams='none' />?operation=fetchSwPortInfo" + "&macAddress=" + macEl.value + "&portNo=" + portArg.port + "&portMode=" + portMode + "&swEth48=" + swEth48El.value + "&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:handlePortInfoResult,failure:requestPortInfoFailed,timeout: 10000, argument: {"portType": portArg.portType, "portElId" : portElId, "portMode" : portMode}}, null);
	//}
}

function requestPortInfoFailed() {
	// do nothing
}

function handlePortInfoResult(o) {
	
	eval("var result = " + o.responseText);
	var portElId = o.argument.portElId; // e.g. USB_0, ETH_1, SFP_1 (usb start from 0, others start from 1)
	var portMode = o.argument.portMode; // ETH/SFP/USB
	var contentDiv = document.getElementById("bd_content");
	
	// over all STP state
	var overAllStpState = '<s:property value="%{stpState}"/>';
	
	if(result.success){
		//var portType = result.portType;
		var portTypeHmConfig = o.argument.portType; // code, 1:phone & data, 2:hiveap, 3:monitor/mirror, 4:access, 5:8021.q, 6:WAN
		var portTypeFromDevice = result.portType; // code, 0:Access, 1:Trunk, 2:WAN
		var portTypeStringFromDevice = result.portTypeString; // name
		var upTimePort = result.upTimePort;
		var voiceVLANs = formatVlans(result.voiceVLANs);
		var dataVLANs = formatVlans(result.dataVLANs);
		var linkState = result.linkState; // 0:Down, 1:Up, 2:Down by stpd
		var linkStateString = result.linkStateString;
		var lineProtocol = result.lineProtocol;
		var authenticationState = result.authenticationState;
		var authenticationStateString = result.authenticationStateString;
		var stpMode = result.stpMode;
		var stpRole = result.stpRole; // if current port's stpRole = 4:disabled, not show STP info
		var stpRoleString = result.stpRoleString;
		var stpState = result.stpState;
		var portPoe = result.portPoe;
		var linkStatePortChannel = result.linkStatePortChannel; // 0:Down, 1:Up
		var upTimePortChannel = result.upTimePortChannel;
		var portChannel = result.portChannel;
		var portChannelMembers = result.portChannelMembers;
		var destMirrorPort = result.destMirrorPort; // 0:not dest mirror, 1: is dest mirror port
		var pvid = result.pvid; // trunk: native vlan, access: access vlan
		var receivingError = result.receivingError; // receiving error
		var transmissionError = result.transmissionError; // transmission error
		var portIsError = false;
		var portErrors = '';
		if (receivingError) {
			portIsError = true;
			portErrors = receivingError;
		}
		if (transmissionError) {
			portIsError = true;
			/* if (receivingError) {
				portErrors += '<BR>';
			} */
			portErrors += transmissionError;
		}
		//debug('portIsError=' +portIsError+', receivingError=' + receivingError + ', transmissionError=' + transmissionError + ', portErrors=' + portErrors);
				
		// refresh port color and icon
		//refreshPortsColorAndIcon(portElId, portMode, linkState, portPoe, portIsError);
		refreshPortsColorAndIcon2(portElId, portMode, linkState, portPoe, portIsError);  // fix bug 23221
		
		// now usb port only refresh up/down status, no show popup dialog
		if (PORT_MODE.PORT_USB == portMode) {
			return;
		}

		if (mouseOutFlag) {
			// if mouse already leave port, don't show portInfoPanel
			return;
		}
		
		var isPortChannelMember = false;
		
		// set content
		var reult =  "<table>";
		if (portChannel && portChannel.length > 0) {
			isPortChannelMember = true;
			// port channel members
			reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.port.channel" />' + "</td><td></td><td>" + portChannel + " (" + portChannelMembers + ")"+ "</td></tr>";
			if (linkStatePortChannel == 1) {
				// only when port channel up, show port channel up time
				reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.up.time.port.channel" />' + "</td><td></td><td>" + upTimePortChannel + "</td></tr>";
			}
		}
		
		//var reult =  "<table><tr><td>"+ '<s:text name="monitor.hiveAp.switch.port.type" />' + "</td><td></td><td>" + portTypeStringFromDevice + " (" + getPortTypeString(portTypeHmConfig) + ")</td></tr>";
		var notMatchIcon = "<img width='16' name='indication' alt='Mismatch' title='' src='" + '<s:property value="%{contextPathStr}"/>' + "/images/config-mismatch.png' class='dblk' style='cursor: pointer;' onclick='' />";
		var matchIcon = "";
		var showMatchInfo = "";
		var portTypeMatch = checkPortTypeMatch(portTypeHmConfig, portTypeFromDevice, destMirrorPort);
		if (!portTypeMatch) {
			showMatchInfo = notMatchIcon;			
		}
		// only show port type configed on hm
		var reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.port.type" />' + "</td><td>" +showMatchInfo+"</td><td>" + getPortTypeString(portTypeHmConfig) + "</td></tr>";
		if (linkState == 1) {
			// only when port up, show up time
			reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.up.time" />' + "</td><td></td><td>" + upTimePort + "</td></tr>";
		}
		if ((portTypeFromDevice == 0 || portTypeFromDevice == 1) && destMirrorPort != 1) {
			// only Access/Trunk port and port is not dest mirror port has vlans (port in WAN mode,no vlan infos)
			if (portTypeFromDevice == 1 && !isPortChannelMember) {
				// only Turnk port has voice vlans & port channel member do not need show voice vlan
				reult = reult + '<tr><td>'+ '<s:text name="monitor.hiveAp.switch.voice.vlans" />' + '</td><td></td><td width="310px" style="word-wrap:break-word;word-break:break-all">' + voiceVLANs + '</td></tr>';	
			}
			reult = reult + '<tr><td>'+ '<s:text name="monitor.hiveAp.switch.data.vlans" />' + '</td><td></td><td width="310px" style="word-wrap:break-word;word-break:break-all">' + dataVLANs + '</td></tr>';
		}
		// show trunk port native vlan (fix bug 23519)
		if (portTypeFromDevice == 1) {
			// only Trunk port has native vlan
			reult = reult + '<tr><td>'+ '<s:text name="monitor.hiveAp.switch.native.vlan" />' + '</td><td></td><td width="310px" style="word-wrap:break-word;word-break:break-all">' + pvid + '</td></tr>';
		}
		reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.link.state" />' + "</td><td></td><td>" + linkStateString + "</td></tr>";
		reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.line.protocol" />' + "</td><td></td><td>" + lineProtocol + "</td></tr>";
		if ((portTypeFromDevice == 0 || portTypeFromDevice == 1) && authenticationState != 64 && authenticationState != 0 && !isPortChannelMember) {
			// Access/Trunk port(port in WAN mode,no auth info) & port auth not disabled (64:disabled) & not 0(No data and no voice vlan success) & is not PortChannelMember
			reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.authentication.state" />' + "</td><td></td><td>" + authenticationStateString + "</td></tr>";	
		}
		if ((portTypeFromDevice == 0 || portTypeFromDevice == 1) && linkState == 1 && overAllStpState == 'Enabled' && destMirrorPort != 1) {
			// only Access/Trunk port & port is up & device's STP is enabled && port is not dest mirror port
			if (stpRole != 4) {
				// port STP enabled
				reult = reult + "<tr><td>"+ '<s:text name="config.switchSettings.stp.mode" />' + "</td><td></td><td>" + stpMode + "</td></tr>";
				reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.stp.role" />' + "</td><td></td><td>" + stpRoleString + "</td></tr>";
				reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.stp.state" />' + "</td><td></td><td>" + stpState + "</td></tr>";
			} else {
				// if STP role is disabled, means port STP is disabled
				reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.stp" />' + "</td><td></td><td>" + stpRoleString + "</td></tr>";
			}
		}
		if (portIsError) {
			reult = reult + "<tr><td>"+ '<s:text name="monitor.hiveAp.switch.port.error" />' + "</td><td></td><td>" + portErrors + "</td></tr>";
		}
		reult = reult + "</table>";

		contentDiv.innerHTML=reult
	} else {
		if (mouseOutFlag) {
			// if mouse already leave port, don't show portInfoPanel
			return;
		}
		// now usb port only refresh up/down status, no show popup dialog
		if (PORT_MODE.PORT_USB == portMode) {
			return;
		}
		contentDiv.innerHTML= result.msg;
	}
	
	// set title
	var portName = result.portName;
	var portTitle = document.getElementById("portInfoTitle");
	portTitle.innerHTML = portName + ' <s:text name="monitor.hiveAp.switch.port.overview" />';

	showPortInfoPanel();
}

var debug = function(msg) {
    if(window.console && console.debug) {
        if(typeof msg == 'string' || typeof msg == 'number' || typeof msg == 'boolean') {
            console.debug(msg);
        } else {
            console.dir(msg);
        }
    }
};
</script>
<style>
td.panelLabel {
	padding: 0px 0px 5px 8px;
	color: #003366;
}

td.panelLabel2 {
	height:20px;
	vertical-align:top;
	color: #003366;
}

td.paddingStyle {
	padding-left: 40px;
	vertical-align: top;
}
td.verticalStyle{
   vertical-align: top;
}
</style>

<div id="content"><s:form action="hiveAp">
	<s:hidden name="id" id="hiveAp_id"></s:hidden>
	<s:hidden name="macAddress" id ="macAddress"></s:hidden>
	<s:hidden name="swEth48" id ="swEth48"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
  					<td><input type="button" name="ignore" value="Refresh"
						class="button" onClick="synchronizeApInfo(<s:property value="%{id}"/>);"
						<s:property value="writeDisabled" />></td>
  					<s:if test="%{blnSwitch || blnSwitchAsBr}">
  					<td><input type="button" name="ignore" value="<s:text name="monitor.hiveAp.switch.clear.error.counters"/>"
						class="button" onClick="clearErrorCounters(<s:property value="%{id}"/>);" style="width: 150px;"
						<s:property value="writeDisabled" />></td></s:if>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
				<table style="padding: 6px 4px 5px 4px;" cellpadding="0" cellspacing="0" border="0"  class="editBox" width="<s:if test="%{swEth48}">97%</s:if><s:else>95%</s:else>">
				<tr>
				<td>
				<!--[if lte IE 8]><div class="ie8"><![endif]-->
				<!--[if (gt IE 8)|(!IE)]><!--><div class=""><!-- <![endif]-->
					<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
					<tr><th colspan="4" align="left"><s:text name="monitor.hiveAp.system.glance"/></th></tr>
					<s:if test="%{blnSwitch || blnSwitchAsBr}">
						<tr>
							<td colspan="10" height="5px">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="10" id="swMonitor" class="<s:if test="%{swEth48}">sr48</s:if><s:else>sr24</s:else>">
								<div id="portGroupSection" class="clearfix" <s:if test="%{swEth48}">style="min-width:926px;"</s:if>></div>
							</td>
						</tr>
						<tr>
							<td colspan="10" height="5px" id="tdPortInfo">&nbsp;</td>
						</tr>
					</s:if>
					<tr>
						<td colspan="10">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td><tiles:insertDefinition name="flash" /></td>
								</tr>
							</table>
						</td>
					</tr>
					</table>
					<div>
				</td>
				</tr>
				<tr>
				<td>
				<s:if test="%{blnBranchRouter || blnSwitch || blnSwitchAsBr}">
					<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
					<tr><th colspan="4" align="left"><s:text name="monitor.hiveAp.system.title"/></th></tr>
					<tr><td height="5px"></td></tr>
					<tr>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system"/></b>
									</td>
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="hiveAp.hostName"/></td>
									<td width="260px"><a href='<s:url action="hiveAp" includeParams="none"><s:param name="operation" value="%{'edit2'}"/><s:param name="viewType" value="%{'config'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{domainId}"/></s:url>'><s:property value="%{name}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.ipAddress"/></td>
									<td><s:property value="%{ipAddress}"/></td>								
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="monitor.hiveAp.model"/></td>
									<td><s:property value="%{model}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.type"/></td>
									<td><s:property value="%{deviceType}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.macAddress"/></td>
									<td><s:property value="%{macAddress}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.sw.version"/></td>
									<td><s:property value="%{sw}"/></td>
								</tr>
								<s:if test="blnSwitch || blnSwitchAsBr">
									<tr>
										<td class="panelLabel"><s:text name="monitor.hiveAp.switch.system.temparture"/></td>
										<td><s:property value="%{sysTemparture}"/></td>
									</tr>	
									<tr>
										<td class="panelLabel"><s:text name="monitor.hiveAp.switch.fan.status"/></td>
										<td><s:property value="%{fanStatus}"/></td>
									</tr>
									<s:if test="supportPowerStatus">
									<tr>
										<td class="panelLabel"><s:text name="monitor.hiveAp.switch.power.status"/></td>
										<td ><s:property value="%{powerStatus}"/></td>
									</tr>
									</s:if>
								</s:if>
							</table>
						</td>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system.overview"/></b>
									</td>
								</tr>
								<tr>
									<td class="panelLabel" width="180px"><s:text name="monitor.hiveAp.severity.status"/></td>
									<td width="260px"><a href='<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}"/><s:param name="apId" value="%{macAddress}"/><s:param name="filterVHM" value="%{domainName}"/></s:url>'>
										<s:property escape="false" value="%{status}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.manageStatus"/></td>
									<td><s:property escape="false" value="%{manageStatus}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.origin"/></td>
									<td><s:property escape="false" value="%{origin}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.connectionTime"/></td>
									<td><s:property value="%{upTime}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.topology"/></td>
									<td>
										<s:if test="%{inTopology}">
											<a href='<s:url action="mapNodes" includeParams="none"><s:param name="operation" value="%{'redirect'}"/><s:param name="id" value="%{mapId}"/></s:url>'>
											<s:property value="%{topologyMap}"/></a>
										</s:if>
										<s:else><s:property value="%{topologyMap}"/></s:else>
									</td>
								</tr>
								<s:if test="blnSwitch || blnSwitchAsBr">
									<tr>
										<td class="panelLabel"><s:text name="monitor.hiveAp.switch.util"/></td>
										<td><s:property value="%{swUtilization}"/></td>
									</tr>		
									<tr>
										<td class="panelLabel"><s:text name="config.switchSettings.stp.mode"/></td>
										<td><s:property value="%{stpMode}"/></td>
									</tr>	
									<tr>
										<td class="panelLabel"><s:text name="monitor.hiveAp.switch.stp.state"/></td>
										<td><s:property value="%{stpState}"/></td>
									</tr>																											
								</s:if>
							</table>
						</td>						
					</tr>		
				<s:if test="%{wanDeviceInterfaceList!=null && wanDeviceInterfaceList.size!=0}">
					<tr><td colspan="6" style="border-top:1px solid #CCCCCC;"></td></tr>
					<tr><td height="5px"></td></tr>
					<tr>
					 <td colspan="6" class="panelLabel" align="left">
					    <b><s:text name="geneva_03.monitor.hiveAp.sw.wan"></s:text></b>
					 </td>
				   </tr>
				  <tr>
				    <td colspan="2" valign="top">
					     <table cellpadding="0" cellspacing="0" border="0" width="100%">
					         <s:iterator value="%{wanDeviceInterfaceList}">
					           <tr>
					             <td class="panelLabel" width="220px" height="30px"><s:property value="wanPortName"/>
					               <br><font color="gray">(<s:property value="wanOrderName"/>)</font>
					             </td> 
					             <td valign="top"><s:text name="geneva_03.monitor.hiveAp.sw.ipAddress"></s:text></td> 
					             <td class="paddingStyle"><s:property value="ipAddress"/>
					              <img src="<s:url value="%{wanStatusImg}" />" style="vertical-align: middle;"
					                alt="<s:property value='%{wanStatusImgAlt}'/>" title="<s:property value='%{wanStatusImgAlt}'/>" />
					             </td>
					         </tr>
					        </s:iterator>
					        <s:if test="%{displaySIMCardDetails}">
						         <tr>
						             <td></td> 
						             <td valign="top"><s:text name="geneva_03.monitor.hiveAp.sw.dataRate"/></td> 
						             <td class="paddingStyle">
						              <table>
						                   <tr>
						                        <td height="15px"><s:text name="geneva_03.monitor.hiveAp.sw.dataOverage"/></td>	 
						                   </tr>
						                   <tr>
						                       <td height="15px"><s:property value="%{dwThroughput}"/> - <s:text name="geneva_03.monitor.hiveAp.sw.dataDownload"/></td>
						                   </tr>
						                   <tr>
						                       <td height="15px"><s:property value="%{upThroughput}"/> - <s:text name="geneva_03.monitor.hiveAp.sw.dataUpload"/></td>
						                   </tr>
						              </table>
						             </td>
						         </tr>
					         </s:if>
					     </table>
					  </td>
					  <s:if test="%{displaySIMCardDetails}">
						  <td colspan="2" valign="top">
						     <table cellpadding="0" cellspacing="0" border="0" width="100%" style="margin-left: 10px">
						          <s:iterator value="%{wanDeviceInterfaceList}" status="status">
						             <s:if test="%{#status.index!=0}">
						               <tr><td class="panelLabel" width="220px" height="30px"></td> </tr>
						             </s:if>
					              </s:iterator>
						          <tr>
						             <td class="panelLabel2"><s:text name="geneva_03.monitor.hiveAp.sw.carrier"></s:text></td>
						             <td class="verticalStyle"><s:property value="%{ahRouter.carrier}"/></td>
						          </tr>
						          <tr>
						             <td class="panelLabel2"><s:text name="geneva_03.monitor.hiveAp.sw.imei"></s:text></td>
						             <td class="verticalStyle"><s:property value="%{ahRouter.imei}"/></td>
						          </tr>
						          <tr>
						             <td class="panelLabel2"><s:text name="geneva_03.monitor.hiveAp.sw.simCard"></s:text></td>
						             <td class="verticalStyle"><s:property value="%{ahRouter.simStatus}"/></td>
						          </tr>
						          <tr>
						             <td class="panelLabel2"><s:text name="geneva_03.monitor.hiveAp.sw.signalStrength"></s:text></td>
						             <td class="verticalStyle">
						             <s:if test="%{ahRouter.rssi!=-1 && ahRouter.rssi!=0}">
						              <img class="dinl ver-bot" src="<s:url value="%{ahRouter.barsStatus}" />"/>
						                 <s:property value="%{ahRouter.networkModeType}"/> 
						                 (RSSI:<s:property value="%{ahRouter.showRssiValue}"/> dBm)
						             </s:if>
						             </td>
						          </tr>
						          <tr>
						             <td class="panelLabel2"><s:text name="geneva_03.monitor.hiveAp.sw.availableService"></s:text></td>
						             <td class="verticalStyle"><s:property value="%{ahRouter.systemMode}"/> </td>
						          </tr>
						    </table>
						  </td>
					  </s:if>
					</tr>
				  </s:if>
				 </table>
				</s:if>
				<s:elseif test="%{blnVpnGateway}">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
					<tr><th colspan="4" align="left"><s:text name="monitor.hiveAp.system.title"/></th></tr>
					<tr><td height="5px"></td></tr>
					<tr>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system"/></b>
									</td>
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="hiveAp.hostName"/></td>
									<td width="260px"><a href='<s:url action="hiveAp" includeParams="none"><s:param name="operation" value="%{'edit2'}"/><s:param name="viewType" value="%{'config'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{domainId}"/></s:url>'><s:property value="%{name}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.ipAddress"/></td>
									<td><s:property value="%{ipAddress}"/></td>								
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="monitor.hiveAp.model"/></td>
									<td><s:property value="%{model}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.type"/></td>
									<td><s:property value="%{deviceType}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.macAddress"/></td>
									<td><s:property value="%{macAddress}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.sw.version"/></td>
									<td><s:property value="%{sw}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="config.vpn.gateway.settings.gateways.dynamicRouting"/></td>
									<s:if test="%{dataSource.routingProfile.enableDynamicRouting}">
									<td><s:property escape="false" value="%{dataSource.routingProfile.stringType}"/></td>
									</s:if>
									<s:else>
									<td>&nbsp;</td>
									</s:else>								
								</tr>
							</table>
						</td>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system.overview"/></b>
									</td>
								</tr>
								<tr>
									<td class="panelLabel" width="180px"><s:text name="monitor.hiveAp.severity.status"/></td>
									<td width="260px"><a href='<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}"/><s:param name="apId" value="%{macAddress}"/><s:param name="filterVHM" value="%{domainName}"/></s:url>'>
										<s:property escape="false" value="%{status}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.manageStatus"/></td>
									<td><s:property escape="false" value="%{manageStatus}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.origin"/></td>
									<td><s:property escape="false" value="%{origin}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.connectionTime"/></td>
									<td><s:property value="%{upTime}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.topology"/></td>
									<td>
										<s:if test="%{inTopology}">
											<a href='<s:url action="mapNodes" includeParams="none"><s:param name="operation" value="%{'redirect'}"/><s:param name="id" value="%{mapId}"/></s:url>'>
											<s:property value="%{topologyMap}"/></a>
										</s:if>
										<s:else><s:property value="%{topologyMap}"/></s:else>
									</td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="config.vpn.service.topology"/></td>
									<td>
										<s:if test="%{inVpnTopology}">
											<a href="javascript:void(0);" class="npcLinkA" onclick="openVpnTopologyPanel(<s:property value="vpnServiceOfVpnGateway"/>)">VPN Topology</a>
										</s:if>
										<s:else>&nbsp;</s:else>
									</td>
								</tr>	
							</table>
						</td>						
					</tr>					
				</table>
				</s:elseif>
				<s:else>
					<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
					<tr><th colspan="4" align="left"><s:text name="monitor.hiveAp.system.title"/></th></tr>
					<tr><td height="5px"></td></tr>
					<tr>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system"/></b>
									</td>
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="hiveAp.hostName"/></td>
									<td width="260px"><a href='<s:url action="hiveAp" includeParams="none"><s:param name="operation" value="%{'edit2'}"/><s:param name="viewType" value="%{'config'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{domainId}"/></s:url>'><s:property value="%{name}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.ipAddress"/></td>
									<td><s:property value="%{ipAddress}"/></td>								
								</tr>
								<tr>
									<td width="180px" class="panelLabel"><s:text name="monitor.hiveAp.model"/></td>
									<td><s:property value="%{model}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.type"/></td>
									<td><s:property value="%{deviceType}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.macAddress"/></td>
									<td><s:property value="%{macAddress}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.sw.version"/></td>
									<td><s:property value="%{sw}"/></td>
								</tr>
							</table>
						</td>
						<td colspan="2" style="vertical-align:top">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="panelLabel" align="left">
										<b><s:text name="monitor.hiveAp.system.title.system.overview"/></b>
									</td>
								</tr>
								<tr>
									<td class="panelLabel" width="180px"><s:text name="monitor.hiveAp.severity.status"/></td>
									<td width="260px"><a href='<s:url action="alarms" includeParams="none"><s:param name="operation" value="%{'search'}"/><s:param name="apId" value="%{macAddress}"/><s:param name="filterVHM" value="%{domainName}"/></s:url>'>
										<s:property escape="false" value="%{status}"/></a></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.manageStatus"/></td>
									<td><s:property escape="false" value="%{manageStatus}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.origin"/></td>
									<td><s:property escape="false" value="%{origin}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.connectionTime"/></td>
									<td><s:property value="%{upTime}"/></td>
								</tr>
								<tr>
									<td class="panelLabel"><s:text name="monitor.hiveAp.topology"/></td>
									<td>
										<s:if test="%{inTopology}">
											<a href='<s:url action="mapNodes" includeParams="none"><s:param name="operation" value="%{'redirect'}"/><s:param name="id" value="%{mapId}"/></s:url>'>
											<s:property value="%{topologyMap}"/></a>
										</s:if>
										<s:else><s:property value="%{topologyMap}"/></s:else>
									</td>
								</tr>
							</table>
						</td>						
					</tr>
					</table>
				</s:else>
				</td>
				</tr>
				<s:if test="%{pageLayouts.blnNetworkDetails}">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
							<tr><th colspan="5" align="left"><s:text name="monitor.hiveAp.network.title"/></th></tr>
							<tr>
								<td class="list" align="left" width="100px"><b>
									<s:if test="%{blnSwitchAsBr}"><s:text name="config.switchSettings.igmp.vlan.id" 
									/></s:if><s:else><s:text name="monitor.hiveAp.network.interface" 
									/></s:else>
									</b></td>
								<td class="list" align="left" width="200px"><b><s:text name="monitor.hiveAp.network.network" /></b></td>
								<td class="list" align="left" width="180px"><b><s:text name="monitor.hiveAp.network.type" /></b></td>
								<td class="list" align="left" width="180px"><b><s:text name="monitor.hiveAp.network.subnet" /></b></td>
								<td class="list" align="left"><b><s:text name="monitor.hiveAp.network.webSecurity" /></b></td>
							</tr>
							<tr>
								<td colspan="5">
									<div id="networkDetails" style="overflow-x:hidden;overflow-y:auto;width:100%;height:250px;border:0px solid #808080">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<s:iterator value="%{networkList}" status="status" id="nwMap">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="list" width="100px"><s:property value="#nwMap.key" />&nbsp;</td>
												<td class="list" colspan="4">
													<table>
														<s:iterator value="#nwMap.value" status="sv" id="nwMapValue">
														<tr>
															<td width="200px"><s:property value="networkName" />&nbsp;</td>
															<td width="180px"><s:property value="networkType" />&nbsp;</td>
															<td width="200px"><s:property value="subnet" />&nbsp;</td>
															<td><s:property value="webSecurity" />&nbsp;</td>
														</tr>
														</s:iterator>
													</table>
												</td>
											</tr>
											</s:iterator>
											<s:if test="%{gridCount_d > 0}">
											<s:generator separator="," val="%{' '}" count="%{gridCount_d}">
												<s:iterator>
													<tiles:insertDefinition name="rowClass" />
													<tr class="<s:property value="%{#rowClass}"/>">
														<td class="list" colspan="5">&nbsp;</td>
													</tr>
												</s:iterator>
											</s:generator>
										</s:if>
										</table>
									</div>
								</td>
							</tr>
							</table>
						</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnPortDetails}">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
								<s:if test="%{blnSwitch || blnSwitchAsBr}">
									<tr><th colspan="8" align="left"><s:text name="monitor.hiveAp.port.title"/></th></tr>
									<tr>
										<td class="list" align="left" width="100px"><b><s:text name="monitor.hiveAp.port.label" /></b></td>
										<td class="list" align="left" width="100px"><b><s:text name="monitor.hiveAp.port.tx.packets" /></b></td>
										<td class="list" align="left" width="100px"><b><s:text name="monitor.hiveAp.port.tx.bytes" /></b></td>
										<td class="list" align="left" width="100px"><b><s:text name="monitor.hiveAp.port.rx.packets" /></b></td>
										<td class="list" align="left" width="100px"><b><s:text name="monitor.hiveAp.port.rx.bytes" /></b></td>
										<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.port.unicast.packets" /></b></td>
										<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.port.muticast.packets" /></b></td>
										<td class="list" align="left" ><b><s:text name="monitor.hiveAp.port.broadcast.packets" /></b></td>
									</tr>
									<tr>
										<td colspan="8">
											<div id="portDetails" style="overflow-x:hidden;overflow-y:auto;width:100%;height:250px;border:0px solid #808080">
											<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<s:iterator value="%{swPortStatsList}" status="status">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="list" align="left" width="100px"><s:property value="portName"/>&nbsp;</td>
												<td class="list" align="left" width="100px"><s:property value="txPacketCountString"/>&nbsp;</td>
												<td class="list" align="left" width="100px"><s:property value="txBytesCountString"/>&nbsp;</td>
												<td class="list" align="left" width="100px"><s:property value="rxPacketCountString"/>&nbsp;</td>
												<td class="list" align="left" width="100px"><s:property value="rxBytesCountString"/>&nbsp;</td>
												<td class="list" align="left" width="120px"><s:property value="unicastPacketsString"/>&nbsp;</td>
												<td class="list" align="left" width="120px"><s:property value="muticastPacketsString"/>&nbsp;</td>
												<td class="list" align="left"><s:property value="broadcastPacketsString"/>&nbsp;</td>
											</tr>
											</s:iterator>
											<s:if test="%{gridCount_p > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount_p}">
													<s:iterator>
														<tiles:insertDefinition name="rowClass" />
														<tr class="<s:property value="%{#rowClass}"/>">
															<td class="list" colspan="8">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>
											</table>
											</div>
										</td>
									</tr>
								</s:if>
								<s:else>
									<tr><th colspan="6" align="left"><s:text name="monitor.hiveAp.port.title"/></th></tr>
									<tr>
									    <td class="list" align="left" width="20px"><b><s:text name="" /></b></td>
										<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.port.label" /></b></td>
										<!--<td class="list" align="left" width="150px"><b><s:text name="monitor.hiveAp.port.mode" /></b></td>
										--><td class="list" align="left" width="150px"><b><s:text name="monitor.hiveAp.port.admin.status" /></b></td>
										<td class="list" align="left" width="150px"><b><s:text name="monitor.hiveAp.port.link.status" /></b></td>
										<td class="list" align="left" width="250px"><b><s:text name="monitor.hiveAp.port.network" /></b></td>
										<td class="list" align="left"><b><s:text name="monitor.hiveAp.port.lan.profile" /></b></td>
									</tr>
									<s:iterator value="%{deviceInterfaceAdapters}" status="status">
									<tr>
									    <s:if test='%{deviceInterface.wanType}'>
									    <s:if test='%{deviceInterface.usedInPBR}'>
									    <td class="list" align="left"><img class="dinl" src="<s:url value="/images/wanStatus_Green.png" />" width="16" height="16" alt="Active WAN" title="Active WAN" />&nbsp;</td>
									    </s:if>
									    <s:else >
									    <td class="list" align="left"><img class="dinl" src="<s:url value="/images/wanStatus_Gray.png" />" width="16" height="16" alt="InActive WAN" title="InActive WAN" />&nbsp;</td>
									    </s:else>
									    </s:if>
									    <s:else>
									    <td class="list" align="left">&nbsp;</td>
									    </s:else>
										<td class="list" align="left"><s:property value="interfaceDisplayName"/>&nbsp;</td>
										<!--<td class="list" align="left"><s:property value="deviceInterface.accessModeString"/>&nbsp;</td>
										--><td class="list" align="left"><s:property value="deviceInterface.adminStateString"/>&nbsp;</td>
										<td class="list" align="left"><s:property value="deviceInterface.linkStatusString"/>&nbsp;</td>
										<s:if test="%{deviceInterface.lanProfile != null}">
											<td class="list" align="left">
												<s:iterator value="deviceInterface.lanProfile.vpnNetworks" status="status">
													<s:property value="networkVlanString" />&nbsp;<br/>
												</s:iterator>
											</td>
											<td class="list" align="left"><s:property value="deviceInterface.lanProfile.name"/>&nbsp;</td>
										</s:if>
										<s:else>
										<td class="list" align="left" colspan="2">&nbsp;</td>
										</s:else>
									</tr>
									</s:iterator>
								</s:else>
							</table>
						</td>
					</tr>
				</s:if>
				<s:if test="%{pseBundle.blnPSESupport}">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
							<tr>
								<s:if test="blnSwitch || blnSwitchAsBr">
								<th colspan="6" align="left"><s:text name="monitor.hiveAp.pse.title"/></th>
								</s:if>
								<s:else>
								<th colspan="5" align="left"><s:text name="monitor.hiveAp.pse.title"/></th>
								</s:else>
								<th><input style="float: right; display: <s:property value='supportPoeBounce'/>" type="button" name="ignore" value="Cycle Power" class="buttonlong"
										onClick="recyclePower();"
										<s:property value="writeDisabled" />></th>
							</tr>
							<tr>
								<td class="list" align="left" width="20px">
									<s:checkbox name="arrayPseBounce_all" onclick="toggleClickAllPoeBounce(this.checked)"
													cssStyle="display: %{supportPoeBounce}"
													id="arrayPseBounce_all"/></td>
								<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.pse.detail.port" /></b></td>
								<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.pse.detail.status" /></b></td>
								<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.pse.detail.power.label" /></b></td>
								<td class="list" align="left" width="150px"><b><s:text name="monitor.hiveAp.pse.detail.pdType" /></b></td>
								<td class="list" align="left" width="180px"><b><s:text name="monitor.hiveAp.pse.detail.pdClass" /></b></td>
								<s:if test="blnSwitch || blnSwitchAsBr">
								<td class="list" align="left" width="180px"><b><s:text name="monitor.hiveAp.pse.detail.power.cutoff.priority" /></b></td>
								</s:if>
							</tr>
							<s:if test="blnSwitch || blnSwitchAsBr">
								<tr>
									<td colspan="7">
										<div id="pseDetails" style="overflow-x:hidden;overflow-y:auto;width:100%;height:250px;border:0px solid #808080">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<s:iterator value="%{pseBundle.pseStatusLst}" status="status">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="list" align="left" width="20px" ><s:checkbox name="arrayPseBounce" 
													onclick="toggleClickPoeBounce(this)"
													cssStyle="display: %{supportPoeBounce}"
													id="arrayPseBounce_%{#status.index}"
													fieldValue="%{interfName}" /></td>
												<td class="list" align="left" width="120px"><s:property value="interfName"/>&nbsp;</td>
												<td class="list" align="left" width="120px"><s:property value="statusString"/>&nbsp;</td>
												<td class="list" align="left" width="120px"><s:property value="powerString"/>&nbsp;</td>
												<td class="list" align="left" width="150px"><s:property value="pdTypeString"/>&nbsp;</td>
												<td class="list" align="left" width="180px"><s:property value="pdClassString"/>&nbsp;</td>
												<td class="list" align="left" width="150px" ><s:property value="powerCutoffPriorityString"/>&nbsp;</td>
											</tr>
											</s:iterator>
										</table>
										</div>
									</td>
								</tr>
								<tr><td class="list" colspan="7" align="left" height="20px">
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.total"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.totalPower"/></span>
									&nbsp;&nbsp;
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.used"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.powerUsed"/></span>
									&nbsp;&nbsp;
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.remain"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.remainingPower"/></span>
								</td></tr>
							</s:if>
							<s:else>
								<s:iterator value="%{pseBundle.pseStatusLst}" status="status">
								<tr>
									<td class="list" align="left" width="20px" ><s:checkbox name="arrayPseBounce" 
										onclick="toggleClickPoeBounce(this)"
										cssStyle="display: %{supportPoeBounce}"
										id="arrayPseBounce_%{#status.index}"
										fieldValue="%{interfName}" /></td>
									<td class="list" align="left"><s:property value="interfName"/>&nbsp;</td>
									<td class="list" align="left"><s:property value="statusString"/>&nbsp;</td>
									<td class="list" align="left"><s:property value="powerString"/>&nbsp;</td>
									<td class="list" align="left"><s:property value="pdTypeString"/>&nbsp;</td>
									<td class="list" align="left"><s:property value="pdClassString"/>&nbsp;</td>
								</tr>
								</s:iterator>
								<tr><td class="list" colspan="6" align="left" height="20px">
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.total"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.totalPower"/></span>
									&nbsp;&nbsp;
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.used"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.powerUsed"/></span>
									&nbsp;&nbsp;
									<span class="summaryLabel"><s:text name="monitor.hiveAp.pse.detail.power.remain"/>:</span>
									<span class="summaryValue"><s:property value="pseBundle.remainingPower"/></span>
								</td></tr>
							</s:else>
							</table>
						</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnVpnTunnelDetails}">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
							<tr><th colspan="3" align="left"><s:text name="monitor.hiveAp.vpn.tunnel.title"/></th></tr>
							<tr>
								<td class="list" align="left" width="200px"><b><s:text name="monitor.hiveAp.vpn.tunnel.branch" /></b></td>
								<td class="list" align="left" width="200px"><b><s:text name="monitor.hiveAp.vpn.tunnel.status" /></b></td>
								<td class="list" align="left"><b><s:text name="monitor.hiveAp.vpn.tunnel.uptime" /></b></td>
							</tr>
							<s:iterator value="%{vpnBranchTunnelList}" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<td class="list" width="200px">
								<s:if test="%{clientId != null && clientId > 0}">
									<a href='<s:url action="hiveAp"><s:param name="operation" value="%{'showHiveApDetails'}"/><s:param name="id" value="%{clientId}"/></s:url>'><s:property
												value="clientName" /> </a>
								</s:if>
								</td>
								<td class="list" width="200px"><s:property value="clientStatus" />&nbsp;</td>
								<td class="list"><s:property value="upTime" />&nbsp;</td>
							</tr>
							</s:iterator>
							<s:if test="%{gridCount_d > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount_d}">
								<s:iterator>
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" colspan="5">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
							</table>
						</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnRadioDetails}">
					<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
						<tr><th colspan="8" align="left"><s:text name="monitor.hiveAp.radio.title"/></th></tr>
						<tr>
							<td class="list" align="left" width="60px"><b><s:text name="monitor.hiveAp.radio" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.ifType" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.ifMode" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.channel" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.eirp" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.noise" /></b></td>
							<td class="list" align="left" width="120px"><b><s:text name="monitor.hiveAp.radio.ssid" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.radio.datarate" /></b></td>
						</tr>
						<tr>
							<td class="list"><s:property value="wifi0_name" />&nbsp;</td>
							<td class="list" title="<s:property value='wifi0_type_tip'/>"><s:property value="wifi0_type" />&nbsp;</td>
							<td class="list"><s:property value="wifi0_mode" />&nbsp;</td>
							<td class="list"><s:property value="wifi0_channel" />&nbsp;</td>
							<td class="list"><s:property value="wifi0_eirp" />&nbsp;</td>
							<td class="list"><s:property value="wifi0_noise" />&nbsp;</td>
							<td class="list">
								<s:iterator value="%{wifi0_ssids}" status="status" id="item">
									<a onclick="locateXY(event);" href="javaScript: showSsidDetails('<s:property value="%{#item}"/>', <s:property value="%{domainId}"/>);"><s:property
									value="%{#item}" /></a>
									<s:if test="#status.last==false">&#44;</s:if>&nbsp;
								</s:iterator>&nbsp;
							</td>
						    <td class="list">
								<s:property value="%{wifi0RateStr}" />
							</td>
						</tr>
						<tr>
							<td class="list"><s:property value="wifi1_name" />&nbsp;</td>
							<td class="list" title="<s:property value='wifi1_type_tip'/>"><s:property value="wifi1_type" />&nbsp;</td>
							<td class="list"><s:property value="wifi1_mode" />&nbsp;</td>
							<td class="list"><s:property value="wifi1_channel" />&nbsp;</td>
							<td class="list"><s:property value="wifi1_eirp" />&nbsp;</td>
							<td class="list"><s:property value="wifi1_noise" />&nbsp;</td>
							<td class="list">
								<s:iterator value="%{wifi1_ssids}" status="status" id="item">
									<a onclick="locateXY(event);" href="javaScript: showSsidDetails('<s:property value="%{#item}"/>', <s:property value="%{domainId}"/>);"><s:property
									value="%{#item}" /></a>
									<s:if test="#status.last==false">&#44;</s:if>&nbsp;
								</s:iterator>&nbsp;
							</td>
							<td class="list">
								<s:property value="%{wifi1RateStr}" />
							</td>
						</tr>
						<s:if test="%{gridCount_r > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount_r}">
								<s:iterator>
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" colspan="7">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
						</table>
					</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnNeighborDetails}">
					<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
						<tr><th colspan="9" align="left"><s:text name="monitor.hiveAp.neighbor.title"/></th></tr>
						<tr>
							<td class="list" align="left" width="150px"><b><s:text name="monitor.hiveAp.switch.chassis.id" /></b></td>
							<td class="list" align="left" width="130px"><b><s:text name="topology.neighbor.title.incoming.port" /></b></td>
							<td class="list" align="left" width="130px"><b><s:text name="topology.neighbor.title.peerport"/></b></td>
							<td class="list" align="left" width="150px"><b><s:text name="topology.neighbor.title.neighborHostname" /></b></td>
							<td class="list" align="left" width="180px"><b><s:text name="topology.neighbor.title.connectionTime" /></b></td>
							<td class="list" align="left" width="130px"><b><s:text name="topology.neighbor.title.linkCost" /></b></td>
							<s:if test="!blnSwitch && !blnSwitchAsBr">
								<td class="list" align="left" width="130px"><b><s:text name="topology.neighbor.title.rssi" /></b></td>
							</s:if>
							<td class="list" align="left" width="130px"><b><s:text name="topology.neighbor.title.linkType" /></b></td>
							<s:if test="!blnSwitch && !blnSwitchAsBr">								
								<td class="list" align="left"><b><s:text name="topology.neighbor.title.protocol"/></b></td>
							</s:if>
							<s:if test="blnSwitch || blnSwitchAsBr">
								<td class="list" align="left" width="130px">&nbsp;</td>
								<td class="list" align="left">&nbsp;</td>
							</s:if>
						</tr>
						<s:if test="blnSwitch || blnSwitchAsBr">
							<tr>
								<td colspan="9">
									<div id="neighborDetails" style="overflow-x:hidden;overflow-y:auto;width:100%;height:250px;border:0px solid #808080">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" style="table-layout:fixed;word-wrap:break-word;word-break:break-all">
											<s:iterator value="%{lldpInformationList}" status="status">
												<tiles:insertDefinition name="rowClass" />
												<tr class="<s:property value="%{#rowClass}"/>">
													<td class="list" width="150px"><s:property value="deviceMac" />&nbsp;</td>
													<td class="list" width="130px"><s:property value="ifName" />&nbsp;</td>
													<td class="list" width="130px"><s:property value="portID" />&nbsp;</td>
													<td class="list" width="150px"><s:property value="systemName" />&nbsp;</td>
													<td class="list" width="180px">&nbsp;</td>
													<td class="list" width="130px">&nbsp;</td>
													<td class="list" width="130px">&nbsp;</td>
													<td class="list" width="130px">&nbsp;</td>
													<td class="list">&nbsp;</td>
												</tr>
											</s:iterator>
											<s:if test="%{gridCount_lldp > 0}">
												<s:generator separator="," val="%{' '}" count="%{gridCount_lldp}">
													<s:iterator>
														<tiles:insertDefinition name="rowClass" />
														<tr class="<s:property value="%{#rowClass}"/>">
															<td class="list" colspan="9">&nbsp;</td>
														</tr>
													</s:iterator>
												</s:generator>
											</s:if>
										</table>
									</div>
								</td>
							</tr>
						</s:if>
						<s:else>
							<s:iterator value="%{neighborList}" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list"><a
										href="javaScript: showNeighborDetails('<s:property value="%{neighborAPID}"/>', <s:property value="%{domainId}"/>);"><s:property
										value="neighborAPID" /></a></td>
									<td class="list"><s:property value="ifName" />&nbsp;</td>
									<td class="list"><s:property value="portID" />&nbsp;</td>
									<td class="list"><s:property value="hostName" />&nbsp;</td>
									<td class="list"><s:property value="linkUpTimeString" />&nbsp;</td>
									<td class="list"><s:property value="linkCost" />&nbsp;</td>
									<td class="list"><s:property value="rssiDbm" />&nbsp;</td>
									<td class="list"><s:property value="linkTypeString" />&nbsp;</td>									
									<td class="list"><s:text name="topology.neighbor.title.amrp"/> &nbsp;</td>
								</tr>
							</s:iterator>
							<s:if test="%{lldpInformationList != null && lldpInformationList.size() > 0 }">
								<s:iterator value="%{lldpInformationList}" status="status">
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" width="150px"><s:property value="deviceMac" />&nbsp;</td>
										<td class="list" width="130px"><s:property value="ifName" />&nbsp;</td>
										<td class="list"><s:property value="portID" />&nbsp;</td>
										<td class="list" width="150px"><s:property value="systemName" />&nbsp;</td>
										<td class="list" width="180px">&nbsp;</td>
										<td class="list" width="130px">&nbsp;</td>
										<td class="list" width="130px">&nbsp;</td>
										<td class="list" width="130px">&nbsp;</td>										
										<td class="list">
										  <s:if test="%{protocol == 1}">
										  	<s:text name="topology.neighbor.title.lldp"/> &nbsp;
										  </s:if>
										  <s:elseif test="%{protocol == 2}">
										  	<s:text name="topology.neighbor.title.cdp"/> &nbsp;
										  </s:elseif>
										  <s:else>
										  	<s:text name="topology.neighbor.title.unknown"/> &nbsp;
										  </s:else>
										</td>
									</tr>
								</s:iterator>								
							</s:if>
							<s:if test="%{gridCount_n > 0}">
								<s:generator separator="," val="%{' '}" count="%{gridCount_n}">
									<s:iterator>
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="list" colspan="9">&nbsp;</td>
										</tr>
									</s:iterator>
								</s:generator>
							</s:if>							
						</s:else>
						</table>
					</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnClientDetails}">
					<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
						<s:if test="blnSwitch || blnSwitchAsBr">
							<tr><th colspan="10" align="left"><s:text name="monitor.hiveAp.wired.client.details"/></th></tr>
							<tr>
								<td class="list" align="left" width="100px"><b><s:text name="topology.client.title.macAddress" /></b></td>
								<td class="list" align="left" width="110px"><b><s:text name="topology.client.title.hostname" /></b></td>
								<td class="list" align="left" width="100px"><b><s:text name="topology.client.title.ipAddress" /></b></td>
								<td class="list" align="left" width="100px"><b><s:text name="topology.client.title.userName" /></b></td>
								<td class="list" align="left" width="100px"><b><s:text name="topology.client.title.osType" /></b></td>
								<td class="list" align="left" width="90px" ><b><s:text name="topology.client.title.port" /></b></td>
								<td class="list" align="left" width="75px" style="word-wrap:break-word;"><b><s:text name="topology.client.title.connectivityStatus" /></b></td>
								<td class="list" align="left" width="85px" style="word-wrap:break-word;"><b><s:text name="topology.client.title.authenticationMethod" /></b></td>
								<td class="list" align="left" width="50px"><b><s:text name="topology.client.title.vlan" /></b></td>
								<td class="list" align="left" width="105px"><b><s:text name="topology.client.title.userProfile" /></b></td>
							</tr>
							<tr>
								<td colspan="10">
									<div id="clientDetails" style="overflow-x:hidden;overflow-y:auto;width:100%;height:250px;border:0px solid #808080">
									<table cellpadding="0" cellspacing="0" border="0" width="100%" style="table-layout:fixed;word-wrap:break-word;word-break:break-all">
									<s:iterator value="%{clientList}" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="list" width="100px">
												<s:if test="%{clientChannel > 0}">
													<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{id}"/><s:param name="listType" value="%{'wireless'}"/><s:param name="pageFrom" value="%{'deviceMonitor'}"/></s:url>'><s:property
														value="clientMac" /> </a>
												</s:if>
												<s:else>
													<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{id}"/><s:param name="listType" value="%{'wired'}"/><s:param name="pageFrom" value="%{'deviceMonitor'}"/></s:url>'><s:property
														value="clientMac" /> </a>
												</s:else>
											</td>
											<td class="list" width="110px"><s:property value="clientHostname" />&nbsp;</td>
											<td class="list" width="100px"><s:property value="clientIP" />&nbsp;</td>
											<td class="list" width="100px"><s:property value="clientUsername" />&nbsp;</td>
											<td class="list" width="100px"><s:property value="clientOsInfo" />&nbsp;</td>
											<td class="list" width="90px"><s:property value="ifName" />&nbsp;</td>
											<td class="list" width="75px"><s:property value="connectStateString" />&nbsp;</td>
											<td class="list" width="85px"><s:property value="clientAuthMethodString" />&nbsp;</td>
											<td class="list" width="50px"><s:property value="clientVLAN" />&nbsp;</td>
											<td class="list" width="100px"><s:property value="userProfileName" />&nbsp;</td>
										</tr>
									</s:iterator>
									<s:if test="%{gridCount_c > 0}">
										<s:generator separator="," val="%{' '}" count="%{gridCount_c}">
											<s:iterator>
												<tiles:insertDefinition name="rowClass" />
												<tr class="<s:property value="%{#rowClass}"/>">
													<td class="list" colspan="10">&nbsp;</td>
												</tr>
											</s:iterator>
										</s:generator>
									</s:if>
									</table>
									</div>
								</td>
							</tr>
						</s:if>
						<s:else>
							<tr><th colspan="10" align="left"><s:text name="monitor.hiveAp.client.title"/></th></tr>
							<tr>
								<td class="list" align="left"><b><s:text name="topology.client.title.macAddress" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.hostname" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.ipAddress" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.associationTime" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.duration" /></b></td>
								<s:if test="%{blnBranchRouter}">
									<td class="list" align="left"><b><s:text name="topology.client.title.ssid.lan" /></b></td>
								</s:if>
								<s:else>
									<td class="list" align="left"><b><s:text name="topology.client.title.ssid" /></b></td>
								</s:else>
								<td class="list" align="left"><b><s:text name="topology.client.title.authenticationMethod" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.encryptionMethod" /></b></td>
								<td class="list" align="left"><b><s:text name="topology.client.title.radioMode" /></b></td>
								<s:if test="%{blnBranchRouter}">
									<td class="list" align="left"><b><s:text name="topology.client.title.userProfile" /></b></td>
								</s:if>
							</tr>
							<tr>
							<s:iterator value="%{clientList}" status="status">
								<tiles:insertDefinition name="rowClass" />
								<tr class="<s:property value="%{#rowClass}"/>">
									<td class="list">
										<s:if test="%{clientChannel > 0}">
											<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{id}"/><s:param name="listType" value="%{'wireless'}"/><s:param name="pageFrom" value="%{'deviceMonitor'}"/></s:url>'><s:property
												value="clientMac" /> </a>
										</s:if>
										<s:else>
											<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{id}"/><s:param name="listType" value="%{'wired'}"/><s:param name="pageFrom" value="%{'deviceMonitor'}"/></s:url>'><s:property
												value="clientMac" /> </a>
										</s:else>
									</td>
									<td class="list"><s:property value="clientHostname" />&nbsp;</td>
									<td class="list"><s:property value="clientIP" />&nbsp;</td>
									<td class="list"><s:property value="startTimeString" />&nbsp;</td>
									<td class="list"><s:property value="durationString" />&nbsp;</td>
									<td class="list"><s:property value="clientSSID" />&nbsp;</td>
									<td class="list"><s:property value="clientAuthMethodString" />&nbsp;</td>
									<td class="list"><s:property value="clientEncryptionMethodString" />&nbsp;</td>
									<td class="list"><s:property value="clientMacPtlString" />&nbsp;</td>
									<s:if test="%{blnBranchRouter}">
										<td class="list"><s:property value="userProfileName" />&nbsp;</td>
									</s:if>
								</tr>
							</s:iterator>
							<s:if test="%{gridCount_c > 0}">
								<s:generator separator="," val="%{' '}" count="%{gridCount_c}">
									<s:iterator>
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="list" colspan="10">&nbsp;</td>
										</tr>
									</s:iterator>
								</s:generator>
							</s:if>
							</tr>
						</s:else>
						</table>
					</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnIdpReportDetails}">
					<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view">
						<tr><th colspan="8" align="left"><s:text name="monitor.hiveAp.idp.report.title"/></th></tr>
						<tr>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.ifBSSID" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.ifSSID" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.rssi" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.network" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.onMap" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.support" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.noncompliant" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.hiveAp.report.time" /></b></td>
						</tr>
						<s:iterator value="%{idpList}" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<td class="list"><s:property value="ifMacAddress" />&nbsp;</td>
								<td class="list"><s:property value="ssid" />&nbsp;</td>
								<td class="list"><s:property value="rssiDbm" />&nbsp;</td>
								<td class="list" align="center"><s:property value="networkString" />&nbsp;</td>
								<s:if test="%{rssiCount > 0}">
									<td class="list" nowrap="nowrap"><a href="#location" onclick="showClientLocation(<s:property value="id" />);">
										<s:property value="mapName" /></a>
										&nbsp;
									</td>
								</s:if>
								<s:else>
									<td class="list" nowrap="nowrap">
										<s:property value="mapName" />
										&nbsp;
									</td>
								</s:else>
								<td class="list"><s:property value="supportString" />&nbsp;</td>
								<td class="list"><s:property value="complianceString" />&nbsp;</td>
								<td class="list"><s:property value="reportTimeString" />&nbsp;</td>
							</tr>
						</s:iterator>
						<s:if test="%{gridCount_i > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount_i}">
								<s:iterator>
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" colspan="8">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
						</table>
					</td>
					</tr>
				</s:if>
				<s:if test="%{pageLayouts.blnEventDetails}">
					<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%" class="view" style="table-layout:fixed;word-wrap:break-word;word-break:break-all">
						<tr><th colspan="5" align="left"><s:text name="monitor.hiveAp.event.title"/></th></tr>
						<tr>
							<td class="list" align="left"><b><s:text name="monitor.alarms.apId" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.alarms.apName" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.alarms.alarmTime" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.alarms.alarmDesc" /></b></td>
							<td class="list" align="left"><b><s:text name="monitor.alarms.objectName" /></b></td>
						</tr>
						<s:iterator value="%{eventList}" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<td class="list"><a title="More..."
								href='<s:url action="events" includeParams="none"><s:param name="operation" value="%{'search'}"/>
								<s:param name="apId" value="%{apId}"/><s:param name="filterVHM" value="%{domainName}"/></s:url>'><s:property
								value="%{apId}" /></a>&nbsp;</td>
								<td class="list"><s:property value="apName" />&nbsp;</td>
								<td class="list"><s:property value="trapTimeString" />&nbsp;</td>
								<td class="list"><s:property value="trapDesc" />&nbsp;</td>
								<td class="list"><s:property value="objectName" />&nbsp;</td>
							</tr>
						</s:iterator>
						<s:if test="%{gridCount_e > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount_e}">
								<s:iterator>
									<tiles:insertDefinition name="rowClass" />
									<tr class="<s:property value="%{#rowClass}"/>">
										<td class="list" colspan="5">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
						</table>
					</td>
					</tr>
				</s:if>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>

<!-- ====================== VPN topology begin ====================== -->
<div>
<div id="vpnTitle" class="leftNavH1"></div>
<table id="vpnTable" border="0" cellspacing="0" cellpadding="0" width="100%">
</table>
</div>
<div id="vpnTopologyPanel" style="display: none;">
	<div class="bd">
		<iframe id="vpnTopology" name="vpnTopology" width="0" height="0"
			frameborder="0" style="background-color: #fff;" src="">
		</iframe>
	</div>
</div>
<div id="portInfoPanel" style="display: none;">
	<div class="hd" id="portInfoTitle">Eth1 Overview </div>
	<div class="bd">
		<div id="bd_top" class="bd_top">
		</div>
		<div id="bd_content" class="bd_content"></div>
	</div>
	<!-- <div class="ft"></div>  -->
</div>
<script type="text/javascript">
var vpnTopologyPanel = null;

function createVpnTopologyPanel(width, height){
	var div = document.getElementById("vpnTopologyPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vpnTopology");
	iframe.width = width;
	iframe.height = height;
	vpnTopologyPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px",
							fixedcenter:true,
							visible:false,
							constraintoviewport:true } );
	vpnTopologyPanel.render(document.body);
	div.style.display="";
	vpnTopologyPanel.beforeHideEvent.subscribe(clearVpnTopologyData);
	var resize = createResizer("vpnTopologyPanel");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, vpnTopologyPanel, true);
	resize.on("endResize", function(args){
		vpnTopologyPanelResizeCallback();
	}, vpnTopologyPanel, true);
}

//Create Resize instance, binding it to the 'resizablepanel' DIV
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 650,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
function vpnTopologyPanelResizeCallback(){
	if(null != vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.location.reload();
	}
}
function clearVpnTopologyData(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "none";
	}
	if(vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.onHidePage();
	}
}
function openVpnTopologyPanel(id){
	if(null == vpnTopologyPanel){
		var width = YAHOO.util.Dom.getViewportWidth();
		var height = YAHOO.util.Dom.getViewportHeight();
		createVpnTopologyPanel(width*0.8, height*0.8);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "";
	}
	vpnTopologyPanel.show();
	var iframe = document.getElementById("vpnTopology");
	iframe.src ="<s:url value='vpnServices.action' includeParams='none' />?operation=initVpnTopologyPanel"
			+"&jsonMode=true&id="+id+"&pageId="+new Date().getTime();
}
function updateVpnTopologyPanelTitle(str){
	if(null != vpnTopologyPanel){
		var iframeTopologyDom = hm.util.getIFrameDOMById("vpnTopology");
		iframeTopologyDom.updateTopologyDialogTitle("<s:text name='config.vpn.service.topology'/>"+" - "+str);
	}
}
var vpnTopologyIframeWindow;
</script>
<!-- ====================== VPN topology end ====================== -->

<div id="poeBounceInfoPanel" style="display: none;">
	<div class="hd" id="poeBounceInfoTitle"><s:text name="glasgow_07.info.port.pse.bouce.title" /></div>
	<div class="bd">
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft" align="center" ><input type="button" style="margin-top: 5px;" class="button" onclick="hiddenPoeBouncePanel()" value="OK"/></div>
</div>
<script type="text/javascript">

var poeBounceInfoPanel = null;
function createPoeBounceInfoPanel() {
	var div = window.document.getElementById("poeBounceInfoPanel");
	poeBounceInfoPanel = new YAHOO.widget.Panel(div, { width:"800px", visible:false, fixedcenter:"contained", draggable:true, constraintoviewport:true } );
	poeBounceInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(poeBounceInfoPanel);
	poeBounceInfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function toggleClickAllPoeBounce(checked){
	$("input[name=arrayPseBounce]").each(function(){
		$(this).attr("checked",checked);
	});
}

function toggleClickPoeBounce(check){
	if(check.checked){
		return;
	}
	var checkAll = document.getElementById('arrayPseBounce_all');
	if (checkAll) {
		checkAll.checked = false;
	}
}

function recyclePower(){
	var arrayPseBounces = [];
	$("input[name=arrayPseBounce]:checked").each(function(){
		arrayPseBounces.push($(this).val());
	});
	
	if(arrayPseBounces.length < 1){
		warnDialog.cfg.setProperty('text', '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		warnDialog.show();
		return;
	}
	
	confirmDialog.cfg.setProperty('text', '<s:text name="glasgow_07.info.port.pse.bouce.confirm.warning"></s:text>');
	confirmDialog.show();
	
}

function doContinueOper(){
	var arrayPseBounces = [];
	$("input[name=arrayPseBounce]:checked").each(function(){
		arrayPseBounces.push($(this).val());
	});
	var reqArgs = {
			'operation': 'recyclePower',
			'arrayPseBounce': arrayPseBounces
	};
	$.post('hiveApMonitor.action',
			$.param(reqArgs, true),
			function(data, textStatus) {
				succRecyclePower(data);
			},
			'json');
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function succRecyclePower(details){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	var cliDiv = document.getElementById("cli_viewer");
	cliDiv.innerHTML = "<pre>" + details.v.replace(/\n/g,"<br>") + "</pre>";
	if(null == poeBounceInfoPanel){
		createPoeBounceInfoPanel();
    }
	poeBounceInfoPanel.cfg.setProperty('visible', true);
}

function hiddenPoeBouncePanel(){
	poeBounceInfoPanel.cfg.setProperty('visible', false);
	
	// refresh monitor page
	toggleClickAllPoeBounce(false);
	var id=document.getElementById("hiveAp_id").value;
	synchronizeApInfo(id);
}

</script>