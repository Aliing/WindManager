/*!
 * This widget is created for the Classifier Tag feature
 * 
 * Dependencies:
 * 	UI Core, 
 *  UI Autocomplete,
 *  UI Sortable
 *  hm.util
 *  
 * Revision 1.14.2.9.4.1.4.3 2013/8/15 13:50:50 wpliang
 * Bug 28434 fix
 *  
 * $Author: wpliang $
 * $Date: 2014/04/01 11:15:13 $
 * $Revision: 1.14.2.9.4.1.4.3.18.1 $
 * $Log: ct-debug.js,v $
 * Revision 1.14.2.9.4.1.4.3.18.1  2014/04/01 11:15:13  wpliang
 * fix bug 31532
 *
 * Revision 1.14.2.9.4.1.4.3  2013/08/15 05:54:17  wpliang
 * fix bug 28434
 *
 * Revision 1.14.2.9.4.1.4.2  2013/07/04 07:20:52  nfang
 * fix bug 27502,27049,27582,27605, 27472
 *
 * Revision 1.14.2.9.4.1.4.1  2013/06/20 08:32:00  ljdai
 * Bug 25477 - <Classifier tag support on Network policy>The Topology node name will be shown after new a order with topology node but not apply.
 *
 * Revision 1.14.2.9.4.1  2013/05/14 07:01:10  she
 * Fix bug 25789 and 24936
 *
 * Revision 1.14.2.9  2013/03/08 05:09:08  yyye
 * change because of bug 24330  fix
 *
 * Revision 1.14.2.8  2013/02/28 07:23:05  yyye
 * change because of bug 24330  fix
 *
 * Revision 1.14.2.7  2013/02/28 03:43:30  ljdai
 * Bug 24644 - <Classifier tag support on Network policy>Add a title in the matching device dialog, e.g., Matching Devices for Tag1=Small
 *
 * Revision 1.14.2.6  2013/01/27 08:18:15  ljdai
 * bug fix
 *
 * Revision 1.14.2.5  2013/01/22 09:14:54  ljdai
 *  Non-default Device Template Configuration(Classifier Tag)
 *
 * Revision 1.14.2.4  2013/01/11 06:46:21  yyye
 * sync with R1 release
 *
 * Revision 1.7.2.18  2013/01/06 10:42:24  yyye
 * change because of bug fix
 *
 * Revision 1.7.2.17  2013/01/05 11:03:06  yyye
 * change because of bug fix
 *
 * Revision 1.7.2.16  2013/01/04 07:18:00  yyye
 * change because of bug fix
 *
 * Revision 1.7.2.15  2012/12/20 08:45:00  yyye
 * sync with FANG
 *
 * Revision 1.7.2.14  2012/11/28 10:49:51  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.13  2012/11/25 07:58:51  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.12  2012/11/23 10:30:55  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.11  2012/11/22 14:33:03  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.10  2012/11/21 03:47:07  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.9  2012/11/19 10:34:00  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.8  2012/11/19 09:42:04  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.7  2012/11/19 06:58:01  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.6  2012/11/16 11:40:15  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.5  2012/11/14 08:33:04  yyye
 * remove debug output
 *
 * Revision 1.7.2.4  2012/11/14 08:27:05  yyye
 * change because of Classifier Tag Bug fix
 *
 * Revision 1.7.2.3  2012/11/08 06:56:57  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.7.2.2  2012/11/07 10:33:37  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.7.2.1  2012/11/07 03:46:15  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.9  2012/11/06 07:37:07  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.8  2012/10/17 06:01:36  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.7  2012/09/24 03:10:50  yyye
 * change because of Classifier Tag Enhancements
 *
 * Revision 1.6  2012/09/13 10:00:52  yyye
 * update for device tag custom
 *
 * Revision 1.5  2012/09/13 09:03:41  yyye
 * update for device tag custom
 *
 * Revision 1.4  2012/09/12 08:07:17  yyye
 * update for device tag custom
 *
 * Revision 1.3  2012/09/12 04:51:54  ylin
 * Handle the options for 'types' field.
 *
 * Revision 1.2  2012/09/10 07:22:04  ylin
 * Fixed js error.
 *
 * Revision 1.1  2012/09/04 09:06:09  ylin
 * Classifier Tag UI - initial.
 *
 */
;(function ($, window, document, undefined) {
	var DEF_CLASSES = {
			ui: 'ui-classifier',
			td: 'listHead',
			button: 'selectbutton',
			selectedStatus: 'selectmenu-status',
			arrowIcon: 'selectmenu-icon ui-icon ui-icon-triangle-1-s',
			menuContainer: 'selectmenu-container',
			menu: 'selectmenu',
			menuHover: 'hover',
			tagContainer: 'defaultContainer',
			tagsNoneA: 'selectbutton none',
			tagsNoneS: 'none',
			tagItem: 'tag-choice',
			tagItemText: 'tag-choice-text',
			tagItemCancel: 'cancel-icon ui-icon ui-icon-close',
			tagComboToggle: 'tag-combo-toggle',
			tags: 'tagsContainer',
			devices: 'deviceContainer',
			topology: 'topoContainer',
			desc: 'desc',
			uiTable: 'ui-classifier-items',
			uiItemContainer: 'itemContainer',
			uiItem: 'item',
			uiItemDisable: 'item-state-disable',
			checkBox: 'checkable',
			checkBoxDisable: 'check-state-disable',
			textPointer: 'pointer',
			itemHolder: 'ui-state-highlight',
			operationIcon: 'optmenu',
			operationMenuContainer: 'optmenu-container',
			mask: 'itemsMask'
		},
		DEF_VALUE_PROPS = {
			flag: null,
			validateFn: function(){return true},
			desc: null,
			items: {
						// necessary widget attributes
			        	id: 'classifierTagId',
			        	field: 'vlanId',
			        	elType: 'input',
			        	// basic HTML attributes
			        	onkeypress: null,
			        	maxlength: 12,
			        	size: false
			        }
		},
		DEF_ID_PREFIX = {
			tagNone: 'li_tag_none',
			tagInput: 'li_tag_input',
			tagItem: 'li_tag_choice',
			deviceInput: 'li_device_input',
			deviceItem: 'li_device_choice',
			topoInput: 'li_topo_input',
			topoItem: 'li_topo_choice'
		},
		DEF_EMTPTY_FUNCTION = function(){},
		DEF_OPER_ACTION = [
		                   {text : 'Matching Devices',display:true, fn: DEF_EMTPTY_FUNCTION},
		                   {text : 'View Conflicts',display:true, fn: DEF_EMTPTY_FUNCTION},
		                   {text : 'Edit',display:true, fn: DEF_EMTPTY_FUNCTION},
		                   {text : 'Remove', display:true,fn: DEF_EMTPTY_FUNCTION}
		                   ],
		DEF_TYPES = [{key: 4, text: 'Device Tags'}, {key: 3, text: 'Device Name'}, {key: 2, text: 'Topology Node'}],
		DEF_TYPE_KEYS = [4, 3, 2, 1],
		DEF_GLOBAL_TYPE = {key: 1, text: 'Global'},
		DEF_GLOBAL_PROPS = {id: 'globalValueId'},
		DEF_ITEM_WIDTHS = {value: false, type: 100, tagValue: 240, desc: 100},
		DEF_CHOOSEN_TEXT = 'Choose...',
		DEF_EMPTY_ANCHOR_HREF = 'javascript: void(0)',
		DEF_SUGGESTION_WILDCARD = '*',
		DEF_SEPARATOR_SYMBOL = '|';
	
	var debug = function(msg) {
		if(window.console && console.debug) {
			if(typeof msg == 'string') {
				//console.debug(msg);
			} else {
				//console.dir(msg);
			}
		}
	},
	uniqueArray = function(array) {
		var hash = new Object();
		for(var i=0, j=0; i < array.length; i++) {
			if(hash[array[i]] == undefined) {
				hash[array[i]] = j++;
			}
		}
		var newArray = new Array();
		for(var value in hash) {
			if(value!='')
			newArray.push(value);
		}
		return newArray;
	};
	
	$.widget("aerohive.classifierTag", {
		//TODO core of the widget
		options: {
			key: 1,
			types: DEF_TYPES, // [{key: 4, text: 'Device Tags'}, null, null]
			valueProps: DEF_VALUE_PROPS,
			widgetWidth: {desc: false},
			cIds: {
				Tag : ['tag1Id', 'tag2Id', 'tag3Id'],
				Device: 'deviceId',
				Topo: 'locationId'
			},
			cNames: {
				Tag: ['Tag1', 'Tag2', 'Tag3'],
				Device: 'Device Name',
				Topo: 'Topology Node'
			},
			deviceTagInitValue: {
				Tag: ['None', 'None', 'None']
			},
			initDeviceTags: true,//initialize the device tags
			initDeviceNames: true,//initialize the device names
			initTopologyMaps: true,//initialize the topology maps
			describable: true,//whether the description field exists
			descriptionId: 'descId',
			deviceNames: [],//device name list
			topologyMaps: [],//Map hierarchy list
			// for items options
			checkable: 'ruleIndices',
			zindexDisplay: false,
			zindexDisplayAtTop: false,
			needGlobalItem: true,
			globalProps: DEF_GLOBAL_PROPS,
			itemTableId: false,
			itemEditable: {value: true, valueName: 'vlanNames', desc: true, descName: 'descNames'},
			itemWidths: DEF_ITEM_WIDTHS,
			itemOperation: DEF_OPER_ACTION,
			valueItemSpan: false,
			selectItemName: null,
			selectItemDefine: null,
			onlyDisplayValue: null,
			needShowTagFields : false,
			needShowDeviceNames : false,
			needShowTopology : false,
			needNoChange: false,
			renderDiv:null,
			errorEl: {id: 'checkAll'},
			inputTagDone:{funcName:'',argument:''}
		},
		
		_setOption: function(key, value) {
			debug("setOption...");
			var self = this;
			switch(key) {
				case 'types':
					break;
				case 'deviceNames':
					self.updateDeviceNames(value);
					break;
				case 'topologyMaps':
					self.updateTopology(value);
					break;
				default :
					break;
					
			}
			$.Widget.prototype._setOption.apply(self, arguments);
		},
		
		_setOptions: function(options) { // invoke when set the 'option'
			debug("setOptions...");
			var self = this;
			$.each(options, function(key, value) {
				self._setOption(key, value);
			});
		},
		
		_create: function() {
			// create HTML and bind event
			var self = this,
				el = self.element,
				opt = self.options;
			debug("create...");
			//debug(opt);
			
			// hide the section as default
			self.hide();
			
			// if the selected element is a row then add the cell into it
			if(el.is("tr")) {
				debug("It is a row in a table.");
				
				el.addClass(DEF_CLASSES.ui);
				
				if(opt.checkable) {
					// add the empty space for check box
					$('<td/>').addClass(DEF_CLASSES.td).width(10).html('&nbsp;').appendTo(el);
				}
				
				// set the attribute for the value input
				// TODO need to support more flexible for two/three value item
				if(opt.valueProps) {
					if($.isArray(opt.valueProps)) {
						// multi-value group
						for(var index=0; index<opt.valueProps.length; index++) {
							var valueProp = opt.valueProps[index];
							var vItems = valueProp.items;
							var row ;
							if(opt.valueItemSpan){row = $('<td/>').addClass(DEF_CLASSES.td).attr('valign', 'top').attr('id', valueProp.flag.name).attr('colspan',2);}
							else{ row = $('<td/>').addClass(DEF_CLASSES.td).attr('valign', 'top').attr('id', valueProp.flag.name);}
							if($.isArray(vItems)) {
								for(var j=0; j<vItems.length; j++) {
									var valueEl = self._buildValueEl(vItems[j]);
									row.append(valueEl);
									
									if(j<vItems.length-1) {
										var conStr = valueProp.concatenateHTML ? valueProp.concatenateHTML : '';
										row.append(conStr);
									} else if(j==vItems.length-1) {
										var descStr = valueProp.desc ? valueProp.desc : '';
										row.append(descStr);
									}
								}
							}
							if(valueProp.selected) {
								self._valueFlagKey = valueProp.flag.type;
							} else {
								row.hide();
							}
							row.appendTo(el);
							debug("add multi-value group, sucess index: " +index);
						}
					} else {
						var vItems = opt.valueProps.items;
						if($.isArray(vItems)) {
							// multi-value 
							var row = $('<td/>').addClass(DEF_CLASSES.td).attr('valign', 'top');
							for(var j=0; j<vItems.length; j++) {
								var valueEl = self._buildValueEl(vItems[j]);
								row.append(valueEl);
								
								if(j>0 && j<vItems.length-1) {
									var conStr = valueProp.concatenateHTML ? valueProp.concatenateHTML : '';
									row.append(conStr);
								} else if(j==vItems.length-1) {
									var descStr = valueProp.desc ? valueProp.desc : '';
									row.append(descStr);
								}
								row.appendTo(el);
							}
						} else {
							// for single value
							var valueEl = self._buildValueEl(vItems);
							var descStr = opt.valueProps.desc ? opt.valueProps.desc : '';
							$('<td/>').addClass(DEF_CLASSES.td).attr('valign', "top").append(valueEl).append(descStr).appendTo(el);
						}
					}
				}
				
				// initialize the choose item
				opt.types = $.grep(opt.types, function(type) {
					// filter the null value
					if(type) return true;
				});
				$.extend(self._typeArray, []);
				//yyye add to avoid reproduce item in choose item.
				self._typeArray=[];				
				if(opt.types) {
					if($.isArray(opt.types)) {
						$.each(opt.types, function(index, val) {
							if($.inArray(val.key, DEF_TYPE_KEYS) >= 0) {								
								self._typeArray.push(val);								
							}
						});
					}
				} else {
					$.extend(self._typeArray, DEF_TYPES);
				}
				if(self._typeArray.length == 0) {
					self.destroy();
				} else if(self._typeArray.length == 1) {												
					// only one item exists, no need to create the choose item,this is just for device tag use now.
				} else {
					if(opt.id === undefined) {
						opt.id = el.attr('id') + "_selectMenu";
					}
					self._chooseEl = $('<a>').attr('id', opt.id).attr('href', DEF_EMPTY_ANCHOR_HREF)
									.addClass(DEF_CLASSES.button)
									.append('<span class="'+DEF_CLASSES.selectedStatus+'">' + DEF_CHOOSEN_TEXT + '</span>')
									.append('<span class="'+DEF_CLASSES.arrowIcon+'"/>');
					$('<td/>').addClass(DEF_CLASSES.td).attr('valign', "top").width(100).append(self._chooseEl).appendTo(el).attr('id','chooseCtner');;
					
					self._chooseEl.click(function(event) {
						event.preventDefault();
						var menu = $(this).parent().find('span ul.'+DEF_CLASSES.menuContainer);
						if(menu.is(':hidden')) {
							menu.show();
						} else {
							menu.hide();
						}
					});
					
					var ulEl;
					$.each(self._typeArray, function(index, val) {
						if(index == 0) {
							ulEl = $('<ul>').addClass(DEF_CLASSES.menuContainer);
						}
						ulEl.append('<li><span>' + val.text + '</span></li>');
					});
					$('<span class="'+DEF_CLASSES.menu+'"></span>').append(ulEl).insertAfter(self._chooseEl);
					
					// initialize the hover/click event on <ul> and <li>
					$('ul.'+DEF_CLASSES.menuContainer+' li').hover(
						function() { // mouse in
							$(this).addClass(DEF_CLASSES.menuHover);
							$(this).parent().hover(function(){}, function() { // mouse out
								var menu = $(this).parent().find('ul.'+DEF_CLASSES.menuContainer);
								if(menu && menu.not(':hidden')) {
									menu.hide();
								}
							});
						},
						function() { // mouse out
							$(this).removeClass(DEF_CLASSES.menuHover);
						}
					).click(function(event) {
						event.preventDefault();
						self._selectType($(this).find('span').html());
						// hide menu after click
						var menu = $(this).parent();
						if(menu && menu.not(':hidden')) {
							menu.hide();
						}
					});
					
					// add table section
					self._tableWidget = $('<td>').addClass(DEF_CLASSES.uiTable).attr('colspan', 10);
					self._sortedWidget = $('<ul/>')
									.addClass(DEF_CLASSES.uiItemContainer)
									.appendTo(self._tableWidget);
					self._sortedWidget.sortable({
						placeholder: DEF_CLASSES.itemHolder,
						items: "li:not(." + DEF_CLASSES.uiItemDisable +")",
						start: function(event, ui) {
							debug("sortableImte start...");
							debug("|__ index: "+ui.item.index());
							
							$(this).attr('prevIndex', ui.item.index());
						},
						update: function(event, ui) {
							debug("sortableImte update...");
							debug("|__ index: "+ui.item.index()+" , old index: " + $(this).attr('prevIndex'));
							
							self._displayMask($(this), true); // show mask
							// set new order to server
							var timeFlag = {
									ignore: new Date().getTime()
									};
							$.getJSON(self._url,
									{
										operation: 'changeOrder',
										tagKey: opt.key,
										prevIndex: $(this).attr('prevIndex'),
										curIndex: ui.item.index(),
										timeFlagValue:timeFlag
									},
									function(data) {
										debug("Get response from server side");
										debug(data);
										self._displayMask(self._sortedWidget, false);
									})
									.error(function() {
										debug("Error occurs when change order");
										self._displayMask(self._sortedWidget, false);
									});
						}
					}).disableSelection();
					// mask
					var mask = $('<div/>').addClass(DEF_CLASSES.mask);
					$('<div/>').css('position', 'relative').append(mask).appendTo(self._tableWidget);
					
					if(opt.itemTableId) {
						var row = $('tr#'+opt.itemTableId);
						if(row.length) {
							row.append(self._tableWidget);
						}
					} else {
						$('<tr>').append(self._tableWidget).insertAfter(el);
					}
				}
			}
		},
		
		_init: function() {
			var self = this,
				opt = self.options;
			debug("init...");
			if(opt.itemOperation) {
				$.each(opt.itemOperation, function(key, value) {
				if(value!=null){
					if(value.text == 'Remove') {
						value.fn = function() {self.removeItems(false, $(this))};
					} else if(value.text == 'Edit') {
						value.fn = function() {self.editItems(false, $(this))};
					}
					else if(value.text == 'View Conflicts') {
						value.fn = function() {self.viewItems(false, $(this))};
					}
					else if(value.text == 'Matching Devices') {
						value.fn = function() {self.matchItems(false, $(this))};
					}
				}
				});
			}
			if(opt.initTopologyMaps) {
				self.initTopologyMaps();
				$(".ui-autocomplete").hide();
			}
			if(opt.initDeviceTags) {
				self.initDeviceTags();
			}
			if(opt.initDeviceNames) {
				self.initDeviceNames();
			}
		},
		
		destroy: function() {
			//TODO
			debug("destroy...");
			$.Widget.prototype.destroy.call(this);
		},
		
		show: function() {
			debug("show...");
			var self = this,
				el = self.element;
			if(el.is(':hidden')) {
				el.show();
			}
		},
		
		hide: function() {
			debug("hide...");
			var self = this,
				el = self.element;
			if(el.not(':hidden')) {
				el.hide();
			}
		},
		
		_selectType: function(selText) {
			var self = this;
				el = self.element,
				opt = self.options;
			debug(selText);
			$.each(opt.types, function(index, val) {
				if(val.text === selText) {
					// change the choose item text
					self._chooseEl.find('span.' + DEF_CLASSES.selectedStatus).text(selText);
					
					self._resetTagValueByType(self._currentSel);
					
					self._currentSel = val.key;
					switch(val.key) {
						case 4: 
							self._hideDeviceNames();
							self._hideTopology();
							
							self._showTagFields();
							break;
						case 3:
							self._hideTagFields();
							self._hideTopology();
							
							self._showDeviceNames();
							break;
						case 2:
							self._hideDeviceNames();
							self._hideTagFields();
							
							self._showTopology();
							break;
						default:
							self._hideDeviceNames();
							self._hideTagFields();
							self._hideTopology();
							break;
					}
				}
			});
			if(opt.describable) {
				// description field
				var descCell = el.find('td.' + DEF_CLASSES.desc);
				if(descCell.length == 0) {
					// create first time
					var descFields = $('<td/>')
									.addClass(DEF_CLASSES.td)
									.addClass(DEF_CLASSES.desc).attr('valign', "top").attr('id','descCtner');;
					if(opt.widgetWidth && opt.widgetWidth.desc) {
						descFields.width(opt.widgetWidth.desc);
					} 
					$('<input>')
						.attr('id', opt.descriptionId)
						.attr('type', 'text')
						.attr('maxLength', 64)
						.attr('size', 24)
						.appendTo(descFields);
					descFields.append('<br>(0-64 characters)').appendTo(el);
				} else {
					descCell.appendTo(el);
					if(descCell.is(':hidden')) {
						descCell.show();
					}
				}
			}
		},
		
		/** Tag1/Tag2/Tag3 section */
		_showTagFields: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			var zDisplay=opt.zindexDisplay;
			var zDisplayTop=opt.zindexDisplayAtTop;
			debug("_showTagFields...");
			var existCell = el.find('td.' + DEF_CLASSES.tags);
			if(existCell.length == 0) {
				// create first time
				var tagFields = $('<td/>')
								.addClass(DEF_CLASSES.td)
								.addClass(DEF_CLASSES.tagContainer)
								.addClass(DEF_CLASSES.tags).attr('valign', "top").attr('id','tagCtner');
				if(opt.cIds.Tag.length == 3 &&  opt.cIds.Tag.length == opt.cNames.Tag.length) {
					$.each(opt.cIds.Tag, function(index, value) {
						var suffix = '_' + index;
						var tempIndex=parseInt(index)+1;
						var ulEl = $('<ul/>')
						.append('<li id="' + DEF_ID_PREFIX.tagNone + suffix + '"><a class="' + DEF_CLASSES.tagsNoneA + '" href="'+ DEF_EMPTY_ANCHOR_HREF +'"><span class="' + DEF_CLASSES.tagsNoneS + '">None</span></a></li>')
						.append('<li id="' + DEF_ID_PREFIX.tagInput + suffix + '"><input id="' + opt.cIds.Tag[index] + '" name="dataSource.classificationTag'+tempIndex+'" type="text" maxlength="64" ></li>')
						.append('<li class="' + DEF_CLASSES.tagItem + '" id="' + DEF_ID_PREFIX.tagItem + suffix + '"><a href="' + DEF_EMPTY_ANCHOR_HREF + '" class="' +DEF_CLASSES.tagItemText+ '"><span></span></a><a href="'+DEF_EMPTY_ANCHOR_HREF+'" class="' +DEF_CLASSES.tagItemCancel+ '"></a></li>');
						
						$('<div/>').append('<label for="' +opt.cIds.Tag[index] + '">' + opt.cNames.Tag[index] + ':&nbsp;</label>')
						.append(ulEl)
						.appendTo(tagFields);
						
						var preValue;
						$.each(ulEl.find('li'), function(index, value) {
							var timer; // use for return to 'None' status
							if(index == 0) {
								// None status
								self._initNoneItem($(this));
							} else if (index == 1) {
								// Input status
								var tagIndex = $(this).attr('id').replace(DEF_ID_PREFIX.tagInput + '_', '');
								var inputEl = $(this).find('input');								
								// auto complete
								$(this).find('input')
								.bind('keydown', function(event) {
									if(event.which == 13) {
										// key press 'Enter'
										event.preventDefault();
										var target = $(event.target);
										var value = target.val();
										if($.trim(value).length == 0) {
											$(this).parent().hide();
											$(this).parent().prev().show();
											return;
										}
										if($.inArray(value, $('input#'+target.attr('id')).autocomplete('option', 'source')) == -1) {
											// no exist
											
										} else {
											$(this).parent().hide();
											$(this).parent().next().find('a.' +DEF_CLASSES.tagItemText+ ' span').text(value);
											$(this).parent().next().show();
										}
										$(this).autocomplete('close');
									}
								})
								.bind('focusin', function() {
									preValue = $(this).val();
									$(this).autocomplete('close');
									if(timer) {
										clearTimeout(timer);
										timer = null;
										debug('trigger clearTimeout');
									}
									if(zDisplay)
										inputEl.autocomplete('widget').css('z-index', 5);
									if(zDisplayTop)
										inputEl.autocomplete('widget').css('z-index', 7);
									
									$(this).autocomplete('search', ''); // open the suggestion
								})
								.bind('focusout', function() {
									debug("focus out... fire");
									var value = $(this).val();
									if($.trim(value).length == 0) {
										timer = setTimeout(function($el) {
											return function() {
												debug('trigger setTimeout');
												if($.trim($el.val()).length == 0) {
													$el.parent().hide();
													$el.parent().prev().show();
												}
												timer = null;
											}
										}($(this)), 2*1000);
										if(preValue != value){
											self._inputTagComplate(el,opt)
										}
									} else {
										if (value.indexOf("'") > -1) {
											$(this).val("");
											alert("Device tag cannot contain '");
											return;
										}
										if (value.indexOf('"') > -1) {
											$(this).val("");
											alert('Device tag cannot contain "');
											return;
										}
										if($(this).parent().not(':hidden')) {
											if($.inArray(value, $(this).autocomplete('option', 'source')) == -1) {
												// dump, it is a new value
												debug("dump, it is a new value");
											} else {												
												var display=value;
												if($.trim(value).length > 10){
													display=value.substring(0, 10)+'...'; 
												}												
												$(this).parent().hide();
												$(this).parent().next().find('a.' +DEF_CLASSES.tagItemText+ ' span').text(display);
												$(this).parent().next().show();
												$(this).autocomplete('true'); // Changing because of fixing bug 25789
											}
										}
										if(preValue != value){
											self._inputTagComplate(el,opt)
										}
									}
								})
								.autocomplete({
									minLength: 0,
									source: self._tagValues[tagIndex],
									select: function (event, ui) {
										debug("select... fire");
										debug(ui.item ? "Selected: " + ui.item.value + ", id=" + ui.item.id : "Nothing selected, input is: " + this.value);
										// when use the mouse to manual select item, the 'focus out' event will fire before the input value is set.
										self._showTagItem(this, ui.item.value);
										if(preValue != ui.item.value){
											self._inputTagComplate(el,opt)
										}
									}
								});
								if(opt.renderDiv){
									inputEl.autocomplete( "option", "appendTo", "#"+opt.renderDiv);
								}
								if(opt.needShowTopology){
									inputEl.autocomplete( "option", "appendTo", "#classfierEditPanelAPanel_c" );
								}
							} else {
								// Item status
								self._initTagItem($(this),opt);
							}							
						});
					});
					tagFields.appendTo(el);					
					//Dispaly device tag via it's default Database value
					if(opt.deviceTagInitValue.Tag[0]!='None')self._showTagItem('input#'+opt.cIds.Tag[0], opt.deviceTagInitValue.Tag[0]);					
					if(opt.deviceTagInitValue.Tag[1]!='None')self._showTagItem('input#'+opt.cIds.Tag[1], opt.deviceTagInitValue.Tag[1]);
					if(opt.deviceTagInitValue.Tag[2]!='None')self._showTagItem('input#'+opt.cIds.Tag[2], opt.deviceTagInitValue.Tag[2]);
					
				} else {
					debug("Some error happened.");
				}
			} else {
				if(existCell.is(':hidden')) {
					existCell.show();
				}
				//TODO set the fields for 'Edit'
			}
		},
		_hideTagFields: function() {
			var self = this,
				el = self.element;
			debug("_hideTagFields...");
			var existCell = el.find('td.' + DEF_CLASSES.tags);
			if(existCell.length > 0) {
				existCell.hide();
			}
			// close the autocomplete
			$.each(opt.cIds.Tag, function(index, value) {
				var iEl = existCell.find('input#'+opt.cIds.Tag[index]);
				if(iEl.length > 0) {
					iEl.autocomplete('close');
				}
			});
			//TODO reset fields
		},
		_showTagItem: function(inputEl, value) {
			debug("_showTagItem..., value=" + $(inputEl+":first").val());
			if($(inputEl).size()==1){
				if($(inputEl).parent().not(':hidden')) {
					$(inputEl).parent().hide();
					if($.trim(value).length == 0) {
						$(inputEl).parent().prev().show();
						$(inputEl).parent().next().find("span").html('');
						$(inputEl).parent().next().hide();
						$(inputEl).val('');
					} else {
						var display=value;
						if($.trim(value).length > 10){
							display=value.substring(0, 10)+'...'; 
						}
						$(inputEl).parent().prev().hide();
						$(inputEl).parent().next().find('a.' +DEF_CLASSES.tagItemText+ ' span').text(display);
						$(inputEl).parent().next().show();
						$(inputEl).val(value);
						debug("after update, value="+$(inputEl).val());
					}
				}
			}
			else{
			if($(inputEl+":first").parent().not(':hidden')) {
				$(inputEl+":first").parent().hide();
				if($.trim(value).length == 0) {
					$(inputEl+":first").parent().prev().show();
					$(inputEl+":first").parent().next().find("span").html('');
					$(inputEl+":first").parent().next().hide();
					$(inputEl+":first").val('');
				} else {
					var display=value;
					if($.trim(value).length > 10){
						display=value.substring(0, 10)+'...'; 
					}
					$(inputEl+":first").parent().prev().hide();
					$(inputEl+":first").parent().next().find('a.' +DEF_CLASSES.tagItemText+ ' span').text(display);
					$(inputEl+":first").parent().next().show();
					$(inputEl+":first").val(value);
					debug("after update, value="+$(inputEl+":first").val());
				}
			}
			}
		},
		updateTagValues: function(source, index) { // ['tagvalue1', 'tagevalue2']
			var self = this,
				el = self.element,
				opt = self.options;
			if($.isArray(source)) {
				// merge, unique
				$.extend(self._tagValues[index], uniqueArray($.merge(source,self._tagValues[index])));
				// sort the array (alpha order)				
				self._tagValues[index].sort(function (a, b) {
    				return a.toLowerCase().localeCompare(b.toLowerCase());
				});	
				debug('after merge, source='+self._tagValues[index]);
				if(opt.needNoChange){
					self._tagValues[index]= $.merge(["[-No Change-]"],self._tagValues[index]);
				}
				if($('input#'+opt.cIds.Tag[index]).length > 0) {
					$('input#'+opt.cIds.Tag[index]).autocomplete('option', 'source', self._tagValues[index]);
				}
			}
		},
		_buildValueEl: function(itemProp, filterAttrs) {
			var valueEl = itemProp.elType && itemProp.elType == 'select' ? 
					$('<select/>') : $('<input type="text">');
			for(key in itemProp) {
				if (key === 'field' 
					|| key === 'validateFn'
					|| key === 'elType'
					|| key === 'desc') {
					// dump
					continue;
				}
				if(filterAttrs && $.inArray(key, filterAttrs) >= 0) {
					continue;
				}
				if(itemProp[key]) {
					valueEl.attr(key, itemProp[key]);
				}
			}
			return valueEl;
		},
		
		/** DeviceNames section */
		_showDeviceNames: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			var zDisplay=opt.zindexDisplay;
			var zDisplayTop=opt.zindexDisplayAtTop;	
			debug("_showDeviceNames...");
			var existCell = el.find('td.' + DEF_CLASSES.devices);
			if(existCell.length == 0) {
				var deviceField = $('<td/>')
								.addClass(DEF_CLASSES.td)
								.addClass(DEF_CLASSES.tagContainer)
								.addClass(DEF_CLASSES.devices).attr('valign', "top").attr('id','deviceCtner');
				
				var ulEl = $('<ul/>')
				.append('<li><a class="' + DEF_CLASSES.tagsNoneA + '" href="'+ DEF_EMPTY_ANCHOR_HREF +'"><span class="' + DEF_CLASSES.tagsNoneS + '">None</span></a></li>')
				.append('<li id="' + DEF_ID_PREFIX.deviceInput + '"><input id="' + opt.cIds.Device + '" maxlength="32" '+'onkeypress="return hm.util.keyPressPermit(event,'+"'name');"+'" type="text"><a href="' + DEF_EMPTY_ANCHOR_HREF + '" class="' + DEF_CLASSES.tagComboToggle+ '"><span/></a></li>')
				.append('<li class="' + DEF_CLASSES.tagItem + '" id="' + DEF_ID_PREFIX.deviceItem + '"><a href="' + DEF_EMPTY_ANCHOR_HREF + '" class="' +DEF_CLASSES.tagItemText+ '"><span></span></a><a href="'+DEF_EMPTY_ANCHOR_HREF+'" class="' +DEF_CLASSES.tagItemCancel+ '"></a></li>');
				
				$('<div/>').append(ulEl).appendTo(deviceField);
				
				$.each(ulEl.find('li'), function(index, value) {
					var timer;
					if(index ==0) {
						// None status
						self._initNoneItem($(this));
					}else if(index == 1) {
						// Input status with auto complete
						var inputEl = $(this).find('input')
						.bind('keydown', function(event) {
							if(event.which == 13) {
								debug("_showDeviceNames, enter press...");
								// key press 'Enter'
								event.preventDefault();
								var target = $(event.target);
								var value = target.val();
								if($.trim(value).length == 0) {
									debug("_showDeviceNames, do nothing for empty input.");
									return;
								}								

								// fire the focusout
								if(opt.describable && opt.descriptionId) {
									var desc = el.find('input#'+opt.descriptionId);
									desc.focus();
								} else {
									$(this).focusout();
								}
							}
						})						
						.bind('focusin', function() {
							if(timer) {
								clearTimeout(timer);
								timer = null;
								debug('trigger clearTimeout');
							}
							if(zDisplay)
								inputEl.autocomplete('widget').css('z-index', 5);
							if(zDisplayTop)
								inputEl.autocomplete('widget').css('z-index', 7);
						})
						.bind('focusout', function() {
							var value = $(this).val();
							var length = $.trim(value).length;
							debug("focusout... fire, value="+value);
							if(length == 0) {
								timer = setTimeout(function($el) {
									return function() {
										debug('trigger setTimeout');
										if($.trim($el.val()).length == 0) {
											$el.parent().hide();
											$el.parent().prev().show();
										}
										timer = null;
									}
								}($(this)), 2*1000);
							} else {
								if (value.indexOf("'") > -1) {
									alert("Device name cannot contain '");
									return;
								}
								if (value.indexOf('"') > -1) {
									alert('Device name cannot contain "');
									return;
								}
								
								var eIndex = $.inArray(value, self._deviceNames);
								if(eIndex == -1) {
									if(length > 1 && value.slice(-1) == DEF_SUGGESTION_WILDCARD) {
										var valueNotWildcard = value.slice(0, length-1); // use wildcard
										var matcher = new RegExp("^" + valueNotWildcard);
										var prefixMatchValues = $.grep(self._deviceNames, function(item, index) {
											return matcher.test(item);
										});
										if(prefixMatchValues.length > 0) {
											self._showTagItem($(this), value);
										}
									}
								} else {
									self._showTagItem($(this), value);
								}
							}
						})
						.autocomplete({
							delay: 0,
							minLength: 0,
							source: function (req, response) {
								var value = req.term;
								var length = $.trim(req.term).length;
								if(length > 1 && req.term.slice(-1) == DEF_SUGGESTION_WILDCARD) {
									value = req.term.slice(0, length-1); // use wildcard
								}
								var re = $.ui.autocomplete.escapeRegex(value);
								var matcher = new RegExp("^" + re);
								var newSrc = $.grep(self._deviceNames, function(item, index) {
									return matcher.test(item);
								});
								response(newSrc);
							},
							select: function (event, ui) {
								debug("select... fire");
								debug(ui.item ? "Selected: " + ui.item.value + ", id=" + ui.item.id : "Nothing selected, input is: " + this.value);
								self._showTagItem(this, ui.item.value);
							},
							change: function (event, ui) {
								debug("change...fire");
							}
						});
						
						$(this).find('a.' + DEF_CLASSES.tagComboToggle)
								.attr('tabIndex', -1)
								.attr('title', 'Show All')
								.click(function() {
									if(inputEl.autocomplete('widget').is(':visible')) {
										inputEl.autocomplete('close');
										return false;
									}
									$(this).blur();									
									inputEl.autocomplete('search', '');									
									inputEl.focus();
									return false;
								});
						if(opt.renderDiv){
							inputEl.autocomplete( "option", "appendTo", "#"+opt.renderDiv);
						}
						if(opt.needShowTopology){
							inputEl.autocomplete( "option", "appendTo", "#classfierEditPanelAPanel_c" );
						}
					} else {
						// Item status
						self._initTagItem($(this));
					}
				});
				
				deviceField.appendTo(el);
			} else {
				if(existCell.is(':hidden')) {
					existCell.show();
				}
				//TODO set the fields for 'Edit'
			}
		},
		_hideDeviceNames: function() {
			var self = this,
				el = self.element;
			debug("_hideDeviceNames...");
			var existCell = el.find('td.' + DEF_CLASSES.devices);
			if(existCell.length > 0) {
				existCell.hide();
			}
			// close the autocomplete
			var iEl = existCell.find('input#'+opt.cIds.Device);
			if(iEl.length > 0) {
				iEl.autocomplete('close');
			}
			//TODO reset fields
		},
		updateDeviceNames: function(source) { // ['BR-100', 'AP-120']
			var self = this,
				el = self.element,
				opt = self.options;
			if($.isArray(source)) {
				// merge, unique
				$.extend(self._deviceNames, uniqueArray($.merge(source,self._deviceNames)));
				// sort the array (alpha order)
				self._deviceNames.sort(function (a, b) {
    				return a.toLowerCase().localeCompare(b.toLowerCase());
				});
				debug('after merge, source='+self._deviceNames);
				if($('input#'+opt.cIds.Device).length > 0) {
					$('input#'+opt.cIds.Device).autocomplete('option', 'source'); // update source
				}
			}
		},
		
		/** Topology section */
		_showTopology: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			var zDisplay=opt.zindexDisplay;
			var zDisplayTop=opt.zindexDisplayAtTop;	
			debug("_showTopology...");
			var existCell = el.find('td.' + DEF_CLASSES.topology);
			if(existCell.length == 0) {
				var topoField = $('<td/>')
								.addClass(DEF_CLASSES.td)
								.addClass(DEF_CLASSES.tagContainer)
								.addClass(DEF_CLASSES.topology).attr('valign', "top").attr('id','topologyCtner');
				
				var ulEl = $('<ul/>')
				.append('<li><a class="' + DEF_CLASSES.tagsNoneA + '" href="'+ DEF_EMPTY_ANCHOR_HREF +'"><span class="' + DEF_CLASSES.tagsNoneS + '">None</span></a></li>')
				.append('<li id="' + DEF_ID_PREFIX.topoInput + '"><input id="' + opt.cIds.Topo + '" type="text"><input id="' +opt.cIds.Topo+ '_hidden" type="hidden"><a href="' + DEF_EMPTY_ANCHOR_HREF + '" class="' + DEF_CLASSES.tagComboToggle+ '"><span/></a></li>')
				.append('<li class="' + DEF_CLASSES.tagItem + '" id="' + DEF_ID_PREFIX.topoItem + '"><a href="' + DEF_EMPTY_ANCHOR_HREF + '" class="' +DEF_CLASSES.tagItemText+ '"><span></span></a><a href="'+DEF_EMPTY_ANCHOR_HREF+'" class="' +DEF_CLASSES.tagItemCancel+ '"></a></li>');
				
				$('<div/>').append(ulEl).appendTo(topoField);
				
				$.each(ulEl.find('li'), function(index, value) {
					var timer;
					if(index ==0) {
						// None status
						self._initNoneItem($(this));
					}else if(index == 1) {
						var tem = $(this).find('input#' + opt.cIds.Topo);	
						// Input status with auto complete						
						var inputEl = $(this).find('input#' + opt.cIds.Topo)
						.attr('readonly', true)
						.autocomplete({
							delay: 0,
							minLength: 0,
							source: self._topologyMaps,
							focus: function (event, ui) {
								$(this).val(ui.item.mapName);								
								return false;
							},
							select: function (event, ui) {
								debug("select... fire");
								debug(ui.item ? "Selected: " + ui.item.value + ", id=" + ui.item.id +", index="+ui.item.index : "Nothing selected, input is: " + this.value);
								$(this).val(ui.item.mapName);
								$(this).parent().find('input#'+opt.cIds.Topo+'_hidden').val(ui.item.id);
								self._showTagItem(this, ui.item.mapName);
								
								if(timer) {
									clearTimeout(timer);
									timer = null;
									debug('trigger clearTimeout');
								}
							}
						})
						.data("autocomplete")._renderItem = function(ul, item) {
							if(zDisplay)
								ul.autocomplete('widget').css('z-index', 5);
							if(zDisplayTop)
								ul.autocomplete('widget').css('z-index', 7);
							return $("<li></li>")
									.data("item.autocomplete", item)
									.append("<a style='padding-left:" + (item.indent*4 + 4) + "px;'>" + item.mapName + "</a>")
									.appendTo(ul);							
						};
											
						$(this).find('a.' + DEF_CLASSES.tagComboToggle)
								.attr('tabIndex', -1)
								.attr('title', 'Show All')
								.click(function() {
									if(tem.autocomplete('widget').is(':visible')) {
										tem.autocomplete('close');
										
										if($.trim(tem.val()).length == 0) {
											timer = setTimeout(function($el) {
												return function() {
													if($.trim($el.val()).length == 0) {
														$el.parent().hide();
														$el.parent().prev().show();
														
														tem.autocomplete('close');
													}
													timer = null;
												}
											}(tem), 2*1000);
										}
										return false;
									}	
									$(this).blur();									
									tem.autocomplete('search', '');
									return false;
								});
						
						if(opt.renderDiv){
							tem.autocomplete( "option", "appendTo", "#"+opt.renderDiv);
						}
						if(opt.needShowTopology){
							tem.autocomplete( "option", "appendTo", "#classfierEditPanelAPanel_c" );
						}
					} else {
						// Item status
						self._initTagItem($(this));
					}
				});
				
				topoField.appendTo(el);
			} else {
				if(existCell.is(':hidden')) {
					existCell.show();
				}
				//TODO set the fields for 'Edit'
			}			
		},
		_hideTopology: function() {
			var self = this,
			el = self.element;
			debug("_hideTopology...");
			var existCell = el.find('td.' + DEF_CLASSES.topology);
			if(existCell.length > 0) {
				existCell.hide();
			}
			// close the autocomplete
			var iEl = existCell.find('input#'+opt.cIds.Topo);
			if(iEl.length > 0) {
				iEl.autocomplete('close');
			}
			//TODO reset fields
		},
		updateTopology: function(source) { // [{id: 1, mapName: 'map', indent: 0}]
			var self = this,
				el = self.element,
				opt = self.options;
			if($.isArray(source)) {
				// merge, unique
				$.extend(self._topologyMaps, source);
				//debug(self._topologyMaps);
				$('input#'+opt.cIds.Topo).autocomplete('option', 'source', self._topologyMaps); // update source
			}
		},
		
		/** Add item to sortalbe container*/
		addItem: function(item, index) {
			// the index for global is -1
			// {id: 12, value: null/'ver', type: 1, tagValue: 'String', desc: 'desc'}
			debug("add item...");
			var self = this,
				el = self.element,
				opt = self.options;
			
			var isGlobalItem = index && index == -1;
			var filterAttrs = ['id'];
			
			if(self._tableWidget) {
				var itemContainer = self._tableWidget.find('ul.' + DEF_CLASSES.uiItemContainer);
				// set id
				if(item.oId != item.id) {
					// reset the position
					var samePositionItem = itemContainer.find('li#'+item.id);
					if(samePositionItem.length) {
						//samePositionItem.attr('id', item.oId);
					}
				}
				var itemEl = $('<li/>').addClass(DEF_CLASSES.uiItem);//.attr('id', item.id)
				if(isGlobalItem) {
					// disable dnd for global item
					itemEl.addClass(DEF_CLASSES.uiItemDisable);
				}
				
				if(opt.checkable) {
					var chkEl = $('<span/>').addClass(DEF_CLASSES.checkBox);
					if(isGlobalItem) {
						chkEl.addClass(DEF_CLASSES.checkBoxDisable);
					} else {
						chkEl.append('<input type="checkbox" name="' + opt.checkable + '"/>');
					}
					chkEl.appendTo(itemEl);
				}
				var tempItemEditable=opt.itemEditable;
				if(opt.onlyDisplayValue!=null&&item.value.indexOf('|')!=-1) {
					opt.itemEditable={value: true, valueName: 'macRangeFrom|macRangeTo', desc: true, descName: 'descriptions'};					
				}
				
				var subItemWidths = null;
				var subItemOperation = null;
				// set value
				if(item.value) {
					var spanEl = $('<span/>');
					if(opt.itemEditable && opt.itemEditable.value) {
						if(opt.valueProps) {
							if($.isArray(opt.valueProps)) {
								//TODO multi-value group
								for(var i=0; i<opt.valueProps.length; i++) {
									var valueProp = opt.valueProps[i];
									var vItems = valueProp.items;
									var names = self._splitStr(opt.itemEditable.valueName); // HTML names
									var globalIds = isGlobalItem ? self._getGlobalIdsByFlag(valueProp.flag.type) : null; // HTML ids
									
									if(valueProp.flag && valueProp.flag.type === self._valueFlagKey) {
										subItemWidths = valueProp.subItemWidths;
										subItemOperation = valueProp.subItemOperation;
										var values = self._splitStr(item.value);
										
										debug("add item, HTML names=" + names);
										debug("add item, HTML values=" + values);
										
										for(var j=0; j<vItems.length; j++) {
											var valueEl = self._buildValueEl(vItems[j], filterAttrs);
											if(values[j]==" ")values[j]= "null";
											valueEl.attr('name', names[j]).attr('value', values[j] === "null" ? "" : values[j]);
											
											if(names[j]==opt.selectItemName){
												if(opt.selectItemDefine!=null){		
													if(values[j]>0){
														valueEl = $('<span/>').addClass(DEF_CLASSES.textPointer).html(opt.selectItemDefine[values[j]-1]).appendTo(itemEl);													
													}
													if(values[j]==undefined){		
														for(var k=0;k<opt.selectItemDefine.length;k++){
															valueEl.append("<option value='"+(k+1)+"'>"+opt.selectItemDefine[k]+"</option>");
														}
													}													
												}												
											}
											
											if(isGlobalItem && globalIds) {
												// add id for global item input
												valueEl.attr('id', globalIds[j]);
											}
											
											spanEl.append(valueEl);
											
											if(valueProp.concatenateHTML && j<vItems.length-1) {
												valueEl.after(valueProp.concatenateHTML);
											}
										}
										spanEl.appendTo(itemEl);
									} else {
										if(isGlobalItem && globalIds) {
											for(var j=0; j<vItems.length; j++) {
												var valueEl = self._buildValueEl(vItems[j], filterAttrs);
												valueEl.attr('name', names[j]).attr('disabled', true).val("");
												
												// add id for global item input
												valueEl.attr('id', globalIds[j]);
												valueEl.hide();
												
												spanEl.append(valueEl);
											}
										}
									}
								}
							} else {
								var vItems = opt.valueProps.items;
								if($.isArray(vItems)) {
									//TODO multi-value
									var names = self._splitStr(opt.itemEditable.valueName);
									var values = self._splitStr(item.value);
									
									var globalIds;
									if(opt.globalProps && opt.globalProps.id) {
										globalIds = self._splitStr(opt.globalProps.id);
									}
									for(var j=0; j<vItems.length; j++) {
										var valueEl = self._buildValueEl(vItems[j], filterAttrs);
										if(values[j]==" ")values[j]= "null";
										valueEl.attr('name', names[j]).attr('value', values[j] === "null" ? "" : values[j]);
										
										if(isGlobalItem && opt.globalProps) {
											// add id for global item input
											valueEl.attr('id', globalIds[j]);
										}
										spanEl.append(valueEl);
									}
									spanEl.appendTo(itemEl);
								} else {
									var valueEl = self._buildValueEl(vItems, filterAttrs)
									.attr('name', opt.itemEditable.valueName)
									.attr('value', item.value == 0 ? "" : item.value);
									
									if(isGlobalItem && opt.globalProps && opt.globalProps.id) {
										// add id for global item input
										valueEl.attr('id', opt.globalProps.id);
									}
									
									spanEl.append(valueEl).appendTo(itemEl);
								}
							}
						}
					} else {
						spanEl.addClass(DEF_CLASSES.textPointer).html(item.value).appendTo(itemEl);
					}
					
					//spanEl.width(opt.itemWidths.value);
					spanEl.width(self._getItemWidths(subItemWidths,opt.itemWidths,'value'));
					spanEl[0].style.display = "inline-block";
				} else {
						$('<span/>').addClass(DEF_CLASSES.textPointer).width(self._getItemWidths(subItemWidths,opt.itemWidths,'value')).appendTo(itemEl);
						//$('<span/>').addClass(DEF_CLASSES.textPointer).width(opt.itemWidths.value).appendTo(itemEl);
				}
				
				opt.itemEditable=tempItemEditable;
				// set type
				var types = $.extend([],opt.types);
				if(opt.needGlobalItem) {
					types.push(DEF_GLOBAL_TYPE);
				}
				$.each(types, function(index, value) {
					if(value.key == item.type) {
						debug("Current item type: "+ item.type +", name: " + value.text);
						$('<span/>').addClass(DEF_CLASSES.textPointer).width(self._getItemWidths(subItemWidths,opt.itemWidths,'type')).html(value.text).appendTo(itemEl);
						//$('<span/>').addClass(DEF_CLASSES.textPointer).width(opt.itemWidths.type).html(value.text).appendTo(itemEl);
						return;
					}
				});
				// set tag value
				//$('<span/>').addClass(DEF_CLASSES.textPointer).width(opt.itemWidths.tagValue).html(item.tagValue).appendTo(itemEl);
				$('<span/>').addClass(DEF_CLASSES.textPointer).width(self._getItemWidths(subItemWidths,opt.itemWidths,'tagValue')).html(item.tagValue).appendTo(itemEl);
				// set description
				if(!opt.describable || item.desc === undefined) {
					//dump
				} else {
					if(opt.itemEditable && opt.itemEditable.desc) {
						var descInput = $('<input/>')
										.attr('name', opt.itemEditable.descName)
										.attr('value', item.desc)
										.attr('size', 24)
										.attr('maxLength', 64);
						$('<span/>').append(descInput).appendTo(itemEl);
					} else {
						//$('<span/>').addClass(DEF_CLASSES.textPointer).width(opt.itemWidths.tagValue).html(item.desc).appendTo(itemEl);
						$('<span/>').addClass(DEF_CLASSES.textPointer).width(self._getItemWidths(subItemWidths,opt.itemWidths,'tagValue')).html(item.desc).appendTo(itemEl);
					}
				}
				
				var itemOperation = $.extend(true,[],opt.itemOperation);
				if(subItemOperation){
					$.extend(true,itemOperation,subItemOperation);
				}
				
				// operation icon
				if(!isGlobalItem && $.isArray(itemOperation)) {
					var operationC = $('<ul/>')
					.addClass(DEF_CLASSES.operationMenuContainer);
					$.each(itemOperation, function(index, value) {
						if(value!=null && value.display){						
						$('<li/>').append('<span>' + value.text + '</span>')
						.click(value.fn)
						.hover(
								function() { // mouse in
									$(this).addClass(DEF_CLASSES.menuHover);
									$(this).parent().hover(function(){}, function() { // mouse out
										if($(this).not(':hidden')) {
											$(this).hide();
										}
									});
								},
								function() { // mouse out
									$(this).removeClass(DEF_CLASSES.menuHover);
								}
						)
						.appendTo(operationC);
						}
					});
					
					$('<span/>')
					.addClass(DEF_CLASSES.operationIcon)
					.append(operationC)
					.appendTo(itemEl)
					.click(function(event) {
						//Add these code to fix bug 24936
						var oc = $("ul.optmenu-container");
						var t = $(event.target).hasClass("optmenu");
						if(oc.length > 0 && t){
							for(var i = 0; i<oc.length; i++){
								if($(oc[i]).is(':hidden')){
									continue;
								}else{
									$(oc[i]).hide();
									continue;
								}
							}
						}
						//The end of fixing 25936
						var menu = $(this).find('ul');
						if(menu.length > 0) {
							if(menu.is(':hidden')) {
								menu.show();
							} else {
								menu.hide();
							}
						}
					});
					
					itemEl.find('ul').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
						event.stopImmediatePropagation();
					});
				}
				
				if(index >= 0) {
					var oItemEl = itemContainer.find('li.'+DEF_CLASSES.uiItem).eq(index);
					if(oItemEl.length) {
						itemEl.insertBefore(oItemEl);
					} else {
						debug("Warning: unable to find the li element [index="+index+"], please check.");						
						if(!opt.needGlobalItem||opt.onlyDisplayValue!=null) {
							itemEl.appendTo(itemContainer);
						}
					}
				} else {
					debug("Append the item to the end.");
					itemEl.appendTo(itemContainer);
				}
				
				
				// make the the input element editable
				// 'click.sortable mousedown.sortable' - event.target.focus(), not work well in Firefox
				itemEl.find('input').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
					event.stopImmediatePropagation();
				});
				
				return itemEl;
			}
		},
		_getItemWidths: function(subItemWidths,itemWidths,itemName){
			var result;
			var flag = subItemWidths && subItemWidths.tagValue;
			switch (itemName) {
				case "value":
					result = flag ?subItemWidths.value :itemWidths.value;
					break;
				case "type":
					result = flag ?subItemWidths.type :itemWidths.type;
					break;
				case "tagValue":
					result = flag ?subItemWidths.tagValue :itemWidths.tagValue;
					break;
				case "desc":
					result = flag ?subItemWidths.desc :itemWidths.desc;
					break;
				default:
					retuslt = 0;
			}
			return result;
		},
		saveItem: function() {
			debug("start save item...");
			var self = this,
				opt = self.options;
			
			var params = {
					operation: 'add',
					tagKey: opt.key,
					type: self._currentSel
					
			};
			
			if(params.type == 0) {
				self._showErrorMessage("Please choose the type.");
				return;
			}

			var param_values = self._getValues();
			if(!param_values) {
				debug("value is empty");
				return;
			}
			$.extend(params, param_values); // values
	
			var param_tagValues = self._getTagValueByType(self._currentSel);
			if(!param_tagValues) {
				debug("tag is empty");
				return;
			}
			$.extend(params, param_tagValues); // tag value
			var descValue = self._getTagDescValue();
			$.extend(params, descValue); // description
			
			// show the mask
			self._displayMask(self._sortedWidget, true);
			
			self._sendAddItemRequest(params);
			
		},
		saveMacRangeItem: function() {
			debug("start saveMacRangeItem ....");
			var self = this,
				opt = self.options;
			
			var params = {
					operation: 'add',
					tagKey: opt.key,
					type: -1
					
			};			
			
			var param_values = self._getValues();
			if(!param_values) {
				debug("value is empty");
				return;
			}
			$.extend(params, param_values); // values
			var descValue = self._getTagDescValue();
			$.extend(params, descValue); // description
			
			// show the mask
			self._displayMask(self._sortedWidget, true);
			
			self._sendAddItemRequest(params);
			
		},
		resetOrder: function() {
			debug("start reset order...");
			var self = this,
				opt = self.options;
			
			self._displayMask(self._sortedWidget, true);
			var timeFlag = {
					ignore: new Date().getTime()
					};
			$.getJSON(self._url,
					{operation: 'resetOrder', tagKey: opt.key,timeFlagValue:timeFlag,configTemplateId:$("#configTemplateId").val()},
					function(data) {
						debug("reset order, Get response from server side");
						debug(data);
						if(data.succ && data.items && self._tableWidget) {
							var itemContainer = self._tableWidget.find('ul.' + DEF_CLASSES.uiItemContainer);
							itemContainer.empty();
							
							for(var i=0;i<data.items.length;i++) {
								var item = {
										id: data.items[i].index,
										value: data.items[i].value, 
										type: data.items[i].type, 
										tagValue: data.items[i].tagValue, 
										desc: data.items[i].desc};
								if(data.items[i].global) {
									self.addItem(item, -1);
								} else {
									//TODO---start
									//self.addItem(item);
									if(($("#configTemplateId").val()!=undefined) 
											&& ($("#configTemplateId").val() != data.items[i].configTemplateId)
											&& (data.items[i].configTemplateId != 0)){
										self.addItem(item,-1);
										$(".item-state-disable").attr('style','display:none;');
									}else{
										self.addItem(item);
									}
									//TODO---end
								}
							}
							
							// reset the item
							self._restNewItem(self._currentSel);
						} else {
							self._showErrorMessage(data.errmsg);
						}
						// hide the mask
						self._displayMask(self._sortedWidget, false);
					})
			.error(function() {
				debug("reset order, Error occurs...");
				// hide the mask
				self._displayMask(self._sortedWidget, false);
			});
		},
		addEmptyGlobalItem: function() {
			debug("add an empty item...");
			var self = this,
				opt = self.options;
			
			if(!opt.needGlobalItem) {
				return;
			}
			var params = {
					operation: 'add',
					tagKey: opt.key,
					type: DEF_GLOBAL_TYPE.key
			};
			
			self._sendAddItemRequest(params);
			
		},
		removeAllItem: function(outerInvoke) {
			debug("remove the empty item...");
			var self = this,
				opt = self.options;
			
			if(!opt.needGlobalItem) {
				return;
			}
			var params = {
					operation: 'removeAllItem',
					tagKey: opt.key,
					type: DEF_GLOBAL_TYPE.key
					
			};
			
			self._sendAddItemRequest(params,outerInvoke);
			
		},
		_getGlobalIdsByFlag: function(flagType) { // for multi-value group, return null/Array
			debug("get global ids by flag...");
			var self = this,
				el = self.element,
				opt = self.options,
				valueProps = opt.valueProps;
			if($.isArray(opt.globalProps)) {
				for(var i=0; i<opt.globalProps.length; i++) {
					if(opt.globalProps[i].flagType === flagType) {
						var globalIds = self._splitStr(opt.globalProps[i].id);
						return globalIds;
					}
				}
			}
			return null;
		},
		showValueByFlag: function(cellId) { // for multi-value group
			debug("show value by flag, "+cellId);
			var self = this,
				el = self.element,
				table = self._tableWidget,
				opt = self.options,
				valueProps = opt.valueProps;
			if($.isArray(valueProps)) {
				var cellEl = el.find('td#'+cellId);
				for(var index=0; index<valueProps.length; index++) {
					var valueProp = valueProps[index];
					var flagObj = valueProp.flag;
					var type = flagObj.type;
					var globalIds = self._getGlobalIdsByFlag(type);
					
					if(flagObj && cellEl.length) {
						if(flagObj.name === cellId) {
							// match current selected type
							cellEl[0].style.display = "";
							self._valueFlagKey = type;
							
							var tempCellEl=$("td#chooseCtner");								
							self._showTdStr(tempCellEl);								
														
							tempCellEl=$("td#tagCtner");
							var isExistTagValue=self._showTdStr(tempCellEl);			
							tempCellEl=$("td#deviceCtner");
							var isExistDvcValue=self._showTdStr(tempCellEl);			
							tempCellEl=$("td#topologyCtner");
							var isExistTopoValue=self._showTdStr(tempCellEl);
							if(isExistTagValue&&isExistDvcValue&&isExistTopoValue){
								tempCellEl=$("td#descCtner");
								if(tempCellEl[0]!=undefined){
									self._hideTdStr(tempCellEl);
								}
							}
							
							if(opt.onlyDisplayValue!=null&&flagObj.name==opt.onlyDisplayValue.name){								
								tempCellEl=$("td#chooseCtner");								
								self._hideTdStr(tempCellEl);								
								tempCellEl=$("td#tagCtner");
								self._hideTdStr(tempCellEl);			
								tempCellEl=$("td#deviceCtner");
								self._hideTdStr(tempCellEl);			
								tempCellEl=$("td#topologyCtner");
								self._hideTdStr(tempCellEl);	
								tempCellEl=$("td#descCtner");
								if(tempCellEl[0]==undefined){
									var descFields = $('<td/>')
													.addClass(DEF_CLASSES.td)
													.addClass(DEF_CLASSES.desc).attr('valign', "top").attr('id','descCtner');;
									if(opt.widgetWidth && opt.widgetWidth.desc) {
										descFields.width(opt.widgetWidth.desc);
									} 
									$('<input>')
										.attr('id', opt.descriptionId)
										.attr('type', 'text')
										.attr('maxLength', 64)
										.attr('size', 24)
										.appendTo(descFields);
									descFields.append('<br>(0-64 characters)').appendTo(el);
								
								}
								else{
									self._showTdStr(tempCellEl);
								}
							}
							
							
							// for global item
							if(globalIds && $.isArray(globalIds)) {
								for(var j=0; j<globalIds.length; j++) {
									var gEl = table.find("#"+globalIds[j])
									if(gEl.length) {
										gEl.attr("disabled", false);
										gEl[0].style.display = "inline";
										
										if(valueProp.concatenateHTML && j<globalIds.length-1) {
											gEl.after(valueProp.concatenateHTML);
										}
									}
								}
							}
						} else {
							// mismatch current selected type
							el.find('td#'+flagObj.name).hide();
							
							// for global item
							if(globalIds && $.isArray(globalIds)) {
								for(var j=0; j<globalIds.length; j++) {
									var gEl = table.find("#"+globalIds[j]);
									if(gEl.length) {
										gEl.hide();
										gEl.attr("disabled", true);
										
										if(valueProp.concatenateHTML && j<globalIds.length-1) {
											var nextEl = gEl[0].nextSibling;
											if(nextEl.nodeType == 3) {
												nextEl.nodeValue = nextEl.nodeValue.replace(valueProp.concatenateHTML.replace(/&nbsp;/g,"\u00a0"), "");
											}
										}
									}
								}
							}
						}
					}
				}
			}
		},		
		initTopologyMaps: function() {
			var self = this;
			var timeFlag = {
					ignore: new Date().getTime()
					};
			$.getJSON(self._url,
					{operation: 'maps',timeFlagValue:timeFlag},
					function(data) {
						if(data.succ) {
							self.updateTopology(data.value);
							if (self.options.needShowTopology) {
								self._showTopology();
							}
							debug("initTopologyMaps, init topology maps successfully...");
						}
					})
					.error(function() {
						debug("initTopologyMaps, Error occurs...");
					});
		},
		initDeviceNames: function() {
			var self = this;
			var timeFlag = {
					ignore: new Date().getTime()
					};
			$.getJSON(self._url,
					{operation: 'deviceNames',timeFlagValue:timeFlag},
					function(data) {
						if(data.succ) {
							self.updateDeviceNames(data.value);
							if (self.options.needShowDeviceNames) {
								self._showDeviceNames();
							}
							debug("initDeviceNames, init device names successfully...");
						}
					})
					.error(function() {
						debug("initDeviceNames, Error occurs...");
					});
		},		
		initDeviceTags: function() {
			var self = this;
			var opt = self.options;
			var timeFlag = {
					ignore: new Date().getTime()
					};
			$.getJSON(self._url,
					{operation: 'tagValues',timeFlagValue:timeFlag},
					function(data) {
						if(data.succ) {
							if(data.value.tag1) {
								self.updateTagValues(data.value.tag1,0);
							}
							if(data.value.tag2) {
								self.updateTagValues(data.value.tag2,1);
							}
							if(data.value.tag3) {
								self.updateTagValues(data.value.tag3,2);
							}
							opt.cNames={
								Tag: [data.value.customTag1, data.value.customTag2, data.value.customTag3],
								Device: 'Device Name',
								Topo: 'Topology Node'
							};
							if (opt.needShowTagFields) {
								self._showTagFields();
							}
							debug("initDeviceTags, init device tags successfully...");
						}
					})
					.error(function() {
						debug("initDeviceTags, Error occurs...");
					});
		},
		removeItems: function(outerInvoke, $el) {
			var self = this,
			opt = self.options;
			debug("remove items...");
			var indexStr, rows = [];
			if(outerInvoke) {
				//TODO
			} else {
				var row = $el.parent().parent().parent();
				var index = self._getItemIndexes(row);
				rows.push(row);
				indexStr = index;
			}
			self._sortedWidget.find("li.needAlertInRed").removeClass('needAlertInRed');
			var vlanIdValue=0;
			if(opt.errorEl.id=='checkAllIpReserveClass'||opt.errorEl.id=='checkAllSubnetClass'){
				vlanIdValue=$("input#vlanId").val();				
			}
			var timeFlag = {
					ignore: new Date().getTime()
					};	
			$.getJSON(self._url, 
					{tagKey: opt.key, operation: 'remove', selectedItems: index,vlanId: vlanIdValue,timeFlagValue:timeFlag}, 
					function(data) {
						if(data.succ) {
							for(var index=0; index<rows.length; index++) {
								rows[index].remove();
							}
						}
					})
					.error(function() {
						debug("removeItems, Error occurs...");
					});
		},
		editItems: function(outerInvoke, $el) {			
			var self = this,
			opt = self.options;
			var row = $el.parent().parent().parent();
			var index = self._getItemIndexes(row);
			self._sortedWidget.find("li.needAlertInRed").removeClass('needAlertInRed');
			var vlanIdValue=0;
			if(opt.errorEl.id=='checkAllIpReserveClass'||opt.errorEl.id=='checkAllSubnetClass'){
				vlanIdValue=$("input#vlanId").val();				
			}	
			var timeFlag = {
					ignore: new Date().getTime()
					};	
			$.getJSON(self._url, 
					{tagKey: opt.key, operation: 'edit1', selectedItems: index,vlanId: vlanIdValue,timeFlagValue:timeFlag}, 
				function(data) {
					if(data.succ) {
						var sType=data.value;	
						var sTagValue=data.tagValue;
						$("input#ahTempClassifierIndexValue").attr("value",index);
						$("input#ahTempClassifierItemType").attr("value",sType);
						$("input#ahTempClassifierKeyValue").attr("value",opt.key);
						$("input#ahTempClassifierEditBtn").click();		
						if(!opt.needShowTopology)
						widgetClick(sType,sTagValue);	
						setTimeout(function(){		
							if(sType==4){				
								self._showTagItem('input#'+opt.cIds.Tag[0], "");	 
								self._showTagItem('input#'+opt.cIds.Tag[1], "");	 
								self._showTagItem('input#'+opt.cIds.Tag[2], "");	
								self._showTagItem('input#'+opt.cIds.Tag[0], data.tag1);	 
								self._showTagItem('input#'+opt.cIds.Tag[1], data.tag2);	 
								self._showTagItem('input#'+opt.cIds.Tag[2], data.tag3);	
							}
							if(sType==3){
								self._showTagItem('input#'+opt.cIds.Device, "");
								self._showTagItem('input#'+opt.cIds.Device, sTagValue);
							}
							if(sType==2){
								self._showTagItem('input#'+opt.cIds.Topo, "");   
								self._showTagItem('input#'+opt.cIds.Topo, sTagValue);   
							}	
					    }, 200);
					}
					})
				.error(function() {
					debug("edit1Items, Error occurs...");
				});
		
		},
		viewItems: function(outerInvoke, $el) {
			var self = this,
			opt = self.options;
			var indexStr, rows = [];
			if(outerInvoke) {
				//TODO
			} else {
				var row = $el.parent().parent().parent();
				var index = self._getItemIndexes(row);
				rows.push(row);
				indexStr = index;
			}			
			var timeFlag = {
					ignore: new Date().getTime()
					};	
			$.getJSON(self._url, 
					{tagKey: opt.key, operation: 'view', selectedItems: index,timeFlagValue:timeFlag}, 
					function(data) {
						if(data.succ) {
							var ruleIndex=self._splitStr(data.items);	
							if(ruleIndex==''){
								$("div#classfierPopupPanel").empty();
								$('<p style="margin-top: 15px;">').append('No Conflicted Rule.').appendTo($("div#classfierPopupPanel"));
								//=======================
								$("div#classfierPopupPanelTitle").empty();
								$("div#classfierPopupPanelTitle").css('height','20px');
								$('<span align=left style="padding:1px 2px 1px 2px;color:#C84B00;background:#E0E0E0;-webkit-border-radius:4px;-moz-border-radius:4px;">').append('View Conflicts').appendTo($("div#classfierPopupPanelTitle"));
								//=======================
								$("input#ahTempClassifierMatchBtn").click();	return;
							}							
							for(var i=0; i<ruleIndex.length; i++) {	
								var tempIndex=parseInt(ruleIndex[i])+1;
								self._sortedWidget.find("li.item:nth-child("+tempIndex+")").addClass('needAlertInRed');
							}
							setTimeout(function(){		
						    	var ttIdx=tempIndex;
						    	self._sortedWidget.find("li.needAlertInRed").removeClass('needAlertInRed');
						    }, 10000);
						}
					})
					.error(function() {
						debug("viewItems, Error occurs...");
					});
		},			
		matchItems: function(outerInvoke, $el) {
			var self = this,
			opt = self.options;			
			if(outerInvoke) {
				//TODO
			} else {
				var row = $el.parent().parent().parent();
				var index = self._getItemIndexes(row);	
			}
			$("div#classfierPopupPanel").empty();
			var timeFlag = {
					ignore: new Date().getTime()
					};			
			$.getJSON(self._url, 
					{tagKey: opt.key, operation: 'match', selectedItems: index,timeFlagValue:timeFlag}, 
					function(data) {
						if(data.succ) {
							var dvcList=self._splitStr(data.items);		
							var tempLength=parseInt(dvcList.length)+1-1;
							for(var j=0; j<tempLength; j++) {		
								if(dvcList[j]==undefined)continue;
								$('<p align=left>').append(dvcList[j].trim()).appendTo($("div#classfierPopupPanel"));	
							}
							$("div#classfierPopupPanel").css('height','60px');
							if(dvcList.length==1){
								if(dvcList[0]=='')
								$('<p style="margin-top: 15px;">').append('No device matched').appendTo($("div#classfierPopupPanel"));
							}		
							//=======================
							$("div#classfierPopupPanelTitle").empty();
							$("div#classfierPopupPanelTitle").css('height','20px');
							$('<span align=left style="padding:1px 2px 1px 2px;color:#C84B00;background:#E0E0E0;-webkit-border-radius:4px;-moz-border-radius:4px;">').append('Matching Devices').appendTo($("div#classfierPopupPanelTitle"));
							//=======================
							$("input#ahTempClassifierMatchBtn").click();							
						}
					})
					.error(function() {
						debug("matchItems, Error occurs...");
					});
		},	
		/** _Common section */
		_initNoneItem: function($el) {
			// None status
			$el.find('a.' + DEF_CLASSES.tagsNoneS).click(function(event) {
				event.preventDefault();
				$(this).parent().hide();
				$(this).parent().next().show();
				$(this).parent().next().find('input').focus();
			});
		},
		_initTagItem: function($el,opt) {
			var self = this;
			// Item status
			var closeEl = $el.find('a.cancel-icon');
			if(closeEl.length == 1) {
				closeEl.click(function(event) {
					event.preventDefault();
					$(this).parent().hide();
					$(this).parent().prev().prev().show();
					$(this).parent().prev().find('input').val('');
					var el = $el.parent().parent().parent().parent();
					self._inputTagComplate(el,opt)
				});
			}
			var tagEl = $el.find('a.' + DEF_CLASSES.tagItemText);
			if(tagEl.length == 1) {
				tagEl.click(function(event) {
					event.preventDefault();
					$(this).parent().hide();
					$(this).parent().prev().show();
					$(this).parent().prev().find('input').focus();
				});
			}
		},
		_inputTagComplate: function(el,opt){
			if(opt != undefined && opt.inputTagDone.funcName && opt.inputTagDone.argument && el != undefined){
				if(opt.inputTagDone.funcName instanceof Function){
					var tag1 = el.find('input#'+opt.cIds.Tag[0]) == undefined ? '': el.find('input#'+opt.cIds.Tag[0]).val();
					var tag2 = el.find('input#'+opt.cIds.Tag[1]) == undefined ? '': el.find('input#'+opt.cIds.Tag[1]).val();
					var tag3 = el.find('input#'+opt.cIds.Tag[2]) == undefined ? '': el.find('input#'+opt.cIds.Tag[2]).val();
					opt.inputTagDone.funcName.call(this,opt.inputTagDone.argument,tag1,tag2,tag3);
				}
			}
		},
		_displayMask: function($container, masked) {
			debug("show/hide mask...");
			var maskLayer = $container.next().find('div.'+DEF_CLASSES.mask);
			if(maskLayer.length) {
				if(masked) {
					var width = $container.width(), height = $container.height();
					debug("show mask, height="+height);
					maskLayer.width(width).height(height).css('top', '-'+height+'px');
					maskLayer.show();
				} else {
					maskLayer.hide();
					debug("hide mask...");
				}
			}
		},
		_getValues: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			var zDisplayTop=opt.zindexDisplayAtTop;
			var valuesObj = {};
			if(opt.valueProps) {
				if($.isArray(opt.valueProps)) {
					// multi-value group
					debug("get value for multi-value group");
					for(var index=0; index<opt.valueProps.length; index++) {
						var valueProp = opt.valueProps[index];
						if(valueProp.flag && valueProp.flag.type == self._valueFlagKey) {
							if(valueProp.validateFn && typeof valueProp.validateFn === 'function' && valueProp.validateFn()) {
								if($.isArray(valueProp.items)) {
									for(var j=0; j<valueProp.items.length; j++) {
										var item = valueProp.items[j];
										var eType = item.elType && item.elType === 'select' ? 'select' : 'input'; 
										var iE = el.find(eType+'#'+item.id);
										if(iE.length) {
											valuesObj[item.field] = iE.val();
										}
									}
								}
							} else {
								return false;
							}
						}
					}
				} else {
					var valueProps = opt.valueProps;
					var vItems = valueProps.items;
					if($.isArray(vItems)) {
						// multi-value
						if(valueProps.validateFn && typeof valueProps.validateFn === 'function' && valueProps.validateFn()) {
							for(var j=0; j<valueProp.items.length; j++) {
								var item = valueProp.items[j];
								var eType = item.elType && item.elType === 'select' ? 'select' : 'input';
								var iE = el.find(eType+'#'+item.id);
								if(iE.length) {
									valuesObj[item.field] = iE.val();
								}
							}
						} else {
							return false;
						}
					} else {
						//TODO===start
						//var iE = el.find('input#'+vItems.id);
						var eType = vItems.elType && vItems.elType === 'select' ? 'select' : 'input';
						var iE = el.find(eType+'#'+vItems.id);
						//TODO==end
						if(valueProps.validateFn && typeof valueProps.validateFn === 'function' && valueProps.validateFn()) {
							if(iE.length) { 
								valuesObj[vItems.field] = iE.val();
							}
						} else {
							return false;
						}
					}
				}
			}
			if(opt.errorEl.id=='checkAllIpReserveClass'||opt.errorEl.id=='checkAllSubnetClass'){
				var vlanIdValue=$("input#vlanId").val();
				var vlanIdTempObj = {vlanId:vlanIdValue};
				$.extend(valuesObj, vlanIdTempObj); 
			}			
			return valuesObj;
		},
		_resetValues: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			
			if(opt.valueProps) {
				if($.isArray(opt.valueProps)) {
					// multi-value group
					for(var index=0; index<opt.valueProps.length; index++) {
						var valueProp = opt.valueProps[index];
						var vItems = valueProp.items;
						if($.isArray(vItems)) {
							for(var j=0; j<vItems.length; j++) {
								var item = valueProp.items[j];
								var eType = item.elType && item.elType === 'select' ? 'select' : 'input';
								var iE = el.find(eType+'#'+item.id);
								if(iE.length) {
									if(eType === 'select') {
										iE.val('-1');
									} else {
										iE.val('');
									}
								}
							}
						}
					}
				} else {
					var valueProp = opt.valueProps;
					var vItems = valueProp.items;
					if($.isArray(vItems)) {
						// multi-value
						for(var j=0; j<vItems.length; j++) {
							var item = valueProp.items[j];
							var eType = item.elType && item.elType === 'select' ? 'select' : 'input';
							var iE = el.find(eType+'#'+item.id);
							if(iE.length) {
								if(eType === 'select') {
									iE.val('-1');
								} else {
									iE.val('');
								}
							}
						}
					} else {
						var iE = el.find('input#'+vItems.id);
						if(iE.length) {
							iE.val('');
						}
					}
				}
			}
		},
		_getTagValueByType: function(type) {
			var self = this,
				el = self.element,
				opt = self.options;
			
			var valueObj = new Object();
			switch (type) {
			case 4:
				valueObj.tag1 = el.find('input#'+opt.cIds.Tag[0]).val();
				valueObj.tag2 = el.find('input#'+opt.cIds.Tag[1]).val();
				valueObj.tag3 = el.find('input#'+opt.cIds.Tag[2]).val();
				if(self._isEmptyStr(valueObj.tag1) 
						&& self._isEmptyStr(valueObj.tag2) 
						&& self._isEmptyStr(valueObj.tag3)) {
					self._showErrorMessage(opt.cNames.Tag[0]+" or " + opt.cNames.Tag[1] + " or " + opt.cNames.Tag[2] + " is required field.");
					return false;
				} 
				break;
			case 3:
				valueObj.typeName = el.find('input#'+opt.cIds.Device).val();
				if(self._isEmptyStr(valueObj.typeName)) {
					self._showErrorMessage(opt.cNames.Device + " is required field.");
					return false;
				}
				break;
			case 2:
				
				valueObj.locationId = el.find('input#'+opt.cIds.Topo+'_hidden').val();
				if(self._isEmptyStr(valueObj.locationId)) {
					self._showErrorMessage(opt.cNames.Topo + " is required field.");
					return false;
				}
				break;

			default:
				break;
			}
			return valueObj;
		},
		_resetTagValueByType: function(type) {
			var self = this,
				el = self.element,
				opt = self.options;

			switch (type) {
			case 4:
				el.find('input#'+opt.cIds.Tag[0]).val('').parent().hide().next().hide().prev().prev().show();
				el.find('input#'+opt.cIds.Tag[1]).val('').parent().hide().next().hide().prev().prev().show();
				el.find('input#'+opt.cIds.Tag[2]).val('').parent().hide().next().hide().prev().prev().show();
				break;
			case 3:
				el.find('input#'+opt.cIds.Device).val('').parent().hide().next().hide().prev().prev().show();
				break;
			case 2:
				el.find('input#'+opt.cIds.Topo+'_hidden').val('');
				el.find('input#'+opt.cIds.Topo).val('').parent().hide().next().hide().prev().prev().show();
				break;
				
			default:
				break;
			}
		},
		_getTagDescValue: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			
			var descObj = new Object();
			var desc = el.find('input#'+opt.descriptionId);
			if(desc.length) {
				descObj.description = desc.val();
			}
			return descObj;
		},
		_resetTagDescValue: function() {
			var self = this,
				el = self.element,
				opt = self.options;
			
			var desc = el.find('input#'+opt.descriptionId);
			if(desc.length) {
				desc.val('');
			}
		},
		_restNewItem: function(type) {
			var self = this;
			
			self._resetValues();
			self._resetTagValueByType(type);
			self._resetTagDescValue();
		},
		_sendAddItemRequest: function(params,outerInvoke) {
			var self = this;
			var timeFlag = {
					ignore: new Date().getTime()
					};
			$.extend(params, timeFlag); 
			
			$.getJSON(self._url,
					params,
					function(data) {
						// response {succ:true,index:-1~N, value: "test", tagValue, errmsg: ""}
						debug("Get response from server side");
						//{id: 12, value: null/'ver', type: 1, tagValue: 'String', desc: 'desc'}
						if(data.succ) {
							self.addItem({
								id: data.index,
								oId: data.oIndex,
								value: data.value, 
								type: params.type, 
								tagValue: data.tagValue, 
								desc: params.description ? params.description : ""}, 
								data.index);
							// reset the item
							self._restNewItem(params.type);
						} else {
							self._showErrorMessage(data.errmsg);
						}
						
						if($.isFunction(outerInvoke)){
							outerInvoke.apply(self);
						}
						// hide the mask
						self._displayMask(self._sortedWidget, false);
					})
			.error(function() {
				debug("Error occurs when save item");
				// hide the mask
				self._displayMask(self._sortedWidget, false);
			});
		},
		_showErrorMessage: function(msg) {
			var self = this,
				opt = self.options;
			if(msg) {
				hm.util.hideFieldError();
				if(opt.errorEl && document.getElementById(opt.errorEl.id)) {
					hm.util.reportFieldError(document.getElementById(opt.errorEl.id), msg);
				} else {
					hm.util.reportFieldError(self._sortedWidget[0], msg);
				}
			}
		},
		_isEmptyStr: function(str) {
			if(typeof str == 'string' && $.trim(str).length == 0) {
				return true;
			} else {
				return false;
			}
		},
		_splitStr: function(str) {
			if(typeof str == 'string') {
				return str.split(DEF_SEPARATOR_SYMBOL);
			} else {
				return str;
			}
		},
		_hideTdStr: function(item) {
			var tempHideTdCell = item;
			if(tempHideTdCell){
				var length = tempHideTdCell.length;
				for(var i=0;i<length;i++){
					tempHideTdCell[i].style.display="none";
				}
			}
		},
		_showTdStr: function(item) {
			var tempShowTdCell = item;
			if(tempShowTdCell){
				for(var i=0;i<tempShowTdCell.length;i++){
					tempShowTdCell[i].style.display="";
				}
			}
		},
		_getItemIndexes: function(items) {
			var self = this;
			
			if($.isArray(items)) {
				
			} else {
				return self._sortedWidget.find("li.item").index(items);
			}
		},		
		_valueFlagKey: null, // use for multi-value group to distinguish which type for values
		_typeArray:  [],
		_currentSel: 0,
		_chooseEl: null,
		_tagValues: [[],[],[]],
		_deviceNames: [],
		_topologyMaps: [],
		_tableWidget: null,
		_sortedWidget: null,
		_url: "classifierTag.action"
		
	});	
	$.extend($.aerohive.classifierTag, {
		version: '1.0.0'
	});	
})(jQuery, window, document);

function showOrderDialog(){
	$("div#classfierHelpPanel").empty();
	$('<p>')
	.append('By default, HiveManager applies the definitions for an object in the following order of priority:<br>')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;1. A definition with three device tags matching the same tags on a device<br>')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;2. A definition with two matching device tags <br> ')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;3. A definition with one matching device tag <br>')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;4. A definition with a device name that matches that of a device <br>')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;5. A definition with a matching map name <br> ')
	.append('&nbsp;&nbsp;&nbsp;&nbsp;6. A global definition <br>')
	.appendTo($("div#classfierHelpPanel"));
	$("input#ahTempClassifierHelpBtn").click();	return;
}

