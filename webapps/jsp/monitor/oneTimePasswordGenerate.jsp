<%@taglib prefix="s" uri="/struts-tags"%>

<style type="text/css">
body {
	margin:0;
	padding:0;
}
</style>

<script type="text/javascript">
var formGenerateOtp = "generateOtp";
var generateOtpPanel = null;

function createGenerateOtpPanel(){
	var div = document.getElementById("generateOtpPanel");
	generateOtpPanel = new YAHOO.widget.Panel(div, {
		width:"420px",
		visible:false,
		fixedcenter:true,
		draggable:true,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	generateOtpPanel.render();
	div.style.display="";
}

function hideGenerateOtpPanel(){
	if(null != generateOtpPanel){
		generateOtpPanel.hide();
	}
}

function openGenerateOTP(){
	if(null == generateOtpPanel){
		createGenerateOtpPanel();
	}
	document.getElementById("numberOfOtp").value = 1;
	document.getElementById("desOfOtp").value = "";
	generateOtpPanel.show();
}

function validateNumberOfOTP(){
	var inputElement = document.getElementById("numberOfOtp");
	if(inputElement.value.length == 0){
		hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.autoProvisioning.otp.generate.number" /></s:param></s:text>');
		inputElement.focus();
        return false;
	}
	if (inputElement.value.length > 0) {
		var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="hiveAp.autoProvisioning.otp.generate.number" />', 1, 9999);
	    if (message != null) {
	        hm.util.reportFieldError(inputElement, message);
	        inputElement.focus();
	        return false;
	    }
    }
	return true;
}

function submitGenerateOtp(operation){
	if(!validateNumberOfOTP()){
		return false;
	}
	hm.util.show("processingOTP");
	document.forms[formGenerateOtp].operation.value = operation;
	document.forms[formGenerateOtp].submit();
}

</script>


<div id="generateOtpPanel" style="display: none;">
	<div class="hd">
		<s:text name="hiveAp.autoProvisioning.otp.generate.tittle" />
	</div>
	<div class="bd">
		<s:form action="oneTimePassword" id="generateOtp" name="generateOtp">
			<s:hidden name="operation" />
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<div id="processingOTP" style="display:none">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="note">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td class="noteError">Your request is being processed ...</td>
							</tr>
							<tr>
								<td height="6"></td>
							</tr>
						</table>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
							width="100%">
							<tr>
								<td class="labelT1" width="152px">
									<label><s:text name="hiveAp.autoProvisioning.otp.generate.number" /><font color="red">*</font></label>
							   </td>
							   <td  width="300px"><s:textfield name="numberOfOtp" maxlength="4" 
								id = "numberOfOtp" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>&nbsp;<s:text name="hiveAp.autoProvisioning.otp.generate.number.range"/></td>
							</tr>
							<tr>
								<td class="labelT1" width="152px">
									<label><s:text name="hiveAp.autoProvisioning.otp.generate.description" /></label>
							   </td>
							   <td  width="300px"><s:textfield name="desOfOtp" maxlength="64" 
								id = "desOfOtp" />&nbsp;<s:text name="hiveAp.autoProvisioning.otp.generate.description.prompt"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td style="padding-top: 8px;">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<input type="button"  value="Generate" id="generateOTP"
										class="button" onClick="submitGenerateOtp('generate');"/>
								</td>
								<td>
									<input type="button"  value="Cancel"
										class="button" onClick="hideGenerateOtpPanel();"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:form>
	</div>
</div>