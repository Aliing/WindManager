function fillData(){
var txt =[["Registration Successful!"],
["You will automatically be forwarded to <A href='transfer-url'>transfer-url</A> in pause-time seconds."],
["Redirect page with JavaScript"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}