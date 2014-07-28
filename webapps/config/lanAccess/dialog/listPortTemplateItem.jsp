<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.bo.network.SingleTableItem"%>
<%@page import="com.ah.ui.actions.hiveap.NetworkPolicyAction"%>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/ct.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/widget/panel.css" includeParams="none"/>?v=<s:property value="verParam" />" />
<link rel="stylesheet" type="text/css" 
    href="<s:url value="/css/accordionview.css" includeParams="none"/>?v=<s:property value="verParam" />" />
    
<script>
function submitPortItemAction(){
   	var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
    document.forms["portitemConfigure"].operation.value = "savePortItem";
    YAHOO.util.Connect.setForm(document.getElementById("portitemConfigure"));
    var transaction = YAHOO.util.Connect.asyncRequest('post', url,
            {success : succSubmit, failure : resultDoNothing, timeout: 60000}, null);
}

var succSubmit = function(o){
	 try {
	        eval("var details = " + o.responseText);
	    }catch(e){
	    	//=========================================
	    	$("#classfierEditPanelAPanel_mask").remove();
	    	$("#classfierEditPanelAPanel_c").remove();
	    	//=========================================
	    	$("#x").attr("href","javascript:hideSubDialogOverlay();");
	    	hideSubDialogOverlay();
	        set_innerHTML(accordionView.getDrawerContentId('netWorkPolicy'),
	                o.responseText);
	        return;
	    }	
}
</script>
<div id="content" style="padding: 0">
	<s:form action="networkPolicy" id="portitemConfigure" name="portitemConfigure">
		<s:hidden name="operation" />
		<s:hidden name="portTemplateId" id="portTemplateId"/>
		<s:hidden name="configTemplateId" id="configTemplateId"/>
		<table cellspacing="0" cellpadding="0" border="0" width="100%" >
			<tr>
				<td height="20px"></td>
			</tr>
			<tr>
				<td>
					<label><s:text name="config.port.item.defaulttemplate" />&nbsp;<s:property value="%{defaultTemplateName}"/>&nbsp;<s:property value="%{deviceTemplateInfo}"/></span>
					</label>
				</td>
			</tr>
			<tr>
				<td height="16">
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding:4px 0px 4px 4px;" valign="top">
					<table cellspacing="0" cellpadding="0" border="0" class="embedded" style="width: 100%">
						<tr id="newButton">
							<td colspan="7" style="padding-bottom: 2px;">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><input type="button" name="ignore" value="New" class="button" id="newItemBtn"></td>																								
									<td><input type="button" name="ignore" value="Reset Order" id="resetButton" class="button" ></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr id="createButton" style="display:none;">
							<td colspan="7" style="padding-bottom: 2px;">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><input type="button" name="ignore" value="<s:text name="button.apply"/>" class="button" id="addItemBtn"></td>
									<td><input type="button" name="ignore" value="Cancel" id="cancelItemBtn" class="button"></td>
									<td><input type="button" name="ignore" value="Reset Order" id="resetButton" class="button" ></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td height="16">
							</td>
						</tr>
						<tr id="headerSection">											
							<th align="left" width="240"><s:text name="config.port.item.template" /></th>
							<th align="left" width="110"><s:text name="config.port.item.type" /></th>
							<th align="left" width="220"><s:text name="config.port.item.value" /></th>
							<th align="left" width="100"><s:text name="config.port.item.description" /></th>
						</tr>
						<tr id="portItemClassifierTagContainer"></tr>	
						<tr id="portItemsRow"/>
					</table>
				</td>
			</tr>
			<tr>
				<td  class="noteInfo" colspan="6" width="100"><s:text name="config.port.item.note1" /><a href="javascript:showOrderDialog1()" class="marginBtn">?</a>&nbsp;<s:text name="config.port.item.note2" /></td>
			</tr>
			<tr>
				<td height="16">
				</td>
			</tr>
			<tr>
				<td align="center">
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td style="padding-right: 10px;">
								<input type="button" name="ignore" value="OK"
									class="button" onClick="submitPortItemAction('');"
									<s:property value="writeDisabled" />>
							</td>
		 
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<style type="text/css">
img.dialogTitleImg {
    border: medium none;
    margin-right: 10px;
    padding: 0;
    vertical-align: middle;
}
#editClassifierTagContainer td.listHead {
    background-color: #FFFFFF; 
    height: 24px;   
}
 #editClassifierTagContainer td.defaultContainer input {    
 	margin-left: 75px;
    width: 75px;
}

 #editClassifierTagContainer .ui-menu .ui-menu-item a {
 	width: 135px;
 }
  #editClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 75px;	
}
 #editClassifierTagContainer td.defaultContainer a.none {	
	margin-left: 75px;	
}
 
 #portItemClassifierTagContainer .ui-menu .ui-menu-item a {
 	width: 85px;
 }

 #portItemClassifierTagContainer td.defaultContainer input {    
 	margin-left: 75px;
    width: 85px;
}

 #portItemClassifierTagContainer td.defaultContainer a.none {	
	margin-left: 75px;	
}
 #portItemClassifierTagContainer td.defaultContainer li.tag-choice {
	padding-left: 75px;	
}
 #resetButton {
 	width: 80px;
 } 
.ui-classifier-items ul.itemContainer li{
 	background-image:none;
}
.ui-classifier td.defaultContainer li{
    background-image:none;
}
#portItemClassifierTagContainer td.defaultContainer ul{
 	width:auto;
 	list-style:none;
}
.ui-classifier-items ul.optmenu-container{
    width: auto;
}
</style>
<script>
function widgetClick(sType,sTagValue) {
	var ids= $("#editClassifierTagContainer").find("td.listHead:nth-child(1)").attr("id");
	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
	if(ids==undefined)
	$("#editClassifierTagContainer").find("td.listHead:nth-child(1)").remove();
	if(sType==4){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").show();
	}
	if(sType==3){
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").hide();
		$("#editClassifierTagContainer").find("#deviceCtner").show();
	}
	if(sType==2){
		$("#editClassifierTagContainer").find("#deviceCtner").hide();
		$("#editClassifierTagContainer").find("#tagCtner").hide();
		$("#editClassifierTagContainer").find("#topologyCtner").show();
	}
}

function init() {
	$("#nonGlobalId").html("");
	<s:if test="%{templist != null}">		
		<s:iterator value="%{templist}" status="status">
			var key = '<s:property value="%{templist[#status.index][0]}"/>';
			var value = '<s:property value="%{templist[#status.index][1]}"/>';
			$("#nonGlobalId").append("<option value='"+ key +"'>"+ value +"</option>");
		</s:iterator>	
	</s:if>
 
	$("#portItemsRow ul").empty();
 	var configTemplateId = '<s:property value="%{configTemplateId}"/>';
	var portTemplateId = '<s:property value="%{portTemplateId}"/>';
	$("#configTemplateId").val(configTemplateId);
	$("#portTemplateId").val(portTemplateId);

	<s:if test="%{itemlist != null}">
		<s:iterator value="%{itemlist}" status="status">
			var item = {}; 
			item.id = '<s:property value="%{#status.index}"/>';
			item.value = '<s:property value="@com.ah.ui.actions.hiveap.NetworkPolicyAction@getPortTemplateName(itemlist[#status.index].nonGlobalId,true)"/>';
			item.type = '<s:property value="%{itemlist[#status.index].type}"/>';
			item.tagValue = '<s:property value="getTagValue(domainId)"/>';	
			//item.tagValue = '<s:property value="%{itemlist[#status.index].tagValue}"/>';	
			item.desc = '<s:property value="%{itemlist[#status.index].description}"/>';
			if('<s:property value="%{itemlist[#status.index].configTemplateId}"/>' == configTemplateId 
					|| '<s:property value="%{itemlist[#status.index].configTemplateId}"/>' == 0){
				$("#portItemClassifierTagContainer").classifierTag('addItem', item);
			}else{
				$("#portItemClassifierTagContainer").classifierTag('addItem', item, -1);
			}
		</s:iterator>	
	</s:if>	
 
	$(".item-state-disable").attr('style','display:none;');
	hm.util.hide('portItemClassifierTagContainer');	
	
}

YAHOO.util.Event.onDOMReady(function(){

	setTimeout(function(){
		if(!window.jQuery) {
			head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
		}
		if(!window.jQuery.ui) {
			head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
		}
		head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
				"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
		function(){
		    var DOM = YAHOO.util.Dom;
		    var editPanel;
		    var ctt;
		    
		   var ct = $("#portItemClassifierTagContainer").classifierTag(
						{
							key: 41,
							checkable:null,
							needGlobalItem: false,
							zindexDisplay:true,
							renderDiv: 'subDialogOverlay_c',
							itemWidths:{value: 220, type: 100, tagValue: 180 ,desc: 80},
							valueProps: {items: {id: 'nonGlobalId', elType: 'select',  field: 'nonGlobalId'},validateFn: function(){return checkTemplateId(Get('nonGlobalId'), 'Template');}},		
							itemTableId: 'portItemsRow',
							itemEditable: {value: false, valueName: 'nonGlobalIds', desc: true, descName: 'descriptions'},
						});
			init();
		$('input#newItemBtn').click(function() {
				hm.util.hide('newButton');
				hm.util.show('createButton');
				hm.util.show('portItemClassifierTagContainer');	
			});
			$('input#cancelItemBtn').click(function() {
			    hm.util.hide('createButton');
			    hm.util.show('newButton');
			    hm.util.hide('portItemClassifierTagContainer');		
			    $(".ui-autocomplete").hide();
			});
			$('input#addItemBtn').click(function() {
				$("#portItemClassifierTagContainer").classifierTag('saveItem');
			});
			$('input#resetButton').click(function() {
				$("#portItemClassifierTagContainer").classifierTag('resetOrder');
			});	  
		
		    var aerohivePanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierPopupWholePanel', {width: 200, closeIcon: 'images/cancel.png'});
		    DOM.get('ahTempClassifierMatchBtn').onclick = function(){    	
		    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
		    	aerohivePanel.openDialog();
		    };	    
		    
		    DOM.get('classfierPopupPanelOkButton').onclick = function(){ 
		    	aerohivePanel.closeDialog();
		    };	    
		    
		    var helpPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierHelpWholePanel', {width: 420, closeIcon: 'images/cancel.png'});
		    DOM.get('ahTempClassifierHelpBtn').onclick = function(){    	
		    	$("a.ahPanel-close").attr('style','position: absolute; right: 25px; top: 25px;');
		    	helpPanel.openDialog();
		    };
		    DOM.get('classfierHelpOkButton').onclick = function(){ 
		    	helpPanel.closeDialog();
		    };
		   
		    DOM.get('ahTempClassifierEditBtn').onclick = function(){
		    	if(null == editPanel) {
		    		editPanel = new YAHOO.Aerohive.YUI2.widget.Panel('classfierEditPanel', {width: 270, closeIcon: 'images/cancel.png'});
		    	}
		    	if(null == ctt){
		    	 ctt=$("#editClassifierTagContainer").classifierTag(
		 				{
	    					key: 41,
	    					types:  [ {key: 2, text: 'Topology Node'},null,null],
	    					widgetWidth: {desc: 0},
	    					valueProps: null,
	    					itemEditable: false,
	    					describable: false,	
	    					zindexDisplay: true,
	    					renderDiv: 'subDialogOverlay_c',
	    					needShowTagFields: true,
	    					needShowDeviceNames: true,
	    					needShowTopology: true
		 				}
		 			);
		    	}
		    	$("#editClassifierTagContainer").show();
		    	var tempEditType=$("input#ahTempClassifierItemType").val();
		    	var tempEditValue=$("input#ahTempClassifierKeyValue").val();
		    	setTimeout(function(){
		    		widgetClick(tempEditType,tempEditValue);
		    		editPanel.openDialog();
		    	}, 300);
		    };
	 
		    DOM.get('cancelInEditClassifierTagContainer').onclick = function(){
		    	editPanel.closeDialog();
		    };
		
		    DOM.get('saveInEditClassifierTagContainer').onclick = function(){
		    	var stag1=$("#editClassifierTagContainer").find("li#li_tag_input_0").find("input").val();
		    	var stag2=$("#editClassifierTagContainer").find("li#li_tag_input_1").find("input").val();
		    	var stag3=$("#editClassifierTagContainer").find("li#li_tag_input_2").find("input").val();
		    	var dvcName=$("#editClassifierTagContainer").find("li#li_device_input").find("input").val();
		    	var topoName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId_hidden").val();
		    	var tpName=$("#editClassifierTagContainer").find("li#li_topo_input").find("#locationId").val();
		
		    	var idx=$("input#ahTempClassifierIndexValue").val();
		    	var itemType=$("input#ahTempClassifierItemType").val();
		
		    	if(itemType==4&&stag1==""&&stag2==""&&stag3==""){
		    		alert('<s:text name="home.hmSettings.devicetag.deviceTagNull" />');return;
		    	}
				if(itemType==3&&dvcName==""){
					alert('<s:text name="home.hmSettings.devicetag.deviceNameNull" />');return;
		    	}
				if(itemType==2&&topoName==""&&tpName==""){
					alert('<s:text name="home.hmSettings.devicetag.topologyNull" />');return;
				}
				if(itemType==2&&topoName==""){editPanel.closeDialog(); return;}
		
			    $.getJSON("classifierTag.action",
							{tagKey: 41, operation: 'edit', selectedItems: idx,tag1:stag1 ,tag2:stag2 ,tag3:stag3 ,typeName:dvcName,locationId:topoName},
						function(data) {
							if(data.succ) {
								//just set the edited value back to jsp page
								var htmlIdx=parseInt(idx)+1;
								var targetString=data.value;
								$("#portItemsRow").find("li.item:nth-child("+htmlIdx+")").find("span:nth-child(3)").html(targetString);
								editPanel.closeDialog();
							}else{
								var ttab = document.getElementById("editClassifierTagContainer");
								hm.util.reportFieldError(ttab,data.errmsg);
							}
							})
						.error(function() {
							debug("editItems, Error occurs...");
						});
		   	 };				
	   	 });
	    }, 10);
	});

function showOrderDialog1(){
	$("div#classfierHelpPanel").empty();
	$('<p>')
	.append('By default, HiveManager applies the object definitions in the following order of priority:<br>')
	.append('1. A definition with three device tags matching the same tags on a device<br>')
	.append('2. A definition with two matching device tags<br> ')
	.append('3. A definition with one matching device tag<br>')
	.append('4. A definition with a host name that matches that of a device <br>')
	.append('5. A definition with a matching map name <br> ')
	.append('6. A default definition<br>')
	.appendTo($("div#classfierHelpPanel"));
	$("input#ahTempClassifierHelpBtn").click();	return;
}

function checkTemplateId(templateId, title){
	var table = document.getElementById("portItemsRow");
	if (templateId.value.length == 0) {
        hm.util.reportFieldError(table, '<s:text name="error.requiredField"><s:param>'+title+'</s:param></s:text>');
        vlanId.focus();
        return false;
    } 
	return true;
}
</script>
<div id="classfierHelpWholePanel" style="display: none;height: 160px">
	<div id="classfierHelpPanel" style="overflow: auto;word-wrap: break-word; word-break: normal;"></div>	
	<input id="classfierHelpOkButton" type="button" value="OK" style="margin-left: 180px;">
</div>
<div id="classfierPopupWholePanel" style="display: none;height: 100px">
	<div id="classfierPopupPanelTitle"></div>
	<div id="classfierPopupPanel" style="overflow: auto;"></div>
	<input id="classfierPopupPanelOkButton" type="button" value="OK" style="margin-left: 80px; margin-top: 10px;">
</div>
<div id="classfierEditPanel" style="display: none;">
	<table width="270" border="0" >
		<tr id="editClassifierTagContainer">
		</tr>
        <tr>
           <td align="center" >
              <input type="button" value="Save" id="saveInEditClassifierTagContainer">
              <input type="button" value="Cancel" id="cancelInEditClassifierTagContainer">
           </td>
        </tr>
     </table>
</div>
<div style="display: none;">
<input type="button" value="AH Panel" id="ahTempClassifierMatchBtn">
<input type="button" value="AH Panel" id="ahTempClassifierHelpBtn">
<input type="button" value="Submit" id="ahTempClassifierEditBtn">
<input type="text" value="Submit" id="ahTempClassifierIndexValue" value="">
<input type="text" value="Submit" id="ahTempClassifierItemType" value="">
</div>