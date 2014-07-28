<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>

<script>
var formName = 'vpnServices';
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
    showProcessing();
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="vpnServices">
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
					<s:if test="%{!easyMode}">
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>" 
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('vpnServices')"
						<s:property value="writeDisabled" />></td>
					</s:if>
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
			<table id="hiveTable" cellspacing="0" cellpadding="0" border="0"
				class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
						<s:if test="%{columnId == 1}">
							<th><ah:sort name="profileName" key="config.vpn.service.name" /></th>
						</s:if>
						<s:if test="%{columnId == 2}">
							<th><ah:sort name="certificate" key="config.vpn.service.list.certificate" /></th>
						</s:if>
						<s:if test="%{columnId == 3}">
							<th><ah:sort name="privateKey" key="config.vpn.service.list.privateKey" /></th>
						</s:if>
						<s:if test="%{columnId == 4}">
							<th><ah:sort name="serverPrivateIp1" key="config.vpn.service.server1.privateIp" /></th>
						</s:if>
						<s:if test="%{columnId == 5}">
							<th><ah:sort name="serverPublicIp1" key="config.vpn.service.server1.publicIp" /></th>
						</s:if>
						<s:if test="%{columnId == 6}">
							<th><ah:sort name="serverPrivateIp2" key="config.vpn.service.server2.privateIp" /></th>
						</s:if>
						<s:if test="%{columnId == 7}">
							<th><ah:sort name="serverPublicIp2" key="config.vpn.service.server2.publicIp" /></th>
						</s:if>
						<s:if test="%{columnId == 8}">
							<th><ah:sort name="description" key="config.vpn.service.description" /></th>
						</s:if>
						<s:if test="%{columnId == 9}">
							<th><ah:sort name="clientIpPoolStart1" key="config.vpn.service.list.server.ippool.start" /></th>
						</s:if>
						<s:if test="%{columnId == 10}">
							<th><ah:sort name="clientIpPoolEnd1" key="config.vpn.service.list.server.ippool.end" /></th>
						</s:if>
					</s:iterator>
					<th><s:text name="config.vpn.service.topology" /></th>
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
										href='<s:url value="vpnServices.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="profileName" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="vpnServices.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="profileName" /></a></td>
								</s:else>
							</s:if>
							<s:if test="%{columnId == 2}">
								<td class="list"><s:property value="certificate" /></td>
							</s:if>
							<s:if test="%{columnId == 3}">
								<td class="list"><s:property value="privateKey" /></td>
							</s:if>
							<s:if test="%{columnId == 4}">
								<td class="list"><s:property value="serverPrivateIp1" /></td>
							</s:if>
							<s:if test="%{columnId == 5}">
								<td class="list"><s:property value="serverPublicIp1" /></td>
							</s:if>
							<s:if test="%{columnId == 6}">
								<td class="list">&nbsp;<s:property value="serverPrivateIp2" /></td>
							</s:if>
							<s:if test="%{columnId == 7}">
								<td class="list">&nbsp;<s:property value="serverPublicIp2" /></td>
							</s:if>
							<s:if test="%{columnId == 8}">
								<td class="list">&nbsp;<s:property value="description" /></td>
							</s:if>
							<s:if test="%{columnId == 9}">
								<td class="list">&nbsp;<s:property value="clientIpPoolStart1" /></td>
							</s:if>
							<s:if test="%{columnId == 10}">
								<td class="list">&nbsp;<s:property value="clientIpPoolEnd1" /></td>
							</s:if>
						</s:iterator>
						<td class="list"><a href="#vpnTopology" onclick="openVpnTopologyPanel(<s:property value="id" />);"><s:text name="config.vpn.service.topology.view" /></a></td>
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
<div id="vpnTopologyPanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="vpnTopology" name="vpnTopology" width="0" height="0"
			frameborder="0" style="background-color: #999;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var vpnTopologyPanel = null;
function createVpnTopologyPanel(width, height){
	var div = document.getElementById("vpnTopologyPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vpnTopology");
	iframe.width = width;
	iframe.height = height;
	vpnTopologyPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:true, visible:false, constraintoviewport:true } );
	vpnTopologyPanel.render();
	div.style.display="";
	vpnTopologyPanel.beforeHideEvent.subscribe(clearVpnTopologyData);
	var resize = createResizer("vpnTopologyPanel");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, vpnTopologyPanel, true);
	resize.on("endResize", function(args){
		vpnTopologyPanelResizeCallback();
	}, vpnTopologyPanel, true);
}
//Create Resize instance, binding it to the 'resizablepanel' DIV
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 650,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
function vpnTopologyPanelResizeCallback(){
	if(null != vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.location.reload();
	}
}
function clearVpnTopologyData(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "none";
	}
	if(vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.onHidePage();
	}
}
function openVpnTopologyPanel(id){
	if(null == vpnTopologyPanel){
		var width = YAHOO.util.Dom.getViewportWidth();
		var height = YAHOO.util.Dom.getViewportHeight();
		createVpnTopologyPanel(width*0.8, height*0.8);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "";
	}
	vpnTopologyPanel.header.innerHTML = "<s:text name="config.vpn.service.topology"/>";
	vpnTopologyPanel.show();
	var iframe = document.getElementById("vpnTopology");
	iframe.src ="<s:url value='vpnServices.action' includeParams='none' />?operation=initVpnTopologyPanel&id="+id+"&pageId="+new Date().getTime();
}
function updateVpnTopologyPanelTitle(str){
	if(null != vpnTopologyPanel){
		vpnTopologyPanel.header.innerHTML = "<s:text name="config.vpn.service.topology"/>"+" - "+str;
	}
}
var vpnTopologyIframeWindow;
</script>