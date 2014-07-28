<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.be.parameter.BeParaModule"%>
<%@page import="com.ah.be.common.NmsUtil"%>

<link type="text/css" rel="stylesheet"
	href="<s:url value="/css/jquery-ui.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<link type="text/css" rel="stylesheet"
	href="<s:url value="/config/welcomePage/newWelcomePage.css" includeParams="none"/>?v=<s:property value="verParam" />"></link>
<script src="<s:url value="/js/jquery.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/underscore-min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/jquery-ui.min.js" includeParams="none" />?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/js/country-timezone.js" includeParams="none" />?v=<s:property value="verParam" />"></script>

<script type="text/javascript">
	var formName = "newStartHere";
	var startStep = (function($){
		/** step configs here **/
		var STEP_DEF = {
			REVIEW_INVENTORY: "review-inventory",
			ACTIVATE_LICENSE: "activate-license",
			MANAGEMENT_SETTINGS: "management-settings"
		};
		var step_orders = new Array();
		<s:if test="%{hMOnline}">
		step_orders.push(STEP_DEF.REVIEW_INVENTORY);
		</s:if>
		<s:if test="%{hMOnline || isInHomeDomain}">
		step_orders.push(STEP_DEF.ACTIVATE_LICENSE);
		</s:if>
		step_orders.push(STEP_DEF.MANAGEMENT_SETTINGS);
		
		var step_names = {};
		step_names[STEP_DEF.REVIEW_INVENTORY] = '<s:text name="hm.missionux.wecomle.title.inventory"/>';
		step_names[STEP_DEF.ACTIVATE_LICENSE] = '<s:text name="hm.missionux.wecomle.title.license"/>';
		step_names[STEP_DEF.MANAGEMENT_SETTINGS] = '<s:text name="hm.missionux.wecomle.title.management"/>';
		
		var step_indicators = {};
		
		var curStepIdPos = 0;
		var getCurStepIndicatorId = function() {
			curStepIdPos++;
			return {
				order: curStepIdPos,
				id: "step-indicator-" + curStepIdPos
			}
		};
		var addStepIndicatorsOnPage = function() {
			var curDealPos = 0;
			_.each(step_orders, function(curOrder) {
				curDealPos++;
				var idTmp = getCurStepIndicatorId();
				step_indicators[curOrder] = {
					id: idTmp.id
				};
				var stepTmp = '<div class="single-step-indicator" id="'+idTmp.id+'">';
				stepTmp += '<div class="order"><span>' + idTmp.order + '</span></div>';
				stepTmp += '<div class="text">' + step_names[curOrder] + '</div>';
				if (curDealPos < step_orders.length) {
					stepTmp += '<div class="single-step-indicator-separate"><img src="<s:url value="/images/blank.png" />"/></div>';
				}
				stepTmp += '</div>';
				$(".step-indicator-content").append($(stepTmp));
			});
		};
		var getStepsCount = function() {
			return step_orders.length;
		};
		
		/** assistant configs **/
		var $stepContainers = {};
		var CUR_STEP = step_orders[0];
		var BACK_BTN_CTL, NEXT_BTN_CTL;
		
		/** assistant functions **/
		var getResultBoolean = function(result) {
			if (typeof result !== 'undefined' && !result) {
				return false;
			}
			return true;
		};
		var orderController = {
			back: function() {
				if (!getResultBoolean(steps[CUR_STEP].back())) {
					return;
				}
				var backstep = getPreviousStep();
				if (backstep) {
					CUR_STEP = backstep;
					renderCurrentStepPage();
				}
			},
			next: function() {
				if (!getResultBoolean(steps[CUR_STEP].next())) {
					return;
				}
				var nextstep = getNextStep();
				if (nextstep) {
					CUR_STEP = nextstep;
					renderCurrentStepPage();
				}
			},
			gotoLast: function() {
				CUR_STEP = STEP_DEF.WELCOME_DONE;
				renderCurrentStepPage();
			},
			init: function() {
				renderCurrentStepPage();
			},
			adjust: function() {
				BACK_BTN_CTL.changeText();
				NEXT_BTN_CTL.changeText();
				if (isFirstStep()) {
					BACK_BTN_CTL.show(false);
					NEXT_BTN_CTL.show(true);
				} else if (isLastStep()) {
					BACK_BTN_CTL.show(true);
					NEXT_BTN_CTL.show(false);
				} else {
					BACK_BTN_CTL.show(true);
					NEXT_BTN_CTL.show(true);
				}
				if (steps[CUR_STEP] && steps[CUR_STEP].onshow) {
					steps[CUR_STEP].onshow();
				}
			}
		};
		var isCurrentCertainPosition = function(pos) {
			return function() {
				var curStepIdx = -1;
				for (var i = 0; i < step_orders.length; i++) {
					if (step_orders[i] == CUR_STEP) {
						curStepIdx = i;
						break;
					}
				}
				if (curStepIdx == pos) {
					return true;
				}
			}
		};
		var isFirstStep = isCurrentCertainPosition(0),
			isLastStep = isCurrentCertainPosition(step_orders.length-1);
		
		var getDeltaStep = function(delta) {
			return function() {
				var curStepIdx = -1;
				for (var i = 0; i < step_orders.length; i++) {
					if (step_orders[i] == CUR_STEP) {
						curStepIdx = i;
						break;
					}
				}
				if (curStepIdx + delta >= 0 && curStepIdx + delta < step_orders.length) {
					return step_orders[curStepIdx + delta];
				}
			}
		};
		var getPreviousStep = getDeltaStep(-1),
			getNextStep = getDeltaStep(1);
		
		var buttonControl = function($btnEl, option) {
			var self = this;
			option = option || {};
			var defText = option.text || $btnEl.val();
			self.disable = function(blnDisabled) {
				$btnEl.attr("disabled", blnDisabled);
			};
			self.show = function(blnShown) {
				if (blnShown) {
					$btnEl.show();
				} else {
					$btnEl.hide();
				}
			};
			self.changeText = function(text) {
				$btnEl.val(text || defText);
			};
			self.activate = function(blnActivated) {
				if (blnActivated) {
					$btnEl.addClass("active");
				} else {
					$btnEl.removeClass("active");
				}
				$btnEl.attr("disabled", false);
			};
			self.click = function(callback) {
				$btnEl.click();
				if (callback) {
					callback();
				}
			};
			self.jq = function(myMethod) {
				var args = Array.prototype.slice.call(arguments, 1);
				$btnEl[myMethod].apply($btnEl, args);
			};
		};
		var buttonControlProxy = function(method) {
			var buttonMaps = {
				"back": BACK_BTN_CTL,
				"next": NEXT_BTN_CTL
			};
			var args = arguments,
				argLen = args.length,
				methodArg = null,
				btns;
			if (argLen > 2) {
				methodArg = args[1];
				btns = args[2];
			} else if (argLen > 1) {
				btns = args[1];
			}
			if (!$.isArray(methodArg)) {
				methodArg = [methodArg];
			}
			if (!$.isArray(btns)) {
				btns = [btns];
			}
			_.each(btns, function(btn) {
				btn = buttonMaps[btn];
				btn[method].apply(btn, methodArg);
			});
		};
		var ori_tip_message;
		var set_tip_message = function(tip) {
			$("div.step-tip").html(tip);
		};
		var renderCurrentStepPage = function() {
			if (!ori_tip_message) {
				ori_tip_message = $("div.step-tip").html();
			}
			if (steps[CUR_STEP] && steps[CUR_STEP].tip) {
				set_tip_message(steps[CUR_STEP].tip);
			} else {
				set_tip_message(ori_tip_message);
			}
			for (var key in $stepContainers) {
				if (key == CUR_STEP) {
					$stepContainers[key].show();
				} else {
					$stepContainers[key].hide();
				}
			}
			_.each(_.values(step_indicators), function(value) {
				$("#" + value.id).removeClass("active");
			});
			if (step_indicators[CUR_STEP]) {
				$("#" + step_indicators[CUR_STEP].id).addClass("active");
			}
			pageTitle.reset();
			orderController.adjust();
		};
		
		
		/** initialize these when page was loaded **/
		var onPageLoaded = function() {
			for (var key in steps) {
				$stepContainers[key] = $("div#" + key);
			}
			
			BACK_BTN_CTL = new buttonControl($("#btn-back")),
			NEXT_BTN_CTL = new buttonControl($("#btn-next"));

			$("#btn-back").click(function(e) {
				orderController.back();
			});
			$("#btn-next").click(function(e) {
				orderController.next();
			});
		};
		
		/** to hold configs from each step section **/
		var page_load_events = [];
		page_load_events.push(onPageLoaded);
		
		var add_page_onload_event = function(eArg) {
			page_load_events.push(eArg);
		};
		
		var afterLoadPage = function() {
			addStepIndicatorsOnPage();
			_.each(page_load_events, function(eArg) {
				eArg();
			});
			orderController.init();
		};
		
		var steps = {};
		var def_step_definition = {
			tip: "",
			back: function(){;},
			next: function(){;},
			onshow: function(){;}
		};
		var add_step_definition = function(key, def) {
			steps[key] = $.extend(true, {}, def_step_definition, def);
		};
		

		function submitAction(operation, validateFunc) {
			operation = operation || "configDone";
			if (validateFunc && !validateFunc(operation)) {
				return;
			}
			document.forms[formName].operation.value = operation;
			document.forms[formName].submit();
		}
		
		var oriPageTitle;
		var pageTitle = {
			subTitle: function(title){
				if (!oriPageTitle) {
					oriPageTitle = document.title;
				}
				document.title = oriPageTitle + " - " + title;
			},
			reset: function() {
				if (oriPageTitle) {
					document.title = oriPageTitle;
				}
			}
		};
		
		return {
			add_onload_event: add_page_onload_event,
			afterLoadPage: afterLoadPage,
			STEP_DEF: STEP_DEF,
			add_step_definition: add_step_definition,
			otherConfigs: {},
			BUTTONS: {
				BACK: function() {return BACK_BTN_CTL;},
				NEXT: function() {return NEXT_BTN_CTL;},
				take: buttonControlProxy
			},
			configDone: orderController.gotoLast,
			submitForm: submitAction,
			pageTitle: pageTitle,
			getStepsCount: getStepsCount
		};
	})(jQuery);
</script>
	
<div class="content">
<s:form action="newStartHere" name="newStartHere" id="newStartHere">
	<s:hidden name="networkName"/>
	<s:hidden name="operation"/>
	<div class="top-tip">
		<div class="welcome-string">
			<s:text name="hm.missionux.wecomle.title.welcome"><s:param><s:property value="%{systemNmsName}"/></s:param></s:text>
		</div>
		<div class="step-tip">
			<s:if test="%{hMOnline || isInHomeDomain}">
				<s:if test="%{hMOnline}">
					<s:text name="hm.missionux.wecomle.inventory.step.tip"/>
				</s:if>
				<s:else>
					<s:text name="geneva_04.hm.missionux.wecomle.inventory.step.tip.no.inventory"/>
				</s:else>
			</s:if>
			<s:else>
				<s:if test="%{hMOnline}">
					<s:text name="hm.missionux.wecomle.inventory.step.tip.no.license"/>
				</s:if>
				<s:else>
					<s:text name="geneva_04.hm.missionux.wecomle.inventory.step.tip.no.license.no.inventory"/>
				</s:else>
			</s:else>
		</div>
	</div>
	<div class="step-indicator">
		<div class="step-indicator-content">
		</div>
	</div>
	<div class="step-main-content">
		<div id="review-inventory">
			<!-- so that do not need to load invertory, can leave here -->
			<s:if test="%{hMOnline}">
				<tiles:insertDefinition name="reviewInventory" />
			</s:if>
		</div>
		<div id="activate-license">
			<!-- so that do not need to load license information, can leave here -->
			<s:if test="%{hMOnline || isInHomeDomain}">
				<tiles:insertDefinition name="activateLicense" />
			</s:if>
		</div>
		<div id="management-settings">
			<tiles:insertDefinition name="managementSettings" />
		</div>
	</div>
	<div class="footer-helper">
		<div class="btn-group">
			<input type="button" class="startHere_common_btn" value="&lt;&nbsp;<s:text name='common.button.back'/>" id="btn-back"/>
			<input type="button" class="startHere_common_btn" value="<s:text name='common.button.next'/>&nbsp;&gt;" id="btn-next"/>
		</div>
		<div class="ie_only_show ie_verison_note">
		</div>
	</div>
</s:form>
</div>

<script type="text/javascript">
	$(function() {
		if ($.browser.msie && parseInt($.browser.version, 10) < 8) {
			$(".ie_verison_note").html("Note: Page layout in IE maybe weird if the version older than IE8.");
		}
		startStep.afterLoadPage();
		//if (startStep.getStepsCount() == 1) {
		//	$(".step-indicator").hide();
		//}
	});
</script>
