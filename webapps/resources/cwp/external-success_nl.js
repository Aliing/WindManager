function fillData(){
var txt =[["Registratie Gelukt!"],
["U wordt automatisch doorgezonen naar <A href='transfer-url'>transfer-url</A> in <pause-time> seconden。"],
["Redirect pagina met JavaScript"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}