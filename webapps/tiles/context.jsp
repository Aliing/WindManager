<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div style="display:none" id="hiddenDiv"><s:hidden
	name="operation" /><s:hidden name="id" /><s:hidden name="tabId" /><s:hidden
	name="forward" /><s:hidden name="tableId" /><s:hidden name="formChanged" /><s:hidden 
	name="paintbrushSource" id="paintbrushSource" /><s:hidden 
	name="paintbrushSourceName" id="paintbrushSourceName" /></div>
<s:if test="%{tableId > 0}">
	<div id="culumnsPanel" style="display:none">
	<div class="hd">Customize Table Columns</div>
	<div class="bd">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
			<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
				width="100%">
				<tr>
					<td style="padding: 6px 5px 5px 10px;">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td><b>Available Columns</b></td>
							<td></td>
							<td><b>Selected Columns</b></td>
						</tr>
						<tr>
							<td height="2"></td>
						</tr>
						<tr>
							<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><select multiple="multiple" id="leftTableColumns" size="12"
										style="width: 190px;">
										<s:iterator value="%{availableColumns}">
											<option value="<s:property value="%{columnId}" />"><s:property
												value="%{columnDescription}" /></option>
										</s:iterator>
									</select></td>
								</tr>
							</table>
							</td>
							<td valign="center" style="padding: 0 5px 0 4px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><input type="button" class="transfer" value="&gt;"
										onclick="columnsChanged = true;hm.options.moveSelectedOptions(document.getElementById('leftTableColumns'),
					                             			document.getElementById('rightTableColumns'), false, '', '', 0);" />
									</td>
								</tr>
								<tr>
									<td><input type="button" class="transfer" value="&lt;"
										onclick="columnsChanged = true;hm.options.moveSelectedOptions(document.getElementById('rightTableColumns'),
					                             			document.getElementById('leftTableColumns'), false, '', '', 0);" />
									</td>
								</tr>
							</table>
							</td>
							<td>
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><select multiple="multiple" id="rightTableColumns" size="12"
										style="width: 190px;">
										<s:iterator value="%{selectedColumns}">
											<option value="<s:property value="%{columnId}" />"><s:property
												value="%{columnDescription}" /></option>
										</s:iterator>
									</select></td>
								</tr>
							</table>
							</td>
							<td valign="center" style="padding: 0px 1px 0 4px;">
							<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td><input type="button" class="moveRow" value="Up"
										onclick="columnsChanged = true;hm.options.moveOptionUp(document.getElementById('rightTableColumns'), '', '');" /></td>
								</tr>
								<tr>
									<td><input type="button" class="moveRow" value="Down"
										onclick="columnsChanged = true;hm.options.moveOptionDown(document.getElementById('rightTableColumns'), '', '');" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="4px"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 8px;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" name="ignore" value="Save"
						class="button" onClick="saveColumns();"></td>
					<td><input type="button" name="ignore" value="Reset"
						class="button" onClick="resetColumns();"></td>
					<td><input type="button" name="ignore" value="Cancel"
						class="button"
						onClick="cancelColumns();"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</div>
	</div>
	<script type="text/javascript"
		src="<s:url value="/js/hm.options.js" includeParams="none" />?v=<s:property value='verParam' />"></script>
	<script type="text/javascript">
	var culumnsPanel = null;
	var columnsOperation = false;
	var columnsChanged = false;
	function createColumnsPanel() {
	var div = document.getElementById('culumnsPanel');
		culumnsPanel = new YAHOO.widget.Panel(div, {
			width:"540px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true
			});
		culumnsPanel.render(document.body);
		div.style.display = "";
		culumnsPanel.hideEvent.subscribe(closeColumnsPanel);
		overlayManager.register(culumnsPanel);
		culumnsPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
	}
	function showColumnsPanel() {
		if(null == culumnsPanel){
			createColumnsPanel();
		}
		culumnsPanel.cfg.setProperty('visible', true);
	}
	function closeColumnsPanel() {
		if (!columnsOperation && columnsChanged) {
			createColumnIds();
			submitAction('cancelColumns');
		}
	}
	function saveColumns() {
		columnsOperation = true;
		culumnsPanel.cfg.setProperty('visible', false);
		createColumnIds();
		submitAction('saveColumns');
	}
	function resetColumns() {
		columnsOperation = true;
		culumnsPanel.cfg.setProperty('visible', false);
		createColumnIds();
		submitAction('resetColumns');
	}
	function cancelColumns() {
		culumnsPanel.cfg.setProperty('visible', false);
		<%-- a hideEvent will follow which will call closeColumnsPanel()
			 and perform the cancel operation if required. --%>
	}
	function createColumnIds() {
		var div = document.getElementById('hiddenDiv');
		var select = document.getElementById('rightTableColumns');
		for (var i = 0; i < select.length; i++) {
			var input = document.createElement("input");
			input.type = "hidden";
			input.name = "selectedColumnIds";
			input.value = select.options[i].value;
			div.appendChild(input);
	    }
	}
	var getColumnOptionId = function(columnObj) {
		return columnObj.columnId;
	}
	var getColumnOptionName = function(columnObj) {
		return columnObj.columnDescription;
	}
	function initializeColumns(leftColumns, rightColumns) {
		var leftSelectObj = document.getElementById("leftTableColumns");
		var rightSelectObj = document.getElementById("rightTableColumns");
		hm.util.resetSelectionValues(leftSelectObj, leftColumns, getColumnOptionId, getColumnOptionName, false);
		hm.util.resetSelectionValues(rightSelectObj, rightColumns, getColumnOptionId, getColumnOptionName, false);
	}
	function encapColumnOption(id, value) {
		var obj = {};
		obj.columnId = id;
		obj.columnDescription = value;
		return obj;
	}
	
	<s:if test="%{enablePageAutoRefreshSetting}">
	function doAutoRefreshSettingSubmit() {
		if (!shouldDoAutoRefreshSetting()) {
			return;
		}
		var el = document.getElementById("autoRefreshSetting");
		var el1 = document.getElementById("pageRefInterval");
		if (el && el1) {
			var blnAutoRefresh = false;
			if (el.checked) {
				blnAutoRefresh = true;
			}
			var postfix = "operation=autoRefreshSetting&pageAutoRefresh="+blnAutoRefresh+"&pageRefInterval="+el1.value;
			if (hm.util.isAFunction(doCustomAutoRefreshSettingSubmit)) {
				enableOrDisableAutoRefreshSetting(false);
				doCustomAutoRefreshSettingSubmit(postfix);
			}
		}
	}
	function updateAutoRefreshStatus(blnAutoRefresh) {
		enableOrDisableAutoRefreshSetting(true);
		var el = document.getElementById("autoRefreshSetting");
		var el1 = document.getElementById("configRefInterval");
		if (blnAutoRefresh) {
			if (el && el1) {
				el.checked = true;
				el1.style.display = "";
			}
		} else {
			if (el && el1) {
				el.checked = false;
				el1.style.display = "none";
			}
		}
	}
	function enableOrDisableAutoRefreshSetting(enable) {
		var el = document.getElementById("autoRefreshSetting");
		var el1 = document.getElementById("pageRefInterval");
		if (enable) {
			el.disabled = false;
			el1.disabled = false;
		} else {
			el.disabled = true;
			el1.disabled = true;
		}
	}
	var previousAutoRefresh = true;
	<s:if test="%{pageAutoRefresh == false}">
	previousAutoRefresh = false;
	</s:if>
	var previousAutoInterval = <s:property value="%{pageRefInterval}"/>;
	function shouldDoAutoRefreshSetting() {
		var el = document.getElementById("autoRefreshSetting");
		if (previousAutoRefresh != el.checked) {
			previousAutoRefresh = el.checked;
			return true;
		}
		var el1 = document.getElementById("pageRefInterval");
		if (previousAutoInterval != el1.value) {
			previousAutoInterval = el1.value
			return true;
		}
		return false;
	}
	</s:if>
</script>
</s:if>
<s:if test="%{!easyMode || (lastExConfigGuide!='ssid' && exConfigGuideFeature!='hiveapEx' && exConfigGuideFeature!='uploadConfigEx' && exConfigGuideFeature!='manageAPEx') || 
	(exConfigGuideFeature=='uploadConfigEx' && pageCount > 0 && rowCount > 15) || 
	(exConfigGuideFeature=='manageAPEx' && pageCount > 0 && rowCount > 15) ||
	(lstTitle!=null && lstTitle.size>1) ||
	(lastExConfigGuide=='ssid' && lstTitle.size==0) ||
	(lstTitle.size==1 && firstTitleStartLocalUser)}">
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{jsonMode==false}">
				<tr>
					<td><img
						src="<s:url value="/images/rounded/top_left_white.gif" />"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td class="menu_bg"><img width="9" height="20"
						src="<s:url value="/images/spacer.gif" />" alt="" class="dblk" /></td>
				</tr>
			</s:if>
			<s:else>
				<tr>
					<td></td>
				</tr>
				<tr>
					<td></td>
				</tr>
			
			</s:else>
		</table>
		
		</td>
		<td class="menu_bg"></td>
		<td class="menu_bg" style="padding: 3px 0 0px 0;">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<script>insertPageContext();</script>
			</tr>
		</table>
		</td>
		<!-- add auto refresh setting section start -->
		<s:if test="%{enablePageAutoRefreshSetting}">
			<td class="menu_bg" style="padding: 1px 15px 0 15px;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td nowrap>
						<!--<input name="autoRefreshSetting" 
								id="autoRefreshSettingOn" type="radio"
								<s:if test="%{pageAutoRefresh}">checked</s:if> 
								onclick="javascript:doAutoRefreshSettingSubmit();"><label for="autoRefreshSettingOn"><s:text name="config.auto.refresh.on"/></label>&nbsp;
						<input name="autoRefreshSetting" 
								id="autoRefreshSettingOff" type="radio"
								<s:if test="%{pageAutoRefresh == false}">checked</s:if> 
								onclick="javascript:doAutoRefreshSettingSubmit();"><label for="autoRefreshSettingOff"><s:text name="config.auto.refresh.off"/></label>&nbsp;
							-->
						<input name="autoRefreshSetting" style="vertical-align: middle;"
								id="autoRefreshSetting" type="checkbox"
								<s:if test="%{pageAutoRefresh}">checked</s:if> 
								onclick="javascript:doAutoRefreshSettingSubmit();">
						<label for="autoRefreshSetting"><s:text name="config.auto.refresh.title"/></label>&nbsp;
						
						<span id="configRefInterval" <s:if test="%{pageAutoRefresh == false}">style="display:none"</s:if>>
						<label><s:text name="config.auto.refresh.interval"/></label>
						<s:select name="pageRefInterval" id="pageRefInterval" cssStyle="width: 100px;" cssClass="normal"
							value="%{pageRefInterval}" list="%{enumRefIntervalType}" listKey="key"
							onchange="javascript:doAutoRefreshSettingSubmit();" listValue="value"/>&nbsp;
						</span>
					</td>
				</tr>
			</table>
			</td>
		</s:if>
		<!-- add auto refresh setting section end -->
		<s:if test="%{jsonMode==false}">
			<td class="menu_bg" width="50000"></td>
		</s:if>
		<s:else>
			<td width="50000"></td>
		</s:else>
		<s:if test="%{selectedL2Feature.key=='summary'}">
			<td class="menu_bg" style="padding: 3px 0 0px 0;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<script>insertSummaryPageContext();</script>
				</tr>
			</table>
			</td>
		</s:if>
		
		<!-- add icon for table customization, joseph 10/30/2008 -->
		<s:if test="%{tableId > 0 && (page != null) && (exConfigGuideFeature == null)}">
		<td class="menu_bg">
		<table border="0" cellspacing="0" cellpadding="0" id="editTable">
			<tr>
				<td style="padding-top: 1px;"><a href="javascript: showColumnsPanel();">
					<img src="<s:url value="/images/edit_table/EditTable-bold.png" />"
						onMouseOver="this.src='<s:url value="/images/edit_table/EditTable-fade.png" />'"
						onMouseOut="this.src='<s:url value="/images/edit_table/EditTable-bold.png" />'"
						title="Edit Table" width="24" height="24" border="0px" class="dblk">
					</a></td>
			</tr>
		</table>
		</td>
		</s:if>

		<s:if test="%{pageCount > 0}">
			<tiles:insertAttribute name="paging" />
		</s:if>

		<td>
		<table border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{jsonMode==false}">
				<tr>
					<td><img
						src="<s:url value="/images/rounded/top_right_white.gif" />"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td class="menu_bg"><img width="9" height="20"
						src="<s:url value="/images/spacer.gif" />" alt="" class="dblk" /></td>
				</tr>
			</s:if>
			<s:else>
			<tr>
				<td></td>
				</tr>
				<tr>
					<td></td>
				</tr>
			
			</s:else>
		</table>
		</td>
		
	</tr>
</table>
</s:if>