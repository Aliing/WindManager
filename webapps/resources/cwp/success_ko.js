function fillData(){
var txt =[["Secure Internet Portal"],
["로그인을 성공하였습니다."],
["<strong>등록해 주셔서 감사합니다。</strong>"],
["<strong>Please use the following Private Pre-Shared Key to access the secure SSID: <span id='ppsk_ssid'></span></strong>"],
["로그인을 성공하였습니다."],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["아이디"],
["비밀번호"],
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