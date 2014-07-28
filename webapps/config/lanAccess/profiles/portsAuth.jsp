<%@taglib prefix="s" uri="/struts-tags"%>
<div>
    <fieldset id="authSection">
        <legend>Authentication</legend>
        <div id="primaryAuthRow">
	        <s:checkbox id="enablePrimaryAuth" name="dataSource.enabledPrimaryAuth"/>
	        <label for="enablePrimaryAuth"><s:text name="config.lanProfile.enable.auth.first" /></label>&nbsp;
	        <s:select list="#{1: '802.1X', 2: 'MAC'}" name="dataSource.primaryAuth"
	            id="selectAuthOpt" cssStyle="width: 80px;"
	            listKey="key" listValue="value"/>
	        <s:if test='%{dataSource.primaryAuth == 2}'>
	        <div id="macAuthSection" style="display: inline;">
	        ,&nbsp;via&nbsp;
	        <s:select name="dataSource.authProtocol"
	                list="%{enumRadiusAuth}" listKey="key"
	                listValue="value" cssStyle="width: 115px;" />
	        <span>&nbsp;protocol</span>
	        </div>
	        </s:if>
        </div>
        <div style="padding: 5px 0 0 25px;" id="secondaryAuthRow">
           <s:checkbox id="enableSecondaryAuth" name="dataSource.enableSecondaryAuth"/>
           <label for="enableSecondaryAuth">
               <s:text name="config.lanProfile.enable.auth.second" />&nbsp;
               <span class="show48021x" <s:if test='%{dataSource.primaryAuth == 1}'>style="display: none;"</s:if>>802.1X</span>
               <span class="show4MAC" <s:if test='%{dataSource.primaryAuth == 2}'>style="display: none;"</s:if>>MAC</span>
           </label>
           <s:if test='%{dataSource.primaryAuth == 1}'>
           <div id="macAuthSection" style="display: inline;">
           ,&nbsp;via&nbsp;
           <s:select name="dataSource.authProtocol"
                   list="%{enumRadiusAuth}" listKey="key"
                   listValue="value" cssStyle="width: 115px;" />
           <span>&nbsp;protocol</span>
           </div>
           </s:if>
       </div>
       <s:if test="%{(normalView && (limitType != 1 && limitType !=5)) || chesapeakeAsRouter}">
       <div style="padding-top: 5px;" id="cwpSection">
           <s:checkbox id="cwpChk" name="dataSource.enabledCWP"/>
           <label for="cwpChk"><s:text name="hiveAp.ethCwp.enable.cwp"/></label>
       </div>
       <div id="cwpNoteSection" class="npcNoteTitle" style="padding: 0 0 0 25px;"><s:text name="config.port.authentication.cwp.note"/></div>
       </s:if>
       <div id="sameVlanSection" style="padding-top: 5px;<s:if test="%{(!dataSource.enablePrimaryAuth  && !dataSource.enabledCWP) || dataSource.portType != 4}">display:none</s:if>;">
       		<s:checkbox id="enabledSameVlan" name="dataSource.enabledSameVlan"/>
        	<label for="enabledSameVlan"><s:text name="config.port.authentication.sameVLAN.label"/></label>
       </div>
    </fieldset>
    <div id="apAuthenSection" style="padding: 5px 0;">
        <s:checkbox id="apAuthChk" name="dataSource.enabledApAuth"/> 
        <label for="apAuthChk"><s:text name="config.port.authentication.enabledAP.label"/></label>
    </div>
</div>