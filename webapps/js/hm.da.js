var hm = hm || {};
hm.da = hm.da || {};
hm.da.callback = {};

hm.da.MODE_VIEW = 1;
hm.da.MODE_NEW = 2;
hm.da.MODE_DRILL = 3;

hm.da.C_READ = 1;
hm.da.C_WRITE = 2;
hm.da.C_NONE = 3;

hm.da.TAB_HEIGHT=35;
hm.da.TAB_LEFTRIGHT=4;


hm.da.createTabTR = function(arrays) {
	var tr = Get("da_tr_tab");
	//var td=hm.da.createTD(110,hm.da.TAB_HEIGHT, null, "Dashboard", "dac_td_tab_title");
	//td.setAttribute("nowrap", "nowrap");
	//tr.appendChild(td);
	for(var i=0; i<arrays.length; i++) {
		if (arrays[i][2]) {
			td=hm.da.createTD(null,hm.da.TAB_HEIGHT, "da_tr_tab_td" + arrays[i][0], null, "dac_td_tab_link_sel");
		} else {
			td=hm.da.createTD(null,hm.da.TAB_HEIGHT, "da_tr_tab_td" + arrays[i][0], null, "dac_td_tab_link");
		}
		var ctable=document.createElement("table");
		ctable.setAttribute("cellspacing","0px");
		ctable.setAttribute("cellpadding","0px");
		ctable.setAttribute("border","0");
		ctable.setAttribute("id","da_table_id_" +  arrays[i][0]);
		ctable.className="dac_table_tab_class";

		var ctbody=document.createElement("tbody");
		var ctr = document.createElement("tr");
		var ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_left");
		ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
		ctr.appendChild(ctd);
		ctd = hm.da.createTD(null,hm.da.TAB_HEIGHT, "renameTab_alink_td_id_" + arrays[i][0], null, "dac_td_tab_center");
		ctd.setAttribute("nowrap", "nowrap");
		var tabTextValue = arrays[i][1];
		
		var a = hm.da.createA(hm.da.changeTab, tabTextValue, "renameTab_alink_id_" + arrays[i][0], null, arrays[i][0], null,hm.da.displayTextField);
		ctd.appendChild(a);
		ctr.appendChild(ctd);

		ctd = hm.da.createTD(null,hm.da.TAB_HEIGHT, "renameTab_text_td_id_" + arrays[i][0], null, "dac_td_tab_center_changeTab");
		ctd.setAttribute("nowrap", "nowrap");
		ctd.style.display="none";
		var tx = hm.da.createTextBox(tabTextValue,arrays[i][0]);
		ctd.appendChild(tx);
		ctr.appendChild(ctd);

		ctd = hm.da.createTD(20,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_center_remove");
		a = hm.da.createA(hm.da.displayRemoveDlg, null, null,null, arrays[i][0], hm.da.removetabTD);
		var img;
		if (writePermission) {
			img = hm.da.createImage(imagesBaseUrl + "/dashboard/aclose.png",9, 9, "dac_img_tab_close","", "right");
		} else {
			img = hm.da.createImage(imagesBaseUrl + "/dashboard/uclose.png",9, 9, "dac_img_tab_close","", "right");
		}
		a.appendChild(img);
		ctd.appendChild(a);
		ctr.appendChild(ctd);
		ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_right");
		ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
		ctr.appendChild(ctd);
		ctbody.appendChild(ctr);
		ctable.appendChild(ctbody);
		td.appendChild(ctable);
		tr.appendChild(td);
	}
	td=hm.da.createTD(30,hm.da.TAB_HEIGHT, "da_td_tab_new", null, "dac_td_tab_new");

	var a = hm.da.createA(hm.da.createNewTab);
	var img;
	if (writePermission) {
		img = hm.da.createImage(imagesBaseUrl + "/dashboard/anew.png",16, 16,"dac_img_tab_new","New");
	} else {
		img = hm.da.createImage(imagesBaseUrl + "/dashboard/unew.png",16, 16,"dac_img_tab_new","New");
	}
	a.appendChild(img);
	td.appendChild(a);
	tr.appendChild(td);

	/**
	td=hm.da.createTD(40,hm.da.TAB_HEIGHT, "da_td_tab_clone", null, "dac_td_tab_clone");

	var a = hm.da.createA(hm.da.createCloneTab);
	var img;
	if (writePermission) {
		img = hm.da.createImage(imagesBaseUrl + "/modify.png",16, 16,"dac_img_tab_clone","Clone current Tab");
	} else {
		img = hm.da.createImage(imagesBaseUrl + "/modify_disable.png",16, 16,"dac_img_tab_clone","Clone current Tab");
	}
	a.appendChild(img);
	td.appendChild(a);
	tr.appendChild(td);
	**/
	
	td=hm.da.createTD("100%",hm.da.TAB_HEIGHT, "da_td_tab_blank", null, "dac_td_tab_blank");
	tr.appendChild(td);

	
	hm.da.resetTabLength();
}

hm.da.resetTabLength=function(){
	var $tabs = $(".dac_td_tab_center");
	if ($tabs.length > 13) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>5) {
				$(this).children('a').html(html.substr(0,2) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if ($tabs.length > 11) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>6) {
				$(this).children('a').html(html.substr(0,3) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if ($tabs.length >= 11) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>7) {
				$(this).children('a').html(html.substr(0,4) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if($tabs.length >= 9) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>9) {
				$(this).children('a').html(html.substr(0,6) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if($tabs.length >= 8) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>12) {
				$(this).children('a').html(html.substr(0,9) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if($tabs.length >= 7) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>15) {
				$(this).children('a').html(html.substr(0,12) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if($tabs.length >= 6) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>21) {
				$(this).children('a').html(html.substr(0,18) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else if($tabs.length >= 5) {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			if(html.length>25) {
				$(this).children('a').html(html.substr(0,22) + "...");
			} else {
				$(this).children('a').html(html);
			}
		});
	} else {
		$tabs.each(function() {
			var html = $(this).children('a').attr("title");
			$(this).children('a').html(html);
		});
	}
}

hm.da.disabledRemoveNewTabButton = function (){
	$(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/uclose.png");
	$(".dac_img_tab_close").attr("title", "");
	$(".dac_img_tab_new").attr("title", "");
	$(".dac_img_tab_new").attr("src", imagesBaseUrl + "/dashboard/unew.png");
	$(".dac_img_tab_clone").attr("src", imagesBaseUrl + "/modify_disable.png");
}
hm.da.enabledRemoveNewTabButton = function (){
	$(".dac_img_tab_close").attr("src", imagesBaseUrl + "/dashboard/aclose.png");
	$(".dac_img_tab_close").attr("title", "Remove");
	$(".dac_img_tab_new").attr("src", imagesBaseUrl + "/dashboard/anew.png");
	$(".dac_img_tab_new").attr("title", "New");
	$(".dac_img_tab_clone").attr("src", imagesBaseUrl + "/modify.png");
}

hm.da.switchMode = function(pageMode, editMode){
	switch(pageMode){
	case hm.da.MODE_VIEW:
		Get("da_tr_viewMode").style.display="";
		if (applicationPerspectiveId==currentDashBoardId) {
			Get("common_tab_time_td").style.display="none";
			Get("lastUpdateTime1aImg").style.display="none";
			Get("application_tab_time_td").style.display="";
		} else {
			Get("common_tab_time_td").style.display="";
			Get("lastUpdateTime1aImg").style.display="";
			Get("application_tab_time_td").style.display="none";
		}
		Get("da_tr_newMode").style.display="none";
		Get("da_tr_drillMode").style.display="none";
		
		Get("menuButtonForDrillDownDiv").style.display="none";
		if(editMode == hm.da.C_READ){
			Get("menuButtonForNormalDiv").style.display="";
		} else if(editMode == hm.da.C_WRITE){
			Get("menuButtonForNormalDiv").style.display="none";
		}
		
		break;
	case hm.da.MODE_NEW:
		Get("da_tr_viewMode").style.display="none";
		Get("da_tr_newMode").style.display="";
		Get("da_tr_drillMode").style.display="none";
		
		Get("menuButtonForDrillDownDiv").style.display="none";
		Get("menuButtonForNormalDiv").style.display="none";
		
		break;
	case hm.da.MODE_DRILL:
		Get("da_tr_viewMode").style.display="none";
		Get("da_tr_newMode").style.display="none";
		Get("da_tr_drillMode").style.display="";
		
		Get("menuButtonForDrillDownDiv").style.display="";
		Get("menuButtonForNormalDiv").style.display="none";
		
		break;
	default:
		alert("Unknow mode: " + pageMode + "...");
		break;
	}
	if(editMode == hm.da.C_READ){
		Get("left_view_pane_wrap").style.display="";
		Get("splitter").style.display="";
		Get("monitor_view_pane_wrap").style.display="";
		Get("config_view_pane_wrap").style.display="none";
	} else if(editMode == hm.da.C_WRITE){
		Get("left_view_pane_wrap").style.display="";
		Get("splitter").style.display="";
		Get("monitor_view_pane_wrap").style.display="none";
		Get("config_view_pane_wrap").style.display="";
		if(widgetPanel){
			widgetPanel.toggleDisplay(getAvailableComponents(pageMode));
			widgetPanel.syncWidgetSelection();
		}
	} else if (editMode == hm.da.C_NONE) {
		Get("left_view_pane_wrap").style.display="none";
		Get("splitter").style.display="none";
	}

}

hm.da.createNewTab = function() {
	if (customMode || !writePermission) {
		return false;
	}
	
	hm.da.succCheckNewTab=function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Create New Tab Error. Session timeout.", "Error");
			return false;
		}

		if (data.t) {
			hm.da.createNewTabReal();

		} else {
			showWarnDialog("The maximum of six dashboard perspectives has been reached.", "Error");
			return false;
		}
	}

	var url = "dashboard.action?operation=checkTabSize&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succCheckNewTab, timeout: 60000});

}

hm.da.createNewTabReal = function(){
	customMode=true;
	hm.da.switchMode(hm.da.MODE_NEW, hm.da.C_WRITE );
	hm.da.disabledRemoveNewTabButton();
	var newItemTd = Get("da_td_tab_new");
	var td = hm.da.createTempNewTabTD("New");
	newItemTd.parentNode.insertBefore(td,newItemTd);

	if (hm.da.callback.createNewTabDone) {
		hm.da.callback.createNewTabDone();
	}
	sizeLeftDaGroup();
	hm.da.resetTabLength();
}

hm.da.createCloneTab = function() {
	if (customMode || !writePermission) {
		return false;
	}
	if(currentDashBoardId==null /* todo cannot drill down|| isDrilldownDash()*/) {
		return false;
	}
	customMode=true;
	hm.da.switchMode(hm.da.MODE_NEW, hm.da.C_WRITE );
	hm.da.disabledRemoveNewTabButton();
	var newItemTd = Get("da_td_tab_new");
	var td = hm.da.createTempNewTabTD("Clone");
	newItemTd.parentNode.insertBefore(td,newItemTd);
	cloneTabFlg=true;
	if (hm.da.callback.createCloneTabDone) {
		hm.da.callback.createCloneTabDone();
	}
	sizeLeftDaGroup();
	hm.da.resetTabLength();
}

hm.da.createTempNewTabTD = function (label) {
	var td=hm.da.createTD(null,hm.da.TAB_HEIGHT, "da_td_tab_new_temp", null, "dac_td_tab_link_sel");
	var ctable=document.createElement("table");
	ctable.setAttribute("cellspacing","0px");
	ctable.setAttribute("cellpadding","0px");
	ctable.setAttribute("border","0");

	var ctbody=document.createElement("tbody");
	var ctr = document.createElement("tr");
	var ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_left");
	ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
	ctr.appendChild(ctd);
	ctd = hm.da.createTD(null,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_center");
	ctd.setAttribute("nowrap", "nowrap");
	var a = hm.da.createA(null, label, null, null, null);
	ctd.appendChild(a);
	ctr.appendChild(ctd);
	ctd = hm.da.createTD(20,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_center_remove");
	//a = hm.da.createA(null, null, null,null, null, null);
	//var img = hm.da.createImage(imagesBaseUrl + "/dashboard/uclose.png",9, 9, "dac_img_tab_close","Remove", "right");
	//a.appendChild(img);
	//ctd.appendChild(a);
	ctr.appendChild(ctd);
	ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_right");
	ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
	ctr.appendChild(ctd);
	ctbody.appendChild(ctr);
	ctable.appendChild(ctbody);
	td.appendChild(ctable);
	
	$("#da_tr_tab_td" + currentDashBoardId).removeClass("dac_td_tab_link_sel");
	$("#da_tr_tab_td" + currentDashBoardId).addClass("dac_td_tab_link");
	//if ($("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").length>0) {
	//	$("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").hide();
	//}
	return td;
}

hm.da.succSaveNewTab=function(data){
	if (!data) {
		hm.da.hideWaitingPanel();
		showWarnDialog("Save Tab Error. Session timeout.", "Error");
		return false;
	}

	if (data.t) {
		$("#da_tr_tab_td" + currentDashBoardId).removeClass("dac_td_tab_link_sel");
		$("#da_tr_tab_td" + currentDashBoardId).addClass("dac_td_tab_link");
		currentDashBoardId=data.dId;
		currentDashBoardType=data.daType;
		
		changeCustomTime(data.tmtp, false);
		if(data.tmtp==4) {
			Get("da_span_custom_time_detail").innerHTML=data.tmval;
		}
		eval("var data2 = " + data.v);
		eval("var data3 = " + data.v3);
		Get("da_div_group_filter_tree").innerHTML='';
		Get("da_div_group_filter_userpro_tree").innerHTML='';
		//hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree");
		hm.da.createTree(data.obId, "da_div_group_filter_tree", data2);
		hm.da.createTree(data.fobId, "da_div_group_filter_userpro_tree", data3);
		
		hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_READ );
		hm.da.enabledRemoveNewTabButton();
		Get("da_tr_tab").removeChild(Get("da_td_tab_new_temp"));
		cloneTabFlg=false;

		var newItemTd = Get("da_td_tab_new");
		var td = hm.da.createNewAddTD(data.dId, data.name);
		newItemTd.parentNode.insertBefore(td,newItemTd);
		$("#da_tr_tab_td" + data.dId).addClass("dac_td_tab_link_sel");
		
		//if ($("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").length>0) {
		//	$("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").show();
		//}

		sizeLeftDaGroup();
		hm.da.resetTabLength();

		hm.da.resetHighIntervalTimeOut(data);

		if (hm.da.callback.succSaveNewTabDone) {
			hm.da.callback.succSaveNewTabDone(currentDashBoardId);
		}

		var dashboardTmp = getCurrentDashboard();
		if (dashboardTmp && data.d) {
			dashboardTmp.refreshDaConfigsAfterSuccSaving(data.d);
		}
		setCustomMode(false);
		resetTableMouseAction();
	} else {
		hm.util.displayJsonErrorNote(data.m);
	}
	hm.da.hideWaitingPanel();
}

hm.da.resetHighIntervalTimeOut = function (data){
	// high ap appliction interval
	if(data.dailog) {
		//fnr todo
		showWarnDialog(data.dailog, "Warnning");
	}
	if(data.timeo) {
		if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + data.dId]){
			clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + data.dId]);
		}
		var tmFction = "hm.da.dispalyHighIntervalWarn(\"" + data.name + "\",\"" + data.dId +"\")";
		highApTimeOutArray["warningHighIntervalAPTimeoutId" + data.dId] = setTimeout(tmFction, appWarningDailogTimeout);
	} else {
		if (data.dId) {
			if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + data.dId]){
				clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + data.dId]);
			}
		}
	}
}

hm.da.createNewAddTD= function (id, text){
	var td=hm.da.createTD(null,hm.da.TAB_HEIGHT, "da_tr_tab_td" + id, null, "dac_td_tab_link_sel");

	var ctable=document.createElement("table");
	ctable.setAttribute("cellspacing","0px");
	ctable.setAttribute("cellpadding","0px");
	ctable.setAttribute("border","0");
	ctable.className="dac_table_tab_class";

	var ctbody=document.createElement("tbody");
	var ctr = document.createElement("tr");
	var ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_left");
	ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
	ctr.appendChild(ctd);

	ctd = hm.da.createTD(null,hm.da.TAB_HEIGHT, "renameTab_alink_td_id_" + id, null, "dac_td_tab_center");
	ctd.setAttribute("nowrap", "nowrap");
	var a = hm.da.createA(hm.da.changeTab, text, "renameTab_alink_id_" + id, null, id, null,hm.da.displayTextField);
	ctd.appendChild(a);
	ctr.appendChild(ctd);

	ctd = hm.da.createTD(null,hm.da.TAB_HEIGHT, "renameTab_text_td_id_" + id, null, "dac_td_tab_center_changeTab");
	ctd.setAttribute("nowrap", "nowrap");
	ctd.style.display="none";
	var tx = hm.da.createTextBox(text,id);
	ctd.appendChild(tx);
	ctr.appendChild(ctd);


	ctd = hm.da.createTD(20,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_center_remove");
	a = hm.da.createA(hm.da.displayRemoveDlg, null, null,null, id, hm.da.removetabTD);
	var img;
	if (writePermission) {
		img = hm.da.createImage(imagesBaseUrl + "/dashboard/aclose.png",9, 9, "dac_img_tab_close","", "right");
	} else {
		img = hm.da.createImage(imagesBaseUrl + "/dashboard/uclose.png",9, 9, "dac_img_tab_close","", "right");
	}
	a.appendChild(img);
	ctd.appendChild(a);
	ctr.appendChild(ctd);
	ctd = hm.da.createTD(hm.da.TAB_LEFTRIGHT,hm.da.TAB_HEIGHT, null, null, "dac_td_tab_right");
	ctd.innerHTML="&nbsp;&nbsp;&nbsp;";
	ctr.appendChild(ctd);
	ctbody.appendChild(ctr);
	ctable.appendChild(ctbody);
	td.appendChild(ctable);
	return td;
}
hm.da.newMonitorTab = function (tabType, options) {
	if (customMode) {
		return false;
	}

	hm.da.succNewMonitorTab=function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Open Monitor Tab Error. Session timeout.", "Error");
			return false;
		}

		if (data.t) {
			$("#da_tr_tab_td" + currentDashBoardId).removeClass("dac_td_tab_link_sel");
			$("#da_tr_tab_td" + currentDashBoardId).addClass("dac_td_tab_link");
			//if ($("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").length>0) {
			//	$("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").hide();
			//}
			currentDashBoardId=data.dId;
			currentDashBoardType=data.daType;
			
			changeCustomTime(data.tmtp, false);
			if(data.tmtp==4) {
				Get("da_span_custom_time_detail").innerHTML=data.tmval;
			}
			eval("var data2 = " + data.v);
			eval("var data3 = " + data.v3);
			Get("da_div_group_filter_tree").innerHTML='';
			Get("da_div_group_filter_userpro_tree").innerHTML='';
			//hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree");
			hm.da.createTree(data.obId, "da_div_group_filter_tree", data2);
			hm.da.createTree(data.fobId, "da_div_group_filter_userpro_tree", data3);
			
			hm.da.switchMode(hm.da.MODE_DRILL, hm.da.C_READ );
			hm.da.enabledRemoveNewTabButton();
			cloneTabFlg=false;

			var newItemTd = Get("da_td_tab_new");
			var td = hm.da.createNewAddTD(data.dId, data.name);
			newItemTd.parentNode.insertBefore(td,newItemTd);
			$("#da_tr_tab_td" + data.dId).addClass("dac_td_tab_link_sel");
			
			//if ($("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").length>0) {
			//	$("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").show();
			//}
			sizeLeftDaGroup();
			hm.da.resetTabLength();
			var dashboardTmp = getCurrentDashboard();
			if (dashboardTmp && data.d) {
				dashboardTmp.refreshDaConfigsAfterSuccSaving(data.d);
			}

			if (hm.da.callback.succNewMonitorTabDone) {
				hm.da.callback.succNewMonitorTabDone(currentDashBoardId, data.name);
			}
			setCustomMode(false);
			resetTableMouseAction();
		} else {
			showWarnDialog(data.m, "Error");
			//hm.util.displayJsonErrorNote(data.m);
		}
	}

	var url = "dashboard.action?operation=newMonitorTab&tabName=" + hm.util.encodeForURIArg(options.tabName)
				+ "&drilldownNode=" + hm.util.encodeForURIArg(options.monitorEl)
				+ "&widgetConfigId=" + options.reportId
				+ "&widgetId=" + options.widgetId
				+ "&monitorType="+ tabType + "&ignore="+new Date().getTime();
	if (options.metric) {
		url += "&ddMetricKey=" + options.metric;
	}
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succNewMonitorTab, timeout: 60000});
}

hm.da.showWaitingPanel=function(){
	if (waitingPanel==null) {
		createWaitingPanel();
	}
	waitingPanel.show();
}
hm.da.hideWaitingPanel=function(){
	if (waitingPanel!=null) {
		waitingPanel.hide();
	}
}

hm.da.newDrillTab = function (tabType,tabName,ddInfo) {
	if (customMode) {
		return false;
	}

	hm.da.succNewDrillTab=function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Open Drill down Tab Error. Session timeout.", "Error");
			return false;
		}

		if (data.t) {
			$("#da_tr_tab_td" + currentDashBoardId).removeClass("dac_td_tab_link_sel");
			$("#da_tr_tab_td" + currentDashBoardId).addClass("dac_td_tab_link");
			currentDashBoardId=data.dId;
			currentDashBoardType=data.daType;
			hm.da.switchMode(hm.da.MODE_DRILL, hm.da.C_READ );
			hm.da.enabledRemoveNewTabButton();
			cloneTabFlg=false;
			var newItemTd = Get("da_td_tab_new");
			var td = hm.da.createNewAddTD(data.dId, data.name);
			newItemTd.parentNode.insertBefore(td,newItemTd);
			$("#da_tr_tab_td" + data.dId).addClass("dac_td_tab_link_sel");
			
			//if ($("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").length>0) {
			//	$("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").show();
			//}
			sizeLeftDaGroup();
			hm.da.resetTabLength();

			if (hm.da.callback.succStartDrilldownTabDone) {
				hm.da.callback.succStartDrilldownTabDone(currentDashBoardId);
			}
			setCustomMode(false);
			resetTableMouseAction();
		} else {
			hm.util.displayJsonErrorNote(data.m);
		}
	}

	var url = "dashboard.action?operation=newDrillTab&tabName=" + tabName
					+ "&monitorType="+ tabType
					+ "&drilldownType=" + ddInfo.drilldownType
					+ "&drilldownNode=" + ddInfo.node
					+ "&bkType=" + ddInfo.bkType
					+ "&bkValue=" + ddInfo.bkValue
					+ "&isSwitch=" + ddInfo.isSwitch
					+ "&blnDrilldownOvertime=" + ddInfo.overtime
					+ "&drilldownTime=" + ddInfo.ctime
					+ "&ddMetricKey=" + ddInfo.metric
					+ "&widgetId=" + ddInfo.widgetId
					+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', hm.util.encodeURI(url),  {success : hm.da.succNewDrillTab, timeout: 60000});
}

hm.da.changeTab = function (id) {
	if (customMode) {
		return false;
	}
	if ($("#da_tr_tab_td" + id).attr("class") =='dac_td_tab_link_sel'){
		return false;
	}
	if ($("#da_tr_tab_td" + id).attr("class") =='dac_td_tab_link_changeTab'){
		return false;
	}
	YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + id), "dac_td_tab_link");
	YAHOO.util.Dom.addClass(Get("da_tr_tab_td" + id), "dac_td_tab_link_changeTab");
	hm.da.showWaitingPanel();
	hm.da.succChangeTabInfo= function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + id), "dac_td_tab_link_changeTab");
			YAHOO.util.Dom.addClass(Get("da_tr_tab_td" + id), "dac_td_tab_link");
			hm.da.hideWaitingPanel();
			resetTableMouseAction();
			showWarnDialog("Change Tab Error. Session timeout.", "Error");
			return false;
		}

		if (data.t) {
			YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + data.dId), "dac_td_tab_link_changeTab");
			YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + currentDashBoardId), "dac_td_tab_link_sel");
			YAHOO.util.Dom.addClass(Get("da_tr_tab_td" + currentDashBoardId), "dac_td_tab_link");
			YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + data.dId), "dac_td_tab_link");
			YAHOO.util.Dom.addClass(Get("da_tr_tab_td" + data.dId), "dac_td_tab_link_sel");
			
			//if ($("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").length>0) {
			//	$("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").show();
			//}
			//if ($("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").length>0) {
			//	$("#da_tr_tab_td" + currentDashBoardId).find(".dac_td_tab_center_remove").hide();
			//}
			currentDashBoardId=data.dId;
			currentDashBoardType=data.daType;
			
			if(data.drill) {
				hm.da.switchMode(hm.da.MODE_DRILL, hm.da.C_READ);
			} else {
				hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_READ);
				changeCustomTime(data.tmtp, false);
				if(data.tmtp==4) {
					Get("da_span_custom_time_detail").innerHTML=data.tmval;
				}
				eval("var data2 = " + data.v);
				eval("var data3 = " + data.v3);
				Get("da_div_group_filter_tree").innerHTML='';
				Get("da_div_group_filter_userpro_tree").innerHTML='';
				//hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree");
				hm.da.createTree(data.obId, "da_div_group_filter_tree", data2);
				hm.da.createTree(data.fobId, "da_div_group_filter_userpro_tree", data3);
				
				hm.da.resetHighIntervalTimeOut(data);
			}
			if (hm.da.callback.changeTabDone) {
				hm.da.callback.changeTabDone(currentDashBoardId);
			}
		} else {
			YAHOO.util.Dom.removeClass(Get("da_tr_tab_td" + id), "dac_td_tab_link_changeTab");
			YAHOO.util.Dom.addClass(Get("da_tr_tab_td" + id), "dac_td_tab_link");
			hm.util.displayJsonErrorNote(data.m);
		}
		hm.da.hideWaitingPanel();
		resetTableMouseAction();
	};

	var url = "dashboard.action?operation=changeTab&tabChangeId=" + id +"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succChangeTabInfo, timeout: 60000});
}

hm.da.displayRemoveDlg = function (id, callFun) {
	beforeSubmitAction(document.forms[formName]);
	if (customMode || !writePermission) {
		return false;
	}
	if(defaultTabId!= null && id==defaultTabId) {
		showWarnDialog("The default dashboard perspective cannot be removed.", "Error");
		return false;
	}
	if(applicationPerspectiveId!= null && id==applicationPerspectiveId){
		showWarnDialog(appInvalidMsg, "Error");
		return false;
	}
	 var cancelBtn = function(){
        this.hide();
    };
    var mybuttons = [ { text:"Yes", handler: function(){this.hide();callFun(id);} },
                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
    var warningMsg = "<html><body>This operation will remove the selected item.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?</body></html>";
    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
    dlg.show();
}

hm.da.removetabTD =function (id) {
	hm.da.succRemoveTabInfo= function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Remove Tab Error. Session timeout.", "Error");
			return false;
		}

		if (data.t) {
			var rmObj = document.getElementById("da_tr_tab_td" + data.rid);
			rmObj.parentNode.removeChild(rmObj);
			hm.util.displayJsonInfoNote(data.m);
			if(data.op){
				currentDashBoardId=data.dId;
				currentDashBoardType=data.daType;
				
				hm.da.switchMode(hm.da.MODE_VIEW, hm.da.C_READ);
				changeCustomTime(data.tmtp, false);
				if(data.tmtp==4) {
					Get("da_span_custom_time_detail").innerHTML=data.tmval;
				}
				eval("var data2 = " + data.v);
				eval("var data3 = " + data.v3);
				Get("da_div_group_filter_tree").innerHTML='';
				Get("da_div_group_filter_userpro_tree").innerHTML='';
				//hm.da.initSelectWidgetNode(data.map,"da_div_group_topy_tree");
				hm.da.createTree(data.obId, "da_div_group_filter_tree", data2);
				hm.da.createTree(data.fobId, "da_div_group_filter_userpro_tree", data3);
				$("#da_tr_tab_td" + data.dId).removeClass("dac_td_tab_link");
				$("#da_tr_tab_td" + data.dId).addClass("dac_td_tab_link_sel");
				//if ($("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").length>0) {
				//	$("#da_tr_tab_td" + data.dId).find(".dac_td_tab_center_remove").show();
				//}

				if (hm.da.callback.removeTabDone) {
					hm.da.callback.removeTabDone(id, currentDashBoardId);
				}
			}
			hm.da.resetTabLength();
			resetTableMouseAction();
		} else {
			hm.util.displayJsonErrorNote(data.m);
		}
	};

	var rmObj = document.getElementById("da_tr_tab_td" + id);
	if (YAHOO.util.Dom.hasClass(rmObj,'dac_td_tab_link_sel')) {
		var url = "dashboard.action?operation=removeTabActive&tabRemoveId=" + id +"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succRemoveTabInfo, timeout: 60000});
	} else {
		var url = "dashboard.action?operation=removeTab&tabRemoveId=" + id +"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succRemoveTabInfo, timeout: 60000});

	}
}

hm.da.createTD = function (width,height,id, text,className) {
	var td=document.createElement("td");
	if (width) {
		td.width = width;
	}
	if (height) {
		td.height = height;
	}
	if (id) {
		td.setAttribute("id",id);
	}
	if (text) {
		td.innerHTML = text;
	}
	if (className) {
		td.className = className;
	}
	return td;
}

hm.da.displayTextFieldKeypress= function(e, mid){
	var keycode;
	if(window.event) // IE
	{
		keycode = window.event.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
	} else {
		return true;
	}

	if (keycode==34) return false;
	if (keycode==13) {
		Get("renameTab_text_id_"+ mid).blur();
		return true;
	} else {
		return true;
	}
};

hm.da.displayTextFieldBlur=function(mid){
	if (Get("renameTab_text_id_"+ mid).value.trim().length == 0){
		Get("renameTab_text_td_id_"+ mid).style.display="none";
		Get("renameTab_alink_td_id_"+ mid).style.display="";
		showWarnDialog("Please enter a perspective name.", "Error");
		return;
	}
	if (Get("renameTab_text_id_"+ mid).value.indexOf("\"")>0) {
		Get("renameTab_text_td_id_"+ mid).style.display="none";
		Get("renameTab_alink_td_id_"+ mid).style.display="";
		showWarnDialog("Perspective name cannot contain quotation marks (\").", "Error");
		return;
	}

	if (Get("renameTab_text_id_"+ mid).value == Get("renameTab_alink_id_"+ mid).title) {
		Get("renameTab_text_td_id_"+ mid).style.display="none";
		Get("renameTab_alink_td_id_"+ mid).style.display="";
		return ;
	}

	Get("renameTab_text_td_id_"+ mid).style.display="none";
	Get("renameTab_alink_td_id_"+ mid).style.display="";
	hm.da.changeTabName(mid,Get("renameTab_text_id_"+ mid).value);

};

hm.da.changeTabName=function(mid, tabName){
	hm.da.succChangeTabName= function(o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Change Tab Name Error. Session timeout.", "Error");
			//Get("renameTab_alink_id_"+ mid).innerHTML=Get("renameTab_text_id_"+ mid).value;
			//Get("renameTab_alink_id_"+ mid).title=Get("renameTab_text_id_"+ mid).value;
			return false;
		}
		if (data.m) {
			showWarnDialog("Change Tab Name Error. Session timeout.", "Error");
		}
		if (data.n) {
			showWarnDialog(data.n, "Error");
		}
		if (data.t) {
			Get("renameTab_alink_id_"+ mid).innerHTML=tabName;
			Get("renameTab_alink_id_"+ mid).title=tabName;
			hm.da.resetTabLength();
		}
	};

	var url = "dashboard.action?operation=changeTabName&tabChangeId=" + mid + "&tabName=" + encodeURIComponent(tabName) +"&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succChangeTabName, timeout: 60000});
}

hm.da.displayTextField = function (value, clickItemID) {
	if (customMode || !writePermission) {
		return false;
	}
	if ($("#da_tr_tab_td" + clickItemID).attr("class") !='dac_td_tab_link_sel'){
		return false;
	}
	Get("renameTab_alink_td_id_" + clickItemID).style.display="none";
	Get("renameTab_text_td_id_" + clickItemID).style.display="";
	Get("renameTab_text_id_" + + clickItemID).value=value;
	Get("renameTab_text_id_" + + clickItemID).focus();
	Get("renameTab_text_id_" + + clickItemID).select();
}

hm.da.createTextBox=function(text,myId){
	var tx = document.createElement("input");
	tx.type="text";
	tx.id="renameTab_text_id_" + myId;
	tx.maxLength=64;
	tx.size=24;
	tx.value=text;
	tx.onkeypress=  function (e) {return hm.da.displayTextFieldKeypress(e,myId);};
	tx.onblur=function(){hm.da.displayTextFieldBlur(myId);};
	return tx;
}

hm.da.createA = function (clickfc,text, id,className, fcPara1, fcPara2, dblClickfc) {
	var a = document.createElement("a");
	a.setAttribute("href","javascript:void(0);");
	if (clickfc) {
		a.onclick = function() { if (fcPara1 && fcPara2) {clickfc(fcPara1,fcPara2);} else if (fcPara1) {clickfc(fcPara1);} else {clickfc();} };
	} else {
		a.onclick= function(){ ignoreCheck = true;return false;}
	}

	if (dblClickfc) {
		a.ondblclick=function () {dblClickfc(Get(id).title,fcPara1);};
	}
	if (text) {
		a.innerHTML = text;
		a.title=text;
	}
	if (className) {
		a.className = className;
	}
	if (id) {
		a.setAttribute("id",id);
	}
	return a;
}
hm.da.createImage=function (src,width, height, className,title,align) {
	var img = hm.util.loadImage(src);
	if (img ==null) return null;
	if (width) {
		img.width = width;
	}
	if (height) {
		img.height = height;
	}
	if (className) {
		img.className = className;
	}
	if (title) {
		img.title = title;
	}
	if (align) {
		img.align = align;
	} else {
		img.align = "absmiddle";
	}
	return img;
}

var mapTree = null;
var labelMapClicked = null;
var labelFilterClicked = null;
var labelFilterUpClicked = null;
var selectedMapNode = null;
var selectedFilterNode = null;
var selectedFilterUpNode = null;

var widgetMapTree = null;
var widgetLabelMapClicked = null;
var widgetLabelFilterClicked = null;
var widgetLabelFilterUpClicked = null;
var widgetSelectedMapNode = null;
var widgetSelectedFilterNode = null;
var widgetSelectedFilterUpNode = null;

hm.da.createTree = function (selectGroupId, treeViewDivId, mapHierarchy) {
	mapTree = new YAHOO.widget.TreeView(treeViewDivId);
	hm.da.populateTree(mapHierarchy, mapTree.getRoot(), 0);

	hm.da.nodeLableClickReal=function(node,treeViewDivId) {
		hm.da.highlightNode(node,treeViewDivId);
		if (treeViewDivId=="da_div_group_topy_tree") {
			labelMapClicked = node.label;
			var url = formName + ".action?operation=changeTopyTree&treeType=" + node.data.tp + "&treeId=" + encodeURIComponent(node.data.id) +"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : function(o) {
						hm.da.succChangeTreeNode(o, function(chart, checked){
							if (checked) {
								return;
							}
							this.setTmpReportConfig(chart.container,
									{
										'lid': node.data.id
									}, true);
						});
				}, timeout: 60000});
		} else if (treeViewDivId=="da_div_group_filter_tree") {
			labelFilterClicked = node.label;
			if (formName=='dashboard'){
				hm.da.showWaitingPanel();
			}
			var url = formName + ".action?operation=changeFilterTree&treeType=" + node.data.tp + "&treeId=" + encodeURIComponent(node.data.id) +"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : function(o) {
						hm.da.succChangeTreeNode(o, function(chart, checked){
							if (checked) {
								return;
							}
							this.setTmpReportConfig(chart.container,
									{
										'obId': node.data.id,
										'obType': node.data.tp
									}, true);
						});
					}, timeout: 60000});
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree") {
			labelFilterUpClicked = node.label;
			var url = formName + ".action?operation=changeFilterUpTree&treeType=" + node.data.tp + "&treeId=" + encodeURIComponent(node.data.id) +"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : function(o) {
					hm.da.succChangeTreeNode(o, function(chart, checked){
						if (checked) {
							return;
						}
						this.setTmpReportConfig(chart.container,
								{
									'fobId': node.data.id,
									'fobType': node.data.tp
								}, true);
					});
				}, timeout: 60000});
		}
	}
	
	mapTree.subscribe("labelClick", function(node) {
		if (customMode) {
			return false;
		}
		if (!isDash()) {
			return false;
		}
		hm.da.succFetchDetailDevice= function(o){
			try {
				eval("var data = " + o.responseText);
			}catch(e){
				showWarnDialog("fetch Detail Device Error. Session timeout.", "Error");
				return false;
			}

			if (data.t) {
				if (data.v) {
					for(var i = 0; i < data.v.length; i++) {
						var tmpNode = new YAHOO.widget.TextNode({
					        label: data.v[i].label,
					        title: data.v[i].title,
					        id: data.v[i].id,
					        tp: data.v[i].tp,
					        expanded: data.v[i].expanded});
						 tmpNode.insertBefore(node);
					}
				}
				 node.parent.refresh();
				 hm.da.highlightNode(selectedFilterNode,treeViewDivId);
			} else {
				showWarnDialog(data.m, "Error");
			}
		};

		if (node.data.tp == "-11") {
			//alert("to do fetch device." + node.parent.children.length);
			var url = formName + ".action?operation=fetchDetailDevice&offset=" + (node.parent.children.length - 1)  
				//+ "&widgetLocationId=" + selectedMapNode.data.id 
				+ "&treeId=" + encodeURIComponent(node.parent.data.id) + "&treeType=" + node.parent.data.tp
				+ "&filterObjectType=" + selectedFilterNode.data.tp + "&filterObjectId=" + encodeURIComponent(selectedFilterNode.data.id)
				+ "&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succFetchDetailDevice, timeout: 60000});
			return false;
		}
		
		hm.da.checkApNumberForAppSucc=function(o){
			try {
				eval("var data = " + o.responseText);
			}catch(e){
				showWarnDialog("Change Map Session timeout.", "Error");
				return false;
			}

			if (data.t) {
				if (data.w) {
					var cancelBtn = function(){
				        this.hide();
				    };
				    var mybuttons = [ { text:"Continue", handler: function(){this.hide();hm.da.nodeLableClickReal(node,treeViewDivId);} },
				                      { text:"Cancel", handler: cancelBtn, isDefault:true} ];
				    var warningMsg = "<html><body>" + data.w+ "</body></html>";
				    var dlg = userDefinedConfirmDialog(warningMsg, mybuttons, "Warning");
				    dlg.show();
				} else {
					hm.da.nodeLableClickReal(node,treeViewDivId);
				}
			} else {
				showWarnDialog(data.m, "Error");
			}
		}
		
		if (treeViewDivId=="da_div_group_filter_tree" && formName=='dashboard' && applicationPerspectiveId == currentDashBoardId) {
			var url = formName + ".action?operation=checkApNumberForApp"
			+ "&treeId=" + encodeURIComponent(node.data.id) + "&treeType=" + node.data.tp
			+ "&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.checkApNumberForAppSucc, timeout: 60000});
			return false;
		} else {
			hm.da.nodeLableClickReal(node,treeViewDivId);
		}
		
		//alert(node.data.id);
		// to do submit;
	});
	mapTree.subscribe("clickEvent", function(node) {
		sizeLeftDaGroup();
	});
	mapTree.subscribe("expand", function(node) {
		if (treeViewDivId=="da_div_group_topy_tree") {
			if (labelMapClicked == node.label) {
				labelMapClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_tree") {
			if (labelFilterClicked == node.label) {
				labelFilterClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree") {
			if (labelFilterUpClicked == node.label) {
				labelFilterUpClicked = null;
				return false;
			}
		}

	});
	mapTree.subscribe("expandComplete", function(node) {
		sizeLeftDaGroup();
	});

	mapTree.subscribe("collapseComplete", function(node) {
		sizeLeftDaGroup();
	});

	mapTree.subscribe("collapse", function(node) {
		if (treeViewDivId=="da_div_group_topy_tree") {
			if (labelMapClicked == node.label) {
				labelMapClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_tree") {
			if (labelFilterClicked == node.label) {
				labelFilterClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree") {
			if (labelFilterUpClicked == node.label) {
				labelFilterUpClicked = null;
				return false;
			}
		}
	});
	mapTree.render();
	if (selectGroupId!=null && selectGroupId!= undefined) {
		var cuNode = hm.da.findTreeNodeByDataId(mapTree.getRoot(), selectGroupId);
		if (cuNode!=null && cuNode!= undefined) {
			hm.da.highlightNode(cuNode,treeViewDivId);
		}
	}
	sizeLeftDaGroup();
}

hm.da.createTreeWidget = function (selectGroupId, treeViewDivId, mapHierarchy) {
	widgetMapTree = new YAHOO.widget.TreeView(treeViewDivId);
	hm.da.populateTree(mapHierarchy, widgetMapTree.getRoot(), 0);
	widgetMapTree.subscribe("labelClick", function(node) {
		//alert(treeViewDivId);
		hm.da.succFetchWidgetDetailDevice= function(o){
			try {
				eval("var data = " + o.responseText);
			}catch(e){
				showWarnDialog("Fetch Widget Detail Device Error. Session timeout.", "Error");
				return false;
			}

			if (data.t) {
				if (data.v) {
					for(var i = 0; i < data.v.length; i++) {
						var tmpNode = new YAHOO.widget.TextNode({
					        label: data.v[i].label,
					        title: data.v[i].title,
					        id: data.v[i].id,
					        tp: data.v[i].tp,
					        expanded: data.v[i].expanded});
						 tmpNode.insertBefore(node);
					}
				}
				 node.parent.refresh();
				 hm.da.highlightNode(widgetSelectedFilterNode,treeViewDivId);
			} else {
				showWarnDialog(data.m, "Error");
			}
		};

		if (node.data.tp == "-11") {
			//alert("to do fetch device." + node.parent.children.length);
			var url = formName + ".action?operation=fetchDetailDevice&offset=" + (node.parent.children.length - 1)  
				//+ "&widgetLocationId=" + widgetSelectedMapNode.data.id 
				+ "&treeId=" + encodeURIComponent(node.parent.data.id) + "&treeType=" + node.parent.data.tp
				+ "&filterObjectType=" + widgetSelectedFilterNode.data.tp + "&filterObjectId=" + encodeURIComponent(widgetSelectedFilterNode.data.id)
				+ "&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succFetchWidgetDetailDevice, timeout: 60000});
			return false;
		}

		hm.da.highlightNode(node,treeViewDivId);

		if (treeViewDivId=="da_div_group_topy_tree1") {
			widgetLabelMapClicked = node.label;
			var url = formName + ".action?operation=changeWidgetTopyTree&treeType=" + node.data.tp + "&treeId=" + encodeURIComponent(node.data.id) +"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succChangeWidgetTreeNode, timeout: 60000});
		} else if (treeViewDivId=="da_div_group_filter_tree1") {
			widgetLabelFilterClicked = node.label;
			var url = formName + ".action?operation=changeWidgetFilterTree&treeType=" + node.data.tp + "&treeId=" + encodeURIComponent(node.data.id)
				//+ "&widgetLocationId=" + widgetSelectedMapNode.data.id
				+"&ignore="+new Date().getTime();
			var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succChangeWidgetTreeNode, timeout: 60000});
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree1") {
			widgetLabelFilterUpClicked = node.label;
		}
		// to do submit;
	});

	widgetMapTree.subscribe("expand", function(node) {
		if (treeViewDivId=="da_div_group_topy_tree1") {
			if (widgetLabelMapClicked == node.label) {
				widgetLabelMapClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_tree1") {
			if (widgetLabelFilterClicked == node.label) {
				widgetLabelFilterClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree1") {
			if (widgetLabelFilterUpClicked == node.label) {
				widgetLabelFilterUpClicked = null;
				return false;
			}
		}

	});

	widgetMapTree.subscribe("clickEvent", function(node) {

	});

	widgetMapTree.subscribe("collapse", function(node) {
		if (treeViewDivId=="da_div_group_topy_tree1") {
			if (widgetLabelMapClicked == node.label) {
				widgetLabelMapClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_tree1") {
			if (widgetLabelFilterClicked == node.label) {
				widgetLabelFilterClicked = null;
				return false;
			}
		} else if (treeViewDivId=="da_div_group_filter_userpro_tree1") {
			if (widgetLabelFilterUpClicked == node.label) {
				widgetLabelFilterUpClicked = null;
				return false;
			}
		}
	});
	widgetMapTree.render();
	if (selectGroupId!=null && selectGroupId!= undefined) {
		var cuNode = hm.da.findTreeNodeByDataId(widgetMapTree.getRoot(), selectGroupId);
		if (cuNode!=null && cuNode!= undefined) {
			hm.da.highlightNode(cuNode,treeViewDivId);
		}
	}
}

hm.da.populateTree=function(items, parentNode, depth) {
	if (items!=undefined) {
		for (var i = 0; i < items.length; i++) {
			if(items[i] != undefined){
				var node = new YAHOO.widget.TextNode(items[i], parentNode); // depth
																			// < 2
				node.title = null;  // Interferes with menus
				// href not works, trigger in event instead
				// node.href = "javascript:nodeSelected(" + node.index + ")";
				hm.da.populateTree(items[i].items, node, depth+1);
			}
		}
	}
}

/*
 * Find tree node
 */
hm.da.findTreeNodeByDataId=function (treeNode, nodeDataId) {
	for (var i = 0; i < treeNode.children.length; i++) {
		if (treeNode.children[i].data.id == nodeDataId) {
			return treeNode.children[i];
		} else {
			var node = hm.da.findTreeNodeByDataId(treeNode.children[i], nodeDataId);
			if (node != null) {
				return node;
			}
		}
	}
	return null;
}


hm.da.highlightNode = function(node,treeViewDivId) {
	// expands parent nodes
	var parentNode = node.parent;
	while(null != parentNode.parent){
		parentNode.expand();
		parentNode = parentNode.parent;
	}
	if (treeViewDivId=="da_div_group_topy_tree"){
		if (selectedMapNode != null) {
			YAHOO.util.Dom.removeClass(selectedMapNode.labelElId, 'ygtvlabelSel');
		}
		selectedMapNode = node;
		YAHOO.util.Dom.addClass(selectedMapNode.labelElId, 'ygtvlabelSel');
	} else if (treeViewDivId=="da_div_group_filter_tree"){
		if (selectedFilterNode != null) {
			YAHOO.util.Dom.removeClass(selectedFilterNode.labelElId, 'ygtvlabelSel');
		}
		selectedFilterNode = node;
		YAHOO.util.Dom.addClass(selectedFilterNode.labelElId, 'ygtvlabelSel');
	} else if (treeViewDivId=="da_div_group_filter_userpro_tree"){
		if (selectedFilterUpNode != null) {
			YAHOO.util.Dom.removeClass(selectedFilterUpNode.labelElId, 'ygtvlabelSel');
		}
		selectedFilterUpNode = node;
		YAHOO.util.Dom.addClass(selectedFilterUpNode.labelElId, 'ygtvlabelSel');

	} else if (treeViewDivId=="da_div_group_topy_tree1"){
		if (widgetSelectedMapNode != null) {
			YAHOO.util.Dom.removeClass(widgetSelectedMapNode.labelElId, 'ygtvlabelSel');
		}
		widgetSelectedMapNode = node;
		YAHOO.util.Dom.addClass(widgetSelectedMapNode.labelElId, 'ygtvlabelSel');
	} else if (treeViewDivId=="da_div_group_filter_tree1"){
		if (widgetSelectedFilterNode != null) {
			YAHOO.util.Dom.removeClass(widgetSelectedFilterNode.labelElId, 'ygtvlabelSel');
		}
		widgetSelectedFilterNode = node;
		YAHOO.util.Dom.addClass(widgetSelectedFilterNode.labelElId, 'ygtvlabelSel');
	} else if (treeViewDivId=="da_div_group_filter_userpro_tree1"){
		if (widgetSelectedFilterUpNode != null) {
			YAHOO.util.Dom.removeClass(widgetSelectedFilterUpNode.labelElId, 'ygtvlabelSel');
		}
		widgetSelectedFilterUpNode = node;
		YAHOO.util.Dom.addClass(widgetSelectedFilterUpNode.labelElId, 'ygtvlabelSel');
	}
}

hm.da.initSelectWidgetNode=function(selectTreeId, treeViewDivId){
	if (selectTreeId!=null && selectTreeId!= undefined) {
		var cuNode = hm.da.findTreeNodeByDataId(YAHOO.widget.TreeView.getTree(treeViewDivId).getRoot(), selectTreeId);
		if (cuNode!=null && cuNode!= undefined) {
			hm.da.highlightNode(cuNode,treeViewDivId);
		}
	}
}
hm.da.saveWidgetGroupPanel = function(callback) {
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp) {
		showWarnDialog("No dashboard is defined.", "Error");
		return false;
	}
	var result;
	if (callback) {
		result = callback.apply(dashboardTmp);
	}
	if (typeof result === 'undefined' || result) {
		hideWidgetGroupPanel();
	}
}

var changeTreeNodeTimeoutId;

hm.da.succChangeTreeNode= function(o, callback){
	try {
		eval("var data = " + o.responseText);
	}catch(e){
		hm.da.hideWaitingPanel();
		showWarnDialog("Change tree node Error. Session timeout.", "Error");
		return false;
	}

	if (data.t) {
		if (data.v) {
			eval("var data2 = " + data.v);
			Get("da_div_group_filter_tree").innerHTML='';
			hm.da.createTree("-2", "da_div_group_filter_tree", data2);
		}
		if (data.v3) {
			eval("var data3 = " + data.v3);
			Get("da_div_group_filter_userpro_tree").innerHTML='';
			hm.da.createTree("-2", "da_div_group_filter_userpro_tree", data3);
		}
		if (formName=='dashboard'){
			hm.da.resetHighIntervalTimeOut(data);
			clearTimeout(changeTreeNodeTimeoutId);
			changeTreeNodeTimeoutId=setTimeout(function() { refreshDashboard({blnForceIntervalInit: true}); }, 2000);
		}

		if (callback) {
			var dashboardTmp = getCurrentDashboard();
			dashboardTmp.applyToCharts(function(chart) {
				var tmpTopoConfig = this.getCurReportWidgetConfig(chart);
				callback.apply(this, [chart, tmpTopoConfig.checked]);
			});
		}
	} else {
		hm.util.displayJsonErrorNote(data.m);
	}
	hm.da.hideWaitingPanel();
}

hm.da.succChangeWidgetTreeNode= function(o){
	try {
		eval("var data = " + o.responseText);
	}catch(e){
		showWarnDialog("Change tree node Error. Session timeout.", "Error");
		return false;
	}

	if (data.t) {
		if (data.v) {
			eval("var data2 = " + data.v);
			Get("da_div_group_filter_tree1").innerHTML='';
			hm.da.createTreeWidget("-2", "da_div_group_filter_tree1", data2);
		}
		if (data.v3) {
			eval("var data3 = " + data.v3);
			Get("da_div_group_filter_userpro_tree1").innerHTML='';
			hm.da.createTreeWidget("-2", "da_div_group_filter_userpro_tree1", data3);
		}
	} else {
		showWarnDialog(data.m, "Error");
	}
}


hm.da.fillSelect=function (selectObj, lst, selectedValue) {
	selectObj.length=0;
	selectObj.length=lst.length;
	for(var i = 0; i < lst.length; i++) {
		selectObj.options[i].text=lst[i].k;
		selectObj.options[i].value=lst[i].v;
	}
	if(selectedValue) {
		selectObj.value=selectedValue;
	}
}

hm.da.removeSelectObject= function(selectObj, value) {
	if(!value || selectObj==null) {
		return false;
	}
	for(var i=0; i<selectObj.length; i++){
		if(selectObj.options[i].value == value){
			selectObj.remove(i);
			selectObj.selectedIndex=0;
			break;
		}
	}
}

hm.da.initWidgetCopyDivValue = function (widgetId){
	hm.da.succInitWidgetCopyDivValue = function (o) {
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Init widget value Error. Session timeout.", "Error");
			return false;
		}

		if(data.t) {
			Get("da_select_wd_groupType").value=data.tp;
			Get("da_tx_wd_title").value=data.mm_n;
			hm.da.fillSelect(Get("da_select_wd_metric"),data.mm, data.mm_v);
		} else {
			hm.util.displayJsonErrorNote(data.m);
		}
	};
	if(null ==widgetId || widgetId==undefined) {
		widgetId=-1;
	}
	var url = formName + ".action?operation=initWidgetCopyDiv&widgetId=" + widgetId + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : hm.da.succInitWidgetCopyDivValue, timeout: 60000});
}

hm.da.checkWidgetDone=function (widgetId) {
	if(Get("da_select_wd_metric").value==-1) {
		return "Please select one premade data set.";
	}

	var message = hm.util.validateNameWithBlanks(Get("da_tx_wd_title").value, "Name");
	if (message != null) {
	    return message;
	}

	return null;
}

hm.da.saveWidgetCopyDiv = function (widgetId, dashboardTmp, chartContainer,editContent, divHtml) {
	beforeSubmitAction(document.forms[formName]);
	var url;
	if(null ==widgetId || widgetId==undefined) {
		url = formName + ".action?operation=saveWidgetCopyDiv&componentGroupType=" + Get("da_select_wd_groupType").value
		+ "&widgetName=" +Get("da_tx_wd_title").value
		+ "&preMetricId=" + Get("da_select_wd_metric").value
		+ "&ignore="+new Date().getTime();
	} else {
		url = formName + ".action?operation=saveWidgetCopyDiv&componentGroupType=" + Get("da_select_wd_groupType").value
		+ "&widgetName=" +Get("da_tx_wd_title").value
		+ "&preMetricId=" + Get("da_select_wd_metric").value
		+ "&widgetId=" + widgetId + "&ignore="+new Date().getTime();
	}

	var succSaveWidgetCopyDiv = function (o){
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Save widget value Error. Session timeout.", "Error");
			return false;
		}
		if(data.t) {
			dashboardTmp.setCurrentEditWidgetOptions({
				'title': $('#da_tx_wd_title').val()
			});
			$('#widgetCopyDiv').html(divHtml);
			$('#'+editContent).html('');
			dashboardTmp.toggleEditArea(chartContainer, false, true, data.v);
			if (data.dcc && widgetPanel) {
				widgetPanel.addWidget(data.dcc);
			}
			if(formName == "dashboard"){
				Get("da_bt_custom_save").style.display="";
			}
		} else {
			hm.util.displayJsonErrorNoteWithID(data.m,"widgetCopyErrorDiv");
		}
	};
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succSaveWidgetCopyDiv, timeout: 60000});

}

hm.da.changeComponentGroup = function (value) {
	var succChangeComponentGroup = function (o) {
		try {
			eval("var data = " + o.responseText);
		}catch(e){
			showWarnDialog("Change Group Type Error. Session timeout.", "Error");
			return false;
		}
		if(data.t) {
			Get("da_tx_wd_title").value=data.mm_n;
			hm.da.fillSelect(Get("da_select_wd_metric"),data.mm, data.mm_v);
		} else {
			hm.util.displayJsonErrorNote(data.m);
		}
	};

	var url = formName + ".action?operation=changeComponentGroup&componentGroupType=" + value + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succChangeComponentGroup, timeout: 60000});
};

hm.da.changeWidgetMetric = function (value) {
	Get("da_tx_wd_title").value=value;
}

hm.da.dispalyHighIntervalWarn = function (name, id) {
	var cancelBtn = function(){
		dlg.hide();
		if(udConfirmDialogForAppTimeoutID){
			clearTimeout(udConfirmDialogForAppTimeoutID);
		}
        hm.da.resetIntervalApp(id,name,false)
    };
    if(udConfirmDialogForApp!=null && udConfirmDialogForApp.cfg.getProperty('visible')==true){
    	if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]){
			clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]);
		}
		var tmFction = "hm.da.dispalyHighIntervalWarn(\"" + name + "\",\"" + id +"\")";
		highApTimeOutArray["warningHighIntervalAPTimeoutId" + id] = setTimeout(tmFction, 10000);
    } else {
    	//fnr todo
    	if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]){
			clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]);
		}
	    //var mybuttons = [ { text:"Continue", handler: function(){this.hide();hm.da.resetIntervalApp(id,name,true);} },
	    //                  { text:"OK", handler: cancelBtn, isDefault:true} ];
	    var mybuttons = [ { text:"OK", handler: cancelBtn, isDefault:true} ];
	    var warningMsg = "<html><body>Data collection and refresh for perspective (" + name  + ") has timed-out due to inactivity.</body></html>";
	    var dlg = userDefinedConfirmDialogForApp(warningMsg, mybuttons, "Warning");
    	dlg.show();
    	udConfirmDialogForAppTimeoutID = setTimeout(cancelBtn,60000);
    }

}
hm.da.resetIntervalApp = function (id,name, flag) {
	if(flag) {
		if(udConfirmDialogForAppTimeoutID){
			clearTimeout(udConfirmDialogForAppTimeoutID);
		}
		if (highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]){
			clearTimeout(highApTimeOutArray["warningHighIntervalAPTimeoutId" + id]);
		}
		var tmFction = "hm.da.dispalyHighIntervalWarn(\"" + name + "\",\"" + id +"\")";
		highApTimeOutArray["warningHighIntervalAPTimeoutId" + id] = setTimeout(tmFction, appWarningDailogTimeout);
	}
	var url = formName + ".action?operation=changeAppApHighInterval&removeHighIntervalFlg="+ flag
		+"&removeHighIntervalId=" + id
		+ "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {timeout: 60000});
}

var udConfirmDialogForAppTimeoutID=null;
var udConfirmDialogForApp=null;
function userDefinedConfirmDialogForApp(text, myButtons, titile) {
    if(null == udConfirmDialogForApp) {
    	udConfirmDialogForApp =
         new YAHOO.widget.SimpleDialog("udConfirmDialogForApp",
                  { width: "350px",
                    fixedcenter: true,
                    visible: false,
                    draggable: true,
                    modal:true,
                    close: false,
                    icon: YAHOO.widget.SimpleDialog.ICON_WARN,
                    constraintoviewport: true
                  } );
    }
    if(titile) {
    	udConfirmDialogForApp.setHeader(titile);
    }
    udConfirmDialogForApp.cfg.setProperty("text", text);
    udConfirmDialogForApp.cfg.queueProperty("buttons", myButtons);
    udConfirmDialogForApp.render(document.body);
    return udConfirmDialogForApp;
}

hm.da.validateForDeviceDetailLink = function(chart) {
	if (!chart) {
		return false;
	}
	var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
	if (!curConfig) {
		return false;
	}
	return chart.currentDashboard.daHelper.isChartDeviceLinkable(curConfig);
};

var _MONITOR_TYPE_DISPLAY_NAME = {
	user: "User",
	app: "App",
	client: "Client",
	device: "Device",
	port: "Port",
	appclient: "AppClient"
};
var _MONITOR_TYPE_TYPE = {
	user: 4,
	app: 3,
	client: 6,
	device: 7,
	port: 8,
	appclient: 9
};
hm.da.validateForMonitorTab = function(chart, cText, options) {
	if (!chart || !cText) {
		return false;
	}

	//normal chart widget
	var dashboardTmp = getCurrentDashboard();
	if (!dashboardTmp || dashboardTmp.boardId < 0) {
		return false;
	}

	options = options || {};
	if(options.isdrilldown){
		return {
			'type': 5,
			'name': 'Drilldown' + ' - ' + cText
		};
	}
	
	var curConfig = chart.currentDashboard.getReportConfig(chart.reportId);
	if (!curConfig) {
		return false;
	}
	
	var mType = curConfig.xmt;
	if (options.isOnPoint) {
		mType = chart.currentDashboard.daHelper.getMetricProperties(curConfig, options.metric, "mt");
	}
	if (!mType) {
		return false;
	}
	
	var $tabs = $(".dac_td_tab_center");
	if ($tabs.length >= 10) {
		showWarnDialog("The maximum of ten dashboard perspectives has been reached.", "Error");
		return false;
	}

	var mtPrefix = '';
	if (mType in _MONITOR_TYPE_TYPE) {
		mtPrefix = _MONITOR_TYPE_DISPLAY_NAME[mType];
		mType = _MONITOR_TYPE_TYPE[mType];
	} else {
		return false;
	}

	return {
		'type': mType,
		'name': mtPrefix + ' - ' + cText
	};
}