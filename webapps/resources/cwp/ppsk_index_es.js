function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["<strong>Los usuarios nuevos deberán de rellenar los campos de abajo para solicitar una Private PSK para acceder de manera segura a la red Wi-Fi.</strong>"],
["Nombre*"],
["Apellidos*"],
["Email*"],
["Teléfono"],
["Visita a*"],
["Comentario"],
["Tómese un momento para rellenar el formulario (*requerido)"],
["Loguearse"],
["Registro"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
fillPlaceHolder("field1",txt[2]);
fillPlaceHolder("field2",txt[3]);
fillPlaceHolder("field3",txt[4]);
fillPlaceHolder("opt_field1",txt[5]);
fillPlaceHolder("field4",txt[6]);
fillPlaceHolder("opt_field2",txt[7]);
document.getElementById("momentNote").innerHTML=txt[8];
document.title=txt[9];
document.getElementById("register_button_id").innerHTML=txt[10];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}