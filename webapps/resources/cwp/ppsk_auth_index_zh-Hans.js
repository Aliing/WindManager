function fillData(){
var txt =[["安全的互联网门户"],
["<strong>如果您有有效的网络证书，请提交证书以便收到您的用于安全的网络访问的私人预共享密钥。</strong>"],
["用户名"],
["密码"],
["登录"],
["登录"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("submitKeyNote").innerHTML=txt[1];
fillPlaceHolder("field12",txt[2]);
fillPlaceHolder("field22",txt[3]);
document.title=txt[4];
document.getElementById("loginbutton").innerHTML=txt[5];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}