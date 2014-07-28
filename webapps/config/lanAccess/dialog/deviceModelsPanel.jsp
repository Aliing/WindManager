<%@taglib prefix="s" uri="/struts-tags"%>
<style>
div.deviceModels {
    padding: 5px 0;
    cursor: pointer;
}
div.deviceModels * {
    margin: 0;
    padding: 0;
}
div.deviceModels ul {
    list-style: none;
    text-align: left;
}
div.deviceModels li {
    padding-left: 20px;
    line-height: 1.5em;
    border-top: 3px solid white;
}
div.deviceModels li.selected {
    background: url("images/check.png") no-repeat scroll 0 -2px #ffc20e;
}
</style>
<div id="deviceModelSelection" style="display: none;">
    <div class="hd">Device Models</div>
    <div class="bd">
        <div id="deviceListSection"></div>
        <div id="fe_errorDeviceSection" style="display: none;">
            <div class="noteError" id="textfe_errorDeviceSection">ChangeMe</div>
        </div>
    </div>
    <div class="ft" style="font-size: 100%; text-align: center;">
        <input type="button" id="dmsOkBtn" value="<s:text name="common.button.ok"/>"/>
        <input type="button" id="dmsCancelBtn" value="<s:text name="common.button.cancel"/>"/>
    </div>    
</div>
<script id="deviceListTmpl" type="text/x-dot-template">
    {{~it.array :models:index}}
    <div class="deviceModels">
        <p>{{=models.title}}</p>
        <ul class="port{{=models.portsNum}}" ref="{{=models.portsNum}}">
            {{~models.devices :device:j}}
            <li id="{{=device.model}}">{{=device.name}}</li>
            {{~}}
        </ul>
    </div>
    {{~}}
</script>
<script id="devicesTypeTmpl" type="text/x-dot-template">
    <input type="hidden" name="deviceModels" value="{{=it.deviceModels}}" id="portConfigure_deviceModels"/>
    <input type="hidden" name="portNum" value="{{=it.portNum}}" id="portConfigure_portNum"/>
    <label>{{=it.deviceNames}}</label>, when functioning as 
    <select name="deviceType" style="width: 70px;" onchange="emptyPortConfigration(this);">
    {{~it.deviceTypes :deviceType:index}}
    {{ if(it.switchOnly) { }}
    {{? deviceType.type == 4}}
    <option value="{{=deviceType.type}}">{{=deviceType.name}}</option>
    {{?}}
    {{ } else if (it.routerOnly) { }}
    {{? deviceType.type == 1}}
    <option value="{{=deviceType.type}}">{{=deviceType.name}}</option>
    {{?}}
    {{ } else if (it.wirelessOnly) { }}
    {{? deviceType.type == 0}}
    <option value="{{=deviceType.type}}">{{=deviceType.name}}</option>
    {{?}}
    {{ } else if (it.noWireless) { }}
    {{? deviceType.type == 1 || deviceType.type == 4}}
    <option value="{{=deviceType.type}}" {{? deviceType.type == it.deviceType}}selected{{?}}>{{=deviceType.name}}</option>
    {{?}}
    {{ } else if (it.noRouter) { }}
    {{? deviceType.type == 0 || deviceType.type == 4}}
    <option value="{{=deviceType.type}}" {{? deviceType.type == it.deviceType}}selected{{?}}>{{=deviceType.name}}</option>
    {{?}}
    {{ } else if (it.noSwitch) { }}
    {{? deviceType.type == 0 || deviceType.type == 1}}
    <option value="{{=deviceType.type}}" {{? deviceType.type == it.deviceType}}selected{{?}}>{{=deviceType.name}}</option>
    {{?}}
    {{ } else { }}
    <option value="{{=deviceType.type}}" {{? deviceType.type == it.deviceType}}selected{{?}}>{{=deviceType.name}}</option>
    {{ } }}
    {{~}}
    </select>
</script>
<script>
var DMPANEL = (function($, YAHOO){
	var SUPPORT_SWITCH = 1, SUPPORT_ROUTER = 2, SUPPORT_AP = 3, SUPPORT_NO_AP = 4, SUPPORT_NO_ROUTER = 5, SUPPORT_NO_SWITCH = 6; 
	var DEVICE_TYPE_AP = {name: 'AP', type: 0},
	    DEVICE_TYPE_ROUTER = {name: 'Router', type: 1},
	    DEVICE_TYPE_SWITCH = {name: 'Switch', type: 4}; 
	var AP_330 = {name: 'AP330', model: 8, types: [/* DEVICE_TYPE_AP, */ DEVICE_TYPE_ROUTER]},
	    AP_350 = {name: 'AP350', model: 9, types: [/* DEVICE_TYPE_AP, */ DEVICE_TYPE_ROUTER]},
	    BR_100 = {name: 'BR100', model: 11, types: [DEVICE_TYPE_AP, DEVICE_TYPE_ROUTER]},
	    BR_200 = {name: 'BR200', model: 13, types: [DEVICE_TYPE_ROUTER]},
	    BR_200_WP = {name: 'BR200-WP', model: 14, types: [DEVICE_TYPE_ROUTER]},
	    BR_200_LTE_VZ = {name: 'BR200-LTE-VZ', model: 19, types: [DEVICE_TYPE_ROUTER]},
	    SR_24 = {name: 'SR2024', model: 17, types: [DEVICE_TYPE_ROUTER, DEVICE_TYPE_SWITCH]},
	    SR_2024P = {name: 'SR2024P', model: 24, types: [DEVICE_TYPE_ROUTER, DEVICE_TYPE_SWITCH]},
	    SR_2124P = {name: 'SR2124P', model: 22, types: [/*DEVICE_TYPE_ROUTER, */DEVICE_TYPE_SWITCH]},
	    SR_2148P = {name: 'SR2148P', model: 23, types: [/*DEVICE_TYPE_ROUTER, */DEVICE_TYPE_SWITCH]},
	    SR_48 = {name: 'SR2048P', model: 18, types: [DEVICE_TYPE_SWITCH]};
	var ports2Models = {title: '2-Port Devices', portsNum: 2, devices: [AP_330, AP_350]},
	    ports5Models = {title: '5-Port Devices', portsNum: 5, devices: [BR_100, BR_200, BR_200_WP, BR_200_LTE_VZ]},
	    ports5Models2 = {title: '5-Port Devices', portsNum: 5, devices: [BR_100]},
	    ports24Models = {title: '24-Port Devices', portsNum: 24, devices: [SR_24,SR_2024P,SR_2124P]},
	    ports24Models2 = {title: '24-Port Devices', portsNum: 24, devices: [SR_24,SR_2024P]}, //Now the 2124p cannot function as router
	    ports48Models = {title: '48-Port Devices', portsNum: 48, devices: [SR_2148P]};
	var _deviceModelPanel = null,
	    _curSelectedModels = {portsNum: 0, devices: []}, // this filed will render to 'devicesTypeTmpl'
	    _devices = [AP_330, AP_350, BR_100, BR_200, BR_200_WP, BR_200_LTE_VZ, SR_24, SR_2124P,SR_2024P, SR_2148P],
	    _deviceModelsGrp = [ports2Models, ports5Models, ports24Models, ports48Models],
	    _deviceModelsGrp_switchOnly = [$.extend(true, {switchOnly: true}, ports24Models),
	                                   $.extend(true, {switchOnly: true}, ports48Models)],
	    _deviceModelsGrp_routerOnly = [$.extend(true, {routerOnly: true}, ports2Models), 
	                                   $.extend(true, {routerOnly: true}, ports5Models), 
	                                   $.extend(true, {routerOnly: true}, ports24Models2)],//need to rollback fro 2124p
	   _deviceModelsGrp_wirelessOnly = [$.extend(true, {wirelessOnly: true}, ports5Models2)],
	   _deviceModelsGrp_noWireless = [$.extend(true, {noWireless: true}, ports2Models),
	                                  $.extend(true, {noWireless: true}, ports5Models),
	                                  $.extend(true, {noWireless: true}, ports24Models),
	                                  $.extend(true, {noWireless: true}, ports48Models)],
	   _deviceModelsGrp_noRouter = [$.extend(true, {noRouter: true}, ports5Models2),
	                                  $.extend(true, {noRouter: true}, ports24Models),//need to rollback fro 2124p
	                                  $.extend(true, {noRouter: true}, ports48Models)],
	   _deviceModelsGrp_noSwitch = [$.extend(true, {noSwitch: true}, ports2Models),
	                                  $.extend(true, {noSwitch: true}, ports5Models),
	                                  $.extend(true, {noSwitch: true}, ports24Models2)],
	   _currentDeviceModelsGrp = [];
	/**
	*
	* @Param - deviceType, Number, Optional, to limit the device type
	* 
	*/
	var _init = function() {
		debug("init panel");
	    var div = document.getElementById('deviceModelSelection');
	    // remove the anchor HTML element for duplicate panel
	    $('#deviceModelSelection').find("a.container-close").remove();
	    // empty the dialog to reinit
	    //$('#deviceListSection').html('');
	    
	    _deviceModelPanel = new YAHOO.widget.Panel(div, {
	        width: "210px",
	        visible:false,
	        fixedcenter:true,
	        close: true,
	        draggable:false,
	        modal:true,
	        constraintoviewport:true,
	        underlay: "none",
	        zIndex:1
	        });
	    //Allow escape key to close box
	    var escListener = new YAHOO.util.KeyListener(document, { keys:27},
	              { fn:_deviceModelPanel.hide,scope:_deviceModelPanel,correctScope:true } );
	    _deviceModelPanel.cfg.queueProperty("keylisteners", escListener);
	    
	    // init the body
	    _initDeviceModels();
	    new Template('deviceListTmpl', {array: _currentDeviceModelsGrp}).render('deviceListSection');
	    
	    // init event
	    _initEvent();
	    
	    _deviceModelPanel.render(document.body);
	    div.style.display = "";
	};
	var _initEvent = function() {
		// deattach
		$('#deviceListSection').off('click', 'li');
		$('#deviceListSection').off('click', 'p');
		// attach
		$('#deviceListSection').on('click', 'li', function(event) {
			var self = $(this);
			debug("click on "+self.attr('id')+", "+self.text());
			if(_updateSelected(self.parent().attr('ref'), self.attr('id'))) {
				self.toggleClass('selected');
				debug("toggleClass...");
			}
			event.stopPropagation();
		});
		/*
		$('#deviceListSection').on('click', 'p', function(event) {
			var self = $(this);
			debug("click on "+self.attr('id')+", "+self.text());
			var $ulEl = self.next('ul');
			if($ulEl.length) {
				_resetStatus();
				
				var portNum = $ulEl.attr('ref');
				$ulEl.find('li').each(function(){
					var liSelf = $(this);
					if(_updateSelected(portNum, liSelf.attr('id'))) {
						liSelf.toggleClass('selected');
		            }
				});
			}
			event.stopPropagation();
		});
		*/
		$('#dmsOkBtn').off('click');
		$('#dmsCancelBtn').off('click');
		
		new YAHOO.widget.Button("dmsOkBtn");
		$('#dmsOkBtn').on('click', function(event) {
             	var state = _getCurrentState();
             	debug('---- current state ---- ');
             	debug(state);
             	if(state) {
             		if(state.portNum == 24 && state.deviceModels.split(",").length > 1) {
             			hm.util.reportFieldError({id: 'errorDeviceSection'}, 
             					"Can not configure both "+SR_24.name+ " and "+SR_2124P.name+" at the same time.");
             			return;
             		}
             		_deviceModelPanel.hide();
             		if(state.deviceTypes 
             				&& $.isArray(state.deviceTypes)) {
             			// set the switch as default option
             			if($.inArray(DEVICE_TYPE_SWITCH, state.deviceTypes) >= 0) {
             			    state.deviceType = DEVICE_TYPE_SWITCH.type;
             			} else if ($.inArray(DEVICE_TYPE_ROUTER, state.deviceTypes) >= 0) {
             			    state.deviceType = DEVICE_TYPE_ROUTER.type;
             			}
             		}
             		_renderDeviceTypeSection(state);
             	} else {
             		hm.util.reportFieldError({id: 'errorDeviceSection'}, "Please select at least one device models.");
             	}
		});
		new YAHOO.widget.Button("dmsCancelBtn", 
                {onclick: {fn: function() {_deviceModelPanel.hide();}}});
	};
	var _renderDeviceTypeSection = function(data) {
		if(Get('portConfigure_portNum')) {
			var prePortNum = Get('portConfigure_portNum').value;
			debug("prePortNum = "+prePortNum+ ", "+(prePortNum == data.portNum));
			if(prePortNum != data.portNum) {
				emptyPortConfigration();
			}
		}
		/* data struct format
		data = {
			deviceModels: '17, 19', 
			portNum: 24,
			deviceNames: 'SR2024, SR2024P',
			deviceType: 4, // selected option
			deviceTypes: 
			    [{type: '1', name: 'Router'}, 
			    {type: '4', name: 'Switch'}]
		}
		*/
		new Template('devicesTypeTmpl', data).render('devicesTypeSection');
		if(data.portNum == 2) {
			$('#noteAPFunctionSection').show();
		} else {
			$('#noteAPFunctionSection').hide();
		}
	};
	var _getCurrentState = function() {
		var devices = _curSelectedModels.devices;
		if(_curSelectedModels.portsNum && devices.length > 0) {
			var deviceNames = [], deviceModels = [], deviceTypes = [];
			var state = {};
			for(var index = 0; index < devices.length; index++) {
				var device = devices[index];
				//debug(device);
				deviceNames.push(device.name);
				deviceModels.push(device.model);
				
				if(deviceTypes.length == 0) {
					// new object to avoid change the values in source
					deviceTypes = [].concat(device.types);
				} else {
					for(var j=0; j<deviceTypes.length; j++) {
						var type = deviceTypes[j];
						if($.inArray(type, device.types) == -1) {
							deviceTypes.splice(j, 1);
						}
					}
				}
			}
			deviceNames.sort();
			deviceModels.sort();
			
			state.deviceNames = deviceNames.join(); 
			state.deviceModels = deviceModels.join();
			state.deviceTypes = deviceTypes;
			state.portNum = _curSelectedModels.portsNum;
			
			// flag for the select tag element 
			var models = _getModelsByPortsNum(state.portNum);
			if(models.switchOnly) {
				state.switchOnly = models.switchOnly;
			}
			if(models.routerOnly) {
				state.routerOnly = models.routerOnly;
			}
			if(models.wirelessOnly) {
				state.wirelessOnly = models.wirelessOnly;
			}
			if(models.noWireless) {
				state.noWireless = models.noWireless;
			}
			if(models.noRouter) {
				state.noRouter = models.noRouter;
			}
			if(models.noSwitch) {
				state.noSwitch = models.noSwitch;
			}
			return state; 
		}
	};
	var _getDeviceByModel = function(deviceModel) {
		var selectDevice;
        for(var index=0; index<_devices.length; index++) {
            if(deviceModel == _devices[index].model) {
                selectDevice = _devices[index];
                break;
            }
        }
        return selectDevice;
	};
	var _getModelsByPortsNum = function(portsNum) {
		var deviceModels;
        for(var index=0; index < _currentDeviceModelsGrp.length; index++) {
            if(portsNum == _currentDeviceModelsGrp[index].portsNum) {
            	deviceModels = _currentDeviceModelsGrp[index];
                break;
            }
        }
        return deviceModels;
	};
	var _updateSelected = function(portsNum, deviceModel, keepSelection) {
		debug("_updateSelected, portsNum="+portsNum+", deviceModel="+deviceModel+", keepSelection="+keepSelection);
		if(portsNum) {
			var deviceModels = _getModelsByPortsNum(portsNum);
			var selectDevice = _getDeviceByModel(deviceModel);
			if(deviceModels && selectDevice) {
				if(_curSelectedModels.portsNum) {
					// has selected items
					if(deviceModels.portsNum === _curSelectedModels.portsNum) {
						// same ports num
                        var index = $.inArray(selectDevice, _curSelectedModels.devices);
                        if( index == -1) {
                        	if(!keepSelection) {
	                        	// reset before add
	                        	_resetStatus();
                        	}
                            // add to array
                            _curSelectedModels.devices.push(selectDevice);
                        } else {
                            // remove from array
                            _curSelectedModels.devices.splice(index, 1);
                            // reset the portsNum
                            if(_curSelectedModels.devices.length == 0) {
                            	_curSelectedModels.portsNum = 0;
                            } else {
                            	// to handle multi-selection in previous version, deselect others
                            	_resetStatus();
	                            // add to array
	                            _curSelectedModels.devices.push(selectDevice);
                            }
                        }
                        return true;
                    } else {
                    	// different ports num will remove the previous status
                    	// clear the previous status
                    	_resetStatus();
                    	
						_curSelectedModels.portsNum = parseInt(portsNum);
						_curSelectedModels.devices.push(selectDevice);
						return true;
                    }
				} else {
					// empty
					_curSelectedModels.portsNum = parseInt(portsNum);
					_curSelectedModels.devices.push(selectDevice);
                    return true;
				}
			}
		}
		return false;
	};
	var _selectedDevice = function(portsNum, deviceModel) {
		$('#deviceListSection').find('ul[ref='+portsNum+'] li#'+deviceModel).addClass('selected');
	};
	var _resetStatus = function() {
		debug("_resetStatus...");
		$('#deviceListSection').find('li').removeClass('selected');
		_curSelectedModels.devices = [];
	};
	var _initDeviceModels = function() {
		var limitTypeEl = Get('networkPolicyTemplate_configType4Port'), limitType = 0;
		
		//default value
		_currentDeviceModelsGrp = _deviceModelsGrp;
		
		if(limitTypeEl) {
			limitType = limitTypeEl.value;
		    debug("current network policy type is "+limitType);
		    if(limitType) {
	            if(limitType == SUPPORT_SWITCH) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_switchOnly;
	            } else if(limitType == SUPPORT_ROUTER) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_routerOnly; 
	            } else if(limitType == SUPPORT_AP) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_wirelessOnly;
	            } else if(limitType == SUPPORT_NO_AP) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_noWireless;
	            } else if(limitType == SUPPORT_NO_ROUTER) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_noRouter;
	            } else if(limitType == SUPPORT_NO_SWITCH) {
	                _currentDeviceModelsGrp = _deviceModelsGrp_noSwitch;
	            }
		    }
		}
	};
	var debug = function(msg) {
		if(window.console && console.debug) {
			if(typeof msg == 'string') {
				//console.debug(msg);
			} else {
				//console.dir(msg);
			}
		}
	};
	
	var open = function(limitType) {
		debug("open panel...");
		if(null == _deviceModelPanel) {
			_init();
		}
		hm.util.hideFieldError();
		_deviceModelPanel.show();
	};
	
	var update = function(data) {
		debug("update panel...");
		debug(data)
		if(data) {
			var portsNum = data.portsNum, deviceModels = data.deviceModels;
			if($.isArray(deviceModels)) {
               if(null == _deviceModelPanel) {
                   _init();
               }
				for(var i=0; i<deviceModels.length; i++) {
					_updateSelected(portsNum, deviceModels[i], true); // keep the selection status
	                $('#deviceListSection').find("li#"+deviceModels[i]).toggleClass('selected');
				}
                var state = _getCurrentState();
                state.deviceType = data.deviceType;
                debug(state);
                if(state) {
                	_renderDeviceTypeSection(state);
                }
                
			}
		}
	};
	
	return {
		open:  open,
		update: update
	};
}(jQuery, YAHOO));

function emptyPortConfigration(element) {
	YAHOO.util.Dom.setStyle(['interfaceSection', 'loadBalanceSection', 'portsPse', 'monitorSection'], 'display', 'none');
	<s:if test="%{dataSource.id != null}">
	YAHOO.util.Dom.setStyle('noteConfigeSection', 'display', '');
	</s:if>
} 
</script>
