function fillData(){
var txt =[["安全的網際網路入口"],
["使用條款"],
["&lt; 返回到登陸頁面"],
["登錄-使用條款"],
[""],/*#$UserPolicy-Resource#$*/
];  

$("#h1Title").html(txt[0]);
$("#h2Title").html(txt[1]);
$("#linkNote").html(txt[2]);
document.title=txt[3];
$("#userPolicy").html(txt[4]);
}