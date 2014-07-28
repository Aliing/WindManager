<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<style type="text/css">
<!--
#reportSettingView{
	width: 200px;
}
#reportSettingView .report-title{
	padding: 8px;
	font-size: 14px;
	font-weight: bold;
	text-align: center;
	color: #444;
	background: #FFE43F;
	-webkit-border-top-left-radius: 4px;
	-webkit-border-top-right-radius: 4px;
	-moz-border-radius-topleft: 4px;
	-moz-border-radius-topright: 4px;
	border-top-left-radius: 4px;
	border-top-right-radius: 4px;
}
#reportSettingView .report-header{
	padding: 6px 6px 6px 14px;
	border-left: 1px solid #DEDCD5;
	border-right: 1px solid #DEDCD5;
	border-bottom: 1px solid #DEDCD5;
	cursor: default;
	font: bold;
}
#reportSettingView .report-content{
	display: none;
}
#reportSettingView .report-select{
	padding-left: 6px;
	border-left: 10px solid #BDBAAB;
	background: #DEDCD5;
	color: #4F4F4F;
}
-->
</style>
<script type="text/javascript">
<!--
var reportSettingPane = (function(_$){
	var isReportSettingPaneInitialized = false;
	var sh = 0, bh = 0, th = 0;
	var initializeLayout = function(){
		var vpHeight = YAHOO.util.Dom.getViewportHeight();
		var fst = _$("#reportSettingView").offset().top;
		var lst = _$("#div_rsv_lst").offset().top;
		bh = lst - fst;
		th = lst;
		sh = (vpHeight < th) ? 0 : vpHeight - th;
		var ph = sh + bh;
		_$('#reportSettingView').css("height", ph+"px");
		// register listener
		_$('#reportSettingView .report-header').click(function(e, trigger){
			if(!trigger){
				return;
			}
			if(_$('#reportSettingView .report-header').not(this).hasClass("report-select")){
				_$('#reportSettingView .report-header').not(this).removeClass("report-select").next().css("display", "none");
			}
			if(!_$(this).hasClass("report-select")){
				_$(this).addClass("report-select").next().css("display", "block");
				if(widgetPanel && _$(this).is(_$('#reportSettingView .report-header').first())){
					widgetPanel.toggleDisplay(null, sh);
				}
			}
		});
		YAHOO.util.Event.addListener(window, 'resize', function(){
			var vpHeight = YAHOO.util.Dom.getViewportHeight();
			sh = (vpHeight < th) ? 0 : vpHeight - th;
			var ph = sh + bh;
			_$('#reportSettingView').css("height", ph+"px");
			monitorPane.resizeLayout(sh);
			widgetPanel.resizeLayout(sh);
		});
		//_$('#reportSettingView .report-header').first().trigger('click');
		isReportSettingPaneInitialized = true;
	};

	var expandWidgetBox = function(){
		_$('#reportSettingView .report-header').first().trigger('click', ["trigger"]);
	};

	var expandFilterBox = function(){
		_$('#reportSettingView .report-header').last().trigger('click', ["trigger"]);
	};

	var initializeReportSettingTree = function(){
		if(!isReportSettingPaneInitialized){
			initializeLayout();
		}
		if(null != monitorPane){
			return monitorPane.init(sh);
		}
	};

	var initializeReportSettingWidget = function(){
		if(!isReportSettingPaneInitialized){
			initializeLayout();
		}
		if(null != widgetPanel){
			widgetPanel.init(sh);
		}
	};

	var resizeWidth = function(width){
		monitorPane.resizeWidth(width);
		widgetPanel.resizeWidth(width);
	};

	return {
		initWidget: initializeReportSettingWidget,
		initTree: initializeReportSettingTree,
		expandWidgetBox: expandWidgetBox,
		expandFilterBox: expandFilterBox,
		resizeWidth: resizeWidth
	};
})(jQuery);
//-->
</script>

<div id="reportSettingView">
	<div class="report-title"><s:text name="hm.recurreport.setting.title" /></div>
	<div class="report-header"><s:text name="hm.recurreport.setting.widget" /></div>
	<div class="report-content">
	<tiles:insertDefinition name="reportConfigView" />
	</div>
	<div class="report-header"><s:text name="hm.recurreport.setting.filter" /></div>
	<div class="report-content">
	<tiles:insertDefinition name="reportMonitorView" />
	</div>
</div>
<div id="div_rsv_lst" style="width: 1px; height: 0px; font-size: 1px;">
</div>