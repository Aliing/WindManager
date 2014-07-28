function fillData(){
var txt =[["注册成功"],
["在几秒后您将被自动重定向到<A href='transfer-url'>传输URL。</A> "],
["使用JavaScript重定向页面"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}