
//global inition buttons
function initButton(scope) {
	scope = scope === undefined ? "" : scope;
	$(scope + " button").each(function() {
		var name = $(this).attr("name");
		var iconClass = true;
		switch (name) {
		case "add":
			iconClass = "ui-icon-plus";
			break;
		case "edit":
			iconClass = "ui-icon-wrench";
			break;
		case "delete":
			iconClass = "ui-icon-trash";
			break;
		case "search":
			iconClass = "ui-icon-search";
			break;
		case "cancel":
			iconClass = "ui-icon-cancel";
			break;
		case "confirm":
			iconClass = "ui-icon-check";
			break;
		case "save":
			iconClass = "ui-icon-check";
			break;
		case "seeknext":
			iconClass = "ui-icon-seek-next";
			break;
		case "next":
			iconClass = "ui-icon-seek-next";
			break;
		case "seekprev":
			iconClass = "ui-icon-seek-prev";
			break;
		case "previous":
			iconClass = "ui-icon-seek-prev";
			break;
		case "first":
			iconClass = "ui-icon-seek-start";
			break;
		case "last":
			iconClass = "ui-icon-seek-end";
			break;
		case "refresh":
			iconClass = "ui-icon-refresh";
			break;
		case "login":
			iconClass = "ui-icon-unlocked";
			break;
		case "reset":
			iconClass = "ui-icon-close";
			break;
		case "close":
			iconClass = "ui-icon-close";
			break;
		case "power":
			iconClass = "ui-icon-power";
			break;
		case "folderopen":
			iconClass = "ui-icon-folder-open";
			break;
		case "return":
			iconClass = "ui-icon-arrowreturn-1-w";
			break;
		case "import":
			iconClass = "ui-icon-arrowreturn-1-s";
			break;
		case "configure":
			iconClass = "ui-icon-gear";
			break;
		default:
			iconClass = false;
		}
		if (iconClass == false) {
			$(this).button();
		} else {
			$(this).button({
				icons : {
					primary : iconClass
				}
			});
		}

	});
}

/**
 * @param {Object}
 *            obj: the event target
 * @param {Object}
 *            name: the name of checkboxs
 */
function changeAllCk(obj, name) {
	name = name === undefined ? "" : "[name=" + name + "]";
	if ($(obj).attr("checked") == "checked") {
		$("input:checkbox" + name).attr("checked", "checked");
	} else {
		$("input:checkbox" + name).removeAttr("checked");
	}

}

/**
 * validate the form, now it's base on jquery.validate, for some custom edit, it
 * also depend on beautytip
 * 
 * @param {Object}
 *            formId
 * @param {Object}
 *            extraOption
 */
function validateForm(formId, extraOption) {
	var option = {
		/*
		 * errorPlacement:
		 * function(error,element){element.attr("title",error.html());error.html("").appendTo(element.parent());},
		 */
		/* success: "valid", */
		customShowLabel : function(element, message) {

			var title = message;
			message = "&nbsp";
			var label = this.errorsFor(element);
			if (label.length) {
				/*
				 * // refresh error/success class label.removeClass(
				 * this.settings.validClass ).addClass( this.settings.errorClass ); //
				 * check if we have a generated label, replace the message then
				 * label.attr("generated") && label.attr("title",title);
				 */
				/*
				 * because the tooltip, we need renew lable, here have some bug
				 * that never remove the tooltip which's label hade been
				 * removed, maybe I will fix it later
				 */
				this.toShow.remove(label);
				label.remove();

			}

			// create label
			label = $("<" + this.settings.errorElement + "/>").attr({
				"for" : this.idOrName(element),
				generated : true
			}).addClass(this.settings.errorClass).html(message || "").attr(
					"title", title);
			;
			if (this.settings.wrapper) {
				// make sure the element is visible, even in IE
				// actually showing the wrapped element is handled elsewhere
				label = label.hide().show().wrap(
						"<" + this.settings.wrapper + "/>").parent();
			}
			if (!this.labelContainer.append(label).length)
				this.settings.errorPlacement ? this.settings.errorPlacement(
						label, $(element)) : label.insertAfter(element);

			if (!title && this.settings.success) {
				title = "";
				label.text("").attr("title", "");
				;
				typeof this.settings.success == "string" ? label
						.addClass(this.settings.success) : this.settings
						.success(label);
				this.toShow = this.toShow.add(label);
				label.bt("");
			} else {
				this.toShow = this.toShow.add(label);
				label.bt({
					cssStyles : {
						fontSize : '12px',
						color : "#f05200"
					},
					width : 150,
					fill : '#ffffff',
					strokeStyle : '#f05200',
					spikeLength : 15,
					spikeGirth : 10,
					cornerRadius : 4,
					shadow : true,
					shadowColor : '#bbbbbb',
					shadowBlur : 6,
					shadowOffsetX : 0,
					shadowOffsetY : 4,
					positions : [ 'right', 'top' ],
					shrinkToFit : true

				});

			}

		},
		meta : "validate"
	};
	$.extend(true, option, extraOption);
	return $("#" + formId).validate(option);
}

/**
 * give the obj a tooltip
 * 
 * @param {Object}
 *            id: The id of the element
 * @param {Object}
 *            content: the content will be in the tooltip(null or undefined if
 *            not need)
 * @param {Object}
 *            extraOption:
 */
function toolTip(id, content, extraOption) {
	var option = {
		cssStyles : {
			fontSize : '12px',
			padding : "5px 10px"
		},
		padding : 0,
		fill : '#ffffff',
		strokeStyle : '#ffca3e',
		spikeLength : 15,
		spikeGirth : 10,
		cornerRadius : 4,
		shrinkToFit : true,
		shadow : true,
		shadowColor : '#bbbbbb',
		shadowBlur : 6,
		shadowOffsetX : 0,
		shadowOffsetY : 4
	};
	$.extend(true, option, extraOption);
	if (content == null || content === undefined) {
		$("#" + id).bt(option);
	} else {
		$("#" + id).bt(content, option);
	}
}

/**
 * transform table to be a rich table
 * 
 * @param {Object}
 *            tableId: the table's id
 * @param {Object}
 *            initSort: a array define how sort when init( e.g. [[2,'asc'],
 *            [3,'desc']])
 * @param {Object}
 *            unSortable: a array define which columns can't be sorted ( e.g. [
 *            2,3 ])
 * @param {Object}
 *            refreshFn: the function of refresh
 * @param {Object}
 *            exportFn: the function of export
 * @param {Object}
 *            extraOption: a json object which can include all option for
 *            component ( e.g. {"bInfo": false})
 */
function transformRichTable(tableId, initSort, unSortable, exportFn, refreshFn,
		extraOption) {
	var objStr;
	if (tableId === undefined || tableId == null) {
		objStr = ".list table";
	} else {
		objStr = "#" + tableId;
	}
	if (initSort === undefined || initSort == null) {
		initSort = [ [ 0, 'asc' ] ];
	}
	if (unSortable === undefined || unSortable == null) {
		unSortable = [];
	}
	if (refreshFn === undefined || refreshFn == null) {
		refreshFn = "goPage";
	}

	var option = {
		"iDisplayLength" : 1000,
		"bPaginate" : false,
		"bInfo" : false,
		"aaSorting" : initSort,
		"aoColumnDefs" : [ {
			"bSortable" : false,
			"aTargets" : unSortable
		} ],
		"oLanguage" : {
			"sSearch" : "Filter Grid:"
		},
		"bDestroy" : true,
		"bRetrieve" : true
	};
	$.extend(true, option, extraOption);
	var dataTable = $(objStr).dataTable(option);
	// new FixedHeader(dataTable,{ "zTop": "60" });
	$(".dataTables_filter")
			.append(
					"<a title='Refresh' id='listRefresh' class='icon refresh' href='javascript:void(0)' onclick='"
							+ refreshFn + "()'></a>");
	if (exportFn === undefined || exportFn == null) {

	} else {
		$(".dataTables_filter")
				.append(
						"<a title='Export' id='listExport' class='icon export' href='javascript:void(0)' onclick='"
								+ exportFn + "()'></a>");
	}
	return dataTable;
}

/**
 * pop window
 * 
 * @param url:
 *            the url of the pop window
 * @param width:
 *            the width of the pop window ( can be "500px" or 500 or "100%",
 *            default is "100%" )
 * @param height:
 *            the height of the pop window ( can be "500px" or 500 or "100%",
 *            default is "100%" )
 * @param onClosed:
 *            the callback function when close popwin
 * @param {Object}
 *            extraOption: a json object which can include all option for
 *            component ( e.g. {initialWidth:"30px"})
 */
function popWin(url, width, height, onClosed, extraOption) {
	$.colorbox.close();
	width = width === undefined ? "100%" : width;
	height = height === undefined ? "100%" : height;
	onClosed = onClosed === undefined ? false : onClosed;
	var option = {
		iframe : true,
		width : width,
		height : height,
		href : url,
		onClosed : onClosed,
		overlayClose : false,
		opacity : 0.5,
		initialWidth : "30px",
		initialHeight : "30px"
	};
	$.extend(true, option, extraOption);
	return $.colorbox(option);
}
// close the pop window
function closePopWin() {
	parent.$.colorbox.close();
}

/**
 * show the Waiting
 * 
 * @param block:
 *            true to block the page, else false ( default is true )
 */

function showWaiting(block, msg, width, height) {

	block = block === undefined ? true : block;
	msg = msg === msg ? "Please Wait..." : msg;
	width = width === undefined ? 200 : width;
	height = height === undefined ? 50 : height;

	var $body = $("body");
	var $appendTo = $body;
	var $waitContent = $('<div name="waitContent" class="waitContent ui-widget ui-widget-content ui-corner-all"></div>');

	var $img = $('<div name="waitImg" class="waitImg"/>');
	$waitContent.append($img);
	// when width >300, add message
	if ($body.width() > 300) {
		var $waitMessage = $('<div name="waitMessage" class="waitMessage">'
				+ msg + '</div>');
		$waitContent.append($waitMessage);
	}
	// else only show img
	else {
		width = 70;
	}
	if (block) {
		var $waitOverlay = $('<div name="waitOverlay" class="overlay waitOverlay"></div>');
		$appendTo.append($waitOverlay);
	}
	var left = $(window).width() / 2 - width / 2 + $(document).scrollLeft();
	var top = $(window).height() / 2 - height / 2 + $(document).scrollTop();
	// this is special operation for popwin
	if (parent.$("#cboxLoadedContent").length == 1
			&& parent.$("#colorbox").is(":visible")) {
		left = (parent.$("#cboxLoadedContent").width() - width) / 2
				+ $(document).scrollLeft();
		top = (parent.$("#cboxLoadedContent").height() - height) / 2
				+ $(document).scrollTop();
	}
	$appendTo.append($waitContent);
	$waitContent.css("width", width);
	$waitContent.css("height", height);
	$waitContent.css("top", top);
	$waitContent.css("left", left);

}
// hide the waiting
function hideWaiting() {

	if ($("[name=waitOverlay]").length == 1) {
		$("[name=waitOverlay]").remove();
	}
	if ($("[name=waitContent]").length == 1) {
		$("[name=waitContent]").remove();
	}
}

// transform system alert as pnotify( It's cool )
function consume_alert() {
	if (_alert) {
		return;
	}
	_alert = window.alert;
	window.alert = function(message) {
		$.pnotify({
			title : "Alert",
			text : message
		});
	};
}
// release system alert
function release_alert() {
	if (!_alert) {
		return;
	}
	window.alert = _alert;
	_alert = null;
}

/**
 * show notice on the center of page
 * 
 * @param text:maininfo
 *            text
 * @param
 *            type:(pnotify_type_error,pnotify_type_notice,pnotify_type_confirm,pnotify_type_success)
 * @param functionName:when
 *            type is confirm,we need function call back to confirm option
 * @param title:tile
 *            content
 * @param {Object}
 *            extraOption: a json object which can include all option for
 *            component ( e.g. {pnotify_stack: false})
 * @return
 */
var pnotify = null;
var pnotify_type_error = "error";
var pnotify_type_notice = "notice";
var pnotify_type_info = "info";
var pnotify_type_confirm = "confirm";
var pnotify_type_success = "success";
function callPnotify(text, type, functionName, title, extraOption) {
	hideWaiting();
	var modal_overlay;
	var effect_in = "fade";
	var pnotify_hide = true;
	var pnotify_closer = true;
	var pnotify_addclass = "custom";
	var pnotify_icon = "picon customicon-alert";
	var pnotify_delay = 5000;
	var pnotify_type = type;
	var pnotify_sticker = false;
	switch (type) {
	case pnotify_type_error: {
		effect_in = "bounce";
		pnotify_icon = "picon customicon-error";
		break;
	}
	case pnotify_type_notice: {
		pnotify_icon = "picon customicon-alert";
		break;
	}
	case pnotify_type_info: {
		pnotify_icon = "picon customicon-alert";
		break;
	}
	case pnotify_type_success: {
		pnotify_icon = "picon customicon-ok";
		pnotify_delay = 5000;
		break;
	}
	case pnotify_type_confirm: {
		pnotify_icon = "picon customicon-help";
		pnotify_hide = false;
		pnotify_closer = false;
		pnotify_sticker = false;
		pnotify_type = pnotify_type_info;
		text = "<div name='pnotice_content' style='height:50px;padding:5px;background-color:#ffffff;'>"
				+ "<div name='pnotice_text' >"
				+ text
				+ "</div>"
				+ "<br><div name='pnotice_btn_bar' style='float:right'>"
				+ "<button onclick='if (pnotify.pnotify_remove)pnotify.pnotify_remove();"
				+ functionName
				+ "();' name='pnotice_btn_confirm' type='button'>Confirm</button>"
				+ "&nbsp;&nbsp;&nbsp;<button name='pnotice_btn_cancel' type='button' onclick='if (pnotify.pnotify_remove) pnotify.pnotify_remove();'>Cancel</button>"
				+ "</div>" + "</div>";
		break;
	}
	}

	if (typeof (title) == "undefined") {
		/*
		 * switch(type) { case pnotify_type_error: title =
		 * typeof(global_callPnotify_title_error)=="undefined"?"Error!":global_callPnotify_title_error;
		 * break; case pnotify_type_notice: title =
		 * typeof(global_callPnotify_title_notice)=="undefined"?"Notice!":global_callPnotify_title_notice;
		 * break; case pnotify_type_confirm: title =
		 * typeof(global_callPnotify_title_confirm)=="undefined"?"Confirm!":global_callPnotify_title_confirm;
		 * break; default: title = "&nbsp;"; }
		 */
		// now we do not use tile when title is undefined
		title = text;
		text = "&nbsp;";
		pnotify_addclass = "custom";

	}

	if (pnotify != null) {
		try {
			pnotify.pnotify_remove();
		} catch (e) {

		}
	}
	$.pnotify.defaults.styling = "jqueryui";
	var option = {
		pnotify_addclass : pnotify_addclass,
		pnotify_icon : pnotify_icon,
		pnotify_title : title,
		pnotify_text : text,
		pnotify_type : pnotify_type,
		pnotify_hide : pnotify_hide,
		pnotify_closer : pnotify_closer,
		pnotify_delay : pnotify_delay,
		pnotify_sticker : pnotify_sticker,
		pnotify_stack : false,
		pnotify_history : false,
		mouse_reset : false,
		pnotify_animation : {
			effect_in : effect_in,
			effect_out : 'fade'
		},
		pnotify_animate_speed : '100',
		pnotify_shadow : true,
		pnotify_width : "350px",
		pnotify_before_open : function(pnotify) {
			// Position this notice in the center of the screen.
			pnotify.css({
				"top" : ($(window).height() / 2) - (pnotify.height() / 2),
				"left" : ($(window).width() / 2) - (pnotify.width() / 2)
			});
			if (type == pnotify_type_confirm) {
				if (modal_overlay) {
					modal_overlay.fadeIn("fast");
				} else {
					modal_overlay = $("<div class='overlay'/>");
					modal_overlay.appendTo("body").fadeIn("fast");
				}
			}

		},
		before_close : function() {
			if (modal_overlay)
				modal_overlay.fadeOut("fast");
		}
	};
	$.extend(true, option, extraOption);
	pnotify = $.pnotify(option);
	if (type == pnotify_type_confirm) {
		$("[name=pnotice_btn_confirm]").button({
			icons : {
				primary : "ui-icon-check"
			}
		});
		$("[name=pnotice_btn_cancel]").button({
			icons : {
				primary : "ui-icon-cancel"
			}
		});
	}
}

/**
 * 
 * @param $souceSelect :
 *            the scope of the dict;
 * @param $targetSelect:
 *            the selet cascade with scope;
 * @param itemCatagory :
 *            the Catagory of dict;
 * 
 * using to implements a cascadeSelect;
 */
function cascadeSelection($sourceSelect, $targetSelect, itemCatagory) {

	$sourceSelect.change(function() {
		var scope = $sourceSelect[0].value;
		$.post(ctx + "/common/getCascadeSelectItems", {
			scope : scope,
			catagory : itemCatagory
		}, function(jsonStr) {
			var json = eval(jsonStr);
			$targetSelect.get(0).options.length = 0;
			for ( var i = 0; i < json.length; i++) {
				$targetSelect.get(0).options.add(new Option(json[i].itemText,
						json[i].itemCode));
			}
		}, "text");
	});

}

function cascadeSelectionOnchange($sourceSelect, $targetSelect, itemCatagory)
{
	var scope = $sourceSelect[0].value;
	$.post(ctx + "/common/getCascadeSelectItems", {
		scope : scope,
		catagory : itemCatagory
	}, function(jsonStr) {
		var json = eval(jsonStr);
		$targetSelect.get(0).options.length = 0;
		for ( var i = 0; i < json.length; i++) {
			$targetSelect.get(0).options.add(new Option(json[i].itemText,
					json[i].itemCode));
		}
	}, "text");
}