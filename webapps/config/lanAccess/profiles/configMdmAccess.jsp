<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
$(document).ready(function() { 
	
 });  
function enableMDMcheck(checked) {
	if (checked) {
		Get("enablemdmselect").style.display="block";
	} else {
		Get("enablemdmselect").style.display="none";
		//Get(formName + "_routingPolicyId").value=-1;
	}
}

var selectUIElement;
function clickMdmPolicy(operation){
	<s:if test="%{jsonMode}">
		var url = '<s:url action="portAccess" includeParams="none"/>' 
			+ '?operation='+operation
			+ '&jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		
		if (operation == 'editConfigmdmPolicy'){
			url = url + "&configmdmId=" + $("#portAccess_configmdmId").val();
		}
		selectUIElement= Get(formName + "_configmdmId");
		
		//openIFrameDialog(780, 710, url);
		openIFrameDialog(780, 500, url);
	</s:if>
	<s:else>
		submitAction(operation);
	</s:else>
	
}

</script>


		<div>
		<fieldset><legend><s:text name="config.ssid.advanced.mdm.enrollment.title" /></legend>
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td class="labelT1" width="100px"  colspan="5">
									<s:checkbox name="dataSource.enableMDM" onclick="enableMDMcheck(this.checked);"  />
									<s:text name="config.ssid.advanced.mdm.enrollment.enable"/>
									</td>
									<td id="enablemdmselect" style="padding-top:6px;">		
											<s:select name="configmdmId" list="%{configmdmList}" listKey="id" listValue="value" cssStyle="width: 140px;" />
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/new_disable.png" />"
													width="16" height="16" alt="New" title="New" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:clickMdmPolicy('newConfigmdmPolicy')"><img class="dinl"
													src="<s:url value="/images/new.png" />"
													width="16" height="16" alt="New" title="New" /></a>
												</s:else>
												<s:if test="%{writeDisabled == 'disabled'}">
													<img class="dinl marginBtn"
													src="<s:url value="/images/modify_disable.png" />"
													width="16" height="16" alt="Modify" title="Modify" />
												</s:if>
												<s:else>
													<a class="marginBtn" href="javascript:clickMdmPolicy('editConfigmdmPolicy')"><img class="dinl"
													src="<s:url value="/images/modify.png" />"
													width="16" height="16" alt="Modify" title="Modify" /></a>
												</s:else>
											
						
						</td>
					</tr>
			 	</table>
			</fieldset>
			</div>
	

	