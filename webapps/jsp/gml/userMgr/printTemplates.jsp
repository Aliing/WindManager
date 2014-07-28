<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'printTemplate';
var thisOperation;
var lastDefault;

function submitAction(operation) {
    thisOperation = operation;

    if (operation == 'remove') {
    	var checkbox = document.getElementsByName("selectedIds");
    	for(var i=0; i<checkbox.length; i++) {
    		if(checkbox[i].checked) {
    			var defaultValue = document.getElementById("asDefault_"+checkbox[i].value+"default");
    			if (defaultValue.checked) {
    				if(warnDialog != null)
    				{
    					warnDialog.cfg.setProperty('text',"The default print template should not be deleted.");
    					warnDialog.show();
    				}
    				return;
    			}
    		}
    	}

        hm.util.checkAndConfirmDelete();
    } else if (operation == 'clone') {
        hm.util.checkAndConfirmClone();
    } else if (operation == 'preview') {
        hm.util.checkAndConfirmOne('preview');
    } else {
        doContinueOper();
    }

}

function doContinueOper() {
    if (thisOperation == 'preview') {
		document.forms[formName].target = '_blank';
	} else {
		showProcessing();
    	document.forms[formName].target = '_self';
	}

    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function selectDefault(boId) {
	var url = '<s:url action="printTemplate" includeParams="none"></s:url>'
 		+ "?operation=default&defaultId=" + boId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function getLastDefault() {
	var defaults = document.getElementsByName("asDefault");

	for(var i=0; i<defaults.length; i++) {
		if(defaults[i].checked) {
			lastDefault = i;
			break;
		}
	}
}

function setLastDefault() {
	var defaults = document.getElementsByName("asDefault");

	for(var i=0; i<defaults.length; i++) {
		if(i == lastDefault) {
			defaults[i].checked = true;
		} else {
			defaults[i].checked = false;
		}
	}
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);

	if(details.failed) {
		setLastDefault();

		if(warnDialog != null)
		{
			warnDialog.cfg.setProperty('text', details.msg);
			warnDialog.show();
		}
	}
};

var detailsFailed = function(o) {
	//alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}
</script>

<div id="content"><s:form action="printTemplate">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="New"
						class="button" onClick="submitAction('new');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Clone"
						class="button" onClick="submitAction('clone');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Remove"
						class="button" onClick="submitAction('remove');"
						<s:property value="writeDisabled" />></td>
					<td><input type="button" name="ignore" value="Preview"
						class="button" onClick="submitAction('preview');"
						<s:property value="writeDisabled" />></td>
					</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table id ="hiveTable" cellspacing="0" cellpadding="0" border="0" class="view">
				<tr>
					<th class="check"><input type="checkbox" id="checkAll"
						onClick="hm.util.toggleCheckAll(this);"></th>
					<s:iterator value="%{selectedColumns}">
					<s:if test="%{columnId == 1}">
						<th><ah:sort name="name" key="gml.template.name" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
						<th><ah:sort name="asDefault" key="gml.template.default" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
						<th><ah:sort name="enabled" key="gml.template.status" /></th>
					</s:if>
					</s:iterator>
				</tr>
				<s:if test="%{page.size() == 0}">
					<ah:emptyList />
				</s:if>
				<tiles:insertDefinition name="selectAll" />
				<s:iterator value="page" status="status" id="pageRow">
					<tiles:insertDefinition name="rowClass" />
					<tr class="<s:property value="%{#rowClass}"/>">
						<td class="listCheck"><ah:checkItem /></td>
						<s:iterator value="%{selectedColumns}">
							<s:if test="%{columnId == 1}">
		   						<s:if test="%{showDomain}">
									<td class="list"><a
										href='<s:url value="printTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/><s:param name="domainId" value="%{#pageRow.owner.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:if>
								<s:else>
									<td class="list"><a
										href='<s:url value="printTemplate.action"><s:param name="operation" value="%{'edit'}"/><s:param name="id" value="%{#pageRow.id}"/></s:url>'><s:property
										value="name" /></a></td>
								</s:else>
							</s:if>
							<s:elseif test="%{columnId == 2}">
								<td class="list">
									<s:radio label="Gender" name="asDefault"
										list="#{'default':''}" id="asDefault_%{#pageRow.id}"
										onmousedown="getLastDefault();" onclick="selectDefault(%{#pageRow.id});" value="%{asDefaultString}"/>&nbsp;
								</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list"><s:property value="statusString" />&nbsp;</td>
							</s:elseif>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>