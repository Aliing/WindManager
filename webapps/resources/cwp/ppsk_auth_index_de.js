function fillData(){
var txt =[["Sicheres Internet Portal"],
["<strong>Verfügen Sie über gültige Zugangsdaten, geben Sie sie bitte ein, um einen individuellen Pre-Shared Key für sicheren Internet Zugang zu erhalten.</strong> "],
["Benutzername"],
["Passwort"],
["Login"],
["Log In"],
];  

$("#h1Title").html(txt[0]);
$("#submitKeyNote").html(txt[1]);
fillPlaceHolder("field12",txt[2]);
fillPlaceHolder("field22",txt[3]);
document.title=txt[4];
$("#loginbutton").html(txt[5]);
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}