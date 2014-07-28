<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@page import="com.ah.ui.actions.Navigation"%>

<script>
var formName = 'qosClassfierAndMarker';
function onLoadPage() {	
	if (document.getElementById("qosName").disabled == false) {
			document.getElementById("qosName").focus();
	}
	//var chID="<s:property value="%{chID}"/>";
	//if(chID!="" && chID!='')
	//	selectMarking(chID);
}

function submitAction(operation) {
	if (validate(operation)) {
		if (operation != 'create') {
			showProcessing();
		}
		document.forms[formName].operation.value = operation;
		
	    document.forms[formName].submit();
	}
}
function validate(operation) {
	if (operation == "<%=Navigation.L2_FEATURE_QOS_CLASSFIER_AND_MARKER%>" ||
		operation == 'cancel' + '<s:property value="lstForward"/>' 
		)
	{
		return true;
	}

    if (!validateName()) {
		return false;
	}
	
	return true;
}

function validateName() {
       var inputElement=document.getElementById("qosName");
       var message = hm.util.validateName(inputElement.value, '<s:text name="config.qos.classification.name" />');
       if (message != null) {
           hm.util.reportFieldError(inputElement, message);
           inputElement.focus();
           return false;
       }     
      return true;
}
function selectMarking(chID)
{
    var ch=document.getElementById(chID);
    var obj=document.getElementById("marksEnabled");
    var objPT=document.getElementById("checkPT");
    var objET=document.getElementById("checkET");
    var objDT=document.getElementById("checkDT");
    
    if(ch.checked){
      selectMarked(chID);
      if (chID=="checkE") {
      	objPT.checked=false;
      }
      if (chID=="checkP") {
      	objET.checked=false;
      }
      obj.checked=true;
    }
    else{
      unselectOther("checkD");
      unselectOther("checkE");
      unselectOther("checkP");
      if (objPT.checked || objET.checked || objDT.checked) {
          obj.checked=true;
      } else {
      	  obj.checked=false;
      }
    }
}

function selectMarkingT(chID)
{
    var ch=document.getElementById(chID);
    var obj=document.getElementById("marksEnabled");
    var objP=document.getElementById("checkP");
    var objE=document.getElementById("checkE");
    var objD=document.getElementById("checkD");
    if(ch.checked){
      selectMarked(chID);
      if (chID=="checkET") {
      	objP.checked=false;
      }
      if (chID=="checkPT") {
      	objE.checked=false;
      }
      obj.checked=true;
    }
    else{
      unselectOther("checkDT");
      unselectOther("checkET");
      unselectOther("checkPT");
      if (objP.checked || objE.checked || objD.checked) {
          obj.checked=true;
      } else {
      	  obj.checked=false;
      }
    }
}

function selectMarked(chID)
{   
    if(chID=="checkE"){
       unselectOther("checkP");
       unselectOther("checkD");
    }
    if(chID=="checkP"){
       unselectOther("checkE");
       unselectOther("checkD");
    }
    if(chID=="checkD"){
       unselectOther("checkP");
       unselectOther("checkE");
    }
    if(chID=="checkET"){
       unselectOther("checkPT");
       unselectOther("checkDT");
    }
    if(chID=="checkPT"){
       unselectOther("checkET");
       unselectOther("checkDT");
    }
    if(chID=="checkDT"){
       unselectOther("checkPT");
       unselectOther("checkET");
    }
}
function unselectOther(chID)
{
    var obj=document.getElementById(chID);
    obj.checked=false;
}
function selectOther(chID)
{
    var obj=document.getElementById(chID);
    obj.checked=true;
}
function isSelectMarked(ch)
{
   if(!ch.checked)
   {
      unselectOther("checkD");
      unselectOther("checkE");
      unselectOther("checkP");
      unselectOther("checkDT");
      unselectOther("checkET");
      unselectOther("checkPT");
   }
   else
   {
      var obj=document.getElementById("checkP");
      obj.checked=true;
      var objT=document.getElementById("checkPT");
      objT.checked=true;
      selectMarking("checkP");
      selectMarkingT("checkPT");
   }
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
       document.writeln('<td class="crumb" nowrap><a href="<s:url action="qosClassfierAndMarker" includeParams="none"></s:url>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
       <s:if test="%{dataSource.id == null}">
           document.writeln('New </td>');
       </s:if>
       <s:else>
           document.writeln('Edit \'<s:property value="displayName" />\'</td>');
       </s:else>
    </s:else>
}

</script>
<div id="content"><s:form action="qosClassfierAndMarker">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<s:if test="%{dataSource.id == null}">
						<td><input type="button" name="ignore" value="<s:text name="button.create"/>"
							class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
							<s:property value="writeDisabled" />></td>
					</s:if>
					<s:else>
						<td><input type="button" name="ignore" value="<s:text name="button.update"/>"
							class="button" onClick="submitAction('update');"
							<s:property value="writeDisabled" />></td>
					</s:else>
					<s:if test="%{lstForward == null || lstForward == ''}">
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('<%=Navigation.L2_FEATURE_QOS_CLASSFIER_AND_MARKER%>');">
						</td>
					</s:if>
					<s:else>
						<td><input type="button" name="cancel" value="Cancel"
							class="button"
							onClick="submitAction('cancel<s:property value="lstForward"/>');">
						</td>
					</s:else>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="550">
				<tr>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="4"></td>
						</tr>
						<tr>
							<td class="labelT1" width="120"><s:text
								name="config.qos.classification.name" /><font color="red"><s:text name="*"/></font></td>
							<td><s:textfield name="dataSource.qosName" size="24" id="qosName"
								maxlength="%{nameLength}" disabled="%{disabledName}" 
								onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
								<s:text name="config.name.range"/></td>
						</tr>				
						<tr>
							<td class="labelT1" width="120"><s:text name="config.qos.classification.description" /></td>
							<td colspan="2"><s:textfield name="dataSource.description" size="48"
								maxlength="%{descriptionLength}" />
								<s:text name="config.description.range"/></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="4" colspan="2"></td>
				</tr>
				
				<tr>
				   <td style="padding:6px 0px 4px 4px;" colspan="2">
				     <table cellspacing="0" cellpadding="0" border="0" width="540">
				        <tr>
				         <td class="sepLine" colspan="2" style="padding:0px 10px 0px 10px;"><img
						   src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" /></td>
						</tr>
					 </table>
					</td>
				</tr>
							
				<tr>
					<td style="padding:6px 0px 4px 9px;">
					<table border="0" cellspacing="0" cellpadding="0" width="400">
						<tr>
							<td height="4" colspan="2"></td>
						</tr>
						        <tr>
									<td style="padding:4px 2px 0 10px"><s:text
										name="Ingress classification criteria and order " /></td>
								    <td style="padding:4px 2px 0 20px"><s:text
										name="Egress marking criteria " /></td>
								</tr>
								<tr>
									<td style="padding:4px 0 0 15px" align="left" colspan="2">1 <s:checkbox
										name="dataSource.networkServicesEnabled"
										id="networkServicesEnabled"/> <s:text
										name="config.qos.classification.tab.networkServices" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 15px"  colspan="2">2 <s:checkbox
										name="dataSource.macOuisEnabled" id="macOuisEnabled" /> <s:text
										name="config.qos.classification.tab.macOuis" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 15px"  colspan="2">3 <s:checkbox
										name="dataSource.ssidEnabled"/>
									<s:text name="SSIDs" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 15px"  colspan="2">4 <s:checkbox
										name="dataSource.marksEnabled" id="marksEnabled"
										onclick="isSelectMarked(this)"/> <s:text
										name="config.qos.classification.tab.mark" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 50px" > <s:checkbox
										name="dataSource.checkP" id="checkP" onclick="selectMarking(this.id)"/><s:text
										name="config.qos.protocolP" /></td>
									<td style="padding:2px 0 0 25px" ><s:checkbox
										name="dataSource.checkPT" id="checkPT"  
										onclick="selectMarkingT(this.id)"/><s:text
										name="config.qos.protocolP" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 50px" > <s:checkbox
										name="dataSource.checkD" id="checkD" onclick="selectMarking(this.id)"/><s:text
										name="config.qos.protocolD" /></td>
									<td style="padding:2px 0 0 25px" ><s:checkbox
										name="dataSource.checkDT" id="checkDT"  
										onclick="selectMarkingT(this.id)"/><s:text
										name="config.qos.protocolD" /></td>
								</tr>
								<tr>
									<td style="padding:2px 0 0 50px" > <s:checkbox
										name="dataSource.checkE" id="checkE" onclick="selectMarking(this.id)"/><s:text
										name="config.qos.protocolE" /></td>
									<td style="padding:2px 0 0 25px" ><s:checkbox 
										name="dataSource.checkET" id="checkET"
										onclick="selectMarkingT(this.id)"/><s:text
										name="config.qos.protocolE" /></td>
								</tr>		
					</table>
					</td>
				</tr>
				
				<tr>
					<td height="4"></td>
				</tr>	
			</table>		
			</td>
		</tr>
	</table>
</s:form></div>
