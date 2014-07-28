function fillData(){
var txt =[["Portale di accesso ad Internet sicuro"],
["Politicadi utilizzo"],
["&lt; Torna alla Login"],
["Login - Uso Politica"],
[""],/*#$UserPolicy-Resource#$*/
];  


$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}