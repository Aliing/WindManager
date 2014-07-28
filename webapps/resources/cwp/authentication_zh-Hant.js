function fillData(){
var txt =[["安全的網際網路入口"],
["線上用戶(使用者)"],
["<strong>登錄安全的網際網路：</strong>"],
["登入時表示您已經閱讀並接受 <a href='reg.php?ah_goal=use-policy.html'>使用條款</a>。"],
["使用者名稱"],
["密碼"],
["登入"],
["登入"],
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