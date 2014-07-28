YAHOO.namespace("YAHOO.aerohive.widget");
YAHOO.aerohive.widget.DebugLog = function(msg) {
     //console.debug("tabviewSimple>>>"+msg);
};

(function() {
      //sub tabview
      var dom = YAHOO.util.Dom,
      event = YAHOO.util.Event,
      anim = YAHOO.util.Anim,
      lang = YAHOO.lang;
      var debug = YAHOO.aerohive.widget.DebugLog;
      
      var SubTabviewSimple = function(el, oAttr) {
        el = dom.get(el);
        oAttr = oAttr || {};
        YAHOO.aerohive.widget.SubTabviewSimple.superclass.constructor.call(this, el, oAttr);
    
      this.initEvents(oAttr);
      this.initSubTabViewSimple(oAttr);
      this.refresh(['id'], true);
   };
  
   YAHOO.aerohive.widget.SubTabviewSimple = SubTabviewSimple;
  
   YAHOO.extend(SubTabviewSimple, YAHOO.util.Element, {
        
            //belong to which TabviewSimple
            _parent : null,
            
            //is a function
            _getContentResult : null,
            
            //can set from run time
            _disabled : false,
            
            CLASS : {
                  OPENED: "sub_tabview_selected",
                  NOT_OPEN: "sub_tabview_not_selected",
                  MOUSE_OVER: "sub_tabview_mouseover",
                  MOUSE_OUT: "sub_tabview_not_selected",
                  CONTENT_HIDE: "sub_tabview_content_hide",
                  CONTENT_SHOW: "sub_tabview_content_show",
                  DISABLED_DISABLED: "sub_tabview_disabled",
                  DISABLED_HIDE: "sub_tabview_hide"
            },
            
            DISABLED_SHOW_TYPE : {
            	TYPE_HIDE: 'hide',
            	TYPE_DISABLED: 'disabled'
            },
            
            EVENT : {
                  BEFORE_OPEN: 'beforeOpen',
                  AFTER_OPENED: 'afterOpened',
                  BEFORE_CLOSE: 'beforeClose',
                  AFTER_CLOSED: 'afterClosed',
                  MOUSE_OVER: 'mouseover',
                  MOUSE_OUT: 'mouseout',
                  GET_CONTENT_RESULT: 'getContentResult'
            },
            
            initAttributes : function(oAttr) {
                  SubTabviewSimple.superclass.initAttributes.call(this, oAttr);
             this.setAttributeConfig('id', {
                  writeOnce : true,
                  validator : function(value) {
                       return (/^[a-zA-Z][\w0-9\-_.:]*$/.test(value));
                  },
                  value : dom.generateId(),
                  method : function(value) {
                       this.get('element').id = value;
                  }
             });
             this.setAttributeConfig('contentDiv', {
                 value : oAttr.contentDiv || '',
                 validator: lang.isString
             });
             this.setAttributeConfig('opened', {
                 value : oAttr.opened || false,
                 validator: lang.isBoolean
             });
             this.setAttributeConfig('disableOnclick', {
               value: oAttr.disableOnclick || false,
               validator: lang.isBoolean
             });
             this.setAttributeConfig('disableAll', {
                 value: oAttr.disableAll || false,
                 validator: lang.isBoolean
             });
             this.setAttributeConfig('disabledType', {
                 value: oAttr.disabledType || this.DISABLED_SHOW_TYPE.TYPE_HIDE,
                 validator: lang.isString
             });
        },
            
            initEvents : function (oAttr) {
            if (this.isDisabled()) {
                  return;
            }
            if(oAttr.mouseover && lang.isFunction(oAttr.mouseover)) {
                this.on(this.EVENT.MOUSE_OVER, oAttr.mouseover, this, true);
           } else {
                this.on(this.EVENT.MOUSE_OVER, this.defaultMouseOver, this, true);
           }
           if(oAttr.mouseout && lang.isFunction(oAttr.mouseout)) {
                this.on(this.EVENT.MOUSE_OUT, oAttr.mouseout, this, true);
           } else {
                this.on(this.EVENT.MOUSE_OUT, this.defaultMouseOut, this, true);
           }
            if(oAttr.beforeOpen && lang.isFunction(oAttr.beforeOpen)) {
                this.on(this.EVENT.BEFORE_OPEN, oAttr.beforeOpen, this, true);
           } else {
                this.on(this.EVENT.BEFORE_OPEN, function(){return true;}, this, true);
           }
            if(oAttr.afterOpened && lang.isFunction(oAttr.afterOpened)) {
                this.on(this.EVENT.AFTER_OPENED, oAttr.afterOpened, this, true);
           } else {
                this.on(this.EVENT.AFTER_OPENED, function(){return true;}, this, true);
           }
            if(oAttr.beforeClose && lang.isFunction(oAttr.beforeClose)) {
                this.on(this.EVENT.BEFORE_CLOSE, oAttr.beforeClose, this, true);
           } else {
                this.on(this.EVENT.BEFORE_CLOSE, function(){return true;}, this, true);
           }
            if(oAttr.afterClosed && lang.isFunction(oAttr.afterClosed)) {
                this.on(this.EVENT.AFTER_CLOSED, oAttr.afterClosed, this, true);
           } else {
                this.on(this.EVENT.AFTER_CLOSED, function(){return true;}, this, true);
           }
            if(!this.get("disableOnclick")) {
                this.on('click', this._onClick, this, true);
           }
       },
      
       initSubTabViewSimple : function(oAttr) {
         if (this.isDisabled()) {
                  this.gotoDisabled(this.get("disabledType"));
                  return;
         }
         if (this.get("opened")) {
               this.showContentDiv();
         } else {
               this.hideContentDiv();
         }
         if(oAttr.getContentResult && lang.isFunction(oAttr.getContentResult)) {
               this._getContentResult = oAttr.getContentResult;
         } else {
               this._getContentResult = function(){return null;};
         }
       },
      
       _onClick : function (arg) {
         if (this.isPaused() || this.isDisabled()) {
               return;
         }
           var ev;
           var curId = this.get("id");
           if(arg.nodeType === undefined) {
               ev = event.getTarget(arg);
               event.preventDefault(arg);
               event.stopPropagation(arg);
           } else {
               ev = arg;
           }
         
           var elClickedNode;
           if(ev.id == curId) {
                elClickedNode = ev;
           } else {
                if(ev.parentNode && ev.parentNode.id === this.get("id")) {
                     elClickedNode = ev.parentNode;
                }
                else {
                    return;
                }
           }
         
           if (this.isOpened() == false) {
                if(this.fireEvent(this.EVENT.BEFORE_OPEN, dom.get(curId))) {
                     //this.gotoOpen();
                   this.getParent().openCertainSubTabview(curId);
                     this.fireEvent(this.EVENT.AFTER_OPENED, dom.get(curId));
                } else {
                     return;
                }
           } else {
                if (this.getParent().getOpenedSubTabview() == curId) {
                  return;
                }
                if(this.fireEvent(this.EVENT.BEFORE_CLOSE, dom.get(curId))) {
                     //this.gotoClose();
                   this.getParent().closeCertainSubTabview(curId);
                     this.fireEvent(this.EVENT.AFTER_CLOSED, dom.get(curId));
                } else {
                     return;
                }
           }
         },
        
        setSubTabviewStyle : function(showType) {
             this.removeAllStylesOfSubTabview();
             dom.addClass(this.get("id"), showType);
        },
       
        removeAllStylesOfSubTabview : function() {
            var curEl = this.get("id");
            if (dom.hasClass(curEl, this.CLASS.MOUSE_OVER)) {
                 dom.removeClass(curEl, this.CLASS.MOUSE_OVER);
            }
            if (dom.hasClass(curEl, this.CLASS.MOUSE_OUT)) {
                 dom.removeClass(curEl, this.CLASS.MOUSE_OUT);
            }
            if (dom.hasClass(curEl, this.CLASS.OPENED)) {
                dom.removeClass(curEl, this.CLASS.OPENED);
           }
           if (dom.hasClass(curEl, this.CLASS.NOT_OPEN)) {
                dom.removeClass(curEl, this.CLASS.NOT_OPEN);
           }
        },
       
        removeHideOrShowOfContentDiv : function() {
            var curEl = this.get("contentDiv");
            if (dom.hasClass(curEl, this.CLASS.CONTENT_HIDE)) {
                dom.removeClass(curEl, this.CLASS.CONTENT_HIDE);
           }
           if (dom.hasClass(curEl, this.CLASS.CONTENT_SHOW)) {
                dom.removeClass(curEl, this.CLASS.CONTENT_SHOW);
           }
        },
       
        removeDisabledStyleOfSubTabview : function() {
            var curEl = this.get("id");
            if (dom.hasClass(curEl, this.CLASS.DISABLED_DISABLED)) {
                 dom.removeClass(curEl, this.CLASS.DISABLED_DISABLED);
            }
            if (dom.hasClass(curEl, this.CLASS.DISABLED_HIDE)) {
                 dom.removeClass(curEl, this.CLASS.DISABLED_HIDE);
            }
        },
       
        changeContentDivStyle : function(showType) {
            this.removeHideOrShowOfContentDiv();
            dom.addClass(this.get("contentDiv"), showType);
        },
       
        showContentDiv : function() {
            this.changeContentDivStyle(this.CLASS.CONTENT_SHOW);
            this.setSubTabviewStyle(this.CLASS.OPENED);
        },
       
        hideContentDiv : function() {
            this.changeContentDivStyle(this.CLASS.CONTENT_HIDE);
            this.setSubTabviewStyle(this.CLASS.NOT_OPEN);
        },
            
       isOpened : function() {
        if (dom.hasClass(this.get("id"), this.CLASS.OPENED)) {
               return true;
          }
          return false;
       },
      
       defaultMouseOver : function(arg) {
         if (this.isPaused()) {
               return;
         }
           if (!this.isOpened()) {
                this.setSubTabviewStyle(this.CLASS.MOUSE_OVER);
           }
      },
    
      defaultMouseOut : function(arg) {
            if (this.isPaused()) {
                  return;
            }
           if (!this.isOpened()) {
                this.setSubTabviewStyle(this.CLASS.MOUSE_OUT);
           }
      },
     
      setParent : function(parent) {
                  this._parent = parent;
            },
            getParent : function() {
                  return this._parent;
            },
            
            getContentDiv : function() {
                  return this.get("contentDiv");
            },
            
            gotoOpen : function() {
                  //it should change the sub tabview div and show the content div
                  this.setSubTabviewStyle(this.CLASS.OPENED);
                  this.showContentDiv();
            },
            
            gotoClose : function() {
                  //it should change the sub tabview div and hide the content div
                  this.setSubTabviewStyle(this.CLASS.NOT_OPEN);
                  this.hideContentDiv();
            },
            
            gotoDisabled : function(showType) {
                  this.setDisabled(true);
                  if (showType == this.DISABLED_SHOW_TYPE.TYPE_HIDE) {
                        this.setSubTabviewStyle(this.CLASS.DISABLED_HIDE);
                  } else {
                        this.setSubTabviewStyle(this.CLASS.DISABLED_DISABLED);
                  }
                  this.gotoClose();
            },
            
            gotoEnabled : function(oAttr) {
                  //should enable events and display
                  //restore style
                  this.removeDisabledStyleOfSubTabview();
                  this.gotoClose();
                  //set to be not disabled
                  this.setDisabled(false);
            },
            
            getId : function() {
                  return this.get("id");
            },
            
            isDisabled : function() {
                  return this.getDisabled() || this.get("disableAll");
            },
            
            setDisabled : function(disabled) {
                  this._disabled = disabled;
            },
            getDisabled : function() {
                  return this._disabled;
            },
            
            isPaused : function() {
                  return this.getParent().getPaused();
            },
      
       //used to get content from user's sub tabview
       getContentResult : function() {
         return this._getContentResult();
       }
   });
}
)();

(function() {
      //main tabview
      var dom = YAHOO.util.Dom,
            event = YAHOO.util.Event,
            anim = YAHOO.util.Anim,
            lang = YAHOO.lang;
      var debug = YAHOO.aerohive.widget.DebugLog;
      
      var TabviewSimple = function(el, oAttr) {
          el = dom.get(el);
          oAttr = oAttr || {};
          YAHOO.aerohive.widget.TabviewSimple.superclass.constructor.call(this, el, oAttr);
      
        this.initEvents(oAttr);
        this.initTabViewSimple(oAttr);
        this.refresh(['id'], true);
      };
      
      YAHOO.aerohive.widget.TabviewSimple = TabviewSimple;
      YAHOO.extend(TabviewSimple, YAHOO.util.Element, {
            //holds all sub tabviews
            _subTabviews: null,
            
            //which sub tabview is now opened
            _curSubTabviewOpened: null,
            
            //set to stop some events
            _paused: false,
            
            initAttributes : function(oAttr) {
                  TabviewSimple.superclass.initAttributes.call(this, oAttr);
               this.setAttributeConfig('id', {
                    writeOnce : true,
                    validator : function(value) {
                         return (/^[a-zA-Z][\w0-9\-_.:]*$/.test(value));
                    },
                    value : dom.generateId(),
                    method : function(value) {
                         this.get('element').id = value;
                    }
               });
               this.setAttributeConfig('disabledType', {
                 value: oAttr.disabledType || 'hide',
                 validator: lang.isString
               });
      },
      
      initEvents : function (oAttr) {
      },
      
      initTabViewSimple : function(oAttr) {
            this.initSubTabviews(oAttr);
      },
      
      initSubTabviews : function(oAttr) {
            if (this._subTabviews == null) {
                  this._subTabviews = new Array();
            }
            if (oAttr.subTabviews && lang.isArray(oAttr.subTabviews)) {
                  for (var i = 0; i < oAttr.subTabviews.length; i++) {
                        var attr = oAttr.subTabviews[i];
                        var subTabviewTmp = new YAHOO.aerohive.widget.SubTabviewSimple(attr.id,
                                    attr);
                        subTabviewTmp.setParent(this);
                        this._subTabviews.push(subTabviewTmp);
                        if (subTabviewTmp.get("opened")) {
                        	this._curSubTabviewOpened = subTabviewTmp.getId();
                        }
                  }
            }
            if (this._curSubTabviewOpened == null) {
                  this.openFirstNotDisabledSubTabview();
            }
      },
      
      appendSubTabview : function(oAttr) {
    	  if (this._subTabviews == null) {
              this._subTabviews = new Array();
    	  }
    	  if (oAttr.subTabview) {
    		  var attr = oAttr.subTabview;
		      var subTabviewTmp = new YAHOO.aerohive.widget.SubTabviewSimple(attr.id,
		    		  attr);
		      subTabviewTmp.setParent(this);
		      this._subTabviews.push(subTabviewTmp);
		      if (subTabviewTmp.get("opened")) {
		    	  this._curSubTabviewOpened = subTabviewTmp.getId();
		      }
    	  }
      },
      
      openFirstNotDisabledSubTabview : function() {
    	  if(this._subTabviews != null && this._subTabviews.length > 0) {
    		  for (var i = 0; i < this._subTabviews.length; i++) {
    			  var subTabviewTmp = this._subTabviews[i];
    			  if (subTabviewTmp != null && subTabviewTmp.isDisabled() == false) {
    				  this.openCertainSubTabview(subTabviewTmp.getId());
    			  }
    		  }
        }
      },
      
      getOpenedSubTabview : function() {
            if (this._subTabviews != null && this._subTabviews.length > 0) {
                  for(var i = 0; i < this._subTabviews.length; i++) {
                        var subTabviewTmp = this._subTabviews[i];
                        if (subTabviewTmp.isOpened()) {
                              this._curSubTabviewOpened = subTabviewTmp.getId();
                              break;
                        }
                  }
            }
            this._curSubTabviewOpened = null;
      },
      
      getExistSubTabview : function(id) {
            if (this._subTabviews != null && this._subTabviews.length > 0) {
                  for(var i = 0; i < this._subTabviews.length; i++) {
                        var subTabviewTmp = this._subTabviews[i];
                        if (subTabviewTmp.getId() == id) {
                              return subTabviewTmp;;
                        }
                  }
            }
            return null;
      },
      
      openCertainSubTabview : function(id) {
            //first, try to close the opened one
            if (this._curSubTabviewOpened != null) {
                  this.closeCertainSubTabview(this._curSubTabviewOpened);
            }
            var subTabviewTmp = this.getExistSubTabview(id);
            if (subTabviewTmp != null) {
                  if (subTabviewTmp.isOpened() || subTabviewTmp.isDisabled()) {
                        return;
                  }
                  subTabviewTmp.gotoOpen();
                  this._curSubTabviewOpened = subTabviewTmp.getId();
            }
      },
      
      closeCertainSubTabview : function(id) {
            var subTabviewTmp = this.getExistSubTabview(id);
            if (subTabviewTmp != null) {
                  if (subTabviewTmp.isDisabled() || subTabviewTmp.isOpened() == false ) {
                        return;
                  }
                  subTabviewTmp.gotoClose();
                  this._curSubTabviewOpened = null;
            }
      },
      
      disableCertainSubTabview : function(id) {
            var subTabviewTmp = this.getExistSubTabview(id);
            if (subTabviewTmp != null) {
                  if (subTabviewTmp.isDisabled()) {
                        return;
                  }
                  subTabviewTmp.gotoDisabled(this.get("disabledType"));
                  //if closed the opened sub tabview
                  if (this.getOpenedSubTabview() == id) {
                	  this._curSubTabviewOpened = null;
                  }
            }
      },
      
      disableSubTabviews : function(toDisableArray) {
            if (toDisableArray) {
                  if (lang.isString(toDisableArray)) {
                        this.disableCertainSubTabview(toDisableArray);
                  } else if (lang.isArray(toDisableArray)) {
                        for (var i = 0; i < toDisableArray.length; i++) {
                              this.disableCertainSubTabview(toDisableArray[i]);
                        }
                  }
            }
            if (this.getOpenedSubTabview() == null) {
            	this.openFirstNotDisabledSubTabview();
            }
      },
      
      enableCertainSubTabview : function(id) {
            var subTabviewTmp = this.getExistSubTabview(id);
            if (subTabviewTmp != null) {
                  if (subTabviewTmp.isDisabled() == false) {
                        return;
                  }
                  subTabviewTmp.gotoEnabled();
            }
      },
      
      enableSubTabviews : function(toEnableArray) {
            if (toEnableArray) {
                  if (lang.isString(toEnableArray)) {
                        this.enableCertainSubTabview(toEnableArray);
                  } else if (lang.isArray(toEnableArray)) {
                        for (var i = 0; i < toEnableArray.length; i++) {
                              this.enableCertainSubTabview(toEnableArray[i]);
                        }
                  }
            }
      },
      
      isCertainSubTabviewDisabled : function(subTabview) {
    	  var subTabviewTmp = this.getExistSubTabview(subTabview);
          if (subTabviewTmp != null) {
                return subTabviewTmp.isDisabled();
          }
          return true;
      },
      
      setPaused: function(paused) {
            this._paused = paused;
      },
      getPaused: function(paused) {
            return this._paused;
      },
      
      getOpenedSubTabview : function() {
            return this._curSubTabviewOpened;
      },
      
      //get all results from all sub tabview
      getAllContentResults : function() {
            var result = new Array();
            if (this._subTabviews != null && this._subTabviews.length > 0) {
                  for(var i = 0; i < this._subTabviews.length; i++) {
                        var subTabviewTmp = this._subTabviews[i];
                        if (!subTabviewTmp.isDisabled()) {
                              var tmpArray = new Array();
                              tmpArray.push(subTabviewTmp.getId());
                              tmpArray.push(subTabviewTmp.getContentResult());
                              result.push(tmpArray);
                        }
                  }
            }
            return result;
      },
      
      getCertainContentResult : function(subTabview) {
                  if (this._subTabviews != null && this._subTabviews.length > 0) {
                        for(var i = 0; i < this._subTabviews.length; i++) {
                              var subTabviewTmp = this._subTabviews[i];
                              if (subTabviewTmp.getId() != subTabview) {
                                    continue;
                              }
                              if (!subTabviewTmp.isDisabled()) {
                                    return subTabviewTmp.getContentResult();
                              }
                        }
                  }
            }
      
            });
}
)();

YAHOO.register("tabviewsimple", YAHOO.aerohive.widget.TabviewSimple, {version: "1.0", build: "1"});