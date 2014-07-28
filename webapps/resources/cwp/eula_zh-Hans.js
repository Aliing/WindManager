function fillData(){
var txt =[["安全的互联网门户"],
["可接受的使用条款"],
["登录"],
[""],/*#$UserPolicy-Resource#$*/
["接受"],
["取消"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}