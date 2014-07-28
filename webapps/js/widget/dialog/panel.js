/*!
 * This widget is created for the common panel under Aerohive style
 * 
 * Dependencies:
 * 	YUI core, 
 *  YUI Panel,
 *  YUI Element
 *  
 * $Revision: 1.2 $
 * $Log: panel.js,v $
 * Revision 1.2  2012/11/06 07:37:06  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.1  2012/10/18 05:21:32  ylin
 * UI widget - panel initial.
 *
 */
YAHOO.namespace("Aerohive.YUI2.widget");
YAHOO.Aerohive.YUI2.widget.DebugLog = function(msg) {
	//YAHOO.log(msg, "debug", "panel.js");
	if(window.console && console.debug) {
		if(typeof msg == 'string') {
			//console.debug(msg);
		} else {
			//console.dir(msg);
		}
	}
};
(function() {
	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, LANG = YAHOO.lang;
	var debug = YAHOO.Aerohive.YUI2.widget.DebugLog;
	
	var Panel = function(el, userConfig) {
		el = YUD.get(el);		
		userConfig = userConfig || {};
		debug("before set id="+el.id);
		YAHOO.Aerohive.YUI2.widget.Panel.superclass.constructor.call(this, el, userConfig);
		debug("after set id="+el.id);
		
		debug("start init HTML");
		this.initHTML(el, userConfig);
		debug("start init events");
		this.initEvents(userConfig);
		debug("refresh attr");
		
		this.refresh(['id', 'width'],true);
	};
	YAHOO.Aerohive.YUI2.widget.Panel = Panel;
	YAHOO.extend(Panel, YAHOO.util.Element, {
		DEF_PANEL_CONFIG: {
			visible:false,
			fixedcenter:false,
			close: false,
			draggable:false,
			modal:true,
			constraintoviewport:true,
			underlay: "none",
			zIndex:1
		},
		DEF_TAG: {
			// tag name for the wrapper around the content for a panel
			CONTENT_TAG_NAME: 'div',
			TABLE_TAG_NAME: 'table',
			ROW_TAG_NAME: 'tr',
			CELL_TAG_NAME: 'td'
		},
		CLASS: {
			AHPANEL: 'ahPanel',
			TOP_RADIUS_LEFT: 'ul',
			TOP_RADIUS_MIDDLE: 'um',
			TOP_RADIUS_RIGHT: 'ur',
			CONTENT_LEFT: 'ml',
			CONTENT_RIGHT: 'mr',
			CONTENT_MIDDLE: 'mm',
			BOTTOM_RADIUS_LEFT: 'bl',
			BOTTOM_RADIUS_MIDDLE: 'bm',
			BOTTOM_RADIUS_RIGHT: 'br',
			CLOSE_BUTTON: 'ahPanel-close'
		},
		EVENT: {
			BEFORE_OPEN_PANEL: 'beforeOpen',
			AFTER_CLOSED_PANEL: 'afterClose'
		},
		DEF_TABLE_STYLE: {width: '100%', border: '0', cellspacing: '0', cellpadding: 0},
		_yuiPanel: null,
		_container: null,
		_containerWidthEl: null,
		_content: null, // initialize the content
		_iframeHTMLObj: null,
		_closeButton: null,
		
		initAttributes: function(userConfig) {
			Panel.superclass.initAttributes.call(this, userConfig);
			/*
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
			*/
			this.setAttributeConfig('width', {
				value : '480',
				validator: LANG.isNumber
			});
			this.setAttributeConfig('maxHeight', {
				// use for limit the position when the y-scrollbar exist
				value : userConfig.maxHeight || false
			});
			this.setAttributeConfig('useIframe', {
				value : userConfig.useIframe || false,
				validator: LANG.isBoolean
			});
			this.setAttributeConfig('useNativeDialog', {
				value : userConfig.useNativeDialog || false,
				validator: LANG.isBoolean
			});
			this.setAttributeConfig('YUIConfig', {
				value : userConfig.YUIConfig || false
			});
			this.setAttributeConfig('title', {
				value : userConfig.title || false
			});
			this.setAttributeConfig('close', {
				value : userConfig.close || true,
				validator: LANG.isBoolean
			});
			this.setAttributeConfig('fixed', {
				value : userConfig.fixed || false,
				validator: LANG.isBoolean
			});
			this.setAttributeConfig('disabledESC', {
				value : userConfig.disabledESC || false,
				validator: LANG.isBoolean
			});
			this.setAttributeConfig('closeIcon', {
				value: '/hm/images/cancel.png',
				validator: LANG.isString
			});
			this.setAttributeConfig('renderBody', {
				value: userConfig.renderBody || false
			});
		},
		initHTML: function(el, userConfig) {
			var self = this;
			
			debug("start init...");
			
			debug('id: '+self.get('id')+', width: '+self.get('width'));
						
			if(self.get('useNativeDialog')) {
				var widgetId = el.id;
				//create the skeleton
				self._container = document.createElement(self.DEF_TAG.CONTENT_TAG_NAME);
				self._container.id = widgetId + 'YPanel';
				var title = document.createElement(self.DEF_TAG.CONTENT_TAG_NAME);
				YUD.addClass(title, 'hd');
				self._container.appendChild(title);
				var body = document.createElement(self.DEF_TAG.CONTENT_TAG_NAME);
				YUD.addClass(body, 'bd');
				self._container.appendChild(body);
				
				var contentEl = YUD.get(el);
				body.appendChild(contentEl);
				YUD.setStyle(contentEl, 'display', '');
				
			} else {
				var widgetId = el.id;
				// create the skeleton
				self._container = document.createElement(self.DEF_TAG.CONTENT_TAG_NAME);
				self._container.id = widgetId + 'APanel';
				YUD.addClass(self._container, self.CLASS.AHPANEL);
				
				// create a table
				var tableEl = document.createElement(self.DEF_TAG.TABLE_TAG_NAME);
				self._initHTMLAttribute(tableEl, self.DEF_TABLE_STYLE);
				
				// _add a new row for top
				var topCell = tableEl.insertRow(0).insertCell(0);
				var topShadowTable = document.createElement(self.DEF_TAG.TABLE_TAG_NAME);
				self._initHTMLAttribute(topShadowTable, self.DEF_TABLE_STYLE);
				var topShadowRow = topShadowTable.insertRow(0);
				// __add cells for border RADIUS effective
				YUD.addClass(topShadowRow.insertCell(0), self.CLASS.TOP_RADIUS_LEFT);
				self._containerWidthEl = topShadowRow.insertCell(1);
				YUD.addClass(self._containerWidthEl, self.CLASS.TOP_RADIUS_MIDDLE);
				YUD.addClass(topShadowRow.insertCell(2), self.CLASS.TOP_RADIUS_RIGHT);
				topCell.appendChild(topShadowTable);
				// _add a new row for content
				var contentCell = tableEl.insertRow(1).insertCell(0);
				var contentTable = document.createElement(self.DEF_TAG.TABLE_TAG_NAME);
				self._initHTMLAttribute(contentTable, self.DEF_TABLE_STYLE);
				var contentRow = contentTable.insertRow(0);
				YUD.addClass(contentRow.insertCell(0), self.CLASS.CONTENT_LEFT);
				self._content = contentRow.insertCell(1);
				YUD.addClass(self._content, self.CLASS.CONTENT_MIDDLE);
				YUD.addClass(contentRow.insertCell(2), self.CLASS.CONTENT_RIGHT);
				contentCell.appendChild(contentTable);
				// _add a new row for bottom
				var bottomCell = tableEl.insertRow(2).insertCell(0);
				var bottomShadowTable = document.createElement(self.DEF_TAG.TABLE_TAG_NAME);
				self._initHTMLAttribute(bottomShadowTable, self.DEF_TABLE_STYLE);
				var bottomShadowRow = bottomShadowTable.insertRow(0);
				// __add cells for RADIUS effective
				YUD.addClass(bottomShadowRow.insertCell(0), self.CLASS.BOTTOM_RADIUS_LEFT);
				var bottomMidCell = bottomShadowRow.insertCell(1);
				YUD.addClass(bottomMidCell, self.CLASS.BOTTOM_RADIUS_MIDDLE);
				YUD.addClass(bottomShadowRow.insertCell(2), self.CLASS.BOTTOM_RADIUS_RIGHT);
				bottomCell.appendChild(bottomShadowTable);
				
				self._container.appendChild(tableEl);
				
				// set the width
				YUD.setStyle(self._containerWidthEl, 'width', self.get('width') + 'px');
				YUD.setStyle(bottomMidCell, 'width', self.get('width') + 'px');
				YUD.setStyle(bottomCell, 'width', '100%');
				
				// append to body
				if(document.body.firstChild) {
					document.body.insertBefore(self._container, document.body.firstChild);
				} else {
					document.body.appendChild(self._container);
				}
				// append the content to dialog
				var contentEl = YUD.get(el);
				if(self.get('useIframe') && self._iframeHTMLObj) {
					self._content.appendChild(contentEl);
				} else {
					// hide anchor for annoying dotted border
					self._content.innerHTML = '<a style="display:none" href="javascript: void(0);"></a>';
					// close button
					if(self.get('close')) {
						var autoId = YUD.generateId();
						self._content.innerHTML += '<a id="' +autoId + '" class="' + self.CLASS.CLOSE_BUTTON + '" href="javascript: void(0);"><img class="dinl close-icon" title="Cancel" alt="Cancel" src="'+ 
							self.get('closeIcon')+ '"></a>';
						self._closeButton = YUD.get(autoId);
						
						// set style
						//YUD.setStyle(self._content, 'position', 'relative');
						YUD.setStyle(self._closeButton, 'position', 'absolute');
						YUD.setStyle(self._closeButton, 'right', '40px');
						
						debug("add close button");
					}
					//self._content.appendChild();// title and icon
					self._content.appendChild(contentEl);
				}
				YUD.setStyle(contentEl, 'display', '');
				
				debug("finish create panel content");
			}
			
			YUD.setStyle(self._container, 'borderStyle', 'none');
			if(self.get('fixed')) {
				var fixedContainer = document.createElement(self.DEF_TAG.CONTENT_TAG_NAME);
				fixedContainer.appendChild(self._container);
				YUD.setStyle(fixedContainer, 'position', 'fixed');
			}
			
			// change the width for iframe type
			if(userConfig.YUIConfig) {
				var defConfig = self.DEF_PANEL_CONFIG;
				var key;
				for(key in userConfig.YUIConfig) {
					if(key) {
						defConfig[key] = userConfig.YUIConfig[key];
					}
				}				
				self._yuiPanel = new YAHOO.widget.Panel(self._container, defConfig);
			} else {
				// create a panel as default
				self._yuiPanel = new YAHOO.widget.Panel(self._container, self.DEF_PANEL_CONFIG);
			}
			
			if(self.get('useNativeDialog')) {
				self._yuiPanel.cfg.setProperty("width", self.get('width') + 'px');
				self._yuiPanel.cfg.queueProperty("close", true);
				if(self.get('title')) {
					//set title for AH panel
					self._yuiPanel.setHeader(self.get('title'));
				}
			} else {
				// override the width
				self._yuiPanel.cfg.setProperty("width", (self.get('width') + 80) + 'px');
			}
			
			// set the height/width for iframe source panel
			if(userConfig.useIframe && self._iframeHTMLObj) {
				self._iframeHTMLObj.width = self.get('width');
				self._iframeHTMLObj.height = self.get('height');
				self._iframeHTMLObj.src = '';
			}
			debug("finish init...")
		},
		initEvents: function(userConfig) {
			var self = this;
			
			if(self._yuiPanel) {
				if(self._closeButton) {
					debug('bind the click event on close button');
					YUE.on(self._closeButton, 'click', self.closeDialog, this, true);
				}
				// escape key to close box
				if(!userConfig.disabledESC) {
					var escListener = new YAHOO.util.KeyListener(
							document, 
							{
								keys : 27
							}, 
							{
								// add scope and fn for iframe
								fn : function(){return self.closeDialog();},
								scope : self._yuiPanel,
								correctScope : true
							});
					self._yuiPanel.cfg.queueProperty("keylisteners", escListener);
				}
			}
			
			if(userConfig.beforeOpen && LANG.isFunction(userConfig.beforeOpen)) {
				self.on(self.EVENT.BEFORE_OPEN_PANEL, userConfig.beforeOpen, this, true); 
			}
			if(userConfig.afterClose && LANG.isFunction(userConfig.afterClose)) {
				self.on(self.EVENT.AFTER_CLOSED_PANEL, userConfig.afterClose, this, true); 
			}
			// should render here because the key listener
			if(userConfig.renderBody) {
				self._yuiPanel.render(userConfig.renderBody);
			} else {
				self._yuiPanel.render(document.body);
			}
		},
		openDialog: function(notAdjustPosition) {
			var self = this;
			
			if(self._yuiPanel) {
				debug('open panel...');
				self.fireEvent(self.EVENT.BEFORE_OPEN_PANEL, YUD.get(self.get('id')));
				// change the width for iframe type
				//self._yuiPanel.show();
				self._yuiPanel.center();
				self._yuiPanel.cfg.setProperty('visible', true);
				
				if(notAdjustPosition) {
					return;
				}
				if(self.get('maxHeight') && LANG.isNumber(self.get('maxHeight'))) {
					var documentHeight = YUD.getDocumentHeight();
					var clientRegion = YUD.getClientRegion();
					var panelRegion = YUD.getRegion(self.get('id'));
					var panelY = self._yuiPanel.cfg.getProperty('y');
					debug("documentHeight="+documentHeight);
					debug("clientRegion="+clientRegion);
					debug("offsetHeight="+panelRegion);
					if(clientRegion && clientRegion.height < documentHeight && panelRegion) {
						if(panelRegion.top == panelRegion.bottom || panelRegion.height == 0) {
							var diff = clientRegion.height - self.get('maxHeight');
							debug("panelY="+panelY+", diff="+diff);
							if(diff > 0 && panelY > diff) {
								self._yuiPanel.cfg.setProperty('y', panelY - diff);
								debug("change the y position.");
							}
						}
					}
				}
			}
		},
		closeDialog: function() {
			var self = this;
			if(self._yuiPanel){
				debug('close panel...');
				//self._yuiPanel.hide();
				self._yuiPanel.cfg.setProperty('visible', false);
				// change the width for iframe type
				self.fireEvent(self.EVENT.AFTER_PANEL_CLOSED, YUD.get(self.get('id')));
			}
		},
		center: function() {
			var self = this;
			if(self._yuiPanel) {
				self._yuiPanel.center();
			}
		},
		_initHTMLAttribute: function(element, attributes) {
			if(element && LANG.isObject(attributes)) {
				for(var key in attributes) {
					YUD.setAttribute(element, key, attributes[key]);
				}
			}
		}
	});
})();
YAHOO.register("panel", YAHOO.Aerohive.YUI2.widget.Panel, {version: "1.0", build: "1"});