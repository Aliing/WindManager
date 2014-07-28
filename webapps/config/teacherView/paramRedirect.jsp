<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Teacher View Redirection</title>
</head>
<body onload="document.cheatForm.submit();">
<p>You are being redirected to TeacherView...</p>
<form name="cheatForm" action="<s:property value="redirectURL" />" method="post">
	 <input type="hidden" name="classId" value="<s:property value="className" />"/>
	 <input type="hidden" name="rosterType" value="<s:property value="rosterType" />"/>
	 <input type="hidden" name="apStudents" value="<s:property value="apStudents" />"/>
	 <input type="hidden" name="expiredTime" value="<s:property value="expiredTime" />"/>
	 <input type="hidden" name="action" value="<s:property value="redirectAction" />"/>
	 <input type="hidden" name="hmTVUrl" value="<s:property value="hMTVURL" />"/>
	 <input type="hidden" name="proxyServer" value="<s:property value="proxyServer" />"/>
	 <input type="hidden" name="caseInsensitive" value="<s:property value="caseInsensitive" />"/>
	 <input type="hidden" name="adminUserName" value="<s:property value="adminUserName" />"/>
</form>
</body>
</html>