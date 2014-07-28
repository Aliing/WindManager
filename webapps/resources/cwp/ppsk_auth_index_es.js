function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["<strong>Si usted tiene credenciales validas, por favor introdúzcalas para recibir su Private PSK para acceder de manera segura a la red.</strong> "],
["Usuario"],
["Contraseña"],
["Loguearse"],
["Loguearse"],
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