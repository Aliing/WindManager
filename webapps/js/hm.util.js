var hm = hm || {};
hm.util = hm.util || {};

hm.util.INTEGER_MIN_VALUE = -2147483648;
hm.util.INTEGER_MAX_VALUE = 2147483646;
hm.util.SHORT_MIN_VALUE = -32768;
hm.util.SHORT_MAX_VALUE = 32767;
hm.util.SHORT_UNSIGNED_MAX_VALUE = 65535;

hm.util.sessionTimeout = 15; // minutes

/*
 * String trim function;
 */
String.prototype.trim = function(){return this.replace(/(^\s*)|(\s*$)/g, "");}

Array.prototype.contains = function(e)
{
    for(i = 0; i < this.length; i++) {
        if (this[i] == e)
        return true;
    }
    return false;
}

Array.prototype.fetch = function(e)
{
    for(i = 0; i < this.length; i++) {
        if ((this[i] instanceof Array) && this[i][0] == e)
        return this[i];
    }
    return null;
}

Array.prototype.removeAItem = function(e) {
	if (this == undefined
			|| this == null
			|| this.length < 1) {
		return false;
	}
	for(var i = 0; i < this.length; i++) {
        if (this[i] == e) {
        	this.splice(i, 1);
            break;
        }
    }

	return true;
}

/*
 * Stop event bubble
 */
hm.util.stopBubble = function(e) {
	if (e && e.stopPropagation) {
		// for not IE
		e.stopPropagation();
	}
	else {
		// for IE
		if (window.event){
			window.event.cancelBubble = true;
		}
	}
}
/*
 * Register onClick event listener
 */
hm.util.registerRowClickedEvents = function() {
	var inputElements = document.getElementsByName('selectedIds');
	// If no elements found, length will be 0
    for (var i = 0; i < inputElements.length; i++) {
    	var inputElement = inputElements[i];
		YAHOO.util.Event.addListener(inputElement, "click", hm.util.rowClicked);
	}
}

/*
 * 'Check All' checkbox event handler
 */
hm.util.toggleCheckAll = function(checkAll) {
	hm.util.toggleHideElement('pageItemsSelectedRow', !checkAll.checked);
	if (!checkAll.checked) {
		hm.util.toggleHideElement('allItemsSelectedRow', true);
		hm.util.toggleAllItemsSelected(false);
	}
	var inputElements = document.getElementsByName('selectedIds');
	if (inputElements) {
	    for (var i = 0; i < inputElements.length; i++) {
    		var cb = inputElements[i];
			if (!cb.disabled && cb.checked != checkAll.checked) {
				cb.checked = checkAll.checked;
				this.toggleRow(cb);
			}
		}
	}
}

/*
 * 'Check Row' checkbox event handler
 */
hm.util.toggleCheck = function(check) {
	if (check.checked) {
		return;
	}
	hm.util.toggleHideElement('pageItemsSelectedRow', true);
	hm.util.toggleHideElement('allItemsSelectedRow', true);
	hm.util.toggleAllItemsSelected(false);
	var checkAll = document.getElementById('checkAll');
	if (checkAll) {
		checkAll.checked = false;
	}
}

/*
 * Select all items event handler
 */
hm.util.setAllItemsSelected = function() {
	hm.util.toggleHideElement('pageItemsSelectedRow', true);
	hm.util.toggleHideElement('allItemsSelectedRow', false);
	hm.util.toggleAllItemsSelected(true);
}

hm.util.toggleAllItemsSelected = function(value) {
	var allItemsSelected = document.getElementById('allItemsSelectedVar');
	if (allItemsSelected) {
		allItemsSelected.value = value;
	}
}

/*
 * Select all items event handler
 */
hm.util.clearSelection = function() {
	hm.util.toggleHideElement('allItemsSelectedRow', true);
	hm.util.toggleAllItemsSelected(false);
	var checkAll = document.getElementById('checkAll');
	if (checkAll) {
		checkAll.checked = false;
		hm.util.toggleCheckAll(checkAll);
	}
}

/*
 * Vector contains element ?
 */
hm.util.vectorContains = function(vector, element) {
	for (var i = 0; i < vector.length; i++) {
		if (vector[i] == element) {
			return true;
		}
	}
	return false;
}

// Private functions

/*
 * Row checkbox was clicked.  Remember last row clicked and
 * check if SHIFT key was held for multi-(un)select.
 */
hm.util.rowClicked = function(event) {
	if (!event) {
		// Use window.event for IE
		event = window.event;
	}
	if (event.target) {
		var checkBox = event.target;
	} else {
		var checkBox = event.srcElement;
	}
	hm.util.toggleRow(checkBox);
	if (hm.util.lastRowClicked && event.shiftKey) {
		var index = hm.util.getElementIndex(checkBox);
		var lastIndex = hm.util.getElementIndex(hm.util.lastRowClicked);
		if (lastIndex < index) {
			hm.util.toggleRows(checkBox.name, lastIndex, index, checkBox.checked);
		} else {
			hm.util.toggleRows(checkBox.name, index + 1, lastIndex + 1, checkBox.checked);
		}
	} else {
		// No last row or no shift key
	}
	hm.util.lastRowClicked = checkBox;
}

/*
 * Calculate row index for element.
 */
hm.util.getElementIndex = function(element) {
	inputElements = document.getElementsByName(element.name);
    for (var i = 0; i < inputElements.length; i++) {
    	if (inputElements[i] == element) {
    		return i;
    	}
    }
    return -1;
}

/*
 * Update number of rows to match value.
 */
hm.util.toggleRows = function(name, from, until, value) {
	inputElements = document.getElementsByName(name);
    for (var i = from; i < until; i++) {
    	var inputElement = inputElements[i];
    	if (inputElement.checked != value) {
    		inputElement.checked = value;
			hm.util.toggleRow(inputElements[i]);
		}
    }
}

/*
 * Toggle row class (for color).
 */
hm.util.toggleRow = function(checkBox) {
	var row = checkBox.parentNode.parentNode;
	if (checkBox.checked) {
		if (/\beven\b/.test(row.className)) {
			row.className = row.className.replace(/\beven\b/, "evenSelected");
		} else {
			row.className = row.className.replace(/\bodd\b/, "oddSelected");
		}
	} else {
		if (/\bevenSelected\b/.test(row.className)) {
			row.className = row.className.replace(/\bevenSelected\b/, "even");
		} else {
			row.className = row.className.replace(/\boddSelected\b/, "odd");
		}
	}
}

hm.util.toggleHideElement = function(name, hide) {
	var element = document.getElementById(name);
	if (element) {
		if (hide) {
			element.style.display = "none";
		} else {
			element.style.display = "";
		}
	}
}
hm.util.toggleHideElements = function(elementIds, hide) {
	if(YAHOO.lang.isArray(elementIds)) {
		for(var index=elementIds.length; index >= 0; index--) {
			this.toggleHideElement(elementIds[index], hide);
		}
	}
}

hm.util.loadImage = function(url) {
	var img = null;
	if (document.images) {
		img = new Image();
		img.src = url;
	}
	return img;
}

/*
 * Show node
 */
hm.util.show = function(nodeId) {
	YAHOO.util.Dom.setStyle(nodeId, "display", "");
}

/*
 * Hide node
 */
hm.util.hide = function(nodeId) {
	YAHOO.util.Dom.setStyle(nodeId, "display", "none");
}

hm.util.invokeTreeOperation = function(url, operation, key) {
	url = url + "?operation=" + operation + "&key=" + key + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {}, null);
}

hm.util.invokeTrack = function(url, page, start) {
	if (page.length > 0 && start.length > 0) {
		var load = new Date().getTime() - start;
		if (load < 100000) {
			YAHOO.util.Connect.asyncRequest('GET', url+"?operation=track&pageOp="+page+"&loadTime="+load, {}, null);
		}
	}
}

hm.util.invokeMapTrack = function(url, page, start) {
	if (!hm.util.mapReload) {
		hm.util.mapReload = true;
		hm.util.invokeTrack(url, page, start);
	}
}
/*
 * Validation functions
 */
hm.util.reporttableFieldError = function(element, message) {
	// Reset the timer
	clearTimeout(this.fieldErrorTimeoutId);
	// element.focus(); not working on Firefox
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (fe == null) {
		this.fieldErrorIds.removeAItem(feId);
		var td = document.createElement("td");
		td.className = 'noteError';
		td.setAttribute("id", "text" + feId);
		td.appendChild(document.createTextNode(message));
		var tr = document.createElement("tr");
		tr.appendChild(td);
		var tbody = document.createElement("tbody");
		tbody.appendChild(tr);
		var table = document.createElement("table");
		table.border ="0";
		table.cellSpacing = "0";
		table.cellPadding = "0";
		table.appendChild(tbody);
		var div = document.createElement("div");
		div.setAttribute("id", feId);
		div.style.display = "none";
		div.appendChild(table);
		td = document.createElement("td");
		tr = document.createElement("tr");
		tr.appendChild(td);
		td = document.createElement("td");
		td.colSpan = 10; // Span all remaining columns, usually only 1
        td.appendChild(div);
		tr.appendChild(td);
		tr.setAttribute("name", "errormessagetr");
		var row = element.parentNode.parentNode;
		table = row.parentNode;
		table.insertBefore(tr, row);
	} else {
		// Field has had error message before, just change the text
		var td = document.getElementById("text" + feId);
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(message));
	}
	if (hm.util.vectorContains(this.fieldErrorIds, feId)) {
		// Field error message is still visible
	} else {
		this.fieldErrorIds.push(feId);
		this.showFieldError(feId);
	}
	this.delaydeleteFieldError(feId,2);

}
hm.util.delaydeleteFieldError = function(feId,seconds) {
	this.fieldErrorTimeoutId = setTimeout("hm.util.deleteFieldError('"+feId+"')", seconds * 5000);  // seconds
}
hm.util.deleteFieldError=function(feId)
{
	var tep=document.getElementById(feId).parentNode.parentNode;
	tep.parentNode.removeChild(tep);
}
/**
 * for pbr
 * */
hm.util.reportTableSelectError = function(element, message) {
	// Reset the timer
	clearTimeout(this.fieldErrorTimeoutId);
	// element.focus(); not working on Firefox
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (fe == null) {
		this.fieldErrorIds.removeAItem(feId);
		var td = document.createElement("td");
		td.className = 'noteError';
		td.setAttribute("id", "text" + feId);
		td.appendChild(document.createTextNode(message));
		var tr = document.createElement("tr");
		tr.appendChild(td);
		var tbody = document.createElement("tbody");
		tbody.appendChild(tr);
		var table = document.createElement("table");
		table.border ="0";
		table.cellSpacing = "0";
		table.cellPadding = "0";
		table.appendChild(tbody);
		var div = document.createElement("div");
		div.setAttribute("id", feId);
		div.style.display = "none";
		div.appendChild(table);
		td = document.createElement("td");
		tr = document.createElement("tr");
		tr.appendChild(td);
		td = document.createElement("td");
		td.colSpan = 10; // Span all remaining columns, usually only 1
        td.appendChild(div);
		tr.appendChild(td);
		tr.setAttribute("name", "errormessagetr");
		var row = element.parentNode;
		table = row.parentNode;
		table.insertBefore(tr, row);
	} else {
		// Field has had error message before, just change the text
		var td = document.getElementById("text" + feId);
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(message));
	}
	if (hm.util.vectorContains(this.fieldErrorIds, feId)) {
		// Field error message is still visible
	} else {
		this.fieldErrorIds.push(feId);
		this.showFieldError(feId);
	}
	this.delaydeleteFieldError(feId,2);

}
/*
 * Validation functions
 */
hm.util.reportFieldError = function(element, message) {
	// Reset the timer
	clearTimeout(this.fieldErrorTimeoutId);
	// element.focus(); not working on Firefox
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (fe == null) {
		this.fieldErrorIds.removeAItem(feId);
		var td = document.createElement("td");
		td.className = 'noteError';
		td.setAttribute("id", "text" + feId);
		td.appendChild(document.createTextNode(message));
		var tr = document.createElement("tr");
		tr.appendChild(td);
		var tbody = document.createElement("tbody");
		tbody.appendChild(tr);
		var table = document.createElement("table");
		table.border ="0";
		table.cellSpacing = "0";
		table.cellPadding = "0";
		table.appendChild(tbody);
		var div = document.createElement("div");
		div.setAttribute("id", feId);
		div.style.display = "none";
		div.appendChild(table);
		td = document.createElement("td");
		tr = document.createElement("tr");
		tr.appendChild(td);
		td = document.createElement("td");
		td.colSpan = 10; // Span all remaining columns, usually only 1
        td.appendChild(div);
		tr.appendChild(td);
		var row = element.parentNode.parentNode;
		table = row.parentNode;
		table.insertBefore(tr, row);
	} else {
		// Field has had error message before, just change the text
		var td = document.getElementById("text" + feId);
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(message));
	}
	if (hm.util.vectorContains(this.fieldErrorIds, feId)) {
		// Field error message is still visible
	} else {
		this.fieldErrorIds.push(feId);
		this.showFieldError(feId);
	}
	this.delayHideFieldError(2);
}

hm.util.reportFieldErrorDivGen = function(options) {
	return function(element, message) {
		hm.util.reportFieldErrorDiv(element, message, options);
	};
};
hm.util.reportFieldErrorDiv = function(element, message, options) {
	// Reset the timer
	clearTimeout(this.fieldErrorTimeoutId);
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (!message || message.trim() == "") {
		this.hideFieldError();
		return;
	}
	options = options || {};
	if (options.className && options.className.indexOf(" noteError") < 0) {
		options.className = options.className + " noteError";
	}
	var className = options.className || 'noteError';
	var renderFunc = options.renderFunc || function(baseElement, errDiv) {
		baseElement.parentNode.insertBefore(div, baseElement.parentNode.firstChild);
	}
	if (fe == null) {
		this.fieldErrorIds.removeAItem(feId);
		var div = document.createElement("div");
		div.className = className;
		div.setAttribute("id", feId);
		div.innerHTML = message;
		renderFunc(element, div);
	} else {
		// Field has had error message before, just change the text
		var div = document.getElementById(feId);
		div.innerHTML = message;
	}
	if (hm.util.vectorContains(this.fieldErrorIds, feId)) {
		// Field error message is still visible
	} else {
		this.fieldErrorIds.push(feId);
		this.showFieldError(feId);
	}
	this.delayHideFieldError(6);
}

hm.util.fieldErrorIds = new Array();

hm.util.onUnloadValidation = function() {
	clearTimeout(this.fieldErrorTimeoutId);
}

hm.util.delayHideFieldError = function(seconds) {
	this.fieldErrorTimeoutId = setTimeout("hm.util.hideFieldError()", seconds * 5000);  // seconds
}

hm.util.showFieldError = function(feId) {
	hm.util.wipeIn(feId, 300);
}

hm.util.hideFieldError = function() {
	for (var i = 0; i < this.fieldErrorIds.length; i++) {
		hm.util.wipeOut(this.fieldErrorIds[i], 600);
	}
	this.fieldErrorIds = new Array();
}

hm.util.validateName = function(value, label) {
	return hm.util.validateString(value, label);
}

hm.util.validateNameWithBlanks = function(value, label) {
	return hm.util.validateStringWithBlank(value, label);
}

hm.util.validateIntegerRange = function(value, label, min, max) {

	if (value.length == 0 || isNaN(value)) {
		return label + " must be a positive integer number.";
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if (48 > code || code > 57) {
				return label + " must be a positive integer number.";
	       }
	   }
	}

	if (value < min || value > max){
		return label + " must be between " + min + " and " + max + ".";
	}
}

hm.util.validateInteger = function(value,label){

	if (value.length == 0 || isNaN(value)) {
		return label + " must be a integer number.";
	}else{
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if ((48 > code || code > 57 ) && code != 45) {
				return label + " must be a integer number.";
	       }
	    }
	}
}

hm.util.validateNumberRange = function(value, label, min, max, excludebBoundary) {

	if (value.length == 0 || isNaN(value)) {
		return label + " must be a positive number.";
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if ((48 > code || code > 57) && code != 46 ) {
				return label + " must be a positive number.";
	       }
	   }
	}
	if(excludebBoundary){
		if (value <= min || value >= max){
			return label + " must be between " + min + " and " + max + ".";
		}
	}else{
		if (value < min || value > max){
			return label + " must be between " + min + " and " + max + ".";
		}
	}
}

hm.util.validateHexRange = function(value, label) {

	if (value.length == 0) {
		return label + " must be a hex number.";
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if (!((code >47 && code <58) ||(code >64 && code <71) || (code >96 && code <103))) {
	       		return label + " must be a hex number.";
	       	}
	   }
	}
}

hm.util.trim = function(s)	{
  	if	(s == null || s.length == 0 || s.indexOf(" ") < 0) {
  		return s;
  	}
	for (var j = s.length - 1; j >= 0; j--) {
		if (s.charAt(j) == " ") {
			continue;
		} else {
			break;
		}
	}
 	s =  s.substring(0, j+1);
	for (var k = 0; k < s.length; k++) {
		if (s.charAt(k) == " ") {
			continue;
		} else {
			break;
		}
	}
	return s.substring(k);
}

hm.util.validateIpAddressFormat = function(ipAddress) {
	if (ipAddress.length == 0) {
		return false;
	}
    //var tokens = hm.util.trim(ipAddress).split(".");
	//fix bug 16823
	var tokens = ipAddress.split(".");
	if (tokens.length != 4) {
		return false;
	}
	if (tokens[0] == 0) {
     	return false;
    }
	for (var k=0; k<4; k++) {
		if (isNaN(tokens[k])|| (tokens[k].length ==0) || (tokens[k].length >3) ||tokens[k]>255 || tokens[k]<0) {
			return false;
		}
		if (tokens[k].length > 1 && tokens[k].indexOf("0")==0) {
			return false;
		}
		if ((tokens[k].indexOf(" ")>-1)) {
			return false;
		}
	}
	return true;
}

hm.util.validateIpAddress = function(ipAddress) {
	if(!hm.util.validateIpAddressFormat(ipAddress)) {
		return false;
	}
	var tokens = ipAddress.split(".");
	if (tokens[3] > 254) {
     	return false;
    }
	return true;
}
/**
 * supply ip address the last section can be 255.
 */
hm.util.validateIpAddressExtension = function(ipAddress) {
	if(!hm.util.validateIpAddressFormat(ipAddress)) {
		return false;
	}
	var tokens = ipAddress.split(".");
	if (tokens[3] > 255) {
     	return false;
    }
	return true;
}

// supply ip address(include 0 and boardcast) check, mask can be empty.
hm.util.validateIpAddressWithMask= function(ipAddress, netmask) {
	if(!hm.util.validateIpAddressFormat(ipAddress)) {
		return false;
	}
	//exist mask
	if (netmask != undefined) {
		// 1-32
		if (netmask.indexOf(".") == -1) {
			if (isNaN(netmask.trim())) {
				return false;
			}
			netmask = parseInt(netmask.trim());
			if (netmask >= 32 || netmask <= 0) {
				return false;
			}
			// convert mask to 255.255.255.255
			netmask = hm.util.intToStringNetMask(netmask);
			
		//255.255.255.255	
		} else {
			if (!hm.util.validateMask(netmask)) {
				return false;
			}
		}
		
		var ip = hm.util.stringToIntAddress(ipAddress);
		var mask = hm.util.stringToIntAddress(netmask);
		var network = ip & mask;
		var address = network | (~mask);
		var broad = hm.util.intToStringAddress(address);
		var networkString = hm.util.intToStringAddress(network);
		if (ipAddress == broad || ipAddress == networkString) {
			return false;
		}
		
	}
	return true;
}

hm.util.validateHostname = function(hostname) {
	if (hostname.length > 255)
		return false;
	var rule = /^([a-zA-Z0-9]|[a-zA-Z0-9][-a-zA-Z0-9]{0,61}[a-zA-Z0-9])(\.([a-zA-Z0-9]|[a-zA-Z0-9][-a-zA-Z0-9]{0,61}[a-zA-Z0-9]))*$/g;
	var reg = new RegExp(rule);
	return reg.test(hostname);
}

hm.util.validateMacAddress = function(macAddress, maxLength) {
	if (macAddress.length != maxLength) {
		return false;
	}
    if (macAddress.indexOf(" ") > -1) {
		return false;
	}
	for (var count = 0; count < macAddress.length; count++) {
       var code = macAddress.charCodeAt(count);
       if (!((code >47 && code <58) ||(code >64 && code <71) || (code >96 && code <103))) {
       		return false;
       }
    }
	return true;
}

hm.util.normalizeIpAddress = function(ipAddress) {
	if (ipAddress.length == 0) {
		return null;
	}
    var tokens = hm.util.trim(ipAddress).split(".");
	if (tokens.length < 4 || tokens.length > 4) {
		return null;
	}
	var normalized = "";
	for (var k=0; k<4; k++) {
		if (isNaN(tokens[k])|| (tokens[k].length ==0) ||tokens[k]>255 || tokens[k]<0) {
			return null;
		}
		if ((tokens[k].indexOf(" ")>-1)) {
			return null;
		}
		if (k == 3) {
			normalized += parseInt(tokens[k]);
		} else {
			normalized += parseInt(tokens[k]) + '.';
		}
	}
	return normalized;
}

hm.util.validateMask = function(mask) {
	if (!hm.util.validateIpAddressFormat(mask)) {
		return false;
	}
	var intMask = hm.util.stringToIntAddress(mask);
	var bit = 1 << 31;
	for (var i=0; i<32; i++) {
		if (intMask & bit) {
			intMask <<= 1;
		} else {
			break;
		}
	}
	return intMask == 0;
}

/*
 * Multicase address is D class, value between 224.0.0.0-239.255.255.255.
 */
hm.util.validateMulticastAddress = function(ipAddress){
	var isIp = hm.util.validateIpAddressFormat(ipAddress);
	if(!isIp){
		return false;
	}
	var start = hm.util.getIpAddressValue("224.0.0.0");
	var end = hm.util.getIpAddressValue("239.255.255.255");
	var address = hm.util.getIpAddressValue(ipAddress);
	return (address >= start && address <= end);
}

/*
 * this consists of the IP network ID and an all 1's host number
 */
hm.util.validateBroadcastAddress = function(ipAddress, netmask){
	var isIp = hm.util.validateIpAddressFormat(ipAddress);
	var isMask = hm.util.validateMask(netmask);
	if(!isIp || !isMask){
		return false;
	}
	var ip = hm.util.stringToIntAddress(ipAddress);
	var mask = hm.util.stringToIntAddress(netmask);
	var network = ip & mask;
	var address = network | (~mask);
	var broad = hm.util.intToStringAddress(address);
	return ipAddress == broad;
}

/*
 * the string char must be 'a'~'z' or 'A'~'Z'
 */
hm.util.validateSipFieldString = function(value) {
	if (value.length == 0) {
		return false;
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if (!((code >64 && code <91) || (code >96 && code <123))) {
	       		return false;
	       	}
	   }
	}
	return true;
}

/*
 * the string char must be '0'~'9' 'a'~'z' or 'A'~'Z'
 */
hm.util.validateActivationKeyString = function(value) {
	if (value.length == 0) {
		return false;
	} else {
		for (var count = 0; count<value.length; count++) {
	       var code = value.charCodeAt(count);
	       if (!((code >47 && code <58) ||(code >64 && code <91) || (code >96 && code <123))) {
	       		return false;
	       	}
	   }
	}
	return true;
}

/*
 * the string char must be '0'~'9' 'a'~'z' or 'A'~'Z' or '-' '.'
 */
hm.util.validateDnsDomainString = function(value, label) {
	for (var count = 0; count<value.length; count++) {
       var code = value.charCodeAt(count);
       if (!(code == 45 || code == 46 || (code >47 && code <58) || (code >64 && code <91) || (code >96 && code <123))) {
    	   return label + " cannot contain '" + String.fromCharCode(code) + "'.";
       	}
   }
}

/*
 * Assumes a valid address and mask string
 */
hm.util.validateSubnet = function(networkAddress, mask) {
	var intNetworkAddress = hm.util.stringToIntAddress(networkAddress);
	var intMask = hm.util.stringToIntAddress(mask);
	var i = intNetworkAddress & ~intMask;
	return i == 0;
}

/*
 * Assumes a valid address string
 */
hm.util.stringToIntAddress = function(address) {
    var tokens = hm.util.trim(address).split(".");
    var intAddress = parseInt(tokens[0]);
	for (var i=1; i<4; i++) {
		intAddress <<= 8;
		intAddress += parseInt(tokens[i]);
	}
	return intAddress;
}

/*
 * Reverse a int address to string
 */
hm.util.intToStringAddress = function(address) {
	var ip = (address >>> 24) & 0xff;
	ip += "." + ((address & 0x00ffffff) >>> 16);
	ip += "." + ((address & 0x0000ffff) >>> 8);
	ip += "." + (address & 0x000000ff);
	return ip;
}

/*
 * transfer a string ip to a interger value
 */
hm.util.getIpAddressValue = function(ipAddress){
	ipAddress = ipAddress || "";
	var fragement = ipAddress.split(".");
	var v1 = (fragement[0]||"")*Math.pow(2,24);
	var v2 = (fragement[1]||"")*Math.pow(2,16);
	var v3 = (fragement[2]||"")*Math.pow(2,8);
	var v4 = (fragement[3]||"")*Math.pow(2,0);
	return v1 + v2 + v3 + v4;
}

/**
*	transfer number netmask to String. 32 to "255.255.255.255"; 24 to "255.255.255.0"
*/
hm.util.intToStringNetMask = function(netmask){
	if (netmask>=32 || netmask<=0) {
		return "";
	}
	var rightValue=32-netmask;
	var doubleVlaue=((Math.pow(2,32)-Math.pow(2,rightValue))).toString(2);
	return parseInt(doubleVlaue.substring(0, 8), 2) + "." + parseInt(doubleVlaue.substring(8, 16),2) + "." + parseInt(doubleVlaue.substring(16, 24),2) + "." + parseInt(doubleVlaue.substring(24, 32),2) ;
}

/*
 * validate two address with a netmask in a same subnet,
 * also support check the start ip and end ip order.
 */
hm.util.validateIpSubnet = function(startIpAddress, label1, endIpAddress, label2, netmask, checkOrder, maxCount){
	function getStartIpAddressValue(intAddress, netmask){
		var maskValue = hm.util.getIpAddressValue(netmask);
		var s = intAddress & maskValue;
		return s < 0 ? Math.pow(2,32)+s : s;
	}

	function getValidIpAddressCount(netmask){
		var maskValue = hm.util.getIpAddressValue(netmask);
		return Math.pow(2,32) - maskValue;
	}

	var ipValue1 = hm.util.getIpAddressValue(startIpAddress);
	var ipValue2 = hm.util.getIpAddressValue(endIpAddress);
	if(checkOrder && (ipValue2 < ipValue1)){
		return label1 + " cannot be larger than " + label2;
	}
	var maxValue = Math.max(ipValue1, ipValue2);
	var minValue = Math.min(ipValue1, ipValue2);
	var startIpValue = getStartIpAddressValue(minValue, netmask);
	var ipCount = getValidIpAddressCount(netmask);
	if ( maxValue >= (startIpValue + ipCount) ){
		return "The " + label1 + "  is not in the same subnet as " + label2;
	}
	if(maxCount && (ipValue2-ipValue1 + 1) > maxCount){
		return "The ip count between " + label1 + " and " + label2 + " cannot be larger than " + maxCount;
	}
}


/*
* validate two subnet whether overlap,
* 
*/
hm.util.validateOverlapSubnet = function(startIpAddress1, endIpAddress1, firstNetmask, startIpAddress2, endIpAddress2, secondNetmask){
	function getStartIpAddressValue(intAddress, netmask){
		var maskValue = hm.util.getIpAddressValue(netmask);
		var s = intAddress & maskValue;
		return s < 0 ? Math.pow(2,32)+s : s;
	}

	function getValidIpAddressCount(netmask){
		var maskValue = hm.util.getIpAddressValue(netmask);
		return Math.pow(2,32) - maskValue;
	}

	var ipLong1 = hm.util.getIpAddressValue(startIpAddress1);
	var ipLong2 = hm.util.getIpAddressValue(startIpAddress2);
	
	var maskLong1 = Math.pow(2,32)-Math.pow(2,(32-firstNetmask));
	var maskLong2 = Math.pow(2,32)-Math.pow(2,(32-secondNetmask));

	var s1 = ipLong1 & maskLong1;
	var s2 = ipLong2 & maskLong2;
	ipLong1 =  s1 < 0 ? Math.pow(2,32)+s1 : s1;
	ipLong2 =  s2 < 0 ? Math.pow(2,32)+s2 : s2;

	if (s1==s2) {
		return "WAN ports subnet cannot be overlaped,please check it";
	}
		
	var minIpLong = ipLong1 > ipLong2 ? ipLong2: ipLong1;
	
	if(firstNetmask != secondNetmask) {
	    // compare the subnetwork is contained by the other one.
	    var s3 = (firstNetmask < secondNetmask ? ipLong2 : ipLong1)
	    & (firstNetmask < secondNetmask ? maskLong1 : maskLong2);
	    var ipLong3 = s3 < 0 ? Math.pow(2, 32) + s3 : s3;
	    var s5 = (ipLong1 & (firstNetmask < secondNetmask ? maskLong1 : maskLong2)) ==
	    		(ipLong2 & (firstNetmask < secondNetmask ? maskLong1 : maskLong2));
	    if(s5 && ipLong3 == minIpLong) {
	    	return "WAN ports subnet cannot be overlaped,please check it";
	    }
	}

	var s4 = ipLong1 & ipLong2;
	var ipLong4 =  s4 < 0 ? Math.pow(2,32)+s4 : s4;

	if(ipLong4 > minIpLong){
		return "WAN ports subnet cannot be overlaped,please check it";
	}
	
}


/*
 * Animation: wipe in
 */
hm.util.wipeIn = function(nodeId, ms) {
	hm.util.show(nodeId);
//	dojo.lfx.html.wipeIn(nodeId, ms).play();
}

/*
 * Animation: wipe out
 */
hm.util.wipeOut = function(nodeId, ms) {
	hm.util.hide(nodeId);
//	dojo.lfx.html.wipeOut(nodeId, ms).play();
}

/*
 * Input direct or select from pull down list
 */
hm.util.singObjectSelect = function(selEle, inputName) {
	var inputEle = document.getElementById(inputName);
	inputEle.value = selEle.options[selEle.selectedIndex].text;
	if (inputEle.value=='Create new VLAN') {
		inputEle.value='';
	}
	inputEle.style.zIndex = inputEle.value != ''? -1 : 2;
	if (inputEle.value=='') {
		inputEle.focus();
	}
	var sipTr = document.getElementById("radiusLibrarySipOperation");
	if (sipTr != undefined) {
		hm.util.radiusLibrarySip(sipTr.options[sipTr.selectedIndex].value, selEle.options[selEle.selectedIndex].value, true);
	}
	var osVersionDes = document.getElementById("osObject_description");
	if (osVersionDes != undefined && inputName == 'osVersion') {
		if (selEle.selectedIndex > 0) {
			osVersionDes.value = hm.util.DEFAULT_OS_OBJECTS_VERSION_DES[selEle.selectedIndex-1];
		} else {
			osVersionDes.value = "";
		}
	}
	var option55Des = document.getElementById("osObject_option55");
	var option55Values = document.getElementById("option55DhcpSelect");
	if (option55Des != undefined && inputName == 'dhcpOsVersion' && document.getElementById('dhcpOsVersion').value != '') {
		if(option55Values!= undefined && option55Values[selEle.selectedIndex].text != ""){
			option55Des.value=option55Values[selEle.selectedIndex].text;
		} else {
			option55Des.value='Default';
		}
		option55Des.readOnly=true;
	}
	if (option55Des != undefined && inputName == 'dhcpOsVersion' && document.getElementById('dhcpOsVersion').value == '') {
		option55Des.readOnly=false;
		option55Des.value=''
	}
}

hm.util.onblurEvent = function(selEle, inputName){
	if(inputName == 'dhcpOsVersion') {
		var osVersionDhcpSelect = document.getElementById('osVersionDhcpSelect');
		var dhcpOsVersion = document.getElementById('dhcpOsVersion');
		var option55 = document.getElementById('osObject_option55');
		var option55Values = document.getElementById("option55DhcpSelect");
		for (var i=0;i<osVersionDhcpSelect.options.length;i++) {
			if (osVersionDhcpSelect.options[i].text==dhcpOsVersion.value && dhcpOsVersion.value!=''){
				if(option55Values!= undefined && option55Values[i].text != ""){
					option55.value=option55Values[i].text;
				} else {
					option55.value='Default';
				}
				option55.readOnly=true;
				break;
			}
			if(i==osVersionDhcpSelect.options.length-1) {
				option55.readOnly=false;
				if(option55.value=='Default'){
					option55.value=''
				}
			}
		}
	}

}

hm.util.radiusLibrarySip = function(operIndex, fieldIndex, fieldChange) {
	// occur before or after
	if (operIndex == 5 || operIndex == 6) {
		hm.util.hide('valueText');
		hm.util.show('valueTime');
		hm.util.hide('valueList');
		hm.util.hide('valueNumber');
	} else if (operIndex == 7 || operIndex == 8 || operIndex == 9) {
		hm.util.hide('valueText');
		hm.util.hide('valueTime');
		hm.util.hide('valueList');
		hm.util.show('valueNumber');
	} else {
		// BL(valid patron), CQ(valid patron password) (Y or N)   BH(currency type)
		if (fieldIndex == 13 || fieldIndex == 14 || fieldIndex == 22) {
			hm.util.hide('valueText');
			hm.util.show('valueList');
			hm.util.hide('valueTime');
			hm.util.hide('valueNumber');
			var selectOp = document.getElementById("valueListSelect");
			var listValue = fieldIndex == 13 ? hm.util.RADIUS_LIBRARY_SIP_BH : hm.util.RADIUS_LIBRARY_SIP_BL;
			if (selectOp.length != listValue.length) {
				selectOp.length = 0;
				selectOp.length = listValue.length;
				for(var i = 0; i < listValue.length; i ++) {
					selectOp.options[i].value=listValue[i];
					selectOp.options[i].text=listValue[i];
				}
			}
		} else if (fieldIndex == 23 && fieldChange) {
			document.getElementById("radiusLibrarySipOperation").selectedIndex = 4;
			hm.util.hide('valueText');
			hm.util.show('valueTime');
			hm.util.hide('valueList');
			hm.util.hide('valueNumber');
		} else {
			hm.util.hide('valueList');
			hm.util.show('valueText');
			hm.util.hide('valueTime');
			hm.util.hide('valueNumber');
			var valueStr = document.forms[formName].valueStr;
			// BZ(hold items limit), CA(overdue items limit), CB(charged items limit)
			if (fieldIndex == 17 || fieldIndex == 18 || fieldIndex == 19) {
				valueStr.value = "0010";
			// BV(fee amount), CC(fee limit)
			} else if (fieldIndex == 16 || fieldIndex == 20) {
				valueStr.value = "10.1";
			// BE(e-mail address)
			} else if (fieldIndex == 11) {
				valueStr.value = "admin@163.com";
			} else {
				valueStr.value = "";
			}
		}
	}
}

/*
 * Make the select editable
 */
hm.util.createEditableSelect = function(select, inputName){
	select = typeof select == "string" ? document.getElementById(select):select;
	inputName = inputName || "selectInput";
	var container = document.createElement("div");
	container.style.position = "relative";
	select.parentNode.insertBefore(container, select); // insert container
	var input = document.createElement("input");
	input.type = "text";
	input.name = inputName;
	input.className = "selectInput";
	input.style.width = (select.offsetWidth - 32) + "px";
	container.appendChild(input);
	container.appendChild(select);
	select.inputElement = input;
	// fix issue in chrome
	select.style.margin = "0px";
	// add blank item if there's none
	if(select.options.length == 0){
		// append a blank item
		var option = new Option("", "");
		try{
			select.add(option, null); // DOM
		}catch(e){
			select.add(option); // IE
		}
	} else if(select.options.length == 1 && select.options[0].value == -1){
		// change the first item to blank
		select.options[0].text == " ";
	} else {
		// insert a blank item
		var option = new Option("select, then type directly...", "");
		option.style.color = "#999";
		try{
			select.add(option, select.options[0]); // DOM
		}catch(e){
			select.add(option, 0); // IE
		}
	}
	function update(select, input){
		input.value = select.options[select.selectedIndex].value;
		input.style.zIndex = input.value != ''? -1 : 2;
		if (input.value.trim()=='') {
			input.focus();
		}
	}
	// register listener
	YAHOO.util.Event.addListener(select, "change", function() {
		update(this, input);
	});
	update(select, input);
}

/*
 * Animation: keyPress Permit
 *         e: event
 *      type: 'ten': permit to input char 0..9
 *            'hex': permit to input char 0..9 and A..F
 *            'hexWithChar': permit to input char 0..9 and A..F and : and -
 *            'ip' : permit to input char 0..9 and '.'
 *            'ipMask' : permit to input char 0..9 and '.' and '/'
 *            'tendot' : permit to input char 0..9 and '.'
 *            'attribute' : permit to input char 0..9 and '-'','
 *            'name' : permission base on document, don't allow 3 chars : 32space 34" and 63?
 *            'nameWithBlank' : permission base on document, don't allow 2 chars : 34" and 63?
 *	          'password' : permission base on document, don't allow 3 chars : 32space 34" and 63?
 *            'psk' : permission base on document, don't allow 3 chars : 32space 34" and 63?
 *	          'ssid' : permission base on document, allow all the print chars
 *            'location' : permission base on document, don't allow 20 chars : 32space 34" 36$ 38& 39' 40( 41) 42* 43+ 44, 45- 46. 47/ 59; 60< 62> 63? 92\ 96` 124|
 *            'username' : permission base on document, don't allow 5 chars : 32space 34" 47/ 58: and 63?
 *            'directory' : don't allow 13 chars : 34" 35# 38& 39' 40( 41) 42* 47/ 59; 63? 92\ 124| 126~
 *			  'alphaNum' : permit '0'~'9','A'~'Z', 'a'~'z'
 *			  'alphaNum-_' : permit '0'~'9','A'~'Z', 'a'~'z', '-', '_'
 *            'activation' : permit '0'~'9','A'~'Z','a'~'z' and '-'
 *            'dnsDomain' : permit '0'~'9','A'~'Z','a'~'z' and '-' '.'
 *			  'ipTrack' : permit to input char 0..9 and '.'','
 *            'radioChannel' : permit to input char 0..9 and '-'
 *			  'phoneNum' : permit '0'-'9', '(', ')','+','-', ' '
 *			  'specialname' : permission base on document, don't allow 5 chars : 32space 34" 35# 47/ 58: 63?
 *			  'cliblob':permission base on document, don't allow 2 chars : 34" and 63?
 */
hm.util.keyPressPermit = function(e,type) {
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	if (type=="ten") {
		if(48 <= keycode && keycode <=57){
			return true;
		}
	} else if (type=="tendot") {
		if ((48 <= keycode && keycode <=57) || keycode == 46) {
			return true;
		}
	} else if (type=="integer") {
		if ((48 <= keycode && keycode <=57) || keycode == 45) {
			return true;
		}
	} else if (type=="hex") {
		if((48 <= keycode && keycode <=57) ||
		(65 <= keycode && keycode <=70) ||
		(97 <= keycode && keycode <=102)){
			return true;
		}
	} else if (type=="hexWithChar") {
		if((48 <= keycode && keycode <=57) ||
		(65 <= keycode && keycode <=70) ||
		(97 <= keycode && keycode <=102) || keycode==45 || keycode==58){
			return true;
		}

	} else if (type=="alphaNum") {
		if((48 <= keycode && keycode <=57) ||
		(65 <= keycode && keycode <=90) ||
		(97 <= keycode && keycode <=122)){
			return true;
		}
	} else if (type=="alphaNum-_") {
		if((48 <= keycode && keycode <=57) ||
		(65 <= keycode && keycode <=90) ||
		(97 <= keycode && keycode <=122) ||
		keycode == 45 || keycode == 95){
			return true;
		}
	} else if (type=="ip") {
		if((48 <= keycode && keycode <=57) || keycode ==46){
			return true;
		}
	} else if (type=="ipMask") {
		if((47 <= keycode && keycode <=57) || keycode ==46){
			return true;
		}
	} else if (type=="attribute") {
		if((48 <= keycode && keycode <=57) || keycode ==44 || keycode ==45){
			return true;
		}
	} else if (type=="ipTrack") {
		if((48 <= keycode && keycode <=57) || keycode ==44 || keycode ==46){
			return true;
		}
	} else if (type=="name") {
		//if(32 != keycode && 63 != keycode){
		//	return true;
		//}
		if(_containChar(hm.util.KEYCODES_STRING, keycode) > -1){
			return true;
		}
	} else if (type=="nameWithBlank") {
		if(_containChar(hm.util.KEYCODES_STRINGWithBlank, keycode) > -1){
			return true;
		}
	} else if(type=="nameDomainUser"){
		if(_containChar(hm.util.KEYCODES_DOMAINUSERNAME, keycode) > -1){
			return true;
		}
	} else if (type=="password") {
		//if(32 != keycode){
		//	return true;
		//}
		if(_containChar(hm.util.KEYCODES_PWD, keycode) > -1){
			return true;
		}
	} else if (type=="ssid") {
		if(_containChar(hm.util.KEYCODES_SSID, keycode) > -1){
			return true;
		}
	} else if (type=="pskUserName") {
		if(_containChar(hm.util.KEYCODES_PSK_USERNAME, keycode) > -1){
			return true;
		}
	} else if (type=="username") {
		if(_containChar(hm.util.KEYCODES_USERNAME, keycode) > -1){
			return true;
		}
	} else if (type=="directory") {
		if(_containChar(hm.util.KEYCODES_DirectoryName, keycode) > -1){
			return true;
		}
	} else if (type=="radioChannel") {
		if((48 <= keycode && keycode <=57) || keycode == 45){
			return true;
		}
	} else if (type=="activation") {
		if((48 <= keycode && keycode <=57) || keycode == 45 || (65 <= keycode && keycode <=90)
				|| (97 <= keycode && keycode <=122)){
			return true;
		}
	} else if (type=="dnsDomain") {
		if((48 <= keycode && keycode <=57) || keycode == 45 || (65 <= keycode && keycode <=90)
				|| (97 <= keycode && keycode <=122) || keycode ==46){
			return true;
		}
	} else if (type=="phoneNum") {
		if ((keycode >47 && keycode <58)
       			|| (keycode == 32) || (keycode == 35)
       			||(keycode == 40)  || (keycode == 41) || (keycode == 42)
       			||(keycode == 43)  || (keycode == 45)) {
       		return true;
       }
	}else if(type=="macaddress"){
		if((48 <= keycode && keycode <=57) ||
				(65 <= keycode && keycode <=70) ||
				(97 <= keycode && keycode <=102) || (keycode == 46) || (keycode == 45) || (keycode == 58)){
					return true;
		}
	}else if (type=="scanchannel") {
		if((48 <= keycode && keycode <=57) || keycode == 44){
			return true;
		}
	}else if (type=="specialname") {
		if(_containChar(hm.util.KEYCODES_SPECIALNAME, keycode) > -1){
			return true;
		}
	}else if(type=="cliblob"){
		if(_containChar(hm.util.KEYCODES_CLIBLOB, keycode) > -1){
			return true;
		}
	}

	return false;
}

hm.util.keyPressWithBlankPermit = function(e,type) {
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	// if Enter is pressed.
	if (keycode == 32) {
		return true;
	}
	return hm.util.keyPressPermit(e, type);
}
hm.util.keyPressWithEnterPermit = function(e,type) {
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}

	// if Enter is pressed.
	if (keycode == 13) {
		return true;
	}

	return hm.util.keyPressPermit(e, type);
}
/*
 * Return -1 when not contains, else return index.
 */
_containChar = function(range, keycode) {
	for(var i = 0; i<range.length; i++){
		if(range[i] == keycode){
			return i;
		}
	}
	return -1;
}

_validateString = function(range, value, label) {
	if (value.length == 0 || value.trim().length == 0) {
		return label + " is a required field.";
	}
	//special for ssid check: cannot only with '|(124)'.
	if(range == hm.util.KEYCODES_SSID){
		if(value.length == 1){
			var startCode = value.charCodeAt(0);
			if(startCode == 124){
				return label + " cannot only be '" + String.fromCharCode(startCode) + "'.";
			}
		}
	}
	//special for username check: cannot start with '#(35)', '+(43)', '-(45)'.
	if(range == hm.util.KEYCODES_USERNAME){
		var startCode = value.charCodeAt(0);
		if(startCode == 35 || startCode == 43 || startCode == 45){
			return label + " cannot start with '" + String.fromCharCode(startCode) + "'.";
		}
	}
	for(var i = 0; i< value.length; i++){
		var charCode = value.charCodeAt(i);
		if(_containChar(range, charCode) == -1){
			return label + " cannot contain '" + String.fromCharCode(charCode) + "'.";
		}
	}
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateSsid = function(value, label){
	return _validateString(hm.util.KEYCODES_SSID,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validatePskUserName = function(value, label){
	return _validateString(hm.util.KEYCODES_PSK_USERNAME,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validatePassword = function(value, label){
	return _validateString(hm.util.KEYCODES_PWD,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateUsername = function(value, label){
	return _validateString(hm.util.KEYCODES_USERNAME,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateString = function(value, label){
	return _validateString(hm.util.KEYCODES_STRING,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateStringWithBlank = function(value, label){
	return _validateString(hm.util.KEYCODES_STRINGWithBlank,value,label);
}

hm.util.validateCLIBlob = function(value, label){
	return _validateString(hm.util.KEYCODES_CLIBLOB,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateDoaminUserName = function(value, label){
	return _validateString(hm.util.KEYCODES_DOMAINUSERNAME,value,label);
}

/*
 * Return error message when contain invalid charactor, else return null.
 */
hm.util.validateDirectoryName = function(value, label){
	return _validateString(hm.util.KEYCODES_DirectoryName,value,label);
}

/*
 * Find child node by nodeName
 */
hm.util.findChildNode = function(node, name) {
	for (var i = 0; i < node.childNodes.length; i++) {
		if (node.childNodes[i].nodeName == name) {
			return node.childNodes[i];
		}
	}
	return null;
}

/*
 * Find TBODY element
 */
hm.util.findTableBody = function(tableId) {
	var table = document.getElementById(tableId);
	if (table == null) {
		return;
	}
	return hm.util.findChildNode(table, "TBODY");
}

/**
 * If only 1 row is selected, move it up by 1 spot.
 * If N rows are selected, move rows 2 to N above node 1.
 */
hm.util.moveRowsUp = function(tableId) {
	var tbody = hm.util.findTableBody(tableId);
	if (tbody == null) {
		return;
	}
	var offset = 0;
	var firstRow = -1;
	var target = -1;
	var source = -1;
	for (var i = 0; i < tbody.childNodes.length; i++) {
		var tr = tbody.childNodes[i];
		if (tr.nodeName == "TR") {
			var td = hm.util.findChildNode(tr, "TD");
			if (td == null) {
				continue;
			}
			var cb = td.childNodes[0];
			if (cb.nodeName != "INPUT") {
				continue;
			}
			if (firstRow < 0) {
				firstRow = i;
			}
			if (cb.checked) {
				if (target < 0) {
					target = i;
				} else {
					source = i;
					hm.util.moveRow(tbody, source, target, offset);
					// target has moved 1 spot
					target += offset + 1;
				}
			}
		} else {
			offset = 1;
			// spurious text element
		}
	}
	if (target < 0) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else if (source < 0 && target != firstRow) {
		// move target 1 spot higher
		hm.util.moveRow(tbody, target, target-1-offset, offset);
	}
}

/**
 * If only 1 row is selected, move it down by 1 spot.
 * If N rows are selected, move rows 1 to N-1 below node N.
 */
hm.util.moveRowsDown = function(tableId) {
	var tbody = hm.util.findTableBody(tableId);
	if (tbody == null) {
		return;
	}
	var offset = 0;
	var lastRow = -1;
	var target = -1;
	var source = -1;
	for (var i = tbody.childNodes.length - 1; i >= 0; i--) {
		var tr = tbody.childNodes[i];
		if (tr.nodeName == "TR") {
			var td = hm.util.findChildNode(tr, "TD");
			if (td == null) {
				continue;
			}
			var cb = td.childNodes[0];
			if (cb.nodeName != "INPUT") {
				continue;
			}
			if (lastRow < 0) {
				lastRow = i;
			}
			if (cb.checked) {
				if (target < 0) {
					target = i;
				} else {
					source = i;
					hm.util.moveRow(tbody, source, target, offset);
					// target has moved 1 spot
					target -= offset + 1;
				}
			}
		} else {
			offset = 1;
			// spurious text element
		}
	}
	if (target < 0) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else if (source < 0 && target != lastRow) {
		// move target 1 spot higher
		hm.util.moveRow(tbody, target, target+1+offset, offset);
	}
}

/**
 * Move 1 row.
 */
hm.util.moveRow = function(tbody, fromIndex, toIndex, offset) {
	var tr = tbody.childNodes[fromIndex];
	tbody.removeChild(tr);
	if (offset > 0) {
		// for Firefox
		var text = tbody.childNodes[fromIndex];
		tbody.removeChild(text);
		if (toIndex > tbody.childNodes.length - 1) {
			tbody.appendChild(text);
		} else {
			tbody.insertBefore(text, tbody.childNodes[toIndex]);
		}
	}
	if (toIndex > tbody.childNodes.length - 1) {
		tbody.appendChild(tr);
	} else {
		tbody.insertBefore(tr, tbody.childNodes[toIndex]);
	}
	hm.util.checkMovedRow(tr);
}

/*
 * For IE6, if row was moved, need to check it again.
 */
hm.util.checkMovedRow = function(tr) {
	var td = hm.util.findChildNode(tr, "TD");
	if (td == null) {
		return;
	}
	var cb = td.childNodes[0];
	if (cb.nodeName != "INPUT") {
		return;
	}
	if (!cb.checked) { // for IE6
		cb.checked = true;
	}
}

hm.util.hasCheckedBoxes = function(cbs) {
	var allItemsSelected = document.getElementById('allItemsSelectedVar');
	if (allItemsSelected && allItemsSelected.value == "true") {
		return true;
	}
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			return true;
		}
	}
	return false;
}

hm.util.hasAvailableCheckBoxs = function(cbs) {
	var allItemsSelected = document.getElementById('allItemsSelectedVar');
	if (allItemsSelected && allItemsSelected.value == "true") {
		return true;
	}
	if (cbs.length > 0) {
		return true;
	}
	return false;
}

hm.util.getSelectedIds = function() {
	var cbs = document.getElementsByName('selectedIds');
	var selecteds = new Array();
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].checked) {
			selecteds[selecteds.length] = cbs[i].value;
		}
	}
	return selecteds;
}

/*
 * Prompt for confirm delete
 */
hm.util.checkAndConfirmDelete = function() {
	hm.util.checkAndConfirmDeleteRule('selectedIds');
}

hm.util.checkAndConfirmDeleteRule = function(idName) {
	var inputElements = document.getElementsByName(idName);
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to remove.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected items.<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

hm.util.checkAndConfirmDeleteVhm = function() {
	var inputElements = document.getElementsByName("selectedIds");
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to remove.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('width', '430px');
		confirmDialog.cfg.setProperty('text', '<html><body>This operation will remove the selected VHMs.<br><br>Reset or keep the configurations on the devices you remove:<br>&nbsp;<input type="radio"  value="true" name="ck_resetRadio" id="ck_resetDeviceFlag">Reset device configurations to their default or bootstrap settings<br>&nbsp;<input type="radio" value="false" name="ck_resetRadio" checked="checked" id="ck_resetDeviceUnselect">Keep existing device configurations.<br><br>Do you want to continue?</body></html>');
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm delete
 */
hm.util.checkAndConfirmDeleteRuleFromRedirect = function(idName, confirmMsg, hmolFlg) {
	var inputElements = document.getElementsByName(idName);
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to remove.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		var mybuttons = [ { text:"Yes", isDefault:true, handler: function(){this.hide();doContinueOper();} }, 
	                      { text:"No", handler: function(){this.hide();}} ];
		/*var selectDeviceCount;
		var checkAll = document.getElementById('allItemsSelectedVar');
		if (checkAll && checkAll.value=="true") {
			selectDeviceCount="all devices?"
		} else {
			var len = hm.util.getSelectedIds().length;
			if (len==1) {
				selectDeviceCount = "device?";
			} else {
				selectDeviceCount = "" + hm.util.getSelectedIds().length + " devices?";
			}
		}*/
		
	    var msgsg = "<html><body>"+ confirmMsg + "<br><br>&nbsp;&nbsp;Do you want to continue?</body></html>";
	    var dlg = userDefinedConfirmDialog(msgsg, mybuttons, "Remove Selected Devices");
	    if (hmolFlg) {
	    	dlg.cfg.setProperty('width', '500px');
	    } else {
	    	dlg.cfg.setProperty('width', '430px');
	    }
	    dlg.show();
	}
}

/*
 * Prompt for confirm remove all successful entries
 */
hm.util.checkAndConfirmDeleteSuccessful = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to remove.");
		warnDialog.show();
	}else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove all successful items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm modify
 */
hm.util.checkAndConfirmModify = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to modify.");
		warnDialog.show();
		return;
	}
	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	doContinueOper();
}

/*
 * Prompt for confirm operation
 */
hm.util.checkAndConfirmMultiple = function(operation) {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to " + operation);
		warnDialog.show();
		return;
	}
	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}
	doContinueOper();
}

/*
 * Prompt for confirm multi modify unmanaged device
 */
hm.util.checkMultiModifyUnmanagedDevice = function(idName, confirmMsg,maps, ishmol) {
	var inputElements = document.getElementsByName(idName);
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to modify.");
		warnDialog.show();
		return;
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	} else {
		if (ishmol) {
			var selectedDeviceCount=0;
			var selectedNotPreConfig=false;
			var selectedApId='';
			var selectedApSerial='';
			var allchecked = document.getElementById('allItemsSelectedVar');
			if (allchecked && allchecked.value == "true") {
				for( oneItem in maps ) {
					selectedDeviceCount++;
					if (maps[oneItem].precfg==false) {
						selectedNotPreConfig=true;
					}
					if (selectedDeviceCount==1) {
						selectedApId=selectedApId + maps[oneItem].id;
						selectedApSerial=maps[oneItem].serialNum;
					} else {
						selectedApId=selectedApId + "," +maps[oneItem].id;
					}
				}
			} else {
				for (var i = 0; i < inputElements.length; i++) {
					if (inputElements[i].checked) {
						selectedDeviceCount++;
						if (maps[inputElements[i].value].precfg==false) {
							selectedNotPreConfig=true;
						}
						if (selectedDeviceCount==1) {
							selectedApId=selectedApId + maps[inputElements[i].value].id;
							selectedApSerial=maps[inputElements[i].value].serialNum;
						} else {
							selectedApId=selectedApId + "," +maps[inputElements[i].value].id;
						}
					}
				}
			}
			
			if (selectedDeviceCount!=1 && selectedNotPreConfig) {
				warnDialog.cfg.setProperty('text', confirmMsg);
				warnDialog.show();
				return;
			} else {
				doClickModifyContinueOper(selectedDeviceCount,selectedNotPreConfig,selectedApId,selectedApSerial);
			}
		} else {
			var selectedDeviceCount=0;
			var selectedApId='';
			var allchecked = document.getElementById('allItemsSelectedVar');
			if (allchecked && allchecked.value == "true") {
				for( oneItem in maps ) {
					selectedDeviceCount++;
					if (maps[oneItem].precfg==false) {
						selectedNotPreConfig=true;
					}
					if (selectedDeviceCount==1) {
						selectedApId=selectedApId + maps[oneItem].id;
						selectedApSerial=maps[oneItem].serialNum;
					} else {
						selectedApId=selectedApId + "," +maps[oneItem].id;
					}
				}
			} else {
				for (var i = 0; i < inputElements.length; i++) {
					if (inputElements[i].checked) {
						selectedDeviceCount++;
						if (selectedDeviceCount==1) {
							selectedApId=selectedApId + inputElements[i].value;
						} else {
							selectedApId=selectedApId + "," +inputElements[i].value;
						}
					}
				}
			}
			doClickModifyContinueOper(selectedDeviceCount,false,selectedApId,null);
		}
	}
}

/*
 * Prompt for confirm operation
 */
hm.util.checkAndConfirmOne = function(operation) {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to " + operation);
		warnDialog.show();
		return;
	}

	var selectCount = 0;

	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			selectCount++;
		}
	}

	if (selectCount != 1) {
		warnDialog.cfg.setProperty('text', "Please select one item.");
		warnDialog.show();
		return;
	}

	doContinueOper();
}

/*
 * Prompt for confirm warn
 */
hm.util.checkAndConfirmRevoke = function(warning) {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to revoke.");
		warnDialog.show();
		return;
	}
	if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
		return;
	}

	confirmDialog.cfg.setProperty('text', warning);
	confirmDialog.show();
}

/*
 * Prompt for confirm enable/disable reports
 */
hm.util.checkAndConfirmTriggerReport = function(isEnable) {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to " + (isEnable?"enable":"disable") + ".");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		var msg = isEnable ? "<html><body>This operation will enable the scheduled generation<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;of previously disabled reports. Do you want to<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;continue?</body></html>"
				: "<html><body>This operation will disable the scheduled generation<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;of all selected reports. Do you want to continue?</body></html>";
		confirmDialog.cfg.setProperty('text', msg);
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm accept
 */
hm.util.checkAndConfirmAccept = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to accept.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will accept the selected items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm move to friendly APs
 */
hm.util.checkAndConfirmFriendly = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to move.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will reclassify the selected items<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;as friendly APs. Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm move to rogue APs
 */
hm.util.checkAndConfirmRogue = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to move.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will reclassify the selected items<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;as rogue APs. Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm remove items
 */
hm.util.confirmRemoveItems = function() {
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected items<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from the list. Do you want to continue?</body></html>");
	confirmDialog.show();
}

/*
 * Prompt for confirm reassign HiveAPs to other virtual HM
 */
hm.util.checkAndConfirmReassign = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to reassign to a virtual HM.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will reassign the selected items<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to another virtual HM. Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm mitigate rogue APs
 */
hm.util.checkAndConfirmMitigate = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to mitigate.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>Mitigating rogue APs may have legal <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;consequences. Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm terminate mitigation on rogue APs
 */
hm.util.checkAndConfirmTerminateMitigate = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to terminate mitigation.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		confirmDialog.cfg.setProperty('text', "<html><body>This operation will terminate mitigation<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;of the selected items. Do you want to continue?</body></html>");
		confirmDialog.show();
	}
}

/*
 * Prompt for confirm refresh an IDP APs
 */
hm.util.checkRefreshIdp = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to refresh.");
		warnDialog.show();
	} else if (!hm.util.hasCheckedBoxes(inputElements)) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	} else {
		doContinueOper();
	}
}

/*
 * Prompt for confirm Clone select one item
 */
hm.util.confirmSelectOne = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		alert("There is no item to clone.");
		return false;
	}
	var selectCount = 0;
	for (var i = 0; i < inputElements.length; i++) {
		if (inputElements[i].checked) {
			selectCount++;
		}
	}
	if (selectCount != 1) {
		alert("Please select one item.");
		return false;
	}
	return true;
}

/*
 * Prompt for confirm Clone
 */
hm.util.checkAndConfirmClone = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to clone.");
		warnDialog.show();
	} else {
		var selectCount = 0;
		for (var i = 0; i < inputElements.length; i++) {
			if (inputElements[i].checked) {
				selectCount++;
			}
		}
		if (selectCount != 1) {
			warnDialog.cfg.setProperty('text', "Please select one item.");
			warnDialog.show();
		} else {
			doContinueOper();
		}
	}
}

/*
 * Prompt for confirm RunNow
 */
hm.util.checkAndConfirmRun = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to run.");
		warnDialog.show();
	} else {
		var selectCount = 0;
		for (var i = 0; i < inputElements.length; i++) {
			if (inputElements[i].checked) {
				selectCount++;
			}
		}
		if (selectCount != 1) {
			warnDialog.cfg.setProperty('text', "Please select one item.");
			warnDialog.show();
		} else {
			doContinueOper();
		}
	}
}

/*
 * Prompt for confirm assign
 */
hm.util.checkAndConfirmAssign = function() {
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to assign.");
		warnDialog.show();
	} else {
		var selectCount = 0;
		for (var i = 0; i < inputElements.length; i++) {
			if (inputElements[i].checked) {
				selectCount++;
			}
		}
		if (selectCount != 1) {
			warnDialog.cfg.setProperty('text', "Please select one item.");
			warnDialog.show();
		} else {
			doContinueOper();
		}
	}
}

hm.util.showtitle = function (obj){
	if(navigator.userAgent.indexOf("MSIE")>0) {
		oPopup = window.createPopup();
		if(obj.selectedIndex >= 0){
			var oPopBody = oPopup.document.body
			oPopBody.style.backgroundColor = "lightyellow";
			oPopBody.style.border = "solid black 1px";
			oPopBody.innerHTML = obj.options[obj.selectedIndex].text;
			oPopup.show(event.screenX+5, event.screenY,obj.options[obj.selectedIndex].text.length*10, 20);
			t=setTimeout("hm.util.hidetitle()", 2000);
		}
	} else {
		obj.title = obj.options[obj.selectedIndex].text;
	}
}
hm.util.hidetitle = function (){
	oPopup.hide();
}

/*
 * Prompt for confirm image Cancel
 */
hm.util.confirmCancel = function() {
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will cancel the upload process.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

/*
 * Prompt for confirm remove Captive Web Page Directory
 */
hm.util.confirmRemoveCwpDirectory = function() {
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the captive web page<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Directory. Do you want to continue?</body></html>");
	confirmDialog.show();
}

/*
 * Prompt for confirm remove Map Container
 */
hm.util.confirmRemoveMapContainer = function(mapName) {
	confirmDialog.cfg.setProperty('text', "<html><body>Are you sure you want to delete <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'"+ mapName + "'?</body></html>");
	confirmDialog.show();
}

/*
 * Prompt for confirm remove Map background image
 */
hm.util.confirmRemoveMapImage = function(imageName) {
	confirmDialog.cfg.setProperty('text', "<html><body>Are you sure you want to delete <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'"+ imageName + "'?</body></html>");
	confirmDialog.show();
}

/*
 * Prompt for confirm upload Map background image
 */
hm.util.confirmUploadMapImage = function(imageFiles) {
	var images = [];
	for(var i=0; i<imageFiles.length; i++){
		var size = imageFiles[i].size;
		var name = imageFiles[i].name;
		if(size/1024 > 1024){ //MB
			size = parseInt(size/1024/1024*100)/100 + " MB";
		}else{//KB
			size = parseInt(size/1024) + " KB";
		}
		images.push(" '" + name + "'" + " is " + size);
	}
	if(images.length > 1){
		confirmDialog.cfg.setProperty('text', "<html><body>Image"+images+".<br />For better performance, the image size should be less than 500 KB. Please resize the image first (for exmaple at <a href='http://www.picresize.com' target='_blank'>www.picresize.com</a>). Otherwise, HiveManager will automatically resize the image after it is uploaded. Do you still want to import these images?</body></html>");
	}else{
		confirmDialog.cfg.setProperty('text', "<html><body>The image"+images[0]+". For better performance, the image size should be less than 500 KB. Please resize the image first (for exmaple at <a href='http://www.picresize.com' target='_blank'>www.picresize.com</a>). Otherwise, HiveManager will automatically resize the image after it is uploaded. Do you still want to import this image?</body></html>");
	}
	confirmDialog.show();
}

/*
 * Prompt for confirm reboot devices
 */
hm.util.confirmRebooting = function(hasSwitch) {
	if(hasSwitch){
		confirmDialog.cfg.setProperty('text', "<html><body>Important: Do not unplug the power cord or PoE<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cable while rebooting. Note that all devices powered<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;by PoE from selected switches will lose power when<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;they reboot.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}else{
		confirmDialog.cfg.setProperty('text', "<html><body>Important: Do not unplug the power cord or PoE<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cable while rebooting.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}
	confirmDialog.show();
}

/*
 * Prompt for confirm reboot devices
 */
hm.util.confirmResetConfig = function(hasSwitch) {
	if(hasSwitch){
		confirmDialog.cfg.setProperty('text', "<html><body>Important: This operation will remove all existing settings (except for bootstrap settings) from the selected devices and reboot them. They will then reconnect to HiveManager as new devices.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}else{
		confirmDialog.cfg.setProperty('text', "<html><body>Important: This operation will remove all existing settings (except for bootstrap settings) from the selected devices and reboot them. They will then reconnect to HiveManager as new devices.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}
	confirmDialog.show();
}

/*
 * Prompt for confirm boot from backup
 */
hm.util.confirmBootImage = function(hasSwitch) {
	confirmDialog.cfg.setProperty('width', '380px');
	if(hasSwitch){
		confirmDialog.cfg.setProperty('text', "<html><body>Important: This operation will cause the selected devices<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to reboot. Do not unplug the power cord or PoE cable<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;while rebooting. Note that all devices powered by PoE<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from selected switches will lose power when they reboot.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}else{
		confirmDialog.cfg.setProperty('text', "<html><body>Important: This operation will cause the selected devices<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to reboot. Do not unplug the power cord or PoE cable<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;while rebooting.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	}
	confirmDialog.show();
}

/*
 * Prompt for confirm boot from pse reset
 */
hm.util.confirmRestPSE = function() {
	confirmDialog.cfg.setProperty('width', '380px');
	confirmDialog.cfg.setProperty('text', "<html><body>Important: All devices powered by PoE from the selected<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;switch will lose power during the power reset process.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
	confirmDialog.show();
}

hm.util.disableObject = function(obj){
	if(obj.disabled!=null && !obj.disabled) {
		obj.disabled=true;
	}
   	try{
     	obj.onclick=function(){return   false;};
   	}catch(e){
   	}
   	for(var i=0;i<obj.childNodes.length;i++){
   		hm.util.disableObject(obj.childNodes[i]);
	}
}

hm.util.enableObject = function(obj){
	if(obj.disabled!=null && obj.disabled) {
		obj.disabled=false;
	}

    for(var i=0;i<obj.childNodes.length;i++){
      hm.util.enableObject(obj.childNodes[i]);
    }
}

hm.util.convert2Html = function(msg){
	return msg.replace(/<(.+?)>/g,"&lt;$1&gt;").replace(/  /g," &nbsp;").replace(/(\r\n)|\r|\n/g,"<br>");
}


hm.util.insertSelectValue = function(value, text, selectObj, sortTextFlg, selectFlg) {
	var selectObjLength=selectObj.length;
	selectObj.length=selectObjLength+1;
	var indexInsert=selectObjLength;
	for(var i=0; i<selectObjLength; i++) {
		if (sortTextFlg) {
			if (selectObj.options[i].text>text){
				indexInsert=i;
				break;
			}
		} else {
			if (selectObj.options[i].value>value){
				indexInsert=i;
				break;
			}
		}
	}
	while (selectObjLength>indexInsert){
		selectObj.options[selectObjLength].value=selectObj.options[selectObjLength-1].value;
		selectObj.options[selectObjLength].text=selectObj.options[selectObjLength-1].text;
		selectObjLength--;
	}
	selectObj.options[indexInsert].value=value;
	selectObj.options[indexInsert].text=text;
	if (selectFlg){
		selectObj.selectedIndex = indexInsert;
	}
	if (selectObj.options[0].value == -1 && selectObj.options[0].text == 'None available') {
		 selectObj.remove(0);
	}
	// select or input value to create object
	var spanNode = selectObj.parentNode;
	if (spanNode && spanNode.nodeName == "SPAN" && spanNode.style.overflow == "hidden") {
		var textNode = spanNode.lastChild;
		if (textNode && textNode.nodeName == "INPUT" && textNode.type == "text" && YAHOO.util.Dom.getStyle(textNode, "position") == "absolute") {
			textNode.value = text;
			if (textNode.value=='Create new VLAN') {
				textNode.value='';
			}
			textNode.style.zIndex = textNode.value != ''? -1 : 2;
			if (textNode.value=='') {
				textNode.focus();
			}
		}
	}
}

hm.util.refreshAllSelectValue = function(selectObj, values) {
	var selectObjLength=selectObj.length;
	selectObj.length = 0;
	var indexInsert = 0;
	if (values && values.length > 0) {
		for (var i = 0; i < values.length; i++) {
			var tmpValue = values[i];
			selectObj.length++;
			selectObj.options[indexInsert].value = tmpValue.id;
			selectObj.options[indexInsert].text = tmpValue.name;
			indexInsert++;
		}
	} else {
		selectObj.length++;
		selectObj.options[0].value = -1;
		selectObj.options[0].text = 'None available';
	}
}

hm.util.resetSelectionValues = function(selectObj, values, getIdFunc, getValueFunc, withNoneTip) {
	if (!hm.util.isAFunction(getIdFunc)
			|| !hm.util.isAFunction(getValueFunc)) {
		return;
	}
	selectObj.length = 0;
	var indexInsert = 0;
	if (values && values.length > 0) {
		for (var i = 0; i < values.length; i++) {
			var tmpValue = values[i];
			selectObj.length++;
			selectObj.options[indexInsert].value = getIdFunc(tmpValue);
			selectObj.options[indexInsert].text = getValueFunc(tmpValue);
			indexInsert++;
		}
	} else {
		if (withNoneTip) {
			selectObj.length++;
			selectObj.options[0].value = -1;
			selectObj.options[0].text = 'None available';
		}
	}
}

hm.util.refreshRightOfMultiSelectValue = function(selectObj, values, leftSelectObj) {
	selectObj.length = 0;
	var indexInsert = 0;
	if (values && values.length > 0) {
		for (var i = 0; i < values.length; i++) {
			var tmpValue = values[i];
			selectObj.length++;
			selectObj.options[indexInsert].value = tmpValue.id;
			selectObj.options[indexInsert].text = tmpValue.name;
			indexInsert++;
		}
		for (var i = 0; i < values.length; i++) {
			var tmpValue = values[i];
			for (var j = 0; j < leftSelectObj.length; j++) {
				if (leftSelectObj.options[j].value == tmpValue.id) {
					leftSelectObj.remove(j);
					break;
				}
			}
		}
	} /*else {
		selectObj.length++;
		selectObj.options[0].value = -1;
		selectObj.options[0].text = 'None available';
	} */
}

hm.util.getMultiSelectValues = function(selectObj) {
	var rtns = new Array();
	var selectObjLength=selectObj.length;
	if (selectObjLength < 1) {
		return rtns;
	}
	if (selectObjLength == 1 && selectObj.options[0].value == -1) {
		return rtns;
	}
	for (var i = 0; i < selectObjLength; i++) {
		var itemTmp = {};
		itemTmp.id = selectObj.options[i].value;
		itemTmp.name = selectObj.options[i].text;
		rtns.push(itemTmp);
	}
	return rtns;
}

hm.util.displayJsonInfoNote=function(msg){
	if (msg==null || msg=='') {
		return;
	}
	document.getElementById("notes").innerHTML='<table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">' +
					'<tr> <td height="5"></td></tr>' +
					'<tr><td class="noteInfo">' + msg + '</td></tr>' +
					'<tr><td height="6"></td></tr></table>';
	hm.util.show("notes");
	delayHideNotes(4);
}

hm.util.displayJsonErrorNote=function(msg){
	if (msg==null || msg=='') {
		return;
	}
	if (this.notesTimeoutId) {
		clearTimeout(this.notesTimeoutId);
	}
	if (notesTimeoutId) {
		clearTimeout(notesTimeoutId);
	}
	document.getElementById("notes").innerHTML='<table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">' +
					'<tr> <td height="5"></td></tr>' +
					'<tr><td class="noteError">' + msg + '</td></tr>' +
					'<tr><td height="6"></td></tr></table>';
	hm.util.show("notes");
	delayHideNotes(5);
}

hm.util.displayJsonErrorNoteWithID=function(msg, divId){
	if (msg==null || msg=='') {
		return;
	}
	document.getElementById(divId).innerHTML='<table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">' +
					'<tr> <td height="5"></td></tr>' +
					'<tr><td class="noteError">' + msg + '</td></tr>' +
					'<tr><td height="6"></td></tr></table>';
	hm.util.show(divId);
	hm.util.delayHideNotes(4,divId);
}

hm.util.displayJsonInfoNoteWithID=function(msg, divId){
	if (msg==null || msg=='' || divId==null || divId=='') {
		return;
	}
	clearTimeout(this.notesTimeoutId);
	document.getElementById(divId).innerHTML='<table width="100%" border="0" cellspacing="0" cellpadding="0" class="note">' +
					'<tr> <td height="5"></td></tr>' +
					'<tr><td class="noteInfo">' + msg + '</td></tr>' +
					'<tr><td height="6"></td></tr></table>';
	hm.util.show(divId);
	hm.util.delayHideNotes(4,divId);
}

hm.util.delayHideNotes=function(seconds,divId) {
	this.notesTimeoutId = setTimeout("hm.util.hideNotes('" + divId + "')", seconds * 2000);  // seconds
}
hm.util.hideNotes=function(divId) {
	hm.util.wipeOut(divId, 800);
}

hm.util.validateEmail = function(emailStr){
	/* The following variable tells the rest of the function whether or not
	to verify that the address ends in a two-letter country or well-known
	TLD.  1 means check it, 0 means don't. */

	var checkTLD=1;

	/* The following is the list of known TLDs that an e-mail address must end with. */

	var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum|cat|asia)$/;

	/* The following pattern is used to check if the entered e-mail address
	fits the user@domain format.  It also is used to separate the username
	from the domain. */

	var emailPat=/^(.+)@(.+)$/;

	/* The following string represents the pattern for matching all special
	characters.  We don't want to allow special characters in the address.
	These characters include ( ) < > @ , ; : \ " . [ ] */

	var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";

	/* The following string represents the range of characters allowed in a
	username or domainname.  It really states which chars aren't allowed.*/

	var validChars="\[^\\s" + specialChars + "\]";

	/* The following pattern applies if the "user" is a quoted string (in
	which case, there are no rules about which characters are allowed
	and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
	is a legal e-mail address. */

	var quotedUser="(\"[^\"]*\")";

	/* The following pattern applies for domains that are IP addresses,
	rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
	e-mail address. NOTE: The square brackets are required. */

	var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;

	/* The following string represents an atom (basically a series of non-special characters.) */

	var atom=validChars + '+';

	/* The following string represents one word in the typical username.
	For example, in john.doe@somewhere.com, john and doe are words.
	Basically, a word is either an atom or quoted string. */

	var word="(" + atom + "|" + quotedUser + ")";

	// The following pattern describes the structure of the user

	var userPat=new RegExp("^" + word + "(\\." + word + ")*$");

	/* The following pattern describes the structure of a normal symbolic
	domain, as opposed to ipDomainPat, shown above. */

	var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");

	/* Finally, let's start trying to figure out if the supplied address is valid. */

	/* Begin with the coarse pattern to simply break up user@domain into
	different pieces that are easy to analyze. */

	var matchArray=emailStr.match(emailPat);

	if (matchArray==null) {

		/* Too many/few @'s or something; basically, this address doesn't
		even fit the general mould of a valid e-mail address. */

		//alert("Email address seems incorrect (check @ and .'s)");
		return false;
	}
	var user=matchArray[1];
	var domain=matchArray[2];

	// Start by checking that only basic ASCII characters are in the strings (0-127).

	for (i=0; i<user.length; i++) {
		if (user.charCodeAt(i)>127) {
			//alert("Ths username contains invalid characters.");
			return false;
		}
	}
	for (i=0; i<domain.length; i++) {
		if (domain.charCodeAt(i)>127) {
			//alert("Ths domain name contains invalid characters.");
			return false;
		}
	}

	// See if "user" is valid

	if (user.match(userPat)==null) {

		// user is not valid

		//alert("The username doesn't seem to be valid.");
		return false;
	}

	/* if the e-mail address is at an IP address (as opposed to a symbolic
	host name) make sure the IP address is valid. */

	var IPArray=domain.match(ipDomainPat);
	if (IPArray!=null)
	{
		// this is an IP address
		for (var i=1;i<=4;i++)
		{
			if (IPArray[i]>255)
			{
				//alert("Destination IP address is invalid!");
				return false;
		  	}
		}
		return true;
	}

	// Domain is symbolic name.  Check if it's valid.
	var atomPat=new RegExp("^" + atom + "$");
	var domArr=domain.split(".");
	var len=domArr.length;
	for (i=0;i<len;i++) {
		if (domArr[i].search(atomPat)==-1) {
			//alert("The domain name does not seem to be valid.");
			return false;
	   	}
	}

	/* domain name seems valid, but now make sure that it ends in a
	known top-level domain (like com, edu, gov) or a two-letter word,
	representing country (uk, nl), and that there's a hostname preceding
	the domain or country. */

	if (checkTLD && domArr[domArr.length-1].length!=2 &&
	domArr[domArr.length-1].search(knownDomsPat)==-1) {
		//alert("The address must end in a well-known domain or two letter " + "country.");
		return false;
	}

	// Make sure there's a host name preceding the domain.

	if (len<2) {
		//alert("This address is missing a hostname!");
		return false;
	}

	// If we've gotten this far, everything's valid!
	return true;
}

/*
 * format: yyyy-MM-dd hh:mm:ss 1970-01-01 00:00:00
 * Feeling 2007-10-25
*/
hm.util.compareDatetime=function(endDatetime,startDatetime){
    var datetimeS=startDatetime.split(" ");
    var datetimeE=endDatetime.split(" ");
    var result_date=hm.util.compareDate(datetimeE[0],datetimeS[0]);
    var result_time=hm.util.compareTime(datetimeE[1],datetimeS[1]);
    if(result_date=="1" || result_date=="-1")
      return result_date;
    else
      return result_time;

}

/*
 * format: yyyy-MM-dd 1970-01-01
 * Feeling 2007-10-25
 * return: "1"--endDate>startDate;"0"--endDate=startDate;"-1"--endDate<startDate;
*/
hm.util.compareDate=function(endDate,startDate){
	var arrayS=startDate.replace("-", "");
    var arrayE=endDate.replace("-", "");

    if(arrayS==arrayE)
      return "0";
    if(arrayE>arrayS)
      return "1";
    if(arrayE<arrayS)
      return "-1";
}
/*
 * format: hh:mm:ss 00:00:00
 * Feeling 2007-10-25
 * return: "1"--endTime>startTime;"0"--endTime=startTime;"-1"--endTime<startTime;
*/
hm.util.compareTime=function(endTime,startTime)
{
    var timeS=startTime.split(":");
    var timeE=endTime.split(":");
    var return_value="-2";
    return_value=hm.util.compareNumber(timeE[0],timeS[0]);
    if(return_value=="0" && timeE.length==2)
       return_value=hm.util.compareNumber(timeE[1],timeS[1]);
    if(return_value=="0" && timeE.length==3)
       return_value=hm.util.compareNumber(timeE[2],timeS[2]);

    return return_value;
}
/*
 * Feeling 2007-10-25
*/
hm.util.compareNumber=function(num1,num2)
{
   if(parseInt(num1)>parseInt(num2))
      return "1";
   else
   if(parseInt(num1)==parseInt(num2))
      return "0";
   else
   if(parseInt(num1)<parseInt(num2))
      return "-1";
   else
      "-2";
}

/*
 * Update paging information
 */
hm.util.updatePagingInfo=function(current, total) {
	var td = document.getElementById("pageNumber");
	if (td == null) {
		return false;
	}
	td.removeChild(td.firstChild);
	td.appendChild(document.createTextNode(current + " / " + total));
	return true;
}

hm.util.startSystemStatusTimer = function(url) {
	hm.util.systemStatusUrl = url;
	hm.util.systemStatusLiveCount = 0;
	hm.util.restartSystemStatusTimer();
}

hm.util.restartSystemStatusTimer = function() {
	var interval = 15;        // seconds
	var duration = hm.util.sessionTimeout * 60;  // minutes * 60
	var total = duration / interval;
	if (hm.util.systemStatusLiveCount++ < total) {
//		alert("total: " + total + ", live count: " + hm.util.systemStatusLiveCount);
		hm.util.systemStatusTimeoutId = setTimeout("hm.util.pollSystemStatusCache()", interval * 1000);  // seconds
	}
}

hm.util.clearSystemStatusTimer = function() {
	clearTimeout(hm.util.systemStatusTimeoutId);
}

hm.util.pollSystemStatusCache = function() {
	var url = hm.util.systemStatusUrl + "?operation=systemStatus&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : hm.util.updateAlarmCounts }, null);
}

hm.util.updateAlarmCount = function(id, count) {
	var td = document.getElementById(id);
	if (td == null) {
		return;
	}
	var oldCount = parseInt(td.firstChild.nodeValue);
	if (oldCount != count) {
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(count));
	}
}

hm.util.updateAlarmCounts = function(o) {
	try {
		eval("var systemStatus = " + o.responseText);
		var alarmCount = systemStatus[0];
		hm.util.updateAlarmCount("ac_cr", alarmCount.cr);
		hm.util.updateAlarmCount("ac_ma", alarmCount.ma);
		hm.util.updateAlarmCount("ac_mi", alarmCount.mi);
		hm.util.updateAlarmCount("ac_inf", alarmCount.inf);
		hm.util.updateAlarmCount("ac_cl", alarmCount.cl);
		hm.util.updateAlarmCount("sc_nh", alarmCount.nh);
	//	hm.util.updateAlarmCount("sc_ra", alarmCount.ra);
		hm.util.updateAlarmCount("ac_innet", alarmCount.innet);
		hm.util.updateAlarmCount("ac_onmap", alarmCount.onmap);
		hm.util.updateAlarmCount("ac_strong", alarmCount.strong);
		hm.util.updateAlarmCount("ac_weak", alarmCount.weak);
	} catch (e){

	}
	hm.util.restartSystemStatusTimer();
}
hm.util.checkSpace=function(value){
   if(value.length==0)
      return true;
   if(value.indexOf(' ')>-1)
      return false;
   return true;
}
hm.util.compareValue = function(maxValue,value){
   if(value.length==0 || maxValue.length==0)
      return true;
   if(parseInt(value)*10>parseInt(maxValue))
      return false;
   return true;
}
hm.util.checkMaxValue = function(e,type,maxValue,value) {
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}
	if (type=="ten") {
		if(48 <= keycode && keycode <=57 ){
		    if(maxValue.length>0 && value.length>0)
		    {
		        return hm.util.compareValue(maxValue,value);
		    }
		    else
		        return true;
		}
	}
	if (type=="hex") {
		if((48 <= keycode && keycode <=57) ||
		(65 <= keycode && keycode <=70) ||
		(97 <= keycode && keycode <=102)){
			return true;
		}
	}
	return false;
}

hm.util.replaceChildren = function(parent, child) {
	while (parent.firstChild != null) {
		parent.removeChild(parent.firstChild);
	}
	parent.appendChild(child);

}

hm.util._LIST_SELECTION_NOITEM = -1;
hm.util._LIST_SELECTION_NOSELECTION = -2;
hm.util._LIST_SELECTION_INVALIDSELECTION = -3;
hm.util._getListSelection = function(elId){
	var el = document.getElementById(elId);
	if(el.options.length == 0){//blank list
		return hm.util._LIST_SELECTION_NOITEM;
	}else {
		var index = el.selectedIndex;
		if(index < 0){//no selection
			return hm.util._LIST_SELECTION_NOSELECTION;
		}
		var value = el.options[index].value;
		if(value == undefined || value <= 0){//invalid selection
			return hm.util._LIST_SELECTION_INVALIDSELECTION;
		}
		var selArray = new Array();
		for(var i=0; i<el.options.length; i++){
			if(el.options[i].selected){
				value = el.options[i].value;
				selArray.push(value);
			}
		}
		return selArray;
	}
}

/*
 * This function is for Modify/View profile from a list box validation.
 * When isMultiple is true, will return the array of selected id
 */
hm.util.validateListSelection = function(elId, isMultiple) {
	var value = hm.util._getListSelection(elId);
	if(YAHOO.lang.isArray(value)){
		if(isMultiple){
			return value;
		}else{//validate single selection
			if(value.length != 1){//multiple selected
				warnDialog.cfg.setProperty('text', "Please select one item.");
				warnDialog.show();
				return -1;
			}else{
				return value[0];
			}
		}
	}else{
		switch(value){
			case hm.util._LIST_SELECTION_NOITEM:
				warnDialog.cfg.setProperty('text', "There is no item.");
				warnDialog.show();
			break;
			case hm.util._LIST_SELECTION_NOSELECTION:
				warnDialog.cfg.setProperty('text', "Please select an item.");
				warnDialog.show();
			break;
			case hm.util._LIST_SELECTION_INVALIDSELECTION:
				warnDialog.cfg.setProperty('text', "Please select a valid item.");
				warnDialog.show();
			break;
		}
		return -1;
	}
}

hm.util.validateOptionTransferSelection = function(optionTransfer){
	var leftValue = hm.util._getListSelection("leftOptions_" + optionTransfer);
	var rightValue = hm.util._getListSelection(optionTransfer);
	if(YAHOO.lang.isArray(leftValue)||YAHOO.lang.isArray(rightValue)){
		var total = [];
		if(YAHOO.lang.isArray(leftValue)){
			total = total.concat(leftValue);
		}
		if(YAHOO.lang.isArray(rightValue)){
			total = total.concat(rightValue);
		}
		if(total.length != 1){//multiple selected
			warnDialog.cfg.setProperty('text', "Please select one item.");
			warnDialog.show();
			return -1;
		}else{
			return total[0];
		}
	}else{
		if(leftValue == hm.util._LIST_SELECTION_NOITEM
		&& rightValue == hm.util._LIST_SELECTION_NOITEM){//no items
			warnDialog.cfg.setProperty('text', "There is no item.");
			warnDialog.show();
		}else if(leftValue == hm.util._LIST_SELECTION_NOSELECTION
		&& rightValue == hm.util._LIST_SELECTION_NOSELECTION){// no selected items
			warnDialog.cfg.setProperty('text', "Please select an item.");
			warnDialog.show();
		}else if(leftValue == hm.util._LIST_SELECTION_INVALIDSELECTION
		&& rightValue == hm.util._LIST_SELECTION_INVALIDSELECTION){// select invalid item
			warnDialog.cfg.setProperty('text', "Please select a valid item.");
			warnDialog.show();
		}else{
			warnDialog.cfg.setProperty('text', "Please select an item.");
			warnDialog.show();
		}
		return -1;
	}
}

hm.util.unselectItems = function(elId){
	var el = document.getElementById(elId);
	if(el){
		el.selectedIndex = -1;
	}
}
//password id array(pwdIds) and textfield id array(textIds) should be symmetrical

hm.util.toggleObscurePassword = function(selected,pwdIds,textIds,pwdIdsRow,textIdsRow){
	// pwdIdsRow and textIdsRow are optional, added for the problem when click the Obscure Security Key the range will change)
	if(pwdIdsRow && textIdsRow){
		if(selected){
			document.getElementById(pwdIdsRow).style.display = "";
			document.getElementById(textIdsRow).style.display = "none";
		}else{
			document.getElementById(pwdIdsRow).style.display = "none";
			document.getElementById(textIdsRow).style.display = "";
		}
	}

	for (var i = 0; i < pwdIds.length; i++)
	{
		var password = document.getElementById(pwdIds[i]);
		var password_text = document.getElementById(textIds[i]);

		if (selected){
			password.style.display = "";
			password_text.style.display = "none";
			password_text.disabled = true;
			password.disabled = false;
			password.value = password_text.value;
		}
		else
		{
			password.style.display = "none";
			password_text.style.display = "";
			password_text.disabled = false;
			password.disabled = true;

			password_text.value = password.value;
			if (password_text.type == "text") {
				password_text.autocomplete = "off";
			}
		}
	}
}


hm.util.toggleObscurePasswordList = function(checkEle,passName,textName){
	var parentNode = checkEle.parentNode;
	
	var password, password_text;
	
	var childNodes = parentNode.childNodes;
	for(var i=0; i<childNodes.length; i++){
		var attrName = childNodes[i].name;
		if(attrName == passName){
			password = childNodes[i];
		}else if(attrName == textName){
			password_text = childNodes[i];
		}
	}
	
	if(!password || !password_text){
		return;
	}
	
	if (checkEle.checked){
		password.style.display = "";
		password_text.style.display = "none";
		password_text.disabled = true;
		password.disabled = false;
		password.value = password_text.value;
	}else{
		password.style.display = "none";
		password_text.style.display = "";
		password_text.disabled = false;
		password.disabled = true;

		password_text.value = password.value;
		if (password_text.type == "text") {
			password_text.autocomplete = "off";
		}
	}
}

/*function to calculate "strength" of a password
* expects password, returns strength
*/
hm.util.checkPassowrdStrength = function (passwd) {
	//calculate strength out of length
	var intScore = 0;
	var length = passwd.length;
	if (length > 16) {
		intScore = 18;
	} else if (length > 8) {
		intScore = 12
	} else if (length > 5) {
		intScore = 6;
	} else if (length > 0) {
		intScore = 3;
	}

	 // LETTERS
	 // [verified] at least one lower case letter
	if (passwd.match(/[a-z]/)) {
		intScore = (intScore+1)
	}
	// [verified] at least one upper case letter
	if (passwd.match(/[A-Z]/)) {
		intScore = (intScore+5)
	}
	// NUMBERS
	// [verified] at least one number
	if (passwd.match(/\d+/)) {
		intScore = (intScore+5)
	}
	// [verified] at least two numbers
	if (passwd.match(/(\d.*\d)/)) {
		intScore = (intScore+2)
	}
	// [verified] at least three numbers
	if (passwd.match(/(\d.*\d.*\d)/)) {
		intScore = (intScore+3)
	}
	// SPECIAL CHAR
	// [verified] at least one special character
	if (passwd.match(/[!#$%&'()*+,-.\/:;<=>@\[\]\\^_`{|}~]/)) {
		intScore = (intScore+5)
	}
	// [verified] at least two special characters
	if (passwd.match(/([!#$%&'()*+,-.\/:;<=>@\[\]\\^_`{|}~].*[!#$%&'()*+,-.\/:;<=>@\[\]\\^_`{|}~])/)) {
		intScore = (intScore+5)
	}
	// COMBOS
	// [verified] both upper and lower case
	if (passwd.match(/[a-z]/) && passwd.match(/[A-Z]/)) {
		intScore = (intScore+2)
	}
	// [verified] both letters and numbers
	if (passwd.match(/([a-zA-Z])/) && passwd.match(/([0-9])/)) {
		intScore = (intScore+2)
	}
	// [verified] both letters and numbers and special characters
	if (passwd.match(/([a-zA-Z])/) && passwd.match(/([0-9])/) && passwd.match(/[!#$%&'()*+,-.\/:;<=>@\[\]\\^_`{|}~]/)) {
		intScore = (intScore+2)
	}
	// [Verified] Upper Letters, Lower Letters, numbers and special characters
	if (passwd.match(/[a-z]/) && passwd.match(/[A-Z]/) && passwd.match(/\d/) && passwd.match(/[!#$%&'()*+,-.\/:;<=>@\[\]\\^_`{|}~]/)){
		intScore = (intScore+2)
	}

	if (intScore < 16) {
		strVerdict = "very weak";
	} else if (intScore < 25) {
		strVerdict = "weak";
	} else if (intScore < 35) {
		strVerdict = "mediocre";
	} else if (intScore < 45) {
		strVerdict = "strong";
	} else {
		strVerdict = "stronger";
	}

	return strVerdict;
}

/**
 * Check if select valid option when the inputElement is null.
 * Check if there is the same value in select options as the value of inputElement.
 */
hm.util.hasSelectedOptionSameValue = function(selectElement, inputElement) {
	if (null != inputElement && '' != inputElement.value) {
		var selectOption = selectElement.options;
		for (var i = 0; i < selectOption.length; i++) {
			if (selectOption[i].text == inputElement.value) {
				return true;
			}
		}
	}
	return false;
}

/*
 * get CSS Style object with given selector name
 */
hm.util.getStyle = function(selector, document){
	if(!selector){
		return null;
	}
	document = document || window.document
	for(var i=0; i<document.styleSheets.length; i++){
		var rules;
		if(document.styleSheets[i].cssRules){//DOM
			rules = document.styleSheets[i].cssRules
		}else{//IE
			rules = document.styleSheets[i].rules;
		}
		for(var j=0; j<rules.length; j++){
			if(rules[j].selectorText == selector){
				return rules[j].style;
			}
		}
	}
	return null;
}
 /*
  * get Elements by class name
  */
hm.util.getElementsByClassName = function(className, document, parentEl){
	document = document || window.document
	var elements = (parentEl?document.getElementById(parentEl):document).getElementsByTagName("*");
	var objArr = new Array();
	for(var i=0; i<elements.length; i++){
		var classNames = elements[i].className;
		if(typeof classNames == "string"){
			var array = classNames.split(" ");
			for(var j=0; j<array.length; j++){
				if(array[j] == className){
					objArr.push(elements[i]);
				}
			}
		}
	}
	return objArr;
}

/*
 * Resize iframe size, avoid scroll bar, it's better to use in onload function.
 * If the content inside iframe will resize based on DOM operation, it's better
 * to set autoResize to true.
 */
 hm.util.resizeIframeSize = function(iframeId, autoResize, iframePanel){
	var iframeObj = parent.document.getElementById(iframeId);
 	function resizeIframeWindow(){
	 	if(iframeObj){
		 	iframeObj.height = document.documentElement.scrollHeight ;
			iframeObj.width = document.documentElement.scrollWidth ;
	 	}
 	}
 	// delay some moment to get correct size
	setTimeout(function(){
		resizeIframeWindow();
		if(iframePanel){
	 		iframePanel.center();
	 	}
	}, 10);
	YAHOO.util.Event.addListener(document, "unload", function(){
		iframeObj.height = 200;
		iframeObj.width = 300;
		if(iframePanel){
	 		iframePanel.center();
	 	}
	});
 	if(autoResize){
 		setInterval(resizeIframeWindow, 50);
 	}
 }

// Declaring required variables
var digits = "0123456789";

// non-digit characters which are allowed in phone numbers
var phoneNumberDelimiters = "()- ";

// characters which are allowed in international phone numbers
// (a leading + is OK)
var validWorldPhoneChars = phoneNumberDelimiters + "+*#";

// Minimum no of digits in an international phone no.
var minDigitsInIPhoneNumber = 10;

function isInteger(s)
{   var i;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function trim(s)
{   var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not a whitespace, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (c != " ") returnString += c;
    }
    return returnString;
}

function stripCharsInBag(s, bag)
{   var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

hm.util.checkInternationalPhone=function(strPhone){
		var bracket=3;
		strPhone=trim(strPhone);
		if(strPhone.indexOf("+")>1) return false;
		if(strPhone.indexOf("-")!=-1)bracket=bracket+1;
		if(strPhone.indexOf("(")!=-1 && strPhone.indexOf("(")>bracket)return false;
		var brchr=strPhone.indexOf("(");
		if(strPhone.indexOf("(")!=-1 && strPhone.charAt(brchr+2)!=")")return false;
		if(strPhone.indexOf("(")==-1 && strPhone.indexOf(")")!=-1)return false;
		s=stripCharsInBag(strPhone,validWorldPhoneChars);
		return (isInteger(s) && s.length >= minDigitsInIPhoneNumber);
}

/*
 * Compare two ip address
 * the parameter is two ip addresses
 * return start ip is not larger than end ip
 */
hm.util.compareIpAddress=function(startIp, endIp){
	if (null == startIp || null == endIp || startIp.length == 0 || endIp.length == 0) {
		return false;
	}
	var startIpValue = hm.util.trim(startIp).split(".");
	var endIpValue = hm.util.trim(endIp).split(".");
	var ipCorrect = false;
	if (parseInt(startIpValue[0]) < parseInt(endIpValue[0])) {
		ipCorrect = true;
	} else if (parseInt(startIpValue[0]) == parseInt(endIpValue[0])){
		if (parseInt(startIpValue[1]) < parseInt(endIpValue[1])) {
			ipCorrect = true;
		} else if (parseInt(startIpValue[1]) == parseInt(endIpValue[1])) {
			if (parseInt(startIpValue[2]) < parseInt(endIpValue[2])) {
				ipCorrect = true;
			} else if (parseInt(startIpValue[2]) == parseInt(endIpValue[2])){
				if (parseInt(startIpValue[3]) <= parseInt(endIpValue[3])) {
					ipCorrect = true;
				}
			}
		}
	}
	return ipCorrect;
}

////////////// This section is used in customized tag CheckListTag //////////////////
var selectedRow = null;

hm.util.selectSpecialRow=function(row, isMultiple, isSpecialSelected, containerId) {
	var tableId = "dataTable";
	if(containerId) {
		tableId = containerId;
	}
	var table = document.getElementById(tableId);
	var rows = table.getElementsByTagName("tr");

	for(var i=0; i<rows.length; i++) {
		var cell1 = rows[i].cells[0];
		var cell2 = rows[i].cells[1];
		var cell3 = rows[i].cells[2];
		var cell4 = rows[i].cells[3]; // menu item

		var specialMark;
		var isSpecialRow = false;
		var specialArray = cell3.getElementsByTagName("input");
		for(var index=0; index<specialArray.length; index++) {
			if(specialArray[index].name == "specialRow") {
				specialMark = specialArray[index];
				isSpecialRow = true;
				break;
			}
		}
		if (isSpecialRow != isSpecialSelected) {
			continue;
		}

		//for the specific SPAN element
		var spanElement;
		var spanElementClone;
		var spanArray = cell2.getElementsByTagName("span");
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'editText') {
				spanElement = spanArray[index];
				break;
			}
		}
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'cloneText') {
				spanElementClone = spanArray[index];
				break;
			}
		}

		if(cell2 == row) {
			/*
			 * change color
			 */
			if(cell1.firstChild.checked) { // was seleted
				// unselect it
				cell2.style.backgroundColor = "#f9f9f7";
				if(spanElement) {
					spanElement.style.display = "none";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "none";
				}
			} else { // was unselected
				// select it
				cell2.style.backgroundColor = "#ffc20e";
				if(spanElement) {
					spanElement.style.display = "inline-block";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "inline-block";
				}
			}
			/*
			 * check checkbox
			 */
			cell1.firstChild.checked = !cell1.firstChild.checked;
		} else {
			if(!isMultiple){
				/*
				 * change color
				 */
				cell2.style.backgroundColor = "#f9f9f7";

				/*
				 * check checkbox
				 */
				cell1.firstChild.checked = false;

				if(spanElement) {
					spanElement.style.display = "none";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "none";
				}
			}
		}
		if(cell4) {
			var divs = cell4.getElementsByTagName("div");
			if(divs.length > 0 && divs[0].className == 'dialogListMenu') {
				divs[0].style.display = "none";
			}
		}
	}
}
hm.util.selectRowAndGrayOutOthers=function(row, containerId,portId){
	var tableId = "dataTable";
	if(containerId) {
		tableId = containerId;
	}
	var table = document.getElementById(tableId);
	var rows = table.getElementsByTagName("tr");

	for(var i=0; i<rows.length; i++) {
		var cell1 = rows[i].cells[0];
		var cell2 = rows[i].cells[1];
		var cell3 = rows[i].cells[2]; // menu item

		//for the specific SPAN element
		var spanElement;
		var spanElementClone;
		var spanArray = cell2.getElementsByTagName("span");
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'editText') {
				spanElement = spanArray[index];
				break;
			}
		}
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'cloneText') {
				spanElementClone = spanArray[index];
				break;
			}
		}

		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'checkedIcon') {
				spanElementClone = spanArray[index];
				break;
			}
		}
		if(cell2 == row) {
			/*
			 * change color
			 */
			if(cell1.firstChild.checked) { // was seleted
				// unselect it
				
				//unselsect the gray item
				var selectDeviceModels =cell2.getAttribute('devicemodels').split("-");
				for(var j=0; j<rows.length; j++) {
					var containFlag = 0;
					var isInothersSelectItem =0;
					var otherItemTD = rows[j].cells[1];
					if(otherItemTD != row){
						if(otherItemTD.getAttribute('devicetype') == cell2.getAttribute('devicetype')){
							var otherItemDeviceModels = otherItemTD.getAttribute('devicemodels').split("-");
							if(otherItemDeviceModels.length > selectDeviceModels.length){
								//get the other selectitems,compare it!
								
								for(var index=0; index<rows.length; index++){
									var otherSelectItemTD = rows[index].cells[1];
									var otherSelectItemDeviceModels = otherSelectItemTD.getAttribute('devicemodels').split("-");
									if(otherSelectItemTD != row && otherSelectItemTD.previousSibling.firstChild.checked
												&& otherSelectItemTD.getAttribute('devicetype') == otherItemTD.getAttribute('devicetype')){
										for(var number=0;number<otherItemDeviceModels.length;number++){
											if(otherSelectItemDeviceModels.contains(otherItemDeviceModels[number])){
												++isInothersSelectItem;
												break;
											}
										}
									}
									for(var number=0;number<otherItemDeviceModels.length;number++){
										if(selectDeviceModels.contains(otherItemDeviceModels[number])){
											++containFlag;
											break;
										}
									}
								}
							}else{
								//the selectItem contains all device models of grayItem when type is same
								for(var index=0;index<otherItemDeviceModels.length;index++){
									if(selectDeviceModels.contains(otherItemDeviceModels[index])){
										++containFlag;
										break;
									}
								}
							}
							if(isInothersSelectItem == 0 && containFlag > 0){
								//change the gray to white
								otherItemTD.style.backgroundColor="#f9f9f7";
								otherItemTD.style.color="";
								var spanArray = otherItemTD.getElementsByTagName("span");
								for(var index=0;index<spanArray.length; index++){
									if(spanArray[index].className == 'word-wrap1'){
										spanArray[index].className ='word-wrap';
									}
								}
							}
						}
					}
				}
				//unselsect the select item
				cell2.style.backgroundColor = "#f9f9f7";
				if(spanElement) {
					spanElement.style.display = "none";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "none";
				}
			} else { // was unselected
				// select it
				var selectDeviceModels =cell2.getAttribute('devicemodels').split("-");
				for(var j=0; j<rows.length; j++) {
					var flag = 0;
					var otherItemTD = rows[j].cells[1];
					if(otherItemTD != row){
						if(otherItemTD.getAttribute('devicetype') == cell2.getAttribute('devicetype')){
							var otherItemModels = otherItemTD.getAttribute('devicemodels').split("-");
							for(var index=0;index<otherItemModels.length;index++){
								if(selectDeviceModels.contains(otherItemModels[index])){
									++flag;
									break;
								}
							}
							if(flag > 0){
								//change the white to gray
								otherItemTD.style.backgroundColor="#B9C1C1";
								otherItemTD.style.color="white";
								rows[j].cells[0].firstChild.checked = false;
								var spanArray = otherItemTD.getElementsByTagName("span");
								for(var index=0;index<spanArray.length; index++){
									if(spanArray[index].className == 'word-wrap' || spanArray[index].className == 'word-wrap2' ){											if(otherItemTD.getAttribute('devicemodels') == cell2.getAttribute('devicemodels')){
										spanArray[index].className ='word-wrap2';
									}else{
										spanArray[index].className ='word-wrap1';
									}
								}
										//==================================================
										if(spanArray[index].className == 'checkedIcon'){
											spanArray[index].style.display="none";
										}
										if(spanArray[index].className == 'editText'){
											spanArray[index].style.display="none";
										}
										//==================================================
									}
								}
						}
					}
				}
					
					cell2.style.backgroundColor = "#ffc20e";
					//===========================
					cell2.style.color = "black";
					//===========================
					if(spanElement) {
						spanElement.style.display = "inline-block";
					}
					if(spanElementClone) {
						spanElementClone.style.display = "inline-block";
					}
				}
			/*
			 * check checkbox
			 */
			 cell1.firstChild.checked = !cell1.firstChild.checked;
		}
		if(cell3) {
			var divs = cell3.getElementsByTagName("div");
			if(divs.length > 0 && divs[0].className == 'dialogListMenu') {
				divs[0].style.display = "none";
			}
		}
	}

}
hm.util.selectRow=function(row, isMultiple, containerId) {
	var tableId = "dataTable";
	if(containerId) {
		tableId = containerId;
	}
	var table = document.getElementById(tableId);
	var rows = table.getElementsByTagName("tr");

	if(!isMultiple) {
		selectedRow = row;
	}

	for(var i=0; i<rows.length; i++) {
		var cell1 = rows[i].cells[0];
		var cell2 = rows[i].cells[1];
		var cell3 = rows[i].cells[2]; // menu item

		//for the specific SPAN element
		var spanElement;
		var spanElementClone;
		var spanArray = cell2.getElementsByTagName("span");
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'editText') {
				spanElement = spanArray[index];
				break;
			}
		}
		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'cloneText') {
				spanElementClone = spanArray[index];
				break;
			}
		}

		for(var index=0; index<spanArray.length; index++) {
			if(spanArray[index].className == 'checkedIcon') {
				spanElementClone = spanArray[index];
				break;
			}
		}
		if(cell2 == row) {
			/*
			 * change color
			 */
			if(cell1.firstChild.checked) { // was seleted
				// unselect it
				cell2.style.backgroundColor = "#f9f9f7";
				if(spanElement) {
					spanElement.style.display = "none";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "none";
				}
			} else { // was unselected
				// select it
				cell2.style.backgroundColor = "#ffc20e";
				if(spanElement) {
					spanElement.style.display = "inline-block";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "inline-block";
				}
			}
			/*
			 * check checkbox
			 */
			cell1.firstChild.checked = !cell1.firstChild.checked;
		} else {
			if(!isMultiple){
				/*
				 * change color
				 */
				cell2.style.backgroundColor = "#f9f9f7";

				/*
				 * check checkbox
				 */
				cell1.firstChild.checked = false;

				if(spanElement) {
					spanElement.style.display = "none";
				}
				if(spanElementClone) {
					spanElementClone.style.display = "none";
				}
			}
		}
		if(cell3) {
			var divs = cell3.getElementsByTagName("div");
			if(divs.length > 0 && divs[0].className == 'dialogListMenu') {
				divs[0].style.display = "none";
			}
		}
	}
}

hm.util.overRow=function(row, isMultiple, event, containerId) {
	var tableId = "dataTable";
	if(containerId) {
		tableId = containerId;
	}
	var table = document.getElementById(tableId);
	var rows = table.getElementsByTagName("tr");
	var cell1 = null;

	for(var i=0; i<rows.length; i++) {
		var cell2 = rows[i].cells[1];

		if(cell2 == row) {
			cell1 = rows[i].cells[0];
			break;
		}
	}

	if(cell1 && cell1.firstChild.checked) {
		hm.util.stopBubble(event);
		return;
	} else {
		row.style.backgroundColor = "#f3f3f0";
	}
	hm.util.stopBubble(event);
}

hm.util.outRow=function(row, isMultiple, event, containerId) {
	var tableId = "dataTable";
	if(containerId) {
		tableId = containerId;
	}
	var table = document.getElementById(tableId);
	var rows = table.getElementsByTagName("tr");
	var cell1 = null;

	for(var i=0; i<rows.length; i++) {
		var cell2 = rows[i].cells[1];

		if(cell2 == row) {
			cell1 = rows[i].cells[0];
			break;
		}
	}

	if(cell1 && cell1.firstChild.checked) {
		hm.util.stopBubble(event);
		return ;
	} else {
		row.style.backgroundColor = "#f9f9f7";
	}
	hm.util.stopBubble(event);
}

////////////// Start for Menu for selection dialog /////////////////////
hm.util._openedMenus = new Array();
hm.util.listMenu=function(menuId, containerId, event) {
	// register the onclick event for document
	if(document.onclick == undefined) {
		document.onclick = hm.util.clearMenus;
	}
	// close previous menu
	while(this._openedMenus.length > 0) {
		var lastEl = this._openedMenus.pop();
		if(lastEl && lastEl.id != menuId) {
			lastEl.style.display = "none";
		}
	}
	var dialogEl = document.getElementById(containerId);
	var element = document.getElementById(menuId);
	if(element && dialogEl) {
		if(element.style.display == "none") {

			element.style.top = "";

			element.style.display = "block";

			// only change the positive right position when there is scroll bar
			if(dialogEl.scrollHeight > dialogEl.offsetHeight) {
				var rightValue = hm.util.getStyleProperty(element, "right");
				var isNegativeNum = false;
				if(rightValue) {
					isNegativeNum = new RegExp('^-[0-9]*px$', 'g').test(rightValue);
				}
				if(isNegativeNum) {
					// change the sepcific negative position
					element.style.right = "-10px";
				} else {
					// change the normal positive position
					element.style.right = "35px";
				}
			}
			// calculte the position after display if scroll some height
			// handle IE8+ after the meta element "X-UA-Compatible" set as "IE=9"
			// handle Maxton like browser which has very wired rendering model
			if(!isNaN(dialogEl.scrollTop)
					&& (YAHOO.env.ua.opera || YAHOO.env.ua.webkit || YAHOO.env.ua.ie > 7
							|| (YAHOO.env.ua.ie == 7 && dialogEl.scrollWidth == dialogEl.clientWidth))) {
				element.style.top = (element.offsetTop - dialogEl.scrollTop) + "px";
			}

			this._openedMenus.push(element);
		} else {
			element.style.display = "none";
		}
	}
	hm.util.stopBubble(event);
}
hm.util.mouseoverMenu=function(menu, event) {
	menu.className = 'hover';
	hm.util.stopBubble(event);
}
hm.util.mouseoutMenu=function(menu, event) {
	menu.className = '';
	hm.util.stopBubble(event);
}
hm.util.mousePosition=function(ev) {
	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop  - document.body.clientTop
	};
}
hm.util.getOpenedMenus=function() { return this._openedMenus; }
hm.util.clearMenus=function(event) {
	if (!event)
		event = window.event;
	var tg = (window.event) ? event.srcElement : event.target;
	//console.debug("target, nodeName="+tg.nodeName+", id="+tg.id+", class="+tg.className);

	var menus = hm.util.getOpenedMenus();
	// close previous menu
	while(menus && menus.length > 0) {
		var lastEl = menus.pop();
		if(lastEl) {
			lastEl.style.display = "none";
		}
	}
}
////////////// End for Menu for selection dialog /////////////////////

hm.util.getSelectedCheckItems = function(elementName) {
	var checks = document.getElementsByName(elementName);

	if(checks == null || checks == undefined || checks.length == 0) {
		return hm.util._LIST_SELECTION_NOITEM;
	}

	var selected = [];

	for(var i=0; i<checks.length; i++) {
		if(checks[i].checked) {
			selected.push(checks[i].value);
		}
	}

	if(selected.length == 0) {
		return hm.util._LIST_SELECTION_NOSELECTION;
	}

	return selected;
}
///////////// End of CheckListTag ///////////////////////////////////////////////////

// iframe
hm.util.getIFrameDOMById = function(id){
    if (document.all){//IE
        if (document.frames[id])
            return  doc = document.frames[id];
    }else{//Firefox
        if (document.getElementById(id))
            return  doc = document.getElementById(id).contentWindow;
    }
}

// drag table
hm.util.initDragTableElement = function(myDataTable) {
	var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        DDM = YAHOO.util.DragDropMgr,
        myDTDTargets = {},
        onRowSelect = function(ev) {
        	var par = myDataTable.getTrEl(Event.getTarget(ev)),
                srcData,
                srcIndex,
                tmpIndex = null,
                ddRow = new YAHOO.util.DDProxy(par.id);

                ddRow.handleMouseDown(ev.event);


                /**
                * Once we start dragging a row, we make the proxyEl look like the src Element. We get also cache all the data related to the
                * @return void
                * @static
                * @method startDrag
                */
                ddRow.startDrag = function () {
                    proxyEl  = this.getDragEl();
                    srcEl = this.getEl();
                    srcData = myDataTable.getRecord(srcEl).getData();
                    srcIndex = srcEl.sectionRowIndex;
                    // Make the proxy look like the source element
                    Dom.setStyle(srcEl, "visibility", "hidden");
                    proxyEl.innerHTML = "<table><tbody>"+srcEl.innerHTML+"</tbody></table>";
                };

                /**
                * Once we end dragging a row, we swap the proxy with the real element.
                * @param x : The x Coordinate
                * @param y : The y Coordinate
                * @return void
                * @static
                * @method endDrag
                */
                ddRow.endDrag = function(x,y) {
                    Dom.setStyle(proxyEl, "visibility", "hidden");
                    Dom.setStyle(srcEl, "visibility", "");
                };

                /**
                * This is the function that does the trick of swapping one row with another.
                * @param e : The drag event
                * @param id : The id of the row being dragged
                * @return void
                * @static
                * @method onDragOver
                */
                ddRow.onDragOver = function(e, id) {
                    // Reorder rows as user drags

                    var destEl = Dom.get(id),
                        destIndex = destEl.sectionRowIndex;


                    if (destEl.nodeName.toLowerCase() === "tr") {
                        if(tmpIndex !==null) {
                            myDataTable.deleteRow(tmpIndex);
                        }
                        else {
                            myDataTable.deleteRow(srcIndex);
                        }

                    myDataTable.addRow(srcData, destIndex);
                    tmpIndex = destIndex;


                    DDM.refreshCache();
                    }
                };
            };

        	myDataTable.subscribe('cellMousedownEvent', onRowSelect);

	         //////////////////////////////////////////////////////////////////////////////
	         // Create DDTarget instances when DataTable is initialized
	         //////////////////////////////////////////////////////////////////////////////
	         myDataTable.subscribe("initEvent", function() {

	             var i, id,
	                 allRows = this.getTbodyEl().rows;

	             for(i=0; i<allRows.length; i++) {
	                 id = allRows[i].id;
	                 // Clean up any existing Drag instances
	                 if (myDTDTargets[id]) {
	                      myDTDTargets[id].unreg();
	                      delete myDTDTargets[id];
	                 }
	                 // Create a Drag instance for each row
	                 myDTDTargets[id] = new YAHOO.util.DDTarget(id);
	             }
	         });

	         //////////////////////////////////////////////////////////////////////////////
	         // Create DDTarget instances when new row is added
	         //////////////////////////////////////////////////////////////////////////////
	         myDataTable.subscribe("rowAddEvent",function(e){
	             var id = e.record.getId();

	             myDTDTargets[id] = new YAHOO.util.DDTarget(id);
	         });

	         myDataTable.subscribe("rowClickEvent", myDataTable.onEventSelectRow);

	         // Set up editing flow for google browser
	         if (navigator.appVersion.indexOf('Chrome') > -1) {
	         	var highlightEditableCell = function(oArgs) {
		             var elCell = oArgs.target;
		             if(YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable")) {
		                 this.highlightCell(elCell);
		             }
		         };
	         	myDataTable.subscribe("cellMouseoverEvent", highlightEditableCell);
         		myDataTable.subscribe("cellMouseoutEvent", myDataTable.onEventUnhighlightCell);
        		myDataTable.subscribe("cellClickEvent", myDataTable.onEventShowCellEditor);
	         }
}

// welcome and global setting password check
hm.util.validateOptionNewPasswordLength = function(passwordElement, confirmElement, passTitle, confirmTitle, minLength, noteTitle){
	var message = hm.util.validatePassword(passwordElement.value, passTitle);
	if (message != null) {
		hm.util.reportFieldError(passwordElement, message);
    	passwordElement.focus();
    	return false;
	}
	message = hm.util.validatePassword(confirmElement.value, confirmTitle);
	if (message != null) {
		hm.util.reportFieldError(confirmElement, message);
    	confirmElement.focus();
    	return false;
	}

	noteTitle = noteTitle.replace("(", "").replace(")", "");
	if (passwordElement.value.length < minLength) {
	    hm.util.reportFieldError(passwordElement, passTitle+' must be '+noteTitle+'.');
	    passwordElement.focus();
	    return false;
	}

	if (confirmElement.value.length < minLength) {
	    hm.util.reportFieldError(confirmElement, confirmTitle+' must be '+noteTitle+'.');
	    confirmElement.focus();
	    return false;
	}

	if (passwordElement.value != confirmElement.value) {
	 	hm.util.reportFieldError(confirmElement, confirmTitle+' is different from '+(passTitle=='The password'? 'the password' : passTitle)+'.');
		confirmElement.focus();
		return false;
	}
	return true;
}

/**
 * Enhance the security of user password
 */
hm.util.validateUserNewPasswordFormat = function(passwordElement, confirmElement, passTitle, confirmTitle, minLength, noteTitle, userName){
	if (hm.util.validateOptionNewPasswordLength(passwordElement, confirmElement, "The password", "The confirm password", minLength, noteTitle)) {
		if (null == passwordElement.value.match(/[A-Z]/g) || null == passwordElement.value.match(/[0-9]/g)) {
			hm.util.reportFieldError(passwordElement, "The password must include at least one number and one uppercase character.");
		    passwordElement.focus();
		    return false;
		}
		if (passwordElement.value == userName) {
			hm.util.reportFieldError(passwordElement, "The password cannot be the same as the user name.");
		    passwordElement.focus();
		    return false;
		}
	} else {
		return false;
	}
	return true;
}

hm.util.isAFunction = function(funcArg) {
	if (typeof funcArg == 'function') {
		return true;
	}
	return false;
}

hm.util.getStyleProperty = function(node, property) {
    if (node.style[property]) {
        return node.style[property];
    } else if (node.currentStyle) {
        return node.currentStyle[property];
    } else if (document.defaultView && document.defaultView.getComputedStyle) {
        var style = document.defaultView.getComputedStyle(node, null);
        return style.getPropertyValue(property);
    }
    return null;
}

hm.util.toggleElementDisplay = function(elId) {
	var el = document.getElementById(elId);
	if (el) {
		if (el.style.display == 'none') {
			el.style.display = '';
		} else {
			el.style.display = 'none';
		}
	}
}

var udConfirmDialog;
function userDefinedConfirmDialog(text, myButtons, titile) {
    if(null == udConfirmDialog) {
        udConfirmDialog =
         new YAHOO.widget.SimpleDialog("udConfirmDialog",
                  { width: "350px",
                    fixedcenter: true,
                    visible: false,
                    draggable: true,
                    modal:true,
                    close: false,
                    icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                    constraintoviewport: true
                  } );
    }
    if(titile) {
        udConfirmDialog.setHeader(titile);
    }
    udConfirmDialog.cfg.setProperty("text", text);
    udConfirmDialog.cfg.queueProperty("buttons", myButtons);
    udConfirmDialog.render(document.body);
    return udConfirmDialog;
}

hm.util.upperCaseInitialLetter = function(words) {
	if(words && typeof words === 'string') {
		return words.replace(/\b\w+\b/g, function(word){
			return word.substring(0, 1).toUpperCase() + word.substring(1);
		});
	}
	return words;
}

hm.util.addMouseScrollAction = function(element, fn) {
	if(element && typeof element === 'object') {
		if (element.addEventListener) {
			// all browsers except IE before version 9, bubble
			element.addEventListener ("scroll", fn, false);
		} else {
	        if (element.attachEvent) {
	        	// IE before version 9
	        	element.attachEvent ("onscroll", fn);
	        }
		}
	}
}

hm.util.encodeURI = function(uri) {
	return encodeURI(uri);
}

hm.util.encodeForURIArg = function(uri) {
	return encodeURIComponent(uri);
}

hm.util.addYUICalendarButton = function(options) {
	var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

	var btnClickEvent = function() {
		oCalendarMenu.setBody("&#32;");
		oCalendarMenu.body.id = options.bodyId;
		oCalendarMenu.render(this.get("container"));
		oCalendarMenu.align();

		var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id);
		oCalendar.render();

		oCalendar.changePageEvent.subscribe(function () {
		    window.setTimeout(function () {
		        oCalendarMenu.show();
		    }, 0);
		});

		oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
		    var aDate;
		    if (p_aArgs) {
		        aDate = p_aArgs[0][0];
		        options.afterSelect(aDate);
		    }
		    oCalendarMenu.hide();

		});
		this.unsubscribe("click", onButtonClick);
	};

    var timeButton = new YAHOO.widget.Button({
                                        type: "menu",
                                        id: options.id,
                                        label: "",
                                        menu: oCalendarMenu,
                                        container: options.container});

    timeButton.on("click", btnClickEvent);
}

hm.util.createFormArrayEls = function(_container, name, data, blnEmptyContainer) {
	if (data && _container && name && data.length > 0) {
		var hiddenEl;
		if (blnEmptyContainer) {
			_container.innerHTML = "";
		}
		for (var i = 0; i < data.length; i++) {
			hiddenEl = document.createElement('input');
			hiddenEl.type = "hidden";
			hiddenEl.name = name;
			hiddenEl.value = data[i];
			_container.appendChild(hiddenEl);
		}
	}
}

hm.util.CommonDelayHelper = function(func, options) {
	options = options || {};
	var timeId;
	var delayTime = options.delayTime || 1000;
	var lastChkTime = 0;
	var blnSleepOrRunOnce = false;

	var executeCheck = function() {
		return true;
	};
	if (options.check) {
		executeCheck = options.check;
	}

	function delayToNext() {
		if (timeId) {
			clearTimeout(timeId);
		}
		timeId = setTimeout(function(){func();}, delayTime);
	}

	function runOperation(callback) {
		if (blnSleepOrRunOnce) {
			blnSleepOrRunOnce = false;
			return false;
		}
		var curTime = new Date().getTime();
		if (!lastChkTime || ((curTime - lastChkTime) < delayTime) || !executeCheck()) {
			lastChkTime = curTime;
			delayToNext();
			return false;
		}
		if (callback) {
			callback();
		}
		return this;
	}

	function gotoSleep(blnWaitOnce) {
		if (blnWaitOnce) {
			runOperation(func);
			blnSleepOrRunOnce = true;
		} else {
			blnSleepOrRunOnce = false;
			clearCurDelayOperation();
		}
	}

	function clearCurDelayOperation() {
		if (timeId) {
			clearTimeout(timeId);
		}
		lastChkTime = 0;
		blnSleepOrRunOnce = false;
		return this;
	}

	return {
		run: runOperation,
		sleep: gotoSleep,
		clear: clearCurDelayOperation
	};
}

hm.util.debug = function(msg) {
	if(window.console && console.debug) {
		if(typeof msg == 'string') {
			console.debug(msg);
		} else {
			console.dir(msg);
		}
	}
}

//Constants values
// allow all the print chars
hm.util.KEYCODES_SSID = [32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 2 chars : 39' 64@ 92\
hm.util.KEYCODES_PSK_USERNAME = [32,33,34,35,36,37,38,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,63,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 3 chars : 32space 34" and 63?
hm.util.KEYCODES_PWD = [33,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 5 chars : 32space 34" 47/ 58: and 63?
hm.util.KEYCODES_USERNAME = [33,35,36,37,38,39,40,41,42,43,44,45,46,48,49,50,
						51,52,53,54,55,56,57,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 3 chars : 32space 34" and 63?
hm.util.KEYCODES_STRING = [33,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
//don't allow the following chars : 32space 34" 35# 37% 38& 47/ 58: and 63?
hm.util.KEYCODES_SPECIALNAME = [33,36,39,40,41,42,43,44,45,46,48,49,50,
						51,52,53,54,55,56,57,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 2 chars : 34" and 63?
hm.util.KEYCODES_STRINGWithBlank = [32,33,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];
// don't allow 16 chars : 32space 34" 35# 36$ 37% 38& 39' 40( 41) 42* 47/ 59; 60< 62> 63? 92\ 94^ 96~ 124| 126~
hm.util.KEYCODES_DirectoryName = [33,43,44,45,46,48,49,50,
						51,52,53,54,55,56,57,58,61,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,93,95,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,125];
//don't allow 2 chars : 34" 63? 92\
hm.util.KEYCODES_DOMAINUSERNAME = [32,33,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];

//don't allow 2 chars : 34" and 63? and alow 10\n 13\r
hm.util.KEYCODES_CLIBLOB = [10,13,32,33,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
						51,52,53,54,55,56,57,58,59,60,61,62,64,65,66,67,68,69,
						70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
						89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
						106,107,108,109,110,111,112,113,114,115,116,117,118,119,
						120,121,122,123,124,125,126];


hm.util.RADIUS_LIBRARY_SIP_BL = ["", "Y", "N"];

hm.util.RADIUS_LIBRARY_SIP_BH = ["","USD","AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","BHD","BIF",
		"BMD","BND","BOB","BRL","BSD","BTN","BWP","BYR","BZD","CAD","CDF","CHF","CLP","CNY","COP","CRC","CUP","CVE","CYP","CZK","DJF",
		"DKK","DOP","DZD","EEK","EGP","ERN","ETB","EUR","FJD","FKP","GBP","GEL","GGP","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","HNL",
		"HRK","HTG","HUF","IDR","ILS","IMP","INR","IQD","IRR","ISK","JEP","JMD","JOD","JPY","KES","KGS","KHR","KMF","KPW","KRW","KWD",
		"KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MTL","MUR","MVR",
		"MWK","MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON","RSD",
		"RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SHP","SLL","SOS","SPL","SRD","STD","SVC","SYP","SZL","THB","TJS","TMM","TND",
		"TOP","TRY","TTD","TVD","TWD","TZS","UAH","UGX","UYU","UZS","VEB","VEF","VND","VUV","WST","XAF","XAG","XAU","XCD","XDR","XOF",
		"XPD","XPF","XPT","YER","ZAR","ZMK","ZWD"];

hm.util.DEFAULT_OS_OBJECTS_VERSION_DES = ["Android", "iPad", "iPhone", "Fedora 13", "Mac OS", "Windows Mobile", "Windows XP",
        "Windows 2003", "Windows Vista; Windows 2008","Windows 7"];
