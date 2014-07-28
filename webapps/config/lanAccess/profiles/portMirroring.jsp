<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<div id="mirrorSetion">
	<s:hidden id="operationChoose" value="" />
	<fieldset>
		<legend>
			<s:text name="config.lanProfile.title.monitor" />
		</legend>
		<table width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="10">
					<div id="mirrorNotes" style="display:none">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							style="padding-left: 7px;">
							<tr>
								<td height="5"></td>
							</tr>
							<tr>
								<td id="mirrorNotesConent" class="noteError"></td>
							</tr>
							<tr>
								<td height="6"></td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<s:if test="%{monitorPorts.size == 1}">
				<tr>
					<td style="width: 25px; padding: 5px 0 5px 0;"><s:checkbox
							id="enableSourceVlan"
							name="enableSourceVlan" value="%{enableSourceVlan}"
							onchange="changeSourceVlans(this.checked)" /></td>
					<td colspan="4" style="padding: 5px 0 2px 0;"><s:text
							name='config.lanProfile.title.monitor.source.type.vlan' /></td>
				</tr>
				<tr>
					<td></td>
					<td style="width: 150px; padding:5px 0 5px 0;"><s:text
							name='config.lanProfile.title.monitor.interface' /></td>
					<td style="padding-bottom: 5px">
					<s:hidden id="destinationInterface"	name="destinationInterface" value="%{monitorPorts[0].destinationPort}"/>
					<s:label value="%{monitorPorts[0].portName}" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="padding-bottom: 5px;"><s:text
							name='config.lanProfile.title.monitor.source.vlan' /></td>
					<td><s:textfield id="ingressVlan"
							name="ingressVlan" value="%{ingressVlan}"
							disabled="%{!enableSourceVlan}" 
							onkeypress="return hm.util.keyPressPermit(event,'attribute');"/></td>
				</tr>
			</s:if>
			<s:else>
				<tr>
					<td style="width: 25px; padding: 5px 0 5px 0;">
						<input type="checkbox" disabled="disabled" id="enableSourceVlan" />
						<input type="hidden" id="ingressVlan">
					</td>
					<td colspan="4" style="padding: 5px 0 2px 0;"><s:text
							name='config.lanProfile.title.monitor.source.type.vlan' /></td>
				</tr>
				<tr>
					<td></td>
					<td style="padding: 5px 0 5px 0;" colspan="5" class="noteInfo">
						<s:text name="config.lanProfile.mirror.vlan.note" />
					</td>
				</tr>
			</s:else>
			
			<tr>
				<td style="width: 25px; padding: 5px 0 5px 0;"><s:checkbox
						id="enableSourcePort"
						name="enableSourcePort" value="%{enableSourcePort}"
						onchange="enablePortBased(this.checked)" /></td>
				<td colspan="4" style="padding: 5px 0 2px 0;"><s:text
						name='config.lanProfile.title.monitor.source.type.port' /></td>
			</tr>
		</table>
		<table cellspacing="0" cellpadding="0" border="0" width="100%"
			id="tbl_port_mirroring" class="view">
			<tr>
				<th class="colList" width="150em;" align="left"><s:text
						name="config.lanProfile.title.monitor.interface" /></th>
				<th class="colList" width="60em;" align="left" style="display: none"><s:text
						name="config.lanProfile.title.monitor.enable" /></th>
				<th class="colList" width="150em"><s:text
						name="config.lanProfile.title.monitor.type.direction" /></th>
				<th class="colList" width="250em;" colspan="2"><s:text
						name="config.lanProfile.title.monitor.source" /></th>
			</tr>
			<%-- <tiles:insertDefinition name="rowClass" /> --%>
			<tr>
				<td id="errorMessage"></td>
			</tr>

			<s:iterator value="%{monitorPorts}" status="status"
				id="monitorProfiles">
				<tr>
					<td><s:hidden value="%{#monitorProfiles.destinationPort}"
							cssClass="hiddenMark" /> <s:hidden
							id="label_%{#monitorProfiles.destinationPort}"
							value="%{#monitorProfiles.destinationPort}" /> <s:label
							name="label_%{#monitorProfiles.portName}"
							value="%{#monitorProfiles.portName}">
						</s:label></td>
					<td><s:text
							name="config.lanProfile.title.monitor.type.ingress" /></td>
					<td class="ellipsis"><s:label
							id="label_%{#monitorProfiles.destinationPort}_ingress"
							value="%{#monitorProfiles.ingressPorts}">
							<s:hidden id="hidden_%{#monitorProfiles.destinationPort}_ingress"
								name="ingressPorts" value="%{#monitorProfiles.ingressPort}" />
						</s:label></td>
					<td class="npcButton even" style="padding-left: 5px;"><a
						href="javascript:void(0);" class="btCurrent"
						title="<s:text name="config.networkpolicy.button.choose"/>"> <span
							id="button_<s:property value='%{#monitorProfiles.destinationPort}' />_ingress"
							onclick="selectPorts('<s:property value='%{#monitorProfiles.destinationPort}' />_ingress');">
								<s:text name="config.networkpolicy.button.choose" />
						</span>
					</a></td>
				</tr>
				<tr>
					<td class="odd"><s:hidden
							value="%{#monitorProfiles.destinationPort}" cssClass="hiddenMark" /></td>
					<td class="odd"><s:text
							name="config.lanProfile.title.monitor.type.egress" /></td>
					<td class="odd" style="min-width: 50px"><s:label
							id="label_%{#monitorProfiles.destinationPort}_egress"
							value="%{#monitorProfiles.egressPorts}">
							<s:hidden id="hidden_%{#monitorProfiles.destinationPort}_egress"
								name="egressPorts" value="%{#monitorProfiles.egressPort}" />
						</s:label></td>
					<td class="npcButton odd" style="padding-left: 5px;"><a
						href="javascript:void(0);" class="btCurrent"
						title="<s:text name="config.networkpolicy.button.choose"/>"> <span
							id="button_<s:property value='%{#monitorProfiles.destinationPort}' />_egress"
							onclick="selectPorts('<s:property value='%{#monitorProfiles.destinationPort}' />_egress');">
								<s:text name="config.networkpolicy.button.choose" />
						</span>
					</a></td>
				</tr>
				<tr>
					<td style="border-bottom: 1px solid darkgray;"><s:hidden
							value="%{#monitorProfiles.destinationPort}" cssClass="hiddenMark" /></td>
					<td style="border-bottom: 1px solid darkgray;"><s:text
							name="config.lanProfile.title.monitor.type.both" /></td>
					<td style="min-width: 50px; border-bottom: 1px solid darkgray">
						<s:label id="label_%{#monitorProfiles.destinationPort}_both"
							value="%{#monitorProfiles.bothPorts}">
							<s:hidden id="hidden_%{#monitorProfiles.destinationPort}_both"
								name="bothPorts" value="%{#monitorProfiles.bothPort}" />
						</s:label>
					</td>
					<td class="npcButton"
						style="padding-left: 5px; border-bottom: 1px solid darkgray"><a
						href="javascript:void(0);" class="btCurrent"
						title="<s:text name="config.networkpolicy.button.choose"/>"> <span
							id="button_<s:property value='%{#monitorProfiles.destinationPort}' />_both"
							onclick="selectPorts('<s:property value='%{#monitorProfiles.destinationPort}' />_both');">
								<s:text name="config.networkpolicy.button.choose" />
						</span>
					</a></td>
				</tr>
			</s:iterator>
		</table>
	</fieldset>
	<div id="portSelectListTable"
		style="width: 150px; display: none; position: fixed;">
		<div id="PortsListDiv" class="selectList"
			style="width: 100%; heigth: 100%; border: 1px solid darkgray;">
			<table width="100%" cellspacing="0" cellpadding="0"
				id="portsDataTable">
				<s:hidden id="listSize" value="%{sourcePortsList.size()}" />
				<s:iterator value="%{sourcePortsList}" id="sourcePortsList"
					status="status">
					<tr>
						<td style="display: none"><input type="checkbox"
							id="hidden_port_<s:property value="id"/>" name="idValues"
							value="<s:property value="id"/>"></input></td>
						<td id="port_<s:property value="id"/>"
							onclick="hm.util.selectRow(this, true,'portsDataTable');"
							class="selectListTD"><span
							title="<s:property value="value"/>"
							id="<s:property value="value"/>"><s:property value="value" /></span>
						</td>
					</tr>
				</s:iterator>
				<tr>
					<td style="display: none"><input type="checkbox"></input></td>
					<td id="noItem" style="display: none;" class="selectListTD"><span><s:text
								name="config.optionsTransfer.none" /></span></td>
				</tr>
			</table>
		</div>
		<div
			style="background-color: #F9F9F7; border: 1px solid darkgray; border-top: 0px solid darkgray; width: 100%">
			<table width="100%" cellspacing="0" cellpadding="0">
				<tr>
					<td class="npcButton" style="padding-left: 15px;"><a
						href="javascript:void(0);" class="btCurrent"
						onclick="javascript: selectSourcePorts();"
						title="<s:text name="config.networkpolicy.button.ok"/>"> <span>
								<s:text name="config.networkpolicy.button.ok" />
						</span>
					</a></td>
					<td class="npcButton" style="padding-left: 15px;"><a
						href="javascript:void(0);" class="btCurrent"
						onclick="javascript: cancelSelectPorts();"
						title="<s:text name="config.networkpolicy.button.cancel"/>"> <span>
								<s:text name="config.networkpolicy.button.cancel" />
						</span>
					</a></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
	function selectPorts(data){
		
		var pos = $("#button_"+ data).offset();
		$("#portSelectListTable").css({
		    left: pos.left - $(document).scrollLeft(),
		    top: pos.top - $(document).scrollTop()
		});
		
		// hide selected port
		var selected = new Array();
		selected = dataToArray(getAllSelectedPort());
		selected = selected.split(",");
		for(var s = 0; s < selected.length; s ++){
			if(isPortChannel(selected[s])){
				var portChannel = getPortChannel(selected[s]);
				for (var n = 0; n < portChannel.length; n ++) {
					$("#port_" + portChannel[n]).hide();
					$("#port_" + portChannel[n]).removeAttr("checked");
				}
			}
			if(isPortChannelMemberPort(selected[s])){
				var agg = getPortChannelIdByMemberPort(selected[s]);
				$("#port_" + agg).hide();
			}
			
			
			$("#port_" + selected[s]).hide();
			$("#port_" + selected[s]).css({
				background : "F9F9F7"
			});
			$("#port_" + selected[s]).removeAttr("checked");
		}
		
		if(document.getElementById("hidden_" + data).value != ""){
			var portVal = dataToArray(document.getElementById("hidden_" + data).value);
			
			var array = portVal.split(",");
			
			for (var i = 0; i < array.length; i ++){
				document.getElementById("port_" + array[i]).style.background = "#FFC20E";
				document.getElementById("port_" + array[i]).style.display = "block";
				document.getElementById("hidden_port_" + array[i]).checked = "checked";
				
				if (isPortChannelMemberPort(array[i])){
					var agg = getPortChannelIdByMemberPort(array[i]);
					$("#port_" + agg).show();
				}
				
				if (isPortChannel(array[i])){
					var memberPorts = getPortChannel(array[i]);
					for (var n = 0; n < memberPorts.length; n ++){
						$("#port_" + memberPorts[n]).show();
					}
				}
			}
			
		}
		var tmp = getPortListHideElement().length;
		if($("#listSize").val() - tmp > 8){
			$("#noItem").hide();
			$("#PortsListDiv").attr("class","selectList");
		}else if($("#listSize").val() - tmp != 0){
			$("#noItem").hide();
			$("#PortsListDiv").attr("class","selectListExpress");
		}else{
			$("#PortsListDiv").attr("class","selectListExpress");
			$("#noItem").show();
		}
		$("#portSelectListTable").show();
		document.getElementById("operationChoose").value = data;
		
	}
	
	function cancelSelectPorts(){
		initSelectPannel();
		$("#portSelectListTable").hide();
	}
	
	function selectSourcePorts(){
		var checkBoxs = document.getElementsByName("idValues");
		var chooseId = document.getElementById("operationChoose").value;
		
		var hiddenElement = getPortListHideElement();
		
		if(validateChoose(checkBoxs) != ""){
			var temp = $("#port_" + validateChoose(checkBoxs) + " :first-child").attr("id");
			hm.util.reportFieldError(document.getElementById("errorMessage"),'<s:text name="error.port.mirroring.choose"><s:param>'+temp+'</s:param></s:text>');
			return false;
		}
		var arrayPort = new Array();
		for (var i = 0; i < checkBoxs.length; i++) {
			if (checkBoxs[i].checked && checkBoxs[i].value) {
				
				if(isPortChannel(checkBoxs[i].value)){
					for(var s = 0; s < hiddenElement.length; s ++){
						if(checkBoxs[i].value == getPortChannelIdByMemberPort(hiddenElement[s])){
							var temp = $("#port_" + hiddenElement[s] + " :first-child").attr("id");
							hm.util.reportFieldError(document.getElementById("errorMessage"),'<s:text name="error.port.mirroring.choose"><s:param>'+temp+'</s:param></s:text>');
							return false;
						}
					}
					
					var portChannel = getPortChannel(checkBoxs[i].value);
					for (var n = 0; n < portChannel.length; n ++) {
						$("#port_" + portChannel[n]).hide();
						$("#port_" + portChannel[n]).removeAttr("checked");
					}
				}
				if(isPortChannelMemberPort(checkBoxs[i].value)){
					var agg = getPortChannelIdByMemberPort(checkBoxs[i].value);
					$("#port_" + agg).hide();
				}
				arrayPort.push(checkBoxs[i].value);
				document.getElementById("port_" + checkBoxs[i].value).style.display = "none";
				checkBoxs[i].checked = false;
			}
		}
		arrayPort.distinct();
		var tempVal = mergeRange(arrayPort);
		
		document.getElementById("hidden_" + chooseId).value = tempVal;
		
		document.getElementById("label_" + chooseId).innerHTML = addPrefix(tempVal);
		
		initSelectPannel();
		$("#portSelectListTable").hide();
	}
	
	function cleanArray(actual){
		var newArray = new Array();
			for(var i = 0; i < actual.length; i++){
		    	if (actual[i]){
		        	newArray.push(actual[i]);
		    	}
		  	}
		return newArray;
	}

	function initSelectPannel(){
		var hidden_checkBox = document.getElementsByName("idValues");
		for(var i = 0; i < hidden_checkBox.length; i ++){
			hidden_checkBox[i].checked = "";
		}
		$("#portsDataTable tr").find("td[class=selectListTD]").each(function (){
			$(this).css({"background":"#F9F9F7"})
		});
	}
	
	/* function toView(data){
		for (var a = 0; a < data.length; a ++)
		{
			if(parseInt(data[a]) == 24 && data[a + 1] != ";"){
				data.splice(a + 1,0,";");
			}else{
				if(data[a + 1] - data[a] != 1 && data[a] != ";"){
					data.splice(a + 1,0,";");
				}
			}
		}

		var newdata = data.toString().split(";");
		newdata = cleanArray(newdata);
		for (var a = 0; a < data.length; a ++)
		{
			if(newdata[a] != null && newdata[a] != "" ){
				newdata[a] = replaceIt(newdata[a]);
				if(newdata[a].length > 2){
					newdata[a] = newdata[a][0] + "-" + newdata[a][newdata[a].length - 1];
				}
			}
		}
		return newdata;
	} */

	function replaceIt(data){
		var newArray = new Array();
		if(data != null && data != ""){
			for (var i = 0; i < data.length; i ++)
			{
				newArray = cleanArray(data.split(","));
			}
		}
		return newArray;
	}
	
	function addPrefix(data){
		var newArray = data.toString().split(",");

		for (var i = 0; i < newArray.length; i ++){
			if(newArray[i].length > 0){
				if(newArray[i].indexOf("-") > 0){
					var temp = newArray[i].split("-");
					newArray[i] = $("#port_" + temp[0] + " :first-child").attr("id") + "-" + $("#port_" + temp[1] + " :first-child").attr("id");
				}else{
					newArray[i] = $("#port_" + newArray[i] + " :first-child").attr("id");
				}
			}else {
				return "";
			}
		}
		return newArray;
	}
	
	function validateMirrorSession(){
		var enableSourceVlan = Get("enableSourceVlan");
		var enableSourcePort = Get("enableSourcePort");
		var ingressPort = new Array();
		var egressPort = new Array();
		var bothPort = new Array();
		var tmpObjArray = new Array();
		
		if(!enableSourceVlan.checked && !enableSourcePort.checked){
			showMirrorNotes('<s:text name="error.port.mirroring.enable.required" />');
			return false;
		}else if(enableSourceVlan.checked){
			var ingressVlan = document.getElementById("ingressVlan");
			/* var destinationList = document.getElementById("destinationList"); */
			var interfaceId = document.getElementById("destinationInterface");
			if(typeof ingressVlan && ingressVlan.value.length < 1){
				showMirrorNotes('<s:text name="error.requiredField"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
				return false;
			}
			
			if(!validateVlans(ingressVlan)){
				return false;
			}
			
			egressPort = $("#hidden_" + interfaceId.value + "_egress").val();
			
			ingressPort = $("#hidden_" + interfaceId.value + "_ingress").val();
			
			bothPort = $("#hidden_" + interfaceId.value + "_both").val();
		}else{
			var egressPorts = document.getElementsByName("egressPorts");
			if(egressPorts.length > 0){
				for(var e = 0; e < egressPorts.length; e ++){
					if(egressPorts[e].value.length > 0){
						egressPort.push(egressPorts[e].value);
					}
				}
			}
			
			var ingressPorts = document.getElementsByName("ingressPorts");
			if(ingressPorts.length > 0){
				for(var e = 0; e < ingressPorts.length; e ++){
					if(ingressPorts[e].value.length > 0){
						ingressPort.push(ingressPorts[e].value);
					}
				}
			}
			
			var bothPorts = document.getElementsByName("bothPorts");
			if(bothPorts.length > 0){
				for(var e = 0; e < bothPorts.length; e ++){
					if(bothPorts[e].value.length > 0){
						bothPort.push(bothPorts[e].value);
					}
				}
			}
			
			if(bothPort.length == 0 && ingressPort.length == 0 && egressPort.length == 0){
				showMirrorNotes('<s:text name="error.port.mirroring.enable.required" />');
				return false;
			}
		}
		
		if(ingressPort.toString().length > 0){
			tmpObjArray = tmpObjArray.concat(dataToArray(ingressPort.toString()));
		}
		
		if(egressPort.toString().length > 0){
			tmpObjArray = tmpObjArray.concat(dataToArray(egressPort.toString()));
		}
		
		if(bothPort.toString().length > 0){
			tmpObjArray = tmpObjArray.concat(dataToArray(bothPort.toString()));
		}
		
		var tmpArray = new Array();
		tmpArray = tmpObjArray.toString().split(",");
		if(isRepeat(tmpArray)){
			showMirrorNotes('<s:text name="error.port.mirroring.source.port.check" />');
			return false;
		}
		return true;
	}
	
	function dataToArray(data){
		var newArray = data.split(",");
		for(var a = 0; a < newArray.length; a++){
			if(newArray[a].indexOf("-") > 0){
				var tempArray = newArray[a].split("-");
				var startNum = parseInt(tempArray[0]);
				var endNum = parseInt(tempArray[1]);
				tempArray = new Array();
				tempArray.push(startNum);
				while(startNum < endNum - 1){
					startNum = startNum + 1;
					tempArray.push(startNum);
				}
				tempArray.push(endNum);
				newArray[a] = tempArray;
			}

		}
		return newArray.distinct().toString();
	}
	
	function isRepeat(arr){
	    var hash = {};
	    for(var i in arr) {
	        if(hash[arr[i]])
	            return true;
	        hash[arr[i]] = true;
	    }
	    return false;
	}
	
	function isPortChannelMemberPort(portId){
		eval('var memberPorts = <s:property escape="false" value="allPortChannelMemberPorts"/>');
		var tmpArray = new Array();
		for (var port in memberPorts){
			tmpArray.push(memberPorts[port]);
		}
		tmpArray = tmpArray.toString().split(",");
		return tmpArray.contains(portId);
	}
	
	function isPortChannel(portId){
		eval('var memberPorts = <s:property escape="false" value="allPortChannelMemberPorts"/>');
		var tmpArray = new Array();
		for (var port in memberPorts){
			tmpArray.push(port);
		}
		return tmpArray.contains(portId);
	}
	
	function getPortChannel(portChannelId){
		eval('var memberPorts = <s:property escape="false" value="allPortChannelMemberPorts"/>');
		var tmpArray = new Array();
		for (var port in memberPorts){
			if(port == portChannelId){
				return memberPorts[port];
			}
		}
	}
	
	function getPortChannelByMemberPort(portId){
		eval('var memberPorts = <s:property escape="false" value="allPortChannelMemberPorts"/>');
		for (var port in memberPorts){
			if(memberPorts[port].contains(portId)){
				var tmpArray = new Array();
				tmpArray.push(port);
				tmpArray = tmpArray.concat(tmpArray,memberPorts[port])
				tmpArray.distinct();
				return tmpArray;
			}
		}
	}
	
	function getPortChannelIdByMemberPort(portId){
		eval('var memberPorts = <s:property escape="false" value="allPortChannelMemberPorts"/>');
		for (var port in memberPorts){
			if(memberPorts[port].contains(portId)){
				var tmpId = port;
				return tmpId;
			}
		}
	}
	
	Array.prototype.contains = function (element) {
	    for (var i = 0; i < this.length; i++) {
	        if (this[i] == element) {
	            return true;
	        }
	    }
	    return false;
	}
	
	Array.prototype.distinct = function(){
	     var self = this;
	     var _a = this.concat().sort();
	     _a.sort(function(a,b){
	         if(a == b){
	             var n = self.indexOf(a);
	             self.splice(n,1);
	         }
	     });
	     return self;
	 };
	 
	 function validateVlansLength(data){
			var argMerge = new Array();
			var vlan_list = new Array();
			if(data.indexOf(",") > 0){
				var tmpObj = data.split(",");
				for(var i = 0; i < tmpObj.length; i ++){
					if(tmpObj[i].indexOf("-") > 0){
						var from = parseInt((tmpObj[i].substring(0, tmpObj[i].indexOf("-"))).trim());
						var to = parseInt((tmpObj[i].substring(tmpObj[i].indexOf("-") +1)).trim());
						for(var n = from; n <= to; n ++){
							argMerge[n] = true;
						}
					}else{
						argMerge[tmpObj[i]] = true;
					} 
				}
			 }else{
				if(data.indexOf("-") > 0){
					var from = parseInt((data.substring(0, data.indexOf("-"))).trim());
					var to = parseInt((data.substring(data.indexOf("-") +1)).trim());
					for(var n = from; n <= to; n ++){
						argMerge[n] = true;
					}
				}else{
					argMerge[data] = true;
				} 
			 }
				
			for(var m=0; m<argMerge.length; m++){
				if(argMerge[m] && argMerge[m] != "") {
					vlan_list.push(m);
				}
			}
			if(vlan_list.length > 255){
				showMirrorNotes("<s:text name='error.port.mirroring.vlan.limit' />");
				return false;
			}
			return true;
		}
	
	function validateChoose(checkBoxs){
		 var tmpArray = new Array();
		 for(var i = 0; i < checkBoxs.length; i ++){
			 if(checkBoxs[i].checked){
				 tmpArray.push(checkBoxs[i].value);
			 }
		 }
		 
		 for(var m = 0; m < tmpArray.length; m ++){
			 if (isPortChannelMemberPort(tmpArray[m])){
				 if(tmpArray.contains(getPortChannelIdByMemberPort(tmpArray[m]))){
					 return tmpArray[m];
				 }
			 }
		 }
		 
		 return "";
	}

	function mergeRange(sourceList){
		var resList = new Array();
		if(sourceList == null || sourceList.length == 0){
			return resList;
		}
		
		var argMerge = new Array();
		
		for (var i = 0; i < sourceList.length; i ++){
			if(sourceList[i].indexOf("-") > 0){
				var from, to;
				from = (sourceList[i].substring(0, sourceList[i].indexOf("-"))).trim();
				to = (sourceList[i].substring(sourceList[i].indexOf("-") +1)).trim();
				for(var m=from; m<=to; m++){
					argMerge[m] = true;
				}
			}else{
				argMerge[sourceList[i]] = true;
			}
		}
		argMerge.push(false);
		var fromAttr = 0, toAttr = 0;
		for(var n=0; n<argMerge.length; n++){
			if(argMerge[n]) {
				toAttr = n;
				if(fromAttr == 0){
					fromAttr = n;
				}
			}else{
				if(fromAttr == toAttr && toAttr > 0) {
					resList.push(fromAttr);
					fromAttr = 0;
					toAttr = 0;
				}else if(fromAttr > 0 && toAttr > 0 && fromAttr != toAttr) {
					resList.push(fromAttr + "-" + toAttr);
					fromAttr = 0;
					toAttr = 0;
				}
			}
		}
		return resList;
	}
	
	$(window).scroll(function() {
		$("#portSelectListTable").hide();
		initSelectPannel();
	});
	
	function getAllSelectedPort(){
		var $dataTbl = $('div#mirrorSetion');
		var gIds = new Array();
		var getIdFromValue = function(value) {
		    if (typeof value != "undefined") {
		    	if(value != ""){
		        	gIds.push(value);
		    	}
		    }
		};
		$dataTbl.find("input[id^=hidden_][id$=_ingress]").each(function(){
		    getIdFromValue(this.value);
		});
		$dataTbl.find("input[id^=hidden_][id$=_egress]").each(function(){
		    getIdFromValue(this.value);
		});
		$dataTbl.find("input[id^=hidden_][id$=_both]").each(function(){
		    getIdFromValue(this.value);
		});
		return gIds.toString();
	}
	
	function getPortListHideElement(){
		var portsTable = $("#portsDataTable");
		var allPortElement = portsTable.find("td[id^=port_]");
		var tmpArray = new Array();
		for(var i = 0; i < allPortElement.length; i ++){
			if(allPortElement[i].style.display == "none"){
				tmpArray.push((allPortElement[i].id).substring(5));
			}
		}
		return tmpArray;
	}
	
	function showMirrorNotes(message, configs) {
		var notesTimeoutId;
		var pageNotesTimeoutId = 0;
		clearTimeout(pageNotesTimeoutId);
		var pageNotesElement = document.getElementById('mirrorNotesConent');
		pageNotesElement.innerHTML = message || '';
		configs = configs || {};
		hm.util.wipeIn('mirrorNotes', 300);
		pageNotesTimeoutId = setTimeout("hideMirrorNotes()", 10000);
	}

	function hideMirrorNotes() {
		hm.util.wipeOut('mirrorNotes', 600);
	}

	function validateVlans(data){
		var vlan = data;
		if(!validateVlansLength(vlan.value)){
			return false;
		}
		if(vlan.value.length != 0){
			var vlanList =vlan.value.split(',');
			var vlanValues = new Array();
			var vlanRangerValues = new Array();
			for(var j=0;j<vlanList.length;j++){
				var pattern = /^(\d+-)?\d+$/;
				if(!pattern.test(vlanList[j])){
					showMirrorNotes('<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
				var vlans = vlanList[j].split('-');
				var numVlan0=Number(vlans[0]);
				var numVlan1=Number(vlans[1]);
				var pattern_value = /^[1-9]\d{0,2}$|^[1-3]\d{3}$|^40[0-8][0-9]$|^409[0-4]$/; //1-4094
				for(var i=0;i<vlans.length;i++) {
					if(!pattern_value.test(vlans[i])){
						showMirrorNotes('<s:text name="error.formatInvalid"><s:param><s:text name="config.vlanGroup.vlans" /></s:param></s:text>');
						vlan.focus();
				        return false;
					}
				}
			    if(vlans.length>1 && numVlan0 > numVlan1){
			    	showMirrorNotes('<s:text name="error.formatInvalid"><s:param><s:text name="config.BonjourGatewaySetting.vlans" /></s:param></s:text>');
					vlan.focus();
			        return false;
				}
			    if(vlans.length>1){
			    	for(var i=0;i<=numVlan1-numVlan0;i++){
						if(vlanValues.contains(numVlan0+i)){
							showMirrorNotes('<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
							vlan.focus();
					        return false;
						} else {
							vlanValues.push(numVlan0+i);
						}
					}
			    } else {
			    	if(vlanValues.contains(numVlan0)){
			    		showMirrorNotes('<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
						vlan.focus();
				        return false;
					} else {
						vlanValues.push(numVlan0);
					}
			    }
				
				if(vlanRangerValues.contains(vlanList[j])){
					showMirrorNotes('<s:text name="error.bonjour.gateway.vlan.range.reduplicate"/>');
					vlan.focus();
			        return false;
				} else {
					vlanRangerValues.push(vlanList[j]);
				}
			}
		}
		
	    return true;
	}
	
	function changeSourceVlans(checked){
		if(checked){
			Get("ingressVlan").disabled = false;
		}else{
			Get("ingressVlan").disabled = true;
		}
	}
	
/* 	function enablePorts(checked){
		if(checked){
			hiddenAllTableItem(interfaceId);
		}else{
			showAllTableItem();
		}
	}
 */	/* function changeDestination(interfaceId){
		$("#destinationInterface").attr("value", interfaceId);
		if(interfaceId){
			hiddenAllTableItem(interfaceId);	
		}
	} */

	function hiddenAllTableItem(interfaceId){
		$("#tbl_port_mirroring").find("input[class = hiddenMark]").each(function (){
			var enableItem = $(this).parent().parent().find("input[name = enableMonitorSession]");
			if(this.value == interfaceId){
				enableItem.attr("checked","checked");
				//$(this).parent().parent().show();
			}else{
				enableItem.removeAttr("checked");
				//$(this).parent().parent().hide();
			}
		});
	}
	
	function showAllTableItem(){
		$("#tbl_port_mirroring").find("input[class = hiddenMark]").each(function (){
			var enableItem = $(this).parent().parent().find("input[name = enableMonitorSession]");
			if(enableItem){
				enableItem.attr("checked","checked");
				//$(this).parent().parent().show();
			}
		});
	}
	
	function enablePortBased(checked){
		if(checked){
			$("#tbl_port_mirroring").show();
		}else{
			$("#tbl_port_mirroring").hide();
		}
	}
	
</script>

<style type="text/css">
#mirrorSetion .selectListTDGroup {
	border: 3px solid white;
	font-size: 14px;
	font-weight: normal;
	line-height: 12px;
	padding-left: 5px;
}

#mirrorSetion .ellipsis {
	cursor: pointer;
}

#mirrorSetion .selectListTD {
	border: 1px solid white;
	font-size: 14px;
	background-color: rgb(249, 249, 247);
	padding-left: 5px;
}

#mirrorSetion .selectList {
	border: 0px solid white;
	overflow-x: hidden;
	overflow-y: auto;
	max-height: 150px;
}

#mirrorSetion .selectListExpress {
	border: 0px solid white;
	overflow-x: hidden;
	overflow-y: auto;
}

#mirrorSetion .selectListTD span {
	width: 60em;
	cursor: pointer;
	padding-left: 25px;
}

#mirrorSetion td.odd {
	background: #F5F5F5;
}

#mirrorSetion td.even {
	background: #FFFFFF;
}

#mirrorSetion td.ellipsis {
	min-width: 50px;
	max-width: 150px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	-o-text-overflow: ellipsis; /*For Opera*/
	-ms-text-overflow: ellipsis; /*For IE8*/
	-moz-binding: url(assets/xml/ellipsis.xml#ellipsis); /*For Firefox3.x*/
}
}
</style>