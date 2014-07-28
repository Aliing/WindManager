<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
<!--
function focusReportBasicSettings(){
	Get("reportName").focus();
}

function cleanReportBasicSettings(){
	Get("reportName").value = "";
	Get("reportDescription").value = "";
	Get("reportHeader").value = "";
	Get("reportFooter").value = "";
	Get("reportSummary").value = "";
}

function fillReportBasicSettings(id, name, desc, header, footer, summary){
	Get("reportName").value = name ? name : "";
	Get("reportDescription").value = desc ? desc : "";
	Get("reportHeader").value = header ? header : "";
	Get("reportFooter").value = footer ? footer : "";
	Get("reportSummary").value = summary ? summary : "";
	if(id){
		Get("reportName").disabled = true;
	}
}

function validateReportBasicSettings(){
	var reportName = Get("reportName");
	var reportSummary = Get("reportSummary");
	if(!reportName.disabled){
		var message = hm.util.validateNameWithBlanks(reportName.value, '<s:text name="hm.recurreport.config.new.name" />');
		if (message != null) {
		    hm.util.reportFieldError(reportName, message);
		    reportName.focus();
		    return false;
		}
	}
	if (reportSummary.value.length > 1024){
		var message = '<s:text name="error.keyValueRange"><s:param><s:text name="hm.recurreport.config.new.summary" /></s:param><s:param>1024</s:param></s:text>';
	    hm.util.reportFieldError(reportSummary, message);
	    reportSummary.focus();
	    return false;
	}
	return true;
}
//-->
</script>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.name" /><font color="red"><s:text
			name="*" /></font></td>
		<td><s:textfield id="reportName" name="reportName" size="32" maxlength="32" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/> <s:text name="hm.recurreport.config.new.name.note" /></td>
	</tr>
</table>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.desc" /></td>
		<td><s:textfield id="reportDescription" name="reportDescription" size="32" maxlength="128" /> <s:text name="hm.recurreport.config.new.desc.note" /></td>
	</tr>
</table>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.header" /></td>
		<td><s:textfield id="reportHeader" name="reportHeader" size="32" maxlength="128" /> <s:text name="hm.recurreport.config.new.header.note" /></td>
	</tr>
</table>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.footer" /></td>
		<td><s:textfield id="reportFooter" name="reportFooter" size="32" maxlength="128" /> <s:text name="hm.recurreport.config.new.header.note" /></td>
	</tr>
</table>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td valign="top" class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.summary" /></td>
		<td valign="top"><s:textarea id="reportSummary" name="reportSummary" cols="50" rows="8" cssStyle="width: 280px;" /> <s:text name="hm.recurreport.config.new.summary.note" /></td>
	</tr>
	<tr>
		<td height="5px"></td>
	</tr>
</table>