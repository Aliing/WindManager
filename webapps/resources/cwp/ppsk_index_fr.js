function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["<strong>Tout nouvel utilisateur doit remplir le formulaire ci-dessous afin d’obtenir une clé privée partagée (PPSK) pour accéder de manière sécurisée au réseau.</strong>"],
["Prénom*"],
["Nom*"],
["E-mail*"],
["Téléphone"],
["Personne visitée*"],
["Commentaires"],
["L’enregistrement peut prendre quelques secondes (* champ obligatoire)."],
["Se connecter"],
["S’enregistrer"],
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