<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content"><s:form action="haMonitor">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td height="6px"></td>
		</tr>
		<tr>
			<td>
				<s:if test="%{inHaStatus}">
					<table border="0" cellspacing="0" cellpadding="0" class="editBox" width="500px">					
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td class="noteInfo" style="padding-left:10px">
								<s:property value="%{runMessage}"/>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td style="padding-left:10px">
							<table cellspacing="0" cellpadding="0" border="0" class="view">
								<tr>
									<th align="left" nowrap><s:text name="home.ha.monitoring.table.th.node.ip"/></th>
									<th align="left" nowrap><s:text name="home.ha.monitoring.table.th.node.status"/></th>
									<th align="left" nowrap><s:text name="home.ha.monitoring.table.th.node.up.time"/></th>
								</tr>
								<tr class="odd">
									<td class="list"><s:property value="%{node1Ip}"/></td>
									<td class="list"><s:property value="%{node1Status}"/></td>
									<td class="list"><s:property value="%{node1Time}"/></td>
								</tr>
								<tr class="even">
									<td class="list"><a href='https://<s:property value="%{node2Ip}"/>/hm'><s:property value="%{node2Ip}"/></a></td>
									<td class="list"><s:property value="%{node2Status}"/></td>
									<td class="list"><s:property value="%{node2Time}"/></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td class="noteInfo" style="padding-left:10px">
							<fieldset style="width:450px">
								<s:property value="%{dbRunMessage}" escape="false" />
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td class="noteInfo" style="padding-left:10px">
							<fieldset style="width:450px">
								<s:property value="%{lastSwitchover}"/>
							</fieldset>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
					</table>
				</s:if>
				<s:else>
					<table border="0" cellspacing="0" cellpadding="0" class="editBox" width="100%">
						<tr>
							<td height="10px"></td>
						</tr>
						<tr>
							<td class="noteInfo" style="padding-left:10px">
								<s:property value="%{runMessage}"/>
							</td>
						</tr>
						<tr>
							<td height="10px"></td>
						</tr>
					</table>
				</s:else>
			</td>
		</tr>
	</table>
</s:form></div>
