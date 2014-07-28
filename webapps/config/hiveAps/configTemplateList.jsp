<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<s:if test="%{fullMode}">
<script src="<s:url value="/js/innerhtml.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
	<style>
	    .ul {
	    width:40px;
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
	    }
	    .um {
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
	    }
	    .ur {
	    width:40px;
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
	    }
	    .ml {
	    width:40px;
	    height:100%;
	    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
	    }
	    .mm {
	    height:100%;
	    background-color: #f9f9f7;
	    }
	    .mr {
	    width:40px;
	    height:100%;
	    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
	    }
	    .bl {
	    width: 40px;
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
	    }
	    .bm {
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
	    }

		.br {
		width: 40px;
	    height:40px;
	    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
	    }

		#newNetworkPolicyPanelId.yui-panel {
			border: none;
			overflow: visible;
			background-color: transparent;
		}

		img.dialogTitleImg {
			padding: 0;
			vertical-align: middle;
			align: center;
			border:none;
			margin-right: 10px;
		}
	</style>
</s:if>

<script>
var formName = 'configTemplate';
var thisOperation;

function submitAction(operation) {

    thisOperation = operation;
    if (operation == 'remove') {
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else {
        doContinueOper();
    }
}
function doContinueOper() {
	<s:if test="%{fullMode}">
		if (thisOperation == 'clone') {
			showCloneNetworkPolicyDlg();
		} else {
			showProcessing();
		    document.forms[formName].operation.value = thisOperation;
		    document.forms[formName].submit();
		}
	</s:if>
	<s:else>
	    showProcessing();
	    document.forms[formName].operation.value = thisOperation;
	    document.forms[formName].submit();
    </s:else>
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="configTemplate">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="New"
						class="button" onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone" id="cloneNpId"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>"
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('configTemplate')"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table id = "hiveTable" cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="configName" key="config.configTemplate.configName" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="hiveProfile.hiveName" key="config.configTemplate.hive" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="vlan.vlanName" key="config.configTemplate.vlan" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><ah:sort name="vlanNative.vlanName" key="config.configTemplate.vlanNative" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
							<th><s:text name="config.configTemplate.overview" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
							<th><ah:sort name="description" key="config.configTemplate.description" /></th>
						</s:if>

						<s:if test="%{columnId == 7}">
							<th><ah:sort name="mgmtServiceDns" key="config.configTemplate.mgtDns" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">
							<th><ah:sort name="mgmtServiceTime" key="config.configTemplate.mgtTime" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">
							<th><ah:sort name="mgmtServiceSnmp" key="config.configTemplate.mgtSnmp" /></th>
						</s:if>
						<s:if test="%{columnId == 10}">
							<th><ah:sort name="locationServer" key="config.configTemplate.locationServer" /></th>
						</s:if>
						<s:if test="%{columnId == 11}">
							<th><ah:sort name="mgmtServiceSyslog" key="config.configTemplate.mgtSyslog" /></th>
						</s:if>
						<s:if test="%{columnId == 12}">
							<th><ah:sort name="algConfiguration" key="config.configTemplate.algConfig" /></th>
						</s:if>
						<s:if test="%{columnId == 13}">
							<th><ah:sort name="mgmtServiceOption" key="config.configTemplate.mgtOption" /></th>
						</s:if>
						<s:if test="%{columnId == 14}">
							<th><ah:sort name="idsPolicy" key="config.configTemplate.idsPolicy" /></th>
						</s:if>
						<s:if test="%{columnId == 15}">
							<th><ah:sort name="accessConsole" key="config.configTemplate.accessConsole" /></th>
						</s:if>
						<s:if test="%{columnId == 16}">
							<th><ah:sort name="ipFilter" key="config.configTemplate.ipFilter" /></th>
						</s:if>
				
						<s:if test="%{columnId == 17}">
							<th><ah:sort name="supplementalCLI" key="hollywood_02.supp_cli.configTemplateList.title" /></th>
						</s:if>
				

					</s:iterator>
					<s:if test="%{showDomain}">
         				<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>

				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
					    <s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
					       <td class="listCheck"><input type="checkbox" disabled /></td>
					   	</s:if>
					   	<s:else>
							<td class="listCheck"><ah:checkItem /></td>
						</s:else>

						<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 1}">
									<s:if test="%{showDomain}">
										<td class="list"><a
											href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
											value="configName" /></a></td>
									</s:if>
									<s:else>
										<td class="list"><a
											href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
											value="configName" /></a></td>
									</s:else>
								</s:if>
								<s:if test="%{columnId == 2}">
									<td class="list">&nbsp;<s:property value="hiveProfile.hiveName" /></td>
   								</s:if>
								<s:if test="%{columnId == 3}">
									<td class="list">&nbsp;<s:property value="vlan.vlanName" /></td>
   								</s:if>
								<s:if test="%{columnId == 4}">
									<td class="list">&nbsp;<s:property value="vlanNative.vlanName" /></td>
   								</s:if>
								<s:if test="%{columnId == 5}">
									<td class="list">&nbsp;
										<s:iterator value="%{ssidInterfaces.values}" status="status" id="templateSsid">
											<!--<s:if test="%{#templateSsid.ssidProfile != null}">
												<a href='<s:url value="ssidProfiles.action"><s:param name="operation" value="%{'edit'}"/><s:param name="networkPolicyId4Drawer" value="%{id}"/><s:param name="id" value="%{#templateSsid.ssidProfile.id}"/><s:param name="domainId" value="%{owner.id}"/></s:url>'><s:property
												value="%{interfaceName}" /></a>
												&nbsp;&nbsp;
											</s:if> -->
											<s:if test="%{showDomain}">
												<a
													href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
													value="interfaceName" /></a>
											</s:if>
											<s:else>
												<a
													href='<s:url value="configTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
													value="interfaceName" /></a>
											</s:else>
											&nbsp;&nbsp;
										</s:iterator>
									</td>
   								</s:if>
								<s:if test="%{columnId == 6}">
									<td class="list">&nbsp;<s:property value="description" /></td>
   								</s:if>

   								<s:if test="%{columnId == 7}">
									<td class="list">&nbsp;<s:property value="mgmtServiceDns.mgmtName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 8}">
									<td class="list">&nbsp;<s:property value="mgmtServiceTime.mgmtName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 9}">
									<td class="list">&nbsp;<s:property value="mgmtServiceSnmp.mgmtName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 10}">
									<td class="list">&nbsp;<s:property value="locationServer.name" /></td>
   								</s:if>
   								<s:if test="%{columnId == 11}">
									<td class="list">&nbsp;<s:property value="mgmtServiceSyslog.mgmtName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 12}">
									<td class="list">&nbsp;<s:property value="algConfiguration.configName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 13}">
									<td class="list">&nbsp;<s:property value="mgmtServiceOption.mgmtName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 14}">
									<td class="list">&nbsp;<s:property value="idsPolicy.policyName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 15}">
									<td class="list">&nbsp;<s:property value="accessConsole.consoleName" /></td>
   								</s:if>
   								<s:if test="%{columnId == 16}">
									<td class="list">&nbsp;<s:property value="ipFilter.filterName" /></td>
   								</s:if>
   								
   								<s:if test="%{columnId == 17}">
									<td class="list">&nbsp;<s:property value="supplementalCLI.supplementalName" /></td>
   								</s:if>
   								
   						</s:iterator>
   						<s:if test="%{showDomain}">
         					<td class="list"><s:property value="%{owner.domainName}" /></td>
   						</s:if>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<s:if test="%{fullMode}">
	<div id="newNetworkPolicyPanelId" style="display: none;">
		<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td width="100%">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="ul"></td><td class="um" id="tdUM" style="width:585px;"></td><td class="ur"></td>
					</tr>
				</table>
			</td></tr>
			<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newNetworkPolicyPanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
			</td></tr>
			<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:585px;"></td><td class="br"></td>
				</tr>
			</table>
			</td></tr>
		</table>
	</div>

	<script type="text/javascript">
		var newNetworkPolicyPanel = null;
		function createSelectNetworkPolicy() {
			var div = document.getElementById('newNetworkPolicyPanelId');
			if (div) {
				newNetworkPolicyPanel = new YAHOO.widget.Panel(div, {
					width:"665px",
					underlay: "none",
					visible:false,
					draggable:false,
					close:false,
					modal:true,
					constraintoviewport:true,
					zIndex:999
					});
				newNetworkPolicyPanel.render(document.body);
				div.style.display = "";
			}
		}

		function showCloneNetworkPolicyDlg() {
			if (newNetworkPolicyPanel == null) {
				createSelectNetworkPolicy();
			}
			var selectedIds = hm.util.getSelectedIds();
			var cloneSrcId = selectedIds[0];
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicyCloneDlg"
				 + "&cloneSrcId="+cloneSrcId
				 + "&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succShowCloneNetworkPolicyDlg, failure : failShowCloneNetworkPolicyDlg, timeout: 60000}, null);
		}

		var succShowCloneNetworkPolicyDlg = function(o) {
			set_innerHTML("newNetworkPolicyPanelContentId",
					o.responseText);
			if (newNetworkPolicyPanel != null) {
				newNetworkPolicyPanel.cfg.setProperty("context", ["cloneNpId", "tl", "bl"]);
				newNetworkPolicyPanel.cfg.setProperty('visible', true);
				newNetworkPolicyPanel.show();
			}
		}

		var failShowCloneNetworkPolicyDlg = function(o) {
			warnDialog.cfg.setProperty('text', "Failed to clone network policy.");
			warnDialog.show();
		}

		function hideCloneNetworkPolicyDlg() {
			if (newNetworkPolicyPanel != null) {
				newNetworkPolicyPanel.hide();
			}
		}

		function hideModifyNetworkPolicyPanel() {
			hideCloneNetworkPolicyDlg();
		}
	</script>
</s:if>