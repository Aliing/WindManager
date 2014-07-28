<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script src="<s:url value="/js/hm.util.js" />"></script>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script type="text/javascript">
	var formName = 'networkPolicy';
	
	/*Click the new button in dlg  */
	function newVpn(blnWirelessRouter){
		var url = "<s:url action='vpnServices' includeParams='none' />?operation=new&jsonMode=true"+"&wirelessRoutingEnabled="+blnWirelessRouter+"&ignore="+new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : succNewVpn, failure : resultDoNothing, timeout: 60000}, null);	
	}

	var succNewVpn = function(o) {
	    subDrawerOperation= "createVpnService";
		hideSubDialogOverlay();
		// set the sub drawer title
		accordionView.setSubDrawerTitle('netWorkPolicy', 'sub_NetworkPolicy', initialSubDrawerTitle("New VPN Service"));
		accordionView.expandSubDrawer('netWorkPolicy', 'sub_NetworkPolicy');
		
		var subDrawerContentId = accordionView.getSubDrawerContentId('netWorkPolicy', 'sub_NetworkPolicy');
		set_innerHTML(subDrawerContentId, o.responseText);
		notesTimeoutId = setTimeout("hideNotes()", 4000);
	}

	function finishSelectVpn(){
		var itemValue = hm.util.getSelectedCheckItems("vpnSelectedId");
		if (hm.util._LIST_SELECTION_NOITEM == itemValue) {
			Get("errNote").innerHTML="There is no item.";
		    return;
		}
		/* else if (hm.util._LIST_SELECTION_NOSELECTION == itemValue) {
			Get("errNote").innerHTML='<s:text name="error.pleaseSelect"><s:param>VPN Profile</s:param></s:text>';
		    return;
		}  */
		document.forms["selectVpn"].operation.value = "finishSelectVpn";
		var url = "<s:url action='networkPolicy' includeParams='none' />?ignore="+new Date().getTime();
		YAHOO.util.Connect.setForm(document.getElementById("selectVpn"));
		var transaction = YAHOO.util.Connect.asyncRequest('post', url, {success : succFinishSelectVpn, failure : finishSelectVpnDoNothing, timeout: 60000}, null);	
	} 

	var succFinishSelectVpn = function (o){
		try {
			eval("var details = " + o.responseText);
		}catch(e){
			hideSubDialogOverlay();
			yDom.replaceClass(Get("netWorkPolicyContentId"), "hidden", "block");
			set_innerHTML("netWorkPolicyContentId",o.responseText);
			return;
		}
		var hideErrNotes = function () {
			hm.util.wipeOut('errNote', 800);
		}
		hm.util.show("errNote");
		Get("errNote").className="noteError";
		Get("errNote").innerHTML=details.e;
		var notesTimeoutId = setTimeout("hideErrNotes()", 4000);
	}; 
	
	var finishSelectVpnDoNothing = function(o){
		
	}
	
	function editVpnServiceDialog(vpnServiceId, event) {
        // close this dialog
        hideSubDialogOverlay();
        // expand the subdrawer
        editVpnService(vpnServiceId,<s:property value="dataSource.blnWirelessRouter" />);
        
        // stop bubble!!!!
        hm.util.stopBubble(event);

        // init the callback function
		//this function will be called when back to the NetworkPolicy drawer from the sub drawer,
		//and destroy after invoked.
        networkPolicyCallbackFn = function() {
        /* 	alert("enter");
       	 var url = "<s:url action='networkPolicy' includeParams='none' />?operation=selectVpnProfile"
			 	+ "&ignore="+new Date().getTime();
       		selectVpn(url); */
        }
	}
	
</script>

<div id="content" style="padding: 0px;">
	<s:form action="networkPolicy" name="selectVpn" id="selectVpn">
		<s:hidden name="operation" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><div id="errNote" class="noteError"></div></td>
			</tr>
			<tr>
				<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td>
								<ah:checkList name="vpnSelectedId" list="vpnSelectList" width="100%"
									listKey="id" listValue="value" value="vpnSelectedId" editEvent="editVpnServiceDialog"/>
							</td>
						</tr>
						<tr>
							<td height="15px"/>
						</tr>
						<tr>
							<td align="center" width="100%">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td class="npcButton" ><a href="javascript:void(0);"
													class="btCurrent" onclick="finishSelectVpn();"
													title="<s:text name="config.networkpolicy.button.ok"/>"><span><s:text
																name="config.networkpolicy.button.ok" />
												</span>
											</a>
										</td>
										<td width="40px">&nbsp;</td>
										<s:if test="%{writeDisabled==''}">
											<td class="npcButton"><a href="javascript:void(0);"
													class="btCurrent" onclick="newVpn(<s:property value="%{dataSource.configType.routerContained}"/>);"
													title="<s:text name="config.networkpolicy.button.new"/>"><span><s:text
														name="config.networkpolicy.button.new" />
													</span>
												</a>
											</td>
										</s:if>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</s:form>
</div>