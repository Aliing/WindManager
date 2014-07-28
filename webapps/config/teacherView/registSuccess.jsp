<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
td.sucMess {
	font-family: Arial, Helvetica, Verdana, sans-serif;
	color: #CC3300;
	font-size: 20px;
	font-weight: bold;
}
</style>
<div id="content">
	<table border="0" cellspacing="0" cellpadding="0" width="900" align="center">
		<tr>
			<td height="10px"></td>
		</tr>
		<tr>
			<td align="center" class="sucMess" style="padding-top:50px"><s:property value="%{confirmMessage}"/>
			</td>
		</tr>
		<tr>
			<td height="10px"></td>
		</tr>
	</table>
</div>
