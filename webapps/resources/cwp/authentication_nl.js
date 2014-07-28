function fillData(){
var txt =[["Beveiligde Internet Toegang"],
["Bestaande Gebruiker"],
["<strong>Log in voor beveiligde Internet toegang:</strong>"],
["Als u inlogt, verklaart u kennis te hebben genomen van de  <a href='reg.php?ah_goal=use-policy.html'>algemene voorwaarden</a>."],
["Gebruikersnaam"],
["Wachtwoord"],
["Login"],
["Inloggen"],
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