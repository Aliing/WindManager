<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="%{selectFileName}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
</head>
<body>
<div id="content"><s:form action="auxiliaryFileMgmt">
	<div><pre><s:property
		value="%{fileDetail}" escape="false" /></pre></div>
</s:form></div>
</body>
</html>