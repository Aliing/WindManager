function fillData(){
var txt =[["Sicheres Internet Portal"],
["Registrierte Benutzer"],
["<strong>Log in fürsicheren Internet Zugang:</strong>"],
["Mitdem Login wirdbestätigt, die Richtlinienfür die Internet  <a href='reg.php?ah_goal=use-policy.html'>Benutzunggelesen und akzeptiertzuhaben</a>."],
["Benutzername"],
["Passwort"],
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