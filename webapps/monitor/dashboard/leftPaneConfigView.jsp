<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style type="text/css">
<!--
ul.accordionWidget,ul.accordionWidget li {
	margin: 0;
	padding: 0;
}

ul.accordionWidget {
	border: 1px solid #DEDCD5;
	border-bottom-width: 0px;
	background-color: #e4e4e4;
	display: block;
}

div.fixed{
 	position: fixed;
	top: 0px;
	z-index: 999;
}

ul.accordionWidget .accordionHeader {
	cursor: pointer;
	background-color: #EEE;
	padding: 4px 2px 4px 14px;
	font-weight: bold;
	color: #4F4F4F;
	border-bottom: 1px solid #DEDCD5;
}

ul.accordionWidget .accordionHeader:hover {
	background-color: #E8E8E8;
}

ul.accordionWidget .configSubHead {
	padding: 4px 0 4px 14px;
}

ul.accordionWidget .hidden{
	display: none;
}

ul.accordionWidget .accordionContent {
	display: block;
	overflow: auto;
	height: 0px;
	background-color: #FFFFFF;
}

ul.accordionWidget .configWidget {
	line-height: 1.6em;
/* 	height: 1.6em; */
	padding-left: 18px;
	position: relative;
}

ul.accordionWidget .configWidget:hover {
	background: #EFEFEF;
}

ul.accordionWidget .configWidget label{
	display: block;
	cursor: pointer;
}

/* ul.accordionWidget .configWidget:hover .configWidgetDel{
	display: block;
}
 */
ul.accordionWidget .configWidgetDel{
	width: 10px;
	height: 20px;
	display: block;
	cursor: pointer;
	position: absolute;
	right: 5px;
	bottom: 0px;
	background: url('<s:url value="/images/jquery/ui-icons_222222_256x240.png" includeParams="none"/>') no-repeat scroll -180px -95px transparent;
}

ul.accordionWidget li {
	position: relative;
	overflow: hidden;
}

ul.accordionWidget .accordionHeaderP {
	background: #EEE url('<s:url value="/yui/treeview/assets/sprite-menu.gif" includeParams="none"/>') no-repeat scroll -8px -313px;
}

ul.accordionWidget .accordionHeaderM {
	background: #EEE url('<s:url value="/yui/treeview/assets/sprite-menu.gif" includeParams="none"/>') no-repeat scroll -8px 4px;
}
-->
</style>

<script type="text/javascript">
var widgetPanel = (function(_$){
	var DOM_ID_PREFIX = "_config";
	var isConfigPaneInitialized = false;
	var height, sh, bh, th, ph;
	var currentTypes;
	var TYPES = [];
	<s:iterator var="groupType" value="lstComponentGroupType">
	TYPES[TYPES.length] = <s:property value="key" />;
	</s:iterator>
	var dataHash = {};
	var activedBoxes = [];//[#_config_2,#_config_4] LI element
	var data = <s:property value="dashbordComponentsJsonString" escapeHtml="false"/>;
	var selected = [5, 6, 7, 8];
	var initializeConfigPane = function(ht){
		height = ht;
		createWidgetTree();
		<%--selectWidgets(selected);--%>
	};
	
	var caculateDimensions = function(ht, itemChanged){
		if(itemChanged){
			var fst = _$("#leftPaneConfigView").offset().top;
			var lst = _$("#div_lst").offset().top;
			bh = lst - fst;
			th = lst;
		}
		if(ht > 0){
			ph = ht;
			sh = (ph < bh) ? 0 : ph - bh;
		}else{
			var vpHeight = YAHOO.util.Dom.getViewportHeight();
			sh = (vpHeight < th) ? 0 : vpHeight - th;
			ph = sh + bh;
		}
	};

	var initializeConfigPaneLayout = function(){
		debug("need to increate height: " + sh);
		_$("#leftPaneConfigView").css("height", (ph-2)+"px");
		bindHeaderClickEvent();
		bindScrollEvent();
		// set flag
		isConfigPaneInitialized = true;
	};
	
	var triggerItem = function(availableTypes){
		_$('#'+DOM_ID_PREFIX+"_"+availableTypes[0]).find('.accordionHeader').trigger('click');
	};
	
	var resetConfigPaneDispaly = function(availableTypes, ht){
		if(activedBoxes.length > 0){
			_$.each(activedBoxes, function(index, value){
				_$(value).find('.accordionContent').not(':animated').css("height", "0px");
				toggleConfigHeadIcon(_$(value).find('.accordionHeader'), true);
			});
			activedBoxes = [];
		}
		_$.each(TYPES, function(index, _TYPE){
			if(_$.inArray(_TYPE, availableTypes) > -1){
				_$('#'+DOM_ID_PREFIX+"_"+_TYPE).css("display", "block");
			}else{
				_$('#'+DOM_ID_PREFIX+"_"+_TYPE).css("display", "none");
			}
		});
		_$("#leftPaneConfigView").css("height", "auto");
		caculateDimensions(ht, true);
		_$("#leftPaneConfigView").css("height", (ph-2)+"px");
	};

	var resizeLayout = function(ht){
		if(!isConfigPaneInitialized){
			height = ht || height;
			return;
		}
		caculateDimensions(ht, false);
		_$("#leftPaneConfigView").css("height", (ph-2)+"px");
		var ac = activedBoxes.length;
		_$.each(activedBoxes, function(index, value){
			_$(value).find('.accordionContent').not(':animated').css("height", sh/ac+"px");
		});
	};

   	var resizeWidth = function(width){
   		_$("#leftPaneConfigView").css("width", width+"px");
   	};

	var bindHeaderClickEvent = function(){
		// register click listener
		_$('#leftPaneConfigView .accordionHeader').click(function(e){
			debug("actived id boxes before click: " + activedBoxes);
			var li = _$(this).closest('li');
			var oo = li.find('.accordionContent');
			if(_$.inArray("#"+li.attr("id"), activedBoxes) > -1){
				debug("clicked element is already actived.");
				if(activedBoxes.length == 2){
					if(activedBoxes[0]=="#"+li.attr("id")){
						activedBoxes.shift();
					}else{
						activedBoxes.pop();
					}
					var exo = activedBoxes[0];
					oo.not(':animated').animate({height: "0px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(li).find('.accordionHeader'),true)});
					_$(exo).find('.accordionContent').not(':animated').animate({height: sh + "px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(exo).find('.accordionHeader'),false)});
				}
				return;
			}
			if(activedBoxes.length == 0){
				oo.not(':animated').animate({height: sh+"px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(li).find('.accordionHeader'),false)});
			}else if(activedBoxes.length == 1){
				var eo = activedBoxes[0];
				_$(eo).find('.accordionContent').not(':animated').animate({height: sh/2 + "px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(eo).find('.accordionHeader'),false)});
				oo.not(':animated').animate({height: sh/2 +"px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(li).find('.accordionHeader'),false)});
			}else{
				var co = activedBoxes.shift();
				_$(co).find('.accordionContent').not(':animated').animate({height: "0px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(co).find('.accordionHeader'),true)});
				oo.not(':animated').animate({height: sh/2 +"px"}, {complete: (function(e,c){return function(){toggleConfigHeadIcon(e,c)}})(_$(li).find('.accordionHeader'),false)});
			}
			activedBoxes.push("#"+li.attr("id"));
			debug("actived id boxes after click: " + activedBoxes);
		});
	};

	var bindScrollEvent = function(){
		var fst = _$("#leftPaneConfigView").offset().top;
		_$(window).scroll(function(){
			var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
			if(scrollTop > fst){
				_$("#leftPaneConfigView").addClass("fixed");
			}else{
				_$("#leftPaneConfigView").removeClass("fixed");
			}
		});
	};

	var toggleConfigHeadIcon = function(accordionHeader, collapse){
		if(collapse){
			_$(accordionHeader).removeClass("accordionHeaderM");
			_$(accordionHeader).addClass("accordionHeaderP");
		}else{
			_$(accordionHeader).removeClass("accordionHeaderP");
			_$(accordionHeader).addClass("accordionHeaderM");
		}
	}

	var toggleConfigPaneDispaly = function(availableTypes, ht){
		if(isConfigPaneInitialized == false){
			initializeConfigPaneLayout();
		}
		if(undefined == availableTypes){
			availableTypes = TYPES;
		}
		if(currentTypes == undefined || [].concat(currentTypes).sort().join(",") != [].concat(availableTypes).sort().join(",")){
			resetConfigPaneDispaly(availableTypes, ht);
			triggerItem(availableTypes);
		}
		currentTypes = availableTypes;
	};

	var createWidgetTree = function(){
		_$.each(data, function(index, value){
			dataHash["_"+value.id] = value;
			buildWidget(value);
		});
	};

	var buildWidget = function(widgetObject){
		var type = widgetObject.t;
		var subType = widgetObject.st;
		var belong = [];
		_$.each(TYPES, function(index, _TYPE){
			if((type & _TYPE) > 0){
				var subTypeId = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType;
				if(_$("#" + subTypeId).hasClass("hidden")){
					// show sub type element
					toggleSubTypeElement(subTypeId, true);
				}
				// append to sub type element
				buildWidgetElement(_TYPE, subType, widgetObject);
				belong[belong.length] = _TYPE;
			}
		});
		return belong;
	};

	var toggleSubTypeElement = function(subTypeId, isShow){
		debug("toggle sub type id: " + subTypeId + ", isShow: " + isShow);
		if(isShow){
			_$("#" + subTypeId).removeClass("hidden");
		}else{
			_$("#" + subTypeId).addClass("hidden");
		}
	};

	var buildWidgetElement = function(_TYPE, subType, widgetObject){
		var parentId = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType;
		var parent = _$("#" + parentId);
		var id = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType + "_" + widgetObject.id;
		debug("create widget: " + widgetObject.n + ", parent node: " + parentId);
		var delhtml = "";
		var disabled = "";
		<s:if test="%{writePermission}">
		if(widgetObject.del){
			delhtml = "<div title='<s:text name="button.remove"></s:text>' class='configWidgetDel' onclick='widgetPanel.removeWidget("+widgetObject.id+");'></div>";
		}
		</s:if>
		<s:else>
		disabled = " disabled";
		</s:else>
		var titlehtml = "";
		if(widgetObject.fn){
			titlehtml = " title='"+widgetObject.fn+"'";
		}
		var chkid = id+"_chk";
		var html = " <div id='"+id+"' class='configWidget'><table cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td valign='top' width='1px'><input type='checkbox' id='"+chkid+"'" + disabled
		 +" onclick='widgetPanel.toggleWidget(this.checked, "+widgetObject.id+");'></td><td><label for='"+chkid+"'"+titlehtml+">"+(widgetObject.fn||widgetObject.n)+"</label>"+delhtml+"</tr></table></div>";
		parent.append(html);
	};

	var findWidgetById = function(widgetId){
		return dataHash["_"+widgetId];
	};

	var getBelongedBoxInfo = function(widgetObject){
		var belongedActivedBoxs = [];
		var belongedInActivedBoxs = [];
		var type = widgetObject.t;
		_$.each(TYPES, function(index, _TYPE){
			if((type & _TYPE) > 0){
				var id = "#" + DOM_ID_PREFIX + "_" + _TYPE;
				if(_$.inArray(id, activedBoxes) > -1){
					belongedActivedBoxs.push(_TYPE);
				}else{
					belongedInActivedBoxs.push(_TYPE);
				}
			}
		});
		return {"a" : belongedActivedBoxs, "ia" : belongedInActivedBoxs};
	};

	var requestToggleWidget = function(isChecked, widgetId){
		debug("widget: " + widgetId + " isChecked? " + isChecked);
		toggleWidgetElement(widgetId, isChecked);
		drawWidgetComponent(widgetId, isChecked);
	};

	var toggleWidgetElement = function(widgetId, isChecked){
		var widgetObject =findWidgetById(widgetId);
		if(widgetObject){
			//change dom checkbox value
			var type = widgetObject.t;
			var subType = widgetObject.st;
			_$.each(TYPES, function(index, _TYPE){
				if((type & _TYPE) > 0){
					var id = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType + "_" + widgetId;
					_$("#" + id + " input[type='checkbox']").attr("checked", isChecked);
				}
			});
		}
	};

	var drawWidgetComponent = function(widgetId, isChecked){
		var dashboardTmp = getCurrentDashboard();
		if (isChecked) {
			var result = dashboardTmp.addNewChart(widgetId);
			if ((typeof result === "string")) {
				uncheckWidgetElement(widgetId);
				showWarnDialog(result);
			} else if (!result) {
				uncheckWidgetElement(widgetId);
			}
		} else {
			dashboardTmp.removeExistChart(widgetId);
		}
	};

	var uncheckWidgetElement = function(widgetId){
		toggleWidgetElement(widgetId, false);
	};

	var selectWidgets = function(selected){
		_$.each(selected, function(index, widgetId){
			toggleWidgetElement(widgetId, true);
		});
	};

	var resetWidgets = function(){
		_$.each(data, function(index, widgetObject){
			toggleWidgetElement(widgetObject.id, false);
		});
	};

	var reselectWidgets = function(selected){
		resetWidgets();
		selectWidgets(selected);
	};

	var syncSelectWidgetElement = function(){
		var dashboard = getCurrentDashboard();
		var current = dashboard ? dashboard.getAllReportIds() : null;
		debug("select widget: " + current);
		reselectWidgets(current || []);
	};

	var lunchConfirmDialog = function(sucHandler){
	    var mybuttons = [ { text:"Yes", handler: function(){this.hide();sucHandler();}},
	                      { text:"Cancel", handler: function(){this.hide();}, isDefault:true} ];
	    var warningMsg = "<s:text name="info.da.widget.delete.confirm"></s:text>";
	    var confirmDialog = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
	    confirmDialog.show();
	};

	var requestRemoveWidgetElement = function(widgetId){
		lunchConfirmDialog(function(){removeWidgetElementCall(widgetId)});
	};

	var removeWidgetElementCall = function(widgetId){
		<%--removeWidgetElement(widgetId);--%>
		var url = "recurReport.action?operation=removeDashboardComponent&id="+widgetId+"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : removeWidgetElementCallBack, timeout: 60000, argument: [widgetId]});
	};

	var removeWidgetElementCallBack = function(o){
		eval("var data = " + o.responseText);
		var widgetId = o.argument[0];
		if(data.s){
			removeWidgetElement(widgetId);
			getCurrentDashboard().removeExistChart(widgetId);
		}else{
			showWarnDialog(data.m);
		}
	};

	var removeWidgetElement = function(widgetId){
		var widgetObject =findWidgetById(widgetId);
		if(widgetObject){
			var type = widgetObject.t;
			var subType = widgetObject.st;
			_$.each(TYPES, function(index, _TYPE){
				if((type & _TYPE) > 0){
					//remove
					var id = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType + "_" + widgetId;
					_$("#"+id).remove();
					//check if sub type need to hide
					var subTypeId = DOM_ID_PREFIX + "_" + _TYPE + "_" + subType;
					if(_$("#" + subTypeId).children().length == 1){
						// not show sub type element
						toggleSubTypeElement(subTypeId, false);
					}
				}
			});
			//TODO remove from data hash
		}
	};

	var requestAddWidgetElement = function(widgetObject){
		var name = widgetObject.n;
		var type = widgetObject.t;
		var id = widgetObject.id;
		debug("new widget name: " + name + ", type: " + type + ", db id: " + id);
		data[data.length] = widgetObject;
		dataHash["_"+id] = widgetObject;
		var belong = buildWidget(widgetObject);
		if(belong.length > 0){
			toggleWidgetElement(id, true);
			var info = getBelongedBoxInfo(widgetObject);
			debug("new widget belong actived box size: "+ info.a.length + ", inactived box size: " + info.ia.length);
			if(info.a.length == 0 && info.ia.length > 0){
				var _TYPE = info.ia[0];
				debug("try to open box: " + _TYPE);
				var boxId = "#" + DOM_ID_PREFIX + "_" + _TYPE;
				_$(boxId).find('.accordionHeader').trigger('click');
			}else{
				var boxId = "#" + DOM_ID_PREFIX + "_" + belong[0];
			}
			//make the added element scroll into view
			_$(boxId).find('.accordionContent').animate({scrollTop: _$(boxId).find('.accordionContent')[0].scrollHeight}, 1000);
		}
	};

	return {
		init: initializeConfigPane,
		toggleDisplay: toggleConfigPaneDispaly,
		toggleWidget: requestToggleWidget,
		removeWidget: requestRemoveWidgetElement,
		addWidget: requestAddWidgetElement,
		uncheckWidget: uncheckWidgetElement,
		syncWidgetSelection: syncSelectWidgetElement,
		resizeLayout: resizeLayout,
		resizeWidth: resizeWidth
	};
})(jQuery);

</script>
<div id='leftPaneConfigView'>
<ul class="accordionWidget">
<s:iterator var="groupType" value="lstComponentGroupType">
	<li id='_config_<s:property value="key" />'>
		<div class='accordionHeader accordionHeaderP'><s:property value="value" /></div>
		<div class='accordionContent'>
			<s:iterator var="groupSubType" value="lstComponentGroupSubType">
			<div id='_config_<s:property value="%{#groupType.key}" />_<s:property value="%{#groupSubType.key}" />' class='hidden'><div class='configSubHead'><s:property value="%{#groupSubType.value}" /></div></div>
			</s:iterator>
		</div>
	</li>
</s:iterator>
</ul>
</div>
<div id="div_lst" style="width: 1px; height: 0px; font-size: 1px;">
</div>