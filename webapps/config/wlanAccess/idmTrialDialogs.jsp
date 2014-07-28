<%@taglib prefix="s" uri="/struts-tags"%>
<style type="text/css">
ul.contactList {
    line-height: 25px;
    margin: 10px 0;
}
ul.contactList li:first-child span{
    padding-left: 5px;
}
ul.contactList li+li>span>span {
    padding-left: 60px;
}
</style>
<div id="completeCustomerInfoPanel" style="display: none;">
    <div class="hd">Complete your information</div>
    <div class="bd">
        <div style="padding: 10px 0 0 5px;">Please fill the necessary information</div>
        <div style="padding: 20px 0 20px 20px;" id="customerFieldsContent">
        </div>
        <div style="margin: 0 0 15px 20px;">
	         <input type="button" value="Okay" id="completeCustomerBtn" class="button"/>
	         <input type="button" value="Reset" id="resetCustomerBtn" style="margin-left: 40px;" class="button"/>
	         <input type="button" value="Cancel" id="closeCustomerBtn" style="margin-left: 40px;" class="button"/>
        </div>
    </div>
</div>
<div id="idmTrialSettingsPanel" style="display: none;">
    <div class="hd">Sign up for an ID Manager Trial</div>
    <div class="bd">
        <div>
	        <fieldset>
	            <legend>Number of concurrent accounts</legend>
	            <div style="line-height: 25px;" id="accountsContent"></div>
	        </fieldset>
	        <fieldset style="margin-top: 10px;">
	            <legend>Directory integration for employee sponsorship</legend>
	            <div style="line-height: 25px;">
	                <div>
	                    <input type="checkbox" id="enableDI" style="margin-bottom: 3px;"/><label for="enableDI">Enable directory integration</label>
	                </div>
	                <div style="margin-left: 20px;">
	                    <label>Domain:&nbsp;</label><input name="domainName" id="domainNameEl" style="width: 200px; margin-left: 20px;" disabled="disabled"/>
	                </div>
	            </div>
	        </fieldset>
        </div>
        <div style="margin: 10px 0; text-align: center;">
            <input type="button" value="Create my free trial" id="trialBtn" class="button" style="width: 140px;"/>
        </div>
    </div>
</div>
<script id="customerFieldsTmpl" type="text/x-dot-template">
   <table style="line-height: 25px;">
       <tbody>
       <tr>
           <td style="width: 100px;">Email</td>
           <td>{{=it.email}}</td>
       </tr>
       <tr>
           <td>First Name<label style="color: red;">*</label></td>
           <td><input id="firstName" maxlength="32" value="{{=it.firstName||''}}"
               onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>&nbsp;(1-32 characters)</td>
       </tr>
       <tr>
           <td>Last Name<label style="color: red;">*</label></td>
           <td><input id="lastName" maxlength="32" value="{{=it.lastName||''}}"
               onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>&nbsp;(1-32 characters)</td>
       </tr>
       <tr>
           <td>Company Name<label style="color: red;">*</label></td>
           <td><input id="companyName" maxlength="64" value="{{=it.companyName||''}}"
               onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>&nbsp;(1-64 characters)</td>
       </tr>
       </tbody>
   </table>
</script>
<script id="accountsTmpl" type="text/x-dot-template">
{{~it.array :option:index}}
<div><input type="radio" value="{{=option.maxguests}}" name="guestAccount" {{? index==0}}checked="checked"{{?}} id="guestAccount_{{=index}}"/><label for="guestAccount_{{=index}}">{{=option.desc}}</label></div>
{{~}}
</script>
<script id="idmTmpl" type="text/x-dot-template">
<input type="checkbox" onclick="enabledCloudAuth(this.checked);" id="enableIDMChk" value="true" name="dataSource.enabledIDM">
<input type="hidden" value="true" name="__checkbox_dataSource.enabledIDM" id="__checkbox_enableIDMChk">
<span class="icon-idm "><label class="text-idm " for="enableIDMChk">Use Aerohive ID Manager</label></span>&nbsp;
<a target="_blank" tabindex="-1" href="{{=it.idmWeb}}" style="display: none;" id="manageGuestIDMAnchor">Manage Guests</a>
</script>
<script>
var customerInfoPanel = null, idmTrialPanel = null;
var fields = <s:property value='customerFields' escapeHtml="false"/>;
var requestTimeout = 15 * 60 * 1000;

var waitingPanel = null;
function createWaitingPanel() {
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
    waitingPanel.setHeader("Sending request...");
    waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
    waitingPanel.render(document.body);
}
function openWaitingPanel() {
	if(null == waitingPanel) {
	    createWaitingPanel();
	}
	waitingPanel.show();
}
function hideWaitingPanel(){
    if(null != waitingPanel){
    	waitingPanel.cfg.setProperty('visible', false);
    }
}

function initTrialLink() {
	if(typeof(doT) == 'undefined' || !(doT && doT.template)) {
		head.js("<s:url value='/js/doT.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(Get('trialIDMAnchor')) {
		if(fields.email) {
			// need to complete customer
			YAHOO.util.Event.on('trialIDMAnchor', 'click', openCustomerInfoPanel);
		} else {
			// directly open option
			YAHOO.util.Event.on('trialIDMAnchor', 'click', getTrialSettings);
		}
	}
	
}
function renderHTML(tmplId, contentId, content) {
	var contentEl = document.getElementById(contentId);
	var tmplEl = document.getElementById(tmplId);
	if(contentEl && tmplEl) {
		var fn = doT.template(tmplEl.text);
		contentEl.innerHTML = fn(content);
	}
}
function initCompleteCustomerInfoPanel() {
    renderHTML('customerFieldsTmpl', 'customerFieldsContent', fields);
    
    var div = document.getElementById('completeCustomerInfoPanel');
    customerInfoPanel = new YAHOO.widget.Panel(div, {
        width:"410px",
        visible:false,
        draggable:false,
        modal:true,
        constraintoviewport:true,
        zIndex:10
        });
    //Allow escape key to close box
    var escListener = new YAHOO.util.KeyListener(document, { keys:27},
            { fn:hideCustomerInfoPanel, scope:customerInfoPanel, correctScope:true } );
    customerInfoPanel.cfg.queueProperty("keylisteners", escListener);
    
    customerInfoPanel.render(formName);
    div.style.display = "";
    
    YAHOO.util.Event.on('completeCustomerBtn', 'click', complateCustomerInfo);
    YAHOO.util.Event.on('resetCustomerBtn', 'click', resetCustomerInfo);
    YAHOO.util.Event.on('closeCustomerBtn', 'click', hideCustomerInfoPanel);
}
function openCustomerInfoPanel(){
    if(null == customerInfoPanel){
    	initCompleteCustomerInfoPanel();
    }
    customerInfoPanel.center();
    customerInfoPanel.cfg.setProperty('visible', true);
}

function hideCustomerInfoPanel(){
    if(null != customerInfoPanel){
    	customerInfoPanel.cfg.setProperty('visible', false);
    }
    resetCustomerInfo();
}
function complateCustomerInfo() {
	if(Get('firstName') && Get('firstName').value == '') {
		Get('firstName').focus();
		hm.util.reportFieldError(Get('firstName'), '<s:text name="error.requiredField"><s:param>First Name</s:param></s:text>');
		return;
	}
	if(Get('lastName') && Get('lastName').value == '') {
		Get('lastName').focus();
		hm.util.reportFieldError(Get('lastName'), '<s:text name="error.requiredField"><s:param>Last Name</s:param></s:text>');
		return;
	}
	if(Get('companyName') && Get('companyName').value == '') {
		Get('companyName').focus();
		hm.util.reportFieldError(Get('companyName'), '<s:text name="error.requiredField"><s:param>Company Name</s:param></s:text>');
		return;
	}
	var url = idmCustomerUrl;
	url += "&firstName=" + encodeURIComponent(Get('firstName').value) 
	   + "&lastName=" + encodeURIComponent(Get('lastName').value) 
	   + "&companyName=" + encodeURIComponent(Get('companyName').value);
	url += "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
			{success : initTriaSettings, failure : handleFailureRequest, timeout: requestTimeout}, null);
	openWaitingPanel();
}
function initTriaSettings(o) {
	hideWaitingPanel();
	eval("var details = " + o.responseText);
	if(details.succ) {
		if(details.settings) {
			renderHTML('accountsTmpl', 'accountsContent', {array: eval("("+details.settings+")")});
			if(details.email) {
				Get('domainNameEl').value = details.email.substr(details.email.indexOf('@')+1); 
			}
			// change the trial link event
			YAHOO.util.Event.removeListener('trialIDMAnchor', 'click');
			YAHOO.util.Event.on('trialIDMAnchor', 'click', getTrialSettings);
			
			openIdmTrialPanel();
		} else {
			promptWarnning(details.errMsg);
		}
	} else {
		promptWarnning(details.errMsg);
	}
}
function promptWarnning(messag) {
    if(warnDialog) {
        warnDialog.cfg.setProperty('text', messag);
        warnDialog.show();
    }
}
function resetCustomerInfo() {
	if(Get('firstName')) {
		Get('firstName').value='';
	}
	if(Get('lastName')) {
		Get('lastName').value='';
	}
	if(Get('companyName')) {
		Get('companyName').value='';
	}
}
function getTrialSettings() {
	if(null == idmTrialPanel) {
		var transaction = YAHOO.util.Connect.asyncRequest('GET', trialSettingsUrl, 
				{success : initTriaSettings, failure : handleFailureRequest, timeout: requestTimeout}, null);
		openWaitingPanel();
	} else {
		openIdmTrialPanel();
	}
}
function initIdmTrialPanel() {
    var div = document.getElementById('idmTrialSettingsPanel');
    idmTrialPanel = new YAHOO.widget.Panel(div, {
        width:"450px",
        visible:false,
        draggable:false,
        modal:true,
        constraintoviewport:true,
        zIndex:10
        });
    //Allow escape key to close box
    var escListener = new YAHOO.util.KeyListener(document, { keys:27},
    		{ fn:hideIdmTrialPanel, scope:idmTrialPanel, correctScope:true } );
    idmTrialPanel.cfg.queueProperty("keylisteners", escListener);
    
    idmTrialPanel.render(formName);
    div.style.display = "";
    
    YAHOO.util.Event.on('trialBtn', 'click', createIDMCustomer);
    YAHOO.util.Event.on('enableDI', 'click', function(){
    	Get('domainNameEl').disabled = !this.checked;
    });
}
function createIDMCustomer() {

	var maxGuests = 0;
	var radios = document.getElementsByName('guestAccount');
	for(var i=0; i<radios.length; i++) {
		if(radios[i].checked) {
			maxGuests = radios[i].value;
			break;
		}
	}
	// validate domain name
	if(Get('enableDI').checked && !Get('domainNameEl').disabled) {
		var dnName = Get('domainNameEl').value;
		if(dnName.trim() == '') {
			hm.util.reportFieldError(Get('domainNameEl'), '<s:text name="error.requiredField"><s:param>Domain Name</s:param></s:text>');
			Get('domainNameEl').focus();
			return;
		} else {
            var domainRe = new RegExp(/^[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,6}$/i);
            if(!domainRe.test(dnName.trim())) {
				hm.util.reportFieldError(Get('domainNameEl'), '<s:text name="error.formatInvalid"><s:param>Domain Name</s:param></s:text>');
				Get('domainNameEl').focus();
				return;
            }
		}
	}
	var createUrl = createIDMCustomerUrl;
	createUrl += "&maxGuests=" + maxGuests + "&enabledGuestDirectory=" + Get('enableDI').checked + "&guestDomainName=" + encodeURIComponent(Get('domainNameEl').value);
	createUrl += "&ignore="+new Date().getTime();
	
    var transaction = YAHOO.util.Connect.asyncRequest('GET', createUrl, {success : createIDMCustomerSucc, failure : handleFailureRequest, timeout: requestTimeout}, null);
    openWaitingPanel();
}
function createIDMCustomerSucc(o) {
	hideWaitingPanel();
	hideIdmTrialPanel();
	eval("var details = " + o.responseText);
	if(details.succ) {
		// refresh the IDM section
		if(details.idmWeb) {
			renderHTML('idmTmpl', 'idmContent', {idmWeb: details.idmWeb});
		}
	} else {
		promptWarnning(details.errMsg);
	}
}
function openIdmTrialPanel(){
    hideCustomerInfoPanel();
    
    if(null == idmTrialPanel){
    	initIdmTrialPanel();
    }
    idmTrialPanel.center();
    idmTrialPanel.cfg.setProperty('visible', true);
}

function hideIdmTrialPanel(){
    if(null != idmTrialPanel){
    	idmTrialPanel.cfg.setProperty('visible', false);
    }
}

function handleFailureRequest() {
	hideWaitingPanel();
	hideCustomerInfoPanel();
	hideIdmTrialPanel();
	showWarnDialog("Request timeout, please refresh page or try it later.");
}
</script>