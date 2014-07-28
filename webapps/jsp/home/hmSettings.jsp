<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.admin.HmAccessControl"%>
<%@page import="com.ah.be.common.NmsUtil"%>

<script src="<s:url value="/js/hm.options.js" includeParams="none" />"></script>

<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/fonts/fonts-min.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/resize/assets/skins/sam/resize.css" includeParams="none" />" />
<link rel="stylesheet" type="text/css"
	href="<s:url value="/yui/calendar/assets/skins/sam/calendar.css"  includeParams="none"/>" />
<script type="text/javascript"
	src="<s:url value="/yui/resize/resize-min.js" includeParams="none" />"></script>
<script type="text/javascript"
	src="<s:url value="/yui/calendar/calendar-min.js"  includeParams="none"/>"></script>
<style type="text/css">
div.yuimenu .bd {
	zoom: normal;
}

#calendarcontainer {
	padding: 10px;
}

#calendarpicker1 button {
	background: url(<s:url value ="/images/calendar_icon.gif" includeParams ="none"/>) center center no-repeat;
	margin: 2px 0; /* For IE */
	height: 1.5em; /* For IE */
}

#calendarmenu {
	position: absolute;
}

.options {
	height: 0px;
	width: 100%;
	position: absolute;
	padding: 0 10px 0px;
	top: 0px;
	left: -10px;
	background: #EEEEEE;
	overflow: hidden;
	border-bottom: 1px solid #999;
}

.settingsToolBar {
	text-align: right;
	background-color: #EEEEEE;
	height: 22px;
}

.settingCaption {
	font-size: 12px;
	font-weight: bold;
	font: solid;
	color: #003366;
	padding: 2px 10px 0px 10px;
	float: left;
}

.settingsToolBar a {
	text-decoration: none;
	color: #003366;
	margin-right: 5px;
	font-weight: bold;
}

.settingsToolBar a span {
	font-size: 12px;
	color: #99CCCC;
}

a#saveOption:hover img,a#exitOption:hover img {
	filter: alpha(opacity ="50");
	opacity: 0.5;
}

.settingsToolBar a:hover span {
	color: #003366;
}

td.showValue {
	padding: 8px 0px 5px 10px;
	vertical-align: top;
	color: #000080;
}

td.labelT2 {
	padding: 0px 0px 6px 10px;
	vertical-align: top;
	line-height: 15px;
}
</style>

<script type="text/javascript">
    function formatValue(value){
        var v=value;
        if(value.length==0)
           return v;
        if(parseInt(value)<=9)
           v="0"+value;
        return v;
    }
YAHOO.util.Event.onDOMReady(function () {
        function onButtonClick1() {

            /*
                 Create an empty body element for the Overlay instance in order
                 to reserve space to render the Calendar instance into.
            */
            oCalendarMenu.setBody("&#32;");
            oCalendarMenu.body.id = "calendarcontainer";
            // Render the Overlay instance into the Button's parent element
            oCalendarMenu.render(this.get("container"));
            // Align the Overlay to the Button instance
            oCalendarMenu.align();
            /*
                 Create a Calendar instance and render it into the body
                 element of the Overlay.
            */
            var oCalendar = new YAHOO.widget.Calendar("buttoncalendar", oCalendarMenu.body.id);
            oCalendar.render();
            /*
                Subscribe to the Calendar instance's "changePage" event to
                keep the Overlay visible when either the previous or next page
                controls are clicked.
            */
            oCalendar.changePageEvent.subscribe(function () {
                window.setTimeout(function () {

                    oCalendarMenu.show();

                }, 0);

            });

            /*
                Subscribe to the Calendar instance's "select" event to
                update the month, day, year form fields when the user
                selects a date.
            */
            oCalendar.selectEvent.subscribe(function (p_sType, p_aArgs) {
                var aDate;
                if (p_aArgs) {
                    aDate = p_aArgs[0][0];
                    var beginDate_doc = document.getElementById("dateTime");
                    beginDate_doc.value = aDate[0]+ "-" +formatValue(aDate[1]) + "-" + formatValue(aDate[2])  ;
                }
                oCalendarMenu.hide();

            });
            /*
                 Unsubscribe from the "click" event so that this code is
                 only executed once
            */
            this.unsubscribe("click", onButtonClick1);

        };

        // Create an Overlay instance to house the Calendar instance
        var oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu");

        // Create a Button instance of type "menu"
        var timeButton = new YAHOO.widget.Button({
                                            type: "menu",
                                            id: "calendarpicker1",
                                            label: "",
                                            menu: oCalendarMenu,
                                            container: "datetimeDiv" });

        /*
            Add a "click" event listener that will render the Overlay, and
            instantiate the Calendar the first time the Button instance is
            clicked.
        */
        timeButton.on("click", onButtonClick1);
	});

</script>

<script>
var formName = 'hmSettings';
var thisOperation;

var inHomeDomain = <s:property value="isInHomeDomain" />;
var locatePosition = '<s:property value="locatePosition" />';

function onLoadPage()
{
	onLoadNotes();
	
	createWaitingPanel();

	createAnimation();

	if (<s:property value="showRouteOption" />)
	{
		showRoutingOption();
	}

	if (<s:property value="showNetworkOption" />)
	{
		showNetworkOption();
	}

	if (<s:property value="showDateTimeOption" />)
	{
		showDateTimeOption();
	}

	if (<s:property value="showLoginAccessOption" />)
	{
		showLoginAccessOption();
	}

	if (<s:property value="showCertOption" />)
	{
		showCertOption();
	}

	if (<s:property value="showSSHOption" />)
	{
		showSSHOption();
	}

	if (<s:property value="showSessionOption" />)
	{
		showSessionOption();
	}

	if (<s:property value="showImproveOption" />)
	{
		showImproveOption();
	}
	
	if (<s:property value="showExpressModeOption" />)
	{
		showExpressModeOption();
	}
	
	if (<s:property value="showLogExpirationOption" />)
	{
		showLogExpirationOption();
	}
	
	if (<s:property value="showMaxUploadNumOption" />)
	{
		showMaxUpdateNumOption();
	}
	
	if (<s:property value="showGenerationThreadsOption" />)
	{
		showUpdateThreadsOption();
	}
	
	if (<s:property value="showSearchUserNumOption" />)
	{
		showUpdateSearchUserNumOption();
	}
	
    if (<s:property value="showCloudAuthServerOption" />)
    {
        showCloudAuthServerOption();
    }	
    
    if (<s:property value="showTCAAlarmOption" />)
	{
		showTCAAlarmOption();
	}
    if (<s:property value="showDeviceTagOption" />)
	{
    	showDeviceTagOption();
	}
    
    if (<s:property value="showClientProfileOption" />)
    {
        showClientProfileOption();
    }
    
    if (<s:property value="showAPIOption" />)
	{
		showAPIOption();
	}
    
    if(<s:property value="showSupplementalCLIOption"/>){
    	showSupplementalCLIOption();
    }
    
	if(locatePosition != null && locatePosition !=""){
		window.location="#"+locatePosition;
	}
}

var waitingPanel = null;
function createWaitingPanel() {
	// Initialize the temporary Panel to display while waiting for external content to load
	waitingPanel = new YAHOO.widget.Panel('wait',
			{ width:"270px",
			  fixedcenter:true,
			  close:false,
			  draggable:false,
			  zindex:4,
			  modal:true,
			  visible:false
			}
		);
	waitingPanel.setHeader("The operation is progressing...");
	waitingPanel.setBody('<img src="<s:url value="/images/waiting.gif" />" />');
	waitingPanel.render(document.body);
}

var dateTimeOptionsAnim = null;
var dateTimeSpaceAnim = null;

var networkOptionsAnim = null;
var networkSpaceAnim = null;

var routingOptionsAnim = null;
var routingSpaceAnim = null;

var loginAccessOptionsAnim = null;
var loginAccessSpaceAnim = null;

var certOptionsAnim = null;
var certSpaceAnim = null;

var sshOptionsAnim = null;
var sshSpaceAnim = null;

var sessionOptionsAnim = null;
var sessionSpaceAnim = null;

var improveOptionsAnim = null;
var improveSpaceAnim = null;

var expressModeOptionsAnim = null;
var expressModeSpaceAnim = null;

var logExpirationOptionsAnim = null;
var logExpirationSpaceAnim = null;

var maxUpdateNumOptionsAnim = null;
var maxUpdateNumSpaceAnim = null;

var generationThreadsOptionsAnim = null;
var generationThreadsSpaceAnim = null;

var maxSearchUserNumOptionsAnim = null;
var maxSearchUserNumSpaceAnim = null;

var cloudAuthServerOptionsAnim = null;
var cloudAuthServerSpaceAnim = null;

var tcaAlarmOptionsAnim = null;
var tcaAlarmSpaceAnim = null;

var customDeviceTagOptionsAnim = null;
var customDeviceTagSpaceAnim = null;

var apiOptionsAnim = null;
var apiSpaceAnim = null;

var supplementalCLIOptionsAnim = null;
var supplementalCLISpaceAnim = null;

function createAnimation(){
	dateTimeOptionsAnim = new YAHOO.util.Anim('dateTimeOptions');
	dateTimeOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	dateTimeSpaceAnim = new YAHOO.util.Anim('dateTimeSpace');

	networkOptionsAnim = new YAHOO.util.Anim('networkOptions');
	networkOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	networkSpaceAnim = new YAHOO.util.Anim('networkSpace');

	routingOptionsAnim = new YAHOO.util.Anim('routingOptions');
	routingOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	routingSpaceAnim = new YAHOO.util.Anim('routingSpace');

	loginAccessOptionsAnim = new YAHOO.util.Anim('loginAccessOptions');
	loginAccessOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	loginAccessSpaceAnim = new YAHOO.util.Anim('loginAccessSpace');

	certOptionsAnim = new YAHOO.util.Anim('certOptions');
	certOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	certSpaceAnim = new YAHOO.util.Anim('certSpace');

	sshOptionsAnim = new YAHOO.util.Anim('sshOptions');
	sshOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	sshSpaceAnim = new YAHOO.util.Anim('sshSpace');

	sessionOptionsAnim = new YAHOO.util.Anim('sessionOptions');
	sessionOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	sessionSpaceAnim = new YAHOO.util.Anim('sessionSpace');

	improveOptionsAnim = new YAHOO.util.Anim('improveOptions');
	improveOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	improveSpaceAnim = new YAHOO.util.Anim('improveSpace');
	
	expressModeOptionsAnim = new YAHOO.util.Anim('expressModeOptions');
	expressModeOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	expressModeSpaceAnim = new YAHOO.util.Anim('expressModeSpace');
	
	logExpirationOptionsAnim = new YAHOO.util.Anim('logExpirationOptions');
	logExpirationOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	logExpirationSpaceAnim = new YAHOO.util.Anim('logExpirationSpace');
	
	maxUpdateNumOptionsAnim = new YAHOO.util.Anim('maxUploadNumOptions');
	maxUpdateNumOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	maxUpdateNumSpaceAnim = new YAHOO.util.Anim('maxUploadNumSpace');
	
	generationThreadsOptionsAnim = new YAHOO.util.Anim('generationThreadsOptions');
	generationThreadsOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	generationThreadsSpaceAnim = new YAHOO.util.Anim('generationThreadsSpace');
	
	maxSearchUserNumOptionsAnim = new YAHOO.util.Anim('maxSearchUserNumOptions');
	maxSearchUserNumOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	maxSearchUserNumSpaceAnim = new YAHOO.util.Anim('maxSearchUserNumSpace');
    
    cloudAuthServerOptionsAnim = new YAHOO.util.Anim('cloudAuthServerOptions');
    cloudAuthServerOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
    cloudAuthServerSpaceAnim = new YAHOO.util.Anim('cloudAuthServerSpace');	
    
    tcaAlarmOptionsAnim = new YAHOO.util.Anim('tcaAlarmOptions');
	tcaAlarmOptionsAnim.method =YAHOO.util.Easing.easeOutStrong;
	tcaAlarmSpaceAnim = new YAHOO.util.Anim('tcaAlarmSpace');
	
	customDeviceTagOptionsAnim = new YAHOO.util.Anim('customDeviceTagOptions');
	customDeviceTagOptionsAnim.method =YAHOO.util.Easing.easeOutStrong;
	customDeviceTagSpaceAnim = new YAHOO.util.Anim('customDeviceTagSpace');
    
    clientProfileOptionsAnim = new YAHOO.util.Anim('clientProfileOptions');
	clientProfileOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	clientProfileSpaceAnim = new YAHOO.util.Anim('clientProfileSpace');	
	
	apiOptionsAnim = new YAHOO.util.Anim('apiOptions');
	apiOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	apiSpaceAnim = new YAHOO.util.Anim('apiSpace');
	
	supplementalCLIOptionsAnim = new YAHOO.util.Anim("supplementalCLIOptions");
	supplementalCLIOptionsAnim.method = YAHOO.util.Easing.easeOutStrong;
	supplementalCLISpaceAnim = new YAHOO.util.Anim('supplementalCLISpace');;
}

function dateTimeOptionsExpand(){
	var optionsHeight = inHomeDomain ? 300 : 120;
	var viewHeight = document.getElementById('dateTimeView').offsetHeight;
	var spaceHeight = optionsHeight - viewHeight;

	dateTimeOptionsAnim.stop();
	dateTimeSpaceAnim.stop();
	dateTimeOptionsAnim.attributes.height = { to: optionsHeight };
	dateTimeOptionsAnim.animate();
	dateTimeSpaceAnim.attributes.height = { to: spaceHeight };
	dateTimeSpaceAnim.duration = 0.1;
	dateTimeSpaceAnim.animate();
}

function dateTimeOptionsCollapse(){
	dateTimeOptionsAnim.stop();
	dateTimeSpaceAnim.stop();
	dateTimeOptionsAnim.attributes.height = { to: 0 };
	dateTimeOptionsAnim.animate();
	dateTimeSpaceAnim.attributes.height = { to: 0 };
	dateTimeSpaceAnim.duration = 0.1;
	dateTimeSpaceAnim.animate();
}

function networkOptionsExpand()
{
	var optionsHeight;
	var spaceHeight;

	var viewHeight = document.getElementById('networkView').offsetHeight;

	haStatus = document.getElementById("enableHA").checked;
	if(haStatus)
	{
		hm.util.show('haSection');
		hm.util.hide('normalSection');

		//optionsHeight = 520;
		optionsHeight = document.getElementById('haSection').offsetHeight + 140;
	}
	else
	{
		hm.util.show('normalSection');
		hm.util.hide('haSection');

		//optionsHeight = 330;
		optionsHeight = document.getElementById('normalSection').offsetHeight + 130;
	}

	spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	networkOptionsAnim.stop();
	networkSpaceAnim.stop();
	networkOptionsAnim.attributes.height = { to: optionsHeight };
	networkOptionsAnim.animate();
	networkSpaceAnim.attributes.height = { to: spaceHeight };
	networkSpaceAnim.duration = 0.1;
	networkSpaceAnim.animate();
}

function networkOptionsCollapse(){
	networkOptionsAnim.stop();
	networkSpaceAnim.stop();
	networkOptionsAnim.attributes.height = { to: 0 };
	networkOptionsAnim.animate();
	networkSpaceAnim.attributes.height = { to: 0 };
	networkSpaceAnim.duration = 0.1;
	networkSpaceAnim.animate();
}

function routingOptionsExpand(){
	var viewHeight = document.getElementById('routingView').offsetHeight;
	var optionsHeight = 80 + viewHeight;
	var spaceHeight = 80;

	routingOptionsAnim.stop();
	routingSpaceAnim.stop();
	routingOptionsAnim.attributes.height = { to: optionsHeight };
	routingOptionsAnim.animate();
	routingSpaceAnim.attributes.height = { to: spaceHeight };
	routingSpaceAnim.duration = 0.1;
	routingSpaceAnim.animate();
}

function routingOptionsCollapse(){
	routingOptionsAnim.stop();
	routingSpaceAnim.stop();
	routingOptionsAnim.attributes.height = { to: 0 };
	routingOptionsAnim.animate();
	routingSpaceAnim.attributes.height = { to: 0 };
	routingSpaceAnim.duration = 0.1;
	routingSpaceAnim.animate();
}

function certOptionsExpand(){
	var viewHeight = document.getElementById('certView').offsetHeight;
	var optionsHeight = 200;

	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	certOptionsAnim.stop();
	certSpaceAnim.stop();
	certOptionsAnim.attributes.height = { to: optionsHeight };
	certOptionsAnim.animate();
	certSpaceAnim.attributes.height = { to: spaceHeight };
	certSpaceAnim.duration = 0.1;
	certSpaceAnim.animate();
}

function certOptionsCollapse(){
	certOptionsAnim.stop();
	certSpaceAnim.stop();
	certOptionsAnim.attributes.height = { to: 0 };
	certOptionsAnim.animate();
	certSpaceAnim.attributes.height = { to: 0 };
	certSpaceAnim.duration = 0.1;
	certSpaceAnim.animate();
}

function sshOptionsExpand(){
	var viewHeight = document.getElementById('sshView').offsetHeight;
	var optionsHeight = 100;

	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	sshOptionsAnim.stop();
	sshSpaceAnim.stop();
	sshOptionsAnim.attributes.height = { to: optionsHeight };
	sshOptionsAnim.animate();
	sshSpaceAnim.attributes.height = { to: spaceHeight };
	sshSpaceAnim.duration = 0.1;
	sshSpaceAnim.animate();
}

function sshOptionsCollapse(){
	sshOptionsAnim.stop();
	sshSpaceAnim.stop();
	sshOptionsAnim.attributes.height = { to: 0 };
	sshOptionsAnim.animate();
	sshSpaceAnim.attributes.height = { to: 0 };
	sshSpaceAnim.duration = 0.1;
	sshSpaceAnim.animate();
}

function sessionOptionsExpand(){
	var viewHeight = document.getElementById('sessionView').offsetHeight;
	var optionsHeight = 80;

	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	sessionOptionsAnim.stop();
	sessionSpaceAnim.stop();
	sessionOptionsAnim.attributes.height = { to: optionsHeight };
	sessionOptionsAnim.animate();
	sessionSpaceAnim.attributes.height = { to: spaceHeight };
	sessionSpaceAnim.duration = 0.1;
	sessionSpaceAnim.animate();
}

function sessionOptionsCollapse(){
	sessionOptionsAnim.stop();
	sessionSpaceAnim.stop();
	sessionOptionsAnim.attributes.height = { to: 0 };
	sessionOptionsAnim.animate();
	sessionSpaceAnim.attributes.height = { to: 0 };
	sessionSpaceAnim.duration = 0.1;
	sessionSpaceAnim.animate();
}

function improveOptionsExpand(){
	var viewHeight = document.getElementById('improveView').offsetHeight;
	var optionsHeight = 80;

	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	improveOptionsAnim.stop();
	improveSpaceAnim.stop();
	improveOptionsAnim.attributes.height = { to: optionsHeight };
	improveOptionsAnim.animate();
	improveSpaceAnim.attributes.height = { to: spaceHeight };
	improveSpaceAnim.duration = 0.1;
	improveSpaceAnim.animate();
}

function improveOptionsCollapse(){
	improveOptionsAnim.stop();
	improveSpaceAnim.stop();
	improveOptionsAnim.attributes.height = { to: 0 };
	improveOptionsAnim.animate();
	improveSpaceAnim.attributes.height = { to: 0 };
	improveSpaceAnim.duration = 0.1;
	improveSpaceAnim.animate();
}

function loginAccessOptionsExpand(){
	var loginAccessView = document.getElementById('loginAccessView');
	var viewHeight = loginAccessView.offsetHeight;
	var optionsHeight = 320;

	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	var optionsToHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	loginAccessOptionsAnim.stop();
	loginAccessSpaceAnim.stop();
	loginAccessOptionsAnim.attributes.height = { to: optionsToHeight };
	loginAccessOptionsAnim.animate();
	loginAccessSpaceAnim.attributes.height = { to: spaceHeight };
	loginAccessSpaceAnim.duration = 0.1;
	loginAccessSpaceAnim.animate();
}

function loginAccessOptionsCollapse(){
	loginAccessOptionsAnim.stop();
	loginAccessSpaceAnim.stop();
	loginAccessOptionsAnim.attributes.height = { to: 0 };
	loginAccessOptionsAnim.animate();
	loginAccessSpaceAnim.attributes.height = { to: 0 };
	loginAccessSpaceAnim.duration = 0.1;
	loginAccessSpaceAnim.animate();
}

// express mode - expand & collapse
function expressModeOptionsExpand()
{
    var viewHeight = document.getElementById('expressModeView').offsetHeight;
    var optionsHeight = 40;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    expressModeOptionsAnim.stop();
    expressModeSpaceAnim.stop();
    expressModeOptionsAnim.attributes.height = { to: optionsHeight };
    expressModeOptionsAnim.animate();
    expressModeSpaceAnim.attributes.height = { to: spaceHeight };
    expressModeSpaceAnim.duration = 0.1;
    expressModeSpaceAnim.animate();
}

function expressModeOptionsCollapse()
{
	expressModeOptionsAnim.stop();
	expressModeSpaceAnim.stop();
	expressModeOptionsAnim.attributes.height = { to: 0 };
	expressModeOptionsAnim.animate();
	expressModeSpaceAnim.attributes.height = { to: 0 };
	expressModeSpaceAnim.duration = 0.1;
	expressModeSpaceAnim.animate();
}
//
// logExpiration - expand & collapse
function logExpirationOptionsExpand()
{
    var viewHeight = document.getElementById('logExpirationView').offsetHeight;
    var optionsHeight = 80;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    logExpirationOptionsAnim.stop();
    logExpirationSpaceAnim.stop();
    logExpirationOptionsAnim.attributes.height = { to: optionsHeight };
    logExpirationOptionsAnim.animate();
    logExpirationSpaceAnim.attributes.height = { to: spaceHeight };
    logExpirationSpaceAnim.duration = 0.1;
    logExpirationSpaceAnim.animate();
}

function logExpirationOptionsCollapse()
{
	logExpirationOptionsAnim.stop();
	logExpirationSpaceAnim.stop();
	logExpirationOptionsAnim.attributes.height = { to: 0 };
	logExpirationOptionsAnim.animate();
	logExpirationSpaceAnim.attributes.height = { to: 0 };
	logExpirationSpaceAnim.duration = 0.1;
	logExpirationSpaceAnim.animate();
}
//

// Max HiveOS image update number setting - expand & collapse
function maxUpdateNumOptionsExpand()
{
    var viewHeight = document.getElementById('maxUploadNumView').offsetHeight;
    var optionsHeight = 50;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    maxUpdateNumOptionsAnim.stop();
    maxUpdateNumSpaceAnim.stop();
    maxUpdateNumOptionsAnim.attributes.height = { to: optionsHeight };
    maxUpdateNumOptionsAnim.animate();
    maxUpdateNumSpaceAnim.attributes.height = { to: spaceHeight };
    maxUpdateNumSpaceAnim.duration = 0.1;
    maxUpdateNumSpaceAnim.animate();
}

function maxUpdateNumOptionsCollapse()
{
	maxUpdateNumOptionsAnim.stop();
	maxUpdateNumSpaceAnim.stop();
	maxUpdateNumOptionsAnim.attributes.height = { to: 0 };
	maxUpdateNumOptionsAnim.animate();
	maxUpdateNumSpaceAnim.attributes.height = { to: 0 };
	maxUpdateNumSpaceAnim.duration = 0.1;
	maxUpdateNumSpaceAnim.animate();
}
//
function clickDateTimeSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showDateTimeOption.value = true;
	document.forms[formName].locatePosition.value = "dateTimeSaveDiv";
    document.forms[formName].submit();
}

function showDateTimeOption(){
	document.getElementById("dateTimeSettingDiv").style.display = "none";
	document.getElementById("dateTimeSaveDiv").style.display = "";
	// fix select component cannot be mask issue in IE6
	if(YAHOO.env.ua.ie == 6){
		//TODO
	}
	dateTimeOptionsExpand();
}

function saveDateTimeOption(){
	if(!validateDateTimeConfig()){
		return;
	}

	var isInHomeDomain = <s:property value="isInHomeDomain" />;
	var confirmMessage;
   	if (isInHomeDomain)
   	{
   		confirmMessage = '<s:text name="home.hmSettings.save.confirm" />';
   	}
   	else
   	{
		confirmMessage = '<s:text name="admin.hmOperation.continue.confirm" />';
   	}

	thisOperation = 'updateDateTime';
	confirmDialog.cfg.setProperty('text', confirmMessage);
	confirmDialog.show();
}

function doContinueOper()
{
	if (thisOperation == 'updateDateTime')
	{
		url = "<s:url action='hmSettings' includeParams='none'/>" + "?ignore=" + new Date().getTime();
		document.forms[formName].operation.value = 'updateDateTime';
		YAHOO.util.Connect.setForm(document.getElementById(formName));

		if(waitingPanel != null)
		{
			waitingPanel.show();
		}
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:updateDateTimeResult,timeout: 120000}, null);
	}else
	    submitAction(thisOperation);
}

function updateDateTimeResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var details = " + o.responseText);
	if(details.succ){
		exitDateTimeOption();
		showHangMessage(details.message);

		if (details.currentTime)
		{
			document.getElementById("currentTimeTd").innerHTML = "<td id='currentTimeTd'>["+details.currentTime+"]</td>";
			document.getElementById("currentTimeTd").className="showValue";
		}

		if (details.restart)
		{
			url = "<s:url action='hmSettings' includeParams='none'/>?operation=restartHM&ignore=" + new Date().getTime();
			YAHOO.util.Connect.asyncRequest('POST', url, {}, null);
		}
	}else{
		showErrorMessage(details.message);
	}
}

function exitDateTimeOption(){
	document.forms[formName].showDateTimeOption.value = false;

	document.getElementById("dateTimeSettingDiv").style.display = "";
	document.getElementById("dateTimeSaveDiv").style.display = "none";
	dateTimeOptionsCollapse();
}

function clickNetworkSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showNetworkOption.value = true;
	document.forms[formName].locatePosition.value = "networkSaveDiv";
    document.forms[formName].submit();
}

function showNetworkOption()
{
	if (inHomeDomain)
	{
		haStatus = document.getElementById("enableHA").checked;

		if(haStatus)
		{
			hm.util.show('haSection');
			hm.util.hide('normalSection');
		}
		else
		{
			hm.util.show('normalSection');
			hm.util.hide('haSection');
		}
	}

	document.getElementById("networkSettingDiv").style.display = "none";
	document.getElementById("networkSaveDiv").style.display = "";
	networkOptionsExpand();
}
var _warnDialog;
function showBreakHa() {
	if (_warnDialog == null) {
		_warnDialog = new YAHOO.widget.SimpleDialog("warnDialog", {
			width : "350px",
			fixedcenter : true,
			visible : false,
			draggable : true,
			modal : true,
			close : true,
			icon : YAHOO.widget.SimpleDialog.ICON_ALARM,
			constraintoviewport : true,
			buttons : [ {
				text : "OK",
				handler : _handleYes,
				isDefault : true
			} ]
		});
		_warnDialog.setHeader("Warning");
		_warnDialog.render(document.body);
	}
	_warnDialog.cfg.setProperty('text', '<s:text name="home.hmSettings.ha.break.confirm" />');
	_warnDialog.show();
}
function _handleYes(){
	this.hide();
	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showNetworkOption.value = true;
	document.forms[formName].locatePosition.value = "networkSaveDiv";
    document.forms[formName].submit();
	//continueExe();
	
}
function continueExe(){
	var confirms = new Array();
    var index = 0;

	thisOperation = 'updateNetwork';
    if (!haStatus && document.getElementById("enableHA").checked)
    {
        confirms[index] = '<s:text name="home.hmSettings.ha.warning0" />';
        index++;
    }
    else if (haStatus && !document.getElementById("enableHA").checked)
    {
    	confirms[index] = '<s:text name="home.hmSettings.ha.warning" />';
        index++;
    }

    confirmUpdate(confirms);
}
function saveNetworkOption()
{
	if (!validateNetwork())
	{
		return;
	}

	if (haStatus && !document.getElementById("enableHA").checked)
    {				 
		<s:if test="!enableExternalDb && switchOverDb">
		showBreakHa();
    	</s:if>
    	<s:else>
    	continueExe();
    	</s:else>
    }else{
    	continueExe();
    }
}

function confirmUpdate(confirms,title)
{
	var titleMsg = "";
	if (confirms.length == 0)
	{
		titleMsg = '<s:text name="admin.hmOperation.continue.confirm" />';
	} else if (confirms.length == 1)
	{
		titleMsg += confirms[0]+"<br>";

		titleMsg = titleMsg+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?"
	} else if (confirms.length > 1)
	{
		titleMsg += "1. "+confirms[0]+"<br>"
		for (i = 1;i<confirms.length;i++)
		{
			titleMsg += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ (i+1) +". "+confirms[i]+"<br>"
		}

		titleMsg = titleMsg+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Do you want to continue?"
	}
	if(title){
		confirmDialog.setHeader(title);
	}

	confirmDialog.cfg.setProperty('text',titleMsg);
	confirmDialog.show();
}

function exitNetworkOption()
{
	document.forms[formName].showNetworkOption.value = false;

	document.getElementById("networkSettingDiv").style.display = "";
	document.getElementById("networkSaveDiv").style.display = "none";
	networkOptionsCollapse();
}

function clickRoutingSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showRouteOption.value = true;
	document.forms[formName].locatePosition.value = 'routingCancelDiv';
    document.forms[formName].submit();
}

function showRoutingOption()
{
	document.getElementById("routingSettingDiv").style.display = "none";
	document.getElementById("routingCancelDiv").style.display = "";
	routingOptionsExpand();
}

function exitRoutingOption()
{
	document.forms[formName].showRouteOption.value = false;

	document.getElementById("routingSettingDiv").style.display = "";
	document.getElementById("routingCancelDiv").style.display = "none";
	routingOptionsCollapse();
}

function clickCertSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showCertOption.value = true;
    document.forms[formName].submit();
}

function showCertOption()
{
	document.getElementById("certSettingDiv").style.display = "none";
	document.getElementById("certSaveDiv").style.display = "";
	certOptionsExpand();
}

function exitCertOption()
{
	document.forms[formName].showCertOption.value = false;

	document.getElementById("certSettingDiv").style.display = "";
	document.getElementById("certSaveDiv").style.display = "none";
	certOptionsCollapse();
}

function saveCertOption()
{
	if (!validateCert())
	{
		return;
	}

	var confirms = new Array();
    var index = 0;
    confirms[index] = '<s:text name="warn.admin.management.update.https" />';

	thisOperation = 'updateCert';
	var title = 'Warning';
    confirmUpdate(confirms,title);
}

function validateCert()
{
	if (document.getElementById("importCert").checked)
	{
		var certificateFile = document.getElementById("certificateFile");
		if ( certificateFile.value.length == 0)
		{
	        hm.util.reportFieldError(certificateFile, '<s:text name="error.requiredField"><s:param><s:text name="admin.installCert.certificate" /></s:param></s:text>');
	        certificateFile.focus();
	        return false;
	  	}

		var privateKey = document.getElementById("privateKey");
		if ( privateKey.value.length == 0)
		{
	        hm.util.reportFieldError(privateKey, '<s:text name="error.requiredField"><s:param><s:text name="admin.installCert.privateKey" /></s:param></s:text>');
	        privateKey.focus();
	        return false;
	  	}

	  	var passPhrase;
		var confirmPassphrase;
		if (document.getElementById("chkToggleDisplay_cert").checked)
		{
			passPhrase = document.getElementById("certPassPhrase");
			confirmPassphrase = document.getElementById("confirmCertPass");
		}
		else
		{
			passPhrase = document.getElementById("certPassPhrase_text");
			confirmPassphrase = document.getElementById("confirmCertPass_text");
		}

		if ( passPhrase.value.length == 0)
		{
	        hm.util.reportFieldError(passPhrase, '<s:text name="error.requiredField"><s:param><s:text name="admin.installCert.passphrase" /></s:param></s:text>');
	        passPhrase.focus();
	        return false;
	  	}

		if ( confirmPassphrase.value.length == 0)
		{
	        hm.util.reportFieldError(confirmPassphrase, '<s:text name="error.requiredField"><s:param><s:text name="admin.installCert.confirmPassphrase" /></s:param></s:text>');
	        confirmPassphrase.focus();
	        return false;
	  	}
	  	else if (confirmPassphrase.value.valueOf() != passPhrase.value.valueOf() )
	  	{
	  		hm.util.reportFieldError(confirmPassphrase, '<s:text name="error.notEqual"><s:param><s:text name="admin.installCert.confirmPassphrase" /></s:param><s:param><s:text name="admin.installCert.passphrase" /></s:param></s:text>');
	        confirmPassphrase.focus();
	        return false;
	  	}
	}

	return true;
}

function clickSSHSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showSSHOption.value = true;
	document.forms[formName].locatePosition.value = "sshSaveDiv";
    document.forms[formName].submit();
}

function showSSHOption()
{
	document.getElementById("sshSettingDiv").style.display = "none";
	document.getElementById("sshSaveDiv").style.display = "";
	sshOptionsExpand();
}

function exitSSHOption()
{
	document.forms[formName].showSSHOption.value = false;

	document.getElementById("sshSettingDiv").style.display = "";
	document.getElementById("sshSaveDiv").style.display = "none";
	sshOptionsCollapse();
}

function saveSSHOption()
{
	if (!validateSSH())
	{
		return;
	}

	submitAction('updateSSH');
}

function validateSSH()
{
	var sshPortNumber = document.getElementById("sshPortNumber");

	if ( sshPortNumber.value.length == 0)
	{
        hm.util.reportFieldError(sshPortNumber, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.portNumber" /></s:param></s:text>');
        sshPortNumber.focus();
        return false;
  	}
  	else if (!isValidSSHPort(sshPortNumber.value))
    {
		hm.util.reportFieldError(sshPortNumber, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.portNumber" /></s:param><s:param><s:text name="admin.management.sshPortRange" /></s:param></s:text>');
		sshPortNumber.focus();
		return false;
	}

	return true;
}

function isValidSSHPort(value)
{
	var intValue = value.valueOf();
	if ( intValue == 22 || (intValue >=1025 && intValue <= 65535) )
	{
		return true;
	}

	return false;
}

function clickSessionSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showSessionOption.value = true;
	document.forms[formName].locatePosition.value = "sessionSaveDiv";
    document.forms[formName].submit();
}

function showSessionOption()
{
	document.getElementById("sessionSettingDiv").style.display = "none";
	document.getElementById("sessionSaveDiv").style.display = "";
	sessionOptionsExpand();
}

function exitSessionOption()
{
	document.forms[formName].showSessionOption.value = false;

	document.getElementById("sessionSettingDiv").style.display = "";
	document.getElementById("sessionSaveDiv").style.display = "none";
	sessionOptionsCollapse();
}

function saveSessionOption()
{
	if (!validateSession())
	{
		return;
	}

	submitAction('updateSession');
}

function saveAPIOption()
{
	if(document.getElementById("enableApiAccess").checked){
	  if (!validateAPI())
		{
			return;
	  }
	}
	submitAction('updateAPISettings');
}

function validateAPI()
{
	var userName = document.getElementById("apiUserName");
	var password;
	var confirmPassword;
	if (document.getElementById("chkAPIToggleDisplay").checked){
		password = document.getElementById("apiPassword");
		confirmPassword = document.getElementById("passwordConfirm");
	}else{
		password = document.getElementById("apiPassword_text");
		confirmPassword = document.getElementById("passwordConfirm_text");
	}
	
	if (userName.value.length == 0){
		hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.api.userName" /></s:param></s:text>');
		userName.focus();
        return false;
  	}
	
	if(<s:property value="%{addOperation}"/>){
		if(!document.getElementById('changePassword').checked){
			document.getElementById('changePassword').checked=true;
			changeUserPassword(true);
			password.value="";
			confirmPassword.value="";
		}
	}
	
    if(document.getElementById('changePassword').checked){
    	if (password.value.length == 0){
    		hm.util.reportFieldError(password, '<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.api.password" /></s:param></s:text>');
    		password.focus();
            return false;
      	}
    	if (confirmPassword.value!=password.value) {
            hm.util.reportFieldError(password, '<s:text name="error.passwordConfirm"></s:text>');
            password.focus();
            return false;
        }
	}else{
		password.value="";
	}
	return true;
}

function clickAPISetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showAPIOption.value = true;
	document.forms[formName].locatePosition.value = "apiSaveDiv";
    document.forms[formName].submit();
}

function showAPIOption()
{
	document.getElementById("APISettingDiv").style.display = "none";
	document.getElementById("apiSaveDiv").style.display = "";
	apiOptionsExpand(100);
}

function exitAPIOption()
{
	document.forms[formName].showAPIOption.value = false;

	document.getElementById("APISettingDiv").style.display = "";
	document.getElementById("apiSaveDiv").style.display = "none";
	APIOptionsCollapse(0,0);
}

function APIOptionsCollapse(optionsHeight,spaceHeight){
	apiOptionsAnim.stop();
	apiOptionsAnim.stop();
	apiOptionsAnim.attributes.height = { to: optionsHeight };
	apiOptionsAnim.animate();
	apiSpaceAnim.attributes.height = { to: spaceHeight };
	apiSpaceAnim.duration = 0.1;
	apiSpaceAnim.animate();
}

function apiOptionsExpand(optionsHeight){
	var viewHeight = document.getElementById('apiView').offsetHeight;
	var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
	optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

	apiOptionsAnim.stop();
	apiSpaceAnim.stop();
	apiOptionsAnim.attributes.height = { to: optionsHeight };
	apiOptionsAnim.animate();
	apiSpaceAnim.attributes.height = { to: spaceHeight };
	apiSpaceAnim.duration = 0.1;
	apiSpaceAnim.animate();
}

function changeUserPassword(checked)
{
	if(checked) {
		apiOptionsExpand(200);
		hm.util.show('passwordSection');
	} else {
		APIOptionsCollapse(125,0);
		hm.util.hide('passwordSection');
	}
}
function changeApiUserInfoStyle(checked){
	if(checked) {
		hm.util.show('apiUserInfo');
	} else {
		hm.util.hide('apiUserInfo');
	}
}

function validateSession()
{
	if (document.getElementById("finiteSession").checked)
	{
		var sessionExpiration = document.getElementById("sessionExpiration");
		if ( sessionExpiration.value.length == 0)
		{
	        hm.util.reportFieldError(sessionExpiration, '<s:text name="error.requiredField"><s:param><s:text name="admin.management.adminSessionUpdate" /></s:param></s:text>');
	        sessionExpiration.focus();
	        return false;
	  	}
	  	else if (!isValidExpiration(sessionExpiration.value))
	    {
			hm.util.reportFieldError(sessionExpiration, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.management.adminSessionUpdate" /></s:param><s:param><s:text name="admin.management.adminSessioRange" /></s:param></s:text>');
			sessionExpiration.focus();
			return false;
		}
	}

	return true;
}

function isValidExpiration(expiration)
{
	var intValue = expiration.valueOf();
	if ( intValue >=5 && intValue <= 120 )
	{
		return true;
	}

	return false;
}

function clickImproveSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showImproveOption.value = true;
	document.forms[formName].locatePosition.value = "improveSaveDiv";
    document.forms[formName].submit();
}

function showImproveOption()
{
	document.getElementById("improveSettingDiv").style.display = "none";
	document.getElementById("improveSaveDiv").style.display = "";
	improveOptionsExpand();
}

function exitImproveOption()
{
	document.forms[formName].showImproveOption.value = false;

	document.getElementById("improveSettingDiv").style.display = "";
	document.getElementById("improveSaveDiv").style.display = "none";
	improveOptionsCollapse();
}

function saveImproveOption()
{
	submitAction('updateImprove');
}
// express Mode - click & show & exit & save
function clickExpressModeSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showExpressModeOption.value = true;
    document.forms[formName].submit();  
}

function showExpressModeOption()
{
    document.getElementById("expressModeSettingDiv").style.display = "none";
    document.getElementById("expressModeSaveDiv").style.display = "";
    expressModeOptionsExpand(); 
}

function exitExpressModeOption()
{
    document.forms[formName].showExpressModeOption.value = false;

    document.getElementById("expressModeSettingDiv").style.display = "";
    document.getElementById("expressModeSaveDiv").style.display = "none";
    expressModeOptionsCollapse();
}

function saveExpressModeOption()
{
    submitAction('updateExpressMode');
}
//
function selectSSHKeyGen(checked)
{
	document.getElementById("genAlgorithm").disabled=!checked;
}

function addRoute()
{
	if (!validateAddRoute())
	{
		return;
	}

	url = "<s:url action='hmSettings' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'checkRouteValid';
	YAHOO.util.Connect.setForm(document.getElementById(formName));

	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:checkRouteResult}, null);

	if(waitingPanel != null)
	{
		waitingPanel.show();
	}
}

function removeRoute()
{
	thisOperation = 'removeRoute';
	hm.util.checkAndConfirmDelete();
}

function checkRouteResult(o)
{
	if(waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var details = " + o.responseText);
	if(details.valid){
		submitAction('addRoute');
	}else{
		showErrorMessage(details.message);
	}
}

function clickLoginAccessSetting()
{
	showProcessing();

	document.forms[formName].operation.value = "refresh";
	document.forms[formName].showLoginAccessOption.value = true;
	document.forms[formName].locatePosition.value = "loginAccessSaveDiv";
    document.forms[formName].submit();
}

function showLoginAccessOption()
{
	document.getElementById("loginAccessSettingDiv").style.display = "none";
	document.getElementById("loginAccessSaveDiv").style.display = "";
	loginAccessOptionsExpand();
}

function saveLoginAccessOption()
{
	if (!validateLoginAccess())
	{
		return;
	}

	hm.options.selectAllOptions("allowedIps");
    hm.options.selectAllOptions("deniedIps");
	submitAction('updateAccessControl');
}

function exitLoginAccessOption()
{
	document.forms[formName].showLoginAccessOption.value = false;

	document.getElementById("loginAccessSettingDiv").style.display = "";
	document.getElementById("loginAccessSaveDiv").style.display = "none";
	loginAccessOptionsCollapse();
}

function submitAction(operation)
{
    if (validate(operation))
	{
		document.forms[formName].operation.value = operation;
		try
		{
			document.forms[formName].submit();
			
			if(waitingPanel != null)
			{
				waitingPanel.show();
			}
		}
		catch(e)
		{
			if (e instanceof Error && e.name == "TypeError")
			{
				var certificateFile = document.getElementById("certificateFile");
				hm.util.reportFieldError(certificateFile, 'Please select valid file!');
			}
		}
	}
}

function validateDateTimeConfig()
{
	if (document.getElementById(formName + '_syncModesyncNTP').checked)
	{
		var ntpServer = document.getElementById("ntpServer");
		if ( ntpServer.value.length == 0)
		{
            hm.util.reportFieldError(ntpServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.timeSet.ntpServer" /></s:param></s:text>');
            ntpServer.focus();
            return false;
   	 	}
   	 	var ntpInterval = document.getElementById("ntpInterval");
		if ( ntpInterval.value.length == 0)
		{
            hm.util.reportFieldError(ntpInterval, '<s:text name="error.requiredField"><s:param><s:text name="admin.timeSet.syncInterval" /></s:param></s:text>');
            ntpInterval.focus();
            return false;
   	 	}
   	 	var message = hm.util.validateIntegerRange(ntpInterval.value, '<s:text name="admin.timeSet.syncInterval" />',60,10080);
	    if (message != null) {
	        hm.util.reportFieldError(ntpInterval, message);
	        ntpInterval.focus();
	        return false;
	    }
	}

	return true;
}

function validateAddRoute()
{
	var dest = document.getElementById("routeDest");
	var mask = document.getElementById("routeMask");
	var gateway = document.getElementById("routeGateway");

	if (dest.value.length == 0) {
            hm.util.reportFieldError(dest, '<s:text name="error.requiredField"><s:param><s:text name="admin.routing.destination" /></s:param></s:text>');
            dest.focus();
            return false;
    } else if (! hm.util.validateIpAddress(dest.value)) {
		hm.util.reportFieldError(dest, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.routing.destination" /></s:param></s:text>');
		dest.focus();
		return false;
	}

	if (mask.value.length == 0) {
            hm.util.reportFieldError(mask, '<s:text name="error.requiredField"><s:param><s:text name="admin.routing.mask" /></s:param></s:text>');
            mask.focus();
            return false;
    } else if (! hm.util.validateMask(mask.value)) {
		hm.util.reportFieldError(mask, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.routing.mask" /></s:param></s:text>');
		mask.focus();
		return false;
	}

	if (gateway.value.length == 0) {
            hm.util.reportFieldError(gateway, '<s:text name="error.requiredField"><s:param><s:text name="admin.routing.gateway" /></s:param></s:text>');
            gateway.focus();
            return false;
    } else if (! hm.util.validateIpAddress(gateway.value)) {
		hm.util.reportFieldError(gateway, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.routing.gateway" /></s:param></s:text>');
		gateway.focus();
		return false;
	}

	return true;
}

function validateLoginAccess()
{
	var typeCbs = document.getElementsByName("accessControlType");
	for(var i=0; i<typeCbs.length; i++){
		var typeCb = typeCbs[i];
		if(typeCb.checked && typeCb.value == <%=HmAccessControl.CONTROL_TYPE_PERMIT%>){
			var allowIps = document.getElementById('allowedIps');
			if(allowIps.length < 1){
				hm.util.reportFieldError(allowIps, '<s:text name="error.pleaseAddItems"></s:text>');
				showAllowCreateSection();
				return false;
			}
		}
	}
	return true;
}

function validateNetwork()
{
	var enableHA = document.getElementById("enableHA").checked;
	if (enableHA)
	{
		return validateHANetwork();
	}
	else
	{
		return validateNormalNetwork();
	}
}

function validateNormalNetwork()
{
	var hostName = document.getElementById("hostName");
	var ip_eth0 = document.getElementById("ip_eth0");
	var mask_eth0 = document.getElementById("mask_eth0");
	if (!<%=NmsUtil.isHostedHMApplication()%>)
	{
		var ip_eth1 = document.getElementById("ip_eth1");
		var mask_eth1 = document.getElementById("mask_eth1");
	}
	var gateway = document.getElementById("defaultGateway");
	var primaryDNS = document.getElementById("primaryDNS");
	var secondDNS = document.getElementById("secondDNS");
	var tertiaryDNS = document.getElementById("tertiaryDNS");

	//hostname
	if (hostName.value.length == 0) {
        hm.util.reportFieldError(hostName, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.hostname" /></s:param></s:text>');
        hostName.focus();
        return false;
    }

    //MGT ip&mask
    if (ip_eth0.value.length == 0)
    {
        hm.util.reportFieldError(ip_eth0, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
        ip_eth0.focus();
        return false;
    }
    else if (!hm.util.validateIpAddress(ip_eth0.value))
	{
		hm.util.reportFieldError(ip_eth0, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
		ip_eth0.focus();
		return false;
	}

	if (mask_eth0.value.length == 0)
	{
		hm.util.reportFieldError(mask_eth0, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.mgtInterfaceMask" /></s:param></s:text>');
		mask_eth0.focus();
		return false;
    }
    else if (!hm.util.validateMask(mask_eth0.value))
    {
		hm.util.reportFieldError(mask_eth0, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceMask" /></s:param></s:text>');
		mask_eth0.focus();
		return false;
	}

	//LAN ip&mask
	if (!<%=NmsUtil.isHostedHMApplication()%>)
	{
		var enableLan = document.getElementById("cbEnableLan");
		if (enableLan.checked)
		{
			if (ip_eth1.value.length == 0)
		    {
		        hm.util.reportFieldError(ip_eth1, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
		        ip_eth1.focus();
		        return false;
		    }
		    else if (!hm.util.validateIpAddress(ip_eth1.value))
			{
				hm.util.reportFieldError(ip_eth1, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
				ip_eth1.focus();
				return false;
			}

			if (mask_eth1.value.length == 0)
			{
				hm.util.reportFieldError(mask_eth1, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.lanInterfaceMask" /></s:param></s:text>');
				mask_eth1.focus();
				return false;
		    }
		    else if (!hm.util.validateMask(mask_eth1.value))
		    {
				hm.util.reportFieldError(mask_eth1, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.lanInterfaceMask" /></s:param></s:text>');
				mask_eth1.focus();
				return false;
			}
		}
	}

	//gateway
	if (gateway.value.length == 0) {
            hm.util.reportFieldError(gateway, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
            gateway.focus();
            return false;
    } else if (! hm.util.validateIpAddress(gateway.value)) {
		hm.util.reportFieldError(gateway, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
		gateway.focus();
		return false;
	}
	
	//database setting
	if (!<%=NmsUtil.isHostedHMApplication()%>)
	{
		if (document.getElementById("enableSeparateExternalDb").checked){
			var remoteDbIp = document.getElementById("remoteDbIp");
			var remoteDbSshPwd;
			if (document.getElementById("chkToggleDisplay_RemoteDb").checked)
			{
				remoteDbSshPwd = document.getElementById("remoteDbSshPwd");
			}
			else
			{
				remoteDbSshPwd = document.getElementById("remoteDbSshPwd_text");
			}
			
			if (remoteDbIp.value.length == 0) {
	            hm.util.reportFieldError(remoteDbIp, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
	            remoteDbIp.focus();
	            return false;
		    }else if(hm.util.trim(remoteDbIp.value) == "127.0.0.1"){
                 hm.util.reportFieldError(remoteDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
            	 remoteDbIp.focus();
            	 return false;
	        }else if(! hm.util.validateIpAddress(remoteDbIp.value)){
                hm.util.reportFieldError(remoteDbIp,  '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
           		remoteDbIp.focus();
           		return false;
		    }else if(remoteDbIp.value.valueOf() == ip_eth0.value.valueOf()){
		    	hm.util.reportFieldError(remoteDbIp,  '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
        		remoteDbIp.focus();
        		return false;
		    }
			
			if (remoteDbSshPwd.value.length == 0) {
	            hm.util.reportFieldError(remoteDbSshPwd, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.ssh.password" /></s:param></s:text>');
	            remoteDbSshPwd.focus();
	            return false;
		    } 
		}
	}
	//DNS
	if (primaryDNS.value.length > 0 && ! hm.util.validateIpAddress(primaryDNS.value)) {
		hm.util.reportFieldError(primaryDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.primaryDns" /></s:param></s:text>');
		primaryDNS.focus();
		return false;
	}

	if (secondDNS.value.length > 0 && ! hm.util.validateIpAddress(secondDNS.value)) {
		hm.util.reportFieldError(secondDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.secondDns" /></s:param></s:text>');
		secondDNS.focus();
		return false;
	}

	if (tertiaryDNS.value.length > 0 && ! hm.util.validateIpAddress(tertiaryDNS.value)) {
		hm.util.reportFieldError(tertiaryDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.thirdDns" /></s:param></s:text>');
		tertiaryDNS.focus();
		return false;
	}

	var enableProxy = document.getElementById("enableProxy");
	if (enableProxy.checked) {
		var proxyServer = document.getElementById("proxyServer");
		var proxyPort = document.getElementById("proxyPort");

		if (proxyServer.value.length == 0) {
	        hm.util.reportFieldError(proxyServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.proxyServer" /></s:param></s:text>');
	        proxyServer.focus();
	        return false;
	    }

		if (proxyPort.value.length == 0) {
	        hm.util.reportFieldError(proxyPort, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.proxyPort" /></s:param></s:text>');
	        proxyPort.focus();
	        return false;
	    }
		else if (!isValidPort(proxyPort) )
		{
			hm.util.reportFieldError(proxyPort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.interface.proxyPort" /></s:param><s:param><s:text name="admin.backupDB.portRange" /></s:param></s:text>');
			proxyPort.focus();
			return false;
		}
	}

	return true;
}

function isValidPort(port)
{
	var intValue = parseInt(port.value);
	if ( intValue>=1 && intValue <= 65535 )
	{
		return true;
	}

	return false;
}

function validateHANetwork()
{
	if (<%=NmsUtil.isHostedHMApplication()%>)
	{
		if (!validateHADNS())
		{
			return false;
		}
		
		if (document.getElementById("haEmailSection")) {
			var emailAddr = document.getElementById("haNotifyEmail");
		    if (emailAddr.value.length > 0
		    		&& !hm.util.validateEmail(emailAddr.value))
			{
				hm.util.reportFieldError(emailAddr, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.ha.email" /></s:param></s:text>');
				emailAddr.focus();
				return false;
			}
		}

		return true;
	}

	var primaryHostName = document.getElementById("primaryHostName");
	//var secondaryHostName = document.getElementById("secondaryHostName");
	var primaryMGTIP = document.getElementById("primaryMGTIP");
	var primaryMGTMask = document.getElementById("primaryMGTMask");
	var primaryGateway = document.getElementById("primaryGateway");
	var secondaryMGTIP = document.getElementById("secondaryMGTIP");
	var primaryLANIP = document.getElementById("primaryLANIP");
	var primaryLANMask = document.getElementById("primaryLANMask");
	var secondaryLANIP = document.getElementById("secondaryLANIP");

	//primaryHostName
	if (primaryHostName.value.length == 0) {
        hm.util.reportFieldError(primaryHostName, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.hostname" /></s:param></s:text>');
        primaryHostName.focus();
        return false;
    }

    //primaryMGTIP
    if (primaryMGTIP.value.length == 0)
    {
        hm.util.reportFieldError(primaryMGTIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
        primaryMGTIP.focus();
        return false;
    }
    else if (!hm.util.validateIpAddress(primaryMGTIP.value))
	{
		hm.util.reportFieldError(primaryMGTIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
		primaryMGTIP.focus();
		return false;
	}else if(hm.util.trim(primaryMGTIP.value) == "127.0.0.1"){
		hm.util.reportFieldError(primaryMGTIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
		primaryMGTIP.focus();
        return false;
	}

	//primaryMGTMask
    if (primaryMGTMask.value.length == 0)
    {
        hm.util.reportFieldError(primaryMGTMask, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.mgtInterfaceMask" /></s:param></s:text>');
        primaryMGTMask.focus();
        return false;
    }
    else if (!hm.util.validateIpAddress(primaryMGTMask.value))
	{
		hm.util.reportFieldError(primaryMGTMask, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceMask" /></s:param></s:text>');
		primaryMGTMask.focus();
		return false;
	}

	//primaryGateway
    if (primaryGateway.value.length == 0)
    {
        hm.util.reportFieldError(primaryGateway, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
        primaryGateway.focus();
        return false;
    }
    else if (!hm.util.validateIpAddress(primaryGateway.value))
	{
		hm.util.reportFieldError(primaryGateway, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.defaultGateway" /></s:param></s:text>');
		primaryGateway.focus();
		return false;
	}

	//secondaryMGTIP
    if (secondaryMGTIP.value.length == 0)
    {
        hm.util.reportFieldError(secondaryMGTIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
        secondaryMGTIP.focus();
        return false;
    }
    else if (!hm.util.validateIpAddress(secondaryMGTIP.value))
	{
		hm.util.reportFieldError(secondaryMGTIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
		secondaryMGTIP.focus();
		return false;
	}
    else if (secondaryMGTIP.value.valueOf() == primaryMGTIP.value.valueOf())
	{
		hm.util.reportFieldError(secondaryMGTIP, '<s:text name="error.equal"><s:param><s:text name="home.hmsettings.secondary.node" />  <s:text name="admin.ha.lanInterfaceIP" /></s:param><s:param><s:text name="home.hmsettings.primary.node" />  <s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
        secondaryMGTIP.focus();
        return false;
	}else if(hm.util.trim(secondaryMGTIP.value) == "127.0.0.1"){
		hm.util.reportFieldError(secondaryMGTIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
        secondaryMGTIP.focus();
        return false;
	}

	//lan port validation lan setting
	if (document.getElementById("haPortlan").checked)
	{
		if (primaryLANIP.value.length == 0)
        {
            hm.util.reportFieldError(primaryLANIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
            primaryLANIP();
            return false;
        }

		if (primaryLANMask.value.length == 0)
        {
            hm.util.reportFieldError(primaryLANMask, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.lanInterfaceMask" /></s:param></s:text>');
            primaryLANMask();
            return false;
        }

		if (secondaryLANIP.value.length == 0)
        {
            hm.util.reportFieldError(secondaryLANIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
            secondaryLANIP();
            return false;
        }
		
		//primaryLANIP
	    if (primaryLANIP.value.length > 0 && !hm.util.validateIpAddress(primaryLANIP.value))
	   	{
	   		hm.util.reportFieldError(primaryLANIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
	   		primaryLANIP.focus();
	   		return false;
	   	}

	   	//primaryLANMask
	       if (primaryLANMask.value.length > 0 && !hm.util.validateIpAddress(primaryLANMask.value))
	   	{
	   		hm.util.reportFieldError(primaryLANMask, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.lanInterfaceMask" /></s:param></s:text>');
	   		primaryLANMask.focus();
	   		return false;
	   	}

	   	//secondaryLANIP
	   	if (secondaryLANIP.value.length > 0)
	   	{
	   		if (!hm.util.validateIpAddress(secondaryLANIP.value))
	       	{
	       		hm.util.reportFieldError(secondaryLANIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
	       		secondaryLANIP.focus();
	       		return false;
	       	}
	       	else if (secondaryLANIP.value.valueOf() == primaryLANIP.value.valueOf())
	       	{
	       		hm.util.reportFieldError(secondaryLANIP, '<s:text name="error.equal"><s:param><s:text name="home.hmsettings.secondary.node" />  <s:text name="admin.ha.lanInterfaceIP" /></s:param><s:param><s:text name="home.hmsettings.primary.node" />  <s:text name="admin.ha.lanInterfaceIP" /></s:param></s:text>');
	               secondaryLANIP.focus();
	               return false;
	       	}
	      }
	}
	
   	//secondary node ssh pwd
	var haSecretPwd;
	if (document.getElementById("chkToggleDisplay").checked)
	{
		haSecretPwd = document.getElementById("haSecret");
	}
	else
	{
		haSecretPwd = document.getElementById("haSecret_text");
	}
	
	if (haSecretPwd.value.length == 0) {
        hm.util.reportFieldError(haSecretPwd, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.ssh.password" /></s:param></s:text>');
        haSecretPwd.focus();
        return false;
    } 
  	//secondaryGateway
	if (!document.getElementById("haPortlan").checked)
	{
	    var enableExternalIP = document.getElementById("enableExternalIP");
	    if (enableExternalIP.checked)
	    {
	    	var primaryExternalIP = document.getElementById("primaryExternalIP");
	    	if (primaryExternalIP.value.length == 0) {
	            hm.util.reportFieldError(primaryExternalIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.externalip.hostname" /></s:param></s:text>');
	            primaryExternalIP.focus();
	            return false;
	        } else if (!hm.util.validateIpAddress(primaryExternalIP.value)) {
	        	if(!hm.util.validateHostname(primaryExternalIP.value)){
	    			hm.util.reportFieldError(primaryExternalIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.externalip.hostname" /></s:param></s:text>');
	    			primaryExternalIP.focus();
	    			return false;
	        	}
	    	}
	
	    	var secondaryExternalIP = document.getElementById("secondaryExternalIP");
	    	if (secondaryExternalIP.value.length == 0) {
	            hm.util.reportFieldError(secondaryExternalIP, '<s:text name="error.requiredField"><s:param><s:text name="admin.ha.externalip.hostname" /></s:param></s:text>');
	            secondaryExternalIP.focus();
	            return false;
	        } else if (!hm.util.validateIpAddress(secondaryExternalIP.value)) {
	        	if(!hm.util.validateHostname(secondaryExternalIP.value)){
	    			hm.util.reportFieldError(secondaryExternalIP, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.ha.externalip.hostname" /></s:param></s:text>');
	    			secondaryExternalIP.focus();
	    			return false;
	        	}
	    	}
	    }
	}
   	
	//database setting
	if (document.getElementById("enableExternalDb").checked){
		var haPrimaryDbIp = document.getElementById("haPrimaryDbIp");
		var haPrimaryDbPwd;
		var haSecondaryDbIp = document.getElementById("haSecondaryDbIp");
		var haSecondaryDbPwd;
		if (document.getElementById("chkToggleDisplay_haPriDb").checked)
		{
			haPrimaryDbPwd = document.getElementById("haPrimaryDbPwd");
		}
		else
		{
			haPrimaryDbPwd = document.getElementById("haPrimaryDbPwd_text");
		}
		
		if (document.getElementById("chkToggleDisplay_haSecDb").checked)
		{
			haSecondaryDbPwd = document.getElementById("haSecondaryDbPwd");
		}
		else
		{
			haSecondaryDbPwd = document.getElementById("haSecondaryDbPwd_text");
		}
		
		if (haPrimaryDbIp.value.length == 0) {
            hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
            haPrimaryDbIp.focus();
            return false;
	    }else if(! hm.util.validateIpAddress(haPrimaryDbIp.value)){
            hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
            haPrimaryDbIp.focus();
    		return false;
	    }
		if (haPrimaryDbIp.value.length > 0 && !hm.util.validateIpAddress(haPrimaryDbIp.value)) {
			hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
			haPrimaryDbIp.focus();
			return false;
		}else if(hm.util.trim(haPrimaryDbIp.value) == "127.0.0.1"){
			hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
			haPrimaryDbIp.focus();
	        return false;
		}else if (haPrimaryDbIp.value.valueOf() == primaryMGTIP.value.valueOf())
		{
			hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.primary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="home.hmsettings.primary.node" />  <s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
			haPrimaryDbIp.focus();
	        return false;
		}else if (haPrimaryDbIp.value.valueOf() == secondaryMGTIP.value.valueOf())
		{
			hm.util.reportFieldError(haPrimaryDbIp, '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.primary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="home.hmsettings.secondary.node" />  <s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
			haPrimaryDbIp.focus();
	        return false;
		}
		
		if (haPrimaryDbPwd.value.length == 0) {
            hm.util.reportFieldError(haPrimaryDbPwd, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.ssh.password" /></s:param></s:text>');
            haPrimaryDbPwd.focus();
            return false;
	    } 
		if (haSecondaryDbIp.value.length == 0) {
            hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
            haSecondaryDbIp.focus();
            return false;
	    }else if(! hm.util.validateIpAddress(haSecondaryDbIp.value)){
            hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
            haSecondaryDbIp.focus();
    		return false;
	    }
		if (haSecondaryDbIp.value.length > 0 && !hm.util.validateIpAddress(haSecondaryDbIp.value)) {
			hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
			haSecondaryDbIp.focus();
			return false;
		}else if(hm.util.trim(haSecondaryDbIp.value) == "127.0.0.1"){
			hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
			haSecondaryDbIp.focus();
	        return false;
		}else if (haSecondaryDbIp.value.valueOf() == primaryMGTIP.value.valueOf())
		{
			hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.secondary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="home.hmsettings.primary.node" />  <s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
			haSecondaryDbIp.focus();
	        return false;
		}else if (haSecondaryDbIp.value.valueOf() == secondaryMGTIP.value.valueOf())
		{
			hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.secondary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="home.hmsettings.secondary.node" />  <s:text name="admin.ha.mgtInterfaceIP" /></s:param></s:text>');
			haSecondaryDbIp.focus();
	        return false;
		}else if (haSecondaryDbIp.value.valueOf() == haPrimaryDbIp.value.valueOf())
		{
			hm.util.reportFieldError(haSecondaryDbIp, '<s:text name="error.equal"><s:param><s:text name="admin.externaldb.secondary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param><s:param><s:text name="admin.externaldb.primary.database" />  <s:text name="admin.externaldb.database.ip" /></s:param></s:text>');
			haSecondaryDbIp.focus();
	        return false;
		}
		if (haSecondaryDbPwd.value.length == 0) {
            hm.util.reportFieldError(haSecondaryDbPwd, '<s:text name="error.requiredField"><s:param><s:text name="admin.externaldb.ssh.password" /></s:param></s:text>');
            haSecondaryDbPwd.focus();
            return false;
	    } 
	}
	//DNS
	if (!validateHADNS())
	{
		return false;
	}

	var enableProxy = document.getElementById("haEnableProxy");
	if (enableProxy.checked) {
		var proxyServer = document.getElementById("haProxyServer");
		var proxyPort = document.getElementById("haProxyPort");

		if (proxyServer.value.length == 0) {
	        hm.util.reportFieldError(proxyServer, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.proxyServer" /></s:param></s:text>');
	        proxyServer.focus();
	        return false;
	    }

		if (proxyPort.value.length == 0) {
	        hm.util.reportFieldError(proxyPort, '<s:text name="error.requiredField"><s:param><s:text name="admin.interface.proxyPort" /></s:param></s:text>');
	        proxyPort.focus();
	        return false;
	    }
		else if (!isValidPort(proxyPort) )
		{
			hm.util.reportFieldError(proxyPort, '<s:text name="error.keyValueRange"><s:param><s:text name="admin.interface.proxyPort" /></s:param><s:param><s:text name="admin.backupDB.portRange" /></s:param></s:text>');
			proxyPort.focus();
			return false;
		}
	}

	return true;
}

function validateHADNS()
{
	var haPrimaryDNS = document.getElementById("haPrimaryDNS");
	var haSecondDNS = document.getElementById("haSecondDNS");
	var haTertiaryDNS = document.getElementById("haTertiaryDNS");

	if (haPrimaryDNS.value.length > 0 && ! hm.util.validateIpAddress(haPrimaryDNS.value)) {
		hm.util.reportFieldError(haPrimaryDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.primaryDns" /></s:param></s:text>');
		haPrimaryDNS.focus();
		return false;
	}

	if (haSecondDNS.value.length > 0 && ! hm.util.validateIpAddress(haSecondDNS.value)) {
		hm.util.reportFieldError(haSecondDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.secondDns" /></s:param></s:text>');
		haSecondDNS.focus();
		return false;
	}

	if (haTertiaryDNS.value.length > 0 && ! hm.util.validateIpAddress(haTertiaryDNS.value)) {
		hm.util.reportFieldError(haTertiaryDNS, '<s:text name="error.formatInvalid"><s:param><s:text name="admin.interface.thirdDns" /></s:param></s:text>');
		haTertiaryDNS.focus();
		return false;
	}

	return true;
}

function validate(operation)
{
	return true;
}

function selectManually(checked)
{
	document.getElementById("hour").disabled = !checked;
	document.getElementById("minute").disabled = !checked;
	document.getElementById("second").disabled = !checked;
	document.getElementById("calendarpicker1").disabled = !checked;
	document.getElementById("ntpServer").disabled = checked;
	document.getElementById("ntpInterval").disabled = checked;
	document.getElementById(formName+"_dateFormat1").disabled = checked;
	document.getElementById(formName+"_dateFormat2").disabled = checked;
	document.getElementById(formName+"_dateSeparator1").disabled = checked;
	document.getElementById(formName+"_dateSeparator2").disabled = checked;
}

function selectSyncNTP(checked)
{
	document.getElementById("hour").disabled = checked;
	document.getElementById("minute").disabled = checked;
	document.getElementById("second").disabled = checked;
	document.getElementById("calendarpicker1").disabled = checked;
	document.getElementById("ntpServer").disabled = !checked;
	document.getElementById("ntpInterval").disabled = !checked;
	document.getElementById(formName+"_dateFormat1").disabled = checked;
	document.getElementById(formName+"_dateFormat2").disabled = checked;
	document.getElementById(formName+"_dateSeparator1").disabled = checked;
	document.getElementById(formName+"_dateSeparator2").disabled = checked;
}

function selectDateFormatSettings(checked)
{
	document.getElementById("hour").disabled = checked;
	document.getElementById("minute").disabled = checked;
	document.getElementById("second").disabled = checked;
	document.getElementById("calendarpicker1").disabled = checked;
	document.getElementById("ntpServer").disabled = checked;
	document.getElementById("ntpInterval").disabled = checked;
	document.getElementById(formName+"_dateFormat1").disabled = !checked;
	document.getElementById(formName+"_dateFormat2").disabled = !checked;
	document.getElementById(formName+"_dateSeparator1").disabled = !checked;
	document.getElementById(formName+"_dateSeparator2").disabled = !checked;
}

function showHangMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteInfo";
	hm.util.show("noteSection");
	if(!(message == "<s:text name='message.admin.separatedb.local.success'/>"
			|| message == "<s:text name='message.admin.separatedb.success'/>")){
		setTimeout("hideMessage()", 5 * 2000);
	}else{
		setTimeout("hideMessage()", 5 * 12000);
	}
}

function hideMessage()
{
	hm.util.hide('noteSection');
}

function showErrorMessage(message)
{
	document.getElementById("noteTD").innerHTML = "<td id='noteTD'>"+ message +"</td>";
	document.getElementById("noteTD").className="noteError";
	hm.util.show("noteSection");
	setTimeout("initNoteSection()", 5 * 2000);
}

function initNoteSection()
{
	hm.util.hide('noteSection');
}

function showCreateSection()
{
	document.getElementById("addRouteBtn").style.display = "none";
	document.getElementById("removeRouteBtn").style.display = "none";
	hm.util.show('addRouteTR');
	hm.util.show('inputRouteSection');
	// to fix column overlap issue on certain browsers
	var trh = document.getElementById('headerSection');
	var trc = document.getElementById('inputRouteSection');
	var table = trh.parentNode;
	table.removeChild(trh);
	table.insertBefore(trh, trc);
}

function hideCreateSection()
{
	document.getElementById("addRouteBtn").style.display = "block";
	document.getElementById("removeRouteBtn").style.display = "block";
	hm.util.hide('addRouteTR');
	hm.util.hide('inputRouteSection');
}

function controlTypeChanged(cb){
	if(cb.value == <%=HmAccessControl.CONTROL_TYPE_DENY%>){
		document.getElementById("allowSection").style.display = "none";
		document.getElementById("denySection").style.display = "";
	}else{
		document.getElementById("allowSection").style.display = "";
		document.getElementById("denySection").style.display = "none";
	}
}

function showAllowCreateSection(){
	hm.util.show('createAllowButton');
	hm.util.show('allowCreateSection');
	hm.util.hide('newAllowButton');
	var ip = document.getElementById("allowIp");
	if(ip){
		ip.focus();
	}
}

function hideAllowCreateSection(){
	hm.util.hide('createAllowButton');
	hm.util.hide('allowCreateSection');
	hm.util.show('newAllowButton');
}

function showDenyCreateSection(){
	hm.util.show('createDenyButton');
	hm.util.show('denyCreateSection');
	hm.util.hide('newDenyButton');
	var ip = document.getElementById("denyIp");
	if(ip){
		ip.focus();
	}
}

function hideDenyCreateSection(){
	hm.util.hide('createDenyButton');
	hm.util.hide('denyCreateSection');
	hm.util.show('newDenyButton');
}

function removeAllowIpAddress(){
	removeIpAddress("allowedIps");
}

function removeDenyIpAddress(){
	removeIpAddress("deniedIps");
}

function addAllowIpAddress(){
	addIpAddress("allowIp","allowMask","allowedIps");
}

function addDenyIpAddress(){
	addIpAddress("denyIp","denyMask","deniedIps");
}

function removeIpAddress(ipList){
	var listEl = Get(ipList);
	if(listEl.length == 0){
		return;
	}
	var items = listEl.options;
	var anySelected = false;
	for(var i=items.length; i>0; i--){
		var item = items[i-1];
		if(item.selected){
			anySelected = true;
			listEl.remove(i-1);
		}
	}
	if(!anySelected){
		hm.util.reportFieldError(listEl, '<s:text name="error.pleaseSelectItemOfRemeved"></s:text>');
		return;
	}
}

function addIpAddress(ipAddress, netmask, ipList){
	if(!validateIp(ipAddress)||!validateNetmask(netmask)){
		return;
	}
	var ipEl = Get(ipAddress);
	var maskEl = Get(netmask);
	var listEl = Get(ipList);
	var value = ipEl.value.trim() + "/" + maskEl.value.trim();
	if(!validateExisted(listEl, value, ipEl)){
		return;
	}
	var option = new Option(value,value);
	try{
		listEl.add(option, null);
	}catch(e){
		listEl.add(option);
	}
	ipEl.value = maskEl.value = "";
	ipEl.focus();
}

function validateExisted(list, value, dispEl){
	var listEl = Get(list);
	if(listEl && value){
		var items = listEl.options;
		for(var i=0; i<items.length; i++){
			var item = items[i].text;
			if(item == value){
				hm.util.reportFieldError(dispEl, '<s:text name="error.addObjectExists"></s:text>');
				return false;
			}
		}
	}
	return true;
}

function validateIp(ipEl){
	var ipAddressEl = Get(ipEl);
	if (ipAddressEl.value.length == 0) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.requiredField"><s:param><s:text name="hm.access.control.ip" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
  	}
	if (! hm.util.validateIpAddress(ipAddressEl.value)) {
		hm.util.reportFieldError(ipAddressEl, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.access.control.ip" /></s:param></s:text>');
		ipAddressEl.focus();
		return false;
	}
	return true;
}

function validateNetmask(maskEl){
	var netmaskEl = Get(maskEl);
	if (netmaskEl.value.length == 0) {
		netmaskEl.value = "255.255.255.255";
  	}
	if (! hm.util.validateMask(netmaskEl.value)) {
		hm.util.reportFieldError(netmaskEl, '<s:text name="error.formatInvalid"><s:param><s:text name="hm.access.control.mask" /></s:param></s:text>');
		netmaskEl.focus();
		return false;
	}
	return true;
}

function keyPressPermit(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}

	if( 32 == keycode || 35==keycode || 36==keycode )
	{
		return false;
	}

	return true;
}

function keyPressPermit4Domain(e)
{
	var keycode;
	if(window.event) // IE
	{
		keycode = e.keyCode;
	} else if(e.which) // Netscape/Firefox/Opera
	{
		keycode = e.which;
		if (keycode==8) {return true;}
	} else {
		return true;
	}

	if((keycode>=65 && keycode<=90)||
	   (keycode>=97 && keycode<=122)||
	   (keycode>=48 && keycode<=57)||
	   (keycode==45)||
	   (keycode==46))
	{
		return true;
	}

	return false;
}

function selectedEnableLan(checked)
{
	if(checked)
	{
		document.getElementById("ip_eth1").readOnly = false;
		document.getElementById("mask_eth1").readOnly = false;
		document.getElementById("ip_eth1_tr").style.color="";
		document.getElementById("mask_eth1_tr").style.color="";
	}
	else
	{
		document.getElementById("ip_eth1").readOnly = true;
		document.getElementById("mask_eth1").readOnly = true;
		document.getElementById("ip_eth1_tr").style.color="gray";
		document.getElementById("mask_eth1_tr").style.color="gray";
	}
}

function selectedEnableProxy(checked)
{
	if(checked)
	{
		document.getElementById("proxyServer").readOnly = false;
		document.getElementById("proxyPort").readOnly = false;
		document.getElementById("proxyUserName").readOnly = false;
		document.getElementById("proxyPassword").readOnly = false;
		document.getElementById("chkToggleDisplay_Proxy").disabled = false;
	}
	else
	{
		document.getElementById("proxyServer").readOnly = true;
		document.getElementById("proxyPort").readOnly = true;
		document.getElementById("proxyUserName").readOnly = true;
		document.getElementById("proxyPassword").readOnly = true;
		document.getElementById("chkToggleDisplay_Proxy").disabled = true;
	}
}

function selectedHAEnableProxy(checked)
{
	if(checked)
	{
		document.getElementById("haProxyServer").readOnly = false;
		document.getElementById("haProxyPort").readOnly = false;
		document.getElementById("haProxyUserName").readOnly = false;
		document.getElementById("haProxyPassword").readOnly = false;
		document.getElementById("chkToggleDisplay_HAProxy").disabled = false;
	}
	else
	{
		document.getElementById("haProxyServer").readOnly = true;
		document.getElementById("haProxyPort").readOnly = true;
		document.getElementById("haProxyUserName").readOnly = true;
		document.getElementById("haProxyPassword").readOnly = true;
		document.getElementById("chkToggleDisplay_HAProxy").disabled = true;
	}
}

function testConnect2LS()
{
	var url = "<s:url action='hmSettings' includeParams='none'/>" + "?ignore=" + new Date().getTime();
	document.forms[formName].operation.value = 'testLS';
	YAHOO.util.Connect.setForm(document.getElementById(formName));
	var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success:testLSResult,timeout: 120000}, null);

	if (waitingPanel != null)
	{
		waitingPanel.show();
	}
}

function testLSResult(o)
{
	if (waitingPanel != null)
	{
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	showInfoDialog(result.message);
}

//fix bug 14780
function changeHmPort(haPort){
	var viewHeight = document.getElementById('networkView').offsetHeight;
	var currentSpaceHeight = document.getElementById('networkSpace').offsetHeight;
	var realViewHeight = viewHeight - currentSpaceHeight;
	var hdnEnableHA = document.getElementById(formName +"_hdnEnableHA").value;
	var primaryLANIP = document.getElementById(formName+'_hdnPrimaryLANIP').value;
	var primaryLANMask = document.getElementById(formName+'_hdnPrimaryLANMask').value;
	if (hdnEnableHA == 'false') {
		if (haPort == 'mgt') {
			document.getElementById('primaryLANIP').value = '';
			document.getElementById('primaryLANMask').value='';
			hm.util.hide('HaPort_SecLanIp');
			hm.util.hide('HaPort_LanNetmask');
			hm.util.hide('HaPort_LanIp');
			hm.util.show('HA_Mgt_ExternalIpHost');
			hm.util.hide('HA_Primary_Node_Tr1');
			hm.util.hide('HA_Primary_Node_Tr2');
			var haLen = document.getElementById('haSection').offsetHeight + 140;
			if (haLen > realViewHeight)
			{
				networkOptionResize(haLen,haLen - realViewHeight);
			}
			else
			{
				networkOptionResize(realViewHeight,0);
			}
			networkOptionResize(haLen,haLen-realViewHeight);
		} else {
			document.getElementById('primaryLANIP').value = primaryLANIP;
			document.getElementById('primaryLANMask').value=primaryLANMask;
			hm.util.show('HaPort_SecLanIp');
			hm.util.show('HaPort_LanNetmask');
			hm.util.show('HaPort_LanIp');
			hm.util.hide('HA_Mgt_ExternalIpHost');
			hm.util.show('HA_Primary_Node_Tr1');
			hm.util.show('HA_Primary_Node_Tr2');
			var haLen = document.getElementById('haSection').offsetHeight + 130;
			if (haLen > realViewHeight)
			{
				networkOptionResize(haLen,haLen - realViewHeight);
			}
			else
			{
				networkOptionResize(realViewHeight,0);
			}
			networkOptionResize(haLen,haLen-realViewHeight);
		}
	} 
}


function selectEnableHA(checked)
{
	var viewHeight = document.getElementById('networkView').offsetHeight;
	var currentSpaceHeight = document.getElementById('networkSpace').offsetHeight;
	var realViewHeight = viewHeight - currentSpaceHeight;

	if(checked)
	{
		hm.util.show('haSection');
		hm.util.hide('normalSection');
		hm.util.show('HA_Enable');
		hm.util.hide('HA_Disable');
		hm.util.show('HA_Mgt');
		hm.util.show('HA_Lan');
		var haLen = document.getElementById('haSection').offsetHeight + 140;
		if (haLen > realViewHeight)
		{
			networkOptionResize(haLen,haLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
		networkOptionResize(haLen,haLen-realViewHeight);

		selectEnableExternalIP(document.getElementById("enableExternalIP").checked);
	}
	else
	{
		hm.util.show('normalSection');
		hm.util.hide('haSection');
		hm.util.show('HA_Disable');
		hm.util.hide('HA_Enable');
		hm.util.hide('HA_Mgt');
		hm.util.hide('HA_Lan');
		var normalLen = document.getElementById('normalSection').offsetHeight + 130;
		if (normalLen > realViewHeight)
		{
			networkOptionResize(normalLen,normalLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
}

function selectEnableExternalIP(checked)
{
	var viewHeight = document.getElementById('networkView').offsetHeight;
	var currentSpaceHeight = document.getElementById('networkSpace').offsetHeight;
	var realViewHeight = viewHeight - currentSpaceHeight;
	if(checked)
	{
		//document.getElementById("primaryExternalIP").disabled = false;
		//document.getElementById("secondaryExternalIP").disabled = false;
		hm.util.show("HA_Mgt_ExternalIp");
		var haLen = document.getElementById('haSection').offsetHeight + 140;
		if (haLen > realViewHeight)
		{
			networkOptionResize(haLen,haLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
	else
	{
		//document.getElementById("primaryExternalIP").disabled = true;
		//document.getElementById("secondaryExternalIP").disabled = true;
		hm.util.hide("HA_Mgt_ExternalIp");
		var haLen = document.getElementById('haSection').offsetHeight + 130;
		if (haLen > realViewHeight)
		{
			networkOptionResize(haLen,haLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
}

function selectEnableExternalDb(checked)
{
	var viewHeight = document.getElementById('networkView').offsetHeight;
	var currentSpaceHeight = document.getElementById('networkSpace').offsetHeight;
	var realViewHeight = viewHeight - currentSpaceHeight;
	if(checked)
	{
		hm.util.show("HA_ExternalDb");
		var haLen = document.getElementById('haSection').offsetHeight + 140;
		if (haLen > realViewHeight)
		{
			networkOptionResize(haLen,haLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
	else
	{
		hm.util.hide("HA_ExternalDb");
		var haLen = document.getElementById('haSection').offsetHeight + 130;
		if (haLen > realViewHeight)
		{
			networkOptionResize(haLen,haLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
}

function selectSeparateExternalDb(checked)
{
	var viewHeight = document.getElementById('networkView').offsetHeight;
	var currentSpaceHeight = document.getElementById('networkSpace').offsetHeight;
	var realViewHeight = viewHeight - currentSpaceHeight;
	if(checked)
	{
		hm.util.show("Separate_ExternalDb");
		var normalLen = document.getElementById('normalSection').offsetHeight + 140;
		if (normalLen > realViewHeight)
		{
			networkOptionResize(normalLen,normalLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
	else
	{
		hm.util.hide("Separate_ExternalDb");
		var normalLen = document.getElementById('normalSection').offsetHeight + 130;
		if (normalLen > realViewHeight)
		{
			networkOptionResize(normalLen,normalLen - realViewHeight);
		}
		else
		{
			networkOptionResize(realViewHeight,0);
		}
	}
}

function networkOptionResize(optionsHeight,spaceHeight)
{
	networkOptionsAnim.stop();
	networkSpaceAnim.stop();
	networkOptionsAnim.attributes.height = { to: optionsHeight };
	networkOptionsAnim.animate();
	networkSpaceAnim.attributes.height = { to: spaceHeight };
	networkSpaceAnim.duration = 0.1;
	networkSpaceAnim.animate();
}

function haSwitchOver()
{
	var btn = document.getElementById("switchHABtn");
	if(btn.value == "OK")
	{
		document.forms[formName].operation.value = "haSwitch";
	    document.forms[formName].submit();
	    
	    if(waitingPanel != null)
		{
			waitingPanel.show();
		}
	}
	else
	{
		url = "<s:url action='hmSettings' includeParams='none' />" + "?operation=checkHAOnline&ignore=" + new Date().getTime();
		var transaction = YAHOO.util.Connect.asyncRequest('GET', url, { success: checkSwitchResult,timeout: 120000}, null);
		if (waitingPanel != null)
		{
			waitingPanel.setHeader("Checking ha status...");
			waitingPanel.show();
		}

		return;
	}
}

function checkSwitchResult(o)
{
	if (waitingPanel != null)
	{
		waitingPanel.setHeader("The operation is progressing...");
		waitingPanel.hide();
	}

	eval("var result = " + o.responseText);
	if(result.success)
	{
		var btn = document.getElementById("switchHABtn");
		btn.value = "OK";
		btn.setAttribute("class","button");//Mozilla
		btn.setAttribute("className","button");//IE
		document.getElementById("cancelSwitchSection").style.display="block";
		document.getElementById("switchConfirmSection").style.display="block";
		document.getElementById("switchExplainSection").style.display="none";
	}
	else
	{
		warnDialog.cfg.setProperty('text', "Can't execute ha switch over operation. "+result.error);
		warnDialog.show();
	}
}

function cancelSwitchOver()
{
	var btn = document.getElementById("switchHABtn");
	btn.value = "Switch Over";
	btn.setAttribute("class","button long");//Mozilla
	btn.setAttribute("className","button long");//IE
	document.getElementById("cancelSwitchSection").style.display="none";
	document.getElementById("switchConfirmSection").style.display="none";
	document.getElementById("switchExplainSection").style.display="block";
}

function selectSelfSigned(checked)
{
	if(checked)
	{
		hm.util.hide('importCertSection');
	}
}

function selectImportCert(checked)
{
	if(checked)
	{
		hm.util.show('importCertSection');
	}
}

function selectFiniteSession(checked)
{
	document.getElementById("sessionExpiration").disabled= !checked;
}

function insertPageContext() {
	document.writeln('<td class="crumb" nowrap><s:property value="%{selectedL2Feature.description}" /></td>');
}

//Log Expiration - click & show & exit & save
function clickLogExpirationSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showLogExpirationOption.value = true;
    document.forms[formName].locatePosition.value = "logExpirationSaveDiv";
    document.forms[formName].submit();  
}

function showLogExpirationOption()
{
    document.getElementById("logExpirationSettingDiv").style.display = "none";
    document.getElementById("logExpirationSaveDiv").style.display = "";
    logExpirationOptionsExpand(); 
}

function exitLogExpirationOption()
{
    document.forms[formName].showLogExpirationOption.value = false;

    document.getElementById("logExpirationSettingDiv").style.display = "";
    document.getElementById("logExpirationSaveDiv").style.display = "none";
    logExpirationOptionsCollapse();
}

function saveLogExpirationOption()
{
	var syslogDays = document.getElementById("syslogExpirationDays");
	var auditlogDays = document.getElementById("auditlogExpirationDays");
	var l3FirewallLogExpirationDays = document.getElementById("l3FirewallLogExpirationDays");
	
	hm.util.hideFieldError();
	
	if (syslogDays.value.length == 0){
		hm.util.reportFieldError(syslogDays, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.logExpiration.syslog" /></s:param></s:text>');
		syslogDays.focus();
		return;
	}
	if (parseInt(syslogDays.value) == 0){
		hm.util.reportFieldError(syslogDays, 
				'<s:text name="error.formatInvalid"><s:param><s:text name="home.hmSettings.logExpiration.syslog" /></s:param></s:text>');
		syslogDays.focus();
		return;
	}
	if (auditlogDays.value.length == 0){
		hm.util.reportFieldError(auditlogDays, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.logExpiration.auditlog" /></s:param></s:text>');
		syslogDays.focus();
		return;
	}
	if (parseInt(auditlogDays.value) == 0){
		hm.util.reportFieldError(auditlogDays, 
				'<s:text name="error.formatInvalid"><s:param><s:text name="home.hmSettings.logExpiration.auditlog" /></s:param></s:text>');
		auditlogDays.focus();
		return;
	}
	 if (l3FirewallLogExpirationDays.value.length == 0){
		hm.util.reportFieldError(l3FirewallLogExpirationDays, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.logExpiration.l3firewalllog" /></s:param></s:text>');
		l3FirewallLogExpirationDays.focus();
		return;
	}
	if (parseInt(l3FirewallLogExpirationDays.value) == 0){
		hm.util.reportFieldError(l3FirewallLogExpirationDays, 
				'<s:text name="error.formatInvalid"><s:param><s:text name="home.hmSettings.logExpiration.l3firewalllog" /></s:param></s:text>');
		l3FirewallLogExpirationDays.focus();
		return;
	} 
	
		if(!validateSysLogDaysRange(syslogDays)){
			return false;
		}
		
		if(!validateAuditLogDaysRange(auditlogDays)){
			return false;
		}
		
		if(!validateL3FirewallLogDaysRange(l3FirewallLogExpirationDays)){
			return false;
		} 
	
    submitAction('updateLogExpiration');
}

function validateSysLogDaysRange(element){
	var message = hm.util.validateIntegerRange(element.value,'<s:text name="home.hmSettings.logExpiration.syslog" />',<s:property value="%{sysLogExpirationDaysRange.min()}" />,<s:property value="%{sysLogExpirationDaysRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(element, message);
		return false;
	}else{
		return true;
	}
}

function validateAuditLogDaysRange(element){
	var message = hm.util.validateIntegerRange(element.value,'<s:text name="home.hmSettings.logExpiration.auditlog" />',<s:property value="%{auditLogExpirationDaysRange.min()}" />,<s:property value="%{auditLogExpirationDaysRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(element, message);
		return false;
	}else{
		return true;
	}
}

function validateL3FirewallLogDaysRange(element){
	var message = hm.util.validateIntegerRange(element.value,'<s:text name="home.hmSettings.logExpiration.l3firewalllog" />',<s:property value="%{l3FirewallLogExpirationDaysRange.min()}" />,<s:property value="%{l3FirewallLogExpirationDaysRange.max()}" />);
	if (message != null) {
		hm.util.reportFieldError(element, message);
		return false;
	}else{
		return true;
	}
} 

//Max HiveAp image update number setting
function clickMaxUploadNumSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showMaxUploadNumOption.value = true;
    document.forms[formName].submit();  
}

function showMaxUpdateNumOption()
{
    document.getElementById("maxUploadNumSettingDiv").style.display = "none";
    document.getElementById("maxUploadNumSaveDiv").style.display = "";
    maxUpdateNumOptionsExpand(); 
}

function saveMaxUploadNumOption()
{
	var maxUpdateNum = document.getElementById("maxUpdateNum");
	if (maxUpdateNum.value.length == 0){
		hm.util.reportFieldError(maxUpdateNum, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.maxUploadNum.value" /></s:param></s:text>');
		maxUpdateNum.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(maxUpdateNum.value, '<s:text name="home.hmSettings.maxUploadNum.value" />', 1, 500);
	if (message != null) {
		hm.util.reportFieldError(maxUpdateNum, message);
		maxUpdateNum.focus();
		return;
	}
    submitAction('updateMaxUpdate');
}

function exitMaxUploadNumOption()
{
    document.forms[formName].showMaxUploadNumOption.value = false;

    document.getElementById("maxUploadNumSettingDiv").style.display = "";
    document.getElementById("maxUploadNumSaveDiv").style.display = "none";
    maxUpdateNumOptionsCollapse();
}

/* begin the setting of generation of threads */
function showUpdateThreadsOption()
{
    document.getElementById("generationThreadsSettingDiv").style.display = "none";
    document.getElementById("generationThreadsSaveDiv").style.display = "";
    generationThreadsOptionsExpand(); 
}


function generationThreadsOptionsExpand()
{
    var viewHeight = document.getElementById('generationThreadsView').offsetHeight;
    var optionsHeight = 60;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    generationThreadsOptionsAnim.stop();
    generationThreadsSpaceAnim.stop();
    generationThreadsOptionsAnim.attributes.height = { to: optionsHeight };
    generationThreadsOptionsAnim.animate();
    generationThreadsSpaceAnim.attributes.height = { to: spaceHeight };
    generationThreadsSpaceAnim.duration = 0.1;
    generationThreadsSpaceAnim.animate();
}

function clickGenerationThreadsSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showGenerationThreadsOption.value = true;
    document.forms[formName].locatePosition.value = "generationThreadsSaveDiv";
    document.forms[formName].submit();  
}

function saveGenerationThreadsOption()
{
	var concurrentConfigGenNum = document.getElementById("concurrentConfigGenNum");
	if (concurrentConfigGenNum.value.length == 0){
		hm.util.reportFieldError(concurrentConfigGenNum, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.maximum.concurrent.generations.title" /></s:param></s:text>');
		concurrentConfigGenNum.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(concurrentConfigGenNum.value, '<s:text name="home.hmSettings.maximum.concurrent.generations.title" />', 1, 10);
	if (message != null) {
		hm.util.reportFieldError(concurrentConfigGenNum, message);
		concurrentConfigGenNum.focus();
		return;
	}
    submitAction('updateConcurrentConfigGenNum');
}

function exitGenerationThreadsOption()
{
    document.forms[formName].showGenerationThreadsOption.value = false;

    document.getElementById("generationThreadsSettingDiv").style.display = "";
    document.getElementById("generationThreadsSaveDiv").style.display = "none";
    generationThreadsOptionsCollapse();
}

function generationThreadsOptionsCollapse()
{
	generationThreadsOptionsAnim.stop();
	generationThreadsSpaceAnim.stop();
	generationThreadsOptionsAnim.attributes.height = { to: 0 };
	generationThreadsOptionsAnim.animate();
	generationThreadsSpaceAnim.attributes.height = { to: 0 };
	generationThreadsSpaceAnim.duration = 0.1;
	generationThreadsSpaceAnim.animate();
} 

/* end the setting of generation of threads */

/* begin set the search user number  */
function showUpdateSearchUserNumOption()
{
    document.getElementById("maxSearchUserNumSettingDiv").style.display = "none";
    document.getElementById("maxSearchUserNumSaveDiv").style.display = "";
    serachUserNumOptionsExpand(); 
}


function serachUserNumOptionsExpand()
{
    var viewHeight = document.getElementById('maxSearchUserNumView').offsetHeight;
    var optionsHeight = 50;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    maxSearchUserNumOptionsAnim.stop();
    maxSearchUserNumSpaceAnim.stop();
    maxSearchUserNumOptionsAnim.attributes.height = { to: optionsHeight };
    maxSearchUserNumOptionsAnim.animate();
    maxSearchUserNumSpaceAnim.attributes.height = { to: spaceHeight };
    maxSearchUserNumSpaceAnim.duration = 0.1;
    maxSearchUserNumSpaceAnim.animate();
}

function clickMaxSearchUserNumSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showSearchUserNumOption.value = true;
    document.forms[formName].locatePosition.value = "maxSearchUserNumSaveDiv";
    document.forms[formName].submit();  
}

function saveMaxSearchUserNumOption()
{
	var concurrentSearchUserNum = document.getElementById("concurrentSearchUserNum");
	if (concurrentSearchUserNum.value.length == 0){
		hm.util.reportFieldError(concurrentSearchUserNum, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.maximum.concurrent.search.usernum.title" /></s:param></s:text>');
		concurrentSearchUserNum.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(concurrentSearchUserNum.value, '<s:text name="home.hmSettings.maximum.concurrent.search.usernum.title" />', 1, 10);
	if (message != null) {
		hm.util.reportFieldError(concurrentSearchUserNum, message);
		concurrentSearchUserNum.focus();
		return;
	}
    submitAction('updateConcurrentSearchUserNum');
}

function exitMaxSearchUserNumOption()
{
    document.forms[formName].showSearchUserNumOption.value = false;

    document.getElementById("maxSearchUserNumSettingDiv").style.display = "";
    document.getElementById("maxSearchUserNumSaveDiv").style.display = "none";
    searchUserNumOptionsCollapse();
}

function searchUserNumOptionsCollapse()
{
	maxSearchUserNumOptionsAnim.stop();
	maxSearchUserNumSpaceAnim.stop();
	maxSearchUserNumOptionsAnim.attributes.height = { to: 0 };
	maxSearchUserNumOptionsAnim.animate();
	maxSearchUserNumSpaceAnim.attributes.height = { to: 0 };
	maxSearchUserNumSpaceAnim.duration = 0.1;
	maxSearchUserNumSpaceAnim.animate();
} 
/* end set the search user number */

/* begin set the CA server  */
function showCloudAuthServerOption()
{
    document.getElementById("cloudAuthServerSettingDiv").style.display = "none";
    document.getElementById("cloudAuthServerSaveDiv").style.display = "";
    cloudAuthServerOptionsExpand(); 
}

function cloudAuthServerOptionsExpand()
{
    var viewHeight = document.getElementById('cloudAuthServerView').offsetHeight;
    var optionsHeight = 80;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    cloudAuthServerOptionsAnim.stop();
    cloudAuthServerSpaceAnim.stop();
    cloudAuthServerOptionsAnim.attributes.height = { to: optionsHeight };
    cloudAuthServerOptionsAnim.animate();
    cloudAuthServerSpaceAnim.attributes.height = { to: spaceHeight };
    cloudAuthServerSpaceAnim.duration = 0.1;
    cloudAuthServerSpaceAnim.animate();
}

function clickCloudAuthServerSetting()
{
    showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showCloudAuthServerOption.value = true;
    document.forms[formName].locatePosition.value = "cloudAuthServerSaveDiv";
    document.forms[formName].submit();  
}

function saveCloudAuthServerOption()
{
    submitAction('updateCloudAuthServer');
}

function exitCloudAuthServerOption()
{
    document.forms[formName].showCloudAuthServerOption.value = false;

    document.getElementById("cloudAuthServerSettingDiv").style.display = "";
    document.getElementById("cloudAuthServerSaveDiv").style.display = "none";
    cloudAuthServerOptionsCollapse();
}

function cloudAuthServerOptionsCollapse()
{
    cloudAuthServerOptionsAnim.stop();
    cloudAuthServerSpaceAnim.stop();
    cloudAuthServerOptionsAnim.attributes.height = { to: 0 };
    cloudAuthServerOptionsAnim.animate();
    cloudAuthServerSpaceAnim.attributes.height = { to: 0 };
    cloudAuthServerSpaceAnim.duration = 0.1;
    cloudAuthServerSpaceAnim.animate();
}

/* end set the CA server */

/*  --start tca alarm ---*/
function showTCAAlarmOption()
{
    document.getElementById("tcaAlarmSettingDiv").style.display = "none";
    document.getElementById("tcaAlarmSaveDiv").style.display = "";
    tcaAlarmOptionsExpand(); 
}


function tcaAlarmOptionsExpand()
{
    var viewHeight = document.getElementById('tcaAlarmView').offsetHeight;
    var optionsHeight = 150;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    tcaAlarmOptionsAnim.stop();
    tcaAlarmSpaceAnim.stop();
    tcaAlarmOptionsAnim.attributes.height = { to: optionsHeight };
    tcaAlarmOptionsAnim.animate();
    tcaAlarmSpaceAnim.attributes.height = { to: spaceHeight };
    tcaAlarmSpaceAnim.duration = 0.1;
    tcaAlarmSpaceAnim.animate();
}

function clickTCAAlarmSetting()
{
	showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showTCAAlarmOption.value = true;
    document.forms[formName].locatePosition.value = "tcaAlarmSaveDiv";
    document.forms[formName].submit();  
}

function saveTCAAlarmOption()
{
	var tcaMeatureItem=document.getElementById("tcaMeasureItem");
	var highGate=document.getElementById("tcaHighThreshold");
	var lowGate=document.getElementById("tcaLowThreshold");
	var interval=document.getElementById("tcaInterval");
	//check null
	
	if(tcaMeatureItem.value == 0){
		hm.util.reportFieldError(tcaMeatureItem, 
		'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.tcaalarm.edit.selectItem.label" /></s:param></s:text>');
		tcaMeatureItem.focus();
        return;
	}
	
	if (highGate.value.length == 0){
		hm.util.reportFieldError(highGate, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.tcaalarm.edit.highThreshold.label" /></s:param></s:text>');
		highGate.focus();
		return;
	}
	
	if (lowGate.value.length == 0){
		hm.util.reportFieldError(lowGate, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.tcaalarm.edit.lowThreshold.label" /></s:param></s:text>');
		lowGate.focus();
		return;
	}
	
	if (interval.value.length == 0){
		hm.util.reportFieldError(interval, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.tcaalarm.edit.monitorInteval.label" /></s:param></s:text>');
		interval.focus();
		return;
	}
	//check whether it's number
	if(isNaN(highGate.value)){
		 hm.util.reportFieldError(highGate, '<s:text name="home.hmSettings.tcaalarm.edit.error.isNotANumber"></s:text>');
		 highGate.value="";
		 highGate.focus();
		 return;
	}
	
	if(isNaN(lowGate.value)){
		 hm.util.reportFieldError(lowGate, '<s:text name="home.hmSettings.tcaalarm.edit.error.isNotANumber"></s:text>');
		 lowGate.value="";
		 lowGate.focus();
		 return;
	}
	
	if(isNaN(interval.value)){
		 hm.util.reportFieldError(interval, '<s:text name="home.hmSettings.tcaalarm.edit.error.isNotANumber"></s:text>');
		 interval.value="";
		 interval.focus();
		 return;
	}
	//check the range
	var message = hm.util.validateIntegerRange(highGate.value, '<s:text name="home.hmSettings.tcaalarm.edit.highThreshold.label" />',
			50, 90);
	if(message) {
		hm.util.reportFieldError(highGate, message);
		highGate.focus();
		return;
	};
	
	var messageLow = hm.util.validateIntegerRange(lowGate.value, '<s:text name="home.hmSettings.tcaalarm.edit.lowThreshold.label" />',
			50, 90);
	if(messageLow) {
		hm.util.reportFieldError(lowGate, messageLow);
		lowGate.focus();
		return;
	};
	
	var messageInterval = hm.util.validateIntegerRange(interval.value, '<s:text name="home.hmSettings.tcaalarm.edit.monitorInteval.label" />',
			10, 60);
	if(messageInterval) {
		hm.util.reportFieldError(interval, messageInterval);
		interval.focus();
		return;
	};
	
	
	
	//check the relation of highThreshold and lowThreshold
	if(lowGate.value>=highGate.value){
		hm.util.reportFieldError(highGate, '<s:text name="home.hmSettings.tcaalarm.edit.error.HighLowLogic"></s:text>');
		// highGate.value="";
		 highGate.focus();
		 return;
	}
	
	/* 
	if (serverEl.value.length == 0){
		hm.util.reportFieldError(serverEl, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.cloudAuthServer.gateway.url" /></s:param></s:text>');
		serverEl.focus();
		return;
	}
	if (certServerEl.value.length == 0){
		hm.util.reportFieldError(certServerEl, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.cloudAuthServer.cert.url" /></s:param></s:text>');
		certServerEl.focus();
		return;
	}
	if (webServerEl.value.length == 0){
		hm.util.reportFieldError(webServerEl, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.cloudAuthServer.web.url" /></s:param></s:text>');
		webServerEl.focus();
		return;
	}
	if (tlsPortIdEl.value.length == 0){
		hm.util.reportFieldError(tlsPortIdEl, 
				'<s:text name="error.requiredField"><s:param><s:text name="home.hmSettings.cloudAuthServer.tlsPort" /></s:param></s:text>');
		tlsPortIdEl.focus();
		return;
	}
	var message = hm.util.validateIntegerRange(tlsPortIdEl.value, '<s:text name="home.hmSettings.cloudAuthServer.tlsPort" />',
			1, 65535);
	if(message) {
		hm.util.reportFieldError(tlsPortIdEl, message);
		tlsPortIdEl.focus();
		return;
	}; */
	
    submitAction('updateTCAAlarm');
}

function exittcaAlarmOption()
{
    document.forms[formName].showTCAAlarmOption.value = false;

    document.getElementById("tcaAlarmSettingDiv").style.display = "";
    document.getElementById("tcaAlarmSaveDiv").style.display = "none";
    tcaAlarmOptionsCollapse();
}


function tcaAlarmOptionsCollapse()
{
	tcaAlarmOptionsAnim.stop();
	tcaAlarmSpaceAnim.stop();
	tcaAlarmOptionsAnim.attributes.height = { to: 0 };
	tcaAlarmOptionsAnim.animate();
	tcaAlarmSpaceAnim.attributes.height = { to: 0 };
	tcaAlarmSpaceAnim.duration = 0.1;
	tcaAlarmSpaceAnim.animate();
} 


function loadMeasureParameter()
{
	var tcaMeatureItem=document.getElementById("tcaMeasureItem");
	var highGate=document.getElementById("tcaHighThreshold");
	var lowGate=document.getElementById("tcaLowThreshold");
	var interval=document.getElementById("tcaInterval");
	if(tcaMeatureItem.value == 0){
		highGate.value="";
		lowGate.value="";
		interval.value="";
	}
	
	if(tcaMeatureItem.value!=0){
		var id = tcaMeatureItem.options[tcaMeatureItem.selectedIndex].value;
		var url = "<s:url action='hmSettings' includeParams='none'/>?operation=refreshMeasureInfo&measureId=" + id;
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : refreshMeasureItem, timeout: 60000}, null);	
		
	}
	
	
}

function refreshMeasureItem(o){
	var tcaMeatureItem=document.getElementById("tcaMeasureItem");
	var highGate=document.getElementById("tcaHighThreshold");
	var lowGate=document.getElementById("tcaLowThreshold");
	var interval=document.getElementById("tcaInterval");
	eval("var measureInfos = " + o.responseText);
	
	highGate.value=measureInfos.high;
	lowGate.value=measureInfos.low;
	interval.value=measureInfos.interval;
	
	
	/* if(details.succ){
	}else{
		showErrorMessage(details.message);
	} */
}


/*end for tca alarm */
/*start for custom device tag*/

function clickDeviceTagSetting()
{
	showProcessing();
    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showDeviceTagOption.value = true;
    document.forms[formName].locatePosition.value = "customDeviceTagSaveDiv";
    document.forms[formName].submit();  
}

function showDeviceTagOption()
{
    document.getElementById("customDeviceTagSettingDiv").style.display = "none";
    document.getElementById("customDeviceTagSaveDiv").style.display = "";
    customDeviceTagOptionsExpand(); 
}

function exitDeviceTagOption()
{
    document.forms[formName].showDeviceTagOption.value = false;
    document.getElementById("customDeviceTagSettingDiv").style.display = "";
    document.getElementById("customDeviceTagSaveDiv").style.display = "none";
    customDeviceTagOptionsCollapse();
}


function customDeviceTagOptionsExpand()
{
    var viewHeight = document.getElementById('customDeviceTagView').offsetHeight;
    var optionsHeight = 120;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    customDeviceTagOptionsAnim.stop();
    customDeviceTagOptionsAnim.stop();
    customDeviceTagOptionsAnim.attributes.height = { to: optionsHeight };
    customDeviceTagOptionsAnim.animate();
    customDeviceTagSpaceAnim.attributes.height = { to: spaceHeight };
    customDeviceTagSpaceAnim.duration = 0.1;
    customDeviceTagSpaceAnim.animate();
}

function customDeviceTagOptionsCollapse()
{
	customDeviceTagOptionsAnim.stop();
	customDeviceTagSpaceAnim.stop();
	customDeviceTagOptionsAnim.attributes.height = { to: 0 };
	customDeviceTagOptionsAnim.animate();
	customDeviceTagSpaceAnim.attributes.height = { to: 0 };
	customDeviceTagSpaceAnim.duration = 0.1;
	customDeviceTagSpaceAnim.animate();
}

function editCustomTag(operation) 
{   
   if(operation == 'customTag1')
   {    
     if(document.hmSettings.testCheckbox1.checked){
         hm.util.show('inputname1'); hm.util.show('inputname11');
         hm.util.hide('showname1');
     }else{             
         hm.util.show('showname1');
         hm.util.hide('inputname1');hm.util.hide('inputname11');
     }
   }
   
   if(operation == 'customTag2')
   {    
     if(document.hmSettings.testCheckbox2.checked){
         hm.util.show('inputname2');hm.util.show('inputname22');
         hm.util.hide('showname2');
     }else{             
         hm.util.show('showname2');
         hm.util.hide('inputname2');hm.util.hide('inputname22');
     }
   }
   
   if(operation == 'customTag3')
   {    
     if(document.hmSettings.testCheckbox3.checked){
         hm.util.show('inputname3');hm.util.show('inputname33');
         hm.util.hide('showname3');
     }else{             
         hm.util.show('showname3');
         hm.util.hide('inputname3');hm.util.hide('inputname33');
     }
   }	  

 }
 
function saveDeviceTagOption()
{
	var customName1 = document.getElementById("inputname1");
	var customName2 = document.getElementById("inputname2");
	var customName3 = document.getElementById("inputname3");
	hm.util.hideFieldError();	
	
	var a1=customName1.value.length;
	var a2=customName2.value.length;
	var a3=customName3.value.length;
	
	if(a1>16||a2>16||a3>16||a1<1||a2<1||a3<1){
	   alert("Please keep custom device tag text value between  1 - 16 characters");
	   return;
	}
    submitAction('updateDeviceTag');
}

/***end for custom device tag*/
 /* start for client profile */
 function showClientProfileOption()
{
    document.getElementById("clientProfileSettingDiv").style.display = "none";
    document.getElementById("clientProfileSaveDiv").style.display = "";
    clientProfileOptionsExpand(); 
}
function clientProfileOptionsExpand()
{
    var viewHeight = document.getElementById('clientProfileView').offsetHeight;
    var optionsHeight = 80;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    clientProfileOptionsAnim.stop();
    clientProfileSpaceAnim.stop();
    clientProfileOptionsAnim.attributes.height = { to: optionsHeight };
    clientProfileOptionsAnim.animate();
    clientProfileSpaceAnim.attributes.height = { to: spaceHeight };
    clientProfileSpaceAnim.duration = 0.1;
    clientProfileSpaceAnim.animate();
}

function clickClientProfileSetting()
{
    showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showClientProfileOption.value = true;
    document.forms[formName].locatePosition.value = "clientProfileSaveDiv";
    document.forms[formName].submit();  
}

function saveClientProfileOption()
{
    submitAction('updateClientProfile');
}

function exitClientProfileOption()
{
    document.forms[formName].showClientProfileOption.value = false;

    document.getElementById("clientProfileSettingDiv").style.display = "";
    document.getElementById("clientProfileSaveDiv").style.display = "none";
    clientProfileOptionsCollapse();
}

function clientProfileOptionsCollapse()
{
	clientProfileOptionsAnim.stop();
	clientProfileSpaceAnim.stop();
	clientProfileOptionsAnim.attributes.height = { to: 0 };
	clientProfileOptionsAnim.animate();
	clientProfileSpaceAnim.attributes.height = { to: 0 };
	clientProfileSpaceAnim.duration = 0.1;
	clientProfileSpaceAnim.animate();
}
 /* end for client profile */
 
/* start supplemental cli */

 function showSupplementalCLIOption()
{
    document.getElementById("supplementalCLISettingDiv").style.display = "none";
    document.getElementById("supplementalCLISaveDiv").style.display = "";
    supplementalCLIOptionsExpand(); 
}
function supplementalCLIOptionsExpand()
{
    var viewHeight = document.getElementById('supplementalCLIView').offsetHeight;
    var optionsHeight = 80;

    var spaceHeight = (optionsHeight > viewHeight) ? (optionsHeight - viewHeight) : 0;
    optionsHeight = (optionsHeight > viewHeight) ? optionsHeight : viewHeight;

    supplementalCLIOptionsAnim.stop();
    supplementalCLISpaceAnim.stop();
    supplementalCLIOptionsAnim.attributes.height = { to: optionsHeight };
    supplementalCLIOptionsAnim.animate();
    supplementalCLISpaceAnim.attributes.height = { to: spaceHeight };
    supplementalCLISpaceAnim.duration = 0.1;
    supplementalCLISpaceAnim.animate();
}

function clickSupplementalCLISetting()
{
   showProcessing();

    document.forms[formName].operation.value = "refresh";
    document.forms[formName].showSupplementalCLIOption.value = true;
    document.forms[formName].locatePosition.value = "supplementalCLISaveDiv";
    document.forms[formName].submit(); 

}

function saveSupplementalCLIOption()
{
    submitAction('updateSupplementalCLI');
}

function exitSupplementalCLIOption()
{
    document.forms[formName].showClientProfileOption.value = false;

    document.getElementById("supplementalCLISettingDiv").style.display = "";
    document.getElementById("supplementalCLISaveDiv").style.display = "none";
    supplementalCLIOptionsCollapse();
}

function supplementalCLIOptionsCollapse()
{
	supplementalCLIOptionsAnim.stop();
	supplementalCLISpaceAnim.stop();
	supplementalCLIOptionsAnim.attributes.height = { to: 0 };
	supplementalCLIOptionsAnim.animate();
	supplementalCLISpaceAnim.attributes.height = { to: 0 };
	supplementalCLISpaceAnim.duration = 0.1;
	supplementalCLISpaceAnim.animate();
}

/* end supplemental cli */
 
//end
</script>
<div id="content"><s:form action="hmSettings">
	<s:hidden name="showDateTimeOption" />
	<s:hidden name="showRouteOption" />
	<s:hidden name="showNetworkOption" />
	<s:hidden name="showLoginAccessOption" />
	<s:hidden name="showCertOption" />
	<s:hidden name="showSSHOption" />
	<s:hidden name="showSessionOption" />
	<s:hidden name="showImproveOption" />
	<s:hidden name="showExpressModeOption" />
	<s:hidden name="showLogExpirationOption" />
	<s:hidden name="showMaxUploadNumOption" />
	<s:hidden name="showGenerationThreadsOption" />
	<s:hidden name="showSearchUserNumOption" />
	<s:hidden name="showCloudAuthServerOption" />
	<s:hidden name="hdnEnableHA" />
	<s:hidden name="hdnPrimaryLANIP" />
	<s:hidden name="hdnPrimaryLANMask" />
	<s:hidden name="locatePosition" />
	<s:hidden name="showTCAAlarmOption" />
	<s:hidden name="showDeviceTagOption" />
	<s:hidden name="showClientProfileOption" />
	<s:hidden name="showAPIOption" />
	<s:hidden name="showSupplementalCLIOption"/>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><tiles:insertDefinition name="context" /></td>
		</tr>
		<tr>
			<td class="buttons">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="button" id="refreshBtn" name="ignore"
						value="Refresh" class="button" onClick="submitAction('refresh');"
						<s:property value="writeDisabled" />></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="10">
			<div id="noteSection" style="display: none">
			<table width="700px" border="0" cellspacing="0" cellpadding="0"
				class="note">
				<tr>
					<td height="5"></td>
				</tr>
				<tr>
					<td id="noteTD" nowrap="nowrap"></td>
				</tr>
				<tr>
					<td height="5"></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td><tiles:insertDefinition name="notes" /></td>
		</tr>
		<tr>
			<td>
			<table class="editBox" cellspacing="0" cellpadding="0" border="0"
				width="850px">
				<tr>
					<td style="padding: 10px 10px 0px 15px;"><%-- add this password dummy to fix issue with auto complete function --%>
					<input style="display: none;" name="dummy_pwd" id="dummy_pwd"
						type="password">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="dateTimeSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.systemTime" /> </span> <a href="#showOptions"
								onclick="clickDateTimeSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="dateTimeSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editDateTime" /> </span> <a href="#saveOptions"
								onclick="saveDateTimeOption();"> <img alt="Save"
								title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitDateTimeOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="dateTimeView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="180px"><s:text
												name="home.hmSettings.currentTime" /></td>
											<td id="currentTimeTd" class="showValue"><s:property
												value="currentTimeShow" escape="false" /> &nbsp;</td>
										</tr>
										<tr style="display:<s:property value="%{hide4VHM}"/>">
											<td class="labelT1"><s:text
												name="home.hmSettings.timeSync" /></td>
											<td class="showValue"><s:property
												value="dateTimeSyncShow" /> &nbsp;</td>
										</tr>
										<tr style="display:<s:property value="%{hide4VHM}"/>">
											<td class="labelT1"><s:text
												name="home.hmSettings.actAsNTP" /></td>
											<td class="showValue"><s:property
												value="asNtpServerShow" /> &nbsp;</td>
										</tr>
										<tr>
											<td height="0px">
											<div id="dateTimeSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="dateTimeOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td class="labelT1">
												<label style="padding-right: 60px;">
													<s:text name="admin.timeSet.timeZone" />
												</label>
												<s:select id="timezone" name="timezone" value="%{timezone}" list="%{enumTimeZone}" listKey="key" listValue="value" />
											</td>
										</tr>
										<tr>
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td style="padding-left: 10px;"><s:radio
														label="Gender" name="syncMode"
														list="#{'manually':'Set the date/time on the system clock manually'}"
														onclick="selectManually(this.checked);"
														value="%{syncMode}" /></td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td height="5"></td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td style="padding-left: 30px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="95px"><label> <s:text
																name="admin.timeSet.date" /> </label></td>
															<td><s:textfield name="dateTime" id="dateTime"
																value="%{dateTime}" readonly="true" size="10" /></td>
															<td width="50px">
															<div id="datetimeDiv" />
															</td>
															<td><s:select id="hour" name="hour" value="%{hour}"
																list="enumHours" listKey="key" listValue="value"
																disabled="%{disabledManually}" /> <s:select id="minute"
																name="minute" value="%{minute}" list="enumMinutes"
																listKey="key" listValue="value"
																disabled="%{disabledManually}" /> <s:select id="second"
																name="second" value="%{second}" list="enumSeconds"
																listKey="key" listValue="value"
																disabled="%{disabledManually}" /></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td style="padding-left: 10px;"><s:radio
														label="Gender" name="syncMode"
														list="#{'dateFormatSettings':'Customize the date/time format'}"
														onclick="selectDateFormatSettings(this.checked);"
														value="%{syncMode}" /></td>
												</tr>
												<tr>
													<td style="padding-left: 30px;">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td class="labelT1" width="95px">
																	<label>
																		<s:text name="home.hmSettings.dateFormat" />
																	</label>
																</td>
																<td>
																	<s:radio name="dateFormat" list="%{dateFormatItem1}" listKey="key"
																				listValue="value" disabled="%{disabledDateFormat}"/>
																</td>
																<td>
																	<s:radio name="dateFormat" list="%{dateFormatItem2}" listKey="key"
																				listValue="value" disabled="%{disabledDateFormat}"/>
																</td>
															</tr>
															<tr>
																<td class="labelT1" width="95px">
																	<label>
																		<s:text name="home.hmSettings.dateSeparator" />
																	</label>
																</td>
																<td>
																	<s:radio name="dateSeparator" list="%{dateSeparator1}" listKey="key"
																				listValue="value" disabled="%{disabledDateFormat}"/>
																</td>
																<td>
																	<s:radio name="dateSeparator" list="%{dateSeparator2}" listKey="key"
																				listValue="value" disabled="%{disabledDateFormat}"/>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td style="padding-left: 10px;"><s:radio
														label="Gender" name="syncMode"
														list="#{'syncNTP':'Synchronize the system clock with an NTP server'}"
														onclick="selectSyncNTP(this.checked);" value="%{syncMode}" />
													</td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td height="5"></td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td style="padding-left: 30px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="95px"><label> <s:text
																name="admin.timeSet.ntpServer" /> </label></td>
															<td colspan="3"><s:textfield id="ntpServer"
																name="ntpServer" disabled="%{disabledSyncNTP}"
																maxlength="32" size="40" /> <s:text
																name="admin.timeSet.ntpServerRange" /></td>
														</tr>
														<tr>
															<td class="labelT1" width="95px"><label> <s:text
																name="admin.timeSet.syncInterval" /> </label></td>
															<td colspan="3"><s:textfield id="ntpInterval"
																name="ntpInterval"
																onkeypress="return hm.util.keyPressPermit(event,'ten');"
																disabled="%{disabledSyncNTP}" maxlength="5" size="20" />
															<s:text name="admin.timeSet.syncMins" /></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr style="display:<s:property value="%{hide4VHM}"/>">
													<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td style="padding: 0 2px 0 10px"><s:checkbox
																name="ntpServiceStart" /></td>
															<td colspan="4"><s:text
																name="admin.timeSet.ntpServer.enable" /></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4VHM}"/>;">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="networkSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.networkSettings" /> </span> <a
								href="#showOptions" onclick="clickNetworkSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="networkSaveDiv"
								style="display: none;"><span class="settingCaption">
								<s:if test="%{hMOnline}">
								<s:text name="home.hmSettings.editNetworkSettings" />
								</s:if><s:else>
								<s:text name="home.hmSettings.editNetworkHASettings" />
								</s:else>
								</span> <a
								href="#saveOptions" onclick="saveNetworkOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitNetworkOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="networkView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="labelT1" width="120px"><s:text
												name="admin.interface.hostname" /></td>
											<td class="showValue"><s:property value="hostName" />
											&nbsp;</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
												name="admin.interface.domain" /></td>
											<td class="showValue"><s:property value="networkDomain" />
											&nbsp;</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
												name="admin.interface.dnsServers" /></td>
											<td class="showValue"><s:property value="dnsServers" />
											&nbsp;</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
												name="admin.interface.defaultGateway" /></td>
											<td class="showValue"><s:property value="defaultGateway" />
											&nbsp;</td>
										</tr>
										<tr>
											<td class="labelT1"><s:text
												name="home.hmSettings.interfaceStatus" /></td>
											<td class="labelT1">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<th><s:text name="home.hmSettings.interface" /></th>
													<th><s:text name="home.hmSettings.status" /></th>
													<th><s:text name="home.hmSettings.ip" /></th>
													<th><s:text name="home.hmSettings.netmask" /></th>
													<th><s:text name="home.hmSettings.rate" /></th>
												</tr>
												<tr>
													<td class="list" style="color: #000080;"><s:text
														name="home.hmSettings.mgt" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="status_eth0" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="ip_eth0" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="mask_eth0" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="rate_eth0" /></td>
												</tr>
												<tr style="display:<s:property value="%{hide4HHM}"/>">
													<td class="list" style="color: #000080;"><s:text
														name="home.hmSettings.lan" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="status_eth1" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="ip_eth1" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="mask_eth1" /></td>
													<td class="list" style="color: #000080;"><s:property
														value="rate_eth1" /></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hide4HHM}"/>">
											<td class="labelT1"><s:text
												name="admin.interface.proxySetting" /></td>
											<td class="showValue"><s:property
												value="proxyConfiguration" /> &nbsp;</td>
										</tr>
										<s:if test="!hMOnline && externalDbIpInfoDisplay">
										<tr>
											<td class="labelT1"><s:text
												name="admin.externaldb.ip.show" /></td>
											<td class="showValue"><s:property
												value="externalDbIpInfo" /> &nbsp;</td>
										</tr>
										</s:if>
										<tr style="display:<s:property value="%{hide4HA}"/>">
											<td height="5"></td>
										</tr>
										<tr style="display:<s:property value="%{hide4HA}"/>">
											<td class="sepLine" colspan="2"><img
												src="<s:url value="/images/spacer.gif"/>" height="1"
												class="dblk" /></td>
										</tr>
										<tr style="display:<s:property value="%{hide4HA}"/>">
											<td height="5"></td>
										</tr>
										<tr style="display:<s:property value="%{hide4HA}"/>">
											<td colspan="2">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="labelT1"><s:text
														name="home.hmSettings.haEnabled" /></td>
													<td class="showValue"><s:property value="enableHAShow" />
													&nbsp;</td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="home.hmSettings.haStatus" /></td>
													<td class="showValue"><s:property value="haStatus" />
													&nbsp;</td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="home.hmSettings.secondaryHostName" /></td>
													<td class="showValue"><s:property
														value="passiveHostName" /> &nbsp;</td>
												</tr>
												<tr>
													<td class="labelT1"><s:text
														name="home.hmSettings.secondaryIP" /></td>
													<td class="showValue"><s:property
														value="passiveIPMask" /> &nbsp;</td>
												</tr>
												<tr>
													<td class="buttons" colspan="2" style="padding-left: 10px">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><input type="button" id="switchHABtn"
																name="ignore" value="Switch Over" class="button long"
																onClick="haSwitchOver();"
																<s:property value="writeDisabled" />></td>
															<td>
															<div style="display: none" id="cancelSwitchSection">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><input type="button" name="ignore"
																		value="Cancel" class="button"
																		onClick="cancelSwitchOver();"></td>
																</tr>
															</table>
															</div>
															</td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td colspan="2" style="padding-left: 10px">
													<div style="display: none" id="switchConfirmSection">
													<table class="editBox" cellspacing="0" cellpadding="0"
														border="0" width="500">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding: 10px 10px 20px 10px" align="center"
																valign="middle"><label> <strong><s:text
																name="admin.hmOperation.confirm" /> </strong> </label></td>
														</tr>
													</table>
													</div>
													<div id="switchExplainSection">
													<table class="editBox" cellspacing="0" cellpadding="0"
														border="0" width="500">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding: 10px 10px 20px 10px"><label>
															<strong><s:text
																name="admin.ha.switchover.explain" /> </strong> </label></td>
														</tr>
													</table>
													</div>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="networkSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="networkOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td  class="labelT1">
												<table cellspacing="0" cellpadding="0" border="0" width="100%" id="HA_Enable" style="display: <s:property value="%{enableHA?'':'none'}"/>">
													<tr>
															<td class="labelT1" width="130px"><label> <s:text
																name="admin.interface.hostname" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="primaryHostName"
																name="primaryHostName"
																onkeypress="return keyPressPermit(event);" size="32"
																maxlength="%{hostNameLength}"
																readonly="%{writeDisable4HA}" /> <s:text
																name="admin.interface.hostname.range" /></td>
														</tr>
														<tr style="display: <s:property value="%{writeDisable4HA?'':'none'}"/>">
															<td class="labelT1" width="130px"><label> <s:text
																name="admin.ha.secondaryHostName" /></label></td>
															<td><s:textfield id="secondaryHostName"
																name="secondaryHostName" size="32" readonly="true" /></td>
														</tr>
														<tr>
															<td class="labelT1" width="130px"><label> <s:text
																name="admin.interface.domain" /> </label></td>
															<td><s:textfield id="haDomain" name="haDomainName"
																size="64"
																onkeypress="return keyPressPermit4Domain(event);"
																maxlength="%{domainLength}"
																readonly="%{writeDisable4HA}" /> <s:text
																name="admin.interface.domain.range" /></td>
														</tr>
													</table>
												<table cellspacing="0" cellpadding="0" border="0" width="100%" id="HA_Disable" style="display: <s:property value="%{enableHA?'none':''}"/>">
													<tr>
														<td class="labelT1" width="130px"><label> <s:text
															name="admin.interface.hostname" /><font color="red"><s:text
															name="*" /> </font> </label></td>
														<td><s:textfield id="hostName" name="hostName"
															size="32" onkeypress="return keyPressPermit(event);"
															maxlength="%{hostNameLength}" /> <s:text
															name="admin.interface.hostname.range" /></td>
													</tr>
													<tr>
														<td class="labelT1" width="130px"><label> <s:text
															name="admin.interface.domain" /> </label></td>
														<td><s:textfield id="domain" name="networkDomain"
															size="64"
															onkeypress="return keyPressPermit4Domain(event);"
															maxlength="%{domainLength}" /> <s:text
															name="admin.interface.domain.range" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr style="display:<s:property value="%{hide4HHM}"/>">
											<td class="labelT1">
												<table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
													<td width="135px"><s:checkbox name="enableHA" id="enableHA" onclick="selectEnableHA(this.checked);" /> <label>
													<s:text name="admin.ha.enableHA" /> </label></td>
													<td id="HA_Mgt" width="150px" style="display: <s:property value="%{enableHA?'':'none'}"/>"><s:radio label="Gender"
														id="haPort" name="haPort"
														list="#{'mgt':'Use MGT Port for HA'}" value="%{haPort}"
														disabled="%{writeDisable4HA}" onclick="changeHmPort('mgt');" /></td>
													<td id="HA_Lan" style="display: <s:property value="%{enableHA?'':'none'}"/>"><s:radio label="Gender"
														id="haPort" name="haPort"
														list="#{'lan':'Use LAN Port for HA'}" value="%{haPort}"
														disabled="%{writeDisable4HA}" onclick="changeHmPort('lan');" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
										<tr id="normalSection">
											<td colspan="3">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td style="padding-left: 15px;">
													<fieldset style="<s:if test="%{hMOnline}">width: 300px</s:if><s:else>width: 640px</s:else>"><legend>
													<s:if test="%{hMOnline}">
													<s:text name="home.hmSettings.interfaceSettings" />
													</s:if><s:else>
													<s:text name="home.hmsettings.node.settings" /></s:else> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td class="labelT1" width="150"><label> <s:text
																name="admin.ha.mgtInterfaceIP" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="ip_eth0" name="ip_eth0"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.ha.mgtInterfaceMask" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="mask_eth0" name="mask_eth0"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.defaultGateway" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="defaultGateway"
																name="defaultGateway" maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr style="display:<s:property value="%{hide4HHM}"/>;">
															<td style="padding-left: 10px; padding-top: 5px;"
																colspan="2"><s:checkbox name="enableLan"
																id="cbEnableLan"
																onclick="selectedEnableLan(this.checked)" /> <label>
															<s:text name="home.hmsettings.node.enablelan" /> </label></td>
														</tr>
														<tr id="ip_eth1_tr" style="<s:if test="%{!enableLan}">color:gray;</s:if> display:<s:property value="%{hide4HHM}"/>;">
															<td class="labelT1"><label> <s:text
																name="admin.ha.lanInterfaceIP" /><font color="red"><s:text
																name="*" /> </font>  </label></td>
															<td><s:textfield id="ip_eth1" name="ip_eth1"
																maxlength="%{ipAddressLength}" readonly="%{disabledLan}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr id="mask_eth1_tr" style="<s:if test="%{!enableLan}">color:gray;</s:if> display:<s:property value="%{hide4HHM}"/>;">
															<td class="labelT1"><label> <s:text
																name="admin.ha.lanInterfaceMask" /><font color="red"><s:text
																name="*" /> </font>  </label></td>
															<td><s:textfield id="mask_eth1" name="mask_eth1"
																maxlength="%{ipAddressLength}" readonly="%{disabledLan}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td height="5"></td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
													<s:if test="%{hMOnline}">
														<td style="padding-left:10px;">
															<fieldset style="width: 300px"><legend>
													<s:text name="admin.interface.dnsConf" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td class="labelT1" width="120px"><label> <s:text
																name="admin.interface.primaryDns" /> </label></td>
															<td><s:textfield id="primaryDNS" name="primaryDNS"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.secondDns" /> </label></td>
															<td><s:textfield id="secondDNS" name="secondDNS"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.thirdDns" /> </label></td>
															<td><s:textfield id="tertiaryDNS" name="tertiaryDNS"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td height="5"></td>
														</tr>
													</table>
													</div>
													</fieldset>	
														</td>
													</s:if>
												</tr>
												<s:if test="%{! hMOnline}">
												<tr><td height="5"></td></tr>
													<tr>
														<td class="labelT1" style="<s:if test="enableHA">color:gray;</s:if>"><s:checkbox name="enableSeparateExternalDb"
															id="enableSeparateExternalDb" onclick="selectSeparateExternalDb(this.checked);" disabled="%{enableHA}"  /><label>
														<s:text name="admin.ha.enableExternalDB" /> </label></td>
													</tr>
													<tr id="Separate_ExternalDb" style="display: <s:property value="%{enableSeparateExternalDb?'':'none'}"/>">
														<td style="padding-left: 15px;">
														<fieldset style="width: 640px">
														<div>
														<table cellspacing="0" cellpadding="0" border="0"
															width="100%">
															<tr>
																<td height="5" colspan="2"></td>
															</tr>
															<tr>
																<td class="labelT1" width="180px"><label> <s:text
																	name="admin.externaldb.database.ip" /><font color="red"><s:text
																	name="*" /> </font></label></td>
																<td><s:textfield id="remoteDbIp"
																	name="remoteDbIp" maxlength="%{ipAddressLength}"
																	onkeypress="return hm.util.keyPressPermit(event,'ip');" />
																</td>
															</tr>
															<tr>
																<td class="labelT1" width="180px"><label> <s:text
																	name="admin.externaldb.ssh.password" /><font color="red"><s:text
																	name="*" /> </font> </label></td>
																<td><s:password id="remoteDbSshPwd"
																		name="remoteDbSshPwd" maxlength="64" showPassword="true" 
																		onkeypress="return hm.util.keyPressPermit(event,'password');" />
																	<s:textfield id="remoteDbSshPwd_text" name="remoteDbSshPwd" 
																		disabled="true" maxlength="64" 
																		cssStyle="display:none" /> 
																</td>
															</tr>
															<tr>
															<td>&nbsp;</td>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:checkbox id="chkToggleDisplay_RemoteDb"
																		name="ignore" value="true" 
																		onclick="hm.util.toggleObscurePassword(this.checked,['remoteDbSshPwd'],['remoteDbSshPwd_text']);" />
																	</td>
																	<td><s:text name="admin.externaldb.obscurePassword" />
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														</table>
														</div>
														</fieldset>
														</td>
													</tr>
												<tr><td height="10"></td></tr>
												<tr>
													<td style="padding-left: 15px;">
													<fieldset style="width: 640px"><legend>
													<s:text name="admin.interface.dnsConf" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="5"></td>
														</tr>
														<tr>
															<td class="labelT1" width="180px"><label> <s:text
																name="admin.interface.primaryDns" /> </label></td>
															<td><s:textfield id="primaryDNS"
																name="primaryDNS" maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
															<td class="labelT1" width="180px" style="padding-left:40px"><label> <s:text
																name="admin.interface.secondDns" /> </label></td>
															<td><s:textfield id="secondDNS" name="secondDNS"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.thirdDns" /> </label></td>
															<td><s:textfield id="tertiaryDNS"
																name="tertiaryDNS" maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
												<tr><td height="5"></td></tr>
												</s:if>
												<tr style="display:<s:property value="%{hide4HHM}"/>">
													<td style="padding-left: 15px; padding-top: 5px;"
														colspan="2">
													<fieldset style="width: 640px"><legend>
													<s:text name="admin.interface.proxySetting" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding-left: 10px;" colspan="2"><s:checkbox
																name="enableProxy" id="enableProxy"
																onclick="selectedEnableProxy(this.checked)" /> <label>
															<s:text name="admin.interface.enableProxy" /> </label></td>
														</tr>
														<tr>
															<td class="labelT1" width="260px"><label> <s:text
																name="admin.interface.proxyServer" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="proxyServer" name="proxyServer"
																maxlength="128"
																readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'name');" />
																<s:text name="admin.interface.proxyServer.Range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.proxyPort" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="proxyPort" name="proxyPort"
																maxlength="5" readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />
															<s:text name="admin.interface.proxyPort.portRange" /></td>
														</tr>
														<tr>
															<td class="labelT1" width="120px"><label> <s:text
																name="admin.interface.username" /> </label></td>
															<td><s:textfield id="proxyUserName"
																name="proxyUserName" maxlength="%{64}"
																readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'name');" />
																<s:text name="admin.interface.namepass.Range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1" width="150px"><label> <s:text
																name="admin.interface.password" /> </label></td>
															<td><s:password id="proxyPassword"
																name="proxyPassword" size="20" maxlength="64"
																showPassword="true" readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'password');" />
															<s:textfield id="proxyPassword_text" name="proxyPassword"
																disabled="true" size="20" maxlength="64"
																cssStyle="display:none" /> <s:text
																name="admin.interface.namepass.Range" /></td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:checkbox id="chkToggleDisplay_Proxy"
																		name="ignore" value="true" disabled="%{disabledProxy}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['proxyPassword'],['proxyPassword_text']);" />
																	</td>
																	<td><s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1" colspan="2"><input type="button"
																name="testLS" value="<s:text name="admin.interface.testProxy"/>"
																width="250px" onClick="testConnect2LS();"></td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="haSection">
											<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td style="padding-left: 15px;" colspan="2">
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr style="display: none;">
															<td class="labelT1" colspan="2"><s:checkbox
																name="enableFallback" id="enableFallback"
																disabled="%{writeDisable4HA}" /> <label> <s:text
																name="admin.ha.enableFallback" /> </label></td>
														</tr>
													</table>
													</td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td style="padding-left: 15px;">
													<fieldset style="width: 677px"><legend>
													<s:text name="home.hmsettings.node.settings" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td>
																<table cellspacing="0" cellpadding="0" border="0" width="100%">
																<tr>
																	<td height="10" colspan="2"></td>
																</tr>
																<tr>
																	<td class="labelT1" colspan="2" align="center">
																		<s:text name="home.hmsettings.primary.node"/>
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="180px"><label> <s:text
																		name="admin.ha.mgtInterfaceIP" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="primaryMGTIP"
																		name="primaryMGTIP" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr>
																	<td class="labelT1"><label> <s:text
																		name="admin.ha.mgtInterfaceMask" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="primaryMGTMask"
																		name="primaryMGTMask" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr>
																	<td class="labelT1"><label> <s:text
																		name="admin.interface.defaultGateway" /> <font
																		color="red"><s:text name="*" /> </font> </label></td>
																	<td><s:textfield id="primaryGateway"
																		name="primaryGateway" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr id="HaPort_LanIp" style="display: <s:property value="%{haPort=='mgt'?'none':''}"/>">
																	<td class="labelT1"><label> <s:text
																		name="admin.ha.lanInterfaceIP" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="primaryLANIP"
																		name="primaryLANIP" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr id="HaPort_LanNetmask"  style="display: <s:property value="%{haPort=='mgt'?'none':''}"/>">
																	<td class="labelT1"><label> <s:text
																		name="admin.ha.lanInterfaceMask" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="primaryLANMask"
																		name="primaryLANMask" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
															</table>
														</td>
														<td style="padding-left: 6px;">
															<table cellspacing="0" cellpadding="0" border="0"
																width="100%">
																<tr>
																	<td class="labelT1" colspan="2" align="center">
																		<s:text name="home.hmsettings.secondary.node"/>
																	</td>
																</tr>
																<tr>
																	<td class="labelT1" width="180px"><label> <s:text
																		name="admin.ha.mgtInterfaceIP" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="secondaryMGTIP"
																		name="secondaryMGTIP" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr  id="HaPort_SecLanIp" style="display: <s:property value="%{haPort=='mgt'?'none':''}"/>">
																	<td class="labelT1"><label> <s:text
																		name="admin.ha.lanInterfaceIP" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:textfield id="secondaryLANIP"
																		name="secondaryLANIP" maxlength="%{ipAddressLength}"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');"
																		readonly="%{writeDisable4HA}" /></td>
																</tr>
																<tr>
																	<td class="labelT1"><label> <s:text name="admin.externaldb.ssh.password" /><font color="red"><s:text
																		name="*" /> </font> </label></td>
																	<td><s:password id="haSecret" name="haSecret" maxlength="64" showPassword="true" readonly="%{writeDisable4HA}" /> <s:textfield
																		id="haSecret_text" name="haSecret" disabled="true"
																		maxlength="64" cssStyle="display:none" onkeypress="return keyPressPermit(event);"/></td>
																</tr>
																<tr>
																	<td>&nbsp;</td>
																	<td>
																	<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><s:checkbox id="chkToggleDisplay" name="ignore" value="true" disabled="%{writeDisable4HA}"
																				onclick="hm.util.toggleObscurePassword(this.checked,['haSecret'],['haSecret_text']);" />
																			</td>
																			<td><s:text name="admin.user.obscurePassword" />
																			</td>
																		</tr>
																	</table>
																	</td>
																</tr>
																<tr id="HA_Primary_Node_Tr1" style="display:<s:property value="%{haPort=='mgt'?'none':''}"/>"><td>&nbsp;</td></tr>
																<tr id="HA_Primary_Node_Tr2" style="display:<s:property value="%{haPort=='mgt'?'none':''}"/>"><td>&nbsp;</td></tr>
															</table>
														</td>
													</tr>
													<tr id="HA_Mgt_ExternalIpHost" style="display: <s:property value="%{haPort=='mgt'?'':'none'}"/>">
														<td colspan="2">
															<table>
																<tr>
																	<td  class="labelT1" colspan="2"><s:checkbox name="enableExternalIP" id="enableExternalIP" onclick="selectEnableExternalIP(this.checked);"
																			disabled="%{writeDisable4HA}" />
																			<label><s:text name="home.hmsettings.externalip.hostname.settings" /> </label>
																	</td>
																</tr>
																<tr id="HA_Mgt_ExternalIp" style="display:<s:property value="enableExternalIP?'':'none'"/>">
																	<td class="labelT1">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td class="labelT1" width="178px" style="padding-left: 0px;">
																				<s:text name="admin.ha.externalip.hostname" /><font color="red"><s:text name="*" /> </font>
																				</td>
																				<td><s:textfield id="primaryExternalIP" name="primaryExternalIP" onkeypress="return keyPressPermit(event);"
																					readonly="%{writeDisable4HA}" />
																			    </td>
																			</tr>
																		</table>
																	</td>
																	<td class="labelT1">
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td style="padding-left: 24px;" class="labelT1" width="179px"><label> <s:text name="admin.ha.externalip.hostname" /><font color="red"><s:text
																					name="*" /> </font> </label>
																				</td>
																				<td><s:textfield id="secondaryExternalIP" name="secondaryExternalIP"
																					onkeypress="return keyPressPermit(event);" readonly="%{writeDisable4HA}" />
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
												</div>
												</fieldset>
												</td>
												</tr>
												<tr>
													<td height="5"></td>
												</tr>
												<s:if test="%{!hMOnline}">
												<tr>
													<td class="labelT1" style="<s:if test="!writeDisable4HA && enableHA">color:gray;</s:if>">
													<s:checkbox name="enableExternalDb"
														id="enableExternalDb" onclick="selectEnableExternalDb(this.checked);" disabled="%{enableHA}" /> <label>
													<s:text name="admin.ha.enableExternalDB" /> </label></td>
												</tr>
												<tr id="HA_ExternalDb" style="display:<s:property value="%{enableExternalDb?'':'none'}"/>">
													<td style="padding-left: 15px;" colspan="2">
													<fieldset style="width: 677px">
													<div>
													<table cellspacing="0" cellpadding="0" border="0" width="100%">
														<tr>
															<td height="10" colspan="2"></td>
														</tr>
														<tr>
															<td class="labelT1">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr><td colspan="2" align="center"><s:text name="admin.externaldb.primary.database" /></td></tr>
																	<tr>
																		<td class="labelT1" width="172px"><label> <s:text name="admin.externaldb.database.ip" /> <font
																					color="red"><s:text name="*" /> </font> </label></td>
																		<td><s:textfield id="haPrimaryDbIp" name="haPrimaryDbIp" maxlength="%{ipAddressLength}" readonly="%{writeDisable4HA}"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1"><label> <s:text name="admin.externaldb.ssh.password" /> <font
																					color="red"><s:text name="*" /> </font> </label></td>
																		<td><s:password id="haPrimaryDbPwd" name="haPrimaryDbPwd" maxlength="64" showPassword="true" readonly="%{writeDisable4HA}" /> <s:textfield
																			id="haPrimaryDbPwd_text" name="haPrimaryDbPwd" disabled="true"
																			maxlength="64" cssStyle="display:none" /></td>
																	</tr>
																	<tr>
																		<td>&nbsp;</td>
																		<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><s:checkbox id="chkToggleDisplay_haPriDb" name="ignore" value="true" disabled="%{writeDisable4HA}"
																					onclick="hm.util.toggleObscurePassword(this.checked,['haPrimaryDbPwd'],['haPrimaryDbPwd_text']);" />
																				</td>
																				<td><s:text name="admin.externaldb.obscurePassword" />
																				</td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																</table>
															</td>
															<td class="labelT1">
																<table cellspacing="0" cellpadding="0" border="0">
																	<tr><td colspan="2" align="center"><s:text name="admin.externaldb.secondary.database" /></td></tr>
																	<tr>
																		<td class="labelT1" width="170px" style="padding-left:20px"><label> <s:text
																			name="admin.externaldb.database.ip" /><font
																					color="red"><s:text name="*" /> </font>  </label></td>
																		<td><s:textfield id="haSecondaryDbIp" name="haSecondaryDbIp" maxlength="%{ipAddressLength}" readonly="%{writeDisable4HA}"
																			onkeypress="return hm.util.keyPressPermit(event,'ip');" /></td>
																	</tr>
																	<tr>
																		<td class="labelT1" style="padding-left:20px"><label> <s:text name="admin.externaldb.ssh.password" /> <font
																					color="red"><s:text name="*" /> </font> </label></td>
																		<td><s:password id="haSecondaryDbPwd" name="haSecondaryDbPwd" maxlength="64" showPassword="true" readonly="%{writeDisable4HA}" /> <s:textfield
																			id="haSecondaryDbPwd_text" name="haSecondaryDbPwd" disabled="true"
																			maxlength="64" cssStyle="display:none" /></td>
																	</tr>
																	<tr>
																		<td>&nbsp;</td>
																		<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td><s:checkbox id="chkToggleDisplay_haSecDb" name="ignore" value="true" disabled="%{writeDisable4HA}"
																					onclick="hm.util.toggleObscurePassword(this.checked,['haSecondaryDbPwd'],['haSecondaryDbPwd_text']);" />
																				</td>
																				<td><s:text name="admin.externaldb.obscurePassword" />
																				</td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td height="10" colspan="2"></td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
												</s:if>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td style="padding-left: 15px;" colspan="2">
													<fieldset style="width: 677px"><legend>
													<s:text name="admin.interface.dnsConf" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="5"></td>
														</tr>
														<tr>
															<td class="labelT1" width="180px"><label> <s:text
																name="admin.interface.primaryDns" /> </label></td>
															<td><s:textfield id="haPrimaryDNS"
																name="haPrimaryDNS" maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
															<td class="labelT1" width="174px" style="padding-left:28px"><label> <s:text
																name="admin.interface.secondDns" /> </label></td>
															<td><s:textfield id="haSecondDNS" name="haSecondDNS"
																maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.thirdDns" /> </label></td>
															<td><s:textfield id="haTertiaryDNS"
																name="haTertiaryDNS" maxlength="%{ipAddressLength}"
																onkeypress="return hm.util.keyPressPermit(event,'ip');" />
															</td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
												<tr style="display:<s:property value="%{show4HHMHA}"/>" id="haEmailSection">
													<td style="padding-left: 15px; padding-top: 5px;"colspan="2">
														<fieldset style="width: 680px"><legend>
														<s:text name="admin.interface.ha.email.title" /> </legend>
														<div>
														<table cellspacing="0" cellpadding="0" border="0"
															width="100%">
															<tr>
																<td height="10"></td>
															</tr>
															<tr>
																<td class="labelT1" width="120px"><label> <s:text
																	name="admin.interface.ha.email" /></label></td>
																<td><s:textfield id="haNotifyEmail"
																	name="haNotifyEmail" size="48" maxlength="128" />
																	<s:text name="admin.email.address.range" />
																</td>
															</tr>
															<tr>
																<td height="10"></td>
															</tr>
														</table>
														</div>
														</fieldset>
													</td>
												</tr>
												<tr><td height="5"></td></tr>
												<tr style="display:<s:property value="%{hide4HHM}"/>">
													<td style="padding-left: 15px; padding-top: 5px;"
														colspan="2">
													<fieldset style="width: 680px"><legend>
													<s:text name="admin.interface.proxySetting" /> </legend>
													<div>
													<table cellspacing="0" cellpadding="0" border="0"
														width="100%">
														<tr>
															<td height="10"></td>
														</tr>
														<tr>
															<td style="padding-left: 10px;" colspan="2"><s:checkbox
																name="haEnableProxy" id="haEnableProxy"
																onclick="selectedHAEnableProxy(this.checked)" /> <label>
															<s:text name="admin.interface.enableProxy" /> </label></td>
														</tr>
														<tr>
															<td class="labelT1" width="260px"><label> <s:text
																name="admin.interface.proxyServer" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="haProxyServer"
																name="haProxyServer" maxlength="128"
																readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'name');" />
																<s:text name="admin.interface.proxyServer.Range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1"><label> <s:text
																name="admin.interface.proxyPort" /><font color="red"><s:text
																name="*" /> </font> </label></td>
															<td><s:textfield id="haProxyPort" name="haProxyPort"
																maxlength="5" readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'ten');" />
															<s:text name="admin.interface.proxyPort.portRange" /></td>
														</tr>
														<tr>
															<td class="labelT1" width="120px"><label> <s:text
																name="admin.interface.username" /> </label></td>
															<td><s:textfield id="haProxyUserName"
																name="haProxyUserName" maxlength="%{64}"
																readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'name');" />
																<s:text name="admin.interface.namepass.Range" />
															</td>
														</tr>
														<tr>
															<td class="labelT1" width="150px"><label> <s:text
																name="admin.interface.password" /> </label></td>
															<td><s:password id="haProxyPassword"
																name="haProxyPassword" size="20" maxlength="64"
																showPassword="true" readonly="%{disabledProxy}"
																onkeypress="return hm.util.keyPressPermit(event,'password');" />
															<s:textfield id="haProxyPassword_text"
																name="haProxyPassword" disabled="true" size="20"
																maxlength="64" cssStyle="display:none" /> <s:text
																name="admin.interface.namepass.Range" /></td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td>
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td><s:checkbox id="chkToggleDisplay_HAProxy"
																		name="ignore" value="true" disabled="%{disabledProxy}"
																		onclick="hm.util.toggleObscurePassword(this.checked,['haProxyPassword'],['haProxyPassword_text']);" />
																	</td>
																	<td><s:text name="admin.user.obscurePassword" />
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td class="labelT1" colspan="2"><input type="button"
																name="testLS" value="<s:text name="admin.interface.testProxy"/>"
																width="250px" onClick="testConnect2LS();"></td>
														</tr>
													</table>
													</div>
													</fieldset>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
			</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4VHM}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="loginAccessSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.loginAccessList" /> </span> <a
								href="#showOptions" onclick="clickLoginAccessSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" style="display: none;"
								id="loginAccessSaveDiv"><span class="settingCaption"><s:text
								name="home.hmSettings.editLoginAccess" /> </span> <a
								href="#saveOptions" onclick="saveLoginAccessOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitLoginAccessOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="loginAccessView">
									<table cellspacing="0" cellpadding="0" border="0">
										<s:if test="%{emptyLoginAccess}">
											<ah:emptyList />
										</s:if>
										<s:else>
											<tr>
												<td class="labelT1" width="120px"><s:text
													name="hm.access.control.type" /></td>
												<td class="showValue"><s:property
													value="accessControlTypeShow" /> &nbsp;</td>
											</tr>
											<tr>
												<td class="labelT1"><s:text
													name="hm.access.control.behavior" /></td>
												<td class="showValue"><s:property
													value="denyBehaviorShow" /> &nbsp;</td>
											</tr>
											<tr>
												<td class="labelT1"><s:text
													name="hm.access.control.ipList" /></td>
												<td class="labelT1">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<th><s:text name="hm.access.control.ip" /></th>
														<th><s:text name="hm.access.control.mask" /></th>
													</tr>
													<s:if test="%{ipAddressList.size() == 0}">
														<ah:emptyList />
													</s:if>
													<s:iterator value="ipAddressList" status="status">
														<tr>
															<td class="list"><s:property value="ipAddress" /></td>
															<td class="list"><s:property value="netmask" /></td>
														</tr>
													</s:iterator>
												</table>
												</td>
											</tr>
										</s:else>
										<tr>
											<td height="0px">
											<div id="loginAccessSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="loginAccessOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td><!-- access control type -->
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="145px"><s:text
																name="hm.access.control.type"></s:text></td>
															<td width="65px"><s:radio
																onclick="controlTypeChanged(this);" label="Gender"
																name="accessControlType" list="%{controlType1}"
																listKey="key" listValue="value" /></td>
															<td><s:radio onclick="controlTypeChanged(this);"
																label="Gender" name="accessControlType"
																list="%{controlType2}" listKey="key" listValue="value" />
															</td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td height="10px"></td>
										</tr>
										<tr id="allowSection"
											style="display:<s:property value="%{allowStyle}"/>">
											<td><!-- allowed access -->
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="hm.access.control.allow"></s:text></td>
													<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr id="newAllowButton">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td width="80px"><input type="button"
																		name="ignore" value="Add" class="button"
																		onClick="showAllowCreateSection();"
																		<s:property value="writeDisabled" />></td>
																	<td><input type="button" name="ignore"
																		value="Remove" class="button"
																		onClick="removeAllowIpAddress();"
																		<s:property value="writeDisabled" />></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display: none;" id="createAllowButton">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td width="80px"><input type="button"
																		name="ignore" value="Apply" class="button"
																		onClick="addAllowIpAddress();"></td>
																	<td><input type="button" name="ignore"
																		value="Cancel" class="button"
																		onClick="hideAllowCreateSection();"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display: none;" id="allowCreateSection">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td style="padding-left: 0;" class="labelT1 listHead"
																		width="70px"><s:text name="hm.access.control.ip"></s:text>
																	</td>
																	<td class="listHead"><s:textfield id="allowIp"
																		size="18" maxlength="18"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');" />
																	</td>

																	<td class="labelT1 listHead" width="60px"><s:text
																		name="hm.access.control.mask"></s:text></td>
																	<td class="listHead"><s:textfield id="allowMask"
																		size="18" maxlength="18"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');" />
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="5px"></td>
														</tr>
														<tr>
															<td colspan="2"><s:select multiple="true" size="12"
																id="allowedIps" name="allowedIps" list="%{allowedIps}"
																cssStyle="width: 220px;" /></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="denySection"
											style="display:<s:property value="%{denyStyle}"/>">
											<td><!-- denied access -->
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="hm.access.control.deny"></s:text></td>
													<td>
													<table border="0" cellspacing="0" cellpadding="0">
														<tr id="newDenyButton">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td width="80px"><input type="button"
																		name="ignore" value="Add" class="button"
																		onClick="showDenyCreateSection();"
																		<s:property value="writeDisabled" />></td>
																	<td><input type="button" name="ignore"
																		value="Remove" class="button"
																		onClick="removeDenyIpAddress();"
																		<s:property value="writeDisabled" />></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display: none;" id="createDenyButton">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td width="80px"><input type="button"
																		name="ignore" value="Apply" class="button"
																		onClick="addDenyIpAddress();"></td>
																	<td><input type="button" name="ignore"
																		value="Cancel" class="button"
																		onClick="hideDenyCreateSection();"></td>
																</tr>
															</table>
															</td>
														</tr>
														<tr style="display: none;" id="denyCreateSection">
															<td colspan="2">
															<table border="0" cellspacing="0" cellpadding="0">
																<tr>
																	<td style="padding-left: 0;" class="labelT1 listHead"
																		width="70px"><s:text name="hm.access.control.ip"></s:text>
																	</td>
																	<td class="listHead"><s:textfield id="denyIp"
																		size="18" maxlength="18"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');" />
																	</td>

																	<td class="labelT1 listHead" width="60px"><s:text
																		name="hm.access.control.mask"></s:text></td>
																	<td class="listHead"><s:textfield id="denyMask"
																		size="18" maxlength="18"
																		onkeypress="return hm.util.keyPressPermit(event,'ip');" />
																	</td>
																</tr>
															</table>
															</td>
														</tr>
														<tr>
															<td height="5px"></td>
														</tr>
														<tr>
															<td colspan="2"><s:select multiple="true" size="12"
																id="deniedIps" name="deniedIps" list="%{deniedIps}"
																cssStyle="width: 220px;" /></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
										<tr>
											<td height="5px"></td>
										</tr>
										<tr>
											<td><!-- deny behavior -->
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="labelT1" width="150px"><s:text
														name="hm.access.control.behavior"></s:text></td>
													<td><s:select name="denyBehavior"
														list="%{denyBehaviors}" listKey="key" listValue="value"
														cssStyle="width: 220px;"></s:select></td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4VHM}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="routingSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.routing" /> </span> <a href="#showOptions"
								onclick="clickRoutingSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="routingCancelDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editRouting" /> </span> <a href="#exitOptions"
								onclick="exitRoutingOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="routingView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<th><s:text name="admin.routing.destination" /></th>
											<th><s:text name="admin.routing.mask" /></th>
											<th><s:text name="admin.routing.gateway" /></th>
										</tr>
										<s:if test="%{routeList.size() == 0}">
											<ah:emptyList />
										</s:if>
										<s:iterator value="routeList" status="status">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="list"><s:property value="dest" /></td>
												<td class="list"><s:property value="mask" /></td>
												<td class="list"><s:property value="gateway" /></td>
											</tr>
										</s:iterator>
										<tr>
											<td height="0px">
											<div id="routingSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="routingOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td class="buttons" colspan="4">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr id="newButton">
													<td><input type="button" id="addRouteBtn" name="add"
														value="Add" class="button" onClick="showCreateSection();"
														<s:property value="writeDisabled" />></td>
													<td><input type="button" id="removeRouteBtn"
														name="remove" value="Remove" class="button"
														onClick="removeRoute();"
														<s:property value="writeDisabled" />></td>
												</tr>
												<tr style="display: none;" id="addRouteTR">
													<td><input type="button" name="ignore" value="Apply"
														class="button" onClick="addRoute();"></td>
													<td><input type="button" name="ignore" value="Cancel"
														class="button" onClick="hideCreateSection();"></td>
												</tr>
											</table>
											</td>
										</tr>
										<tr id="headerSection">
											<th class="check"><input type="checkbox" id="checkAll"
												onClick="hm.util.toggleCheckAll(this);"></th>
											<th><s:text name="admin.routing.destination" /></th>
											<th><s:text name="admin.routing.mask" /></th>
											<th><s:text name="admin.routing.gateway" /></th>
										</tr>
										<tr style="display: none;" id="inputRouteSection">
											<td class="listHead"></td>
											<td class="listHead" valign="top"><s:textfield
												id="routeDest" name="routeDest" size="30" maxlength="15"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" />
											</td>
											<td class="listHead" valign="top"><s:textfield
												id="routeMask" name="routeMask" size="30" maxlength="16"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" />
											</td>
											<td class="listHead" valign="top"><s:textfield
												id="routeGateway" name="routeGateway" size="30"
												maxlength="15"
												onkeypress="return hm.util.keyPressPermit(event,'ip');" />
											</td>
										</tr>
										<s:if test="%{routeList.size() == 0}">
											<ah:emptyList />
										</s:if>
										<tiles:insertDefinition name="selectAll" />
										<s:iterator value="routeList" status="status">
											<tiles:insertDefinition name="rowClass" />
											<tr class="<s:property value="%{#rowClass}"/>">
												<td class="listCheck"><ah:checkItem /></td>
												<td class="list"><s:property value="dest" /></td>
												<td class="list"><s:property value="mask" /></td>
												<td class="list"><s:property value="gateway" /></td>
											</tr>
										</s:iterator>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4VHM}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="certSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.httpCert" /> </span> <a href="#showOptions"
								onclick="clickCertSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="certSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editCert" /> </span> <a href="#saveOptions"
								onclick="saveCertOption();"> <img alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitCertOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="certView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td style="padding: 10px 10px 20px 10px"><label>
											<s:text name="%{certDetails}" /> </label></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="certSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="certOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td style="padding: 0 0 0 15px"><s:radio label="Gender"
												id="" name="updateCertType"
												list="#{'selfSignedCert':'Generate Self-Signed Certificate'}"
												onclick="selectSelfSigned(this.checked);"
												value="%{updateCertType}" /></td>
										</tr>
										<tr>
											<td style="padding: 5px 0 0 15px"><s:radio
												label="Gender" id="" name="updateCertType"
												list="#{'importCert':'Import Server Certificate'}"
												onclick="selectImportCert(this.checked);"
												value="%{updateCertType}" /></td>
										</tr>
										<tr style="display:<s:property value="%{hideImportCert}"/>"
											id="importCertSection">
											<td style="padding-left: 20px;">
											<table cellspacing="0" cellpadding="0" border="0"
												width="100%">
												<tr>
													<td class="labelT1" width="150"><label> <s:text
														name="admin.installCert.certificate" /><font color="red"><s:text
														name="*" /> </font> </label></td>
													<td style="padding: 0 15px 0 15px"><s:select
														id="certificateFile" name="certificateFile"
														cssStyle="width: 303px;" list="%{availableCaFile}"
														value="certificateFile" /> <input type="button"
														value="Import" class="button short"
														onClick="submitAction('importCert');"></td>
												</tr>
												<tr>
													<td class="labelT1" width="150"><label> <s:text
														name="admin.installCert.privateKey" /><font color="red"><s:text
														name="*" /> </font> </label></td>
													<td style="padding: 0 15px 0 15px"><s:select
														id="privateKey" name="privateKeyFile"
														cssStyle="width: 303px;" list="%{availableKeyFile}"
														value="privateKeyFile" /> <input type="button"
														value="Import" class="button short"
														onClick="submitAction('importKey');"></td>
												</tr>
												<tr>
													<td class="labelT1"><label> <s:text
														name="admin.installCert.passphrase" /><font color="red"><s:text
														name="*" /> </font> </label></td>
													<td style="padding: 0 15px 0 15px"><s:password
														id="certPassPhrase" name="passPhrase" size="32"
														maxlength="32" /> <s:textfield id="certPassPhrase_text"
														name="passPhrase" size="32" maxlength="32"
														cssStyle="display:none" disabled="true" /><s:text name="admin.management.webSecurity.webSenseDefaultUserName.range" /></td>
												</tr>
												<tr>
													<td class="labelT1"><label> <s:text
														name="admin.installCert.confirmPassphrase" /> <font
														color="red"><s:text name="*" /> </font> </label></td>
													<td style="padding: 0 15px 0 15px"><s:password
														id="confirmCertPass" name="confirmPassPhrase" size="32"
														maxlength="32" /> <s:textfield id="confirmCertPass_text"
														name="confirmPassPhrase" size="32" maxlength="32"
														cssStyle="display:none" /><s:text name="admin.management.webSecurity.webSenseDefaultUserName.range" /></td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td style="padding-left: 15px;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td><s:checkbox id="chkToggleDisplay_cert"
																name="ignore" value="true"
																onclick="hm.util.toggleObscurePassword(this.checked,['certPassPhrase','confirmCertPass'],['certPassPhrase_text','confirmCertPass_text']);" />
															</td>
															<td><s:text name="admin.user.obscurePassword" /></td>
														</tr>
													</table>
													</td>
												</tr>
											</table>
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4VHM}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="sshSettingDiv"><span
								class="settingCaption"><s:text name="home.hmSettings.ssh" />
							</span> <a href="#showOptions" onclick="clickSSHSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="sshSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editSSH" /> </span> <a href="#saveOptions"
								onclick="saveSSHOption();"> <img alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitSSHOption();"> <img alt="Cancel" title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="sshView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text
												name="home.hmSettings.sshPort" /></td>
											<td class="showValue"><s:property value="sshPortNumber" /></td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="sshSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="sshOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<s:if test="%{!oEMSystem}">
										<tr>
											<td colspan="2" class="noteInfo"><s:text
												name="admin.management.sshPortNote" /></td>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
										</s:if>
										<tr>
											<td width="150px"><s:text
												name="admin.management.portNumber" /></td>
											<td><s:textfield id="sshPortNumber" name="sshPortNumber"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"
												maxlength="5" cssStyle="width: 95px;" /> <s:text
												name="admin.management.sshPortRange" /></td>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td><s:checkbox name="sshKeyGen" id="sshKeyGen"
												onclick="selectSSHKeyGen(this.checked);" /> <label>
											<s:text name="admin.management.sshKeyGen" /> </label></td>
											<td><s:select id="genAlgorithm" name="genAlgorithm"
												value="%{genAlgorithm}" list="enumAlgorithm" listKey="key"
												listValue="value" disabled="true" /></td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr style="display:<s:property value="%{hide4HHM}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="sessionSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.session" /> </span> <a href="#showOptions"
								onclick="clickSessionSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="sessionSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editSession" /> </span> <a href="#saveOptions"
								onclick="saveSessionOption();"> <img alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitSessionOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="sessionView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text
												name="home.hmSettings.sessionExpiration" /></td>
											<td class="showValue"><s:property
												value="sessionExpirationShow" /></td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="sessionSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="sessionOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td class="labelT1" width="200px"><s:checkbox
													name="finiteSession" id="finiteSession"
													onclick="selectFiniteSession(this.checked);" /> 
												<s:text name="admin.management.adminSessionTimeout" />
											</td>	
											<td>
												<s:textfield id="sessionExpiration"
														name="sessionExpiration"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="3" cssStyle="width: 95px;"
														disabled="%{disabledSession}" /> 
												<s:text name="admin.management.adminSessioRange" />
											</td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<s:if test="%{!oEMSystem}">
				<!--for vhm can enable cop collect app data option,now all vhm can see this setting-->
				<tr style="display:">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="improveSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.improve" /> </span> <a href="#showOptions"
								onclick="clickImproveSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a></div>
							<div class="settingsToolBar" id="improveSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editImprove" /> </span> <a href="#saveOptions"
								onclick="saveImproveOption();"> <img alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitImproveOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a></div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="improveView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<s:if test="%{isInHomeDomain}">
										<tr>
											<td class="labelT1" width="450px"><s:text
												name="home.hmSettings.improveParticipate" /></td>
											<td class="showValue"><s:property
												value="improveParticipateShow" /></td>
										</tr>
										</s:if>
										<tr>
											<td class="labelT1" width="450px"><s:text name="home.hmSettings.enableCollectAppData" /></td>
											<td class="showValue"><s:if test="%{enableCollectAppData == true}">Yes</s:if><s:else>No</s:else></td>
										</tr>
									<!--<tr>
											<td class="labelT1" width="350px"><s:text
												name="home.hmSettings.dataOfImprovement" /></td>
											<td class="showValue"><s:property
												value="dataOfImprovementShow" /></td>
										</tr> -->
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="improveSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="improveOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<s:if test="%{isInHomeDomain}">
										<tr>
											<td colspan="2"><s:checkbox
												name="participateImprovement" id="participateImprovement" />
											<label> <s:text
												name="admin.management.participateImprovement" /> </label></td>
										</tr>
										<tr>
											<td height="5"></td>
										</tr>
										</s:if>
										<tr>
											<td colspan="2"><s:checkbox name="enableCollectAppData" id="enableCollectAppData" />
											<label> <s:text name="home.hmSettings.enableCollectAppData" /> </label></td>
										</tr>
									<!--<tr>
											<td colspan="2"><s:checkbox
												name="dataOfImprovement" id="dataOfImprovement" />
											<label> <s:text
												name="admin.management.dataOfImprovement" /> </label></td>
										</tr> -->
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				</s:if>
				<!-- express mode -->
				<s:if test="%{oEMSystem && !containsExpressModeVHM}">
				<tr style="display:<s:property value="%{hide4VHM}"/>">
                    <td style="padding: 10px 10px 0px 15px;">
                    <fieldset style="padding: 0px;">
                    <div>
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="expressModeSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.expressMode" /> </span> <a href="#showOptions"
								onclick="clickExpressModeSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="expressModeSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editExpressMode" /> </span> <a
								href="#saveOptions" onclick="saveExpressModeOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitExpressModeOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
                            <td style="padding: 0px 10px 0px 10px;" valign="top">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="expressModeView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="350px"><s:text
												name="home.hmSettings.expressModeSetting" /></td>
											<td class="showValue"><s:property
												value="expressModeShow" /></td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="expressModeSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="expressModeOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td colspan="2"><s:checkbox name="enableExpressMode"
												id="expressMode" /> <label> <s:text
												name="home.hmSettings.expressModeSetting" /> </label></td>
										</tr>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
                            </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>
                </s:if>
                <!-- Log Expiration -->
				<tr style="display:<s:property value="%{hide4VHM}"/>">
                    <td style="padding: 10px 10px 0px 15px;">
                    <fieldset style="padding: 0px;">
                    <div>
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="logExpirationSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.logExpiration" /> </span> <a href="#showOptions"
								onclick="clickLogExpirationSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="logExpirationSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editLogExpiration" /> </span> <a
								href="#saveOptions" onclick="saveLogExpirationOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitLogExpirationOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
                            <td style="padding: 0px 10px 0px 10px;" valign="top">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="logExpirationView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text
												name="home.hmSettings.logExpiration.syslog" /></td>
											<td class="showValue"><s:property
												value="syslogExpirationDays" /> Days</td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text
												name="home.hmSettings.logExpiration.auditlog" /></td>
											<td class="showValue"><s:property
												value="auditlogExpirationDays" /> Days</td>
										</tr>
										<s:if test="%{fullMode}">
											<tr style="display:none">
												<td class="labelT1" width="180px"><s:text
													name="home.hmSettings.logExpiration.l3firewalllog" /></td>
												<td class="showValue"><s:property
													value="l3FirewallLogExpirationDays" /> Days</td>
											</tr>
										</s:if>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="logExpirationSpace"></div>
											</td>
										</tr>
									</table>
									</div>
									<div id="logExpirationOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										<tr>
											<td class="labelT1" width="200px"><label> <s:text
												name="home.hmSettings.logExpiration.syslog" /> </label></td>
											<td><s:textfield id="syslogExpirationDays"
												name="syslogExpirationDays"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"
												maxlength="3" cssStyle="width: 95px;"/>
												<label style="padding-left:5px">
													<s:text name="home.hmSettings.logExpiration.range" />
												</label>
											</td>
										</tr>
										<tr>
											<td class="labelT1" width="200px"><label> <s:text
												name="home.hmSettings.logExpiration.auditlog" /> </label></td>
											<td><s:textfield id="auditlogExpirationDays"
												name="auditlogExpirationDays"
												onkeypress="return hm.util.keyPressPermit(event,'ten');"
												maxlength="3" cssStyle="width: 95px;"/>
												<label style="padding-left:5px">
													<s:text name="home.hmSettings.logExpiration.range" />
												</label>
											</td>
										</tr>
										<s:if test="%{fullMode}">
											<tr style="display:none">
												<td class="labelT1" width="200px"><label> <s:text
													name="home.hmSettings.logExpiration.l3firewalllog" /> </label></td>
												<td><s:textfield id="l3FirewallLogExpirationDays"
													name="l3FirewallLogExpirationDays"
													onkeypress="return hm.util.keyPressPermit(event,'ten');"
													maxlength="3" cssStyle="width: 95px;" />
													<label style="padding-left:5px">
														<s:text name="home.hmSettings.logExpiration.range" />
													</label>
												</td>
											</tr>
										</s:if>
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
                            </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>                
				<tr>
					<td height="10"></td>
				</tr>
				<!-- HiveOS softver update max number settings -->
				<tr style="display:<s:property value="%{maxUploadApSettingsStyle}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<div class="settingsToolBar" id="maxUploadNumSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.maxUploadNum.setting" /> </span> <a href="#showOptions"
								onclick="clickMaxUploadNumSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="maxUploadNumSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.maxUploadNum.setting" /> </span> <a
								href="#saveOptions" onclick="saveMaxUploadNumOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitMaxUploadNumOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
										<div style="position: relative;">
											<div id="maxUploadNumView">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td class="labelT1" width="180px"><s:text
														name="home.hmSettings.maxUploadNum.value" /></td>
													<td class="showValue"><s:property
														value="maxUpdateNum" /></td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td height="0px">
													<div id="maxUploadNumSpace"></div>
													</td>
												</tr>
											</table>
											</div>
											<div id="maxUploadNumOptions" class="options">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1" width="200px"><label> <s:text
														name="home.hmSettings.maxUploadNum.value" /> </label></td>
													<td><s:textfield id="maxUpdateNum"
														name="maxUpdateNum"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="3" cssStyle="width: 95px;" />
														<s:text name="home.hmSettings.maxUploadNum.range"/></td>
												</tr>
											</table>
											</div>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<!-- HiveOS config threads -->
				<tr style="display:<s:property value="%{hide4Standalone}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<div class="settingsToolBar" id="generationThreadsSettingDiv"><span
									class="settingCaption"><s:text
									name="home.hmSettings.maximum.concurrent.generations.setting" /> </span> <a href="#showOptions"
									onclick="clickGenerationThreadsSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
									name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
								</div>
								<div class="settingsToolBar" id="generationThreadsSaveDiv"
									style="display: none;"><span class="settingCaption"><s:text
									name="home.hmSettings.maximum.concurrent.generations.setting" /> </span> <a
									href="#saveOptions" onclick="saveGenerationThreadsOption();"> <img
									alt="Save" title="Save"
									src="<s:url value="/images/save.png" includeParams="none"/>"
									width="16" class="dinl"> </a> <a href="#exitOptions"
									onclick="exitGenerationThreadsOption();"> <img alt="Cancel"
									title="Cancel"
									src="<s:url value="/images/cancel.png" includeParams="none"/>"
									width="16" class="dinl"> </a>
								</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
										<div style="position: relative;">
											<div id="generationThreadsView">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td class="labelT1" width="180px"><s:text
															name="home.hmSettings.maximum.concurrent.generations.title" /></td>
														<td class="showValue"><s:property
															value="concurrentConfigGenNum" /></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
													<tr>
														<td height="0px">
														<div id="generationThreadsSpace"></div>
														</td>
													</tr>
												</table>
											</div>
											<div id="generationThreadsOptions" class="options">
												<table cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td height="5"></td>
													</tr>
													<tr>
														<td style="padding-left: 10px;" class="noteInfo" colspan="2">
															<s:text name="hm.processes.restart"/><br>
														</td>
													</tr>
													<tr>
														<td class="labelT1" width="200px"><label> <s:text
															name="home.hmSettings.maximum.concurrent.generations.title" /> </label></td>
														<td><s:textfield id="concurrentConfigGenNum"
															name="concurrentConfigGenNum"
															onkeypress="return hm.util.keyPressPermit(event,'ten');"
															maxlength="3" cssStyle="width: 95px;" />
															<s:text name="home.hmSettings.maximum.concurrent.generations.range"/></td>
													</tr>
													<tr>
														<td height="10"></td>
													</tr>
												</table>
											</div>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<!-- Concurrent Hivemanager Search User Count settings -->
				<tr style="display:<s:property value="%{hide4Standalone}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
								<div class="settingsToolBar" id="maxSearchUserNumSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.maximum.concurrent.search.usernum.setting" /> </span> <a href="#showOptions"
								onclick="clickMaxSearchUserNumSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="maxSearchUserNumSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.maximum.concurrent.search.usernum.setting" /> </span> <a
								href="#saveOptions" onclick="saveMaxSearchUserNumOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitMaxSearchUserNumOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
										<div style="position: relative;">
											<div id="maxSearchUserNumView">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td class="labelT1" width="180px"><s:text
														name="home.hmSettings.maximum.concurrent.search.usernum.title" /></td>
													<td class="showValue"><s:property
														value="concurrentSearchUserNum" /></td>
												</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td height="0px">
													<div id="maxSearchUserNumSpace"></div>
													</td>
												</tr>
											</table>
											</div>
											<div id="maxSearchUserNumOptions" class="options">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1" width="200px"><label> <s:text
														name="home.hmSettings.maximum.concurrent.search.usernum.title" /> </label></td>
													<td><s:textfield id="concurrentSearchUserNum"
														name="concurrentSearchUserNum"
														onkeypress="return hm.util.keyPressPermit(event,'ten');"
														maxlength="3" cssStyle="width: 95px;" />
														<s:text name="home.hmSettings.maximum.concurrent.search.usernum.range"/></td>
												</tr>
											</table>
											</div>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
                <!-- ID Manager -->
                <tr style="display:<s:property value="%{hide4StandaloneVHM}"/>">
                    <td style="padding: 10px 10px 0px 15px;">
                    <fieldset style="padding: 0px;">
                    <div>
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
                        <tr>
                            <td>
                                <div class="settingsToolBar" id="cloudAuthServerSettingDiv"><span
                                class="settingCaption"><s:text
                                name="home.hmSettings.cloudAuthServer.setting" /> </span> <a href="#showOptions"
                                onclick="clickCloudAuthServerSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
                                name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
                            </div>
                            <div class="settingsToolBar" id="cloudAuthServerSaveDiv"
                                style="display: none;"><span class="settingCaption">Enable&#32;<s:text
                                name="home.hmSettings.cloudAuthServer.setting" /> </span> <a
                                href="#saveOptions" onclick="saveCloudAuthServerOption();"> <img
                                alt="Save" title="Save"
                                src="<s:url value="/images/save.png" includeParams="none"/>"
                                width="16" class="dinl"> </a> <a href="#exitOptions"
                                onclick="exitCloudAuthServerOption();"> <img alt="Cancel"
                                title="Cancel"
                                src="<s:url value="/images/cancel.png" includeParams="none"/>"
                                width="16" class="dinl"> </a>
                            </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0px 10px 0px 10px;" valign="top">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                <tr>
                                    <td>
                                        <div style="position: relative;">
                                            <div id="cloudAuthServerView">
                                            <table cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td height="10"></td>
                                                </tr>
                                                <tr>
                                                    <td class="labelT1" width="350px"><s:text
                                                        name="home.hmSettings.cloudAuthServer.enabledBeta" /></td>
                                                    <td class="showValue">
                                                    	<s:if test="enabledBetaIDM">Yes</s:if>
                                                    	<s:else>No</s:else>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td height="10"></td>
                                                </tr>
                                                <tr>
                                                    <td height="0px">
                                                    <div id="cloudAuthServerSpace"></div>
                                                    </td>
                                                </tr>
                                            </table>
                                            </div>
                                            <div id="cloudAuthServerOptions" class="options">
                                            <table cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td height="5"></td>
                                                </tr>
                                                <tr>
                                                    <td><s:checkbox name="enabledBetaIDM"/></td>
                                                    <td class="labelT1"><label for="hmSettings_enabledBetaIDM"> <s:text
                                                        name="home.hmSettings.cloudAuthServer.enabledBeta" /> </label></td>
                                                </tr>
                                                <tr>
                                                    <td/>
                                                    <td class="labelT1 noteInfo"><label> <s:text
                                                        name="home.hmSettings.cloudAuthServer.note" /> </label></td>
                                                </tr>
                                            </table>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>	
                <!-- ---------------------------           tca alarm------------------- -->
				<tr style="display:<s:property value="%{Hide4TCAAlarm}"/>">
					<td style="padding: 10px 10px 0px 15px;">
					<fieldset style="padding: 0px;">
					<div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr style="display:<s:property value="%{Hide4TCAAlarm}"/>">
							<td>
								<div class="settingsToolBar" id="tcaAlarmSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.tcaalarm.setting" /> </span> <a href="#showOptions"
								onclick="clickTCAAlarmSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="tcaAlarmSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.tcaalarm.edit" /> </span> <a
								href="#saveOptions" onclick="saveTCAAlarmOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exittcaAlarmOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
							<td style="padding: 0px 10px 0px 10px;" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
										<div style="position: relative;">
											<div id="tcaAlarmView">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="10"></td>
												</tr>
												 <tr>
                                                 <td><table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<th><s:text name="home.hmSettings.tca.measureitem" /></th>
													<th><s:text name="home.hmSettings.tca.highthreshold" /></th>
													<th><s:text name="home.hmSettings.tca.lowthreshold" /></th>
													<th><s:text name="home.hmSettings.tca.interval" /></th>
												</tr>
												<s:iterator value="tcaAlarmList" status="status">
												<tr>
													<td class="list" style="color: #000080;">
													<s:property  value="meatureItem" />
													</td>
													<td class="list" style="color: #000080;">
													<s:property  value="highThresholdPercentStr" />
													</td>
													<td class="list" style="color: #000080;">
													<s:property value="lowThresholdPercentStr" />
													</td>
													<td class="list" style="color: #000080;">
													<s:property value="interval" />
													</td>
												</tr>
												</s:iterator>
											</table>
											</td>
											</tr>
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td height="0px">
													<div id="tcaAlarmSpace"></div>
													</td>
												</tr>
											</table>
											</div>
											
											<div id="tcaAlarmOptions" class="options">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td class="labelT1" width="120px"><label> <s:text
														name="home.hmSettings.tcaalarm.edit.selectItem.label" /> </label></td>
													<td><s:select id="tcaMeasureItem" name="selectedTcaMeasureItem"	value="%{selectedTcaMeasureItem}" list="%{tcaAlarmList}" listKey="id"
															listValue="meatureItem" headerKey="0" headerValue="" cssStyle="width: 200px;"  onchange="loadMeasureParameter()"/>
														</td>
												</tr>
												<tr>
													<td class="labelT1" width="120px"><label> <s:text
														name="home.hmSettings.tcaalarm.edit.highThreshold.label" /> </label></td>
													<td><s:textfield id="tcaHighThreshold"
														name="tcaHighThreshold" cssStyle="width: 200px;" />
														<s:text name="home.hmSettings.tcaalarm.edit.diskusage.highThreshold.range" />
														</td>
												</tr>
                                                <tr>
                                                    <td class="labelT1" width="120px"><label> <s:text
                                                        name="home.hmSettings.tcaalarm.edit.lowThreshold.label" /> </label></td>
                                                    <td><s:textfield id="tcaLowThreshold"
                                                        name="tcaLowThreshold" cssStyle="width: 200px;" />
                                                        <s:text name="home.hmSettings.tcaalarm.edit.diskusage.lowThreshold.range" />
                                                        </td>
                                                </tr>												
												<tr>
													<td class="labelT1" width="120px"><label> <s:text
														name="home.hmSettings.tcaalarm.edit.monitorInteval.label" /> </label></td>
													<td><s:textfield id="tcaInterval"
														name="tcaInterval" cssStyle="width: 200px;" />
														<s:text name="home.hmSettings.tcaalarm.edit.diskusage.interval.range" />
														</td>
												</tr>
												
											</table>
											</div>
										</div>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</div>
					</fieldset>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
				<!-- ---------------------------           tca alarm end------------------- -->	
				<!-- ---------------------------           custom device tag------------------- -->
				<tr style="display:<s:property value="%{Hide4CustomDeviceTag}"/>">
                    <td style="padding: 10px 10px 0px 15px;">
                    <fieldset style="padding: 0px;">
                    <div>
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td>
							<div class="settingsToolBar" id="customDeviceTagSettingDiv"><span
								class="settingCaption"><s:text
								name="home.hmSettings.customDeviceTag" /> </span> <a href="#showOptions"
								onclick="clickDeviceTagSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
								name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
							</div>
							<div class="settingsToolBar" id="customDeviceTagSaveDiv"
								style="display: none;"><span class="settingCaption"><s:text
								name="home.hmSettings.editCustomDeviceTag" /> </span> <a
								href="#saveOptions" onclick="saveDeviceTagOption();"> <img
								alt="Save" title="Save"
								src="<s:url value="/images/save.png" includeParams="none"/>"
								width="16" class="dinl"> </a> <a href="#exitOptions"
								onclick="exitDeviceTagOption();"> <img alt="Cancel"
								title="Cancel"
								src="<s:url value="/images/cancel.png" includeParams="none"/>"
								width="16" class="dinl"> </a>
							</div>
							</td>
						</tr>
						<tr>
                            <td style="padding: 0px 10px 0px 10px;" valign="top">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
								<tr>
									<td>
									<div style="position: relative;">
									<div id="customDeviceTagView">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text name="admin.customTag.Tag1" /></td>
											<td class="showValue"><s:text name="customTag1"/></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text name="admin.customTag.Tag2" /></td>
											<td class="showValue"><s:text name="customTag2"/></td>
										</tr>
										<tr>
											<td class="labelT1" width="180px"><s:text name="admin.customTag.Tag3" /></td>
											<td class="showValue"><s:text name="customTag3"/></td>
										</tr>
										<tr>
											<td height="10"></td>
										</tr>
										<tr>
											<td height="0px">
											<div id="customDeviceTagSpace"></div>
											</td>
										</tr>
									</table>								
																		
									</div>
									<div id="customDeviceTagOptions" class="options">
									<table cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td height="5"></td>
										</tr>
										
										<tr>
											<td class="labelT1" width="80" style="padding-left: 15px;">		
												<s:checkbox id="testCheckbox1"	name="ignore" value="false"	onclick="editCustomTag('customTag1')" />						
 											<label>
												<s:text name="admin.customTag.Tag1" />
											</label>
											</td>								
											<td id="showname1"  style="padding-top: 3px">
													<s:text name="customTag1"/> </td>							
											<td>									
												<s:textfield id="inputname1" name="customTag1"  style="display:none"	size="22" maxlength="16"  
													onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
											</td>	
											<td id="inputname11" style="display:none" >&nbsp;<s:text name="admin.management.updateWebsenseServer.accountID.range" /></td>
										</tr>	
										
										<tr>
											<td class="labelT1" width="80" style="padding-left: 15px;">		
												<s:checkbox id="testCheckbox2"	name="ignore" value="false"	onclick="editCustomTag('customTag2')" />						
 											<label>
												<s:text name="admin.customTag.Tag2" />
											</label>
											</td>								
											<td id="showname2" style="padding-top: 3px" >
													<s:text name="customTag2"/> </td>							
											<td>									
												<s:textfield id="inputname2" name="customTag2"  style="display:none"	size="22" maxlength="16"  
													onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
											</td>	
											<td id="inputname22" style="display:none" >&nbsp;<s:text name="admin.management.updateWebsenseServer.accountID.range" /></td>
										</tr>	
										
										<tr>
											<td class="labelT1" width="80" style="padding-left: 15px;">		
												<s:checkbox id="testCheckbox3"	name="ignore" value="false"	onclick="editCustomTag('customTag3')" />						
 											<label>
												<s:text name="admin.customTag.Tag3" />
											</label>
											</td>								
											<td id="showname3" style="padding-top: 3px">
													<s:text name="customTag3"/> </td>							
											<td>									
												<s:textfield id="inputname3" name="customTag3"  style="display:none"	size="22" maxlength="16"  
													onkeypress="return hm.util.keyPressPermit(event,'name');" /> 
											</td>	
											<td id="inputname33" style="display:none" >&nbsp;<s:text name="admin.management.updateWebsenseServer.accountID.range" /></td>
										</tr>											
																			
									</table>
									</div>
									</div>
									</td>
								</tr>
							</table>
                            </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>  
                <!-- -------------------------    client profile    ----------------------->
				<tr style="display:none">
                    <td style="padding: 10px 10px 0px 15px;">
                    <fieldset style="padding: 0px;">
                    <div>
                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
                        <tr>
                            <td>
                                <div class="settingsToolBar" id="clientProfileSettingDiv"><span
                                class="settingCaption"><s:text
                                name="home.hmSettings.clientProfile.setting" /> </span> <a href="#showOptions"
                                onclick="clickClientProfileSetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
                                name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
                            </div>
                            <div class="settingsToolBar" id="clientProfileSaveDiv"
                                style="display: none;"><span class="settingCaption">Update&#32;<s:text
                                name="home.hmSettings.clientProfile.setting" /> </span> <a
                                href="#saveOptions" onclick="saveClientProfileOption();"> <img
                                alt="Save" title="Save"
                                src="<s:url value="/images/save.png" includeParams="none"/>"
                                width="16" class="dinl"> </a> <a href="#exitOptions"
                                onclick="exitClientProfileOption();"> <img alt="Cancel"
                                title="Cancel"
                                src="<s:url value="/images/cancel.png" includeParams="none"/>"
                                width="16" class="dinl"> </a>
                            </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0px 10px 0px 10px;" valign="top">
                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
                                <tr>
                                    <td>
                                        <div style="position: relative;">
                                            <div id="clientProfileView">
                                            <table cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td height="10"></td>
                                                </tr>
                                                <tr>
                                                    <td class="labelT1" width="180px"><s:text
                                                        name="home.hmSettings.clientProfile.enabledClientProfile" /></td>
                                                    <td class="showValue"><s:property
                                                        value="enabledClientProfileShow" /></td>
                                                </tr>
                                                <tr>
                                                    <td height="10"></td>
                                                </tr>
                                                <tr>
                                                    <td height="0px">
                                                    <div id="clientProfileSpace"></div>
                                                    </td>
                                                </tr>
                                            </table>
                                            </div>
                                            <div id="clientProfileOptions" class="options">
                                            <table cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td height="5"></td>
                                                </tr>
                                                <tr>
                                                    <td><s:checkbox name="enabledClientProfile"/></td>
                                                    <td class="labelT1"><label for="hmSettings_enabledClientProfile"> <s:text
                                                        name="home.hmSettings.clientProfile.enabledClientProfile" /> </label></td>
                                                </tr>
                                              <!--   <tr>
                                                    
                                                    <td class="labelT1 noteInfo"><label> <s:text
                                                        name="home.hmSettings.clientProfile.note" /> </label></td>
                                                </tr>--> 
                                            </table>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>              
				<tr>
					<td height="10"></td>
				</tr>
				<s:if test="%{!HMOnline}">
				  <tr>
					<td style="padding: 10px 10px 0px 15px;">
						<fieldset style="padding: 0px;">
							<div>
								<table cellspacing="0" cellpadding="0" border="0" width="100%">
								 <tr>
									<td>
									<div class="settingsToolBar" id="APISettingDiv">
										<span class="settingCaption"><s:text
												name="home.hmSettings.api" /> </span> <a href="#showOptions"
											onclick="clickAPISetting();"><s:text
												name="home.hmSettings.settings.label" /><span>&#9660;</span>
										</a>
									</div>
									<div class="settingsToolBar" id="apiSaveDiv"
										style="display: none;">
										<span class="settingCaption"><s:text
												name="home.hmSettings.editAPI" /> </span> <a
											href="#saveOptions" onclick="saveAPIOption();"> <img
											alt="Save" title="Save"
											src="<s:url value="/images/save.png" includeParams="none"/>"
											width="16" class="dinl">
										</a> <a href="#exitOptions" onclick="exitAPIOption();"> <img
											alt="Cancel" title="Cancel"
											src="<s:url value="/images/cancel.png" includeParams="none"/>"
											width="16" class="dinl">
										</a>
									</div>
									</td>
								</tr>
								<tr>
									<td style="padding: 0px 10px 0px 10px;" valign="top">
									<table cellspacing="0" cellpadding="0" border="0"
											width="100%">
									  <tr>
									  <td>
										<div style="position: relative;">
										  <div id="apiView">
											<table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="10"></td>
												</tr>
												<tr>
													<td class="labelT1" width="180px"><s:text name="mgmtSettings.APIsetting.APIAccess.label" /> </td>
													<td class="showValue"><s:property value="%{enableApiAccess}"/> </td>
												</tr>
												<tr>
													<td class="labelT1" width="180px"><s:text name="home.hmSettings.api.userName" /> </td>
													<td class="showValue"><s:property value="%{apiUserName}"/> </td>
												</tr>
												<tr >
													<td height="60"></td>
												</tr>
												<tr>
													<td height="0px">
														<div id="apiSpace"></div>
													</td>
												</tr>
											 </table>
											</div>
											<div id="apiOptions" class="options">
											  <table cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													<td colspan="2" class="labelT1"><s:text
															name="home.hmSettings.api.description"/></td>
												</tr>
												<tr>
													<td colspan="2" class="labelT2"><s:checkbox
															id="enableApiAccess" name="enableApiAccess" onclick="changeApiUserInfoStyle(this.checked)"/> <label>
															<s:text name="mgmtSettings.APIsetting.APIAccess.checkbox"/>
													</label></td>
												 </tr>
												 <tr>
												  <td colspan="2" class="labelT2" style="display:<s:property value="%{enableApiStyle}"/>" id="apiUserInfo">
												    <table cellspacing="0" cellpadding="0" border="0">
												      <tr>
													    <td class="labelT1" width="120px"><s:text
															name="home.hmSettings.api.userName" /></td>
													    <td><s:textfield id="apiUserName" onkeypress="return hm.util.keyPressPermit(event,'name');"
															name="apiUserName" size="24" /></td>
												       </tr>
												       <tr>
													      <td colspan="2" class="labelT1"><s:checkbox
															id="changePassword" name="changePassword"
															onclick="changeUserPassword(this.checked);" /> <label>
															<s:text name="admin.user.changePassword" />
													     </label></td>
												       </tr>
												  <tr>
													<td colspan="2" id="passwordSection" style="display: none">
													  <table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labelT1" width="120px"><label> <s:text
																		name="home.hmSettings.api.password" /> <font
																	color="red"><s:text name="*" /> </font>
															</label></td>
															<td><s:password id="apiPassword" onkeypress="return hm.util.keyPressPermit(event,'password');"
																	name="apiPassword" size="24" maxlength="32" />
																<s:textfield id="apiPassword_text" onkeypress="return hm.util.keyPressPermit(event,'password');"
																	name="apiPassword" disabled="true" size="24"
																	maxlength="32" cssStyle="display:none" /> <s:text
																	name="admin.user.password.ranger" /></td>
														</tr>
														<tr>
														 <td class="labelT1" width="120px"><label> <s:text
																		name="admin.user.password.confirm" /> <font
																	color="red"><s:text name="*" /> </font>
															</label></td>
															<td><s:password id="passwordConfirm" onkeypress="return hm.util.keyPressPermit(event,'password');"
																	name="passwordConfirm" size="24" maxlength="32" />
																<s:textfield id="passwordConfirm_text" onkeypress="return hm.util.keyPressPermit(event,'password');"
																	name="passwordConfirm" disabled="true"
																	size="24" maxlength="32"
																	cssStyle="display:none" /> <s:text
																	name="admin.user.password.ranger" /></td>
														</tr>
														<tr>
														 <td>&nbsp;</td>
														  <td>
															<table border="0" cellspacing="0" cellpadding="0">
															 <tr>
																<td><s:checkbox id="chkAPIToggleDisplay"
																		name="ignore" value="true"
																		onclick="hm.util.toggleObscurePassword(this.checked,['apiPassword','passwordConfirm'],['apiPassword_text','passwordConfirm_text']);" />
																</td>
																<td><s:text name="admin.user.obscurePassword" /></td>
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
											   </table>
											  </div>
											 </div>
											</td>
										 </tr>
									 </table>
									</td>
								</tr>
							</table>
							</div>
						</fieldset>
					</td>
				</tr>
			 </s:if>
			 <s:if test="%{!easyMode}">
			 <tr>
                  <td style="padding: 10px 10px 0px 15px;">
                   <fieldset style="padding: 0px;">
                   <div>
                   <table cellspacing="0" cellpadding="0" border="0" width="100%">
                       <tr>
                           <td>
	                           <div class="settingsToolBar" id="supplementalCLISettingDiv"><span
	                               class="settingCaption"><s:text
	                               name="hollywood_02.supp_cli_vhm_enable" /> </span> <a href="#showOptions"
	                               onclick="clickSupplementalCLISetting();" style="display:<s:property value="%{hideIfNoPermission}"/>"><s:text
	                               name="home.hmSettings.settings.label" /><span>&#9660;</span> </a>
	                           </div>
	                           <div class="settingsToolBar" id="supplementalCLISaveDiv"
	                               style="display: none;"><span class="settingCaption">Enable&#32;<s:text
	                               name="hollywood_02.supp_cli_vhm_status" /> </span> <a
	                               href="#saveOptions" onclick="saveSupplementalCLIOption();"> <img
	                               alt="Save" title="Save"
	                               src="<s:url value="/images/save.png" includeParams="none"/>"
	                               width="16" class="dinl"> </a> <a href="#exitOptions"
	                               onclick="exitSupplementalCLIOption();"> <img alt="Cancel"
	                               title="Cancel"
	                               src="<s:url value="/images/cancel.png" includeParams="none"/>"
	                               width="16" class="dinl"> </a>
	                           </div>
                           </td>
                       </tr>
                       <tr>
                           <td style="padding: 0px 10px 0px 10px;" valign="top">
                           <table cellspacing="0" cellpadding="0" border="0" width="100%">
                               <tr>
                                   <td>
                                       <div style="position: relative;">
                                           <div id="supplementalCLIView">
                                           <table cellspacing="0" cellpadding="0" border="0">
                                               <tr>
                                                   <td height="10"></td>
                                               </tr>
                                               <tr>
                                                   <td class="labelT1" width="350px"><s:text
                                                       name="hollywood_02.supp_cli_vhm_enable" /></td>
                                                   <td class="showValue">
                                                   	<s:if test="enableSupplementalCLI">Yes</s:if>
                                                   	<s:else>No</s:else>
                                                   </td>
                                               </tr>
                                               <tr>
                                                   <td height="10"></td>
                                               </tr>
                                               <tr>
                                                   <td height="0px">
                                                   <div id="supplementalCLISpace"></div>
                                                   </td>
                                               </tr>
                                           </table>
                                           </div>
                                           <div id="supplementalCLIOptions" class="options">
                                           <table cellspacing="0" cellpadding="0" border="0">
                                               <tr>
                                                   <td height="5"></td>
                                               </tr>
                                               <tr>
                                                   <td><s:checkbox name="enableSupplementalCLI"/></td>
                                                   <td class="labelT1"><label for="hmSettings_enableSupplementalCLI"> <s:text
                                                       name="hollywood_02.supp_cli_vhm_enable" /> </label></td>
                                               </tr>
                                               <tr>
                                                   <td/>
                                                   <td class="labelT1 noteInfo"><label> <s:text
                                                       name="hollywood_02.supp_cli_vhm.note" /> </label></td>
                                               </tr>
                                           </table>
                                           </div>
                                       </div>
                                   </td>
                               </tr>
                           </table>
                           </td>
                        </tr>
                    </table>
                    </div>
                    </fieldset>
                    </td>
                </tr>
               </s:if>	
			</table>
			</td>
		</tr>
	</table>
</s:form></div>
