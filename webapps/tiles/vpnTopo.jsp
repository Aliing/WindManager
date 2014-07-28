<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script>


function fetchVPNNodes() {
	var url = '<s:url action="vpnServices" includeParams="none" />?operation=fetchVPNNodes&ignore=' + new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: addVPNNodes, failure : connectFailed }, null);
}

var addVPNNodes = function(o) {
	eval("var details = " + o.responseText);
	
	if(details.length < 1) {
		return ;
	}
	
	/*
	 * title
	 */
	 Get("vpnTitle").innerHTML = "VPN Topology";
	
	var table = Get('vpnTable');
	
	/*
	 * remove existing rows
	 */
	for(var i=table.rows.length - 1; i>=1; i--) {
		table.deleteRow(i);
	}
	
	/*
	 * add rows to table
	 */
	 for(var i=0; i<details.length; i++) {
		// tree iconhm
    	var newRow = table.insertRow(-1);
    	var oCell = newRow.insertCell(-1);
    	
    	oCell.style.align = "left";
    	
    	if(i == details.length - 1){
    		oCell.className = "ygtvcell ygtvln";
    	} else {
    		oCell.className = "ygtvcell ygtvtn";
    	}
    	
    	
    	oCell.innerHTML = "<a class='ygtvspacer' href='#'>&nbsp;</a>";
    	
    	// name
    	oCell = newRow.insertCell(-1);
    	oCell.style.align = "left";
    	
    	oCell.className = "ygtvcell ygtvcontent";
    	//oCell.style.width = "160px";
		oCell.innerHTML = "<span onclick='showVPNTopo(" + details[i].id + ")' class='ygtvlabel'>" + details[i].name + "</span>";
	 }
};

var connectFailed = function(o) {
};

function showVPNTopo(id) {
	openVpnTopologyPanel(id);
}

/* get VPN topology from server */
fetchVPNNodes();
</script>

<div>
<div id="vpnTitle" class="leftNavH1"></div>
<table id="vpnTable" border="0" cellspacing="0" cellpadding="0" width="100%">
</table>
</div>
<div id="vpnTopologyPanel" style="display: none;">
	<div class="hd"></div>
	<div class="bd">
		<iframe id="vpnTopology" name="vpnTopology" width="0" height="0"
			frameborder="0" style="background-color: #999;" src="">
		</iframe>
	</div>
</div>
<script type="text/javascript">
var vpnTopologyPanel = null;

function createVpnTopologyPanel(width, height){
	var div = document.getElementById("vpnTopologyPanel");
	width = width || 500;
	height = height || 400;
	var iframe = document.getElementById("vpnTopology");
	iframe.width = width;
	iframe.height = height;
	vpnTopologyPanel = new YAHOO.widget.Panel(div, { width:(width+20)+"px", 
							fixedcenter:true, 
							visible:false, 
							constraintoviewport:true } );
	vpnTopologyPanel.render(document.body);
	div.style.display="";
	vpnTopologyPanel.beforeHideEvent.subscribe(clearVpnTopologyData);
	var resize = createResizer("vpnTopologyPanel");
	resize.on("resize", function(args) {
		var panelHeight = args.height;
		this.cfg.setProperty("height", panelHeight + "px");
		iframe.width = args.width - 20;
		iframe.height = args.height - 42;
	}, vpnTopologyPanel, true);
	resize.on("endResize", function(args){
		vpnTopologyPanelResizeCallback();
	}, vpnTopologyPanel, true);
}

//Create Resize instance, binding it to the 'resizablepanel' DIV
function createResizer(binding){
    var resize = new YAHOO.util.Resize(binding, {
        handles: ["br"],
        autoRatio: false,
        minWidth: 650,
        minHeight: 400,
        useShim: true,//over iframe
        status: true
    });
    return resize;
}
function vpnTopologyPanelResizeCallback(){
	if(null != vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.location.reload();
	}
}
function clearVpnTopologyData(){
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "none";
	}
	if(vpnTopologyIframeWindow){
		vpnTopologyIframeWindow.onHidePage();
	}
}
function openVpnTopologyPanel(id){
	if(null == vpnTopologyPanel){
		var width = YAHOO.util.Dom.getViewportWidth();
		var height = YAHOO.util.Dom.getViewportHeight();
		createVpnTopologyPanel(width*0.8, height*0.8);
	}
	//fix YUI issue with IE: tables in iframe will not disappear while overlay hidden.
	if(YAHOO.env.ua.ie){
		document.getElementById("vpnTopology").style.display = "";
	}
	vpnTopologyPanel.header.innerHTML = "<s:text name="config.vpn.service.topology"/>";
	vpnTopologyPanel.show();
	var iframe = document.getElementById("vpnTopology");
	iframe.src ="<s:url value='vpnServices.action' includeParams='none' />?operation=initVpnTopologyPanel&id="+id+"&pageId="+new Date().getTime();
}
function updateVpnTopologyPanelTitle(str){
	if(null != vpnTopologyPanel){
		vpnTopologyPanel.header.innerHTML = "<s:text name="config.vpn.service.topology"/>"+" - "+str;
	}
}
var vpnTopologyIframeWindow;
</script>