function fillData(){
var txt =[["安全的互联网门户"],
["现有用户"],
["登录安全的互联网访问："],
["登录表明您已经阅读并接受 <a href='reg.php?ah_goal=use-policy.html'>使用条款</a>."],
["新用户"],
["<strong>注册以访问该网络。</strong>"],
["用户名"],
["密码"],
["注册需要一段时间完成（*为必填项）。"],
["登录"],
["登录"],
[""],/*#$FirstName-Resource#$*/
[""],/*#$LastName-Resource#$*/
[""],/*#$Email-Resource#$*/
[""],/*#$Phone-Resource#$*/
[""],/*#$Visiting-Resource#$*/
[""],/*#$Comment-Resource#$*/
[""],/*#$Representing-Resource#$*/
["注册"],
["新用户？要求进入"]
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.getElementById("loginNote").innerHTML=txt[2];
document.getElementById("usePolicyNote").innerHTML=txt[3];
document.getElementById("newUserId").innerHTML=txt[4];
document.getElementById("regNote").innerHTML=txt[5];
fillPlaceHolder("field12",txt[6]);
fillPlaceHolder("field22",txt[7]);
document.getElementById("momentNote").innerHTML=txt[8];
document.title=txt[9];
document.getElementById("login_button_id").innerHTML=txt[10];
document.getElementById("register_button_id").innerHTML=txt[18];
document.getElementById("newUserRequastAccess").innerHTML=txt[19];
var label1=document.getElementById("fieldFirstNameMark");
var label2=document.getElementById("fieldLastNameMark");
var label3=document.getElementById("fieldEmailMark");
var label4=document.getElementById("opt_fieldPhoneMark");
var label5=document.getElementById("fieldVisitingMark");
var label5Idm=document.getElementById("opt_fieldVisitingMark");
var label6=document.getElementById("opt_fieldCommentMark");
var label7=document.getElementById("opt_fieldRepresentingMark");
if(label1!=null){
	fillPlaceHolder("fieldFirstNameMark",txt[11]);
}
if(label2!=null){
	fillPlaceHolder("fieldLastNameMark",txt[12]);
}
if(label3!=null){
	fillPlaceHolder("fieldEmailMark",txt[13]);
}
if(label4!=null){
	fillPlaceHolder("opt_fieldPhoneMark",txt[14]);
}
if(label5!=null){
	fillPlaceHolder("fieldVisitingMark",txt[15]);
}
if(label5Idm != null){
	fillPlaceHolder("opt_fieldVisitingMark",txt[15]);
}
if(label6!=null){
	fillPlaceHolder("opt_fieldCommentMark",txt[16]);
}
if(label7!=null){
	fillPlaceHolder("opt_fieldRepresentingMark",txt[17]);
}
}
/*#$CountryCode-Resource#$*/
function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
	}