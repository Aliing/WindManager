function fillData(){
var txt =[["安全的網際網路入口"],
["登錄成功"],
["<strong>感謝您的註冊。</strong>"],
["<strong>請使用下面的私人預設共用金鑰(PPSK)來訪問安全的SSID： <span id='ppsk_ssid'></span></strong>"],
["登錄成功"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["用户名"],
["密码"],
["註冊成功"],
["请下次访问时记住您的信息。"],
["您的賬號還沒有激活，需要您所填寫的員工批准。我們將會發送給員工一封郵件來批准請求。一旦該員工批准，您就可以使用了。"]
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