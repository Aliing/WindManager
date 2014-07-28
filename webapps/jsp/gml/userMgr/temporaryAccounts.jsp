<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
// QUIRKS FLAG, FOR BOX MODEL
var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
// UNDERLAY/IFRAME SYNC REQUIRED
var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

var formName = 'temporaryAccount';
var thisOperation;

function submitAction(operation) {
    thisOperation = operation;
    
    if (operation == 'allocating') {
    	document.forms["contentAction"].operationClass.value = "createAccounts";
    	doContinueOper();
    } else if(operation == 'revoke') {
    	document.forms["contentAction"].operationClass.value = "revokeAccounts";
    	hm.util.checkAndConfirmRevoke("This operation will revoke the allocated account(s). Are you sure?");
    } else {
    	doContinueOper();
    }   
}

function doContinueOper() {
    showProcessing();
    document.forms["contentAction"].operation.value = thisOperation;
    document.forms["contentAction"].submit();
}

function printAccount() {
	// check item selection
	var inputElements = document.getElementsByName('selectedIds');
	
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to print");
		warnDialog.show();
		return;
	}
	
	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}
	
	var selectCount = 0;
	
	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			selectCount++;
		}
	}
	
	if (selectCount != 1) {
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}
	
	var selectedTemplateId = <s:property value="selectedTemplateId" />;
	var selectElement = document.getElementById("templateList");
	var selectOptions = selectElement.options;
	for(var i=0;i<selectOptions.length;i++){
		if(selectElement.options[i].value == selectedTemplateId){
			selectElement.selectedIndex = i;
		}
	}
	
	if (printPanel != null) {
	    if (IE_SYNC) {
	        // Keep the underlay and iframe size in sync.
	        printPanel.sizeUnderlay();
	        // Syncing the iframe can be expensive. Disable iframe if you
	        // don't need it.
	        printPanel.syncIframe();
	    }
	    
		printPanel.cfg.setProperty('visible', true);
	}

}

function emailAccount(){
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to " + operation);
		warnDialog.show();
		return;
	}
	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	
	var selectedTemplateId = <s:property value="selectedTemplateId" />;
	var selectElement = document.getElementById("emailTemplateList");
	var selectOptions = selectElement.options;
	for(var i=0;i<selectOptions.length;i++){
		if(selectElement.options[i].value == selectedTemplateId){
			selectElement.selectedIndex = i;
		}
	}
	
	if (emailPanel != null) {
	    if (IE_SYNC) {
	        // Keep the underlay and iframe size in sync.
	        emailPanel.sizeUnderlay();
	        // Syncing the iframe can be expensive. Disable iframe if you
	        // don't need it.
	        emailPanel.syncIframe();
	    }
	    
	    emailPanel.cfg.setProperty('visible', true);
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

</script>

<div id="content"><s:form name="contentAction" action="temporaryAccount">
<s:hidden name="operationClass" />
<s:hidden name="templateId"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Create"
						class="button" onClick="submitAction('allocating');"
						style="display: <s:property value="%{showCreate}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
					<td><input type="button" name="ignore" value="Print"
						id="printButton"
						class="button" onClick="printAccount();"
						style="display: <s:property value="%{showCreate}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
					<td><input type="button" name="ignore" value="Email"
						id="emailButton"
						class="button" onClick="emailAccount();"
						style="display: <s:property value="%{showCreate}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
					<td><input type="button" name="ignore" value="Revoke"
						class="button" onClick="submitAction('revoke');"
						style="display: <s:property value="%{showRevoke}"/>"
						<s:property value="%{writeDisabled}" /> /></td>
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
					<s:iterator value="%{selectedColumns}">
					<s:if test="%{columnId == 1}">
						<th><ah:sort name="visitorName" key="gml.temporary.visitor" /></th>
					</s:if>
					<s:if test="%{columnId == 2}">
						<th><s:text name="gml.temporary.psk" /></th>
					</s:if>
					<s:if test="%{columnId == 3}">
						<th><ah:sort name="userName" key="gml.clientmonitor.userName" /></th>
					</s:if>
					<s:if test="%{columnId == 4}">
						<s:if test="%{fullMode}">
							<th><ah:sort name="localUserGroup" key="gml.temporary.userGroup" /></th>
						</s:if>
						<s:elseif test="%{easyMode}">
							<th><ah:sort name="localUserGroup" key="gml.temporary.ssid" /></th>
						</s:elseif>
					</s:if>
					<s:if test="%{columnId == 5}">
						<th><s:text name="gml.temporary.startTime" /></th>
					</s:if>
					<s:if test="%{columnId == 6}">
						<th><s:text name="gml.temporary.endTime" /></th>
					</s:if>
					<s:if test="%{columnId == 7}">
						<th><ah:sort name="mailAddress" key="gml.temporary.email" /></th>
					</s:if>
					<s:if test="%{columnId == 8}">
						<th><ah:sort name="description" key="gml.temporary.comment" /></th>
					</s:if>
					<s:if test="%{columnId == 9}">
						<s:if test="%{fullMode}">
							<th><ah:sort name="ssidName" key="gml.temporary.ssid" /></th>
						</s:if>
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
						<td class="listCheck"><ah:checkItem /></td>
   						<s:iterator value="%{selectedColumns}">
   							<s:if test="%{columnId == 1}">
   								<td class="list"><s:property value="visitorName" />&nbsp;</td>
   							</s:if>
		   					<s:elseif test="%{columnId == 2}">
								<td class="list"><s:property value="strPsk" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 3}">
								<td class="list"><s:property value="userName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 4}">
								<td class="list"><s:property value="userGroupName" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 5}">
								<td class="list"><s:property value="startTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 6}">
								<td class="list"><s:property value="expiredTimeString" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 7}">
								<td class="list"><s:property value="mailAddress" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 8}">
								<td class="list"><s:property value="description" />&nbsp;</td>
							</s:elseif>
							<s:elseif test="%{columnId == 9}">
								<s:if test="%{fullMode}">
									<td class="list"><s:property value="ssidName" />&nbsp;</td>
								</s:if>
							</s:elseif>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="printPanel" style="display: none;">
<s:form name="printAction" action="temporaryAccount">
<%--div class="hd"></div> --%>
<div class="bd" style="background-color: #FFFFFF;">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td style="padding: 0 0 20px 0">
			<font size="2">Open print window with template...</font>
		</td>
	</tr>
	<tr>
		<td>
			<font size="3">
			<s:select id="templateList" name="printTemplates" list="%{printTemplateList}" 
				listKey="id" listValue="value" cssStyle="width: 280px; height: 30px;"
				headerKey="0"/>
			</font>
		</td>
	</tr>
	<tr>
		<td style="padding: 20px 0 0 0">
			<table>
				<tr>
					<td>
						<input type="button" name="ignore" value="OK"
						class="button" onClick="selectTemplate();"/>
					</td>
					<td>
						<input type="button" name="ignore" value="Cancel"
						class="button" onClick="cancelPrint();"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>		
</div>
</s:form></div>
<div id="emailPanel" style="display: none;">
<s:form name="emailAction" action="temporaryAccount">
<%--div class="hd"></div> --%>
<div class="bd" style="background-color: #FFFFFF;">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td style="padding: 0 0 20px 0">
			<font size="2">Open email window with template...</font>
		</td>
	</tr>
	<tr>
		<td>
			<font size="3">
			<s:select id="emailTemplateList" name="emailTemplates" list="%{printTemplateList}" 
				listKey="id" listValue="value" cssStyle="width: 280px; height: 30px;"
				headerKey="0"/>
			</font>
		</td>
	</tr>
	<tr>
		<td style="padding: 20px 0 0 0">
			<table>
				<tr>
					<td>
						<input type="button" name="ignore" value="OK"
						class="button" onClick="selectEmailTemplate();"/>
					</td>
					<td>
						<input type="button" name="ignore" value="Cancel"
						class="button" onClick="cancelEmail();"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>		
</div>
</s:form></div>
<script >
var printPanel = null;
var emailPanel = null;
function onLoadPage() {
	var div = document.getElementById('printPanel');
	printPanel = new YAHOO.widget.Panel(div, { width:"320px", visible:false, draggable:true, constraintoviewport:true } );
	var code = document.getElementById('printButton');
	printPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code) + 2);
	printPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 25);
	printPanel.setHeader('Print Account');
	printPanel.render();
	div.style.display = "";
	
	// for email 
	var emailDiv = document.getElementById('emailPanel');
	emailPanel = new YAHOO.widget.Panel(emailDiv, { width:"320px", visible:false, draggable:true, constraintoviewport:true } );
	var codeEmail = document.getElementById('emailButton');
	emailPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(codeEmail) + 2);
	emailPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(codeEmail) + 25);
	emailPanel.setHeader('Email Account');
	emailPanel.render();
	emailDiv.style.display = "";
}

function selectTemplate(){
	var value = document.getElementById("templateList").value;
	printPanel.cfg.setProperty('visible', false);
	var boIds = hm.util.getSelectedIds();
	var url = '<s:url action="temporaryAccount" includeParams="none"></s:url>' 
				+ "?operation=checkPrint&templateId=" + value
				+ "&userId=" + boIds[0];
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback, null);

}

function cancelPrint() {
	printPanel.cfg.setProperty('visible', false);
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	
	if(details.a){
		var url = "<s:url action='temporaryAccount' includeParams='none'/>" 
				+ "?operation=print&userId=" + details.u;
				+ "&templateId=" + details.t;
		
		window.open(url, 'printWindow', 'height=500, width=600, menubar=yes,toolbar=no,location=no,directories=no,scrollbars=yes, resizable, top=250,left=400');
	} else {
		warnDialog.cfg.setProperty('text', details.e);
		warnDialog.show();
	}
	
};

var detailsFailed = function(o) {
	//alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

// for email
function selectEmailTemplate(){
	 thisOperation = 'email';
	var value = document.getElementById("emailTemplateList").value;
	emailPanel.cfg.setProperty('visible', false);

	document.forms["contentAction"].templateId.value = value;
	document.forms["contentAction"].operationClass.value = "createAccounts";
	doContinueOper();
}

function cancelEmail() {
	emailPanel.cfg.setProperty('visible', false);
}

</script>
