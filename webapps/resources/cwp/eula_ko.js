function fillData(){
var txt =[["Secure Internet Portal"],
["인가된 사용 정책"],
["로그인"],
[""],/*#$UserPolicy-Resource#$*/
["승인"],
["취소"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.title=txt[2];
$("#userPolicy").html(txt[3]);
document.getElementById("SubmitButton").value=txt[4];
document.getElementById("CancelButton").value=txt[5];
}

