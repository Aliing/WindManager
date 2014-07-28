function fillData(){
var txt =[["安全的互联网门户"],
["现有用户"],
["<strong>登录安全的互联网访问：</strong>"],
["登录表明您已经阅读并接受 <a href='reg.php?ah_goal=use-policy.html'>使用条款</a>。"],
["用户名"],
["密码"],
["登录"],
["登录"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.getElementById("loginNote").innerHTML=txt[2];
document.getElementById("usePolicyNote").innerHTML=txt[3];
fillPlaceHolder("field12",txt[4]);
fillPlaceHolder("field22",txt[5]);
document.title=txt[6];
document.getElementById("loginbutton").innerHTML=txt[7];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
	}