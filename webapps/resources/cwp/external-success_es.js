function fillData(){
var txt =[["Registro correcto!"],
["Usted será automáticamente redirigido a <A href='transfer-url'>transfer-url</A> en pause-time segundos."],
["Página redirigida con JavaScript"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}