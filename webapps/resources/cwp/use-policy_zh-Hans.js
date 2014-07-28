function fillData(){
var txt =[["安全的互联网门户"],
["使用条款"],
["&lt; 返回到登陆页面"],
["登录 - 使用条款"],
[""],/*#$UserPolicy-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}