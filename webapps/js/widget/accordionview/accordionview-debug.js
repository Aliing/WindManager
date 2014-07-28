// $Id: accordionview-debug.js,v 1.1 2011/10/10 07:46:53 ylin Exp $
YAHOO.namespace("YAHOO.aerohive.widget");
YAHOO.aerohive.widget.DebugLog = function(msg) {
	YAHOO.log(msg, "debug", "accordionview.js")
	//console.debug("accrodionview>>>"+msg);
};
(function() {
	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, YUA = YAHOO.util.Anim, Lang = YAHOO.lang;
	var debug = YAHOO.aerohive.widget.DebugLog;
	
	var Drawer = function(el, oAttr) {
        el = YUD.get(el);
        oAttr = oAttr || {};
        YAHOO.aerohive.widget.Drawer.superclass.constructor.call(this, el, oAttr); 
        
        this.initEvents(oAttr);
        this.refresh(['id'], true);  
	};
	
	YAHOO.aerohive.widget.Drawer = Drawer;
	YAHOO.extend(Drawer, YAHOO.util.Element, {
		/**
		 * the parent AccordionView object
		 * @private
		 */
		_parent: null,
		/**
		 * the animation object
		 * @private
		 */
		_animator: null,
		/**
		 * the children of current drawer
		 * @private
		 */
		_subDrawers: null,
        EVENT : {
        	MOUSE_OVER: 'mouseover',
        	MOUSE_OUT: 'mouseout',
        	BEFORE_OPEN: 'beforeopen',
    		AFTER_OPENED: 'afteropened',
			AFTER_CLOSED: 'afterclosed'
        },
		initAttributes: function (oAttr) {
			Drawer.superclass.initAttributes.call(this, oAttr);
			this.setAttributeConfig('id', {
                writeOnce: true,
                validator: function (value) {
                    return (/^[a-zA-Z][\w0-9\-_.:]*$/.test(value));
                },
                value: YUD.generateId(),
                method: function (value) {
                    this.get('element').id = value;
                }
			});
            this.setAttributeConfig('width', {
                value: '100%',
                method: function (value) {
                    this.setStyle('width', value);
                }
            });
            this.setAttributeConfig('cache', {
                value: false,
                validator: YAHOO.lang.isBoolean
            });
            this.setAttributeConfig('height', {
            	value: oAttr.height || '100',
            	validator: YAHOO.lang.isNumber
            });
            this.setAttributeConfig('autoheight', {
            	value: oAttr.autoheight || false,
            	validator: YAHOO.lang.isBoolean
            });
            // use for the SubDrawer
            this.setAttributeConfig('title', {
            	value: oAttr.title || false
            });
            this.setAttributeConfig('disableOnclick', {
            	value: oAttr.disableOnclick || false,
            	validator: YAHOO.lang.isBoolean
            });
		},
		initEvents : function (oAttr) {
			if(oAttr.mouseover && Lang.isFunction(oAttr.mouseover)) {
				this.on(this.EVENT.MOUSE_OVER, oAttr.mouseover, this, true); 
			}
			if(oAttr.beforeopen && Lang.isFunction(oAttr.beforeopen)) {
				this.on(this.EVENT.BEFORE_OPEN, oAttr.beforeopen, this, true); 
			} else {
				this.on(this.EVENT.BEFORE_OPEN, function(){return true;}, this, true); 
			}
			if(oAttr.afteropened && Lang.isFunction(oAttr.afteropened)) {
				this.on(this.EVENT.AFTER_OPENED, oAttr.afteropened, this, true); 
			}
			if(oAttr.afterclosed && Lang.isFunction(oAttr.afterclosed)) {
				this.on(this.EVENT.AFTER_CLOSED, oAttr.afterclosed, this, true); 
			}
			if(!this.get("disableOnclick")) {
				this.on('click', this._onClick, this, true);
			}
		},
		setParent : function (parent) {
			this._parent = parent;
		},
		getParent : function () {
			return this._parent;
		},
		addChildren : function (children) {
			if(null === this._subDrawers) {
				this._subDrawers = [];
			}
			if(Lang.isArray(children)) {
				this._subDrawers = children;
			} else {
				debug("add chilren into current drawer:"+this.get("id"));
				this._subDrawers.push(children);
			}
		},
		getChildren : function () {
			return this._subDrawers;
		},
		_onClick : function (arg) {
            var ev;
            if(arg.nodeType === undefined) {
                ev = YUE.getTarget(arg);
                YUE.preventDefault(arg);
                YUE.stopPropagation(arg);
            } else {
                ev = arg;
            }
            
            // get the target
            debug("_onclick: arg:"+Lang.dump(arg)+" ev:"+Lang.dump(ev)+" tagName:"+ev.tagName+" type:"+ev.nodeType);
            var elClickedNode;
            if(ev.id == this.get("id")) {
            	elClickedNode = ev;
            } else {
            	if(ev.parentNode && ev.parentNode.id === this.get("id")) {
            		// if the current element is a heading element, it would be nice if we could use
            		// (ev instance of HTMLHeadingElement), but it doesn't guarantee work well because
            		// it is not required by the specification. Therefore we can check the element tagName like this:
            		// if (ev.nodeType == 1 && 
            		//		(ev.tagName.length == 2 && ev.tagName.toLowerCase().charAt(0) == 'h' )) {
            		// }
            		elClickedNode = ev.parentNode;
            	} else {
            		// TODO
            		return;
            	}
            }
			debug("_onclick: "+elClickedNode.nodeType+" id: "+elClickedNode.id +" class: "+elClickedNode.className);
			
			// get the container
			var contentEl = YUD.get(elClickedNode.id+this._parent.SUFFIX.CONTENT_ID);
			if (null == contentEl) {
				return;
			}
			// is it opened or not
			var isHidden = YUD.hasClass(contentEl, this._parent.CLASSES.HIDDEN);
			debug("contentEl id:"+contentEl.id+" className:"+contentEl.className+" isHidden:"+isHidden+" is opened a drawer:"+this._parent.isOpenedDrawer());
			// check the opened drawer
			if (this._parent.isOpenedDrawer()) {
				if(this._parent.getOpenedDrawerId()== elClickedNode.id) {
					debug("current drawer is opened");
					if(!this._parent.get("collapsableAll")) {
						debug("current drawer is opened, and the property collapsableAll is false, stay in the drawer");
						return;
					}
				} else {
					// if the children in opened state, stop event
					if(this._parent.isOpenedSubDrawer(this._parent.getOpenedDrawerId())) {
						return;
					}
					if (this.fireEvent(this.EVENT.BEFORE_OPEN, 
							{toOpen: elClickedNode.id, Opened: this._parent.getOpenedDrawerId()}) === false) {
						debug("fire the before open event, and false");
						return; 
					} else {
						this._parent.collapseSpecificDrawer(this._parent.getOpenedDrawerId());
					}
				}
			}
			if (isHidden) {
				this.openDrawer();
			} else {
				this.closeDrawer();
			}
		},
		openDrawer : function () {
			var elId = this.get("id");
			debug("open drawer..."+elId);
			// open drawer
			var contentEl = YUD.get(elId + this._parent.SUFFIX.CONTENT_ID);
			YUD.replaceClass(contentEl, this._parent.CLASSES.HIDDEN, this._parent.CLASSES.BLOCK);
			var textArray = YUD.get(elId).getElementsByTagName("h3");
			if(null != textArray && textArray.length > 0) {
				var h3Text = textArray[0];
				YUD.addClass(h3Text, this._parent.CLASSES.ACTIVE);
			}
			// change the drawer bg-image
			YUD.addClass(Get(this._parent.CONFIG.ITEM_TAG_NAME+"_"+elId), this._parent.CLASSES.ACTIVE);
			// store the the opened drawer id
			this._parent.setOpenedDrawerId(elId);
			// animation
			var animate = this._parent.get('animate');
			if(animate) {
				var heightOption = {from: 0, to: this.get('height')};
				if (this._animator === null) {
					// create a animator for the drawer
					var aSpeed = animate.speed ? animate.speed : 0.5;
					var aEffect = animate.effect ? animate.effect : YAHOO.util.Easing.easeBoth;
					var aOptions = {height: heightOption};
					this._animator = new YUA(contentEl.id, aOptions, aSpeed, aEffect);
				}
				// reference current drawer for fire event
				var that = this;
				// unsubscribe this event 
				this._animator.onComplete.unsubscribeAll();
				this._animator.onComplete.subscribe(function(){
					// reset the height
					if(that.get('autoheight')) {
						YUD.setStyle(contentEl, 'height', 'auto');
					}
					that.fireEvent(that.EVENT.AFTER_OPENED, YUD.get(elId));
				}); 
				this._animator.attributes.height = heightOption;
				this._animator.animate();
				debug("do open event animate");
			} else {
				// fire the after opened event
				this.fireEvent(this.EVENT.AFTER_OPENED, YUD.get(elId));
			}
		},
		closeDrawer : function () {
			var elId = this.get("id");
			debug("close drawer..."+elId);
			// close drawer
			var contentEl = YUD.get(elId + this._parent.SUFFIX.CONTENT_ID);
			YUD.replaceClass(contentEl, this._parent.CLASSES.BLOCK, this._parent.CLASSES.HIDDEN);
			var textArray = YUD.get(elId).getElementsByTagName("h3");
			if(null != textArray && textArray.length > 0) {
				var h3Text = textArray[0];
				YUD.removeClass(h3Text, this._parent.CLASSES.ACTIVE);
			}
			// change the drawer bg-image
			YUD.removeClass(YUD.get(this._parent.CONFIG.ITEM_TAG_NAME+"_"+elId), this._parent.CLASSES.ACTIVE);
			if(false === this.get('cache')) {
				contentEl.innerHTML = "";
			}
			// reset the the opened drawer id
			this._parent.setOpenedDrawerId(null);
			// animation
			var animate = this._parent.get('animate');
			if(animate) {
				var heightOption = {from: contentEl.offsetHeight, to: 0};
				if (null === this._animator) {
					// create a animator for the drawer
					var aSpeed = animate.speed ? animate.speed : 0.5;
					var aEffect = animate.effect ? animate.effect : YAHOO.util.Easing.easeBoth;
					var aOptions = {height: heightOption};
					this._animator = new YUA(contentEl.id, aOptions, aSpeed, aEffect);
				}
				// reference current drawer for fire event
				var that = this;
				// unsubscribe this event
				this._animator.onComplete.unsubscribeAll();
				this._animator.onComplete.subscribe(function(){
					that.fireEvent(that.EVENT.AFTER_CLOSED, YUD.get(elId));
				}); 
				this._animator.attributes.height = heightOption;
				this._animator.animate();
				debug("do close event animate");
			} else {
				// fire the after closed event
				this.fireEvent(this.EVENT.AFTER_CLOSED, YUD.get(elId));
			}
		}
	});
})();
(function() {
	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, YUA = YAHOO.util.Anim;
	var debug = YAHOO.aerohive.widget.DebugLog;
	
	var AccordionView = function(el, oAttr) {
        el = YUD.get(el);
        oAttr = oAttr || {};
        if(!el) {
        	//TODO create a element
        }
        if (el.id) { oAttr.id = el.id; }
        debug("now init the accordion view... id:"+el.id+" oAttr:"+oAttr.width);

        YAHOO.aerohive.widget.AccordionView.superclass.constructor.call(this, el, oAttr); 
        debug("prepare to init drawers...");
        this.initDrawers(el, oAttr);
        debug("refresh...");
        this.refresh(['id', 'width'],true);  
	};
	// prototype
	YAHOO.aerohive.widget.AccordionView = AccordionView;
	YAHOO.extend(AccordionView, YAHOO.util.Element, {
		CONFIG : {
			// tag name for the entire accordion
			TAG_NAME : 'ul',
			// tag name for the wrapper around a toggle + content pair
			ITEM_TAG_NAME : 'li',
			// tag name for the wrapper around the content for a panel
			CONTENT_TAG_NAME : 'div'
		},
        CLASSES : {
            // the entire accordion
            ACCORDION : 'aero-accordionview',
            // the panel
            DRAWER : 'aero-accordion-drawer',
            // the sub panel
            SUBDRAWER : 'aero-accordion-sub-drawer',
            // the element that toggles a panel
            TOGGLE : 'yui-accordion-toggle',
            // the drawer title
            DRAWERTITLE : 'aero-accordion-drawer-title',
            // the element that contains the content of a drawer
            CONTENT : 'aero-accordion-content',
            // to indicate that a toggle is active
            ACTIVE : 'active',
            // to indicate that content is hidden
            HIDDEN : 'hidden',
            // to indicate that content is block
            BLOCK : 'block',
            // the loading icon
            LOADINGICON : 'loadingIcon'
        },
        SUFFIX : {
        	CONTENT_ID : 'ContentId',
        	DRAWER_CONTAINER_ID : 'Container'
        },
        /**
         * Holds references to all accordion drawers (list elements) in an array
         * @property _drawers
         * @private
         * @type Array
         */
        _drawers : null,
        /**
         * Hold the id of opened drawer
         * @property _openedDrawerId
         * @private
         * @ type String
         */
        _openedDrawerId : null,
        /**
         * The id of loading division with the (loading) GIF in background
         */
        _loadingLayerId: "loadingPanel",
        
		initAttributes : function(oAttr) {
			AccordionView.superclass.initAttributes.call(this, oAttr);
			this.setAttributeConfig('id', {
				writeOnce : true,
				validator : function(value) {
					return (/^[a-zA-Z][\w0-9\-_.:]*$/.test(value));
				},
				value : YUD.generateId(),
				method : function(value) {
					this.get('element').id = value;
				}
			});
			this.setAttributeConfig('width', {
				value : '150px',
				method : function(value) {
					this.setStyle('width', value + 'px');
				}
			});
			this.setAttributeConfig('animate', {
				value : oAttr.animate || false,
				validator: YAHOO.lang.isBoolean
			});
			this.setAttributeConfig('collapsableAll', {
				value : false,
				validator: YAHOO.lang.isBoolean
			});
			this.setAttributeConfig('expandItem', {
				value : 0,
				validator: YAHOO.lang.isNumber
			});
			this.setAttributeConfig('enableLoader', {
				value : oAttr.enableLoader || false,
				validator: YAHOO.lang.isBoolean
			});
		},
		/**
		 * Initialize the drawer items for the accordion, and expand the drawer[0] by default
		 * @param {HTMLElement} el The element for the accordion
		 * @param {Object} oAttr attributes key map
		 * @method initDrawers
		 * @public
		 */
		initDrawers : function(el, oAttr) {
			if(!YUD.hasClass(el, this.CLASSES.ACCORDION)) {
				YUD.addClass(el, this.CLASSES.ACCORDION);
			}
			var ulEl = el.getElementsByTagName(this.CONFIG.TAG_NAME)[0];
			ulEl.id = this.CONFIG.TAG_NAME + "_" + el.id;
			var drawerItems = ulEl.getElementsByTagName(this.CONFIG.ITEM_TAG_NAME);
			if (this._drawers === null) {
				this._drawers = [];
			}
			for(var index = 0; index < drawerItems.length; index++) {
				var drawerEl = drawerItems[index];
				// if the <li> element belongs to the <ul> element
				if(drawerEl.parentNode === ulEl) {
                    for (var eHeader = drawerEl.firstChild; eHeader && eHeader.nodeType != 1; eHeader = eHeader.nextSibling) {
                        // This loop looks for the first non-textNode element
                	}
                    if (eHeader) {
                    	if(oAttr.drawers) {
                    		for(var j = 0 ; j < oAttr.drawers.length; j++) {
                    			if(oAttr.drawers[j].id == eHeader.id) {
                    				drawerEl.id = this.CONFIG.ITEM_TAG_NAME + "_" + eHeader.id;
                    				if(!YUD.hasClass(drawerEl, this.CLASSES.DRAWER)) {
                    					YUD.addClass(drawerEl, this.CLASSES.DRAWER);
                    				}
                    				if(!YUD.hasClass(eHeader, this.CLASSES.DRAWERTITLE)) {
                    					YUD.addClass(eHeader, this.CLASSES.DRAWERTITLE);
                    				}
                    				
                    				var drawerWidget = new YAHOO.aerohive.widget.Drawer(eHeader.id, oAttr.drawers[j]);
                    				drawerWidget.setParent(this);
                    				this._drawers.push(drawerWidget);
                    				
                    				// create the container for content, hidden style
                    				var drawerContent = drawerEl.appendChild(document.createElement(this.CONFIG.CONTENT_TAG_NAME));
                    				drawerContent.id = eHeader.id + this.SUFFIX.CONTENT_ID;
                					YUD.addClass(drawerContent, this.CLASSES.CONTENT);
                					YUD.addClass(drawerContent, this.CLASSES.HIDDEN);
                    				break;
                    			}
                    		}
                    	}
                    }
				}
			}
			// initial loading
			if(this.get("enableLoader")) {
				var loadingPanel = document.createElement(this.CONFIG.CONTENT_TAG_NAME);
				loadingPanel.id = this._loadingLayerId;
				var loadingIconPanel = loadingPanel.appendChild(document.createElement(this.CONFIG.CONTENT_TAG_NAME));
				YUD.addClass(loadingIconPanel, this.CLASSES.LOADINGICON);
				
				document.body.appendChild(loadingPanel);
				this.hideLoadingPanel();
			}
			// expand the drawer, default index is zero
			if (null === this._drawers) {
				return;
			}
			this._drawers[this.get("expandItem")].openDrawer();
		},
		isOpenedSubDrawer : function (drawerId) {
			debug("isOpenedSubDrawer: id="+drawerId);
			var drawer = this._getDrawer(drawerId);
			if(drawer) {
				var children = drawer.getChildren();
				debug("isOpenedSubDrawer: children="+children);
				if(children) {
					for(var j=0; j<children.length; j++) {
						if(children[j].isOpened()) {
							return true;
						}
					}
				}
			}
			return false;
		},
		collapseSpecificDrawer : function (drawerId) {
			var drawer = this._getDrawer(drawerId);
			if(drawer) {
				drawer.closeDrawer();
			}
		},
		getDrawerContentId : function (drawerId) {
			var drawer = this._getDrawer(drawerId);
			if(drawer) {
				return drawer.get("id") + this.SUFFIX.CONTENT_ID;
			}
			return null;
		},
		_getDrawer : function (drawerId) {
			if (null === this._drawers) {
				return null;
			}
			for(var index=0; index < this._drawers.length; index++) {
				if(this._drawers[index].get("id") == drawerId) {
					return this._drawers[index];
				}
			}
		},
		appendChildren : function (drawerId, subDrawerProperties) {
			if (null === this._drawers) {
				return;
			}
			for(var index=0; index < this._drawers.length; index++) {
				if(this._drawers[index].get("id") == drawerId) {
					debug("appendChildren: the parent drawer id:"+drawerId);
					for(var j=0; j < subDrawerProperties.length; j++) {
						
						var subDrawerProperty = subDrawerProperties[j];
						debug("subDrawerProperty is:"+Lang.dump(subDrawerProperty));
						// create/get the children container
						var parentDrawer = YUD.get(this.CONFIG.ITEM_TAG_NAME + "_" +drawerId);
						var childrenContainerId = subDrawerProperty.id + "_" + this.SUFFIX.DRAWER_CONTAINER_ID;
						var childrenContainer = YUD.get(childrenContainerId);
						if(!childrenContainer) {
							childrenContainer = parentDrawer.appendChild(document.createElement(this.CONFIG.CONTENT_TAG_NAME));
							childrenContainer.id = childrenContainerId;
							YUD.setStyle(childrenContainer, "padding", "0px");
							
						}
						// create/get the <ul> 
						var subULId = this.CONFIG.TAG_NAME + "_" + subDrawerProperty.id;
						var subULEl = YUD.get(subULId);
						if(!subULEl) {
							subULEl = childrenContainer.appendChild(document.createElement(this.CONFIG.TAG_NAME));
							subULEl.id = subULId;
							YUD.addClass(subULEl, this.CLASSES.HIDDEN);
						}
						// create/get the <li>
						var subLIId = this.CONFIG.ITEM_TAG_NAME + "_" + subDrawerProperty.id;
						var subLI = YUD.get(subLIId);
						if(!subLI) {
							subLI = subULEl.appendChild(document.createElement(this.CONFIG.ITEM_TAG_NAME));
							subLI.id = subLIId;
						}
						// create/get the title&&content
						var subDrawerTitleId = subDrawerProperty.id
						var subDrawerTitle = YUD.get(subDrawerTitleId);
						if(!subDrawerTitle) {
							subDrawerTitle = subLI.appendChild(document.createElement(this.CONFIG.CONTENT_TAG_NAME));
							subDrawerTitle.id = subDrawerTitleId;
							var title = subDrawerProperty.title;
							if(title) {
								subDrawerTitle.innerHTML = title;
							}
							YUD.addClass(subDrawerTitle, this.CLASSES.DRAWERTITLE);
						}
						var subDrawerContentId = subDrawerProperty.id + this.SUFFIX.CONTENT_ID;
						var subDrawerContent = YUD.get(subDrawerContentId);
						if(!subDrawerContent) {
							subDrawerContent = subLI.appendChild(document.createElement(this.CONFIG.CONTENT_TAG_NAME));
							subDrawerContent.id = subDrawerContentId;
							YUD.addClass(subDrawerContent, this.CLASSES.CONTENT);
						}
						// initial the sub drawer
						var drawerWidget = new YAHOO.aerohive.widget.SubDrawer(subDrawerProperty.id, subDrawerProperty);
						drawerWidget.setParent(this._drawers[index]);
						this._drawers[index].addChildren(drawerWidget);
					}
					break;
				}
			}
		},
		expandSubDrawer : function (drawerId, subDrawerId) {
			var subDrawer = this._getSubDrawer(drawerId, subDrawerId);
			if(subDrawer) {
				subDrawer.openDrawer();
			}
		},
		collapseSubDrawer : function (drawerId, subDrawerId) {
			var subDrawer = this._getSubDrawer(drawerId, subDrawerId);
			if(subDrawer) {
				subDrawer.closeDrawer();
			}
		},
		setSubDrawerTitle : function (drawerId, subDrawerId, title) {
			var subDrawer = this._getSubDrawer(drawerId, subDrawerId);
			if(subDrawer) {
				var subDrawerTitle = YUD.get(subDrawer.get("id"));
				if(subDrawerTitle) {
					subDrawerTitle.innerHTML = title;
				}
			}
		},
		getSubDrawerContentId : function (drawerId, subDrawerId) {
			var subDrawer = this._getSubDrawer(drawerId, subDrawerId);
			if(subDrawer) {
				return subDrawer.get("id") + this.SUFFIX.CONTENT_ID;
			}
			return null;
		},
		_getSubDrawer : function (drawerId, subDrawerId) {
			if (null === this._drawers) {
				return null;
			}
			for(var index=0; index < this._drawers.length; index++) {
				if(this._drawers[index].get("id") == drawerId && 
						drawerId == this.getOpenedDrawerId()) {
					var children = this._drawers[index].getChildren();
					if(children) {
						for(var j=0; j<children.length; j++) {
							if(children[j].get("id") == subDrawerId) {
								return children[j];
							}
						}
					}
				}
			}
		},
		setOpenedDrawerId : function (drawerId) {
			this._openedDrawerId = drawerId;
		},
		getOpenedDrawerId : function () {
			return this._openedDrawerId
		},
		isOpenedDrawer : function () {
			if(this._openedDrawerId) {
				return true;
			} else {
				return false;
			}
		},
		_centerPosition : function (container) {
			return {
				x:YUD.getX(container) + container.offsetWidth/2 - 20,
				y:YUD.getY(container) + container.offsetHeight/2 - 5
			};
		},
		hideLoadingPanel : function () {
			YUD.setStyle(this._loadingLayerId, "display", "none");
		},
		showLoadingPanel : function (container) {
			this.hideLoadingPanel();
			var el = YUD.get(this._loadingLayerId);
			if(el) {
				var xy = this._centerPosition(container);
				if(isNaN(xy.x) || isNaN(xy.y)) {
					return;
				}
				debug("container:"+container.id+", loading position:"+YAHOO.lang.dump(xy));
				
				YUD.setStyle(el, 'left', xy.x + 'px');
				YUD.setStyle(el, 'top', xy.y + 'px');
				YUD.setStyle(el, 'z-index', 900);
				YUD.setStyle(el, 'position', 'absolute');
				
				YUD.setStyle(this._loadingLayerId, "display", "");
			}
		}
	});
})();
(function() {
	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, YUA = YAHOO.util.Anim, Lang = YAHOO.lang;
	var debug = YAHOO.aerohive.widget.DebugLog;
	
	var SubDrawer = function(el, oAttr) {
        el = YUD.get(el);
        oAttr = oAttr || {};
        YAHOO.aerohive.widget.SubDrawer.superclass.constructor.call(this, el, oAttr); 
        
	};
	
	YAHOO.aerohive.widget.SubDrawer = SubDrawer;
	YAHOO.extend(SubDrawer, YAHOO.aerohive.widget.Drawer, {
		_open : false,
		openDrawer : function () {
			var elId = this.get("id");
			debug("open drawer..."+elId+" parent:"+this._parent.get("id"));
			YUD.replaceClass(YUD.get(this._parent.get("id") + this._parent.getParent().SUFFIX.CONTENT_ID), 
					this._parent.getParent().CLASSES.BLOCK, this._parent.getParent().CLASSES.HIDDEN);
			YUD.replaceClass(YUD.get(this._parent.getParent().CONFIG.TAG_NAME + "_" + elId), 
					this._parent.getParent().CLASSES.HIDDEN, this._parent.getParent().CLASSES.BLOCK);
			// fire the after opened event
			this.fireEvent(this.EVENT.AFTER_OPENED, YUD.get(elId));
			
			this._open = true;
		},
		closeDrawer : function () {
			var elId = this.get("id");
			debug("close drawer..."+elId);
			YUD.replaceClass(YUD.get(this._parent.get("id") + this._parent.getParent().SUFFIX.CONTENT_ID), 
					this._parent.getParent().CLASSES.HIDDEN, this._parent.getParent().CLASSES.BLOCK);
			YUD.replaceClass(YUD.get(this._parent.getParent().CONFIG.TAG_NAME + "_" + elId), 
					this._parent.getParent().CLASSES.BLOCK, this._parent.getParent().CLASSES.HIDDEN);
			YUD.get(elId + this._parent.getParent().SUFFIX.CONTENT_ID).innerHTML = "";
			// fire the after opened event
			this.fireEvent(this.EVENT.AFTER_CLOSED, YUD.get(elId));
			
			this._open = false;
		},
		isOpened : function () {
			return this._open;
		}
	});
})();
YAHOO.register("accordionview", YAHOO.aerohive.widget.AccordionView, {version: "1.11", build: "1"});