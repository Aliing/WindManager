<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
</head>

<frameset rows="80,*" frameborder="no" border="0" framespacing="0">
	<frame src="" name="topFrame" scrolling="no" noresize="noresize"
		id="topFrame" title="topFrame" style="background-color:#FF0000" />
	<frameset rows="*,24" cols="*" framespacing="0" frameborder="no"
		border="0">
		<frameset rows="*" cols="235,*" framespacing="0" frameborder="no"
			border="0">
			<frameset rows="*,50" frameborder="no" border="0" framespacing="0">
				<frame src="" name="leftFrame" scrolling="no" noresize="noresize"
					id="leftFrame" title="leftFrame" style="background-color:#00FF00" />
				<frame
					src="<s:url action="configTest" includeParams="none"><s:param name="operation" value="%{'mgmtLeft'}" /></s:url>"
					name="bottomFrame1" scrolling="no" noresize="noresize"
					id="bottomFrame1" title="alarm" />
			</frameset>
			<frame src="" name="mainFrame" id="mainFrame" title="mainFrame"
				style="background-color:#0000FF" />
		</frameset>
		<frame src="/monitor/mgmt/note.jsp" name="bottomFrame" scrolling="no"
			noresize="noresize" id="bottomFrame" title="bottomFrame" />
	</frameset>
</frameset>
<noframes>
<body>
</body>
</noframes>
</html>
