<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style>
.myEllipsis {  
    width: 120px;   
    white-space: nowrap;  
    overflow: hidden;  
    text-overflow: ellipsis;  
    -o-text-overflow: ellipsis; /*For Opera*/  
    -ms-text-overflow: ellipsis; /*For IE8*/  
    -moz-binding: url(assets/xml/ellipsis.xml#ellipsis); /*For Firefox3.x*/  
}  
</style>
<div id="content">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<s:if test="configured">
		<tr>
			<td class="noteInfo">
				<s:text name="monitor.hiveAp.port.details.note"/>
			</td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view"  width="100%">
				<tr>
					<th align="left" nowrap><s:text name="monitor.hiveAp.port.label" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.port.admin.status" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.port.link.status" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.port.network" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.port.exactip" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.network.interface" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.network.subnet" /></th>
					<th align="left" nowrap><s:text name="monitor.hiveAp.switch.port.type" /></th>
				</tr>
			<s:iterator value="%{deviceInterfaceAdapters}" status="status">
				<tr>
					<td class="list" align="left"><s:property value="deviceInterface.interfaceName"/>&nbsp;</td>
					<td class="list" align="left"><s:property value="deviceInterface.adminStateString"/>&nbsp;</td>
					<td class="list" align="left"><s:property value="deviceInterface.linkStatusString"/>&nbsp;</td>
					<!-- <td class="list" align="left">
						<s:iterator value="deviceInterface.lanProfile.vpnNetworks" status="status">
							<s:property value="networkVlanString" />&nbsp;<br/>
						</s:iterator>
					</td>
					 -->
					<td class="list myEllipsis" align="left">
						<s:iterator value="mgtInterface4BrReports" status="status">
							<label title="<s:property value="networkName" />"><s:property value="networkName" /></label>&nbsp;<br/>
						</s:iterator>
					</td>
					<td class="list" align="left">
						<s:iterator value="mgtInterface4BrReports" status="status">
							<s:property value="exactIP" />&nbsp;<br/>
						</s:iterator>
					</td>
					<td class="list" align="left">
						<s:iterator value="mgtInterface4BrReports" status="status">
							<s:property value="mgtName" />&nbsp;<br/>
						</s:iterator>
					</td>
					<td class="list" align="left">
						<s:iterator value="mgtInterface4BrReports" status="status">
							<s:property value="subnet" />&nbsp;<br/>
						</s:iterator>
					</td>
					<td class="list" align="left"><s:property value="accessProfileName"/>&nbsp;</td>
				</tr>
			</s:iterator>
			</table>
			</td>
		</tr>
		</s:if>
		<s:else>
		<tr>
			<td style="padding-top: 10px;" width="400px" align="left"><s:text name="hvieAp.brstaticRoute.portDetails.notConfigured.msg"/></td>
		</tr>
		</s:else>
	</table>
</div>