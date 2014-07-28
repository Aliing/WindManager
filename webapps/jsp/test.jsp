<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
function submitAction(operation) {
	showProcessing();
	document.configTest.operation.value = operation;
    document.configTest.submit();
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap>Test</td>');
}
function openWebSsh(){
	var url="<s:url action='configTest' includeParams='none'/>" + "?operation=testSsh";
	window.open(url,"","scrollbars=yes,width=700px,height=650px,resizable=yes,top=150,left=250");
}
</script>

<div id="content"><s:form action="configTest">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Create HPs"
						class="button" onClick="submitAction('testCreate');"></td>
					<td><input type="button" name="ignore" value="Remove HPs"
						class="button" onClick="submitAction('testRemove');"></td>
					<td>
					<button name="ignore" class="row"
						onClick="submitAction('testQuery');">Query</button>
					</td>
					<td>
					<button name="ignore" class="row"
						onClick="submitAction('testUpdate');">Update</button>
					</td>
					<td>
					<button name="ignore" class="row"
						onClick="submitAction('testMap');">Maps</button>
					</td>
					<td>
					<button name="ignore" class="row"
						onClick="submitAction('testAdmin');">Admin</button>
					</td>
				</tr>
				<tr>
					<td><input type="button" name="ignore" value="Create APs"
						class="button" onClick="submitAction('testHiveApsCreate');"></td>
					<td><input type="button" name="ignore" value="Update APs"
						class="button" onClick="submitAction('testHiveApsUpdate');"></td>
					<td><input type="button" name="ignore" value="Remove APs"
						class="button" onClick="submitAction('testHiveApsRemove');"></td>
					<td><input type="button" name="ignore" value="Bulk Create"
						class="button" onClick="submitAction('testHiveApsBulkCreate');"></td>
					<td><input type="button" name="ignore" value="Bulk Update"
						class="button" onClick="submitAction('testHiveApsBulkUpdate');"></td>
					<td><input type="button" name="ignore" value="Clients"
						class="button" onClick="submitAction('testClientRssi');"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" name="ignore"
						value="Create SSID Profiles" class="button long"
						onClick="submitAction('testCreateSsid');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Remove SSID Profiles" class="button long"
						onClick="submitAction('testRemoveSsid');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="statistics data" class="button long"
						onClick="submitAction('testAddPerformace');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="query statistics" class="button long"
						onClick="submitAction('testQueryStatistics');"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" name="ignore"
						value="Create Alarms" class="button long"
						onClick="submitAction('testCreateAlarms');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Remove Alarms" class="button long"
						onClick="submitAction('testRemoveAlarms');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Create Events" class="button long"
						onClick="submitAction('testCreateEvents');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Remove Events" class="button long"
						onClick="submitAction('testRemoveEvents');"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" name="ignore"
						value="Network Objects" class="button long"
						onClick="submitAction('testNetObjects');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Create Mobile Users" class="button long"
						onClick="submitAction('testAddMobileUser');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Open SSH Panel" class="button long"
						onClick="javascript:openWebSsh();"></td>
					<td colspan="2">Click <a
						href='<s:url action="configTest"><s:param name="operation" value="%{'download'}"/></s:url>'>here</a>
					to download.</td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" name="ignore"
						value="Create Clients" class="button long"
						onClick="submitAction('testCreateClients');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Bulk Create Clients" class="button long"
						onClick="submitAction('testBulkCreateClients');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Fetch/Update Clients" class="button long"
						onClick="submitAction('testUpdateClients');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Batch Update Clients" class="button long"
						onClick="submitAction('testBatchUpdateClients');"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" name="ignore"
						value="Query by Client MAC" class="button long"
						onClick="submitAction('testQueryClientsBy1Mac');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Query by Cl/AP MAC" class="button long"
						onClick="submitAction('testQueryClientsBy2Mac');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Remove Clients" class="button long"
						onClick="submitAction('testRemoveClients');"></td>
					<td colspan="2"><input type="button" name="ignore"
						value="Place APs" class="button long"
						onClick="submitAction('testPlaceAps');"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<script>
function delayHideNotes() {
}
</script>
		<tr>
			<td height="10"></td>
		</tr>
		<tr>
			<td>
			<table width="450" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
					<fieldset style="width: 350px"><legend>Test
					Parameters</legend>
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td><img
								src="<%=request.getContextPath()%>/images/spacer.gif" width="1"
								height="4" class="dblk"></td>
						</tr>
						<tr>
							<td class="labelT1"><label>Number of threads: </label></td>
							<td><s:textfield name="threads" value="5" /></td>
						</tr>
						<tr>
							<td class="labelT1"><label>Number of BOs per thread:
							</label></td>
							<td><s:textfield name="selectedId" value="100" /></td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td height="10"></td>
		</tr>
		<tr>
			<%--
					<td colspan="2"><applet
						codebase="<s:url value="/applets/" includeParams="none"/>"
						code="com.ah.ssh.ConnectApplet.class" archive="lib/ganymed-ssh2-build210.jar" width=200 height=30>
						<param name="hostname" value="10.3.10.51">
						You need a Java-enabled browser to view this. </applet></td>
			<td colspan="2"><applet
				codebase="<s:url value="/applets/" includeParams="none"/>"
				code="com.mindbright.application.MindTerm.class"
				archive="lib/mindterm.jar" width=0 height=0>
				<param name="cabinets" value="mindterm.cab">
				<param NAME="sepframe" value="true">
				<param NAME="debug" value="true">
			</applet></td>
			<td colspan="2"><applet
				codebase="<s:url value="/applets/" includeParams="none"/>"
				code="com.mindbright.application.MindTerm.class"
				archive="lib/mindterm.jar" width=700 height=410>
				<param name="cabinets" value="lib/mindterm.cab">
				<param name="server" value="gemini">
				<param name="username" value="admin">
				<param name="password" value="aerohive">
				<param name="quiet" value="true">
				<param name="term-type" value="xterm-color">
				<param name="geometry" value="100x24">
				<param name="sepframe" value="false">
				<param name="debug" value="true">
				<param name="exit-on-logout" value="true">
				<param name="savepasswords" value="yes">
				<param name="menus" value="yes">
			</applet></td>
  --%>
			<%--
			<td colspan="2"><applet
				codebase="<s:url value="/applets/" includeParams="none"/>"
				code="com.jcraft.jcterm.JCTermApplet.class"
				archive="lib/jsch-0.1.36.jar,lib/signed-term.jar" width=810
				height=490> You need a Java-enabled browser to view this. </applet></td>
  --%>
		</tr>
	</table>
</s:form></div>
