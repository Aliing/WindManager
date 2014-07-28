<%@ taglib prefix="s" uri="/struts-tags"%>

<style>
<!--
.leafLabel {
	position:absolute;
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
	color: #003366;
	white-space: nowrap;
	padding: 1px 3px 1px 3px;
	border-top: 1px solid #999999;
	border-right: 1px solid #999999;
	border-bottom: 1px solid #999999;
	border-left: 1px solid #999999; /* remove for target icons */
	background-color: #FFFFFF;
}

#canvas{
	position: relative;
	background-image: url(images/hm/bkg.gif);
	background-position: center;
	background-repeat: no-repeat;
	background-color: #EFEFEF;
}

b.vpnLabel{
	font-weight: bold;
	color: #003366;
}
div.iconToolbar {
    background-image: url(images/hm_v2/profile/hm-icon-vpn-big.png);
    background-repeat: no-repeat;
    background-position: 20px 0;
    width: 100%;
    height: 40px;
}
-->
</style>

<script src="<s:url value="/js/raphael-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script type="text/javascript">
<!--
//document.oncontextmenu = function(){return false;};
var formName = "vpnServices";
var pollTimeoutId;
var pollCount = 0;
var vpnTopology = {ratio: 0.3, queueCount: 18, radian: 80};

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
}

function onLoadPage(){
	var profileName = "<s:property value="%{vpnProfileName}"/>";
	var profileId = <s:property value="%{id}"/>;
	var pageId = <s:property value="%{pageId}"/>
	if(profileName && profileId){
		top.updateVpnTopologyPanelTitle(profileName);
		vpnTopology.profileId = profileId;
		vpnTopology.pageId = pageId;
		vpnTopology.selectedMap = -1;
		vpnTopology.canvasWidth = YAHOO.util.Dom.getViewportWidth() - 10;
		vpnTopology.canvasHeight = YAHOO.util.Dom.getViewportHeight() - parseInt(YAHOO.util.Dom.getStyle("toolBar", "height")) - 10;
		vpnTopology.serverWidth = vpnTopology.canvasWidth * vpnTopology.ratio > 200 ? 200 : vpnTopology.canvasWidth * vpnTopology.ratio;

		// set width/height
		YAHOO.util.Dom.setStyle("vpnTopology", "width", vpnTopology.canvasWidth + "px");
		// set canvas height
		YAHOO.util.Dom.setStyle("canvas", "height", vpnTopology.canvasHeight + "px");
		// create surface
		vpnTopology.createRafael("canvas", vpnTopology.canvasWidth, vpnTopology.canvasHeight);
		// Load icon images
		vpnTopology.loadIcons("<s:url includeParams="none" value="/images" />");
		// Request vpn topologys
		vpnTopology.requestVpnTopologys();
	}
	top.vpnTopologyIframeWindow = window;
}

function onHidePage(){
	clearTimeout(pollTimeoutId);
	clearTimeout(processingTimeoutId);
}

/*
* Pre-load icons
*/
vpnTopology.loadIcons = function(baseUrl) {
	vpnTopology.leafNodeIcons = new Array();
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/green/green_target_icon.png");
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/yellow/yellow_target_icon.png");
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/orange/orange_target_icon.png");
	vpnTopology.leafNodeIcons[vpnTopology.leafNodeIcons.length] = hm.util.loadImage(baseUrl + "/nodes/red/red_target_icon.png");
}

vpnTopology.requestVpnTopologys = function(){
	var url = '<s:url action="vpnServices" includeParams="none"></s:url>' + "?operation=vpnTopologys&id=" + vpnTopology.profileId +"&pageId="+ vpnTopology.pageId + "&mapId="+ vpnTopology.selectedMap + "&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, vpnTopology.processTopologys);
}

vpnTopology.processTopologys = function(o){
	eval("var data = " + o.responseText);
	var triggerIds = new Array();
	if(data.servers){
		vpnTopology.serverNodes = new Array();
		vpnTopology.serverNodesHash = new Array();
		for(var i=0; i<data.servers.length; i++){
			var vpnServerNode = {jsonNode: data.servers[i]};
			var avgY = vpnTopology.canvasHeight/data.servers.length;
			vpnServerNode.x = vpnTopology.canvasWidth/2;
			vpnServerNode.y = avgY*i + avgY/2;
			vpnTopology.addVpnNode(vpnServerNode);
			
			vpnTopology.serverNodes[vpnTopology.serverNodes.length] = vpnServerNode;
			vpnTopology.serverNodesHash[data.servers[i].nodeId] = vpnServerNode;
			triggerIds[triggerIds.length] = vpnServerNode.jsonNode.nodeId;
		}
	}
	if(data.clients){
		vpnTopology.clientNodes = new Array();
		vpnTopology.clientNodesHash = new Array();
		for(var i=0; i<data.clients.length; i++){
			var vpnClientNode = {jsonNode: data.clients[i]};
			var totalWidth = vpnTopology.canvasWidth - vpnTopology.serverWidth;
			var totalLot = Math.ceil(data.clients.length/(vpnTopology.queueCount*2))*2;
			var avgWidth = totalWidth/totalLot;
			var index = i%vpnTopology.queueCount;
			var isLeftSide =  Math.floor(i/vpnTopology.queueCount)%2 == 1;
			var currentLot = Math.floor(i/vpnTopology.queueCount)/2;
			var currentQueueCount = (vpnTopology.queueCount * (currentLot+1) > data.clients.length)? data.clients.length%vpnTopology.queueCount : vpnTopology.queueCount;
			var deltaX = vpnTopology.radian/(Math.ceil(currentQueueCount/2));

			if(isLeftSide){
				var avgX = (index < Math.ceil(currentQueueCount/2)) ? index*deltaX : (currentQueueCount -1 -index)*deltaX;
				vpnClientNode.x = vpnTopology.canvasWidth/2 - (currentLot * avgWidth + avgWidth/2) - avgX + 50;
			}else{
				var avgX = (index < Math.ceil(currentQueueCount/2)) ? index*deltaX : (currentQueueCount -1 -index)*deltaX;
				vpnClientNode.x = vpnTopology.serverWidth + vpnTopology.canvasWidth/2 + (currentLot * avgWidth + avgWidth/2) + avgX - 150;
			}
			var avgY = vpnTopology.canvasHeight/currentQueueCount;
			vpnClientNode.y = avgY*index + avgY/2;
			vpnTopology.addVpnNode(vpnClientNode);
			
			vpnTopology.clientNodes[vpnTopology.clientNodes.length] = vpnClientNode;
			vpnTopology.clientNodesHash[data.clients[i].nodeId] = vpnClientNode;
			triggerIds[triggerIds.length] = vpnClientNode.jsonNode.nodeId;
		}
	}
	if(data.links){
		vpnTopology.vpnLinks = new Array();
		var baseTimeLine = new Date().getTime();
		for(var i=0; i<data.links.length; i++){
			var vpnLink = {jsonNode: data.links[i], baseTimeLine: baseTimeLine};
			if (vpnTopology.addLink(vpnLink)) {
				vpnTopology.vpnLinks[vpnTopology.vpnLinks.length] = vpnLink;
			}
		}
	}
	if(data.maps){
		var mapSelector = document.getElementById("mapSelector");
		var mapValue = mapSelector.value;
		mapSelector.length = 0;
		mapSelector.length=data.maps.length + 1;
		mapSelector.options[0].value = -1;
		mapSelector.options[0].text = "All";
		for(var i = 0; i < data.maps.length; i ++) {
			mapSelector.options[i+1].value = data.maps[i].id;
			mapSelector.options[i+1].text = data.maps[i].n;
			if(mapValue && mapValue == data.maps[i].id){
				mapSelector.options[i+1].selected = true;
			}
		}
		document.getElementById("topology_filter").style.display = "";
	}
	
	if(data.primaryTunnel){
		vpnTopology.primaryTunnel = data.primaryTunnel;
	}else{
		vpnTopology.primaryTunnel = "";
	}

	if(data.backupTunnel){
		vpnTopology.backupTunnel = data.backupTunnel;
	}else{
		vpnTopology.backupTunnel = "";
	}
	
	if(triggerIds.length > 0){
		vpnTopology.createTooltips(triggerIds);
	}
	vpnTopology.startPollTimer();
}

vpnTopology.createTooltips = function(groupIds){
	if(null == vpnTopology.tooltips){
		// we'll set the tooltip text dynamically,
		vpnTopology.tooltips = new YAHOO.widget.Tooltip("tt", {width: "260px", autodismissdelay: 15000, effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.20}});
		// Set the text for the tooltip just before we display it.
	    vpnTopology.tooltips.contextTriggerEvent.subscribe(
	        function(type, args) {
	            var context = args[0];
	            var nodeId = context.id;
	            var label = "", content = "", currentNode, isVpnServer = false;
	            if(null != vpnTopology.clientNodesHash[nodeId]){
	            	currentNode = vpnTopology.clientNodesHash[nodeId];
	            	label = "VPN Client";
		        }else{
		        	currentNode = vpnTopology.serverNodesHash[nodeId];
		        	label = "VPN Server";
		        	isVpnServer = true;
			    }
			    if(null != currentNode){
			    	content += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
			    	content += "<tr><td valign=\"top\" width=\"100px\"><b class=\"vpnLabel\">Character:</b></td><td>"+label+"</td></tr>";
				    content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Hostname:</b></td><td>"+currentNode.jsonNode.apName+"</td></tr>";
				    content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Node ID:</b></td><td>"+currentNode.jsonNode.nodeId+"</td></tr>";
					if(isVpnServer){
						content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Connections:</b></td><td>"+currentNode.links.length+"</td></tr>";
					}else{
						var links = currentNode.links;
						for(var i=0; i<vpnTopology.serverNodes.length; i++){
							var serverNode = vpnTopology.serverNodes[i];
							var link;
							for(var j=0; j<links.length; j++){
								if(links[j].toNode.jsonNode.nodeId == serverNode.jsonNode.nodeId
										|| links[j].fromNode.jsonNode.nodeId == serverNode.jsonNode.nodeId){
									link = links[j];
								}
							}
							if(link){
								content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Tunnel Status:</b></td><td>"+vpnTopology.transferUpTime((new Date().getTime()-link.baseTimeLine) + link.jsonNode.upTime)+" ("+serverNode.jsonNode.apName+")</td></tr>";
							}else{
								content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Tunnel Status:</b></td><td>down ("+serverNode.jsonNode.apName+")</td></tr>";
							}
							link = null;// reset
						}
						//Add primary tunnel
						content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Primary Tunnel:</b></td><td>"+vpnTopology.primaryTunnel+"</td></tr>";
						//Add backup tunnel
						content += "<tr><td valign=\"top\"><b class=\"vpnLabel\">Backup Tunnel:</b></td><td>"+vpnTopology.backupTunnel+"</td></tr>";
					}
				    content += "</table>";
		            this.cfg.setProperty("text", content);
				}
	        }
	    );
	}
	vpnTopology.tooltips.cfg.setProperty("context", groupIds);
}

vpnTopology.transferUpTime = function(millisecond){
	var second = millisecond < 0 ? 0 : parseInt(millisecond/1000);
	var str_time;
	var int_year = parseInt(second / (3600 * 24 * 365));
	var remain_days = parseInt(second % (3600 * 24 * 365));
	var int_day = parseInt(remain_days / (3600 * 24));
	var remain_hours = parseInt(remain_days % (3600 * 24));
	var int_hour = parseInt(remain_hours / 3600);
	var remain_minutes = parseInt(remain_hours % 3600);
	var int_min = parseInt(remain_minutes / 60);
	var int_sec = parseInt(remain_minutes % 60);

	if (second >= 3600 * 24 * 365) {
		str_time = int_year + " Years " + int_day + " Days, " + int_hour
				+ " Hrs " + int_min + " Mins " + int_sec + " Secs";
	} else if (second >= 3600 * 24) {
		str_time = int_day + " Days, " + int_hour + " Hrs " + int_min
				+ " Mins " + int_sec + " Secs";
	} else if (second >= 3600) {
		str_time = int_hour + " Hrs " + int_min + " Mins " + int_sec
				+ " Secs";
	} else if (second >= 60) {
		str_time = int_min + " Mins " + int_sec + " Secs";
	} else {
		str_time = int_sec + " Secs";
	}
	return str_time;
}

vpnTopology.addVpnNode = function(vpnNode){
	vpnNode.links = new Array();
	
	vpnNode.iconCX = parseInt(vpnTopology.leafNodeIcons[0].width / 2);
	vpnNode.iconCY = parseInt(vpnTopology.leafNodeIcons[0].height / 2);
	
	var canvas = document.getElementById("canvas");
	var div = document.createElement("div");
	div.id = vpnNode.jsonNode.nodeId;
	div.style.display = "block";
	div.style.position = "absolute";
	div.style.left = (vpnNode.x-vpnNode.iconCX) + "px";
	div.style.top = (vpnNode.y-vpnNode.iconCY) + "px";

	var img = document.createElement("img");
	img.src = vpnTopology.leafNodeIcons[vpnNode.jsonNode.s].src;
	var imgDiv = document.createElement("div");
	imgDiv.id = 'l' + vpnNode.jsonNode.nodeId;
	imgDiv.appendChild(img);

	var labelDiv = document.createElement("div");
	labelDiv.id = 'll' + vpnNode.jsonNode.nodeId;
	labelDiv.className = "leafLabel";
	labelDiv.style.left = "33px";
	labelDiv.style.top = "9px";
	labelDiv.appendChild(document.createTextNode(vpnNode.jsonNode.apName));
	
	div.appendChild(imgDiv);
	div.appendChild(labelDiv);
	
	canvas.appendChild(div);
	vpnNode.nodeDiv = div;
}

vpnTopology.addLink = function(vpnLink){
	var fromNode = vpnTopology.serverNodesHash[vpnLink.jsonNode.from]||vpnTopology.clientNodesHash[vpnLink.jsonNode.from];
	var toNode = vpnTopology.serverNodesHash[vpnLink.jsonNode.to]||vpnTopology.clientNodesHash[vpnLink.jsonNode.to];
	if (!fromNode) {
	//	alert("Link refers to non existing from node: " + vpnLink.jsonNode.from);
		return false;
	}
	if (!toNode) {
	//	alert("Link refers to non existing to node: " + vpnLink.jsonNode.to);
		return false;
	}
	vpnLink.fromNode = fromNode;
	vpnLink.toNode = toNode;

	fromNode.links[fromNode.links.length] = vpnLink;
	toNode.links[toNode.links.length] = vpnLink;

	var line = vpnTopology.getLine(vpnLink);
	vpnTopology.drawLink(vpnLink, line);
	return true;
}

vpnTopology.getLine = function(vpnLink){
	var X1 = vpnLink.fromNode.x;
	var Y1 = vpnLink.fromNode.y;
	var X2 = vpnLink.toNode.x;
	var Y2 = vpnLink.toNode.y;
	var line = { x1:X1, y1:Y1, x2:X2, y2:Y2};
	return line;
}

vpnTopology.drawLink = function(vpnLink, line){
	if (vpnLink.jsonNode.connected) {
		var lineColor = "#00AA00";
	} else {
		var lineColor = "#FF0000";
	}
	vpnLink.line = vpnTopology.createRafaelLine(vpnTopology.surface, line, lineColor, 1, 2);
}

vpnTopology.createRafaelLine = function(surface, line, lineColor, alpha, width) {
	var x1 = line.x1; var y1 = line.y1; var x2 = line.x2; var y2 = line.y2;
	if (YAHOO.env.ua.ie > 0) {
		x1 += 1; y1 += 1; x2 += 1; y2 += 1;
	}
	return surface.path({stroke: lineColor, 'stroke-width': width, "stroke-opacity": alpha}).moveTo(x1, y1).lineTo(x2, y2);
}

vpnTopology.createRafael = function(div, width, height){
	vpnTopology.surface = Raphael(div, width, height);
}

vpnTopology.removeShape = function(surface, shape){
	if (shape) {
		shape.remove();
	}
}

vpnTopology.startPollTimer = function(){
	var interval = 15;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (pollCount++ < total) {
		pollTimeoutId = setTimeout("vpnTopology.pollVpnTopology();", interval * 1000);  // seconds
	}
}

vpnTopology.pollVpnTopology = function(){
	var url = '<s:url action="vpnServices" includeParams="none"></s:url>' + "?operation=pollVpnTopology&id=" + vpnTopology.profileId +"&pageId="+ vpnTopology.pageId + "&mapId="+ vpnTopology.selectedMap + "&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, vpnTopology.updateVpnTopology);
}

vpnTopology.updateVpnTopology = function(o){
	eval("var data = " + o.responseText);
	if(data.updated){
		vpnTopology.removeLinks();
		vpnTopology.processTopologys(o);
	} else {
		vpnTopology.startPollTimer();
	}
}

/*
* Remove links
*/
vpnTopology.removeLinks = function() {
	var anchor = document.getElementById("canvas");
	for (var i = 0; i < vpnTopology.vpnLinks.length; i++) {
		var link = vpnTopology.vpnLinks[i];
		vpnTopology.removeShape(vpnTopology.surface, link.line);
	}
	vpnTopology.vpnLinks = new Array();
	for (var i = 0; i < vpnTopology.serverNodes.length; i++) {
		var node = vpnTopology.serverNodes[i];
		anchor.removeChild(node.nodeDiv);
		node.links = new Array();
	}
	for (var i = 0; i < vpnTopology.clientNodes.length; i++) {
		var node = vpnTopology.clientNodes[i];
		anchor.removeChild(node.nodeDiv);
		node.links = new Array();
	}
}

function refreshVpnTopology(){
	if(null == waitingPanel){
		createWaitingPanel();
	}
	waitingPanel.show();
	var url = '<s:url action="vpnServices" includeParams="none"></s:url>' + "?operation=refreshVpnTopologys&id=" + vpnTopology.profileId +"&pageId="+ vpnTopology.pageId + "&mapId="+ vpnTopology.selectedMap + "&ignore="+ + new Date().getTime();
	ajaxRequest(null, url, vpnTopology.refreshVpnTopologyResult);
}

vpnTopology.refreshVpnTopologyResult = function(o){
	eval("var data = " + o.responseText);
	if(null != waitingPanel){
		waitingPanel.hide();
	}
	if(data.error){
		vpnTopology.showInfo(data.error);
	}else{
		vpnTopology.showInfo("<s:text name='info.hiveAp.vpn.refresh.success' />");
		clearTimeout(pollTimeoutId);
		vpnTopology.pollVpnTopology();
	}
}

vpnTopology.showInfo = function(info){
	clearTimeout(processingTimeoutId);
	var td = document.getElementById("note");
	//hm.util.replaceChildren(td, document.createTextNode(info+"<br>sss"));
	td.innerHTML = info;
	hm.util.show('processing');
	vpnTopology.delayHideProcessing(5);
}

var processingTimeoutId;
vpnTopology.delayHideProcessing = function(seconds) {
	processingTimeoutId = setTimeout("hm.util.hide('processing');", seconds * 1000);  // seconds
}

function showMapVpnTopology(mapId){
	clearTimeout(pollTimeoutId);
	vpnTopology.selectedMap = mapId;
	vpnTopology.pollVpnTopology();
}

function updateTopologyDialogTitle(str) {
	Get("vpnTopologyTitle").innerHTML = str;
}
//-->
</script>
<div id="vpnTopology" style="position: relative;">
<s:if test="%{jsonMode}">
<div id="toolBar" class="iconToolbar">
    <span class="npcHead1" id="vpnTopologyTitle" style="float: left;padding: 8px 0 0 70px;display: inline-block;"><s:text name="config.vpn.service.topology"/></span>
    <span class="npcButton" style="padding: 8px 50px 0 0;display: inline-block;float: right;">
        <a href="javascript: void(0);" class="btCurrent"
            onclick="refreshVpnTopology();" title="<s:text name="common.button.refresh"/>">
            <span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.refresh"/></span>
        </a>
    </span>
    <span id="topology_filter" style="padding: 8px 20px 0 0;display: none;float: right;">Filter by map: <select id="mapSelector" style="width: 100px" onchange="showMapVpnTopology(this.value);"></select></span>
    <div style="clear: both;"></div>
</div>
</s:if>
<s:else>
<div id="toolBar" style="width:100%; height:30px; border-bottom: 1px solid #999;" align="left">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="button" name="ignore" value="Refresh" class="button" onclick="refreshVpnTopology();" <s:property value="writeDisabled" />></td>
		<td><div id="topology_filter" style="float: right; padding-right: 20px; display: none;">Filter by map: <select id="mapSelector" style="width: 100px" onchange="showMapVpnTopology(this.value);"></select></div></td>
	</tr>
</table>
</div>
</s:else>
<div id="processing" style="display: none; position: absolute; top: 30px; width: 100%; z-index: 999;">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">
	<tr>
		<td height="5"></td>
	</tr>
	<tr>
		<td class="noteInfo" id="note">Your request is being processed ...</td>
	</tr>
	<tr>
		<td height="6"></td>
	</tr>
</table>
</div>
<div id="canvas"></div>
</div>