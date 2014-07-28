function fillData(){
var txt =[["安全的網際網路入口"],
["<strong>如果您有有效的網路使用認證，請提認證以便收到您的私人預設共用金鑰(PPSK)以安全使用網路。</strong> "],
["用戶名稱"],
["密碼"],
["登入"],
["登入"],
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