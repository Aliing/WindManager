<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tiles:insertDefinition name="tabView" />
<%@page import="com.ah.bo.wlan.Cwp"%>
<%@page import="com.ah.bo.wlan.CwpPageCustomization"%>
<style>
.transparent_frame {
	border-style: dotted; 
	border-width: 1px;
}
.div_frame {
	border-style: solid; 
	border-width: 1px;
	border-color: white;
	background-color: #dddddd;
}
.gray_text {
	color: gray;
	text-align: left;
}
#usePolicyTD{
	color: #003464;
	font-weight: bold;
}
.notice{
	padding: 10px 0 0 0;
}
.head_line{
	color: gray;
	font-weight: bold;
	font-size: 20px;
	text-align: center;
}
.sub_head_line{
	color: gray;
	font-weight: bold;
	font-size: 16px;
	text-align: left;
}
.head_notes {
	color: gray;
	font-weight: bold;
	font-size: 12px;
	text-align: left;
}
HR {
	color: white;
	height: 3px;
}
</style>
<script>
var formName = 'captivePortalWeb';
var pageTabs;
var idmSelfRegFlag = <s:property value="%{idmSelfReg}"/> || false;
var MAXIMUM_USE_POLICY_SIZE = 8000;

function onLoadPage() {
	parent.customPageIFrame = window;
}

function submitActionClose(operation) {
	if (validate(operation)) {
		var url = '<s:url action="captivePortalWeb" includeParams="none" />' 
			+ '?ignore='+new Date().getTime();
		document.forms[formName].operation.value = 'customizePage';
		YAHOO.util.Connect.setForm(document.forms[formName]);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterCustomizePage}, null);
	}
}

var afterCustomizePage = function(o) {
	eval("var details = " + o.responseText);
	
	if(!details.ok) {
		hm.util.reportFieldError(document.getElementById("notes"), details.msg);
		return ;
	}
	
	parent.closeCustomizeWindow();  
}

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		parent.beforeSubmitAction(parent.document.forms[formName]);
	    document.forms[formName].submit();
	}
}

function validate(operation) {
	if(!validateForegroundColor()) {
		return false;
	}
	
	if(!validateFieldLabel()) {
		return false;
	}
	
	if(!validateSuccessNotice()) {
		return false;
	}
	
	if(!validateFailureNotice()) {
		return false;
	}
	
	return true;
}

function validateForegroundColor() {
	var errorElement = Get("errorElement");
	// R
	var inputElement = Get("foregroundColorR");
		      
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(errorElement, '<s:text name="error.requiredField"><s:param>Foreground Color - R</s:param></s:text>');
		inputElement.focus();
		return false;
	}
		    
	var message = hm.util.validateIntegerRange(inputElement.value, 
			'Foreground Color - R', 0, 255);
		    
	if (message != null) {
		hm.util.reportFieldError(errorElement, message);
		inputElement.focus();
		return false;
	}
		    
	// G
	inputElement = Get("foregroundColorG");
		    
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(errorElement, '<s:text name="error.requiredField"><s:param>Foreground Color - G</s:param></s:text>');
		inputElement.focus();
		return false;
	}
		    
	message = hm.util.validateIntegerRange(inputElement.value, 
			'Foreground Color - G', 0, 255);
	
	if (message != null) {
		hm.util.reportFieldError(errorElement, message);
		inputElement.focus();
		return false;
	}
		      
		      // B
	inputElement = Get("foregroundColorB");
		      
	if (inputElement.value.length == 0) {
		hm.util.reportFieldError(errorElement, '<s:text name="error.requiredField"><s:param>Foreground Color - B</s:param></s:text>');
		inputElement.focus();
		return false;
	}
		      
	message = hm.util.validateIntegerRange(inputElement.value, 
		'Foreground Color - B', 0, 255);
		      
	if (message != null) {
		hm.util.reportFieldError(errorElement, message);
		inputElement.focus();
		return false;
	}			
		      
    return true;
}

function validateSuccessNotice() {
	if(document.forms[formName].customPage.value != "Success") {
		return true; 
	}
	
	var radios = document.getElementsByName("successMessageType");
	
	if(radios[0].checked) {
		var inputElement = Get("notice");
	
		if(inputElement.value.length > <s:property value="%{successNoticeLength}" />) {
			hm.util.reportFieldError(Get("noteTD"), 
				'<s:text name="error.config.cwp.page.customization.successNoticeRange" />');
	        inputElement.focus();
	        return false;
		}
	}
	
	if(<s:property value="easyMode"/>) {
		return true;
	}
	
	if(radios[1].checked) {
		var inputElement = Get("librarySIPStatus");
	
		if(inputElement.value.length > <s:property value="%{successNoticeLength}" />) {
			hm.util.reportFieldError(Get("noteTD"), 
				'<s:text name="error.config.cwp.page.customization.librarySIPStatusRange" />');
	        inputElement.focus();
	        return false;
		}
		
		inputElement = Get("librarySIPFines");
	
		if(inputElement.value.length > <s:property value="%{successNoticeLength}" />) {
			hm.util.reportFieldError(Get("noteTD"), 
				'<s:text name="error.config.cwp.page.customization.librarySIPFinesRange" />');
	        inputElement.focus();
	        return false;
		}
	}
	
	
	
	return true;
}

function validateFailureNotice() {
	if(document.forms[formName].customPage.value != "Failure") {
		return true; 
	}
	
	if(<s:property value="easyMode"/>) {
		return true;
	}
	
	var radios = document.getElementsByName("failureMessageType");
	
	if(radios[1].checked) {
		var inputElement = Get("librarySIPBlock");
	
		if(inputElement.value.length > <s:property value="%{successNoticeLength}" />) {
			hm.util.reportFieldError(Get("noteTD"), 
				'<s:text name="error.config.cwp.page.customization.librarySIPBlockRange" />');
	        inputElement.focus();
	        return false;
		}
	}
	
	
	
	return true;
}

function cancel() {
	parent.closeCustomizeWindow();
}


var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var td;

	for (var i = 0; i < details.length; i ++) {
		td = Get(details[i].id);
		
		if(td == null) {
			continue;
		}
		
		var value = details[i].v;
		updateListValues(td, value, td.value);
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

/* CWP multi language support start */

function saveMultiRes(){
	if(!validateSuccessNotice()) {
		return false;
	}
	
	if(!validateFailureNotice()) {
		return false;
	}
	
	if(!validateFieldLabel()){
		return false;
	}
	 var lang=Get("multilanguagerescombobox");
	 var notice=Get("notice");
	 var libSipBlock=document.getElementById("librarySIPBlock");
	 var libSipStatus=document.getElementById("librarySIPStatus");
	 var libSipFines=document.getElementById("librarySIPFines");
	 var usePolicy=document.getElementById("usePolicy");
	 
		var label1=Get("label_FirstNameMark");
		var label2=Get("label_LastNameMark");
		var label3=Get("label_EmailMark");
		var label4=Get("label_PhoneMark");
		var label5=Get("label_VisitingMark");
		var label6=Get("label_CommentMark");
		var label7=Get("label_RepresentingMark");
		if(label1!=null){
			 document.forms[formName].labelFirstNameMarkPostValue.value=label1.value;
		}
		if(label2!=null){
			 document.forms[formName].labelLastNameMarkPostValue.value=label2.value;
		}
		if(label3!=null){
			 document.forms[formName].labelEmailMarkPostValue.value=label3.value;
		}
		if(label4!=null){
			 document.forms[formName].labelPhoneMarkPostValue.value=label4.value;
		}
		if(label5!=null){
			 document.forms[formName].labelVisitingMarkPostValue.value=label5.value;
		}
		if(label6!=null){
			 document.forms[formName].labelCommentMarkPostValue.value=label6.value;
		}
		if(label7!=null){
			document.forms[formName].labelRepresentingMarkPostValue.value=label7.value;
		}
		
	 if(notice!=null){
		// noticeValue=noticeValue.replace(/[\r\n]/g,'<br>');
		 document.forms[formName].noticePostValue.value=notice.value.replace(/[\r\n]/g,'<br>');
	 }
	 if(libSipBlock!=null){
	 document.forms[formName].librarySIPBlockPostValue.value=libSipBlock.value.replace(/[\r\n]/g,'<br>');
	 }
	 if(libSipStatus!=null){
	 document.forms[formName].librarySIPStatusPostValue.value=libSipStatus.value.replace(/[\r\n]/g,'<br>');
	 }
	 if(libSipFines!=null){
	 document.forms[formName].librarySIPFinesPostValue.value=libSipFines.value.replace(/[\r\n]/g,'<br>');
	 }
	 if(usePolicy!=null){
		 document.forms[formName].usePolicyPostValue.value=usePolicy.value.replace(/[\r\n]/g,'<br>');
	 }
	 
	 if(document.forms[formName].usePolicyPostValue.value.length > MAXIMUM_USE_POLICY_SIZE) {
         hm.util.reportFieldError(Get("notes"), 
        		 '<s:text name="error.config.cwp.page.customization.usepolicy.limitation"><s:param>' +MAXIMUM_USE_POLICY_SIZE+ '</s:param></s:text>');
		 return false;
	 }
		
	var url = '<s:url action="captivePortalWeb" includeParams="none" />' 
			+ '?ignore='+new Date().getTime();
		document.forms[formName].operation.value = 'saveMultiRes';
		document.forms[formName].languagePostValue.value=lang.value;
		YAHOO.util.Connect.setForm(document.forms[formName]);
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterSaveMultiRes}, null);
	
}

function afterSaveMultiRes(o){
  eval("var details = " + o.responseText);
  if(!details.ok && details.msg) {
	  hm.util.reportFieldError(document.getElementById("notes"), details.msg);
  }
}

function resetOneRes(){
	var res=Get("multilanguagerescombobox");
	var idmSelfReg=Get(formName + "_idmSelfReg");
	
	var url ="<s:url action='captivePortalWeb' includeParams='none'/>?operation=resetOneRes&languageId=" + res.value+"&idmSelfReg=" + idmSelfReg.value;
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : resetAll, timeout: 60000}, null);
	
}

function setDisabledElement(id, flag) {
	if(Get(id)) {
		Get(id).disabled = flag;
	}
}

function resetAll(o) {
	Get("backgroundImage").value = '<%=CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D%>';
	Get("tileBackground").checked = false;
	Get("foregroundColorR").value = 255;
	Get("foregroundColorG").value = 255;
	Get("foregroundColorB").value = 255;
	//Get("headImage").value = '<%=CwpPageCustomization.DEFAULT_HEAD_IMAGE%>';
	Get("footImage").value = '<%=CwpPageCustomization.DEFAULT_FOOT_IMAGE%>';
	//Get("multilanguagerescombobox").value=1;
	var libSipBlock=document.getElementById("librarySIPBlock");
	var libSipStatus=document.getElementById("librarySIPStatus");
	var libSipFines=document.getElementById("librarySIPFines");
	var notice=document.getElementById("notice");
	var usePolicy=document.getElementById("usePolicy");
	
	var label1=Get("label_FirstNameMark");
	var label2=Get("label_LastNameMark");
	var label3=Get("label_EmailMark");
	var label4=Get("label_PhoneMark");
	var label5=Get("label_VisitingMark");
	var label6=Get("label_CommentMark");
	var label7=Get("label_RepresentingMark");
	
	eval("var details = " + o.responseText);

	if(notice!=null){
		notice.value=details.notice.replace(/<br>/ig, "\r\n");
	}
	if(libSipBlock!=null){
		libSipBlock.value=details.libSipBlock.replace(/<br>/ig, "\r\n");
	}
	if(libSipStatus!=null){
		libSipStatus.value=details.libSipStatus.replace(/<br>/ig, "\r\n");
	}
	if(libSipFines!=null){
		libSipFines.value=details.libSipFines.replace(/<br>/ig, "\r\n");
	}
	if(usePolicy!=null){
		usePolicy.value=details.usePolicy.replace(/<br>/ig, "\r\n");
	}
	if(label1!=null){
		label1.value=details.FirstNameMark;
	}
	if(label2!=null){
		label2.value=details.LastNameMark;
	}
	if(label3!=null){
		label3.value=details.EmailMark;
	}
	if(label4!=null){
		label4.value=details.PhoneMark;
	}
	if(label5!=null){
		label5.value=details.VisitingMark;
	}
	if(label6!=null){
		label6.value=details.CommentMark;
	}
	if(label7!=null){
		label7.value=details.RepresentingMark;
	}
	
	
	if(document.forms[formName].customPage.value == "Login"
		|| document.forms[formName].customPage.value == "PPSK") {
		
		if(document.forms[formName].registType.value == <%= Cwp.REGISTRATION_TYPE_REGISTERED%>
			|| document.forms[formName].registType.value == <%= Cwp.REGISTRATION_TYPE_BOTH%>) {
			// registration fields
			for(var i=0; i<6; i++) {
				if(idmSelfRegFlag) {
					if(i==5 && Get("enabled_" + i) === null) {
						i=i+1;
					    Get("order_" + i).value = i;
					} else {
					    Get("order_" + i).value = i+1;
					}
	                Get("order_" + i).disabled = false;
	                
					Get("enabled_" + i).checked = true;
					Get("required_" + i).disabled = (i < 2) ? true : false;
					Get("required_" + i).checked = (i < 3) ? true : false;
				} else {
				    Get("order_" + i).value = i+1;
	                Get("order_" + i).disabled = false;
	                
					Get("enabled_" + i).checked = true;
					Get("required_" + i).disabled = false;
					
					if(i==3 || i== 5)
						Get("required_" + i).checked = false;
					else
						Get("required_" + i).checked = true;
					
				}
			}
			setDisabledElement("label_FirstNameMark" , false);
			setDisabledElement("label_LastNameMark" ,false);
			setDisabledElement("label_EmailMark" ,false);
			setDisabledElement("label_PhoneMark" ,false);
			setDisabledElement("label_VisitingMark" ,false);
			setDisabledElement("label_CommentMark" ,false);
			setDisabledElement("label_RepresentingMark" ,false);
		} 
	} else if(document.forms[formName].customPage.value == "Success") {
//		Get("notice").value = '<%=CwpPageCustomization.DEFAULT_SUCCESS_NOTICE%>';
		
		var radios = document.getElementsByName("successMessageType");
		radios[0].checked = true;
		selectGeneralSuccess(true);
	} else if(document.forms[formName].customPage.value == "Failure") {
		var radios = document.getElementsByName("failureMessageType");
		radios[0].checked = true;
		selectGeneralFailure(true);
	}
}

function refreshMultiRes(){
	var res=Get("multilanguagerescombobox");
	
	
	var url ="<s:url action='captivePortalWeb' includeParams='none'/>?operation=refreshMultiRes&languageId=" + res.value;
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : updateMultiRes, timeout: 60000}, null);
	
}

function updateMultiRes(o){
	
	
	var libSipBlock=document.getElementById("librarySIPBlock");
	var libSipStatus=document.getElementById("librarySIPStatus");
	var libSipFines=document.getElementById("librarySIPFines");
	var notice=document.getElementById("notice");
	var usePolicy=document.getElementById("usePolicy");
	var label1=Get("label_FirstNameMark");
	var label2=Get("label_LastNameMark");
	var label3=Get("label_EmailMark");
	var label4=Get("label_PhoneMark");
	var label5=Get("label_VisitingMark");
	var label6=Get("label_CommentMark");
	var label7=Get("label_RepresentingMark");

	eval("var details = " + o.responseText);

	if(notice!=null){
		notice.value=details.notice.replace(/<br>/ig, "\r\n");
	}
	if(libSipBlock!=null){
		libSipBlock.value=details.libSipBlock.replace(/<br>/ig, "\r\n");
	}
	if(libSipStatus!=null){
		libSipStatus.value=details.libSipStatus.replace(/<br>/ig, "\r\n");
	}
	if(libSipFines!=null){
		libSipFines.value=details.libSipFines.replace(/<br>/ig, "\r\n");
	}
	if(usePolicy!=null){
		usePolicy.value=details.usePolicy.replace(/<br>/ig, "\r\n");
	}
	if(label1!=null){
		label1.value=details.FirstNameMark;
	}
	if(label2!=null){
		label2.value=details.LastNameMark;
	}
	if(label3!=null){
		label3.value=details.EmailMark;
	}
	if(label4!=null){
		label4.value=details.PhoneMark;
	}
	if(label5!=null){
		label5.value=details.VisitingMark;
	}
	if(label6!=null){
		label6.value=details.CommentMark;
	}
	if(label7!=null){
		label7.value=details.RepresentingMark;
	}
	
}

function refreshOnLoad(){
	var libSipBlock=document.getElementById("librarySIPBlock");
	var libSipStatus=document.getElementById("librarySIPStatus");
	var libSipFines=document.getElementById("librarySIPFines");
	var notice=document.getElementById("notice");
	var usePolicy=document.getElementById("usePolicy");

	if(notice!=null){
		notice.value=notice.value.replace(/<br>/ig, "\r\n");
	}
	if(libSipBlock!=null){
		libSipBlock.value=libSipBlock.value.replace(/<br>/ig, "\r\n");
	}
	if(libSipStatus!=null){
		libSipStatus.value=libSipStatus.value.replace(/<br>/ig, "\r\n");
	}
	if(libSipFines!=null){
		libSipFines.value=libSipFines.value.replace(/<br>/ig, "\r\n");
	}
	if(usePolicy!=null){
		usePolicy.value=usePolicy.value.replace(/<br>/ig, "\r\n");
	}
	
}

function validateMultiRes(){
	
	
	var libSipBlock=document.getElementById("librarySIPBlock");
	var libSipStatus=document.getElementById("librarySIPStatus");
	var libSipFines=document.getElementById("librarySIPFines");
	var notice=document.getElementById("notice");
	var usePolicy=document.getElementById("usePolicy");

	if(notice!=null){
		notice.value=details.notice.replace(/<br>/ig, "\r\n");
	}
	if(libSipBlock!=null){
		libSipBlock.value=details.libSipBlock.replace(/<br>/ig, "\r\n");
	}
	if(libSipStatus!=null){
		libSipStatus.value=details.libSipStatus.replace(/<br>/ig, "\r\n");
	}
	if(libSipFines!=null){
		libSipFines.value=details.libSipFines.replace(/<br>/ig, "\r\n");
	}
	if(usePolicy!=null){
		usePolicy.value=details.usePolicy.replace(/<br>/ig, "\r\n");
	}
	
	
}

/* CWP multi language support end */

function enableFields(checked, index) {
	var required = Get("required_" + index);
	required.disabled = !checked;
	if(!checked) {
		required.checked = false;
	}
	
	var label=document.getElementsByName("fieldlabels_"+index)[0];
	label.disabled = !checked;
	
	var order = Get("order_" + index);
	order.disabled = !checked;
}

function enableRequired(checked,index){
	if(checked){
		var label=document.getElementsByName("fieldlabels_"+index)[0];
		label.value = label.value + "*";
	}else{
		var label=document.getElementsByName("fieldlabels_"+index)[0];
		label.value = label.value.replace("*","").trim();
	}
}

function validateFieldLabel() {
	if(document.forms[formName].customPage.value != "Login") {
		return true; 
	}
	
	if(document.forms[formName].registType.value != <%= Cwp.REGISTRATION_TYPE_REGISTERED%>
		&& document.forms[formName].registType.value != <%= Cwp.REGISTRATION_TYPE_BOTH%>) {
		return true;
	}
	
	
	for(var i=0; i<7; i++) {
		if(null == Get("enabled_" + i) || !Get("enabled_" + i).checked) {
			continue;
		}
		
		var label=document.getElementsByName("fieldlabels_"+i)[0];
		
		if(label.value.length == 0) {
			hm.util.reportFieldError(label, '<s:text name="error.config.cwp.page.customization.labelRequired" />');
			label.focus();
			return false;
		}
	}
	if(idmSelfRegFlag && document.forms[formName].registType.value == <%= Cwp.REGISTRATION_TYPE_BOTH%>) {
		var emailEl=Get("label_EmailMark"), emailPos = emailEl.name.replace('fieldlabels_', '');
		var phoneEl=Get("label_PhoneMark"), phonePos = phoneEl.name.replace('fieldlabels_', '');
		if(!(Get("required_"+emailPos).checked || Get("required_"+phonePos).checked)
				|| !(Get("enabled_"+emailPos).checked || Get("enabled_"+phonePos).checked)) {
			hm.util.reportFieldError((emailPos < phonePos ? emailEl : phoneEl), 
					'One of Email or Phone field is required.');
			return false;
		} else if ((Get("required_"+emailPos).checked && !Get("enabled_"+emailPos).checked)
				|| (Get("required_"+phonePos).checked && !Get("enabled_"+phonePos).checked)) {
			hm.util.reportFieldError((emailPos < phonePos ? emailEl : phoneEl), 
					'The required field should be enabled first.');
			return false;
		}
	}
	
	
	return true;
}


function previewPage() {
	var pageType = document.forms[formName].customPage.value;
	
	if(pageType == "Login") {
		submitAction("previewIndexPage");	
	} else if(pageType == "Success") {
		submitAction("previewSuccessPage");
	} else if(pageType == "Failure") {
		submitAction("previewFailurePage");
	} else if(pageType == "PPSK") {
		submitAction("previewPpskPage");
	}
}

function selectGeneralSuccess(checked) {
	if(checked) {
		document.getElementById("generalDiv").style.display = "";
		document.getElementById("libraryDiv").style.display = "none";
	} 
}

function selectLibrarySuccess(checked) {
	if(checked) {
		document.getElementById("generalDiv").style.display = "none";
		document.getElementById("libraryDiv").style.display = "";
	} 
}

function selectGeneralFailure(checked) {
	if(checked) {
		document.getElementById("failureGeneralDiv").style.display = "";
		document.getElementById("failureLibraryDiv").style.display = "none";
	} 
}

function selectLibraryFailure(checked) {
	if(checked) {
		document.getElementById("failureGeneralDiv").style.display = "none";
		document.getElementById("failureLibraryDiv").style.display = "";
	} 
}

</script>

<div id="content">
<s:form action="captivePortalWeb">
	<s:hidden name="operation" />
	<s:hidden name="customPage" />
	<s:hidden name="registType" />
	<s:hidden name="noticePostValue" />
	<s:hidden name="languagePostValue" />
	<s:hidden name="librarySIPBlockPostValue" />
	<s:hidden name="librarySIPStatusPostValue" />
	<s:hidden name="librarySIPFinesPostValue" />
	<s:hidden name="usePolicyPostValue" />
	<s:hidden name="labelFirstNameMarkPostValue" />
	<s:hidden name="labelLastNameMarkPostValue" />
	<s:hidden name="labelEmailMarkPostValue" />
	<s:hidden name="labelPhoneMarkPostValue" />
	<s:hidden name="labelVisitingMarkPostValue" />
	<s:hidden name="labelCommentMarkPostValue" />
	<s:hidden name="idmSelfReg" />
	<s:hidden name="labelRepresentingMarkPostValue" />
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button"
							onClick="submitActionClose('customizePage');"
							<s:property value="writeDisabled" />></td>
					<td>
						<input type="button" name="ignore" value="Reset"
							<s:property value="writeDisabled" />
								class="button" onClick="resetOneRes();"
								style="width: 90px;">
					</td>
					<td>
						<input type="button" name="preview" value="Preview"
						<s:property value="writeDisabled" />
							class="button" onClick="previewPage();"
							style="width: 90px;">
					</td>
					<td>
						<input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="cancel();">
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<table>
		<tr>
			<td>
				<span id="noteTD" />
			</td>
		</tr>
	</table>
	<table width="100%" height="660px" border="0" cellspacing="0" cellpadding="0" bgcolor="#ccccff">
		<tr>
			<td colspan="3"><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr height="100%">
			<td id="column_1" valign="top">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
						<div>
						<table>
							<tr>
								<td width="110px">
									<s:text name="config.cwp.page.customization.backgroundImage" />
								</td>
							</tr>
							<tr>
								<td>
									<s:select id="backgroundImage" name="backgroundImage"
										list="%{availableBackgroundImages}" value="%{backgroundImage}"
										cssStyle="width: 160px;" />
								</td>
							</tr>
							<tr>
								<td>
									<input type="button" name="addBackgroundImage" value="Add/Remove" 
										<s:property value="writeDisabled" />
										class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Resource', 'newCwpCustomFile');"
										style="width: 90px;">
								</td>
							</tr>
							<tr>
								<td>
									<table>
									<tr>
										<td valign="top">
											<s:checkbox	id="tileBackground" name="tileBackgroundImage" 
											value="%{tileBackgroundImage}" />
										</td>
										<td>
											<s:text name="config.cwp.page.customization.tile" /> 
										</td>
									</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table>
										<tr>
											<td></td>
											<td><s:textfield id="errorElement" cssStyle="display: none;"/></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="110px">
									<s:text name="config.cwp.page.customization.foregroundColor" />
								</td>
							</tr>
							<tr>
								<td>
									R:<s:textfield id="foregroundColorR" name="foregroundColorR" value="%{foregroundColorR}" 
										size="3" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								</td>
							</tr>
							<tr>
								<td>
									G:<s:textfield id="foregroundColorG" name="foregroundColorG" value="%{foregroundColorG}" 
								    	size="3" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								</td>
							</tr>
							<tr>
								<td>
									B:<s:textfield id="foregroundColorB" name="foregroundColorB" value="%{foregroundColorB}" 
										size="3" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								</td>
							</tr>
							<tr>
							    <td class="labelT1" width="120px" style="padding:4px 0 4px 10px">
									   <s:text	name="config.cwp.language.support.previewLanguage_label" />
							    </td>
							</tr>
							<tr>
							    <td >
									  <s:select name="customizeLanguage" id="multilanguagerescombobox"
										list="%{customizeEnumLanguage}" listKey="key" listValue="value" onChange="refreshMultiRes()"
										value="customizeLanguage" cssStyle="width: 150px;" />
							    </td>
							</tr>
							<tr>
					            <td>
						             <input type="button" name="ignore" value="<s:text name="button.create"/>"
							           class="button"
							           onClick="saveMultiRes();"
							<s:property value="writeDisabled" />></td>
							</tr>
						</table>
						</div>
						</td>
					</tr>
				</table>
			</td>
			<td id="column_2" width="60%" align="center" height="100%">
				<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="10%"></td>
					</tr>
					<s:if test="%{customizeLoginPage || customizePPSKPage}">
					<tr>
						<td >
						<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
							<tr style="display: <s:property value="%{showH1Title}" />;">
								<td colspan="2" style="padding:5px 0 5px 0;" class="head_line">
									<s:text name="config.cwp.page.auth"></s:text>					
								</td>
							</tr>
							<!-- Authentication -->
							<tr style="display: <s:property value="%{showAuthOrPPSK}"/>;" align="center">
								<td align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<s:if test="%{customizeLoginPage}">
									<tr>
										<td colspan="2" style="padding:5px 0 5px 0;" class="sub_head_line">
											<s:text name="config.cwp.page.auth.existingUser"></s:text>					
										</td>
									</tr>
									<tr>
										<td colspan="2" style="padding:5px 0 5px 0;" class="head_notes">
											<s:text name="config.cwp.page.auth.notes"></s:text>					
										</td>
									</tr>
									</s:if>
									<s:if test="%{customizePPSKPage}">
									<tr>
										<td colspan="2" style="padding:5px 0 5px 0; width: 280px;" class="head_notes">
											<s:text name="config.cwp.page.ppsk.auth.notes"></s:text>					
										</td>
									</tr>
									</s:if>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="auth_username" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="auth_pass" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr align="center">
										<td>
											<table>
												<tr>
													<s:if test="%{customizeLoginPage}">
													<td style="width: 190px;" class="gray_text">
														<s:text name="config.cwp.page.prompt.2"></s:text>
										</td>
													</s:if>
													<s:else>
														<td></td>
													</s:else>
													<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="config.cwp.language.support.login_button_label"></s:text>
																</td>
										<td>
														<img id="loginImg" src="<s:url value="/images/cwp/login.png"/>" 
															class="dblk"  />					
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr align="center">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="300px">
												<HR  style="display: <s:property value="%{showCurrentBoth}" />;" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<!-- registration -->
							<tr style="display: <s:property value="%{showRegistrated}"/>;" align="center">
								<td align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td colspan="6" style="padding:5px 0 5px 0;" class="sub_head_line">
											<s:text name="config.cwp.page.registration"></s:text>					
										</td>
									</tr>
									<tr>
										<td colspan="6" style="padding:5px 0 5px 0;" class="head_notes">
											<s:text name="config.cwp.page.reg.notes"></s:text>					
										</td>
									</tr>
									<tr>
										<th align="center" width="60"><s:text
											name="config.cwp.page.customization.fieldEnabled" /></th>
										<th align="center" width="90"><s:text
											name="config.cwp.page.customization.fieldRequired" /></th>
										<th align="center" width="80"><s:text
											name="config.cwp.page.customization.fieldLabel" /></th>
										<th align="center" width="60"><s:text
											name="config.cwp.page.customization.fieldOrder" /></th>
									</tr>
										<s:iterator id="fields"
											value="%{dataSource.pageCustomization.pageFields}"
													status="status">
											<s:if test="%{(idmSelfReg && registType == 3) ? (!''.equals(label) && !label.equals('Comment')) : (!''.equals(label) && !label.equals('Representing'))}">
											<tr>
												<s:if test="%{idmSelfReg && registType == 3 && !''.equals(label) && (label.startsWith('First Name') || label.startsWith('Last Name'))}">
												<td class="listCheck" align="center">
												<s:checkbox
													id="enabled_%{#status.index}" name="enableds"
													onclick="enableFields(this.checked,%{#status.index});"
													fieldValue="%{#status.index}" value="%{enabled}" disabled="true"/></td>
												<td class="listCheck" align="center"><s:checkbox
													name="requireds" id="required_%{#status.index}"
													fieldValue="%{#status.index}" value="%{required}"
													disabled="true"/></td>
												</s:if>
												<s:else>
												<td class="listCheck" align="center">
												<s:checkbox
													id="enabled_%{#status.index}" name="enableds"
													onclick="enableFields(this.checked,%{#status.index});"
													fieldValue="%{#status.index}" value="%{enabled}"/></td>
												<td class="listCheck" align="center"><s:checkbox
													name="requireds" id="required_%{#status.index}"
													fieldValue="%{#status.index}" value="%{required}"
													disabled="%{!enabled}"/></td>
												</s:else>
												<td class="list" align="center"><s:textfield
													name="fieldlabels_%{#status.index}" value="%{label}" maxlength="32" cssStyle="width: 80px;"
													id="label_%{fieldMark}" disabled="%{!enabled}"
													onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
												</td>
												<td class="list" align="center"><s:select name="orders"
													value="%{place}" id="order_%{#status.index}"
													list="%{fieldOrders}" listKey="key" listValue="value"
													disabled="%{!enabled}" /></td>
											</tr>
											</s:if>
										</s:iterator>
									<tr align="center">
										<td colspan="6">
											<table>
												<tr>
													<td style="width: 200px;" class="gray_text">
														<s:text name="config.cwp.page.reg.prompt"></s:text>
													</td>
															<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="config.cwp.language.support.register_button_label"></s:text>
																</td>
										<td>
														<img id="loginImg" src="<s:url value="/images/cwp/register.png"/>" 
															class="dblk"  />					
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<!-- PPSK Server-->
							<tr style="display: <s:property value="%{showPPSKServerReg}"/>;" align="center">
								<td align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td colspan="2" style="padding:5px 0 5px 0; width: 280px;" class="head_notes">
											<s:text name="config.cwp.page.ppsk.reg.notes"></s:text>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="firstName" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="lastName" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="email" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="phone" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="visiting" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr>
										<td align="center" style="padding: 4px 0 4px 0;">
											<s:textfield name="comment" cssStyle="width: 200px;" disabled="true"/>					
										</td>
									</tr>
									<tr align="center">
										<td>
											<table>
												<tr>
													<td class="head_notes" width="180px">It may take a moment for registration to complete (* required).</td>
													<td style="height:50px;font-size:25px;text-align:center;color:white;float:left;margin-top:13px;">
																	<s:text name="config.cwp.language.support.register_button_label"></s:text>
																</td>
													<td colspan="2" style="padding:10px 0 5px 0;" align="center">
														<img id="loginImg" src="<s:url value="/images/cwp/register.png"/>" 
															class="dblk"  />				
										</td>
									</tr>
								</table>
								</td>
							</tr>
								</table>
								</td>
							</tr>
							
							<tr align="center">
								<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="300px">
												<HR  style="display: <s:property value="%{showCurrentBoth}" />;" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<!-- use policy -->
							<tr align="center" style="display: <s:property value="%{showUsePolicy}"/>;">
								<td align="center">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td id="usePolicyTD">
											<s:text name="config.cwp.page.customization.userPolicy"></s:text>					
										</td>
									</tr>
									<tr>
										<td style="padding:0px 0 0 10px;">
											<s:textarea id="usePolicy" name="usePolicy" value="%{usePolicy}"  
												cssStyle="width: 300px;" rows="5"/>					
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
					</s:if>
					<s:if test="%{customizeSuccessPage}">
					<tr>
						<td colspan="6" align="center">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr align="left">
								<td colspan="2" class="head_line">
									<s:text name="config.cwp.page.login.success"></s:text>
								</td>
							</tr>
							<tr align="center"><td>
							<table border="0" cellspacing="0" cellpadding="0">
								<tr style="display: <s:property value='showSIP'/>">
									<td>
										<s:radio label="Gender" name="successMessageType"
											list="#{'General':''}" value="%{successMessageType}"
											onclick="selectGeneralSuccess(this.checked);" />
									</td>
									<td align="left" width="100%" valign="middle">
										<s:text name="config.cwp.success.general" />
									</td>
								</tr>
								<s:if test="%{!easyMode}">
								<tr style="display: <s:property value='showLibrarySIPStyle'/>">
									<td>
										<s:radio label="Gender" name="successMessageType"
											list="#{'LibrarySIP':''}" value="%{successMessageType}"
											onclick="selectLibrarySuccess(this.checked);" />
									</td>
									<td align="left" width="100%" valign="middle">
										<s:text name="config.cwp.success.library" />
									</td>
								</tr>
								</s:if>
								<tr>
									<td colspan="10">
										<div id="generalDiv" style="display: <s:property value="%{showGeneralSection}"/>;">
											<table>
												<tr>
													<td align="left" class="notice">
														<s:text name="config.cwp.page.customization.notice"></s:text>
													</td>
												</tr>
												<tr>
													<td>
														<s:textarea id="notice" name="notice" value="%{notice}" 
																	cssStyle="width: 280px;" rows="6"/>		
													</td>
												</tr>
											</table>
										</div>
										<div id="libraryDiv" style="display: <s:property value="%{showLibrarySection}"/>;">
											<table>
												<tr>
													<td align="left" class="notice">
														<s:text name="config.cwp.success.library.status" />
													</td>
												</tr>
												<tr>
													<td>
														<s:textarea id="librarySIPStatus" name="librarySIPStatus" value="%{librarySIPStatus}" 
																	cssStyle="width: 280px;" rows="6"/>	
													</td>
												</tr>
												<tr>
													<td height="10px"></td>
												</tr>
												<tr>
													<td align="left" class="notice">
														<s:text name="config.cwp.success.library.fines" />
													</td>
												</tr>
												<tr>
													<td>
														<s:textarea id="librarySIPFines" name="librarySIPFines" value="%{librarySIPFines}" 
																	cssStyle="width: 280px;" rows="6"/>	
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
							</table></td>
							</tr>
						</table>
						</td>
					</tr>
					</s:if>
					<s:if test="%{customizeFailurePage}">
					<tr>
						<td colspan="6" align="center">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr align="left">
								<td class="head_line">
									<s:text name="config.cwp.page.login.failure"></s:text>
								</td>
							</tr>
							<tr align="center" style="display: <s:property value='showSIP'/>">
								<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td>
											<s:radio label="Gender" name="failureMessageType"
												list="#{'General':''}" value="%{failureMessageType}"
												onclick="selectGeneralFailure(this.checked);" />
										</td>
										<td align="left" width="100%" valign="middle">
											<s:text name="config.cwp.success.general" />
										</td>
									</tr>
									<s:if test="!easyMode">
									<tr style="display: <s:property value='showLibrarySIPStyle'/>">
										<td>
											<s:radio label="Gender" name="failureMessageType"
												list="#{'LibrarySIP':''}" value="%{failureMessageType}"
												onclick="selectLibraryFailure(this.checked);" />
										</td>
										<td align="left" width="100%" valign="middle">
											<s:text name="config.cwp.success.library" />
										</td>
									</tr>
									</s:if>
								</table>
								</td>
							</tr>
							<tr>
								<td colspan="10">
									<div id="failureGeneralDiv" style="display: <s:property value="%{showFailureGeneralSection}"/>;">
									<table>
										<tr align="left">
											<td	class="gray_text" style="padding: 10px 0 5px 0">
												<s:text name="config.cwp.page.failure.line1"></s:text>
											</td>
										</tr>
										<tr align="left">
											<td	class="gray_text" style="padding: 10px 0 5px 0" width="300px">
												<s:text name="config.cwp.page.failure.line2"></s:text>
											</td>
										</tr>		
									</table>
									</div>
									<div id="failureLibraryDiv" style="display: <s:property value="%{showFailureLibrarySection}"/>;">
									<table>
										<tr>
											<td align="left" class="notice">
												<s:text name="config.cwp.success.library.block" />
											</td>
										</tr>
										<tr>
											<td>
												<s:textarea id="librarySIPBlock" name="librarySIPBlock" value="%{librarySIPBlock}" 
															cssStyle="width: 280px;" rows="6"/>	
											</td>
										</tr>
									</table>
									</div>
								</td>
							</tr>
							
						</table>
						</td>
					</tr>
					</s:if>
					<tr>
						<td height="10%"></td>
					</tr>
				</table>
			</td>
			<td id="column_3" width="30%" style="padding:0 0 0 30px;">
				<table height="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td valign="top">
						<%-- <div class="div_frame">
						<table>
							<tr>
								<td>
									<s:text name="config.cwp.page.customization.headImage" />
								</td>
							</tr>
							<tr>
								<td>200 x 103 px; 72 dpi</td>
							</tr>
							<tr>
								<td>
									<s:select id="headImage" name="headerImage"
										list="%{availableHeadImages}" value="%{headerImage}"
										cssStyle="width: 180px;" />
								</td>
							</tr>
							<tr>
								<td>
									<input type="button" name="addHeadImage" value="Add/Remove" 
									<s:property value="writeDisabled" />
										class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Resource', 'newCwpCustomFile');"
										style="width: 90px;">
								</td>
							</tr>
						</table>
						</div> --%>
						</td>
					</tr>
					<tr>
						<td valign="bottom">&nbsp;
						<div class="div_frame" style="display:<s:property value="showFootImage"/>">
						<table>
							<tr>
								<td>
									<s:text name="config.cwp.page.customization.footImage" />
								</td>
							</tr>
							<tr>
								<td>
									<s:select id="footImage" name="footerImage"
										list="%{availableFootImages}" value="%{footerImage}"
										cssStyle="width: 180px;" />
																			
								</td>
							</tr>
							<tr>
								<td>
									<input type="button" name="addFootImage" value="Add/Remove" 
										<s:property value="writeDisabled" />
										class="button" onClick="openUploadFilePanel('Add/Remove CWP Web Page Resource', 'newCwpCustomFile');"
										style="width: 90px;">
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
</s:form>
</div>
<div id="uploadFilePanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="uploadFileFrame" name="uploadFileFrame" width="0" height="0"
			frameborder="0" style="background-color: #FFFFFF;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var uploadFilePanel = null;
var oprationName;

function createUploadFilePanel(width, height){
	var div = Get("uploadFilePanel");
	var iframe = Get("uploadFileFrame");
	iframe.width = width;
	iframe.height = height;
	uploadFilePanel = new YAHOO.widget.Panel(div, 
	                                        { width:(width+10)+"px", 
											  fixedcenter:false, 
											  visible:false,
											  draggable: true,
											  modal:true,
											  close:true,
											  constraintoviewport:true } );
	uploadFilePanel.render();
	div.style.display="";
	uploadFilePanel.beforeHideEvent.subscribe(refreshCwpPage);
}

function refreshCwpPage(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		Get("uploadFileFrame").style.display = "none";
	}
	showCwpFiles(oprationName);
}

function openUploadFilePanel(title, doOperation)
{
	var width = 580;
	
	if ("newCwpCustomFile" == doOperation) {
		oprationName = "cwp&resource";
	} 
	
	if(uploadFilePanel == null){
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

var uploadFileIframeWindow;
</script>