<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'gmClientMonitor';
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

<div id="content">
	<s:form action="gmClientMonitor">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<tiles:insertDefinition name="context" />
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0" class="view">
						<tr>
							<s:iterator value="%{selectedColumns}">
								<s:if test="%{columnId == 4}">
									<th>
										<ah:sort name="clientMac" key="gml.clientmonitor.clientMac" />
									</th>
								</s:if>
								<s:if test="%{columnId == 1}">
									<th>
										<ah:sort name="clientUsername" key="gml.clientmonitor.userName" />
									</th>
								</s:if>
								<s:if test="%{columnId == 2}">
									<th>
										<ah:sort name="clientIP" key="gml.clientmonitor.ipAddress" />
									</th>
								</s:if>
								<s:if test="%{columnId == 3}">
									<th>
										<s:text name="gml.clientmonitor.userGroup" />
									</th>
								</s:if>
								<s:if test="%{columnId == 5}">
									<th>
										<ah:sort name="startTimeStamp" key="gml.clientmonitor.sessionStarted" />
									</th>
								</s:if>
								<s:if test="%{columnId == 6}">
									<th>
										<s:text name="gml.clientmonitor.sessionTime" />
									</th>
								</s:if>
							</s:iterator>
						</tr>
						<s:if test="%{page.size() == 0}">
							<ah:emptyList />
						</s:if>
						<tiles:insertDefinition name="selectAll" />
						<s:iterator value="page" status="status">
							<tiles:insertDefinition name="rowClass" />
							<tr class="<s:property value="%{#rowClass}"/>">
								<s:iterator value="%{selectedColumns}">
									<s:if test="%{columnId == 4}">
										<td class="list">
											<s:property value="clientMac" />
										</td>
									</s:if>
									<s:if test="%{columnId == 1}">
										<td class="list">
											<s:property value="clientUsername" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 2}">
										<td class="list">
											<s:property value="clientIP" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 3}">
										<td class="list">
											<s:property value="clientUserProfId" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 5}">
										<td class="list">
											<s:property value="startTimeString" />
											&nbsp;
										</td>
									</s:if>
									<s:if test="%{columnId == 6}">
										<td class="list">
											<s:property value="durationString" />
											&nbsp;
										</td>
									</s:if>
								</s:iterator>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
