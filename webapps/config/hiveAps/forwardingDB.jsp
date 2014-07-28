<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%> 

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script src="<s:url value="/js/widget/dataTable/ahDataTable.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>


<style type="text/css">
/* body {
	margin:0;
	padding:0;
} */
</style>

<script type="text/javascript">
function enableMacAddressLearingAll(checked){
	if(checked){
		Get("selectedMacLearingVlans").style.display = "none";
	}else{
		Get("selectedMacLearingVlans").style.display = "";
	}
}

function validateForwardingDBIdleTimeout(operation){
	if(operation == 'create2' || operation == 'update2'){
		var idleTimeout = document.getElementById(formName + "_dataSource_forwardingDB_idleTimeout");
		if(idleTimeout){
			if (idleTimeout.value.length == 0) {
				hm.util.reportFieldError(idleTimeout, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.forwardingdb.settings.idle.timeout" /></s:param></s:text>');
				showForwardingDBSetting();
				return false;
			}
			
			if(idleTimeout.value == 0){
				return true;
			}else{
				var message = hm.util.validateIntegerRange(idleTimeout.value, '<s:text name="hiveAp.forwardingdb.settings.idle.timeout" />',10,650);
				if (message != null) {
					hm.util.reportFieldError(idleTimeout, message);
					showForwardingDBSetting();
					return false;
				}
			}
		}
	}
	return true;
}

function showForwardingDBSetting(){
	showHideContent("forwardingDB", "")
}
</script>

<div id="forwardingDBdiv">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td><div><table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.forwardingdb.settings" />','forwardingDB');</script></td>
				</tr>
				<tr>
					<td style="padding-left:7px"><div id="forwardingDB" style="display: <s:property value="%{dataSource.forwardingDBSettingStyle}"/>">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td style="padding: 5px 0 5px 10px;" colspan="2">
									<s:checkbox name="dataSource.forwardingDB.enableMacLearnForAllVlans" value="%{dataSource.forwardingDB.enableMacLearnForAllVlans}" 
										onclick="enableMacAddressLearingAll(this.checked);"/>
									<s:text name="hiveAp.forwardingdb.settings.enable.maclearning.all"/></td>
							</tr>
							<tr id="selectedMacLearingVlans" style="display:<s:property value="selectedMacLearningForVlansStyle"/>">
								<td width="338px" colspan="2" style="padding: 5px 0 5px 30px;">
									<fieldset><legend><s:text
										name="hiveAp.forwardingdb.settings.enable.maclearning.vlans" /></legend>
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<s:push value="%{forwardingDBVlanList}">
												<td><tiles:insertDefinition
													name="optionsTransfer" /></td>
											</s:push>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
									</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td class="labelT1" style="padding: 5px 0 5px 10px;" width="140px"><s:text name="hiveAp.forwardingdb.settings.idle.timeout" /></td>
								<td><s:textfield name="dataSource.forwardingDB.idleTimeout"
									size="24" maxlength="4"
									onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
								<s:text name="hiveAp.forwardingdb.settings.idle.timeout.range" /></td>
							</tr>
							<tr>
								<td colspan="2" style="padding: 5px 0 5px 10px;">
									<fieldset><legend><s:text
										name="hiveAp.forwardingdb.settings.static.macaddress.entry" /></legend>
										<table cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td>
													<label id="tableErrorForFDB"></label>
												</td>
												<td>
													<div id="ahDataTableForFDB"></div>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</div></td>
				</tr>
			</table></div></td>
		</tr>
	</table>
</div>
<script type="text/javascript">
var forwardingDBahDataTable;
function onLoadAhDataTableForForwardingDB() {
	var ahDtClumnDefs = eval('<s:property escape="false" value="fdb_ahDtClumnDefs"/>');
	var dataSource = eval('<s:property escape="false" value="fdb_ahDtDatas"/>');
	var editInfo = "<s:property  escape='false' value='fdb_editInfo'/>";
	if (editInfo) {
		eval("var ahDtEditInfo = " + "<s:property  escape='false' value='fdb_editInfo'/>");
	}else {
		var ahDtEditInfo;
	}

    var myConfigs = {
  		editInfo:{
  			name:"editInfo",
  		}
    }
        
    $.extend(true, myConfigs, ahDtEditInfo)
        
    var myColumnDefs = [
		/* {
			type: "hidden",
			mark: "ruleIds",
			editMark:"ruleIds_edit",
			display: "RowId",
			defaultValue: ""
		}, */
		{
			type: "dropdown",
			mark: "vlans",
			editMark:"fdb_vlans",
			display: '<s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.vlan"/>',
			//changeCol:2,
			defaultValue: 0,
			width:"150px"
		},
        {
			type: "dropdownGenerate",
			mark: "interfaces",
			editMark:"fdb_interfaces",
			display: '<s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.interface"/>',
			defaultValue: -1,
			validate:validateInterface,
			width:"100px"
		},
		{
			type: "text",
			mark: "macAddress",
			display: '<s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress"/>',
			editMark:"fdb_macaddress",
			defaultValue: "",
			keypress:"hex",
			validate:validateMacAddress,
			width:"100px",
			maxlength:12
		}
	];
       
	var myColumns = [];
	for (var i = 0; i < myColumnDefs.length; i++) {
		var optionTmp = myColumnDefs[i];
		var bln = false;
		if(ahDtClumnDefs){
			for (var j = 0; j < ahDtClumnDefs.length; j++) {
				if (myColumnDefs[i].mark == ahDtClumnDefs[j].mark) {
					optionTmp = $.extend(true, optionTmp, ahDtClumnDefs[j]);
					myColumns.push(optionTmp);
					bln = true;
					break;
				}
			}
		}
		if(!bln){
			myColumns.push(optionTmp);
		}
		
	} 

    forwardingDBahDataTable = new AhDataTablePanel.DataTablePanel("ahDataTableForFDB",myColumns,dataSource,myConfigs);
    forwardingDBahDataTable.render();
    
    function validateMacAddress(data){
    	if (data.length == 0)
    	{
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress" /></s:param></s:text>');
            return false;
    	}else if (!hm.util.validateMacAddress(data,12))
    	{
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress" /></s:param></s:text>');
    		return false;
    	}else if(data == "ffffffffffff" || data.substring(0,2) == "01"
    			|| data.substring(0,2) == "11"
    			|| data == "000000000000"){
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress" /></s:param></s:text>');
    		return false;
    	}
    	
    	return true;
    }
    
    function validateInterface(data){
    	if(data.length == 0){
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.interface" /></s:param></s:text>');
            return false;
    	}
    	return true;
    }
}
</script>