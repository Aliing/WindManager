function initWidgetPage() {
	var zIndex = 0;

	// BEGIN :: Non-draggable targets
	var col1Target = new YAHOO.util.DDTarget("Column1");
	var col2Target = new YAHOO.util.DDTarget("Column2");
	// END :: Non-draggable targets

	var _nodeClick = function(e) {
		e = e || window.event;
		
		var objTar = e.target || e.srcElement;
        //Is the target an href?
        if (YAHOO.util.Selector.test(objTar, 'a')) {
            var a = e.target || e.srcElement, anim = null, div = a.parentNode.parentNode;
            //Did they click on the min button
            if (YAHOO.util.Dom.hasClass(a,'min')) {
                //Get some node references
                if (!YAHOO.util.Dom.hasClass(div,'collapsed')) {
                	YAHOO.util.Dom.addClass(div,'collapsed');
                } else {
                    //Set the vars for expanding it
                    YAHOO.util.Dom.removeClass(div,'collapsed');
                }
                setCookies("");
            }
            //Was close clicked?
            if (YAHOO.util.Dom.hasClass(a,'close')) {
            	if(confirm('This widget will be removed, ok?')) {
	           	 	anim = new YAHOO.util.Anim(div);
	           	 	anim.attributes.opacity = { to: 0 };
                    anim.duration=0.125;
                    anim.method = YAHOO.util.Easing.easeOut;
                    anim.onComplete.subscribe(function() {
                    	var anim = new YAHOO.util.Anim(div);
		           	 	anim.attributes.height = { to: 0 };
	                    anim.duration=0.125;
	                    anim.method = YAHOO.util.Easing.easeOut;
                    	anim.onComplete.subscribe(function() {
                    		document.getElementById('ck'+div.id).checked=false;
	                    	var strId='ck'+div.id;
	                    	div.parentNode.removeChild(div);
	                    	setCookies(strId);
						}); 
						anim.animate();
					}); 
	            	anim.animate();
            	}
            }
        }
        return false;
    }; 

	//Helper method to create the markup for the module..
    var createMod = function(el1) {
    	var a = document.createElement("a");
    	a.title="minimize module";
    	a.className="min";
    	a.href="#";
    	el1.insertBefore(a, el1.firstChild);
    	a.onclick=function(e) {return _nodeClick(e); };
    	
    	var c = document.createElement("a");
    	c.title="close module";
    	c.className="close";
    	c.href="#";	
    	el1.appendChild(c);
    	c.onclick=function(e) {return _nodeClick(e); };

    };

	var elements = YAHOO.util.Dom.getElementsByClassName('widgetHandle', 'div');
	for(var i=0;i<elements.length;i++){
		createMod(elements[i]);
	};


	// BEGIN :: Objects to drag
	var marker, container, oriContainer;
	var lastRectNode = [];
	marker = document.createElement("div");	
	col1Target.onDragEnter = col2Target.onDragEnter = function(e, id) {
			var el = document.getElementById(id);
			if (id.substr(0, 6)	=== "Column") {
				el.appendChild(marker);
			} else {
				container = el.parentNode;
				container.insertBefore(marker, el);
			}
	};

	var dragWidgets = YAHOO.util.Dom.getElementsByClassName('widgetRec', 'div');
	for(var i=0;i<dragWidgets.length;i++){
		var recDrag = new  YAHOO.util.DDProxy(dragWidgets[i].id);
		recDrag.setHandleElId(dragWidgets[i].id + 'Handle');
		recDrag.startDrag = function(x, y) {
			var dragEl = this.getDragEl(); 
			var el = this.getEl();
			oriContainer = container = el.parentNode;
			el.style.display = "none";
			dragEl.style.zIndex = ++zIndex;
			dragEl.innerHTML = el.innerHTML;
			dragEl.className=el.className;
			dragEl.style.borderColor = "#335779";
			dragEl.style.color = "#000";
			dragEl.style.backgroundColor = "#fff";
			dragEl.style.textAlign = "center";
			
			marker.style.display = "none";
			marker.style.height = YAHOO.util.Dom.getStyle(dragEl, "height");	
			marker.style.width = YAHOO.util.Dom.getStyle(dragEl, "width");
			marker.className=dragEl.className;
			marker.style.margin = "5px"; 
			marker.style.marginBottom = "20px"; 
			marker.style.border = "2px dashed #335779";
			marker.style.display= "block";
			container.insertBefore(marker, el);
		};
		recDrag.onDragEnter= function(e, id) {
			var el = document.getElementById(id);
			oriContainer=el;
			if (id.substr(0, 6)	=== "Column") {
				el.appendChild(marker);
			} else {
				container = el.parentNode;
				container.insertBefore(marker, el);
			}
		};
		recDrag.onDragOut=function(e, id) {
			var el = document.getElementById(id);
			lastRectNode[container.id] = getLastNode(container.lastChild);
			if (lastRectNode[container.id] && el.id === lastRectNode[container.id].id) {
				container.appendChild(marker);
			}	
		};
		recDrag.onDrag=function(e) {
			//alert(oriContainer.id);
			if (oriContainer.id.substr(0, 6)=== "Column") {
				oriContainer.appendChild(marker);
			} else {
				//alert((e.clientY+document.documentElement.scrollTop)  + "---" +oriContainer.id + "===" + (parseInt(YAHOO.util.Dom.getY(oriContainer)) + parseInt(oriContainer.offsetHeight/2)));
				if ((e.pageY || (e.clientY+document.documentElement.scrollTop)) > (parseInt(YAHOO.util.Dom.getY(oriContainer)) + parseInt(oriContainer.offsetHeight/2))){
					insertAfter(marker,oriContainer);
				} else {
					container.insertBefore(marker,oriContainer);
				}
			}
			
			//alert(this.getEl().id + "====" + oriContainer.offsetHeight + "===" + YAHOO.util.Dom.getY(oriContainer) + e.x);

		};
		recDrag.endDrag = function(e, id) {
			var el = this.getEl(); 
			
			try {
				marker = container.replaceChild(el, marker);
			} catch(err) {
				marker = marker.parentNode.replaceChild(el, marker);
			}	
			el.style.display = "block";
			this.getDragEl().innerHTML="";
			setCookies("");
		};
	};

	function insertAfter(newEl, targetEl)
    {
        var parentEl = targetEl.parentNode;
        
        if(parentEl.lastChild == targetEl)
        {
            parentEl.appendChild(newEl);
        }else
        {
            parentEl.insertBefore(newEl,targetEl.nextSibling);
        }            
    }
	
	// END :: Event handlers
	
	// BEGIN :: Helper methods
	var getLastNode = function(lastChild) {
			if(lastChild) {
				var id = lastChild.id;
				if (id && id.substring(0, 6) === "widget") {
					return lastChild;
				} 
				return getLastNode(lastChild.previousSibling);
			}
	}
	/**var isEmpty = function(el) {
			var test = function(el) { 
				return ((el && el.id) ? el.id.substr(0, 6) == "widget" : false);
			} 
			var kids = YAHOO.util.Dom.getChildrenBy(el, test);
			return (kids.length == 0 ? true : false);
	}**/
	// END :: Helper methods
};

var insertWidgitFlash = function (swf,width,height,application,bgcolor){
// Version check based upon the values entered above in "Globals"
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
	// Check to see if the version meets the requirements for playback
	if (hasRequestedVersion) {
	    	// if we've detected an acceptable version
			// embed the Flash Content SWF when all tests are passed
			AC_FL_RunContent(
						"src", swf,
						"width", width,
						"height", height,
						"align", "middle",
						"id", application,
						"quality", "high",
						"bgcolor", bgcolor,
						"name", application,
						"allowScriptAccess","sameDomain",
						"type", "application/x-shockwave-flash",
						"wmode", "opaque",
						"pluginspage", "http://www.adobe.com/go/getflashplayer"
		);
	} else {  // flash is too old or we can't detect the plugin
	    var alternateContent = 'This content requires the Adobe Flash Player. '
	   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
	    document.write(alternateContent);  // insert non-flash content
	}
};

/**
 * Handle a similar ComboBox UI
 * 
 * @method autoCompelteComboBox
 * 
 * @param inputElId {String} ID of the input HTMLElement
 * @param containerElId {String} ID of the existing DIV container HTMLElement
 * @param arrowElId {String} ID of the arrow HTMLElement
 * @param dataSource {YAHOO.widget.DataSource} DataSource instance.
 * @param maxDisplaySize {Int} (recommend) Max size of display in the DIV container, default size is 20
 * @param myHiddenFieldId {String} (option) ID of the hidden field for custom event attached on YAHOO.widget.AutoComplete.itemSelectEvent
 * @param acProperties {Object} (option) The custom properties for the auto-complete configuration
 * @param containerElHeight {Int} Height of the existing DIV container HTMLElement
 * 
 * @return The AutoComplete element (with properties: oDS, oAC)
 * 
 * @usage: HTML code
 * <code>
 * <div style="z-index: 9000">
 *		<input type="text" id="inputElId"/>
 *		<a id="arrowElId" tabindex="-1" href="javascript:void(0);" class="acDropdown"></a>
 * 		<div id="containerElId"></div>
 *		<input type="hidden" id="myHiddenFieldId"/>
 * </div>
 * </code>
 */
var autoCompelteComboBox = function (inputElId, containerElId, arrowElId, dataSource, maxDisplaySize, myHiddenFieldId, acProperties,containerElHeight) {
	// default max display size
	var maxSize = 20;
	if(maxDisplaySize) {
		maxSize = maxDisplaySize;
	}
	
	// default properties configuration
	var oConfigs = {
			//prehighlightClassName: "yui-ac-prehighlight",
			userShadow: true,
			queryDelay: 0,
			animVert: false,
			forceSelection: true,
			//typeAhead: true,
			minQueryLength: 0,
			maxResultsDisplayed: maxSize
	}
	if(acProperties) {
		oConfigs = acProperties;
	}
	
	// set the view row
	var maxViewResult = 10;
	var containerHeight = maxViewResult * 20;
	if(containerElHeight){
		containerHeight=containerElHeight;
	}
	
	// new a auto-complete instance
	var acEl = new YAHOO.widget.AutoComplete(inputElId, containerElId, dataSource, oConfigs);
	
	// toggler
	var togglerArrow = function(e) {
		if(acEl.isContainerOpen()) {
			acEl.collapseContainer();

		} else {
			// set the view height
			YAHOO.util.Dom.setStyle(Get(containerElId), "height", containerHeight+"px");
			
			acEl.getInputEl().focus();// keep the widget active
			setTimeout(function() {// For IE
				acEl.sendQuery("");
			}, 0.5);
			
			// set scroll-y bar
			YAHOO.util.Dom.setStyle(Get(containerElId), "overflow-y", "auto");
			YAHOO.util.Dom.setStyle(Get(containerElId), "overflow-x", "hidden");
		}
	}
	YAHOO.util.Event.on(arrowElId, "click", togglerArrow);
	// For collapse
	acEl.containerCollapseEvent.subscribe(function(){
		// set the view height
		YAHOO.util.Dom.setStyle(Get(containerElId), "height", "0px");
	});
	// For lost focus
	acEl.textboxKeyEvent.subscribe(function(){
		// set the view height
		if(YAHOO.util.Dom.getStyle(Get(containerElId), "height") == "0px")
			YAHOO.util.Dom.setStyle(Get(containerElId), "height", containerHeight+"px");
	});
	
	// attach the custom item event
	if(myHiddenFieldId && Get(myHiddenFieldId)) {
	    // Define an event handler to populate a hidden form field
	    // when an item gets selected
	    var myHiddenField = Get(myHiddenFieldId);
	    var itemHanlder = function(sType, aArgs) {
	        var myAC = aArgs[0]; // reference back to the AC instance
	        var elLI = aArgs[1]; // reference to the selected LI element
	        var oData = aArgs[2]; // object literal of selected item's result data
	        
	        // update hidden form field with the selected item's ID
	        myHiddenField.value = oData.id;
	    };
	    acEl.itemSelectEvent.subscribe(itemHanlder);
	}
	
	return {
		oDS: dataSource,
		oAC: acEl
	};
}