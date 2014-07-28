<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
#title {
color: #dde2e8;
padding: 12px 30px 12px 20px;
background: #7F93A4;
}

#title td.title {
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 20px;
font-weight: bold;
line-height: 20px;
}

#title td.hive {
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 14px;
font-weight: bold;
line-height: 16px;
}

#foot {
	height: 5px;
	line-height: 5px;
}

.firstLevelSeparator {
height: 1px;
background-color: white;
}

div.actived {
	border-left: 5px solid #F5A433;
}

.firstLevelBar {
padding: 0px 28px 0px 28px;
line-height: 28px;
cursor: pointer;
border-left: 5px solid #D1CEC3;
background: #F8F8F8;
}

.firstLevelText {
color: #767676;
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 18px;
font-weight: bold;
}

.firstLevelSmallText {
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 14px;
font-weight: bold;
}

.secondLevelBar {
color: #767676;
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 15px;
font-weight: bold;
line-height: 18px;
padding: 2px 28px 2px 28px;
cursor: pointer;
}

.secondLevelText {
color: #767676;
font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
font-size: 15px;
font-weight: bold;
line-height: 20px;
padding-left: 35px;
}

.innerFrame {
scrolling: auto;
border-top-style:none;
border-bottom-style:none;
border-right-style:none;
border-left-style:none;
width:100%;
height:100%;
}

.frameDiv{
border-top-style:none;
border-bottom-style:none;
border-right-style:none;
border-left-style:none;
width:100%;
height:100%;
background: #fff;
}

td.button {
padding-right: 0px;
text-align: right;
width: 75px;
}

.checkMarkBar {
padding-top: 4px;
text-align: right;
padding-right: 30px;
line-height: 18px;
}

.delete_button {
	width: 60px;
	height: 20px;
	font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
	font-size: 11px;
	font-weight: bold;
	color: #003366;
}
.delete_button:disabled {
	color: #9d9daf;
}
</style>
<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<script type="text/javascript">
var formName = 'configGuide';
var lastOpened = '<s:property value="%{lastOpened}"/>';
var ssidProfileId = '<s:property value="%{ssidProfileId}"/>';
var ssidProfileName = '<s:property value="%{ssidProfileName}"/>';
var userProfileId = '<s:property value="%{userProfileId}"/>';
var hiveAPHeight = '<s:property value="%{hiveAPHeight}"/>';
var thisOperation;
var thisRemoveSsidId;
// delete flag, if 'delete SSID' was selected, flag=1; else flag=0;
var deleteFlag = 0;
function showAccessMessageDialog(info)
{
	if (infoDialog == null)
	{
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "450px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon:"",
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]});
		infoDialog.setHeader("<s:text name="info.home.start.here.access.popup.head"/>");
		infoDialog.render(document.body);
	}

	infoDialog.cfg.setProperty("text",info);
	infoDialog.show();
}

function decode(text)
{
return text.replace(/&amp;/g, '&').replace(/&quot;/g, '/"').replace(/&lt;/g, '<').replace(/&gt;/g, '>');
}
function onLoadPage() {
	YAHOO.util.Dom.setStyle(document.body, "background-color", "#f1f1f1");
	<s:if test="%{!hideAccessMessage}">
	var accessMessage='<s:property value="%{accessMessage}"/>';
	if(accessMessage)
		{
		showAccessMessageDialog(decode(accessMessage));
		}
	</s:if>
	 createAnimation();
<%-- hide from server side, avoid re-layout on page rendering.
	 /*
	  * hide the left menu bar
	  */
	 hideSlider();
--%>
	 /*
	  * open the last drawer
	  */
	 if(lastOpened == 'ssid') {
		addActiveElement(Get("ssidSection"));
		openSSID();
	 } else if (lastOpened == 'uploadConfigEx'){
		addActiveElement(Get("uploadSection"));
		uploadAnimExpanded();
	 } else {// default
		addActiveElement(Get("apSection"));
	 	apAnimExpanded();
	 }
}

function showLeftMenu() {
	sizeLeftMenu();
}

var apAnimation;
var uploadAnimation;

var ssidAnimation;

function createAnimation () {
	apAnimation = new YAHOO.util.Anim('apAnimation');
	apAnimation.method = YAHOO.util.Easing.easeOut;
	apAnimation.duration = 0.5;

	uploadAnimation = new YAHOO.util.Anim('uploadAnimation');
	uploadAnimation.method = YAHOO.util.Easing.easeOut;
	uploadAnimation.duration = 0.5;

	ssidAnimation = new YAHOO.util.Anim('ssidAnimation');
	ssidAnimation.method = YAHOO.util.Easing.easeOut;
	ssidAnimation.duration = 0.5;
}

function submitAction(operation, removeId) {
	thisOperation = operation;
	thisRemoveSsidId=removeId;
	if("guideHelps" == operation){
		openHelpPage();
	} else if ('removeSsid' == operation) {
		// set delete flag
		deleteFlag = 1;
		confirmDialog.show();
	}else{
		doContinueOper();
	}
}


function doContinueOper() {
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].removeSsidId.value = thisRemoveSsidId;
    document.forms[formName].submit();
}

function validate(operation){
	return true;
}
/**
 * Get the mouse position
 */
function mousePosition(ev){
	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop - document.body.clientTop
	};
}
<%--
function mouseOver1stLevelDrawer(div) {
	// set normal CSS
	YAHOO.util.Dom.setStyle(this,
			'background-image', 'url(images/hm/express/hm-2nd-level-drawer-hover.png)');
}

function mouseOut1stLevelDrawer(div) {
	// set normal CSS
	YAHOO.util.Dom.setStyle(this,
			'background-image', 'url(images/hm/express/hm-2nd-level-drawer.png)');
}

/**
 * Add mouse event for the second and third level drawer, change the value of 'backgroud-image' in CSS
 */
YAHOO.util.Event.onDOMReady(function() {

	YAHOO.util.Event.on("1stLevelDrawerAP", "mouseover", mouseOver1stLevelDrawer);
	YAHOO.util.Event.on("1stLevelDrawerAP", "mouseout", mouseOut1stLevelDrawer);

	YAHOO.util.Event.on("1stLevelDrawerSSID", "mouseover", mouseOver1stLevelDrawer);
	YAHOO.util.Event.on("1stLevelDrawerSSID", "mouseout", mouseOut1stLevelDrawer);

	YAHOO.util.Event.on("1stLevelDrawerUpload", "mouseover", mouseOver1stLevelDrawer);
	YAHOO.util.Event.on("1stLevelDrawerUpload", "mouseout", mouseOut1stLevelDrawer);

	var onTRMouseOver = function (event, matchedEl, container) {
		if(matchedEl.id == 'ssidIframeRow'){
			// dump
		}else{
			// set hover CSS
			// add logic for 'delete SSID' feture: because the (img) float style cannot work in in IE, add a table for delete image.
			if(strContains(matchedEl.id, 'subRow_')){
				var rowId = matchedEl.id.replace('subRow_', 'ssidRow_');
				YAHOO.util.Dom.setStyle(Get(rowId).cells[0],
						'background-image', 'url(images/hm/express/hm-3rd-level-drawer-hover.png)');
			}else{
				YAHOO.util.Dom.setStyle(matchedEl.cells[0],
						'background-image', 'url(images/hm/express/hm-3rd-level-drawer-hover.png)');
			}

		}
	};

	var onTRMouseOut = function (event, matchedEl, container) {
		if(matchedEl.id == 'ssidIframeRow'){
			// dump
		}else{
			// set normal CSS
			// add logic for 'delete SSID' feture: because the (img) float style cannot work in in IE, add a table for delete image.
			if(strContains(matchedEl.id, 'subRow_')){
				var rowId = matchedEl.id.replace('subRow_', 'ssidRow_');
				YAHOO.util.Dom.setStyle(Get(rowId).cells[0],
						'background-image', 'url(images/hm/express/hm-3rd-level-drawer.png)');
			}else{
				YAHOO.util.Dom.setStyle(matchedEl.cells[0],
						'background-image', 'url(images/hm/express/hm-3rd-level-drawer.png)');
			}

		}
	};

	YAHOO.util.Event.delegate("ssidListAnimation", "mouseover", onTRMouseOver, "tr");
	YAHOO.util.Event.delegate("ssidListAnimation", "mouseout", onTRMouseOut, "tr");

});--%>
</script>

<div id="content">
<s:form action="configGuide">
<s:hidden name="removeSsidId"/>
<s:hidden name="operation"/>
	<table width="950px;" border="0" cellspacing="0" cellpadding="0" align="center" id="firstTable">
		<tr>
			<td height="20px;"></td>
		</tr>
		<tr>
			<td>
			<div id="title">
			<table>
				<tr>
					<td width="100%" class="title">
						Guided Configuration
					</td>
					<td class="hive" nowrap="nowrap">
						<%-- Hive-<s:property value="%{hiveName}"/> --%>
							HiveManager - Express Mode
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div id="1stLevelDrawerAP" class="firstLevelBar">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td width="100%"><div id="apSection" class="firstLevelText">1. Configure Access Points</div></td>
					<td id="apButton" class="button" style="display: none;">
						<input type="button" value="Continue" onclick="clickContinueButton('ap')" class="button"/>
					</td>
					<td id="apCheckMark" class="checkMarkBar" style="display: none;">
						<img src="<s:url value="/images/hm/express/hm-checkmark.png" includeParams="none"/>"/>
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td class="firstLevelSeparator">
			</td>
		</tr>
		<tr>
			<td height="0px;">
			<div id="apAnimation" style="display: none;" class="frameDiv" align="center">
				<iframe id="apIframe" class="innerFrame" src=""></iframe>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div id="1stLevelDrawerSSID" class="firstLevelBar">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td><div id="ssidSection" class="firstLevelText">2. Configure SSIDs and User Access</div></td>
					<s:if test="%{!writeDisable4Struts}">
					<td id="addSSID" class="button" style="display: none;">
						<input type="button" value="New SSID" onclick="addSSID();" class="button"
						<s:property value="writeDisabled" /> />
					</td>
					</s:if>
					<td id="ssidButton" class="button" style="display: none;">
						<input type="button" value="Continue" onclick="clickContinueButton('ssid')" class="button"/>
					</td>
					<td id="ssidCheckMark" class="checkMarkBar" style="display: none; width: 25px;">
						<img src="<s:url value="/images/hm/express/hm-checkmark.png" includeParams="none"/>" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td height="0px;">
			<div id="ssidListAnimation" style="display: block;" class="frameDiv">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" id="ssidTable">
			<s:iterator value="ssids">
				<tr id="ssidRow_<s:property value="id"/>">
					<td class="secondLevelBar">
						<div onclick="onclickSSID('<s:property value="id"/>')">
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<tr id="subRow_<s:property value="id"/>">
								<td width="100%" class="secondLevelText"><s:property value="ssidName"/></td>
								<%--
								<s:if test="%{ssidCount > 1 && !writeDisable4Struts}">
								<td>
									<input type="button" value="Remove" class="delete_button"
										onclick="submitAction('removeSsid', <s:property value="id"/>);"
										<s:property value="writeDisabled" /> />
								</td>
								</s:if>
								--%>
							</tr>
						</table>
						</div>
					</td>
				</tr>
			</s:iterator>
				<tr id="ssidIframeRow">
					<td>
					<div id="ssidAnimation" style="display: none;" align="center">
						<iframe id="ssidIframe" class="innerFrame" src=""></iframe>
					</div>
					</td>
				</tr>
				<tr id="ssidRow_temp" style="display: none;">
					<td class="secondLevelBar" style="padding-left: 62px;"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td class="firstLevelSeparator">
			</td>
		</tr>
		<tr>
			<td>
			<div id="1stLevelDrawerUpload" class="firstLevelBar">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td width="100%"><div id="uploadSection" class="firstLevelText">3. Upload My Configuration</div></td>
					<s:if test="%{jumpFromIDM}">
					<td id="doneButtonCell" class="button" style="display: none;padding-right: 5px;">
                        <input type="button" value="Done" class="button" id="doneButton"/>
                    </td>
					</s:if>
					<td id="uploadCheckMark" class="checkMarkBar" style="display: none;">
						<img src="<s:url value="/images/hm/express/hm-checkmark.png" includeParams="none"/>" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td height="0px;">
			<div id="uploadAnimation" style="display: none;" class="frameDiv" align="center">
				<iframe id="uploadIframe" class="innerFrame" src=""></iframe>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div id="foot">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td height="20px">
			</td>
		</tr>
	</table>
</s:form>
</div>

<script type="text/javascript">
var apExpanded = 0; // false
var uploadExpanded = 0; // false
var ssidExpanded = 0; // false

var tempSSIDRowId='ssidRow_temp';
/**
 * Array, use for change active/inactive font style.
 * do pop/push operation when click event.
 */
var activeElements = [];

///////////// My Access Point Section /////////////
YAHOO.util.Event.on('apSection', 'click', function() {
	if (apExpanded) {
		//check HiveAp
		if(keepChangedHiveAp()){
			return;
		}

		apAnimCollapsed();

		inactiveElements();
	}else {
		//check SSID
		if (keepChangedSSID()){
			return;
		}else if(ssidLastValue == 'add'){
			// the temp row should be hide
			hideAddSSIDRow();
		}

		inactiveElements();

		if (ssidExpanded) {
			ssidAnimCollapsed();
		}
		if (uploadExpanded) {
			uploadAnimCollapsed();
		}

		addActiveElement(Get("apSection"));

		apAnimExpanded();
	}
});

function apAnimCollapsed (){
	apAnimation.stop();
	apAnimation.attributes.height = { to: 0 };
	apExpanded = 0;
	Get('apAnimation').style.display = 'none';
	apAnimation.animate();
	Get('apIframe').src='';

}
//iframe ready flag for Firefox/Chrome
var isReadyExpandAp;
// if animation is finished
var isAnimationCompleted;

function apAnimExpanded (){
	isReadyExpandAp = 0;
	isAnimationCompleted = 0;
	// TODO redirect, height
	apAnimation.stop();
	apAnimation.attributes.height = { to: hiveAPHeight };
	apExpanded = 1;
	apAnimation.animate();

	hm.util.show('apAnimation');
	showLoadingPanel(Get('apAnimation'));

	if (YAHOO.env.ua.ie) {
		apAnimation.onComplete.subscribe(function() {
			isAnimationCompleted = 1;
		});
	} else {
		// Not work in IE8, the iframe state change to 'complete' before the animation complete
		apAnimation.onComplete.subscribe(function() {
			// show busy hour glass
			if (isReadyExpandAp) {
			} else {
				showLoadingPanel(Get('apAnimation'));
			}
		});
	}

	<s:if test="%{fromEditAp > 0}">
		var url="hiveAp.action?operation=edit2&exConfigGuideFeature=hiveapEx&hmListType=manageAPEx&listTypeFromSession=manageAPEx&id=<s:property value='%{fromEditAp}'/>";
	</s:if>
	<s:else>
		var url="hiveAp.action?operation=manageAPEx&exConfigGuideFeature=manageAPEx&hmListType=manageAPEx&listTypeFromSession=manageAPEx";
	</s:else>

	Get('apIframe').src=url;

	// show or hide check mark for guided flow
	// the onload/onreadystatechange function will be invoke when the src of iframe was changed
	if (YAHOO.env.ua.ie){
		Get('apIframe').onreadystatechange = function(){
	        if (Get('apIframe').readyState == "loading"){
	        	showLoadingPanel(Get('apAnimation'));
	        }
	        //For IE, the iframe has two states 'loading', 'inactive' before 'complete'
	        if (Get('apIframe').readyState == "complete"){
	        	afterHiveAPIframeReady();
	        }
	    };
	} else {
		Get('apIframe').onload = function(){
			afterHiveAPIframeReady();
	    };
	}
 	// set others checkmark
	if(<s:property value="ssidCount" /> > 0){
		showCheckMark('ssid');
	}
}
/**
 * Show/Hide the button/checkmark after the AP iframe reload
 */
var aplistSize = 0;
function afterHiveAPIframeReady() {
	var count = <s:property value="hiveAPsCount" />;
	if(getIFrameDOMById("apIframe").document.getElementById('hiveApListTable')){
		if(count == 0){
			hideButtonAndCheckMark('ap');
		}else{
			showButton('ap');
		}
	}else if(getIFrameDOMById("apIframe").document.getElementById('editHiveAPTable')){
		hideButtonAndCheckMark('ap');
	}else{
		if(count == 0 ){
			hideButtonAndCheckMark('ap');
		}else{
			showCheckMark('ap');
		}
	}
	// hide busy hour glass
	hideLoadingPanel();
	// check if the WLAN policy was changed, show/hide the warning message
	if(isContainsAPsAndSSIDs())
		checkWLANPolicy();

	isReadyExpandAp = 1;
}

/**
 * Check have the SSID form been changed. If changed, popup the confirm dialog to prompt user.
 */
function keepChangedHiveAp(){
	var iframeDOM = getIFrameDOMById("apIframe");
	if (iframeDOM && typeof(iframeDOM.formDataIsChanged) == 'function' &&
			getIFrameDOMById("apIframe").document.getElementById('editHiveAPTable')){
		var isFormChanged = iframeDOM.formDataIsChanged();
		if(isFormChanged){
			return !confirm("You have unsaved changes. They will be lost if you continue.\nDo you want to continue?");
		}
	}
	return false;
}
///////////// My SSIDs Section /////////////
YAHOO.util.Event.on('ssidSection', 'click', function() {
	if (ssidExpanded) {
		if(keepChangedSSID()){
			return;
		}

		ssidAnimCollapsed();
		inactiveElements();

		return;
	}else {
		//check HiveAp
		if(keepChangedHiveAp()){
			return;
		}

		inactiveElements();

		if (apExpanded) {
			apAnimCollapsed();
		}
		if (uploadExpanded) {
			uploadAnimCollapsed();
		}

		addActiveElement(Get("ssidSection"));

		openSSID();
	}
});

/**
 * colse other drawer before expand the ssid drawer
 */
function checkCollapse4SSID() {
	if(keepChangedHiveAp()){
		return false;
	}

	inactiveElements();

	if (apExpanded) {
		apAnimCollapsed();
	}
	if (uploadExpanded) {
		uploadAnimCollapsed();
	}

	addActiveElement(Get("ssidSection"));

	return true;
}
/**
 * add some logic before expand the ssid animation
 */
function openSSID (){
	if(!checkCollapse4SSID()){
		return;
	}

	var trArray = document.getElementById('ssidTable').rows;
	var rowLength = trArray.length;
	//alert("tr height="+trArray[0].offsetHeight+" ssidListHeight:"+ssidListHeight+" rowLength:"+rowLength);
	// set default expanded drawer
	if (rowLength == 2){
		// if there is no available SSIDS, guide people to add a SSID;
		if(lastOpened == 'ssid'){
			// back to SSID, need to show the add row for page
			showAddSSIDRow();
			if (userProfileId) {
				ssidAnimExpanded(null,null,userProfileId);
			} else {
				ssidAnimExpanded();
			}
		}else{
			// new SSID
			if ('<s:property value="writeDisabled" />' == '')
				addSSID();
		}
	}else{
		// else show the first available SSID value or the current edit row.
		if (ssidProfileId){
			// if ssidProfileId is available, it meens the page is redirected from privous edit operation,
			// should get back the edit data from session.
			if (userProfileId) {
				onclickSSID(ssidProfileId, userProfileId);
			} else {
				onclickSSID(ssidProfileId);
			}
		}else{
			if(lastOpened == 'ssid' && ssidProfileName!=null && ssidProfileName!=''){
				// a new page, show the add row
				showAddSSIDRow();
				if (userProfileId) {
					ssidAnimExpanded(null,null,userProfileId);
				} else {
					ssidAnimExpanded();
				}
			}else{// default first row was selected
				var firstRow = trArray[0];
				onclickSSID(getSSIDRowDataId(firstRow));
			}
		}
	}
}
// sub SSID animation
/**
 * the ssidLastValue equals 'add' when add a SSID, should not allow to change to another SSID or add another SSID;
 * if in the condition of 'add SSID', **MUST** remember to change the ssidLastValue in the Iframe!!!
 * else it equals the (SsidProfiles.)id of the current selected SSID.
 */
var ssidLastValue = '';

// @Deprecated
function popupConfirmDialog (){
	if (null != confirmDialog){
		confirmDialog.cfg.setProperty("text","<html><body>You have unsaved changes. They will be lost if you continue.<p>Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

//function handleYes() {
//    alert("test");
//    this.hide();
//}

/**
 * Get first SSID row if exist
 */
function getFirstSSIDRow() {
	var trArray = document.getElementById('ssidTable').rows;
	var rowLength = trArray.length;
	if (rowLength == 2){
		return null;
	}else{
		 return trArray[0];
	}
}
/**
 * Get the data id of the SSID row
 */
function getSSIDRowDataId(row) {
	var trId= row.id;
	return ssid = trId.substr(8);
}

function onclickSSID (value, userId){
	// if mouse selected on the 'delete' picture do nothing
	if(deleteFlag){
		return;
	}
	if(!checkCollapse4SSID()){
		return;
	}
	if (ssidExpanded) {
		if (keepChangedSSID()) {
			return;
		}else if (ssidLastValue == value) {
			ssidAnimCollapsed();
			inactiveElements();
			return;
		}else if(ssidLastValue == 'add'){
			// the temp row should be remove
			hideAddSSIDRow();
		}
	}
	var row = Get("ssidRow_"+value);
	var iframeRowIndex = Get("ssidIframeRow").rowIndex;
	var index = row.rowIndex;

	if (activeElements.length > 1)
		inactiveLastElement();
	// add a table in row
	addActiveElement(Get('subRow_'+value).cells[0]);

	ssidLastValue = value;

	if (index+1 != iframeRowIndex){
		// move the Iframe row after this SSID row
		moveSSIDIframeRow(row);
	}
	if (userId) {
		ssidAnimExpanded(null, null, userId);
	} else {
		ssidAnimExpanded(value);
	}
}

/**
 * add a new SSID
 */
function addSSID (){
	if(!checkCollapse4SSID()){
		return;
	}
	if (ssidExpanded) {
		if(keepChangedSSID()){
			return;
		}else if(ssidLastValue == 'add'){
			// the temp row should be remove
			hideAddSSIDRow();
		}
	}
	showAddSSIDRow();
	// set the url and open the drawer
	var url = "ssidProfiles.action?operation=new&exConfigGuideFeature=ssid";
	ssidAnimExpanded(null, url);
}

/**
 * show 'Add SSID' row
 */
function showAddSSIDRow () {
	ssidLastValue = 'add';

	var addSSIDRow = Get(tempSSIDRowId);
	addSSIDRow.cells[0].innerHTML = 'New SSID';
	// show this row
	hm.util.show(tempSSIDRowId);

	// move the iframe row after the new row
	moveSSIDIframeRow(addSSIDRow);
	// add to ative array for change font color
	if (activeElements.length > 1)
		inactiveLastElement();
	addActiveElement(addSSIDRow.cells[0]);
}

function hideAddSSIDRow () {
	ssidLastValue = '';
	var addRow = Get(tempSSIDRowId);
	hm.util.hide(tempSSIDRowId);
}

/**
 * Check have the SSID form been changed. If changed, popup the confirm dialog to prompt user.
 */
function keepChangedSSID(){
	var iframeDOM = getIFrameDOMById("ssidIframe");
	if (iframeDOM && typeof(iframeDOM.formDataIsChanged) == 'function'){
		var isFormChanged = iframeDOM.formDataIsChanged();
		if(isFormChanged){
			return !confirm("You have unsaved changes. They will be lost if you continue.\nDo you want to continue?");
			//popupConfirmDialog();
			//return true;
		}
	}
	return false;
}

function ssidAnimCollapsed (){
	ssidAnimation.stop();
	ssidAnimation.attributes.height = { to: 0 };
	ssidExpanded = 0;
	ssidAnimation.animate();
	Get('ssidIframe').src = '';
}

//iframe ready flag for Firefox/Chrome
var isReadyExpandSsid;
/**
 * @param {String} the (SsidProfiles.)id of the current selected SSID.
 * @param {String} the action url, usr for 'add another SSID', to replace the default url pattern.
 */
function ssidAnimExpanded (id, anotherUrl, userId){
	isReadyExpandSsid = 0;

	ssidAnimation.stop();
	// set height (the tr height is 23)
	var ssidHeight = 610 + (id ? 23 : 0);
	ssidAnimation.attributes.height = { to: ssidHeight };
	ssidExpanded = 1;
	ssidAnimation.animate();

	hm.util.show('ssidAnimation');
	showLoadingPanel(Get('ssidAnimation'));
	if (!YAHOO.env.ua.ie) {
	// Not work in IE8, the iframe state change to 'complete' before the animation complete
		ssidAnimation.onComplete.subscribe(function() {
			if(!isReadyExpandSsid)
				// show busy hour glass
				showLoadingPanel(Get('ssidAnimation'));
		});
	}
	// start set the default url //
	var idPara = '';
	if (id) {
		idPara = '&id='+id;
	}
	var url;
	// default edit operation
	if (userId) {
		url="userProfiles.action?operation=edit&exConfigGuideFeature=ssid&id=" + userId;
	} else {
		url="ssidProfiles.action?operation=edit&exConfigGuideFeature=ssid" + idPara;
	}

	// end set the default url //

	if (anotherUrl) {
		// change the url
		url = anotherUrl;
	}

	var iframe = Get('ssidIframe');
	iframe.src=''; // For Some brower doesn't response correctly
	iframe.src = url + "&ignore=" + new Date().getTime();

	if (YAHOO.env.ua.ie){
	    iframe.onreadystatechange = function(){
	    	//alert("ssidListAnimation height:"+Get('ssidListAnimation').offsetHeight+"\iframe height:"+iframe.offsetHeight);

	        if (iframe.readyState == "loading"){
	        	// show busy hour glass
				showLoadingPanel(Get('ssidAnimation'));
	        }
	        //For IE, the iframe has two states 'loading', 'inactive' before 'complete'
	        if (iframe.readyState == "complete"){
	        	afterSSIDIframeReady();
	        }
	    };
	} else {
	    iframe.onload = function(){
	    	//alert("ssidListAnimation height:"+Get('ssidListAnimation').offsetHeight+"\iframe height:"+iframe.offsetHeight);
    		afterSSIDIframeReady();
	    };
	}
	// set others checkmark
	if(<s:property value="hiveAPsCount" /> > 0){
		showCheckMark('ap');
	}
}

/**
 * Show/Hide the button/checkmark after the SSID iframe reload
 */
function afterSSIDIframeReady() {
	if(getIFrameDOMById("ssidIframe").document.getElementById('ssidEditTable')){
		if(getIFrameDOMById("ssidIframe").isFormChanged){
			// for nested page in SSID
			//console.debug("the SSID form is changed. @"+ new Date());
		}else if(ssidLastValue == 'add'){
			hideButtonAndCheckMark('ssid');
		}else{
			showButton('ssid');
		}
		//getIFrameDOMById("ssidIframe").registerFormListener();
	} else {
		var forms = getIFrameDOMById("ssidIframe").document.forms;
		if (forms.length == 0) {
			var ssidSize = <s:property value="ssidCount" />;
			if(0 == ssidSize){
				hideButtonAndCheckMark('ssid');
			}else{
				showCheckMark('ssid');
			}
		} else {
			hideButtonAndCheckMark('ssid');
		}
	}
	// hide busy hour glass
	hideLoadingPanel();
	// check if the WLAN policy was changed, show/hide the warning message
	if(isContainsAPsAndSSIDs())
		checkWLANPolicy();

	isReadyExpandSsid = 1;
}

///////////// Upload My configuration Section /////////////
YAHOO.util.Event.on('uploadSection', 'click', function() {
	if (uploadExpanded) {
		uploadAnimCollapsed();

		inactiveElements();
	}else {
		//check HiveAp
		if (keepChangedHiveAp()){
			return;
		}
		//check SSID
		if (keepChangedSSID()){
			return;
		}else if(ssidLastValue == 'add'){
			// the temp row should be showed
			hideAddSSIDRow();
		}

		inactiveElements();

		if (apExpanded) {
			apAnimCollapsed();
		}
		if (ssidExpanded) {
			ssidAnimCollapsed();
		}

		addActiveElement(Get("uploadSection"));

		uploadAnimExpanded();
	}
});

function uploadAnimCollapsed (){
	Get('uploadAnimation').style.display = 'none';
	uploadAnimation.stop();
	uploadAnimation.attributes.height = { to: 0 };
	uploadExpanded = 0;
	uploadAnimation.animate();

	Get('uploadIframe').src = '';
}
// iframe ready flag for Firefox/Chrome
var isReadyExpandUpload;

function uploadAnimExpanded (){
	//TODO set height
	uploadAnimation.stop();
	isAnimationCompleted = 0;
	uploadAnimation.attributes.height = { to: hiveAPHeight };
	uploadExpanded = 1;
	uploadAnimation.animate();

	isReadyExpandUpload = 0;


	hm.util.show('uploadAnimation');
	showLoadingPanel(Get('uploadAnimation'));
	if (YAHOO.env.ua.ie) {
		uploadAnimation.onComplete.subscribe(function() {
			isAnimationCompleted = 1;
		});
	} else {
		uploadAnimation.onComplete.subscribe(function() {
			if(isReadyExpandUpload){
			} else {
				// Not work in IE8, the iframe state change to 'complete' before the animation complete
				// show busy hour glass
				showLoadingPanel(Get('uploadAnimation'));
			}
		});
	}

	var url="hiveAp.action?operation=manageAPEx&exConfigGuideFeature=uploadConfigEx&hmListType=manageAPEx&listTypeFromSession=manageAPEx";

	var iframe = Get('uploadIframe');
	iframe.src = url;

	if (YAHOO.env.ua.ie){
	    iframe.onreadystatechange = function(){
	    	//For IE, show the loading image before 'complete' state
	        if (iframe.readyState == "loading"){
	        	// show busy hour glass
				showLoadingPanel(Get('uploadAnimation'));
	        }
	        if (iframe.readyState == "complete"){
	        	afterUploadIframeReady();
	        }
	    };
	} else {
	    iframe.onload = function(){
    		afterUploadIframeReady();
	    };
	}
 	// set others checkmark
	if(<s:property value="hiveAPsCount" /> > 0){
		showCheckMark('ap');
	}
	if(<s:property value="ssidCount" /> > 0){
		showCheckMark('ssid');
	}
}

function afterUploadIframeReady() {
	// hide busy hour glass
	hideLoadingPanel();

	isReadyExpandUpload = 1;

}
///////////// Warning Message Section /////////////
/**
 * set warning message. No need to show button if no any SSID???
 */
function setWarningMessage(msg) {
	var warningDiv = Get('guidedWarning');
	if(warningDiv){
		var cells = warningDiv.getElementsByTagName('td');
		if (cells.length != 2) {
			return;
		}
		var msgcell, buttoncell;
		if(cells[0].id == 'guidedMsgEx'){
			msgcell = cells[0];
			buttoncell = cells[1];
		}else {
			msgcell = cells[1];
			buttoncell = cells[0];
		}
		msgcell.innerHTML = msg;
		if(typeof(hideMessageInSession) == 'function') {
			//hideMessageInSession();
			YAHOO.util.Dom.setStyle('lsWarning', "display", "none");
		}
		hm.util.show('guidedWarning');
	}
}
function hideWarningMessage() {
	hm.util.hide('guidedWarning');
	YAHOO.util.Dom.setStyle('lsWarning', "display", "");
}
/**
 * goto the upload panel
 */
function gotoUploadPanel() {
	if(uploadExpanded)
		return;

	YAHOO.util.UserAction.click(Get('uploadSection'));
}
/**
 * check the session in server
 */
function checkWLANPolicy() {
	var url = "<s:url action='configGuide' includeParams='none' />" + "?operation=checkWLANChangeStatus" +"&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : checkResult }, null);
}

function checkResult(o) {
	eval("var result = " + o.responseText);
	if(result.isChanged){
		setWarningMessage('<s:text name="config.guided.warning.msg"></s:text>');
	}else{
		YAHOO.util.Dom.setStyle('lsWarning', "display", "");
	}
}
///////////// Common fucntion /////////////
function isContainsAPsAndSSIDs() {
	var apCount = <s:property value="hiveAPsCount" />;
	var ssidCount = <s:property value="ssidCount" />;
	if(apCount == 0){
		return false;
	}
	return true;
}

function hideButtonAndCheckMark(param) {
	if(Get(param+'Button')) hm.util.hide(param+'Button');
	if(Get(param+'CheckMark')) hm.util.hide(param+'CheckMark');

	if('ssid' == param) {
		hm.util.hide('addSSID');
	}
}

function showCheckMark(param) {
	if(Get(param+'Button')) hm.util.hide(param+'Button');
	if(Get(param+'CheckMark')) hm.util.show(param+'CheckMark');

	if('ssid' == param) {
		hm.util.hide('addSSID');
	}
}

function showButton(param) {
	if(Get(param+'Button')) hm.util.show(param+'Button');
	if(Get(param+'CheckMark')) hm.util.hide(param+'CheckMark');

	if('ssid' == param) {
		hm.util.show('addSSID');
	}
}

var continueOperation;
/**
 * continue action
 */
function clickContinueButton(param) {

	if('ap' == param){
		YAHOO.util.UserAction.click(Get('ssidSection'));
	}else if('ssid' == param){
		YAHOO.util.UserAction.click(Get('uploadSection'));
	}
	continueOperation = param;
}

/**
 * get Iframe DOM by element Id
 */
function getIFrameDOMById(id){
    if (document.all){//IE
    	if (document.frames[id])
       		return	doc = document.frames[id];
	}else{//Firefox
		if (document.getElementById(id))
	   		return	doc = document.getElementById(id).contentWindow;
	}
}
/**
 * move the iframe row after the designated row
 * @param {Element} the designated row
 */
function moveSSIDIframeRow (destRow) {

	 Get('ssidIframe').src = '';

	// get the row parent - table
	var parent=Get("ssidIframeRow").parentNode;
	//table remove a row
	var originNode=parent.removeChild(Get("ssidIframeRow"));
	// insert row
	var rowId = destRow.id;
	if (rowId == 'ssidRow_add' ){
		YAHOO.util.Dom.insertBefore(originNode,destRow);
	}else{
		YAHOO.util.Dom.insertAfter(originNode,destRow);
	}
}

/**
 * set an active element font style
 */
function addActiveElement (element) {
	element.style.color="#1E4A78";
	activeElements.push(element);
	if(element.id == "ssidSection"){
		YAHOO.util.Dom.addClass("1stLevelDrawerSSID", "actived");
	}else if(element.id == "uploadSection"){
		YAHOO.util.Dom.addClass("1stLevelDrawerUpload", "actived");
	}else if(element.id == "apSection"){
		YAHOO.util.Dom.addClass("1stLevelDrawerAP", "actived");
	}
}
/**
 * set all element for inactive font style
 */
function inactiveElements () {
	for(var i=activeElements.length-1; i >= 0; i--){
		inactiveLastElement();
	}
	YAHOO.util.Dom.removeClass("1stLevelDrawerSSID", "actived");
	YAHOO.util.Dom.removeClass("1stLevelDrawerUpload", "actived");
	YAHOO.util.Dom.removeClass("1stLevelDrawerAP", "actived");
}
/**
 * set last element as inactive font style
 */
function inactiveLastElement () {
	activeElements.pop().style.color="#767676";
}
/**
 * check if contains charaters in string
 */
function strContains(str, sub){
 return (str.indexOf(sub) != -1);
}

</script>
<!-- loading panel -->
<div id="loadingPanel" style="display: none;">
	<img src="<s:url value="/images/hm/express/ajax-loader-on-white.gif" includeParams="none"/>" />
</div>
<script type="text/javascript">
var busyPanel;

function centerPosition(container) {
	return {
		x:YAHOO.util.Dom.getX(container) + container.offsetWidth/2 - 28,
		y:YAHOO.util.Dom.getY(container) + container.offsetHeight/2
	};
}
// use a panel??
function createLoadingPanel(container) {
	busyPanel = new YAHOO.widget.Panel('loadingPanel',
            { xy:centerPosition(container),
              fixedcenter:true,
              close:false,
              draggable:false,
              visible:false
            }
        );
	busyPanel.render(document.body);
}
/**
 * Busy hour glass, show loading image
 */
function showLoadingPanel(container) {
	if(null == busyPanel){// use panel
		//createLoadingPanel(container);
	}
	//loadingPanel.show();
	setLoadingDiv(container);
}
/**
 * Loading iframe done, hide loading image
 */
function hideLoadingPanel() {
	if(busyPanel){
		busyPanel.hide();
	}

	hm.util.hide('loadingPanel');
}
/**
 * Relocate the image position
 */
function setLoadingDiv(container) {
	var xy = centerPosition(container);
	var el = Get('loadingPanel');
	//alert("x="+xy.x+" y="+xy.y);
	if(isNaN(xy.x) || isNaN(xy.y)) {
		return;
	}
	YAHOO.util.Dom.setStyle(el, 'left', xy.x + 'px');
	YAHOO.util.Dom.setStyle(el, 'top', (xy.y > 600 || xy.y < 300 ? 400 : xy.y) + 'px');
	YAHOO.util.Dom.setStyle(el, 'z-index', 4);
	YAHOO.util.Dom.setStyle(el, 'position', 'absolute');

	hm.util.show('loadingPanel');

}
</script>

<script type="text/javascript"
	src="<s:url value="/yui/event-simulate/event-simulate-min.js" includeParams="none" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/selector/selector-min.js" includeParams="none" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/event/event-min.js" includeParams="none" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/event-delegate/event-delegate-min.js" includeParams="none" />"></script>