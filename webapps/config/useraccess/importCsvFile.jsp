<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'importCsvFile';

function submitAction(operation) {
	if (validate(operation)) {
		document.forms[formName].operation.value = operation;
		var csvFile = document.getElementById("csvFile");
	    try {
	    	if (operation != 'import' && operation != 'download') {
	    		csvFile.disabled = true;
	    	}
		    document.forms[formName].submit();
            if(operation != 'download'){
                showProcessing();
           }
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
	var csvFile = document.getElementById("csvFile");
	if (csvFile.value.length == 0) {
		hm.util.reportFieldError(csvFile, '<s:text name="error.requiredField"><s:param><s:text name="config.csvfile.title" /></s:param></s:text>');
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
	<s:form action="importCsvFile" enctype="multipart/form-data" method="POST">
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
										<s:text name="config.csvfile.import" />
									</legend>
									<div>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td height="10"></td>
											</tr>
											<tr>
												<td colspan="2" class="noteInfo" style="padding:0 5px 0 10px">
												    <s:if test="%{isTeacherView}">
												       <label>
                                                          <s:text name="config.csvfile.message.title" />
                                                       </label>
												    </s:if>
													<label>
														<s:property value="noticeInfo" escape="false"/>
													</label>
												</td>
											</tr>
											<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td colspan="2" style="padding:0 5px 0 10px">
													<label><FONT color="red">
														<s:text name="config.csvfile.notice" />
													</FONT></label>
												</td>
											</tr>
											<s:if test="%{isLocalUserFlag}">
                                                <tr>
	                                                <td class="noteInfo" colspan="2" style="padding:10px 5px 0 10px">
	                                                	<s:text name="gotham.hm.system.log.import.localuser.note.1" />
	                                                    <ul>
	                                                    	<li><s:text name="gotham.hm.system.log.import.localuser.note.2" /></li>
	                                                    	<li><s:text name="gotham.hm.system.log.import.localuser.note.3" /></li>
	                                                    	<li><s:text name="gotham.hm.system.log.import.localuser.note.4" /></li>
	                                                    </ul>
                                                    
	                                                </td>
                                                </tr>
                                            </s:if>
                                            <s:if test="%{isTeacherView}">
                                                <tr>
	                                                <td colspan="2" style="padding:0 5px 0 10px">
	                                                    <label><FONT color="red">
	                                                        <s:text name="config.csvfile.notice.repeat" />
	                                                    </FONT></label>
	                                                </td>
                                                </tr>
                                            </s:if>
											<s:if test="%{isTeacherView}">
												<tr>
	                                                <td class="labelT1" width="120">
	                                                    <label>
	                                                        <s:text name="config.csvfile.download" />
	                                                    </label>
	                                                </td>
	                                                <td>
	                                                   <input type="button" id="ignore" name="ignore" value="<s:text name="common.button.download"/>" class="button" onClick="submitAction('download');">
	                                                </td>
	                                            </tr>
											</s:if>
											<tr>
												<td height="5"></td>
											</tr>
											<tr>
												<td class="labelT1" width="100">
													<label>
														<s:text name="config.csvfile.title" />
													</label>
												</td>
												<td>
													<s:file id="csvFile" name="upload" size="90" />
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
