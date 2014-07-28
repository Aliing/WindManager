<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<link rel="stylesheet" href="<s:url value="/css/hm_v2.css" includeParams="none"/>" type="text/css" />
<s:if test="%{jsonMode}">
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none"/>" ></script>
</s:if>
<script>
var formName = 'serviceFilter';
var thisOpreation;
function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_filterName").disabled == false) {
		document.getElementById(formName + "_dataSource_filterName").focus();
	}
}

function submitAction(operation) {
	if (validate(operation)) {
		
		<s:if test="%{jsonMode}">
			if (operation != 'create') {
				showProcessing();
			}
		    if ('cancel' + '<s:property value="lstForward"/>' == operation) {
				parent.closeIFrameDialog();	
			} else{
				saveTrafficFilter(operation);
			}
		</s:if>
		<s:else>
			if (operation != 'create') {
				showProcessing();
			}
		    document.forms[formName].operation.value = operation;
		    document.forms[formName].submit();
		</s:else>
		
	}
}

function saveTrafficFilter(operation) {
	var url;
	thisOpreation = operation;
	 if (operation == 'create') {
		 url = "<s:url action='serviceFilter' includeParams='none' />"+ "?jsonMode=true" +"&ignore="+new Date().getTime(); 
	 } else if (operation == 'update'){
		 var id;
		 <s:if test="%{dataSource.id != null}">
		     id = <s:property value="dataSource.id"/>; 
		 </s:if>
		 url = "<s:url action='serviceFilter' includeParams='none' />"+ "?jsonMode=true"+ "&id="+id +"&ignore="+new Date().getTime(); 
	 }
	 document.forms["serviceFilter"].operation.value = operation;
	 YAHOO.util.Connect.setForm(document.forms["serviceFilter"]);
	 var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveTrafficFilter, failure : failSaveTrafficFilter, timeout: 60000}, null);
	
}

var succSaveTrafficFilter = function (o) {
	try {
		eval("var details = " + o.responseText);
		if (details.ok) {
			parent.closeIFrameDialog();
			if (thisOpreation == 'create') {
				if(details.id != null && details.name != null){
					var selectUIElement = parent.selectUIElement;
					if(selectUIElement) {
						if (Object.prototype.toString.call(selectUIElement) == '[object Array]') {
							for (var i=0;i<selectUIElement.length;i++) {
								if( i == 0){
									hm.simpleObject.addOption(selectUIElement[i], details.id, details.name, true);
								} else {
									hm.simpleObject.addOption(selectUIElement[i], details.id, details.name, false);
								}
							}
						} else {
							hm.simpleObject.addOption(selectUIElement, details.id, details.name, true);
						}
					}
				}
			}
		} else {
			hm.util.displayJsonErrorNote(details.msg);
			return;
		}
	}catch(e){
		alert("error")
		
		return;
	}
}

var failSaveTrafficFilter = function(o){
	
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
		return true;
	}

	if (!validateFilterName()) {
		return false;
	}

	return true;
}

function validateFilterName() {
    var inputElement = document.getElementById(formName + "_dataSource_filterName");
	var message = hm.util.validateName(inputElement.value, '<s:text name="config.serviceFilter.name" />');
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="serviceFilter" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			<s:if test="%{dataSource.defaultFlag}">
				document.writeln('Default Value \'<s:property value="changedName" />\'</td>');
			</s:if>
			<s:else>
			    document.writeln('Edit \'<s:property value="changedName" />\'</td>');
			</s:else>
		</s:else>
	</s:else>
}
</script>
<div id="content">
	<s:form action="serviceFilter" name="serviceFilter" id="serviceFilter">

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{jsonMode == true}">
				<s:hidden name="operation" />
				<s:hidden name="jsonMode" />
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0"  width="100%">
						<tr>
							<td align="left">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="<s:url value="/images/hm_v2/profile/HM-icon-traffic_filters.png" includeParams="none"/>"
										width="40" height="40" alt="" class="dblk" />
									</td>
									<td class="dialogPanelTitle">
										<s:if test="%{dataSource.id == null}">
											<s:text name="config.title.trafficFilters"/>
										</s:if> <s:else>
											<s:text name="config.title.trafficFilters.edit"/>
										</s:else>
									</td>
									<td style="padding-left: 10px">
										<a href="javascript:void(0);"  onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
											<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
												alt="" class="dblk" />
										</a>
									</td>
								</tr>
							</table>
							</td>
							<td align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitAction('cancel' + '<s:property value="lstForward"/>');" title="Cancel"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;">Cancel</span></a></td>
									<td width="20px">&nbsp;</td>
									<s:if test="%{dataSource.id == null}">
										<td class="npcButton">
										<s:if test="'' == writeDisabled">
											<a href="javascript:void(0);" class="btCurrent"  onclick="submitAction('create');" title="<s:text name="button.create"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.create"/></span></a>
										</s:if>
										</td>
									</s:if>
									<s:else>
										<td class="npcButton">
										<s:if test="%{'' == updateDisabled}">
											<a href="javascript:void(0);" class="btCurrent" onclick="submitAction('update');" title="<s:text name="button.update"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="button.update"/></span></a>
										</s:if>
									</s:else>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</s:if>
			<s:else>
				<tr>
					<td><tiles:insertDefinition name="context" />
					</td>
				</tr>
				<tr>
					<td class="buttons">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<s:if test="%{dataSource.id == null}">
									<td><input type="button" name="ignore"
										value="<s:text name="button.create"/>" class="button"
										onClick="submitAction('create<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />>
									</td>
								</s:if>
								<s:else>
									<td><input type="button" name="ignore"
										value="<s:text name="button.update"/>" class="button"
										onClick="submitAction('update<s:property value="lstForward"/>');"
										<s:property value="updateDisabled" />>
									</td>
								</s:else>
								<td><input type="button" name="ignore" value="Cancel"
									class="button"
									onClick="submitAction('cancel<s:property value="lstForward"/>');">
								</td>
							</tr>
						</table></td>
				</tr>
			</s:else>
				<tr>
					<td><tiles:insertDefinition name="notes" /></td>
				</tr>
			<tr>
				<td style="padding-top: 5px;">
					<s:if test="%{jsonMode == true}">
						<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="100%">
					</s:if>
					<s:else>
						<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="620">
					</s:else>	
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td class="labelT1" width="110"><s:text
												name="config.serviceFilter.name" /><font color="red"><s:text
													name="*" />
										</font>
										</td>
										<td><s:textfield name="dataSource.filterName" size="48"
												onkeypress="return hm.util.keyPressPermit(event,'name');"
												maxlength="%{nameLength}" disabled="%{disabledName}" />&nbsp;<s:text
												name="config.serviceFilter.name.range" />
										</td>
									</tr>
									<tr>
										<td class="labelT1"><s:text
												name="config.serviceFilter.description" />
										</td>
										<td><s:textfield name="dataSource.description" size="48"
												maxlength="%{descriptionLength}" />&nbsp;<s:text
												name="config.serviceFilter.description.range" />
										</td>
									</tr>
								</table></td>
						</tr>
						<tr>
							<td style="padding: 4px 4px 4px 4px">
								<table border="0" cellspacing="0" cellpadding="0" width="100%">
									<tr>
										<td class="sepLine" colspan="3"><img
											src="<s:url value="/images/spacer.gif"/>" height="1"
											class="dblk" />
										</td>
									</tr>
								</table></td>
						</tr>
						<tr>
							<td style="padding: 0 0 0 10px">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><s:text name="config.serviceFilter.tabTitle" /></td>
									</tr>
								</table></td>
						</tr>
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="4"></td>
									</tr>
									<tr>
										<td style="padding: 0 2px 0 6px"><s:checkbox
												name="dataSource.enableSSH" value="%{dataSource.enableSSH}" />
										</td>
										<td width="360px"><s:text
												name="config.serviceFilter.enableSSH" />
										</td>
									</tr>
									<tr>
										<td style="padding: 0 2px 0 6px"><s:checkbox
												name="dataSource.enableTelnet"
												value="%{dataSource.enableTelnet}" />
										</td>
										<td width="360px"><s:text
												name="config.serviceFilter.enableTelnet" />
										</td>
									</tr>
									<tr>
										<td style="padding: 0 2px 0 6px"><s:checkbox
												name="dataSource.enablePing"
												value="%{dataSource.enablePing}" />
										</td>
										<td width="360px"><s:text
												name="config.serviceFilter.enablePing" />
										</td>
									</tr>
									<tr>
										<td style="padding: 0 2px 0 6px"><s:checkbox
												name="dataSource.enableSNMP"
												value="%{dataSource.enableSNMP}" />
										</td>
										<td width="360px"><s:text
												name="config.serviceFilter.enableSNMP" />
										</td>
									</tr>
									<tr>
										<td style="padding: 0 2px 0 6px"><s:checkbox
												name="dataSource.interTraffic"
												value="%{dataSource.interTraffic}" />
										</td>
										<td width="360px"><s:text
												name="config.serviceFilter.inter.station.traffic" />
										</td>
									</tr>
								</table></td>
						</tr>

						<tr>
							<td height="8"></td>
						</tr>
					</table></td>
			</tr>

		</table>
	</s:form>
</div>
