<%@taglib prefix="s" uri="/struts-tags"%>
<script src="<s:url value="/js/widget/dialog/panel.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script type="text/javascript">
//=====================================
function addRemovePortItem(portTemplateId){
    var url = "<s:url action='networkPolicy' includeParams='none' />?operation=addRemovePortItem"
        	+ '&portTemplateId=' + portTemplateId
                + "&ignore="+new Date().getTime();
    
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : fetchPortsItemList, failure : resultDoNothing, timeout: 60000}, null);
}
var fetchPortsItemList = function(o) {
 	try{
		eval("var result = " + o.responseText);
		if(result.err){
			warnDialog.cfg.setProperty('text', result.err);
			warnDialog.show();
			return;
		}
	}catch(e){ 
		var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-LAN_Profile.png" width="40px" height="40px" '+
        'title="<s:text name="config.port.item.title" />" class="dialogTitleImg" />'+
        '<span class="npcHead1" style="padding-left:10px;"><s:text name="config.port.item.title" /></span>';
    Get("hdDivSpan").innerHTML = tittle;
    set_innerHTML("bdDiv",o.responseText);
    $("#x").attr("href","javascript:submitPortItemAction();");
    openSubDialogOverlay("850px");
	}
}
//=====================================================================
function addRemovePort(url, wirelessMode) {
    if (!url) {
        var url = "<s:url action='networkPolicy' includeParams='none' />?operation=listPortTemplates"
                + (wirelessMode ? "&wirelessMode=" + wirelessMode : "")
                + "&ignore="+new Date().getTime();
    }
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : fetchPortsList, failure : resultDoNothing, timeout: 60000}, null);
}
var fetchPortsList = function(o) {
    var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-LAN_Profile.png" width="40px" height="40px" '+
        'title="<s:text name="config.v2.select.port.profile.popup.title" />" class="dialogTitleImg" />'+
        '<span class="npcHead1" style="padding-left:10px;"><s:text name="config.v2.select.port.profile.popup.title" /></span>';
    Get("hdDivSpan").innerHTML = tittle;
    set_innerHTML("bdDiv",o.responseText);
    openSubDialogOverlay("600px");
}
// --- New ---
function clickNewPort(wirelessMode) {
    subDrawerCloneOperation = "";
    var limitType = 0;
    if(Get('networkPolicyTemplate_configType4Port')) {
    	limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
    var additionalEditFlag = false;
    var url = "<s:url action='portConfigure' includeParams='none' />?operation=new&jsonMode=true" +
    		"&contentShowType=dlg" +
    		"&limitType="+limitType +
            "&parentNpId="+Get("networkPolicyTemplate_id").value +
            (wirelessMode ? "&wirelessMode=" + wirelessMode : "") + "&ignore=" +new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : newPort, failure : resultDoNothing, timeout: 60000, argument: [additionalEditFlag]}, null);
}

var newPort = function(o) {
    openPortTemplPanel(o.responseText, o.argument[0]);
}
function clonePort(portId) {
    var ports = new Array();
    ports.push(portId);
    var limitType = 0;
    if(Get('networkPolicyTemplate_configType4Port')) {
        limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
    var additionalEditFlag = false;
    var url = "<s:url action='portConfigure' includeParams='none' />?operation=clone&selectedIds=" + ports
    		+ "&contentShowType=dlg" 
    		+ "&limitType=" + limitType
            + "&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : newPort, failure : resultDoNothing, timeout: 60000, argument: [additionalEditFlag]}, null);
}
//listPortTemplates
function checkIsNonDefault(portId){
	var clickNodeClassName = document.getElementById('dataTable'+ portId+'Span').className;
	var clickNodeColor = document.getElementById('dataTable'+ portId+'Span').parentNode.style.backgroundColor;
	var check = document.getElementById('dataTable'+ portId+'Span').parentNode.getElementsByTagName("span")[0].style.display;
	if(clickNodeClassName != "word-wrap1"){
		if(null != clickNodeColor && clickNodeColor != "" && check == "inline-block"){
			var url = "<s:url action='networkPolicy' includeParams='none' />?selectedId=" + portId +"&ignore="+new Date().getTime();
			 document.forms['listPortTemplates'].operation.value = "checkNonDefaultTemplates";
			    YAHOO.util.Connect.setForm(document.getElementById('listPortTemplates'));
			    var transaction = YAHOO.util.Connect.asyncRequest('post', url, 
			            {success : succHaveNonDefaultTemplates, failure : resultDoNothing, timeout: 60000,argument: [portId]}, null);  
		}else{
			var selectRow = document.getElementById("dataTable"+portId+"Span").parentNode;
			hm.util.selectRowAndGrayOutOthers(selectRow, null, portId);
		}
	}
		 
	
}
var succHaveNonDefaultTemplates = function (o){
	 try {
	        eval("var details = " + o.responseText);
		    //======================================================================
	    	if(details.defaultSelectedId != undefined && details.defaultSelectedId != null){
	    		var defaultSelectedId = details.defaultSelectedId;
		    	var selectRow = document.getElementById("dataTable"+defaultSelectedId+"Span").parentNode;
		    	hm.util.selectRowAndGrayOutOthers(selectRow, null, defaultSelectedId);
		    	return;	
	    	}
	    	//=======================================================================
	    }catch(e){
	    	var selectRow = document.getElementById("dataTable"+o.argument[0]+"Span").parentNode;
	    	hm.util.selectRowAndGrayOutOthers(selectRow, null, o.argument[0]);
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
	    Get("errNote").innerHTML=details.err;
	    setTimeout("hideErrNotes()", 10000);
}
//--- Continue Select ---
function selectedPort() {
    var itemValue = hm.util.getSelectedCheckItems("selectedPortTempalteIds");
    if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
        Get("errNote").innerHTML="There is no item.";
        return;
    }
    var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms["listPortTemplates"].operation.value = "selectedPortTemplates";
    YAHOO.util.Connect.setForm(document.getElementById("listPortTemplates"));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url,
            {success : succSelectedPort, failure : resultDoNothing, timeout: 60000}, null);
}
var succSelectedPort = function (o){
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
    Get("errNote").innerHTML=details.err;
    setTimeout("hideErrNotes()", 10000);
};
function viewPort(lanId, viewAdditionalSettings,selectTmpIdStr,networkPolicyId){
	
	var limitType = 0;
    if(Get('networkPolicyTemplate_configType4Port')) {
        limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
    var additionalEditFlag = (viewAdditionalSettings ? viewAdditionalSettings : false);
    var url = "<s:url action='portConfigure' includeParams='none' />?operation=edit&jsonMode=true&limitType="+limitType
    		+ "&contentShowType=dlg"
    		+ "&currentPolicyID=" + (networkPolicyId? networkPolicyId : -1)
    		+ "&selectIDs=" + (selectTmpIdStr? selectTmpIdStr : "")
    		+ "&editAdditionalSettings=" + additionalEditFlag
    		+"&id="+ lanId + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : editPort, failure : resultDoNothing, timeout: 60000, argument: [additionalEditFlag]}, null);
}
function viewPortAddition(lanId, portIndex){
	
	var limitType = 0, indexStr = '';
    if(Get('networkPolicyTemplate_configType4Port')) {
        limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
    if(portIndex !== undefined && portIndex >= 0) {
    	indexStr = "&portTemplateIndex=" + portIndex
    }
    var url = "<s:url action='portConfigure' includeParams='none' />?operation=edit&jsonMode=true&limitType="+limitType
    		+ "&contentShowType=dlg"
    		+ "&editAdditionalSettings=true"
    		+ indexStr
    		+"&id="+ lanId + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : editPort, failure : resultDoNothing, timeout: 60000, argument: [true]}, null);
}
var editPort = function(o) {
	openPortTemplPanel(o.responseText, o.argument[0]);
};
function removePort(portId) {
    var url = "<s:url action='portConfigure' includeParams='none' />?operation=remove&jsonMode=true" 
    		+ "&contentShowType=dlg" 
    		+ "&allSelectedIds="+ portId 
    		+ "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
    		{success : delPort, failure : resultDoNothing, timeout: 60000, argument: [portId]}, null);
}
var delPort = function(o) {
	eval("var details="+o.responseText);
    var hideErrNotes = function () {
        hm.util.wipeOut('errNote', 810);
    }
	if(details.succ) {
		var arg = o.argument[0];
		var selectRow = document.getElementById("dataTable"+o.argument[0]+"Span").parentNode;
    	hm.util.selectRowAndGrayOutOthers(selectRow, null, o.argument[0]);	
		if(arg) {
			  var $spanEl = $('#selectedPortTempalteIdsDiv').find('#dataTable'+arg+'Span');
			  if($spanEl.length) {
				  $spanEl.parent().parent().remove();
				  fetchConfigTemplate2Page(true);
			  }
		}
	} else {
	    hm.util.show("errNote");
	    Get("errNote").className="noteError";
	    Get("errNote").innerHTML=details.errMsg;
	    setTimeout("hideErrNotes()", 10000);
	}
}
function savePortTemplate(operation) {
    if(validatePortForm && typeof(validatePortForm) == 'function') {
        if(!validatePortForm()) return;
    }
    var url = "<s:url action='portConfigure' includeParams='none' />?operation="+ operation
            +"&jsonMode=true&ignore="+new Date().getTime();
    YAHOO.util.Connect.setForm(document.getElementById("portConfigure"));
    var transaction = YAHOO.util.Connect.asyncRequest('POST', url,
            {success : savePort, failure : resultDoNothing, timeout: 60000}, null);
}
var savePort = function(o) {
   try {
		eval("var details = " + o.responseText);
	}catch(e){
		return;
	}
    if (details.succ) {
    	closePortTemplPanel();
        if (details.newState){
            hideSubDialogOverlay();
            var url = "<s:url action='networkPolicy' includeParams='none' />?operation=listPortTemplates"
                + "&createPortTempalteIds=" + details.id
                + "&ignore="+new Date().getTime();
            addRemovePort(url);
        } else if (details.updateAdditional) {
        	hideSubDialogOverlay();
        	// need to refresh
            var params = "";
        	if(details.updateId) {
        		params += "&expandPortTemplateId="+details.updateId;
        	}
            if(details.portTemplateIndex !== undefined && details.portTemplateIndex > -1) {
                params += "&tmpIndex="+details.portTemplateIndex;
            }
            if(params) {
                fetchConfigTemplate2Page(true, false, params);
            }
        } else {
            if(subDialogOverlay.cfg.getProperty('visible') == false) {
                openSubDialogOverlay();
            }else{
            	var url = "<s:url action='networkPolicy' includeParams='none' />?operation=listPortTemplates"
            		 + "&updatePortTempalteIds=" + details.updateId
                    + "&ignore="+new Date().getTime();
                addRemovePort(url);
            }
        }
    } else {
    	if(details.error && details.msg) {
            hm.util.reportFieldError({id: 'ActionErrorRow'}, details.msg);
    	}else if(details.err){
    		  var hideErrNotes = function () {
    		        hm.util.wipeOut('errNote', 800);
    		    }

    		    hm.util.show("errNote");
    		    Get("errNote").className="noteError";
    		    Get("errNote").innerHTML=details.err;
    		    setTimeout("hideErrNotes()", 10000);
    	}
    }
}
var portTemplPanel;
function openPortTemplPanel(text, additionSettings) {
	if(null == portTemplPanel) {
		portTemplPanel = new YAHOO.Aerohive.YUI2.widget.Panel('portTemplatePanel', 
				{width: 700, close: false, maxHeight: 530});
	}
	if(text) {
		set_innerHTML("portTemplatePanel",text);
	}
	hideTempSubDialogOverlay();
	portTemplPanel.openDialog(!additionSettings);
}
function closePortTemplPanel() {
	if(portTemplPanel) {
		portTemplPanel.closeDialog();
		
	}
	var el = Get('portConfigure_editAdditionalSettings');
	if(!el || el.value == "false") {
	    if(subDialogOverlay.cfg.getProperty('visible') == false) {
	        subDialogOverlay.cfg.setProperty('visible', true);
	    }
	}
	Get('portTemplatePanel').innerHTML = '';
}
function hideTempSubDialogOverlay() {
    if(null != subDialogOverlay){
        subDialogOverlay.cfg.setProperty('visible', false);
   }
}
//========Access Profile============
function addRemoveAccess(url, portId, deviceType, portNum, urlStr,index) {
    if (!url) {
        var url = "<s:url action='portAccess' includeParams='none' />?operation=listAccess4Port"
                + "&portTemplateId="+portId
                + "&deviceType="+deviceType
                + "&portNum="+portNum
                + (urlStr ? urlStr : "")
                + (index ? '&tmpIndex='+index : "")
                + "&ignore="+new Date().getTime();
    }
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : fetchAccessList, failure : resultDoNothing, timeout: 60000}, null);
}
var fetchAccessList = function(o) {
    var tittle = '<img src="/hm/images/hm_v2/profile/HM-icon-LAN_Profile.png" width="40px" height="40px" '+
        'title="<s:text name="config.v2.select.port.profile.popup.title" />" class="dialogTitleImg" />'+
        '<span class="npcHead1" style="padding-left:10px;"><s:text name="config.v2.select.access.profile.popup.title" /></span>';
    Get("hdDivSpan").innerHTML = tittle;
    set_innerHTML("bdDiv",o.responseText);
    openSubDialogOverlay("360px");
}
function clearAccessButton(portTemplateId, portNum, deviceType, tmpIndex) {
	var args = {id: portTemplateId, portNum: portNum, deviceType: deviceType, tmpIndex: tmpIndex};
    var cancelBtn = function(){
        this.hide();
    };
    var continueBtn = function(){
        this.hide();
        $('#wirePortGroupSection_' + portTemplateId + (tmpIndex >= 0 ? '_'+ tmpIndex : '')).portsConfig("configure", args, function() {
            var params = portTemplateId ? "&expandPortTemplateId="+portTemplateId : "";
            if(tmpIndex && tmpIndex != -1){
                params = params + "&tmpIndex="+tmpIndex;
            }
            fetchConfigTemplate2Page(true, false, params);
        });
    };
    var mybuttons = [ { text:"Yes", handler: continueBtn }, 
                      { text:"No", handler: cancelBtn, isDefault:true} ];
    var dlg = userDefinedConfirmDialog('<s:text name="warn.port.template.clearConfig"/>', 
            mybuttons, "Warning");
    dlg.show();
}
function selectedAccess() {
    var itemValue = hm.util.getSelectedCheckItems("selectedAccessIds");
    if(hm.util._LIST_SELECTION_NOITEM == itemValue) {
    	Get("errNote").innerHTML="There is no item.";
    	return;
    }
    var portTemplateId = Get('listPortAccess_portTemplateId').value;
    var tmpIndex = Get('listPortAccess_tmpIndex').value;
    var portNum = Get('listPortAccess_portNum').value;
    var deviceType = Get('listPortAccess_deviceType').value;
    var flag = false;
    if($('div[id^=wirePortGroupSection_'+ portTemplateId +'_]').length > 0 && tmpIndex != -1){
    	flag = $('#wirePortGroupSection_'+portTemplateId+'_'+tmpIndex).portsConfig("existConfiguredPorts");
    }else{
    	 flag = $('#wirePortGroupSection_'+portTemplateId).portsConfig("existConfiguredPorts");
    }
    var args = {id: portTemplateId, portNum: portNum, deviceType: deviceType, tmpIndex: tmpIndex};
    
    if(flag) {
        var cancelBtn = function(){
            this.hide();
            if(null != subDialogOverlay){
                subDialogOverlay.cfg.setProperty('visible', true);
            }
        };
        var continueBtn = function(){
            this.hide();
            continueSelectedAccess(itemValue, args);
        };
    }
    
    if(hm.util._LIST_SELECTION_NOSELECTION == itemValue) {
    	if(flag) {
	        hideTempSubDialogOverlay();
	        var mybuttons = [ { text:"Yes", handler: continueBtn }, 
	                          { text:"No", handler: cancelBtn, isDefault:true} ];
	        var dlg = userDefinedConfirmDialog('<s:text name="warn.port.template.clearConfig"/>', 
	                mybuttons, "Warning");
	        dlg.show();
    	} else {
    		Get("errNote").innerHTML='<s:text name="warn.port.template.noConfig"/>';
    		return;
    	}
    } else {
    	if(flag && typeof flag === 'object' && !$.isEmptyObject(flag)) {
	        hideTempSubDialogOverlay();
	        var message = '<s:text name="warn.port.template.chgAgg"/>', index = 0;
	        message += '<ul>';
	        for(key in flag) {
	        	message += '<li>'+'<s:text name="warn.port.template.removePortFromAgg"><s:param>'
	        	  +flag[key]+'</s:param><s:param>'+key+'</s:param></s:text>'+'</li>';
	        }
	        message += '</ul>';
	        var aggButtons = [ { text:"Yes", handler: continueBtn }, 
	                          { text:"No", handler: function(){this.hide();}, isDefault:true} ];
	        var aggDlg = userDefinedConfirmDialog(message, aggButtons, "Warning");
	        aggDlg.show();
    	} else {
    		continueSelectedAccess(itemValue, args);
    	}
    }
}
function continueSelectedAccess(itemValue, arguments) {
	debug("continue SelectedAccess...");
	Get("errNote").innerHTML= "";
	if($.isArray(itemValue) && itemValue.length>0) {
		arguments.selectedAccessIds = itemValue;
	}
    var portTemplateId = Get('listPortAccess_portTemplateId').value;
    var tmpIndex = Get('listPortAccess_tmpIndex').value;
    var portNum = 0, deviceType = 0;
    var callback = function(result) {
    	if(result.succ) {
    		if(result.monitor) {
    		    if(Get('listPortAccess_portNum')) {
    		    	portNum = Get('listPortAccess_portNum').value;
    		    } 
    		    if(Get('listPortAccess_deviceType')) {
    		    	deviceType = Get('listPortAccess_deviceType').value;
    		    }
    		    
    		    var url = "<s:url action='portConfigure' includeParams='none' />?operation=editMonitor&jsonMode=true"
    		    		+ "&id="+result.id 
    		    		+ "&selectedAccessIds=" + itemValue
    		    		+ "&deviceType" + deviceType
    		    		+ "&portNum" + portNum
    		    		+ "&ignore=" +new Date().getTime();
    		    YAHOO.util.Connect.asyncRequest('GET', url,
    	            {success : openMirrorPanel, failure : resultDoNothing, timeout: 60000}, null);
    		} else {
    			if(result.aggWarning) {
    				showWarnDialog(result.aggWarning);
    			}
	            hideSubDialogOverlay();
	            var params = portTemplateId ? "&expandPortTemplateId="+portTemplateId : "";
	            if(tmpIndex && tmpIndex != -1){
	            	params = params + "&tmpIndex="+tmpIndex;
	            }
	    		fetchConfigTemplate2Page(true, false, params);
    		}
    	} else {
    		debug('error..' + result.errMsg);
    		if(result.errMsg) {
    			//showWarnDialog(result.errMsg);
    	         if(null != subDialogOverlay && subDialogOverlay.cfg.setProperty('visible') === false){
    	                subDialogOverlay.cfg.setProperty('visible', true);
    	            }
    			Get("errNote").innerHTML=result.errMsg;
    		}
    	}
    };
    if($('div[id^=wirePortGroupSection_'+ portTemplateId +'_]').length > 0 && tmpIndex != -1){
  	  $('#wirePortGroupSection_' + portTemplateId +'_'+ tmpIndex).portsConfig("configure", arguments, callback);
    }else{
  	  $('#wirePortGroupSection_'+ portTemplateId).portsConfig("configure", arguments, callback);
    }
}
var mirrorPanel;
function openMirrorPanel(o) {
    try {
        eval("var details = " + o.responseText);
        if(details.errMsg) {
        	showWarnDialog(details.errMsg);
        }
    }catch(e){
        hideSubDialogOverlay();
	    if(null == mirrorPanel) {
	    	mirrorPanel = new YAHOO.Aerohive.YUI2.widget.Panel('portMirrorPanel', 
	                {width: 700, close: false, disabledESC: true, maxHeight: 530});
	    }
	    mirrorPanel.openDialog();
	    set_innerHTML("portMirrorPanel", o.responseText);
        return;
    }
}
var succSelectedAccess = function (o){
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
    Get("errNote").innerHTML=details.err;
    setTimeout("hideErrNotes()", 10000);
};
function clickNewPortAccess(cloneFlag, portId) {
    subDrawerCloneOperation = "";
    var limitType = 0;
    if(Get('networkPolicyTemplate_configType4Port')) {
        limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
    var portNum = 0;
    if(Get('listPortAccess_portNum')) {
    	portNum = Get('listPortAccess_portNum').value;
    } 
    var deviceType = 0;
    if(Get('listPortAccess_deviceType')) {
    	deviceType = Get('listPortAccess_deviceType').value;
    }
    var portTemplateId = 0; 
    if(Get('listPortAccess_portTemplateId')) {
    	portTemplateId = Get('listPortAccess_portTemplateId').value;
    }
    var tmpIndex = -1;
    if(Get('listPortAccess_tmpIndex')) {
    	tmpIndex = Get('listPortAccess_tmpIndex').value;
    }
    var url = "<s:url action='portAccess' includeParams='none' />?operation=new";
    var prevAccStr = null;
    if(cloneFlag) {
    	var ports = [];
        ports.push(portId);
    	url = "<s:url action='portAccess' includeParams='none' />?operation=clone&selectedIds=" + ports;
    	prevAccStr = '&selectedAccessId='+portId;
    } else {
    	var itemValue = hm.util.getSelectedCheckItems("selectedAccessIds");
    	if($.isArray(itemValue) && itemValue.length>0) {
    		prevAccStr = '&selectedAccessIds='+itemValue;
   	    }
    }
    url += "&contentShowType=dlg" + "&limitType=" + limitType 
            + "&portNum=" + portNum 
            + "&deviceType=" + deviceType 
            + "&portTemplateId=" + portTemplateId + "&tmpIndex=" + tmpIndex + "&jsonMode=true&ignore="+new Date().getTime();
    
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : newPortAccess, failure : resultDoNothing, timeout: 60000}, null);

    networkPolicyCallbackFn = function() {
        addRemoveAccess(null, portTemplateId, deviceType, portNum, prevAccStr,tmpIndex);
    };
}
var newPortAccess = function(o) {
    hideSubDialogOverlay();

    subDrawerOperation= "createPortAccess";
    // set the sub drawer title
    if(subDrawerCloneOperation == "clonePortAccess") {
        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Clone Port Types"));
    } else {
        accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New Port Types"));
    }
    accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
    // get the sub drawer content
    var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
    set_innerHTML(subDrawerContentId, o.responseText);
    notesTimeoutId = setTimeout("hideNotes()", 10000);
}

function viewPortAccess(lanId, normalView){
	var editId = lanId;
    var portNum = 0, deviceType = 0, portTemplateId = 0;
    var limitType = 0, tmpIndex = -1;
    if(lanId == 0) {
    	return;
    }
    if(Get('networkPolicyTemplate_configType4Port')) {
        limitType = Get('networkPolicyTemplate_configType4Port').value;
    }
	if(normalView) {
		networkPolicyCallbackFn = null;
	} else {
	    if(Get('listPortAccess_portNum')) {
	        portNum = Get('listPortAccess_portNum').value;
	    } 
	    if(Get('listPortAccess_deviceType')) {
	        deviceType = Get('listPortAccess_deviceType').value;
	    }
	    if(Get('listPortAccess_portTemplateId')) {
	        portTemplateId = Get('listPortAccess_portTemplateId').value;
	    }
	    if(Get('listPortAccess_tmpIndex')) {
	    	tmpIndex = Get('listPortAccess_tmpIndex').value;
	    }
	    networkPolicyCallbackFn = function() {
	        addRemoveAccess(null, portTemplateId, deviceType, portNum, lanId ? "&selectedAccessId="+lanId : null,tmpIndex);
	    };
		
	}
    var url = "<s:url action='portAccess' includeParams='none' />?operation=edit&jsonMode=true&limitType="+limitType
		    + "&portNum=" + portNum
		    + "&deviceType=" + deviceType
		    + "&portTemplateId=" + portTemplateId
		    + "&tmpIndex=" + tmpIndex
    		+"&id=" + editId + (normalView ? "&normalView=" + normalView : "") + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url,
            {success : editPortAccess, failure : resultDoNothing, timeout: 60000}, null);
}
var editPortAccess = function(o) {
	hideSubDialogOverlay();
	
    subDrawerOperation= "updatePortAccess";
    // set the sub drawer title
    accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("Edit Port Types"));
    accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
    // get the sub drawer content
    var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
    set_innerHTML(subDrawerContentId, o.responseText);
    notesTimeoutId = setTimeout("hideNotes()", 10000);
}
function savePortAccessProfile(operation) {
    if(typeof(validatePortAccessForm) === 'function') {
        if(!validatePortAccessForm()) return;
    }
    var continueSavePortAccess = function() {
	    var url = "<s:url action='portAccess' includeParams='none' />?operation="+ operation
	            +"&jsonMode=true&ignore="+new Date().getTime();
	    YAHOO.util.Connect.setForm(document.getElementById("portAccess"));
	    var transaction = YAHOO.util.Connect.asyncRequest('POST', url,
	            {success : savePortAccess, failure : resultDoNothing, timeout: 60000}, null);	
    };
    if(typeof(promptPortTypeChanged) === 'function') {
    	if(promptPortTypeChanged()) {
		    var cancelBtn = function(){
		        this.hide();
		    },
		    continueBtn = function(){
		        this.hide();
		        continueSavePortAccess();
		    },
		    mybuttons = [ { text:"OK", handler: continueBtn }, 
		                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
		    var dlg = userDefinedConfirmDialog('<s:text name="warn.port.access.change.portType"></s:text>', 
		            mybuttons, "Warning");
		    dlg.show();
    	} else {
    		continueSavePortAccess();
    	}
    } else {
    	continueSavePortAccess();
    }
}
var savePortAccess = function(o) {
    try {
        eval("var details = " + o.responseText);
    }catch(e){
        if (subDrawerOperation=='createPortAccess') {
            newPortAccess(o);
        } else {
            editPortAccess(o);
        }
        return;
    }
    if (details.succ) {
        if (details.newState){
            back2AccessDialog(details);
        } else {
            if(details.normalView) {
                fetchConfigTemplate2Page(true);
            } else {
                back2AccessDialog(details);
            }
        }
    }
}
function removePortAccess(portId) {
	  var tmpIndex = -1;
	    if(Get('listPortAccess_tmpIndex')) {
	    	tmpIndex = Get('listPortAccess_tmpIndex').value;
	    }
    var url = "<s:url action='portAccess' includeParams='none' />?operation=remove&jsonMode=true" 
            + "&contentShowType=dlg" 
            + "&allSelectedIds="+ portId 
            + "&tmpIndex=" + tmpIndex
            + "&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
            {success : delPortAccess, failure : resultDoNothing, timeout: 60000, argument: [portId]}, null);
}
var delPortAccess = function(o) {
    eval("var details="+o.responseText);
    var hideErrNotes = function () {
        hm.util.wipeOut('errNote', 800);
    }
    if(details.succ) {
        var arg = o.argument[0];
        if(arg) {
              var $spanEl = $('#selectedAccessIdsDiv').find('#dataTable'+arg+'Span');
              if($spanEl.length) {
                  $spanEl.parent().parent().remove();
              }
        }
    } else {
        hm.util.show("errNote");
        Get("errNote").className="noteError";
        Get("errNote").innerHTML=details.errMsg;
        setTimeout("hideErrNotes()", 10000);
    }
}
function back2AccessDialog(details) {
    var portTemplateId = 0, portNum = 0, deviceType = 0,tmpIndex = -1;
    if(Get('portAccess_portTemplateId')) {
    	portTemplateId = Get('portAccess_portTemplateId').value;
    }
    if(Get('portAccess_portNum')) {
    	portNum = Get('portAccess_portNum').value;
    }
    if(Get('portAccess_deviceType')) {
    	deviceType = Get('portAccess_deviceType').value;
    }
    if(Get('portAccess_tmpIndex')) {
    	tmpIndex = Get('portAccess_tmpIndex').value;
    }
    var accessIds =[];
    if(details && details.id) {
    	accessIds.push(details.id);
    }
    var url = "<s:url action='portAccess' includeParams='none' />?operation=listAccess4Port"
        + "&portTemplateId="+portTemplateId
        + "&deviceType="+deviceType
        + "&portNum="+portNum
        + "&selectedAccessIds="+accessIds
        + "&tmpIndex=" + tmpIndex
        + "&ignore="+new Date().getTime();
    addRemoveAccess(url);
    
    networkPolicyCallbackFn = null;
    backNetWorkPolicy();
}
/*=== VLAN ===*/
function editTrunkModeVlan4Access(accessId) {
    var url = "<s:url action='portAccess' includeParams='none' />?operation=showVlans4Access"
    		+"&selectedAccessId="+accessId
    		+"&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('Get', url,
    		{success : succFetchTrunkVlan, failure : resultDoNothing, timeout: 60000}, null);
}
var succFetchTrunkVlan = function(o) {
	var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-vlan-big.png" width="40px" height="40px" '+ 
		'title="<s:text name="config.port.access.vlan.popup.title" />" class="dialogTitleImg" />'+ 
		'<span class="npcHead1" style="padding-left:10px;"><s:text name="config.port.access.vlan.popup.title" /></span>';
	Get("hdDivSpan").innerHTML = tittle;
	set_innerHTML("bdDiv",o.responseText);
	openSubDialogOverlay("430px");
}
function newVlan4Acc(accessId) {
    var url = "<s:url action='vlan' includeParams='none' />?operation=new&jsonMode=true"
    		+ "&contentShowType=dlg&parentDomID=selecteNativeVlanOpt"
    		+ "&ignore="+new Date().getTime();
    openIFrameDialog(800, 400, url);
}
var succNewVlan4Acc = function(o) {
}
function editVlan4Acc(accessId) {
    var selectedValue = hm.util.validateListSelection('selecteNativeVlanOpt');
    if(selectedValue < 0){
        return;
    }
    var url = "<s:url action='vlan' includeParams='none' />?operation=edit&jsonMode=true"
    		+ "&id="+selectedValue+"&contentShowType=dlg"
    		+ "&parentDomID=selecteNativeVlanOpt"
    		+ "&ignore="+new Date().getTime();
    openIFrameDialog(800, 400, url);
}
function validateNativeVlan4Acc() {
    var vlannames = Get("selecteNativeVlanOpt");
    var vlanValue = Get("selectedNativeVlanName");
     if ("" == vlanValue.value) {
         hm.util.reportFieldError(Get("errorDisplayVlan4Acc"), 
        		 '<s:text name="error.config.network.object.input.direct"><s:param>Native VLAN</s:param></s:text>');
         vlanValue.focus();
         return false;
     }
    if (!hm.util.hasSelectedOptionSameValue(vlannames,vlanValue)) {
        var message = hm.util.validateIntegerRange(vlanValue.value, 'Native VLAN', 1, 4094);
        if (message != null) {
            hm.util.reportFieldError(Get("errorDisplayVlan4Acc"), message);
            vlanValue.focus();
            return false;
        }
        document.forms['nativeVLANSelectPage'].selectedVlanId.value = -1;
    } else {
        document.forms['nativeVLANSelectPage'].selectedVlanId.value = vlannames.options[vlannames.selectedIndex].value;
    }
    return true;
}
function selectVlan4Acc(limitationVLANs) {
	var arg = {},
	   selectEl = Get('selecteNativeVlanOpt'),
	   option = selectEl.options[selectEl.selectedIndex];
	
	if(validateNativeVlan4Acc()) {
		if(option.value == -1) {
			var inputValue = Get("selectedNativeVlanName").value;
			arg.vlanName = inputValue;
		} else {
			arg.vlanId = option.value, arg.vlanName = option.text;
		}
		arg.accessId = Get('nativeVLANSelectPage_selectedAccessId').value;
		arg.allowedVlans = Get('nativeVLANSelectPage_allowVlans').value;
		
		if(!validateAllowVlanFormat(Get('nativeVLANSelectPage_allowVlans'), limitationVLANs)) {
			return;
		}
		
	    var url = "<s:url action='portAccess' includeParams='none' />?operation=selectedVlans4Access" 
	    		+"&jsonMode=true&ignore="+new Date().getTime();
	    YAHOO.util.Connect.setForm(document.getElementById("nativeVLANSelectPage"));
	    var transaction = YAHOO.util.Connect.asyncRequest('POST', url,
	    		{success : succSelectedVlan4Acc, failure : resultDoNothing, timeout: 60000, argument: [arg]}, null);
	}
}
var succSelectedVlan4Acc = function(o) {
	eval("var details = " + o.responseText);
	if(details.succ) {
		var arg = o.argument[0];
		if(!arg.vlanId) {
			arg.vlanId = details.vlanId; 
		}
		if(!arg.allowedVlansStr) {
			arg.allowedVlansStr = details.allowedVlansStr; 
		}
		if(Get('accessNativeVlanSection_'+arg.accessId)) {
			new Template('accessNativeVlanTmpl', arg).render('accessNativeVlanSection_'+arg.accessId);
			refreshNetworkObjPage();
		} else {
			fetchConfigTemplate2Page(true);
		}
		hideSubDialogOverlay();
	}
}
function validateAllowVlanFormat(element, limitationVLANs) {
    if(element){
	    if (element.value.length == 0) {
	        hm.util.reportFieldError(element, '<s:text name="error.requiredField"><s:param><s:text name="hiveAp.if.allowedVlan" /></s:param></s:text>');
	        return false;
	    }
	    var MAX_SUPPORT_ALLOWED_VLANS = 255, allowedVLANs = [];
	    var vlans = element.value.split(",");
	    var messageNote = limitationVLANs ? '<s:text name="hiveAp.if.allowedVlan.note1"></s:text>' : "<s:text name='hiveAp.if.allowedVlan.note'></s:text>";
	    var hasAllOption = false;
	    for(var i=0; i<vlans.length; i++){
	        var vlan = vlans[i];
	        if('' == vlan){
	        	element.focus();
	        	showWarnDialog(messageNote);
	            return false;
	        }
	        if(isNaN(vlan)){//not a number
	        	var matchFlag = (limitationVLANs ? !vlan.match(/^all$/i) : !vlan.match(/^all$/i) && !vlan.match(/^auto$/i));
	            if(matchFlag){
	                // is a number range?
	                var range = vlan.split("-");
	                if(range.length != 2){
	                	element.focus();
	                	showWarnDialog(messageNote);
	                    return false;
	                }
	                var start = range[0];
	                var end = range[1];
	                var number = parseInt(start);
	                if(isNaN(start) || isNaN(number) || !isInteger(start.trim()) || number <1 || number > 4094){
	                	element.focus();
	                	showWarnDialog(messageNote);
	                    return false;
	                }
	                number = parseInt(end);
	                if(isNaN(end) || isNaN(number) || !isInteger(end.trim()) || number <1 || number > 4094){
	                	element.focus();
	                	showWarnDialog(messageNote);
	                    return false;
	                }
	                if(parseInt(start) > parseInt(end)){
	                	element.focus();
	                	showWarnDialog(messageNote);
	                    return false;
	                }
	                for(var index=parseInt(start); index<=parseInt(end); index++) {
	                    //count the max support allowed VLANs
	                    if(!allowedVLANs.contains(index)) {
	                        allowedVLANs.push(index);
	                    }
	                }
	            }else{
	                if(vlan.match(/^all$/i)){
	                    hasAllOption = true;
	                }
	            }
	        }else{
	            var number = parseInt(vlan);
	            if(!isInteger(vlan.trim())|| number <1 || number > 4094){
	            	element.focus();
	            	showWarnDialog(messageNote);
	                return false;
	            } else {
	            	//count the max support allowed VLANs
	            	if(!allowedVLANs.contains(number)) {
	            		allowedVLANs.push(number);
	            	}
	            }
	        }
	    }
	    if(hasAllOption && vlans.length > 1){
	    	element.focus();
	    	showWarnDialog('<s:text name="hiveAp.if.allowedVlan.note2"></s:text>');
	        return false;
	    } else {
	    	if(limitationVLANs && allowedVLANs.length > MAX_SUPPORT_ALLOWED_VLANS) {
	    		showWarnDialog("The allowed VLANs should not exceed 255.");
	    		return false;
	    	}
	    }
	    return true;
    }
    return false;
}
/*=== UserProfile ===*/
function addOrRemoveUserProfile4Acc(accessId) {
    var url = "<s:url action='portAccess' includeParams='none' />?operation=showUserProfile4Access"
            +"&selectedAccessId="+accessId
            +"&jsonMode=true&ignore="+new Date().getTime();
    var transaction = YAHOO.util.Connect.asyncRequest('Get', url,
            {success : succFetchUserProfile4Acc, failure : resultDoNothing, timeout: 60000}, null);
}
var succFetchUserProfile4Acc = function(o) {
    var tittle = '<img src="/hm/images/hm_v2/profile/hm-icon-users-big.png" width="40px" height="40px" '+ 
        'title="<s:text name="config.port.access.userprofile.popup.title" />" class="dialogTitleImg" />'+ 
        '<span class="npcHead1" style="padding-left:10px;"><s:text name="config.port.access.userprofile.popup.title" /></span>';
    Get("hdDivSpan").innerHTML = tittle;
    set_innerHTML("bdDiv",o.responseText);
    openSubDialogOverlay("480px");
}
function selectUserProfile4Acc() {
    var url = "<s:url action='portAccess' includeParams='none' />?operation=selectedUserProfile4Access" 
        +"&jsonMode=true&ignore="+new Date().getTime();
    YAHOO.util.Connect.setForm(document.getElementById("accUserProfileSelectPage"));
    var transaction = YAHOO.util.Connect.asyncRequest('POST', url,
        {success : succSelectedUserProfile4Acc, failure : resultDoNothing, timeout: 60000}, null);
}
var succSelectedUserProfile4Acc = function(o) {
	eval("var details = " + o.responseText);
	if(details.succ) {
		hideSubDialogOverlay();
		finishSelectUserProfileForCertainObj();
	}
}
var debug = function(msg) {
    if(window.console && console.debug) {
        if(typeof msg == 'string') {
            //console.debug(msg);
        } else {
            //console.dir(msg);
        }
    }
};
</script>
<script id="wirePortGroupTmpl" type="text/x-dot-template">
    <div class="portContainer" ref="{{=it.num}}">
        {{? it.type==='USB' && it.logo}}
        <div class="companyLogo"></div>
        {{?}}
        <ul class="port-list" id="{{=it.id}}">
        {{? !it.vertical }}
        {{ for(var i=0; i<it.num; i++) { }}
            <li class="port {{=it.className}} unselected {{? it.disabled0 && i+it.startIndex == 0}}disabled {{=it.disabled0Class||''}}{{?}}" 
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">
                {{? it.label && it.type==='USB' && it.logo}}<span class="labelDown">{{=it.type}}</span>{{?}}
                {{? it.label && it.type==='USB' && !it.logo}}<span class="labelUp">{{=it.type}}</span>{{?}}
                {{? it.label && it.type==='LTE'}}<span class="labelUp">{{=it.type}}</span>{{?}}
                {{? it.label && (it.type!=='USB' && it.type!=='LTE')}}<span class="labelUp">{{=i+it.labelStartIndex}}</span>{{?}}
                {{? it.color}}
                <span class="portColor"></span>
                {{?}}
            </li>
        {{ } }}
        {{?? true }}
        {{ for(var i=0; i<it.num; i+=2) { }}
            <li class="port {{=it.className}} unselected" 
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">
                {{? it.label}}<span class="labelUp">{{=i+it.labelStartIndex}}</span>{{?}}
                {{? it.color}}<span class="portColor"></span>{{?}}
            </li>
        {{ } }}
        {{ for(var i=1; i<it.num; i+=2) { }}
            <li class="port {{=it.className}} unselected" 
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">
                {{? it.label}}<span class="labelDown">{{=i+it.labelStartIndex}}</span>{{?}}
                {{? it.color}}<span class="portColor"></span>{{?}}
            </li>
        {{ } }}
        {{?}}
        </ul>
    </div>
</script>
<script id="wirePortGroupDescTmpl" type="text/x-dot-template">
    <div>
        <p class="portDesc">Port Assignment</p>
        <div style="padding-left: 25px;display: none;">
        {{~it.array :assignment:index}}
            <p>{{=assignment.key}}&nbsp;&nbsp;(<a class="npcLinkA" href="javascript:void(0);" onclick="viewPortAccess({{=assignment.id}}, true);return false;">{{=assignment.value}}</a>)</p>
        {{~}}
        </div>
    </div>
</script>
<script id="accessNativeVlanTmpl" type="text/x-dot-template">
&nbsp;<a class="npcLinkA" href="javascript:void(0);" onclick="editVlan({{=it.vlanId}})">
<span title='<s:property value="{{=it.vlanName}}" />'>{{=it.vlanName}}</span></a>
<br/>&nbsp;&nbsp;<span class="smallTd" title="{{=it.allowedVlans}}">({{=it.allowedVlansStr}})</span>
</script>
<style type="text/css">
.ui-classifier-items ul.itemContainer .item span.pointer{
	white-space:normal;
}
#editClassifierTagContainer td.listHead {
    background-color: #FFFFFF; 
    height: 24px;   
}
</style>
