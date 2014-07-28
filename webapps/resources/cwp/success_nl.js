function fillData(){
var txt =[["Beveiligde Internet toegang"],
["Inloggen gelukt"],
["<strong>Hartelijk dank registreren.</strong>"],
["<strong>Gebruik de volgende netwerksleutel om toegang te verkijgen tot de beveiligde SSID: <span id='ppsk_ssid'></span></strong>"],
["Inloggen gelukt"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["Gebruikersnaam"],
["Wachtwoord"],
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