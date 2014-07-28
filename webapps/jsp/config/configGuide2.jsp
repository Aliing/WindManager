<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.config.UserProfilesAction"%>
<%@page import="com.ah.bo.hiveap.HiveAp"%>
<script src="<s:url value="/js/innerhtml.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/hm.options.js" />"></script>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ports.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />

<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/hm.widget.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<!--[if lte IE 8]>
    <link rel="stylesheet" type="text/css" href="<s:url value="/css/ie-style.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<![endif]--> 
<script src="<s:url value="/js/hm.widget.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/animation/animation-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/yui/selector/selector-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<script
    src="<s:url value="/js/doT.min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
    src="<s:url value="/js/json2-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
    src="<s:url value="/js/widget/ports/ports.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<style type="text/css">
div#content {
	height: 100%;
	width: 100%;
}

label.text {
	padding-left: 20px;
}

div.header, div.footer {
	margin: 0 auto;
	width: 1000px;
	line-height: 56px;
	background-size: cover;
}

div.header {
	/* font */
	font-family: century gothic,helvetica light, arial, sans serif;
	font-size: 22px;
	font-weight: 300;
	color: #717174;
	background: #f8f8f8;
}

div.footer {
	height: 5px;
	line-height: 5px;
}

span.icon {
	float: right;
	height: 25px;
	margin-top: 20px;
}
img.selected {
	border: 4px solid #0092d6;
}
img.unselected {
	border: 4px solid #fff;
}

.a0{
	float:left;
	margin-left:0px;
	width:102px;
	height:15px;
	border:1px solid #5B94DF;
}

.a1{
	float:left;
	height:13px;width:0px;font-size:12px;
	border-left:1px solid  #FFFFFF;
	border-top:1px solid  #FFFFFF;
	border-bottom:1px solid  #FFFFFF;
	background-Color:#8cd92b;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#66f900, endColorstr=#81ACE7);
}

.cursorPointer {
	cursor: pointer;
}

#updateBTN ul {
    padding: 2px 0;
}
#updateBTN li {
    background-image: none;
    line-height: 1.4;
}

td#accessPortType span:first-child input[type="radio"] {
    margin-left: 0;
}

.selectList.npSelectContainer {
	width: 35em;
}
</style>
<!--[if lte IE 8]><div id="content" class="configguide ie8"><![endif]-->
<!--[if (gt IE 8)|(!IE)]><!--><div id="content" class="configguide"><!-- <![endif]-->
	<div class="header" style="margin-top: 20px;">
		<!--<span class="icon" style="margin-right: 10px;"><img src="<s:url value="/images/hm_v2/HM-icon-help.png" includeParams="none"/>" width="25px"></span>
		<span class="icon" style="cursor: pointer;margin-right: 10px;"><img id="globalSettingsId" src="<s:url value="/images/hm_v2/HM-icon-settings.png" includeParams="none"/>" onClick="javascript: doGlobalEditNetworkPolicy();" width="25px"></span>-->
		<label class="text">Network Configuration</label>
	</div>

	<div style="padding: 0px;width: 1000px;" id="accordionDiv" class="aero-accordionview">
		<ul>
			<li class="aero-accordion-drawer">
				<div id="selectNetWorkPolicy" class="aero-accordion-drawer-title"><h3 class="npcButton" id="selectedHeader">
					1 - Choose Network Policy
					</h3>
				</div>
			</li>
			<li style="margin: 2px 0px 2px 0px;" class="aero-accordion-drawer">
				<div id="netWorkPolicy" class="aero-accordion-drawer-title"><h3 class="npcButton">
					<a class="btCurrent" href="javascript:void(0);" style="float: right;" >
						<span id="netWorkPolicySpanId" class="hidden" style="padding-bottom: 5px;padding-top: 0px;height: 25px;">Continue</span></a>
					<a class="btCurrent" id="btNetworkSave" href="javascript:void(0);" style="float: right; margin:0px 25px; display: none;">
						<span id="netWorkPolicySaveSpanId" class="hidden" style="padding-bottom: 5px;padding-top: 0px;height: 25px;">Save</span></a>
						2 - Configure Interfaces and User Access
					</h3>
				</div>
			</li>
			<li class="aero-accordion-drawer">
				<div id="cofigurePolicy" class="aero-accordion-drawer-title">
				    <h3 class="npcButton">
				    <s:if test="%{jumpFromIDM}">
					<a class="btCurrent" href="javascript:void(0);" style="float: right; margin:0px 25px;">
						<span id="uploadDoneSpanId" class="hidden" style="padding-bottom: 5px;padding-top: 0px;height: 25px;">Done</span></a>
					</s:if>
				    3 - Configure and Update Devices</h3>
				</div>
			</li>
		</ul>
	</div>
	<div class="footer"><label class="text">&nbsp;</label></div>
</div>

<script type="text/javascript">
<s:if test="%{!hideAccessMessage}">
function showAccessMessageDialog(info)
{
	if (infoDialog == null)
	{
		infoDialog = new YAHOO.widget.SimpleDialog("infoDlg", {
		width: "450px",
		fixedcenter:true,
		modal:true,
	    visible:false,
		draggable:true,
		constraintoviewport: true,
		icon:"",
		buttons: [ { text:"&nbsp;OK&nbsp;", handler:handleNo, isDefault:true } ]});
		infoDialog.setHeader("<s:text name="info.home.start.here.access.popup.head"/>");
		infoDialog.render(document.body);
	}

	infoDialog.cfg.setProperty("text",info);
	infoDialog.show();
}

function decode(text)
{
return text.replace(/&amp;/g, '&').replace(/&quot;/g, '/"').replace(/&lt;/g, '<').replace(/&gt;/g, '>');
}
function onLoadPage() {
	var accessMessage='<s:property value="%{accessMessage}"/>';
	if(accessMessage)
	{
		showAccessMessageDialog(decode(accessMessage));
	}
}
</s:if>
	function getSingleSelectId(elementName) {
		var checkedItems = hm.util.getSelectedCheckItems(elementName);
		if (checkedItems == hm.util._LIST_SELECTION_NOITEM || checkedItems == hm.util._LIST_SELECTION_NOSELECTION) {
			return null;
		} else {
			return checkedItems[0];
		}
	}

	function showLeftMenu() {
	    sizeLeftMenu();
	}

	var ErrorShownHelper = (function(elName, elClassName) {
		var errNoteElName = elName;
		var oriErrNoteElName = elName;
		var noteTimeIdDic = {};
		var hideErrNotesErrorHelper = function (elName) {
			return function() {
				hm.util.wipeOut(elName, 800);
			}
		}
		return {
			showError : function(errMsg, elName1) {
				var oldNoteElName = this.errNoteElName;
				if (elName1) {
					this.errNoteElName = elName1;
				} else {
					this.errNoteElName = oriErrNoteElName;
				}
				if (noteTimeIdDic[this.errNoteElName]) {
					clearTimeout(noteTimeIdDic[this.errNoteElName]);
				}
				if (Get(this.errNoteElName)) {
					hm.util.show(this.errNoteElName);
					Get(this.errNoteElName).className=elClassName;
					Get(this.errNoteElName).innerHTML=errMsg;
					noteTimeIdDic[this.errNoteElName] = setTimeout(hideErrNotesErrorHelper(this.errNoteElName), 10000);
				}
			}
		}
	})("errNote", "noteError");

</script>

<script type="text/javascript">
var DEVICE_TYPE_BRANCH_ROUTER = <%=HiveAp.Device_TYPE_BRANCH_ROUTER%>;

	var yDom = YAHOO.util.Dom, yEvent = YAHOO.util.Event;
	var Lang = YAHOO.util.Lang;
	var accordionView;
	var subDrawerOperation;
	var subDrawerCloneOperation;
	var hasOpenedSubDrawer = false;
	var hasLeftNetworkPolicySelect = false;
	var initialNetworkPolicyTitle = "1 - Choose Network Policy";
	//var npWirelessRoutingEnabled = false;
	var npIdStr = '<s:property value="networkPolicyId4Drawer" />';
	var npId4Drawer = -1;
	if (npIdStr != null && npIdStr != '') {
		npId4Drawer = parseInt(npIdStr);
	}
	var networkPolicyCallbackFn;
	var need2SaveNetworkPolicy; // flag for save the profile
	//override this function if you want to use the function of doContinueOper()
	var doNetworkPolicyContinueOper = null;
	var currentNetworkPolicyIdValue = null;
	var doContinueOperOri = function(){
		if (doNetworkPolicyContinueOper !== null && hm.util.isAFunction(doNetworkPolicyContinueOper)) {
			doNetworkPolicyContinueOper();
			doNetworkPolicyContinueOper = null;
		}
	}

	function doContinueOper() {
		doContinueOperOri();
	}
	function restoreDoContinueOper() {
		doContinueOper = doContinueOperOri;
	}

	yEvent.onDOMReady(function() {
<%-- hide from server side, avoid re-layout on page rendering.
	    // hide the left menu bar
		//hideSlider();
--%>
		// change the body bg
		yDom.setStyle(document.body, "background-color", "#fff");

		var spadIds = ["netWorkPolicySpanId"];
		yEvent.on(spadIds, "click", function(e){
			saveNetworkPolicyGui(this.id, e, 'cofigurePolicy'); // click the 'Continue' button
		});
		yEvent.on("netWorkPolicySaveSpanId", "click", function(e){
			saveNetworkPolicyGui(this.id, e); // click the 'Save' button
		});

		accordionView = new YAHOO.aerohive.widget.AccordionView("accordionDiv",
				{width: '1000',
				enableLoader: true,
				<s:if test="%{networkPolicyId4Drawer > 0}">
				expandItem: 1,
				</s:if>
				drawers: initDrawersProperties([{id: 'selectNetWorkPolicy', height: '355', cache: true},
				  		                      {id: 'netWorkPolicy', height: '500'},
						                      {id: 'cofigurePolicy', height: '400'}]),
               	animate: {speed: 0.7, effect: YAHOO.util.Easing.easeBoth}
				});

		accordionView.appendChildren('selectNetWorkPolicy', [{id: 'sub_selectNetWorkPolicy',
			disableOnclick: true,
			afteropened: afterSelectNetworkSubDrawerOpenedEvent,
			afterclosed: afterSelectNetworkSubDrawerClosedEvent}]);
		accordionView.appendChildren('netWorkPolicy', [{id: 'sub_NetworkPolicy', height: '500',
			disableOnclick: true,
			afteropened: afterNetworkSubDrawerOpenedEvent,
			afterclosed: afterNetworkSubDrawerClosedEvent}]);
		accordionView.appendChildren('cofigurePolicy', [{id: 'sub_cofigurePolicy',
			disableOnclick: true}]);
	});
	var drawerMouseOverEvent = function(e) {
		debugLog("fire the mouse over event:"+e);
	}
	var beforeDrawerOpenEvent = function(arg) {
		debugLog("fire the before open event:"+Lang.dump(arg));

		// auto save Network Policy profile
		if(arg.Opened == 'netWorkPolicy') {
			if(need2SaveNetworkPolicy) {
				saveNetworkPolicyGui(arg.Opened, null, arg.toOpen);
				return false;
			}
		}

		if(arg.toOpen !== 'selectNetWorkPolicy') {
			hasLeftNetworkPolicySelect = true;
			//if(arg.toOpen == 'netWorkPolicy') {
				if (hm.util.getSelectedCheckItems("networkPolicys")==hm.util._LIST_SELECTION_NOITEM){
					warnDialog.cfg.setProperty('text', "The network policy list is empty.");
					warnDialog.show();
					return false;
				} else if (hm.util.getSelectedCheckItems("networkPolicys")==hm.util._LIST_SELECTION_NOSELECTION){
					warnDialog.cfg.setProperty('text', "Please choose network policy first.");
					warnDialog.show();
					return false;
				}

				// change the SelectNetworkPolicy title
				replaceSelectNetworkPolicyTitle(initialNetworkPolicyTitle, getSelectedNetworkPolicyName());
			//}
		}
		if(arg.toOpen == 'selectNetWorkPolicy') {
			if (npIdStr != '-1') {
				var hrefStr = window.location.href;
				hrefStr = hrefStr.replace(/\?networkPolicyId4Drawer=[0-9]*$/g, '');
				hrefStr = hrefStr.replace(/\?networkPolicyId4Drawer=[0-9]*&/g, '?');
				hrefStr = hrefStr.replace(/&networkPolicyId4Drawer=[0-9]*/g, '');
				window.location.href = hrefStr;
				return false;
			}
			debugLog("clear the select NetWork html");
			Get(accordionView.getDrawerContentId('selectNetWorkPolicy')).innerHTML = "";
			// avoid the dialog to effect the selection for network policy
			hideSubDialogOverlay();
			// reset the SelectNetworkPolicy title
			replaceSelectNetworkPolicyTitle(currentNetworkPolicyTitle, initialNetworkPolicyTitle);
			restoreDoContinueOper();
		}
		if(arg.toOpen == 'netWorkPolicy') {
			restoreDoContinueOper();
		}
		if(arg.Opened == 'cofigurePolicy') {
			if (timeoutId) {
				window.clearTimeout(timeoutId);
			}
		}
		return true;
	}
	var afterDrawerOpenedEvent = function (drawer) {
		// show the loading icon
		accordionView.showLoadingPanel(Get(accordionView.getDrawerContentId(drawer.id)));

		debugLog("fire the after opened event:"+drawer.id);
		fetchDynamicHTML(drawer.id);

		if(Get(drawer.id+"SpanId")) {
			yDom.replaceClass(Get(drawer.id+"SpanId"), "hidden", "inline");
		}
		if(Get(drawer.id+"SaveSpanId")) {
			yDom.replaceClass(Get(drawer.id+"SaveSpanId"), "hidden", "inline");
		}
	}
	var afterDrawerClosedEvent = function (drawer) {
		debugLog("fire the after closed event, id:"+Lang.dump(drawer));
		if(Get(drawer.id+"SpanId")) {
			yDom.replaceClass(Get(drawer.id+"SpanId"), "inline", "hidden");
		}
		if(Get(drawer.id+"SaveSpanId")) {
			yDom.replaceClass(Get(drawer.id+"SaveSpanId"), "inline", "hidden");
		}
		Get("btNetworkSave").style.display="none";
		// For IDM flow
		if(Get("uploadDoneSpanId")) {
			yDom.replaceClass(Get("uploadDoneSpanId"), "inline", "hidden");
		}
	}
	function initDrawersProperties(drawers) {
		var drawerArray = new Array();
		for(var i = 0; i < drawers.length ; i++) {
			drawerArray.push({id: drawers[i].id, height: drawers[i].height,
				cache: Lang.isUndefined(drawers[i].cache) ? false : drawers[i].cache,
				autoheight: Lang.isUndefined(drawers[i].autoheight) ? false : drawers[i].autoheight,
				/*mouseover: drawerMouseOverEvent,*/
				beforeopen: beforeDrawerOpenEvent,
				afteropened: afterDrawerOpenedEvent,
				afterclosed: afterDrawerClosedEvent
				});
		}
		return drawerArray;
	}

	function saveNetworkPolicyGui(request, e, targetDrawerId){
		
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=saveNetworkPolicyGui" + "&id=" + currentNetworkPolicyIdValue + "&ignore="+new Date().getTime();
		if (!targetDrawerId) {
			url = url + "&saveAlways=true";
		}
		if(notesTimeoutId) {
			clearTimeout(notesTimeoutId);
			hm.util.hide("errNoteForAllNetwork");
		}
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
		{success : succSaveNetworkPolicyGui, failure : resultDoNothing, argument : {req: request, target: targetDrawerId}, timeout: 60000}, null);
		hm.util.stopBubble(e);
	}

	function succSaveNetworkPolicyGui(o) {
		eval("var details = " + o.responseText);
		if (details.t) {
			// clear the flag after save the Network Policy
			need2SaveNetworkPolicy = false;
			if(o.argument.target) {
				YAHOO.util.UserAction.click(Get(o.argument.target));
			} else {
				Get("errNoteForAllNetwork").className="noteInfo";
				Get("errNoteForAllNetwork").innerHTML="The Network Policy was updated successfully.";
				hm.util.show("errNoteForAllNetwork");
				var notesTimeoutId = setTimeout("hm.util.wipeOut('errNoteForAllNetwork', 800);", 10000);
			}
		} else {
			Get("errNoteForAllNetwork").className="noteError";
			Get("errNoteForAllNetwork").innerHTML=details.m;
			hm.util.show("errNoteForAllNetwork");
			var notesTimeoutId = setTimeout("hm.util.wipeOut('errNoteForAllNetwork', 800);", 30000);
		}
	}

	function continueOperation(request, event) {

		if(strContains(request, 'selectNetWorkPolicy')) {
			YAHOO.util.UserAction.click(Get('netWorkPolicy'));
		} else if (strContains(request, 'netWorkPolicy')) {
			YAHOO.util.UserAction.click(Get('cofigurePolicy'));
		}
		// stop bubble
		hm.util.stopBubble(event);
	}

	function doFinishSelectNetworkPolicy() {
		continueOperation('selectNetWorkPolicySpanId');
	}

	function afterSelectNetworkSubDrawerOpenedEvent(drawer) {
		debugLog("afterSelectNetworkSubDrawerOpenedEvent"+drawer.id);
		if(Get("selectNetWorkPolicySpanId")) {
			yDom.replaceClass(Get("selectNetWorkPolicySpanId"), "inline", "hidden");
		}
		hasOpenedSubDrawer = true;
	}
	function afterSelectNetworkSubDrawerClosedEvent(drawer) {
		debugLog("afterSelectNetworkSubDrawerClosedEvent");
		if(Get("selectNetWorkPolicySpanId")) {
			yDom.replaceClass(Get("selectNetWorkPolicySpanId"), "hidden", "inline");
		}
		hasOpenedSubDrawer = false;
	}

	function afterNetworkSubDrawerOpenedEvent(drawer) {
		debugLog("afterNetworkSubDrawerOpenedEvent"+drawer.id);
		if(Get("netWorkPolicySpanId")) {
			yDom.replaceClass(Get("netWorkPolicySpanId"), "inline", "hidden");
		}
		if(Get("netWorkPolicySaveSpanId")) {
			yDom.replaceClass(Get("netWorkPolicySaveSpanId"), "inline", "hidden");
		}
		hasOpenedSubDrawer = true;
		window.scrollTo(1,1);
	}
	function afterNetworkSubDrawerClosedEvent(drawer) {
		debugLog("afterNetworkSubDrawerClosedEvent");
		if(Get("netWorkPolicySpanId")) {
			yDom.replaceClass(Get("netWorkPolicySpanId"), "hidden", "inline");
		}
		if(Get("netWorkPolicySaveSpanId")) {
			yDom.replaceClass(Get("netWorkPolicySaveSpanId"), "hidden", "inline");
		}
		hasOpenedSubDrawer = false;
	}

	function fetchDynamicHTML(request) {
		if('netWorkPolicy' == request) {
			if (npId4Drawer > -1) {
				fetchNetworkPolicy2PageWithSelectedItemBE('<s:property value="networkPolicyId4Drawer" />');
				fetchConfigTemplate2Page(false,'<s:property value="networkPolicyId4Drawer" />');
				npId4Drawer = -1;
			} else {
				var selecteds = hm.util.getSelectedCheckItems("networkPolicys");
				fetchConfigTemplate2Page(false,selecteds[0]);
			}
		}else if('cofigurePolicy' == request){
			fetchHiveApsListPage();
		} else if ('selectNetWorkPolicy' == request) {
			//prepare page for select network policy
			fetchNetworkPolicy2Page();
		}
	}

	function fetchNetworkPolicy2Page() {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicySelect"
			 + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkPolicySelect, failure : resultDoNothing, timeout: 60000}, null);
	}
	var flowControls = {};
	function setGotoNetworkPolicyDrawer() {
		flowControls.gotoNetworkPolicyDrawer = true;
	}
	function doGotoNetworkPolicyDrawer() {
		if (flowControls
				&& flowControls.gotoNetworkPolicyDrawer == true) {
			flowControls.gotoNetworkPolicyDrawer = false;
			doFinishSelectNetworkPolicy();
		}
	}
	var succFetchNetworkPolicySelect = function(o) {
		set_innerHTML(accordionView.getDrawerContentId('selectNetWorkPolicy'),
				o.responseText);
		// hide the loading icon
		accordionView.hideLoadingPanel();
		setTimeout(function() { doGotoNetworkPolicyDrawer();}, 500);
	}
	function fetchNetworkPolicy2PageWithSelectedItem(itemId) {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicySelect"
			 + "&selectedNetworkPolicyId="+itemId
			 + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkPolicySelect, failure : resultDoNothing, timeout: 60000}, null);
	}
	function fetchNetworkPolicy2PageWithSelectedItemBE(itemId) {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicySelect"
			 + "&selectedNetworkPolicyId="+itemId
			 + "&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkPolicySelectBE, failure : resultDoNothing, timeout: 60000}, null);
	}
	var succFetchNetworkPolicySelectBE = function(o) {
		set_innerHTML(accordionView.getDrawerContentId('selectNetWorkPolicy'),
				o.responseText);
		YAHOO.util.Event.onContentReady('networkPolicySelectListTable', succFetchNetworkPolicySelectBESetTitle, this);
	}
	function succFetchNetworkPolicySelectBESetTitle() {
		replaceSelectNetworkPolicyTitle(initialNetworkPolicyTitle, getSelectedNetworkPolicyName());
	}

	function fetchConfigTemplate2Page(refreshFlg, id, params) {
		if (id && id!=null && id>0) {
			currentNetworkPolicyIdValue = id;
		}
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=edit"
		 + "&ignore="+new Date().getTime();
		 if (refreshFlg) {
		 	url = url + "&refreshFlg=" + refreshFlg;
		 }
		 if (id) {
		 	url = url + "&id="+ id;
		 }
		 if(params) {
			 url += params;
		 }
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchPolicy, failure : resultDoNothing, timeout: 60000}, null);
	}

	var resultDoNothing = function(o) {
	//	alert("failed.");
	};

	var succFetchPolicy = function (o){
		try {
			eval("var details = " + o.responseText);
		}catch(e){
			// set overflow as hidden
			//yDom.addClass(accordionView.getDrawerContentId('netWorkPolicy'), 'hiddenOverFlow');
			yDom.setStyle(Get(accordionView.getDrawerContentId('netWorkPolicy')), 'overflowY', 'hidden');
			// to avoid the first open overlapped
			yDom.setStyle(Get(accordionView.getDrawerContentId('netWorkPolicy')), 'marginBottom', '50px');

			set_innerHTML(accordionView.getDrawerContentId('netWorkPolicy'),
				o.responseText);
			backNetWorkPolicy();
			notesTimeoutId = setTimeout("hideNotes()", 10000);

			// calculate the height
			yEvent.onContentReady(accordionView.getDrawerContentId('netWorkPolicy'), function() {
				setTimeout(recalculateDrawerHeight(this.id), 1000);
			});

			// set the flag: need to save the Network Policy
			need2SaveNetworkPolicy = true;
			return;
		}
		if (details.e){
			alert(details.e);
		}
	}

	function recalculateDrawerHeight(drawId) {
		return function() {
	        // hide the loading icon
	        accordionView.hideLoadingPanel();

	        var el = Get(drawId);
	        var sel = YAHOO.util.Selector;
	        var nodes = sel.query('div#content form table', el);
	        if(nodes.length <= 0 || YAHOO.env.ua.chrome) {
	            yDom.setStyle(Get(drawId), 'height', 'auto');
	        } else {
	            var table = nodes[0];
	            //yDom.setStyle(Get(drawId), 'height', table.offsetHeight+10+'px');
	            yDom.setStyle(Get(drawId), 'height', '');
	        }
	        // remove hidden overflow attr
	        //yDom.removeClass(drawId, 'hiddenOverFlow');
	        yDom.setStyle(Get(drawId), 'overflowY', '');
		}
	}

	function fetchHiveApsListPage() {
		if ((subOperation == 'create2' || subOperation == 'update2' || subOperation == 'multiUpdate') && document.getElementById("filterSelect")) {
			filterChanged(document.getElementById("filterSelect").value, 1);
		} else {
			var selecteds = hm.util.getSelectedCheckItems("networkPolicys");
			var url = "<s:url action='hiveAp' includeParams='none' />?operation=manageAPGuid"+
					"&hmListType=manageAPGuid"+
					"&jsonMode=true"+
					"&selectNetworkPolicyId="+selecteds[0]+
					"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchHiveApsList, failure : resultDoNothing, timeout: 60000}, null);
		}
	}

	function initialCWPSubDrawerTitle (text) {
		var title = "<h4 class='npcButtonTitle'>" +
		'<a class="btCurrent" href="javascript:void(0);" id="btSubNetworkSave" style="float: right; display:none;"><span class="buttonLink" id="subNetWorkPolicySpanId_save" style="padding-bottom: 2px;padding-top: 0px;" onclick="saveSubNetWorkPolicy();">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span class="buttonLink" id="subNetWorkPolicySpanId_cacel" style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="backNetWorkPolicy();">Cancel</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span class="buttonLink" id="subNetWorkPolicySpanId_export" style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="exportDefaultCWP();">Export</span></a>' +
		'<span class="subdrawer_title">' + text + "</span>" + "</h4>";
		return title;
	}

	function initialSubDrawerTitle (text) {
		var title = "<h4 class='npcButtonTitle'>" +
		'<a class="btCurrent" href="javascript:void(0);" id="btSubNetworkSave" style="float: right; display:none;"><span class="buttonLink" id="subNetWorkPolicySpanId_save" style="padding-bottom: 2px;padding-top: 0px;" onclick="saveSubNetWorkPolicy();">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span class="buttonLink" id="subNetWorkPolicySpanId_cacel" style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="backNetWorkPolicy();">Cancel</span></a>' +
		'<span class="subdrawer_title">' + text + "</span>" + "</h4>";
		return title;
	}

	function initialSelectNPSubDrawerTitle (text) {
		var title = "<h4 class='npcButtonTitle'>" +
		'<a class="btCurrent" href="javascript:void(0);" id="btSubSelectNPSave" style="float: right; display:none;"><span class="buttonLink" id="selectNetWorkPolicySpanId_save" style="padding-bottom: 2px;padding-top: 0px;" onclick="saveSubSelectNetworkPolicy();">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span class="buttonLink" id="selectNetWorkPolicySpanId_cancel" style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="cancelSelectNPSubDrawerFromButton();">Cancel</span></a>' +
		'<span class="subdrawer_title">' + text + "</span>" + "</h4>";
		return title;
	}

	function showHideSelectNetworkPolicySubSaveBT(bln){
		if (bln) {
			Get("btSubSelectNPSave").style.display='';
		} else {
			Get("btSubSelectNPSave").style.display='none';
		}
	}
	function openSelectNPSubDrawer(request) {
		accordionView.setSubDrawerTitle('selectNetWorkPolicy', 'sub_selectNetWorkPolicy', initialSelectNPSubDrawerTitle(request.title));
		accordionView.expandSubDrawer('selectNetWorkPolicy', 'sub_selectNetWorkPolicy');
		var subDrawerContentId = accordionView.getSubDrawerContentId('selectNetWorkPolicy', 'sub_selectNetWorkPolicy');
		set_innerHTML(subDrawerContentId, request.responseText);
	}
	function cancelSelectNPSubDrawer() {
		accordionView.collapseSubDrawer('selectNetWorkPolicy', 'sub_selectNetWorkPolicy');
		subDrawerOperation = '';
		initializeCurrentHelpLinkUrl();
	}
	function cancelSelectNPSubDrawerFromButton() {
		cancelSelectNPSubDrawer();
		if (curNetworkNewOrModifyType == '1') {
			showSelectNetworkPolicyPanel();
		} else if (curNetworkNewOrModifyType == '2') {
			showModifyNetworkPolicyPanel();
		}
		initializeCurrentHelpLinkUrl();
	}
	function saveSubSelectNetworkPolicy() {
		if(subDrawerOperation == 'createHiveProfile') {
			saveHiveProfileJSON2('create');
		} else if(subDrawerOperation == 'updateHiveProfile') {
			saveHiveProfileJSON2('update');
		}
	}

	function openSubPanel(request) {
		accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle(request));
		accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	}
	function backNetWorkPolicy() {
		accordionView.collapseSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
		subDrawerOperation = '';
		subDrawerCloneOperation='';
		if(networkPolicyCallbackFn) {
			networkPolicyCallbackFn();
			networkPolicyCallbackFn = null;
		}
		destroyPanels();
		initializeCurrentHelpLinkUrl();
	}
	function destroyPanels() {
		// destroy the panels which using the original close button from YUI
		if(typeof(subnetOverlay) != "undefined" && subnetOverlay) {
			try{
				subnetOverlay.destroy();
			} catch(err) {
				// dump, for the this.cfg is null in sometimes.
				//console.error(err.description);
			}
		}
	}
	function saveSubNetWorkPolicy() {
		//accordionView.collapseSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
		if(subDrawerOperation == 'createSSID') {
			saveSsidToNetWorkPolicy('create');
		} else if(subDrawerOperation == 'updateSSID') {
			saveSsidToNetWorkPolicy('update');
		} else if (subDrawerOperation == 'updateMgtAdvancesSetting'){
			saveMgtAdvancedSettingToNetWorkPolicy();
		} else if(subDrawerOperation == 'createVpnService'){
			saveVpnServiceToNetWorkPolicy('create');
		} else if(subDrawerOperation == 'updateVpnService'){
			saveVpnServiceToNetWorkPolicy('update');
		} else if(subDrawerOperation == 'createCWP') {
			saveCwpForSSID();
		} else if(subDrawerOperation == 'editCWP') {
			editCWP();
		} else if (subDrawerOperation=='createVpnNetwork') {
			saveVpnNetworkForSsid('create');
		} else if (subDrawerOperation=='updateVpnNetwork') {
			saveVpnNetworkForSsid('update');
		} else if (subDrawerOperation=='createNetworkObject') {
			saveNetworkForMapping('create');
		} else if (subDrawerOperation=='updateNetworkObject') {
			saveNetworkForMapping('update');
		} else if(subDrawerOperation == 'createScheduler') {
			saveSchedulerForSSID();
		} else if(subDrawerOperation == 'editScheduler'){
			editScheduler();
		} else if(subDrawerOperation == 'createUserProfile') {
			saveUserProfileJSON2('create');
		} else if(subDrawerOperation == 'updateUserProfile') {
			saveUserProfileJSON2('update');
		} else if(subDrawerOperation == 'createPortTemplate') {
			savePortTemplate('create');
		} else if(subDrawerOperation == 'updatePortTemplate') {
			savePortTemplate('update');
		} else if (subDrawerOperation =='createLocalUserGroup') {
			saveLocalUserGroup('create');
		} else if (subDrawerOperation =='updateLocalUserGroup') {
			saveLocalUserGroup('update');
		} else if (subDrawerOperation == 'createVLAN') {
			saveVlanFromDrawer('create');
		} else if (subDrawerOperation == 'updateVLAN') {
			saveVlanFromDrawer('update');
		} else if(subDrawerOperation == 'createPortAccess') {
			savePortAccessProfile('create');
		} else if(subDrawerOperation == 'updatePortAccess') {
            savePortAccessProfile('update');
        }
	}

	////////=========change the 'SelectNetworkPolicy' title: start=================///////
	var currentNetworkPolicyTitle = initialNetworkPolicyTitle;
	function replaceSelectNetworkPolicyTitle(subStr, newString) {
		var title = Get('selectNetWorkPolicy');
		if(title) {
			var titleText = title.innerHTML;
			title.innerHTML = titleText.replace(subStr, newString);
			currentNetworkPolicyTitle = newString;
			// rebind the click event
			yEvent.on("selectNetWorkPolicySpanId", "click", function(e){continueOperation(this.id, e);});

			// specific the 'Selected Network Policy' title
			var liElement = Get('li_selectNetWorkPolicy');
			var headerElement = Get('selectedHeader');
			if(currentNetworkPolicyTitle == initialNetworkPolicyTitle) {
				yDom.removeClass(liElement, 'selectedNetworkPolicy');
				yDom.removeClass(headerElement, 'selectedNetworkPolicy');
			} else {
				if(!yDom.hasClass(liElement, 'selectedNetworkPolicy')) {
					yDom.addClass(liElement, 'selectedNetworkPolicy');
				}
				if(!yDom.hasClass(headerElement, 'selectedNetworkPolicy')) {
					yDom.addClass(headerElement, 'selectedNetworkPolicy');
				}
			}
		}
	}
	function getSelectedNetworkPolicyName() {
		var titleText = initialNetworkPolicyTitle;
		var selectedId = hm.util.getSelectedCheckItems("networkPolicys");
		var items = document.getElementsByName("networkPolicys");
		for(var i=0; i<items.length; i++) {
			if(items[i].checked && items[i].value == selectedId) {
				var cellItem = yDom.getNextSibling(items[i].parentNode);
				var spanItems = cellItem.getElementsByTagName("span");
				var textItem;
				for(var j=0; j<spanItems.length; j++) {
					if(spanItems[j].className == 'word-wrap' /*'ellipsis'*/) {
						textItem = spanItems[j];
						break;
					}
				}
				if(null == textItem) {textItem.innerHTML = 'Unknown';}
				titleText = "1 - Configure Network Policy - " + textItem.innerHTML;
				break;
			}
		}
		return titleText;
	}
	////////=========change the 'SelectNetworkPolicy' title: end=================///////

	////////=========common=================///////
	/**
	 * check if contains charaters in string
	 */
	function strContains(str, sub){
	 	return (str.indexOf(sub) != -1);
	}
	function debugLog(msg) {
		 //console.debug(msg);
	}

	function showHideNetworkPolicySubSaveBT(bln){
		if (bln) {
			Get("btSubNetworkSave").style.display='';
		} else {
			Get("btSubNetworkSave").style.display='none';
		}
	}

	function showHideNetworkPolicySaveBT(bln){
		if (bln) {
			Get("btNetworkSave").style.display='';
		} else {
			Get("btNetworkSave").style.display='none';
		}
	}

	/**
	 * adjust the Edit link in list dialog for IE (when scrollbar is visible)
	 */
	var adjustEditLink = function () {
		var container = document.getElementById(this.id);
		// handle IE7- after the meta element "X-UA-Compatible" set as "IE=9"
		if(YAHOO.env.ua.ie && YAHOO.env.ua.ie < 8) {
			var scrollbarWidth = container.offsetWidth - container.clientWidth;
			//alert("offsetWidth:"+container.offsetWidth+" clientWidth:"+container.clientWidth+" scrollWidth:"+container.scrollWidth+" scrollbarWidth:"+scrollbarWidth);
			if(isNaN(scrollbarWidth)) {
				//dump
			} else {
				if(container.scrollWidth == container.clientWidth) {
					// dump, handle Maxton like browser which has very wired rendering model
				} else if(scrollbarWidth > 0) {
					scrollbarWidth = scrollbarWidth + 8;
					// adjust the edit span
					var spanElements = container.getElementsByTagName("span");
					for(var index = 0; index < spanElements.length; index++) {
						if(spanElements[index].className == 'editText'){
							spanElements[index].style.paddingRight = scrollbarWidth + "px";
						}
					}
				}
			}
		}
		// add scroll event for the list dialog
		hm.util.addMouseScrollAction(container, hm.util.clearMenus);
	}
	////////=========common=================///////
</script>

<!-- Port Profile : start -->
<tiles:insertDefinition name="portScript4NetworkPolicy" />
<!-- Port Profile : end -->

<!--        fnr code begin  -->
<script>
var subDialogOverlay = null;
YAHOO.util.Event.onDOMReady(initDialogPanel);
function initDialogPanel() {
// create Dialog overlay
	var div = document.getElementById('subDialogOverlay');
	subDialogOverlay = new YAHOO.widget.Panel(div, {
		width:"340px",
		visible:false,
		//fixedcenter:true,
		close: false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		underlay: "none",
		zIndex:1
		});

	//Allow escape key to close box
	var escListener = new YAHOO.util.KeyListener(document, { keys:27},
	  		  { fn:hideSubDialogOverlay,
	     			scope:subDialogOverlay,
	     			correctScope:true } );
	subDialogOverlay.cfg.queueProperty("keylisteners", escListener);

	subDialogOverlay.render(document.body);
	div.style.display = "";
}
function openSubDialogOverlay(width){
	if(null != subDialogOverlay){
		if (Get("hdDivImg")) {
			Get("hdDivImg").style.display = "none";
		}
		if (!YAHOO.util.Dom.hasClass("hdDivSpan", "npcHead1")) {
			YAHOO.util.Dom.addClass("hdDivSpan", "npcHead1")
		}
		if (YAHOO.util.Dom.hasClass("hdDivSpan", "dialogTitle")) {
			YAHOO.util.Dom.removeClass("hdDivSpan", "dialogTitle")
		}
		if(width) {
			subDialogOverlay.cfg.setProperty('width', width);
			YAHOO.util.Dom.setStyle(["topMidDiv", "bottomMidDiv"], "width", width);
		} else {
			subDialogOverlay.cfg.setProperty('width', "340px");
			YAHOO.util.Dom.setStyle(["topMidDiv", "bottomMidDiv"], "width", "370px");
		}
		subDialogOverlay.center();
		subDialogOverlay.cfg.setProperty('visible', true);
		hm.util.show("spaceEmptyRow");
	}
}
function openSubDialogOverlayWithTitleImg(imgSrc){
	if(null != subDialogOverlay){
		if (Get("hdDivImg")) {
			Get("hdDivImg").style.display = "";
			if(typeof imgSrc != undefined && imgSrc != null && imgSrc != '') {
				Get("hdDivImg").src = imgSrc;
			} else {
				Get("hdDivImg").src = "images/hm_v2/profile/hm-icon-users-big.png";
			}
		}
		if (YAHOO.util.Dom.hasClass("hdDivSpan", "npcHead1")) {
			YAHOO.util.Dom.removeClass("hdDivSpan", "npcHead1")
		}
		if (!YAHOO.util.Dom.hasClass("hdDivSpan", "dialogTitle")) {
			YAHOO.util.Dom.addClass("hdDivSpan", "dialogTitle")
		}
		subDialogOverlay.center();
		subDialogOverlay.cfg.setProperty('visible', true);
		hm.util.show("spaceEmptyRow");
	}
}

function hideSubDialogOverlay(){
	if(null != subDialogOverlay){
		subDialogOverlay.cfg.setProperty('visible', false);
		Get("hdDivSpan").innerHTML="";
		Get("bdDiv").innerHTML="";
	}
	$(".ui-autocomplete").hide();
}

function addRemoveSsid(url){
	if (!url) {
		var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveSsidPage"
		 	+ "&ignore="+new Date().getTime();
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchSsidPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchSsidPage = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-ssid-big.png" width="40px" height="40px" '+
					'title="<s:text name="config.v2.select.ssid.profile.popup.title" />" class="dialogTitleImg" />'+
					'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.v2.select.ssid.profile.popup.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

function finishSelectSsid(){
	var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
	document.forms["ssidSelectPage"].operation.value = "finishSelectSsid";
	YAHOO.util.Connect.setForm(document.getElementById("ssidSelectPage"));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFinishSelectSsid, failure : resultDoNothing, timeout: 60000}, null);
}

var succFinishSelectSsid = function (o){
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		hideSubDialogOverlay();
		set_innerHTML(accordionView.getDrawerContentId('netWorkPolicy'),
				o.responseText);
		return;
	}
	var hideErrNotes = function () {
		hm.util.wipeOut('errNote', 800);
	}
	if(subDialogOverlay.cfg.getProperty('visible') == false) {
		openSubDialogOverlay();
	}
	hm.util.show("errNote");
	Get("errNote").className="noteError";
	Get("errNote").innerHTML=details.e;
	var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
};

function cloneSsid(ssidId) {
	var objArr = new Array();
	objArr.push(ssidId);
	subDrawerCloneOperation="cloneSSID";
	var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=clone&selectedIds=" + objArr +  "&blnJsonMode=true&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewSsid, failure : resultDoNothing, timeout: 60000}, null);

}

function newSsid(){
	subDrawerCloneOperation='';
	var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=new&blnJsonMode=true&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewSsid, failure : resultDoNothing, timeout: 60000}, null);
	networkPolicyCallbackFn = function() {
     	 var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveSsidPage"
 			+ "&ignore="+new Date().getTime();
		addRemoveSsid(url);
    }
}

var succNewSsid = function(o) {
	subDrawerOperation= "createSSID";

	hideSubDialogOverlay();
	// set the sub drawer title
	if (subDrawerCloneOperation=="cloneSSID") {
		accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone SSID"));
	} else {
		accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New SSID"));
	}
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// fetch the page
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function saveSsidToNetWorkPolicy(opera) {
	var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation="+ opera +"&blnJsonMode=true&ignore="+new Date().getTime();
	if(!validateSsidForJson(opera)){
		return false;
	}
	YAHOO.util.Connect.setForm(document.getElementById("ssidProfilesFull"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveSsid, failure : resultDoNothing, timeout: 60000}, null);
}

var succSaveSsid = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if (subDrawerOperation=='updateSSID') {
			succEditSsid(o);
		} else {
			succNewSsid(o);
		}
		return;
	}
	if (details.t) {
		// new ssid
		if (details.n){
			backNetWorkPolicy();
			networkPolicyCallbackFn=null;
			var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveSsidPage"
			 	+ "&createSsidId=" + details.id
			 	+ "&ignore="+new Date().getTime();
			addRemoveSsid(url);
		} else {
			fetchConfigTemplate2Page(true);
		}
	}
}

function editSsid(ssidId){
	var url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=edit&blnJsonMode=true&id=" + ssidId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditSsid, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditSsid = function(o) {
	subDrawerOperation = "updateSSID";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit SSID"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function showVlanMappingSelectDialog(){
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchAddRemoveVlanMappingSelect"
		+ "&blnJsonMode=true"
	 	+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchAddRemoveVlanMappingSelect, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchAddRemoveVlanMappingSelect = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-vlan-big.png" width="40px" height="40px" '+
	'title="<s:text name="config.vlanGroup.vlans" />" class="dialogTitleImg" />'+
	'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.vlanGroup.vlans" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

function removeSelectVlanMapping(vlanId){
	var succRemoveSelectVlanMapping = function (o){
		eval("var details = " + o.responseText);
		if (details.t) {
			refreshNetworkObjPage();
		}
	};

	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=removeSelectVlanMapping"
		+ "&blnJsonMode=true"
		+ "&vlanMappingId=" + vlanId
	 	+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succRemoveSelectVlanMapping, failure : resultDoNothing, timeout: 60000}, null);

}

function addRemoveUserGroups(parentType, parentId, target, newAddGroupId){
	edittingParentType=parentType;
	edittingSSID=parentId;
	edittingUserGroupTarget = target;
	var url = "";

	if(parentType == 'SSID') {
		url = "<s:url action='ssidProfilesFull' includeParams='none' />?operation=fetchAddRemoveUserGroup";
		if (newAddGroupId) {
			url = url + "&newAddGroupId=" + newAddGroupId;
		}
	} else {
		url = "<s:url action='portAccess' includeParams='none' />?operation=fetchAddRemoveUserGroup";
	}

	url = url + "&id="+parentId
		+ "&userGroupTarget="+target
		+ "&blnJsonMode=true"
	 	+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchUserGroupPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchUserGroupPage = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-Local_User_Groups-big.png" width="40px" height="40px" '+
	'title="<s:text name="config.title.localUserGroup" />" class="dialogTitleImg" />'+
	'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.title.localUserGroup" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

function finishSelectLocalUserGroup(){
	var url = "";
	var edittingFormName = 'userGroupSelectPage';

	if(edittingParentType == 'SSID') {
		url = "<s:url action='ssidProfilesFull' includeParams='none' />";
		edittingFormName = 'userGroupSelectPage';
	} else {
		url = "<s:url action='portAccess' includeParams='none' />";
		edittingFormName = 'userGroupSelectPage4Lan';
	}

	url = url + "?ignore=" + new Date().getTime()
		+ "&id="+edittingSSID+ "&userGroupTarget="+edittingUserGroupTarget;
	document.forms[edittingFormName].operation.value = "finishSelectLocalUserGroup";
	YAHOO.util.Connect.setForm(document.getElementById(edittingFormName));
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFinishSelectLocalUserGroup, failure : resultDoNothing, timeout: 60000}, null);
}

var hideErrNotes = function () {
	hm.util.wipeOut('errNote', 800);
}
function hideNotes() {
	hm.util.wipeOut('notes', 800);
}
function showProcessing() {
	hm.util.show('processing');
}

var succFinishSelectLocalUserGroup = function (o){
	eval("var details = " + o.responseText);
	if (details.t) {
		hideSubDialogOverlay();
		fetchConfigTemplate2Page(true);
	}else {
		if (details.e){
			hm.util.show("errNote");
			Get("errNote").className="noteError";
			Get("errNote").innerHTML=details.e;
			var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
		}
	}
};

function editLocalUserGroup(groupId){
	var url = "<s:url action='localUserGroup' includeParams='none' />?operation=edit&jsonMode=true&id=" + groupId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditLocalUserGroup, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditLocalUserGroup = function(o) {
	subDrawerOperation = "updateLocalUserGroup";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit Local User Group"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}


function newLocalUserGroup(){
	var url = "<s:url action='localUserGroup' includeParams='none' />?operation=new&jsonMode=true&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewLocalUserGoup, failure : resultDoNothing, timeout: 60000}, null);

}

var succNewLocalUserGoup = function(o) {
	subDrawerOperation= "createLocalUserGroup";
	hideSubDialogOverlay();
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New Local User Group"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// fetch the page
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function saveLocalUserGroup(opera) {
	var url = "<s:url action='localUserGroup' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	if(!validateLocalUserGroupForJson(opera)){
		return false;
	}
	document.forms["localUserGroup"].operation.value = opera;
	YAHOO.util.Connect.setForm(document.getElementById("localUserGroup"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveLocalUserGroup, failure : resultDoNothing, timeout: 60000}, null);
}

var succSaveLocalUserGroup = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if (subDrawerOperation=='updateLocalUserGroup') {
			succEditLocalUserGroup(o);
		} else {
			succNewLocalUserGoup(o);
		}
		return;
	}
	if (details.t) {
		backNetWorkPolicy();
		if (details.n) {
			addRemoveUserGroups(edittingParentType, edittingSSID, edittingUserGroupTarget, details.id);
		}
	}
}

function editMgtAdvancedSetting(){
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=editMgtAdvancedSetting&jsonMode=true" + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditMgtAdvancedSetting, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditMgtAdvancedSetting = function(o) {
	subDrawerOperation = "updateMgtAdvancesSetting";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Additional Settings"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function saveMgtAdvancedSettingToNetWorkPolicy() {
	var url = "<s:url action='networkPolicy' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	if(!validateMgtAdvancedForJson('saveMgtAdvancedSetting')){
		return false;
	}
	document.forms["networkPolicyMgtAdvancedSetting"].operation.value = 'saveMgtAdvancedSetting';
	YAHOO.util.Connect.setForm(document.getElementById("networkPolicyMgtAdvancedSetting"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveMgtAdvancedSetting, failure : resultDoNothing, timeout: 60000}, null);
}

var succSaveMgtAdvancedSetting = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		succEditMgtAdvancedSetting();
		return;
	}
	if (details.t) {
		backNetWorkPolicy();
	}
}

function editVpnNetwork(vpnNetWorkId) {
	var url = "<s:url action='vpnNetworks' includeParams='none' />?operation=edit&contentShowType=subdrawer&jsonMode=true&id=" + vpnNetWorkId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditVpnNetwork, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditVpnNetwork = function(o) {
	subDrawerOperation = "updateVpnNetwork";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit Network"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function saveVpnNetworkForSsid(operation) {
	var url = "<s:url action='vpnNetworks' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	if(!validateNetworkForJson(operation)){
		return false;
	}
	document.forms["vpnNetworks"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("vpnNetworks"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveVpnNetwork, failure : resultDoNothing, timeout: 60000}, null);
}

var succSaveVpnNetwork = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if(subDrawerOperation=='createVpnNetwork') {
			newVPNNetwork(o);
		} else {
			succEditVpnNetwork(o);
		}
		return;
	}
	if (details.t) {
		backNetWorkPolicy();
		// new ssid
		if(details.newState) {
			selectNetworks4Lan(details.lanId);
		} else {
			fetchConfigTemplate2Page(true);
		}
	}
}

/*=========================Begin Select VPN Profile========================  */
/*Click the select button  */
function selectVpnBtn(url){
	if(!url){
		url = "<s:url action='networkPolicy' includeParams='none' />?operation=selectVpnProfile"
		 	+ "&ignore="+new Date().getTime();
	}
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchVpnProfiles, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchVpnProfiles = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-vpn-big.png" width="40px" height="40px" '+
					'title="<s:text name="config.vpn.dialog.title" />" class="dialogTitleImg" />'+
					'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.vpn.dialog.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

function editVpnService(vpnServiceId,blnRouter){
	var url = "<s:url action='vpnServices' includeParams='none' />?operation=edit&jsonMode=true&id=" + vpnServiceId +
			"&wirelessRoutingEnabled=" + blnRouter +
			"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditVpnService, failure : resultDoNothing, timeout: 60000}, null);
}

var succEditVpnService = function(o) {
	subDrawerOperation = "updateVpnService";
	// set the sub drawer title
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit VPN Service"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId,o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

/* ========================End Select VPN Profile============================ */

//=====================firewall policy begin========================
function selectFirewallPolicy(fwId) {
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchSelectFwPolicyPage";
	if (null != fwId) {
		url += "&selectFwPolicyId="+fwId;
	}
	url += "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchFwPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchFwPage = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-firewall-big.png" width="40px" height="40px" '+
					'title="<s:text name="config.firewall.policy.list.title" />" class="dialogTitleImg" />'+
					'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.firewall.policy.list.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

var succFinishSelectFw = function (o){
	hideSubDialogOverlay();
	closeIFrameDialog();
	var htmlStr = Get("bonjourGwTd").innerHTML.trim();
	eval("var data = " + o.responseText);
	if (data.result) {
		Get("fwPolicyNameTd").innerHTML = '<table cellspacing="0" cellpadding="0" border="0">'
			+'<tr>'
			+'<td class="imageTd" valign="middle"><img src="<s:url value="/images/hm_v2/profile/HM-icon-firewall.png" />" width="30" height="30" alt="firewall" title="firewall" /></td>'
			+'<td style="padding-left: 5px;vertical-align: top;"><a class="npcLinkA" href="javascript:void(0);" onclick="editFirewallPolicyInPanel('+data.fwId+')">'
			+ data.fwName +'</a></td>'
			+'</tr>'
			+'</table>';
        if(htmlStr.length == 0) {
            Get("bonjourGwTd").innerHTML = '<span style="height:43px; display:inline-block;"></span>';
        }
	} else {
        var pattern = /^<span/i;
        if(pattern.test(htmlStr)) {
            Get("bonjourGwTd").innerHTML = '';
            Get("fwPolicyNameTd").innerHTML = '';
        } else {
        	if(htmlStr.length ==0) {
	            Get("fwPolicyNameTd").innerHTML = '';
        	} else {
	            Get("fwPolicyNameTd").innerHTML='<span style="height:43px; display:inline-block;"></span>';
        	}
        }
	}
};

function editFirewallPolicyInPanel(fwId) {
	hideSubDialogOverlay();
	var url = "<s:url action='firewallPolicy' includeParams='none' />?operation=editFw&id="+fwId+"&ignore="+new Date().getTime();
	openIFrameDialog(800, 450, url);
}
//=====================firewall policy end========================

//=====================radius server begin========================
function showRadiusServerSelectDialog(ssid, rsid, typeFlag) {
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchSelectRSPage&ssidForRs="+ssid+"&selectRadiusServerId="
		 +rsid+"&radiusTypeFlag="+typeFlag+"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchRSPage, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchRSPage = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-hiveap-radius.png" width="40px" height="40px" '+
					'title="<s:text name="config.radiusAssign.config2.select.list.title" />" class="dialogTitleImg" />'+
					'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.radiusAssign.config2.select.list.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

var succFinishSelectRS = function (o){
	hideSubDialogOverlay();
	eval("var data = " + o.responseText);
	var notStr = '<s:text name="config.networkpolicy.ssid.list.radiusserver.note"/>';
	if (parseInt(data.flag) == 2) {
		notStr = '<s:text name="config.networkpolicy.ssid.list.self.pskRadiusserver.note"/>';
	}
	Get("radiusTD"+data.flag+"_"+data.ssid).innerHTML='<table cellspacing="0" cellpadding="0" border="0"><tr><td>'+
	'<a class="npcLinkA" href="javascript:void(0);" onClick="showRadiusServerSelectDialog('+data.ssid+','+data.rsid+','+data.flag+');">'+
	data.rsNameSub+'</a></td></tr><tr><td class="smallTd">&nbsp;'+notStr+'</td></tr></table>';
};
//=====================radius server end========================
</script>
<!-- The  start VLAN dialog -->
<script>
var mgtVlanDialogOverlay = null;
YAHOO.util.Event.onDOMReady(initMgtVlanDialogPanel);
function initMgtVlanDialogPanel() {
// create Dialog overlay
	var div = document.getElementById('mgtVlanSelectPanelId');
	mgtVlanDialogOverlay = new YAHOO.widget.Panel(div, {
		width:"750px",
		visible:false,
		fixedcenter:false,
		close: false,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		underlay: "none",
		zIndex:1
		});
	mgtVlanDialogOverlay.render(document.body);
	div.style.display = "";
}
function openMgtVlanDialogOverlay(){
	if(null != mgtVlanDialogOverlay){
		if (Get("switchOnlyHideNaviteVlan").style.display=="none") {
			mgtVlanDialogOverlay.cfg.setProperty('width', "660px");
		} else {
			mgtVlanDialogOverlay.cfg.setProperty('width', "750px");
		}
		mgtVlanDialogOverlay.center();
		mgtVlanDialogOverlay.cfg.setProperty('constraintoviewport', true);
		mgtVlanDialogOverlay.cfg.setProperty('visible', true);
	}
}

function hideMgtVlanDialogOverlay(){
	if(null != mgtVlanDialogOverlay){
		mgtVlanDialogOverlay.cfg.setProperty('visible', false);
		Get("mgtVlanSelectPanelContent").innerHTML="";
	}
}
function chooseMgtVlan(){
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=chooseMgtVlan"
	 	+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchMgtVlanProfiles, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchMgtVlanProfiles = function (o){
	set_innerHTML("mgtVlanSelectPanelContent",o.responseText);
	openMgtVlanDialogOverlay();
};

</script>
<div id="mgtVlanSelectPanelId" style="display:none;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" ><div></div></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="ml"></td>
		<td class="mm">
			<div id="mgtVlanSelectPanelContent">
			</div>
		</td>
		<td class="mr"></td>
	</tr>
</table>
</td></tr>
<tr><td>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td class="bm" ><div></div></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
</div>
<!-- The end VLAN dialog -->

<div id="subDialogOverlay" style="display: none;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td width="370px" class="um" id="topMidDiv"></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td>
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ml"></td>
			<td class="mm">
				<table class="innerPage" cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
						<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
							<td>
								<img src="images/hm_v2/profile/hm-icon-users-big.png" id="hdDivImg" class="dialogTitleImg" style="padding-right:0;display:none;" />
							</td>
							<td>
								<span class="npcHead1" style="padding-left:0" id="hdDivSpan">Configure Subnetwork</span>
							</td>
							</tr>
						</table>
						</td>
						<td align="right">
							<a href="javascript:void(0);" style="display: none;"></a>
							<a id="x" href="javascript:hideSubDialogOverlay();">
							   <img src="<s:url value="/images/cancel.png" />"
							    width="16" height="16" alt="Cancel" title="Cancel" class="dinl"/></a>
						</td>
					</tr>
					<tr id="spaceEmptyRow">
						<td colspan="2" height="15px"/>
					</tr>
					<tr>
						<td colspan="2" >
							<div id="bdDiv">
							</div>
						</td>
					</tr>
				</table>
			</td>
			<td class="mr"></td>
		</tr>
	</table>
</td></tr>
<tr><td>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td width="370px" class="bm" id="bottomMidDiv"></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
</div>

<!--        fnr code end  -->

<script type="text/javascript"
	src="<s:url value="/yui/event-simulate/event-simulate-min.js" includeParams="none" />"></script>
<script
	src="<s:url value="/yui/element/element-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/widget/accordionview/accordionview-min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>

<!--        begin:    jchen coding        -->
<script>
var edittingBindTarget;
var edittingParentType;
var edittingSSID;
var edittingUserGroupTarget;
var edittingPPSKCwp;
var edittingWpaCwp;
var cwpOperation;

function createCWPFromSSID(bindTarget, ssidId, isPPSK, isWpa) {
	edittingBindTarget = bindTarget;
	edittingSSID = ssidId;
	edittingPPSKCwp = isPPSK;
	edittingWpaCwp = isWpa;
	cwpOperation = 'createCWPFromSSID';
	var url = '<s:url action="captivePortalWeb" includeParams="none" />'
		+ '?operation=createCwpFromSSID&ssidId=' + ssidId
		+ '&ppskCwp=' + isPPSK
		+ '&wpaCwp=' + isWpa
		+ '&bindTarget=' + bindTarget
		+ '&jsonMode=true'
		+ '&ignore=' + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:creatingCwpForSSID}, null);
}

function editCWPInSubdrawer(cwpId, bindTarget,ssidId,isPPSK) {
	cwpOperation = 'editCWPInSubdrawer';
	var url = '<s:url action="captivePortalWeb" includeParams="none" />'
		+ '?operation=edit'
		+ '&ssidId=' + 	ssidId
		+ '&id=' + cwpId
		+ '&ppskCwp=' + isPPSK
		+ '&bindTarget=' + bindTarget
		+ '&jsonMode=true'
		+ '&ignore=' + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:edittingCwp}, null);
}

var creatingCwpForSSID = function(o) {
	subDrawerOperation= "createCWP";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialCWPSubDrawerTitle("New Captive Web Portal"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

var edittingCwp = function(o) {
	subDrawerOperation= "editCWP";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialCWPSubDrawerTitle("Edit Captive Web Portal"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function exportDefaultCWP() {
    var formName = 'captivePortalWeb';
    document.forms[formName].operation.value = 'exportWebPages';
    document.forms[formName].submit();
}

function saveCwpForSSID() {
	var thisOperation = "create";

	if (validate(thisOperation)) {
		showProcessing();

		Get(formName + "_dataSource_loginDisplayStyle").value = Get("loginPageDiv").style.display;
		Get(formName + "_dataSource_successDisplayStyle").value = Get("successPageDiv").style.display;
		Get(formName + "_dataSource_failureDisplayStyle").value = Get("failurePageDiv").style.display;
		Get(formName + "_dataSource_advancedDisplayStyle").value = Get("advanced").style.display;
		Get(formName + "_dataSource_walledGardenDisplayStyle").value = Get("walledGarden").style.display;

		document.forms[formName].operation.value = thisOperation;
		var url = '<s:url action="captivePortalWeb" includeParams="none" />'
			+ '?ssidId=' + edittingSSID
			+ '&ppskCwp=' + edittingPPSKCwp
			+ '&wpaCwp=' + edittingWpaCwp
			+ '&bindTarget=' + edittingBindTarget
			+ '&jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("captivePortalWeb"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterSaveCwp}, null);
	}
}

function editCWP() {
	var thisOperation = "update";

	if (validate(thisOperation)) {
		showProcessing();

		Get(formName + "_dataSource_loginDisplayStyle").value = Get("loginPageDiv").style.display;
		Get(formName + "_dataSource_successDisplayStyle").value = Get("successPageDiv").style.display;
		Get(formName + "_dataSource_failureDisplayStyle").value = Get("failurePageDiv").style.display;
		Get(formName + "_dataSource_advancedDisplayStyle").value = Get("advanced").style.display;
		Get(formName + "_dataSource_walledGardenDisplayStyle").value = Get("walledGarden").style.display;

		document.forms[formName].operation.value = thisOperation;
		var url = '<s:url action="captivePortalWeb" includeParams="none" />'
			+ '?jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("captivePortalWeb"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterSaveCwp}, null);
	}
}

var afterSaveCwp = function(o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		/*
		 * error, the returned is a cwp.jsp page
		 */
		 creatingCwpForSSID(o);
		return;
	}

	if(!details.ok) {
		alert(details.msg);
	}

	/*
	 * successful
	 */
	 accordionView.collapseSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	fetchConfigTemplate2Page(true);
}
</script>
<!--        end:      jchen coding        -->
<!-- Scheduler begin -->

<!-- calendar css just for IE start-->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer, #calendarcontainer1,#calendarcontainer2 {
	padding: 10px;
}
#calendarpicker button, #calendarpicker1 button, #calendarpicker2 button, #calendarpicker3 button, #calendarpicker4 button {
    background: url(<s:url value="/images/calendar_icon.gif" includeParams="none"/>) center center no-repeat;
    *margin: 2px 0; /* For IE */
    *height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
}
 #month-field,
    #day-field {
        width: 2em;
    }

    #year-field {
        width: 3em;
    }
</style>
<!--  calendar css just for IE end -->

<script>
var schedulerOperation;
function createSchedulerFromSSID(bindTarget,ssidId,selectedSchedulers) {
	edittingSSID = ssidId;
	edittingBindTarget = bindTarget;
	var url = '<s:url action="scheduler" includeParams="none" />'
		+ '?operation=createSchedulerFromSSID&ssidId=' + ssidId
		+ '&bindTarget=' + bindTarget
		+ '&selectedSchedulerList=' + selectedSchedulers
		+ '&jsonMode=true'
		+ '&ignore=' + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:creatingSchedulerForSSID}, null);
}

function editSchedulerInSubdrawer(schedulerId, bindTarget,ssidId,selectedSchedulers) {
	schedulerOperation = 'editSchedulerInSubdrawer';
	var url = '<s:url action="scheduler" includeParams="none" />'
		+ '?operation=edit'
		+ '&ssidId=' + 	ssidId
		+ '&id=' + schedulerId
		+ '&selectedSchedulerList=' + selectedSchedulers
		+ '&bindTarget=' + bindTarget
		+ '&jsonMode=true'
		+ '&ignore=' + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:edittingScheduler}, null);
}

var creatingSchedulerForSSID = function(o) {

	subDrawerOperation= "createScheduler";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New Schedule"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

var edittingScheduler = function(o) {
	subDrawerOperation= "editScheduler";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit Schedule"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function editScheduler() {
	var thisOperation = "update";

	if (validate(thisOperation)) {
		showProcessing();

		document.forms[formName].operation.value = thisOperation;
		var url = '<s:url action="scheduler" includeParams="none" />'
			+ '?jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("scheduler"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterSaveScheduler}, null);
	}
}

function saveSchedulerForSSID() {
	var thisOperation = "create";

	if (validate(thisOperation)) {
		showProcessing();

		document.forms[formName].operation.value = thisOperation;

		var url = '<s:url action="scheduler" includeParams="none" />'
			+ '?ssidId=' + edittingSSID
			+ '&bindTarget=' + edittingBindTarget
			+ '&jsonMode=true'
			+ '&ignore=' + new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("scheduler"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:afterSaveScheduler}, null);
	}
}

var afterSaveScheduler = function(o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		/*
		 * error, the returned is a schedule.jsp page
		 */
		 creatingSchedulerForSSID(o);
		return;
	}

	if(!details.ok) {
		alert(details.msg);
	} else {
		/*
		 * successful
		 */
		 accordionView.collapseSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
		fetchConfigTemplate2Page(true);
	}


}
</script>
<!-- Scheduler end -->

<!--        begin:    common iFrame dialog  -->
<style>
    .ul {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UL.png) no-repeat left top;
    }
    .um {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UM.png) repeat-x center top;
    }
    .ur {
    width:40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-UR.png) no-repeat right top;
    }
    .ml {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-ML.png) repeat-y 0% 50%;
    }
    .mm {
    height:100%;
    background-color: #f9f9f7;
    }
    .mr {
    width:40px;
    height:100%;
    background: transparent url(images/hm_v2/popup/HM-Popup-MR.png) repeat-y 100% 50%;
    }
    .bl {
    width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LL.png) no-repeat left bottom;
    }
    .bm {
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LM.png) repeat-x center bottom;
    }

	.br {
	width: 40px;
    height:40px;
    background:url(images/hm_v2/popup/HM-Popup-LR.png) no-repeat right bottom;
    }

#iframeDialog.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}

#subDialogOverlay.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}

#mgtVlanSelectPanelId.yui-panel{
	border: none;
	overflow: visible;
	background-color: transparent;
}
#qosSettingNewPanel.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}

#trafficFilterPanel.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
.innerPage {
	background-color: #f9f9f7;
}
</style>
<div id="iframeDialog" style="display: none;" >
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="100%">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" id="tdUM"></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ml"></td>
			<td class="mm">
				<iframe id="dialogFrame" name="dialogFrame"
					style="overflow: auto;" frameborder="0">
				</iframe>
			</td>
			<td class="mr"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td class="bm" id="tdBM"></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
</div>
<script>
var iframeDialog = null;

function createIFrameDialog(width, height){
	var div = Get("iframeDialog");
	var iframe = Get("dialogFrame");
	iframe.width = width - 80;
	iframe.height = height - 80;
	iframe.src = '';

	iframeDialog = new YAHOO.widget.Panel(div,
	                                        { width:width+"px",
											  fixedcenter: false,
											  underlay: "none",
											  modal: true,
											  close: false,
											  visible:false,
											  draggable: false,
											  constraintoviewport:true,
											  zIndex: 999} );
	//Allow escape key to close box
	var escListener = new YAHOO.util.KeyListener(document, { keys:27 },
												  		   { fn:closeIFrameDialog,
											     			scope:iframe.document,
											     			correctScope:true });
	iframeDialog.cfg.queueProperty("keylisteners", escListener);

	iframeDialog.render(document.body);
	div.style.display="";
	document.getElementById("tdUM").style.width = (width - 80) + "px";
	document.getElementById("tdBM").style.width = (width - 80) + "px";
}

function prepareBeforeOpenIFrameDialog(width, height, source) {
	iframeDialog = null;

	createIFrameDialog(width, height);

	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	//if(YAHOO.env.ua.ie){
		Get("dialogFrame").style.display = "";
	//}

	var iframe = Get("dialogFrame");
	iframe.src = source;

	//Allow escape key to close box
	iFrameLoadedAction(iframe,escListenerForIFrame);
	
	return iframeDialog;
}

function openIFrameDialog(width, height, source) {
	iframeDialog = prepareBeforeOpenIFrameDialog(width, height, source);
	iframeDialog.show();
	iframeDialog.center();
}

function iFrameLoadedAction(iframe,method){
	if (YAHOO.env.ua.ie){
	    iframe.onreadystatechange = function(){
	        //For IE, the iframe has two states 'loading', 'inactive' before 'complete'
	        if (iframe.readyState == "complete"){
	        	method();
	        }
	    };
	} else {
	    iframe.onload = function(){
	    	method();
	    };
	}
}

function escListenerForIFrame(){
	var dialogFrameDum =hm.util.getIFrameDOMById("dialogFrame").document;
	var escListener = new YAHOO.util.KeyListener(dialogFrameDum, { keys:27 },
	  		   { fn:closeIFrameDialog,
  			scope:dialogFrameDum,
  			correctScope:true });
	escListener.enable();
}

function isIFrameDialogOpen(){
	if (iframeDialog==null) {
		return false;
	} else {
		return true;
	}
}

function closeIFrameDialog() {
	if (iframeDialog && null != iframeDialog) {
		iframeDialog.hide();

		//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
		//if(YAHOO.env.ua.ie){
			Get("dialogFrame").style.display = "none";
		//}
		iframeDialog=null;
		//Get("dialogFrame").src = "";
	}
}

function changeIFrameDialog(width, height) {
	if (null == iframeDialog) {
		openIFrameDialog(width, height, "");
	} else {
		var iframe = Get("dialogFrame");
		iframe.width = width - 80;
		iframe.height = height - 80;

		iframeDialog.cfg.setProperty('width', width+"px");

		document.getElementById("tdUM").style.width = (width - 80) + "px";
		document.getElementById("tdBM").style.width = (width - 80) + "px";
		iframeDialog.center();
	}
}

function changeIFrameDialogWithoutPosChg(width, height) {
	if (null == iframeDialog) {
		openIFrameDialog(width, height, "");
	} else {
		var iframe = Get("dialogFrame");
		iframe.width = width - 80;
		iframe.height = height - 80;

		iframeDialog.cfg.setProperty('width', width+"px");

		document.getElementById("tdUM").style.width = (width - 80) + "px";
		document.getElementById("tdBM").style.width = (width - 80) + "px";
	}
}

</script>

<script>
// HiveAP JAVA Script
var thisOperation;
var subOperation;
function submitHiveApAction(operation) {
	//if(!validate(operation)){
	//	return false;
	//}
	thisOperation = operation;
	var url = "";
	if(operation == 'remove'){
		hm.util.checkAndConfirmDelete();
		return;
	}
	if(operation == 'multiEdit'){
		hm.util.checkAndConfirmModify();
		return;
	}


	if(operation == 'saveColumns'
		|| operation == 'resetColumns'
		|| operation == 'cancelColumns'
		|| operation == 'firstPage'
		|| operation == 'resizePage'
		|| operation == 'previousPage'
		|| operation == 'nextPage'
		|| operation == 'lastPage'
		|| operation == 'gotoPage'){
		document.forms["hiveApList"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById('hiveApList'));
		url =  "<s:url action='hiveAp' includeParams='none' />" +
			"?hmListType=manageAPGuid"+
			"&jsonMode=true"+
			"&ignore="+new Date().getTime();
	}else if(operation == 'newGuid'
			|| operation == 'cancel'){
		url =  "<s:url action='hiveAp' includeParams='none' />?operation="+ operation +
			"&hmListType=manageAPGuid"+
			"&filter=" + document.getElementById('filterSelect').value +
			"&jsonMode=true"+
			"&ignore="+new Date().getTime();
	} else if (operation == 'newScheduler' || operation == 'editScheduler'
			|| operation == 'newSchedulerMulti' || operation == 'editSchedulerMulti') {
		if (operation == 'newScheduler' || operation == 'newSchedulerMulti') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID=hiveAp_scheduler"
					+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_scheduler");
			if(value < 0){
				return ;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
				+ "&scheduler="+value
				+ "&parentDomID=hiveAp_scheduler"
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(800, 450, url);
		return;
	}else if (operation == 'newSuppCLI' || operation == 'editSuppCLI'
			|| operation == 'newSuppCLIMulti' || operation == 'editSuppCLIMulti') {
		if (operation == 'newSuppCLI' || operation == 'newSuppCLIMulti') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
					+ "&parentDomID=hiveAp_supplementalCLIId"
					+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_supplementalCLIId");
			if(value < 0){
				return ;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
				+ "&supplementalCLIId="+value
				+ "&parentDomID=supplementalCLIId"
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(830, 700, url);
		return;
	} else if (operation == 'newPPPoE' || operation == 'editPPPoE') {
		if (operation == 'newPPPoE') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
				+ "&parentDomID=hiveAp_pppoeAuthProfile"
				+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_pppoeAuthProfile");
			if(value < 0){
				return ;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
				+ "&pppoeAuthProfile="+value
				+ "&parentDomID=hiveAp_pppoeAuthProfile"
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(800, 450, url);
		return;
	} else if (operation == 'editMacAddressEth0' || operation == 'newMacAddressEth0'
					|| operation =='editMacAddressEth1' || operation =='newMacAddressEth1'
					|| operation == 'editMacAddressAgg0' || operation == 'newMacAddressAgg0'
					|| operation == 'editMacAddressRed0' || operation == 'newMacAddressRed0') {
			var parentDomIDs_eth0Maces = "leftOptions_eth0Maces,leftOptions_eth1Maces,leftOptions_agg0Maces,leftOptions_red0Maces";
			var parentDomIDs_eth1Maces = "leftOptions_eth1Maces,leftOptions_eth0Maces,leftOptions_agg0Maces,leftOptions_red0Maces";
			var parentDomIDs_agg0Maces = "leftOptions_agg0Maces,leftOptions_eth1Maces,leftOptions_eth0Maces,leftOptions_red0Maces";
			var parentDomIDs_red0Maces = "leftOptions_red0Maces,leftOptions_eth1Maces,leftOptions_agg0Maces,leftOptions_eth0Maces";
			if (operation == 'newMacAddressEth0') {
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=" + parentDomIDs_eth0Maces
						+ "&ignore="+new Date().getTime();
			} else if (operation == 'editMacAddressEth0') {
				var value = hm.util.validateOptionTransferSelection("eth0Maces");
				if(value < 0){
					return ;
				}
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&learningMacId="+value
						+ "&parentDomID=" + parentDomIDs_eth0Maces
						+ "&ignore="+new Date().getTime();

			} else if (operation =='newMacAddressEth1') {
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=" + parentDomIDs_eth1Maces
						+ "&ignore="+new Date().getTime();

			} else if (operation =='editMacAddressEth1') {
				var value = hm.util.validateOptionTransferSelection("eth1Maces");
				if(value < 0){
					return ;
				}
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&learningMacId="+value
						+ "&parentDomID=" + parentDomIDs_eth1Maces
						+ "&ignore="+new Date().getTime();

			} else if (operation == 'newMacAddressAgg0') {
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=" + parentDomIDs_agg0Maces
						+ "&ignore="+new Date().getTime();
			} else if (operation == 'editMacAddressAgg0') {
				var value = hm.util.validateOptionTransferSelection("agg0Maces");
				if(value < 0){
					return ;
				}
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&learningMacId="+value
						+ "&parentDomID=" + parentDomIDs_agg0Maces
						+ "&ignore="+new Date().getTime();
			} else if (operation == 'newMacAddressRed0') {
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&parentDomID=" + parentDomIDs_red0Maces
						+ "&ignore="+new Date().getTime();
			} else if (operation == 'editMacAddressRed0') {
				var value = hm.util.validateOptionTransferSelection("red0Maces");
				if(value < 0){
					return ;
				}
				url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"
						+ "&learningMacId="+value
						+ "&parentDomID=" + parentDomIDs_red0Maces
						+ "&ignore="+new Date().getTime();
			}
			openIFrameDialog(820, 450, url);
			return;

	} else if (operation == 'newEthCwpDefaultRegUserProfile'
					|| operation == 'editEthCwpDefaultRegUserProfile'
					|| operation == 'newEthCwpDefaultAuthUserProfile'
					|| operation == 'editEthCwpDefaultAuthUserProfile'
					|| operation == 'newEthCwpUserprofile'
					|| operation == 'editEthCwpUserprofile'
					|| operation == 'newUserProfileEth0'
					|| operation == 'editUserProfileEth0'
					|| operation == 'newUserProfileEth1'
					|| operation == 'editUserProfileEth1'
					|| operation == 'newUserProfileAgg0'
					|| operation == 'editUserProfileAgg0'
					|| operation == 'newUserProfileRed0'
					|| operation == 'editUserProfileRed0'
					) {
		var formNameTmp = "hiveAp";
		if (operation == 'newEthCwpDefaultRegUserProfile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_ethDefaultRegUserprofile"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
	 				+ "&ignore="+new Date().getTime();
		} else if (operation == 'editEthCwpDefaultRegUserProfile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_ethDefaultRegUserprofile"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&ethDefaultRegUserprofile="+document.forms[formNameTmp].ethDefaultRegUserprofile.value
					+ "&dataSource.ethCwpEnableMacAuth="+Get(formNameTmp+"_dataSource_ethCwpEnableMacAuth").checked
					+ "&dataSource.ethCwpEnableEthCwp="+Get(formNameTmp+"_dataSource_ethCwpEnableEthCwp").checked
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newEthCwpDefaultAuthUserProfile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_ethDefaultAuthUserprofile"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editEthCwpDefaultAuthUserProfile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_ethDefaultAuthUserprofile"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&ethDefaultAuthUserprofile="+document.forms[formNameTmp].ethDefaultAuthUserprofile.value
					+ "&dataSource.ethCwpEnableMacAuth="+Get(formNameTmp+"_dataSource_ethCwpEnableMacAuth").checked
					+ "&dataSource.ethCwpEnableEthCwp="+Get(formNameTmp+"_dataSource_ethCwpEnableEthCwp").checked
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newEthCwpUserprofile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID=leftOptions_ethUserProfiles"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editEthCwpUserprofile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID=leftOptions_ethUserProfiles"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&ethUserProfileId="+document.forms[formName].ethUserProfileId.value
					+ "&dataSource.ethCwpEnableMacAuth="+Get(formNameTmp+"_dataSource_ethCwpEnableMacAuth").checked
					+ "&dataSource.ethCwpEnableEthCwp="+Get(formNameTmp+"_dataSource_ethCwpEnableEthCwp").checked
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newUserProfileEth0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileEth0"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editUserProfileEth0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileEth0"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&userProfileEth0="+document.forms[formNameTmp].userProfileEth0.value
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newUserProfileEth1') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileEth1"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editUserProfileEth1') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileEth1"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&userProfileEth1="+document.forms[formNameTmp].userProfileEth1.value
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newUserProfileAgg0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileAgg0"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editUserProfileAgg0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileAgg0"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&userProfileAgg0="+document.forms[formNameTmp].userProfileAgg0.value
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'newUserProfileRed0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileRed0"
					+ "&contentShowType=dlg"
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		} else if (operation == 'editUserProfileRed0') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true" +"&parentDomID="+formNameTmp+"_userProfileRed0"
					+ "&contentShowType=dlg"
					+ "&id="+Get(formNameTmp+"_dataSource_id").value
					+ "&userProfileRed0="+document.forms[formNameTmp].userProfileRed0.value
					+ "&wirelessRoutingEnable="+npWirelessRoutingEnabledHiveAp
					+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(800, 600, url);
		return;
	} else if (operation == 'newRadius' || operation == 'editRadius') {
		var selectElId = "hiveAp" + "_radiusServer";
		if (operation == 'newRadius') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_radiusServer");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&radiusServer="+value+"&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		}
		/**var formNameTmp = "hiveAp";
		if (Get(formNameTmp + "_dataSource_deviceType").value==DEVICE_TYPE_BRANCH_ROUTER){
			url = url + "&newEditRadiusForBrFlg=true";
		}**/
		openIFrameDialog(900, 750, url);
		return;
	} else if (operation == 'newRadiusProxy' || operation == 'editRadiusProxy') {
		var selectElId = "hiveAp" + "_radiusProxy";
		if (operation == 'newRadiusProxy') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_radiusProxy");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&radiusProxy="+value+"&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		}
		/**
		var formNameTmp = "hiveAp";
		if (Get(formNameTmp + "_dataSource_deviceType").value==DEVICE_TYPE_BRANCH_ROUTER){
			url = url + "&newEditRadiusForBrFlg=true";
		}**/
		openIFrameDialog(900, 750, url);
		return;
	} else if (operation == 'newRoutingPbrPolicy' || operation == 'editRoutingPbrPolicy') {
		var selectElId = "hiveAp" + "_routingPolicyId";
		if (operation == 'newRoutingPbrPolicy') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp" + "_routingPolicyId");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&routingPolicyId="+value+"&parentDomID="+selectElId
				+ "&contentShowType=dlg"+ "&ignore="+new Date().getTime();
		}
		/**
		var formNameTmp = "hiveAp";
		if (Get(formNameTmp + "_dataSource_deviceType").value==DEVICE_TYPE_BRANCH_ROUTER){
			url = url + "&newEditRadiusForBrFlg=true";
		}**/
		openIFrameDialog(880, 600, url);
		return;
	} else if (operation == 'newEthCwpCwpProfile' || operation == 'editEthCwpCwpProfile') {
		var selectElId = "hiveAp_cwpProfile";
		if (operation == 'newEthCwpCwpProfile') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
			+ "&contentShowType=dlg" + "&parentIframeOpenFlg=true" + "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("hiveAp_cwpProfile");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&cwpProfile="+value+"&parentDomID="+selectElId
			+ "&contentShowType=dlg" + "&parentIframeOpenFlg=true" + "&ignore="+new Date().getTime();
		}
		openIFrameDialog(960, 800, url);
		return;
	} else if (operation == 'newEthCwpRadiusClient' || operation == 'editEthCwpRadiusClient') {
		var selectElId = "hiveAp_ethCwpRadiusClient";
		if (operation == 'newEthCwpRadiusClient') {
			url = "<s:url action='radiusAssignment' includeParams='none' />?operation=new&jsonMode=true&parentDomID="+selectElId
		 		+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection(selectElId);
			if(value < 0){
				return;
			}
			url = "<s:url action='radiusAssignment' includeParams='none' />?operation=edit&jsonMode=true&id="+value+"&parentDomID="+selectElId
		 		+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(950, 500, url);
		return;
	} else if (operation == 'newWifi0RadioProfile' || operation == 'editWifi0RadioProfile'
		|| operation == 'newWifi1RadioProfile' || operation == 'editWifi1RadioProfile'
		|| operation == 'newWifi0RadioProfileMulti' || operation == 'editWifi0RadioProfileMulti'
		|| operation == 'newWifi1RadioProfileMulti' || operation == 'editWifi1RadioProfileMulti') {
		if (operation == 'newWifi0RadioProfile' || operation == 'newWifi1RadioProfile'
				|| operation == 'newWifi0RadioProfileMulti' || operation == 'newWifi1RadioProfileMulti') {
			var radioType = "bg";
			if (operation == 'newWifi0RadioProfile' || operation == 'newWifi0RadioProfileMulti') {
				radioType = is11nHiveAP() ? "ng" : "bg";
			} else {
				radioType = is11nHiveAP() ? "na" : "a";
			}
			url = "<s:url action='radioProfile' includeParams='none' />?operation=new&jsonMode=true&radioType="+radioType
			if(operation == 'newWifi0RadioProfile'){
				var radioNameObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioName");
				if(radioNameObj){
					url = url + "&radioProfileName="+radioNameObj.value;
				}
				var radioModeObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_radioMode");
				if(radioModeObj){
					url = url + "&radioProfileMode="+ radioModeObj.options[radioModeObj.selectedIndex].value;
				}
				var channelWidthObj = document.getElementById(formName + "_dataSource_tempWifi0RadioProfile_channelWidth");
				if(channelWidthObj){
					url = url + "&radioProfileChannelWidth="+ channelWidthObj.options[channelWidthObj.selectedIndex].value;
				}
			}else if(operation == 'newWifi1RadioProfile'){
				var radioNameObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioName");
				if(radioNameObj){
					url = url + "&radioProfileName="+radioNameObj.value;
				}
				var radioModeObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_radioMode");
				if(radioModeObj){
					url = url + "&radioProfileMode="+ radioModeObj.options[radioModeObj.selectedIndex].value;
				}
				var channelWidthObj = document.getElementById(formName + "_dataSource_tempWifi1RadioProfile_channelWidth");
				if(channelWidthObj){
					url = url + "&radioProfileChannelWidth="+ channelWidthObj.options[channelWidthObj.selectedIndex].value;
				}
			}
			url = url + "&ignore="+new Date().getTime();
		} else {
			var selectId;
			if (operation == 'editWifi0RadioProfile' || operation == 'editWifi0RadioProfileMulti') {
				selectId = document.getElementById(formName + "_wifi0RadioProfile").value;
			} else {
				selectId = document.getElementById(formName + "_wifi1RadioProfile").value;
			}
			url = "<s:url action='radioProfile' includeParams='none' />?operation=edit&jsonMode=true&id="+selectId+"&ignore="+new Date().getTime();
		}
		openIFrameDialog(850, 500, url);
		return;
	} else if (operation == 'createWifi0RadioProfile' || operation == 'createWifi1RadioProfile') {
		var radioUrl = "<s:url action='hiveAp' includeParams='none' />?operation=" + operation + "&apModelType="
		+document.getElementById("hiveAp_dataSource_hiveApModel").value+"&jsonMode=true&ignore="+new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("hiveAp"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', radioUrl, {success : changeRadioProfileList, failure : resultDoNothing, timeout: 60000}, null);
		return;
	} else if (operation == 'newIpTrack' || operation == 'editIpTrack') {
		var selectElId = "hiveAp_vpnIpTrackId";
		if (operation == 'newIpTrack') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection(selectElId);
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&vpnIpTrackId="+value+"&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(780, 560, url);
		return;
	}else if (operation == 'newDhcpServer' || operation == 'editDhcpServer') {
		var selectElId = "leftOptions" + "_dhcpServers";
		if (operation == 'newDhcpServer') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateOptionTransferSelection("dhcpServers");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&dhcpServer="+value+"&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(780, 550, url);
		return;
	}else if (operation == 'newWifiClientPreferredSsid' || operation == 'editWifiClientPreferredSsid') {
		var selectElId = "leftOptions" + "_preferredSsids";
		if (operation == 'newWifiClientPreferredSsid') {
			url = "<s:url action='wifiClinetPerferredSsid' includeParams='none' />?operation=new&jsonMode=true" + "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateOptionTransferSelection("preferredSsids");
			if(value < 0){
				return;
			}
			url = "<s:url action='wifiClinetPerferredSsid' includeParams='none' />?operation=edit&jsonMode=true"+"&id="+value+"&ignore="+new Date().getTime();
		}
		openIFrameDialog(780, 500, url);
		return;
	}else if (operation == 'newCapwapIp' || operation == 'editCapwapIp'
				|| operation == 'newCapwapIpMulti' || operation == 'editCapwapIpMulti') {
		var selectElId = "capwapSelect";
		if (operation == 'newCapwapIp' || operation == 'newCapwapIpMulti') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("capwapSelect");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&capwapIp="+value+"&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(780, 550, url);
		return;
	}else if (operation == 'newCapwapBackupIp' || operation == 'editCapwapBackupIp'
				|| operation == 'newCapwapBackupIpMulti' || operation == 'editCapwapBackupIpMulti') {
		var selectElId = "capwapBackupSelect";
		if (operation == 'newCapwapBackupIp' || operation == 'newCapwapBackupIpMulti') {
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+ "&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		} else {
			var value = hm.util.validateListSelection("capwapBackupSelect");
			if(value < 0){
				return;
			}
			url = "<s:url action='hiveAp' includeParams='none' />?operation="+operation+"&jsonMode=true"+"&capwapBackupIp="+value+"&parentDomID="+selectElId
				+ "&ignore="+new Date().getTime();
		}
		openIFrameDialog(780, 550, url);
		return;
	}
	if ("" != url) {
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succFetchHiveApsList, failure : resultDoNothing, timeout: 60000}, null);
	}
}

var hideErrNotesForRadiobg = function () {
	hm.util.wipeOut('errNoteForRadiobg', 800);
}
var hideErrNotesForRadioa = function () {
	hm.util.wipeOut('errNoteForRadioa', 800);
}

var changeRadioProfileList = function(o) {
	eval("var detailsR = " + o.responseText);
	if (detailsR.errbg) {
		hm.util.show("errNoteForRadiobg");
		Get("errNoteForRadiobg").className="noteError";
		Get("errNoteForRadiobg").innerHTML=detailsR.errbg;
		var notesTimeoutId = setTimeout("hideErrNotesForRadiobg()", 10000);
	} else if (detailsR.erra) {
		hm.util.show("errNoteForRadioa");
		Get("errNoteForRadioa").className="noteError";
		Get("errNoteForRadioa").innerHTML=detailsR.erra;
		var notesTimeoutId = setTimeout("hideErrNotesForRadioa()", 10000);
	} else {
		if (detailsR.wifi == 0) {
			document.getElementById("hiveAp_dataSource_tempWifi0RadioProfile_radioName").value = "";
			hideSimpleCreateSection('wifi0RadioProfileCreateSection');
		} else {
			document.getElementById("hiveAp_dataSource_tempWifi1RadioProfile_radioName").value = "";
			hideSimpleCreateSection('wifi1RadioProfileCreateSection');
		}
		if (detailsR.listId && undefined != document.getElementById(detailsR.listId)) {
			hm.util.insertSelectValue(detailsR.newId, detailsR.name, document.getElementById(detailsR.listId), false, true);
		}
		//modify the show of Radio Mode, synchronize with the new and edit radio profile
		var wifi0 = document.getElementById("hiveAp_wifi0RadioProfile");
		var wifi1 = document.getElementById("hiveAp_wifi1RadioProfile");
		selectRadioProfile(wifi0);
		selectRadioProfile(wifi1);
	}
};

function saveHiveApAction(operation){
	if(operation == 'create2' || operation == 'update2'){
		if(!validateHiveAp2ForJson(operation)){
			return false;
		}
		subOperation =operation;
		YAHOO.util.Connect.setForm(document.getElementById('hiveAp'));
		url =  "<s:url action='hiveAp' includeParams='none' />" +
			"?operation="+operation+
			"&jsonMode=true"+
			"&ignore="+new Date().getTime();
		document.forms["hiveAp"].hmListType.value = "manageAPGuid";
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveHiveAp, failure : resultDoNothing, timeout: 60000}, null);
	} else if (operation == 'multiUpdate') {
		if (!validMultiEditHiveAPForJson(operation)) {
			return false;
		}
		subOperation =operation;
		url =  "<s:url action='hiveAp' includeParams='none' />" +
			"?jsonMode=true"+
			"&ignore="+new Date().getTime();
		document.forms["hiveAp"].hmListType.value = "manageAPGuid";
		document.forms["hiveAp"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById('hiveAp'));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succSaveHiveAp, failure : resultDoNothing, timeout: 60000}, null);
	}
}

function editHiveApAction(id){
	subOperation ="editHiveApGuid";
	url =  "<s:url action='hiveAp' includeParams='none' />"+
		"?operation=editHiveApGuid"+
		"&hmListType=manageAPGuid"+
		"&id="+id+
		"&jsonMode=true"+
		"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succNewHiveAp, failure : resultDoNothing, timeout: 60000}, null);
}

function newHiveApAction(){
	subOperation = "newGuid";
	var url = "<s:url action='hiveAp' includeParams='none' />?operation=newGuid&jsonMode=true&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewHiveAp, failure : resultDoNothing, timeout: 60000}, null);
}

function submitHiveApUrl(url){
	var transaction = YAHOO.util.Connect.asyncRequest('get', url, {success : succFetchHiveApsList, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchHiveApsList = function(o){
    // set overflow as hidden
    //yDom.addClass(accordionView.getDrawerContentId('cofigurePolicy'), 'hiddenOverFlow');
    yDom.setStyle(Get(accordionView.getDrawerContentId('cofigurePolicy')), 'overflowY', 'hidden');

	set_innerHTML(accordionView.getDrawerContentId('cofigurePolicy'),
			o.responseText);
	backConfigPolicy();
	setTimeout("onLoadPage()", 1000);
	setTimeout("onLoadPaging()", 1000);

    // calculate the height
    yEvent.onContentReady(accordionView.getDrawerContentId('cofigurePolicy'), function() {
        setTimeout(recalculateDrawerHeight(this.id), 1500);
    });
};

var succNewHiveAp = function(o){
	// set the sub drawer title
	accordionView.setSubDrawerTitle('cofigurePolicy', 'sub_cofigurePolicy', initialHiveApSubDrawerTitle());
	accordionView.expandSubDrawer('cofigurePolicy', 'sub_cofigurePolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('cofigurePolicy', 'sub_cofigurePolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("onLoadPage()", 1000);
	setTimeout("hideNotes()", 10000);
};

var succSaveHiveAp = function(o){
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if (subOperation == 'create2' || subOperation == 'update2') {
			if(subOperation == 'create2'){
				subOperation = 'newGuid';
			}
			if(subOperation == 'update2'){
				subOperation = 'editHiveApGuid';
			}
			succNewHiveAp(o);
		}
		if (subOperation == 'multiUpdate') {
			subOperation = "multiEdit";
			succMultiEditHiveAp(o);
		}
		return;
	}
	if (details.t) {
		fetchHiveApsListPage();
	}
}

var succMultiEditHiveAp = function(o){
	// set the sub drawer title
	accordionView.setSubDrawerTitle('cofigurePolicy', 'sub_cofigurePolicy', initialHiveApSubDrawerTitle());
	accordionView.expandSubDrawer('cofigurePolicy', 'sub_cofigurePolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('cofigurePolicy', 'sub_cofigurePolicy');

	// TODO fetch the page
	set_innerHTML(subDrawerContentId, o.responseText);
	notesTimeoutId = setTimeout("onloadPageforJsonMode()", 1000);
	setTimeout("hideNotes()", 10000);
};

function initialHiveApSubDrawerTitle (text) {
	var title = "";
	if(subOperation == "newGuid"){
		title = "<h4 class='npcButton'>" +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;"><span style="padding-bottom: 2px;padding-top: 0px;" onclick="saveHiveApAction(\'create2\');">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="submitHiveApAction(\'cancel\');">Cancel</span></a>' +
		"New Device" + "</h4>";
	}else if(subOperation == "editHiveApGuid"){
		title = "<h4 class='npcButton'>" +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;"><span style="padding-bottom: 2px;padding-top: 0px;" onclick="saveHiveApAction(\'update2\');">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="submitHiveApAction(\'cancel\');">Cancel</span></a>' +
		"Edit Device" + "</h4>";
	}else if(subOperation == "multiEdit"){
		title = "<h4 class='npcButton'>" +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;"><span style="padding-bottom: 2px;padding-top: 0px;" onclick="saveHiveApAction(\'multiUpdate\');">Save</span></a>' +
		'<a class="btCurrent" href="javascript:void(0);" style="float: right;margin-right:20px;"><span style="margin-right:10px;padding-bottom: 2px;padding-top: 0px;" onclick="submitHiveApAction(\'cancel\');">Cancel</span></a>' +
		"Multiple Edit Device" + "</h4>";
	}

	return title;
}

function backConfigPolicy() {
	accordionView.collapseSubDrawer('cofigurePolicy', 'sub_cofigurePolicy');

	var subDrawerContentId = accordionView.getSubDrawerContentId('cofigurePolicy', 'sub_cofigurePolicy');
	set_innerHTML(subDrawerContentId, '');
	subOperation = '';
	initializeCurrentHelpLinkUrl();
}
</script>
<!--        end:    common iFrame dialog    -->

<!-- style defined for drawer1 start -->
<style type="text/css">
	/* for select list */
	.subDrawer2 {
		margin: 0 auto;
		text-align: center;
		padding-top: 25px;
		padding-bottom: 15px;
	}

	span.title {
		margin: 0 auto;
		font-family: century gothic, helvetica light, helvetica, arial, sans serif;
		font-size: 20px;
		font-weight: bold;
		line-height: 25px;
		color: #FFCC33;
	}

	div.selectList {
		margin: 0 auto;
		font-family: century gothic, helvetica light, helvetica, arial, sans serif;
		width: 300px;
		border: 1px solid grey;
		text-align: left;
		height: 150px;
		position: relative;
	}

	.editBox1 {
		background-color: #FFFFFF;
	}

	span.dialogTitle {
		vertical-align: middle;
		text-align: left;
		align: left;
		font-family: century gothic, helvetica light, helvetica, arial, sans serif;
		font-size: 16px;
		font-weight: bold;
		line-height: 20px;
		color: #FF6600;
	}
	.dialogTip {
		vertical-align:top;
		text-align: left;
		font-family: century gothic, helvetica light, helvetica, arial, sans serif;
		font-size: 11px;
		padding-left: 0;
		padding-top: 20px;
		color: #393939;
		line-height: 1.6em;
	}
	img.dialogTitleImg {
		padding: 0;
		vertical-align: middle;
		align: center;
		border:none;
		margin-right: 10px;
	}
	div#networkPolicysDiv  div.dialogListMenu {
	    right: -20px;
    }
    div#networkPolicysDiv .dialogListMenu ul {
        padding: 1px 0;
        margin: 0px;
        border-color: #fff;
        border-style: solid;
        border-width: 1px 0 0;
    }
    div#networkPolicysDiv .dialogListMenu li {
        list-style-type: none;
        text-decoration: none;
        text-align: left;
        line-height: 18px;
        cursor: pointer;
        background-image: none;
    }
</style>
<!-- style defined for drawer1 end -->
<!-- start_wx_02: script for user profile(new/edit) -->
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/selectedtable.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/css/widget/tabviewSimple.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<script
	src="<s:url value="/js/widget/selectedtable.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
	src="<s:url value="/js/widget/tabviewSimple.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<style type="text/css">
#modifyNetworkPolicyPanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
#newNetworkPolicyPanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
#userProfileSelectPanelId.yui-panel {
	border: none;
	overflow: visible;
	background-color: transparent;
}
</style>
<script type="text/javascript">
function newUserProfiles2(ssidId, userProfileSubTabId, args) {
	var upType = 0;
	if (args != null && args.upType) {
		upType = args.upType;
	}
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&operation=new"
			+ "&dealingParentId="+ssidId
			+ "&userProfileSubTabId="+userProfileSubTabId
			+ "&dealUserProfile4Who=" + <%= UserProfilesAction.DEAL_USERPROFILE_FOR_SSID_JSON %>
			+ "&upType="+upType
			+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewUserProfiles2, failure : failNewUserProfiles2, timeout: 60000}, null);
}
var succNewUserProfiles2 = function(o) {
	commonDealNewUserProfile2Succ(o);
	hideUserProfileSelectDialog();
}
var failNewUserProfiles2 = function(o) {
	commonDealNewUserProfile2Fail(o);
	hideUserProfileSelectDialog();
}

function newUserProfiles2Lan(lanId) {
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&operation=new"
		+ "&dealingParentId="+lanId
		+ "&dealUserProfile4Who=" + <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>
		+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewUserProfiles2Lan, failure : failNewUserProfiles2Lan, timeout: 60000}, null);
}
var succNewUserProfiles2Lan = function(o) {
	commonDealNewUserProfile2Succ(o);
	hideUserProfileSelectDialog();
}
var failNewUserProfiles2Lan = function(o) {
	commonDealNewUserProfile2Fail(o);
	hideUserProfileSelectDialog();
}

var commonDealNewUserProfile2Succ = function(o) {
	subDrawerOperation= "createUserProfile";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New User Profile"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	set_innerHTML(subDrawerContentId, o.responseText);
}
var commonDealNewUserProfile2Fail = function(o) {
	eval("var details = " + o.responseText);
	var hideErrNotes = function () {
		hm.util.wipeOut('errNote', 800);
	}
	hm.util.show("errNote");
	Get("errNote").className="noteError";
	Get("errNote").innerHTML=details.e;
	var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
}

function editUserProfiles2(ssidId, userProfileId){
	succAddLanUserProfileCallBack.setOperate("");
	succAddSsidUserProfileCallBack.setOperate("");
	editUserProfiles2RealOperate(ssidId, userProfileId);
}
function editUserProfiles2RealOperate(ssidId, userProfileId){
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&operation=edit"
			+ "&dealingParentId="+ssidId
			+ "&id="+userProfileId
			+ "&dealUserProfile4Who=" + <%= UserProfilesAction.DEAL_USERPROFILE_FOR_SSID_JSON %>
			+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditUserProfiles2, failure : failEditUserProfiles2, timeout: 60000}, null);
}
function editUserProfiles2Lan(lanId, userProfileId){
	succAddLanUserProfileCallBack.setOperate("");
	editUserProfiles2LanRealOperate(lanId, userProfileId);
}
function editUserProfiles2LanRealOperate(lanId, userProfileId){
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&operation=edit"
		+ "&dealingParentId="+lanId
		+ "&id="+userProfileId
		+ "&dealUserProfile4Who=" + <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>
		+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succEditUserProfiles2, failure : failEditUserProfiles2, timeout: 60000}, null);
}
var succEditUserProfiles2 = function(o) {
	subDrawerOperation= "updateUserProfile";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit User Profile"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	set_innerHTML(subDrawerContentId, o.responseText);
}
var failEditUserProfiles2 = function(o) {
	eval("var details = " + o.responseText);
	var hideErrNotes = function () {
		hm.util.wipeOut('errNote', 800);
	}
	hm.util.show("errNote");
	Get("errNote").className="noteError";
	Get("errNote").innerHTML=details.e;
	var notesTimeoutId = setTimeout("hideErrNotes()", 10000);
}

function cloneUserProfiles2LanRealOperate(lanId, userProfileId){
	cloneUserProfiles2RealOperate(lanId, userProfileId, <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>);
}
function cloneUserProfiles2SsidRealOperate(lanId, userProfileId){
	cloneUserProfiles2RealOperate(lanId, userProfileId, <%= UserProfilesAction.DEAL_USERPROFILE_FOR_SSID_JSON %>);
}
function cloneUserProfiles2RealOperate(lanId, userProfileId, whereFrom){
	var selectedIdsTmp = new Array();
	selectedIdsTmp.push(userProfileId);
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&operation=clone"
		+ "&dealingParentId="+lanId
		+ "&selectedIds="+selectedIdsTmp
		+ "&dealUserProfile4Who=" + whereFrom
		+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succCloneUserProfiles2, failure : failCloneUserProfiles2, timeout: 60000}, null);
}
var succCloneUserProfiles2 = function(o) {
	subDrawerOperation= "createUserProfile";
	accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone User Profile"));
	accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
	var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

	set_innerHTML(subDrawerContentId, o.responseText);
}
var failCloneUserProfiles2 = function(o) {
	try {
		eval("var details = " + o.responseText);
	} catch (e) {
		//do nothing for now
	}
	ErrorShownHelper.showError(detail.e, "errNoteForAllNetwork");
}

function saveUserProfileJSON2(opera) {
	if(!validate(opera)) {
		return;
	}
	var url = "<s:url action='userProfiles' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	prepareBeforeSubmitUserProfile(opera);
	document.getElementById("userProfiles2Form").operation.value = opera;
	YAHOO.util.Connect.setForm(document.getElementById("userProfiles2Form"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveUserProfile, failure : resultDoNothing, timeout: 60000}, null);
}
var succSaveUserProfile = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if (subDrawerOperation=='updateUserProfile') {
			succEditUserProfiles2(o);
		} else {
			if (details.whereFrom == <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>) {
				succNewUserProfiles2Lan(o);
			} else {
				succNewUserProfiles2(o);
			}
		}
		backNetWorkPolicy();
		fetchConfigTemplate2Page(true);
		return;
	}
	if (details.resultStatus == false) {
		<%--(use commom funciton) ErrorShownHelper.showError(details.errMsg, "errorNoteJson");--%>
		showPageNotes(details.errMsg);
		return;
	}
	if (details.resultStatus == true && details.addedId != null && details.addedId != "") {
		//try to open user profile select dialog and make the newly added one selected.
		networkPolicyCallbackFn = null;
		backNetWorkPolicy();
		if (details.whereFrom == <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>) {
			succAddLanUserProfileCallBack.callback(details.parentId, details.addedId, details.configPhoneData, details.support4LAN);
		} else {
			succAddSsidUserProfileCallBack.callback(details.parentId, details.addedId, details.upTabId, details.upType);
		}
	} else {
		networkPolicyCallbackFn = null;
		if (succAddLanUserProfileCallBack.getOperate() != ""
				&& details.whereFrom
				&& details.whereFrom == <%= UserProfilesAction.DEAL_USERPROFILE_FOR_LAN_JSON %>) {
			backNetWorkPolicy();
			succAddLanUserProfileCallBack.callback(details.parentId, "", details.configPhoneData, details.support4LAN);
		} else if (succAddSsidUserProfileCallBack.getOperate() != ""
				&& details.whereFrom
				&& details.whereFrom == <%= UserProfilesAction.DEAL_USERPROFILE_FOR_SSID_JSON %>){
			backNetWorkPolicy();
			succAddSsidUserProfileCallBack.callback(details.parentId, "", details.upTabId, details.upType);
		} else {
			fetchConfigTemplate2Page(true);
		}
	}
}

var succAddLanUserProfileCallBack = (function(){
	var upType=-1;
	var operate = "";
	return {
		callback : function(parentId, addedId, phonedataSupport, support4LAN) {
			if (this.operate == "edit") {
				selectUserProfile4Lan(parentId, -1, phonedataSupport, support4LAN, {upType: this.upType});
			} else {
				selectUserProfile4Lan(parentId, addedId, phonedataSupport, support4LAN, {upType: this.upType});
			}
		},
		cancelBack : function(parentId, phonedataSupport, support4LAN) {
			selectUserProfile4Lan(parentId, -1, phonedataSupport, support4LAN, {upType: this.upType});
		},
		setUpType : function(upType) {
			this.upType = upType;
		},
		setOperate : function(opType) {
			this.operate = opType;
		},
		getOperate : function() {
			return this.operate;
		}
	}
})();
var succAddSsidUserProfileCallBack = (function(){
	var upType=-1;
	var upTabId="";
	var operate = "";
	return {
		callback : function(parentId, addedId, upTabIdTmp, upTypeTmp) {
			if (this.operate == "edit") {
				var args = {upTabId: this.upTabId, upType: this.upType};
				showUserProfileSelectDialog(parentId, -1, args);
			} else if (this.operate == "clone") {
				var args = {upTabId: this.upTabId, upType: this.upType};
				showUserProfileSelectDialog(parentId, addedId, args);
			} else {
				var args = {upTabId: upTabIdTmp, upType: upTypeTmp};
				showUserProfileSelectDialog(parentId, addedId, args);
			}
		},
		cancelBack : function(parentId) {
			var args = {upTabId: this.upTabId, upType: this.upType};
			showUserProfileSelectDialog(parentId, -1, args);
		},
		setUpType : function(upType) {
			this.upType = upType;
		},
		setOperate : function(opType) {
			this.operate = opType;
		},
		getOperate : function() {
			return this.operate;
		},
		setUpTabId : function(upTabId) {
			this.upTabId = upTabId;
		}
	}
})();

function saveHiveProfileJSON2(opera) {
	if(!validateHiveForJson(opera)){
		return false;
	}
	var url = "<s:url action='hiveProfiles' includeParams='none' />?operation="+ opera +"&jsonMode=true&ignore="+new Date().getTime();
	YAHOO.util.Connect.setForm(document.getElementById("hiveProfiles"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveHiveProfile, failure : resultDoNothing, timeout: 60000}, null);
}
var succSaveHiveProfile = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		//back to main page, reopen the select network policy dialog
		cancelSelectNPSubDrawer();
		return;
	}
	if (details.resultStatus == false) {
		if (Get("errMsgJson4hiveProfiles")) {
			hm.util.reportFieldError(Get("errMsgJson4hiveProfiles"), details.errMsg);
		}
		return;
	}
	if (details.resultStatus == true && details.addedId != null && details.addedId != "") {
		cancelSelectNPSubDrawer();
		if (curNetworkNewOrModifyType == '1') {
			fetchSelectNetworkPolicyNewDlg({addedId: details.addedId});
		} else if (curNetworkNewOrModifyType == '2') {
			fetchNetworkPolicyModify2Page({addedId: details.addedId});
		}
	} else {
		cancelSelectNPSubDrawer();
		if (curNetworkNewOrModifyType == '1') {
			showSelectNetworkPolicyPanel();
			restoreTmpNetworkPolicyArgs();
		} else if (curNetworkNewOrModifyType == '2') {
			showModifyNetworkPolicyPanel();
		}
	}
}

//the dialog defined for network policy modify
var modifyNetworkPolicyPanel = null;
YAHOO.util.Event.onDOMReady(preparePanels4ModifyNetworkPolicy);
function preparePanels4ModifyNetworkPolicy() {
	var div = document.getElementById('modifyNetworkPolicyPanelId');
	modifyNetworkPolicyPanel = new YAHOO.widget.Panel(div, {
		width:"850px",
		underlay: "none",
		visible:false,
		draggable:false,
		close:false,
		modal:true,
		constraintoviewport:true,
		zIndex:999
		});
	modifyNetworkPolicyPanel.render(document.body);
	div.style.display = "";
}

function showModifyNetworkPolicyPanel(){
	if(null != modifyNetworkPolicyPanel){
		//modifyNetworkPolicyPanel.cfg.setProperty("context", ["globalSettingsId", "tr", "tr"]);
		modifyNetworkPolicyPanel.center();
		modifyNetworkPolicyPanel.cfg.setProperty('visible', true);
	}
}

function hideModifyNetworkPolicyPanel(){
	if(null != modifyNetworkPolicyPanel){
		set_innerHTML("modifyNetworkPolicyPanelContentId", "");
		modifyNetworkPolicyPanel.cfg.setProperty('visible', false);
	}
}

var defaultSelectedNetworkPolicyId = null;
function fetchNetworkPolicyModify2Page(arg) {
	if (!checkWhetherNetworkPolicySelected()) {
		return;
	}
	var curOpenedDrawerId = accordionView.getOpenedDrawerId();
	var save2Db = true;
	//if (curOpenedDrawerId == "selectNetWorkPolicy") {
		//if (hasLeftNetworkPolicySelect == false || getSingleSelectId("networkPolicys") != defaultSelectedNetworkPolicyId) {
	//		save2Db = true;
		//}
	//}
	var addedHiveId = -1;
	if (arg && arg.addedId) {
		addedHiveId = arg.addedId;
	}
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicyModify"
		 + "&networkPolicyId="+getSingleSelectId("networkPolicys")
		 + "&save2Db="+save2Db
		 + "&addedHiveId="+addedHiveId
		 + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchNetworkPolicyModify, failure : resultDoNothing, timeout: 60000}, null);
}
var succFetchNetworkPolicyModify = function(o) {
	set_innerHTML("modifyNetworkPolicyPanelContentId",
			o.responseText);
	setTimeout(function() {
		showModifyNetworkPolicyPanel();},
		50);
}

function doGlobalEditNetworkPolicy() {
	//if (hasOpenedSubDrawer == true) {
	//	return;
	//}
	fetchNetworkPolicyModify2Page();
}

function checkWhetherNetworkPolicySelected() {
	if (hm.util.getSelectedCheckItems("networkPolicys")==hm.util._LIST_SELECTION_NOITEM){
		warnDialog.cfg.setProperty('text', "The network policy list is empty.");
		warnDialog.show();
		return false;
	} else if (hm.util.getSelectedCheckItems("networkPolicys")==hm.util._LIST_SELECTION_NOSELECTION){
		warnDialog.cfg.setProperty('text', "Please choose network policy first.");
		warnDialog.show();
		return false;
	}
	return true;
}

function doRemoveNetworkPolicy() {
	if (!checkWhetherNetworkPolicySelected()) {
		return;
	}
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=removeNetworkPolicy"
		 + "&selectedIds="+hm.util.getSelectedCheckItems("networkPolicys")
		 + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succRemoveNetworkPolicy, failure : resultDoNothing, timeout: 60000}, null);
}

var succRemoveNetworkPolicy = function(o) {
	set_innerHTML(accordionView.getDrawerContentId('selectNetWorkPolicy'),
			o.responseText);
}

function doCloneNetworkPolicy() {
	if (!checkWhetherNetworkPolicySelected()) {
		return;
	}
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=networkPolicyCloneDlg"
		 + "&cloneSrcId="+getSingleSelectId("networkPolicys")
		 + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succShowCloneNetworkPolicyDlg, failure : resultDoNothing, timeout: 60000}, null);
}

var succShowCloneNetworkPolicyDlg = function(o) {
	set_innerHTML("modifyNetworkPolicyPanelContentId",
			o.responseText);
	showModifyNetworkPolicyPanel();
}

var curNetworkNewOrModifyType = '1';
function addNewHiveFromSelectNetworkPolicy() {
	saveTmpNetworkPolicyArgs();
	curNetworkNewOrModifyType = '1';
	var url = "<s:url action='hiveProfiles' includeParams='none' />?jsonMode=true&operation=new"
				+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewHiveProfiles, failure : resultDoNothing, timeout: 60000}, null);
}

var succNewHiveProfiles = function(o) {
	subDrawerOperation='createHiveProfile';
	openSelectNPSubDrawer({title: "New Hive", responseText: o.responseText});
	showHideSelectNetworkPolicySubSaveBT(true);
	hideSelectNetworkPolicyPanel();
}

function addModifyHiveFromSelectNetworkPolicy() {
	saveModifyTmpNetworkPolicyArgs();
	curNetworkNewOrModifyType = '2';
	var url = "<s:url action='hiveProfiles' includeParams='none' />?jsonMode=true&operation=new"
				+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewHiveProfiles4ModifyNp, failure : resultDoNothing, timeout: 60000}, null);
}

var succNewHiveProfiles4ModifyNp = function(o) {
	subDrawerOperation='createHiveProfile';
	openSelectNPSubDrawer({title: "New Hive", responseText: o.responseText});
	showHideSelectNetworkPolicySubSaveBT(true);
	hideModifyNetworkPolicyPanel();
}

function selectPpskServerIpDlg(ssidId){
	var url = "<s:url action='ssidProfilesSimple' includeParams='none' />?operation=fetchPpskServer"
			+ "&ssidId="+ssidId
		 	+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succFetchPpskServerIpDlg, failure : resultDoNothing, timeout: 60000}, null);
}

var succFetchPpskServerIpDlg = function (o){
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-hiveap-ppsk.png" width="40px" height="40px" '+
	'title="<s:text name="config.configTemplate.ppskreg.dialog.title" />" class="dialogTitleImg" />'+
	'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.configTemplate.ppskreg.dialog.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay();
};

//var selectedUserProfileSsidTmp = {};
</script>
<!-- Bonjour Gateway Profile : start -->
<script type="text/javascript">
function addRemoveBonjourGw(url) {
    if (!url) {
        var url = "<s:url action='networkPolicy' includeParams='none' />?operation=fetchSelectBonjourGw"
            + "&ignore="+new Date().getTime();
    }
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : fetchBonjourGwsList, failure : resultDoNothing, timeout: 60000}, null);
}
var fetchBonjourGwsList = function(o) {
    var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-bonjour-40x40.png" width="40px" height="40px" '+
        'title="<s:text name="config.v2.select.lan.profile.popup.title" />" class="dialogTitleImg" />'+
        '<span class="npcHead1" style="padding-left:10px;"><s:text name="config.v2.select.bonjourGw.profile.popup.title" /></span>';
    Get("hdDivSpan").innerHTML = tittle;
    set_innerHTML("bdDiv",o.responseText);
    openSubDialogOverlay("365px");
}
function viewBonjourGw(bonjourGwId){
    var url = "<s:url action='bonjourGatewaySettings' includeParams='none' />?operation=edit&id="
            + bonjourGwId + "&jsonMode=true&ignore="+new Date().getTime();
    openIFrameDialog(800, 450, url);
}
var finishSelectedBonjourGw = function (o){
    hideSubDialogOverlay();
    closeIFrameDialog();
    var htmlStr = '';
    if(Get("fwPolicyNameTd"))
        htmlStr = Get("fwPolicyNameTd").innerHTML.trim();
    eval("var data = " + o.responseText);
    if (data.result) {
        Get("bonjourGwTd").innerHTML = '<table cellspacing="0" cellpadding="0" border="0">'
            +'<tr>'
            +'<td class="imageTd" valign="middle"><img src="<s:url value="/images/hm_v2/profile/HM-icon-bonjour-30x30.png" />" width="30" height="30" alt="Bonjour Gateway" title="Bonjour Gateway" /></td>'
            +'<td style="padding-left: 5px;vertical-align: top;"><a class="npcLinkA" href="javascript:void(0);" onclick="viewBonjourGw('+data.bgId+')">'
            + data.bonjourGwName +'</a></td>'
            +'</tr>'
            +'</table>';
        if(htmlStr.length == 0 && Get("fwPolicyNameTd")) {
        	Get("fwPolicyNameTd").innerHTML = '<span style="height:43px; display:inline-block;"></span>';
        }
    } else {
    	// unselect
    	var pattern = /^<span/i;
    	if(Get("fwPolicyNameTd") && htmlStr.length !=0) {
    		if(pattern.test(htmlStr)) {
	    		Get("fwPolicyNameTd").innerHTML = '';
		        Get("bonjourGwTd").innerHTML='';
    		} else {
		        Get("bonjourGwTd").innerHTML='<span style="height:43px; display:inline-block;"></span>';
    		}
    	} else {
	        Get("bonjourGwTd").innerHTML='';
    	}
    }
};
</script>
<!-- end_wx_02: script for user profile(new/edit) -->
<div id="modifyNetworkPolicyPanelId" style="display: none;">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td width="100%">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ul"></td><td class="um" id="tdUM" style="width:770px"></td><td class="ur"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ml"></td>
			<td class="mm">
				<div id="modifyNetworkPolicyPanelContentId"></div>
			</td>
			<td class="mr"></td>
		</tr>
	</table>
</td></tr>
<tr><td width="100%">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="bl"></td><td class="bm" id="tdBM" style="width:770px"></td><td class="br"></td>
		</tr>
	</table>
</td></tr>
</table>
</div>
<script type="text/javascript">
	function openHelpLinkPage(url) {
		window.open(url, 'newHelpWindows', 'height=600, width=800, resizable=yes');
	}
</script>
<!-- VLAN section -->
<script>
function editVlan(vlanId) {
    var url = "<s:url action='vlan' includeParams='none' />?operation=edit&contentShowType=subdrawer&jsonMode=true&id="
    		+ vlanId + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
    		{success : succEditVlan, failure : resultDoNothing, timeout: 60000}, null);
}
var succEditVlan = function(o) {
    subDrawerOperation = "updateVLAN";
    // set the sub drawer title
    accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit VLAN"));
    accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
    var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');

    set_innerHTML(subDrawerContentId,o.responseText);
    notesTimeoutId = setTimeout("hideNotes()", 10000);
}
function saveVlanFromDrawer(operation) {
    var url = "<s:url action='vlan' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
    if(!validateVlanForJson(operation)){
        return false;
    }
    document.forms["vlan"].operation.value = operation;
    YAHOO.util.Connect.setForm(document.getElementById("vlan"));
    var transaction = YAHOO.util.Connect.asyncRequest('POST', url,
    		{success : succSaveVlanFromDrawer, failure : resultDoNothing, timeout: 60000}, null);
}
var succSaveVlanFromDrawer = function (o) {
    try {
        eval("var details = " + o.responseText);
    }catch(e){
        if(subDrawerOperation=='createVlan') {
        	newVlanProfile(o);
        } else {
        	succEditVlan(o);
        }
        return;
    }
    if (details.t) {
        backNetWorkPolicy();
        if(details.newState) {
        	selectVlans4Lan(details.lanId);
        } else {
            fetchConfigTemplate2Page(true);
        }
    }
}
</script>
<script>
function finishSelectUserProfileForCertainObj() {
	hideSubDialogOverlay();
	fetchConfigTemplate2Page(true);
}
function editUserProfileVlanMappingPanel(option) {
	hideSubDialogOverlay();
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=upVlanMapping&upVlanMappingType="+option.type
				+ "&upVlanRelativeId="+option.id
				+ "&parentIframeOpenFlg="+(("parentOpen" in option)?option.parentOpen:true)
				+ "&jsonMode=true&contentShowType=dlg&ignore="+new Date().getTime();
	openIFrameDialog(500, 450, url);
}
function displayVlanItemsPanel(el, option) {
	if (commonPopUpDlg.inShown()) {
		commonPopUpDlg.hide();
	}
	var vlanId = option;
	if (typeof option == "object") {
		if (option.id) {
			vlanId = option.id;
		} else if (option.mark) {
			vlanId = $(el).find(option.mark).val();
		}
	}
	blnNoHideIFrameNoModalNow = false;
	var elPos = $(el).offset();
	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=descVlanItems&vlanId="+vlanId+"&jsonMode=true&contentShowType=dlg&ignore="+new Date().getTime();
	commonPopUpDlg.open(url, {
						width: 400,
						height: 350
					})
					.noModal()
					.show()
					.moveTo(elPos.left - 175, elPos.top);
}
function hideIFrameNoModalPanel(blnForce, blnSleep) {
	if (noModalIFrameHideDelayHelper) {
		if (blnForce) {
			commonPopUpDlg.hide();
			//if(YAHOO.env.ua.ie){
				Get("dialogFrame").style.display = "none";
			//}
			noModalIFrameHideDelayHelper.clear();
		} else if (noModalIFrameHideDelayHelper.run(function(){
			commonPopUpDlg.hide();
			//if(YAHOO.env.ua.ie){
				Get("dialogFrame").style.display = "none";
			//}
		})) {
			noModalIFrameHideDelayHelper.clear();
		} else if (blnSleep) {
			noModalIFrameHideDelayHelper.sleep(true);
		}
	} else {
		commonPopUpDlg.hide();
		//if(YAHOO.env.ua.ie){
			Get("dialogFrame").style.display = "none";
		//}
	}
}

var blnNoHideIFrameNoModalNow = false;
var noModalIFrameHideDelayHelper = new hm.util.CommonDelayHelper(hideIFrameNoModalPanel, {
	delayTime: 100, 
	check: function(){return !blnNoHideIFrameNoModalNow;}
	});
</script>

<script>
function saveNetworkForMapping(operation) {
	var url = "<s:url action='vpnNetworks' includeParams='none' />?jsonMode=true&ignore="+new Date().getTime();
	if(!validateNetworkForJson(operation)){
		return false;
	}
	document.forms["vpnNetworks"].operation.value = operation;
	YAHOO.util.Connect.setForm(document.getElementById("vpnNetworks"));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveNetworkForMapping, failure : resultDoNothing, timeout: 60000}, null);
}

var succSaveNetworkForMapping = function (o) {
	try {
		eval("var details = " + o.responseText);
	}catch(e){
		if (subDrawerOperation=='createNetworkObject'){
			succNetworkObject(o);
		} else {
			succEditNetworkObject(o);
		}
		return;
	}
	if (details.t) {
		if (details.newState) {
			networkPolicyCallbackFn = null;
			backNetWorkPolicy();
		   	editVlanNetworkMappingNetwork(preVlanId, details.newId);
		} else {
			backNetWorkPolicy();
		}
	}
}

</script>

<script>
	var commonPopUpDlg = (function(){
		var commonPopDlg = null;
		var blnInShow = false;
		
		function prepareCommonPopDlg(src, options) {
			options = options || {};
			commonPopDlg = prepareBeforeOpenIFrameDialog(options.width||400, options.height||400, src);
			commonPopDlg.cfg.setProperty("fixedcenter", false);
		}
		
		function tobeNoModalDlg() {
			commonPopDlg.cfg.setProperty("modal", false);
			return this;
		}
		
		function showDialog() {
			if (commonPopDlg) {
				commonPopDlg.cfg.setProperty("visible", true);
			}
			blnInShow = true;
			showCallback();
			return this;
		}
		
		function hideDialog() {
			if (commonPopDlg) {
				commonPopDlg.cfg.setProperty("visible", false);
			}
			blnInShow = false;
			hideCallback();
			return this;
		}
		
		function openDialog(src, options) {
			prepareCommonPopDlg(src, options);
			return this;
		}
		
		function moveDialogTo() {
			if (!commonPopDlg) {
				return this;
			}
			var arg = arguments,
				len = arg.length;
			if (len === 0) {
				commonPopDlg.cfg.setProperty("fixedcenter", true);
			} else {
				commonPopDlg.cfg.setProperty("fixedcenter", false);
			}
			
			if (len >= 2) {
				if (typeof arg[0] === "string" || typeof arg[0] === "object") {
					commonPopDlg.cfg.setProperty("context", [arg[0], arg[1], (len>2)?arg[2]:arg[1]]);
				} else {
					commonPopDlg.cfg.setProperty("xy", [arg[0], arg[1]]);
				}
			}
			
			return this;
		}
		
		function isDialogShown() {
			return blnInShow;
		}
		
		return {
			open: openDialog,
			hide: hideDialog,
			show: showDialog,
			moveTo: moveDialogTo,
			inShown: isDialogShown,
			noModal: tobeNoModalDlg
		}
	})();
	
	function mouseEnterEvent() {
		blnNoHideIFrameNoModalNow = true;
		hideIFrameNoModalPanel(false, true);
	}
	function mouseLeaveEvent() {
		blnNoHideIFrameNoModalNow = false;
		hideIFrameNoModalPanel(true);
	}
	var $iframeDialog = $("#iframeDialog");
	function showCallback() {
		$iframeDialog.bind("mouseenter", mouseEnterEvent);
		$iframeDialog.bind("mouseleave", mouseLeaveEvent);
	}
	function hideCallback() {
		$iframeDialog.unbind("mouseenter", mouseEnterEvent);
		$iframeDialog.unbind("mouseleave", mouseLeaveEvent);
	};
</script>

<div id="updateBTN">
	<div id="toolsMenuDiv" class="yuimenu"></div>
	<div id="updateMenuDiv" class="yuimenu"></div>
</div>
<script>
var up_menu = null;
var g_oMenu = null;
var tooltip = null;
</script>