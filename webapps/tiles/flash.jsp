<%@taglib prefix="s" uri="/struts-tags"%>

<script language="JavaScript" type="text/javascript">
<!--
// Version check based upon the values entered above in "Globals"
var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
// Check to see if the version meets the requirements for playback
if (hasRequestedVersion) {
    	// if we've detected an acceptable version
		// embed the Flash Content SWF when all tests are passed
		AC_FL_RunContent(
					"src", "<s:url value="/monitor/reports/" includeParams="none"/>${swf}",
					"width", "${width}",
					"height", "${height}",
					"align", "middle",
					"id", "${application}",
					"quality", "high",
					"bgcolor", "${bgcolor}",
					"name", "${application}",
					"allowScriptAccess","sameDomain",
					"type", "application/x-shockwave-flash",
					"wmode", "opaque",
					"pluginspage", "http://www.adobe.com/go/getflashplayer"
	);
} else {  // flash is too old or we can't detect the plugin
    var alternateContent = 'This content requires the Adobe Flash Player. '
   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
    document.write(alternateContent);  // insert non-flash content
}
// -->
</script>
<noscript><object
	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
	id="${application}" width="${width}" height="${height}"
	codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
	<param name="movie"
		value="<s:url value="/monitor/reports/" includeParams="none"/>${swf}.swf" />
	<param name="quality" value="high" />
	<param name="bgcolor" value="${bgcolor}" />
	<param name="allowScriptAccess" value="sameDomain" />
	<param name="wmode" value="opaque"/>
	<embed src="${swf}.swf" quality="high" bgcolor="${bgcolor}"
		width="${width}" height="${height}" name="${application}"
		align="middle" play="true" loop="false" quality="high"
		allowScriptAccess="sameDomain" type="application/x-shockwave-flash"
		pluginspage="http://www.adobe.com/go/getflashplayer" wmode="opaque">
	</embed> </object></noscript>
