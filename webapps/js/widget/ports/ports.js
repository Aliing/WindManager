/*!
 * For the Ports Configuration
 * Dependencies:
 *  jQuery,
 *  jQuery UI - selectable,
 *  doT,
 *  json2
 *  
 */
;(function($, window, document, undefined){
	var debug = function(msg) {
		if(window.console && console.debug) {
			if(typeof msg == 'string') {
				//console.debug(msg);
			} else {
				//console.dir(msg);
			}
		}
	};
    var dataKey = '_portConfig', timerKey = '_timer';
    var shrinkableModels = [18, 23]; // which are more the 24-ports
    var DEF_PORT_MODE = ['ETH', 'SFP', 'USB', 'LTE'],
        DEF_PORT_TYPE = [
            {key: 1, name: 'Phone & Data', className: 'ip-port'},
            {key: 2, name: 'Aerohive AP', className: 'ap-port'},
            {key: 3, name: 'Monitor', className: 'monitor-port'},
            {key: 4, name: 'Access', className: 'access-port'},
            {key: 5, name: '802.1Q', className: 't8021q-port'},
            {key: 6, name: 'WAN', className: 'wan-port'}
        ],
        DEF_48_PORTS = [{num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', logo: true, label: true, disabled0: true},
                        {num: 12, type: 'ETH', id: 'ethPortGrp1', startIndex: 1, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 1}, 
                        {num: 12, type: 'ETH', id: 'ethPortGrp2', startIndex: 13, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 13}, 
                        {num: 12, type: 'ETH', id: 'ethPortGrp3', startIndex: 25, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 25},
                        {num: 12, type: 'ETH', id: 'ethPortGrp4', startIndex: 37, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 37},
                        {num: 4, type: 'SFP', id: 'sfpPortGrp', startIndex: 1, vertical: true, className: 'sfp-port', titlePrefix: 'SFP1/', label: true, labelStartIndex: 49}],
        DEF_24_PORTS = [{num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', logo: true, label: true},
                        {num: 12, type: 'ETH', id: 'ethPortGrp1', startIndex: 1, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 1}, 
                        {num: 12, type: 'ETH', id: 'ethPortGrp2', startIndex: 13, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 13}, 
                        {num: 4, type: 'SFP', id: 'sfpPortGrp', startIndex: 1, vertical: true, className: 'sfp-port', titlePrefix: 'SFP1/', label: true, labelStartIndex: 25}],
	    DEF_24_PORTS_disabled_USB = [{num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', logo: true, label: true, disabled0: true},
	                    {num: 12, type: 'ETH', id: 'ethPortGrp', startIndex: 1, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 1}, 
	                    {num: 12, type: 'ETH', id: 'ethPortGrp', startIndex: 13, vertical: true, className: 'basic-port', titlePrefix: 'Eth1/', label: true, labelStartIndex: 13}, 
	                    {num: 4, type: 'SFP', id: 'sfpPortGrp', startIndex: 1, vertical: true, className: 'sfp-port', titlePrefix: 'SFP1/', label: true, labelStartIndex: 25}],
        DEF_5_PORTS = [{num: 4, type: 'ETH', id: 'ethPortGrp', startIndex: 1, className: 'basic-port', titlePrefix: 'Eth', label: true, labelStartIndex: 1},
                       {num: 1, type: 'ETH', id: 'ethPortGrp0', startIndex: 0, className: 'basic-port', titlePrefix: 'Eth', disabled0: true, disabled0Class: 'wan-port-passive', label: true, labelStartIndex: 0},
                       {num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', label: true}],
        DEF_5_PORTS_disabled_USB = [{num: 4, type: 'ETH', id: 'ethPortGrp', startIndex: 1, className: 'basic-port', titlePrefix: 'Eth', label: true, labelStartIndex: 1},
                                    {num: 1, type: 'ETH', id: 'ethPortGrp0', startIndex: 0, className: 'basic-port', titlePrefix: 'Eth', disabled0: true, disabled0Class: 'wan-port-passive', label: true, labelStartIndex: 0},
                                    {num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', label: true, disabled0: true}],
        DEF_5_PORTS_disabled_USB_LTE = [{num: 4, type: 'ETH', id: 'ethPortGrp', startIndex: 1, className: 'basic-port', titlePrefix: 'Eth', label: true, labelStartIndex: 1},
                                   {num: 1, type: 'ETH', id: 'ethPortGrp0', startIndex: 0, className: 'basic-port', titlePrefix: 'Eth', disabled0: true, disabled0Class: 'wan-port-passive', label: true, labelStartIndex: 0},
                                   {num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', label: true, disabled0: true},
                                   {num: 1, type: 'LTE', id: 'ltePorgGrp', startIndex: 0, className: 'lte-port', titlePrefix: 'LTE', label: true, disabled0: true}],
        DEF_2_PORTS = [{num: 2, type: 'ETH', id: 'ethPortGrp', startIndex: 0, className: 'basic-port', titlePrefix: 'Eth', disabled0: true, disabled0Class: 'wan-port-passive', label: true, labelStartIndex: 0},
                       {num: 1, type: 'USB', id: 'usbPorgGrp', startIndex: 0, className: 'usb-port', titlePrefix: 'USB', label: true, disabled0: true}];
    var PORT_CLASS = {
    		SELECTED: 'ui-selected',
    		UP: 'labelUp',
    		TOP: 'top'
    };
    var defCache = {
        elementId: null,
        defSelectedGroup:  {
            ports:{
                    ETH: [], //port number array
                    SFP: [], 
                    USB: []
                },
            groupNum: -1, // 1xx format
            portType: -1,
            accessProfileId: -1,// for drawer
            portChannel: 0 // for drawer
        },
        defSelectedPort: {
        	accessProfileId: -1,
            groupNum: -1,
            portType: -1,
            port: -1,
            ETH: false,
            SFP: false,
            USB: false
        },
        selectedGroup: null,
        selectedPort: null,
        allPortGroups: [],// element like selectedGroup
        profileContents: []
    };
    var emptyFn = function(){};
	var methods = {
		init : function(options) {
			var defaults = {
					version: '1.0',
                    contentId: null, // current element id attached on
					tmplId: 'portGroupTmpl',
					portNum: 0, // 24, option for group
					deviceModels: [17], //SR24, option for group
					deviceType: 4, // switch, option for group
					mode: 0, // view-0, port-1, device-2, policy-3, monitor-4, drawer-5
					url: "portConfigure.action",
					innerHTMLContentId: "profilesSection", // for port-1, dynamic content page
					editEvent: {editableFn: emptyFn, uneditableFn: emptyFn}, // for port-1, drawer-5
					afterClickEvent: {select: emptyFn, deselect: emptyFn}, // for monitor-4
					mouseEvent: {enter: emptyFn, leave: emptyFn}, // for monitor-4
					templateProfileId: null, // for drawer-5
					dropdownBoxId: null, // for drawer-5, deprecated!
					tooltip: null, // for drawer-5, null || true || elementId
					hoverDelay: 500, // for drawer-5, hover delay invoke after specific ms
					portChannel: null, // for drawer-5, [chk: 'checkboxId', input: 'inputBoxId']
					maximumPortChannel: 30, // for drawer-5
					errorFn: null// for drawer-5
			};

			return this.each(function() {
				var $this = $(this), 
					data = $this.data(dataKey);

				// If the plugin hasn't been initialized yet
				if (!data) {
					/*
					 * Do more setup stuff here
					 */
					$this.data(dataKey, {
						target: $this,
				        settings: $.extend(true, {}, defaults, options, {contentId: this.id}),
				        cache: $.extend(true, {}, defCache)
					});
                    
                    var settings = $this.data(dataKey).settings;
    				// create the port group view section
	    			methods._initPortsGroup(settings);
                    methods._initEvent($this);
                    debug("finish init.");
				}
			});
		},
		destroy: function() {
			return this.each(function() {

				var $this = $(this), 
					data = $this.data(dataKey);

				// Namespacing FTW
				methods._clearCache(data.cache);
				$this.removeData(dataKey);
			})
		},
		show : function() {
            var $this = $(this),
	        	data = $this.data(dataKey), 
	        	settings = data.settings;
            $('#'+settings.contentId).show();
		},
		hide : function() {
            var $this = $(this),
	        	data = $this.data(dataKey), 
	        	settings = data.settings;
            $('#'+settings.contentId).hide();
		},
		update: function(groupStatus) {
			// ...
            var $this = $(this),
            	data = $this.data(dataKey),
            	cache = data.cache,
            	settings = data.settings,
            	contentId = settings.contentId;
            debug('update...');
            //debug(groupStatus);
            if($.isArray(groupStatus)) {
                
                cache.allPortGroups = groupStatus;

                for(var i=0; i<cache.allPortGroups.length; i++) {
                    var portGroup = cache.allPortGroups[i];
                    methods._updatePortGroupStatus(contentId, portGroup);
                }
            }
		},
		configure: function(dataParam, callback) {
			debug('start configure.');
            var $this = $(this),
            	data = $this.data(dataKey), 
            	settings = data.settings,
            	cache = data.cache;
            
			var params = {
					operation: 'configure',
					ignore: new Date().getTime()
			};
			if(dataParam) {
				$.extend(params, dataParam);
			}
			var selectedGroup = cache.selectedGroup,
				groupParams = [];
			if(methods._isPortsExsitInGroup(selectedGroup)) {
				var str = JSON.stringify(selectedGroup);
				debug(str+"\n"+str.length);
				groupParams.push(str);
			}
			if(groupParams.length > 0) {
				params.jsonGroups = groupParams; 
				params = $.param(params, true);
				debug(params);
	            $.getJSON(settings.url,
	            		params,
	            		function(data) {
	            	if($.isFunction(callback)) {
	            		callback(data);
	            	}
	            }).error(function(){});
			} else {
				if($.isFunction(settings.errorFn)) {
					settings.errorFn('Please select the ports for the Wired Access Profile.');
				}
			}
		},
		getAccessProfileId: function() {
            var $this = $(this),
            	data = $this.data(dataKey), 
            	cache = data.cache;
            var selectedGroup = cache.selectedGroup,
            	selectedPorts = selectedGroup.ports, 
            	accessProfileId = selectedGroup.accessProfileId,
            	accessIdArray = [];
            
            if(selectedGroup.accessProfileId == -1 && selectedPorts) {
            	for(var j=0; j<DEF_PORT_MODE.length; j++) {
            		var mode = DEF_PORT_MODE[j];
            		if(!$.isArray(selectedPorts[mode]) || selectedPorts[mode].length <= 0) {
            			continue;
            		}
            		
            		for(var k=0; k<selectedPorts[mode].length; k++) {
            			var  tempAccessId = -1;
            			// loop the cache
            			for(var i=0; i<cache.allPortGroups.length; i++) {
            				var ports = cache.allPortGroups[i].ports,
            					accessId = cache.allPortGroups[i].accessProfileId;
            				// exist in the specific group
            				if($.inArray(selectedPorts[mode][k], ports[mode]) >= 0) {
            					tempAccessId = accessId;
            					break;
            				}
            			}
            			if($.inArray(tempAccessId, accessIdArray) == -1) {
            				// push the access Id to
            				accessIdArray.push(tempAccessId);
            			}
            			
            		}
            	}
            	if(accessIdArray.length == 1) {
            		// same access
            		accessProfileId = accessIdArray[0];
            	} else {
            		// none or multi access
            		accessProfileId = -1;
            	}
            }
            debug('get access id = '+accessProfileId);
            return accessProfileId;
		},
		validatePortChannel: function() {
            var $this = $(this), 
            	data = $this.data(dataKey), 
            	settings = data.settings;
            if(settings.mode == 5 && settings.portChannel && settings.portChannel.chk && settings.portChannel.input) {
            	var chkEl = $('#'+settings.portChannel.chk+":checked");
            	if(chkEl.length) {
            		var inputEl = $('#'+settings.portChannel.input);
            		if(inputEl.length) {
            			var num, value = inputEl.val().trim();
            			if(value == '' || (num = parseInt(value)) <= 0 || num > settings.maximumPortChannel) {
            				inputEl.focus();
            				settings.errorFn('Please input valid port-channel number.');
            				return false;
            			}
            		}
            	}
            }
            return true;
		},
		existConfiguredPorts: function () {
			debug('start check whether exist configured ports.');
            var $this = $(this),
            	data = $this.data(dataKey), 
            	settings = data.settings,
            	cache = data.cache;
			return methods._isExistConfiguredPortInGroup(settings.contentId, cache.selectedGroup, 
					cache.allPortGroups, settings.portNum);
		},
		getCurrentGroup: function() {
            var $this = $(this), 
            	data = $this.data(dataKey),
            	cache = data.cache;
			return cache.selectedGroup;
		},
		updatePortClaxx: function(updateData) {
			// update the ports class
			/*
			 * {
			 * 		ports: {
			 * 			ETH: [],
			 * 			SFP: [],
			 * 			USB: []
			 * 		},
			 * 		className: 'claxxName'
			 * }
			 */
			var $this = $(this), 
				data = $this.data(dataKey),
				settings = data.settings,
				contentId = settings.contentId;
			
			if($.isArray(updateData)) {
				for(var i=0; i<updateData.length; i++) {
					if(updateData[i].ports && updateData[i].className) {
						for(var j=0; j<DEF_PORT_MODE.length; j++) {
							var mode = DEF_PORT_MODE[j];
							if(updateData[i].ports[mode] && $.isArray(updateData[i].ports[mode])) {
								methods._updatePortsStyle(contentId, mode, updateData[i].ports[mode], updateData[i].className);
							}
						}
					}
				}
			} else {
				if(updateData.ports && updateData.className) {
					for(var j=0; j<DEF_PORT_MODE.length; j++) {
						var mode = DEF_PORT_MODE[j];
						if(updateData.ports[mode] && $.isArray(updateData.ports[mode])) {
							methods._updatePortsStyle(contentId, mode, updateData.ports[mode], updateData.className);
						}
					}
				}
			}
		},
		updatePortColors: function(colorMap) {
			/*
			 * colorMap : [{
			 * 		accessProfileId: 123,
			 * 		color: '#ccc'
			 * }]
			 */
			var $this = $(this), 
				data = $this.data(dataKey),
				settings = data ? data.settings : {},
				cache = data ? data.cache : {},
				contentId = settings.contentId;
			
			if(cache) {
				var groups = cache.allPortGroups, idField = 'accessProfileId';
				if(groups && $.isArray(groups)) {
					for(var i=0; i<groups.length; i++) {
						var group = groups[i], accessId = group[idField];
						if(accessId && accessId !== -1) {
							for(var j=0; j<colorMap.length; j++) {
								if(colorMap[j] && colorMap[j][idField] === accessId) {
									var ports = group.ports;
									for(var m=0; m<DEF_PORT_MODE.length; m++) {
										var mode = DEF_PORT_MODE[m];
										if(ports[mode] && $.isArray(ports[mode])) {
											methods._updatePortsColorLable(contentId, mode, ports[mode], colorMap[j].color);
										}
									}
									break;
								}
							}
						}
					}
				}
			}
		},
		clear: function() {
			// clear the selection status and disable the relative UI
			var $this = $(this), 
				data = $this.data(dataKey),
				settings = data.settings,
				cache = data.cache;
			
			settings.editEvent.uneditableFn();
			methods._clearSelectedStatus(cache, true);
			methods._updatePortChannelUi(settings.portChannel, false, '', true);
		},
		// ============= _private methods ================= //
        _updatePortsStyle: function(contentId, portModeStr, portNumArray, className) {
            if(contentId && $.isArray(portNumArray) && typeof className === 'string') {
                for(var i=0; i<portNumArray.length; i++) {
                    $('#'+contentId).find('#'+portModeStr+'_'+portNumArray[i]).addClass(className);
                }
            }
        },
        _updatePortsColorLable: function(contentId, portModeStr, portNumArray, hexColor) {
            if(contentId && $.isArray(portNumArray) && typeof hexColor === 'string') {
                for(var i=0; i<portNumArray.length; i++) {
                    var $portIcon = $('#'+contentId).find('#'+portModeStr+'_'+portNumArray[i]);
                    if($portIcon.length) {
                    	var $label = $portIcon.find('span.portColor');
                    	if($label.length == 1) {
                    		$label.css('background-color', hexColor);
                    	}
                    }
                }
            }
        },
        _clearCache: function(cache) {
                cache.selectedGroup = null;
                cache.selectedPort = null;
                cache.allPortGroups = null;
                cache.profileContents = null;
        },
        _needShrink: function(deviceModel) {
        	if($.inArray(deviceModel, shrinkableModels) >=0) {
        		return true;
        	}
        	return false;
        },
		_initPortsGroup: function(settings) {
            if(settings) {
            	contentId = settings.contentId;
                debug('start init the ports group');
                // get the options according parameters
                var tmplOptions = methods._getPortOptions(settings);
                debug(tmplOptions);
                
                var shrinkIcons = true;
                /* To keep the same icon for per device template, change both as 25*25
                if(settings.deviceModels.length == 1) {
                	shrinkIcons = methods._needShrink(settings.deviceModels[0]);
                }
                */

                for(var i=0; i<tmplOptions.length; i++) {
                	debug('render '+contentId);
                    var tmplOption = tmplOptions[i];
                    if(settings.mode == 5) {
                    	tmplOption.color = true;
                    } 
                    new Template(settings.tmplId, tmplOption)
                        .render(contentId, true)
                        .done(function(){
                            if(tmplOption.num > 1 && tmplOption.vertical) {
                                var width = ((shrinkIcons ? 25 : 32) + 7) * (tmplOption.num/2);
                                $('#'+contentId).find('ul#'+tmplOption.id).css('max-width', width);
                            }
                        });
                }
            }
		},
		_getPortOptions: function(settings) {
			var tmplOptions = DEF_24_PORTS_disabled_USB;
			if(settings.portNum) {
				// if portsNum exists
				if(settings.portNum === 2) {
					tmplOptions = DEF_2_PORTS;
				} else if (settings.portNum === 5) {
					if(settings.deviceType == 0) {
						tmplOptions = DEF_5_PORTS_disabled_USB;
					} else {
						tmplOptions = DEF_5_PORTS_disabled_USB;
					}
				} else if (settings.portNum === 6) {
					tmplOptions = DEF_5_PORTS_disabled_USB_LTE;
				} else if (settings.portNum === 24) {
					if(settings.deviceType == 4) {
						tmplOptions = DEF_24_PORTS_disabled_USB;
					}
				} else if (settings.portNum === 48) {
					tmplOptions = DEF_48_PORTS;
				}
			} else {
				// if portsNum doesn't exist
				if(settings.deviceType === 0 || settings.deviceType === 1) {
					// ap/branch router
					if($.isArray(settings.deviceModels)) {
						if($.inArray(8, settings.deviceModels) >= 0
								|| $.inArray(9, settings.deviceModels) >= 0) {
							// 2 ports [8, 9]
							tmplOptions = DEF_2_PORTS;
						} else if ($.inArray(11, settings.deviceModels) >= 0 
								|| $.inArray(13, settings.deviceModels) >= 0
								|| $.inArray(14, settings.deviceModels) >= 0) {
							// 5 ports [11, 13, 14]
							if(settings.deviceType == 0) {
								tmplOptions = DEF_5_PORTS_disabled_USB;
							} else {
								tmplOptions = DEF_5_PORTS_disabled_USB;
							}
						} else if ($.inArray(19, settings.deviceModels) >= 0) {
							// 5 ports [19]
							tmplOptions = DEF_5_PORTS_disabled_USB_LTE;
						} else if ($.inArray(23, settings.deviceModels) >= 0) {
							tmplOptions = DEF_48_PORTS;
						}
					}
				} else if(settings.deviceType == 4) {
					if ($.inArray(23, settings.deviceModels) >= 0) {
						tmplOptions = DEF_48_PORTS;
					} else {
						tmplOptions = DEF_24_PORTS_disabled_USB;
					}
				}
			}
			return tmplOptions;
		},
        _initEvent: function($this) {
            var data = $this.data(dataKey),
            	settings = data.settings,
            	cache = data.cache,
            	contentId = cache.elementId = settings.contentId;
            
            debug('init event on ' + contentId+', mode='+settings.mode);
            
            $('#'+contentId)
                .find('ul.port-list li')
                .on({
                	click: function(event) {
                        var self = $(this);
                        if(self.hasClass("disabled")) {
                        	return;
                        }
                        switch(settings.mode){
                        case 1:
                        case 2:
                        case 4:
                        case 5:
                        default:
                        	// do nothing
                        	break;
                        }
                    },
                    mouseenter: function(event) {
                        var self = $(this);
                        switch(settings.mode){
                        case 4:
                        	methods._initMouseEvent4(self, settings, cache, true);
                        	break;
                        case 5:
                        	// for drawer
                            if(self.hasClass("disabled")) {
                            	return;
                            }
                            methods._initMouseEvent5(self, event, settings, cache, true);
                            break;
                        default :
                        	//debug("no mouse enter event for this mode:"+settings.mode);
                        	break;
                        }
                    },
                    mouseleave: function(event) {
                    	var self = $(this);
                    	switch(settings.mode){
                    	case 4:
                    		methods._initMouseEvent4(self, settings, cache, false);
                    		break;
                    	case 5:
                    		// for drawer
                            if(self.hasClass("disabled")) {
                            	return;
                            }
                            methods._initMouseEvent5(self, event, settings, cache, false);
                            break;
                    	default :
                    		//debug("no mouse leave event for this mode:"+settings.mode);
                    		break;
                    	}
                    }
                });
            
            if(settings.mode == 5) {
            	// selectable only for Drawer - 5
            	$('#'+contentId).bind("mousedown", function(e) {
            		e.metaKey = true;
            	}).selectable({
            		filter: 'li:not(".disabled")',
            		selected: function(event, ui){
            			var self = $(this);
            			methods._selecteEvent5(self, settings, cache, ui.selected.id, true);
            		},
            		unselected: function(event, ui){
            			var self = $(this);
            			methods._selecteEvent5(self, settings, cache, ui.unselected.id, false);
            		}
            	});
            	// external UI
            	methods._initEvent4DrawerMode(settings, cache);
            }
        },
        // ============= For Monitor 4 ===========
        _initMouseEvent4: function(self, settings, cache, enter) {
        	var elId = self.attr('id');
        	
        	debug('mouse event4 on the li '+elId);
        	var curGroup = methods._getCurSelectedGroup(elId, cache);
        	
        	debug(cache.selectedPort);
        	
        	if(settings.mouseEvent) {
        		var events = settings.mouseEvent;
    			if(enter) {
    				if($.isFunction(events.enter)) {
    					events.enter(cache.selectedPort);
    				}
    			} else {
    				if($.isFunction(events.leave)) {
    					events.leave(cache.selectedPort);
    				}
    			}
        	}
        },
        _initClickEvent4: function(self, settings, cache) {
        	var elId = self.attr('id');
        	
        	debug('click event4 on the li '+elId);
        	var curGroup = methods._getCurSelectedGroup(elId, cache);
        	
        	debug(cache.selectedPort);
        	
        	self.toggleClass(PORT_CLASS.SELECTED);
        	if(settings.afterClickEvent) {
        		var events = settings.afterClickEvent;
        		if(self.hasClass(PORT_CLASS.SELECTED)) {
        			if($.isFunction(events.select)) {
        				events.select(cache.selectedPort);
        			}
        		} else {
        			if($.isFunction(events.deselect)) {
        				events.deselect(cache.selectedPort);
        			}
        		}
        	}
        },
        // ============= For Drawer 5 ===========
        _showTooltip: function(self, settings, cache, group, clearStatus) {
			debug('show tooltip for group');
			if(settings.tooltip) {
				var $tooltip = $('#'+settings.tooltip),
					$arrow = $('#'+settings.tooltip+'>div.arrow'),
					position = self.position(),
					isUp = self.find('span').hasClass(PORT_CLASS.UP);
				
				// style the tooltip
				if($tooltip.length) {
					if(isUp) {
						$arrow.removeClass(PORT_CLASS.TOP);
					} else {
						$arrow.addClass(PORT_CLASS.TOP);
					}
					$tooltip
					.css({left: (position.left - $tooltip.outerWidth()/2 + self.width()/2 + 7) + 'px',
						top: isUp ? (position.top - $tooltip.height() - $arrow.height() + 4) + 'px' : (position.top + self.height() + $arrow.height()/2 + 4) + 'px',
						display: 'block'});
				}
				// bind event on tooltip
				$tooltip.unbind('mouseenter mouseleave').hover(
						function(){
							$(this).css('display', 'block');
						},
						function(){
							$(this).css('display', 'none');
						}
				);
				$tooltip.find(':first-child').off('click').click(function(){
					if(clearStatus) {
						methods._clearSelectedStatus(cache, true);
					}
					// select all same port type ports
					cache.selectedGroup = $.extend(true, {}, group);
					// select all ports in cache
            		methods._toggleSelectedStatus(settings.contentId, cache.selectedGroup.ports);
                	// for the external UI
                	methods._updateExternalUI(cache, settings);
                	
                	$(this).parent().css('display', 'none');
				});
			} else {
				debug('need to create a tootip');
			}
        },
        _initMouseEvent5: function(self, event, settings, cache, enter) {
        	var elId = self.attr('id'), 
        		timer = self.data(timerKey) || 0,
        		title = self.attr('title');
        	
        	//debug('mouse event5 on the li '+elId+ (enter ? ", enter":", leave"));
        	
        	clearTimeout(timer);
        	
        	if(settings.portNum == 2) {
        		// no need to show the tooltip
        		return;
        	}
        	
        	if(!self.hasClass(PORT_CLASS.SELECTED)) {
        		if(enter) {
        			debug("only show tooltip on unselected port");
        			var curGroup = methods._getCurSelectedGroup(elId, cache),
        				preGroup = methods._getPreSelectedGroup(cache);
        			
        			if(preGroup) {
        				if(curGroup) {
        					debug('selected other ports, show tooltip and then will merge port together.');
        					var updatingGroup = $.extend(true, {}, preGroup);
        					if(updatingGroup.accessProfileId != curGroup.accessProfileId) {
        						// reset the access profile ID
        						updatingGroup.accessProfileId = -1;
        					}
        		    		for(var i=0; i<DEF_PORT_MODE.length; i++) {
        		    			var mode = DEF_PORT_MODE[i];
        		    			if(curGroup.ports[mode] && $.isArray(curGroup.ports[mode])) {
        		    				if(updatingGroup.ports[mode] === undefined) {
        		    					updatingGroup.ports[mode] = [];
        		    				}
        		    				if($.isArray(updatingGroup.ports[mode])) {
        		    					for(var ii=0; ii<curGroup.ports[mode].length; ii++) {
        		    						if($.inArray(curGroup.ports[mode][ii], updatingGroup.ports[mode]) == -1) {
        		    							// add the new port to previous selected group
        		    							updatingGroup.ports[mode].push(curGroup.ports[mode][ii]);
        		    						}
        		    					}
        		    				}
        		    			}
        		    		}
        					//show tooltip
        					timer = setTimeout(function() {
        	            		methods._showTooltip(self, settings, cache, updatingGroup, true);
        					}, settings.hoverDelay);
        					self.data(timerKey, timer);
        				}
        			} else {
        				if(curGroup) {
        					debug('no selected any port yet.');
        					// avoid the title effect
        					//self.removeAttr('title');
        					
        					//show tooltip
        					var updatingGroup2 = $.extend(true, {}, curGroup);
        					timer = setTimeout(function() {
        	            		methods._showTooltip(self, settings, cache, updatingGroup2, false);
        					}, settings.hoverDelay);
        					self.data(timerKey, timer);
        				}
        			}
        		} else {
        			// hide tooltip
        			debug('hide tooltip');
        			$('#'+settings.tooltip).css('display', 'none');
        		}
        	}
        },
        _selecteEvent5: function(self, settings, cache, elementId, selected) {
        	var elId = elementId;

        	debug('select event5 on the li '+elId+", "+ (selected ? "selected" : "unselected"));
        	
        	if(settings.tooltip) {
        		var timer = self.data(timerKey) || 0;
        		clearTimeout(timer);
        		$('#'+settings.tooltip).css('display', 'none');
        	}
        	
        	var curGroup = methods._getCurSelectedGroup(elId, cache),
        		preGroup = methods._getPreSelectedGroup(cache),
        		field = 'accessProfileId';
        	
        	debug(cache.selectedPort);
            debug('before selected group');
            debug(preGroup);
            debug('current group');
            debug(curGroup);
        	
            if(settings.deviceType == 4 && cache.selectedPort.USB) {
            	settings.errorFn('The USB port is not configurable when the device is functioning as switch.');
            	return;
            }
            if(preGroup) {
            	// the group of selected port is configured
                debug('has selected port group before');
                if(!selected) {
                	debug('cur port is selected port before');
                	// remove the selected port from cache
                	methods._removeSelectedPort5(cache ,cache.selectedPort);
                	
                } else {
                	debug('cur port is not selected port yet');
                	// add the selected port to cache
                	methods._addSelectedPort5(cache, cache.selectedPort);
                }
            } else {
            	debug('not selected port yet');
            	// add the selected port to cache
            	methods._addSelectedPort5(cache, cache.selectedPort);
            }
            
        	debug('after selected');
        	debug(cache.selectedGroup);
        	
        	// for the external UI
        	methods._updateExternalUI(cache, settings);
        },
        _initClickEvent5: function(self, settings, cache) {
        	var elId = self.attr('id');
        	
        	debug('click event5 on the li '+elId);
        	
        	if(settings.tooltip) {
        		var timer = self.data(timerKey) || 0;
        		clearTimeout(timer);
        		$('#'+settings.tooltip).css('display', 'none');
        	}
        	
        	var curGroup = methods._getCurSelectedGroup(elId, cache),
        		preGroup = methods._getPreSelectedGroup(cache),
        		field = 'accessProfileId';
        	
        	debug(cache.selectedPort);
            debug('before selected group');
            debug(preGroup);
            debug('current group');
            debug(curGroup);
        	
            if(settings.deviceType == 4 && cache.selectedPort.USB) {
            	settings.errorFn('The USB port is not configurable when the device is functioning as switch.');
            	return;
            }
            if(preGroup) {
            	// the group of selected port is configured
                debug('has selected port group before');
                if(self.hasClass(PORT_CLASS.SELECTED)) {
                	debug('cur port is selected port before');
                	// remove the selected port from cache
                	methods._removeSelectedPort5(cache ,cache.selectedPort);
                	
                } else {
                	debug('cur port is not selected port yet');
                	// add the selected port to cache
                	methods._addSelectedPort5(cache, cache.selectedPort);
                }
            } else {
            	debug('not selected port yet');
            	// add the selected port to cache
            	methods._addSelectedPort5(cache, cache.selectedPort);
            }
            self.toggleClass(PORT_CLASS.SELECTED);
            
        	debug('after selected');
        	debug(cache.selectedGroup);
        	
        	// for the external UI
        	methods._updateExternalUI(cache, settings);
        },
        _updateExternalUI: function(cache, settings) {
        	// for the external UI
            if(methods._hasSelectedPorts(cache)) {
            	settings.editEvent.editableFn();
            	if(methods._getPortGroupStatus(cache.selectedGroup) == 1) {
            		methods._updatePortChannelUi(settings.portChannel, false, '', false);
            		cache.selectedGroup.portChannel = 0;
            	} else {
            		methods._updatePortChannelUi(settings.portChannel, false, '', true);
            		cache.selectedGroup.portChannel = 0;
            	}
            } else {
            	settings.editEvent.uneditableFn();
            	methods._updatePortChannelUi(settings.portChannel, false, '', true);
            	cache.selectedGroup.portChannel = 0;
            }
        },
        _updatePortChannelUi: function(portChannel, checked, value, disabled) {
        	if(portChannel && portChannel.chk && portChannel.input) {
        		var $chkEl = $('#'+portChannel.chk), $inputEl = $('#'+portChannel.input);
        		$chkEl.attr('checked', value ? checked : false);
        		$chkEl.attr('disabled', disabled ? disabled : false);
        		$inputEl.val(value ? value : '').attr('disabled', value ? !checked : true);
        	}
        },
        _updateDropDownList: function(elementId, value) {
        	var element = $('#'+elementId)[0];
        	debug('try to update dropdown list:'+elementId+" to "+value);
        	if(element && element.options) {
        		for(var index=0; index<element.options.length; index++) {
        			if(element.options[index].value == value) {
        				if(!element.options[index].selected) {
        					element.options[index].selected = true;
        					debug('update dropdown list: '+elementId+', select index='+index);
        					//$('#'+elementId).change();
        				}
        			}
        		}
        	}
        },
        _addSelectedPort5: function(cache, selectedPort) {
        	if(!cache.selectedGroup) {
        		cache.selectedGroup = $.extend(true, {}, cache.defSelectedGroup);
        	}
        	debug("try to add the selected port5.");
        	var group = cache.selectedGroup;
    		for(var i=0; i<DEF_PORT_MODE.length; i++) {
    			var mode = DEF_PORT_MODE[i];
    			if(group.ports[mode] === undefined) {
    				// avoid the undefined value for first selected on the configured ports then try to select another phy ports
    				group.ports[mode] = [];
    			}
    			if(selectedPort[mode] && $.inArray(selectedPort.port, group.ports[mode]) == -1) {
    				group.ports[mode].push(selectedPort.port);
    				if(group.accessProfileId > 0 
    						&& group.accessProfileId != selectedPort.accessProfileId) {
    					group.accessProfileId = -1;
    				}
    				if(group.groupNum > 0 
    						&& group.groupNum != selectedPort.groupNum) {
    					group.groupNum = -1;
    				}
    				break;
    			}
    		}
        },
        _removeSelectedPort5: function(cache, selectedPort) {
            var group = cache.selectedGroup;
            if(group) {
                debug('try to remove the selected port5.');
                for(var i=0; i<DEF_PORT_MODE.length; i++) {
                    var mode = DEF_PORT_MODE[i],
                        index = $.inArray(selectedPort.port, group.ports[mode]);
                    if(selectedPort[mode] && index >= 0) {
                        group.ports[mode].splice(index, 1);
                    }
                }
            }
        },
        _initEvent4DrawerMode: function(settings, cache) {
            if(settings.mode == 5 && settings.portChannel && settings.portChannel.chk && settings.portChannel.input) {
            	debug('init event on '+settings.portChannel.chk+" and "+settings.portChannel.input);
            	$('#'+settings.portChannel.chk).click(function(){
            		if(this.checked) {
            			$('#'+settings.portChannel.input).attr('disabled', false);
            		} else {
            			$('#'+settings.portChannel.input).val('').attr('disabled', true);
            			//remove the field
                		var group = cache.selectedGroup;
                		if(group) {
                			group.portChannel = 0;
                		}
            		}
            	});
            	$('#'+settings.portChannel.input).change(function(){
            		var value = $(this).val().trim(), 
            			group = cache.selectedGroup;
            		debug('the input value is change, '+value);
            		if(null == group) {
            			return;
            		}
            		var num;
    				if(value != '' && (num = parseInt(value)) > 0 && num <= settings.maximumPortChannel) {
    					debug('validate port channel, '+num);
    					group.portChannel = num; 
    				} else {
    					$('#'+settings.portChannel.input).focus();
    					//$('#'+settings.portChannel.input).val('');
    					settings.errorFn('Please input validate port-channel number.');
    					return;
    				}
    				debug(cache.selectedGroup);
    				// update port channel
                    if(cache.allPortGroups && $.isArray(cache.allPortGroups)) {
                        for(var i=0; i<cache.allPortGroups.length; i++) {
                        	var cachePortsGroup = cache.allPortGroups[i],
                        		cachePorts = cachePortsGroup.ports;
                        	
                            if(group.accessProfileId != -1 
                            		&& group.accessProfileId == cachePortsGroup.accessProfileId) {
                            	cachePortsGroup.portChannel = group.portChannel;
                            	break;
                            }
                        }
                    }
            	});
            }
        },
        // ======== _global method==========
        _getPreSelectedGroup: function(cache) {
            if(cache.selectedGroup && cache.selectedGroup.ports) {
                var ports = cache.selectedGroup.ports;
                for(var i=0; i<DEF_PORT_MODE.length; i++) {
                    // adjust the ports value is not empty
                    var mode = DEF_PORT_MODE[i];
                    if(ports[mode] && $.isArray(ports[mode]) && ports[mode].length>0) {
                        return cache.selectedGroup;
                    }
                }
            } else {
                // init selected group
                cache.selectedGroup = $.extend(true, {}, cache.defSelectedGroup);
            }
            return null;
        },
        _hasSelectedPorts: function(cache) {
        	return methods._isPortsExsitInGroup(cache.selectedGroup);
        },
        _isPortsExsitInGroup: function(group) {
        	if(group && group.ports) {
        		var ports = group.ports;
        		for(var i=0; i<DEF_PORT_MODE.length; i++) {
        			// adjust the ports value is not empty
        			var mode = DEF_PORT_MODE[i];
        			if(ports[mode] && $.isArray(ports[mode]) && ports[mode].length>0) {
        				return true;
        			}
        		}
        	}
        	return false;
        },
        _isExistConfiguredPortInGroup: function(containerId, group, groups, ethPortNum) {
        	// no exist - false,
        	// configured but no agg ports - true,
        	// configured and also agg ports - {channel : [port1, port2], ...} = {1: [1, 26], ...}.
        	var flag = false;
        	if(group && group.ports) {
        		var ports = group.ports, curChannel = group.portChannel;
        		
        		// check whether flag is equals 1
        		label1: for(var i=0; i<DEF_PORT_MODE.length; i++) {
        			// adjust the ports value is not empty
        			var mode = DEF_PORT_MODE[i];
        			if(ports[mode] && $.isArray(ports[mode])) {
        				for(var j=0; j<ports[mode].length; j++) {
        					var $portEl = $('#'+containerId).find('li#'+mode+'_'+ports[mode][j]);
        					if($portEl.length) {
        						for(var k=0; k<DEF_PORT_TYPE.length; k++) {
        							if(DEF_PORT_TYPE[k] && DEF_PORT_TYPE[k].className && $portEl.hasClass(DEF_PORT_TYPE[k].className)) {
        								flag = true;
        								break label1;
        							}
        						}
        					}
        				}
        			}
        		}
        		
        		if(flag && ethPortNum >= 24 && groups && $.isArray(groups)) {
        			// prompt message for the agg ports
        			flag = {};
        			label2: for(var i=0; i<DEF_PORT_MODE.length; i++) {
        				// adjust the ports value is not empty
        				var mode = DEF_PORT_MODE[i];
        				if(ports[mode] && $.isArray(ports[mode])) {
        					for(var j=0; j<ports[mode].length; j++) {
        						for(var k=0; k<groups.length; k++) {
        							var cacheGroup = groups[k], channel = cacheGroup.portChannel;
        							if(channel && channel > 0 && curChannel !== undefined && curChannel != channel
        									&& cacheGroup.ports && cacheGroup.ports[mode] 
        									&& $.inArray(ports[mode][j], cacheGroup.ports[mode]) >=0) {
        								if(flag[channel] === undefined) {
        									flag[channel] = []; // initialize
        								}
        								// add the ETH port number for SFP port
        								flag[channel].push(ports[mode][j] + (i === 1 ? ethPortNum : 0));
        								flag[channel].sort(function(a, b){return a-b});
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        	return flag;
        },
        _getPortGroupStatus: function(group) {
        	// status: 0-no selected ports|only one selected port|, 
        	// 1-two selected same type ports, 2-differen phycal ports were selected
        	var status = 0;
        	if(group && group.ports) {
        		var ports = group.ports;
        		var existMode = false;
        		for(var i=0; i<DEF_PORT_MODE.length; i++) {
        			// adjust the ports value is not empty
        			var mode = DEF_PORT_MODE[i];
        			if(ports[mode] && $.isArray(ports[mode]) && ports[mode].length>0) {
        				if(existMode) {
        					status = 2;
        					break;
        				}
        				existMode = true;
        				if(status == 0 && ports[mode].length>1) {
        					status = 1;
        					continue;
        				} else if (status == 1) {
        					status = 2;
        					break;
        				}
        			}
        		}
        	}
        	return status;
        },
        _getCurSelectedGroup: function(elementId, cache) {
            if(cache.allPortGroups && $.isArray(cache.allPortGroups)) {
                if(typeof elementId == 'string') {
                    var port = elementId.split('_');
                    if(port.length == 2) {
                        var portMode = port[0],
                            portNum = parseInt(port[1]);

                        // init the selectd port
                        cache.selectedPort = $.extend({}, cache.defSelectedPort);
                        var selectedPort = cache.selectedPort;// pass value via reference

                        // set the basic properties
                        selectedPort.port = portNum;
                        selectedPort[portMode] = true;
                        // set the group properties
                        for(var i=0; i<cache.allPortGroups.length; i++) {
                            var ports = cache.allPortGroups[i].ports;
                            if(ports && ports[portMode] && $.isArray(ports[portMode]) && $.inArray(portNum, ports[portMode]) >= 0) {
                                selectedPort.accessProfileId = cache.allPortGroups[i].accessProfileId;
                                selectedPort.groupNum = cache.allPortGroups[i].groupNum;
                                selectedPort.portType = cache.allPortGroups[i].portType;
                                return cache.allPortGroups[i];
                            }
                        }
                    }
                }
            }
            return null;
        },
        _updateSelectdGroup: function(newGroup, cache, disabledPrompt) {
            if(null === newGroup) {
                return;
            }
            if(cache.elementId) {
                if(!methods._clearSelectedStatus(cache, disabledPrompt)) {
                    return;
                }

                // add class
                if(newGroup.ports) {
                    var ports = newGroup.ports;
                   for(var i=0; i<DEF_PORT_MODE.length; i++) {
                       var mode = DEF_PORT_MODE[i];
                       if(ports[mode] && $.isArray(ports[mode]) && ports[mode].length>0) {
                           for(var j=0; j<ports[mode].length; j++) {
                               $('#'+cache.elementId).find('ul.port-list li#'+mode+'_'+ports[mode][j]).addClass(PORT_CLASS.SELECTED);
                           }
                       }
                   }
                }
            }
            cache.selectedGroup = newGroup;
        },
        _toggleSelectedStatus: function(contentId, ports) {
        	if(ports) {
        		if($.isArray(ports.ETH) && ports.ETH.length > 0) {
        			for(var i=0; i<ports.ETH.length; i++) {
        				$('#'+contentId).find('#'+DEF_PORT_MODE[0]+'_'+ports.ETH[i]).toggleClass(PORT_CLASS.SELECTED);
        			}
        		}
        		if ($.isArray(ports.SFP) && ports.SFP.length > 0) {
        			for(var j=0; j<ports.SFP.length; j++) {
        				$('#'+contentId).find('#'+DEF_PORT_MODE[1]+'_'+ports.SFP[j]).toggleClass(PORT_CLASS.SELECTED);
        			}
        		}
        		if ($.isArray(ports.USB) && ports.USB.length > 0) {
        			for(var k=0; k<ports.USB.length; k++) {
        				$('#'+contentId).find('#'+DEF_PORT_MODE[2]+'_'+ports.USB[k]).toggleClass(PORT_CLASS.SELECTED);
        			}
        		}
        	}
        },
        _clearSelectedStatus: function(cache, disabledPrompt) {
            var flag = true;
            if(!disabledPrompt) {
                flag = confirm('Warning: Previous changes of configuration will lost if change the group type.\nDo you want to continue?');
            }
            if(flag) {
                // clear previous class
                $('#'+cache.elementId).find('ul.port-list li').removeClass(PORT_CLASS.SELECTED);
                // reset the cache
                cache.selectedGroup = null;
                while(cache.profileContents.length > 0) {
                	$('#'+cache.profileContents.pop()).html('');
                }
                
            }
            return flag;
        },
		_updatePortGroupStatus: function(contentId, portGroup) {
            var ports = portGroup.ports;
            
            // update the icons
            if(portGroup.portType > 0) {
                var type = DEF_PORT_TYPE[portGroup.portType - 1];
                methods._updatePortsStyle(contentId, DEF_PORT_MODE[0], ports.ETH, type.className);
                methods._updatePortsStyle(contentId, DEF_PORT_MODE[1], ports.SFP, type.className);
                methods._updatePortsStyle(contentId, DEF_PORT_MODE[2], ports.USB, type.className);
            }
            // select the specific group
            if(portGroup.selected) {
                if($.isArray(ports.ETH) && ports.ETH.length > 0) {
                    $('#'+contentId).find('#'+DEF_PORT_MODE[0]+'_'+ports.ETH[0]).click();
                } else if ($.isArray(ports.SFP) && ports.SFP.length > 0) {
                	$('#'+contentId).find('#'+DEF_PORT_MODE[1]+'_'+ports.SFP[0]).click();
                } else if ($.isArray(ports.USB) && ports.USB.length > 0) {
                	$('#'+contentId).find('#'+DEF_PORT_MODE[2]+'_'+ports.USB[0]).click();
                }
            }
		},
		_updateContentViaInnerHTML: function(contentId, text) {
			if(undefined == set_innerHTML) {
				//dump
				debug("damn, unable to find the innerHTML");
			} else {
				if(set_innerHTML && typeof set_innerHTML == 'function') {
					set_innerHTML(contentId,text);
					debug("greate!!");
				} else {
					debug("damn, unable to set the innerHTML");
				}
			}
		},
		_retrivePortGroupData: function(type, groupNum, settings, cache) {
			debug('start retrive.');
            
			var params = {
					operation: 'retrive',
					portType: type,
					groupNum: groupNum,
					ignore: new Date().getTime()
			};
			debug(params);
            $.getJSON(settings.url,
            		params,
            		function(data) {
            	if(data.succ) {
            		if(data.profiles) {
            			var key;
            			for(key in data.profiles) {
            				debug('the profiles key: ' + key);
            				var profileData = $.extend({}, data.profiles[key]);
            				new Template(key+'Tmpl', profileData).render(key+'Section');
            				// add the id of content to cache
            				cache.profileContents.push(key+'Section');
            			}
            		}
            		// render the page via dynamic plain text
            		$.ajax({url: settings.url, 
            				data: {operation: 'dynamicContent', portType: data.portType, groupNum: data.groupNum},
            				dataType: 'text',
            				success: function(data) {
            					methods._updateContentViaInnerHTML(settings.innerHTMLContentId, data);
            				}});
            	}
            }).error(function(){});
		}
	};

	$.fn.portsConfig = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			debug('Method ' + method + ' does not exist on jQuery.tooltip');
		}    
  };

})(jQuery, window, document);

;(function(window,document,undefined){

    var global = (function(){ return this || (0,eval)('this'); }());

    /* Template */
	global.Template = function (tmplId, data) {
		this.tmplId = tmplId;
		this.data = data;
		this.element = null;
		
		return this;
	}
	Template.prototype = {
			render: function(contentId, append) {
				var self = this;
				self.element = document.getElementById(contentId);
				var tmplEl = document.getElementById(self.tmplId);
				if(self.element && tmplEl) {
					//TODO hide or show the loading icon
					self.element.style.display = 'none';
					
					var fn = doT.template(tmplEl.text);
                    if(append) {
					    self.element.innerHTML += fn(self.data); 
                    } else {
					    self.element.innerHTML = fn(self.data); 
                    }
				}
				self.done();
				return self;
			},
			done: function(execFn) {
				var self = this;
				if(execFn && typeof execFn == 'function') {
					execFn();
				}
				if(self.element) {
					self.element.style.display = '';
				}
			}
	}
}(window,document));
