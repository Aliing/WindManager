function fillData(){
var txt =[["Portale Internet sicuro"],
["Politica di utilizzo accettabile"],
["Accesso"],
[""],/*#$UserPolicy-Resource#$*/
["Accettare"],
["Annullare"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

