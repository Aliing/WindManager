<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script>
var waitingPanel = null;
function createWaitingPanel() {
    // Initialize the temporary Panel to display while waiting for external content to load
    waitingPanel = new YAHOO.widget.Panel('wait',
            { width:"260px",
              fixedcenter:true,
              close:false,
              draggable:false,
              zindex:4,
              modal:true,
              visible:false
            }
        );
    waitingPanel.setHeader("The operation is progressing...");
    waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
    waitingPanel.render(document.body);
}

function insertPageContext() {
    document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />'+'&nbsp;Customer ID');
    document.writeln('</td>');
}
</script>
<div id="content">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <tiles:insertDefinition name="context" />
                </td>
            </tr>
            <tr>
                <td class="buttons">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <input type="button" name="ignore" value="Retrieve"
                                    class="button" onClick="getCustomerId();"
                                    style="width: 90px;"
                                    <s:property value="writeDisabled" />>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td style="padding-left: 5px;">
                    <div id="noteSection" style="display:none">
                        <table width="400px" border="0" cellspacing="0" cellpadding="0"
                            class="note">
	                        <tr>
	                            <td class="noteInfo" id="infoRow"><s:property escape="false" /></td>
	                        </tr>
	                        <tr>
	                            <td class="noteError" id="errorRow"><s:property escape="false" /></td>
	                        </tr>	                                                 
                            <tr>
                                <td height="5"></td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <table class="editBox" cellspacing="0" cellpadding="0" border="0"
                        width="550px">
                        <tr>
                            <td height="10"></td>
                        </tr>
		                <tr>
		                    <td class="labelT1" width="140px"><label> <s:text
		                        name="admin.user.emailAddress" /><font color="red"><s:text
		                        name="*" /> </font> </label></td>
		                    <td><s:textfield id="email" name="userName"
		                        size="48" maxlength="128" />
		                    <s:text name="admin.email.address.range" /></td>
		                </tr>
                        <tr>
                            <td class="labelT1">
                                <label> <s:text name="admin.user.password" /><font color="red"><s:text name="*" /> </font> </label>
                            </td>
                            <td><s:password id="userPassword" name="userPassword"
                                size="24" maxlength="32" />
                                <s:textfield id="userPassword_text" name="userPassword" disabled="true"
                                size="24" maxlength="32" cssStyle="display:none" />
                            <s:text name="admin.user.password.ranger" /></td>
                        </tr>
                        <tr>
                            <td class="labelT1"><label> <s:text
                                name="admin.user.password.confirm" /><font color="red"><s:text
                                name="*" /> </font> </label></td>
                            <td><s:password id="passwordConfirm" name="passwordConfirm"
                                size="24" maxlength="%{passwdLength}" /> <s:textfield
                                id="passwordConfirm_text" name="passwordConfirm"
                                disabled="true" size="24" maxlength="%{passwdLength}"
                                cssStyle="display:none" /> <s:text
                                name="admin.user.password.ranger" /></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><s:checkbox id="chkToggleDisplay" name="ignore"
                                        value="true" disabled="%{writeDisable4Struts}"
                                        onclick="hm.util.toggleObscurePassword(this.checked,['userPassword','passwordConfirm'],['userPassword_text','passwordConfirm_text']);" />
                                    </td>
                                    <td><s:text name="admin.user.obscurePassword" /></td>
                                </tr>
                            </table>
                            </td>
                        </tr>                        
                        <tr>
                            <td height="10"></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
</div>
<script type="text/javascript">
var YUD = YAHOO.util.Dom;
var noteTimer;
function getCustomerId() {
	var YUC = YAHOO.util.Connect;
	var emailEl = YUD.get("email");
	var value = emailEl.value.trim();
	if(value.length == 0) {
		emailEl.focus();
        hm.util.reportFieldError(emailEl, 
        		'<s:text name="error.requiredField"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
        return;
	}
       if(!hm.util.validateEmail(value)) {
		emailEl.focus();
        hm.util.reportFieldError(emailEl, 
        		'<s:text name="error.gml.temporary.email.invalid"></s:text>');
        return;
       }
        
	var enableObs = YUD.get("chkToggleDisplay");
	var pwEl = YUD.get("userPassword");
	var confirmpwEl = YUD.get("passwordConfirm");
	if(!enableObs.checked) {
		pwEl = YUD.get("userPassword_text");
		confirmpwEl = YUD.get("passwordConfirm_text");
	}
	if(pwEl.value.trim().length == 0) {
		pwEl.focus();
        hm.util.reportFieldError(pwEl, 
                '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password" /></s:param></s:text>');	
        return;
	}
	if(confirmpwEl.value.trim().length == 0) {
		confirmpwEl.focus();
        hm.util.reportFieldError(confirmpwEl, 
                '<s:text name="error.requiredField"><s:param><s:text name="admin.user.password.confirm" /></s:param></s:text>');
        return;
	}
    if (confirmpwEl.value.trim() != pwEl.value.trim()) {
        pwEl.focus();
        hm.util.reportFieldError(pwEl, '<s:text name="error.passwordConfirm"></s:text>');
        return;
    }
    
    var url = "<s:url action='retrieveCACustomerId' includeParams='none' />?operation=retrieve"
    		+ "&userName=" + encodeURIComponent(emailEl.value.trim())
    		+ "&password=" + encodeURIComponent(pwEl.value.trim())
    		+ "&ignore="+new Date().getTime();
    YUC.asyncRequest('GET', url, 
    		{success : succRetriveId, failure : failRetriveId, timeout: 60000}, null);
    
	if(null == waitingPanel) {
		createWaitingPanel();
	}
	waitingPanel.show();
}
function succRetriveId(o) {
	if(null != waitingPanel) {
		waitingPanel.hide();
	}
	eval("var details = " + o.responseText);
	if(details.succ) {
		showRespMsg('<s:text name="info.idm.retrieveCustomerId.succ"></s:text>' ,true);
	} else {
		showRespMsg(details.err);
	}
}
function failRetriveId(o) {}
function showRespMsg(txt, flag) {
	if(flag) {
		YUD.get("infoRow").innerHTML = txt;
		YUD.get("errorRow").innerHTML = '';
	} else {
		YUD.get("infoRow").innerHTML = '';
		YUD.get("errorRow").innerHTML = txt;
	}
	YUD.get("noteSection").style.display = '';
	noteTimer = setTimeout("hideRespMsg()", 5 * 1000);  // 5 seconds
}
function hideRespMsg() {
    YUD.get("infoRow").innerHTML = '';
    YUD.get("errorRow").innerHTML = '';
    YUD.get("noteSection").style.display = 'none';	
}
function onUnloadNotes() {
    clearTimeout(noteTimer);
}
</script>