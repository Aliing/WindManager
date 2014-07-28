<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.ah.bo.hiveap.AhInterface"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>

<script>

var DEVICE_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;
var BR_MAX_ROUTE_COUNT = <%=AhInterface.BR_MAX_ROUTE_COUNT%>;

function showBRStaticRouting(){
	showHideContent("brStaticRouting", "")
}

function validateBRAdvertiseRoute(operation){
	if(operation != 'create2' && operation != 'update2'){
		return true;
	}

	var allAdvertiseRoutes = document.getElementsByName("advertiseCvg");
	if(!allAdvertiseRoutes){
		return true;
	}
	var routeCounts = 0;
	for(var i=0; i<allAdvertiseRoutes.length; i++){
		var el = allAdvertiseRoutes[i];
		if(el.checked){
			routeCounts ++;
		}
	}

	if(routeCounts > BR_MAX_ROUTE_COUNT && $("#"+formName+"_dataSource_deviceType").val() == DEVICE_TYPE_BRANCH_ROUTER){
		var message = '<s:text name="error.be.config.create.br.max.advertiseRoute">
						<s:param><%=AhInterface.BR_MAX_ROUTE_COUNT%></s:param>
					</s:text>'
		var eleObj = document.getElementById("error_message_static_route");
		hm.util.reportFieldError(eleObj, message);
		showBRStaticRouting();
		showHideContent("networkSettings", "none");
		eleObj.focus();
		return false;
	}else{
		return true;
	}
}

</script>
<div id="brStaticRoutingStyle">
	<s:if test="%{dataSource.deviceInfo.onlyRouterFunc}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.routingProfiles.staticRoutes" />','brStaticRouting');</script></td>
		</tr>
		<tr>
			<td style="padding-left:20px;">
			<div id="brStaticRouting" style="display: <s:property value="%{dataSource.brStaticRoutingDisplayStyle}"/>">
			<fieldset>
				<table cellspacing="0" cellpadding="0" border="0" class="embedded">
					<tr>
						<td height="10"></td>
					</tr>
					<tr id="newButtonBrStaticRouting">
						<td colspan="3" style="padding-bottom: 2px;" >
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><input type="button" name="ignore" value="New"
										class="button" onClick="showCreateSection('brStaticRouting');"
										<s:property value="writeDisabled" />></td>
									<td><input type="button" name="ignore" value="Remove"
										class="button" <s:property value="writeDisabled" />
										onClick="doRemoveBrStaticRouting();"></td>
									<td>
										<a style="padding-left: 15px;" href="javascript: openPortDetails();"><s:text name="hvieAp.brstaticRoute.portDetails"/></a>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr style="display:none" id="createButtonBrStaticRouting">
						<td colspan="3" style="padding-bottom: 2px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><input type="button" name="ignore" value="<s:text name="button.apply"/>"
									class="button" <s:property value="writeDisabled" /> onClick="doAddBrStaticRouting();"></td>
								<td><input type="button" name="ignore" value="Remove"
									class="button" <s:property value="writeDisabled" />
									onClick="doRemoveBrStaticRouting();"></td>
								<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
									class="button" onClick="hideCreateSection('brStaticRouting');"></td>
								<td>
									<a style="padding-left: 15px;" href="javascript: openPortDetails();"><s:text name="hvieAp.brstaticRoute.portDetails"/></a>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr id="headerSectionBrStaticRouting">
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<th align="left" style="padding-left: 0;" width="30px"><input
										type="checkbox" id="checkAllBrStaticRouting"
										onClick="toggleCheckAllBrStaticRouting(this);"></th>
									<th align="left" width="200px"><s:text
										name="config.routingProfiles.ip" /></th>
									<th align="left" width="200px"><s:text
										name="config.routingProfiles.netmask" /></th>
									<th align="left" width="200px"><s:text
										name="config.routingProfiles.gateway" /></th>
									<th align="left" width="200px"><s:text
										name="hiveAp.br.staticrouting.advertise" /></th>
								</tr>
							</table>
						</td>
					</tr>
					<tr style="display:none" id="createSectionBrStaticRouting">
						<td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="listHead" width="30px">&nbsp;</td>
									<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteIpInput" maxlength="15"
										onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteMaskInput" maxlength="15"
										onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									<td class="listHead" valign="top" width="200px"><s:textfield size="20" name="brStaticRouteGwInput" maxlength="15"
										onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
									<td class="listHead" valign="top" align ="center" width="200px"><s:checkbox name="advertiseCvg" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
						<table border="0" cellspacing="0" cellpadding="0" id="tbbrroute_id">
							<tr>
								<td id="error_message_static_route"/>
							</tr>
						<s:iterator value="%{dataSource.ipRoutes}" status="status">
							<tr>
								<td class="listCheck" width="30px"><s:checkbox name="brStaticRouteingIndices"
									fieldValue="%{#status.index}" /></td>
								<td class="list" width="200px"><s:property value="sourceIp" /></td>
								<td class="list" width="200px"><s:property value="netmask" /></td>
								<td class="list" width="200px"><s:property value="gateway" /></td>
								<td class="list" width="200px" align ="center"><s:checkbox name ="advertiseCvg" disabled="true" /></td>
							</tr>
						</s:iterator>
						</table>
						</td>
					</tr>
					<tr>
						<td colspan="5" width="100%">
						<table id="intBrStaticRouitngTblGridCount" width="100%" cellspacing="0" cellpadding="0" border="0" class="embedded" >
						<s:if test="%{gridCount > 0}">
							<s:generator separator="," val="%{' '}" count="%{gridCount}">
								<s:iterator>
									<tr>
										<td class="list" colspan="5">&nbsp;</td>
									</tr>
								</s:iterator>
							</s:generator>
						</s:if>
					</table>
					</td>
				</tr>
				</table>
				</fieldset>
				</div>
			</td>
		</tr>
	</table>
	</s:if>
</div>