function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["Utilisateur existant"],
["<strong>Identifiez-vous pour accéder à Internet:</strong>"],
["En vous connectant, vous reconnaissez avoir lu et accepté la  <a href='reg.php?ah_goal=use-policy.html'>politique d’utilisation</a>."],
["Nom d’utilisateur"],
["Mot de passe"],
["Connexion"],
["Se connecter"],
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