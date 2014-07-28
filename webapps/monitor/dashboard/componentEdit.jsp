<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.util.js" />"></script>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div id="dashboardComponentDiv">
	<s:form action="dashboardComponent" id="dashboardComponent" name="dashboardComponent">
		<s:hidden name="operation" />
		<s:hidden name="groupSize" />
		<s:hidden name="groupItemMap" />
		<s:hidden name="groupItemMap" />
		<s:hidden name="xdataSelect"/>
		<s:hidden name="specifyType"/>
		<s:hidden name="specifyName"/>
		<s:hidden name="enableBreakdowns"/>
		<s:hidden name="validBreakdowns"/>
		<s:hidden name="enableDisplayTotals"/>
			
		<div id="componentDivError"></div>
		
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			 <tr>
				<td height="250px">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td><s:checkbox name="dataSource.realDaComponent.enableExampleData" id="enableExampleData" disabled="%{disableForMetric}"></s:checkbox>
								<label for="enableExampleData"><s:text name="hm.dashboard.component.config.example.data"/></label>
							</td>
							<td colspan="2"></td>
						</tr>
						<tr>
							<td height="20px"></td>
						</tr>
						<tr>
							<td><s:text name="hm.dashboard.component.config.header.data"/></td>
							<td colspan="2"><s:text name="hm.dashboard.component.config.header.display.name"/></td>
						</tr>
						<tr>
							<td ><s:select id="widgetConfigSourceData" name="widgetConfigSourceData" 
								list="%{sourceDataList}" listKey="key" title='%{getText("hm.dashboard.component.config.header.data.tip")}'
								listValue="value" cssStyle="width: 180px;"
								onchange="changeSourceData(this);" disabled="%{disableForMetric}"></s:select></td>
							<td colspan="2">
								<s:textfield id="widgetConfigDisplayName" name="dataSource.realDaComponent.displayName"
									 size="70" title="%{getText('hm.dashboard.component.config.header.display.name.tip')}" 
									 placeholder="%{getText('hm.dashboard.component.config.header.display.name.tip')}"
									 onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
							</td>
							
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><s:text name="hm.dashboard.component.config.header.display.value"/></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<s:textfield id="widgetConfigDisplayValue" name="dataSource.realDaComponent.displayValue" 
								placeholder='%{getText("hm.dashboard.component.config.header.display.value.tip")}'
								title='%{getText("hm.dashboard.component.config.header.display.value.tip")}' size="70" />
								<s:hidden id="widgetConfigDisplayValueKey" name="dataSource.realDaComponent.displayValueKey"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td height="10px"></td>
						</tr>
						<tr>
							<td colspan="3">
								<fieldset><legend>Metric Sets</legend>
								<table cellspacing="0" cellpadding="0" width="100%" height="100%">
									<tr>
										<td colspan="3" >
											<div style="width:695px; height:250px;overflow-x:hidden;overflow-y:scroll;margin-top: 5px;">
												<table width="100%" height="100%" id="table_group_id" >
													<s:iterator value="%{dashboardComponentGroupMap}" id="group" status="status">
													<tr id="group_<s:property value='%{#status.index+1}' />" >
														<td style="vertical-align: top;">
															<fieldset><legend><span id="setCount_<s:property value='%{#status.index+1}' />">Metric Set<s:property value='%{#status.index+1}' /></span></legend>
															<table cellspacing="0" cellpadding="0" width="100%" height="100%">
															    <tr style="padding-left:20px;">
															    	<td colspan="5">
															    		<s:hidden id="dashboardComponentGroupId_<s:property value='%{#status.index+1}' />" 
															    			value="%{#status.index+1}" name="dashboardComponentGroupIds" />
															    	</td>
															    </tr>  
																<s:iterator value="#group.value" status="status2">
																<tr id="item_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />" >
																	<td><span id="itemCount_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />">Metric <s:property value='%{#status2.index+1}' /></span></td>
																	<td class="list" >
																		<s:select id="widgetConfigItemData_%{#status.index+1}_%{#status2.index+1}" name="sourceDatas" list="%{sourceDataList}"
																			 listKey="key" listValue="value" value="%{sourceData}" disabled="%{defaultMetricFlag}"
																			 onchange="changeItemData(this);" 
																			 cssStyle="width: 130px;" cssClass="sourceDataSelectCss" ></s:select>
																	</td>
																	<td class="list" >
																		<s:textfield id="displayName_%{#status.index+1}_%{#status2.index+1}" name="displayNames" value="%{displayName}"
																			 title="%{getText('hm.dashboard.component.config.metric.display.name.tip')}"
																			 size="35" disabled="%{defaultMetricFlag}" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');" />
																	</td>
																	<td class="list" >
																		<s:checkbox id="enableBreakdown_%{#status.index+1}_%{#status2.index+1}" name="enableBreakdownCheck"
																			 value="%{enableBreakdown}" disabled="%{!validBreakdown || defaultMetricFlag}" onclick="checkBreakdown(this);"></s:checkbox>
																		<label for="enableBreakdown_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />"><s:text name="hm.dashboard.component.config.datasource.breakdown"/></label>
																	</td>
																	<td   height="18px;">
																		<a href="javascript:void(0);" id="itemDelete_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />" 
																			onclick="removeItem(this);">
																			<img width="16" height="16" style="border: 0;" src="/hm/images/cancel.png" title="Cancel">
																		</a>
																	</td>
																</tr>
																<tr id="item_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />_with">
																	<td></td>
																	<td class="list" >
																		&nbsp;
																	</td>
																	<td class="list">
																			<s:textfield id="displayValue_%{#status.index+1}_%{#status2.index+1}" name="displayValues" 
																				title="%{getText('hm.dashboard.component.config.metric.display.value.tip')}"
																				value="%{displayValue}" size="35" disabled="%{defaultMetricFlag}"/>
																		<s:hidden id="keyValue_%{#status.index+1}_%{#status2.index+1}" name="displayValuesKey" value="%{displayValueKey}"/>
																	</td>
																	<td class="list">
																		<div style="display:<s:property value="%{enableBreakdown?'':'none'}"/>">
																			<s:textfield id="levelBreakDown_%{#status.index+1}_%{#status2.index+1}" name="levelBreakDowns" cssStyle="width:20px;" value="%{levelBreakDown}"
																				onkeypress="return hm.util.keyPressPermit(event,'integer');" disabled="%{defaultMetricFlag}"/>
																			<s:text name="hm.dashboard.component.config.levels"/>
																			<s:checkbox id="enableDisplayTotal_%{#status.index+1}_%{#status2.index+1}" name="enableDisplayTotalCheck"
																				 value="%{enableDisplayTotal}" disabled="%{defaultMetricFlag}"></s:checkbox>
																			<label for="enableDisplayTotal_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />"><s:text name="hm.dashboard.component.config.display.total" /></label>
																		</div>
																	</td>
																	<td >
																		&nbsp;
																	</td>
																</tr>
																<tr id="item_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />_without">
																	<td width="20" height="18" colspan="5">
																		<a href="javascript:void(0);" id="itemAdd_<s:property value='%{#status.index+1}' />_<s:property value='%{#status2.index+1}' />" onclick="addItem(this);">
																			<img width="10" height="10" style="border: 0;" src="/hm/images/new.png" title="New">
																			<s:text name="hm.dashboard.component.config.addMetric.stacked"/>
																		</a>
																	</td>
																</tr>
																</s:iterator>
															</table>
															</fieldset>
														</td>
														<td style="border-width:0px;vertical-align:bottom;text-align:right;" width="18" height="18">
															<a href="javascript:void(0);" id="groupDelete_<s:property value='%{#status.index+1}' />" onclick="removeGroup(this);">
																<img width="16" height="16" style="border: 0;" src="/hm/images/cancel.png" title="Cancel">
															</a>
														</td>  
													</tr> 
													<tr id="groupAdd_<s:property value='%{#status.index+1}' />" height="10px">
														<td colspan="2" style="vertical-align: bottom;">
															<a href="javascript:void(0);" id="groupAdd_<s:property value='%{#status.index+1}' />" onclick="addGroup(this);">
																<img width="10" height="10" style="border: 0;" src="/hm/images/new.png" title="New">
																<s:text name="hm.dashboard.component.config.addMetric.comparison"/>
															</a>
														</td>
													</tr>
													</s:iterator>
													<tr id="groupEmpty" style="display:none"><td colspan="2"></td></tr>
												</table>
											</div>
										</td>
									</tr>
									<tr>
										<td colspan="3" style="padding-top:5px;padding-bottom:10px;padding-right:10px;">
											<div>
												<table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
													<tr style="text-align:left; vertical-align:bottom;" height="20px;">
													   <td width="25%">
													   </td>
													   <td>
													   		<label><s:text name="hm.dashboard.component.config.metric.name"/> </label>
															<s:textfield id="premadeMetricName" name="metricName" maxlength="64" size="48" 
																title="%{getText('hm.dashboard.component.config.save.premade.metric.tip')}"
																placeholder="%{getText('hm.dashboard.component.config.save.premade.metric.tip')}"
																onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
													   		<input type="button" value='<s:text name="hm.dashboard.component.config.save.premade.metric"/>' 
																class="button" style="width:120px;" onClick="savePremadeMetric('savePremadeMetric');"/>
													   </td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
								</table>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td colspan="3" style="text-align:right;padding-top:20px;" >
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td colspan="2">
										</td>
										<td align="left">
											<table>
												<tr>
													<td id="premadeWidgetNameNote"></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
									    <td width="15px" style="display:<s:property value="displayHtmlCk"/>"> 
									    	<s:checkbox name="dataSource.realDaComponent.enabledHtml" id="customHtmlACk"
									    	 	disabled="%{disabledHtmlCk}" onclick="clickEnabledHtml(this.checked);">
									    	</s:checkbox>
									    </td>
									    <td align="left" style="display:<s:property value="displayHtmlCk"/>" width="220px"> 
									    	<a href="javascript:void(0);" id="customHtmlAItem" onclick="clickCustomHtmlLink();" 
									    		class='<s:property value="htmlLinkClass"/>'>
									    	<s:text name="hm.dashboard.component.config.customHtml"/></a>
									    </td>
									    <td align="left">
									    	<label><s:text name="hm.dashboard.component.config.widget.name"/> </label> 
									    	<s:textfield id="premadeWidgetName" name="widgetName" maxlength="64"  size="48"
									    		title="%{getText('hm.dashboard.component.config.save.premade.widget.tip')}"
									    		placeholder="%{getText('hm.dashboard.component.config.save.premade.widget.tip')}"
									    		 onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
									   		<input type="button"  value='<s:text name="hm.dashboard.component.config.save.premade.widget"/>' 
												class="button" style="width:100px;" onClick="savePremadeWidget('savePremadeWidget');"/>
									    </td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr> 
			<tr>
				<td style="padding-top: 8px;text-align:center;">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<s:if test="%{dataSource.id == null}">
							<td>
								<input type="button"  value="Ok" class="button"
									 onClick="submitDashboardComponent('create');" <s:property value="defaultWidgetFlag" />/>
							</td>
							</s:if>
							<s:else>
							<td>
								<input type="button" value="Ok" class="button"
									 onClick="submitDashboardComponent('update');" <s:property value="defaultWidgetFlag" />/>
							</td>
							</s:else>
							<td>
								<input type="button"  value="Cancel"
									class="button" onClick="hideDashboardComponentPanel();"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<!-- Template -->
<div id="widgetConfigTemplateDiv" style="display:none">
	<table id="source_template" style="border:1px #000000 solid;">
		<tr id="group_template">
			<td style="vertical-align: top;">
				<fieldset><legend><span id="setCount_template"></span></legend>
				<table cellspacing="0" cellpadding="0" width="100%" height="100%">
					<tr id="item_display_template" >
						<td ><span id="itemCount_template"></span></td>
						<td class="list">
							<s:select id="widgetConfigItemData_template" name="sourceDatas" list="sourceDataList" 
								cssStyle="width: 130px;" title="%{sourceData}" 
								listKey="key" listValue="value"
								onchange="changeItemData(this);"
								cssClass="sourceDataSelectCss"></s:select>
						</td>
						<td class="list">
							<s:textfield id="displayName_template" name="displayNames" placeholder="%{getText('hm.dashboard.component.config.metric.display.name.tip')}"
								title="%{getText('hm.dashboard.component.config.metric.display.name.tip')}"
								size="35" onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"/>
						</td>
						<td class="list" >
							<s:checkbox id="enableBreakdown_template" name="enableBreakdownTemplate" onclick="checkBreakdown(this);"></s:checkbox>
							<label for="enableBreakdown_template"><s:text name="hm.dashboard.component.config.datasource.breakdown"/></label>
						</td>
						<td >
							<a href="javascript:void(0);" id="itemDelete_template" onclick="removeItem(this);">
							<img width="14" height="14"  style="border:0;" src="/hm/images/cancel.png" title="Delete">
							</a>
						</td>
					</tr>
					<tr id="item_with_template">
						<td></td>
						<td class="list" >
							&nbsp;
						</td>
						<td class="list" >
							<s:textfield id="displayValue_template" name="displayValues" size="35"
								placeholder="%{getText('hm.dashboard.component.config.metric.display.value.tip')}"
								title="%{getText('hm.dashboard.component.config.metric.display.value.tip')}"/>
							<s:hidden id="keyValue_template" name="displayValuesKey"/>
						</td>
						<td class="list">
							<div style="display:none">
								<s:textfield id="levelBreakDown_template" name="levelBreakDowns" cssStyle="width:15px;" 
								onkeypress="return hm.util.keyPressPermit(event,'integer');" value="0"/>
							<s:text name="hm.dashboard.component.config.levels"/>
							<s:checkbox id="enableDisplayTotal_template" name="enableDisplayTotalTemplate" ></s:checkbox>
							<label for="enableDisplayTotal_template"><s:text name="hm.dashboard.component.config.display.total"/></label>
							</div>
						</td>
						<td   width="20" height="14" >
							&nbsp;
						</td> 
					</tr>
					<tr id="item_without_template">
						<td height="20px;" style="padding-top:3px;padding-bottom:5px;" colspan="5">
							<a href="javascript:void(0);" id="itemAdd_template" onclick="addItem(this);" >
								<img width="10" height="10" style="border: 0;" src="/hm/images/new.png" title="New">
								<s:text name="hm.dashboard.component.config.addMetric.stacked"/>
							</a>
						</td>
					</tr>
					<tr id="itemEmpty" style="display:none"><td></td></tr>
				</table>
				</fieldset>
			</td>
			<td style="border-width:0px;vertical-align:bottom;text-align:right;" width="18" height="10">
				<a href="javascript:void(0);" id="groupDelete_template" onclick="removeGroup(this);">
				<img width="14" height="14" style="border: 0;" src="/hm/images/cancel.png" title="Delete">
				</a>
			</td>
		</tr>
		<tr id="group_template_add">
			<td height="10px" colspan="2" style="vertical-align: bottom;">
				<a href="javascript:void(0);" id="groupAdd_template"  onclick="addGroup(this);">
					<img width="10" height="10" style="border: 0;" src="/hm/images/new.png" title="New">
					<s:text name="hm.dashboard.component.config.addMetric.comparison"/>
				</a>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
var formComponent = "dashboardComponent";
var groupCount;
var defaultYaxis;

YAHOO.util.Event.onDOMReady(function(){
	groupCount = <s:property value="%{dashboardComponentGroupMap.size()}"/>;
});

function clickEnabledHtml(checked){
	if(checked){
		Get("customHtmlAItem").className="";
	} else {
		Get("customHtmlAItem").className="customHtmlAItemDisable";
	}
}

function clickCustomHtmlLink(){
	if(!Get("customHtmlACk").checked) {
		return false;
	}
	openCustomHtmlPanel();
}

function isEmpty(o){
	for(key in o){
		return false;
	}
	return true;
}

function isBreakdown(yaxis){
	if(!isEmpty(hm.da.breakdownObj)){
		for(key in hm.da.breakdownObj){
			if(key == yaxis){
				return hm.da.breakdownObj[key];
			}
		}
	}
	return false;
}

function initWidgetConfig(){
	//set the value of data
	hm.da.fillAxisSelect(Get("widgetConfigSourceData"),'<s:property value="%{xdataSelect}"/>','<s:property value="%{specifyType}"/>');
    
	//set the value of yaxis
	$('select.sourceDataSelectCss').each(
		function (){
			hm.da.fillSourceDataSelect(this,'<s:property value="%{xdataSelect}"/>');
			//$(this).val($(this).attr("title"));
	});
	
	//select the default yaxis
	initYaxisSelectedData();
	
	//add default group
	addGroup();
	
	disableDefaultMetric();
}

function disableDefaultMetric(){
	<s:if test="%{defaultMetricFlag}">
		$("a[id^=groupDelete_]").each(function (){
			if(this.id != "groupDelete_template"){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel_disable.png");
			}
			this.onclick = null;
		});
		
		$("a[id^=groupAdd_]").each(function (){
			if(this.id != "groupAdd_template"){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/new_disable.png");
			}
			
			this.onclick = null;
		});
		
		$("a[id^=itemDelete_]").each(function (){
			if(this.id != "itemDelete_template"){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel_disable.png");
				
			}
			this.onclick = null;
		});
		
		$("a[id^=itemAdd_]").each(function (){
			if(this.id != "itemAdd_template"){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/new_disable.png");
				
			}
			this.onclick = null;
		});
	</s:if>
}

function initYaxisSelectedData(){
	if(Get("widgetConfigSourceData").value == -1){
		Get("widgetConfigDisplayName").value = "";
		Get("widgetConfigDisplayValue").value = "";
	}
	defaultYaxis = hm.da.showDefaultYaxis(Get("widgetConfigSourceData").value);
	Get("widgetConfigItemData_template").value = defaultYaxis;
	Get("enableBreakdown_template").disabled = !isBreakdown(defaultYaxis);
	Get("widgetConfigItemData_template").onchange(); 
}


function checkBreakdown(ele){
	var idIndex = ele.id;
	idIndex = idIndex.substr(idIndex.indexOf("_"));
	if(ele.checked){
		document.getElementById("levelBreakDown"+idIndex).parentNode.style.display="";
		document.getElementById("levelBreakDown"+idIndex).focus();
		document.getElementById("levelBreakDown"+idIndex).select();
	}else{
		document.getElementById("levelBreakDown"+idIndex).parentNode.style.display="none";
	}
	
}

function changeSourceData(ele) {
	//show the default value of display name/option and value
	if(ele && ele.value){
		if(ele.value != -1){
			Get("widgetConfigDisplayName").value = hm.da.showDefaultSourceDisplayName(ele.value);
			Get("widgetConfigDisplayValue").value = AeroHive.metric(ele.options[ele.selectedIndex].text);    // "["+value+"]";
		}else{
			Get("widgetConfigDisplayName").value = "";
			Get("widgetConfigDisplayValue").value = "";
		}
		//fill the option of yaxis
		hm.da.fillSourceDataSelect(Get("widgetConfigItemData_template"),ele.value);
		$('table#table_group_id tr[id^=group_]').remove();
		$('table#table_group_id tr[id^=groupAdd_]').remove();
		groupCount=0;
		//select the default yaxis
		initYaxisSelectedData();
		//add corresponding group
		addGroup();
	}
}

function addGroup(ele){
	var emptyRow = Get("groupEmpty");
	var newTemplate = Get("group_template").cloneNode(true);
	var newTemplateAdd = Get("group_template_add").cloneNode(true);
	
	if(emptyRow && newTemplate && newTemplateAdd){
		if(0 == groupCount){
			newTemplate.id = "group_1";
			newTemplateAdd.id = "groupAdd_1";
			groupCount++;
			modifyRowDomAttr(newTemplate,1);
			modifyRowDomAttr(newTemplateAdd,1);
			YAHOO.util.Dom.insertBefore(newTemplate, emptyRow);
			YAHOO.util.Dom.insertAfter(newTemplateAdd,newTemplate);
		}else{
			var index;
			if(ele && ele.id){
				index = ele.id;
				index = index.substr(index.indexOf("_"));
				
				//default group and item does not need to validate.
				if(!validate('addGroup')){
					return false;
				}
				
				var suffix = groupCount++ +1;
				newTemplate.id = "group_"+suffix;
				newTemplateAdd.id = "groupAdd_"+suffix;
				modifyRowDomAttr(newTemplate,suffix);
				modifyRowDomAttr(newTemplateAdd,suffix);
				//add new group
				var currentRow = Get("groupAdd"+index);
				if(currentRow){
					YAHOO.util.Dom.insertAfter(newTemplate,currentRow);
					YAHOO.util.Dom.insertAfter(newTemplateAdd,newTemplate);
				}
			}
		}
	}
	checkDeleteDisable();
}

function modifyTrAttr(specificRow,suffix,itemSuffix){
	var elements = specificRow.getElementsByTagName("tr");
	for(var index=0; index<elements.length; index++){
		if(elements[index].id == "item_display_template"){
			elements[index].id = "item_"+suffix+"_"+itemSuffix;
		}else if(elements[index].id == "item_with_template"){
			elements[index].id = "item_"+suffix+"_"+itemSuffix+"_with";
		}else if(elements[index].id == "item_without_template"){
			elements[index].id = "item_"+suffix+"_"+itemSuffix+"_without";
		}
	}
}

function modifyAlinkAttr(specificRow,suffix,itemSuffix){
	var groupOperates = specificRow.getElementsByTagName("a");
	for(var i = 0;i<groupOperates.length;i++){
		if(groupOperates[i].id == "groupDelete_template"){
			groupOperates[i].id = "groupDelete_"+suffix;
		}else if(groupOperates[i].id == "groupAdd_template"){
			groupOperates[i].id = "groupAdd_"+suffix;
		}else if(groupOperates[i].id == "itemAdd_template"){
			groupOperates[i].id = "itemAdd_"+suffix+"_"+itemSuffix;
		}else if(groupOperates[i].id == "itemDelete_template"){
			groupOperates[i].id = "itemDelete_"+suffix+"_"+itemSuffix;
		}
	}
}

function modifyCheckboxAttr(specificRow,suffix,itemSuffix){
	var radioItems = specificRow.getElementsByTagName("input");
	for(var j = 0;j<radioItems.length;j++){
		if("checkbox" == radioItems[j].type){
			if(radioItems[j].id == "enableBreakdown_template"){
				radioItems[j].id = "enableBreakdown_"+suffix+"_"+itemSuffix;
				radioItems[j].name="enableBreakdownCheck";
			}else if(radioItems[j].id == "enableDisplayTotal_template"){
				radioItems[j].id = "enableDisplayTotal_"+suffix+"_"+itemSuffix;
				radioItems[j].name="enableDisplayTotalCheck";
			}
		}
	}
	//set label for checkbox
	var labelItems = specificRow.getElementsByTagName("label");
	for(var j = 0;j<labelItems.length;j++){
		var forVaule=labelItems[j].getAttributeNode("for").value;
		if(forVaule == "enableBreakdown_template"){
			labelItems[j].setAttribute("for","enableBreakdown_"+suffix+"_"+itemSuffix);
		}else if(forVaule == "enableDisplayTotal_template"){
			labelItems[j].setAttribute("for","enableDisplayTotal_"+suffix+"_"+itemSuffix);
		}
	}
}

function modifyHiddenAttr(specificRow,suffix,itemSuffix){
	var valueKey = specificRow.getElementsByTagName("input");
	for(var j = 0;j<valueKey.length;j++){
		if("hidden" == valueKey[j].type){
			if(valueKey[j].id == "validBreakdown_template"){
				valueKey[j].id = "validBreakdown_"+suffix+"_"+itemSuffix;
				valueKey[j].name = "validBreakdowns";
				valueKey[j].value = isBreakdown(defaultYaxis);
			}
		}
	}
}

function modifySelectAttr(specificRow,suffix,itemSuffix){
	var selectItems = specificRow.getElementsByTagName("select");
	if(selectItems.length >0){
		if(selectItems[0].id == "widgetConfigItemData_template"){
			selectItems[0].id = "widgetConfigItemData_"+suffix+"_"+itemSuffix;
			selectItems[0].value = defaultYaxis;
		}
	}
}

function modifyTextInputAttr(specificRow,suffix,itemSuffix){
	var displayNameItems = specificRow.getElementsByTagName("input");
	for(var j = 0;j<displayNameItems.length;j++){
		if("text" == displayNameItems[j].type){
			if(displayNameItems[j].id == "displayName_template"){
				displayNameItems[j].id = "displayName_"+suffix+"_"+itemSuffix;
			}else if(displayNameItems[j].id == "displayValue_template"){
				displayNameItems[j].id = "displayValue_"+suffix+"_"+itemSuffix;
			}else if(displayNameItems[j].id == "levelBreakDown_template"){
				displayNameItems[j].id = "levelBreakDown_"+suffix+"_"+itemSuffix;
			}/*else if(displayNameItems[j].id == "levelBreakDownWithout_template"){
				displayNameItems[j].id = "levelBreakDownWithout_"+suffix+"_"+itemSuffix;
			} */
		}else if("hidden" == displayNameItems[j].type){
			if(displayNameItems[j].id == "keyValue_template"){
				displayNameItems[j].id = "keyValue_"+suffix+"_"+itemSuffix;
			}
		}
	}
}

function modifySpanAttr(specificRow,suffix,itemSuffix){
	var spanItems = specificRow.getElementsByTagName("span");
	for(var j=0;j<spanItems.length;j++){
		if(spanItems[j].id == "setCount_template"){
			spanItems[j].id = "setCount_"+suffix;
			spanItems[j].innerHTML = "Metric Set "+ suffix;
		}else if(spanItems[j].id == "itemCount_template"){
			spanItems[j].id = "itemCount_"+itemSuffix;
			spanItems[j].innerHTML = "Metric "+ itemSuffix;
		}
	}
	
}

function modifyRowDomAttr(specificRow,suffix,itemSuffix){
	//modify row id when add group
	if(specificRow.id.substring(0,5) == "group"){
		modifyTrAttr(specificRow,suffix,1);
		modifyAlinkAttr(specificRow,suffix,1);
		modifyCheckboxAttr(specificRow,suffix,1);
		modifySelectAttr(specificRow,suffix,1);
		modifyTextInputAttr(specificRow,suffix,1);
		modifySpanAttr(specificRow,suffix,1);
	}else if(specificRow.id.substring(0,4) == "item"){
		modifyAlinkAttr(specificRow,suffix,itemSuffix);
		modifyCheckboxAttr(specificRow,suffix,itemSuffix);
		modifySelectAttr(specificRow,suffix,itemSuffix);
		modifyTextInputAttr(specificRow,suffix,itemSuffix);
		modifySpanAttr(specificRow,suffix,itemSuffix);
	}
}

function removeGroup(ele){
	//can not be removed when there is only one group,except the template select
	if($("tr[id^=group_]").length == 3){
 		//hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.group.default"/>',"componentDivError");
		return false;
	}
	
	var index,currentRow;
	if(ele.id){
		index = ele.id;
		index = index.substr(index.indexOf("_"));
	}
	currentRow = Get("group"+index);
	if(currentRow){
		currentRow.parentNode.deleteRow(currentRow.rowIndex);
	}
	currentRow = Get("groupAdd"+index);
	if(currentRow){
		currentRow.parentNode.deleteRow(currentRow.rowIndex);
	}
	checkDeleteDisable();
}

function addItem(ele){
	if(!validateSourceData()){
		return false;
	}
	
	if(!validateSingleItemData(ele)){
		return false;
	}
	
	var itemIndex,groupIndex,idSuffix,sourceId,suffix;
	if(ele.id){
		itemIndex = ele.id;
		sourceId = ele.id;
		itemIndex = itemIndex.substr(itemIndex.lastIndexOf("_")+1);
		idSuffix = sourceId.substr(0,sourceId.lastIndexOf("_"));
		groupIndex = idSuffix.substr(idSuffix.indexOf("_")+1);
	}
	
	var lastRow = Get(ele.parentNode.parentNode.id);
	var currentGroup = Get("group_"+groupIndex);
	
	if(currentGroup){
		var elements = currentGroup.getElementsByTagName("tr");
		if(elements && elements.length > 3){
			suffix = (elements.length -1)/3 +1;
		}
	}
	
	if(lastRow){
		var newTemplate = Get("item_display_template").cloneNode(true);
		var newTemplateWith = Get("item_with_template").cloneNode(true);
		var newTemplateWithout = Get("item_without_template").cloneNode(true);
		newTemplate.id = "item_"+groupIndex+"_"+suffix;
		newTemplateWith.id = "item_"+groupIndex+"_"+suffix+"_with";
		newTemplateWithout.id = "item_"+groupIndex+"_"+suffix+"_without";
		modifyRowDomAttr(newTemplate,groupIndex,suffix);
		YAHOO.util.Dom.insertAfter(newTemplate, lastRow);
		modifyRowDomAttr(newTemplateWith,groupIndex,suffix);
		YAHOO.util.Dom.insertAfter(newTemplateWith,newTemplate);
		modifyRowDomAttr(newTemplateWithout,groupIndex,suffix);
		YAHOO.util.Dom.insertAfter(newTemplateWithout,newTemplateWith); 
	}
	checkDeleteDisable();
}

function checkDeleteDisable(){
	$("a[id^=groupDelete_]").each(function (){
		if(this.id != "groupDelete_template"){
			if($("a[id^=groupDelete_]").length >2){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel.png");
				$("#"+this.id+" img").attr("width", 14);
				$("#"+this.id+" img").attr("height", 14);
			}else{
				//$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel_disable.png");
				$("#"+this.id+" img").attr("src", null);
				$("#"+this.id+" img").attr("width", 0);
				$("#"+this.id+" img").attr("height", 0);
			}
		}
	});
	$("a[id^=itemDelete_]").each(function (){
		if(this.id != "itemDelete_template"){
			if($("a[id^=itemDelete_]").length >2){
				$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel.png");
				$("#"+this.id+" img").attr("width", 14);
				$("#"+this.id+" img").attr("height", 14);
			}else{
				//$("#"+this.id+" img").attr("src", imagesBaseUrl + "/cancel_disable.png");
				$("#"+this.id+" img").attr("src", null);
				$("#"+this.id+" img").attr("width", 0);
				$("#"+this.id+" img").attr("height", 0);
			}
		}
	});
}

function removeItem(ele){
	//can not be removed when there is only one item,except the template select
	if($("select[id^=widgetConfigItemData]").length == 2){
		//hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.item.default"/>',"componentDivError");
		return false;
	}
	
	var index,currentRow,currentRowWith,currentRowWithout;
	if(ele.id){
		index = ele.id;
		index = index.substr(index.indexOf("_"),index.length);
	}
	
	currentRow = Get("item"+index);
	currentRowWith = Get("item"+index+"_with");
	currentRowWithout = Get("item"+index+"_without");
	if(currentRow && currentRowWith && currentRowWithout){
		currentRow.parentNode.deleteRow(currentRow.rowIndex);
		currentRowWith.parentNode.deleteRow(currentRowWith.rowIndex);
		currentRowWithout.parentNode.deleteRow(currentRowWithout.rowIndex);
	} 
	
	index = index.substr(0,index.lastIndexOf("_"));
	var currentItemSize = $("tr[id^=item"+index+"]").length;
	//when one item exist, delete group at the same time.
	if(currentItemSize == 0){
		Get("groupDelete"+index).onclick();
	}

	checkDeleteDisable();
}

function getDisplayOptionValues() {
	var result = new Array();
	var temp =  new Array();
	var count = -1;
	
	$("input[name^='displayOption_']").each(function (){
		var selected = document.getElementsByName(this.name);
		count++;
		for(var j = 0; j < 3; j ++) {
			if (selected[j].checked) {
				init = selected[j].value;
				break;
			}
		}
		result[count]=init;
	});
	var realCount = count/3;
	for(var i=0;i<realCount;i++){
		temp[i] = result[i*3];
	}
	Get(formComponent+"_displayOptions").value=temp;
}

function getCheckboxValues(){
	var result = new Array();
	var validBreakdown = new Array(); 
	var options = document.getElementsByName("enableBreakdownCheck");
	for(var i = 0;i<options.length;i++){
		result[i] = options[i].checked;
		validBreakdown[i] = options[i].disabled;
	}
	Get(formComponent+"_enableBreakdowns").value=result;
	Get(formComponent+"_validBreakdowns").value=validBreakdown;
	
	options = document.getElementsByName("enableDisplayTotalCheck");
	for(var i = 0;i<options.length;i++){
		result[i]=options[i].checked;
	}
	Get(formComponent+"_enableDisplayTotals").value=result;
}

function getGroupItemMaping(){
	//get the size of group
	var groupSize = $("tr[id^='group_']").length-1;
	Get(formComponent+"_groupSize").value = groupSize;
	var itemArray = new Array();
	var i = -1;
	$("tr[id^=group_]").each(
		function (){
			if(this.id != "group_template"){
				i++;
				var index = this.id;
				index = index.substring(index.indexOf("_"));
				var itemSize = $("tr[id^='item"+index+"_']").length/3;
				itemArray[i] = itemSize;
			}
		}		
	);
	
	Get(formComponent+"_groupItemMap").value = itemArray;
	return false;
}

function savePremadeWidget(operation){
	if(!validate(operation)){
		return false;
	}
	if(!checkMetricDupAndLevel(true)){
		return false;
	}
	
	var succSavePremadeWidget = function (o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Save As widget Error.", "Error");
			return false;
		}
		if(data.succFlag) {
			hm.util.displayJsonInfoNoteWithID(data.msg,"componentDivError");
			hm.util.insertSelectValue(data.id, data.name, Get("da_select_wd_widget"), true, false);
		} else {
			hm.util.displayJsonErrorNoteWithID(data.msg,"componentDivError");
		}
	};
	if(Get("premadeWidgetName").value.trim().length==0) {
		 hm.util.reportFieldError(Get("premadeWidgetNameNote"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.component.config.widget.name"/></s:param></s:text>');
		 Get("premadeWidgetName").focus();
		 return false;
	}
	
	var message = hm.util.validateNameWithBlanks(Get("premadeWidgetName").value, '<s:text name="hm.dashboard.component.config.widget.name"/>');
	if (message != null) {
	    hm.util.reportFieldError(Get("premadeWidgetName"), message);
	    Get("premadeWidgetName").focus();
	    return false;
	}
	
	beforeSubmit();
	document.forms["dashboardComponent"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("dashboardComponent"));
	url = "dashboardComponent.action?ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succSavePremadeWidget, timeout: 60000});
}

function savePremadeMetric(operation){
	if(!validate(operation)){
		return false;
	}
	if(!checkMetricDupAndLevel(false)){
		return false;
	}
	
	var succSavePremadeMetric = function (o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Save As metric Error.", "Error");
			return false;
		}
		if(data.succFlag) {
			hm.util.displayJsonInfoNoteWithID(data.msg,"componentDivError");
			hm.util.insertSelectValue(data.id, data.name, Get("da_select_wd_metric"), true, false);
		} else {
			hm.util.displayJsonErrorNoteWithID(data.msg,"componentDivError");
		}
	};
	if(Get("premadeMetricName").value.trim().length==0) {
		 hm.util.reportFieldError(Get("premadeMetricName"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.component.config.metric.name"/></s:param></s:text>');
		 Get("premadeMetricName").focus();
		 return false;
	}
	
	var message = hm.util.validateNameWithBlanks(Get("premadeMetricName").value, '<s:text name="hm.dashboard.component.config.metric.name"/>');
	if (message != null) {
	    hm.util.reportFieldError(Get("premadeMetricName"), message);
	    Get("premadeMetricName").focus();
	    return false;
	}
	
	beforeSubmit();
	document.forms["dashboardComponent"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("dashboardComponent"));
	url = "dashboardComponent.action?ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succSavePremadeMetric, timeout: 60000});
}

function submitDashboardComponent(operation){
	if(!validate(operation)){
		return false;
	}
	if(!checkMetricDupAndLevel(true)){
		return false;
	}
	document.forms["dashboardComponent"].operation.value = operation;
	var url = "<s:url action='dashboardComponent' includeParams='none' />?ignore="+new Date().getTime();
	if (operation == 'update') {
		url = url + "&id="+'<s:property value="dataSource.id" />';
	}
	beforeSubmit();
	YAHOO.util.Connect.setForm(document.getElementById("dashboardComponent"));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succCreateComponent, failure : doNothing, timeout: 60000}, null);
}

var succCreateComponent = function(o){
	try{
		eval("var details = " + o.responseText);
		if(details.succFlag){
			hideDashboardComponentPanel();
			//hm.util.displayJsonInfoNoteWithID(details.msg,"componentDivError");
			var dashboardTmp = getCurrentDashboard();
			dashboardTmp.setCurrentEditWidgetOptions({
				'title': $('#da_tx_wd_title').val()
			});
			$('#widgetCopyDiv').html($('#'+curEditContent).html());
			$('#'+curEditContent).html('');
			dashboardTmp.toggleEditArea("-1", false, true, details.id);
		}
	}catch(e){
		//alert("error:"+e);
		showWarnDialog("Save As widget Error.", "Error");
		return false;
	}
	
};

var doNothing = function(o){
	
};

function beforeSubmit(){
	Get(formComponent + "_xdataSelect").value = Get("widgetConfigSourceData").value;
	getCheckboxValues();
	getGroupItemMaping();
	setValueExpressionKey();
}

function setValueExpressionKey(){
	Get("widgetConfigDisplayValueKey").value = hm.da.getValueExpressionKey(Get("widgetConfigSourceData").value,Get("widgetConfigDisplayValue").value);
	$("input[id^=displayValue_]").each(function(){
		if (this.id != 'displayValue_template'){
			var idSuffix = this.id.substring(this.id.indexOf("_"),this.id.length);
			Get("keyValue"+idSuffix).value=hm.da.getValueExpressionKey(Get("widgetConfigSourceData").value,this.value);
		}
	});
}

function validate(operation){
	if(!validateSourceData()){
		return false;
	}
	
	if(operation != 'savePremadeMetric' && operation != 'addGroup'){
		if(!validateDisplayValue()){
			return false;
		}
	}
	
	if(!validateAllItem()){
		return false;
	}
	
	return true;
}

function validateSourceData(){
	if(Get("widgetConfigSourceData") && Get("widgetConfigSourceData").value == -1){
		hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.select.sourcedata"/>',"componentDivError");
		return false;
	}
	
	return true;
}

function validateDisplayValue(){
	if(Get("widgetConfigDisplayValue") && 
			(Get("widgetConfigDisplayValue").value.length == 0 
					|| Get("widgetConfigDisplayValue").value.trim().length == 0)){
		hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.input.displayvalue"/>',"componentDivError");
		return false;
	}
	
	//validate expression if valid
	var result = AeroHive.validate(Get("widgetConfigDisplayValue").value,Get("widgetConfigSourceData").value);
	if(!result){
		hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.input.displayvalue.validate"/>',"componentDivError");
		return false;
	}
	
	return true;
}

function validateSingleItemData(ele){
	if(ele && ele.id){
		var temp = ele.id;
		var idSuffix = temp.substring(temp.indexOf("_"),temp.length);
		//the default id of s:radio was added with the corresponding by struts2 
		if(Get("widgetConfigItemData"+idSuffix) && Get("widgetConfigItemData"+idSuffix).value == -1){
			hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.item.select"/>',"componentDivError");
			return false;
		}

		var displayName = Get("displayName"+idSuffix);
		if(displayName && (displayName.value.length == 0 || displayName.value.trim().length == 0)){
			hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.item.display.name"/>',"componentDivError");
			return false;
		}
			
		var displayValue = Get("displayValue"+idSuffix);
		if(displayValue && (displayValue.value.length == 0 || displayValue.value.trim().length == 0)){
			hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.input.displayvalue"/>',"componentDivError");
			return false;
		}
		
		//validate enablebreakdown
		var enableBreakdown = Get("enableBreakdown"+idSuffix);
		if(enableBreakdown.checked){
			var levels = Get("levelBreakDown"+idSuffix);
			
			if(levels && levels.value.length == 0){
				hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.item.level.with.aggregation"/>',"componentDivError");
				return false;
			} 
			
			var message = hm.util.validateInteger(levels.value,'<s:text name="hm.dashboard.component.config.levels"/>');
			if(message != null){
				hm.util.displayJsonErrorNoteWithID(message,"componentDivError");
				return false;
			}
			
		}
		
		var chartTypeTmp;
		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp) {
			chartTypeTmp = AhDashboardHelper.helper.getDefaultChartType(Get("widgetConfigSourceData").value);
		}
		//validate expression
		var result;
		if (chartTypeTmp == 'table' || chartTypeTmp == 'list') {
			result = AeroHive.validate(displayValue.value,Get("widgetConfigSourceData").value);
		} else {
			result = AeroHive.validate(displayValue.value,Get("widgetConfigSourceData").value, ["number"]);
		}
		if(!result){
			hm.util.displayJsonErrorNoteWithID('<s:text name="error.da.component.input.displayvalue.validate"/>', "componentDivError");
			return false;
		}
		
	}
	return true;
}

function validateAllItem(){
	var flag = true;
	$("a[id^=itemAdd]").each(
			function (){
				if(this.id != "itemAdd_template"){
					if(!validateSingleItemData(this)){
						flag = false;
					}
					
				}
			}
	);
	return flag;
}


function changeItemData(ele){
	if(ele && ele.id){
		var temp = ele.id;
		var idSuffix = temp.substring(temp.indexOf("_"),temp.length);
		if(ele && ele.value != -1){
			if(ele.selectedIndex == -1){
				ele.selectedIndex = 0;
			}
			Get("displayName"+idSuffix).value = hm.da.showDefaultItemDisplayName(Get("widgetConfigSourceData").value,ele.value);
			Get("displayValue"+idSuffix).value =AeroHive.metric(ele.options[ele.selectedIndex].text);
			var isBreakDown = !isBreakdown(ele.value);
			//when breakdown is disabled, hide the levels and display total
			if(isBreakDown){
				Get("enableBreakdown" + idSuffix).checked = false;
				if(Get("levelBreakDown"+idSuffix).parentNode.style.display == ""){
					Get("levelBreakDown"+idSuffix).parentNode.style.display = "none";
				}
			}
			Get("enableBreakdown" + idSuffix).disabled = isBreakDown;
		}else{
			Get("displayName"+idSuffix).value = "";
			Get("displayValue"+idSuffix).value ="";
			Get("enableBreakdown"+idSuffix).disabled =true;
		}
	}
}


<s:if test="%{dataSource.realComponentType!='cuhtml'}">
	YAHOO.util.Event.onContentReady("widgetConfigTemplateDiv", function(){setTimeout(initWidgetConfig, 100);}, this);
</s:if>

function checkMetricDupAndLevel(includeX){
	var ret = true;
	if(includeX){
		var ax=getMetricsFromExpression(hm.da.getValueExpressionKey(Get("widgetConfigSourceData").value,Get("widgetConfigDisplayValue").value));
		$("input[id^=displayValue_]").each(function (){
			if (!ret) {
				return;
			}
			if(this.id != "displayValue_template"){
				var idSuffix = this.id.substring(this.id.indexOf("_"),this.id.length);
				var av=Get("widgetConfigItemData" + idSuffix).value;
				var aeb=Get("enableBreakdown" + idSuffix).checked;
				var aev=Get("levelBreakDown" + idSuffix).value;
				
				if(ax[av]!=undefined && aeb && aev!=0) {
					hm.util.displayJsonErrorNoteWithID("Metric(" + Get("displayName" +idSuffix).value + ") at value expression that don't allow breakdown.","componentDivError");
					ret= false;
				}
			}
		});
	}
	
	var daLevel = {};
	$("input[id^=displayValue_]").each(function (){
		if (!ret) {
			return ;
		}
		if(this.id != "displayValue_template"){
			var idSuffix = this.id.substring(this.id.indexOf("_"),this.id.length);
			
			var ax=getMetricsFromExpression(hm.da.getValueExpressionKey(Get("widgetConfigSourceData").value,this.value));
			for (var key in ax) {
				var le = daLevel[key];
				var daLe=0;
				if(Get("enableBreakdown" + idSuffix).checked){
					daLe=Get("levelBreakDown" + idSuffix).value;
				}
				if(typeof(le)=="undefined") {
					daLevel[key]=daLe;
				} else {
					if(daLevel[key]!=daLe) {
						hm.util.displayJsonErrorNoteWithID("Metric(" + Get("displayName" +idSuffix).value + ") breakdown level must same.","componentDivError");
						ret= false;
					}
				}
			}
		}
	});
	return ret;
}

function getMetricsFromExpression(s) {
	if (s==null || s.trim().length == 0) {
		return null;
	}
	var retArray={};
	var str = s;
	var leftPos = str.indexOf('[');
	var rightPos = str.indexOf(']');
	while (str!=null && str.trim().length != 0
			&& leftPos >= 0) {
		if (rightPos - leftPos > 1) {
			retArray[str.substring(leftPos+1, rightPos)]=str.substring(leftPos+1, rightPos);
			str = str.substr(rightPos+1);
		}
		if (str!=null && str.trim().length != 0) {
			leftPos = str.indexOf('[');
			rightPos = str.indexOf(']');
		}
	}
	return retArray;
}

/**

function saveAsCuHtmlPanel(){
	var succSaveAsCuHtmlPanel = function (o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Save As widget Error.", "Error");
			return false;
		}
		if(data.t) {
			hm.util.displayJsonInfoNoteWithID(data.m,"componentDivError");
			hm.util.insertSelectValue(data.va, data.te, Get("da_select_wd_widget"), true, false);
		} else {
			hm.util.displayJsonErrorNoteWithID(data.m,"componentDivError");
		}
	};
	if(Get("cuHtmlWidgetName").value.trim().length==0) {
		 hm.util.reportFieldError(Get("cuHtmlWidgetName"), '<s:text name="error.requiredField"><s:param><s:text name="hm.dashboard.component.config.widget.name"/></s:param></s:text>');
		 Get("cuHtmlWidgetName").focus();
		 return false;
		 var message = hm.util.validateNameWithBlanks(Get("cuHtmlWidgetName").value, '<s:text name="hm.dashboard.component.config.widget.name"/>');
		if (message != null) {
		    hm.util.reportFieldError(Get("cuHtmlWidgetName"), message);
		    Get("cuHtmlWidgetName").focus();
		    return false;
		}
	}
	var textareaValue = Get("textAreaCuHtml").value;
	url = "dashboardComponent.action?operation=saveAsCuHtmlPanel&widgetName=" + Get("cuHtmlWidgetName").value + "&textCuHtml="+ textareaValue +"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succSaveAsCuHtmlPanel, timeout: 60000});
}
**/
</script>

