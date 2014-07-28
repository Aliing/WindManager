var hm = hm || {};
hm.paintbrush = hm.paintbrush || {};

hm.paintbrush.MESSAGE_NONE_SELECTED = "Please select one or more destination items.";
hm.paintbrush.MESSAGE_SELF_SELECTED = "<span style='color:#f00;'>The source item cannot also be a destination item.</span>";
hm.paintbrush.MESSAGE_SELECTED = function(n){
	if(n > 1){
		return n + " destination items have been selected.";
	}else{
		return n + " destination item has been selected.";
	}
};


YAHOO.util.Event.onDOMReady(function () {
	hm.paintbrush.init();
});

hm.paintbrush.init = function(){
	// register
	var inputElements = document.getElementsByName("selectedIds");
	YAHOO.util.Event.addListener(inputElements, "click", hm.paintbrush.destinationSelected);
	var checkAllElement = document.getElementById("checkAll");
	YAHOO.util.Event.addListener(checkAllElement, "click", hm.paintbrush.destinationSelected, "checkAll");
	var allItemsSelectedVarSelect = document.getElementById("allItemsSelectedVarSelect");
	if(allItemsSelectedVarSelect){
		YAHOO.util.Event.addListener(allItemsSelectedVarSelect, "click", hm.paintbrush.destinationAllSelected);
	}
	var allItemsSelectedVarClear = document.getElementById("allItemsSelectedVarClear");
	if(allItemsSelectedVarClear){
		YAHOO.util.Event.addListener(allItemsSelectedVarClear, "click", hm.paintbrush.destinationSelected);
	}
	
	// initialize some fields
	hm.paintbrush.triggerButton = document.getElementById("brushTriggerBtn");
	hm.paintbrush.sourceIdElement = document.getElementById("paintbrushSource");
	hm.paintbrush.sourceNameElement = document.getElementById("paintbrushSourceName");
	
	if(hm.paintbrush.sourceIdElement && hm.paintbrush.sourceIdElement.value > 0){
		hm.paintbrush.showPaintbrushUI(hm.paintbrush.sourceIdElement.value, hm.paintbrush.sourceNameElement.value);
	}
};

hm.paintbrush.destinationSelected = function(event, obj){
	if(!hm.paintbrush.started){
		return;
	}
	event = event || window.event;
	if (event.target) {
		var checkBox = event.target;
	} else {
		var checkBox = event.srcElement;
	}
	var x = YAHOO.util.Dom.getX('checkAll') + 40;
	var y = YAHOO.util.Dom.getY(checkBox) + (obj=='checkAll' ? 40 : 18);
	// motion the pannel
	if(hm.paintbrush.anim.isAnimated()){
		hm.paintbrush.anim.stop(false);
	}
	hm.paintbrush.anim.attributes.points.to = [x, y];
	hm.paintbrush.anim.animate();
	// delay for some time, in order to avoid count value incorrect when use shift key to select
	window.setTimeout(hm.paintbrush.updatePaintbrushPanelInfo, 50);
}

hm.paintbrush.destinationAllSelected = function(event){
	if(!hm.paintbrush.started){
		return;
	}
	var el = document.getElementById("availableRowCountSpan");
	if(el){
		hm.paintbrush.updatePanelInfo(hm.paintbrush.MESSAGE_SELECTED(el.innerHTML), false);
	}
}

hm.paintbrush.triggerPaintbrush = function(formName, actionName){
	if(hm.paintbrush.started){
		//hm.paintbrush.stopPaintbrush(); hide panel to trigger stop function
		if(hm.paintbrush.contentPanel){
			hm.paintbrush.contentPanel.hide();
		}
	}else{
		hm.paintbrush.startPaintbrush(formName, actionName);
	}
}

hm.paintbrush.startPaintbrush = function(formName, actionName){
	var inputElements = document.getElementsByName('selectedIds');
	if (!hm.util.hasAvailableCheckBoxs(inputElements)) {
		warnDialog.cfg.setProperty('text', "There is no item to paintbrush.");
		warnDialog.show();
		return;
	}
	
	var selected = hm.util.getSelectedIds();
	if (selected.length != 1) {
		warnDialog.cfg.setProperty('text', "Please select a source item.");
		warnDialog.show();
		return;
	}
	
	var sourceId = selected[0];
	// assign the value to the form hidden element
	if(hm.paintbrush.sourceIdElement){
		hm.paintbrush.sourceIdElement.value = sourceId;
	}
	var url = actionName||formName + ".action?operation=paintbrushSourceInfo&id=" + sourceId + "&ignore=" + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest("GET", url, {success : hm.paintbrush.setPaintbrushSourceInfo}, null);
	
	// show paintbrush UI
	hm.paintbrush.showPaintbrushUI(sourceId);
};

hm.paintbrush.stopPaintbrush = function(){
	//reset row color
	var checkBox = hm.paintbrush.sourceCheckBox;
	if(checkBox){
		checkBox.parentNode.parentNode.style.backgroundColor = "";
	}
	//unselect rows
	var inputElements = document.getElementsByName('selectedIds');
	// If no elements found, length will be 0
    for (var i = 0; i < inputElements.length; i++) {
    	hm.paintbrush.unselectCheckBox(inputElements[i]);
	}
	// reset the value of form hidden element value
	if(hm.paintbrush.sourceIdElement){
		hm.paintbrush.sourceIdElement.value = "";
	}
	if(hm.paintbrush.sourceNameElement){
		hm.paintbrush.sourceNameElement.value = "";
	}
	
	if(hm.paintbrush.otherPageItemSelected()){
		submitAction("cancelPaintbrush");
	}else{
		//reset button
		hm.paintbrush.decorateTriggerButton(false);
		hm.paintbrush.started = false;
	}
};

hm.paintbrush.setPaintbrushSourceInfo = function(o){
	eval("var result = " + o.responseText);
	if(result.n && hm.paintbrush.sourceNameElement){
		hm.paintbrush.sourceNameElement.value = result.n;
		if(null != hm.paintbrush.contentPanel){
			hm.paintbrush.contentPanel.header.innerHTML = "Paintbrush Panel - " + result.n;
		}
	}
}

hm.paintbrush.showPaintbrushUI = function(sourceId, sourceName){
	if(null == hm.paintbrush.contentPanel){
		// create a pannel inside the form
		hm.paintbrush.createPanel(formName);
	}
	if(null == hm.paintbrush.anim){
		hm.paintbrush.createAnimation();
	}
	
	if(sourceName){
		hm.paintbrush.contentPanel.header.innerHTML = "Paintbrush Panel - " + sourceName;
	}
	
	var checkBox = hm.paintbrush.getCheckBox(sourceId);
	hm.paintbrush.sourceCheckBox = checkBox;
	
	if(checkBox){
		var row = checkBox.parentNode.parentNode;
		hm.paintbrush.unselectCheckBox(checkBox);
		hm.paintbrush.highlightSelectedRow(row);
	}
	
	var x = YAHOO.util.Dom.getX('checkAll') + 40;
	var y = null== checkBox ? (YAHOO.util.Dom.getY('checkAll') + 40) : (YAHOO.util.Dom.getY(checkBox) + 18);
	hm.paintbrush.locatePanel(x, y);
	
	hm.paintbrush.updatePaintbrushPanelInfo();
	hm.paintbrush.contentPanel.show();
	
	hm.paintbrush.decorateTriggerButton(true);
	hm.paintbrush.started = true;
}

hm.paintbrush.updateBrush = function(){
	submitAction("paintbrush");
};

hm.paintbrush.createPanel = function(formName){
	// Instantiate a Panel from script
	hm.paintbrush.contentPanel = new YAHOO.widget.Panel("panel", { width:"320px", visible:false, draggable:false, underlay:"none" } );
	hm.paintbrush.contentPanel.setHeader("Paintbrush Panel");
	hm.paintbrush.contentPanel.setBody('<span id="brushContent"></span>');
	hm.paintbrush.contentPanel.setFooter('<input type="button" id="brushUpdateBtn" value="Update" ' +
			'class="button" disabled="disabled" onclick="hm.paintbrush.updateBrush()" />');
	hm.paintbrush.contentPanel.beforeHideEvent.subscribe(hm.paintbrush.stopPaintbrush);
	hm.paintbrush.contentPanel.render(document.forms[formName]);
};

/*
 * Create the hidden element from HTML Mark
hm.paintbrush.createSourceHiddenElement = function(formName){
	var sourceElement = document.createElement("input");
	sourceElement.type = "hidden";
	sourceElement.name = "paintbrushSource";
	document.forms[formName].appendChild(sourceElement);
	hm.paintbrush.sourceHiddenElement = sourceElement;
};
*/

hm.paintbrush.createAnimation = function(){
	hm.paintbrush.anim = new YAHOO.util.Motion(hm.paintbrush.contentPanel.element, {points: { to: [0, 0] }}, 0.5, YAHOO.util.Easing.easeOut);
};

hm.paintbrush.unselectCheckBox = function(checkBox){
	checkBox.checked = false;
	hm.util.toggleRow(checkBox);
};

hm.paintbrush.highlightSelectedRow = function(row){
	row.style.backgroundColor = "#FFCC33";
};

hm.paintbrush.decorateTriggerButton = function(starting){
	if(hm.paintbrush.triggerButton){
		hm.paintbrush.triggerButton.style.borderStyle = starting?"inset":"";
		hm.paintbrush.triggerButton.style.backgroundColor = starting?"#FFCC33":"";
	}
};

hm.paintbrush.getCheckBox = function(cbValue){
	var cbs = document.getElementsByName('selectedIds');
	var cb;
	for (var i = 0; i < cbs.length; i++) {
		if (cbs[i].value == cbValue) {
			cb = cbs[i];
		}
	}
	return cb;
};

hm.paintbrush.locatePanel = function(x, y){
	hm.paintbrush.contentPanel.cfg.setProperty("x",x);
	hm.paintbrush.contentPanel.cfg.setProperty("y",y);
};

hm.paintbrush.updatePaintbrushPanelInfo = function(){
	var allIds = hm.paintbrush.getAllSelectedIds();
	var n = allIds.length;
	if(n == 0){
		hm.paintbrush.updatePanelInfo(hm.paintbrush.MESSAGE_NONE_SELECTED, true);
	}else{
		var selfSelected = false;
		var checkBox = hm.paintbrush.sourceCheckBox;
		if(checkBox){
			var selfId = checkBox.value;
			for(var i=0; i<n; i++){
				if(allIds[i] == selfId){
					selfSelected = true;
					break;
				}
			}
		}
		if(selfSelected){
			hm.paintbrush.updatePanelInfo(hm.paintbrush.MESSAGE_SELF_SELECTED, true);
		}else{
			hm.paintbrush.updatePanelInfo(hm.paintbrush.MESSAGE_SELECTED(n), false);
		}
	}
}

hm.paintbrush.updatePanelInfo = function(message, disabled){
	YAHOO.util.Dom.get("brushContent").innerHTML = message;
	YAHOO.util.Dom.get("brushUpdateBtn").disabled = disabled;
};

hm.paintbrush.getAllSelectedIds = function(){
	var selectedIds = hm.util.getSelectedIds();
	var previousIds = new Array();
	var s = document.getElementsByName("allSelectedIds");
	var p = document.getElementsByName("pageIds");
	for(var i=0; i<s.length; i++){
		var value = s[i].value;
		var existed = false;
		for(var j=0; j<p.length; j++){
			if(value == p[j].value){
				existed = true;
				break;
			}
		}
		if(!existed){
			previousIds.push(s[i].value);
		}
	}
	for(var i=0; i< selectedIds.length; i++){
		var value = selectedIds[i];
		var existed = false;
		for(var j=0; j<previousIds.length; j++){
			if(value == previousIds[j]){
				existed = true;
				break;
			}
		}
		if(!existed){
			previousIds.push(value);
		}
	}
	return previousIds;
};
hm.paintbrush.otherPageItemSelected = function(){
	var s = document.getElementsByName("allSelectedIds");
	var p = document.getElementsByName("pageIds");
	for(var i=0; i<s.length; i++){
		var value = s[i].value;
		var existed = false;
		for(var j=0; j<p.length; j++){
			if(value == p[j].value ){
				existed = true;
			}
		}
		if(!existed){
			return true;
		}
	}
	return false;
}