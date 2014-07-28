function fillData(){
var txt =[["Secure Internet Portal"],
["<strong>If you have valid network credentials, please submit them below to receive your private preshared Key for secure network access.</strong> "],
["username"],
["password"],
["Login"],
["Log In"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("submitKeyNote").innerHTML=txt[1];
fillPlaceHolder("field12",txt[2]);
fillPlaceHolder("field22",txt[3]);
document.title=txt[4];
document.getElementById("loginbutton").innerHTML=txt[5];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}