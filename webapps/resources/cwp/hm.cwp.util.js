
function addPlaceHolders(){
	if(checkPlaceHolders()){
	     initPlaceHolders();
	}
}

function checkPlaceHolders(){
		if('placeholder' in document.createElement('input')){
			customRequiredValidationMessage();
			return false;
		} else {
			return true;
		}
}


function initPlaceHolders(){
	function target (e){
		var e=e||window.event;
		return e.target||e.srcElement;
	};
	function _getEmptyHintEl(el){
		var hintEl=el.hintEl;
		return hintEl && g(hintEl);
	};
	function blurFn(e){
		var el=target(e);
		if(!el || el.tagName !='INPUT' && el.tagName !='TEXTAREA') return;
		var	emptyHintEl=el.__emptyHintEl;
		if(emptyHintEl){
			if(el.value) 
				emptyHintEl.style.display='none';
			else 
				emptyHintEl.style.display='';
		
		}
	};
	function focusFn(e){
		var el=target(e);
		if(!el || el.tagName !='INPUT' && el.tagName !='TEXTAREA') return;
		var emptyHintEl=el.__emptyHintEl;
		if(emptyHintEl){
			emptyHintEl.style.display='none';
		}
	};
	if(document.addEventListener){
		document.addEventListener('focus',focusFn, true);
		document.addEventListener('blur', blurFn, true);
	}
	else{
		document.attachEvent('onfocusin',focusFn);
		document.attachEvent('onfocusout',blurFn);
	}

	var elss=[document.getElementsByTagName('input'),document.getElementsByTagName('textarea')];
	for(var n=0;n<2;n++){
		var els=elss[n];
		for(var i =0;i<els.length;i++){
			var el=els[i];
			var placeholder=el.getAttribute('placeholder'),
				emptyHintEl=el.__emptyHintEl;
			if(placeholder && !emptyHintEl){
				emptyHintEl=document.createElement('span');
				emptyHintEl.innerHTML=placeholder;
				emptyHintEl.className='placeholder';
				emptyHintEl.onclick=function (el){return function(){try{el.focus();}catch(ex){}}}(el);
				if(el.value) emptyHintEl.style.display='none';
				el.parentNode.insertBefore(emptyHintEl,el);
				el.__emptyHintEl=emptyHintEl;
			}
		}
	}
}

function customRequiredValidationMessage() {
	var elss=[document.getElementsByTagName('input'),document.getElementsByTagName('textarea')];
	for(var n=0;n<2;n++){
		var els=elss[n];
		for(var i =0;i<els.length;i++){
			var el=els[i];
			if(null != el.getAttribute('required')
					|| el.getAttribute('name') === 'countryCode') {
			    el.oninvalid = function(e) {
			    	var targetEl = e.target, label = (targetEl.name === 'countryCode' ? getCountryCodeErrorMeesgage() : getErrorMessage(targetEl.id));
			    	targetEl.setCustomValidity("");
			    	if (!targetEl.validity.valid) {
			    		targetEl.setCustomValidity(label);
			    	}
			    };
			    el.oninput = function(e) {
			    	e.target.setCustomValidity("");
			    };
			}
		}
	}
}
function getLabelText(id) {
	var msgMapping = {
			'field12': 'your user name',
			'field22' : 'your password',
			'fieldFirstNameMark' : 'your first name',
			'fieldLastNameMark' : 'your last name',
			'fieldLastNameMark' : 'your last name',
			'fieldEmailMark' : 'an email adress',
			'fieldPhoneMark' : 'a phone number',
			'fieldVisitingMark' : 'visiting',
			'fieldCommentMark' : 'your comment',
			'fieldRepresentingMark' : 'representing'
			};
	return msgMapping[id];
}
function getCountryCodeErrorMeesgage() {
	return "Please enter a vaild country code";
}
function getErrorMessage(elementId) {
	var element = document.getElementById(elementId);
	if(element) {
		var label = getLabelText(elementId);
		if(null == label) {
			label = element.getAttribute('placeholder');
		}
		return "Please enter " + label;
	}
	return null;
}

var validateSubmitExistUserLogin = function(formIndex){

	var inputusername = $("#field12");
	var inputpassword = $("#field22");
	if(inputusername.val() == null || inputusername.val() == ''){
		var message = getErrorMessage(inputusername.id);
		reportFieldError(inputusername, message);
		return false;
	}
	if(inputpassword.val() == null || inputpassword.val() == ''){
		var message = getErrorMessage(inputpassword.id);
		reportFieldError(inputpassword, message);
		return false;
	}
}

var emailReg = function() {
	return new RegExp(/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i);
}
var printErr = function($element, message) {
	var fieldMark = $element.get(0);
	reportFieldError(fieldMark, message);
	$element.focus();
}
var validateSubmit = function(formIndex) {
	var elss = [ $("input", formIndex), $("textarea", formIndex) ];
	for ( var n = 0; n < 2; n++) {
		var els = elss[n];
		for ( var i = 0; i < els.length; i++) {
			var el = els[i];
			var required = el.getAttribute('required');
			if (required != null) {
				if (el.value == null || el.value == '') {
					var message = getErrorMessage(el.id);
					reportFieldError(el, message);
					return false;
				}
			}

		}
	}

	if ($("#fieldEmailMark") != null && $("#fieldEmailMark").val() != null
			&& $("#fieldEmailMark").val() != '') {
		var re = emailReg();
		if (!re.test($("#fieldEmailMark").val())) {
			printErr($("#fieldEmailMark"), 'Please enter a valid email address');
			return false;
		}
	}
	
	var phoneText, country = $('input[name="countryCode"]');
	if(country.length) {
		if($('#fieldPhoneMark').length) {
			phoneText = $('#fieldPhoneMark'); 
		} else if($('#opt_fieldPhoneMark').length) {
			phoneText = $('#opt_fieldPhoneMark'); 
		}
		if(phoneText) {
			if($.trim(phoneText.val()) != '') {
				if(($.trim(country.val()) == '' || $.trim(country.val()) == '+' )) {
					reportFieldError(phoneText[0], 'Please select or enter a country calling code');
					country.focus();
					return false;
				} else {
					var phoneRe = new RegExp(/^[a-z0-9]+[a-z0-9-\s]*$/i);
					if(!phoneRe.test(phoneText.val())) {
						reportFieldError(phoneText[0], 'Please input a vaild phone number');
						phoneText.focus();
						return false;
					}
				}
			}
		}
	}
	
	var domains = $('input#domainlist');
	if(domains.length && $.trim(domains.val()) != '' && domains.val() != '$IDM_APPROVE_DOMAIN_LIST') {
		var approval = $('input[name="visiting"]'), re = emailReg(), domainArr = domains.val().split(',');
		if (!re.test(approval.val())) {
			printErr(approval, 'Please enter a valid empolyee email address');
			return false;
		} else {
			var flag = false, value = $.trim(approval.val());
            if (typeof String.prototype.endsWith !== 'function') {
                String.prototype.endsWith = function(suffix) {
                    return this.indexOf(suffix, this.length - suffix.length) !== -1;
                };
            }
            for(var i=0; i<domainArr.length; i++) {
                if(value.endsWith($.trim(domainArr[i]))) {
                    flag = true;
                    break;
                }
            }
			if(!flag) {
				printErr(approval, 'Please enter a valid empolyee email address');
				return false;
			}
		}
	}

	// for ppsc_index
	var mailField = $("#field3");
	if ($("#field3") != null && $("#field3").attr("name") == "email"
			&& $("#field3").val() != null && $("#field3").val() != '') {
		var re = new RegExp(/^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/);
		if (!re.test($("#field3").val())) {
			printErr($("#field3"), 'Please enter an email address');
			return false;
		}
	}
}

fieldErrorIds = new Array();
var fieldErrorTimeoutId;

function reportFieldError(element, message) {
	clearTimeout(fieldErrorTimeoutId);
	hideFieldError();
	var feId = "fe_" + element.id;
	var fe = document.getElementById(feId);
	if (fe == null) {
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
		tr = document.createElement("tr");
		td = document.createElement("td");
		td.appendChild(div);
		tr.appendChild(td);
		var row = element.parentNode.parentNode;
		table = row.parentNode;
		table.insertBefore(tr, row);
	} else {
		var td = document.getElementById("text" + feId);
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(message));
	}
	if (vectorContains(fieldErrorIds, feId)) {
	} else {
		fieldErrorIds.push(feId);
		
		document.getElementById(feId).style.display = "";
	}
	delayHideFieldError(10);
}


function delayHideFieldError(seconds) {
	fieldErrorTimeoutId = setTimeout("hideFieldError()", seconds * 1000);  // seconds
}


function hideFieldError() {
	for (var i = 0; i < fieldErrorIds.length; i++) {
		document.getElementById(fieldErrorIds[i]).style.display = "none";
	}
	fieldErrorIds = new Array();
}

function vectorContains(vector, element) {
	for (var i = 0; i < vector.length; i++) {
		if (vector[i] == element) {
			return true;
		}
	}
	return false;
}
