<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<style>
.emptyStyle {
	width: 3px;
}
</style>
<script>
	var confirmRmDialog = null;
	var detailListMenu = null;
	var formName = 'kddrLog';
	function submitAction(operation) {
		showProcessing();
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}

	function showMenu(src, parentFileName, fileName) {
		if (null == detailListMenu) {
			createDetailListMenu();
		}
		var x = YAHOO.util.Dom.getX(src);
		var y = YAHOO.util.Dom.getY(src);
		detailListMenu.cfg.setProperty("xy", [ x + 60, y + 5 ]);
		detailListMenu.parentFileName = parentFileName;
		detailListMenu.fileName = fileName;
		detailListMenu.show();
	}
	function createDetailListMenu() {
		detailListMenu = new YAHOO.widget.Menu("detailMenu", {
			fixedcenter : false
		});
		var items = [ [ {
			text : "<s:text name="admin.kddrLog.downloadFile"/>"
		}, {
			text : "<s:text name="admin.kddrLog.removeFile"/>"
		} ] ];
		detailListMenu.addItems(items);
		detailListMenu.subscribe('click', onMenuItemClick);
		detailListMenu.render(document.body);
	}

	function onMenuItemClick(p_sType, p_aArguments) {
		var event = p_aArguments[0];
		var menuItem = p_aArguments[1];
		if (menuItem.cfg.getProperty("disabled") == true
				|| detailListMenu.fileName == undefined) {
			return;
		}
		var text = menuItem.cfg.getProperty("text");
		if (text == "<s:text name="admin.kddrLog.downloadFile"/>") {
			downloadFile();
		} else if (text == "<s:text name="admin.kddrLog.removeFile"/>") {
			if (null == confirmRmDialog) {
				initConfirmRmDialog();
			}
			confirmRmDialog.show();
		}
	}

	function initConfirmRmDialog() {
		confirmRmDialog = new YAHOO.widget.SimpleDialog(
				"confirmRmDialog",
				{
					width : "350px",
					fixedcenter : true,
					visible : false,
					draggable : true,
					modal : true,
					close : true,
					text : "<html><body>This operation will remove the selected item.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
					icon : YAHOO.widget.SimpleDialog.ICON_WARN,
					constraintoviewport : true,
					buttons : [ {
						text : "Yes",
						handler : removeFile,
						isDefault : true
					}, {
						text : "&nbsp;No&nbsp;",
						handler : handleNo

					} ]
				});
		confirmRmDialog.setHeader("Confirm");
		confirmRmDialog.render(document.body);
	}

	var removeFile = function() {
		this.hide();
		showProcessing();
		url = "<s:url action='kddrLog' includeParams='none'/>"
				+ "?operation=removeFile&parentFileName="
				+ detailListMenu.parentFileName + "&fileName="
				+ detailListMenu.fileName + "&ignore=" + new Date().getTime();
		location.href = url;
	};

	function downloadFile() {
		url = "<s:url action='kddrLog' includeParams='none'/>"
				+ "?operation=downloadFile&parentFileName="
				+ detailListMenu.parentFileName + "&fileName="
				+ detailListMenu.fileName + "&ignore=" + new Date().getTime();
		location.href = url;
	}
	function insertPageContext() {
		document
				.writeln('<td class="crumb" nowrap><s:text name="config.configTemplate.KddrLogTitle" /></td>');
	}
</script>

<div id="content">
	<s:form action="kddrLog">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td height="4"></td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"width="70%">
						<tr>
							<td style="padding: 5px 4px 0 8px">
								<table cellspacing="0" cellpadding="0" border="0" width="100%"
									class="view">
									<tr>
										<th class="emptyStyle"></th>
										<s:iterator value="%{selectedColumns}">
											<s:if test="%{columnId == 1}">
												<th align="left" nowrap><ah:sort name="deviceName"
														key="admin.kddrLog.deviceName" /></th>
											</s:if>
											<s:if test="%{columnId == 2}">
												<th align="left" nowrap><ah:sort name="fileName"
														key="admin.kddrLog.fileName" /></th>
											</s:if>
											<s:if test="%{columnId == 3}">
												<th align="left" nowrap><ah:sort name="logTimeStamp"
														key="admin.kddrLog.time" /></th>
											</s:if>
										</s:iterator>
										<s:if test="%{showDomain}">
											<th><ah:sort name="parentFileName" key="config.domain" /></th>
										</s:if>
									</tr>
									<s:if test="%{page.size() == 0}">
										<ah:emptyList />
									</s:if>
									<s:iterator value="page" status="status">
										<tiles:insertDefinition name="rowClass" />
										<tr class="<s:property value="%{#rowClass}"/>">
											<td class="emptyStyle"></td>
											<s:iterator value="%{selectedColumns}">
												<s:if test="%{columnId == 1}">
													<td class="list"><s:property value="%{deviceName}" /></td>
												</s:if>
												<s:if test="%{columnId == 2}">
													<td class="list"><a
														onclick="showMenu( this, '<s:property value="%{parentFileName}" />','<s:property value="%{fileName}" />')"
														href="javascript: void 0;"><s:property
																value="%{fileName}" /></a></td>
												</s:if>
												<s:if test="%{columnId == 3}">
													<td class="list"><s:property value="%{logTime}" /></td>
												</s:if>
											</s:iterator>
											<s:if test="%{showDomain}">
												<td class="list"><s:property value="%{parentFileName}" />
												</td>
											</s:if>
										</tr>
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
