<%@taglib prefix="s" uri="/struts-tags"%>

<s:form action="reportExport" name="%{chartId}_Form" id="%{chartId}_Form">
<div>
	<div style="margin-bottom:5px;">
		<table cellspacing="0" cellpadding="0" align="center" style="font-size: 12px;">
			<tr>
				<td></td>
				<td class="labelT1" style="padding-left: 0;">
					<span><s:text name="report.networkusage.onceMail.text"/></span>
				</td>
			</tr>
			<tr style="height: 3px;">
				<td colspan="2"></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<s:textfield name="emailAddress" size="45" 
							onkeypress="return hm.util.keyPressPermit(event,'name');" 
							maxlength="128"/>&nbsp;<s:text name="report.reportList.email.emailNoteRange" />
				</td>
			</tr>
			<tr style="height: 8px;">
				<td colspan="2"></td>
			</tr>
			<tr>
				<td></td>
				<td nowrap="nowrap"  class="noteInfo" style="padding-left: 10px"><s:text
						name="report.reportList.email.note" /></td>
			</tr>
			<tr>
				<td></td>
				<td nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
						name="report.reportList.email.emailNote" /></td>
			</tr>
			<tr style="height: 8px;">
				<td colspan="2"></td>
			</tr>
			<tr style="text-align: right;">
				<td colspan="2">
					<span>
						<input id='<s:property value="chartId"/>_pre_send' type="button" value="<s:text name='common.button.send'/>"></input>
					</span>
				</td>
			</tr>
		</table>
	</div>
</div>
</s:form>

<script type="text/javascript">
YAHOO.util.Event.onContentReady("<s:property value="chartId"/>_Form", function() {
	var formName_chart_tmp = "<s:property value="chartId"/>_Form";
	var elPrefix_tmp = "<s:property value="chartId"/>_pre";
	var currentChart = (AhReportChart.getCurrentPopUpWinChart());
	var emailInstance = (currentChart.ctlButtons.get("__Email"));
	
	var _el_name_emailAddress = formName_chart_tmp + '_emailAddress';
	var _el_name_btn_send = elPrefix_tmp + '_send';
	
	var doSendEmail = function() {
		if (validateInputs_chart_tmp() === false) {
			focusInputEl();
			return;
		}
		var el = document.getElementById(_el_name_emailAddress);
		if (el) {
			emailInstance.hideConfigPanel();
			emailInstance.sendEmailToAddress(el.value);
		}
	};
	var doCancel = function() {
		emailInstance.hideConfigPanel();
	};
	
	var enterToFinishInput = function(e) {
		var keyCode = e.keyCode ? e.keyCode : (e.which ? e.which : e.charCode);
		if (keyCode == 13) {
			doSendEmail();
		}
	}
	$('#'+_el_name_emailAddress).keyup(function(event){
		enterToFinishInput(event);
	});
	$('#'+_el_name_btn_send).click(function(){
		doSendEmail();
	});
	
	function validateInputs_chart_tmp() {
		var el = document.getElementById(_el_name_emailAddress);
		if (el) {
			var emailTmp = el.value;
			if ($.trim(emailTmp) == '') {
				hm.util.reportFieldError(el, "Please input email address!");
				return false;
			}
			if (hm.util.validateEmail(emailTmp) == false) {
				hm.util.reportFieldError(el, "Please input a valid email address!");
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	function focusInputEl() {
		$('#'+_el_name_emailAddress).focus();
		$('#'+_el_name_emailAddress).select();
	}
	
	focusInputEl();
}, this);
</script>