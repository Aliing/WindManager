<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<script
	src="<s:url value="/js/hm.util.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script>
function onLoadCanvas() {
	top.startTime = "<s:property value="%{start}" />";
	top.requestBldNodes(window, floors.length);
	top.enableHeatChannelBoxes();
	reqNails();
}
var floors = <s:property escape="false" value="%{JSONString}" />;
function reqNails() {
	for (var i = 0; i < floors.length; i++) {
		var td = document.getElementById("td" + i);
		var img = document.createElement("img");
		img.width = <s:property value="%{imageWidth}" />;
		img.height = <s:property value="%{imageHeight}" />;
		img.className = "floorImg";
		img.style.cursor = "pointer";
		reqImg(floors[i], img);
		td.appendChild(img);
	}
}
function reqImg(floor, img) {
	if (top.getFrequency() == 1) {
		var chis = floor.ch2is;
		var chs = floor.ch2s;
	} else {
		var chis = floor.ch1is;
		var chs = floor.ch1s;
	}
	img.src = "mapBld.action?operation=floor&id="+floor.id+floor.apids+chs+chis+"&ignore="+new Date().getTime();
}
function selectFloor(floorId) {
	top.hm.map.selectNewTreeNode(floorId);
}
function updateNails() {
	for (var i = 0; i < floors.length; i++) {
		var td = document.getElementById("td" + i);
		var img = td.firstChild;
		reqImg(floors[i], img);
	}
}
function originChanged(id) {
	var ox = document.getElementById("ox" + id).firstChild.value;
	var oy = document.getElementById("oy" + id).firstChild.value;
	top.updateOrigin(floors[id].id, ox, oy);
}
</script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style>
html,body {
	width: 100%;
	height: 100%;
	border: none;
	padding: 0 0 0 0;
	margin: 0 0 0 0;
}

input,td,textarea,th {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	font-size: 12px;
}

input.button,button.row {
	margin-top: 0px;
	margin-right: 2px;
	margin-bottom: 0px;
	width: 80px;
	font-family: Arial, Helvetica, Verdana sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #003366;
}

input.button:disabled {
	color: #9d9daf;
}

fieldset {
	border: 1px solid #999;
	padding: 0px 6px 8px 9px;
}

legend {
	padding: 1px 4px 1px 4px;
	border-color: #999 #ccc #ccc #999;
	border-style: solid;
	border-width: 1px;
	background: #eee;
}

td.floor {
	padding: 3px 0 0 5px;
	color: #003366;
	font-weight: bold;
	font-size: 14px;
}

td.labelB1 {
	padding: 4px 0px 0px 6px;
}

td.labelB2 {
	padding-top: 4px;
	color: #003366;
}

img.floorImg {
	background-color: #cccccc;
	display: block;
}
</style>
</head>
<body class="yui-skin-sam" onload="onLoadCanvas()">
<table border="0" cellspacing="0" cellpadding="0">
	<s:iterator value="page" status="status">
		<tr>
			<td id="td<s:property value="%{#status.index}"/>" onclick="selectFloor('<s:property value="%{floor.id}" />');"></td>
			<td valign="top" width="100%">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td style="border-bottom: 1px solid #646464;" width="100%"><img
						src="<s:url value="/images/spacer.gif" includeParams="none"/>"
						height="15" style="display: block;" /></td>
				</tr>
				<tr>
					<td class="floor"><s:property value="%{floor.mapName}" /></td>
				</tr>
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="140"><img
								src="<s:url value="/images/spacer.gif" includeParams="none"/>"
								height="1" style="display: block;" /></td>
						</tr>
						<s:if test="%{vpnCount > 0}">
						<tr>
							<td class="labelB1" width="160px;" nowrap>Number of VPN Gateways</td>
							<td class="labelB2"><s:property value="%{vpnCount}" /></td>
						</tr>
						</s:if>
						<s:if test="%{srCount > 0}">
						<tr>
							<td class="labelB1" width="160px;" nowrap>Number of SRs</td>
							<td class="labelB2"><s:property value="%{srCount}" /></td>
						</tr>
						</s:if>
						<s:if test="%{brCount > 0}">
						<tr>
							<td class="labelB1" width="160px;" nowrap>Number of BRs</td>
							<td class="labelB2"><s:property value="%{brCount}" /></td>
						</tr>
						</s:if>
						<s:if test="%{apCount > 0}">
						<tr>
							<td class="labelB1" width="160px;" nowrap>Number of APs</td>
							<td class="labelB2"><s:property value="%{apCount}" /></td>
						</tr>
						</s:if>
						<s:if test="%{apCount == 0 && brCount == 0 && srCount ==0 && vpnCount == 0}">
						<tr>
							<td class="labelB1" width="160px;" nowrap>Number of Devices</td>
							<td class="labelB2">0</td>
						</tr>
						</s:if>
						<s:if test="%{floor.perimeter.size == 0}">
							<s:if test="%{floor.actualWidth > 0}">
								<tr>
									<td class="labelB1" nowrap>Service Area</td>
									<td class="labelB2" nowrap>n/a&nbsp;&nbsp;<font
										color="#000">(<s:text name="warn.topo.none.perimiters"/>)</font></td>
								</tr>
							</s:if>
						</s:if>
						<s:else>
							<tr>
								<td height="3px"></td>
							</tr>
							<tr>
								<td class="labelB1" nowrap>Service Area</td>
								<td class="labelB2"><s:property value="%{areaFeet}" />&nbsp;&nbsp;<font
									color="#000">sq ft</font></td>
							</tr>
							<tr>
								<td class="labelB1" align="right" style="padding-right: 6px;">or</td>
								<td class="labelB2"><s:property value="%{areaMeters}" />&nbsp;&nbsp;<font
									color="#000">sq m</font></td>
							</tr>
							<s:if test="%{apCount > 0 || brCount > 0}">
								<tr>
									<td height="3px"></td>
								</tr>
								<tr>
									<td class="labelB1" nowrap>Average Area per <s:if test="%{apCount > 0 && brCount > 0}">AP/BR</s:if><s:elseif test="%{apCount > 0}">AP</s:elseif><s:else>BR</s:else></td>
									<td class="labelB2"><s:property value="%{areaApFeet}" />&nbsp;&nbsp;<font
										color="#000">sq ft</font></td>
								</tr>
								<tr>
									<td class="labelB1" align="right" style="padding-right: 6px;">or</td>
									<td class="labelB2"><s:property value="%{areaApMeters}" />&nbsp;&nbsp;<font
										color="#000">sq m</font></td>
								</tr>
							</s:if>
						</s:else>
					</table>
					</td>
				</tr>
				<s:if test="%{floor.actualWidth > 0}">
					<tr>
						<td style="padding-top: 12px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding-left: 2px;">
								<fieldset><legend>Floor Alignment</legend>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding: 8px 6px 0 0;"><b>X</b></td>
										<td nowrap style="padding-top: 8px;"
											id="ox<s:property value="%{#status.index}"/>"><s:textfield
											size="6" name="originX" maxlength="16"
											onkeypress="return hm.util.keyPressPermit(event,'tendot');" />&nbsp;<s:property
											value="%{lengthUnit}" /></td>
										<td style="padding-left: 20px;" rowspan="2"><input
											type="button" name="ignore" value="Update" class="button"
											onClick="originChanged(<s:property value="%{#status.index}"/>);"
											<s:property value="mapWriteDisabled"/>></td>
									</tr>
									<tr>
										<td style="padding: 4px 6px 0 0;"><b>Y</b></td>
										<td style="padding-top: 4px;"
											id="oy<s:property value="%{#status.index}"/>"><s:textfield
											size="6" name="originY" maxlength="16"
											onkeypress="return hm.util.keyPressPermit(event,'tendot');" />&nbsp;<s:property
											value="%{lengthUnit}" /></td>
									</tr>
								</table>
								</fieldset>
								</td>
								<td width="100%"></td>
							</tr>
						</table>
						</td>
					</tr>
				</s:if>
			</table>
			</td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
