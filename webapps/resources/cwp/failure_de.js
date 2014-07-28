function fillData(){
var txt =[["Sicheres Internet Portal"],
["Login fehlgeschlagen."],
["Login nicht erfolgreich"],
[""],/*#$LibSipFailFines-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#sipDeny").html(txt[3]);
document.title=txt[2];
}