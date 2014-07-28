<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
var formName = 'hmAccess';
</script>

<html>
<head>
<title><s:text name="feature.access.deny" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<script type="text/javascript"
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js" />"></script>
<link rel="stylesheet" href="<s:url value="/css/hm.css"/>"
	type="text/css" />
<style>
.deny {
	font-size: 16px;
	font-weight: bold;
	color: #000000;
	padding: 20px 0 0 40px;
}
</style>
</head>
<body class="body_bg" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">
<s:form action="hmAccess">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="<s:url value="/images/spacer.gif" />" width="1"
				height="50" alt="" class="dblk" /></td>
		</tr>
		<tr>
			<td style="padding: 4px 0 0 4px" align="center" valign="middle">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><img
						src="<s:url value="/images/rounded/top_left_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
					<td class="menu_bg" height="1"><img
						src="<s:url value="/images/spacer.gif" includeParams="none"/>"
						width="500px" height="1" alt="" class="dblk" /></td>
					<td><img
						src="<s:url value="/images/rounded/top_right_white.gif" includeParams="none"/>"
						width="9" height="9" alt="" class="dblk" /></td>
				</tr>
				<tr>
					<td class="menu_bg" height="150"></td>
					<td class="menu_bg" valign="top">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr height="5">
							</tr>
							<tr>
								<td colspan="2" align="left">
									<div class="deny">Access denied</div>
								</td>
							</tr>
						</table>
					</td>
					<td class="menu_bg" height="150"></td>
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
</body>
</html>
