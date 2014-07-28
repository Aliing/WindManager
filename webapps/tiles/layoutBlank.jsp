<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=7;FF=3;OtherUA=4" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />

<!--CSS file (default YUI Sam Skin) -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/assets/skins/sam/resize.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none"/>?v=<s:property value="verParam" /> " />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/autocomplete/assets/skins/sam/autocomplete.css"  includeParams="none"/>?v=<s:property value="verParam" />" />

<script type="text/javascript"
	src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/menu/menu-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/autocomplete/autocomplete-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/head.load.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
	
<s:if test="%{jsonMode == true}">
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />

<script type="text/javascript">
function dynamicAddSelect(listBox,name,value){
	 var tempOption = document.createElement("option");
	 tempOption.appendChild(document.createTextNode(name));
	 if(arguments.length == 3){
	  tempOption.setAttribute("value",value);
	 }
	 listBox.appendChild(tempOption);
	 listBox.value = value;
	 
	var ops =  listBox.options;
	if (ops[0].value == -1 && ops[0].text == 'None available') {
		listBox.remove(ops[0].selectedIndex);
	}
	
}
</script>
</s:if>

<style type="text/css">
html,body {
	margin: 0;
	padding: 0;
	<s:if test="%{jsonMode == true}">
		background-color: transparent;
	</s:if>
	<s:else>
		background-color: #fff;
	</s:else>
}
</style>
<script type="text/javascript">
var confirmDialog;
var warnDialog;
var infoDialog;
function initConfirmDialog() {
	confirmDialog =
     new YAHOO.widget.SimpleDialog("confirmDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                text: "<html><body>This operation will remove the selected item(s).<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
                           { text:"&nbsp;No&nbsp;", handler:handleNo } ]
              } );
     confirmDialog.setHeader("Confirm");
     confirmDialog.render(document.body);
     confirmDialog.cancelEvent.subscribe(confirmCancelHandler);
}

function initWarnDialog() {
	warnDialog =
     new YAHOO.widget.SimpleDialog("warnDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                icon: YAHOO.widget.SimpleDialog.ICON_ALARM,
                constraintoviewport: true,
                buttons: [ { text:"OK", handler:handleNo, isDefault:true } ]
              } );
     warnDialog.setHeader("Warning");
     warnDialog.render(document.body);
}

function showWarnDialog(message){
	if (warnDialog == null){
		initWarnDialog();
	}
	warnDialog.cfg.setProperty("text",message);
	warnDialog.show();
}

function showInfoDialog(info)
{
	if (infoDialog == null)
	{
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "350px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon: YAHOO.widget.SimpleDialog.ICON_INFO,
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]});
		infoDialog.setHeader("Information");
		infoDialog.render(document.body);
	}
	
	infoDialog.cfg.setProperty("text",info);
	infoDialog.show();
}

function confirmCancelHandler(){
}

var handleYes = function() {
    this.hide();
    doContinueOper();
};

var handleNo = function() {
    this.hide();
};

function onLoadNotes() {
}
function onLoadPaging() {
}
function onLoadPage() {
}
function onLoadEvent() {
	onLoadNotes();
	onLoadPaging();
	onLoadPage();
	trackDiffInit();
	initConfirmDialog();
	initWarnDialog();
	avoidSavePromptDialog();
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	onUnloadPage();
}
function beforeUnloadEvent() {
}

function avoidSavePromptDialog(){
	// Make auto complete off for those password tag, to avoid password 
	// save prompt dialog in config page
	var elements = document.getElementsByTagName("input");
	for(var i=0; i<elements.length; i++){
		if(elements[i].type == "password"){
			elements[i].setAttribute("autocomplete","off");
		}
	}
}

var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

var ajaxRequest = function(formName, url, doSuccess, type){
	type = type || "GET";
	if(formName){
		YAHOO.util.Connect.setForm(Get(formName));
	}
	var transaction = YAHOO.util.Connect.asyncRequest(type, url, {success : doSuccess}, null);
}

<s:if test="%{userContext != null && userContext.getUserName().length() > 0}">
//common function for profile edit page (folding label)
function insertFoldingLabelContext(labelName, contentId){
	document.writeln('<span style="cursor: pointer;" onclick="alternateFoldingContent(\''+ contentId + '\');">');
	document.writeln('<img id="' + contentId + 'ShowImg" src="<s:url value="/images/expand_plus.gif" includeParams="none"/>" \
			alt="Show Option" class="expandImg" style="display: inline"/>' +
			'<img id="' + contentId + 'HideImg" src="<s:url value="/images/expand_minus.gif" includeParams="none"/>" \
			alt="Hide Option" class="expandImg" style="display: none"/>');
	document.writeln(labelName);
	document.writeln('</span>');

	//adjust icons after page loaded!
	YAHOO.util.Event.onDOMReady(function () {
		adjustFoldingIcon(contentId);
	});
}

var Get = function(o){return typeof o == "string" ? document.getElementById(o): o;}

function alternateFoldingContent(contentId){
	var contentEl = Get(contentId);
	var cssStyle = contentEl.style.display=="none" ? "":"none";
	showHideContent(contentId, cssStyle);
}

function showHideContent(contentId, cssStyle){
	var contentEl = Get(contentId);
	if(contentEl) {
		contentEl.style.display=cssStyle;
		adjustFoldingIcon(contentId);
	}
}

function adjustFoldingIcon(contentId){
	var contentEl = Get(contentId);
	if(contentEl == null){return;}
	var showEl = Get(contentId+"ShowImg");
	var hideEl = Get(contentId+"HideImg");
	if(contentEl.style.display=="none"){
		showEl&&(showEl.style.display = "inline");
		hideEl&&(hideEl.style.display = "none");
	}else{
		showEl&&(showEl.style.display = "none");
		hideEl&&(hideEl.style.display = "inline");
	}
}
</s:if>

</script>

<s:if test="%{!trackFormChanges || updateDisabled == 'disabled'}">
 	<script src="<s:url value="/js/jquery.min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script type="text/javascript">
	//debug
	var trackFormChanges = <s:property value="trackFormChanges" />;
	var updateDisabled = '<s:property value="updateDisabled" />';
	function trackDiffInit(){
		//dummy
	}
	function traceDiffResult(e){
		//dummy
	}
	function beforeSubmitAction(formObj){
		//dummy
	}
	</script>
</s:if>
<s:else>
	<!-- track changes in a form -->
	<script src="<s:url value="/js/jquery.min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script type="text/javascript">
	var formInitialDataHash = [];
	var ignoreCheck = false;
	var formChangedDefault = <s:property value="formChanged" />;
	function getDocumentValidForms(){
		var fms = [];
		var forms = document.forms;
		for(var i=0; i< forms.length; i++){
			var fm = forms[i];
			if(fm.name && fm.action){
				fms.push(fm);
			}
		}
		return fms;
	}
	function formDataIsChanged(){
		var forms = getDocumentValidForms();
		for(var i=0; i<forms.length; i++){
			var fm = forms[i];
			if(formInitialDataHash[fm.name] != undefined){
				var diffs = diff(fm, formInitialDataHash[fm.name]);
				if(diffs.count > 1){
					return true;
				}else if((diffs.count == 1) && diffs.diffs["formChanged"] == undefined){
					// it is not 'formChanged' different, another one changed.
					return true;
				}
			}
		}
		return false;
	}
	function trackDiffInit(){
		try{
			var forms = getDocumentValidForms();
			for(var i=0; i< forms.length; i++){
				var fm = forms[i];
				var initialData = toJSObject(fm);
				formInitialDataHash[fm.name] = initialData;
			}
			//using jquery to add listener to buttons and anchors
			//select is the item in the report pages
			$("form a, button, form input[type='button'].button, form fieldset>select")
				.mousedown(prepareFormsubmit)
				.mouseup(resetFlag);
			/*prevant prompt for javascript pseudo-protocol in anchors in IE browser*/
			if (YAHOO.env.ua.ie) {$("a[href^='javascript:']").click(prevantPrompt);}
		}catch(err){
			//debug
			alert("error in trackDiffInit:"+err.description+"\n\n Click OK continue");
		}
	}
	/* special for submit with '[-New-]' item in select list */
	function beforeSubmitAction(formObj){
		ignoreCheck = true;
		var formChanged = formDataIsChanged() || formChangedDefault;
		if(formObj.formChanged != undefined){
			formObj.formChanged.value = formChanged;
		}
	}

	var allowPrompt = true;/*Allow user to be warned by default*/
	function prevantPrompt(){
		allowPrompt = false;
	}

	var trackChangeTimeoutId;
	function resetFlag(){//reset when validation unpassed or client side click.
		trackChangeTimeoutId = setTimeout(function(){ignoreCheck = false; trackChangeTimeoutId = 0;}, 500);
	}
	function prepareFormsubmit(){
		if(trackChangeTimeoutId){
			clearTimeout(trackChangeTimeoutId);
		}
		//do not ignore check while click on an anchor with '#' or 'javascript:';
		if(this.href && (this.href.indexOf('#')>-1 || this.href.search(/https:/)>-1)){
			return;
		}//ignore check while do double click in the list in report pages.
		else if(this.tagName && this.tagName == 'SELECT' && this.parentNode && this.parentNode.tagName =='FIELDSET'){
			ignoreCheck = true;
		}else{
			ignoreCheck = true;
			var formChanged = formDataIsChanged() || formChangedDefault;
			var forms = getDocumentValidForms();
			for(var form in forms){
				if(forms[form].formChanged != undefined){
					forms[form].formChanged.value = formChanged;
				}
			}
		}
	}
	var trackTitle = '<s:property value="trackChangeTitle" />';
	var tips = "You have unsaved changes. They will be lost if you continue."
				+"\n\n(If you don't like this popup window, change the option under"
				+"\n\"Home\">\""+trackTitle+"\")";
	function traceDiffResult(e){
		if(allowPrompt){
			try{
				e = e || window.event;
				var formChanged;
				if(!ignoreCheck){
					formChanged = formDataIsChanged() || formChangedDefault;
				}
				if(formChanged){
					hm.util.hide("processing");
					// for IE and FF
					if(e){
						e.returnValue = tips;
					}
					// for Safari
					return tips;
				}
			}catch(err){
				alert("error in traceDiffResult:"+err.description+"\n\n Click OK continue");
			}
		}else{
			allowPrompt = true;/*Reset the flag to its default value*/
		}
	};
	//API for store/compare form.
	var toJSObject = function(f, o){
	  var opts = o || {};
	  var h = {};
	  function addIntoArray(orig, val) {
	   if (orig) {
	     var r = null;
	     if (typeof orig == 'string') {
	       r = [];
	       r.push(orig);
	     }else {
		 	r = orig;
		 }
	     r.push(val);
	     return r;
	   }else {
	   	 return val;
	   }
	  }

	  for (var i = 0; i < f.elements.length; i++) {
	    var elem = f.elements[i];
	    // Elements should have a name
	    if (elem.name) {
	      var n = elem.name;

	      switch (elem.type) {
	        // Text fields, hidden form elements, etc.
	        case 'text':
	        case 'hidden':
	        case 'password':
	        case 'textarea':
	        case 'select-one':
	          h[n] = elem.value;
	          break;
	        // Multi-option select
	        case 'select-multiple':
	          for(var j = 0; j < elem.options.length; j++) {
	            var e = elem.options[j];
	           // if(e.selected) {
	            h[n] = addIntoArray(h[n], e.value);
	            //}
	          }
	          break;
	        // Radio buttons
	        case 'radio':
	          if (elem.checked) {
	            h[n] = elem.value;
	          }
	          break;
	        // Checkboxes
	        case 'checkbox':
	          if (elem.checked) {
	            h[n] = addIntoArray(h[n], elem.value);
	          }
	          break;
	      }
	    }
	  }
	  return h;
	}

	var diff = function (formUpdated, formOrig, opts) {
	  var o = opts || {};
	  // Accept either form or hash-conversion of form
	  var hUpdated = formUpdated.elements ?toJSObject(formUpdated) : formUpdated;
	  var hOrig = formOrig.elements ?toJSObject(formOrig) : formOrig;
	  var diffs = [];
	  var count = 0;

	  function addDiff(n, hA, hB, secondPass) {
	    if (!diffs[n]) {
	      count++;
	      diffs[n] = secondPass?{ origVal: hA[n], newVal: hB[n] } :{ origVal: hB[n], newVal: hA[n] };
	    }
	  }

	  function diffSearch(hA, hB, secondPass) {
	    for (n in hA) {
	      // Elem doesn't exist in B
	      if (typeof hB[n] == 'undefined') {
	        addDiff(n, hA, hB, secondPass);
	      }
	      // Elem exists in both
	      else {
	        v = hA[n];
	        // Multi-value -- array, hA[n] actually has values
	        if (v instanceof Array) {
	          if (!hB[n] || (hB[n].toString() != v.toString())) {
	            addDiff(n, hA, hB, secondPass);
	          }
	        }else {// Single value -- null or string
	          if (hB[n] != v) {
	            addDiff(n, hA, hB, secondPass);
	          }
	        }
	      }
	    }
	  }
	  // First search check all items in updated
	  diffSearch(hUpdated, hOrig, false);
	  // Second search, check all items in orig
	  diffSearch(hOrig, hUpdated, true);

	  // Return an obj with the count and the hash of diffs
	  return { count: count, diffs: diffs};
	}
</script>
</s:else>
</head>
<body class="yui-skin-sam" onbeforeunload="beforeUnloadEvent();"
	onload="onLoadEvent();" onunload="onUnloadEvent();">
<%-- YUI table sorting doesn't work when inside a table element!
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" style="padding: 4px 4px 0px 5px;" width="100%"><tiles:insertAttribute name="body" /></td>
	</tr>
</table>
 --%>
 <div style="padding: 0px 4px 0px 5px; vertical-align: top;">
 <tiles:insertAttribute name="body" />
 </div>
</body>
</html>