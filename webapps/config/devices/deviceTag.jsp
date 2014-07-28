<%@taglib prefix="s" uri="/struts-tags"%>
<script src="<s:url value="/js/widget/classifiertag/ct-debug.js" includeParams="none"/>?v=<s:property value='verParam' />"></script>
<style type="text/css">
#ap2ClassifierTagContainer td.listHead {
    background-color: #FFFFFF;    
}

#ap2ClassifierTagContainer ul {    
 padding-left: 100px;
 width: auto !important;
 }
 
 #ap2ClassifierTagContainer td.defaultContainer input {    
    width: 100px;
}

#ap2ClassifierTagContainer div {
    float: left;
    width: 33.3%;
}

 
#ap2ClassifierTagContainer td.listHead{
border-bottom: 0 ;
}

#ap2ClassifierTagContainer li {    
    text-decoration: none;
	line-height: none;
	background-image: none;	
	line-height: 1.9em;
 }
 
#ap2ClassifierTagContainer li.tag-choice a.cancel-icon{
	top: 2px;
}	
</style>
<script>
function initWidgetGui() {
	if(!window.jQuery) {
		head.js("<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	if(!window.jQuery.ui) {
		head.js("<s:url value='/js/jquery-ui.min.js' includeParams='none'/>?v=<s:property value='verParam' />");
	}
	head.js("<s:url value='/js/widget/classifiertag/ct-debug.js' includeParams='none'/>?v=<s:property value='verParam' />",
			"<s:url value='/js/widget/dialog/panel.js' includeParams='none'/>?v=<s:property value='verParam' />",
	function(){
		var deviceTagInitValue1="<s:text name="dataSource.classificationTag1"/>";
		var deviceTagInitValue2="<s:text name="dataSource.classificationTag2"/>";
		var deviceTagInitValue3="<s:text name="dataSource.classificationTag3"/>";
		if(deviceTagInitValue1=="dataSource.classificationTag1")deviceTagInitValue1="None"
		if(deviceTagInitValue2=="dataSource.classificationTag2")deviceTagInitValue2="None"
		if(deviceTagInitValue3=="dataSource.classificationTag3")deviceTagInitValue3="None"
		var templateEle = document.getElementById("hiveAp_configTemplate");
		var ct = $("#ap2ClassifierTagContainer").classifierTag(
				{
					key: 8,
					types:  [{key: 4, text: 'Device Tags'}, null, null],
					widgetWidth: {desc: 0},
					valueProps: null,
					itemEditable: false,
					describable: false,	
					needShowTagFields: true,
					deviceTagInitValue: {
						Tag: [deviceTagInitValue1, deviceTagInitValue2, deviceTagInitValue3]
					},
					inputTagDone:{
						funcName: requestTemplateInfo,
						argument: templateEle}
				});    
    	$("#ap2ClassifierTagContainer").show();
	});	
}

<s:if test="%{jsonMode}">
window.setTimeout("initWidgetGui()", 200);
</s:if>
<s:else>
$(document).ready(function()
{	
	initWidgetGui();
}
);
</s:else>
</script>	
<div>
	<fieldset>
		<legend><s:text name="hiveAp.classification.tag" /></legend>
		<table cellspacing="0" cellpadding="0" border="0" class="embedded" width="100%">
			<tr>
				<td height="10"></td>
			</tr>																								
			<tr id="ap2ClassifierTagContainer"></tr>
		</table>
	</fieldset>
</div>