function fillData(){
var txt =[["Portal seguro de acceso a internet"],
["Usuarios existentes"],
["<strong>Introduzca sus datos (Loguearse) para acceder de manera segura a internet:</strong>"],
["Loguearse indica que usted ha leído y acepta las  <a href='reg.php?ah_goal=use-policy.html'>Políticas de Uso</a>."],
["Usuario"],
["Contraseña"],
["Loguearse"],
["Loguearse"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.getElementById("loginNote").innerHTML=txt[2];
document.getElementById("usePolicyNote").innerHTML=txt[3];
fillPlaceHolder("field12",txt[4]);
fillPlaceHolder("field22",txt[5]);
document.title=txt[6];
document.getElementById("loginbutton").innerHTML=txt[7];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
	}