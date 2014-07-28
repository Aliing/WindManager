function fillData(){
var txt =[["Portale di accesso ad Internet sicuro"],
["<strong>Se si dispone di credenziali di accesso valide, occorreinserirle qui sotto in modo da ricevereunachiaveprivata personale</strong> "],
["Nome Utente"],
["Password"],
["Accesso"],
["Accesso"],
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