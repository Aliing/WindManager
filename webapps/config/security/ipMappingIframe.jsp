<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
var positionId;
function onLoadPage() {
	positionId=parent.document.getElementById("vpnNetworks_positionId").value;
	var obj=document.getElementById("positionId").options;
	for(var i=0;i<obj.length;i++){
		if(obj[i].value==positionId){
			obj[i].selected=true;
			break;
		}
	}
	requestAction(0,true);
}
function requestAction(showType,parentParam){
	var branchSize = "<s:property value="%{branchSize}"/>";
	var localNetWork="<s:property value="%{localNetWork}"/>";
	var firstIpIsGateWay="<s:property value="%{firstIpIsGateWay}"/>";
	var positionRange="<s:property value="%{positionRange}"/>";
	var uniqueSubnetwork="<s:property value="%{uniqueSubnetwork}"/>";
	var branchId=document.getElementById("branchId").value;
	if(!parentParam){
		positionId=document.getElementById("positionId").value;
	}
	var url = "<s:url action='vpnNetworks.action' includeParams='none' />?operation=ipMappingResultDiag&branchId="
			  +branchId+"&positionId="+positionId+"&branchSize="+branchSize+"&localNetWork="+localNetWork+
			  "&firstIpIsGateWay="+firstIpIsGateWay+"&showType="+showType+"&positionRange="+positionRange+
			  "&uniqueSubnetwork="+uniqueSubnetwork+"&ignore="+new Date().getTime();
	var iframe = document.getElementById("branchResultIframe");
	iframe.src=url;
}
function changeShowType(){
	var showType=document.getElementById("showType").value;
	if(0==showType){
		document.getElementById("branchId").style.display="none";
		document.getElementById("positionId").style.display="";
		requestAction(0,false);
	}else{
		document.getElementById("branchId").style.display="";
		document.getElementById("positionId").style.display="none";
		requestAction(1,false);
	}
}
</script>
<div>
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr height="35">
			<td width="100%">
				<table>
					<tr>
						<td width="60"><s:text name="config.vpn.subnet.ipMappingSerarch"></s:text></td>
						<td><select id="showType" onchange="changeShowType()"
							style="width: 80px">
								<option value="0">
									<s:text name="config.vpn.subnet.ipMappingSerarch.position" />
								</option>
								<option value="1">
									<s:text name="config.vpn.subnet.ipMappingSerarch.branch" />
								</option>
						</select></td>
						<td><s:select id="positionId" name="positionId"
								list="%{positionList}" listKey="id" listValue="value"
								cssStyle="width: 120px;margin-left: 5px"
								onchange="requestAction(0)" /> <s:select id="branchId"
								name="branchId" list="%{branchList}" listKey="id"
								listValue="value"
								cssStyle="width: 120px;margin-left: 5px;display:none"
								onchange="requestAction(1)" /></td>
						<s:if test="%{!uniqueSubnetwork}">
							<td><font color="red"> <s:text
										name="config.vpn.subnet.ipMappingSerarch.allBranchSame"></s:text></font>
							</td>
						</s:if>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3"><iframe id="branchResultIframe"
					name="branchResultIframe" frameborder="0" width="100%"
					height="230px" src=""> </iframe></td>
		</tr>
	</table>
</div>
