function fillData(){
var txt =[["등록이 되었습니다"],
["잠시후 자동으로 <A href='transfer-url'>transfer-url</A> 로 이동 될것입니다."],
["자바 스크립을 사용항"],
];  

document.getElementById("regNote").innerHTML=txt[0];
document.getElementById("forwardNote").innerHTML=txt[1];
document.title=txt[2];
}