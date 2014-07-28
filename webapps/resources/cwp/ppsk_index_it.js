function fillData(){
var txt =[["Portale di accesso Internet sicuro"],
["<strong>I nuovi utenti devono compilare il modulo sottostante per richiedere una chiave già condivisa privata per l'accesso alla rete wireless sicura.</strong>"],
["Nome*"],
["Cognome*"],
["Email*"],
["Telefono"],
["Referente*"],
["Commento"],
["Il dipartimento IT potrebbeimpiegarequalcheminuto per completare la registrazione (* richiesto)"],
["Accesso"],
["Registrazione"],
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