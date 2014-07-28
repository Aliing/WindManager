<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>
var formName = 'auxiliaryFileMgmt';
var thisOperation;
var viewFile;

function submitAction(operation) {
	thisOperation = operation;
    confirmDownload();
}

function doContinueOper() {  
	if (thisOperation == 'view') {
		if (viewFile.length > 28) {
			showDetails(viewFile.substr(viewFile.indexOf('auxiliaryFileMgmt_selectFile')+28));
		} else {
			var title = document.getElementById("title");
			hm.util.reportFieldError(title, 'There is something wrong with this file.');
			title.focus();
		}
	} else {
		document.forms[formName].operation.value = thisOperation;
		document.forms[formName].submit();
	}
}

function confirmDownload() {
	<s:if test="%{fileList.size() == 0}">
		warnDialog.cfg.setProperty('text', "There is no item to download.");
		warnDialog.show();
	</s:if>
	
	var cbs = document.getElementsByName('selectFile');
	var isHasChecked = false;
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			isHasChecked = true;
			viewFile = cbs[i].id;
		}
	}
	if (!isHasChecked) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		doContinueOper();
	}
}

function showDetails(name) {
	var url = "<s:url action='auxiliaryFileMgmt' includeParams='none'/>"+ "?operation=showDetails&selectFileName=" + name;
	window.open(url, 'newWindow', 'height=600, width=700, scrollbars, resizable, top=250,left=400');
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}

</script>

<div id="content">
	<s:form action="auxiliaryFileMgmt">
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
							<td>
								<input type="button" name="view" value="View" class="button"
									onClick="submitAction('view');" >
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
				<td style="padding-top: 5px;">
					<table class="editBox" cellspacing="0" cellpadding="0" border="0" width="400">
						<tr>
							<td style="padding:10px 10px 10px 10px">
								<table border="0" cellspacing="0" cellpadding="0">
									<s:iterator id="fileTable" value="%{fileList}"
										status="status">
										<s:if test="%{fileName.startsWith('ah')}">
										<tr class="list">
											<td class="listSelect" align="center">
												<s:radio label="Gender" name="selectFile"
													list="#{fileName:''}" value="%{selectFile}" />
											</td>
											<td>
												<a href="javascript: showDetails('<s:property value="%{fileName}" />')">
													<s:property value="%{fileName}" /></a>
											</td>
										</tr>
										</s:if>
									</s:iterator>
								</table>
							</td>
							<td style="padding:10px 10px 10px 10px">
								<table border="0" cellspacing="0" cellpadding="0">
									<s:iterator id="fileTable" value="%{fileList}"
										status="status">
										<s:if test="%{!fileName.startsWith('ah')}">
										<tr class="list">
											<td class="listSelect" align="center">
												<s:radio label="Gender" name="selectFile"
													list="#{fileName:''}" value="%{selectFile}" />
											</td>
											<td>
												<a href="javascript: showDetails('<s:property value="%{fileName}" />')">
													<s:property value="%{fileName}" /></a>
											</td>
										</tr>
										</s:if>
									</s:iterator>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
