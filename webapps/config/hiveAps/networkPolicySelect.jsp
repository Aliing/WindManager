<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script type="text/javascript">
	var formName2 = "newNetworkPolicyPanelForm";
	
	defaultSelectedNetworkPolicyId = '<s:property value="defaultConfigTemplateId"/>';

	function newNetworkPolicy() {
		npOperationLock.npNew = false;
		fetchSelectNetworkPolicyNewDlg(null);
	}
	
	var npOperationLock = {};
	npOperationLock.npNew = false;
	
	var newNetworkPolicyPanel = null;
	function preparePanels4SelectNetworkPolicy() {
		var div = document.getElementById('newNetworkPolicyPanelId');
		newNetworkPolicyPanel = new YAHOO.widget.Panel(div, {
			width:"850px",
			underlay: "none",
			visible:false,
			draggable:false,
			close:false,
			modal:true,
			constraintoviewport:true,
			zIndex:1
			});
		newNetworkPolicyPanel.render(document.body);
		div.style.display = "";
	}
	
	function fetchSelectNetworkPolicyNewDlg(arg) {
		var addedHiveId = -1;
		if (arg && arg.addedId) {
			addedHiveId = arg.addedId;
		}
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicySelectDlg"
			 + "&addedHiveId="+addedHiveId
			 + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkPolicyNewDlg, failure : resultDoNothing, timeout: 60000}, null);
	}
	
	var succFetchNetworkPolicyNewDlg = function(o) {
		set_innerHTML("newNetworkPolicyPanelContentId",
				o.responseText);
		YAHOO.util.Event.onContentReady("newNetworkPolicyPanelContentId", showSelectNetworkPolicyPanel, this);
	}
	
	function showSelectNetworkPolicyPanel(){
		if(null != newNetworkPolicyPanel){
			newNetworkPolicyPanel.center();
			newNetworkPolicyPanel.cfg.setProperty('visible', true);
			//set config name the focused field
			if (Get(formName2+"_configName") != null) {
				Get(formName2+"_configName").focus();
			}
		}
		restoreTmpNetworkPolicyArgs?restoreTmpNetworkPolicyArgs():null;
	}

	function hideSelectNetworkPolicyPanel(){
		if(null != newNetworkPolicyPanel){
			set_innerHTML("newNetworkPolicyPanelContentId", "");
			newNetworkPolicyPanel.cfg.setProperty('visible', false);
		}
	}
	
	var tmpNetworkPolicyArgs = {};
	var tmpNetworkPolicyModifyArgs = {};
	
	function editNetworkPolicyDialog(npId, event) {
        hideModifyNetworkPolicyPanel();
        
        doGlobalEditNetworkPolicy();
        
        hm.util.stopBubble(event);

        networkPolicyCallbackFn = null;
	}
	
	function cloneNetworkPolicyDialog(npId, event) {
		hideModifyNetworkPolicyPanel();
        
        doCloneNetworkPolicy();
        
        hm.util.stopBubble(event);

        networkPolicyCallbackFn = null;
	}
	
	function removeNetworkPolicyDialog(npId, event) {
		doNetworkPolicyContinueOper = function() {
			hideModifyNetworkPolicyPanel();
	        doRemoveNetworkPolicy();
	        hm.util.stopBubble(event);
	        networkPolicyCallbackFn = null;
		};
		
		hm.util.confirmRemoveItems();
	}
	
	YAHOO.util.Event.onContentReady("networkPolicysDiv", adjustEditLink, this);
</script>
<div>
	<s:form action="networkPolicy" id="selectNetworkPolicyForm" name="selectNetworkPolicyForm">
		<s:hidden name="operation" />
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="npcNoteTitle">
					<s:text name="config.v2.select.network.policy.tip.main"/>
				</td>
			</tr>
			<tr>
				<td class="subDrawer2">
					<span class="npcHead1"><s:text name="config.v2.select.network.policy.select.title"/></span><br>
				</td>
			</tr>
			<tr>
				<td align="center">
					<table><tr><td><span id="policyListErrorDiv"/></span></td></tr></table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="selectList npSelectContainer" id="networkPolicySelectListTable">
						<ah:checkList name="networkPolicys" list="networkPolicyList" multiple="false" 
							listKey="id" listValue="value" value="defaultConfigTemplateId" editEvent="editNetworkPolicyDialog"
							cloneEvent="cloneNetworkPolicyDialog"
							removeEvent="removeNetworkPolicyDialog"
							menuContainerStyle="width:55px;"
							itemWidth="20em"
							width="100%" height="150px"/>
					</div>
					<div style="margin:0 auto; width: 240px; padding-top: 10px;">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td align="center">
								<table id="npSelectButtonsTblId" border="0" cellspacing="0" cellpadding="0">
							<s:if test="%{writeDisabled == 'disabled'}">
								<tr>
									<td class="npcButton" style="padding-right:15px;">
										<a class="btCurrent" href="javascript: void(0);" style="float: right;" onClick="javascript:doFinishSelectNetworkPolicy();">
											<span id="selectNetWorkPolicySpanId" class="minWidth"><s:text name="common.button.ok"/></span></a>
									</td>
									<%-- <td class="npcButton" style="padding-left:15px;">
										<a href="javascript: void(0);" class="btCurrent" 
											title="<s:text name="config.v2.select.network.policy.button.new"/>">
											<span class="minWidth"><s:text name="config.v2.select.network.policy.button.new"/></span></a>
									</td> --%>
								</tr>
							</s:if>
							<s:else>
								<tr>
									<td class="npcButton" style="padding-right:15px;">
										<a class="btCurrent" href="javascript: void(0);" style="float: right;" onClick="javascript:doFinishSelectNetworkPolicy();">
											<span id="selectNetWorkPolicySpanId" class="minWidth"><s:text name="common.button.ok"/></span></a>
									</td>
									<td class="npcButton" style="padding-left:15px;">
										<a href="javascript: void(0);" class="btCurrent" 
										onclick="newNetworkPolicy();" title="<s:text name="config.v2.select.network.policy.button.new"/>">
											<span class="minWidth"><s:text name="config.v2.select.network.policy.button.new"/></span></a>
									</td>
								</tr>
							</s:else>
								</table>
							</td>
						</tr>
					</table>
					</div>
				</td>
			</tr>
		</table>
	</s:form>
</div>



	
	<div id="newNetworkPolicyPanelId" style="display: none;">
	
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="100%">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" id="tdUM" style="width:770px;"></td><td class="ur"></td>
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
			<td class="bl"></td><td class="bm" id="tdBM" style="width:770px;"></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
	
	</div>

<script type="text/javascript">
	YAHOO.util.Event.onContentReady("newNetworkPolicyPanelId", function() {
			preparePanels4SelectNetworkPolicy();
			
			<s:if test="%{messagesToShown != null && messagesToShown.size() > 0}">
			var messages = "";
			<s:iterator value="messagesToShown" status="status">
				messages += "<s:property /><br/>";
			</s:iterator>
			showInfoDialog("<html><body>"+messages+"</body></html>");
			</s:if>
		}, this);
</script>