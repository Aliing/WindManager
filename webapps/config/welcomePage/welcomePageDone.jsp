<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<link type="text/css" rel="stylesheet"
	href="<s:url value="/config/welcomePage/newWelcomePage.css" includeParams="none"/>"></link>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />"></script>

<div class="content">
<s:form action="newStartHere" name="newStartHere" id="newStartHere">
	<s:hidden name="operation"/>
	<div class="config_done">
		<div class="step_sub_title"><s:text name="hm.missionux.wecomle.done.configuration" /></div>
		<div class="step_sub_content">
			<div class="sub_content_tip"><s:text name="hm.missionux.wecomle.done.main.content" /></div>
			<div class="config_tip">
				<span><s:text name="hm.missionux.wecomle.done.main.action.tip" /></span>
				<input type="button" class="startHere_common_btn config_done_btn" value="Configure"/>
			</div>
		</div>
	</div>
</s:form>
</div>

<script type="text/javascript">
	var formName = "newStartHere";
	function submitAction(operation) {
		document.forms[formName].operation.value = operation;
		document.forms[formName].submit();
	}
	
	$(function() {
		$("input.config_done_btn").click(function(e) {
			submitAction("startUsingHM");
		});
	});
</script>