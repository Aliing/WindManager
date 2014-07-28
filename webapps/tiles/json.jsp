<%@taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/html");
%>
<s:property escape="false" value="%{JSONString}" />
