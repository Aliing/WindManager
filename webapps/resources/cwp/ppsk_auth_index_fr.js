function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["<strong>Si vous disposez d’un compte valide, veuillez-vous identifier ci-dessous afin de recevoir votre clé privée partagée pour accéder au réseau.</strong> "],
["Nom d’utilisateur"],
["Mot de passe"],
["Se connecter"],
["Se connecter"],
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