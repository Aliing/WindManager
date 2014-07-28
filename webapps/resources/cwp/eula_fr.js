function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["Politique d’utilisation"],
["Se connecter"],
[""],/*#$UserPolicy-Resource#$*/
["Accepter"],
["Annuler"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

