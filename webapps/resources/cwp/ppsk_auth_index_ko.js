function fillData(){
var txt =[["Secure Internet Portal"],
["<strong>유효한 인증서를 갖고 있으면, PPSK 키를 받기 위해 아래 정보를 입력해 주세요</strong> "],
["사용자"],
["패스워드"],
["로그인"],
["로그인"],
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