<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<link rel="stylesheet" type="text/css"
    href="<s:url value="/yui/carousel/assets/skins/sam/carousel.css" includeParams="none"/>?v=<s:property value="verParam" />" />
    
<script src="<s:url value="/yui/animation/animation-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/yui/element/element-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script src="<s:url value="/yui/carousel/carousel-min.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>

<style type="text/css">
#contentCtrl {
	position: absolute;
	top: 0;
	left: 50%;
	margin-left: -260px;
}
#contentCtrl span#prev, #contentCtrl span#next {
    position: absolute;
    z-index: 2;
    width: 11px;
    height: 11px;
    left: 3px;
    top: 1px;
    cursor: pointer;
}
#contentCtrl span#prev{
    background: url(images/notificationmsg/arrow_left.png) no-repeat scroll 0 0 transparent;
}
#contentCtrl span#next{
    left: 55px;
    background: url(images/notificationmsg/arrow_right.png) no-repeat scroll 0 0 transparent;
}
#contentCtrl .hidden{
    display: none;
}
#contentCtrl #play {
    background: url(images/notificationmsg/play-normal-icon.png) no-repeat scroll 0 0 transparent;
}
#contentCtrl #stop {
    background: url(images/notificationmsg/stop-red-icon.png) no-repeat scroll 0 0 transparent;
}
/*--Message CSS--*/
#containerMsg {
    height: 52px;
    border: none;
}
#containerMsg a {
    text-decoration: none;
}
#contentCtrl .yui-carousel {
	position: absolute;
}
#containerMsg .yui-carousel-pagination {
	position: absolute;
	left: 20px;
	text-align: left;
    font-family: Arial, Helvetica, Verdana, sans-serif;
    font-size: 11px;
    z-index: 1;
}
#containerMsg .yui-carousel-nav {
	background: none;
	padding: 0;
}
#containerMsg .yui-carousel-nav span {
	display: none;
}
#containerMsg .item {
    display: inline;
    margin: 0;
    overflow: hidden;
    width: 540px;
}
#containerMsg .yui-carousel-nav ul {
	margin: 0;
	position: absolute;
	left: 75px;
	top: -7px;
	z-index: 2;
	display: none;
}
#containerMsg .yui-carousel-element li {
    height: 52px;
    text-align: left;
    background-color: #FDF1C1;
    color: #717174;
}
#containerMsg .yui-carousel-item-selected {
    border: none;
}
#containerMsg .yui-carousel-element span.close {
    margin: 0;
    position: absolute;
    right: 6px;
    top: 6px;
    background: url(images/notificationmsg/close_btn.png) no-repeat scroll 0 0 transparent;
    width: 11px;
    height: 11px;
    cursor: pointer;
}
#containerMsg .yui-carousel-element span.close:hover {
    background: url(images/notificationmsg/close_btn_hover.png) no-repeat scroll 0 0 transparent;
}
.notificationMsg {
    padding-top: 15px;
    padding-left: 40px;
    padding-right: 20px;
    font-family: Arial, Helvetica, Verdana, sans-serif;
    font-size: 12px;
    text-align: left;
    white-space: normal;
    word-wrap: break-word;
    max-width: 480px; 
    min-width: 400px;
    float: left;
    background: url("images/notificationmsg/warning_msg_icon.png") no-repeat scroll 15px 15px transparent;
}
.notificationBtns {
    /**
    float: left;
    padding: 15px 0 0 0;
    **/
}
.notificationBtns input {
	width: 45px;
}
</style>

<div id="contentCtrl">
	<!-- The Carousel container -->
	<div id="containerMsg" style="visibility: hidden;">
	    <ol id="carousel">
	    <s:iterator value="sessionNotificationMessages">
	       <s:if test="enableDisplay">
	        <li class="item" <s:if test="null != itemStyle && itemStyle != ''">style='<s:property value="itemStyle"/>'</s:if> >
	            <div class="notificationMsg"
	               <s:if test="null != msgStyle">style="<s:property value='msgStyle'/>"</s:if>
	               >
	                <label><s:property value="contents" escape="false"/></label>
	                <span class="notificationBtns" style="<s:property value='btnGroupStyle' />">
			            <s:iterator value="actionButtons">
			                <input type="button" name="ignore" value="<s:property value='btnNameValue' />" class="button"
			                     title="<s:property value='desc'/>" width="<s:property value='goBtnWidth'/>"
				                <s:if test="null != func">
				                 onclick='<s:property value="func"/>;'
				                </s:if>
				                <s:if test="null != goBtnWidth && goBtnWidth != ''">
				                 style='width: <s:property value="goBtnWidth"/>'
				                </s:if>
			                />
			            </s:iterator>
	                </span>
	            </div>
	            <s:if test="null != closeButton">
	            <span class="close" title="<s:property value='closeButton.desc'/>"
	               <s:if test="null != closeButton.func && closeButton.func != ''">
	               onclick='<s:property value="closeButton.func"/>;'
	               </s:if> 
	            ></span>
	            </s:if>
	        </li>
	       </s:if>
	    </s:iterator>
	    </ol>
		<span id="prev" class="hidden"></span>
	    <span id="next" class="hidden"></span>
	</div>
    <!-- 
    <span id="play"></span>
    <span id="stop"></span>
     -->
</div>
<!-- For license -->
<s:if test="lsMessageEnableDisplay">
<tiles:insertDefinition name="licenseInTitle" />
</s:if>
<tiles:insertDefinition name="osVersionDetails" />
<script>
     var carousel;
     var DOM = YAHOO.util.Dom, EVENT = YAHOO.util.Event;
             
     EVENT.onDOMReady(function (ev) {
         carousel    = new YAHOO.widget.Carousel("containerMsg", {
                     numVisible: 1,
                     isCircular: true,
                     autoPlayInterval: 15000,//15 seconds
                     animation: { speed: 0.5 }
             });
         if (carousel.get("numItems") > 1) {
	         // set the pagination text
	         carousel.registerPagination("{firstVisible} <strong>of</strong> {numItems}");
         } else {
        	 // reset the position when only one item
        	 var divArray = DOM.get("carousel").getElementsByTagName("div");
        	 if(divArray[0] && divArray[0].className == "notificationMsg") {
        		if(divArray[0].offsetHeight > 30) {
        			divArray[0].style.paddingTop="10px";
        		}
        	 }
         }
         
         // redefined the navigation {prev, next}
         carousel.set("navigation", {prev: [document.getElementById("prev")], next: [document.getElementById("next")]});
         
         // select the specific item after create
         <s:if test="!autoPlayMsg">
         //carousel.set("selectedItem", <s:property value="msgPlayNum"/>);
         </s:if>

         carousel.render(); // get ready for rendering the widget
         carousel.show();   // display the widget
         
         // binded with event
         if (carousel.get("numItems") > 1) {
	        carousel.startAutoPlay();
         	EVENT.on("contentCtrl", "mouseover", mouseOverContainer);
         	EVENT.on("contentCtrl", "mouseout", mouseOutContainer);
         }
         updateNavigation();
         
         DOM.setStyle("containerMsg", "visibility", "visible");
     });
    //----------------------Common Method----------------------//
    function debug(msg) {
    	//console.debug(msg);
    }
    function removeMsgItem() {
        var index = carousel.get("firstVisible");
        debug("the currrent index:"+index+" numItens:"+carousel.get("numItems"));
        debug(carousel.isAutoPlayOn?"autoplay on":"autoplay off");
        if(index == carousel.get("numItems") - 1) {
        	if(index-1 < 0) {
        		//TODO
        	} else {
	        	debug("remove the last item, need to scroll to previous item.");
	        	carousel.scrollTo(index-1, true);
        	}
        }
        carousel.removeItem(index);
        carousel.updatePagination();
        if(carousel.get("numItems") == 0) {
        	carousel.stopAutoPlay();
        	carousel.hide();
        	DOM.setStyle("containerMsg", "visibility", "hidden");
        } else if(carousel.get("numItems") == 1) {
        	updateNavigation();
        	
        	EVENT.removeListener("contentCtrl","mouseover", mouseOverContainer);
        	EVENT.removeListener("contentCtrl","mouseout", mouseOutContainer);
        }
        debug("after remove firstVisible:"+carousel.get("firstVisible")+" numItems:"+carousel.get("numItems"));
    }
    function updateNavigation() {
    	var flag = false;
    	if(carousel && carousel.get("numItems") > 1) {
    		flag = true;
    	}
        var prevEl = document.getElementById("prev");
        var nextEl = document.getElementById("next");
        if(flag) {
	        DOM.removeClass(prevEl, "hidden");
	        DOM.removeClass(nextEl, "hidden");
        } else {
            DOM.addClass(prevEl, "hidden");
            DOM.addClass(nextEl, "hidden");
        }
    }
    var mouseOverContainer = function() {
        if (carousel && carousel.get("numItems") > 1 && carousel.isAutoPlayOn()) {
            carousel.stopAutoPlay();
        }
    }
    var mouseOutContainer = function() {
        if (carousel && carousel.get("numItems") > 1) {
            carousel.startAutoPlay();
        }
    }
    //----------------------Buttons action----------------------//
    var AJAX = YAHOO.util.Connect;
    var handleHideMsgSuccess = function (o) {
        eval("var details = " + o.responseText);
        if(details.succ) {
            removeMsgItem();
        } else {
            showWarnDialog("Some errors occur when doing current operation. Please try it later.");
        }
    };
    var handleHideMsgFailure = function (o) {
        showWarnDialog("Failure to do current operation. Please try it later.");
    };
    ///////// OlderVersion ////////////
    function olderVersionMoreDetails() {
    	openOSDetailPanel();
    }
    function noDisplayOlderVersion() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=noDisplayOlderVer"
                +"&ignore=" + new Date().getTime();
        var noDisplayOlderVersionCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        
        var transaction = AJAX.asyncRequest('GET', url, noDisplayOlderVersionCallBack);
    }
    ////////// License ///////////
    function hideMessageInSession() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=removeLsMessageInSession"
            +"&ignore=" + new Date().getTime();
        var hideLsCallbackLs = {
        		success: handleHideMsgSuccess,
        		failure: handleHideMsgFailure,
        		timeout: 15000
        };
        var transaction = YAHOO.util.Connect.asyncRequest('GET', url, hideLsCallbackLs);
    }
	////////// Client Manager ///////////
    function goToEnableClientManager() {
    	document.location.href = "<s:url action='hmServices' includeParams='none' />?operation=hmServices&onboardUpdate=true";	
    }
    function noDisplayClientManager() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=noDisplayClientManager"
            +"&ignore=" + new Date().getTime();
        var hideCMCallback = {
        		success: handleHideMsgSuccess,
        		failure: handleHideMsgFailure,
        		timeout: 15000
        };
        var transaction = YAHOO.util.Connect.asyncRequest('GET', url, hideCMCallback);
    }
    ////////// Express ///////////
    <s:if test="%{changedExWLANConfig}">
    // goto the config guided page and open the upload panel
    function gotoUploadPanel() {
        document.location.href = "<s:url action='configGuide' includeParams='none' />" 
            + "?lastExConfigGuide=uploadConfigEx" +"&ignore=" + new Date().getTime();
    }
    </s:if>
    ////////// for passive node //////////
	function gotoActiveNodeFromPassive() {
		document.location.href = '<s:property value="%{masterURL}"/>';
	}
    ////////// Pre BR //////////
    function gotoBRUploadURL() {
    	document.location.href = "<s:url action='hiveAp' includeParams='none' />" 
    		   + "?hmListType=managedRouters&operation=managedHiveAps";
    }
    
    var initialHelpLinkUrl = '<s:property value="helpLink" />';
    var  currentHelpLinkUrl = initialHelpLinkUrl;
    function setCurrentHelpLinkUrl(url) {
    	currentHelpLinkUrl = url;
    }
    function initializeCurrentHelpLinkUrl() {
    	 currentHelpLinkUrl = initialHelpLinkUrl;
    }
    function openHelpPage() {
    	window.open(currentHelpLinkUrl, 'newHelpWindows', 'height=600, width=800, resizable=yes');
    }
    
    function gotoOnLineHelpDiskFullURL(){
    	openHelpPage();
    	hideTCAMessageInSession();
    }
    
    ////////// TCA DISK FULL ALARM ///////////
    function hideTCAMessageInSession() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=removeTCADiskFullMessageInSession"
            +"&ignore=" + new Date().getTime();
        var hideTCACallbackLs = {
        		success: handleHideMsgSuccess,
        		failure: handleHideMsgFailure,
        		timeout: 15000
        };
        var transaction = YAHOO.util.Connect.asyncRequest('GET', url, hideTCACallbackLs);
    }
    
    
    function watchlistClean() {
    	document.location.href = "<s:url action='reportSetting' includeParams='none' />?operation=cleanWatchlist";
    	closeNotifyWatchlistClean();
    	
    }
    function closeNotifyWatchlistClean() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=closeNotifyWatchlistClean";
        var closeNotifyWatchlistCleanCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        
        var transaction = AJAX.asyncRequest('GET', url, closeNotifyWatchlistCleanCallBack);
    }
    
    function watchlistUpdate() {
    	document.location.href = "<s:url action='reportSetting' includeParams='none' />";
    	closeNotifyWatchlistUpdate();
    	
    }
    function closeNotifyWatchlistUpdate() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=closeNotifyWatchlistUpdate";
        var closeNotifyWatchlistUpdateCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        
        var transaction = AJAX.asyncRequest('GET', url, closeNotifyWatchlistUpdateCallBack);
    }
    

    function closeNotifyL7DisableMsg() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=closeNotifyL7DisableMsg";
        var closeNotifyL7DisableMsgCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        
        var transaction = AJAX.asyncRequest('GET', url, closeNotifyL7DisableMsgCallBack);
    }
    
    function closeHmUpgradeLogMsg() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=closeHmUpgradeLogMsg";
        var closeHmUpgradeLogMsgCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        
        var transaction = AJAX.asyncRequest('GET', url, closeHmUpgradeLogMsgCallBack);
    }
    
    function goToHmUpgradeLogList() {
    	document.location.href = "<s:url action='upgradeLog' includeParams='none' />";	
    }
    
    
    var waitingPanel = null;
    function createWaitingPanel() {
    	waitingPanel = new YAHOO.widget.Panel('wait',
    			{ width:"260px",
    			  fixedcenter:true,
    			  close:false,
    			  draggable:false,
    			  zindex:4,
    			  modal:true,
    			  visible:false
    			}
    		);
    	waitingPanel.setHeader("The operation is progressing...");
    	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" includeParams='none'/>" />');
    	waitingPanel.render(document.body);
    	overlayManager.register(waitingPanel);
    	waitingPanel.beforeShowEvent.subscribe(function(){overlayManager.bringToTop(this)});
    }
    
    function closeCriticalAlarmMsg() {
        var url = "<s:url action='notificationMsg' includeParams='none' />?operation=closeCriticalAlarmMsg";
        var closeCriticalAlarmMsgCallBack = {
                   success: handleHideMsgSuccess,
                   failure: handleHideMsgFailure,
                   timeout: 15000
        };
        var transaction = AJAX.asyncRequest('GET', url, closeCriticalAlarmMsgCallBack);
    }
    
    function clearCriticalAlarm(){
    	url = "<s:url action='alarms' includeParams='none' />" + "?operation=clearCriticalAlarm"+"&ignore=" + new Date().getTime();
    	YAHOO.util.Connect.asyncRequest('get', url, {success:clearCriticalAlarmResult}, null);
    	if(waitingPanel != null){
    		waitingPanel.show();
    	}else{
    		createWaitingPanel();
    	}
    }
    
    function clearCriticalAlarmResult(o){
    	if(waitingPanel != null)
    	{
    		waitingPanel.hide();
    	}
    	eval("var result = " + o.responseText);
    	if(result.success){
    		showInfoDialog("Clear Critical Alarm successfully.");
    	}else{
    		showInfoDialog("Clear Critical Alarm failed.");
    	}
    }
    
    
</script>