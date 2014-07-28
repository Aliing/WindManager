function fillData(){
var txt =[["Portale di accesso Internet sicuro"],
["Utentegiàesistente"],
["<strong>Accessosicuro ad Internet:</strong>"],
["L'accesso indica di aver letto e accettato il <a uso href='reg.php?ah_goal=use-policy.html'> privacy </ a>."],
["Nome Utente"],
["Password"],
["Accesso"],
["Accesso"],
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