<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
	
<%@page import="com.ah.be.common.NmsUtil"%>

<style>
div.selectList {
    font-family: century gothic,helvetica light,helvetica,arial,sans serif;
    font-size:14px;
    font-weight:bold;
    height: 150px;
    margin: 0 auto;
    position: relative;
    text-align: left;
    width: 380px;
    background-color: #F9F9F7;
    padding-left: 5px;
}
div.selectListTitle {
    font-family: century gothic,helvetica light,helvetica,arial,sans serif;
    font-size:14px;
    font-weight:bold;
    height: 20px;
    margin: 0 auto;
    position: relative;
    text-align: left;
    width: 380px;
    background-color: #F9F9F7;
    padding-left: 5px;
}

.selectListTitle {
    font-family: century gothic,helvetica light,helvetica,arial,sans serif;
    font-size:14px;
    font-weight:bold;
    height: 20px;
    margin: 0 auto;
    position: relative;
    text-align: left;
    width: 380px;
    background-color: #F9F9F7;
    padding-left: 5px;
}
.serviceList{
 font-family: century gothic,helvetica light,helvetica,arial,sans serif;
    font-size:14px;
    font-weight:bold;
    height: 20px;
    margin: 0 auto;
    position: relative;
    text-align: left;
    background-color: #F9F9F7;
    padding-left: 5px;
}
.sharedspan{
    height: 20px;
    margin: 0 auto;
    position: relative;
    text-align: left;
    background-color: #F9F9F7;
    padding-left: 5px;
}
th a:link {
	text-decoration : none;
}
th a:HOVER {
	text-decoration : underline;
}

.wrap{
	word-break:break-all;
	word-wrap : break-word ; 
}
</style>

<script>
function onLoadPage(){
	createWaitingPanel();
}

var formName='bonjourGatewayMonitoring';
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}

function clickRealmItem(realmId,realmName,event) {
	
	// clear All status
	var selectedBddDevices = document.getElementsByName('selectedBddDevices');
	for(var i=0;i<selectedBddDevices.length;i++) {
		if(selectedBddDevices[i].value==-2){
			selectedBddDevices[i].checked = false;
		}
	}
	
	if(realmId==-1 && realmName=='<s:text name="config.optionsTransfer.none"/>'){
		return;
	}
	
	showProcessing();
	document.forms[formName].operation.value = 'clickRealm';
    document.forms[formName].submit();
}

function clickBddDeviceItem(bddDeviceId,event){
	clickAllMethod(bddDeviceId);
	if(bddDeviceId==-1){
		return;
	}
	showProcessing();
	document.forms[formName].operation.value = 'clickBddDevice';
    document.forms[formName].submit();
}

function clickAllMethod(bddDeviceId){
	var selectedBddDevices = document.getElementsByName('selectedBddDevices');
	if(bddDeviceId == -2){ //all
		var allChecked;
		for(var i=0;i<selectedBddDevices.length;i++) {
			if(selectedBddDevices[i].value==-2){
				allChecked = selectedBddDevices[i].checked;
			}
		}
		var table = document.getElementById("bddSelectList");
		var rows = table.getElementsByTagName("tr");
		for(var i=0; i<rows.length; i++) {
			var cell1 = rows[i].cells[0];
			cell1.firstChild.checked =allChecked;
		}
		
	} else {
		var allchecked = true;
		for(var i=0;i<selectedBddDevices.length;i++) {
			if(selectedBddDevices[i].value==-2){
				continue;
			}
			if(!selectedBddDevices[i].checked){
				allchecked = false;
				break;
			}
		}
		for(var i=0;i<selectedBddDevices.length;i++) {
			if(selectedBddDevices[i].value==-2){
				selectedBddDevices[i].checked = allchecked;
			}
		}
	}
}

function editBdd(id,event){
	showProcessing();
	document.forms[formName].operation.value = 'editNetworkPolicy';
	document.forms[formName].bgId.value = id;
    document.forms[formName].submit();
    
}

function editRealm(id,event){
	openRealmPanel(id);
}

function openRealmPanel(id){
	if(null == realmPanel){
		createRealmPanel();
	}
	if(null != realmPanel){
		document.getElementById('realmId').value = id;
		document.getElementById('realmName').value=getRealmName(id);
	}
	realmPanel.show();
}

function hideRealmPanel(){
	if(null != realmPanel){
		realmPanel.hide();
	}
}

function getRealmName(id){
	var name = id;
	name=document.getElementById("realmsSelectList"+id+"Span").firstChild.data;
	return name;
}

function submitRealm(){
	var realmId = document.getElementById('realmId').value;
	var realmName = document.getElementById('realmName').value;
	if(validate()){
		var url = "<s:url action='bonjourGatewayMonitoring' includeParams='none' />"+ "?realmId="+realmId +"&realmName="+realmName+"&operation=modifyRealmName"+"&ignore="+new Date().getTime(); 
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succModifyRealmName, failure : failModifyRealmName, timeout: 60000}, null);
	}
}

var timeoutId;
var hideError = function() {
	hm.util.wipeOut(document.getElementById("errNote"), 600);
}

var delayHideError = function(seconds) {
	timeoutId= setTimeout("hideError()", seconds * 5000);  // seconds
}

var succModifyRealmName = function (o){
	eval("var details = " + o.responseText);
	if (details.resultStatus == false) {
		var msg = details.resultMsg;
		if (msg==null || msg=='') {
			return;
		}
		clearTimeout(timeoutId);
		document.getElementById("errNote").innerHTML=msg;
		document.getElementById("errNote").className="noteError";
		hm.util.show("errNote");
		delayHideError(2);
	
		return;
	}else{
		if(details.realmName){
			var realmId = document.getElementById('realmId').value;
			document.getElementById('realmsSelectList'+realmId+'Span').innerHTML = details.realmName;
			hideRealmPanel();
		}
	}
}

var failModifyRealmName = function(o){
	
}

var realmPanel = null;
function createRealmPanel(){
	var div = window.document.getElementById('realmPanel');
	realmPanel = new YAHOO.widget.Panel(div, { width:"300px", visible:false, fixedcenter:true, draggable:true, constraintoviewport:true } );
	realmPanel.render(document.body);
	div.style.display = "";
	overlayManager.register(realmPanel);
	realmPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function submitAction(operation) {
	showProcessing();
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
	
}

function validate(){
	var realmName = document.getElementById('realmName');
	if(realmName.value.length ==0){
		hm.util.reportFieldError(realmName, '<s:text name="error.requiredField"><s:param><s:text name="monitor.bonjour.gateway.realm.modifyname" /></s:param></s:text>');
		return false;
	}
	return true;
}

function sortColItem(name){
	showProcessing();
	document.forms[formName].orderBy.value=name;
	document.forms[formName].operation.value = 'sort';
    document.forms[formName].submit();
}

function getSelectedBddIds(){
	var selectedBddIds='';
	var selectedBddDevices = document.getElementsByName('selectedBddDevices');
	for(var i=0;i<selectedBddDevices.length;i++) {
		if(selectedBddDevices[i].checked){
			selectedBddIds=selectedBddIds+selectedBddDevices[i].value+',';
		}
	}
	if(selectedBddIds != ''){
		selectedBddIds = selectedBddIds.substring(0,selectedBddIds.length-1);
	}
	return selectedBddIds;
}

function getSelectRealmIds(){
	var selectedRealmIds='';
	var selectedRealms = document.getElementsByName('selectedRealms');
	for(var i=0;i<selectedRealms.length;i++) {
		if(selectedRealms[i].checked){
			var textValue =document.getElementById('realmsSelectList'+selectedRealms[i].value+'Span').innerHTML;
			if(selectedRealms[i].value != -1 ||textValue !='<s:text name="config.optionsTransfer.none"/>'){
				selectedRealmIds=selectedRealms[i].value;
			}	
		}
	}
	return selectedRealmIds;
}

function submitRefresh(operation){
	var selectedBddIds = getSelectedBddIds();
	var selectedRealmIds = getSelectRealmIds();
	if(selectedRealmIds == ''){
		showInfoDialog("Please select one realm at least.");
		return;
	}
	if(waitingPanel != null){
		waitingPanel.setHeader("Refreshing...");
		waitingPanel.show();
	}
	var url = "<s:url action='bonjourGatewayMonitoring' includeParams='none' />"+ "?selectedBddIds="+selectedBddIds +"&selectedRealmIds="+selectedRealmIds+"&operation=refresh"+"&ignore="+new Date().getTime(); 
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succRefresh, failure : failRefresh, timeout: 60000}, null);
	
}
var succRefresh = function (o){
	
	eval("var result = " + o.responseText);
	setTimeout(refresh(result),3000);
}

function refresh(result){
	return function(){
		if(waitingPanel != null)
		{
			waitingPanel.hide();
		}
		
		if(result.success == 0)
		{
			document.getElementById('bonjourGatewayMonitoring_selectedBddMacs').value=result.selectedBddMacs
			submitAction('view');
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
		} else if(result.success == 3){
			showInfoDialog("Please select one realm at least.")
		}
	}
}

var failRefresh = function (o){
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}
}
</script>

<div id="content">
	<s:form action="bonjourGatewayMonitoring">
		<s:hidden name="bgId"/>
		<s:hidden name="orderBy"/>
		<s:hidden name="ascending"/>
		<s:hidden name="selectedBddMacs"/>
		<table>
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td>
					<table width="570" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="button" name="ignore" value="Refresh"
							class="button"
							onClick="submitRefresh('refresh');"
							<s:property value="writeDisabled" />>
							<span style="background-color:#F9F9F7;">Update time:<s:property value="updateTime" /></span></td>
					</tr>
					<tr>
						<td colspan="2" height="10px">
									
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
								<td width="400px;">
								<div class="selectListTitle" ><s:text name="monitor.bonjour.gateway.realmslist.title"/></div>
									<div class="selectList" id="realmsSelectListTable">
										<ah:checkList name="selectedRealms" list="availableRealms" listKey="id" listValue="value" value="selectedRealms"
										 editMenuText="Change Realm Name&nbsp;" menuContainerStyle="width: auto;right: -100px;z-index:1;" 
										 height="150px" width="100%" itemWidth="280px" multiple="false" clickEvent="clickRealmItem" containerId="realmsSelectList" listKeyIsString="true"/><!-- editEvent="editRealm" -->
									</div>
								</td>
								<td width="400px;" style="padding-left: 10px;">
								<div class="selectListTitle"><s:text name="monitor.bonjour.gateway.bddlist.title"/></div>
									<div class="selectList" id="bddSelectListTable">
										<ah:checkList name="selectedBddDevices" list="availableBddDevices" listKey="id" listValue="value" value="selectedBddDevices"
										 editMenuText="View Network Policy&nbsp;" menuContainerStyle="width: auto;right: -100px;"
										 height="150px" width="100%" itemWidth="280px" multiple="true" containerId="bddSelectList" clickEvent="clickBddDeviceItem" editEvent="editBdd"/>
									</div>
								</td>
								<td width="100px;">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3" height="10px">
									
								</td>
							</tr>
							<tr>
								<td colspan="3" style="padding-left: 10px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="serviceList" width="300px" valign="bottom"><s:text name="monitor.bonjour.gateway.servicelist.title"/>&nbsp;
												<s:text name="monitor.bonjour.gateway.service.total"><s:param><s:property value="serviceDetailLength"/></s:param></s:text>
											</td>
											<td class="sharedspan" valign="bottom">
												<s:checkbox name="chkShared" id="chkShared" onclick="submitAction('checkShared');" />
												<s:text name='monitor.bonjour.gateway.service.shared.only'/>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="3" style="padding-left: 10px;">
									<table border="0" cellspacing="0" cellpadding="0" width="100%" style="table-layout:fixed">
										<tr class="serviceList" >
											<th width="50px" align="center">
												<s:text name="monitor.bonjour.gateway.service.shared"/></th>
											<th width="100px" align="left">
											<a href="javascript:void(0);" onclick="sortColItem('type');"><s:text name="monitor.bonjour.gateway.service.type"/></a>
												<s:if test="%{itemOderby && orderBy=='type'}">
													<img src="<s:url value="/images/spacer.gif" includeParams="none"/>"	width="5" class="dinl" />
													<s:if test="ascending">
														<img src="<s:url value="/images/sorting/arrow_up.png" includeParams="none"/>" class="dinl" />
													</s:if>
													<s:else>
														<img src="<s:url value="/images/sorting/arrow_down.png" includeParams="none"/>" class="dinl" />
													</s:else>
												</s:if>
											</th>
											<th width="200px" align="left"><s:text name="monitor.bonjour.gateway.service.name"/></th>
											<th width="100px" align="center">
												<a href="javascript:void(0);" onclick="sortColItem('vlan');"><s:text name="monitor.bonjour.gateway.service.vlan"/></a>
												<s:if test="%{itemOderby && orderBy=='vlan'}">
													<img src="<s:url value="/images/spacer.gif" includeParams="none"/>"	width="5" class="dinl" />
													<s:if test="ascending">
														<img src="<s:url value="/images/sorting/arrow_up.png" includeParams="none"/>" class="dinl" />
													</s:if>
													<s:else>
														<img src="<s:url value="/images/sorting/arrow_down.png" includeParams="none"/>" class="dinl" />
													</s:else>
												</s:if>
											</th>
											<th width="150px">
												<s:text name="monitor.bonjour.gateway.service.vlangroup"/>
											</th>
											<%-- <th width="100px" align="left"><s:text name="monitor.bonjour.gateway.service.device"/></th> --%>
											<th width="110px" align="left"><s:text name="monitor.bonjour.gateway.service.remotebdd"/></th>
										</tr>
										<s:iterator value="%{bonjourServiceDetails}" status="status" id="serviceDetail">
											<tr class="serviceList" >
												<td class="list" valign="top" align="center" width="50px">
													<s:if test="shared">
														<img src="<s:url value="/images/check.png" includeParams="none"/>" width="20" height="20" />
													</s:if>
													<s:else>
														&nbsp;
													</s:else>
												</td>
												<td class="list wrap" valign="top"  align="left" width="100px"><s:property value="type" /></td>
												<td class="list wrap" valign="top"  align="left" width="200px">
													<table border="0" cellspacing="0" cellpadding="0" width="100%">
														<tr>
															<td>
																<script type="text/javascript">insertFoldingLabelContext("<s:property value="name" />",'serviceDetail_<s:property value="#status.index"/>' );</script>
															</td>
														</tr>
														<tr>
															<td style="padding-left: 30px;">
																<div id="serviceDetail_<s:property value="#status.index"/>" style="display: none;">
																	<table border="0" cellspacing="0" cellpadding="0" width="100%" style="table-layout:fixed">
																		<tr>
																			<td class="wrap"><s:property value="ip4Str" /></td>
																		</tr>
																		<tr>
																			<td class="wrap"><s:property value="text" /></td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
													</table>
												</td>
												<td class="list wrap" valign="top"  align="center" width="100px"><s:property value="vlan" /></td>
												<td class="list wrap" valign="top"  align="left" width="150px">
														<s:iterator value="%{#serviceDetail.vlanGroups}" status="status1" id="vlanGroup" >
															<s:if test="vlanGroupIsAny">
																<div><s:property value="vlanGroupName" /></div>
															</s:if>
															<s:else>
																<div>
																	<table  border="0" cellspacing="0" cellpadding="0" width="100%">
																		<tr>
																			<td>
																				<script type="text/javascript">insertFoldingLabelContext('<s:property value="vlanGroupName" />','vlanGroupName_<s:property value="#status.index"/>_<s:property value="#status1.index"/>' );</script>
																			</td>
																		</tr>
																		<tr>
																			<td style="padding-left: 30px;">
																				<div id="vlanGroupName_<s:property value="#status.index"/>_<s:property value="#status1.index"/>" style="display: none;">
																					<table border="0" cellspacing="0" cellpadding="0" width="100%" style="table-layout:fixed">
																						<tr>
																							<td class="wrap"><s:property value="vlans" /></td>
																						</tr>
																					</table>
																				</div>
																			</td>
																		</tr>
																	</table>
																</div>
															</s:else>
														</s:iterator>
												</td>
												<%-- <td class="list wrap" valign="top"  align="left" width="100px"><s:property value="host" /></td> --%>
												<td class="list wrap" valign="top"  align="left" width="100px"><s:property value="shareRomoteBdd" /></td>
											</tr>
										</s:iterator>
										<tr class="serviceList" >
											<td height="10px" colspan="8"></td>
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
	</s:form>
</div>
<div id="realmPanel" style="display: none;">
	<div class="hd">
		<s:text name="monitor.bonjour.gateway.realm.modifyname.title" />
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
											<td></td>
											<td><div id="errNote" style="display: none;"></div></td>
										</tr>
										<tr>
											<td><s:text name="monitor.bonjour.gateway.realm.modifyname" /></td>
											<td><s:textfield name="realmName" maxlength="128" onkeypress="return hm.util.keyPressPermit(event,'name');"></s:textfield>
												<s:hidden id="realmId"/>
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
							<td><input type="button" name="ignore" value="Submit"
								class="button"
								onClick="submitRealm();">
							</td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="hideRealmPanel();">
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
	</div>
</div>

<script>
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
	waitingPanel.setHeader("Refresh...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}
</script>