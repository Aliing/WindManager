<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'macAuth';

function submitAction(operation) {
	thisOperation = operation;
    if (operation == 'removeFromGroup') {
   	    if (!checkUserGroup()) {
   	        return;
   	    }
        hm.util.checkAndConfirmDelete();
        if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
            warnDialog.cfg.setProperty('text', "There is no item to add.");
            warnDialog.show();
        } else if (!hm.util.hasCheckedBoxes(inputElements)) {
            warnDialog.cfg.setProperty('text', "Please select at least one item.");
            warnDialog.show();
        } else {
            showProcessing();
            doContinueOper();
        }
     } else if (operation == 'addToGroup') {
        if (!checkUserGroup()) {
            return;
        }
        var inputElements = document.getElementsByName('selectedIds');
        if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
            warnDialog.cfg.setProperty('text', "There is no item to add.");
            warnDialog.show();
        } else if (!hm.util.hasCheckedBoxes(inputElements)) {
            warnDialog.cfg.setProperty('text', "Please select at least one item.");
            warnDialog.show();
        } else {
            showProcessing();
            doContinueOper();
        }
    } else if(operation == 'macAuth'){
        var redirect_url = "<s:url action='macAuth' includeParams='none' />" + "?operation=macAuthList";
        window.location.href = redirect_url;
    } else if(operation == "editGroup"){
        var value = hm.util.validateListSelection(formName + "_userGroupId");
        if(value < 0){
            return;
        }else{
            document.forms[formName].userGroupId.value = value;
            doContinueOper();
        }
    } else if(operation == "refresh"){
        var value = document.getElementsByName(formName + "_userGroupId");
        document.forms[formName].userGroupId.value = value;
        doContinueOper();
    } else {
        doContinueOper();
    }   
}

function toggleSelectAll(checkAll) {
    hm.util.toggleHideElement('pageItemsSelectedRow', !checkAll.checked);
    if (!checkAll.checked) {
        hm.util.toggleHideElement('allItemsSelectedRow', true);
        hm.util.toggleAllItemsSelected(false);
    }else{
        hm.util.toggleHideElement('allItemsSelectedRow', false);
        hm.util.toggleAllItemsSelected(true);	
    }
    var inputElements = document.getElementsByName('selectedIds');
    if (inputElements) {
        for (var i = 0; i < inputElements.length; i++) {
            var cb = inputElements[i];
            if (!cb.disabled && cb.checked != checkAll.checked) {
                cb.checked = checkAll.checked;
                hm.util.toggleRow(cb);
            }
        }
    }
}

function doContinueOper() {
    document.forms[formName].operation.value = thisOperation;
    document.forms[formName].submit();
}

function checkUserGroup() {
    var userGroupId = document.getElementById(formName + "_userGroupId");
    if (userGroupId.value == null || userGroupId.value < 0) {
    	hm.util.reportFieldError(userGroupId, '<s:text name="error.requiredField"><s:param><s:text name="config.localUser.userGroup" /></s:param></s:text>');
        userGroupId.focus();
        return false;
    }
    return true;
}

function changeUserGroup(){
   var userGroupId = document.getElementById(formName + "_userGroupId").value;
   submitAction("changeUserGroup");
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
    document.writeln('<td class="crumb" nowrap><a href="<s:url action="macAuth" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
    document.writeln('MAC Auth</td>');
    </s:else>
}
</script>
<div id="content"><s:form action="macAuth">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><tiles:insertDefinition name="context" /></td>
        </tr>
        <tr>
            <td class="buttons">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><input style="width:100px;" type="button" name="add" value="<s:text name="button.addtogroup"/>"
                        class="button"
                        onClick="submitAction('addToGroup<s:property value="lstForward"/>');"
                        <s:property value="writeDisabled" />>
                    </td>
                    <td><input style="width:140px;" type="button" name="remove" value="<s:text name="button.removefromgroup"/>"
                        class="button" onClick="submitAction('removeFromGroup<s:property value="lstForward"/>');"
                        <s:property value="writeDisabled" />></td>
                    <s:if test="%{lstForward == null || lstForward == ''}">
                    <td><input type="button" name="cancel" value="Cancel"
                            class="button"
                            onClick="submitAction('<%=Navigation.L2_FEATURE_LOCAL_USER%>');">
                        </td>
                    </s:if>
                    <s:else>
                        <td><input type="button" name="cancel" value="Cancel"
                            class="button"
                            onClick="submitAction('cancel<s:property value="lstForward"/>');">
                    </td>
                    </s:else>
                    <td><input type="button" name="refresh" value="<s:text name="common.button.refresh"/>"
                        class="button"
                        onClick="submitAction('refresh');"
                        <s:property value="writeDisabled" />>
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
            <table class="editBox" cellspacing="0" cellpadding="0" border="0" width="720px">
                <tr>
                    <td>
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td class="labelT1" width="100px">
                                <label id="userGroupTitle"><s:property value="userGroupTitle" /></label><font color="red"><s:text name="*"/></font></td>
                                <td width="200px"><s:select name="userGroupId" cssStyle="width: 200px;"
                                    list="%{localUserGroup}" listKey="id" listValue="value"
                                    onchange="changeUserGroup();"/>
                                </td>
                                <td>
                                    <s:if test="%{showNewGroupButton == 'disabled'}">
                                        <img class="dinl marginBtn"
                                        src="<s:url value="/images/new_disable.png" />"
                                        width="16" height="16" alt="New" title="New" />
                                    </s:if>
                                    <s:else>
                                        <a class="marginBtn" href="javascript:submitAction('newGroup')"><img class="dinl"
                                        src="<s:url value="/images/new.png" />"
                                        width="16" height="16" alt="New" title="New" /></a>
                                    </s:else>
                                </td>
                                <td>
                                    <s:if test="%{showNewGroupButton == 'disabled'}">
                                        <img class="dinl marginBtn"
                                        src="<s:url value="/images/modify_disable.png" />"
                                        width="16" height="16" alt="Modify" title="Modify" />
                                    </s:if>
                                    <s:else>
                                        <a class="marginBtn" href="javascript:submitAction('editGroup')"><img class="dinl"
                                        src="<s:url value="/images/modify.png" />"
                                        width="16" height="16" alt="Modify" title="Modify" /></a>
                                    </s:else>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td height="5"></td>
                </tr>
                <tr>
                    <td class="labelT1" style="padding-left:10px;display: <s:property value="%{totalBindStuStyle}"/>"><s:text name="config.title.macauth.totalbindstu"/><s:property value="totalBindStu"/></td>
                </tr>                
		        <tr>
		            <td style="padding-left:10px">
		            <table cellspacing="0" cellpadding="0" border="0" class="view">
		                <tr>
		                    <th class="check"><input type="checkbox" id="checkAll"
		                        onClick="toggleSelectAll(this);"></th>
		                    <s:iterator value="%{selectedColumns}">
		                        <s:if test="%{columnId == 1}">
		                            <th align="left" nowrap><ah:sort name="studentID"
		                                key="config.tv.studentId" /></th>
		                        </s:if>
		                        <s:elseif test="%{columnId == 2}">
		                            <th align="left" nowrap><ah:sort name="studentName"
		                                key="config.tv.studentName" /></th>
		                        </s:elseif>
		                        <s:elseif test="%{columnId == 3}">
                                    <th align="left" nowrap><s:text name="config.macOrOui.macAddress" /></th>
		                        </s:elseif>
		                        <s:elseif test="%{columnId == 4}">
		                            <th align="left" nowrap><ah:sort name="schoolId"
                                        key="config.tv.schoolId" /></th>
		                        </s:elseif>
		                    </s:iterator>
		                    <s:if test="%{showDomain}">
		                        <th><ah:sort name="owner.domainName" key="config.domain" /></th>
		                    </s:if>
		                </tr>
		                <s:if test="%{page.size() == 0}">
		                    <ah:emptyList />
		                </s:if>
		                <tiles:insertDefinition name="selectAll" />
		                <s:iterator value="page" status="status">
		                    <tiles:insertDefinition name="rowClass" />
		                    <tr class="<s:property value="%{#rowClass}"/>">
		                        <s:if test="%{showDomain && owner.domainName!='home' && owner.domainName!='global'}">
		                            <td class="listCheck"><input type="checkbox" disabled /></td>
		                        </s:if>
		                        <s:else>
		                            <td class="listCheck"><ah:checkItem /></td>
		                        </s:else>
		                        <s:iterator value="%{selectedColumns}">
		                            <s:if test="%{columnId == 1}">
		                                <td class="list" id="studentId"><s:property value="studentId" />&nbsp;</td>
		                            </s:if>
		                            <s:elseif test="%{columnId == 2}">
		                                <td class="list" id="studentName"><s:property value="studentName" />&nbsp;</td>
		                            </s:elseif>
		                            <s:elseif test="%{columnId == 3}">
		                                <td class="list" id="macAddress"><s:property value="macAddress" />&nbsp;</td>
		                            </s:elseif>
		                            <s:elseif test="%{columnId == 4}">
		                                <td class="list" id="schoolId"><s:property value="schoolId" />&nbsp;</td>
		                            </s:elseif>
		                        </s:iterator>
		                        <s:if test="%{showDomain}">
		                            <td class="list"><s:property value="%{owner.domainName}" /></td>
		                        </s:if>
		                    </tr>
		                </s:iterator>
		            </table>
		            </td>
		        </tr>
            </table>
            </td>
        </tr>
    </table>
</s:form></div>
