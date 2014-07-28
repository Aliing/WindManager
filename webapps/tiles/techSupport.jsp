<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.be.admin.QueueOperation.HHMUpdateStatusItem"%>

<!-- CSS -->
<script src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" />"></script>


<style type="text/css">
#guideContent {
	line-height: 1.5em;
	padding: 0px 5px 10px 5px;
}

span.title {
	font-weight: bold;
	font-size: 12px;
	color: #003366;
}

.current-item {
	background: #EBF4FF none repeat scroll 0%;
}

.infoList tr {
	border-bottom: 1px dashed #E1E1E1;
	padding: 10px 15px 20px;
}

.infoList .current-item td {
	border-bottom: 1px solid #C0DAFF;
}
</style>


<script>
var sysName = '<s:property value="%{systemNmsName}"/>';

function showWaitMessage() {
	document.getElementById("tellFriendNoteId").className="noteInfo";
	document.getElementById("tellFriendNoteId").innerHTML = "Your request is being processed ...";
	document.getElementById("tellFriendNoteDiv").style.display="";
}

// Tell-Friend Panel Begin ===============================================

var tellFriendPanel = null;
function createTellFriendPanel(){
	var div = window.document.getElementById('tellFriendPanel');
	tellFriendPanel = new YAHOO.widget.Panel(div, { width:"368px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	tellFriendPanel.render(document.body);
	div.style.display = "";
	tellFriendPanel.beforeHideEvent.subscribe(clearFriendContent);
}

function clearFriendContent() {
	document.getElementById("mailAddress").value = "";
	document.getElementById("shareAccount").checked = false;
	document.getElementById("personalNote").value = "";
	document.getElementById("tellFriendNoteDiv").style.display = "none";
}

function openTellFriendPanel() {
	if(null == tellFriendPanel){
		createTellFriendPanel();
	}
	
	tellFriendPanel.show();	
}

function hideTellFriendPanel() {
	clearFriendContent();
	
	if(null != tellFriendPanel){
		tellFriendPanel.hide();
	}	
}

function tellFriend() {
	var element = document.getElementById("mailAddress");
	var address = element.value;
	
	if(address == "" || address.trim().length == 0) {
		hm.util.reportFieldError(element, '<s:text name="hm.config.guide.tellFriend.email.empty" />');
        element.focus();
		return ;
	}
	
	if (!hm.util.validateEmail(address))
	{
		hm.util.reportFieldError(element, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
		element.focus();
		return false;
	}
	
	showWaitMessage();
	
	var url = "<s:url action='configGuide' includeParams='none' />?operation=tellFriend&mailAddress=" + address
				+ "&shareAccount=" + document.getElementById("shareAccount").checked + "&personalNote="
				+ document.getElementById("personalNote").value.replace(/\n/g, "<br>");
				
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
		{ success:tellFriendOk,failure:tellFriendFailed,timeout: 100000}, null);
}

var tellFriendOk = function(o) {
	eval("var result = " + o.responseText);
	
	if(result.success)
		document.getElementById("tellFriendNoteId").className="noteInfo";
	else 
		document.getElementById("tellFriendNoteId").className="noteError";
	
	
	document.getElementById("tellFriendNoteId").innerHTML = result.msg;
	document.getElementById("tellFriendNoteDiv").style.display="";
};

var tellFriendFailed = function(o) {
};

// Tell-Friend Panel Begin ===============================================

// Upgrade to HM Begin ===============================================
function upgrade() {
	thisOperation = 'upgradeToHM'; // variable 'thisOperation' defined in file 'view2.jsp'
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will upgrade the Planner to the " + sysName + " Online demo.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue ?</body></html>");
	confirmDialog.show();
}

function upgradeToHM() {
	if(waitingPanel != null) {
		waitingPanel.setHeader("Upgrading from the Planner to the " + sysName + " Online demo...");
		waitingPanel.show();	
	}

    var url = "<s:url action='updateSoftware' includeParams='none' />?operation=upgradeToHM";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
		{ success:upgradeOk,failure:upgradeFailed}, null);
}

var upgradeFailed = function(o) {
	if(waitingPanel != null) {
		waitingPanel.hide();
	}
};

var upgradeOk = function(o) {
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	eval("var result = " + o.responseText);
	
	if (result.success)
	{
		startPollUpdateStatusTimer();
		if(waitingPanel != null){
			waitingPanel.show();
		}
	}
	else
	{
		warnDialog.cfg.setProperty('text', result.message);
		warnDialog.show();
	}
};

var updateStatusOverLay;
var pollTimeoutId;
var interval = 1;

function startPollUpdateStatusTimer() {
	pollTimeoutId = setTimeout("pollUpdateStatus()", interval * 1000);  // seconds
}
function pollUpdateStatus() {
	var url = "<s:url action='updateSoftware' includeParams='none' />?operation=pollUpdateStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : updateStatus }, null);
}

function updateStatus(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
	if(null != updateStatusOverLay){
		updateStatusOverLay.cfg.setProperty('visible', false);
	}
	
	eval("var result = " + o.responseText);
	var status = result.status;
	if (status == <%=HHMUpdateStatusItem.UPDATE_FINISHED%>)
	{
		clearTimeout(pollTimeoutId);
		if (result.success) {
			if(dialog == null) {
				initDialog();
			}
			dialog.cfg.setProperty('text', "<html><body>The upgrade completed successfully.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
				+ "Please log in once again." + "</body></html>");
			dialog.show();
		} else {
			warnDialog.cfg.setProperty('text', "<html><body>Unable to upgrade the Planner to the " + sysName + " Online demo ("+result.message+"). <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
				+ "There appears to be a problem with the server. Try again later, and if the problem persists, contact <a>support@aerohive.com</a>.</body></html>");
			warnDialog.show();
		}
		
		return;
	} else if (status == <%=HHMUpdateStatusItem.UPDATE_WAITTING%>)
	{
		document.getElementById('updateWaitTD').style.display = "block";
		document.getElementById('updateRunTD').style.display = "none";
	
		document.getElementById("updateStatusTD").innerHTML = "<td id='updateStatusTD'>"+ result.message +"</td>";
		
	} else if (status == <%=HHMUpdateStatusItem.UPDATE_RUNNING%>)
	{
		document.getElementById('updateWaitTD').style.display = "none";
		document.getElementById('updateRunTD').style.display = "block";
	
		// update status icon
		var backupImg = document.getElementById("backupImg");
		var transferImg = document.getElementById("transferImg");
		var restoreImg = document.getElementById("restoreImg");
		var laterImg = document.getElementById("laterImg");
		var runStatus = result.runStatus;
		if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Backup_Data%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			transferImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		} 
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Move_Data%>) 
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			restoreImg.src = "<s:url value="/images/HM-capwap-down.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		}
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Restore_Data%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			
			laterImg.src = "<s:url value="/images/HM-capwap-down.png" />";
		}
		else if (runStatus == <%=HHMUpdateStatusItem.Update_Status_Change%>)
		{
			backupImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			transferImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			restoreImg.src = "<s:url value="/images/HM-capwap-up.png" />";
			laterImg.src = "<s:url value="/images/HM-capwap-up.png" />";
		}
		
		interval = 1;
	}
	
	if(null == updateStatusOverLay){
		var div = document.getElementById('updateStatusPanel');
		updateStatusOverLay = new YAHOO.widget.Panel(div, {
			width:"420px",
			visible:false,
			fixedcenter:true,
			close:false,
			draggable:false,
			modal:true,
			constraintoviewport:true,
			zIndex:4
			});
		updateStatusOverLay.render(document.body);
		div.style.display = "";
	}
	
	if(null != updateStatusOverLay){
		updateStatusOverLay.cfg.setProperty('visible', true);
	}
	
	startPollUpdateStatusTimer();
}

function cancelUpdate()
{
	var url = "<s:url action='updateSoftware' includeParams='none' />?operation=cancelUpdate&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : cancelUpdateStatus }, null);
}

function cancelUpdateStatus(o)
{
	eval("var data = " + o.responseText);
	if (data.success)
	{
		clearTimeout(pollTimeoutId);
		if(null != updateStatusOverLay){
			updateStatusOverLay.cfg.setProperty('visible', false);
		}
	}
}

var dialog;
function initDialog() {
	dialog =
     new YAHOO.widget.SimpleDialog("dialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleOk, isDefault:true } ]
              } );
     dialog.setHeader("Message");
     dialog.render(document.body);
}

var handleOk = function() {
	dialog.hide();
	var url = "<s:url action='configGuide' includeParams='none' />?operation=logout";
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
			{success:logoutOk,failure:logoutFailed,timeout: 100000}, null);	
};

var logoutOk = function(o) {
	window.location.reload();
}

var logoutFailed = function(o) {
}

// Upgrade to HM End===============================================

YAHOO.util.Event.onDOMReady(function () {
	loadGlowImages();//load images first!
	var table = document.getElementById("infoList");
	YAHOO.util.Event.addListener(table.rows, "mouseover", mouseOverRow);
	YAHOO.util.Event.addListener(table.rows, "mouseout", mouseOutRow);
});

var guidedObjects = {
	userProfileObj:{
		over:"<s:url value="/images/UserProfiles-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/UserProfiles.png" includeParams="none"/>"
	},
	ssidObj:{
		over:"<s:url value="/images/SSIDs-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/SSIDs.png" includeParams="none"/>"
	},
	ludObj:{
		over:"<s:url value="/images/LocalUserDB-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/LocalUserDB.png" includeParams="none"/>"
	},
	wlanObj:{
		over:"<s:url value="/images/WLANpolicies-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/WLANpolicies.png" includeParams="none"/>"
	},
	hiveApObj:{
		over:"<s:url value="/images/HiveAPs-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/HiveAPs.png" includeParams="none"/>"
	},
	hiveApUpdateObj:{
		over:"<s:url value="/images/Update-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/Update.png" includeParams="none"/>"
	},
	contactObj:{
		over:"<s:url value="/images/Sales-Support-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/Sales-Support.png" includeParams="none"/>"
	},
	friendObj:{
		over:"<s:url value="/images/Tell-Friend-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/Tell-Friend.png" includeParams="none"/>"
	}
	
}
function loadGlowImages(){
	for(var prop in guidedObjects){
		var obj = guidedObjects[prop];
		hm.util.loadImage(obj.over);
		hm.util.loadImage(obj.out);
	}
}

function mouseOverRow(){
	this.className = 'current-item';
	
	if(this.cells[0].firstChild && 
		this.cells[0].firstChild.src && this.id){
		this.cells[0].firstChild.src = guidedObjects[this.id].over;
	}
}

function mouseOutRow(){
	this.className = '';
	
	if(this.cells[0].firstChild && 
		this.cells[0].firstChild.src && this.id){
		this.cells[0].firstChild.src = guidedObjects[this.id].out;
	}
}
</script>


<table id="infoList" border="0" cellspacing="0" cellpadding="0"
	width="100%">
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr align="center">
				<td valign="top"><img height="42px"
					alt="<s:text name="hm.config.guide.support" />"
					src="<s:url value="/images/Sales-Support.png" includeParams="none"/>" /></td>
			</tr>
			<tr align="center">
				<td valign="top"><a href="#support"
					onclick="openContactSupportPanel();"><span class="title"><s:text
					name="hm.config.guide.support" /></span></a></td>

			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="10px"></td>
	</tr>
	<s:if test="%{!userContext.superUser}">
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr align="center">
					<td valign="top"><img height="42px"
						alt="<s:text name="hm.config.guide.tellFriend" />"
						src="<s:url value="/images/Tell-Friend.png" includeParams="none"/>" /></td>
				</tr>
				<tr align="center">
					<td valign="top"><a href="#tellFriend"
						onclick="openTellFriendPanel();"><span class="title"><s:text
						name="hm.config.guide.tellFriend" /></span></a></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
	</s:if>
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr align="center">
				<td valign="top"><img height="42px"
					alt="<s:text name="topology.menu.operation.changePassword" />"
					src="<s:url value="/images/ChangePass-Glow.png" includeParams="none"/>" /></td>
			</tr>
			<tr align="center">
				<td valign="top"><a href="#changePassword"
					onclick="openChangePasswordPanel();"><span class="title"><s:text
					name="topology.menu.operation.changePassword" /></span></a></td>

			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="10px"></td>
	</tr>
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr align="center">	
				<td valign="top"><img  height="42px"
			alt="<s:text name="hm.config.guide.upgradeHM" />"
			src="<s:url value="/images/HM-Upgrade.png" includeParams="none"/>" /></td>
	</tr>
	<tr align="center">
		<td valign="top"><a href="#upgradeHM"
					onclick="upgrade();"><span class="title"><s:text
			name="hm.config.guide.upgradeHM" /></span></a></td>
	</tr>
</table>
		</td>
	</tr>
</table>

<div id="tellFriendPanel" style="display: none;">
<div class="hd"><s:text name="hm.config.guide.tellFriend" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td>
				<div id="tellFriendNoteDiv" style="display: none">
				<table width="320px" border="0" cellspacing="0" cellpadding="0"
					class="note">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td id="tellFriendNoteId"></td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
				</div>
				<div style="margin: 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td><s:text name="hm.config.guide.tellFriend.email" /><font
							color="red">*</font></td>
						<td><s:textfield id="mailAddress" size="32" /></td>
					</tr>
					<tr>
						<td></td>
						<td>
						<table>
							<tr>
								<td><s:checkbox id="shareAccount" name="share" /></td>
								<td><s:text name="hm.config.guide.tellFriend.share" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td valign="top"><s:text
							name="hm.config.guide.tellFriend.note" /></td>
						<td><s:textarea cssStyle="width: 190px;" id="personalNote"
							rows="5" /></td>
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
				<td><input type="button" name="ignore" value="Submit"
					class="button" onClick="tellFriend();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" onClick="hideTellFriendPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="updateStatusPanel" style="display: none">
<div class="hd">Update Software ...</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td id="updateWaitTD">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td width="20px"><img id="ignore"
					src="<s:url value="/images/waitingSquare.gif" />" /></td>
				<td id="updateStatusTD" width="400px"></td>
			</tr>
			<tr style="padding-top: 5px">
				<td align="center" colspan="2"><input type="button"
					id="cancelBtn" name="ignore" value="Cancel" class="button"
					onClick="cancelUpdate();" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td id="updateRunTD">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td><img id="backupImg"
					src="<s:url value="/images/HM-capwap-down.png" />" /></td>
				<td><s:text name="update.software.backup.database" /></td>
			</tr>
			<tr>
				<td><img id="transferImg"
					src="<s:url value="/images/HM-capwap-down.png" />" /></td>
				<td><s:text name="update.software.move.data" /></td>
			</tr>
			<tr>
				<td><img id="restoreImg"
					src="<s:url value="/images/HM-capwap-down.png" />" /></td>
				<td><s:text name="update.software.restore.database" /></td>
			</tr>
			<tr>
				<td><img id="laterImg"
					src="<s:url value="/images/HM-capwap-down.png" />" /></td>
				<td><s:text name="update.software.other.actions" /></td>
			</tr>
			<tr>
				<td height="10"></td>
			</tr>
			<tr>
				<td></td>
				<td><img src="<s:url value="/images/waiting.gif" />" /></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<%--for Change password panel--%>
<div id="changePasswordPanel" style="display: none;">
<div class="hd"><s:text
	name="topology.menu.operation.changePassword" /></div>
<div class="bd"><iframe id="changePasswordFrame"
	name="changePasswordFrame" width="0" height="0" frameborder="0" src="">
</iframe></div>
</div>
<script type="text/javascript">
var changePasswordPanel = null;
function createChangePasswordPanel(width, height){
	var div = document.getElementById("changePasswordPanel");
	width = width || 600;
	height = height || 400;
	var iframe = document.getElementById("changePasswordFrame");
	iframe.width = width;
	iframe.height = height;
	changePasswordPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
	changePasswordPanel.render(document.body);
	div.style.display="";
	overlayManager.register(changePasswordPanel);
	changePasswordPanel.beforeHideEvent.subscribe(clearChangePasswordData);
	changePasswordPanel.beforeShowEvent.subscribe(bringPanelToTop);
}
function clearChangePasswordData(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("changePasswordFrame").style.display = "none";
	}
}
function openChangePasswordPanel()
{
	var viewportWidth = YAHOO.util.Dom.getViewportWidth()*0.8;
	var viewportHeight = YAHOO.util.Dom.getViewportHeight()*0.8;
	if(viewportWidth >= 600){
		viewportWidth = 600;
	}
	if(viewportHeight >= 300){
		viewportHeight = 300;
	}
	
	if(null == changePasswordPanel){
		createChangePasswordPanel(viewportWidth,viewportHeight);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("changePasswordFrame").style.display = "";
	}
	changePasswordPanel.show();
	var iframe = document.getElementById("changePasswordFrame");
	iframe.src ="<s:url value='userPasswordModify.action' includeParams='none' />?operation=changePassword4Plan";
}
</script>