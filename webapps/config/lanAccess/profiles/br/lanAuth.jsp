<%@taglib prefix="s" uri="/struts-tags"%>

<fieldset>
    <legend><s:text name="config.title.lanProfile.access"/></legend>
    <table>
        <s:if test="%{limitType == 3}">
        <tr>
            <td style="padding:5px 4px 0 6px" id="primaryAuthRow">
                <s:checkbox id="enablePrimaryAuth" name="dataSource.enabledPrimaryAuth"/>
                <label for="enablePrimaryAuth">
                    <s:text name="config.lanProfile.enable.auth.enable" />&nbsp;MAC
                </label>
                <div style="display: inline;">
                ,&nbsp;via&nbsp;
                <s:select name="dataSource.authProtocol"
                        list="%{enumRadiusAuth}" listKey="key"
                        listValue="value" cssStyle="width: 115px;" />
                <span>&nbsp;protocol</span>
                </div>
            </td>
        </tr>
        </s:if>
        <s:else>
        <s:if test="%{showIDMIcon}">
        <tr id="idmEnabledRow">
            <td>
                <div>
                <table cellpadding="0" cellspacing="0" border="0" width="100%">
                <tr style="line-height: 2em;">
                    <td width="160px"/>
                    <td id="idmContent">
                    <s:if test="%{usabledIDM}">
                    <s:checkbox id="enableIDMChk" name="dataSource.enabledIDM"/>
                    </s:if>
                    <span class="icon-idm <s:if test="%{!usabledIDM}">icon-idm-disable</s:if>">
                    <label for="enableIDMChk" class="text-idm <s:if test="%{!usabledIDM}">text-idm-disable</s:if>"
                    <s:if test="%{!usabledIDM}">title="<s:text name='warn.cloudauth.guide.register'/>"</s:if>
                    ><s:text name="config.radiusProxy.cloudAuth.use"/></label>
                    </span>&nbsp;
                    <s:if test="%{usabledIDM}">
                    <a id="manageGuestIDMAnchor" style="display: none;" href="<s:property value='manageGuestLink4IDM'/>" tabindex="-1" target="_blank"><s:text name="config.ssid.idm.guest.link.text"/></a>
                    </s:if>
                    <s:elseif test="%{allowedTrial}">
                    <a id="trialIDMAnchor" href="javascript: void(0);" tabindex="-1" title="<s:text name='config.ssid.idm.trial.link.desc'/>"><s:text name="config.ssid.idm.trial.link.text"/></a>&nbsp;&nbsp;
                    <a id="idmexplaination" href="javascript: void(0);" tabindex="-1" title="<s:text name='config.ssid.idm.desc'/>">?</a>
                    </s:elseif>
                    </td>
                </tr>
                </table>
                </div>
            </td>
        </tr>
        <tr>
            <td style="padding:5px 4px 0 6px;display: none;" id="8021xAuthRow">
                <s:checkbox id="enablePrimaryAuthHide" name="dataSource.enabledPrimaryAuth" disabled="true"/>
                <label for="enablePrimaryAuthHide"><s:text name="config.lanProfile.enable.auth.enable" />&nbsp;802.1X</label>
                <s:hidden name="dataSource.primaryAuth" disabled="true" value="1" id="primaryAuthHide"/>
            </td>
        </tr>
        </s:if>
        <s:else>
        <s:if test="%{dataSource.id != null && (usabledIDM || allowedTrial)}">
        <tr><td><div class="npcNoteTitle" style="padding: 0 0 0 25px;"><s:text name="glasgow_32.config.idm.wired.br100.note"/></div></td></tr>
        </s:if>
        </s:else>
        <tr>
            <td style="padding:5px 4px 0 6px" id="primaryAuthRow">
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
            </td>
        </tr>
        <tr>
            <td style="padding:5px 4px 0 25px" id="secondaryAuthRow">
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
            </td>
        </tr>
        </s:else>
        <tr>
            <td style="padding:5px 4px 0 6px">
                <s:checkbox id="cwpChk" name="dataSource.enabledCWP"/>
                <label for="cwpChk"><s:text name="hiveAp.ethCwp.enable.cwp" /></label>
            </td>
        </tr>
        <tr>
        	<td id="sameVlanSection" style="padding:5px 4px 0 6px;<s:if test="%{!dataSource.enablePrimaryAuth  && !dataSource.enabledCWP}">display:none</s:if>;">
	       		<s:checkbox id="enabledSameVlan" name="dataSource.enabledSameVlan"/>
	        	<label for="enabledSameVlan"><s:text name="config.port.authentication.sameVLAN.label"/></label>
	       </td>
        </tr>
    </table>
</fieldset>