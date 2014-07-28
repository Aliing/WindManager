<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tr>
	<td height="10"></td>
</tr>
<tr><td>
<fieldset style="width:1220px;"><legend>Application Watchlist</legend>
<%-- <div id="appContent" style="display:none;">
		<table>
		<s:iterator value="%{unSelectedAppList}" status="status">
	          <tr class="even" appId="<s:property value='id'/>" appType="<s:property value="%{appType}"/>">
	          		<td class="tdsystemchk">
					 <input type="checkbox" name="systemAppIds" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
					 </td>
					 <td class="tdsystemappname">
					 <a href="javascript:void(0);" title="<s:property value="%{description}"/>"><s:property value="%{appName}"/></a>
					 </td>
       		        <td class="tdsystemusage" realValue="<s:property value='lastDayUsage'/>"><s:property value='lastDayUsageStr'/></td>
       		        <td class="tdsystemusage" realValue="<s:property value='lastMonthUsage'/>"><s:property value="lastMonthUsageStr"/></td>
       		        <td class="tdsystemgroup"><s:property value="appGroupName"/></td>
       		  </tr>	
	      </s:iterator> 
		</table>
	</div> --%>
<table cellspacing="0" cellpadding="0" border="0" width="80%">
	<input type="hidden" id="selectedAppIds" name="selectedAppIds">
	<input type="hidden" id="selectedCustomAppIds" name="selectedCustomAppIds">
	<tr><td height="10px"></td></tr>
	<tr><td>Create a customized application watchlist.</td></tr>
	<tr>
		<td height="10px" colspan="3"></td>
	</tr>
	<tr><td height="10px" id="messageError"></td></tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="1180px" style="table-layout: fixed;">
	<tr>
		<td width="580px" height="356px">
			<div class="yui-navset">
			    <ul class="yui-nav">
			        <li id="systemTab" class="selected"><a href="javascript:void(0);" onclick="changeTab('systemTab');"><em><s:text name="geneva_26.config.system.application.tab"/></em></a></li>
			        <li id="customTab"><a href="javascript:void(0);" onclick="changeTab('customTab');"><em><s:text name="geneva_26.config.custom.application.tab"/></em></a></li>
			    </ul>  
			    <div class="yui-content" style="background:#ffffff;height:320px;">
			        <div id="systemDiv">
			        	<table width="100%">
								<tr><td>
									<table width="100%">
									<tr>
										<td>
											<span id="currentAvailableSystemNum"><s:property value="unSelectedAppNum"/></span> Available (<s:property value="totalSystemAppNum"/> Total)
						                	<input type="hidden" id="totalSystemAppNum" value="<s:property value="totalSystemAppNum"/>"/>
						                </td>
						                <%-- <td align="right">
											<input type="radio" onclick="changeService('application');" checked="checked" id="application_radio"/><s:text name="geneva_26.config.system.application.search.application"/>&nbsp;&nbsp;
											<input type="radio" onclick="changeService('group');" id="group_radio"/><s:text name="geneva_26.config.system.application.search.group"/>&nbsp;&nbsp;
											<input type="text" id="system_search_key" name="systemSearchKey" onkeydown="javascript:return appService.enterKeywords(event,'system_search_key','leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
											 <a href="javascript: void(0);" onclick="appService.searchApps('system_search_key','leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');">
						                         <img src="<s:url value="/images/search/Search-PMS296.png" includeParams="none"/>" border="0" width="20" height="15" alt="" />
						                    </a>
										</td> --%>
										<td align="right">
											<input type="radio" onclick="changeService('application');" checked="checked" id="application_radio"/><s:text name="geneva_26.config.system.application.search.application"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<input type="radio" onclick="changeService('group');" id="group_radio"/><s:text name="geneva_26.config.system.application.search.group"/>&nbsp;&nbsp;&nbsp;&nbsp;
										</td>
										<td id="appSearchTd" width="150px">
											<div id="searchKeyContainerDiv">
											<input type="text" id="system_search_key" name="searchKey" onkeydown="javascript:return appService.enterKeywords(event,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
											<div id="searchKeyContainer"></div>
						                    </div>
										</td>
										<td id="groupSearchTd" width="150px" style="display:none;">
											<div id="searchGroupKeyContainerDiv">
											<input type="text" id="system_search_groupkey" name="searchKey" onkeydown="javascript:return appService.enterKeywords(event,'leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');" value="" />
											<div id="searchGroupKeyContainer"></div>
						                    </div>
										</td>
										<td>
											<div style="width: 20px; padding-left: 5px;">
											<a class="searchElement" href="javascript: void(0);" onclick="appService.searchApps('leftSystemAppTable','application_radio','currentAvailableSystemNum','currentSelectSystemNum','totalSystemAppNum');"> <img class="dinl"
												src="<s:url value="/images/search/Search-WarmGrey11.png" />"
												width="20" height="20" alt="Search" title="Search" /> </a>
											</div>
										</td>
									</tr>
									</table>
								</td></tr>
								<tr><td>
									<div class="divcontainer">
										<table id="system_left_thead_table_id">
											<tbody>
											<tr>
												<th class="thchk" align="center"><input type="checkbox" id="checkAllSystem" onClick="checkAllSystemItem(this);" /></th>
												<th class="thapphead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,1,'',this);">Application</th>
												<th class="thhead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,2,'float',this);">Usage(Last Day)</th>
												<th class="thhead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,3,'float',this);">Usage(Last 30 Days)</th>
												<th class="thgrouphead SortNone" onclick="sortTable('system_left_thead_table_id','system_left_table_id','leftSystemAppTable',0,4,'',this);">Group</th>
											</tr>
											</tbody>
										</table>
										</div>
						                <div class="container">
						        		<table class="show" id="system_left_table_id" style="table-layout: fixed;">
											<tbody id="leftSystemAppTable">
														<s:iterator value="%{unSelectedAppList}" status="status">
												          <tr class="even" appId="<s:property value='id'/>" appType="<s:property value="%{appType}"/>">
												          		<td class="tdsystemchk">
																 <input type="checkbox" name="systemAppIds" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
																 </td>
																 <td class="tdsystemappname">
																 <a href="javascript:void(0);" title="<s:property value="%{description}"/>"><s:property value="%{appName}"/></a>
																 </td>
											       		        <td class="tdsystemusage" realValue="<s:property value='lastDayUsage'/>"><s:property value='lastDayUsageStr'/></td>
											       		        <td class="tdsystemusage" realValue="<s:property value='lastMonthUsage'/>"><s:property value="lastMonthUsageStr"/></td>
											       		        <td class="tdsystemgroup"><s:property value="appGroupName"/></td>
											       		  </tr>	
												      </s:iterator> 
						        		          <%-- <tr><td style="padding:100px 150px 0px 100px;"><div style="position:relative;"><img src="<s:url value='/images/waiting.gif'/>" /><span style="font-size:15px; position: absolute;">loading</span></div></td></tr>  --%>
						        		    </tbody>
						                </table>
						                </div>
								</td></tr>
							</table>
			        </div>
			        <div id="customDiv" class="yui-hidden">
			        		<table width="100%">
									<tr><td>
										<table width="100%">
										<tr>
											<td width="200px">
												<span id="currentAvailableCustomNum"><s:property value="unSelectedCustomAppNum"/></span>
												 Available (<span id="totalShowCustomAppNum"><s:property value="totalCustomAppNum"/></span> Total)
							                	<input type="hidden" id="totalCustomAppNum" value="<s:property value="totalCustomAppNum"/>"/>
							                </td>
							                <%-- <td align="right">
												<input type="text" id="custom_search_key" name="customSearchKey" onkeydown="javascript:appService.enterKeywords(event,'custom_search_key','leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');" value="" />
												 <a href="javascript: void(0);" onclick="appService.searchApps('custom_search_key','leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');">
							                         <img src="<s:url value="/images/search/Search-PMS296.png" includeParams="none"/>" border="0" width="20" height="15" alt="" />
							                    </a>
											</td> --%>
											<td style="padding-left:200px;">
												<div id="searchCustomKeyContainerDiv">
												<input type="text" id="custom_search_key" name="customSearchKey" onkeydown="javascript:appService.enterKeywords(event,'leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');" value="" />
												<div id="searchCustomKeyContainer"></div>
							                    </div>
											</td>
											<td>
												<div style="width: 20px; padding-left: 5px;">
												<a class="searchElement" href="javascript:void(0);" onclick="appService.searchApps('leftCustomAppTable','','currentAvailableCustomNum','currentSelectCustomNum','totalCustomAppNum');"> <img class="dinl"
													src="<s:url value="/images/search/Search-WarmGrey11.png" />"
													width="20" height="20" alt="Search" title="Search" /> </a>
												</div>
											</td>
										</tr>
										</table>
									</td></tr>
									<tr><td>
										<div class="customdivcontainer">
											<table id="custom_left_thead_table_id">
												<tbody>
												<tr>
													<th class="thchk" align="center"><input type="checkbox" id="checkAllCustom" onClick="checkAllCustomItem(this);" /></th>
													<th class="thcustomapphead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,1,'',this);">Application</th>
													<th class="thcustomhead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,2,'float',this);">Usage (Last Day)</th>
													<th class="thcustomhead SortNone" onclick="sortTable('custom_left_thead_table_id','custom_left_table_id','leftCustomAppTable',0,3,'float',this);">Usage (Last 30 Days)</th>
												</tr>
												</tbody>
											</table>
											</div>
							                <div class="container">
							        		<table class="show" id="custom_left_table_id" style="table-layout: fixed;">
												<tbody id="leftCustomAppTable">
												<s:iterator value="%{unSelectedCustomAppList}" status="status">
													<tr class="even" appId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>">
														 <td class="tdcustomchk">
														 <input type="checkbox" name="customAppIds" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
														 </td>
														 <td class="tdcustomappname">
														 <a href="javascript:void(0);" title="<s:property value="%{description}"/>">
														 <s:property value="%{customAppName}" escape="false"/>
														 </a>
														 </td>
														 <td class="tdcustomusage" realValue="<s:property value="%{lastDayUsage}"/>">
														 <s:property value="%{lastDayUsageStr}"/>
														 </td>
														 <td class="tdcustomusage" realValue="<s:property value="%{lastMonthUsage}"/>">
														 <s:property value="%{lastMonthUsageStr}"/>
														 </td>
													</tr>
												</s:iterator>
												</tbody>
											</table>
							                </div>
									</td></tr>
								</table>
			        </div>
			     </div>
			</div>
		</td>
		<td width="60px" height="356px">
			<table width="100%" height="100%">
				<tr>
					<td id="systemTdId" style="padding-left:10px;padding-right:10px;">
				         <input type="button" id="addAppButton" value=">" onclick="appService.addApp()" class="transfer">&nbsp;&nbsp;&nbsp;&nbsp;
				         <br/><br/><br/>
				         <input type="button" id="removeAppButton" value="<" onclick="appService.removeApp()" class="transfer">&nbsp;&nbsp;&nbsp;&nbsp;
				    </td>
				    <td id="customTdId" style="padding-left:10px;padding-right:10px;display:none;">
						<s:if test="%{writeDisabled == 'disabled'}">
               				<img class="dinl" src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" />
		                </s:if>
		                <s:else>
               				<a class="marginBtn" style="padding-left:8px;" href="javascript:void(0);" onclick="addCustomApp();return false;"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New Custom Application" title="New Custom Application" /></a>
		                </s:else>
				     	 <br/><br/>
				         <input type="button" id="addAppButton" value=">" onclick="appService.addApp()" class="transfer">&nbsp;&nbsp;&nbsp;&nbsp;
				         <br/><br/><br/>
				         <input type="button" id="removeAppButton" value="<" onclick="appService.removeApp()" class="transfer">&nbsp;&nbsp;&nbsp;&nbsp;
				    </td>
				</tr>
			</table>
		</td>
		<td width="580px" height="356px" style="vertical-align: top;">
			<div class="yui-navset" style="width: 580px;">
				<table width="100%"><tr style="line-height:20px;"><td>
						Selected Applications
				</td></tr></table>
			<div class="yui-content" style="background:#ffffff;">
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
					<tr>
					    <td>
					    	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
					             <tr style="line-height:32px;">
					                <td>
					                <span id="currentSelectAllNum"><s:property value="totalSelectedAppNum"/></span> Selected (<s:property value="watchlistLimitation"/> Max)
					                <input type="hidden" id="currentSelectSystemNum" value="<s:property value="selectedAppNum"/>"/>
					                <input type="hidden" id="currentSelectCustomNum" value="<s:property value="selectedCustomAppNum"/>"/>
					                </td>
					             </tr>
					             <tr>
					                <td>
					                <div class="divcontainer">
									<table id="right_thead_table_id">
										<tbody>
										<tr>
											<th class="thchk" align="center"><input type="checkbox" id="checkAllApp" onClick="checkAllAppItem(this);" /></th>
											<th class="thapphead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,1,'',this);">Application</th>
											<th class="thhead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,2,'float',this);">Usage(Last Day)</th>
											<th class="thhead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,3,'float',this);">Usage(Last 30 Days)</th>
											<th class="thapphead SortNone" onclick="sortTable('right_thead_table_id','right_table_id','rightAppTable',0,4,'',this);">Group</th>
										</tr>
										</tbody>
									</table>
									</div>
					                <div class="container">
					        		<table class="show" id="right_table_id" style="table-layout: fixed;">
									<tbody id="rightAppTable">
					        		<s:iterator value="%{selectedAppDtoList}" status="status">
								          <tr class="even" appId="<s:property value='id'/>" appType="<s:property value="%{appType}"/>">
								          		<td class="tdsystemchk">
												 <input type="checkbox" name="<s:if test="appType == 0">systemAppIds</s:if><s:else>customAppIds</s:else>" serviceId="<s:property value="%{id}"/>" appType="<s:property value="%{appType}"/>" />
												 </td>
												 <td class="tdsystemappname">
												 <a href="javascript:void(0);" title="<s:property value="%{description}"/>">
												 <s:property value="%{appName}" escape="false"/>
												 </a>
												 </td>
						        		        <td class="tdsystemusage" realValue="<s:property value='lastDayUsage'/>"><s:property value='lastDayUsageStr'/></td>
						        		        <td class="tdsystemusage" realValue="<s:property value='lastMonthUsage'/>"><s:property value="lastMonthUsageStr"/></td>
						        		        <td class="tdsystemgroup"><s:property value="appGroupName"/></td>
						        		  </tr>	
								    </s:iterator>        
					        		</tbody>
					                </table>
					                </div>
					                </td>
					             </tr>
					         </table>
					    </td>
					</tr>
				</table>
				</div>
				</div>
		</td>
	</tr>
	<tr>
	<td class="noteInfo" colspan="3" style="line-height:40px;padding-left: 0px">
		Note: Remember to push the configuration to your devices to activate application monitoring.
	</td>
	</tr>
</table>
<div id="newCustomAppPanelId" style="display: none;">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr><td width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ul"></td><td class="um" id="tdUM" style="width:750px;"></td><td class="ur"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="ml"></td>
					<td class="mm">
						<div id="newCustomAppPanelContentId"></div>
					</td>
					<td class="mr"></td>
				</tr>
			</table>
		</td></tr>
		<tr><td width="100%">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="bl"></td><td class="bm" id="tdBM" style="width:750px;"></td><td class="br"></td>
				</tr>
			</table>
		</td></tr>
	</table>
</div>
</fieldset></td></tr>
<!--application profile setting end-->
