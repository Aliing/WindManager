<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
	.toolDiv{
		border: 1px solid #ddd; 
		border-bottom-width: 0;
		padding: 2px 4px; 
		height: 18px;
		background: #dfedfd;
	}
	.toolDiv:hover{
		background: #d6e7fc;
	}
	@media screen and (-webkit-min-device-pixel-ratio:0) {
		/* Chrome hack */
		.toolDiv{
			position: relative;
			bottom: -2px;
		}
	}
</style>
<table cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td><s:property value="leftTitle" /></td>
		<td></td>
		<td><s:property value="rightTitle" /></td>
	</tr>
	<tr>
		<td height="1"></td>
	</tr>
	<tr>
		<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<s:if test="%{actionPostfix != null}">
				<tr>
					<td>
						<s:if test="%{fullMode || (easyMode && type == null)}">
							<div class="toolDiv">
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<!--<s:if test="%{jsonMode == true}">
									  	 <s:if test="%{dataSource.id == null}">
										  	 <a class="marginBtn" href="#" onclick="showSchedulerNewDialog('new')"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
									  	 </s:if>
									 	 <s:else>
										 	 <a class="marginBtn" href="#" onclick="showSchedulerNewDialog('edit')"><img class="dinl"
												src="<s:url value="/images/new.png" />"
												width="16" height="16" alt="New" title="New" /></a>
									  	 </s:else>
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('new<s:property value="actionPostfix"/>')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									</s:else>
									-->
									<a class="marginBtn" href="javascript:submitAction('new<s:property value="actionPostfix"/>')"><img class="dinl"
										src="<s:url value="/images/new.png" />"
										width="16" height="16" alt="New" title="New" /></a>
									
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/modify_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<!--<s:if test="%{jsonMode == true}">
									  	 <s:if test="%{dataSource.id == null}">
										  	 <a class="marginBtn" href="#" onclick="showSchedulerEditDialog('new')"><img class="dinl"
												src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
									  	 </s:if>
									 	 <s:else>
										 	 <a class="marginBtn" href="#" onclick="showSchedulerEditDialog('edit')"><img class="dinl"
												src="<s:url value="/images/modify.png" />"
												width="16" height="16" alt="Modify" title="Modify" /></a>
									  	 </s:else>
									</s:if>
									<s:else>
										<a class="marginBtn" href="javascript:submitAction('edit<s:property value="actionPostfix"/>')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
									</s:else>
									-->
									<a class="marginBtn" href="javascript:submitAction('edit<s:property value="actionPostfix"/>')"><img class="dinl"
											src="<s:url value="/images/modify.png" />"
											width="16" height="16" alt="Modify" title="Modify" /></a>
								</s:else>
							</div>
						</s:if>
						<s:elseif test="%{easyMode && type != null}">
							<div class="toolDiv">
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/new_disable.png" />"
									width="16" height="16" alt="New" title="New" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:hm.simpleObject.newSimple('<s:property value="type"/>', 
										'<s:property value="subType"/>', 'leftOptions_<s:property value="name"/>', '<s:property value="callbackFn"/>', <s:property value="domainId"/>)"><img class="dinl"
									src="<s:url value="/images/new.png" />"
									width="16" height="16" alt="New" title="New" /></a>
								</s:else>
								<s:if test="%{writeDisabled == 'disabled'}">
									<img class="dinl marginBtn"
									src="<s:url value="/images/cancel_disable.png" />"
									width="16" height="16" alt="Modify" title="Modify" />
								</s:if>
								<s:else>
									<a class="marginBtn" href="javascript:hm.simpleObject.removeSimple('<s:property value="type"/>',
										'leftOptions_<s:property value="name"/>', '<s:property value="callbackFn"/>')"><img class="dinl"
									src="<s:url value="/images/cancel.png" />"
									width="16" height="16" alt="Remove" title="Remove" /></a>
								</s:else>
							</div>
						</s:elseif>
					</td>
				</tr>
			</s:if>
			<tr>
				<td><ah:select list="leftOptions" /></td>
			</tr>
		</table>
		</td>
		<td valign="center" style="padding: 0 4px 0 4px;">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td><input type="button" class="transfer" value="&gt;" <s:property value="operateDisabled" />
					onclick="hm.options.moveSelectedOptions(document.getElementById('leftOptions_<s:property value="name"/>'),
					                             			document.getElementById('<s:property value="name"/>'), <s:property value="sort"/>, '', '', '<s:property value="limitCount" />');" />
				</td>
			</tr>
			<tr>
				<td><input type="button" class="transfer" value="&gt;&gt;" <s:property value="operateDisabled" />
					onclick="hm.options.moveAllOptions(document.getElementById('leftOptions_<s:property value="name"/>'),
					                        		   document.getElementById('<s:property value="name"/>'), <s:property value="sort"/>, '', '', '<s:property value="limitCount" />');" />
				</td>
			</tr>
			<tr>
				<td height="8"></td>
			</tr>
			<tr>
				<td><input type="button" class="transfer" value="&lt;" <s:property value="operateDisabled" />
					onclick="hm.options.moveSelectedOptions(document.getElementById('<s:property value="name"/>'),
					                             			document.getElementById('leftOptions_<s:property value="name"/>'), <s:property value="sort"/>, '', '', 0);" />
				</td>
			</tr>
			<tr>
				<td><input type="button" class="transfer" value="&lt;&lt;" <s:property value="operateDisabled" />
					onclick="hm.options.moveAllOptions(document.getElementById('<s:property value="name"/>'),
					                        		   document.getElementById('leftOptions_<s:property value="name"/>'), <s:property value="sort"/>, '', '', 0);" />
				</td>
			</tr>
		</table>
		</td>
		<td>
		<table cellspacing="0" cellpadding="0" border="0">
			<s:if test="%{actionPostfix != null}">
				<tr>
					<td>
						<div class="toolDiv">
						</div>
					</td>
				</tr>
			</s:if>
			<tr>
				<td><ah:select list="rightOptions" /></td>
			</tr>
		</table>
		</td>
		<s:if test="%{withSort == true}">
			<td>
	           <table cellspacing="0" cellpadding="0" border="0">
	               <tr>
	                   <td><input type="button" class="moveRow" value="Up"
	                       onclick="columnsChanged = true;hm.options.moveOptionUp(document.getElementById('preferredSsids'), '', '');" /></td>
	               </tr>
	               <tr>
	                   <td><input type="button" class="moveRow" value="Down"
	                       onclick="columnsChanged = true;hm.options.moveOptionDown(document.getElementById('preferredSsids'), '', '');" /></td>
	               </tr>
	           </table>
			</td>
		</s:if>
	</tr>
</table>
