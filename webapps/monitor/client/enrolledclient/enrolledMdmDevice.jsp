<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<link rel="stylesheet" href="css/hm.css">
<link rel="stylesheet" href="css/te.css" />
<link rel="stylesheet" href="css/data_table.css" />
<script src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/mvc/ae.js"></script>
<style>
.activeRound{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/activeRound.png) no-repeat center;
	vertical-align:center
}
.inactiveRound{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/inactiveRound.png) no-repeat center;
	vertical-align:center
}
.mdm-set-item-title-enroll{
	background-repeat:no-repeat;
	background-position: 0 0;
	font-weight:700;
}
#contentenroll{
	width:920px;
}
.managed{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/yes.png) no-repeat center;
	vertical-align:center
}
.unmanaged{
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/no.png) no-repeat center;
	vertical-align:center
}
.locationIconInfo{
	cursor:pointer;
	width:16px;
	height:16px;
	float:left;
	background-repeat:no-repeat;
	background:url(../hm/images/icons/loca_info.png) no-repeat center;
	vertical-align:center
}
</style>
<script>
function insertPageContext()
{
	document.writeln('<td class="crumb" nowrap><a href="<s:url action="enrolledClients" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
	<s:if test="%{dataSource.id==null}">
		document.writeln('New </td>');
	</s:if>
	<s:else>
		//<s:if test="%{dataSource.defaultFlag}">
		//	document.writeln('Default Value \'<s:property value="displayName" />\'</td>');
		//</s:if>
		//<s:else>
		    document.writeln('\'<s:property value="displayName" />\'</td>');
		//</s:else>
	</s:else>
}
</script>
	<div>
		<table>
			<tr>
				<td>
					<tiles:insertDefinition name="context" /> 
				</td>
			</tr>
			<tr>
				<td>
					<tiles:insertDefinition name="notes" />
				</td>
			</tr>
		</table>
	</div>
	<div id="contentenroll">
		<div class="deinfo-area J-mod" data-switch="on"  data-mod="AE.hm.mdm.deviceInfo">
					<p class="mdm-set-item-title-enroll"><s:text name="monitor.enrolled.device.general.info"/></p>
					<div class="mdm-set-item-content-info" style="display:block;">
						<table width="100%" cellspacing="0" cellpadding="0" class="mdm-set-item-table">
							<tbody>
							 <s:if test="%{generalInformation.size==0}">
								<ah:emptyList/>
							</s:if>
						   	<s:iterator value="%{generalInformation}">
										<tr>
											<td width="20%">
												<s:text name="monitor.enrolled.device.app.status.info"/>
											</td>
											<td width="30%">
												<s:if test="%{status == 0}">
													<div class="inactiveRound" title="Inactive"></div><s:text name="monitor.enrolled.client.status.off"/>
												</s:if>
												<s:if test="%{status == 1}">
													<div class="activeRound" title="Active"></div><s:text name="monitor.enrolled.client.status.on"/>
												</s:if>
												<s:if test="%{status == null}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
											</td>
											<td width="20%">
												<s:text name="monitor.enrolled.client.connect"/>
											</td>
											<td width="30%">
												<s:if test="%{lastConnect == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="lastConnect"/>
												</s:else>
											</td>
										</tr>
										<tr >
											<td > 
												<s:text name="monitor.enrolled.device.general.info.phone"/>
											</td>
											<td>
												<s:if test="%{phoneNum == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="phoneNum"/>
												</s:else>
												
											</td>
											<td>
												<s:text name="monitor.enrolled.device.general.info.udid"/>
											</td>
											<td>
												<s:if test="%{udid == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="udid"/>
												</s:else>
											</td>
										</tr>
										<tr>
											<td>
												<s:text name="monitor.enrolled.device.general.info.storage"/>
											</td>
											<td>
													<s:if test="%{deviceStorage == '----'}">
															<s:text name="monitor.enrolled.device.detail.info.blank"/>
													</s:if>
													<s:else>
													<div style="margin-top:-6px;height:15px;width:180px;float:left;vertical-align:middle;border:1px solid #555555;">
													<div style="position:relative;height:15px;line-height:15px;width:<s:property value='storagePercentage'/>;background-color:green;">
														<div style="position:absolute;right:5px;color:white;"></div>
													</div>
													<div style="margin-top:6px">
															<s:property value="deviceStorage"/>
													</div>
												</div>
												</s:else>
											</td>
											<td>
												<s:text name="monitor.enrolled.device.general.info.battery.level"/>
											</td>
											<td>
													<s:if test="%{batteryLevel == '----'}">
															<s:text name="monitor.enrolled.device.detail.info.blank"/>
													</s:if>
													<s:else>
													<div style="margin-top:-6px;height:15px;width:180px;float:left;vertical-align:middle;border:1px solid #555555;">
													<div style="position:relative;height:15px;line-height:15px;width:<s:property value='batteryPercentage'/>;background-color:green;">
														<div style="position:absolute;right:5px;color:white;"></div>
													</div>
													<div style="margin-top:6px">
															<s:property value="batteryLevel"/>
													</div>
												</div>
												</s:else>
											</td>
										</tr>
										<tr style="padding-top:10px;">
											<td><div style="margin-top:10px;"><s:text name="monitor.enrolled.client.passcode.present"/></div></td>
											<td>
												<table>
													<tr>
													<s:if test="passwordPresent == null">
														<td><s:text name="monitor.enrolled.device.detail.info.blank"/></td>
													</s:if>
													<s:if test="passwordPresent == 'false'">
														<td><div class="unmanaged" title="False"></div></td><td>False</td>
													</s:if>
													<s:if test="passwordPresent == 'true'">
														<td><div class="managed" title="True"></div></td><td>True</td>
													</s:if>
													</tr>
												</table>
											</td>
											<td><div style="margin-top:10px;"><s:text name="monitor.enrolled.client.data.protection"/></div></td>
											<td>
												<table>
													<tr>
													<s:if test="dataProtection == null">
														<td><s:text name="monitor.enrolled.device.detail.info.blank"/></td>
													</s:if>
													<s:if test="dataProtection == 'false'">
														<td><div class="unmanaged" title="False"></div></td><td>False</td>
													</s:if>
													<s:if test="dataProtection == 'true'">
														<td><div class="managed" title="True"></div></td><td>True</td>
													</s:if>
													</tr>
												</table>
											</td>
										</tr>
							</s:iterator>
							<tr style="padding-left:0px;">
								<td><p class="mdm-set-item-title-enroll" style="padding-left:0px;"><s:text name="monitor.enrolled.device.network.info"/></p></td>
							</tr>
							 <s:iterator value="%{networkInformation}">
										<tr>
											<td>
												<s:text name="monitor.enrolled.device.network.cellular.tech.info"/>
											</td>
											<td>
											<s:if test="%{cellularTech == 0}">
												<s:text name="monitor.enrolled.device.detail.info.blank"/>
											</s:if>
											<s:if test="%{cellularTech == 1}">
												<s:text name="monitor.enrolled.device.network.cellular.tech.info.gsm"/>
											</s:if>
											<s:if test="%{cellularTech == 2}">
												<s:text name="monitor.enrolled.device.network.cellular.tech.info.cdma"/>
											</s:if>
											<s:if test="%{cellularTech == null}">
												<s:text name="monitor.enrolled.device.detail.info.blank"/>
											</s:if>
											</td>
											<td>
												<s:text name="monitor.enrolled.device.network.operator.info"/>
											</td>
											<td>
												<s:if test="%{simCarrierNetwork == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="simCarrierNetwork"/>
												</s:else>
											</td>
										</tr>
										<tr>
											<%-- <td>
												<s:text name="monitor.enrolled.device.network.carrier.ver.info"/>
											</td>
											<td>
												<s:if test="%{carrierVersion == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="carrierVersion"/>
												</s:else>
											</td> --%>
											<td> 
												<s:text name="monitor.enrolled.device.network.modem.info"/>
											</td>
											<td>
												<s:if test="%{modemFirmware == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:if test="%{modemFirmware == null}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="modemFirmware"/>
												</s:else>
											</td>
											<td>
												<s:text name="monitor.enrolled.device.network.wifi.info"/>
											</td>
											<td>
												<s:if test="%{wifiMac == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="wifiMac"/>
												</s:else>
											</td>
										</tr>
										<tr>
											<%-- <td>
												<s:text name="monitor.enrolled.device.network.ip.info"/>
											</td>
											<td>
												<s:if test="%{ipAddress == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:if test="%{ipAddress == null}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="ipAddress"/>
												</s:else>
											</td> --%>
											<%-- <td>
												<s:text name="monitor.enrolled.device.network.wifi.info"/>
											</td>
											<td>
												<s:if test="%{wifiMac == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="wifiMac"/>
												</s:else>
											</td> --%>
											<td>
												<s:text name="monitor.enrolled.device.network.blue.info"/>
											</td>
											<td>
												<s:if test="%{blueToothMAC == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="blueToothMAC"/>
												</s:else>
											</td>
											<td>
											</td>
											<td></td>
										</tr>
							</s:iterator>
							<tr style="padding-left:0px;">
								<td>
									<p class="mdm-set-item-title-enroll" style="padding-left:0px;"><s:text name="monitor.enrolled.device.restriction.info"/></p>
								</td>
							</tr>
							 <s:if test="restictionInformation.size==0">
								<tr>
									<td><s:text name="monitor.enrolled.device.detail.no.restriction.value"/></td>
								</tr>
							</s:if>
							 <s:iterator value="%{restictionInformation}">
										<tr>
											<td colspan="2">
												<s:property value="name"/>
											</td>
											<td colspan="2">
												<s:property value="value"/>
											</td>
										</tr>
							</s:iterator>
							<tr>
								<td colspan="2"><p class="mdm-set-item-title-enroll" style="padding-left:0px;"><s:text name="monitor.enrolled.device.app.info"/></p></td>
								<s:iterator value="%{networkInformation}">
								<td>
									 <s:if test="ipAddress == null || ipAddress == ''">
										 <p class="mdm-set-item-title-enroll" style="padding-left:0px;" title="<s:text name='monitor.enrolled.device.loc.location.title'/>(No IP Information)"><s:text name="monitor.enrolled.device.loc.info.approximate"/></p>
									 </s:if>
									<s:else>
										<p class="mdm-set-item-title-enroll" style="padding-left:0px;" title="<s:text name='monitor.enrolled.device.loc.location.title'/>('<s:property value="ipAddress"/>')">
										<table>
											<tr><td><s:text name="monitor.enrolled.device.loc.info.approximate"/></td>
											<s:if test="markOnPrimess == true">
												<td><div class="locationIconInfo" title="<s:text name="monitor.enrolled.device.location.regist.mapid.remainder"/>"></div></td>
											</s:if>
											<s:else>
												<td><div class="locationIconInfo" title="<s:text name='monitor.enrolled.device.loc.location.title'/>('<s:property value="ipAddress"/>')"></div></td>
											</s:else>
											</tr>
										</table>
										</p>
									</s:else>
								</td>
								</s:iterator>
							</tr>
					 		<tr>
								<td colspan="2" style="padding-left:0px;padding-right:20px;">
									<div class="mdm-set-item-content-info" style="display:block;">
										<table width="100%" cellspacing="0" cellpadding="0" class="datatable" id="J-dataTable">
											<thead>
												<tr>
 													<th><s:text name="monitor.enrolled.device.app.name.info"/></th>
													<%-- <th><s:text name="monitor.enrolled.device.app.managed.info"/></th> --%>
													<th><s:text name="monitor.enrolled.device.app.version.info"/></th>
													<th><s:text name="monitor.enrolled.device.app.appSize.info"/></th>
													<th><s:text name="monitor.enrolled.device.app.dataSize.info"/></th>
												</tr>
											</thead>
											<tbody>
						 					<s:iterator value="%{appcationInformation}">
											<tr style="width:418px;">
												<td>
												<s:if test="%{name == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="name"/>
												</s:else>
												</td>
												<%-- <td>
												<s:if test="%{managed == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="managed"/>
												</s:else>
												</td> --%>
												<td>
												<s:if test="%{version == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="version"/>
												</s:else>
												</td>
												<td>
												<s:if test="%{bundleSize == ''}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="bundleSize"/>
												</s:else>
												</td>
												<td>
												<s:if test="%{dynamicSize.equals('')}">
													<s:text name="monitor.enrolled.device.detail.info.blank"/>
												</s:if>
												<s:else>
													<s:property value="dynamicSize"/>
												</s:else>
												</td>
											</tr>
											</s:iterator> 
										</tbody>
									</table>
								</div>
								</td>
								 <td colspan="2" style="padding-left:0px;">
								<div class="mdm-set-item-content-info" style="display:block;">
									<s:iterator value="%{enrolledClientDetails}">
										<div id="locationDiv" class="edit">
											<table>
												<tr>
													<td style="width:164px;"><s:text name="monitor.enrolled.device.loc.latt.longi"/></td>
													<s:if test="latitude == '' || longitude == ''">
													<td title="<s:text name='monitor.enrolled.device.loc.aero.def.info'/>"><div style="width:150%;">-122.013881, 37.409608</div></td>
													</s:if>
													<s:else>
													<td title="<s:property value='address'/>"><div style="width:150%;" id="long_lati_location"><s:property value="latitude"/><s:property value="longitude"/></div></td>
													</s:else>
												</tr>
											</table>
										</div>
										<div id="J-map" style="margin-top:5px;margin-bottom:50px;border-style:solid;border-width:1pt; border-color:#cccccc;min-width:350px;min-height:300px"></div>
									</s:iterator>
								</div>
								</td>
							</tr> 
							</tbody>
						</table>
				</div>
		</div>
	</div>
	<s:if test="enableGoogleMapKey == false">
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&sensor=false<s:property escape="false" value="gmeKey"/>"></script>
	</s:if>
	<s:else>
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=<s:property escape="false" value="gmeKey"/>&sensor=false"></script>
	</s:else>
	 <script >

	$(function() {
		 var location = {
	            areacode : '',
	            city : '',
	            country_code : '',<s:iterator value="%{enrolledClientDetails}">
				country_name:'<s:property value="address"/>',
				ip : '<s:property value="publicIp"/>',
				latitude : '<s:property value="latitude"/>',
				longitude : '<s:property value="longitude"/>',</s:iterator>
	            metrocode : '',
	            region_code : '',
	            region_name : '',
	            zipcode :''
	    }, ipQueryReturned = false;
	    var long_lati_location = $('#long_lati_location');
	    
	    if(location.latitude == '' || location.longitude == ''){
	        var url = 'http://freegeoip.net/json/' + location.ip;
	        $.get(url, function(result){
	            location = result;
	            ipQueryReturned = true;
	        }, 'json');
	    }else{
	        ipQueryReturned = true;
	    }
	    
	    var initialize = function(){
	        // default ,we suppose we can't find the latitude and longitude
	        var zoom = 16;
	        if(location.latitude == '' || location.longitude == ''){
	            location.country_name = 'Aerohive Networks 330 Gibraltar Drive Sunnyvale, CA 94089 United States';
	            location.latitude = 37.409608, location.longitude = -122.013881;
	        }
	        if(long_lati_location.length){
	            long_lati_location.attr('title', location.city+' '+location.region_name+' '+location.country_name);
	            long_lati_location.html(location.longitude + ' , ' + location.latitude);
	        }
	        var center = new google.maps.LatLng(location.latitude, location.longitude);
	       var mapOptions = {
	                center: center,
	                scaleControl: true,
	                zoom: zoom,
	                mapTypeId: google.maps.MapTypeId.ROADMAP,
	                panControl: true,
	                streetViewControl: false,
	                zoomControl: true
	                };
	        var map = new google.maps.Map(document.getElementById('J-map'),mapOptions);
	        var infowindow = new google.maps.InfoWindow(); 
	        infowindow.setContent("<div style='min-height:80px;'><table><tr><td>Address:</td><td>"+location.country_name+' '+location.region_name+' '+location.city+"</td></tr><tr><td>Longitude:</td><td>"+location.longitude+"</td></tr><tr><td>Latitude:</td><td>"+location.latitude+"</td></tr></table></div>");
	        var marker = new google.maps.Marker({
	            position: center,
	            map: map
	        });
	        google.maps.event.addListener(marker, 'click', function(event) {
	            infowindow.open(map, marker);
	       }); 
	    };
	  	 window.onload = function(){
	        var interval = setInterval(function(){
	            if(ipQueryReturned){
	                clearInterval(interval);
	                initialize();
	            }
	        }, 100);
	    };
	});  
	</script>
	<script type="text/javascript" src="js/config/txt_configs.js"></script>
	<script type="text/javascript" src="js/config/form_configs.js"></script>
	<script type="text/javascript" src="js/common/tools.js"></script>
	 <script type="text/javascript" src="js/pluins/google_map_load.js"></script> 
	 <script type="text/javascript" src="js/pluins/data.table.js"></script> 
	 <script type="text/javascript" src="js/app/device_info.js"></script> 
	 <script type="text/javascript">
		AE.Mod.init();
	</script> 
	