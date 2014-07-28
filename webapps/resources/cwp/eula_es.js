function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["Política de uso acceptable"],
["Loguearse"],
[""],/*#$UserPolicy-Resource#$*/
["Aceptar"],
["Cancelar"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

