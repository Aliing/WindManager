function fillData(){
var txt =[["安全的互联网门户"],
["登录成功"],
["<strong>感谢您的注册。</strong>"],
["<strong>请使用下面的私有预设密钥来访问安全的SSID： <span id='ppsk_ssid'></span></strong>"],
["登录成功"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["用户名"],
["密码"],
["注册成功"],
["请下次访问时记住您的信息。"],
["您的账号还没有激活，需要您所填写的员工批准。我们将会发送给员工一封邮件来批准请求。一旦该员工批准，您就可以使用了。"]
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