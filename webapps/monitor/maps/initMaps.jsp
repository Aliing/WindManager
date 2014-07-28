<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script type="text/javascript"
	src="<s:url value="/js/hm.util.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
var formName = 'mapsInit';
function submitAction(operation) {
	if (validate(operation)) {
		hm.util.hide("notes");
		showProcessing();
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	var text = document.getElementById("mapName");
	var message = hm.util.validateNameWithBlanks(text.value, '<s:text name="hm.topology.init.map.company" />');
	if (message != null) {
		hm.util.reportFieldError(text, message);
		text.focus();
    	return false;
	}
	text = document.getElementById("mapStreet");
	message = hm.util.validateNameWithBlanks(text.value, '<s:text name="hm.topology.init.map.street" />');
	if (message != null) {
		hm.util.reportFieldError(text, message);
		text.focus();
    	return false;
	}
	text = document.getElementById("mapCity");
	message = hm.util.validateNameWithBlanks(text.value, '<s:text name="hm.topology.init.map.city" />');
	if (message != null) {
		hm.util.reportFieldError(text, message);
		text.focus();
    	return false;
	}
	return true;
}
function initConfirmDialog() {
}
function initWarnDialog() {
}
function createPlanningPanel(width, height){
	var div = document.getElementById("planningPanel");
	width = width || 300;
	height = height || 200;
	var iframe = document.getElementById("planning_tool");
	iframe.width = width;
	iframe.height = height;
	planningPanel = new YAHOO.widget.Panel(div, {modal:true, fixedcenter:"contained", visible:false, constraintoviewport:true } );
	planningPanel.render(document.body);
	div.style.display="";
	planningPanel.beforeHideEvent.subscribe(clearPlanningData);
}
function clearPlanningData(){
	// fix YUI issue with IE: tables in iframe will not disappear while overlay
	// hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("planning_tool").style.display = "none";
	}
}
function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" />');
	document.writeln('</td>');
}
function onLoadPage() {
	adjustContentHeight();
	document.getElementById("notes").innerHTML='<table width="600px" border="0" cellspacing="0" cellpadding="0" class="statusNote zinitStatusNote">' +
	'<tr><td height="5"></td></tr><tr><td class="initNoteInfo"><s:text name="hm.topology.init.map.note" /></td></tr><tr><td height="6"></td></tr></table>';
	hm.util.show("notes");
}
function adjustContentHeight() {
	var wh = YAHOO.util.Dom.getViewportHeight();
	var vpY = YAHOO.util.Dom.getY("content");
	YAHOO.util.Dom.setStyle("content", "height", (wh-vpY) + "px");	
}
</script>
<style>
td.initNoteInfo {
	font-weight: bold;
	color: #003366;
}
.initStatusNote {
	background-color: #FFFFCC;
}
</style>
<div id="content">
	<s:form action="mapsInit" enctype="multipart/form-data" method="post">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><tiles:insertDefinition name="context" /></td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td><tiles:insertDefinition name="notes" /></td>
			</tr>
			<tr>
				<td height="3"></td>
			</tr>
			<tr>
				<td>
					<table class="editBox" cellspacing="0" cellpadding="0" border="0"
						width="600px">
						<tr>
							<td style="padding: 6px 5px 5px 5px;">
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
									<tr>
										<td class="labelT1" width="160px"><s:text name="hm.topology.init.map.company" /><font color="red"><s:text
													name="*" /></font></td>
										<td width="160px"><s:textfield id="mapName" size="30"
												name="mapName"
												onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
												maxlength="32" /></td>
										<td></td>
									</tr>
									<tr>
										<td class="labelT1"><s:text name="hm.topology.init.map.street" /><font color="red"><s:text
													name="*" /></font></td>
										<td width="160px"><s:textfield id="mapStreet" size="30"
												name="mapStreet"
												onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
												maxlength="50" /></td>
										<td></td>
									</tr>
									<tr>
										<td class="labelT1"><s:text name="hm.topology.init.map.city" /><font color="red"><s:text
													name="*" /></font></td>
										<td width="160px"><s:textfield id="mapCity" size="30"
												name="mapCity"
												onkeypress="return hm.util.keyPressPermit(event,'nameWithBlank');"
												maxlength="50" /></td>
										<td></td>
									</tr>
									<tr>
										<td class="labelT1"><label><s:text name="hm.planning.config.countrycode" /></label></td>
										<td nowrap><s:select name="countryCode" list="%{countryCodeValues}"
														listKey="key" listValue="value" cssStyle="width: 350px;" /></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="buttons">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding-left: 0px;"><input type="button" id="ignore" name="ignore"
								value="Get Started" class="button"
								onClick="submitAction('init');"
								<s:property value="writeDisabled" />></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
