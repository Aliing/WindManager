<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.be.common.NmsUtil"%>

<script>
var timeout;
var flashTime = 0;
function blink() {
	flashTime++;
	var soccer = document.getElementById("soccer");
	if (flashTime < 10) {
		soccer.style.visibility = (soccer.style.visibility=="hidden")?"visible":"hidden";
		timeout = window.setTimeout("blink()", 800);
	} else {
		soccer.style.visibility = "visible";
	}
}
function changeAvailUpImage(over) {
	if (over) {
		window.clearTimeout(timeout);
	} else {
		blink();
	}
}

var initialHelpLinkUrl = '<s:property value="helpLink" />';
var  currentHelpLinkUrl = initialHelpLinkUrl;
function setCurrentHelpLinkUrl(url) {
	currentHelpLinkUrl = url;
}
function initializeCurrentHelpLinkUrl() {
	 currentHelpLinkUrl = initialHelpLinkUrl;
}
function openHelpPage() {
	window.open(currentHelpLinkUrl, 'newHelpWindows', 'height=600, width=800, resizable=yes');
}

var logoutMenu;
function logoutAction() {
	var width = 141;
	<s:if test="%{showStagingSwitch}">
		width = 67;
	</s:if>
	var lm_x = YAHOO.util.Dom.getX('td_logout') - width;
	var lm_y = YAHOO.util.Dom.getY('td_logout') + 26;
	logoutMenu.cfg.setProperty("xy", [lm_x, lm_y]);
	logoutMenu.show();
}
function logoutMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
	//	alert("This menu item is disabled.");
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	if (text == "Log Out") {
		var url = "<s:url value="logout.action" includeParams="none"/>";
		document.location.href = url;
	} else if (text != "<s:property value="switchVHMText"/>") {
		switchDomain(text);
	}
}

var sysName = '<s:property value="%{systemNmsName}"/>';
var args = ["<s:property value="%{selectedL1Feature.action}"/>", "<s:property value="%{selectedL2FeatureKey}"/>"];
function switchDomain(domainName) {
	var url = "<s:url action="switchDomain" includeParams="none" />?operation=switchDomain&domainName=" + encodeURIComponent(domainName) + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : domainSwitched }, null);
}
function domainSwitched(o) {
	eval("var result = " + o.responseText);
	if (result.success) {
		var td = window.document.getElementById('login_name');
		td.removeChild(td.firstChild);
		td.appendChild(document.createTextNode(result.dn));
		var redirect_url;
		if(args[0].length == 0 || args[1].length == 0 ){
			redirect_url = "<s:url value="index.jsp" includeParams="none"/>";
		}else{
			redirect_url = "<s:url action='" + args[0] + "' includeParams="none" />" + "?operation=" + args[1];
		}
		window.location.href = redirect_url;
	}else{
		if(result.msg && warnDialog != null){
			warnDialog.cfg.setProperty('text', result.msg);
			warnDialog.show();
		} else {
			window.location.href = "<s:url action='login' includeParams='none' />";	
		}
	}
}
var totalPage= "<s:property value="switchDomainDataSize"/>";
var currentPage=1;

function createLogoutMenu() {
	var cfg = {zindex: 1000,maxheight:100000,minscrollheight:100000};
	logoutMenu = new YAHOO.widget.Menu('logout_menu', cfg);
	var logoutItems;
	if (<s:property value="%{userContext != null && userContext.vadAdmin}" />) {
		// not contain 'All VHM' item for vad user
		logoutItems = 
				[
		       			[{text: "Log Out"}],
		       			[{text: "<s:property value="switchVHMText"/>", 
		       				submenu: {
		       					id: "switch", // Id for the submenu element to be created
		       					itemdata: [
		       						<s:iterator value="switchDomainDataCurrent" status="status">
		       							{text: "<s:property value="domainNameESC" escape="false" />", disabled: <s:property value="selected" />} ,
		       						</s:iterator>
		       					]
		       				}
		        		}]
		       	];
	} else {
		logoutItems = 
				[
		       			[{text: "Log Out"}],
		       			[{text: "<s:property value="switchVHMText"/>", 
		       				submenu: {
		       					id: "switch", // Id for the submenu element to be created
		       					itemdata: [
									{text: "<s:property value="allVHMsText"/>", disabled: <s:property value="showDomain" /> }
			       					<s:iterator value="switchDomainDataCurrent" status="status">
		       							,{text: "<s:property value="domainNameESC" escape="false" />", disabled: <s:property value="selected" />} ,
		       						</s:iterator>
			       				]
		       				}
		        		}]
		       	];
	}

	logoutMenu.addItems(logoutItems);
	logoutMenu.subscribe('click', logoutMenuItemClick);
	logoutMenu.render(document.body);
	// insert a header for filtering
	var filterInputId = YAHOO.util.Dom.generateId();
	var defaultMessage = "<s:property value="filterVHMText"/>";
	var vhmMenu = logoutMenu.getSubmenus()[0];
	vhmMenu.setHeader('<table><tr><td><a href="#1q" id="ssback"><img width="24px" height="24px" border="0" title="Previous" onMouseOver="this.src=\'<s:url value="/images/paging/prev_over.png" />\'" onMouseOut="this.src=\'<s:url value="/images/paging/prev.png" />\'" src="<s:url value="/images/paging/prev.png" includeParams="none"/>"/> </a></td><td> <label id="pageLabel">' + currentPage + '&nbsp; / &nbsp;'+  totalPage + '</label> </td><td>'+ '<a href="#2q" id="ssnext"><img width="24px" height="24px" border="0" title="Next" onMouseOver="this.src=\'<s:url value="/images/paging/next_over.png" />\'" onMouseOut="this.src=\'<s:url value="/images/paging/next.png" />\'" src="<s:url value="/images/paging/next.png" includeParams="none"/>"/></a></td> </tr> <tr><td colspan="3"> <input autocomplete="off" type="text" value="'+defaultMessage+'" id="'+filterInputId+'"></td></tr></table>');	// set input as a fixed length
	
	var hlength = vhmMenu.header.offsetWidth*0.9;
	YAHOO.util.Dom.setStyle(filterInputId, "width", (hlength<100?100:hlength)+"px");
	YAHOO.util.Dom.setStyle(vhmMenu.body, "zoom", "normal");
	
	YAHOO.util.Event.on("ssback", "click", function(){
		if (currentPage>1) {
			currentPage--;
			switchDomainPage(currentPage);
		} else {
			return;
		}
		//alert('ssback');
	}, vhmMenu, true);
	YAHOO.util.Event.on("ssnext", "click", function(){
		if (currentPage<totalPage) {
			currentPage++;
			switchDomainPage(currentPage);
		} else {
			return;
		}
	}, vhmMenu, true);
	
	YAHOO.util.Event.on(filterInputId, "keyup", function(){
		var inputElement = document.getElementById(filterInputId);
		YAHOO.util.Dom.setStyle(inputElement, "color", "#000");
		filterVhm(inputElement.value);
	}, vhmMenu, true);
	YAHOO.util.Event.on(filterInputId, "focus", function(){
		var inputElement = document.getElementById(filterInputId);
		if(inputElement.value == defaultMessage){
			inputElement.value = "";
			YAHOO.util.Dom.setStyle(inputElement, "color", "#000");
		}
	}, null, true);
	YAHOO.util.Event.on(filterInputId, "blur", function(){
		var inputElement = document.getElementById(filterInputId);
		if(inputElement.value.length == 0){
			inputElement.value = defaultMessage;
			YAHOO.util.Dom.setStyle(inputElement, "color", "#999");
		}
	}, null, true);
	
	function switchDomainPage() {
		var t =  document.getElementById(filterInputId).value;
		var url = "<s:url action="switchDomain" includeParams="none" />?operation=switchDomainPage&filterPara=" + t + "&domanCurrentPage=" + (currentPage -1) +"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : domainPageSwitched }, null);
	}
	function domainPageSwitched(o) {
		eval("var result = " + o.responseText);
		var value = result.v;
		totalPage=result.s;
		var vhmMenu = logoutMenu.getSubmenus()[0];
		var items = vhmMenu.getItems();
		for(var i=items.length-1; i>=0; i--){
			vhmMenu.removeItem(items[i]);
		}
		if (<s:property value="%{userContext != null && !userContext.vadAdmin}" />) {
			if (currentPage==1) {
				vhmMenu.addItem({text: "<s:property value="allVHMsText"/>", disabled: <s:property value="showDomain" /> });
			}
		}
		
		for(var i = 0; i < value.length; i ++)
		{
			vhmMenu.addItem(new YAHOO.widget.MenuItem(value[i]));
		}
		vhmMenu.render();
		document.getElementById('pageLabel').innerHTML= currentPage + "&nbsp; / &nbsp;"+  totalPage;
		if(YAHOO.env.ua.ie){//fix shadow issue
			vhmMenu.configShadow(null, [true], null);
		}
	}
	
	function filterVhm(t){
		currentPage=1;
		var url = "<s:url action="switchDomain" includeParams="none" />?operation=switchDomainPage&filterPara=" + t + "&domanCurrentPage=" + (currentPage -1) +"&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success : domainPageSwitched }, null);
	}
	/**
	function filterVhm(t, vhmMenu){
		var items = vhmMenu.getItems();
		for(var i=0; i<items.length; i++){
			var text = items[i].cfg.getProperty("text");
			if(t.length == 0 || text.toLowerCase().indexOf(t.toLowerCase()) == 0){
				YAHOO.util.Dom.setStyle(items[i].element, "display", "");
			}else{
				YAHOO.util.Dom.setStyle(items[i].element, "display", "none");
			}
		}
		vhmMenu.align();
		if(YAHOO.env.ua.ie){//fix shadow issue
			vhmMenu.configShadow(null, [true], null);
		}
	}**/
}
function fixLogoPng(){
	//var img = document.getElementById("logo_img");
	// to fix all png images in IE6.
	for(var i=0; i<document.images.length; i++){
		var img = document.images[i];
		var imgName = img.src;
        if (null != imgName && imgName.search(/.png/i) > 0){
        	var imgID = (img.id) ? "id='" + img.id + "' " : "";
        	var imgClass = (img.className) ? "class='" + img.className + "' " : "";
        	var imgTitle = (img.title) ? "title='" + img.title + "' " : "title='" + img.alt + "' ";
        	var imgStyle = "display:inline-block;" + img.style.cssText;
        	if (img.align == "left") imgStyle = "float:left;" + imgStyle
        	if (img.align == "right") imgStyle = "float:right;" + imgStyle
        	if (img.parentElement.href) imgStyle = "cursor:hand;" + imgStyle
        	var strNewHTML = "<span " + imgID + imgClass + imgTitle
        					+ " style=\"" + "width:" + img.width + "px; height:" + img.height + "px;" + imgStyle + ";"
        					+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
        					+ "(src=\'" + img.src + "\', sizingMethod='scale');\"></span>";
        	img.outerHTML = strNewHTML;
        	i--; // avoid it not take effect
        }
	}
}

var BrowserCheck = {
	$: function(o){
		return typeof o == 'string' ? document.getElementById(o):o;
	},
	init: function() {
		var type = BrowserCheck.unSupportMsg();
	    if (type != null && BrowserCheck.Cookie.get('ignore_browser_check') != 'on') {
	        if (BrowserCheck.$('browser_compatibility')) {
	        	BrowserCheck.$('browser_compatibility').innerHTML = BrowserCheck.getMessage(type);
	        	if(type == 'warning'){
	        		YAHOO.util.Dom.setStyle('browser_compatibility','width','650px');
	        	}else{
	        		YAHOO.util.Dom.setStyle('browser_compatibility','width','600px');
			    }
	        	YAHOO.util.Dom.setStyle('browser_compatibility','display','');
	        }
	    }
	    else {
	    	if (BrowserCheck.$('browser_compatibility')) {
	    		YAHOO.util.Dom.setStyle('browser_compatibility','width','0px');
	        	YAHOO.util.Dom.setStyle('browser_compatibility','display','none');
	        }
	    }
	},
	unSupportMsg: function(){
		var isChrome = window.MessageEvent&&!document.getBoxObjectFor;
		if(!YAHOO.env.ua.ie && !YAHOO.env.ua.gecko && !isChrome){
			return 'unsupport';
		}
		if(YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7){
			return 'warning';
		}
		return null;
	},
	getMessage: function(type) {
	    <s:if test="%{planningOnly||planner}">
		    var msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Planner does not support this browser. You might encounter problems if you continue using it.</div>";
		    if (type == 'warning') {
		        msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Because this is an earlier browser version, some advanced Planner features might not be supported.</div>";
		    }
	    </s:if>
	    <s:else>
		    var msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;"+sysName+" does not support this browser. You might encounter problems if you continue using it.</div>";
		    if (type == 'warning') {
		        msg = "<div style='float:left;'><img alt='Reminder' src='<s:url value="/images/reminder.png" includeParams="none"/>' width='16' class='dinl'></div><div style='float:left;'>&nbsp;&nbsp;&nbsp;Because this is an earlier browser version, some advanced "+sysName+" features might not be supported.</div>";
		    }
	    </s:else>
	    msg += ' <div style="float: right;"><a href="#closeCheck" onclick="BrowserCheck.ignore();"><img alt="Ignore it" title="Ignore it" src="<s:url value="/images/cancel.png" includeParams="none"/>" width="16" class="dinl"></a>'
	    return msg;
	},
	ignore: function() {
		if (BrowserCheck.$('browser_compatibility')) {
			YAHOO.util.Dom.setStyle('browser_compatibility','width','0px');
			YAHOO.util.Dom.setStyle('browser_compatibility','display','none');
	    }
		BrowserCheck.Cookie.erase('ignore_browser_check');
		BrowserCheck.Cookie.set('ignore_browser_check', 'on');
	},
	Cookie: {
	    set: function(name, value, daysToExpire) {
	        var expire = '';
	        if (!daysToExpire) daysToExpire = 365;
	        var d = new Date();
	        d.setTime(d.getTime() + (86400000 * parseFloat(daysToExpire)));
	        expire = 'expires=' + d.toGMTString();
	        var path = "path=/"
	        var cookieValue = escape(name) + '=' + escape(value || '') + '; ' + path + '; ' + expire + ';';
	        return document.cookie = cookieValue;
	    },
	    get: function(name) {
	        var cookie = document.cookie.match(new RegExp('(^|;)\\s*' + escape(name) + '=([^;\\s]+)'));
	        return (cookie ? unescape(cookie[2]) : null);
	    },
	    erase: function(name) {
	        var cookie = BrowserCheck.Cookie.get(name) || true;
	        BrowserCheck.Cookie.set(name, '', -1);
	        return cookie;
	    }
	}
}
YAHOO.util.Event.onDOMReady(function () {
	BrowserCheck.init();
});

var helpSettingPanel = null;
var aboutHMPanel = null;
function clickHelpSettingMenu()
{
	if (helpSettingPanel == null)
	{
		createHelpSettingPanel();
	}

	var url = "<s:url action='helpSetting' includeParams='none' />?operation=init&ignore=" + new Date().getTime();
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: initHelpSetting, failure:initHelpSetting,timeout: 10000}, null);
}

function initHelpSetting(o)
{
	eval("var result = " + o.responseText);
	
	document.getElementById('rdDefaultHelpDir').checked = result.useDefault;
	document.getElementById('rdHelpDir').checked = !result.useDefault;
	
	document.getElementById('helpDefaultDir').value=result.defaultDir;
	
	if (result.helpDir)
	{
		document.getElementById('helpDir').value=result.helpDir;
	}

	openHelpSettingPanel();
}

function createHelpSettingPanel()
{
	// create client refresh setting overlay
	var div = document.getElementById('helpSettingPanel');
	helpSettingPanel = new YAHOO.widget.Panel(div, {
		width:"600px",
		visible:false,
		//fixedcenter:true,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1
		});
	helpSettingPanel.render(document.body);
	div.style.display = "";
}

function openHelpSettingPanel()
{
	if(null != helpSettingPanel){
		helpSettingPanel.cfg.setProperty('x', YAHOO.util.Dom.getX('about')-parseInt(helpSettingPanel.cfg.getProperty('width'))+20);
		helpSettingPanel.cfg.setProperty('y', YAHOO.util.Dom.getY('about')+22);
		helpSettingPanel.cfg.setProperty('visible', true);
	}
}

function hideHelpSettingPanel(){
	if(null != helpSettingPanel){
		helpSettingPanel.cfg.setProperty('visible', false);
	}
}

function updateHelpSetting()
{
	var useDefault = document.getElementById('rdDefaultHelpDir').checked;
	var helpDir = document.getElementById('helpDir').value;

	var url = "<s:url action='helpSetting' includeParams='none' />?operation=updateHelpSetting&useDefault="+useDefault+"&helpDir="+encodeURIComponent(helpDir)+"&ignore=" + new Date().getTime();
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: updateHelpSettingResult, failure:updateHelpSettingResult,timeout: 10000}, null);
}

function updateHelpSettingResult(o)
{
	hideHelpSettingPanel();
	
	eval("var result = " + o.responseText);
	
	if (!result.success)
	{
		if(result.message && warnDialog != null){
			warnDialog.cfg.setProperty('text', result.message);
			warnDialog.show();
		}
	}
}

function clickHelpVideoMenu(){
	var url = '<s:property value="videoGuideLink" />';
	window.open(url, 'newHelpWindows', 'height=600, width=800,resizable=yes, scrollbars=yes');
}

function clickContactSupportMenu(){
	var url = '<s:property value="supportPageLink" />';
	window.open(url, 'newHelpWindows', 'height=600, width=800,resizable=yes, scrollbars=yes');
}

// contact support
var contactSupportPanel = null;

function openContactSupportPanel()
{
	if (contactSupportPanel == null)
	{
		createContactSupportPanel();
	}
	
	hm.util.hide("contactSupportNoteDiv");
	document.getElementById('contactSupportEmail').value = "<s:property value='userContext.emailAddress' />";
	document.getElementById('contactSupportPhone').value = '';
	document.getElementById('contactSupportMessage').value = '';
	document.getElementById('contactSupportCountry').value='';

	if(null != contactSupportPanel){
		//contactSupportPanel.cfg.setProperty('x', YAHOO.util.Dom.getX('about')-parseInt(helpSupportPanel.cfg.getProperty('width'))+20);
		//contactSupportPanel.cfg.setProperty('y', YAHOO.util.Dom.getY('about')+22);
		contactSupportPanel.cfg.setProperty('visible', true);
	}
}

function createContactSupportPanel()
{
	var div = document.getElementById('contactSupportPanel');
	contactSupportPanel = new YAHOO.widget.Panel(div, {
		width:"450px",
		autofillheight:"body",
		visible:false,
		draggable:true,
		modal:false,
		constraintoviewport:true,
		zIndex:1,
		fixedcenter:true
		});
	contactSupportPanel.render(document.body);
	div.style.display = "";
}

function hideContactSupportPanel(){
	if(null != contactSupportPanel){
		contactSupportPanel.cfg.setProperty('visible', false);
	}
}

function contactSupport() {
	var email = document.getElementById("contactSupportEmail");
	
	if(email.value.trim().length == 0) {
		hm.util.reportFieldError(email, '<s:text name="hm.config.guide.tellFriend.email.empty" />');
        email.focus();
		return ;
	}
	
	if (!hm.util.validateEmail(email.value))
	{
		hm.util.reportFieldError(email, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.user.emailAddress" /></s:param></s:text>');
		email.focus();
		return false;
	}
	
	var phone = document.getElementById("contactSupportPhone");
	
	if(phone.value.trim().length > 0 && !hm.util.checkInternationalPhone(phone.value)) {
		hm.util.reportFieldError(phone, '<s:text name="hm.config.guide.support.phone.invalid" />');
        phone.focus();
		return ;
	}
	
	var content = document.getElementById("contactSupportMessage");
	
	if(content.value.trim().length == 0) {
		hm.util.reportFieldError(content, '<s:text name="hm.config.guide.support.issues.empty" />');
        content.focus();
		return ;
	}
	
	var country = document.getElementById('contactSupportCountry');
    
   	var	url = "<s:url action='configGuide' includeParams='none' />?operation=support&reportedIssues=" 
				+ content.value.replace(/\n/g, "<br>") + "&userPhone=" + phone.value
				+ "&contactorEmail=" + email.value + "&country=" + country.value;
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: contactSupportResult, failure: contactSupportFail,timeout: 100000}, null);
	
	contactSupportNoteInfo("Your message is being sent...");
}

function contactSupportResult(o) {
	eval("var result = " + o.responseText);
	
	if (result.success) {
		contactSupportNoteInfo(result.msg);
	} else {
		contactSupportNoteError(result.msg);
	}
}

function contactSupportFail(o) {}

function contactSupportNoteInfo(info)
{
	document.getElementById('contactSupportNote').innerHTML = "<td id='contactSupportNote'>"+info+"</td>";
	document.getElementById('contactSupportNote').className="noteInfo";
	hm.util.show("contactSupportNoteDiv");
}

function contactSupportNoteError(error)
{
	document.getElementById('contactSupportNote').innerHTML = "<td id='contactSupportNote'>"+error+"</td>";
	document.getElementById('contactSupportNote').className="noteError";
	hm.util.show("contactSupportNoteDiv");
}

// contact support --- end

function clickAboutHMMenu()
{
	if (aboutHMPanel == null)
	{
		createAboutHMPanel();
	}
	
	aboutHMPanel.cfg.setProperty('visible', true);
}

function createAboutHMPanel()
{
	var div = document.getElementById('aboutHMPanel');
	var panelWidth;
	
	<s:if test="%{!oEMSystem}">
		panelWidth = "400px";
	</s:if>
	<s:else>
		panelWidth = "520px";
	</s:else>
	
	aboutHMPanel = new YAHOO.widget.Panel(div, {
		width:panelWidth,
		visible:false,
		fixedcenter:true,
		draggable:false,
		modal:true,
		constraintoviewport:true,
		zIndex:1
		});
	aboutHMPanel.render(document.body);
	div.style.display = "";
}

function hideAboutHMPanel(){
	if(null != aboutHMPanel){
		aboutHMPanel.cfg.setProperty('visible', false);
	}
}

var checkUpdateWaitingPanel;
function clickCheckUpdateMenu()
{
	var url = "<s:url action='helpSetting' includeParams='none' />?operation=checkUpdate&ignore=" + new Date().getTime();
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkUpdateResult, failure: checkUpdateResult,timeout: 60000}, null);

	if (checkUpdateWaitingPanel == null)
	{
		checkUpdateWaitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"260px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
		checkUpdateWaitingPanel.setHeader("Retrieving Information...");
		checkUpdateWaitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams='none'/>" />');
		checkUpdateWaitingPanel.render(document.body);
	}
	
	checkUpdateWaitingPanel.show();
}

function checkUpdateResult(o)
{
	eval("var result = " + o.responseText);
	
	if (!result.success)
	{
		checkUpdateWaitingPanel.hide();
		showInfoDialog("You have the most recent "+sysName+" image. There are no newer versions available.");
		return;
	}
	
	if (result.permit)
	{
		redirect_url = "<s:url action='updateSoftware' includeParams='none' />?operation=updateSoftware&updateSource=licenseServer";
		window.location.href = redirect_url;
	}
}

var helpMenu;
function createHelpMenu()
{
	helpMenu = new YAHOO.widget.Menu('help_menu', { fixedcenter: false });
	
	<s:if test="%{userContext.userGroup.GMUserGroup}">
		items = [
				 [
				 	{ text: '<s:text name="helpmenu.GMHelp"/>'},
				 	{ text: '<s:text name="helpmenu.aboutGM"/>'}
				 ]
				];
	</s:if>
	<s:else>
		items = [
				 [
				 	{ text: '<s:text name="helpmenu.HMHelp"/>'},
				 	{ text: '<s:text name="helpmenu.setting"/>'}
				 ]
				];
		
		<s:if test="%{!oEMSystem}">
			items.push({text: '<s:text name="helpmenu.video"/>'});
		</s:if>
		
		if (<%=NmsUtil.isHostedHMApplication()%>)
		{
			items.push({text: '<s:text name="helpmenu.support"/>'});
		}
		
		if (!<%=NmsUtil.isHostedHMApplication()%>)
		{
			<s:if test="%{!oEMSystem}">
				items.push({text: '<s:text name="helpmenu.checkUpdate"/>'});
			</s:if>
		}
		
		items.push({text: '<s:text name="helpmenu.aboutHM"/>'});
	</s:else>
	
	<s:if test="%{planningOnly}">
		items = [
				 [
				 	{ text: '<s:text name="helpmenu.PlannerHelp"/>'}
				 ]
				];
	</s:if>
	
	helpMenu.addItems(items);
	helpMenu.subscribe('click', helpMenuItemClick);
	helpMenu.render(document.body);
}

function helpMenuItemClick(p_sType, p_aArguments) {
	var event = p_aArguments[0];
	var menuItem = p_aArguments[1];
	if(menuItem.cfg.getProperty("disabled") == true){
		return;
	}
	var text = menuItem.cfg.getProperty("text");
	
	if (text == '<s:text name="helpmenu.setting"/>') {
		clickHelpSettingMenu();
	} else if (text == '<s:text name="helpmenu.support"/>') {
		//openContactSupportPanel();
		clickContactSupportMenu(); // fix bug 26033, not open send mail dialog, just open aerohive support page
	} else if (text == '<s:text name="helpmenu.video"/>') {
		clickHelpVideoMenu();
	} else if (text == '<s:text name="helpmenu.aboutHM"/>') {
		clickAboutHMMenu();
	} else if (text == '<s:text name="helpmenu.aboutGM"/>') {
		clickAboutHMMenu();
	} else if (text == '<s:text name="helpmenu.checkUpdate"/>') {
		clickCheckUpdateMenu();
	} else if (text == '<s:text name="helpmenu.HMHelp"/>') {
		openHelpPage();
	} else if (text == '<s:text name="helpmenu.GMHelp"/>') {
		openHelpPage();
	} else if (text == '<s:text name="helpmenu.PlannerHelp"/>'){
		openHelpPage();
	}
}

function showHelpMenu()
{
	if (helpMenu == null)
	{
		createHelpMenu();
	}

	var x = YAHOO.util.Dom.getX('about');
	var y = YAHOO.util.Dom.getY('about');
	YAHOO.util.Dom.setX('help_menu', x - 70);
	YAHOO.util.Dom.setY('help_menu', y+20);
	helpMenu.show();
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

<s:if test="%{!topoSearch}">
	<script type="text/javascript">
	var arrayStates = [
		"MAC=",
		"IP=",
		"Devices=",
		"Configuration=",
		"Clients=",
		"Events & Alarms=",
		"Administration=",
		"Tools="
	];
	function searchAutoComplete()
	{
		YAHOO.example.BasicLocal = function() {
		    // Use a LocalDataSource
		    var oDS = new YAHOO.util.LocalDataSource(arrayStates);
		    // Optional to define fields for single-dimensional array
		    oDS.responseSchema = {fields : ["searchKey"]};
		
		    // Instantiate the AutoComplete
		    var oAC = new YAHOO.widget.AutoComplete("searchkey", "searchInputContainer", oDS);
		    oAC.prehighlightClassName = "yui-ac-prehighlight";
		    oAC.useShadow = true;
		    oAC.typeAhead = true;
		    oAC.autoHighlight = false;
		    
		    return {
		        oDS: oDS,
		        oAC: oAC
		    };
		}();
		// fix bug in IE that the search field not visiable when window onload and resize.
		if (YAHOO.env.ua.ie) {
			var timer_searchbox = 0;
			function triggerDisplaySearchContainer(){
				var searchContainer = document.getElementById('searchContainer');
				if(searchContainer){
					clearTimeout(timer_searchbox);
					timer_searchbox = setTimeout(function(){
						YAHOO.util.Dom.setStyle('searchContainer', 'visibility', 'hidden');
						YAHOO.util.Dom.setStyle('searchContainer', 'visibility', 'visible');
					}, 50);
				}
			}
			YAHOO.util.Event.addListener(window, 'load', triggerDisplaySearchContainer);
			YAHOO.util.Event.addListener(window, 'resize', triggerDisplaySearchContainer);
		}
		//resize search div
		var middleTD = document.getElementById('middleTD');
		if (middleTD == null) {
			return;
		}
		var vpHeight = YAHOO.util.Dom.getViewportHeight();
		var pagingTD = document.getElementById('pagingTD');
		if (pagingTD == null) {
			return;
		}
		
		var extra = vpHeight - YAHOO.util.Dom.getY(pagingTD) - 50 + parseInt(middleTD.height) + YAHOO.util.Dom.getDocumentScrollTop();
		if (YAHOO.env.ua.ie == 7) {
			extra += 2;
		}
		if (YAHOO.env.ua.webkit > 0) {	
	//		extra -= 2;
		}
		
		if (extra > 0) {
			middleTD.height = extra;
		} else {
			middleTD.height = 1;
		}
	}
	
	var searchWaitPanel;
	function createSearchWaitPanel() {
		// Initialize the temporary Panel to display while waiting for external content to load
		searchWaitPanel = new YAHOO.widget.Panel('wait',
				{ width:"260px",
				  fixedcenter:true,
				  close:false,
				  draggable:false,
				  zindex:4,
				  modal:true,
				  visible:false
				}
			);
		searchWaitPanel.setHeader("Searching...");
		searchWaitPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
		searchWaitPanel.render(document.body);
	}
	
	function topoSearch()
	{
		var searchKey = document.getElementById('searchkey');
		if (searchKey.value.length == 0)
		{
			searchKey.style.backgroundColor="#FF0000";
			return;
		}
		
		var url = "<s:url action='search' includeParams='none' />" + "?operation=topoSearch&searchKey="+encodeURIComponent(searchKey.value)+"&ignore=" + new Date().getTime();
	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: topoSearchResult, failure:topoSearchResult,timeout: 90000}, null);
		
		if (searchWaitPanel==null)
		{
			createSearchWaitPanel();
		}
		searchWaitPanel.show();
	}
	
	function topoSearchResult(o)
	{
		if (searchWaitPanel!=null)
		{
			searchWaitPanel.hide();
		}
		
		eval("var result = " + o.responseText);
		if (result.success && result.url)
		{
			window.location.href = result.url;
		}
		else
		{
			document.getElementById('searchkey').style.backgroundColor="#FF0000";
		}
	}
	
	function searchFieldInput(searchKey)
	{
		searchKey.style.backgroundColor="#FFFFFF";
	}
	
	function hmSearch()
	{
		var searchKey = document.getElementById('searchkey');
		if (searchKey.value.length == 0)
		{
			searchKey.style.backgroundColor="#FF0000";
			return;
		} else if (arrayStates.toString().indexOf(searchKey.value) >= 0 && searchKey.value.indexOf("=") > 0)
		{
			searchKey.style.backgroundColor="#FF0000";
			return;
		} 
	
		// maybe warn dialog here for long search time consuming
		var url = "<s:url action='search' includeParams='none' />" + "?operation=hmSearch&searchKey="+encodeURIComponent(searchKey.value)
		   +"&includeConfig="+document.getElementById("search_config").checked+"&includeHiveAP="+document.getElementById("search_hiveap").checked
		   +"&includeClient="+document.getElementById("search_client").checked+"&includeAdmin="+document.getElementById("search_admin").checked
		   +"&includeFault="+document.getElementById("search_fault").checked+"&includeTool="+document.getElementById("search_tool").checked
		   +"&ignore=" + new Date().getTime();
	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: searchResult, failure:searchResult,timeout: 90000}, null);
		
		if (searchWaitPanel==null)
		{
			createSearchWaitPanel();
		}
		searchWaitPanel.show();
	}
	
	function searchResult(o)
	{
		if (searchWaitPanel!=null)
		{
			searchWaitPanel.hide();
		}
		
		eval("var result = " + o.responseText);
		
		if (result.success)
		{
			var redirect_url;
			if (result.url) {
				redirect_url = result.url;
			} else if(args[0].length == 0 || args[1].length == 0 ){
				redirect_url = "<s:url value="index.jsp" includeParams="none"/>";
			} else {
				redirect_url = "<s:url action='" + args[0] + "' includeParams="none" />" + "?operation=" + args[1];
			}
			window.location.href = redirect_url;
		} else 
		{
			document.getElementById('searchkey').style.backgroundColor="#FF0000";
			if(result.errMsg) {
				showWarnDialog(result.errMsg);
			}
		}
	}
	
	function closeSearchPanel()
	{
		var url = "<s:url action='search' includeParams='none' />" + "?operation=closePanel&ignore=" + new Date().getTime();
	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: searchResult, failure:searchResult,timeout: 10000}, null);
	}
	
	function searchPaging(operation)
	{
		var url = "<s:url action='search' includeParams='none' />" + "?operation="+operation
		  +"&searchGotoPage="+document.getElementById('searchGotoPageId').value+"&ignore=" + new Date().getTime();
	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: searchResult, failure:searchResult,timeout: 10000}, null);
		
		if (searchWaitPanel!=null)
		{
			searchWaitPanel.show();
		}
	}
	
	function resizeSearchPage()
	{
		var url = "<s:url action='search' includeParams='none' />" 
		 + "?operation=resizePage&searchPageSize="+document.getElementById('searchPageSize').value+"&ignore=" + new Date().getTime();
	
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: searchResult, failure:searchResult,timeout: 10000}, null);
		
		if (searchWaitPanel!=null)
		{
			searchWaitPanel.show();
		}
	}
	
	var searchScopePanel = null
	var anim = null;
	function toggleSearchScope()
	{	
		if (YAHOO.util.Dom.getStyle("searchScopeDiv","display") == 'block')
		{
			searchScopePanel.hide();
			document.getElementById('searchScopeDiv').style.display="none";
		}
		else
		{
			if(null == searchScopePanel)
			{
				createSearchScopePanel();
			}
			searchScopePanel.show();
			document.getElementById('searchScopeDiv').style.display="block";
			anim.animate();
		}
	}
	
	function createSearchScopePanel()
	{
		searchScopePanel = new YAHOO.widget.Overlay("searchScopeDiv",{ visible: true, context:["searchContainer","tl","bl"]});
		searchScopePanel.render(document.body);
		document.getElementById("searchScopeDiv").style.display = "";
		var x = YAHOO.util.Dom.getX('searchContainer');
		var y = YAHOO.util.Dom.getY('searchContainer');
        anim = new YAHOO.util.Motion('searchScopeDiv',YAHOO.util.Easing.easeIn);
        anim.attributes = {  points: {from: [x, y], to: [x, y+24] } };
	}
	
	function searchFieldPress(e,topo)
	{
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
		
		if (keycode == 13)
		{
			if (topo)
			{
				topoSearch();
			}
			else
			{
				hmSearch();
			}
		}
	
		return true;
	}
</script>
</s:if>

<script>
	function tlmi(m) {
		location.href = m+"&ts=top&start="+new Date().getTime();
	}
</script>

<style>

#searchContainer{width: 180px; padding-top: 2px;height:22px;}
.searchElement{display: block;float: left;}

#switch div.hd, #reassign_menu div.hd{background-color: #FFF; border-width: 1px 1px 0px; border-style: solid solid none; border-color: #999; text-align: center;}
#switch div.bd, #reassign_menu div.bd{border-top: 0px; }
#switch div.hd input, #reassign_menu div.hd input{margin: 4px 12px 2px; border: 1px solid #999; border-color: #888 #ccc #ccc; background-color: #FFF; color: #999; background: url(<s:url value="/images/Search-PMS296_10x10.png" includeParams="none"/>) no-repeat 98% center}
</style>
<s:if test="%{easyMode}">
	<div style="position: relative;" class="skin_hme">
</s:if>
<s:else>
	<div style="position: relative;">
</s:else>
<div id="browser_compatibility" class="unsupported"
	style="display: none;"></div>
<s:if test="%{fullScreenMode}">
	<div style="display: none;">
</s:if>
<s:else>
	<div>
</s:else>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<s:if test="%{easyMode}">
			<td style="padding-left: 5px;" class="top_nav_bg" width="111"><img
				id="logo_img" src="<s:url value="/images/company_logo.png"/>"
				width="111" height="48" alt="" class="dblk"></td>
			<td class="top_nav_bg" width="100%">&nbsp;</td>
			<td width="680"><img
				src="<s:url value="/images/hm/header-light-graphic.png" includeParams="none"/>"
				width="680" alt="" class="dblk"></td>
		</s:if>
		<s:else>
			<td class="top_nav_bg" style="padding-left: 5px;">
				<s:if test="%{oEMSystem}">
					<img id="logo_img" src="<s:url value="/images/company_logo_reverse.png"/>"
						width="255" height="55" alt="" class="dblk">
				</s:if>
				<s:else>
					<img id="logo_img" src="<s:url value="/images/company_logo_reverse.png"/>"
						width="128" height="55" alt="" class="dblk">
				</s:else>
			</td>
			<td class="top_nav_bg" width="100%" colspan="2"></td>
			<td class="top_nav_bg" width="28"><div style="width: 28px;"></div></td>
		</s:else>
		<td class="top_nav_right_bg" align="right">
		<table border="0" cellspacing="0" cellpadding="0">
			<s:if test="%{teacherView}">
				<tr>
					<td class="hm_logo"><b>Teacher</b>View</td>
				</tr>
				<tr>
					<td class="hm_version" align="right" valign="bottom" colspan="2">
						<s:property
							value="%{versionInfo.getTvMainVer()}" />r<s:property
							value="%{versionInfo.getTvSubVer()}" /><s:text name="hm.version.subversion"/>
					</td>
				</tr>
			</s:if>
			<s:else>
				<tr>
					<s:if test="%{showUpdateIcon && 'updateSoftware' != selectedL2Feature.key}">
						<td valign="middle" rowspan="2" align="center">
						<div id="soccer">
							<a href="<s:url action="updateSoftware"><s:param name="operation" value="%{'updateSoftware'}" />
							<s:if test="%{isInHomeDomain}">
								<s:param name="updateSource" value="%{'licenseServer'}" />
							</s:if>
							</s:url>"
							onmouseover="javascript:changeAvailUpImage(true);"
							onmouseout="javascript:changeAvailUpImage(false);"> <img
							name="avaiUpImage" src="<s:url value="/images/hm/bee-glow.png"/>"
							alt="Available Updates" border="0" title="Available Updates">
						<script type="text/javascript">blink();</script> </a></div>
						</td>
					</s:if>
					<s:if test="%{planningOnly||planner}">
						<td class="hm_logo">Planner</td>
					</s:if>
					<s:else>
						<s:if test="%{userContext.userGroup.GMUserGroup}">
							<td class="hm_logo"><b>User</b>Manager</td>
						</s:if>
						<s:else>
							<s:if test="%{oEMSystem}">
								<td class="hm_logo" nowrap="nowrap"><s:property value="%{systemNmsNameTop}" escapeHtml="false"/></td>
							</s:if>
							<s:else>
								<td class="hm_logo"><s:property value="%{systemNmsNameTop}" escapeHtml="false"/></td>
							</s:else>
						</s:else>
					</s:else>
					<td class="hm_logo" style="padding: 0 5px 0 2px; font-size: 9px;"
						valign="top">&#174</td>
				</tr>
				<tr>
					<td class="hm_version" align="right" valign="bottom" colspan="2">
					<s:if test="%{!(planningOnly||planner)}">
						<s:if test="%{easyMode}">Express&nbsp;&nbsp;</s:if>
						<s:elseif test="%{fullMode}">Enterprise&nbsp;&nbsp;</s:elseif>
					</s:if>
					<s:property value="%{versionInfo.getMainVersion()}" />r<s:property
						value="%{versionInfo.getSubVersion()}" /><s:text name="hm.version.subversion"/>
					</td>
				</tr>
			</s:else>
		</table>
		</td>
	</tr>
</table>
</div>
<s:if test="%{userContext != null && userContext.navigationTree.childNodes.size() > 0}">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="top_nav_tab">
</s:if><s:else>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
</s:else>
	<tr>
		<td class="top_menu_bg">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="10"><img
					src="<s:url value="/images/spacer.gif" includeParams="none"/>"
					width="10" alt="" class="dblk"></td>
				<s:if test="%{null == licenseMessage && !hideTopPannel}">
					<s:iterator id="feature"
						value="%{userContext.navigationTree.childNodes}" status="status">
						<s:if test="%{selectedL1Feature.key == #feature.key}">
							<s:set name="selected" value="%{true}" />
							<td class="top_menu_bg_sel top_menu_item_sel" nowrap="nowrap"><a
								class="top_menu_sel" href="#tlm" id="userSelectNav"
								onclick='tlmi("<s:url action="topMenu" includeParams="none"><s:param name="operation" value="%{#feature.key}"/></s:url>");'>
								<s:property value="shortDescription" />
								</a></td>
						</s:if>
						<s:else>
							<s:if test="%{!#status.first && !#selected}">
								<s:if test="%{easyMode}">
									<td width="4"><img
										src="<s:url value="/images/hm/nav-gray-divider.png"/>"
										width="4" alt="" class="dblk"></td>
								</s:if>
								<s:else>
									<td width="2"><img
										src="<s:url value="/images/hm/nav-yellow-divider.gif"/>"
										width="2" alt="" class="dblk"></td>
								</s:else>
							</s:if>
							<s:set name="selected" value="%{false}" />
							<td class="top_menu_item" nowrap="nowrap"><a
								class="top_menu" href="#tlm"
								onclick='tlmi("<s:url action="topMenu" includeParams="none"><s:param name="operation" value="%{#feature.key}"/><s:param name="shortDescription" value="%{#feature.shortDescription}"/></s:url>");'>
								<s:property value="shortDescription" />
								</a></td>
						</s:else>
					</s:iterator>
				</s:if>
			</tr>
		</table>
		</td>
		<s:if test="%{topoSearch}">
			<td class="top_menu_bg"></td>
		</s:if>
		<s:else>
			<td class="top_menu_bg"><s:if
				test="%{(null == licenseMessage && !hideTopPannel) && showSearchField}">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img width="30" height="1" src="<s:url value="/images/spacer.gif" includeParams="none"/>"/></td>
							<td>
								<div id="searchContainer">
									<s:textfield id="searchkey"
										name="searchResult.searchKey" maxlength="100"
										value="%{searchResult.searchKeyShow}"
										onkeypress="return searchFieldPress(event,false);"
										onkeydown="searchFieldInput(this);" />
									<div id="searchInputContainer"></div>
								</div>
							</td>
							<td>
								<div style="width: 40px; padding-left: 5px;">
									<a class="searchElement" href="#search" onclick="hmSearch();"> <img class="dinl"
										src="<s:url value="/images/search/Search-WarmGrey11.png" />"
										width="20" height="20" alt="Search" title="Search" /> </a>
								<s:if test="%{easyMode}">
									<img
										src="<s:url value="/images/hm/nav-gray-divider.png"/>" width="4"
										alt="" class="dinl searchElement">
								</s:if>
								<s:else>
									<img
										src="<s:url value="/images/hm/nav-yellow-divider.gif"/>"
										width="2" alt="" class="dinl searchElement">
								</s:else>
								
									<a class="searchElement" href="#search"
										onclick="toggleSearchScope();"> <img class="dinl"
										src="<s:url value="/images/search/DownArrow-WarmGrey11.png" />"
										width="10" height="20" title="Search Scope" /> </a>
								</div>
							</td>
						</tr>
					</table>
			</s:if></td>
		</s:else>
		<td class="top_menu_bg" width="100%">&nbsp;</td>
		<s:if
			test="%{userContext != null && userContext.getUserName().length() > 0}">
			<s:if test="%{!easyMode}">
				<td class="top_menu_bg" width="28"><div style="width: 28px;"></div></td>
			</s:if>
			<td class="top_menu_bg">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if
						test="%{userContext != null && userContext.getUserName().length() > 0}">
						<td id="login_name" class="top_right_menu_bg top_menu_item"
							style="color: #444;" nowrap="nowrap"><s:property
							value="%{userContext.domainUserName}" /></td>
						<s:if test="%{easyMode}">
							<td width="4" id="td_logout"><img
								src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
								width="4" alt="" class="dblk"></td>
						</s:if>
						<s:else>
							<td width="2" id="td_logout"><img
								src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
								width="2" alt="" class="dblk"></td>
						</s:else>
						<s:if test="%{showStagingSwitch}">
							<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
								class="top_right_menu" href="<s:url action="logout" includeParams="none"><s:param name="operation" value="%{'switchMyHive'}"/></s:url>">MyHive</a></td>
							<s:if test="%{easyMode}">
								<td width="4"><img
									src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
									width="4" alt="" class="dblk"></td>
							</s:if>
							<s:else>
								<td width="2"><img
									src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
									width="2" alt="" class="dblk"></td>
							</s:else>
						</s:if>
						<s:if
							test="%{showLogoutMenu && null == licenseMessage && !hideTopPannel}">
							<script>createLogoutMenu();
							</script>
							<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
								class="top_right_menu" href="#logout" onclick="logoutAction();">Log
							Out ...</a></td>
						</s:if>
						<s:else>
							<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
								class="top_right_menu"
								href="<s:url value="logout.action" includeParams="none"/>">Log
							Out</a></td>
						</s:else>
					</s:if>
					<s:else>
						<td class="top_right_menu_bg top_menu_item" nowrap="nowrap"><a
							class="top_right_menu"
							href="<s:url value="login.action" includeParams="none"/>">Log
						In</a></td>
					</s:else>
					<s:if test="%{easyMode}">
						<td width="4"><img
							src="<s:url value="/images/hm/nav-blue-divider.png" includeParams="none"/>"
							width="4" alt="" class="dblk"></td>
					</s:if>
					<s:else>
						<td width="2"><img
							src="<s:url value="/images/hm/nav-brown-divider.gif" includeParams="none"/>"
							width="2" alt="" class="dblk"></td>
					</s:else>
					<%--
						Learning from network that whatever a vertical or horizontal scrollbar will be automatically rolled when the 'onclick' is being triggered.
					    To overcome this problem, two ways might be selected.
					    1. Appending a statement 'return false;' to the javascript:call(); e.g. onclick="javascript:openHelpPage(); return false;"
					    2. Using href="#this" instead of href="#"

					    Modified by Yang on Nov. 12th, 2008
					--%>
					<td class="top_right_menu_bg top_menu_item" id="about"
						nowrap="nowrap" style="padding-right: 2px;">
						<s:if test="%{teacherView}">
							<a class="top_right_menu" href="#about" onclick="clickAboutHMMenu();">About</a>
						</s:if>
						<s:else>
							<a class="top_right_menu" href="#help" onclick="showHelpMenu();">Help...</a>
						</s:else>
					</td>
					<td width="100%" class="top_right_menu_bg">&nbsp;</td>
				</tr>
			</table>
			</td>
		</s:if>
		<s:else>
			<td class="top_menu_bg">&nbsp;</td>
			<td class="top_menu_bg" width="100%">&nbsp;</td>
		</s:else>
	</tr>
</table>
</div>

<div id="helpSettingPanel" style="display: none;">
<div class="hd">Help Settings</div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td height="10"></td>
			</tr>
			<tr>
				<td style="padding-left: 5px;" colspan="2"><s:text
					name="config.helpSetting.helpdir" /></td>
			</tr>
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td style="padding-left: 35px;"><s:radio label="Gender" id=""
					list="#{'rdDefaultHelpDir':''}" name="helpDirSelect" /></td>
				<td><s:textfield id="helpDefaultDir" name="ignore" cssStyle="width:500px"
					readonly="true" /></td>
			</tr>
			<tr>
				<td height="10"></td>
			</tr>
			<tr>
				<td style="padding-left: 35px;"><s:radio label="Gender" id=""
					list="#{'rdHelpDir':''}" name="helpDirSelect" /></td>
				<td><s:textfield id="helpDir" name="ignore" cssStyle="width:500px" /></td>
			</tr>
			<tr>
				<td height="10"></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 8px;">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<s:if test="%{!inPassiveNode}">
					<input type="button" name="ignore" value="Update"
						id="ignore" class="button" onClick="updateHelpSetting();">
					</s:if>
				</td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" onClick="hideHelpSettingPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="contactSupportPanel" style="display: none;">
<div class="hd"><s:text name="hm.config.guide.support" /></div>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
		<table class="settingBox" cellspacing="0" cellpadding="0" border="0"
			width="100%">
			<tr>
				<td>
				<div id="contactSupportNoteDiv" style="display: none">
				<table width="350px" border="0" cellspacing="0" cellpadding="0"
					class="note">
					<tr>
						<td height="5px"></td>
					</tr>
					<tr>
						<td id="contactSupportNote"></td>
					</tr>
					<tr>
						<td height="5px"></td>
					</tr>
				</table>
				</div>
				<div style="margin: 5px;">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td><s:text name="hm.config.guide.support.email" /><font
							color="red">*</font></td>
						<td><s:textfield id="contactSupportEmail"
							cssStyle="width: 200px;" /></td>
					</tr>
					<tr>
						<td height="10px"></td>
					</tr>
					<tr>
						<td><s:text name="hm.config.guide.support.phone" /></td>
						<td><s:textfield id="contactSupportPhone" maxlength="32"
							cssStyle="width: 200px;"
							onkeypress="return hm.util.keyPressPermit(event,'phoneNum');" />
						</td>
					</tr>
					<tr>
						<td height="10px"></td>
					</tr>
					<tr>
						<td><s:text name="hm.config.guide.support.country" /></td>
						<td><s:textfield id="contactSupportCountry" maxlength="128"
							cssStyle="width: 200px;" /></td>
					</tr>
					<tr>
						<td height="10px"></td>
					</tr>
					<tr>
						<td valign="top" style="padding: 0 6px 0 2px"><s:text
							name="hm.config.guide.support.issues" /><font color="red">*</font>
						</td>
						<td><s:textarea cssStyle="width: 200px;"
							id="contactSupportMessage" rows="5" /></td>
					</tr>
				</table>
				</div>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="10px"></td>
	</tr>
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="button" name="ignore" value="Submit"
					class="button" onClick="contactSupport();"></td>
				<td><input type="button" name="ignore" value="Cancel"
					class="button" onClick="hideContactSupportPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<div id="aboutHMPanel" style="display: none;">
<s:if test="%{teacherView}">
<div class="hd">About TeacherView</div>
</s:if>
<s:else>
<div class="hd"><s:if test="%{userContext.userGroup.GMUserGroup}">
			About UserManager
		</s:if> <s:else>
			About <s:property value="%{systemNmsName}"/>
		</s:else></div>
</s:else>
<div class="bd">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td style="padding-left: 5px;"><img id="logo_img"
			src="<s:url value="/images/company_logo.png"/>" 
			alt="" class="company_logo"></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<s:if test="%{hMOnline && domain.domainName!='home'}">
				<tr>
					<td class="labelT1" width="100px"><s:text
						name="admin.vhmMgr.vhmID" />:</td>
					<td class="labelT1"><s:property value="domain.vhmID" /></td>
				</tr>
			</s:if>
			<tr>
				<td class="labelT1" width="100px">Software Version:</td>
				<s:if test="%{teacherView}">
					<td class="labelT1"><s:property
					value="sessionVersionInfo.getTvMainVer()" />r<s:property
					value="sessionVersionInfo.getTvSubVer()" /><s:text name="hm.version.subversion"/></td>
				</s:if>
				<s:else>
					<td class="labelT1"><s:property
						value="sessionVersionInfo.getMainVersion()" />r<s:property
						value="sessionVersionInfo.getSubVersion()" /><s:text name="hm.version.subversion"/></td>
				</s:else>
			</tr>
			<tr>
				<td class="labelT1" width="100px">Build Time:</td>
				<td class="labelT1"><s:property
					value="sessionVersionInfo.getBuildTime()" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding: 10px 4px 10px 4px" colspan=2>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="sepLine"><img
					src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td align="right" colspan="2">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="button" name="ignore" value="OK"
					class="button" onClick="hideAboutHMPanel();"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</div>

<s:if test="%{!topoSearch}">
	<div id="searchScopeDiv" style="display: none;">
	<div class="bd">
	<table class="editBox" border="0" cellspacing="0" cellpadding="0"
		width="150px">
		<tr>
			<td height="5px"></td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchConfig}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_config"
				name="igore" value="%{searchResult == null || searchResult.config}" />Configuration</td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchAP}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_hiveap"
				name="igore" value="%{searchResult == null || searchResult.hiveap}" />Devices</td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchClients}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_client"
				name="igore" value="%{searchResult == null || searchResult.client}" />Clients</td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchAdmin}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_admin"
				name="igore" value="%{searchResult == null || searchResult.admin}" />Administration</td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchTool}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_tool"
				name="igore" value="%{searchResult == null || searchResult.tool}" />Tools</td>
		</tr>
		<tr style="display:<s:property value="%{hideSearchFault}"/>">
			<td style="padding-left: 5px"><s:checkbox id="search_fault"
				name="igore" value="%{searchResult != null && searchResult.fault}" />Events
			and Alarms</td>
		</tr>
		<tr>
			<td height="5px"></td>
		</tr>
	</table>
	</div>
	</div>
</s:if>

<s:if
	test="%{userContext != null && userContext.getUserName().length() > 0}">
	<s:if test="%{showUpgradeWarning}">
		<div id="upgradeWarningPanel" style="display: none">
		<div class="hd"><s:property value="upgradeWarningTitle" /></div>
		<div class="bd">
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td><span style="color: red"> <s:property
					value="upgradeWarningInfo" /> </span></td>
			</tr>
			<tr>
				<td height="16"></td>
			</tr>
			<tr>
				<td style="padding-right: 10px;" align="right"><input
					type="button" name="ignore" value="Close" class="button"
					onClick="closeUpgradeWarningPanel();"></td>
			</tr>
		</table>
		</div>
		</div>
		<script type="text/javascript">
var upgradeWarningPanel = null;

createUpgradeWarningPanel();
showUpgradeWarningPanel();

function createUpgradeWarningPanel() {
	var div = document.getElementById('upgradeWarningPanel');
		upgradeWarningPanel = new YAHOO.widget.Panel(div, {
			width:"500px",
			visible:false,
			fixedcenter:true,
			draggable:true,
			constraintoviewport:true,
			modal:true,
			close:true,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration: 1}
			});
		upgradeWarningPanel.render(document.body);
		div.style.display ="";
}
	
function showUpgradeWarningPanel() {
	upgradeWarningPanel.cfg.setProperty('visible', true);
}

function closeUpgradeWarningPanel() {
	upgradeWarningPanel.cfg.setProperty('visible', false);		
}
</script>
	</s:if>
</s:if>

<s:if test="%{showUserRegInfoWarning && 'licenseMgr' != selectedL2Feature.key}">
	<div id="userRegInfoWarningPanel" style="display: none">
	<div class="hd"><s:text name="admin.license.send.userReg.info.pannel.title" /></div>
		<div class="bd">
		<s:form action="licenseMgr">
		<s:hidden name="operation" />
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tr>
				<td>
					<tiles:insertDefinition name="userRegisterInfo" />
				</td>
			</tr>
		</table>
		</s:form>
		</div>
	</div>
<script type="text/javascript">
var userRegInfoWarningPanel = null;

createUserRegInfoWarningPanel();
showUserRegInfoWarningPanel();

function createUserRegInfoWarningPanel() {
	var div = document.getElementById('userRegInfoWarningPanel');
		userRegInfoWarningPanel = new YAHOO.widget.Panel(div, {
			width:"540px",
			visible:false,
			fixedcenter:true,
			draggable:false,
			constraintoviewport:true,
			modal:true,
			close:false,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration: 1}
			});
		userRegInfoWarningPanel.render(document.body);
		div.style.display ="";
}
	
function showUserRegInfoWarningPanel() {
	userRegInfoWarningPanel.cfg.setProperty('visible', true);
}

function closeUserRegInfoWarningPanel() {
	userRegInfoWarningPanel.cfg.setProperty('visible', false);		
}
</script>
</s:if>

<s:if test="%{null != sessionNotificationMessages && !sessionNotificationMessages.isEmpty()}">
<tiles:insertDefinition name="notificationMsg" />
</s:if>

<script>
<s:if test="%{null != userContext}">
window.onfocus= function (){
	var c_domainUserName = getCookie("c_domainUserName")
	if(c_domainUserName == null || c_domainUserName == ""){
		var url = "<s:url action='helpSetting' includeParams='none' />" + "?operation=searchDomainUserName&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: searchDomainUserNameResult, failure:searchDomainUserNameResult,timeout: 10000}, null);
	} else {
		if(c_domainUserName != '<s:property value="%{userContext.domainUserId}" />'){
			window.location.assign(window.location.href);
		}
	}
}

setCookie("c_domainUserName",'<s:property value="%{userContext.domainUserId}" />');

function searchDomainUserNameResult(o)
{
	eval("var result = " + o.responseText);
	
	if (result.success){
		c_domainUserName = result.domainUserName;
	} else {
		c_domainUserName="";
	}
	if(c_domainUserName!= null && c_domainUserName != ""
		&& c_domainUserName != '<s:property value="%{userContext.domainUserId}" />'){
		window.location.assign(window.location.href);
	}
}

function getCookie(c_name){
	if(document.cookie.length>0){
		var c_start= document.cookie.indexOf(c_name+"=");
		if(c_start!=1){
			c_start=c_start+c_name.length+1;
			var c_end= document.cookie.indexOf(";",c_start);
			if(c_end == -1) c_end=document.cookie.length;
			return unescape(document.cookie.substring(c_start,c_end));
		}
	}
	return "";
}

function setCookie(c_name,value){
	document.cookie=c_name+"="+escape(value)+";"
}
</s:if>
</script>