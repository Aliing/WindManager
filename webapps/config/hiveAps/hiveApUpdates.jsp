<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveApUpdateSettings"%>
<%@page import="com.ah.be.config.hiveap.UpdateParameters"%>
<script type="text/javascript" src="<s:url value="/js/doT.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript" src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
<!--
	#calendarpicker button, #calendarpicker2 button {
	    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
	    *margin: 2px 0; /* For IE */
	    *height: 1.5em; /* For IE */
	}

	td.tftp			 { padding-bottom: 0;}
	td.tftp a        { text-decoration: none;}
	td.tftp a:link   { text-decoration: none;}
	td.tftp a:hover  { color: #CC3300; text-decoration: underline;}
	td.tftp a:active { text-decoration: none;}
	
	#imageOptions{
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
	
	.imgToolBar{
		margin: 1px; 
		padding: 2px 10px 0; 
		text-align: right; 
/* 		background-color: #EDF5FF;  */
	}
	.imgToolBar a{
		text-decoration: none;
		color: #003366;
		margin-right: 5px;
		font-weight: bold;
	}
	.imgToolBar a span{
		font-size: 12px;
		color: #99CCCC;
	}
	a#saveOption:hover img, a#exitOption:hover img{
		filter: alpha(opacity="50");
		opacity: 0.5;
	}
	.imgToolBar a:hover span{
		 color: #003366;
	}
-->
</style>

<script id="imageFileInfo" type=" type="text/x-dot-template">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr><td><s:text name="hiveAp.update.images.selected.file.info" /></td></tr>
		</table>
	</td>
</tr>
<tr>
	<td style="background: #f2f2f2; border: 1px solid #ddd;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td class="labelT1" width="170px"><s:text name="hiveAp.update.images.fileName" /></td>
				<td>{{=it.fn}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.update.images.version" /></td>
				<td>{{=it.ver}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.update.images.platform" /></td>
				<td>{{=it.pf}}</td>
			</tr>
			<tr>
				<td class="labelT1"><s:text name="hiveAp.update.images.size" /></td>
				<td>{{=it.sz}}</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</script>

<script id="imageVerInfo" type=" type="text/x-dot-template">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<tr><td><s:text name="hiveAp.update.images.selected.ver.info" /></td></tr>
		</table>
	</td>
</tr>
<tr>
	<td style="background: #f2f2f2; border: 1px solid #ddd; padding: 5px 2px;">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<th style="text-align: left;"><s:text name="hiveAp.update.images.version" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.update.images.fileName" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.update.images.platform" /></th>
				<th style="text-align: left;"><s:text name="hiveAp.update.images.size" /></th>
			</tr>
{{ for (var i = 0, l = it.length; i < l; i++) { }}
			<tr>
				{{? i == 0}}
				<td class="list" rowspan={{=it.length}}>{{=it[i].ver}}</td>
				{{?}}
				<td class="list">{{=it[i].fn}}</td>
				<td class="list">{{=it[i].pf}}</td>
				<td class="list">{{=it[i].sz}}</td>
			</tr>
{{ } }}
		</table>
	</td>
</tr>
</table>
</script>
<script type="text/javascript">
<!--
function requestImageInfo(){
	<s:if test="%{!dsEnable}">
	var fileStyle = document.getElementById("imageSelTypeFileTr").style.display;
	var verStyle = document.getElementById("imageSelTypeVerTr").style.display;
	var versionTypes = document.getElementsByName("imageVersionSelectType");
	var versionValue = "";
	for(var i=0; i<versionTypes.length; i++){
		if(versionTypes[i].checked){
			versionValue = versionTypes[i].value;
			break;
		}
	}
	if("" == verStyle){
		var selectedVersion;
		if("latest" == versionValue){
			selectedVersion = document.getElementById(formName + "_latestVersion").value;
		}else{
			selectedVersion = document.getElementById(formName + "_selectedVersion").value;
		}
		if(selectedVersion != NONE_ITEM){
			var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchImageInfo&selectedVersion="+encodeURIComponent(selectedVersion)+"&ignore="+ + new Date().getTime();
			ajaxRequest(null, url, imageCallback);
		}else{
			document.getElementById("imageVerInfoBox").style.display = "none";
		}
	}else if("" == fileStyle){
		var selectedImage = document.getElementById(formName + "_selectedImage").value;
		if(selectedImage != NONE_ITEM){
			var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchImageInfo&selectedImage="+encodeURIComponent(selectedImage)+"&ignore="+ + new Date().getTime();
			ajaxRequest(null, url, imageCallback);
		}else{
			document.getElementById("imageFileInfoBox").style.display = "none";
		}
	}
	</s:if>
}

function imageCallback(o){
	eval("var result = " + o.responseText);
	if(result.ver){
		document.getElementById("imageVerInfoBox").style.display = "";
	}else{
		document.getElementById("imageVerInfoBox").style.display = "none";
	}
	if(result.file){
		document.getElementById("imageFileInfoBox").style.display = "";
	}else{
		document.getElementById("imageFileInfoBox").style.display = "none";
	}
	if(result.ver){
		document.getElementById("imageVerInfoBox").innerHTML = doT.template(document.getElementById("imageVerInfo").innerHTML)(result.ver);
	}
	if(result.file){
		document.getElementById("imageFileInfoBox").innerHTML = doT.template(document.getElementById("imageFileInfo").innerHTML)(result.file);
	}
}

function selectVersionTypeChanged(type){
	var selectedVersion = document.getElementById(formName + "_selectedVersion");
	var latestVersion = document.getElementById(formName + "_latestVersion");
	if("latest" == type){
		selectedVersion.disabled = true;
	}else{
		selectedVersion.disabled = false;
	}
	if(selectedVersion.value != latestVersion.value){
		requestImageInfo();
	}
}

function imageFileSelectionChanged(){
	requestImageInfo();
}

function imageVerSelectionChanged(){
	requestImageInfo();
}

//-->
</script>
<script>
var formName = 'hiveApUpdate';
var NONE_ITEM = '<s:text name="config.optionsTransfer.none" />';
var operation;

var ACTIVATE_TYPE_AT = '<%=HiveApUpdateSettings.ActivateType.activateAtTime.toString()%>';
var ACTIVATE_TYPE_AFTER = '<%=HiveApUpdateSettings.ActivateType.activateAfterTime.toString()%>';
var ACTIVATE_TYPE_NEXT = '<%=HiveApUpdateSettings.ActivateType.activateNextTime.toString()%>';
var IMAGE_SELECTION_VER = '<%=HiveApUpdateSettings.ImageSelectionType.softVer.toString()%>';
var TRANSFER_TYPE_SCP = '<%=HiveApUpdateSettings.TransferType.scp.toString()%>';
var CONNECT_TYPE_128 = <%=HiveApUpdateSettings.CONNECT_TYPE_128K%>;
var CONNECT_TYPE_256 = <%=HiveApUpdateSettings.CONNECT_TYPE_256K%>;
var CONNECT_TYPE_1500 = <%=HiveApUpdateSettings.CONNECT_TYPE_1500K%>;
var CONNECT_TYPE_2000 = <%=HiveApUpdateSettings.CONNECT_TYPE_2000K%>;
var CONNECT_TYPE_LOCAL = <%=HiveApUpdateSettings.CONNECT_TYPE_LOCAL%>;
var MAX_UPLOAD_HIVEAP_COUNTS = <s:property value="%{maxUploadNum}"/>
var isUploadImageAllBR100 = <s:property value="%{uploadImageAllBr100}"/>
var isDsEnable = <s:property value="%{dsEnable}"/>

function submitAction(opt) {
	operation = opt;
	if (validate(operation)) {
		//if(operation == "uploadImage"){
		//	getUploadWaitingMsg();
		//}else{
			showProcessing();
			document.forms[formName].operation.value = operation;
			document.forms[formName].submit();
		//}
	}
}

function doContinueOper(){
	showProcessing();
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

//function getUploadWaitingMsg(){
//	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=uploadMsg&ignore=" + new Date().getTime();
//	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:getUploadMsgResult, failure:getUploadMsgFailed, timeout:60000}, null);
//}

//var getUploadMsgResult = function(o){
//	eval("var result = " + o.responseText);
//	var msg = result.message;
//	if(msg != null && msg != ""){
//		confirmDialog.cfg.setProperty('text', msg);
//		confirmDialog.show();
//	}else{
//		doContinueOper();
//	}
//};

//var getUploadMsgFailed = function(o){
//
//};

var waitingPanel = null;
var imageOptionsAnim = null;
var imageSpaceAnim = null;
function onLoadPage(){
	requestImageInfo();
	//create waiting panel;
	createWaitingPanel();
	//create Animation;
	createAnimation();
	//bind animation listener
	bindAnimationListener();
	// show the unsupported image update Aps Message
	showUnsupportedApsMessage();
	
}
function showUnsupportedApsMessage(){
	var unsupportedApsMessage  = "<s:property value='%{unsupportedApsMessage}' />" ;
	if(unsupportedApsMessage){
		showInfoDialog(unsupportedApsMessage);
	}
}

function bindAnimationListener(){
	<s:if test="%{writeDisabled != 'disabled'}">
	var showOptionEl = document.getElementById("showOption");
	showOptionEl.onclick = showOption;
	showOptionEl.style.visibility = 'visible';
	</s:if>
}

function createAnimation(){
	imageOptionsAnim = new YAHOO.util.Anim('imageOptions');
	imageOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	imageSpaceAnim = new YAHOO.util.Anim('imageSpace');
}

function imageOptionsExpand(){
	imageOptionsAnim.stop();
	imageSpaceAnim.stop();
	<s:if test="%{dsEnable}">
		imageOptionsAnim.attributes.height = { to: 320 };
		imageSpaceAnim.attributes.height = { to: 75 };
	</s:if>
	<s:else>
		imageOptionsAnim.attributes.height = { to: 360 };
		imageSpaceAnim.attributes.height = { to: 80 };
	</s:else>
	imageOptionsAnim.animate();
	imageSpaceAnim.duration = 0.1;
	imageSpaceAnim.animate();
}

function imageOptionsCollapse(){
	imageOptionsAnim.stop();
	imageSpaceAnim.stop();
	imageOptionsAnim.attributes.height = { to: 0 };
	imageOptionsAnim.animate();
	imageSpaceAnim.attributes.height = { to: 0 };
	imageSpaceAnim.duration = 0.1;
	imageSpaceAnim.animate();
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

function validate(operation) {
	if(!validateImageSelection(operation)){
		return false;
	}
	if(!validateApSelection(operation)){
		return false;
	}
	return true;
}

function validateApSelection(operation){
	if(operation == 'uploadImage'){
		var cbs = document.getElementsByName('selectedIds');
		var isSelected = false;
		var selectCount = 0;
		for (var i = 0; i < cbs.length; i++) {
			if (cbs[i].checked) {
				isSelected = true;
				selectCount++;
				//break;
			}
		}
		
		var listElement = document.getElementById('checkAll');
		if(!isSelected){
			hm.util.reportFieldError(listElement, '<s:text name="info.selectObject"></s:text>');
			return false;
		}
		if(selectCount > MAX_UPLOAD_HIVEAP_COUNTS){
			hm.util.reportFieldError(listElement, '<s:text name="error.hiveAp.update.image.maxCount"><s:param>'+MAX_UPLOAD_HIVEAP_COUNTS+'</s:param></s:text>');
			return false;
		}
	}
	return true;
}

function validateImageSelection(operation){
	if(operation == 'uploadImage'){
		var fileStyle = document.getElementById("imageSelTypeFileTr").style.display;
		if(fileStyle == ""){
			var selectedImage = document.getElementById(formName+"_selectedImage");
			var selectedValue = selectedImage.options[selectedImage.selectedIndex].value;
			if(!selectedValue.match('.img')){
				hm.util.reportFieldError(selectedImage, '<s:text name="error.pleaseSelect"><s:param><s:text name="hiveAp.update.images.label" /></s:param></s:text>');
				return false;
			}
		}else{
			var selectedImage = document.getElementById(formName+"_selectedVersion");
			var selectedValue = selectedImage.options[selectedImage.selectedIndex].value;
			if(selectedValue == '<s:text name="config.optionsTransfer.none" />'){
				hm.util.reportFieldError(selectedImage, '<s:text name="error.pleaseSelect"><s:param><s:text name="hiveAp.update.images.version.label" /></s:param></s:text>');
				return false;
			}
		}
	}
	return true;
}

function enableTftp(){
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?operation=enableTftp&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success:enableTftpResult, failure:enableTftpFailed, timeout:60000}, null);
	if(waitingPanel != null){
		waitingPanel.show();
	}
}

function enableTftpResult(o){
	eval("var result = " + o.responseText);
	if(waitingPanel != null){
		waitingPanel.hide();
	}
	if(result.suc){
		var tftpEl = document.getElementById(formName + "_imageTransfertftp");
		var tftpTd = document.getElementById("tftpTd");
		tftpEl.disabled = false;
		tftpEl.checked = true;
		tftpTd.style.visibility = "hidden";
	}else{
		warnDialog.cfg.setProperty('text', "Enable TFTP Service failed.");
		warnDialog.show();
	}
}

function enableTftpFailed(o){
	if(waitingPanel != null){
		waitingPanel.hide();
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="hiveApUpdate" includeParams="none"/>?operation=<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	document.writeln('<s:text name="hiveAp.update.image"/> </td>');
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
                    var endDate_doc = document.getElementById("imageDate");
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
                                            container: "imageDateTimeDiv" });
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
	// fix select component cannot be mask issue in IE6
	if(YAHOO.env.ua.ie == 6){
		showOption.showSelector = document.getElementById("imageSelTypeFileTr").style.display 
			== "none"? document.getElementById("imageSelTypeVerTr"):document.getElementById("imageSelTypeFileTr");
		showOption.showSelector.style.visibility ="hidden";
	}
	imageOptionsExpand();
}

function saveOption(){
	if(!validateImageActivateTime() || !validateImageTimedout()){
		return;
	}
	url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'saveImageOption';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success:saveImageOptionsResult}, null);
}

function saveImageOptionsResult(o){
	eval("var details = " + o.responseText);
	if(details.suc){
		exitOption();
		if(!isUploadImageAllBR100){
			document.getElementById("activateTypeTd").innerHTML = details.time;
		}
		<%--<s:if test="%{!dsEnable}">
			document.getElementById("transferProtocolTd").innerHTML = details.protocol;
		</s:if>//no such element on dom --%>
		var connectionTypeTd = document.getElementById("connectionTypeTd");
		if(connectionTypeTd){
			connectionTypeTd.innerHTML = details.conn;
		}
		document.getElementById("timedoutTd").innerHTML = details.timedout;
		if(IMAGE_SELECTION_VER == details.select){
			document.getElementById("imageSelTypeFileTr").style.display = "none";
			document.getElementById("imageSelTypeVerTr").style.display = "";
		}else{
			document.getElementById("imageSelTypeFileTr").style.display = "";
			document.getElementById("imageSelTypeVerTr").style.display = "none";
		}
		if(details.distributed){
			document.getElementById("enableDistributed").style.display = "";
		}else{
			document.getElementById("enableDistributed").style.display = "none";
		}
		requestImageInfo();
	}else{
		warnDialog.cfg.setProperty('text', '<s:text name="error.hiveap.image.option.save.failed" />');
		warnDialog.show();
	}
}

function exitOption(){
	document.getElementById("showOption").style.display = "";
	document.getElementById("saveOption").style.display = "none";
	document.getElementById("exitOption").style.display = "none";
	document.getElementById("updateBtn").disabled = false;
	// fix select component cannot be mask issue in IE6
	if(YAHOO.env.ua.ie == 6 && showOption.showSelector){
		showOption.showSelector.style.visibility ="visible";
	}
	imageOptionsCollapse();
}

function validateImageActivateTime(){
	var activateAtElement = document.getElementById(formName+"_imageActivateType" + ACTIVATE_TYPE_AT);
	var activateAfterElement = document.getElementById(formName+"_imageActivateType" + ACTIVATE_TYPE_AFTER);
	var activateNextElement = document.getElementById(formName+"_imageActivateType" + ACTIVATE_TYPE_NEXT);
	if(null == activateAtElement || null == activateAfterElement || null == activateNextElement){
		return true;
	}
	if(activateAfterElement.checked){
		var activateAfterElement_offTime = document.getElementById("imageOffset");

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

function validateImageTimedout(){
	var timedoutEl = document.getElementById(formName + "_dataSource_imageTimedout");
	
	if(timedoutEl.value.length == 0){
		hm.util.reportFieldError(timedoutEl, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.images.timeout" /></s:param></s:text>');
		return false;
	}
	var message = hm.util.validateIntegerRange(timedoutEl.value, '<s:text name="hiveAp.update.images.timeout" />',
	                                           <s:property value="15" />,
	                                           <s:property value="720" />);
	if (message != null) {
		hm.util.reportFieldError(timedoutEl, message);
		return false;
	}
	return true;
}

function connectTypeChange(connectType){
	var timedoutEl = document.getElementById(formName + "_dataSource_imageTimedout");
	switch(parseInt(connectType)){
	case CONNECT_TYPE_128:
	case CONNECT_TYPE_256:
		timedoutEl.value = 30;
		break;
	case CONNECT_TYPE_1500:
	case CONNECT_TYPE_2000:
	case CONNECT_TYPE_LOCAL:
		timedoutEl.value = 15;
		break;
	}
}

function imageActivateChange(activateValue){
	var activateAtElement_hour = document.getElementById("imageHour");
	var activateAtElement_minute = document.getElementById("imageMin");
	var activateAfterElement_offTime = document.getElementById("imageOffset");

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
function trasferChanged(trasferType){
	var connectTypeEl = document.getElementById(formName + "_dataSource_imageConnType");
	if(TRANSFER_TYPE_SCP == trasferType){
		connectTypeEl.disabled = false;
	}else{
		connectTypeEl.value = CONNECT_TYPE_LOCAL;
		connectTypeEl.disabled = true;
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
						class="button" onClick="submitAction('uploadImage');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="cancel" value="Cancel"
						class="button"
						onClick="submitAction('<%=Navigation.L2_FEATURE_MANAGED_HIVE_APS%>');"
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
			<table  class="editBox" cellspacing="0" cellpadding="0" border="0" width="700px">
				<tr>
					<td>
						<div class="imgToolBar">
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
									<div id="imageListDiv" style="position: relative;">
										<div>
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr><td height="5px"></td></tr>
															
															<s:if test="%{imageUpgradeNote != null}">
															<tr>
																<td class="noteInfo" colspan="10">Note: <s:property value="imageUpgradeNote"/></td>
															</tr>
															<tr><td height="5px"></td></tr>
															</s:if>
															<tr id="imageSelTypeFileTr" style="display: <s:property value="imageSelTypeFileStyle" />">
																<td class="labelT1" width="180px"><s:text
																	name="hiveAp.update.images.label" /></td>
																<td nowrap="nowrap"><s:select name="selectedImage" list="%{availableImageFiles}" listKey="key" listValue="value" onchange="imageFileSelectionChanged();" cssStyle="width: 250px;" />
																<s:if test="%{isInHomeDomain && !dsEnable}">
																<input type="button" name="importImage1" value="Add/Remove" style="width:100px"
																class="button" onClick='openUploadFilePanel("Add/Remove <s:text name='hiveAp.autoProvisioning.imageName.label'/>", "newImageFile");'></s:if></td>
															</tr>
															<tr id="imageSelTypeVerTr" style="display: <s:property value="imageSelTypeVerStyle" />">
																<td>
																	<table width="100%" cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td width="1px"></td><td class="labelT1" style="padding-left: 2px;"><s:radio onchange="selectVersionTypeChanged(this.value);" label="Gender" name="imageVersionSelectType" list="%{imageVersionSelectType1}" listKey="key" listValue="value"/>
																			&nbsp;&nbsp;&nbsp;<s:hidden name="latestVersion" value="%{latestImageVersion}" /><span id="selectVersionLatestLabel"><s:property value="%{latestImageVersion}" /></span></td>
																			<td rowspan="2">
																				<s:if test="%{isInHomeDomain && !dsEnable}">
																				<input type="button" name="importImage2" value="Add/Remove" style="width:100px"
																				class="button" onClick='openUploadFilePanel("Add/Remove <s:text name='hiveAp.autoProvisioning.imageName.label'/>", "newImageFile");'></s:if>
																			</td>
																		</tr>
																		<tr>
																			<td></td><td class="labelT1" style="padding-left: 2px;"><s:radio onchange="selectVersionTypeChanged(this.value);" label="Gender" name="imageVersionSelectType" list="%{imageVersionSelectType2}" listKey="key" listValue="value"/>
																			&nbsp;&nbsp;&nbsp;<s:select onchange="imageVerSelectionChanged();" name="selectedVersion" list="%{availableImageVersions}" cssStyle="width: 200px;" disabled="%{otherImageVersionDisabled}" />
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr><td height="5px"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr><td id="imageVerInfoBox" style="display: none; padding-left: 10px; padding-right: 10px;"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr><td id="imageFileInfoBox" style="display: none; padding-left: 10px; padding-right: 10px;"></td></tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="labelT1" width="170px"><s:text name="hiveAp.update.configuration.time.tag" /></td>
																<s:if test="%{uploadImageAllBr100}">
																	<td class="labelT1" id="activateTypeTd"><s:property value="dataSource.br100ImageActivateTimeHtmlString" escape="false" /></td>
																</s:if>
																<s:else>
																	<td class="labelT1" id="activateTypeTd"><s:property value="dataSource.imageActivateTimeHtmlString" escape="false" /></td>
																</s:else>
															</tr>
															<s:if test="%{!dsEnable}">
																<tr>
																	<td class="labelT1"><s:text name="hiveAp.update.images.connect.type" /></td>
																	<td class="labelT1" id="connectionTypeTd"><s:property value="dataSource.connectionString" /></td>
																</tr>
															</s:if>
															<tr>
																<td class="labelT1"><s:text name="hiveAp.update.images.timeout" /></td>
																<td class="labelT1" id="timedoutTd"><s:property value="dataSource.timedoutString" /></td>
															</tr>
															<tr id="enableDistributed" style="display: <s:property value="distributedUpgradesStyle" />">
																<td class="labelT1"><s:text name="hiveAp.update.images.distributedUpgrades.server"/></td>
																<td>
																	<table>
																		<tr>
																			<td class="labelT1" id="distributedServer" style="width:100px" ><label value="11" id="disServer"><s:property value="distributedServerHost" /></label></td>
																			<td width="40px"></td>
																			<td><input type="button" class="button" name="ignore" style="width:100px" value="Change server" onClick="openDistributedServerList();"></td>
																			<td id="mastServerList" style="display:none;">
																				<s:select name="distributedServer" value="%{distributedServer}"  id="distributedServerList" onchange="distributedServerChange(this);"
																					list="distributedServers" listKey="id" listValue="value" cssStyle="width:100px;"/>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr><td height="0px"><div id="imageSpace"></div></td></tr>
														</table>
													</td>
												</tr>
											</table>
										</div>
										<div id="imageOptions">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr><!-- update type section -->
													<td>
														<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="5px"></td></tr>
															<tr>
																<td width="280px"><s:radio label="Gender" name="imageSelectType" value="%{dataSource.imageSelectType}" list="%{imageSelectType1}" listKey="key" listValue="value"/></td>
																<td><s:radio label="Gender"	name="imageSelectType" value="%{dataSource.imageSelectType}" list="%{imageSelectType2}" listKey="key" listValue="value"/></td>
															</tr>
															<tr><td height="10px"></td></tr>
														</table>
													</td>
												</tr>
												<tr><!-- activate time section -->
													<td>
														<fieldset><legend><s:text name="hiveAp.update.configuration.time.tag"/></legend>
														<table cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td height="5px"/>
														</tr>
														<tr>
															<td colspan="20" class="noteInfo" style="padding-left: 6px;"><s:text name="hiveAp.update.images.br100.note" /></td>
														</tr>
														<tr>
															<td colspan="20"><s:radio label="Gender" name="imageActivateType" value="%{dataSource.imageActivateType}" list="%{imageActivateType1}" listKey="key" listValue="value" onclick="imageActivateChange(this.value);"/></td>
														</tr>
														<tr>
															<td width="150px"></td>
															<td width="80px"><s:textfield id="imageDate" name="imageDate" readonly="true" size="10" maxlength="10" /></td>
															<td width="30px"><div id="imageDateTimeDiv" ></div></td>
															<td><s:select id="imageHour" name="imageHour" list="ENUM_HOURS" listKey="key"
																					listValue="value" disabled="%{imageActiveAtDisabled}" />
																<s:select id="imageMin" name="imageMin" list="ENUM_MINUTES" listKey="key"
																					listValue="value" disabled="%{imageActiveAtDisabled}" /></td>
														</tr>
														<tr><td height="10"></td></tr>
														<tr>
															<td><s:radio label="Gender" name="imageActivateType" value="%{dataSource.imageActivateType}" list="%{imageActivateType2}" listKey="key" listValue="value" onclick="imageActivateChange(this.value);"/></td>
															<td colspan="3"><s:textfield id="imageOffset" name="dataSource.imageActivateOffset" disabled="%{imageActiveAfterDisabled}" size="4"
																maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
																<s:text name="hiveAp.update.configuration.activateAfterTip"/></td>
														</tr>
														<tr><td height="10"></td></tr>
														<tr>
															<td colspan="5"><s:radio label="Gender" name="imageActivateType" value="%{dataSource.imageActivateType}" list="%{imageActivateType3}" listKey="key" listValue="value" onclick="imageActivateChange(this.value);"/></td>
														</tr>
														</table>
														</fieldset>
													</td>
												</tr>
												<s:if test="%{dsEnable}">
													<tr><td>
													<table cellspacing="0" cellpadding="0" border="0">
														<tr><td height="5px" colspan="10"></td></tr>
														<tr>
															<td class="labelT1" style="padding-left: 5px; padding-right: 5px;"><s:text name="hiveAp.update.images.image.timeout" /></td>
															<td><s:textfield name="dataSource.imageTimedout" onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="4" size="4" />
																<s:text name="hiveAp.update.images.timeout.label" /></td>
														</tr>
													</table>
													</td></tr>
												</s:if>
												<s:else>
													<tr><td height="5px"></td></tr>
													<tr><!-- transfer protocol section -->
														<td>
															<fieldset><legend><s:text name="hiveAp.update.protocol.tag"/></legend>
															<table cellspacing="0" cellpadding="0" border="0">
															<tr><td height="10px"></td></tr>
															<tr>
																<td width="80px"><s:radio label="Gender" name="imageTransfer" value="%{dataSource.imageTransfer}" list="%{imageTransfer1}" listKey="key" listValue="value" onclick="trasferChanged(this.value);"/></td>
																<td><s:radio label="Gender" name="imageTransfer" value="%{dataSource.imageTransfer}" list="%{imageTransfer2}" listKey="key" listValue="value" onclick="trasferChanged(this.value);" disabled="%{!tftpBtnEnabled}"/></td>
																<s:if test="%{needShowEnableLabel}">
																	<td class="tftp" id="tftpTd" style="padding-top:3px;">&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:enableTftp();">[Enable]</a></td>
																</s:if>
																<s:else>
																	<td></td>
																</s:else>
																<td class="labelT1" style="padding-left: 85px; padding-right: 5px;"><s:text name="hiveAp.update.images.connect.type" /></td>
																<td><s:select name="dataSource.imageConnType" list="%{connectTypes}" listKey="key" listValue="value" onchange="connectTypeChange(this.value);" disabled="%{usingTftpTransfer}"/></td>
															</tr>
															<tr>
																<td></td><td></td><td></td>
																<td class="labelT1" style="padding-left: 85px; padding-right: 5px;"><s:text name="hiveAp.update.images.timeout" /></td>
																<td><s:textfield name="dataSource.imageTimedout" onkeypress="return hm.util.keyPressPermit(event,'ten');" maxlength="4" size="4" />
																	<s:text name="hiveAp.update.images.timeout.label" /></td>
															</tr>
															<tr>
																<td height="5px"></td>
															</tr>
															</table>
															</fieldset>
														</td>
													</tr>
													<tr><td height="5px"></td></tr>
													<tr>
														<td>
															<fieldset><legend>Distributed Upgrade</legend>
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td width="10px"><s:checkbox name="dataSource.distributedUpgrades" value="%{dataSource.distributedUpgrades}" /></td>
																		<td style="padding-top: 2px;"><s:text name="hiveAp.update.images.distributedUpgrades"/></td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
												</s:else>
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
										<th class="check"><input type="checkbox" id="checkAll"
											onClick="hm.util.toggleCheckAll(this);"></th>
										<th align="left"><ah:sort name="hostName" key="hiveAp.hostName" /></th>
										<th align="left"><ah:sort name="macAddress" key="hiveAp.macaddress" /></th>
										<th align="left"><ah:sort name="ipAddress" key="hiveAp.interface.ipAddress" /></th>
										<th align="left"><ah:sort name="productName" key="monitor.hiveAp.model" /></th>
										<th align="left"><ah:sort name="softVer" key="monitor.hiveAp.sw" /></th>
										<th align="left"><ah:sort name="runningHive" key="hm.config.start.network" /></th>
										<th align="left"><ah:sort name="lastImageTime" key="hiveAp.update.time" /></th>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<tiles:insertDefinition name="selectAll" />
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="listCheck" onClick="selectedImageClick();"><ah:checkItem /></td>
											<td class="list"><s:property value="hostName" /></td>
											<td class="list"><s:property value="macAddress" />&nbsp;</td>
											<td class="list"><s:property value="ipAddress" />&nbsp;</td>
											<td class="list"><s:property value="productName" />&nbsp;</td>
											<td class="list"><s:property value="displayVerNoBuild" />&nbsp;</td>
											<td class="list"><s:property value="runningHive" /></td>
											<td class="list"><s:property value="lastImageTimeString" /></td>
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
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" src="">
		</iframe>
	</div>
</div>
<script id="imageFileList" type=" type="text/x-dot-template">
{{ for (var i = 0, l = it.list.length; i < l; i++) { }}
	<option value="{{=it.list[i].fn}}" {{ if(it.list[i].fn == it.val) { }} selected="selected" {{ } }}>{{=it.list[i].fnDisp}}</option>
{{ } }}
</script>
<script id="imageVerList" type=" type="text/x-dot-template">
{{ for (var i = 0, l = it.list.length; i < l; i++) { }}
	<option value="{{=it.list[i].ver}}" {{ if(it.list[i].ver == it.val) { }} selected="selected" {{ } }}>{{=it.list[i].verDisp}}</option>
{{ } }}
</script>
<script type="text/javascript">
var uploadFilePanel = null;
function createUploadFilePanel(width, height, title){
	var div = document.getElementById("uploadFilePanel");
	var iframe = document.getElementById("uploadFileFrame");
	iframe.width = width;
	iframe.height = height;
	uploadFilePanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	uploadFilePanel.setHeader(title);
	uploadFilePanel.render();
	div.style.display="";
	uploadFilePanel.beforeHideEvent.subscribe(requestImageList);
}
function requestImageList(){
	var url = '<s:url action="hiveApUpdate" includeParams="none"></s:url>' + "?operation=fetchImageList&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, imageListCallback);
}

function imageListCallback(o){
	var selVer = document.getElementById(formName + "_selectedVersion").value;
	var selFile = document.getElementById(formName + "_selectedImage").value;
	eval("var result = " + o.responseText);
	if(result.files){
		swapInnerHTML(formName + "_selectedImage", doT.template(document.getElementById("imageFileList").innerHTML)({"list": result.files, "val": selFile}));
	}
	if(result.vers){
		swapInnerHTML(formName + "_selectedVersion", doT.template(document.getElementById("imageVerList").innerHTML)({"list": result.vers, "val": selVer}));
		//update latest version info
		document.getElementById(formName + "_latestVersion").value = result.vers[0].ver;
		document.getElementById("selectVersionLatestLabel").innerHTML = result.vers[0].verDisp;
	}
	requestImageInfo();
}

/* hack for across that works in both IE and other browsers*/
function swapInnerHTML(objID,newHTML) {
	var el=document.getElementById(objID);
	el.outerHTML=el.outerHTML.replace(el.innerHTML+'</select>',newHTML+'</select>');
}

function openUploadFilePanel(title, doOperation){
	if(null == uploadFilePanel){
		createUploadFilePanel(555,600,title);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("uploadFileFrame").style.display = "";
	}
	uploadFilePanel.show();
	var iframe = document.getElementById("uploadFileFrame");
	iframe.src ="<s:url value='hiveApFile.action' includeParams='none' />?operation="+doOperation;
}

function openDistributedServerList(){
	var disList = document.getElementById("mastServerList").style.display;
	if(disList == ""){
		document.getElementById("mastServerList").style.display = "none";
	}else{
		document.getElementById("mastServerList").style.display = "";
	}
}

function distributedServerChange(selected){
	var selectedServer = document.getElementById("mastServerList");
	document.getElementById("disServer").innerHTML = selected.options[selected.selectedIndex].text;
	selectedServer.style.display = "none";
	
	url = '<s:url action="hiveApUpdate" includeParams="none"/>' + "?operation=saveSelectedDistServer&distributedServer="+selected.value;
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success:nullFunction}, null);
}

function nullFunction(){

}

function selectedImageClick(){
	var fileStyle = document.getElementById("enableDistributed").style.display;
	if(fileStyle == ""){
		url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
		document.forms[formName].operation.value = 'distributedDownloadList';
		YAHOO.util.Connect.setForm(document.getElementById(formName));
		var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success:distributedDownloadResult}, null);
	}
}

function distributedDownloadResult(o){
	eval("var result = " + o.responseText);
	if(result.suc){
		document.getElementById("distributedServerList").value = result.disServerId;
		document.getElementById("disServer").innerHTML = result.disServerStr;
		
		var selectList = document.getElementById("distributedServerList");
		selectList.options.length = 0;
		var dl = result.disList;
		for(var i=0; i<result.disList.length; i++){
			selectList.options.add(new Option(dl[i].value, dl[i].key));
		}
		selectList.options[0].selected = true;
	}else if (result.sameHive) {
		return;
	}else {
		document.getElementById("disServer").innerHTML = "None";
		document.getElementById("distributedServerList").length = 0;
		var listTable = document.getElementById("checkAll");
		if(result.multiHive){
			hm.util.reportFieldError(listTable, '<s:text name="image.distributor.imageHive"></s:text>');
		}
		if(result.noneHive){
			hm.util.reportFieldError(listTable, '<s:text name="image.distributor.image.noHive"></s:text>');
		}
	}
}

var uploadFileIframeWindow;
</script>