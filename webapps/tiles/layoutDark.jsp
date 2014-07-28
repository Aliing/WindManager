<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http:// www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<tiles:importAttribute name="leftWidth" scope="request" />

<html>
<head>
<title><s:property value="%{selectedL2Feature.description}" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<style>
td.clients {background-image: url(<s:url value="/images/hm/bkg.gif" includeParams="none"/>);}
td.expandedClients {padding: 4px 4px 0px 5px;}
td.collapsedClients {padding: 4px 4px 0px 20px;}
#sliderAnchor{outline: none; position: absolute; left: -20px; top: 6px;}
#sliderAnchor img{width: 18px;height: 18px; border: 0px; background: url(<s:url value="/images/slider.gif" includeParams="none"/>) no-repeat;}
#sliderAnchor img.expanded {background-position:  0px 0px; height: 18px;}
#sliderAnchor img.collapsed {background-position: -18px 0px; height: 94px;}
#sliderAnchor img.expandedMouseOver {background-position: 0px -94px; height: 18px;}
#sliderAnchor img.collapsedMouseOver {background-position: -18px -94px; height: 94px;}
td.hide{display: none;}

</style>

<link rel="shortcut icon"
	href="<s:url value="/images/favicon.ico" includeParams="none"/>"
	type="image/x-icon" />

<script src="<s:url value="/js/hm.util.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<s:if test="%{useCdn}">
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css"
		href="<s:property value="yuiBase" />/assets/skins/sam/skin.css" />
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/utilities/utilities.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/datasource/datasource-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/autocomplete/autocomplete-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/container/container-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/menu/menu-min.js"></script>
	<script type="text/javascript"
		src="<s:property value="yuiBase" />/button/button-min.js"></script>
</s:if>
<s:else>
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/container/assets/skins/sam/container.css" includeParams="none"/>?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/menu/assets/skins/sam/menu.css" includeParams="none"/>?v=<s:property value="verParam" /> " />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/fonts/fonts-min.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/button/assets/skins/sam/button.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
	<link rel="stylesheet" type="text/css"
		href="<s:url value="/yui/autocomplete/assets/skins/sam/autocomplete.css"  includeParams="none"/>?v=<s:property value="verParam" />" />
	<script
		src="<s:url value="/yui/utilities/utilities.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/container/container-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/menu/menu-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/button/button-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/datasource/datasource-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/autocomplete/autocomplete-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/logger/logger-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
	<script
		src="<s:url value="/yui/yahoo-dom-event/yahoo-dom-event.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
</s:else>
<script src="<s:url value="/js/head.load.min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/hm.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script>
var confirmDialog;
var warnDialog;
var infoDialog;
var oPopup;
var overlayManager;
var console = window.console || undefined;
var _debug_flag = false;
var debug = function(message){
	if(console && _debug_flag){
		console.log(message);
	}
}

function initConfirmDialog() {
	confirmDialog =
     new YAHOO.widget.SimpleDialog("confirmDialog",
              { width: "350px",
                fixedcenter: true,
                visible: false,
                draggable: true,
                modal:true,
                close: true,
                text: "<html><body>This operation will remove the selected items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                constraintoviewport: true,
                buttons: [ { text:"Yes", handler:handleYes, isDefault:true },
                           { text:"&nbsp;No&nbsp;", handler:handleNo } ]
              } );
     confirmDialog.setHeader("Confirm");
     confirmDialog.render(document.body);
     confirmDialog.cancelEvent.subscribe(confirmCancelHandler);
}

function confirmCancelHandler(){
}

function initWarnDialog() {	
    if (null == warnDialog) {
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

var handleYes = function() {
    this.hide();
    doContinueOper();
};

var handleNo = function() {
    this.hide();
};

function onLoadPage() {
}
function onLoadNotes() {
}
function onLoadPaging() {
}
function onLoadPage() {
}
var currentHeight;
function onLoadEvent() {
	searchAutoComplete();

	onLoadNotes();
	onLoadPaging();
	// before onLoadPage function, initialize overlay manager
	overlayManager = new YAHOO.widget.OverlayManager();
	onLoadPage();

	initConfirmDialog();
	initWarnDialog();
	hm.util.registerRowClickedEvents();
	sizeLeftMenu();
	currentHeight = document.documentElement.clientHeight;
	window.onresize = function(){
		var bodyHeight = document.documentElement.clientHeight;
		if(bodyHeight != currentHeight){
			sizeLeftMenu();
		}
		currentHeight = bodyHeight;
	}
	hm.util.startSystemStatusTimer("<s:url action="alarms" includeParams="none" />");
//	alert("YUI DOM version: " + YAHOO.env.getVersion("dom").version);
	if(YAHOO.env.ua.ie == 6){
		//fix PNG bug in IE6
		fixLogoPng();
	}
	trackDiffInit();
	avoidSavePromptDialog();
	hm.util.invokeTrack("<s:url value="navigationTree.action" includeParams="none"></s:url>", "<s:property value="%{operation}" />", "<s:property value="%{start}" />");

	<s:if test="%{popupSimulator}">
		openSimulatorPanel();
	</s:if>
<%-- hide from server side, avoid re-layout on page rendering.
	<s:if test="%{easyMode && lastExConfigGuide != null}">
		/*
		 * hide the left menu bar
		 */
		 hideSlider();
	</s:if>
--%>
	// display (or not) the search box under 'Advanced Configuration'
	displaySearchConfigBox();
	//support ipad label click
	addIpadLableClick();

	// show access authorized remaining time.
	var tmpAccessAuthorizedLeftTime = '<s:property value="tmpAccessAuthorizedLeftTime" />';
	if (tmpAccessAuthorizedLeftTime != null && tmpAccessAuthorizedLeftTime.length > 0) {
		tmpAccessAuthorizedLeftTime = tmpAccessAuthorizedLeftTime.replace("&lt;br&gt;", "<br>");
		showInfoDialog(tmpAccessAuthorizedLeftTime);
	}
}
/*
 * js support ipad label click begin
 */
 function addIpadLableClick () {
	if (navigator.userAgent.match(/iPhone/i) || navigator.userAgent.match(/iPod/i) || navigator.userAgent.match(/iPad/i)) {
		labelfix();
	}
}

function labelfix() {
	var labels = document.getElementsByTagName('label'),
	target_id,
	el;
	for (var i = 0; labels[i]; i++) {
		if (labels[i].getAttribute('for')) {
			labels[i].onclick = labelClick;
		}
	}
}

function labelClick() {
	el = document.getElementById(this.getAttribute('for'));
	if (['radio', 'checkbox'].indexOf(el.getAttribute('type')) != -1) {
		el.setAttribute('selected', !el.getAttribute('selected'));
	} else {
		el.focus();
	}
}
/*
 * js support ipad label click end
 */

function displaySearchConfigBox() {
	if(typeof(navTree) != 'undefined' && navTree) {
		for(var index=0; index < navTree.length; index++) {
			var node = navTree[index];
			if(node.id == "advancedConfig") {
				if(typeof(handleConfigSearchBox) == 'function') {
					handleConfigSearchBox(node);
				}
				break;
			}
		}
	}
}
function onUnloadNotes() {
}
function onUnloadPage() {
}
function onUnloadEvent() {
	onUnloadNotes();
	hm.util.onUnloadValidation();
	onUnloadPage();
	hm.util.clearSystemStatusTimer();
}
function onBeforeUnloadEvent(e){
	traceDiffResult(e);
}

function displayTriggerIconDiv(){
	Get("triggerIconDiv").style.display="";
}
function hideTriggerIconDiv(){
	Get("triggerIconDiv").style.display="none";
}
function resizeLeftMenu() {
	var bodyHeight = document.documentElement.clientHeight;
	if(bodyHeight != currentHeight){
		sizeLeftMenu();
	}
	currentHeight = bodyHeight;
}
function sizeLeftMenu() {
	var div = document.getElementById("leftMenu");
	if (div != null) {
		sizeLeftMenuScroll(div);
		return;
	}
	var td = document.getElementById('lms');
	if (td == null) {
		return;
	}
	var vpHeight = YAHOO.util.Dom.getViewportHeight();
	if(location.href.indexOf("reports.action") != -1) {
		// get the document height when stay in the report page
		vpHeight = YAHOO.util.Dom.getDocumentHeight();
	}
	var lse = document.getElementById('lse');
	if (lse == null) {
		return;
	}
	var extra = vpHeight - YAHOO.util.Dom.getY(lse) - 13 + parseInt(td.height) + YAHOO.util.Dom.getDocumentScrollTop();
	if (YAHOO.env.ua.ie >= 7) {
	//	extra += 2;
	}
	if (YAHOO.env.ua.webkit > 0) {
//		extra -= 2;
	}
	extra += 12;
	if (extra > 0) {
		td.height = extra;
	} else {
		td.height = 1;
	}
}
// With scroll bars
function sizeLeftMenuScroll(div) {
	var vpHeight = YAHOO.util.Dom.getViewportHeight();
	var lss = document.getElementById('lss');
	if (lss == null) {
		return;
	}
	var lse = document.getElementById('lse');
	if (lse == null) {
		return;
	}
	var lsh = YAHOO.util.Dom.getY(lse) - YAHOO.util.Dom.getY(lss);

	var lms = YAHOO.util.Dom.getY(div);

	var lmh = vpHeight - lms - lsh - 36;
	if (YAHOO.env.ua.ie) {
		lmh += 3;
	}

	scrollTo(0, 0);

	if (lmh < 100) {
		lmh = 100;
	}

	if (YAHOO.env.ua.ie) {
		div.style.height = lmh;
	} else {
		div.style.height = lmh + "px;";
	}
}

var ajaxRequest = function(formName, url, doSuccess, type){
	type = type || "GET";
	if(formName){
		YAHOO.util.Connect.setForm(Get(formName));
	}
	var transaction = YAHOO.util.Connect.asyncRequest(type, url, {success : doSuccess}, null);
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

var leftMenuWidth;
function hideLeftMenu() {}
function showLeftMenu() {
	 sizeLeftMenu();
}
function triggerSlider(){
	var Dom = YAHOO.util.Dom;
	var sliderAnchor = document.getElementById("sliderAnchor");
	var leftMenuBar = document.getElementById("leftMenuBar");
	var child = Dom.getFirstChild(sliderAnchor);
	var tit = document.getElementById("userSelectNav");
	var cli=tit.getAttribute("onclick");
	if(null == leftMenuWidth){
		// fix unexcepted width in FF.
		leftMenuWidth = Dom.getStyle(leftMenuBar,"width");
		Dom.setStyle(leftMenuBar, "width", leftMenuWidth);
	}
	if(Dom.getStyle(leftMenuBar, "display") != "none"){//expand -- collapse
		Dom.setStyle(leftMenuBar, "display", "none");
		//Dom.setStyle(sliderAnchor, "left", "-20px");
		Dom.setAttribute(child, "title", "Show left panel");
		Dom.replaceClass(child, "expandedMouseOver", "collapsed");
		Dom.replaceClass(Get("bodyTD"), "expandedClients", "collapsedClients");
 		        var url = "<s:url action='/hm/users.action' includeParams='none'/>" + "?operation=updateUser&displayNav=false&onclick="+cli + "&ignore=" + new Date().getTime();
		        var transaction = YAHOO.util.Connect.asyncRequest('POST', url, null, null);
		hideLeftMenu();
	}else{//collapse -- expand
		Dom.setStyle(leftMenuBar, "display", "");
		//Dom.setStyle(sliderAnchor, "left", "-20px");
		Dom.setAttribute(child, "title", "Hide left panel");
		Dom.replaceClass(child, "collapsedMouseOver", "expanded");
		Dom.replaceClass(Get("bodyTD"), "collapsedClients", "expandedClients");
 		        var url = "<s:url action='/hm/users.action' includeParams='none'/>" + "?operation=updateUser&displayNav=true&onclick="+cli+ "&ignore=" + new Date().getTime();
		        var transaction = YAHOO.util.Connect.asyncRequest('POST', url, null, null);
		showLeftMenu();
	}
}
<%-- hide from server side, avoid re-layout on page rendering.
function hideSlider(){
	var Dom = YAHOO.util.Dom;
	var sliderAnchor = document.getElementById("sliderAnchor");
	var leftMenuBar = document.getElementById("leftMenuBar");
	var child = Dom.getFirstChild(sliderAnchor);
	if(null == leftMenuWidth){
		// fix unexcepted width in FF.
		leftMenuWidth = Dom.getStyle(leftMenuBar,"width");
		Dom.setStyle(leftMenuBar, "width", leftMenuWidth);
	}

//	Dom.addClass(leftMenuBar, "hide");
	Dom.replaceClass(child, "collapseMouseOver", "expandPanel");
	Dom.setStyle(child, "height", "94px");
	Dom.setStyle(Get("bodyTD"), "padding-left", "20px");
	hideLeftMenu();
}
--%>
function overImageStyle() {
	var Dom = YAHOO.util.Dom;
	var sliderAnchor = document.getElementById("sliderAnchor");
	var leftMenuBar = document.getElementById("leftMenuBar");
	var child = Dom.getFirstChild(sliderAnchor);

	if(null == leftMenuWidth){
		// fix unexcepted width in FF.
		leftMenuWidth = Dom.getStyle(leftMenuBar,"width");
		Dom.setStyle(leftMenuBar, "width", leftMenuWidth);
	}

	if(Dom.getStyle(leftMenuBar, "display") != "none"){//collapse
		Dom.replaceClass(child, "expanded", "expandedMouseOver");
	}else{
		Dom.replaceClass(child, "collapsed", "collapsedMouseOver");
	}
}

function outImageStyle() {
	var Dom = YAHOO.util.Dom;
	var sliderAnchor = document.getElementById("sliderAnchor");
	var leftMenuBar = document.getElementById("leftMenuBar");
	var child = Dom.getFirstChild(sliderAnchor);

	if(null == leftMenuWidth){
		// fix unexcepted width in FF.
		leftMenuWidth = Dom.getStyle(leftMenuBar,"width");
		Dom.setStyle(leftMenuBar, "width", leftMenuWidth);
	}

	if(Dom.getStyle(leftMenuBar, "display") != "none"){//collapse
		Dom.replaceClass(child, "expandedMouseOver", "expanded");
	}else{
		Dom.replaceClass(child, "collapsedMouseOver", "collapsed");
	}
}

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
<body class="body_bg skin_hm yui-skin-sam" onload="onLoadEvent();"
	onunload="onUnloadEvent();" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0"
	onbeforeunload="onBeforeUnloadEvent(event);">
<script type="text/javascript">
	//avoid access hm from iframe
	if (top.location != self.location){
		top.location=self.location;
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><tiles:insertAttribute name="topPane" /></td>
	</tr>
	<tr>
		<td>
		<div class="mainContent">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td id="leftMenuBar" class="dark" valign="top" style="display: <s:property value="%{menuDisplayStyleString}" />" >
				<table border="0" cellspacing="0" cellpadding="0">
					<s:if test="%{searchResult != null}">
						<tr>
							<td class="left_nav_bg">
								<img
									src="<s:url value="/images/spacer.gif" includeParams="none"/>"
									width="${leftWidth + 100}" height="1" alt="" class="dblk" />
							</td>
						</tr>
						<tr>
							<td class="left_nav_bg"><tiles:insertAttribute
								name="leftSearch" /></td>
						</tr>
						<tr>
							<td class="left_nav_bg" height="1" id="lms"></td>
						</tr>
						<tr>
							<td height="1" id="lse"></td>
						</tr>
					</s:if>
					<s:else>
						<tr>
							<td class="left_nav_bg"><img
								src="<s:url value="/images/spacer.gif" includeParams="none"/>"
								width="${leftWidth + 20}" height="1" alt="" class="dblk" /></td>
						</tr>
						<tr>
							<td class="left_nav_bg"><tiles:insertAttribute name="leftMenu" /></td>
						</tr>
						<tr>
							<td class="left_nav_bg"><tiles:insertAttribute
								name="leftFilter" /></td>
						</tr>
		<%--
						<s:if test="%{selectedL1Feature.key == 'tools'}">
							<tr>
								<td height="20" class="left_nav_bg"></td>
							</tr>
							<tr>
								<td class="left_nav_bg"><a class="leftNavL1Hidden"
									href='<s:url action="configTest" includeParams="none"><s:param name="operation" value="%{'test'}" /></s:url>'>Test</a></td>
							</tr>
							<tr>
								<td height="20" class="left_nav_bg"></td>
							</tr>
							<tr>
								<td class="left_nav_bg"><a class="leftNavL1Hidden"
									href='<s:url action="spectralAnalysis" includeParams="none"><s:param name="operation" value="%{'analysis'}" /></s:url>'>Analysis</a></td>
							</tr>
							<tr>
								<td height="20" class="left_nav_bg"></td>
							</tr>
							<tr>
								<td class="left_nav_bg"><a class="leftNavL1Hidden"
									href='<s:url action="configTest" includeParams="none"><s:param name="operation" value="%{'mgmtTop'}" /></s:url>'>Top</a></td>
							</tr>
						</s:if>
		  --%>
						<tr>
							<td class="left_nav_bg" height="1" id="lms"></td>
						</tr>
						<tr>
							<td><tiles:insertAttribute name="statusView" /></td>
						</tr>
					</s:else>
				</table>
				</td>
				<td valign="top" class="client <s:property value="%{slideStyleStatus}" />Clients" width="100%" id="bodyTD">
				<div style="position: relative; z-index: 2" id="triggerIconDiv">
					<a id="sliderAnchor" hidefocus="true" href="javascript:void(0)" onclick="triggerSlider()">
						<img src="<s:url value="/images/spacer.gif" includeParams="none"/>" class="<s:property value="%{slideStyleStatus}" />"
							onmouseover="overImageStyle();" onmouseout="outImageStyle();"/>
					</a>
				</div>
				<tiles:insertAttribute name="body" />
				</td>
			</tr>
			</table>
		</div>
		</td>
	</tr>
	<s:if test="%{popupSimulator}">
	<tr>
		<td>
		<div id="simulatorPanel" style="display: none;">
			<div class="hd"><s:text name="tools.simulator.simulateAP"/></div>
			<div class="bd">
				<iframe id="simulatorFrame" name="simulatorFrame" width="0" height="0"
					frameborder="0" src="javascript:">
				</iframe>
			</div>
		</div>
		<script type="text/javascript">
		var simulatorPanel = null;
		function createSimulatorPanel(width, height){
			var div = document.getElementById("simulatorPanel");
			width = width || 600;
			height = height || 600;
			var iframe = document.getElementById("simulatorFrame");
			iframe.width = width;
			iframe.height = height;
			simulatorPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", fixedcenter:"contained", visible:false, constraintoviewport:true } );
			simulatorPanel.render();
			div.style.display="";
			simulatorPanel.beforeHideEvent.subscribe(clearSimulatorData);
		}
		function clearSimulatorData(){
			//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
			if(YAHOO.env.ua.ie){
				document.getElementById("simulatorFrame").style.display = "none";
			}

			// send simulator flag
			var url = "<s:url action='apSimulator' includeParams='none' />?operation=setSimulatorFlag&ignore=" + new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, { success:sendResult}, null);
		}
		function sendResult(o){
		}
		function openSimulatorPanel()
		{
			if(null == simulatorPanel){
				createSimulatorPanel(600,400);
			}
			//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
			if(YAHOO.env.ua.ie){
				document.getElementById("simulatorFrame").style.display = "";
			}
			simulatorPanel.show();
			var iframe = document.getElementById("simulatorFrame");
			iframe.src ="<s:url value='apSimulator.action' includeParams='none' />?operation=blankSimulator&ignore=" + new Date().getTime();
		}
		</script>
		</td>
	</tr>
	</s:if>
</table>
</body>
</html>
