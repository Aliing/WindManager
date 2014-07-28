;(function($, _) {
	var win = window;
	var hm = win.hm || hm || {};
	hm.util = hm.util || {};
	win.hm = hm;
	
	function insertAfter(newEl, targetEl) {
        var parentEl = targetEl.parentNode;
        if(parentEl.lastChild == targetEl) {
            parentEl.appendChild(newEl);
        } else {
            parentEl.insertBefore(newEl,targetEl.nextSibling);
        }            
    }
	
	var ahDragAndDropHelper = function(options, callback) {
		if (!options.dragContainers) {
			return;
		}
		var dragContainers = options.dragContainers;
		var dragElPattern = options.dragElPattern;
		var self = this;
		var marker,
			container, 
			oriContainer,
			elDisplayStyle;
		var lastRectNode = [];
		
		//please override it
		self.getElDisplayStyle = function() {
		};
		//please override it
		self.createMarker = function() {
		};
		//please override it if needed
		self.addAdditionalToRecDrag = function(recDrag) {
		};
		//please override it
		self.startDragFunc = function(x, y) {
		};
		self.render = function() {
			_.each(dragContainers, function(container) {
				new YAHOO.util.DDTarget(container).onDragEnter = containerDragEnter;
			});
			enableDragForElements();
			marker = self.createMarker();
			elDisplayStyle = self.getElDisplayStyle();
		};
		
		self.enableDragForElement = function(el) {
			if (typeof el === "string") {
				el = $(el);
			}
			var recDrag = new  YAHOO.util.DDProxy(el.id);
			self.addAdditionalToRecDrag.apply(el, [recDrag]);
			recDrag.startDrag = function(x, y) {
				var el = this.getEl();
				oriContainer = container = el.parentNode;
				self.startDragFunc.apply(this, [x, y, marker]);
				container.insertBefore(marker, el);
			};
			recDrag.onDragEnter= function(e, id) {
				var el = document.getElementById(id);
				oriContainer=el;
				if (isElContainer(id)) {
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
				if (isElContainer(oriContainer.id)) {
					oriContainer.appendChild(marker);
				} else {
					if ((e.pageY || (e.clientY+document.documentElement.scrollTop)) > (parseInt(YAHOO.util.Dom.getY(oriContainer)) + parseInt(oriContainer.offsetHeight/2))){
						insertAfter(marker,oriContainer);
					} else {
						container.insertBefore(marker,oriContainer);
					}
				}
			};
			recDrag.endDrag = function(e, id) {
				var el = this.getEl(); 
				try {
					marker = container.replaceChild(el, marker);
				} catch(err) {
					marker = marker.parentNode.replaceChild(el, marker);
				}	
				el.style.display = elDisplayStyle;
				this.getDragEl().innerHTML="";
				if(options.events.whenEndDrag){
					options.events.whenEndDrag();
				}
			};
		};
		var isElContainer = function(elId) {
			return _.contains(dragContainers, elId);
		};
		var containerDragEnter = function(e, id) {
			var el = document.getElementById(id);
			if (isElContainer(id)) {
				el.appendChild(marker);
			} else {
				container = el.parentNode;
				container.insertBefore(marker, el);
			}
		};
		var getLastNode = function(lastChild) {
			if(lastChild) {
				var id = lastChild.id;
				if (id && id === options.lastChildId) {
					return lastChild;
				} 
				return getLastNode(lastChild.previousSibling);
			}
		};
		var enableDragForElements = function() {
			$(dragElPattern).each(function(){
				self.enableDragForElement(this);
			});
		};
	};
	
	hm.util.AhDragAndDropTableHelper = function(options, callback) {
		var self = this;
		ahDragAndDropHelper.call(self, options, callback);
		self.getElDisplayStyle = function() {
			return "table-row";
		};
		self.createMarker = function() {
			return document.createElement("tr");
		};
		self.addAdditionalToRecDrag = function(recDrag) {
			recDrag.setHandleElId($(this).find(options.dragHandler));
		};
		self.startDragFunc = function(x, y, marker) {
			var dragEl = this.getDragEl(); 
			var el = this.getEl();
			el.style.display = "none";
			dragEl.innerHTML = "<table><tr>" + el.innerHTML + "</tr></table>";
			dragEl.style.borderColor = "#335779";
			dragEl.style.color = "#000";
			dragEl.style.backgroundColor = "#fff";
			dragEl.style.textAlign = "center";
			
			marker.style.display = "none";
			marker.style.height = YAHOO.util.Dom.getStyle(dragEl, "height");	
			marker.style.width = YAHOO.util.Dom.getStyle(dragEl, "width");
			marker.className = dragEl.className;
			marker.style.margin = "5px"; 
			marker.style.marginBottom = "20px"; 
			marker.style.border = "2px dashed #335779";
			marker.style.display= self.getElDisplayStyle();
		};
	};
	
	hm.util.AhDragAndDropCommonHelper = function(options, callback) {
		var self = this;
		ahDragAndDropHelper.call(self, options, callback);
		self.getElDisplayStyle = function() {
			return "block";
		};
		self.createMarker = function() {
			return document.createElement("div");
		};
		self.addAdditionalToRecDrag = function(recDrag) {
			recDrag.setHandleElId($(this).find(options.dragHandler));
		};
		self.startDragFunc = function(x, y, marker) {
			var dragEl = this.getDragEl(); 
			var el = this.getEl();
			el.style.display = "none";
			//dragEl.style.zIndex = ++zIndex;
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
			marker.style.display= self.getElDisplayStyle();
		};
	};
})(jQuery, _);