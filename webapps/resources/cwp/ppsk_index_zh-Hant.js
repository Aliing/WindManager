function fillData(){
var txt =[["安全的網際網路入口"],
["<strong>新用戶應該填寫下表以取得用以登陸無線網路的私人預設共用金鑰(PPSK)。</strong>"],
["名*"],
["姓*"],
["電子郵件*"],
["聯繫電話"],
["訪問*"],
["備註"],
["註冊需要一段時間完成（*為必填項）。"],
["登入"],
["註冊"],
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