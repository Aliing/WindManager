<%@ taglib prefix="s" uri="/struts-tags"%>
<style type="text/css">
#notes{position: relative;}
a.close-icon{position: absolute; display: block; width: 16px; height: 16px; top: 1px; right: 1px; background: url("<s:url value="/images/cancel.png" includeParams="none"/>") no-repeat scroll 0 0 transparent}
<s:if test="%{messagePermanent}">
.statusNote{padding-right: 20px;}
</s:if>
</style>

<script type="text/javascript">
var notesTimeoutId;
var pageNotesTimeoutId = 0;
function delayHideNotes(seconds) {
	notesTimeoutId = setTimeout("hideNotes()", seconds * 2000);  // seconds
}
function hideNotes() {
	hm.util.wipeOut('notes', 800);
}
function showProcessing() {
	hm.util.show('processing');
}
function showPageNotes(message, configs) {
	clearTimeout(pageNotesTimeoutId);
	var pageNotesElement = document.getElementById('pageNotesConent');
	pageNotesElement.innerHTML = message || '';
	configs = configs || {};
	pageNotesElement.className = configs.type == 'info' ? "noteInfo" : "noteError";
	hm.util.wipeIn('pageNotes', 300);
	pageNotesTimeoutId = setTimeout("hidePageNotes()", 10000);
}
function hidePageNotes() {
	hm.util.wipeOut('pageNotes', 600);
}

// Error messages are shown longer than info messages
function onLoadNotes() {
	<s:if test="%{actionErrors.size > 0}">
		<s:if test="%{!messagePermanent}">
		delayHideNotes(5);
		</s:if>
	</s:if>
	<s:elseif test="%{actionMessages.size > 0}">
		delayHideNotes(2);
	</s:elseif>
}
function onUnloadNotes() {
    clearTimeout(notesTimeoutId);
    clearTimeout(pageNotesTimeoutId);
}
</script>
<table width="550" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<div id="notes">
				<s:if test="%{actionErrors.size > 0}">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="statusNote">
					<tr>
						<td height="5"></td>
					</tr>
					<s:iterator id="note" value="actionErrors" status="status">
						<tr>
							<td class="noteError"><s:property escape="false" /></td>
						</tr>
					</s:iterator>
					<tr>
						<td height="6"></td>
					</tr>
				</table>
				</s:if>
				<s:elseif test="%{actionMessages.size > 0}">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="statusNote">
					<tr>
						<td height="5"></td>
					</tr>
					<s:iterator id="note" value="actionMessages" status="status">
						<tr>
							<td class="noteInfo"><s:property escape="false" /></td>
						</tr>
					</s:iterator>
					<tr>
						<td height="6"></td>
					</tr>
				</table>
				</s:elseif>
				<s:if test="%{messagePermanent}">
					<a class="close-icon" title="Close" onclick="hideNotes();" href="javascript:;"></a>
				</s:if>
			</div>
		</td>
	</tr>
	<tr>
		<td>
		<div id="processing" style="display:none">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="statusNote">
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td class="noteInfo">Your request is being processed ...</td>
			</tr>
			<tr>
				<td height="6"></td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td>
			<div id="pageNotes" style="display:none">
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				style="padding-left: 7px;">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td id="pageNotesConent" class="noteError"></td>
				</tr>
				<tr>
					<td height="6"></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
