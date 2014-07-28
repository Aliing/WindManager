function fillData(){
var txt =[["Secure Internet Portal"],
["Acceptable Use Policy"],
["Login"],
[""],/*#$UserPolicy-Resource#$*/
["Accept"],
["Cancel"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

