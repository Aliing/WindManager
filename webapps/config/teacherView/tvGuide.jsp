<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<style type="text/css">
ul.operation {
	margin: 0px;
	padding-left: 10px;
	padding-top: 10px;
	list-style-type: disc;
}
#guideContent {
	line-height: 1.5em;
	padding: 0px 5px 10px 5px;
}

table.guideBox{
	background-color: #FFFFFF;
	-moz-border-radius: 4px;
    -webkit-border-radius: 4px;
	border: 1px solid #999;
	background-color: #FFFFCC;
}

ul.guideList{
	margin: 10px 0;
	padding-left: 25px;
	list-style-type: circle;
	color: #0A4687;
}
ul.guideList li{
	padding: 5px 15px 5px 2px;
}
ul.guideList a{
	text-decoration: underline;
	color: #003366;
	font-weight: bold;
}
ul.guideList a:HOVER{
	text-decoration: none;
}

a.guideTitle{
	font-weight: bold;
	font-size: 12px;
	color: #003366;
	text-decoration: underline;
}

a.guideTitle:HOVER{
	text-decoration: none;
}

b.countItem {
	font-weight: normal;
}

a.addNew {
	font-style: normal;
}

.infoList td {
	border-bottom: 1px dashed #E1E1E1;
	padding: 10px 15px 20px;
}

.current-item {
	background: #EEEEEE none repeat scroll 0%;
}

.infoList .current-item td {
	border-bottom: 1px solid #C0DAFF;
}
</style>
<script type="text/javascript">
var formName = 'tvGuide';

function submitAction(operation) {
	if (validate(operation)){
		document.forms[formName].operation.value = operation;
	    document.forms[formName].submit();
	}
}

function validate(operation){
	return true;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

YAHOO.util.Event.onDOMReady(function () {
	loadGlowImages();//load images first!
	var table = document.getElementById("infoList");
	YAHOO.util.Event.addListener(table.rows, "mouseover", mouseOverRow);
	YAHOO.util.Event.addListener(table.rows, "mouseout", mouseOutRow);
});

var guidedObjects = {
	classObj:{
		over:"<s:url value="/images/Class-glow.png" includeParams="none"/>",
		out:"<s:url value="/images/Class-no-glow.png" includeParams="none"/>"
	},
	stuObj:{
		over:"<s:url value="/images/LocalUserDB-Glow.png" includeParams="none"/>",
		out:"<s:url value="/images/LocalUserDB.png" includeParams="none"/>"
	},
	resourceObj:{
		over:"<s:url value="/images/ResourceMap-glow.png" includeParams="none"/>",
		out:"<s:url value="/images/ResourceMap-no-glow.png" includeParams="none"/>"
	},
	cartObj:{
		over:"<s:url value="/images/Cart-glow.png" includeParams="none"/>",
		out:"<s:url value="/images/Cart-no-glow.png" includeParams="none"/>"
	}
}
function loadGlowImages(){
	for(var prop in guidedObjects){
		var obj = guidedObjects[prop];
		obj.overImg = hm.util.loadImage(obj.over);
		obj.outImg = hm.util.loadImage(obj.out);
	}
}

function mouseOverRow(){
	this.className = 'current-item';
	var firstChild = this.cells[0].firstChild;
	if(firstChild && firstChild.src && this.id){
		if(YAHOO.env.ua.ie){
			firstChild.src = guidedObjects[this.id].over;
		}else{//reduce request
			this.cells[0].replaceChild(guidedObjects[this.id].overImg, firstChild);
		}
	}
}

function mouseOutRow(){
	this.className = '';
	var firstChild = this.cells[0].firstChild;
	if(firstChild && firstChild.src && this.id){
		if(YAHOO.env.ua.ie){
			firstChild.src = guidedObjects[this.id].out;
		}else{//reduce request
			this.cells[0].replaceChild(guidedObjects[this.id].outImg, firstChild);
		}
	}
}

</script>

<div id="content"><s:form action="tvGuide">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td style="padding-top: 5px;">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" width="740px">
					<table class="editBox" border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr>
							<td class="labelH1"
								style="font-size: 16px; background: #EEEEEE; padding-top: 20px; padding-left: 20px; padding-bottom: 5px;"><s:text
								name="config.tv.guide.title" /></td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
						<tr>
							<td class="labelT">
							<div id="guideContent">
							<table id="infoList" class="infoList" width="100%" border="0"
								cellspacing="0" cellpadding="0">
								<tr id="classObj">
									<td valign="top" width="65px"><img
										alt="<s:text
									name="config.tv.guide.class.title" />"
										src="<s:url value="/images/Class-no-glow.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configClasses"
										onclick="submitAction('configClasses')"><s:text
										name="config.tv.guide.class.title" /></a>:
									<s:text name="config.tv.guide.class" /><br>
									<ul class="operation">
										<li><a class="addNew" href='#addClass'
											onclick="submitAction('addClass')"> <s:text
											name="config.tv.guide.class.addNew" /></a></li>
										<li><a class="addNew" href='#configClasses'
											onclick="submitAction('configClasses')"> <s:text
											name="config.tv.guide.class.list" /></a></li>
									</ul>
									</td>
								</tr>

								<tr id="cartObj">
									<td valign="top" width="65px"><img
										alt="<s:text name="config.tv.guide.computercart.title" />"
										src="<s:url value="/images/Cart-no-glow.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configComputerCart"
										onclick="submitAction('configComputerCart')"><s:text
										name="config.tv.guide.computercart.title" /></a>:
									<s:text name="config.tv.guide.computercart" /><br>
									<ul class="operation">
										<li><a class="addNew" href='#addComputerCart'
											onclick="submitAction('addComputerCart')"> <s:text
											name="config.tv.guide.computercart.addNew" /></a></li>
										<li><a class="addNew" href='#configComputerCart'
											onclick="submitAction('configComputerCart')"> <s:text
											name="config.tv.guide.computercart.list" /></a></li>
									</ul>
									</td>
								</tr>

								<tr id="stuObj">
									<td valign="top" width="65px"><img
										alt="<s:text name="config.tv.guide.studentroster.title" />"
										src="<s:url value="/images/LocalUserDB.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configStudentRoster"
										onclick="submitAction('configStudentRoster')"><s:text
										name="config.tv.guide.studentroster.title" /></a>:
									<s:text name="config.tv.guide.studentroster" /><br>
									<ul class="operation">
										<li><a class="addNew" href='#addStudentRoster'
											onclick="submitAction('addStudentRoster')"> <s:text
											name="config.tv.guide.studentroster.addNew" /></a></li>
										<li><a class="addNew" href='#configStudentRoster'
											onclick="submitAction('configStudentRoster')"> <s:text
											name="config.tv.guide.studentroster.list" /></a></li>
									</ul>
									</td>
								</tr>
								<tr id="resourceObj">
									<td valign="top" width="65px"><img
										alt="<s:text name="config.tv.guide.resourcemap.title" />"
										src="<s:url value="/images/ResourceMap-no-glow.png" includeParams="none"/>" /></td>
									<td valign="top"><a class="guideTitle" href="#configResourceMap"
										onclick="submitAction('configResourceMap')"><s:text
										name="config.tv.guide.resourcemap.title" /></a>:
									<s:text name="config.tv.guide.resourcemap" /><br>
									<ul class="operation">
										<li><a class="addNew" href='#addResourceMap'
											onclick="submitAction('addResourceMap')"> <s:text
											name="config.tv.guide.resourcemap.addNew" /></a></li>
										<li><a class="addNew" href='#configResourceMap'
											onclick="submitAction('configResourceMap')"> <s:text
											name="config.tv.guide.resourcemap.list" /></a></li>
									</ul>
									</td>
								</tr>
							</table>
							</div>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
