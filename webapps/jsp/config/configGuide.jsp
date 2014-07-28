<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
ul.operation {
	margin: 0px; 
	padding-left: 10px; 
	padding-top: 10px;
	list-style-type: disc;
}
#guideContent {
	line-height: 1.5em;
	padding: 0px 5px 10px 5px;
}

ul.guideList{
	margin: 10px 0;
	padding-left: 25px;
	list-style-type: circle;
	color: #0A4687;
}
ul.guideList li{
	padding: 5px 15px 5px 2px;
}
ul.guideList a{
	text-decoration: underline;
	color: #003366;
	font-weight: bold;
}
ul.guideList a:HOVER{
	text-decoration: none;
}

a.guideTitle{
	font-weight: bold;
	font-size: 12px;
	color: #003366;
	text-decoration: underline;
}

a.guideTitle:HOVER{
	text-decoration: none;
}

b.countItem {
	font-weight: normal;
}

a.addNew {
	font-style: normal;
}

.infoList td {
	border-bottom: 1px dashed #E1E1E1;
	padding: 10px 15px 20px;
}

.current-item {
	background: #EEEEEE none repeat scroll 0%;
}

.infoList .current-item td {
	border-bottom: 1px solid #C0DAFF;
}
</style>
<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>
<script type="text/javascript">
var formName = 'configGuide';

function submitAction(operation) {
	if (validate(operation)){
		if("guideHelps" == operation){
			openHelpPage();
		}else{
			document.forms[formName].operation.value = operation;
			hm.options.selectAllOptions('hiveApList');
		    document.forms[formName].submit();
		}
	}
}

function validate(operation){
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

// HiveAP list pannel
var hiveApListPanel = null;
function createHiveApListPanel(){
	var div = window.document.getElementById('hiveApListPanel');
	hiveApListPanel = new YAHOO.widget.Panel(div, { width:"360px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	hiveApListPanel.render();
	div.style.display = "";
}

function openHiveApListPanel() {
	if(null == hiveApListPanel){
		createHiveApListPanel();
	}
	
	hiveApListPanel.show();	
}

function hideHiveApListPanel(){
	if(null != hiveApListPanel){
		hiveApListPanel.hide();
	}	
}
// HiveAP list pannel end


function showWaitMessage() {
	Get("tellFriendNoteId").className="noteInfo";
	Get("tellFriendNoteId").innerHTML = "Your message is being sent...";
	Get("tellFriendNoteDiv").style.display="";
}

// Tell-Friend Panel Begin ===============================================

var tellFriendPanel = null;
function createTellFriendPanel(){
	var div = window.document.getElementById('tellFriendPanel');
	tellFriendPanel = new YAHOO.widget.Panel(div, { width:"368px", visible:false, fixedcenter:"contained", draggable:true, constraintoviewport:true } );
	tellFriendPanel.render(document.body);
	div.style.display = "";
	tellFriendPanel.beforeHideEvent.subscribe(clearFriendContent);
}

function clearFriendContent() {
	Get("mailAddress").value = "";
	Get("shareAccount").checked = false;
	Get("personalNote").value = "";
	Get("tellFriendNoteDiv").style.display = "none";
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
	var element = Get("mailAddress");
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
				+ "&shareAccount=" + Get("shareAccount").checked + "&personalNote="
				+ Get("personalNote").value.replace(/\n/g, "<br>");
				
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, 
		{ success:tellFriendOk,failure:tellFriendFailed,timeout: 100000}, null);
}

var tellFriendOk = function(o) {
	eval("var result = " + o.responseText);
	
	if(result.success)
		Get("tellFriendNoteId").className="noteInfo";
	else 
		Get("tellFriendNoteId").className="noteError";
	
	
	Get("tellFriendNoteId").innerHTML = result.msg;
	Get("tellFriendNoteDiv").style.display="";
};

var tellFriendFailed = function(o) {
};


// Tell-Friend Panel End ===============================================


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
		obj.overImg = hm.util.loadImage(obj.over);
		obj.outImg = hm.util.loadImage(obj.out);
	}
}

function mouseOverRow(){
	this.className = 'current-item';
	var firstChild = this.cells[0].firstChild;
	if(firstChild && firstChild.src && this.id){
		if(YAHOO.env.ua.ie){
			firstChild.src = guidedObjects[this.id].over;
		}else{//reduce request
			this.cells[0].replaceChild(guidedObjects[this.id].overImg, firstChild);
		}
	}
}

function mouseOutRow(){
	this.className = '';
	var firstChild = this.cells[0].firstChild;
	if(firstChild && firstChild.src && this.id){
		if(YAHOO.env.ua.ie){
			firstChild.src = guidedObjects[this.id].out;
		}else{//reduce request
			this.cells[0].replaceChild(guidedObjects[this.id].outImg, firstChild);
		}
	}
}

</script>

<div id="content"><s:form action="configGuide">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" width="740px">
					<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="labelH1"
								style="font-size: 16px; background: #EEEEEE; padding-top: 20px; padding-left: 20px; padding-bottom: 5px;"><s:text
								name="hm.config.guide.title" /></td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td class="labelT">
							<div id="guideContent">
							<table id="infoList" class="infoList" width="100%" border="0"
								cellspacing="0" cellpadding="0">
								<%--
								<tr id="guideHelpObj">
									<td valign="top" width="65px"><img
										alt="<s:text
									name="hm.config.guide.hiveAp.title" />"
										src="<s:url value="/images/DocCBT.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#guideHelps"
										onclick="submitAction('guideHelps')"><s:text
										name="hm.config.guide.help.title" /></a>:
									<s:text name="hm.config.guide.help" /><br>
									<div style="padding-top:10px;">
									<a class="addNew" href='#guideHelps'
												onclick="submitAction('guideHelps')"> <s:text
												name="hm.config.guide.help.list" /></a>
									</div></td>
								</tr> --%>
								<%-- if the license is invalid, not show the section --%>
								<s:if test="%{showHiveApInfo}">
								<tr id="hiveApObj">
									<td valign="top" width="65px"><img
										alt="<s:text
									name="hm.config.guide.hiveAp.title" />"
										src="<s:url value="/images/HiveAPs.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configHiveAp"
										onclick="submitAction('configHiveAp')"><s:text
										name="hm.config.guide.hiveAp.title" /></a>:
									<s:text name="hm.config.guide.hiveAp" /><br>
									<s:property value="%{hiveApDetail}" escape="false" /></td>
								</tr>
								</s:if>
								<s:if test="%{fullMode}">
									<tr id="userProfileObj">
										<td valign="top" width="65px"><img
											alt="<s:text
									name="hm.config.guide.userProfile.title" />"
											src="<s:url value="/images/UserProfiles.png" includeParams="none"/>" /></td>
										<td valign="top"><a class="guideTitle" href="#configUserProfile"
											onclick="submitAction('configUserProfile')"><s:text
											name="hm.config.guide.userProfile.title" /></a>:
										<s:text name="hm.config.guide.userProfile" /><br>
										<!-- 
										<s:property value="%{userProfileDetail}" escape="false" /> <s:iterator
											value="userProfileItems" status="status">
											<s:if test="%{showDomain}">
												<s:if test="%{#status.last}">
													<a
														href='<s:url value="userProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
													<s:property value="userProfileName" /></a>.)
												</s:if>
												<s:else>
													<a
														href='<s:url value="userProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
													<s:property value="userProfileName" /></a>, 
												</s:else>
											</s:if>
											<s:else>
												<s:if test="%{#status.last}">
													<a
														href='<s:url value="userProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
													<s:property value="userProfileName" /></a>.)
												</s:if>
												<s:else>
													<a
														href='<s:url value="userProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
													<s:property value="userProfileName" /></a>, 
												</s:else>
											</s:else>
										</s:iterator> <br> -->
										<ul class="operation">
											<li><a class="addNew" href='#addUserProfile'
												onclick="submitAction('addUserProfile')"> <s:text
												name="hm.config.guide.userProfile.addNew" /></a></li>
											<li><a class="addNew" href='#configUserProfile'
												onclick="submitAction('configUserProfile')"> <s:text
												name="hm.config.guide.userProfile.list" /></a></li>
										</ul>
										</td>
									</tr>
								</s:if>
								<tr id="ssidObj">
									<td valign="top" width="65px"><img
										alt="<s:text
									name="hm.config.guide.ssid.title" />"
										src="<s:url value="/images/SSIDs.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configSsid"
										onclick="submitAction('configSsid')"><s:text
										name="hm.config.guide.ssid.title" /></a>:
									<s:text name="hm.config.guide.ssid" /><br>
									<!-- 
									<s:property value="%{ssidDetail}" escape="false" /> <s:iterator
										value="ssidItems" status="status">
										<s:if test="%{showDomain}">
											<s:if test="%{#status.last}">
												<a
													href='<s:url value="ssidProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
												<s:property value="ssidName" /></a>.)
											</s:if>
											<s:else>
												<a
													href='<s:url value="ssidProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
												<s:property value="ssidName" /></a>, 
											</s:else>
										</s:if>
										<s:else>
											<s:if test="%{#status.last}">
												<a
													href='<s:url value="ssidProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
												<s:property value="ssidName" /></a>.)
											</s:if>
											<s:else>
												<a
													href='<s:url value="ssidProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
												<s:property value="ssidName" /></a>, 
											</s:else>
										</s:else>
									</s:iterator> <br> -->
									<ul class="operation">
										<li><a class="addNew" href='#addSSID'
											onclick="submitAction('addSsid')"> <s:text
											name="hm.config.guide.ssid.addNew" /></a></li>
										<li><a class="addNew" href='#configSsid'
											onclick="submitAction('configSsid')"> <s:text
											name="hm.config.guide.ssid.list" /></a></li>
									</ul>
									</td>
								</tr>
								<s:if test="%{showUserSection}">
									<tr id="ludObj">
										<td valign="top" width="65px"><img
											alt="<s:text
									name="hm.config.guide.user.title" />"
											src="<s:url value="/images/LocalUserDB.png" includeParams="none"/>" /></td>
										<td valign="top"><a class="guideTitle" href="#configUser"
											onclick="submitAction('configUser')"><s:text
											name="hm.config.guide.user.title" /></a>:
										<s:text name="hm.config.guide.user" /><br>
										<s:property value="%{userDetail}" escape="false" /></td>
									</tr>
								</s:if>
								<s:if test="%{fullMode}">
									<tr id="wlanObj">
										<td valign="top" width="65px"><img
											alt="<s:text
									name="hm.config.guide.wlanPolicy.title" />"
											src="<s:url value="/images/WLANpolicies.png" includeParams="none"/>" /></td>
										<td valign="top"><a class="guideTitle" href="#configWlanPolicy"
											onclick="submitAction('configWlanPolicy')"><s:text
											name="hm.config.guide.wlanPolicy.title" /></a>:
										<s:text name="hm.config.guide.wlanPolicy" /><br>
										<!-- 
										<s:property value="%{wlanPolicyDetail}" escape="false" /> <s:iterator
											value="wlanPolicyItems" status="status">
											<s:if test="%{showDomain}">
												<s:if test="%{#status.last}">
													<a
														href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
													<s:property value="configName" /></a>.)
												</s:if>
												<s:else>
													<a
														href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'>
													<s:property value="configName" /></a>, 
												</s:else>
											</s:if>
											<s:else>
												<s:if test="%{#status.last}">
													<a
														href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
													<s:property value="configName" /></a>.)
												</s:if>
												<s:else>
													<a
														href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{id}"/></s:url>'>
													<s:property value="configName" /></a>, 
												</s:else>
											</s:else>
										</s:iterator> <br>-->
										<ul class="operation">
											<li><a class="addNew" href='#addWlanPolicy'
												onclick="submitAction('addWlanPolicy')"> <s:text
												name="hm.config.guide.wlanPolicy.addNew" /></a></li>
											<li><a class="addNew" href='#configWlanPolicy'
												onclick="submitAction('configWlanPolicy')"> <s:text
												name="hm.config.guide.wlanPolicy.list" /></a></li>
										</ul>
										</td>
									</tr>
								</s:if>
								<%-- if the license is invalid, not show the section --%>
								<s:if test="%{showHiveApInfo}">
								<tr id="hiveApUpdateObj">
									<td valign="top" width="65px"><img
										alt="<s:text
									name="hm.config.guide.hiveAp.updates.title" />"
										src="<s:url value="/images/Update.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configHiveApUpdates"
										onclick="openHiveApListPanel();"><s:text
										name="hm.config.guide.hiveAp.updates.title" /></a>: <s:text
										name="hm.config.guide.hiveAp.updates" /><br>
									<s:property value="%{updateDetail}" escape="false" /></td>
								</tr>
								</s:if>
								<%--<s:if test="%{HMOnline}">
								<tr id="contactObj">
									<td valign="top" width="65px" style="border-top: 1px solid #999;"><img
										alt="<s:text name="hm.config.guide.support" />"
										src="<s:url value="/images/Sales-Support.png" includeParams="none"/>" /></td>
									<td valign="top" style="border-top: 1px solid #999;"><a class="guideTitle" href="#support"
										onclick="openContactSupportPanel();"><s:text
										name="hm.config.guide.support" /></a>: <s:text
										name="hm.config.guide.support.description" /></td>
								</tr>
								<s:if test="%{!userContext.superUser}">
								<tr id="friendObj">	
									<td valign="top" width="65px"><img
										alt="<s:text name="hm.config.guide.tellFriend" />"
										src="<s:url value="/images/Tell-Friend.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#tellFriend"
										onclick="openTellFriendPanel();"><s:text
										name="hm.config.guide.tellFriend" /></a>: <s:text
										name="hm.config.guide.tellFriend.description" /></td>
								</tr>
								</s:if>
								</s:if>--%>
							</table>
							</div>
							</td>
						</tr>
					</table>
					</td>
					<s:if test="%{!oEMSystem}">
					<td width="5px"> </td>
					<td valign="top" width="180px">
					<table class="guideBox" border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="labelH1"
								style="font-size: 14px; padding-top: 20px; padding-left: 10px; padding-bottom: 5px;"><s:text
								name="hm.config.guide.help.window.title" /></td>
						</tr>
						<tr>
							<td>
								<ul class="guideList">
									<li><a href="#guideHelps"
										onclick="submitAction('guideHelps')"><s:text
										name="hm.config.guide.help.title" /></a>: <s:text name="hm.config.guide.help" /></li>
									<li><a href="#video"
										onclick="clickHelpVideoMenu();"><s:text
										name="hm.config.guide.video.title" /></a>: <s:text
										name="hm.config.guide.video.description" /></li>
									<s:if test="%{HMOnline}">
									<li><a href="#support"
										onclick="openContactSupportPanel();"><s:text
										name="hm.config.guide.support" /></a>: <s:text
										name="hm.config.guide.support.description" /></li>
									<s:if test="%{!userContext.superUser}">
									<li><a href="#tellFriend"
										onclick="openTellFriendPanel();"><s:text
										name="hm.config.guide.tellFriend" /></a>: <s:text
										name="hm.config.guide.tellFriend.description" /></li>
									</s:if>
									</s:if>
								</ul>
							</td>
						</tr>
					</table>
					</td>
					</s:if>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<div id="hiveApListPanel" style="display: none;">
		<div class="hd"><s:text name="hm.config.guide.updateHiveAp.list"/></div>
		<div class="bd">
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
					<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<s:push value="%{hiveApOptions}">
								<td style="padding:6px" colspan="3"><tiles:insertDefinition name="optionsTransfer"/></td>
							</s:push>
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
							<td><input type="button" name="ignore" value="Update"
								class="button" onClick="submitAction('configHiveApUpdates');"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideHiveApListPanel();"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
</s:form></div>

<div id="tellFriendPanel" style="display: none;">
	<div class="hd"><s:text name="hm.config.guide.tellFriend"/></div>
	<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
				<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<div id="tellFriendNoteDiv" style="display:none">
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
									<td>
										<s:text	name="hm.config.guide.tellFriend.email" /><font color="red">*</font>
									</td>
									<td>
										<s:textfield id="mailAddress" size="32" />
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
									<table>
										<tr>
											<td>
												<s:checkbox id="shareAccount" name="share" />
											</td>
											<td>
												<s:text	name="hm.config.guide.tellFriend.share" />
											</td>
										</tr>
									</table>	
									</td>
								</tr>
								<tr>
									<td valign="top">
										<s:text	name="hm.config.guide.tellFriend.note" />
									</td>
									<td>
										<s:textarea cssStyle="width: 190px;" id="personalNote" rows="5"/>
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