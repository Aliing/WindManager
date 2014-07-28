function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["Conditions d’utilisation"],
["&lt; Retour à la page de Connexion"],
["Connexion – Politique d’Utilisation"],
[""],/*#$UserPolicy-Resource#$*/
];  


$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}