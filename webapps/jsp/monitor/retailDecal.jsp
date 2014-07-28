<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<script type="text/javascript">
var decalPanel;
function createDecalPanel(){
	var div = document.getElementById('decalPanel');
	decalPanel = new YAHOO.widget.Panel(div, { width:"620px", visible:false, fixedcenter:"contained", draggable:true, constraintoviewport:true } );
	decalPanel.render(document.body);
	div.style.display = "";
}
function showDecalPanel(){
	if(null == decalPanel){
		createDecalPanel();
	}
	decalPanel.show();
}
function hideDecalPanel(){
	if(null != decalPanel){
		decalPanel.hide();
	}
}
function printDecal(){
	pwin = window.open(document.getElementById("retail_decal_img").src, "_blank");
	setTimeout(function(){pwin.print();}, 500);
	hideDecalPanel();
}

</script>
<div id="decalPanel" style="display: none;">
    <div class="hd"><s:text name="presence.retail.analytics.decal.title" /></div>
    <div class="bd">
    	<table class="settingBox" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<div style="padding: 4px; text-align: center;"><s:text name="presence.retail.analytics.decal.message"></s:text></div>
				</td>
			</tr>
			<tr>
				<td align="center">
					<img id="retail_decal_img" src="<s:url value="/images/Euclid_Aerohive_Decal.png" includeParams="none"/>"
					width="447" height="448" alt="" class="dblk">
				</td>
			</tr>
    	</table>
    </div>
    <div class="ft">
    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    		<tr>
    			<td align="center">
    				<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td><input type="button" name="ignore" value="<s:text name="button.print" />"
								class="button" onClick="printDecal();"
								<s:property value="writeDisabled" />></td>
							<td><input type="button" name="ignore" value="<s:text name="button.close" />"
								class="button" onClick="hideDecalPanel();"
								<s:property value="writeDisabled" />></td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
    				</table>
    			</td>
    		</tr>  	
    	</table>
    </div>
</div>
