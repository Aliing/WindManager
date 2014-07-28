function fillData(){
var txt =[["安全的互联网门户"],
["<strong>新用户应填写下面的表格，请求私人预共享密钥以访问安全的无线网络。</strong>"],
["名字*"],
["姓*"],
["电子邮件*"],
["联系电话"],
["访问*"],
["注释"],
["注册需要一段时间完成（*为必填项）。"],
["登录"],
["注册"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
fillPlaceHolder("field1",txt[2]);
fillPlaceHolder("field2",txt[3]);
fillPlaceHolder("field3",txt[4]);
fillPlaceHolder("opt_field1",txt[5]);
fillPlaceHolder("field4",txt[6]);
fillPlaceHolder("opt_field2",txt[7]);
document.getElementById("momentNote").innerHTML=txt[8];
document.title=txt[9];
document.getElementById("register_button_id").innerHTML=txt[10];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}