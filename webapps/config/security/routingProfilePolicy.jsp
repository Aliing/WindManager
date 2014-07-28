<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.network.RoutingProfilePolicy"%>


<script src="<s:url value="/js/jquery.min.js" includeParams="none" />"
	type="text/javascript"></script>
<script
	src="<s:url value="/js/jquery.tablednd.0.7.min.js" includeParams="none" />"
	type="text/javascript"></script>


<script type="text/javascript">
var formName = 'routingProfilePolicy';
var POLICYRULE_SPLIT = <%=RoutingProfilePolicy.POLICYRULE_SPLIT%>;
var POLICYRULE_ALL = <%=RoutingProfilePolicy.POLICYRULE_ALL%>;
var POLICYRULE_CUSTOM = <%=RoutingProfilePolicy.POLICYRULE_CUSTOM%>;
var splitPrimaryKey="<s:property value='%{splitTunnelPrimarykey}'/>";
var splitBackupKey="<s:property value='%{splitTunnelBackupkey}'/>";
var deleteObject;
var tempCount=1;
$(document).ready(function() { 
   $("#customRuleTable").tableDnD(); 
   $("#templeprimaryoutselect").find("option").each(function(){
		if($(this).val()=="-------------------------")
		{
			$(this).attr("disabled","true");
		
		}
	});
   
   $("#templeout2select").find("option").each(function(){
		if($(this).val()=="-------------------------")
		{
			$(this).attr("disabled","true");
		}
	});
	 $("#splitTunnelPrimary").find("option").each(function(){
			if($(this).val()=="-------------------------")
			{
				$(this).attr("disabled","true");
			}
		});
	 $("#splitTunnelBackup").find("option").each(function(){
			if($(this).val()=="-------------------------")
			{
				$(this).attr("disabled","true");
			}
		});
	 
 		$("#tunelTd1").hide();
		$("#tunelTd2").hide();
		$("#tunelTd3").hide();
 });

function delcheckTableRow(obj){
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will remove the current item <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from the list. Do you want to continue?</body></html>");
	confirmDialog.show();
	deleteObject=obj;
}
function delEditingTableRow(obj){
	confirmDialog.cfg.setProperty('text', "<html><body>This operation will cancel the editing item <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from the list. Do you want to continue?</body></html>");
	confirmDialog.show();
	deleteObject=obj;
}
function onLoadPage() {
	if (!document.getElementById("profileName").disabled) {
		document.getElementById("profileName").focus();
	}
	
	initProfileType();
	<s:if test="%{jsonMode}">
	if(top.isIFrameDialogOpen()) {
		top.changeIFrameDialog(1080, 600);
	}
	</s:if>
}

function initProfileType(){
	
	<s:if test="%{dataSource.profileType==0}">
	$("#customRuleTable").attr("style","width: 940px;display:");
	$("#splitTunelTable").attr("style","width: 940px;display:none");
	$("#splitTunnelNotes").hide();
	$("#TunnelAllNotes").hide();
	$("#CustomNotes").show();
	Get(formName + "_dataSource_profileType"+POLICYRULE_CUSTOM).checked=true;
	
	</s:if>
	<s:if test="%{dataSource.profileType==1}">
	$("#customRuleTable").attr("style","width: 940px;display:none");
	$("#splitTunelTable").attr("style","width: 940px;display:");
	$("#splitTunnelNotes").show();
	$("#TunnelAllNotes").hide();
	$("#CustomNotes").hide();
	
	$("#splitTunneldestination1").show();
	$("#TunnelAlldestination1").hide();
	$("#splitTunneldestination2").show();
	$("#TunnelAlldestination2").hide();
	$("#splitTunnelTr").show();
	$("table[id=customRuleTable] input[name=ruleIndices]:checkbox").each(function(){
		var tr=$(this).parent().parent();
		tr.remove();
	});
	selector("splitTunnelPrimary",splitPrimaryKey);
	selector("splitTunnelBackup",splitBackupKey);
	if(splitPrimaryKey=="Drop"){
		$("#splitTunnelBackup").val("");
		$("#splitTunnelBackup").attr({disabled:true});
	}else{
		$("#splitTunnelBackup").attr({disabled:false});
		selector("splitTunnelBackup",splitBackupKey);
	}
	destroyer("splitTunnelPrimary","splitTunnelBackup");
	$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
	$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
	$("#tunelTd3").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" id="splitout1" value="" /><s:hidden name="out2arr" id="splitout2" value="" />	');	
	
	Get(formName + "_dataSource_profileType"+POLICYRULE_SPLIT).checked=true;
	
	</s:if>
	
	<s:if test="%{dataSource.profileType==2}">
	$("#customRuleTable").attr("style","width: 940px;display:none");
	$("#splitTunelTable").attr("style","width: 940px;display:");
	$("#splitTunnelNotes").hide();
	$("#TunnelAllNotes").show();
	$("#CustomNotes").hide();
	$("#splitTunneldestination1").hide();
	$("#TunnelAlldestination1").show();
	$("#splitTunneldestination2").hide();
	$("#TunnelAlldestination2").show();
	$("#splitTunnelTr").hide();
	
	$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="-" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
	$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
	$("#tunelTd3").html('');	
	$("table[id=customRuleTable] input[name=ruleIndices]:checkbox").each(function(){
		var tr=$(this).parent().parent();
		tr.remove();
	});
	Get(formName + "_dataSource_profileType"+POLICYRULE_ALL).checked=true;
// 	</s:if>
	
	<s:if test="%{dataSource.profileType==3}">
	$("#customRuleTable").attr("style","width: 940px;display:");
	$("#splitTunelTable").attr("style","width: 940px;display:none");
	$("#splitTunnelNotes").hide();
	$("#TunnelAllNotes").hide();
	$("#CustomNotes").show();
	Get(formName + "_dataSource_profileType"+POLICYRULE_CUSTOM).checked=true;
	
	</s:if>
	
}

function validateWanInterface(id)
{
	if($("#"+id).val()!="VPN" && $("#"+id).val()!="Drop" && $("#"+id).val()!=""&&  $("#"+id).val()!="Eth0"&&  $("#"+id).val()!="USB0")
	{
		return true;
	}else {
		return false;
	}
	
}

/* function validateMutiForwardAction(){
	var tempForwardValue=$("#select3").find("option:selected").val();
	var tempBackForwardValue=$("#select4").find("option:selected").val();
	if(tempForwardValue==tempBackForwardValue){
		$("tr[name='errormessagetr']").remove();
		hm.util.reportTableSelectError(document.getElementById("select3"), '<s:text name="config.routing.policy.rules.forwarding.muti.warnning" />');
		 document.getElementById("select3").focus();
    	return false;
	}
	return true;
} */

function saveRow (obj) {   
	$("tr[name='errormessagetr']").remove();
	if(!validateInput2())
	{
		return false;
	}
	if(!validateInput4()){
		return false;
	}
	if(!validateForwardingAction()){
		return false;
	}
/* 	if(!validateMutiForwardAction()){
		return false;
	} */
	if(!validateBackupForwardingAction){
		return false;
	}
	var sourcetypearr=$("#sourcetypearrhide").clone();
	var sourcevaluearr=$("#sourcevaluearrhide").clone();
	var destinationtypearr=$("#destinationtypearrhide").clone();
	var destinationvaluearr=$("#destinationvaluearrhide").clone();
	var out1arr=$("#out1arrhide").clone();
	var out2arr=$("#out2arrhide").clone();
	var nRow =$(obj).parent().parent();
	nRow.find("td").each(function(index){
		switch(index){
		case 0:
			break;
		case 1:
			sourcetypearr.attr("value",$("#select1").val());
			sourcetypearr.attr("name","sourcetypearr");
			if($("#select1").val()=="0"){
			sourcevaluearr.attr("value","");
			}else{
			sourcevaluearr.attr("value",$("#input1").val());
			}
			sourcevaluearr.attr("name","sourcevaluearr");
			$(this).width(230);
			if($("#select1").val()=="0"){
//				$(this).html($("#select1").val());
				$(this).html("Any");
			}else if($("#select1").val()=="3"){
				$(this).html("Interface("+$("#input1").find("option:selected").text()+")");
			}else if($("#select1").val()=="1"){
				$(this).html("IP Range("+$("#input1").val()+")");
			}else if($("#select1").val()=="2"){
				$(this).html("Network("+$("#input1").val()+")");
			}else if($("#select1").val()=="4"){
				$(this).html("User Profile("+$("#input1").val()+")");
			}
			break;
		case 2:
			destinationtypearr.attr("value",$("#select2").val());
			destinationtypearr.attr("name","destinationtypearr");
			if($("#select2").val()=="0"){
				destinationvaluearr.attr("value","");
			}else if($("#select2").val()=="4"){
				destinationvaluearr.attr("value","");
			}else{
				destinationvaluearr.attr("value",$("#input2").val());
			}
			destinationvaluearr.attr("name","destinationvaluearr");
			$(this).width(230);
			if($("#select2").val()=="0")
			{
				$(this).html("Any");
			}else if($("#select2").val()=="4")
			{
				$(this).html("Private");
			}else if($("#select2").val()=="1")
			{
				$(this).html("IP Range("+$("#input2").val()+")");
			}else if($("#select2").val()=="2")
			{
				$(this).html("Network("+$("#input2").val()+")");
			}else if($("#select2").val()=="3")
			{
				$(this).html("Hostname("+$("#input2").val()+")");
			}
			break;
		
		case 3:
			
			if($(this).children().children("select").val()!=null&& $(this).children().children("select").val()!=""){
			out1arr.attr("value",$(this).children().children("select").val());
			out1arr.attr("name","out1arr");
			var temp=$(this).find("option:selected").text();
			if(temp=="Corporate Network (VPN)"){
					$(this).width(160);
				}
			$(this).html($(this).find("option:selected").text());
			}else{
				out1arr.attr("value","-");
				out1arr.attr("name","out1arr");
				$(this).html("-");
			}
			break;
		case 4:
			if($(this).children().children("select").val()!=null&& $(this).children().children("select").val()!=""&& !$(this).children("select").attr("disabled")){
			out2arr.attr("value",$(this).children().children("select").val());
			out2arr.attr("name","out2arr");
			var temp=$(this).find("option:selected").text();
			if(temp=="Corporate Network (VPN)"){
				$(this).width(160);
				}
			$(this).html($(this).find("option:selected").text());
			}else{
				out2arr.attr("value","-");
				out2arr.attr("name","out2arr");
				$(this).html("-");
			}
			
			break;
	
		case 5:
			$(this).html(sourcetypearr);
			sourcetypearr.after(sourcevaluearr);
			sourcevaluearr.after(destinationtypearr);
			destinationtypearr.after(destinationvaluearr);
			destinationvaluearr.after(out1arr);
			out1arr.after(out2arr);
			break;
		case 6:
			$(this).html($('<img class="dinl" src="<s:url value="/images/new.png" />" width="16" onclick="addTableRow(this);" height="16"  style="cursor: pointer" alt="new" title="New" /><img class="dinl"src="<s:url value="/images/modify.png" />" width="16" onclick="editRow(this);" style="cursor: pointer" height="16" alt="modify" title="Modify" /><img class="dinl" src="<s:url value="/images/trash.png" />" width="16" height="16" style="cursor: pointer"  onclick="delcheckTableRow(this);" alt="remove" title="Remove" />'));
			break;
		default:
			break;
		}
		
	});
	 $("#customRuleTable").tableDnD();  
}



function selector(id,selectvalue)
{
	for(var i=0;i<$("#"+id).children().length;i++)
	{
		
		if($("#"+id).children().eq(i).val()==selectvalue)
		{
			$("#"+id).children().eq(i).attr({selected:true});
		}
		
	}
}
function destroyer(firstid,secid){
	 var tempOut1Value=$("#"+firstid).find("option:selected").val();
	 if(tempOut1Value!=null&&tempOut1Value!=""){
	   $("#"+secid).find("option").each(function(){
			if($(this).val()==tempOut1Value)
			{
					$(this).remove();
					
			}
		});
	 }
}

function checkhassave()
{
	
	var editid=$("#customRuleTable img[id=tempsave]").attr("id");
	
	if(editid!=null&&editid!="")
	{
		showWarnDialog('<s:text name ="warn.pbr.save.message" />');
		return false;

	}
	
	return true;
}
var editObject;
var tep;
var tep2;
var tep3;
var tep4;
function editRow(obj)
{
	if(!checkhassave()){
		return false;
	}
	var nRow =$(obj).parent().parent();
	editObject=nRow.clone();
	var selectedValue;
	nRow.find("td").each(function(index){
		switch(index){
		case 0:
			break;
		case 1:
			
			tep=$(this).html().trim();
			var context1=$("#sourcediv1").clone();
			var context2=$("#sourcediv2").clone();
			$(this).html(context1.append(context2));
			context1.attr("id","select1div1");
			context2.attr("id","select1div2");
			context1.children("select").attr("id","select1");
			context2.children().attr("id","input1");
			
			if(tep!="Any"){
				var str=tep.split("(");
				if(str[0]=="Interface"){
					selectedValue=str[1].substring(0,str[1].length-1);
					var contextselect=$("#templatesourvevalueselect").clone();
					contextselect.attr("id","input1");
					$("#select1div2").html(contextselect);
					$("#input1").before('<s:text name="Value"/>&nbsp;');
					selector("input1",selectedValue);
					selector("select1","3");
				}else if(str[0]=="User Profile"){
					var tempSelectedValue=str[1].substring(0,str[1].length-1);
					var contextselect4=$("#templatesourveprofileselect").clone();
					contextselect4.attr("id","input1");
					$("#select1div2").html(contextselect4);
					$("#input1").before('<s:text name="Value"/>&nbsp;');	
					$("#input1").append('<option value="-------" disabled="true" >------------------</option>');
					$("#input1").append('<option value="Any Guest">Any Guest</option>');
					selector("input1",tempSelectedValue);
					selector("select1","4");
				}else {
					if(str[0]=="IP Range"){
						selector("select1","1");
					}
					if(str[0]=="Network"){
						selector("select1","2");
					}
					if(str[0]=="User Profile"){
						selector("select1","3")
					}
					$("#input1").attr("value",str[1].substr(0,str[1].length-1));
				}
				if(str[0]!="Any"){
					$("#select1div2").attr("style","padding-top: 5px;display:");
				}
				selector("select1",str[0]);
			}
			break;
		case 2:
			tep2=$(this).html().trim();
			var context3=$("#destinationdiv1").clone();
			var context4=$("#destinationdiv2").clone();
			$(this).html(context3.append(context4));
			context3.attr("id","select2div1");
			context4.attr("id","select2div2");
			context3.children("select").attr("id","select2");
			context4.children().attr("id","input2");
			if(tep2!="Any"&& tep2!="Private"){
				var tep2_str=tep2.split("(");
				if(tep2_str[0]=="IP Range"){
					selector("select2","1");
				}
				if(tep2_str[0]=="Network"){
					selector("select2","2");
				}
				if(tep2_str[0]=="Hostname"){
					selector("select2","3");
				}
				selector("select2",tep2_str[0]);
	 			$("#input2").attr("value",tep2_str[1].substr(0,tep2_str[1].length-1));
				if(tep2_str[0]!="Any"){
					$("#select2div2").attr("style","padding-top: 5px;display:");
				}
			}else if(tep2=="Private"){
				selector("select2","4");
			}else if(tep2=="Any"){
				selector("select2","0")
			}
			break;
		case 3:
			tep3=$(this).html().trim();
			var context5=$("#templeprimaryoutselect").clone();
			context5.attr("id","select3");
			$(this).html($('<div ></div>').html(context5));
			selector("select3",tep3);
			if(selectedValue!=null||selectedValue!=""){
				preventSame("select3",selectedValue);
			}
			break;
		case 4:
			tep4=$(this).html().trim();
			var context6=$("#templeout2select").clone();
			context6.attr("id","select4");
			$(this).html($('<div ></div>').html(context6));	
			if(tep3=="Drop"){
				$("#select4").val("");
				$("#select4").attr("disabled","true");
			}else{
				$("#select4").attr({disabled:false});
				selector("select4",tep4);
			}
			if(selectedValue!=null||selectedValue!=""){
				preventSame("select4",selectedValue);
			}
			preventSame("select4",tep3);
			break;
		case 5:
			break;	
		case 6:
			$(this).html($('<img id="tempsave" class="dinl" onclick="saveRow(this);" style="cursor: pointer" src="<s:url value="/images/save.png" />" width="16" height="16" alt="save" title="Save" /> <img class="dinl" id="tempdelete" onclick="cancelWhenEditting(this);" style="cursor: pointer"	src="<s:url value="/images/cancel.png" />" width="16" alt="delete" title="Cancel" />'));
			break;
		default:
		break;
		}
	});
}

function cancelWhenEditting(obj){
	var sourcetypearr=$("#sourcetypearrhide").clone();
	var sourcevaluearr=$("#sourcevaluearrhide").clone();
	var destinationtypearr=$("#destinationtypearrhide").clone();
	var destinationvaluearr=$("#destinationvaluearrhide").clone();
	var out1arr=$("#out1arrhide").clone();
	var out2arr=$("#out2arrhide").clone();
	var nRow =$(obj).parent().parent();
	nRow.find("td").each(function(index){
		switch(index){
		case 0:
			break;
		case 1:
			if(tep=="Any"){
				sourcetypearr.attr("value","0");
				sourcevaluearr.attr("value","");
			}else{
				var str=tep.split("(");
				var sourcetype = str[0];
				var sourcevalue = str[1].substring(0,str[1].length-1);
				 if(sourcetype=="IP Range"){
					sourcetypearr.attr("value","1");
					sourcevaluearr.attr("value",sourcevalue);
				}else if(sourcetype=="Interface"){
					sourcetypearr.attr("value","3");
					sourcevaluearr.attr("value",sourcevalue);
				}else if(sourcetype == "Network"){
					sourcetypearr.attr("value","2");
					sourcevaluearr.attr("value",sourcevalue);
				}else if(sourcetype=="User Profile"){
					sourcetypearr.attr("value","4");
					sourcevaluearr.attr("value",sourcevalue);
				}
			}
			sourcetypearr.attr("name","sourcetypearr");
			sourcevaluearr.attr("name","sourcevaluearr");
			$(this).width(230);
			$(this).html(tep);
			break;
		case 2:
			if(tep2=="Any"){
				destinationtypearr.attr("value","0");
				destinationvaluearr.attr("value","");
			}else if(tep2=="Private"){
				destinationtypearr.attr("value","4");
				destinationvaluearr.attr("value","");
			}else {
				var str=tep2.split("(");
				var destinationtype = str[0];
				var destinationvalue = str[1].substring(0,str[1].length-1);
				if(destinationtype=="IP Range"){
					destinationtypearr.attr("value","1");
					destinationvaluearr.attr("value",destinationvalue);
				}else if(destinationtype == "Hostname"){
					destinationtypearr.attr("value","3");
					destinationvaluearr.attr("value",destinationvalue);
				}else if(destinationtype == "Network"){
					destinationtypearr.attr("value","2");
					destinationvaluearr.attr("value",destinationvalue);
				}
			}
			destinationtypearr.attr("name","destinationtypearr");
			destinationvaluearr.attr("name","destinationvaluearr");
			$(this).width(230);
			$(this).html(tep2);
			break;
		
		case 3:
			out1arr.attr("value",tep3);
			out1arr.attr("name","out1arr");
			$(this).html(tep3);
			break;
		case 4:
			out2arr.attr("value",tep4);
			out2arr.attr("name","out2arr");
			$(this).html(tep4);
			break;
		case 5:
			$(this).html(sourcetypearr);
			sourcetypearr.after(sourcevaluearr);
			sourcevaluearr.after(destinationtypearr);
			destinationtypearr.after(destinationvaluearr);
			destinationvaluearr.after(out1arr);
			out1arr.after(out2arr);
			break;
		case 6:
			$(this).html($('<img class="dinl" src="<s:url value="/images/new.png" />" width="16" onclick="addTableRow(this);" height="16"  style="cursor: pointer" alt="new" title="New" /><img class="dinl"src="<s:url value="/images/modify.png" />" width="16" onclick="editRow(this);" style="cursor: pointer" height="16" alt="modify" title="Modify" /><img class="dinl" src="<s:url value="/images/trash.png" />" width="16" height="16" style="cursor: pointer"  onclick="delcheckTableRow(this);" alt="remove" title="Remove" />'));
			break;
		default:
			break;
		}
		
	});
	 $("#customRuleTable").tableDnD(); 
     
}

	
function preventSame(id,value){
	$("#"+id).find("option").each(function(){
		if($(this).val()==value){
			$(this).remove();
		}
	});
}

function cancelEdit(obj){
	var temtr=$(obj).parent().parent();
	temtr.find("td").each(function(index){
		switch(index){
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			$(this).html(editObject.children("td").eq(index).html().trim());
			break;
		default:
		break;
		}
	});
	$("tr[name='errormessagetr']").remove();
}

function doContinueOper() {
	if(deleteObject==""||deleteObject==null){
		$("table[id=customRuleTable] input[name=ruleIndices]:checkbox").each(function(){
			if($(this).attr("checked")=="checked"){
				var tr=$(this).parent().parent();
				tr.remove();
			}
		});
	}else{
		var tr=$(deleteObject).parent().parent();
		tr.remove();
	}
}

function checkprofileType(){
	
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_CUSTOM).checked){

		$("table[id=splitTunelTable] input[name=ruleIndices]:checkbox").each(function(){
				var tr=$(this).parent().parent();
				tr.remove();
		});
	}else {
		$("table[id=customRuleTable] input[name=ruleIndices]:checkbox").each(function(){
			var tr=$(this).parent().parent();
			tr.remove();
		});
	}
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_SPLIT).checked){
		$("#splitout1").attr("value",$("#splitTunnelPrimary").val());
		$("#splitout2").attr("value",$("#splitTunnelBackup").val());
	}
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_ALL).checked){
		var tr=$("#tunelTd3").remove();
	}


}
function checkSplitTunnel(){
	var tempSplitForwardValue=$("#splitTunnelPrimary").find("option:selected").val();
	if(tempSplitForwardValue==null||tempSplitForwardValue==""){
		$("tr[name='errormessagetr']").remove();
		hm.util.reportTableSelectError(document.getElementById("splitTunnelPrimary"), '<s:text name="config.routing.policy.rules.forwarding.warning" />');
		 document.getElementById("splitTunnelPrimary").focus();
    	return false;
	}
	return true;
}
function submitAction(operation) {
	if(operation == 'cancel'|| operation == 'cancelhiveAp'){
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();	
	    return false;
	}
	if(!checkhassave()){
		return false;
	}
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_SPLIT).checked==true){
		if(!checkSplitTunnel()){
			return false;
		}
	}
	
	checkprofileType();
	
	if(validateProfileName()){
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();		
	}
	 
	    

}
 

function toggleCheckAllRules(chkEl) {
	
	$("input[name=ruleIndices]").attr("checked", chkEl.checked);
}


function addTableRow(obj) {
	if(checkhassave()){
	var tr=$(obj).parent().parent();	
	var cum=getExsitRowCounts()+1;
	var rowclone=$("#templetr").clone();
	var row=rowclone.attr("id","routingPolicyPriRow_"+cum);
	
	row.find("td").each(function(index){
		switch(index){
		case 0:
			break;
		case 1:
			var context1=$("#sourcediv1").clone();
			var context2=$("#sourcediv2").clone();
			$(this).html(context1.append(context2));
			context1.attr("id","select1div1");
			context2.attr("id","select1div2");
			context1.children("select").attr("id","select1");
			context2.children().attr("id","input1"); 
	
			break;
		
		case 2:
			var context3=$("#destinationdiv1").clone();
			var context4=$("#destinationdiv2").clone();
			$(this).html(context3.append(context4));
			context3.attr("id","select2div1");
			context4.attr("id","select2div2");
			context3.children("select").attr("id","select2");
			context4.children().attr("id","input2");
	
			break;
		
		case 3:
			var context5=$("#templeprimaryoutselect").clone();
			context5.attr("id","select3");
			$(this).html($("<div ></div>").html(context5));
			
			break;
		case 4:
			var context6=$("#templeout2select").clone();
			context6.attr("id","select4");

			$(this).html($("<div ></div>").html(context6));
		
			break;
		
		case 5:
			break;	
		case 6:
			$(this).html($('<img id="tempsave" class="dinl" onclick="saveRow(this);" style="cursor: pointer" src="<s:url value="/images/save.png" />" width="16" height="16" alt="save" title="Save" /> <img class="dinl" id="tempdelete" onclick="delEditingTableRow(this);" style="cursor: pointer"	src="<s:url value="/images/cancel.png" />" width="16"  title="Cancel"/>') );
			break;
		default:
		break;
		}
	});
	if(tr.attr("id").substr(20)==0){
		 $("#routingPolicyPriRow_0").after(row);
	}
	else
	{
		$("#customRuleTable").find("tr").each(function(index){
			if($(this).attr("id")==tr.attr("id")){
				tr.after(row);
			}
			
		});
	}
	
	}
}
function checkIpStr(ipStr) 
{ 
	if(ipStr == "") 
	{ 
	return false; 
	} 
	if(ipStr.match(/[\u4E00-\u9FA5]/)!=null) 
	{ 
	return false; 
	} 
	if(ipStr.length>15) 
	{ 
	return false; 
	} 
	if(ipStr.length<7) 
	{ 
	return false; 
	} 
	if(ipStr.indexOf(" ")!=-1) 
	{ 
	return false; 
	} 
	var ipDomainPat=/^(\d{1,3})[.](\d{1,3})[.](\d{1,3})[.](\d{1,3})$/; 
	var IPArray = ipStr.match(ipDomainPat); 
	 if (IPArray != null)
	 { 
		for (var i = 1; i <= 4; i++) 
		{ 
		       if (i == 1) 
		       { 
		           if (IPArray[i] == 0 || IPArray[i] > 239) 
					{ 
		               return false; 
		            }       
		       }       
		       else 
			   { 
					if(IPArray[i] > 255) 
					{ 
					return false; 
					} 
				} 
        } 
		        return true; 
    } 
		
		return false; 
} 

function isEndIpLargeThanStartIp(beginIpValue, endIpValue) 
{ 
	var beginArry = beginIpValue.split("."); 
	var endArry  =  endIpValue.split("."); 
	
	for(var i=0;i<beginArry.length;i++) 
	{     
		var currValue_begin =parseInt(beginArry[i],10); 
		var currValue_end = parseInt(endArry[i],10); 
		
 		if(currValue_begin==currValue_end) 
 		{ 
 			if(i == beginArry.length-1){
 				return true;
 			}
 		continue; 
 		} 
		if(currValue_end>currValue_begin) 
		{ 
		return true; 
		} 
		else{
			return false;
		}
	} 
	    return false; 
} 
function validateInput2()
{
	if($("#select1").val()=="1")
	{
			if($("#input1").val().indexOf("-")>=0){
				var arr=$("#input1").val().split("-");
				if(!(checkIpStr(arr[0].trim())&&checkIpStr(arr[1].trim())&&isEndIpLargeThanStartIp(arr[0].trim(),arr[1].trim()))){
					$("tr[name='errormessagetr']").remove();
					hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.iprangewarning" />');
					return false;
				}
			}
			else
			{
				$("tr[name='errormessagetr']").remove();
				 hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.iprangewarning" />');
				return false;
			}
	}
	else if($("#select1").val()=="2")
	{
				if($("#input1").val().indexOf("/")>=0)
				{
					
					var arr2=$("#input1").val().split("/");
						if(!checkIpStr(arr2[0]))
						{
							$("tr[name='errormessagetr']").remove();
							 hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.networkwarning" />');
							 document.getElementById("input1").focus();
							 return false;
						return false;
						}
						var netmask=/^(\d{1,2})$/; 
						var reg = new RegExp(netmask);
						if(parseInt(arr2[1])< 4||parseInt(arr2[1])>32||!reg.test(arr2[1]))
						{
							$("tr[name='errormessagetr']").remove();
							 hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.network.netmask.warning" />');
							 document.getElementById("input1").focus();
							 return false;
						}
						if(arr2[2]!=null&&arr2[2]!=""){
							$("tr[name='errormessagetr']").remove();
							 hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.networkwarning" />');
							 document.getElementById("input1").focus();
							 return false;
						}
					
				}
				else
				{
					$("tr[name='errormessagetr']").remove();
					 hm.util.reporttableFieldError(document.getElementById("input1"),'<s:text name="config.routing.policy.rules.sourcevalue.networkwarning" />');
					 document.getElementById("input1").focus();
					return false;
				}
				
	}
	return true;
}

function validateInput4()
{
	if($("#select2").val()=="1")
	{
			if($("#input2").val().indexOf("-")>=0)
			{
				var arr=$("#input2").val().split("-");
				if(!(checkIpStr(arr[0].trim())&&checkIpStr(arr[1].trim())&&isEndIpLargeThanStartIp(arr[0].trim(),arr[1].trim())))
				{
					$("tr[name='errormessagetr']").remove();
					 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.iprangewarning" />');
					return false;
				}
				if(arr[2]!=""&& arr[2]!=null)
				{
					$("tr[name='errormessagetr']").remove();
					 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.iprangewarning" />');
						return false;
				}
			}
			else
			{
				$("tr[name='errormessagetr']").remove();
				 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.iprangewarning" />');
				return false;
			}
	}
	else if($("#select2").val()=="2")
	{
		
		if($("#input2").val().indexOf("/")>=0){
			
			var arr2=$("#input2").val().split("/");
				if(!checkIpStr(arr2[0]))
				{
					$("tr[name='errormessagetr']").remove();
					 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.networkwarning" />');
					 document.getElementById("input2").focus();
					 return false;
				return false;
				}
				var netmask=/^(\d{1,2})$/; 
				var reg = new RegExp(netmask);
				if(parseInt(arr2[1])<4||parseInt(arr2[1])>32||!reg.test(arr2[1]))
				{
					$("tr[name='errormessagetr']").remove();
					 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.network.netmask.warning" />');
					 document.getElementById("input2").focus();
					 return false;
				}
			
		}
		else
		{
			$("tr[name='errormessagetr']").remove();
			 hm.util.reporttableFieldError(document.getElementById("input2"),'<s:text name="config.routing.policy.rules.destinationvalue.networkwarning" />');
			 document.getElementById("input2").focus();
			return false;
		}
		
	}
	else if($("#select2").val()=="3")
	{
		var leg=document.getElementById("input2").value;
		var message = hm.util.validateName(leg, '<s:text name="config.routing.policy.rules.destinationvalue" />');
    	if(leg.length>32){
    		message='<s:text name="config.routing.policy.rules.destinationvalue.hostname" />';
    	}
		if (message != null) 
    	{
    		$("tr[name='errormessagetr']").remove();
    		hm.util.reporttableFieldError(document.getElementById("input2"), message);
    		 document.getElementById("input2").focus();
        	return false;
    	}
		
	}
  	return true;
}
function validateForwardingAction(){
	var tempForwardValue=$("#select3").val();
	if(tempForwardValue==null||tempForwardValue==""){
		$("tr[name='errormessagetr']").remove();
		hm.util.reportTableSelectError(document.getElementById("select3"), '<s:text name="config.routing.policy.rules.forwarding.warning" />');
		 document.getElementById("select3").focus();
    	return false;
	}
	return true;
}
function validateBackupForwardingAction(){
	var tempBackForwardValue=$("#select4").find("option:selected").val();
	if(tempBackForwardValue==null||tempBackForwardValue==""){
		$("tr[name='errormessagetr']").remove();
		hm.util.reporttableFieldError(document.getElementById("select4"), '<s:text name="config.routing.policy.rules.backup.forwarding.warning" />');
		 document.getElementById("select4").focus();
    	return false;
	}
	return true;
}
function sourcetypechange(obj){
	
	if($(obj).val()!="0"){
		$("#select1div2").attr("style","padding-top: 5px;display:block");
		
	}else{
		$("#select1div2").attr("style","padding-top: 5px;display:none");
	}
	$("#select1").find("option").each(function(index){
		if($("#select1").val()==$(this).val()){
			switch(index){
			case 0:
/* 				addoptions("select3");
				addoptions("select4"); */
				break;
			case 1:
/* 				addoptions("select3");
				addoptions("select4"); */
				$("#select1div2").html('<s:text name="Value"/>&nbsp;'+'<s:textfield id="input1"  value="" style="width: 188px" />');
				break;
			case 2:
/* 				addoptions("select3");
				addoptions("select4"); */
				$("#select1div2").html('<s:text name="Value"/>&nbsp;'+'<s:textfield id="input1"  value="" style="width: 188px" />');
				break;
			case 3:
				var contextselect=$("#templatesourvevalueselect").clone();
				contextselect.attr("id","input1");
				$("#select1div2").html(contextselect);
				$("#input1").before('<s:text name="Value"/>&nbsp;');
				sourcevaluechange();
				break;
			case 4:
/* 				addoptions("select3");
				addoptions("select4"); */
				if(tempCount==1){
					$("#templatesourveprofileselect").append('<option value="-------" disabled="true" >------------------</option>');
					$("#templatesourveprofileselect").append('<option value="Any Guest">Any Guest</option>');
				}
				tempCount++;
				var contextselect4=$("#templatesourveprofileselect").clone();
				contextselect4.attr("id","input1");
				$("#select1div2").html(contextselect4);
				$("#input1").before('<s:text name="Value"/>&nbsp;');	
				
				break;
				default:
				break;
			}
		} 
	});
}
function destinationvaluechange(obj){
	
	if($(obj).val()!="0"&& $(obj).val()!="4"){
		$("#select2div2").attr("style","padding-top: 5px;display:block");
		$("#input2").val("");
		
	}else{
		$("#select2div2").attr("style","padding-top: 5px;display:none");
		$("#input2").val("");
	}
}


function addoptions(id){
	$("#"+id).children().remove();
	$("#"+id).append('<option value=""></option>');
	$("#"+id).append('<option value="Primary WAN">Primary WAN</option>');
	$("#"+id).append('<option value="Backup WAN-1">Backup WAN-1</option>');
	$("#"+id).append('<option value="Backup WAN-2">Backup WAN-2</option>');
	$("#"+id).append('<option value="Corporate Network (VPN)"  >Corporate Network (VPN)</option>');
	$("#"+id).append('<option value="Drop" >Drop</option>');
	$("#"+id).append('<option value="-------------------------" disabled="true"  style="display:none">-------------------------</option>');
	$("#"+id).append('<option value="USB" style="display:none">USB</option>');
	$("#"+id).append('<option value="Wifi0" style="display:none">Wifi0</option>');
	$("#"+id).append('<option value="Eth0" style="display:none">Eth0</option>');
	$("#"+id).append('<option value="Eth1" style="display:none">Eth1</option>');
	$("#"+id).append('<option value="Eth2" style="display:none">Eth2</option>');
	$("#"+id).append('<option value="Eth3" style="display:none">Eth3</option>');
	$("#"+id).append('<option value="Eth4" style="display:none">Eth4</option>');
	$("#"+id).append('<option value="Eth5" style="display:none">Eth5</option>');
	$("#"+id).append('<option value="Eth6" style="display:none">Eth6</option>');
	$("#"+id).append('<option value="Eth7" style="display:none">Eth7</option>');
	$("#"+id).append('<option value="Eth8" style="display:none">Eth8</option>');
	$("#"+id).append('<option value="Eth9" style="display:none">Eth9</option>');
	$("#"+id).append('<option value="Eth10" style="display:none">Eth10</option>');
	$("#"+id).append('<option value="Eth11" style="display:none">Eth11</option>');
	$("#"+id).append('<option value="Eth12" style="display:none">Eth12</option>');
	$("#"+id).append('<option value="Eth13" style="display:none">Eth13</option>');
	$("#"+id).append('<option value="Eth14" style="display:none">Eth14</option>');
	$("#"+id).append('<option value="Eth15" style="display:none">Eth15</option>');
	$("#"+id).append('<option value="Eth16" style="display:none">Eth16</option>');
	$("#"+id).append('<option value="Eth17" style="display:none">Eth17</option>');
	$("#"+id).append('<option value="Eth18" style="display:none">Eth18</option>');
	$("#"+id).append('<option value="Eth19" style="display:none">Eth19</option>');
	$("#"+id).append('<option value="Eth20" style="display:none">Eth20</option>');
	$("#"+id).append('<option value="Eth21" style="display:none">Eth21</option>');
	$("#"+id).append('<option value="Eth22" style="display:none">Eth22</option>');
	$("#"+id).append('<option value="Eth23" style="display:none">Eth23</option>');
	$("#"+id).append('<option value="Eth24" style="display:none">Eth24</option>');
	$("#"+id).append('<option value="Eth25" style="display:none">Eth25</option>');
	$("#"+id).append('<option value="Eth26" style="display:none">Eth26</option>');
	$("#"+id).append('<option value="Eth27" style="display:none">Eth27</option>');
	$("#"+id).append('<option value="Eth28" style="display:none">Eth28</option>');
}


 



function getExsitRowCounts(){
	return $("table[id=customRuleTable] input[name=ruleIndices]:checkbox").length;
}

function insertPageContext() {
	<s:if test="%{lstTitle!=null && lstTitle.size>1}">
		document.writeln('<td class="crumb" nowrap>');
		<s:iterator value="lstTitle">
			document.writeln(" <s:property/> ");
		</s:iterator>
		document.writeln('</td>');
	</s:if>
	<s:else>
		document.writeln('<td class="crumb" nowrap><a href="<s:url action="routingProfilePolicy" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
		<s:if test="%{dataSource.id == null}">
			document.writeln('New </td>');
		</s:if>
		<s:else>
			document.writeln('Edit \'<s:property value="changedName" />\'</td>');
		</s:else>
	</s:else>	
}
function validateProfileName(){
	var text=document.getElementById("profileName");
	var message=hm.util.validateNameWithBlanks(text.value,'<s:text name="config.routing.pbr.name" />'); 
	if (message != null) {
	hm.util.reportFieldError(text, message);
	text.focus();
	return false;
	}
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_SPLIT).checked==true){
		if(!checkSplitTunnel()){
			return false;
		}
	}
	return true;
}

function sourcevaluechange(){
	var temp3=$("#select3").find("option:selected").text();
	var select3key=$("#select3").val();
	addoptions("select3");
	$("#select3").find("option").each(function(){
		
		if($(this).text()==$("#input1").val() && $("#select1").val()=="3")
		{
			$(this).remove();
		}
		
	});
	if($("#input1").val()!=temp3){
		selector("select3",select3key);
	}
	var temp4=$("#select4").find("option:selected").text();
	var select4key=$("#select4").val();
	addoptions("select4");
	$("#select4").find("option").each(function(){
		if($(this).text()==$("#input1").val() && $("#select1").val()=="3")
		{
			$(this).remove();
			
		}
	});
	if($("#input1").val()!=temp4){
		selector("select4",select4key);
	}
	if(select3key!=null&&select3key!=""){
		$("#select4").find("option").each(function(){
			if($(this).val()==select3key){
				$(this).remove();
			}
		});
	}
}
function primarychange(){
	var select4tep=$("#select4").val();
	addoptions("select4");
	$("#select4").find("option").each(function(){
		if(($(this).val()==$("#select3").val()&& $("#select3").val()!="" )||( $(this).text()==$("#input1").val() && $("#select1").val()=="Interface"))
		{
			$(this).remove();
			
		}
	});
	if(select4tep!=$("#select3").val()){
		selector("select4",select4tep);
	}
	var tempBackupSelectedValue=$("#select4").find("option:selected").val();
	var tempPrimarySelectedValue=$("#select3").find("option:selected").val();
	if(tempPrimarySelectedValue=="Drop"){
		$("#select4").val("");
		$("#select4").attr({disabled:true});
	}else{
		$("#select4").attr({disabled:false});
/* 		if(tempBackupSelectedValue!=null||tempBackupSelectedValue!=""){
			if(tempPrimarySelectedValue!=tempBackupSelectedValue){ */
				$("#select4").find("option").each(function (){
					if($(this).val()==tempBackupSelectedValue){
						$(this).attr({selected:true});
					}
				});
/* 			}
		} */
	}
	if($("#select1").val()=="3" && $("#input1").val()!=""){
		$("#select4").find("option").each(function (){
			if($(this).val() == $("#input1").val()){
				$(this).remove();
			}
		});
	}
	
}
function primaryChangeSplit(){
	var primarySelectedValue=$("#splitTunnelPrimary").val();
	var tempBackupSelected=$("#splitTunnelBackup").find("option:selected").val();
	addoptions("splitTunnelBackup");
	$("#splitTunnelBackup").find("option").each(function (){
		if($(this).val()==primarySelectedValue && primarySelectedValue!=""){
			$(this).remove();
		}
	});
	
	if(primarySelectedValue=="Drop")
	{
		$("#splitTunnelBackup").val("");
		$("#splitTunnelBackup").attr({disabled:true});
		
	} else
	{
		$("#splitTunnelBackup").attr({disabled:false});
		$("#splitTunnelBackup").find("option").each(function (){
			if($(this).val()==tempBackupSelected){
				$(this).attr({selected:true});
			}
		});
	} 
}
function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if(!checkhassave()){
		return false;
	}
	if(Get(formName + "_dataSource_profileType"+POLICYRULE_SPLIT).checked==true){
		if(!checkSplitTunnel()){
			return false;
		}
	}
	
	checkprofileType();
	
	if (validateProfileName()) {
		var url =  "<s:url action='routingProfilePolicy' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["routingProfilePolicy"].operation.value = operation;
	//	argsEnableBeforeSubmit();
		YAHOO.util.Connect.setForm(document.getElementById("routingProfilePolicy"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succSaveRoutingPolicy, failure : resultDoNothing, timeout: 60000}, null);
	//	argsDisableBeforeSubmit();
	}
}
var resultDoNothing = function(o) {
	//alert("failed.");
};

var succSaveRoutingPolicy = function (o) {
	eval("var details = " + o.responseText);
	if (details.t) {
		if (details.n){
			hm.util.insertSelectValue(details.nId, details.nName, parent.Get(details.pId), true, true);
		}
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}

var tempChoice=true;
function changeProfileType(obj){
		if($(obj).val()==POLICYRULE_SPLIT){
			if(tempChoice){
				if(checkhassave()){
					$("#customRuleTable").hide();
					$("#splitTunelTable").show();
					$("#splitTunnelNotes").show();
					$("#TunnelAllNotes").hide();
					$("#CustomNotes").hide();
					
					$("#splitTunneldestination1").show();
					$("#TunnelAlldestination1").hide();
					$("#splitTunneldestination2").show();
					$("#TunnelAlldestination2").hide();
					$("#splitTunnelTr").show();
					$("#tunelTd1").hide();
					$("#tunelTd2").hide();
					$("#tunelTd3").hide();
					
 					$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
					$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
					$("#tunelTd3").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" id="splitout1" value="" /><s:hidden name="out2arr" id="splitout2" value="" />	');		
				}else{
					$("#routingProfilePolicy_dataSource_profileType1").attr("checked",false);
					$("#routingProfilePolicy_dataSource_profileType2").attr("checked",false);
					$("#routingProfilePolicy_dataSource_profileType3").attr("checked",true);
					return false;
				}
			}else{
				
			$("#customRuleTable").hide();
			$("#splitTunelTable").show();
			$("#splitTunnelNotes").show();
			$("#TunnelAllNotes").hide();
			$("#CustomNotes").hide();
			
			$("#splitTunneldestination1").show();
			$("#TunnelAlldestination1").hide();
			$("#splitTunneldestination2").show();
			$("#TunnelAlldestination2").hide();
			$("#splitTunnelTr").show();
			$("#tunelTd1").hide();
			$("#tunelTd2").hide();
			$("#tunelTd3").hide();

		$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
		$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="4" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
		$("#tunelTd3").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" id="splitout1" value="" /><s:hidden name="out2arr" id="splitout2" value="" />	');		
			}
		
		}else if($(obj).val()==POLICYRULE_ALL){
			if(tempChoice){
				if(checkhassave()){
					$("#customRuleTable").hide();
					$("#splitTunelTable").show();
					$("#splitTunnelNotes").hide();
					$("#TunnelAllNotes").show();
					$("#CustomNotes").hide();
					$("#splitTunneldestination1").hide();
					$("#TunnelAlldestination1").show();
					$("#splitTunneldestination2").hide();
					$("#TunnelAlldestination2").show();
					$("#splitTunnelTr").hide();
					$("#tunelTd1").hide();
					$("#tunelTd2").hide();
					
					$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="-" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
					$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
					$("#tunelTd3").html('');
				}else{
					$("#routingProfilePolicy_dataSource_profileType1").attr("checked",false);
					$("#routingProfilePolicy_dataSource_profileType2").attr("checked",false);
					$("#routingProfilePolicy_dataSource_profileType3").attr("checked",true);
					return false;
				}
			}else {
			$("#customRuleTable").hide();
			$("#splitTunelTable").show();
			$("#splitTunnelNotes").hide();
			$("#TunnelAllNotes").show();
			$("#CustomNotes").hide();
			$("#splitTunneldestination1").hide();
			$("#TunnelAlldestination1").show();
			$("#splitTunneldestination2").hide();
			$("#TunnelAlldestination2").show();
			$("#splitTunnelTr").hide();
			$("#tunelTd1").hide();
			$("#tunelTd2").hide();
			$("#tunelTd1").html('<s:hidden name="sourcetypearr" value="4" /><s:hidden name="sourcevaluearr" value="Any Guest" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="-" /><s:hidden name="out1arr" value="Drop" /><s:hidden name="out2arr" value="-" />');	
			$("#tunelTd2").html('<s:hidden name="sourcetypearr" value="0" /><s:hidden name="sourcevaluearr" value="" /><s:hidden name="destinationtypearr" value="0" /><s:hidden name="destinationvaluearr" value="" /><s:hidden name="out1arr" value="Corporate Network (VPN)" /><s:hidden name="out2arr" value="-" />');	
			$("#tunelTd3").html('');	
			}
		}else if($(obj).val()==POLICYRULE_CUSTOM){
//			tempChoice=true;
			$("#customRuleTable").show();
			$("#splitTunelTable").hide();
			$("#splitTunnelNotes").hide();
			$("#TunnelAllNotes").hide();
			$("#CustomNotes").show();
			}
			
} 

</script>



<div id="content">
	<s:form action="routingProfilePolicy" name="routingProfilePolicy"
		id="routingProfilePolicy">
		<s:if test="%{jsonMode==true}">
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<s:hidden name="id" />
			<s:hidden name="parentDomID" />
			<s:hidden name="contentShowType" />
			<s:hidden name="parentIframeOpenFlg" />
		</s:if>
		<s:if test="%{jsonMode==false}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><tiles:insertDefinition name="context" /></td>
				</tr>
				<tr>
					<td class="buttons">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<s:if test="%{dataSource.id == null}">
									<td><input type="button" name="ignore"
										value="<s:text name="button.create"/>" class="button"
										onClick="submitAction('create<s:property value="lstForward"/>');"
										<s:property value="writeDisabled" />></td>
								</s:if>
								<s:else>
									<td><input type="button" name="ignore" o
										value="<s:text name="button.update"/>" class="button"
										onClick="submitAction('update<s:property value="lstForward"/>');"
										<s:property value="updateDisabled" />></td>
								</s:else>
								<td><input type="button" name="ignore" value="Cancel"
									class="button"
									onClick="submitAction('cancel<s:property value="lstForward"/>');"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</s:if>
		<s:else>
			<div id="vlanTitleDiv" class="topFixedTitle">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td style="padding: 10px 10px 10px 10px">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td align="left">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><img
													src="<s:url value="/images/hm_v2/profile/HM-icon-IP_Tracking.png" includeParams="none"/>"
													width="40" height="40" alt="" class="dblk" /></td>
												<td class="dialogPanelTitle"><s:if
														test="%{dataSource.id == null}">
														<s:text name="config.title.routingPolicy" />
													</s:if> <s:else>
														<s:text name="config.title.routingPolicy.edit" />
													</s:else> &nbsp;</td>
												<td><a href="javascript:void(0);"
													onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');">
														<img
														src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
														alt="" class="dblk" />
												</a></td>
											</tr>
										</table>
									</td>
									<td align="right"><s:if test="%{!parentIframeOpenFlg}">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="npcButton"><a href="javascript:void(0);"
														class="btCurrent" onclick="submitActionJson('cancel')"
														title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text
																	name="config.v2.select.user.profile.popup.cancel" /></span></a></td>
													<td width="20px">&nbsp;</td>
													<td class="npcButton"><s:if
															test="%{dataSource.id == null}">
															<s:if test="%{writeDisabled == 'disabled'}">
									&nbsp;</td>
													</s:if>
													<s:else>
														<a href="javascript:void(0);" class="btCurrent"
															onclick="submitActionJson('create');"
															title="<s:text name="button.update"/>"><span><s:text
																	name="button.update" /></span></a>
														</td>
													</s:else>
													</s:if>
													<s:else>
														<s:if test="%{updateDisabled == 'disabled'}">
									&nbsp;</td>
														</s:if>
														<s:else>
															<a href="javascript:void(0);" class="btCurrent"
																onclick="submitActionJson('update');"
																title="<s:text name="button.update"/>"><span><s:text
																		name="button.update" /></span></a>
															</td>
														</s:else>
													</s:else>
												</tr>
											</table>
										</s:if> <s:else>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="npcButton"><a href="javascript:void(0);"
														class="btCurrent"
														onClick="submitAction('cancel<s:property value="lstForward"/>');"
														title="<s:text name="common.button.cancel"/>"><span
															style="margin-right: 10px; padding-bottom: 2px; padding-top: 2px;"><s:text
																	name="common.button.cancel" /></span></a></td>
													<td width="20px">&nbsp;</td>
													<s:if test="%{dataSource.id == null}">
														<s:if test="%{writeDisabled == ''}">
															<td class="npcButton"><a href="javascript:void(0);"
																class="btCurrent"
																onClick="submitAction('create<s:property value="lstForward"/>');"
																title="<s:text name="common.button.save"/>"><span
																	style="padding-bottom: 2px; padding-top: 2px;"><s:text
																			name="common.button.save" /></span></a></td>
														</s:if>
													</s:if>
													<s:else>
														<s:if test="%{updateDisabled == ''}">
															<td class="npcButton"><a href="javascript:void(0);"
																class="btCurrent"
																onClick="submitAction('update<s:property value="lstForward"/>');"
																title="<s:text name="common.button.save"/>"><span
																	style="padding-bottom: 2px; padding-top: 2px;"><s:text
																			name="common.button.save" /></span></a></td>
														</s:if>
													</s:else>
												</tr>
											</table>
										</s:else></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</s:else>
		<s:if test="%{jsonMode == true}">
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				class="topFixedTitle">
				</s:if>
				<s:else>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						</s:else>
						<tr>
							<td style="padding-left: 2px"><tiles:insertDefinition
									name="notes" /></td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td>
								<table class="editBox" cellspacing="0" cellpadding="0"
									border="0" width="900px">
									<tr od>
										<td>
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="4"></td>
												</tr>
												<tr>
													<td class="labelT1" width="80"><label><s:text
																name="config.routing.pbr.name" /><font color="red"><s:text
																	name="*" /></font></label></td>
													<td><s:textfield name="dataSource.profileName"
															id="profileName"
															onkeypress="return hm.util.keyPressPermit(event,'name');"
															size="36" maxlength="32" disabled="%{disabledName}" />&nbsp;<s:text
															name="config.routing.policy.name.range" /></td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
															name="config.routing.policy.description" /></td>
													<td><s:textfield name="dataSource.description"
															size="72" maxlength="64" />&nbsp;<s:text
															name="config.routing.policy.description.range" /></td>
												</tr>

											</table>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>

									<tr style="padding-top: 10px">
										<td style="padding: 0 10px 0px 10px">
											<fieldset>
												<legend>
													<s:text name="config.routing.pbr.routing.policies" />
												</legend>
												<table cellspacing="0" cellpadding="0" border="0"
													style="width: 900px">
													<tr>
														<td width="230px"><s:radio onclick="this.blur();"
																onchange="changeProfileType(this);" label="Gender"
																name="dataSource.profileType" list="%{splitTunnel}"
																listKey="key" listValue="value" /></td>
														<td width="230px"><s:radio onclick="this.blur();"
																onchange="changeProfileType(this);" label="Gender"
																name="dataSource.profileType" list="%{tunnelAll}"
																listKey="key" listValue="value" /></td>
														<td><s:radio onclick="this.blur();"
																onchange="changeProfileType(this);" label="Gender"
																name="dataSource.profileType" list="%{custom}"
																listKey="key" listValue="value" /></td>
													</tr>
													<tr>
														<td colspan="4"
															style="padding-left: 10px; padding-bottom: 10px:; padding-top: 10px"
															class="noteInfo" width="940px">
															<div id="splitTunnelNotes">
																<s:text
																	name="config.routing.policy.pbr.split.tunnel.notes" />
															</div>
															<div id="TunnelAllNotes">
																<s:text
																	name="config.routing.policy.pbr.tunnel.all.notes" />
															</div>
															<div id="CustomNotes">
																<s:text name="config.routing.policy.pbr.custom.notes" />
															</div>
														</td>
													</tr>
												</table>
												<table width="100%" border="0" bordercolordark="#ffffff"
													id="customRuleTable">
													<tr id="routingPolicyPriRow_0" class="nodrop">
														<th align="left"
															style="padding-left: 0px; width: 5px; display: none"><input
															type="checkbox" id="checkAll"
															onClick="toggleCheckAllRules(this);"></th>
														<th align="left" style="padding-left: 1px; width: 270px;"><s:text
																name="config.routing.policy.rules.sourcetype" /></th>
														<th align="left" style="padding-left: 1px; width: 270px;"><s:text
																name="config.routing.policy.rules.destinationtype" /></th>
														<th align="left" style="padding-left: 1px; width: 120px"><s:text
																name="config.routing.policy.rules.primary" /></th>
														<th align="left" style="padding-left: 1px; width: 120px"><s:text
																name="config.routing.policy.rules.backup1" /></th>
														<th align="left" nowrap="nowrap"
															style="padding-left: 1px; width: 90px"><img
															class="dinl" src="<s:url value="/images/new.png" />"
															width="16" height="16" style="cursor: pointer" alt="New"
															title="New" onclick="addTableRow(this);" /></th>
													</tr>
													<s:iterator value="ruleList" status="status">
														<tiles:insertDefinition name="rowClass" />
														<tr id="routingPolicyPriRow_<s:property value='%{#status.index+1}' />" class="<s:property value="%{#rowClass}"/>">
															<td class="listCheck" style="display: none"><s:checkbox name="ruleIndices" /></td>
															<td class="list" width="220px">
															<s:if test="%{sourcetype==1}"><s:text name="config.title.pbr.routingProfile.ip.range" /><s:if test="%{sourcevalue!=''}">(<s:property value="sourcevalue" />)</s:if></s:if>
															<s:if test="%{sourcetype==2}"><s:text name="config.title.pbr.routingProfile.network" /><s:if test="%{sourcevalue!=''}">(<s:property value="sourcevalue" />)</s:if></s:if> 
															<s:if test="%{sourcetype==3}"><s:text name="config.title.pbr.routingProfile.interface" /><s:if test="%{sourcevalue!=''}">(<s:property value="sourcevalue" />)</s:if></s:if>
															<s:if test="%{sourcetype==4}"><s:text name="config.title.pbr.routingProfile.user.profile" /><s:if test="%{sourcevalue!=''}">(<s:property value="sourcevalue" />)</s:if></s:if>
															<s:if test="%{sourcetype==0}"><s:text name="config.title.pbr.routingProfile.any" /><s:if test="%{sourcevalue!=''}">(<s:property value="sourcevalue" />)</s:if></s:if>
															</td>

															<td class="list" width="220px">
															<s:if test="%{destinationtype==1}"><s:text name="config.title.pbr.routingProfile.ip.range" /><s:if test="%{destinationvalue!=''}">(<s:property value="destinationvalue" />)</s:if></s:if>
															<s:if test="%{destinationtype==2}"><s:text name="config.title.pbr.routingProfile.network" /><s:if test="%{destinationvalue!=''}">(<s:property value="destinationvalue" />)</s:if></s:if>
															<s:if test="%{destinationtype==3}"><s:text name="config.title.pbr.routingProfile.hostname" /><s:if test="%{destinationvalue!=''}">(<s:property value="destinationvalue" />)</s:if></s:if>
															<s:if test="%{destinationtype==4}"><s:text name="config.title.pbr.routingProfile.private" /><s:if test="%{destinationvalue!=''}">(<s:property value="destinationvalue" />)</s:if></s:if>
															<s:if test="%{destinationtype==0}"><s:text name="config.title.pbr.routingProfile.any" /><s:if test="%{destinationvalue!=''}">(<s:property value="destinationvalue" />)</s:if></s:if>
															</td>

															<td class="list" valign="top" width="150px">
															<s:if test="%{out1==''}">
																-
															</s:if>
															<s:else>
																<s:property value="out1" />
															</s:else>
															</td>
															<td class="list" valign="top" width="150px">
															<s:if test="%{out2==''}">
																-
															</s:if>
															<s:else>
																	<s:property value="out2" />
															</s:else>
															</td>
															<td style="display: none"><s:hidden
																	name="sourcetypearr" value="%{sourcetype}"></s:hidden>
																<s:hidden name="sourcevaluearr" value="%{sourcevalue}"></s:hidden>
																<s:hidden name="destinationtypearr"
																	value="%{destinationtype}"></s:hidden> <s:hidden
																	name="destinationvaluearr" value="%{destinationvalue}"></s:hidden>
																<s:hidden name="out1arr" value="%{out1}"></s:hidden> <s:hidden
																	name="out2arr" value="%{out2}"></s:hidden></td>

															<td width="48" class="list"><img class="dinl"
																src="<s:url value="/images/new.png" />" width="16"
																onclick="addTableRow(this);" height="16"
																style="cursor: pointer" alt="new" title="New" /><img
																class="dinl" src="<s:url value="/images/modify.png" />"
																width="16" onclick="editRow(this);" height="16"
																style="cursor: pointer" alt="modify" title="Modify" /><img
																class="dinl" src="<s:url value="/images/trash.png" />"
																width="16" height="16" onclick="delcheckTableRow(this);"
																style="cursor: pointer" alt="remove" title="Remove" />
															</td>
														</tr>
													</s:iterator>
												</table>
												<table width="100%" style="width: 880px" border="0"
													bordercolordark="#ffffff" id="splitTunelTable">
													<tr id="routingPolicySplitRow_0" class="nodrop">
														<th align="left" style="padding-left: 1px; width: 260px;"><s:text
																name="config.routing.policy.rules.sourcetype" /></th>
														<th align="left" style="padding-left: 1px; width: 260px;"><s:text
																name="config.routing.policy.rules.destinationtype" /></th>
														<th align="left" style="padding-left: 1px; width: 150px"><s:text
																name="config.routing.policy.rules.primary" /></th>
														<th align="left" style="padding-left: 1px; width: 150px"><s:text
																name="config.routing.policy.rules.backup1" /></th>
													</tr>
													<tr>
														<td class="listCheck" style="display: none"><s:checkbox
																name="ruleIndices" /></td>
														<td class="list"><s:text
																name="config.pbr.split.userprofile.anyguest" /></td>
														<td class="list">
															<div id="splitTunneldestination1">
																<s:text name="config.pbr.split.private" />
															</div>
															<div id="TunnelAlldestination1">
																<s:text name="config.pbr.split.any" />
															</div>
														</td>
														<td class="list"><s:text name="config.pbr.split.drop" />
														</td>
														<td class="list"></td>
														<td class="list" id="tunelTd1"><s:hidden
																name="sourcetypearr" value="4" /> <s:hidden
																name="sourcevaluearr" value="Any Guest" /> <s:hidden
																name="destinationtypearr" value="4" /> <s:hidden
																name="destinationvaluearr" value="" /> <s:hidden
																name="out1arr" value="Drop" /> <s:hidden name="out2arr"
																value="" /></td>
													</tr>
													<tr>
														<td class="listCheck" style="display: none"><s:checkbox
																name="ruleIndices" /></td>
														<td class="list"><s:text name="config.pbr.split.any" />
														</td>
														<td class="list">
															<div id="splitTunneldestination2">
																<s:text name="config.pbr.split.private" />
															</div>
															<div id="TunnelAlldestination2">
																<s:text name="config.pbr.split.any" />
															</div>
														</td>
														<td class="list"><s:text
																name="config.pbr.split.corporate.network" /></td>
														<td class="list"></td>
														<td class="list" id="tunelTd2"><s:hidden
																name="sourcetypearr" value="0" /> <s:hidden
																name="sourcevaluearr" value="" /> <s:hidden
																name="destinationtypearr" value="4" /> <s:hidden
																name="destinationvaluearr" value="" /> <s:hidden
																name="out1arr" value="Corporate Network (VPN)" /> <s:hidden
																name="out2arr" value="" /></td>
													</tr>
													<tr id="splitTunnelTr">
														<td class="listCheck" style="display: none"><s:checkbox
																name="ruleIndices" /></td>
														<td class="list"><s:text name="config.pbr.split.any" />
														</td>
														<td class="list"><s:text name="config.pbr.split.any" />
														</td>
														<td class="list">
															<div>
																<s:select id="splitTunnelPrimary" list="%{pbrSelects}"
																	onchange="primaryChangeSplit();" headerKey=""
																	headerValue="" />
															</div>

														</td>
														<td class="list">

															<div>
																<s:select id="splitTunnelBackup" list="%{pbrSelects}"
																	onchange="primarychange();" headerKey="" headerValue="" />
															</div>

														</td>
														<td class="list" id="tunelTd3"><s:hidden
																name="sourcetypearr" value="0" /> <s:hidden
																name="sourcevaluearr" value="" /> <s:hidden
																name="destinationtypearr" value="0" /> <s:hidden
																name="destinationvaluearr" value="" /> <s:hidden
																name="out1arr" id="splitout1" value="" /> <s:hidden
																name="out2arr" id="splitout2" value="" /></td>
													</tr>
												</table>
												<table id="templetable" style="display: none">
													<tr id="templetr" class="" style="cursor: move;">
														<td class="listCheck" style="display: none"><s:checkbox
																id="hidecheckbox" name="ruleIndices" /></td>

														<td class="list" valign="top"><div id="sourcediv1">
																Type&nbsp;&nbsp;&nbsp;
																<s:select id="templatesourceselect"
																	cssStyle="width:90px"
																	list="#{'1':'IP Range','2':'Network','3':'Interface','4':'User Profile'}"
																	onchange="sourcetypechange(this);" headerKey="0"
																	headerValue="Any" />
															</div>
															<div id="sourcediv2"
																style="padding-top: 5px; display: none">
																Value&nbsp;
																<s:textfield id="templatesourceinput" value=""
																	style="width: 188px" />
															</div></td>

														<td class="list" valign="top"><div
																id="destinationdiv1">
																Type&nbsp;&nbsp;&nbsp;
																<s:select id="templatedestinationselect"
																	cssStyle="width:90px"
																	list="#{'1':'IP Range','2':'Network','3':'Hostname','4':'Private'}"
																	onchange="destinationvaluechange(this);" headerKey="0"
																	headerValue="Any" />
															</div>
															<div id="destinationdiv2"
																style="padding-top: 5px; display: none">
																Value&nbsp;
																<s:textfield id="templatedestinationinput" value=""
																	style="width: 188px" />
															</div></td>

														<td class="list" valign="top"><s:select
																id="templeprimaryoutselect" list="%{pbrSelects}"
																onchange="primarychange();" headerKey="" headerValue="" /></td>
														<td class="list" valign="top"><s:select
																id="templeout2select" list="%{pbrSelects}" onchange=""
																headerKey="" headerValue="" /></td>

														<td style="display: none"></td>
														<td width="48" class="list"><img id="tempsave"
															class="dinl" onclick="saveRow(this);"
															style="cursor: pointer"
															src="<s:url value="/images/save.png" />" width="16"
															height="16" alt="save" title="Save" /> <img class="dinl"
															id="tempdelete" onclick="delcheckTableRow(this);"
															style="cursor: pointer"
															src="<s:url value="/images/cancel.png" />" width="16"
															title="Cancel" /></td>
													</tr>
													<tr>
														<td><s:select id="templatesourvevalueselect"
																list="%{pbrForInterfaceSelectsed}"
																onchange="sourcevaluechange();" /> <s:select
																id="templatesourveprofileselect"
																list="%{userprofileList}" listValue="value"
																listKey="value" /> <s:hidden name="sourcetypearrhide"
																id="sourcetypearrhide"></s:hidden> <s:hidden
																name="sourcevaluearrhide" id="sourcevaluearrhide"></s:hidden>
															<s:hidden name="destinationtypearrhide"
																id="destinationtypearrhide"></s:hidden> <s:hidden
																name="destinationvaluearrhide"
																id="destinationvaluearrhide"></s:hidden> <s:hidden
																name="out1arrhide" id="out1arrhide"></s:hidden> <s:hidden
																name="out2arrhide" id="out2arrhide"></s:hidden></td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					</s:form>


					</div>