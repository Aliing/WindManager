<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
	<script src="<s:url value="/js/innerhtml.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<%@page import="com.ah.be.common.NmsUtil"%>

<style type="text/css">
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
.viewclientlist{
	font-size: 14px;
	width: 50px important!;
}

.css_icon_cmenrolled{
	width:16px;
	height:16px;
	display:block;
	border:1px solid #FFFFFF; 
	background-repeat:no-repeat;
	background:url(../hm/images/icons/cm_blue.png) no-repeat center;
}
.css_icon_cmenrolled_over{
	width:16px;
	height:16px;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/cm_blue.png) no-repeat center;
	border:1px solid #0093de; 
	display:block;
}

.css_icon_idmenrolled{
	width:16px;
	height:16px;
	display:block;
	border:1px solid #FFFFFF; 
	background-repeat:no-repeat;
	background:url(../hm/images/icons/idm_blue.png) no-repeat center;
}
.css_icon_idmenrolled_over{
	width:16px;
	height:16px;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/idm_blue.png) no-repeat center;
	border:1px solid #0093de; 
	display:block;
}

.css_icon_slenrolled{
	width:16px;
	height:16px;
	display:block;
	border:1px solid #FFFFFF; 
	background-repeat:no-repeat;
	background:url(../hm/images/icons/sl_blue.png) no-repeat center;
}
.css_icon_slenrolled_over{
	width:16px;
	height:16px;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/sl_blue.png) no-repeat center;
	border:1px solid #0093de; 
	display:block;
}

</style>

<script>
var formName = 'clientMonitor';
var sysName = '<s:property value="%{systemNmsName}"/>';

var deauthOverlay = null;
var multiEditPanel = null;
var refreshSetPanel = null;
var cliInfoPanel = null;


function openDeauthOverlay()
{
	if (!checkClientExists())
	{
		return;
	}

	initialValues();
	if(null != deauthOverlay){
		deauthOverlay.cfg.setProperty('visible', true);
	}
}

function hideDeauthOverlay(){
	if(null != deauthOverlay){
		deauthOverlay.cfg.setProperty('visible', false);
	}
}

function openRefreshSetPanel()
{
	if(null != refreshSetPanel){
		refreshSetPanel.cfg.setProperty('visible', true);
	}
}

function hideRefreshSetPanel(){
	if(null != refreshSetPanel){
		refreshSetPanel.cfg.setProperty('visible', false);
	}
}

function openModifyDialog()
{
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0)
	{
		warnDialog.cfg.setProperty('text', "There is no clients to modify.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements))
	{
		warnDialog.cfg.setProperty('text', "Please select at least one client.");
		warnDialog.show();
	}
	else
	{
		url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=initEditValues&selectedClientIDStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: showMultiEditOverlay, failure:showMultiEditOverlay,timeout: 5000}, null);
	}
}

function showMultiEditOverlay(o)
{
	eval("var result = " + o.responseText);
	if(result.eUserName)
	{
		document.getElementById("editUserName").value = result.eUserName;
	}
	else
	{
		document.getElementById("editUserName").value = "";
	}

	if(result.eHostName)
	{
		document.getElementById("editHostName").value = result.eHostName;
	}
	else
	{
		document.getElementById("editHostName").value = "";
	}

	if(result.eIP)
	{
		document.getElementById("editIP").value = result.eIP;
	}
	else
	{
		document.getElementById("editIP").value = "";
	}

	if(result.eComment1)
	{
		document.getElementById("editComment1").value = result.eComment1;
	}
	else
	{
		document.getElementById("editComment1").value = "";
	}

	if(result.eComment2)
	{
		document.getElementById("editComment2").value = result.eComment2;
	}
	else
	{
		document.getElementById("editComment2").value = "";
	}

	if (hm.util.getSelectedIds().length > 1)
	{
		document.getElementById("flagEditIP").disabled = true;
	}
	else
	{
		document.getElementById("flagEditIP").disabled = false;
	}

	document.getElementById("flagEditUserName").checked =false;
	document.getElementById("flagEditHostName").checked =false;
	document.getElementById("flagEditIP").checked =false;
	document.getElementById("flagEditComment1").checked =false;
	document.getElementById("flagEditComment2").checked =false;
	document.getElementById("editUserName").disabled=true;
	document.getElementById("editHostName").disabled=true;
	document.getElementById("editIP").disabled=true;
	document.getElementById("editComment1").disabled=true;
	document.getElementById("editComment2").disabled=true;

	hm.util.hideFieldError();
	multiEditPanel.cfg.setProperty('visible', true);
}

function saveEditResults()
{
	if (!validateModifyClients())
	{
		return;
	}

	multiEditPanel.cfg.setProperty('visible', false);

	if (document.getElementById("flagEditUserName").checked || document.getElementById("flagEditHostName").checked
		|| document.getElementById("flagEditIP").checked || document.getElementById("flagEditComment1").checked
		||document.getElementById("flagEditComment2").checked)
	{
		submitFormAction('multiEditForm','saveEditResults');
	}
	else
	{
		hm.util.clearSelection();
	}
}

function validateModifyClients()
{
	if (document.getElementById("flagEditIP").checked)
	{
		var clientIP = document.getElementById("editIP");
		if (clientIP.value.length == 0)
		{
			hm.util.reportFieldError(clientIP, '<s:text name="error.requiredField"><s:param><s:text name="monitor.client.clientIP" /></s:param></s:text>');
	        clientIP.focus();
	        return false;
		}
		else if (!hm.util.validateIpAddress(clientIP.value))
		{
			hm.util.reportFieldError(clientIP, '<s:text name="error.formatInvalid"><s:param><s:text name="monitor.client.clientIP" /></s:param></s:text>');
			clientIP.focus();
			return false;
		}
	}

	var hostName = document.getElementById("editHostName");
	if (document.getElementById("flagEditHostName").checked && hostName.value.length == 0)
	{
		hm.util.reportFieldError(hostName, '<s:text name="error.requiredField"><s:param><s:text name="monitor.client.clientHostname" /></s:param></s:text>');
        hostName.focus();
        return false;
	}

	var userName = document.getElementById("editUserName");
	if (document.getElementById("flagEditUserName").checked && userName.value.length == 0)
	{
		hm.util.reportFieldError(userName, '<s:text name="error.requiredField"><s:param><s:text name="monitor.client.clientUserName" /></s:param></s:text>');
        userName.focus();
        return false;
	}

	var comment1 = document.getElementById("editComment1");
	if (document.getElementById("flagEditComment1").checked && comment1.value.length == 0)
	{
		hm.util.reportFieldError(comment1, '<s:text name="error.requiredField"><s:param><s:text name="monitor.client.comment1" /></s:param></s:text>');
        comment1.focus();
        return false;
	}

	var comment2 = document.getElementById("editComment2");
	if (document.getElementById("flagEditComment2").checked && comment2.value.length == 0)
	{
		hm.util.reportFieldError(comment2, '<s:text name="error.requiredField"><s:param><s:text name="monitor.client.comment2" /></s:param></s:text>');
        comment2.focus();
        return false;
	}

	return true;
}

var operationMenu;
function createOperationMenu()
{
	operationMenu = new YAHOO.widget.Menu('operation_menu', { fixedcenter: false });
	var operationItems = [
			 [
		        { text: '<s:text name="monitor.activeClient.operation.deauthClient"/>', onclick: { fn: openDeauthOverlay }},
		        { text: '<s:text name="monitor.activeClient.operation.refreshClient"/>', onclick: { fn: submitRefreshClientAction }}
		        <s:if test="%{!wiredClientList}">
		        ,{ text: '<s:text name="topology.menu.troubleshoot.clientTrace"/>', onclick: { fn: openClientTracePanel }}
		        </s:if>
			 ]
	];

	<s:if test="%{!easyMode}">
		// do not show below menu itmes if is wired client list.
		<s:if test="%{!wiredClientList}">
			operationItems.push(
						{ text: '<s:text name="monitor.activeClient.operation.location"/>', onclick: { fn: showLocation }}
						);

			<s:if test="%{!'wired'.equals(listType)}">
				operationItems.push(
						{ text: '<s:text name="monitor.activeClient.operation.show.tsinfo"/>', onclick: { fn: showTsinfo }}
						);
				operationItems.push(
						{ text: '<s:text name="monitor.activeClient.operation.enroll"/>', onclick: { fn: showEnrollStatus }}
						);
			</s:if>

			operationItems.push(
		        	{ text: '<s:text name="monitor.activeClient.operation.newWatchList"/>', onclick: { fn: submitNewClientWatchAction }}
					);

			if (!<%=NmsUtil.isHostedHMApplication()%>)
		    {
		    	operationItems.push(
		    			{text: '<s:text name="monitor.activeClient.operation.addtoWatchList"/>', disabled: <s:property value="disableAddToWatchList" />,
					        submenu: {
								id: "addtoWatchList", // Id for the submenu element to be created
								itemdata: [
									<s:iterator value="locationWatchList" status="status">
										{text: "<s:property value="name" />", onclick: { fn: locationWatchMenuClick, obj: "<s:property value="name" />" }},
									</s:iterator>
								]}
						});
		    }
		</s:if>
	</s:if>
	<s:else>
		<s:if test="%{!'wired'.equals(listType)}">
			operationItems.push(
					{ text: '<s:text name="monitor.activeClient.operation.show.tsinfo"/>', onclick: { fn: showTsinfo }}
					);
			operationItems.push(
					{ text: '<s:text name="monitor.activeClient.operation.enroll"/>', onclick: { fn: showEnrollStatus }}
					);
		</s:if>
	</s:else>

	<s:if test="%{teacherViewEnabled}">

		operationItems.push(
    			{text: '<s:text name="monitor.activeClient.operation.addToComputerCart"/>', disabled: <s:property value="disableAddToComputerCartList" />,
			        submenu: {
						id: "addToComputerCart", // Id for the submenu element to be created
						itemdata: [
							<s:iterator value="computerCartList" status="status">
								{text: "<s:property value="cartName" />", onclick: { fn: computerCartMenuClick, obj: "<s:property value="cartName" />" }},
							</s:iterator>
						]}
				});
	</s:if>

	operationMenu.addItems(operationItems);
	//operationMenu.subscribe('click', operationMenuItemClick);
	operationMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('operationMenuBtn');
		var y = YAHOO.util.Dom.getY('operationMenuBtn');
		YAHOO.util.Dom.setX('operation_menu', x);
		YAHOO.util.Dom.setY('operation_menu', y+20);
	});

	operationMenu.render(document.body);
}

function computerCartMenuClick(p_sType, p_aArgs, p_oValue)
{
	//validate
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0) {
		warnDialog.cfg.setProperty('text', "There is no item to operation.");
		warnDialog.show();
		return false;
	}

	if (!hm.util.hasCheckedBoxes(inputElements))
	{
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	showProcessing();
	document.forms[formName].operation.value = 'addToComputerCart';
	document.forms[formName].selectAll.value = isSelectAll();
	document.forms[formName].selectedClientIDStr.value = hm.util.getSelectedIds();
	document.forms[formName].clientWatchName.value=p_oValue;
    document.forms[formName].submit();
}

function locationWatchMenuClick(p_sType, p_aArgs, p_oValue)
{
	//validate
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0) {
		warnDialog.cfg.setProperty('text', "There is no item to operation.");
		warnDialog.show();
		return false;
	}

	if (!hm.util.hasCheckedBoxes(inputElements))
	{
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	showProcessing();
	document.forms[formName].operation.value = 'addToClientWatch';
	document.forms[formName].selectAll.value = isSelectAll();
	document.forms[formName].selectedClientIDStr.value = hm.util.getSelectedIds();
	document.forms[formName].clientWatchName.value=p_oValue;
    document.forms[formName].submit();
}

var clientId;
function showTsinfo() {
	if (!checkClientExists()) {
		return;
	}
	var inputElements = document.getElementsByName('selectedIds');
	clientId = 0;
	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			var clientType = Get("clientType_"+inputElements[i].value);
			if(clientType && clientType.value == "wired"){
				warnDialog.cfg.setProperty('text', '<s:text name="error.mdm.object.wired.client.selected"/>');
				warnDialog.show();
				return;
			}
			if (!clientId) {
				clientId = inputElements[i].value;
			}
		}
	}

	if(hm.util.getSelectedIds().length > 1){
		warnDialog.cfg.setProperty('text', '<s:text name="action.error.select.one.item" />');
		warnDialog.show();
		return;
	}

	if (!clientId)
	{
		warnDialog.cfg.setProperty('text', '<s:text name="action.error.no.item.to.operation"/>');
		warnDialog.show();
		return;
	}

	var url = "<s:url action='clientMonitor' includeParams='none' />" +
				"?operation=showTsinfo"+"&selectedClientIDStr="+
				hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success: prepareTsinfoResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}
var editTsinfoPanel = null;
var prepareTsinfoResult = function(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	try{
		eval("var result = " + o.responseText);
		if(result.errMsg){
			showWarnDialog(result.errMsg);
		}
	}catch(e){
		var div = window.document.getElementById('clientTsInfoPannel');
		if(div.innerHTML != ""){
			set_innerHTML("clientTsInfoPannel", "");
		}
		set_innerHTML("clientTsInfoPannel", o.responseText);
		editTsinfoPanel = new YAHOO.widget.Panel(div, {
			width:"500px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true,
			modal:true
		} );
		editTsinfoPanel.render(document.body);
		editTsinfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
		editTsinfoPanel.show();
	}
}
function deleteTs(data){
	var tids = new Array();
	tids = data;
	if(tids.length < 1){
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	}else{
		var url = "<s:url action='clientMonitor' includeParams='none' />" +
		"?operation=deleteTsinfo"+"&selectedClientIDStr="+
		hm.util.getSelectedIds() + "&tids=" + tids +"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success: deleteTsinfoResult, failure:abortResult,timeout: 3000000}, null);
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
}

function deleteTsinfoResult(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);

	/* if(result.deletedId.length > 0){
		hideEditTsinfoPanel();
		showTsinfo();
		var tidArray = new Array();
		tidArray = result.deletedId;
		for(var i = 0 ; i < tidArray.length; i ++){
			Get("tids_" + tidArray[i]).checked = false;
			$("#row_" + tidArray[i]).hide();
		}
		$("#tbl_tsinfo tbody tr:visible:odd").each(function() {
		    $(this).removeClass().addClass("odd");
		});
		$("#tbl_tsinfo tbody tr:visible:even").each(function() {
		    $(this).removeClass("even").removeClass("odd").addClass("even");
		});
	}
	if(!result.succ){
		warnDialog.cfg.setProperty('text', result.errMsg);
		warnDialog.show();
	} */

	if(result.succ){
		hideEditTsinfoPanel();
		showTsinfo();
	}else{
		warnDialog.cfg.setProperty('text', result.errMsg);
		warnDialog.show();
	}

}

function hideEditTsinfoPanel(){
	if(null != editTsinfoPanel){
		editTsinfoPanel.hide();
		set_innerHTML("clientTsInfoPannel", "");
	}
}

function showLocation() {
	if (!checkClientExists()) {
		return;
	}
	var inputElements = document.getElementsByName('selectedIds');
	clientId = 0;
	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			if (!clientId) {
				clientId = inputElements[i].value;
			} else {
				warnDialog.cfg.setProperty('text', "Please select only one item.");
				warnDialog.show();
				return;
			}
		}
	}
	if (!clientId)
	{
		warnDialog.cfg.setProperty('text', "Please select an item first.");
		warnDialog.show();
		return;
	}
	requestLocation("locateClient", clientId, 2, locationSucceeded);
}

function showClientLocation(id) {
	clientId = id;
	requestLocation("locateClient", clientId, 1, locationSucceeded);
}
function requestLocation(operation, clientId, circle, callback) {
	url = "<s:url action='maps' includeParams='none' />?operation=" + operation + "&clientId=" + clientId + "&ch1=" + circle;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success:callback,failure:requestFailed,timeout: 10000}, null);
}
var locationInfoDialog = null;
var locationSucceeded = function(o){
	eval("var result = " + o.responseText);
	if (locationInfoDialog == null) {
		locationInfoDialog =
			new YAHOO.widget.SimpleDialog("locationInfoDialog",
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
		var circle = result.circle ? 1 : 2;
		var url = "<s:url action='maps' includeParams='none' />?operation=mapClient&selectedMapId="+result.mapId+"&clientId="+clientId+"&ch1="+circle;
		window.location.href = url;
	}
}
var requestFailed = function(o) {
	warnDialog.cfg.setProperty('text', "Operation failed.  Please try again later.");
	warnDialog.show();
}

function showOperationMenu()
{
	operationMenu.show();
}

var settingsMenu;
function createSettingMenu()
{
	settingsMenu = new YAHOO.widget.Menu('settings_menu', { fixedcenter: false });
	var settingItems = [
			 [
		        { text: '<s:text name="monitor.activeClient.setting.refresh"/>'}
			 ]
	];
	settingsMenu.addItems(settingItems);
	settingsMenu.subscribe('click', settingsMenuItemClick);
	settingsMenu.subscribe("beforeShow", function(){
		var x = YAHOO.util.Dom.getX('settingsMenuBtn');
		var y = YAHOO.util.Dom.getY('settingsMenuBtn');
		YAHOO.util.Dom.setX('settings_menu', x);
		YAHOO.util.Dom.setY('settings_menu', y+20);
	});

	settingsMenu.render(document.body);
}

function settingsMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	var text = menuItem.cfg.getProperty("text");

	if (text == '<s:text name="monitor.activeClient.setting.refresh"/>')
	{
		openRefreshSetPanel();
	}
}

function showSettingMenu()
{
	settingsMenu.show();
}

function onLoadPage() {
	// Overlay for waiting dialog
	createWaitingPanel();
	// create deauth overlay
	var div = document.getElementById('deauthPanel');
	deauthOverlay = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	deauthOverlay.render(document.body);
	div.style.display = "";
	overlayManager.register(deauthOverlay);
	deauthOverlay.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});

	//create multi edit overlay
	div = document.getElementById('multiEditPanel');
	multiEditPanel = new YAHOO.widget.Panel(div, {
			width:"420px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true
			});
	multiEditPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(multiEditPanel);
	multiEditPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});

	// create client refresh setting overlay
	var div = document.getElementById('refreshSettingPanel');
	refreshSetPanel = new YAHOO.widget.Panel(div, {
		width:"500px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	refreshSetPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(refreshSetPanel);
	refreshSetPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});

	createCliInfoPanel();

	//start client paging timer
	<s:if test="%{pageAutoRefresh}">
	startClientPagingTimer();
	</s:if>
}
function onUnloadPage() {
	clearTimeout(clientPagingTimeoutId);
}

var doCustomAutoRefreshSettingSubmit = function(postfix) {
	var baseUrl = "<s:url action="clientMonitor" includeParams="none" />?ignore=" + new Date().getTime();
	var url = baseUrl + "&" + postfix;
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateAutoRefreshSetting }, null);
}
var updateAutoRefreshSetting = function(o) {
	eval("var result = " + o.responseText);
	if (hm.util.isAFunction(updateAutoRefreshStatus)) {
		updateAutoRefreshStatus(result.autoOn);
	}
	if (result.autoOn == false) {
		clearTimeout(clientPagingTimeoutId);
	} else {
		if (result.refreshOnce) {
			submitAction('refreshFromCache');
		} else {
			startClientPagingTimer();
		}
	}
}

var clientPagingLiveCount = 0;
var clientPagingTimeoutId;
var duration = <s:property value="%{sessionTimeOut}" /> * 60;  // minutes * 60
var interval = <s:property value="%{pageRefInterval}"/>; // seconds
var total = duration / interval;

function startClientPagingTimer() {
	if (clientPagingLiveCount++ < total) {
		clientPagingTimeoutId = setTimeout("pollClientPagingCache()", interval * 1000);  // seconds
	}
}
function pollClientPagingCache() {
	var url = "<s:url action="clientMonitor" includeParams="none" />?operation=pollClientList&cacheId=<s:property value="%{cacheId}"/>&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateClient }, null);
}
//cached value for refresh which is updated when overlay is showing
var cachedRefresh = false;

function updateClient(o) {
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
	startClientPagingTimer();
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
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams='none'/>" />');
	waitingPanel.render(document.body);
	overlayManager.register(waitingPanel);
	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function submitRefreshClientAction()
{
	submitAction('refreshClient');
}

function submitNewClientWatchAction()
{
	submitAction('newClientWatch');
}

var thisOperation;
function submitAction(operation)
{
	thisOperation = operation;
	if (operation == 'refreshClient')
	{
		checkAndConfirmRefreshClient();
	}
	else if (operation == 'deauthClient')
	{
		confirmDeauthClient();
	}
	else if (validate(operation))
	{
		doContinueOper();
	}
}

function submitFormAction(formName, operation)
{
	showProcessing();
	document.forms[formName].operation.value = operation;
	document.forms[formName].selectAll.value = isSelectAll();
	document.forms[formName].selectedClientIDStr.value = hm.util.getSelectedIds();
	//selectAll="+isSelectAll()+"&selectedClientIDStr="+hm.util.getSelectedIds()
    document.forms[formName].submit();
}

function doContinueOper()
{
	if (thisOperation == 'refreshClient')
	{
		refreshActiveClients();
		return;
	}

	if (thisOperation == 'deauthClient')
	{
		deauthActiveClients();
		return;
	}

	if(thisOperation == 'updateOSVersion'){
		var os_option55 = document.getElementById("dataSource_os_option55");
		var os_type = document.getElementById("dataSource_os_type");
		var append_marking = document.getElementById("dataSource_append_marking");
		if(validateOSVersion()){
			document.getElementById(formName + "_client_os_option55").value = os_option55.value;
			document.getElementById(formName + "_client_os_type").value = os_type.value;
			document.getElementById(formName + "_append_marking").value = append_marking.checked;

			doAjaxRequest('validateOSVersion',validateResult);
		}
	}else{
		showProcessing();
		document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
	}
}

function validate(operation)
{
	return true;
}

checkAndConfirmRefreshClient = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0) {
		warnDialog.cfg.setProperty('text', "There are no clients to refresh.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one client.");
		warnDialog.show();
	} else if (inputElements.length > 50) {
		confirmDialog.cfg.setProperty('text', "<html><body>If you refresh numerous clients at one time, the operation might take up to several minutes.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	} else
	{
		doContinueOper();
	}
}

checkClientExists = function()
{
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements.length == 0) {
		warnDialog.cfg.setProperty('text', "There are no clients on which to perform the selected operation.");
		warnDialog.show();
		return false;
	}

	return true;
}

confirmDeauthClient = function()
{
	var clientSelect = document.getElementById("deauthClientSelect");
	if (clientSelect.value == 2) //selected Mac
	{
		var inputElements = document.getElementsByName('selectedIds');
		if (!hm.util.hasCheckedBoxes(inputElements))
		{
			warnDialog.cfg.setProperty('text', "Please select at least one client.");
			warnDialog.show();
			return;
		}
	}

	confirmDialog.cfg.setProperty('text', "<html><body>This operation will deauth the selected client(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

function insertPageContext()
{
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
	<s:if test="%{enableClientManagement && !wiredClientList && !wirelessClientList}">
		document.writeln('<td nowrap>&nbsp;&nbsp;&nbsp;&nbsp;<a id="viewClientList" href="<s:property value='viewClientListURL'/>" tabindex="-1" target="_blank"><s:text name="config.active.client.manage.helplink.lablel"/>&nbsp;&nbsp;></a></td>');
	</s:if>
}

function refreshActiveClients()
{
	url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=refreshClient&selectAll="+isSelectAll()+"&selectedClientIDStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();

	//Mark: let's simply set timeout value a huge number.
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: refreshClientsResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function deauthActiveClients()
{
	url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=deauthClient&deauthClientSelect="+document.getElementById("deauthClientSelect").value+"&clearCache="+document.getElementById("isClearCache").checked+"&deauthCacheSelect="+document.getElementById("deauthCacheSelect").value+"&selectedClientIDStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();

	//Mark: let's simply set timeout value a huge number.
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: deauthClientsResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var abortResult = function(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	hm.util.clearSelection();
}

var refreshClientsResult = function(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	hm.util.clearSelection();

	eval("var result = " + o.responseText);
	if(result.success == 0)
	{
		var redirect_url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=view&listType="+result.listType+"&ignore=" + new Date().getTime();
		window.location.replace(redirect_url);
	}
	else if(result.success == 1)
	{
		if(warnDialog != null)
		{
			warnDialog.cfg.setProperty('text', "Refresh failed. Maybe <s:text name='hm.config.guide.hiveAp.title'/> are disconnected from " + sysName + ".");
			warnDialog.show();
		}
	}
	else if(result.success == 2)
	{
		if(warnDialog != null)
		{
			warnDialog.cfg.setProperty('text', "Refresh partly failed. Maybe some <s:text name='hm.config.guide.hiveAp.title'/> are disconnected from " + sysName + ".");
			warnDialog.show();
		}
	}
}

var deauthClientsResult = function(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	hm.util.clearSelection();

	eval("var result = " + o.responseText);
	if(result.success){
		var redirect_url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=view&ignore=" + new Date().getTime();
		window.location.replace(redirect_url);
	}else{
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', "<s:text name='error.monitor.activeClient.refresh'/>");
			warnDialog.show();
		}
	}
}

function isSelectAll()
{
	return document.getElementById("checkAll").checked;
}

function selectClearCache(checked)
{
	if (checked)
	{
		document.getElementById("deauthCacheSelect").disabled = false;
	}
	else
	{
		document.getElementById("deauthCacheSelect").disabled = true;
	}
}

function selectEditUserName(checked)
{
	if (checked)
	{
		document.getElementById("editUserName").disabled = false;
	}
	else
	{
		document.getElementById("editUserName").disabled = true;
	}
}

function selectEditHostName(checked)
{
	if (checked)
	{
		document.getElementById("editHostName").disabled = false;
	}
	else
	{
		document.getElementById("editHostName").disabled = true;
	}
}

function selectEditClietIP(checked)
{
	if (checked)
	{
		document.getElementById("editIP").disabled = false;
	}
	else
	{
		document.getElementById("editIP").disabled = true;
	}
}

function selectEditComment1(checked)
{
	if (checked)
	{
		document.getElementById("editComment1").disabled = false;
	}
	else
	{
		document.getElementById("editComment1").disabled = true;
	}
}

function selectEditComment2(checked)
{
	if (checked)
	{
		document.getElementById("editComment2").disabled = false;
	}
	else
	{
		document.getElementById("editComment2").disabled = true;
	}
}

function selectEnableClientRefresh(checked)
{
	document.getElementById("clientRefreshInterval").disabled = false;
	document.getElementById("clientRefreshFilter").disabled = false;
}

function selectDisableClientRefresh(checked)
{
	document.getElementById("clientRefreshInterval").disabled = true;
	document.getElementById("clientRefreshFilter").disabled = true;
}

function updateRefreshSetting()
{
	if (document.getElementById("enableClientRefresh").checked)
	{
		var interval = document.getElementById("clientRefreshInterval");

		if (interval.value.length == 0)
		{
	        hm.util.reportFieldError(interval, '<s:text name="error.requiredField"><s:param><s:text name="admin.activeClientConfig.refreshInterval" /></s:param></s:text>');
	        interval.focus();
	        return false;
	    }
	    else if (!isValidInterval(interval.value))
	    {
			hm.util.reportFieldError(interval, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.activeClientConfig.refreshInterval" /></s:param><s:param><s:text name="admin.activeClientConfig.intervalRange" /></s:param></s:text>');
			interval.focus();
			return false;
		}
	}

	var clientRefreshFlag = document.getElementById('disableClientRefresh').checked ? "disableClientRefresh" : "enableClientRefresh";

	url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=updateRefreshSetting"
		+ "&clientRefreshFlag="+clientRefreshFlag+"&clientRefreshInterval="+document.getElementById("clientRefreshInterval").value
		+"&clientRefreshFilter="+document.getElementById("clientRefreshFilter").value+"&ignore=" + new Date().getTime();

	YAHOO.util.Connect.asyncRequest('get', url, {success:updateRefreshResult}, null);

	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function isValidInterval(interval)
{
	var intValue = interval.valueOf();
	if ( intValue >=10 && intValue <= 1440 )
	{
		return true;
	}

	return false;
}

function updateRefreshResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	if(result.success){
		var redirect_url = "<s:url action='clientMonitor' includeParams='none' />" + "?operation=view&ignore=" + new Date().getTime();
		window.location.replace(redirect_url);
	}else{
		if(warnDialog != null){
			warnDialog.cfg.setProperty('text', "Unable to update periodic client database updates setting. DB error.");
			warnDialog.show();
		}
	}
}

function viewMonitoringInfo(clientMac){
	var redirect_url = "<s:url action='hiveApToolkit' includeParams='none' />" + "?operation=toolDebugClient&clientMac="+clientMac+"&ignore=" + new Date().getTime();
	window.location.href = redirect_url;
}


var debugClientPanel = null;
function createDebugClientPanel(width, height){
	var div = document.getElementById("debugClientPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("debug_client");
	iframe.width = width;
	iframe.height = height;
	debugClientPanel = new YAHOO.widget.Panel(div, { fixedcenter:true, width:(width+20)+"px", visible:false, constraintoviewport:true} );
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
	if(null == debugClientPanel){
		var viewportWidth = YAHOO.util.Dom.getViewportWidth()*0.8;
		var viewportHeight = YAHOO.util.Dom.getViewportHeight()*0.8;
		if(viewportWidth >= 835){
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
	document.forms[formName].operation.value = "initDebugClientFromActiveclient";
	document.forms[formName].target="debug_client";
	document.forms[formName].action = "<s:url action='hiveApToolkit' includeParams='none' />";
	document.forms[formName].submit();
	document.forms[formName].target="_self";
	document.forms[formName].action = "<s:url action='clientMonitor' includeParams='none' />";
}
function updateClientDebugPanelTitle(str){
	if(null != debugClientPanel){
		debugClientPanel.header.innerHTML = "<s:text name="topology.menu.troubleshoot.clientTrace"/>"+" - "+str;
	}
}
var clientTraceIframeWindow;

function createCliInfoPanel() {
	var div = window.document.getElementById('cliInfoPanel');
	cliInfoPanel = new YAHOO.widget.Panel(div, {
		width:"600px",
		visible:false,
		fixedcenter:"contained",
		draggable:true,
		constraintoviewport:true } );
	cliInfoPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(cliInfoPanel);
	//cliInfoPanel.hideEvent.subscribe(resetParams);
	cliInfoPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function showEnrollStatus() {
	if (!checkClientExists()) {
		return;
	}
	var inputElements = document.getElementsByName('selectedIds');
	clientId = 0;
	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			var clientType = Get("clientType_"+inputElements[i].value);
			if(clientType && clientType.value == "wired"){
				warnDialog.cfg.setProperty('text', '<s:text name="error.mdm.object.wired.client.selected"/>');
				warnDialog.show();
				return;
			}
			if (!clientId) {
				clientId = inputElements[i].value;
			}
		}
	}
	if (!clientId)
	{
		warnDialog.cfg.setProperty('text', '<s:text name="action.error.no.item.to.operation"/>');
		warnDialog.show();
		return;
	}


	var url = "<s:url action='clientMonitor' includeParams='none' />" +
			"?operation=showEnrollStatus"+"&selectedClientIDStr="+hm.util.getSelectedIds()+"&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success: enrollStatusResult, failure:abortResult,timeout: 3000000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var enrollStatusResult = function(o)
{
	if(waitingPanel != null){
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	var cliDiv = document.getElementById("cli_viewer");

	if(result.suc){
		cliDiv.innerHTML = "<pre>" + result.msg + "</pre>";
	}else{
		cliDiv.innerHTML = "<pre>" + result.err + "</pre>";
	}

	if(null == cliInfoPanel){
		createCliInfoPanel();
	}
   /* var panelHeight = YAHOO.util.Dom.get('cliInfoPanel').offsetHeight;
	resetPanelSize(panelHeight, cliInfoPanel); */
	cliInfoPanel.cfg.setProperty('visible', true);
}

//collect OS type
var editOSVersionPanel = null;
function createEditOSVersionPanel(){
	var div = window.document.getElementById('editOSVersionPanel');
	editOSVersionPanel = new YAHOO.widget.Panel(div, {
		width:"460px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		constraintoviewport:true,
		modal:true
	} );
	editOSVersionPanel.render(document.body);
	div.style.display = "";
	editOSVersionPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function hideEditOSVersionPanel(){
	if(null != editOSVersionPanel){
		document.getElementById("dataSource_os_type").value = "";
		document.getElementById("dataSource_os_option55").value = "";
		startClientPagingTimer();
		editOSVersionPanel.hide();
	}
}

function showEditOSVersionPanel(option55,osversion){
	if(null != editOSVersionPanel){
		editOSVersionPanel.show();
	}else{
		createEditOSVersionPanel();
		editOSVersionPanel.show();
	}
	clearTimeout(clientPagingTimeoutId);
	document.getElementById("dataSource_os_option55").value = option55;
	if(osversion != "unknown"){
		document.getElementById("dataSource_os_type").value = osversion;
	}
}

function validateOSVersion(){
	var os_option55 = document.getElementById("dataSource_os_option55");
	var os_type = document.getElementById("dataSource_os_type");
	var append_marking = document.getElementById("dataSource_append_marking");

	if(os_type.value.length <= 0){
		hm.util.reportFieldError(os_type, '<s:text name="error.requiredField"><s:param><s:text name="config.textfile.osObject.osType" /></s:param></s:text>');
		os_type.focus();
	    return false;
	}else if(os_type.value.length > 32){
		hm.util.reportFieldError(os_type, '<s:text name="error.keyLengthRange"><s:param><s:text name="config.textfile.osObject.osType" /></s:param><s:param>32</s:param></s:text>');
		os_type.focus();
	    return false;
	}
	return true;
}

function doAjaxRequest(operation, callback){
	document.forms[formName].operation.value = operation;
	YAHOO.util.Connect.setForm(document.forms[formName]);
	url = "<s:url action='clientMonitor' includeParams='none' />";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:callback,failure:connectedFailed,timeout: 60000});
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

var validateResult = function(o){
	var os_option55 = document.getElementById("dataSource_os_option55");
	var os_type = document.getElementById("dataSource_os_type");
	var append_marking = document.getElementById("dataSource_append_marking");

	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var response = " + o.responseText);
	var errorMessage = "";

	if(response.results == 0)
	{
		//doAjaxRequest("updateOSVersion",refreshPage);
		document.forms[formName].operation.value = 'updateOSVersion';
	    document.forms[formName].submit();

	}else if(response.results == 1){
		errorMessage = '<s:text name="error.create.invalid"><s:param><s:text name="config.textfile.osObject.osType" /></s:param><s:param>' + os_type.value +'</s:param></s:text>';
		hm.util.reportFieldError(os_type, errorMessage);
		os_type.focus();
	}else if(response.results == 2){
		errorMessage = '<s:text name="error.create.fail"><s:param>' + os_type.value +'</s:param></s:text>';
		hm.util.reportFieldError(os_type, errorMessage);
		os_type.focus();
	}else if(response.results == 3){
		hm.util.reportFieldError(append_marking, '<s:text name="error.create.fileNotExist"></s:text>');
	}
}

function showEnrolledClientDetails(url) {
	<s:if test="%{showDomain}">
		return;
	</s:if>
	<s:else>
		window.open(url, target='_blank');
	</s:else>
}
</script>

<div id="content">
	<s:form action="clientMonitor">
		<s:hidden name="cacheId" />
		<s:hidden name="selectAll" />
		<s:hidden name="selectedClientIDStr" />
		<s:hidden name="clientWatchName" />
		<s:hidden name="listType"/>
		<s:hidden name="client_os_type"/>
		<s:hidden name="client_os_option55"/>
		<s:hidden name="append_marking"/>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<script>createOperationMenu();createSettingMenu();
							</script>
							<td>
								<input type="button" name="ignore" value="Operation..."
									class="button" id="operationMenuBtn"
									onclick="showOperationMenu();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<div id="o_menu" class="yuimenu" />
							</td>
							<td>
								<input type="button" name="ignore" value="Modify" class="button"
									onClick="openModifyDialog();"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="ignore" value="Settings..."
									class="button" id="settingsMenuBtn"
									onclick="showSettingMenu();"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td>
								<table width="100%" cellspacing="0" cellpadding="0" border="0"
									class="view">
									<tr>
										<th class="check">
											<input type="checkbox" id="checkAll"
												onClick="hm.util.toggleCheckAll(this);">
										</th>
										<s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th style="padding-left: <s:property value="%{indent}" />px">
													<ah:sort name="clientMac" key="monitor.client.clientMac" />
												</th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th>
													<s:text name="report.reportList.title.vendorName" />
												</th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th>
													<ah:sort name="clientIP" key="monitor.client.clientIP"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 29}">
												<th>
													<s:text name="monitor.client.clientNatIP"/>
												</th>
											</s:if>
											<s:if test="%{columnId == 4}">
												<th>
													<ah:sort name="clientHostname"
														key="monitor.client.clientHostname" />
												</th>
											</s:if>
											<s:if test="%{columnId == 5}">
												<th>
													<ah:sort name="clientUsername"
														key="monitor.client.clientUserName" />
												</th>
											</s:if>
											<s:if test="%{columnId == 24}">
												<th>
													<ah:sort name="email"
														key="monitor.client.email" />
												</th>
											</s:if>
											<s:if test="%{columnId == 25}">
												<th>
													<ah:sort name="companyName"
														key="monitor.client.company" />
												</th>
											</s:if>
											<s:if test="%{columnId == 22}">
												<th>
													<ah:sort name="clientOsInfo"
														key="monitor.client.osInfo" />
												</th>
											</s:if>
											<s:if test="%{columnId == 18}">
												<th>
													<s:text name="monitor.client.clientLocation" />
												</th>
											</s:if>
											<s:if test="%{columnId == 23}">
												<th>
													<s:text name="monitor.client.lastHourData" />
												</th>
											</s:if>
											<s:if test="%{columnId == 6}">
												<th>
													<ah:sort name="startTimeStamp"
														key="monitor.client.startTime" />
												</th>
											</s:if>
											<s:if test="%{columnId == 7}">
											<s:if test="%{wiredClientList == true}">
												<th>
													<ah:sort name="apName" key="monitor.client.deviceName" />
												</th>
											</s:if>
											<s:else>
												<th>
													<ah:sort name="apName" key="monitor.client.apName" />
												</th>
											</s:else>

											</s:if>
											<s:if test="%{columnId == 8}">
												<th>
													<ah:sort name="apMac" key="monitor.client.associationAP" />
												</th>
											</s:if>
											<s:if test="%{columnId == 9}">
												<th>
													<s:if test="%{wiredClientList}">
														<ah:sort name="clientSSID" key="monitor.client.ssid.lan" />
													</s:if>
													<s:else>
														<ah:sort name="clientSSID" key="monitor.client.ssid" />
													</s:else>
												</th>
											</s:if>
											<s:if test="%{columnId == 10}">
												<th>
													<ah:sort name="clientBSSID" key="monitor.client.bssid" />
												</th>
											</s:if>
											<s:if test="%{columnId == 11}">
												<th>
													<ah:sort name="clientMACProtocol"
														key="monitor.client.associateMode" />
												</th>
											</s:if>
											<s:if test="%{columnId == 12}">
												<th>
													<ah:sort name="clientVLAN" key="monitor.client.vlan" />
												</th>
											</s:if>
											<s:if test="%{columnId == 13}">
												<th>
													<ah:sort name="clientUserProfId"
														key="monitor.client.userProfileID" />
												</th>
											</s:if>
											<s:if test="%{columnId == 14}">
												<th>
													<ah:sort name="clientAuthMethod"
														key="monitor.client.clientAuth" />
												</th>
											</s:if>
											<s:if test="%{columnId == 15}">
												<th>
													<ah:sort name="clientEncryptionMethod"
														key="monitor.client.encryption" />
												</th>
											</s:if>
											<s:if test="%{columnId == 19}">
												<th>
													<ah:sort name="clientChannel" key="monitor.client.channel" />
												</th>
											</s:if>
											<s:if test="%{columnId == 16}">
												<th>
													<ah:sort name="comment1" key="monitor.client.comment1" />
												</th>
											</s:if>
											<s:if test="%{columnId == 17}">
												<th>
													<ah:sort name="comment1" key="monitor.client.comment2" />
												</th>
											</s:if>
											<s:if test="%{columnId == 20}">
												<th>
													<ah:sort name="overallClientHealthScore" key="monitor.client.health" />
												</th>
											</s:if>
											<s:if test="%{columnId == 21}">
												<th>
													<ah:sort name="ifName" key="monitor.client.ifName" />
												</th>
											</s:if>
											<s:if test="%{columnId == 26}">
												<th>
													<ah:sort name="clientRssi" key="monitor.client.rssi" />
												</th>
											</s:if>
											<s:if test="%{columnId == 27}">
												<th>
													<s:text name="monitor.client.snr" />
												</th>
											</s:if>
											<s:if test="%{'all'.equals(listType) && columnId == 28}">
												<th>
													<s:text name="monitor.client.type" />
												</th>
											</s:if>
											<s:if test="%{columnId == 30}">
												<th>
													<s:text name="monitor.enrolled.client.managed" />
												</th>
											</s:if>

										</s:iterator>
										<s:if test="%{showDomain}">
											<th>
												<s:text name="config.domain" />
											</th>
										</s:if>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status" id="pageRow">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<s:if
												test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
												<td class="listCheck">
													<input type="checkbox" disabled="disabled" />
												</td>
											</s:if>
											<s:else>
												<td class="listCheck">
													<ah:checkItem />
												</td>
											</s:else>
											<s:iterator value="%{selectedColumns}">
												<s:if test="%{columnId == 1}">
													<td nowrap="nowrap" class="list">
														<s:if test="%{monitoring}">
															<s:if test="%{showDomain && #pageRow.owner.domainName!='home' && #pageRow.owner.domainName!='global'}">
																<img src="<s:url value="/images/monitoring.png" includeParams="none"/>" title="<s:text
																name="infor.monitor.activeClient.monitoring"></s:text>" width="16" class="dinl" style="vertical-align:middle;"></img>
															</s:if>
															<s:else>
																<img src="<s:url value="/images/monitoring.png" includeParams="none"/>" title="<s:text
																name="infor.monitor.activeClient.monitoring.clickable"></s:text>" width="16" class="dinl" style="vertical-align:middle; cursor: pointer;"
																onclick="viewMonitoringInfo('<s:property value='clientMac' />');"></img>
															</s:else>
														</s:if>
														<s:else>
															<s:if test="%{anyMonitoring}">
																<img src="<s:url value="/images/spacer.gif" includeParams="none"/>" width="16"></img>
															</s:if>
														</s:else>
														<s:if test="%{clientChannel > 0}">
															<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="enrolledClientMacAddress" value="%{clientMac}"/><s:param name="listType" value="%{'wireless'}"/></s:url>'><s:property
																value="clientMac" /> </a>
														</s:if>
														<s:else>
															<a href='<s:url action="clientMonitor"><s:param name="operation" value="%{'showDetail'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="listType" value="%{'wired'}"/><s:param name="enrolledClientMacAddress" value="%{clientMac}"/></s:url>'><s:property
																value="clientMac" /> </a>
														</s:else>
														<input type="hidden" id='clientType_<s:property value="#pageRow.id"/>' value='<s:property value="activeClientType"/>'/>
													</td>
												</s:if>
												<s:if test="%{columnId == 2}">
													<td class="list" nowrap="nowrap">
														<s:property value="vendorName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 3}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientIP" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 29}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientNatIP" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 4}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientHostname" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 5}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientUsername" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 24}">
													<td class="list" nowrap="nowrap">
														<s:property value="email" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 25}">
													<td class="list" nowrap="nowrap">
														<s:property value="companyName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 22}">
													<s:if test="%{osOption55Exist}">
														<td class="list" nowrap="nowrap">
															<a href="#" onClick="showEditOSVersionPanel('<s:property value="os_option55" />','<s:property value="clientOsInfo" />')" >
																<s:property value="clientOsInfo" />
															</a>
														</td>
													</s:if>
													<s:else>
														<td class="list" nowrap="nowrap">
															<s:property value="clientOsInfo" />
															&nbsp;
													</td>
													</s:else>
												</s:if>
												<s:if test="%{columnId == 18}">
													<s:if test="%{rssiCount > 0}">
														<td class="list" nowrap="nowrap">
															<a href="#location"
																onclick="showClientLocation(<s:property value="#pageRow.id" />);">
																<s:property value="mapName" /> </a> &nbsp;
														</td>
													</s:if>
													<s:else>
														<td class="list" nowrap="nowrap">
															<s:property value="mapName" />
															&nbsp;
														</td>
													</s:else>
												</s:if>
												<s:if test="%{columnId == 23}">
													<td class="list" nowrap="nowrap">
														<s:property value="last2HourDataString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 6}">
													<td class="list" nowrap="nowrap">
														<s:property value="startTimeString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 7}">
													<td class="list" nowrap="nowrap">
														<s:property value="apName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 8}">
													<td class="list" nowrap="nowrap">
														<s:property value="apMac" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 9}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientSSID" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 10}">
													<td class="list" nowrap="nowrap">
													<s:if test="%{clientBSSID != ''}">
														<a
															href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedHiveAps'}"/><s:param name="operation" value="%{'showHiveApDetails'}"/><s:param name="id" value="%{belongAPID}"/></s:url>'><s:property
																value="clientBSSID" />&nbsp;</a>
													</s:if>&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 11}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientMacPtlString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 12}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientVLANString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 13}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientUserProfId4Show" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 14}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientAuthMethodString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 15}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientEncryptionMethodString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 19}">
													<td class="list" nowrap="nowrap" align="center">
														<s:property value="clientChannelString" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 16}">
													<td class="list" nowrap="nowrap">
														<s:property value="comment1" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 17}">
													<td class="list" nowrap="nowrap">
														<s:property value="comment2" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 20}">
													<td class="list" align="center">
														<s:if test="%{overallClientHealthScore>=0 && overallClientHealthScore<25}">
															<img src="<s:url value="/images/client/SLA-Health-25-EKG.png" />"
																title="<s:property value="overallClientHealthScore" />" border="0px" class="dblk">
														</s:if>
														<s:elseif test="%{overallClientHealthScore>=25 && overallClientHealthScore<50}">
															<img src="<s:url value="/images/client/SLA-Health-75-EKG.png" />"
																title="<s:property value="overallClientHealthScore" />" border="0px" class="dblk">
														</s:elseif>
														<s:else>
															<img src="<s:url value="/images/client/SLA-Health-100-EKG.png" />"
																title="<s:property value="overallClientHealthScore" />" border="0px" class="dblk">
														</s:else>
													</td>
												</s:if>
												<s:if test="%{columnId == 21}">
													<td class="list" nowrap="nowrap">
														<s:property value="ifName" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 26}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientRSSI4Show" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 27}">
													<td class="list" nowrap="nowrap">
														<s:property value="clientSNRShow" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{'all'.equals(listType) && columnId == 28}">
													<td class="list" nowrap="nowrap">
														<s:property value="activeClientType" />
														&nbsp;
													</td>
												</s:if>
												<s:if test="%{columnId == 30}">
													<td class="list">
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
														<s:if test="%{displayEnrolledCMIcon}">
															<td valign="middle" style="padding-right: 2px">
															<a href="javascript: void(0);" onclick="showEnrolledClientDetails('<s:property value="enrolledCMURL" />')">
															<span class="css_icon_cmenrolled" onmousemove="this.className='css_icon_cmenrolled_over'"
															 onmouseout="this.className='css_icon_cmenrolled'"
															 title="Click it will display client detail information on Client Management server."></span>
															</a>
															
															</td>
														</s:if>
														<s:if test="%{displayEnrolledSLIcon}">
															<td valign="middle" style="padding-right: 2px">
															<a href="javascript: void(0);" onclick="showEnrolledClientDetails('<s:property value="enrolledSLURL" />')">
															<span class="css_icon_slenrolled" onmousemove="this.className='css_icon_slenrolled_over'"
															 onmouseout="this.className='css_icon_slenrolled'"
															 title="Click it will display client detail information on Social Login server."></span>
															</a>
															
															</td>
														</s:if>
														<s:if test="%{displayEnrolledIDMIcon}">
															<td>
															<a href="javascript: void(0);" onclick="showEnrolledClientDetails('<s:property value="enrolledIDMURL" />')">
															<span class="css_icon_idmenrolled" onmousemove="this.className='css_icon_idmenrolled_over'"
															 onmouseout="this.className='css_icon_idmenrolled'"
															 title="Click it will display client detail information on IDM server."></span>
															</a>
															</td>
														</s:if>
															</tr>
														</table>
													</td>
												</s:if>
											</s:iterator>
											<s:if test="%{showDomain}">
												<td class="list" nowrap="nowrap">
													<s:property value="%{owner.domainName}" />
												</td>
											</s:if>
										</tr>
									</s:iterator>
								</table>
							</td>
						</tr>
						<tr>
							<td height="4"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

		<div id="deauthPanel" style="display: none;">
			<div class="hd">
				Deauth Client
			</div>
			<div class="bd">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
							<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<td style="padding: 6px 5px 5px 5px;">
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
											<tr>
												<td class="labelT1" colspan="2">
													<s:text name="monitor.activeClient.deauth.clientMac" />
												</td>
												<td>
													<s:select id="deauthClientSelect" name="deauthClientSelect"
														list="deauthClientSelectList" listKey="key"
														listValue="value" cssStyle="width:152px;" />
												</td>
											</tr>
											<tr>
												<td class="labelT1" colspan="2">
													<s:checkbox name="isClearCache" id="isClearCache"
														onclick="selectClearCache(this.checked);" />
													<s:text name="monitor.activeClient.deauth.clearCache" />
												</td>
												<td>
													<s:select id="deauthCacheSelect" name="deauthCacheSelect"
														list="deauthCacheSelectList" listKey="key"
														listValue="value" cssStyle="width:152px;" disabled="true" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding-top: 8px;">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="button" name="ignore" value="OK" id="deauthBtn"
											class="button" onClick="submitAction('deauthClient');">
									</td>
									<td>
										<input type="button" name="ignore" value="Cancel"
											class="button" onClick="hideDeauthOverlay();">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<div id="refreshSettingPanel" style="display: none;">
			<div class="hd">
				Periodic Client Database Updates
			</div>
			<div class="bd">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td colspan="2">
							<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
								width="100%">
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td style="padding-left: 15px;">
										<s:radio label="Gender" id="" name="clientRefreshFlag"
											list="#{'disableClientRefresh':'Disable periodic updates of the client database'}"
											onclick="selectDisableClientRefresh(this.checked);"
											value="%{clientRefreshFlag}" />
									</td>
								</tr>
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td style="padding-left: 15px;">
										<s:radio label="Gender" id="" name="clientRefreshFlag"
											list="#{'enableClientRefresh':'Enable periodic updates of the client database'}"
											onclick="selectEnableClientRefresh(this.checked);"
											value="%{clientRefreshFlag}" />
									</td>
								</tr>
								<tr>
									<td height="5"></td>
								</tr>
								<tr>
									<td style="padding-left: 40px;">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td width="160px">
													<label>
														<s:text name="admin.activeClientConfig.refreshInterval" />
													</label>
												</td>
												<td>
													<s:textfield id="clientRefreshInterval"
														name="clientRefreshInterval"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="4" cssStyle="width: 95px;"
														disabled="%{disableClientRefresh}" />
													<s:text name="admin.activeClientConfig.intervalRange" />
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td width="160px">
													<label>
														<s:text name="admin.management.clientRefreshFilter" />
													</label>
												</td>
												<td>
													<s:select id="clientRefreshFilter"
														name="clientRefreshFilter" headerKey="-2"
														headerValue="All" list="clientRefreshFilterList"
														listKey="id" listValue="value" cssStyle="width:100px;"
														disabled="%{disableClientRefresh}" />
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="padding-top: 8px;" colspan="2">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="button" name="ignore" value="Save" id="ignore"
											class="button" onClick="updateRefreshSetting();">
									</td>
									<td>
										<input type="button" name="ignore" value="Cancel"
											class="button" onClick="hideRefreshSetPanel();">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</div>

	</s:form>
</div>

<div id="multiEditPanel" style="display:none">
	<div class="hd">
		Modify Client
	</div>
	<div class="bd">
		<s:form action="clientMonitor" id="multiEditForm" name="multiEditForm">
			<s:hidden name="operation" />
			<s:hidden name="selectAll" />
			<s:hidden name="selectedClientIDStr" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td style="padding: 6px 5px 5px 10px;">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td style="padding:10px 0px 10px 0" colspan="2"
												class="noteInfo">
												<s:text name="monitor.client.modifyNote" />
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="120px">
												<s:checkbox name="flagEditIP" id="flagEditIP"
													onclick="selectEditClietIP(this.checked);" />
												<s:text name="monitor.client.clientIP" />
											</td>
											<td>
												<s:textfield name="editIP" id="editIP" maxlength="32"
													size="20" disabled="true"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditHostName" id="flagEditHostName"
													onclick="selectEditHostName(this.checked);" />
												<s:text name="monitor.client.clientHostname" />
											</td>
											<td>
												<s:textfield name="editHostName" id="editHostName"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditUserName" id="flagEditUserName"
													onclick="selectEditUserName(this.checked);" />
												<s:text name="monitor.client.clientUserName" />
											</td>
											<td>
												<s:textfield name="editUserName" id="editUserName"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditComment1" id="flagEditComment1"
													onclick="selectEditComment1(this.checked);" />
												<s:text name="monitor.client.comment1" />
											</td>
											<td>
												<s:textfield name="editComment1" id="editComment1"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:checkbox name="flagEditComment2" id="flagEditComment2"
													onclick="selectEditComment2(this.checked);" />
												<s:text name="monitor.client.comment2" />
											</td>
											<td>
												<s:textfield name="editComment2" id="editComment2"
													maxlength="32" size="20" disabled="true" />
											</td>
										</tr>
										<tr>
											<td height="4px"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top:8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button" name="ignore" value="Save" class="button"
										onClick="saveEditResults();">
								</td>
								<td>
									<input type="button" name="ignore" value="Cancel"
										class="button"
										onClick="multiEditPanel.cfg.setProperty('visible', false);">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>
<div id="debugClientPanel" style="display: none;">
	<div class="hd">
		<s:text name="topology.menu.troubleshoot.clientTrace" />
	</div>
	<div class="bd">
		<iframe id="debug_client" name="debug_client" width="0" height="0"
			frameborder="0" style="background-color: #999;" src="">
		</iframe>
	</div>
</div>
<div id="cliInfoPanel" style="display: none;">
	<div class="hd" id="cliInfoTitle"><s:text name="monitor.activeClient.operation.enroll"/></div>
	<div class="bd">
		<div id="bd_top" class="bd_top">
		</div>
		<div id="cli_viewer" class="cli_viewer"></div>
	</div>
	<div class="ft"></div>
</div>
<div id="editOSVersionPanel" style="display:none">
	<div class="hd">
		<s:text name="config.textfile.title.osObject.collect" />
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
											<td class="labelT1">
											<s:text name="config.textfile.osObject.osType" /><font color="red"><s:text name="*" /></font>
											</td>
											<td>
												<s:textfield name="dataSource_os_type" size="32"/>
											</td>
											<td>
												<s:text name="config.ipFilter.name.range" />
											</td>
										</tr>
										<tr>
											<td class="labelT1">
												<s:text name="config.textfile.osObject.option55" />
											</td>
											<td>
												<s:textfield name="dataSource_os_option55" size="32" readonly="true"/>
											</td>
											<td>
												<s:text name="config.osObject.option55.range" />
											</td>
										</tr>
										<tr style="display:<s:property value='%{displayInHomeDomain}'/>">
											<td class="labelT1" colspan="3">
												<s:checkbox name="dataSource_append_marking" value="true"/>
												<s:text name="config.textfile.osObject.append" />
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="commit" value="<s:text name="config.usb.modem.selectFile.submit" />"
								class="button" onClick="submitAction('updateOSVersion');" <s:property value="writeDisabled" />>
							</td>
							<td><input type="button" name="ignore" value="<s:text name="config.usb.modem.selectFile.cancel" />"
								class="button" onClick="hideEditOSVersionPanel();">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div id="clientTsInfoPannel"></div>
</div>