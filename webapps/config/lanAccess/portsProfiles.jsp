<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<s:if test="portType == 1">
<tiles:insertAttribute name="portAuthJson" />
</s:if>