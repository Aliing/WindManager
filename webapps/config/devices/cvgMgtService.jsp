<%@taglib prefix="s" uri="/struts-tags"%>
<div id="cvgMgtServerTr">
	<s:if test="%{dataSource.deviceInfo.cvgAsL3Vpn}">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="config.configTemplate.serverSettings" />','cvgMgtServer');</script></td>
		</tr>
		<tr>
			<td style="padding-left:7px"><div id="cvgMgtServer" style="display: <s:property value="%{dataSource.cvgMgtServerDisplayStyle}"/>">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="labelT1" width="100px"><s:text name="config.configTemplate.mgtDns" /></td>
						<td width="280px">
							<s:select name="dnsForCVGId" list="%{dnsForCVGList}" listKey="id"
								listValue="value" cssStyle="width: 160px;" />
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newMgtDns')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editMgtDns')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
					 	</td>
					 	<td class="labelT1" width="100px"><s:text name="config.configTemplate.mgtSyslog" /></td>
						<td>
							<s:select name="syslogForCVGId" list="%{syslogForCVGList}" listKey="id"
								listValue="value" cssStyle="width: 160px;" />
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newMgtSyslog')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editMgtSyslog')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
					 	</td>
					</tr>
					<tr>
						<td class="labelT1"><s:text name="config.configTemplate.mgtTime" /></td>
						<td>
							<s:select name="ntpForCVGId" list="%{ntpForCVGList}" listKey="id"
								listValue="value" cssStyle="width: 160px;" />
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newMgtTime')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editMgtTime')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
						</td>
						
						<td class="labelT1"><s:text name="config.configTemplate.mgtSnmp" /></td>
						<td>
							<s:select name="snmpForCVGId" list="%{snmpForCVGList}" listKey="id"
								listValue="value" cssStyle="width: 160px;" />
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/new_disable.png" />"
								width="16" height="16" alt="New" title="New" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('newMgtSnmp')"><img class="dinl"
								src="<s:url value="/images/new.png" />"
								width="16" height="16" alt="New" title="New" /></a>
							</s:else>
							<s:if test="%{writeDisabled == 'disabled'}">
								<img class="dinl marginBtn"
								src="<s:url value="/images/modify_disable.png" />"
								width="16" height="16" alt="Modify" title="Modify" />
							</s:if>
							<s:else>
								<a class="marginBtn" href="javascript:submitAction('editMgtSnmp')"><img class="dinl"
								src="<s:url value="/images/modify.png" />"
								width="16" height="16" alt="Modify" title="Modify" /></a>
							</s:else>
					 	</td>
					</tr>
				</table>
			</div></td>
		</tr>
	</table>
	</s:if>
</div>