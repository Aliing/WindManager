<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'printTemplate';

function onLoadPage() {
	if (document.getElementById(formName + "_dataSource_name").disabled == false) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
	
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'preview') {
			document.forms[formName].target = '_blank';
		} else {
			document.forms[formName].target = '_self';
		}

		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(operation == 'cancel') {
		return true;
	}
	
	if (!checkName()) {
		return false;
	}
	
	if (!checkFields()) {
		return false;
	}
	
	if(!checkHtml()) {
		return false;
	}
	return true;
}

function checkName() {
	var name = document.getElementById(formName + "_dataSource_name");
	var message = hm.util.validateName(name.value, '<s:text name="gml.template.name" />');
	
   	if (message != null) {
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}	 
	
	return true;
}

function changeTemplateType(value) {

}

function checkFields() {
	var fields = document.getElementsByName("requireds");
	var required = 0;
	
	for(var i=0; i<fields.length; i++) {
		if(fields[i].checked) {
			required++;
			break;
		}
	}
	
	if(required > 0) {
		return true;
	} else {
		hm.util.reportFieldError(document.getElementById("fields"), "At least one field should be required.");
		return false;
	}
}

function checkHtml(){
	var headerHTML = document.getElementById("headerHTML");
	if(headerHTML.value.trim().length == 0) return true;
	if(headerHTML.value.length > 2048) {
		hm.util.reportFieldError(headerHTML, '<s:text name="error.keyLengthRange"><s:param><s:text name="gml.template.header.html" /></s:param><s:param><s:text name="gml.template.footer.html.range" /></s:param></s:text>');
		headerHTML.focus();
	    return false;
	}
	
	var footerHTML = document.getElementById("footerHTML");
	if(footerHTML.value.trim().length == 0) return true;
	if(footerHTML.value.length > 2048) {
		hm.util.reportFieldError(footerHTML, '<s:text name="error.keyLengthRange"><s:param><s:text name="gml.template.footer.html" /></s:param><s:param><s:text name="gml.template.footer.html.range" /></s:param></s:text>');
		footerHTML.focus();
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
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="printTemplate" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id == null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		document.writeln('Edit \'<s:property value="changedName" />\'</td>');
	</s:else>
	</s:else>
}  
</script>

<div id="content"><s:form action="printTemplate">
<s:hidden name="templateId" /> 
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="create" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update');"
							<s:property value="updateDisabled" />></td>
					</s:else>
					<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
					</td>
					<td><input type="button" name="preview" value="Preview"
							class="button"
							onClick="submitAction('preview');">
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="650px">
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td class="labelT1" width="80px"><label><s:text
									name="gml.template.name" /><font color="red"><s:text name="*"/></font></label></td>
								<td><s:textfield size="48"
									name="dataSource.name" maxlength="%{nameLength}"
									disabled="%{disabledName}" onkeypress="return hm.util.keyPressPermit(event,'pskUserName');" />&nbsp;<s:text
									name="gml.template.name.range" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td align="left">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td colspan="2" class="labelT1">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td width="20px">
												<s:checkbox id="checkEnabled"
													name="dataSource.enabled"
													value="%{dataSource.enabled}" /></td>
											<td align="left">
												<s:text name="gml.template.enable" /></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding: 8px 10px 5px 10px;">
					<fieldset>
						<legend>
							<s:text name="gml.template.fields" />
						</legend>
						<div class="yui-content">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr id="fields">
									<th align="center">
										<s:text name="gml.template.field.required" />
									</th>
									<th align="center">
										<s:text name="gml.template.field.label" />
									</th>
									<th align="center">
										<s:text name="gml.template.field.order" />
									</th>
								</tr>	
								<s:iterator id="fields" value="%{dataSource.templateFields}" status="status">
									<tr class="list">
										<td class="listCheck" align="center">
											<s:checkbox name="requireds" id="required_%{#status.index}" 
												fieldValue="%{#status.index}" value="%{required}"  />
										</td>
										<td class="list" align="center">
											<s:textfield name="labels" id="label_%(#status.index)" value="%{label}" cssStyle="display: none;"/>
											<s:property value="%{label}"/>
										</td>
										<td class="list" align="center">
											<s:select name="orders" value="%{place}" id="order_%{#status.index}" 
												list="%{fieldOrders}" listKey="key" listValue="value" />
										</td>
									</tr>
								</s:iterator>
							</table>
						</div>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td style="padding: 8px 10px 5px 10px;">
					<fieldset>
						<legend>
							<s:text name="gml.template.header.html" />
						</legend>
						<div class="yui-content">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<s:textarea id="headerHTML" name="dataSource.headerHTML" value="%{dataSource.headerHTML}"
												cols="88" rows="5"/> 
								</td>
								<td><s:text name="gml.template.footer.html.range"></s:text></td>
							</tr>
						</table>
						</div>
					</fieldset>
					</td>
				</tr>		
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td style="padding: 8px 10px 5px 10px;">
					<fieldset>
						<legend>
							<s:text name="gml.template.footer.html" />
						</legend>
						<div class="yui-content">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td height="4"></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<s:textarea id="footerHTML" name="dataSource.footerHTML" value="%{dataSource.footerHTML}"
												cols="88" rows="5"/> 
								</td>
								<td><s:text name="gml.template.footer.html.range"></s:text></td>
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
	</table>
</s:form></div>