<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div class="inventory_review">
	<div class="step_sub_title"><s:text name="hm.missionux.wecomle.inventory.subtitle"/></div>
	<div class="step_sub_content">
		<div class="inventory_title"><s:text name="hm.missionux.wecomle.inventory.list.title"/></div>
		<div class="inventory_list" id="inventory_list_div">
			<div class="inventory_device_number"><div class="device_number_total" id="allConApNum"></div> <s:text name="hm.missionux.wecomle.inventory.list.title.ap"/> <s:if test="%{hMOnline}">(<s:property value="conApNum"/> <s:text name="hm.missionux.wecomle.inventory.list.title.connected"/>)</s:if></div>
			<div class="inventory_device_number"><div class="device_number_total" id="allConBrNum"></div> <s:text name="hm.missionux.wecomle.inventory.list.title.br"/> <s:if test="%{hMOnline}">(<s:property value="conBrNum"/> <s:text name="hm.missionux.wecomle.inventory.list.title.connected"/>)</s:if></div>
			<div class="inventory_device_number"><div class="device_number_total" id="allConSwNum"></div> <s:text name="hm.missionux.wecomle.inventory.list.title.sw"/> <s:if test="%{hMOnline}">(<s:property value="conSwNum"/> <s:text name="hm.missionux.wecomle.inventory.list.title.connected"/>)</s:if></div>
			<div class="inventory_device_number"><div class="device_number_total" id="allConCvgNum"></div> <s:text name="hm.missionux.wecomle.inventory.list.title.cvg"/> <s:if test="%{hMOnline}">(<s:property value="conCvgNum"/> <s:text name="hm.missionux.wecomle.inventory.list.title.connected"/>)</s:if></div>
			<div class="inventory_device_seperate">&nbsp;</div>
			<div class="inventory_device_number"><div class="device_number_total" id="allConTotalNum"></div> <s:text name="hm.missionux.wecomle.inventory.list.title.total"/> <s:if test="%{hMOnline}">(<s:property value="conTotalNum"/> <s:text name="hm.missionux.wecomle.inventory.list.title.connected"/>)</s:if></div>
		</div>
		<div class="inventory_tip">
			<div class="inventory_tip_topword"><s:text name="hm.missionux.wecomle.inventory.note.dontsee"/></div>
			<div class="inventory_tip_box">
				<div class="inventory_tip_box_bold"><s:text name="hm.missionux.wecomle.inventory.note.text1"/><br><br></div>
				<div class="inventory_tip_box_normal"><s:text name="hm.missionux.wecomle.inventory.note.text2"/><br><br>
					<s:text name="hm.missionux.wecomle.inventory.note.text3"/>
				</div>
			</div>
		</div>
		<div id="loadPapgDiv" style="display:none;"><img src="./images/waiting.gif" ></div>
	</div>
</div>

<script type="text/javascript">
	startStep.add_step_definition(startStep.STEP_DEF.REVIEW_INVENTORY, {
		onshow: function() {
			startStep.pageTitle.subTitle('<s:text name="hm.missionux.wecomle.inventory.subtitle"/>');
			startStep.BUTTONS.BACK().activate(false);
			startStep.BUTTONS.NEXT().activate(true);
			Get("loadPapgDiv").style.top = YAHOO.util.Dom.getY(Get("inventory_list_div")) + "px";
			Get("loadPapgDiv").style.left = YAHOO.util.Dom.getX(Get("inventory_list_div")) + "px";
		}
	});
	
	startStep.add_onload_event(function () {
		Get("loadPapgDiv").style.height=Get("inventory_list_div").offsetHeight + "px";
		Get("loadPapgDiv").style.width=Get("inventory_list_div").offsetWidth + "px";
		Get("loadPapgDiv").style.top = YAHOO.util.Dom.getY(Get("inventory_list_div")) + "px";
		Get("loadPapgDiv").style.left = YAHOO.util.Dom.getX(Get("inventory_list_div")) + "px";
		Get("loadPapgDiv").style.display="";
		setTimeout(fetchDeviceFromDirectory, 1000);
	})
	
	YAHOO.util.Event.addListener(window, 'resize', function(){
		Get("loadPapgDiv").style.top = YAHOO.util.Dom.getY(Get("inventory_list_div")) + "px";
		Get("loadPapgDiv").style.left = YAHOO.util.Dom.getX(Get("inventory_list_div")) + "px";
	});
	
	function fetchDeviceFromDirectory() {
		var succFetchDeviceNumber = function (o) {
			try {
				eval("var data = " + o.responseText);
			}catch(e){
				Get("allConApNum").innerHTML=0;
				Get("allConSwNum").innerHTML=0;
				Get("allConBrNum").innerHTML=0;
				Get("allConCvgNum").innerHTML=0;
				Get("allConTotalNum").innerHTML=0;
				warnDialog.cfg.setProperty('text', "<s:text name="hm.missionux.wecomle.inventory.cannot.getdevice.msg"/>");
				warnDialog.show();
				Get("loadPapgDiv").style.display="none";
				return false;
			}
			if(data.t) {
				Get("allConApNum").innerHTML=data.n_ap;
				Get("allConSwNum").innerHTML=data.n_sw;
				Get("allConBrNum").innerHTML=data.n_br;;
				Get("allConCvgNum").innerHTML=data.n_cvg;;
				Get("allConTotalNum").innerHTML=data.n_total;
			} else {
				Get("allConApNum").innerHTML=0;
				Get("allConSwNum").innerHTML=0;
				Get("allConBrNum").innerHTML=0;
				Get("allConCvgNum").innerHTML=0;
				Get("allConTotalNum").innerHTML=0;
				warnDialog.cfg.setProperty('text', "<s:text name="hm.missionux.wecomle.inventory.cannot.getdevice.msg"/>");
				warnDialog.show();
			}
			Get("loadPapgDiv").style.display="none";
		};
		
		var failureFetchDeviceNumber = function (o) {
			Get("allConApNum").innerHTML=0;
			Get("allConSwNum").innerHTML=0;
			Get("allConBrNum").innerHTML=0;
			Get("allConCvgNum").innerHTML=0;
			Get("allConTotalNum").innerHTML=0;
			warnDialog.cfg.setProperty('text', "<s:text name="hm.missionux.wecomle.inventory.cannot.getdevice.msg"/>");
			warnDialog.show();
			Get("loadPapgDiv").style.display="none";
			return false;
		};

		var url = "newStartHere.action?operation=fetchDeviceNumber&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('post', url,  {success : succFetchDeviceNumber, failure : failureFetchDeviceNumber, timeout: 15000});

	}
</script>