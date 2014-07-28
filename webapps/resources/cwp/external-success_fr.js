function fillData(){
var txt =[["Enregistrement réussi !"],
["Vous allez être automatiquement transféré vers <A href='transfer-url'>transfer-url</A> dans pause-time secondes."],
["Page de redirection avec JavaScript"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}