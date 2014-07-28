<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
  
<script>
var formName = 'appProfileForm';

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="appProfile" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{profile.id == null || profile.id == 0}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit </td>');
		</s:else>
	</s:else>
}
</script>

<div id="content">
    <s:form action="appProfile" id="appProfileForm" name="appProfileForm" method="post">
	<s:hidden name="profile.id" id="profile.id" value="%{profile.id}"/>
	<s:hidden name="selectedAppIds" id="selectedAppIds"/>
	<s:if test="%{jsonMode}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="id" />
		<s:hidden name="contentShowType" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="parentIframeOpenFlg"/>
	</s:if>
	
	<s:if test="%{jsonMode == true}">
		<div id="appProfiletitleDiv" style="margin-bottom:15px;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
						   <img src="<s:url value="/images/hm_v2/profile/HM-icon-Classifier_maps.png" includeParams="none"/>" width="40" height="40" alt="" class="dblk" />
						</td>
						<s:if test="%{profile.id == null || profile.id < 1}">
						<td class="dialogPanelTitle"><s:text name="config.title.appProfile"/></td>
						</s:if>
						<s:else>
						<td class="dialogPanelTitle"><s:text name="config.title.appProfile.edit"/></td>
						</s:else>
					</tr>
				</table>
				</td>
				<td align="right">			
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="npcButton"><a title="Cancel" onclick="parent.closeIFrameDialog();" style="float: right; margin-right: 20px;" class="btCurrent" href="javascript:void(0);">
						    <span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a>
						</td>
						<td class="npcButton">
						    <s:if test="%{profile.defaultFlag == false}">
						      <a title="Save" onclick="saveApp('ajax');" style="float: right;" id="saveBtnId" class="btCurrent" href="javascript:void(0);">
						      <span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a>
						    </s:if>
						</td>
				   </tr>
				</table>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="create" value="<s:text name="button.create"/>" class="button"
							onClick="saveApp('');"  <s:if test="%{profile.defaultFlag == true}">disabled="disabled"</s:if>
 							<s:property value="writeDisabled" />>
					</td>
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="operCancel();">
					</td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			     <table border="0" width="600" cellspacing="0" cellpadding="0" class="editBox">
			
				<tbody><tr>
					<td style="padding: 6px 4px 5px 4px;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tbody><tr>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tbody><tr>
									<td width="100" class="labelT1"><label><s:text name="config.appProfile.name" /><font color="red">*</font></label></td>
									<td><input type="text" onkeypress="return hm.util.keyPressPermit(event,'name');" <s:if test="%{profile.defaultFlag==true || profile.id > 0}">disabled="disabled"</s:if> id="profile.profileName" value="<s:property value='profile.profileName'/>" maxlength="32" size="24" name="profile.profileName">&nbsp;(1-32 characters)</td>
								</tr>
								<tr>
									<td colspan="3" style="padding:6px 0px 6px 0px">
										<table border="0" width="100%" cellspacing="0" cellpadding="0">
											<tbody><tr>
												<td class="sepLine"><img height="1" class="dblk" src="/hm/images/spacer.gif"></td>
											</tr>
										</tbody></table>
									</td>
								</tr>
								
								<tr>
									<td valign="top" style="padding:4px 0px 4px 4px;" colspan="3">									
									    <!--
									    <table border="0" width="100%" cellspacing="0" cellpadding="0" class="embedded">
									        <tr>
									        <th align="left" width="20" style="padding-left: 0;"><input type="checkbox" onclick="toggleCheckAllRules(this,'selectedIds');" id="checkAllDhcp"></th>
											<th align="left" width="200"><s:text name="config.application.name" /></th>
											<th align="left" width="200"><s:text name="config.application.shortname" /></th>
											<th align="left" width="200"><s:text name="config.application.code" /></th>
											<th align="left" width="200"><s:text name="config.application.groupname" /></th>
							                </tr>
									        <s:iterator value="profile.applicationList" status="status">
											    <tr>
										            <td class="listCheck"><input type="checkbox" checked value='<s:property value="id"/>' name="selectedIds"></td>
													<td width="100" class="list"><s:property value='appName'/></td>
													<td width="100" class="list"><s:property value='shortName'/></td>
													<td width="100" class="list"><s:property value='appCode'/></td>
													<td width="100" class="list"><s:property value='appGroupName'/></td>
												</tr>
											</s:iterator>	
											<s:iterator value="fixedAppList" status="status">
											    <tr>
										            <td class="listCheck"><input type="checkbox" value='<s:property value="id"/>' name="selectedIds"></td>
													<td width="100" class="list"><s:property value='appName'/></td>
													<td width="100" class="list"><s:property value='shortName'/></td>
													<td width="100" class="list"><s:property value='appCode'/></td>
													<td width="100" class="list"><s:property value='appGroupName'/></td>
												</tr>
											</s:iterator>		
									    </table>
									    -->
   									    <table id="appTable" cellspacing="0" cellpadding="0" border="0" width="100%" class="embedded">
												<tr>
													<th align="center" style="padding-left: 4px;">&nbsp;</th>
													<th align="right">&nbsp;</th>
													<th align="left">&nbsp;</th>
													<th align="left" style="padding-left: 3px;" ><s:text name="config.application.name" /></th>
													<th align="left" style="padding-left: 3px;" ><s:text name="config.application.shortname" /></th>
													<th align="left" style="padding-left: 3px;" ><s:text name="config.application.code" /></th>	
												</tr>
												
											    
											    <s:iterator value="%{applicationMap.keySet()}" id="groupName" status="outerStatus">
											          <s:set name="appGroupInfo" value="%{applicationMap.get(#groupName)}"/>
											          <tr style="display:" id='<s:property value="groupName"/>' class="even" parentId="root">
															<td align="center" class="listCheck">
															    <input type="checkbox" value="0" onclick="selectGroupApps(this, '<s:property value="groupName"/>');" <s:if test="%{#appGroupInfo.selected == true}">checked</s:if>>
															</td>								
															<td class="list" style="padding-left: 3px;">
																<a style="cursor:default;" href="javascript: toggleFeature('<s:property value="groupName"/>')"><img src="<s:url value="/images/expand_plus_line.gif" includeParams="none"/>" style="border:0;" alt="" width="16" height="11" /></a>
															</td>
															<td style="padding-left: 3px;" id="td33" colspan="4" class="list"><s:property value="groupName"/></td>
												      </tr>
										              <s:iterator value="%{#appGroupInfo.appList}" status="status">
										                  
										                  <tr style="display:none" id="fr34" class="odd" parentId='<s:property value="groupName"/>'>
																<td align="center" class="listCheck">
																    <s:if test="%{selected == true}">
																        <input type="checkbox" checked="checked" value='<s:property value="id"/>' name="selectedIds">
																    </s:if>
																    <s:else>
																        <input type="checkbox" value='<s:property value="id"/>'   name="selectedIds">
																    </s:else>
																    
																	
																</td>
																<td class="listCheck">&nbsp;</td>
																<td class="listCheck">
																    <!--last one is treenode_grid_l.gif-->
																    <s:if test="%{!#status.last}">
																        <img width="18" height="20" class="dblk" alt="" src="/hm/images/menutree/treenode_grid_t.gif">
																    <s:else>
																        <img width="18" height="20" class="dblk" alt="" src="/hm/images/menutree/treenode_grid_l.gif">
																    </s:else>
																    </s:if> 
																</td>
																<td class="list"><s:property value="appName"/></td>
																<td class="list"><s:property value="shortName"/></td>
							                                    <td class="list"><s:property value="appCode"/></td>
															</tr>
										              </s:iterator>
											 
							                     </s:iterator>

										</table>
								    </td>
							    </tr>	
								
								
							</tbody></table>
						</td>

						</tr>
					</tbody></table>
				</td>
	
			</tr>
			</tbody></table>
			     
		    </td>
	</tr>
	</table>
</s:form></div>

<script language="javascript">

var image_plus = hm.util.loadImage("<s:url value="/images/expand_plus_line.gif" includeParams="none"/>");
var image_minus = hm.util.loadImage("<s:url value="/images/expand_minus_line.gif" includeParams="none"/>");

YAHOO.util.Event.onDOMReady(function() {
	<s:if test="%{jsonMode == true}">
 	if(top.isIFrameDialogOpen()) {
 		top.changeIFrameDialog(710,560);
 	}
 	</s:if>
});


function selectGroupApps(groupCheckObj, groupId) {
	var trs = getDom('appTable').getElementsByTagName('tr');
	for (var i = 0; i < trs.length; i++) {
		if (trs[i].getAttribute("parentId") != null && trs[i].getAttribute("parentId") == groupId) {
			trs[i].getElementsByTagName("input")[0].checked = groupCheckObj.checked;
		}
	}
}

function toggleFeature(groupId) {
	//hm.util.show(); hm.util.hide();
	var currentImg = getDom(groupId).getElementsByTagName("img")[0];
	var trs = getDom('appTable').getElementsByTagName('tr');
	if (currentImg.src.indexOf('expand_plus_line.gif') > -1) {
		currentImg.src = image_minus.src;
		for (var i = 0; i < trs.length; i++) {
			if (trs[i].getAttribute("parentId") != null && trs[i].getAttribute("parentId") == groupId) {
				hm.util.show(trs[i]);
			}
		}
	}
	else {
		currentImg.src = image_plus.src;
		for (var i = 0; i < trs.length; i++) {
			if (trs[i].getAttribute("parentId") != null && trs[i].getAttribute("parentId") == groupId) {
				hm.util.hide(trs[i]);
			}
		}
	}
}


function operCancel() {
	document.forms[formName].operation.value = "";
	document.forms[formName].submit();
}

function insertAfter(newEl, targetEl) {
    var parentEl = targetEl.parentNode;
    if (parentEl.lastChild == targetEl) {
        parentEl.appendChild(newEl);
    } else {
        parentEl.insertBefore(newEl,targetEl.nextSibling);
    }            
}

function getDomValue(id) {
	return document.getElementById(id).value;
}

function getDom(id) {
	return document.getElementById(id);
}

function removeApp() {
	var table = getDom('selectAppSection').parentNode;
	var deleteTrs = new Array();
	var inputElements = document.getElementsByName("selectedIds");
	var n = inputElements.length;
	for(var i = 0; i < n; i++) {
		if(inputElements[i].type == "checkbox" &&  inputElements[i].checked == true) {
			deleteTrs.push(inputElements[i].parentNode.parentNode);
		}
	}
	for(var i = 0; i < deleteTrs.length; i++) {
		table.removeChild(deleteTrs[i]);
	}
}

function addApp() {    
    hm.util.hide('newButtonSection');
	hm.util.show('applyButtonSection');
	hm.util.show('selectAppSection');
}

function applyApp() {
	var selectAppSection = getDom('selectAppSection');
	var table = selectAppSection.parentNode;
	var selectIndex = getDom("appSelect").selectedIndex;
	var value = getDom("appSelect").options[selectIndex].value;
	var appName = getDom("appSelect").options[selectIndex].text;
	if (value.length < 1) {
		alert("app data error");
		return false;
	}
	var appId = value.substring(0, value.indexOf(","));
	var appCode = value.substring(value.indexOf(",") + 1);
	//alert(appName + " " + appId + " " + appCode);
	
	hm.util.hide('applyButtonSection');
	hm.util.show('newButtonSection');
	hm.util.hide('selectAppSection');
	var tr1 = document.createElement("tr");
	var td1 = document.createElement("td");
	td1.setAttribute("class","listCheck");		
	td1.innerHTML = '<input name="selectedIds" type="checkbox" value="' + appId  + '">';
	tr1.appendChild(td1);
	var td2 = document.createElement("td");
	td2.setAttribute("width","100");	
	td2.setAttribute("class","list");	
	td2.innerHTML = '<input name="appName" readOnly="true" type="text" size="30" value="' + appName  + '">';
	tr1.appendChild(td2);
	var td3 = document.createElement("td");
	td3.setAttribute("width","100");	
	td3.setAttribute("class","list");	
	td3.innerHTML = '<input name="appCode" readOnly="true" type="text" size="30" value="' + appCode  + '">';
	tr1.appendChild(td3);
	//table.appendChild(tr1);
	//table.insertBefore(tr1, selectAppSection);
	insertAfter(tr1, selectAppSection);
}

var resultDoNothing = function(o) {
//	alert("failed.");
};

var callbackSaveApp = function (o) {
	try {
		//alert(o.responseText);
		eval("var data = " + o.responseText);
		if (data.errMsg != null) {
			hm.util.displayJsonErrorNote(data.errMsg);
			return false;
		}
		if(data.profileId && data.profileId > 0) {
			var parentSelectDom = parent.document.getElementById(data.parentDomID);
			if(parentSelectDom != null && data.isCreateFlag == true) {
				hm.util.insertSelectValue(data.profileId, data.profileName, parentSelectDom, false, true);
			}
		}
		else {
			hm.util.displayJsonErrorNote("save failure");
		}
		parent.closeIFrameDialog();
	} catch (e) {
		hm.util.displayJsonErrorNote("error");
	}
}


function saveApp(saveType) {
	//var defaultFlag = "<s:property value='profile.defaultFlag'/>";
	//if (defaultFlag == 1) {
	//	showInfoDialog("Default applicaton profile can not be changed.");
	//return false;
	//}
	var profileNameObj = getDom("profile.profileName");
	var message = hm.util.validateName(profileNameObj.value, '<s:text name="config.appProfile.name" />');
	if (message != null) {
		hm.util.reportFieldError(profileNameObj, message);
		profileNameObj.focus();
    	return false;
	}
	
	//if (profileNameObj.value.trim().length == 0) {
	//	hm.util.reportFieldError(profileNameObj, '<s:text name="config.osObject.name" />');
	//	profileNameObj.focus();
    //	return false;
	//}
	
	var selectedIds = "";
	var inputElements = document.getElementsByName("selectedIds");
	for(var i = 0; i < inputElements.length; i++) {
		if(inputElements[i].type == "checkbox" &&  inputElements[i].checked == true) {
			selectedIds = selectedIds + inputElements[i].value + ",";
		}
	}
	if (selectedIds.length > 0) {
		selectedIds = selectedIds.substring(0, selectedIds.length - 1);
	}
	else {
		hm.util.reportFieldError(profileNameObj, 'Select one or more application');
		profileNameObj.focus();
    	return false;
	}
	getDom("selectedAppIds").value = selectedIds;
	document.forms[formName].operation.value = "save";
	
	if (saveType == "ajax") {
		var url =  "<s:url action='appProfile' includeParams='none' />" + "?jsonMode=true&ignore=" + new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("appProfileForm"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : callbackSaveApp, failure : resultDoNothing, timeout: 60000}, null);	
	    return false;	
	}
	document.forms[formName].submit();
}

function checkProfile(name, version,checkAll) {
	var table = document.getElementById(checkAll);
	if (version.value.trim().length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+name+'</s:param></s:text>');
        version.focus();
        return false;
    }
	return true;
}

function toggleCheckAllRules(cb,name) {
	var cbs = document.getElementsByName(name);
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}
</script>

