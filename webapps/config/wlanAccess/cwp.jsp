<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.wlan.Cwp"%>
<%@page import="com.ah.bo.wlan.WalledGardenItem"%>
<tiles:insertDefinition name="tabView" />
<style>
<!-- 
.customize_button {
	margin-top: 0px;
	margin-right: 2px;
	margin-bottom: 0px;
	width: 200px;
/*	height: 22px; */
	font-family: Arial, Helvetica, Verdana sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #003366;
}
#captivePortalWeb input#inputVlanValue {
    padding-left: 4px;
    padding-right: 4px;
    border-left-width: 2px;
    border-right-width: 2px;
}
-->
</style>
<script src="<s:url value="/js/hm.simpleObject.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<s:if test="%{jsonMode == false}">
<script>window.jQuery || document.write('<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"><\/script>')</script>
</s:if>
<script>
var formName = 'captivePortalWeb';
var thisOperation;
var myConfirmDialog;

function initMyConfirmDialog() {
	myConfirmDialog =
     new YAHOO.widget.SimpleDialog("myConfirmDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                text: "<html><body>Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleOK },
                           { text:"Cancel", handler:handleCancel, isDefault:true } ]
              } );
     myConfirmDialog.setHeader("Confirm");
     myConfirmDialog.render(document.body);
     myConfirmDialog.cancelEvent.subscribe(confirmCancelHandler);
}

function confirmCancelHandler(){
	thisOperation = null;
}

var handleOK = function() {
    this.hide();
    
    if(<s:property value='jsonMode'/>) {
    	if(cwpOperation == 'createCWPFromSSID') {
    		saveCwpForSSID();
    	} else if(cwpOperation == 'editCWPInSubdrawer') {
    		editCWP();
    	}
    	
    } else {
    	if(thisOperation != null && thisOperation != undefined)
    		submitAction(thisOperation);
    }
    
};

var handleCancel = function() {
    this.hide();
    thisOperation = null;
    hm.util.reportFieldError(Get(formName + "_dataSource_enabledHttps"), 
    	'<s:text name="error.config.cwp.ppskServer.https"></s:text>');
    showHideContent("advanced","");
};

function onLoadPage() {
	var tabId = <s:property value="%{tabId}"/>;
	
	if (Get(formName + "_dataSource_cwpName").disabled == false) {
		Get(formName + "_dataSource_cwpName").focus();
	}
	
	if(Get("service").value == <%=WalledGardenItem.SERVICE_PROTOCOL%>) {
		Get("protocolNumber").disabled = false;
		Get("port").disabled = false;
	} else {
		Get("protocolNumber").disabled = true;
		Get("port").disabled = true;
	}
	
	adjustFoldingIcon('advanced');
	adjustFoldingIcon('walledGarden');
	
	initMyConfirmDialog();
	
	changeRegistrationType(Get(formName + "_dataSource_registrationType").value);
	
	<s:if test="%{jsonMode == true}">
	 	if(top.isIFrameDialogOpen()) {
	 		top.changeIFrameDialog(960, 800);
	 	}
	</s:if>
}

function submitAction(operation) {
	if (operation == 'exportWebPages' || validate(operation)) {
		Get(formName + "_dataSource_loginDisplayStyle").value = Get("loginPageDiv").style.display;
		Get(formName + "_dataSource_successDisplayStyle").value = Get("successPageDiv").style.display;
		Get(formName + "_dataSource_failureDisplayStyle").value = Get("failurePageDiv").style.display;
		Get(formName + "_dataSource_languageSupportDisplayStyle").value = Get("languageSupportPageDiv").style.display;
		Get(formName + "_dataSource_advancedDisplayStyle").value = Get("advanced").style.display;
		Get(formName + "_dataSource_walledGardenDisplayStyle").value = Get("walledGarden").style.display;
		<s:if test="%{jsonMode == false}">
			if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
				showProcessing();
			}
			document.forms[formName].operation.value = operation;
			document.forms[formName].submit();
		</s:if>
		<s:else>
			var vlanSelectId = 'vlanSelect';
			var WGIpSelectId = 'WGIpSelect';
			//content opened in subdrawer
    		<s:if test="%{contentShownInDlg == false}">
    		 	if (operation == 'newVlanId') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&parentDomID="+vlanSelectId
				 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 400, url);
					return ;
				} else if (operation == 'editVlanId') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&vlanId="+document.forms[formName].vlanId.value
							+ "&parentDomID="+vlanSelectId
		 					+ "&ignore="+new Date().getTime();
					openIFrameDialog(800, 400, url);
					return ;
				} else if(operation == "addKeyFile" 
						|| operation == "editKeyFile") {
					var width = 790, height = 480;
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&parentDomID=keyFileName";
					if(operation == "editKeyFile") {
						url = url + "&certificate=" + Get("keyFileName").value;
						height = 320;
					}
					openIFrameDialog(width, height, url+ "&ignore=" + new Date().getTime());
					return;
				} else if (operation == 'newWGIp') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID="+WGIpSelectId
			 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				} else if (operation == 'editWGIp') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&WGIp="+document.forms[formName].WGIp.value
							+ "&parentDomID="+WGIpSelectId
		 					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				} else if(operation == "newSuccessExternalURL"){
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=successExternalURLId"
			 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				} else if (operation == 'editSuccessExternalURL') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&successExternalURLId="+document.forms[formName].successExternalURLId.value
							+ "&parentDomID=successExternalURLId"
		 					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				} else if(operation == "newFailureExternalURL"){
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=failureExternalURLId"
			 			+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				} else if (operation == 'editFailureExternalURL') {
					var url = "<s:url action='captivePortalWeb' includeParams='none' />?operation="+operation+"&jsonMode=true"
							+ "&failureExternalURLId="+document.forms[formName].failureExternalURLId.value
							+ "&parentDomID=failureExternalURLId"
		 					+ "&ignore="+new Date().getTime();
					openIFrameDialog(950, 450, url);
					return ;
				}
    		 	
			</s:if>
			//content opened in iframe dialog
			<s:else>
				if (operation == 'newVlanId'
					|| operation == 'editVlanId') {
					if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
						showProcessing();
					}
				}
				
				document.forms[formName].operation.value = operation;
				document.forms[formName].submit();
			</s:else>
		</s:else>
		
	}
}

function saveCwpJsonDlg(operation) {
	if (validate(operation)) {
		if (operation == 'create<s:property value="lstForward"/>' || operation == 'update<s:property value="lstForward"/>') {
			showProcessing();
		}
		
		Get(formName + "_dataSource_loginDisplayStyle").value = Get("loginPageDiv").style.display;
		Get(formName + "_dataSource_successDisplayStyle").value = Get("successPageDiv").style.display;
		Get(formName + "_dataSource_failureDisplayStyle").value = Get("failurePageDiv").style.display;
		Get(formName + "_dataSource_languageSupportDisplayStyle").value = Get("languageSupportPageDiv").style.display;
		
		Get(formName + "_dataSource_advancedDisplayStyle").value = Get("advanced").style.display;
		Get(formName + "_dataSource_walledGardenDisplayStyle").value = Get("walledGarden").style.display;
		
		var url = "";
		if (operation == 'create') {
			url = "<s:url action='captivePortalWeb' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		} else if (operation == 'update') {
			url = "<s:url action='captivePortalWeb' includeParams='none' />" + "?jsonMode=true" 
					+ "&ignore=" + new Date().getTime(); 
		}
		document.forms[formName].operation.value = operation;
		YAHOO.util.Connect.setForm(document.forms["captivePortalWeb"]);
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : successSaveCwpJsonDlg, timeout: 60000}, null);
	}
}

var successSaveCwpJsonDlg = function(o) {
	try {
		eval("var details = " + o.responseText);
		
		if (details.ok == true) {
			var parentSelectDom = parent.document.getElementById(details.parentDomID);
			
			if(parentSelectDom != null) {
				if(details.id != null && details.id != ''){
					hm.util.insertSelectValue(details.id, details.name, parentSelectDom, false, true);
					parent.cwpProfileChanged();
				}
				
				parentSelectDom.focus();
			}
			parent.closeIFrameDialog();
		}else{
			hm.util.displayJsonErrorNote(details.msg);
			return;
		}
		
	}catch(e){
		// do nothing now
	}
}

function validate(operation) {
	if (operation == 'cancel' + '<s:property value="lstForward"/>') {
	 	Get(formName + "_dataSource_registrationPeriod").value=0;
	  	Get(formName + "_dataSource_leaseTime").value=0;
	  	Get(formName + "_dataSource_numberField").value=0;
	    Get(formName + "_dataSource_requestField").value=0;
		return true;
	}
	
	if (operation == 'addBackgroundImage'
			|| operation == 'addHeadImage'
			|| operation == 'addFootImage'
			|| operation == 'addUserPolicy'
			|| operation == "newVlanId"
			|| operation == "newWGIp"
			|| operation == "openCustomizePage"
			|| operation == 'newSuccessExternalURL'
			|| operation == 'newFailureExternalURL') {
			
	 	return true;
	}

	if(operation == 'editWGIp') {
    	var value = hm.util.validateListSelection("WGIpSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].WGIp.value = value;
		}
		
		return true;
    }
    
	if (operation == 'addKeyFile') {
		if (!validateRegistrationPeriod()) {
			return false;
		}
		if(Get(formName + "_dataSource_serverType2").checked==true) {
			if (!validateLeaseTime()) {
			 	return false;
			}
		}
		
		if (!validateNumberField()) {
			return false;
		}
		if (!validateRequestField()) {
			return false;
		}
		
		return true;
	}
	
	 if(operation == 'editKeyFile') {
    	var value = hm.util.validateListSelection("keyFileName");

    	if(value < 0){
			return false;
		}else{
			document.forms[formName].keyFileName.value = value;
		}
		
		return true;
    }
    
    if(operation == 'editVlanId') {
    	var value = hm.util.validateListSelection("vlanSelect");
		if(value < 0){
			return false
		}else{
			document.forms[formName].vlanId.value = value;
		}
		
		return true;
    }
    
    if(operation == 'addWGItem') {
    	// server ip
    	var ipNames = Get("WGIpSelect");
		var ipValue = document.forms[formName].WGIpValue;
		var showError = Get("showError");
		
		if ("" == ipValue.value) {
		    hm.util.reportFieldError(showError, 
		    	'<s:text name="error.config.network.object.input.direct"><s:param><s:text name="config.cwp.detail.walledGarden.server" /></s:param></s:text>');
		    ipValue.focus();
			return false;
		}
		
		if (!hm.util.hasSelectedOptionSameValue(ipNames, ipValue)) {
			if (!hm.util.validateIpAddress(ipValue.value)) {
				var message = hm.util.validateName(ipValue.value, 
					'<s:text name="config.cwp.detail.walledGarden.server" />');
			   	
			   	if (message != null) {
			    	hm.util.reportFieldError(showError, message);
			       	ipValue.focus();
			       	return false;
			   	}
			}
		
			document.forms[formName].WGIp.value = -1;
		} else {
			document.forms[formName].WGIp.value = ipNames.options[ipNames.selectedIndex].value;
		}
		
		if(Get("service").value 
				== <%=WalledGardenItem.SERVICE_PROTOCOL%>) {
			// protocol number
			var element = Get("protocolNumber");
			
		  	if (element.value.trim().length == 0) {
	            hm.util.reportFieldError(showError, 
	            	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.walledGarden.protocol" /></s:param></s:text>');
	            element.focus();
	            return false;
	      	}
		    
		    var message = hm.util.validateIntegerRange(element.value, '<s:text name="config.cwp.detail.walledGarden.protocol" />',
		                                                       <s:property value="%{walledGardenProtocolRange.min()}" />,
		                                                       <s:property value="%{walledGardenProtocolRange.max()}" />);
		                                                     
		    if (message != null) {
		        hm.util.reportFieldError(showError, message);
		       	element.focus();
		        return false;
		    }
		    
		    // port
			element = Get("port");
			
		  	if (element.value.trim().length == 0) {
	            hm.util.reportFieldError(showError, 
	            	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.walledGarden.port" /></s:param></s:text>');
	            element.focus();
	            return false;
	      	}
		    
		    message = hm.util.validateIntegerRange(element.value, '<s:text name="config.cwp.detail.walledGarden.port" />',
		                                                       <s:property value="%{walledGardenPortRange.min()}" />,
		                                                       <s:property value="%{walledGardenPortRange.max()}" />);
		    if (message != null) {
		        hm.util.reportFieldError(showError, message);
		       	element.focus();
		        return false;
		    }
		}
	   	    
		return true;
    }

	if (operation == 'removeWGItems' || operation == 'removeWGItemsNone') {
		var cbs = document.getElementsByName('itemIndices');
		var showError = Get("showError");
		
		if (cbs.length == 0) {
			hm.util.reportFieldError(showError, '<s:text name="info.emptyList"></s:text>');
			return false;
		}
		
		if (!hm.util.hasCheckedBoxes(cbs)) {
            hm.util.reportFieldError(showError, 
            	'<s:text name="error.pleaseSelectItem"><s:param><s:text name="config.cwp.detail.walledGarden.rule" /></s:param></s:text>');  
			return false;
		}
		
		return true;
	} 
	
	if(operation == 'editSuccessExternalURL') {
    	var value = hm.util.validateListSelection("successExternalURLId");

    	if(value < 0){
			return false;
		}
		return true;
    }
	 
	if(operation == 'editFailureExternalURL') {
    	var value = hm.util.validateListSelection("failureExternalURLId");

    	if(value < 0){
			return false;
		}
		return true;
    }
	
	if (!validateCwpName()) {
		return false;
	}

	if (!validateRegistrationPeriod()) {
		return false;
	}
	
	if(Get(formName + "_dataSource_registrationType").value 
			== <%=Cwp.REGISTRATION_TYPE_EXTERNAL%>) { // external cwp
		if (!validateVlan()) {
			return false;
		}
		
			if(!validateLoginURL()) {
				return false;
			}
			
			if(!validateSecret()) {
				return false;
			}
		
	} else if(Get(formName + "_dataSource_registrationType").value 
			== <%=Cwp.REGISTRATION_TYPE_PPSK%>) {// PPSK Server
			if(!validateHTTPS(operation)) {
				return false;
			}
			if (!validateVlan()) {
				return false;
			}
	} else { // internal cwp
		if(Get(formName + "_dataSource_serverType2").checked==true) {
			if (!validateLeaseTime()) {
				return false;
			}
		}
		
		/*
		 * check login page settings
		 */
		var radios = document.getElementsByName("webPageRadioType");

		if(radios[0].checked) { // customize
			//validate if Force user to accept the network use policy selected
			var element = Get(formName + "_dataSource_enableUsePolicy");
			if(element && element.checked){
				 if(Get("loginPageDiv").style.display="none"){
					 alternateFoldingContent("loginPageDiv");
				 }
				 if(Get("advanced").style.display="none"){
					 alternateFoldingContent("advanced");
				 }
				 hm.util.reportFieldError(element, 
	            	'<s:text name="config.cwp.check.usepolicy.warn" />');
				 element.focus();
	             return false;
			}
		} else { // import
			if (!validateNumberField()) {
				return false;
			}
			if (!validateRequestField()) {
				return false;
			}
			
			if (!validateLoginFile()) {
				return false;
			}
		}
		
		if(!Get(formName + "_dataSource_serverType2").checked) {
			if (!validateVlan()) {
				return false;
			}
		}
	}
	
	if(!validateSuccessFile()) {
		return false;
	}
	
	if(!validateSuccessExternalURL()) {
		return false;
	}
	
	if(!validateFailureFile()) {
		return false;
	}
	
	if(!validateFailureRedirection()) {
		return false;
	}
	
	if(!validateFailureExternalURL()) {
		return false;
	}
	
	if(Get(formName + "_dataSource_enabledPopup").checked) {
		if(!validateSessionAlert()) {
			return false;
		}
	}
	
	if (!Get(formName + "_dataSource_useDefaultNetwork").checked) {
		if (!checkIdAddress()) {
			return false;
		}
	}
	
	if (Get(formName + "_dataSource_enabledHttps").checked) {
		if (!validateKeyFile()) {
			return false;
		}
	}
	
	if(!validateRedirectDelay()) {
		return false;
	}
	
	if(!validateBlockRedirect()) {
		return false;
	}
	return true;
}

function validateHTTPS(operation) {
	var checkHTTPS = Get(formName + "_dataSource_enabledHttps");
	
	if (!checkHTTPS.checked) {
		if(thisOperation != null && thisOperation != undefined) { 
			return true;
		} else {
			thisOperation = operation;
		}
			
			
		myConfirmDialog.cfg.setProperty('text', "<html><body>" + '<s:text name="error.config.cwp.ppskServer.https.warn"></s:text>'
        	+ "<br><br>" + '<s:text name="error.config.cwp.ppskServer.https.warn.click"></s:text>' + "</body></html>");
		myConfirmDialog.show();
		return false;
	}
	
	return true;
}

function validateLoginFile() {
	var directory = Get("directoryName");

	 if (hm.util.trim(directory.value) == 'None available') {
          hm.util.reportFieldError(directory, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.directoryName" /></s:param></s:text>');
          showHideContent("loginPageDiv","");
          directory.focus();
          return false;
      }
    
    var webPage = Get("webPageName");

	if (hm.util.trim(webPage.value) == 'None available') {
         hm.util.reportFieldError(webPage, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.webPageName" /></s:param></s:text>');
         showHideContent("loginPageDiv","");
         webPage.focus();
         return false;
      }
      
     return true;
}

function validateSuccessFile() {
	
	if(!Get(formName + "_dataSource_showSuccessPage").checked) {
		return true;
	}
	
	var radios = document.getElementsByName("successPageRadioType");
	
	if(!radios[1].checked) {
		return true;
	}
	
	var directory = Get("successDirectoryName");
	
	 if (hm.util.trim(directory.value) == 'None available') {
          hm.util.reportFieldError(directory, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.directoryName" /></s:param></s:text>');
          showHideContent("successPageDiv","");
          directory.focus();
          return false;
      }
    
	var resultPage = Get("resultPageName");
	
	if (hm.util.trim(resultPage.value) == 'None available') {
        hm.util.reportFieldError(resultPage, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.resultPageName" /></s:param></s:text>');
        showHideContent("successPageDiv","");
        resultPage.focus();
        return false;
    }
    
    if(Get(formName + "_dataSource_registrationType").value 
			!= <%=Cwp.REGISTRATION_TYPE_EXTERNAL%>) { 
		radios = document.getElementsByName("webPageRadioType");
	
		if(radios[1].checked) {
			var loginDirectory = Get("directoryName");
			
			if(loginDirectory.value != directory.value) {
				hm.util.reportFieldError(directory, '<s:text name="error.config.cwp.directory.notSame.1" />');
	            showHideContent("successPageDiv","");
          		directory.focus();
	            return false;
			}
		}	
	}
    
	return true;
}

function validateFailureFile() {
	
	if(!Get(formName + "_dataSource_showFailurePage").checked) {
		return true;
	}
	
	var radios = document.getElementsByName("failurePageRadioType");
	
	if(!radios[1].checked) {
		return true;
	}
	
	var directory = Get("failureDirectoryName");
	
	 if (hm.util.trim(directory.value) == 'None available') {
          hm.util.reportFieldError(directory, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.directoryName" /></s:param></s:text>');
          showHideContent("failurePageDiv","");
          directory.focus();
          return false;
      }
    
	var resultPage = Get("failurePageName");
	
	if (hm.util.trim(resultPage.value) == 'None available') {
        hm.util.reportFieldError(resultPage, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.failurePageName" /></s:param></s:text>');
        showHideContent("failurePageDiv","");
        resultPage.focus();
        return false;
    }
    
    if(Get(formName + "_dataSource_registrationType").value 
			!= <%=Cwp.REGISTRATION_TYPE_EXTERNAL%>) {
		radios = document.getElementsByName("webPageRadioType");
	
		if(radios[1].checked) {
			var loginDirectory = Get("directoryName");
			
			if(loginDirectory.value != directory.value) {
				hm.util.reportFieldError(directory, '<s:text name="error.config.cwp.directory.notSame.2" />');
	            showHideContent("failurePageDiv","");
                directory.focus();
	            return false;
			}
		}
		 
	}
    
	if(Get(formName + "_dataSource_showSuccessPage").checked) {
		var radios = document.getElementsByName("successPageRadioType");
	
		if(radios[1].checked) {
			var successDirectory = Get("successDirectoryName");
			
			if(successDirectory.value != directory.value) {
				hm.util.reportFieldError(directory, '<s:text name="error.config.cwp.directory.notSame.3" />');
	            showHideContent("failurePageDiv","");
                directory.focus();
	            return false;
			}
		}
	}
	
	return true;
}

function validateKeyFile() {
	var keyFileNameElement = Get("keyFileName");
	
	if (keyFileNameElement.value == -1) {
        hm.util.reportFieldError(keyFileNameElement, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.keyFileName" /></s:param></s:text>');
        showHideContent("advanced","");
        keyFileNameElement.focus();
        return false;
	}
	
	return true;
}

function validateCwpName() {
    var inputElement = Get(formName + "_dataSource_cwpName");
    var message = hm.util.validateDirectoryName(inputElement.value, '<s:text name="config.cwp.detail.cwpName" />');
	
	if (message != null) {
	    hm.util.reportFieldError(inputElement, message);
	    inputElement.focus();
	    return false;
	}
    return true;
}

function validateRegistrationPeriod() {
      var inputElement = Get(formName + "_dataSource_registrationPeriod");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.registrationPeriod" /></s:param></s:text>');
            showHideContent("advanced","");
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.cwp.detail.registrationPeriod" />',
                                                       <s:property value="%{registrationPeriodRange.min()}" />,
                                                       <s:property value="%{registrationPeriodRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
            showHideContent("advanced","");
           	inputElement.focus();
            return false;
      }
      return true;
}

 function validateLeaseTime() {
       var inputElement = Get(formName + "_dataSource_leaseTime");
       if (inputElement.value.length == 0) {
             hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.leaseTime" /></s:param></s:text>');
             showHideContent("advanced","");
             inputElement.focus();
             return false;
       }
       var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.cwp.detail.leaseTime" />',
                                                        <s:property value="%{leaseTimeRange.min()}" />,
                                                        <s:property value="%{leaseTimeRange.max()}" />);
       if (message != null) {
             hm.util.reportFieldError(inputElement, message);
             showHideContent("advanced","");
             inputElement.focus();
             return false;
       }
       return true;
 }

function validateNumberField() {
      var inputElement = Get(formName + "_dataSource_numberField");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.numberField" /></s:param></s:text>');
            showHideContent("loginPageDiv","");
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.cwp.detail.numberField" />',
                                                       <s:property value="%{numberFieldRange.min()}" />,
                                                       <s:property value="%{numberFieldRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	showHideContent("loginPageDiv","");
           	inputElement.focus();
            return false;
      }
      return true;
}

function validateRequestField() {
      var inputElement = Get(formName + "_dataSource_requestField");
      if (inputElement.value.length == 0) {
            hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.requestField" /></s:param></s:text>');
            showHideContent("loginPageDiv","");
            inputElement.focus();
            return false;
      }
      var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="config.cwp.detail.requestField" />',
                                                       <s:property value="%{requestFieldRange.min()}" />,
                                                       <s:property value="%{requestFieldRange.max()}" />);
      if (message != null) {
            hm.util.reportFieldError(inputElement, message);
           	showHideContent("loginPageDiv","");
           	inputElement.focus();
            return false;
      }
      return true;
}

function show_hideNetwork(checked) {
    var hideNetwork = Get("hideNetwork");
	if (!checked){
		hideNetwork.style.display="block";
	}
	if (checked){
		hideNetwork.style.display="none";
	}
}

function show_hideFile(checked) {
    var hideFile = Get("hideFile");
	if (!checked){
		hideFile.style.display="block";
	}	
	if (checked){
		hideFile.style.display="none";
	}
}

function showServerType(type)
{
  	var div_internal = Get("internal");
  	var div_external = Get("external");
  	
  	if(type == "1") {
  		div_internal.style.display="none";
  		div_external.style.display="";
  		Get("walledGardenTr").style.display="";
  	} else if(type=="2"){
  	  	div_internal.style.display="block";
  	  	div_external.style.display="none";
  	  	Get("walledGardenTr").style.display="none";
  	  	showHideContent("walledGarden","none");
  	} 
  	
}


function show_hideKeyFile(checked) {
	if(checked)
		Get("selectKeyFile").style.display = "";
	else
		Get("selectKeyFile").style.display = "none";
}

function checkIdAddress() {
	<s:if test="%{fullMode}">
	var ipForEth0 = Get(formName + "_dataSource_ipForEth0");
	var maskForEth0 = Get(formName + "_dataSource_maskForEth0");
	
	if (! hm.util.validateIpAddress(ipForEth0.value)) {
		hm.util.reportFieldError(ipForEth0, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.ipForEth0" /></s:param></s:text>');
		showHideContent("advanced","");
		ipForEth0.focus();
		return false;
	}
	
	if (! hm.util.validateMask(maskForEth0.value)) {
		hm.util.reportFieldError(maskForEth0, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.maskForEth0" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForEth0.focus();
		return false;
	}
	
	//validate the limitation of netmask is 24
	if(!validateLimitionOfNetmask(maskForEth0.value)){
		hm.util.reportFieldError(maskForEth0, '<s:text name="error.cwp.networksetting.netmask.limitation"><s:param><s:text name="config.cwp.msg.maskForEth0" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForEth0.focus();
		return false;
	}
	
	var ipForEth1 = Get(formName + "_dataSource_ipForEth1");
	var maskForEth1 = Get(formName + "_dataSource_maskForEth1");
	
	if (! hm.util.validateIpAddress(ipForEth1.value)) {
		hm.util.reportFieldError(ipForEth1, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.ipForEth1" /></s:param></s:text>');
		showHideContent("advanced","");
		ipForEth1.focus();
		return false;
	}
	
	if (! hm.util.validateMask(maskForEth1.value)) {
		hm.util.reportFieldError(maskForEth1, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.maskForEth1" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForEth1.focus();
		return false;
	}
	//validate the limitation of netmask is 24
	if(!validateLimitionOfNetmask(maskForEth1.value)){
		hm.util.reportFieldError(maskForEth1, '<s:text name="error.cwp.networksetting.netmask.limitation"><s:param><s:text name="config.cwp.msg.maskForEth1" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForEth1.focus();
		return false;
	}
	</s:if>
	
	var ipForA = Get(formName + "_dataSource_ipForAMode");
	var ipForBG = Get(formName + "_dataSource_ipForBGMode");
	var maskForA = Get(formName + "_dataSource_maskForAMode");
	var maskForBG = Get(formName + "_dataSource_maskForBGMode");
	
	
	
	if (! hm.util.validateIpAddress(ipForA.value)) {
		hm.util.reportFieldError(ipForA, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.ipForAMode" /></s:param></s:text>');
		showHideContent("advanced","");
		ipForA.focus();
		return false;
	}
	
	if (! hm.util.validateMask(maskForA.value)) {
		hm.util.reportFieldError(maskForA, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.maskForAMode" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForA.focus();
		return false;
	}
	
	//validate the limitation of netmask is 24
	if(!validateLimitionOfNetmask(maskForA.value)){
		hm.util.reportFieldError(maskForA, '<s:text name="error.cwp.networksetting.netmask.limitation"><s:param><s:text name="config.cwp.msg.maskForAMode" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForA.focus();
		return false;
	}
	
	if (! hm.util.validateIpAddress(ipForBG.value)) {
		hm.util.reportFieldError(ipForBG, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.ipForBGMode" /></s:param></s:text>');
		showHideContent("advanced","");
		ipForBG.focus();
		return false;
	}
	
	if (! hm.util.validateMask(maskForBG.value)) {
		hm.util.reportFieldError(maskForBG, '<s:text name="error.formatInvalid"><s:param><s:text name="config.cwp.msg.maskForBGMode" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForBG.focus();
		return false;
	}
	
	//validate the limitation of netmask is 24
	if(!validateLimitionOfNetmask(maskForBG.value)){
		hm.util.reportFieldError(maskForBG, '<s:text name="error.cwp.networksetting.netmask.limitation"><s:param><s:text name="config.cwp.msg.maskForBGMode" /></s:param></s:text>');
		showHideContent("advanced","");
		maskForBG.focus();
		return false;
	}
	
	return true;
}

//validate the limitation of netmask is 24
function validateLimitionOfNetmask(value){
	var tokens = hm.util.trim(value).split(".");
	if( parseInt(tokens[3]) > 0){
		return false;
	}  
	return true;
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td;

	for (var i = 0; i < details.length; i ++) {
		td = Get(details[i].id);
		var value = details[i].v;
		
		if ("pageName" == details[i].id) {
			updateListValues(Get("webPageName"), value, Get("webPageName").value);
			updateListValues(Get("resultPageName"), value, Get("resultPageName").value);
			updateListValues(Get("failurePageName"), value, Get("failurePageName").value);
			
		} else {
			updateListValues(td, value, td.value);
		}
		
		if ("directoryName" == details[i].id) {
			updateListValues(Get("successDirectoryName"), value, Get("successDirectoryName").value);
			updateListValues(Get("failureDirectoryName"), value, Get("failureDirectoryName").value);
		}
		
		if ("backgroundImage" == details[i].id) {
			updateListValues(Get("successBackgroundImage"), value, Get("successBackgroundImage").value);
			updateListValues(Get("failureBackgroundImage"), value, Get("failureBackgroundImage").value);
		}
		
		if ("headImage" == details[i].id) {
			updateListValues(Get("successHeadImage"), value, Get("successHeadImage").value);
			updateListValues(Get("failureHeadImage"), value, Get("failureHeadImage").value);
		}
		
		if ("footImage" == details[i].id) {
			updateListValues(Get("successFootImage"), value, Get("successFootImage").value);
			updateListValues(Get("failureFootImage"), value, Get("failureFootImage").value);
		}
	}
};

function updateListValues(td, value, selValue) {
	td.length=0;
	td.length=value.length;
	for(var j = 0; j < value.length; j ++)
	{
		td.options[j].value=td.options[j].text=value[j];
		if (value[j] == selValue) {
			td.selectedIndex = j;		
		}
	}	
}

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};

function showCwpFiles(name) {
	var url = '<s:url action="captivePortalWeb"><s:param name="operation" value="viewFile"/></s:url>' + "&name="+encodeURIComponent(name)+'&ignore='+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);

}

function changeRegistrationType(value) {
	if(value == <%=Cwp.REGISTRATION_TYPE_EXTERNAL%>) {
		Get("internalCWP").style.display = "none";
		Get("internalCWP2").style.display = "none";
		Get("external").style.display = "";
		Get("authMethod").style.display = "";
			Get("externalCWP").style.display = "";
		Get("ppskServerType").style.display = "none";
		
		if(Get("passwordEncrypt").value == <%= Cwp.PASSWORD_ENCRYPTION_SHARED%>) {
			Get(formName + "_dataSource_authMethod").value = <%= Cwp.AUTH_METHOD_CHAP%>;
			Get(formName + "_dataSource_authMethod").disabled = true;
		} else {
			Get(formName + "_dataSource_authMethod").disabled = false;
		}
		
		Get(formName + "_dataSource_useLoginAsFailure").checked = false;
		useLoginAsFailure(false);
		Get("trLoginAsFailure").style.display = "none";
		
		Get("walledGardenTr").style.display = "";
	} else if(value == <%=Cwp.REGISTRATION_TYPE_PPSK%>) {
		Get("internalCWP").style.display = "";
		Get("ppskServerType").style.display = "";
		Get("externalCWP").style.display = "none";
		Get("internalCWP2").style.display = "none";
		var serverType = document.getElementsByName("dataSource.serverType");
		serverType[0].checked = true;
		showServerType(1);
		
		Get("authMethod").style.display = "none";
		
		/*var https = Get(formName + "_dataSource_enabledHttps");
		
		if(!https.checked) {
			https.checked = true;
			show_hideKeyFile(true);
		}*/
		
		Get(formName + "_dataSource_useLoginAsFailure").checked = false;
		useLoginAsFailure(false);
		Get("trLoginAsFailure").style.display = "none";
	} else {
		Get("internalCWP").style.display = "block";
		Get("internalCWP2").style.display = "block";
		Get("externalCWP").style.display = "none";
		Get("ppskServerType").style.display = "none";
		
		Get(formName + "_dataSource_authMethod").disabled = false;
		
		var serverRadios = document.getElementsByName("dataSource.serverType");
		
		if(serverRadios[0].checked) {
			Get("walledGardenTr").style.display = "";
			Get("external").style.display = "";
		} else {
			Get("external").style.display = "none";
			Get("walledGardenTr").style.display = "none";
		}
		
		/* Get(formName + "_dataSource_useLoginAsFailure").checked = true;
		useLoginAsFailure(true); */
		Get("trLoginAsFailure").style.display = "";
	}
	
	var radios = document.getElementsByName("webPageRadioType");
	
	if(value == <%=Cwp.REGISTRATION_TYPE_REGISTERED%>) {
		Get("authMethod").style.display = "none";
		
		if(radios[1].checked) {
			Get("regFields").style.display = "";
		}
	} 
	
	if(value == <%=Cwp.REGISTRATION_TYPE_AUTHENTICATED%>) {
		Get("authMethod").style.display = "";
		
		if(radios[1].checked) {
			Get("regFields").style.display = "none";
		}
	} 

	if(value == <%=Cwp.REGISTRATION_TYPE_BOTH%>) {
		Get("authMethod").style.display = "";
		
		if(radios[1].checked) {
			Get("regFields").style.display = "";
		}
	} 
	
	if(value == <%=Cwp.REGISTRATION_TYPE_EULA%>) {
		Get("authMethod").style.display = "none";
		
		if(radios[1].checked) {
			Get("regFields").style.display = "none";
		}
	}
	
	/* control failure page settings */
	if(value == <%=Cwp.REGISTRATION_TYPE_EULA%>) {
		Get("failureTR1").style.display = "none";
		Get("failureTR2").style.display = "none";
		Get("failureTR3").style.display = "none";
		Get("failureTR4").style.display = "none";
	} else {
		Get("failureTR1").style.display = "";
		Get("failureTR2").style.display = "";
		Get("failureTR3").style.display = "";
		Get("failureTR4").style.display = "";
	}
	
	//Control the show of Force user to accept the network use policy
	if(value == <%=Cwp.REGISTRATION_TYPE_REGISTERED%> 
			|| value == <%=Cwp.REGISTRATION_TYPE_EULA%>
			|| value == <%=Cwp.REGISTRATION_TYPE_EXTERNAL%>){
		Get(formName + "_dataSource_enableUsePolicy").checked = false;
		Get("enableUsePolicyTr").style.display = "none";
	}else{
		Get("enableUsePolicyTr").style.display = "";
	}
}

function defaultSelect(checked) {
	Get("customizePage").style.display = "none";
	Get("importPage").style.display = "none";
	
	var registrationType = Get(formName + "_dataSource_registrationType").value;
}

function customizeSelect(checked) {
	if(checked) {
		Get("customizePage").style.display = "";
		Get("importPage").style.display = "none";
	}
}

function importSelect(checked) {
	if(checked) {
		Get("customizePage").style.display = "none";
		Get("importPage").style.display = "";
		
		var registrationType = Get(formName + "_dataSource_registrationType").value;
		
		if(registrationType == <%=Cwp.REGISTRATION_TYPE_REGISTERED%>
			 || registrationType == <%=Cwp.REGISTRATION_TYPE_BOTH%>) {
			Get("regFields").style.display = "";
		} else {
			Get("regFields").style.display = "none";
		}
	}
}

function validateVlan() {

	if(!Get(formName + "_dataSource_overrideVlan").checked) {
		return true;
	}
	
	var vlanNames = Get("vlanSelect");
	var vlanValue = document.forms[formName].inputVlanValue;
	var showError = Get("errorDisplay");
	
	if ("" == vlanValue.value) {
	    hm.util.reportFieldError(showError, 
	    	'<s:text name="error.config.network.object.input.direct"><s:param>VLAN ID</s:param></s:text>');
	    showHideContent("advanced","");
	    vlanValue.focus();
		return false;
	}
	
	if (!hm.util.hasSelectedOptionSameValue(vlanNames, vlanValue)) {
		var message = hm.util.validateIntegerRange(vlanValue.value, 'VLAN ID', 1, 4094);
		
		if (message != null) {
            hm.util.reportFieldError(showError, message);
            showHideContent("advanced","");
            vlanValue.focus();
            return false;
        }
		document.forms[formName].vlanId.value = -1;
    } else {
    	document.forms[formName].vlanId.value = vlanNames.options[vlanNames.selectedIndex].value;
    }
    
    return true;
}

function validateLoginURL() {
	var e = Get("loginURL");
	var value = e.value;
	
	if(value.trim().length == 0) {
		 hm.util.reportFieldError(e, 
	    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.externalCWP.loginURL" /></s:param></s:text>');
	    showHideContent("loginPageDiv","");
	    e.focus();
		return false;
	}
	
	if ( !(value.indexOf('http') == 0
		|| value.indexOf('https') == 0)
		|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
    	hm.util.reportFieldError(e, 
	    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.detail.externalCWP.loginURL" /></s:param></s:text>');
	    showHideContent("loginPageDiv","");
	    e.focus();
	    e.select();
		return false;
	
    } 
  
  return true;
     
}

function validateSecret() {
	if(!<s:property value="commonStyle"/>) {
		return true;
	}
	
	if(Get("passwordEncrypt").value 
		!= <%= Cwp.PASSWORD_ENCRYPTION_SHARED%>) {
		return true;
	}
	
	var element;
	var confirmElement;
	
	if (Get("chkToggleDisplay").checked)
	{
		element = Get("sharedSecret");
		confirmElement = Get("confirmSecret");
	}
	else
	{
		element = Get("sharedSecret_text");
		confirmElement = Get("confirmSecret_text");
	}
	
	var value = element.value;
	
	if(value.trim().length == 0) {
		hm.util.reportFieldError(element, 
	    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.externalCWP.sharedSecret" /></s:param></s:text>');
	    element.focus();
	    showHideContent("loginPageDiv","");
		return false;
	}
	
	var confirmValue = confirmElement.value;
	
	if(confirmValue.trim().length == 0) {
		 hm.util.reportFieldError(confirmElement, 
	    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.externalCWP.confirmSecret" /></s:param></s:text>');
	    confirmElement.focus();
	    showHideContent("loginPageDiv","");
		return false;
	}
	
	if(value != confirmValue) {
		hm.util.reportFieldError(element, 
	    	'<s:text name="error.config.cwp.password.notMatch">' +
	    		'<s:param><s:text name="config.cwp.detail.externalCWP.sharedSecret" /></s:param>' +
	    		'<s:param><s:text name="config.cwp.detail.externalCWP.confirmSecret" /></s:param>' +
	    		'</s:text>');
	    confirmElement.focus();
	    confirmElement.select();
	    showHideContent("loginPageDiv","");
		return false;
	}
	
	return true;
}

function validateSessionAlert() {
	var element = Get("sessionAlert");
	var value = element.value;
	
	if(value.trim().length == 0){
		hm.util.reportFieldError(element, 
	    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.detail.externalCWP.sessionAlert" /></s:param></s:text>');
	    showHideContent("advanced","");
	    element.focus();
		return false;
	}
	
	var message = hm.util.validateIntegerRange(element.value, 
					'<s:text name="config.cwp.detail.externalCWP.sessionAlert" />', 
					<s:property value="%{sessionAlertRange.min()}" />,
                    <s:property value="%{sessionAlertRange.max()}" />);
		
	if (message != null) {
        hm.util.reportFieldError(element, message);
        showHideContent("advanced","");
        element.focus();
        return false;
    }
	
	return true;
}

function changeService(value) {
	if(value == <%=WalledGardenItem.SERVICE_PROTOCOL%>) { // protocol
		Get("protocolNumber").disabled = false;
		Get("port").disabled = false;
	} else { // all/web
		Get("protocolNumber").disabled = true;
		Get("port").disabled = true;
	}
}

function toggleCheckAllItems(cb) {
	var cbs = document.getElementsByName('itemIndices');
	
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked != cb.checked) {
			cbs[i].checked = cb.checked;
		}
	}
}

function selectPPSKServerAuth(checked) {
}

function selectPPSKServerReg(checked) {
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
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="captivePortalWeb" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedCwpName" />\'</td>');
		</s:else>
	</s:else>
}

function showCreateSection() {
	hm.util.hide('newButton');
	hm.util.show('createButton');
	hm.util.show('createSection');
	var trh = Get('headerSection');
	var trc = Get('createSection');
	var table = trh.parentNode;	
	table.removeChild(trh);
	table.insertBefore(trh, trc);
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

function changeEncryption(value) {
	if(value == <%=Cwp.PASSWORD_ENCRYPTION_SHARED%>) { // shared
		Get("secret").style.display = "";
		Get("secretConfirm").style.display = "";
		Get("obscure").style.display = "";
		
		Get(formName + "_dataSource_authMethod").value = <%= Cwp.AUTH_METHOD_CHAP%>;
		Get(formName + "_dataSource_authMethod").disabled = true;
	} else {
		Get("secret").style.display = "none";
		Get("secretConfirm").style.display = "none";
		Get("obscure").style.display = "none";
		
		Get(formName + "_dataSource_authMethod").disabled = false;
	}
	
}

function changeSessionTimer(checked) {
	if(checked) {
		Get("sessionAlertDiv").style.display = "";
	} else {
		Get("sessionAlertDiv").style.display = "none";
	}
}

function successNoRedirection(checked) {
	Get("successExternalRow").style.display = "none";
}

function successRedirectOriginal(checked) {
	Get("successExternalRow").style.display = "none";
}

function successRedirectExternal(checked) {
	Get("successExternalRow").style.display = "";
}

function failureNoRedirection(checked) {
	Get("failureExternalRow").style.display = "none";
}

function failureRedirectLogin(checked) {
	Get("failureExternalRow").style.display = "none";
}

function failureRedirectExternal(checked) {
	Get("failureExternalRow").style.display = "";
}

function validateSuccessExternalURL() {
	var radios = document.getElementsByName("successRedirection");
	
	if(!radios[2].checked) {
		return true;
	}
	
	<s:if test="%{easyMode}">
		var e = Get("successExternalURL");
		var value = e.value;
		if(value.trim().length == 0) {
			 hm.util.reportFieldError(e, 
		    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
		    showHideContent("successPageDiv","");
	        e.focus();
			return false;
		}
		
		if ( !(value.indexOf('http') == 0
			|| value.indexOf('https') == 0)
			|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
	    	hm.util.reportFieldError(e, 
		    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
		    showHideContent("successPageDiv","");
	        e.focus();
		    e.select();
			return false;
	    } 
	</s:if>
	<s:else>
	    if(Get(formName + "_dataSource_externalURLSuccessType1").checked){
		//if($("#singleSuccessUrlTr").is(":visible")){
			var e = Get("successExternalURL");
			var value = e.value;
			if(value.trim().length == 0) {
				 hm.util.reportFieldError(e, 
			    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
			    showHideContent("successPageDiv","");
		        e.focus();
				return false;
			}
			
			if ( !(value.indexOf('http') == 0
				|| value.indexOf('https') == 0)
				|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
		    	hm.util.reportFieldError(e, 
			    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
			    showHideContent("successPageDiv","");
		        e.focus();
			    e.select();
				return false;
		    } 
		}else{
			var e = Get("successExternalURLId");
			
	    	if(e.value < 0){
	    		hm.util.reportFieldError(e, 
			    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
			    showHideContent("successPageDiv","");
	    		e.focus();
				return false;
			}
		}
	</s:else>
	
	
  
  	return true;
}

function validateFailureExternalURL() {
	var radios = document.getElementsByName("failureRedirection");
	
	if(!radios[2].checked) {
		return true;
	}
	
	<s:if test="%{easyMode}">
		var e = Get("failureExternalURL");
		var value = e.value;
		
		if(value.trim().length == 0) {
			 hm.util.reportFieldError(e, 
		    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
		    showHideContent("failurePageDiv","");
	        e.focus();
			return false;
		}
		
		if ( !(value.indexOf('http') == 0
			|| value.indexOf('https') == 0)
			|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
	    	hm.util.reportFieldError(e, 
		    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
		    showHideContent("failurePageDiv","");
		    e.focus();
		    e.select();
			return false;
		
	    } 
	</s:if>
	<s:else>
		if(Get(formName + "_dataSource_externalURLFailureType1").checked){
		//if($("#singleFailureUrlTr").is(":visible")){
			var e = Get("failureExternalURL");
			var value = e.value;
			
			if(value.trim().length == 0) {
				 hm.util.reportFieldError(e, 
			    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
			    showHideContent("failurePageDiv","");
		        e.focus();
				return false;
			}
			
			if ( !(value.indexOf('http') == 0
				|| value.indexOf('https') == 0)
				|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
		    	hm.util.reportFieldError(e, 
			    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
			    showHideContent("failurePageDiv","");
			    e.focus();
			    e.select();
				return false;
			
		    } 
		}else{
			var e = Get("failureExternalURLId");
			
			if(e.value < 0){
				hm.util.reportFieldError(e, 
		    	'<s:text name="error.requiredField"><s:param><s:text name="config.cwp.success.redirect.external.url" /></s:param></s:text>');
		    	showHideContent("failurePageDiv","");
				e.focus();
				return false;
			}
		}
	</s:else>
  
  	return true;
}

function validateFailureRedirection() {
	var elLoginAsFailure = Get(formName + "_dataSource_useLoginAsFailure");
	
	if(!elLoginAsFailure.checked) {
		return true;
	}
	
	var radios = document.getElementsByName("failureRedirection");
	
	if(!radios[0].checked) {
		hm.util.reportFieldError(radios[0], 
    		'<s:text name="error.config.cwp.failure.redirection"></s:text>');
    	showHideContent("failurePageDiv","");
    	return false;
	}
	
	return true;
}

function showSuccessPage(checked) {
	if(checked) {
		Get("successPageSource").style.display = "";
	} else {
		Get("successPageSource").style.display = "none";
	}
}

function customizeSuccessPage(checked) {
	if(!checked) {
		return ;
	}
	
	Get("customizeSuccessPage").style.display = "";
	Get("importSuccessPage").style.display = "none";
}

function importSuccessPage(checked) {
	if(!checked) {
		return ;
	}
	
	Get("customizeSuccessPage").style.display = "none";
	Get("importSuccessPage").style.display = "";
}

function showFailurePage(checked) {
	if(checked) {
		Get("failurePageSource").style.display = "";
	} else {
		Get("failurePageSource").style.display = "none";
	}
}

function useLoginAsFailure(checked) {
	if(checked) {
		Get("failurePageRadio").style.display = "none";
		Get("failurePageCustom").style.display = "none";
		
		var radios = document.getElementsByName("failureRedirection");
		radios[0].checked = true;
		failureNoRedirection(true);
	} else {
		Get("failurePageRadio").style.display = "";
		Get("failurePageCustom").style.display = "";
	}
}

function customizeFailurePage(checked) {
	if(!checked) {
		return ;
	}
	
	Get("customizeFailurePage").style.display = "";
	Get("importFailurePage").style.display = "none";
}

function importFailurePage(checked) {
	if(!checked) {
		return ;
	}
	
	Get("customizeFailurePage").style.display = "none";
	Get("importFailurePage").style.display = "";
}

function validateRedirectDelay() {
	var inputElement = Get(formName + "_dataSource_successDelay");
	
    if (inputElement.value.length == 0) {
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>Time to delay</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    
    var message = hm.util.validateIntegerRange(inputElement.value, 'Time to delay',
                                                     <s:property value="%{successDelayRange.min()}" />,
                                                     <s:property value="%{successDelayRange.max()}" />);
    if (message != null) {
          hm.util.reportFieldError(inputElement, message);
    	  inputElement.focus();
          return false;
    }
    
    inputElement = Get(formName + "_dataSource_failureDelay");
	
    if (inputElement.value.length == 0) {
    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param>Time to delay</s:param></s:text>');
        inputElement.focus();
        return false;
    }
    
    message = hm.util.validateIntegerRange(inputElement.value, 'Time to delay',
                                                     <s:property value="%{failureDelayRange.min()}" />,
                                                     <s:property value="%{failureDelayRange.max()}" />);
    if (message != null) {
          hm.util.reportFieldError(inputElement, message);
    	  inputElement.focus();
          return false;
    }
    
    return true;
}

function validateBlockRedirect() {
	if(<s:property value="easyMode"/>) {
		return true;
	}
	
	var inputElement = Get(formName + "_dataSource_blockRedirectURL");
	var value = inputElement.value;
	
	if(value.trim().length == 0) {
		return true;
	}
	
	if ( !(value.indexOf('http') == 0
		|| value.indexOf('https') == 0)
		|| value.search(/(\w+):\/\/([\S.]+)(\S*)/) == -1 ){ 
    	hm.util.reportFieldError(inputElement, 
	    	'<s:text name="error.config.cwp.input.invalid"><s:param><s:text name="config.cwp.detail.librarySIP.blockRedirect"/></s:param></s:text>');
	    showHideContent("advanced","");
	    inputElement.focus();
	    inputElement.select();
		return false;
	
    } 
    
    return true;
}

function overrideVlan(checked) {
	if(checked) {
		Get("vlanTD").style.display = "";
	} else {
		Get("vlanTD").style.display = "none";
	}	

}

<!--  function CWP multi language support start -->
function validateMultiLanguage(checkBoxid){
	
	var currentBox=Get(checkBoxid);
	
	var supportsChinese=!Get("supportSimpleChineseID").checked;
	var supportEnglish=!Get("supportEnglishID").checked;
	var supportGerman=!Get("supportGermanID").checked;
	var supportFrench=!Get("supportFrenchID").checked;
	var supportKorean=!Get("supportKoreanID").checked;
	var supportDutch=!Get("supportDutchID").checked;
	var supportSpanish=!Get("supportSpanishID").checked;
	var supportTChinese=!Get("supportTraditionalChineseID").checked;
	var supportItalian=!Get("supportItalianID").checked;


	
	if(supportsChinese && supportEnglish && supportGerman && supportFrench && supportKorean && supportDutch && supportSpanish && supportTChinese && supportItalian){
		//alert("Something Wrong");
		hm.util.reportFieldError(currentBox, '<s:text name="error.cwp.multi.language.checklanguage" />');
		currentBox.checked = true;
		return false;
	}
	
	return true;
		
}

function warnEnglishMsg(checkBoxid)
{
	var currentBox=Get(checkBoxid);
	
	hm.util.reportFieldError(currentBox, "English is the mandatory language");
	currentBox.checked = true;
	return false;
	
	
	
	
}


function updateLanguageList(checkBoxid) {
	
	if(!validateMultiLanguage(checkBoxid)){
		return false;
	}
	
	var combobox=Get("defaultlanguagecombobox");
	var currentDefaultLanguage=combobox.value;
	combobox.options.length = 0;
	//var supportGerman=!Get("supportGermanID").checked;
	var supportsChinese=Get("supportSimpleChineseID").checked;
	var supportEnglish=Get("supportEnglishID").checked;
	var supportGerman=Get("supportGermanID").checked;
	var supportFrench=Get("supportFrenchID").checked;
	var supportKorean=Get("supportKoreanID").checked;
	var supportDutch=Get("supportDutchID").checked;
	var supportSpanish=Get("supportSpanishID").checked;
	var supportTChinese=Get("supportTraditionalChineseID").checked;
	var supportItalian=Get("supportItalianID").checked;
	
	   if(supportDutch){
	        var op6 = document.createElement("OPTION"); 
	        op6.value = 6;  
	        op6.innerHTML ='<s:text name="config.cwp.language.support.dutch" />'; 
	        if(currentDefaultLanguage==op6.value){
	           	op6.selected=true;
	           }
	        combobox.appendChild(op6); 
	   }
   if(supportEnglish){
        var op1 = document.createElement("OPTION"); 
        op1.value = 1;  
        op1.innerHTML ='<s:text name="config.cwp.language.support.english" />'; 
        if(currentDefaultLanguage==op1.value){
        	op1.selected=true;
        }
        combobox.appendChild(op1); 
   }
   if(supportFrench){
       var op4 = document.createElement("OPTION"); 
       op4.value = 4;  
       op4.innerHTML ='<s:text name="config.cwp.language.support.french" />'; 
       if(currentDefaultLanguage==op4.value){
          	op4.selected=true;
          }
       combobox.appendChild(op4); 
  }
   if(supportGerman){
       var op3 = document.createElement("OPTION"); 
       op3.value = 3;  
       op3.innerHTML ='<s:text name="config.cwp.language.support.german" />'; 
       if(currentDefaultLanguage==op3.value){
          	op3.selected=true;
          }
       combobox.appendChild(op3); 
  }
   if(supportItalian){
       var op9 = document.createElement("OPTION"); 
       op9.value = 9;  
       op9.innerHTML ='<s:text name="config.cwp.language.support.italian" />'; 
       if(currentDefaultLanguage==op9.value){
          	op9.selected=true;
          }
       combobox.appendChild(op9); 
  }
   if(supportKorean){
       var op5 = document.createElement("OPTION"); 
       op5.value = 5;  
       op5.innerHTML ='<s:text name="config.cwp.language.support.korean" />'; 
       if(currentDefaultLanguage==op5.value){
          	op5.selected=true;
          }
       combobox.appendChild(op5); 
  }
   if(supportsChinese){
       var op2 = document.createElement("OPTION"); 
       op2.value = 2;  
       op2.innerHTML ='<s:text name="config.cwp.language.support.simpleChinese" />'; 
       if(currentDefaultLanguage==op2.value){
       	op2.selected=true;
       }
       combobox.appendChild(op2); 
   }
   if(supportSpanish){
        var op7 = document.createElement("OPTION"); 
        op7.value = 7;  
        op7.innerHTML ='<s:text name="config.cwp.language.support.spanish" />'; 
        if(currentDefaultLanguage==op7.value){
           	op7.selected=true;
           }
        combobox.appendChild(op7); 
   }
   if(supportTChinese){
        var op8 = document.createElement("OPTION"); 
        op8.value = 8;  
        op8.innerHTML ='<s:text name="config.cwp.language.support.traditionalChinese" />'; 
        if(currentDefaultLanguage==op8.value){
           	op8.selected=true;
           }
        combobox.appendChild(op8); 
   }

   
   

}

function refreshLanguageComboBox(o)
{
	var combobox=document.getElementById();
	eval("var details = " + o.responseText);
	
}


<!--  function CWP multi language support end  -->

function addWGItem() {
	
	if(!validate('addWGItem')) {
		return ;
	}
	
	var url = '<s:url action="captivePortalWeb.action" includeParams="none"></s:url>' + '?ignore='+new Date().getTime();
	document.forms[formName].operation.value = 'addWGItem';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: addItemToWG}, null);

}

var addItemToWG = function (o) {
	eval("var details = " + o.responseText);
	
	if(!details.ok) { // failed
		hm.util.reportFieldError(Get('headerSection'), details.msg);
    } else { // succeeded, add one row
		var table = Get('wgTable');
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "10px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='itemIndices' value='" + details.itemId + "' />";							
    	// id
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "70px";
    	oCell.className = "list";
		oCell.innerHTML = details.itemId;
    	// server
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details.server;
    	// service
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "100px";
    	oCell.className = "list";
    	oCell.innerHTML = details.service;
    	// protocol
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "100px";
    	oCell.className = "list";
		oCell.innerHTML = details.protocol;
    	// port
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "70px";
    	oCell.className = "list";
		oCell.innerHTML = details.port;
		
		// Fixed Bug 30794, add the new Option
		hm.util.insertSelectValue(details.serverId, details.server, Get('WGIpSelect'), false, false);
	}
};

function removeWGItems() {
	if(!validate('removeWGItems')) {
		return ;
	}
	
	var url = '<s:url action="captivePortalWeb.action" includeParams="none"></s:url>' + '?ignore='+new Date().getTime();
	document.forms[formName].operation.value = 'removeWGItems';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success: removeItemsFromWG}, null);
}

var removeItemsFromWG = function (o) {
	eval("var details = " + o.responseText);
	var table = Get('wgTable');
	
	/*
	 * remove existing rows
	 */
	for(var i=table.rows.length - 1; i>=0; i--) {
		table.deleteRow(i);
	}

	/*
	 * add new rows
	 */
	for(var i=0; i<details.length; i++) {
		var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	// checkbox
    	oCell.style.align = "left";
    	oCell.style.width = "10px";
    	oCell.className = "listCheck";
		oCell.innerHTML = "<input type='checkbox' name='itemIndices' value='" + details[i].itemId + "' />";							
    	// id
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "70px";
    	oCell.className = "list";
		oCell.innerHTML = details[i].itemId;
    	// server
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "200px";
    	oCell.className = "list";
    	oCell.innerHTML = details[i].server;
    	// service
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "100px";
    	oCell.className = "list";
    	oCell.innerHTML = details[i].service;
    	// protocol
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "100px";
    	oCell.className = "list";
		oCell.innerHTML = details[i].protocol;
    	// port
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	oCell.style.width = "70px";
    	oCell.className = "list";
		oCell.innerHTML = details[i].port;
	}
};


function successURLTypeChanged(radio){
	if(radio.value == 1){
		$("#singleSuccessUrlTr").show();
		$("#taggedSuccessUrlTr").hide();
	}else{
		$("#taggedSuccessUrlTr").show();
		$("#singleSuccessUrlTr").hide();
	}
}

function failureURLTypeChanged(radio){
	if(radio.value == 1){
		$("#singleFailureUrlTr").show();
		$("#taggedFailureUrlTr").hide();
	}else{
		$("#taggedFailureUrlTr").show();
		$("#singleFailureUrlTr").hide();
	}
}

<s:if test="%{jsonMode == true && contentShowType == 'subdrawer'}">
	var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}
	window.setTimeout("onLoadPage()", 100);
	<s:if test="%{writeDisabled!=''}">
		showHideNetworkPolicySubSaveBT(false);
	</s:if>
	<s:else>
		showHideNetworkPolicySubSaveBT(true);
	</s:else>
</s:if>
</script>
<s:if test="%{easyMode && lastExConfigGuide!=null}">
<div id="content" style="padding-left: 20px">
</s:if>
<s:else>
<div id="content">
</s:else>
<s:form action="captivePortalWeb">
<s:if test="%{jsonMode == true}">
<s:hidden name="id" />
<s:hidden name="operation" />
<s:hidden name="contentShowType" />
<s:hidden name="parentDomID"/>
<s:hidden name="parentIframeOpenFlg"/>
<s:hidden name="jsonMode" />
</s:if>
<s:hidden name="vlanId" />
<s:hidden name="WGIp" />
<s:hidden name="customPage" />
<s:hidden name="registType" />
<s:hidden name="dataSource.advancedDisplayStyle" />
<s:hidden name="dataSource.walledGardenDisplayStyle" />
<s:hidden name="dataSource.loginDisplayStyle" />
<s:hidden name="dataSource.successDisplayStyle" />
<s:hidden name="dataSource.failureDisplayStyle" />
<s:hidden name="dataSource.languageSupportDisplayStyle" />
<s:hidden name="idmSelfReg" />
<s:hidden name="enableRegistrationType" />

		<s:if test="%{jsonMode == true && contentShownInDlg == true}">
			<div id="cwpDlgTitleDiv" style="margin-bottom:15px;" class="topFixedTitle">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="80%">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-cwp-big.png" includeParams="none"/>"
								width="40" height="40" alt="" class="dblk" />
							</td>
							<s:if test="%{dataSource.id == null}">
							<td class="dialogPanelTitle"><s:text name="config.title.cwp"/></td>
							</s:if>
							<s:else>
							<td class="dialogPanelTitle"><s:text name="config.title.cwp.edit"/></td>
							</s:else>
							<td style="padding-left:10px;">
								<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
									<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
										alt="" class="dblk"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" style="float: right; margin-right: 20px;" onclick="parent.closeIFrameDialog();" title="<s:text name="common.button.cancel"/>"><span style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.cancel"/></span></a></td>
							<s:if test="%{dataSource.id == null}">
								<s:if test="%{writeDisabled == 'disabled'}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="cwpDlgSaveBtnId" style="float: right;" onclick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="cwpDlgSaveBtnId" style="float: right;" onclick="saveCwpJsonDlg('create');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="cwpDlgSaveBtnId" style="float: right;" onclick="return false;" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:if>
								<s:else>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" id="cwpDlgSaveBtnId" style="float: right;" onclick="saveCwpJsonDlg('update');" title="<s:text name="common.button.save"/>"><span style="padding-bottom: 2px; padding-top: 2px;"><s:text name="common.button.save"/></span></a></td>
								</s:else>
							</s:else>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</div>
		</s:if>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	
		<s:if test="%{jsonMode == false}">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
					<td><input type="button" name="ignore" value="Export"
						class="button" <s:property value="writeDisabled" />
						onClick="submitAction('exportWebPages');"></td>
				</tr>
			</table>
			</td>
		</tr>
		</s:if>
		
		<tr>
			<td style="padding-top: 5px;">
			<s:if test="%{jsonMode == true}">
				<s:if test="%{contentShownInDlg == true}">
					<table border="0" cellspacing="0" cellpadding="0" width="750px" class="topFixedTitle">
				</s:if>
				<s:else>
					<table border="0" cellspacing="0" cellpadding="0" width="750px">
				</s:else>
			</s:if>
			<s:else>
			<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="750px">
			</s:else>
				<tr>
					<td><tiles:insertDefinition name="notes" /></td>
				</tr>
				<tr>
					<td>
					<%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd" type="password">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="150"><s:text
								name="config.cwp.detail.cwpName" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.cwpName" size="28"
								onkeypress="return hm.util.keyPressPermit(event,'directory');"
								maxlength="%{cwpNameLength}" disabled="%{disabledName}" />&nbsp;<s:text
								name="config.cwp.detail.cwpName.range" /></td>
						</tr>
						<s:if test="%{disabledName}">
							<tr style="display: <s:property value="enableRegistrationType"/>">
								<td></td>
								<td><FONT color="blue"><s:text name="config.cwp.change.note" /></FONT></td>
							</tr>
						</s:if>
						<tr style="display: <s:property value="enableRegistrationType"/>">
							<td class="labelT1" width="150"><s:text
								name="config.cwp.detail.registType" /></td>
							<td><s:select name="dataSource.registrationType"
								list="%{enumRegistrationType}" listKey="key" listValue="value"
								value="dataSource.registrationType" cssStyle="width: 230px;" 
								onchange="changeRegistrationType(this.value);"
								/></td>
						</tr>
						<tr>
							<td class="labelT1" width="150"><s:text
								name="config.cwp.detail.description" /></td>
							<td><s:textfield name="dataSource.comment" size="48"
								maxlength="%{commentLength}" />&nbsp;<s:text
								name="config.cwp.detail.description.range" /></td>
						</tr>

					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td style="padding:0 4px 0 4px">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="sepLine" colspan="3"><img
								src="<s:url value="/images/spacer.gif"/>" height="1"
								class="dblk" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="12"></td>
				</tr>
				<%-- Login Page--%>
				<tr>
       				<td style="padding-left: 10px;">
       					<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.loginPage" />',
       						'loginPageDiv');</script></td>
    			</tr>
    			<tr>
					<td height="6"></td>
				</tr>
				<tr>
					<td style="padding: 0 6px 0 10px">
						<div id="loginPageDiv" style="display: <s:property value="%{dataSource.loginDisplayStyle}"/>">
						<fieldset><div>
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td>
									<div id="internalCWP" style="display:<s:property value="%{showInternalCWP}"/>">
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td>
											<table>
												<tr id="ppskServerType" style="display: <s:property value="%{showPPSKServer}" />;">
													<td style="padding:5px 0 0 10px">
														<s:text	name="config.cwp.ecwp.ppskServer.type" />
													</td>
													<td style="padding:0 0 0 4px">
														<s:radio label="Gender" name="ppskServerRadioType"
														list="#{'authentication':''}" id="ppskServerAuth"
														onclick="selectPPSKServerAuth(this.checked);" value="%{ppskServerRadioType}"/>
													</td>
													<td style="padding:0 0 0 0">
														<s:text name="config.cwp.detail.ppsk.type.auth" />
													</td>
													<s:if test="%{!idmSelfReg}">
													<td style="padding:0px 0 0 4px">
														<s:radio label="Gender" name="ppskServerRadioType"
														list="#{'registration':''}" id="ppskServerReg"
														onclick="selectPPSKServerReg(this.checked);" value="%{ppskServerRadioType}"/>
													</td>
													<td style="padding:0 0 0 0">
														<s:text name="config.cwp.detail.ppsk.type.reg" />
													</td>
													</s:if>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding:5px 0 0 6px">
														<s:radio label="Gender" name="webPageRadioType"
														list="#{'customize':''}" id="radioCustomize"
														onclick="customizeSelect(this.checked);" value="%{webPageRadioType}"/>
													</td>
													<td style="padding:5px 0 0 0">
														<s:text name="config.cwp.detail.customize" />
													</td>
													<td style="padding:5px 0 0 6px">
														<s:radio label="Gender" name="webPageRadioType"
														list="#{'import':''}" id="radioImport"
														onclick="importSelect(this.checked);" value="%{webPageRadioType}"/>
													</td>
													<td style="padding:5px 0 0 0">
														<s:text name="config.cwp.detail.import" />
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td>
											<!-- begin Import web pages -->
											<div style="display:<s:property value="%{hideFile}"/>"	id="importPage">
											<table border="0" cellspacing="0" cellpadding="0" width="100%">
												<tr>
													<td style="padding:5px 5px 5px 10px">
													<fieldset>
													<div>
													<table border="0" cellspacing="0" cellpadding="0" width="100%">
														<tr>
															<td style="padding:5px 0 0 0">
															<table id="regFields" style="display:<s:property value="%{showRegistrated}"/>" border="0" cellspacing="0" cellpadding="0" width="100%">
																<tr>
																	<td width="120px"><s:text
																		name="config.cwp.detail.requestField" /></td>
																	<td width="120px"><s:textfield
																	name="dataSource.requestField" size="6" maxlength="1"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																&nbsp;<s:text name="config.cwp.detail.requestField.range" /></td>
																<td width="100px"><s:text
																	name="config.cwp.detail.numberField" /></td>
																<td width="100px"><s:textfield
																	name="dataSource.numberField" size="6" maxlength="1"
																	onkeypress="return hm.util.keyPressPermit(event,'ten');" />
																&nbsp;<s:text name="config.cwp.detail.numberField.range" /></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
														<td height="4"></td>
													</tr>
													<tr>
														<td>
														<table border="0" cellspacing="0" cellpadding="0" width="100%">
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.directoryName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="directoryName" name="dataSource.directoryName"
																	list="%{availableCwpDirs}" value="dataSource.directoryName"
																	onchange="showCwpFiles(this.options[this.selectedIndex].text);"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addDirectory" value="Create" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Directory', 'newCwpDirectory', 'login');"
																	style="width: 90px;"></td>
															</tr>
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.webPageName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="webPageName" name="dataSource.webPageName"
																	list="%{availableCwpFiles}" value="dataSource.webPageName"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addFileOne" value="Add/Remove" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page', 'newCwpFile', 'login');"
																	style="width: 90px;"></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>
												</div>
												</fieldset>
												</td>
											</tr>
										</table>
										</div>
										<!-- end Import web pages -->
										<!-- begin Customize web pages -->
										<div style="display:<s:property value="%{customizePage}"/>" id="customizePage">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td style="padding:5px 5px 5px 10px">
												<div class="yui-content">
												<div>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td style="padding:8px 0 0 0">
																<input type="button" name="ignore" 
																	value="<s:text name="config.cwp.customize.login.page"/>"
																	class="customize_button" onClick="customizeLoginPage();"
																	<s:property value="writeDisabled" />>
															</td>
														</tr>
													</table>
												</div>
												</div>
												</td>
											</tr>
										</table>
										</div>
										<!-- end Customize web pages -->
										</td>
									</tr>
									<tr>
										<td height="6"></td>
									</tr>
									
								</table>
							</div>
						</td>
					</tr>
					<tr id="authMethod" style="display: <s:property value="%{showAuthenticateMethod}" />;">
						<td style="padding: 10px 0 0 0">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="160px" style="padding:4px 0 4px 10px">
									<s:text	name="config.cwp.detail.authMethod" />
								</td>
								<td>
									<s:select name="dataSource.authMethod"
										list="%{enumAuthMethod}" listKey="key" listValue="value"
										value="dataSource.authMethod" cssStyle="width: 150px;" 
										disabled="%{authMethodDisabled}"/>
								</td>
							</tr>
						</table>	
						</td>
					</tr>
					<tr>
						<td>
							<div id="externalCWP" style="display:<s:property value="%{showExternalCWP}"/>">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td colspan="4">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
												<table>
													<tr>
														<td>
														</td>
														<td>
															<FONT color="blue">
																<s:text
																name="config.cwp.detail.externalCWP.loginURL.notes" />
															</FONT>
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="156px"><s:text
															name="config.cwp.detail.externalCWP.loginURL" /><font color="red">*</font></td>
														<td><s:textfield id="loginURL" cssStyle="width:300px;"
															name="dataSource.loginURL" maxlength="%{loginURLLength}"/>
															<s:text	name="config.cwp.detail.externalCWP.loginURL.range" /></td>
													</tr>
												</table>
												</td>
											</tr>
											<s:if test="%{commonStyle}">
											<tr>
												<td>
												<table>
													<tr>
														<td class="labelT1" width="156px"><s:text
															name="config.cwp.detail.externalCWP.passwordEncrypt" /></td>
														<td><s:select id="passwordEncrypt" name="dataSource.passwordEncryption" 
															list="enumPasswordEncrypt" listKey="key" listValue="value"
															value="dataSource.passwordEncryption" cssStyle="width: 220px;"
															onchange="changeEncryption(this.value);"/></td>
													</tr>
													<tr id="secret" style="display: <s:property value="%{passwordDisplay}" />;">
														<td class="labelT1"><s:text
															name="config.cwp.detail.externalCWP.sharedSecret" /><font color="red">*</font></td>
														<td><s:password id="sharedSecret" cssStyle="width:300px;"
															name="dataSource.sharedSecret" value="%{dataSource.sharedSecret}"
															maxlength="%{sharedSecretLength}" showPassword="true"
															onkeypress="return hm.util.keyPressPermit(event,'password');"/>
															<s:textfield id="sharedSecret_text" cssStyle="width:300px;display:none;"
																name="dataSource.sharedSecret" value="%{dataSource.sharedSecret}"
																disabled="true" maxlength="%{sharedSecretLength}"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
																<s:text
																	name="config.cwp.detail.externalCWP.sharedSecret.range" /></td>
													</tr>
													<tr id="secretConfirm" style="display: <s:property value="%{passwordDisplay}" />;">
														<td class="labelT1"><s:text
															name="config.cwp.detail.externalCWP.confirmSecret" /><font color="red">*</font></td>
														<td><s:password id="confirmSecret" cssStyle="width:300px;" showPassword="true" 
															value="%{dataSource.sharedSecret}" maxlength="%{sharedSecretLength}"
															onkeypress="return hm.util.keyPressPermit(event,'password');"/>
															<s:textfield id="confirmSecret_text" cssStyle="width:300px;display:none;"
																disabled="true" maxlength="%{sharedSecretLength}"
																value="%{dataSource.sharedSecret}"
																onkeypress="return hm.util.keyPressPermit(event,'password');"/>
															</td>
													</tr>
													<tr id="obscure" style="display: <s:property value="%{passwordDisplay}" />;">
														<td></td>
														<td>
															<table>
																<tr>
																	<td>
																		<s:checkbox id="chkToggleDisplay" name="ignore" value="true"
																			disabled="%{writeDisable4Struts}"
																			onclick="hm.util.toggleObscurePassword(this.checked,
																				['sharedSecret','confirmSecret'],
																				['sharedSecret_text','confirmSecret_text']);" />
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
											</s:if>
											<s:if test="%{depaulStyle}">
											<tr>
												<td>
												<table>
													<tr>
														<td style="padding: 0 0 0 6px">
															<s:radio label="Gender" name="reassociateRadioType"
																list="#{'reassociate':''}" 
																value="%{reassociateRadioType}"/>
														</td>
														<td><s:text name="config.cwp.reassociate" /></td>
													</tr>
													<tr>
														<td style="padding: 0 0 0 6px">
															<s:radio label="Gender" name="reassociateRadioType"
																list="#{'direct-access':''}" 
																value="%{reassociateRadioType}"/>
														</td>
														<td><s:text name="config.cwp.direct.access" /></td>
													</tr>
												</table>
												</td>
											</tr>
											</s:if>
											<s:if test="%{nnuStyle}">
											<tr>
												<td>
												<table>
													<tr>
														
													</tr>
												</table>
												</td>
											</tr>
											</s:if>
										</table>
										</td>
									</tr>
									<tr>
										<td height="6"></td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					</table>
					</div></fieldset>
					</div>
					</td>
				</tr>
				<tr>
					<td height="12"></td>
				</tr>
				<%-- Success Page--%>
				<tr>
       				<td style="padding-left: 10px;">
       					<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.successPage" />',
       						'successPageDiv');</script></td>
    			</tr>
    			<tr>
					<td height="6"></td>
				</tr>
				<tr>
					<td style="padding: 0 6px 0 10px">
						<div id="successPageDiv" style="display: <s:property value="%{dataSource.successDisplayStyle}"/>">
						<fieldset><div>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
								<table>
									<tr>
										<td>
											<s:checkbox
											name="dataSource.showSuccessPage"
											value="%{dataSource.showSuccessPage}" 
											onclick="showSuccessPage(this.checked);"/></td>
										<td><s:text name="config.cwp.success.show" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr id="successPageSource" style="display: <s:property value="%{successPageSourceDisplay}"/>;">
								<td>
								<table>
									<tr>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding:0 2px 0 4px">
														<s:radio label="Gender" name="successPageRadioType"
														list="#{'customize':''}"
														onclick="customizeSuccessPage(this.checked);" value="%{successPageRadioType}"/>
													</td>
													<td style="padding:0 2px 0 0">
														<s:text name="config.cwp.detail.customize" />
													</td>
													<td style="padding:0px 2px 0 4px">
														<s:radio label="Gender" name="successPageRadioType"
														list="#{'import':''}"
														onclick="importSuccessPage(this.checked);" value="%{successPageRadioType}"/>
													</td>
													<td style="padding:0 2px 0 0">
														<s:text name="config.cwp.detail.import" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>
										<table>
											<tr>
												<td style="padding:0 0 0 10px">
												<!-- begin customize web pages -->
												<div style="display:<s:property value="%{customizeSuccessPageDisplay}"/>" id="customizeSuccessPage">
												<div>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding:8px 0 0 0">
															<input type="button" name="ignore" 
																value="<s:text name="config.cwp.customize.success.page"/>"
																class="customize_button" onClick="openCustomizePanel('Success');"
																<s:property value="writeDisabled" />>
														</td>
													</tr>
												</table>
												</div>
												</div>
												<!-- end customize web pages -->
												<!-- begin import web pages -->
												<div style="display:<s:property value="%{importSuccessPage}"/>" id="importSuccessPage">
												<fieldset><div>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding:0 0 0 0">
														<table>
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.directoryName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="successDirectoryName" name="successDirectoryName"
																	list="%{availableCwpDirs}" value="dataSource.directoryName"
																	onchange="showCwpFiles(this.options[this.selectedIndex].text);"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addDirectory" value="Create" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Directory', 'newCwpDirectory', 'success');"
																	style="width: 90px;"></td>
															</tr>
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.resultPageName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="resultPageName" name="dataSource.resultPageName"
																	list="%{availableCwpFiles}" value="dataSource.resultPageName"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addFileTwo" value="Add/Remove" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page', 'newCwpFile', 'success');"
																	style="width: 90px;"></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>
												</div></fieldset>
												</div>
												<!-- end import web pages -->
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td style="padding: 10px 0 0 0;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 0" width="140px">
											<s:text name="config.cwp.success.redirect" />
										</td>
										<td style="padding:0 2px 0 4px">
											<s:radio label="Gender" name="successRedirection"
											list="#{'no-redirection':''}" id="rdSuccessNoRedirect"
											onclick="successNoRedirection(this.checked);" value="%{successRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.success.redirect.no" />
										</td>
										<td style="padding:0 2px 0 4px">
											<s:radio label="Gender" name="successRedirection"
											list="#{'original':''}" id="rdSuccessRedirectOriginal"
											onclick="successRedirectOriginal(this.checked);" value="%{successRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.success.redirect.original" />
										</td>
										<td style="padding:0px 2px 0 4px">
											<s:radio label="Gender" name="successRedirection"
											list="#{'external':''}" id="rdSuccessRedirectExternal"
											onclick="successRedirectExternal(this.checked);" value="%{successRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.success.redirect.external" />
										</td>
									</tr>
									<tr id="successExternalRow" style="display: <s:property value="%{successExternalDisplay}"/>">
										<td colspan="10" style="padding: 0 0 0 60px">
										<table>
											<tr>
												<td>
												</td>
												<td>
													<FONT color="blue">
														<s:text
														name="config.cwp.success.redirect.external.url.notes" />
													</FONT>
												</td>
											</tr>
											<tr>
												<s:if test="%{easyMode}">
												<td>
													<s:text name="config.cwp.success.redirect.external.url" />
												</td>
												<td>
													<s:textfield id="successExternalURL" cssStyle="width:200px;"
														name="dataSource.successExternalURL" maxlength="256"/>
													<s:text	name="config.cwp.detail.externalCWP.loginURL.range" />
												</td>
												</s:if>
												<s:else>
												<td style="padding-right:20px;">
													<s:radio onclick="this.blur();" onchange="successURLTypeChanged(this);" label="Gender" name="dataSource.externalURLSuccessType" list="%{externalURLSingle}" listKey="key" listValue="value"/>
												</td>
												<td id="singleSuccessUrlTr"  style="display: <s:property value="%{singleSuccessURL}"/>">
													<s:textfield id="successExternalURL" cssStyle="width:200px;"
														name="dataSource.successExternalURL" maxlength="256"/>
													<s:text	name="config.cwp.detail.externalCWP.loginURL.range" />
												</td>
												</s:else>
											</tr>
											<s:if test="%{!easyMode}">
											<tr>
												<td><s:radio onclick="this.blur();" onchange="successURLTypeChanged(this);" label="Gender" name="dataSource.externalURLSuccessType" list="%{externalURLTagged}" listKey="key" listValue="value"/>
												</td>
												<td id="taggedSuccessUrlTr" style="display: <s:property value="%{taggedSuccessURL}"/>">
													<s:select name="successExternalURLId" id="successExternalURLId"
													list="%{ipAddressIdList}" listKey="id" listValue="value"
													cssStyle="width: 180px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newSuccessExternalURL')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editSuccessExternalURL')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
											</s:if>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</div></fieldset>
						</div>
					</td>
				</tr>
				<tr>
					<td height="12"></td>
				</tr>
				<%-- Failure Page--%>
				<tr id="failureTR1" style="display: <s:property value="%{showFailurePageSection}"/>">
       				<td style="padding-left: 10px;">
       					<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.failurePage" />',
       						'failurePageDiv');</script></td>
    			</tr>
    			<tr id="failureTR2" style="display: <s:property value="%{showFailurePageSection}"/>">
					<td height="6"></td>
				</tr>
				<tr id="failureTR3" style="display: <s:property value="%{showFailurePageSection}"/>">
					<td style="padding: 0 6px 0 10px">
						<div id="failurePageDiv" style="display: <s:property value="%{dataSource.failureDisplayStyle}"/>">
						<fieldset><div>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
								<table>
									<tr>
										<td>
											<s:checkbox
											name="dataSource.showFailurePage"
											value="%{dataSource.showFailurePage}" 
											onclick="showFailurePage(this.checked);"/></td>
										<td><s:text name="config.cwp.failure.show" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr id="failurePageSource" style="display: <s:property value="%{failurePageSourceDisplay}"/>;">
								<td>
								<table>
									<tr id="trLoginAsFailure" style="display: <s:property value="%{loginAsFailureDisplay}"/>;">
										<td>
										<table>
											<tr>
												<td>
													<s:checkbox
													name="dataSource.useLoginAsFailure"
													value="%{dataSource.useLoginAsFailure}" 
													onclick="useLoginAsFailure(this.checked);"/></td>
												<td><s:text name="config.cwp.failure.login" /></td>
											</tr>
										</table>
										</td>
									</tr>
									<tr id="failurePageRadio" style="display: <s:property value="%{failurePageCustomDisplay}"/>;">
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding:0 2px 0 4px">
														<s:radio label="Gender" name="failurePageRadioType"
														list="#{'customize':''}"
														onclick="customizeFailurePage(this.checked);" value="%{failurePageRadioType}"/>
													</td>
													<td style="padding:0 2px 0 0">
														<s:text name="config.cwp.detail.customize" />
													</td>
													<td style="padding:0px 2px 0 4px">
														<s:radio label="Gender" name="failurePageRadioType"
														list="#{'import':''}"
														onclick="importFailurePage(this.checked);" value="%{failurePageRadioType}"/>
													</td>
													<td style="padding:0 2px 0 0">
														<s:text name="config.cwp.detail.import" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr id="failurePageCustom" style="display: <s:property value="%{failurePageCustomDisplay}"/>;">
										<td>
										<table>
											<tr>
												<td style="padding:0 0 0 10px">
												<!-- begin customize web pages -->
												<div style="display:<s:property value="%{customizeFailurePageDisplay}"/>" id="customizeFailurePage">
												<div>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding:8px 0 0 0">
															<input type="button" name="ignore" 
																value="<s:text name="config.cwp.customize.failure.page"/>"
																class="customize_button" onClick="openCustomizePanel('Failure');"
																<s:property value="writeDisabled" />>
														</td>
													</tr>
												</table>
												</div>
												</div>
												<!-- end customize web pages -->
												<!-- begin import web pages -->
												<div style="display:<s:property value="%{importFailurePage}"/>" id="importFailurePage">
												<fieldset><div>
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td style="padding:0 0 0 0">
														<table>
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.directoryName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="failureDirectoryName" name="failureDirectoryName"
																	list="%{availableCwpDirs}" value="dataSource.directoryName"
																	onchange="showCwpFiles(this.options[this.selectedIndex].text);"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addDirectory" value="Create" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Directory', 'newCwpDirectory', 'failure');"
																	style="width: 90px;"></td>
															</tr>
															<tr>
																<td width="120px" style="padding:4px 0 4px 0"><s:text
																	name="config.cwp.detail.failurePageName" /></td>
																<td style="padding:4px 0 4px 0"><s:select
																	id="failurePageName" name="dataSource.failurePageName"
																	list="%{availableCwpFiles}" value="dataSource.failurePageName"
																	cssStyle="width: 200px;" />
																	<input type="button" name="addFileTwo" value="Add/Remove" <s:property value="writeDisabled" />
																	class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page', 'newCwpFile', 'failure');"
																	style="width: 90px;"></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>
												</div></fieldset>
												</div>
												<!-- end import web pages -->
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td style="padding: 10px 0 0 0;">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 0" width="140px">
											<s:text name="config.cwp.failure.redirect" />
										</td>
										<td style="padding:0 2px 0 4px">
											<s:radio label="Gender" name="failureRedirection"
											list="#{'no-redirection':''}" 
											onclick="failureNoRedirection(this.checked);" value="%{failureRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.success.redirect.no" />
										</td>
										<td style="padding:0 2px 0 4px">
											<s:radio label="Gender" name="failureRedirection"
											list="#{'login':''}" 
											onclick="failureRedirectLogin(this.checked);" value="%{failureRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.failure.redirect.login" />
										</td>
										<td style="padding:0px 2px 0 4px">
											<s:radio label="Gender" name="failureRedirection"
											list="#{'external':''}" 
											onclick="failureRedirectExternal(this.checked);" value="%{failureRedirection}"/>
										</td>
										<td style="padding:0 2px 0 0">
											<s:text name="config.cwp.success.redirect.external" />
										</td>
									</tr>
									<tr id="failureExternalRow" style="display: <s:property value="%{failureExternalDisplay}"/>">
										<td colspan="10" style="padding: 0 0 0 60px">
										<table>
											<tr>
												<td>
												</td>
												<td>
													<FONT color="blue">
														<s:text
														name="config.cwp.success.redirect.external.url.notes" />
													</FONT>
												</td>
											</tr>
											<tr>
												<s:if test="%{easyMode}">
												<td>
													<s:text name="config.cwp.success.redirect.external.url" />
												</td>
												<td>
													<s:textfield id="failureExternalURL" cssStyle="width:200px;"
														name="dataSource.failureExternalURL" maxlength="256"/>
													<s:text	name="config.cwp.detail.externalCWP.loginURL.range" />
												</td>
												</s:if>
												<s:else>
													<td style="padding-right:20px;"><s:radio onclick="this.blur();" onchange="failureURLTypeChanged(this);" label="Gender" name="dataSource.externalURLFailureType" list="%{externalURLSingle}" listKey="key" listValue="value"/>
													</td>
													<td id="singleFailureUrlTr" style="display: <s:property value="singleFailureURL"/>">
														<s:textfield id="failureExternalURL" cssStyle="width:200px;"
															name="dataSource.failureExternalURL" maxlength="256"/>
														<s:text	name="config.cwp.detail.externalCWP.loginURL.range" />
													</td>
												</s:else>
											</tr>
											<s:if test="%{!easyMode}">
											<tr>
												<%-- <td><s:text name="config.cwp.success.redirect.external.url" /></td> --%>
												<td style="padding-right:20px;"><s:radio onclick="this.blur();" onchange="failureURLTypeChanged(this);" label="Gender" name="dataSource.externalURLFailureType" list="%{externalURLTagged}" listKey="key" listValue="value"/>
												</td>
												<td id="taggedFailureUrlTr" style="display: <s:property value="taggedFailureURL"/>">
													<s:select name="failureExternalURLId" id="failureExternalURLId"
													list="%{ipAddressIdList}" listKey="id" listValue="value"
													cssStyle="width: 180px;" />
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/new_disable.png" />"
														width="16" height="16" alt="New" title="New" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('newFailureExternalURL')"><img class="dinl"
														src="<s:url value="/images/new.png" />"
														width="16" height="16" alt="New" title="New" /></a>
													</s:else>
													<s:if test="%{writeDisabled == 'disabled'}">
														<img class="dinl marginBtn"
														src="<s:url value="/images/modify_disable.png" />"
														width="16" height="16" alt="Modify" title="Modify" />
													</s:if>
													<s:else>
														<a class="marginBtn" href="javascript:submitAction('editFailureExternalURL')"><img class="dinl"
														src="<s:url value="/images/modify.png" />"
														width="16" height="16" alt="Modify" title="Modify" /></a>
													</s:else>
												</td>
											</tr>
											</s:if>
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</div></fieldset>
						</div>
					</td>
				</tr>
				<tr id="failureTR4" style="display: <s:property value="%{showFailurePageSection}"/>">
					<td height="12"></td>
				</tr>
	<%-----------------------------------------------------CWP Multi Language Support Page start------------------------------------%>
				<tr id="languageSupportTR1" style="display: <s:property value="%{showMultiLanguagePageSection}"/>">
       				<td style="padding-left: 10px;">
       					<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.language.support" />',
       						'languageSupportPageDiv');</script></td>
    			</tr>
    			<tr id="languageSupportTR2" style="display: <s:property value="%{showMultiLanguagePageSection}"/>">
					<td height="6"></td>
				</tr>
				<tr id="languageSupportTR3" style="display: <s:property value="%{showMultiLanguagePageSection}"/>">
					<td style="padding: 0 6px 0 10px">
						<div id="languageSupportPageDiv" style="display: <s:property value="%{dataSource.languageSupportDisplayStyle}"/>">
						<fieldset><div>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
								<table>
								<tr>
								<td>
								<s:text name="config.cwp.language.support.chooseLanguage_label" />
								</td>
								</tr>
									<tr>
									<td>
									<table>
									<tr>
									<td>
											<s:checkbox
											id="supportDutchID"
											name="supportDutch"
											value="%{supportDutch}" 
											onclick="updateLanguageList('supportDutchID');"
											/></td>
										<td><s:text name="config.cwp.language.support.dutch" /></td>
										
										<td>
											<s:checkbox
											id="supportEnglishID"
											name="supportEnglish"
											value="%{supportEnglish}" 
											onclick="warnEnglishMsg(supportEnglishID)"
											/></td>
										<td><s:text name="config.cwp.language.support.english" /></td>
									</tr>
									<tr>
									    <td>
											<s:checkbox
											id="supportFrenchID"
											name="supportFrench"
											value="%{supportFrench}" 
											onclick="updateLanguageList('supportFrenchID');"
											/></td>
										<td><s:text name="config.cwp.language.support.french" /></td>
										<td>
											<s:checkbox
											id="supportGermanID"
											name="supportGerman"
											value="%{supportGerman}" 
											onclick="updateLanguageList('supportGermanID');"
											/></td>
										<td><s:text name="config.cwp.language.support.german" /></td>
										
									</tr>
									<tr>
									    <td>
											<s:checkbox
											id="supportItalianID"
											name="supportItalian"
											value="%{supportItalian}" 
											onclick="updateLanguageList('supportItalianID');"
											/></td>
										<td><s:text name="config.cwp.language.support.italian" /></td>
										<td>
											<s:checkbox
											id="supportKoreanID"
											name="supportKorean"
											value="%{supportKorean}" 
											onclick="updateLanguageList('supportKoreanID');"
											/></td>
										<td><s:text name="config.cwp.language.support.korean" /></td>
										
									</tr>
									<tr>
									    <td>
											<s:checkbox
											id="supportSimpleChineseID"
											name="supportSimpleChinese"
											value="%{supportSimpleChinese}" 
											onclick="updateLanguageList('supportSimpleChineseID');"
											/></td>
										<td><s:text name="config.cwp.language.support.simpleChinese" /></td>
										<td>
											<s:checkbox
											id="supportSpanishID"
											name="supportSpanish"
											value="%{supportSpanish}" 
											onclick="updateLanguageList('supportSpanishID');"
											/></td>
										<td><s:text name="config.cwp.language.support.spanish" /></td>
										
									</tr>
									<tr>
									    <td>
											<s:checkbox
											id="supportTraditionalChineseID"
											name="supportTraditionalChinese"
											value="%{supportTraditionalChinese}" 
											onclick="updateLanguageList('supportTraditionalChineseID');"
											/></td>
										<td><s:text name="config.cwp.language.support.traditionalChinese" /></td>
										
									</tr>
									</table>
									</td>
									</tr>
									<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
                                    <tr>
								     <td class="labelT1" width="130px" style="padding:4px 0 4px 10px">
									   <s:text	name="config.cwp.language.support.defaultLanguage_label" />
								    </td>
								    <td>
									<s:select name="dataSource.defaultLanguage" id="defaultlanguagecombobox"
										list="%{enumlanguage}" listKey="key" listValue="value"
										value="dataSource.defaultLanguage" cssStyle="width: 150px;" 
										/>
								    </td>
							</tr>
						</table>	
						</td>
									</tr>
								</table>
								</td>
							</tr>

						</table>
						</div></fieldset>
						</div>
					</td>
				</tr>
				<tr id="languageSupportTR4" style="display: <s:property value="%{showMultiLanguagePageSection}"/>">
					<td height="12"></td>
				</tr>
				
				
				<!--end of CWP Multi Language Support Page  -->
				
				<!-- Advanced Configuration-->
				<tr>
       				<td style="padding-left: 10px;">
       					<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.advanced" />',
       						'advanced');</script></td>
    			</tr>
    			<tr>
					<td height="6"></td>
				</tr>
				<tr>
       				<td style="padding: 0 6px 0 10px;">
           				<div id="advanced" style="display: <s:property value="%{dataSource.advancedDisplayStyle}"/>">
           				<fieldset><div>
              			<table cellspacing="0" cellpadding="0" border="0" >
              				<tr>
								<td colspan="2" height="10"></td>
							</tr>
							<!-- display session timer -->
							<tr>
								<td colspan="2">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 6px"><s:checkbox
											name="dataSource.enabledPopup"
											value="%{dataSource.enabledPopup}" 
											onclick="changeSessionTimer(this.checked);"/></td>
										<td><s:text name="config.cwp.enable.popup" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr id="sessionAlertDiv"
								style="display: <s:property value="%{dataSource.sessionTimerDisplay}"/>">
								<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td style="padding:4px 2px 4px 33px"><s:text
												name="config.cwp.detail.externalCWP.sessionAlert" />
											</td>
											<td style="padding:2px 2px 0px 4px">
												<s:textfield id="sessionAlert" name="dataSource.sessionAlert" 
													maxlength="2" cssStyle="width:50px"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;
												<s:text
													name="config.cwp.detail.externalCWP.sessionAlert.range" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- default network setting -->
							<tr>
								<td colspan="2" >
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0 2px 0 6px"><s:checkbox
											name="dataSource.useDefaultNetwork"
											value="%{dataSource.useDefaultNetwork}"
											onclick="show_hideNetwork(this.checked);" /></td>
										<td><s:text name="config.cwp.detail.useDefaultNetwork" /></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" >
								<div style="display:<s:property value="%{hideNetwork}"/>"
									id="hideNetwork">
								<table border="0" cellspacing="0" cellpadding="0">
									<s:if test="%{fullMode}">
									<tr>
										<td style="padding:5px 5px 5px 10px">
										<fieldset><legend><s:text
											name="config.cwp.detail.eth0" /></legend>
										<div>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td height="2px" />
											</tr>
											<tr>
												<td width="70px"><s:text
													name="config.cwp.detail.ipAddress" /></td>
												<td width="170px"><s:textfield
													name="dataSource.ipForEth0" size="24" maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												<td width="70px"><s:text name="config.cwp.detail.netmask" /></td>
												<td><s:textfield name="dataSource.maskForEth0" size="24"
													maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
											</tr>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
									<tr>
										<td style="padding:5px 5px 5px 10px">
										<fieldset><legend><s:text
											name="config.cwp.detail.eth1" /></legend>
										<div>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td height="2px" />
											</tr>
											<tr>
												<td width="70px"><s:text
													name="config.cwp.detail.ipAddress" /></td>
												<td width="170px"><s:textfield
													name="dataSource.ipForEth1" size="24" maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												<td width="70px"><s:text name="config.cwp.detail.netmask" /></td>
												<td><s:textfield name="dataSource.maskForEth1" size="24"
													maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
											</tr>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
									</s:if>
									<tr>
										<td style="padding:5px 5px 5px 10px">
										<fieldset><legend><s:text
											name="config.cwp.detail.radio11a" /></legend>
										<div>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td height="2px" />
											</tr>
											<tr>
												<td width="70px"><s:text
													name="config.cwp.detail.ipAddress" /></td>
												<td width="170px"><s:textfield
													name="dataSource.ipForAMode" size="24" maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												<td width="70px"><s:text name="config.cwp.detail.netmask" /></td>
												<td><s:textfield name="dataSource.maskForAMode" size="24"
													maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
											</tr>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
									<tr>
										<td style="padding:5px 5px 5px 10px">
										<fieldset><legend><s:text
											name="config.cwp.detail.radio11b" /></legend>
										<div>
										<table border="0" cellspacing="0" cellpadding="0" width="100%">
											<tr>
												<td height="2px" />
											</tr>
											<tr>
												<td width="70px"><s:text
													name="config.cwp.detail.ipAddress" /></td>
												<td width="170px"><s:textfield
													name="dataSource.ipForBGMode" size="24" maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
												<td width="70px"><s:text name="config.cwp.detail.netmask" /></td>
												<td><s:textfield name="dataSource.maskForBGMode" size="24"
													maxlength="15"
													onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
											</tr>
										</table>
										</div>
										</fieldset>
										</td>
									</tr>
								</table>
								</div>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- HTTPS -->
							<tr>
								<td colspan="2"><table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0px 2px 0px 6px"><s:checkbox
											name="dataSource.enabledHttps"
											value="%{dataSource.enabledHttps}"
											onclick="show_hideKeyFile(this.checked);" /></td>
										<td><s:text
											name="config.cwp.detail.enabledHttps" /></td>
									</tr>
								</table></td>
							</tr>
							<tr id="selectKeyFile" colspan="2" style="display:<s:property value="%{hideKeyFileList}"/>">
								<td colspan="2"><table>
									<tr>
										<td style="padding:0 2px 0px 30px"><s:text
											name="config.cwp.detail.keyFileName" /></td>
										<td style="padding:0 2px 0px 26px"><s:select id="keyFileName" name="certificate"
											list="%{certificateList}" value="%{certificate}"
											listKey="id" listValue="value"
											cssStyle="width: 172px;" />
											<!-- input type="button" name="addKeyFile" value="new"
											id="btnAddKeyFile" <s:property value="writeDisabled" />
											class="button short" onClick="submitAction('addKeyFile');" -->
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/new_disable.png" />"
											width="16" height="16" alt="New" title="New" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('addKeyFile')"><img class="dinl"
											src="<s:url value="/images/new.png" />"
											width="16" height="16" alt="New" title="New" /></a>
										</s:else>
										<s:if test="%{writeDisabled == 'disabled'}">
											<img class="dinl marginBtn"
											src="<s:url value="/images/modify_disable.png" />"
											width="16" height="16" alt="Modify" title="Modify" />
										</s:if>
										<s:else>
											<a class="marginBtn" href="javascript:submitAction('editKeyFile')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
										</s:else></td>
									</tr>
									<tr>
										<td colspan="4">
											<table>
												<tr>
													<td style="padding:0 0px 0px 24px">
														<s:checkbox
															name="dataSource.certificateDN"
															value="%{dataSource.certificateDN}" />
													</td>
													<td>
														<s:text
															name="config.cwp.detail.certificateDN" />
													</td>
												</tr>
												<tr>
													<td></td>
													<td>
														<FONT color="blue"><s:text name="config.cwp.detail.certificateDN.notes" /></FONT>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- HTTP 302 -->
							<tr>
								<td colspan="2"><table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td style="padding:0px 2px 0px 6px"><s:checkbox
											name="dataSource.enabledHTTP302"
											value="%{dataSource.enabledHTTP302}" /></td>
										<td><s:text
											name="config.cwp.http302" /></td>
									</tr>
								</table></td>
							</tr>
							<!--Use Policy Check  -->
							<tr id="enableUsePolicyTr" style="display:<s:property value="%{enableUsePolicyStyle}"/>">
								<td colspan="2"><table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="6"></td>
									</tr>
									<tr>
										<td style="padding:0px 2px 0px 6px"><s:checkbox
											name="dataSource.enableUsePolicy"
											value="%{dataSource.enableUsePolicy}" /></td>
										<td><s:text name="config.cwp.check.usepolicy" /></td>
									</tr>
								</table></td>
							</tr>
							<!-- external and internal DHCP and DNS-->
							<tr>
								<td colspan="2">
									<div id="internalCWP2" style="display:<s:property value="%{showInternalServer}"/>">
										<table>
											<tr>
												<td height="6"></td>
											</tr>
											<tr>
												<td>
													<table>
														<tr>
															<td>
																<fieldset><legend><s:text name="config.cwp.detail.dhcp-dns" /></legend>
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="6"></td>
																	</tr>
																	<tr>
																		<td>
																			<table border="0" cellspacing="0" cellpadding="0">
																				<tr>
																					<td style="padding-left: 5px" valign="top">
																							<s:radio name="dataSource.serverType"
																							list="#{1:''}"
																							value="%{dataSource.serverType}" onclick="showServerType(this.value)" />
																						</td>	
																						<td><s:text name="config.cwp.detail.externalServer" /></td>	
																						<td style="padding-left: 5px" valign="top">
																							<s:radio name="dataSource.serverType"
																							list="#{2:''}"
																							value="%{dataSource.serverType}" onclick="showServerType(this.value)" />
																						</td>	
																						<td><s:text name="config.cwp.detail.internalServer" /></td>	
																				</tr>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td height="6"></td>
																	</tr>
																	<tr>
																		<td colspan="4" style="padding-left:16px">
																		<div style="display:<s:property value="%{showInternal}"/>" id="internal">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td class="labelT1" width="75px"><s:text
																					name="config.cwp.detail.leaseTime" /></td>
																				<td width="210px"><s:textfield name="dataSource.leaseTime" size="8"
																					maxlength="5"
																					onkeypress="return hm.util.keyPressPermit(event,'ten');" />&nbsp;<s:text
																					name="config.cwp.detail.leaseTime.range" /></td>
																				<td width="120px"><s:text
																					name="config.cwp.detail.renewalResponse" /></td>
																				<td><s:select name="dataSource.dhcpMode"
																					list="%{enumDhcpMode}" listKey="key" listValue="value"
																					value="dataSource.dhcpMode" cssStyle="width: 160px;" /></td>
																					
																			</tr>
																		</table>
																		</div>
																		</td>
																	</tr>
																</table>
																</fieldset>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- vlan -->
							<tr style="display:<s:property value="%{showExternal}"/>" id="external">
								<td colspan="2" style="padding: 6px 0 0 0;">
								<table cellspacing="0" cellpadding="0" border="0" >
									<tr>
										<td style="padding: 0 0 0 6px;">
											<s:checkbox
												name="dataSource.overrideVlan"
												value="%{dataSource.overrideVlan}" 
												onclick="overrideVlan(this.checked);"/>
										</td>
										<td>
											<s:text name="config.cwp.detail.vlan" />
										</td>
										<td id="vlanTD" style="padding: 0 0 0 6px; display:<s:property value="%{showVlan}"/>"><ah:createOrSelect divId="errorDisplay" list="vlanIdList" typeString="VlanId" 
											selectIdName="vlanSelect" inputValueName="inputVlanValue" swidth="120px"/>
										</td>								
									</tr>
									<tr>
										<td></td>
										<td colspan="10">
											<FONT color="blue"><s:text name="config.cwp.detail.vlan.notes" /></FONT>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<tr>
								<td class="labelT1" width="130px"><s:text
									name="config.cwp.detail.registrationPeriod" /></td>
								<td><s:textfield name="dataSource.registrationPeriod"
									maxlength="6"
									onkeypress="return hm.util.keyPressPermit(event,'ten');" />
									<s:text	name="config.cwp.detail.registrationPeriod.range" /></td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- web server domain name-->
							<tr>
								<td class="labelT1" width="180px"><s:text
									name="config.cwp.detail.domainName" /></td>
								<td>
									<s:textfield
											name="dataSource.serverDomainName" maxlength="32"
											onkeypress="return hm.util.keyPressPermit(event,'name');" />
											<s:text name="config.cwp.detail.domainName.range" />
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- walled garden  -->
							<tr id="walledGardenTr" style="display: <s:property value="%{walledGardenDisplay}"/>">
						       <td colspan="2" style="padding-left: 10px;">
						       		<script type="text/javascript">insertFoldingLabelContext('<s:text name="config.cwp.detail.walledGarden" />',
						       			'walledGarden');</script>
						       </td>
						    </tr>
						    <tr>
						       <td colspan="2" style="padding-left: 20px;">
						           <div id="walledGarden" style="display: <s:property value="%{dataSource.walledGardenDisplayStyle}"/>">
						              <table cellspacing="0" cellpadding="0" border="0" >
						                  <tr>
											<td style="padding:4px 0px 4px 0px;" valign="top">
												<table cellspacing="0" cellpadding="0" border="0" class="embedded">
													<tr style="display:<s:property value="%{hideNewButton}"/>" id="newButton">
														<td colspan="7" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore" value="New" <s:property value="writeDisabled" />
																	class="button" onClick="showCreateSection();"></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" <s:property value="writeDisabled" />
																	onClick="removeWGItems();"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr style="display:<s:property value="%{hideCreateItem}"/>" id="createButton">
														<td colspan="7" style="padding-bottom: 2px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" <s:property value="writeDisabled" />
																	class="button" onClick="addWGItem();"></td>
																<td><input type="button" name="ignore" value="Remove"
																	class="button" <s:property value="writeDisabled" />
																	onClick="removeWGItems();"></td>
																<td><input type="button" name="ignore" value="Cancel" <s:property value="writeDisabled" />
																	class="button" onClick="hideCreateSection();"></td>
															</tr>
														</table>
														</td>
													</tr>
													<tr>
													   <td colspan="10" style="padding: 5px;">
													   		<FONT color="blue"><s:text name="config.cwp.wallgarden.note"/></FONT>
													   </td>
													</tr>
													<tr>
														<td><div id="showError" /></td>
													</tr>
													<tr id="headerSection">
														<th align="left" style="padding-left: 0;" width="10"><input
															type="checkbox" id="checkAll"
															onClick="toggleCheckAllItems(this);"></th>
														<th align="left" width="70"><s:text
															name="config.cwp.detail.walledGarden.ruleId" /></th>
														<th align="left" width="200"><s:text
															name="config.cwp.detail.walledGarden.server" /></th>
														<th align="left" width="70"><s:text
															name="config.cwp.detail.walledGarden.service" /></th>
														<th align="left" width="100"><s:text
															name="config.cwp.detail.walledGarden.protocol" /></th>
															<th align="left" width="70"><s:text
																name="config.cwp.detail.walledGarden.port" /></th>
													</tr>
													<tr style="display: <s:property value="%{hideCreateItem}"/>" id="createSection">
														<td class="listHead" width="10">&nbsp;</td>
														<td class="listHead" width="70">&nbsp;</td>
														<td class="listHead" valign="top" width="200">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><ah:createOrSelect divId="errorWG"
																	list="ipList" typeString="WGIp" swidth="180px"
																	selectIdName="WGIpSelect" inputValueName="WGIpValue" 
																	/></td>
																</tr>
															</table>
														</td>
														<td class="listHead" valign="top" width="70">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:select id="service" list="%{serviceList}"
																		name="serviceId" value="serviceId"
																		listKey="key" listValue="value"
																		onchange="changeService(this.value);" /></td>
																</tr>
															</table>
														</td>
														<td class="listHead" valign="top" width="100">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:textfield id="protocolNumber"
																		name="protocolNumber" maxlength="5" 
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"
																		cssStyle="width: 60px;"/></td>
																</tr>
															</table>
														</td>
														<td class="listHead" valign="top" width="70">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:textfield id="port"
																		name="port" maxlength="5"
																		onkeypress="return hm.util.keyPressPermit(event,'ten');"
																		 cssStyle="width: 60px;"/></td>
																</tr>
															</table>
														</td>
													</tr>
													<!-- 
													<s:if test="%{gridCount > 0}">
														<s:generator separator="," val="%{' '}" count="%{gridCount}">
															<s:iterator>
																<tr>
																	<td class="list" colspan="6">&nbsp;</td>
																</tr>
															</s:iterator>
														</s:generator>
													</s:if>
													 -->	
													<tr>
														<td valign="top" colspan="7">
															<table cellspacing="0" cellpadding="0" border="0"
																class="embedded" id="wgTable">
															<s:iterator value="%{dataSource.walledGarden}" status="status">
																<tr class="list">
																	<td align="left" class="listCheck" width="10"><s:checkbox name="itemIndices"
																		fieldValue="%{itemId}" /></td>
																	<td align="left" class="list" width="70"><s:property value="itemId" /></td>
																	<td align="left" class="list" width="200"><s:property value="server.addressName" /></td>
																	<td align="left" class="list" width="70"><s:property value="serviceName" /></td>
																	<td align="left" class="list" width="100"><s:property value="protocolValue" escape="false"/></td>
																	<td align="left" class="list" width="70"><s:property value="portValue" escape="false"/></td>
																</tr>
															</s:iterator>							
															</table>
														</td>
													</tr>
												</table>
											</td>
											</tr>
 		            			      </table>
					                </div>
								</td>
						    </tr>
						    <tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- success and failure delay -->
							<tr>
								<td colspan="2" style="padding: 5px 5px 5px 5px;">
								<fieldset>
								<div>
								<table>
									<tr>
										<td width="250px" style="padding: 6px 0 0 0"><s:text
											name="config.cwp.success.delay" /></td>
										<td style="padding: 6px 0 0 0"><s:textfield
											name="dataSource.successDelay" size="4" maxlength="2"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											&nbsp;<s:text name="config.cwp.redirect.delay" /></td>
									</tr>
									<tr>
										<td width="250px"><s:text
											name="config.cwp.failure.delay" /></td>
										<td><s:textfield
											name="dataSource.failureDelay" size="4" maxlength="2"
											onkeypress="return hm.util.keyPressPermit(event,'ten');" />
											&nbsp;<s:text name="config.cwp.redirect.delay" /></td>
									</tr>
								</table>
								</div>
								</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2" height="6"></td>
							</tr>
							<!-- Library SIP -->
							<s:if test="!easyMode">
							<tr>
								<td colspan="2" style="padding: 5px 5px 5px 5px;">
								<fieldset>
								<legend><s:text name="config.cwp.detail.librarySIP" /></legend>
								<div>
								<table>
									<tr>
										<td width="150px" style="padding: 6px 0 0 0"><s:text
											name="config.cwp.detail.librarySIP.blockRedirect" /></td>
										<td style="padding: 6px 0 0 0"><s:textfield
											name="dataSource.blockRedirectURL" size="42" maxlength="256" />
											&nbsp;<s:text name="config.cwp.detail.librarySIP.blockRedirect.range" /></td>
									</tr>
									<tr>
										<td>
										</td>
										<td>
											<FONT color="blue">
												<s:text	name="config.cwp.success.redirect.external.url.notes" />
											</FONT>
										</td>
									</tr>
									<tr>
										<td colspan="2">
											<s:text	name="config.cwp.detail.librarySIP.blockRedirect.notes" />
										</td>
									</tr>
								</table>
								</div>
								</fieldset>
								</td>
							</tr>
							</s:if>
						</table>
						</div></fieldset>
                     	</div>
					</td>
    			</tr>
    			<tr>
					<td height="10"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
<div id="uploadFilePanelElement" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" style="background-color: #FFFFFF;" src="">
		</iframe>
	</div>
</div>

<div id="customizePagePanelElement" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="customizePageFrame" name="customizePageFrame" width="0" height="0"
			frameborder="0" style="background-color: #FFFFFF;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var uploadFilePanel;
var customizePagePanel;
var oprationName;

function createUploadFilePanel(width, height){
	var div = Get("uploadFilePanelElement");
	var iframe = Get("uploadFileFrame");
	iframe.width = width;
	iframe.height = height;
	uploadFilePanel = new YAHOO.widget.Panel(div, 
	                                        { width:(width+10)+"px", 
											  fixedcenter:"contained", 
											  visible:false,
											  draggable: true,
											  modal:true,
											  constraintoviewport:true } );
	uploadFilePanel.render();
	div.style.display="";
	uploadFilePanel.beforeHideEvent.subscribe(refreshCwpPage);
}

function createCustomizePagePanel(width, height){
	var div = Get("customizePagePanelElement");
	var iframe = Get("customizePageFrame");
	iframe.width = width;
	iframe.height = height;
	customizePagePanel = new YAHOO.widget.Panel(div, 
	                                        { width:(width+10)+"px", 
											  fixedcenter:"contained", 
											  visible:false,
											  close: true,
											  modal:true,
											  draggable: true,
											  constraintoviewport:true } );
	customizePagePanel.render(document.body);
	div.style.display="";
	customizePagePanel.beforeHideEvent.subscribe(closeCustomizePanel);
}

function refreshCwpPage(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("uploadFileFrame").style.display = "none";
	}
	
	showCwpFiles(oprationName);
}

function closeCustomizePanel() {
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("customizePageFrame").style.display = "none";
	}
}

function openUploadFilePanel(title, doOperation, directoryName)
{
	var directory;
	
	if(directoryName == "login") {
		directory = Get("directoryName");
	} else if(directoryName == "success") {
		directory = Get("successDirectoryName");
	} else if(directoryName == "failure") {
		directory = Get("failureDirectoryName");
	}
	
	var width = 580;
	if ("newCwpFile" == doOperation) {		
		if(directory.options.length == 1 && '<s:text name="config.optionsTransfer.none" />' == directory.options[0].text) {
			hm.util.reportFieldError(directory, '<s:text name="error.hiveAPFile.createDir" />');
			directory.focus();
			return;
		}
		oprationName = directory.value;
		doOperation += "&cwpDir="+encodeURIComponent(oprationName);
		title += " in directory ("+directory.value+")";
	} else if ("newCwpCustomFile" == doOperation) {
		oprationName = "cwp&resource";
	} else if ("newCwpDirectory" == doOperation) {
		//width = 550;
		oprationName = "cwp&directory"+directory.value;
	}
	
	if(uploadFilePanel == null) {
		createUploadFilePanel(width,445);
	} 
	
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("uploadFileFrame").style.display = "";
	}
	uploadFilePanel.setHeader(title);
	uploadFilePanel.show();

	var iframe = Get("uploadFileFrame");
	iframe.src ="<s:url value='hiveApFile.action' includeParams='none' />?operation="+doOperation;
}

function customizeLoginPage() {
	if(Get(formName + "_dataSource_registrationType").value 
		== <%=Cwp.REGISTRATION_TYPE_PPSK%>) {
		openCustomizePanel('PPSK');
	} else {
		openCustomizePanel('Login');
	}
}

function openCustomizePanel(pageType) {
	if(customizePagePanel == null) {
		createCustomizePagePanel(800, 700);
	}
	
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("customizePageFrame").style.display = "";
	}
	
	if(pageType == 'PPSK') {
		customizePagePanel.setHeader("Customize Private PSK Page");
	} else {
		customizePagePanel.setHeader("Customize " + pageType + " Page");
	}
	
	customizePagePanel.show();
	document.forms[formName].target="customizePageFrame";
	document.forms[formName].customPage.value = pageType;
	document.forms[formName].registType.value = Get(formName + "_dataSource_registrationType").value;
	document.forms[formName].operation.value = "openCustomizePage";
	document.forms[formName].submit();
	document.forms[formName].target="_self";
	
	
	/* var iframe = Get("customizePageFrame");
	iframe.src ="<s:url value='captivePortalWeb.action' includeParams='none' />?operation=openCustomizePage"
			+ "&customPage=" + pageType
			+ "&registType=" + Get(formName + "_dataSource_registrationType").value; */
}

function closeCustomizeWindow() {
	customizePagePanel.hide();
}

var uploadFileIframeWindow;
var customPageIFrame;
</script>

<s:if test="%{jsonMode == true && contentShownInSubDrawer}">
	<script>
		setCurrentHelpLinkUrl('<s:property value="helpLink" />');
	</script>
</s:if>