function fillData(){
var txt =[["Secure Internet Portal"],
["기존 사용자"],
["<strong>보안을 위해 로그인을 해주세요:</strong>"],
["로그인을 하는 경우 이용약관을 읽었으며 그에 동의하였음을 의  <a href='reg.php?ah_goal=use-policy.html'>미합니다</a>."],
["아이디"],
["비밀번호"],
["로그인"],
["로그인"],
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