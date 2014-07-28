<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<link type="text/css" rel="stylesheet" href="<s:url value="/css/hm_tab.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>

<style type="text/css">
.device-conn-status {
	height: 16px;
	width: 16px;
	margin: 0 auto;
}
.device-conn-status.conn-status1 {
	height: 18px;
	width: 21px;
	background: url(images/cloud_inactive.png) no-repeat scroll 0 0 transparent;
}
.device-conn-status.conn-status2 {
	height: 18px;
	width: 21px;
	background: url(images/cloud_active.png) no-repeat scroll 0 0 transparent;
}
.device-conn-status.conn-status3 {
	background: url(images/HM-capwap-down.png) no-repeat scroll 0 0 transparent;
}
.device-conn-status.conn-status4 {
	background: url(images/HM-capwap-up.png) no-repeat scroll 0 0 transparent;
}
</style>

<script>
var formName = 'deviceInventory';

var pre_config_device_serial=null;
var diid_apid_config_map = <s:property escape="false" value="%{listObjectConfigString}" />;

var thisOperation;
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
		<s:if test="%{hMOnline}">
			hm.util.checkAndConfirmDeleteRuleFromRedirect('selectedIds','<s:text name ="deviceinventory.remove.dlg.confirm" />');
		</s:if>
		<s:else>
			hm.util.checkAndConfirmDeleteRuleFromRedirect('selectedIds','<s:text name ="deviceinventory.remove.dlg.confirm.single" />');
		</s:else>
    } else if (operation == 'export') {
    	hm.util.checkAndConfirmMultiple("export");
    } else {
    	doContinueOper();
    }	
}
function doContinueOper() {
	if (thisOperation !== 'export') {
		showProcessing();
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:text name="geneva_08.device.inventory.title" /></td>');
}

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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
	overlayManager.register(waitingPanel);
	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
}

function createDeviceInventoryMenu() {
	var di_Menu = new YAHOO.widget.Menu("s_menu_inventory", { fixedcenter: false, zIndex: 999 });
	var di_Items=[];
	//var subItem=[];
	<s:if test="%{hMOnline}">
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.addImport"/>'}]);
	</s:if>
	<s:else>
	 subArray=[];
	 <s:iterator value="%{apModel}" status="status">
	 	subArray.push([{ text: '<s:property value="value" />', onclick: { fn: newDeviceClick, obj: <s:property value="key"/> }}]);
	 </s:iterator>
	 di_Items.push([{text: '<s:text name="geneva_08.hm.menu.add"/>',submenu: {id: "addNewDeviceModelSelect",itemdata:subArray}}]);
	</s:else>
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.remove"/>'}]);
	//di_Items.push(subItem);
	di_Items.push([{text: '<s:text name="geneva_08.hm.menu.export"/>'}]);
	di_Menu.addItems(di_Items);
	
	di_Menu.subscribe('click', function (p_sType, p_aArguments){
		var event = p_aArguments[0];
		var menuItem = p_aArguments[1];
		var menuText = menuItem.cfg.getProperty("text");
		if (menuText=='<s:text name="geneva_08.hm.menu.addImport"/>') {
			<s:if test="%{hMOnline}">
				openScanOverlay();
			</s:if>
		} else if (menuText=='<s:text name="geneva_08.hm.menu.remove"/>') {
			submitAction('remove');
		} else if (menuText=='<s:text name="geneva_08.hm.menu.export"/>') {
			submitAction('export')
		}
	});

	di_Menu.subscribe("beforeShow", function(){
		var x1 = YAHOO.util.Dom.getX('menu_inventory');
		var y1 = YAHOO.util.Dom.getY('menu_inventory');
		YAHOO.util.Dom.setX('s_menu_inventory', x1 + 1);
		YAHOO.util.Dom.setY('s_menu_inventory', y1+25);
	});

	di_Menu.render();

	YAHOO.util.Event.addListener("menu_inventory", "click", di_Menu.show, null, di_Menu);
}
var di_modify_menu=null;
function createModifyMenu() {
	di_modify_menu = new YAHOO.widget.Menu("s_menu_modify", { fixedcenter: false, zIndex: 999 });
	var di_Items=[];
	 <s:iterator value="%{apModel}" status="status">
	 	di_Items.push([{ text: '<s:property value="value" />', onclick: { fn: newDeviceClickModify, obj: <s:property value="key"/> }}]);
	 </s:iterator>
	 di_modify_menu.addItems(di_Items);

	 di_modify_menu.subscribe("beforeShow", function(){
		var x1 = YAHOO.util.Dom.getX('menu_modify');
		var y1 = YAHOO.util.Dom.getY('menu_modify');
		YAHOO.util.Dom.setX('s_menu_modify', x1 + 1);
		YAHOO.util.Dom.setY('s_menu_modify', y1+25);
	});
	 di_modify_menu.render();
	//YAHOO.util.Event.addListener("menu_modify", "click", menu_modify_click(di_Menu), null, di_Menu);
}

YAHOO.util.Event.onDOMReady(function () {
	<s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive && writeDisabled!='disabled') || (!hMOnline && writeDisabled!='disabled')}">
		createDeviceInventoryMenu();
	</s:if>
	<s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive && writeDisabled!='disabled')}">
		createModifyMenu();
	</s:if>
});

function newDeviceClick(p_sType, p_aArgs, p_oValue) {
	var lHref = 'hiveAp.action?operation=new2&hiveApModel=' + p_oValue + "&diMenuTypeKey=" + "<s:property value="%{selectedL2Feature.key}"/>" + "&ignore=" + new Date().getTime();
	window.location.href = lHref;

}

function newDeviceClickModify(p_sType, p_aArgs, p_oValue) {
	var lHref = 'hiveAp.action?operation=new2&hiveApModel=' + p_oValue 
			+ "&pre_serialNumber=" + pre_config_device_serial
			+ "&diMenuTypeKey=" + "<s:property value="%{selectedL2Feature.key}"/>" + "&ignore=" + new Date().getTime();
	window.location.href = lHref;
}

function clickModifyButton() {
	hm.util.checkMultiModifyUnmanagedDevice('selectedIds','<s:text name ="geneva_08.device.inventory.multiedit.error" />',diid_apid_config_map, true);
}

function doClickModifyContinueOper(m_selectCount, m_not_cfg_before, m_ids, serialNum) {
	if (m_selectCount==1 && m_not_cfg_before) {
		pre_config_device_serial= serialNum;
		di_modify_menu.show();
	} else if (m_selectCount==1 && !m_not_cfg_before) {
		var lHref = 'hiveAp.action?operation=edit2&id=' + m_ids + "&diMenuTypeKey=" + "<s:property value="%{selectedL2Feature.key}"/>" + "&ignore=" + new Date().getTime();
		window.location.href = lHref;
	} else {
		var lHref = 'hiveAp.action?operation=multiEdit&selectedDeviceIdStr=' + m_ids + "&diMenuTypeKey=" + "<s:property value="%{selectedL2Feature.key}"/>" + "&ignore=" + new Date().getTime();
		window.location.href = lHref;
	}
}
/**
var diid_apid_config_map={
		9:  {
		 id:4945, 
		 precfg: true,
		 serialNum:"11111111112220"
		 },
		10:  {
		 id:4969, 
		 precfg: true,
		 serialNum:"11111111112221"
		 },
		 11:  {
			 id:4962, 
			 precfg: true,
			 serialNum:"11111111112222"
			 },

		12:  {
			 id:4962, 
			 precfg: false,
			 serialNum:"33333333330001"
			 },

		13:  {
			 id:4962, 
			 precfg: false,
			 serialNum:"33333333330002"
			 }
		};**/

</script>

<div id="content"><s:form action="deviceInventory">
	<s:hidden name="diMenuType"></s:hidden>
	<s:hidden name="diMenuTypeKey"></s:hidden>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="%{(hMOnline && isInHomeDomain==false) || !hMOnline}">
			<tr>
				<td class="menu_bg" style="padding-top:10px">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="15px" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
							<td class="tab_unSelectTabStyle" nowrap>
							<s:if test="%{selectedL2Feature.key=='managedHiveAps'}">
								<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedHiveAps'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:if>
							<s:elseif test="%{selectedL2Feature.key=='configHiveAps'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedHiveAps'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='branchRouters'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedRouters'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='configBranchRouters'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedRouters'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='switches'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedSwitches'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='configSwitches'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedSwitches'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='vpnGateways'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedVPNGateways'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='configVpnGateways'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedVPNGateways'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='deviceHiveAps'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedDeviceAPs'}"/><s:param name="viewType" value="%{'monitor'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							<s:elseif test="%{selectedL2Feature.key=='configDeviceHiveAps'}">
							<a href='<s:url action="hiveAp"><s:param name="hmListType" value="%{'managedDeviceAPs'}"/><s:param name="viewType" value="%{'config'}"/></s:url>'>
							<s:text name="geneva_08.tab.title.alldevice"/></a>
							</s:elseif>
							</td>
							<td class="tab_selectTabStyle" nowrap><a href="javascript:void(0);" ><s:text name="geneva_08.tab.title.newdevice"/></a></td>
							<td width="100%" class="tab_unSelectStyle">&nbsp;&nbsp;&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
		</s:if>
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{hMOnline}">
						<td><input type="button" name="ignore" value="Modify" id="menu_modify"
								class="button" onClick="clickModifyButton();"
								<s:property value="writeDisabled" />></td>
						<td><input type="button" name="refresh" value="<s:text name="geneva_08.button.syncDevice"/>"
							class="button" onClick="submitAction('refresh');" <s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="Modify"
								class="button" onClick="clickModifyButton();"
								<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{(hMOnline && isInHomeDomain==false && userHasAccessMyHive && writeDisabled!='disabled') || (!hMOnline && writeDisabled!='disabled')}">
					 	<td><input type="button" name="ignore" value="<s:text name="geneva_08.button.deviceInventory"/>"
							class="button long" id="menu_inventory">
						</td>
						<td><div id="s_menu_inventory" class="yuimenu" style="width:140px;"></div>
						</td>
					 </s:if>
					 <s:if test="%{hMOnline}">
					 <td><div id="s_menu_modify" class="yuimenu" style="width:170px;"></div>
						</td>
					</s:if>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0"
				class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th align="left" nowrap><ah:sort name="di.serialNumber"
								key="hiveAp.serialNumber" /></th>
						</s:if>
						<s:elseif test="%{columnId == 2}">
							<th align="left" nowrap><ah:sort name="hiveap.macaddress"
									key="hiveAp.macaddress" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 3}">
							<th align="left" nowrap><ah:sort name="hiveap.hostname"
									key="hiveAp.hostName" /></th>
						</s:elseif>
						<s:elseif test="%{columnId == 4}">
							<th align="left" nowrap>
								<ah:sort name="di.connectstatus"
									key="geneva_08.device.inventory.connect.status" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 5}">
							<th align="left" nowrap>
								<ah:sort name="np.configname"
									key="geneva_08.device.inventory.list.title.networkpolicy" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 6}">
							<th align="left" nowrap>
								<s:text name="geneva_08.device.inventory.list.title.hive" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 7}">
							<th align="left" nowrap>
								<ah:sort name="map.mapname"
									key="geneva_08.device.inventory.list.title.topology" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 8}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.hiveapmodel"
									key="hiveAp.model" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 9}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.devicetype"
									key="hiveAp.device.type" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 10}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.cfgIpAddress"
									key="hiveAp.interface.ipAddress" orderByIp="true" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 11}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.dhcp"
									key="hiveAp.head.dhcp" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 12}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.cfgNetmask"
									key="hiveAp.netmask" orderByIp="true" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 13}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.cfgGateway"
									key="hiveAp.gateway" orderByIp="true" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 14}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.location"
									key="hiveAp.location" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 15}">
							<th align="left" nowrap>
								<s:text name="config.configTemplate.vlanNative" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 16}">
							<th align="left" nowrap>
								<s:text name="config.configTemplate.vlan" />
							</th>
						</s:elseif>
						<s:elseif test="%{columnId == 17}">
							<th align="left" nowrap>
								<ah:sort name="hiveap.capwapClientIp"
									key="hiveAp.capwapIpAddress" orderByIp="true" />
							</th>
						</s:elseif>
					</s:iterator>
					<s:if test="%{showDomain}">
						<th><ah:sort name="owner.domainName" key="config.domain" /></th>
   					</s:if>
				</tr>
				<s:if test="%{page == null || page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
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
		   						<td class="list" nowrap="nowrap"><s:property value="serialNumber" />&nbsp;</td>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list" nowrap="nowrap"><s:property value="macAddress" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list" nowrap="nowrap"><s:property value="hostName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list" nowrap="nowrap">
									<div class='device-conn-status conn-status<s:property value="connectStatus" />'
										title='<s:property value="connectStatusDesc" />'></div>
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list" nowrap="nowrap">
									<s:if test="%{deviceType == 2}">
										N/A &nbsp;
									</s:if>
									<s:else>
										<s:property value="networkPolicyName" />&nbsp;
									</s:else>
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<td class="list" nowrap="nowrap"><s:property value="hiveName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 7}">
								<td class="list" nowrap="nowrap"><s:property value="mapName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 8}">
								<td class="list" nowrap="nowrap"><s:property value="hiveApModelString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 9}">
								<td class="list" nowrap="nowrap"><s:property value="deviceTypeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 10}">
								<td class="list" nowrap="nowrap"><s:property value="cfgIpAddress" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 11}">
								<td class="list" nowrap="nowrap"><s:property value="dhcpString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 12}">
								<td class="list" nowrap="nowrap"><s:property value="cfgNetmask" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 13}">
								<td class="list" nowrap="nowrap"><s:property value="cfgGateway" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 14}">
								<td class="list" nowrap="nowrap"><s:property value="location" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 15}">
								<td class="list" nowrap="nowrap"><s:property value="nativeVlanName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 16}">
								<td class="list" nowrap="nowrap"><s:property value="vlanName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 17}">
								<td class="list" nowrap="nowrap"><s:property value="capwapClientIp" />&nbsp;</td>
							</s:elseif>
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
<tiles:insertDefinition name="deviceMappingRedirector" />
<script>
	if (scanOverlayResult==null) {
		var div = document.getElementById('scanPanelResult');
		scanOverlayResult = new YAHOO.widget.Panel(div, {
			width:"610px",
			visible:false,
			fixedcenter:"contained",
			draggable:true,
			close: false, 
			modal:true,
			constraintoviewport:true,
			zIndex:1
			});
		scanOverlayResult.render(document.body);
		scanOverlayResult.moveTo(1,1);//fix scroll bar issue
		div.style.display = "";
	}
	scanOverlayResult.hideEvent.subscribe(refreshDeviceInventoryListPage);
	
	function refreshDeviceInventoryListPage() {
		if (importDialogResultOpen==1) {
			<s:if test="%{selectedL2Feature.key=='configHiveAps' || selectedL2Feature.key=='configVpnGateways' || selectedL2Feature.key=='configBranchRouters' || selectedL2Feature.key=='configSwitches' || selectedL2Feature.key=='configDeviceHiveAps'}">
				document.forms[formName].diMenuTypeKey.value = 'configHiveAps';
			</s:if>
			<s:else>
				document.forms[formName].diMenuTypeKey.value = 'managedHiveAps';
			</s:else>
			submitAction("nothing");
		}
	}
</script>
