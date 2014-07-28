<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<title>503 Service Temporarily Unavailable</title>
<link rel="stylesheet" href="<s:url value="/css/hm.css" includeParams="none"/>" type="text/css" />
<script language="JavaScript">
function fixLogoPng(){
	if(navigator.appVersion.indexOf("MSIE 6") != -1){
		//fix PNG bug in IE6
		var img = document.getElementById("logo_img");
		var imgName = img.src;
	  	if (null != imgName){
		  	var imgID = (img.id) ? "id='" + img.id + "' " : "";
		  	var imgClass = (img.className) ? "class='" + img.className + "' " : "";
		  	var imgTitle = (img.title) ? "title='" + img.title + "' " : "title='" + img.alt + "' ";
		  	var imgStyle = "display:inline-block;" + img.style.cssText;
		  	if (img.align == "left") imgStyle = "float:left;" + imgStyle
		  	if (img.align == "right") imgStyle = "float:right;" + imgStyle
		  	if (img.parentElement.href) imgStyle = "cursor:hand;" + imgStyle
		  	var strNewHTML = "<span " + imgID + imgClass + imgTitle
		  					+ " style=\"" + "width:" + img.width + "px; height:" + img.height + "px;" + imgStyle + ";"
		  					+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
		  					+ "(src=\'" + imgName + "\', sizingMethod='scale');\"></span>";
		  	img.outerHTML = strNewHTML;
	   	}
	}
}
</script>
<style>
.content_bg {
	text-align: center;
	padding-top: 100px;
}

span {
	font-family: Century Gothic,  Helvetica,Arial,sans-serif;
	color: #C84B00;
	font-size: 24px;
	font-weight: bold;
	margin-bottom:44px;
}

p {
	color:#6F6F6F;
	font-family:Helvetica,Arial,Sans Seril;
	font-size:18px;
	margin-bottom:32px;
}
</style>
</head>
<body class="body_bg client skin_hm" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="fixLogoPng()">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="top_nav_bg" style="padding-left: 5px;">
			<img id="logo_img" src="<s:url value="/images/company_logo_reverse.png"/>"
				width="128" height="55" alt="" class="dblk">
		</td>
		<td class="top_nav_bg" width="100%" colspan="2"></td>
		<td width="28"><img
			src="<s:url value="/images/hm/header-blue-graphic.png" includeParams="none"/>"
			width="28" alt="" class="dblk"></td>
	</tr>
</table>
<div class="content_bg"><span>Service Temporarily Unavailable</span>
<p>The server is temporarily unable to service your request </p>
<p><s:if test="%{fieldErrors.size > 0}"><s:fielderror /></s:if>
<s:else>due to maintenance downtime or services being restarted.</s:else></p> 
<p>Please try again later.</p>
<p>If you have any other problems, please contact <a href="http://www.aerohive.com">Aerohive Networks, Inc.</a></p>
</div>
</body></html>