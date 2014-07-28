function fillData(){
var txt =[["Sicheres Internet Portal"],
["<strong>Neue Benutzer müssen das folgende Formular ausfüllen um einen Private Pre-Shared Key für den Netzwerkzugriff zu beantragen.</strong>"],
["Vorname*"],
["Nachname*"],
["Email*"],
["Telefon"],
["Besuchte Person*"],
["Kommentar"],
["Die Registrierung kann einen Moment dauern (*erforderlich)"],
["Login"],
["Registrierung"],
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