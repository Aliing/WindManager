<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%> 

<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/ahdatatable/ahdatatable.css" includeParams="none"/>?v=<s:property value="verParam" />" />

<div>
<s:if test="%{dataSource.deviceInfo.sptEthernetMore_24}">
<style type="text/css">
/* body {
	margin:0;
	padding:0;
} */
</style>

<script type="text/javascript">
function disabledMacAddressLearingAll(checked){
	if(checked){
		Get("partVlans").disabled = true;
	}else{
		Get("partVlans").disabled = false;
	}
	
	Get("disabledPartVlans").checked = false;
	Get("partVlansTd").style.display = "none";
}

function disabledMacAddressLearingPart(checked){
	if(checked){
		Get("partVlansTd").style.display = "";
		Get("partVlans").disabled = false;
	}else{
		Get("partVlansTd").style.display = "none";
		Get("partVlans").disabled = true;
	}
	Get("disabledAllVlans").checked = false;
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

function validateForwardingDBVlans(operation){
	if(operation == "create2" || operation == "update2"){
		if(Get("disabledPartVlans").checked){
			var vlan = document.getElementById("partVlans");
			if(vlan.value.length == 0){
				hm.util.reportFieldError(vlan, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.forwardingdb.settings.enable.maclearning.part" /></s:param></s:text>');
				vlan.focus();
		        return false;
			}
			var vlanList =vlan.value.split(',');
			var vlanValues = new Array();
			var vlanRangerValues = new Array();
			for(var j=0;j<vlanList.length;j++){
				var pattern = /^(\d+-)?\d+$/;
				if(!pattern.test(vlanList[j])){
					hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.enable.maclearning.part" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
				var vlans = vlanList[j].split('-');
				var numVlan0=Number(vlans[0]);
				var numVlan1=Number(vlans[1]);
				var pattern_value = /^[1-9]\d{0,2}$|^[1-3]\d{3}$|^40[0-8][0-9]$|^409[0-4]$/; //1-4094
				for(var i=0;i<vlans.length;i++) {
					if(!pattern_value.test(vlans[i])){
						hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.enable.maclearning.part" /></s:param></s:text>');
						vlan.focus();
				        return false;
					}
				}
			    if(vlans.length>1 && numVlan0 > numVlan1){
			    	hm.util.reportFieldError(vlan, '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.enable.maclearning.part" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
			    if(vlans.length>1){
			    	for(var i=0;i<=numVlan1-numVlan0;i++){
						if(vlanValues.contains(numVlan0+i)){
							hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
							vlan.focus();
					        return false;
						} else {
							vlanValues.push(numVlan0+i);
						}
					}
			    } else {
			    	if(vlanValues.contains(numVlan0)){
						hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
						vlan.focus();
				        return false;
					} else {
						vlanValues.push(numVlan0);
					}
			    }
				
				if(vlanRangerValues.contains(vlanList[j])){
					hm.util.reportFieldError(vlan, '<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
					vlan.focus();
			        return false;
				} else {
					vlanRangerValues.push(vlanList[j]);
				}
			}
		}
		
	}
	return true;
}
</script>

<div id="forwardingDBdiv">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td><div><table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td height="10px"></td>
				</tr>
				<tr>
					<td><script type="text/javascript">insertFoldingLabelContext('<s:text name="hiveAp.forwardingdb.settings" />','forwardingDB');</script></td>
				</tr>
				<tr>
					<td style="padding-left:7px"><div id="forwardingDB" style="display: <s:property value="%{dataSource.forwardingDBSettingStyle}"/>">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelT1" width="250px">
									<s:checkbox id="disabledAllVlans" name="dataSource.forwardingDB.disableMacLearnForAllVlans" 
										value="%{dataSource.forwardingDB.disableMacLearnForAllVlans}" 
										onclick="disabledMacAddressLearingAll(this.checked);"/>
									<s:text name="hiveAp.forwardingdb.settings.enable.maclearning.all"/></td>
								<td>
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td width="250px;">
												<s:checkbox id="disabledPartVlans" name="dataSource.forwardingDB.disableMacLearnForPartVlans" 
													value="%{dataSource.forwardingDB.disableMacLearnForPartVlans}" 
													onclick="disabledMacAddressLearingPart(this.checked);"/>
												<s:text name="hiveAp.forwardingdb.settings.enable.maclearning.part"/>
											</td>
											<td id="partVlansTd" style="display:<s:property value="fdbPartVlansStyle "/>">
												<s:textfield id="partVlans"  name="dataSource.forwardingDB.vlans"
													 onkeypress="return hm.util.keyPressPermit(event,'attribute');"
													 disabled="%{dataSource.forwardingDB.disableMacLearnForAllVlans}"/> 
												&nbsp;<s:text name="config.vlanGroup.vlans.range" />
											</td>
										</tr>
									</table>
								</td>
								
								
							</tr>
							<%-- <tr id="selectedMacLearingVlans" style="display:<s:property value="selectedMacLearningForVlansStyle"/>">
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
							</tr> --%>
							<tr>
								<td colspan="2">
									<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
											<td class="labelT1" style="padding: 5px 0 5px 15px;" ><s:text name="hiveAp.forwardingdb.settings.idle.timeout" /></td>
											<td><s:textfield name="dataSource.forwardingDB.idleTimeout"
												size="24" maxlength="4"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"/>
											<s:text name="hiveAp.forwardingdb.settings.idle.timeout.range" /></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td style="padding: 5px 0 5px 10px;" colspan="2">
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

function onLoadAhDataTableForForwardingDB() {
	var ahDtClumnDefs = eval('<s:property escape="false" value="fdb_ahDtClumnDefs"/>');
	var dataSource = eval('<s:property escape="false" value="fdb_ahDtDatas"/>');
        
    var myColumnDefs = [
		{
			type: "text",
			mark: "vlans",
			editMark:"fdb_vlans",
			display: '<s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.vlan"/> <s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.vlan.range"/>',
			keypress:"ten",
			width:"150px",
			maxlength:"4",
			validate:validateVlanId
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
			keypress:"macaddress",
			validate:validateMacAddress,
			width:"100px",
			maxlength:17
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

    var forwardingDBahDataTable = new AhDataTablePanel.DataTablePanel("ahDataTableForFDB",myColumns,dataSource);
    forwardingDBahDataTable.render();
    
    function validateMacAddress(data){
    	if (data.length == 0)
    	{
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress" /></s:param></s:text>');
            return false;
    	}else if(!validateInvalidMacAddress(data)){
    		return false;
    	}
    	
    	return true;
    }
    
    function validateMacAddressSecondChar(data){
    	if(data.substring(1,2) == "1"
			|| data.substring(1,2) == "3"
				|| data.substring(1,2) == "5"
				|| data.substring(1,2) == "7"
				|| data.substring(1,2) == "9"
				|| data.substring(1,2) == "B"
				|| data.substring(1,2) == "D"
				|| data.substring(1,2) == "F"){
			//Adding Static Multicast (x{0}-xx-xx-xx-xx-xx) is NOT Supported.
			var pattern1 = /[A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}/;
			var pattern2 =  /[A-F\d]{4}[:|\.|-][A-F\d]{4}[:|\.|-][A-F\d]{4}/;
			
			if(pattern1.test(data)){
				hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.secodeOdd"><s:param>'+data.substring(1,2)+'</s:param>'+'<s:param>'+data.substring(2,3)+'</s:param></s:text>');
			}else if(pattern2.test(data)){
				hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.secodeOddFourGroup"><s:param>'+data.substring(1,2)+'</s:param>'+'<s:param>'+data.substring(4,5)+'</s:param></s:text>');
			}else{
				hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.secodeOddAll"><s:param>'+data.substring(1,2)+'</s:param></s:text>');
			}
			
			return false;
		}
		
		return true;
    }
    
    function validateMacAddressAllZeroF4Format1(data){
    	var all0_format = /[0]{2}[:|\.|-][0]{2}[:|\.|-][0]{2}[:|\.|-][0]{2}[:|\.|-][0]{2}[:|\.|-][0]{2}/;
		if(all0_format.test(data)){
			//Adding Static All Zero is NOT Supported.
			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allzero"></s:text>');
			return false;
		}
		var allf_format = /[F]{2}[:|\.|-][F]{2}[:|\.|-][F]{2}[:|\.|-][F]{2}[:|\.|-][F]{2}[:|\.|-][F]{2}/;
		if(allf_format.test(data)){
			//Adding Static Broadcast is NOT Supported.
			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allf"></s:text>');
			return false;
		}
		return true;
    }
    
    function validateMacAddressAllZeroF4Format2(data){
    	var all0_format =/[0]{4}[:|\.|-][0]{4}[:|\.|-][0]{4}/;
		if(all0_format.test(data)){
			//Adding Static All Zero is NOT Supported.
			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allzero"></s:text>');
			return false;
		}
		var allf_format = /[F]{4}[:|\.|-][F]{4}[:|\.|-][F]{4}/;
		if(allf_format.test(data)){
			//Adding Static Broadcast is NOT Supported.
			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allf"></s:text>');
			return false;
		}
		return true;
    }
    
    function validateMacAddressAllZeroF4Format3(data){
    	var all0_format =/[0]{12}/
   		if(all0_format.test(data)){
   			//Adding Static All Zero is NOT Supported.
   			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allzero"></s:text>');
   			return false;
   		}
    	var allf_format = /[F]{12}/
   		if(allf_format.test(data)){
   			//Adding Static Broadcast is NOT Supported.
   			hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.forwarding.db.allf"></s:text>');
   			return false;
   		}
    	return true;
    }
    
    function validateInvalidMacAddress(data){
    	//00:11:22:33:44:55  or  00-11-22-33-44-55 or 00.11.22.33.44.55
    	var supportFormat1 = true;
    	//0011:2233:4455 or 0011-2233-4455 or 0011.2233.4455
    	var supportFormat2 = true;
    	//001122334455
    	var supportFormat3 = true;
    	//all change to uppercase
    	data = data.toUpperCase();
    	
    	if(supportFormat1){
    		//00:11:22:33:44:55  or  00-11-22-33-44-55 or 00.11.22.33.44.55
    		var pattern1 = /[A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}[:|\.|-][A-F\d]{2}/;
       		if(!pattern1.test(data)){
       			supportFormat1 = false;
       		}else{
       			//the separator should be the same
       			if(!((data.substring(2,3) == data.substring(5,6)) 
       					&&(data.substring(5,6) == data.substring(8,9))
       					&&(data.substring(8,9) == data.substring(11,12))
       					&&(data.substring(11,12) == data.substring(14,15)))){
       				supportFormat1 = false;
       			}
       			if(data.length != 17){
       				supportFormat1 = false;
       			}
       			if(!validateMacAddressAllZeroF4Format1(data)){
       				return false;
       			}
       		}
    	}
    	
    	if(supportFormat2){
    		//0011:2233:4455 or 0011-2233-4455 or 0011.2233.4455
    		var pattern2 = /[A-F\d]{4}[:|\.|-][A-F\d]{4}[:|\.|-][A-F\d]{4}/;
    		if(!pattern2.test(data)){
    			supportFormat2 = false;
    		}else{
    			//the separator should be the same
    			if(!((data.substring(4,5) == data.substring(9,10)))){
       				supportFormat2 = false;
       			}
    			//make constraint for the length
    			if(data.length != 14){
    				supportFormat2 = false;
    			}
    			if(!validateMacAddressAllZeroF4Format2(data)){
    				return false;
    			}
    		}
    	}
    	
    	if(supportFormat3){
    		//001122334455
    		var pattern3 = /[A-F\d]{12}/;
    		if(!pattern3.test(data)){
    			supportFormat3 = false;
    		}else{
    			if(data.length != 12){
    				supportFormat3 = false;
    			}
    			
    			if(!validateMacAddressAllZeroF4Format3(data)){
    				return false;
    			}
    		}
    		
    	}
		
    	if(!supportFormat1&&!supportFormat2&&!supportFormat3){
    		//three supported format all not adapted
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.formatInvalid"><s:param><s:text name="hiveAp.forwardingdb.settings.static.macaddress.entry.macaddress" /></s:param></s:text>');
    		return false;
    	}
    	
    	if(!validateMacAddressSecondChar(data)){
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
    
    function validateVlanId(data){
    	var inputElement = data;
    	if(inputElement.length != 0){
    		var message = hm.util.validateIntegerRange(inputElement, '<s:text name="config.switchSettings.igmp.vlan.id" />',
    	            1,4094);
    		if (message != null) {
    			hm.util.reportFieldError(Get("tableErrorForFDB"), message);
    			return false;
    		}
    	}else{
    		hm.util.reportFieldError(Get("tableErrorForFDB"), '<s:text name="error.requiredField"><s:param><s:text name="config.switchSettings.igmp.vlan.id" /></s:param></s:text>');
    		return false;
    	}
    	
    	return true;
    }
}
</script>
</s:if>
</div>