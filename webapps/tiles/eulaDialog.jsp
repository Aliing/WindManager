<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'license';

function onLoadPage() {
	document.getElementById("agreeBut").focus();
}

function submitAction(operation) {
	document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
}
</script>

<s:form action="license">
	<s:hidden name="operation" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="10" alt="" class="dblk" /></td>
		</tr>
				<tr>
			<td style="padding: 4px 0 0 4px" align="center" valign="middle">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><img
						src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
					<td rowspan="2" class="menu_bg" style="padding: 6px 0 0 0;"
						valign="top" width="600px" id="totalDetail">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr height="5">
						</tr>
						<tr>
							<td colspan="2" align="left">
								<iframe frameborder="0" width="600px" height="500px" 
									src="<s:url value="/tiles/companyEula.htm" />"></iframe>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr align="center">
							<td><input id="agreeBut"
								type="button" name="ignore" value="Agree" class="button"
								onclick="submitAction('agree');" /></td>
							<td style="padding-left: 5px;"><input
								type="button" name="ignore" value="Disagree" class="button"
								onclick="submitAction('notAgree');" /></td>
						</tr>
					</table>
					</td>
					<td><img
						src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td class="menu_bg" height="540"></td>
					<td class="menu_bg" height="540"></td>
				</tr>
				<tr>
					<td><img
						src="<s:url value="/images/rounded/bottom_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
					<td class="menu_bg" height="1"><img
						src="<s:url value="/images/spacer.gif" includeParams="none"/>"
						width="100%" height="1" alt="" class="dblk" /></td>
					<td><img
						src="<s:url value="/images/rounded/bottom_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form>
