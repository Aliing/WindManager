<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script>
var selectUIElement;
function clickServiceFilter(operation){
	selectUIElement = Get(formName + "_serviceFilterId");
	<s:if test="%{jsonMode}">
	    var url = '<s:url action="portAccess" includeParams="none"/>' 
	        + '?operation='+operation
	        + '&jsonMode=true'
	        + '&ignore=' + new Date().getTime();
	        
	    if (operation == 'editServiceFilter'){
	        url = url + "&serviceFilterId=" + $("#portAccess_serviceFilterId").val();
	    }
	    openIFrameDialog(880, 450, url);
    </s:if>
    <s:else>
    	submitAction(operation);
    </s:else>
}
</script>

<fieldset>
    <legend><s:text name="config.ssid.allOption.legend" /></legend>
    <table>
        <tr>
            <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td height="4px"/>
                </tr>
                <tr>
                    <td style="padding-left: 10px">
                        <table cellspacing="0" cellpadding="0" border="0" width="100%">
                            <tr>
                                <td class="labelT1" width="220px"><s:text
                                    name="config.lanProfile.mgtServiceFilter" /></td>
                                <td width="200px" style="padding:0 5px 0 10px;"><s:select
                                    name="serviceFilterId" list="%{serviceFilterList}" listKey="id"
                                    listValue="value" cssStyle="width: 200px;" /></td>
                                <td width="20px">
                                    <s:if test="%{writeDisabled == 'disabled'}">
                                        <img class="dinl marginBtn"
                                        src="<s:url value="/images/new_disable.png" />"
                                        width="16" height="16" alt="New" title="New" />
                                    </s:if>
                                    <s:else>
                                        <a class="marginBtn" href="javascript:clickServiceFilter('newServiceFilter')"><img class="dinl"
                                        src="<s:url value="/images/new.png" />"
                                        width="16" height="16" alt="New" title="New" /></a>
                                    </s:else>
                                </td>
                                <td>
                                    <s:if test="%{writeDisabled == 'disabled'}">
                                        <img class="dinl marginBtn"
                                        src="<s:url value="/images/modify_disable.png" />"
                                        width="16" height="16" alt="Modify" title="Modify" />
                                    </s:if>
                                    <s:else>
                                        <a class="marginBtn" href="javascript:clickServiceFilter('editServiceFilter')"><img class="dinl"
                                        src="<s:url value="/images/modify.png" />"
                                        width="16" height="16" alt="Modify" title="Modify" /></a>
                                    </s:else>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td height="4px"/>
                </tr>
                <tr id="authSequenceRow">
                    <td style="padding-left: 10px">
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td class="labelT1" width="220px"><s:text name="config.ssid.auth.sequence"></s:text></td>
                                <td width="200px" class="labelT1"><s:select name="dataSource.authSequence"
                                    list="%{enumAuthSequence}" listKey="key" listValue="value"
                                    value="dataSource.authSequence" cssStyle="width: 320px;" />
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
            </td>
        </tr>
    </table>
</fieldset>