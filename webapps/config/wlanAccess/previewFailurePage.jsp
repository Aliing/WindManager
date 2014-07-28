<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
<!--
.style1 {
	font-family:Century Gothic, Helvetica, Arial, sans-serif;
	font-size:30px;
	text-align:center;
	font-weight:200;
	color: <s:property value="%{currentSuccessForegroundColor}" />;}

.style5 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
}

.style-failure_Current {
	font-family: Arial, Helvetica, sans-serif; 
    font-weight: bold;
    font-size: 20px;
    text-align: center;
	color: <s:property value="%{currentFailureForegroundColor}" />;}	
.style-failure-content_Current {
	font-family: Arial, Helvetica, sans-serif; 
    font-size: 14px;
    text-align: left;
	color: <s:property value="%{currentFailureForegroundColor}" />;}	
	
-->
</style>
<script>
var formName = 'captivePortalWeb';

function submitAction(operation) {
	document.forms[formName].operation.value = operation;
    document.forms[formName].submit();
}

</script>

<div id="content">
<s:form action="captivePortalWeb">
<s:hidden name="operation" />
<s:hidden name="customPage" />
<s:hidden name="registType" />
<s:hidden name="idmSelfReg" />
	
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<input type="button" class="button" name="return" value="Return"
							onClick="submitAction('previewReturn');" />
					</td>
					 <td class="labelT1" width="120px" style="padding:4px 0 4px 10px">
									   <s:text	name="config.cwp.language.support.previewLanguage_label" />
								    </td>
								    <td>
									<s:select name="previewLanguage" id="changelanguagecombobox"
										list="%{previewenumlanguage}" listKey="key" listValue="value" onChange="submitAction('previewFailurePageMultiLan')"
										value="previewLanguage" cssStyle="width: 150px;" 
										/>
					 </td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%"
				style="background-color: #333333;">
				<tr>
					<td id="currentPage" align="center">
					<div  style="background-image: url(<s:url value="%{currentFailureBackgroundImage}" />);
							background-repeat: <s:property value="%{currentFailureBackgroundTile}" />;
							 background-position: center;">
						<table border="0" cellspacing="0" cellpadding="0" 
							style="width: 400px; height: 659px;">
							<tr>
								<td height="60px"></td>
							</tr>
							<tr style="display: <s:property value="%{showH1Title}" />;">
								<td align="center"><p class="style1">
								<s:text name="%{CWPPreviewFailureSecureInternetPortal_label}"></s:text>
								</p></td>
							</tr>
							<tr>
								<td>
					        		 <img src="<s:url value="/images/cwp/fail.png"/>" style="float:left;"/>
					        		 <p  class="style-failure_Current">
					        		 <s:text name="%{CWPPreviewFailureLoginFailed_label}"></s:text>
					        		 </p>
								</td>
							</tr>
							<tr>
								<td>
							            <div>
							            <table>
							            	<s:if test="%{showFailureGeneralSection == ''}">
						            			<tr>
								            		<td>
								            		<div class="style-failure-content_Current">
								            		 <s:text name="%{CWPPreviewFailurefailurecontent1_label}"></s:text>
								            		</div>
								            		</td>
								            	</tr>
								            	<tr>
								            		<td>
								            		<div class="style-failure-content_Current">
								            			&lt;The Reply-Message from RADIUS server&gt;
								            		</div>
								            		</td>
								            	</tr>
								            	<tr>
								            		<td>
								            		<div class="style-failure-content_Current">
								            		<s:text name="%{CWPPreviewFailurefailurecontent2_label}"></s:text>
								            		</div>
								            		</td>
								            	</tr>	
						            		</s:if>
						            		<s:else>
						            			<tr>
						            				<td>
						            				<div class="style-failure-content_Current">
						            					<s:text name="%{CWPPreviewFailurelibrarySIPBlock}"></s:text>
						            				</div>
						            				</td>
						            			</tr>
						            		</s:else>
							            	
							            </table>
							            </div>
								</td>
							</tr>
							<s:if test="%{!hMForOEM}">
								<tr>
									<td align="right">
										<img alt="" src="<s:url value="%{currentFailureFootImage}" />" style="width: 235px; height: 69px;"><br>
									</td>
								</tr>
							</s:if>
						</table>
					</div>
					</td>					
				</tr>
			</table>
		</td>
	</tr>
</table>
</s:form>
</div>