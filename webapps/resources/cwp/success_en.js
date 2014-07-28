function fillData(){
var txt =[["Secure Internet Portal"],
["Login Successful"],
["<strong>Thank you for registering.</strong>"],
["<strong>Please use the following Private Pre-Shared Key to access the secure SSID: <span id='ppsk_ssid'></span></strong>"],
["Login Successful"],
[""],/*#$Notice-Resource#$*/
[""],/*#$LibSipStatus-Resource#$*/
[""],/*#$LibSipSuccessFines-Resource#$*/
["Username"],
["Password"],
["Registration Successful"],
["Registration was successful. Your user name and password have been sent to you by email/SMS.<br><br>Note: If your password does not arrive within a few minutes, register again and a new password will be generated and sent to you. Should you eventually receive multiple passwords, use the latest one."],
["YOUR ACCOUNT IS NOT YET ACTIVE. Your guest account requires the approval of the employee you are visiting. We will notify you when approval has been granted and your account has been activated."]
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