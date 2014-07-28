;(function($){
	var win = window;
	win.AhDataTablePanel = {};
	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, YUA = YAHOO.util.Anim;
	
	var getDisplayString = function(blnShown) {
		if (blnShown) {
			return "block";
		}
		return "none";
	};
	var isDefined = function(value) {
		return typeof value !== "undefined";
	};
	var isString = function(value) {
		return typeof value === "string";
	};
	function isObject(obj) {
		return typeof obj === 'object';
	}
	
	daRnd.today=new Date();
	daRnd.seed=daRnd.today.getTime();
	function daRnd() {
		daRnd.seed = (daRnd.seed*9301+49297) % 233280;
		return daRnd.seed/(233280.0);
	};
	function daRand(number) {
		return Math.ceil(daRnd()*number);
	};
	function daDefRandTr() {
		return daRand(100000);
	};
	function daDefRandTd() {
		return daRand(100000);
	};

	var alterBgColor = function(container,options){
		options=$.extend({
			odd:"odd",
			even:"even",
			selected:"selected"
		},options);

		$("#"+container+" tbody>tr:odd").removeClass();
		$("#"+container+" tbody>tr:even").removeClass();
		$("#"+container+" tbody>tr:visible:odd").addClass(options.odd);
		$("#"+container+" tbody>tr:visible:even").addClass(options.even);
	}
	
	function addClickEventForAllRows(container){
		$("#"+container+" tbody>tr").click(function() {
			var hasSelected = $(this).hasClass('selected');
			$(this)[hasSelected?"removeClass":"addClass"]('selected');
		})
	}
	
	function addClickEventForSingleRow(trEl){
		$(trEl).unbind("click");
		$(trEl).click(function() {
			var hasSelected = $(this).hasClass('selected');
			$(this)[hasSelected?"removeClass":"addClass"]('selected');
		})
	}
	
	var DataTablePanel = function(containerId,columnDefs,dataSource,myConfigs,callback) {
		var self = this,
			container = containerId;
		
		var $el = function(el) {
			return $("#" + el);
		};
		var	$container = $el(container);
		
		var tdEditEls = {};
		var trTdIds = {};
		var gTdIds = {};
		var blnRowEdited = false;
		var rowEditIndex = -1;
		
		//for filter menu
		var filterCondition={};
		
		var genCurTrIdNumber = function() {
			var num = daDefRandTr();
			while (num in trTdIds) {
				num = daDefRandTr();
			}
			return num;
		};
		var getTrIdString = function(num) {
			return "row_edit_tr_" + num;
		};
		var getTrIdNum = function(id) {
			return id.replace("row_edit_tr_", "");
		};
		
		var genCurTdIdNumber = function(trNum) {
			if (gTdIds) {
				var num = daDefRandTd();
				while (num in gTdIds) {
					num = daDefRandTd();
				}
				return num;
			}
		};
		var getTdIdString = function(num) {
			return "row_edit_td_" + num;
		};
		var getTdIdNum = function(id) {
			return id.replace("row_edit_td_", "");
		};
		
		var fields =columnDefs;
		if (fields && fields.length > 0) {
			for (var i = 0; i < fields.length; i++) {
				var classNames = fields[i].classNames || {};
				classNames.displayTd = classNames.displayTd || "ahDataTabelTd";
				if (classNames.displayText) {
					classNames.displayText = "tdText " + classNames.displayText;
				} else {
					classNames.displayText = "tdText";
				}
				if (classNames.edit) {
					classNames.edit = "hdEdit " + classNames.edit;
				} else {
					classNames.edit = "hdEdit";
				}
				fields[i].classNames = classNames;
			}
		}
		var editRowInfo;
		var addDrag = false;
		if(myConfigs){
			editRowInfo = myConfigs.editInfo;
			if(editRowInfo.data){
				rowEditIndex = editRowInfo.data.rowIndex;
			}
			if(myConfigs.dragEvents){
				addDrag = true;
			}
		}

		self.render = function(callbackArg) {
			createCommonOpContainer();
			alterBgColor(container);
			addClickEventForAllRows(container);
		};
		
		self.addRowToDropdownList = function(row){
			for (var key =0; key<fields.length;key++) {
				if(fields[key].type != 'dropdown' && fields[key].type != 'dropdownnew'){
					continue;
				}
				
				if(fields[key].mark == row.mark){		
					var option={};
					option.value = row.value;
					option.label = row.label;
					fields[key].options.push(option);
					break;
				}
			}
		}
		
		var newOptionsForDropDown;
		self.setNewOptionsForDrowdownList = function(parameter){
			newOptionsForDropDown = parameter;
			var trEls = $("#"+container+" tbody>tr");
			if(trEls){
				for(var i=trEls.length-1;i>=0;i--){
					trEls[i].parentNode.removeChild(trEls[i]);
				}
				alterBgColor(container);
				blnRowEdited = false;
			}
		};
		
		self.renderData = function(optionArg) {
			insertData(optionArg);
		};
		self.getRowData = function(){
			var row = $("#"+container+" tbody>tr:eq("+rowEditIndex+")").get(0);
			var editData
			if(rowEditIndex >= 0 && row){
				editData = "{editInfo:{name:\""+ editRowInfo.name + "\", blnNew: " + self.isThisANewRow(row) + ",data:"+"{rowIndex:"+ rowEditIndex +",";
				//var tds = row.getElementsByTagName("td");
				var tds = $(row).find("td");
				if (tds && tds.length > 0) {
					for (var i = 0; i < tds.length; i++) {
						var td = tds[i];
						if(td.id && tdEditEls[td.id]){
							editData+=tdEditEls[td.id].getEditElName()+":[\""+tdEditEls[td.id].getCurValue()+"\",\""+tdEditEls[td.id].getDisplayText()+"\"],"
						}
					}
				}
				editData = editData.substring(0,editData.length-1)
				editData +="}}}";
			}
			
			return editData;
		}
		
		self.alterTrBgColor = function() {
			alterBgColor(container);
		};
		
		self.isThisANewRow = function(trEl) {
			return curEditRows.get(trEl) || false;
		};
		
		self.addCurEditRow = function(trEl) {
			curEditRows.add(trEl);
		};
		var curEditRowsObj = {};
		var curEditRows = {
			add: function(trEl) {
				if (!trEl) {
					return;
				}
				if (isString(trEl)) {
					curEditRowsObj[trEl] = true;
				} else {
					curEditRowsObj[trEl.id] = true;
				}
			},
			remove: function(trEl) {
				if (!trEl) {
					return;
				}
				if (isString(trEl)) {
					delete curEditRowsObj[trEl];
				} else {
					delete curEditRowsObj[trEl.id];
				}
			},
			get: function(trEl) {
				if (!trEl) {
					return;
				}
				if (isString(trEl)) {
					return curEditRowsObj[trEl];
				} else {
					return curEditRowsObj[trEl.id];
				}
			},
			init: function() {
				curEditRowsObj = {};
			}
		};
		self.getCurrentEditRow = function() {
			if (rowEditIndex >= 0) {
				return $container.find("tbody tr:eq(" + rowEditIndex + ")");
			}
		};
		self.cloneRowLine = function(option) {
			if (!option) {
				return;
			}
			var trIdsResult = [];
			var $trEl;
			if (isString(option.trEl)) {
				$trEl = $el(option.trEl);
			} else {
				$trEl = option.trEl;
			}
			var value = self.getRowValues($trEl);
			
			if (option.oriRow === "remove") {
				self.removeRow($trEl);
			}
			
			var values = [];
			var dealKey;
			var dealKeyFunc;
			var texts = {};
			if (option.valueDeal) {
				for (var key in option.valueDeal) {
					dealKey = key;
					dealKeyFunc = option.valueDeal[key].func;
					// support on only one field now
					break;
				}
			}
			if (isDefined(dealKey) && isDefined(dealKeyFunc)) {
				var dealKeyValues = dealKeyFunc(value[dealKey]);
				for (var i = 0; i < dealKeyValues.length; i++) {
					var valueTmp = {};
					valueTmp[dealKey] = dealKeyValues[i];
					if (isObject(valueTmp[dealKey])) {
						texts[valueTmp[dealKey].value] = valueTmp[dealKey].text; 
						valueTmp[dealKey] = valueTmp[dealKey].value;
					}
					values.push($.extend(true, {}, value, valueTmp));
				}
			} else {
				values.push(value);
			}
			
			var dupCheckColumns;
			var dupStrategy;
			if (option.whenExist && option.whenExist.columns) {
				dupCheckColumns = {};
				for (var i = 0; i < option.whenExist.columns.length; i++) {
					dupCheckColumns[option.whenExist.columns[i]] = true;
				}
				dupStrategy = option.whenExist.strategy;
			}
			
			var blnDupDeal = false;
			if (dupStrategy && dupStrategy !== "ignore") {
				blnDupDeal = true;
			}
			var len = values.length;
			var preRows = getCurrentRows();
			for (var i = 0; i < len; i++) {
				var blnNeedAdd = true;
				if (blnDupDeal) {
					var rows = self.getCertainRows(mergeExistValues(dupCheckColumns, values[i]), preRows);
					if (rows) {
						var result = dealWithFoundRows(rows, dupStrategy, values[i]);
						if (result) {
							blnNeedAdd = result.result;
							if (result.ids && result.ids.length > 0) {
								for (var j = 0; j < result.ids.length; j++) {
									trIdsResult.push(result.ids[j]);
								}
							}
						}
					}
				}
				if (blnNeedAdd) {
					var trIdTmp = genCurTrIdNumber();
					prepareTemplateRow(function(trElNew) {
						tbodyEl.appendChild(trElNew);
					}, {
						blnEditMode: false, 
						trNum: trIdTmp,
						datas: values[i],
						dataWithText: texts
					});
					trIdsResult.push(trIdTmp);
				}
			}
			
			return trIdsResult;
		};
		var dealWithFoundRows = function(rows, strategy, data) {
			var result = false;
			var refreshedIds = [];
			for (var i = 0; i < rows.length; i++) {
				if (strategy == "update") {
					self.updateRowData($(rows[i]), data);
					refreshedIds.push(getTrIdNum(rows[i].id));
				} else if (strategy == "remove") {
					self.removeRow($(rows[i]));
					result = true;
				}
			}
			
			return {
				result: result,
				ids: refreshedIds
			};
		};
		var mergeExistValues = function(oriObj, dataObj) {
			var result = $.extend(true, {}, oriObj);
			for (var key in oriObj) {
				oriObj[key] = dataObj[key];
			}
			return oriObj;
		};
		self.updateRowData = function($trEl, data, blnEditable) {
			if (!data) {
				return;
			}
			iterateRowDataTds($trEl, function(rowTd) {
				var editWidget = tdEditEls[rowTd.id];
				if (!editWidget) {
					return;
				}
				var $vinput = $(rowTd).find("input.hdValue");
				var value = data[$vinput.attr("name")] || "";
				var text;
				if (isObject(value)) {
					if ("text" in value) {
						text = value.text;
						/*editWidget.getDisplayText = (function(text) {
							return function() {
								return text;
							};
						})(value.text);*/
					}
					value = value.value;
				}
				editWidget.setCurValue(value, text);
			});
			if (!blnEditable) {
				makeARowEditable($trEl[0], false);
			}
		};
		self.removeRowsWithCondition = function(option) {
			var rows = self.getCertainRows(option);
			if (rows) {
				for (var i = 0; i < rows.length; i++) {
					self.removeRow($(rows[i]));
				}
			}
		};
		self.removeRow = function($trEl) {
			if (isString($trEl)) {
				$trEl = $el($trEl);
			}
			var tdIds = trTdIds[$trEl.attr("id")];
			trTdIds[$trEl.attr("id")] = null;
			if (tdIds && tdIds.length > 0) {
				for (var i = 0; i < tdIds.length; i++) {
					gTdIds[tdIds[i]] = null;
					tdEditEls[tdIds[i]] = null;
				}
			}
			$trEl.remove();
		};
		var getCurrentRows = function() {
			var preRows = [];
			iterateRows(function(){
				preRows.push(this);
			});
			return preRows;
		};
		self.getCertainRows = function(option, preRows) {
			var rows = [];
			if (!preRows) {
				preRows = getCurrentRows();
			}
			for (var i = 0; i < preRows.length; i++) {
				var blnMatch = true;
				for (var key in option) {
					var value = option[key];
					if (isObject(value)) {
						value = value.value;
					}
					if ($(preRows[i]).find("input.hdValue[name="+key+"]").val() != value) {
						blnMatch = false;
						break;
					}
				}
				if (blnMatch) {
					rows.push(preRows[i]);
				}
			}
			if (rows.length === 0) {
				return false;
			}
			return rows;
		};
		self.getRowValues = function($trEl) {
			var values = {};
			if (isString($trEl)) {
				$trEl = $el(trEl);
			}
			iterateRowDataTds($trEl[0], function(rowTd) {
				var $vinput = $(rowTd).find("input.hdValue");
				values[$vinput.attr("name")] = $vinput.val() || "";
			});
			return values;
		};
		self.getRowCertainEditWidget = function(trEl, column) {
			if (!trEl || !column) {
				return;
			}
			var resultWidget;
			iterateRowDataTds(trEl, function(rowTd) {
				if ($(rowTd).find("input.hdValue").attr("name") == column) {
					resultWidget = tdEditEls[rowTd.id];
				}
			});
			return resultWidget;
		};
		
		var insertData = function(dataSource, func) {
			var trIdTmps = [];
			if(editRowInfo && editRowInfo.name){
				var edit = document.createElement('input');
				edit.type = "hidden";
				edit.name = editRowInfo.name;
				edit.id = editRowInfo.name;
				tbodyEl.appendChild(edit);
			}
			if (dataSource
					&& dataSource.length > 0) {
				var len = dataSource.length;
				for (var i = 0; i < len; i++) {
					var trIdTmp = genCurTrIdNumber();
					prepareTemplateRow(function(trElNew) {
						tbodyEl.appendChild(trElNew);
					}, {
						blnEditMode: false, 
						trNum: trIdTmp,
						datas: dataSource[i]
					});
					trIdTmps.push(trIdTmp);
				}
			} else {
				//var trIdTmp = genCurTrIdNumber();
				//tbodyEl.appendChild(prepareTemplateRow(true, trIdTmp));
				//trIdTmps.push(trIdTmp);
			}
			
			if (func) {
				func();
			}
			if (trIdTmps) {
				for (var i = 0; i < trIdTmps.length; i++) {
					var rowId = getTrIdString(trIdTmps[i]);
					if(editRowInfo 
							&& editRowInfo.data 
							&& editRowInfo.data.rowIndex == i){
						makeARowEditable(rowId, true);
						blnRowEdited = true;
						var tdIds = trTdIds[trIdTmps[i]];
						for(var j=0;j<tdIds.length;j++){
							var tdId = getTdIdString(tdIds[j]);
							var textShown = editRowInfo.data[tdEditEls[tdId].getEditElName()];
							var $tdEl = $('#'+tdId);
							var texts = $tdEl.find(".tdText");
							var hiddens = $tdEl.find(".hdValue");
							var selectText = $("#"+tdId+" .selectText").get(0);
							if (isDefined(textShown)) {
								tdEditEls[tdId].setCurValue(textShown[0]);
								if (texts && texts.length > 0) {
									if(textShown[1]){
										texts[0].innerHTML = textShown[1];
									} else {
										texts[0].innerHTML = textShown[0];
									}
								}
								if(hiddens && hiddens.length > 0){
									hiddens[0].value = textShown[0];
								}
								if(selectText){
									selectText.value = textShown[1];
								}
							}
						}
					} else {
						makeARowEditable(rowId, false);
					}
					
				}
			}
			return trIdTmps;
		};
		
		var makeARowEditable = function(rowId, blnEditable,blnCancel,blnNew) {
			iterateRowTds(rowId, function(rowTd) {
				if (tdEditEls[rowTd.id]) {
					if (blnEditable) {
						tdEditEls[rowTd.id].show();
					} else {
						if(blnCancel){
							tdEditEls[rowTd.id].cancel();
						} else {
							tdEditEls[rowTd.id].hide();
						}
					}
				} else if (rowTd.name === "op_td_of_row_edit") {
					var $rowTd = $(rowTd);
					var els = $rowTd.find(".whenView");
					var elsEdit = $rowTd.find(".whenEdit");
					var elsEdit_cancel = $rowTd.find(".whenEdit.edit");
					var elsEdit_del = $rowTd.find(".whenEdit.new");
					if (blnEditable) {
						for (var i = 0; i < els.length; i++) {
							els[i].style.display = "none";
						}
						for (var i = 0; i < elsEdit.length; i++) {
							elsEdit[i].style.display = "inline-block";
						}
						if(blnNew){
							for (var i = 0; i < elsEdit_del.length; i++) {
								elsEdit_del[i].style.display = "inline-block";
							}
							for (var i = 0; i < elsEdit_cancel.length; i++) {
								elsEdit_cancel[i].style.display = "none";
							}
						} else {
							for (var i = 0; i < elsEdit_del.length; i++) {
								elsEdit_del[i].style.display = "none";
							}
							for (var i = 0; i < elsEdit_cancel.length; i++) {
								elsEdit_cancel[i].style.display = "inline-block";
							}
						}
					} else {
						for (var i = 0; i < els.length; i++) {
							els[i].style.display = "inline-block";
						}
						for (var i = 0; i < elsEdit.length; i++) {
							elsEdit[i].style.display = "none";
						}
						for (var i = 0; i < elsEdit_cancel.length; i++) {
							elsEdit_cancel[i].style.display = "none";
						}
						for (var i = 0; i < elsEdit_del.length; i++) {
							elsEdit_del[i].style.display = "none";
						}
					}
				}
			});
		if (blnEditable && myConfigs && myConfigs.whenEditRow) {
                    myConfigs.whenEditRow.apply(self, [getRowElement(rowId)]);
		 }
		};
		
		var getRowElement = function(trEl) {
		 if (isString(trEl)) {
		  return document.getElementById(trEl);
		 }
		 return trEl;
		};
		
		var iterateRowTds = function(trEl, func) {
			if (isString(trEl)) {
				trEl = document.getElementById(trEl);
			}
			//var tds = tdEl.getElementsByTagName("td");
			var tds = $(trEl).find("td");
			if (tds && tds.length > 0) {
				for (var i = 0; i < tds.length; i++) {
					func(tds[i]);
				}
			}
		};
		var iterateRowDataTds = function(trEl, func) {
			if (isString(trEl)) {
				trEl = document.getElementById(trEl);
			}
			var tds = $(trEl).find("td.ahDataTabelTd");
			if (tds && tds.length > 0) {
				for (var i = 0; i < tds.length; i++) {
					func(tds[i]);
				}
			}
		};
		
		var iterateRows = function(func) {
			$('#'+containerId+' tbody>tr').each(function(){
				func.apply(this);
			});
		};
		
		var tbodyEl;
		var createCommonOpContainer = function() {
			var tblEl = document.createElement("table");
			tblEl.style.border = "1px solid black";
			var theadEl = document.createElement("thead");
			theadEl.appendChild(prepareTableHeads());
			tblEl.appendChild(theadEl);
			tbodyEl = document.createElement("tbody");
			tbodyEl.id = container + "_tbody";
			var trIdTmps = insertData(dataSource, function() {
				tblEl.appendChild(tbodyEl);
				document.getElementById(container).appendChild(tblEl);
			});
			
			initMyConfirmDialog();
		};
		
		var prepareTableHeads = function() {
			var trEl = document.createElement("tr");
			var thEl;
			
			//add drag
			if(addDrag){
				thEl = document.createElement("th");
				trEl.appendChild(thEl);
			}
			
			for (var key =0; key<fields.length;key++) {
				thEl = document.createElement("th");
				$(thEl).css('width', fields[key].width);
				thEl.className="ahDataTabelTh";
				var divEl = document.createElement("div");
				if(fields[key].type == 'hidden'){
					divEl.style.display=getDisplayString(false);
				}
				var textSpanEl = document.createElement("span");
				var divTxt = document.createTextNode(fields[key].display);
				textSpanEl.appendChild(divTxt);
				divEl.appendChild(textSpanEl);
				thEl.appendChild(divEl);
				
				if(fields[key] && fields[key].events && fields[key].events.helpLink){
					var hlepLinkSpanEl = document.createElement("span");
					var hlepLink = document.createElement("a");
					hlepLink.href = "javascript: void(0);";
					hlepLink.style.padding = "0px 0px 0px 10px";
					hlepLink.innerHTML="?";
					
					hlepLink.onclick = (function(helpLink) {
						return function() {
							helpLink.apply(self);
						}
					})(fields[key].events.helpLink);
					hlepLinkSpanEl.appendChild(hlepLink);
					divEl.appendChild(hlepLinkSpanEl);
				}
				
				// now just support dropdown
				if(fields[key].filter && fields[key].options){
					var _headId = getHeadId(fields[key].mark);
					var _headMenuId = getHeadMenuId(_headId);
					var _filterIconId = getFilterIconId(_headId);
					var field = fields[key];
			       	var type = "select"; 
			       	textSpanEl.id = _headId;
			       	textSpanEl.style.textDecoration= "underline";
			       	
			       	// add filter icon
					var filterImg = "<img src=\"/hm/images/spacer.gif\" class=\"dinl\" width=\"5\" >"+"<img src=\"images/ahdatatable/table_filter.png\" class=\"dinl\" >";
					var filterIcon =document.createElement('span');
					filterIcon.id = _filterIconId;
					filterIcon.innerHTML=filterImg;
					filterIcon.style.display="none";
					textSpanEl.appendChild(filterIcon);
					
			       	if(YUD.get(_headMenuId)== null){
			       		var menuContent =thEl.appendChild(document.createElement('div'));
		            	menuContent.id =  _headMenuId;
			       	}
			       	
			       	textSpanEl.onclick = (function(field,type,_headMenuId) {
						return function() {
					        if(YUD.get(_headMenuId).innerHTML == ''){
					        	createMenu(field,type);
					        }
						}
					})(field,type,_headMenuId);
				}
				
				trEl.appendChild(thEl);
			}
			prepareOpActions(trEl,true);
			
			trEl.style.background = '#EEEEEE';
			return trEl;
		};
		
		var createMenu = function(field,type){
			var _headId = getHeadId(field.mark);
			var _headFilterId = getHeadFilterId(_headId);
			var _headMenuId = getHeadMenuId(_headId);
			var oMenu = new YAHOO.widget.Menu(_headMenuId, { fixedcenter: false, zIndex: 999,classname:"menuClass"});
    		oMenu.addItems([
    						[
    						 { text: "<span id='"+_headFilterId+"'>Filter</span>", classname: "menuItemClass",onclick: { fn: onFilterMenuClick, obj:  [type,field] } }
    						],
    						[
    						 { text: '<span>Clear</span>', classname: "menuItemClass",onclick: { fn:  onClearMenuClick, obj: [type,field] } }
    						]
    					]);
    		oMenu.render();
    		oMenu.subscribe("beforeShow", function(){
    			var x = YAHOO.util.Dom.getX(_headId);
    			var y = YAHOO.util.Dom.getY(_headId);
    			YAHOO.util.Dom.setX(_headMenuId, x);
    			YAHOO.util.Dom.setY(_headMenuId, y+20);
    		});
		
    		YAHOO.util.Event.addListener(_headId, "click", oMenu.show, null, oMenu);
    		oMenu.show();
        
		}
		
		var onFilterMenuClick = function(p_sType, p_aArgs, p_oValue){
			var filterType = p_oValue[0];
			var field = p_oValue[1];
			var _headId = getHeadId(field.mark);
			var _headFilterMenuId = getHeadFilterMenuId(_headId);
			var _headFilterId = getHeadFilterId(_headId);
			var _headFilterParentId = getHeadFilterParentId(_headId);
			var goId="ahDataTable_"+field.mark+"_go";
			var filterDiv = YUD.get(_headFilterMenuId);
			var div = YUD.get(_headId);
			if (filterDiv == null) {
				var filterDivContent = div.parentNode.appendChild(document.createElement('div'));
				filterDivContent.id = _headFilterMenuId;
			}
			if(YUD.get(_headFilterId) != null){
				YUD.get(_headFilterId).parentNode.id = _headFilterParentId;
			}
			
			$("#"+_headFilterMenuId).empty();
			var filterMenu = new YAHOO.widget.Menu(_headFilterMenuId, { fixedcenter: false, zIndex: 999 });
			if ("select" == filterType) {
				var $tempDiv = $("<div></div>")
				var $sel = $("<select></select>");
				$sel.css("width","100px");
				if(field.options){
					for(var i=0;i<field.options.length;i++){
						var $opt = $("<option value="+field.options[i].value+">"+field.options[i].label+"</option>");
						if(filterCondition && filterCondition[field.mark]){
							if(filterCondition[field.mark] == field.options[i].value){
								$opt = $("<option selected='true' value="+field.options[i].value+">"+field.options[i].label+"</option>");
							}
						}
						$sel.append($opt);
					}
				}
				$tempDiv.append($sel);
				var html="";
				html=html+"<table><tr><td><input type='button' value='Go' id='"+goId+"'></td></tr><tr><td style='scrolling:yes;'><table><tr><td>";
				html=html+$tempDiv.html();
				html=html+"</td></tr></table></td></tr></table>";
				filterMenu.setBody(html);
			}
			
			filterMenu.render();
			filterMenu.subscribe("beforeShow", function(){
    			var x = YAHOO.util.Dom.getX(_headId);
    			var y = YAHOO.util.Dom.getY(_headId);
    			YAHOO.util.Dom.setX(_headFilterMenuId, x);
    			YAHOO.util.Dom.setY(_headFilterMenuId, y+20);
    		});
        	
        	if("select" == filterType) {
        		YUD.get(goId).onclick=function (){
        			return clickGo(field.mark);
        		}; // band go click event
        	}
        	YAHOO.util.Event.addListener(_headFilterParentId, "click", filterMenu.show, null, filterMenu);
        	filterMenu.show();
		}

		var clickGo = function(mark){
			var _headFilterMenuId = getHeadFilterMenuId(getHeadId(mark));
			var _filterIconId = getFilterIconId(getHeadId(mark));
			
			var $text = $('#'+_headFilterMenuId+' select :selected').val();
			if($text != undefined){
				filterCondition[mark] = $text;
	    		hideTrByFilterCondition(filterCondition);
			}
    		
			alterBgColor(containerId);
			if(YUD.get(_filterIconId)){
				YUD.get(_filterIconId).style.display="inline-block"
			}
			
			// fix IE shadow error
			$("#"+_headFilterMenuId+">.yui-menu-shadow").removeClass("yui-menu-shadow-visible");
		}
		
		var onClearMenuClick = function(p_sType, p_aArgs, p_oValue){
			var filterType = p_oValue[0];
			var field = p_oValue[1];
			var _filterIconId = getFilterIconId(getHeadId(field.mark));
			delete(filterCondition[field.mark])

			hideTrByFilterCondition(filterCondition);
			
			alterBgColor(containerId);
			if(YUD.get(_filterIconId)){
				YUD.get(_filterIconId).style.display="none"
			}
		}
		
		var hideTrByFilterCondition = function(filterCondition){
			var hiddenIndex = [];
			for(var key in filterCondition){
				$('#'+containerId+' tbody>tr').each(function(i){
					var $input = $(this).find('td :input[name='+key+']');
					var $text = filterCondition[key];
//					if($input.val() == 'undefined'){
//						continue;
//					}
					if($text < 0 || $input.val() < 0 || $input.val() == '[-any-]'){
//						$(this).show();
					} else if( $input.val() != $text){
//						$(this).hide();
						hiddenIndex.push(i);
					} else {
//						$(this).show();
					}
				});
			}
			
			$('#'+containerId+' tbody>tr').each(function(j){
				if(hiddenIndex.contains(j)){
					$(this).hide();
				} else {
					$(this).show();
				}
			});
		}
		
		var getHeadId = function(mark){
			return mark+"Head";
		}
		var getHeadMenuId = function(_headId){
			return _headId+"Menu";
		}
		var getHeadFilterMenuId = function(_headId){
			return _headId+"FilterMenu";
		}
		var getHeadFilterId = function(_headId){
			return  _headId+"Filter";
		}
		var getHeadFilterParentId = function(_headId){
			return  getHeadFilterId(_headId)+"Parent"; //for banding filterMenu Listener
		}
		var getFilterIconId = function(_headId){
			return _headId+"Icon"
		}
		
		var tdsCount = 0;
		var prepareTemplateRow = function(renderElFunc, optionArg) {
			tdsCount = 0;
			if (!optionArg) {
			  optionArg = {};
			 }
			 var blnEditMode = false;
			 if (isDefined(optionArg.blnEditMode)) {
			  blnEditMode = optionArg.blnEditMode;
			 }
			 var trNum = optionArg.trNum;
			 var datas = optionArg.datas;
				 
			if (!isDefined(trNum)) {
				trNum = genCurTrIdNumber();
			}
			if (!(trNum in trTdIds)) {
				trTdIds[trNum] = [];
			}
			var trEl = document.createElement("tr");
			trEl.id = getTrIdString(trNum);
			var tdEl;
			var tdHideEl;
			var tdTextEl;
			var tdEditEl;
			var tdHideEditEl;
			var tdNum;
			
			//add drag
			if(addDrag){
				tdEl = document.createElement("td");
				tdEl.className = "dragMe";
				var image = document.createElement('img');
				image.width=16;
				image.height=16;
				image.title="Drag Me";
				image.alt="Drag Me";
				image.className="dinl";
				image.src='images/ahdatatable/hm-drag.png';
				tdEl.appendChild(image)
				trEl.appendChild(tdEl);
				tdsCount++;
			}
			
			for (var key = 0; key < fields.length; key++) {
				tdEl = document.createElement("td");
				tdNum = genCurTdIdNumber(trNum);
				trTdIds[trNum].push(tdNum);
				gTdIds[tdNum]=true;
				tdEl.id = getTdIdString(tdNum);
				tdEl.className = fields[key].classNames.displayTd;
				tdHideEl = document.createElement('input');
				tdHideEl.className = "hdValue";
				tdHideEl.type = "hidden";
				tdHideEl.name = fields[key].mark;
				
				tdTextEl = document.createElement('span');
				if(fields[key].type == 'hidden'){
					tdTextEl.className = "tdTextHidden";
					tdTextEl.style.display = getDisplayString(false);
				} else {
					tdTextEl.className = fields[key].classNames.displayText;
					tdTextEl.style.display =getDisplayString(!blnEditMode);
				}
				
				var optionsTmp = {
						"field": fields[key],
						"parent": tdEl.id,
						"blnEditMode": blnEditMode,
						"tdNum":tdNum,
						"curRowId": trEl.id,
						"curDataTbl": self,
						"dataWithText": optionArg.dataWithText
					};
				if (datas) {
					optionsTmp["value"] = datas[fields[key].mark];
				}
				tdEditEl = createEditableTdEl(key, optionsTmp, blnEditMode);
				tdEl.appendChild(tdHideEl);
				tdEl.appendChild(tdTextEl);
				if (tdEditEl) {
					tdEl.appendChild(tdEditEl);
				}
				trEl.appendChild(tdEl);
				
				tdsCount++;
			}
			
			prepareOpActions(trEl,false);
			tdsCount++;
			
			if (renderElFunc) {
			  renderElFunc.apply(trEl, [trEl]);
			 }
			 
			setTimeout(
					function() {
						$("#"+trEl.id).find("td").each(function() {
							if (tdEditEls[this.id]
							&& tdEditEls[this.id].afterRender) {
								tdEditEls[this.id].afterRender();
							}
						});
					}, 20
			 );
			//return trEl;
		};
		var insertAfter = function(newElement,targetElement){
			  //var parent=targetElement.parentNode;
			  if(targetElement.lastChild==targetElement){
				  targetElement.appendChild(newElement);
			  }else{
				  targetElement.insertBefore(newElement,targetElement.firstChild);
			  }
		};
		var opHeaderOptions = {
				"add": {
					display: "New",
					events: {
						_click: function(trEl) {
							if(!blnRowEdited){
								prepareTemplateRow(function(trElNew) {
									curEditRows.add(trElNew);
									//var tbodyEl = document.getElementById(container).getElementsByTagName("tbody")[0];
									var tbodyEl = $('#'+container).find("tbody")[0];
									var referRow = tbodyEl;
									insertAfter(trElNew,referRow);
									makeARowEditable(trElNew, true,false,true);
									blnRowEdited = true;
									if (myConfigs && myConfigs.dragEvents && myConfigs.dragEvents.customForNewRow) {
										 myConfigs.dragEvents.customForNewRow.apply(trElNew);
									}
								});
								rowEditIndex = trEl.rowIndex;
							}
						}
					}
				},
				"remove": {
					display: "Delete",
					events: {
						_click: function(trEl) {
							if(!blnRowEdited){
								myConfirmDialog.cfg.setProperty('buttons',[ {text:"Yes", 
																			handler:function(){
																				this.hide();
																				var selectTrEls = $(trEl).parent().parent().find(".selected");
																				if(!selectTrEls || selectTrEls.length == 0){
																					//var trEls = trEl.parentNode.parentNode.getElementsByTagName("tr");
																					var trEls = $(trEl).parent().parent().find("tr");
																					for(var i=trEls.length-1;i>0;i--){
																						trEls[i].parentNode.removeChild(trEls[i]);
																					}
																				} else {
																					for(var i=selectTrEls.length-1;i>=0;i--){
																						selectTrEls[i].parentNode.removeChild(selectTrEls[i]);
																					}
																				}
																				alterBgColor(container);}, 
																			isDefault:true 
																			},
										 			                        { text:"&nbsp;No&nbsp;", 
																			  handler:handleNo } 
																		  ]);
								var selectTrEls = $(trEl).parent().parent().find(".selected");
								if(!selectTrEls || selectTrEls.length == 0){
									myConfirmDialog.cfg.setProperty('text', "<html><body>This operation will remove all items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");	
								} else {
									myConfirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the selected items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
								}
								myConfirmDialog.show();
							}
						}
					}
				}
		};
		var opOptions = {
			"edit": {
				display: "Edit",
				events: {
					_click: function(trEl) {
						if(!blnRowEdited){
							makeARowEditable(trEl, true);	
							blnRowEdited = true;
							rowEditIndex = trEl.rowIndex - 1;
						}
					}
				}
			},
			"add": {
				display: "New",
				events: {
					_click: function(trEl) {
						if(!blnRowEdited){
							prepareTemplateRow(function(trElNew) {
								curEditRows.add(trElNew);
								if (trEl.nextSibling) {
									trEl.parentNode.insertBefore(trElNew, trEl.nextSibling);
								} else {
									trEl.parentNode.appendChild(trElNew);
								}
								makeARowEditable(trElNew, true,false,true);
								alterBgColor(container);
								blnRowEdited = true;
								if (myConfigs && myConfigs.dragEvents && myConfigs.dragEvents.customForNewRow) {
									 myConfigs.dragEvents.customForNewRow.apply(trElNew);
								}
							});
							rowEditIndex = trEl.rowIndex;
						}
					}
				}
			},
			"remove": {
				display: "Delete",
				events: {
					_click: function(trEl) {
						if(!blnRowEdited){
							myConfirmDialog.cfg.setProperty('buttons',[ {text:"Yes", 
																		handler:function(){
																			this.hide();
																			trEl.parentNode.removeChild(trEl);
																			alterBgColor(container);}, 
																		isDefault:true 
																		},
								     			                        { text:"&nbsp;No&nbsp;", 
																		  handler:handleNo } 
																	  ]);
							myConfirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the current item.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>");
							myConfirmDialog.show();
						}
					}
				}
			},
			"done": {
				display: "Done",
				events: {
					_click: function(trEl) {
						var validateResult = true;
						//var tdEls = trEl.getElementsByTagName("td");
						var tdEls = $(trEl).find("td");
						
						for(var i =0;i<fields.length;i++){
							if(fields[i].validate){
								var value;
								var tdEl;
								if(addDrag){
									tdEl = tdEditEls[tdEls[i+1].id]; // the first td is drag,so +1
								} else {
									tdEl = tdEditEls[tdEls[i].id];
								}
								
								if(tdEl){
									value = tdEl.getCurValue();
								}
								if(!fields[i].validate.apply(self,[value])){
									validateResult = false;
									break;
								}
							}
						}
						if(validateResult){
							curEditRows.remove(trEl);
							makeARowEditable(trEl, false);
							addClickEventForSingleRow(trEl);
							var trIdsResult;
							if (myConfigs && myConfigs.whenSaveRow) {
								trIdsResult = myConfigs.whenSaveRow.apply(self, [trEl]);
							}
							var addDragTmp = false;
							if (myConfigs && myConfigs.dragEvents && myConfigs.dragEvents.customForNewRow) {
								addDragTmp = true;
							}
							if (trIdsResult) {
								for (var i = 0; i < trIdsResult.length; i++) {
									var trElTmp = $el(getTrIdString(trIdsResult[i]))[0];
									makeARowEditable(trElTmp, false);
									addClickEventForSingleRow(trElTmp);
									if (addDragTmp) {
										 myConfigs.dragEvents.customForNewRow.apply(trElTmp);
									}
								}
							}
							alterBgColor(container);
							blnRowEdited = false;
							rowEditIndex=-1;
						}
					}
				},
				initShown: false,
				className: "whenEdit"
			},
			"cancelEdit":{
				display:"Cancel",
				events:{
					_click:function(trEl) {
						curEditRows.remove(trEl);
						makeARowEditable(trEl,false,true);
						//addClickEventForSingleRow(trEl);
						blnRowEdited = false;
						rowEditIndex = -1;
						if (myConfigs && myConfigs.whenCancelEditRow) {
							myConfigs.whenCancelEditRow.apply(self, [trEl]);
						}
					}
				},
				initShown:false,
				className:"whenEdit edit"
			},
			"cancelNew":{
				display:"Cancel",
				events:{
					_click: function(trEl) {
						curEditRows.remove(trEl);
						trEl.parentNode.removeChild(trEl);
						blnRowEdited = false;
						rowEditIndex = -1;
					}
				},
				initShown:false,
				className:"whenEdit new"
			}
		};
		var prepareOpActions = function(trEl,isHeader) {
			var tdEl,
				divEl,
				opOption,
				aLink,
				className;
			
			tdEl = isHeader ? document.createElement("th") : document.createElement("td");
			tdEl.nowrap = "nowrap";
			tdEl.name = "op_td_of_row_edit";
			tdEl.className = "ahDataTableOptionTd";
			divEl = document.createElement("div");
			divEl.className = "ahDataTableOption";
			var operations =  isHeader ? opHeaderOptions : opOptions
			for (var key in operations) {
				opOption = operations[key];
				aLink = document.createElement("a");
				aLink.href = "javascript: void(0);";
				aLink.style.margin = "0px 10px 0px 0px";
				aLink.onclick = (function(curRow, events) {
					return function() {
						if (typeof prevantPrompt != "undefined") {
							 prevantPrompt();
						}
						events._click.apply(tdEl, [curRow]);
					}
				})(trEl, opOption.events);
				if (isDefined(opOption.initShown) && !opOption.initShown) {
					aLink.style.display = "none";
				}
				className = opOption.className;
				if (!className) {
					className = "whenView";
				}
				aLink.className = className;
				
				var image = document.createElement('img');
				image.width=16;
				image.height=16;
				image.title=opOption.display;
				image.alt=opOption.display;
				image.className="dinl";
				
				if(opOption.display == "New"){
					image.src='images/new.png';
				} else if(opOption.display == "Cancel"){
					image.src='images/cancel.png';
				} else if(opOption.display == "Delete"){
					image.src='images/ahdatatable/hm-trash.png';
				} else if(opOption.display == "Done"){
					image.src='images/save.png';
				} else if(opOption.display == "Edit"){
					image.src='images/ahdatatable/modify-one.png';
				}
				
				
				aLink.appendChild(image);
				divEl.appendChild(aLink);
			}
			tdEl.appendChild(divEl);
			trEl.appendChild(tdEl);
		};
		
		var createEditableTdEl = function(key, optionArg, blnEditMode) {
			if (!optionArg) {
				return;
			}
			var editWidget;
			var commonOptions = {
					"parent": optionArg.parent,
					"blnEditMode": optionArg.blnEditMode,
					"field": fields[key],
					"value": optionArg.value,
					"tdEditEls": tdEditEls,
					"tdNum":optionArg.tdNum,
					"curRowId": optionArg.curRowId,
					"curDataTbl": optionArg.curDataTbl,
					"dataWithText": optionArg.dataWithText,
					"addDrag":addDrag
				};
			if(editRowInfo){
				commonOptions["editInfo"]= editRowInfo.name;
				commonOptions["blnEditNewRow"]= editRowInfo.blnNew;
			}
			if(newOptionsForDropDown){
				commonOptions["newOptionsForDropDownInfo"]= newOptionsForDropDown;
			}
			if (optionArg.field.type === 'text') {
				editWidget = new RowCellEditWidgetText(commonOptions);
			} else if (optionArg.field.type === 'dropdown') {
				editWidget = new RowCellEditWidgetDropDown(commonOptions);
			} else if (optionArg.field.type === 'checkbox') {
				editWidget = new RowCellEditWidgetCheckBox(commonOptions);
			} else if(optionArg.field.type === 'dropdowneidt') {
				editWidget = new RowCellEditWidgetDropDownEdit(commonOptions);
		    } else if(optionArg.field.type === 'dropdownnew') {
				editWidget = new RowCellEditWidgetDropDownNew(commonOptions);
		    } else if(optionArg.field.type === 'hidden') {
		    	editWidget = new RowCellEditWidgetHidden(commonOptions);
			} else if(optionArg.field.type ==='dropdownChange') {
				editWidget = new RowCellEditWidgetDropdownChange(commonOptions);
			} else if(optionArg.field.type ==='dropdownGenerate'){
				editWidget = new RowCellEditWidgetDropDownOptionsGenerate(commonOptions);
			} else if(optionArg.field.type ==='dropdownMultiple') {
				editWidget = new RowCellEditWidgetDropDownMultiple(commonOptions);
			} else if (optionArg.field.type === 'dropdownPopup') {
				editWidget = new RowCellEditWidgetDropDownPopup(commonOptions);
			} else if(optionArg.field.type === 'dropdownRemove') {
				editWidget = new RowCellEditWidgetDropDownRemove(commonOptions);
		    }
			if (editWidget != null) {
				editWidget.render();
				tdEditEls[optionArg.parent] = editWidget;
				return editWidget.getContainerEl();
			}
		};
	};
	
	// ====== widget define start =======
	var RowCellEditWidget = function(options, callback) {
		var self = this,
			parent = options.parent;
		self.curDataTbl = options.curDataTbl;
		self.curRowId = options.curRowId;

		self.isCurRowANewRow = function() {
			return self.curDataTbl.isThisANewRow(self.curRowId);
		};
		// please override this function
		// when in display mode, text returned by this function will be shown
		self.getDisplayText = function() {
			return "N/A";
		};
		
		// please override this function
		// this value will be the real data to be used when passed to back end
		self.getCurValue = function() {
			return -1;
		};
		
		// please override this function
		// this value will be the real data to be used when passed to back end
		self.setCurValue = function(value, text) {
			
		};
		
		// please override this function
		// it will return the HTML element of edit widget
		self.getContainerEl = function() {
			return null;
		};
		
		// please override this function
		self.afterRender = function() {
			
		};
		
		var toggleTextNode = function(blnShown, textShown) {
			var texts = $('#' + parent).find(".tdText");
			if (texts && texts.length > 0) {
				if (blnShown) {
					texts[0].style.display = "block";
				} else {
					texts[0].style.display = "none";
				}
				
				if (isDefined(textShown)) {
					var pattern = /<[^>].*?>/ig;
					if(pattern.test(textShown)){
						texts[0].innerHTML = textShown;
					}else{
						texts[0].title = textShown;
						if(textShown.length > 100){
							textShown = textShown.substring(0,100)+"...";
						}
						texts[0].innerHTML = textShown;
					}
				}
			}
		};
		self.setDatasourceValue = function(value,text) {
			if (!isDefined(value)) {
				return;
			}
			var values = $('#'+parent).find(".hdValue");
			if (values && values.length > 0) {
				values[0].value = value;
			}
		};
		var getDatasourceValue = function() {
			var values = $('#'+parent).find(".hdValue");
			if (values && values.length > 0) {
				return values[0].value;
			}
		};
		self.show = function() {
			toggleTextNode(false);
			self.showWidget();
		};
		// please override this function
		// override to define how to show the edit widget, e.g. just set style.display to block
		self.showWidget = function() {
			
		};
		
		self.hide = function() {
			toggleTextNode(true, self.getDisplayText());
			self.setDatasourceValue(self.getCurValue(),self.getDisplayText());
			self.hideWidget();
		};
		// please override this function
		// override to define how to hide the edit widget, e.g. just set style.display to none
		self.hideWidget = function() {
			
		};
		
		self.cancel = function() {
			toggleTextNode(true);
			self.hideWidget();
			self.cancelWidget(getDatasourceValue());
		};
		// please override this function
		// override to define what will be done when you cancel the edit mode of tr
		self.cancelWidget = function(value) {
			
		};
		// please override this function
		// get the element name of edit widget in a td, this name should be unique in a td
		self.getEditElName = function() {
			return null;
		};
		
		// please override this function if needed
		// you can call this function to update data of this edit widget, you can use widget special data type
		self.updateElData = function(data) {
		};
		
		// please override this function if needed
		// call this function to enable/disable the edit widget
		self.disableEl = function(blnDisabled) {
			var el = self.getContainerEl();
			if (arguments.length === 0 || blnDisabled) {
				el.prop("disabled", "disabled");
			} else {
				el.removeAttr("disabled");
			}
		};
		
		self.changeHiddenValue = function(value) {
			$(self.getContainerEl()).parent("td").find("input.hdValue").val(value);
		};
		self.getHiddenValue = function() {
			return $(self.getContainerEl()).parent("td").find("input.hdValue").val();
		};
		self.$getTextDisplayEl = function() {
			return $(self.getContainerEl()).parent("td").find("span.tdText");
		};
		// you can override it to fetch value of edit widget, getCurValue() is used to hold real data, but not only the edit widget value
		self.getEditWidgetValue = function() {
			return self.getHiddenValue();
		};
	};
	
	var RowCellEditWidgetText = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('input');
			myEl.className = "hdEdit";
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			if(options.field.width){
				myEl.style.width=options.field.width;
				myEl.width=options.field.width;
			}
			
			if(options.field.maxlength){
				myEl.maxLength = options.field.maxlength;
			}
			
			myEl.type = "text";
			
			if(options.field.disabled){
				myEl.disabled = options.field.disabled;
			}
			if(options.field.keypress){
				$(myEl).keypress(function(e) {
					return hm.util.keyPressPermit(e,options.field.keypress);
				});
			}
			
			myEl.style.display = getDisplayString(options.blnEditMode);
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.value;
		};
		
		self.getCurValue = function() {
			return myEl.value;
		};
		
		self.setCurValue = function(value) {
			myEl.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.value = value;
		};
		self.getEditElName = function() {
			return myEl.name;
		};
	};
	
	var RowCellEditWidgetHidden = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('input');
			myEl.className = "hdHidden";
			myEl.type = "hidden";
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			//myEl.style.width=options.field.width;
			myEl.style.display = getDisplayString(options.blnEditMode);
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.value;
		};
		
		self.getCurValue = function() {
			return myEl.value;
		};
		
		self.setCurValue = function(value) {
			myEl.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.value = value;
		};
		self.getEditElName = function() {
			return myEl.name;
		};
	};
	
	var RowCellEditWidgetDropdownChange = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement("span");
			myEl.style.overflow = "hidden";
			myEl.style.position = "relative";
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.style.width=options.field.width;
			
			var select = document.createElement('select');
			select.name=options.field.editMark;
			select.id=options.field.editMark+"_"+options.tdNum;
			select.style.width=options.field.width;
			//select.style.height="19px";
			select.onchange = changeFunc;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					select.appendChild(optionEl);
				}
			}
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				select.value = value;
			}
			
			var text = document.createElement("input");
			text.className="selectText";
			var textWidth = parseInt(options.field.width)-28
			text.style.width=textWidth+"px";
			text.value=$(select).find("option:selected").text();
			
			myEl.appendChild(select);
			myEl.appendChild(text);
			
			var $text = $(text);
			var $select = $(select);
			$(text)
			.autocomplete({
				delay:0,
				minLength:0,
				source: function( request, response ) {
                    var matcher = new RegExp("^"+$.ui.autocomplete.escapeRegex(request.term)+"[\s|\S]*", "i" );
                    response( $select.children( "option" ).map(function() {
                        var text = $( this ).text();
                        if ( this.value && ( !request.term || matcher.test(text) ) )
                            return {
                                label: text,
                                value: text,
                                option: this
                            };
                    }));
                },
                select: function( event, ui ) {
                    ui.item.option.selected = true;
                    changeFunc();
                },
                change: function( event, ui ) {
                    if ( !ui.item )
                        return removeIfInvalid( this );
                }
			})
			.dblclick(function(){
				$(this).select();
			})
			.addClass( "ui-widget ui-widget-content ui-corner-left" );
			
			function removeIfInvalid(element) {
	            var value = $( element ).val(),
	                matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( value ) + "$", "i" ),
	                valid = false;
	            $select.children( "option" ).each(function() {
	                if ( $( this ).text().match( matcher ) ) {
	                    this.selected = valid = true;
	                    return false;
	                }
	            });
	            
	            if ( !valid ) {
	                // remove invalid value, as it didn't match anything
	                $( element )
	                    .val("");
	                $select.val("-1");
	                $text.data( "autocomplete" ).term = "";
	                changeFunc();
	                return false;
	            } else {
	            	 changeFunc();
	            }
	        }
		};
		
		var changeFunc = function(){
			var selectedIndex = myEl.firstChild.selectedIndex;
			var changeCol;
			if(options.addDrag){
				changeCol = options.field.changeCol+1;// the first td is drag,so +1
			} else {
				changeCol = options.field.changeCol;
			}
			
			var row = myEl.parentNode.parentNode;
			//var tds = row.getElementsByTagName("td");
			var tds = $(row).find("td");
			if (tds[changeCol]) {
				var tdId = tds[changeCol].id;
				if(options.tdEditEls[tdId]){
					options.tdEditEls[tdId].setCurValue(options.field.changeValue[selectedIndex]);
				}
			}
			if(myEl.firstChild[selectedIndex]){
				myEl.lastChild.value=myEl.firstChild[selectedIndex].text;
			} else {
				myEl.lastChild.value=myEl.firstChild[0].text;
			}
			
		}
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.firstChild.options[myEl.firstChild.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.firstChild.value;
		};
		
		self.setCurValue = function(value) {
			myEl.firstChild.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.firstChild.value = value;
		};
		self.getEditElName = function() {
			return myEl.firstChild.name;
		};
		
		self.setDatasourceValue = function(value,text) {
			if (!isDefined(value)) {
				return;
			}
			var $parentEl = $('#'+options.parent);
			var values = $parentEl.find(".hdValue");
			if (values && values.length > 0) {
				values[0].value = value;
			}
			
			//selectText 
			var selectText  = $parentEl.find(".selectText");
			if (selectText && selectText.length > 0) {
				selectText[0].value = text;
			}
		};
	};
	
	var RowCellEditWidgetDropDown = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('select');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			myEl.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					myEl.appendChild(optionEl);
				}
			}
			if(options.field.events && options.field.events.change){
				myEl.onchange = (function(link) {
						return function() {
							link.apply(self);
						}
					})(options.field.events.change);
			}
			
			if(options.field.events && options.field.events.onChange){
				myEl.onchange = function() {
					options.field.events.onChange.apply(self,[myEl.parentNode.parentNode]);
				};
			}
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.options[myEl.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.value;
		};
		
		self.setCurValue = function(value) {
			 myEl.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.value = value;
		};
		self.getEditElName = function() {
			return myEl.name;
		};
		
		self.updateElData = function(data){
			myEl.options.length = 0;
			if (data) {
				var optionEl;
				var defaultValue = "";
				for (var i = 0; i < data.length; i++) {
					if(data[i].label == ""){
						defaultValue = data[i].value;
						break;
					}
				}
				for (var i = 0; i < data.length; i++) {
					if(data[i].label != ""){
						optionEl = document.createElement('option');
						optionEl.value = data[i].value;
						optionEl.innerHTML = data[i].label;
						if(defaultValue == data[i].value){
							optionEl.selected = true;
						}
						myEl.appendChild(optionEl);
					}
				}
			}
		}
	};
	
	var RowCellEditWidgetDropDownOptionsGenerate = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('select');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			myEl.style.width=options.field.width;
			newOptions = options.field.newOptions;
			var optionEl;
			if(options.newOptionsForDropDownInfo){
				myEl.options.length = 0;
				for (var i = 0; i < options.newOptionsForDropDownInfo.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.newOptionsForDropDownInfo[i].value;
					optionEl.innerHTML = options.newOptionsForDropDownInfo[i].label;
					myEl.appendChild(optionEl);
				}
			}else{
				if (options.field.options) {
					for (var i = 0; i < options.field.options.length; i++) {
						optionEl = document.createElement('option');
						optionEl.value = options.field.options[i].value;
						optionEl.innerHTML = options.field.options[i].label;
						myEl.appendChild(optionEl);
					}
				}
			}
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.value = value;
			}
		};
		
		
			
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.options[myEl.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.value;
		};
		
		self.setCurValue = function(value) {
			 myEl.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.value = value;
		};
		self.getEditElName = function() {
			return myEl.name;
		};
	};

	var RowCellEditWidgetCheckBox = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('input');
			myEl.className = "hdEdit";
			myEl.type = "checkbox";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			myEl.style.width=options.field.width;
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.checked = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.checked?"<input type='checkbox' checked='true' disabled='true'/>":"<input type='checkbox' disabled='true' />";
		};
		
		self.getCurValue = function() {
			return myEl.checked;
		};
		
		self.setCurValue = function(value) {
			myEl.checked = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.checked = value;
		};
		
		self.getEditElName = function() {
			return myEl.name;
		};
	};
	
	var RowCellEditWidgetDropDownEdit = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
			
		};
		var createMyEl = function() {
			
			myEl = document.createElement('div');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			var width = parseInt(options.field.width) + 50;
			myEl.style.width = width+'px';
			var select =  document.createElement('select');
			select.name=options.field.editMark;
			select.id=options.field.editMark+"_"+options.tdNum;
			select.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					select.appendChild(optionEl);
				}
			}
			myEl.appendChild(select);
			var link_add =  document.createElement('a');
			link_add.href ='#';
			link_add.className="marginBtn";
			var image_add = document.createElement('img');
			image_add.src='images/new.png';
			image_add.width=16;
			image_add.height=16;
			image_add.title='New';
			image_add.alt='New';
			image_add.className="dinl";
			var link_edit =  document.createElement('a');
			link_edit.href ='#';
			link_edit.className="marginBtn";
			var image_edit = document.createElement('img');
			image_edit.src='images/modify.png';
			image_edit.width=16;
			image_edit.height=16;
			image_edit.title='Modify';
			image_edit.alt='Modify';
			image_edit.className="dinl";
			link_add.appendChild(image_add);
			if(options.field.events.newClick){
				link_add.onclick = function() {
					saveEditRowData(myEl,options.tdEditEls,options.editInfo);
					options.field.events.newClick.apply(self);
				};
			}
			
			link_edit.appendChild(image_edit);
			if (options.field.events.editClick) {
				link_edit.onclick = function() {
					saveEditRowData(myEl,options.tdEditEls,options.editInfo);
					options.field.events.editClick.apply(self,[select.id]);
				};
			}
			myEl.appendChild(link_add);
			myEl.appendChild(link_edit);
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.firstChild.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.firstChild.options[myEl.firstChild.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.firstChild.value;
		};
		
		self.setCurValue = function(value) {
			 myEl.firstChild.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.firstChild.value = value;
		};
		
		self.getEditElName = function() {
			return myEl.firstChild.name;
		};
	};
	
	var RowCellEditWidgetDropDownNew = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
			
		};
		var createMyEl = function() {
			
			myEl = document.createElement('div');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			var width = parseInt(options.field.width) + 50;
			myEl.style.width = width+'px';
			var select =  document.createElement('select');
			select.name=options.field.editMark;
			select.id=options.field.editMark+"_"+options.tdNum;
			select.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					select.appendChild(optionEl);
				}
			}
			myEl.appendChild(select);
			var link_add =  document.createElement('a');
			link_add.href ='#';
			link_add.className="marginBtn";
			var image_add = document.createElement('img');
			image_add.src='images/new.png';
			image_add.width=16;
			image_add.height=16;
			image_add.title='New';
			image_add.alt='New';
			image_add.className="dinl";			
			link_add.appendChild(image_add);
			if(options.field.events.newClick){
				link_add.onclick = function() {
					saveEditRowData(myEl,options.tdEditEls,options.editInfo);
					options.field.events.newClick.apply(self);
				};
			}
			
		
			myEl.appendChild(link_add);
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.firstChild.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.firstChild.options[myEl.firstChild.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.firstChild.value;
		};
		
		self.setCurValue = function(value) {
			 myEl.firstChild.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.firstChild.value = value;
		};
		
		self.getEditElName = function() {
			return myEl.firstChild.name;
		};
	};
	
	var RowCellEditWidgetDropDownMultiple = function(options, callback) {

		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
		};
		var createMyEl = function() {
			myEl = document.createElement('select');
			myEl.multiple = "multiple";
			myEl.size = "10";
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			myEl.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					if (isDefined(options.value)){
						var selectedVal = options.value.split(",");
						for(var j = 0; j < selectedVal.length; j++){
							if(options.field.options[i].value == selectedVal[j]){
								optionEl.selected = true;
								break;
							}
						}
					}
					myEl.appendChild(optionEl);
				}
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		self.getDisplayText = function() {
			var displayTex = "";
			var displayVal = "";
				for(var loop=0; loop < myEl.options.length; loop++){
					if(myEl.options[loop].selected){
						displayVal += myEl.options[loop].text + ",";
					}
				}
			
			if(displayVal != ""){
				displayTex = displayVal.substring(0,displayVal.length -1);
			}
			return displayTex;
		};
		
		self.getCurValue = function() {
			var displayTex = "";
				var displayVal = "";
				for(var loop=0; loop < myEl.options.length; loop++){
					if(myEl.options[loop].selected){
						displayVal += myEl.options[loop].value + ",";
					}
				}
				if(displayVal != ""){
					displayTex = displayVal.substring(0,displayVal.length -1);
				}
			return displayTex;
		};
		
		/*self.setCurValue = function(value) {
			myEl.value = value;
		};*/
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			if (!value) {
				return;
			}
			var values = value.split(",");
			var valLen = values.length;
			for(var loop=0; loop < myEl.options.length; loop++){
				var cur = 0;
				var blnSelected = false;
				for (var cur = 0; cur < valLen; cur++) {
					if (myEl.options[loop].value == values[cur]) {
						myEl.options[loop].selected = true;
						blnSelected = true;
						break;
					}
				}
				if (!blnSelected) {
					myEl.options[loop].selected = false;
				}
			}
		};
		self.getEditElName = function() {
			return myEl.name;
		};
	};
	
	var RowCellEditWidgetDropDownPopup = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		var elValue;
		var elText;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
			
		};
		
		var createMyEl = function() {
			myEl = document.createElement('span');
			var select = document.createElement('select');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			myEl.name=options.field.editMark;
			myEl.id=options.field.editMark+"_"+options.tdNum;
			myEl.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					select.appendChild(optionEl);
				}
			}
			if(options.field.events && options.field.events.change){
				select.onchange = (function(select) {
						return function() {
							select.apply(self);
						}
					})(options.field.events.change);
			}
			myEl.appendChild(select);
			var textServiceNode = document.createTextNode("Number of Services Selected: ");
			myEl.appendChild(textServiceNode);
			var hlepLink = document.createElement("a");
			hlepLink.href = "javascript: void(0);";
			hlepLink.style.padding = "0px 0px 0px 0px";
			hlepLink.innerHTML="0";
			if(options.field.events && options.field.events.go){
				hlepLink.onclick = (function(helpLink) {
						return function() {
							helpLink.apply(self);
							return false;
						}
					})(options.field.events.go);
			}
			myEl.appendChild(hlepLink);
			if (isObject(options.value)) {
				elValue = options.value.value;
				elText = options.value.text;
			} else {
				elValue = options.value;
			}
			if (!isDefined(elValue)) {
				if (isDefined(options.field.defaultValue)) {
					elValue = options.field.defaultValue;
				}
				if (isDefined(options.field.defaultText)) {
					elText = options.field.defaultText;
				}
			}
		};
		
		self.showWidget = function() {
			if (self.isCurRowANewRow() || (!self.getHiddenValue() && options.blnEditNewRow)) {
				if (options.blnEditNewRow) {
					options.blnEditNewRow = false;
				}
				self.curDataTbl.addCurEditRow(self.curRowId);
				self.$getTextDisplayEl().hide();
				myEl.style.display = "block";
			} else {
				self.$getTextDisplayEl().show();
				myEl.style.display = "none";
			}
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			var textTmp = elText; 
			if (!textTmp && options.dataWithText) {
				textTmp = options.dataWithText[elValue];
			}
			return textTmp;
		};
		
		self.getCurValue = function() {
			return self.getHiddenValue();
		};
		
		self.getEditWidgetValue = function() {
			return myEl.firstChild.value;
		};
		
		self.setCurValue = function(value, text) {
			 //myEl.firstChild.value = "====";
			elValue = value;
			self.changeHiddenValue(value);
			if (text) {
				elText = text;
			}
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.firstChild.value = value;
		};
		
		self.getEditElName = function() {
			return myEl.name;
		};
		
		self.afterRender = function() {
			if (elValue) {
				self.changeHiddenValue(elValue);
			}
		};
		
		self.disabledService = function() {
			var val = myEl.firstChild.value;
			if(val == "1" || val == "2"){
				myEl.firstChild.disabled = "disabled";
			}
		};
		
		self.changeSelectedServiceNum = function(value) {
			$(myEl).find("a")[0].innerHTML = value;
		};
		
		self.getShowServiceText = function() {
			return $(myEl).prev().html();
		};
	};
	
	var RowCellEditWidgetDropDownRemove = function(options, callback) {
		var self = this;
		RowCellEditWidget.call(self, options, callback);
		
		var myEl;
		
		self.render = function() {
			if (!myEl) {
				createMyEl();
			}
			
		};
		var createMyEl = function() {
			
			myEl = document.createElement('div');
			myEl.className = "hdEdit";
			myEl.style.display = getDisplayString(options.blnEditMode);
			var width = parseInt(options.field.width) + 50;
			myEl.style.width = width+'px';
			var select =  document.createElement('select');
			select.name=options.field.editMark;
			select.id=options.field.editMark+"_"+options.tdNum;
			select.style.width=options.field.width;
			var optionEl;
			if (options.field.options) {
				for (var i = 0; i < options.field.options.length; i++) {
					optionEl = document.createElement('option');
					optionEl.value = options.field.options[i].value;
					optionEl.innerHTML = options.field.options[i].label;
					select.appendChild(optionEl);
				}
			}
			myEl.appendChild(select);
			var link_add =  document.createElement('a');
			link_add.href ='#';
			link_add.className="marginBtn";
			var image_add = document.createElement('img');
			image_add.src='images/new.png';
			image_add.width=16;
			image_add.height=16;
			image_add.title='New';
			image_add.alt='New';
			image_add.className="dinl";
			var link_edit =  document.createElement('a');
			link_edit.href ='#';
			link_edit.className="marginBtn";
			var image_edit = document.createElement('img');
			image_edit.src='images/cancel.png';
			image_edit.width=16;
			image_edit.height=16;
			image_edit.title='Remove';
			image_edit.alt='Remove';
			image_edit.className="dinl";
			link_add.appendChild(image_add);
			if(options.field.events.newClick){
				link_add.onclick = function() {
					saveEditRowData(myEl,options.tdEditEls,options.editInfo);
					options.field.events.newClick.apply(self,[select.id]);
				};
			}
			
			link_edit.appendChild(image_edit);
			if (options.field.events.editClick) {
				link_edit.onclick = function() {
					saveEditRowData(myEl,options.tdEditEls,options.editInfo);
					options.field.events.editClick.apply(self,[select.id]);
				};
			}
			myEl.appendChild(link_add);
			myEl.appendChild(link_edit);
			
			var value = options.value;
			if (!isDefined(value)) {
				if (isDefined(options.field.defaultValue)) {
					value = options.field.defaultValue;
				}
			}
			if (isDefined(value)) {
				myEl.firstChild.value = value;
			}
		};
		
		self.showWidget = function() {
			myEl.style.display = "block";
		};
		
		self.hideWidget = function() {
			myEl.style.display = "none";
		};
		
		self.getDisplayText = function() {
			return myEl.firstChild.options[myEl.firstChild.selectedIndex].text;
		};
		
		self.getCurValue = function() {
			return myEl.firstChild.value;
		};
		
		self.setCurValue = function(value) {
			 myEl.firstChild.value = value;
		};
		
		self.getContainerEl = function() {
			return myEl;
		};
		
		self.cancelWidget = function(value) {
			myEl.firstChild.value = value;
		};
		
		self.getEditElName = function() {
			return myEl.firstChild.name;
		};
		
		self.getSelectEl = function() {
			return myEl.firstChild;
		};
	};
	
	function saveEditRowData(myEl,tdEditEls,editInfo){
		var row = myEl.parentNode.parentNode;
		//var tds = row.getElementsByTagName("td");
		var tds = $(row).find("td");
		var rowIndex = row.rowIndex - 1;
		var blnNew = false;
		if (tds && tds.length > 0) {
			for (var i = 0; i < tds.length; i++) {
				var td = tds[i];
				if(td.id && tdEditEls[td.id]){
					blnNew = tdEditEls[td.id].isCurRowANewRow();
					break;
				}
			}
		}
		var editData = "{editInfo:{name:\""+ editInfo + "\", blnNew: " + blnNew + ",data:"+"{rowIndex:"+ rowIndex +",";
		if (tds && tds.length > 0) {
			for (var i = 0; i < tds.length; i++) {
				var td = tds[i];
				if(td.id && tdEditEls[td.id]){
					editData+=tdEditEls[td.id].getEditElName()+":[\""+tdEditEls[td.id].getCurValue()+"\",\""+tdEditEls[td.id].getDisplayText()+"\"],"
				}
			}
		}
		editData = editData.substring(0,editData.length-1)
		editData +="}}}";
		if(document.getElementById(editInfo)){
			document.getElementById(editInfo).value= editData;
		}
	}
	
	var myConfirmDialog;
	function initMyConfirmDialog() {
		var $confirmDialog = $('<div id="myConfirmDialog">');
		$("body").append($confirmDialog);
		myConfirmDialog =
	     new YAHOO.widget.SimpleDialog("myConfirmDialog",
	              { width: "350px",
	                fixedcenter: true,
	                visible: false,
	                draggable: true,
	                modal:true,
	                close: true,
	                text: "<html><body>This operation will remove the selected items.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>",
	                icon: YAHOO.widget.SimpleDialog.ICON_WARN,
	                constraintoviewport: true
	              } );
		myConfirmDialog.setHeader("Confirm");
		myConfirmDialog.render(document.body);
		myConfirmDialog.cancelEvent.subscribe(confirmCancelHandler);
	}
	
	var handleNo = function() {
	    this.hide();
	};
	
	// ====== widget define end =======
	
	$.extend(AhDataTablePanel, {
		DataTablePanel: DataTablePanel,
		product: 'DataTablePanel',
		version: '0.0.1'
	});
})(jQuery);
