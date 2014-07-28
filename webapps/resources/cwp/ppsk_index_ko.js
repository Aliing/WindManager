function fillData(){
var txt =[["Secure Internet Portal"],
["<strong>새 사용자는 PPSK 를 발급받기위해 아래 정보를 입력해 주십시요.</strong>"],
["이름*"],
["성*"],
["Email*"],
["전화번호"],
["방문내역*"],
["추가사항"],
["등록이 완료되는데 까지 약간의 시간이 소요될 수 있습니다."],
["로그인"],
["등록하기"],
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