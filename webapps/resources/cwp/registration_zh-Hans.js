function fillData(){
var txt =[["安全的互联网门户"],
["注册"],
["<strong>注册以访问该网络。</strong>"],
["注册需要一段时间完成（*为必填项）。"],
["登录"],
[""],/*#$FirstName-Resource#$*/
[""],/*#$LastName-Resource#$*/
[""],/*#$Email-Resource#$*/
[""],/*#$Phone-Resource#$*/
[""],/*#$Visiting-Resource#$*/
[""],/*#$Comment-Resource#$*/
["注册"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.getElementById("p1TitleAccess").innerHTML=txt[2];
document.getElementById("momentNote").innerHTML=txt[3];
document.title=txt[4];
document.getElementById("register_button_id").innerHTML=txt[11];
var label1=document.getElementById("fieldFirstNameMark");
var label2=document.getElementById("fieldLastNameMark");
var label3=document.getElementById("fieldEmailMark");
var label4=document.getElementById("opt_fieldPhoneMark");
var label5=document.getElementById("fieldVisitingMark");
var label6=document.getElementById("opt_fieldCommentMark");
if(label1!=null){
	fillPlaceHolder("fieldFirstNameMark",txt[5]);
}
if(label2!=null){
	fillPlaceHolder("fieldLastNameMark",txt[6]);
}
if(label3!=null){
	fillPlaceHolder("fieldEmailMark",txt[7]);
}
if(label4!=null){
	fillPlaceHolder("opt_fieldPhoneMark",txt[8]);
}
if(label5!=null){
	fillPlaceHolder("fieldVisitingMark",txt[9]);
}
if(label6!=null){
	fillPlaceHolder("opt_fieldCommentMark",txt[10]);
}
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}