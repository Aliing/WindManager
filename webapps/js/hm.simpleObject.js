var hm = hm || {};
hm.simpleObject = hm.simpleObject || {};

/* definitions must be the same as definitions in BaseAction*/
hm.simpleObject.TYPE_IP = "ip";
hm.simpleObject.TYPE_MAC = "mac";
hm.simpleObject.TYPE_VLAN = "vlan";

hm.simpleObject.IP_SUB_IP = "ipAddress";
hm.simpleObject.IP_SUB_HOST = "hostName";
hm.simpleObject.IP_SUB_NETWORK = "network";
hm.simpleObject.IP_SUB_WILDCARD = "wildcard";
hm.simpleObject.MAC_SUB_MAC = "macAddress";
hm.simpleObject.MAC_SUB_OUI = "macOui";

hm.simpleObject.FORM_IP_PREFIX = "ip_form_";
hm.simpleObject.FORM_MAC_PREFIX = "mac_form_";
hm.simpleObject.FORM_VLAN_PREFIX = "vlan_form_";

hm.simpleObject.INPUT_FIELD_ID = "";

YAHOO.util.Event.onDOMReady(function () {
	//hm.simpleObject.initDebug();
});

hm.simpleObject.initDebug = function(){
	YAHOO.widget.Logger.enableBrowserConsole();
}

hm.simpleObject.overlayHash = {};

hm.simpleObject.ajaxRequest = function(form, url, doSuccess, argument){
	var type = "POST";
	if(form != null){
		YAHOO.util.Connect.setForm(form);
	}
	var transaction = YAHOO.util.Connect.asyncRequest(type, url, {success: doSuccess, argument: argument}, null);
}

hm.simpleObject.newSimple = function(type, subType, destination, callbackFn, domainId){
	YAHOO.log("type:"+type+", subType:"+subType+", destination:"+destination
		+", callbackFn:"+callbackFn+", domainId:"+domainId);
	if(!destination){
		YAHOO.log("destination is null, do nothing...");
		return;
	}
	var destinationEl = document.getElementById(destination);
	if(!destinationEl){
		YAHOO.log("destination object is not in DOM, do nothing...");
		return;
	}
	hm.simpleObject.overlayHash[destination] = hm.simpleObject.overlayHash[destination] || {};
	var overlay;
	switch(type){
		case hm.simpleObject.TYPE_IP:
			if(!hm.simpleObject.overlayHash[destination].overlay){
				//create corresponding ip overlay
				overlay = hm.simpleObject.createIpOverlay(destination, subType);
				//store subType if needed
				if(subType){
					hm.simpleObject.overlayHash[destination].subType = subType;
				}
			}else{
				overlay = hm.simpleObject.overlayHash[destination].overlay;
				// clear input
				var formName = hm.simpleObject.FORM_IP_PREFIX + destination;
				document.forms[formName]["ip"].value = "";
			}
		break;
		case hm.simpleObject.TYPE_MAC:
			if(!hm.simpleObject.overlayHash[destination].overlay){
				//create corresponding mac overlay
				overlay = hm.simpleObject.createMacOverlay(destination, subType);
				//store subType if needed
				if(subType){
					hm.simpleObject.overlayHash[destination].subType = subType;
				}
			}else{
				overlay = hm.simpleObject.overlayHash[destination].overlay;
				// clear input
				var formName = hm.simpleObject.FORM_MAC_PREFIX + destination;
				document.forms[formName]["mac"].value = "";
			}
		break;
		case hm.simpleObject.TYPE_VLAN:
			if(!hm.simpleObject.overlayHash[destination].overlay){
				//create corresponding vlan overlay
				overlay = hm.simpleObject.createVlanOverlay(destination);
			}else{
				overlay = hm.simpleObject.overlayHash[destination].overlay;
				// clear input
				var formName = hm.simpleObject.FORM_VLAN_PREFIX + destination;
				document.forms[formName]["vlan"].value = "";
			}
		break;
		default:
		YAHOO.log("Unknow type, ignore it...");
	}
	if(null != overlay){ // store objects into hash if needed.
		if(!hm.simpleObject.overlayHash[destination].overlay){
			//store overlay, for create used.
			hm.simpleObject.overlayHash[destination].overlay = overlay;
		}
		if(!hm.simpleObject.overlayHash[destination].addCallbackFn){
			//store callback function if existed, for create used.
			if(callbackFn && typeof window[callbackFn] == 'function'){
				hm.simpleObject.overlayHash[destination].addCallbackFn = window[callbackFn];
			}
		}
		if(!hm.simpleObject.overlayHash[destination].domainId){
			//store domainId, for create used.
			hm.simpleObject.overlayHash[destination].domainId = domainId;
		}
		// adjust location
		overlay.cfg.setProperty("xy",YAHOO.util.Dom.getXY(destination));
		// show overlay
		overlay.show();
	}
}

hm.simpleObject.removeSimple = function(type,destination, callbackFn){
	var destinationEl = document.getElementById(destination);
	if(!destinationEl){
		YAHOO.log("destination object is not in DOM, do nothing...");
		return;
	}
	hm.simpleObject.overlayHash[destination] = hm.simpleObject.overlayHash[destination] || {};
	if(!hm.simpleObject.overlayHash[destination].removeCallbackFn){
		//store callback function if existed, for create used.
		if(callbackFn && typeof window[callbackFn] == 'function'){
			hm.simpleObject.overlayHash[destination].removeCallbackFn = window[callbackFn];
		}
	}
	var result = hm.util.validateListSelection(destination, true);
	if(result < 0){
		return;
	}
	YAHOO.log("select removed items:"+result);
	// show confirm dialog before removal
	hm.simpleObject.showConfirmDialog(type, destination, result);
}

hm.simpleObject.removeSimpleByDelKey = function(e, type, destination, inputName, callbackFn){
	var keycode;
	// IE
	if(window.event){
		keycode = e.keyCode;
		
	// Netscape/Firefox/Opera
	} else if(e.which){
		keycode = e.which;
	} else {
		return;
	}
	// delete key(virtual key number)
	if(keycode==46){
		hm.simpleObject.INPUT_FIELD_ID = inputName;
		hm.simpleObject.removeSimple(type, destination, callbackFn);
	}
}

hm.simpleObject.createMacOverlay = function(destination, type){
	var header, desc;
	if(type == hm.simpleObject.MAC_SUB_MAC){
		header = "Add MAC Address";
		desc = "";
	}else if(type == hm.simpleObject.MAC_SUB_OUI){
		header = "Add MAC OUI";
		desc = "";
	}else{
		header = "Add MAC Address/OUI";
		desc = "MAC Address or OUI";
	}
	// Instantiate a Panel from script 
	var overlay = new YAHOO.widget.Panel(destination + "_mac", { width:"250px", visible:false, draggable:false, close:true, underlay:"none" } );
	overlay.setHeader(header);
	var bodyHTML = '<form id="'+ hm.simpleObject.FORM_MAC_PREFIX + destination + '" name="' + hm.simpleObject.FORM_MAC_PREFIX + destination+'">\
						<table border="0" cellspacing="0" cellpadding="0" width="100%">\
							<tr>\
								<td></td><!--fix note layout issue add one td ahead-->\
								<td><input id="'+destination+'_input_mac" name="mac" type="text" size="24" maxlength="12" title="'+desc+'" /></td>\
								<td align="right"><span class="yui-button yui-push-button" style="vertical-align:middle;"><span class="first-child">\
									<button type="button" onclick="hm.simpleObject.requestMacAdd(\''+destination+'\');">Apply</button></span></span></td>\
							</tr>\
						</table>\
					</form>';
	overlay.setBody(bodyHTML);
	overlay.render(document.body);
	YAHOO.log("IP panel for:"+destination+" has been created.");
	return overlay;
}


hm.simpleObject.requestMacRemove = function(destination, removeIds){
	YAHOO.log("requestMacRemove for:"+destination);
	var destObject = hm.simpleObject.overlayHash[destination];
	if(destObject == null){
		YAHOO.log("requestMacRemove, destObject is null.");
		return;
	}
	var url = "macAddress.action?operation=removeSimpleMac&macIdString="+removeIds+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(null, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.requestMacAdd = function(destination){
	YAHOO.log("requestMacAdd for:"+destination);
	var formName = hm.simpleObject.FORM_MAC_PREFIX + destination;
	var destObject = hm.simpleObject.overlayHash[destination];
	var macForm = document.forms[formName];
	if(macForm == null || destObject == null){
		YAHOO.log("requestMacAdd, form is null or destObject is null.");
		return;
	}
	var macEl = macForm["mac"];
	if(!hm.simpleObject.validateMac(macEl, destObject.subType)){
		return;
	}
	var url = "macAddress.action?operation=createSimpleMac&domainId="+destObject.domainId+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(macForm, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.validateMac = function(el, subType){
	if(subType == hm.simpleObject.MAC_SUB_MAC){
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'MAC address is a required field.');
			el.focus();
			return false;
		}
		if(!hm.util.validateMacAddress(el.value, 12)){
			hm.util.reportFieldError(el,"MAC address format is invalid");
			el.focus();
			return false;
		}
	}else if(subType == hm.simpleObject.MAC_SUB_OUI){
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'MAC OUI is a required field.');
			el.focus();
			return false;
		}
		if(!hm.util.validateMacAddress(el.value, 6)){
			hm.util.reportFieldError(el,"MAC OUI format is invalid");
			el.focus();
			return false;
		}
	}else{
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'MAC Address/OUI is a required field.');
			el.focus();
			return false;
		}
		if(!hm.util.validateMacAddress(el.value, 12) && !hm.util.validateMacAddress(el.value, 6)){
			hm.util.reportFieldError(el,"MAC Address/OUI format is invalid");
			el.focus();
			return false;
		}
	}
	return true;
}

hm.simpleObject.createIpOverlay = function(destination, type){
	var header, desc;
	if(type == hm.simpleObject.IP_SUB_IP){
		header = "Add IP Address";
		desc = "Format examples: 192.168.1.1";
	}else if(type == hm.simpleObject.IP_SUB_HOST){
		header = "Add Hostname";
		desc = "(1-32 characters)";
	}else if(type == hm.simpleObject.IP_SUB_NETWORK){
		header = "Add Network";
		desc = "Format examples: 192.168.1.1, 192.168.1.1/16 or 192.168.1.1/255.255.0.0";
	}else if(type == hm.simpleObject.IP_SUB_WILDCARD){
		header = "Add Wildcard";
		desc = "Format examples: 192.168.1.1, 192.168.1.1/16 or 192.168.1.1/255.0.0.255";
	}else{
		header = "Add IP Address/Hostname";
		desc = "IP Address or Hostname";
	}
	// Instantiate a Panel from script 
	var overlay = new YAHOO.widget.Panel(destination + "_ip", { width:"250px", visible:false, draggable:false, close:true, underlay:"none" } );
	overlay.setHeader(header);
	var bodyHTML = '<form id="'+ hm.simpleObject.FORM_IP_PREFIX + destination + '" name="' + hm.simpleObject.FORM_IP_PREFIX + destination+'">\
						<table border="0" cellspacing="0" cellpadding="0" width="100%">\
							<tr>\
								<td></td><!--fix note layout issue add one td ahead-->\
								<td><input id="'+destination+'_input_ip" name="ip" type="text" size="24" maxlength="32" title="'+desc+'" /></td>\
								<td align="right"><span class="yui-button yui-push-button" style="vertical-align:middle;"><span class="first-child">\
									<button type="button" onclick="hm.simpleObject.requestIpAdd(\''+destination+'\');">Apply</button></span></span></td>\
							</tr>\
						</table>\
					</form>';
	overlay.setBody(bodyHTML);
	overlay.render(document.body);
	YAHOO.log("IP panel for:"+destination+" has been created.");
	return overlay;
}


hm.simpleObject.requestIpRemove = function(destination, removeIds){
	YAHOO.log("requestIpRemove for:"+destination);
	var destObject = hm.simpleObject.overlayHash[destination];
	if(destObject == null){
		YAHOO.log("requestIpRemove, destObject is null.");
		return;
	}
	var url = "ipAddress.action?operation=removeSimpleIp&ipIdString="+removeIds+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(null, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.requestIpAdd = function(destination){
	YAHOO.log("requestIpAdd for:"+destination);
	var formName = hm.simpleObject.FORM_IP_PREFIX + destination;
	var destObject = hm.simpleObject.overlayHash[destination];
	var ipForm = document.forms[formName];
	if(ipForm == null || destObject == null){
		YAHOO.log("requestIpAdd, form is null or destObject is null.");
		return;
	}
	var ipEl = ipForm["ip"];
	if(!hm.simpleObject.validateIp(ipEl, destObject.subType)){
		return;
	}
	var url = "ipAddress.action?operation=createSimpleIp&domainId="+destObject.domainId+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(ipForm, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.validateIp = function(el, subType){
	if(subType == hm.simpleObject.IP_SUB_IP){
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'IP address is a required field.');
			el.focus();
			return false;
		}
		if(el.value.indexOf("/")>-1){
			var ipmask = el.value.split("/");
			var ip = ipmask[0];
			var mask = ipmask[1];
			YAHOO.log("validateIp, ip:"+ip+", mask:"+mask);
			if(!hm.util.validateIpAddress(ip)){
				hm.util.reportFieldError(el,"IP address format is invalid");
				el.focus();
				return false;
			}
			if(mask.indexOf(".")>-1){
				//format: 255.255.255.255
				if(!hm.util.validateMask(mask)){
					hm.util.reportFieldError(el,"Netmask is invalid");
					el.focus();
					return false;
				}
			}else{
				//format: 1-32
				if(isNaN(mask)||mask<1||mask>32){
					hm.util.reportFieldError(el,"Netmask is invalid");
					el.focus();
					return false;
				}
			}
		}else{
			if(!hm.util.validateIpAddress(el.value)){
				hm.util.reportFieldError(el,"IP address format is invalid");
				el.focus();
				return false;
			}
		}
	}else if(subType == hm.simpleObject.IP_SUB_NETWORK){
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'Network is a required field.');
			el.focus();
			return false;
		}
		if(el.value.indexOf("/")>-1){
			var ipmask = el.value.split("/");
			var ip = ipmask[0];
			var mask = ipmask[1];
			YAHOO.log("validateIp, ip:"+ip+", mask:"+mask);
			if(!hm.util.validateIpAddress(ip)){
				hm.util.reportFieldError(el,"Network format is invalid");
				el.focus();
				return false;
			}
			if(mask.indexOf(".")>-1){
				//format: 255.255.255.255
				if(!hm.util.validateMask(mask)){
					hm.util.reportFieldError(el,"Network is invalid");
					el.focus();
					return false;
				}
			}else{
				//format: 1-32
				if(isNaN(mask)||mask<1||mask>32){
					hm.util.reportFieldError(el,"Network is invalid");
					el.focus();
					return false;
				}
			}
		}else{
			if(!hm.util.validateIpAddress(el.value)){
				hm.util.reportFieldError(el,"Network format is invalid");
				el.focus();
				return false;
			}
		}
	}else if(subType == hm.simpleObject.IP_SUB_WILDCARD){
		if (el.value.length == 0) {
			hm.util.reportFieldError(el, 'Wildcard is a required field.');
			el.focus();
			return false;
		}
		if(el.value.indexOf("/")>-1){
			var ipmask = el.value.split("/");
			var ip = ipmask[0];
			var mask = ipmask[1];
			YAHOO.log("validateIp, ip:"+ip+", mask:"+mask);
			if(!hm.util.validateIpAddress(ip)){
				hm.util.reportFieldError(el,"Wildcard format is invalid");
				el.focus();
				return false;
			}
			if(mask.indexOf(".")<0){
				//format: 1-32
				if(isNaN(mask)||mask<1||mask>32){
					hm.util.reportFieldError(el,"Wildcard is invalid");
					el.focus();
					return false;
				}
			}
		}else{
			if(!hm.util.validateIpAddress(el.value)){
				hm.util.reportFieldError(el,"Wildcard format is invalid");
				el.focus();
				return false;
			}
		}
	}else if(subType == hm.simpleObject.IP_SUB_HOST){
		var message = hm.util.validateName(el.value, 'Hostname');
		if (message != null) {
		    hm.util.reportFieldError(el, message);
		    el.focus();
		    return false;
		}
	}else{
		var message = hm.util.validateName(el.value, 'IP Address/Hostname');
		if (message != null) {
		    hm.util.reportFieldError(el, message);
		    el.focus();
		    return false;
		}
	}
	return true;
}

hm.simpleObject.createVlanOverlay = function(destination){
	// Instantiate a Panel from script 
	var overlay = new YAHOO.widget.Panel(destination + "_vlan", { width:"250px", visible:false, draggable:false, close:true, underlay:"none" } );
	overlay.setHeader("Add VLAN");
	var bodyHTML = '<form id="'+ hm.simpleObject.FORM_VLAN_PREFIX + destination + '" name="' + hm.simpleObject.FORM_VLAN_PREFIX + destination+'">\
						<table border="0" cellspacing="0" cellpadding="0" width="100%">\
							<tr>\
								<td></td><!--fix note layout issue add one td ahead-->\
								<td><input id="'+destination+'_input_vlan" name="vlan" type="text" size="12" maxlength="4" /> (1-4094)</td>\
								<td align="right"><span class="yui-button yui-push-button" style="vertical-align:middle;"><span class="first-child">\
									<button type="button" onclick="hm.simpleObject.requestVlanAdd(\''+destination+'\');">Apply</button></span></span></td>\
							</tr>\
						</table>\
					</form>';
	overlay.setBody(bodyHTML);
	overlay.render(document.body);
	YAHOO.log("VLAN panel for:"+destination+" has been created.");
	return overlay;
}

hm.simpleObject.requestVlanRemove = function(destination, removeIds){
	YAHOO.log("requestVlanRemove for:"+destination);
	var destObject = hm.simpleObject.overlayHash[destination];
	if(destObject == null){
		YAHOO.log("requestVlanRemove, destObject is null.");
		return;
	}
	var url = "vlan.action?operation=removeSimpleVlan&vlanIdString="+removeIds+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(null, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.requestVlanAdd = function(destination){
	YAHOO.log("requestVlanAdd for:"+destination);
	var formName = hm.simpleObject.FORM_VLAN_PREFIX + destination;
	var destObject = hm.simpleObject.overlayHash[destination];
	var vlanForm = document.forms[formName];
	if(vlanForm == null || destObject == null){
		YAHOO.log("requestVlanAdd, form is null or destObject is null.");
		return;
	}
	var vlanEl = vlanForm["vlan"];
	if(!hm.simpleObject.validateVlan(vlanEl)){
		return;
	}
	var url = "vlan.action?operation=createSimpleVlan&domainId="+destObject.domainId+"&ignore="+new Date().getTime();
	hm.simpleObject.ajaxRequest(vlanForm, url, hm.simpleObject.defaultSimpleProcess, destination);
}

hm.simpleObject.validateVlan = function(el){
	if (el.value.length == 0) {
		hm.util.reportFieldError(el, 'VLAN is a required field.');
		el.focus();
		return false;
	}
	var message = hm.util.validateIntegerRange(el.value, 'VLAN',1, 4094);
	if (message != null) {
		hm.util.reportFieldError(el, message);
		el.focus();
		return false;
	}
	return true;
}

hm.simpleObject.defaultSimpleProcess = function(o){
	eval("var data = " + o.responseText);
	var type = data.type;
	YAHOO.log("Default Vlan Process, result type:"+type+", destination:"+o.argument);
	if(type && o.argument){
		if(type == "add"){ // add process
			if(data.msg){
				hm.simpleObject.showMessage(data.msg);
			}
			if(data.item){
				// hide overlay first
				if(hm.simpleObject.overlayHash[o.argument]){
					hm.simpleObject.overlayHash[o.argument].overlay.hide();
				}
				var destEl = document.getElementById(o.argument);
				hm.simpleObject.addOption(destEl, data.item.key, data.item.value, true);
			}
			if(hm.simpleObject.overlayHash[o.argument] && hm.simpleObject.overlayHash[o.argument].addCallbackFn){
				hm.simpleObject.overlayHash[o.argument].addCallbackFn(data);
			}
		}else if(type == "remove"){ // remove process
			if(data.msg){
				hm.simpleObject.showMessage(data.msg);
			}
			if(data.items){
				var destEl = document.getElementById(o.argument);
				hm.simpleObject.removeOptions(destEl, data.items);
			}
			if(hm.simpleObject.overlayHash[o.argument] && hm.simpleObject.overlayHash[o.argument].removeCallbackFn){
				hm.simpleObject.overlayHash[o.argument].removeCallbackFn(data);
			}
		}
	}
}

hm.simpleObject.processRemoveRequest = function(){
	var type = hm.simpleObject.removeObjectType;
	var removeObjectDestination = hm.simpleObject.removeObjectDestination;
	var removeIds = hm.simpleObject.removeObjectIds;
	if(!type || !removeObjectDestination || !removeIds){
		YAHOO.log("Missing type or removeObjectDestination or removeIds...");
		return;
	}
	switch(type){
		case hm.simpleObject.TYPE_IP:
			hm.simpleObject.requestIpRemove(removeObjectDestination, removeIds);
		break;
		case hm.simpleObject.TYPE_MAC:
			hm.simpleObject.requestMacRemove(removeObjectDestination, removeIds);
		break;
		case hm.simpleObject.TYPE_VLAN:
			hm.simpleObject.requestVlanRemove(removeObjectDestination, removeIds);
		break;
		default:
		YAHOO.log("Unknow type, ignore it...");
	}
	hm.simpleObject.confirmDialog.hide();
}

hm.simpleObject.showConfirmDialog = function(type, destination, removeIds){
	if(null == hm.simpleObject.confirmDialog){
		hm.simpleObject.confirmDialog = hm.simpleObject.createConfirmDialog();
	}
	hm.simpleObject.removeObjectType = type;
	hm.simpleObject.removeObjectDestination = destination;
	hm.simpleObject.removeObjectIds = removeIds;
	if(removeIds.length > 1){
		hm.simpleObject.confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected items.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue ?</body></html>");
	}else{
		hm.simpleObject.confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected item.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue ?</body></html>");
	}
	hm.simpleObject.confirmDialog.show();
}

hm.simpleObject.createConfirmDialog = function(){
	var confirmDialog = new YAHOO.widget.SimpleDialog("simpleObjectConfirmDialog",
          { width: "350px",
            fixedcenter: true,
            visible: false,
            draggable: true,
            modal:true,
            close: true,
            icon: YAHOO.widget.SimpleDialog.ICON_WARN,
            constraintoviewport: true,
            buttons: [ { text:"Yes", handler:hm.simpleObject.processRemoveRequest, isDefault:true },
                       { text:"&nbsp;No&nbsp;", handler:function(){this.hide()} } ]
          } );
     confirmDialog.setHeader("Confirm");
     confirmDialog.render(document.body);
     return confirmDialog;
}

/* tool functions */
hm.simpleObject.showMessage = function(message){
	if(null != warnDialog){
		warnDialog.cfg.setProperty('text', message);
		warnDialog.show();
	}
}
hm.simpleObject.addOption = function(selector, value, text, isSelect){
	if(null == selector){
		return;
	}
	// if there's a 'None Item Avaliable', remove it first
	if(selector.length==1 && selector.options[0].value == -1){
		hm.simpleObject.removeOptions(selector, [-1]);
	}
	var option = new Option(text, value, isSelect);
	try{
		selector.add(option, null); // DOM
	}catch(e){
		selector.add(option); // IE
	}
}
hm.simpleObject.removeOptions = function(selector, values){
	if(null == selector){
		return;
	}
	for(var j=0; j<values.length; j++){
		for(var i=0; i<selector.length; i++){
			if(selector.options[i].value == values[j]){
				// for simple object input direct tag
				if ('' != hm.simpleObject.INPUT_FIELD_ID && i == selector.selectedIndex) {
					var inputEle = document.getElementById(hm.simpleObject.INPUT_FIELD_ID);
					selector.selectedIndex = 0;
					inputEle.value = '';
					inputEle.style.zIndex = 2;
					inputEle.focus();
					hm.simpleObject.INPUT_FIELD_ID = "";
				}
				selector.remove(i);
				break;
			}
		}
	}
}
/* end */
