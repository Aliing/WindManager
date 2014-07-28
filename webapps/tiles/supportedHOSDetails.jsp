<%@taglib prefix="s" uri="/struts-tags"%>
<style>
.title {
    text-transform: capitalize;
}
#versionContents {
    padding: 10px 0 10px 20px;
    line-height: 22px;
}
</style>
<div id="osDetails" style="display: none;">
    <div class="hd title"><s:text name="notification.message.osversions.title" /></div>
    <div class="bd">
        <div><s:text name="notification.message.osversions.desc" /></div>
        <div id="versionContents"></div>
        <div id="osDetailsHelp">
            <a href="<s:url action='hiveAp' includeParams='none' />?hmListType=managedHiveAps&operation=managedHiveAps&viewType=config"><s:text name="notification.message.osversions.link" /></a>
        </div>
    </div>
</div>
<script type="text/javascript">
var osDetailsPanel;
function openOSDetailPanel() {
	if(null == osDetailsPanel) {
		createOSDetailPanel();
	}
	osDetailsPanel.center();
	osDetailsPanel.cfg.setProperty('visible', true);
}
function createOSDetailPanel() {
	osDetailsPanel = new YAHOO.widget.Panel("osDetails", {
		width: "380px",
		visible: false,
		close: true,
		draggable: true
	});
	var contents = "";
	var deviceModel, deviceModelName, deviceModelVersion;

	<s:iterator value="@com.ah.util.notificationmsg.AhNotificationMsgUtil@getLatestDeviceSupportVersionDescMap()">
	deviceModel = '<s:property value="key.key" />';
	deviceModelName = '<s:property value="key.value" />';
	deviceModelVersion = '<s:property value="value" />';
	contents += deviceModelName + ": <b>" + deviceModelVersion +"</b><br/>"; 
	</s:iterator>
	//osDetailsPanel.setBody(contents);
	document.getElementById("versionContents").innerHTML = contents;
	
	osDetailsPanel.render(document.body);
	document.getElementById("osDetails").style.display = "";
	
    var escKeyListener = new YAHOO.util.KeyListener(document, { keys:27 },  
              { fn: osDetailsPanel.hide, scope:osDetailsPanel, correctScope:true });
    escKeyListener.enable();
    osDetailsPanel.cfg.queueProperty("keylisteners", escKeyListener);
}
</script>