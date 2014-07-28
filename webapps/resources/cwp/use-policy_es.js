function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["Política de uso"],
["&lt; Volver a la página de login"],
["Loguearse – Política de Uso"],
[""],/*#$UserPolicy-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}