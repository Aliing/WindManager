function fillData(){
var txt =[["Beveiligde Internet toegang"],
["Gebruikersovereenkomst"],
["Inloggen"],
[""],/*#$UserPolicy-Resource#$*/
["Accepteer"],
["Annuleer"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

