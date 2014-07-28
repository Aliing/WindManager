//a table whose rows can be selected, by wx, 7/23/2011.
YAHOO.namespace("YAHOO.aerohive.widget");
YAHOO.aerohive.widget.DebugLog = function(msg) {
     //console.debug("selectedtable>>>"+msg);
};
YAHOO.aerohive.widget.DebugLogTime = function(msg, curTime, costTime) {
    //console.debug("selectedtable>>>"+msg+"  === " + curTime + ", cost: " + costTime + "ms");
};
(function() {
     var dom = YAHOO.util.Dom,
          event = YAHOO.util.Event,
          anim = YAHOO.util.Anim,
          lang = YAHOO.lang;
     var debug = YAHOO.aerohive.widget.DebugLog;
     var debugTime = (function(){
    	 var lastLogTime;
    	 return function(msg, first) {
    		 var curTime = new Date().getTime();
    		 if (lastLogTime == undefined || lastLogTime == null) lastLogTime = curTime;
    		 if (first && first == true) lastLogTime = curTime;
    		 YAHOO.aerohive.widget.DebugLogTime(msg, curTime, curTime - lastLogTime);
    		 lastLogTime = curTime;
    	 }
     })();
   
     var SelectedTableRow = function(el, oAttr) {
        el = dom.get(el);
        oAttr = oAttr || {};
        YAHOO.aerohive.widget.SelectedTableRow.superclass.constructor.call(this, el, oAttr);
      
        this.initEvents(oAttr);
        this.initTheRow();
        this.refresh(['id'], true);
     };
   
     YAHOO.aerohive.widget.SelectedTableRow = SelectedTableRow;
   
     YAHOO.extend(SelectedTableRow, YAHOO.util.Element, {
          //it will be used to do scroll
          _animator: null,
        
          //define which table it belongs
          _parent: null,
         
          //used at run time
          _disabled: false,
        
          //define some customer events
          EVENT : {
              MOUSE_OVER: 'mouseover',
              MOUSE_OUT: 'mouseout',
              BEFORE_SELECT: 'beforeselect',
              BEFORE_DESELECT: 'beforedeselect',
              AFTER_SELECTED: 'afterselected',
              AFTER_DESELECTED: 'afterdeselected',
              DISABLED_ROW_SHOW: 'disabledrowshow'
         },
         //define which class to show
         CLASS : {
              SELECTED: 'trSelectedCss',
              NOT_SELECTED: 'trNotSelectedCss',
              MOUSE_OVER: 'trHoverCss',
              MOUSE_OUT: 'trHoverOutCss',
              DISABLED_ROW_STYLE: 'trDisabledCss',
              HIDDEN_ROW_STYLE: 'trHiddenCss'
         },
         //what to show with disabled item
         DISABLED_ROWS_SHOW_TYPE : {
             TYPE_HIDE: 'hide',
             TYPE_DISABLED: 'disabled'
        },
          //what should used is only table id
         initAttributes : function(oAttr) {
              SelectedTableRow.superclass.initAttributes.call(this, oAttr);
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
               this.setAttributeConfig('disableOnclick', {
                 value: oAttr.disableOnclick || false,
                 validator: YAHOO.lang.isBoolean
               });
               this.setAttributeConfig('showDisabledType', {
                   value : oAttr.showDisabledType || this.DISABLED_ROWS_SHOW_TYPE.TYPE_DISABLED,
                   validator: lang.isString
               });
               this.setAttributeConfig('auxiliaryContent', {
                   value : oAttr.auxiliaryContent || '',
                   validator: lang.isString
               });
          },
          initEvents : function (oAttr) {
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
               if(oAttr.beforeselect && lang.isFunction(oAttr.beforeselect)) {
                    this.on(this.EVENT.BEFORE_SELECT, oAttr.beforeselect, this, true);
               } else {
                    this.on(this.EVENT.BEFORE_SELECT, function(){return true;}, this, true);
               }
               if(oAttr.beforedeselect && lang.isFunction(oAttr.beforedeselect)) {
                    this.on(this.EVENT.BEFORE_DESELECT, oAttr.beforedeselect, this, true);
               } else {
                    this.on(this.EVENT.BEFORE_DESELECT, function(){return true;}, this, true);
               }
               if(oAttr.afterselected && lang.isFunction(oAttr.afterselected)) {
                    this.on(this.EVENT.AFTER_SELECTED, oAttr.afterselected, this, true);
               }
               if(oAttr.afterdeselected && lang.isFunction(oAttr.afterdeselected)) {
                    this.on(this.EVENT.AFTER_DESELECTED, oAttr.afterdeselected, this, true);
               }
               if(oAttr.disabledrowshow && lang.isFunction(oAttr.disabledrowshow)) {
                   this.on(this.EVENT.DISABLED_ROW_SHOW, oAttr.disabledrowshow, this, true);
              } else {
                   this.on(this.EVENT.DISABLED_ROW_SHOW, this.defaultDisabledShowStyle, this, true);
              }
               if(!this.get("disableOnclick")) {
                    this.on('click', this._onClick, this, true);
               }
          },
        
          _onClick : function (arg) {
              if (this.isDisabled()) {
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
                 else if (ev.tagName.toLowerCase() == "span") {
                	 // add support for span in td
                	 if (ev.parentNode 
                			 && ev.parentNode.parentNode 
                			 && ev.parentNode.parentNode.id === this.get("id")) {
                		 elClickedNode = ev.parentNode.parentNode;
                	 }
                 }
                 else {
                     return;
                 }
            }
          
            if (this.isSelected()) {
                 if(this.fireEvent(this.EVENT.BEFORE_DESELECT, dom.get(curId))) {
                      this.setRowStyle(this.CLASS.NOT_SELECTED);
                      this.fireEvent(this.EVENT.AFTER_DESELECTED, dom.get(curId));
                 } else {
                      return;
                 }
            } else {
                 if(this.fireEvent(this.EVENT.BEFORE_SELECT, dom.get(curId))) {
                      if (this.getParent().isMultipleSelect()) {
                      } else {
                           this.getParent().removeAllSelectedRows();
                      }
                      this.setRowStyle(this.CLASS.SELECTED);
                      this.fireEvent(this.EVENT.AFTER_SELECTED, dom.get(curId));
                 } else {
                      return;
                 }
            }
          },
          
          initTheRow : function() {
        	  if (this.isSelected() == false) {
        		  this.setRowStyle(this.CLASS.NOT_SELECTED);
        	  }
          },
          
          isSelected : function() {
               if (dom.hasClass(this.get("id"), this.CLASS.SELECTED)) {
                    return true;
               }
               return false;
          },
        
          setRowStyle : function(showType) {
               this.removeAllRowStyles();
               dom.addClass(this.get("id"), showType);
          },
        
          setDeselectRowStyle : function() {
               this.setRowStyle(this.CLASS.NOT_SELECTED);
          },
        
          removeAllRowStyles : function() {
               var curEl = this.get("id");
               if (dom.hasClass(curEl, this.CLASS.MOUSE_OVER)) {
                    dom.removeClass(curEl, this.CLASS.MOUSE_OVER);
               }
               if (dom.hasClass(curEl, this.CLASS.MOUSE_OUT)) {
                    dom.removeClass(curEl, this.CLASS.MOUSE_OUT);
               }
               if (dom.hasClass(curEl, this.CLASS.SELECTED)) {
                    dom.removeClass(curEl, this.CLASS.SELECTED);
               }
               if (dom.hasClass(curEl, this.CLASS.NOT_SELECTED)) {
                    dom.removeClass(curEl, this.CLASS.NOT_SELECTED);
               }
          },
         
          removeDisabledShowStyles : function() {
              var curEl = this.getId();
              if (dom.hasClass(curEl, this.CLASS.HIDDEN_ROW_STYLE)) {
                  dom.removeClass(curEl, this.CLASS.HIDDEN_ROW_STYLE);
             }
             if (dom.hasClass(curEl, this.CLASS.DISABLED_ROW_STYLE)) {
                  dom.removeClass(curEl, this.CLASS.DISABLED_ROW_STYLE);
             }
          },
        
          getRowIdValue : function() {
               return this.getParent().getRowIdValue(this.get("id"));
          },
        
          defaultMouseOver : function(arg) {
              if (this.isDisabled()) {
                    return;
              }
               if (!this.isSelected()) {
                    this.setRowStyle(this.CLASS.MOUSE_OVER);
               }
          },
        
          defaultMouseOut : function(arg) {
              if (this.isDisabled()) {
                    return;
              }
               if (!this.isSelected()) {
                    this.setRowStyle(this.CLASS.MOUSE_OUT);
               }
          },
         
          defaultDisabledShowStyle : function(arg) {
              var showType = this.get("showDisabledType");
              var el = this.get("id");
              if (this.DISABLED_ROWS_SHOW_TYPE.TYPE_HIDE == showType) {
                  dom.addClass(el, this.CLASS.HIDDEN_ROW_STYLE);
              } else if (this.DISABLED_ROWS_SHOW_TYPE.TYPE_DISABLED == showType) {
                  dom.addClass(el, this.CLASS.DISABLED_ROW_STYLE);
              }
          },
          
          setAuxiliaryContentText : function(text) {
        	  if (this.get("auxiliaryContent") != null && this.get("auxiliaryContent") != '') {
        		  var auxiliaryTmp = this.getAuxiliaryHolderId();
        		  if (auxiliaryTmp) {
        			  auxiliaryTmp.innerHTML = text;
        		  }
        	  }  
          },
          
          getAuxiliaryHolderId : function() {
        	  if (this.get("auxiliaryContent") != null && this.get("auxiliaryContent") != '') {
        		  var auxiliaryIdText = this.get("auxiliaryContent") + this.getRowIdValue();
        		  return dom.get(auxiliaryIdText);
        	  }
        	  return null;
          },
         
          gotoDisabled : function() {
        	  this.removeAllRowStyles();
        	  this.removeDisabledShowStyles();
              this.fireEvent(this.EVENT.DISABLED_ROW_SHOW, dom.get(this.getId()));
              this.setDisabled(true);
          },
         
          gotoEnabled : function() {
              this.setDisabled(false);
              this.setDeselectRowStyle();
              this.removeDisabledShowStyles();
          },
         
          isDisabled : function() {
              return this.getDisabled();
          },
         
          setDisabled : function(disabled) {
              this._disabled = disabled;
          },
          getDisabled : function() {
              return this._disabled;
          },
         
          getId : function() {
              return this.get("id");
          },
        
          setParent : function(parent) {
               this._parent = parent;
          },
          getParent : function() {
               return this._parent;
          }
        
     });
}
)();

(function() {
     var dom = YAHOO.util.Dom,
          event = YAHOO.util.Event,
          anim = YAHOO.util.Anim,
          lang = YAHOO.lang;
     var debug = YAHOO.aerohive.widget.DebugLog;
   
     var SelectedTable = function(el, oAttr) {
        el = dom.get(el);
        oAttr = oAttr || {};
        YAHOO.aerohive.widget.SelectedTable.superclass.constructor.call(this, el, oAttr);
      
        this.initEvents(oAttr);
        this.initRows(oAttr);
        this.refresh(['id'], true);
     };
   
     YAHOO.aerohive.widget.SelectedTable = SelectedTable;
   
     YAHOO.extend(SelectedTable, YAHOO.util.Element, {
          //it will be used to do scroll
          _animator: null,
        
          //all rows a table contained
          _rows: null,
   
          //define which rows can not be selected
          _disabledRows: null,
   
          EVENT : {
              DISABLED_ROWS_SHOW: 'disabledrowsshow',
              ROW_MOUSE_OVER: 'rowmouseover',
              ROW_MOUSE_OUT: 'rowmouseout',
              ROW_BEFORE_SELECT: 'rowbeforeselect',
              ROW_BEFORE_DESELECT: 'rowbeforedeselect',
              ROW_AFTER_SELECTED: 'rowafterselected',
              ROW_AFTER_DESELECTED: 'rowafterdeselected'
          },
        
          //to initialize some attributes
          initAttributes : function(oAttr) {
               SelectedTable.superclass.initAttributes.call(this, oAttr);
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
               this.setAttributeConfig('name', {
                    value : oAttr.name || oAttr.id,
                    validator: lang.isString
               });
               this.setAttributeConfig('animate', {
                    value : oAttr.animate || false
               });
               this.setAttributeConfig('multiSelect', {
                    value : oAttr.multiSelect || false,
                    validator: lang.isBoolean
               });
               this.setAttributeConfig('minSelect', {
                    value : oAttr.minSelect || 0,
                    validator: lang.isNumber
               });
               this.setAttributeConfig('idValue', {
                    value : oAttr.idValue || 'idValue',
                    validator: lang.isString
               });
               this.setAttributeConfig('showDisabledType', {
                   value : oAttr.showDisabledType || 'disabled',
                   validator: lang.isString
               });
               this.setAttributeConfig('auxiliaryContent', {
                   value : oAttr.auxiliaryContent || '',
                   validator: lang.isString
               });
               this.setAttributeConfig('overflowDiv', {
                   value : oAttr.overflowDiv || '',
                   validator: lang.isString
               });
          },
        
          initEvents : function (oAttr) {
          },
        
          initRows : function(oAttr) {
               this.removeAllDisabledRowStyle();
               //make all rows children of the table
               var tbl = dom.get(this.get("id"));
               if (tbl == null) {
                    return;
               }
               var trs = tbl.getElementsByTagName("tr");
               if (trs == null) {
                    return;
               }
               this._rows = new Array();
               for (var i = 0; i < trs.length; i++) {
                    var trTmp = trs[i];
                    if (typeof trTmp.id == undefined || trTmp.id == null || trTmp.id == "") {
                         //assign an id to those tr without id
                         trTmp.id = this.get("id")+"_tr"+i;
                    }
                    var disableClick = false;
                    if (this.getRowIdValue(trTmp.id) < 0) {
                    	disableClick = true;
                    }
                    var selectedTableRowTmp = new YAHOO.aerohive.widget.SelectedTableRow(trTmp.id,
                              {
                              id : trTmp.id,
                              mouseover : oAttr.rowmouseover,
                             mouseout : oAttr.rowmouseout,
                             beforeselect : oAttr.rowbeforeselect,
                             beforedeselect : oAttr.rowbeforedeselect,
                              afterselected : oAttr.rowafterselected,
                              afterdeselected : oAttr.rowafterdeselected,
                              disabledrowshow : oAttr.disabledrowsshow,
                              showDisabledType : oAttr.showDisabledType,
                              auxiliaryContent : oAttr.auxiliaryContent,
                              disableOnclick : disableClick
                              });
                    selectedTableRowTmp.setParent(this);
                    if (this.isDisabledRow(selectedTableRowTmp.getId())) {
                        selectedTableRowTmp.gotoDisabled();
                   }
                    this._rows.push(selectedTableRowTmp);
               }
          },
          
          startScrollToTr: function(idValue) {
        	  var trTmp = this.getChildRowElement(idValue);
        	  if (trTmp == null) return;
        	  var trXY = dom.getXY(trTmp.getId());
        	  var overflowXY = dom.getXY(this.get("overflowDiv"));
        	  var mvHeight = trXY[1] - overflowXY[1];
        	  if (mvHeight < 0) mvHeight = 0;
        	  var attr = { 
  			  		scroll: {to: [0, mvHeight], speed: 100}
  	  			};
        	  var animator = new YAHOO.util.Scroll(this.get("overflowDiv"), attr);
        	  if (animator != null) {
        		  animator.animate();
        	  }
          },
        
          getSelectedRowsCount : function() {
               var resultCount = 0;
               if (this._rows != null) {
                    for (var i = 0; i < this._rows.length; i++) {
                         var trTmp = this._rows[i];
                         if (trTmp.isDisabled() == false && trTmp.isSelected()) {
                              resultCount++;
                         }
                    }
               }
               return resultCount;
          },
        
          getSelectedRowsResult : function() {
               var results = new Array();
               if (this._rows != null) {
                    for (var i = 0; i < this._rows.length; i++) {
                         var trTmp = this._rows[i];
                         if (trTmp.isDisabled() == false && trTmp.isSelected()) {
                              results.push(trTmp.getRowIdValue());
                         }
                    }
               }
               return results;
          },
        
          removeAllSelectedRows : function() {
               if (this._rows != null) {
                    for (var i = 0; i < this._rows.length; i++) {
                         var trTmp = this._rows[i];
                         if (trTmp.isSelected()) {
                              trTmp.setDeselectRowStyle();
                         }
                    }
               }
          },
        
          removeAllDisabledRowStyle : function() {
              if (this._rows != null) {
                  for (var i = 0; i < this._rows.length; i++) {
                       var trTmp = this._rows[i];
                       if (trTmp.isDisabled()) {
                            trTmp.gotoEnabled();
                       }
                  }
             }
          },
          
          getChildRowElement : function(idValue) {
        	  if (this._rows != null) {
                  for (var i = 0; i < this._rows.length; i++) {
                       var trTmp = this._rows[i];
                       if (trTmp.getRowIdValue() == idValue) {
                            return trTmp;
                       }
                  }
             }
          },
          
          setRowAuxiliaryContentText : function(idValue, text) {
        	  var el = this.getChildRowElement(idValue);
        	  if (typeof el != undefined && el != null) {
        		  el.setAuxiliaryContentText(text);
        	  }
          },
          
          setRowsAuxiliaryContentText : function(toSetArray, text) {
        	  if (toSetArray) {
                  if (lang.isString(toSetArray)) {
                	  	this.setRowAuxiliaryContentText(toSetArray, text);
                  } else if (lang.isArray(toSetArray)) {
                        for (var i = 0; i < toSetArray.length; i++) {
                        	this.setRowAuxiliaryContentText(toSetArray[i], text);
                        }
                  }
            }
          },
         
          disableARow : function(idValue) {
              if (this._rows != null) {
                  for (var i = 0; i < this._rows.length; i++) {
                       var trTmp = this._rows[i];
                       if (trTmp.getRowIdValue() == idValue) {
                            trTmp.gotoDisabled();
                       }
                  }
             }
          },
         
          disableRows : function(toDisableArray) {
                  if (toDisableArray) {
                        this.removeAllDisabledRowStyle();
                        if (lang.isString(toDisableArray)) {
                              this.disableARow(toDisableArray);
                        } else if (lang.isArray(toDisableArray)) {
                              for (var i = 0; i < toDisableArray.length; i++) {
                                    this.disableARow(toDisableArray[i]);
                              }
                        }
                  }
            },
            
            disableARowWithAuxiliary : function(idValue, text) {
            		var el = this.getChildRowElement(idValue);
          	  		if (typeof el != undefined && el != null) {
          	  			el.gotoDisabled();
          	  			el.setAuxiliaryContentText(text);
          	  		}
            },
            
            disableRowsWithAuxiliary : function(toDisableArray, text) {
            	if(this.get("auxiliaryContent") == null || this.get("auxiliaryContent") == '') {
            		return;
            	}
                    if (toDisableArray) {
                          this.removeAllDisabledRowStyle();
                          if (lang.isString(toDisableArray)) {
                                this.disableARowWithAuxiliary(toDisableArray, text);
                          } else if (lang.isArray(toDisableArray)) {
                                for (var i = 0; i < toDisableArray.length; i++) {
                                      this.disableARowWithAuxiliary(toDisableArray[i], text);
                                }
                          }
                    }
              },
              
              disableRowsWithAuxiliaryWithPrevious : function(toDisableArray, text) {
              	if(this.get("auxiliaryContent") == null || this.get("auxiliaryContent") == '') {
              		return;
              	}
                      if (toDisableArray) {
                            if (lang.isString(toDisableArray)) {
                                  this.disableARowWithAuxiliary(toDisableArray, text);
                            } else if (lang.isArray(toDisableArray)) {
                                  for (var i = 0; i < toDisableArray.length; i++) {
                                        this.disableARowWithAuxiliary(toDisableArray[i], text);
                                  }
                            }
                      }
                },
            
            enableARow : function(idValue) {
              if (this._rows != null) {
                for (var i = 0; i < this._rows.length; i++) {
                     var trTmp = this._rows[i];
                     if (trTmp.getRowIdValue() == idValue) {
                          trTmp.gotoEnabled();
                     }
                }
           }
        },
       
        enableRows : function(toEnableArray) {
            if (toEnableArray) {
                  if (lang.isString(toEnableArray)) {
                        this.enableARow(toEnableArray);
                  } else if (lang.isArray(toEnableArray)) {
                        for (var i = 0; i < toEnableArray.length; i++) {
                              this.enableARow(toEnableArray[i]);
                        }
                  }
            }
      },
        
          isDisabledRow : function(el) {
               if(this._disabledRows == null) {
                    return false;
               }
               for (var i = 0; i < this._disabledRows.length; i++) {
                    if (this._disabledRows[i] == this.getRowIdValue(el)) {
                         return true;
                    }
               }
               return false;
          },
        
          getRowIdValue : function(el) {
               var idValue = this.getIdValueElName();
               if (idValue == null || idValue == "") {
                    return -1;
               }
               var tr = document.getElementById(el);
               if (tr != null) {
                    var tds = tr.getElementsByTagName("td");
                    if (tds == undefined || tds == null || tds.length < 1) {
                         return -1;
                    }
                    for (var i = 0; i < tds.length; i++) {
                         var finds = tds[i].getElementsByTagName("input");
                         if (finds != undefined && finds != null && finds.length > 0) {
                              for (var j = 0; j < finds.length; j++) {
                                   if (finds[j].name == idValue && (finds[j].type == "hidden" || finds[j].type.toLowerCase() == "checkbox")) {
                                        return finds[j].value;
                                   }
                          }
                     }
                    }
               }
               return -1;
          },
          
          prepareCheckItems : function() {
        	  if (this._rows != null) {
                  for (var i = 0; i < this._rows.length; i++) {
                       var trTmp = this._rows[i];
                       var trEl = document.getElementById(trTmp.getId());
                       var checkMark = false;
                       if (trTmp.isDisabled() == false && trTmp.isSelected()) {
                    	   checkMark = true;
                       }
                       var tds = trEl.getElementsByTagName("td");
         			   if (tds == undefined || tds == null || tds.length < 1) {
         				  continue;
         			   }
         			   for (var j = 0; j < tds.length; j++) {
                           var finds = tds[j].getElementsByTagName("input");
                           if (finds != undefined && finds != null && finds.length > 0) {
                                for (var k = 0; k < finds.length; k++) {
                                     if (finds[k].type.toLowerCase() == "checkbox") {
                                    	 finds[k].checked = checkMark;
                                     }
                            }
                        }
         			 }
                  }
             }
          },
        
          getRows : function() {
               return this._rows;
          },
          setRows : function(rows) {
               this._rows = rows;
          },
        
          getDisabledRows : function() {
               return this._disabledRows;
          },
          setDisabledRows : function(disabledRows) {
               this._disabledRows = disabledRows;
          },
        
          isMultipleSelect : function() {
               return this.get("multiSelect");
          },
        
          getMinSelect : function() {
               return this.get("minSelect");
          },
        
          getIdValueElName : function() {
               return this.get("idValue");
          }
     });
}
)();

YAHOO.register("selectedtable", YAHOO.aerohive.widget.SelectedTable, {version: "1.0", build: "1"});