<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveApUpdateSettings"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript" src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
<!--
    #scriptPanel .bd {
        overflow:auto;
        height:35em;
        background-color:#fff;
        padding:10px;
    }
    #scriptPanel .ft {
        height:15px;
        padding:0;
    }
    #scriptPanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }
	#calendarpicker button, #calendarpicker2 button {
	    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
	    *margin: 2px 0; /* For IE */
	    *height: 1.5em; /* For IE */
	}
	
	#configOptions{
		height: 0px; 
		width: 100%;
		position: absolute; 
		padding: 0 10px 3px;
		top: -6px; 
		left: -10px; 
		background: #FFFFFF; 
		border-bottom: 1px solid #999;
		overflow: hidden;
	}
	
	.wzdToolBar{
		margin: 1px; 
		padding: 2px 10px 0; 
		text-align: right; 
/* 		background-color: #EDF5FF;  */
	}
	.wzdToolBar a{
		text-decoration: none;
		color: #003366;
		margin-right: 5px;
		font-weight: bold;
	}
	.wzdToolBar a span{
		font-size: 12px;
		color: #99CCCC;
	}
	a#saveOption:hover img, a#exitOption:hover img{
		filter: alpha(opacity="50");
		opacity: 0.5;
	}
	.wzdToolBar a:hover span{
		 color: #003366;
	}
-->
</style>
<script>
var formName = 'hiveApUpdate';
var thisOperaton;

var ACTIVATE_TYPE_AT = '<%=HiveApUpdateSettings.ActivateType.activateAtTime.toString()%>';
var ACTIVATE_TYPE_AFTER = '<%=HiveApUpdateSettings.ActivateType.activateAfterTime.toString()%>';
var ACTIVATE_TYPE_NEXT = '<%=HiveApUpdateSettings.ActivateType.activateNextTime.toString()%>';

var CONFIG_SELECT_TYPE_FULL = '<%=HiveApUpdateSettings.ConfigSelectType.full.toString()%>';
var CONFIG_SELECT_TYPE_DELTA_C = '<%=HiveApUpdateSettings.ConfigSelectType.deltaConfig.toString()%>';
var CONFIG_SELECT_TYPE_DELTA_R = '<%=HiveApUpdateSettings.ConfigSelectType.deltaRunning.toString()%>';
var CONFIG_SELECT_TYPE_AUTO = '<%=HiveApUpdateSettings.ConfigSelectType.auto.toString()%>';

function submitAction(operation) {
	if (validate(operation)) {
		// hide upload warning message
		if(operation == 'uploadWizard'){
			if(Get('guidedWarning')) {
				hm.util.hide('guidedWarning');
			}
			thisOperaton=operation;
			checkWiFiClientMode();
		} else {
			showProcessing();
			document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
	    }
	}
}

function checkWiFiClientMode(){
	if(waitingPanel != null){
		waitingPanel.show();
	}
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'checkWiFiClientMode';
	YAHOO.util.Connect.setForm(document.getElementById("hiveApUpdate"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succCheckWiFiClientMode, failure : resultDoNothing, timeout: 240000}, null);
}

function succCheckWiFiClientMode(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var details = " + o.responseText);
	if (details.t){	
		var wfcmConfirmDialog =
		    new YAHOO.widget.SimpleDialog("wfcmConfirmDialog",
		             { width: "350px",
		               fixedcenter: true,
		               visible: false,
		               draggable: true,
		               modal:true,
		               close: true,
		               icon: YAHOO.widget.SimpleDialog.ICON_WARN,
		               constraintoviewport: true,
		               buttons: [ { text:"Yes", handler:handleWFCMYes, isDefault:true },
		                          { text:"&nbsp;No&nbsp;", handler:handleNo } ]
		             } );
		wfcmConfirmDialog.setHeader("Confirm");
		wfcmConfirmDialog.cfg.setProperty('text', "<html><body>" + details.msg + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		wfcmConfirmDialog.render(document.body);
		wfcmConfirmDialog.show();
	} else {
		checkNetworkPolicy();
	}
}

function handleWFCMYes(){
	checkNetworkPolicy();
}

function doContinueOper() {
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function checkNetworkPolicy(){
	if(waitingPanel != null){
		waitingPanel.show();
	}
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'checkNetworkPolicy';
	YAHOO.util.Connect.setForm(document.getElementById("hiveApUpdate"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succCheckNetworkPolicy, failure : resultDoNothing, timeout: 240000}, null);
}

var succCheckNetworkPolicy=function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	
	eval("var details = " + o.responseText);
	if (details.t){	
		if (details.wn) {
			thisOperation='uploadWizard';
			confirmDialog.cfg.setProperty('text', "<html><body>" + details.wn + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
			confirmDialog.show();
		} else {
			showProcessing();
			document.forms[formName].operation.value = 'uploadWizard';
			document.forms[formName].submit();
		}
	} else {
		hm.util.displayJsonErrorNote(details.errorMsg);
	}
}

var resultDoNothing=function(o){
	hm.util.displayJsonErrorNote("<s:text name='error.request.timeout'/>");
}

var scriptPanel = null;
var detailListMenu = null;
var waitingPanel = null;
var configOptionsAnim = null;
var configSpaceAnim = null;
var CONFIGS_HEIGHT = 340, SPACE_HEIGHT = 125, OPTIONS_HEIGHT = 120;
function onLoadPage(){
	//create Script View overlay;
	//createScriptPanel(); //create panel until first time used, to avoid odd vertical bar
	//create waiting panel;
	createWaitingPanel();
	//create Animation;
	createAnimation();
	//bind animation listener
	bindAnimationListener();
}

function bindAnimationListener(){
	<s:if test="%{writeDisabled != 'disabled'}">
	var showOptionEl = document.getElementById("showOption");
	showOptionEl.onclick = showOption;
	showOptionEl.style.visibility = 'visible';
	</s:if>
}

function createAnimation(){
	configOptionsAnim = new YAHOO.util.Anim('configOptions');
	configOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	configSpaceAnim = new YAHOO.util.Anim('configSpace');
}

function configOptionsExpand(){
	var b = isConfigOptionShowing()
	configOptionsAnim.stop();
	configSpaceAnim.stop();
	configOptionsAnim.attributes.height = { to: b?CONFIGS_HEIGHT:(CONFIGS_HEIGHT - OPTIONS_HEIGHT)};
	configOptionsAnim.animate();
	configSpaceAnim.attributes.height = { to: b?SPACE_HEIGHT:(SPACE_HEIGHT - OPTIONS_HEIGHT)};
	configSpaceAnim.duration = 0.1;
	configSpaceAnim.animate();
}

function configOptionsCollapse(){
	configOptionsAnim.stop();
	configSpaceAnim.stop();
	configOptionsAnim.attributes.height = { to: 0 };
	configOptionsAnim.animate();
	configSpaceAnim.attributes.height = { to: 0 };
	configSpaceAnim.duration = 0.1;
	configSpaceAnim.animate();
}

function showConfigOption(configType){
	configOptionsAnim.stop();
	configSpaceAnim.stop();
	var activateTimeTr = document.getElementById("activateTimeTr");
	var configOptionsDiv = document.getElementById("configOptions");
	var configSpaceDiv = document.getElementById("configSpace");
	switch(configType){
	case CONFIG_SELECT_TYPE_FULL:
	case CONFIG_SELECT_TYPE_AUTO:
		activateTimeTr.style.display = "";
		configOptionsDiv.style.height = CONFIGS_HEIGHT + "px";
		configSpaceDiv.style.height = SPACE_HEIGHT + "px";
		break;
	case CONFIG_SELECT_TYPE_DELTA_C:
	case CONFIG_SELECT_TYPE_DELTA_R:
		activateTimeTr.style.display = "none";
		configOptionsDiv.style.height = CONFIGS_HEIGHT - OPTIONS_HEIGHT + "px";
		configSpaceDiv.style.height = SPACE_HEIGHT - OPTIONS_HEIGHT + "px";
		break;
	}
}

function isConfigOptionShowing(){
	var options = document.forms[formName]['configSelectType'];
	for(var i=0; i<options.length; i++){
		if(options[i].checked && (options[i].value == CONFIG_SELECT_TYPE_AUTO || options[i].value == CONFIG_SELECT_TYPE_FULL)){
			return true;
		}
	}
	return false;
}

function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"240px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("Loading configuration...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams="none" />" />');
	waitingPanel.render(document.body);
}

function createScriptPanel() {
	var div = document.getElementById('scriptPanel');
	scriptPanel = new YAHOO.widget.Panel('scriptPanel', { width:"580px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	scriptPanel.render();
	div.style.display = "";

	// Create Resize instance, binding it to the 'scriptPanel' DIV
	var resize = new YAHOO.util.Resize('scriptPanel', {
	    handles: ['br'],
	    autoRatio: false,
	    minWidth: 300,
	    minHeight: 200,
	    status: true
	});

	// Setup resize handler to update the size of the Panel's body element
	// whenever the size of the 'resizablepanel' DIV changes
	resize.on('resize', resizeScirptPanel, scriptPanel, true);
}

function resizeScirptPanel(obj){
	// QUIRKS FLAG, FOR BOX MODEL
	var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

	// UNDERLAY/IFRAME SYNC REQUIRED
	var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

    var panelHeight = obj.height;
    var headerHeight = this.header.offsetHeight; // Content + Padding + Border
    var footerHeight = this.footer.offsetHeight; // Content + Padding + Border

    var bodyHeight = (panelHeight - headerHeight - footerHeight);
    var bodyContentHeight = (IE_QUIRKS) ? bodyHeight : bodyHeight - 20;

    YAHOO.util.Dom.setStyle(this.body, 'height', bodyContentHeight + 'px');

    if (IE_SYNC) {

        // Keep the underlay and iframe size in sync.

        // You could also set the width property, to achieve the
        // same results, if you wanted to keep the panel's internal
        // width property in sync with the DOM width.

        this.sizeUnderlay();

        // Syncing the iframe can be expensive. Disable iframe if you
        // don't need it.

        this.syncIframe();
    }
}

function createDetailListMenu(){
	detailListMenu = new YAHOO.widget.Menu("detailMenu", { fixedcenter: false });
    var items = [[{text: "<s:text name="hiveAp.update.configuration.menu.item1"/>"},
                  {text: "<s:text name="hiveAp.update.configuration.menu.item2"/>"}]];
    detailListMenu.addItems(items);
    detailListMenu.subscribe('click',onMenuItemClick);
    detailListMenu.render(document.body);
}

function validate(operation) {
	if(operation == 'uploadWizard'){
		if(!validateUploadConfigItem(operation)){
			return false;
		}
		if(!validateApSelection(operation)){
			return false;
		}
	}
	return true;
}

function validateApSelection(operation){
	var cbs = document.getElementsByName('selectedIds');
	var isSelected = false;
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			isSelected = true;
			break;
		}
	}
	if(!isSelected){
		var listElement = document.getElementById('checkAll');
		hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
		return false;
	}
	return true;
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	if (details.err) {
		hm.util.displayJsonErrorNote(details.err);
		if(waitingPanel != null){
			waitingPanel.hide();
		}
		return;
	}
	
	var contentDiv = document.getElementById("content_viewer");
	contentDiv.innerHTML = details.v.replace(/\n/g,"<br>");
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(scriptPanel == null){
		createScriptPanel();
	}
	if(details.t){
		scriptPanel.header.innerHTML = details.t;
	}
	if (scriptPanel != null) {
		scriptPanel.cfg.setProperty('visible', true);
	}
	//if user click delta script and old script file not exist, alert;
	if(details.exist){
		warnDialog.cfg.setProperty('text', "<s:text name='error.config.absentOldConfig'/>");
		warnDialog.show();
	}
}

var detailsFailed = function(o) {
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(scriptPanel == null){
		createScriptPanel();
	}
	if (scriptPanel != null) {
		var contentDiv = document.getElementById("content_viewer");
		contentDiv.innerHTML = "<s:text name='error.request.timeout'/>";
		scriptPanel.cfg.setProperty('visible', true);
	}
};
var callback = {
	success : detailsSuccess,
	failure : detailsFailed,
	timeout: 240000
};
function scriptDetails(id) {
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=viewScript&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function pskDetails(id) {
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=viewPsk&id=" + id + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function showMenu(src, apId){
	if(null == detailListMenu){
		createDetailListMenu();
	}
	var x = YAHOO.util.Dom.getX(src);
	var y = YAHOO.util.Dom.getY(src);
	detailListMenu.cfg.setProperty("xy",[x+60,y+5]);
	detailListMenu.apId = apId;
	detailListMenu.show();
}

function onMenuItemClick(p_sType, p_aArguments){
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	//menu item is disabled, do nothing.
	if(menuItem.cfg.getProperty("disabled") == true || detailListMenu.apId == undefined){
	//	alert("This menu item is disabled.");
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	if(text == "<s:text name="hiveAp.update.configuration.menu.item1"/>"){
		scriptDetails(detailListMenu.apId);
	}else if(text == "<s:text name="hiveAp.update.configuration.menu.item2"/>"){
		pskDetails(detailListMenu.apId);
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.configuration"/> </td>');
}
</script>

<script type="text/javascript">
function formatValue(value){
    var v=value;
    if(value.length==0)
       return v;
    if(parseInt(value)<=9)
       v="0"+value;
    return v;
}
YAHOO.util.Event.onDOMReady(function () {
        function onButtonClick2() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu2.setBody("&#32;");
            oCalendarMenu2.body.id = "calendarcontainer2";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu2.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu2.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu2.body.id,{navigator: true});
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {

                    oCalendarMenu2.show();

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
                    var endDate_doc = document.getElementById("configDate");
                    endDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu2.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick2);

        };
        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu2 = new YAHOO.widget.Overlay("calendarmenu");
        // Create a Button instance of type "menu"
        var startTimeButton2 = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker2",
                                            label: "",
                                            menu: oCalendarMenu2,
                                            container: "configDateTimeDiv" });
        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startTimeButton2.on("click", onButtonClick2);

        if(YAHOO.env.ua.ie == 6){
            document.getElementById("saveOption").innerHTML = "Save";
            document.getElementById("exitOption").innerHTML = "Cancel";
        }
	});

function showOption(){
	document.getElementById("showOption").style.display = "none";
	document.getElementById("saveOption").style.display = "";
	document.getElementById("exitOption").style.display = "";
	document.getElementById("updateBtn").disabled = true;
	configOptionsExpand();
}

function saveOption(){
	if(!validateConfigActivateTime() || !validateSaveConfigItem()){
		return;
	}
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'saveConfigOption';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success:saveConfigOptionsResult}, null);
}

function saveConfigOptionsResult(o){
	eval("var details = " + o.responseText);
	if(details.suc){
		exitOption();
		document.getElementById("activateTypeTd").innerHTML = details.time;
		document.getElementById("uploadTypeTd").innerHTML = details.type;
		document.getElementById("configItemDiv").innerHTML = details.items;
	}else{
		warnDialog.cfg.setProperty('text', '<s:text name="error.hiveap.config.option.save.failed" />');
		warnDialog.show();
	}
	document.getElementById("activeTypeLabelTr").style.visibility = details.show?"visible":"hidden";
}

function exitOption(){
	document.getElementById("showOption").style.display = "";
	document.getElementById("saveOption").style.display = "none";
	document.getElementById("exitOption").style.display = "none";
	document.getElementById("updateBtn").disabled = false;
	configOptionsCollapse();
}



function validateConfigActivateTime(){
	var activateAtElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_AT);
	var activateAfterElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_AFTER);
	var activateNextElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_NEXT);
	if(null == activateAtElement || null == activateAfterElement || null == activateNextElement){
		return true;
	}
	if(activateAfterElement.checked){
		var activateAfterElement_offTime = document.getElementById("configOffset");

		if(activateAfterElement_offTime.value.length == 0){
			hm.util.reportFieldError(activateAfterElement_offTime, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.configuration.offsetTime" /></s:param></s:text>');
			return false;
		}
		var message = hm.util.validateIntegerRange(activateAfterElement_offTime.value, '<s:text name="hiveAp.update.configuration.offsetTime" />',
		                                           <s:property value="0" />,
		                                           <s:property value="3600" />);
		if (message != null) {
			hm.util.reportFieldError(activateAfterElement_offTime, message);
			return false;
		}
	}
	return true;
}

function validateSaveConfigItem(){
	var configConfiguration = document.getElementById(formName+"_dataSource_configConfiguration");
	var configCwp = document.getElementById(formName+"_dataSource_configCwp");
	var configCertificate = document.getElementById(formName+"_dataSource_configCertificate");
	var configUserDatabase = document.getElementById(formName+"_dataSource_configUserDatabase");
	if(!configConfiguration.checked && !configCwp.checked && !configCertificate.checked && !configUserDatabase.checked){
		hm.util.reportFieldError(configConfiguration, '<s:text name="error.hiveap.config.item.select" />');
		return false;
	}
	return true;
}

function validateUploadConfigItem(){
	var configConfiguration = document.getElementById("_configConfiguration");
	var configCwp = document.getElementById("_configCwp");
	var configCertificate = document.getElementById("_configCertificate");
	var configUserDatabase = document.getElementById("_configUserDatabase");
	if(!configConfiguration.checked && !configCwp.checked && !configCertificate.checked && !configUserDatabase.checked){
		hm.util.reportFieldError(configConfiguration, '<s:text name="error.hiveap.config.item.select" />');
		return false;
	}
	return true;
}

function configActivateChange(activateValue){
	var activateAtElement_hour = document.getElementById("configHour");
	var activateAtElement_minute = document.getElementById("configMin");
	var activateAfterElement_offTime = document.getElementById("configOffset");

	switch(activateValue){
		case ACTIVATE_TYPE_AT:
		activateAtElement_hour.disabled= false;
		activateAtElement_minute.disabled = false;
		activateAfterElement_offTime.disabled = true;
		break;
		case ACTIVATE_TYPE_AFTER:
		activateAtElement_hour.disabled= true;
		activateAtElement_minute.disabled = true;
		activateAfterElement_offTime.disabled = false;
		break;
		case ACTIVATE_TYPE_NEXT:
		activateAtElement_hour.disabled= true;
		activateAtElement_minute.disabled = true;
		activateAfterElement_offTime.disabled = true;
		break;
	}
}

</script>

<div id="content"><s:form id="hiveApUpdate" action="hiveApUpdate">
	<s:hidden name="sessionToken" value="%{sessionTokenInfo}" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input id="updateBtn" type="button" name="ignore" value="Upload"
						class="button" onClick="submitAction('uploadWizard');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button" onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
						<s:property value="writeDisabled" />>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="650px">
				<tr>
					<td>
						<div class="wzdToolBar">
						<a id="showOption" href="#showOptions" style="visibility: hidden;"><s:text name="hiveAp.update.images.settings.label"/><span>&#9660;</span></a>
						<a id="saveOption" href="#saveOptions" onclick="saveOption();" style="display: none;">
							<img alt="Save" title="Save" src="<s:url value="/images/save.png" includeParams="none"/>" width="16" class="dinl"></a>
						<a id="exitOption" href="#exitOptions" onclick="exitOption();" style="display: none;">
							<img alt="Cancel" title="Cancel" src="<s:url value="/images/cancel.png" includeParams="none"/>" width="16" class="dinl"></a>
					</div>
					</td>
				</tr>
				<tr>
					<td style="padding: 4px 10px 10px 10px;" valign="top">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td>
									<div style="position: relative;">
										<div>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="100px" style="padding-left: 5px"><s:text name="hiveAp.update.configuration.uploadType" /></td>
																<td class="labelT1" id="uploadTypeTd"><s:property value="dataSource.uploadTypeHtmlString" escape="false" /></td>
															</tr>
															<tr id="activeTypeLabelTr" style="visibility: <s:property value="%{activeTimeLabelStyle}"/>">
																<td class="labelT1" style="padding-left: 5px"><s:text name="hiveAp.update.configuration.time.tag" /></td>
																<td class="labelT1" id="activateTypeTd"><s:property value="dataSource.configActivateTimeHtmlString" escape="false" /></td>
															</tr>
															<tr><td height="5px"></td></tr>
															<tr>
																<td colspan="2">
																	<div id="configItemDiv"><s:property value="dataSource.configItemHtmlString" escape="false" /></div>
																</td>
															</tr>
															<tr>
																<td colspan="2" class="noteInfo" style="padding-left: 20px"><s:text name="hiveAp.update.configuration.wizard.note"/></td>
															</tr>
															<tr><td height="0px"><div id="configSpace"></div></td></tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
										<div id="configOptions">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr><!-- update type section -->
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="5px"></td></tr>
															<tr>
																<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType1}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
															</tr>
															<tr>
																<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType2}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
															</tr>
															<tr>
																<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType3}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
															</tr>
															<tr>
																<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType4}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
															</tr>
															<tr><td height="5px"></td></tr>
														</table>
													</td>
												</tr>
												<tr><td height="5px"></td></tr>
												<tr id="activateTimeTr" style="display: <s:property value="%{activateTimeStyle}"/>"><!-- activate time section -->
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td>
															<fieldset><legend><s:text name="hiveAp.update.configuration.time.wizard.tag"/></legend>
															<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="2px"></td></tr>
															<tr>
																<td colspan="20"><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType1}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
															</tr>
															<tr>
																<td width="150px"></td>
																<td width="80px"><s:textfield id="configDate" name="configDate" readonly="true" size="10" maxlength="10" /></td>
																<td width="30px"><div id="configDateTimeDiv" ></div></td>
																<td><s:select id="configHour" name="configHour" list="ENUM_HOURS" listKey="key"
																						listValue="value" disabled="%{configActiveAtDisabled}" />
																	<s:select id="configMin" name="configMin" list="ENUM_MINUTES" listKey="key"
																						listValue="value" disabled="%{configActiveAtDisabled}" /></td>
															</tr>
															<tr><td height="10"></td></tr>
															<tr>
																<td><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType2}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
																<td colspan="3"><s:textfield id="configOffset" name="dataSource.configActivateOffset" disabled="%{configActiveAfterDisabled}" size="4"
																	maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																	<s:text name="hiveAp.update.configuration.activateAfterTip"/></td>
															</tr>
															<tr><td height="10"></td></tr>
															<tr>
																<td colspan="5"><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType3}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
															</tr>
															</table>
															</fieldset>
															</td>
														</tr>
														<tr><td height="10px"></td></tr>
														</table>
													</td>
												</tr>
												<tr><!-- configuration item section -->
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
														<tr><td height="5px"></td></tr>
														<tr>
															<td><s:checkbox name="dataSource.configConfiguration" /></td>
															<td><s:text name="hiveAp.update.configuration.item.configuration" /></td>
														</tr>
														<tr><td height="5px"></td></tr>
														<tr>
															<td ><s:checkbox name="dataSource.configCwp" /></td>
															<td><s:text name="hiveAp.update.configuration.item.cwp" /></td>
														</tr>
														<tr><td height="5px"></td></tr>
														<tr>
															<td><s:checkbox name="dataSource.configCertificate" /></td>
															<td><s:text name="hiveAp.update.configuration.item.certificate" /></td>
														</tr>
														<tr><td height="5px"></td></tr>
														<tr>
															<td><s:checkbox name="dataSource.configUserDatabase" /></td>
															<td><s:text name="hiveAp.update.configuration.item.credential" /></td>
														</tr>
														<tr><td height="5px"></td></tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</div>
								</td>
							</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr>
							<td>
								<table cellspacing="0" cellpadding="0" border="0" class="view" width="100%" id="listTable">
									<tr>
										<th align="left"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="softVer" key="monitor.hiveAp.sw" /></th>
										<th align="left"><ah:sort name="lastCfgTime" key="hiveAp.update.time" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck"><ah:checkItem /></td>
											<td class="list"><a onclick="showMenu( this, '<s:property value="%{id}" />')" href="javascript: void 0;"><s:property value="hostName" /></a></td>
											<td class="list"><s:property value="macAddress" />&nbsp;</td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
											<td class="list"><s:property value="lastConfigurationTimeString" /></td>
										</tr>
									</s:iterator>
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
<div id="scriptPanel" style="display: none;">
<div class="hd">Configuration Details</div>
<div class="bd" id="content_viewer">
</div>
<div class="ft"></div>
</div>