function fillData(){
var txt =[["註冊成功"],
["在幾秒後您將被自動重定向到<A href='transfer-url'>傳輸URL。</A> "],
["使用JavaScript重定向頁面"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}