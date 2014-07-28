<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<style>
p {
	font-family: Verdana, sans-serif;
	font-size: 20px;
	color: #CC3300;
	font-weight: bold;
}
</style>
<script>
var formName = 'teacherView';
var pageLoaded = false;

function onLoadPage() {
	getClassInfo();
	getTeacherList();
}

function submitAction(operation) {
	document.forms[formName].operation.value = operation;
	document.forms[formName].submit();
}

function getClassInfo() {
	var classid = document.getElementById("classes").value;
	
	if(classid == -1) {
		return ;
	}
	
	var url = '<s:url action="teacherView" includeParams="none" />' + '?operation=classInfo&classId=' + escape(classid);
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : updateClassInfo}, null);
}

var updateClassInfo = function(o) {
	eval("var details = " + o.responseText);
	var table = document.getElementById("classDetails");
	
	if(table.rows.length > 1) {
		for (var i=table.rows.length-1; i>0; i--) {
			table.deleteRow(i);
		}
	}
	
	for (var i = 0; i < details.length; i ++) {
		var newTR = table.insertRow(table.rows.length);
		var newTD = newTR.insertCell(0);
		
		if(details[i].selected) {
			newTD.innerHTML = '<img src="<s:url value="/images/status1.png" />"' + 
									'width="16" height="16"/>';
		} else {
			newTD.innerHTML = "&nbsp;";
		}
		
		newTD = newTR.insertCell(1);
		newTD.width = "80px";
		newTD.innerHTML = details[i].day;
		newTD = newTR.insertCell(2);
		newTD.innerHTML = details[i].start;
		newTD = newTR.insertCell(3);
		newTD.innerHTML = details[i].end;
		newTD = newTR.insertCell(4);
		newTD.innerHTML = details[i].room;
	}
}

function getTeacherList() {
	var subject = document.getElementById("subjects").value;
	
	if(subject == 'None available') {
		return ;
	}
	
	var url = '<s:url action="teacherView" />' + '?operation=teacherList&subject=' + escape(subject);
	
	if(!pageLoaded) {
		var classId = document.getElementById("classes").value;
		url = url + '&classId=' + escape(classId); 
	}
	
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : updateTeacherList}, null);
}

var updateTeacherList = function(o) {
	eval("var details = " + o.responseText);
	var teacherList = document.getElementById("teachers");
	teacherList.length = 0;
	teacherList.length = details.length;
	
	for(var i=0; i<details.length; i++) {
		teacherList.options[i].value = teacherList.options[i].text = details[i].teacher; 
	}
	
	if(pageLoaded) {
		getClassOfTeacher(teacherList.options[0].value);
	} else {
		pageLoaded = true;
	}
	
}

function getClassOfTeacher(teacher) {
	var subject = document.getElementById("subjects").value;
	var url = '<s:url action="teacherView" />' + '?operation=refreshClassList&teacher=' + encodeURIComponent(teacher)
		+ '&subject=' + encodeURIComponent(subject);
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, {success : refreshClassList}, null);
}

var refreshClassList = function(o) {
	eval("var details = " + o.responseText);
	var classList = document.getElementById("classes");
	classList.length = 0;
	classList.length = details.length;
	
	for(var i=0; i<details.length; i++) {
		classList.options[i].value = details[i].id;
		classList.options[i].text = details[i].name; 
	}
	
	getClassInfo();
}

</script>

<s:form action="teacherView">
	<s:hidden name="operation" />
	<table  width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="100px"></td>
		</tr>
		<tr>
			<td align="center" colspan="2">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left">
							<tiles:insertDefinition name="notes" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<s:if test="%{teacherViewEnabled}">
			<td align="center" valign="middle">
				<table class="editBox" border="0" cellspacing="0" cellpadding="0" height="360px" width="550px">
					<tr>
						<td colspan="2" align="left" style="padding: 10px 0 0 30px">
							<s:text name="teacherView.prompt"></s:text>
						</td>
					</tr>
					<tr>
						<td colspan="2" height="30px"></td>
					</tr>
					<tr>
						<td width="80px" style="padding: 0 0 0 30px">
							<s:text name="teacherView.class"></s:text>
						</td>			
						<td align="left">
							<s:select id="classes" list="%{teacherClassList}"
								name="classId" value="classId"
								listKey="id" listValue="value"
								cssStyle="width: 200px;"
								onchange="getClassInfo();" />
						</td>	
					</tr>
					<tr>
						<td></td>
						<td align="left">
							<table id="classDetails">
								<tr>
									<td width="10px">&nbsp;</td>								
									<th width="80px"><s:text name="teacherView.weekDay" /></th>
									<th width="60px"><s:text name="teacherView.startTime" /></th>
									<th width="60px"><s:text name="teacherView.endTime" /></th>
									<th width="60px"><s:text name="teacherView.room" /></th>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="2" height="30px"></td>
					</tr>
					<tr>
						<td colspan="2" align="left" style="padding: 0 0 0 30px">
							<table>
								<tr>
									<td>
										<script type="text/javascript">insertFoldingLabelContext('<s:text name="teacherView.substitute" />',
       										'substitute');</script>
									</td>
								</tr>
								<tr>
									<td height="10px"></td>
								</tr>
								<tr>
       								<td style="padding: 0 6px 0 80px;">
       									<div id="substitute" style="display: none">
       									<fieldset><div>
       									<table cellspacing="0" cellpadding="0" border="0" >
       										<tr>
												<td colspan="2" height="10"></td>
											</tr>
											<tr>
												<td class="labelT1" width="80px">
													<s:text name="teacherView.subject"></s:text>
												</td>
												<td>
													<s:select id="subjects" list="%{subjectList}"
														name="subject" value="subject"
														cssStyle="width: 160px;"
														onchange="getTeacherList();" />
												</td>
											</tr>
											<tr>
												<td class="labelT1" width="80px">
													<s:text name="teacherView.teacher"></s:text>
												</td>
												<td>
													<s:select id="teachers" list="#{'-1':'To be added'}"
														name="teacher" value="teacher"
														cssStyle="width: 160px;"
														onchange="getClassOfTeacher(this.value);" />
												</td>
											</tr>
              							</table>
       									</div></fieldset>
       									</div>
       								</td>
       							</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="2" height="20px"></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<table cellspacing="0" cellpadding="0" border="0" >
								<tr>
									<td>
										<input type="button" name="ignore" value="<s:text name="teacherView.submit"/>"
											class="button"
											onClick="submitAction('redirect');">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>	
			</td>
			</s:if>
			<s:else>
			<td align="center" valign="middle" height="500px">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<p><s:text name="teacherView.disabled"></s:text></p>
						</td>
					</tr>
				</table>	
			</td>
			</s:else>
		</tr>
	</table>
</s:form>