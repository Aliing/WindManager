<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.ui.actions.Navigation"%>
<%@page import="com.ah.bo.hiveap.HiveApUpdateSettings"%>

<link rel="stylesheet" type="text/css" href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css" href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<link type="text/css" rel="stylesheet" href="<s:url value="/yui/datatable/assets/skins/sam/datatable.css" includeParams="none"/>"></link>
<script type="text/javascript" src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript" src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<!-- Dependencies -->
<script type="text/javascript"
	src="<s:url value="/yui/datasource/datasource-min.js" includeParams="none"/>"></script>
<!-- Source files -->
<script type="text/javascript"
	src="<s:url value="/yui/datatable/datatable-min.js" includeParams="none"/>"></script>

<style type="text/css">
<!--
    #scriptPanel .bd {
        overflow:auto;
        height:35em;
        background-color:#fff;
        padding:10px;
    }
    #scriptPanel .ft {
        height:15px;
        padding:0;
    }
    #scriptPanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }
	#calendarpicker button, #calendarpicker1 button {
	    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
	    *margin: 2px 0; /* For IE */
	    *height: 1.5em; /* For IE */
	}
	
	#configOptions{
		height: 0px; 
		width: 100%;
		position: absolute; 
		padding: 0 10px 3px;
		top: -6px; 
		left: -10px; 
		background: #FFFFFF; 
		border-bottom: 1px solid #999;
		overflow: hidden;
	}
	
	.wzdToolBar{
		margin: 1px; 
		padding: 2px 10px 0; 
		text-align: right; 
/* 		background-color: #EDF5FF;  */
	}
	.wzdToolBar a{
		text-decoration: none;
		color: #003366;
		margin-right: 5px;
		font-weight: bold;
	}
	.wzdToolBar a span{
		font-size: 12px;
		color: #99CCCC;
	}
	a#saveOption:hover img, a#exitOption:hover img{
		filter: alpha(opacity="50");
		opacity: 0.5;
	}
	.wzdToolBar a:hover span{
		 color: #003366;
	}
-->
</style>

<script type="text/javascript">
	var formName = "hiveApUpdate";
	
	var ACTIVATE_TYPE_AT = '<%=HiveApUpdateSettings.ActivateType.activateAtTime.toString()%>';
	var ACTIVATE_TYPE_AFTER = '<%=HiveApUpdateSettings.ActivateType.activateAfterTime.toString()%>';
	var ACTIVATE_TYPE_NEXT = '<%=HiveApUpdateSettings.ActivateType.activateNextTime.toString()%>';
	
	var CONFIG_SELECT_TYPE_FULL = '<%=HiveApUpdateSettings.ConfigSelectType.full.toString()%>';
	var CONFIG_SELECT_TYPE_DELTA_C = '<%=HiveApUpdateSettings.ConfigSelectType.deltaConfig.toString()%>';
	var CONFIG_SELECT_TYPE_DELTA_R = '<%=HiveApUpdateSettings.ConfigSelectType.deltaRunning.toString()%>';
	var CONFIG_SELECT_TYPE_AUTO = '<%=HiveApUpdateSettings.ConfigSelectType.auto.toString()%>';
	
	function onLoadPage() {
		//top.updateOptionPanel = window;
	}

	function showConfigOption(configType){
		var activateTimeTr = document.getElementById("activateTimeTr");
		var configOptionsDiv = document.getElementById("configOptions");
		var configSpaceDiv = document.getElementById("configSpace");
		switch(configType){
		case CONFIG_SELECT_TYPE_FULL:
		case CONFIG_SELECT_TYPE_AUTO:
			activateTimeTr.style.display = "";
			break;
		case CONFIG_SELECT_TYPE_DELTA_C:
		case CONFIG_SELECT_TYPE_DELTA_R:
			activateTimeTr.style.display = "none";
			break;
		}
	}
	
	function configActivateChange(activateValue){
		var activateAtElement_hour = document.getElementById("configHour");
		var activateAtElement_minute = document.getElementById("configMin");
		var activateAfterElement_offTime = document.getElementById("configOffset");
	
		switch(activateValue){
			case ACTIVATE_TYPE_AT:
			activateAtElement_hour.disabled= false;
			activateAtElement_minute.disabled = false;
			activateAfterElement_offTime.disabled = true;
			break;
			case ACTIVATE_TYPE_AFTER:
			activateAtElement_hour.disabled= true;
			activateAtElement_minute.disabled = true;
			activateAfterElement_offTime.disabled = false;
			break;
			case ACTIVATE_TYPE_NEXT:
			activateAtElement_hour.disabled= true;
			activateAtElement_minute.disabled = true;
			activateAfterElement_offTime.disabled = true;
			break;
		}
	}
	
	function validateConfigActivateTime(){
		var activateAtElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_AT);
		var activateAfterElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_AFTER);
		var activateNextElement = document.getElementById(formName+"_configActivateType" + ACTIVATE_TYPE_NEXT);
		if(null == activateAtElement || null == activateAfterElement || null == activateNextElement){
			return true;
		}
		if(activateAfterElement.checked){
			var activateAfterElement_offTime = document.getElementById("configOffset");
	
			if(activateAfterElement_offTime.value.length == 0){
				hm.util.reportFieldError(activateAfterElement_offTime, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.update.configuration.offsetTime" /></s:param></s:text>');
				return false;
			}
			var message = hm.util.validateIntegerRange(activateAfterElement_offTime.value, '<s:text name="hiveAp.update.configuration.offsetTime" />',
			                                           <s:property value="0" />,
			                                           <s:property value="3600" />);
			if (message != null) {
				hm.util.reportFieldError(activateAfterElement_offTime, message);
				return false;
			}
		}
		return true;
	}
	
	function validateSaveConfigItem(){
		var configConfiguration = document.getElementById(formName+"_dataSource_configConfiguration");
		var configCwp = document.getElementById(formName+"_dataSource_configCwp");
		var configCertificate = document.getElementById(formName+"_dataSource_configCertificate");
		var configUserDatabase = document.getElementById(formName+"_dataSource_configUserDatabase");
		if(!configConfiguration.checked && !configCwp.checked && !configCertificate.checked && !configUserDatabase.checked){
			hm.util.reportFieldError(configConfiguration, '<s:text name="error.hiveap.config.item.select" />');
			return false;
		}
		return true;
	}
	
	function saveOption(){
		if(!validateConfigActivateTime() || !validateSaveConfigItem()){
			return;
		}
		var url = "<s:url action='hiveApUpdate' includeParams='none'/>" + "?ignore=" + new Date().getTime();
		
		document.forms[formName].operation.value = "saveConfigOption";
		YAHOO.util.Connect.setForm(document.getElementById(formName));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success:saveConfigOptionsResult}, null);
	}
	
	function uploadOpt(){
		if(!validateConfigActivateTime()){
			return;
		}
		parent.closeUpdateOptionPanel();
		
		var configConfigurationValue = document.getElementById(formName + "_dataSource_configConfiguration").checked;
		var configCwpValue = document.getElementById(formName + "_dataSource_configCwp").checked;
		var configCertificateValue = document.getElementById(formName + "_dataSource_configCertificate").checked;
		var configUserDatabaseValue = document.getElementById(formName + "_dataSource_configUserDatabase").checked;
		var param = "configConfiguration="+configConfigurationValue+
					"&configCwp="+configCwpValue+
					"&configCertificate="+configCertificateValue+
					"&configUserDatabase="+configUserDatabaseValue+
					"&saveUploadSetting=false";
		
		var configSelectTypes = document.getElementsByName("configSelectType");
		for(var i=0; i<configSelectTypes.length; i++){
			if(configSelectTypes[i].checked){
				param = param + "&configSelectType="+configSelectTypes[i].value;
			}
		}
		
		if(configSelectTypes[0].checked || configSelectTypes[1].checked){
			var configActivateTypes = document.getElementsByName("configActivateType");
			for(var j=0; j<configActivateTypes.length; j++){
				if(configActivateTypes[j].checked){
					param = param + "&configActivateType="+configActivateTypes[j].value;
				}
			}
			var configDateValue = document.getElementById("configDate").value;
			var configHourValue = document.getElementById("configHour").value;
			var configMinValue = document.getElementById("configMin").value;
			param = param + "&configDate="+configDateValue+
							"&configHour="+configHourValue+
							"&configMinValue="+configMinValue;
		}	
		<s:if test="easyMode">
			parent.configUpdateAdvanceMode_express(param);
		</s:if>
		<s:else>
			parent.configUpdateAdvanceMode(param);
		</s:else>
	}
	
	function saveConfigOptionsResult(o){
		parent.closeUpdateOptionPanel();
	}
</script>
<script type="text/javascript">

function formatValue(value){
    var v=value;
    if(value.length==0)
        return v;
    if(parseInt(value)<=9)
           v="0"+value;
    return v;
}
YAHOO.util.Event.onDOMReady(function () {
        function onButtonClick1() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu.setBody("&#32;");
            oCalendarMenu.body.id = "calendarcontainer";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id,{navigator: true});
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {

                    oCalendarMenu.show();

                }, 0);

            });

            /*
                Subscribe to the Calendar instance's "select" event to
                update the month, day, year form fields when the user
                selects a date.
            */
            oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
                var aDate;
                if (p_aArgs) {
                    aDate = p_aArgs[0][0];
                    var beginDate_doc = document.getElementById("configDate");
                    beginDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick1);

        };
       
        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");
        
        // Create a Button instance of type "menu"
        var startDateTimeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "configDateTimeDiv" });

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        startDateTimeButton.on("click", onButtonClick1);
	});
</script>
<div id="content"><s:form id="hiveApUpdate" action="hiveApUpdate">
	<s:hidden name="operation" />
	<table class="settingBox" cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td heigh="10px" ></td>
		</tr>
		<tr>
			<td align="right" style="padding-right: 20px">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td heigh="10px" colspan="3"></td>
					</tr>
					<tr>
						<td class="npcButton" align="right">
							<a href="javascript:void(0);" class="btCurrent" onclick="saveOption();" title="Save" <s:property value="writeDisabled" />><span>Save</span></a>
						</td>
						<td width="20px">&nbsp;</td>
						<td class="npcButton" align="right">
							<a href="javascript:void(0);" class="btCurrent" onclick="uploadOpt();" title="Upload" <s:property value="writeDisabled" />><span>Upload</span></a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td heigh="5px" ></td>
		</tr>
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" border="0">
					<tr><td height="5px"></td></tr>
					<tr>
						<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType1}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
					</tr>
					<tr>
						<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType2}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
					</tr>
					<tr>
						<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType3}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
					</tr>
					<tr>
						<td><s:radio label="Gender" name="configSelectType" value="%{dataSource.configSelectType}" list="%{configSelectType4}" onchange="showConfigOption(this.value);" onclick="this.blur();" listKey="key" listValue="value" /></td>
					</tr>
					<tr><td height="5px"></td></tr>
				</table>
			</td>
		</tr>
		<tr><td height="5px"></td></tr>
		<tr id="activateTimeTr" style="display: <s:property value="%{activateTimeStyle}"/>">
			<td>
				<table cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
					<fieldset><legend><s:text name="hiveAp.update.configuration.time.wizard.tag"/></legend>
					<table cellspacing="0" cellpadding="0" border="0">
					<tr><td height="2px"></td></tr>
					<tr>
						<td width="150px"><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType1}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
						<td><s:textfield id="configDate" name="configDate" readonly="true" size="10" maxlength="10" /></td>
						<td width="30px"><div id="configDateTimeDiv" /></td>
						<td><s:select id="configHour" name="configHour" list="ENUM_HOURS" listKey="key"
												listValue="value" disabled="%{configActiveAtDisabled}" />
							<s:select id="configMin" name="configMin" list="ENUM_MINUTES" listKey="key"
												listValue="value" disabled="%{configActiveAtDisabled}" /></td>
					</tr>
					<tr><td height="10"></td></tr>
					<tr>
						<td><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType2}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
						<td colspan="3"><s:textfield id="configOffset" name="dataSource.configActivateOffset" disabled="%{configActiveAfterDisabled}" size="4"
							maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
							<s:text name="hiveAp.update.configuration.activateAfterTip"/></td>
					</tr>
					<tr><td height="10"></td></tr>
					<tr>
						<td colspan="5"><s:radio label="Gender" name="configActivateType" value="%{dataSource.configActivateType}" list="%{configActivateType3}" listKey="key" listValue="value" onclick="configActivateChange(this.value);"/></td>
					</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<tr><td height="10px"></td></tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table cellspacing="0" cellpadding="0" border="0">
				<tr><td height="5px"></td></tr>
				<tr>
					<td><s:checkbox name="dataSource.configConfiguration" /></td>
					<td><s:text name="hiveAp.update.configuration.item.configuration" /></td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td><s:checkbox name="dataSource.configCwp" /></td>
					<td><s:text name="hiveAp.update.configuration.item.cwp" /></td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td><s:checkbox name="dataSource.configCertificate" /></td>
					<td><s:text name="hiveAp.update.configuration.item.certificate" /></td>
				</tr>
				<tr><td height="5px"></td></tr>
				<tr>
					<td><s:checkbox name="dataSource.configUserDatabase" /></td>
					<td><s:text name="hiveAp.update.configuration.item.credential" /></td>
				</tr>
				<tr><td height="5px"></td></tr>
				</table>
			</td>
		</tr>
	</table>
</s:form></div>