function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["Login correcto"],
["<strong>Gracias por registrarse.</strong>"],
["<strong>Por favor, use la siguiente Private PSK para acceder al SSID de manera segura: <span id='ppsk_ssid'></span></strong>"],
["Login correcto"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["Usuario"],
["Contraseña"],
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