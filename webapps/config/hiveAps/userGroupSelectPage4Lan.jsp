<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var formName = 'userGroupSelectPage4Lan';
function prepareMgtAdvancedSaveBtPerrmit(){
	<s:if test="%{updateDisabled!=''}">
		Get("btNewUserGroup").style.display="none";
		Get("btOkUserGroup").style.display="none";
	</s:if>
	<s:else>
		Get("btNewUserGroup").style.display="";
		Get("btOkUserGroup").style.display="";
	</s:else>
}
window.setTimeout("prepareMgtAdvancedSaveBtPerrmit()", 100);
</script>

<div id="content" style="padding: 0"><s:form action="lanProfiles" name="userGroupSelectPage4Lan" id="userGroupSelectPage4Lan">
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
							<ah:checkList  name="radiusUserGroupIds" multiple="true" width="100%" list="lstLocalUserGroups" listKey="id" listValue="value" value="radiusUserGroupIds"/>
						</td>	
					</tr>
					<tr>
						<td height="15px"/>
					</tr>
					<tr>
						<td align="center" width="100%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btOkUserGroup" style="display:none;" onclick="finishSelectLocalUserGroup();" title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text name="config.networkpolicy.button.ok"/></span></a></td>
									<td width="40px">&nbsp;</td>
									<td class="npcButton" ><a href="javascript:void(0);" class="btCurrent" id="btNewUserGroup" style="display:none;" onclick="newLocalUserGroup();" title="<s:text name="config.networkpolicy.button.addnew"/>"><span><s:text name="config.networkpolicy.button.addnew"/></span></a></td>
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
