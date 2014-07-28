<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'auxiliaryFileMgmt';
var uploadPanel = null;

function onLoadPage() {
	//Create file upload Overlay
	var div = document.getElementById('uploadPanel');
	uploadPanel = new YAHOO.widget.Panel(div, { width:"540px",visible:false,draggable:true,constraintoviewport:true,modal:true } );
	var code = document.getElementById('title');
	uploadPanel.cfg.setProperty('x', YAHOO.util.Dom.getX(code) + 100);
	uploadPanel.cfg.setProperty('y', YAHOO.util.Dom.getY(code) + 25);
	uploadPanel.render();
	div.style.display = "";
}

function submitAction(operation) {
	if (operation == 'upload') {
		var file = document.getElementById("localFile");
		if (file.value.length == 0 || file.value.length < 5) {
			hm.util.reportFieldError(file, '<s:text name="error.requiredField"><s:param><s:text name="admin.auxiliary.file.new.dictionary" /></s:param></s:text>');
            file.focus();
		} else {
			document.forms[formName].operation.value = operation;
		    try {
			    document.forms[formName].submit();
			    showProcessing();
			} catch (e) {
				if (e instanceof Error && e.name == "TypeError") {
					hm.util.reportFieldError(file, '<s:text name="error.fileNotExist"></s:text>');
					file.focus();
				}
			}
			changeUploadPanel(false);
		}
	} else {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

function changeUploadPanel(checked) {
	if (!checked) {
		document.getElementById("localFile").value = "";
	}
	if (uploadPanel != null) {
		uploadPanel.cfg.setProperty('visible', checked);
	}
}

</script>

<div id="content">
	<s:form action="auxiliaryFileMgmt" enctype="multipart/form-data"
		method="POST">
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
							<td id="title">
								<input type="button" name="download" value="Download" class="button"
									onClick="submitAction('download');"
									<s:property value="writeDisabled" />>
							</td>
							<s:if test="%{titleName == 'macouiDictionary'}">
								<td>
									<input type="button" name="upload" value="Upload" class="button"
										onClick="changeUploadPanel(true);" <s:property value="writeDisabled4Upload" />>
								</td>
							</s:if>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
		</table>
		<div style="padding-left:10px;height:700px;width:800px;overflow-x:scroll;overflow-y:scroll;background-color:#fff;">
			<pre><s:property value="%{fileDetail}" /></pre>
		</div>
		<div id="uploadPanel" style="display: none;">
		<div class="hd">Upload New MAC OUI Dictionary</div>
		<div class="bd">
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="520px">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td colspan="2" class="noteInfo" style="padding:0 10px 0 10px;"><s:text name="admin.auxiliary.file.new.dictionary.note" /></td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td class="labelT1" width="120px"><s:text name="admin.auxiliary.file.new.dictionary" /></td>
					<td><s:file id="localFile" name="upload" size="50" value="%{upload}" accept="text/plain" />
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="button" name="ignore" value="Upload"
								class="button" onClick="submitAction('upload');"></td>
							<td><input type="button" name="ignore" value="Cancel"
								class="button" onClick="changeUploadPanel(false);"></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4"></td>
				</tr>
			</table>
		</div>
		</div>
	</s:form>
</div>