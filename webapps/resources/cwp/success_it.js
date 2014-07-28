function fillData(){
var txt =[["Portale di accessoInternet sicuro"],
["Accessoeseguito"],
["<strong>Grazie per esserti registrato.</strong>"],
["<strong>Si prega di utilizzare il seguente privato Pre-Shared Key per accedere al SSID sicuro: <span id='ppsk_ssid'></span></strong>"],
["Accessoeseguito"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["Nome  Utente"],
["Password"],
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