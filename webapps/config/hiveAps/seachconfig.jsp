<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
span#searchConfigKeyWordSection {
	display: block;
	padding-left: 12px;
	margin: 20px 0 15px;
}
span.submit {
	display: inline;
	position: absolute;
	cursor: pointer;
	width: 20px;
	height: 20px;
	margin: 3px 0 0 -20px;
}
#searchConfigKeyWord {
	background: url(images/search/Search-WarmGrey11.png) no-repeat scroll right 0 #fff;
	height: 17px;
	width: 135px;
	padding-right: 20px;
	border: 1px solid #DDDDDD;
    border-radius: 6px 6px 6px 6px;
}
#searchConfigKeyWord:focus{
	border: 1px solid #999;
	outline:none;
}
</style>
<div id="searchConfigSection" style="display: none;">
	<span id="searchConfigKeyWordSection">
		<s:textfield id="searchConfigKeyWord"
			onkeypress="return searchConfigKeyPress(event);"
			onkeydown="searchFieldInput(this);" />
		<span class="submit" onclick="configurationSearch();"></span>
	</span>
</div>
<script type="text/javascript">
function configurationSearch() {
	var searchKey = document.getElementById('searchConfigKeyWord');
	if (searchKey.value.trim().length == 0) {
		searchKey.style.backgroundColor="#FF0000";
		return;
	}
	var url = "<s:url action='search' includeParams='none' />" 
				+ "?operation=configSearch&searchKey="+encodeURIComponent(searchKey.value) 
				+ "&ignore=" + new Date().getTime();

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, 
			{ success: searchConfigResult, failure: searchConfigResult,timeout: 90000}, null);
	
	if (null == searchWaitPanel) {
		createSearchWaitPanel();
	}
	searchWaitPanel.show();
}
function searchConfigResult(o)
{
	if (searchWaitPanel!=null) {
		searchWaitPanel.hide();
	}
	
	eval("var result = " + o.responseText);
	
	if (result.success) {
		var redirect_url;
		if (result.url) {
			redirect_url = result.url;
		} else if(args[0].length == 0 || args[1].length == 0 ){
			redirect_url = "<s:url value="index.jsp" includeParams="none"/>";
		} else {
			redirect_url = "<s:url action='" + args[0] + "' includeParams="none" />" + "?operation=" + args[1];
		}
		window.location.href = redirect_url;
	} else {
		document.getElementById('searchConfigKeyWord').style.backgroundColor="#FF0000";
		if(result.errMsg) {
			showWarnDialog(result.errMsg);
		}
	}
}
function searchConfigKeyPress(e) {
	var keycode;
	if(window.event) {
		// IE
		keycode = e.keyCode;
	} else if(e.which) {
		// Netscape/Firefox/Opera
		keycode = e.which;
		if (keycode==8) {return true;} // backspace
	} else {
		return true;
	}
	
	if (keycode == 13) {
		configurationSearch();
	}

	return true;
}
</script>