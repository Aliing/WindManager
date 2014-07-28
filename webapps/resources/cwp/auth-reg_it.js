function fillData(){
var txt =[["Portale di accesso Internet sicuro"],
["Utentegiàesistente"],
["<strong>Accessosicuro ad Internet:</strong>"],
["L'accesso indica di aver letto e accettato il <a uso href='reg.php?ah_goal=use-policy.html'> privacy </ a>."],
["Nuovoutente"],
["<strong>Registrarsi per accedere alla rete.</strong>"],
["Nome Utente"],
["Password"],
["Il dipartimento IT potrebbeimpiegarequalcheminuto per completare la registrazione (* richiesto)"],
["Accesso"],
["Accesso"],
[""],/*#$FirstName-Resource#$*/
[""],/*#$LastName-Resource#$*/
[""],/*#$Email-Resource#$*/
[""],/*#$Phone-Resource#$*/
[""],/*#$Visiting-Resource#$*/
[""],/*#$Comment-Resource#$*/
[""],/*#$Representing-Resource#$*/
["Registrazione"],
["Nuovo utente? richiesta di accesso"]
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
document.getElementById("loginNote").innerHTML=txt[2];
document.getElementById("usePolicyNote").innerHTML=txt[3];
document.getElementById("newUserId").innerHTML=txt[4];
document.getElementById("regNote").innerHTML=txt[5];
fillPlaceHolder("field12",txt[6]);
fillPlaceHolder("field22",txt[7]);
document.getElementById("momentNote").innerHTML=txt[8];
document.title=txt[9];
document.getElementById("login_button_id").innerHTML=txt[10];
document.getElementById("register_button_id").innerHTML=txt[18];
document.getElementById("newUserRequastAccess").innerHTML=txt[19];
var label1=document.getElementById("fieldFirstNameMark");
var label2=document.getElementById("fieldLastNameMark");
var label3=document.getElementById("fieldEmailMark");
var label4=document.getElementById("opt_fieldPhoneMark");
var label5=document.getElementById("fieldVisitingMark");
var label5Idm=document.getElementById("opt_fieldVisitingMark");
var label6=document.getElementById("opt_fieldCommentMark");
var label7=document.getElementById("opt_fieldRepresentingMark");
if(label1!=null){
	fillPlaceHolder("fieldFirstNameMark",txt[11]);
}
if(label2!=null){
	fillPlaceHolder("fieldLastNameMark",txt[12]);
}
if(label3!=null){
	fillPlaceHolder("fieldEmailMark",txt[13]);
}
if(label4!=null){
	fillPlaceHolder("opt_fieldPhoneMark",txt[14]);
}
if(label5!=null){
	fillPlaceHolder("fieldVisitingMark",txt[15]);
}
if(label5Idm != null){
	fillPlaceHolder("opt_fieldVisitingMark",txt[15]);
}
if(label6!=null){
	fillPlaceHolder("opt_fieldCommentMark",txt[16]);
}
if(label7!=null){
	fillPlaceHolder("opt_fieldRepresentingMark",txt[17]);
}
}
/*#$CountryCode-Resource#$*/
function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
	}