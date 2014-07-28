<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style>
td.listString {
	padding-right: 25px;
	font-size: 14px;
	padding-bottom: 8px;
	padding-top: 3px;
	cursor: pointer;
}
table.view tr.even.canHover.hover {
	background-color: #FFE43F;
}
table.view tr.odd.canHover.hover {
	background-color: #FFE43F;
}
table.view tr.disabled td {
	color: #BDBAAB;
	cursor: default;
}
#up_vlan_mapping_modify_container.yui-panel {
	border: none;
	overflow: visible;
	background-color: whiteSmoke;
	padding-bottom: 3px;
}
</style>
<script>
var formName = 'userProfileVlanMappingForm';
function onLoadPage() {
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(500,450);
	 	}
 	</s:if>
}
</script>

<div id="content">
<s:form action="networkPolicy" name="userProfileVlanMappingForm" id="userProfileVlanMappingForm">
	<s:hidden name="operation" />
	<s:hidden name="vlanId" />
	<s:hidden name="upVlanMappingType" />
	<s:hidden name="upVlanRelativeId" />
	<s:if test="%{jsonMode == true && contentShownInDlg == true}">
		<div id="userProfileVlanMappingDlgTitleDiv" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td>
					<table>
						<tr>
							<td width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="dialogPanelTitle"><s:text name="config.title.userprofile.vlan.mapping"/></td>
									<td style="padding-left:10px;">
										<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
											<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
												alt="" class="dblk"/>
										</a>
									</td>
								</tr>
							</table>
							</td>
							<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="right" style="padding-left:10px;" width="80px" nowrap><a id="close_dlg_link" href="javascript:void(0);" class="btCurrent"
										onclick="javascript: hideUserProfileVlanMappingDlg();" title="close"><img class="dinl" width="16px" height="16px" src="images/cancel.png" style="border:none;"/></a></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</div>
		<div style="width:100%; height: 320px; overflow: auto;">
		<s:if test="%{jsonMode == true && contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
				<tr>
					<td>
						<div>
							<div style="font-size: 12px; color: #999; padding-left: 10px; padding-bottom: 2px;">
								<s:text name="info.tip.userprofile.vlan.mapping.override.warn"/>
							</div>
							<table id="up_vlan_container_tbl" class="view" cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<th style="width:75%;">
										<s:text name="config.configTemplate.userProfile" />
									</th>
									<th style="width: 150px;">
										<s:text name="config.networkpolicy.ssid.list.vlan" />
									</th>
								</tr>
								<tbody>
									<s:iterator value="%{upVlanMapping}" status="status" id="upVlan">
									<s:if test="%{#status.even}">
										<s:if test="%{#upVlan.blnUpDefault}">
											<tr id="upvlan_mapping_tr_<s:property value="%{#status.index}" />" 
											title='<s:text name="info.tip.userprofile.vlan.mapping.title.not.allow"/>'
											class="even disabled">
										</s:if>
										<s:else>
											<tr id="upvlan_mapping_tr_<s:property value="%{#status.index}" />" 
											title='<s:text name="info.tip.userprofile.vlan.mapping.title.reassign"><s:param><s:property value="%{#upVlan.upName}"/></s:param></s:text>'
											class="even canHover">
										</s:else>
									</s:if>
									<s:else>
										<s:if test="%{#upVlan.blnUpDefault}">
											<tr id="upvlan_mapping_tr_<s:property value="%{#status.index}" />" 
											title='<s:text name="info.tip.userprofile.vlan.mapping.title.not.allow"/>'
											class="odd disabled">
										</s:if>
										<s:else>
											<tr id="upvlan_mapping_tr_<s:property value="%{#status.index}" />" 
											title='<s:text name="info.tip.userprofile.vlan.mapping.title.reassign"><s:param><s:property value="%{#upVlan.upName}"/></s:param></s:text>'
											class="odd canHover">
										</s:else>
									</s:else>
										<td name="_up_name" class="listString">
											<s:if test='%{#upVlan.upType == "reassign"}'>
												&nbsp;&nbsp;&nbsp;&nbsp;
											</s:if>
											<span><s:property value="%{#upVlan.upName}" /></span>
											<input type="hidden" name="mappingUpId" value='<s:property value="%{#upVlan.upId}" />' />	
										</td>
										<td name="_vlan_name" class="listString">
											<span name="upv_name_<s:property value="%{#upVlan.upId}" />_<s:property value="%{#upVlan.vlanId}" />"><s:property value="%{#upVlan.vlanName}" /></span>
											<input type="hidden" name="mappingVlanId_<s:property value="%{#upVlan.upId}" />_<s:property value="%{#upVlan.vlanId}" />" value='<s:property value="%{#upVlan.vlanId}" />' />
										</td>
									</tr>
									</s:iterator>
								</tbody>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<div>
			<table align="center">
				<tr style="height: 15px"><td></td></tr>
				<tr>
					<s:if test="%{updateDisabled != 'disabled'}">
						<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="userProfileVlanMappingDlgSaveBtnId" style="float: right;" onclick="javascript: saveUserProfileVlanMappingDlg();" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
					</s:if>
					<s:else>
						<td class="npcButton"></td>
					</s:else>
				</tr>
			</table>
		</div>
	</s:if>
</s:form>
</div>

<div id="up_vlan_mapping_modify_container">
	<table class="innerPage" style="padding: 0 2px 3px 3px;border:none;" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td></td>
	    	<td style="padding:6px 0px 0px 0px;">
					<ah:createOrSelect divId="errorDisplay" list="vlanIdList" typeString="VlanIdForUpMapping"
						selectIdName="myVlanSelect" inputValueName="inputVlanIdValue" swidth="150px"
						stitle="config.userprofile.vlan.tooltip" />
			</td>
			<td>
				<s:if test="%{updateDisabled != 'disabled'}">
					<table>
						<tr>
							<td class="npcButton">
								<a href="javascript:void(0);" class="btCurrent" id="singleUpVlanMappingChangeDone"
									style="float: right;"
									title="<s:text name="common.button.done"/>"><span 
									style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.done"/></span></a>
							</td>
						</tr>
					</table>
				</s:if>
		    </td>
		</tr>
	</table>
</div>

<script>
var upVlanModifyPanel = null;
function prepareUpVlanModifyPanel() {
	var div = document.getElementById('up_vlan_mapping_modify_container');
	upVlanModifyPanel = new YAHOO.widget.Panel(div, {
		width: "285px",
		underlay: "none",
		visible: false,
		draggable: true,
		close: true,
		modal: true,
		constraintoviewport: true,
		zIndex: 9999
		});
	upVlanModifyPanel.render(document.body);
	div.style.display = "";
	upVlanModifyPanel.beforeHideEvent.subscribe(function(){blnInVlanChangeDlg = false;});
}

function showUpVlanModifyPanel(el){
	if(null != upVlanModifyPanel){
		if (el) {
			upVlanModifyPanel.cfg.setProperty("context", [el, "tr", "br"]);
		} else {
			upVlanModifyPanel.center();
		}
		upVlanModifyPanel.cfg.setProperty('visible', true);
		blnInVlanChangeDlg = true;
	}
}

function hideUpVlanModifyPanel(){
	if(null != upVlanModifyPanel){
		upVlanModifyPanel.cfg.setProperty('visible', false);
	}
	blnInVlanChangeDlg = false;
}

var blnInVlanChangeDlg;

function submitAction(operation) {
	var vlanSelected;
	if(operation == 'editVlanIdForUpMapping') {
		vlanSelected = hm.util.validateListSelection("myVlanSelect");
		if(vlanSelected < 0){
			return;
		}
    } else {
    	var vlans = document.getElementById("myVlanSelect");
    	vlanSelected = vlans.options[vlans.selectedIndex].value;
    }
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation="+operation+"&jsonMode=true"
		+ "&vlanId="+vlanSelected
		+ "&userProfileId=" + curModifyRowInfo.upId
		+ "&upVlanMappingType=<s:property value='upVlanMappingType'/>"
		+ "&upVlanRelativeId=<s:property value='upVlanRelativeId'/>"
		+ "&ignore="+new Date().getTime();
	parent.closeIFrameDialog();
	parent.openIFrameDialog(800,450, url);
}

function hideUserProfileVlanMappingDlg() {
	parent.closeIFrameDialog();
	<s:if test="%{parentIframeOpenFlg == true}">
		if (parent) {
			parent.fetchConfigTemplate2Page(true);
		} else {
			fetchConfigTemplate2Page(true);
		}
	</s:if>
}
function saveUserProfileVlanMappingDlg() {
	if (blnInVlanChangeDlg) {
		return;
	}
	var ids = getUpVlanMappings();
	$.post('networkPolicy.action',
			$.param({
				"operation": "saveVlanForUpMapping",
				"mappingUpId": ids.upIds,
				"mappingVlanId": ids.vlanIds
			}, true),
			function(data, textStatus) {
				if (parent) {
					parent.closeIFrameDialog();
					parent.fetchConfigTemplate2Page(true);
				} else {
					closeIFrameDialog();
					fetchConfigTemplate2Page(true);
				}
			},
			'json');
}
function getUpVlanMappings() {
	var upIds = [],
		vlanIds = [],
		upValTmp, vlanValTmp;
	$("#up_vlan_container_tbl tbody tr").each(function() {
		upValTmp = $(this).find("input[name=mappingUpId]").val();
		vlanValTmp = $(this).find("input[name^=mappingVlanId]").val();
		if (upValTmp && vlanValTmp) {
			upIds.push(upValTmp);
			vlanIds.push(vlanValTmp);
		}
	});
	return {
		upIds: upIds,
		vlanIds: vlanIds
	};
}
</script>

<script>
function validateVlanValue() {
	var showError = document.getElementById("errorDisplay");
	var vlans = document.getElementById("myVlanSelect");
	var vlanEle = document.getElementById("inputVlanIdValue");
	if (!vlanEle.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.userprofile.vlan" /></s:param></s:text>');
        vlanEle.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(vlans, vlanEle)) {
		var message = hm.util.validateIntegerRange(vlanEle.value, '<s:text name="config.userprofile.vlan" />', 1, 4094);
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            vlanEle.focus();
            return false;
        }
		return -1;
	} else {
		return vlans.options[vlans.selectedIndex].value;
	}
}
function insertNewOptionToVlanList(id, name) {
	var sel = document.getElementById("myVlanSelect");
	if (sel) {
		var $sel = $(sel);
		if ($("#myVlanSelect option[value="+id+"]").length > 0) {
			$sel.val(id).change();
			return;
		}
		var optionEls = sel.getElementsByTagName("option");
		if (optionEls) {
			if (optionEls.length == 1) {
				$sel.append($("<option value='"+id+"'>"+name+"</option>")[0]);
			} else if (optionEls.length > 1) {
				var i = 1;
				for (i = 1; i < optionEls.length; i++) {
					if (name <= optionEls[i].innerHTML) {
						sel.insertBefore($("<option value='"+id+"'>"+name+"</option>")[0], optionEls[i]);
						break;
					}
				}
				if (i === optionEls.length) {
					$sel.append($("<option value='"+id+"'>"+name+"</option>")[0]);
				}
			}
		}
		$sel.val(id).change();
	}
}
function updateUpVlanMappingShownInfo() {
	var id = $("#myVlanSelect").val(),
		text = $("#myVlanSelect").find("option:selected").text(),
		oldId = curModifyRowInfo.vlanId,
		oldUpId = curModifyRowInfo.upId;
	$("#up_vlan_container_tbl tbody tr td span[name=upv_name_"+oldUpId+"_"+oldId+"]")
		.attr({"name": "upv_name_"+oldUpId+"_"+id})
		.text(text);
	$("#up_vlan_container_tbl tbody tr td input[name=mappingVlanId_"+oldUpId+"_"+oldId+"]")
		.attr({"name": "mappingVlanId_"+oldUpId+"_"+id})
		.val(id);
}
function getInfoForVlanModification(el) {
	if (!el) {
		return;
	}
	return {
		rowId: el.id,
		upName: $("#"+el.id+" td[name=_up_name] span").text(),
		upId: $("#"+el.id+" td[name=_up_name] input[name=mappingUpId]").val(),
		vlanName: $("#"+el.id+" td[name=_vlan_name] span").text(),
		vlanId: $("#"+el.id+" td[name=_vlan_name] input[name^=mappingVlanId]").val()
	};
}
var curModifyRowInfo;
$(function() {
	prepareUpVlanModifyPanel();
	$("#up_vlan_container_tbl tbody tr.canHover").mouseover(function(e) {
		$(this).addClass("hover");
	}).mouseout(function(e) {
		$(this).removeClass("hover");
	}).click(function(e) {
		var upvlanInfo = getInfoForVlanModification(this);
		if (upvlanInfo) {
			$("#up_vlan_mapping_modify_container div.hd").text(upvlanInfo.upName + " [" + upvlanInfo.vlanName + "]");
			$("#myVlanSelect").val(upvlanInfo.vlanId).change();
		}
		curModifyRowInfo= upvlanInfo;
		showUpVlanModifyPanel($("#" + curModifyRowInfo.rowId + " td[name=_vlan_name]")[0]);
	});
	
	$("#singleUpVlanMappingChangeDone").click(function() {
		var curVlan = validateVlanValue();
		if (!curVlan) {
			return;
		}
		if (curVlan == -1) {
			$.post('networkPolicy.action',
					{
						"operation": "newVlanForUpMapping",
						"inputVlanIdValue": $("#inputVlanIdValue").val(),
						"upVlanMappingType": "<s:property value='upVlanMappingType'/>",
						"upVlanRelativeId": "<s:property value='upVlanRelativeId'/>"
					},
					function(data, textStatus) {
						insertNewOptionToVlanList(data.id, data.text);
						updateUpVlanMappingShownInfo();
						hideUpVlanModifyPanel();
					},
					'json');
		} else {
			updateUpVlanMappingShownInfo();
			hideUpVlanModifyPanel();
		}
	});
});
</script>