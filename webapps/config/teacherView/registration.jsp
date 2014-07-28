<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="<s:url value="/js/hm.options.js" />"></script>

<style>
span {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	color: #CC3300;
	font-size: 20px;
	font-weight: bold;
}
</style>
<script>
var formName = 'studentRegist';

function onLoadPage() {
	changeValidateCode('validateImage');
	document.getElementById(formName + "_dataSource_studentName").focus();
}

function submitAction() {
	if (validate()) {
		showProcessing();
		document.forms[formName].operation.value = 'submit';
		hm.options.selectAllOptions('selectClass');
    	document.forms[formName].submit();
	}
}

function validate() {
	var name = document.getElementById(formName + "_dataSource_studentName");
	var message = hm.util.validateName(name.value, '<s:text name="config.tv.studentName" />');
	if (message != null) {
		hm.util.reportFieldError(name, message);
    	name.focus();
    	return false;
	}
	
	if (!checkInputLength(document.getElementById("randText"), '<s:text name="config.validate.code" />')) {
		return false;
	}

	var tvclass = document.getElementById("selectTB");
	if (document.getElementById("selectClass").length == 0){
		hm.util.reportFieldError(tvclass, '<s:text name="error.requiredField"><s:param><s:text name="config.tv.class" /></s:param></s:text>');
    	return false;
	}
	return true;
}

function checkInputLength(element, title) {
	if (element.value.length == 0) {
		hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
		element.focus();
		return false;
	}
	return true;
}

function checkEmailAddress(element, title){
	if (!checkInputLength(element, title)) {
		return false;
	}
	if (!hm.util.validateEmail(element.value.trim())) {
		hm.util.reportFieldError(element, '<s:text name="error.formatInvalid"><s:param>'+title+'</s:param></s:text>');
		element.focus();
		return false;
	}
	return true;
}

var detailsSuccess = function(o) {
	eval("var details = " + o.responseText);
	var leftOption = document.getElementById("leftOptions_selectClass");
	var leftValues = details;
	leftOption.length=0;
	leftOption.length=leftValues.length;
	for(var i = 0; i < leftValues.length; i ++) {
		leftOption.options[i].value=leftValues[i].id;
		leftOption.options[i].text=leftValues[i].value;
	}
	var rightOption = document.getElementById("selectClass");
	rightOption.length=0;
};

var detailsFailed = function(o) {
//	alert("failed.");
};

var callback = {
	success : detailsSuccess,
	failure : detailsFailed
};


function getNewClassList(vhmId) {
	var url = "<s:url action='studentRegist' includeParams='none'/>" + "?operation=changeVhm&classVhmId="+vhmId+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
}

function changeValidateCode(obj) {   
	var timenow = new Date().getTime();   
	document.getElementById(obj).src="randValidateCode.action?d="+timenow;   
}   


</script>
	<div id="content">
		<s:form action="studentRegist">
			<s:hidden name="operation"></s:hidden>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td align="center">
						<span><s:text name="config.teacher.view.registration.title"/></span>
					</td>
				</tr>
				<tr>
					<td align="center"><tiles:insertDefinition name="notes" /></td>
				</tr>
				<tr>
					<td align="center">
						<table cellspacing="0" cellpadding="0" border="0" class="editBox" width="550">
							<tr>
								<td height="10px"></td>
							</tr>
							<tr>
								<td align="left">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="110"><s:text name="config.tv.studentName" /><font color="red"><s:text name="*"/></font></td>
											<td><s:textfield name="dataSource.studentName" size="30"
												onkeypress="return hm.util.keyPressPermit(event,'name');" maxlength="32" />
												<s:text name="config.tv.className.range" /></td>
										</tr>
										<tr style="display:<s:property value="%{showVhmList}"/>">
											<td class="labelT1" width="110"><s:text name="admin.vhmMgr.vhmName" /><font color="red"><s:text name="*"/></font></td>
											<td><s:select name="classVhmId" list="%{lstVhm}" id="vhmNames" listKey="id"
												listValue="value" cssStyle="width: 180px" onchange="getNewClassList(this.options[this.selectedIndex].value)" /></td>
										</tr>
										<tr>
											<td class="labelT1"><s:text name="config.hp.description" /></td>	
											<td><s:textfield name="dataSource.description" size="50" maxlength="256" />
												<s:text name="config.tv.description.range" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="left">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="labelT1" width="110"><s:text name="config.validate.code" /><font color="red"><s:text name="*"/></font></td>
											<td><s:textfield id="randText" name="rand" size="5" maxlength="4"></s:textfield></td>
											<td><img id="validateImage" onclick="changeValidateCode('validateImage')" title="Click to refresh code"/></td>
											<td><a href="javascript:void(0);" onclick="changeValidateCode('validateImage')" title="Click to refresh code">Refresh code</a></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
							<td style="padding:4px 20px 0 20px" align="center">
								<fieldset><legend><s:text 
										name="config.teacherView.student.register.class" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr><td><label id="selectTB"></label></td></tr>
											</table>
											</td>
										</tr>
										<tr>
											<s:push value="%{classOptions}">
												<td colspan="3"><tiles:insertDefinition name="optionsTransfer"/></td>
											</s:push>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
									</table>
								</fieldset>
							</td>
							</tr>
							<tr>
								<td height="5px"></td>
							</tr>
							<tr>
								<td align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><input type="button" name="save" value="Submit" class="button"
												onClick="submitAction();" <s:property value="disableRegister" />></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td height="10px"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>