<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.MgmtServiceIPTrack"%>
<script>
var TRACK_IP_INTERVAL = <%=MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKIP%>
var TRACK_WAN_INTERVAL = <%=MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKWAN%>

var formName = 'mgmtIpTrack';
function onLoadPage() {
	if (!document.getElementById(formName + "_dataSource_trackName").disabled) {
		document.getElementById(formName + "_dataSource_trackName").focus();
	}
		<s:if test="%{jsonMode}">
		if(top.isIFrameDialogOpen()) {
			top.changeIFrameDialog(780, 560);
		}
		</s:if>
		if(document.getElementsByName('dataSource.groupType')[1].checked == true){
			document.getElementById("actionsIpTrack").style.display = "none";
		}
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
	    document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if (validate(operation)) {
		var url =  "<s:url action='mgmtIpTrack' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["mgmtIpTrack"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById("mgmtIpTrack"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveIpTrack, failure : resultDoNothing, timeout: 60000}, null);	
	}
}
var resultDoNothing = function(o) {
//	alert("failed.");
};

var succSaveIpTrack = function (o) {
	eval("var details = " + o.responseText);
	if (details.t) {
		if (details.n){
			if (details.pId=='networkPolicyMgtAdvancedSetting_routerIpTrackId') {
				hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), false, true);
				hm.util.insertSelectValue(details.nId, details.nName, parent.Get("leftOptions_ipTrackIds"), false, false);
			} else if (details.pId=='leftOptions_ipTrackIds') {
				/* hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), false, false);
				if (parent.Get("networkPolicyMgtAdvancedSetting_routerIpTrackId")) {
					hm.util.insertSelectValue(details.nId, details.nName, parent.Get("networkPolicyMgtAdvancedSetting_routerIpTrackId"), false, false);
				} */
				insertNewTrackIp(details);
			} else if(details.pId=='ipTrackWANList_ID' || details.pId=='primaryIpTrackId' || details.pId=='backup1TrackId' || details.pId=='backup2TrackId'){
				insertNewTrackIp(details);
			}
			else {
				hm.util.insertSelectValue(details.nId, details.nName, parent.document.getElementById(details.pId), false, true);
			}
		}else{
			//edit ip track, if the group type is changed ,the select must refresh
			updateTrackIpList(details);
		}
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}

function updateTrackIpList(details){
	if(details.gt == 1){
		removeIpTrackBackhaul(details);
		insertIpTrackWAN(details);
	}
	if(details.gt == 0){
		removeIpTrackWAN(details);
		insertIpTrackBackhaul(details);
	}
}

function removeIpTrackWAN(details){
	var ipTrackWANList = parent.Get("ipTrackWANList_ID");
	$(ipTrackWANList).find(" option[value='"+details.nId+"']").remove();
	
//	var routerIpTrack = parent.Get("networkPolicyMgtAdvancedSetting_routerIpTrackId");
//	$(routerIpTrack).find(" option[value='"+details.nId+"']").remove();
	
	var primaryIpTrackId = parent.Get("primaryIpTrackId");
	$(primaryIpTrackId).find(" option[value='"+details.nId+"']").remove();
	
	var backup1TrackId = parent.Get("backup1TrackId");
	$(backup1TrackId).find(" option[value='"+details.nId+"']").remove();
	
	var backup2TrackId = parent.Get("backup2TrackId");
	$(backup2TrackId).find(" option[value='"+details.nId+"']").remove();
}
function removeIpTrackBackhaul(details){
	var leftOptions_ipTrackIds = parent.Get("leftOptions_ipTrackIds");
	$(leftOptions_ipTrackIds).find(" option[value='"+details.nId+"']").remove();
}
function insertIpTrackWAN(details){
	var ipTrackWanList = parent.Get("ipTrackWANList_ID");
	if($(ipTrackWanList).find(" option[value='"+details.nId+"']").length ==0) {
		if(details.pId == "ipTrackWANList_ID") {
			hm.util.insertSelectValue(details.nId, details.nName, ipTrackWanList, false, true);
		} else {
			hm.util.insertSelectValue(details.nId, details.nName, ipTrackWanList, false, false);
		}
	}
		
	
//	var routerIpTrack = parent.Get("networkPolicyMgtAdvancedSetting_routerIpTrackId");
//	if (routerIpTrack) {
//		if($(routerIpTrack).find(" option[value='"+details.nId+"']").length ==0)
//			hm.util.insertSelectValue(details.nId, details.nName, routerIpTrack, false, false);
//	}
	var primaryIpTrackId = parent.Get("primaryIpTrackId");
	if (primaryIpTrackId) {
		if($(primaryIpTrackId).find(" option[value='"+details.nId+"']").length ==0) {
			if(details.pId == "primaryIpTrackId") {
				hm.util.insertSelectValue(details.nId, details.nName, primaryIpTrackId, false, true);
			} else {
				hm.util.insertSelectValue(details.nId, details.nName, primaryIpTrackId, false, false);
			}
		}
	}
	var backup1TrackId = parent.Get("backup1TrackId");
	if (backup1TrackId) {
		if($(backup1TrackId).find(" option[value='"+details.nId+"']").length ==0) {
			if(details.pId == "backup1TrackId") {
				hm.util.insertSelectValue(details.nId, details.nName, backup1TrackId, false, true);
			} else {
				hm.util.insertSelectValue(details.nId, details.nName, backup1TrackId, false, false);
			}
		}
	}
	var backup2TrackId = parent.Get("backup2TrackId");
	if (backup2TrackId) {
		if($(backup2TrackId).find(" option[value='"+details.nId+"']").length ==0) {
			if(details.pId == "backup2TrackId") {
				hm.util.insertSelectValue(details.nId, details.nName, backup2TrackId, false, true);
			} else {
				hm.util.insertSelectValue(details.nId, details.nName, backup2TrackId, false, false);
			}
		}
	}
}

function insertIpTrackBackhaul(details){
	var leftOptions_ipTracks = parent.Get("leftOptions_ipTrackIds");
	if($(leftOptions_ipTracks).find(" option[value='"+details.nId+"']").length ==0)
		hm.util.insertSelectValue(details.nId, details.nName, leftOptions_ipTracks, false, true);
	
//	var routerIpTrackId = parent.Get("networkPolicyMgtAdvancedSetting_routerIpTrackId")
//	if (routerIpTrackId) {
//		if($(routerIpTrackId).find(" option[value='"+details.nId+"']").length ==0)
//			hm.util.insertSelectValue(details.nId, details.nName, routerIpTrackId, false, false);
//	}
}
function insertNewTrackIp(details){
	if(details.gt== 0){
		insertIpTrackBackhaul(details);
	}else{
		insertIpTrackWAN(details);
	}
}
function validate(operation) {
	// fix bug 27533 , fix bug 27943 2013-07-18
	if('<%=Navigation.L2_FEATURE_MGMT_IP_TRACKING%>' == operation || operation == 'cancel<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_interval").value = "6";
		/* document.getElementById(formName + "_dataSource_timeout").value = "2"; */
		document.getElementById(formName + "_dataSource_retryTime").value = "2";
		return true;
	}
	var name = document.getElementById(formName + "_dataSource_trackName");
	var message = hm.util.validateName(name.value, '<s:text name="config.ip.track.name" />');
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}

	var ips = document.getElementById(formName + "_dataSource_ipAddresses");
	var ipValues = ips.value;
	var gateway = document.getElementById(formName + "_dataSource_useGateway");
	if (ipValues.length == 0 && !gateway.checked) {
		hm.util.reportFieldError(ips, '<s:text name="error.requiredField"><s:param><s:text name="config.ip.track.ipAddress" /></s:param></s:text>');
        ips.focus();
        return false;
	}
	if (ipValues.length > 0) {
		var validIps = new Array();
		var validChars = "0123456789.,";
		for (var k = 0; k < ipValues.length; k ++) {
			if (validChars.indexOf(ipValues.charAt(k)) < 0) {
				hm.util.reportFieldError(ips, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ip.track.ipAddress" /></s:param></s:text>');
				ips.focus();
				return false;
			}
		}
		var allIps = ipValues.split(",");
		var ipNum = gateway.checked ? 3 : 4;

		if (allIps.length > ipNum) {
			hm.util.reportFieldError(ips, '<s:text name="error.entryLimit"><s:param><s:text name="config.ip.track.ipAddress" /></s:param><s:param>'+ipNum+'</s:param></s:text>');
	        ips.focus();
	        return false;
		}
		for (var i = 0; i < allIps.length; i ++) {
			if (!hm.util.validateIpAddress(allIps[i]) || '255.255.255.255' == allIps[i]) {
				hm.util.reportFieldError(ips, '<s:text name="error.formatInvalid"><s:param>IP Address ('+allIps[i]+') </s:param></s:text>');
				ips.focus();
				return false;
			}
			for (var j = 0; j < validIps.length; j ++) {
				if (allIps[i] == validIps[j]) {
					hm.util.reportFieldError(ips, '<s:text name="error.config.mgmt.ip.track.sameIP"><s:param>'+allIps[i]+'</s:param></s:text>');
					ips.focus();
					return false;
				}
			}
			validIps.push(allIps[i]);
		}
	}

	var interval = document.getElementById(formName + "_dataSource_interval");
	/* var timeout = document.getElementById(formName + "_dataSource_timeout"); */
	var retry = document.getElementById(formName + "_dataSource_retryTime");
	if (!checkInputRange(interval, '<s:text name="config.ip.track.interval" />',
    								<s:property value="%{intervalRange.min()}" />,
    								<s:property value="%{intervalRange.max()}" />)) {
        return false;
    }
    /* if (!checkInputRange(timeout, '<s:text name="config.ip.track.timeout" />',
    							   <s:property value="%{intervalRange.min()}" />,
    							   parseInt(interval.value))) {
        return false;
   	} */
   	if (!checkInputRange(retry, '<s:text name="config.ip.track.retry" />',
    							 <s:property value="%{retryRange.min()}" />,
    							 <s:property value="%{retryRange.max()}" />)) {
        return false;
    }
	return true;
}

function checkInputRange(inputElement, title, min, max)
{
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }

	var message = hm.util.validateIntegerRange(inputElement.value, title, min, max);
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="mgmtIpTrack" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}

function showIpTrackActions(value){
	var show = document.getElementById("actionsIpTrack");
	if(value == 0){
		document.getElementById(formName + "_dataSource_interval").value = TRACK_IP_INTERVAL;
		show.style.display="block";
	}
	else{
		document.getElementById(formName + "_dataSource_interval").value = TRACK_WAN_INTERVAL;
		show.style.display="none";
	}
}
</script>
<div id="content"><s:form action="mgmtIpTrack" id="mgmtIpTrack">
	<s:if test="%{jsonMode==true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg" />
	</s:if>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode==false}">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<s:if test="%{dataSource.id == null}">
							<td><input type="button" name="create" value="<s:text name="button.create"/>"
								class="button"
								onClick="submitAction('create<s:property value="lstForward"/>');" <s:property value="writeDisabled" />>
							</td>
						</s:if>
						<s:else>
							<td><input type="button" name="update" value="<s:text name="button.update"/>"
								class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
								<s:property value="updateDisabled" />></td>
						</s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button" onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</s:if>
		<s:else>
		<tr>
			<td style="padding:10px 10px 10px 10px">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td align="left">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Tracking.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<td class="dialogPanelTitle">
								<s:if test="%{dataSource.id == null}">
									<s:text name="config.ip.track.title"/>
								</s:if>
								<s:else>
									<s:text name="config.ip.track.title.edit"/>
								</s:else>
								&nbsp;
							</td>
							<td>
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td align="right">
					<s:if test="%{!parentIframeOpenFlg}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<td class="npcButton">
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == 'disabled'}">
									&nbsp;</td>
								</s:if>
								<s:else>
									<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
								</s:else>
							</s:else>
						</tr>
					</table>
					</s:if>
					<s:else>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:if>
							<s:else>
								<s:if test="%{updateDisabled == ''}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
							</s:else>
						</tr>
					</table>
					</s:else>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="660">
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="660">
							<tr>
								<td class="labelT1" width="130"><label><s:text
									name="config.ip.track.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="48"
									name="dataSource.trackName" maxlength="%{nameLength}"
									disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'name');" />&nbsp;<s:text
									name="config.ssid.ssidName_range" /></td>
							</tr>
							<tr>
								<td class="labelT1" width="130px"><label><s:text
									name="config.localUser.description" /></label></td>
								<td><s:textfield size="48"
									name="dataSource.description" maxlength="%{commentLength}" />&nbsp;<s:text
									name="config.ssid.description_range" /></td>
							</tr>
							<tr style="padding:2px 0 2px 8px"> 
								<td style="padding:10px 15px 2px 5px" colspan="2">
									<fieldset>
										<table>
											<tr>
											    <s:if test="%{enableTrackIP == true}">
												<td><s:radio name="dataSource.groupType" list="#{'0':''}" value="dataSource.groupType" onclick="showIpTrackActions(value)"/></td>
												</s:if>
												<s:else>
												<td><s:radio name="dataSource.groupType" list="#{'0':''}" value="dataSource.groupType" onclick="showIpTrackActions(value)" disabled="true"/></td>
												</s:else>
												<td colspan="2"><s:text name="config.ip.track.type.remendial" /></td>
											</tr>
											<tr>
											    <s:if test="%{enableTrackWAN == true}">
												<td><s:radio name="dataSource.groupType" list="#{'1':''}" value="dataSource.groupType" onclick="showIpTrackActions(value)"/></td>
												</s:if>
												<s:else>
												<td><s:radio name="dataSource.groupType" list="#{'1':''}" value="dataSource.groupType" onclick="showIpTrackActions(value)" disabled="true"/></td>
												</s:else>
												<td colspan="2"><s:text name="config.ip.track.type.wan" /></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td style="padding:2px 0 2px 8px">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><s:checkbox name="dataSource.enableTrack" /></td>
											<td><s:text name="config.ip.track.enable" /></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding:0 15px 2px 5px">
					<fieldset>
						<legend><s:text name="config.ip.track.target.title" /></legend>
						<table>
							<tr>
								<td class="noteInfo" colspan="2"><s:text
									name="config.ip.track.ipAddress.allow" /></td>
							</tr>
							<tr>
								<td width="115"><label><s:text
									name="config.ip.track.ipAddress" /></label></td>
								<td><s:textfield size="70" name="dataSource.ipAddresses" maxlength="63"
									onkeypress="return hm.util.keyPressPermit(event,'ipTrack');" /></td>
							</tr>
							<tr>
								<td colspan="2">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><s:checkbox name="dataSource.useGateway" /></td>
											<td><s:text name="admin.interface.defaultGateway" /></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" width="130"><label><s:text
									name="config.ip.track.logic" /></label></td>
								<td><s:select name="dataSource.trackLogic" cssStyle="width: 280px;"
									list="%{enumTrackLogic}" listKey="key" listValue="value" /></td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="config.ip.track.interval" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield size="6" name="dataSource.interval" maxlength="3"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.ip.track.intervalRange" /></td>
							</tr>
							<%-- <tr>
								<td class="labelT1"><s:text name="config.ip.track.timeout" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield size="6" name="dataSource.timeout" maxlength="3"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.ip.track.timeoutRange" /></td>
							</tr> --%>
							<tr>
								<td class="labelT1"><s:text name="config.ip.track.retry" /><font color="red"><s:text name="*"/></font></td>
								<td><s:textfield size="6" name="dataSource.retryTime" maxlength="4"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
									name="config.ip.track.retryRange" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="actionsIpTrack">
					<td style="padding:0 15px 2px 5px">
						<fieldset>
						<legend><s:text name="config.ip.track.action" /></legend>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td height="6"></td>
							</tr>
							<tr>
								<td style="padding-left:5px"><s:checkbox name="dataSource.enableAccess" /></td>
								<td><s:text name="config.ip.track.action.enableAccess" /></td>
							</tr>
							<tr>
								<td height="6"></td>
							</tr>
							<tr>
								<td style="padding-left:5px"><s:checkbox name="dataSource.disableRadio" /></td>
								<td><s:text name="config.ip.track.action.disableRadio" /></td>
							</tr>
							<tr>
								<td height="6"></td>
							</tr>
							<tr>
								<td style="padding-left:5px"><s:checkbox name="dataSource.startFailover" /></td>
								<td>
									<s:text name="config.ip.track.action.startFailover" />
									<s:if test="%{!oEMSystem}">
										<s:text name="config.ip.track.action.startFailover.notSupport" />
									</s:if>
									
								</td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
</s:form></div>
