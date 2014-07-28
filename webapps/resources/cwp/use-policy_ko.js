function fillData(){
var txt =[["Secure Internet Portal"],
["이용약관"],
["&lt; Return to Login"],
["Login - Use Policy"],
[""],/*#$UserPolicy-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}