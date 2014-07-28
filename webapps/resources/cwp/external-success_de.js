function fillData(){
var txt =[["Registrierung erfolgreich!"],
["Sie werden automatisch in <pause-time> zu <A href='transfer-url'>transfer-url</A> weitergeleitet."],
["Weiterleitung mit JaveScript"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}