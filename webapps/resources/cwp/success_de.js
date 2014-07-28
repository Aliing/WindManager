function fillData(){
var txt =[["Sicheres Internet Portal"],
["Login erfolgreich"],
["<strong>Vielen Dank für Ihre Registrierung.</strong>"],
["<strong>Bitte benutzen Sie folgenden Private Pre-Shared-Key zum sicheren Zugriff auf die SSID:  <span id='ppsk_ssid'></span></strong>"],
["Login erfolgreich"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["Benutzername"],
["Passwort"],
[""],
[""],
[""]
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#thanksNote").html(txt[2]);
$("#ssidNote").html(txt[3]);
document.title=txt[4];
$("#notice").html(txt[5]);
$("#ok").html(txt[6]);
$("#fines").html(txt[7]);
$("#idm_yunp").html(txt[8]);
$("#idm_ypwp").html(txt[9]);
$("#idm_register").html(txt[10]);
$("#idm_reminder").html(txt[11]);
$("#idm_reminder2").html(txt[12]);
}