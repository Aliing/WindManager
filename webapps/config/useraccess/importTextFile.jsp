<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'importTextFile';

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		var csvFile = document.getElementById("textFile");
	    try {
	    	if (operation != 'import') {
	    		csvFile.disabled = true;
	    	}
		    document.forms[formName].submit();
		    showProcessing();
		} catch (e) {
			if (e instanceof Error && e.name == "TypeError") {
				hm.util.reportFieldError(csvFile, '<s:text name="error.fileNotExist"></s:text>');
				csvFile.focus();
			}
		}
	}
}

function validate(operation) 
{
	if (operation != 'import') {
		return true;
	}	
	var csvFile = document.getElementById("textFile");
	if (csvFile.value.length == 0) {
		hm.util.reportFieldError(csvFile, '<s:text name="error.requiredField"><s:param><s:text name="config.textfile.title" /></s:param></s:text>');
        csvFile.focus();
        return false;
	}		    
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="titleStr"/></td>');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="importTextFile" enctype="multipart/form-data" method="POST">
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
								<input type="button" name="import" value="Import" class="button"
									onClick="submitAction('import');"
									<s:property value="writeDisabled" />>
							</td>
							<td>
								<input type="button" name="cancel" value="Return" class="button"
									onClick="submitAction('cancel');"
									<s:property value="writeDisabled" />>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="850px">
						<tr>
							<td style="padding:10px 10px 10px 10px">
								<fieldset>
									<legend>
										<s:text name="config.textfile.import" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td colspan="2" class="noteInfo" style="padding:0 5px 0 10px">
													<label>
														<s:property value="noticeInfo" escape="false"/>
													</label>
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
											<!--<tr>
												<td colspan="2" style="padding:0 5px 0 10px">
													<label><FONT color="red">
														<s:text name="config.csvfile.notice" />
													</FONT></label>
												</td>
											</tr>
											--><tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td class="labelT1" width="100">
													<label>
														<s:text name="config.textfile.title" />
													</label>
												</td>
												<td>
													<s:file id="textFile" name="upload"
														accept="text/html,text/plain" size="90" />
												</td>
											</tr>
											<tr>
												<td colspan="2" style="padding-left:10px;word-break:break-all">
													<pre><FONT color="blue">
														<s:property value="resultMessage" />
													</FONT></pre>
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td height="3"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
