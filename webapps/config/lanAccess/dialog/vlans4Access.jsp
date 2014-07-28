<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script>
YAHOO.util.Event.onContentReady("selectedVlanIdDiv", adjustEditLink, this);
</script>
<style type="text/css">
div#errorDisplayVlan4Acc {
    display: inline;
}
</style>
<div style="padding: 0; height: 125px;">
<s:form action="portAccess" name="nativeVLANSelectPage" id="nativeVLANSelectPage">
<s:hidden name="selectedAccessId" />
<s:hidden name="selectedVlanId" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr id="fe_errorDisplayVlan4Acc" style="display: none;">
            <td id="textfe_errorDisplayVlan4Acc" class="noteError" colspan="2">ChangeMe</td>
        </tr>
        <tr>
            <td class="labelT1" style="padding-left: 5px;padding-right: 5px; width: 85px;">Native VLAN <label style="color: red">*</label></td>
            <td>
                 <ah:createOrSelect divId="errorDisplayVlan4Acc" list="vlanList" typeString="Vlan4Acc"
                    hideButton="true"
                    selectIdName="selecteNativeVlanOpt" inputValueName="selectedNativeVlanName" swidth="135px" />
                 <s:if test="%{writeDisabled == 'disabled'}">
                     <img class="dinl marginBtn"
                         src="<s:url value="/images/new_disable.png" />"
                         width="16" height="16" alt="New" title="New" />
                 </s:if> <s:else>
                     <a class="marginBtn"
                         href="javascript:newVlan4Acc(<s:property value="%{#portProfile.id}"/>);"><img
                         class="dinl"
                         src="<s:url value="/images/new.png" />" width="16"
                         height="16" alt="New" title="New" /></a>
                 </s:else> <s:if test="%{writeDisabled == 'disabled'}">
                     <img class="dinl marginBtn"
                         src="<s:url value="/images/modify_disable.png" />"
                         width="16" height="16" alt="Modify" title="Modify" />
                 </s:if> <s:else>
                     <a class="marginBtn"
                         href="javascript:editVlan4Acc(<s:property value="%{#portProfile.id}"/>);"><img
                         class="dinl"
                         src="<s:url value="/images/modify.png" />"
                         width="16" height="16" alt="Modify" title="Modify" /></a>
                 </s:else>       
            </td>
        </tr>
        <tr>
            <td class="labelT1" style="padding-left: 5px;padding-right: 5px;">Allowed VLAN <label style="color: red">*</label></td>
            <td><s:textfield name="allowVlans" cssStyle="width: 130px;"/>&nbsp;<s:text name="config.port.access.vlan.popup.exmaple"></s:text></td>
        </tr>
        <tr>
            <td style="color: gray;padding-left: 5px;" colspan="2">
            <s:if test="%{acc4Chesapeake}">
            <s:text name="config.port.access.vlan.popup.limitation.note"></s:text>
            </s:if>
            <s:else>
            <s:text name="config.port.access.vlan.popup.limitation.br.note"></s:text>
            </s:else>
            </td>
        </tr>
        <tr id="fe_ErrorVlanRow" style="display: none;">
            <td class="noteError" id="textfe_ErrorVLANRow" colspan="4" style="padding-left: 20px;">To be changed</td>
        </tr>
        <tr>
            <td class="npcButton" style="position: relative;" colspan="2" >
               <s:if test="%{writeDisabled==''}">
               <div style="position: absolute; padding: 20px 0 0 105px;">
                  <a href="javascript: void(0);" class="btCurrent" style="margin-right:20px;" 
                          onclick="selectVlan4Acc(<s:property value='acc4Chesapeake'/>); return false;" 
                          title="<s:text name="common.button.ok"/>"><span style="text-align:center;"><s:text name="common.button.ok"/></span></a>
                   <a href="javascript: void(0);" onclick="hideSubDialogOverlay();return false;" 
                   style="margin-left:20px;"  
                   class="btCurrent" title="<s:text name="common.button.cancel"/>">
                   <span style="text-align:center;"><s:text name="common.button.cancel"/></span></a>
               </div>
               </s:if>
            </td>
        </tr>
    </table>
    </s:form>
</div>