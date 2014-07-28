<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>

<script type="text/javascript">
<!--
var reportCustomTimeSelectIndex=1;

function fillReportScheduleSetting(index){
	var ele = Get("refrequency"+index);
	if(ele){
		ele.checked = true;
		clickCustomTime(index);
	}
}

function clickCustomTime(index){
	reportCustomTimeSelectIndex=index;
	init_report_customtime_settings();
}

function init_report_customtime_settings(){
	hm.util.hideFieldError();
	var url = "recurReport.action?operation=fetchScheduleSetting&refrequency=" + reportCustomTimeSelectIndex + "&ignore="+new Date().getTime();
	var transaction = YAHOO.util.Connect.asyncRequest('get', url,  {success : succfetchReportCustomTime, timeout: 60000});
}

var succfetchReportCustomTime=function(o){
	try {
		eval("var data = " + o.responseText);
	}catch(e){
		showWarnDialog("Fetch Custom Setting Error.", "Error");
		return false;
	}
	if (data.t) {
		<%--Get("enableTimeLocal").checked=data.lo;--%>
		Get("reEmailAddress").value=data.cuE;
		if(data.f==1) {
			Get("tr_custom_timetype_daily").style.display="";
			Get("tr_custom_timetype_weekly").style.display="none";
			Get("tr_custom_timetype_custom").style.display="none";
			Get("reCustomDay").checked=data.cuD;
			Get("reCustomTime").checked=data.cuT;
			customDayClick(data.cuD);
			customTimeClick(data.cuT)
			var dataCuDv = data.cuDv;
			for(var i=0;i<7;i++) {
				if(i==0) {
					if(dataCuDv.charAt(0)==1) {
						Get("cDay7").checked = true;
					} else {
						Get("cDay7").checked = false;
					}
				} else {
					if(dataCuDv.charAt(i)==1) {
						Get("cDay" + i).checked = true;
					} else {
						Get("cDay" + i).checked = false;
					}
				}
			}
			Get("reCustomTimeStart").value=data.cuTs;
			Get("reCustomTimeEnd").value=data.cuTe;
		} else if (data.f==2){
			Get("tr_custom_timetype_daily").style.display="none";
			Get("tr_custom_timetype_weekly").style.display="";
			Get("tr_custom_timetype_custom").style.display="none";
			//if(data.cuW==0) {
			//	data.cuW=7;
			//}
			Get("rDay" + data.cuW).checked = true;
		} else if (data.f==3){
			Get("tr_custom_timetype_daily").style.display="none";
			Get("tr_custom_timetype_weekly").style.display="none";
			Get("tr_custom_timetype_custom").style.display="none";
		} else if (data.f==4) {
			Get("tr_custom_timetype_daily").style.display="none";
			Get("tr_custom_timetype_weekly").style.display="none";
			Get("tr_custom_timetype_custom").style.display="";
			Get("cmTypeDay" + data.cmcutp).checked = true;
			if(data.cmcutp==1) {
				Get("reCmTimePeriodRange").innerHTML='(1-365 days)';
			} else {
				Get("reCmTimePeriodRange").innerHTML='(1-12 months)';
			}
			Get("reCmTimePeriod").value = data.cmcuperiod;
			Get("cmTypeStartDay" + data.cmcusd).checked = true;
			if(data.cmcusd==1) {
				Get("cmTypeStartDayValue").value = data.cmcusdvalue;
				Get("cmTypeStartDayValueTR").style.display="";
				Get("cmTypeStartDayWeekTR").style.display="none";
			} else {
				Get("cmTypeStartWeek"+ data.cmcusdvalue).checked = true;
				Get("cmTypeStartDayValueTR").style.display="none";
				Get("cmTypeStartDayWeekTR").style.display="";
			}
			Get("cmTypeStartMonth").value = data.cmcusm;
			changeCmTypeStartMonth(data.cmcusm);
			Get("cmTypeStartSpecYear").value=data.cmcussm;
		}
		return true;
	} else {
		showWarnDialog("Fetch Custom Setting Error.", "Error");
		return false;
	}
}

function changeCmTypeStartDay(value) {
	if(value==1) {
		Get("cmTypeStartDayValueTR").style.display="";
		Get("cmTypeStartDayWeekTR").style.display="none";
		Get("cmTypeStartDayValue").value = 1;
	} else {
		Get("cmTypeStartDayValueTR").style.display="none";
		Get("cmTypeStartDayWeekTR").style.display="";
		Get("cmTypeStartWeek1").checked = true;
	}
}

function changeCmTypeDay(value){
	Get("reCmTimePeriod").value=1;
	if(value==1) {
		Get("reCmTimePeriodRange").innerHTML='(1-365 days)';
	} else {
		Get("reCmTimePeriodRange").innerHTML='(1-12 months)';
	}
}

function changeCmTypeStartMonth(value) {
	if(value==13 || value==14) {
		Get("TdSpecYearField").style.display="none";
		Get("cmTypeStartSpecYear").value=2012;
	} else {
		Get("TdSpecYearField").style.display="";
	}
}

function customDayClick(checked) {
	if (checked) {
		Get("cDay1").disabled=false;
		Get("cDay2").disabled=false;
		Get("cDay3").disabled=false;
		Get("cDay4").disabled=false;
		Get("cDay5").disabled=false;
		Get("cDay6").disabled=false;
		Get("cDay7").disabled=false;
	} else {
		Get("cDay1").disabled=true;
		Get("cDay2").disabled=true;
		Get("cDay3").disabled=true;
		Get("cDay4").disabled=true;
		Get("cDay5").disabled=true;
		Get("cDay6").disabled=true;
		Get("cDay7").disabled=true;
	}
}

function customTimeClick(checked) {
	if (checked) {
		Get("reCustomTimeStart").disabled=false;
		Get("reCustomTimeEnd").disabled=false;
	} else {
		Get("reCustomTimeStart").disabled=true;
		Get("reCustomTimeEnd").disabled=true;
	}
}

function validateReportScheduleSettings(){
	if(Get("tr_custom_timetype_daily").style.display!="none"){
		if (Get("reCustomDay").checked) {
			if (!Get("cDay1").checked &&
				!Get("cDay2").checked &&
				!Get("cDay3").checked &&
				!Get("cDay4").checked &&
				!Get("cDay5").checked &&
				!Get("cDay6").checked &&
				!Get("cDay7").checked) {
				hm.util.reportFieldError(document.getElementById("errorCustomTime"), '<s:text name="error.requiredField"><s:param><s:text name="report.networkusage.customDay"/></s:param></s:text>');
				return false;
			}
		}
		if (Get("reCustomTime").checked) {
			if (parseInt(Get("reCustomTimeStart").value)>=parseInt(Get("reCustomTimeEnd").value)) {
				hm.util.reportFieldError(Get("reCustomTimeStart"), '<s:text name="error.shourldLargerThan"><s:param>To time</s:param><s:param>From time</s:param></s:text>');
				return false;
			}
		}
	}

	if(Get("tr_custom_timetype_custom").style.display!="none"){
		var inputElement = document.getElementById("reCmTimePeriod");
		var sdv = Get("cmTypeStartDayValue");
		if(inputElement.value.length == 0){
	    	hm.util.reportFieldError(inputElement, '<s:text name="error.requiredField"><s:param><s:text name="hm.recurreport.config.customize.time.customtype.reportperiod" /></s:param></s:text>');
	        inputElement.focus();
			return false;
		}
		if(Get("cmTypeDay1").checked) {
			 var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="hm.recurreport.config.customize.time.customtype.reportperiod" />', 1, 365);
			 if (message != null) {
				 hm.util.reportFieldError(inputElement,message);
				 inputElement.focus();
				 return false;
			 }
		}
		if(Get("cmTypeDay2").checked) {
			 var message = hm.util.validateIntegerRange(inputElement.value, '<s:text name="hm.recurreport.config.customize.time.customtype.reportperiod" />', 1, 12);
			 if (message != null) {
				 hm.util.reportFieldError(inputElement,message);
				 inputElement.focus();
				 return false;
			 }
		}
		if(Get("cmTypeStartDay1").checked) {
			if(sdv.value.length == 0){
		    	hm.util.reportFieldError(Get("cmTypeStartDay2"), '<s:text name="error.requiredField"><s:param><s:text name="hm.recurreport.config.customize.time.customtype.startday" /></s:param></s:text>');
		    	sdv.focus();
				return false;
			}
			var message = hm.util.validateIntegerRange(sdv.value, '<s:text name="hm.recurreport.config.customize.time.customtype.startday" />', 1, 28);
			 if (message != null) {
				 hm.util.reportFieldError(Get("cmTypeStartDay2"),message);
				 sdv.focus();
				 return false;
			 }
		}
		var dmyV= Get("cmTypeStartMonth").value;
		var ssy = Get("cmTypeStartSpecYear");
		if (dmyV != 13 && dmyV != 14){
			if(ssy.value.length == 0){
		    	hm.util.reportFieldError(ssy, '<s:text name="error.requiredField"><s:param><s:text name="hm.recurreport.config.customize.time.customtype.startmonth" /></s:param></s:text>');
		    	ssy.focus();
				return false;
			}
			var message = hm.util.validateIntegerRange(ssy.value, 'Specify Year', 2010, 2020);
			 if (message != null) {
				 hm.util.reportFieldError(ssy,message);
				 ssy.focus();
				 return false;
			 }
		}
	}

	if (document.getElementById("reEmailAddress").value.trim()=="") {
	  hm.util.reportFieldError(document.getElementById("reEmailAddress"), '<s:text name="error.requiredField"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
	  return false;
	}

	var emails = document.getElementById("reEmailAddress").value.split(";");
	for (var i=0;i<emails.length;i++) {
		if (i==emails.length-1 && emails[i].trim()=="") {
			break;
		}
		if (!hm.util.validateEmail(emails[i].trim())) {
			hm.util.reportFieldError(document.getElementById("reEmailAddress"), '<s:text name="error.formatInvalid"><s:param><s:text name="report.reportList.emailAddress" /></s:param></s:text>');
			document.getElementById("reEmailAddress").focus();
			return false;
		}
	}
	return true;
}

function collectReportScheduleSettings(formName){
	if(reportCustomTimeSelectIndex==1) {
		var reDayValue="";
		if(Get("cDay7").checked){
			reDayValue=reDayValue + "1";
		} else {
			reDayValue=reDayValue + "0";
		}
		for(var i=1;i<7;i++) {
			if(Get("cDay" + i).checked){
				reDayValue=reDayValue + "1";
			} else {
				reDayValue=reDayValue + "0";
			}
		}
		document.forms[formName].reCustomDayValue.value = reDayValue;
	} else if(reportCustomTimeSelectIndex==2) {
		var reWeekStartValue=0;
		for(var i=0;i<7;i++) {
			if(Get("rDay" + i).checked){
				reWeekStartValue=i;
				break;
			}
		}
		document.forms[formName].reWeekStart.value = reWeekStartValue;
	} else if(reportCustomTimeSelectIndex==4) {
		if(Get("cmTypeDay2").checked) {
			document.forms[formName].reCmTimeType.value = 2;
		} else {
			document.forms[formName].reCmTimeType.value = 1;
		}

		if(Get("cmTypeStartDay2").checked) {
			var reWeekStartValue=0;
			for(var i=0;i<7;i++) {
				if(Get("cmTypeStartWeek" + i).checked){
					reWeekStartValue=i;
					break;
				}
			}
			document.forms[formName].reCmTimeStartDayType.value = 2;
			document.forms[formName].reCmTimeStartDayValue.value = reWeekStartValue;
		} else {
			document.forms[formName].reCmTimeStartDayType.value = 1;
			document.forms[formName].reCmTimeStartDayValue.value = Get("cmTypeStartDayValue").value;
		}
		document.forms[formName].reCmTimeStartMontyYear.value = Get("cmTypeStartMonth").value;
		document.forms[formName].reCmTimeStartSepcYear.value = Get("cmTypeStartSpecYear").value;
	}
}

//-->
</script>
<s:hidden name="reCustomDayValue"></s:hidden>
<s:hidden name="reWeekStart"></s:hidden>
<s:hidden name="reCmTimeType"></s:hidden>
<s:hidden name="reCmTimeStartDayType"></s:hidden>
<s:hidden name="reCmTimeStartDayValue"></s:hidden>
<s:hidden name="reCmTimeStartMontyYear"></s:hidden>
<s:hidden name="reCmTimeStartSepcYear"></s:hidden>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.new.schedule" /></td>
					<td>
						<table  border="0" cellspacing="0" cellpadding="0" width="300px">
							<tr>
								<td><s:radio name="refrequency" id="refrequency" value="1" onclick="clickCustomTime(1);" list="#{'1':''}"/><label for="refrequency1"><s:text name="hm.recurreport.config.new.schedule.daily"/></label></td>
								<td><s:radio name="refrequency" id="refrequency" onclick="clickCustomTime(2);" list="#{'2':''}"/><label for="refrequency2"><s:text name="hm.recurreport.config.new.schedule.weekly"/></label></td>
								<td><s:radio name="refrequency" id="refrequency" onclick="clickCustomTime(3);" list="#{'3':''}"/><label for="refrequency3"><s:text name="hm.recurreport.config.new.schedule.monthly"/></label></td>
								<td style="display:none;"><s:radio name="refrequency" id="refrequency" onclick="clickCustomTime(4);" list="#{'4':''}"/><label for="refrequency4"><s:text name="hm.recurreport.config.new.schedule.custom"/></label></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr id="tr_custom_timetype_daily" style="display: none;">
					<td width="140px"></td>
					<td>
						<table  border="0" cellspacing="0" cellpadding="0">
							<tr><td colspan="2"><table><tr><td><span id="errorCustomTime"/></span></td></tr></table></td></tr>
							<tr>
								<td width="150px" style="padding: 2px 2px 2px 2px">
									<s:checkbox name="reCustomDay" id="reCustomDay" onclick="customDayClick(this.checked);"></s:checkbox><s:text name="report.networkusage.customDay" /></td>
								<td style="padding: 2px 2px 2px 4px" width="250px">
									<s:checkbox name="reCustomTime" id="reCustomTime" onclick="customTimeClick(this.checked);"></s:checkbox><s:text name="report.networkusage.customTime" /></td>
							</tr>
							<tr>
								<td style="padding: 2px 2px 2px 2px">
									<table  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:checkbox name="c7" id="cDay7" /></td>
											<td><s:checkbox name="c1" id="cDay1" /></td>
											<td><s:checkbox name="c2" id="cDay2" /></td>
											<td><s:checkbox name="c3" id="cDay3" /></td>
											<td><s:checkbox name="c4" id="cDay4" /></td>
											<td><s:checkbox name="c5" id="cDay5" /></td>
											<td><s:checkbox name="c6" id="cDay6" /></td>
										</tr>
										<tr>
											<td>&nbsp;&nbsp;S</td>
											<td>&nbsp;&nbsp;M</td>
											<td>&nbsp;&nbsp;T</td>
											<td>&nbsp;&nbsp;W</td>
											<td>&nbsp;&nbsp;T</td>
											<td>&nbsp;&nbsp;F</td>
											<td>&nbsp;&nbsp;S</td>
										</tr>
									</table>
								</td>

								<td valign="top" style="padding: 2px 2px 2px 4px" nowrap="nowrap">
								<s:select name="reCustomTimeStart"
									value="%{reCustomTimeStart}" list="%{lstHours}"
									id="reCustomTimeStart" listKey="id" listValue="value"/>--
								<s:select name="reCustomTimeEnd"
									value="%{reCustomTimeEnd}" list="%{lstHours}"
									id="reCustomTimeEnd" listKey="id" listValue="value"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="tr_custom_timetype_weekly" style="display: none;">
					<td class="labelT1"><s:text name="hm.recurreport.config.customize.time.weekstart"/></td>
					<td>
						<table  border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><s:radio name="rDay" id="rDay" list="#{'0':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'1':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'2':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'3':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'4':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'5':''}"/></td>
								<td><s:radio name="rDay" id="rDay" list="#{'6':''}"/></td>
							</tr>
							<tr>
								<td>&nbsp;&nbsp;S</td>
								<td>&nbsp;&nbsp;M</td>
								<td>&nbsp;&nbsp;T</td>
								<td>&nbsp;&nbsp;W</td>
								<td>&nbsp;&nbsp;T</td>
								<td>&nbsp;&nbsp;F</td>
								<td>&nbsp;&nbsp;S</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr id="tr_custom_timetype_custom" style="display: none;">
					<td colspan="2">
						<table  border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td class="labelT1" width="140px"><s:text name="hm.recurreport.config.customize.time.customtype"/></td>
								<td colspan="2">
									<table  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:radio name="cmTypeDay" id="cmTypeDay" onclick="changeCmTypeDay(1);" list="#{'1':''}"/><label for="cmTypeDay1"><s:text name="hm.recurreport.config.customize.time.customtype.day"/></label></td>
											<td><s:radio name="cmTypeDay" id="cmTypeDay" onclick="changeCmTypeDay(2);" list="#{'2':''}"/><label for="cmTypeDay2"><s:text name="hm.recurreport.config.customize.time.customtype.month"/></label></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="hm.recurreport.config.customize.time.customtype.reportperiod"/></td>
								<td colspan="2"><s:textfield name="reCmTimePeriod" id="reCmTimePeriod" size="15" maxlength="3" onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield>&nbsp;<span id="reCmTimePeriodRange"/></td>
							</tr>
							<tr>
								<td class="labelT1" height="22px"><s:text name="hm.recurreport.config.customize.time.customtype.startday"/></td>
								<td colspan="2">
									<table  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><s:radio name="cmTypeStartDay" id="cmTypeStartDay" onclick="changeCmTypeStartDay(1);" list="#{'1':''}"/><label for="cmTypeStartDay1"><s:text name="hm.recurreport.config.customize.time.customtype.day"/></label></td>
											<td><s:radio name="cmTypeStartDay" id="cmTypeStartDay" onclick="changeCmTypeStartDay(2);" list="#{'2':''}"/><label for="cmTypeStartDay2"><s:text name="hm.recurreport.config.customize.time.customtype.week"/></label></td>
											<td style="padding-left: 30px">
												<table  border="0" cellspacing="0" cellpadding="0">
													<tr id="cmTypeStartDayValueTR">
														<td>&nbsp;Start on&nbsp;<s:textfield name="cmTypeStartDayValue" id="cmTypeStartDayValue"
																size="15" maxlength="2" onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield>th Day (1-28)</td>
													</tr>
													<tr id="cmTypeStartDayWeekTR">
														<td>
															<table  border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td rowspan="2">&nbsp;Start on&nbsp;</td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'0':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'1':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'2':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'3':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'4':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'5':''}"/></td>
																	<td><s:radio name="cmTypeStartWeek" id="cmTypeStartWeek" list="#{'6':''}"/></td>
																</tr>
																<tr>
																	<td>&nbsp;&nbsp;S</td>
																	<td>&nbsp;&nbsp;M</td>
																	<td>&nbsp;&nbsp;T</td>
																	<td>&nbsp;&nbsp;W</td>
																	<td>&nbsp;&nbsp;T</td>
																	<td>&nbsp;&nbsp;F</td>
																	<td>&nbsp;&nbsp;S</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td class="labelT1"><s:text name="hm.recurreport.config.customize.time.customtype.startmonth"/></td>
								<td style="padding-right: 20px"><s:select name="cmTypeStartMonth" list="%{lstCuMonthYear}"
									id="cmTypeStartMonth" listKey="id" listValue="value" cssStyle="width:110px"
									onchange="changeCmTypeStartMonth(this.value)"/></td>
								<td id="TdSpecYearField" style="display:none;">&nbsp;Start on&nbsp;<s:textfield name="cmTypeStartSpecYear" id="cmTypeStartSpecYear"
																size="15" maxlength="4" onkeypress="return hm.util.keyPressPermit(event,'ten');"></s:textfield>&nbsp;(2010-2020)</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="labelT1" width="140px"><s:text
						name="report.reportList.emailAddress" /><font color="red"><s:text
						name="*" /></font></td>
					<td nowrap="nowrap"><s:textfield name="reEmailAddress"
						id="reEmailAddress" size="32" maxlength="128" />
						<s:text name="report.reportList.email.emailNoteRange" /></td>
				</tr>
				<tr>
					<td nowrap="nowrap"  class="noteInfo" colspan="2" style="padding-left: 10px"><s:text
							name="report.reportList.email.note" /></td>
				</tr>
				<tr>
					<td colspan="2" nowrap="nowrap" style="padding-left: 43px" class="noteInfo"><s:text
							name="report.reportList.email.emailNote" /></td>
				</tr>
				<tr>
					<td height="5px"></td>
				</tr>
			</table>
		</td>
	</tr>
<%--tr>
		<td colspan="2" style="padding: 2px 2px 2px 4px"> <s:checkbox name="enableTimeLocal" id="enableTimeLocal"></s:checkbox><s:text name="hm.dashboard.component.config.time.local"/></td>
	</tr> --%>
</table>