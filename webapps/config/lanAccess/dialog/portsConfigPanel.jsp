<%@taglib prefix="s" uri="/struts-tags"%>
<div id="portTypeSelection" style="display: none;">
    <div class="hd">Port Group Settings</div>
    <div class="bd">
        <div style="padding: 10px 0;">
            <label>Port Type:</label><s:radio name="portType" list="enumPortType" listKey="key" listValue="value" value="1"/>
        </div>
        <div class="linkagg">
            <input type="checkbox" name="enableLinkAgg" id="enableLinkAggChk"/>
            <label for="enableLinkAggChk"><s:text name="config.port.linkaggregation.desc"/></label>&nbsp;
            <input id="portChannelId" style="width: 60px;" maxlength="2" class="portChannel"
                onkeypress="return hm.util.keyPressPermit(event,'ten');" disabled/>&nbsp;(1-64)
        </div>
        <!-- div class="linkagg" style="padding-left: 25px;">
            <label><s:text name="config.port.linkaggregation.lb.mode"/></label>&nbsp;
            <s:select list="enumLoadblanceMode" listKey="key" listValue="value"
                cssClass="portChannel"  cssStyle="width: 175px;"
                name="loadBlanceMode" disabled="true"></s:select>
        </div-->
    </div>
    <div class="ft" style="font-size: 100%; text-align: center;">
        <input type="button" id="OkBtn" value="<s:text name="common.button.ok"/>"/>
        <input type="button" id="CancelBtn" value="<s:text name="common.button.cancel"/>"/>
    </div>
</div>
<script>
// for Port selection
var portTypePanel;
var curPortType = -1;
function openPortTypePanel() {
    if(null == portTypePanel) {
        var div = document.getElementById('portTypeSelection');
        portTypePanel = new YAHOO.widget.Panel(div, {
            width: "545px",
            visible:false,
            fixedcenter:true,
            close: true,
            draggable:true,
            modal:true,
            constraintoviewport:true,
            underlay: "none",
            zIndex:1
            });
        //Allow escape key to close box
        var escListener = new YAHOO.util.KeyListener(document, { keys:27},
                  { fn:portTypePanel.hide,scope:portTypePanel,correctScope:true } );
        portTypePanel.cfg.queueProperty("keylisteners", escListener);
        portTypePanel.render(document.body);
        
        //init event on panel
        new YAHOO.widget.Button("OkBtn", 
                { onclick: {fn: function(){
                    if(isLinkAggEnalbed(curPortType)) {
                        var portChannelEl = Get("portChannelId");
                        if(portChannelEl) {
                            var msg = hm.util.validateNumberRange(portChannelEl.value, 'Port Channel Number', 1, 64, true);
                            if(msg) {
                                hm.util.reportFieldError(portChannelEl, msg);
                            } else {
                                //var param = {portChannel: portChannelEl.value, loadBlanceMode: lbModeEl.value, portType: curPortType};
                                var param = {portChannel: portChannelEl.value, portType: curPortType};
                                $('#portGroupSection').portsConfig('configure', param);
                            }
                        }
                    } else {
                        $('#portGroupSection').portsConfig('configure', {portType: curPortType});
                    }
                }}});
        new YAHOO.widget.Button("CancelBtn", 
                {onclick: {fn: function() {portTypePanel.hide()}}});
        YAHOO.util.Event.on("enableLinkAggChk", "click", function(){
            $('.portChannel').attr('disabled', !this.checked);
        });
        YAHOO.util.Event.addListener(['portType1', 'portType2', 'portType3', 'portType4', 'portType5', 'portType6'], 
                "click",  function(){
            curPortType = this.value;
            if(isLinkAggEnalbed(this.value)) {
                $('.linkagg').show();
            } else {
                $('.linkagg').hide();
            }
        });
        
        div.style.display = "";
    }
    // before open
    var group = $('#portGroupSection').portsConfig('getCurrentGroup');
    if(group.portType == -1) {
        $('#portType1').click();
    } else {
        $('#portType'+group.portType).click();
    }
    
    portTypePanel.show();
}
function isLinkAggEnalbed(portType) {
    return (portType == 2 || portType == 4 || portType ==5);
}
</script>