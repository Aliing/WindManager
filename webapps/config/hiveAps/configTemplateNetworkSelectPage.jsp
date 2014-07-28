<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'configTemplateNetworkSelectPage';
var preVlanId='<s:property value="%{dataSource.preVlanId}"/>';
YAHOO.util.Event.onContentReady("selectNetworkIdDiv", adjustEditLink, this);
</script>

<div id="content" style="padding: 0"><s:form action="networkPolicy" name="configTemplateNetworkSelectPage" id="configTemplateNetworkSelectPage">
<s:hidden name="operation" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><div id="errNote" class="noteError"></div></td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td>
							<ah:checkList name="selectNetworkId" multiple="false" width="100%" itemWidth="175px" list="networkObjectList" listKey="id" listValue="value" value="selectNetworkId" editEvent="editNetworkObjectDialog" cloneEvent="cloneNetworkObjectDialog"/>
						</td>
					</tr>
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="finishSelectNetworkObject();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
									<td width="40px">&nbsp;</td>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" onclick="newNetworkObject();" title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text name="config.networkpolicy.button.new"/></span></a></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</s:form>
</div>
