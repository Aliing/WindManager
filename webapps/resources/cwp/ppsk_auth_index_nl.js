function fillData(){
var txt =[["Beveiligde Internet toegang"],
["<strong>Indien u over geldige inloggegevens beschikt, vult u deze dan hieronder in om uw eigen netwerksleutel te krijgen voor een beveiligde internet verbinding.</strong> "],
["Gebruikersnaam"],
["Wachtwoord"],
["Inloggen"],
["Inloggen"],
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