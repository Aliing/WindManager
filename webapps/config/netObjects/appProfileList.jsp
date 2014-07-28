<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.paintbrush.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script>
var formName = 'appProfileForm';

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="selectedL2Feature.description" /></td>');
}
</script>

<div id="content">
    <s:form action="appProfile" id="appProfileForm" name="appProfileForm" method="post">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="new" value="New" class="button"
						onClick="submitAction('init');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('cloneProfile');"
						<s:property value="writeDisabled" />></td>
					<!--
					<td><input type="button" name="ignore" value="<s:text name="button.paintbrush"/>" 
						id="brushTriggerBtn"
						class="button" onclick="hm.paintbrush.triggerPaintbrush('osObject')"
						<s:property value="writeDisabled" />></td>
					-->
					<td><input type="button" name="remove" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					
					<th align="left" nowrap>
						<ah:sort name="profileName" key="config.appProfile.name" />
					</th>
					<th align="left" nowrap>
						<s:text name="config.appProfile.applist"/>
					</th>
						
					
					<th><ah:sort name="owner.domainName" key="config.domain" /></th>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck" <s:if test="%{defaultFlag == true}">defaultFlag="true"</s:if>><ah:checkItem /></td>
   						
						<td class="list"><a
							href='<s:url value="appProfile.action"><s:param name="operation" value="%{'init'}"/><s:param name="profile.id" value="%{id}"/></s:url>'><s:property
							value="profileName" /></a></td>
				
					<td class="list">
					     <select style="width: 200px;">
					     <option>--selected application list--</option>
					     <s:iterator value="applicationList">
					        <option><s:property value="appName"/></option>
					     </s:iterator>
					      </select>
					</td>
							
					<td class="list"><s:property value="%{owner.domainName}" /></td>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>

<script language="javascript">
YAHOO.util.Event.onDOMReady(function() {

});

function removeCheck() {
	var inputElements = document.getElementsByName("selectedIds");
	for(var i = 0; i < inputElements.length; i++) {
		if(inputElements[i].type == "checkbox" &&  inputElements[i].checked == true) {
			if (inputElements[i].parentNode.getAttribute("defaultFlag") != null) {
				return false;
			}
		}
	}
	return true;
}

var thisOperation;
function submitAction(operation) {
	thisOperation = operation;
	if (operation == 'remove') {
		if (!removeCheck()) {
			showInfoDialog("Default applicaton profile can not be removed.");
			return false;
		}
        hm.util.checkAndConfirmDelete();
    } else if (operation == 'cloneProfile') {
    	hm.util.checkAndConfirmClone();
    } else {
    	doContinueOper();
    }	
}
function doContinueOper() {
	if(thisOperation != "export"){
		showProcessing();
	}
	document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}


</script>
