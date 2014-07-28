<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap"%>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script><!--
var formName = 'activeDirectoryOrLdap';
var TRYVERIFY = <%=ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_TRY%>;
var ipnames;
var inputElement;
var errorTitle;
var showError;
var AUTH = 'auth';
var MULTIPLE_DOMAIN = "multipleDomain"

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
	waitingPanel.setHeader("Retrieving Information...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

YAHOO.util.Event.addListener(window, "load", function() {
		createWaitingPanel();
});
    
function onLoadPage() {
	
	// show/hide button
	setBtnShowHide();
	initFullnameTip();
	initDomainuserTip();
	
	if (!document.getElementById(formName + "_dataSource_name").disabled) {
		document.getElementById(formName + "_dataSource_name").focus();
	}
	if(document.getElementById("hideActive").style.display == "") {
		ipnames = document.getElementById("myAdSelect");
		inputElement = document.forms[formName].inputAdValue;
		errorTitle = '<s:text name="config.radiusOnHiveAp.adDomain.server" />';
		showError = document.getElementById("adErrorDisplay");
	} else if(document.getElementById("hideOpen").style.display == "") {
		ipnames = document.getElementById("myLdapSelect");
		inputElement = document.forms[formName].inputLdapValue;
		errorTitle = '<s:text name="config.radiusOnHiveAp.ldapServer" />';
		showError = document.getElementById("ldapErrorDisplay");
	}
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
    	top.changeIFrameDialog(910, 750);
	}
	</s:if>

	// add autocomplete UI
	initAutoCompleteRadiusComboBox();
	
	//get the domain info of selected AP
	doGetApDomain();
}

function initFullnameTip(){
	var el = document.getElementById("defDomFullName");
	if(el == null){return;}
	var updateTip = function(){
			if(el.value.length == 0){
				el.style.backgroundImage = "url(<s:url value="/images/tip-fullname.png" includeParams="none"/>)";
				el.style.backgroundRepeat = "no-repeat";
			}else{
				el.style.backgroundImage = "none";
			}
		}
	window.setInterval(updateTip, 100);
}

function initDomainuserTip(){
	var el = document.getElementById("defDomBind");
	if(el == null){return;}
	var updateTip = function(){
			if(el.value.length == 0){
				el.style.backgroundImage = "url(<s:url value="/images/tip-domainuser.png" includeParams="none"/>)";
				el.style.backgroundRepeat = "no-repeat";
			}else{
				el.style.backgroundImage = "none";
			}
		}
	window.setInterval(updateTip, 100);
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		if(document.getElementById("hideOpen").style.display == "") {
			Get(formName + "_dataSource_optionalStyle").value = Get("optionSet").style.display;
		}
		if(document.getElementById("hideActive").style.display == "") {
			Get(formName + "_dataSource_multipleDomainStyle").value = Get(MULTIPLE_DOMAIN).style.display;
		}
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if('<%=Navigation.L2_FEATURE_RADIUS_ACTIVE_DIRECTORY%>' == operation 
		|| operation == 'cancel<s:property value="lstForward"/>') {
		document.getElementById(formName + "_dataSource_destinationPort").value = 0;
		return true;
	}
	if(operation == 'newFile4' || operation == 'newFile5' || operation == 'newFile6' || operation == 'newIpAddress') {
		var port = document.getElementById(formName + "_dataSource_destinationPort");
    	if(port.value.length > 0 && !checkInputRange(port, '<s:text name="config.radiusOnHiveAp.ldap.port" />', 1,65535)) {
    		return false;
    	}
    	setCreateSingleObj();
		return true;
	}
	
	if(operation == "editIpAddress"){
		var value = hm.util.validateListSelection(document.getElementById("hideActive").style.display == "" ? "myAdSelect" : "myLdapSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].ipAddressId.value = value;
		}
	}
	<s:if test="%{jsonMode}">
	if(operation == "newIpAddress" || operation == "editIpAddress"){
		top.changeIFrameDialog(950, 450);
	}
	</s:if>
	
	if (operation == 'create<s:property value="lstForward"/>' || operation == 'create') {
		var name = document.getElementById(formName + "_dataSource_name");
		if (!checkNameValid(name, '<s:text name="config.radiusOnHiveAp.radiusName" />', name)) {
	       	return false;
	   	}
	}
   	
   	var table = document.getElementById("checkAll");
	if (operation == 'addAdDomain') {
		var rowcount = document.getElementsByName('domServers');
		if(rowcount.length >= 7) {
			showHideContent(MULTIPLE_DOMAIN,"");
			hm.util.reportFieldError(table, '<s:text name="error.entryLimit"><s:param><s:text name="config.radiusOnHiveAp.ad.multiple.domain" /></s:param><s:param value="8" /></s:text>');
       		table.focus();
       		return false;
		} 
		var adFullName = document.forms[formName].adFullName;
		var domServer = document.forms[formName].domServer;
		var bindDn = document.forms[formName].bindDn;
		
		if(!checkNameWithBlankValidate(adFullName, '<s:text name="config.radiusOnHiveAp.workName" />', MULTIPLE_DOMAIN)
			|| !checkNameWithBlankValidate(domServer, '<s:text name="config.radiusOnHiveAp.adDomain.server" />', MULTIPLE_DOMAIN)
			|| !checkNameWithBlankValidate(bindDn, '<s:text name="config.radiusOnHiveAp.domain.user" />', MULTIPLE_DOMAIN)) {
			return false;
		}
		var password;
		var confirm;
		if (document.getElementById("chkBindDisplay").checked) {
			password = document.getElementById("bindPass");
			confirm = document.getElementById("confirmBind");
		} else {
			password = document.getElementById("bindPass_text");
			confirm = document.getElementById("confirmBind_text");
		}
		if(!checkAdDomainPassword(password, confirm, '<s:text name="config.radiusOnHiveAp.domain.user.password" />', 
			'<s:text name="config.radiusOnHiveAp.passConf" />', MULTIPLE_DOMAIN)) {
        	return false;
    	}
		setCreateSingleObj();
	}
	if (operation == 'removeAdDomain' || operation == 'removeAdDomainNone') {
		var cbs = document.getElementsByName('ruleIndices');
		if (cbs.length == 0) {
			showHideContent(MULTIPLE_DOMAIN,"");
			hm.util.reportFieldError(table, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		if (!hm.util.hasCheckedBoxes(cbs)) {
			showHideContent(MULTIPLE_DOMAIN,"");
            hm.util.reportFieldError(table, '<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.radiusOnHiveAp.ad.multiple.domain" /></s:param></s:text>');
			return false;
		}
		setCreateSingleObj();
	}
	
	if (operation == 'create<s:property value="lstForward"/>' 
		|| operation == 'update<s:property value="lstForward"/>'
		|| operation == 'create'
		|| operation == 'update') {
		// open directory does not have this
		if(document.getElementById("hideOpen").style.display == "" || document.getElementById("hideActive").style.display == "") {
			if(!checkServerIp()){
				return false;
			}
		}
		if(document.getElementById("hideActive").style.display == "") {
			// if section of AP's IP and gataway is displayed
			if (document.getElementById("apIpDns").style.display == "") {
				// check Ap static ip and dns
				if(!checkApIpAndDns()) {
					return false;
				}
			}
			
			// check all operations(retrieving,joining,test authentication) are successful or not
			if(!checkRetrieveTestSuccess()) {
				return false;
			}
			
			/*// set ad server's ip/hostname
			document.forms[formName].ipAddressId.value = -1;
			document.forms[formName].inputIpValue.value = document.getElementById("adServer").value;*/
			
			var domain = document.getElementById("defDomName");
			var fullName = document.getElementById("defDomFullName");
			
			var message = hm.util.validateStringWithBlank(domain.value, '<s:text name="config.radiusOnHiveAp.workName" /> of Default Domain');
		   	if (message != null) {
		   		hm.util.reportFieldError(fullName, message);
		       	fullName.focus();
		       	return false;
		   	}
		   	
			if(!checkNameWithBlankValidate(fullName, '<s:text name="config.radiusOnHiveAp.workName" /> of Default Domain')) {
				return false;
			}
			
			var computerOU = document.getElementById(formName + "_dataSource_computerOU");
			if (computerOU.value.length > 0 && !checkNameWithBlankValidate(computerOU, 
				'<s:text name="config.radiusOnHiveAp.active.computer" />')) {
		  		return false;
			}
			
			if(!checkAdminAd()) {
				return false;
			}
			
			if(!checkBindDnAd()) {
				return false;
			}
	    }
		
		if(document.getElementById("hideOpen").style.display == "") {
			var basedN = document.getElementById(formName + "_dataSource_basedN");
			var bindDnName = document.getElementById(formName + "_dataSource_bindDnName");
			var filterAttr = document.getElementById(formName + "_dataSource_filterAttr");
			
			if(!checkNameWithBlankValidate(basedN, '<s:text name="config.radiusOnHiveAp.basedn" />')
				|| !checkNameWithBlankValidate(bindDnName, '<s:text name="config.radiusOnHiveAp.bindDN.name" />')) {
				return false;
			}

			var passwordO;
			var confirmPass;
			if (document.getElementById("chkPasswordODisplay").checked) {
				passwordO = document.getElementById("passwordO");
				confirmPass = document.getElementById("confirmPasswordO");
			} else {
				passwordO = document.getElementById("passwordO_text");
				confirmPass = document.getElementById("confirmPasswordO_text");
			}
			if(!checkAdDomainPassword(passwordO, confirmPass, '<s:text name="config.radiusOnHiveAp.bindDN.password" />', 
				'<s:text name="config.radiusOnHiveAp.passConf" />')) {
	        	return false;
	    	}
		   	if (filterAttr.value.length > 0) {
		   		var message = hm.util.validateStringWithBlank(filterAttr.value, '<s:text name="config.radiusOnHiveAp.ldap.filter.attribute" />');
			   	if (message != null) {
			   		showHideContent("optionSet","");
			   		hm.util.reportFieldError(filterAttr, message);
			       	filterAttr.focus();
			       	return false;
			   	}
		   	}
		  			
			var port = document.getElementById(formName + "_dataSource_destinationPort");
		  	if(!checkInputRange(port, '<s:text name="config.radiusOnHiveAp.ldap.port" />', 1,65535)) {
		  		return false;
		  	}

		  	var protocol = document.getElementById(formName + "_dataSource_ldapProtocol");
		  	var tls = document.getElementById("enableLdapTls");
		  	if (parseInt(protocol.value) == <%=ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL_LDAPS%> && !tls.checked) {
		  		showHideContent("optionSet","");
		  		hm.util.reportFieldError(tls, '<s:text name="error.radius.useEdirectory.mustTLS"><s:param>LDAPS communication protocol</s:param></s:text>');
		  		tls.focus();
	   			return false;
		  	}
		  	
			// var auth = document.getElementById("hideAuth");
			if(tls.checked) {
				var clientFile = document.getElementById(formName + "_dataSource_clientFile");
				var keyFileO = document.getElementById(formName + "_dataSource_keyFileO");
				var caCertFile = document.getElementById(formName + "_dataSource_caCertFileO");
		    	if(caCertFile.value == "")
		    	{
		            hm.util.reportFieldError(caCertFile, '<s:text name="info.emptyList" />');
		            caCertFile.focus();
					return false;
		    	}
				if(clientFile.value == "" && keyFileO.value != "") {
		   			hm.util.reportFieldError(clientFile, '<s:text name="error.radius.openldap.file"><s:param><s:text name="config.radiusOnHiveAp.client" /></s:param></s:text>');
		   			clientFile.focus();
		   			return false;
				} else if(clientFile.value != "" && keyFileO.value == "") {
		   			hm.util.reportFieldError(keyFileO, '<s:text name="error.radius.openldap.file"><s:param><s:text name="config.radiusOnHiveAp.private" /></s:param></s:text>');
		   			keyFileO.focus();
		   			return false;
				}
				
				var keyPassword;
				var confPassword;
				if (document.getElementById("chkKeyPassDisplay").checked) {
					keyPassword = document.getElementById("keyPasswordO");
					confPassword = document.getElementById("confPasswordO");
				} else {
					keyPassword = document.getElementById("keyPasswordO_text");
					confPassword = document.getElementById("confPasswordO_text");
				}
				if(clientFile.value == "" && keyFileO.value == "") {
					if(keyPassword.value.length > 0) {
						hm.util.reportFieldError(keyPassword, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.private" /></s:param><s:param><s:text name="config.radiusOnHiveAp.keyPass" /></s:param></s:text>');
	        			keyPassword.focus();
	        			return false;
					}
					if(confPassword.value.length > 0) {
						hm.util.reportFieldError(confPassword, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.private" /></s:param><s:param><s:text name="config.radiusOnHiveAp.passConf" /></s:param></s:text>');
	        			confPassword.focus();
	        			return false;
					}
				}
				if(keyPassword.value.length > 0 || confPassword.value.length > 0) {
					if(!checkPassword(keyPassword, confPassword, '<s:text name="config.radiusOnHiveAp.keyPass" />', 
						'<s:text name="config.radiusOnHiveAp.passConf" />')) {
		  				return false;
					}
				}
			}
		}
		
		if(document.getElementById("hideOpenDir").style.display == "") {
			var domain = document.getElementById("defOdDomName");
			var fullName = document.getElementById("defOdDomFullName");
			var bindDn = document.getElementById("defOdDomBind");
			var filterAttr = document.getElementById(formName + "_dataSource_filterAttrOd");
			
			if(!checkNameWithBlankValidate(domain, '<s:text name="config.radiusOnHiveAp.workName" /> of Default Domain')
				|| !checkNameWithBlankValidate(fullName, '<s:text name="config.radiusOnHiveAp.realmName" /> of Default Domain')
				|| !checkNameWithBlankValidate(bindDn, '<s:text name="config.radiusOnHiveAp.bindDN.name" /> of Default Domain')) {
				return false;
			}
			
			var password;
			var confirm;
			if (document.getElementById("defOdDomChkBindDisplay").checked) {
				password = document.getElementById("defOdDomBindPass");
				confirm = document.getElementById("defOdDomConfirmBind");
			} else {
				password = document.getElementById("defOdDomBindPass_text");
				confirm = document.getElementById("defOdDomConfirmBind_text");
			}
			if(!checkAdDomainPassword(password, confirm, '<s:text name="config.radiusOnHiveAp.bindDN.password" /> of Default Domain', 
				'<s:text name="config.radiusOnHiveAp.passConf" /> of Default Domain')) {
	        	return false;
	    	}
			var userNameOd = document.getElementById(formName + "_dataSource_userNameOd");
			var passwordOd;
			var confirmPass;
			if (document.getElementById("chkPasswordOdDisplay").checked) {
				passwordOd = document.getElementById("passwordOd");
				confirmPass = document.getElementById("confirmPasswordOd");
			} else {
				passwordOd = document.getElementById("passwordOd_text");
				confirmPass = document.getElementById("confirmPasswordOd_text");
			}
			if (userNameOd.value.length > 0) {
				if(!checkNameWithBlankValidate(userNameOd, '<s:text name="config.radiusOnHiveAp.userName" />')) {
					return false;
				}
				if(!checkAdDomainPassword(passwordOd, confirmPass, '<s:text name="config.radiusOnHiveAp.password" />', 
					'<s:text name="config.radiusOnHiveAp.passConf" />')) {
		        	return false;
		    	}
			} else {
				if (passwordOd.value.length > 0) {
					hm.util.reportFieldError(passwordOd, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param><s:param><s:text name="config.radiusOnHiveAp.password" /></s:param></s:text>');
	        		passwordOd.focus();
	        		return false;
				} 
				if (confirmPass.value.length > 0) {
					hm.util.reportFieldError(confirmPass, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param><s:param><s:text name="config.radiusOnHiveAp.passConf" /></s:param></s:text>');
	        		confirmPass.focus();
	        		return false;
				}
			}
		   	if (filterAttr.value.length > 0) {
		   		var message = hm.util.validateStringWithBlank(filterAttr.value, '<s:text name="config.radiusOnHiveAp.ldap.filter.attribute" />');
			   	if (message != null) {
			   		showHideContent("optionSet","");
			   		hm.util.reportFieldError(filterAttr, message);
			       	filterAttr.focus();
			       	return false;
			   	}
		   	}
	    }
	}
	return true;
}

function checkApIpAndDns() {
    var hiveAPIpAddressElement=Get("hiveAPIpAddress");
    if(hiveAPIpAddressElement.value==""){
        hm.util.reportFieldError(hiveAPIpAddressElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.ipAddress" /></s:param></s:text>');
        hiveAPIpAddressElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpAddressElement.value)){
        hm.util.reportFieldError(hiveAPIpAddressElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.ipAddress" /></s:param></s:text>');
        hiveAPIpAddressElement.focus();
        return false;
    }
    var hiveAPIpNetmaskElement=Get("hiveAPNetmask");
    if(hiveAPIpNetmaskElement.value==""){
        hm.util.reportFieldError(hiveAPIpNetmaskElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.netmask" /></s:param></s:text>');
        hiveAPIpNetmaskElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpNetmaskElement.value)){
        hm.util.reportFieldError(hiveAPIpNetmaskElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.netmask" /></s:param></s:text>');
        hiveAPIpNetmaskElement.focus();
        return false;
    }
    var hiveAPIpGatewayElement=Get("hiveAPGateway");
    if(hiveAPIpGatewayElement.value==""){
        hm.util.reportFieldError(hiveAPIpGatewayElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.gateway" /></s:param></s:text>');
        hiveAPIpGatewayElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPIpGatewayElement.value)){
        hm.util.reportFieldError(hiveAPIpGatewayElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.gateway" /></s:param></s:text>');
        hiveAPIpGatewayElement.focus();
        return false;
    }
    var hiveAPDNSServerElement=Get("hiveAPDNSServer");
    if(hiveAPDNSServerElement.value==""){
        hm.util.reportFieldError(hiveAPDNSServerElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.radiusServer.dns" /></s:param></s:text>');
        hiveAPDNSServerElement.focus();
        return false;
    }
    if(!hm.util.validateIpAddress(hiveAPDNSServerElement.value)){
        hm.util.reportFieldError(hiveAPDNSServerElement, '<s:text name="error.formatInvalid"><s:param><s:text name="config.ssid.radiusServer.dns" /></s:param></s:text>');
        hiveAPDNSServerElement.focus();
        return false;
    }
    return true;
}

function checkServerIp() {
	if ("" == inputElement.value) {
        hm.util.reportFieldError(showError, '<s:text name="error.config.network.object.input.direct"><s:param>'+errorTitle+'</s:param></s:text>');
        inputElement.focus();
		return false;
	}
	if (!hm.util.hasSelectedOptionSameValue(ipnames, inputElement)) {
		if (!hm.util.validateIpAddress(inputElement.value)) {
			if (!checkNameValid(inputElement, errorTitle, showError)) {
		       	return false;
		   	}
		}
		document.forms[formName].ipAddressId.value = -1;
	} else {
		document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
	}
	document.forms[formName].inputIpValue.value = inputElement.value;
	return true;
}

function checkRetrieveTestSuccess(){
	var retrieve = document.getElementById(formName + "_dataSource_retrieveSuccess");
	var testJoin = document.getElementById(formName + "_dataSource_testJoinSuccess");
	var testAuth = document.getElementById(formName + "_dataSource_testAuthSuccess");
	var defDomFullName = document.getElementById("defDomFullName")
	var userNameA = document.getElementById("userNameA")
	var defDomBind = document.getElementById("defDomBind")
	if(retrieve.value == 0) {
		hm.util.reportFieldError(defDomFullName, '<s:text name="info.config.retrieveAd.tip"></s:text>');
		return false;
	} else if(testJoin.value == 0) {
		hm.util.reportFieldError(userNameA, '<s:text name="info.config.testJoin.tip"></s:text>');
		return false;
	} else if(testAuth.value == 0) {
		hm.util.reportFieldError(defDomBind, '<s:text name="info.config.testAuth.tip"></s:text>');
		return false;
	}
	return true;
}

function checkBindDnAd() {
	var bindDn = document.getElementById("defDomBind");
	if(!checkDomainUserName(bindDn, '<s:text name="config.radiusOnHiveAp.domain.user" />')) {
		return false;
	} else {
	   	// fixed Bug 15445, for the backend side slice the test user name to test.
	   	var testName = bindDn.value;
    	var pos = testName.indexOf("@");
    	if(pos > -1) {
    		if(pos < testName.length) {
    			var domainFullName = document.getElementById("defDomFullName").value;
    			if(testName.substr(pos+1) != domainFullName) {
			        hm.util.reportFieldError(bindDn, 
			        		'<s:text name="error.ssid.test.domain.mismatch"/>');
			        return false;
    			}
    		}
    	}
	}
	
	var password;
	var confirm;
	if (document.getElementById("defDomChkBindDisplay").checked) {
		password = document.getElementById("defDomBindPass");
		confirm = document.getElementById("defDomConfirmBind");
	} else {
		password = document.getElementById("defDomBindPass_text");
		confirm = document.getElementById("defDomConfirmBind_text");
	}
	if(!checkAdDomainPassword(password, confirm, '<s:text name="config.radiusOnHiveAp.bindDN.password" /> of Default Domain', 
		'<s:text name="config.radiusOnHiveAp.passConf" /> of Default Domain')) {
       	return false;
   	}
   	return true;
}

function checkAdminAd() {
	var userNameA = document.getElementById("userNameA");
	var passwordA;
	var confirmPass;
	if (document.getElementById("chkPasswordADisplay").checked) {
		passwordA = document.getElementById("passwordA");
		confirmPass = document.getElementById("confirmPasswordA");
	} else {
		passwordA = document.getElementById("passwordA_text");
		confirmPass = document.getElementById("confirmPasswordA_text");
	}
	
	if (userNameA.value.length > 0) {
		if(!checkNameWithBlankValidate(userNameA, '<s:text name="config.radiusOnHiveAp.userName" />')) {
			return false;
		}
		if(!checkAdDomainPassword(passwordA, confirmPass, '<s:text name="config.radiusOnHiveAp.password" />', 
			'<s:text name="config.radiusOnHiveAp.passConf" />')) {
        	return false;
    	}
	} else {
		if (passwordA.value.length > 0) {
			hm.util.reportFieldError(passwordA, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param><s:param><s:text name="config.radiusOnHiveAp.password" /></s:param></s:text>');
       		passwordA.focus();
       		return false;
		} 
		if (confirmPass.value.length > 0) {
			hm.util.reportFieldError(confirmPass, '<s:text name="error.radius.noNeedPassword"><s:param><s:text name="config.radiusOnHiveAp.userName" /></s:param><s:param><s:text name="config.radiusOnHiveAp.passConf" /></s:param></s:text>');
       		confirmPass.focus();
       		return false;
		}
	}
	return true;
}

function setCreateSingleObj() {
	if (hm.util.hasSelectedOptionSameValue(ipnames, inputElement)) {
		document.forms[formName].ipAddressId.value = ipnames.options[ipnames.selectedIndex].value;
	} else {
		document.forms[formName].ipAddressId.value = -1;
		document.forms[formName].inputIpValue.value = inputElement.value;
	}
}

function checkNameValid(name, title, errorTd) {
	var message = hm.util.validateName(name.value, title);
   	if (message != null) {
   		hm.util.reportFieldError(errorTd, message);
       	name.focus();
       	return false;
   	}
   	return true;
}

function checkIfInput(inputElement, title)
{
	if (inputElement.value.length == 0) {
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    return true;
}

function checkInputRange(inputElement, title, min, max)
{
	if (inputElement.value.length == 0) {
		showHideContent("optionSet","");
        hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        inputElement.focus();
        return false;
    }
	var message = hm.util.validateIntegerRange(inputElement.value, title,min,max);
    if (message != null) {
    	showHideContent("optionSet","");
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    return true; 
}

function checkNameWithBlankValidate(name, title, contextId)
{
	var message = hm.util.validateStringWithBlank(name.value, title);
   	if (message != null) {
		if(contextId != null){
        	showHideContent(contextId,"");
        }
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}
    return true;
}

function checkDomainUserName(name, title, contextId){
	var message = hm.util.validateDoaminUserName(name.value, title);
   	if (message != null) {
		if(contextId != null){
        	showHideContent(contextId,"");
        }
   		hm.util.reportFieldError(name, message);
       	name.focus();
       	return false;
   	}
    return true;
}

function checkAdDomainPassword(password, confirm, passtitle, confirmtitle, contextId)
{
   	if (!checkNameWithBlankValidate(password, passtitle, contextId)) {
       	return false;
   	}
   	if (!checkNameWithBlankValidate(confirm, confirmtitle, contextId)) {
       	return false;
   	}
	if (password.value != confirm.value) {
		if(contextId != null){
        	showHideContent(contextId,"");
        }
        hm.util.reportFieldError(confirm, '<s:text name="error.password.notMatch" />');
        confirm.focus();
        return false;
    }
    return true;
}

function checkPassword(password, confirm, passtitle, confirmtitle)
{
	var message = hm.util.validatePassword(password.value, passtitle);
   	if (message != null) {
   		hm.util.reportFieldError(password, message);
       	password.focus();
       	return false;
   	}
   	var message = hm.util.validatePassword(confirm.value, confirmtitle);
   	if (message != null) {
   		hm.util.reportFieldError(confirm, message);
       	confirm.focus();
       	return false;
   	}
	if (password.value != confirm.value) {
        hm.util.reportFieldError(confirm, '<s:text name="error.notEqual"><s:param>'+confirmtitle+'</s:param><s:param>'+passtitle+'</s:param></s:text>');
        confirm.focus();
        return false;
    }
    return true;
}

function showActive() {
	document.getElementById("hideActive").style.display = "";
	document.getElementById("hideOpen").style.display = "none";
	document.getElementById("hideOpenDir").style.display = "none";
	ipnames = document.getElementById("myAdSelect");
	inputElement = document.forms[formName].inputAdValue;
	errorTitle = '<s:text name="config.radiusOnHiveAp.adDomain.server" />';
	showError = document.getElementById("adErrorDisplay");
}

function showOpenActive() {
	document.getElementById("hideActive").style.display = "none";
	document.getElementById("hideOpen").style.display = "none";
	document.getElementById("hideOpenDir").style.display = "";
}

function showOpen() {
	document.getElementById("hideActive").style.display = "none";
	document.getElementById("hideOpen").style.display = "";
	document.getElementById("hideOpenDir").style.display = "none";
	ipnames = document.getElementById("myLdapSelect");
	inputElement = document.forms[formName].inputLdapValue;
	errorTitle = '<s:text name="config.radiusOnHiveAp.ldapServer" />';
	showError = document.getElementById("ldapErrorDisplay");
}

function authTlsEnable(checked) {
	var auth = document.getElementById("hideAuth");
	auth.style.display = checked ? "" : "none";
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	// var trh = document.getElementById('headerSection');
	// var trc = document.getElementById('createSection');
	// var table = trh.parentNode;	
	// table.removeChild(trh);
	// table.insertBefore(trh, trc);
}

function hideCreateSection() {
	hm.util.hide('createButton');
	hm.util.show('newButton');
	hm.util.hide('createSection');
}

function toggleCheckAllRules(cb) {
	var cbs = document.getElementsByName('ruleIndices');
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function changeProtocol(protocol) {
	document.getElementById(formName + "_dataSource_destinationPort").value=(<%=ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL_LDAPS%>
	== protocol) ? 636 : 389;
}

function validateListSelection(elId){
	var el = document.getElementById(elId);
	if (el.options.length == 0) {//blank list
		hm.util.reportFieldError(el, '<s:text name="info.config.no.hiveAP.radius.server" />');
		return -1;
	} else {
		var index = el.selectedIndex;
		if (index < 0) {//no selection
			hm.util.reportFieldError(el, '<s:text name="info.config.select.hiveAP.radius.server" />');
			return -1;
		}
		var value = el.options[index].value;
		if (value == undefined || value <= 0) {//invalid selection
			hm.util.reportFieldError(el, '<s:text name="info.config.select.valid.hiveAP.radius.server" />');
			return -1;
		}
		return value;
	}
}

function retrieveAdInfo(){
	var el = document.getElementById("apMacHidden");
	var serverMac;
	if (el.value == "" || el.value == -1) {
		hm.util.reportFieldError(document.getElementById("apListTd"), '<s:text name="info.config.select.hiveAP.radius.server" />');
		return '';
	} else {
		serverMac = el.value;
	}
	/*var serverMac = validateListSelection("apServer");
	if(serverMac < 0){
		return;
	}*/
	var fullName = document.getElementById("defDomFullName");
	if (!checkNameWithBlankValidate(fullName, '<s:text name="config.radiusOnHiveAp.workName" /> of Default Domain')) {
		return;
	}
    var callback =
    {
		success: retrieveSuccess,
		failure: failure
    };
	var sUrl = '<s:url action="activeDirectoryOrLdap" includeParams='none' />' + '?operation=retrieveAdInfo&fullName=' + fullName.value + '&apServer=' + serverMac + '&ignore=' + new Date().getTime();
    //AJAX GET
	waitingPanel.show();
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}

var retrieveSuccess = function(o){
	waitingPanel.hide();
	eval("var result = " + o.responseText);
	var retrieveMessage = document.getElementById("retrieveMessage");
	var inputAdValue = document.getElementById("inputAdValue");
	var myAdSelect = document.getElementById("myAdSelect");
	if (result.resCode != 0) {
		// failed
		//hm.util.hide("hideAdmin");
		//hm.util.hide("hideBindDn");
		//hm.util.hide("hideActiveMutipleDomain");
		document.getElementById("defDomName").value = "";
		//document.getElementById("adServer").value = "";
		document.getElementById("adBaseDn").value = "";
		inputAdValue.value = "";
		inputAdValue.style.zIndex = 2;
		myAdSelect.selectedIndex = 0;
		//document.getElementById("adServerLabel").innerHTML = "";
		document.getElementById("adBaseDnLabel").innerHTML = "";
		document.getElementById(formName + "_dataSource_retrieveSuccess").value = 0;
		retrieveMessage.innerHTML = result.msg;
		YAHOO.util.Dom.replaceClass("retrieveMessageTd", "noteInfo", "noteError");
		hm.util.show("retrieveRow");
		//hm.util.reportFieldError(retrieveMessage, result.msg);
	} else {
		// success
		hm.util.show("hideAdmin");
		showHideBtn(false, true, false);
		document.getElementById("defDomName").value = result.domainName;
		//document.getElementById("adServer").value = result.adServer;
		inputAdValue.value = result.adServer;
		inputAdValue.style.zIndex = 2;
		myAdSelect.selectedIndex = 0;
		document.getElementById("adBaseDn").value = result.baseDN;
		//document.getElementById("adServerLabel").innerHTML = result.adServer;
		document.getElementById("adBaseDnLabel").innerHTML = result.baseDN;
		document.getElementById(formName + "_dataSource_retrieveSuccess").value = 1;
		document.getElementById(formName + "_dataSource_adminIsShow").value = 1;
		retrieveMessage.innerHTML = result.msg;
		YAHOO.util.Dom.replaceClass("retrieveMessageTd", "noteError", "noteInfo");
		hm.util.show("retrieveRow");
		delaySuccessHideNotes(2, "retrieveRow");
	}
}

function getUrl(type) {
	var el = document.getElementById("apMacHidden");
	var serverMac;
	if (el.value == "" || el.value == -1) {
		hm.util.reportFieldError(document.getElementById("apListTd"), '<s:text name="info.config.select.hiveAP.radius.server" />');
		return '';
	} else {
		serverMac = el.value;
	}
	if(!checkServerIp()){
		return '';
	} else {
		var ipAddressId = document.forms[formName].ipAddressId;
		var inputIpValue = document.forms[formName].inputIpValue;
	}
	if (type == AUTH) {
		if(!checkBindDnAd()) {
			return '';
		}
		var bindDnName = document.getElementById("defDomBind");
		var bindDnpass;
		if (document.getElementById("defDomChkBindDisplay").checked) {
			bindDnpass = document.getElementById("defDomBindPass");
		} else {
			bindDnpass = document.getElementById("defDomBindPass_text");
		}
	} else {
		var userNameA = document.getElementById("userNameA");
		if(!checkNameWithBlankValidate(userNameA, '<s:text name="config.radiusOnHiveAp.userName" />')) {
			return '';
		}
		if(!checkAdminAd()) {
			return '';
		}
		var passwordA;
		if (document.getElementById("chkPasswordADisplay").checked) {
			passwordA = document.getElementById("passwordA");
		} else {
			passwordA = document.getElementById("passwordA_text");
		}
	}
	var domName = document.getElementById("defDomName");
	var fullName = document.getElementById("defDomFullName");
	var baseDn = document.getElementById("adBaseDn");
	var sUrl = '<s:url action="activeDirectoryOrLdap" includeParams='none' />' + '?domName=' + domName.value + '&fullName=' + fullName.value + '&apServer=' + serverMac + '&ipAddressId=' + ipAddressId.value + '&inputIpValue=' + inputIpValue.value + '&baseDn=' + baseDn.value + '&ignore=' + new Date().getTime();
	if (type == AUTH) {
		sUrl = sUrl + '&operation=testAuth';
		sUrl = sUrl + '&bindDnName=' + escape(bindDnName.value) + '&bindDnpass=' + escape(bindDnpass.value);
	} else {
		sUrl = sUrl + '&operation=testJoin';
		sUrl = sUrl + '&userNameA=' + escape(userNameA.value) + '&passwordA=' + escape(passwordA.value);
	}
	return sUrl;
}

function doJoin(){
	var doSave = document.getElementById("chkSaveCredentials").checked;
    var callback =
    {
		success: testJoinSuccess,
		failure: failure,
		argument: {"doSave": doSave}
    };
	var sUrl = getUrl();
	// OU
	var computerOU = document.getElementById(formName + "_dataSource_computerOU");
	if (computerOU.value.length > 0) {
		if (!checkNameWithBlankValidate(computerOU, 
			'<s:text name="config.radiusOnHiveAp.active.computer" />')) {
	  		return;
		} else {
			sUrl += "&computerOu=" + escape(computerOU.value);
		}
	}
	// ldapSaslWrapping 0:plain, 1:sign, 2:seal 
	//var ldapSaslWrappingEl = document.getElementById(formName + "_dataSource_ldapSaslWrapping");
	//var ldapSaslWrapping = ldapSaslWrappingEl.options[ldapSaslWrappingEl.selectedIndex].value;
	var ldapSaslWrapping = 0; // revert LDAP SASL feature in FUJI, make it always return ""(plain);
	
	if (sUrl.length > 0 && ldapSaslWrapping.length > 0) {
		sUrl += "&ldapSaslWrapping=" + ldapSaslWrapping;
	}
	if(sUrl == '') return;
    //AJAX GET
	waitingPanel.show();
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}

var testJoinSuccess = function(o){
	waitingPanel.hide();
	eval("var result = " + o.responseText);
	var testJoinMessage = document.getElementById("testJoinMessage");
	if (result.resCode != 0) {
		// failed
		//hm.util.hide("hideBindDn");
		//hm.util.hide("hideActiveMutipleDomain");
		document.getElementById(formName + "_dataSource_testJoinSuccess").value = 0;
		hm.util.hide("testJoinRow");
		hm.util.reportFieldError(testJoinMessage, result.msg);
	} else {
		// success
		hm.util.show("hideBindDn");
		showHideBtn(false, true, true);
		document.getElementById(formName + "_dataSource_testJoinSuccess").value = 1;
		document.getElementById(formName + "_dataSource_domainUserIsShow").value = 1;
		testJoinMessage.innerHTML = result.msg;
		hm.util.show("testJoinRow");
		delaySuccessHideNotes(2, "testJoinRow");
		doClearAdminCredential(o.argument.doSave);
	}
}

function doClearAdminCredential(doSave) {
	
	if (!doSave) {
		//clear admin credentials
		document.getElementById("userNameA").value = "";
		document.getElementById("passwordA").value = "";
		document.getElementById("confirmPasswordA").value = "";
		document.getElementById("passwordA_text").value = "";
		document.getElementById("confirmPasswordA_text").value = "";
	}
}

function doTestAuth(){
    var callback =
    {
		success: testAuthSuccess,
		failure: failure
    };
	var sUrl = getUrl(AUTH);
	if(sUrl == '') return;
	//AJAX GET
	waitingPanel.show();
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}

var testAuthSuccess = function(o){
	waitingPanel.hide();
	eval("var result = " + o.responseText);
	var testAuthMessage = document.getElementById("testAuthMessage");
	if (result.resCode != 0) {
		// failed
		//hm.util.hide("hideActiveMutipleDomain");
		document.getElementById(formName + "_dataSource_testAuthSuccess").value = 0;
		hm.util.hide("testAuthRow");
		hm.util.reportFieldError(testAuthMessage, result.msg);
	} else {
		// success
		hm.util.show("hideActiveMutipleDomain");
		showHideBtn(false, true, true);
		document.getElementById(formName + "_dataSource_testAuthSuccess").value = 1;
		document.getElementById(formName + "_dataSource_multiDomainIsShow").value = 1;
		testAuthMessage.innerHTML = result.msg;
		hm.util.show("testAuthRow");
		delaySuccessHideNotes(2, "testAuthRow");
	}
}

var failure = function(o) {
	waitingPanel.hide();
	//alert("failed.");
};

var successNotesTimeoutId;
function delaySuccessHideNotes(seconds, elId) {
	successNotesTimeoutId = setTimeout('hideSuccessNotes("'+ elId +'")', seconds * 5000);  // seconds
}
function hideSuccessNotes(elId) {
	hm.util.wipeOut(elId, 800);
}

function doGetApDomain(){
	var el = document.getElementById("apMacHidden");
	var serverMac;
	if (el.value == "" || el.value == -1) {
		return;
	} else {
		serverMac = el.value;
	}
    var callback =
    {
		success: getApDomainSuccess,
		failure: failure
    };
	var sUrl = '<s:url action="activeDirectoryOrLdap" includeParams='none' />' + '?operation=getApDomain&apServer=' + serverMac + '&ignore=' + new Date().getTime();
    //AJAX GET
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}

var getApDomainSuccess = function(o){
	eval("var result = " + o.responseText);
	var message = document.getElementById("apDomainMessage");
	if (result.resCode == 0 && result.fullDomainName != "") {
		// success
		var msg = '<s:text name="info.config.getApDomain.success" ><s:param>' + result.fullDomainName + '</s:param></s:text>';
		message.innerHTML = msg;
		hm.util.show("apDomain");
		delaySuccessHideNotes(2, "apDomain");
	} else {
		// failure
		message.innerHTML = "";
		hm.util.hide("apDomain");
	}
}

function showHideBtn(showRetrieveBtn, showJoinBtn, showAuthBtn) {
	/*if (showRetrieveBtn) {
		hm.util.show("retrieveBtnId");
	} else {
		hm.util.hide("retrieveBtnId");
	} */ //for bug 14378 fix: leave retrieve button always visible
	if (showJoinBtn) {
		hm.util.show("joinBtnId");
	} else {
		hm.util.hide("joinBtnId");
	}
	if (showAuthBtn) {
		hm.util.show("authBtnId");
	} else {
		hm.util.hide("authBtnId");
	}
}

function textOnchange(text) {
	if (text.id == "defDomFullName") {
		// Retrieve AD Info area's text changed
		document.getElementById(formName + "_dataSource_retrieveSuccess").value = 0;
		document.getElementById(formName + "_dataSource_testJoinSuccess").value = 0;
		document.getElementById(formName + "_dataSource_testAuthSuccess").value = 0;
		//hm.util.hide("hideAdmin");
		//hm.util.hide("hideBindDn");
		//hm.util.hide("hideActiveMutipleDomain");
		showHideBtn(true, false, false);
	
	} else if (text.id == "userNameA" || text.id == "passwordA" || 
		text.id == "passwordA_text" || text.id == "confirmPasswordA" || text.id == "confirmPasswordA_text") {
		// Text Join area's text changed
		document.getElementById(formName + "_dataSource_testJoinSuccess").value = 0;
		document.getElementById(formName + "_dataSource_testAuthSuccess").value = 0;
		//hm.util.hide("hideBindDn");
		//hm.util.hide("hideActiveMutipleDomain");
		showHideBtn(false, true, false);
	
	} else if (text.id == "defDomBind" || text.id == "defDomBindPass" ||
		text.id == "defDomBindPass_text" || text.id == "defDomConfirmBind" || text.id == "defDomConfirmBind_text") {
		// Test Auth area's text changed
		document.getElementById(formName + "_dataSource_testAuthSuccess").value = 0;
		//hm.util.hide("hideActiveMutipleDomain");
		showHideBtn(false, true, true);
	}
}

function setBtnShowHide() {
	var retrieveSuccess = document.getElementById(formName + "_dataSource_retrieveSuccess").value;
	var testJoinSuccess = document.getElementById(formName + "_dataSource_testJoinSuccess").value;
	var testAuthSuccess = document.getElementById(formName + "_dataSource_testAuthSuccess").value;
	
	if (retrieveSuccess == 0) {//retrieve unsuccess
		showHideBtn(true, false, false);
	} else if (testJoinSuccess == 0) {
		showHideBtn(false, true, false);
	} else if (testAuthSuccess == 0) {
		showHideBtn(false, true, true);
	} else {
		showHideBtn(false, true, true);
	}
}

<s:if test="%{!jsonMode}">
function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="activeDirectoryOrLdap" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>
}
</s:if>

function initAutoCompleteRadiusComboBox() {
	// init DataSource
	var dataStr = "<s:property value='apListString'/>";
	//alert('dataStr=' + dataStr.replace(/\&quot;/g,'"'));
	var dataSource = eval('[' + dataStr.replace(/\&quot;/g,'"') + ']');
	
	var hiveAPDataSource = new YAHOO.util.LocalDataSource(dataSource);
	hiveAPDataSource.responseSchema = {fields :["name" , "id"]};
	
	// AutoComplete BomboBox primary RADIUS server constructor
	var acComboBox = autoCompelteComboBox('apServerName', 'apContainer', 'apComboBox', hiveAPDataSource, dataSource.length, "apMacHidden");
	acComboBox.oAC.resultTypeList = false;

    // Define an event handler to populate a hidden form field
    // when an item gets selected
    var myHiddenField = document.getElementById("apMacHidden");
    var itemHanlder = function(sType, aArgs) {
        var myAC = aArgs[0]; // reference back to the AC instance
        var elLI = aArgs[1]; // reference to the selected LI element
        var oData = aArgs[2]; // object literal of selected item's result data
        
        // update hidden form field with the selected item's ID
        myHiddenField.value = oData.id;
    	
    	//get HiveAP IP and DNS
    	onChangeHiveAP();
    };
    acComboBox.oAC.itemSelectEvent.subscribe(itemHanlder);
	
	//////// Start: custom event for primary RADIUS server ///////////
	var initHiveApName = "<s:property value='%{dataSource.apHostName}'/>";
	if(initHiveApName.trim() != '') {
		acComboBox.oAC.textboxFocusEvent.subscribe(function(){
				if(acComboBox.oAC.getInputEl().value.trim() == initHiveApName){
					//acComboBox.oAC.getInputEl().focus();
					setTimeout(function() {// For IE
						acComboBox.oAC.sendQuery(initHiveApName);
					}, 0);
				} else {
					//onChangePrimaryHiveAP();
				}
		});
	}
	//////// End: custom event for primary RADIUS server ///////////
}

function onChangeHiveAP(){
	var el = document.getElementById("apMacHidden");
	var serverMac;
	if (el.value == "" || el.value == -1) {
		hm.util.hide("apIpDns");
		return;
	} else {
		serverMac = el.value;
	}
    var callback =
    {
		success: settingIPCfginfo
    };
	var sUrl = '<s:url action="activeDirectoryOrLdap" includeParams='none' />' + '?operation=onChangeHiveAP&apServer=' + serverMac + '&ignore=' + new Date().getTime();
    //AJAX GET
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
}

function settingIPCfginfo(o){
	
	hm.util.show("apIpDns");
	eval("var details = " + o.responseText);
	
	// AP domain
	var message = document.getElementById("apDomainMessage");
	if (details.resCode == 0 && details.fullDomainName != "") {
		// success
		var msg = '<s:text name="info.config.getApDomain.success" ><s:param>' + details.fullDomainName + '</s:param></s:text>';
		message.innerHTML = msg;
		hm.util.show("apDomain");
		delaySuccessHideNotes(2, "apDomain");
	} else {
		// failure
		message.innerHTML = "";
		hm.util.hide("apDomain");
	}
	
	// AP ip & DNS
	/*if(details.dhcp){
		document.getElementById("hiveAPIpAddress").value = "";
		document.getElementById("hiveAPNetmask").value = "";
		document.getElementById("hiveAPGateway").value = "";
	}else{*/
		document.getElementById("hiveAPIpAddress").value = details.ipAddress;
		document.getElementById("hiveAPNetmask").value = details.netmask;
		document.getElementById("hiveAPGateway").value = details.gateway;
	//}
	document.getElementById("hiveAPDNSServer").value = details.dnsServer;
	
	if(details.dhcp || details.dnsServer == ''){
		hm.util.show("apNoStaticIpDnsNote");
	}else{
		hm.util.hide("apNoStaticIpDnsNote");
	}
}

function pushConfigToAp() {
	// check Ap static ip and dns
	var el = document.getElementById("apMacHidden");
	var serverMac;
	if (el.value == "" || el.value == -1) {
		return;
	} else {
		serverMac = el.value;
	}
	if(!checkApIpAndDns()) {
		return;
	}
    var callback =
    {
		success: pushConfigToApDone
    };
    var apIpAddress = Get("hiveAPIpAddress").value;
    var apIpNetmask = Get("hiveAPNetmask").value;
    var apIpGateway = Get("hiveAPGateway").value;
    var apDnsServer = Get("hiveAPDNSServer").value;
	var sUrl = '<s:url action="activeDirectoryOrLdap" includeParams='none' />' + '?operation=pushConfigToAp&apServer=' + serverMac + '&apIpAddress=' + apIpAddress + '&apIpNetmask=' + apIpNetmask + '&apIpGateway=' + apIpGateway + '&apDnsServer=' + apDnsServer + '&ignore=' + new Date().getTime();
	//AJAX GET
	waitingPanel.show();
    var transaction = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback);
	//TODO
}

function pushConfigToApDone(o) {
	waitingPanel.hide();
	eval("var details = " + o.responseText);
	if (details.resCode == 0) {
		hm.util.hide("apNoStaticIpDnsNote");
	} else {
		hm.util.show("apNoStaticIpDnsNote");
		var tdEl = document.getElementById("apIpDnsEl");
		hm.util.reportFieldError(tdEl, details.msg);
	}
}

--></script>
<div id="content"><s:form action="activeDirectoryOrLdap">
	<s:if test="%{jsonMode == true}">
		<s:hidden name="operation" />
		<s:hidden name="jsonMode" />
		<s:hidden name="parentDomID"/>
		<s:hidden name="contentShowType" />
		<s:hidden name="parentIframeOpenFlg"/>
		<s:hidden name="id" />
	</s:if>
    <s:hidden name="ipAddressId" />
    <s:hidden name="inputIpValue" />
    <s:hidden name="dataSource.optionalStyle"></s:hidden>
    <s:hidden name="dataSource.retrieveSuccess"></s:hidden>
    <s:hidden name="dataSource.testJoinSuccess"></s:hidden>
    <s:hidden name="dataSource.testAuthSuccess"></s:hidden>
    <s:hidden name="dataSource.adminIsShow"></s:hidden>
    <s:hidden name="dataSource.domainUserIsShow"></s:hidden>
    <s:hidden name="dataSource.multiDomainIsShow"></s:hidden>
    <s:hidden name="dataSource.multipleDomainStyle"></s:hidden>
	<s:if test="%{jsonMode == true}">
		<div id="titleDiv" style="margin-bottom:15px;" class="topFixedTitle">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="left">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-hiveap-radius-big.png" includeParams="none"/>"
							width="40" height="40" alt="" class="dblk" />
						</td>
						<td class="dialogPanelTitle">
							<s:if test="%{dataSource.id == null}">
							<s:text name="config.title.activeDirectory"/>
							</s:if>
							<s:else>
							<s:text name="config.title.activeDirectory.edit"/></s:else>
						</td>
					</tr>
				</table>
				</td>
				<td align="right">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onClick="submitAction('cancel<s:property value="lstForward"/>');" title="<s:text name="common.button.cancel"/>"><span><s:text name="common.button.cancel"/></span></a></td>
							<td width="20px">&nbsp;</td>
							<s:if test="%{dataSource.id == null}">
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" onClick="submitAction('create<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:if>
							<s:else>
								<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="saveBtnId" onClick="submitAction('update<s:property value="lstForward"/>');" title="<s:text name="common.button.save"/>"><span><s:text name="common.button.save"/></span></a></td>
							</s:else>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	</s:if>
	<s:if test="%{jsonMode == true}">
		<div style="margin:0 auto; width:100%;">
		<s:if test="%{contentShownInDlg == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="topFixedTitle">
		</s:if>
		<s:else>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
		</s:else>
	</s:if>
	<s:else>
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
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />>
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="update" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_RADIUS_ACTIVE_DIRECTORY%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		</s:else>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td height="5"></td>
		</tr>
		<tr>
			<td>
			<div>
			<s:if test="%{jsonMode == true}">
				<table cellspacing="0" cellpadding="0" border="0" width="800px">
			</s:if>
			<s:else>
				<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="800px">
			</s:else>
				<tr>
					<td>
						<%-- add this password dummy to fix issue with auto complete function --%>
						<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="labelT1" width="165px"><label><s:text
									name="config.radiusOnHiveAp.radiusName" /><font color="red"><s:text name="*"/></font></label></td>
								<td colspan="2"><s:textfield size="48" name="dataSource.name"
									maxlength="%{radiusNameLength}" disabled="%{disabledName}" 
									onkeypress="return hm.util.keyPressPermit(event,'name');" />
									<s:text name="config.radiusOnHiveAp.passRange" /></td>
							</tr>
							<tr>
								<td  class="labelT1"><label><s:text
									name="config.radiusOnHiveAp.description" /></label></td>
								<td colspan="2"><s:textfield size="48" name="dataSource.description"
									maxlength="%{commentLength}" />
									<s:text name="config.ssid.description_range" /></td>	
							</tr>
							<tr>
								<td style="padding-left:5px"><s:radio label="Gender" disabled="%{disabledName}"
									name="radioType" list="#{'directory':'Active Directory'}"
									onclick="showActive();" value="%{radioType}" /></td>
								<td style="padding-left:15px"><s:radio label="Gender" disabled="%{disabledName}"
									name="radioType" list="#{'ldap':'LDAP Server'}"
									onclick="showOpen();" value="%{radioType}" /></td>
								<td style="padding-left:5px"><s:radio label="Gender" disabled="%{disabledName}"
									name="radioType" list="#{'openDirect':'Open Directory'}"
									onclick="showOpenActive();" value="%{radioType}" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding:6px 4px 2px 4px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="sepLine"><img src="<s:url value="/images/spacer.gif"/>" height="1"
							class="dblk" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="3"></td>
				</tr>
				<tr style="display:<s:property value="%{hideActive}"/>" id="hideActive">
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr style="" id="apRadius">
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
									              		<td class="labelT1" width="300px"><s:text name="config.ssid.newRadius.apRadius" /></td>
														<td id="apListTd" >
												   <%-- 	<div style="position:relative;">
															<span style="overflow:hidden;">
																<s:select id="apServer" name="apServer" list="%{apList}" listKey="key" listValue="value"
																cssStyle="width:270px;height:19px" onchange="changeZindex(this.selectedIndex);doGetApDomain();" 
																title="%{haveDnsAndNtp}" />
																<s:textfield id="inputApServer" cssStyle="position:absolute;width:247px;height:13px;z-index:2;left:0px;"
																maxlength="6" onkeyup="changeApList(this.value);" onchange="changeApList(this.value);" title="Input device name to select device" />
															</span>
															</div>  --%>
															<div>
											        		<div style="z-index: 90; width:223px" id="apServerDiv">
												        		<s:textfield id="apServerName" name="dataSource.apHostName"
												                    maxlength="18" size="25" title="%{haveDnsAndNtp}"/>
												                    <a id="apComboBox" tabindex="-1" href="javascript:void(0);" class="acDropdown"></a>
											        			<div id="apContainer"></div>
											        			<s:hidden id="apMacHidden" name="dataSource.apMac"></s:hidden>
											        		</div>
											        		</div>
														</td>
														<td>&nbsp;</td>
													</tr>
													<tr id="apDomain" style="display:none"><td/><td class="noteInfo" colspan="2" id="apDomainMessage"/></tr>
													<tr id="apNoStaticIpDnsNote" style="display:<s:property value="%{noStaticIporDns}"/>""><td style="padding: 5px 15px 5px;"
														class="noteInfo" colspan="3" id="apStaticIpMessage"><s:text name="error.config.hiveAp.have.no.staticip.dns" /></td></tr>
													<tr id="apIpDns" style="display:<s:property value="%{showIpArea}"/>" ">
													    <td style="padding: 5px 10px 5px;" colspan="3">
													        <table cellspacing="0" cellpadding="0" border="0" id="apIpDnsEl">
													        	<tr>
													        		<td>
			 															<table cellspacing="0" cellpadding="0" border="0">
																			<tr id="fe_hiveAPIpAddress" style="display: none">
																				<td/>
																				<td class="noteError" id="textfe_hiveAPIpAddress">To be changed</td>
																			</tr>
																			<tr id="fe_hiveAPNetmask" style="display: none">
																				<td colspan="4"/>
																				<td class="noteError" id="textfe_hiveAPNetmask">To be changed</td>
																			</tr>
																			<tr>
																                <td class="labelT1" width="120px"><s:text name="config.ssid.radiusServer.ipAddress" /><font color="red"><s:text name="*"/></font></td>
																                <td><s:textfield id="hiveAPIpAddress" name="dataSource.staticHiveAPIpAddress"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																                <td width="30px"/>    
																                <td class="labelT1" width="92px"><s:text name="config.ssid.radiusServer.netmask" /><font color="red"><s:text name="*"/></font></td>
																                <td><s:textfield id="hiveAPNetmask" name="dataSource.staticHiveAPNetmask"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																            </tr>
																			<tr id="fe_hiveAPGateway" style="display: none">
																				<td/>
																				<td class="noteError" id="textfe_hiveAPGateway">To be changed</td>
																			</tr>
																			<tr id="fe_hiveAPDNSServer" style="display: none">
																				<td colspan="4"/>
																				<td class="noteError" id="textfe_hiveAPDNSServer">To be changed</td>
																			</tr>
																            <tr>
																                <td class="labelT1"><s:text name="config.ssid.radiusServer.gateway" /><font color="red"><s:text name="*"/></font></td>
																                <td><s:textfield id="hiveAPGateway" name="dataSource.staticHiveAPGateway"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																                <td width="30px"/>    
																                <td class="labelT1"><s:text name="config.ssid.radiusServer.dns" /><font color="red"><s:text name="*"/></font>&nbsp;<a class="marginBtn" 
																					href="javascript:showInfoDialog('<s:text name="info.config.hiveap.for.connection.dns.tip" />')">?</a></td>
																                <td><s:textfield id="hiveAPDNSServer" name="dataSource.dnsServer"
																                    maxlength="18" onkeypress="return hm.util.keyPressPermit(event,'ip');"/></td>
																            </tr>
																        </table>
													        		</td>
													        		<td>
																    	<table>
																    		<tr>
																    			<td>&nbsp;&nbsp;<input style="width:100px" type="button" name="configAp" 
																    				value="<s:text name="config.radiusOnHiveAp.button.apply.ap.config"/>"
																					class="button" onClick="pushConfigToAp();" <s:property value="writeDisabled" />>&nbsp;<a class="marginBtn" 
																					href="javascript:showInfoDialog('<s:text name="info.config.hiveap.for.connection.update.tip" />')">?</a></td>
																    		</tr>
																    	</table>
													        		</td>
													        	</tr>
													        </table>
													    </td>
													</tr>
													<tr id="fe_apIpDnsEl" style="display:none"><td style="padding: 5px 15px 5px;"
														class="noteError" colspan="3" id="textfe_apIpDnsEl">To be changed</td></tr>
												</table>
											</td>													
										</tr>
										<tr>
											<td style="padding:2px 0 5px 5px">
												<fieldset>
												<legend><s:text name="config.radiusOnHiveAp.workName.default" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr style="display:none">
														<s:hidden name="dataSource.defDomain.domain" id="defDomName" />
														<s:hidden name="dataSource.hidAdServer" id="adServer" />
														<s:hidden name="dataSource.hidAdBaseDn" id="adBaseDn" />
													</tr>
													<tr>
														<td class="labelT1" width="147px"><label><s:text name="config.radiusOnHiveAp.workName" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:textfield size="36" name="dataSource.defDomain.fullName" id="defDomFullName" maxlength="64"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" onchange="textOnchange(this);"/>
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
														<td align="right" width="200px">&nbsp;&nbsp;<div id="retrieveBtnId"><input style="width:185px" type="button" name="retrieve" value="<s:text name="config.radiusOnHiveAp.button.retrieveAdInfo"/>"
															class="button" onClick="retrieveAdInfo();" <s:property value="writeDisabled" />></div></td>
													</tr>
													<tr>
														<td class="labelT1" ><s:text name="config.radiusOnHiveAp.adDomain.server" /><font color="red"><s:text name="*"/></font></td>
														<!--<td colspan="2"><div id="adServerLabel" ><s:property value="%{dataSource.hidAdServer}" /></div></td>  -->
														<td>
															<ah:createOrSelect divId="adErrorDisplay" list="availableIpAddress" typeString="IpAddress" 
																selectIdName="myAdSelect" inputValueName="inputAdValue"
																swidth="270px" tlength="64" />
														</td>
													</tr>
													<tr>
														<td class="labelT1" ><s:text name="hm.tool.test.ldap.label.basedn" /></td>
														<td colspan="2"><div id="adBaseDnLabel" ><s:property value="%{dataSource.hidAdBaseDn}" /></div></td>
													</tr>
													<tr>	
									              		<td class="labelT1"><s:text name="config.radiusOnHiveAp.active.computer" /></td>
														<td colspan="2"><s:textfield size="64" name="dataSource.computerOU" maxlength="256"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.computerOu" /></td>
													</tr>
													<tr>	
									              		<td class="labelT1" ><s:text name="config.radiusOnHiveAp.authTls" /></td>
														<td colspan="2" style="padding:2px 0 2px 0"><s:checkbox name="enableAdTls" value="%{enableAdTls}" /></td> 
													</tr>
													<tr id="retrieveRow" style="display:none"><td/><td id="retrieveMessageTd" class="noteInfo" colspan="2"><div style="width:550px" id="retrieveMessage"/></td></tr>
												</table>
												</fieldset>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hideAdmin}"/>" id="hideAdmin">
											<td style="padding:2px 0 5px 5px">
												<fieldset>
												<legend><s:text name="config.radiusOnHiveAp.workName.admin" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="147px"><s:text name="config.radiusOnHiveAp.userName" /></td>
														<td><s:textfield size="24" name="dataSource.userNameA" id="userNameA" maxlength="32"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" onchange="textOnchange(this);"/>
															<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.password" /></td>
														<td colspan="2"><s:password size="48" id="passwordA" name="dataSource.passwordA" maxlength="%{passLength}"
									     					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true"  onchange="textOnchange(this);"/>
									              			<s:textfield id="passwordA_text" name="dataSource.passwordA" size="48" maxlength="%{passLength}" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" onchange="textOnchange(this);" />
															<s:text name="config.radiusOnHiveAp.desRange" /></td>
																															
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.passConf" /></td>
														<td colspan="2"><s:password size="48" id="confirmPasswordA" maxlength="%{passLength}" showPassword="true"
									     					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" value="%{dataSource.passwordA}" onchange="textOnchange(this);" />
									              			<s:textfield id="confirmPasswordA_text" size="48" maxlength="%{passLength}" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" onchange="textOnchange(this);" />
															<s:checkbox id="chkPasswordADisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['passwordA','confirmPasswordA'],['passwordA_text','confirmPasswordA_text']);"
																			disabled="%{writeDisable4Struts}" /><s:text name="admin.user.obscurePassword" /></td>	              		
													</tr>
					                                <%-- <tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.ldap.sasl.wrapping" /></td>
														<td colspan="2"><s:select name="dataSource.ldapSaslWrapping" cssStyle="width: 93px;"
															list="%{ldapSaslWrappings}" listKey="key" listValue="value" /></td>
													</tr> --%>
													<tr>
														<td>&nbsp;</td>
														<td colspan="2">
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																		<s:checkbox id="chkSaveCredentials" name="dataSource.saveCredentials" disabled="%{writeDisable4Struts}" />
																	</td>
																	<td>
																		<s:text name="config.radiusOnHiveAp.save.credential" />&nbsp;<a class="marginBtn" 
																				href="javascript:showInfoDialog('<s:text name="info.config.hiveap.join.domain.save.credentials.tip" />')">?</a>
																	</td>
																	<td align="right" width="320px"><div id="joinBtnId"><input style="width:110px" type="button" name="joinSave" value="<s:text name="config.radiusOnHiveAp.button.join.save"/>"
																		class="button" onClick="doJoin();" <s:property value="writeDisabled" /> /></div></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr id="testJoinRow" style="display:none"><td/><td id="testJoinMessageTd" class="noteInfo" colspan="2"><div id="testJoinMessage"/></td></tr>
												</table>
												</fieldset>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hideBindDn}"/>" id="hideBindDn">
											<td style="padding:2px 0 5px 5px">
												<fieldset>
												<legend><s:text name="config.radiusOnHiveAp.workName.bindDn" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">													
													<tr>
														<td class="labelT1" width="147px"><label><s:text name="config.radiusOnHiveAp.domain.user" /><font color="red"><s:text name="*"/></font></label></td>
														<td colspan="2"><s:textfield size="64" name="dataSource.defDomain.bindDnName" id="defDomBind" maxlength="256"
															onkeypress="return hm.util.keyPressPermit(event,'nameDomainUser');" onchange="textOnchange(this);" />
															<s:text name="config.radiusOnHiveAp.identity.range" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.domain.user.password" /><font color="red"><s:text name="*"/></font></label></td>
														<td colspan="2"><s:password name="dataSource.defDomain.bindDnPass" maxlength="64" id="defDomBindPass" size="48" 
											               	onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" onchange="textOnchange(this);" />
											               	<s:textfield id="defDomBindPass_text" name="dataSource.defDomain.bindDnPass" maxlength="64" cssStyle="display:none" size="48"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" onchange="textOnchange(this);" />
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.passConf" /><font color="red"><s:text name="*"/></font></label></td>
														<td colspan="2"><s:password id="defDomConfirmBind" maxlength="64" size="48"
											           		onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" 
											           		value="%{dataSource.defDomain.bindDnPass}" onchange="textOnchange(this);" />
											                <s:textfield id="defDomConfirmBind_text" maxlength="64" cssStyle="display:none" size="48"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" onchange="textOnchange(this);" />
															<s:checkbox id="defDomChkBindDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
											                (this.checked,['defDomBindPass','defDomConfirmBind'],['defDomBindPass_text','defDomConfirmBind_text']);" />
											                <s:text name="admin.user.obscurePassword" /></td>
													</tr>
													<tr>
														<td >&nbsp;</td>
														<td align="right" width="465px"><div id="authBtnId"><input style="width:125px" type="button" name="testAuth" value="<s:text name="config.radiusOnHiveAp.button.testAuthentication"/>"
															class="button" onClick="doTestAuth();" <s:property value="writeDisabled" />></div></td>
														<td>&nbsp;</td>
													</tr>
													<tr id="testAuthRow" style="display:none"><td/><td id="testAuthMessageTd" class="noteInfo" colspan="2"><div id="testAuthMessage"/></td></tr>
												</table>
												</fieldset>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<!-- Multiple Domain -->
							<tr style="display:<s:property value="%{hideActiveMutipleDomain}"/>" id="hideActiveMutipleDomain">
								<td>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.optional" />','multipleDomain');</script></td>
										</tr>
										<tr>
											<td style="padding-left: 5px;">
												<div id="multipleDomain" style="display:<s:property value="%{dataSource.multipleDomainStyle}"/>">
												<table cellspacing="0" cellpadding="0" border="0"><tr>
													<td style="padding: 2px 0pt 5px 5px;">
													<fieldset><legend><s:text
														name="config.radiusOnHiveAp.ad.multiple.domain" /></legend>
														<table cellspacing="0" cellpadding="0" border="0" class="embedded" id="adTable">
															<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
																<td colspan="4" style="padding-top:2px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																			class="button" onClick="showCreateSection();"></td>
																		<td><input type="button" name="ignore" value="Remove"
																			class="button" <s:property value="writeDisabled" />
																			onClick="submitAction('removeAdDomain');"></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
																<td colspan="4" style="padding-top:2px;">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																			class="button" onClick="submitAction('addAdDomain');"></td>
																		<td><input type="button" name="ignore" value="Remove"
																			class="button" <s:property value="writeDisabled" />
																			onClick="submitAction('removeAdDomainNone');"></td>
																		<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																			class="button" onClick="hideCreateSection();"></td>
																	</tr>
																</table>
																</td>
															</tr>
															<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createSection">
																<td colspan="4" style="padding:4px 5px 4px 5px;">
																<fieldset style="background-color: #edf5ff">
																<legend><s:text name="config.authentication.active.directory.add.domain" /></legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td class="labelT1" width="147px"><label><s:text name="config.radiusOnHiveAp.workName" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="48" name="adFullName" maxlength="64"
																			onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																			<s:text name="config.radiusOnHiveAp.passRange" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.adDomain.server" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="48" name="domServer" maxlength="64"
																			onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																			<s:text name="config.radiusOnHiveAp.passRange" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.domain.user" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:textfield size="64" name="bindDn" maxlength="256"
																			onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																			<s:text name="config.radiusOnHiveAp.identity.range" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.domain.user.password" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:password name="bindPass" maxlength="64" id="bindPass" size="48" 
															               	onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" value="%{bindPass}" />
															               	<s:textfield id="bindPass_text" name="bindPass" maxlength="64" cssStyle="display:none" size="48"
																			onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
																			<s:text name="config.radiusOnHiveAp.passRange" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.passConf" /><font color="red"><s:text name="*"/></font></label></td>
																		<td><s:password id="confirmBind" maxlength="64" size="48"
															           		onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" value="%{bindPass}" />
															                <s:textfield id="confirmBind_text" maxlength="64" cssStyle="display:none" size="48"
																			onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																			<s:text name="config.radiusOnHiveAp.passRange" /></td>
																	</tr>
																	<tr>
																		<td>&nbsp;</td>
																		<td><s:checkbox id="chkBindDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
															                (this.checked,['bindPass','confirmBind'],['bindPass_text','confirmBind_text']);" />
															                <s:text name="admin.user.obscurePassword" /></td>
																	</tr>
																</table>
																</fieldset>
																</td>
															</tr>
															<tr id="headerSection">
																<th align="left" style="padding-left: 0;" width="10"><input
																	type="checkbox" id="checkAll"
																	onClick="toggleCheckAllRules(this);"></th>
																<th align="left" width="190"><s:text
																	name="config.radiusOnHiveAp.workName" /></th>
																<th align="left" width="190"><s:text
																	name="config.radiusOnHiveAp.adDomain.server" /></th>
																<th align="left" width="300"><s:text
																	name="config.radiusOnHiveAp.domain.user" /></th>
															</tr>
															<s:iterator value="%{dataSource.nonDefDomains}" status="status">
																<tr>
																	<td class="listCheck" width="10"><s:checkbox name="ruleIndices"
																		fieldValue="%{#status.index}" /></td>
																	<td class="list" width="190"><s:if test="%{domain != ''}"><s:property value="domain"/></s:if><s:else><s:property value="fullName"/></s:else></td>
																	<td class="list" width="190"><s:textfield size="32" name="domServers" maxlength="64" value="%{server}"
																		onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" /></td>
																	<td class="list" width="250"><s:textfield size="45" name="bindDns" value="%{bindDnName}" maxlength="256"
																		onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" /></td>
																</tr>
															</s:iterator>
															<s:if test="%{gridCount > 0}">
																<s:generator separator="," val="%{' '}" count="%{gridCount}">
																	<s:iterator>
																		<tr>
																			<td class="list" colspan="4">&nbsp;</td>
																		</tr>
																	</s:iterator>
																</s:generator>
															</s:if>		
														</table>
													</fieldset>
													</td>
												</tr>
												</table>
												</div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hideOpen}"/>" id="hideOpen">
					<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td class="labelT1" width="165px"><s:text name="config.radiusOnHiveAp.ldapServer" /><font color="red"><s:text name="*"/></font></td>
									<td>
										<ah:createOrSelect divId="ldapErrorDisplay" list="availableIpAddress" typeString="IpAddress" 
											selectIdName="myLdapSelect" inputValueName="inputLdapValue" 
											swidth="270px" />
									</td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.radiusOnHiveAp.basedn" /><font color="red"><s:text name="*"/></font></td>
									<td><s:textfield size="64" name="dataSource.basedN" maxlength="256"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
										<s:text name="config.radiusOnHiveAp.identity.range" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.radiusOnHiveAp.bindDN.name" /><font color="red"><s:text name="*"/></font></td>
									<td><s:textfield size="64" name="dataSource.bindDnName" maxlength="256"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
										<s:text name="config.radiusOnHiveAp.identity.range" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.radiusOnHiveAp.bindDN.password" /><font color="red"><s:text name="*"/></font></td>
									<td><s:password size="48" id="passwordO" name="dataSource.passwordO" maxlength="%{passLength}"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" />
	           							<s:textfield id="passwordO_text" name="dataSource.passwordO" size="48" maxlength="%{passLength}" cssStyle="display:none"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
	           							<s:text name="config.radiusOnHiveAp.passRange" /></td>
								</tr>
								<tr>
									<td class="labelT1"><s:text name="config.radiusOnHiveAp.passConf" /><font color="red"><s:text name="*"/></font></td>
									<td><s:password size="48" id="confirmPasswordO" maxlength="%{passLength}" showPassword="true"
	           							onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" value="%{dataSource.passwordO}" />
	           							<s:textfield size="48" id="confirmPasswordO_text" maxlength="%{passLength}" cssStyle="display:none"
										onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
	           							<s:text name="config.radiusOnHiveAp.passRange" /></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>
													<s:checkbox id="chkPasswordODisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['passwordO','confirmPasswordO'],['passwordO_text','confirmPasswordO_text']);"
														disabled="%{writeDisable4Struts}" />
												</td>
												<td>
													<s:text name="admin.user.obscurePassword" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
                                <tr>
									<td class="labelT1"><s:text name="config.radiusOnHiveAp.ldapServer.protocol" /></td>
									<td><s:select name="dataSource.ldapProtocol" cssStyle="width: 93px;"
										list="%{protocol}" onchange="changeProtocol(this.options[this.selectedIndex].value);" listKey="key"
										listValue="value" /></td>
								</tr>
								<tr>
							       	<td style="padding-left: 10px;" colspan="2"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.optional" />','optionSet');</script></td>
							    </tr>
							    <tr>
			       					<td style="padding-left: 15px;display: <s:property value="%{dataSource.optionalStyle}"/>" id="optionSet" colspan="2">
			       						<table cellspacing="0" cellpadding="0" border="0">
			       							<tr>
												<td>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="labelT1" width="150px"><s:text name="config.radiusOnHiveAp.ldap.filter.attribute" /></td>
														<td><s:textfield size="24" name="dataSource.filterAttr" maxlength="32"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
													</tr>
													<tr>
														<td class="labelT1">&nbsp;</td>
														<td><s:checkbox name="dataSource.stripFilter"/><label><s:text
															name="config.radiusOnHiveAp.strip.filter" /></label></td>
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.ldap.port" /><font color="red"><s:text name="*"/></font></td>
														<td><s:textfield size="12" name="dataSource.destinationPort" maxlength="5"
															onkeypress="return hm.util.keyPressPermit(event,'ten');" />
															<s:text name="config.radiusOnHiveAp.portRange" /></td>
													</tr>
													<tr>
														<td style="padding:2px 0 2px 5px" colspan="2"><s:checkbox name="enableLdapTls" value="%{enableLdapTls}"
															onclick="authTlsEnable(this.checked);" id="enableLdapTls" />
															<s:text name="config.radiusOnHiveAp.authTls.ldap" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr style="display:<s:property value="%{hideAuth}"/>" id="hideAuth">
												<td>
													<fieldset>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td height="2px" />
															</tr>
															<tr>
																<td class="labelT1" width="118"><s:text name="config.radiusOnHiveAp.caCert" /><font color="red"><s:text name="*"/></font></td>
																<td><s:select name="dataSource.caCertFileO" cssStyle="width: 273px;"
																	list="%{availableCaFile}" value="dataSource.caCertFileO" />
																	<input type="button" value="Import" <s:property value="writeDisabled" />
																	class="button short" onClick="submitAction('newFile4');"></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.radiusOnHiveAp.client" /></td>
																<td><s:select name="dataSource.clientFile" cssStyle="width: 273px;"
																	list="%{availableKeyFile}" value="dataSource.clientFile" />
																	<input type="button" value="Import" <s:property value="writeDisabled" />
																	class="button short" onClick="submitAction('newFile6');"></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.radiusOnHiveAp.private" /></td>
																<td><s:select name="dataSource.keyFileO" cssStyle="width: 273px;"
																	list="%{availableKeyFile}" value="dataSource.keyFileO" />
																	<input type="button" value="Import" <s:property value="writeDisabled" />
																	class="button short" onClick="submitAction('newFile5');"></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.radiusOnHiveAp.keyPass" /></td>
																<td><s:password id="keyPasswordO" name="dataSource.keyPasswordO" cssStyle="width: 273px;" maxlength="%{passLength}" 
							              							onkeypress="return hm.util.keyPressPermit(event,'password');" showPassword="true" />
							              							<s:textfield id="keyPasswordO_text" name="dataSource.keyPasswordO" maxlength="%{passLength}" cssStyle="width: 273px;display:none"
																	onkeypress="return hm.util.keyPressPermit(event,'password');" disabled="true" />
							              							<s:text name="config.radiusOnHiveAp.desRange" /></td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.radiusOnHiveAp.passConf" /></td>
																<td><s:password cssStyle="width: 273px;" id="confPasswordO" maxlength="%{passLength}" showPassword="true"
							              							onkeypress="return hm.util.keyPressPermit(event,'password');" value="%{dataSource.keyPasswordO}" />
							              							<s:textfield id="confPasswordO_text" maxlength="%{passLength}" cssStyle="width: 273px;display:none"
																	onkeypress="return hm.util.keyPressPermit(event,'password');" />
							              							<s:text name="config.radiusOnHiveAp.desRange" /></td>
															</tr>
															<tr>
																<td>&nbsp;</td>
																<td>
																	<s:checkbox id="chkKeyPassDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['keyPasswordO','confPasswordO'],['keyPasswordO_text','confPasswordO_text']);"
																	disabled="%{writeDisable4Struts}" />
																	<s:text name="admin.user.obscurePassword" />
																</td>
															</tr>
															<tr>
																<td class="labelT1"><s:text name="config.radiusOnHiveAp.verify" /></td>
																<td><s:select name="dataSource.verifyServer" cssStyle="width: 100px;"
																	list="%{verify}" value="dataSource.verifyServer" listKey="key"
																	listValue="value" /></td>
															</tr>		
														</table>
													</fieldset>
												</td>
											</tr>
			       						</table>
			       					</td>
			       				</tr>
						</table>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hideOpenDir}"/>" id="hideOpenDir">
					<td>
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td style="padding:2px 0 5px 5px">
												<fieldset>
												<legend><s:text name="config.radiusOnHiveAp.workName.default" /></legend>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="147px"><label><s:text name="config.radiusOnHiveAp.workName" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:textfield size="48" name="dataSource.defOdDomain.domain" id="defOdDomName" maxlength="64"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.realmName" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:textfield size="48" name="dataSource.defOdDomain.fullName" id="defOdDomFullName" maxlength="64"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.bindDN.name" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:textfield size="64" name="dataSource.defOdDomain.bindDnName" id="defOdDomBind" maxlength="256"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.identity.range" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.bindDN.password" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:password name="dataSource.defOdDomain.bindDnPass" maxlength="64" id="defOdDomBindPass" size="48" 
											               	onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" />
											               	<s:textfield id="defOdDomBindPass_text" name="dataSource.defOdDomain.bindDnPass" maxlength="64" cssStyle="display:none" size="48"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
													</tr>
													<tr>
														<td class="labelT1"><label><s:text name="config.radiusOnHiveAp.passConf" /><font color="red"><s:text name="*"/></font></label></td>
														<td><s:password id="defOdDomConfirmBind" maxlength="64" size="48"
											           		onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" value="%{dataSource.defOdDomain.bindDnPass}" />
											                <s:textfield id="defOdDomConfirmBind_text" maxlength="64" cssStyle="display:none" size="48"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.passRange" /></td>
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td><s:checkbox id="defOdDomChkBindDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword
											                (this.checked,['defOdDomBindPass','defOdDomConfirmBind'],['defOdDomBindPass_text','defOdDomConfirmBind_text']);" />
											                <s:text name="admin.user.obscurePassword" /></td>
													</tr>
												</table>
												</fieldset>
											</td>
										</tr>
										<tr>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labelT1" width="165px"><s:text name="config.radiusOnHiveAp.userName" /></td>
														<td><s:textfield size="24" name="dataSource.userNameOd" maxlength="32"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.nameRange0" /></td>	
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.password" /></td>
														<td><s:password size="48" id="passwordOd" name="dataSource.passwordOd" maxlength="%{passLength}"
									     					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" showPassword="true" />
									              			<s:textfield id="passwordOd_text" name="dataSource.passwordOd" size="48" maxlength="%{passLength}" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" disabled="true" />
															<s:text name="config.radiusOnHiveAp.desRange" /></td>
																															
													</tr>
													<tr>
														<td class="labelT1"><s:text name="config.radiusOnHiveAp.passConf" /></td>
														<td><s:password size="48" id="confirmPasswordOd" maxlength="%{passLength}" showPassword="true"
									     					onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" value="%{dataSource.passwordOd}" />
									              			<s:textfield id="confirmPasswordOd_text" size="48" maxlength="%{passLength}" cssStyle="display:none"
															onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
															<s:text name="config.radiusOnHiveAp.desRange" /></td>	              		
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td>
															<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																		<s:checkbox id="chkPasswordOdDisplay" name="ignore" value="true" onclick="hm.util.toggleObscurePassword(this.checked,['passwordOd','confirmPasswordOd'],['passwordOd_text','confirmPasswordOd_text']);"
																			disabled="%{writeDisable4Struts}" />
																	</td>
																	<td>
																		<s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>	
									              		<td class="labelT1" width="145"><s:text name="config.radiusOnHiveAp.authTls" /></td>
														<td style="padding:2px 0 2px 0"><s:checkbox name="enableOdTls" value="%{enableOdTls}" /></td> 
													</tr>
												</table>
											</td>            		
										</tr>
										<tr>
									       	<td style="padding-left: 10px;" colspan="2"><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.radiusOnHiveAp.optional" />','optionSetOd');</script></td>
									    </tr>
									    <tr>
					       					<td style="padding-left: 15px;display: <s:property value="%{dataSource.optionalStyle}"/>" id="optionSetOd" colspan="2">
					       						<table cellspacing="0" cellpadding="0" border="0">
					       							<tr>
														<td>
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="150px"><s:text name="config.radiusOnHiveAp.ldap.filter.attribute" /></td>
																<td><s:textfield size="24" name="dataSource.filterAttrOd" maxlength="32"
																	onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																	<s:text name="config.radiusOnHiveAp.nameRange0" /></td>
															</tr>
															<tr>
																<td class="labelT1">&nbsp;</td>
																<td><s:checkbox name="dataSource.stripFilterOd"/><label><s:text
																	name="config.radiusOnHiveAp.strip.filter" /></label></td>
															</tr>
														</table>
														</td>
													</tr>
					       						</table>
					       					</td>
					       				</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>	
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
	</table>
	<s:if test="%{jsonMode == true}">
		</div>
	</s:if>
</s:form></div>
