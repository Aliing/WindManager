function fillData(){
var txt =[["Secure Internet Portal"],
["Existing Users"],
["<strong>Log in for secure Internet access:</strong>"],
["Logging in indicates you have read and accepted the <a href='reg.php?ah_goal=use-policy.html'>Use Policy</a>."],
["Username"],
["Password"],
["Login"],
["Log In"],
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