function fillData(){
var txt =[["Portail d’Accès Internet Sécurisé"],
["Échec de connexion"],
["Login Unsuccessful"],
[""],/*#$LibSipFailFines-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#sipDeny").html(txt[3]);
document.title=txt[2];
}